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

package org.springframework.cache.ehcache;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.ConfigurationFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

/**
 * {@link FactoryBean} that exposes an EhCache {@link net.sf.ehcache.CacheManager}
 * instance (independent or shared), configured from a specified config location.
 *
 * <p>If no config location is specified, a CacheManager will be configured from
 * "ehcache.xml" in the root of the class path (that is, default EhCache initialization
 * - as defined in the EhCache docs - will apply).
 *
 * <p>Setting up a separate EhCacheManagerFactoryBean is also advisable when using
 * EhCacheFactoryBean, as it provides a (by default) independent CacheManager instance
 * and cares for proper shutdown of the CacheManager. EhCacheManagerFactoryBean is
 * also necessary for loading EhCache configuration from a non-default config location.
 *
 * <p>Note: As of Spring 4.1, Spring's EhCache support requires EhCache 2.5 or higher.
 *
 * <p>
 *  {@link FactoryBean}公开了从指定的配置位置配置的EhCache {@link netsfehcacheCacheManager}实例(独立或共享)
 * 
 * <p>如果没有指定配置位置,则会在类路径根目录中的"ehcachexml"中配置CacheManager(即EhCache文档中定义的默认EhCache初始化 - 将适用)
 * 
 *  使用EhCacheFactoryBean时,设置单独的EhCacheManagerFactoryBean也是可取的,因为它提供了(默认情况下)独立的CacheManager实例,并且关心CacheMa
 * nager的正确关闭EhCacheManagerFactoryBean也是从非默认配置位置加载EhCache配置所必需的。
 * 
 *  注意：从Spring 41开始,Spring的EhCache支持需要EhCache 25或更高版本
 * 
 * 
 * @author Juergen Hoeller
 * @author Dmitriy Kopylenko
 * @since 1.1.1
 * @see #setConfigLocation
 * @see #setShared
 * @see EhCacheFactoryBean
 * @see net.sf.ehcache.CacheManager
 */
public class EhCacheManagerFactoryBean implements FactoryBean<CacheManager>, InitializingBean, DisposableBean {

	protected final Log logger = LogFactory.getLog(getClass());

	private Resource configLocation;

	private String cacheManagerName;

	private boolean acceptExisting = false;

	private boolean shared = false;

	private CacheManager cacheManager;

	private boolean locallyManaged = true;


	/**
	 * Set the location of the EhCache config file. A typical value is "/WEB-INF/ehcache.xml".
	 * <p>Default is "ehcache.xml" in the root of the class path, or if not found,
	 * "ehcache-failsafe.xml" in the EhCache jar (default EhCache initialization).
	 * <p>
	 * 设置EhCache配置文件的位置典型值为"/ WEB-INF / ehcachexml"<p>默认为类路径根目录中的"ehcachexml",如果没有找到EhCache jar中的"ehcache-fa
	 * iluresafexml" (默认EhCache初始化)。
	 * 
	 * 
	 * @see net.sf.ehcache.CacheManager#create(java.io.InputStream)
	 * @see net.sf.ehcache.CacheManager#CacheManager(java.io.InputStream)
	 */
	public void setConfigLocation(Resource configLocation) {
		this.configLocation = configLocation;
	}

	/**
	 * Set the name of the EhCache CacheManager (if a specific name is desired).
	 * <p>
	 *  设置EhCache CacheManager的名称(如果需要特定名称)
	 * 
	 * 
	 * @see net.sf.ehcache.config.Configuration#setName(String)
	 */
	public void setCacheManagerName(String cacheManagerName) {
		this.cacheManagerName = cacheManagerName;
	}

