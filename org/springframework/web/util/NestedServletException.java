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

package org.springframework.web.util;

import javax.servlet.ServletException;

import org.springframework.core.NestedExceptionUtils;

/**
 * Subclass of {@link ServletException} that properly handles a root cause in terms
 * of message and stacktrace, just like NestedChecked/RuntimeException does.
 *
 * <p>Note that the plain ServletException doesn't expose its root cause at all,
 * neither in the exception message nor in printed stack traces! While this might
 * be fixed in later Servlet API variants (which even differ per vendor for the
 * same API version), it is not reliably available on Servlet 2.4 (the minimum
 * version required by Spring 3.x), which is why we need to do it ourselves.
 *
 * <p>The similarity between this class and the NestedChecked/RuntimeException
 * class is unavoidable, as this class needs to derive from ServletException.
 *
 * <p>
 *  根据消息和堆栈跟踪正确处理根本原因的{@link ServletException}的子类,就像NestedChecked / RuntimeException一样
 * 
 * <p>请注意,纯ServletException不会暴露其根本原因,无论是在异常消息还是打印堆栈跟踪！虽然这可能在以后的Servlet API变体中有所修改(对于相同的API版本而言,它们对于每个供应商
 * 而言是不同的),但是在Servlet 24(Spring 3x要求的最低版本)上是不可靠的,这就是为什么我们需要自己做。
 * 
 *  <p>此类与NestedChecked / RuntimeException类之间的相似性是不可避免的,因为该类需要从ServletException派生
 * 
 * 
 * @author Juergen Hoeller
 * @since 1.2.5
 * @see #getMessage
 * @see #printStackTrace
 * @see org.springframework.core.NestedCheckedException
 * @see org.springframework.core.NestedRuntimeException
 */
public class NestedServletException extends ServletException {

	/** Use serialVersionUID from Spring 1.2 for interoperability */
	private static final long serialVersionUID = -5292377985529381145L;

	static {
		// Eagerly load the NestedExceptionUtils class to avoid classloader deadlock
		// issues on OSGi when calling getMessage(). Reported by Don Brown; SPR-5607.
		NestedExceptionUtils.class.getName();
	}


	/**
	 * Construct a {@code NestedServletException} with the specified detail message.
	 * <p>
	 *  使用指定的详细消息构造{@code NestedServletException}
	 * 
	 * 
	 * @param msg the detail message
	 */
	public NestedServletException(String msg) {
		super(msg);
	}

	/**
	 * Construct a {@code NestedServletException} with the specified detail message
	 * and nested exception.
	 * <p>
	 *  使用指定的详细消息和嵌套异常构造{@code NestedServletException}
	 * 
	 * 
	 * @param msg the detail message
	 * @param cause the nested exception
	 */
	public NestedServletException(String msg, Throwable cause) {
		super(msg, cause);
		// Set JDK 1.4 exception chain cause if not done by ServletException class already
		// (this differs between Servlet API versions).
		if (getCause() == null && cause!=null) {
			initCause(cause);
		}
	}


	/**
	 * Return the detail message, including the message from the nested exception
	 * if there is one.
	 * <p>
	 * 返回详细消息,包括来自嵌套异常的消息(如果有的话)
	 */
	@Override
	public String getMessage() {
		return NestedExceptionUtils.buildMessage(super.getMessage(), getCause());
	}

}
