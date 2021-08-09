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

import java.util.concurrent.Callable;

import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * Intercepts concurrent request handling, where the concurrent result is
 * obtained by executing a {@link Callable} on behalf of the application with
 * an {@link AsyncTaskExecutor}.
 *
 * <p>A {@code CallableProcessingInterceptor} is invoked before and after the
 * invocation of the {@code Callable} task in the asynchronous thread, as well
 * as on timeout from a container thread, or after completing for any reason
 * including a timeout or network error.
 *
 * <p>As a general rule exceptions raised by interceptor methods will cause
 * async processing to resume by dispatching back to the container and using
 * the Exception instance as the concurrent result. Such exceptions will then
 * be processed through the {@code HandlerExceptionResolver} mechanism.
 *
 * <p>The {@link #handleTimeout(NativeWebRequest, Callable) afterTimeout} method
 * can select a value to be used to resume processing.
 *
 * <p>
 *  拦截并发请求处理,其中通过使用{@link AsyncTaskExecutor}代表应用程序执行{@link Callable}获得并发结果,
 * 
 * <p>在异步线程中调用{@code Callable}任务之前和之后调用{@code CallableProcessingInterceptor},以及从容器线程超时或在任何原因(包括超时或网络)完成之
 * 后调用错误。
 * 
 *  作为一般规则,拦截器方法引发的异常将导致异步处理通过分派回容器并使用Exception实例作为并发结果来恢复,然后将通过{@code HandlerExceptionResolver}机制来处理异常处
 * 理。
 * 
 *  <p> {@link #handleTimeout(NativeWebRequest,Callable)afterTimeout}方法可以选择要用于恢复处理的值
 * 
 * 
 * @author Rossen Stoyanchev
 * @author Rob Winch
 * @since 3.2
 */
public interface CallableProcessingInterceptor {

	static final Object RESULT_NONE = new Object();

	static final Object RESPONSE_HANDLED = new Object();

	/**
	 * Invoked <em>before</em> the start of concurrent handling in the original
	 * thread in which the {@code Callable} is submitted for concurrent handling.
	 * <p>This is useful for capturing the state of the current thread just prior to
	 * invoking the {@link Callable}. Once the state is captured, it can then be
	 * transferred to the new {@link Thread} in
	 * {@link #preProcess(NativeWebRequest, Callable)}. Capturing the state of
	 * Spring Security's SecurityContextHolder and migrating it to the new Thread
	 * is a concrete example of where this is useful.
	 * <p>
	 * 在</em>之前调用<em>在原始线程中开始并发处理,其中{@code Callable}被提交用于并发处理<p>这对于在调用之前捕获当前线程的状态很有用{@link Callable}一旦状态被捕获,
	 * 它就可以转移到{@link #preProcess(NativeWebRequest,Callable))中的新的{@link Thread}}捕获Spring Security的SecurityCon
	 * textHolder的状态并将其迁移到新线程是一个有用的具体例子。
	 * 
	 * 
	 * @param request the current request
	 * @param task the task for the current async request
	 * @throws Exception in case of errors
	 */
	<T> void  beforeConcurrentHandling(NativeWebRequest request, Callable<T> task) throws Exception;

	/**
	 * Invoked <em>after</em> the start of concurrent handling in the async
	 * thread in which the {@code Callable} is executed and <em>before</em> the
	 * actual invocation of the {@code Callable}.
	 * <p>
	 *  在执行{@code Callable}的异步线程中的&lt; / em>之后调用<em>,然后</em>实际调用{@code Callable}
	 * 
	 * 
	 * @param request the current request
	 * @param task the task for the current async request
	 * @throws Exception in case of errors
	 */
	<T> void preProcess(NativeWebRequest request, Callable<T> task) throws Exception;

	/**
	 * Invoked <em>after</em> the {@code Callable} has produced a result in the
	 * async thread in which the {@code Callable} is executed. This method may
	 * be invoked later than {@code afterTimeout} or {@code afterCompletion}
	 * depending on when the {@code Callable} finishes processing.
	 * <p>
	 * 在</em>之后调用<em> {@code Callable}在执行{@code Callable}的异步线程中产生了一个结果。
	 * 该方法可能会晚于{@code afterTimeout}或{@code afterCompletion }取决于{@code Callable}何时完成处理。
	 * 
	 * 
	 * @param request the current request
	 * @param task the task for the current async request
	 * @param concurrentResult the result of concurrent processing, which could
	 * be a {@link Throwable} if the {@code Callable} raised an exception
	 * @throws Exception in case of errors
	 */
	<T> void postProcess(NativeWebRequest request, Callable<T> task, Object concurrentResult) throws Exception;

	/**
	 * Invoked from a container thread when the async request times out before
	 * the {@code Callable} task completes. Implementations may return a value,
	 * including an {@link Exception}, to use instead of the value the
	 * {@link Callable} did not return in time.
	 * <p>
	 *  当异步请求在{@code Callable}任务完成之前超时时,从容器线程调用实现可能会返回一个值,包括{@link异常},而不是{@link Callable}未返回的值时间
	 * 
	 * 
	 * @param request the current request
	 * @param task the task for the current async request
	 * @return a concurrent result value; if the value is anything other than
	 * {@link #RESULT_NONE} or {@link #RESPONSE_HANDLED}, concurrent processing
	 * is resumed and subsequent interceptors are not invoked
	 * @throws Exception in case of errors
	 */
	<T> Object handleTimeout(NativeWebRequest request, Callable<T> task) throws Exception;

	/**
	 * Invoked from a container thread when async processing completes for any
	 * reason including timeout or network error.
	 * <p>
	 *  当异步处理完成任何原因(包括超时或网络错误)时,从容器线程调用
	 * 
	 * @param request the current request
	 * @param task the task for the current async request
	 * @throws Exception in case of errors
	 */
	<T> void afterCompletion(NativeWebRequest request, Callable<T> task) throws Exception;

}
