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

import java.util.Collection;
import java.util.Map;
import javax.servlet.jsp.JspException;

import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.support.BindStatus;

/**
 * Databinding-aware JSP tag that renders an HTML '{@code select}'
 * element.
 *
 * <p>Inner '{@code option}' tags can be rendered using one of the
 * approaches supported by the OptionWriter class.
 *
 * <p>Also supports the use of nested {@link OptionTag OptionTags} or
 * (typically one) nested {@link OptionsTag}.
 *
 * <p>
 *  数据绑定感知的JSP标签,用于呈现HTML"{@code select}"元素
 * 
 *  <p>可以使用OptionWriter类支持的方法之一呈现内部{@code选项}'标签
 * 
 * <p>还支持使用嵌套的{@link OptionTag OptionTags}或(通常为一个)嵌套{@link OptionsTag}
 * 
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 * @see OptionTag
 */
@SuppressWarnings("serial")
public class SelectTag extends AbstractHtmlInputElementTag {

	/**
	 * The {@link javax.servlet.jsp.PageContext} attribute under
	 * which the bound value is exposed to inner {@link OptionTag OptionTags}.
	 * <p>
	 *  {@link javaxservletjspPageContext}属性,绑定值暴露在内部{@link OptionTag OptionTags}
	 * 
	 */
	public static final String LIST_VALUE_PAGE_ATTRIBUTE =
			"org.springframework.web.servlet.tags.form.SelectTag.listValue";

	/**
	 * Marker object for items that have been specified but resolve to null.
	 * Allows to differentiate between 'set but null' and 'not set at all'.
	 * <p>
	 *  已指定但解决为null的项目的标记对象允许区分"设置但为空"和"全部未设置"
	 * 
	 */
	private static final Object EMPTY = new Object();


	/**
	 * The {@link Collection}, {@link Map} or array of objects used to generate the inner
	 * '{@code option}' tags.
	 * <p>
	 *  {@link Collection},{@link Map}或用于生成内部"{@code选项}"标签的对象数组
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

	/**
	 * The value of the HTML '{@code size}' attribute rendered
	 * on the final '{@code select}' element.
	 * <p>
	 *  HTML'{@code size}'属性的值在最终的"{@code select}"元素上呈现
	 * 
	 */
	private String size;

	/**
	 * Indicates whether or not the '{@code select}' tag allows
	 * multiple-selections.
	 * <p>
	 * 指示"{@code select}"标签是否允许多重选择
	 * 
	 */
	private Object multiple;

	/**
	 * The {@link TagWriter} instance that the output is being written.
	 * <p>Only used in conjunction with nested {@link OptionTag OptionTags}.
	 * <p>
	 *  正在写入输出的{@link TagWriter}实例<p>仅与嵌套的{@link OptionTag OptionTags}结合使用
	 * 
	 */
	private TagWriter tagWriter;


	/**
	 * Set the {@link Collection}, {@link Map} or array of objects used to
	 * generate the inner '{@code option}' tags.
	 * <p>Required when wishing to render '{@code option}' tags from
	 * an array, {@link Collection} or {@link Map}.
	 * <p>Typically a runtime expression.
	 * <p>
	 *  设置{@link Collection},{@link Map}或用于生成内部"{@code选项}"标签的对象数组<p>如果希望从数组中呈现"{@code选项}"标签,则为{ {@link Collection}
	 * 或{@link Map} <p>通常是运行时表达式。
	 * 
	 * 
	 * @param items the items that comprise the options of this selection
	 */
	public void setItems(Object items) {
		this.items = (items != null ? items : EMPTY);
	}

	/**
	 * Get the value of the '{@code items}' attribute.
	 * <p>May be a runtime expression.
	 * <p>
	 *  获取"{@code items}"属性的值<p>可以是运行时表达式
	 * 
	 */
	protected Object getItems() {
		return this.items;
	}

