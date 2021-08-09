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

package org.springframework.web.context;

import javax.servlet.ServletContext;

import org.springframework.context.ApplicationContext;

/**
 * Interface to provide configuration for a web application. This is read-only while
 * the application is running, but may be reloaded if the implementation supports this.
 *
 * <p>This interface adds a {@code getServletContext()} method to the generic
 * ApplicationContext interface, and defines a well-known application attribute name
 * that the root context must be bound to in the bootstrap process.
 *
 * <p>Like generic application contexts, web application contexts are hierarchical.
 * There is a single root context per application, while each servlet in the application
 * (including a dispatcher servlet in the MVC framework) has its own child context.
 *
 * <p>In addition to standard application context lifecycle capabilities,
 * WebApplicationContext implementations need to detect {@link ServletContextAware}
 * beans and invoke the {@code setServletContext} method accordingly.
 *
 * <p>
 *  为Web应用程序提供配置的界面在应用程序运行时,它是只读的,但如果实现支持此操作,则可以重新加载
 * 
 * <p>此接口向通用ApplicationContext接口添加{@code getServletContext()}方法,并定义了引导过程中必须绑定根上下文的知名应用程序属性名称
 * 
 *  像通用应用程序上下文一样,Web应用程序上下文是分层的。每个应用程序都有单个根上下文,而应用程序中的每个servlet(包括MVC框架中的调度程序servlet)都有自己的子上下文
 * 
 *  <p>除了标准的应用程序上下文生命周期功能之外,WebApplicationContext实现需要检测{@link ServletContextAware} bean并相应地调用{@code setServletContext}
 * 方法。
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since January 19, 2001
 * @see ServletContextAware#setServletContext
 */
public interface WebApplicationContext extends ApplicationContext {

	/**
	 * Context attribute to bind root WebApplicationContext to on successful startup.
	 * <p>Note: If the startup of the root context fails, this attribute can contain
	 * an exception or error as value. Use WebApplicationContextUtils for convenient
	 * lookup of the root WebApplicationContext.
	 * <p>
	 * 注意：如果根上下文的启动失败,则该属性可以包含异常或错误作为值使用WebApplicationContextUtils方便查找根WebApplicationContext
	 * 
	 * 
	 * @see org.springframework.web.context.support.WebApplicationContextUtils#getWebApplicationContext
	 * @see org.springframework.web.context.support.WebApplicationContextUtils#getRequiredWebApplicationContext
	 */
	String ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE = WebApplicationContext.class.getName() + ".ROOT";

	/**
	 * Scope identifier for request scope: "request".
	 * Supported in addition to the standard scopes "singleton" and "prototype".
	 * <p>
	 *  请求范围的范围标识符："请求"除了标准范围"单例"和"原型"之外,
	 * 
	 */
	String SCOPE_REQUEST = "request";

	/**
	 * Scope identifier for session scope: "session".
	 * Supported in addition to the standard scopes "singleton" and "prototype".
	 * <p>
	 *  会话范围的范围标识符："会话"除标准范围"单例"和"原型"之外,
	 * 
	 */
	String SCOPE_SESSION = "session";

	/**
	 * Scope identifier for global session scope: "globalSession".
	 * Supported in addition to the standard scopes "singleton" and "prototype".
	 * <p>
	 *  全局会话范围的范围标识符："globalSession"除了标准范围"singleton"和"prototype"之外,
	 * 
	 */
	String SCOPE_GLOBAL_SESSION = "globalSession";

	/**
	 * Scope identifier for the global web application scope: "application".
	 * Supported in addition to the standard scopes "singleton" and "prototype".
	 * <p>
	 * 全球Web应用范围的范围标识符："application"除标准范围"singleton"和"prototype"之外,
	 * 
	 */
	String SCOPE_APPLICATION = "application";

	/**
	 * Name of the ServletContext environment bean in the factory.
	 * <p>
	 *  工厂中ServletContext环境bean的名称
	 * 
	 * 
	 * @see javax.servlet.ServletContext
	 */
	String SERVLET_CONTEXT_BEAN_NAME = "servletContext";

	/**
	 * Name of the ServletContext/PortletContext init-params environment bean in the factory.
	 * <p>Note: Possibly merged with ServletConfig/PortletConfig parameters.
	 * ServletConfig parameters override ServletContext parameters of the same name.
	 * <p>
	 *  ServletContext / PortletContext的名称在工厂中的init-params环境bean <p>注意：可能与ServletConfig / PortletConfig参数合并S
	 * ervletConfig参数覆盖同名的ServletContext参数。
	 * 
	 * 
	 * @see javax.servlet.ServletContext#getInitParameterNames()
	 * @see javax.servlet.ServletContext#getInitParameter(String)
	 * @see javax.servlet.ServletConfig#getInitParameterNames()
	 * @see javax.servlet.ServletConfig#getInitParameter(String)
	 */
	String CONTEXT_PARAMETERS_BEAN_NAME = "contextParameters";

	/**
	 * Name of the ServletContext/PortletContext attributes environment bean in the factory.
	 * <p>
	 *  工厂中ServletContext / PortletContext属性环境bean的名称
	 * 
	 * 
	 * @see javax.servlet.ServletContext#getAttributeNames()
	 * @see javax.servlet.ServletContext#getAttribute(String)
	 */
	String CONTEXT_ATTRIBUTES_BEAN_NAME = "contextAttributes";


	/**
	 * Return the standard Servlet API ServletContext for this application.
	 * <p>Also available for a Portlet application, in addition to the PortletContext.
	 * <p>
	 *  返回此应用程序的标准Servlet API ServletContext <p>除了PortletContext之外,Portlet应用程序也可用
	 */
	ServletContext getServletContext();

}
