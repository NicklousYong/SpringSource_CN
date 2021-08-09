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

package org.springframework.web.servlet.i18n;

import java.util.Locale;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.lang.UsesJava7;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * Interceptor that allows for changing the current locale on every request,
 * via a configurable request parameter (default parameter name: "locale").
 *
 * <p>
 *  拦截器允许通过可配置的请求参数(默认参数名称"locale")在每个请求上更改当前区域设置
 * 
 * 
 * @author Juergen Hoeller
 * @author Rossen Stoyanchev
 * @since 20.06.2003
 * @see org.springframework.web.servlet.LocaleResolver
 */
public class LocaleChangeInterceptor extends HandlerInterceptorAdapter {

	/**
	 * Default name of the locale specification parameter: "locale".
	 * <p>
	 *  语言环境规范参数的默认名称："locale"
	 * 
	 */
	public static final String DEFAULT_PARAM_NAME = "locale";


	protected final Log logger = LogFactory.getLog(getClass());

	private String paramName = DEFAULT_PARAM_NAME;

	private String[] httpMethods;

	private boolean ignoreInvalidLocale = false;

	private boolean languageTagCompliant = false;


	/**
	 * Set the name of the parameter that contains a locale specification
	 * in a locale change request. Default is "locale".
	 * <p>
	 * 在区域设置更改请求中设置包含区域设置规范的参数的名称默认为"locale"
	 * 
	 */
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	/**
	 * Return the name of the parameter that contains a locale specification
	 * in a locale change request.
	 * <p>
	 *  在区域设置更改请求中返回包含区域设置规范的参数的名称
	 * 
	 */
	public String getParamName() {
		return this.paramName;
	}

	/**
	 * Configure the HTTP method(s) over which the locale can be changed.
	 * <p>
	 *  配置可以更改语言环境的HTTP方法
	 * 
	 * 
	 * @param httpMethods the methods
	 * @since 4.2
	 */
	public void setHttpMethods(String... httpMethods) {
		this.httpMethods = httpMethods;
	}

	/**
	 * Return the configured HTTP methods.
	 * <p>
	 *  返回配置的HTTP方法
	 * 
	 * 
	 * @since 4.2
	 */
	public String[] getHttpMethods() {
		return this.httpMethods;
	}

	/**
	 * Set whether to ignore an invalid value for the locale parameter.
	 * <p>
	 *  设置是否忽略区域设置参数的无效值
	 * 
	 * 
	 * @since 4.2.2
	 */
	public void setIgnoreInvalidLocale(boolean ignoreInvalidLocale) {
		this.ignoreInvalidLocale = ignoreInvalidLocale;
	}

	/**
	 * Return whether to ignore an invalid value for the locale parameter.
	 * <p>
	 *  返回是否忽略区域设置参数的无效值
	 * 
	 * 
	 * @since 4.2.2
	 */
	public boolean isIgnoreInvalidLocale() {
		return this.ignoreInvalidLocale;
	}

	/**
	 * Specify whether to parse request parameter values as BCP 47 language tags
	 * instead of Java's legacy locale specification format.
	 * The default is {@code false}.
	 * <p>Note: This mode requires JDK 7 or higher. Set this flag to {@code true}
	 * for BCP 47 compliance on JDK 7+ only.
	 * <p>
	 *  指定是否将请求参数值解析为BCP 47语言标记,而不是Java的旧语言环境规范格式默认为{@code false} <p>注意：此模式需要JDK 7或更高版本将此标志设置为{@code true}仅适
	 * 用于JDK 7+的合规性。
	 * 
	 * 
	 * @since 4.3
	 * @see Locale#forLanguageTag(String)
	 * @see Locale#toLanguageTag()
	 */
	public void setLanguageTagCompliant(boolean languageTagCompliant) {
		this.languageTagCompliant = languageTagCompliant;
	}

	/**
	 * Return whether to use BCP 47 language tags instead of Java's legacy
	 * locale specification format.
	 * <p>
	 * 返回是否使用BCP 47语言标记,而不是Java的旧版本区域设置规范格式
	 * 
	 * 
	 * @since 4.3
	 */
	public boolean isLanguageTagCompliant() {
		return this.languageTagCompliant;
	}


	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws ServletException {

		String newLocale = request.getParameter(getParamName());
		if (newLocale != null) {
			if (checkHttpMethod(request.getMethod())) {
				LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
				if (localeResolver == null) {
					throw new IllegalStateException(
							"No LocaleResolver found: not in a DispatcherServlet request?");
				}
				try {
					localeResolver.setLocale(request, response, parseLocaleValue(newLocale));
				}
				catch (IllegalArgumentException ex) {
					if (isIgnoreInvalidLocale()) {
						logger.debug("Ignoring invalid locale value [" + newLocale + "]: " + ex.getMessage());
					}
					else {
						throw ex;
					}
				}
			}
		}
		// Proceed in any case.
		return true;
	}

	private boolean checkHttpMethod(String currentMethod) {
		String[] configuredMethods = getHttpMethods();
		if (ObjectUtils.isEmpty(configuredMethods)) {
			return true;
		}
		for (String configuredMethod : configuredMethods) {
			if (configuredMethod.equalsIgnoreCase(currentMethod)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Parse the given locale value as coming from a request parameter.
	 * <p>The default implementation calls {@link StringUtils#parseLocaleString(String)}
	 * or JDK 7's {@link Locale#forLanguageTag(String)}, depending on the
	 * {@link #setLanguageTagCompliant "languageTagCompliant"} configuration property.
	 * <p>
	 *  将给定的区域设置值解析为来自请求参数<p>默认的实现方法会调用{@link StringUtils#parseLocaleString(String)}或JDK 7的{@link Locale#forLanguageTag(String)}
	 * ,具体取决于{@link #setLanguageTagCompliant "languageTagCompliant"}配置属性。
	 * 
	 * @param locale the locale value to parse
	 * @return the corresponding {@code Locale} instance
	 * @since 4.3
	 */
	@UsesJava7
	protected Locale parseLocaleValue(String locale) {
		return (isLanguageTagCompliant() ? Locale.forLanguageTag(locale) : StringUtils.parseLocaleString(locale));
	}

}
