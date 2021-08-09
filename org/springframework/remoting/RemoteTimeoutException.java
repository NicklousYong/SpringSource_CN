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

package org.springframework.remoting;

/**
 * RemoteAccessException subclass to be thrown when the execution
 * of the target method did not complete before a configurable
 * timeout, for example when a reply message was not received.
 * <p>
 *  RemoteAccessException子类在目标方法的执行在配置超时之前未完成时被抛出,例如当未收到回复消息时
 * 
 * 
 * @author Stephane Nicoll
 * @since 4.2
 */
@SuppressWarnings("serial")
public class RemoteTimeoutException extends RemoteAccessException {

	/**
	 * Constructor for RemoteTimeoutException.
	 * <p>
	 *  RemoteTimeoutException的构造方法
	 * 
	 * 
	 * @param msg the detail message
	 */
	public RemoteTimeoutException(String msg) {
		super(msg);
	}

	/**
	 * Constructor for RemoteTimeoutException.
	 * <p>
	 * RemoteTimeoutException的构造方法
	 * 
	 * @param msg the detail message
	 * @param cause the root cause from the remoting API in use
	 */
	public RemoteTimeoutException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
