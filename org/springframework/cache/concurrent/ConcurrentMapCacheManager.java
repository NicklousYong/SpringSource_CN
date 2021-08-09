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

package org.springframework.cache.concurrent;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.serializer.support.SerializationDelegate;

/**
 * {@link CacheManager} implementation that lazily builds {@link ConcurrentMapCache}
 * instances for each {@link #getCache} request. Also supports a 'static' mode where
 * the set of cache names is pre-defined through {@link #setCacheNames}, with no
 * dynamic creation of further cache regions at runtime.
 *
 * <p>Note: This is by no means a sophisticated CacheManager; it comes with no
 * cache configuration options. However, it may be useful for testing or simple
 * caching scenarios. For advanced local caching needs, consider
 * {@link org.springframework.cache.jcache.JCacheCacheManager},
 * {@link org.springframework.cache.ehcache.EhCacheCacheManager},
 * {@link org.springframework.cache.caffeine.CaffeineCacheManager} or
 * {@link org.springframework.cache.guava.GuavaCacheManager}.
 *
 * <p>
 * {@link CacheManager}实现,为每个{@link #getCache}请求懒惰构建{@link ConcurrentMapCache}实例还支持"静态"模式,其中通过{@link #setCacheNames}
 * 预定义了一组缓存名称,其中在运行时没有动态创建进一步的缓存区域。
 * 
 *  注意：这绝对不是一个复杂的CacheManager;它没有缓存配置选项但是,它可能对于测试或简单的缓存方案很有用。
 * 对于高级本地缓存需求,请考虑{@link orgspringframeworkcachejcacheJCacheCacheManager},{@link orgspringframeworkcacheehcacheEhCacheCacheManager}
 * ,{@link orgspringframeworkcachecaffeineCaffeineCacheManager}或{@link orgspringframeworkcacheguavaGuavaCacheManager}
 * 。
 *  注意：这绝对不是一个复杂的CacheManager;它没有缓存配置选项但是,它可能对于测试或简单的缓存方案很有用。
 * 
 * 
 * @author Juergen Hoeller
 * @since 3.1
 * @see ConcurrentMapCache
 */
public class ConcurrentMapCacheManager implements CacheManager, BeanClassLoaderAware {

	private final ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<String, Cache>(16);

	private boolean dynamic = true;

	private boolean allowNullValues = true;

	private boolean storeByValue = false;

	private SerializationDelegate serialization;


	/**
	 * Construct a dynamic ConcurrentMapCacheManager,
	 * lazily creating cache instances as they are being requested.
	 * <p>
	 * 构造动态ConcurrentMapCacheManager,在请求时缓慢创建缓存实例
	 * 
	 */
	public ConcurrentMapCacheManager() {
	}

	/**
	 * Construct a static ConcurrentMapCacheManager,
	 * managing caches for the specified cache names only.
	 * <p>
	 *  构造静态ConcurrentMapCacheManager,仅管理指定的缓存名称的缓存
	 * 
	 */
	public ConcurrentMapCacheManager(String... cacheNames) {
		setCacheNames(Arrays.asList(cacheNames));
	}


	/**
	 * Specify the set of cache names for this CacheManager's 'static' mode.
	 * <p>The number of caches and their names will be fixed after a call to this method,
	 * with no creation of further cache regions at runtime.
	 * <p>Calling this with a {@code null} collection argument resets the
	 * mode to 'dynamic', allowing for further creation of caches again.
	 * <p>
	 *  指定CacheManager的"静态"模式的缓存名称集<p>在调用此方法之后,缓存及其名称的数量将被修复,而在运行时没有创建进一步的缓存区域<p>使用{ @code null} collection参
	 * 数将模式重置为"动态",允许再次进一步创建缓存。
	 * 
	 */
	public void setCacheNames(Collection<String> cacheNames) {
		if (cacheNames != null) {
			for (String name : cacheNames) {
				this.cacheMap.put(name, createConcurrentMapCache(name));
			}
			this.dynamic = false;
		}
		else {
			this.dynamic = true;
		}
	}

