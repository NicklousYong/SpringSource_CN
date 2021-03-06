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

package org.springframework.web.servlet.tags.form;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.jsp.JspException;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * Abstract base class to provide common methods for implementing
 * databinding-aware JSP tags for rendering <i>multiple</i>
 * HTML '{@code input}' elements with a '{@code type}'
 * of '{@code checkbox}' or '{@code radio}'.
 *
 * <p>
 *  抽象基类提供了实现数据绑定感知的JSP标签的常用方法,用于使用"{@code checkbox}"的{@code type}来呈现<i>多个</i> HTML'{@code input}'元素或"{@code radio}
 * "。
 * 
 * 
 * @author Juergen Hoeller
 * @author Scott Andrews
 * @since 2.5.2
 */
@SuppressWarnings("serial")
public abstract class AbstractMultiCheckedElementTag extends AbstractCheckedElementTag {

	/**
	 * The HTML '{@code span}' tag.
	 * <p>
	 * HTML"{@code span}"标签
	 * 
	 */
	private static final String SPAN_TAG = "span";


	/**
	 * The {@link java.util.Collection}, {@link java.util.Map} or array of objects
	 * used to generate the '{@code input type="checkbox/radio"}' tags.
	 * <p>
	 *  {@link javautilCollection},{@link javautilMap}或用于生成"{@code input type ="checkbox / radio"}"标签的对象数组
	 * 
	 */
	private Object items;

	/**
	 * The name of the property mapped to the '{@code value}' attribute
	 * of the '{@code input type="checkbox/radio"}' tag.
	 * <p>
	 *  映射到"{@code input type ="checkbox / radio"}"标签的"{@code value}"属性的属性名称
	 * 
	 */
	private String itemValue;

	/**
	 * The value to be displayed as part of the '{@code input type="checkbox/radio"}' tag.
	 * <p>
	 *  要显示为"{@code input type ="复选框/无线电"}"标签的一部分的值
	 * 
	 */
	private String itemLabel;

	/**
	 * The HTML element used to enclose the '{@code input type="checkbox/radio"}' tag.
	 * <p>
	 *  用于包含"{@code input type ="checkbox / radio"}'标签的HTML元素
	 * 
	 */
	private String element = SPAN_TAG;

	/**
	 * Delimiter to use between each '{@code input type="checkbox/radio"}' tags.
	 * <p>
	 *  每个"{@code input type ="checkbox / radio"}"标签之间使用分隔符
	 * 
	 */
	private String delimiter;


	/**
	 * Set the {@link java.util.Collection}, {@link java.util.Map} or array of objects
	 * used to generate the '{@code input type="checkbox/radio"}' tags.
	 * <p>Typically a runtime expression.
	 * <p>
	 *  设置{@link javautilCollection},{@link javautilMap}或用于生成"{@code input type ="checkbox / radio"}"标签的对象数组
	 * <p>通常,运行时表达式。
	 * 
	 * 
	 * @param items said items
	 */
	public void setItems(Object items) {
		Assert.notNull(items, "'items' must not be null");
		this.items = items;
	}

	/**
	 * Get the {@link java.util.Collection}, {@link java.util.Map} or array of objects
	 * used to generate the '{@code input type="checkbox/radio"}' tags.
	 * <p>
	 * 获取{@link javautilCollection},{@link javautilMap}或用于生成"{@code input type ="checkbox / radio"}"标签的对象数组。
	 * 
	 */
	protected Object getItems() {
		return this.items;
	}

	/**
	 * Set the name of the property mapped to the '{@code value}' attribute
	 * of the '{@code input type="checkbox/radio"}' tag.
	 * <p>May be a runtime expression.
	 * <p>
	 *  设置映射到"{@code input type ="checkbox / radio"}"标签的"{@code value}"属性的属性的名称<p>可以是运行时表达式
	 * 
	 */
	public void setItemValue(String itemValue) {
		Assert.hasText(itemValue, "'itemValue' must not be empty");
		this.itemValue = itemValue;
	}

