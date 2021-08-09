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

package org.springframework.web.multipart;

import javax.servlet.http.HttpServletRequest;

/**
 * A strategy interface for multipart file upload resolution in accordance
 * with <a href="http://www.ietf.org/rfc/rfc1867.txt">RFC 1867</a>.
 * Implementations are typically usable both within an application context
 * and standalone.
 *
 * <p>There are two concrete implementations included in Spring, as of Spring 3.1:
 * <ul>
 * <li>{@link org.springframework.web.multipart.commons.CommonsMultipartResolver}
 * for Apache Commons FileUpload
 * <li>{@link org.springframework.web.multipart.support.StandardServletMultipartResolver}
 * for the Servlet 3.0+ Part API
 * </ul>
 *
 * <p>There is no default resolver implementation used for Spring
 * {@link org.springframework.web.servlet.DispatcherServlet DispatcherServlets},
 * as an application might choose to parse its multipart requests itself. To define
 * an implementation, create a bean with the id "multipartResolver" in a
 * {@link org.springframework.web.servlet.DispatcherServlet DispatcherServlet's}
 * application context. Such a resolver gets applied to all requests handled
 * by that {@link org.springframework.web.servlet.DispatcherServlet}.
 *
 * <p>If a {@link org.springframework.web.servlet.DispatcherServlet} detects a
 * multipart request, it will resolve it via the configured {@link MultipartResolver}
 * and pass on a wrapped {@link javax.servlet.http.HttpServletRequest}. Controllers
 * can then cast their given request to the {@link MultipartHttpServletRequest}
 * interface, which allows for access to any {@link MultipartFile MultipartFiles}.
 * Note that this cast is only supported in case of an actual multipart request.
 *
 * <pre class="code">
 * public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) {
 *   MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
 *   MultipartFile multipartFile = multipartRequest.getFile("image");
 *   ...
 * }</pre>
 *
 * Instead of direct access, command or form controllers can register a
 * {@link org.springframework.web.multipart.support.ByteArrayMultipartFileEditor}
 * or {@link org.springframework.web.multipart.support.StringMultipartFileEditor}
 * with their data binder, to automatically apply multipart content to form
 * bean properties.
 *
 * <p>As an alternative to using a {@link MultipartResolver} with a
 * {@link org.springframework.web.servlet.DispatcherServlet},
 * a {@link org.springframework.web.multipart.support.MultipartFilter} can be
 * registered in {@code web.xml}. It will delegate to a corresponding
 * {@link MultipartResolver} bean in the root application context. This is mainly
 * intended for applications that do not use Spring's own web MVC framework.
 *
 * <p>Note: There is hardly ever a need to access the {@link MultipartResolver}
 * itself from application code. It will simply do its work behind the scenes,
 * making {@link MultipartHttpServletRequest MultipartHttpServletRequests}
 * available to controllers.
 *
 * <p>
 *  根据<a href=\"http://wwwietforg/rfc/rfc1867txt\"> RFC 1867 </a>进行多部分文件上传解决的策略界面实现通常在应用程序环境和独立环境中都可用
 * 
 * 在Spring中有两个具体的实现,从Spring 31开始：
 * <ul>
 *  对于Servlet 30+ Part API,Apache Commons FileUpload <li> {@ link orgspringframeworkwebmultipartcommonsCommonsMultipartResolver}
 *  <li> {@ link orgspringframeworkwebmultipartsupportStandardServletMultipartResolver}。
 * </ul>
 * 
 * <p>没有用于Spring {@link orgspringframeworkwebservletDispatcherServlet DispatcherServlets}的默认解析器实现,因为应用程序
 * 可能会选择解析其多部分请求本身要定义一个实现,请在{@link orgspringframeworkwebservletDispatcherServlet中创建一个ID为"multipartResolver"的bean DispatcherServlet的应用程序上下文这样的解析器被应用于由{@link orgspringframeworkwebservletDispatcherServlet}
 * 处理的所有请求。
 * 
 * <p>如果{@link orgspringframeworkwebservletDispatcherServlet}检测到多部分请求,它将通过配置的{@link MultipartResolver}解析
 * 它,并传递一个包装的{@link javaxservlethttpHttpServletRequest}控制器,然后可以将其给定的请求转发给{@link MultipartHttpServletRequest }
 * 接口,允许访问任何{@link MultipartFile MultipartFiles}注意,只有在实际的多部分请求的情况下,才支持此转换。
 * 
 * <pre class="code">
 *  public ModelAndView handleRequest(HttpServletRequest request,HttpServletResponse response){MultipartHttpServletRequest multipartRequest =(MultipartHttpServletRequest)请求; MultipartFile multipartFile = multipartRequestgetFile("image"); }
 *  </PRE>。
 * 
 * 
 * @author Juergen Hoeller
 * @author Trevor D. Cook
 * @since 29.09.2003
 * @see MultipartHttpServletRequest
 * @see MultipartFile
 * @see org.springframework.web.multipart.commons.CommonsMultipartResolver
 * @see org.springframework.web.multipart.support.ByteArrayMultipartFileEditor
 * @see org.springframework.web.multipart.support.StringMultipartFileEditor
 * @see org.springframework.web.servlet.DispatcherServlet
 */
