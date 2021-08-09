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

package org.springframework.context.event;

import java.util.concurrent.Executor;

import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.ResolvableType;
import org.springframework.util.ErrorHandler;

/**
 * Simple implementation of the {@link ApplicationEventMulticaster} interface.
 *
 * <p>Multicasts all events to all registered listeners, leaving it up to
 * the listeners to ignore events that they are not interested in.
 * Listeners will usually perform corresponding {@code instanceof}
 * checks on the passed-in event object.
 *
 * <p>By default, all listeners are invoked in the calling thread.
 * This allows the danger of a rogue listener blocking the entire application,
 * but adds minimal overhead. Specify an alternative task executor to have
 * listeners executed in different threads, for example from a thread pool.
 *
 * <p>
 *  简单实现{@link ApplicationEventMulticaster}界面
 * 
 * 将所有事件组播到所有已注册的侦听器,留给侦听器忽略他们不感​​兴趣的事件侦听器通常对传入的事件对象执行相应的{@code instanceof}检查
 * 
 *  默认情况下,在调用线程中调用所有侦听器这允许流氓侦听器阻止整个应用程序的危险,但增加最小的开销指定一个替代的任务执行程序,以使侦听器在不同的线程中执行,例如从线程池
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 * @see #setTaskExecutor
 */
public class SimpleApplicationEventMulticaster extends AbstractApplicationEventMulticaster {

	private Executor taskExecutor;

	private ErrorHandler errorHandler;


	/**
	 * Create a new SimpleApplicationEventMulticaster.
	 * <p>
	 *  创建一个新的SimpleApplicationEventMulticaster
	 * 
	 */
	public SimpleApplicationEventMulticaster() {
	}

	/**
	 * Create a new SimpleApplicationEventMulticaster for the given BeanFactory.
	 * <p>
	 *  为给定的BeanFactory创建一个新的SimpleApplicationEventMulticaster
	 * 
	 */
	public SimpleApplicationEventMulticaster(BeanFactory beanFactory) {
		setBeanFactory(beanFactory);
	}


	/**
	 * Set a custom executor (typically a {@link org.springframework.core.task.TaskExecutor})
	 * to invoke each listener with.
	 * <p>Default is equivalent to {@link org.springframework.core.task.SyncTaskExecutor},
	 * executing all listeners synchronously in the calling thread.
	 * <p>Consider specifying an asynchronous task executor here to not block the
	 * caller until all listeners have been executed. However, note that asynchronous
	 * execution will not participate in the caller's thread context (class loader,
	 * transaction association) unless the TaskExecutor explicitly supports this.
	 * <p>
	 * 设置一个自定义执行程序(通常是{@link orgspringframeworkcoretaskTaskExecutor})以使用<p>调用每个监听器。
	 * Default等效于{@link orgspringframeworkcoretaskSyncTaskExecutor},在调用线程中同步执行所有侦听器<p>请考虑在此处指定异步任务执行程序不阻塞调用者
	 * ,直到所有侦听器都被执行。
	 * 设置一个自定义执行程序(通常是{@link orgspringframeworkcoretaskTaskExecutor})以使用<p>调用每个监听器。
	 * 但是,请注意,异步执行不会参与调用者的线程上下文(类加载器,事务关联),除非TaskExecutor明确支持。
	 * 
	 * 
	 * @see org.springframework.core.task.SyncTaskExecutor
	 * @see org.springframework.core.task.SimpleAsyncTaskExecutor
	 */
	public void setTaskExecutor(Executor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	/**
	 * Return the current task executor for this multicaster.
	 * <p>
	 *  返回此多核心的当前任务执行程序
	 * 
	 */
	protected Executor getTaskExecutor() {
		return this.taskExecutor;
	}

	/**
	 * Set the {@link ErrorHandler} to invoke in case an exception is thrown
	 * from a listener.
	 * <p>Default is none, with a listener exception stopping the current
	 * multicast and getting propagated to the publisher of the current event.
	 * If a {@linkplain #setTaskExecutor task executor} is specified, each
	 * individual listener exception will get propagated to the executor but
	 * won't necessarily stop execution of other listeners.
	 * <p>Consider setting an {@link ErrorHandler} implementation that catches
	 * and logs exceptions (a la
	 * {@link org.springframework.scheduling.support.TaskUtils#LOG_AND_SUPPRESS_ERROR_HANDLER})
	 * or an implementation that logs exceptions while nevertheless propagating them
	 * (e.g. {@link org.springframework.scheduling.support.TaskUtils#LOG_AND_PROPAGATE_ERROR_HANDLER}).
	 * <p>
	 * 设置{@link ErrorHandler}以调用侦听器引发异常<p>默认值为none,侦听器异常停止当前多播并传播到当前事件的发布者如果{@linkplain #setTaskExecutor任务执行程序}
	 * 被指定,每个单独的侦听器异常将被传播到执行程序,但不一定会停止执行其他侦听器<p>考虑设置捕获和记录异常的{@link ErrorHandler}实现(一个{@link orgspringframeworkschedulingsupportTaskUtils #LOG_AND_SUPPRESS_ERROR_HANDLER}
	 * )或在传播它们时记录异常的实现(例如{@link orgspringframeworkschedulingsupportTaskUtils#LOG_AND_PROPAGATE_ERROR_HANDLER}
	 * )。
	 * 
	 * 
	 * @since 4.1
	 */
	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	/**
	 * Return the current error handler for this multicaster.
	 * <p>
	 * 返回此多数据库的当前错误处理程序
	 * 
	 * 
	 * @since 4.1
	 */
	protected ErrorHandler getErrorHandler() {
		return this.errorHandler;
	}


	@Override
	public void multicastEvent(ApplicationEvent event) {
		multicastEvent(event, resolveDefaultEventType(event));
	}

	@Override
	public void multicastEvent(final ApplicationEvent event, ResolvableType eventType) {
		ResolvableType type = (eventType != null ? eventType : resolveDefaultEventType(event));
		for (final ApplicationListener<?> listener : getApplicationListeners(event, type)) {
			Executor executor = getTaskExecutor();
			if (executor != null) {
				executor.execute(new Runnable() {
					@Override
					public void run() {
						invokeListener(listener, event);
					}
				});
			}
			else {
				invokeListener(listener, event);
			}
		}
	}

	private ResolvableType resolveDefaultEventType(ApplicationEvent event) {
		return ResolvableType.forInstance(event);
	}

	/**
	 * Invoke the given listener with the given event.
	 * <p>
	 *  使用给定的事件调用给定的侦听器
	 * 
	 * @param listener the ApplicationListener to invoke
	 * @param event the current event to propagate
	 * @since 4.1
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	protected void invokeListener(ApplicationListener listener, ApplicationEvent event) {
		ErrorHandler errorHandler = getErrorHandler();
		if (errorHandler != null) {
			try {
				listener.onApplicationEvent(event);
			}
			catch (Throwable err) {
				errorHandler.handleError(err);
			}
		}
		else {
			try {
				listener.onApplicationEvent(event);
			}
			catch (ClassCastException ex) {
				// Possibly a lambda-defined listener which we could not resolve the generic event type for
				LogFactory.getLog(getClass()).debug("Non-matching event type for listener: " + listener, ex);
			}
		}
	}

}