	/**
	 * Set the name of the property mapped to the '{@code value}'
	 * attribute of the '{@code option}' tag.
	 * <p>Required when wishing to render '{@code option}' tags from
	 * an array or {@link Collection}.
	 * <p>May be a runtime expression.
	 * <p>
	 *  设置映射到"{@code选项}"标签的"{@code value}"属性的属性的名称<p>当要从数组或{@link中呈现"{@code选项}"标签时,必需集合} <p>可能是一个运行时表达式
	 * 
	 */
	public void setItemValue(String itemValue) {
		this.itemValue = itemValue;
	}

	/**
	 * Get the value of the '{@code itemValue}' attribute.
	 * <p>May be a runtime expression.
	 * <p>
	 * 获取"{@code itemValue}"属性的值<p>可以是运行时表达式
	 * 
	 */
	protected String getItemValue() {
		return this.itemValue;
	}

	/**
	 * Set the name of the property mapped to the label (inner text) of the
	 * '{@code option}' tag.
	 * <p>May be a runtime expression.
	 * <p>
	 *  设置映射到"{@code选项"}标签的标签(内部文本)的属性的名称<p>可以是运行时表达式
	 * 
	 */
	public void setItemLabel(String itemLabel) {
		this.itemLabel = itemLabel;
	}

	/**
	 * Get the value of the '{@code itemLabel}' attribute.
	 * <p>May be a runtime expression.
	 * <p>
	 *  获取"{@code itemLabel}"属性的值<p>可以是运行时表达式
	 * 
	 */
	protected String getItemLabel() {
		return this.itemLabel;
	}

	/**
	 * Set the value of the HTML '{@code size}' attribute rendered
	 * on the final '{@code select}' element.
	 * <p>
	 *  设置在最终的"{@code select}"元素上呈现的HTML"{@code size}"属性的值
	 * 
	 */
	public void setSize(String size) {
		this.size = size;
	}

	/**
	 * Get the value of the '{@code size}' attribute.
	 * <p>
	 *  获取"{@code size}"属性的值
	 * 
	 */
	protected String getSize() {
		return this.size;
	}

	/**
	 * Set the value of the HTML '{@code multiple}' attribute rendered
	 * on the final '{@code select}' element.
	 * <p>
	 *  设置最终"{@code select}"元素上呈现的HTML"{@code multiple}"属性的值
	 * 
	 */
	public void setMultiple(Object multiple) {
		this.multiple = multiple;
	}

	/**
	 * Get the value of the HTML '{@code multiple}' attribute rendered
	 * on the final '{@code select}' element.
	 * <p>
	 *  获取最终"{@code select}"元素上呈现的HTML"{@code multiple}"属性的值
	 * 
	 */
	protected Object getMultiple() {
		return this.multiple;
	}


	/**
	 * Renders the HTML '{@code select}' tag to the supplied
	 * {@link TagWriter}.
	 * <p>Renders nested '{@code option}' tags if the
	 * {@link #setItems items} property is set, otherwise exposes the
	 * bound value for the nested {@link OptionTag OptionTags}.
	 * <p>
	 * 如果设置了{@link #setItems items}属性,则将HTML"{@code select}"标签呈现给提供的{@link TagWriter} <p>渲染嵌套的"{@code选项}"标签,
	 * 否则显示绑定值对于嵌套的{@link OptionTag OptionTags}。
	 * 
	 */
	@Override
	protected int writeTagContent(TagWriter tagWriter) throws JspException {
		tagWriter.startTag("select");
		writeDefaultAttributes(tagWriter);
		if (isMultiple()) {
			tagWriter.writeAttribute("multiple", "multiple");
		}
		tagWriter.writeOptionalAttributeValue("size", getDisplayString(evaluate("size", getSize())));

		Object items = getItems();
		if (items != null) {
			// Items specified, but might still be empty...
			if (items != EMPTY) {
				Object itemsObject = evaluate("items", items);
				if (itemsObject != null) {
					final String selectName = getName();
					String valueProperty = (getItemValue() != null ?
							ObjectUtils.getDisplayString(evaluate("itemValue", getItemValue())) : null);
					String labelProperty = (getItemLabel() != null ?
							ObjectUtils.getDisplayString(evaluate("itemLabel", getItemLabel())) : null);
					OptionWriter optionWriter =
							new OptionWriter(itemsObject, getBindStatus(), valueProperty, labelProperty, isHtmlEscape()) {
								@Override
								protected String processOptionValue(String resolvedValue) {
									return processFieldValue(selectName, resolvedValue, "option");
								}
							};
					optionWriter.writeOptions(tagWriter);
				}
			}
			tagWriter.endTag(true);
			writeHiddenTagIfNecessary(tagWriter);
			return SKIP_BODY;
		}
		else {
			// Using nested <form:option/> tags, so just expose the value in the PageContext...
			tagWriter.forceBlock();
			this.tagWriter = tagWriter;
			this.pageContext.setAttribute(LIST_VALUE_PAGE_ATTRIBUTE, getBindStatus());
			return EVAL_BODY_INCLUDE;
		}
	}

