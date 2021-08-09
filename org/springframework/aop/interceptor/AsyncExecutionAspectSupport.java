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

package org.springframework.aop.interceptor;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.lang.UsesJava8;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * Base class for asynchronous method execution aspects, such as
 * {@code org.springframework.scheduling.annotation.AnnotationAsyncExecutionInterceptor}
 * or {@code org.springframework.scheduling.aspectj.AnnotationAsyncExecutionAspect}.
 *
 * <p>Provides support for <i>executor qualification</i> on a method-by-method basis.
 * {@code AsyncExecutionAspectSupport} objects must be constructed with a default {@code
 * Executor}, but each individual method may further qualify a specific {@code Executor}
 * bean to be used when executing it, e.g. through an annotation attribute.
 *
 * <p>
 * 用于异步方法执行方面的基类,例如{@code orgspringframeworkschedulingannotationAnnotationAsyncExecutionInterceptor}或{@code orgspringframeworkschedulingaspectjAnnotationAsyncExecutionAspect}
 * 。
 * 
 *  <p>在逐个方法的基础上提供对<i>执行者资格</i>的支持{@code AsyncExecutionAspectSupport}对象必须使用默认的{@code Executor}构建,但每个单独的方
 * 法可能会进一步限定具体的{@code Executor} bean在执行时使用,例如通过注释属性。
 * 
 * 
 * @author Chris Beams
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 * @since 3.1.2
 */
public abstract class AsyncExecutionAspectSupport implements BeanFactoryAware {

	/**
	 * The default name of the {@link TaskExecutor} bean to pick up: "taskExecutor".
	 * <p>Note that the initial lookup happens by type; this is just the fallback
	 * in case of multiple executor beans found in the context.
	 * <p>
	 *  要取出的{@link TaskExecutor} bean的默认名称为"taskExecutor"<p>请注意,初始查找按类型发生;这只是在上下文中发现多个执行程序bean的情况下的后备
	 * 
	 * 
	 * @since 4.2.6
	 */
	public static final String DEFAULT_TASK_EXECUTOR_BEAN_NAME = "taskExecutor";


	// Java 8's CompletableFuture type present?
	private static final boolean completableFuturePresent = ClassUtils.isPresent(
			"java.util.concurrent.CompletableFuture", AsyncExecutionInterceptor.class.getClassLoader());


	protected final Log logger = LogFactory.getLog(getClass());

	private final Map<Method, AsyncTaskExecutor> executors = new ConcurrentHashMap<Method, AsyncTaskExecutor>(16);

	private volatile Executor defaultExecutor;

	private AsyncUncaughtExceptionHandler exceptionHandler;

	private BeanFactory beanFactory;


	/**
	 * Create a new instance with a default {@link AsyncUncaughtExceptionHandler}.
	 * <p>
	 * 使用默认的{@link AsyncUncaughtExceptionHandler}创建一个新的实例
	 * 
	 * 
	 * @param defaultExecutor the {@code Executor} (typically a Spring {@code AsyncTaskExecutor}
	 * or {@link java.util.concurrent.ExecutorService}) to delegate to, unless a more specific
	 * executor has been requested via a qualifier on the async method, in which case the
	 * executor will be looked up at invocation time against the enclosing bean factory
	 */
	public AsyncExecutionAspectSupport(Executor defaultExecutor) {
		this(defaultExecutor, new SimpleAsyncUncaughtExceptionHandler());
	}

	/**
	 * Create a new {@link AsyncExecutionAspectSupport} with the given exception handler.
	 * <p>
	 *  使用给定的异常处理程序创建一个新的{@link AsyncExecutionAspectSupport}
	 * 
	 * 
	 * @param defaultExecutor the {@code Executor} (typically a Spring {@code AsyncTaskExecutor}
	 * or {@link java.util.concurrent.ExecutorService}) to delegate to, unless a more specific
	 * executor has been requested via a qualifier on the async method, in which case the
	 * executor will be looked up at invocation time against the enclosing bean factory
	 * @param exceptionHandler the {@link AsyncUncaughtExceptionHandler} to use
	 */
	public AsyncExecutionAspectSupport(Executor defaultExecutor, AsyncUncaughtExceptionHandler exceptionHandler) {
		this.defaultExecutor = defaultExecutor;
		this.exceptionHandler = exceptionHandler;
	}


	/**
	 * Supply the executor to be used when executing async methods.
	 * <p>
	 *  提供执行异步方法时使用的执行器
	 * 
	 * 
	 * @param defaultExecutor the {@code Executor} (typically a Spring {@code AsyncTaskExecutor}
	 * or {@link java.util.concurrent.ExecutorService}) to delegate to, unless a more specific
	 * executor has been requested via a qualifier on the async method, in which case the
	 * executor will be looked up at invocation time against the enclosing bean factory
	 * @see #getExecutorQualifier(Method)
	 * @see #setBeanFactory(BeanFactory)
	 * @see #getDefaultExecutor(BeanFactory)
	 */
	public void setExecutor(Executor defaultExecutor) {
		this.defaultExecutor = defaultExecutor;
	}

