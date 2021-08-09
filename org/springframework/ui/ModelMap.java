/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2013 the original author or authors.
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
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.core.Conventions;
import org.springframework.util.Assert;

/**
 * Implementation of {@link java.util.Map} for use when building model data for use
 * with UI tools. Supports chained calls and generation of model attribute names.
 *
 * <p>This class serves as generic model holder for both Servlet and Portlet MVC,
 * but is not tied to either of those. Check out the {@link Model} interface for
 * a Java-5-based interface variant that serves the same purpose.
 *
 * <p>
 *  在构建用于UI工具的模型数据时使用的{@link javautilMap}的实现支持链接调用和生成模型属性名称
 * 
 * <p>这个类作为Servlet和Portlet MVC的通用模型持有者,但并不绑定到任何一个。检查{@link Model}接口为基于Java-5的接口变体提供同样的用途
 * 
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 * @see Conventions#getVariableName
 * @see org.springframework.web.servlet.ModelAndView
 * @see org.springframework.web.portlet.ModelAndView
 */
@SuppressWarnings("serial")
public class ModelMap extends LinkedHashMap<String, Object> {

	/**
	 * Construct a new, empty {@code ModelMap}.
	 * <p>
	 *  构造一个新的,空的{@code ModelMap}
	 * 
	 */
	public ModelMap() {
	}

	/**
	 * Construct a new {@code ModelMap} containing the supplied attribute
	 * under the supplied name.
	 * <p>
	 *  在提供的名称下构造一个包含提供的属性的新的{@code ModelMap}
	 * 
	 * 
	 * @see #addAttribute(String, Object)
	 */
	public ModelMap(String attributeName, Object attributeValue) {
		addAttribute(attributeName, attributeValue);
	}

	/**
	 * Construct a new {@code ModelMap} containing the supplied attribute.
	 * Uses attribute name generation to generate the key for the supplied model
	 * object.
	 * <p>
	 *  构造一个新的{@code ModelMap},其中包含所提供的属性使用属性名称生成来生成提供的模型对象的键
	 * 
	 * 
	 * @see #addAttribute(Object)
	 */
	public ModelMap(Object attributeValue) {
		addAttribute(attributeValue);
	}


	/**
	 * Add the supplied attribute under the supplied name.
	 * <p>
	 *  在提供的名称下添加提供的属性
	 * 
	 * 
	 * @param attributeName the name of the model attribute (never {@code null})
	 * @param attributeValue the model attribute value (can be {@code null})
	 */
	public ModelMap addAttribute(String attributeName, Object attributeValue) {
		Assert.notNull(attributeName, "Model attribute name must not be null");
		put(attributeName, attributeValue);
		return this;
	}

	/**
	 * Add the supplied attribute to this {@code Map} using a
	 * {@link org.springframework.core.Conventions#getVariableName generated name}.
	 * <p><emphasis>Note: Empty {@link Collection Collections} are not added to
	 * the model when using this method because we cannot correctly determine
	 * the true convention name. View code should check for {@code null} rather
	 * than for empty collections as is already done by JSTL tags.</emphasis>
	 * <p>
	 * 使用{@link orgspringframeworkcoreConventions#getVariableName生成的名称}将提供的属性添加到此{@code Map} <p> <strong>注意：
	 * 使用此方法时,空{@link集合集合}不会添加到模型中,因为我们无法正确确定真正约会名称查看代码应该检查{@code null},而不是像JSTL标签已经完成的空集合</emphasis>。
	 * 
	 * 
	 * @param attributeValue the model attribute value (never {@code null})
	 */
	public ModelMap addAttribute(Object attributeValue) {
		Assert.notNull(attributeValue, "Model object must not be null");
		if (attributeValue instanceof Collection && ((Collection<?>) attributeValue).isEmpty()) {
			return this;
		}
		return addAttribute(Conventions.getVariableName(attributeValue), attributeValue);
	}

	/**
	 * Copy all attributes in the supplied {@code Collection} into this
	 * {@code Map}, using attribute name generation for each element.
	 * <p>
	 *  将提供的{@code Collection}中的所有属性复制到此{@code Map}中,使用每个元素的属性名称生成
	 * 
	 * 
	 * @see #addAttribute(Object)
	 */
	public ModelMap addAllAttributes(Collection<?> attributeValues) {
		if (attributeValues != null) {
			for (Object attributeValue : attributeValues) {
				addAttribute(attributeValue);
			}
		}
		return this;
	}

	/**
	 * Copy all attributes in the supplied {@code Map} into this {@code Map}.
	 * <p>
	 *  将提供的{@code Map}中的所有属性复制到此{@code Map}
	 * 
	 * 
	 * @see #addAttribute(String, Object)
	 */
	public ModelMap addAllAttributes(Map<String, ?> attributes) {
		if (attributes != null) {
			putAll(attributes);
		}
		return this;
	}

	/**
	 * Copy all attributes in the supplied {@code Map} into this {@code Map},
	 * with existing objects of the same name taking precedence (i.e. not getting
	 * replaced).
	 * <p>
	 *  将提供的{@code Map}中的所有属性复制到此{@code Map}中,同名的现有对象优先(即不被替换)
	 * 
	 */
	public ModelMap mergeAttributes(Map<String, ?> attributes) {
		if (attributes != null) {
			for (Map.Entry<String, ?> entry : attributes.entrySet()) {
				String key = entry.getKey();
				if (!containsKey(key)) {
					put(key, entry.getValue());
				}
			}
		}
		return this;
	}

	/**
	 * Does this model contain an attribute of the given name?
	 * <p>
	 * 该模型是否包含给定名称的属性?
	 * 
	 * @param attributeName the name of the model attribute (never {@code null})
	 * @return whether this model contains a corresponding attribute
	 */
	public boolean containsAttribute(String attributeName) {
		return containsKey(attributeName);
	}

}
