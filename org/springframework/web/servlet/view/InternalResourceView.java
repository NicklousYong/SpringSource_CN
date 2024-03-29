/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2014 the original author or authors.
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

package org.springframework.web.servlet.view;

import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

/**
 * Wrapper for a JSP or other resource within the same web application.
 * Exposes model objects as request attributes and forwards the request to
 * the specified resource URL using a {@link javax.servlet.RequestDispatcher}.
 *
 * <p>A URL for this view is supposed to specify a resource within the web
 * application, suitable for RequestDispatcher's {@code forward} or
 * {@code include} method.
 *
 * <p>If operating within an already included request or within a response that
 * has already been committed, this view will fall back to an include instead of
 * a forward. This can be enforced by calling {@code response.flushBuffer()}
 * (which will commit the response) before rendering the view.
 *
 * <p>Typical usage with {@link InternalResourceViewResolver} looks as follows,
 * from the perspective of the DispatcherServlet context definition:
 *
 * <pre class="code">&lt;bean id="viewResolver" class="org.springframework.web.servlet.view.InternalResourceViewResolver"&gt;
 *   &lt;property name="prefix" value="/WEB-INF/jsp/"/&gt;
 *   &lt;property name="suffix" value=".jsp"/&gt;
 * &lt;/bean&gt;</pre>
 *
 * Every view name returned from a handler will be translated to a JSP
 * resource (for example: "myView" -> "/WEB-INF/jsp/myView.jsp"), using
 * this view class by default.
 *
 * <p>
 *  JSP或同一Web应用程序中的其他资源的包装器将模型对象作为请求属性展开,并使用{@link javaxservletRequestDispatcher}将请求转发到指定的资源URL
 * 
 * <p>此视图的URL应该指定Web应用程序中的资源,适用于RequestDispatcher的{@code forward}或{@code include}方法
 * 
 *  <p>如果在已经包含的请求中或在已经提交的响应中操作,则此视图将返回到包含而不是转发。这可以通过调用{@code responseflushBuffer()}来执行响应),然后再呈现视图
 * 
 *  <p>从DispatcherServlet上下文定义的角度来看,{@link InternalResourceViewResolver}的典型用法如下所示：
 * 
 * <pre class ="code">&lt; bean id ="viewResolver"class ="orgspringframeworkwebservletviewInternalResour
 * ceViewResolver"&gt; &lt; property name ="prefix"value ="/ WEB-INF / jsp /"/&gt; &lt; property name ="
 * suffix"value ="jsp"/&gt; &LT; /豆腐&GT; </PRE>。
 * 
 *  从处理程序返回的每个视图名称将被转换为JSP资源(例如："myView" - >"/ WEB-INF / jsp / myViewjsp"),默认情况下使用此视图类
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @see javax.servlet.RequestDispatcher#forward
 * @see javax.servlet.RequestDispatcher#include
 * @see javax.servlet.ServletResponse#flushBuffer
 * @see InternalResourceViewResolver
 * @see JstlView
 */
public class InternalResourceView extends AbstractUrlBasedView {

	private boolean alwaysInclude = false;

	private boolean preventDispatchLoop = false;


	/**
	 * Constructor for use as a bean.
	 * <p>
	 *  用作bean的构造方法
	 * 
	 * 
	 * @see #setUrl
	 * @see #setAlwaysInclude
	 */
	public InternalResourceView() {
	}

	/**
	 * Create a new InternalResourceView with the given URL.
	 * <p>
	 *  使用给定的URL创建一个新的InternalResourceView
	 * 
	 * 
	 * @param url the URL to forward to
	 * @see #setAlwaysInclude
	 */
	public InternalResourceView(String url) {
		super(url);
	}

	/**
	 * Create a new InternalResourceView with the given URL.
	 * <p>
	 *  使用给定的URL创建一个新的InternalResourceView
	 * 
	 * 
	 * @param url the URL to forward to
	 * @param alwaysInclude whether to always include the view rather than forward to it
	 */
	public InternalResourceView(String url, boolean alwaysInclude) {
		super(url);
		this.alwaysInclude = alwaysInclude;
	}


	/**
	 * Specify whether to always include the view rather than forward to it.
	 * <p>Default is "false". Switch this flag on to enforce the use of a
	 * Servlet include, even if a forward would be possible.
	 * <p>
	 *  指定是否始终包含视图而不是转发到视图<p>默认值为"false"将此标志打开以强制使用Servlet包含,即使可以进行转发
	 * 
	 * 
	 * @see javax.servlet.RequestDispatcher#forward
	 * @see javax.servlet.RequestDispatcher#include
	 * @see #useInclude(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void setAlwaysInclude(boolean alwaysInclude) {
		this.alwaysInclude = alwaysInclude;
	}

	/**
	 * Set whether to explicitly prevent dispatching back to the
	 * current handler path.
	 * <p>Default is "false". Switch this to "true" for convention-based
	 * views where a dispatch back to the current handler path is a
	 * definitive error.
	 * <p>
	 * 设置是否显式阻止调度回当前的处理程序路径<p>默认值为"false"将基于约定的视图切换为"true",其中回调到当前处理程序路径是一个确定的错误
	 * 
	 */
	public void setPreventDispatchLoop(boolean preventDispatchLoop) {
		this.preventDispatchLoop = preventDispatchLoop;
	}

	/**
	 * An ApplicationContext is not strictly required for InternalResourceView.
	 * <p>
	 *  InternalResourceView不需要ApplicationContext
	 * 
	 */
	@Override
	protected boolean isContextRequired() {
		return false;
	}


