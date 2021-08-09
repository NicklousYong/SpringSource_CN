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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ThemeResolver;
import org.springframework.web.util.CookieGenerator;
import org.springframework.web.util.WebUtils;

/**
 * {@link ThemeResolver} implementation that uses a cookie sent back to the user
 * in case of a custom setting, with a fallback to the default theme.
 * This is particularly useful for stateless applications without user sessions.
 *
 * <p>Custom controllers can thus override the user's theme by calling
 * {@code setThemeName}, e.g. responding to a certain theme change request.
 *
 * <p>
 *  {@link ThemeResolver}实现,在自定义设置的情况下使用发送回用户的cookie,并将其回退到默认主题对于无用户应用程序而言,这是非常有用的
 * 
 * <p>自定义控制器可以通过调用{@code setThemeName}覆盖用户的主题,例如响应某个主题更改请求
 * 
 * 
 * @author Jean-Pierre Pawlak
 * @author Juergen Hoeller
 * @since 17.06.2003
 * @see #setThemeName
 */
public class CookieThemeResolver extends CookieGenerator implements ThemeResolver {

	public final static String ORIGINAL_DEFAULT_THEME_NAME = "theme";

	/**
	 * Name of the request attribute that holds the theme name. Only used
	 * for overriding a cookie value if the theme has been changed in the
	 * course of the current request! Use RequestContext.getTheme() to
	 * retrieve the current theme in controllers or views.
	 * <p>
	 *  持有主题名称的请求属性的名称仅在当前请求过程中更改主题时才用于覆盖Cookie值！使用RequestContextgetTheme()在控制器或视图中检索当前主题
	 * 
	 * 
	 * @see org.springframework.web.servlet.support.RequestContext#getTheme
	 */
	public static final String THEME_REQUEST_ATTRIBUTE_NAME = CookieThemeResolver.class.getName() + ".THEME";

	public static final String DEFAULT_COOKIE_NAME = CookieThemeResolver.class.getName() + ".THEME";


	private String defaultThemeName = ORIGINAL_DEFAULT_THEME_NAME;


	public CookieThemeResolver() {
		setCookieName(DEFAULT_COOKIE_NAME);
	}


	/**
	 * Set the name of the default theme.
	 * <p>
	 *  设置默认主题的名称
	 * 
	 */
	public void setDefaultThemeName(String defaultThemeName) {
		this.defaultThemeName = defaultThemeName;
	}

	/**
	 * Return the name of the default theme.
	 * <p>
	 *  返回默认主题的名称
	 */
	public String getDefaultThemeName() {
		return defaultThemeName;
	}


	@Override
	public String resolveThemeName(HttpServletRequest request) {
		// Check request for preparsed or preset theme.
		String themeName = (String) request.getAttribute(THEME_REQUEST_ATTRIBUTE_NAME);
		if (themeName != null) {
			return themeName;
		}

		// Retrieve cookie value from request.
		Cookie cookie = WebUtils.getCookie(request, getCookieName());
		if (cookie != null) {
			String value = cookie.getValue();
			if (StringUtils.hasText(value)) {
				themeName = value;
			}
		}

		// Fall back to default theme.
		if (themeName == null) {
			themeName = getDefaultThemeName();
		}
		request.setAttribute(THEME_REQUEST_ATTRIBUTE_NAME, themeName);
		return themeName;
	}

	@Override
	public void setThemeName(HttpServletRequest request, HttpServletResponse response, String themeName) {
		if (StringUtils.hasText(themeName)) {
			// Set request attribute and add cookie.
			request.setAttribute(THEME_REQUEST_ATTRIBUTE_NAME, themeName);
			addCookie(response, themeName);
		}
		else {
			// Set request attribute to fallback theme and remove cookie.
			request.setAttribute(THEME_REQUEST_ATTRIBUTE_NAME, getDefaultThemeName());
			removeCookie(response);
		}
	}

}
