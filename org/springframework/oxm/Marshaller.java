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

package org.springframework.oxm;

import java.io.IOException;
import javax.xml.transform.Result;

/**
 * Defines the contract for Object XML Mapping Marshallers. Implementations of this interface
 * can serialize a given Object to an XML Stream.
 *
 * <p>Although the {@code marshal} method accepts a {@code java.lang.Object} as its
 * first parameter, most {@code Marshaller} implementations cannot handle arbitrary
 * {@code Object}s. Instead, a object class must be registered with the marshaller,
 * or have a common base class.
 *
 * <p>
 *  定义对象XML映射编组器的合同此接口的实现可以将给定对象序列化到XML流
 * 
 * <p>尽管{@code marshal}方法接受一个{@code javalangObject}作为其第一个参数,但大部分{@code Marshaller}实现不能处理任意的{@code Object}
 * 而是必须向对象类注册,或者有一个共同的基础类。
 * 
 * 
 * @author Arjen Poutsma
 * @since 3.0
 * @see Unmarshaller
 */
public interface Marshaller {

	/**
	 * Indicate whether this marshaller can marshal instances of the supplied type.
	 * <p>
	 *  指示此编组者是否可以组织提供的类型的实例
	 * 
	 * 
	 * @param clazz the class that this marshaller is being asked if it can marshal
	 * @return {@code true} if this marshaller can indeed marshal instances of the supplied class;
	 * {@code false} otherwise
	 */
	boolean supports(Class<?> clazz);

	/**
	 * Marshal the object graph with the given root into the provided {@link Result}.
	 * <p>
	 *  将给定根的对象图元组织到提供的{@link Result}
	 * 
	 * @param graph the root of the object graph to marshal
	 * @param result the result to marshal to
	 * @throws IOException if an I/O error occurs
	 * @throws XmlMappingException if the given object cannot be marshalled to the result
	 */
	void marshal(Object graph, Result result) throws IOException, XmlMappingException;

}
