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

package org.springframework.web.servlet.support;

import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.ui.context.Theme;
import org.springframework.ui.context.ThemeSource;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.FlashMapManager;
import org.springframework.web.servlet.LocaleContextResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ThemeResolver;

/**
 * Utility class for easy access to request-specific state which has been
 * set by the {@link org.springframework.web.servlet.DispatcherServlet}.
 *
 * <p>Supports lookup of current WebApplicationContext, LocaleResolver,
 * Locale, ThemeResolver, Theme, and MultipartResolver.
 *
 * <p>
 *  实用程序类可以方便地访问由{@link orgspringframeworkwebservletDispatcherServlet}设置的请求特定状态
 * 
 * <p>支持查找当前的WebApplicationContext,LocaleResolver,Locale,ThemeResolver,Theme和MultipartResolver
 * 
 * 
 * @author Juergen Hoeller
 * @author Rossen Stoyanchev
 * @since 03.03.2003
 * @see RequestContext
 * @see org.springframework.web.servlet.DispatcherServlet
 */
public abstract class RequestContextUtils {

	/**
	 * The name of the bean to use to look up in an implementation of
	 * {@link RequestDataValueProcessor} has been configured.
	 * <p>
	 *  用于查找{@link RequestDataValueProcessor}实现的bean的名称已被配置
	 * 
	 * 
	 * @since 4.2.1
	 */
	public static final String REQUEST_DATA_VALUE_PROCESSOR_BEAN_NAME = "requestDataValueProcessor";


	/**
	 * Look for the WebApplicationContext associated with the DispatcherServlet
	 * that has initiated request processing.
	 * <p>
	 *  查找与启动请求处理的DispatcherServlet关联的WebApplicationContext
	 * 
	 * 
	 * @param request current HTTP request
	 * @return the request-specific web application context
	 * @throws IllegalStateException if no servlet-specific context has been found
	 * @see #getWebApplicationContext(ServletRequest, ServletContext)
	 * @deprecated as of Spring 4.2.1, in favor of
	 * {@link #findWebApplicationContext(HttpServletRequest)}
	 */
	@Deprecated
	public static WebApplicationContext getWebApplicationContext(ServletRequest request) throws IllegalStateException {
		return getWebApplicationContext(request, null);
	}

