/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2013 the original author or authors.
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

package org.springframework.ui.velocity;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.CommonsLogLogChute;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * Factory that configures a VelocityEngine. Can be used standalone,
 * but typically you will either use {@link VelocityEngineFactoryBean}
 * for preparing a VelocityEngine as bean reference, or
 * {@link org.springframework.web.servlet.view.velocity.VelocityConfigurer}
 * for web views.
 *
 * <p>The optional "configLocation" property sets the location of the Velocity
 * properties file, within the current application. Velocity properties can be
 * overridden via "velocityProperties", or even completely specified locally,
 * avoiding the need for an external properties file.
 *
 * <p>The "resourceLoaderPath" property can be used to specify the Velocity
 * resource loader path via Spring's Resource abstraction, possibly relative
 * to the Spring application context.
 *
 * <p>If "overrideLogging" is true (the default), the VelocityEngine will be
 * configured to log via Commons Logging, that is, using
 * {@link CommonsLogLogChute} as log system.
 *
 * <p>The simplest way to use this class is to specify a
 * {@link #setResourceLoaderPath(String) "resourceLoaderPath"}; the
 * VelocityEngine typically then does not need any further configuration.
 *
 * <p>
 * 配置VelocityEngine的工厂可以独立使用,但通常您将使用{@link VelocityEngineFactoryBean}准备VelocityEngine作为bean引用,或者{@link orgspringframeworkwebservletviewvelocityVelocityConfigurer}
 * 用于Web视图。
 * 
 *  <p>可选的"configLocation"属性设置Velocity属性文件的位置,在当前应用程序中,Velocity属性可以通过"velocityProperties"进行覆盖,甚至可以在本地完全指
 * 定,避免需要外部属性文件。
 * 
 *  <p>"resourceLoaderPath"属性可用于通过Spring的资源抽象来指定Velocity资源加载程序路径,可能相对于Spring应用程序上下文
 * 
 * <p>如果"overrideLogging"为true(默认值),VelocityEngine将被配置为通过Commons Logging登录,即使用{@link CommonsLogLogChute}
 * 作为日志系统。
 * 
 *  <p>使用此类的最简单的方法是指定{@link #setResourceLoaderPath(String)"resourceLoaderPath"}; VelocityEngine通常不需要任何进一
 * 步的配置。
 * 
 * 
 * @author Juergen Hoeller
 * @see #setConfigLocation
 * @see #setVelocityProperties
 * @see #setResourceLoaderPath
 * @see #setOverrideLogging
 * @see #createVelocityEngine
 * @see VelocityEngineFactoryBean
 * @see org.springframework.web.servlet.view.velocity.VelocityConfigurer
 * @see org.apache.velocity.app.VelocityEngine
 * @deprecated as of Spring 4.3, in favor of FreeMarker
 */
@Deprecated
public class VelocityEngineFactory {

	protected final Log logger = LogFactory.getLog(getClass());

	private Resource configLocation;

	private final Map<String, Object> velocityProperties = new HashMap<String, Object>();

	private String resourceLoaderPath;

	private ResourceLoader resourceLoader = new DefaultResourceLoader();

	private boolean preferFileSystemAccess = true;

	private boolean overrideLogging = true;


	/**
	 * Set the location of the Velocity config file.
	 * Alternatively, you can specify all properties locally.
	 * <p>
	 *  设置Velocity配置文件的位置或者,您可以在本地指定所有属性
	 * 
	 * 
	 * @see #setVelocityProperties
	 * @see #setResourceLoaderPath
	 */
	public void setConfigLocation(Resource configLocation) {
		this.configLocation = configLocation;
	}

	/**
	 * Set Velocity properties, like "file.resource.loader.path".
	 * Can be used to override values in a Velocity config file,
	 * or to specify all necessary properties locally.
	 * <p>Note that the Velocity resource loader path also be set to any
	 * Spring resource location via the "resourceLoaderPath" property.
	 * Setting it here is just necessary when using a non-file-based
	 * resource loader.
	 * <p>
	 * 设置Velocity属性,如"fileresourceloaderpath"可用于覆盖Velocity配置文件中的值,或者在本地指定所有必需的属性<p>请注意,Velocity资源加载程序路径也可以通过
	 * "resourceLoaderPath"设置为任何Spring资源位置,属性在使用非基于文件的资源加载器时,只需设置它就可以了。
	 * 
	 * 
	 * @see #setVelocityPropertiesMap
	 * @see #setConfigLocation
	 * @see #setResourceLoaderPath
	 */
	public void setVelocityProperties(Properties velocityProperties) {
		CollectionUtils.mergePropertiesIntoMap(velocityProperties, this.velocityProperties);
	}

	/**
	 * Set Velocity properties as Map, to allow for non-String values
	 * like "ds.resource.loader.instance".
	 * <p>
	 *  将Velocity属性设置为Map,以允许非String值,如"dsresourceloaderinstance"
	 * 
	 * 
	 * @see #setVelocityProperties
	 */
	public void setVelocityPropertiesMap(Map<String, Object> velocityPropertiesMap) {
		if (velocityPropertiesMap != null) {
			this.velocityProperties.putAll(velocityPropertiesMap);
		}
	}

	/**
	 * Set the Velocity resource loader path via a Spring resource location.
	 * Accepts multiple locations in Velocity's comma-separated path style.
	 * <p>When populated via a String, standard URLs like "file:" and "classpath:"
	 * pseudo URLs are supported, as understood by ResourceLoader. Allows for
	 * relative paths when running in an ApplicationContext.
	 * <p>Will define a path for the default Velocity resource loader with the name
	 * "file". If the specified resource cannot be resolved to a {@code java.io.File},
	 * a generic SpringResourceLoader will be used under the name "spring", without
	 * modification detection.
	 * <p>Note that resource caching will be enabled in any case. With the file
	 * resource loader, the last-modified timestamp will be checked on access to
	 * detect changes. With SpringResourceLoader, the resource will be cached
	 * forever (for example for class path resources).
	 * <p>To specify a modification check interval for files, use Velocity's
	 * standard "file.resource.loader.modificationCheckInterval" property. By default,
	 * the file timestamp is checked on every access (which is surprisingly fast).
	 * Of course, this just applies when loading resources from the file system.
	 * <p>To enforce the use of SpringResourceLoader, i.e. to not resolve a path
	 * as file system resource in any case, turn off the "preferFileSystemAccess"
	 * flag. See the latter's javadoc for details.
	 * <p>
	 * 通过Spring资源位置设置Velocity资源加载程序路径以Velocity逗号分隔的路径样式接受多个位置<p>当通过String填充时,支持诸如"file："和"classpath："伪URL的标准
	 * URL,如ResourceLoader在ApplicationContext中运行时允许相对路径<p>将使用名称"file"定义默认Velocity资源加载器的路径如果指定的资源无法解析为{@code javaioFile}
	 * ,则将使用通用的SpringResourceLoader在名称"spring"下,没有修改检测<p>请注意,资源缓存将被启用在任何情况下使用文件资源加载程序,最后修改的时间戳将被检查访问以检测更改使用S
	 * pringResourceLoader,资源将被永久缓存(例如对于类路径资源)<p>要为文件指定修改检查间隔,请使用Velocity的标准"fileresourceloadermodificationC
	 * heckInterval"属性默认情况下,每个访问(即令人惊讶的是快速)当然,这只是在从文件系统加载资源时适用<p>要强制使用SpringResourceLoader,即在任何情况下不解析文件系统资源的
	 * 路径,请关闭"preferFileSystemAccess"标志javadoc的详细信息。
	 * 
	 * 
	 * @see #setResourceLoader
	 * @see #setVelocityProperties
	 * @see #setPreferFileSystemAccess
	 * @see SpringResourceLoader
	 * @see org.apache.velocity.runtime.resource.loader.FileResourceLoader
	 */
	public void setResourceLoaderPath(String resourceLoaderPath) {
		this.resourceLoaderPath = resourceLoaderPath;
	}

	/**
	 * Set the Spring ResourceLoader to use for loading Velocity template files.
	 * The default is DefaultResourceLoader. Will get overridden by the
	 * ApplicationContext if running in a context.
	 * <p>
	 * 设置Spring ResourceLoader用于加载Velocity模板文件默认值为DefaultResourceLoader如果在上下文中运行,将被ApplicationContext覆盖
	 * 
	 * 
	 * @see org.springframework.core.io.DefaultResourceLoader
	 * @see org.springframework.context.ApplicationContext
	 */
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	/**
	 * Return the Spring ResourceLoader to use for loading Velocity template files.
	 * <p>
	 *  返回Spring ResourceLoader用于加载Velocity模板文件
	 * 
	 */
	protected ResourceLoader getResourceLoader() {
		return this.resourceLoader;
	}

	/**
	 * Set whether to prefer file system access for template loading.
	 * File system access enables hot detection of template changes.
	 * <p>If this is enabled, VelocityEngineFactory will try to resolve the
	 * specified "resourceLoaderPath" as file system resource (which will work
	 * for expanded class path resources and ServletContext resources too).
	 * <p>Default is "true". Turn this off to always load via SpringResourceLoader
	 * (i.e. as stream, without hot detection of template changes), which might
	 * be necessary if some of your templates reside in an expanded classes
	 * directory while others reside in jar files.
	 * <p>
	 * 设置是否选择文件系统访问模板加载文件系统访问启用热检测模板更改<p>如果启用,VelocityEngineFactory将尝试将指定的"resourceLoaderPath"解析为文件系统资源(这将适用
	 * 于扩展的类路径资源和ServletContext资源)<p>默认值为"true"将其关闭,以始终通过SpringResourceLoader加载(即,作为流,没有热检测模板更改),如果某些模板驻留在扩展
	 * 的类目录中,这可能是必需的其他人驻留在jar文件中。
	 * 
	 * 
	 * @see #setResourceLoaderPath
	 */
	public void setPreferFileSystemAccess(boolean preferFileSystemAccess) {
		this.preferFileSystemAccess = preferFileSystemAccess;
	}

	/**
	 * Return whether to prefer file system access for template loading.
	 * <p>
	 *  返回是否喜欢文件系统访问模板加载
	 * 
	 */
	protected boolean isPreferFileSystemAccess() {
		return this.preferFileSystemAccess;
	}

	/**
	 * Set whether Velocity should log via Commons Logging, i.e. whether Velocity's
	 * log system should be set to {@link CommonsLogLogChute}. Default is "true".
	 * <p>
	 * 设置Velocity是否应该通过Commons Logging登录,即Velocity的日志系统是否应设置为{@link CommonsLogLogChute}默认为"true"
	 * 
	 */
	public void setOverrideLogging(boolean overrideLogging) {
		this.overrideLogging = overrideLogging;
	}


	/**
	 * Prepare the VelocityEngine instance and return it.
	 * <p>
	 *  准备VelocityEngine实例并返回
	 * 
	 * 
	 * @return the VelocityEngine instance
	 * @throws IOException if the config file wasn't found
	 * @throws VelocityException on Velocity initialization failure
	 */
	public VelocityEngine createVelocityEngine() throws IOException, VelocityException {
		VelocityEngine velocityEngine = newVelocityEngine();
		Map<String, Object> props = new HashMap<String, Object>();

		// Load config file if set.
		if (this.configLocation != null) {
			if (logger.isInfoEnabled()) {
				logger.info("Loading Velocity config from [" + this.configLocation + "]");
			}
			CollectionUtils.mergePropertiesIntoMap(PropertiesLoaderUtils.loadProperties(this.configLocation), props);
		}

		// Merge local properties if set.
		if (!this.velocityProperties.isEmpty()) {
			props.putAll(this.velocityProperties);
		}

		// Set a resource loader path, if required.
		if (this.resourceLoaderPath != null) {
			initVelocityResourceLoader(velocityEngine, this.resourceLoaderPath);
		}

		// Log via Commons Logging?
		if (this.overrideLogging) {
			velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM, new CommonsLogLogChute());
		}

		// Apply properties to VelocityEngine.
		for (Map.Entry<String, Object> entry : props.entrySet()) {
			velocityEngine.setProperty(entry.getKey(), entry.getValue());
		}

		postProcessVelocityEngine(velocityEngine);

		// Perform actual initialization.
		velocityEngine.init();

		return velocityEngine;
	}

	/**
	 * Return a new VelocityEngine. Subclasses can override this for
	 * custom initialization, or for using a mock object for testing.
	 * <p>Called by {@code createVelocityEngine()}.
	 * <p>
	 *  返回一个新的VelocityEngine子类可以覆盖此自定义初始化,或使用模拟对象进行测试<p>由{@code createVelocityEngine()}调用
	 * 
	 * 
	 * @return the VelocityEngine instance
	 * @throws IOException if a config file wasn't found
	 * @throws VelocityException on Velocity initialization failure
	 * @see #createVelocityEngine()
	 */
	protected VelocityEngine newVelocityEngine() throws IOException, VelocityException {
		return new VelocityEngine();
	}

	/**
	 * Initialize a Velocity resource loader for the given VelocityEngine:
	 * either a standard Velocity FileResourceLoader or a SpringResourceLoader.
	 * <p>Called by {@code createVelocityEngine()}.
	 * <p>
	 *  为给定的VelocityEngine初始化Velocity资源加载器：标准Velocity FileResourceLoader或SpringResourceLoader <p>由{@code createVelocityEngine()}
	 * 调用。
	 * 
	 * 
	 * @param velocityEngine the VelocityEngine to configure
	 * @param resourceLoaderPath the path to load Velocity resources from
	 * @see org.apache.velocity.runtime.resource.loader.FileResourceLoader
	 * @see SpringResourceLoader
	 * @see #initSpringResourceLoader
	 * @see #createVelocityEngine()
	 */
	protected void initVelocityResourceLoader(VelocityEngine velocityEngine, String resourceLoaderPath) {
		if (isPreferFileSystemAccess()) {
			// Try to load via the file system, fall back to SpringResourceLoader
			// (for hot detection of template changes, if possible).
			try {
				StringBuilder resolvedPath = new StringBuilder();
				String[] paths = StringUtils.commaDelimitedListToStringArray(resourceLoaderPath);
				for (int i = 0; i < paths.length; i++) {
					String path = paths[i];
					Resource resource = getResourceLoader().getResource(path);
					File file = resource.getFile();  // will fail if not resolvable in the file system
					if (logger.isDebugEnabled()) {
						logger.debug("Resource loader path [" + path + "] resolved to file [" + file.getAbsolutePath() + "]");
					}
					resolvedPath.append(file.getAbsolutePath());
					if (i < paths.length - 1) {
						resolvedPath.append(',');
					}
				}
				velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
				velocityEngine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_CACHE, "true");
				velocityEngine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, resolvedPath.toString());
			}
			catch (IOException ex) {
				if (logger.isDebugEnabled()) {
					logger.debug("Cannot resolve resource loader path [" + resourceLoaderPath +
							"] to [java.io.File]: using SpringResourceLoader", ex);
				}
				initSpringResourceLoader(velocityEngine, resourceLoaderPath);
			}
		}
		else {
			// Always load via SpringResourceLoader
			// (without hot detection of template changes).
			if (logger.isDebugEnabled()) {
				logger.debug("File system access not preferred: using SpringResourceLoader");
			}
			initSpringResourceLoader(velocityEngine, resourceLoaderPath);
		}
	}

	/**
	 * Initialize a SpringResourceLoader for the given VelocityEngine.
	 * <p>Called by {@code initVelocityResourceLoader}.
	 * <p>
	 *  为给定的VelocityEngine <p>初始化SpringResourceLoader由{@code initVelocityResourceLoader}调用
	 * 
	 * 
	 * @param velocityEngine the VelocityEngine to configure
	 * @param resourceLoaderPath the path to load Velocity resources from
	 * @see SpringResourceLoader
	 * @see #initVelocityResourceLoader
	 */
	protected void initSpringResourceLoader(VelocityEngine velocityEngine, String resourceLoaderPath) {
		velocityEngine.setProperty(
				RuntimeConstants.RESOURCE_LOADER, SpringResourceLoader.NAME);
		velocityEngine.setProperty(
				SpringResourceLoader.SPRING_RESOURCE_LOADER_CLASS, SpringResourceLoader.class.getName());
		velocityEngine.setProperty(
				SpringResourceLoader.SPRING_RESOURCE_LOADER_CACHE, "true");
		velocityEngine.setApplicationAttribute(
				SpringResourceLoader.SPRING_RESOURCE_LOADER, getResourceLoader());
		velocityEngine.setApplicationAttribute(
				SpringResourceLoader.SPRING_RESOURCE_LOADER_PATH, resourceLoaderPath);
	}

	/**
	 * To be implemented by subclasses that want to perform custom
	 * post-processing of the VelocityEngine after this FactoryBean
	 * performed its default configuration (but before VelocityEngine.init).
	 * <p>Called by {@code createVelocityEngine()}.
	 * <p>
	 * 在由FactoryBean执行默认配置(但在VelocityEngineinit之前)执行定制后处理VelocityEngine的子类时,由{@code createVelocityEngine()}调
	 * 用)。
	 * 
	 * @param velocityEngine the current VelocityEngine
	 * @throws IOException if a config file wasn't found
	 * @throws VelocityException on Velocity initialization failure
	 * @see #createVelocityEngine()
	 * @see org.apache.velocity.app.VelocityEngine#init
	 */
	protected void postProcessVelocityEngine(VelocityEngine velocityEngine)
			throws IOException, VelocityException {
	}

}
