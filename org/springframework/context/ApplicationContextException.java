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

package org.springframework.context;

import org.springframework.beans.FatalBeanException;

/**
 * Exception thrown during application context initialization.
 *
 * <p>
 *  在应用程序上下文初始化期间抛出异常
 * 
 * 
 * @author Rod Johnson
 */
@SuppressWarnings("serial")
public class ApplicationContextException extends FatalBeanException {

	/**
	 * Create a new {@code ApplicationContextException}
	 * with the specified detail message and no root cause.
	 * <p>
	 *  使用指定的详细消息创建一个新的{@code ApplicationContextException},而不是根本原因
	 * 
	 * 
	 * @param msg the detail message
	 */
	public ApplicationContextException(String msg) {
		super(msg);
	}

	/**
	 * Create a new {@code ApplicationContextException}
	 * with the specified detail message and the given root cause.
	 * <p>
	 * 使用指定的详细消息和给定的根本原因创建一个新的{@code ApplicationContextException}
	 * 
	 * @param msg the detail message
	 * @param cause the root cause
	 */
	public ApplicationContextException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
