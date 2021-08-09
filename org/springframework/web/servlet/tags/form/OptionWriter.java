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

import java.beans.PropertyEditor;
import java.util.Collection;
import java.util.Map;
import javax.servlet.jsp.JspException;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.support.BindStatus;

/**
 * Provides supporting functionality to render a list of '{@code option}'
 * tags based on some source object. This object can be either an array, a
 * {@link Collection}, or a {@link Map}.
 * <h3>Using an array or a {@link Collection}:</h3>
 * <p>
 * If you supply an array or {@link Collection} source object to render the
 * inner '{@code option}' tags, you may optionally specify the name of
 * the property on the objects which corresponds to the <em>value</em> of the
 * rendered '{@code option}' (i.e., the {@code valueProperty})
 * and the name of the property that corresponds to the <em>label</em> (i.e.,
 * the {@code labelProperty}). These properties are then used when
 * rendering each element of the array/{@link Collection} as an '{@code option}'.
 * If either property name is omitted, the value of {@link Object#toString()} of
 * the corresponding array/{@link Collection} element is used instead.  However,
 * if the item is an enum, {@link Enum#name()} is used as the default value.
 * </p>
 * <h3>Using a {@link Map}:</h3>
 * <p>
 * You can alternatively choose to render '{@code option}' tags by
 * supplying a {@link Map} as the source object.
 * </p>
 * <p>
 * If you <strong>omit</strong> property names for the <em>value</em> and
 * <em>label</em>:
 * </p>
 * <ul>
 * <li>the {@code key} of each {@link Map} entry will correspond to the
 * <em>value</em> of the rendered '{@code option}', and</li>
 * <li>the {@code value} of each {@link Map} entry will correspond to
 * the <em>label</em> of the rendered '{@code option}'.</li>
 * </ul>
 * <p>
 * If you <strong>supply</strong> property names for the <em>value</em> and
 * <em>label</em>:
 * </p>
 * <ul>
 * <li>the <em>value</em> of the rendered '{@code option}' will be
 * retrieved from the {@code valueProperty} on the object
 * corresponding to the {@code key} of each {@link Map} entry, and</li>
 * <li>the <em>label</em> of the rendered '{@code option}' will be
 * retrieved from the {@code labelProperty} on the object
 * corresponding to the {@code value} of each {@link Map} entry.
 * </ul>
 * <h3>When using either of these approaches:</h3>
 * <ul>
 * <li>Property names for the <em>value</em> and <em>label</em> are
 * specified as arguments to the
 * {@link #OptionWriter(Object, BindStatus, String, String, boolean) constructor}.</li>
 * <li>An '{@code option}' is marked as 'selected' if its key
 * {@link #isOptionSelected matches} the value that is bound to the tag instance.</li>
 * </ul>
 *
 * <p>
 * 提供支持功能,以基于某些源对象呈现"{@code选项}"标签列表此对象可以是数组,{@link Collection}或{@link Map} <h3>使用数组或{@link Collection}：</h3>
 * 。
 * <p>
 * 如果您提供数组或{@link Collection}源对象来呈现内部的"{@code选项}"标签,则可以选择在与<em>值对应的对象上指定属性的名称</em>的渲染"{@code选项}"(即{@code valueProperty}
 * )和与<em>标签</em>对应的属性名称(即{@code labelProperty}))这些属性然后在将数组/ {@ link Collection}的每个元素渲染为"{@code选项}"时使用。
 * 如果省略任一属性名称,则相应数组的{@link Object#toString()}的值为{使用@link Collection}元素但是,如果项目是枚举,则使用{@link Enum#name()}作
 * 为默认值。
 * </p>
 *  <h3>使用{@link地图}：</h3>
 * <p>
 * 您还可以选择通过提供{@link Map}作为源对象来呈现"{@code选项}"标签
 * </p>
 * <p>
 *  如果您<strong>省略<em> </em>和<em>标签</em>的</strong>属性名称：
 * </p>
 * <ul>
 *  每个{@link Map}条目的{@code key}将对应于渲染的"{@code选项"}的<em>值</em>,</li> <li> {每个{@link Map}条目的@code值}将对应于渲染的"
 * {@code选项}"</li>的<em>标签</em>。
 * </ul>
 * <p>
 *  如果<strong>为<em> </em>和<em>标签</em>提供</strong>属性名称：
 * </p>
 * <ul>
 * 将从每个{@link Map}的{@code键}对应的对象的{@code valueProperty}中检索呈现的"{@code选项}"的<em>值</em>条目和</li> <li>将从与{@code值对应的对象上的{@code labelProperty}
 * 中检索呈现的"{@code选项}"的<em>标签</em>每个{@link Map}条目。
 * </ul>
 *  <h3>使用以下任一方法时：</h3>
 * <ul>
 *  <li> </em>和<em>标签</em>的属性名称被指定为{@link #OptionWriter(Object,BindStatus,String,String,boolean))构造函数的参数}
 *  li> <li>如果{@link #isOptionSelected匹配}绑定到标记实例的值,则"{@code选项}"标记为"已选择"</li>。
 * </ul>
 * 
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Scott Andrews
 * @since 2.0
 */
