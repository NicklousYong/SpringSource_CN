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

package org.springframework.oxm;

import org.springframework.core.NestedRuntimeException;

/**
 * Root of the hierarchy of Object XML Mapping exceptions.
 *
 * <p>
 *  对象XML映射异常的层次结构的根
 * 
 * 
 * @author Arjen Poutsma
 * @since 3.0
 */
@SuppressWarnings("serial")
public abstract class XmlMappingException extends NestedRuntimeException {

	/**
	 * Construct an {@code XmlMappingException} with the specified detail message.
	 * <p>
	 *  使用指定的详细信息构造一个{@code XmlMappingException}
	 * 
	 * 
	 * @param msg the detail message
	 */
	public XmlMappingException(String msg) {
		super(msg);
	}

	/**
	 * Construct an {@code XmlMappingException} with the specified detail message
	 * and nested exception.
	 * <p>
	 * 使用指定的详细消息和嵌套异常构造{@code XmlMappingException}
	 * 
	 * @param msg the detail message
	 * @param cause the nested exception
	 */
	public XmlMappingException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
