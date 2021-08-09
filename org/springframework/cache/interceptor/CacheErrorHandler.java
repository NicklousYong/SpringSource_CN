/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2014 the original author or authors.
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

/**
 * A strategy for handling cache-related errors. In most cases, any
 * exception thrown by the provider should simply be thrown back at
 * the client but, in some circumstances, the infrastructure may need
 * to handle cache-provider exceptions in a different way.
 *
 * <p>Typically, failing to retrieve an object from the cache with
 * a given id can be transparently managed as a cache miss by not
 * throwing back such exception.
 *
 * <p>
 * 处理缓存相关错误的策略在大多数情况下,提供程序抛出的任何异常都应该简单地在客户机上抛出,但在某些情况下,基础架构可能需要以不同的方式处理缓存提供者异常
 * 
 *  通常,无法从具有给定ID的缓存中检索对象可以通过不抛出此异常作为缓存未命中而被透明地管理
 * 
 * 
 * @author Stephane Nicoll
 * @since 4.1
 */
public interface CacheErrorHandler {

	/**
	 * Handle the given runtime exception thrown by the cache provider when
	 * retrieving an item with the specified {@code key}, possibly
	 * rethrowing it as a fatal exception.
	 * <p>
	 *  处理由缓存提供者在使用指定的{@code key}检索项目时抛出的给定的运行时异常,可能会将其重新抛售为致命异常
	 * 
	 * 
	 * @param exception the exception thrown by the cache provider
	 * @param cache the cache
	 * @param key the key used to get the item
	 * @see Cache#get(Object)
	 */
	void handleCacheGetError(RuntimeException exception, Cache cache, Object key);

	/**
	 * Handle the given runtime exception thrown by the cache provider when
	 * updating an item with the specified {@code key} and {@code value},
	 * possibly rethrowing it as a fatal exception.
	 * <p>
	 *  当使用指定的{@code键}和{@code值}更新项目时,处理由缓存提供程序抛出的给定的运行时异常,可能会将其重新导致为致命异常
	 * 
	 * 
	 * @param exception the exception thrown by the cache provider
	 * @param cache the cache
	 * @param key the key used to update the item
	 * @param value the value to associate with the key
	 * @see Cache#put(Object, Object)
	 */
	void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value);

	/**
	 * Handle the given runtime exception thrown by the cache provider when
	 * clearing an item with the specified {@code key}, possibly rethrowing
	 * it as a fatal exception.
	 * <p>
	 * 处理由缓存提供者抛出的给定的运行时异常,使用指定的{@code键}清除一个项目时,可能会将其重新抛售为致命异常
	 * 
	 * 
	 * @param exception the exception thrown by the cache provider
	 * @param cache the cache
	 * @param key the key used to clear the item
	 */
	void handleCacheEvictError(RuntimeException exception, Cache cache, Object key);

	/**
	 * Handle the given runtime exception thrown by the cache provider when
	 * clearing the specified {@link Cache}, possibly rethrowing it as a
	 * fatal exception.
	 * <p>
	 *  在清除指定的{@link Cache}时,处理缓存提供者抛出的给定的运行时异常,可能会将其重新抛售为致命异常
	 * 
	 * @param exception the exception thrown by the cache provider
	 * @param cache the cache to clear
	 */
	void handleCacheClearError(RuntimeException exception, Cache cache);

}
