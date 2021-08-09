/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2015 the original author or authors.
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

import javax.servlet.jsp.JspException;

import org.springframework.web.util.HtmlUtils;

/**
 * Superclass for tags that output content that might get HTML-escaped.
 *
 * <p>Provides a "htmlEscape" property for explicitly specifying whether to
 * apply HTML escaping. If not set, a page-level default (e.g. from the
 * HtmlEscapeTag) or an application-wide default (the "defaultHtmlEscape"
 * context-param in {@code web.xml}) is used.
 *
 * <p>
 *  超类,用于输出可能获得HTML转义的内容的标签
 * 
 * <p>提供一个"htmlEscape"属性,用于显式指定是否应用HTML转义如果未设置,页级默认(例如从​​HtmlEscapeTag)或应用程序范围的默认值("defaultHtmlEscape"上下
 * 文参数在{@code webxml})。
 * 
 * 
 * @author Juergen Hoeller
 * @author Brian Clozel
 * @since 1.1
 * @see #setHtmlEscape
 * @see HtmlEscapeTag
 * @see org.springframework.web.servlet.support.RequestContext#isDefaultHtmlEscape
 * @see org.springframework.web.util.WebUtils#getDefaultHtmlEscape
 * @see org.springframework.web.util.WebUtils#getResponseEncodedHtmlEscape
 */
@SuppressWarnings("serial")
public abstract class HtmlEscapingAwareTag extends RequestContextAwareTag {

	private Boolean htmlEscape;


	/**
	 * Set HTML escaping for this tag, as boolean value.
	 * Overrides the default HTML escaping setting for the current page.
	 * <p>
	 *  为此标记设置HTML转义为布尔值覆盖当前页面的默认HTML转义设置
	 * 
	 * 
	 * @see HtmlEscapeTag#setDefaultHtmlEscape
	 */
	public void setHtmlEscape(boolean htmlEscape) throws JspException {
		this.htmlEscape = htmlEscape;
	}

	/**
	 * Return the HTML escaping setting for this tag,
	 * or the default setting if not overridden.
	 * <p>
	 *  返回此标记的HTML转义设置,如果不覆盖,则返回默认设置
	 * 
	 * 
	 * @see #isDefaultHtmlEscape()
	 */
	protected boolean isHtmlEscape() {
		if (this.htmlEscape != null) {
			return this.htmlEscape.booleanValue();
		}
		else {
			return isDefaultHtmlEscape();
		}
	}

	/**
	 * Return the applicable default HTML escape setting for this tag.
	 * <p>The default implementation checks the RequestContext's setting,
	 * falling back to {@code false} in case of no explicit default given.
	 * <p>
	 *  返回此标签的适用的默认HTML转义设置<p>默认实现检查RequestContext的设置,如果没有给出明确的默认值,则返回{@code false}
	 * 
	 * 
	 * @see #getRequestContext()
	 */
	protected boolean isDefaultHtmlEscape() {
		return getRequestContext().isDefaultHtmlEscape();
	}

	/**
	 * Return the applicable default for the use of response encoding with
	 * HTML escaping for this tag.
	 * <p>The default implementation checks the RequestContext's setting,
	 * falling back to {@code false} in case of no explicit default given.
	 * <p>
	 * 返回适用的默认值以使用HTML转义的响应编码<p>默认实现检查RequestContext的设置,如果没有给出明确的默认值,则返回{@code false}
	 * 
	 * 
	 * @since 4.1.2
	 * @see #getRequestContext()
	 */
	protected boolean isResponseEncodedHtmlEscape() {
		return getRequestContext().isResponseEncodedHtmlEscape();
	}

	/**
	 * HTML-encodes the given String, only if the "htmlEscape" setting is enabled.
	 * <p>The response encoding will be taken into account if the
	 * "responseEncodedHtmlEscape" setting is enabled as well.
	 * <p>
	 *  HTML编码给定的字符串,只有启用"htmlEscape"设置<p>如果启用了"responseEncodedHtmlEscape"设置,将会考虑响应编码
	 * 
	 * @param content the String to escape
	 * @return the escaped String
	 * @since 4.1.2
	 * @see #isHtmlEscape()
	 * @see #isResponseEncodedHtmlEscape()
	 */
	protected String htmlEscape(String content) {
		String out = content;
		if (isHtmlEscape()) {
			if (isResponseEncodedHtmlEscape()) {
				out = HtmlUtils.htmlEscape(content, this.pageContext.getResponse().getCharacterEncoding());
			}
			else {
				out = HtmlUtils.htmlEscape(content);
			}
		}
		return out;
	}

}
