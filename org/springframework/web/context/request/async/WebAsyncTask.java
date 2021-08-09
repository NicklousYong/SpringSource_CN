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

package org.springframework.web.context.request.async;

import java.util.concurrent.Callable;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.util.Assert;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * Holder for a {@link Callable}, a timeout value, and a task executor.
 *
 * <p>
 *  持有者为{@link Callable},超时值和任务执行者
 * 
 * 
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @since 3.2
 */
public class WebAsyncTask<V> implements BeanFactoryAware {

	private final Callable<V> callable;

	private Long timeout;

	private AsyncTaskExecutor executor;

	private String executorName;

	private BeanFactory beanFactory;

	private Callable<V> timeoutCallback;

	private Runnable completionCallback;


	/**
	 * Create a {@code WebAsyncTask} wrapping the given {@link Callable}.
	 * <p>
	 *  创建一个包含给定{@link Callable}的{@code WebAsyncTask}
	 * 
	 * 
	 * @param callable the callable for concurrent handling
	 */
	public WebAsyncTask(Callable<V> callable) {
		Assert.notNull(callable, "Callable must not be null");
		this.callable = callable;
	}

	/**
	 * Create a {@code WebAsyncTask} with a timeout value and a {@link Callable}.
	 * <p>
	 *  创建具有超时值的{@code WebAsyncTask}和{@link Callable}
	 * 
	 * 
	 * @param timeout a timeout value in milliseconds
	 * @param callable the callable for concurrent handling
	 */
	public WebAsyncTask(long timeout, Callable<V> callable) {
		this(callable);
		this.timeout = timeout;
	}

	/**
	 * Create a {@code WebAsyncTask} with a timeout value, an executor name, and a {@link Callable}.
	 * <p>
	 * 创建具有超时值的{@code WebAsyncTask},执行者名称和{@link Callable}
	 * 
	 * 
	 * @param timeout timeout value in milliseconds; ignored if {@code null}
	 * @param executorName the name of an executor bean to use
	 * @param callable the callable for concurrent handling
	 */
	public WebAsyncTask(Long timeout, String executorName, Callable<V> callable) {
		this(callable);
		Assert.notNull(executorName, "Executor name must not be null");
		this.executorName = executorName;
		this.timeout = timeout;
	}

	/**
	 * Create a {@code WebAsyncTask} with a timeout value, an executor instance, and a Callable.
	 * <p>
	 *  创建一个带有超时值的{@code WebAsyncTask},一个执行器实例和一个Callable
	 * 
	 * 
	 * @param timeout timeout value in milliseconds; ignored if {@code null}
	 * @param executor the executor to use
	 * @param callable the callable for concurrent handling
	 */
	public WebAsyncTask(Long timeout, AsyncTaskExecutor executor, Callable<V> callable) {
		this(callable);
		Assert.notNull(executor, "Executor must not be null");
		this.executor = executor;
		this.timeout = timeout;
	}


	/**
	 * Return the {@link Callable} to use for concurrent handling (never {@code null}).
	 * <p>
	 *  返回{@link Callable}以用于并发处理(从不{@code null})
	 * 
	 */
	public Callable<?> getCallable() {
		return this.callable;
	}

	/**
	 * Return the timeout value in milliseconds, or {@code null} if no timeout is set.
	 * <p>
	 *  以毫秒为单位返回超时值,如果没有超时设置,则返回{@code null}
	 * 
	 */
	public Long getTimeout() {
		return this.timeout;
	}

	/**
	 * A {@link BeanFactory} to use for resolving an executor name.
	 * <p>This factory reference will automatically be set when
	 * {@code WebAsyncTask} is used within a Spring MVC controller.
	 * <p>
	 *  用于解析执行器名称的{@link BeanFactory} <p>在Spring MVC控制器中使用{@code WebAsyncTask}时,将自动设置此工厂引用
	 * 
	 */
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * Return the AsyncTaskExecutor to use for concurrent handling,
	 * or {@code null} if none specified.
	 * <p>
	 *  返回AsyncTaskExecutor用于并发处理,否则返回{@code null}
	 * 
	 */
	public AsyncTaskExecutor getExecutor() {
		if (this.executor != null) {
			return this.executor;
		}
		else if (this.executorName != null) {
			Assert.state(this.beanFactory != null, "BeanFactory is required to look up an executor bean by name");
			return this.beanFactory.getBean(this.executorName, AsyncTaskExecutor.class);
		}
		else {
			return null;
		}
	}


	/**
	 * Register code to invoke when the async request times out.
	 * <p>This method is called from a container thread when an async request times
	 * out before the {@code Callable} has completed. The callback is executed in
	 * the same thread and therefore should return without blocking. It may return
	 * an alternative value to use, including an {@link Exception} or return
	 * {@link CallableProcessingInterceptor#RESULT_NONE RESULT_NONE}.
	 * <p>
	 * 当异步请求超时时,注册代码调用<p>当异步请求在{@code Callable}完成之前超时时,从容器线程调用此方法回调在同一个线程中执行,因此应该返回而不阻止它可能会返回一个替代值来使用,包括{@link异常}
	 * 或返回{@link CallableProcessingInterceptor#RESULT_NONE RESULT_NONE}。
	 * 
	 */
	public void onTimeout(Callable<V> callback) {
		this.timeoutCallback = callback;
	}

	/**
	 * Register code to invoke when the async request completes.
	 * <p>This method is called from a container thread when an async request
	 * completed for any reason, including timeout and network error.
	 * <p>
	 */
	public void onCompletion(Runnable callback) {
		this.completionCallback = callback;
	}

	CallableProcessingInterceptor getInterceptor() {
		return new CallableProcessingInterceptorAdapter() {
			@Override
			public <T> Object handleTimeout(NativeWebRequest request, Callable<T> task) throws Exception {
				return (timeoutCallback != null ? timeoutCallback.call() : CallableProcessingInterceptor.RESULT_NONE);
			}
			@Override
			public <T> void afterCompletion(NativeWebRequest request, Callable<T> task) throws Exception {
				if (completionCallback != null) {
					completionCallback.run();
				}
			}
		};
	}

}
