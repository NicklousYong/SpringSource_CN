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
import java.util.TimeZone;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.lang.UsesJava7;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleContextResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.util.CookieGenerator;
import org.springframework.web.util.WebUtils;

/**
 * {@link LocaleResolver} implementation that uses a cookie sent back to the user
 * in case of a custom setting, with a fallback to the specified default locale
 * or the request's accept-header locale.
 *
 * <p>This is particularly useful for stateless applications without user sessions.
 * The cookie may optionally contain an associated time zone value as well;
 * alternatively, you may specify a default time zone.
 *
 * <p>Custom controllers can override the user's locale and time zone by calling
 * {@code #setLocale(Context)} on the resolver, e.g. responding to a locale change
 * request. As a more convenient alternative, consider using
 * {@link org.springframework.web.servlet.support.RequestContext#changeLocale}.
 *
 * <p>
 *  {@link LocaleResolver}实现,在使用自定义设置的情况下使用发送回用户的cookie,将其返回到指定的默认语言环境或请求的accept-header语言环境
 * 
 * <p>这对于无用户会话的无状态应用程序特别有用cookie也可以包含关联的时区值;或者,您可以指定默认时区
 * 
 *  <p>自定义控制器可以通过在解析器上调用{@code #setLocale(Context)}覆盖用户的区域设置和时区,例如响应语言环境更改请求作为一个更方便的替代方法,请考虑使用{@link orgspringframeworkwebservletsupportRequestContext#changeLocale}
 * 。
 * 
 * 
 * @author Juergen Hoeller
 * @author Jean-Pierre Pawlak
 * @since 27.02.2003
 * @see #setDefaultLocale
 * @see #setDefaultTimeZone
 */
public class CookieLocaleResolver extends CookieGenerator implements LocaleContextResolver {

	/**
	 * The name of the request attribute that holds the Locale.
	 * <p>Only used for overriding a cookie value if the locale has been
	 * changed in the course of the current request!
	 * <p>Use {@code RequestContext(Utils).getLocale()}
	 * to retrieve the current locale in controllers or views.
	 * <p>
	 *  持有区域设置的请求属性的名称<p>仅在当前请求过程中更改了区域设置时才用于覆盖cookie值！ <p>使用{@code RequestContext(Utils)getLocale()}来检索控制器或
	 * ​​视图中的当前区域设置。
	 * 
	 * 
	 * @see org.springframework.web.servlet.support.RequestContext#getLocale
	 * @see org.springframework.web.servlet.support.RequestContextUtils#getLocale
	 */
	public static final String LOCALE_REQUEST_ATTRIBUTE_NAME = CookieLocaleResolver.class.getName() + ".LOCALE";

	/**
	 * The name of the request attribute that holds the TimeZone.
	 * <p>Only used for overriding a cookie value if the locale has been
	 * changed in the course of the current request!
	 * <p>Use {@code RequestContext(Utils).getTimeZone()}
	 * to retrieve the current time zone in controllers or views.
	 * <p>
	 * 持有TimeZone <p>的请求属性的名称仅在当前请求过程中更改了该区域设置时才用于覆盖cookie值！ <p>使用{@code RequestContext(Utils)getTimeZone()}
	 * 来检索控制器或​​视图中的当前时区。
	 * 
	 * 
	 * @see org.springframework.web.servlet.support.RequestContext#getTimeZone
	 * @see org.springframework.web.servlet.support.RequestContextUtils#getTimeZone
	 */
	public static final String TIME_ZONE_REQUEST_ATTRIBUTE_NAME = CookieLocaleResolver.class.getName() + ".TIME_ZONE";

	/**
	 * The default cookie name used if none is explicitly set.
	 * <p>
	 *  如果没有显式设置,则使用默认的cookie名称
	 * 
	 */
	public static final String DEFAULT_COOKIE_NAME = CookieLocaleResolver.class.getName() + ".LOCALE";


	private boolean languageTagCompliant = false;

	private Locale defaultLocale;

	private TimeZone defaultTimeZone;


	/**
	 * Create a new instance of the {@link CookieLocaleResolver} class
	 * using the {@link #DEFAULT_COOKIE_NAME default cookie name}.
	 * <p>
	 *  使用{@link #DEFAULT_COOKIE_NAME默认Cookie名称}创建{@link CookieLocaleResolver}类的新实例
	 * 
	 */
	public CookieLocaleResolver() {
		setCookieName(DEFAULT_COOKIE_NAME);
	}


