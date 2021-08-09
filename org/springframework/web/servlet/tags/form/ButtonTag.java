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

import javax.servlet.jsp.JspException;

import org.springframework.web.servlet.support.RequestDataValueProcessor;

/**
 * An HTML button tag. This tag is provided for completeness if the application
 * relies on a {@link RequestDataValueProcessor}.
 *
 * <p>
 *  HTML按钮标签如果应用程序依赖于{@link RequestDataValueProcessor},则该标签是为了完整性而提供的
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 3.1
 */
@SuppressWarnings("serial")
public class ButtonTag extends AbstractHtmlElementTag {

	/**
	 * The name of the '{@code disabled}' attribute.
	 * <p>
	 *  "{@code disabled}"属性的名称
	 * 
	 */
	public static final String DISABLED_ATTRIBUTE = "disabled";


	private TagWriter tagWriter;

	private String name;

	private String value;

	private boolean disabled;


	/**
	 * Get the value of the '{@code name}' attribute.
	 * <p>
	 *  获取"{@code name}"属性的值
	 * 
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Set the value of the '{@code name}' attribute.
	 * <p>
	 * 设置"{@code name}"属性的值
	 * 
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Set the value of the '{@code value}' attribute.
	 * <p>
	 *  设置"{@code value}"属性的值
	 * 
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Get the value of the '{@code value}' attribute.
	 * <p>
	 *  获取"{@code value}"属性的值
	 * 
	 */
	public String getValue() {
		return this.value;
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
	public boolean isDisabled() {
		return this.disabled;
	}


	@Override
	protected int writeTagContent(TagWriter tagWriter) throws JspException {
		tagWriter.startTag("button");
		writeDefaultAttributes(tagWriter);
		tagWriter.writeAttribute("type", getType());
		writeValue(tagWriter);
		if (isDisabled()) {
			tagWriter.writeAttribute(DISABLED_ATTRIBUTE, "disabled");
		}
		tagWriter.forceBlock();
		this.tagWriter = tagWriter;
		return EVAL_BODY_INCLUDE;
	}

	/**
	 * Writes the '{@code value}' attribute to the supplied {@link TagWriter}.
	 * Subclasses may choose to override this implementation to control exactly
	 * when the value is written.
	 * <p>
	 *  将"{@code value}"属性写入提供的{@link TagWriter}子类可以选择覆盖此实现来精确控制该值的写入位置
	 * 
	 */
	protected void writeValue(TagWriter tagWriter) throws JspException {
		String valueToUse = (getValue() != null) ? getValue() : getDefaultValue();
		tagWriter.writeAttribute("value", processFieldValue(getName(), valueToUse, getType()));
	}

	/**
	 * Return the default value.
	 * <p>
	 *  返回默认值
	 * 
	 * 
	 * @return The default value if none supplied.
	 */
	protected String getDefaultValue() {
		return "Submit";
	}

	/**
	 * Get the value of the '{@code type}' attribute. Subclasses
	 * can override this to change the type of '{@code input}' element
	 * rendered. Default value is '{@code submit}'.
	 * <p>
	 *  获取"{@code type}"属性的值子类可以覆盖此值以更改呈现的"{@code input}"元素的类型默认值为"{@code submit}"
	 * 
	 */
	protected String getType() {
		return "submit";
	}

	/**
	 * Closes the '{@code button}' block tag.
	 * <p>
	 *  关闭"{@code button}"块标签
	 */
	@Override
	public int doEndTag() throws JspException {
		this.tagWriter.endTag();
		return EVAL_PAGE;
	}

}
