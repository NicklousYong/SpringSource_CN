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

package org.springframework.web.context.support;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.AbstractRefreshableConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.ui.context.Theme;
import org.springframework.ui.context.ThemeSource;
import org.springframework.ui.context.support.UiApplicationContextUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ConfigurableWebEnvironment;
import org.springframework.web.context.ServletConfigAware;
import org.springframework.web.context.ServletContextAware;

/**
 * {@link org.springframework.context.support.AbstractRefreshableApplicationContext}
 * subclass which implements the
 * {@link org.springframework.web.context.ConfigurableWebApplicationContext}
 * interface for web environments. Provides a "configLocations" property,
 * to be populated through the ConfigurableWebApplicationContext interface
 * on web application startup.
 *
 * <p>This class is as easy to subclass as AbstractRefreshableApplicationContext:
 * All you need to implements is the {@link #loadBeanDefinitions} method;
 * see the superclass javadoc for details. Note that implementations are supposed
 * to load bean definitions from the files specified by the locations returned
 * by the {@link #getConfigLocations} method.
 *
 * <p>Interprets resource paths as servlet context resources, i.e. as paths beneath
 * the web application root. Absolute paths, e.g. for files outside the web app root,
 * can be accessed via "file:" URLs, as implemented by
 * {@link org.springframework.core.io.DefaultResourceLoader}.
 *
 * <p>In addition to the special beans detected by
 * {@link org.springframework.context.support.AbstractApplicationContext},
 * this class detects a bean of type {@link org.springframework.ui.context.ThemeSource}
 * in the context, under the special bean name "themeSource".
 *
 * <p><b>This is the web context to be subclassed for a different bean definition format.</b>
 * Such a context implementation can be specified as "contextClass" context-param
 * for {@link org.springframework.web.context.ContextLoader} or as "contextClass"
 * init-param for {@link org.springframework.web.servlet.FrameworkServlet},
 * replacing the default {@link XmlWebApplicationContext}. It will then automatically
 * receive the "contextConfigLocation" context-param or init-param, respectively.
 *
 * <p>Note that WebApplicationContext implementations are generally supposed
 * to configure themselves based on the configuration received through the
 * {@link ConfigurableWebApplicationContext} interface. In contrast, a standalone
 * application context might allow for configuration in custom startup code
 * (for example, {@link org.springframework.context.support.GenericApplicationContext}).
 *
 * <p>
 * 实现Web环境的{@link orgspringframeworkwebcontextConfigurableWebApplicationContext}接口的{@link orgspringframeworkcontextsupportAbstractRefreshableApplicationContext}
 * 子类提供了一个"configLocations"属性,通过Web应用程序启动时的ConfigurableWebApplicationContext接口进行填充。
 * 
 *  <p>这个类与AbstractRefreshableApplicationContext一样容易进行子类化：所有你需要实现的是{@link #loadBeanDefinitions}方法;请参阅超类j
 * avadoc了解详细信息请注意,实现应该从由{@link #getConfigLocations}方法返回的位置指定的文件中加载bean定义。
 * 
 * 将资源路径解释为servlet上下文资源,即作为Web应用程序根目录下的路径绝对路径,例如对于Web应用程序根目录之外的文件,可以通过"file："URL访问,由{@link orgspringframeworkcoreioDefaultResourceLoader}
 * 实现。
 * 
 *  <p>除了由{@link orgspringframeworkcontextsupportAbstractApplicationContext}检测到的特殊bean之外,此类还检测到上下文中的类型为{@link orgspringframeworkuicontextThemeSource}
 * 的bean,特殊bean名称为"themeSource"。
 * 
 * <p> <b>这是为不同的bean定义格式进行子类化的Web上下文</b>这样的上下文实现可以被指定为{@link orgspringframeworkwebcontextContextLoader}或
 * "contextClass"init的"contextClass"context-param -param for {@link orgspringframeworkwebservletFrameworkServlet}
 * ,替换默认的{@link XmlWebApplicationContext},然后它将分别自动接收"contextConfigLocation"上下文参数或init-param。
 * 
 * 注意,WebApplicationContext实现通常基于通过{@link ConfigurableWebApplicationContext}接口接收的配置来配置自己。
 * 相反,独立的应用程序上下文可能允许在自定义启动代码中进行配置(例如,{@link orgspringframeworkcontextsupportGenericApplicationContext} )
 * 。
 * 注意,WebApplicationContext实现通常基于通过{@link ConfigurableWebApplicationContext}接口接收的配置来配置自己。
 * 
 * @author Juergen Hoeller
 * @since 1.1.3
 * @see #loadBeanDefinitions
 * @see org.springframework.web.context.ConfigurableWebApplicationContext#setConfigLocations
 * @see org.springframework.ui.context.ThemeSource
 * @see XmlWebApplicationContext
 */
