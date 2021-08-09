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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Executor;

import org.aopalliance.aop.Advice;

import org.springframework.aop.Pointcut;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * Advisor that activates asynchronous method execution through the {@link Async}
 * annotation. This annotation can be used at the method and type level in
 * implementation classes as well as in service interfaces.
 *
 * <p>This advisor detects the EJB 3.1 {@code javax.ejb.Asynchronous}
 * annotation as well, treating it exactly like Spring's own {@code Async}.
 * Furthermore, a custom async annotation type may get specified through the
 * {@link #setAsyncAnnotationType "asyncAnnotationType"} property.
 *
 * <p>
 *  通过{@link Async}注释激活异步方法执行的顾问此注释可以在实现类中的方法和类型级别以及服务接口中使用
 * 
 * <p>该顾问也检测到EJB 31 {@code javaxejbAsynchronous}注释,完全符合Spring自己的{@code Async}。
 * 此外,可以通过{@link #setAsyncAnnotationType"asyncAnnotationType"}指定自定义异步注释类型。属性。
 * 
 * 
 * @author Juergen Hoeller
 * @since 3.0
 * @see org.springframework.dao.annotation.PersistenceExceptionTranslationAdvisor
 * @see org.springframework.stereotype.Repository
 * @see org.springframework.dao.DataAccessException
 * @see org.springframework.dao.support.PersistenceExceptionTranslator
 */
@SuppressWarnings("serial")
public class AsyncAnnotationAdvisor extends AbstractPointcutAdvisor implements BeanFactoryAware {

	private AsyncUncaughtExceptionHandler exceptionHandler;

	private Advice advice;

	private Pointcut pointcut;


	/**
	 * Create a new {@code AsyncAnnotationAdvisor} for bean-style configuration.
	 * <p>
	 *  为bean样式配置创建一个新的{@code AsyncAnnotationAdvisor}
	 * 
	 */
	public AsyncAnnotationAdvisor() {
		this(null, null);
	}

	/**
	 * Create a new {@code AsyncAnnotationAdvisor} for the given task executor.
	 * <p>
	 *  为给定的任务执行器创建一个新的{@code AsyncAnnotationAdvisor}
	 * 
	 * 
	 * @param executor the task executor to use for asynchronous methods
	 * (can be {@code null} to trigger default executor resolution)
	 * @param exceptionHandler the {@link AsyncUncaughtExceptionHandler} to use to
	 * handle unexpected exception thrown by asynchronous method executions
	 * @see AnnotationAsyncExecutionInterceptor#getDefaultExecutor(BeanFactory)
	 */
	@SuppressWarnings("unchecked")
	public AsyncAnnotationAdvisor(Executor executor, AsyncUncaughtExceptionHandler exceptionHandler) {
		Set<Class<? extends Annotation>> asyncAnnotationTypes = new LinkedHashSet<Class<? extends Annotation>>(2);
		asyncAnnotationTypes.add(Async.class);
		try {
			asyncAnnotationTypes.add((Class<? extends Annotation>)
					ClassUtils.forName("javax.ejb.Asynchronous", AsyncAnnotationAdvisor.class.getClassLoader()));
		}
		catch (ClassNotFoundException ex) {
			// If EJB 3.1 API not present, simply ignore.
		}
		if (exceptionHandler != null) {
			this.exceptionHandler = exceptionHandler;
		}
		else {
			this.exceptionHandler = new SimpleAsyncUncaughtExceptionHandler();
		}
		this.advice = buildAdvice(executor, this.exceptionHandler);
		this.pointcut = buildPointcut(asyncAnnotationTypes);
	}


	/**
	 * Specify the default task executor to use for asynchronous methods.
	 * <p>
	 *  指定用于异步方法的默认任务执行程序
	 * 
	 */
	public void setTaskExecutor(Executor executor) {
		this.advice = buildAdvice(executor, this.exceptionHandler);
	}

	/**
	 * Set the 'async' annotation type.
	 * <p>The default async annotation type is the {@link Async} annotation, as well
	 * as the EJB 3.1 {@code javax.ejb.Asynchronous} annotation (if present).
	 * <p>This setter property exists so that developers can provide their own
	 * (non-Spring-specific) annotation type to indicate that a method is to
	 * be executed asynchronously.
	 * <p>
	 * 设置"异步"注释类型<p>默认的异步注释类型是{@link Async}注释,以及EJB 31 {@code javaxejbAsynchronous}注释(如果存在)<p>此setter属性存在,以便
	 * 开发人员可以提供自己的(非Spring特定的)注释类型,以指示方法将被异步执行。
	 * 
	 * 
	 * @param asyncAnnotationType the desired annotation type
	 */
	public void setAsyncAnnotationType(Class<? extends Annotation> asyncAnnotationType) {
		Assert.notNull(asyncAnnotationType, "'asyncAnnotationType' must not be null");
		Set<Class<? extends Annotation>> asyncAnnotationTypes = new HashSet<Class<? extends Annotation>>();
		asyncAnnotationTypes.add(asyncAnnotationType);
		this.pointcut = buildPointcut(asyncAnnotationTypes);
	}

	/**
	 * Set the {@code BeanFactory} to be used when looking up executors by qualifier.
	 * <p>
	 *  设置{限制器查找执行程序时使用的{@code BeanFactory}
	 * 
	 */
	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		if (this.advice instanceof BeanFactoryAware) {
			((BeanFactoryAware) this.advice).setBeanFactory(beanFactory);
		}
	}


	@Override
	public Advice getAdvice() {
		return this.advice;
	}

	@Override
	public Pointcut getPointcut() {
		return this.pointcut;
	}


	protected Advice buildAdvice(Executor executor, AsyncUncaughtExceptionHandler exceptionHandler) {
		return new AnnotationAsyncExecutionInterceptor(executor, exceptionHandler);
	}

	/**
	 * Calculate a pointcut for the given async annotation types, if any.
	 * <p>
	 *  计算给定异步注释类型的切入点(如果有)
	 * 
	 * @param asyncAnnotationTypes the async annotation types to introspect
	 * @return the applicable Pointcut object, or {@code null} if none
	 */
	protected Pointcut buildPointcut(Set<Class<? extends Annotation>> asyncAnnotationTypes) {
		ComposablePointcut result = null;
		for (Class<? extends Annotation> asyncAnnotationType : asyncAnnotationTypes) {
			Pointcut cpc = new AnnotationMatchingPointcut(asyncAnnotationType, true);
			Pointcut mpc = AnnotationMatchingPointcut.forMethodAnnotation(asyncAnnotationType);
			if (result == null) {
				result = new ComposablePointcut(cpc).union(mpc);
			}
			else {
				result.union(cpc).union(mpc);
			}
		}
		return result;
	}

}
