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

package org.springframework.cache.ehcache;

import java.lang.reflect.Method;
import java.util.Set;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.bootstrap.BootstrapCacheLoader;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.constructs.blocking.BlockingCache;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;
import net.sf.ehcache.constructs.blocking.UpdatingCacheEntryFactory;
import net.sf.ehcache.constructs.blocking.UpdatingSelfPopulatingCache;
import net.sf.ehcache.event.CacheEventListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/**
 * {@link FactoryBean} that creates a named EhCache {@link net.sf.ehcache.Cache} instance
 * (or a decorator that implements the {@link net.sf.ehcache.Ehcache} interface),
 * representing a cache region within an EhCache {@link net.sf.ehcache.CacheManager}.
 *
 * <p>If the specified named cache is not configured in the cache configuration descriptor,
 * this FactoryBean will construct an instance of a Cache with the provided name and the
 * specified cache properties and add it to the CacheManager for later retrieval. If some
 * or all properties are not set at configuration time, this FactoryBean will use defaults.
 *
 * <p>Note: If the named Cache instance is found, the properties will be ignored and the
 * Cache instance will be retrieved from the CacheManager.
 *
 * <p>Note: As of Spring 4.1, Spring's EhCache support requires EhCache 2.5 or higher.
 *
 * <p>
 * {@link FactoryBean}创建一个名为EhCache {@link netsfehcacheCache}实例(或实现{@link netsfehcacheEhcache}接口的装饰器),表示
 * EhCache {@link netsfehcacheCacheManager}中的缓存区域。
 * 
 *  <p>如果在缓存配置描述符中未配置指定的命名高速缓存,则此FactoryBean将使用提供的名称和指定的缓存属性构建缓存的实例,并将其添加到CacheManager以供以后检索。
 * 如果某些或所有属性为未在配置时设置,此FactoryBean将使用默认值。
 * 
 *  注意：如果找到命名的缓存实例,属性将被忽略,并且Cache实例将从CacheManager中检索
 * 
 * 注意：从Spring 41开始,Spring的EhCache支持需要EhCache 25或更高版本
 * 
 * 
 * @author Juergen Hoeller
 * @author Dmitriy Kopylenko
 * @since 1.1.1
 * @see #setCacheManager
 * @see EhCacheManagerFactoryBean
 * @see net.sf.ehcache.Cache
 */
public class EhCacheFactoryBean extends CacheConfiguration implements FactoryBean<Ehcache>, BeanNameAware, InitializingBean {

	// EhCache's setStatisticsEnabled(boolean) available? Not anymore as of EhCache 2.7...
	private static final Method setStatisticsEnabledMethod =
			ClassUtils.getMethodIfAvailable(Ehcache.class, "setStatisticsEnabled", boolean.class);

	// EhCache's setSampledStatisticsEnabled(boolean) available? Not anymore as of EhCache 2.7...
	private static final Method setSampledStatisticsEnabledMethod =
			ClassUtils.getMethodIfAvailable(Ehcache.class, "setSampledStatisticsEnabled", boolean.class);


	protected final Log logger = LogFactory.getLog(getClass());

	private CacheManager cacheManager;

	private boolean blocking = false;

	private CacheEntryFactory cacheEntryFactory;

	private BootstrapCacheLoader bootstrapCacheLoader;

	private Set<CacheEventListener> cacheEventListeners;

	private boolean statisticsEnabled = false;

	private boolean sampledStatisticsEnabled = false;

	private boolean disabled = false;

	private String beanName;

	private Ehcache cache;


	@SuppressWarnings("deprecation")
	public EhCacheFactoryBean() {
		setMaxEntriesLocalHeap(10000);
		setMaxElementsOnDisk(10000000);
		setTimeToLiveSeconds(120);
		setTimeToIdleSeconds(120);
	}


