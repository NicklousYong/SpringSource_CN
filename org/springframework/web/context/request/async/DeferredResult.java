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

import java.util.PriorityQueue;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.util.Assert;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * {@code DeferredResult} provides an alternative to using a {@link Callable} for
 * asynchronous request processing. While a {@code Callable} is executed concurrently
 * on behalf of the application, with a {@code DeferredResult} the application can
 * produce the result from a thread of its choice.
 *
 * <p>Subclasses can extend this class to easily associate additional data or behavior
 * with the {@link DeferredResult}. For example, one might want to associate the user
 * used to create the {@link DeferredResult} by extending the class and adding an
 * additional property for the user. In this way, the user could easily be accessed
 * later without the need to use a data structure to do the mapping.
 *
 * <p>An example of associating additional behavior to this class might be realized
 * by extending the class to implement an additional interface. For example, one
 * might want to implement {@link Comparable} so that when the {@link DeferredResult}
 * is added to a {@link PriorityQueue} it is handled in the correct order.
 *
 * <p>
 * {@code DeferredResult}提供了一种使用{@link Callable}进行异步请求处理的替代方法。
 * 当{@code Callable}代表应用程序并发执行时,使用{@code DeferredResult},应用程序可以生成结果一个选择的线程。
 * 
 *  <p>子类可以扩展此类以便将附加数据或行为与{@link DeferredResult}相关联。
 * 例如,可能需要将用于创建{@link DeferredResult}的用户与扩展类并添加其他属性相关联为用户以这种方式,用户可以很容易地被访问,而不需要使用数据结构来做映射。
 * 
 * <p>将附加行为与此类关联的示例可能是通过扩展类实现一个附加接口来实现的。
 * 例如,可能需要实现{@link Comparable},以便将{@link DeferredResult}添加到{@link PriorityQueue}以正确的顺序处理。
 * 
 * 
 * @author Rossen Stoyanchev
 * @author Rob Winch
 * @since 3.2
 */
public class DeferredResult<T> {

	private static final Object RESULT_NONE = new Object();

	private static final Log logger = LogFactory.getLog(DeferredResult.class);


	private final Long timeout;

	private final Object timeoutResult;

	private Runnable timeoutCallback;

	private Runnable completionCallback;

	private DeferredResultHandler resultHandler;

	private volatile Object result = RESULT_NONE;

	private volatile boolean expired;


	/**
	 * Create a DeferredResult.
	 * <p>
	 *  创建一个DeferredResult
	 * 
	 */
	public DeferredResult() {
		this(null, RESULT_NONE);
	}

	/**
	 * Create a DeferredResult with a timeout value.
	 * <p>By default not set in which case the default configured in the MVC
	 * Java Config or the MVC namespace is used, or if that's not set, then the
	 * timeout depends on the default of the underlying server.
	 * <p>
	 *  创建具有超时值<p>的DeferredResult默认情况下不设置在MVC Java Config或MVC命名空间中配置的默认值,或者如果未设置,则超时取决于底层服务器的默认值
	 * 
	 * 
	 * @param timeout timeout value in milliseconds
	 */
	public DeferredResult(Long timeout) {
		this(timeout, RESULT_NONE);
	}

	/**
	 * Create a DeferredResult with a timeout value and a default result to use
	 * in case of timeout.
	 * <p>
	 *  创建一个具有超时值和默认结果的DeferredResult,以便在超时的情况下使用
	 * 
	 * 
	 * @param timeout timeout value in milliseconds (ignored if {@code null})
	 * @param timeoutResult the result to use
	 */
	public DeferredResult(Long timeout, Object timeoutResult) {
		this.timeoutResult = timeoutResult;
		this.timeout = timeout;
	}


	/**
	 * Return {@code true} if this DeferredResult is no longer usable either
	 * because it was previously set or because the underlying request expired.
	 * <p>The result may have been set with a call to {@link #setResult(Object)},
	 * or {@link #setErrorResult(Object)}, or as a result of a timeout, if a
	 * timeout result was provided to the constructor. The request may also
	 * expire due to a timeout or network error.
	 * <p>
	 * 如果此DeferredResult不再可用,则返回{@code true},因为它先前已设置或因为底层请求已过期<p>结果可能已通过调用{@link #setResult(Object)}或{ @link #setErrorResult(Object)}
	 * ,或作为超时的结果,如果超时结果提供给构造函数。
	 * 请求也可能由于超时或网络错误而过期。
	 * 
	 */
	public final boolean isSetOrExpired() {
		return (this.result != RESULT_NONE || this.expired);
	}

	/**
	 * Return {@code true} if the DeferredResult has been set.
	 * <p>
	 *  如果DeferredResult已设置,返回{@code true}
	 * 
	 * 
	 * @since 4.0
	 */
	public boolean hasResult() {
		return (this.result != RESULT_NONE);
	}

