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

package org.springframework.web.servlet.i18n;

import java.util.Locale;
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.web.servlet.LocaleContextResolver;

/**
 * Abstract base class for {@link LocaleContextResolver} implementations.
 * Provides support for a default locale and a default time zone.
 *
 * <p>Also provides pre-implemented versions of {@link #resolveLocale} and {@link #setLocale},
 * delegating to {@link #resolveLocaleContext} and {@link #setLocaleContext}.
 *
 * <p>
 *  {@link LocaleContextResolver}实现的抽象基类提供对默认语言环境和默认时区的支持
 * 
 * <p>还提供了{@link #resolveLocale}和{@link #setLocale}的预实现版本,委托给{@link #resolveLocaleContext}和{@link #setLocaleContext}
 * 。
 * 
 * 
 * @author Juergen Hoeller
 * @since 4.0
 * @see #setDefaultLocale
 * @see #setDefaultTimeZone
 */
public abstract class AbstractLocaleContextResolver extends AbstractLocaleResolver implements LocaleContextResolver {

	private TimeZone defaultTimeZone;


	/**
	 * Set a default TimeZone that this resolver will return if no other time zone found.
	 * <p>
	 *  设置默认的TimeZone,如果没有找到其他时区,此解析器将返回
	 * 
	 */
	public void setDefaultTimeZone(TimeZone defaultTimeZone) {
		this.defaultTimeZone = defaultTimeZone;
	}

	/**
	 * Return the default TimeZone that this resolver is supposed to fall back to, if any.
	 * <p>
	 *  返回此解析器应该返回的默认TimeZone(如果有)
	 */
	public TimeZone getDefaultTimeZone() {
		return this.defaultTimeZone;
	}


	@Override
	public Locale resolveLocale(HttpServletRequest request) {
		return resolveLocaleContext(request).getLocale();
	}

	@Override
	public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		setLocaleContext(request, response, (locale != null ? new SimpleLocaleContext(locale) : null));
	}

}
