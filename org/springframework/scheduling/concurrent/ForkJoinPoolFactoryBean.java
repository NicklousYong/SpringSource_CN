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

package org.springframework.scheduling.concurrent;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.UsesJava7;

/**
 * A Spring {@link FactoryBean} that builds and exposes a preconfigured {@link ForkJoinPool}.
 * May be used on Java 7 and 8 as well as on Java 6 with {@code jsr166.jar} on the classpath
 * (ideally on the VM bootstrap classpath).
 *
 * <p>For details on the ForkJoinPool API and its use with RecursiveActions, see the
 * <a href="http://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ForkJoinPool.html">JDK 7 javadoc</a>.
 *
 * <p>{@code jsr166.jar}, containing {@code java.util.concurrent} updates for Java 6, can be obtained
 * from the <a href="http://gee.cs.oswego.edu/dl/concurrency-interest/">concurrency interest website</a>.
 *
 * <p>
 *  构建和公开预配置的{@link ForkJoinPool}的Spring {@link FactoryBean}可以在Java 7和8以及类别路径上的{@code jsr166jar}上使用Java 
 * 6和Java 6(理想情况是在VM引导类路径上)。
 * 
 * 有关ForkJoinPool API及其与RecursiveActions的使用的详细信息,请参阅<a href=\"http://docsoraclecom/javase/7/docs/api/java/util/concurrent/ForkJoinPool.html\">
 *  JDK 7 javadoc </一>。
 * 
 *  可以从<a href=\"http://geecsoswegoedu/dl/concurrency-interest/\">并发兴趣网站</a获得包含Java 6的{@code javautilconcurrent}更新的{@ code jsr166jar} >
 * 。
 * 
 * 
 * @author Juergen Hoeller
 * @since 3.1
 */
@UsesJava7
public class ForkJoinPoolFactoryBean implements FactoryBean<ForkJoinPool>, InitializingBean, DisposableBean {

	private boolean commonPool = false;

	private int parallelism = Runtime.getRuntime().availableProcessors();

	private ForkJoinPool.ForkJoinWorkerThreadFactory threadFactory = ForkJoinPool.defaultForkJoinWorkerThreadFactory;

	private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

	private boolean asyncMode = false;

	private int awaitTerminationSeconds = 0;

	private ForkJoinPool forkJoinPool;


	/**
	 * Set whether to expose JDK 8's 'common' {@link ForkJoinPool}.
	 * <p>Default is "false", creating a local {@link ForkJoinPool} instance based on the
	 * {@link #setParallelism "parallelism"}, {@link #setThreadFactory "threadFactory"},
	 * {@link #setUncaughtExceptionHandler "uncaughtExceptionHandler"} and
	 * {@link #setAsyncMode "asyncMode"} properties on this FactoryBean.
	 * <p><b>NOTE:</b> Setting this flag to "true" effectively ignores all other
	 * properties on this FactoryBean, reusing the shared common JDK {@link ForkJoinPool}
	 * instead. This is a fine choice on JDK 8 but does remove the application's ability
	 * to customize ForkJoinPool behavior, in particular the use of custom threads.
	 * <p>
	 * 设置是否公开JDK 8的"common"{@link ForkJoinPool} <p>默认值为"false",基于{@link #setParallelism"parallelism"}创建一个本地{@link ForkJoinPool}
	 * 实例,{@link #setThreadFactory "lineFactory"},这个FactoryBean上的{@link #setUncaughtExceptionHandler"uncaughtExceptionHandler"}
	 * 和{@link #setAsyncMode"asyncMode"}属性<p> <b>注意：</b>将此标志设置为"true"有效地忽略所有这个FactoryBean上的其他属性,重用共享的常用JDK {@link ForkJoinPool}
	 * 这是JDK 8的一个很好的选择,但是删除了应用程序定制ForkJoinPool行为的能力,特别是使用自定义线程。
	 * 
	 * 
	 * @since 3.2
	 * @see java.util.concurrent.ForkJoinPool#commonPool()
	 */
	public void setCommonPool(boolean commonPool) {
		this.commonPool = commonPool;
	}

	/**
	 * Specify the parallelism level. Default is {@link Runtime#availableProcessors()}.
	 * <p>
	 *  指定并行级别默认值为{@link Runtime#availableProcessors()}
	 * 
	 */
	public void setParallelism(int parallelism) {
		this.parallelism = parallelism;
	}

