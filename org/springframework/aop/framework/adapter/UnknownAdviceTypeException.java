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

package org.springframework.aop.framework.adapter;

/**
 * Exception thrown when an attempt is made to use an unsupported
 * Advisor or Advice type.
 *
 * <p>
 *  尝试使用不受支持的顾问或建议类型时抛出异常
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see org.aopalliance.aop.Advice
 * @see org.springframework.aop.Advisor
 */
@SuppressWarnings("serial")
public class UnknownAdviceTypeException extends IllegalArgumentException {

	/**
	 * Create a new UnknownAdviceTypeException for the given advice object.
	 * Will create a message text that says that the object is neither a
	 * subinterface of Advice nor an Advisor.
	 * <p>
	 * 为给定的建议对象创建一个新的UnknownAdviceTypeException将创建一条消息文本,表示对象既不是Advice也不是Advisor的子接口
	 * 
	 * 
	 * @param advice the advice object of unknown type
	 */
	public UnknownAdviceTypeException(Object advice) {
		super("Advice object [" + advice + "] is neither a supported subinterface of " +
				"[org.aopalliance.aop.Advice] nor an [org.springframework.aop.Advisor]");
	}

	/**
	 * Create a new UnknownAdviceTypeException with the given message.
	 * <p>
	 *  用给定的消息创建一个新的UnknownAdviceTypeException
	 * 
	 * @param message the message text
	 */
	public UnknownAdviceTypeException(String message) {
		super(message);
	}

}
