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

package org.springframework.ui.freemarker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import freemarker.template.TemplateException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.CollectionUtils;

/**
 * Factory that configures a FreeMarker Configuration. Can be used standalone, but
 * typically you will either use FreeMarkerConfigurationFactoryBean for preparing a
 * Configuration as bean reference, or FreeMarkerConfigurer for web views.
 *
 * <p>The optional "configLocation" property sets the location of a FreeMarker
 * properties file, within the current application. FreeMarker properties can be
 * overridden via "freemarkerSettings". All of these properties will be set by
 * calling FreeMarker's {@code Configuration.setSettings()} method and are
 * subject to constraints set by FreeMarker.
 *
 * <p>The "freemarkerVariables" property can be used to specify a Map of
 * shared variables that will be applied to the Configuration via the
 * {@code setAllSharedVariables()} method. Like {@code setSettings()},
 * these entries are subject to FreeMarker constraints.
 *
 * <p>The simplest way to use this class is to specify a "templateLoaderPath";
 * FreeMarker does not need any further configuration then.
 *
 * <p>Note: Spring's FreeMarker support requires FreeMarker 2.3 or higher.
 *
 * <p>
 * 配置FreeMarker配置的工厂可以独立使用,但通常您将使用FreeMarkerConfigurationFactoryBean来准备一个配置为bean引用,或者FreeMarkerConfigure
 * r用于Web视图。
 * 
 *  <p>可选的"configLocation"属性设置FreeMarker属性文件的位置,在当前应用程序中FreeMarker的属性可以通过"freemarkerSettings"进行覆盖。
 * 所有这些属性都将通过调用FreeMarker的{@code ConfigurationsetSettings()}方法和受到FreeMarker设置的约束。
 * 
 * <p>"freemarkerVariables"属性可用于指定将通过{@code setAllSharedVariables()}方法应用于配置的共享变量映射像{@code setSettings()}
 * ),这些条目受FreeMarker限制。
 * 
 *  <p>使用这个类的最简单的方法是指定一个"templateLoaderPath"; FreeMarker不需要任何进一步的配置
 * 
 *  注意：Spring的FreeMarker支持需要FreeMarker 23或更高版本
 * 
 * 
 * @author Darren Davison
 * @author Juergen Hoeller
 * @since 03.03.2004
 * @see #setConfigLocation
 * @see #setFreemarkerSettings
 * @see #setFreemarkerVariables
 * @see #setTemplateLoaderPath
 * @see #createConfiguration
 * @see FreeMarkerConfigurationFactoryBean
 * @see org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer
 * @see freemarker.template.Configuration
 */
public class FreeMarkerConfigurationFactory {

	protected final Log logger = LogFactory.getLog(getClass());

	private Resource configLocation;

	private Properties freemarkerSettings;

	private Map<String, Object> freemarkerVariables;

	private String defaultEncoding;

	private final List<TemplateLoader> templateLoaders = new ArrayList<TemplateLoader>();

	private List<TemplateLoader> preTemplateLoaders;

	private List<TemplateLoader> postTemplateLoaders;

	private String[] templateLoaderPaths;

	private ResourceLoader resourceLoader = new DefaultResourceLoader();

	private boolean preferFileSystemAccess = true;


	/**
	 * Set the location of the FreeMarker config file.
	 * Alternatively, you can specify all setting locally.
	 * <p>
	 *  设置FreeMarker配置文件的位置或者,您可以在本地指定所有设置
	 * 
	 * 
	 * @see #setFreemarkerSettings
	 * @see #setTemplateLoaderPath
	 */
	public void setConfigLocation(Resource resource) {
		configLocation = resource;
	}

	/**
	 * Set properties that contain well-known FreeMarker keys which will be
	 * passed to FreeMarker's {@code Configuration.setSettings} method.
	 * <p>
	 *  设置包含已知FreeMarker密钥的属性,这些密钥将被传递给FreeMarker的{@code ConfigurationsetSettings}方法
	 * 
	 * 
	 * @see freemarker.template.Configuration#setSettings
	 */
	public void setFreemarkerSettings(Properties settings) {
		this.freemarkerSettings = settings;
	}

