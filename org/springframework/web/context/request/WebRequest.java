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

package org.springframework.web.context.request;

import java.security.Principal;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * Generic interface for a web request. Mainly intended for generic web
 * request interceptors, giving them access to general request metadata,
 * not for actual handling of the request.
 *
 * <p>
 *  Web请求的通用接口主要用于通用Web请求拦截器,使其能够访问一般请求元数据,而不是实际处理请求
 * 
 * 
 * @author Juergen Hoeller
 * @author Brian Clozel
 * @since 2.0
 * @see WebRequestInterceptor
 */
public interface WebRequest extends RequestAttributes {

	/**
	 * Return the request header of the given name, or {@code null} if none.
	 * <p>Retrieves the first header value in case of a multi-value header.
	 * <p>
	 * 返回给定名称的请求标头,否则返回{@code null} <p>在多值标题的情况下检索第一个标题值
	 * 
	 * 
	 * @since 3.0
	 * @see javax.servlet.http.HttpServletRequest#getHeader(String)
	 */
	String getHeader(String headerName);

	/**
	 * Return the request header values for the given header name,
	 * or {@code null} if none.
	 * <p>A single-value header will be exposed as an array with a single element.
	 * <p>
	 *  返回给定标题名称的请求标头值,否则返回{@code null} <p>单值标题将作为具有单个元素的数组公开
	 * 
	 * 
	 * @since 3.0
	 * @see javax.servlet.http.HttpServletRequest#getHeaders(String)
	 */
	String[] getHeaderValues(String headerName);

	/**
	 * Return a Iterator over request header names.
	 * <p>
	 *  通过请求头名返回Iterator
	 * 
	 * 
	 * @since 3.0
	 * @see javax.servlet.http.HttpServletRequest#getHeaderNames()
	 */
	Iterator<String> getHeaderNames();

	/**
	 * Return the request parameter of the given name, or {@code null} if none.
	 * <p>Retrieves the first parameter value in case of a multi-value parameter.
	 * <p>
	 *  返回给定名称的请求参数,否则返回{@code null} <p>在多值参数的情况下检索第一个参数值
	 * 
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getParameter(String)
	 */
	String getParameter(String paramName);

	/**
	 * Return the request parameter values for the given parameter name,
	 * or {@code null} if none.
	 * <p>A single-value parameter will be exposed as an array with a single element.
	 * <p>
	 *  返回给定参数名称的请求参数值,否则返回{@code null} <p>单值参数将以单个元素作为数组显示
	 * 
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getParameterValues(String)
	 */
	String[] getParameterValues(String paramName);

	/**
	 * Return a Iterator over request parameter names.
	 * <p>
	 *  通过请求参数名返回Iterator
	 * 
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getParameterNames()
	 * @since 3.0
	 */
	Iterator<String> getParameterNames();

	/**
	 * Return a immutable Map of the request parameters, with parameter names as map keys
	 * and parameter values as map values. The map values will be of type String array.
	 * <p>A single-value parameter will be exposed as an array with a single element.
	 * <p>
	 * 返回一个不可变的请求参数映射,将参数名称作为映射键和参数值作为映射值映射值将为String数组<p>单值参数将被暴露为具有单个元素的数组
	 * 
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getParameterMap()
	 */
	Map<String, String[]> getParameterMap();

	/**
	 * Return the primary Locale for this request.
	 * <p>
	 *  返回此请求的主要区域设置
	 * 
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getLocale()
	 */
	Locale getLocale();

	/**
	 * Return the context path for this request
	 * (usually the base path that the current web application is mapped to).
	 * <p>
	 *  返回此请求的上下文路径(通常是当前Web应用程序映射到的基本路径)
	 * 
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getContextPath()
	 */
	String getContextPath();

	/**
	 * Return the remote user for this request, if any.
	 * <p>
	 *  返回该请求的远程用户(如果有)
	 * 
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getRemoteUser()
	 */
	String getRemoteUser();

	/**
	 * Return the user principal for this request, if any.
	 * <p>
	 *  返回此请求的用户主体(如果有)
	 * 
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getUserPrincipal()
	 */
	Principal getUserPrincipal();