	/**
	 * Supply the {@link AsyncUncaughtExceptionHandler} to use to handle exceptions
	 * thrown by invoking asynchronous methods with a {@code void} return type.
	 * <p>
	 *  提供{@link AsyncUncaughtExceptionHandler}以用于处理通过使用{@code void}返回类型调用异步方法抛出的异常
	 * 
	 */
	public void setExceptionHandler(AsyncUncaughtExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

	/**
	 * Set the {@link BeanFactory} to be used when looking up executors by qualifier
	 * or when relying on the default executor lookup algorithm.
	 * <p>
	 *  将{@link BeanFactory}设置为在限定符查找执行程序时使用,或者依靠默认的执行程序查找算法
	 * 
	 * 
	 * @see #findQualifiedExecutor(BeanFactory, String)
	 * @see #getDefaultExecutor(BeanFactory)
	 */
	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}


	/**
	 * Determine the specific executor to use when executing the given method.
	 * Should preferably return an {@link AsyncListenableTaskExecutor} implementation.
	 * <p>
	 *  确定执行给定方法时使用的特定执行程序应优先返回一个{@link AsyncListenableTaskExecutor}实现
	 * 
	 * 
	 * @return the executor to use (or {@code null}, but just if no default executor is available)
	 */
	protected AsyncTaskExecutor determineAsyncExecutor(Method method) {
		AsyncTaskExecutor executor = this.executors.get(method);
		if (executor == null) {
			Executor targetExecutor;
			String qualifier = getExecutorQualifier(method);
			if (StringUtils.hasLength(qualifier)) {
				targetExecutor = findQualifiedExecutor(this.beanFactory, qualifier);
			}
			else {
				targetExecutor = this.defaultExecutor;
				if (targetExecutor == null) {
					synchronized (this.executors) {
						if (this.defaultExecutor == null) {
							this.defaultExecutor = getDefaultExecutor(this.beanFactory);
						}
						targetExecutor = this.defaultExecutor;
					}
				}
			}
			if (targetExecutor == null) {
				return null;
			}
			executor = (targetExecutor instanceof AsyncListenableTaskExecutor ?
					(AsyncListenableTaskExecutor) targetExecutor : new TaskExecutorAdapter(targetExecutor));
			this.executors.put(method, executor);
		}
		return executor;
	}

	/**
	 * Return the qualifier or bean name of the executor to be used when executing the
	 * given async method, typically specified in the form of an annotation attribute.
	 * Returning an empty string or {@code null} indicates that no specific executor has
	 * been specified and that the {@linkplain #setExecutor(Executor) default executor}
	 * should be used.
	 * <p>
	 * 返回在执行给定异步方法时使用的执行器的限定符或bean名称,通常以注释属性的形式指定返回空字符串或{@code null}表示没有指定特定执行程序,并且{应该使用@linkplain #setExecutor(Executor)default executor}
	 * 。
	 * 
	 * 
	 * @param method the method to inspect for executor qualifier metadata
	 * @return the qualifier if specified, otherwise empty String or {@code null}
	 * @see #determineAsyncExecutor(Method)
	 * @see #findQualifiedExecutor(BeanFactory, String)
	 */
	protected abstract String getExecutorQualifier(Method method);

	/**
	 * Retrieve a target executor for the given qualifier.
	 * <p>
	 *  检索给定限定词的目标执行者
	 * 
	 * 
	 * @param qualifier the qualifier to resolve
	 * @return the target executor, or {@code null} if none available
	 * @since 4.2.6
	 * @see #getExecutorQualifier(Method)
	 */
	protected Executor findQualifiedExecutor(BeanFactory beanFactory, String qualifier) {
		if (beanFactory == null) {
			throw new IllegalStateException("BeanFactory must be set on " + getClass().getSimpleName() +
					" to access qualified executor '" + qualifier + "'");
		}
		return BeanFactoryAnnotationUtils.qualifiedBeanOfType(beanFactory, Executor.class, qualifier);
	}

