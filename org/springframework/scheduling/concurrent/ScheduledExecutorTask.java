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

import java.util.concurrent.TimeUnit;

/**
 * JavaBean that describes a scheduled executor task, consisting of the
 * {@link Runnable} and a delay plus period. The period needs to be specified;
 * there is no point in a default for it.
 *
 * <p>The {@link java.util.concurrent.ScheduledExecutorService} does not offer
 * more sophisticated scheduling options such as cron expressions.
 * Consider using {@link ThreadPoolTaskScheduler} for such needs.
 *
 * <p>Note that the {@link java.util.concurrent.ScheduledExecutorService} mechanism
 * uses a {@link Runnable} instance that is shared between repeated executions,
 * in contrast to Quartz which creates a new Job instance for each execution.
 *
 * <p>
 *  描述由{@link Runnable}和延迟加期间组成的计划执行者任务的JavaBean需要指定期间;默认情况下没有任何意义
 * 
 * <p> {@link javautilconcurrentScheduledExecutorService}不提供更复杂的计划选项,如cron表达式考虑使用{@link ThreadPoolTask​​Scheduler}
 * 进行此类需求。
 * 
 *  <p>请注意,{@link javautilconcurrentScheduledExecutorService}机制使用重复执行之间共享的{@link Runnable}实例,与Quartz相反,Q
 * uartz会为每个执行创建一个新的Job实例。
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.0
 * @see java.util.concurrent.ScheduledExecutorService#scheduleWithFixedDelay(java.lang.Runnable, long, long, java.util.concurrent.TimeUnit)
 * @see java.util.concurrent.ScheduledExecutorService#scheduleAtFixedRate(java.lang.Runnable, long, long, java.util.concurrent.TimeUnit)
 */
public class ScheduledExecutorTask {

	private Runnable runnable;

	private long delay = 0;

	private long period = -1;

	private TimeUnit timeUnit = TimeUnit.MILLISECONDS;

	private boolean fixedRate = false;


	/**
	 * Create a new ScheduledExecutorTask,
	 * to be populated via bean properties.
	 * <p>
	 *  创建一个新的ScheduledExecutorTask,通过bean属性来填充
	 * 
	 * 
	 * @see #setDelay
	 * @see #setPeriod
	 * @see #setFixedRate
	 */
	public ScheduledExecutorTask() {
	}

	/**
	 * Create a new ScheduledExecutorTask, with default
	 * one-time execution without delay.
	 * <p>
	 *  创建一个新的ScheduledExecutorTask,默认一次执行没有延迟
	 * 
	 * 
	 * @param executorTask the Runnable to schedule
	 */
	public ScheduledExecutorTask(Runnable executorTask) {
		this.runnable = executorTask;
	}

	/**
	 * Create a new ScheduledExecutorTask, with default
	 * one-time execution with the given delay.
	 * <p>
	 *  创建一个新的ScheduledExecutorTask,默认一次执行与给定的延迟
	 * 
	 * 
	 * @param executorTask the Runnable to schedule
	 * @param delay the delay before starting the task for the first time (ms)
	 */
	public ScheduledExecutorTask(Runnable executorTask, long delay) {
		this.runnable = executorTask;
		this.delay = delay;
	}

	/**
	 * Create a new ScheduledExecutorTask.
	 * <p>
	 *  创建一个新的ScheduledExecutorTask
	 * 
	 * 
	 * @param executorTask the Runnable to schedule
	 * @param delay the delay before starting the task for the first time (ms)
	 * @param period the period between repeated task executions (ms)
	 * @param fixedRate whether to schedule as fixed-rate execution
	 */
	public ScheduledExecutorTask(Runnable executorTask, long delay, long period, boolean fixedRate) {
		this.runnable = executorTask;
		this.delay = delay;
		this.period = period;
		this.fixedRate = fixedRate;
	}


	/**
	 * Set the Runnable to schedule as executor task.
	 * <p>
	 *  将Runnable设置为执行者任务
	 * 
	 */
	public void setRunnable(Runnable executorTask) {
		this.runnable = executorTask;
	}

	/**
	 * Return the Runnable to schedule as executor task.
	 * <p>
	 * 将Runnable返回为执行者任务
	 * 
	 */
	public Runnable getRunnable() {
		return this.runnable;
	}

