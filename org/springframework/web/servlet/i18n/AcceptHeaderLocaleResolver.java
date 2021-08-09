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

package org.springframework.web.servlet.i18n;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.LocaleResolver;

/**
 * {@link LocaleResolver} implementation that simply uses the primary locale
 * specified in the "accept-language" header of the HTTP request (that is,
 * the locale sent by the client browser, normally that of the client's OS).
 *
 * <p>Note: Does not support {@code setLocale}, since the accept header
 * can only be changed through changing the client's locale settings.
 *
 * <p>
 *  {@link LocaleResolver}实现,只需使用HTTP请求的"accept-language"头中指定的主语言环境(即,客户端浏览器发送的语言环境,通常是客户端的操作系统)
 * 
 * <p>注意：不支持{@code setLocale},因为accept标题只能通过更改客户端的区域设置来更改
 * 
 * 
 * @author Juergen Hoeller
 * @author Rossen Stoyanchev
 * @since 27.02.2003
 * @see javax.servlet.http.HttpServletRequest#getLocale()
 */
public class AcceptHeaderLocaleResolver implements LocaleResolver {

	private final List<Locale> supportedLocales = new ArrayList<Locale>(4);

	private Locale defaultLocale;


	/**
	 * Configure supported locales to check against the requested locales
	 * determined via {@link HttpServletRequest#getLocales()}. If this is not
	 * configured then {@link HttpServletRequest#getLocale()} is used instead.
	 * <p>
	 *  配置支持的语言环境来检查通过{@link HttpServletRequest#getLocales())确定的请求的语言环境。
	 * 如果未配置,则使用{@link HttpServletRequest#getLocale()}。
	 * 
	 * 
	 * @param locales the supported locales
	 * @since 4.3
	 */
	public void setSupportedLocales(List<Locale> locales) {
		this.supportedLocales.clear();
		if (locales != null) {
			this.supportedLocales.addAll(locales);
		}
	}

	/**
	 * Return the configured list of supported locales.
	 * <p>
	 *  返回配置的支持的区域设置列表
	 * 
	 * 
	 * @since 4.3
	 */
	public List<Locale> getSupportedLocales() {
		return this.supportedLocales;
	}

	/**
	 * Configure a fixed default locale to fall back on if the request does not
	 * have an "Accept-Language" header.
	 * <p>By default this is not set in which case when there is "Accept-Language"
	 * header, the default locale for the server is used as defined in
	 * {@link HttpServletRequest#getLocale()}.
	 * <p>
	 *  如果请求没有"Accept-Language"标头,则配置一个固定的默认区域设置。
	 * <p>默认情况下,当没有设置"Accept-Language"标头时,服务器的默认语言环境按照{@link HttpServletRequest#getLocale()}中的定义使用。
	 * 
	 * 
	 * @param defaultLocale the default locale to use
	 * @since 4.3
	 */
	public void setDefaultLocale(Locale defaultLocale) {
		this.defaultLocale = defaultLocale;
	}

	/**
	 * The configured default locale, if any.
	 * <p>
	 * 
	 * @since 4.3
	 */
	public Locale getDefaultLocale() {
		return this.defaultLocale;
	}


	@Override
	public Locale resolveLocale(HttpServletRequest request) {
		Locale defaultLocale = getDefaultLocale();
		if (defaultLocale != null && request.getHeader("Accept-Language") == null) {
			return defaultLocale;
		}
		Locale locale = request.getLocale();
		if (!isSupportedLocale(locale)) {
			locale = findSupportedLocale(request, locale);
		}
		return locale;
	}

	private boolean isSupportedLocale(Locale locale) {
		List<Locale> supportedLocales = getSupportedLocales();
		return (supportedLocales.isEmpty() || supportedLocales.contains(locale));
	}

	private Locale findSupportedLocale(HttpServletRequest request, Locale fallback) {
		Enumeration<Locale> requestLocales = request.getLocales();
		while (requestLocales.hasMoreElements()) {
			Locale locale = requestLocales.nextElement();
			if (getSupportedLocales().contains(locale)) {
				return locale;
			}
		}
		return fallback;
	}

	@Override
	public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		throw new UnsupportedOperationException(
				"Cannot change HTTP accept header - use a different locale resolution strategy");
	}

}
