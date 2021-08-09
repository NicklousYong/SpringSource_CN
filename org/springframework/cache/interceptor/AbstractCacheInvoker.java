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

package org.springframework.cache.interceptor;

import org.springframework.cache.Cache;
import org.springframework.util.Assert;

/**
 * A base component for invoking {@link Cache} operations and using a
 * configurable {@link CacheErrorHandler} when an exception occurs.
 *
 * <p>
 *  发生异常时调用{@link Cache}操作和使用可配置的{@link CacheErrorHandler}的基本组件
 * 
 * 
 * @author Stephane Nicoll
 * @since 4.1
 * @see org.springframework.cache.interceptor.CacheErrorHandler
 */
public abstract class AbstractCacheInvoker {

	private CacheErrorHandler errorHandler;


	protected AbstractCacheInvoker() {
		this(new SimpleCacheErrorHandler());
	}

	protected AbstractCacheInvoker(CacheErrorHandler errorHandler) {
		Assert.notNull("ErrorHandler must not be null");
		this.errorHandler = errorHandler;
	}


	/**
	 * Set the {@link CacheErrorHandler} instance to use to handle errors
	 * thrown by the cache provider. By default, a {@link SimpleCacheErrorHandler}
	 * is used who throws any exception as is.
	 * <p>
	 * 设置{@link CacheErrorHandler}实例以用于处理缓存提供程序抛出的错误默认情况下,使用{@link SimpleCacheErrorHandler}来抛出任何异常,
	 * 
	 */
	public void setErrorHandler(CacheErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	/**
	 * Return the {@link CacheErrorHandler} to use.
	 * <p>
	 *  返回{@link CacheErrorHandler}使用
	 * 
	 */
	public CacheErrorHandler getErrorHandler() {
		return this.errorHandler;
	}


	/**
	 * Execute {@link Cache#get(Object)} on the specified {@link Cache} and
	 * invoke the error handler if an exception occurs. Return {@code null}
	 * if the handler does not throw any exception, which simulates a cache
	 * miss in case of error.
	 * <p>
	 *  在指定的{@link Cache}上执行{@link Cache#get(Object)},如果发生异常,则调用错误处理程序Return {@code null}如果处理程序不抛出任何异常,模拟缓存未
	 * 命中,的错误。
	 * 
	 * 
	 * @see Cache#get(Object)
	 */
	protected Cache.ValueWrapper doGet(Cache cache, Object key) {
		try {
			return cache.get(key);
		}
		catch (RuntimeException ex) {
			getErrorHandler().handleCacheGetError(ex, cache, key);
			return null;  // If the exception is handled, return a cache miss
		}
	}

	/**
	 * Execute {@link Cache#put(Object, Object)} on the specified {@link Cache}
	 * and invoke the error handler if an exception occurs.
	 * <p>
	 *  在指定的{@link Cache}上执行{@link Cache#put(Object,Object)},并在发生异常时调用错误处理程序
	 * 
	 */
	protected void doPut(Cache cache, Object key, Object result) {
		try {
			cache.put(key, result);
		}
		catch (RuntimeException ex) {
			getErrorHandler().handleCachePutError(ex, cache, key, result);
		}
	}

	/**
	 * Execute {@link Cache#evict(Object)} on the specified {@link Cache} and
	 * invoke the error handler if an exception occurs.
	 * <p>
	 *  在指定的{@link Cache}上执行{@link Cache#evict(Object)},并在发生异常时调用错误处理程序
	 * 
	 */
	protected void doEvict(Cache cache, Object key) {
		try {
			cache.evict(key);
		}
		catch (RuntimeException ex) {
			getErrorHandler().handleCacheEvictError(ex, cache, key);
		}
	}

	/**
	 * Execute {@link Cache#clear()} on the specified {@link Cache} and
	 * invoke the error handler if an exception occurs.
	 * <p>
	 * 在指定的{@link Cache}上执行{@link Cache#clear()},如果发生异常,则调用错误处理程序
	 */
	protected void doClear(Cache cache) {
		try {
			cache.clear();
		}
		catch (RuntimeException ex) {
			getErrorHandler().handleCacheClearError(ex, cache);
		}
	}

}
