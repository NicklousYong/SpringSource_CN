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

package org.springframework.core.task;

/**
 * Exception thrown when a {@link AsyncTaskExecutor} rejects to accept
 * a given task for execution because of the specified timeout.
 *
 * <p>
 *  当{@link AsyncTaskExecutor}由于指定的超时而拒绝接受给定的任务执行时抛出异常
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.0.3
 * @see AsyncTaskExecutor#execute(Runnable, long)
 * @see TaskRejectedException
 */
@SuppressWarnings("serial")
public class TaskTimeoutException extends TaskRejectedException {

	/**
	 * Create a new {@code TaskTimeoutException}
	 * with the specified detail message and no root cause.
	 * <p>
	 *  使用指定的详细消息创建一个新的{@code TaskTimeoutException},而不是根本原因
	 * 
	 * 
	 * @param msg the detail message
	 */
	public TaskTimeoutException(String msg) {
		super(msg);
	}

	/**
	 * Create a new {@code TaskTimeoutException}
	 * with the specified detail message and the given root cause.
	 * <p>
	 * 使用指定的详细消息和给定的根本原因创建一个新的{@code TaskTimeoutException}
	 * 
	 * @param msg the detail message
	 * @param cause the root cause (usually from using an underlying
	 * API such as the {@code java.util.concurrent} package)
	 * @see java.util.concurrent.RejectedExecutionException
	 */
	public TaskTimeoutException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
