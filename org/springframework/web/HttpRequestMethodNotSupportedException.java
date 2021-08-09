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

package org.springframework.web;

import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletException;

import org.springframework.http.HttpMethod;

/**
 * Exception thrown when a request handler does not support a
 * specific request method.
 *
 * <p>
 *  请求处理程序不支持特定请求方法时抛出异常
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.0
 */
@SuppressWarnings("serial")
public class HttpRequestMethodNotSupportedException extends ServletException {

	private String method;

	private String[] supportedMethods;


	/**
	 * Create a new HttpRequestMethodNotSupportedException.
	 * <p>
	 *  创建一个新的HttpRequestMethodNotSupportedException
	 * 
	 * 
	 * @param method the unsupported HTTP request method
	 */
	public HttpRequestMethodNotSupportedException(String method) {
		this(method, (String[]) null);
	}

	/**
	 * Create a new HttpRequestMethodNotSupportedException.
	 * <p>
	 *  创建一个新的HttpRequestMethodNotSupportedException
	 * 
	 * 
	 * @param method the unsupported HTTP request method
	 * @param supportedMethods the actually supported HTTP methods
	 */
	public HttpRequestMethodNotSupportedException(String method, String[] supportedMethods) {
		this(method, supportedMethods, "Request method '" + method + "' not supported");
	}

	/**
	 * Create a new HttpRequestMethodNotSupportedException.
	 * <p>
	 * 创建一个新的HttpRequestMethodNotSupportedException
	 * 
	 * 
	 * @param method the unsupported HTTP request method
	 * @param supportedMethods the actually supported HTTP methods
	 */
	public HttpRequestMethodNotSupportedException(String method, Collection<String> supportedMethods) {
		this(method, supportedMethods.toArray(new String[supportedMethods.size()]));
	}

	/**
	 * Create a new HttpRequestMethodNotSupportedException.
	 * <p>
	 *  创建一个新的HttpRequestMethodNotSupportedException
	 * 
	 * 
	 * @param method the unsupported HTTP request method
	 * @param msg the detail message
	 */
	public HttpRequestMethodNotSupportedException(String method, String msg) {
		this(method, null, msg);
	}

	/**
	 * Create a new HttpRequestMethodNotSupportedException.
	 * <p>
	 *  创建一个新的HttpRequestMethodNotSupportedException
	 * 
	 * 
	 * @param method the unsupported HTTP request method
	 * @param supportedMethods the actually supported HTTP methods
	 * @param msg the detail message
	 */
	public HttpRequestMethodNotSupportedException(String method, String[] supportedMethods, String msg) {
		super(msg);
		this.method = method;
		this.supportedMethods = supportedMethods;
	}


	/**
	 * Return the HTTP request method that caused the failure.
	 * <p>
	 *  返回导致失败的HTTP请求方法
	 * 
	 */
	public String getMethod() {
		return this.method;
	}

	/**
	 * Return the actually supported HTTP methods, if known.
	 * <p>
	 *  如果已知,返回实际支持的HTTP方法
	 * 
	 */
	public String[] getSupportedMethods() {
		return this.supportedMethods;
	}

	/**
	 * Return the actually supported HTTP methods, if known, as {@link HttpMethod} instances.
	 * <p>
	 *  将实际支持的HTTP方法(如果已知)返回为{@link HttpMethod}实例
	 */
	public Set<HttpMethod> getSupportedHttpMethods() {
		List<HttpMethod> supportedMethods = new LinkedList<HttpMethod>();
		for (String value : this.supportedMethods) {
			HttpMethod resolved = HttpMethod.resolve(value);
			if (resolved != null) {
				supportedMethods.add(resolved);
			}
		}
		return EnumSet.copyOf(supportedMethods);
	}

}
