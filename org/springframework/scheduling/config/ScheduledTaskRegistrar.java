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

package org.springframework.scheduling.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * Helper bean for registering tasks with a {@link TaskScheduler}, typically using cron
 * expressions.
 *
 * <p>As of Spring 3.1, {@code ScheduledTaskRegistrar} has a more prominent user-facing
 * role when used in conjunction with the @{@link
 * org.springframework.scheduling.annotation.EnableAsync EnableAsync} annotation and its
 * {@link org.springframework.scheduling.annotation.SchedulingConfigurer
 * SchedulingConfigurer} callback interface.
 *
 * <p>
 *  用于使用{@link TaskScheduler}注册任务的助手Bean,通常使用cron表达式
 * 
 * <p>截至Spring 31,当与@ {@ link orgspringframeworkschedulingannotationEnableAsync EnableAsync}注释及其{@link orgspringframeworkschedulingannotationSchedulingConfigurer SchedulingConfigurer}
 * 回调接口结合使用时,{@code ScheduledTaskRegistrar}具有更突出的面向用户角色。
 * 
 * 
 * @author Juergen Hoeller
 * @author Chris Beams
 * @author Tobias Montagna-Hay
 * @since 3.0
 * @see org.springframework.scheduling.annotation.EnableAsync
 * @see org.springframework.scheduling.annotation.SchedulingConfigurer
 */
public class ScheduledTaskRegistrar implements InitializingBean, DisposableBean {

	private TaskScheduler taskScheduler;

	private ScheduledExecutorService localExecutor;

	private List<TriggerTask> triggerTasks;

	private List<CronTask> cronTasks;

	private List<IntervalTask> fixedRateTasks;

	private List<IntervalTask> fixedDelayTasks;

	private final Map<Task, ScheduledTask> unresolvedTasks = new HashMap<Task, ScheduledTask>(16);

	private final Set<ScheduledTask> scheduledTasks = new LinkedHashSet<ScheduledTask>(16);


	/**
	 * Set the {@link TaskScheduler} to register scheduled tasks with.
	 * <p>
	 *  设置{@link TaskScheduler}以注册计划的任务
	 * 
	 */
	public void setTaskScheduler(TaskScheduler taskScheduler) {
		Assert.notNull(taskScheduler, "TaskScheduler must not be null");
		this.taskScheduler = taskScheduler;
	}

	/**
	 * Set the {@link TaskScheduler} to register scheduled tasks with, or a
	 * {@link java.util.concurrent.ScheduledExecutorService} to be wrapped as a
	 * {@code TaskScheduler}.
	 * <p>
	 *  设置{@link TaskScheduler}以使用或{@link javautilconcurrentScheduledExecutorService}注册计划任务,以将其作为{@code TaskScheduler}
	 * 。
	 * 
	 */
	public void setScheduler(Object scheduler) {
		Assert.notNull(scheduler, "Scheduler object must not be null");
		if (scheduler instanceof TaskScheduler) {
			this.taskScheduler = (TaskScheduler) scheduler;
		}
		else if (scheduler instanceof ScheduledExecutorService) {
			this.taskScheduler = new ConcurrentTaskScheduler(((ScheduledExecutorService) scheduler));
		}
		else {
			throw new IllegalArgumentException("Unsupported scheduler type: " + scheduler.getClass());
		}
	}

	/**
	 * Return the {@link TaskScheduler} instance for this registrar (may be {@code null}).
	 * <p>
	 *  返回此注册商的{@link TaskScheduler}实例(可能是{@code null})
	 * 
	 */
	public TaskScheduler getScheduler() {
		return this.taskScheduler;
	}


	/**
	 * Specify triggered tasks as a Map of Runnables (the tasks) and Trigger objects
	 * (typically custom implementations of the {@link Trigger} interface).
	 * <p>
	 *  将触发的任务指定为运行映射(任务)和触发器对象(通常是{@link Trigger}接口的自定义实现)
	 * 
	 */
	public void setTriggerTasks(Map<Runnable, Trigger> triggerTasks) {
		this.triggerTasks = new ArrayList<TriggerTask>();
		for (Map.Entry<Runnable, Trigger> task : triggerTasks.entrySet()) {
			addTriggerTask(new TriggerTask(task.getKey(), task.getValue()));
		}
	}

