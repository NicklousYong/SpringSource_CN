/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2014 the original author or authors.
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

package org.springframework.util;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple customizable helper class for creating new {@link Thread} instances.
 * Provides various bean properties: thread name prefix, thread priority, etc.
 *
 * <p>Serves as base class for thread factories such as
 * {@link org.springframework.scheduling.concurrent.CustomizableThreadFactory}.
 *
 * <p>
 *  用于创建新的{@link Thread}实例的简单可定制的助手类提供各种bean属性：线程名称前缀,线程优先级等
 * 
 * <p>作为线程工厂的基类,例如{@link orgspringframeworkschedulingconcurrentCustomizableThreadFactory}
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.0.3
 * @see org.springframework.scheduling.concurrent.CustomizableThreadFactory
 */
@SuppressWarnings("serial")
public class CustomizableThreadCreator implements Serializable {

	private String threadNamePrefix;

	private int threadPriority = Thread.NORM_PRIORITY;

	private boolean daemon = false;

	private ThreadGroup threadGroup;

	private final AtomicInteger threadCount = new AtomicInteger(0);


	/**
	 * Create a new CustomizableThreadCreator with default thread name prefix.
	 * <p>
	 *  使用默认线程名称前缀创建一个新的CustomizableThreadCreator
	 * 
	 */
	public CustomizableThreadCreator() {
		this.threadNamePrefix = getDefaultThreadNamePrefix();
	}

	/**
	 * Create a new CustomizableThreadCreator with the given thread name prefix.
	 * <p>
	 *  使用给定的线程名称前缀创建一个新的CustomizableThreadCreator
	 * 
	 * 
	 * @param threadNamePrefix the prefix to use for the names of newly created threads
	 */
	public CustomizableThreadCreator(String threadNamePrefix) {
		this.threadNamePrefix = (threadNamePrefix != null ? threadNamePrefix : getDefaultThreadNamePrefix());
	}


	/**
	 * Specify the prefix to use for the names of newly created threads.
	 * Default is "SimpleAsyncTaskExecutor-".
	 * <p>
	 *  指定用于新建线程名称的前缀默认为"SimpleAsyncTaskExecutor-"
	 * 
	 */
	public void setThreadNamePrefix(String threadNamePrefix) {
		this.threadNamePrefix = (threadNamePrefix != null ? threadNamePrefix : getDefaultThreadNamePrefix());
	}

	/**
	 * Return the thread name prefix to use for the names of newly
	 * created threads.
	 * <p>
	 *  返回线程名称前缀以用于新创建的线程的名称
	 * 
	 */
	public String getThreadNamePrefix() {
		return this.threadNamePrefix;
	}

	/**
	 * Set the priority of the threads that this factory creates.
	 * Default is 5.
	 * <p>
	 *  设置此工厂创建的线程的优先级为5
	 * 
	 * 
	 * @see java.lang.Thread#NORM_PRIORITY
	 */
	public void setThreadPriority(int threadPriority) {
		this.threadPriority = threadPriority;
	}

	/**
	 * Return the priority of the threads that this factory creates.
	 * <p>
	 *  返回此工厂创建的线程的优先级
	 * 
	 */
	public int getThreadPriority() {
		return this.threadPriority;
	}

	/**
	 * Set whether this factory is supposed to create daemon threads,
	 * just executing as long as the application itself is running.
	 * <p>Default is "false": Concrete factories usually support explicit cancelling.
	 * Hence, if the application shuts down, Runnables will by default finish their
	 * execution.
	 * <p>Specify "true" for eager shutdown of threads which still actively execute
	 * a {@link Runnable} at the time that the application itself shuts down.
	 * <p>
	 * 设置这个工厂是否应该创建守护进程线程,只要应用程序本身运行时执行<p>默认为"false"：具体工厂通常支持显式取消因此,如果应用程序关闭,Runnables将默认完成其执行<p>指定"true"用于在
	 * 应用程序自身关闭时仍然主动执行{@link Runnable}的线程的急切关闭。
	 * 
	 * 
	 * @see java.lang.Thread#setDaemon
	 */
	public void setDaemon(boolean daemon) {
		this.daemon = daemon;
	}

	/**
	 * Return whether this factory should create daemon threads.
	 * <p>
	 *  返回此工厂是否应该创建守护进程线程
	 * 
	 */
	public boolean isDaemon() {
		return this.daemon;
	}

	/**
	 * Specify the name of the thread group that threads should be created in.
	 * <p>
	 *  指定要创建线程的线程组的名称
	 * 
	 * 
	 * @see #setThreadGroup
	 */
	public void setThreadGroupName(String name) {
		this.threadGroup = new ThreadGroup(name);
	}

	/**
	 * Specify the thread group that threads should be created in.
	 * <p>
	 *  指定应在其中创建线程的线程组
	 * 
	 * 
	 * @see #setThreadGroupName
	 */
	public void setThreadGroup(ThreadGroup threadGroup) {
		this.threadGroup = threadGroup;
	}

	/**
	 * Return the thread group that threads should be created in
	 * (or {@code null} for the default group).
	 * <p>
	 *  返回应该创建线程的线程组(或默认组的{@code null})
	 * 
	 */
	public ThreadGroup getThreadGroup() {
		return this.threadGroup;
	}


	/**
	 * Template method for the creation of a new {@link Thread}.
	 * <p>The default implementation creates a new Thread for the given
	 * {@link Runnable}, applying an appropriate thread name.
	 * <p>
	 * 创建新的{@link Thread} <p>的模板方法默认实现为给定的{@link Runnable}创建一个新的线程,应用适当的线程名称
	 * 
	 * 
	 * @param runnable the Runnable to execute
	 * @see #nextThreadName()
	 */
	public Thread createThread(Runnable runnable) {
		Thread thread = new Thread(getThreadGroup(), runnable, nextThreadName());
		thread.setPriority(getThreadPriority());
		thread.setDaemon(isDaemon());
		return thread;
	}

	/**
	 * Return the thread name to use for a newly created {@link Thread}.
	 * <p>The default implementation returns the specified thread name prefix
	 * with an increasing thread count appended: e.g. "SimpleAsyncTaskExecutor-0".
	 * <p>
	 *  返回线程名称以用于新创建的{@link Thread} <p>默认实现返回指定的线程名称前缀,并附加一个增加的线程计数：例如"SimpleAsyncTaskExecutor-0"
	 * 
	 * 
	 * @see #getThreadNamePrefix()
	 */
	protected String nextThreadName() {
		return getThreadNamePrefix() + this.threadCount.incrementAndGet();
	}

	/**
	 * Build the default thread name prefix for this factory.
	 * <p>
	 *  构建此工厂的默认线程名称前缀
	 * 
	 * @return the default thread name prefix (never {@code null})
	 */
	protected String getDefaultThreadNamePrefix() {
		return ClassUtils.getShortName(getClass()) + "-";
	}

}
