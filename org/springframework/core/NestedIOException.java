/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2014 the original author or authors.
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

import java.io.IOException;

/**
 * Subclass of {@link IOException} that properly handles a root cause,
 * exposing the root cause just like NestedChecked/RuntimeException does.
 *
 * <p>Proper root cause handling has not been added to standard IOException before
 * Java 6, which is why we need to do it ourselves for Java 5 compatibility purposes.
 *
 * <p>The similarity between this class and the NestedChecked/RuntimeException
 * class is unavoidable, as this class needs to derive from IOException.
 *
 * <p>
 *  正确处理根本原因的{@link IOException}子类,暴露根本原因就像NestedChecked / RuntimeException一样
 * 
 * 在Java 6之前,正确的根本原因处理尚未添加到标准的IOException中,这就是为什么我们需要为了Java 5的兼容性而进行的
 * 
 *  <p>此类与NestedChecked / RuntimeException类之间的相似性是不可避免的,因为此类需要从IOException导出
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.0
 * @see #getMessage
 * @see #printStackTrace
 * @see org.springframework.core.NestedCheckedException
 * @see org.springframework.core.NestedRuntimeException
 */
@SuppressWarnings("serial")
public class NestedIOException extends IOException {

	static {
		// Eagerly load the NestedExceptionUtils class to avoid classloader deadlock
		// issues on OSGi when calling getMessage(). Reported by Don Brown; SPR-5607.
		NestedExceptionUtils.class.getName();
	}


	/**
	 * Construct a {@code NestedIOException} with the specified detail message.
	 * <p>
	 *  使用指定的详细消息构造{@code NestedIOException}
	 * 
	 * 
	 * @param msg the detail message
	 */
	public NestedIOException(String msg) {
		super(msg);
	}

	/**
	 * Construct a {@code NestedIOException} with the specified detail message
	 * and nested exception.
	 * <p>
	 *  使用指定的详细消息和嵌套异常构造{@code NestedIOException}
	 * 
	 * 
	 * @param msg the detail message
	 * @param cause the nested exception
	 */
	public NestedIOException(String msg, Throwable cause) {
		super(msg, cause);
	}


	/**
	 * Return the detail message, including the message from the nested exception
	 * if there is one.
	 * <p>
	 *  返回详细消息,包括来自嵌套异常的消息(如果有的话)
	 */
	@Override
	public String getMessage() {
		return NestedExceptionUtils.buildMessage(super.getMessage(), getCause());
	}

}