	/**
	 * Specify whether this resolver's cookies should be compliant with BCP 47
	 * language tags instead of Java's legacy locale specification format.
	 * The default is {@code false}.
	 * <p>Note: This mode requires JDK 7 or higher. Set this flag to {@code true}
	 * for BCP 47 compliance on JDK 7+ only.
	 * <p>
	 *  指定此解析器的Cookie是否应符合BCP 47语言标记,而不是Java的旧版本规范格式默认为{@code false} <p>注意：此模式需要JDK 7或更高版本将此标志设置为{@code true}
	 *  BCP 47仅适用于JDK 7+。
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
	 * Return whether this resolver's cookies should be compliant with BCP 47
	 * language tags instead of Java's legacy locale specification format.
	 * <p>
	 * 返回此解析器的Cookie是否应符合BCP 47语言标记,而不是Java的传统语言环境规范格式
	 * 
	 * 
	 * @since 4.3
	 */
	public boolean isLanguageTagCompliant() {
		return this.languageTagCompliant;
	}

	/**
	 * Set a fixed Locale that this resolver will return if no cookie found.
	 * <p>
	 *  设置一个固定的区域设置,如果没有找到cookie,该解析器将返回
	 * 
	 */
	public void setDefaultLocale(Locale defaultLocale) {
		this.defaultLocale = defaultLocale;
	}

	/**
	 * Return the fixed Locale that this resolver will return if no cookie found,
	 * if any.
	 * <p>
	 *  如果没有找到cookie,返回此解析器将返回的固定区域设置(如果有)
	 * 
	 */
	protected Locale getDefaultLocale() {
		return this.defaultLocale;
	}

	/**
	 * Set a fixed TimeZone that this resolver will return if no cookie found.
	 * <p>
	 *  设置一个固定的TimeZone,如果没有找到cookie,该解析器将返回
	 * 
	 * 
	 * @since 4.0
	 */
	public void setDefaultTimeZone(TimeZone defaultTimeZone) {
		this.defaultTimeZone = defaultTimeZone;
	}

	/**
	 * Return the fixed TimeZone that this resolver will return if no cookie found,
	 * if any.
	 * <p>
	 *  如果没有找到cookie,返回此解析器将返回的固定TimeZone(如果有)
	 * 
	 * 
	 * @since 4.0
	 */
	protected TimeZone getDefaultTimeZone() {
		return this.defaultTimeZone;
	}