	/**
	 * Determine whether the user is in the given role for this request.
	 * <p>
	 *  确定用户是否处于此请求的给定角色
	 * 
	 * 
	 * @see javax.servlet.http.HttpServletRequest#isUserInRole(String)
	 */
	boolean isUserInRole(String role);

	/**
	 * Return whether this request has been sent over a secure transport
	 * mechanism (such as SSL).
	 * <p>
	 *  返回此请求是否通过安全的传输机制发送(如SSL)
	 * 
	 * 
	 * @see javax.servlet.http.HttpServletRequest#isSecure()
	 */
	boolean isSecure();

	/**
	 * Check whether the requested resource has been modified given the
	 * supplied last-modified timestamp (as determined by the application).
	 * <p>This will also transparently set the "Last-Modified" response header
	 * and HTTP status when applicable.
	 * <p>Typical usage:
	 * <pre class="code">
	 * public String myHandleMethod(WebRequest webRequest, Model model) {
	 *   long lastModified = // application-specific calculation
	 *   if (request.checkNotModified(lastModified)) {
	 *     // shortcut exit - no further processing necessary
	 *     return null;
	 *   }
	 *   // further request processing, actually building content
	 *   model.addAttribute(...);
	 *   return "myViewName";
	 * }</pre>
	 * <p>This method works with conditional GET/HEAD requests, but
	 * also with conditional POST/PUT/DELETE requests.
	 * <p><strong>Note:</strong> you can use either
	 * this {@code #checkNotModified(long)} method; or
	 * {@link #checkNotModified(String)}. If you want enforce both
	 * a strong entity tag and a Last-Modified value,
	 * as recommended by the HTTP specification,
	 * then you should use {@link #checkNotModified(String, long)}.
	 * <p>If the "If-Modified-Since" header is set but cannot be parsed
	 * to a date value, this method will ignore the header and proceed
	 * with setting the last-modified timestamp on the response.
	 * <p>
	 * 鉴于所提供的最后修改的时间戳(由应用程序确定),请检查所请求的资源是否已被修改。<p>这也将在适用时透明地设置"Last-Modified"响应头和HTTP状态<p>典型用法：
	 * <pre class="code">
	 * public String myHandleMethod(WebRequest webRequest,Model model){long lastModified = //应用程序特定的计算if(requestcheckNotModified(lastModified)){//快捷方式退出 - 无需进一步处理必需返回null; }
	 *  //进一步请求处理,实际构建内容modeladdAttribute();返回"myViewName"; } </pre> <p>此方法适用于条件GET / HEAD请求,但也适用于条件POST / P
	 * UT / DELETE请求<p> <strong>注意：</strong>可以使用{@code #checkNotModified (long)}方法;或{@link #checkNotModified(String)}
	 * 如果要强制实体标签和Last-Modified值,按照HTTP规范的建议,则应使用{@link #checkNotModified(String,long)}<p>如果设置了"If-Modified-S
	 * ince"标题,但无法将其解析为日期值,则此方法将忽略标题,并继续在响应时设置最后修改的时间戳。
	 * 
	 * 
	 * @param lastModifiedTimestamp the last-modified timestamp in
	 * milliseconds that the application determined for the underlying
	 * resource
	 * @return whether the request qualifies as not modified,
	 * allowing to abort request processing and relying on the response
	 * telling the client that the content has not been modified
	 */
	boolean checkNotModified(long lastModifiedTimestamp);

