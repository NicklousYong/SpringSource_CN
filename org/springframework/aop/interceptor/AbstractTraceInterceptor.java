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

import java.io.Serializable;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.aop.support.AopUtils;

/**
 * Base {@code MethodInterceptor} implementation for tracing.
 *
 * <p>By default, log messages are written to the log for the interceptor class,
 * not the class which is being intercepted. Setting the {@code useDynamicLogger}
 * bean property to {@code true} causes all log messages to be written to
 * the {@code Log} for the target class being intercepted.
 *
 * <p>Subclasses must implement the {@code invokeUnderTrace} method, which
 * is invoked by this class ONLY when a particular invocation SHOULD be traced.
 * Subclasses should write to the {@code Log} instance provided.
 *
 * <p>
 *  Base {@code MethodInterceptor}实现用于跟踪
 * 
 * <p>默认情况下,日志消息将写入拦截器类的日志,而不是被拦截的类将{@code useDynamicLogger} bean属性设置为{@code true}将导致所有日志消息写入{ @code日志}被
 * 截获的目标类。
 * 
 *  <p>子类必须实现{@code invokeUnderTrace}方法,该方法仅在需要跟踪特定调用时由该类调用子类应写入提供的{@code Log}实例
 * 
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 1.2
 * @see #setUseDynamicLogger
 * @see #invokeUnderTrace(org.aopalliance.intercept.MethodInvocation, org.apache.commons.logging.Log)
 */
@SuppressWarnings("serial")
public abstract class AbstractTraceInterceptor implements MethodInterceptor, Serializable {

	/**
	 * The default {@code Log} instance used to write trace messages.
	 * This instance is mapped to the implementing {@code Class}.
	 * <p>
	 *  用于写入跟踪消息的默认{@code Log}实例此实例映射到实现的{@code Class}
	 * 
	 */
	protected transient Log defaultLogger = LogFactory.getLog(getClass());

	/**
	 * Indicates whether or not proxy class names should be hidden when using dynamic loggers.
	 * <p>
	 *  指示在使用动态记录器时是否隐藏代理类名称
	 * 
	 * 
	 * @see #setUseDynamicLogger
	 */
	private boolean hideProxyClassNames = false;


	/**
	 * Set whether to use a dynamic logger or a static logger.
	 * Default is a static logger for this trace interceptor.
	 * <p>Used to determine which {@code Log} instance should be used to write
	 * log messages for a particular method invocation: a dynamic one for the
	 * {@code Class} getting called, or a static one for the {@code Class}
	 * of the trace interceptor.
	 * <p><b>NOTE:</b> Specify either this property or "loggerName", not both.
	 * <p>
	 * 设置是使用动态记录器还是静态记录器Default是此跟踪拦截器的静态记录器<p>用于确定哪个{@code Log}实例应用于为特定方法调用写入日志消息：一个动态记录器{@code Class}被调用,或
	 * 者是一个静态的跟踪拦截器的{@code Class} <p> <b>注意：</b>指定此属性或"loggerName",而不是两者。
	 * 
	 * 
	 * @see #getLoggerForInvocation(org.aopalliance.intercept.MethodInvocation)
	 */
	public void setUseDynamicLogger(boolean useDynamicLogger) {
		// Release default logger if it is not being used.
		this.defaultLogger = (useDynamicLogger ? null : LogFactory.getLog(getClass()));
	}

	/**
	 * Set the name of the logger to use. The name will be passed to the
	 * underlying logger implementation through Commons Logging, getting
	 * interpreted as log category according to the logger's configuration.
	 * <p>This can be specified to not log into the category of a class
	 * (whether this interceptor's class or the class getting called)
	 * but rather into a specific named category.
	 * <p><b>NOTE:</b> Specify either this property or "useDynamicLogger", not both.
	 * <p>
	 * 设置要使用的记录器的名称该名称将通过Commons Logging传递到底层日志记录器实现,根据记录器的配置将其解释为日志类别<p>可以指定不登录到类的类别(无论是否这个拦截器的类或类被调用),而是进入
	 * 特定的命名类别<p> <b>注意：</b>指定此属性或"useDynamicLogger",而不是两者。
	 * 
	 * 
	 * @see org.apache.commons.logging.LogFactory#getLog(String)
	 * @see org.apache.log4j.Logger#getLogger(String)
	 * @see java.util.logging.Logger#getLogger(String)
	 */
	public void setLoggerName(String loggerName) {
		this.defaultLogger = LogFactory.getLog(loggerName);
	}

	/**
	 * Set to "true" to have {@link #setUseDynamicLogger dynamic loggers} hide
	 * proxy class names wherever possible. Default is "false".
	 * <p>
	 *  设置为"true"使{@link #setUseDynamicLogger动态记录器}尽可能隐藏代理类名称为"false"
	 * 
	 */
	public void setHideProxyClassNames(boolean hideProxyClassNames) {
		this.hideProxyClassNames = hideProxyClassNames;
	}


