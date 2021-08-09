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

package org.springframework.web.servlet.tags.form;

import javax.servlet.jsp.JspException;

import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.util.TagUtils;

/**
 * Convenient tag that allows one to supply a collection of objects
 * that are to be rendered as '{@code option}' tags within a
 * '{@code select}' tag.
 *
 * <p><i>Must</i> be used within a {@link SelectTag 'select' tag}.
 *
 * <p>
 *  方便的标签,允许人们在"{@code select}"标签中提供要呈现为"{@code选项}"标签的对象集合
 * 
 *  <p> <i>必须在{@link SelectTag'select'tag}内使用</i>
 * 
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Scott Andrews
 * @since 2.0
 */
@SuppressWarnings("serial")
public class OptionsTag extends AbstractHtmlElementTag {

	/**
	 * The {@link java.util.Collection}, {@link java.util.Map} or array of
	 * objects used to generate the inner '{@code option}' tags.
	 * <p>
	 * 用于生成内部"{@code选项}"标签的{@link javautilCollection},{@link javautilMap}或数组的对象
	 * 
	 */
	private Object items;

	/**
	 * The name of the property mapped to the '{@code value}' attribute
	 * of the '{@code option}' tag.
	 * <p>
	 *  属性的名称映射到"{@code选项}"标签的"{@code value}"属性
	 * 
	 */
	private String itemValue;

	/**
	 * The name of the property mapped to the inner text of the
	 * '{@code option}' tag.
	 * <p>
	 *  映射到"{@code选项}"标签内部文本的属性的名称
	 * 
	 */
	private String itemLabel;

	private boolean disabled;


	/**
	 * Set the {@link java.util.Collection}, {@link java.util.Map} or array
	 * of objects used to generate the inner '{@code option}' tags.
	 * <p>Required when wishing to render '{@code option}' tags from an
	 * array, {@link java.util.Collection} or {@link java.util.Map}.
	 * <p>Typically a runtime expression.
	 * <p>
	 *  设置{@link javautilCollection},{@link javautilMap}或用于生成内部"{@code选项}"标签的对象数组<p>当要从数组中呈现"{@code选项}"标签时, 
	 * {@link javautilCollection}或{@link javautilMap} <p>通常是运行时表达式。
	 * 
	 */
	public void setItems(Object items) {
		this.items = items;
	}

	/**
	 * Get the {@link java.util.Collection}, {@link java.util.Map} or array
	 * of objects used to generate the inner '{@code option}' tags.
	 * <p>Typically a runtime expression.
	 * <p>
	 *  获取{@link javautilCollection},{@link javautilMap}或用于生成内部"{@code选项}"标签的对象数组<p>通常,运行时表达式
	 * 
	 */
	protected Object getItems() {
		return this.items;
	}

	/**
	 * Set the name of the property mapped to the '{@code value}'
	 * attribute of the '{@code option}' tag.
	 * <p>Required when wishing to render '{@code option}' tags from
	 * an array or {@link java.util.Collection}.
	 * <p>
	 * 设置映射到"{@code选项}"标签的"{@code value}"属性的属性的名称<p>当要从数组或{@link中呈现"{@code选项}"标签时,必需javautilCollection}
	 * 
	 */
	public void setItemValue(String itemValue) {
		Assert.hasText(itemValue, "'itemValue' must not be empty");
		this.itemValue = itemValue;
	}

	/**
	 * Return the name of the property mapped to the '{@code value}'
	 * attribute of the '{@code option}' tag.
	 * <p>
	 *  返回映射到"{@code option}"标签的"{@code value}"属性的属性名称
	 * 
	 */
	protected String getItemValue() {
		return this.itemValue;
	}

	/**
	 * Set the name of the property mapped to the label (inner text) of the
	 * '{@code option}' tag.
	 * <p>
	 *  设置映射到"{@code选项}"标签的标签(内部文本)的属性的名称
	 * 
	 */
	public void setItemLabel(String itemLabel) {
		Assert.hasText(itemLabel, "'itemLabel' must not be empty");
		this.itemLabel = itemLabel;
	}

