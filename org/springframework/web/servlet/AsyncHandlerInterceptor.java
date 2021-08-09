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

package org.springframework.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;

/**
 * Extends {@code HandlerInterceptor} with a callback method invoked after the
 * start of asynchronous request handling.
 *
 * <p>When a handler starts an asynchronous request, the {@link DispatcherServlet}
 * exits without invoking {@code postHandle} and {@code afterCompletion} as it
 * normally does for a synchronous request, since the result of request handling
 * (e.g. ModelAndView) is likely not yet ready and will be produced concurrently
 * from another thread. In such scenarios, {@link #afterConcurrentHandlingStarted}
 * is invoked instead, allowing implementations to perform tasks such as cleaning
 * up thread-bound attributes before releasing the thread to the Servlet container.
 *
 * <p>When asynchronous handling completes, the request is dispatched to the
 * container for further processing. At this stage the {@code DispatcherServlet}
 * invokes {@code preHandle}, {@code postHandle}, and {@code afterCompletion}.
 * To distinguish between the initial request and the subsequent dispatch
 * after asynchronous handling completes, interceptors can check whether the
 * {@code javax.servlet.DispatcherType} of {@link javax.servlet.ServletRequest}
 * is {@code "REQUEST"} or {@code "ASYNC"}.
 *
 * <p>Note that {@code HandlerInterceptor} implementations may need to do work
 * when an async request times out or completes with a network error. For such
 * cases the Servlet container does not dispatch and therefore the
 * {@code postHandle} and {@code afterCompletion} methods will not be invoked.
 * Instead, interceptors can register to track an asynchronous request through
 * the {@code registerCallbackInterceptor} and {@code registerDeferredResultInterceptor}
 * methods on {@link org.springframework.web.context.request.async.WebAsyncManager
 * WebAsyncManager}. This can be done proactively on every request from
 * {@code preHandle} regardless of whether async request processing will start.
 *
 * <p>
 *  在异步请求处理开始之后调用回调方法来扩展{@code HandlerInterceptor}
 * 
 * <p>当处理程序启动异步请求时,{@link DispatcherServlet}退出而不调用{@code postHandle}和{@code afterCompletion},因为它通常用于同步请求
 * ,因为请求处理的结果(例如ModelAndView)可能尚未准备好并将从另一个线程同时生成在这种情况下,{@link #afterConcurrentHandlingStarted}被调用,允许实现执行
 * 诸如清理线程绑定属性之类的任务,然后将线程释放到Servlet容器。
 * 
 * <p>当异步处理完成时,请求被发送到容器进行进一步处理在这个阶段,{@code DispatcherServlet}调用{@code preHandle},{@code postHandle}和{@code afterCompletion}
 * 来区分初始请求和异步处理完成后的后续调度,拦截器可以检查{@code javaxservletServletRequest}是否为{@code"REQUEST"}或{@code"ASYNC"}的{@code javaxservletDispatcherType}
 * 。
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 3.2
 * @see org.springframework.web.context.request.async.WebAsyncManager
 * @see org.springframework.web.context.request.async.CallableProcessingInterceptor
 * @see org.springframework.web.context.request.async.DeferredResultProcessingInterceptor
 */
public interface AsyncHandlerInterceptor extends HandlerInterceptor {

	/**
	 * Called instead of {@code postHandle} and {@code afterCompletion}, when
	 * the a handler is being executed concurrently.
	 * <p>Implementations may use the provided request and response but should
	 * avoid modifying them in ways that would conflict with the concurrent
	 * execution of the handler. A typical use of this method would be to
	 * clean up thread-local variables.
	 * <p>
	 * <p>请注意,当异步请求超时或完成时,{@code HandlerInterceptor}实现可能需要执行工作,因为网络错误对于这种情况,Servlet容器不会调度,因此{@code postHandle}
	 * 和{@code afterCompletion }方法不会被调用,而是拦截器可以通过{@code registerCallbackInterceptor}和{@code registerDeferredResultInterceptor}
	 * 方法在{@link orgspringframeworkwebcontextrequestasyncWebAsyncManager WebAsyncManager}上注册来跟踪异步请求。
	 * 这可以在{@code preHandle的每个请求上主动完成}无论异步请求处理是否启动。
	 * 
	 * 
	 * @param request the current request
	 * @param response the current response
	 * @param handler the handler (or {@link HandlerMethod}) that started async
	 * execution, for type and/or instance examination
	 * @throws Exception in case of errors
	 */
	void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception;

}