	/**
	 * If using a multi-select, a hidden element is needed to make sure all
	 * items are correctly unselected on the server-side in response to a
	 * {@code null} post.
	 * <p>
	 *  如果使用多选,则需要隐藏的元素来确保在服务器端正确地取消所有项目,以响应{@code null}帖子
	 * 
	 */
	private void writeHiddenTagIfNecessary(TagWriter tagWriter) throws JspException {
		if (isMultiple()) {
			tagWriter.startTag("input");
			tagWriter.writeAttribute("type", "hidden");
			String name = WebDataBinder.DEFAULT_FIELD_MARKER_PREFIX + getName();
			tagWriter.writeAttribute("name", name);
			tagWriter.writeAttribute("value", processFieldValue(name, "1", "hidden"));
			tagWriter.endTag();
		}
	}

	private boolean isMultiple() throws JspException {
		Object multiple = getMultiple();
		if (multiple != null) {
			String stringValue = multiple.toString();
			return ("multiple".equalsIgnoreCase(stringValue) || Boolean.parseBoolean(stringValue));
		}
		return forceMultiple();
	}

	/**
	 * Returns '{@code true}' if the bound value requires the
	 * resultant '{@code select}' tag to be multi-select.
	 * <p>
	 *  如果绑定值要求结果"{@code select}"标签进行多选,返回"{@code true}"
	 * 
	 */
	private boolean forceMultiple() throws JspException {
		BindStatus bindStatus = getBindStatus();
		Class<?> valueType = bindStatus.getValueType();
		if (valueType != null && typeRequiresMultiple(valueType)) {
			return true;
		}
		else if (bindStatus.getEditor() != null) {
			Object editorValue = bindStatus.getEditor().getValue();
			if (editorValue != null && typeRequiresMultiple(editorValue.getClass())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns '{@code true}' for arrays, {@link Collection Collections}
	 * and {@link Map Maps}.
	 * <p>
	 *  返回数组的{@code true},{@link集合集合}和{@link Map Maps}
	 * 
	 */
	private static boolean typeRequiresMultiple(Class<?> type) {
		return (type.isArray() || Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type));
	}

	/**
	 * Closes any block tag that might have been opened when using
	 * nested {@link OptionTag options}.
	 * <p>
	 *  关闭使用嵌套的{@link OptionTag选项}时可能已打开的任何块标记
	 * 
	 */
	@Override
	public int doEndTag() throws JspException {
		if (this.tagWriter != null) {
			this.tagWriter.endTag();
			writeHiddenTagIfNecessary(tagWriter);
		}
		return EVAL_PAGE;
	}

	/**
	 * Clears the {@link TagWriter} that might have been left over when using
	 * nested {@link OptionTag options}.
	 * <p>
	 *  清除使用嵌套的{@link选项标签选项}时可能遗留的{@link TagWriter}
	 */
	@Override
	public void doFinally() {
		super.doFinally();
		this.tagWriter = null;
		this.pageContext.removeAttribute(LIST_VALUE_PAGE_ATTRIBUTE);
	}

}
