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

package org.springframework.web.servlet.theme;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

/**
 * {@link org.springframework.web.servlet.ThemeResolver} implementation that
 * uses a theme attribute in the user's session in case of a custom setting,
 * with a fallback to the default theme. This is most appropriate if the
 * application needs user sessions anyway.
 *
 * <p>Custom controllers can override the user's theme by calling
 * {@code setThemeName}, e.g. responding to a theme change request.
 *
 * <p>
 * {@link orgspringframeworkwebservletThemeResolver}实现,在自定义设置的情况下使用用户会话中的主题属性,并将其回退到默认主题如果应用程序需要用户会话,这是最
 * 合适的。
 * 
 *  <p>自定义控制器可以通过调用{@code setThemeName}覆盖用户的主题,例如响应主题更改请求
 * 
 * @author Jean-Pierre Pawlak
 * @author Juergen Hoeller
 * @since 17.06.2003
 * @see #setThemeName
 */
public class SessionThemeResolver extends AbstractThemeResolver {

	/**
	 * Name of the session attribute that holds the theme name.
	 * Only used internally by this implementation.
	 * Use {@code RequestContext(Utils).getTheme()}
	 * to retrieve the current theme in controllers or views.
	 * <p>
	 * 
	 * 
	 * @see org.springframework.web.servlet.support.RequestContext#getTheme
	 * @see org.springframework.web.servlet.support.RequestContextUtils#getTheme
	 */
	public static final String THEME_SESSION_ATTRIBUTE_NAME = SessionThemeResolver.class.getName() + ".THEME";


	@Override
	public String resolveThemeName(HttpServletRequest request) {
		String themeName = (String) WebUtils.getSessionAttribute(request, THEME_SESSION_ATTRIBUTE_NAME);
		// A specific theme indicated, or do we need to fallback to the default?
		return (themeName != null ? themeName : getDefaultThemeName());
	}

	@Override
	public void setThemeName(HttpServletRequest request, HttpServletResponse response, String themeName) {
		WebUtils.setSessionAttribute(request, THEME_SESSION_ATTRIBUTE_NAME,
				(StringUtils.hasText(themeName) ? themeName : null));
	}

}
