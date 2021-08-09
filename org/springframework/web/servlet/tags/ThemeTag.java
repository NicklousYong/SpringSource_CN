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

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

/**
 * Custom tag to look up a theme message in the scope of this page.
 * Messages are looked up using the ApplicationContext's ThemeSource,
 * and thus should support internationalization.
 *
 * <p>Regards a HTML escaping setting, either on this tag instance,
 * the page level, or the web.xml level.
 *
 * <p>If "code" isn't set or cannot be resolved, "text" will be used
 * as default message.
 *
 * <p>Message arguments can be specified via the {@link #setArguments(Object) arguments}
 * attribute or by using nested {@code <spring:argument>} tags.
 *
 * <p>
 *  自定义标签在本页范围内查找主题消息使用ApplicationContext的ThemeSource查询消息,因此应支持国际化
 * 
 * <p>关于此HTML标签实例,页面级别或webxml级别的HTML转义设置
 * 
 *  <p>如果"代码"未设置或无法解析,则"文本"将用作默认消息
 * 
 *  <p>可以通过{@link #setArguments(Object)arguments}属性或使用嵌套的{@code <spring：argument>}标记指定消息参数
 * 
 * @author Jean-Pierre Pawlak
 * @author Juergen Hoeller
 * @see org.springframework.ui.context.Theme
 * @see org.springframework.ui.context.ThemeSource
 * @see #setCode
 * @see #setText
 * @see #setHtmlEscape
 * @see HtmlEscapeTag#setDefaultHtmlEscape
 * @see org.springframework.web.util.WebUtils#HTML_ESCAPE_CONTEXT_PARAM
 * @see ArgumentTag
 */
@SuppressWarnings("serial")
public class ThemeTag extends MessageTag {

	/**
	 * Use the theme MessageSource for theme message resolution.
	 * <p>
	 * 
	 */
	@Override
	protected MessageSource getMessageSource() {
		return getRequestContext().getTheme().getMessageSource();
	}

	/**
	 * Return exception message that indicates the current theme.
	 * <p>
	 *  使用主题MessageSource进行主题消息解析
	 * 
	 */
	@Override
	protected String getNoSuchMessageExceptionDescription(NoSuchMessageException ex) {
		return "Theme '" + getRequestContext().getTheme().getName() + "': " + ex.getMessage();
	}

}
