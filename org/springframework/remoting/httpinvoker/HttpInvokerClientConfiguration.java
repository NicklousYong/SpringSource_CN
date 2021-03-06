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

package org.springframework.remoting.httpinvoker;

/**
 * Configuration interface for executing HTTP invoker requests.
 *
 * <p>
 *  用于执行HTTP调用者请求的配置界面
 * 
 * 
 * @author Juergen Hoeller
 * @since 1.1
 * @see HttpInvokerRequestExecutor
 * @see HttpInvokerClientInterceptor
 */
public interface HttpInvokerClientConfiguration {

	/**
	 * Return the HTTP URL of the target service.
	 * <p>
	 *  返回目标服务的HTTP URL
	 * 
	 */
	String getServiceUrl();

	/**
	 * Return the codebase URL to download classes from if not found locally.
	 * Can consist of multiple URLs, separated by spaces.
	 * <p>
	 * 返回代码库URL以从本地找不到可以由多个URL组成,以空格分隔
	 * 
	 * @return the codebase URL, or {@code null} if none
	 * @see java.rmi.server.RMIClassLoader
	 */
	String getCodebaseUrl();

}
