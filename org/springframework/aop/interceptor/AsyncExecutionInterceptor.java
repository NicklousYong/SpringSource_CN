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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.Ordered;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.util.ClassUtils;

/**
 * AOP Alliance {@code MethodInterceptor} that processes method invocations
 * asynchronously, using a given {@link org.springframework.core.task.AsyncTaskExecutor}.
 * Typically used with the {@link org.springframework.scheduling.annotation.Async} annotation.
 *
 * <p>In terms of target method signatures, any parameter types are supported.
 * However, the return type is constrained to either {@code void} or
 * {@code java.util.concurrent.Future}. In the latter case, the Future handle
 * returned from the proxy will be an actual asynchronous Future that can be used
 * to track the result of the asynchronous method execution. However, since the
 * target method needs to implement the same signature, it will have to return
 * a temporary Future handle that just passes the return value through
 * (like Spring's {@link org.springframework.scheduling.annotation.AsyncResult}
 * or EJB 3.1's {@code javax.ejb.AsyncResult}).
 *
 * <p>When the return type is {@code java.util.concurrent.Future}, any exception thrown
 * during the execution can be accessed and managed by the caller. With {@code void}
 * return type however, such exceptions cannot be transmitted back. In that case an
 * {@link AsyncUncaughtExceptionHandler} can be registered to process such exceptions.
 *
 * <p>As of Spring 3.1.2 the {@code AnnotationAsyncExecutionInterceptor} subclass is
 * preferred for use due to its support for executor qualification in conjunction with
 * Spring's {@code @Async} annotation.
 *
 * <p>
 * 使用给定的{@link orgspringframeworkcoretaskAsyncTaskExecutor}异步处理方法调用的AOP联盟{@code MethodInterceptor}通常与{@link orgspringframeworkschedulingannotationAsync}
 * 注释一起使用。
 * 
 * <p>在目标方法签名方面,支持任何参数类型但是,返回类型被限制为{@code void}或{@code javautilconcurrentFuture}。
 * 在后一种情况下,从代理返回的Future句柄将是实际异步可以用来跟踪异步方法执行结果的未来然而,由于目标方法需要实现相同的签名,所以它必须返回一个临时的Future句柄,它只是传递返回值(如Spring
 * 的{@链接orgspringframeworkschedulingannotationAsyncResult}或EJB 31的{@code javaxejbAsyncResult})。
 * <p>在目标方法签名方面,支持任何参数类型但是,返回类型被限制为{@code void}或{@code javautilconcurrentFuture}。
 * 
 * <p>当返回类型为{@code javautilconcurrentFuture}时,执行期间抛出的任何异常都可以被调用者访问和管理。
 * 但是,{@code void}返回类型,这种异常不能被传回在这种情况下,{@链接AsyncUncaughtExceptionHandler}可以注册来处理这种异常。
 * 
 *  <p>自Spring 312起,由于支持执行者资格,结合Spring的{@code @Async}注释,{@code AnnotationAsyncExecutionInterceptor}子类是首选
 * 的。
 * 
 * 
 * @author Juergen Hoeller
 * @author Chris Beams
 * @author Stephane Nicoll
 * @since 3.0
 * @see org.springframework.scheduling.annotation.Async
 * @see org.springframework.scheduling.annotation.AsyncAnnotationAdvisor
 * @see org.springframework.scheduling.annotation.AnnotationAsyncExecutionInterceptor
 */
public class AsyncExecutionInterceptor extends AsyncExecutionAspectSupport implements MethodInterceptor, Ordered {

	/**
	 * Create a new instance with a default {@link AsyncUncaughtExceptionHandler}.
	 * <p>
	 *  使用默认的{@link AsyncUncaughtExceptionHandler}创建一个新的实例
	 * 
	 * 
	 * @param defaultExecutor the {@link Executor} (typically a Spring {@link AsyncTaskExecutor}
	 * or {@link java.util.concurrent.ExecutorService}) to delegate to;
	 * as of 4.2.6, a local executor for this interceptor will be built otherwise
	 */
	public AsyncExecutionInterceptor(Executor defaultExecutor) {
		super(defaultExecutor);
	}

