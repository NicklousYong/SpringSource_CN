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

package org.springframework.scheduling.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * JavaBean that allows for configuring a {@link java.util.concurrent.ThreadPoolExecutor}
 * in bean style (through its "corePoolSize", "maxPoolSize", "keepAliveSeconds",
 * "queueCapacity" properties) and exposing it as a bean reference of its native
 * {@link java.util.concurrent.ExecutorService} type.
 *
 * <p>For an alternative, you may set up a {@link ThreadPoolExecutor} instance directly
 * using constructor injection, or use a factory method definition that points to the
 * {@link java.util.concurrent.Executors} class.
 * <b>This is strongly recommended in particular for common {@code @Bean} methods in
 * configuration classes, where this {@code FactoryBean} variant would force you to
 * return the {@code FactoryBean} type instead of the actual {@code Executor} type.</b>
 *
 * <p>If you need a timing-based {@link java.util.concurrent.ScheduledExecutorService}
 * instead, consider {@link ScheduledExecutorFactoryBean}.

 * <p>
 * JavaBean允许在bean样式(通过其"corePoolSize","maxPoolSize","keepAliveSeconds","queueCapacity"属性)中配置{@link javautilconcurrentThreadPoolExecutor}
 * ),并将其作为其本机{@link javautilconcurrentExecutorService}类型的bean引用。
 * 
 *  <p>另外,您可以使用构造函数注入直接设置{@link ThreadPoolExecutor}实例,也可以使用指向{@link javautilconcurrentExecutors}类的工厂方法定义
 * <b>特别强调建议{@code @Bean}方法在配置类中,其中{@code FactoryBean}变体将强制您返回{@code FactoryBean}类型,而不是实际的{@code Executor}
 * 类型</b>。
 * 
 * <p>如果您需要基于时间的{@link javautilconcurrentScheduledExecutorService},请考虑{@link ScheduledExecutorFactoryBean}
 * 。
 * 
 * 
 * @author Juergen Hoeller
 * @since 3.0
 * @see java.util.concurrent.ExecutorService
 * @see java.util.concurrent.Executors
 * @see java.util.concurrent.ThreadPoolExecutor
 */
