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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Servlet Filter that exposes the request to the current thread,
 * through both {@link org.springframework.context.i18n.LocaleContextHolder} and
 * {@link RequestContextHolder}. To be registered as filter in {@code web.xml}.
 *
 * <p>Alternatively, Spring's {@link org.springframework.web.context.request.RequestContextListener}
 * and Spring's {@link org.springframework.web.servlet.DispatcherServlet} also expose
 * the same request context to the current thread.
 *
 * <p>This filter is mainly for use with third-party servlets, e.g. the JSF FacesServlet.
 * Within Spring's own web support, DispatcherServlet's processing is perfectly sufficient.
 *
 * <p>
 *  通过{@link orgspringframeworkcontexti18nLocaleContextHolder}和{@link RequestContextHolder}将请求暴露给当前线程的Se
 * rvlet过滤器要在{@code webxml}中注册为过滤器。
 * 
 * 另外,Spring的{@link orgspringframeworkwebcontextrequestRequestContextListener}和Spring的{@link orgspringframeworkwebservletDispatcherServlet}
 * 也将相同的请求上下文暴露给当前的线程。
 * 
 *  <p>此过滤器主要用于第三方servlet,例如JSF FacesServlet在Spring自己的Web支持中,DispatcherServlet的处理是完全足够的
 * 
 * 
 * @author Juergen Hoeller
 * @author Rod Johnson
 * @author Rossen Stoyanchev
 * @since 2.0
 * @see org.springframework.context.i18n.LocaleContextHolder
 * @see org.springframework.web.context.request.RequestContextHolder
 * @see org.springframework.web.context.request.RequestContextListener
 * @see org.springframework.web.servlet.DispatcherServlet
 */
public class RequestContextFilter extends OncePerRequestFilter {

	private boolean threadContextInheritable = false;


	/**
	 * Set whether to expose the LocaleContext and RequestAttributes as inheritable
	 * for child threads (using an {@link java.lang.InheritableThreadLocal}).
	 * <p>Default is "false", to avoid side effects on spawned background threads.
	 * Switch this to "true" to enable inheritance for custom child threads which
	 * are spawned during request processing and only used for this request
	 * (that is, ending after their initial task, without reuse of the thread).
	 * <p><b>WARNING:</b> Do not use inheritance for child threads if you are
	 * accessing a thread pool which is configured to potentially add new threads
	 * on demand (e.g. a JDK {@link java.util.concurrent.ThreadPoolExecutor}),
	 * since this will expose the inherited context to such a pooled thread.
	 * <p>
	 * 设置是否将LocaleContext和RequestAttributes公开为子线程可继承(使用{@link javalangInheritableThreadLocal})<p>默认为"false",
	 * 以避免对生成的后台线程的副作用将其切换为"true"以启用自定义继承在请求处理期间产生的子线程,仅用于此请求(即,在其初始任务结束后,不重新使用线程)<p> <b>警告：</b>不要对子线程使用继承,如
	 * 果您正在访问一个线程池,该池被配置为可以根据需要添加新线程(例如JDK {@link javautilconcurrentThreadPoolExecutor}),因为这会将继承的上下文暴露于这样一个合
	 * 并的线程。
	 * 
	 */
	public void setThreadContextInheritable(boolean threadContextInheritable) {
		this.threadContextInheritable = threadContextInheritable;
	}


	/**
	 * Returns "false" so that the filter may set up the request context in each
	 * asynchronously dispatched thread.
	 * <p>
	 * 返回"false",以便过滤器可以在每个异步调度的线程中设置请求上下文
	 * 
	 */
	@Override
	protected boolean shouldNotFilterAsyncDispatch() {
		return false;
	}

	/**
	 * Returns "false" so that the filter may set up the request context in an
	 * error dispatch.
	 * <p>
	 *  返回"false",以便过滤器可以在错误分派中设置请求上下文
	 */
	@Override
	protected boolean shouldNotFilterErrorDispatch() {
		return false;
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		ServletRequestAttributes attributes = new ServletRequestAttributes(request, response);
		initContextHolders(request, attributes);

		try {
			filterChain.doFilter(request, response);
		}
		finally {
			resetContextHolders();
			if (logger.isDebugEnabled()) {
				logger.debug("Cleared thread-bound request context: " + request);
			}
			attributes.requestCompleted();
		}
	}

	private void initContextHolders(HttpServletRequest request, ServletRequestAttributes requestAttributes) {
		LocaleContextHolder.setLocale(request.getLocale(), this.threadContextInheritable);
		RequestContextHolder.setRequestAttributes(requestAttributes, this.threadContextInheritable);
		if (logger.isDebugEnabled()) {
			logger.debug("Bound request context to thread: " + request);
		}
	}

	private void resetContextHolders() {
		LocaleContextHolder.resetLocaleContext();
		RequestContextHolder.resetRequestAttributes();
	}

}