	/**
	 * Create a new {@code AsyncExecutionInterceptor}.
	 * <p>
	 *  创建一个新的{@code AsyncExecutionInterceptor}
	 * 
	 * 
	 * @param defaultExecutor the {@link Executor} (typically a Spring {@link AsyncTaskExecutor}
	 * or {@link java.util.concurrent.ExecutorService}) to delegate to;
	 * as of 4.2.6, a local executor for this interceptor will be built otherwise
	 * @param exceptionHandler the {@link AsyncUncaughtExceptionHandler} to use
	 */
	public AsyncExecutionInterceptor(Executor defaultExecutor, AsyncUncaughtExceptionHandler exceptionHandler) {
		super(defaultExecutor, exceptionHandler);
	}


	/**
	 * Intercept the given method invocation, submit the actual calling of the method to
	 * the correct task executor and return immediately to the caller.
	 * <p>
	 * 拦截给定的方法调用,将方法的实际调用提交给正确的任务执行程序,并立即返回给调用者
	 * 
	 * 
	 * @param invocation the method to intercept and make asynchronous
	 * @return {@link Future} if the original method returns {@code Future}; {@code null}
	 * otherwise.
	 */
	@Override
	public Object invoke(final MethodInvocation invocation) throws Throwable {
		Class<?> targetClass = (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);
		Method specificMethod = ClassUtils.getMostSpecificMethod(invocation.getMethod(), targetClass);
		final Method userDeclaredMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);

		AsyncTaskExecutor executor = determineAsyncExecutor(userDeclaredMethod);
		if (executor == null) {
			throw new IllegalStateException(
					"No executor specified and no default executor set on AsyncExecutionInterceptor either");
		}

		Callable<Object> task = new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				try {
					Object result = invocation.proceed();
					if (result instanceof Future) {
						return ((Future<?>) result).get();
					}
				}
				catch (ExecutionException ex) {
					handleError(ex.getCause(), userDeclaredMethod, invocation.getArguments());
				}
				catch (Throwable ex) {
					handleError(ex, userDeclaredMethod, invocation.getArguments());
				}
				return null;
			}
		};

		return doSubmit(task, executor, invocation.getMethod().getReturnType());
	}

	/**
	 * This implementation is a no-op for compatibility in Spring 3.1.2.
	 * Subclasses may override to provide support for extracting qualifier information,
	 * e.g. via an annotation on the given method.
	 * <p>
	 *  在Spring 312中,此实现是兼容性的一个操作子类可以覆盖以提供对提取限定符信息的支持,例如通过给定方法的注释
	 * 
	 * 
	 * @return always {@code null}
	 * @since 3.1.2
	 * @see #determineAsyncExecutor(Method)
	 */
	@Override
	protected String getExecutorQualifier(Method method) {
		return null;
	}

	/**
	 * This implementation searches for a unique {@link org.springframework.core.task.TaskExecutor}
	 * bean in the context, or for an {@link Executor} bean named "taskExecutor" otherwise.
	 * If neither of the two is resolvable (e.g. if no {@code BeanFactory} was configured at all),
	 * this implementation falls back to a newly created {@link SimpleAsyncTaskExecutor} instance
	 * for local use if no default could be found.
	 * <p>
	 *  此实现在上下文中搜索唯一的{@link orgspringframeworkcoretaskTaskExecutor} bean,或者为名为"taskExecutor"的{@link Executor}
	 *  bean进行搜索否则两者都不可解析(例如,如果没有配置{@code BeanFactory} ),此实现将返回到新创建的{@link SimpleAsyncTaskExecutor}实例,以便本地使用
	 * 
	 * @see #DEFAULT_TASK_EXECUTOR_BEAN_NAME
	 */
	@Override
	protected Executor getDefaultExecutor(BeanFactory beanFactory) {
		Executor defaultExecutor = super.getDefaultExecutor(beanFactory);
		return (defaultExecutor != null ? defaultExecutor : new SimpleAsyncTaskExecutor());
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}

}
