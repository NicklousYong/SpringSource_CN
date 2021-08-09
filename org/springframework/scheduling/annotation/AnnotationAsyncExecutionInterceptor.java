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

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

import org.springframework.aop.interceptor.AsyncExecutionInterceptor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.core.annotation.AnnotatedElementUtils;

/**
 * Specialization of {@link AsyncExecutionInterceptor} that delegates method execution to
 * an {@code Executor} based on the {@link Async} annotation. Specifically designed to
 * support use of {@link Async#value()} executor qualification mechanism introduced in
 * Spring 3.1.2. Supports detecting qualifier metadata via {@code @Async} at the method or
 * declaring class level. See {@link #getExecutorQualifier(Method)} for details.
 *
 * <p>
 * 基于{@link Async}注释将方法执行委托给{@code Executor}的{@link AsyncExecutionInterceptor}的专业化专门用于支持使用Spring 312中引入的
 * {@link Async#value()}执行器限定机制支持通过{@code @Async}在方法中检测限定符元数据或声明类级别有关详细信息,请参阅{@link #getExecutorQualifier(Method))。
 * 
 * 
 * @author Chris Beams
 * @author Stephane Nicoll
 * @since 3.1.2
 * @see org.springframework.scheduling.annotation.Async
 * @see org.springframework.scheduling.annotation.AsyncAnnotationAdvisor
 */
public class AnnotationAsyncExecutionInterceptor extends AsyncExecutionInterceptor {

	/**
	 * Create a new {@code AnnotationAsyncExecutionInterceptor} with the given executor
	 * and a simple {@link AsyncUncaughtExceptionHandler}.
	 * <p>
	 *  使用给定的执行器和简单的{@link AsyncUncaughtExceptionHandler}创建一个新的{@code AnnotationAsyncExecutionInterceptor}
	 * 
	 * 
	 * @param defaultExecutor the executor to be used by default if no more specific
	 * executor has been qualified at the method level using {@link Async#value()};
	 * as of 4.2.6, a local executor for this interceptor will be built otherwise
	 */
	public AnnotationAsyncExecutionInterceptor(Executor defaultExecutor) {
		super(defaultExecutor);
	}

	/**
	 * Create a new {@code AnnotationAsyncExecutionInterceptor} with the given executor.
	 * <p>
	 *  与给定的执行器创建一个新的{@code AnnotationAsyncExecutionInterceptor}
	 * 
	 * 
	 * @param defaultExecutor the executor to be used by default if no more specific
	 * executor has been qualified at the method level using {@link Async#value()};
	 * as of 4.2.6, a local executor for this interceptor will be built otherwise
	 * @param exceptionHandler the {@link AsyncUncaughtExceptionHandler} to use to
	 * handle exceptions thrown by asynchronous method executions with {@code void}
	 * return type
	 */
	public AnnotationAsyncExecutionInterceptor(Executor defaultExecutor, AsyncUncaughtExceptionHandler exceptionHandler) {
		super(defaultExecutor, exceptionHandler);
	}


	/**
	 * Return the qualifier or bean name of the executor to be used when executing the
	 * given method, specified via {@link Async#value} at the method or declaring
	 * class level. If {@code @Async} is specified at both the method and class level, the
	 * method's {@code #value} takes precedence (even if empty string, indicating that
	 * the default executor should be used preferentially).
	 * <p>
	 * 返回在执行给定方法时使用的限定符或bean名称,该方法通过方法中的{@link Async#value}指定或声明类级别如果在方法和类级别指定了{@code @Async} ,方法的{@code #value}
	 * 优先(即使是空字符串,表示应优先使用默认执行程序)。
	 * 
	 * @param method the method to inspect for executor qualifier metadata
	 * @return the qualifier if specified, otherwise empty string indicating that the
	 * {@linkplain #setExecutor(Executor) default executor} should be used
	 * @see #determineAsyncExecutor(Method)
	 */
	@Override
	protected String getExecutorQualifier(Method method) {
		// Maintainer's note: changes made here should also be made in
		// AnnotationAsyncExecutionAspect#getExecutorQualifier
		Async async = AnnotatedElementUtils.findMergedAnnotation(method, Async.class);
		if (async == null) {
			async = AnnotatedElementUtils.findMergedAnnotation(method.getDeclaringClass(), Async.class);
		}
		return (async != null ? async.value() : null);
	}

}