	/**
	 * Set whether an existing EhCache CacheManager of the same name will be accepted
	 * for this EhCacheManagerFactoryBean setup. Default is "false".
	 * <p>Typically used in combination with {@link #setCacheManagerName "cacheManagerName"}
	 * but will simply work with the default CacheManager name if none specified.
	 * All references to the same CacheManager name (or the same default) in the
	 * same ClassLoader space will share the specified CacheManager then.
	 * <p>
	 *  设置EhCacheManagerFactoryBean设置中是否接受同一名称的现有EhCache CacheManager默认为"false"<p>通常与{@link #setCacheManagerName"cacheManagerName"}
	 * 组合使用,但是如果没有,将简单地使用默认的CacheManager名称指定在同一个ClassLoader空间中对同一CacheManager名称(或相同的默认值)的所有引用将共享指定的CacheMana
	 * ger,。
	 * 
	 * 
	 * @see #setCacheManagerName
	 * #see #setShared
	 * @see net.sf.ehcache.CacheManager#getCacheManager(String)
	 * @see net.sf.ehcache.CacheManager#CacheManager()
	 */
	public void setAcceptExisting(boolean acceptExisting) {
		this.acceptExisting = acceptExisting;
	}

	/**
	 * Set whether the EhCache CacheManager should be shared (as a singleton at the
	 * ClassLoader level) or independent (typically local within the application).
	 * Default is "false", creating an independent local instance.
	 * <p><b>NOTE:</b> This feature allows for sharing this EhCacheManagerFactoryBean's
	 * CacheManager with any code calling <code>CacheManager.create()</code> in the same
	 * ClassLoader space, with no need to agree on a specific CacheManager name.
	 * However, it only supports a single EhCacheManagerFactoryBean involved which will
	 * control the lifecycle of the underlying CacheManager (in particular, its shutdown).
	 * <p>This flag overrides {@link #setAcceptExisting "acceptExisting"} if both are set,
	 * since it indicates the 'stronger' mode of sharing.
	 * <p>
	 * 设置EhCache CacheManager是否应该共享(作为ClassLoader级别的单例)或独立(通常是应用程序中的本地)默认值为"false",创建独立的本地实例<p> <b>注意：</b>功能
	 * 允许在同一个ClassLoader空间中与任何调用<code> CacheManagercreate()</code>的代码共享此EhCacheManagerFactoryBean的CacheManag
	 * er,而不需要对特定的CacheManager名称达成一致。
	 * 
	 * @see #setCacheManagerName
	 * @see #setAcceptExisting
	 * @see net.sf.ehcache.CacheManager#create()
	 * @see net.sf.ehcache.CacheManager#CacheManager()
	 */
	public void setShared(boolean shared) {
		this.shared = shared;
	}


	@Override
	public void afterPropertiesSet() throws CacheException {
		if (logger.isInfoEnabled()) {
			logger.info("Initializing EhCache CacheManager" +
					(this.cacheManagerName != null ? " '" + this.cacheManagerName + "'" : ""));
		}

		Configuration configuration = (this.configLocation != null ?
				EhCacheManagerUtils.parseConfiguration(this.configLocation) : ConfigurationFactory.parseConfiguration());
		if (this.cacheManagerName != null) {
			configuration.setName(this.cacheManagerName);
		}

		if (this.shared) {
			// Old-school EhCache singleton sharing...
			// No way to find out whether we actually created a new CacheManager
			// or just received an existing singleton reference.
			this.cacheManager = CacheManager.create(configuration);
		}
		else if (this.acceptExisting) {
			// EhCache 2.5+: Reusing an existing CacheManager of the same name.
			// Basically the same code as in CacheManager.getInstance(String),
			// just storing whether we're dealing with an existing instance.
			synchronized (CacheManager.class) {
				this.cacheManager = CacheManager.getCacheManager(this.cacheManagerName);
				if (this.cacheManager == null) {
					this.cacheManager = new CacheManager(configuration);
				}
				else {
					this.locallyManaged = false;
				}
			}
		}
		else {
			// Throwing an exception if a CacheManager of the same name exists already...
			this.cacheManager = new CacheManager(configuration);
		}
	}


	@Override
	public CacheManager getObject() {
		return this.cacheManager;
	}

	@Override
	public Class<? extends CacheManager> getObjectType() {
		return (this.cacheManager != null ? this.cacheManager.getClass() : CacheManager.class);
	}

	@Override
	public boolean isSingleton() {
		return true;
	}


	@Override
	public void destroy() {
		if (this.locallyManaged) {
			if (logger.isInfoEnabled()) {
				logger.info("Shutting down EhCache CacheManager" +
						(this.cacheManagerName != null ? " '" + this.cacheManagerName + "'" : ""));
			}
			this.cacheManager.shutdown();
		}
	}

}
