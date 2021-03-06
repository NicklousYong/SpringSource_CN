/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2012 the original author or authors.
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
 * Extends {@code WebRequestInterceptor} with a callback method invoked during
 * asynchronous request handling.
 *
 * <p>When a handler starts asynchronous request handling, the DispatcherServlet
 * exits without invoking {@code postHandle} and {@code afterCompletion}, as it
 * normally does, since the results of request handling (e.g. ModelAndView) are
 * not available in the current thread and handling is not yet complete.
 * In such scenarios, the {@link #afterConcurrentHandlingStarted(WebRequest)}
 * method is invoked instead allowing implementations to perform tasks such as
 * cleaning up thread bound attributes.
 *
 * <p>When asynchronous handling completes, the request is dispatched to the
 * container for further processing. At this stage the DispatcherServlet invokes
 * {@code preHandle}, {@code postHandle} and {@code afterCompletion} as usual.
 *
 * <p>
 *  在异步请求处理期间调用回调方法来扩展{@code WebRequestInterceptor}
 * 
 * <p>当处理程序启动异步请求处理时,DispatcherServlet退出而不调用{@code postHandle}和{@code afterCompletion},因为通常情况下,请求处理(例如Mo
 * delAndView)的结果在当前线程中不可用并且处理尚未完成在这种情况下,将调用{@link #afterConcurrentHandlingStarted(WebRequest)}方法,从而允许实现
 * 执行诸如清除线程绑定属性。
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 3.2
 *
 * @see org.springframework.web.context.request.async.WebAsyncManager
 */
public interface AsyncWebRequestInterceptor extends WebRequestInterceptor{

	/**
	 * Called instead of {@code postHandle} and {@code afterCompletion}, when the
	 * the handler started handling the request concurrently.
	 *
	 * <p>
	 *  <p>当异步处理完成时,请求被分派到容器进一步处理在这个阶段,DispatcherServlet像往常一样调用{@code preHandle},{@code postHandle}和{@code afterCompletion}
	 * 。
	 * 
	 * 
	 * @param request the current request
	 */
	void afterConcurrentHandlingStarted(WebRequest request);

}
