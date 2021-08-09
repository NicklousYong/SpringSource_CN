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

package org.springframework.web.servlet.support;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.jstl.core.Config;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.SimpleTimeZoneAwareLocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.ui.context.Theme;
import org.springframework.ui.context.ThemeSource;
import org.springframework.ui.context.support.ResourceBundleThemeSource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.EscapedErrors;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.LocaleContextResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ThemeResolver;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.UriTemplate;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.WebUtils;

/**
 * Context holder for request-specific state, like current web application context, current locale,
 * current theme, and potential binding errors. Provides easy access to localized messages and
 * Errors instances.
 *
 * <p>Suitable for exposition to views, and usage within JSP's "useBean" tag, JSP scriptlets, JSTL EL,
 * etc. Necessary for views that do not have access to the servlet request, like FreeMarker templates.
 *
 * <p>Can be instantiated manually, or automatically exposed to views as model attribute via AbstractView's
 * "requestContextAttribute" property.
 *
 * <p>Will also work outside of DispatcherServlet requests, accessing the root WebApplicationContext
 * and using an appropriate fallback for the locale (the HttpServletRequest's primary locale).
 *
 * <p>
 *  用于请求特定状态的上下文持有者,如当前Web应用程序上下文,当前语言环境,当前主题和潜在的绑定错误提供对本地化消息和错误实例的轻松访问
 * 
 * <p>适用于浏览视图,以及JSP的"useBean"标记,JSP scriptlets,JSTL EL等内容的使用对于无法访问servlet请求的视图,如FreeMarker模板
 * 
 *  <p>可以手动实例化,或者通过AbstractView的"requestContextAttribute"属性自动显示为模型属性的视图
 * 
 *  <p>还将在DispatcherServlet请求之外工作,访问根WebApplicationContext并对该区域设置适当的后备(HttpServletRequest的主要区域设置)
 * 
 * 
 * @author Juergen Hoeller
 * @author Rossen Stoyanchev
 * @since 03.03.2003
 * @see org.springframework.web.servlet.DispatcherServlet
 * @see org.springframework.web.servlet.view.AbstractView#setRequestContextAttribute
 * @see org.springframework.web.servlet.view.UrlBasedViewResolver#setRequestContextAttribute
 * @see #getFallbackLocale()
 */
public class RequestContext {

	/**
	 * Default theme name used if the RequestContext cannot find a ThemeResolver.
	 * Only applies to non-DispatcherServlet requests.
	 * <p>Same as AbstractThemeResolver's default, but not linked in here to avoid package interdependencies.
	 * <p>
	 *  如果RequestContext找不到ThemeResolver,则使用默认主题名称仅适用于非DispatcherServlet请求<p>与AbstractThemeResolver的默认值相同,但在
	 * 此处未链接,以避免软件包相互依赖。
	 * 
	 * 
	 * @see org.springframework.web.servlet.theme.AbstractThemeResolver#ORIGINAL_DEFAULT_THEME_NAME
	 */
	public static final String DEFAULT_THEME_NAME = "theme";

	/**
	 * Request attribute to hold the current web application context for RequestContext usage.
	 * By default, the DispatcherServlet's context (or the root context as fallback) is exposed.
	 * <p>
	 * Request属性用于保存RequestContext用法的当前Web应用程序上下文默认情况下,DispatcherServlet的上下文(或根上下文作为后备)被暴露
	 * 
	 */
	public static final String WEB_APPLICATION_CONTEXT_ATTRIBUTE = RequestContext.class.getName() + ".CONTEXT";


	protected static final boolean jstlPresent = ClassUtils.isPresent("javax.servlet.jsp.jstl.core.Config",
			RequestContext.class.getClassLoader());

	private HttpServletRequest request;

	private HttpServletResponse response;

	private Map<String, Object> model;

	private WebApplicationContext webApplicationContext;

	private Locale locale;

	private TimeZone timeZone;

	private Theme theme;

	private Boolean defaultHtmlEscape;

	private Boolean responseEncodedHtmlEscape;

	private UrlPathHelper urlPathHelper;

	private RequestDataValueProcessor requestDataValueProcessor;

	private Map<String, Errors> errorsMap;


	/**
	 * Create a new RequestContext for the given request, using the request attributes for Errors retrieval.
	 * <p>This only works with InternalResourceViews, as Errors instances are part of the model and not
	 * normally exposed as request attributes. It will typically be used within JSPs or custom tags.
	 * <p><b>Will only work within a DispatcherServlet request.</b>
	 * Pass in a ServletContext to be able to fallback to the root WebApplicationContext.
	 * <p>
	 *  为给定的请求创建一个新的RequestContext,使用Errors检索的请求属性<p>这仅适用于InternalResourceViews,因为Errors实例是模型的一部分,通常不会作为请求属性
	 * 公开。
	 * 它通常会在JSP或自定义标签中使用<p> <b>只能在DispatcherServlet请求中工作</b>传递ServletContext以便能够回退到根WebApplicationContext。
	 * 
	 * 
	 * @param request current HTTP request
	 * @see org.springframework.web.servlet.DispatcherServlet
	 * @see #RequestContext(javax.servlet.http.HttpServletRequest, javax.servlet.ServletContext)
	 */
	public RequestContext(HttpServletRequest request) {
		initContext(request, null, null, null);
	}

	/**
	 * Create a new RequestContext for the given request, using the request attributes for Errors retrieval.
	 * <p>This only works with InternalResourceViews, as Errors instances are part of the model and not
	 * normally exposed as request attributes. It will typically be used within JSPs or custom tags.
	 * <p><b>Will only work within a DispatcherServlet request.</b>
	 * Pass in a ServletContext to be able to fallback to the root WebApplicationContext.
	 * <p>
	 * 为给定的请求创建一个新的RequestContext,使用Errors检索的请求属性<p>这仅适用于InternalResourceViews,因为Errors实例是模型的一部分,通常不会作为请求属性公
	 * 开。
	 * 它通常会在JSP或自定义标签中使用<p> <b>只能在DispatcherServlet请求中工作</b>传递ServletContext以便能够回退到根WebApplicationContext。
	 * 
	 * 
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @see org.springframework.web.servlet.DispatcherServlet
	 * @see #RequestContext(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.ServletContext, Map)
	 */
	public RequestContext(HttpServletRequest request, HttpServletResponse response) {
		initContext(request, response, null, null);
	}

