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

package org.springframework.web.method.annotation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.core.ExceptionDepthComparator;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils.MethodFilter;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Discovers {@linkplain ExceptionHandler @ExceptionHandler} methods in a given class,
 * including all of its superclasses, and helps to resolve a given {@link Exception}
 * to the exception types supported by a given {@link Method}.
 *
 * <p>
 *  发现给定类中的{@linkplain ExceptionHandler @ExceptionHandler}方法,包括其所有超类,并有助于将给定的{@link异常}解析为给定的{@link方法}支持的
 * 异常类型。
 * 
 * 
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @since 3.1
 */
public class ExceptionHandlerMethodResolver {

	/**
	 * A filter for selecting {@code @ExceptionHandler} methods.
	 * <p>
	 * 一个用于选择{@code @ExceptionHandler}方法的过滤器
	 * 
	 */
	public static final MethodFilter EXCEPTION_HANDLER_METHODS = new MethodFilter() {
		@Override
		public boolean matches(Method method) {
			return (AnnotationUtils.findAnnotation(method, ExceptionHandler.class) != null);
		}
	};

	/**
	 * Arbitrary {@link Method} reference, indicating no method found in the cache.
	 * <p>
	 *  任意{@link方法}引用,表示缓存中没有找到方法
	 * 
	 */
	private static final Method NO_METHOD_FOUND = ClassUtils.getMethodIfAvailable(System.class, "currentTimeMillis");


	private final Map<Class<? extends Throwable>, Method> mappedMethods =
			new ConcurrentHashMap<Class<? extends Throwable>, Method>(16);

	private final Map<Class<? extends Throwable>, Method> exceptionLookupCache =
			new ConcurrentHashMap<Class<? extends Throwable>, Method>(16);


	/**
	 * A constructor that finds {@link ExceptionHandler} methods in the given type.
	 * <p>
	 *  在给定类型中找到{@link ExceptionHandler}方法的构造函数
	 * 
	 * 
	 * @param handlerType the type to introspect
	 */
	public ExceptionHandlerMethodResolver(Class<?> handlerType) {
		for (Method method : MethodIntrospector.selectMethods(handlerType, EXCEPTION_HANDLER_METHODS)) {
			for (Class<? extends Throwable> exceptionType : detectExceptionMappings(method)) {
				addExceptionMapping(exceptionType, method);
			}
		}
	}


	/**
	 * Extract exception mappings from the {@code @ExceptionHandler} annotation first,
	 * and then as a fallback from the method signature itself.
	 * <p>
	 *  首先从{@code @ExceptionHandler}注解中提取异常映射,然后作为方法签名本身的回退
	 * 
	 */
	@SuppressWarnings("unchecked")
	private List<Class<? extends Throwable>> detectExceptionMappings(Method method) {
		List<Class<? extends Throwable>> result = new ArrayList<Class<? extends Throwable>>();
		detectAnnotationExceptionMappings(method, result);
		if (result.isEmpty()) {
			for (Class<?> paramType : method.getParameterTypes()) {
				if (Throwable.class.isAssignableFrom(paramType)) {
					result.add((Class<? extends Throwable>) paramType);
				}
			}
		}
		Assert.notEmpty(result, "No exception types mapped to {" + method + "}");
		return result;
	}

	protected void detectAnnotationExceptionMappings(Method method, List<Class<? extends Throwable>> result) {
		ExceptionHandler ann = AnnotationUtils.findAnnotation(method, ExceptionHandler.class);
		result.addAll(Arrays.asList(ann.value()));
	}

	private void addExceptionMapping(Class<? extends Throwable> exceptionType, Method method) {
		Method oldMethod = this.mappedMethods.put(exceptionType, method);
		if (oldMethod != null && !oldMethod.equals(method)) {
			throw new IllegalStateException("Ambiguous @ExceptionHandler method mapped for [" +
					exceptionType + "]: {" + oldMethod + ", " + method + "}");
		}
	}

	/**
	 * Whether the contained type has any exception mappings.
	 * <p>
	 *  包含的类型是否有任何异常映射
	 * 
	 */
	public boolean hasExceptionMappings() {
		return !this.mappedMethods.isEmpty();
	}

	/**
	 * Find a {@link Method} to handle the given exception.
	 * Use {@link ExceptionDepthComparator} if more than one match is found.
	 * <p>
	 *  找到{@link方法}来处理给定的异常使用{@link ExceptionDepthComparator}如果找到多个匹配
	 * 
	 * 
	 * @param exception the exception
	 * @return a Method to handle the exception, or {@code null} if none found
	 */
	public Method resolveMethod(Exception exception) {
		Method method = resolveMethodByExceptionType(exception.getClass());
		if (method == null) {
			Throwable cause = exception.getCause();
			if (cause != null) {
				method = resolveMethodByExceptionType(cause.getClass());
			}
		}
		return method;
	}

	/**
	 * Find a {@link Method} to handle the given exception type. This can be
	 * useful if an {@link Exception} instance is not available (e.g. for tools).
	 * <p>
	 *  找到{@link方法}来处理给定的异常类型如果{@link Exception}实例不可用(例如对于工具),这可能很有用
	 * 
	 * 
	 * @param exceptionType the exception type
	 * @return a Method to handle the exception, or {@code null} if none found
	 */
	public Method resolveMethodByExceptionType(Class<? extends Throwable> exceptionType) {
		Method method = this.exceptionLookupCache.get(exceptionType);
		if (method == null) {
			method = getMappedMethod(exceptionType);
			this.exceptionLookupCache.put(exceptionType, (method != null ? method : NO_METHOD_FOUND));
		}
		return (method != NO_METHOD_FOUND ? method : null);
	}

	/**
	 * Return the {@link Method} mapped to the given exception type, or {@code null} if none.
	 * <p>
	 *  将{@link Method}映射到给定的异常类型,否则返回{@code null}
	 */
	private Method getMappedMethod(Class<? extends Throwable> exceptionType) {
		List<Class<? extends Throwable>> matches = new ArrayList<Class<? extends Throwable>>();
		for (Class<? extends Throwable> mappedException : this.mappedMethods.keySet()) {
			if (mappedException.isAssignableFrom(exceptionType)) {
				matches.add(mappedException);
			}
		}
		if (!matches.isEmpty()) {
			Collections.sort(matches, new ExceptionDepthComparator(exceptionType));
			return this.mappedMethods.get(matches.get(0));
		}
		else {
			return null;
		}
	}

}