	/**
	 * Set a Map that contains well-known FreeMarker objects which will be passed
	 * to FreeMarker's {@code Configuration.setAllSharedVariables()} method.
	 * <p>
	 * 设置一个包含着名的FreeMarker对象的Map,该对象将被传递给FreeMarker的{@code ConfigurationsetAllSharedVariables()}方法
	 * 
	 * 
	 * @see freemarker.template.Configuration#setAllSharedVariables
	 */
	public void setFreemarkerVariables(Map<String, Object> variables) {
		this.freemarkerVariables = variables;
	}

	/**
	 * Set the default encoding for the FreeMarker configuration.
	 * If not specified, FreeMarker will use the platform file encoding.
	 * <p>Used for template rendering unless there is an explicit encoding specified
	 * for the rendering process (for example, on Spring's FreeMarkerView).
	 * <p>
	 *  设置FreeMarker配置的默认编码如果未指定,FreeMarker将使用平台文件编码<p>用于模板渲染,除非为渲染过程指定了显式编码(例如,在Spring的FreeMarkerView上)
	 * 
	 * 
	 * @see freemarker.template.Configuration#setDefaultEncoding
	 * @see org.springframework.web.servlet.view.freemarker.FreeMarkerView#setEncoding
	 */
	public void setDefaultEncoding(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
	}

	/**
	 * Set a List of {@code TemplateLoader}s that will be used to search
	 * for templates. For example, one or more custom loaders such as database
	 * loaders could be configured and injected here.
	 * <p>The {@link TemplateLoader TemplateLoaders} specified here will be
	 * registered <i>before</i> the default template loaders that this factory
	 * registers (such as loaders for specified "templateLoaderPaths" or any
	 * loaders registered in {@link #postProcessTemplateLoaders}).
	 * <p>
	 * 设置将用于搜索模板的{@code TemplateLoader}列表例如,可以在此处配置并注入一个或多个自定义加载器(如数据库加载器)。
	 * 此处指定的{@link TemplateLoader TemplateLoaders}将为在</i>此工厂注册的默认模板加载程序之前注册(例如,指定的"templateLoaderPath"的装载程序或
	 * 在{@link #postProcessTemplateLoaders}中注册的任何装载程序)。
	 * 设置将用于搜索模板的{@code TemplateLoader}列表例如,可以在此处配置并注入一个或多个自定义加载器(如数据库加载器)。
	 * 
	 * 
	 * @see #setTemplateLoaderPaths
	 * @see #postProcessTemplateLoaders
	 */
	public void setPreTemplateLoaders(TemplateLoader... preTemplateLoaders) {
		this.preTemplateLoaders = Arrays.asList(preTemplateLoaders);
	}

	/**
	 * Set a List of {@code TemplateLoader}s that will be used to search
	 * for templates. For example, one or more custom loaders such as database
	 * loaders can be configured.
	 * <p>The {@link TemplateLoader TemplateLoaders} specified here will be
	 * registered <i>after</i> the default template loaders that this factory
	 * registers (such as loaders for specified "templateLoaderPaths" or any
	 * loaders registered in {@link #postProcessTemplateLoaders}).
	 * <p>
	 * 设置将用于搜索模板的{@code TemplateLoader}的列表例如,可以配置一个或多个自定义加载器(如数据库加载器)<p>此处指定的{@link TemplateLoader TemplateLoaders}
	 * 将被注册<i >此后,此工厂注册的默认模板装载程序(例如,指定的"templateLoaderPath"的装载程序或在{@link #postProcessTemplateLoaders}中注册的任何装
	 * 载程序)。
	 * 
	 * 
	 * @see #setTemplateLoaderPaths
	 * @see #postProcessTemplateLoaders
	 */
	public void setPostTemplateLoaders(TemplateLoader... postTemplateLoaders) {
		this.postTemplateLoaders = Arrays.asList(postTemplateLoaders);
	}