	/**
	 * Create a new RequestContext for the given request, using the request attributes for Errors retrieval.
	 * <p>This only works with InternalResourceViews, as Errors instances are part of the model and not
	 * normally exposed as request attributes. It will typically be used within JSPs or custom tags.
	 * <p>If a ServletContext is specified, the RequestContext will also work with the root
	 * WebApplicationContext (outside a DispatcherServlet).
	 * <p>
	 * 为给定的请求创建一个新的RequestContext,使用Errors检索的请求属性<p>这仅适用于InternalResourceViews,因为Errors实例是模型的一部分,通常不会作为请求属性公
	 * 开。
	 * 它通常会在JSP或自定义标签中使用<p>如果指定了ServletContext,那么RequestContext也可以与根WebApplicationContext(DispatcherServlet之
	 * 外)一起工作,。
	 * 
	 * 
	 * @param request current HTTP request
	 * @param servletContext the servlet context of the web application (can be {@code null};
	 * necessary for fallback to root WebApplicationContext)
	 * @see org.springframework.web.context.WebApplicationContext
	 * @see org.springframework.web.servlet.DispatcherServlet
	 */
	public RequestContext(HttpServletRequest request, ServletContext servletContext) {
		initContext(request, null, servletContext, null);
	}

	/**
	 * Create a new RequestContext for the given request, using the given model attributes for Errors retrieval.
	 * <p>This works with all View implementations. It will typically be used by View implementations.
	 * <p><b>Will only work within a DispatcherServlet request.</b>
	 * Pass in a ServletContext to be able to fallback to the root WebApplicationContext.
	 * <p>
	 *  为给定的请求创建一个新的RequestContext,使用给定的模型属性进行错误检索<p>这适用于所有View实现它通常由View实现使用<p> <b>只能在DispatcherServlet请求中工
	 * 作</b传递一个ServletContext以便能够回退到根WebApplicationContext。
	 * 
	 * 
	 * @param request current HTTP request
	 * @param model the model attributes for the current view (can be {@code null},
	 * using the request attributes for Errors retrieval)
	 * @see org.springframework.web.servlet.DispatcherServlet
	 * @see #RequestContext(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.ServletContext, Map)
	 */
	public RequestContext(HttpServletRequest request, Map<String, Object> model) {
		initContext(request, null, null, model);
	}

	/**
	 * Create a new RequestContext for the given request, using the given model attributes for Errors retrieval.
	 * <p>This works with all View implementations. It will typically be used by View implementations.
	 * <p>If a ServletContext is specified, the RequestContext will also work with a root
	 * WebApplicationContext (outside a DispatcherServlet).
	 * <p>
	 * 为给定的请求创建一个新的RequestContext,使用给定的模型属性进行错误检索<p>这适用于所有View实现它通常由View实现使用<p>如果指定了ServletContext,则RequestC
	 * ontext也将与根WebApplicationContext(DispatcherServlet外)。
	 * 
	 * 
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param servletContext the servlet context of the web application (can be {@code null}; necessary for
	 * fallback to root WebApplicationContext)
	 * @param model the model attributes for the current view (can be {@code null}, using the request attributes
	 * for Errors retrieval)
	 * @see org.springframework.web.context.WebApplicationContext
	 * @see org.springframework.web.servlet.DispatcherServlet
	 */
	public RequestContext(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext,
			Map<String, Object> model) {

		initContext(request, response, servletContext, model);
	}

	/**
	 * Default constructor for subclasses.
	 * <p>
	 *  子类的默认构造函数
	 * 
	 */
	protected RequestContext() {
	}


