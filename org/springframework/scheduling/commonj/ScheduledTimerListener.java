/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2012 the original author or authors.
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

package org.springframework.scheduling.commonj;

import commonj.timers.TimerListener;

/**
 * JavaBean that describes a scheduled TimerListener, consisting of
 * the TimerListener itself (or a Runnable to create a TimerListener for)
 * and a delay plus period. Period needs to be specified;
 * there is no point in a default for it.
 *
 * <p>The CommonJ TimerManager does not offer more sophisticated scheduling
 * options such as cron expressions. Consider using Quartz for such
 * advanced needs.
 *
 * <p>Note that the TimerManager uses a TimerListener instance that is
 * shared between repeated executions, in contrast to Quartz which
 * instantiates a new Job for each execution.
 *
 * <p>
 * 描述由TimerListener本身(或创建TimerListener的Runnable)组成的调度TimerListener的JavaBean,需要指定延迟加周期时间段;默认情况下没有任何意义
 * 
 *  CommonJ TimerManager不提供更复杂的调度选项,如cron表达式考虑使用Quartz进行此类高级需求
 * 
 *  <p>请注意,TimerManager使用在重复执行之间共享的TimerListener实例,与Quartz相反,Quartz会为每个执行实例化一个新的作业
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.0
 * @see commonj.timers.TimerListener
 * @see commonj.timers.TimerManager#schedule(commonj.timers.TimerListener, long, long)
 * @see commonj.timers.TimerManager#scheduleAtFixedRate(commonj.timers.TimerListener, long, long)
 */
public class ScheduledTimerListener {

	private TimerListener timerListener;

	private long delay = 0;

	private long period = -1;

	private boolean fixedRate = false;


	/**
	 * Create a new ScheduledTimerListener,
	 * to be populated via bean properties.
	 * <p>
	 *  创建一个新的ScheduledTimerListener,通过bean属性填充
	 * 
	 * 
	 * @see #setTimerListener
	 * @see #setDelay
	 * @see #setPeriod
	 * @see #setFixedRate
	 */
	public ScheduledTimerListener() {
	}

	/**
	 * Create a new ScheduledTimerListener, with default
	 * one-time execution without delay.
	 * <p>
	 *  创建一个新的ScheduledTimerListener,默认一次执行没有延迟
	 * 
	 * 
	 * @param timerListener the TimerListener to schedule
	 */
	public ScheduledTimerListener(TimerListener timerListener) {
		this.timerListener = timerListener;
	}

	/**
	 * Create a new ScheduledTimerListener, with default
	 * one-time execution with the given delay.
	 * <p>
	 * 创建一个新的ScheduledTimerListener,默认一次执行与给定的延迟
	 * 
	 * 
	 * @param timerListener the TimerListener to schedule
	 * @param delay the delay before starting the task for the first time (ms)
	 */
	public ScheduledTimerListener(TimerListener timerListener, long delay) {
		this.timerListener = timerListener;
		this.delay = delay;
	}

	/**
	 * Create a new ScheduledTimerListener.
	 * <p>
	 *  创建一个新的ScheduledTimerListener
	 * 
	 * 
	 * @param timerListener the TimerListener to schedule
	 * @param delay the delay before starting the task for the first time (ms)
	 * @param period the period between repeated task executions (ms)
	 * @param fixedRate whether to schedule as fixed-rate execution
	 */
	public ScheduledTimerListener(TimerListener timerListener, long delay, long period, boolean fixedRate) {
		this.timerListener = timerListener;
		this.delay = delay;
		this.period = period;
		this.fixedRate = fixedRate;
	}

	/**
	 * Create a new ScheduledTimerListener, with default
	 * one-time execution without delay.
	 * <p>
	 *  创建一个新的ScheduledTimerListener,默认一次执行没有延迟
	 * 
	 * 
	 * @param timerTask the Runnable to schedule as TimerListener
	 */
	public ScheduledTimerListener(Runnable timerTask) {
		setRunnable(timerTask);
	}

