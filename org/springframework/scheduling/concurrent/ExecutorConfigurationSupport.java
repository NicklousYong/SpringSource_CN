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

package org.springframework.scheduling.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Base class for classes that are setting up a
 * {@code java.util.concurrent.ExecutorService}
 * (typically a {@link java.util.concurrent.ThreadPoolExecutor}).
 * Defines common configuration settings and common lifecycle handling.
 *
 * <p>
 *  正在设置{@code javautilconcurrentExecutorService}的类的基类(通常为{@link javautilconcurrentThreadPoolExecutor}))
 * 定义常见的配置设置和常用的生命周期处理。
 * 
 * 
 * @author Juergen Hoeller
 * @since 3.0
 * @see java.util.concurrent.ExecutorService
 * @see java.util.concurrent.Executors
 * @see java.util.concurrent.ThreadPoolExecutor
 */
@SuppressWarnings("serial")
public abstract class ExecutorConfigurationSupport extends CustomizableThreadFactory
		implements BeanNameAware, InitializingBean, DisposableBean {

	protected final Log logger = LogFactory.getLog(getClass());

	private ThreadFactory threadFactory = this;

	private boolean threadNamePrefixSet = false;

	private RejectedExecutionHandler rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy();

	private boolean waitForTasksToCompleteOnShutdown = false;

	private int awaitTerminationSeconds = 0;

	private String beanName;

	private ExecutorService executor;


	/**
	 * Set the ThreadFactory to use for the ExecutorService's thread pool.
	 * Default is the underlying ExecutorService's default thread factory.
	 * <p>In a Java EE 7 or other managed environment with JSR-236 support,
	 * consider specifying a JNDI-located ManagedThreadFactory: by default,
	 * to be found at "java:comp/DefaultManagedThreadFactory".
	 * Use the "jee:jndi-lookup" namespace element in XML or the programmatic
	 * {@link org.springframework.jndi.JndiLocatorDelegate} for convenient lookup.
	 * Alternatively, consider using Spring's {@link DefaultManagedAwareThreadFactory}
	 * with its fallback to local threads in case of no managed thread factory found.
	 * <p>
	 * 将ThreadFactory设置为用于ExecutorService的线程池默认值为底层ExecutorService的默认线程工厂<p>在Java EE 7或其他支持JSR-236的托管环境中,请考虑
	 * 指定位于JNDI的ManagedThreadFactory：默认情况下,可以找到在"java：comp / DefaultManagedThreadFactory"中使用XML中的"jee：jndi-l
	 * ookup"命名空间元素或程序化的{@link orgspringframeworkjndiJndiLocatorDelegate}来方便查找。
	 * 或者,考虑使用Spring的{@link DefaultManagedAwareThreadFactory}将其回退到本地线程,以防万一没有找到管理线程工厂。
	 * 
	 * 
	 * @see java.util.concurrent.Executors#defaultThreadFactory()
	 * @see javax.enterprise.concurrent.ManagedThreadFactory
	 * @see DefaultManagedAwareThreadFactory
	 */
	public void setThreadFactory(ThreadFactory threadFactory) {
		this.threadFactory = (threadFactory != null ? threadFactory : this);
	}

	@Override
	public void setThreadNamePrefix(String threadNamePrefix) {
		super.setThreadNamePrefix(threadNamePrefix);
		this.threadNamePrefixSet = true;
	}

	/**
	 * Set the RejectedExecutionHandler to use for the ExecutorService.
	 * Default is the ExecutorService's default abort policy.
	 * <p>
	 *  将RejectedExecutionHandler设置为ExecutorService使用默认值是ExecutorService的默认中止策略
	 * 
	 * 
	 * @see java.util.concurrent.ThreadPoolExecutor.AbortPolicy
	 */
	public void setRejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
		this.rejectedExecutionHandler =
				(rejectedExecutionHandler != null ? rejectedExecutionHandler : new ThreadPoolExecutor.AbortPolicy());
	}

	/**
	 * Set whether to wait for scheduled tasks to complete on shutdown,
	 * not interrupting running tasks and executing all tasks in the queue.
	 * <p>Default is "false", shutting down immediately through interrupting
	 * ongoing tasks and clearing the queue. Switch this flag to "true" if you
	 * prefer fully completed tasks at the expense of a longer shutdown phase.
	 * <p>Note that Spring's container shutdown continues while ongoing tasks
	 * are being completed. If you want this executor to block and wait for the
	 * termination of tasks before the rest of the container continues to shut
	 * down - e.g. in order to keep up other resources that your tasks may need -,
	 * set the {@link #setAwaitTerminationSeconds "awaitTerminationSeconds"}
	 * property instead of or in addition to this property.
	 * <p>
	 * 设置是否等待计划任务在关机时完成,不中断运行任务并执行队列中的所有任务<p>默认为"false",通过中断正在进行的任务并清除队列立即关闭将此标志切换为"true"如果您喜欢完全完成的任务,代价更长的关
	 * 闭阶段<p>请注意,正在完成的任务期间,Spring的容器关闭将继续进行如果您希望此执行程序在其余的容器继续之前阻止并等待任务的终止关闭 - 例如为了保持您的任务可能需要的其他资源,请设置{@link #setAwaitTerminationSeconds"awaitTerminationSeconds"}
	 * 属性,而不是此属性或附加属性。
	 * 
	 * 
	 * @see java.util.concurrent.ExecutorService#shutdown()
	 * @see java.util.concurrent.ExecutorService#shutdownNow()
	 */
	public void setWaitForTasksToCompleteOnShutdown(boolean waitForJobsToCompleteOnShutdown) {
		this.waitForTasksToCompleteOnShutdown = waitForJobsToCompleteOnShutdown;
	}

	/**
	 * Set the maximum number of seconds that this executor is supposed to block
	 * on shutdown in order to wait for remaining tasks to complete their execution
	 * before the rest of the container continues to shut down. This is particularly
	 * useful if your remaining tasks are likely to need access to other resources
	 * that are also managed by the container.
	 * <p>By default, this executor won't wait for the termination of tasks at all.
	 * It will either shut down immediately, interrupting ongoing tasks and clearing
	 * the remaining task queue - or, if the
	 * {@link #setWaitForTasksToCompleteOnShutdown "waitForTasksToCompleteOnShutdown"}
	 * flag has been set to {@code true}, it will continue to fully execute all
	 * ongoing tasks as well as all remaining tasks in the queue, in parallel to
	 * the rest of the container shutting down.
	 * <p>In either case, if you specify an await-termination period using this property,
	 * this executor will wait for the given time (max) for the termination of tasks.
	 * As a rule of thumb, specify a significantly higher timeout here if you set
	 * "waitForTasksToCompleteOnShutdown" to {@code true} at the same time,
	 * since all remaining tasks in the queue will still get executed - in contrast
	 * to the default shutdown behavior where it's just about waiting for currently
	 * executing tasks that aren't reacting to thread interruption.
	 * <p>
	 * 设置此执行程序在关闭时应阻止的最大秒数,以便在剩余的任务可能需要访问之前等待其余任务完成执行,然后再继续关闭它。
	 * 由容器管理的其他资源<p>默认情况下,此执行程序根本不会等待终止任务它将立即关闭,中断正在进行的任务并清除剩余的任务队列 - 或者如果{@link #setWaitForTasksToCompleteOnShutdown"waitForTasksToCompleteOnShutdown"}
	 * 标志已设置为{@code true},它将继续完全执行所有正在进行的任务以及队列中剩余的所有任务,与容器的其余部分并行关闭<p>在任一情况下,如果使用此属性指定等待终止期,则此执行程序将等待给定时间(最
	 * 大)为终止任务作为经验法则,如果您在同一时间设置"waitForTasksToCompleteOnShutdown"为{@code true},则在此处指定一个显着更高的超时,因为队列中的所有剩余任务仍
	 * 将执行 - 与默认关闭行为相反,只是等待当前正在执行的任务不会对线程中断做出反应。
	 * 设置此执行程序在关闭时应阻止的最大秒数,以便在剩余的任务可能需要访问之前等待其余任务完成执行,然后再继续关闭它。
	 * 
	 * 
	 * @see java.util.concurrent.ExecutorService#shutdown()
	 * @see java.util.concurrent.ExecutorService#awaitTermination
	 */
	public void setAwaitTerminationSeconds(int awaitTerminationSeconds) {
		this.awaitTerminationSeconds = awaitTerminationSeconds;
	}

	@Override
	public void setBeanName(String name) {
		this.beanName = name;
	}


	/**
	 * Calls {@code initialize()} after the container applied all property values.
	 * <p>
	 * 在容器应用所有属性值之后调用{@code initialize()}
	 * 
	 * 
	 * @see #initialize()
	 */
	@Override
	public void afterPropertiesSet() {
		initialize();
	}

	/**
	 * Set up the ExecutorService.
	 * <p>
	 *  设置ExecutorService
	 * 
	 */
	public void initialize() {
		if (logger.isInfoEnabled()) {
			logger.info("Initializing ExecutorService " + (this.beanName != null ? " '" + this.beanName + "'" : ""));
		}
		if (!this.threadNamePrefixSet && this.beanName != null) {
			setThreadNamePrefix(this.beanName + "-");
		}
		this.executor = initializeExecutor(this.threadFactory, this.rejectedExecutionHandler);
	}

	/**
	 * Create the target {@link java.util.concurrent.ExecutorService} instance.
	 * Called by {@code afterPropertiesSet}.
	 * <p>
	 *  创建目标{@link javautilconcurrentExecutorService}实例调用{@code afterPropertiesSet}
	 * 
	 * 
	 * @param threadFactory the ThreadFactory to use
	 * @param rejectedExecutionHandler the RejectedExecutionHandler to use
	 * @return a new ExecutorService instance
	 * @see #afterPropertiesSet()
	 */
	protected abstract ExecutorService initializeExecutor(
			ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler);


	/**
	 * Calls {@code shutdown} when the BeanFactory destroys
	 * the task executor instance.
	 * <p>
	 *  当BeanFactory破坏任务执行器实例时调用{@code shutdown}
	 * 
	 * 
	 * @see #shutdown()
	 */
	@Override
	public void destroy() {
		shutdown();
	}

	/**
	 * Perform a shutdown on the underlying ExecutorService.
	 * <p>
	 *  在底层的ExecutorService上执行关机
	 * 
	 * 
	 * @see java.util.concurrent.ExecutorService#shutdown()
	 * @see java.util.concurrent.ExecutorService#shutdownNow()
	 * @see #awaitTerminationIfNecessary()
	 */
	public void shutdown() {
		if (logger.isInfoEnabled()) {
			logger.info("Shutting down ExecutorService" + (this.beanName != null ? " '" + this.beanName + "'" : ""));
		}
		if (this.waitForTasksToCompleteOnShutdown) {
			this.executor.shutdown();
		}
		else {
			this.executor.shutdownNow();
		}
		awaitTerminationIfNecessary();
	}

	/**
	 * Wait for the executor to terminate, according to the value of the
	 * {@link #setAwaitTerminationSeconds "awaitTerminationSeconds"} property.
	 * <p>
	 *  根据{@link #setAwaitTerminationSeconds"awaitTerminationSeconds"}属性的值,等待执行者终止
	 */
	private void awaitTerminationIfNecessary() {
		if (this.awaitTerminationSeconds > 0) {
			try {
				if (!this.executor.awaitTermination(this.awaitTerminationSeconds, TimeUnit.SECONDS)) {
					if (logger.isWarnEnabled()) {
						logger.warn("Timed out while waiting for executor" +
								(this.beanName != null ? " '" + this.beanName + "'" : "") + " to terminate");
					}
				}
			}
			catch (InterruptedException ex) {
				if (logger.isWarnEnabled()) {
					logger.warn("Interrupted while waiting for executor" +
							(this.beanName != null ? " '" + this.beanName + "'" : "") + " to terminate");
				}
				Thread.currentThread().interrupt();
			}
		}
	}

}
