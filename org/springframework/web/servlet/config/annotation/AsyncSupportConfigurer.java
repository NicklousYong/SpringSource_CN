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
package org.springframework.web.servlet.config.annotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.util.Assert;
import org.springframework.web.context.request.async.CallableProcessingInterceptor;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.DeferredResultProcessingInterceptor;
import org.springframework.web.context.request.async.WebAsyncTask;

/**
 * Helps with configuring options for asynchronous request processing.
 *
 * <p>
 *  有助于配置异步请求处理的选项
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 3.2
 */
public class AsyncSupportConfigurer {

	private AsyncTaskExecutor taskExecutor;

	private Long timeout;

	private final List<CallableProcessingInterceptor> callableInterceptors =
			new ArrayList<CallableProcessingInterceptor>();

	private final List<DeferredResultProcessingInterceptor> deferredResultInterceptors =
			new ArrayList<DeferredResultProcessingInterceptor>();


	/**
	 * Set the default {@link AsyncTaskExecutor} to use when a controller method
	 * returns a {@link Callable}. Controller methods can override this default on
	 * a per-request basis by returning a {@link WebAsyncTask}.
	 *
	 * <p>By default a {@link SimpleAsyncTaskExecutor} instance is used, and it's
	 * highly recommended to change that default in production since the simple
	 * executor does not re-use threads.
	 *
	 * <p>
	 * 设置默认{@link AsyncTaskExecutor}以在控制器方法返回{@link Callable}控制器方法时使用,可以通过返回{@link WebAsyncTask}来覆盖此默认值,
	 * 
	 *  <p>默认情况下,使用{@link SimpleAsyncTaskExecutor}实例,强烈建议您在生产中更改默认值,因为简单执行程序不会重新使用线程
	 * 
	 * 
	 * @param taskExecutor the task executor instance to use by default
	 */
	public AsyncSupportConfigurer setTaskExecutor(AsyncTaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
		return this;
	}

	/**
	 * Specify the amount of time, in milliseconds, before asynchronous request
	 * handling times out. In Servlet 3, the timeout begins after the main request
	 * processing thread has exited and ends when the request is dispatched again
	 * for further processing of the concurrently produced result.
	 * <p>If this value is not set, the default timeout of the underlying
	 * implementation is used, e.g. 10 seconds on Tomcat with Servlet 3.
	 *
	 * <p>
	 * 指定异步请求处理超时之前的时间(以毫秒为单位)在Servlet 3中,超时在主请求处理线程退出之后开始,并在再次调度请求以进一步处理同时产生的结果时结束<p> If此值未设置,使用底层实现的默认超时时间
	 * ,例如Tomcat上使用Servlet 3的10秒。
	 * 
	 * 
	 * @param timeout the timeout value in milliseconds
	 */
	public AsyncSupportConfigurer setDefaultTimeout(long timeout) {
		this.timeout = timeout;
		return this;
	}

	/**
	 * Configure lifecycle interceptors with callbacks around concurrent request
	 * execution that starts when a controller returns a
	 * {@link java.util.concurrent.Callable}.
	 *
	 * <p>
	 *  配置生命周期拦截器,包括并发请求执行的回调,当控制器返回一个{@link javautilconcurrentCallable}
	 * 
	 * 
	 * @param interceptors the interceptors to register
	 */
	public AsyncSupportConfigurer registerCallableInterceptors(CallableProcessingInterceptor... interceptors) {
		Assert.notNull(interceptors, "Interceptors are required");
		this.callableInterceptors.addAll(Arrays.asList(interceptors));
		return this;
	}

	/**
	 * Configure lifecycle interceptors with callbacks around concurrent request
	 * execution that starts when a controller returns a {@link DeferredResult}.
	 *
	 * <p>
	 *  配置生命周期拦截器,包括并发请求执行的回调,当控制器返回{@link DeferredResult}时,
	 * 
	 * @param interceptors the interceptors to register
	 */
	public AsyncSupportConfigurer registerDeferredResultInterceptors(DeferredResultProcessingInterceptor... interceptors) {
		Assert.notNull(interceptors, "Interceptors are required");
		this.deferredResultInterceptors.addAll(Arrays.asList(interceptors));
		return this;
	}

	protected AsyncTaskExecutor getTaskExecutor() {
		return this.taskExecutor;
	}

	protected Long getTimeout() {
		return this.timeout;
	}

	protected List<CallableProcessingInterceptor> getCallableInterceptors() {
		return this.callableInterceptors;
	}

	protected List<DeferredResultProcessingInterceptor> getDeferredResultInterceptors() {
		return this.deferredResultInterceptors;
	}

}
