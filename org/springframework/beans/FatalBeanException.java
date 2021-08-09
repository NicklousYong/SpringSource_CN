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

/**
 * Thrown on an unrecoverable problem encountered in the
 * beans packages or sub-packages, e.g. bad class or field.
 *
 * <p>
 *  抛出了在bean包或子包中遇到的不可恢复的问题,例如不好的类或字段
 * 
 * 
 * @author Rod Johnson
 */
@SuppressWarnings("serial")
public class FatalBeanException extends BeansException {

	/**
	 * Create a new FatalBeanException with the specified message.
	 * <p>
	 *  使用指定的消息创建一个新的FatalBeanException
	 * 
	 * 
	 * @param msg the detail message
	 */
	public FatalBeanException(String msg) {
		super(msg);
	}

	/**
	 * Create a new FatalBeanException with the specified message
	 * and root cause.
	 * <p>
	 * 使用指定的消息和根本原因创建一个新的FatalBeanException
	 * 
	 * @param msg the detail message
	 * @param cause the root cause
	 */
	public FatalBeanException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
