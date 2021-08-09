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

package org.springframework.web.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.request.async.WebAsyncManager;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.util.WebUtils;

/**
 * Filter base class that aims to guarantee a single execution per request
 * dispatch, on any servlet container. It provides a {@link #doFilterInternal}
 * method with HttpServletRequest and HttpServletResponse arguments.
 *
 * <p>As of Servlet 3.0, a filter may be invoked as part of a
 * {@link javax.servlet.DispatcherType#REQUEST REQUEST} or
 * {@link javax.servlet.DispatcherType#ASYNC ASYNC} dispatches that occur in
 * separate threads. A filter can be configured in {@code web.xml} whether it
 * should be involved in async dispatches. However, in some cases servlet
 * containers assume different default configuration. Therefore sub-classes can
 * override the method {@link #shouldNotFilterAsyncDispatch()} to declare
 * statically if they should indeed be invoked, <em>once</em>, during both types
 * of dispatches in order to provide thread initialization, logging, security,
 * and so on. This mechanism complements and does not replace the need to
 * configure a filter in {@code web.xml} with dispatcher types.
 *
 * <p>Subclasses may use {@link #isAsyncDispatch(HttpServletRequest)} to
 * determine when a filter is invoked as part of an async dispatch, and use
 * {@link #isAsyncStarted(HttpServletRequest)} to determine when the request
 * has been placed in async mode and therefore the current dispatch won't be
 * the last one for the given request.
 *
 * <p>Yet another dispatch type that also occurs in its own thread is
 * {@link javax.servlet.DispatcherType#ERROR ERROR}. Subclasses can override
 * {@link #shouldNotFilterErrorDispatch()} if they wish to declare statically
 * if they should be invoked <em>once</em> during error dispatches.
 *
 * <p>The {@link #getAlreadyFilteredAttributeName} method determines how to
 * identify that a request is already filtered. The default implementation is
 * based on the configured name of the concrete filter instance.
 *
 * <p>
 *  过滤器基类,旨在保证在任何servlet容器上单个执行每个请求分派它提供了一个带有HttpServletRequest和HttpServletResponse参数的{@link #doFilterInternal}
 * 方法。
 * 
 * <p>从Servlet 30开始,过滤器可以作为{@link javaxservletDispatcherType#REQUEST REQUEST}或{@link javaxservletDispatcherType#ASYNC ASYNC}
 * 调度的一部分进行调用,该调度发生在单独的线程中可以在{@code webxml中配置过滤器}是否应该参与异步调度然而,在某些情况下,servlet容器承担不同的默认配置因此,子类可以覆盖方法{@link #shouldNotFilterAsyncDispatch()}
 * 来静态地声明它们是否应该被调用,一次在这两种类型的调度中,为了提供线程初始化,日志记录,安全性等,这个机制是补充并且不代替在{@code webxml}中配置调度器类型的过滤器的需要。
 * 
 * <p>子类可以使用{@link #isAsyncDispatch(HttpServletRequest)}来确定何时调用过滤器作为异步调度的一部分,并使用{@link #isAsyncStarted(HttpServletRequest)}
 * 来确定请求何时被置于异步模式,因此当前调度不会是给定请求的最后一个。
 * 
 *  <p>另外在其自己的线程中发生的另一个dispatch类型是{@link javaxservletDispatcherType#ERROR ERROR}如果他们希望静态地声明它们是否应该被调用<em>
 * ,那么子类可以覆盖{@link #shouldNotFilterErrorDispatch()} < / em>错误调度。
 * 
 * <p> {@link #getAlreadyFilteredAttributeName}方法确定如何识别请求已被过滤默认实现基于具体过滤器实例的配置名称
 * 
 * 
 * @author Juergen Hoeller
 * @author Rossen Stoyanchev
 * @since 06.12.2003
 */
public abstract class OncePerRequestFilter extends GenericFilterBean {

