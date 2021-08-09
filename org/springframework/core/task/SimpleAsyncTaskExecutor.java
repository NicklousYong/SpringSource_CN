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

package org.springframework.core.task;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadFactory;

import org.springframework.util.Assert;
import org.springframework.util.ConcurrencyThrottleSupport;
import org.springframework.util.CustomizableThreadCreator;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureTask;

/**
 * {@link TaskExecutor} implementation that fires up a new Thread for each task,
 * executing it asynchronously.
 *
 * <p>Supports limiting concurrent threads through the "concurrencyLimit"
 * bean property. By default, the number of concurrent threads is unlimited.
 *
 * <p><b>NOTE: This implementation does not reuse threads!</b> Consider a
 * thread-pooling TaskExecutor implementation instead, in particular for
 * executing a large number of short-lived tasks.
 *
 * <p>
 *  {@link TaskExecutor}实现,为每个任务启动一个新的线程,以异步方式执行
 * 
 * <p>支持通过"concurrencyLimit"bean属性限制并发线程默认情况下,并发线程数是无限制的
 * 
 *  <p> <b>注意：此实现不会重用线程！</b>考虑一个线程池的TaskExecutor实现,特别是执行大量的短期任务
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.0
 * @see #setConcurrencyLimit
 * @see SyncTaskExecutor
 * @see org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
 * @see org.springframework.scheduling.commonj.WorkManagerTaskExecutor
 */
@SuppressWarnings("serial")
public class SimpleAsyncTaskExecutor extends CustomizableThreadCreator implements AsyncListenableTaskExecutor, Serializable {

	/**
	 * Permit any number of concurrent invocations: that is, don't throttle concurrency.
	 * <p>
	 *  允许任意数量的并发调用：即不要限制并发
	 * 
	 */
	public static final int UNBOUNDED_CONCURRENCY = ConcurrencyThrottleSupport.UNBOUNDED_CONCURRENCY;

	/**
	 * Switch concurrency 'off': that is, don't allow any concurrent invocations.
	 * <p>
	 *  切换并发"关闭"：即不允许任何并发调用
	 * 
	 */
	public static final int NO_CONCURRENCY = ConcurrencyThrottleSupport.NO_CONCURRENCY;


	/** Internal concurrency throttle used by this executor */
	private final ConcurrencyThrottleAdapter concurrencyThrottle = new ConcurrencyThrottleAdapter();

	private ThreadFactory threadFactory;

	private TaskDecorator taskDecorator;


	/**
	 * Create a new SimpleAsyncTaskExecutor with default thread name prefix.
	 * <p>
	 *  创建一个带有默认线程名称前缀的新SimpleAsyncTaskExecutor
	 * 
	 */
	public SimpleAsyncTaskExecutor() {
		super();
	}

	/**
	 * Create a new SimpleAsyncTaskExecutor with the given thread name prefix.
	 * <p>
	 *  使用给定的线程名称前缀创建一个新的SimpleAsyncTaskExecutor
	 * 
	 * 
	 * @param threadNamePrefix the prefix to use for the names of newly created threads
	 */
	public SimpleAsyncTaskExecutor(String threadNamePrefix) {
		super(threadNamePrefix);
	}

	/**
	 * Create a new SimpleAsyncTaskExecutor with the given external thread factory.
	 * <p>
	 *  使用给定的外部线程工厂创建一个新的SimpleAsyncTaskExecutor
	 * 
	 * 
	 * @param threadFactory the factory to use for creating new Threads
	 */
	public SimpleAsyncTaskExecutor(ThreadFactory threadFactory) {
		this.threadFactory = threadFactory;
	}


	/**
	 * Specify an external factory to use for creating new Threads,
	 * instead of relying on the local properties of this executor.
	 * <p>You may specify an inner ThreadFactory bean or also a ThreadFactory reference
	 * obtained from JNDI (on a Java EE 6 server) or some other lookup mechanism.
	 * <p>
	 * 指定用于创建新线程的外部工厂,而不是依赖此执行程序的本地属性<p>您可以指定内部ThreadFactory bean或从JNDI(在Java EE 6服务器上)获取的ThreadFactory引用或其他
	 * 一些查找机制。
	 * 
	 * 
	 * @see #setThreadNamePrefix
	 * @see #setThreadPriority
	 */
	public void setThreadFactory(ThreadFactory threadFactory) {
		this.threadFactory = threadFactory;
	}

	/**
	 * Return the external factory to use for creating new Threads, if any.
	 * <p>
	 *  返回外部工厂用于创建新线程(如果有)
	 * 
	 */
	public final ThreadFactory getThreadFactory() {
		return this.threadFactory;
	}

	/**
	 * Specify a custom {@link TaskDecorator} to be applied to any {@link Runnable}
	 * about to be executed.
	 * <p>Note that such a decorator is not necessarily being applied to the
	 * user-supplied {@code Runnable}/{@code Callable} but rather to the actual
	 * execution callback (which may be a wrapper around the user-supplied task).
	 * <p>The primary use case is to set some execution context around the task's
	 * invocation, or to provide some monitoring/statistics for task execution.
	 * <p>
	 * 指定要应用于任何要执行的{@link Runnable}的自定义{@link TaskDecorator} <p>请注意,这样的装饰器不一定适用于用户提供的{@code Runnable} / {@ code Callable }
	 * 而是实际的执行回调(可能是用户提供的任务周围的包装)<p>主要用例是在任务的调用周围设置一些执行上下文,或为任务执行提供一些监视/统计信息。
	 * 
	 * 
	 * @since 4.3
	 */
	public final void setTaskDecorator(TaskDecorator taskDecorator) {
		this.taskDecorator = taskDecorator;
	}

