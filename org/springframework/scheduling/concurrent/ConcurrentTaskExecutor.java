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

package org.springframework.scheduling.concurrent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.enterprise.concurrent.ManagedExecutors;
import javax.enterprise.concurrent.ManagedTask;

import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.SchedulingAwareRunnable;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.util.ClassUtils;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * Adapter that takes a {@code java.util.concurrent.Executor} and exposes
 * a Spring {@link org.springframework.core.task.TaskExecutor} for it.
 * Also detects an extended {@code java.util.concurrent.ExecutorService}, adapting
 * the {@link org.springframework.core.task.AsyncTaskExecutor} interface accordingly.
 *
 * <p>Autodetects a JSR-236 {@link javax.enterprise.concurrent.ManagedExecutorService}
 * in order to expose {@link javax.enterprise.concurrent.ManagedTask} adapters for it,
 * exposing a long-running hint based on {@link SchedulingAwareRunnable} and an identity
 * name based on the given Runnable/Callable's {@code toString()}. For JSR-236 style
 * lookup in a Java EE 7 environment, consider using {@link DefaultManagedTaskExecutor}.
 *
 * <p>Note that there is a pre-built {@link ThreadPoolTaskExecutor} that allows
 * for defining a {@link java.util.concurrent.ThreadPoolExecutor} in bean style,
 * exposing it as a Spring {@link org.springframework.core.task.TaskExecutor} directly.
 * This is a convenient alternative to a raw ThreadPoolExecutor definition with
 * a separate definition of the present adapter class.
 *
 * <p>
 * 接受{@code javautilconcurrentExecutor}并公开Spring {@link orgspringframeworkcoretaskTaskExecutor}的适配器也会检测到
 * 扩展的{@code javautilconcurrentExecutorService},从而相应地调整{@link orgspringframeworkcoretaskAsyncTaskExecutor}
 * 接口。
 * 
 *  <p>自动检测JSR-236 {@link javaxenterpriseconcurrentManagedExecutorService},以便为其公开{@link javaxenterpriseconcurrentManagedTask}
 * 适配器,根据{@link SchedulingAwareRunnable}显示长时间运行的提示,并根据给定的Runnable / Callable的{@code toString()}对于Java EE
 *  7环境中的JSR-236样式查找,请考虑使用{@link DefaultManagedTaskExecutor}。
 * 
 * 请注意,有一个预先构建的{@link ThreadPoolTask​​Executor},允许在bean样式中定义一个{@link javautilconcurrentThreadPoolExecutor}
 * ,将其作为Spring {@link orgspringframeworkcoretaskTaskExecutor}将其暴露出来,这是一个方便的替代原始ThreadPoolExecutor定义与当前适配
 * 器类的单独定义。
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.0
 * @see java.util.concurrent.Executor
 * @see java.util.concurrent.ExecutorService
 * @see java.util.concurrent.ThreadPoolExecutor
 * @see java.util.concurrent.Executors
 * @see DefaultManagedTaskExecutor
 * @see ThreadPoolTaskExecutor
 */
public class ConcurrentTaskExecutor implements AsyncListenableTaskExecutor, SchedulingTaskExecutor {

	private static Class<?> managedExecutorServiceClass;

	static {
		try {
			managedExecutorServiceClass = ClassUtils.forName(
					"javax.enterprise.concurrent.ManagedExecutorService",
					ConcurrentTaskScheduler.class.getClassLoader());
		}
		catch (ClassNotFoundException ex) {
			// JSR-236 API not available...
			managedExecutorServiceClass = null;
		}
	}

	private Executor concurrentExecutor;

	private TaskExecutorAdapter adaptedExecutor;


	/**
	 * Create a new ConcurrentTaskExecutor, using a single thread executor as default.
	 * <p>
	 *  创建一个新的ConcurrentTaskExecutor,使用单线程执行器作为默认值
	 * 
	 * 
	 * @see java.util.concurrent.Executors#newSingleThreadExecutor()
	 */
	public ConcurrentTaskExecutor() {
		setConcurrentExecutor(null);
	}

	/**
	 * Create a new ConcurrentTaskExecutor, using the given {@link java.util.concurrent.Executor}.
	 * <p>Autodetects a JSR-236 {@link javax.enterprise.concurrent.ManagedExecutorService}
	 * in order to expose {@link javax.enterprise.concurrent.ManagedTask} adapters for it.
	 * <p>
	 *  创建一个新的ConcurrentTaskExecutor,使用给定的{@link javautilconcurrentExecutor} <p>自动检测JSR-236 {@link javaxenterpriseconcurrentManagedExecutorService}
	 * ,以便为其公开{@link javaxenterpriseconcurrentManagedTask}适配器。
	 * 
	 * 
	 * @param concurrentExecutor the {@link java.util.concurrent.Executor} to delegate to
	 */
	public ConcurrentTaskExecutor(Executor concurrentExecutor) {
		setConcurrentExecutor(concurrentExecutor);
	}


	/**
	 * Specify the {@link java.util.concurrent.Executor} to delegate to.
	 * <p>Autodetects a JSR-236 {@link javax.enterprise.concurrent.ManagedExecutorService}
	 * in order to expose {@link javax.enterprise.concurrent.ManagedTask} adapters for it.
	 * <p>
	 * 指定{@link javautilconcurrentExecutor}委托给<p>自动检测JSR-236 {@link javaxenterpriseconcurrentManagedExecutorService}
	 * ,以便为其公开{@link javaxenterpriseconcurrentManagedTask}适配器。
	 * 
	 */
	public final void setConcurrentExecutor(Executor concurrentExecutor) {
		if (concurrentExecutor != null) {
			this.concurrentExecutor = concurrentExecutor;
			if (managedExecutorServiceClass != null && managedExecutorServiceClass.isInstance(concurrentExecutor)) {
				this.adaptedExecutor = new ManagedTaskExecutorAdapter(concurrentExecutor);
			}
			else {
				this.adaptedExecutor = new TaskExecutorAdapter(concurrentExecutor);
			}
		}
		else {
			this.concurrentExecutor = Executors.newSingleThreadExecutor();
			this.adaptedExecutor = new TaskExecutorAdapter(this.concurrentExecutor);
		}
	}