	/**
	 * Suffix that gets appended to the filter name for the
	 * "already filtered" request attribute.
	 * <p>
	 *  后缀被附加到"已过滤"请求属性的过滤器名称
	 * 
	 * 
	 * @see #getAlreadyFilteredAttributeName
	 */
	public static final String ALREADY_FILTERED_SUFFIX = ".FILTERED";


	/**
	 * This {@code doFilter} implementation stores a request attribute for
	 * "already filtered", proceeding without filtering again if the
	 * attribute is already there.
	 * <p>
	 *  此{@code doFilter}实现存储"已过滤"的请求属性,如果属性已经存在,则不再过滤
	 * 
	 * 
	 * @see #getAlreadyFilteredAttributeName
	 * @see #shouldNotFilter
	 * @see #doFilterInternal
	 */
	@Override
	public final void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
			throw new ServletException("OncePerRequestFilter just supports HTTP requests");
		}
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		String alreadyFilteredAttributeName = getAlreadyFilteredAttributeName();
		boolean hasAlreadyFilteredAttribute = request.getAttribute(alreadyFilteredAttributeName) != null;

		if (hasAlreadyFilteredAttribute || skipDispatch(httpRequest) || shouldNotFilter(httpRequest)) {

			// Proceed without invoking this filter...
			filterChain.doFilter(request, response);
		}
		else {
			// Do invoke this filter...
			request.setAttribute(alreadyFilteredAttributeName, Boolean.TRUE);
			try {
				doFilterInternal(httpRequest, httpResponse, filterChain);
			}
			finally {
				// Remove the "already filtered" request attribute for this request.
				request.removeAttribute(alreadyFilteredAttributeName);
			}
		}
	}


	private boolean skipDispatch(HttpServletRequest request) {
		if (isAsyncDispatch(request) && shouldNotFilterAsyncDispatch()) {
			return true;
		}
		if (request.getAttribute(WebUtils.ERROR_REQUEST_URI_ATTRIBUTE) != null && shouldNotFilterErrorDispatch()) {
			return true;
		}
		return false;
	}

	/**
	 * The dispatcher type {@code javax.servlet.DispatcherType.ASYNC} introduced
	 * in Servlet 3.0 means a filter can be invoked in more than one thread over
	 * the course of a single request. This method returns {@code true} if the
	 * filter is currently executing within an asynchronous dispatch.
	 * <p>
	 *  在Servlet 30中引入的调度器类型{@code javaxservletDispatcherTypeASYNC}意味着可以在单个请求的过程中在多个线程中调用过滤器。
	 * 如果过滤器当前正在异步调度中执行,则此方法返回{@code true}。
	 * 
	 * 
	 * @param request the current request
	 * @since 3.2
	 * @see WebAsyncManager#hasConcurrentResult()
	 */
	protected boolean isAsyncDispatch(HttpServletRequest request) {
		return WebAsyncUtils.getAsyncManager(request).hasConcurrentResult();
	}

	/**
	 * Whether request processing is in asynchronous mode meaning that the
	 * response will not be committed after the current thread is exited.
	 * <p>
	 * 请求处理是否处于异步模式,这意味着在退出当前线程之后响应将不会被提交
	 * 
	 * 
	 * @param request the current request
	 * @since 3.2
	 * @see WebAsyncManager#isConcurrentHandlingStarted()
	 */
	protected boolean isAsyncStarted(HttpServletRequest request) {
		return WebAsyncUtils.getAsyncManager(request).isConcurrentHandlingStarted();
	}

	/**
	 * Return the name of the request attribute that identifies that a request
	 * is already filtered.
	 * <p>The default implementation takes the configured name of the concrete filter
	 * instance and appends ".FILTERED". If the filter is not fully initialized,
	 * it falls back to its class name.
	 * <p>
	 *  返回标识请求已被过滤的请求属性的名称<p>默认实现采用具体过滤器实例的配置名称并附加"FILTERED"如果过滤器未完全初始化,则返回到其类名称
	 * 
	 * 
	 * @see #getFilterName
	 * @see #ALREADY_FILTERED_SUFFIX
	 */
	protected String getAlreadyFilteredAttributeName() {
		String name = getFilterName();
		if (name == null) {
			name = getClass().getName();
		}
		return name + ALREADY_FILTERED_SUFFIX;
	}

	/**
	 * Can be overridden in subclasses for custom filtering control,
	 * returning {@code true} to avoid filtering of the given request.
	 * <p>The default implementation always returns {@code false}.
	 * <p>
	 *  可以在子类中覆盖自定义过滤控制,返回{@code true}以避免过滤给定的请求<p>默认实现总是返回{@code false}
	 * 
	 * 
	 * @param request current HTTP request
	 * @return whether the given request should <i>not</i> be filtered
	 * @throws ServletException in case of errors
	 */
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		return false;
	}

	/**
	 * The dispatcher type {@code javax.servlet.DispatcherType.ASYNC} introduced
	 * in Servlet 3.0 means a filter can be invoked in more than one thread
	 * over the course of a single request. Some filters only need to filter
	 * the initial thread (e.g. request wrapping) while others may need
	 * to be invoked at least once in each additional thread for example for
	 * setting up thread locals or to perform final processing at the very end.
	 * <p>Note that although a filter can be mapped to handle specific dispatcher
	 * types via {@code web.xml} or in Java through the {@code ServletContext},
	 * servlet containers may enforce different defaults with regards to
	 * dispatcher types. This flag enforces the design intent of the filter.
	 * <p>The default return value is "true", which means the filter will not be
	 * invoked during subsequent async dispatches. If "false", the filter will
	 * be invoked during async dispatches with the same guarantees of being
	 * invoked only once during a request within a single thread.
	 * <p>
	 * Servlet 30中引入的调度器类型{@code javaxservletDispatcherTypeASYNC}意味着可以在单个请求的过程中在多个线程中调用过滤器。
	 * 一些过滤器只需要过滤初始线程(例如请求包装),而其他可能需要在每个附加线程中调用至少一次,例如用于设置线程本地或最终执行最终处理。
	 * <p>请注意,虽然过滤器可以映射为通过{@code webxml}或Java中的特定调度器类型来处理{@code ServletContext},servlet容器可能会对分派器类型执行不同的默认值。
	 * 此标志强制过滤器的设计意图<p>默认返回值为"true",这意味着在后续异步调度期间不会调用过滤器如果为"false",则过滤器将在异步调度期间被调用,同样的保证在请求期间仅被调用一次单线程。
	 * 
	 * 
	 * @since 3.2
	 */
	protected boolean shouldNotFilterAsyncDispatch() {
		return true;
	}

	/**
	 * Whether to filter error dispatches such as when the servlet container
	 * processes and error mapped in {@code web.xml}. The default return value
	 * is "true", which means the filter will not be invoked in case of an error
	 * dispatch.
	 * <p>
	 * 是否过滤错误调度,例如servlet容器处理和错误映射到{@code webxml}时的错误调度默认返回值为"true",这意味着在发生错误调度时不会调用过滤器
	 * 
	 * 
	 * @since 3.2
	 */
	protected boolean shouldNotFilterErrorDispatch() {
		return true;
	}


	/**
	 * Same contract as for {@code doFilter}, but guaranteed to be
	 * just invoked once per request within a single request thread.
	 * See {@link #shouldNotFilterAsyncDispatch()} for details.
	 * <p>Provides HttpServletRequest and HttpServletResponse arguments instead of the
	 * default ServletRequest and ServletResponse ones.
	 * <p>
	 *  与{@code doFilter}相同的合同,但保证在单个请求线程中只需调用一次请参阅{@link #shouldNotFilterAsyncDispatch()}以获取详细信息<p>提供HttpSe
	 * rvletRequest和HttpServletResponse参数,而不是默认的ServletRequest和ServletResponse那些。
	 */
	protected abstract void doFilterInternal(
			HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException;

}
