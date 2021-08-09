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

package org.springframework.scheduling.support;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.util.Assert;

/**
 * A trigger for periodic task execution. The period may be applied as either
 * fixed-rate or fixed-delay, and an initial delay value may also be configured.
 * The default initial delay is 0, and the default behavior is fixed-delay
 * (i.e. the interval between successive executions is measured from each
 * <emphasis>completion</emphasis> time). To measure the interval between the
 * scheduled <emphasis>start</emphasis> time of each execution instead, set the
 * 'fixedRate' property to {@code true}.
 *
 * <p>Note that the TaskScheduler interface already defines methods for scheduling
 * tasks at fixed-rate or with fixed-delay. Both also support an optional value
 * for the initial delay. Those methods should be used directly whenever
 * possible. The value of this Trigger implementation is that it can be used
 * within components that rely on the Trigger abstraction. For example, it may
 * be convenient to allow periodic triggers, cron-based triggers, and even
 * custom Trigger implementations to be used interchangeably.
 *
 * <p>
 * 用于周期性任务执行的触发周期可以应用为固定速率或固定延迟,并且也可以配置初始延迟值默认初始延迟为0,默认行为为固定延迟(即,连续执行是从每个<emphasis>完成</emphasis>时间测量)为了测
 * 量每个执行的调度的<emphasis> start </emphasis>时间间隔,请将'fixedRate'属性设置为{@code true}。
 * 
 * 注意,TaskScheduler接口已经定义了以固定速率或固定延迟调度任务的方法。两者也支持初始延迟的可选值这些方法应该尽可能直接使用。此Trigger实现的值是它可以在依赖于触发器抽象的组件中使用。
 * 例如,允许周期性触发器,基于cron的触发器,甚至自定义触发器实现可以互换使用可能很方便。
 * 
 * 
 * @author Mark Fisher
 * @since 3.0
 */
public class PeriodicTrigger implements Trigger {

	private final long period;

	private final TimeUnit timeUnit;

	private volatile long initialDelay = 0;

	private volatile boolean fixedRate = false;


	/**
	 * Create a trigger with the given period in milliseconds.
	 * <p>
	 *  以毫秒为单位创建给定周期的触发器
	 * 
	 */
	public PeriodicTrigger(long period) {
		this(period, null);
	}

	/**
	 * Create a trigger with the given period and time unit. The time unit will
	 * apply not only to the period but also to any 'initialDelay' value, if
	 * configured on this Trigger later via {@link #setInitialDelay(long)}.
	 * <p>
	 *  创建具有给定周期和时间单位的触发器时间单位不仅将应用于期间,而且还将应用于任何'initialDelay'值,如果此触发器稍后通过{@link #setInitialDelay(long)}配置)
	 * 
	 */
	public PeriodicTrigger(long period, TimeUnit timeUnit) {
		Assert.isTrue(period >= 0, "period must not be negative");
		this.timeUnit = (timeUnit != null ? timeUnit : TimeUnit.MILLISECONDS);
		this.period = this.timeUnit.toMillis(period);
	}


	/**
	 * Specify the delay for the initial execution. It will be evaluated in
	 * terms of this trigger's {@link TimeUnit}. If no time unit was explicitly
	 * provided upon instantiation, the default is milliseconds.
	 * <p>
	 * 指定初始执行的延迟将根据此触发器的{@link TimeUnit}进行评估。如果在实例化时未明确提供时间单位,则默认值为毫秒
	 * 
	 */
	public void setInitialDelay(long initialDelay) {
		this.initialDelay = this.timeUnit.toMillis(initialDelay);
	}

	/**
	 * Specify whether the periodic interval should be measured between the
	 * scheduled start times rather than between actual completion times.
	 * The latter, "fixed delay" behavior, is the default.
	 * <p>
	 *  指定是否应在计划的开始时间之间而不是实际完成时间之间测量周期性间隔。后者,"固定延迟"行为是默认值
	 * 
	 */
	public void setFixedRate(boolean fixedRate) {
		this.fixedRate = fixedRate;
	}


	/**
	 * Returns the time after which a task should run again.
	 * <p>
	 *  返回任务再次运行的时间
	 */
	@Override
	public Date nextExecutionTime(TriggerContext triggerContext) {
		if (triggerContext.lastScheduledExecutionTime() == null) {
			return new Date(System.currentTimeMillis() + this.initialDelay);
		}
		else if (this.fixedRate) {
			return new Date(triggerContext.lastScheduledExecutionTime().getTime() + this.period);
		}
		return new Date(triggerContext.lastCompletionTime().getTime() + this.period);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof PeriodicTrigger)) {
			return false;
		}
		PeriodicTrigger other = (PeriodicTrigger) obj;
		return (this.fixedRate == other.fixedRate && this.initialDelay == other.initialDelay && this.period == other.period);
	}

	@Override
	public int hashCode() {
		return (this.fixedRate ? 17 : 29) + (int) (37 * this.period) + (int) (41 * this.initialDelay);
	}

}