	/**
	 * Look for the WebApplicationContext associated with the DispatcherServlet
	 * that has initiated request processing, and for the global context if none
	 * was found associated with the current request. This method is useful to
	 * allow components outside the framework, such as JSP tag handlers,
	 * to access the most specific application context available.
	 * <p>
	 *  查找与启动请求处理的DispatcherServlet相关联的WebApplicationContext,如果没有找到与当前请求相关联的全局上下文,则此方法对于允许框架外的组件(如JSP标签处理程序)
	 * 访问最具体的应用程序上下文可用。
	 * 
	 * 
	 * @param request current HTTP request
	 * @param servletContext current servlet context
	 * @return the request-specific WebApplicationContext, or the global one
	 * if no request-specific context has been found
	 * @throws IllegalStateException if neither a servlet-specific nor a
	 * global context has been found
	 * @see DispatcherServlet#WEB_APPLICATION_CONTEXT_ATTRIBUTE
	 * @see WebApplicationContextUtils#getRequiredWebApplicationContext(ServletContext)
	 * @deprecated as of Spring 4.2.1, in favor of
	 * {@link #findWebApplicationContext(HttpServletRequest, ServletContext)}
	 */
	@Deprecated
	public static WebApplicationContext getWebApplicationContext(
			ServletRequest request, ServletContext servletContext) throws IllegalStateException {

		WebApplicationContext webApplicationContext = (WebApplicationContext) request.getAttribute(
				DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		if (webApplicationContext == null) {
			if (servletContext == null) {
				throw new IllegalStateException("No WebApplicationContext found: not in a DispatcherServlet request?");
			}
			webApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		}
		return webApplicationContext;
	}

	/**
	 * Look for the WebApplicationContext associated with the DispatcherServlet
	 * that has initiated request processing, and for the global context if none
	 * was found associated with the current request. The global context will
	 * be found via the ServletContext or via ContextLoader's current context.
	 * <p>NOTE: This variant remains compatible with Servlet 2.5, explicitly
	 * checking a given ServletContext instead of deriving it from the request.
	 * <p>
	 * 查找与已启动请求处理的DispatcherServlet关联的WebApplicationContext,如果没有找到与当前请求相关联的全局上下文,将通过ServletContext或通过Context
	 * Loader的当前上下文发现全局上下文<p>注意：此变体保留与Servlet 25兼容,明确地检查给定的ServletContext而不是从请求中导出它。
	 * 
	 * 
	 * @param request current HTTP request
	 * @param servletContext current servlet context
	 * @return the request-specific WebApplicationContext, or the global one
	 * if no request-specific context has been found, or {@code null} if none
	 * @since 4.2.1
	 * @see DispatcherServlet#WEB_APPLICATION_CONTEXT_ATTRIBUTE
	 * @see WebApplicationContextUtils#getWebApplicationContext(ServletContext)
	 * @see ContextLoader#getCurrentWebApplicationContext()
	 */
	public static WebApplicationContext findWebApplicationContext(
			HttpServletRequest request, ServletContext servletContext) {

		WebApplicationContext webApplicationContext = (WebApplicationContext) request.getAttribute(
				DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		if (webApplicationContext == null) {
			if (servletContext != null) {
				webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
			}
			if (webApplicationContext == null) {
				webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
			}
		}
		return webApplicationContext;
	}

	/**
	 * Look for the WebApplicationContext associated with the DispatcherServlet
	 * that has initiated request processing, and for the global context if none
	 * was found associated with the current request. The global context will
	 * be found via the ServletContext or via ContextLoader's current context.
	 * <p>NOTE: This variant requires Servlet 3.0+ and is generally recommended
	 * for forward-looking custom user code.
	 * <p>
	 * 查找与已启动请求处理的DispatcherServlet相关联的WebApplicationContext,如果没有找到与当前请求相关联的全局上下文,将通过ServletContext或通过Contex
	 * tLoader的当前上下文发现全局上下文<p>注意：此变体需要Servlet 30+通常推荐用于前瞻性的自定义用户代码。
	 * 
	 * 
	 * @param request current HTTP request
	 * @return the request-specific WebApplicationContext, or the global one
	 * if no request-specific context has been found, or {@code null} if none
	 * @since 4.2.1
	 * @see #findWebApplicationContext(HttpServletRequest, ServletContext)
	 * @see ServletRequest#getServletContext()
	 * @see ContextLoader#getCurrentWebApplicationContext()
	 */
	public static WebApplicationContext findWebApplicationContext(HttpServletRequest request) {
		return findWebApplicationContext(request, request.getServletContext());
	}

	/**
	 * Return the LocaleResolver that has been bound to the request by the
	 * DispatcherServlet.
	 * <p>
	 *  返回由DispatcherServlet绑定到该请求的LocaleResolver
	 * 
	 * 
	 * @param request current HTTP request
	 * @return the current LocaleResolver, or {@code null} if not found
	 */
	public static LocaleResolver getLocaleResolver(HttpServletRequest request) {
		return (LocaleResolver) request.getAttribute(DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE);
	}

	/**
	 * Retrieve the current locale from the given request, using the
	 * LocaleResolver bound to the request by the DispatcherServlet
	 * (if available), falling back to the request's accept-header Locale.
	 * <p>This method serves as a straightforward alternative to the standard
	 * Servlet {@link javax.servlet.http.HttpServletRequest#getLocale()} method,
	 * falling back to the latter if no more specific locale has been found.
	 * <p>Consider using {@link org.springframework.context.i18n.LocaleContextHolder#getLocale()}
	 * which will normally be populated with the same Locale.
	 * <p>
	 * 从给定的请求中检索当前语言环境,使用由DispatcherServlet绑定到该请求的LocaleResolver(如果可用),返回到请求的accept-header Locale <p>此方法作为标准
	 * Servlet的简单替代方法{@link javaxservlethttpHttpServletRequest#getLocale()}方法,如果没有找到更多的特定语言环境,则返回到后一个<p>考虑使用
	 * 通常使用相同语言环境填充的{@link orgspringframeworkcontexti18nLocaleContextHolder#getLocale()}。
	 * 
	 * 
	 * @param request current HTTP request
	 * @return the current locale for the given request, either from the
	 * LocaleResolver or from the plain request itself
	 * @see #getLocaleResolver
	 * @see org.springframework.context.i18n.LocaleContextHolder#getLocale()
	 */
	public static Locale getLocale(HttpServletRequest request) {
		LocaleResolver localeResolver = getLocaleResolver(request);
		return (localeResolver != null ? localeResolver.resolveLocale(request) : request.getLocale());
	}

	/**
	 * Retrieve the current time zone from the given request, using the
	 * TimeZoneAwareLocaleResolver bound to the request by the DispatcherServlet
	 * (if available), falling back to the system's default time zone.
	 * <p>Note: This method returns {@code null} if no specific time zone can be
	 * resolved for the given request. This is in contrast to {@link #getLocale}
	 * where there is always the request's accept-header locale to fall back to.
	 * <p>Consider using {@link org.springframework.context.i18n.LocaleContextHolder#getTimeZone()}
	 * which will normally be populated with the same TimeZone: That method only
	 * differs in terms of its fallback to the system time zone if the LocaleResolver
	 * hasn't provided a specific time zone (instead of this method's {@code null}).
	 * <p>
	 * 从给定的请求中获取当前时区,使用与DispatcherServlet的请求绑定的TimeZoneAwareLocaleResolver(如果可用),返回到系统的默认时区<p>注意：如果没有特定时间,此方
	 * 法返回{@code null}区域可以解决给定的请求这与{@link #getLocale}相反,其中总是有请求的accept-header语言环境回到<p>考虑使用{@link orgspringframeworkcontexti18nLocaleContextHolder#getTimeZone()}
	 * ,这通常是使用相同的TimeZone进行填充：如果LocaleResolver尚未提供特定的时区(而不是此方法的{@code null}),则该方法仅在其回退到系统时区方面不同)。
	 * 
	 * 
	 * @param request current HTTP request
	 * @return the current time zone for the given request, either from the
	 * TimeZoneAwareLocaleResolver or {@code null} if none associated
	 * @see #getLocaleResolver
	 * @see org.springframework.context.i18n.LocaleContextHolder#getTimeZone()
	 */
	public static TimeZone getTimeZone(HttpServletRequest request) {
		LocaleResolver localeResolver = getLocaleResolver(request);
		if (localeResolver instanceof LocaleContextResolver) {
			LocaleContext localeContext = ((LocaleContextResolver) localeResolver).resolveLocaleContext(request);
			if (localeContext instanceof TimeZoneAwareLocaleContext) {
				return ((TimeZoneAwareLocaleContext) localeContext).getTimeZone();
			}
		}
		return null;
	}

	/**
	 * Return the ThemeResolver that has been bound to the request by the
	 * DispatcherServlet.
	 * <p>
	 * 返回已由DispatcherServlet绑定到请求的ThemeResolver
	 * 
	 * 
	 * @param request current HTTP request
	 * @return the current ThemeResolver, or {@code null} if not found
	 */
	public static ThemeResolver getThemeResolver(HttpServletRequest request) {
		return (ThemeResolver) request.getAttribute(DispatcherServlet.THEME_RESOLVER_ATTRIBUTE);
	}

	/**
	 * Return the ThemeSource that has been bound to the request by the
	 * DispatcherServlet.
	 * <p>
	 *  返回已经由DispatcherServlet绑定到请求的ThemeSource
	 * 
	 * 
	 * @param request current HTTP request
	 * @return the current ThemeSource
	 */
	public static ThemeSource getThemeSource(HttpServletRequest request) {
		return (ThemeSource) request.getAttribute(DispatcherServlet.THEME_SOURCE_ATTRIBUTE);
	}

	/**
	 * Retrieves the current theme from the given request, using the ThemeResolver
	 * and ThemeSource bound to the request by the DispatcherServlet.
	 * <p>
	 *  从给定的请求中检索当前主题,使用由DispatcherServlet绑定到该请求的ThemeResolver和ThemeSource
	 * 
	 * 
	 * @param request current HTTP request
	 * @return the current theme, or {@code null} if not found
	 * @see #getThemeResolver
	 */
	public static Theme getTheme(HttpServletRequest request) {
		ThemeResolver themeResolver = getThemeResolver(request);
		ThemeSource themeSource = getThemeSource(request);
		if (themeResolver != null && themeSource != null) {
			String themeName = themeResolver.resolveThemeName(request);
			return themeSource.getTheme(themeName);
		}
		else {
			return null;
		}
	}

	/**
	 * Return a read-only {@link Map} with "input" flash attributes saved on a
	 * previous request.
	 * <p>
	 *  返回一个只读{@link Map},并保存在先前请求中的"输入"flash属性
	 * 
	 * 
	 * @param request the current request
	 * @return a read-only Map, or {@code null} if not found
	 * @see FlashMap
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, ?> getInputFlashMap(HttpServletRequest request) {
		return (Map<String, ?>) request.getAttribute(DispatcherServlet.INPUT_FLASH_MAP_ATTRIBUTE);
	}

	/**
	 * Return the "output" FlashMap with attributes to save for a subsequent request.
	 * <p>
	 *  返回带有属性的"输出"FlashMap,以保存后续请求
	 * 
	 * 
	 * @param request the current request
	 * @return a {@link FlashMap} instance (never {@code null} within a DispatcherServlet request)
	 * @see FlashMap
	 */
	public static FlashMap getOutputFlashMap(HttpServletRequest request) {
		return (FlashMap) request.getAttribute(DispatcherServlet.OUTPUT_FLASH_MAP_ATTRIBUTE);
	}

	/**
	 * Return the FlashMapManager instance to save flash attributes with
	 * before a redirect.
	 * <p>
	 *  返回FlashMapManager实例以在重定向之前保存Flash属性
	 * 
	 * @param request the current request
	 * @return a {@link FlashMapManager} instance (never {@code null} within a DispatcherServlet request)
	 */
	public static FlashMapManager getFlashMapManager(HttpServletRequest request) {
		return (FlashMapManager) request.getAttribute(DispatcherServlet.FLASH_MAP_MANAGER_ATTRIBUTE);
	}

}
