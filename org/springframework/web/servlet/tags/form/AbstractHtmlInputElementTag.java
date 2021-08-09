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

/**
 * Base class for databinding-aware JSP tags that render HTML form input element.
 *
 * <p>Provides a set of properties corresponding to the set of HTML attributes
 * that are common across form input elements.
 *
 * <p>
 *  支持数据绑定的JSP标签的基类,用于呈现HTML表单输入元素
 * 
 *  <p>提供与表单输入元素通用的HTML属性集相对应的一组属性
 * 
 * 
 * @author Rob Harrop
 * @author Rick Evans
 * @author Juergen Hoeller
 * @since 2.0
 */
@SuppressWarnings("serial")
public abstract class AbstractHtmlInputElementTag extends AbstractHtmlElementTag {

	/**
	 * The name of the '{@code onfocus}' attribute.
	 * <p>
	 * "{@code onfocus}"属性的名称
	 * 
	 */
	public static final String ONFOCUS_ATTRIBUTE = "onfocus";

	/**
	 * The name of the '{@code onblur}' attribute.
	 * <p>
	 *  "{@code onblur}"属性的名称
	 * 
	 */
	public static final String ONBLUR_ATTRIBUTE = "onblur";

	/**
	 * The name of the '{@code onchange}' attribute.
	 * <p>
	 *  "{@code onchange}"属性的名称
	 * 
	 */
	public static final String ONCHANGE_ATTRIBUTE = "onchange";

	/**
	 * The name of the '{@code accesskey}' attribute.
	 * <p>
	 *  "{@code accesskey}"属性的名称
	 * 
	 */
	public static final String ACCESSKEY_ATTRIBUTE = "accesskey";

	/**
	 * The name of the '{@code disabled}' attribute.
	 * <p>
	 *  "{@code disabled}"属性的名称
	 * 
	 */
	public static final String DISABLED_ATTRIBUTE = "disabled";

	/**
	 * The name of the '{@code readonly}' attribute.
	 * <p>
	 *  "{@code readonly}"属性的名称
	 * 
	 */
	public static final String READONLY_ATTRIBUTE = "readonly";


	private String onfocus;

	private String onblur;

	private String onchange;

	private String accesskey;

	private boolean disabled;

	private boolean readonly;


	/**
	 * Set the value of the '{@code onfocus}' attribute.
	 * May be a runtime expression.
	 * <p>
	 *  设置"{@code onfocus}"属性的值可以是运行时表达式
	 * 
	 */
	public void setOnfocus(String onfocus) {
		this.onfocus = onfocus;
	}

	/**
	 * Get the value of the '{@code onfocus}' attribute.
	 * <p>
	 *  获取"{@code onfocus}"属性的值
	 * 
	 */
	protected String getOnfocus() {
		return this.onfocus;
	}

	/**
	 * Set the value of the '{@code onblur}' attribute.
	 * May be a runtime expression.
	 * <p>
	 *  设置"{@code onblur}"属性的值可以是运行时表达式
	 * 
	 */
	public void setOnblur(String onblur) {
		this.onblur = onblur;
	}

	/**
	 * Get the value of the '{@code onblur}' attribute.
	 * <p>
	 *  获取"{@code onblur}"属性的值
	 * 
	 */
	protected String getOnblur() {
		return this.onblur;
	}

	/**
	 * Set the value of the '{@code onchange}' attribute.
	 * May be a runtime expression.
	 * <p>
	 *  设置"{@code onchange}"属性的值可以是运行时表达式
	 * 
	 */
	public void setOnchange(String onchange) {
		this.onchange = onchange;
	}

	/**
	 * Get the value of the '{@code onchange}' attribute.
	 * <p>
	 *  获取"{@code onchange}"属性的值
	 * 
	 */
	protected String getOnchange() {
		return this.onchange;
	}

	/**
	 * Set the value of the '{@code accesskey}' attribute.
	 * May be a runtime expression.
	 * <p>
	 *  设置"{@code accesskey}"属性的值可以是运行时表达式
	 * 
	 */
	public void setAccesskey(String accesskey) {
		this.accesskey = accesskey;
	}

	/**
	 * Get the value of the '{@code accesskey}' attribute.
	 * <p>
	 * 获取"{@code accesskey}"属性的值
	 * 
	 */
	protected String getAccesskey() {
		return this.accesskey;
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

	/**
	 * Sets the value of the '{@code readonly}' attribute.
	 * <p>
	 *  设置"{@code readonly}"属性的值
	 * 
	 */
	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	/**
	 * Gets the value of the '{@code readonly}' attribute.
	 * <p>
	 *  获取"{@code readonly}"属性的值
	 * 
	 */
	protected boolean isReadonly() {
		return this.readonly;
	}


	/**
	 * Adds input-specific optional attributes as defined by this base class.
	 * <p>
	 *  添加由此基类定义的输入特定的可选属性
	 */
	@Override
	protected void writeOptionalAttributes(TagWriter tagWriter) throws JspException {
		super.writeOptionalAttributes(tagWriter);

		writeOptionalAttribute(tagWriter, ONFOCUS_ATTRIBUTE, getOnfocus());
		writeOptionalAttribute(tagWriter, ONBLUR_ATTRIBUTE, getOnblur());
		writeOptionalAttribute(tagWriter, ONCHANGE_ATTRIBUTE, getOnchange());
		writeOptionalAttribute(tagWriter, ACCESSKEY_ATTRIBUTE, getAccesskey());
		if (isDisabled()) {
			tagWriter.writeAttribute(DISABLED_ATTRIBUTE, "disabled");
		}
		if (isReadonly()) {
			writeOptionalAttribute(tagWriter, READONLY_ATTRIBUTE, "readonly");
		}
	}

}
