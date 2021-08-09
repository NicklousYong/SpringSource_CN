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

package org.springframework.web.context.request.async;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.RejectedExecutionException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.async.DeferredResult.DeferredResultHandler;
import org.springframework.web.util.UrlPathHelper;

/**
 * The central class for managing asynchronous request processing, mainly intended
 * as an SPI and not typically used directly by application classes.
 *
 * <p>An async scenario starts with request processing as usual in a thread (T1).
 * Concurrent request handling can be initiated by calling
 * {@link #startCallableProcessing(Callable, Object...) startCallableProcessing} or
 * {@link #startDeferredResultProcessing(DeferredResult, Object...) startDeferredResultProcessing},
 * both of which produce a result in a separate thread (T2). The result is saved
 * and the request dispatched to the container, to resume processing with the saved
 * result in a third thread (T3). Within the dispatched thread (T3), the saved
 * result can be accessed via {@link #getConcurrentResult()} or its presence
 * detected via {@link #hasConcurrentResult()}.
 *
 * <p>
 *  用于管理异步请求处理的中心类,主要用作SPI,通常不直接由应用程序类使用
 * 
 * 一个异步方案从线程中的请求处理开始(T1)并发请求处理可以通过调用{@link #startCallableProcessing(Callable,Object)startCallableProcessing}
 * 或{@link #startDeferredResultProcessing(DeferredResult,Object)startDeferredResultProcessing这两个结果都在一个单独的线程(T2)中产生结果。
 * 结果被保存,并且请求被分派到容器,以保存的结果在第三个线程(T3)中恢复处理在分派的线程(T3)中,可以通过{@link #getConcurrentResult()}访问保存的结果,或者通过{@link #hasConcurrentResult())检测到其结果。
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 3.2
 * @see org.springframework.web.context.request.AsyncWebRequestInterceptor
 * @see org.springframework.web.servlet.AsyncHandlerInterceptor
 * @see org.springframework.web.filter.OncePerRequestFilter#shouldNotFilterAsyncDispatch
 * @see org.springframework.web.filter.OncePerRequestFilter#isAsyncDispatch
 */
public final class WebAsyncManager {

	private static final Object RESULT_NONE = new Object();

	private static final Log logger = LogFactory.getLog(WebAsyncManager.class);

	private static final UrlPathHelper urlPathHelper = new UrlPathHelper();

	private static final CallableProcessingInterceptor timeoutCallableInterceptor =
			new TimeoutCallableProcessingInterceptor();

	private static final DeferredResultProcessingInterceptor timeoutDeferredResultInterceptor =
			new TimeoutDeferredResultProcessingInterceptor();


	private AsyncWebRequest asyncWebRequest;

	private AsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor(this.getClass().getSimpleName());

	private Object concurrentResult = RESULT_NONE;

	private Object[] concurrentResultContext;

	private final Map<Object, CallableProcessingInterceptor> callableInterceptors =
			new LinkedHashMap<Object, CallableProcessingInterceptor>();

	private final Map<Object, DeferredResultProcessingInterceptor> deferredResultInterceptors =
			new LinkedHashMap<Object, DeferredResultProcessingInterceptor>();


	/**
	 * Package-private constructor.
	 * <p>
	 *  Package-private构造函数
	 * 
	 * 
	 * @see WebAsyncUtils#getAsyncManager(javax.servlet.ServletRequest)
	 * @see WebAsyncUtils#getAsyncManager(org.springframework.web.context.request.WebRequest)
	 */
	WebAsyncManager() {
	}