	/**
	 * Specify triggered tasks as a list of {@link TriggerTask} objects. Primarily used
	 * by {@code <task:*>} namespace parsing.
	 * <p>
	 * 将触发的任务指定为{@link TriggerTask}对象的列表{@code <task：*>}主要用于命名空间解析
	 * 
	 * 
	 * @since 3.2
	 * @see ScheduledTasksBeanDefinitionParser
	 */
	public void setTriggerTasksList(List<TriggerTask> triggerTasks) {
		this.triggerTasks = triggerTasks;
	}

	/**
	 * Get the trigger tasks as an unmodifiable list of {@link TriggerTask} objects.
	 * <p>
	 *  将触发器任务作为不可修改的{@link TriggerTask}对象列表
	 * 
	 * 
	 * @return the list of tasks (never {@code null})
	 * @since 4.2
	 */
	public List<TriggerTask> getTriggerTaskList() {
		return (this.triggerTasks != null? Collections.unmodifiableList(this.triggerTasks) :
				Collections.<TriggerTask>emptyList());
	}

	/**
	 * Specify triggered tasks as a Map of Runnables (the tasks) and cron expressions.
	 * <p>
	 *  将触发的任务指定为运行图(任务)和cron表达式
	 * 
	 * 
	 * @see CronTrigger
	 */
	public void setCronTasks(Map<Runnable, String> cronTasks) {
		this.cronTasks = new ArrayList<CronTask>();
		for (Map.Entry<Runnable, String> task : cronTasks.entrySet()) {
			addCronTask(task.getKey(), task.getValue());
		}
	}

	/**
	 * Specify triggered tasks as a list of {@link CronTask} objects. Primarily used by
	 * {@code <task:*>} namespace parsing.
	 * <p>
	 *  将触发的任务指定为{@link CronTask}对象的列表{@code <task：*>}主要用于命名空间解析
	 * 
	 * 
	 * @since 3.2
	 * @see ScheduledTasksBeanDefinitionParser
	 */
	public void setCronTasksList(List<CronTask> cronTasks) {
		this.cronTasks = cronTasks;
	}

	/**
	 * Get the cron tasks as an unmodifiable list of {@link CronTask} objects.
	 * <p>
	 *  将cron任务作为不可修改的{@link CronTask}对象列表
	 * 
	 * 
	 * @return the list of tasks (never {@code null})
	 * @since 4.2
	 */
	public List<CronTask> getCronTaskList() {
		return (this.cronTasks != null ? Collections.unmodifiableList(this.cronTasks) :
				Collections.<CronTask>emptyList());
	}

	/**
	 * Specify triggered tasks as a Map of Runnables (the tasks) and fixed-rate values.
	 * <p>
	 *  将触发的任务指定为运行图(任务)和固定速率值
	 * 
	 * 
	 * @see TaskScheduler#scheduleAtFixedRate(Runnable, long)
	 */
	public void setFixedRateTasks(Map<Runnable, Long> fixedRateTasks) {
		this.fixedRateTasks = new ArrayList<IntervalTask>();
		for (Map.Entry<Runnable, Long> task : fixedRateTasks.entrySet()) {
			addFixedRateTask(task.getKey(), task.getValue());
		}
	}

	/**
	 * Specify fixed-rate tasks as a list of {@link IntervalTask} objects. Primarily used
	 * by {@code <task:*>} namespace parsing.
	 * <p>
	 *  将固定速率任务指定为{@link IntervalTask​​}对象的列表{@code <task：*>}命名空间解析主要使用的对象
	 * 
	 * 
	 * @since 3.2
	 * @see ScheduledTasksBeanDefinitionParser
	 */
	public void setFixedRateTasksList(List<IntervalTask> fixedRateTasks) {
		this.fixedRateTasks = fixedRateTasks;
	}

	/**
	 * Get the fixed-rate tasks as an unmodifiable list of {@link IntervalTask} objects.
	 * <p>
	 *  将固定速率任务作为不可修改的{@link IntervalTask​​}对象列表
	 * 
	 * 
	 * @return the list of tasks (never {@code null})
	 * @since 4.2
	 */
	public List<IntervalTask> getFixedRateTaskList() {
		return (this.fixedRateTasks != null ? Collections.unmodifiableList(this.fixedRateTasks) :
				Collections.<IntervalTask>emptyList());
	}

