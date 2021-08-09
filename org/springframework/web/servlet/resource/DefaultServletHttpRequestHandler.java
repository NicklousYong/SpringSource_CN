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

package org.springframework.web.servlet.resource;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.context.ServletContextAware;

/**
 * An {@link HttpRequestHandler} for serving static files using the Servlet container's "default" Servlet.
 *
 * <p>This handler is intended to be used with a "/*" mapping when the
 * {@link org.springframework.web.servlet.DispatcherServlet DispatcherServlet}
 * is mapped to "/", thus  overriding the Servlet container's default handling of static resources.
 * The mapping to this handler should generally be ordered as the last in the chain so that it will
 * only execute when no other more specific mappings (i.e., to controllers) can be matched.
 *
 * <p>Requests are handled by forwarding through the {@link RequestDispatcher} obtained via the
 * name specified through the {@link #setDefaultServletName "defaultServletName" property}.
 * In most cases, the {@code defaultServletName} does not need to be set explicitly, as the
 * handler checks at initialization time for the presence of the default Servlet of well-known
 * containers such as Tomcat, Jetty, Resin, WebLogic and WebSphere. However, when running in a
 * container where the default Servlet's name is not known, or where it has been customized
 * via server configuration, the  {@code defaultServletName} will need to be set explicitly.
 *
 * <p>
 *  使用Servlet容器的"默认"Servlet提供静态文件的{@link HttpRequestHandler}
 * 
 * <p>当{@link orgspringframeworkwebservletDispatcherServlet DispatcherServlet}映射到"/"时,此处理程序旨在与"/ *"映射一起使
 * 用,从而覆盖Servlet容器对静态资源的默认处理对于此处理程序的映射通常应为作为链中的最后一个订单,以便只有当没有其他更具体的映射(即控制器)可以匹配时才执行。
 * 
 * <p>请求通过通过{@link #setDefaultServletName"defaultServletName"属性}指定的名称获取的{@link RequestDispatcher}转发来处理。
 * 在大多数情况下,不需要显式设置{@code defaultServletName}因为处理程序在初始化时检查是否存在Tomcat,Jetty,Resin,WebLogic和WebSphere等众所周知的
 * 容器的默认Servlet。
 * 
 * @author Jeremy Grelle
 * @author Juergen Hoeller
 * @since 3.0.4
 */
public class DefaultServletHttpRequestHandler implements HttpRequestHandler, ServletContextAware {

	/** Default Servlet name used by Tomcat, Jetty, JBoss, and GlassFish */
	private static final String COMMON_DEFAULT_SERVLET_NAME = "default";

	/** Default Servlet name used by Google App Engine */
	private static final String GAE_DEFAULT_SERVLET_NAME = "_ah_default";

	/** Default Servlet name used by Resin */
	private static final String RESIN_DEFAULT_SERVLET_NAME = "resin-file";

	/** Default Servlet name used by WebLogic */
	private static final String WEBLOGIC_DEFAULT_SERVLET_NAME = "FileServlet";

	/** Default Servlet name used by WebSphere */
	private static final String WEBSPHERE_DEFAULT_SERVLET_NAME = "SimpleFileServlet";


	private String defaultServletName;

	private ServletContext servletContext;


	/**
	 * Set the name of the default Servlet to be forwarded to for static resource requests.
	 * <p>
	 * 但是,当运行在默认Servlet名称不知道的容器中时,通过服务器配置进行了定制,{@code defaultServletName}将需要显式设置。
	 * 
	 */
	public void setDefaultServletName(String defaultServletName) {
		this.defaultServletName = defaultServletName;
	}

	/**
	 * If the {@code defaultServletName} property has not been explicitly set,
	 * attempts to locate the default Servlet using the known common
	 * container-specific names.
	 * <p>
	 *  为静态资源请求设置要转发的默认Servlet的名称
	 * 
	 */
	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
		if (!StringUtils.hasText(this.defaultServletName)) {
			if (this.servletContext.getNamedDispatcher(COMMON_DEFAULT_SERVLET_NAME) != null) {
				this.defaultServletName = COMMON_DEFAULT_SERVLET_NAME;
			}
			else if (this.servletContext.getNamedDispatcher(GAE_DEFAULT_SERVLET_NAME) != null) {
				this.defaultServletName = GAE_DEFAULT_SERVLET_NAME;
			}
			else if (this.servletContext.getNamedDispatcher(RESIN_DEFAULT_SERVLET_NAME) != null) {
				this.defaultServletName = RESIN_DEFAULT_SERVLET_NAME;
			}
			else if (this.servletContext.getNamedDispatcher(WEBLOGIC_DEFAULT_SERVLET_NAME) != null) {
				this.defaultServletName = WEBLOGIC_DEFAULT_SERVLET_NAME;
			}
			else if (this.servletContext.getNamedDispatcher(WEBSPHERE_DEFAULT_SERVLET_NAME) != null) {
				this.defaultServletName = WEBSPHERE_DEFAULT_SERVLET_NAME;
			}
			else {
				throw new IllegalStateException("Unable to locate the default servlet for serving static content. " +
						"Please set the 'defaultServletName' property explicitly.");
			}
		}
	}


	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		RequestDispatcher rd = this.servletContext.getNamedDispatcher(this.defaultServletName);
		if (rd == null) {
			throw new IllegalStateException("A RequestDispatcher could not be located for the default servlet '" +
					this.defaultServletName + "'");
		}
		rd.forward(request, response);
	}

}