	/**
	 * Check whether the requested resource has been modified given the
	 * supplied {@code ETag} (entity tag), as determined by the application.
	 * <p>This will also transparently set the "ETag" response header
	 * and HTTP status when applicable.
	 * <p>Typical usage:
	 * <pre class="code">
	 * public String myHandleMethod(WebRequest webRequest, Model model) {
	 *   String eTag = // application-specific calculation
	 *   if (request.checkNotModified(eTag)) {
	 *     // shortcut exit - no further processing necessary
	 *     return null;
	 *   }
	 *   // further request processing, actually building content
	 *   model.addAttribute(...);
	 *   return "myViewName";
	 * }</pre>
	 * <p><strong>Note:</strong> you can use either
	 * this {@code #checkNotModified(String)} method; or
	 * {@link #checkNotModified(long)}. If you want enforce both
	 * a strong entity tag and a Last-Modified value,
	 * as recommended by the HTTP specification,
	 * then you should use {@link #checkNotModified(String, long)}.
	 * <p>
	 * 检查请求的资源是否已被修改,因为提供的{@code ETag}(实体标签),由应用程序确定。<p>这也将透明地设置"ETag"响应头和HTTP状态(适用时)<p>典型用法：
	 * <pre class="code">
	 * public String myHandleMethod(WebRequest webRequest,Model model){String eTag = //应用程序特定的计算if(requestcheckNotModified(eTag)){//快捷方式退出 - 无需进一步处理必需返回null; }
	 *  //进一步请求处理,实际构建内容modeladdAttribute();返回"myViewName"; } </pre> <p> <strong>注意：</strong>可以使用这个{@code #checkNotModified(String)}
	 * 方法;或{@link #checkNotModified(long)}如果要强制实体标签和Last-Modified值,按照HTTP规范的建议,则应使用{@link #checkNotModified(String,long)}
	 * 。
	 * 
	 * 
	 * @param etag the entity tag that the application determined
	 * for the underlying resource. This parameter will be padded
	 * with quotes (") if necessary.
	 * @return true if the request does not require further processing.
	 */
	boolean checkNotModified(String etag);

	/**
	 * Check whether the requested resource has been modified given the
	 * supplied {@code ETag} (entity tag) and last-modified timestamp,
	 * as determined by the application.
	 * <p>This will also transparently set the "ETag" and "Last-Modified"
	 * response headers, and HTTP status when applicable.
	 * <p>Typical usage:
	 * <pre class="code">
	 * public String myHandleMethod(WebRequest webRequest, Model model) {
	 *   String eTag = // application-specific calculation
	 *   long lastModified = // application-specific calculation
	 *   if (request.checkNotModified(eTag, lastModified)) {
	 *     // shortcut exit - no further processing necessary
	 *     return null;
	 *   }
	 *   // further request processing, actually building content
	 *   model.addAttribute(...);
	 *   return "myViewName";
	 * }</pre>
	 * <p>This method works with conditional GET/HEAD requests, but
	 * also with conditional POST/PUT/DELETE requests.
	 * <p><strong>Note:</strong> The HTTP specification recommends
	 * setting both ETag and Last-Modified values, but you can also
	 * use {@code #checkNotModified(String)} or
	 * {@link #checkNotModified(long)}.
	 * <p>
	 * 根据应用程序确定的提供的{@code ETag}(实体标签)和最后修改的时间戳,检查请求的资源是否已被修改。
	 * 这也将透明地设置"ETag"和"Last-Modified"响应标题和HTTP状态适用时<p>典型用法：。
	 * <pre class="code">
	 * public String myHandleMethod(WebRequest webRequest,Model model){String eTag = //应用程序特定的计算long lastModified = //应用程序特定的计算if(requestcheckNotModified(eTag,lastModified)){//快捷方式退出 - 无需进一步处理必需返回null ; }
	 *  //进一步请求处理,实际构建内容modeladdAttribute();返回"myViewName"; } </pre> <p>此方法适用于条件GET / HEAD请求,但也适用于条件POST / P
	 * UT / DELETE请求<p> <strong>注意：</strong> HTTP规范建议将ETag和Last-修改值,但您也可以使用{@code #checkNotModified(String)}
	 * 
	 * @param etag the entity tag that the application determined
	 * for the underlying resource. This parameter will be padded
	 * with quotes (") if necessary.
	 * @param lastModifiedTimestamp the last-modified timestamp in
	 * milliseconds that the application determined for the underlying
	 * resource
	 * @return true if the request does not require further processing.
	 * @since 4.2
	 */
	boolean checkNotModified(String etag, long lastModifiedTimestamp);

	/**
	 * Get a short description of this request,
	 * typically containing request URI and session id.
	 * <p>
	 * 或{@link #checkNotModified(long)}。
	 * 
	 * 
	 * @param includeClientInfo whether to include client-specific
	 * information such as session id and user name
	 * @return the requested description as String
	 */
	String getDescription(boolean includeClientInfo);

}
