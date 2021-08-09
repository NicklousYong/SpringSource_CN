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

package org.springframework.cache.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

/**
 * Composite {@link CacheManager} implementation that iterates over
 * a given collection of delegate {@link CacheManager} instances.
 *
 * <p>Allows {@link NoOpCacheManager} to be automatically added to the end of
 * the list for handling cache declarations without a backing store. Otherwise,
 * any custom {@link CacheManager} may play that role of the last delegate as
 * well, lazily creating cache regions for any requested name.
 *
 * <p>Note: Regular CacheManagers that this composite manager delegates to need
 * to return {@code null} from {@link #getCache(String)} if they are unaware of
 * the specified cache name, allowing for iteration to the next delegate in line.
 * However, most {@link CacheManager} implementations fall back to lazy creation
 * of named caches once requested; check out the specific configuration details
 * for a 'static' mode with fixed cache names, if available.
 *
 * <p>
 *  复合{@link CacheManager}实现,它遍历给定的委托{@link CacheManager}实例集合
 * 
 * <p>允许{@link NoOpCacheManager}自动添加到列表的末尾,用于处理没有后备存储的缓存声明否则,任何自定义{@link CacheManager}也可能扮演上一代委员的角色,懒洋洋地
 * 创建缓存任何请求的名称的区域。
 * 
 *  注意：如果组合管理器不知道指定的高速缓存名称,则该组合管理器委托需要从{@link #getCache(String)}返回{@code null}的常规CacheManagers,允许对下一个委托进
 * 行迭代然而,大多数{@link CacheManager}实现可以追溯到一旦被请求的延迟创建命名缓存;请查看具有固定缓存名称的"静态"模式的具体配置详细信息(如果可用)。
 * 
 * 
 * @author Costin Leau
 * @author Juergen Hoeller
 * @since 3.1
 * @see #setFallbackToNoOpCache
 * @see org.springframework.cache.concurrent.ConcurrentMapCacheManager#setCacheNames
 */
public class CompositeCacheManager implements CacheManager, InitializingBean {

	private final List<CacheManager> cacheManagers = new ArrayList<CacheManager>();

	private boolean fallbackToNoOpCache = false;


	/**
	 * Construct an empty CompositeCacheManager, with delegate CacheManagers to
	 * be added via the {@link #setCacheManagers "cacheManagers"} property.
	 * <p>
	 * 构造一个空的CompositeCacheManager,代理CacheManager将通过{@link #setCacheManager"cacheManagers"}属性添加
	 * 
	 */
	public CompositeCacheManager() {
	}

	/**
	 * Construct a CompositeCacheManager from the given delegate CacheManagers.
	 * <p>
	 *  从给定的代理CacheManagers构造一个CompositeCacheManager
	 * 
	 * 
	 * @param cacheManagers the CacheManagers to delegate to
	 */
	public CompositeCacheManager(CacheManager... cacheManagers) {
		setCacheManagers(Arrays.asList(cacheManagers));
	}


	/**
	 * Specify the CacheManagers to delegate to.
	 * <p>
	 *  指定要委派的CacheManagers
	 * 
	 */
	public void setCacheManagers(Collection<CacheManager> cacheManagers) {
		this.cacheManagers.addAll(cacheManagers);
	}

	/**
	 * Indicate whether a {@link NoOpCacheManager} should be added at the end of the delegate list.
	 * In this case, any {@code getCache} requests not handled by the configured CacheManagers will
	 * be automatically handled by the {@link NoOpCacheManager} (and hence never return {@code null}).
	 * <p>
	 *  指示是否在代理列表的末尾添加{@link NoOpCacheManager}在这种情况下,由配置的CacheManagers处理的任何{@code getCache}请求都将被{@link NoOpCacheManager}
	 * 自动处理(因此从不返回{@code null})。
	 */
	public void setFallbackToNoOpCache(boolean fallbackToNoOpCache) {
		this.fallbackToNoOpCache = fallbackToNoOpCache;
	}

	@Override
	public void afterPropertiesSet() {
		if (this.fallbackToNoOpCache) {
			this.cacheManagers.add(new NoOpCacheManager());
		}
	}


	@Override
	public Cache getCache(String name) {
		for (CacheManager cacheManager : this.cacheManagers) {
			Cache cache = cacheManager.getCache(name);
			if (cache != null) {
				return cache;
			}
		}
		return null;
	}

	@Override
	public Collection<String> getCacheNames() {
		Set<String> names = new LinkedHashSet<String>();
		for (CacheManager manager : this.cacheManagers) {
			names.addAll(manager.getCacheNames());
		}
		return Collections.unmodifiableSet(names);
	}

}
