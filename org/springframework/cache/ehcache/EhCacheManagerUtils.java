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

import java.io.IOException;
import java.io.InputStream;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.ConfigurationFactory;

import org.springframework.core.io.Resource;

/**
 * Convenient builder methods for EhCache 2.5+ {@link CacheManager} setup,
 * providing easy programmatic bootstrapping from a Spring-provided resource.
 * This is primarily intended for use within {@code @Bean} methods in a
 * Spring configuration class.
 *
 * <p>These methods are a simple alternative to custom {@link CacheManager} setup
 * code. For any advanced purposes, consider using {@link #parseConfiguration},
 * customizing the configuration object, and then calling the
 * {@link CacheManager#CacheManager(Configuration)} constructor.
 *
 * <p>
 * EhCache 25+ {@link CacheManager}设置方便的构建器方法,从Spring提供的资源提供简单的编程引导这主要是为了在Spring配置类中的{@code @Bean}方法中使用。
 * 
 *  <p>这些方法是自定义{@link CacheManager}设置代码的简单替代方法对于任何高级目的,请考虑使用{@link #parseConfiguration}自定义配置对象,然后调用{@link CacheManager#CacheManager(Configuration) }
 * 构造函数。
 * 
 * 
 * @author Juergen Hoeller
 * @since 4.1
 */
public abstract class EhCacheManagerUtils {

	/**
	 * Build an EhCache {@link CacheManager} from the default configuration.
	 * <p>The CacheManager will be configured from "ehcache.xml" in the root of the class path
	 * (that is, default EhCache initialization - as defined in the EhCache docs - will apply).
	 * If no configuration file can be found, a fail-safe fallback configuration will be used.
	 * <p>
	 * 从默认配置构建一个EhCache {@link CacheManager} <p> CacheManager将从类路径的根目录中的"ehcachexml"配置(即,EhCache文档中定义的默认EhCa
	 * che初始化 - 将应用)如果不能找到配置文件,将使用故障安全后备配置。
	 * 
	 * 
	 * @return the new EhCache CacheManager
	 * @throws CacheException in case of configuration parsing failure
	 */
	public static CacheManager buildCacheManager() throws CacheException {
		return new CacheManager(ConfigurationFactory.parseConfiguration());
	}

	/**
	 * Build an EhCache {@link CacheManager} from the default configuration.
	 * <p>The CacheManager will be configured from "ehcache.xml" in the root of the class path
	 * (that is, default EhCache initialization - as defined in the EhCache docs - will apply).
	 * If no configuration file can be found, a fail-safe fallback configuration will be used.
	 * <p>
	 *  从默认配置构建一个EhCache {@link CacheManager} <p> CacheManager将从类路径的根目录中的"ehcachexml"配置(即,EhCache文档中定义的默认EhC
	 * ache初始化 - 将应用)如果不能找到配置文件,将使用故障安全后备配置。
	 * 
	 * 
	 * @param name the desired name of the cache manager
	 * @return the new EhCache CacheManager
	 * @throws CacheException in case of configuration parsing failure
	 */
	public static CacheManager buildCacheManager(String name) throws CacheException {
		Configuration configuration = ConfigurationFactory.parseConfiguration();
		configuration.setName(name);
		return new CacheManager(configuration);
	}

	/**
	 * Build an EhCache {@link CacheManager} from the given configuration resource.
	 * <p>
	 *  从给定的配置资源构建一个EhCache {@link CacheManager}
	 * 
	 * 
	 * @param configLocation the location of the configuration file (as a Spring resource)
	 * @return the new EhCache CacheManager
	 * @throws CacheException in case of configuration parsing failure
	 */
	public static CacheManager buildCacheManager(Resource configLocation) throws CacheException {
		return new CacheManager(parseConfiguration(configLocation));
	}

	/**
	 * Build an EhCache {@link CacheManager} from the given configuration resource.
	 * <p>
	 * 从给定的配置资源构建一个EhCache {@link CacheManager}
	 * 
	 * 
	 * @param name the desired name of the cache manager
	 * @param configLocation the location of the configuration file (as a Spring resource)
	 * @return the new EhCache CacheManager
	 * @throws CacheException in case of configuration parsing failure
	 */
	public static CacheManager buildCacheManager(String name, Resource configLocation) throws CacheException {
		Configuration configuration = parseConfiguration(configLocation);
		configuration.setName(name);
		return new CacheManager(configuration);
	}

	/**
	 * Parse EhCache configuration from the given resource, for further use with
	 * custom {@link CacheManager} creation.
	 * <p>
	 *  从给定资源解析EhCache配置,进一步使用自定义{@link CacheManager}创建
	 * 
	 * @param configLocation the location of the configuration file (as a Spring resource)
	 * @return the EhCache Configuration handle
	 * @throws CacheException in case of configuration parsing failure
	 * @see CacheManager#CacheManager(Configuration)
	 * @see CacheManager#create(Configuration)
	 */
	public static Configuration parseConfiguration(Resource configLocation) throws CacheException {
		InputStream is = null;
		try {
			is = configLocation.getInputStream();
			return ConfigurationFactory.parseConfiguration(is);
		}
		catch (IOException ex) {
			throw new CacheException("Failed to parse EhCache configuration resource", ex);
		}
		finally {
			if (is != null) {
				try {
					is.close();
				}
				catch (IOException ex) {
					// ignore
				}
			}
		}
	}

}
