/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2015 the original author or authors.
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

package org.springframework.cache.jcache.interceptor;

import javax.cache.annotation.CacheResult;

import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheOperationInvoker;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.util.ExceptionTypeFilter;
import org.springframework.util.SerializationUtils;

/**
 * Intercept methods annotated with {@link CacheResult}.
 *
 * <p>
 *  截取方法用{@link CacheResult}注释
 * 
 * 
 * @author Stephane Nicoll
 * @since 4.1
 */
@SuppressWarnings("serial")
class CacheResultInterceptor extends AbstractKeyCacheInterceptor<CacheResultOperation, CacheResult> {

	public CacheResultInterceptor(CacheErrorHandler errorHandler) {
		super(errorHandler);
	}

	@Override
	protected Object invoke(CacheOperationInvocationContext<CacheResultOperation> context,
			CacheOperationInvoker invoker) {

		CacheResultOperation operation = context.getOperation();
		Object cacheKey = generateKey(context);

		Cache cache = resolveCache(context);
		Cache exceptionCache = resolveExceptionCache(context);

		if (!operation.isAlwaysInvoked()) {
			Cache.ValueWrapper cachedValue = doGet(cache, cacheKey);
			if (cachedValue != null) {
				return cachedValue.get();
			}
			checkForCachedException(exceptionCache, cacheKey);
		}

		try {
			Object invocationResult = invoker.invoke();
			cache.put(cacheKey, invocationResult);
			return invocationResult;
		}
		catch (CacheOperationInvoker.ThrowableWrapper ex) {
			Throwable original = ex.getOriginal();
			cacheException(exceptionCache, operation.getExceptionTypeFilter(), cacheKey, original);
			throw ex;
		}
	}

	/**
	 * Check for a cached exception. If the exception is found, throw it directly.
	 * <p>
	 *  检查缓存异常如果发现异常,直接抛出异常
	 * 
	 */
	protected void checkForCachedException(Cache exceptionCache, Object cacheKey) {
		if (exceptionCache == null) {
			return;
		}
		Cache.ValueWrapper result = doGet(exceptionCache, cacheKey);
		if (result != null) {
			throw rewriteCallStack((Throwable) result.get(), getClass().getName(), "invoke");
		}
	}


	protected void cacheException(Cache exceptionCache, ExceptionTypeFilter filter, Object cacheKey, Throwable ex) {
		if (exceptionCache == null) {
			return;
		}
		if (filter.match(ex.getClass())) {
			exceptionCache.put(cacheKey, ex);
		}
	}


	private Cache resolveExceptionCache(CacheOperationInvocationContext<CacheResultOperation> context) {
		CacheResolver exceptionCacheResolver = context.getOperation().getExceptionCacheResolver();
		if (exceptionCacheResolver != null) {
			return extractFrom(context.getOperation().getExceptionCacheResolver().resolveCaches(context));
		}
		return null;
	}

	/**
	 * Rewrite the call stack of the specified {@code exception} so that it matches
	 * the current call stack up-to (included) the specified method invocation.
	 * <p>Clone the specified exception. If the exception is not {@code serializable},
	 * the original exception is returned. If no common ancestor can be found, returns
	 * the original exception.
	 * <p>Used to make sure that a cached exception has a valid invocation context.
	 * <p>
	 * 重写指定的{@code异常}的调用堆栈,使其与当前调用堆栈匹配(包含)指定的方法调用<p>克隆指定的异常如果异常不是{@code serializable},原始异常返回如果没有找到共同的祖先,则返回原
	 * 始异常<p>用于确保缓存异常具有有效的调用上下文。
	 * 
	 * @param exception the exception to merge with the current call stack
	 * @param className the class name of the common ancestor
	 * @param methodName the method name of the common ancestor
	 * @return a clone exception with a rewritten call stack composed of the current
	 * call stack up to (included) the common ancestor specified by the {@code className} and
	 * {@code methodName} arguments, followed by stack trace elements of the specified
	 * {@code exception} after the common ancestor.
	 */
	private static CacheOperationInvoker.ThrowableWrapper rewriteCallStack(
			Throwable exception, String className, String methodName) {

		Throwable clone = cloneException(exception);
		if (clone == null) {
			return new CacheOperationInvoker.ThrowableWrapper(exception);
		}

		StackTraceElement[] callStack = new Exception().getStackTrace();
		StackTraceElement[] cachedCallStack = exception.getStackTrace();

		int index = findCommonAncestorIndex(callStack, className, methodName);
		int cachedIndex = findCommonAncestorIndex(cachedCallStack, className, methodName);
		if (index == -1 || cachedIndex == -1) {
			return new CacheOperationInvoker.ThrowableWrapper(exception); // Cannot find common ancestor
		}
		StackTraceElement[] result = new StackTraceElement[cachedIndex + callStack.length - index];
		System.arraycopy(cachedCallStack, 0, result, 0, cachedIndex);
		System.arraycopy(callStack, index, result, cachedIndex, callStack.length - index);

		clone.setStackTrace(result);
		return new CacheOperationInvoker.ThrowableWrapper(clone);
	}

	@SuppressWarnings("unchecked")
	private static <T extends Throwable> T cloneException(T exception) {
		try {
			return (T) SerializationUtils.deserialize(SerializationUtils.serialize(exception));
		}
		catch (Exception ex) {
			return null;  // exception parameter cannot be cloned
		}
	}

	private static int findCommonAncestorIndex(StackTraceElement[] callStack, String className, String methodName) {
		for (int i = 0; i < callStack.length; i++) {
			StackTraceElement element = callStack[i];
			if (className.equals(element.getClassName()) && methodName.equals(element.getMethodName())) {
				return i;
			}
		}
		return -1;
	}

}
