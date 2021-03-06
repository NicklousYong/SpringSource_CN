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

package org.springframework.web.context.request;

/**
 * Extension of the {@link WebRequest} interface, exposing the
 * native request and response objects in a generic fashion.
 *
 * <p>Mainly intended for framework-internal usage,
 * in particular for generic argument resolution code.
 *
 * <p>
 *  扩展{@link WebRequest}界面,以通用方式暴露本机请求和响应对象
 * 
 *  <p>主要用于框架内部使用,特别是用于通用参数解析代码
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.5.2
 */
public interface NativeWebRequest extends WebRequest {

	/**
	 * Return the underlying native request object, if available.
	 * <p>
	 * 返回底层本机请求对象(如果可用)
	 * 
	 * 
	 * @see javax.servlet.http.HttpServletRequest
	 * @see javax.portlet.ActionRequest
	 * @see javax.portlet.RenderRequest
	 */
	Object getNativeRequest();

	/**
	 * Return the underlying native response object, if available.
	 * <p>
	 *  返回底层本机响应对象(如果可用)
	 * 
	 * 
	 * @see javax.servlet.http.HttpServletResponse
	 * @see javax.portlet.ActionResponse
	 * @see javax.portlet.RenderResponse
	 */
	Object getNativeResponse();

	/**
	 * Return the underlying native request object, if available.
	 * <p>
	 *  返回底层本机请求对象(如果可用)
	 * 
	 * 
	 * @param requiredType the desired type of request object
	 * @return the matching request object, or {@code null} if none
	 * of that type is available
	 * @see javax.servlet.http.HttpServletRequest
	 * @see javax.portlet.ActionRequest
	 * @see javax.portlet.RenderRequest
	 */
	<T> T getNativeRequest(Class<T> requiredType);

	/**
	 * Return the underlying native response object, if available.
	 * <p>
	 *  返回底层本机响应对象(如果可用)
	 * 
	 * @param requiredType the desired type of response object
	 * @return the matching response object, or {@code null} if none
	 * of that type is available
	 * @see javax.servlet.http.HttpServletResponse
	 * @see javax.portlet.ActionResponse
	 * @see javax.portlet.RenderResponse
	 */
	<T> T getNativeResponse(Class<T> requiredType);

}