	/**
	 * Return the result, or {@code null} if the result wasn't set. Since the result
	 * can also be {@code null}, it is recommended to use {@link #hasResult()} first
	 * to check if there is a result prior to calling this method.
	 * <p>
	 *  如果没有设置结果返回结果,或{@code null}由于结果也可以是{@code null},因此建议首先使用{@link #hasResult()}来检查是否有结果在调用此方法之前
	 * 
	 * 
	 * @since 4.0
	 */
	public Object getResult() {
		Object resultToCheck = this.result;
		return (resultToCheck != RESULT_NONE ? resultToCheck : null);
	}

	/**
	 * Return the configured timeout value in milliseconds.
	 * <p>
	 *  以毫秒为单位返回配置的超时值
	 * 
	 */
	final Long getTimeoutValue() {
		return this.timeout;
	}

	/**
	 * Register code to invoke when the async request times out.
	 * <p>This method is called from a container thread when an async request
	 * times out before the {@code DeferredResult} has been populated.
	 * It may invoke {@link DeferredResult#setResult setResult} or
	 * {@link DeferredResult#setErrorResult setErrorResult} to resume processing.
	 * <p>
	 * 注册异步请求超时时调用的代码<p>当异步请求在{@code DeferredResult}被填充之前超时时,从容器线程调用此方法它可以调用{@link DeferredResult#setResult setResult}
	 * 或{ @link DeferredResult#setErrorResult setErrorResult}来恢复处理。
	 * 
	 */
	public void onTimeout(Runnable callback) {
		this.timeoutCallback = callback;
	}

	/**
	 * Register code to invoke when the async request completes.
	 * <p>This method is called from a container thread when an async request
	 * completed for any reason including timeout and network error. This is useful
	 * for detecting that a {@code DeferredResult} instance is no longer usable.
	 * <p>
	 *  注册异步请求完成时调用的代码<p>当异步请求以任何原因(包括超时和网络错误)完成时,从容器线程调用此方法这对于检测到{@code DeferredResult}实例不再可用
	 * 
	 */
	public void onCompletion(Runnable callback) {
		this.completionCallback = callback;
	}

	/**
	 * Provide a handler to use to handle the result value.
	 * <p>
	 *  提供一个用于处理结果值的处理程序
	 * 
	 * 
	 * @param resultHandler the handler
	 * @see DeferredResultProcessingInterceptor
	 */
	public final void setResultHandler(DeferredResultHandler resultHandler) {
		Assert.notNull(resultHandler, "DeferredResultHandler is required");
		synchronized (this) {
			this.resultHandler = resultHandler;
			if (this.result != RESULT_NONE && !this.expired) {
				try {
					this.resultHandler.handleResult(this.result);
				}
				catch (Throwable ex) {
					logger.trace("DeferredResult not handled", ex);
				}
			}
		}
	}

	/**
	 * Set the value for the DeferredResult and handle it.
	 * <p>
	 *  设置DeferredResult的值并处理它
	 * 
	 * 
	 * @param result the value to set
	 * @return "true" if the result was set and passed on for handling;
	 * "false" if the result was already set or the async request expired
	 * @see #isSetOrExpired()
	 */
	public boolean setResult(T result) {
		return setResultInternal(result);
	}

	private boolean setResultInternal(Object result) {
		synchronized (this) {
			if (isSetOrExpired()) {
				return false;
			}
			this.result = result;
		}
		if (this.resultHandler != null) {
			this.resultHandler.handleResult(this.result);
		}
		return true;
	}

	/**
	 * Set an error value for the {@link DeferredResult} and handle it.
	 * The value may be an {@link Exception} or {@link Throwable} in which case
	 * it will be processed as if a handler raised the exception.
	 * <p>
	 * 设置{@link DeferredResult}的错误值并处理它。值可能是{@link Exception}或{@link Throwable},在这种情况下,它将被处理,就像处理程序引发异常
	 * 
	 * 
	 * @param result the error result value
	 * @return "true" if the result was set to the error value and passed on for
	 * handling; "false" if the result was already set or the async request expired
	 * @see #isSetOrExpired()
	 */
	public boolean setErrorResult(Object result) {
		return setResultInternal(result);
	}


	final DeferredResultProcessingInterceptor getInterceptor() {
		return new DeferredResultProcessingInterceptorAdapter() {
			@Override
			public <S> boolean handleTimeout(NativeWebRequest request, DeferredResult<S> deferredResult) {
				if (timeoutCallback != null) {
					timeoutCallback.run();
				}
				if (DeferredResult.this.timeoutResult != RESULT_NONE) {
					setResultInternal(timeoutResult);
				}
				return true;
			}
			@Override
			public <S> void afterCompletion(NativeWebRequest request, DeferredResult<S> deferredResult) {
				synchronized (DeferredResult.this) {
					expired = true;
				}
				if (completionCallback != null) {
					completionCallback.run();
				}
			}
		};
	}


	/**
	 * Handles a DeferredResult value when set.
	 * <p>
	 *  设置时处理DeferredResult值
	 */
	public interface DeferredResultHandler {

		void handleResult(Object result);
	}

}