	/**
	 * Initialize this context with the given request, using the given model attributes for Errors retrieval.
	 * <p>Delegates to {@code getFallbackLocale} and {@code getFallbackTheme} for determining the fallback
	 * locale and theme, respectively, if no LocaleResolver and/or ThemeResolver can be found in the request.
	 * <p>
	 *  使用给定的请求初始化此上下文,如果没有LocaleResolver和/或ThemeResolver可以分别使用给定的模型属性进行错误检索(p)代理{@code getFallbackLocale}和{@code getFallbackTheme}
	 * 来确定回退区域设置和主题在请求中找到。
	 * 
	 * 
	 * @param request current HTTP request
	 * @param servletContext the servlet context of the web application (can be {@code null}; necessary for
	 * fallback to root WebApplicationContext)
	 * @param model the model attributes for the current view (can be {@code null}, using the request attributes
	 * for Errors retrieval)
	 * @see #getFallbackLocale
	 * @see #getFallbackTheme
	 * @see org.springframework.web.servlet.DispatcherServlet#LOCALE_RESOLVER_ATTRIBUTE
	 * @see org.springframework.web.servlet.DispatcherServlet#THEME_RESOLVER_ATTRIBUTE
	 */
	protected void initContext(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext,
			Map<String, Object> model) {

		this.request = request;
		this.response = response;
		this.model = model;

		// Fetch WebApplicationContext, either from DispatcherServlet or the root context.
		// ServletContext needs to be specified to be able to fall back to the root context!
		this.webApplicationContext = (WebApplicationContext) request.getAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		if (this.webApplicationContext == null) {
			this.webApplicationContext = RequestContextUtils.findWebApplicationContext(request, servletContext);
			if (this.webApplicationContext == null) {
				throw new IllegalStateException("No WebApplicationContext found: not in a DispatcherServlet " +
						"request and no ContextLoaderListener registered?");
			}
		}

		// Determine locale to use for this RequestContext.
		LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
		if (localeResolver instanceof LocaleContextResolver) {
			LocaleContext localeContext = ((LocaleContextResolver) localeResolver).resolveLocaleContext(request);
			this.locale = localeContext.getLocale();
			if (localeContext instanceof TimeZoneAwareLocaleContext) {
				this.timeZone = ((TimeZoneAwareLocaleContext) localeContext).getTimeZone();
			}
		}
		else if (localeResolver != null) {
			// Try LocaleResolver (we're within a DispatcherServlet request).
			this.locale = localeResolver.resolveLocale(request);
		}

		// Try JSTL fallbacks if necessary.
		if (this.locale == null) {
			this.locale = getFallbackLocale();
		}
		if (this.timeZone == null) {
			this.timeZone = getFallbackTimeZone();
		}

		// Determine default HTML escape setting from the "defaultHtmlEscape"
		// context-param in web.xml, if any.
		this.defaultHtmlEscape = WebUtils.getDefaultHtmlEscape(this.webApplicationContext.getServletContext());

		// Determine response-encoded HTML escape setting from the "responseEncodedHtmlEscape"
		// context-param in web.xml, if any.
		this.responseEncodedHtmlEscape = WebUtils.getResponseEncodedHtmlEscape(this.webApplicationContext.getServletContext());

		this.urlPathHelper = new UrlPathHelper();

		if (this.webApplicationContext.containsBean(RequestContextUtils.REQUEST_DATA_VALUE_PROCESSOR_BEAN_NAME)) {
			this.requestDataValueProcessor = this.webApplicationContext.getBean(
					RequestContextUtils.REQUEST_DATA_VALUE_PROCESSOR_BEAN_NAME, RequestDataValueProcessor.class);
		}
	}

	/**
	 * Determine the fallback locale for this context.
	 * <p>The default implementation checks for a JSTL locale attribute in request, session
	 * or application scope; if not found, returns the {@code HttpServletRequest.getLocale()}.
	 * <p>
	 * 确定此上下文的回退区域设置<p>默认实现在请求,会话或应用程序范围内检查JSTL区域设置属性;如果没有找到,返回{@code HttpServletRequestgetLocale()}
	 * 
	 * 
	 * @return the fallback locale (never {@code null})
	 * @see javax.servlet.http.HttpServletRequest#getLocale()
	 */
	protected Locale getFallbackLocale() {
		if (jstlPresent) {
			Locale locale = JstlLocaleResolver.getJstlLocale(getRequest(), getServletContext());
			if (locale != null) {
				return locale;
			}
		}
		return getRequest().getLocale();
	}

	/**
	 * Determine the fallback time zone for this context.
	 * <p>The default implementation checks for a JSTL time zone attribute in request,
	 * session or application scope; returns {@code null} if not found.
	 * <p>
	 *  确定此上下文的回退时区<p>默认实现在请求,会话或应用程序范围内检查JSTL时区属性;如果找不到,返回{@code null}
	 * 
	 * 
	 * @return the fallback time zone (or {@code null} if none derivable from the request)
	 */
	protected TimeZone getFallbackTimeZone() {
		if (jstlPresent) {
			TimeZone timeZone = JstlLocaleResolver.getJstlTimeZone(getRequest(), getServletContext());
			if (timeZone != null) {
				return timeZone;
			}
		}
		return null;
	}

	/**
	 * Determine the fallback theme for this context.
	 * <p>The default implementation returns the default theme (with name "theme").
	 * <p>
	 *  确定此上下文的回退主题<p>默认实现返回默认主题(名称为"主题")
	 * 
	 * 
	 * @return the fallback theme (never {@code null})
	 */
	protected Theme getFallbackTheme() {
		ThemeSource themeSource = RequestContextUtils.getThemeSource(getRequest());
		if (themeSource == null) {
			themeSource = new ResourceBundleThemeSource();
		}
		Theme theme = themeSource.getTheme(DEFAULT_THEME_NAME);
		if (theme == null) {
			throw new IllegalStateException("No theme defined and no fallback theme found");
		}
		return theme;
	}


	/**
	 * Return the underlying HttpServletRequest. Only intended for cooperating classes in this package.
	 * <p>
	 *  返回底层的HttpServletRequest仅用于此包中的协作类
	 * 
	 */
	protected final HttpServletRequest getRequest() {
		return this.request;
	}

	/**
	 * Return the underlying ServletContext. Only intended for cooperating classes in this package.
	 * <p>
	 *  返回底层的ServletContext仅适用于此包中的协作类
	 * 
	 */
	protected final ServletContext getServletContext() {
		return this.webApplicationContext.getServletContext();
	}

	/**
	 * Return the current WebApplicationContext.
	 * <p>
	 *  返回当前的WebApplicationContext
	 * 
	 */
	public final WebApplicationContext getWebApplicationContext() {
		return this.webApplicationContext;
	}

	/**
	 * Return the current WebApplicationContext as MessageSource.
	 * <p>
	 * 将当前的WebApplicationContext返回为MessageSource
	 * 
	 */
	public final MessageSource getMessageSource() {
		return this.webApplicationContext;
	}

	/**
	 * Return the model Map that this RequestContext encapsulates, if any.
	 * <p>
	 *  返回此RequestContext封装的模型映射(如果有)
	 * 
	 * 
	 * @return the populated model Map, or {@code null} if none available
	 */
	public final Map<String, Object> getModel() {
		return this.model;
	}

	/**
	 * Return the current Locale (falling back to the request locale; never {@code null}).
	 * <p>Typically coming from a DispatcherServlet's {@link LocaleResolver}.
	 * Also includes a fallback check for JSTL's Locale attribute.
	 * <p>
	 *  通常来自DispatcherServlet的{@link LocaleResolver},还包含JSTL的Locale属性的回退检查(返回到请求区域设置;从不{@code null})<p>
	 * 
	 * 
	 * @see RequestContextUtils#getLocale
	 */
	public final Locale getLocale() {
		return this.locale;
	}

