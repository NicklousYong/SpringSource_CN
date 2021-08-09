/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2015 the original author or authors.
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

package org.springframework.remoting.httpinvoker;

import java.io.IOException;

import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationResult;

/**
 * Strategy interface for actual execution of an HTTP invoker request.
 * Used by HttpInvokerClientInterceptor and its subclass
 * HttpInvokerProxyFactoryBean.
 *
 * <p>Two implementations are provided out of the box:
 * <ul>
 * <li><b>{@code SimpleHttpInvokerRequestExecutor}:</b>
 * Uses JDK facilities to execute POST requests, without support
 * for HTTP authentication or advanced configuration options.
 * <li><b>{@code HttpComponentsHttpInvokerRequestExecutor}:</b>
 * Uses Apache's Commons HttpClient to execute POST requests,
 * allowing to use a preconfigured HttpClient instance
 * (potentially with authentication, HTTP connection pooling, etc).
 * </ul>
 *
 * <p>
 *  用于实际执行HTTP调用者请求的策略接口HttpInvokerClientInterceptor及其子类使用HttpInvokerProxyFactoryBean
 * 
 *  <p>开箱即用的实现方式如下：
 * <ul>
 * <li> <b> {@ code SimpleHttpInvokerRequestExecutor}：</b>使用JDK功能执行POST请求,不支持HTTP身份验证或高级配置选项<li> <b> {@ code HttpComponentsHttpInvokerRequestExecutor}
 * ：</b> Apache的Commons HttpClient执行POST请求,允许使用预配置的HttpClient实例(潜在地与认证,HTTP连接池等)。
 * 
 * @author Juergen Hoeller
 * @since 1.1
 * @see HttpInvokerClientInterceptor#setHttpInvokerRequestExecutor
 */
public interface HttpInvokerRequestExecutor {

	/**
	 * Execute a request to send the given remote invocation.
	 * <p>
	 * </ul>
	 * 
	 * 
	 * @param config the HTTP invoker configuration that specifies the
	 * target service
	 * @param invocation the RemoteInvocation to execute
	 * @return the RemoteInvocationResult object
	 * @throws IOException if thrown by I/O operations
	 * @throws ClassNotFoundException if thrown during deserialization
	 * @throws Exception in case of general errors
	 */
	RemoteInvocationResult executeRequest(HttpInvokerClientConfiguration config, RemoteInvocation invocation)
			throws Exception;

}