	/**
	 * Get the name of the property mapped to the '{@code value}' attribute
	 * of the '{@code input type="checkbox/radio"}' tag.
	 * <p>
	 *  获取映射到"{@code input type ="checkbox / radio"}'标签的"{@code value}"属性的属性名称
	 * 
	 */
	protected String getItemValue() {
		return this.itemValue;
	}

	/**
	 * Set the value to be displayed as part of the
	 * '{@code input type="checkbox/radio"}' tag.
	 * <p>May be a runtime expression.
	 * <p>
	 *  将要显示的值设置为"{@code input type ="checkbox / radio"}'标签的一部分<p>可能是运行时表达式
	 * 
	 */
	public void setItemLabel(String itemLabel) {
		Assert.hasText(itemLabel, "'itemLabel' must not be empty");
		this.itemLabel = itemLabel;
	}

	/**
	 * Get the value to be displayed as part of the
	 * '{@code input type="checkbox/radio"}' tag.
	 * <p>
	 *  将该值显示为"{@code input type ="复选框/无线电"}"标签的一部分
	 * 
	 */
	protected String getItemLabel() {
		return this.itemLabel;
	}

	/**
	 * Set the delimiter to be used between each
	 * '{@code input type="checkbox/radio"}' tag.
	 * <p>By default, there is <em>no</em> delimiter.
	 * <p>
	 *  设置每个"{@code input type ="checkbox / radio"}"标签之间使用的分隔符<p>默认情况下,有<em>否</em>分隔符
	 * 
	 */
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	/**
	 * Return the delimiter to be used between each
	 * '{@code input type="radio"}' tag.
	 * <p>
	 * 返回要在每个"{@code input type ="radio"}"标签之间使用的分隔符
	 * 
	 */
	public String getDelimiter() {
		return this.delimiter;
	}

	/**
	 * Set the HTML element used to enclose the
	 * '{@code input type="checkbox/radio"}' tag.
	 * <p>Defaults to an HTML '{@code <span/>}' tag.
	 * <p>
	 *  设置用于包含"{@code input type ="checkbox / radio"}"标签的HTML元素默认为HTML"{@code <span />}"标签
	 * 
	 */
	public void setElement(String element) {
		Assert.hasText(element, "'element' cannot be null or blank");
		this.element = element;
	}

	/**
	 * Get the HTML element used to enclose
	 * '{@code input type="checkbox/radio"}' tag.
	 * <p>
	 *  获取用于包含"{@code input type ="checkbox / radio"}'标签的HTML元素
	 * 
	 */
	public String getElement() {
		return this.element;
	}


	/**
	 * Appends a counter to a specified id as well,
	 * since we're dealing with multiple HTML elements.
	 * <p>
	 *  因为我们处理了多个HTML元素,所以追加一个指定的id的计数器
	 * 
	 */
	@Override
	protected String resolveId() throws JspException {
		Object id = evaluate("id", getId());
		if (id != null) {
			String idString = id.toString();
			return (StringUtils.hasText(idString) ? TagIdGenerator.nextId(idString, this.pageContext) : null);
		}
		return autogenerateId();
	}

