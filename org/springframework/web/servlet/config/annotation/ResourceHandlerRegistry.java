/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2016 the original author or authors.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;

import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

/**
 * Stores registrations of resource handlers for serving static resources such as images, css files and others
 * through Spring MVC including setting cache headers optimized for efficient loading in a web browser.
 * Resources can be served out of locations under web application root, from the classpath, and others.
 *
 * <p>To create a resource handler, use {@link #addResourceHandler(String...)} providing the URL path patterns
 * for which the handler should be invoked to serve static resources (e.g. {@code "/resources/**"}).
 *
 * <p>Then use additional methods on the returned {@link ResourceHandlerRegistration} to add one or more
 * locations from which to serve static content from (e.g. {{@code "/"},
 * {@code "classpath:/META-INF/public-web-resources/"}}) or to specify a cache period for served resources.
 *
 * <p>
 * 存储用于通过Spring MVC提供静态资源(如图像,css文件等)的资源处理程序的注册,包括设置优化以在Web浏览器中高效加载的缓存头。资源可以从Web应用程序根目录,类路径和其他位置
 * 
 *  <p>要创建资源处理程序,请使用{@link #addResourceHandler(String)}提供处理程序应调用以提供静态资源的URL路径模式(例如{@code"/ resources / **"}
 * )。
 * 
 *  <p>然后在返回的{@link ResourceHandlerRegistration}上使用其他方法来添加一个或多个位置,从中提供静态内容(例如{{@code"/"},{@code"classpath：/ META-INF / public-web-resources /"}
 * })或指定服务资源的缓存期。
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 3.1
 * @see DefaultServletHandlerConfigurer
 */
public class ResourceHandlerRegistry {

	private final ServletContext servletContext;

	private final ApplicationContext applicationContext;

	private final ContentNegotiationManager contentNegotiationManager;

	private final List<ResourceHandlerRegistration> registrations = new ArrayList<ResourceHandlerRegistration>();

	private int order = Integer.MAX_VALUE -1;


	/**
	 * Create a new resource handler registry for the given application context.
	 * <p>
	 * 为给定的应用程序上下文创建一个新的资源处理程序注册表
	 * 
	 * 
	 * @param applicationContext the Spring application context
	 * @param servletContext the corresponding Servlet context
	 */
	public ResourceHandlerRegistry(ApplicationContext applicationContext, ServletContext servletContext) {
		this(applicationContext, servletContext, null);
	}

	/**
	 * Create a new resource handler registry for the given application context.
	 * <p>
	 *  为给定的应用程序上下文创建一个新的资源处理程序注册表
	 * 
	 * 
	 * @param applicationContext the Spring application context
	 * @param servletContext the corresponding Servlet context
	 * @param contentNegotiationManager the content negotiation manager to use
	 * @since 4.3
	 */
	public ResourceHandlerRegistry(ApplicationContext applicationContext, ServletContext servletContext,
			ContentNegotiationManager contentNegotiationManager) {

		Assert.notNull(applicationContext, "ApplicationContext is required");
		this.applicationContext = applicationContext;
		this.servletContext = servletContext;
		this.contentNegotiationManager = contentNegotiationManager;
	}


	/**
	 * Add a resource handler for serving static resources based on the specified URL path
	 * patterns. The handler will be invoked for every incoming request that matches to
	 * one of the specified path patterns.
	 * <p>Patterns like {@code "/static/**"} or {@code "/css/{filename:\\w+\\.css}"}
	 * are allowed. See {@link org.springframework.util.AntPathMatcher} for more details on the
	 * syntax.
	 * <p>
	 *  根据指定的URL路径模式添加一个用于提供静态资源的资源处理程序将针对与指定的路径模式之一匹配的每个传入请求调用处理程序,如{@code"/ static / **"}或{@code"/ css / {filename：\\\\ w + \\\\ css}
	 * "}可以查看{@link orgspringframeworkutilAntPathMatcher}了解更多有关语法的详细信息。
	 * 
	 * 
	 * @return A {@link ResourceHandlerRegistration} to use to further configure the
	 * registered resource handler
	 */
	public ResourceHandlerRegistration addResourceHandler(String... pathPatterns) {
		ResourceHandlerRegistration registration =
				new ResourceHandlerRegistration(this.applicationContext, pathPatterns);
		this.registrations.add(registration);
		return registration;
	}

	/**
	 * Whether a resource handler has already been registered for the given path pattern.
	 * <p>
	 *  资源处理程序是否已经为给定的路径模式注册
	 * 
	 */
	public boolean hasMappingForPattern(String pathPattern) {
		for (ResourceHandlerRegistration registration : this.registrations) {
			if (Arrays.asList(registration.getPathPatterns()).contains(pathPattern)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Specify the order to use for resource handling relative to other {@link HandlerMapping}s
	 * configured in the Spring MVC application context.
	 * <p>The default value used is {@code Integer.MAX_VALUE-1}.
	 * <p>
	 * 指定与Spring MVC应用程序上下文中配置的其他{@link HandlerMapping}相关的资源处理顺序<p>使用的默认值为{@code IntegerMAX_VALUE-1}
	 * 
	 */
	public ResourceHandlerRegistry setOrder(int order) {
		this.order = order;
		return this;
	}

	/**
	 * Return a handler mapping with the mapped resource handlers; or {@code null} in case
	 * of no registrations.
	 * <p>
	 *  使用映射的资源处理程序返回处理程序映射;或{@code null},如果没有注册
	 */
	protected AbstractHandlerMapping getHandlerMapping() {
		if (this.registrations.isEmpty()) {
			return null;
		}

		Map<String, HttpRequestHandler> urlMap = new LinkedHashMap<String, HttpRequestHandler>();
		for (ResourceHandlerRegistration registration : this.registrations) {
			for (String pathPattern : registration.getPathPatterns()) {
				ResourceHttpRequestHandler handler = registration.getRequestHandler();
				handler.setServletContext(this.servletContext);
				handler.setApplicationContext(this.applicationContext);
				handler.setContentNegotiationManager(this.contentNegotiationManager);
				try {
					handler.afterPropertiesSet();
					handler.afterSingletonsInstantiated();
				}
				catch (Exception ex) {
					throw new BeanInitializationException("Failed to init ResourceHttpRequestHandler", ex);
				}
				urlMap.put(pathPattern, handler);
			}
		}

		SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
		handlerMapping.setOrder(order);
		handlerMapping.setUrlMap(urlMap);
		return handlerMapping;
	}

}