	/**
	 * Set the maximum number of parallel accesses allowed.
	 * -1 indicates no concurrency limit at all.
	 * <p>In principle, this limit can be changed at runtime,
	 * although it is generally designed as a config time setting.
	 * NOTE: Do not switch between -1 and any concrete limit at runtime,
	 * as this will lead to inconsistent concurrency counts: A limit
	 * of -1 effectively turns off concurrency counting completely.
	 * <p>
	 * 设置允许的最大并行访问数量-1表示无并发限制<p>原则上,此限制可以在运行时更改,尽管通常设计为配置时间设置注意：不要在-1之间切换运行时的具体限制,因为这将导致不一致的并发计数：-1的限制有效地完全关
	 * 闭并发计数。
	 * 
	 * 
	 * @see #UNBOUNDED_CONCURRENCY
	 */
	public void setConcurrencyLimit(int concurrencyLimit) {
		this.concurrencyThrottle.setConcurrencyLimit(concurrencyLimit);
	}

	/**
	 * Return the maximum number of parallel accesses allowed.
	 * <p>
	 *  返回允许的最大并行访问数
	 * 
	 */
	public final int getConcurrencyLimit() {
		return this.concurrencyThrottle.getConcurrencyLimit();
	}

	/**
	 * Return whether this throttle is currently active.
	 * <p>
	 *  返回此节气门当前是否活动
	 * 
	 * 
	 * @return {@code true} if the concurrency limit for this instance is active
	 * @see #getConcurrencyLimit()
	 * @see #setConcurrencyLimit
	 */
	public final boolean isThrottleActive() {
		return this.concurrencyThrottle.isThrottleActive();
	}


	/**
	 * Executes the given task, within a concurrency throttle
	 * if configured (through the superclass's settings).
	 * <p>
	 *  执行给定的任务,如果配置了一个并发油门(通过超类的设置)
	 * 
	 * 
	 * @see #doExecute(Runnable)
	 */
	@Override
	public void execute(Runnable task) {
		execute(task, TIMEOUT_INDEFINITE);
	}

	/**
	 * Executes the given task, within a concurrency throttle
	 * if configured (through the superclass's settings).
	 * <p>Executes urgent tasks (with 'immediate' timeout) directly,
	 * bypassing the concurrency throttle (if active). All other
	 * tasks are subject to throttling.
	 * <p>
	 * 执行给定的任务,如果配置(通过超类的设置)执行紧急任务(直接超时)直接执行紧急任务(绕过并发油门)(如果处于活动状态)所有其他任务都受到限制
	 * 
	 * 
	 * @see #TIMEOUT_IMMEDIATE
	 * @see #doExecute(Runnable)
	 */
	@Override
	public void execute(Runnable task, long startTimeout) {
		Assert.notNull(task, "Runnable must not be null");
		Runnable taskToUse = (this.taskDecorator != null ? this.taskDecorator.decorate(task) : task);
		if (isThrottleActive() && startTimeout > TIMEOUT_IMMEDIATE) {
			this.concurrencyThrottle.beforeAccess();
			doExecute(new ConcurrencyThrottlingRunnable(taskToUse));
		}
		else {
			doExecute(taskToUse);
		}
	}

	@Override
	public Future<?> submit(Runnable task) {
		FutureTask<Object> future = new FutureTask<Object>(task, null);
		execute(future, TIMEOUT_INDEFINITE);
		return future;
	}

	@Override
	public <T> Future<T> submit(Callable<T> task) {
		FutureTask<T> future = new FutureTask<T>(task);
		execute(future, TIMEOUT_INDEFINITE);
		return future;
	}

	@Override
	public ListenableFuture<?> submitListenable(Runnable task) {
		ListenableFutureTask<Object> future = new ListenableFutureTask<Object>(task, null);
		execute(future, TIMEOUT_INDEFINITE);
		return future;
	}

	@Override
	public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
		ListenableFutureTask<T> future = new ListenableFutureTask<T>(task);
		execute(future, TIMEOUT_INDEFINITE);
		return future;
	}

	/**
	 * Template method for the actual execution of a task.
	 * <p>The default implementation creates a new Thread and starts it.
	 * <p>
	 *  用于实际执行任务的模板方法<p>默认实现创建一个新的线程并启动它
	 * 
	 * 
	 * @param task the Runnable to execute
	 * @see #setThreadFactory
	 * @see #createThread
	 * @see java.lang.Thread#start()
	 */
	protected void doExecute(Runnable task) {
		Thread thread = (this.threadFactory != null ? this.threadFactory.newThread(task) : createThread(task));
		thread.start();
	}


	/**
	 * Subclass of the general ConcurrencyThrottleSupport class,
	 * making {@code beforeAccess()} and {@code afterAccess()}
	 * visible to the surrounding class.
	 * <p>
	 *  一般ConcurrencyThrottleSupport类的子类,使{@code beforeAccess()}和{@code afterAccess()}可见周围的类
	 * 
	 */
	private static class ConcurrencyThrottleAdapter extends ConcurrencyThrottleSupport {

		@Override
		protected void beforeAccess() {
			super.beforeAccess();
		}

		@Override
		protected void afterAccess() {
			super.afterAccess();
		}
	}


	/**
	 * This Runnable calls {@code afterAccess()} after the
	 * target Runnable has finished its execution.
	 * <p>
	 *  此Runnable在目标Runnable完成执行后调用{@code afterAccess()}
	 */
	private class ConcurrencyThrottlingRunnable implements Runnable {

		private final Runnable target;

		public ConcurrencyThrottlingRunnable(Runnable target) {
			this.target = target;
		}

		@Override
		public void run() {
			try {
				this.target.run();
			}
			finally {
				concurrencyThrottle.afterAccess();
			}
		}
	}

}
