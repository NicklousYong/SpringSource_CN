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

package org.springframework.beans.factory.support;

import org.springframework.beans.BeanMetadataAttributeAccessor;
import org.springframework.util.Assert;

/**
 * Qualifier for resolving autowire candidates. A bean definition that
 * includes one or more such qualifiers enables fine-grained matching
 * against annotations on a field or parameter to be autowired.
 *
 * <p>
 *  用于解析自动线程候选的限定符包含一个或多个此类限定符的bean定义可以对字段或参数上的注释进行细粒度匹配以进行自动连线
 * 
 * 
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @since 2.5
 * @see org.springframework.beans.factory.annotation.Qualifier
 */
@SuppressWarnings("serial")
public class AutowireCandidateQualifier extends BeanMetadataAttributeAccessor {

	public static String VALUE_KEY = "value";

	private final String typeName;


	/**
	 * Construct a qualifier to match against an annotation of the
	 * given type.
	 * <p>
	 * 构造一个限定符,以匹配给定类型的注释
	 * 
	 * 
	 * @param type the annotation type
	 */
	public AutowireCandidateQualifier(Class<?> type) {
		this(type.getName());
	}

	/**
	 * Construct a qualifier to match against an annotation of the
	 * given type name.
	 * <p>The type name may match the fully-qualified class name of
	 * the annotation or the short class name (without the package).
	 * <p>
	 *  构造一个限定词以匹配给定类型名称的注释<p>类型名称可能与注释的完全限定类名称或短类名称(不包含)匹配)
	 * 
	 * 
	 * @param typeName the name of the annotation type
	 */
	public AutowireCandidateQualifier(String typeName) {
		Assert.notNull(typeName, "Type name must not be null");
		this.typeName = typeName;
	}

	/**
	 * Construct a qualifier to match against an annotation of the
	 * given type whose {@code value} attribute also matches
	 * the specified value.
	 * <p>
	 *  构造一个限定符,使其与{@code value}属性也与指定值匹配的给定类型的注释进行匹配
	 * 
	 * 
	 * @param type the annotation type
	 * @param value the annotation value to match
	 */
	public AutowireCandidateQualifier(Class<?> type, Object value) {
		this(type.getName(), value);
	}

	/**
	 * Construct a qualifier to match against an annotation of the
	 * given type name whose {@code value} attribute also matches
	 * the specified value.
	 * <p>The type name may match the fully-qualified class name of
	 * the annotation or the short class name (without the package).
	 * <p>
	 *  构造一个限定符,以匹配给定类型名称的注释,该类型名称的{@code值}属性也与指定的值匹配<p>类型名称可能与注释的全限定类名称或短类名称匹配(不含包)
	 * 
	 * 
	 * @param typeName the name of the annotation type
	 * @param value the annotation value to match
	 */
	public AutowireCandidateQualifier(String typeName, Object value) {
		Assert.notNull(typeName, "Type name must not be null");
		this.typeName = typeName;
		setAttribute(VALUE_KEY, value);
	}


	/**
	 * Retrieve the type name. This value will be the same as the
	 * type name provided to the constructor or the fully-qualified
	 * class name if a Class instance was provided to the constructor.
	 * <p>
	 * 检索类型名称此值将与提供给构造函数的类型名称或完全限定类名称相同,如果将Class实例提供给构造函数
	 */
	public String getTypeName() {
		return this.typeName;
	}

}