class OptionWriter {

	private final Object optionSource;

	private final BindStatus bindStatus;

	private final String valueProperty;

	private final String labelProperty;

	private final boolean htmlEscape;


	/**
	 * Creates a new {@code OptionWriter} for the supplied {@code objectSource}.
	 * <p>
	 * 为所提供的{@code objectSource}创建一个新的{@code OptionWriter}
	 * 
	 * 
	 * @param optionSource the source of the {@code options} (never {@code null})
	 * @param bindStatus the {@link BindStatus} for the bound value (never {@code null})
	 * @param valueProperty the name of the property used to render {@code option} values
	 * (optional)
	 * @param labelProperty the name of the property used to render {@code option} labels
	 * (optional)
	 */
	public OptionWriter(
			Object optionSource, BindStatus bindStatus, String valueProperty, String labelProperty, boolean htmlEscape) {

		Assert.notNull(optionSource, "'optionSource' must not be null");
		Assert.notNull(bindStatus, "'bindStatus' must not be null");
		this.optionSource = optionSource;
		this.bindStatus = bindStatus;
		this.valueProperty = valueProperty;
		this.labelProperty = labelProperty;
		this.htmlEscape = htmlEscape;
	}


	/**
	 * Write the '{@code option}' tags for the configured {@link #optionSource} to
	 * the supplied {@link TagWriter}.
	 * <p>
	 *  将配置的{@link #optionSource}的"{@code选项}"标签写入提供的{@link TagWriter}
	 * 
	 */
	public void writeOptions(TagWriter tagWriter) throws JspException {
		if (this.optionSource.getClass().isArray()) {
			renderFromArray(tagWriter);
		}
		else if (this.optionSource instanceof Collection) {
			renderFromCollection(tagWriter);
		}
		else if (this.optionSource instanceof Map) {
			renderFromMap(tagWriter);
		}
		else if (this.optionSource instanceof Class && ((Class<?>) this.optionSource).isEnum()) {
			renderFromEnum(tagWriter);
		}
		else {
			throw new JspException(
					"Type [" + this.optionSource.getClass().getName() + "] is not valid for option items");
		}
	}

	/**
	 * Renders the inner '{@code option}' tags using the {@link #optionSource}.
	 * <p>
	 *  使用{@link #optionSource}呈现内部的"{@code选项}"标签
	 * 
	 * 
	 * @see #doRenderFromCollection(java.util.Collection, TagWriter)
	 */
	private void renderFromArray(TagWriter tagWriter) throws JspException {
		doRenderFromCollection(CollectionUtils.arrayToList(this.optionSource), tagWriter);
	}

	/**
	 * Renders the inner '{@code option}' tags using the supplied
	 * {@link Map} as the source.
	 * <p>
	 *  使用提供的{@link Map}作为源,呈现内部的"{@code选项}"标签
	 * 
	 * 
	 * @see #renderOption(TagWriter, Object, Object, Object)
	 */
	private void renderFromMap(TagWriter tagWriter) throws JspException {
		Map<?, ?> optionMap = (Map<?, ?>) this.optionSource;
		for (Map.Entry<?, ?> entry : optionMap.entrySet()) {
			Object mapKey = entry.getKey();
			Object mapValue = entry.getValue();
			Object renderValue = (this.valueProperty != null ?
					PropertyAccessorFactory.forBeanPropertyAccess(mapKey).getPropertyValue(this.valueProperty) :
					mapKey);
			Object renderLabel = (this.labelProperty != null ?
					PropertyAccessorFactory.forBeanPropertyAccess(mapValue).getPropertyValue(this.labelProperty) :
					mapValue);
			renderOption(tagWriter, mapKey, renderValue, renderLabel);
		}
	}

	/**
	 * Renders the inner '{@code option}' tags using the {@link #optionSource}.
	 * <p>
	 *  使用{@link #optionSource}呈现内部的"{@code选项}"标签
	 * 
	 * 
	 * @see #doRenderFromCollection(java.util.Collection, TagWriter)
	 */
	private void renderFromCollection(TagWriter tagWriter) throws JspException {
		doRenderFromCollection((Collection<?>) this.optionSource, tagWriter);
	}