	/**
	 * Retrieve or build a default executor for this advice instance.
	 * An executor returned from here will be cached for further use.
	 * <p>The default implementation searches for a unique {@link TaskExecutor} bean
	 * in the context, or for an {@link Executor} bean named "taskExecutor" otherwise.
	 * If neither of the two is resolvable, this implementation will return {@code null}.
	 * <p>
	 *  检索或构建此通知实例的默认执行程序从此处返回的执行程序将被缓存以供进一步使用<p>默认实现在上下文中搜索唯一的{@link TaskExecutor} bean,或者在{@link Executor}
	 *  bean中搜索命名为"taskExecutor"否则如果两者都不可解析,则此实现将返回{@code null}。
	 * 
	 * 
	 * @param beanFactory the BeanFactory to use for a default executor lookup
	 * @return the default executor, or {@code null} if none available
	 * @since 4.2.6
	 * @see #findQualifiedExecutor(BeanFactory, String)
	 * @see #DEFAULT_TASK_EXECUTOR_BEAN_NAME
	 */
	protected Executor getDefaultExecutor(BeanFactory beanFactory) {
		if (beanFactory != null) {
			try {
				// Search for TaskExecutor bean... not plain Executor since that would
				// match with ScheduledExecutorService as well, which is unusable for
				// our purposes here. TaskExecutor is more clearly designed for it.
				return beanFactory.getBean(TaskExecutor.class);
			}
			catch (NoUniqueBeanDefinitionException ex) {
				try {
					return beanFactory.getBean(DEFAULT_TASK_EXECUTOR_BEAN_NAME, Executor.class);
				}
				catch (NoSuchBeanDefinitionException ex2) {
					if (logger.isInfoEnabled()) {
						logger.info("More than one TaskExecutor bean found within the context, and none is named " +
								"'taskExecutor'. Mark one of them as primary or name it 'taskExecutor' (possibly " +
								"as an alias) in order to use it for async processing: " + ex.getBeanNamesFound());
					}
				}
			}
			catch (NoSuchBeanDefinitionException ex) {
				logger.debug("Could not find default TaskExecutor bean", ex);
				// Giving up -> either using local default executor or none at all...
				logger.info("No TaskExecutor bean found for async processing");
			}
		}
		return null;
	}


	/**
	 * Delegate for actually executing the given task with the chosen executor.
	 * <p>
	 * 代表实际执行给定的任务与选定的执行者
	 * 
	 * 
	 * @param task the task to execute
	 * @param executor the chosen executor
	 * @param returnType the declared return type (potentially a {@link Future} variant)
	 * @return the execution result (potentially a corresponding {@link Future} handle)
	 */
	protected Object doSubmit(Callable<Object> task, AsyncTaskExecutor executor, Class<?> returnType) {
		if (completableFuturePresent) {
			Future<Object> result = CompletableFutureDelegate.processCompletableFuture(returnType, task, executor);
			if (result != null) {
				return result;
			}
		}
		if (ListenableFuture.class.isAssignableFrom(returnType)) {
			return ((AsyncListenableTaskExecutor) executor).submitListenable(task);
		}
		else if (Future.class.isAssignableFrom(returnType)) {
			return executor.submit(task);
		}
		else {
			executor.submit(task);
			return null;
		}
	}

	/**
	 * Handles a fatal error thrown while asynchronously invoking the specified
	 * {@link Method}.
	 * <p>If the return type of the method is a {@link Future} object, the original
	 * exception can be propagated by just throwing it at the higher level. However,
	 * for all other cases, the exception will not be transmitted back to the client.
	 * In that later case, the current {@link AsyncUncaughtExceptionHandler} will be
	 * used to manage such exception.
	 * <p>
	 *  处理在异步调用指定的{@link方法}时抛出的致命错误如果方法的返回类型是{@link Future}对象,则原始异常可以通过将其抛出在较高级别来传播。但是,对于所有其他情况,异常将不会传回客户端。
	 * 在后一种情况下,当前的{@link AsyncUncaughtExceptionHandler}将用于管理此类异常。
	 * 
	 * 
	 * @param ex the exception to handle
	 * @param method the method that was invoked
	 * @param params the parameters used to invoke the method
	 */
	protected void handleError(Throwable ex, Method method, Object... params) throws Exception {
		if (Future.class.isAssignableFrom(method.getReturnType())) {
			ReflectionUtils.rethrowException(ex);
		}
		else {
			// Could not transmit the exception to the caller with default executor
			try {
				this.exceptionHandler.handleUncaughtException(ex, method, params);
			}
			catch (Throwable ex2) {
				logger.error("Exception handler for async method '" + method.toGenericString() +
						"' threw unexpected exception itself", ex2);
			}
		}
	}


	/**
	 * Inner class to avoid a hard dependency on Java 8.
	 * <p>
	 */
	@UsesJava8
	private static class CompletableFutureDelegate {

		public static <T> Future<T> processCompletableFuture(Class<?> returnType, final Callable<T> task, Executor executor) {
			if (!CompletableFuture.class.isAssignableFrom(returnType)) {
				return null;
			}
			return CompletableFuture.supplyAsync(new Supplier<T>() {
				@Override
				public T get() {
					try {
						return task.call();
					}
					catch (Throwable ex) {
						throw new CompletionException(ex);
					}
				}
			}, executor);
		}
	}

}
