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

package org.springframework.beans.factory;

import org.springframework.beans.BeansException;

/**
 * Thrown when a bean doesn't match the expected type.
 *
 * <p>
 *  当bean与预期类型不匹配时抛出
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
@SuppressWarnings("serial")
public class BeanNotOfRequiredTypeException extends BeansException {

	/** The name of the instance that was of the wrong type */
	private String beanName;

	/** The required type */
	private Class<?> requiredType;

	/** The offending type */
	private Class<?> actualType;


	/**
	 * Create a new BeanNotOfRequiredTypeException.
	 * <p>
	 *  创建一个新的BeanNotOfRequiredTypeException
	 * 
	 * 
	 * @param beanName the name of the bean requested
	 * @param requiredType the required type
	 * @param actualType the actual type returned, which did not match
	 * the expected type
	 */
	public BeanNotOfRequiredTypeException(String beanName, Class<?> requiredType, Class<?> actualType) {
		super("Bean named '" + beanName + "' is expected to be of type [" + requiredType.getName() +
				"] but was actually of type [" + actualType.getName() + "]");
		this.beanName = beanName;
		this.requiredType = requiredType;
		this.actualType = actualType;
	}


	/**
	 * Return the name of the instance that was of the wrong type.
	 * <p>
	 *  返回错误类型的实例的名称
	 * 
	 */
	public String getBeanName() {
		return this.beanName;
	}

	/**
	 * Return the expected type for the bean.
	 * <p>
	 *  返回bean的预期类型
	 * 
	 */
	public Class<?> getRequiredType() {
		return this.requiredType;
	}

	/**
	 * Return the actual type of the instance found.
	 * <p>
	 * 返回实际发现的类型
	 */
	public Class<?> getActualType() {
		return this.actualType;
	}

}
