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

package org.springframework.cache.jcache;

import java.util.Collection;
import java.util.LinkedHashSet;
import javax.cache.CacheManager;
import javax.cache.Caching;

import org.springframework.cache.Cache;
import org.springframework.cache.transaction.AbstractTransactionSupportingCacheManager;

/**
 * {@link org.springframework.cache.CacheManager} implementation
 * backed by a JCache {@link javax.cache.CacheManager}.
 *
 * <p>Note: This class has been updated for JCache 1.0, as of Spring 4.0.
 *
 * <p>
 *  由JCache {@link javaxcacheCacheManager}支持的{@link orgspringframeworkcacheCacheManager}实现
 * 
 *  注意：从Spring 40开始,此类已更新为JCache 10
 * 
 * 
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 * @since 3.2
 */
public class JCacheCacheManager extends AbstractTransactionSupportingCacheManager {

	private javax.cache.CacheManager cacheManager;

	private boolean allowNullValues = true;


	/**
	 * Create a new JCacheCacheManager, setting the target JCache CacheManager
	 * through the {@link #setCacheManager} bean property.
	 * <p>
	 * 创建一个新的JCacheCacheManager,通过{@link #setCacheManager} bean属性设置目标JCache CacheManager
	 * 
	 */
	public JCacheCacheManager() {
	}

	/**
	 * Create a new JCacheCacheManager for the given backing JCache.
	 * <p>
	 *  为给定的支持JCache创建一个新的JCacheCacheManager
	 * 
	 * 
	 * @param cacheManager the backing JCache {@link javax.cache.CacheManager}
	 */
	public JCacheCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}


	/**
	 * Set the backing JCache {@link javax.cache.CacheManager}.
	 * <p>
	 *  设置支持JCache {@link javaxcacheCacheManager}
	 * 
	 */
	public void setCacheManager(javax.cache.CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	/**
	 * Return the backing JCache {@link javax.cache.CacheManager}.
	 * <p>
	 *  返回支持JCache {@link javaxcacheCacheManager}
	 * 
	 */
	public javax.cache.CacheManager getCacheManager() {
		return this.cacheManager;
	}

	/**
	 * Specify whether to accept and convert {@code null} values for all caches
	 * in this cache manager.
	 * <p>Default is "true", despite JSR-107 itself not supporting {@code null} values.
	 * An internal holder object will be used to store user-level {@code null}s.
	 * <p>
	 *  指定是否接受并转换此缓存管理器中所有缓存的{@code null}值<p>默认值为"true",尽管JSR-107本身不支持{@code null}值内部持有者对象将用于存储用户级{@code null}
	 *  s。
	 * 
	 */
	public void setAllowNullValues(boolean allowNullValues) {
		this.allowNullValues = allowNullValues;
	}

	/**
	 * Return whether this cache manager accepts and converts {@code null} values
	 * for all of its caches.
	 * <p>
	 */
	public boolean isAllowNullValues() {
		return this.allowNullValues;
	}

	@Override
	public void afterPropertiesSet() {
		if (getCacheManager() == null) {
			setCacheManager(Caching.getCachingProvider().getCacheManager());
		}
		super.afterPropertiesSet();
	}


	@Override
	protected Collection<Cache> loadCaches() {
		Collection<Cache> caches = new LinkedHashSet<Cache>();
		for (String cacheName : getCacheManager().getCacheNames()) {
			javax.cache.Cache<Object, Object> jcache = getCacheManager().getCache(cacheName);
			caches.add(new JCacheCache(jcache, isAllowNullValues()));
		}
		return caches;
	}

	@Override
	protected Cache getMissingCache(String name) {
		// Check the JCache cache again (in case the cache was added at runtime)
		javax.cache.Cache<Object, Object> jcache = getCacheManager().getCache(name);
		if (jcache != null) {
			return new JCacheCache(jcache, isAllowNullValues());
		}
		return null;
	}

}
