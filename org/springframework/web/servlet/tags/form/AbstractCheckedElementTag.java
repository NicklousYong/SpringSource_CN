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

import javax.servlet.jsp.JspException;

/**
 * Abstract base class to provide common methods for
 * implementing databinding-aware JSP tags for rendering an HTML '{@code input}'
 * element with a '{@code type}' of '{@code checkbox}' or '{@code radio}'.
 *
 * <p>
 *  抽象基类提供了实现数据绑定感知的JSP标签的常用方法,用于使用"{@code checkbox}"或"{@code radio}"的"{@code type}"呈现HTML"{@code input}
 * "元素"。
 * 
 * 
 * @author Thomas Risberg
 * @author Juergen Hoeller
 * @author Rossen Stoyanchev
 * @since 2.5
 */
@SuppressWarnings("serial")
public abstract class AbstractCheckedElementTag extends AbstractHtmlInputElementTag {

	/**
	 * Render the '{@code input(checkbox)}' with the supplied value, marking the
	 * '{@code input}' element as 'checked' if the supplied value matches the
	 * bound value.
	 * <p>
	 * 使用提供的值渲染"{@code input(checkbox)}",如果提供的值与绑定值匹配,则将"{@code input}"元素标记为"checked"
	 * 
	 */
	protected void renderFromValue(Object value, TagWriter tagWriter) throws JspException {
		renderFromValue(value, value, tagWriter);
	}

	/**
	 * Render the '{@code input(checkbox)}' with the supplied value, marking the
	 * '{@code input}' element as 'checked' if the supplied value matches the
	 * bound value.
	 * <p>
	 *  使用提供的值渲染"{@code input(checkbox)}",如果提供的值与绑定值匹配,则将"{@code input}"元素标记为"checked"
	 * 
	 */
	protected void renderFromValue(Object item, Object value, TagWriter tagWriter) throws JspException {
		String displayValue = convertToDisplayString(value);
		tagWriter.writeAttribute("value", processFieldValue(getName(), displayValue, getInputType()));
		if (isOptionSelected(value) || (value != item && isOptionSelected(item))) {
			tagWriter.writeAttribute("checked", "checked");
		}
	}

	/**
	 * Determines whether the supplied value matched the selected value
	 * through delegating to {@link SelectedValueComparator#isSelected}.
	 * <p>
	 *  通过委托{@link SelectedValueComparator#isSelected}来确定提供的值是否与选定的值相匹配
	 * 
	 */
	private boolean isOptionSelected(Object value) throws JspException {
		return SelectedValueComparator.isSelected(getBindStatus(), value);
	}

	/**
	 * Render the '{@code input(checkbox)}' with the supplied value, marking
	 * the '{@code input}' element as 'checked' if the supplied Boolean is
	 * {@code true}.
	 * <p>
	 *  使用提供的值渲染"{@code input(checkbox)}",如果提供的布尔值为{@code true},则将"{@code input}"元素标记为"checked"
	 * 
	 */
	protected void renderFromBoolean(Boolean boundValue, TagWriter tagWriter) throws JspException {
		tagWriter.writeAttribute("value", processFieldValue(getName(), "true", getInputType()));
		if (boundValue) {
			tagWriter.writeAttribute("checked", "checked");
		}
	}

	/**
	 * Return a unique ID for the bound name within the current PageContext.
	 * <p>
	 *  返回当前PageContext中绑定名称的唯一ID
	 * 
	 */
	@Override
	protected String autogenerateId() throws JspException {
		return TagIdGenerator.nextId(super.autogenerateId(), this.pageContext);
	}


	/**
	 * Writes the '{@code input}' element to the supplied
	 * {@link TagWriter},
	 * marking it as 'checked' if appropriate.
	 * <p>
	 *  将"{@code input}"元素写入提供的{@link TagWriter},将其标记为"已检查",如果合适
	 * 
	 */
	@Override
	protected abstract int writeTagContent(TagWriter tagWriter) throws JspException;

	/**
	 * Flags "type" as an illegal dynamic attribute.
	 * <p>
	 * 标志"类型"为非法动态属性
	 * 
	 */
	@Override
	protected boolean isValidDynamicAttribute(String localName, Object value) {
		return !"type".equals(localName);
	}

	/**
	 * Return the type of the HTML input element to generate:
	 * "checkbox" or "radio".
	 * <p>
	 *  返回HTML输入元素的类型以生成："复选框"或"收音机"
	 */
	protected abstract String getInputType();

}
