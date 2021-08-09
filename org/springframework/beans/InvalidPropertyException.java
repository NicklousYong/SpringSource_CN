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
 * Exception thrown when referring to an invalid bean property.
 * Carries the offending bean class and property name.
 *
 * <p>
 *  引用无效bean属性时引发的异常执行违规bean类和属性名称
 * 
 * 
 * @author Juergen Hoeller
 * @since 1.0.2
 */
@SuppressWarnings("serial")
public class InvalidPropertyException extends FatalBeanException {

	private Class<?> beanClass;

	private String propertyName;


	/**
	 * Create a new InvalidPropertyException.
	 * <p>
	 *  创建一个新的InvalidPropertyException
	 * 
	 * 
	 * @param beanClass the offending bean class
	 * @param propertyName the offending property
	 * @param msg the detail message
	 */
	public InvalidPropertyException(Class<?> beanClass, String propertyName, String msg) {
		this(beanClass, propertyName, msg, null);
	}

	/**
	 * Create a new InvalidPropertyException.
	 * <p>
	 *  创建一个新的InvalidPropertyException
	 * 
	 * 
	 * @param beanClass the offending bean class
	 * @param propertyName the offending property
	 * @param msg the detail message
	 * @param cause the root cause
	 */
	public InvalidPropertyException(Class<?> beanClass, String propertyName, String msg, Throwable cause) {
		super("Invalid property '" + propertyName + "' of bean class [" + beanClass.getName() + "]: " + msg, cause);
		this.beanClass = beanClass;
		this.propertyName = propertyName;
	}

	/**
	 * Return the offending bean class.
	 * <p>
	 * 返回冒犯的bean类
	 * 
	 */
	public Class<?> getBeanClass() {
		return beanClass;
	}

	/**
	 * Return the name of the offending property.
	 * <p>
	 *  返回违规财产的名称
	 */
	public String getPropertyName() {
		return propertyName;
	}

}