	/**
	 * Get the name of the property mapped to the label (inner text) of the
	 * '{@code option}' tag.
	 * <p>
	 *  获取映射到"{@code选项}"标签的标签(内部文本)的属性名称
	 * 
	 */
	protected String getItemLabel() {
		return this.itemLabel;
	}

	/**
	 * Set the value of the '{@code disabled}' attribute.
	 * <p>
	 *  设置"{@code disabled}"属性的值
	 * 
	 */
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	/**
	 * Get the value of the '{@code disabled}' attribute.
	 * <p>
	 *  获取"{@code disabled}"属性的值
	 * 
	 */
	protected boolean isDisabled() {
		return this.disabled;
	}


	@Override
	protected int writeTagContent(TagWriter tagWriter) throws JspException {
		SelectTag selectTag = getSelectTag();
		Object items = getItems();
		Object itemsObject = null;
		if (items != null) {
			itemsObject = (items instanceof String ? evaluate("items", items) : items);
		}
		else {
			Class<?> selectTagBoundType = selectTag.getBindStatus().getValueType();
			if (selectTagBoundType != null && selectTagBoundType.isEnum()) {
				itemsObject = selectTagBoundType.getEnumConstants();
			}
		}
		if (itemsObject != null) {
			String selectName = selectTag.getName();
			String itemValue = getItemValue();
			String itemLabel = getItemLabel();
			String valueProperty =
					(itemValue != null ? ObjectUtils.getDisplayString(evaluate("itemValue", itemValue)) : null);
			String labelProperty =
					(itemLabel != null ? ObjectUtils.getDisplayString(evaluate("itemLabel", itemLabel)) : null);
			OptionsWriter optionWriter = new OptionsWriter(selectName, itemsObject, valueProperty, labelProperty);
			optionWriter.writeOptions(tagWriter);
		}
		return SKIP_BODY;
	}

	/**
	 * Appends a counter to a specified id,
	 * since we're dealing with multiple HTML elements.
	 * <p>
	 *  追加一个指定的ID的计数器,因为我们处理了多个HTML元素
	 * 
	 */
	@Override
	protected String resolveId() throws JspException {
		Object id = evaluate("id", getId());
		if (id != null) {
			String idString = id.toString();
			return (StringUtils.hasText(idString) ? TagIdGenerator.nextId(idString, this.pageContext) : null);
		}
		return null;
	}

	private SelectTag getSelectTag() {
		TagUtils.assertHasAncestorOfType(this, SelectTag.class, "options", "select");
		return (SelectTag) findAncestorWithClass(this, SelectTag.class);
	}

	@Override
	protected BindStatus getBindStatus() {
		return (BindStatus) this.pageContext.getAttribute(SelectTag.LIST_VALUE_PAGE_ATTRIBUTE);
	}


	/**
	 * Inner class that adapts OptionWriter for multiple options to be rendered.
	 * <p>
	 *  内部类可以使OptionWriter适应多个选项来呈现
	 */
	private class OptionsWriter extends OptionWriter {

		private final String selectName;

		public OptionsWriter(String selectName, Object optionSource, String valueProperty, String labelProperty) {
			super(optionSource, getBindStatus(), valueProperty, labelProperty, isHtmlEscape());
			this.selectName = selectName;
		}

		@Override
		protected boolean isOptionDisabled() throws JspException {
			return isDisabled();
		}

		@Override
		protected void writeCommonAttributes(TagWriter tagWriter) throws JspException {
			writeOptionalAttribute(tagWriter, "id", resolveId());
			writeOptionalAttributes(tagWriter);
		}

		@Override
		protected String processOptionValue(String value) {
			return processFieldValue(this.selectName, value, "option");
		}

	}

}
