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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

/**
 * Abstract base class implementing the common {@link CacheManager} methods.
 * Useful for 'static' environments where the backing caches do not change.
 *
 * <p>
 *  实现普通的{@link CacheManager}方法的抽象基类适用于"静态"环境,其中后备缓存不会更改
 * 
 * 
 * @author Costin Leau
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 * @since 3.1
 */
public abstract class AbstractCacheManager implements CacheManager, InitializingBean {

	private final ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<String, Cache>(16);

	private volatile Set<String> cacheNames = Collections.emptySet();


	// Early cache initialization on startup

	@Override
	public void afterPropertiesSet() {
		initializeCaches();
	}

	/**
	 * Initialize the static configuration of caches.
	 * <p>Triggered on startup through {@link #afterPropertiesSet()};
	 * can also be called to re-initialize at runtime.
	 * <p>
	 * 初始化缓存的静态配置<p>通过{@link #afterPropertiesSet()}启动时触发;也可以调用在运行时重新初始化
	 * 
	 * 
	 * @since 4.2.2
	 * @see #loadCaches()
	 */
	public void initializeCaches() {
		Collection<? extends Cache> caches = loadCaches();

		synchronized (this.cacheMap) {
			this.cacheNames = Collections.emptySet();
			this.cacheMap.clear();
			Set<String> cacheNames = new LinkedHashSet<String>(caches.size());
			for (Cache cache : caches) {
				String name = cache.getName();
				this.cacheMap.put(name, decorateCache(cache));
				cacheNames.add(name);
			}
			this.cacheNames = Collections.unmodifiableSet(cacheNames);
		}
	}

	/**
	 * Load the initial caches for this cache manager.
	 * <p>Called by {@link #afterPropertiesSet()} on startup.
	 * The returned collection may be empty but must not be {@code null}.
	 * <p>
	 *  加载此缓存管理器的初始缓存<p>启动时由{@link #afterPropertiesSet()}调用)返回的集合可能为空,但不能为{@code null}
	 * 
	 */
	protected abstract Collection<? extends Cache> loadCaches();


	// Lazy cache initialization on access

	@Override
	public Cache getCache(String name) {
		Cache cache = this.cacheMap.get(name);
		if (cache != null) {
			return cache;
		}
		else {
			// Fully synchronize now for missing cache creation...
			synchronized (this.cacheMap) {
				cache = this.cacheMap.get(name);
				if (cache == null) {
					cache = getMissingCache(name);
					if (cache != null) {
						cache = decorateCache(cache);
						this.cacheMap.put(name, cache);
						updateCacheNames(name);
					}
				}
				return cache;
			}
		}
	}

	@Override
	public Collection<String> getCacheNames() {
		return this.cacheNames;
	}


	// Common cache initialization delegates for subclasses

	/**
	 * Check for a registered cache of the given name.
	 * In contrast to {@link #getCache(String)}, this method does not trigger
	 * the lazy creation of missing caches via {@link #getMissingCache(String)}.
	 * <p>
	 *  检查给定名称的注册缓存与{@link #getCache(String)}相反,此方法不会通过{@link #getMissingCache(String)}触发丢失缓存的延迟创建
	 * 
	 * 
	 * @param name the cache identifier (must not be {@code null})
	 * @return the associated Cache instance, or {@code null} if none found
	 * @since 4.1
	 * @see #getCache(String)
	 * @see #getMissingCache(String)
	 */
	protected final Cache lookupCache(String name) {
		return this.cacheMap.get(name);
	}

	/**
	 * Dynamically register an additional Cache with this manager.
	 * <p>
	 *  与此经理动态注册一个Cache
	 * 
	 * 
	 * @param cache the Cache to register
	 * @deprecated as of Spring 4.3, in favor of {@link #getMissingCache(String)}
	 */
	@Deprecated
	protected final void addCache(Cache cache) {
		String name = cache.getName();
		synchronized (this.cacheMap) {
			if (this.cacheMap.put(name, decorateCache(cache)) == null) {
				updateCacheNames(name);
			}
		}
	}

	/**
	 * Update the exposed {@link #cacheNames} set with the given name.
	 * <p>This will always be called within a full {@link #cacheMap} lock
	 * and effectively behaves like a {@code CopyOnWriteArraySet} with
	 * preserved order but exposed as an unmodifiable reference.
	 * <p>
	 * 使用给定的名称更新暴露的{@link #cacheNames}集合<p>这将始终在完整的{@link #cacheMap}锁中调用,并且有效地表现为具有保留顺序的{@code CopyOnWriteArraySet}
	 * ,但是作为不可修改的公开参考。
	 * 
	 * 
	 * @param name the name of the cache to be added
	 */
	private void updateCacheNames(String name) {
		Set<String> cacheNames = new LinkedHashSet<String>(this.cacheNames.size() + 1);
		cacheNames.addAll(this.cacheNames);
		cacheNames.add(name);
		this.cacheNames = Collections.unmodifiableSet(cacheNames);
	}


	// Overridable template methods for cache initialization

	/**
	 * Decorate the given Cache object if necessary.
	 * <p>
	 *  必要时装载给定的Cache对象
	 * 
	 * 
	 * @param cache the Cache object to be added to this CacheManager
	 * @return the decorated Cache object to be used instead,
	 * or simply the passed-in Cache object by default
	 */
	protected Cache decorateCache(Cache cache) {
		return cache;
	}

	/**
	 * Return a missing cache with the specified {@code name} or {@code null} if
	 * such cache does not exist or could not be created on the fly.
	 * <p>Some caches may be created at runtime if the native provider supports
	 * it. If a lookup by name does not yield any result, a subclass gets a chance
	 * to register such a cache at runtime. The returned cache will be automatically
	 * added to this instance.
	 * <p>
	 *  如果这样的缓存不存在或者不能在运行中创建,则返回一个缺少的缓存与指定的{@code name}或{@code null} <p>如果本地提供商支持某些缓存可能会在运行时创建如果按名称查找不会产生任何结
	 * 果,一个子类有机会在运行时注册这样的缓存返回的缓存将被自动添加到此实例。
	 * 
	 * @param name the name of the cache to retrieve
	 * @return the missing cache or {@code null} if no such cache exists or could be
	 * created
	 * @since 4.1
	 * @see #getCache(String)
	 */
	protected Cache getMissingCache(String name) {
		return null;
	}

}
