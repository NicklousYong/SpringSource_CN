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

import java.beans.PropertyChangeEvent;

import org.springframework.core.ErrorCoded;

/**
 * Superclass for exceptions related to a property access,
 * such as type mismatch or invocation target exception.
 *
 * <p>
 *  超类用于与属性访问相关的异常,例如类型不匹配或调用目标异常
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
@SuppressWarnings("serial")
public abstract class PropertyAccessException extends BeansException implements ErrorCoded {

	private transient PropertyChangeEvent propertyChangeEvent;


	/**
	 * Create a new PropertyAccessException.
	 * <p>
	 *  创建一个新的PropertyAccessException
	 * 
	 * 
	 * @param propertyChangeEvent the PropertyChangeEvent that resulted in the problem
	 * @param msg the detail message
	 * @param cause the root cause
	 */
	public PropertyAccessException(PropertyChangeEvent propertyChangeEvent, String msg, Throwable cause) {
		super(msg, cause);
		this.propertyChangeEvent = propertyChangeEvent;
	}

	/**
	 * Create a new PropertyAccessException without PropertyChangeEvent.
	 * <p>
	 *  创建一个没有PropertyChangeEvent的新的PropertyAccessException
	 * 
	 * 
	 * @param msg the detail message
	 * @param cause the root cause
	 */
	public PropertyAccessException(String msg, Throwable cause) {
		super(msg, cause);
	}


	/**
	 * Return the PropertyChangeEvent that resulted in the problem.
	 * <p>May be {@code null}; only available if an actual bean property
	 * was affected.
	 * <p>
	 * 返回导致问题的PropertyChangeEvent <p>可能是{@code null};仅当实际的bean属性受到影响时才可用
	 * 
	 */
	public PropertyChangeEvent getPropertyChangeEvent() {
		return this.propertyChangeEvent;
	}

	/**
	 * Return the name of the affected property, if available.
	 * <p>
	 *  返回受影响的财产的名称(如果有)
	 * 
	 */
	public String getPropertyName() {
		return (this.propertyChangeEvent != null ? this.propertyChangeEvent.getPropertyName() : null);
	}

	/**
	 * Return the affected value that was about to be set, if any.
	 * <p>
	 *  返回即将设置的受影响的值(如果有)
	 */
	public Object getValue() {
		return (this.propertyChangeEvent != null ? this.propertyChangeEvent.getNewValue() : null);
	}

}
