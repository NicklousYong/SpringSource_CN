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

import org.springframework.web.context.request.NativeWebRequest;

/**
 * Intercepts concurrent request handling, where the concurrent result is
 * obtained by waiting for a {@link DeferredResult} to be set from a thread
 * chosen by the application (e.g. in response to some external event).
 *
 * <p>A {@code DeferredResultProcessingInterceptor} is invoked before the start
 * of async processing, after the {@code DeferredResult} is set as well as on
 * timeout, or after completing for any reason including a timeout or network
 * error.
 *
 * <p>As a general rule exceptions raised by interceptor methods will cause
 * async processing to resume by dispatching back to the container and using
 * the Exception instance as the concurrent result. Such exceptions will then
 * be processed through the {@code HandlerExceptionResolver} mechanism.
 *
 * <p>The {@link #handleTimeout(NativeWebRequest, DeferredResult) afterTimeout}
 * method can set the {@code DeferredResult} in order to resume processing.
 *
 * <p>
 *  拦截并发请求处理,其中通过等待从应用程序选择的线程(例如响应某些外部事件)设置{@link DeferredResult}获得并发结果,
 * 
 * {@code DeferredResultProcessingInterceptor}在异步处理开始之前被调用,在{@code DeferredResult}被设置为超时之后,或者由于包括超时或网络错误
 * 在内的任何原因而完成。
 * 
 *  作为一般规则,拦截器方法引发的异常将导致异步处理通过分派回容器并使用Exception实例作为并发结果来恢复,然后将通过{@code HandlerExceptionResolver}机制来处理异常处
 * 理。
 * 
 *  <p> {@link #handleTimeout(NativeWebRequest,DeferredResult)afterTimeout}方法可以设置{@code DeferredResult}以
 * 恢复处理。
 * 
 * 
 * @author Rossen Stoyanchev
 * @author Rob Winch
 * @since 3.2
 */
public interface DeferredResultProcessingInterceptor {

	/**
	 * Invoked immediately before the start of concurrent handling, in the same
	 * thread that started it. This method may be used to capture state just prior
	 * to the start of concurrent processing with the given {@code DeferredResult}.
	 * <p>
	 * 在并发处理开始之前调用,在启动它的同一个线程中。该方法可以用于在给定的{@code DeferredResult}开始并行处理之前捕获状态。
	 * 
	 * 
	 * @param request the current request
	 * @param deferredResult the DeferredResult for the current request
	 * @throws Exception in case of errors
	 */
	<T> void beforeConcurrentHandling(NativeWebRequest request, DeferredResult<T> deferredResult) throws Exception;

	/**
	 * Invoked immediately after the start of concurrent handling, in the same
	 * thread that started it. This method may be used to detect the start of
	 * concurrent processing with the given {@code DeferredResult}.
	 * <p>The {@code DeferredResult} may have already been set, for example at
	 * the time of its creation or by another thread.
	 * <p>
	 *  在并发处理开始后立即调用,在启动它的同一个线程中可以使用此方法检测与给定{@code DeferredResult} <p>并发处理的开始{@code DeferredResult}可能已经设置,例如
	 * 在创建时或通过另一个线程。
	 * 
	 * 
	 * @param request the current request
	 * @param deferredResult the DeferredResult for the current request
	 * @throws Exception in case of errors
	 */
	<T> void preProcess(NativeWebRequest request, DeferredResult<T> deferredResult) throws Exception;

	/**
	 * Invoked after a {@code DeferredResult} has been set, via
	 * {@link DeferredResult#setResult(Object)} or
	 * {@link DeferredResult#setErrorResult(Object)}, and is also ready to
	 * handle the concurrent result.
	 * <p>This method may also be invoked after a timeout when the
	 * {@code DeferredResult} was created with a constructor accepting a default
	 * timeout result.
	 * <p>
	 * 通过{@link DeferredResult#setResult(Object)}或{@link DeferredResult#setErrorResult(Object)}设置{@code DeferredResult}
	 * 后调用,并且还可以处理并发结果<p>此方法可能当使用构造函数接受默认超时结果创建{@code DeferredResult}时,也会在超时后调用。
	 * 
	 * 
	 * @param request the current request
	 * @param deferredResult the DeferredResult for the current request
	 * @param concurrentResult the result to which the {@code DeferredResult}
	 * @throws Exception in case of errors
	 */
	<T> void postProcess(NativeWebRequest request, DeferredResult<T> deferredResult, Object concurrentResult) throws Exception;

	/**
	 * Invoked from a container thread when an async request times out before
	 * the {@code DeferredResult} has been set. Implementations may invoke
	 * {@link DeferredResult#setResult(Object) setResult} or
	 * {@link DeferredResult#setErrorResult(Object) setErrorResult} to resume processing.
	 * <p>
	 *  当异步请求在{@code DeferredResult}设置之前超时时,从容器线程调用实现可能会调用{@link DeferredResult#setResult(Object)setResult}或
	 * {@link DeferredResult#setErrorResult(Object)setErrorResult}来恢复处理。
	 * 
	 * 
	 * @param request the current request
	 * @param deferredResult the DeferredResult for the current request; if the
	 * {@code DeferredResult} is set, then concurrent processing is resumed and
	 * subsequent interceptors are not invoked
	 * @return {@code true} if processing should continue, or {@code false} if
	 * other interceptors should not be invoked
	 * @throws Exception in case of errors
	 */
	<T> boolean handleTimeout(NativeWebRequest request, DeferredResult<T> deferredResult) throws Exception;

	/**
	 * Invoked from a container thread when an async request completed for any
	 * reason including timeout and network error. This method is useful for
	 * detecting that a {@code DeferredResult} instance is no longer usable.
	 * <p>
	 * 
	 * @param request the current request
	 * @param deferredResult the DeferredResult for the current request
	 * @throws Exception in case of errors
	 */
	<T> void afterCompletion(NativeWebRequest request, DeferredResult<T> deferredResult) throws Exception;

}
