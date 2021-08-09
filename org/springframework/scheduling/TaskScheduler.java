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

package org.springframework.scheduling;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;

/**
 * Task scheduler interface that abstracts the scheduling of
 * {@link Runnable Runnables} based on different kinds of triggers.
 *
 * <p>This interface is separate from {@link SchedulingTaskExecutor} since it
 * usually represents for a different kind of backend, i.e. a thread pool with
 * different characteristics and capabilities. Implementations may implement
 * both interfaces if they can handle both kinds of execution characteristics.
 *
 * <p>The 'default' implementation is
 * {@link org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler},
 * wrapping a native {@link java.util.concurrent.ScheduledExecutorService}
 * and adding extended trigger capabilities.
 *
 * <p>This interface is roughly equivalent to a JSR-236
 * {@code ManagedScheduledExecutorService} as supported in Java EE 6
 * environments. However, at the time of the Spring 3.0 release, the
 * JSR-236 interfaces have not been released in official form yet.
 *
 * <p>
 *  任务调度器接口,根据不同类型的触发器抽象出{@link Runnable Runnables}的调度
 * 
 * <p>此接口与{@link SchedulingTaskExecutor}分开,因为它通常代表不同类型的后端,即具有不同特性和功能的线程池。如果可以处理两种执行特性,则实现两个接口
 * 
 *  <p>"默认"实现是{@link orgspringframeworkschedulingconcurrentThreadPoolTask​​Scheduler},包装本机{@link javautilconcurrentScheduledExecutorService}
 * 并添加扩展触发器功能。
 * 
 *  <p>此接口大致相当于Java EE 6环境中支持的JSR-236 {@code ManagedScheduledExecutorService}但是,在Spring 30发行版时,JSR-236接口
 * 尚未以官方形式发布。
 * 
 * 
 * @author Juergen Hoeller
 * @since 3.0
 * @see org.springframework.core.task.TaskExecutor
 * @see java.util.concurrent.ScheduledExecutorService
 * @see org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
 */
public interface TaskScheduler {

	/**
	 * Schedule the given {@link Runnable}, invoking it whenever the trigger
	 * indicates a next execution time.
	 * <p>Execution will end once the scheduler shuts down or the returned
	 * {@link ScheduledFuture} gets cancelled.
	 * <p>
	 * 安排给定的{@link Runnable},每当触发器指示下一个执行时间时调用它。执行将在安全程序关闭或返回的{@link ScheduledFuture}被取消后结束
	 * 
	 * 
	 * @param task the Runnable to execute whenever the trigger fires
	 * @param trigger an implementation of the {@link Trigger} interface,
	 * e.g. a {@link org.springframework.scheduling.support.CronTrigger} object
	 * wrapping a cron expression
	 * @return a {@link ScheduledFuture} representing pending completion of the task,
	 * or {@code null} if the given Trigger object never fires (i.e. returns
	 * {@code null} from {@link Trigger#nextExecutionTime})
	 * @throws org.springframework.core.task.TaskRejectedException if the given task was not accepted
	 * for internal reasons (e.g. a pool overload handling policy or a pool shutdown in progress)
	 * @see org.springframework.scheduling.support.CronTrigger
	 */
	ScheduledFuture<?> schedule(Runnable task, Trigger trigger);

	/**
	 * Schedule the given {@link Runnable}, invoking it at the specified execution time.
	 * <p>Execution will end once the scheduler shuts down or the returned
	 * {@link ScheduledFuture} gets cancelled.
	 * <p>
	 *  安排给定的{@link Runnable},在指定的执行时间调用它<p>一旦调度程序关闭或返回的{@link ScheduledFuture}被取消,执行将结束
	 * 
	 * 
	 * @param task the Runnable to execute whenever the trigger fires
	 * @param startTime the desired execution time for the task
	 * (if this is in the past, the task will be executed immediately, i.e. as soon as possible)
	 * @return a {@link ScheduledFuture} representing pending completion of the task
	 * @throws org.springframework.core.task.TaskRejectedException if the given task was not accepted
	 * for internal reasons (e.g. a pool overload handling policy or a pool shutdown in progress)
	 */
	ScheduledFuture<?> schedule(Runnable task, Date startTime);