	/**
	 * Set the Freemarker template loader path via a Spring resource location.
	 * See the "templateLoaderPaths" property for details on path handling.
	 * <p>
	 *  通过Spring资源位置设置Freemarker模板加载程序路径有关路径处理的详细信息,请参阅"templateLoaderPaths"属性
	 * 
	 * 
	 * @see #setTemplateLoaderPaths
	 */
	public void setTemplateLoaderPath(String templateLoaderPath) {
		this.templateLoaderPaths = new String[] {templateLoaderPath};
	}

	/**
	 * Set multiple Freemarker template loader paths via Spring resource locations.
	 * <p>When populated via a String, standard URLs like "file:" and "classpath:"
	 * pseudo URLs are supported, as understood by ResourceEditor. Allows for
	 * relative paths when running in an ApplicationContext.
	 * <p>Will define a path for the default FreeMarker template loader.
	 * If a specified resource cannot be resolved to a {@code java.io.File},
	 * a generic SpringTemplateLoader will be used, without modification detection.
	 * <p>To enforce the use of SpringTemplateLoader, i.e. to not resolve a path
	 * as file system resource in any case, turn off the "preferFileSystemAccess"
	 * flag. See the latter's javadoc for details.
	 * <p>If you wish to specify your own list of TemplateLoaders, do not set this
	 * property and instead use {@code setTemplateLoaders(List templateLoaders)}
	 * <p>
	 * 通过Spring资源位置设置多个Freemarker模板加载程序路径<p>当通过String填充时,支持标准URL(如"file："和"classpath：")伪URL,如ResourceEditor所
	 * 理解的。
	 * 允许在ApplicationContext < p>将定义默认FreeMarker模板加载器的路径如果指定的资源无法解析为{@code javaioFile},则将使用通用的SpringTemplate
	 * Loader,而不进行修改检测<p>要强制使用SpringTemplateLoader,即不要解决路径作为文件系统资源在任何情况下,关闭"preferFileSystemAccess"标志看到后者的ja
	 * vadoc的详细信息<p>如果您想指定自己的TemplateLoaders列表,请不要设置此属性,而应使用{@code setTemplateLoaders(List templateLoaders)}
	 * }。
	 * 
	 * 
	 * @see org.springframework.core.io.ResourceEditor
	 * @see org.springframework.context.ApplicationContext#getResource
	 * @see freemarker.template.Configuration#setDirectoryForTemplateLoading
	 * @see SpringTemplateLoader
	 */
	public void setTemplateLoaderPaths(String... templateLoaderPaths) {
		this.templateLoaderPaths = templateLoaderPaths;
	}

	/**
	 * Set the Spring ResourceLoader to use for loading FreeMarker template files.
	 * The default is DefaultResourceLoader. Will get overridden by the
	 * ApplicationContext if running in a context.
	 * <p>
	 * 设置Spring ResourceLoader用于加载FreeMarker模板文件默认值为DefaultResourceLoader如果在上下文中运行,将被ApplicationContext覆盖
	 * 
	 * 
	 * @see org.springframework.core.io.DefaultResourceLoader
	 */
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	/**
	 * Return the Spring ResourceLoader to use for loading FreeMarker template files.
	 * <p>
	 *  返回Spring ResourceLoader用于加载FreeMarker模板文件
	 * 
	 */
	protected ResourceLoader getResourceLoader() {
		return this.resourceLoader;
	}

