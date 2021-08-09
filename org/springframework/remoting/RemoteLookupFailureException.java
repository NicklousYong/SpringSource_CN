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

package org.springframework.remoting;

/**
 * RemoteAccessException subclass to be thrown in case of a lookup failure,
 * typically if the lookup happens on demand for each method invocation.
 *
 * <p>
 *  在查找失败的情况下抛出RemoteAccessException子类,通常如果按照每个方法调用的需求进行查找
 * 
 * 
 * @author Juergen Hoeller
 * @since 1.1
 */
@SuppressWarnings("serial")
public class RemoteLookupFailureException extends RemoteAccessException {

	/**
	 * Constructor for RemoteLookupFailureException.
	 * <p>
	 *  RemoteLookupFailureException的构造方法
	 * 
	 * 
	 * @param msg the detail message
	 */
	public RemoteLookupFailureException(String msg) {
		super(msg);
	}

	/**
	 * Constructor for RemoteLookupFailureException.
	 * <p>
	 * RemoteLookupFailureException的构造方法
	 * 
	 * @param msg message
	 * @param cause the root cause from the remoting API in use
	 */
	public RemoteLookupFailureException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