	/**
	 * Specify triggered tasks as a Map of Runnables (the tasks) and fixed-delay values.
	 * <p>
	 * 将触发的任务指定为运行图(任务)和固定延迟值
	 * 
	 * 
	 * @see TaskScheduler#scheduleWithFixedDelay(Runnable, long)
	 */
	public void setFixedDelayTasks(Map<Runnable, Long> fixedDelayTasks) {
		this.fixedDelayTasks = new ArrayList<IntervalTask>();
		for (Map.Entry<Runnable, Long> task : fixedDelayTasks.entrySet()) {
			addFixedDelayTask(task.getKey(), task.getValue());
		}
	}

	/**
	 * Specify fixed-delay tasks as a list of {@link IntervalTask} objects. Primarily used
	 * by {@code <task:*>} namespace parsing.
	 * <p>
	 *  将固定延迟任务指定为{@link IntervalTask​​}对象的列表{@code <task：*>}命名空间解析主要使用
	 * 
	 * 
	 * @since 3.2
	 * @see ScheduledTasksBeanDefinitionParser
	 */
	public void setFixedDelayTasksList(List<IntervalTask> fixedDelayTasks) {
		this.fixedDelayTasks = fixedDelayTasks;
	}

	/**
	 * Get the fixed-delay tasks as an unmodifiable list of {@link IntervalTask} objects.
	 * <p>
	 *  将固定延迟任务作为{@link IntervalTask​​}对象的不可修改列表
	 * 
	 * 
	 * @return the list of tasks (never {@code null})
	 * @since 4.2
	 */
	public List<IntervalTask> getFixedDelayTaskList() {
		return (this.fixedDelayTasks != null ? Collections.unmodifiableList(this.fixedDelayTasks) :
				Collections.<IntervalTask>emptyList());
	}


	/**
	 * Add a Runnable task to be triggered per the given {@link Trigger}.
	 * <p>
	 *  添加一个Runnable任务,按照给定的{@link Trigger}触发
	 * 
	 * 
	 * @see TaskScheduler#scheduleAtFixedRate(Runnable, long)
	 */
	public void addTriggerTask(Runnable task, Trigger trigger) {
		addTriggerTask(new TriggerTask(task, trigger));
	}

	/**
	 * Add a {@code TriggerTask}.
	 * <p>
	 *  添加{@code TriggerTask}
	 * 
	 * 
	 * @since 3.2
	 * @see TaskScheduler#scheduleAtFixedRate(Runnable, long)
	 */
	public void addTriggerTask(TriggerTask task) {
		if (this.triggerTasks == null) {
			this.triggerTasks = new ArrayList<TriggerTask>();
		}
		this.triggerTasks.add(task);
	}

	/**
	 * Add a Runnable task to be triggered per the given cron expression
	 * <p>
	 *  添加一个可以根据给定的cron表达式触发的Runnable任务
	 * 
	 */
	public void addCronTask(Runnable task, String expression) {
		addCronTask(new CronTask(task, expression));
	}

	/**
	 * Add a {@link CronTask}.
	 * <p>
	 *  添加{@link CronTask}
	 * 
	 * 
	 * @since 3.2
	 */
	public void addCronTask(CronTask task) {
		if (this.cronTasks == null) {
			this.cronTasks = new ArrayList<CronTask>();
		}
		this.cronTasks.add(task);
	}

	/**
	 * Add a {@code Runnable} task to be triggered at the given fixed-rate interval.
	 * <p>
	 *  添加要以给定固定速率间隔触发的{@code Runnable}任务
	 * 
	 * 
	 * @see TaskScheduler#scheduleAtFixedRate(Runnable, long)
	 */
	public void addFixedRateTask(Runnable task, long interval) {
		addFixedRateTask(new IntervalTask(task, interval, 0));
	}