	/**
	 * Set whether to prefer file system access for template loading.
	 * File system access enables hot detection of template changes.
	 * <p>If this is enabled, FreeMarkerConfigurationFactory will try to resolve
	 * the specified "templateLoaderPath" as file system resource (which will work
	 * for expanded class path resources and ServletContext resources too).
	 * <p>Default is "true". Turn this off to always load via SpringTemplateLoader
	 * (i.e. as stream, without hot detection of template changes), which might
	 * be necessary if some of your templates reside in an expanded classes
	 * directory while others reside in jar files.
	 * <p>
	 * 设置是否选择文件系统访问模板加载文件系统访问启用模板更改的热检测<p>如果启用此功能,FreeMarkerConfigurationFactory将尝试将指定的"templateLoaderPath"解
	 * 析为文件系统资源(这将适用于扩展的类路径资源和ServletContext资源)<p>默认值为"true"将其关闭,以始终通过SpringTemplateLoader加载(即,作为流,不需要模板更改的热
	 * 检测),如果某些模板驻留在扩展的类目录中,这可能是必需的其他人驻留在jar文件中。
	 * 
	 * 
	 * @see #setTemplateLoaderPath
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
	 * Prepare the FreeMarker Configuration and return it.
	 * <p>
	 *  准备FreeMarker配置并返回
	 * 
	 * 
	 * @return the FreeMarker Configuration object
	 * @throws IOException if the config file wasn't found
	 * @throws TemplateException on FreeMarker initialization failure
	 */
	public Configuration createConfiguration() throws IOException, TemplateException {
		Configuration config = newConfiguration();
		Properties props = new Properties();

		// Load config file if specified.
		if (this.configLocation != null) {
			if (logger.isInfoEnabled()) {
				logger.info("Loading FreeMarker configuration from " + this.configLocation);
			}
			PropertiesLoaderUtils.fillProperties(props, this.configLocation);
		}

		// Merge local properties if specified.
		if (this.freemarkerSettings != null) {
			props.putAll(this.freemarkerSettings);
		}

		// FreeMarker will only accept known keys in its setSettings and
		// setAllSharedVariables methods.
		if (!props.isEmpty()) {
			config.setSettings(props);
		}

		if (!CollectionUtils.isEmpty(this.freemarkerVariables)) {
			config.setAllSharedVariables(new SimpleHash(this.freemarkerVariables, config.getObjectWrapper()));
		}

		if (this.defaultEncoding != null) {
			config.setDefaultEncoding(this.defaultEncoding);
		}

		List<TemplateLoader> templateLoaders = new LinkedList<TemplateLoader>(this.templateLoaders);

		// Register template loaders that are supposed to kick in early.
		if (this.preTemplateLoaders != null) {
			templateLoaders.addAll(this.preTemplateLoaders);
		}

		// Register default template loaders.
		if (this.templateLoaderPaths != null) {
			for (String path : this.templateLoaderPaths) {
				templateLoaders.add(getTemplateLoaderForPath(path));
			}
		}
		postProcessTemplateLoaders(templateLoaders);

		// Register template loaders that are supposed to kick in late.
		if (this.postTemplateLoaders != null) {
			templateLoaders.addAll(this.postTemplateLoaders);
		}

		TemplateLoader loader = getAggregateTemplateLoader(templateLoaders);
		if (loader != null) {
			config.setTemplateLoader(loader);
		}

		postProcessConfiguration(config);
		return config;
	}

	/**
	 * Return a new Configuration object. Subclasses can override this for custom
	 * initialization (e.g. specifying a FreeMarker compatibility level which is a
	 * new feature in FreeMarker 2.3.21), or for using a mock object for testing.
	 * <p>Called by {@code createConfiguration()}.
	 * <p>
	 * 返回一个新的配置对象子类可以覆盖此自定义初始化(例如,指定FreeMarker兼容级别,这是FreeMarker 2321中的新功能),或者使用模拟对象进行测试<p>由{@code createConfiguration()}
	 * 调用。
	 * 
	 * 
	 * @return the Configuration object
	 * @throws IOException if a config file wasn't found
	 * @throws TemplateException on FreeMarker initialization failure
	 * @see #createConfiguration()
	 */
	protected Configuration newConfiguration() throws IOException, TemplateException {
		return new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
	}

