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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTag;

import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * Form tag for displaying errors for a particular field or object.
 *
 * <p>This tag supports three main usage patterns:
 *
 * <ol>
 *	<li>Field only - set '{@code path}' to the field name (or path)</li>
 *	<li>Object errors only - omit '{@code path}'</li>
 *	<li>All errors - set '{@code path}' to '{@code *}'</li>
 * </ol>
 *
 * <p>
 *  用于显示特定字段或对象的错误的表单标签
 * 
 *  <p>此标签支持三种主要使用模式：
 * 
 * <ol>
 * <li>仅限字段 - 将"{@code path}"设置为字段名称(或路径)</li> <li>仅限对象错误 - 省略"{@code path}"</li> <li>所有错误 - 将"{@code路径}
 * "设置为"{@code *}"</li>。
 * </ol>
 * 
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Rick Evans
 * @since 2.0
 */
@SuppressWarnings("serial")
public class ErrorsTag extends AbstractHtmlElementBodyTag implements BodyTag {

	/**
	 * The key under which this tag exposes error messages in
	 * the {@link PageContext#PAGE_SCOPE page context scope}.
	 * <p>
	 *  此标记在{@link PageContext#PAGE_SCOPE页面上下文范围内公开错误消息的关键字)
	 * 
	 */
	public static final String MESSAGES_ATTRIBUTE = "messages";

	/**
	 * The HTML '{@code span}' tag.
	 * <p>
	 *  HTML"{@code span}"标签
	 * 
	 */
	public static final String SPAN_TAG = "span";


	private String element = SPAN_TAG;

	private String delimiter = "<br/>";

	/**
	 * Stores any value that existed in the 'errors messages' before the tag was started.
	 * <p>
	 *  在代码开始前存储"错误消息"中存在的任何值
	 * 
	 */
	private Object oldMessages;

	private boolean errorMessagesWereExposed;


	/**
	 * Set the HTML element must be used to render the error messages.
	 * <p>Defaults to an HTML '{@code <span/>}' tag.
	 * <p>
	 *  设置HTML元素必须用于呈现错误消息<p>默认为HTML"{@code <span />}"标签
	 * 
	 */
	public void setElement(String element) {
		Assert.hasText(element, "'element' cannot be null or blank");
		this.element = element;
	}

	/**
	 * Get the HTML element must be used to render the error messages.
	 * <p>
	 *  必须使用HTML元素来呈现错误消息
	 * 
	 */
	public String getElement() {
		return this.element;
	}

	/**
	 * Set the delimiter to be used between error messages.
	 * <p>Defaults to an HTML '{@code <br/>}' tag.
	 * <p>
	 *  设置要在错误消息之间使用的分隔符<p>默认为HTML"{@code <br/>"}标签
	 * 
	 */
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	/**
	 * Return the delimiter to be used between error messages.
	 * <p>
	 *  返回在错误消息之间使用的分隔符
	 * 
	 */
	public String getDelimiter() {
		return this.delimiter;
	}


	/**
	 * Get the value for the HTML '{@code id}' attribute.
	 * <p>Appends '{@code .errors}' to the value returned by {@link #getPropertyPath()}
	 * or to the model attribute name if the {@code <form:errors/>} tag's
	 * '{@code path}' attribute has been omitted.
	 * <p>
	 * 获取HTML"{@code id}"属性的值<p>将{@code错误}添加到{@link #getPropertyPath()}返回的值或模型属性名称,如果{@code <形式：errors />}标签
	 * 的"{@code path}"属性已被省略。
	 * 
	 * 
	 * @return the value for the HTML '{@code id}' attribute
	 * @see #getPropertyPath()
	 */
	@Override
	protected String autogenerateId() throws JspException {
		String path = getPropertyPath();
		if ("".equals(path) || "*".equals(path)) {
			path = (String) this.pageContext.getAttribute(
					FormTag.MODEL_ATTRIBUTE_VARIABLE_NAME, PageContext.REQUEST_SCOPE);
		}
		return StringUtils.deleteAny(path, "[]") + ".errors";
	}

	/**
	 * Get the value for the HTML '{@code name}' attribute.
	 * <p>Simply returns {@code null} because the '{@code name}' attribute
	 * is not a validate attribute for the '{@code span}' element.
	 * <p>
	 *  获取HTML"{@code name}"属性的值<p>只需返回{@code null},因为"{@code name}"属性不是"{@code span}"元素的有效属性
	 * 
	 */
	@Override
	protected String getName() throws JspException {
		return null;
	}

	/**
	 * Should rendering of this tag proceed at all?
	 * <p>Only renders output when there are errors for the configured {@link #setPath path}.
	 * <p>
	 *  是否应该渲染此标签? <p>仅当配置的{@link #setPath路径}存在错误时才显示输出
	 * 
	 * 
	 * @return {@code true} only when there are errors for the configured {@link #setPath path}
	 */
	@Override
	protected boolean shouldRender() throws JspException {
		try {
			return getBindStatus().isError();
		}
		catch (IllegalStateException ex) {
			// Neither BindingResult nor target object available.
			return false;
		}
	}

	@Override
	protected void renderDefaultContent(TagWriter tagWriter) throws JspException {
		tagWriter.startTag(getElement());
		writeDefaultAttributes(tagWriter);
		String delimiter = ObjectUtils.getDisplayString(evaluate("delimiter", getDelimiter()));
		String[] errorMessages = getBindStatus().getErrorMessages();
		for (int i = 0; i < errorMessages.length; i++) {
			String errorMessage = errorMessages[i];
			if (i > 0) {
				tagWriter.appendValue(delimiter);
			}
			tagWriter.appendValue(getDisplayString(errorMessage));
		}
		tagWriter.endTag();
	}

	/**
	 * Exposes any bind status error messages under {@link #MESSAGES_ATTRIBUTE this key}
	 * in the {@link PageContext#PAGE_SCOPE}.
	 * <p>Only called if {@link #shouldRender()} returns {@code true}.
	 * <p>
	 *  在{@link PageContext#PAGE_SCOPE} <p>中的{@link #MESSAGES_ATTRIBUTE此密钥}下发布任何绑定状态错误消息仅在{@link #shouldRender())返回{@code true}
	 * 时调用。
	 * 
	 * 
	 * @see #removeAttributes()
	 */
	@Override
	protected void exposeAttributes() throws JspException {
		List<String> errorMessages = new ArrayList<String>();
		errorMessages.addAll(Arrays.asList(getBindStatus().getErrorMessages()));
		this.oldMessages = this.pageContext.getAttribute(MESSAGES_ATTRIBUTE, PageContext.PAGE_SCOPE);
		this.pageContext.setAttribute(MESSAGES_ATTRIBUTE, errorMessages, PageContext.PAGE_SCOPE);
		this.errorMessagesWereExposed = true;
	}

	/**
	 * Removes any bind status error messages that were previously stored under
	 * {@link #MESSAGES_ATTRIBUTE this key} in the {@link PageContext#PAGE_SCOPE}.
	 * <p>
	 * 
	 * @see #exposeAttributes()
	 */
	@Override
	protected void removeAttributes() {
		if (this.errorMessagesWereExposed) {
			if (this.oldMessages != null) {
				this.pageContext.setAttribute(MESSAGES_ATTRIBUTE, this.oldMessages, PageContext.PAGE_SCOPE);
				this.oldMessages = null;
			}
			else {
				this.pageContext.removeAttribute(MESSAGES_ATTRIBUTE, PageContext.PAGE_SCOPE);
			}
		}
	}

}
