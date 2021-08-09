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

package org.springframework.scheduling.config;

/**
 * {@link Task} implementation defining a {@code Runnable} to be executed at a given
 * millisecond interval which may be treated as fixed-rate or fixed-delay depending on
 * context.
 *
 * <p>
 *  {@link Task}实现定义要以给定的毫秒间隔执行的{@code Runnable},这可以根据上下文被视为固定速率或固定延迟
 * 
 * 
 * @author Chris Beams
 * @since 3.2
 * @see org.springframework.scheduling.annotation.Scheduled#fixedRate()
 * @see org.springframework.scheduling.annotation.Scheduled#fixedDelay()
 * @see ScheduledTaskRegistrar#setFixedRateTasksList(java.util.List)
 * @see ScheduledTaskRegistrar#setFixedDelayTasksList(java.util.List)
 * @see org.springframework.scheduling.TaskScheduler
 */
public class IntervalTask extends Task {

	private final long interval;

	private final long initialDelay;


	/**
	 * Create a new {@code IntervalTask}.
	 * <p>
	 *  创建一个新的{@code IntervalTask​​}
	 * 
	 * 
	 * @param runnable the underlying task to execute
	 * @param interval how often in milliseconds the task should be executed
	 * @param initialDelay initial delay before first execution of the task
	 */
	public IntervalTask(Runnable runnable, long interval, long initialDelay) {
		super(runnable);
		this.interval = interval;
		this.initialDelay = initialDelay;
	}

	/**
	 * Create a new {@code IntervalTask} with no initial delay.
	 * <p>
	 * 创建一个没有初始延迟的新的{@code IntervalTask​​}
	 * 
	 * @param runnable the underlying task to execute
	 * @param interval how often in milliseconds the task should be executed
	 */
	public IntervalTask(Runnable runnable, long interval) {
		this(runnable, interval, 0);
	}


	public long getInterval() {
		return this.interval;
	}

	public long getInitialDelay() {
		return this.initialDelay;
	}

}