	/**
	 * Determine a FreeMarker TemplateLoader for the given path.
	 * <p>Default implementation creates either a FileTemplateLoader or
	 * a SpringTemplateLoader.
	 * <p>
	 *  确定给定路径的FreeMarker TemplateLoader <p>默认实现创建FileTemplateLoader或SpringTemplateLoader
	 * 
	 * 
	 * @param templateLoaderPath the path to load templates from
	 * @return an appropriate TemplateLoader
	 * @see freemarker.cache.FileTemplateLoader
	 * @see SpringTemplateLoader
	 */
	protected TemplateLoader getTemplateLoaderForPath(String templateLoaderPath) {
		if (isPreferFileSystemAccess()) {
			// Try to load via the file system, fall back to SpringTemplateLoader
			// (for hot detection of template changes, if possible).
			try {
				Resource path = getResourceLoader().getResource(templateLoaderPath);
				File file = path.getFile();  // will fail if not resolvable in the file system
				if (logger.isDebugEnabled()) {
					logger.debug(
							"Template loader path [" + path + "] resolved to file path [" + file.getAbsolutePath() + "]");
				}
				return new FileTemplateLoader(file);
			}
			catch (IOException ex) {
				if (logger.isDebugEnabled()) {
					logger.debug("Cannot resolve template loader path [" + templateLoaderPath +
							"] to [java.io.File]: using SpringTemplateLoader as fallback", ex);
				}
				return new SpringTemplateLoader(getResourceLoader(), templateLoaderPath);
			}
		}
		else {
			// Always load via SpringTemplateLoader (without hot detection of template changes).
			logger.debug("File system access not preferred: using SpringTemplateLoader");
			return new SpringTemplateLoader(getResourceLoader(), templateLoaderPath);
		}
	}

	/**
	 * To be overridden by subclasses that want to register custom
	 * TemplateLoader instances after this factory created its default
	 * template loaders.
	 * <p>Called by {@code createConfiguration()}. Note that specified
	 * "postTemplateLoaders" will be registered <i>after</i> any loaders
	 * registered by this callback; as a consequence, they are <i>not</i>
	 * included in the given List.
	 * <p>
	 *  在这个工厂创建了它的默认模板加载器之后,要被要注册自定义TemplateLoader实例的子类覆盖<p>调用{@code createConfiguration()}注意,指定的"postTempla
	 * teLoaders"将在</i>之后注册<i>由此回调注册的装载机;因此,它们不是包含在给定列表中的<i>。
	 * 
	 * 
	 * @param templateLoaders the current List of TemplateLoader instances,
	 * to be modified by a subclass
	 * @see #createConfiguration()
	 * @see #setPostTemplateLoaders
	 */
	protected void postProcessTemplateLoaders(List<TemplateLoader> templateLoaders) {
	}

	/**
	 * Return a TemplateLoader based on the given TemplateLoader list.
	 * If more than one TemplateLoader has been registered, a FreeMarker
	 * MultiTemplateLoader needs to be created.
	 * <p>
	 * 根据给定的TemplateLoader列表返回TemplateLoader如果已经注册了多个TemplateLoader,则需要创建一个FreeMarker MultiTemplateLoader
	 * 
	 * 
	 * @param templateLoaders the final List of TemplateLoader instances
	 * @return the aggregate TemplateLoader
	 */
	protected TemplateLoader getAggregateTemplateLoader(List<TemplateLoader> templateLoaders) {
		int loaderCount = templateLoaders.size();
		switch (loaderCount) {
			case 0:
				logger.info("No FreeMarker TemplateLoaders specified");
				return null;
			case 1:
				return templateLoaders.get(0);
			default:
				TemplateLoader[] loaders = templateLoaders.toArray(new TemplateLoader[loaderCount]);
				return new MultiTemplateLoader(loaders);
		}
	}

	/**
	 * To be overridden by subclasses that want to perform custom
	 * post-processing of the Configuration object after this factory
	 * performed its default initialization.
	 * <p>Called by {@code createConfiguration()}.
	 * <p>
	 *  在这个工厂执行默认初始化之后,要由要执行配置对象的定制后处理的子类覆盖{pcode {@code createConfiguration()}调用)
	 * 
	 * @param config the current Configuration object
	 * @throws IOException if a config file wasn't found
	 * @throws TemplateException on FreeMarker initialization failure
	 * @see #createConfiguration()
	 */
	protected void postProcessConfiguration(Configuration config) throws IOException, TemplateException {
	}

}
