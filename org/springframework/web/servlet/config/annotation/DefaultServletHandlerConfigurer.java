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

package org.springframework.web.servlet.config.annotation;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;

import org.springframework.util.Assert;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.DefaultServletHttpRequestHandler;

/**
 * Configures a request handler for serving static resources by forwarding the request to the Servlet container's
 * "default" Servlet. This is intended to be used when the Spring MVC {@link DispatcherServlet} is mapped to "/"
 * thus overriding the Servlet container's default handling of static resources. Since this handler is configured
 * at the lowest precedence, effectively it allows all other handler mappings to handle the request, and if none
 * of them do, this handler can forward it to the "default" Servlet.
 *
 * <p>
 * 通过将请求转发到Servlet容器的"默认"Servlet来配置请求处理程序来服务静态资源当Spring MVC {@link DispatcherServlet}映射到"/"时,这意味着用于覆盖Ser
 * vlet容器的静态处理资源由于此处理程序配置为最低优先级,因此有效地允许所有其他处理程序映射来处理请求,如果没有这些处理程序可以将此处理程序转发到"默认"Servlet。
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 3.1
 *
 * @see DefaultServletHttpRequestHandler
 */
public class DefaultServletHandlerConfigurer {

	private final ServletContext servletContext;

	private DefaultServletHttpRequestHandler handler;

	/**
	 * Create a {@link DefaultServletHandlerConfigurer} instance.
	 * <p>
	 *  创建一个{@link DefaultServletHandlerConfigurer}实例
	 * 
	 * 
	 * @param servletContext the ServletContext to use to configure the underlying DefaultServletHttpRequestHandler.
	 */
	public DefaultServletHandlerConfigurer(ServletContext servletContext) {
		Assert.notNull(servletContext, "A ServletContext is required to configure default servlet handling");
		this.servletContext = servletContext;
	}

	/**
	 * Enable forwarding to the "default" Servlet. When this method is used the {@link DefaultServletHttpRequestHandler}
	 * will try to auto-detect the "default" Servlet name. Alternatively, you can specify the name of the default
	 * Servlet via {@link #enable(String)}.
	 * <p>
	 * 启用转发到"默认"Servlet当使用此方法时,{@link DefaultServletHttpRequestHandler}将尝试自动检测"默认"Servlet名称或者,您可以通过{@link #enable(String)指定默认Servlet的名称)}
	 * 。
	 * 
	 * 
	 * @see DefaultServletHttpRequestHandler
	 */
	public void enable() {
		enable(null);
	}

	/**
	 * Enable forwarding to the "default" Servlet identified by the given name.
	 * This is useful when the default Servlet cannot be auto-detected, for example when it has been manually configured.
	 * <p>
	 *  启用转发到"默认"由给定名称标识的Servlet当缺省Servlet不能被自动检测时,这是非常有用的,例如手动配置
	 * 
	 * 
	 * @see DefaultServletHttpRequestHandler
	 */
	public void enable(String defaultServletName) {
		handler = new DefaultServletHttpRequestHandler();
		handler.setDefaultServletName(defaultServletName);
		handler.setServletContext(servletContext);
	}

	/**
	 * Return a handler mapping instance ordered at {@link Integer#MAX_VALUE} containing the
	 * {@link DefaultServletHttpRequestHandler} instance mapped to {@code "/**"}; or {@code null} if
	 * default servlet handling was not been enabled.
	 * <p>
	 *  返回在{@link Integer#MAX_VALUE}中排序的处理程序映射实例,其中包含映射到{@code"/ **"}的{@link DefaultServletHttpRequestHandler}
	 * 实例;或者{@code null}如果未启用默认servlet处理。
	 */
	protected AbstractHandlerMapping getHandlerMapping() {
		if (handler == null) {
			return null;
		}

		Map<String, HttpRequestHandler> urlMap = new HashMap<String, HttpRequestHandler>();
		urlMap.put("/**", handler);

		SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
		handlerMapping.setOrder(Integer.MAX_VALUE);
		handlerMapping.setUrlMap(urlMap);
		return handlerMapping;
	}

}
