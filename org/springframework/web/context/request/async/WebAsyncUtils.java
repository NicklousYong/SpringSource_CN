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

package org.springframework.web.context.request.async;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.ClassUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

/**
 * Utility methods related to processing asynchronous web requests.
 *
 * <p>
 *  处理异步Web请求的实用方法
 * 
 * 
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @since 3.2
 */
public abstract class WebAsyncUtils {

	public static final String WEB_ASYNC_MANAGER_ATTRIBUTE = WebAsyncManager.class.getName() + ".WEB_ASYNC_MANAGER";

	// Determine whether Servlet 3.0's ServletRequest.startAsync method is available
	private static final boolean startAsyncAvailable = ClassUtils.hasMethod(ServletRequest.class, "startAsync");


	/**
	 * Obtain the {@link WebAsyncManager} for the current request, or if not
	 * found, create and associate it with the request.
	 * <p>
	 *  获取当前请求的{@link WebAsyncManager},或者如果没有找到,请创建并将其与请求相关联
	 * 
	 */
	public static WebAsyncManager getAsyncManager(ServletRequest servletRequest) {
		WebAsyncManager asyncManager = (WebAsyncManager) servletRequest.getAttribute(WEB_ASYNC_MANAGER_ATTRIBUTE);
		if (asyncManager == null) {
			asyncManager = new WebAsyncManager();
			servletRequest.setAttribute(WEB_ASYNC_MANAGER_ATTRIBUTE, asyncManager);
		}
		return asyncManager;
	}

	/**
	 * Obtain the {@link WebAsyncManager} for the current request, or if not
	 * found, create and associate it with the request.
	 * <p>
	 * 获取当前请求的{@link WebAsyncManager},或者如果没有找到,请创建并将其与请求相关联
	 * 
	 */
	public static WebAsyncManager getAsyncManager(WebRequest webRequest) {
		int scope = RequestAttributes.SCOPE_REQUEST;
		WebAsyncManager asyncManager = (WebAsyncManager) webRequest.getAttribute(WEB_ASYNC_MANAGER_ATTRIBUTE, scope);
		if (asyncManager == null) {
			asyncManager = new WebAsyncManager();
			webRequest.setAttribute(WEB_ASYNC_MANAGER_ATTRIBUTE, asyncManager, scope);
		}
		return asyncManager;
	}

	/**
	 * Create an AsyncWebRequest instance. By default, an instance of
	 * {@link StandardServletAsyncWebRequest} gets created when running in
	 * Servlet 3.0 (or higher) environment - as a fallback, an instance
	 * of {@link NoSupportAsyncWebRequest} will be returned.
	 * <p>
	 *  创建一个AsyncWebRequest实例默认情况下,在Servlet 30(或更高版本)环境中运行时会创建一个{@link StandardServletAsyncWebRequest}的实例 - 
	 * 作为回退,将返回一个{@link NoSupportAsyncWebRequest}的实例。
	 * 
	 * 
	 * @param request the current request
	 * @param response the current response
	 * @return an AsyncWebRequest instance (never {@code null})
	 */
	public static AsyncWebRequest createAsyncWebRequest(HttpServletRequest request, HttpServletResponse response) {
		return (startAsyncAvailable ? AsyncWebRequestFactory.createStandardAsyncWebRequest(request, response) :
				new NoSupportAsyncWebRequest(request, response));
	}


	/**
	 * Inner class to avoid a hard dependency on the Servlet 3.0 API.
	 * <p>
	 */
	private static class AsyncWebRequestFactory {

		public static AsyncWebRequest createStandardAsyncWebRequest(HttpServletRequest request, HttpServletResponse response) {
			return new StandardServletAsyncWebRequest(request, response);
		}
	}

}
