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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that marks a method as a candidate for <i>asynchronous</i> execution.
 * Can also be used at the type level, in which case all of the type's methods are
 * considered as asynchronous.
 *
 * <p>In terms of target method signatures, any parameter types are supported.
 * However, the return type is constrained to either {@code void} or
 * {@link java.util.concurrent.Future}. In the latter case, the {@code Future} handle
 * returned from the proxy will be an actual asynchronous {@code Future} that can be used
 * to track the result of the asynchronous method execution. However, since the
 * target method needs to implement the same signature, it will have to return
 * a temporary {@code Future} handle that just passes the return value through: e.g.
 * Spring's {@link AsyncResult} or EJB 3.1's {@link javax.ejb.AsyncResult}.
 *
 * <p>
 *  将方法标记为<i>异步</i>执行的候选项的注释也可以在类型级别使用,在这种情况下,所有类型的方法都被视为异步
 * 
 * <p>在目标方法签名方面,支持任何参数类型但是,返回类型被限制为{@code void}或{@link javautilconcurrentFuture}。
 * 在后一种情况下,{@code Future}句柄从代理将是一个实际的异步{@code Future},可以用于跟踪异步方法执行的结果但是,由于目标方法需要实现相同的签名,所以必须返回一个临时的{@code Future}
 * 句柄通过以下方式传递返回值：例如Spring的{@link AsyncResult}或EJB 31的{@link javaxejbAsyncResult}。
 * <p>在目标方法签名方面,支持任何参数类型但是,返回类型被限制为{@code void}或{@link javautilconcurrentFuture}。
 * 
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 3.0
 * @see AnnotationAsyncExecutionInterceptor
 * @see AsyncAnnotationAdvisor
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Async {

	/**
	 * A qualifier value for the specified asynchronous operation(s).
	 * <p>May be used to determine the target executor to be used when executing this
	 * method, matching the qualifier value (or the bean name) of a specific
	 * {@link java.util.concurrent.Executor Executor} or
	 * {@link org.springframework.core.task.TaskExecutor TaskExecutor}
	 * bean definition.
	 * <p>When specified on a class level {@code @Async} annotation, indicates that the
	 * given executor should be used for all methods within the class. Method level use
	 * of {@code Async#value} always overrides any value set at the class level.
	 * <p>
	 * 
	 * 
	 * @since 3.1.2
	 */
	String value() default "";

}
