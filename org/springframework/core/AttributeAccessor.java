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
 * Interface defining a generic contract for attaching and accessing metadata
 * to/from arbitrary objects.
 *
 * <p>
 *  界面定义用于附加和访问任意对象的元数据的通用合同
 * 
 * 
 * @author Rob Harrop
 * @since 2.0
 */
public interface AttributeAccessor {

	/**
	 * Set the attribute defined by {@code name} to the supplied	{@code value}.
	 * If {@code value} is {@code null}, the attribute is {@link #removeAttribute removed}.
	 * <p>In general, users should take care to prevent overlaps with other
	 * metadata attributes by using fully-qualified names, perhaps using
	 * class or package names as prefix.
	 * <p>
	 * 将{@code name}定义的属性设置为提供的{@code值}如果{@code值}为{@code null},则属性为{@link #removeAttribute removed} <p>一般来说,
	 * 用户应该通过使用完全限定名称,可能使用类或包名称作为前缀,防止与其他元数据属性重叠。
	 * 
	 * 
	 * @param name the unique attribute key
	 * @param value the attribute value to be attached
	 */
	void setAttribute(String name, Object value);

	/**
	 * Get the value of the attribute identified by {@code name}.
	 * Return {@code null} if the attribute doesn't exist.
	 * <p>
	 *  如果属性不存在,则获取{@code name} Return {@code null}标识的属性的值
	 * 
	 * 
	 * @param name the unique attribute key
	 * @return the current value of the attribute, if any
	 */
	Object getAttribute(String name);

	/**
	 * Remove the attribute identified by {@code name} and return its value.
	 * Return {@code null} if no attribute under {@code name} is found.
	 * <p>
	 *  删除{@code name}标识的属性并返回其值Return {@code null},如果没有找到{@code name}下的属性
	 * 
	 * 
	 * @param name the unique attribute key
	 * @return the last value of the attribute, if any
	 */
	Object removeAttribute(String name);

	/**
	 * Return {@code true} if the attribute identified by {@code name} exists.
	 * Otherwise return {@code false}.
	 * <p>
	 *  如果{@code name}标识的属性存在返回{@code true}否则返回{@code false}
	 * 
	 * 
	 * @param name the unique attribute key
	 */
	boolean hasAttribute(String name);

	/**
	 * Return the names of all attributes.
	 * <p>
	 *  返回所有属性的名称
	 */
	String[] attributeNames();

}
