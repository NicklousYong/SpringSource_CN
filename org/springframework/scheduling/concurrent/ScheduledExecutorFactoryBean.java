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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.lang.UsesJava7;
import org.springframework.scheduling.support.DelegatingErrorHandlingRunnable;
import org.springframework.scheduling.support.TaskUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

/**
 * {@link org.springframework.beans.factory.FactoryBean} that sets up
 * a {@link java.util.concurrent.ScheduledExecutorService}
 * (by default: a {@link java.util.concurrent.ScheduledThreadPoolExecutor})
 * and exposes it for bean references.
 *
 * <p>Allows for registration of {@link ScheduledExecutorTask ScheduledExecutorTasks},
 * automatically starting the {@link ScheduledExecutorService} on initialization and
 * cancelling it on destruction of the context. In scenarios that only require static
 * registration of tasks at startup, there is no need to access the
 * {@link ScheduledExecutorService} instance itself in application code at all;
 * {@code ScheduledExecutorFactoryBean} is then just being used for lifecycle integration.
 *
 * <p>For an alternative, you may set up a {@link ScheduledThreadPoolExecutor} instance
 * directly using constructor injection, or use a factory method definition that points
 * to the {@link java.util.concurrent.Executors} class.
 * <b>This is strongly recommended in particular for common {@code @Bean} methods in
 * configuration classes, where this {@code FactoryBean} variant would force you to
 * return the {@code FactoryBean} type instead of {@code ScheduledExecutorService}.</b>
 *
 * <p>Note that {@link java.util.concurrent.ScheduledExecutorService}
 * uses a {@link Runnable} instance that is shared between repeated executions,
 * in contrast to Quartz which instantiates a new Job for each execution.
 *
 * <p><b>WARNING:</b> {@link Runnable Runnables} submitted via a native
 * {@link java.util.concurrent.ScheduledExecutorService} are removed from
 * the execution schedule once they throw an exception. If you would prefer
 * to continue execution after such an exception, switch this FactoryBean's
 * {@link #setContinueScheduledExecutionAfterException "continueScheduledExecutionAfterException"}
 * property to "true".
 *
 * <p>
 * {@link orgspringframeworkbeansfactoryFactoryBean}设置一个{@link javautilconcurrentScheduledExecutorService}
 * (默认情况下是一个{@link javautilconcurrentScheduledThreadPoolExecutor}),并将其公开给bean引用。
 * 
 *  <p>允许注册{@link ScheduledExecutorTask ScheduledExecutorTasks},在初始化时自动启动{@link ScheduledExecutorService}
 * ,并在上下文销毁时取消它。
 * 在启动时仅需要静态注册任务的情况下,无需访问{@link ScheduledExecutorService}实例本身在应用程序代码中; {@code ScheduledExecutorFactoryBean}
 * 然后被用于生命周期集成。
 * 
 * <p>另外,您可以使用构造函数注入直接设置{@link ScheduledThreadPoolExecutor}实例,也可以使用指向{@link javautilconcurrentExecutors}
 * 类的工厂方法定义<b>特别强调建议{@code @Bean}方法在配置类中,其中{@code FactoryBean}变体将强制您返回{@code FactoryBean}类型而不是{@code ScheduledExecutorService}
 *  </b>。
 * 
 *  <p>请注意,{@link javautilconcurrentScheduledExecutorService}使用重复执行之间共享的{@link Runnable}实例,与Quartz相反,Qua
 * rtz会为每个执行实例化一个新作业。
 * 
 * 警告：通过本机{@link javautilconcurrentScheduledExecutorService}提交的</b> {@link Runnable Runnables}在抛出异常后将从执行
 * 计划中删除如果您希望在这样的异常之后继续执行,将此FactoryBean的{@link #setContinueScheduledExecutionAfterException"continueScheduledExecutionAfterException"}
 * 属性切换为"true"。
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.0
 * @see #setPoolSize
 * @see #setRemoveOnCancelPolicy
 * @see #setThreadFactory
 * @see ScheduledExecutorTask
 * @see java.util.concurrent.ScheduledExecutorService
 * @see java.util.concurrent.ScheduledThreadPoolExecutor
 */
