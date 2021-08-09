/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2012 the original author or authors.
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

package org.springframework.web.servlet.view.freemarker;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletContext;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.ext.jsp.TaglibFactory;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;
import org.springframework.web.context.ServletContextAware;

/**
 * JavaBean to configure FreeMarker for web usage, via the "configLocation"
 * and/or "freemarkerSettings" and/or "templateLoaderPath" properties.
 * The simplest way to use this class is to specify just a "templateLoaderPath";
 * you do not need any further configuration then.
 *
 * <pre class="code">
 * &lt;bean id="freemarkerConfig" class="org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer"&gt;
 *   &lt;property name="templateLoaderPath"&gt;&lt;value&gt;/WEB-INF/freemarker/&lt;/value>&lt;/property&gt;
 * &lt;/bean&gt;</pre>
 *
 * This bean must be included in the application context of any application
 * using Spring's FreeMarkerView for web MVC. It exists purely to configure FreeMarker.
 * It is not meant to be referenced by application components but just internally
 * by FreeMarkerView. Implements FreeMarkerConfig to be found by FreeMarkerView without
 * depending on the bean name the configurer. Each DispatcherServlet can define its
 * own FreeMarkerConfigurer if desired.
 *
 * <p>Note that you can also refer to a preconfigured FreeMarker Configuration
 * instance, for example one set up by FreeMarkerConfigurationFactoryBean, via
 * the "configuration" property. This allows to share a FreeMarker Configuration
 * for web and email usage, for example.
 *
 * <p>This configurer registers a template loader for this package, allowing to
 * reference the "spring.ftl" macro library (contained in this package and thus
 * in spring.jar) like this:
 *
 * <pre class="code">
 * &lt;#import "/spring.ftl" as spring/&gt;
 * &lt;@spring.bind "person.age"/&gt;
 * age is ${spring.status.value}</pre>
 *
 * Note: Spring's FreeMarker support requires FreeMarker 2.3 or higher.
 *
 * <p>
 * JavaBean通过"configLocation"和/或"freemarkerSettings"和/或"templateLoaderPath"属性来配置FreeMarker以进行Web使用。
 * 使用此类的最简单的方法是仅指定一个"templateLoaderPath";你不需要任何进一步的配置。
 * 
 * <pre class="code">
 *  &lt; bean id ="freemarkerConfig"class ="orgspringframeworkwebservletviewfreemarkerFreeMarkerConfigur
 * er"&gt; &lt; property name ="templateLoaderPath"&gt;&lt; value&gt; / WEB-INF / freemarker /&lt; / val
 * ue>&lt; / property&gt; &LT; /豆腐&GT; </PRE>。
 * 
 * 这个bean必须包含在任何应用程序的应用程序上下文中,使用Spring的FreeMarkerView进行web MVC它纯属于配置FreeMarker它不是由应用程序组件引用,而是内部由FreeMark
 * erView实现FreeMarkerConfig可以由FreeMarkerView找到,而不依赖于bean命名configurer如果需要,每个DispatcherServlet都可以定义自己的Free
 * MarkerConfigurer。
 * 
 *  <p>请注意,您还可以通过"配置"属性引用预配置的FreeMarker配置实例,例如由FreeMarkerConfigurationFactoryBean设置的一个实例。
 * 这允许为Web和电子邮件使用共享FreeMarker配置,例如。
 * 
 * <p>此配置程序为此程序包注册模板加载程序,允许引用"springftl"宏库(包含在此程序包中,因此在springjar中),如下所示：
 * 
 * <pre class="code">
 *  &lt; #import"/ springftl"作为弹簧/&gt; &lt; @springbind"personage"/&gt;年龄是$ {springstatusvalue} </pre>
 * 
 *  注意：Spring的FreeMarker支持需要FreeMarker 23或更高版本
 * 
 * @author Darren Davison
 * @author Rob Harrop
 * @since 03.03.2004
 * @see #setConfigLocation
 * @see #setFreemarkerSettings
 * @see #setTemplateLoaderPath
 * @see #setConfiguration
 * @see org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean
 * @see FreeMarkerView
 */
public class FreeMarkerConfigurer extends FreeMarkerConfigurationFactory
		implements FreeMarkerConfig, InitializingBean, ResourceLoaderAware, ServletContextAware {

	private Configuration configuration;

	private TaglibFactory taglibFactory;


	/**
	 * Set a preconfigured Configuration to use for the FreeMarker web config, e.g. a
	 * shared one for web and email usage, set up via FreeMarkerConfigurationFactoryBean.
	 * If this is not set, FreeMarkerConfigurationFactory's properties (inherited by
	 * this class) have to be specified.
	 * <p>
	 * 
	 * 
	 * @see org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean
	 */
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Initialize the {@link TaglibFactory} for the given ServletContext.
	 * <p>
	 *  设置用于FreeMarker Web配置的预配置配置,例如通过FreeMarkerConfigurationFactoryBean设置的用于Web和电子邮件使用的共享配置如果未设置,则必须指定Free
	 * MarkerConfigurationFactory的属性(由此类继承)。
	 * 
	 */
	@Override
	public void setServletContext(ServletContext servletContext) {
		this.taglibFactory = new TaglibFactory(servletContext);
	}


	/**
	 * Initialize FreeMarkerConfigurationFactory's Configuration
	 * if not overridden by a preconfigured FreeMarker Configuation.
	 * <p>Sets up a ClassTemplateLoader to use for loading Spring macros.
	 * <p>
	 *  为给定的ServletContext初始化{@link TaglibFactory}
	 * 
	 * 
	 * @see #createConfiguration
	 * @see #setConfiguration
	 */
	@Override
	public void afterPropertiesSet() throws IOException, TemplateException {
		if (this.configuration == null) {
			this.configuration = createConfiguration();
		}
	}

	/**
	 * This implementation registers an additional ClassTemplateLoader
	 * for the Spring-provided macros, added to the end of the list.
	 * <p>
	 * 初始化FreeMarkerConfigurationFactory的配置,如果没有被预配置的FreeMarker配置覆盖<p>设置一个ClassTemplateLoader用于加载Spring宏
	 * 
	 */
	@Override
	protected void postProcessTemplateLoaders(List<TemplateLoader> templateLoaders) {
		templateLoaders.add(new ClassTemplateLoader(FreeMarkerConfigurer.class, ""));
		logger.info("ClassTemplateLoader for Spring macros added to FreeMarker configuration");
	}


	/**
	 * Return the Configuration object wrapped by this bean.
	 * <p>
	 *  此实现为Spring提供的宏注册了一个附加的ClassTemplateLoader,并添加到列表的末尾
	 * 
	 */
	@Override
	public Configuration getConfiguration() {
		return this.configuration;
	}

	/**
	 * Return the TaglibFactory object wrapped by this bean.
	 * <p>
	 *  返回此bean包装的配置对象
	 * 
	 */
	@Override
	public TaglibFactory getTaglibFactory() {
		return this.taglibFactory;
	}

}