	/**
	 * Renders the '{@code input type="radio"}' element with the configured
	 * {@link #setItems(Object)} values. Marks the element as checked if the
	 * value matches the bound value.
	 * <p>
	 *  使用配置的{@link #setItems(Object)}值呈现"{@code input type ="radio"}'元素如果值与绑定值匹配,则将元素标记为已检查
	 */
	@Override
	@SuppressWarnings("rawtypes")
	protected int writeTagContent(TagWriter tagWriter) throws JspException {
		Object items = getItems();
		Object itemsObject = (items instanceof String ? evaluate("items", items) : items);

		String itemValue = getItemValue();
		String itemLabel = getItemLabel();
		String valueProperty =
				(itemValue != null ? ObjectUtils.getDisplayString(evaluate("itemValue", itemValue)) : null);
		String labelProperty =
				(itemLabel != null ? ObjectUtils.getDisplayString(evaluate("itemLabel", itemLabel)) : null);

		Class<?> boundType = getBindStatus().getValueType();
		if (itemsObject == null && boundType != null && boundType.isEnum()) {
			itemsObject = boundType.getEnumConstants();
		}

		if (itemsObject == null) {
			throw new IllegalArgumentException("Attribute 'items' is required and must be a Collection, an Array or a Map");
		}

		if (itemsObject.getClass().isArray()) {
			Object[] itemsArray = (Object[]) itemsObject;
			for (int i = 0; i < itemsArray.length; i++) {
				Object item = itemsArray[i];
				writeObjectEntry(tagWriter, valueProperty, labelProperty, item, i);
			}
		}
		else if (itemsObject instanceof Collection) {
			final Collection<?> optionCollection = (Collection<?>) itemsObject;
			int itemIndex = 0;
			for (Iterator<?> it = optionCollection.iterator(); it.hasNext(); itemIndex++) {
				Object item = it.next();
				writeObjectEntry(tagWriter, valueProperty, labelProperty, item, itemIndex);
			}
		}
		else if (itemsObject instanceof Map) {
			final Map<?, ?> optionMap = (Map<?, ?>) itemsObject;
			int itemIndex = 0;
			for (Iterator it = optionMap.entrySet().iterator(); it.hasNext(); itemIndex++) {
				Map.Entry entry = (Map.Entry) it.next();
				writeMapEntry(tagWriter, valueProperty, labelProperty, entry, itemIndex);
			}
		}
		else {
			throw new IllegalArgumentException("Attribute 'items' must be an array, a Collection or a Map");
		}

		return SKIP_BODY;
	}

	private void writeObjectEntry(TagWriter tagWriter, String valueProperty,
			String labelProperty, Object item, int itemIndex) throws JspException {

		BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(item);
		Object renderValue;
		if (valueProperty != null) {
			renderValue = wrapper.getPropertyValue(valueProperty);
		}
		else if (item instanceof Enum) {
			renderValue = ((Enum<?>) item).name();
		}
		else {
			renderValue = item;
		}
		Object renderLabel = (labelProperty != null ? wrapper.getPropertyValue(labelProperty) : item);
		writeElementTag(tagWriter, item, renderValue, renderLabel, itemIndex);
	}

	private void writeMapEntry(TagWriter tagWriter, String valueProperty,
			String labelProperty, Map.Entry<?, ?> entry, int itemIndex) throws JspException {

		Object mapKey = entry.getKey();
		Object mapValue = entry.getValue();
		BeanWrapper mapKeyWrapper = PropertyAccessorFactory.forBeanPropertyAccess(mapKey);
		BeanWrapper mapValueWrapper = PropertyAccessorFactory.forBeanPropertyAccess(mapValue);
		Object renderValue = (valueProperty != null ?
				mapKeyWrapper.getPropertyValue(valueProperty) : mapKey.toString());
		Object renderLabel = (labelProperty != null ?
				mapValueWrapper.getPropertyValue(labelProperty) : mapValue.toString());
		writeElementTag(tagWriter, mapKey, renderValue, renderLabel, itemIndex);
	}

	private void writeElementTag(TagWriter tagWriter, Object item, Object value, Object label, int itemIndex)
			throws JspException {

		tagWriter.startTag(getElement());
		if (itemIndex > 0) {
			Object resolvedDelimiter = evaluate("delimiter", getDelimiter());
			if (resolvedDelimiter != null) {
				tagWriter.appendValue(resolvedDelimiter.toString());
			}
		}
		tagWriter.startTag("input");
		String id = resolveId();
		writeOptionalAttribute(tagWriter, "id", id);
		writeOptionalAttribute(tagWriter, "name", getName());
		writeOptionalAttributes(tagWriter);
		tagWriter.writeAttribute("type", getInputType());
		renderFromValue(item, value, tagWriter);
		tagWriter.endTag();
		tagWriter.startTag("label");
		tagWriter.writeAttribute("for", id);
		tagWriter.appendValue(convertToDisplayString(label));
		tagWriter.endTag();
		tagWriter.endTag();
	}

}
