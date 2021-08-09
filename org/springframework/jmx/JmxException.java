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

package org.springframework.jmx;

import org.springframework.core.NestedRuntimeException;

/**
 * General base exception to be thrown on JMX errors.
 * Unchecked since JMX failures are usually fatal.
 *
 * <p>
 *  JMX错误引发的一般基础异常由于JMX故障通常是致命的,因此未被检查
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.0
 */
@SuppressWarnings("serial")
public class JmxException extends NestedRuntimeException {

	/**
	 * Constructor for JmxException.
	 * <p>
	 *  JmxException的构造方法
	 * 
	 * 
	 * @param msg the detail message
	 */
	public JmxException(String msg) {
		super(msg);
	}

	/**
	 * Constructor for JmxException.
	 * <p>
	 *  JmxException的构造方法
	 * 
	 * @param msg the detail message
	 * @param cause the root cause (usually a raw JMX API exception)
	 */
	public JmxException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
