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

package org.springframework.scheduling.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.util.Assert;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureTask;

/**
 * JavaBean that allows for configuring a {@link java.util.concurrent.ThreadPoolExecutor}
 * in bean style (through its "corePoolSize", "maxPoolSize", "keepAliveSeconds", "queueCapacity"
 * properties) and exposing it as a Spring {@link org.springframework.core.task.TaskExecutor}.
 * This class is also well suited for management and monitoring (e.g. through JMX),
 * providing several useful attributes: "corePoolSize", "maxPoolSize", "keepAliveSeconds"
 * (all supporting updates at runtime); "poolSize", "activeCount" (for introspection only).
 *
 * <p>For an alternative, you may set up a ThreadPoolExecutor instance directly using
 * constructor injection, or use a factory method definition that points to the
 * {@link java.util.concurrent.Executors} class. To expose such a raw Executor as a
 * Spring {@link org.springframework.core.task.TaskExecutor}, simply wrap it with a
 * {@link org.springframework.scheduling.concurrent.ConcurrentTaskExecutor} adapter.
 *
 * <p><b>NOTE:</b> This class implements Spring's
 * {@link org.springframework.core.task.TaskExecutor} interface as well as the
 * {@link java.util.concurrent.Executor} interface, with the former being the primary
 * interface, the other just serving as secondary convenience. For this reason, the
 * exception handling follows the TaskExecutor contract rather than the Executor contract,
 * in particular regarding the {@link org.springframework.core.task.TaskRejectedException}.
 *
 * <p><b>If you prefer native {@link java.util.concurrent.ExecutorService} exposure instead,
 * consider {@link ThreadPoolExecutorFactoryBean} as an alternative to this class.</b>
 *
 * <p>
 * JavaBean允许在bean样式(通过其"corePoolSize","maxPoolSize","keepAliveSeconds","queueCapacity"属性)中配置{@link javautilconcurrentThreadPoolExecutor}
 * ),并将其作为Spring {@link orgspringframeworkcoretaskTaskExecutor}将其暴露。
 * 适用于管理和监控(例如通过JMX),提供几个有用的属性："corePoolSize","maxPoolSize","keepAliveSeconds"(所有支持在运行时更新); "poolSize","
 * activeCount"(仅用于内省)。
 * 
 * <p>另外,您可以直接使用构造函数注入方式设置ThreadPoolExecutor实例,也可以使用指向{@link javautilconcurrentExecutors}类的工厂方法定义为了将这样一个
 * 原始的Executor作为Spring {@link orgspringframeworkcoretaskTaskExecutor}只需用{@link orgspringframeworkschedulingconcurrentConcurrentTaskExecutor}
 * 适配器包装即可。
 * 
 * <p> <b>注意：</b>此类实现了Spring的{@link orgspringframeworkcoretaskTaskExecutor}接口以及{@link javautilconcurrentExecutor}
 * 接口,前者是主界面,另一个仅作为辅助方便。
 * 原因,异常处理遵循TaskExecutor合同而不是Executor合同,特别是关于{@link orgspringframeworkcoretaskTaskRejectedException}。
 * 
 *  <p> <b>如果您更喜欢本机{@link javautilconcurrentExecutorService}曝光,请考虑{@link ThreadPoolExecutorFactoryBean}作
 * 为此类的替代方案</b>。
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.0
 * @see org.springframework.core.task.TaskExecutor
 * @see java.util.concurrent.ThreadPoolExecutor
 * @see ConcurrentTaskExecutor
 */