	/**
	 * Return the {@link java.util.concurrent.Executor} that this adapter delegates to.
	 * <p>
	 *  返回此适配器委派的{@link javautilconcurrentExecutor}
	 * 
	 */
	public final Executor getConcurrentExecutor() {
		return this.concurrentExecutor;
	}

	/**
	 * Specify a custom {@link TaskDecorator} to be applied to any {@link Runnable}
	 * about to be executed.
	 * <p>Note that such a decorator is not necessarily being applied to the
	 * user-supplied {@code Runnable}/{@code Callable} but rather to the actual
	 * execution callback (which may be a wrapper around the user-supplied task).
	 * <p>The primary use case is to set some execution context around the task's
	 * invocation, or to provide some monitoring/statistics for task execution.
	 * <p>
	 *  指定要应用于任何要执行的{@link Runnable}的自定义{@link TaskDecorator} <p>请注意,这样的装饰器不一定适用于用户提供的{@code Runnable} / {@ code Callable }
	 * 而是实际的执行回调(可能是用户提供的任务周围的包装)<p>主要用例是在任务的调用周围设置一些执行上下文,或为任务执行提供一些监视/统计信息。
	 * 
	 * 
	 * @since 4.3
	 */
	public final void setTaskDecorator(TaskDecorator taskDecorator) {
		this.adaptedExecutor.setTaskDecorator(taskDecorator);
	}


	@Override
	public void execute(Runnable task) {
		this.adaptedExecutor.execute(task);
	}

	@Override
	public void execute(Runnable task, long startTimeout) {
		this.adaptedExecutor.execute(task, startTimeout);
	}

	@Override
	public Future<?> submit(Runnable task) {
		return this.adaptedExecutor.submit(task);
	}

	@Override
	public <T> Future<T> submit(Callable<T> task) {
		return this.adaptedExecutor.submit(task);
	}

	@Override
	public ListenableFuture<?> submitListenable(Runnable task) {
		return this.adaptedExecutor.submitListenable(task);
	}

	@Override
	public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
		return this.adaptedExecutor.submitListenable(task);
	}

	/**
	 * This task executor prefers short-lived work units.
	 * <p>
	 * 这个任务执行者喜欢短命的工作单位
	 * 
	 */
	@Override
	public boolean prefersShortLivedTasks() {
		return true;
	}


	/**
	 * TaskExecutorAdapter subclass that wraps all provided Runnables and Callables
	 * with a JSR-236 ManagedTask, exposing a long-running hint based on
	 * {@link SchedulingAwareRunnable} and an identity name based on the task's
	 * {@code toString()} representation.
	 * <p>
	 *  TaskExecutorAdapter子类使用JSR-236 ManagedTask包装所有提供的Runnables和Callables,根据{@link SchedulingAwareRunnable}
	 * 显示长时间运行的提示,并根据任务的{@code toString()}表示形式显示一个身份名称。
	 * 
	 */
	private static class ManagedTaskExecutorAdapter extends TaskExecutorAdapter {

		public ManagedTaskExecutorAdapter(Executor concurrentExecutor) {
			super(concurrentExecutor);
		}

		@Override
		public void execute(Runnable task) {
			super.execute(ManagedTaskBuilder.buildManagedTask(task, task.toString()));
		}

		@Override
		public Future<?> submit(Runnable task) {
			return super.submit(ManagedTaskBuilder.buildManagedTask(task, task.toString()));
		}

		@Override
		public <T> Future<T> submit(Callable<T> task) {
			return super.submit(ManagedTaskBuilder.buildManagedTask(task, task.toString()));
		}

		@Override
		public ListenableFuture<?> submitListenable(Runnable task) {
			return super.submitListenable(ManagedTaskBuilder.buildManagedTask(task, task.toString()));
		}

		@Override
		public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
			return super.submitListenable(ManagedTaskBuilder.buildManagedTask(task, task.toString()));
		}
	}


	/**
	 * Delegate that wraps a given Runnable/Callable  with a JSR-236 ManagedTask,
	 * exposing a long-running hint based on {@link SchedulingAwareRunnable}
	 * and a given identity name.
	 * <p>
	 */
	protected static class ManagedTaskBuilder {

		public static Runnable buildManagedTask(Runnable task, String identityName) {
			Map<String, String> properties = new HashMap<String, String>(2);
			if (task instanceof SchedulingAwareRunnable) {
				properties.put(ManagedTask.LONGRUNNING_HINT,
						Boolean.toString(((SchedulingAwareRunnable) task).isLongLived()));
			}
			properties.put(ManagedTask.IDENTITY_NAME, identityName);
			return ManagedExecutors.managedTask(task, properties, null);
		}

		public static <T> Callable<T> buildManagedTask(Callable<T> task, String identityName) {
			Map<String, String> properties = new HashMap<String, String>(1);
			properties.put(ManagedTask.IDENTITY_NAME, identityName);
			return ManagedExecutors.managedTask(task, properties, null);
		}
	}

}
