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

package org.springframework.scheduling.annotation;

import java.lang.annotation.Annotation;
import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.aop.framework.autoproxy.AbstractBeanFactoryAwareAdvisingPostProcessor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.util.Assert;

/**
 * Bean post-processor that automatically applies asynchronous invocation
 * behavior to any bean that carries the {@link Async} annotation at class or
 * method-level by adding a corresponding {@link AsyncAnnotationAdvisor} to the
 * exposed proxy (either an existing AOP proxy or a newly generated proxy that
 * implements all of the target's interfaces).
 *
 * <p>The {@link TaskExecutor} responsible for the asynchronous execution may
 * be provided as well as the annotation type that indicates a method should be
 * invoked asynchronously. If no annotation type is specified, this post-
 * processor will detect both Spring's {@link Async @Async} annotation as well
 * as the EJB 3.1 {@code javax.ejb.Asynchronous} annotation.
 *
 * <p>For methods having a {@code void} return type, any exception thrown
 * during the asynchronous method invocation cannot be accessed by the
 * caller. An {@link AsyncUncaughtExceptionHandler} can be specified to handle
 * these cases.
 *
 * <p>Note: The underlying async advisor applies before existing advisors by default,
 * in order to switch to async execution as early as possible in the invocation chain.
 *
 * <p>
 * Bean后处理器通过将相应的{@link AsyncAnnotationAdvisor}添加到暴露的代理(现有的AOP代理或新的AOP代理)中,将异步调用行为自动应用于在类或方法级别携带{@link Async}
 * 注释的任何bean生成代理,实现所有目标的接口)。
 * 
 *  可以提供负责异步执行的{@link TaskExecutor}以及指示应该异步调用的方法的注释类型如果未指定注释类型,则此后处理器将检测到Spring的{@link异步@Async}注释以及EJB 3
 * 1 {@code javaxejbAsynchronous}注释。
 * 
 * <p>对于具有{@code void}返回类型的方法,异步方法调用期间抛出的任何异常都不能由调用者访问。
 * 可以指定{@link AsyncUncaughtExceptionHandler}来处理这些情况。
 * 
 *  注意：默认情况下,基础异步顾问应用于现有顾问之前,以便尽早在调用链中切换到异步执行
 * 
 * 
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 * @since 3.0
 * @see Async
 * @see AsyncAnnotationAdvisor
 * @see #setBeforeExistingAdvisors
 * @see ScheduledAnnotationBeanPostProcessor
 */
@SuppressWarnings("serial")
public class AsyncAnnotationBeanPostProcessor extends AbstractBeanFactoryAwareAdvisingPostProcessor {

	/**
	 * The default name of the {@link TaskExecutor} bean to pick up: "taskExecutor".
	 * <p>Note that the initial lookup happens by type; this is just the fallback
	 * in case of multiple executor beans found in the context.
	 * <p>
	 *  要取出的{@link TaskExecutor} bean的默认名称为"taskExecutor"<p>请注意,初始查找按类型发生;这只是在上下文中发现多个执行程序bean的情况下的后备
	 * 
	 * 
	 * @since 4.2
	 * @see AnnotationAsyncExecutionInterceptor#DEFAULT_TASK_EXECUTOR_BEAN_NAME
	 */
	public static final String DEFAULT_TASK_EXECUTOR_BEAN_NAME =
			AnnotationAsyncExecutionInterceptor.DEFAULT_TASK_EXECUTOR_BEAN_NAME;


	protected final Log logger = LogFactory.getLog(getClass());

	private Class<? extends Annotation> asyncAnnotationType;

	private Executor executor;

	private AsyncUncaughtExceptionHandler exceptionHandler;


	public AsyncAnnotationBeanPostProcessor() {
		setBeforeExistingAdvisors(true);
	}


	/**
	 * Set the 'async' annotation type to be detected at either class or method
	 * level. By default, both the {@link Async} annotation and the EJB 3.1
	 * {@code javax.ejb.Asynchronous} annotation will be detected.
	 * <p>This setter property exists so that developers can provide their own
	 * (non-Spring-specific) annotation type to indicate that a method (or all
	 * methods of a given class) should be invoked asynchronously.
	 * <p>
	 * 设置要在任何类或方法级别检测的"异步"注释类型默认情况下,{@link Async}注释和EJB 31 {@code javaxejbAsynchronous}注释将被检测<p>此setter属性存在,
	 * 以便开发人员可以提供自己的(非Spring特定的)注释类型,以指示异步地调用方法(或给定类的所有方法)。
	 * 
	 * 
	 * @param asyncAnnotationType the desired annotation type
	 */
	public void setAsyncAnnotationType(Class<? extends Annotation> asyncAnnotationType) {
		Assert.notNull(asyncAnnotationType, "'asyncAnnotationType' must not be null");
		this.asyncAnnotationType = asyncAnnotationType;
	}

	/**
	 * Set the {@link Executor} to use when invoking methods asynchronously.
	 * <p>If not specified, default executor resolution will apply: searching for a
	 * unique {@link TaskExecutor} bean in the context, or for an {@link Executor}
	 * bean named "taskExecutor" otherwise. If neither of the two is resolvable,
	 * a local default executor will be created within the interceptor.
	 * <p>
	 *  将{@link Executor}设置为异步调用方法时使用<p>如果未指定,则将应用默认执行程序解析：在上下文中搜索唯一的{@link TaskExecutor} bean,或者为名称为{@link Executor}
	 *  bean "taskExecutor"否则如果两者都不可解析,将在拦截器内创建一个本地默认执行程序。
	 * 
	 * 
	 * @see AsyncAnnotationAdvisor#AsyncAnnotationAdvisor(Executor, AsyncUncaughtExceptionHandler)
	 * @see AnnotationAsyncExecutionInterceptor#getDefaultExecutor(BeanFactory)
	 * @see #DEFAULT_TASK_EXECUTOR_BEAN_NAME
	 */
	public void setExecutor(Executor executor) {
		this.executor = executor;
	}

	/**
	 * Set the {@link AsyncUncaughtExceptionHandler} to use to handle uncaught
	 * exceptions thrown by asynchronous method executions.
	 * <p>
	 * 
	 * @since 4.1
	 */
	public void setExceptionHandler(AsyncUncaughtExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}


	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		super.setBeanFactory(beanFactory);

		AsyncAnnotationAdvisor advisor = new AsyncAnnotationAdvisor(this.executor, this.exceptionHandler);
		if (this.asyncAnnotationType != null) {
			advisor.setAsyncAnnotationType(this.asyncAnnotationType);
		}
		advisor.setBeanFactory(beanFactory);
		this.advisor = advisor;
	}

}