	/**
	 * Configure the {@link AsyncWebRequest} to use. This property may be set
	 * more than once during a single request to accurately reflect the current
	 * state of the request (e.g. following a forward, request/response
	 * wrapping, etc). However, it should not be set while concurrent handling
	 * is in progress, i.e. while {@link #isConcurrentHandlingStarted()} is
	 * {@code true}.
	 * <p>
	 * 配置{@link AsyncWebRequest}使用此属性可以在单个请求期间多次设置,以准确反映请求的当前状态(例如,在转发,请求/响应包装等之后)但是不应设置而并发处理正在进行中,即{@link #isConcurrentHandlingStarted()}
	 * 是{@code true}。
	 * 
	 * 
	 * @param asyncWebRequest the web request to use
	 */
	public void setAsyncWebRequest(final AsyncWebRequest asyncWebRequest) {
		Assert.notNull(asyncWebRequest, "AsyncWebRequest must not be null");
		this.asyncWebRequest = asyncWebRequest;
		this.asyncWebRequest.addCompletionHandler(new Runnable() {
			@Override
			public void run() {
				asyncWebRequest.removeAttribute(WebAsyncUtils.WEB_ASYNC_MANAGER_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
			}
		});
	}

	/**
	 * Configure an AsyncTaskExecutor for use with concurrent processing via
	 * {@link #startCallableProcessing(Callable, Object...)}.
	 * <p>By default a {@link SimpleAsyncTaskExecutor} instance is used.
	 * <p>
	 *  配置AsyncTaskExecutor用于通过{@link #startCallableProcessing(Callable,Object)}并发处理} <p>默认情况下,使用{@link SimpleAsyncTaskExecutor}
	 * 实例。
	 * 
	 */
	public void setTaskExecutor(AsyncTaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	/**
	 * Whether the selected handler for the current request chose to handle the
	 * request asynchronously. A return value of "true" indicates concurrent
	 * handling is under way and the response will remain open. A return value
	 * of "false" means concurrent handling was either not started or possibly
	 * that it has completed and the request was dispatched for further
	 * processing of the concurrent result.
	 * <p>
	 * 当前请求的选定处理程序是否选择异步处理请求返回值"true"表示并发处理正在进行中,响应将保持打开返回值"false"表示并发处理未启动或可能还没有启动它已经完成,并且发送请求进一步处理并发结果
	 * 
	 */
	public boolean isConcurrentHandlingStarted() {
		return ((this.asyncWebRequest != null) && this.asyncWebRequest.isAsyncStarted());
	}

	/**
	 * Whether a result value exists as a result of concurrent handling.
	 * <p>
	 *  结果值是否存在作为并发处理的结果
	 * 
	 */
	public boolean hasConcurrentResult() {
		return (this.concurrentResult != RESULT_NONE);
	}

	/**
	 * Provides access to the result from concurrent handling.
	 * <p>
	 *  提供对并发处理结果的访问
	 * 
	 * 
	 * @return an Object, possibly an {@code Exception} or {@code Throwable} if
	 * concurrent handling raised one.
	 * @see #clearConcurrentResult()
	 */
	public Object getConcurrentResult() {
		return this.concurrentResult;
	}

	/**
	 * Provides access to additional processing context saved at the start of
	 * concurrent handling.
	 * <p>
	 *  提供访问在并发处理开始时保存的其他处理上下文
	 * 
	 * 
	 * @see #clearConcurrentResult()
	 */
	public Object[] getConcurrentResultContext() {
		return this.concurrentResultContext;
	}

	/**
	 * Get the {@link CallableProcessingInterceptor} registered under the given key.
	 * <p>
	 *  获取在给定键下注册的{@link CallableProcessingInterceptor}
	 * 
	 * 
	 * @param key the key
	 * @return the interceptor registered under that key or {@code null}
	 */
	public CallableProcessingInterceptor getCallableInterceptor(Object key) {
		return this.callableInterceptors.get(key);
	}

	/**
	 * Get the {@link DeferredResultProcessingInterceptor} registered under the given key.
	 * <p>
	 *  获取在给定键下注册的{@link DeferredResultProcessingInterceptor}
	 * 
	 * 
	 * @param key the key
	 * @return the interceptor registered under that key or {@code null}
	 */
	public DeferredResultProcessingInterceptor getDeferredResultInterceptor(Object key) {
		return this.deferredResultInterceptors.get(key);
	}

	/**
	 * Register a {@link CallableProcessingInterceptor} under the given key.
	 * <p>
	 * 在给定的键下注册一个{@link CallableProcessingInterceptor}
	 * 
	 * 
	 * @param key the key
	 * @param interceptor the interceptor to register
	 */
	public void registerCallableInterceptor(Object key, CallableProcessingInterceptor interceptor) {
		Assert.notNull(key, "Key is required");
		Assert.notNull(interceptor, "CallableProcessingInterceptor  is required");
		this.callableInterceptors.put(key, interceptor);
	}

	/**
	 * Register a {@link CallableProcessingInterceptor} without a key.
	 * The key is derived from the class name and hashcode.
	 * <p>
	 *  注册一个没有密钥的{@link CallableProcessingInterceptor}该密钥来自类名和哈希码
	 * 
	 * 
	 * @param interceptors one or more interceptors to register
	 */
	public void registerCallableInterceptors(CallableProcessingInterceptor... interceptors) {
		Assert.notNull(interceptors, "A CallableProcessingInterceptor is required");
		for (CallableProcessingInterceptor interceptor : interceptors) {
			String key = interceptor.getClass().getName() + ":" + interceptor.hashCode();
			this.callableInterceptors.put(key, interceptor);
		}
	}

	/**
	 * Register a {@link DeferredResultProcessingInterceptor} under the given key.
	 * <p>
	 *  在给定的键下注册{@link DeferredResultProcessingInterceptor}
	 * 
	 * 
	 * @param key the key
	 * @param interceptor the interceptor to register
	 */
	public void registerDeferredResultInterceptor(Object key, DeferredResultProcessingInterceptor interceptor) {
		Assert.notNull(key, "Key is required");
		Assert.notNull(interceptor, "DeferredResultProcessingInterceptor is required");
		this.deferredResultInterceptors.put(key, interceptor);
	}

	/**
	 * Register one or more {@link DeferredResultProcessingInterceptor}s without a specified key.
	 * The default key is derived from the interceptor class name and hash code.
	 * <p>
	 *  注册一个或多个没有指定键的{@link DeferredResultProcessingInterceptor}默认键是从拦截器类名和散列码派生的
	 * 
	 * 
	 * @param interceptors one or more interceptors to register
	 */
	public void registerDeferredResultInterceptors(DeferredResultProcessingInterceptor... interceptors) {
		Assert.notNull(interceptors, "A DeferredResultProcessingInterceptor is required");
		for (DeferredResultProcessingInterceptor interceptor : interceptors) {
			String key = interceptor.getClass().getName() + ":" + interceptor.hashCode();
			this.deferredResultInterceptors.put(key, interceptor);
		}
	}

	/**
	 * Clear {@linkplain #getConcurrentResult() concurrentResult} and
	 * {@linkplain #getConcurrentResultContext() concurrentResultContext}.
	 * <p>
	 *  清除{@linkplain #getConcurrentResult()concurrentResult}和{@linkplain #getConcurrentResultContext()concurrentResultContext}
	 * 。
	 * 
	 */
	public void clearConcurrentResult() {
		this.concurrentResult = RESULT_NONE;
		this.concurrentResultContext = null;
	}

	/**
	 * Start concurrent request processing and execute the given task with an
	 * {@link #setTaskExecutor(AsyncTaskExecutor) AsyncTaskExecutor}. The result
	 * from the task execution is saved and the request dispatched in order to
	 * resume processing of that result. If the task raises an Exception then
	 * the saved result will be the raised Exception.
	 * <p>
	 * 启动并发请求处理,并使用{@link #setTaskExecutor(AsyncTaskExecutor)AsyncTaskExecutor}执行给定任务。
	 * 保存任务执行的结果,并发送请求以恢复处理该结果。如果任务引发异常,则保存结果将是提高的异常。
	 * 
	 * 
	 * @param callable a unit of work to be executed asynchronously
	 * @param processingContext additional context to save that can be accessed
	 * via {@link #getConcurrentResultContext()}
	 * @throws Exception if concurrent processing failed to start
	 * @see #getConcurrentResult()
	 * @see #getConcurrentResultContext()
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void startCallableProcessing(Callable<?> callable, Object... processingContext) throws Exception {
		Assert.notNull(callable, "Callable must not be null");
		startCallableProcessing(new WebAsyncTask(callable), processingContext);
	}

	/**
	 * Use the given {@link WebAsyncTask} to configure the task executor as well as
	 * the timeout value of the {@code AsyncWebRequest} before delegating to
	 * {@link #startCallableProcessing(Callable, Object...)}.
	 * <p>
	 *  在委托给{@link #startCallableProcessing(Callable,Object)}之前,使用给定的{@link WebAsyncTask}配置任务执行程序以及{@code AsyncWebRequest}
	 * 的超时值。
	 * 
	 * 
	 * @param webAsyncTask a WebAsyncTask containing the target {@code Callable}
	 * @param processingContext additional context to save that can be accessed
	 * via {@link #getConcurrentResultContext()}
	 * @throws Exception if concurrent processing failed to start
	 */
	public void startCallableProcessing(final WebAsyncTask<?> webAsyncTask, Object... processingContext) throws Exception {
		Assert.notNull(webAsyncTask, "WebAsyncTask must not be null");
		Assert.state(this.asyncWebRequest != null, "AsyncWebRequest must not be null");

		Long timeout = webAsyncTask.getTimeout();
		if (timeout != null) {
			this.asyncWebRequest.setTimeout(timeout);
		}

		AsyncTaskExecutor executor = webAsyncTask.getExecutor();
		if (executor != null) {
			this.taskExecutor = executor;
		}

		List<CallableProcessingInterceptor> interceptors = new ArrayList<CallableProcessingInterceptor>();
		interceptors.add(webAsyncTask.getInterceptor());
		interceptors.addAll(this.callableInterceptors.values());
		interceptors.add(timeoutCallableInterceptor);

		final Callable<?> callable = webAsyncTask.getCallable();
		final CallableInterceptorChain interceptorChain = new CallableInterceptorChain(interceptors);

		this.asyncWebRequest.addTimeoutHandler(new Runnable() {
			@Override
			public void run() {
				logger.debug("Processing timeout");
				Object result = interceptorChain.triggerAfterTimeout(asyncWebRequest, callable);
				if (result != CallableProcessingInterceptor.RESULT_NONE) {
					setConcurrentResultAndDispatch(result);
				}
			}
		});

		this.asyncWebRequest.addCompletionHandler(new Runnable() {
			@Override
			public void run() {
				interceptorChain.triggerAfterCompletion(asyncWebRequest, callable);
			}
		});

		interceptorChain.applyBeforeConcurrentHandling(this.asyncWebRequest, callable);
		startAsyncProcessing(processingContext);
		try {
			this.taskExecutor.submit(new Runnable() {
				@Override
				public void run() {
					Object result = null;
					try {
						interceptorChain.applyPreProcess(asyncWebRequest, callable);
						result = callable.call();
					}
					catch (Throwable ex) {
						result = ex;
					}
					finally {
						result = interceptorChain.applyPostProcess(asyncWebRequest, callable, result);
					}
					setConcurrentResultAndDispatch(result);
				}
			});
		}
		catch (RejectedExecutionException ex) {
			Object result = interceptorChain.applyPostProcess(this.asyncWebRequest, callable, ex);
			setConcurrentResultAndDispatch(result);
			throw ex;
		}
	}

	private void setConcurrentResultAndDispatch(Object result) {
		synchronized (WebAsyncManager.this) {
			if (hasConcurrentResult()) {
				return;
			}
			this.concurrentResult = result;
		}

		if (this.asyncWebRequest.isAsyncComplete()) {
			logger.error("Could not complete async processing due to timeout or network error");
			return;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Concurrent result value [" + this.concurrentResult +
					"] - dispatching request to resume processing");
		}

		this.asyncWebRequest.dispatch();
	}

	/**
	 * Start concurrent request processing and initialize the given
	 * {@link DeferredResult} with a {@link DeferredResultHandler} that saves
	 * the result and dispatches the request to resume processing of that
	 * result. The {@code AsyncWebRequest} is also updated with a completion
	 * handler that expires the {@code DeferredResult} and a timeout handler
	 * assuming the {@code DeferredResult} has a default timeout result.
	 * <p>
	 * 启动并发请求处理并使用{@link DeferredResultHandler}初始化给定的{@link DeferredResult},保存结果并调度请求以恢复该结果的处理{@code AsyncWebRequest}
	 * 还使用完成处理程序更新假设{@code DeferredResult}具有默认超时结果,{@code DeferredResult}和超时处理程序。
	 * 
	 * @param deferredResult the DeferredResult instance to initialize
	 * @param processingContext additional context to save that can be accessed
	 * via {@link #getConcurrentResultContext()}
	 * @throws Exception if concurrent processing failed to start
	 * @see #getConcurrentResult()
	 * @see #getConcurrentResultContext()
	 */
	public void startDeferredResultProcessing(
			final DeferredResult<?> deferredResult, Object... processingContext) throws Exception {

		Assert.notNull(deferredResult, "DeferredResult must not be null");
		Assert.state(this.asyncWebRequest != null, "AsyncWebRequest must not be null");

		Long timeout = deferredResult.getTimeoutValue();
		if (timeout != null) {
			this.asyncWebRequest.setTimeout(timeout);
		}

		List<DeferredResultProcessingInterceptor> interceptors = new ArrayList<DeferredResultProcessingInterceptor>();
		interceptors.add(deferredResult.getInterceptor());
		interceptors.addAll(this.deferredResultInterceptors.values());
		interceptors.add(timeoutDeferredResultInterceptor);

		final DeferredResultInterceptorChain interceptorChain = new DeferredResultInterceptorChain(interceptors);

		this.asyncWebRequest.addTimeoutHandler(new Runnable() {
			@Override
			public void run() {
				try {
					interceptorChain.triggerAfterTimeout(asyncWebRequest, deferredResult);
				}
				catch (Throwable ex) {
					setConcurrentResultAndDispatch(ex);
				}
			}
		});

		this.asyncWebRequest.addCompletionHandler(new Runnable() {
			@Override
			public void run() {
				interceptorChain.triggerAfterCompletion(asyncWebRequest, deferredResult);
			}
		});

		interceptorChain.applyBeforeConcurrentHandling(this.asyncWebRequest, deferredResult);
		startAsyncProcessing(processingContext);

		try {
			interceptorChain.applyPreProcess(this.asyncWebRequest, deferredResult);
			deferredResult.setResultHandler(new DeferredResultHandler() {
				@Override
				public void handleResult(Object result) {
					result = interceptorChain.applyPostProcess(asyncWebRequest, deferredResult, result);
					setConcurrentResultAndDispatch(result);
				}
			});
		}
		catch (Throwable ex) {
			setConcurrentResultAndDispatch(ex);
		}
	}

	private void startAsyncProcessing(Object[] processingContext) {
		clearConcurrentResult();
		this.concurrentResultContext = processingContext;
		this.asyncWebRequest.startAsync();

		if (logger.isDebugEnabled()) {
			HttpServletRequest request = this.asyncWebRequest.getNativeRequest(HttpServletRequest.class);
			String requestUri = urlPathHelper.getRequestUri(request);
			logger.debug("Concurrent handling starting for " + request.getMethod() + " [" + requestUri + "]");
		}
	}

}