@SuppressWarnings("serial")
public class ScheduledExecutorFactoryBean extends ExecutorConfigurationSupport
		implements FactoryBean<ScheduledExecutorService> {

	// ScheduledThreadPoolExecutor.setRemoveOnCancelPolicy(boolean) only available on JDK 7+
	private static final boolean setRemoveOnCancelPolicyAvailable =
			ClassUtils.hasMethod(ScheduledThreadPoolExecutor.class, "setRemoveOnCancelPolicy", boolean.class);


	private int poolSize = 1;

	private ScheduledExecutorTask[] scheduledExecutorTasks;

	private boolean removeOnCancelPolicy = false;

	private boolean continueScheduledExecutionAfterException = false;

	private boolean exposeUnconfigurableExecutor = false;

	private ScheduledExecutorService exposedExecutor;


	/**
	 * Set the ScheduledExecutorService's pool size.
	 * Default is 1.
	 * <p>
	 *  设置ScheduledExecutorService的池大小默认值为1
	 * 
	 */
	public void setPoolSize(int poolSize) {
		Assert.isTrue(poolSize > 0, "'poolSize' must be 1 or higher");
		this.poolSize = poolSize;
	}

	/**
	 * Register a list of ScheduledExecutorTask objects with the ScheduledExecutorService
	 * that this FactoryBean creates. Depending on each ScheduledExecutorTask's settings,
	 * it will be registered via one of ScheduledExecutorService's schedule methods.
	 * <p>
	 *  使用此FactoryBean创建的ScheduledExecutorService注册ScheduledExecutorTask对象的列表根据每个ScheduledExecutorTask的设置,它将
	 * 通过ScheduledExecutorService的计划方法之一注册。
	 * 
	 * 
	 * @see java.util.concurrent.ScheduledExecutorService#schedule(java.lang.Runnable, long, java.util.concurrent.TimeUnit)
	 * @see java.util.concurrent.ScheduledExecutorService#scheduleWithFixedDelay(java.lang.Runnable, long, long, java.util.concurrent.TimeUnit)
	 * @see java.util.concurrent.ScheduledExecutorService#scheduleAtFixedRate(java.lang.Runnable, long, long, java.util.concurrent.TimeUnit)
	 */
	public void setScheduledExecutorTasks(ScheduledExecutorTask... scheduledExecutorTasks) {
		this.scheduledExecutorTasks = scheduledExecutorTasks;
	}

	/**
	 * Set the remove-on-cancel mode on {@link ScheduledThreadPoolExecutor} (JDK 7+).
	 * <p>Default is {@code false}. If set to {@code true}, the target executor will be
	 * switched into remove-on-cancel mode (if possible, with a soft fallback otherwise).
	 * <p>
	 * 在{@link ScheduledThreadPoolExecutor}(JDK 7+)上设置取消取消模式<p>默认值为{@code false}如果设置为{@code true},目标执行程序将被切换
	 * 为取消取消模式(如果可能,否则为软回退)。
	 * 
	 */
	public void setRemoveOnCancelPolicy(boolean removeOnCancelPolicy) {
		this.removeOnCancelPolicy = removeOnCancelPolicy;
	}

	/**
	 * Specify whether to continue the execution of a scheduled task
	 * after it threw an exception.
	 * <p>Default is "false", matching the native behavior of a
	 * {@link java.util.concurrent.ScheduledExecutorService}.
	 * Switch this flag to "true" for exception-proof execution of each task,
	 * continuing scheduled execution as in the case of successful execution.
	 * <p>
	 *  指定在抛出异常后是否继续执行计划任务<p>默认值为"false",与{@link javautilconcurrentScheduledExecutorService}的本机行为相匹配将此标志切换为"
	 * true",以进行每个任务的异常执行在执行成功的情况下继续执行。
	 * 
	 * 
	 * @see java.util.concurrent.ScheduledExecutorService#scheduleAtFixedRate
	 */
	public void setContinueScheduledExecutionAfterException(boolean continueScheduledExecutionAfterException) {
		this.continueScheduledExecutionAfterException = continueScheduledExecutionAfterException;
	}

	/**
	 * Specify whether this FactoryBean should expose an unconfigurable
	 * decorator for the created executor.
	 * <p>Default is "false", exposing the raw executor as bean reference.
	 * Switch this flag to "true" to strictly prevent clients from
	 * modifying the executor's configuration.
	 * <p>
	 * 指定此FactoryBean是否应为已创建的执行程序公开一个不可配置的装饰器<p>默认为"false",将原始执行程序暴露为bean参考将此标志设置为"true",以严格防止客户端修改执行程序的配置
	 * 
	 * 
	 * @see java.util.concurrent.Executors#unconfigurableScheduledExecutorService
	 */
	public void setExposeUnconfigurableExecutor(boolean exposeUnconfigurableExecutor) {
		this.exposeUnconfigurableExecutor = exposeUnconfigurableExecutor;
	}


	@Override
	@UsesJava7
	protected ExecutorService initializeExecutor(
			ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {

		ScheduledExecutorService executor =
				createExecutor(this.poolSize, threadFactory, rejectedExecutionHandler);

		if (this.removeOnCancelPolicy) {
			if (setRemoveOnCancelPolicyAvailable && executor instanceof ScheduledThreadPoolExecutor) {
				((ScheduledThreadPoolExecutor) executor).setRemoveOnCancelPolicy(true);
			}
			else {
				logger.info("Could not apply remove-on-cancel policy - not a Java 7+ ScheduledThreadPoolExecutor");
			}
		}

		// Register specified ScheduledExecutorTasks, if necessary.
		if (!ObjectUtils.isEmpty(this.scheduledExecutorTasks)) {
			registerTasks(this.scheduledExecutorTasks, executor);
		}

		// Wrap executor with an unconfigurable decorator.
		this.exposedExecutor = (this.exposeUnconfigurableExecutor ?
				Executors.unconfigurableScheduledExecutorService(executor) : executor);

		return executor;
	}

	/**
	 * Create a new {@link ScheduledExecutorService} instance.
	 * <p>The default implementation creates a {@link ScheduledThreadPoolExecutor}.
	 * Can be overridden in subclasses to provide custom {@link ScheduledExecutorService} instances.
	 * <p>
	 *  创建一个新的{@link ScheduledExecutorService}实例<p>默认实现创建一个{@link ScheduledThreadPoolExecutor}可以在子类中覆盖以提供自定义
	 * {@link ScheduledExecutorService}实例。
	 * 
	 * 
	 * @param poolSize the specified pool size
	 * @param threadFactory the ThreadFactory to use
	 * @param rejectedExecutionHandler the RejectedExecutionHandler to use
	 * @return a new ScheduledExecutorService instance
	 * @see #afterPropertiesSet()
	 * @see java.util.concurrent.ScheduledThreadPoolExecutor
	 */
	protected ScheduledExecutorService createExecutor(
			int poolSize, ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {

		return new ScheduledThreadPoolExecutor(poolSize, threadFactory, rejectedExecutionHandler);
	}

	/**
	 * Register the specified {@link ScheduledExecutorTask ScheduledExecutorTasks}
	 * on the given {@link ScheduledExecutorService}.
	 * <p>
	 *  在给定的{@link ScheduledExecutorService}上注册指定的{@link ScheduledExecutorTask ScheduledExecutorTasks}
	 * 
	 * 
	 * @param tasks the specified ScheduledExecutorTasks (never empty)
	 * @param executor the ScheduledExecutorService to register the tasks on.
	 */
	protected void registerTasks(ScheduledExecutorTask[] tasks, ScheduledExecutorService executor) {
		for (ScheduledExecutorTask task : tasks) {
			Runnable runnable = getRunnableToSchedule(task);
			if (task.isOneTimeTask()) {
				executor.schedule(runnable, task.getDelay(), task.getTimeUnit());
			}
			else {
				if (task.isFixedRate()) {
					executor.scheduleAtFixedRate(runnable, task.getDelay(), task.getPeriod(), task.getTimeUnit());
				}
				else {
					executor.scheduleWithFixedDelay(runnable, task.getDelay(), task.getPeriod(), task.getTimeUnit());
				}
			}
		}
	}

	/**
	 * Determine the actual Runnable to schedule for the given task.
	 * <p>Wraps the task's Runnable in a
	 * {@link org.springframework.scheduling.support.DelegatingErrorHandlingRunnable}
	 * that will catch and log the Exception. If necessary, it will suppress the
	 * Exception according to the
	 * {@link #setContinueScheduledExecutionAfterException "continueScheduledExecutionAfterException"}
	 * flag.
	 * <p>
	 * 确定为给定任务计划的实际Runnable <p>将任务的Runnable包装在将捕获并记录异常的{@link orgspringframeworkschedulingsupportDelegatingErrorHandlingRunnable}
	 * 中如果需要,它将根据{@link #setContinueScheduledExecutionAfterException"continueScheduledExecutionAfterException"禁止异常" }
	 * 
	 * @param task the ScheduledExecutorTask to schedule
	 * @return the actual Runnable to schedule (may be a decorator)
	 */
	protected Runnable getRunnableToSchedule(ScheduledExecutorTask task) {
		return (this.continueScheduledExecutionAfterException ?
				new DelegatingErrorHandlingRunnable(task.getRunnable(), TaskUtils.LOG_AND_SUPPRESS_ERROR_HANDLER) :
				new DelegatingErrorHandlingRunnable(task.getRunnable(), TaskUtils.LOG_AND_PROPAGATE_ERROR_HANDLER));
	}


	@Override
	public ScheduledExecutorService getObject() {
		return this.exposedExecutor;
	}

	@Override
	public Class<? extends ScheduledExecutorService> getObjectType() {
		return (this.exposedExecutor != null ? this.exposedExecutor.getClass() : ScheduledExecutorService.class);
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
