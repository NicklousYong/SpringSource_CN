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

package org.springframework.core.task;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Extended interface for asynchronous {@link TaskExecutor} implementations,
 * offering an overloaded {@link #execute(Runnable, long)} variant with a start
 * timeout parameter as well support for {@link java.util.concurrent.Callable}.
 *
 * <p>Note: The {@link java.util.concurrent.Executors} class includes a set of
 * methods that can convert some other common closure-like objects, for example,
 * {@link java.security.PrivilegedAction} to {@link Callable} before executing them.
 *
 * <p>Implementing this interface also indicates that the {@link #execute(Runnable)}
 * method will not execute its Runnable in the caller's thread but rather
 * asynchronously in some other thread.
 *
 * <p>
 * 异步{@link TaskExecutor}实现的扩展接口,提供了一个重载的{@link #execute(Runnable,long)}变体,启动超时参数也支持{@link javautilconcurrentCallable}
 * 。
 * 
 *  注意：{@link javautilconcurrentExecutors}类包括一组可以在执行它们之前将其他常见的类似闭包的对象(例如{@link javasecurityPrivilegedAction}
 * )转换为{@link Callable}的方法)。
 * 
 *  <p>实现此接口还表示{@link #execute(Runnable)}方法将不会在调用者的线程中执行其Runnable,而是在某些其他线程中异步执行
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.0.3
 * @see SimpleAsyncTaskExecutor
 * @see org.springframework.scheduling.SchedulingTaskExecutor
 * @see java.util.concurrent.Callable
 * @see java.util.concurrent.Executors
 */
public interface AsyncTaskExecutor extends TaskExecutor {

	/** Constant that indicates immediate execution */
	long TIMEOUT_IMMEDIATE = 0;

	/** Constant that indicates no time limit */
	long TIMEOUT_INDEFINITE = Long.MAX_VALUE;


	/**
	 * Execute the given {@code task}.
	 * <p>
	 *  执行给定的{@code任务}
	 * 
	 * 
	 * @param task the {@code Runnable} to execute (never {@code null})
	 * @param startTimeout the time duration (milliseconds) within which the task is
	 * supposed to start. This is intended as a hint to the executor, allowing for
	 * preferred handling of immediate tasks. Typical values are {@link #TIMEOUT_IMMEDIATE}
	 * or {@link #TIMEOUT_INDEFINITE} (the default as used by {@link #execute(Runnable)}).
	 * @throws TaskTimeoutException in case of the task being rejected because
	 * of the timeout (i.e. it cannot be started in time)
	 * @throws TaskRejectedException if the given task was not accepted
	 */
	void execute(Runnable task, long startTimeout);

	/**
	 * Submit a Runnable task for execution, receiving a Future representing that task.
	 * The Future will return a {@code null} result upon completion.
	 * <p>
	 * 提交一个Runnable任务执行,接收一个表示该任务的Future未来将在完成后返回{@code null}结果
	 * 
	 * 
	 * @param task the {@code Runnable} to execute (never {@code null})
	 * @return a Future representing pending completion of the task
	 * @throws TaskRejectedException if the given task was not accepted
	 * @since 3.0
	 */
	Future<?> submit(Runnable task);

	/**
	 * Submit a Callable task for execution, receiving a Future representing that task.
	 * The Future will return the Callable's result upon completion.
	 * <p>
	 *  提交可执行任务执行,接收代表该任务的未来Future将在完成后返回Callable的结果
	 * 
	 * @param task the {@code Callable} to execute (never {@code null})
	 * @return a Future representing pending completion of the task
	 * @throws TaskRejectedException if the given task was not accepted
	 * @since 3.0
	 */
	<T> Future<T> submit(Callable<T> task);

}
