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

import java.util.concurrent.Executor;

/**
 * Simple task executor interface that abstracts the execution
 * of a {@link Runnable}.
 *
 * <p>Implementations can use all sorts of different execution strategies,
 * such as: synchronous, asynchronous, using a thread pool, and more.
 *
 * <p>Equivalent to JDK 1.5's {@link java.util.concurrent.Executor}
 * interface; extending it now in Spring 3.0, so that clients may declare
 * a dependency on an Executor and receive any TaskExecutor implementation.
 * This interface remains separate from the standard Executor interface
 * mainly for backwards compatibility with JDK 1.4 in Spring 2.x.
 *
 * <p>
 *  简单的任务执行器界面,用于抽象执行{@link Runnable}
 * 
 *  <p>实现可以使用各种不同的执行策略,例如：同步,异步,使用线程池等
 * 
 * <p>相当于JDK 15的{@link javautilconcurrentExecutor}接口;现在在Spring 30中扩展它,以便客户端可以声明对执行程序的依赖并接收任何TaskExecutor
 * 
 * @author Juergen Hoeller
 * @since 2.0
 * @see java.util.concurrent.Executor
 */
public interface TaskExecutor extends Executor {

	/**
	 * Execute the given {@code task}.
	 * <p>The call might return immediately if the implementation uses
	 * an asynchronous execution strategy, or might block in the case
	 * of synchronous execution.
	 * <p>
	 * 实现该接口与标准Executor接口保持分开,主要用于向后兼容JDK 14在Spring 2x。
	 * 
	 * 
	 * @param task the {@code Runnable} to execute (never {@code null})
	 * @throws TaskRejectedException if the given task was not accepted
	 */
	@Override
	void execute(Runnable task);

}
