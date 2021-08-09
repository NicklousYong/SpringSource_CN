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

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;

/**
 * Base class for monitoring interceptors, such as performance monitors.
 * Provides {@code prefix} and {@code suffix} properties
 * that help to classify/group performance monitoring results.
 *
 * <p>Subclasses should call the {@code createInvocationTraceName(MethodInvocation)}
 * method to create a name for the given trace that includes information about the
 * method invocation under trace along with the prefix and suffix added as appropriate.
 *
 * <p>
 *  用于监视拦截器的基类,如性能监视器提供有助于分类/组性能监视结果的{@code前缀}和{@code后缀}属性
 * 
 * <p>子类应调用{@code createInvocationTraceName(MethodInvocation)}方法为给定的跟踪创建一个名称,该名称包含有关跟踪下的方法调用的信息以及相应添加的前缀
 * 和后缀。
 * 
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 1.2.7
 * @see #setPrefix
 * @see #setSuffix
 * @see #createInvocationTraceName
 */
@SuppressWarnings("serial")
public abstract class AbstractMonitoringInterceptor extends AbstractTraceInterceptor {

	private String prefix = "";

	private String suffix = "";

	private boolean logTargetClassInvocation = false;


	/**
	 * Set the text that will get appended to the trace data.
	 * <p>Default is none.
	 * <p>
	 *  设置跟踪数据附加的文本<p>默认值为none
	 * 
	 */
	public void setPrefix(String prefix) {
		this.prefix = (prefix != null ? prefix : "");
	}

	/**
	 * Return the text that will get appended to the trace data.
	 * <p>
	 *  返回将追加到跟踪数据的文本
	 * 
	 */
	protected String getPrefix() {
		return this.prefix;
	}

	/**
	 * Set the text that will get prepended to the trace data.
	 * <p>Default is none.
	 * <p>
	 *  将要添加到跟踪数据中的文本设置为<p>默认值为none
	 * 
	 */
	public void setSuffix(String suffix) {
		this.suffix = (suffix != null ? suffix : "");
	}

	/**
	 * Return the text that will get prepended to the trace data.
	 * <p>
	 *  返回将添加到跟踪数据的文本
	 * 
	 */
	protected String getSuffix() {
		return this.suffix;
	}

	/**
	 * Set whether to log the invocation on the target class, if applicable
	 * (i.e. if the method is actually delegated to the target class).
	 * <p>Default is "false", logging the invocation based on the proxy
	 * interface/class name.
	 * <p>
	 *  设置是否在目标类上记录调用(如果适用)(即如果该方法实际上被委派给目标类)<p>默认值为"false",根据代理接口/类名记录调用
	 * 
	 */
	public void setLogTargetClassInvocation(boolean logTargetClassInvocation) {
		this.logTargetClassInvocation = logTargetClassInvocation;
	}


	/**
	 * Create a {@code String} name for the given {@code MethodInvocation}
	 * that can be used for trace/logging purposes. This name is made up of the
	 * configured prefix, followed by the fully-qualified name of the method being
	 * invoked, followed by the configured suffix.
	 * <p>
	 * 为给定的{@code MethodInvocation}创建可用于跟踪/记录目的的{@code String}名称该名称由配置的前缀组成,后跟被调用方法的完全限定名称,后跟配置的后缀
	 * 
	 * @see #setPrefix
	 * @see #setSuffix
	 */
	protected String createInvocationTraceName(MethodInvocation invocation) {
		StringBuilder sb = new StringBuilder(getPrefix());
		Method method = invocation.getMethod();
		Class<?> clazz = method.getDeclaringClass();
		if (this.logTargetClassInvocation && clazz.isInstance(invocation.getThis())) {
			clazz = invocation.getThis().getClass();
		}
		sb.append(clazz.getName());
		sb.append('.').append(method.getName());
		sb.append(getSuffix());
		return sb.toString();
	}

}