	/**
	 * Render the internal resource given the specified model.
	 * This includes setting the model as request attributes.
	 * <p>
	 *  渲染指定模型的内部资源包括将模型设置为请求属性
	 * 
	 */
	@Override
	protected void renderMergedOutputModel(
			Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		// Expose the model object as request attributes.
		exposeModelAsRequestAttributes(model, request);

		// Expose helpers as request attributes, if any.
		exposeHelpers(request);

		// Determine the path for the request dispatcher.
		String dispatcherPath = prepareForRendering(request, response);

		// Obtain a RequestDispatcher for the target resource (typically a JSP).
		RequestDispatcher rd = getRequestDispatcher(request, dispatcherPath);
		if (rd == null) {
			throw new ServletException("Could not get RequestDispatcher for [" + getUrl() +
					"]: Check that the corresponding file exists within your web application archive!");
		}

		// If already included or response already committed, perform include, else forward.
		if (useInclude(request, response)) {
			response.setContentType(getContentType());
			if (logger.isDebugEnabled()) {
				logger.debug("Including resource [" + getUrl() + "] in InternalResourceView '" + getBeanName() + "'");
			}
			rd.include(request, response);
		}

		else {
			// Note: The forwarded resource is supposed to determine the content type itself.
			if (logger.isDebugEnabled()) {
				logger.debug("Forwarding to resource [" + getUrl() + "] in InternalResourceView '" + getBeanName() + "'");
			}
			rd.forward(request, response);
		}
	}

	/**
	 * Expose helpers unique to each rendering operation. This is necessary so that
	 * different rendering operations can't overwrite each other's contexts etc.
	 * <p>Called by {@link #renderMergedOutputModel(Map, HttpServletRequest, HttpServletResponse)}.
	 * The default implementation is empty. This method can be overridden to add
	 * custom helpers as request attributes.
	 * <p>
	 *  公开每个渲染操作唯一的帮助器这是必要的,以便不同的渲染操作不能覆盖对方的上下文等。
	 * <p>由{@link #renderMergedOutputModel(Map,HttpServletRequest,HttpServletResponse))调用}默认实现为空此方法可以被覆盖以添加自
	 * 定义助手作为请求属性。
	 *  公开每个渲染操作唯一的帮助器这是必要的,以便不同的渲染操作不能覆盖对方的上下文等。
	 * 
	 * 
	 * @param request current HTTP request
	 * @throws Exception if there's a fatal error while we're adding attributes
	 * @see #renderMergedOutputModel
	 * @see JstlView#exposeHelpers
	 */
	protected void exposeHelpers(HttpServletRequest request) throws Exception {
	}

	/**
	 * Prepare for rendering, and determine the request dispatcher path
	 * to forward to (or to include).
	 * <p>This implementation simply returns the configured URL.
	 * Subclasses can override this to determine a resource to render,
	 * typically interpreting the URL in a different manner.
	 * <p>
	 * 准备渲染,并确定要转发到(或包含)的请求分派器路径<p>此实现只返回配置的URL子类可以覆盖此值以确定要呈现的资源,通常以不同的方式解释URL
	 * 
	 * 
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @return the request dispatcher path to use
	 * @throws Exception if preparations failed
	 * @see #getUrl()
	 */
	protected String prepareForRendering(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String path = getUrl();
		if (this.preventDispatchLoop) {
			String uri = request.getRequestURI();
			if (path.startsWith("/") ? uri.equals(path) : uri.equals(StringUtils.applyRelativePath(uri, path))) {
				throw new ServletException("Circular view path [" + path + "]: would dispatch back " +
						"to the current handler URL [" + uri + "] again. Check your ViewResolver setup! " +
						"(Hint: This may be the result of an unspecified view, due to default view name generation.)");
			}
		}
		return path;
	}

	/**
	 * Obtain the RequestDispatcher to use for the forward/include.
	 * <p>The default implementation simply calls
	 * {@link HttpServletRequest#getRequestDispatcher(String)}.
	 * Can be overridden in subclasses.
	 * <p>
	 *  获取RequestDispatcher用于forward / include <p>默认实现只需调用{@link HttpServletRequest#getRequestDispatcher(String)}
	 * 可以在子类中被覆盖。
	 * 
	 * 
	 * @param request current HTTP request
	 * @param path the target URL (as returned from {@link #prepareForRendering})
	 * @return a corresponding RequestDispatcher
	 */
	protected RequestDispatcher getRequestDispatcher(HttpServletRequest request, String path) {
		return request.getRequestDispatcher(path);
	}

	/**
	 * Determine whether to use RequestDispatcher's {@code include} or
	 * {@code forward} method.
	 * <p>Performs a check whether an include URI attribute is found in the request,
	 * indicating an include request, and whether the response has already been committed.
	 * In both cases, an include will be performed, as a forward is not possible anymore.
	 * <p>
	 * 确定是否使用RequestDispatcher的{@code include}或{@code forward}方法<p>执行检查请求中是否找到包含URI属性,指示包含请求以及响应是否已提交在这两种情况下
	 * 将会执行包含,因为前进是不可能的。
	 * 
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @return {@code true} for include, {@code false} for forward
	 * @see javax.servlet.RequestDispatcher#forward
	 * @see javax.servlet.RequestDispatcher#include
	 * @see javax.servlet.ServletResponse#isCommitted
	 * @see org.springframework.web.util.WebUtils#isIncludeRequest
	 */
	protected boolean useInclude(HttpServletRequest request, HttpServletResponse response) {
		return (this.alwaysInclude || WebUtils.isIncludeRequest(request) || response.isCommitted());
	}

}
