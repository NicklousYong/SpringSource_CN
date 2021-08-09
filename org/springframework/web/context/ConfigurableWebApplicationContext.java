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

package org.springframework.web.context;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.springframework.context.ConfigurableApplicationContext;

/**
 * Interface to be implemented by configurable web application contexts.
 * Supported by {@link ContextLoader} and
 * {@link org.springframework.web.servlet.FrameworkServlet}.
 *
 * <p>Note: The setters of this interface need to be called before an
 * invocation of the {@link #refresh} method inherited from
 * {@link org.springframework.context.ConfigurableApplicationContext}.
 * They do not cause an initialization of the context on their own.
 *
 * <p>
 *  由可配置的Web应用程序上下文实现的接口由{@link ContextLoader}和{@link orgspringframeworkwebservletFrameworkServlet}支持
 * 
 * 注意：在{@link orgspringframeworkcontextConfigurableApplicationContext}继承的{@link #refresh}方法的调用之前,需要调用此接口
 * 的setter。
 * 它们不会自己对上下文进行初始化。
 * 
 * 
 * @author Juergen Hoeller
 * @since 05.12.2003
 * @see #refresh
 * @see ContextLoader#createWebApplicationContext
 * @see org.springframework.web.servlet.FrameworkServlet#createWebApplicationContext
 */
public interface ConfigurableWebApplicationContext extends WebApplicationContext, ConfigurableApplicationContext {

	/**
	 * Prefix for ApplicationContext ids that refer to context path and/or servlet name.
	 * <p>
	 *  引用上下文路径和/或servlet名称的ApplicationContext ids的前缀
	 * 
	 */
	String APPLICATION_CONTEXT_ID_PREFIX = WebApplicationContext.class.getName() + ":";

	/**
	 * Name of the ServletConfig environment bean in the factory.
	 * <p>
	 *  工厂中ServletConfig环境bean的名称
	 * 
	 * 
	 * @see javax.servlet.ServletConfig
	 */
	String SERVLET_CONFIG_BEAN_NAME = "servletConfig";


	/**
	 * Set the ServletContext for this web application context.
	 * <p>Does not cause an initialization of the context: refresh needs to be
	 * called after the setting of all configuration properties.
	 * <p>
	 *  设置此Web应用程序上下文的ServletContext <p>不会导致上下文的初始化：在设置所有配置属性后需要调用refresh
	 * 
	 * 
	 * @see #refresh()
	 */
	void setServletContext(ServletContext servletContext);

	/**
	 * Set the ServletConfig for this web application context.
	 * Only called for a WebApplicationContext that belongs to a specific Servlet.
	 * <p>
	 *  设置此Web应用程序上下文的ServletConfig仅对属于特定Servlet的WebApplicationContext进行调用
	 * 
	 * 
	 * @see #refresh()
	 */
	void setServletConfig(ServletConfig servletConfig);

	/**
	 * Return the ServletConfig for this web application context, if any.
	 * <p>
	 *  返回此Web应用程序上下文的ServletConfig(如果有)
	 * 
	 */
	ServletConfig getServletConfig();

	/**
	 * Set the namespace for this web application context,
	 * to be used for building a default context config location.
	 * The root web application context does not have a namespace.
	 * <p>
	 * 设置此Web应用程序上下文的命名空间,用于构建默认上下文配置位置根Web应用程序上下文没有命名空间
	 * 
	 */
	void setNamespace(String namespace);

	/**
	 * Return the namespace for this web application context, if any.
	 * <p>
	 *  返回此Web应用程序上下文的命名空间(如果有)
	 * 
	 */
	String getNamespace();

	/**
	 * Set the config locations for this web application context in init-param style,
	 * i.e. with distinct locations separated by commas, semicolons or whitespace.
	 * <p>If not set, the implementation is supposed to use a default for the
	 * given namespace or the root web application context, as appropriate.
	 * <p>
	 *  在init-param样式中设置此Web应用程序上下文的配置位置,即用逗号,分号或空格分隔的不同位置<p>如果未设置,则该实现应该对给定的命名空间或根Web应用程序使用默认值上下文
	 * 
	 */
	void setConfigLocation(String configLocation);

	/**
	 * Set the config locations for this web application context.
	 * <p>If not set, the implementation is supposed to use a default for the
	 * given namespace or the root web application context, as appropriate.
	 * <p>
	 *  设置此Web应用程序上下文的配置位置<p>如果未设置,则实现应适用于给定命名空间或根Web应用程序上下文的默认值
	 * 
	 */
	void setConfigLocations(String... configLocations);

	/**
	 * Return the config locations for this web application context,
	 * or {@code null} if none specified.
	 * <p>
	 * 返回此Web应用程序上下文的配置位置,否则返回{@code null}
	 */
	String[] getConfigLocations();

}