public abstract class AbstractRefreshableWebApplicationContext extends AbstractRefreshableConfigApplicationContext
		implements ConfigurableWebApplicationContext, ThemeSource {

	/** Servlet context that this context runs in */
	private ServletContext servletContext;

	/** Servlet config that this context runs in, if any */
	private ServletConfig servletConfig;

	/** Namespace of this context, or {@code null} if root */
	private String namespace;

	/** the ThemeSource for this ApplicationContext */
	private ThemeSource themeSource;


	public AbstractRefreshableWebApplicationContext() {
		setDisplayName("Root WebApplicationContext");
	}


	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@Override
	public ServletContext getServletContext() {
		return this.servletContext;
	}

	@Override
	public void setServletConfig(ServletConfig servletConfig) {
		this.servletConfig = servletConfig;
		if (servletConfig != null && this.servletContext == null) {
			setServletContext(servletConfig.getServletContext());
		}
	}

	@Override
	public ServletConfig getServletConfig() {
		return this.servletConfig;
	}

	@Override
	public void setNamespace(String namespace) {
		this.namespace = namespace;
		if (namespace != null) {
			setDisplayName("WebApplicationContext for namespace '" + namespace + "'");
		}
	}

	@Override
	public String getNamespace() {
		return this.namespace;
	}

	@Override
	public String[] getConfigLocations() {
		return super.getConfigLocations();
	}

	@Override
	public String getApplicationName() {
		return (this.servletContext != null ? this.servletContext.getContextPath() : "");
	}

	/**
	 * Create and return a new {@link StandardServletEnvironment}. Subclasses may override
	 * in order to configure the environment or specialize the environment type returned.
	 * <p>
	 * 
	 */
	@Override
	protected ConfigurableEnvironment createEnvironment() {
		return new StandardServletEnvironment();
	}

	/**
	 * Register request/session scopes, a {@link ServletContextAwareProcessor}, etc.
	 * <p>
	 *  创建并返回一个新的{@link StandardServletEnvironment}子类可以覆盖以配置环境或专门化返回的环境类型
	 * 
	 */
	@Override
	protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
		beanFactory.addBeanPostProcessor(new ServletContextAwareProcessor(this.servletContext, this.servletConfig));
		beanFactory.ignoreDependencyInterface(ServletContextAware.class);
		beanFactory.ignoreDependencyInterface(ServletConfigAware.class);

		WebApplicationContextUtils.registerWebApplicationScopes(beanFactory, this.servletContext);
		WebApplicationContextUtils.registerEnvironmentBeans(beanFactory, this.servletContext, this.servletConfig);
	}

	/**
	 * This implementation supports file paths beneath the root of the ServletContext.
	 * <p>
	 *  注册请求/会话范围,{@link ServletContextAwareProcessor}等
	 * 
	 * 
	 * @see ServletContextResource
	 */
	@Override
	protected Resource getResourceByPath(String path) {
		return new ServletContextResource(this.servletContext, path);
	}

	/**
	 * This implementation supports pattern matching in unexpanded WARs too.
	 * <p>
	 *  此实现支持ServletContext根目录下的文件路径
	 * 
	 * 
	 * @see ServletContextResourcePatternResolver
	 */
	@Override
	protected ResourcePatternResolver getResourcePatternResolver() {
		return new ServletContextResourcePatternResolver(this);
	}

	/**
	 * Initialize the theme capability.
	 * <p>
	 *  此实现也支持未展开的WAR中的模式匹配
	 * 
	 */
	@Override
	protected void onRefresh() {
		this.themeSource = UiApplicationContextUtils.initThemeSource(this);
	}

	/**
	 * {@inheritDoc}
	 * <p>Replace {@code Servlet}-related property sources.
	 * <p>
	 * 初始化主题功能
	 * 
	 */
	@Override
	protected void initPropertySources() {
		ConfigurableEnvironment env = getEnvironment();
		if (env instanceof ConfigurableWebEnvironment) {
			((ConfigurableWebEnvironment) env).initPropertySources(this.servletContext, this.servletConfig);
		}
	}

	@Override
	public Theme getTheme(String themeName) {
		return this.themeSource.getTheme(themeName);
	}

}