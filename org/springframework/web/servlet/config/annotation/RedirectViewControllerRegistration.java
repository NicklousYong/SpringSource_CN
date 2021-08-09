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

import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Assist with the registration of a single redirect view controller.
 *
 * <p>
 *  协助注册一个重定向视图控制器
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 4.1
 */
public class RedirectViewControllerRegistration {

	private final String urlPath;

	private final RedirectView redirectView;

	private final ParameterizableViewController controller = new ParameterizableViewController();


	public RedirectViewControllerRegistration(String urlPath, String redirectUrl) {
		Assert.notNull(urlPath, "'urlPath' is required.");
		Assert.notNull(redirectUrl, "'redirectUrl' is required.");
		this.urlPath = urlPath;
		this.redirectView = new RedirectView(redirectUrl);
		this.redirectView.setContextRelative(true);
		this.controller.setView(this.redirectView);
	}


	/**
	 * Set the specific redirect 3xx status code to use.
	 *
	 * <p>If not set, {@link org.springframework.web.servlet.view.RedirectView}
	 * will select {@code HttpStatus.MOVED_TEMPORARILY (302)} by default.
	 * <p>
	 *  设置要使用的特定重定向3xx状态代码
	 * 
	 * <p>如果未设置,{@link orgspringframeworkwebservletviewRedirectView}将默认选择{@code HttpStatusMOVED_TEMPORARILY(302)}
	 * 。
	 * 
	 */
	public RedirectViewControllerRegistration setStatusCode(HttpStatus statusCode) {
		Assert.isTrue(statusCode.is3xxRedirection(), "Not a redirect status code.");
		this.redirectView.setStatusCode(statusCode);
		return this;
	}

	/**
	 * Whether to interpret a given redirect URL that starts with a slash ("/")
	 * as relative to the current ServletContext, i.e. as relative to the web
	 * application root.
	 *
	 * <p>Default is {@code true}.
	 * <p>
	 *  是否解释以斜杠("/")开头的给定的重定向URL相对于当前的ServletContext,即相对于Web应用程序根
	 * 
	 *  <p>默认值为{@code true}
	 * 
	 */
	public RedirectViewControllerRegistration setContextRelative(boolean contextRelative) {
		this.redirectView.setContextRelative(contextRelative);
		return this;
	}

	/**
	 * Whether to propagate the query parameters of the current request through
	 * to the target redirect URL.
	 *
	 * <p>Default is {@code false}.
	 * <p>
	 *  是否将当前请求的查询参数传播到目标重定向URL
	 * 
	 */
	public RedirectViewControllerRegistration setKeepQueryParams(boolean propagate) {
		this.redirectView.setPropagateQueryParams(propagate);
		return this;
	}

	protected void setApplicationContext(ApplicationContext applicationContext) {
		this.controller.setApplicationContext(applicationContext);
		this.redirectView.setApplicationContext(applicationContext);
	}

	protected String getUrlPath() {
		return this.urlPath;
	}

	protected ParameterizableViewController getViewController() {
		return this.controller;
	}

}
