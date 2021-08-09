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

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;

import org.springframework.util.StringUtils;

/**
 * Convenient super class for many html tags that render content using the databinding
 * features of the {@link AbstractHtmlElementTag AbstractHtmlElementTag}. The only thing
 * sub-tags need to do is override {@link #renderDefaultContent(TagWriter)}.
 *
 * <p>
 * 使用{@link AbstractHtmlElementTag AbstractHtmlElementTag}的数据绑定功能呈现内容的许多html标签的方便的超类。
 * 子标签唯一需要做的是覆盖{@link #renderDefaultContent(TagWriter)}。
 * 
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 */
@SuppressWarnings("serial")
public abstract class AbstractHtmlElementBodyTag extends AbstractHtmlElementTag implements BodyTag {

	private BodyContent bodyContent;

	private TagWriter tagWriter;


	@Override
	protected int writeTagContent(TagWriter tagWriter) throws JspException {
		onWriteTagContent();
		this.tagWriter = tagWriter;
		if (shouldRender()) {
			exposeAttributes();
			return EVAL_BODY_BUFFERED;
		}
		else {
			return SKIP_BODY;
		}
	}

	/**
	 * If {@link #shouldRender rendering}, flush any buffered
	 * {@link BodyContent} or, if no {@link BodyContent} is supplied,
	 * {@link #renderDefaultContent render the default content}.
	 * <p>
	 *  如果{@link #shouldRender rendering},刷新任何缓冲的{@link BodyContent},或者如果没有提供{@link BodyContent},{@link #renderDefaultContent呈现默认内容}
	 * 。
	 * 
	 * 
	 * @return Tag#EVAL_PAGE
	 */
	@Override
	public int doEndTag() throws JspException {
		if (shouldRender()) {
			if (this.bodyContent != null && StringUtils.hasText(this.bodyContent.getString())) {
				renderFromBodyContent(this.bodyContent, this.tagWriter);
			}
			else {
				renderDefaultContent(this.tagWriter);
			}
		}
		return EVAL_PAGE;
	}

	/**
	 * Render the tag contents based on the supplied {@link BodyContent}.
	 * <p>The default implementation simply {@link #flushBufferedBodyContent flushes}
	 * the {@link BodyContent} directly to the output. Subclasses may choose to
	 * override this to add additional content to the output.
	 * <p>
	 *  根据提供的{@link BodyContent} <p>渲染标签内容默认实现简单{@link #flushBufferedBodyContent将{@link BodyContent}直接刷新到输出子
	 * 类可以选择覆盖此值以向输出添加附加内容。
	 * 
	 */
	protected void renderFromBodyContent(BodyContent bodyContent, TagWriter tagWriter) throws JspException {
		flushBufferedBodyContent(this.bodyContent);
	}

	/**
	 * Clean up any attributes and stored resources.
	 * <p>
	 *  清理任何属性和存储的资源
	 * 
	 */
	@Override
	public void doFinally() {
		super.doFinally();
		removeAttributes();
		this.tagWriter = null;
		this.bodyContent = null;
	}


	//---------------------------------------------------------------------
	// Template methods
	//---------------------------------------------------------------------

	/**
	 * Called at the start of {@link #writeTagContent} allowing subclasses to perform
	 * any precondition checks or setup tasks that might be necessary.
	 * <p>
	 * 在{@link #writeTagContent}的开始时调用,允许子类执行可能需要的任何前提条件检查或设置任务
	 * 
	 */
	protected void onWriteTagContent() {
	}

	/**
	 * Should rendering of this tag proceed at all. Returns '{@code true}' by default
	 * causing rendering to occur always, Subclasses can override this if they
	 * provide conditional rendering.
	 * <p>
	 *  如果默认情况下,此标签的渲染将继续执行所有返回"{@code true}",导致渲染始终发生,如果子类提供条件渲染
	 * 
	 */
	protected boolean shouldRender() throws JspException {
		return true;
	}

	/**
	 * Called during {@link #writeTagContent} allowing subclasses to add any attributes to the
	 * {@link javax.servlet.jsp.PageContext} as needed.
	 * <p>
	 *  在{@link #writeTagContent}期间调用,允许子类根据需要向{@link javaxservletjspPageContext}添加任何属性
	 * 
	 */
	protected void exposeAttributes() throws JspException {
	}

	/**
	 * Called by {@link #doFinally} allowing subclasses to remove any attributes from the
	 * {@link javax.servlet.jsp.PageContext} as needed.
	 * <p>
	 *  由{@link #doFinally}调用,允许子类根据需要从{@link javaxservletjspPageContext}中删除任何属性
	 * 
	 */
	protected void removeAttributes() {
	}

	/**
	 * The user customised the output of the error messages - flush the
	 * buffered content into the main {@link javax.servlet.jsp.JspWriter}.
	 * <p>
	 *  用户自定义错误消息的输出 - 将缓冲的内容刷新到主{@link javaxservletjspJspWriter}
	 */
	protected void flushBufferedBodyContent(BodyContent bodyContent) throws JspException {
		try {
			bodyContent.writeOut(bodyContent.getEnclosingWriter());
		}
		catch (IOException ex) {
			throw new JspException("Unable to write buffered body content.", ex);
		}
	}

	protected abstract void renderDefaultContent(TagWriter tagWriter) throws JspException;


	//---------------------------------------------------------------------
	// BodyTag implementation
	//---------------------------------------------------------------------

	@Override
	public void doInitBody() throws JspException {
		// no op
	}

	@Override
	public void setBodyContent(BodyContent bodyContent) {
		this.bodyContent = bodyContent;
	}

}
