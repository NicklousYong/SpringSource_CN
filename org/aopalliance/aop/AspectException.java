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

package org.aopalliance.aop;

/**
 * Superclass for all AOP infrastructure exceptions.
 * Unchecked, as such exceptions are fatal and end user
 * code shouldn't be forced to catch them.
 * 
 * <p>
 *  所有AOP基础架构异常的超类未经检查,因为这些例外是致命的,最终用户代码不应被迫抓住他们
 * 
 * 
 * @author Rod Johnson
 * @author Bob Lee
 * @author Juergen Hoeller
 */
@SuppressWarnings("serial")
public class AspectException extends RuntimeException {

	/**
	 * Constructor for AspectException.
	 * <p>
	 *  AspectException的构造方法
	 * 
	 * 
	 * @param message the exception message
	 */
	public AspectException(String message) {
		super(message);
	}

	/**
	 * Constructor for AspectException.
	 * <p>
	 *  AspectException的构造方法
	 * 
	 * @param message the exception message
	 * @param cause the root cause, if any
	 */
	public AspectException(String message, Throwable cause) {
		super(message, cause);
	}

}
