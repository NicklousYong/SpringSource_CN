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

package org.springframework.ui;

import java.util.Collection;
import java.util.Map;

/**
 * Java-5-specific interface that defines a holder for model attributes.
 * Primarily designed for adding attributes to the model.
 * Allows for accessing the overall model as a {@code java.util.Map}.
 *
 * <p>
 *  定义模型属性持有者的Java-5特定界面主要用于向模型添加属性允许以{@code javautilMap}访问整个模型,
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.5.1
 */
public interface Model {

	/**
	 * Add the supplied attribute under the supplied name.
	 * <p>
	 * 在提供的名称下添加提供的属性
	 * 
	 * 
	 * @param attributeName the name of the model attribute (never {@code null})
	 * @param attributeValue the model attribute value (can be {@code null})
	 */
	Model addAttribute(String attributeName, Object attributeValue);

	/**
	 * Add the supplied attribute to this {@code Map} using a
	 * {@link org.springframework.core.Conventions#getVariableName generated name}.
	 * <p><emphasis>Note: Empty {@link java.util.Collection Collections} are not added to
	 * the model when using this method because we cannot correctly determine
	 * the true convention name. View code should check for {@code null} rather
	 * than for empty collections as is already done by JSTL tags.</emphasis>
	 * <p>
	 *  使用{@link orgspringframeworkcoreConventions#getVariableName生成的名称}将提供的属性添加到此{@code Map}中<p> <strong>注意
	 * ：使用此方法时,空{@link javautilCollection Collections}不会添加到模型中,因为我们无法正确确定真正约会名称查看代码应该检查{@code null},而不是像JSTL
	 * 标签已经完成的空集合</emphasis>。
	 * 
	 * 
	 * @param attributeValue the model attribute value (never {@code null})
	 */
	Model addAttribute(Object attributeValue);

	/**
	 * Copy all attributes in the supplied {@code Collection} into this
	 * {@code Map}, using attribute name generation for each element.
	 * <p>
	 *  将提供的{@code Collection}中的所有属性复制到此{@code Map}中,使用每个元素的属性名称生成
	 * 
	 * 
	 * @see #addAttribute(Object)
	 */
	Model addAllAttributes(Collection<?> attributeValues);

	/**
	 * Copy all attributes in the supplied {@code Map} into this {@code Map}.
	 * <p>
	 *  将提供的{@code Map}中的所有属性复制到此{@code Map}
	 * 
	 * 
	 * @see #addAttribute(String, Object)
	 */
	Model addAllAttributes(Map<String, ?> attributes);

	/**
	 * Copy all attributes in the supplied {@code Map} into this {@code Map},
	 * with existing objects of the same name taking precedence (i.e. not getting
	 * replaced).
	 * <p>
	 * 将提供的{@code Map}中的所有属性复制到此{@code Map}中,同名的现有对象优先(即不被替换)
	 * 
	 */
	Model mergeAttributes(Map<String, ?> attributes);

	/**
	 * Does this model contain an attribute of the given name?
	 * <p>
	 *  该模型是否包含给定名称的属性?
	 * 
	 * 
	 * @param attributeName the name of the model attribute (never {@code null})
	 * @return whether this model contains a corresponding attribute
	 */
	boolean containsAttribute(String attributeName);

	/**
	 * Return the current set of model attributes as a Map.
	 * <p>
	 *  将当前的模型属性集返回给Map
	 */
	Map<String, Object> asMap();

}
