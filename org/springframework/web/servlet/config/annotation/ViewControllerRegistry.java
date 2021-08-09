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

package org.springframework.web.servlet.config.annotation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

/**
 * Assists with the registration of simple automated controllers pre-configured
 * with status code and/or a view.
 *
 * <p>
 *  协助注册预先配置有状态码和/或视图的简单自动控制器
 * 
 * 
 * @author Rossen Stoyanchev
 * @author Keith Donald
 * @since 3.1
 */
public class ViewControllerRegistry {

	private final List<ViewControllerRegistration> registrations = new ArrayList<ViewControllerRegistration>(4);

	private final List<RedirectViewControllerRegistration> redirectRegistrations =
			new ArrayList<RedirectViewControllerRegistration>(10);

	private int order = 1;

	private ApplicationContext applicationContext;


	/**
	 * Map a view controller to the given URL path (or pattern) in order to render
	 * a response with a pre-configured status code and view.
	 * <p>Patterns like {@code "/admin/**"} or {@code "/articles/{articlename:\\w+}"}
	 * are allowed. See {@link org.springframework.util.AntPathMatcher} for more details on the
	 * syntax.
	 * <p>
	 * 将视图控制器映射到给定的URL路径(或模式),以便使用预配置的状态代码呈现响应,并查看<p>像{@code"/ admin / **"}或{@code"/文章/ {articlename：\\\\ w +}
	 * "}被允许请参阅{@link orgspringframeworkutilAntPathMatcher}了解有关语法的更多详细信息。
	 * 
	 */
	public ViewControllerRegistration addViewController(String urlPath) {
		ViewControllerRegistration registration = new ViewControllerRegistration(urlPath);
		registration.setApplicationContext(this.applicationContext);
		this.registrations.add(registration);
		return registration;
	}

	/**
	 * Map a view controller to the given URL path (or pattern) in order to redirect
	 * to another URL. By default the redirect URL is expected to be relative to
	 * the current ServletContext, i.e. as relative to the web application root.
	 * <p>
	 *  将视图控制器映射到给定的URL路径(或模式)以重定向到另一个URL默认情况下,重定向URL预期是相对于当前的ServletContext,即相对于Web应用程序根
	 * 
	 * 
	 * @since 4.1
	 */
	public RedirectViewControllerRegistration addRedirectViewController(String urlPath, String redirectUrl) {
		RedirectViewControllerRegistration registration = new RedirectViewControllerRegistration(urlPath, redirectUrl);
		registration.setApplicationContext(this.applicationContext);
		this.redirectRegistrations.add(registration);
		return registration;
	}

	/**
	 * Map a simple controller to the given URL path (or pattern) in order to
	 * set the response status to the given code without rendering a body.
	 * <p>
	 *  将一个简单的控制器映射到给定的URL路径(或模式),以便将响应状态设置为给定代码,而不渲染正文
	 * 
	 * 
	 * @since 4.1
	 */
	public void addStatusController(String urlPath, HttpStatus statusCode) {
		ViewControllerRegistration registration = new ViewControllerRegistration(urlPath);
		registration.setApplicationContext(this.applicationContext);
		registration.setStatusCode(statusCode);
		registration.getViewController().setStatusOnly(true);
		this.registrations.add(registration);
	}

	/**
	 * Specify the order to use for the {@code HandlerMapping} used to map view
	 * controllers relative to other handler mappings configured in Spring MVC.
	 * <p>By default this is set to 1, i.e. right after annotated controllers,
	 * which are ordered at 0.
	 * <p>
	 * 指定用于映射视图控制器相对于Spring MVC中配置的其他处理程序映射的{@code HandlerMapping}的顺序默认情况下,该值设置为1,即在注释控制器之后,即在0
	 * 
	 */
	public void setOrder(int order) {
		this.order = order;
	}

	protected void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}


	/**
	 * Return the {@code HandlerMapping} that contains the registered view
	 * controller mappings, or {@code null} for no registrations.
	 * <p>
	 *  返回包含已注册的视图控制器映射的{@code HandlerMapping},否则不返回{@code null}
	 */
	protected AbstractHandlerMapping getHandlerMapping() {
		if (this.registrations.isEmpty() && this.redirectRegistrations.isEmpty()) {
			return null;
		}
		Map<String, Object> urlMap = new LinkedHashMap<String, Object>();
		for (ViewControllerRegistration registration : this.registrations) {
			urlMap.put(registration.getUrlPath(), registration.getViewController());
		}
		for (RedirectViewControllerRegistration registration : this.redirectRegistrations) {
			urlMap.put(registration.getUrlPath(), registration.getViewController());
		}
		SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
		handlerMapping.setOrder(this.order);
		handlerMapping.setUrlMap(urlMap);
		return handlerMapping;
	}

}
