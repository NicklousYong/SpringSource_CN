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

package org.springframework.web.servlet.tags;

import javax.servlet.jsp.JspException;

/**
 * Sets default HTML escape value for the current page. The actual value
 * can be overridden by escaping-aware tags. The default is "false".
 *
 * <p>Note: You can also set a "defaultHtmlEscape" web.xml context-param.
 * A page-level setting overrides a context-param.
 *
 * <p>
 * 
 * @author Juergen Hoeller
 * @since 04.03.2003
 * @see HtmlEscapingAwareTag#setHtmlEscape
 */
@SuppressWarnings("serial")
public class HtmlEscapeTag extends RequestContextAwareTag {

	private boolean defaultHtmlEscape;


	/**
	 * Set the default value for HTML escaping,
	 * to be put into the current PageContext.
	 * <p>
	 *  设置当前页面的默认HTML转义值实际值可以通过转义感知标签覆盖默认值为"false"
	 * 
	 * <p>注意：您还可以设置"defaultHtmlEscape"webxml上下文参数页面级设置覆盖上下文参数
	 * 
	 */
	public void setDefaultHtmlEscape(boolean defaultHtmlEscape) {
		this.defaultHtmlEscape = defaultHtmlEscape;
	}


	@Override
	protected int doStartTagInternal() throws JspException {
		getRequestContext().setDefaultHtmlEscape(this.defaultHtmlEscape);
		return EVAL_BODY_INCLUDE;
	}

}