	/**
	 * Set the delay before starting the task for the first time,
	 * in milliseconds. Default is 0, immediately starting the
	 * task after successful scheduling.
	 * <p>
	 *  首次启动任务之前设置延迟,以毫秒为单位默认为0,成功安排后立即启动任务
	 * 
	 */
	public void setDelay(long delay) {
		this.delay = delay;
	}

	/**
	 * Return the delay before starting the job for the first time.
	 * <p>
	 *  在开始工作之前首次返回延迟
	 * 
	 */
	public long getDelay() {
		return this.delay;
	}

	/**
	 * Set the period between repeated task executions, in milliseconds.
	 * <p>Default is -1, leading to one-time execution. In case of a positive value,
	 * the task will be executed repeatedly, with the given interval inbetween executions.
	 * <p>Note that the semantics of the period value vary between fixed-rate and
	 * fixed-delay execution.
	 * <p><b>Note:</b> A period of 0 (for example as fixed delay) is <i>not</i> supported,
	 * simply because {@code java.util.concurrent.ScheduledExecutorService} itself
	 * does not support it. Hence a value of 0 will be treated as one-time execution;
	 * however, that value should never be specified explicitly in the first place!
	 * <p>
	 * 设置重复任务执行之间的时间间隔,以毫秒为单位<p>默认值为-1,导致一次执行如果为正值,则任务将重复执行,执行之间的给定间隔<p>请注意,周期值的语义在固定速率和固定延迟执行之间变化<p> <b>注意：
	 * </b>支持的周期为0(例如固定延迟)为<i>不</i>因为{@code javautilconcurrentScheduledExecutorService}本身不支持它,因此值为0将被视为一次执行;
	 * 但是,首先应该绝对不会明确指定该值！。
	 * 
	 * 
	 * @see #setFixedRate
	 * @see #isOneTimeTask()
	 * @see java.util.concurrent.ScheduledExecutorService#scheduleWithFixedDelay(Runnable, long, long, java.util.concurrent.TimeUnit)
	 */
	public void setPeriod(long period) {
		this.period = period;
	}

	/**
	 * Return the period between repeated task executions.
	 * <p>
	 *  重复执行任务之间的时间间隔
	 * 
	 */
	public long getPeriod() {
		return this.period;
	}

	/**
	 * Is this task only ever going to execute once?
	 * <p>
	 *  这个任务是否只会执行一次?
	 * 
	 * 
	 * @return {@code true} if this task is only ever going to execute once
	 * @see #getPeriod()
	 */
	public boolean isOneTimeTask() {
		return (this.period <= 0);
	}

	/**
	 * Specify the time unit for the delay and period values.
	 * Default is milliseconds ({@code TimeUnit.MILLISECONDS}).
	 * <p>
	 * 指定延迟和周期值的时间单位默认值为毫秒({@code TimeUnitMILLISECONDS})
	 * 
	 * 
	 * @see java.util.concurrent.TimeUnit#MILLISECONDS
	 * @see java.util.concurrent.TimeUnit#SECONDS
	 */
	public void setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = (timeUnit != null ? timeUnit : TimeUnit.MILLISECONDS);
	}

	/**
	 * Return the time unit for the delay and period values.
	 * <p>
	 *  返回延迟和周期值的时间单位
	 * 
	 */
	public TimeUnit getTimeUnit() {
		return this.timeUnit;
	}

	/**
	 * Set whether to schedule as fixed-rate execution, rather than
	 * fixed-delay execution. Default is "false", that is, fixed delay.
	 * <p>See ScheduledExecutorService javadoc for details on those execution modes.
	 * <p>
	 *  设置是否计划为固定速率执行,而不是固定延迟执行默认为"false",即固定延迟<p>有关这些执行模式的详细信息,请参阅ScheduledExecutorService javadoc
	 * 
	 * 
	 * @see java.util.concurrent.ScheduledExecutorService#scheduleWithFixedDelay(java.lang.Runnable, long, long, java.util.concurrent.TimeUnit)
	 * @see java.util.concurrent.ScheduledExecutorService#scheduleAtFixedRate(java.lang.Runnable, long, long, java.util.concurrent.TimeUnit)
	 */
	public void setFixedRate(boolean fixedRate) {
		this.fixedRate = fixedRate;
	}

	/**
	 * Return whether to schedule as fixed-rate execution.
	 * <p>
	 *  返回是否以固定速率执行计划
	 */
	public boolean isFixedRate() {
		return this.fixedRate;
	}

}
