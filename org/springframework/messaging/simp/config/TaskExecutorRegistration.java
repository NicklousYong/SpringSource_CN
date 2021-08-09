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

package org.springframework.messaging.simp.config;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * A registration class for customizing the properties of {@link ThreadPoolTaskExecutor}.
 *
 * <p>
 *  用于自定义{@link ThreadPoolTask​​Executor}的属性的注册类
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public class TaskExecutorRegistration {

	private ThreadPoolTaskExecutor taskExecutor;

	private int corePoolSize = Runtime.getRuntime().availableProcessors() * 2;

	private int maxPoolSize = Integer.MAX_VALUE;

	private int queueCapacity = Integer.MAX_VALUE;

	private int keepAliveSeconds = 60;


	public TaskExecutorRegistration() {
	}

	public TaskExecutorRegistration(ThreadPoolTaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	/**
	 * Set the core pool size of the ThreadPoolExecutor.
	 * <p><strong>NOTE:</strong> The core pool size is effectively the max pool size
	 * when an unbounded {@link #queueCapacity(int) queueCapacity} is configured
	 * (the default). This is essentially the "Unbounded queues" strategy as explained
	 * in {@link java.util.concurrent.ThreadPoolExecutor ThreadPoolExecutor}. When
	 * this strategy is used, the {@link #maxPoolSize(int) maxPoolSize} is ignored.
	 * <p>By default this is set to twice the value of
	 * {@link Runtime#availableProcessors()}. In an an application where tasks do not
	 * block frequently, the number should be closer to or equal to the number of
	 * available CPUs/cores.
	 * <p>
	 * 设置ThreadPoolExecutor的核心池大小<p> <strong>注意：</strong>当配置无界{@link #queueCapacity(int)queueCapacity}(默认值)时
	 * ,核心池大小实际上是最大池大小。
	 * 本质上是{@link javautilconcurrentThreadPoolExecutor ThreadPoolExecutor}中所述的"无界队列"策略。
	 * 当使用此策略时,将忽略{@link #maxPoolSize(int)maxPoolSize} <p>默认情况下,该值设置为{ @link Runtime#availableProcessors()}在
	 * 任务不频繁阻塞的应用程序中,该数字应该接近或等于可用CPU /内核的数量。
	 * 本质上是{@link javautilconcurrentThreadPoolExecutor ThreadPoolExecutor}中所述的"无界队列"策略。
	 * 
	 */
	public TaskExecutorRegistration corePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
		return this;
	}

	/**
	 * Set the max pool size of the ThreadPoolExecutor.
	 * <p><strong>NOTE:</strong> When an unbounded
	 * {@link #queueCapacity(int) queueCapacity} is configured (the default), the
	 * max pool size is effectively ignored. See the "Unbounded queues" strategy
	 * in {@link java.util.concurrent.ThreadPoolExecutor ThreadPoolExecutor} for
	 * more details.
	 * <p>By default this is set to {@code Integer.MAX_VALUE}.
	 * <p>
	 * 设置ThreadPoolExecutor的最大池大小<p> <strong>注意：</strong>当配置无界{@link #queueCapacity(int)queueCapacity}(默认值)时
	 * ,最大池大小被有效忽略请参见" {@link javautilconcurrentThreadPoolExecutor ThreadPoolExecutor}了解更多详细信息的<无限队列"策略<p>默认
	 * 情况下,此选项设置为{@code IntegerMAX_VALUE}。
	 * 
	 */
	public TaskExecutorRegistration maxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
		return this;
	}

	/**
	 * Set the queue capacity for the ThreadPoolExecutor.
	 * <p><strong>NOTE:</strong> when an unbounded {@code queueCapacity} is configured
	 * (the default), the core pool size is effectively the max pool size. This is
	 * essentially the "Unbounded queues" strategy as explained in
	 * {@link java.util.concurrent.ThreadPoolExecutor ThreadPoolExecutor}. When
	 * this strategy is used, the {@link #maxPoolSize(int) maxPoolSize} is ignored.
	 * <p>By default this is set to {@code Integer.MAX_VALUE}.
	 * <p>
	 * 设置ThreadPoolExecutor的队列容量<p> <strong>注意：</strong>当配置无限制的{@code queueCapacity}(默认值)时,核心池大小实际上是最大池大小这本质
	 * 上是"无界队列"策略,如{@link javautilconcurrentThreadPoolExecutor ThreadPoolExecutor}中所述。
	 * 使用此策略时,将忽略{@link #maxPoolSize(int)maxPoolSize} <p>默认情况下,该值设置为{@code IntegerMAX_VALUE}。
	 * 
	 */
	public TaskExecutorRegistration queueCapacity(int queueCapacity) {
		this.queueCapacity = queueCapacity;
		return this;
	}

	/**
	 * Set the time limit for which threads may remain idle before being terminated.
	 * If there are more than the core number of threads currently in the pool,
	 * after waiting this amount of time without processing a task, excess threads
	 * will be terminated. This overrides any value set in the constructor.
	 * <p>By default this is set to 60.
	 * <p>
	 */
	public TaskExecutorRegistration keepAliveSeconds(int keepAliveSeconds) {
		this.keepAliveSeconds = keepAliveSeconds;
		return this;
	}

	protected ThreadPoolTaskExecutor getTaskExecutor() {
		ThreadPoolTaskExecutor executor = (this.taskExecutor != null ? this.taskExecutor : new ThreadPoolTaskExecutor());
		executor.setCorePoolSize(this.corePoolSize);
		executor.setMaxPoolSize(this.maxPoolSize);
		executor.setKeepAliveSeconds(this.keepAliveSeconds);
		executor.setQueueCapacity(this.queueCapacity);
		executor.setAllowCoreThreadTimeOut(true);
		return executor;
	}

}