@SuppressWarnings("serial")
public class ThreadPoolExecutorFactoryBean extends ExecutorConfigurationSupport
		implements FactoryBean<ExecutorService>, InitializingBean, DisposableBean {

	private int corePoolSize = 1;

	private int maxPoolSize = Integer.MAX_VALUE;

	private int keepAliveSeconds = 60;

	private boolean allowCoreThreadTimeOut = false;

	private int queueCapacity = Integer.MAX_VALUE;

	private boolean exposeUnconfigurableExecutor = false;

	private ExecutorService exposedExecutor;


	/**
	 * Set the ThreadPoolExecutor's core pool size.
	 * Default is 1.
	 * <p>
	 *  设置ThreadPoolExecutor的核心池大小默认值为1
	 * 
	 */
	public void setCorePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}

	/**
	 * Set the ThreadPoolExecutor's maximum pool size.
	 * Default is {@code Integer.MAX_VALUE}.
	 * <p>
	 *  设置ThreadPoolExecutor的最大池大小默认值为{@code IntegerMAX_VALUE}
	 * 
	 */
	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}

	/**
	 * Set the ThreadPoolExecutor's keep-alive seconds.
	 * Default is 60.
	 * <p>
	 *  设置ThreadPoolExecutor的keep-alive秒默认值为60
	 * 
	 */
	public void setKeepAliveSeconds(int keepAliveSeconds) {
		this.keepAliveSeconds = keepAliveSeconds;
	}

	/**
	 * Specify whether to allow core threads to time out. This enables dynamic
	 * growing and shrinking even in combination with a non-zero queue (since
	 * the max pool size will only grow once the queue is full).
	 * <p>Default is "false".
	 * <p>
	 *  指定是否允许核心线程超时这使得即使与非零队列相结合也能够动态增长和缩小(因为最大池大小只有在队列满时才会增长)<p>默认值为"false"
	 * 
	 * 
	 * @see java.util.concurrent.ThreadPoolExecutor#allowCoreThreadTimeOut(boolean)
	 */
	public void setAllowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
		this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
	}

	/**
	 * Set the capacity for the ThreadPoolExecutor's BlockingQueue.
	 * Default is {@code Integer.MAX_VALUE}.
	 * <p>Any positive value will lead to a LinkedBlockingQueue instance;
	 * any other value will lead to a SynchronousQueue instance.
	 * <p>
	 * 设置ThreadPoolExecutor的BlockingQueue的容量默认值为{@code IntegerMAX_VALUE} <p>任何正值将导致LinkedBlockingQueue实例;任何其
	 * 他值都将导致一个SynchronousQueue实例。
	 * 
	 * 
	 * @see java.util.concurrent.LinkedBlockingQueue
	 * @see java.util.concurrent.SynchronousQueue
	 */
	public void setQueueCapacity(int queueCapacity) {
		this.queueCapacity = queueCapacity;
	}

	/**
	 * Specify whether this FactoryBean should expose an unconfigurable
	 * decorator for the created executor.
	 * <p>Default is "false", exposing the raw executor as bean reference.
	 * Switch this flag to "true" to strictly prevent clients from
	 * modifying the executor's configuration.
	 * <p>
	 *  指定此FactoryBean是否应为已创建的执行程序公开一个不可配置的装饰器<p>默认为"false",将原始执行程序暴露为bean参考将此标志设置为"true",以严格防止客户端修改执行程序的配置。
	 * 
	 * 
	 * @see java.util.concurrent.Executors#unconfigurableExecutorService
	 */
	public void setExposeUnconfigurableExecutor(boolean exposeUnconfigurableExecutor) {
		this.exposeUnconfigurableExecutor = exposeUnconfigurableExecutor;
	}


	@Override
	protected ExecutorService initializeExecutor(
			ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {

		BlockingQueue<Runnable> queue = createQueue(this.queueCapacity);
		ThreadPoolExecutor executor  = createExecutor(this.corePoolSize, this.maxPoolSize,
				this.keepAliveSeconds, queue, threadFactory, rejectedExecutionHandler);
		if (this.allowCoreThreadTimeOut) {
			executor.allowCoreThreadTimeOut(true);
		}

		// Wrap executor with an unconfigurable decorator.
		this.exposedExecutor = (this.exposeUnconfigurableExecutor ?
				Executors.unconfigurableExecutorService(executor) : executor);

		return executor;
	}

	/**
	 * Create a new instance of {@link ThreadPoolExecutor} or a subclass thereof.
	 * <p>The default implementation creates a standard {@link ThreadPoolExecutor}.
	 * Can be overridden to provide custom {@link ThreadPoolExecutor} subclasses.
	 * <p>
	 *  创建{@link ThreadPoolExecutor}或其子类的新实例<p>默认实现创建标准的{@link ThreadPoolExecutor}可以覆盖以提供自定义{@link ThreadPoolExecutor}
	 * 子类。
	 * 
	 * 
	 * @param corePoolSize the specified core pool size
	 * @param maxPoolSize the specified maximum pool size
	 * @param keepAliveSeconds the specified keep-alive time in seconds
	 * @param queue the BlockingQueue to use
	 * @param threadFactory the ThreadFactory to use
	 * @param rejectedExecutionHandler the RejectedExecutionHandler to use
	 * @return a new ThreadPoolExecutor instance
	 * @see #afterPropertiesSet()
	 */
	protected ThreadPoolExecutor createExecutor(
			int corePoolSize, int maxPoolSize, int keepAliveSeconds, BlockingQueue<Runnable> queue,
			ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {

		return new ThreadPoolExecutor(corePoolSize, maxPoolSize,
				keepAliveSeconds, TimeUnit.SECONDS, queue, threadFactory, rejectedExecutionHandler);
	}

	/**
	 * Create the BlockingQueue to use for the ThreadPoolExecutor.
	 * <p>A LinkedBlockingQueue instance will be created for a positive
	 * capacity value; a SynchronousQueue else.
	 * <p>
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


	@Override
	public ExecutorService getObject() {
		return this.exposedExecutor;
	}

	@Override
	public Class<? extends ExecutorService> getObjectType() {
		return (this.exposedExecutor != null ? this.exposedExecutor.getClass() : ExecutorService.class);
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