	/**
	 * Create a new ScheduledTimerListener, with default
	 * one-time execution with the given delay.
	 * <p>
	 *  创建一个新的ScheduledTimerListener,默认一次执行与给定的延迟
	 * 
	 * 
	 * @param timerTask the Runnable to schedule as TimerListener
	 * @param delay the delay before starting the task for the first time (ms)
	 */
	public ScheduledTimerListener(Runnable timerTask, long delay) {
		setRunnable(timerTask);
		this.delay = delay;
	}

	/**
	 * Create a new ScheduledTimerListener.
	 * <p>
	 *  创建一个新的ScheduledTimerListener
	 * 
	 * 
	 * @param timerTask the Runnable to schedule as TimerListener
	 * @param delay the delay before starting the task for the first time (ms)
	 * @param period the period between repeated task executions (ms)
	 * @param fixedRate whether to schedule as fixed-rate execution
	 */
	public ScheduledTimerListener(Runnable timerTask, long delay, long period, boolean fixedRate) {
		setRunnable(timerTask);
		this.delay = delay;
		this.period = period;
		this.fixedRate = fixedRate;
	}


	/**
	 * Set the Runnable to schedule as TimerListener.
	 * <p>
	 *  将Runnable设置为计时器作为TimerListener
	 * 
	 * 
	 * @see DelegatingTimerListener
	 */
	public void setRunnable(Runnable timerTask) {
		this.timerListener = new DelegatingTimerListener(timerTask);
	}

	/**
	 * Set the TimerListener to schedule.
	 * <p>
	 *  设置TimerListener进行调度
	 * 
	 */
	public void setTimerListener(TimerListener timerListener) {
		this.timerListener = timerListener;
	}

	/**
	 * Return the TimerListener to schedule.
	 * <p>
	 *  返回TimerListener进行调度
	 * 
	 */
	public TimerListener getTimerListener() {
		return this.timerListener;
	}

	/**
	 * Set the delay before starting the task for the first time,
	 * in milliseconds. Default is 0, immediately starting the
	 * task after successful scheduling.
	 * <p>If the "firstTime" property is specified, this property will be ignored.
	 * Specify one or the other, not both.
	 * <p>
	 *  在第一次启动任务之前设置延迟(以毫秒为单位)默认值为0,成功调度后立即启动任务<p>如果指定了"firstTime"属性,则此属性将被忽略指定一个或另一个,而不是两者
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
	 * <p>Default is -1, leading to one-time execution. In case of zero or a
	 * positive value, the task will be executed repeatedly, with the given
	 * interval inbetween executions.
	 * <p>Note that the semantics of the period value vary between fixed-rate
	 * and fixed-delay execution.
	 * <p><b>Note:</b> A period of 0 (for example as fixed delay) <i>is</i>
	 * supported, because the CommonJ specification defines this as a legal value.
	 * Hence a value of 0 will result in immediate re-execution after a job has
	 * finished (not in one-time execution like with {@code java.util.Timer}).
	 * <p>
	 * 设置重复任务执行之间的时间间隔(以毫秒为单位)<p>默认值为-1,导致一次执行在零或正值的情况下,任务将重复执行,执行之间的给定间隔<p>注周期值的语义在固定速率和固定延迟执行之间变化<p> <b>注意
	 * ：</b>支持的时间段为0(例如固定延迟)<i>因为CommonJ规范将此定义为合法值因此,值为0将导致作业完成后立即重新执行(不像像{@code javautilTimer}一样执行)。
	 * 
	 * 
	 * @see #setFixedRate
	 * @see #isOneTimeTask()
	 * @see commonj.timers.TimerManager#schedule(commonj.timers.TimerListener, long, long)
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
		return (this.period < 0);
	}

	/**
	 * Set whether to schedule as fixed-rate execution, rather than
	 * fixed-delay execution. Default is "false", i.e. fixed delay.
	 * <p>See TimerManager javadoc for details on those execution modes.
	 * <p>
	 * 设置是否调度为固定速率执行,而不是固定延迟执行默认为"false",即固定延迟<p>有关这些执行模式的详细信息,请参阅TimerManager javadoc
	 * 
	 * 
	 * @see commonj.timers.TimerManager#schedule(commonj.timers.TimerListener, long, long)
	 * @see commonj.timers.TimerManager#scheduleAtFixedRate(commonj.timers.TimerListener, long, long)
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
