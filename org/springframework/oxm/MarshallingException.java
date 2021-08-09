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

/**
 * Base class for exception thrown when a marshalling or unmarshalling error occurs.
 *
 * <p>
 *  出现编组或解组错误时引发异常的基类
 * 
 * 
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @since 3.0
 * @see MarshallingFailureException
 * @see UnmarshallingFailureException
 */
@SuppressWarnings("serial")
public abstract class MarshallingException extends XmlMappingException {

	/**
	 * Construct a {@code MarshallingException} with the specified detail message.
	 * <p>
	 *  使用指定的详细消息构造{@code MarshallingException}
	 * 
	 * 
	 * @param msg the detail message
	 */
	protected MarshallingException(String msg) {
		super(msg);
	}

	/**
	 * Construct a {@code MarshallingException} with the specified detail message
	 * and nested exception.
	 * <p>
	 * 使用指定的详细消息和嵌套异常构造{@code MarshallingException}
	 * 
	 * @param msg the detail message
	 * @param cause the nested exception
	 */
	protected MarshallingException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
