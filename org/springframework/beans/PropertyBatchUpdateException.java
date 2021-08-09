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

import java.io.PrintStream;
import java.io.PrintWriter;

import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * Combined exception, composed of individual PropertyAccessException instances.
 * An object of this class is created at the beginning of the binding
 * process, and errors added to it as necessary.
 *
 * <p>The binding process continues when it encounters application-level
 * PropertyAccessExceptions, applying those changes that can be applied
 * and storing rejected changes in an object of this class.
 *
 * <p>
 *  组合异常,由单独的PropertyAccessException实例组成此类的对象在绑定过程的开始创建,并根据需要添加错误
 * 
 * <p>绑定过程在遇到应用程序级PropertyAccessExceptions时继续,应用可应用的这些更改并将拒绝的更改存储在此类的对象中
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 18 April 2001
 */
@SuppressWarnings("serial")
public class PropertyBatchUpdateException extends BeansException {

	/** List of PropertyAccessException objects */
	private PropertyAccessException[] propertyAccessExceptions;


	/**
	 * Create a new PropertyBatchUpdateException.
	 * <p>
	 *  创建一个新的PropertyBatchUpdateException
	 * 
	 * 
	 * @param propertyAccessExceptions the List of PropertyAccessExceptions
	 */
	public PropertyBatchUpdateException(PropertyAccessException[] propertyAccessExceptions) {
		super(null);
		Assert.notEmpty(propertyAccessExceptions, "At least 1 PropertyAccessException required");
		this.propertyAccessExceptions = propertyAccessExceptions;
	}


	/**
	 * If this returns 0, no errors were encountered during binding.
	 * <p>
	 *  如果返回0,则在绑定期间不会遇到任何错误
	 * 
	 */
	public final int getExceptionCount() {
		return this.propertyAccessExceptions.length;
	}

	/**
	 * Return an array of the propertyAccessExceptions stored in this object.
	 * <p>Will return the empty array (not {@code null}) if there were no errors.
	 * <p>
	 *  返回存储在此对象中的propertyAccessExceptions的数组<p>如果没有错误,将返回空数组(不是{@code null})
	 * 
	 */
	public final PropertyAccessException[] getPropertyAccessExceptions() {
		return this.propertyAccessExceptions;
	}

	/**
	 * Return the exception for this field, or {@code null} if there isn't any.
	 * <p>
	 *  返回此字段的异常,否则返回{@code null}
	 */
	public PropertyAccessException getPropertyAccessException(String propertyName) {
		for (PropertyAccessException pae : this.propertyAccessExceptions) {
			if (ObjectUtils.nullSafeEquals(propertyName, pae.getPropertyName())) {
				return pae;
			}
		}
		return null;
	}


	@Override
	public String getMessage() {
		StringBuilder sb = new StringBuilder("Failed properties: ");
		for (int i = 0; i < this.propertyAccessExceptions.length; i++) {
			sb.append(this.propertyAccessExceptions[i].getMessage());
			if (i < this.propertyAccessExceptions.length - 1) {
				sb.append("; ");
			}
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getName()).append("; nested PropertyAccessExceptions (");
		sb.append(getExceptionCount()).append(") are:");
		for (int i = 0; i < this.propertyAccessExceptions.length; i++) {
			sb.append('\n').append("PropertyAccessException ").append(i + 1).append(": ");
			sb.append(this.propertyAccessExceptions[i]);
		}
		return sb.toString();
	}

	@Override
	public void printStackTrace(PrintStream ps) {
		synchronized (ps) {
			ps.println(getClass().getName() + "; nested PropertyAccessException details (" +
					getExceptionCount() + ") are:");
			for (int i = 0; i < this.propertyAccessExceptions.length; i++) {
				ps.println("PropertyAccessException " + (i + 1) + ":");
				this.propertyAccessExceptions[i].printStackTrace(ps);
			}
		}
	}

	@Override
	public void printStackTrace(PrintWriter pw) {
		synchronized (pw) {
			pw.println(getClass().getName() + "; nested PropertyAccessException details (" +
					getExceptionCount() + ") are:");
			for (int i = 0; i < this.propertyAccessExceptions.length; i++) {
				pw.println("PropertyAccessException " + (i + 1) + ":");
				this.propertyAccessExceptions[i].printStackTrace(pw);
			}
		}
	}

	@Override
	public boolean contains(Class<?> exType) {
		if (exType == null) {
			return false;
		}
		if (exType.isInstance(this)) {
			return true;
		}
		for (PropertyAccessException pae : this.propertyAccessExceptions) {
			if (pae.contains(exType)) {
				return true;
			}
		}
		return false;
	}

}