	@Override
	public Locale resolveLocale(HttpServletRequest request) {
		parseLocaleCookieIfNecessary(request);
		return (Locale) request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME);
	}

	@Override
	public LocaleContext resolveLocaleContext(final HttpServletRequest request) {
		parseLocaleCookieIfNecessary(request);
		return new TimeZoneAwareLocaleContext() {
			@Override
			public Locale getLocale() {
				return (Locale) request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME);
			}
			@Override
			public TimeZone getTimeZone() {
				return (TimeZone) request.getAttribute(TIME_ZONE_REQUEST_ATTRIBUTE_NAME);
			}
		};
	}

	private void parseLocaleCookieIfNecessary(HttpServletRequest request) {
		if (request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME) == null) {
			// Retrieve and parse cookie value.
			Cookie cookie = WebUtils.getCookie(request, getCookieName());
			Locale locale = null;
			TimeZone timeZone = null;
			if (cookie != null) {
				String value = cookie.getValue();
				String localePart = value;
				String timeZonePart = null;
				int spaceIndex = localePart.indexOf(' ');
				if (spaceIndex != -1) {
					localePart = value.substring(0, spaceIndex);
					timeZonePart = value.substring(spaceIndex + 1);
				}
				locale = (!"-".equals(localePart) ? parseLocaleValue(localePart) : null);
				if (timeZonePart != null) {
					timeZone = StringUtils.parseTimeZoneString(timeZonePart);
				}
				if (logger.isDebugEnabled()) {
					logger.debug("Parsed cookie value [" + cookie.getValue() + "] into locale '" + locale +
							"'" + (timeZone != null ? " and time zone '" + timeZone.getID() + "'" : ""));
				}
			}
			request.setAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME,
					(locale != null ? locale : determineDefaultLocale(request)));
			request.setAttribute(TIME_ZONE_REQUEST_ATTRIBUTE_NAME,
					(timeZone != null ? timeZone : determineDefaultTimeZone(request)));
		}
	}

	@Override
	public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		setLocaleContext(request, response, (locale != null ? new SimpleLocaleContext(locale) : null));
	}

	@Override
	public void setLocaleContext(HttpServletRequest request, HttpServletResponse response, LocaleContext localeContext) {
		Locale locale = null;
		TimeZone timeZone = null;
		if (localeContext != null) {
			locale = localeContext.getLocale();
			if (localeContext instanceof TimeZoneAwareLocaleContext) {
				timeZone = ((TimeZoneAwareLocaleContext) localeContext).getTimeZone();
			}
			addCookie(response,
					(locale != null ? toLocaleValue(locale) : "-") + (timeZone != null ? ' ' + timeZone.getID() : ""));
		}
		else {
			removeCookie(response);
		}
		request.setAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME,
				(locale != null ? locale : determineDefaultLocale(request)));
		request.setAttribute(TIME_ZONE_REQUEST_ATTRIBUTE_NAME,
				(timeZone != null ? timeZone : determineDefaultTimeZone(request)));
	}


	/**
	 * Parse the given locale value coming from an incoming cookie.
	 * <p>The default implementation calls {@link StringUtils#parseLocaleString(String)}
	 * or JDK 7's {@link Locale#forLanguageTag(String)}, depending on the
	 * {@link #setLanguageTagCompliant "languageTagCompliant"} configuration property.
	 * <p>
	 *  解析来自传入cookie的给定语言环境值<p>默认实现会调用{@link StringUtils#parseLocaleString(String)}或JDK 7的{@link Locale#forLanguageTag(String)}
	 * ,具体取决于{@link #setLanguageTagCompliant" languageTagCompliant"}配置属性。
	 * 
	 * 
	 * @param locale the locale value to parse
	 * @return the corresponding {@code Locale} instance
	 * @since 4.3
	 */
	@UsesJava7
	protected Locale parseLocaleValue(String locale) {
		return (isLanguageTagCompliant() ? Locale.forLanguageTag(locale) : StringUtils.parseLocaleString(locale));
	}

	/**
	 * Render the given locale as a text value for inclusion in a cookie.
	 * <p>The default implementation calls {@link Locale#toString()}
	 * or JDK 7's {@link Locale#toLanguageTag()}, depending on the
	 * {@link #setLanguageTagCompliant "languageTagCompliant"} configuration property.
	 * <p>
	 * 将给定的语言环境渲染为包含在cookie中的文本值<p>默认实现会调用{@link Locale#toString()}或JDK 7的{@link Locale#toLanguageTag()},具体取
	 * 决于{@link #setLanguageTagCompliant "languageTagCompliant"}配置属性。
	 * 
	 * 
	 * @param locale the locale to stringify
	 * @return a String representation for the given locale
	 * @since 4.3
	 */
	@UsesJava7
	protected String toLocaleValue(Locale locale) {
		return (isLanguageTagCompliant() ? locale.toLanguageTag() : locale.toString());
	}

	/**
	 * Determine the default locale for the given request,
	 * Called if no locale cookie has been found.
	 * <p>The default implementation returns the specified default locale,
	 * if any, else falls back to the request's accept-header locale.
	 * <p>
	 *  确定给定请求的默认区域设置,如果没有找到区域设置cookie,则调用。<p>默认实现返回指定的默认语言环境(如果有),则返回到请求的accept-header语言环境
	 * 
	 * 
	 * @param request the request to resolve the locale for
	 * @return the default locale (never {@code null})
	 * @see #setDefaultLocale
	 * @see javax.servlet.http.HttpServletRequest#getLocale()
	 */
	protected Locale determineDefaultLocale(HttpServletRequest request) {
		Locale defaultLocale = getDefaultLocale();
		if (defaultLocale == null) {
			defaultLocale = request.getLocale();
		}
		return defaultLocale;
	}

	/**
	 * Determine the default time zone for the given request,
	 * Called if no TimeZone cookie has been found.
	 * <p>The default implementation returns the specified default time zone,
	 * if any, or {@code null} otherwise.
	 * <p>
	 *  确定给定请求的默认时区,如果没有找到TimeZone cookie,则调用。<p>默认实现返回指定的默认时区(如果有)或{@code null}否则
	 * 
	 * @param request the request to resolve the time zone for
	 * @return the default time zone (or {@code null} if none defined)
	 * @see #setDefaultTimeZone
	 */
	protected TimeZone determineDefaultTimeZone(HttpServletRequest request) {
		return getDefaultTimeZone();
	}

}
