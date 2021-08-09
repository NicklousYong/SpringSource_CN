/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.core;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/**
 * Defines the algorithm for searching for metadata-associated methods exhaustively
 * including interfaces and parent classes while also dealing with parameterized methods
 * as well as common scenarios encountered with interface and class-based proxies.
 *
 * <p>Typically, but not necessarily, used for finding annotated handler methods.
 *
 * <p>
 * 定义用于搜索元数据相关方法的算法,包括接口和父类,同时处理参数化方法以及接口和基于类的代理遇到的常见情况
 * 
 *  通常,但不一定用于查找注释处理程序方法
 * 
 * 
 * @author Juergen Hoeller
 * @author Rossen Stoyanchev
 * @since 4.2.3
 */
public abstract class MethodIntrospector {

	/**
	 * Select methods on the given target type based on the lookup of associated metadata.
	 * <p>Callers define methods of interest through the {@link MetadataLookup} parameter,
	 * allowing to collect the associated metadata into the result map.
	 * <p>
	 *  基于相关元数据的查找,选择给定目标类型的方法<p>调用者通过{@link MetadataLookup}参数定义感兴趣的方法,允许将相关元数据收集到结果图中
	 * 
	 * 
	 * @param targetType the target type to search methods on
	 * @param metadataLookup a {@link MetadataLookup} callback to inspect methods of interest,
	 * returning non-null metadata to be associated with a given method if there is a match,
	 * or {@code null} for no match
	 * @return the selected methods associated with their metadata (in the order of retrieval),
	 * or an empty map in case of no match
	 */
	public static <T> Map<Method, T> selectMethods(Class<?> targetType, final MetadataLookup<T> metadataLookup) {
		final Map<Method, T> methodMap = new LinkedHashMap<Method, T>();
		Set<Class<?>> handlerTypes = new LinkedHashSet<Class<?>>();
		Class<?> specificHandlerType = null;

		if (!Proxy.isProxyClass(targetType)) {
			handlerTypes.add(targetType);
			specificHandlerType = targetType;
		}
		handlerTypes.addAll(Arrays.asList(targetType.getInterfaces()));

		for (Class<?> currentHandlerType : handlerTypes) {
			final Class<?> targetClass = (specificHandlerType != null ? specificHandlerType : currentHandlerType);

			ReflectionUtils.doWithMethods(currentHandlerType, new ReflectionUtils.MethodCallback() {
				@Override
				public void doWith(Method method) {
					Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
					T result = metadataLookup.inspect(specificMethod);
					if (result != null) {
						Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);
						if (bridgedMethod == specificMethod || metadataLookup.inspect(bridgedMethod) == null) {
							methodMap.put(specificMethod, result);
						}
					}
				}
			}, ReflectionUtils.USER_DECLARED_METHODS);
		}

		return methodMap;
	}

	/**
	 * Select methods on the given target type based on a filter.
	 * <p>Callers define methods of interest through the {@code MethodFilter} parameter.
	 * <p>
	 *  基于过滤器选择给定目标类型的方法<p>调用者通过{@code MethodFilter}参数定义感兴趣的方法
	 * 
	 * 
	 * @param targetType the target type to search methods on
	 * @param methodFilter a {@code MethodFilter} to help
	 * recognize handler methods of interest
	 * @return the selected methods, or an empty set in case of no match
	 */
	public static Set<Method> selectMethods(Class<?> targetType, final ReflectionUtils.MethodFilter methodFilter) {
		return selectMethods(targetType, new MetadataLookup<Boolean>() {
			@Override
			public Boolean inspect(Method method) {
				return (methodFilter.matches(method) ? Boolean.TRUE : null);
			}
		}).keySet();
	}

	/**
	 * Select an invocable method on the target type: either the given method itself
	 * if actually exposed on the target type, or otherwise a corresponding method
	 * on one of the target type's interfaces or on the target type itself.
	 * <p>Matches on user-declared interfaces will be preferred since they are likely
	 * to contain relevant metadata that corresponds to the method on the target class.
	 * <p>
	 * 在目标类型上选择一个可调用的方法：给定的方法本身,如果实际暴露在目标类型上,或者其他对象类型的接口之一或目标类型本身上的对应方法<p>与用户声明的接口匹配将匹配因为它们可能包含与目标类上的方法相对应的相
	 * 关元数据。
	 * 
	 * 
	 * @param method the method to check
	 * @param targetType the target type to search methods on
	 * (typically an interface-based JDK proxy)
	 * @return a corresponding invocable method on the target type
	 * @throws IllegalStateException if the given method is not invocable on the given
	 * target type (typically due to a proxy mismatch)
	 */
	public static Method selectInvocableMethod(Method method, Class<?> targetType) {
		if (method.getDeclaringClass().isAssignableFrom(targetType)) {
			return method;
		}
		try {
			String methodName = method.getName();
			Class<?>[] parameterTypes = method.getParameterTypes();
			for (Class<?> ifc : targetType.getInterfaces()) {
				try {
					return ifc.getMethod(methodName, parameterTypes);
				}
				catch (NoSuchMethodException ex) {
					// Alright, not on this interface then...
				}
			}
			// A final desperate attempt on the proxy class itself...
			return targetType.getMethod(methodName, parameterTypes);
		}
		catch (NoSuchMethodException ex) {
			throw new IllegalStateException(String.format(
					"Need to invoke method '%s' declared on target class '%s', " +
					"but not found in any interface(s) of the exposed proxy type. " +
					"Either pull the method up to an interface or switch to CGLIB " +
					"proxies by enforcing proxy-target-class mode in your configuration.",
					method.getName(), method.getDeclaringClass().getSimpleName()));
		}
	}


	/**
	 * A callback interface for metadata lookup on a given method.
	 * <p>
	 *  用于元数据查询的回调接口
	 * 
	 * 
	 * @param <T> the type of metadata returned
	 */
	public interface MetadataLookup<T> {

		/**
		 * Perform a lookup on the given method and return associated metadata, if any.
		 * <p>
		 *  执行给定方法的查找并返回相关的元数据(如果有)
		 * 
		 * @param method the method to inspect
		 * @return non-null metadata to be associated with a method if there is a match,
		 * or {@code null} for no match
		 */
		T inspect(Method method);
	}

}
