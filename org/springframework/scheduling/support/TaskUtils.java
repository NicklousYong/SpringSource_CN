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

import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.util.ErrorHandler;
import org.springframework.util.ReflectionUtils;

/**
 * Utility methods for decorating tasks with error handling.
 *
 * <p><b>NOTE:</b> This class is intended for internal use by Spring's scheduler
 * implementations. It is only public so that it may be accessed from impl classes
 * within other packages. It is <i>not</i> intended for general use.
 *
 * <p>
 *  使用错误处理装饰任务的实用方法
 * 
 * <p> <b>注意：</b>此类用于Spring的调度程序实现内部使用它只是公共的,以便可以从其他包中的impl类访问它是<i>不是</i>一般用途
 * 
 * 
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @since 3.0
 */
public abstract class TaskUtils {

	/**
	 * An ErrorHandler strategy that will log the Exception but perform
	 * no further handling. This will suppress the error so that
	 * subsequent executions of the task will not be prevented.
	 * <p>
	 *  一个ErrorHandler策略将记录异常,但不执行进一步处理这将会抑制错误,以便后续执行任务不会被阻止
	 * 
	 */
	public static final ErrorHandler LOG_AND_SUPPRESS_ERROR_HANDLER = new LoggingErrorHandler();

	/**
	 * An ErrorHandler strategy that will log at error level and then
	 * re-throw the Exception. Note: this will typically prevent subsequent
	 * execution of a scheduled task.
	 * <p>
	 *  ErrorHandler策略将以错误级别进行登录,然后重新抛出异常注意：这通常会阻止后续执行计划任务
	 * 
	 */
	public static final ErrorHandler LOG_AND_PROPAGATE_ERROR_HANDLER = new PropagatingErrorHandler();


	/**
	 * Decorate the task for error handling. If the provided {@link ErrorHandler}
	 * is not {@code null}, it will be used. Otherwise, repeating tasks will have
	 * errors suppressed by default whereas one-shot tasks will have errors
	 * propagated by default since those errors may be expected through the
	 * returned {@link Future}. In both cases, the errors will be logged.
	 * <p>
	 * 装载错误处理的任务如果提供的{@link ErrorHandler}不是{@code null},则将被使用否则,默认情况下重复的任务将被禁止,而单次任务将默认传播错误,因为这些错误可能会通过返回的{@link Future}
	 * 预期。
	 * 在这两种情况下,将记录错误。
	 * 
	 */
	public static DelegatingErrorHandlingRunnable decorateTaskWithErrorHandler(
			Runnable task, ErrorHandler errorHandler, boolean isRepeatingTask) {

		if (task instanceof DelegatingErrorHandlingRunnable) {
			return (DelegatingErrorHandlingRunnable) task;
		}
		ErrorHandler eh = (errorHandler != null ? errorHandler : getDefaultErrorHandler(isRepeatingTask));
		return new DelegatingErrorHandlingRunnable(task, eh);
	}

	/**
	 * Return the default {@link ErrorHandler} implementation based on the boolean
	 * value indicating whether the task will be repeating or not. For repeating tasks
	 * it will suppress errors, but for one-time tasks it will propagate. In both
	 * cases, the error will be logged.
	 * <p>
	 *  返回基于布尔值的默认{@link ErrorHandler}实现,该值指示任务是否将重复。对于重复任务,它将抑制错误,但对于一次性任务,它将传播在这两种情况下,将记录错误
	 * 
	 */
	public static ErrorHandler getDefaultErrorHandler(boolean isRepeatingTask) {
		return (isRepeatingTask ? LOG_AND_SUPPRESS_ERROR_HANDLER : LOG_AND_PROPAGATE_ERROR_HANDLER);
	}


	/**
	 * An {@link ErrorHandler} implementation that logs the Throwable at error
 	 * level. It does not perform any additional error handling. This can be
 	 * useful when suppression of errors is the intended behavior.
 	 * <p>
 	 * 在错误级别记录Throwable的{@link ErrorHandler}实现它不执行任何其他错误处理在禁止错误是预期行为时,这可能很有用
 	 * 
	 */
	private static class LoggingErrorHandler implements ErrorHandler {

		private final Log logger = LogFactory.getLog(LoggingErrorHandler.class);

		@Override
		public void handleError(Throwable t) {
			if (logger.isErrorEnabled()) {
				logger.error("Unexpected error occurred in scheduled task.", t);
			}
		}
	}


	/**
	 * An {@link ErrorHandler} implementation that logs the Throwable at error
	 * level and then propagates it.
	 * <p>
	 *  一个{@link ErrorHandler}实现,它将Throwable记录在错误级别,然后传播它
	 */
	private static class PropagatingErrorHandler extends LoggingErrorHandler {

		@Override
		public void handleError(Throwable t) {
			super.handleError(t);
			ReflectionUtils.rethrowRuntimeException(t);
		}
	}

}