	/**
	 * Add a fixed-rate {@link IntervalTask}.
	 * <p>
	 *  添加固定费率{@link IntervalTask​​}
	 * 
	 * 
	 * @since 3.2
	 * @see TaskScheduler#scheduleAtFixedRate(Runnable, long)
	 */
	public void addFixedRateTask(IntervalTask task) {
		if (this.fixedRateTasks == null) {
			this.fixedRateTasks = new ArrayList<IntervalTask>();
		}
		this.fixedRateTasks.add(task);
	}

	/**
	 * Add a Runnable task to be triggered with the given fixed delay.
	 * <p>
	 *  添加一个Runnable任务,以给定的固定延迟触发
	 * 
	 * 
	 * @see TaskScheduler#scheduleWithFixedDelay(Runnable, long)
	 */
	public void addFixedDelayTask(Runnable task, long delay) {
		addFixedDelayTask(new IntervalTask(task, delay, 0));
	}

	/**
	 * Add a fixed-delay {@link IntervalTask}.
	 * <p>
	 *  添加固定延时{@link IntervalTask​​}
	 * 
	 * 
	 * @since 3.2
	 * @see TaskScheduler#scheduleWithFixedDelay(Runnable, long)
	 */
	public void addFixedDelayTask(IntervalTask task) {
		if (this.fixedDelayTasks == null) {
			this.fixedDelayTasks = new ArrayList<IntervalTask>();
		}
		this.fixedDelayTasks.add(task);
	}


	/**
	 * Return whether this {@code ScheduledTaskRegistrar} has any tasks registered.
	 * <p>
	 *  返回这个{@code ScheduledTaskRegistrar}是否有任何注册的任务
	 * 
	 * 
	 * @since 3.2
	 */
	public boolean hasTasks() {
		return (!CollectionUtils.isEmpty(this.triggerTasks) ||
				!CollectionUtils.isEmpty(this.cronTasks) ||
				!CollectionUtils.isEmpty(this.fixedRateTasks) ||
				!CollectionUtils.isEmpty(this.fixedDelayTasks));
	}


	/**
	 * Calls {@link #scheduleTasks()} at bean construction time.
	 * <p>
	 * 在bean构建时调用{@link #scheduleTasks()}
	 * 
	 */
	@Override
	public void afterPropertiesSet() {
		scheduleTasks();
	}

	/**
	 * Schedule all registered tasks against the underlying {@linkplain
	 * #setTaskScheduler(TaskScheduler) task scheduler}.
	 * <p>
	 *  根据底层的{@linkplain #setTaskScheduler(TaskScheduler)任务调度程序)计划所有注册的任务}
	 * 
	 */
	protected void scheduleTasks() {
		if (this.taskScheduler == null) {
			this.localExecutor = Executors.newSingleThreadScheduledExecutor();
			this.taskScheduler = new ConcurrentTaskScheduler(this.localExecutor);
		}
		if (this.triggerTasks != null) {
			for (TriggerTask task : this.triggerTasks) {
				addScheduledTask(scheduleTriggerTask(task));
			}
		}
		if (this.cronTasks != null) {
			for (CronTask task : this.cronTasks) {
				addScheduledTask(scheduleCronTask(task));
			}
		}
		if (this.fixedRateTasks != null) {
			for (IntervalTask task : this.fixedRateTasks) {
				addScheduledTask(scheduleFixedRateTask(task));
			}
		}
		if (this.fixedDelayTasks != null) {
			for (IntervalTask task : this.fixedDelayTasks) {
				addScheduledTask(scheduleFixedDelayTask(task));
			}
		}
	}

	private void addScheduledTask(ScheduledTask task) {
		if (task != null) {
			this.scheduledTasks.add(task);
		}
	}


	/**
	 * Schedule the specified trigger task, either right away if possible
	 * or on initialization of the scheduler.
	 * <p>
	 *  安排指定的触发任务,如果可能,或者在初始化调度程序时,请立即进行
	 * 
	 * 
	 * @return a handle to the scheduled task, allowing to cancel it
	 * @since 4.3
	 */
	public ScheduledTask scheduleTriggerTask(TriggerTask task) {
		ScheduledTask scheduledTask = this.unresolvedTasks.remove(task);
		boolean newTask = false;
		if (scheduledTask == null) {
			scheduledTask = new ScheduledTask();
			newTask = true;
		}
		if (this.taskScheduler != null) {
			scheduledTask.future = this.taskScheduler.schedule(task.getRunnable(), task.getTrigger());
		}
		else {
			addTriggerTask(task);
			this.unresolvedTasks.put(task, scheduledTask);
		}
		return (newTask ? scheduledTask : null);
	}

