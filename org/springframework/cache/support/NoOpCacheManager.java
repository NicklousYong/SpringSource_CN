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

package org.springframework.cache.support;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

/**
 * A basic, no operation {@link CacheManager} implementation suitable
 * for disabling caching, typically used for backing cache declarations
 * without an actual backing store.
 *
 * <p>Will simply accept any items into the cache not actually storing them.
 *
 * <p>
 *  一种基本的,无操作的{@link CacheManager}实现,适用于禁用缓存,通常用于在没有实际后台存储的情况下提供缓存声明
 * 
 * <p>只要在缓存中接受任何项目,而不是实际存储它们
 * 
 * 
 * @author Costin Leau
 * @author Stephane Nicoll
 * @since 3.1
 * @see CompositeCacheManager
 */
public class NoOpCacheManager implements CacheManager {

	private final ConcurrentMap<String, Cache> caches = new ConcurrentHashMap<String, Cache>(16);

	private final Set<String> cacheNames = new LinkedHashSet<String>(16);


	/**
	 * This implementation always returns a {@link Cache} implementation that will not store items.
	 * Additionally, the request cache will be remembered by the manager for consistency.
	 * <p>
	 *  这个实现总是返回一个不存储项目的{@link Cache}实现。另外,管理器会记住请求缓存的一致性
	 * 
	 */
	@Override
	public Cache getCache(String name) {
		Cache cache = this.caches.get(name);
		if (cache == null) {
			this.caches.putIfAbsent(name, new NoOpCache(name));
			synchronized (this.cacheNames) {
				this.cacheNames.add(name);
			}
		}

		return this.caches.get(name);
	}

	/**
	 * This implementation returns the name of the caches previously requested.
	 * <p>
	 *  此实现返回先前请求的高速缓存的名称
	 */
	@Override
	public Collection<String> getCacheNames() {
		synchronized (this.cacheNames) {
			return Collections.unmodifiableSet(this.cacheNames);
		}
	}


	private static class NoOpCache implements Cache {

		private final String name;

		public NoOpCache(String name) {
			this.name = name;
		}

		@Override
		public void clear() {
		}

		@Override
		public void evict(Object key) {
		}

		@Override
		public ValueWrapper get(Object key) {
			return null;
		}

		@Override
		public <T> T get(Object key, Class<T> type) {
			return null;
		}

		@Override
		public <T> T get(Object key, Callable<T> valueLoader) {
			try {
				return valueLoader.call();
			}
			catch (Exception ex) {
				throw new ValueRetrievalException(key, valueLoader, ex);
			}
		}

		@Override
		public String getName() {
			return this.name;
		}

		@Override
		public Object getNativeCache() {
			return null;
		}

		@Override
		public void put(Object key, Object value) {
		}

		@Override
		public ValueWrapper putIfAbsent(Object key, Object value) {
			return null;
		}
	}

}