public interface MultipartResolver {

	/**
	 * Determine if the given request contains multipart content.
	 * <p>Will typically check for content type "multipart/form-data", but the actually
	 * accepted requests might depend on the capabilities of the resolver implementation.
	 * <p>
	 * 命令或表单控制器可以使用数据绑定器注册一个{@link orgspringframeworkwebmultipartsupportByteArrayMultipartFileEditor}或{@link orgspringframeworkwebmultipartsupportStringMultipartFileEditor}
	 * 来自动应用多部分内容来形成bean属性,而不是直接访问。
	 * 
	 *  <p>作为将{@link MultipartResolver}与{@link orgspringframeworkwebservletDispatcherServlet}一起使用的替代方法,{@link orgspringframeworkwebmultipartsupportMultipartFilter}
	 * 可以在{@code webxml}中注册,它将委托给相应的{@link MultipartResolver} bean根应用程序上下文这主要用于不使用Spring自己的Web MVC框架的应用程序。
	 * 
	 * 注意：几乎没有必要从应用程序代码访问{@link MultipartResolver}本身,它将简单地在幕后进行工作,使得{@link MultipartHttpServletRequest MultipartHttpServletRequests}
	 * 可用于控制器。
	 * 
	 * 
	 * @param request the servlet request to be evaluated
	 * @return whether the request contains multipart content
	 */
	boolean isMultipart(HttpServletRequest request);

	/**
	 * Parse the given HTTP request into multipart files and parameters,
	 * and wrap the request inside a
	 * {@link org.springframework.web.multipart.MultipartHttpServletRequest}
	 * object that provides access to file descriptors and makes contained
	 * parameters accessible via the standard ServletRequest methods.
	 * <p>
	 *  确定给定的请求是否包含多部分内容通常会检查内容类型"multipart / form-data",但实际接受的请求可能取决于解析器实现的功能
	 * 
	 * 
	 * @param request the servlet request to wrap (must be of a multipart content type)
	 * @return the wrapped servlet request
	 * @throws MultipartException if the servlet request is not multipart, or if
	 * implementation-specific problems are encountered (such as exceeding file size limits)
	 * @see MultipartHttpServletRequest#getFile
	 * @see MultipartHttpServletRequest#getFileNames
	 * @see MultipartHttpServletRequest#getFileMap
	 * @see javax.servlet.http.HttpServletRequest#getParameter
	 * @see javax.servlet.http.HttpServletRequest#getParameterNames
	 * @see javax.servlet.http.HttpServletRequest#getParameterMap
	 */
	MultipartHttpServletRequest resolveMultipart(HttpServletRequest request) throws MultipartException;

	/**
	 * Cleanup any resources used for the multipart handling,
	 * like a storage for the uploaded files.
	 * <p>
	 *  将给定的HTTP请求解析为多部分文件和参数,并将请求包装在一个{@link orgspringframeworkwebmultipartMultipartHttpServletRequest}对象中,
	 * 该对象提供对文件描述符的访问,并使包含的参数可通过标准ServletRequest方法访问。
	 * 
	 * 
	 * @param request the request to cleanup resources for
	 */
	void cleanupMultipart(MultipartHttpServletRequest request);

}