	/**
	 * Schedule the specified cron task, either right away if possible
	 * or on initialization of the scheduler.
	 * <p>
	 *  安排指定的cron任务,如果可能,或者在初始化调度程序时,请立即执行
	 * 
	 * 
	 * @return a handle to the scheduled task, allowing to cancel it
	 * (or {@code null} if processing a previously registered task)
	 * @since 4.3
	 */
	public ScheduledTask scheduleCronTask(CronTask task) {
		ScheduledTask scheduledTask = this.unresolvedTasks.remove(task);
		boolean newTask = false;
		if (scheduledTask == null) {
			scheduledTask = new ScheduledTask();
			newTask = true;
		}
		if (this.taskScheduler != null) {
			scheduledTask.future = this.taskScheduler.schedule(task.getRunnable(), task.getTrigger());
		}
		else {
			addCronTask(task);
			this.unresolvedTasks.put(task, scheduledTask);
		}
		return (newTask ? scheduledTask : null);
	}

	/**
	 * Schedule the specified fixed-rate task, either right away if possible
	 * or on initialization of the scheduler.
	 * <p>
	 *  如果可能,或者在调度程序的初始化时,请立即安排指定的固定速率任务
	 * 
	 * 
	 * @return a handle to the scheduled task, allowing to cancel it
	 * (or {@code null} if processing a previously registered task)
	 * @since 4.3
	 */
	public ScheduledTask scheduleFixedRateTask(IntervalTask task) {
		ScheduledTask scheduledTask = this.unresolvedTasks.remove(task);
		boolean newTask = false;
		if (scheduledTask == null) {
			scheduledTask = new ScheduledTask();
			newTask = true;
		}
		if (this.taskScheduler != null) {
			if (task.getInitialDelay() > 0) {
				Date startTime = new Date(System.currentTimeMillis() + task.getInitialDelay());
				scheduledTask.future =
						this.taskScheduler.scheduleAtFixedRate(task.getRunnable(), startTime, task.getInterval());
			}
			else {
				scheduledTask.future =
						this.taskScheduler.scheduleAtFixedRate(task.getRunnable(), task.getInterval());
			}
		}
		else {
			addFixedRateTask(task);
			this.unresolvedTasks.put(task, scheduledTask);
		}
		return (newTask ? scheduledTask : null);
	}

	/**
	 * Schedule the specified fixed-delay task, either right away if possible
	 * or on initialization of the scheduler.
	 * <p>
	 *  如果可能,或者在调度程序的初始化时,请立即安排指定的固定延迟任务
	 * 
	 * @return a handle to the scheduled task, allowing to cancel it
	 * (or {@code null} if processing a previously registered task)
	 * @since 4.3
	 */
	public ScheduledTask scheduleFixedDelayTask(IntervalTask task) {
		ScheduledTask scheduledTask = this.unresolvedTasks.remove(task);
		boolean newTask = false;
		if (scheduledTask == null) {
			scheduledTask = new ScheduledTask();
			newTask = true;
		}
		if (this.taskScheduler != null) {
			if (task.getInitialDelay() > 0) {
				Date startTime = new Date(System.currentTimeMillis() + task.getInitialDelay());
				scheduledTask.future =
						this.taskScheduler.scheduleWithFixedDelay(task.getRunnable(), startTime, task.getInterval());
			}
			else {
				scheduledTask.future =
						this.taskScheduler.scheduleWithFixedDelay(task.getRunnable(), task.getInterval());
			}
		}
		else {
			addFixedDelayTask(task);
			this.unresolvedTasks.put(task, scheduledTask);
		}
		return (newTask ? scheduledTask : null);
	}


	@Override
	public void destroy() {
		for (ScheduledTask task : this.scheduledTasks) {
			task.cancel();
		}
		if (this.localExecutor != null) {
			this.localExecutor.shutdownNow();
		}
	}

}
