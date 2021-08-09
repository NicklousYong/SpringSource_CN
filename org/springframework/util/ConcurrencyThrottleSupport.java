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

package org.springframework.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Support class for throttling concurrent access to a specific resource.
 *
 * <p>Designed for use as a base class, with the subclass invoking
 * the {@link #beforeAccess()} and {@link #afterAccess()} methods at
 * appropriate points of its workflow. Note that {@code afterAccess}
 * should usually be called in a finally block!
 *
 * <p>The default concurrency limit of this support class is -1
 * ("unbounded concurrency"). Subclasses may override this default;
 * check the javadoc of the concrete class that you're using.
 *
 * <p>
 *  用于限制对特定资源的并发访问的支持类
 * 
 * <p>设计用作基类,子类在其工作流程的适当点调用{@link #beforeAccess()}和{@link #afterAccess()}方法注意,{@code afterAccess}通常应为在一个
 * 最后的块中呼吁！。
 * 
 *  <p>此支持类的默认并发限制为-1("无界并发")子类可以覆盖此默认值;检查您正在使用的具体类的javadoc
 * 
 * 
 * @author Juergen Hoeller
 * @since 1.2.5
 * @see #setConcurrencyLimit
 * @see #beforeAccess()
 * @see #afterAccess()
 * @see org.springframework.aop.interceptor.ConcurrencyThrottleInterceptor
 * @see java.io.Serializable
 */
@SuppressWarnings("serial")
public abstract class ConcurrencyThrottleSupport implements Serializable {

	/**
	 * Permit any number of concurrent invocations: that is, don't throttle concurrency.
	 * <p>
	 *  允许任意数量的并发调用：即不要限制并发
	 * 
	 */
	public static final int UNBOUNDED_CONCURRENCY = -1;

	/**
	 * Switch concurrency 'off': that is, don't allow any concurrent invocations.
	 * <p>
	 *  切换并发"关闭"：即不允许任何并发调用
	 * 
	 */
	public static final int NO_CONCURRENCY = 0;


	/** Transient to optimize serialization */
	protected transient Log logger = LogFactory.getLog(getClass());

	private transient Object monitor = new Object();

	private int concurrencyLimit = UNBOUNDED_CONCURRENCY;

	private int concurrencyCount = 0;


	/**
	 * Set the maximum number of concurrent access attempts allowed.
	 * -1 indicates unbounded concurrency.
	 * <p>In principle, this limit can be changed at runtime,
	 * although it is generally designed as a config time setting.
	 * <p>NOTE: Do not switch between -1 and any concrete limit at runtime,
	 * as this will lead to inconsistent concurrency counts: A limit
	 * of -1 effectively turns off concurrency counting completely.
	 * <p>
	 * 设置允许的并发访问尝试的最大数量-1表示无限制并发<p>原则上,此限制可以在运行时更改,尽管它通常设计为配置时间设置<p>注意：不要在-1和运行时的任何具体限制,因为这将导致不一致的并发计数：-1的限制
	 * 有效地关闭并发计数完全。
	 * 
	 */
	public void setConcurrencyLimit(int concurrencyLimit) {
		this.concurrencyLimit = concurrencyLimit;
	}

	/**
	 * Return the maximum number of concurrent access attempts allowed.
	 * <p>
	 *  返回允许的最大并发访问尝试次数
	 * 
	 */
	public int getConcurrencyLimit() {
		return this.concurrencyLimit;
	}

	/**
	 * Return whether this throttle is currently active.
	 * <p>
	 *  返回此节气门当前是否活动
	 * 
	 * 
	 * @return {@code true} if the concurrency limit for this instance is active
	 * @see #getConcurrencyLimit()
	 */
	public boolean isThrottleActive() {
		return (this.concurrencyLimit > 0);
	}


	/**
	 * To be invoked before the main execution logic of concrete subclasses.
	 * <p>This implementation applies the concurrency throttle.
	 * <p>
	 *  在具体子类的主要执行逻辑之前被调用<p>此实现应用并发节流
	 * 
	 * 
	 * @see #afterAccess()
	 */
	protected void beforeAccess() {
		if (this.concurrencyLimit == NO_CONCURRENCY) {
			throw new IllegalStateException(
					"Currently no invocations allowed - concurrency limit set to NO_CONCURRENCY");
		}
		if (this.concurrencyLimit > 0) {
			boolean debug = logger.isDebugEnabled();
			synchronized (this.monitor) {
				boolean interrupted = false;
				while (this.concurrencyCount >= this.concurrencyLimit) {
					if (interrupted) {
						throw new IllegalStateException("Thread was interrupted while waiting for invocation access, " +
								"but concurrency limit still does not allow for entering");
					}
					if (debug) {
						logger.debug("Concurrency count " + this.concurrencyCount +
								" has reached limit " + this.concurrencyLimit + " - blocking");
					}
					try {
						this.monitor.wait();
					}
					catch (InterruptedException ex) {
						// Re-interrupt current thread, to allow other threads to react.
						Thread.currentThread().interrupt();
						interrupted = true;
					}
				}
				if (debug) {
					logger.debug("Entering throttle at concurrency count " + this.concurrencyCount);
				}
				this.concurrencyCount++;
			}
		}
	}

	/**
	 * To be invoked after the main execution logic of concrete subclasses.
	 * <p>
	 *  在具体子类的主要执行逻辑之后被调用
	 * 
	 * @see #beforeAccess()
	 */
	protected void afterAccess() {
		if (this.concurrencyLimit >= 0) {
			synchronized (this.monitor) {
				this.concurrencyCount--;
				if (logger.isDebugEnabled()) {
					logger.debug("Returning from throttle at concurrency count " + this.concurrencyCount);
				}
				this.monitor.notify();
			}
		}
	}


	//---------------------------------------------------------------------
	// Serialization support
	//---------------------------------------------------------------------

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		// Rely on default serialization, just initialize state after deserialization.
		ois.defaultReadObject();

		// Initialize transient fields.
		this.logger = LogFactory.getLog(getClass());
		this.monitor = new Object();
	}

}
