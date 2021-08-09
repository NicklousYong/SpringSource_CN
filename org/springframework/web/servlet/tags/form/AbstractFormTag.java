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

package org.springframework.web.servlet.tags.form;

import java.beans.PropertyEditor;
import javax.servlet.jsp.JspException;

import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.tags.HtmlEscapingAwareTag;

/**
 * Base class for all JSP form tags. Provides utility methods for
 * null-safe EL evaluation and for accessing and working with a {@link TagWriter}.
 *
 * <p>Subclasses should implement the {@link #writeTagContent(TagWriter)} to perform
 * actual tag rendering.
 *
 * <p>Subclasses (or test classes) can override the {@link #createTagWriter()} method to
 * redirect output to a {@link java.io.Writer} other than the {@link javax.servlet.jsp.JspWriter}
 * associated with the current {@link javax.servlet.jsp.PageContext}.
 *
 * <p>
 *  所有JSP表单标签的基类提供零安全EL评估的实用方法,以及访问和使用{@link TagWriter}
 * 
 * <p>子类应实现{@link #writeTagContent(TagWriter)}来执行实际的标记呈现
 * 
 *  <p>子类(或测试类)可以覆盖{@link #createTagWriter()}方法,将输出重定向到与当前{@link javaxservletjspPageContext}相关联的{@link javaxservletjspJspWriter}
 * 以外的{@link javaioWriter}。
 * 
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 */
@SuppressWarnings("serial")
public abstract class AbstractFormTag extends HtmlEscapingAwareTag {

	/**
	 * Evaluate the supplied value for the supplied attribute name.
	 * <p>The default implementation simply returns the given value as-is.
	 * <p>
	 *  评估提供的属性名称的提供值<p>默认实现只是原样返回给定的值
	 * 
	 */
	protected Object evaluate(String attributeName, Object value) throws JspException {
		return value;
	}

	/**
	 * Optionally writes the supplied value under the supplied attribute name into the supplied
	 * {@link TagWriter}. In this case, the supplied value is {@link #evaluate evaluated} first
	 * and then the {@link ObjectUtils#getDisplayString String representation} is written as the
	 * attribute value. If the resultant {@code String} representation is {@code null}
	 * or empty, no attribute is written.
	 * <p>
	 * 可选地将提供的属性名称下的值写入提供的{@link TagWriter}中。
	 * 在这种情况下,提供的值首先为{@link #evaluate evaluate},然后将{@link ObjectUtils#getDisplayString String representation}
	 * 写为属性值如果结果{@code String}表示为{@code null}或为空,则不会写入属性。
	 * 可选地将提供的属性名称下的值写入提供的{@link TagWriter}中。
	 * 
	 * 
	 * @see TagWriter#writeOptionalAttributeValue(String, String)
	 */
	protected final void writeOptionalAttribute(TagWriter tagWriter, String attributeName, String value)
			throws JspException {

		if (value != null) {
			tagWriter.writeOptionalAttributeValue(attributeName, getDisplayString(evaluate(attributeName, value)));
		}
	}

	/**
	 * Create the {@link TagWriter} which all output will be written to. By default,
	 * the {@link TagWriter} writes its output to the {@link javax.servlet.jsp.JspWriter}
	 * for the current {@link javax.servlet.jsp.PageContext}. Subclasses may choose to
	 * change the {@link java.io.Writer} to which output is actually written.
	 * <p>
	 *  创建所有输出将被写入的{@link TagWriter}默认情况下,{@link TagWriter}将其输出写入{@link javaxservletjspJspWriter},以便当前的{@link javaxservletjspPageContext}
	 * 子类可以选择更改{@link javaioWriter}实际写入的输出。
	 * 
	 */
	protected TagWriter createTagWriter() {
		return new TagWriter(this.pageContext);
	}

	/**
	 * Provide a simple template method that calls {@link #createTagWriter()} and passes
	 * the created {@link TagWriter} to the {@link #writeTagContent(TagWriter)} method.
	 * <p>
	 * 提供一个简单的模板方法,调用{@link #createTagWriter()}并将创建的{@link TagWriter}传递给{@link #writeTagContent(TagWriter)}方
	 * 法。
	 * 
	 * 
	 * @return the value returned by {@link #writeTagContent(TagWriter)}
	 */
	@Override
	protected final int doStartTagInternal() throws Exception {
		return writeTagContent(createTagWriter());
	}

	/**
	 * Get the display value of the supplied {@code Object}, HTML escaped
	 * as required. This version is <strong>not</strong> {@link PropertyEditor}-aware.
	 * <p>
	 *  获取所提供的{@code Object}的显示值,根据需要转义HTML此版本<strong>不</strong> {@link PropertyEditor} -aware
	 * 
	 */
	protected String getDisplayString(Object value) {
		return ValueFormatter.getDisplayString(value, isHtmlEscape());
	}

	/**
	 * Get the display value of the supplied {@code Object}, HTML escaped
	 * as required. If the supplied value is not a {@link String} and the supplied
	 * {@link PropertyEditor} is not null then the {@link PropertyEditor} is used
	 * to obtain the display value.
	 * <p>
	 *  获取所提供的{@code Object}的显示值,根据需要转义HTML如果提供的值不是{@link String},并且提供的{@link PropertyEditor}不为null,则使用{@link PropertyEditor}
	 * 获取显示值。
	 * 
	 */
	protected String getDisplayString(Object value, PropertyEditor propertyEditor) {
		return ValueFormatter.getDisplayString(value, propertyEditor, isHtmlEscape());
	}

	/**
	 * Overridden to default to {@code true} in case of no explicit default given.
	 * <p>
	 *  如果没有给出明确的默认值,则覆盖为{@code true}的默认值
	 * 
	 */
	@Override
	protected boolean isDefaultHtmlEscape() {
		Boolean defaultHtmlEscape = getRequestContext().getDefaultHtmlEscape();
		return (defaultHtmlEscape == null || defaultHtmlEscape.booleanValue());
	}


	/**
	 * Subclasses should implement this method to perform tag content rendering.
	 * <p>
	 *  子类应实现此方法来执行标签内容呈现
	 * 
	 * @return valid tag render instruction as per {@link javax.servlet.jsp.tagext.Tag#doStartTag()}.
	 */
	protected abstract int writeTagContent(TagWriter tagWriter) throws JspException;

}
