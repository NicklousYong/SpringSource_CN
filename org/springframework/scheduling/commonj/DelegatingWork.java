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

package org.springframework.scheduling.commonj;

import commonj.work.Work;

import org.springframework.scheduling.SchedulingAwareRunnable;
import org.springframework.util.Assert;

/**
 * Simple Work adapter that delegates to a given Runnable.
 *
 * <p>
 *  简单工作适配器,委托给给定的Runnable
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.0
 * @see commonj.work.Work
 * @see java.lang.Runnable
 */
public class DelegatingWork implements Work {

	private final Runnable delegate;


	/**
	 * Create a new DelegatingWork.
	 * <p>
	 *  创建一个新的DelegatingWork
	 * 
	 * 
	 * @param delegate the Runnable implementation to delegate to
	 * (may be a SchedulingAwareRunnable for extended support)
	 * @see org.springframework.scheduling.SchedulingAwareRunnable
	 * @see #isDaemon()
	 */
	public DelegatingWork(Runnable delegate) {
		Assert.notNull(delegate, "Delegate must not be null");
		this.delegate = delegate;
	}

	/**
	 * Return the wrapped Runnable implementation.
	 * <p>
	 *  返回包装的Runnable实现
	 * 
	 */
	public final Runnable getDelegate() {
		return this.delegate;
	}


	/**
	 * Delegates execution to the underlying Runnable.
	 * <p>
	 *  将执行委托给底层的Runnable
	 * 
	 */
	@Override
	public void run() {
		this.delegate.run();
	}

	/**
	 * This implementation delegates to
	 * {@link org.springframework.scheduling.SchedulingAwareRunnable#isLongLived()},
	 * if available.
	 * <p>
	 * 此实现委托{@link orgspringframeworkschedulingSchedulingAwareRunnable#isLongLived()},如果可用
	 * 
	 */
	@Override
	public boolean isDaemon() {
		return (this.delegate instanceof SchedulingAwareRunnable &&
				((SchedulingAwareRunnable) this.delegate).isLongLived());
	}

	/**
	 * This implementation is empty, since we expect the Runnable
	 * to terminate based on some specific shutdown signal.
	 * <p>
	 *  这个实现是空的,因为我们期望Runnable根据一些特定的关闭信号终止
	 */
	@Override
	public void release() {
	}

}
