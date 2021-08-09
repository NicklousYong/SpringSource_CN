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

package org.springframework.aop.interceptor;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;

/**
 * Simple AOP Alliance {@code MethodInterceptor} that can be introduced
 * in a chain to display verbose trace information about intercepted method
 * invocations, with method entry and method exit info.
 *
 * <p>Consider using {@code CustomizableTraceInterceptor} for more
 * advanced needs.
 *
 * <p>
 *  简单的AOP联盟{@code MethodInterceptor},可以在链中引入以显示关于截取的方法调用的详细跟踪信息,方法条目和方法退出信息
 * 
 * 考虑使用{@code CustomizableTraceInterceptor}来获得更高级的需求
 * 
 * 
 * @author Dmitriy Kopylenko
 * @author Juergen Hoeller
 * @since 1.2
 * @see CustomizableTraceInterceptor
 */
@SuppressWarnings("serial")
public class SimpleTraceInterceptor extends AbstractTraceInterceptor {

	/**
	 * Create a new SimpleTraceInterceptor with a static logger.
	 * <p>
	 *  使用静态记录器创建一个新的SimpleTraceInterceptor
	 * 
	 */
	public SimpleTraceInterceptor() {
	}

	/**
	 * Create a new SimpleTraceInterceptor with dynamic or static logger,
	 * according to the given flag.
	 * <p>
	 *  根据给定的标志,使用动态或静态记录器创建一个新的SimpleTraceInterceptor
	 * 
	 * 
	 * @param useDynamicLogger whether to use a dynamic logger or a static logger
	 * @see #setUseDynamicLogger
	 */
	public SimpleTraceInterceptor(boolean useDynamicLogger) {
		setUseDynamicLogger(useDynamicLogger);
	}


	@Override
	protected Object invokeUnderTrace(MethodInvocation invocation, Log logger) throws Throwable {
		String invocationDescription = getInvocationDescription(invocation);
		logger.trace("Entering " + invocationDescription);
		try {
			Object rval = invocation.proceed();
			logger.trace("Exiting " + invocationDescription);
			return rval;
		}
		catch (Throwable ex) {
			logger.trace("Exception thrown in " + invocationDescription, ex);
			throw ex;
		}
	}

	/**
	 * Return a description for the given method invocation.
	 * <p>
	 *  返回给定方法调用的描述
	 * 
	 * @param invocation the invocation to describe
	 * @return the description
	 */
	protected String getInvocationDescription(MethodInvocation invocation) {
		return "method '" + invocation.getMethod().getName() + "' of class [" +
				invocation.getThis().getClass().getName() + "]";
	}

}
