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

package org.springframework.web;

import javax.servlet.ServletException;

/**
 * Exception thrown when an HTTP request handler requires a pre-existing session.
 *
 * <p>
 *  当HTTP请求处理程序需要预先存在的会话时抛出异常
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.0
 */
@SuppressWarnings("serial")
public class HttpSessionRequiredException extends ServletException {

	private String expectedAttribute;


	/**
	 * Create a new HttpSessionRequiredException.
	 * <p>
	 *  创建一个新的HttpSessionRequiredException
	 * 
	 * 
	 * @param msg the detail message
	 */
	public HttpSessionRequiredException(String msg) {
		super(msg);
	}

	/**
	 * Create a new HttpSessionRequiredException.
	 * <p>
	 *  创建一个新的HttpSessionRequiredException
	 * 
	 * 
	 * @param msg the detail message
	 * @param expectedAttribute the name of the expected session attribute
	 * @since 4.3
	 */
	public HttpSessionRequiredException(String msg, String expectedAttribute) {
		super(msg);
		this.expectedAttribute = expectedAttribute;
	}


	/**
	 * Return the name of the expected session attribute, if any.
	 * <p>
	 * 返回预期会话属性的名称(如果有)
	 * 
	 * @since 4.3
	 */
	public String getExpectedAttribute() {
		return this.expectedAttribute;
	}

}
