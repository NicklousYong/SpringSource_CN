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

package org.springframework.web.context.support;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.ui.context.Theme;
import org.springframework.ui.context.ThemeSource;
import org.springframework.ui.context.support.UiApplicationContextUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ServletConfigAware;
import org.springframework.web.context.ServletContextAware;

/**
 * Static {@link org.springframework.web.context.WebApplicationContext}
 * implementation for testing. Not intended for use in production applications.
 *
 * <p>Implements the {@link org.springframework.web.context.ConfigurableWebApplicationContext}
 * interface to allow for direct replacement of an {@link XmlWebApplicationContext},
 * despite not actually supporting external configuration files.
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
 * <p>
 *  用于测试的静态{@link orgspringframeworkwebcontextWebApplicationContext}实现不适用于生产应用程序
 * 
 * <p>实现{@link orgspringframeworkwebcontextConfigurableWebApplicationContext}接口,以允许直接替换{@link XmlWebApplicationContext}
 * ,尽管实际上不支持外部配置文件。
 * 
 *  将资源路径解释为servlet上下文资源,即作为Web应用程序根目录下的路径绝对路径,例如对于Web应用程序根目录之外的文件,可以通过"file："URL访问,由{@link orgspringframeworkcoreioDefaultResourceLoader}
 * 实现。
 * 
 *  <p>除了由{@link orgspringframeworkcontextsupportAbstractApplicationContext}检测到的特殊bean之外,此类还检测到上下文中的类型为{@link orgspringframeworkuicontextThemeSource}
 * 的bean,特殊bean名称为"themeSource"。
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see org.springframework.ui.context.ThemeSource
 */
public class StaticWebApplicationContext extends StaticApplicationContext
		implements ConfigurableWebApplicationContext, ThemeSource {

	private ServletContext servletContext;

	private ServletConfig servletConfig;

	private String namespace;

	private ThemeSource themeSource;


	public StaticWebApplicationContext() {
		setDisplayName("Root WebApplicationContext");
	}


	/**
	 * Set the ServletContext that this WebApplicationContext runs in.
	 * <p>
	 * 设置此WebApplicationContext运行的ServletContext
	 * 
	 */
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
			this.servletContext = servletConfig.getServletContext();
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

	/**
	 * The {@link StaticWebApplicationContext} class does not support this method.
	 * <p>
	 *  {@link StaticWebApplicationContext}类不支持此方法
	 * 
	 * 
	 * @throws UnsupportedOperationException <b>always</b>
	 */
	@Override
	public void setConfigLocation(String configLocation) {
		if (configLocation != null) {
			throw new UnsupportedOperationException("StaticWebApplicationContext does not support config locations");
		}
	}

	/**
	 * The {@link StaticWebApplicationContext} class does not support this method.
	 * <p>
	 *  {@link StaticWebApplicationContext}类不支持此方法
	 * 
	 * 
	 * @throws UnsupportedOperationException <b>always</b>
	 */
	@Override
	public void setConfigLocations(String... configLocations) {
		if (configLocations != null) {
			throw new UnsupportedOperationException("StaticWebApplicationContext does not support config locations");
		}
	}

	@Override
	public String[] getConfigLocations() {
		return null;
	}


	/**
	 * Register request/session scopes, a {@link ServletContextAwareProcessor}, etc.
	 * <p>
	 *  注册请求/会话范围,{@link ServletContextAwareProcessor}等
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
	 *  此实现支持ServletContext根目录下的文件路径
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
	 *  此实现也支持未展开的WAR中的模式匹配
	 * 
	 * 
	 * @see ServletContextResourcePatternResolver
	 */
	@Override
	protected ResourcePatternResolver getResourcePatternResolver() {
		return new ServletContextResourcePatternResolver(this);
	}

	/**
	 * Create and return a new {@link StandardServletEnvironment}.
	 * <p>
	 *  创建并返回一个新的{@link StandardServletEnvironment}
	 * 
	 */
	@Override
	protected ConfigurableEnvironment createEnvironment() {
		return new StandardServletEnvironment();
	}

	/**
	 * Initialize the theme capability.
	 * <p>
	 *  初始化主题功能
	 */
	@Override
	protected void onRefresh() {
		this.themeSource = UiApplicationContextUtils.initThemeSource(this);
	}

	@Override
	protected void initPropertySources() {
		WebApplicationContextUtils.initServletPropertySources(getEnvironment().getPropertySources(),
				this.servletContext, this.servletConfig);
	}

	@Override
	public Theme getTheme(String themeName) {
		return this.themeSource.getTheme(themeName);
	}

}
