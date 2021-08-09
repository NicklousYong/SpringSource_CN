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
 * Data-binding aware JSP tag for rendering a hidden HTML '{@code input}' field
 * containing the databound value.
 *
 * <p>Example (binding to 'name' property of form backing object):
 * <pre class="code>
 * &lt;form:hidden path=&quot;name&quot;/&gt;
 * </pre>
 *
 * <p>
 *  用于呈现包含数据绑定值的隐藏HTML"{@code input}"字段的数据绑定感知JSP标记
 * 
 *  <p>示例(绑定到表单后备对象的"name"属性)：
 * <pre class="code>
 *  &lt; form：hidden path =&quot; name&quot; /&gt;
 * </pre>
 * 
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Rossen Stoyanchev
 * @since 2.0
 */
@SuppressWarnings("serial")
public class HiddenInputTag extends AbstractHtmlElementTag {

	/**
	 * The name of the '{@code disabled}' attribute.
	 * <p>
	 * "{@code disabled}"属性的名称
	 * 
	 */
	public static final String DISABLED_ATTRIBUTE = "disabled";

	private boolean disabled;


	/**
	 * Set the value of the '{@code disabled}' attribute.
	 * May be a runtime expression.
	 * <p>
	 *  设置"{@code disabled}"属性的值可以是运行时表达式
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


	/**
	 * Flags "type" as an illegal dynamic attribute.
	 * <p>
	 *  标志"类型"为非法动态属性
	 * 
	 */
	@Override
	protected boolean isValidDynamicAttribute(String localName, Object value) {
		return !"type".equals(localName);
	}

	/**
	 * Writes the HTML '{@code input}' tag to the supplied {@link TagWriter} including the
	 * databound value.
	 * <p>
	 *  将HTML"{@code input}"标签写入提供的{@link TagWriter},包括数据绑定值
	 * 
	 * @see #writeDefaultAttributes(TagWriter)
	 * @see #getBoundValue()
	 */
	@Override
	protected int writeTagContent(TagWriter tagWriter) throws JspException {
		tagWriter.startTag("input");
		writeDefaultAttributes(tagWriter);
		tagWriter.writeAttribute("type", "hidden");
		if (isDisabled()) {
			tagWriter.writeAttribute(DISABLED_ATTRIBUTE, "disabled");
		}
		String value = getDisplayString(getBoundValue(), getPropertyEditor());
		tagWriter.writeAttribute("value", processFieldValue(getName(), value, "hidden"));
		tagWriter.endTag();
		return SKIP_BODY;
	}

}
