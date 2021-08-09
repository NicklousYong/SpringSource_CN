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

package org.springframework.core;

/**
 * Handy class for wrapping checked {@code Exceptions} with a root cause.
 *
 * <p>This class is {@code abstract} to force the programmer to extend
 * the class. {@code getMessage} will include nested exception
 * information; {@code printStackTrace} and other like methods will
 * delegate to the wrapped exception, if any.
 *
 * <p>The similarity between this class and the {@link NestedRuntimeException}
 * class is unavoidable, as Java forces these two classes to have different
 * superclasses (ah, the inflexibility of concrete inheritance!).
 *
 * <p>
 *  使用根本原因来检查{@code异常}的方便类
 * 
 * <p>这个类是{@code abstract}强制程序员扩展类{@code getMessage}将包含嵌套异常信息; {@code printStackTrace}和其他类似的方法将委托给包装的异常(
 * 如果有的话)。
 * 
 *  <p>这个类与{@link NestedRuntimeException}类之间的相似性是不可避免的,因为Java迫使这两个类有不同的超类(啊,具体继承的不灵活性)
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see #getMessage
 * @see #printStackTrace
 * @see NestedRuntimeException
 */
public abstract class NestedCheckedException extends Exception {

	/** Use serialVersionUID from Spring 1.2 for interoperability */
	private static final long serialVersionUID = 7100714597678207546L;

	static {
		// Eagerly load the NestedExceptionUtils class to avoid classloader deadlock
		// issues on OSGi when calling getMessage(). Reported by Don Brown; SPR-5607.
		NestedExceptionUtils.class.getName();
	}


	/**
	 * Construct a {@code NestedCheckedException} with the specified detail message.
	 * <p>
	 *  使用指定的详细消息构造{@code NestedCheckedException}
	 * 
	 * 
	 * @param msg the detail message
	 */
	public NestedCheckedException(String msg) {
		super(msg);
	}

	/**
	 * Construct a {@code NestedCheckedException} with the specified detail message
	 * and nested exception.
	 * <p>
	 *  使用指定的详细消息和嵌套异常构造{@code NestedCheckedException}
	 * 
	 * 
	 * @param msg the detail message
	 * @param cause the nested exception
	 */
	public NestedCheckedException(String msg, Throwable cause) {
		super(msg, cause);
	}


	/**
	 * Return the detail message, including the message from the nested exception
	 * if there is one.
	 * <p>
	 *  返回详细消息,包括来自嵌套异常的消息(如果有的话)
	 * 
	 */
	@Override
	public String getMessage() {
		return NestedExceptionUtils.buildMessage(super.getMessage(), getCause());
	}


	/**
	 * Retrieve the innermost cause of this exception, if any.
	 * <p>
	 *  检索此异常的最内部原因(如果有)
	 * 
	 * 
	 * @return the innermost exception, or {@code null} if none
	 */
	public Throwable getRootCause() {
		Throwable rootCause = null;
		Throwable cause = getCause();
		while (cause != null && cause != rootCause) {
			rootCause = cause;
			cause = cause.getCause();
		}
		return rootCause;
	}

	/**
	 * Retrieve the most specific cause of this exception, that is,
	 * either the innermost cause (root cause) or this exception itself.
	 * <p>Differs from {@link #getRootCause()} in that it falls back
	 * to the present exception if there is no root cause.
	 * <p>
	 * 检索此异常的最具体原因,即最内部原因(根本原因)或此异常本身<p>不同于{@link #getRootCause()},因为如果没有,则返回到当前异常根本原因
	 * 
	 * 
	 * @return the most specific cause (never {@code null})
	 * @since 2.0.3
	 */
	public Throwable getMostSpecificCause() {
		Throwable rootCause = getRootCause();
		return (rootCause != null ? rootCause : this);
	}

	/**
	 * Check whether this exception contains an exception of the given type:
	 * either it is of the given class itself or it contains a nested cause
	 * of the given type.
	 * <p>
	 *  检查此异常是否包含给定类型的异常：它是给定类本身或包含给定类型的嵌套原因
	 * 
	 * @param exType the exception type to look for
	 * @return whether there is a nested exception of the specified type
	 */
	public boolean contains(Class<?> exType) {
		if (exType == null) {
			return false;
		}
		if (exType.isInstance(this)) {
			return true;
		}
		Throwable cause = getCause();
		if (cause == this) {
			return false;
		}
		if (cause instanceof NestedCheckedException) {
			return ((NestedCheckedException) cause).contains(exType);
		}
		else {
			while (cause != null) {
				if (exType.isInstance(cause)) {
					return true;
				}
				if (cause.getCause() == cause) {
					break;
				}
				cause = cause.getCause();
			}
			return false;
		}
	}

}
