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

package org.springframework.core.task;

import java.util.concurrent.Callable;

import org.springframework.util.concurrent.ListenableFuture;

/**
 * Extension of the {@link AsyncTaskExecutor} interface, adding the capability to submit
 * tasks for {@link ListenableFuture}s.
 *
 * <p>
 *  扩展{@link AsyncTaskExecutor}界面,添加了为{@link ListenableFuture}提交任务的功能
 * 
 * 
 * @author Arjen Poutsma
 * @since 4.0
 * @see ListenableFuture
 */
public interface AsyncListenableTaskExecutor extends AsyncTaskExecutor {

	/**
	 * Submit a {@code Runnable} task for execution, receiving a {@code ListenableFuture}
	 * representing that task. The Future will return a {@code null} result upon completion.
	 * <p>
	 * 提交一个{@code Runnable}任务执行,收到代表该任务的{@code ListenableFuture}未来将在完成后返回{@code null}结果
	 * 
	 * 
	 * @param task the {@code Runnable} to execute (never {@code null})
	 * @return a {@code ListenableFuture} representing pending completion of the task
	 * @throws TaskRejectedException if the given task was not accepted
	 */
	ListenableFuture<?> submitListenable(Runnable task);

	/**
	 * Submit a {@code Callable} task for execution, receiving a {@code ListenableFuture}
	 * representing that task. The Future will return the Callable's result upon
	 * completion.
	 * <p>
	 *  提交{@code Callable}任务执行,接收代表该任务的{@code ListenableFuture}未来将在完成后返回Callable的结果
	 * 
	 * @param task the {@code Callable} to execute (never {@code null})
	 * @return a {@code ListenableFuture} representing pending completion of the task
	 * @throws TaskRejectedException if the given task was not accepted
	 */
	<T> ListenableFuture<T> submitListenable(Callable<T> task);

}