	/**
	 * Set a CacheManager from which to retrieve a named Cache instance.
	 * By default, {@code CacheManager.getInstance()} will be called.
	 * <p>Note that in particular for persistent caches, it is advisable to
	 * properly handle the shutdown of the CacheManager: Set up a separate
	 * EhCacheManagerFactoryBean and pass a reference to this bean property.
	 * <p>A separate EhCacheManagerFactoryBean is also necessary for loading
	 * EhCache configuration from a non-default config location.
	 * <p>
	 *  设置一个CacheManager从中检索一个命名的Cache实例默认情况下,{@code CacheManagergetInstance()}将被调用<p>请注意,特别是对于持久性高速缓存,建议正确处
	 * 理CacheManager的关闭：设置单独的EhCacheManagerFactoryBean并传递对该bean属性的引用<p>还需要单独的EhCacheManagerFactoryBean从非默认配置
	 * 位置加载EhCache配置。
	 * 
	 * 
	 * @see EhCacheManagerFactoryBean
	 * @see net.sf.ehcache.CacheManager#getInstance
	 */
	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	/**
	 * Set a name for which to retrieve or create a cache instance.
	 * Default is the bean name of this EhCacheFactoryBean.
	 * <p>
	 *  设置要检索或创建缓存实例的名称Default是此EhCacheFactoryBean的bean名称
	 * 
	 */
	public void setCacheName(String cacheName) {
		setName(cacheName);
	}

	/**
	/* <p>
	/* 
	 * @see #setTimeToLiveSeconds(long)
	 */
	public void setTimeToLive(int timeToLive) {
		setTimeToLiveSeconds(timeToLive);
	}

	/**
	/* <p>
	/* 
	 * @see #setTimeToIdleSeconds(long)
	 */
	public void setTimeToIdle(int timeToIdle) {
		setTimeToIdleSeconds(timeToIdle);
	}

	/**
	/* <p>
	/* 
	 * @see #setDiskSpoolBufferSizeMB(int)
	 */
	public void setDiskSpoolBufferSize(int diskSpoolBufferSize) {
		setDiskSpoolBufferSizeMB(diskSpoolBufferSize);
	}

	/**
	 * Set whether to use a blocking cache that lets read attempts block
	 * until the requested element is created.
	 * <p>If you intend to build a self-populating blocking cache,
	 * consider specifying a {@link #setCacheEntryFactory CacheEntryFactory}.
	 * <p>
	 * 设置是否使用允许读取尝试阻塞的阻止缓存,直到创建所请求的元素<p>如果您打算构建自填充阻塞缓存,请考虑指定{@link #setCacheEntryFactory CacheEntryFactory}。
	 * 
	 * 
	 * @see net.sf.ehcache.constructs.blocking.BlockingCache
	 * @see #setCacheEntryFactory
	 */
	public void setBlocking(boolean blocking) {
		this.blocking = blocking;
	}

	/**
	 * Set an EhCache {@link net.sf.ehcache.constructs.blocking.CacheEntryFactory}
	 * to use for a self-populating cache. If such a factory is specified,
	 * the cache will be decorated with EhCache's
	 * {@link net.sf.ehcache.constructs.blocking.SelfPopulatingCache}.
	 * <p>The specified factory can be of type
	 * {@link net.sf.ehcache.constructs.blocking.UpdatingCacheEntryFactory},
	 * which will lead to the use of an
	 * {@link net.sf.ehcache.constructs.blocking.UpdatingSelfPopulatingCache}.
	 * <p>Note: Any such self-populating cache is automatically a blocking cache.
	 * <p>
	 *  设置EhCache {@link netsfehcacheconstructsblockingCacheEntryFactory}用于自填充缓存如果指定了这样的工厂,缓存将使用EhCache的{@link netsfehcacheconstructsblockingSelfPopulatingCache}
	 * 进行装饰<p>指定的工厂可以是{@link netsfehcacheconstructsblockingUpdatingCacheEntryFactory}类型,这将导致使用{@link netsfehcacheconstructsblockingUpdatingSelfPopulatingCache}
	 *  <p>注意：任何此类自填充缓存都将自动为阻塞缓存。
	 * 
	 * 
	 * @see net.sf.ehcache.constructs.blocking.SelfPopulatingCache
	 * @see net.sf.ehcache.constructs.blocking.UpdatingSelfPopulatingCache
	 * @see net.sf.ehcache.constructs.blocking.UpdatingCacheEntryFactory
	 */
	public void setCacheEntryFactory(CacheEntryFactory cacheEntryFactory) {
		this.cacheEntryFactory = cacheEntryFactory;
	}

