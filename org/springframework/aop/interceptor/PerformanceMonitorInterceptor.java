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

import org.springframework.util.StopWatch;

/**
 * Simple AOP Alliance {@code MethodInterceptor} for performance monitoring.
 * This interceptor has no effect on the intercepted method call.
 *
 * <p>Uses a {@code StopWatch} for the actual performance measuring.
 *
 * <p>
 *  简单的AOP联盟{@code MethodInterceptor}用于性能监视此拦截器对被截取的方法调用没有影响
 * 
 *  <p>使用{@code StopWatch}进行实际的性能测量
 * 
 * 
 * @author Rod Johnson
 * @author Dmitriy Kopylenko
 * @author Rob Harrop
 * @see org.springframework.util.StopWatch
 * @see JamonPerformanceMonitorInterceptor
 */
@SuppressWarnings("serial")
public class PerformanceMonitorInterceptor extends AbstractMonitoringInterceptor {

	/**
	 * Create a new PerformanceMonitorInterceptor with a static logger.
	 * <p>
	 * 使用静态记录器创建一个新的PerformanceMonitorInterceptor
	 * 
	 */
	public PerformanceMonitorInterceptor() {
	}

	/**
	 * Create a new PerformanceMonitorInterceptor with a dynamic or static logger,
	 * according to the given flag.
	 * <p>
	 *  根据给定的标志,使用动态或静态记录器创建一个新的PerformanceMonitorInterceptor
	 * 
	 * @param useDynamicLogger whether to use a dynamic logger or a static logger
	 * @see #setUseDynamicLogger
	 */
	public PerformanceMonitorInterceptor(boolean useDynamicLogger) {
		setUseDynamicLogger(useDynamicLogger);
	}


	@Override
	protected Object invokeUnderTrace(MethodInvocation invocation, Log logger) throws Throwable {
		String name = createInvocationTraceName(invocation);
		StopWatch stopWatch = new StopWatch(name);
		stopWatch.start(name);
		try {
			return invocation.proceed();
		}
		finally {
			stopWatch.stop();
			logger.trace(stopWatch.shortSummary());
		}
	}

}