	/**
	 * Schedule the given {@link Runnable}, invoking it at the specified execution time
	 * and subsequently with the given period.
	 * <p>Execution will end once the scheduler shuts down or the returned
	 * {@link ScheduledFuture} gets cancelled.
	 * <p>
	 *  调度给定的{@link Runnable},在指定的执行时间调用它,然后在给定的时间段内执行<p>一旦调度程序关闭或返回的{@link ScheduledFuture}被取消,执行将结束
	 * 
	 * 
	 * @param task the Runnable to execute whenever the trigger fires
	 * @param startTime the desired first execution time for the task
	 * (if this is in the past, the task will be executed immediately, i.e. as soon as possible)
	 * @param period the interval between successive executions of the task (in milliseconds)
	 * @return a {@link ScheduledFuture} representing pending completion of the task
	 * @throws org.springframework.core.task.TaskRejectedException if the given task was not accepted
	 * for internal reasons (e.g. a pool overload handling policy or a pool shutdown in progress)
	 */
	ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Date startTime, long period);

	/**
	 * Schedule the given {@link Runnable}, starting as soon as possible and
	 * invoking it with the given period.
	 * <p>Execution will end once the scheduler shuts down or the returned
	 * {@link ScheduledFuture} gets cancelled.
	 * <p>
	 * 安排给定的{@link Runnable},尽快启动并在给定的时间内调用它<p>一旦调度程序关闭或返回的{@link ScheduledFuture}被取消,执行将结束
	 * 
	 * 
	 * @param task the Runnable to execute whenever the trigger fires
	 * @param period the interval between successive executions of the task (in milliseconds)
	 * @return a {@link ScheduledFuture} representing pending completion of the task
	 * @throws org.springframework.core.task.TaskRejectedException if the given task was not accepted
	 * for internal reasons (e.g. a pool overload handling policy or a pool shutdown in progress)
	 */
	ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long period);

	/**
	 * Schedule the given {@link Runnable}, invoking it at the specified execution time
	 * and subsequently with the given delay between the completion of one execution
	 * and the start of the next.
	 * <p>Execution will end once the scheduler shuts down or the returned
	 * {@link ScheduledFuture} gets cancelled.
	 * <p>
	 *  调度给定的{@link Runnable},在指定的执行时间调用它,然后在完成一个执行和下一个执行的开始之间给定的延迟。
	 * 执行将在安排程序关闭或返回的{@ link ScheduledFuture}被取消。
	 * 
	 * 
	 * @param task the Runnable to execute whenever the trigger fires
	 * @param startTime the desired first execution time for the task
	 * (if this is in the past, the task will be executed immediately, i.e. as soon as possible)
	 * @param delay the delay between the completion of one execution and the start
	 * of the next (in milliseconds)
	 * @return a {@link ScheduledFuture} representing pending completion of the task
	 * @throws org.springframework.core.task.TaskRejectedException if the given task was not accepted
	 * for internal reasons (e.g. a pool overload handling policy or a pool shutdown in progress)
	 */
	ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Date startTime, long delay);

	/**
	 * Schedule the given {@link Runnable}, starting as soon as possible and
	 * invoking it with the given delay between the completion of one execution
	 * and the start of the next.
	 * <p>Execution will end once the scheduler shuts down or the returned
	 * {@link ScheduledFuture} gets cancelled.
	 * <p>
	 * 
	 * @param task the Runnable to execute whenever the trigger fires
	 * @param delay the interval between successive executions of the task (in milliseconds)
	 * @return a {@link ScheduledFuture} representing pending completion of the task
	 * @throws org.springframework.core.task.TaskRejectedException if the given task was not accepted
	 * for internal reasons (e.g. a pool overload handling policy or a pool shutdown in progress)
	 */
	ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long delay);

}