	/**
	 * Return the current TimeZone (or {@code null} if none derivable from the request).
	 * <p>Typically coming from a DispatcherServlet's {@link LocaleContextResolver}.
	 * Also includes a fallback check for JSTL's TimeZone attribute.
	 * <p>
	 *  返回当前的TimeZone(或{@code null},如果无法从请求中导出)<p>通常来自DispatcherServlet的{@link LocaleContextResolver}。
	 * 还包括JSTL的TimeZone属性的后备检查。
	 * 
	 * 
	 * @see RequestContextUtils#getTimeZone
	 */
	public TimeZone getTimeZone() {
		return this.timeZone;
	}

	/**
	 * Change the current locale to the specified one,
	 * storing the new locale through the configured {@link LocaleResolver}.
	 * <p>
	 *  将当前区域设置更改为指定的区域设置,通过配置的{@link LocaleResolver}存储新的区域设置
	 * 
	 * 
	 * @param locale the new locale
	 * @see LocaleResolver#setLocale
	 * @see #changeLocale(java.util.Locale, java.util.TimeZone)
	 */
	public void changeLocale(Locale locale) {
		LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(this.request);
		if (localeResolver == null) {
			throw new IllegalStateException("Cannot change locale if no LocaleResolver configured");
		}
		localeResolver.setLocale(this.request, this.response, locale);
		this.locale = locale;
	}

	/**
	 * Change the current locale to the specified locale and time zone context,
	 * storing the new locale context through the configured {@link LocaleResolver}.
	 * <p>
	 * 将当前区域设置更改为指定的区域设置和时区上下文,通过配置的{@link LocaleResolver}存储新的区域设置上下文
	 * 
	 * 
	 * @param locale the new locale
	 * @param timeZone the new time zone
	 * @see LocaleContextResolver#setLocaleContext
	 * @see org.springframework.context.i18n.SimpleTimeZoneAwareLocaleContext
	 */
	public void changeLocale(Locale locale, TimeZone timeZone) {
		LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(this.request);
		if (!(localeResolver instanceof LocaleContextResolver)) {
			throw new IllegalStateException("Cannot change locale context if no LocaleContextResolver configured");
		}
		((LocaleContextResolver) localeResolver).setLocaleContext(this.request, this.response,
				new SimpleTimeZoneAwareLocaleContext(locale, timeZone));
		this.locale = locale;
		this.timeZone = timeZone;
	}

	/**
	 * Return the current theme (never {@code null}).
	 * <p>Resolved lazily for more efficiency when theme support is not being used.
	 * <p>
	 *  返回当前主题(从不{@code null})<p>当不使用主题支持时,懒惰地解决了更高的效率
	 * 
	 */
	public Theme getTheme() {
		if (this.theme == null) {
			// Lazily determine theme to use for this RequestContext.
			this.theme = RequestContextUtils.getTheme(this.request);
			if (this.theme == null) {
				// No ThemeResolver and ThemeSource available -> try fallback.
				this.theme = getFallbackTheme();
			}
		}
		return this.theme;
	}

	/**
	 * Change the current theme to the specified one,
	 * storing the new theme name through the configured {@link ThemeResolver}.
	 * <p>
	 *  将当前主题更改为指定的主题,通过配置的{@link ThemeResolver}存储新的主题名称,
	 * 
	 * 
	 * @param theme the new theme
	 * @see ThemeResolver#setThemeName
	 */
	public void changeTheme(Theme theme) {
		ThemeResolver themeResolver = RequestContextUtils.getThemeResolver(this.request);
		if (themeResolver == null) {
			throw new IllegalStateException("Cannot change theme if no ThemeResolver configured");
		}
		themeResolver.setThemeName(this.request, this.response, (theme != null ? theme.getName() : null));
		this.theme = theme;
	}

	/**
	 * Change the current theme to the specified theme by name,
	 * storing the new theme name through the configured {@link ThemeResolver}.
	 * <p>
	 *  通过名称将当前主题更改为指定的主题,通过配置的{@link ThemeResolver}存储新的主题名称,
	 * 
	 * 
	 * @param themeName the name of the new theme
	 * @see ThemeResolver#setThemeName
	 */
	public void changeTheme(String themeName) {
		ThemeResolver themeResolver = RequestContextUtils.getThemeResolver(this.request);
		if (themeResolver == null) {
			throw new IllegalStateException("Cannot change theme if no ThemeResolver configured");
		}
		themeResolver.setThemeName(this.request, this.response, themeName);
		// Ask for re-resolution on next getTheme call.
		this.theme = null;
	}

	/**
	 * (De)activate default HTML escaping for messages and errors, for the scope of this RequestContext.
	 * <p>The default is the application-wide setting (the "defaultHtmlEscape" context-param in web.xml).
	 * <p>
	 *  (De)激活默认的HTML转义消息和错误,这个RequestContext的范围<p>默认是应用程序范围的设置(webxml中的"defaultHtmlEscape"上下文参数)
	 * 
	 * 
	 * @see org.springframework.web.util.WebUtils#getDefaultHtmlEscape
	 */
	public void setDefaultHtmlEscape(boolean defaultHtmlEscape) {
		this.defaultHtmlEscape = defaultHtmlEscape;
	}

	/**
	 * Is default HTML escaping active? Falls back to {@code false} in case of no explicit default given.
	 * <p>
	 * 默认HTML转义是否处于活动状态?如果没有给出明确的默认值,则返回{@code false}
	 * 
	 */
	public boolean isDefaultHtmlEscape() {
		return (this.defaultHtmlEscape != null && this.defaultHtmlEscape.booleanValue());
	}