	/**
	 * Specify whether to accept and convert {@code null} values for all caches
	 * in this cache manager.
	 * <p>Default is "true", despite ConcurrentHashMap itself not supporting {@code null}
	 * values. An internal holder object will be used to store user-level {@code null}s.
	 * <p>Note: A change of the null-value setting will reset all existing caches,
	 * if any, to reconfigure them with the new null-value requirement.
	 * <p>
	 * 指定是否接受并转换此缓存管理器中所有缓存的{@code null}值<p>默认值为"true",尽管ConcurrentHashMap本身不支持{@code null}值内部持有者对象将用于存储用户 -
	 *  级别{@code null} s <p>注意：更改空值设置将重置所有现有缓存(如果有),以使用新的空值要求重新配置它们。
	 * 
	 */
	public void setAllowNullValues(boolean allowNullValues) {
		if (allowNullValues != this.allowNullValues) {
			this.allowNullValues = allowNullValues;
			// Need to recreate all Cache instances with the new null-value configuration...
			recreateCaches();
		}
	}

	/**
	 * Return whether this cache manager accepts and converts {@code null} values
	 * for all of its caches.
	 * <p>
	 *  返回此缓存管理器是否接受并转换其所有缓存的{@code null}值
	 * 
	 */
	public boolean isAllowNullValues() {
		return this.allowNullValues;
	}

	/**
	 * Specify whether this cache manager stores a copy of each entry ({@code true}
	 * or the reference ({@code false} for all of its caches.
	 * <p>Default is "false" so that the value itself is stored and no serializable
	 * contract is required on cached values.
	 * <p>Note: A change of the store-by-value setting will reset all existing caches,
	 * if any, to reconfigure them with the new store-by-value requirement.
	 * <p>
	 * 指定此缓存管理器是否存储每个条目的副本({@code true}或其所有缓存的引用({@code false}),默认值为"false",以便存储该值本身并且无序列化合同在缓存的值<p>上需要注意注意：
	 * 逐个值设置的更改将重置所有现有的高速缓存(如果有的话),以使用新的逐个存储值要求进行重新配置。
	 * 
	 * 
	 * @since 4.3
	 */
	public void setStoreByValue(boolean storeByValue) {
		if (storeByValue != this.storeByValue) {
			this.storeByValue = storeByValue;
			// Need to recreate all Cache instances with the new store-by-value configuration...
			recreateCaches();
		}
	}

	/**
	 * Return whether this cache manager stores a copy of each entry or
	 * a reference for all its caches. If store by value is enabled, any
	 * cache entry must be serializable.
	 * <p>
	 *  返回此缓存管理器是否存储每个条目的副本或其所有缓存的引用如果启用按值存储,则任何缓存条目都必须是可序列化的
	 * 
	 * 
	 * @since 4.3
	 */
	public boolean isStoreByValue() {
		return this.storeByValue;
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.serialization = new SerializationDelegate(classLoader);
		// Need to recreate all Cache instances with new ClassLoader in store-by-value mode...
		if (isStoreByValue()) {
			recreateCaches();
		}
	}


	@Override
	public Collection<String> getCacheNames() {
		return Collections.unmodifiableSet(this.cacheMap.keySet());
	}

	@Override
	public Cache getCache(String name) {
		Cache cache = this.cacheMap.get(name);
		if (cache == null && this.dynamic) {
			synchronized (this.cacheMap) {
				cache = this.cacheMap.get(name);
				if (cache == null) {
					cache = createConcurrentMapCache(name);
					this.cacheMap.put(name, cache);
				}
			}
		}
		return cache;
	}

	private void recreateCaches() {
		for (Map.Entry<String, Cache> entry : this.cacheMap.entrySet()) {
			entry.setValue(createConcurrentMapCache(entry.getKey()));
		}
	}

	/**
	 * Create a new ConcurrentMapCache instance for the specified cache name.
	 * <p>
	 *  为指定的缓存名称创建一个新的ConcurrentMapCache实例
	 * 
	 * @param name the name of the cache
	 * @return the ConcurrentMapCache (or a decorator thereof)
	 */
	protected Cache createConcurrentMapCache(String name) {
		SerializationDelegate actualSerialization = (isStoreByValue() ? this.serialization : null);
		return new ConcurrentMapCache(name, new ConcurrentHashMap<Object, Object>(256),
				isAllowNullValues(), actualSerialization);

	}

}
