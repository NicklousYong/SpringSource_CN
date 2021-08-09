/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2013 the original author or authors.
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

package org.springframework.messaging.handler.invocation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.core.ExceptionDepthComparator;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * Cache exception handling method mappings and provide options to look up a method
 * that should handle an exception. If multiple methods match, they are sorted using
 * {@link ExceptionDepthComparator} and the top match is returned.
 *
 * <p>
 * 缓存异常处理方法映射并提供查找应该处理异常的方法的选项如果多个方法匹配,则使用{@link ExceptionDepthComparator}对其进行排序,并返回顶部的匹配
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public abstract class AbstractExceptionHandlerMethodResolver {

	private static final Method NO_METHOD_FOUND = ClassUtils.getMethodIfAvailable(System.class, "currentTimeMillis");

	private final Map<Class<? extends Throwable>, Method> mappedMethods = new ConcurrentHashMap<Class<? extends Throwable>, Method>(16);

	private final Map<Class<? extends Throwable>, Method> exceptionLookupCache = new ConcurrentHashMap<Class<? extends Throwable>, Method>(16);


	/**
	 * Protected constructor accepting exception-to-method mappings.
	 * <p>
	 *  受保护的构造函数接受异常到方法映射
	 * 
	 */
	protected AbstractExceptionHandlerMethodResolver(Map<Class<? extends Throwable>, Method> mappedMethods) {
		Assert.notNull(mappedMethods, "Mapped Methods must not be null");
		this.mappedMethods.putAll(mappedMethods);
	}

	/**
	 * Extract the exceptions this method handles.This implementation looks for
	 * sub-classes of Throwable in the method signature.
	 * The method is static to ensure safe use from sub-class constructors.
	 * <p>
	 *  提取此方法处理的异常此实现在方法签名中查找Throwable的子类该方法是静态的,以确保子类构造函数的安全使用
	 * 
	 */
	@SuppressWarnings("unchecked")
	protected static List<Class<? extends Throwable>> getExceptionsFromMethodSignature(Method method) {
		List<Class<? extends Throwable>> result = new ArrayList<Class<? extends Throwable>>();
		for (Class<?> paramType : method.getParameterTypes()) {
			if (Throwable.class.isAssignableFrom(paramType)) {
				result.add((Class<? extends Throwable>) paramType);
			}
		}
		Assert.notEmpty(result, "No exception types mapped to {" + method + "}");
		return result;
	}

	/**
	 * Whether the contained type has any exception mappings.
	 * <p>
	 *  包含的类型是否有任何异常映射
	 * 
	 */
	public boolean hasExceptionMappings() {
		return (this.mappedMethods.size() > 0);
	}

	/**
	 * Find a method to handle the given exception.
	 * Use {@link org.springframework.core.ExceptionDepthComparator} if more than one match is found.
	 * <p>
	 *  找到一个处理给定异常的方法如果找到多个匹配,使用{@link orgspringframeworkcoreExceptionDepthComparator}
	 * 
	 * 
	 * @param exception the exception
	 * @return a method to handle the exception or {@code null}
	 */
	public Method resolveMethod(Exception exception) {
		Class<? extends Exception> exceptionType = exception.getClass();
		Method method = this.exceptionLookupCache.get(exceptionType);
		if (method == null) {
			method = getMappedMethod(exceptionType);
			this.exceptionLookupCache.put(exceptionType, method != null ? method : NO_METHOD_FOUND);
		}
		return method != NO_METHOD_FOUND ? method : null;
	}

	/**
	 * Return the method mapped to the given exception type or {@code null}.
	 * <p>
	 *  返回映射到给定异常类型的方法或{@code null}
	 */
	private Method getMappedMethod(Class<? extends Exception> exceptionType) {
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