	/**
	 * Set an EhCache {@link net.sf.ehcache.bootstrap.BootstrapCacheLoader}
	 * for this cache, if any.
	 * <p>
	 * 为此缓存设置EhCache {@link netsfehcachebootstrapBootstrapCacheLoader}(如果有)
	 * 
	 */
	public void setBootstrapCacheLoader(BootstrapCacheLoader bootstrapCacheLoader) {
		this.bootstrapCacheLoader = bootstrapCacheLoader;
	}

	/**
	 * Specify EhCache {@link net.sf.ehcache.event.CacheEventListener cache event listeners}
	 * to registered with this cache.
	 * <p>
	 *  指定在此缓存中注册的EhCache {@link netsfehcacheeventCacheEventListener缓存事件侦听器}
	 * 
	 */
	public void setCacheEventListeners(Set<CacheEventListener> cacheEventListeners) {
		this.cacheEventListeners = cacheEventListeners;
	}

	/**
	 * Set whether to enable EhCache statistics on this cache.
	 * <p>Note: As of EhCache 2.7, statistics are enabled by default, and cannot be turned off.
	 * This setter therefore has no effect in such a scenario.
	 * <p>
	 *  设置是否启用此缓存上的EhCache统计信息<p>注意：从EhCache 27起,默认情况下启用统计信息,不能关闭此设置器因此在此情况下不起作用
	 * 
	 * 
	 * @see net.sf.ehcache.Ehcache#setStatisticsEnabled
	 */
	public void setStatisticsEnabled(boolean statisticsEnabled) {
		this.statisticsEnabled = statisticsEnabled;
	}

	/**
	 * Set whether to enable EhCache's sampled statistics on this cache.
	 * <p>Note: As of EhCache 2.7, statistics are enabled by default, and cannot be turned off.
	 * This setter therefore has no effect in such a scenario.
	 * <p>
	 *  设置是否在此缓存上启用EhCache的采样统计信息<p>注意：从EhCache 27开始,默认情况下启用统计信息,不能关闭此设置器在此情况下不起作用
	 * 
	 * 
	 * @see net.sf.ehcache.Ehcache#setSampledStatisticsEnabled
	 */
	public void setSampledStatisticsEnabled(boolean sampledStatisticsEnabled) {
		this.sampledStatisticsEnabled = sampledStatisticsEnabled;
	}

	/**
	 * Set whether this cache should be marked as disabled.
	 * <p>
	 *  设置此缓存是否应标记为禁用
	 * 
	 * 
	 * @see net.sf.ehcache.Cache#setDisabled
	 */
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	@Override
	public void setBeanName(String name) {
		this.beanName = name;
	}


	@Override
	public void afterPropertiesSet() throws CacheException {
		// If no cache name given, use bean name as cache name.
		String cacheName = getName();
		if (cacheName == null) {
			cacheName = this.beanName;
			setName(cacheName);
		}

		// If no CacheManager given, fetch the default.
		if (this.cacheManager == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Using default EhCache CacheManager for cache region '" + cacheName + "'");
			}
			this.cacheManager = CacheManager.getInstance();
		}