	/**
	 * Set the factory for creating new ForkJoinWorkerThreads.
	 * Default is {@link ForkJoinPool#defaultForkJoinWorkerThreadFactory}.
	 * <p>
	 * 设置创建新的ForkJoinWorkerThreads的工厂默认是{@link ForkJoinPool#defaultForkJoinWorkerThreadFactory}
	 * 
	 */
	public void setThreadFactory(ForkJoinPool.ForkJoinWorkerThreadFactory threadFactory) {
		this.threadFactory = threadFactory;
	}

	/**
	 * Set the handler for internal worker threads that terminate due to unrecoverable errors
	 * encountered while executing tasks. Default is none.
	 * <p>
	 *  设置由于在执行任务时遇到不可恢复的错误而终止的内部工作线程的处理程序。默认值为none
	 * 
	 */
	public void setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
		this.uncaughtExceptionHandler = uncaughtExceptionHandler;
	}

	/**
	 * Specify whether to establish a local first-in-first-out scheduling mode for forked tasks
	 * that are never joined. This mode (asyncMode = {@code true}) may be more appropriate
	 * than the default locally stack-based mode in applications in which worker threads only
	 * process event-style asynchronous tasks. Default is {@code false}.
	 * <p>
	 *  指定是否为从未加入的分叉任务建立本地先进先出调度模式此模式(asyncMode = {@code true})可能比在其中工作的应用程序中基于本地堆栈的默认模式更合适线程只处理事件式异步任务默认是{@code false}
	 * 。
	 * 
	 */
	public void setAsyncMode(boolean asyncMode) {
		this.asyncMode = asyncMode;
	}

	/**
	 * Set the maximum number of seconds that this ForkJoinPool is supposed to block
	 * on shutdown in order to wait for remaining tasks to complete their execution
	 * before the rest of the container continues to shut down. This is particularly
	 * useful if your remaining tasks are likely to need access to other resources
	 * that are also managed by the container.
	 * <p>By default, this ForkJoinPool won't wait for the termination of tasks at all.
	 * It will continue to fully execute all ongoing tasks as well as all remaining
	 * tasks in the queue, in parallel to the rest of the container shutting down.
	 * In contrast, if you specify an await-termination period using this property,
	 * this executor will wait for the given time (max) for the termination of tasks.
	 * <p>Note that this feature works for the {@link #setCommonPool "commonPool"}
	 * mode as well. The underlying ForkJoinPool won't actually terminate in that
	 * case but will wait for all tasks to terminate.
	 * <p>
	 * 设置这个ForkJoinPool在关机时应该阻止的最大秒数,以便在剩余的任务可能需要访问之前等待剩余的任务完成执行。也由容器管理的其他资源<p>默认情况下,此ForkJoinPool不会等待终止任务。
	 * 它将继续完全执行所有正在进行的任务以及队列中的所有剩余任务,并行容器的其余部分关闭相反,如果使用此属性指定等待终止期限,则此执行程序将等待给定时间(最大)以终止任务<p>请注意,此功能适用于{@link #setCommonPool"commonPool"}
	 * 
	 * @see java.util.concurrent.ForkJoinPool#shutdown()
	 * @see java.util.concurrent.ForkJoinPool#awaitTermination
	 */
	public void setAwaitTerminationSeconds(int awaitTerminationSeconds) {
		this.awaitTerminationSeconds = awaitTerminationSeconds;
	}

	@Override
	public void afterPropertiesSet() {
		this.forkJoinPool = (this.commonPool ? ForkJoinPool.commonPool() :
				new ForkJoinPool(this.parallelism, this.threadFactory, this.uncaughtExceptionHandler, this.asyncMode));
	}


	@Override
	public ForkJoinPool getObject() {
		return this.forkJoinPool;
	}

	@Override
	public Class<?> getObjectType() {
		return ForkJoinPool.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}


	@Override
	public void destroy() {
		// Ignored for the common pool.
		this.forkJoinPool.shutdown();

		// Wait for all tasks to terminate - works for the common pool as well.
		if (this.awaitTerminationSeconds > 0) {
			try {
				this.forkJoinPool.awaitTermination(this.awaitTerminationSeconds, TimeUnit.SECONDS);
			}
			catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}
	}

}
