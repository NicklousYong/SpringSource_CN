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

package org.springframework.scheduling.quartz;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.quartz.SchedulerConfigException;
import org.quartz.simpl.SimpleThreadPool;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.scheduling.SchedulingException;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.util.Assert;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureTask;

/**
 * Subclass of Quartz's SimpleThreadPool that implements Spring's
 * {@link org.springframework.core.task.TaskExecutor} interface
 * and listens to Spring lifecycle callbacks.
 *
 * <p>Can be shared between a Quartz Scheduler (specified as "taskExecutor")
 * and other TaskExecutor users, or even used completely independent of
 * a Quartz Scheduler (as plain TaskExecutor backend).
 *
 * <p>
 *  Quartz的SimpleThreadPool子类实现了Spring的{@link orgspringframeworkcoretaskTaskExecutor}接口并且侦听Spring生命周期回调。
 * 
 * <p>可以在Quartz Scheduler(指定为"taskExecutor")和其他TaskExecutor用户之间共享,甚至可以完全独立于Quartz Scheduler(作为简单的TaskExe
 * cutor后端)。
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.0
 * @see org.quartz.simpl.SimpleThreadPool
 * @see org.springframework.core.task.TaskExecutor
 * @see SchedulerFactoryBean#setTaskExecutor
 */
public class SimpleThreadPoolTaskExecutor extends SimpleThreadPool
		implements AsyncListenableTaskExecutor, SchedulingTaskExecutor, InitializingBean, DisposableBean {

	private boolean waitForJobsToCompleteOnShutdown = false;


	/**
	 * Set whether to wait for running jobs to complete on shutdown.
	 * Default is "false".
	 * <p>
	 *  设置是否等待运行作业在关机时完成默认为"false"
	 * 
	 * 
	 * @see org.quartz.simpl.SimpleThreadPool#shutdown(boolean)
	 */
	public void setWaitForJobsToCompleteOnShutdown(boolean waitForJobsToCompleteOnShutdown) {
		this.waitForJobsToCompleteOnShutdown = waitForJobsToCompleteOnShutdown;
	}

	@Override
	public void afterPropertiesSet() throws SchedulerConfigException {
		initialize();
	}


	@Override
	public void execute(Runnable task) {
		Assert.notNull(task, "Runnable must not be null");
		if (!runInThread(task)) {
			throw new SchedulingException("Quartz SimpleThreadPool already shut down");
		}
	}

	@Override
	public void execute(Runnable task, long startTimeout) {
		execute(task);
	}

	@Override
	public Future<?> submit(Runnable task) {
		FutureTask<Object> future = new FutureTask<Object>(task, null);
		execute(future);
		return future;
	}

	@Override
	public <T> Future<T> submit(Callable<T> task) {
		FutureTask<T> future = new FutureTask<T>(task);
		execute(future);
		return future;
	}

	@Override
	public ListenableFuture<?> submitListenable(Runnable task) {
		ListenableFutureTask<Object> future = new ListenableFutureTask<Object>(task, null);
		execute(future);
		return future;
	}

	@Override
	public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
		ListenableFutureTask<T> future = new ListenableFutureTask<T>(task);
		execute(future);
		return future;
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


	@Override
	public void destroy() {
		shutdown(this.waitForJobsToCompleteOnShutdown);
	}

}
