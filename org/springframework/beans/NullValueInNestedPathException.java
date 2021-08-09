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

package org.springframework.beans;

/**
 * Exception thrown when navigation of a valid nested property
 * path encounters a NullPointerException.
 *
 * <p>For example, navigating "spouse.age" could fail because the
 * spouse property of the target object has a null value.
 *
 * <p>
 *  当导航有效的嵌套属性路径遇到NullPointerException异常时抛出异常
 * 
 *  <p>例如,导航"spouseage"可能会失败,因为目标对象的配偶属性具有空值
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
@SuppressWarnings("serial")
public class NullValueInNestedPathException extends InvalidPropertyException {

	/**
	 * Create a new NullValueInNestedPathException.
	 * <p>
	 * 创建一个新的NullValueInNestedPathException
	 * 
	 * 
	 * @param beanClass the offending bean class
	 * @param propertyName the offending property
	 */
	public NullValueInNestedPathException(Class<?> beanClass, String propertyName) {
		super(beanClass, propertyName, "Value of nested property '" + propertyName + "' is null");
	}

	/**
	 * Create a new NullValueInNestedPathException.
	 * <p>
	 *  创建一个新的NullValueInNestedPathException
	 * 
	 * 
	 * @param beanClass the offending bean class
	 * @param propertyName the offending property
	 * @param msg the detail message
	 */
	public NullValueInNestedPathException(Class<?> beanClass, String propertyName, String msg) {
		super(beanClass, propertyName, msg);
	}

	/**
	 * Create a new NullValueInNestedPathException.
	 * <p>
	 *  创建一个新的NullValueInNestedPathException
	 * 
	 * @param beanClass the offending bean class
	 * @param propertyName the offending property
	 * @param msg the detail message
	 * @param cause the root cause
	 * @since 4.3.2
	 */
	public NullValueInNestedPathException(Class<?> beanClass, String propertyName, String msg, Throwable cause) {
		super(beanClass, propertyName, msg, cause);
	}

}