	/**
	 * Renders the inner '{@code option}' tags using the {@link #optionSource}.
	 * <p>
	 *  使用{@link #optionSource}呈现内部的"{@code选项}"标签
	 * 
	 * 
	 * @see #doRenderFromCollection(java.util.Collection, TagWriter)
	 */
	private void renderFromEnum(TagWriter tagWriter) throws JspException {
		doRenderFromCollection(CollectionUtils.arrayToList(((Class<?>) this.optionSource).getEnumConstants()), tagWriter);
	}

	/**
	 * Renders the inner '{@code option}' tags using the supplied {@link Collection} of
	 * objects as the source. The value of the {@link #valueProperty} field is used
	 * when rendering the '{@code value}' of the '{@code option}' and the value of the
	 * {@link #labelProperty} property is used when rendering the label.
	 * <p>
	 *  使用提供的{@link Collection}对象作为源渲染内部的"{@code选项}"标签当渲染"{@code #valueProperty"字段的"{@code值}"时,将使用{@link #valueProperty}
	 *  {@code选项}',并且在渲染标签时使用{@link #labelProperty}属性的值。
	 * 
	 */
	private void doRenderFromCollection(Collection<?> optionCollection, TagWriter tagWriter) throws JspException {
		for (Object item : optionCollection) {
			BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(item);
			Object value;
			if (this.valueProperty != null) {
				value = wrapper.getPropertyValue(this.valueProperty);
			}
			else if (item instanceof Enum) {
				value = ((Enum<?>) item).name();
			}
			else {
				value = item;
			}
			Object label = (this.labelProperty != null ? wrapper.getPropertyValue(this.labelProperty) : item);
			renderOption(tagWriter, item, value, label);
		}
	}

	/**
	 * Renders an HTML '{@code option}' with the supplied value and label. Marks the
	 * value as 'selected' if either the item itself or its value match the bound value.
	 * <p>
	 * 使用提供的值和标签呈现HTML"{@code选项}",如果项目本身或其值与绑定值匹配,则将该值标记为"selected"
	 * 
	 */
	private void renderOption(TagWriter tagWriter, Object item, Object value, Object label) throws JspException {
		tagWriter.startTag("option");
		writeCommonAttributes(tagWriter);

		String valueDisplayString = getDisplayString(value);
		String labelDisplayString = getDisplayString(label);

		valueDisplayString = processOptionValue(valueDisplayString);

		// allows render values to handle some strange browser compat issues.
		tagWriter.writeAttribute("value", valueDisplayString);

		if (isOptionSelected(value) || (value != item && isOptionSelected(item))) {
			tagWriter.writeAttribute("selected", "selected");
		}
		if (isOptionDisabled()) {
			tagWriter.writeAttribute("disabled", "disabled");
		}
		tagWriter.appendValue(labelDisplayString);
		tagWriter.endTag();
	}

	/**
	 * Determines the display value of the supplied {@code Object},
	 * HTML-escaped as required.
	 * <p>
	 *  确定所提供的{@code Object}的显示值,根据需要HTML转义
	 * 
	 */
	private String getDisplayString(Object value) {
		PropertyEditor editor = (value != null ? this.bindStatus.findEditor(value.getClass()) : null);
		return ValueFormatter.getDisplayString(value, editor, this.htmlEscape);
	}

	/**
	 * Process the option value before it is written.
	 * The default implementation simply returns the same value unchanged.
	 * <p>
	 *  在编写选项值之前处理选项值默认实现只是返回相同的值
	 * 
	 */
	protected String processOptionValue(String resolvedValue) {
		return resolvedValue;
	}

	/**
	 * Determine whether the supplied values matched the selected value.
	 * Delegates to {@link SelectedValueComparator#isSelected}.
	 * <p>
	 *  确定提供的值是否与选定的值匹配代表{@link SelectedValueComparator#isSelected}
	 * 
	 */
	private boolean isOptionSelected(Object resolvedValue) {
		return SelectedValueComparator.isSelected(this.bindStatus, resolvedValue);
	}

	/**
	 * Determine whether the option fields should be disabled.
	 * <p>
	 *  确定是否应禁用选项字段
	 * 
	 */
	protected boolean isOptionDisabled() throws JspException {
		return false;
	}

	/**
	 * Writes default attributes configured to the supplied {@link TagWriter}.
	 * <p>
	 *  写入配置为提供的{@link TagWriter}的默认属性
	 */
	protected void writeCommonAttributes(TagWriter tagWriter) throws JspException {
	}

}