	/**
	 * Return the default HTML escape setting, differentiating between no default specified and an explicit value.
	 * <p>
	 *  返回默认的HTML转义设置,区分未指定的默认值和显式值
	 * 
	 * 
	 * @return whether default HTML escaping is enabled (null = no explicit default)
	 */
	public Boolean getDefaultHtmlEscape() {
		return this.defaultHtmlEscape;
	}

	/**
	 * Is HTML escaping using the response encoding by default?
	 * If enabled, only XML markup significant characters will be escaped with UTF-* encodings.
	 * <p>Falls back to {@code true} in case of no explicit default given, as of Spring 4.2.
	 * <p>
	 *  默认情况下是否使用响应编码进行HTML转义?如果启用,只有XML标记有意义的字符才能使用UTF- *编码进行转义<p>如果没有明确的默认值,则返回{@code true},从Spring 42开始
	 * 
	 * 
	 * @since 4.1.2
	 */
	public boolean isResponseEncodedHtmlEscape() {
		return (this.responseEncodedHtmlEscape == null || this.responseEncodedHtmlEscape.booleanValue());
	}

	/**
	 * Return the default setting about use of response encoding for HTML escape setting,
	 * differentiating between no default specified and an explicit value.
	 * <p>
	 *  返回关于使用HTML转义设置的响应编码的默认设置,区分没有默认值和显式值
	 * 
	 * 
	 * @return whether default use of response encoding HTML escaping is enabled (null = no explicit default)
	 * @since 4.1.2
	 */
	public Boolean getResponseEncodedHtmlEscape() {
		return this.responseEncodedHtmlEscape;
	}