@SuppressWarnings("serial")
public class ThreadPoolTaskExecutor extends ExecutorConfigurationSupport
		implements AsyncListenableTaskExecutor, SchedulingTaskExecutor {

	private final Object poolSizeMonitor = new Object();

	private int corePoolSize = 1;

	private int maxPoolSize = Integer.MAX_VALUE;

	private int keepAliveSeconds = 60;

	private int queueCapacity = Integer.MAX_VALUE;

	private boolean allowCoreThreadTimeOut = false;

	private TaskDecorator taskDecorator;

	private ThreadPoolExecutor threadPoolExecutor;


	/**
	 * Set the ThreadPoolExecutor's core pool size.
	 * Default is 1.
	 * <p><b>This setting can be modified at runtime, for example through JMX.</b>
	 * <p>
	 *  设置ThreadPoolExecutor的核心池大小默认值为1 <p> <b>可以在运行时修改此设置,例如通过JMX </b>
	 * 
	 */
	public void setCorePoolSize(int corePoolSize) {
		synchronized (this.poolSizeMonitor) {
			this.corePoolSize = corePoolSize;
			if (this.threadPoolExecutor != null) {
				this.threadPoolExecutor.setCorePoolSize(corePoolSize);
			}
		}
	}

	/**
	 * Return the ThreadPoolExecutor's core pool size.
	 * <p>
	 * 返回ThreadPoolExecutor的核心池大小
	 * 
	 */
	public int getCorePoolSize() {
		synchronized (this.poolSizeMonitor) {
			return this.corePoolSize;
		}
	}

	/**
	 * Set the ThreadPoolExecutor's maximum pool size.
	 * Default is {@code Integer.MAX_VALUE}.
	 * <p><b>This setting can be modified at runtime, for example through JMX.</b>
	 * <p>
	 *  设置ThreadPoolExecutor的最大池大小默认值为{@code IntegerMAX_VALUE} <p> <b>可以在运行时修改此设置,例如通过JMX </b>
	 * 
	 */
	public void setMaxPoolSize(int maxPoolSize) {
		synchronized (this.poolSizeMonitor) {
			this.maxPoolSize = maxPoolSize;
			if (this.threadPoolExecutor != null) {
				this.threadPoolExecutor.setMaximumPoolSize(maxPoolSize);
			}
		}
	}

	/**
	 * Return the ThreadPoolExecutor's maximum pool size.
	 * <p>
	 *  返回ThreadPoolExecutor的最大池大小
	 * 
	 */
	public int getMaxPoolSize() {
		synchronized (this.poolSizeMonitor) {
			return this.maxPoolSize;
		}
	}

	/**
	 * Set the ThreadPoolExecutor's keep-alive seconds.
	 * Default is 60.
	 * <p><b>This setting can be modified at runtime, for example through JMX.</b>
	 * <p>
	 *  设置ThreadPoolExecutor的保持活动秒默认值为60 <p> <b>可以在运行时修改此设置,例如通过JMX </b>
	 * 
	 */
	public void setKeepAliveSeconds(int keepAliveSeconds) {
		synchronized (this.poolSizeMonitor) {
			this.keepAliveSeconds = keepAliveSeconds;
			if (this.threadPoolExecutor != null) {
				this.threadPoolExecutor.setKeepAliveTime(keepAliveSeconds, TimeUnit.SECONDS);
			}
		}
	}

	/**
	 * Return the ThreadPoolExecutor's keep-alive seconds.
	 * <p>
	 *  返回ThreadPoolExecutor的保持活动秒
	 * 
	 */
	public int getKeepAliveSeconds() {
		synchronized (this.poolSizeMonitor) {
			return this.keepAliveSeconds;
		}
	}

	/**
	 * Set the capacity for the ThreadPoolExecutor's BlockingQueue.
	 * Default is {@code Integer.MAX_VALUE}.
	 * <p>Any positive value will lead to a LinkedBlockingQueue instance;
	 * any other value will lead to a SynchronousQueue instance.
	 * <p>
	 *  设置ThreadPoolExecutor的BlockingQueue的容量默认值为{@code IntegerMAX_VALUE} <p>任何正值将导致LinkedBlockingQueue实例;任何
	 * 其他值都将导致一个SynchronousQueue实例。
	 * 
	 * 
	 * @see java.util.concurrent.LinkedBlockingQueue
	 * @see java.util.concurrent.SynchronousQueue
	 */
	public void setQueueCapacity(int queueCapacity) {
		this.queueCapacity = queueCapacity;
	}

	/**
	 * Specify whether to allow core threads to time out. This enables dynamic
	 * growing and shrinking even in combination with a non-zero queue (since
	 * the max pool size will only grow once the queue is full).
	 * <p>Default is "false".
	 * <p>
	 * 指定是否允许核心线程超时这使得即使与非零队列相结合也能够动态增长和缩小(因为最大池大小只有在队列满时才会增长)<p>默认值为"false"
	 * 
	 * 
	 * @see java.util.concurrent.ThreadPoolExecutor#allowCoreThreadTimeOut(boolean)
	 */
	public void setAllowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
		this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
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
	 *  指定要应用于任何要执行的{@link Runnable}的自定义{@link TaskDecorator} <p>请注意,这样的装饰器不一定适用于用户提供的{@code Runnable} / {@ code Callable }
	 * 而是实际的执行回调(可能是用户提供的任务周围的包装)<p>主要用例是在任务的调用周围设置一些执行上下文,或为任务执行提供一些监视/统计信息。
	 * 
	 * 
	 * @since 4.3
	 */
	public void setTaskDecorator(TaskDecorator taskDecorator) {
		this.taskDecorator = taskDecorator;
	}


	/**
	 * Note: This method exposes an {@link ExecutorService} to its base class
	 * but stores the actual {@link ThreadPoolExecutor} handle internally.
	 * Do not override this method for replacing the executor, rather just for
	 * decorating its {@code ExecutorService} handle or storing custom state.
	 * <p>
	 * 注意：此方法将一个{@link ExecutorService}暴露给其基类,但在内部存储实际的{@link ThreadPoolExecutor}句柄不要重写此方法来替换执行器,而只是为了装饰其{@code ExecutorService}
	 * 句柄或存储自定义州。
	 * 
	 */
	@Override
	protected ExecutorService initializeExecutor(
			ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {

		BlockingQueue<Runnable> queue = createQueue(this.queueCapacity);

		ThreadPoolExecutor executor;
		if (this.taskDecorator != null) {
			executor = new ThreadPoolExecutor(
					this.corePoolSize, this.maxPoolSize, this.keepAliveSeconds, TimeUnit.SECONDS,
					queue, threadFactory, rejectedExecutionHandler) {
				@Override
				public void execute(Runnable command) {
					super.execute(taskDecorator.decorate(command));
				}
			};
		}
		else {
			executor = new ThreadPoolExecutor(
					this.corePoolSize, this.maxPoolSize, this.keepAliveSeconds, TimeUnit.SECONDS,
					queue, threadFactory, rejectedExecutionHandler);

		}

		if (this.allowCoreThreadTimeOut) {
			executor.allowCoreThreadTimeOut(true);
		}

		this.threadPoolExecutor = executor;
		return executor;
	}

	/**
	 * Create the BlockingQueue to use for the ThreadPoolExecutor.
	 * <p>A LinkedBlockingQueue instance will be created for a positive
	 * capacity value; a SynchronousQueue else.
	 * <p>
	 *  创建要用于ThreadPoolExecutor的BlockingQueue <p>将为正容量值创建LinkedBlockingQueue实例;一个SynchronousQueue其他
	 * 
	 * 
	 * @param queueCapacity the specified queue capacity
	 * @return the BlockingQueue instance
	 * @see java.util.concurrent.LinkedBlockingQueue
	 * @see java.util.concurrent.SynchronousQueue
	 */
	protected BlockingQueue<Runnable> createQueue(int queueCapacity) {
		if (queueCapacity > 0) {
			return new LinkedBlockingQueue<Runnable>(queueCapacity);
		}
		else {
			return new SynchronousQueue<Runnable>();
		}
	}

	/**
	 * Return the underlying ThreadPoolExecutor for native access.
	 * <p>
	 *  返回底层的ThreadPoolExecutor进行本机访问
	 * 
	 * 
	 * @return the underlying ThreadPoolExecutor (never {@code null})
	 * @throws IllegalStateException if the ThreadPoolTaskExecutor hasn't been initialized yet
	 */
	public ThreadPoolExecutor getThreadPoolExecutor() throws IllegalStateException {
		Assert.state(this.threadPoolExecutor != null, "ThreadPoolTaskExecutor not initialized");
		return this.threadPoolExecutor;
	}

	/**
	 * Return the current pool size.
	 * <p>
	 *  返回当前池大小
	 * 
	 * 
	 * @see java.util.concurrent.ThreadPoolExecutor#getPoolSize()
	 */
	public int getPoolSize() {
		if (this.threadPoolExecutor == null) {
			// Not initialized yet: assume core pool size.
			return this.corePoolSize;
		}
		return this.threadPoolExecutor.getPoolSize();
	}

	/**
	 * Return the number of currently active threads.
	 * <p>
	 *  返回当前活动线程的数量
	 * 
	 * 
	 * @see java.util.concurrent.ThreadPoolExecutor#getActiveCount()
	 */
	public int getActiveCount() {
		if (this.threadPoolExecutor == null) {
			// Not initialized yet: assume no active threads.
			return 0;
		}
		return this.threadPoolExecutor.getActiveCount();
	}


	@Override
	public void execute(Runnable task) {
		Executor executor = getThreadPoolExecutor();
		try {
			executor.execute(task);
		}
		catch (RejectedExecutionException ex) {
			throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
		}
	}

	@Override
	public void execute(Runnable task, long startTimeout) {
		execute(task);
	}

	@Override
	public Future<?> submit(Runnable task) {
		ExecutorService executor = getThreadPoolExecutor();
		try {
			return executor.submit(task);
		}
		catch (RejectedExecutionException ex) {
			throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
		}
	}

	@Override
	public <T> Future<T> submit(Callable<T> task) {
		ExecutorService executor = getThreadPoolExecutor();
		try {
			return executor.submit(task);
		}
		catch (RejectedExecutionException ex) {
			throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
		}
	}

	@Override
	public ListenableFuture<?> submitListenable(Runnable task) {
		ExecutorService executor = getThreadPoolExecutor();
		try {
			ListenableFutureTask<Object> future = new ListenableFutureTask<Object>(task, null);
			executor.execute(future);
			return future;
		}
		catch (RejectedExecutionException ex) {
			throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
		}
	}

	@Override
	public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
		ExecutorService executor = getThreadPoolExecutor();
		try {
			ListenableFutureTask<T> future = new ListenableFutureTask<T>(task);
			executor.execute(future);
			return future;
		}
		catch (RejectedExecutionException ex) {
			throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
		}
	}

	/**
	 * This task executor prefers short-lived work units.
	 * <p>
	 *  这个任务执行者喜欢短命的工作单位
	 */
	@Override
	public boolean prefersShortLivedTasks() {
		return true;
	}

}
