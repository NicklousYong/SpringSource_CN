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

package org.springframework.web.servlet.tags;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;

import org.springframework.web.util.JavaScriptUtils;

/**
 * Custom JSP tag to escape its enclosed body content,
 * applying HTML escaping and/or JavaScript escaping.
 *
 * <p>Provides a "htmlEscape" property for explicitly specifying whether to
 * apply HTML escaping. If not set, a page-level default (e.g. from the
 * HtmlEscapeTag) or an application-wide default (the "defaultHtmlEscape"
 * context-param in web.xml) is used.
 *
 * <p>Provides a "javaScriptEscape" property for specifying whether to apply
 * JavaScript escaping. Can be combined with HTML escaping or used standalone.
 *
 * <p>
 *  自定义JSP标签来转义其封闭的正文内容,应用HTML转义和/或JavaScript转义
 * 
 * <p>提供一个"htmlEscape"属性,用于显式指定是否应用HTML转义如果未设置,页级默认(例如从​​HtmlEscapeTag)或应用程序范围的默认值(webxml中的"defaultHtmlE
 * scape"上下文参数)是用过的。
 * 
 *  <p>提供一个"javaScriptEscape"属性,用于指定是否应用JavaScript转义可以与HTML进行转义或独立使用
 * 
 * 
 * @author Juergen Hoeller
 * @since 1.1.1
 * @see org.springframework.web.util.HtmlUtils
 * @see org.springframework.web.util.JavaScriptUtils
 */
@SuppressWarnings("serial")
public class EscapeBodyTag extends HtmlEscapingAwareTag implements BodyTag {

	private boolean javaScriptEscape = false;

	private BodyContent bodyContent;


	/**
	 * Set JavaScript escaping for this tag, as boolean value.
	 * Default is "false".
	 * <p>
	 *  设置JavaScript转义为此标记,布尔值默认为"false"
	 * 
	 */
	public void setJavaScriptEscape(boolean javaScriptEscape) throws JspException {
		this.javaScriptEscape = javaScriptEscape;
	}


	@Override
	protected int doStartTagInternal() {
		// do nothing
		return EVAL_BODY_BUFFERED;
	}

	@Override
	public void doInitBody() {
		// do nothing
	}

	@Override
	public void setBodyContent(BodyContent bodyContent) {
		this.bodyContent = bodyContent;
	}

	@Override
	public int doAfterBody() throws JspException {
		try {
			String content = readBodyContent();
			// HTML and/or JavaScript escape, if demanded
			content = htmlEscape(content);
			content = this.javaScriptEscape ? JavaScriptUtils.javaScriptEscape(content) : content;
			writeBodyContent(content);
		}
		catch (IOException ex) {
			throw new JspException("Could not write escaped body", ex);
		}
		return (SKIP_BODY);
	}

	/**
	 * Read the unescaped body content from the page.
	 * <p>
	 *  从页面读取未转义的正文内容
	 * 
	 * 
	 * @return the original content
	 * @throws IOException if reading failed
	 */
	protected String readBodyContent() throws IOException {
		return this.bodyContent.getString();
	}

	/**
	 * Write the escaped body content to the page.
	 * <p>Can be overridden in subclasses, e.g. for testing purposes.
	 * <p>
	 *  将转义的正文内容写入页面<p>可以在子类中覆盖,例如用于测试目的
	 * 
	 * @param content the content to write
	 * @throws IOException if writing failed
	 */
	protected void writeBodyContent(String content) throws IOException {
		this.bodyContent.getEnclosingWriter().print(content);
	}

}