	/**
	 * Set the UrlPathHelper to use for context path and request URI decoding.
	 * Can be used to pass a shared UrlPathHelper instance in.
	 * <p>A default UrlPathHelper is always available.
	 * <p>
	 *  设置UrlPathHelper用于上下文路径并请求URI解码可用于在<p>中传递共享的UrlPathHelper实例默认UrlPathHelper始终可用
	 * 
	 */
	public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
		Assert.notNull(urlPathHelper, "UrlPathHelper must not be null");
		this.urlPathHelper = urlPathHelper;
	}

	/**
	 * Return the UrlPathHelper used for context path and request URI decoding.
	 * Can be used to configure the current UrlPathHelper.
	 * <p>A default UrlPathHelper is always available.
	 * <p>
	 * 返回用于上下文路径和请求URI解码的UrlPathHelper可用于配置当前UrlPathHelper <p>默认的UrlPathHelper始终可用
	 * 
	 */
	public UrlPathHelper getUrlPathHelper() {
		return this.urlPathHelper;
	}

	/**
	 * Return the RequestDataValueProcessor instance to use obtained from the
	 * WebApplicationContext under the name {@code "requestDataValueProcessor"}.
	 * Or {@code null} if no matching bean was found.
	 * <p>
	 *  返回RequestDataValueProcessor实例,使用从WebApplicationContext获取的名称为{@code"requestDataValueProcessor"}或{@code null}
	 * ,如果没有找到匹配的bean。
	 * 
	 */
	public RequestDataValueProcessor getRequestDataValueProcessor() {
		return this.requestDataValueProcessor;
	}

	/**
	 * Return the context path of the original request, that is, the path that
	 * indicates the current web application. This is useful for building links
	 * to other resources within the application.
	 * <p>Delegates to the UrlPathHelper for decoding.
	 * <p>
	 *  返回原始请求的上下文路径,即指示当前Web应用程序的路径这对于构建到应用程序内其他资源的链接很有用<p>代理UrlPathHelper进行解码
	 * 
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getContextPath
	 * @see #getUrlPathHelper
	 */
	public String getContextPath() {
		return this.urlPathHelper.getOriginatingContextPath(this.request);
	}

	/**
	 * Return a context-aware URl for the given relative URL.
	 * <p>
	 *  为给定的相对URL返回上下文感知UR1
	 * 
	 * 
	 * @param relativeUrl the relative URL part
	 * @return a URL that points back to the server with an absolute path (also URL-encoded accordingly)
	 */
	public String getContextUrl(String relativeUrl) {
		String url = getContextPath() + relativeUrl;
		if (this.response != null) {
			url = this.response.encodeURL(url);
		}
		return url;
	}

	/**
	 * Return a context-aware URl for the given relative URL with placeholders (named keys with braces {@code {}}).
	 * For example, send in a relative URL {@code foo/{bar}?spam={spam}} and a parameter map
	 * {@code {bar=baz,spam=nuts}} and the result will be {@code [contextpath]/foo/baz?spam=nuts}.
	 * <p>
	 * 使用占位符(带有大括号{@code {}}的命名键)返回给定相对URL的上下文感知URl)例如,发送相对URL {@code foo / {bar}?spam = {spam}}和参数映射{@code {bar = baz,spam = nuts}
	 * },结果将是{@code [contextpath] / foo / baz?spam = nuts}。
	 * 
	 * 
	 * @param relativeUrl the relative URL part
	 * @param params a map of parameters to insert as placeholders in the url
	 * @return a URL that points back to the server with an absolute path (also URL-encoded accordingly)
	 */
	public String getContextUrl(String relativeUrl, Map<String, ?> params) {
		String url = getContextPath() + relativeUrl;
		UriTemplate template = new UriTemplate(url);
		url = template.expand(params).toASCIIString();
		if (this.response != null) {
			url = this.response.encodeURL(url);
		}
		return url;
	}

	/**
	 * Return the path to URL mappings within the current servlet including the
	 * context path and the servlet path of the original request. This is useful
	 * for building links to other resources within the application where a
	 * servlet mapping of the style {@code "/main/*"} is used.
	 * <p>Delegates to the UrlPathHelper to determine the context and servlet path.
	 * <p>
	 *  返回当前servlet中的URL映射路径,包括原始请求的上下文路径和servlet路径这对于构建应用程序中其他资源的链接很有用,其中样式{@code"/ main / *"的servlet映射}使用<p>
	 * 代理UrlPathHelper来确定上下文和servlet路径。
	 * 
	 */
	public String getPathToServlet() {
		String path = this.urlPathHelper.getOriginatingContextPath(this.request);
		if (StringUtils.hasText(this.urlPathHelper.getPathWithinServletMapping(this.request))) {
			path += this.urlPathHelper.getOriginatingServletPath(this.request);
		}
		return path;
	}

	/**
	 * Return the request URI of the original request, that is, the invoked URL
	 * without parameters. This is particularly useful as HTML form action target,
	 * possibly in combination with the original query string.
	 * <p>Delegates to the UrlPathHelper for decoding.
	 * <p>
	 * 返回原始请求的请求URI,即没有参数的被调用的URL这是特别有用的HTML表单操作目标,可能与原始查询字符串组合<p>代理到UrlPathHelper进行解码
	 * 
	 * 
	 * @see #getQueryString
	 * @see org.springframework.web.util.UrlPathHelper#getOriginatingRequestUri
	 * @see #getUrlPathHelper
	 */
	public String getRequestUri() {
		return this.urlPathHelper.getOriginatingRequestUri(this.request);
	}

	/**
	 * Return the query string of the current request, that is, the part after
	 * the request path. This is particularly useful for building an HTML form
	 * action target in combination with the original request URI.
	 * <p>Delegates to the UrlPathHelper for decoding.
	 * <p>
	 *  返回当前请求的查询字符串,即请求路径之后的部分这对于构建HTML表单操作目标与原始请求URI特别有用<p>代理到UrlPathHelper进行解码
	 * 
	 * 
	 * @see #getRequestUri
	 * @see org.springframework.web.util.UrlPathHelper#getOriginatingQueryString
	 * @see #getUrlPathHelper
	 */
	public String getQueryString() {
		return this.urlPathHelper.getOriginatingQueryString(this.request);
	}

	/**
	 * Retrieve the message for the given code, using the "defaultHtmlEscape" setting.
	 * <p>
	 *  使用"defaultHtmlEscape"设置检索给定代码的消息
	 * 
	 * 
	 * @param code code of the message
	 * @param defaultMessage String to return if the lookup fails
	 * @return the message
	 */
	public String getMessage(String code, String defaultMessage) {
		return getMessage(code, null, defaultMessage, isDefaultHtmlEscape());
	}

	/**
	 * Retrieve the message for the given code, using the "defaultHtmlEscape" setting.
	 * <p>
	 *  使用"defaultHtmlEscape"设置检索给定代码的消息
	 * 
	 * 
	 * @param code code of the message
	 * @param args arguments for the message, or {@code null} if none
	 * @param defaultMessage String to return if the lookup fails
	 * @return the message
	 */
	public String getMessage(String code, Object[] args, String defaultMessage) {
		return getMessage(code, args, defaultMessage, isDefaultHtmlEscape());
	}

	/**
	 * Retrieve the message for the given code, using the "defaultHtmlEscape" setting.
	 * <p>
	 *  使用"defaultHtmlEscape"设置检索给定代码的消息
	 * 
	 * 
	 * @param code code of the message
	 * @param args arguments for the message as a List, or {@code null} if none
	 * @param defaultMessage String to return if the lookup fails
	 * @return the message
	 */
	public String getMessage(String code, List<?> args, String defaultMessage) {
		return getMessage(code, (args != null ? args.toArray() : null), defaultMessage, isDefaultHtmlEscape());
	}

	/**
	 * Retrieve the message for the given code.
	 * <p>
	 *  检索给定代码的消息
	 * 
	 * 
	 * @param code code of the message
	 * @param args arguments for the message, or {@code null} if none
	 * @param defaultMessage String to return if the lookup fails
	 * @param htmlEscape HTML escape the message?
	 * @return the message
	 */
	public String getMessage(String code, Object[] args, String defaultMessage, boolean htmlEscape) {
		String msg = this.webApplicationContext.getMessage(code, args, defaultMessage, this.locale);
		return (htmlEscape ? HtmlUtils.htmlEscape(msg) : msg);
	}

	/**
	 * Retrieve the message for the given code, using the "defaultHtmlEscape" setting.
	 * <p>
	 * 使用"defaultHtmlEscape"设置检索给定代码的消息
	 * 
	 * 
	 * @param code code of the message
	 * @return the message
	 * @throws org.springframework.context.NoSuchMessageException if not found
	 */
	public String getMessage(String code) throws NoSuchMessageException {
		return getMessage(code, null, isDefaultHtmlEscape());
	}

	/**
	 * Retrieve the message for the given code, using the "defaultHtmlEscape" setting.
	 * <p>
	 *  使用"defaultHtmlEscape"设置检索给定代码的消息
	 * 
	 * 
	 * @param code code of the message
	 * @param args arguments for the message, or {@code null} if none
	 * @return the message
	 * @throws org.springframework.context.NoSuchMessageException if not found
	 */
	public String getMessage(String code, Object[] args) throws NoSuchMessageException {
		return getMessage(code, args, isDefaultHtmlEscape());
	}

	/**
	 * Retrieve the message for the given code, using the "defaultHtmlEscape" setting.
	 * <p>
	 *  使用"defaultHtmlEscape"设置检索给定代码的消息
	 * 
	 * 
	 * @param code code of the message
	 * @param args arguments for the message as a List, or {@code null} if none
	 * @return the message
	 * @throws org.springframework.context.NoSuchMessageException if not found
	 */
	public String getMessage(String code, List<?> args) throws NoSuchMessageException {
		return getMessage(code, (args != null ? args.toArray() : null), isDefaultHtmlEscape());
	}

	/**
	 * Retrieve the message for the given code.
	 * <p>
	 *  检索给定代码的消息
	 * 
	 * 
	 * @param code code of the message
	 * @param args arguments for the message, or {@code null} if none
	 * @param htmlEscape HTML escape the message?
	 * @return the message
	 * @throws org.springframework.context.NoSuchMessageException if not found
	 */
	public String getMessage(String code, Object[] args, boolean htmlEscape) throws NoSuchMessageException {
		String msg = this.webApplicationContext.getMessage(code, args, this.locale);
		return (htmlEscape ? HtmlUtils.htmlEscape(msg) : msg);
	}

	/**
	 * Retrieve the given MessageSourceResolvable (e.g. an ObjectError instance), using the "defaultHtmlEscape" setting.
	 * <p>
	 *  检索给定MessageSourceResolvable(例如ObjectError实例),使用"defaultHtmlEscape"设定
	 * 
	 * 
	 * @param resolvable the MessageSourceResolvable
	 * @return the message
	 * @throws org.springframework.context.NoSuchMessageException if not found
	 */
	public String getMessage(MessageSourceResolvable resolvable) throws NoSuchMessageException {
		return getMessage(resolvable, isDefaultHtmlEscape());
	}

	/**
	 * Retrieve the given MessageSourceResolvable (e.g. an ObjectError instance).
	 * <p>
	 *  获取给定MessageSourceResolvable(例如ObjectError实例)
	 * 
	 * 
	 * @param resolvable the MessageSourceResolvable
	 * @param htmlEscape HTML escape the message?
	 * @return the message
	 * @throws org.springframework.context.NoSuchMessageException if not found
	 */
	public String getMessage(MessageSourceResolvable resolvable, boolean htmlEscape) throws NoSuchMessageException {
		String msg = this.webApplicationContext.getMessage(resolvable, this.locale);
		return (htmlEscape ? HtmlUtils.htmlEscape(msg) : msg);
	}

	/**
	 * Retrieve the theme message for the given code.
	 * <p>Note that theme messages are never HTML-escaped, as they typically denote
	 * theme-specific resource paths and not client-visible messages.
	 * <p>
	 *  检索给定的代码的主题消息<p>请注意主题的消息从不HTML转义,因为它们通常表示特定主题资源路径,而不是客户端可见的消息
	 * 
	 * 
	 * @param code code of the message
	 * @param defaultMessage String to return if the lookup fails
	 * @return the message
	 */
	public String getThemeMessage(String code, String defaultMessage) {
		return getTheme().getMessageSource().getMessage(code, null, defaultMessage, this.locale);
	}

	/**
	 * Retrieve the theme message for the given code.
	 * <p>Note that theme messages are never HTML-escaped, as they typically denote
	 * theme-specific resource paths and not client-visible messages.
	 * <p>
	 * 检索给定的代码的主题消息<p>请注意主题的消息从不HTML转义,因为它们通常表示特定主题资源路径,而不是客户端可见的消息
	 * 
	 * 
	 * @param code code of the message
	 * @param args arguments for the message, or {@code null} if none
	 * @param defaultMessage String to return if the lookup fails
	 * @return the message
	 */
	public String getThemeMessage(String code, Object[] args, String defaultMessage) {
		return getTheme().getMessageSource().getMessage(code, args, defaultMessage, this.locale);
	}

	/**
	 * Retrieve the theme message for the given code.
	 * <p>Note that theme messages are never HTML-escaped, as they typically denote
	 * theme-specific resource paths and not client-visible messages.
	 * <p>
	 *  检索给定的代码的主题消息<p>请注意主题的消息从不HTML转义,因为它们通常表示特定主题资源路径,而不是客户端可见的消息
	 * 
	 * 
	 * @param code code of the message
	 * @param args arguments for the message as a List, or {@code null} if none
	 * @param defaultMessage String to return if the lookup fails
	 * @return the message
	 */
	public String getThemeMessage(String code, List<?> args, String defaultMessage) {
		return getTheme().getMessageSource().getMessage(code, (args != null ? args.toArray() : null), defaultMessage,
				this.locale);
	}

	/**
	 * Retrieve the theme message for the given code.
	 * <p>Note that theme messages are never HTML-escaped, as they typically denote
	 * theme-specific resource paths and not client-visible messages.
	 * <p>
	 *  检索给定的代码的主题消息<p>请注意主题的消息从不HTML转义,因为它们通常表示特定主题资源路径,而不是客户端可见的消息
	 * 
	 * 
	 * @param code code of the message
	 * @return the message
	 * @throws org.springframework.context.NoSuchMessageException if not found
	 */
	public String getThemeMessage(String code) throws NoSuchMessageException {
		return getTheme().getMessageSource().getMessage(code, null, this.locale);
	}

	/**
	 * Retrieve the theme message for the given code.
	 * <p>Note that theme messages are never HTML-escaped, as they typically denote
	 * theme-specific resource paths and not client-visible messages.
	 * <p>
	 *  检索给定的代码的主题消息<p>请注意主题的消息从不HTML转义,因为它们通常表示特定主题资源路径,而不是客户端可见的消息
	 * 
	 * 
	 * @param code code of the message
	 * @param args arguments for the message, or {@code null} if none
	 * @return the message
	 * @throws org.springframework.context.NoSuchMessageException if not found
	 */
	public String getThemeMessage(String code, Object[] args) throws NoSuchMessageException {
		return getTheme().getMessageSource().getMessage(code, args, this.locale);
	}

	/**
	 * Retrieve the theme message for the given code.
	 * <p>Note that theme messages are never HTML-escaped, as they typically denote
	 * theme-specific resource paths and not client-visible messages.
	 * <p>
	 * 检索给定的代码的主题消息<p>请注意主题的消息从不HTML转义,因为它们通常表示特定主题资源路径,而不是客户端可见的消息
	 * 
	 * 
	 * @param code code of the message
	 * @param args arguments for the message as a List, or {@code null} if none
	 * @return the message
	 * @throws org.springframework.context.NoSuchMessageException if not found
	 */
	public String getThemeMessage(String code, List<?> args) throws NoSuchMessageException {
		return getTheme().getMessageSource().getMessage(code, (args != null ? args.toArray() : null), this.locale);
	}

	/**
	 * Retrieve the given MessageSourceResolvable in the current theme.
	 * <p>Note that theme messages are never HTML-escaped, as they typically denote
	 * theme-specific resource paths and not client-visible messages.
	 * <p>
	 *  检索当前主题中给定的MessageSourceResolvable <p>请注意,主题消息不会HTML转义,因为它们通常表示特定于主题的资源路径,而不是客户端可见消息
	 * 
	 * 
	 * @param resolvable the MessageSourceResolvable
	 * @return the message
	 * @throws org.springframework.context.NoSuchMessageException if not found
	 */
	public String getThemeMessage(MessageSourceResolvable resolvable) throws NoSuchMessageException {
		return getTheme().getMessageSource().getMessage(resolvable, this.locale);
	}

	/**
	 * Retrieve the Errors instance for the given bind object, using the "defaultHtmlEscape" setting.
	 * <p>
	 *  使用"defaultHtmlEscape"设置检索给定绑定对象的Errors实例
	 * 
	 * 
	 * @param name name of the bind object
	 * @return the Errors instance, or {@code null} if not found
	 */
	public Errors getErrors(String name) {
		return getErrors(name, isDefaultHtmlEscape());
	}

	/**
	 * Retrieve the Errors instance for the given bind object.
	 * <p>
	 *  检索给定绑定对象的Errors实例
	 * 
	 * 
	 * @param name name of the bind object
	 * @param htmlEscape create an Errors instance with automatic HTML escaping?
	 * @return the Errors instance, or {@code null} if not found
	 */
	public Errors getErrors(String name, boolean htmlEscape) {
		if (this.errorsMap == null) {
			this.errorsMap = new HashMap<String, Errors>();
		}
		Errors errors = this.errorsMap.get(name);
		boolean put = false;
		if (errors == null) {
			errors = (Errors) getModelObject(BindingResult.MODEL_KEY_PREFIX + name);
			// Check old BindException prefix for backwards compatibility.
			if (errors instanceof BindException) {
				errors = ((BindException) errors).getBindingResult();
			}
			if (errors == null) {
				return null;
			}
			put = true;
		}
		if (htmlEscape && !(errors instanceof EscapedErrors)) {
			errors = new EscapedErrors(errors);
			put = true;
		}
		else if (!htmlEscape && errors instanceof EscapedErrors) {
			errors = ((EscapedErrors) errors).getSource();
			put = true;
		}
		if (put) {
			this.errorsMap.put(name, errors);
		}
		return errors;
	}

	/**
	 * Retrieve the model object for the given model name, either from the model or from the request attributes.
	 * <p>
	 *  从模型或请求属性中检索给定模型名称的模型对象
	 * 
	 * 
	 * @param modelName the name of the model object
	 * @return the model object
	 */
	protected Object getModelObject(String modelName) {
		if (this.model != null) {
			return this.model.get(modelName);
		}
		else {
			return this.request.getAttribute(modelName);
		}
	}

	/**
	 * Create a BindStatus for the given bind object, using the "defaultHtmlEscape" setting.
	 * <p>
	 *  使用"defaultHtmlEscape"设置为给定的绑定对象创建一个BindStatus
	 * 
	 * 
	 * @param path the bean and property path for which values and errors will be resolved (e.g. "person.age")
	 * @return the new BindStatus instance
	 * @throws IllegalStateException if no corresponding Errors object found
	 */
	public BindStatus getBindStatus(String path) throws IllegalStateException {
		return new BindStatus(this, path, isDefaultHtmlEscape());
	}

	/**
	 * Create a BindStatus for the given bind object, using the "defaultHtmlEscape" setting.
	 * <p>
	 * 使用"defaultHtmlEscape"设置为给定的绑定对象创建一个BindStatus
	 * 
	 * 
	 * @param path the bean and property path for which values and errors will be resolved (e.g. "person.age")
	 * @param htmlEscape create a BindStatus with automatic HTML escaping?
	 * @return the new BindStatus instance
	 * @throws IllegalStateException if no corresponding Errors object found
	 */
	public BindStatus getBindStatus(String path, boolean htmlEscape) throws IllegalStateException {
		return new BindStatus(this, path, htmlEscape);
	}


	/**
	 * Inner class that isolates the JSTL dependency.
	 * Just called to resolve the fallback locale if the JSTL API is present.
	 * <p>
	 *  内部类隔离JSTL依赖关系如果JSTL API存在,则调用它来解析后备区域设置
	 */
	private static class JstlLocaleResolver {

		public static Locale getJstlLocale(HttpServletRequest request, ServletContext servletContext) {
			Object localeObject = Config.get(request, Config.FMT_LOCALE);
			if (localeObject == null) {
				HttpSession session = request.getSession(false);
				if (session != null) {
					localeObject = Config.get(session, Config.FMT_LOCALE);
				}
				if (localeObject == null && servletContext != null) {
					localeObject = Config.get(servletContext, Config.FMT_LOCALE);
				}
			}
			return (localeObject instanceof Locale ? (Locale) localeObject : null);
		}

		public static TimeZone getJstlTimeZone(HttpServletRequest request, ServletContext servletContext) {
			Object timeZoneObject = Config.get(request, Config.FMT_TIME_ZONE);
			if (timeZoneObject == null) {
				HttpSession session = request.getSession(false);
				if (session != null) {
					timeZoneObject = Config.get(session, Config.FMT_TIME_ZONE);
				}
				if (timeZoneObject == null && servletContext != null) {
					timeZoneObject = Config.get(servletContext, Config.FMT_TIME_ZONE);
				}
			}
			return (timeZoneObject instanceof TimeZone ? (TimeZone) timeZoneObject : null);
		}
	}

}
