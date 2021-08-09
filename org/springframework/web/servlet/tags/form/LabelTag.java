/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2016 the original author or authors.
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
import org.springframework.util.StringUtils;

/**
 * Databinding-aware JSP tag for rendering an HTML '{@code label}' element
 * that defines text that is associated with a single form element.
 *
 * <p>See the "formTags" showcase application that ships with the
 * full Spring distribution for an example of this class in action.
 *
 * <p>
 *  用于呈现HTML"{@code label}"元素的数据绑定感知JSP标签,用于定义与单个表单元素相关联的文本
 * 
 * <p>请参阅"formTags"展示应用程序,该应用程序附带了完整的Spring发行版,以提供此类操作的示例
 * 
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 */
@SuppressWarnings("serial")
public class LabelTag extends AbstractHtmlElementTag {

	/**
	 * The HTML '{@code label}' tag.
	 * <p>
	 *  HTML"{@code label}"标签
	 * 
	 */
	private static final String LABEL_TAG = "label";

	/**
	 * The name of the '{@code for}' attribute.
	 * <p>
	 *  "{@code for}"属性的名称
	 * 
	 */
	private static final String FOR_ATTRIBUTE = "for";


	/**
	 * The {@link TagWriter} instance being used.
	 * <p>Stored so we can close the tag on {@link #doEndTag()}.
	 * <p>
	 *  {@link TagWriter}实例正在使用<p>存储,所以我们可以在{@link #doEndTag()}上关闭标签
	 * 
	 */
	private TagWriter tagWriter;

	/**
	 * The value of the '{@code for}' attribute.
	 * <p>
	 *  "{@code for}"属性的值
	 * 
	 */
	private String forId;


	/**
	 * Set the value of the '{@code for}' attribute.
	 * <p>Defaults to the value of {@link #getPath}; may be a runtime expression.
	 * <p>
	 *  设置"{@code for}"属性的值<p>默认值为{@link #getPath};可能是一个运行时表达式
	 * 
	 * 
	 * @throws IllegalArgumentException if the supplied value is {@code null}
	 */
	public void setFor(String forId) {
		Assert.notNull(forId, "'forId' must not be null");
		this.forId = forId;
	}

	/**
	 * Get the value of the '{@code id}' attribute.
	 * <p>May be a runtime expression.
	 * <p>
	 *  获取"{@code id}"属性的值<p>可以是运行时表达式
	 * 
	 */
	public String getFor() {
		return this.forId;
	}


	/**
	 * Writes the opening '{@code label}' tag and forces a block tag so
	 * that body content is written correctly.
	 * <p>
	 *  写入开头的"{@code label}"标签,并强制使用块标签,以便正确写入正文内容
	 * 
	 * 
	 * @return {@link javax.servlet.jsp.tagext.Tag#EVAL_BODY_INCLUDE}
	 */
	@Override
	protected int writeTagContent(TagWriter tagWriter) throws JspException {
		tagWriter.startTag(LABEL_TAG);
		tagWriter.writeAttribute(FOR_ATTRIBUTE, resolveFor());
		writeDefaultAttributes(tagWriter);
		tagWriter.forceBlock();
		this.tagWriter = tagWriter;
		return EVAL_BODY_INCLUDE;
	}

	/**
	 * Overrides {@link #getName()} to always return {@code null},
	 * because the '{@code name}' attribute is not supported by the
	 * '{@code label}' tag.
	 * <p>
	 *  覆盖{@link #getName()}始终返回{@code null},因为'{@code label}'标签不支持"{@code name}"属性
	 * 
	 * 
	 * @return the value for the HTML '{@code name}' attribute
	 */
	@Override
	protected String getName() throws JspException {
		// This also suppresses the 'id' attribute (which is okay for a <label/>)
		return null;
	}

	/**
	 * Determine the '{@code for}' attribute value for this tag,
	 * autogenerating one if none specified.
	 * <p>
	 * 确定此标签的"{@code for}"属性值,如果没有指定,则自动生成一个
	 * 
	 * 
	 * @see #getFor()
	 * @see #autogenerateFor()
	 */
	protected String resolveFor() throws JspException {
		if (StringUtils.hasText(this.forId)) {
			return getDisplayString(evaluate(FOR_ATTRIBUTE, this.forId));
		}
		else {
			return autogenerateFor();
		}
	}

	/**
	 * Autogenerate the '{@code for}' attribute value for this tag.
	 * <p>The default implementation delegates to {@link #getPropertyPath()},
	 * deleting invalid characters (such as "[" or "]").
	 * <p>
	 *  自动生成此标签的"{@code for}"属性值<p>默认实现委托为{@link #getPropertyPath()},删除无效字符(例如"["或"]")
	 * 
	 */
	protected String autogenerateFor() throws JspException {
		return StringUtils.deleteAny(getPropertyPath(), "[]");
	}

	/**
	 * Close the '{@code label}' tag.
	 * <p>
	 *  关闭"{@code label}"标签
	 * 
	 */
	@Override
	public int doEndTag() throws JspException {
		this.tagWriter.endTag();
		return EVAL_PAGE;
	}

	/**
	 * Disposes of the {@link TagWriter} instance.
	 * <p>
	 *  处理{@link TagWriter}实例
	 */
	@Override
	public void doFinally() {
		super.doFinally();
		this.tagWriter = null;
	}

}
