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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.web.util.WebUtils;

/**
 * {@link org.springframework.web.servlet.LocaleResolver} implementation that
 * uses a locale attribute in the user's session in case of a custom setting,
 * with a fallback to the specified default locale or the request's
 * accept-header locale.
 *
 * <p>This is most appropriate if the application needs user sessions anyway,
 * i.e. when the {@code HttpSession} does not have to be created just for storing
 * the user's locale. The session may optionally contain an associated time zone
 * attribute as well; alternatively, you may specify a default time zone.
 *
 * <p>Custom controllers can override the user's locale and time zone by calling
 * {@code #setLocale(Context)} on the resolver, e.g. responding to a locale change
 * request. As a more convenient alternative, consider using
 * {@link org.springframework.web.servlet.support.RequestContext#changeLocale}.
 *
 * <p>In contrast to {@link CookieLocaleResolver}, this strategy stores locally
 * chosen locale settings in the Servlet container's {@code HttpSession}. As a
 * consequence, those settings are just temporary for each session and therefore
 * lost when each session terminates.
 *
 * <p>Note that there is no direct relationship with external session management
 * mechanisms such as the "Spring Session" project. This {@code LocaleResolver}
 * will simply evaluate and modify corresponding {@code HttpSession} attributes
 * against the current {@code HttpServletRequest}.
 *
 * <p>
 * {@link orgspringframeworkwebservletLocaleResolver}实现,在使用自定义设置的情况下使用用户会话中的区域设置属性,将其返回到指定的默认语言环境或请求的acc
 * ept-header语言环境。
 * 
 *  如果应用程序需要用户会话,即不需要仅为存储用户的区域设置创建{@code HttpSession},则这是最合适的。会话可以可选地包含关联的时区属性;或者,您可以指定默认时区
 * 
 * <p>自定义控制器可以通过在解析器上调用{@code #setLocale(Context)}覆盖用户的区域设置和时区,例如响应语言环境更改请求作为一个更方便的替代方法,请考虑使用{@link orgspringframeworkwebservletsupportRequestContext#changeLocale}
 * 。
 * 
 *  <p>与{@link CookieLocaleResolver}相反,此策略将本地选择的区域设置存储在Servlet容器的{@code HttpSession}中。
 * 因此,这些设置对于每个会话都是临时的,因此每个会话终止时丢失。
 * 
 * 请注意,与"会话"项目之类的外部会话管理机制没有直接的关系。
 * {@code LocaleResolver}将简单地根据当前的{@code HttpServletRequest}评估和修改相应的{@code HttpSession}属性,。
 * 
 * @author Juergen Hoeller
 * @since 27.02.2003
 * @see #setDefaultLocale
 * @see #setDefaultTimeZone
 */
public class SessionLocaleResolver extends AbstractLocaleContextResolver {

	/**
	 * Name of the session attribute that holds the Locale.
	 * Only used internally by this implementation.
	 * <p>Use {@code RequestContext(Utils).getLocale()}
	 * to retrieve the current locale in controllers or views.
	 * <p>
	 * 
	 * 
	 * @see org.springframework.web.servlet.support.RequestContext#getLocale
	 * @see org.springframework.web.servlet.support.RequestContextUtils#getLocale
	 */
	public static final String LOCALE_SESSION_ATTRIBUTE_NAME = SessionLocaleResolver.class.getName() + ".LOCALE";

	/**
	 * Name of the session attribute that holds the TimeZone.
	 * Only used internally by this implementation.
	 * <p>Use {@code RequestContext(Utils).getTimeZone()}
	 * to retrieve the current time zone in controllers or views.
	 * <p>
	 *  持有区域设置的会话属性的名称仅在此实现内部使用<p>使用{@code RequestContext(Utils)getLocale()}来检索控制器或​​视图中的当前区域设置
	 * 
	 * 
	 * @see org.springframework.web.servlet.support.RequestContext#getTimeZone
	 * @see org.springframework.web.servlet.support.RequestContextUtils#getTimeZone
	 */
	public static final String TIME_ZONE_SESSION_ATTRIBUTE_NAME = SessionLocaleResolver.class.getName() + ".TIME_ZONE";


	@Override
	public Locale resolveLocale(HttpServletRequest request) {
		Locale locale = (Locale) WebUtils.getSessionAttribute(request, LOCALE_SESSION_ATTRIBUTE_NAME);
		if (locale == null) {
			locale = determineDefaultLocale(request);
		}
		return locale;
	}

	@Override
	public LocaleContext resolveLocaleContext(final HttpServletRequest request) {
		return new TimeZoneAwareLocaleContext() {
			@Override
			public Locale getLocale() {
				Locale locale = (Locale) WebUtils.getSessionAttribute(request, LOCALE_SESSION_ATTRIBUTE_NAME);
				if (locale == null) {
					locale = determineDefaultLocale(request);
				}
				return locale;
			}
			@Override
			public TimeZone getTimeZone() {
				TimeZone timeZone = (TimeZone) WebUtils.getSessionAttribute(request, TIME_ZONE_SESSION_ATTRIBUTE_NAME);
				if (timeZone == null) {
					timeZone = determineDefaultTimeZone(request);
				}
				return timeZone;
			}
		};
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
		}
		WebUtils.setSessionAttribute(request, LOCALE_SESSION_ATTRIBUTE_NAME, locale);
		WebUtils.setSessionAttribute(request, TIME_ZONE_SESSION_ATTRIBUTE_NAME, timeZone);
	}


	/**
	 * Determine the default locale for the given request,
	 * Called if no Locale session attribute has been found.
	 * <p>The default implementation returns the specified default locale,
	 * if any, else falls back to the request's accept-header locale.
	 * <p>
	 *  持有TimeZone的会话属性的名称仅在此实现内部使用<p>使用{@code RequestContext(Utils)getTimeZone()}来检索控制器或​​视图中的当前时区
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
	 * Called if no TimeZone session attribute has been found.
	 * <p>The default implementation returns the specified default time zone,
	 * if any, or {@code null} otherwise.
	 * <p>
	 * 确定给定请求的默认区域设置,如果没有找到Locale会话属性,则调用该值。<p>默认实现返回指定的默认语言环境(如果有),则返回到请求的accept-header语言环境
	 * 
	 * 
	 * @param request the request to resolve the time zone for
	 * @return the default time zone (or {@code null} if none defined)
	 * @see #setDefaultTimeZone
	 */
	protected TimeZone determineDefaultTimeZone(HttpServletRequest request) {
		return getDefaultTimeZone();
	}

}