		synchronized (this.cacheManager) {
			// Fetch cache region: If none with the given name exists, create one on the fly.
			Ehcache rawCache;
			boolean cacheExists = this.cacheManager.cacheExists(cacheName);

			if (cacheExists) {
				if (logger.isDebugEnabled()) {
					logger.debug("Using existing EhCache cache region '" + cacheName + "'");
				}
				rawCache = this.cacheManager.getEhcache(cacheName);
			}
			else {
				if (logger.isDebugEnabled()) {
					logger.debug("Creating new EhCache cache region '" + cacheName + "'");
				}
				rawCache = createCache();
				rawCache.setBootstrapCacheLoader(this.bootstrapCacheLoader);
			}

			if (this.cacheEventListeners != null) {
				for (CacheEventListener listener : this.cacheEventListeners) {
					rawCache.getCacheEventNotificationService().registerListener(listener);
				}
			}

			// Needs to happen after listener registration but before setStatisticsEnabled
			if (!cacheExists) {
				this.cacheManager.addCache(rawCache);
			}

			// Only necessary on EhCache <2.7: As of 2.7, statistics are on by default.
			if (this.statisticsEnabled && setStatisticsEnabledMethod != null) {
				ReflectionUtils.invokeMethod(setStatisticsEnabledMethod, rawCache, true);
			}
			if (this.sampledStatisticsEnabled && setSampledStatisticsEnabledMethod != null) {
				ReflectionUtils.invokeMethod(setSampledStatisticsEnabledMethod, rawCache, true);
			}

			if (this.disabled) {
				rawCache.setDisabled(true);
			}

			Ehcache decoratedCache = decorateCache(rawCache);
			if (decoratedCache != rawCache) {
				this.cacheManager.replaceCacheWithDecoratedCache(rawCache, decoratedCache);
			}
			this.cache = decoratedCache;
		}
	}

	/**
	 * Create a raw Cache object based on the configuration of this FactoryBean.
	 * <p>
	 *  根据此FactoryBean的配置创建一个原始的Cache对象
	 * 
	 */
	protected Cache createCache() {
		return new Cache(this);
	}

	/**
	 * Decorate the given Cache, if necessary.
	 * <p>
	 *  如果需要,装饰给定的缓存
	 * 
	 * 
	 * @param cache the raw Cache object, based on the configuration of this FactoryBean
	 * @return the (potentially decorated) cache object to be registered with the CacheManager
	 */
	protected Ehcache decorateCache(Ehcache cache) {
		if (this.cacheEntryFactory != null) {
			if (this.cacheEntryFactory instanceof UpdatingCacheEntryFactory) {
				return new UpdatingSelfPopulatingCache(cache, (UpdatingCacheEntryFactory) this.cacheEntryFactory);
			}
			else {
				return new SelfPopulatingCache(cache, this.cacheEntryFactory);
			}
		}
		if (this.blocking) {
			return new BlockingCache(cache);
		}
		return cache;
	}


	@Override
	public Ehcache getObject() {
		return this.cache;
	}

	/**
	 * Predict the particular {@code Ehcache} implementation that will be returned from
	 * {@link #getObject()} based on logic in {@link #createCache()} and
	 * {@link #decorateCache(Ehcache)} as orchestrated by {@link #afterPropertiesSet()}.
	 * <p>
	 * 根据{@link #createCache()}和{@link #decorateCache(Ehcache)}中的逻辑,根据{@link编排的{@link #getObject()}返回的特定{@code Ehcache}
	 * 实现#afterPropertiesSet()}。
	 */
	@Override
	public Class<? extends Ehcache> getObjectType() {
		if (this.cache != null) {
			return this.cache.getClass();
		}
		if (this.cacheEntryFactory != null) {
			if (this.cacheEntryFactory instanceof UpdatingCacheEntryFactory) {
				return UpdatingSelfPopulatingCache.class;
			}
			else {
				return SelfPopulatingCache.class;
			}
		}
		if (this.blocking) {
			return BlockingCache.class;
		}
		return Cache.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
