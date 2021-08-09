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

package org.springframework.beans;

import org.springframework.core.NestedRuntimeException;
import org.springframework.util.ObjectUtils;

/**
 * Abstract superclass for all exceptions thrown in the beans package
 * and subpackages.
 *
 * <p>Note that this is a runtime (unchecked) exception. Beans exceptions
 * are usually fatal; there is no reason for them to be checked.
 *
 * <p>
 *  bean包和子包中抛出的所有异常的抽象超类
 * 
 *  请注意,这是一个运行时(未检查)异常。Bean异常通常是致命的;没有理由对他们进行检查
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
@SuppressWarnings("serial")
public abstract class BeansException extends NestedRuntimeException {

	/**
	 * Create a new BeansException with the specified message.
	 * <p>
	 * 用指定的消息创建一个新的BeansException
	 * 
	 * 
	 * @param msg the detail message
	 */
	public BeansException(String msg) {
		super(msg);
	}

	/**
	 * Create a new BeansException with the specified message
	 * and root cause.
	 * <p>
	 *  使用指定的消息和根本原因创建一个新的BeansException
	 * 
	 * @param msg the detail message
	 * @param cause the root cause
	 */
	public BeansException(String msg, Throwable cause) {
		super(msg, cause);
	}


	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof BeansException)) {
			return false;
		}
		BeansException otherBe = (BeansException) other;
		return (getMessage().equals(otherBe.getMessage()) &&
				ObjectUtils.nullSafeEquals(getCause(), otherBe.getCause()));
	}

	@Override
	public int hashCode() {
		return getMessage().hashCode();
	}

}
