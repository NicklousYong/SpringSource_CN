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

package org.springframework.beans;

import java.beans.PropertyChangeEvent;

import org.springframework.util.ClassUtils;

/**
 * Exception thrown on a type mismatch when trying to set a bean property.
 *
 * <p>
 *  尝试设置bean属性时,类型不匹配引发异常
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
@SuppressWarnings("serial")
public class TypeMismatchException extends PropertyAccessException {

	/**
	 * Error code that a type mismatch error will be registered with.
	 * <p>
	 *  类型不匹配错误将被注册的错误代码
	 * 
	 */
	public static final String ERROR_CODE = "typeMismatch";


	private transient Object value;

	private Class<?> requiredType;


	/**
	 * Create a new TypeMismatchException.
	 * <p>
	 *  创建一个新的TypeMismatchException
	 * 
	 * 
	 * @param propertyChangeEvent the PropertyChangeEvent that resulted in the problem
	 * @param requiredType the required target type
	 */
	public TypeMismatchException(PropertyChangeEvent propertyChangeEvent, Class<?> requiredType) {
		this(propertyChangeEvent, requiredType, null);
	}

	/**
	 * Create a new TypeMismatchException.
	 * <p>
	 *  创建一个新的TypeMismatchException
	 * 
	 * 
	 * @param propertyChangeEvent the PropertyChangeEvent that resulted in the problem
	 * @param requiredType the required target type (or {@code null} if not known)
	 * @param cause the root cause (may be {@code null})
	 */
	public TypeMismatchException(PropertyChangeEvent propertyChangeEvent, Class<?> requiredType, Throwable cause) {
		super(propertyChangeEvent,
				"Failed to convert property value of type [" +
				ClassUtils.getDescriptiveType(propertyChangeEvent.getNewValue()) + "]" +
				(requiredType != null ?
				 " to required type [" + ClassUtils.getQualifiedName(requiredType) + "]" : "") +
				(propertyChangeEvent.getPropertyName() != null ?
				 " for property '" + propertyChangeEvent.getPropertyName() + "'" : ""),
				cause);
		this.value = propertyChangeEvent.getNewValue();
		this.requiredType = requiredType;
	}

	/**
	 * Create a new TypeMismatchException without PropertyChangeEvent.
	 * <p>
	 * 创建一个没有PropertyChangeEvent的新的TypeMismatchException
	 * 
	 * 
	 * @param value the offending value that couldn't be converted (may be {@code null})
	 * @param requiredType the required target type (or {@code null} if not known)
	 */
	public TypeMismatchException(Object value, Class<?> requiredType) {
		this(value, requiredType, null);
	}

	/**
	 * Create a new TypeMismatchException without PropertyChangeEvent.
	 * <p>
	 *  创建一个没有PropertyChangeEvent的新的TypeMismatchException
	 * 
	 * 
	 * @param value the offending value that couldn't be converted (may be {@code null})
	 * @param requiredType the required target type (or {@code null} if not known)
	 * @param cause the root cause (may be {@code null})
	 */
	public TypeMismatchException(Object value, Class<?> requiredType, Throwable cause) {
		super("Failed to convert value of type [" + ClassUtils.getDescriptiveType(value) + "]" +
				(requiredType != null ? " to required type [" + ClassUtils.getQualifiedName(requiredType) + "]" : ""),
				cause);
		this.value = value;
		this.requiredType = requiredType;
	}


	/**
	 * Return the offending value (may be {@code null}).
	 * <p>
	 *  返回违规值(可能是{@code null})
	 * 
	 */
	@Override
	public Object getValue() {
		return this.value;
	}

	/**
	 * Return the required target type, if any.
	 * <p>
	 *  返回所需的目标类型(如果有)
	 */
	public Class<?> getRequiredType() {
		return this.requiredType;
	}

	@Override
	public String getErrorCode() {
		return ERROR_CODE;
	}

}