	/**
	 * Determines whether or not logging is enabled for the particular {@code MethodInvocation}.
	 * If not, the method invocation proceeds as normal, otherwise the method invocation is passed
	 * to the {@code invokeUnderTrace} method for handling.
	 * <p>
	 * 确定是否为特定的{@code MethodInvocation}启用日志记录如果不是,方法调用正常进行,否则方法调用将传递给{@code invokeUnderTrace}方法来处理
	 * 
	 * 
	 * @see #invokeUnderTrace(org.aopalliance.intercept.MethodInvocation, org.apache.commons.logging.Log)
	 */
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Log logger = getLoggerForInvocation(invocation);
		if (isInterceptorEnabled(invocation, logger)) {
			return invokeUnderTrace(invocation, logger);
		}
		else {
			return invocation.proceed();
		}
	}

	/**
	 * Return the appropriate {@code Log} instance to use for the given
	 * {@code MethodInvocation}. If the {@code useDynamicLogger} flag
	 * is set, the {@code Log} instance will be for the target class of the
	 * {@code MethodInvocation}, otherwise the {@code Log} will be the
	 * default static logger.
	 * <p>
	 *  返回相应的{@code Log}实例以用于给定的{@code MethodInvocation}如果设置了{@code useDynamicLogger}标志,则{@code Log}实例将用于{@code MethodInvocation}
	 * 的目标类,否则,{@code Log}将是默认静态记录器。
	 * 
	 * 
	 * @param invocation the {@code MethodInvocation} being traced
	 * @return the {@code Log} instance to use
	 * @see #setUseDynamicLogger
	 */
	protected Log getLoggerForInvocation(MethodInvocation invocation) {
		if (this.defaultLogger != null) {
			return this.defaultLogger;
		}
		else {
			Object target = invocation.getThis();
			return LogFactory.getLog(getClassForLogging(target));
		}
	}

	/**
	 * Determine the class to use for logging purposes.
	 * <p>
	 *  确定用于记录目的的类
	 * 
	 * 
	 * @param target the target object to introspect
	 * @return the target class for the given object
	 * @see #setHideProxyClassNames
	 */
	protected Class<?> getClassForLogging(Object target) {
		return (this.hideProxyClassNames ? AopUtils.getTargetClass(target) : target.getClass());
	}

	/**
	 * Determine whether the interceptor should kick in, that is,
	 * whether the {@code invokeUnderTrace} method should be called.
	 * <p>Default behavior is to check whether the given {@code Log}
	 * instance is enabled. Subclasses can override this to apply the
	 * interceptor in other cases as well.
	 * <p>
	 * 确定拦截器是否应该启动,也就是说是否应该调用{@code invokeUnderTrace}方法。
	 * <p>默认行为是检查给定的{@code Log}实例是否被启用子类可以覆盖它以将拦截器应用于其他情况也是如此。
	 * 
	 * 
	 * @param invocation the {@code MethodInvocation} being traced
	 * @param logger the {@code Log} instance to check
	 * @see #invokeUnderTrace
	 * @see #isLogEnabled
	 */
	protected boolean isInterceptorEnabled(MethodInvocation invocation, Log logger) {
		return isLogEnabled(logger);
	}

	/**
	 * Determine whether the given {@link Log} instance is enabled.
	 * <p>Default is {@code true} when the "trace" level is enabled.
	 * Subclasses can override this to change the level under which 'tracing' occurs.
	 * <p>
	 *  确定给定的{@link Log}实例是否启用<p>当启用"跟踪"级别时,默认为{@code true}子类可以覆盖此值以更改"跟踪"发生时的级别
	 * 
	 * 
	 * @param logger the {@code Log} instance to check
	 */
	protected boolean isLogEnabled(Log logger) {
		return logger.isTraceEnabled();
	}


	/**
	 * Subclasses must override this method to perform any tracing around the
	 * supplied {@code MethodInvocation}. Subclasses are responsible for
	 * ensuring that the {@code MethodInvocation} actually executes by
	 * calling {@code MethodInvocation.proceed()}.
	 * <p>By default, the passed-in {@code Log} instance will have log level
	 * "trace" enabled. Subclasses do not have to check for this again, unless
	 * they overwrite the {@code isInterceptorEnabled} method to modify
	 * the default behavior.
	 * <p>
	 * 子类必须覆盖此方法以执行围绕所提供的{@code MethodInvocation}的任何跟踪。
	 * 子类负责确保{@code MethodInvocation}通过调用{@code MethodInvocationproceed()}实际执行<p>默认情况下,传入{@code Log}实例将启用日志级
	 * 别"trace"子类不需要再次检查,除非它们覆盖{@code isInterceptorEnabled}方法来修改默认行为。
	 * 
	 * @param logger the {@code Log} to write trace messages to
	 * @return the result of the call to {@code MethodInvocation.proceed()}
	 * @throws Throwable if the call to {@code MethodInvocation.proceed()}
	 * encountered any errors
	 * @see #isInterceptorEnabled
	 * @see #isLogEnabled
	 */
	protected abstract Object invokeUnderTrace(MethodInvocation invocation, Log logger) throws Throwable;

}
