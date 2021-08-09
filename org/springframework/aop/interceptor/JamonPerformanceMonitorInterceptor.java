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

package org.springframework.aop.interceptor;

import com.jamonapi.MonKey;
import com.jamonapi.MonKeyImp;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import com.jamonapi.utils.Misc;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;

/**
 * Performance monitor interceptor that uses <b>JAMon</b> library to perform the
 * performance measurement on the intercepted method and output the stats.
 * In addition, it tracks/counts exceptions thrown by the intercepted method.
 * The stack traces can be viewed in the JAMon web application.
 *
 * <p>This code is inspired by Thierry Templier's blog.
 *
 * <p>
 * 性能监视拦截器,使用<b> JAMon </b>库来执行截取的方法的性能测量并输出统计信息另外,它跟踪/计数被拦截方法抛出的异常堆栈跟踪可以在JAMon Web应用
 * 
 *  <p>这段代码灵感来自于Thierry Templier的博客
 * 
 * 
 * @author Dmitriy Kopylenko
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Steve Souza
 * @since 1.1.3
 * @see com.jamonapi.MonitorFactory
 * @see PerformanceMonitorInterceptor
 */
@SuppressWarnings("serial")
public class JamonPerformanceMonitorInterceptor extends AbstractMonitoringInterceptor {

	private boolean trackAllInvocations = false;


	/**
	 * Create a new JamonPerformanceMonitorInterceptor with a static logger.
	 * <p>
	 *  使用静态记录器创建一个新的JamonPerformanceMonitorInterceptor
	 * 
	 */
	public JamonPerformanceMonitorInterceptor() {
	}

	/**
	 * Create a new JamonPerformanceMonitorInterceptor with a dynamic or static logger,
	 * according to the given flag.
	 * <p>
	 *  根据给定的标志,使用动态或静态记录器创建一个新的JamonPerformanceMonitorInterceptor
	 * 
	 * 
	 * @param useDynamicLogger whether to use a dynamic logger or a static logger
	 * @see #setUseDynamicLogger
	 */
	public JamonPerformanceMonitorInterceptor(boolean useDynamicLogger) {
		setUseDynamicLogger(useDynamicLogger);
	}

	/**
	 * Create a new JamonPerformanceMonitorInterceptor with a dynamic or static logger,
	 * according to the given flag.
	 * <p>
	 *  根据给定的标志,使用动态或静态记录器创建一个新的JamonPerformanceMonitorInterceptor
	 * 
	 * 
	 * @param useDynamicLogger whether to use a dynamic logger or a static logger
	 * @param trackAllInvocations whether to track all invocations that go through
	 * this interceptor, or just invocations with trace logging enabled
	 * @see #setUseDynamicLogger
	 */
	public JamonPerformanceMonitorInterceptor(boolean useDynamicLogger, boolean trackAllInvocations) {
		setUseDynamicLogger(useDynamicLogger);
		setTrackAllInvocations(trackAllInvocations);
	}


	/**
	 * Set whether to track all invocations that go through this interceptor,
	 * or just invocations with trace logging enabled.
	 * <p>Default is "false": Only invocations with trace logging enabled will
	 * be monitored. Specify "true" to let JAMon track all invocations,
	 * gathering statistics even when trace logging is disabled.
	 * <p>
	 * 设置是否跟踪通过此拦截器的所有调用,或仅启用跟踪记录的调用<p>默认为"false"：仅监视启用跟踪记录的调用将指定"true"以让JAMon跟踪所有调用,收集统计信息即使禁用跟踪记录
	 * 
	 */
	public void setTrackAllInvocations(boolean trackAllInvocations) {
		this.trackAllInvocations = trackAllInvocations;
	}


	/**
	 * Always applies the interceptor if the "trackAllInvocations" flag has been set;
	 * else just kicks in if the log is enabled.
	 * <p>
	 *  如果设置了"trackAllInvocations"标志,则始终应用拦截器;否则,如果日志被启用,则启动
	 * 
	 * 
	 * @see #setTrackAllInvocations
	 * @see #isLogEnabled
	 */
	@Override
	protected boolean isInterceptorEnabled(MethodInvocation invocation, Log logger) {
		return (this.trackAllInvocations || isLogEnabled(logger));
	}

	/**
	 * Wraps the invocation with a JAMon Monitor and writes the current
	 * performance statistics to the log (if enabled).
	 * <p>
	 *  使用JAMon Monitor封装调用,并将当前性能统计信息写入日志(如果已启用)
	 * 
	 * 
	 * @see com.jamonapi.MonitorFactory#start
	 * @see com.jamonapi.Monitor#stop
	 */
	@Override
	protected Object invokeUnderTrace(MethodInvocation invocation, Log logger) throws Throwable {
		String name = createInvocationTraceName(invocation);
		MonKey key = new MonKeyImp(name, name, "ms.");

		Monitor monitor = MonitorFactory.start(key);
		try {
			return invocation.proceed();
		}
		catch (Throwable ex) {
			trackException(key, ex);
			throw ex;
		}
		finally {
			monitor.stop();
			if (!this.trackAllInvocations || isLogEnabled(logger)) {
				logger.trace("JAMon performance statistics for method [" + name + "]:\n" + monitor);
			}
		}
	}

	/**
	 * Count the thrown exception and put the stack trace in the details portion of the key.
	 * This will allow the stack trace to be viewed in the JAMon web application.
	 * <p>
	 *  计算抛出的异常并将堆栈跟踪放在密钥的详细信息部分中这将允许在JAMon Web应用程序中查看堆栈跟踪
	 */
	protected void trackException(MonKey key, Throwable ex) {
		String stackTrace = "stackTrace=" + Misc.getExceptionTrace(ex);
		key.setDetails(stackTrace);

		// Specific exception counter. Example: java.lang.RuntimeException
		MonitorFactory.add(new MonKeyImp(ex.getClass().getName(), stackTrace, "Exception"), 1);

		// General exception counter which is a total for all exceptions thrown
		MonitorFactory.add(new MonKeyImp(MonitorFactory.EXCEPTIONS_LABEL, stackTrace, "Exception"), 1);
	}

}
