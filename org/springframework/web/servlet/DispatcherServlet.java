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

package org.springframework.web.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.ui.context.ThemeSource;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.async.WebAsyncManager;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.util.NestedServletException;
import org.springframework.web.util.WebUtils;

/**
 * Central dispatcher for HTTP request handlers/controllers, e.g. for web UI controllers
 * or HTTP-based remote service exporters. Dispatches to registered handlers for processing
 * a web request, providing convenient mapping and exception handling facilities.
 *
 * <p>This servlet is very flexible: It can be used with just about any workflow, with the
 * installation of the appropriate adapter classes. It offers the following functionality
 * that distinguishes it from other request-driven web MVC frameworks:
 *
 * <ul>
 * <li>It is based around a JavaBeans configuration mechanism.
 *
 * <li>It can use any {@link HandlerMapping} implementation - pre-built or provided as part
 * of an application - to control the routing of requests to handler objects. Default is
 * {@link org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping} and
 * {@link org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping}.
 * HandlerMapping objects can be defined as beans in the servlet's application context,
 * implementing the HandlerMapping interface, overriding the default HandlerMapping if
 * present. HandlerMappings can be given any bean name (they are tested by type).
 *
 * <li>It can use any {@link HandlerAdapter}; this allows for using any handler interface.
 * Default adapters are {@link org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter},
 * {@link org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter}, for Spring's
 * {@link org.springframework.web.HttpRequestHandler} and
 * {@link org.springframework.web.servlet.mvc.Controller} interfaces, respectively. A default
 * {@link org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter}
 * will be registered as well. HandlerAdapter objects can be added as beans in the
 * application context, overriding the default HandlerAdapters. Like HandlerMappings,
 * HandlerAdapters can be given any bean name (they are tested by type).
 *
 * <li>The dispatcher's exception resolution strategy can be specified via a
 * {@link HandlerExceptionResolver}, for example mapping certain exceptions to error pages.
 * Default are
 * {@link org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerExceptionResolver},
 * {@link org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver}, and
 * {@link org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver}.
 * These HandlerExceptionResolvers can be overridden through the application context.
 * HandlerExceptionResolver can be given any bean name (they are tested by type).
 *
 * <li>Its view resolution strategy can be specified via a {@link ViewResolver}
 * implementation, resolving symbolic view names into View objects. Default is
 * {@link org.springframework.web.servlet.view.InternalResourceViewResolver}.
 * ViewResolver objects can be added as beans in the application context, overriding the
 * default ViewResolver. ViewResolvers can be given any bean name (they are tested by type).
 *
 * <li>If a {@link View} or view name is not supplied by the user, then the configured
 * {@link RequestToViewNameTranslator} will translate the current request into a view name.
 * The corresponding bean name is "viewNameTranslator"; the default is
 * {@link org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator}.
 *
 * <li>The dispatcher's strategy for resolving multipart requests is determined by a
 * {@link org.springframework.web.multipart.MultipartResolver} implementation.
 * Implementations for Apache Commons FileUpload and Servlet 3 are included; the typical
 * choice is {@link org.springframework.web.multipart.commons.CommonsMultipartResolver}.
 * The MultipartResolver bean name is "multipartResolver"; default is none.
 *
 * <li>Its locale resolution strategy is determined by a {@link LocaleResolver}.
 * Out-of-the-box implementations work via HTTP accept header, cookie, or session.
 * The LocaleResolver bean name is "localeResolver"; default is
 * {@link org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver}.
 *
 * <li>Its theme resolution strategy is determined by a {@link ThemeResolver}.
 * Implementations for a fixed theme and for cookie and session storage are included.
 * The ThemeResolver bean name is "themeResolver"; default is
 * {@link org.springframework.web.servlet.theme.FixedThemeResolver}.
 * </ul>
 *
 * <p><b>NOTE: The {@code @RequestMapping} annotation will only be processed if a
 * corresponding {@code HandlerMapping} (for type-level annotations) and/or
 * {@code HandlerAdapter} (for method-level annotations) is present in the dispatcher.</b>
 * This is the case by default. However, if you are defining custom {@code HandlerMappings}
 * or {@code HandlerAdapters}, then you need to make sure that a corresponding custom
 * {@code DefaultAnnotationHandlerMapping} and/or {@code AnnotationMethodHandlerAdapter}
 * is defined as well - provided that you intend to use {@code @RequestMapping}.
 *
 * <p><b>A web application can define any number of DispatcherServlets.</b>
 * Each servlet will operate in its own namespace, loading its own application context
 * with mappings, handlers, etc. Only the root application context as loaded by
 * {@link org.springframework.web.context.ContextLoaderListener}, if any, will be shared.
 *
 * <p>As of Spring 3.1, {@code DispatcherServlet} may now be injected with a web
 * application context, rather than creating its own internally. This is useful in Servlet
 * 3.0+ environments, which support programmatic registration of servlet instances.
 * See the {@link #DispatcherServlet(WebApplicationContext)} javadoc for details.
 *
 * <p>
 * HTTP请求处理程序/控制器的中央调度程序,例如用于Web UI控制器或基于HTTP的远程服务出口商向处理Web请求的注册处理程序进行调度,提供方便的映射和异常处理设施
 * 
 *  <p>这个servlet是非常灵活的：它可以与任何工作流一起使用,安装适当的适配器类它提供以下功能,区别于其他请求驱动的Web MVC框架：
 * 
 * <ul>
 *  <li>它基于JavaBeans配置机制
 * 
 * <li>它可以使用预先构建或作为应用程序一部分提供的任何{@link HandlerMapping}实现来控制请求到处理程序对象的路由。
 * 默认是{@link orgspringframeworkwebservlethandlerBeanNameUrlHandlerMapping}和{@link orgspringframeworkwebservletmvcannotationDefaultAnnotationHandlerMapping}
 *  HandlerMapping对象在servlet的应用程序上下文中定义为bean,实现HandlerMapping接口,覆盖默认的HandlerMapping(如果存在HandlerMapping),
 * 可以给出任何Bean名称(它们按类型测试)。
 * <li>它可以使用预先构建或作为应用程序一部分提供的任何{@link HandlerMapping}实现来控制请求到处理程序对象的路由。
 * 
 * <li>它可以使用任何{@link HandlerAdapter};这允许使用任何处理程序界面的默认适配器{@link orgspringframeworkwebservletmvcHttpRequestHandlerAdapter}
 * ,{@link orgspringframeworkwebservletmvcSimpleControllerHandlerAdapter},对Spring的{@link orgspringframeworkwebHttpRequestHandler}
 * 和{@link orgspringframeworkwebservletmvcController}接口,分别为A默认{@link orgspringframeworkwebservletmvcannotationAnnotationMethodHandlerAdapter}
 * 将被注册,以及HandlerAdapter对象可以作为应用程序上下文中的bean添加,覆盖默认的HandlerAdapters Like HandlerMappings,HandlerAdapters可
 * 以被赋予任何bean名称(它们按类型进行测试)。
 * 
 * <li>可以通过{@link HandlerExceptionResolver}指定调度程序的异常解决策略,例如将某些异常映射到错误页面。
 * 默认为{@link orgspringframeworkwebservletmvcannotationAnnotationMethodHandlerExceptionResolver},{@link orgspringframeworkwebservletmvcannotationResponseStatusExceptionResolver}
 * 和{@link orgspringframeworkwebservletmvcsupportDefaultHandlerExceptionResolver}这些HandlerExceptionResol
 * ver可以通过应用程序上下文覆盖HandlerExceptionResolver可以给出任何bean名称(它们按类型进行测试)。
 * <li>可以通过{@link HandlerExceptionResolver}指定调度程序的异常解决策略,例如将某些异常映射到错误页面。
 * 
 * <li>其视图分辨率策略可以通过{@link ViewResolver}实现指定,将符号视图名称解析为View对象默认为{@link orgspringframeworkwebservletviewInternalResourceViewResolver}
 *  ViewResolver对象可以作为应用程序上下文中的bean添加,覆盖默认ViewResolver ViewResolvers可以给出任何bean名称(它们按类型进行测试)。
 * 
 *  <li>如果用户不提供{@link View}或视图名称,则配置的{@link RequestToViewNameTranslator}会将当前请求转换为视图名称相应的bean名称为"viewName
 * Translator";默认为{@link orgspringframeworkwebservletviewDefaultRequestToViewNameTranslator}。
 * 
 * 解析多部分请求的调度员策略由{@link orgspringframeworkwebmultipartMultipartResolver}实现确定包含Apache Commons FileUpload和
 * Servlet 3的实现;典型的选择是{@link orgspringframeworkwebmultipartcommonsCommonsMultipartResolver} MultipartRes
 * olver bean的名称是"multipartResolver";默认为无。
 * 
 *  <li>其区域设置解决策略由{@link LocaleResolver}通过HTTP接受头,cookie或会话开箱即用的实现确定LocaleResolver bean名称为"localeResolve
 * r";默认是{@link orgspringframeworkwebservleti18nAcceptHeaderLocaleResolver}。
 * 
 * <li>其主题解析策略由固定主题的{@link ThemeResolver}实现决定,包括Cookie和会话存储。
 * ThemeResolver bean的名称是"themeResolver";默认为{@link orgspringframeworkwebservletthemeFixedThemeResolver}。
 * <li>其主题解析策略由固定主题的{@link ThemeResolver}实现决定,包括Cookie和会话存储。
 * </ul>
 * 
 * 注意：{@code @RequestMapping}注释只有在对应的{@code HandlerMapping}(用于类型级注释)和/或{@code HandlerAdapter}(用于方法级注释)时才
 * 会被处理,存在于调度程序中</b>默认情况下,但如果要定义自定义{@code HandlerMappings}或{@code HandlerAdapters},则需要确保相应的自定义{@code DefaultAnnotationHandlerMapping}
 * 和/或{@code AnnotationMethodHandlerAdapter}也被定义,只要你打算使用{@code @RequestMapping}。
 * 
 * <p> <b> Web应用程序可以定义任意数量的DispatcherServlet </b>每个servlet将在其自己的命名空间中运行,将其自己的应用程序上下文加载到映射,处理程序等只有{@link加载的根应用程序上下文orgspringframeworkwebcontextContextLoaderListener}
 * (如果有的话)将被共享。
 * 
 *  <p>截至春季31,{@code DispatcherServlet}现在可以注入一个Web应用程序上下文,而不是内部创建它在Servlet 30+环境中很有用,它支持servlet实例的编程注册参见
 * {@link #DispatcherServlet(WebApplicationContext)} javadoc的详细信息。
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Chris Beams
 * @author Rossen Stoyanchev
 * @see org.springframework.web.HttpRequestHandler
 * @see org.springframework.web.servlet.mvc.Controller
 * @see org.springframework.web.context.ContextLoaderListener
 */
@SuppressWarnings("serial")
public class DispatcherServlet extends FrameworkServlet {

	/** Well-known name for the MultipartResolver object in the bean factory for this namespace. */
	public static final String MULTIPART_RESOLVER_BEAN_NAME = "multipartResolver";

	/** Well-known name for the LocaleResolver object in the bean factory for this namespace. */
	public static final String LOCALE_RESOLVER_BEAN_NAME = "localeResolver";

	/** Well-known name for the ThemeResolver object in the bean factory for this namespace. */
	public static final String THEME_RESOLVER_BEAN_NAME = "themeResolver";

	/**
	 * Well-known name for the HandlerMapping object in the bean factory for this namespace.
	 * Only used when "detectAllHandlerMappings" is turned off.
	 * <p>
	 *  该名称空间的bean工厂中HandlerMapping对象的知名名称仅在"detectAllHandlerMappings"被关闭时使用
	 * 
	 * 
	 * @see #setDetectAllHandlerMappings
	 */
	public static final String HANDLER_MAPPING_BEAN_NAME = "handlerMapping";

	/**
	 * Well-known name for the HandlerAdapter object in the bean factory for this namespace.
	 * Only used when "detectAllHandlerAdapters" is turned off.
	 * <p>
	 * 该名称空间的bean工厂中HandlerAdapter对象的知名名称仅在"detectAllHandlerAdapters"关闭时使用
	 * 
	 * 
	 * @see #setDetectAllHandlerAdapters
	 */
	public static final String HANDLER_ADAPTER_BEAN_NAME = "handlerAdapter";

	/**
	 * Well-known name for the HandlerExceptionResolver object in the bean factory for this namespace.
	 * Only used when "detectAllHandlerExceptionResolvers" is turned off.
	 * <p>
	 *  该名称空间的bean工厂中HandlerExceptionResolver对象的知名名称仅在"detectAllHandlerExceptionResolvers"被关闭时使用
	 * 
	 * 
	 * @see #setDetectAllHandlerExceptionResolvers
	 */
	public static final String HANDLER_EXCEPTION_RESOLVER_BEAN_NAME = "handlerExceptionResolver";

	/**
	 * Well-known name for the RequestToViewNameTranslator object in the bean factory for this namespace.
	 * <p>
	 *  该名称空间的bean工厂中的RequestToViewNameTranslator对象的知名名称
	 * 
	 */
	public static final String REQUEST_TO_VIEW_NAME_TRANSLATOR_BEAN_NAME = "viewNameTranslator";

	/**
	 * Well-known name for the ViewResolver object in the bean factory for this namespace.
	 * Only used when "detectAllViewResolvers" is turned off.
	 * <p>
	 *  此命名空间的bean工厂中的ViewResolver对象的知名名称仅在"detectAllViewResolvers"关闭时使用
	 * 
	 * 
	 * @see #setDetectAllViewResolvers
	 */
	public static final String VIEW_RESOLVER_BEAN_NAME = "viewResolver";

	/**
	 * Well-known name for the FlashMapManager object in the bean factory for this namespace.
	 * <p>
	 *  该名称空间的bean工厂中的FlashMapManager对象的知名名称
	 * 
	 */
	public static final String FLASH_MAP_MANAGER_BEAN_NAME = "flashMapManager";

	/**
	 * Request attribute to hold the current web application context.
	 * Otherwise only the global web app context is obtainable by tags etc.
	 * <p>
	 *  请求属性保存当前的Web应用程序上下文否则只有全局Web应用程序上下文可以通过标签等获得
	 * 
	 * 
	 * @see org.springframework.web.servlet.support.RequestContextUtils#findWebApplicationContext
	 */
	public static final String WEB_APPLICATION_CONTEXT_ATTRIBUTE = DispatcherServlet.class.getName() + ".CONTEXT";

	/**
	 * Request attribute to hold the current LocaleResolver, retrievable by views.
	 * <p>
	 * 请求属性保存当前的LocaleResolver,可以通过视图检索
	 * 
	 * 
	 * @see org.springframework.web.servlet.support.RequestContextUtils#getLocaleResolver
	 */
	public static final String LOCALE_RESOLVER_ATTRIBUTE = DispatcherServlet.class.getName() + ".LOCALE_RESOLVER";

	/**
	 * Request attribute to hold the current ThemeResolver, retrievable by views.
	 * <p>
	 *  请求属性保存当前的ThemeResolver,可以通过视图检索
	 * 
	 * 
	 * @see org.springframework.web.servlet.support.RequestContextUtils#getThemeResolver
	 */
	public static final String THEME_RESOLVER_ATTRIBUTE = DispatcherServlet.class.getName() + ".THEME_RESOLVER";

	/**
	 * Request attribute to hold the current ThemeSource, retrievable by views.
	 * <p>
	 *  请求属性保存当前的ThemeSource,可以通过视图检索
	 * 
	 * 
	 * @see org.springframework.web.servlet.support.RequestContextUtils#getThemeSource
	 */
	public static final String THEME_SOURCE_ATTRIBUTE = DispatcherServlet.class.getName() + ".THEME_SOURCE";

	/**
	 * Name of request attribute that holds a read-only {@code Map<String,?>}
	 * with "input" flash attributes saved by a previous request, if any.
	 * <p>
	 *  持有只读{@code Map <String,?>}的请求属性的名称与前一个请求保存的"输入"flash属性(如果有)
	 * 
	 * 
	 * @see org.springframework.web.servlet.support.RequestContextUtils#getInputFlashMap(HttpServletRequest)
	 */
	public static final String INPUT_FLASH_MAP_ATTRIBUTE = DispatcherServlet.class.getName() + ".INPUT_FLASH_MAP";

	/**
	 * Name of request attribute that holds the "output" {@link FlashMap} with
	 * attributes to save for a subsequent request.
	 * <p>
	 *  持有"输出"{@link FlashMap}的请求属性的名称,具有为后续请求保存的属性
	 * 
	 * 
	 * @see org.springframework.web.servlet.support.RequestContextUtils#getOutputFlashMap(HttpServletRequest)
	 */
	public static final String OUTPUT_FLASH_MAP_ATTRIBUTE = DispatcherServlet.class.getName() + ".OUTPUT_FLASH_MAP";

	/**
	 * Name of request attribute that holds the {@link FlashMapManager}.
	 * <p>
	 *  持有{@link FlashMapManager}的请求属性名称
	 * 
	 * 
	 * @see org.springframework.web.servlet.support.RequestContextUtils#getFlashMapManager(HttpServletRequest)
	 */
	public static final String FLASH_MAP_MANAGER_ATTRIBUTE = DispatcherServlet.class.getName() + ".FLASH_MAP_MANAGER";

	/**
	 * Name of request attribute that exposes an Exception resolved with an
	 * {@link HandlerExceptionResolver} but where no view was rendered
	 * (e.g. setting the status code).
	 * <p>
	 *  使用{@link HandlerExceptionResolver}解决的异常,但没有呈现任何视图的请求属性的名称(例如设置状态代码)
	 * 
	 */
	public static final String EXCEPTION_ATTRIBUTE = DispatcherServlet.class.getName() + ".EXCEPTION";

	/** Log category to use when no mapped handler is found for a request. */
	public static final String PAGE_NOT_FOUND_LOG_CATEGORY = "org.springframework.web.servlet.PageNotFound";

	/**
	 * Name of the class path resource (relative to the DispatcherServlet class)
	 * that defines DispatcherServlet's default strategy names.
	 * <p>
	 * DispatcherServlet默认策略名称的类路径资源(相对于DispatcherServlet类)的名称
	 * 
	 */
	private static final String DEFAULT_STRATEGIES_PATH = "DispatcherServlet.properties";


	/** Additional logger to use when no mapped handler is found for a request. */
	protected static final Log pageNotFoundLogger = LogFactory.getLog(PAGE_NOT_FOUND_LOG_CATEGORY);

	private static final Properties defaultStrategies;

	static {
		// Load default strategy implementations from properties file.
		// This is currently strictly internal and not meant to be customized
		// by application developers.
		try {
			ClassPathResource resource = new ClassPathResource(DEFAULT_STRATEGIES_PATH, DispatcherServlet.class);
			defaultStrategies = PropertiesLoaderUtils.loadProperties(resource);
		}
		catch (IOException ex) {
			throw new IllegalStateException("Could not load 'DispatcherServlet.properties': " + ex.getMessage());
		}
	}

	/** Detect all HandlerMappings or just expect "handlerMapping" bean? */
	private boolean detectAllHandlerMappings = true;

	/** Detect all HandlerAdapters or just expect "handlerAdapter" bean? */
	private boolean detectAllHandlerAdapters = true;

	/** Detect all HandlerExceptionResolvers or just expect "handlerExceptionResolver" bean? */
	private boolean detectAllHandlerExceptionResolvers = true;

	/** Detect all ViewResolvers or just expect "viewResolver" bean? */
	private boolean detectAllViewResolvers = true;

	/** Throw a NoHandlerFoundException if no Handler was found to process this request? **/
	private boolean throwExceptionIfNoHandlerFound = false;

	/** Perform cleanup of request attributes after include request? */
	private boolean cleanupAfterInclude = true;

	/** MultipartResolver used by this servlet */
	private MultipartResolver multipartResolver;

	/** LocaleResolver used by this servlet */
	private LocaleResolver localeResolver;

	/** ThemeResolver used by this servlet */
	private ThemeResolver themeResolver;

	/** List of HandlerMappings used by this servlet */
	private List<HandlerMapping> handlerMappings;

	/** List of HandlerAdapters used by this servlet */
	private List<HandlerAdapter> handlerAdapters;

	/** List of HandlerExceptionResolvers used by this servlet */
	private List<HandlerExceptionResolver> handlerExceptionResolvers;

	/** RequestToViewNameTranslator used by this servlet */
	private RequestToViewNameTranslator viewNameTranslator;

	/** FlashMapManager used by this servlet */
	private FlashMapManager flashMapManager;

	/** List of ViewResolvers used by this servlet */
	private List<ViewResolver> viewResolvers;


	/**
	 * Create a new {@code DispatcherServlet} that will create its own internal web
	 * application context based on defaults and values provided through servlet
	 * init-params. Typically used in Servlet 2.5 or earlier environments, where the only
	 * option for servlet registration is through {@code web.xml} which requires the use
	 * of a no-arg constructor.
	 * <p>Calling {@link #setContextConfigLocation} (init-param 'contextConfigLocation')
	 * will dictate which XML files will be loaded by the
	 * {@linkplain #DEFAULT_CONTEXT_CLASS default XmlWebApplicationContext}
	 * <p>Calling {@link #setContextClass} (init-param 'contextClass') overrides the
	 * default {@code XmlWebApplicationContext} and allows for specifying an alternative class,
	 * such as {@code AnnotationConfigWebApplicationContext}.
	 * <p>Calling {@link #setContextInitializerClasses} (init-param 'contextInitializerClasses')
	 * indicates which {@code ApplicationContextInitializer} classes should be used to
	 * further configure the internal application context prior to refresh().
	 * <p>
	 * 创建一个新的{@code DispatcherServlet},它将基于通过servlet init-params提供的默认值和值创建自己的内部Web应用程序上下文通常用于Servlet 25或更早版本
	 * 的环境中,其中Servlet注册的唯一选项是通过{@code webxml }需要使用无参数构造函数调用{@link #setContextConfigLocation}(init-param'cont
	 * extConfigLocation)将决定哪些XML文件将由{@linkplain #DEFAULT_CONTEXT_CLASS默认XmlWebApplicationContext}加载到<p>调用{@link #setContextClass}
	 * (init-param'contextClass')将覆盖默认的{@code XmlWebApplicationContext},并允许指定一个替代类,例如{@code AnnotationConfigWebApplicationContext}
	 * <p>调用{@link #setContextInitializerClasses}(init-param'contextInitializerClasses')指示在刷新之前应使用哪些{@code ApplicationContextInitializer}
	 * 类进一步配置内部应用程序上下文。
	 * 
	 * 
	 * @see #DispatcherServlet(WebApplicationContext)
	 */
	public DispatcherServlet() {
		super();
		setDispatchOptionsRequest(true);
	}

	/**
	 * Create a new {@code DispatcherServlet} with the given web application context. This
	 * constructor is useful in Servlet 3.0+ environments where instance-based registration
	 * of servlets is possible through the {@link ServletContext#addServlet} API.
	 * <p>Using this constructor indicates that the following properties / init-params
	 * will be ignored:
	 * <ul>
	 * <li>{@link #setContextClass(Class)} / 'contextClass'</li>
	 * <li>{@link #setContextConfigLocation(String)} / 'contextConfigLocation'</li>
	 * <li>{@link #setContextAttribute(String)} / 'contextAttribute'</li>
	 * <li>{@link #setNamespace(String)} / 'namespace'</li>
	 * </ul>
	 * <p>The given web application context may or may not yet be {@linkplain
	 * ConfigurableApplicationContext#refresh() refreshed}. If it has <strong>not</strong>
	 * already been refreshed (the recommended approach), then the following will occur:
	 * <ul>
	 * <li>If the given context does not already have a {@linkplain
	 * ConfigurableApplicationContext#setParent parent}, the root application context
	 * will be set as the parent.</li>
	 * <li>If the given context has not already been assigned an {@linkplain
	 * ConfigurableApplicationContext#setId id}, one will be assigned to it</li>
	 * <li>{@code ServletContext} and {@code ServletConfig} objects will be delegated to
	 * the application context</li>
	 * <li>{@link #postProcessWebApplicationContext} will be called</li>
	 * <li>Any {@code ApplicationContextInitializer}s specified through the
	 * "contextInitializerClasses" init-param or through the {@link
	 * #setContextInitializers} property will be applied.</li>
	 * <li>{@link ConfigurableApplicationContext#refresh refresh()} will be called if the
	 * context implements {@link ConfigurableApplicationContext}</li>
	 * </ul>
	 * If the context has already been refreshed, none of the above will occur, under the
	 * assumption that the user has performed these actions (or not) per their specific
	 * needs.
	 * <p>See {@link org.springframework.web.WebApplicationInitializer} for usage examples.
	 * <p>
	 * 使用给定的Web应用程序上下文创建新的{@code DispatcherServlet}该构造函数在Servlet 30+环境中很有用,通过{@link ServletContext#addServlet}
	 *  API可以实现基于实例的servlet注册<p>使用此构造函数表示将忽略以下属性/ init-params：。
	 * <ul>
	 *  <li> {@ link #setContextClass(Class)} /'contextClass'</li> <li> {@ link #setContextConfigLocation(String)}
	 *  /'contextConfigLocation'</li> <li> {@ link #setContextAttribute )} /'contextAttribute'</li> <li> {@ link #setNamespace(String)}
	 *  /'namespace'</li>。
	 * </ul>
	 * <p>给定的Web应用程序上下文可能已经或可能尚未被{@linkplain ConfigurableApplicationContext#refresh()刷新}如果已经刷新了<strong>不</strong>
	 * (推荐的方法),则会发生以下情况：。
	 * <ul>
	 * <li>如果给定的上下文没有{@linkplain ConfigurableApplicationContext#setParent parent},则根应用程序上下文将被设置为父</li> <li>如
	 * 果给定的上下文尚未分配{@ linkplain ConfigurableApplicationContext#setId id},将分配一个</li> <li> {@ code ServletContext}
	 * 和{@code ServletConfig}对象将被委派给应用程序上下文</li> <li> {@ link# postProcessWebApplicationContext}将被称为</li> <li>
	 * 将通过"contextInitializerClasses"init-param或通过{@link #setContextInitializers}属性指定的任何{@code ApplicationContextInitializer}
	 * 将被应用</li> <li> {@ link ConfigurableApplicationContext#refresh refresh()}将被调用,如果上下文实现{@link ConfigurableApplicationContext}
	 *  </li>。
	 * </ul>
	 * 如果上下文已被刷新,则不会出现上述情况,假设用户根据具体需要执行了这些操作(或不执行)<p>有关使用示例,请参阅{@link orgspringframeworkwebWebApplicationInitializer}
	 * 。
	 * 
	 * 
	 * @param webApplicationContext the context to use
	 * @see #initWebApplicationContext
	 * @see #configureAndRefreshWebApplicationContext
	 * @see org.springframework.web.WebApplicationInitializer
	 */
	public DispatcherServlet(WebApplicationContext webApplicationContext) {
		super(webApplicationContext);
		setDispatchOptionsRequest(true);
	}


	/**
	 * Set whether to detect all HandlerMapping beans in this servlet's context. Otherwise,
	 * just a single bean with name "handlerMapping" will be expected.
	 * <p>Default is "true". Turn this off if you want this servlet to use a single
	 * HandlerMapping, despite multiple HandlerMapping beans being defined in the context.
	 * <p>
	 *  设置是否检测此servlet上下文中的所有HandlerMapping Bean否则,将只有一个名为"handlerMapping"的bean将被预期<p>默认为"true"如果您希望此servlet
	 * 使用单个HandlerMapping,尽管多次HandlerMapping bean在上下文中被定义。
	 * 
	 */
	public void setDetectAllHandlerMappings(boolean detectAllHandlerMappings) {
		this.detectAllHandlerMappings = detectAllHandlerMappings;
	}

	/**
	 * Set whether to detect all HandlerAdapter beans in this servlet's context. Otherwise,
	 * just a single bean with name "handlerAdapter" will be expected.
	 * <p>Default is "true". Turn this off if you want this servlet to use a single
	 * HandlerAdapter, despite multiple HandlerAdapter beans being defined in the context.
	 * <p>
	 * 设置是否检测这个servlet上下文中的所有HandlerAdapter bean否则,只有一个名为"handlerAdapter"的bean将被预期<p>默认为"true"如果您希望此servlet使
	 * 用单个HandlerAdapter,尽管多个HandlerAdapter bean在上下文中被定义。
	 * 
	 */
	public void setDetectAllHandlerAdapters(boolean detectAllHandlerAdapters) {
		this.detectAllHandlerAdapters = detectAllHandlerAdapters;
	}

	/**
	 * Set whether to detect all HandlerExceptionResolver beans in this servlet's context. Otherwise,
	 * just a single bean with name "handlerExceptionResolver" will be expected.
	 * <p>Default is "true". Turn this off if you want this servlet to use a single
	 * HandlerExceptionResolver, despite multiple HandlerExceptionResolver beans being defined in the context.
	 * <p>
	 *  设置是否检测此servlet上下文中的所有HandlerExceptionResolver bean否则,只有一个名为"handlerExceptionResolver"的bean将被预期<p>默认为
	 * "true"如果您希望此servlet使用单个HandlerExceptionResolver,尽管多个HandlerExceptionResolver bean在上下文中被定义。
	 * 
	 */
	public void setDetectAllHandlerExceptionResolvers(boolean detectAllHandlerExceptionResolvers) {
		this.detectAllHandlerExceptionResolvers = detectAllHandlerExceptionResolvers;
	}

	/**
	 * Set whether to detect all ViewResolver beans in this servlet's context. Otherwise,
	 * just a single bean with name "viewResolver" will be expected.
	 * <p>Default is "true". Turn this off if you want this servlet to use a single
	 * ViewResolver, despite multiple ViewResolver beans being defined in the context.
	 * <p>
	 * 设置是否检测此Servlet上下文中的所有ViewResolver bean否则,只有一个名为"viewResolver"的bean将被预期<p>默认为"true"如果您希望此servlet使用单个Vi
	 * ewResolver,尽管多个ViewResolver bean在上下文中被定义。
	 * 
	 */
	public void setDetectAllViewResolvers(boolean detectAllViewResolvers) {
		this.detectAllViewResolvers = detectAllViewResolvers;
	}

	/**
	 * Set whether to throw a NoHandlerFoundException when no Handler was found for this request.
	 * This exception can then be caught with a HandlerExceptionResolver or an
	 * {@code @ExceptionHandler} controller method.
	 * <p>Note that if {@link org.springframework.web.servlet.resource.DefaultServletHttpRequestHandler}
	 * is used, then requests will always be forwarded to the default servlet and a
	 * NoHandlerFoundException would never be thrown in that case.
	 * <p>Default is "false", meaning the DispatcherServlet sends a NOT_FOUND error through the
	 * Servlet response.
	 * <p>
	 * 设置是否在此请求找不到Handler时抛出NoHandlerFoundException此异常可以使用HandlerExceptionResolver或{@code @ExceptionHandler}
	 * 控制器方法捕获<p>请注意,如果使用{@link orgspringframeworkwebservletresourceDefaultServletHttpRequestHandler},则请求将总是
	 * 转发到默认的servlet,并且在这种情况下永远不会抛出一个NoHandlerFoundException <p>默认值为"false",这意味着DispatcherServlet通过Servlet响应
	 * 发送NOT_FOUND错误。
	 * 
	 * 
	 * @since 4.0
	 */
	public void setThrowExceptionIfNoHandlerFound(boolean throwExceptionIfNoHandlerFound) {
		this.throwExceptionIfNoHandlerFound = throwExceptionIfNoHandlerFound;
	}

	/**
	 * Set whether to perform cleanup of request attributes after an include request, that is,
	 * whether to reset the original state of all request attributes after the DispatcherServlet
	 * has processed within an include request. Otherwise, just the DispatcherServlet's own
	 * request attributes will be reset, but not model attributes for JSPs or special attributes
	 * set by views (for example, JSTL's).
	 * <p>Default is "true", which is strongly recommended. Views should not rely on request attributes
	 * having been set by (dynamic) includes. This allows JSP views rendered by an included controller
	 * to use any model attributes, even with the same names as in the main JSP, without causing side
	 * effects. Only turn this off for special needs, for example to deliberately allow main JSPs to
	 * access attributes from JSP views rendered by an included controller.
	 * <p>
	 * 设置是否在包含请求之后执行清除请求属性,即在DispatcherServlet在包含请求中处理之后是否重置所有请求属性的原始状态否则,只有DispatcherServlet自己的请求属性将被重置,而不是
	 * 模型属性为JSP或由视图设置的特殊属性(例如,JSTL的)<p>默认值为"true",强烈建议视图不应依赖于(动态)包含的请求属性。
	 * 这允许由包括控制器使用任何模型属性,即使与主JSP中的名称相同,也不会产生副作用只有为了特殊需要才能关闭它,例如,故意允许主JSP访问包含控制器呈现的JSP视图的属性。
	 * 
	 */
	public void setCleanupAfterInclude(boolean cleanupAfterInclude) {
		this.cleanupAfterInclude = cleanupAfterInclude;
	}


	/**
	 * This implementation calls {@link #initStrategies}.
	 * <p>
	 * 此实现调用{@link #initStrategies}
	 * 
	 */
	@Override
	protected void onRefresh(ApplicationContext context) {
		initStrategies(context);
	}

	/**
	 * Initialize the strategy objects that this servlet uses.
	 * <p>May be overridden in subclasses in order to initialize further strategy objects.
	 * <p>
	 *  初始化servlet使用的策略对象<p>可能会在子类中被覆盖,以便初始化其他策略对象
	 * 
	 */
	protected void initStrategies(ApplicationContext context) {
		initMultipartResolver(context);
		initLocaleResolver(context);
		initThemeResolver(context);
		initHandlerMappings(context);
		initHandlerAdapters(context);
		initHandlerExceptionResolvers(context);
		initRequestToViewNameTranslator(context);
		initViewResolvers(context);
		initFlashMapManager(context);
	}

	/**
	 * Initialize the MultipartResolver used by this class.
	 * <p>If no bean is defined with the given name in the BeanFactory for this namespace,
	 * no multipart handling is provided.
	 * <p>
	 *  初始化此类使用的MultipartResolver <p>如果在BeanFactory中为此命名空间定义了与给定名称的bean,则不会提供多部分处理
	 * 
	 */
	private void initMultipartResolver(ApplicationContext context) {
		try {
			this.multipartResolver = context.getBean(MULTIPART_RESOLVER_BEAN_NAME, MultipartResolver.class);
			if (logger.isDebugEnabled()) {
				logger.debug("Using MultipartResolver [" + this.multipartResolver + "]");
			}
		}
		catch (NoSuchBeanDefinitionException ex) {
			// Default is no multipart resolver.
			this.multipartResolver = null;
			if (logger.isDebugEnabled()) {
				logger.debug("Unable to locate MultipartResolver with name '" + MULTIPART_RESOLVER_BEAN_NAME +
						"': no multipart request handling provided");
			}
		}
	}

	/**
	 * Initialize the LocaleResolver used by this class.
	 * <p>If no bean is defined with the given name in the BeanFactory for this namespace,
	 * we default to AcceptHeaderLocaleResolver.
	 * <p>
	 *  初始化此类使用的LocaleResolver <p>如果在BeanFactory中为此命名空间定义了bean,那么我们将默认为AcceptHeaderLocaleResolver
	 * 
	 */
	private void initLocaleResolver(ApplicationContext context) {
		try {
			this.localeResolver = context.getBean(LOCALE_RESOLVER_BEAN_NAME, LocaleResolver.class);
			if (logger.isDebugEnabled()) {
				logger.debug("Using LocaleResolver [" + this.localeResolver + "]");
			}
		}
		catch (NoSuchBeanDefinitionException ex) {
			// We need to use the default.
			this.localeResolver = getDefaultStrategy(context, LocaleResolver.class);
			if (logger.isDebugEnabled()) {
				logger.debug("Unable to locate LocaleResolver with name '" + LOCALE_RESOLVER_BEAN_NAME +
						"': using default [" + this.localeResolver + "]");
			}
		}
	}

	/**
	 * Initialize the ThemeResolver used by this class.
	 * <p>If no bean is defined with the given name in the BeanFactory for this namespace,
	 * we default to a FixedThemeResolver.
	 * <p>
	 *  初始化此类使用的ThemeResolver <p>如果在BeanFactory中为此命名空间定义了一个bean,那么我们默认使用FixedThemeResolver
	 * 
	 */
	private void initThemeResolver(ApplicationContext context) {
		try {
			this.themeResolver = context.getBean(THEME_RESOLVER_BEAN_NAME, ThemeResolver.class);
			if (logger.isDebugEnabled()) {
				logger.debug("Using ThemeResolver [" + this.themeResolver + "]");
			}
		}
		catch (NoSuchBeanDefinitionException ex) {
			// We need to use the default.
			this.themeResolver = getDefaultStrategy(context, ThemeResolver.class);
			if (logger.isDebugEnabled()) {
				logger.debug("Unable to locate ThemeResolver with name '" + THEME_RESOLVER_BEAN_NAME +
						"': using default [" + this.themeResolver + "]");
			}
		}
	}

	/**
	 * Initialize the HandlerMappings used by this class.
	 * <p>If no HandlerMapping beans are defined in the BeanFactory for this namespace,
	 * we default to BeanNameUrlHandlerMapping.
	 * <p>
	 * 初始化此类使用的HandlerMappings <p>如果在此命名空间的BeanFactory中未定义HandlerMapping bean,那么我们将默认为BeanNameUrlHandlerMapp
	 * ing。
	 * 
	 */
	private void initHandlerMappings(ApplicationContext context) {
		this.handlerMappings = null;

		if (this.detectAllHandlerMappings) {
			// Find all HandlerMappings in the ApplicationContext, including ancestor contexts.
			Map<String, HandlerMapping> matchingBeans =
					BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerMapping.class, true, false);
			if (!matchingBeans.isEmpty()) {
				this.handlerMappings = new ArrayList<HandlerMapping>(matchingBeans.values());
				// We keep HandlerMappings in sorted order.
				AnnotationAwareOrderComparator.sort(this.handlerMappings);
			}
		}
		else {
			try {
				HandlerMapping hm = context.getBean(HANDLER_MAPPING_BEAN_NAME, HandlerMapping.class);
				this.handlerMappings = Collections.singletonList(hm);
			}
			catch (NoSuchBeanDefinitionException ex) {
				// Ignore, we'll add a default HandlerMapping later.
			}
		}

		// Ensure we have at least one HandlerMapping, by registering
		// a default HandlerMapping if no other mappings are found.
		if (this.handlerMappings == null) {
			this.handlerMappings = getDefaultStrategies(context, HandlerMapping.class);
			if (logger.isDebugEnabled()) {
				logger.debug("No HandlerMappings found in servlet '" + getServletName() + "': using default");
			}
		}
	}

	/**
	 * Initialize the HandlerAdapters used by this class.
	 * <p>If no HandlerAdapter beans are defined in the BeanFactory for this namespace,
	 * we default to SimpleControllerHandlerAdapter.
	 * <p>
	 *  初始化此类使用的HandlerAdapters <p>如果在此命名空间的BeanFactory中未定义HandlerAdapter bean,那么我们默认为SimpleControllerHandle
	 * rAdapter。
	 * 
	 */
	private void initHandlerAdapters(ApplicationContext context) {
		this.handlerAdapters = null;

		if (this.detectAllHandlerAdapters) {
			// Find all HandlerAdapters in the ApplicationContext, including ancestor contexts.
			Map<String, HandlerAdapter> matchingBeans =
					BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerAdapter.class, true, false);
			if (!matchingBeans.isEmpty()) {
				this.handlerAdapters = new ArrayList<HandlerAdapter>(matchingBeans.values());
				// We keep HandlerAdapters in sorted order.
				AnnotationAwareOrderComparator.sort(this.handlerAdapters);
			}
		}
		else {
			try {
				HandlerAdapter ha = context.getBean(HANDLER_ADAPTER_BEAN_NAME, HandlerAdapter.class);
				this.handlerAdapters = Collections.singletonList(ha);
			}
			catch (NoSuchBeanDefinitionException ex) {
				// Ignore, we'll add a default HandlerAdapter later.
			}
		}

		// Ensure we have at least some HandlerAdapters, by registering
		// default HandlerAdapters if no other adapters are found.
		if (this.handlerAdapters == null) {
			this.handlerAdapters = getDefaultStrategies(context, HandlerAdapter.class);
			if (logger.isDebugEnabled()) {
				logger.debug("No HandlerAdapters found in servlet '" + getServletName() + "': using default");
			}
		}
	}

	/**
	 * Initialize the HandlerExceptionResolver used by this class.
	 * <p>If no bean is defined with the given name in the BeanFactory for this namespace,
	 * we default to no exception resolver.
	 * <p>
	 *  初始化此类使用的HandlerExceptionResolver <p>如果在BeanFactory中为此命名空间定义了没有bean的给定名称,那么我们默认不会有异常解析器
	 * 
	 */
	private void initHandlerExceptionResolvers(ApplicationContext context) {
		this.handlerExceptionResolvers = null;

		if (this.detectAllHandlerExceptionResolvers) {
			// Find all HandlerExceptionResolvers in the ApplicationContext, including ancestor contexts.
			Map<String, HandlerExceptionResolver> matchingBeans = BeanFactoryUtils
					.beansOfTypeIncludingAncestors(context, HandlerExceptionResolver.class, true, false);
			if (!matchingBeans.isEmpty()) {
				this.handlerExceptionResolvers = new ArrayList<HandlerExceptionResolver>(matchingBeans.values());
				// We keep HandlerExceptionResolvers in sorted order.
				AnnotationAwareOrderComparator.sort(this.handlerExceptionResolvers);
			}
		}
		else {
			try {
				HandlerExceptionResolver her =
						context.getBean(HANDLER_EXCEPTION_RESOLVER_BEAN_NAME, HandlerExceptionResolver.class);
				this.handlerExceptionResolvers = Collections.singletonList(her);
			}
			catch (NoSuchBeanDefinitionException ex) {
				// Ignore, no HandlerExceptionResolver is fine too.
			}
		}

		// Ensure we have at least some HandlerExceptionResolvers, by registering
		// default HandlerExceptionResolvers if no other resolvers are found.
		if (this.handlerExceptionResolvers == null) {
			this.handlerExceptionResolvers = getDefaultStrategies(context, HandlerExceptionResolver.class);
			if (logger.isDebugEnabled()) {
				logger.debug("No HandlerExceptionResolvers found in servlet '" + getServletName() + "': using default");
			}
		}
	}

	/**
	 * Initialize the RequestToViewNameTranslator used by this servlet instance.
	 * <p>If no implementation is configured then we default to DefaultRequestToViewNameTranslator.
	 * <p>
	 *  初始化此Servlet实例使用的RequestToViewNameTranslator <p>如果未配置实现,那么我们默认为DefaultRequestToViewNameTranslator
	 * 
	 */
	private void initRequestToViewNameTranslator(ApplicationContext context) {
		try {
			this.viewNameTranslator =
					context.getBean(REQUEST_TO_VIEW_NAME_TRANSLATOR_BEAN_NAME, RequestToViewNameTranslator.class);
			if (logger.isDebugEnabled()) {
				logger.debug("Using RequestToViewNameTranslator [" + this.viewNameTranslator + "]");
			}
		}
		catch (NoSuchBeanDefinitionException ex) {
			// We need to use the default.
			this.viewNameTranslator = getDefaultStrategy(context, RequestToViewNameTranslator.class);
			if (logger.isDebugEnabled()) {
				logger.debug("Unable to locate RequestToViewNameTranslator with name '" +
						REQUEST_TO_VIEW_NAME_TRANSLATOR_BEAN_NAME + "': using default [" + this.viewNameTranslator +
						"]");
			}
		}
	}

	/**
	 * Initialize the ViewResolvers used by this class.
	 * <p>If no ViewResolver beans are defined in the BeanFactory for this
	 * namespace, we default to InternalResourceViewResolver.
	 * <p>
	 * 初始化此类使用的ViewResolvers <p>如果在此命名空间的BeanFactory中未定义ViewResolver bean,那么我们默认为InternalResourceViewResolve
	 * r。
	 * 
	 */
	private void initViewResolvers(ApplicationContext context) {
		this.viewResolvers = null;

		if (this.detectAllViewResolvers) {
			// Find all ViewResolvers in the ApplicationContext, including ancestor contexts.
			Map<String, ViewResolver> matchingBeans =
					BeanFactoryUtils.beansOfTypeIncludingAncestors(context, ViewResolver.class, true, false);
			if (!matchingBeans.isEmpty()) {
				this.viewResolvers = new ArrayList<ViewResolver>(matchingBeans.values());
				// We keep ViewResolvers in sorted order.
				AnnotationAwareOrderComparator.sort(this.viewResolvers);
			}
		}
		else {
			try {
				ViewResolver vr = context.getBean(VIEW_RESOLVER_BEAN_NAME, ViewResolver.class);
				this.viewResolvers = Collections.singletonList(vr);
			}
			catch (NoSuchBeanDefinitionException ex) {
				// Ignore, we'll add a default ViewResolver later.
			}
		}

		// Ensure we have at least one ViewResolver, by registering
		// a default ViewResolver if no other resolvers are found.
		if (this.viewResolvers == null) {
			this.viewResolvers = getDefaultStrategies(context, ViewResolver.class);
			if (logger.isDebugEnabled()) {
				logger.debug("No ViewResolvers found in servlet '" + getServletName() + "': using default");
			}
		}
	}

	/**
	 * Initialize the {@link FlashMapManager} used by this servlet instance.
	 * <p>If no implementation is configured then we default to
	 * {@code org.springframework.web.servlet.support.DefaultFlashMapManager}.
	 * <p>
	 *  初始化这个servlet实例使用的{@link FlashMapManager} <p>如果没有配置实现,那么我们默认为{@code orgspringframeworkwebservletsupportDefaultFlashMapManager}
	 * 。
	 * 
	 */
	private void initFlashMapManager(ApplicationContext context) {
		try {
			this.flashMapManager = context.getBean(FLASH_MAP_MANAGER_BEAN_NAME, FlashMapManager.class);
			if (logger.isDebugEnabled()) {
				logger.debug("Using FlashMapManager [" + this.flashMapManager + "]");
			}
		}
		catch (NoSuchBeanDefinitionException ex) {
			// We need to use the default.
			this.flashMapManager = getDefaultStrategy(context, FlashMapManager.class);
			if (logger.isDebugEnabled()) {
				logger.debug("Unable to locate FlashMapManager with name '" +
						FLASH_MAP_MANAGER_BEAN_NAME + "': using default [" + this.flashMapManager + "]");
			}
		}
	}

	/**
	 * Return this servlet's ThemeSource, if any; else return {@code null}.
	 * <p>Default is to return the WebApplicationContext as ThemeSource,
	 * provided that it implements the ThemeSource interface.
	 * <p>
	 *  返回这个servlet的ThemeSource(如果有的话) else return {@code null} <p>默认是将WebApplicationContext作为ThemeSource返回,
	 * 前提是它实现了ThemeSource接口。
	 * 
	 * 
	 * @return the ThemeSource, if any
	 * @see #getWebApplicationContext()
	 */
	public final ThemeSource getThemeSource() {
		if (getWebApplicationContext() instanceof ThemeSource) {
			return (ThemeSource) getWebApplicationContext();
		}
		else {
			return null;
		}
	}

	/**
	 * Obtain this servlet's MultipartResolver, if any.
	 * <p>
	 *  获取这个servlet的MultipartResolver(如果有的话)
	 * 
	 * 
	 * @return the MultipartResolver used by this servlet, or {@code null} if none
	 * (indicating that no multipart support is available)
	 */
	public final MultipartResolver getMultipartResolver() {
		return this.multipartResolver;
	}

	/**
	 * Return the default strategy object for the given strategy interface.
	 * <p>The default implementation delegates to {@link #getDefaultStrategies},
	 * expecting a single object in the list.
	 * <p>
	 *  返回给定策略接口的默认策略对象<p>默认实现委托给{@link #getDefaultStrategies},期望列表中的单个对象
	 * 
	 * 
	 * @param context the current WebApplicationContext
	 * @param strategyInterface the strategy interface
	 * @return the corresponding strategy object
	 * @see #getDefaultStrategies
	 */
	protected <T> T getDefaultStrategy(ApplicationContext context, Class<T> strategyInterface) {
		List<T> strategies = getDefaultStrategies(context, strategyInterface);
		if (strategies.size() != 1) {
			throw new BeanInitializationException(
					"DispatcherServlet needs exactly 1 strategy for interface [" + strategyInterface.getName() + "]");
		}
		return strategies.get(0);
	}

	/**
	 * Create a List of default strategy objects for the given strategy interface.
	 * <p>The default implementation uses the "DispatcherServlet.properties" file (in the same
	 * package as the DispatcherServlet class) to determine the class names. It instantiates
	 * the strategy objects through the context's BeanFactory.
	 * <p>
	 * 创建给定策略接口的默认策略对象列表<p>默认实现使用"DispatcherServletproperties"文件(与DispatcherServlet类在同一个包中)来确定类名称它通过上下文的Bean
	 * Factory实例化策略对象。
	 * 
	 * 
	 * @param context the current WebApplicationContext
	 * @param strategyInterface the strategy interface
	 * @return the List of corresponding strategy objects
	 */
	@SuppressWarnings("unchecked")
	protected <T> List<T> getDefaultStrategies(ApplicationContext context, Class<T> strategyInterface) {
		String key = strategyInterface.getName();
		String value = defaultStrategies.getProperty(key);
		if (value != null) {
			String[] classNames = StringUtils.commaDelimitedListToStringArray(value);
			List<T> strategies = new ArrayList<T>(classNames.length);
			for (String className : classNames) {
				try {
					Class<?> clazz = ClassUtils.forName(className, DispatcherServlet.class.getClassLoader());
					Object strategy = createDefaultStrategy(context, clazz);
					strategies.add((T) strategy);
				}
				catch (ClassNotFoundException ex) {
					throw new BeanInitializationException(
							"Could not find DispatcherServlet's default strategy class [" + className +
									"] for interface [" + key + "]", ex);
				}
				catch (LinkageError err) {
					throw new BeanInitializationException(
							"Error loading DispatcherServlet's default strategy class [" + className +
									"] for interface [" + key + "]: problem with class file or dependent class", err);
				}
			}
			return strategies;
		}
		else {
			return new LinkedList<T>();
		}
	}

	/**
	 * Create a default strategy.
	 * <p>The default implementation uses
	 * {@link org.springframework.beans.factory.config.AutowireCapableBeanFactory#createBean}.
	 * <p>
	 *  创建默认策略<p>默认实现使用{@link orgspringframeworkbeansfactoryconfigAutowireCapableBeanFactory#createBean}
	 * 
	 * 
	 * @param context the current WebApplicationContext
	 * @param clazz the strategy implementation class to instantiate
	 * @return the fully configured strategy instance
	 * @see org.springframework.context.ApplicationContext#getAutowireCapableBeanFactory()
	 * @see org.springframework.beans.factory.config.AutowireCapableBeanFactory#createBean
	 */
	protected Object createDefaultStrategy(ApplicationContext context, Class<?> clazz) {
		return context.getAutowireCapableBeanFactory().createBean(clazz);
	}


	/**
	 * Exposes the DispatcherServlet-specific request attributes and delegates to {@link #doDispatch}
	 * for the actual dispatching.
	 * <p>
	 *  将DispatcherServlet特定的请求属性和代理暴露给实际调度的{@link #doDispatch}
	 * 
	 */
	@Override
	protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (logger.isDebugEnabled()) {
			String resumed = WebAsyncUtils.getAsyncManager(request).hasConcurrentResult() ? " resumed" : "";
			logger.debug("DispatcherServlet with name '" + getServletName() + "'" + resumed +
					" processing " + request.getMethod() + " request for [" + getRequestUri(request) + "]");
		}

		// Keep a snapshot of the request attributes in case of an include,
		// to be able to restore the original attributes after the include.
		Map<String, Object> attributesSnapshot = null;
		if (WebUtils.isIncludeRequest(request)) {
			attributesSnapshot = new HashMap<String, Object>();
			Enumeration<?> attrNames = request.getAttributeNames();
			while (attrNames.hasMoreElements()) {
				String attrName = (String) attrNames.nextElement();
				if (this.cleanupAfterInclude || attrName.startsWith("org.springframework.web.servlet")) {
					attributesSnapshot.put(attrName, request.getAttribute(attrName));
				}
			}
		}

		// Make framework objects available to handlers and view objects.
		request.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, getWebApplicationContext());
		request.setAttribute(LOCALE_RESOLVER_ATTRIBUTE, this.localeResolver);
		request.setAttribute(THEME_RESOLVER_ATTRIBUTE, this.themeResolver);
		request.setAttribute(THEME_SOURCE_ATTRIBUTE, getThemeSource());

		FlashMap inputFlashMap = this.flashMapManager.retrieveAndUpdate(request, response);
		if (inputFlashMap != null) {
			request.setAttribute(INPUT_FLASH_MAP_ATTRIBUTE, Collections.unmodifiableMap(inputFlashMap));
		}
		request.setAttribute(OUTPUT_FLASH_MAP_ATTRIBUTE, new FlashMap());
		request.setAttribute(FLASH_MAP_MANAGER_ATTRIBUTE, this.flashMapManager);

		try {
			doDispatch(request, response);
		}
		finally {
			if (!WebAsyncUtils.getAsyncManager(request).isConcurrentHandlingStarted()) {
				// Restore the original attribute snapshot, in case of an include.
				if (attributesSnapshot != null) {
					restoreAttributesAfterInclude(request, attributesSnapshot);
				}
			}
		}
	}

	/**
	 * Process the actual dispatching to the handler.
	 * <p>The handler will be obtained by applying the servlet's HandlerMappings in order.
	 * The HandlerAdapter will be obtained by querying the servlet's installed HandlerAdapters
	 * to find the first that supports the handler class.
	 * <p>All HTTP methods are handled by this method. It's up to HandlerAdapters or handlers
	 * themselves to decide which methods are acceptable.
	 * <p>
	 * 处理实际的调度到处理程序<p>处理程序将通过应用servlet的HandlerMappings来获得。
	 * HandlerAdapter将通过查询servlet的已安装的HandlerAdapter来获取,以查找支持处理程序类的第一个<p>所有HTTP方法都被处理通过这种方法由HandlerAdapters或
	 * 处理程序自己决定哪些方法是可以接受的。
	 * 处理实际的调度到处理程序<p>处理程序将通过应用servlet的HandlerMappings来获得。
	 * 
	 * 
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @throws Exception in case of any kind of processing failure
	 */
	protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpServletRequest processedRequest = request;
		HandlerExecutionChain mappedHandler = null;
		boolean multipartRequestParsed = false;

		WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);

		try {
			ModelAndView mv = null;
			Exception dispatchException = null;

			try {
				processedRequest = checkMultipart(request);
				multipartRequestParsed = (processedRequest != request);

				// Determine handler for the current request.
				mappedHandler = getHandler(processedRequest);
				if (mappedHandler == null || mappedHandler.getHandler() == null) {
					noHandlerFound(processedRequest, response);
					return;
				}

				// Determine handler adapter for the current request.
				HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());

				// Process last-modified header, if supported by the handler.
				String method = request.getMethod();
				boolean isGet = "GET".equals(method);
				if (isGet || "HEAD".equals(method)) {
					long lastModified = ha.getLastModified(request, mappedHandler.getHandler());
					if (logger.isDebugEnabled()) {
						logger.debug("Last-Modified value for [" + getRequestUri(request) + "] is: " + lastModified);
					}
					if (new ServletWebRequest(request, response).checkNotModified(lastModified) && isGet) {
						return;
					}
				}

				if (!mappedHandler.applyPreHandle(processedRequest, response)) {
					return;
				}

				// Actually invoke the handler.
				mv = ha.handle(processedRequest, response, mappedHandler.getHandler());

				if (asyncManager.isConcurrentHandlingStarted()) {
					return;
				}

				applyDefaultViewName(processedRequest, mv);
				mappedHandler.applyPostHandle(processedRequest, response, mv);
			}
			catch (Exception ex) {
				dispatchException = ex;
			}
			catch (Throwable err) {
				// As of 4.3, we're processing Errors thrown from handler methods as well,
				// making them available for @ExceptionHandler methods and other scenarios.
				dispatchException = new NestedServletException("Handler dispatch failed", err);
			}
			processDispatchResult(processedRequest, response, mappedHandler, mv, dispatchException);
		}
		catch (Exception ex) {
			triggerAfterCompletion(processedRequest, response, mappedHandler, ex);
		}
		catch (Throwable err) {
			triggerAfterCompletion(processedRequest, response, mappedHandler,
					new NestedServletException("Handler processing failed", err));
		}
		finally {
			if (asyncManager.isConcurrentHandlingStarted()) {
				// Instead of postHandle and afterCompletion
				if (mappedHandler != null) {
					mappedHandler.applyAfterConcurrentHandlingStarted(processedRequest, response);
				}
			}
			else {
				// Clean up any resources used by a multipart request.
				if (multipartRequestParsed) {
					cleanupMultipart(processedRequest);
				}
			}
		}
	}

	/**
	 * Do we need view name translation?
	 * <p>
	 *  我们需要查看名称翻译吗?
	 * 
	 */
	private void applyDefaultViewName(HttpServletRequest request, ModelAndView mv) throws Exception {
		if (mv != null && !mv.hasView()) {
			mv.setViewName(getDefaultViewName(request));
		}
	}

	/**
	 * Handle the result of handler selection and handler invocation, which is
	 * either a ModelAndView or an Exception to be resolved to a ModelAndView.
	 * <p>
	 *  处理处理程序选择和处理程序调用的结果,这是一个ModelAndView或要解析为ModelAndView的异常
	 * 
	 */
	private void processDispatchResult(HttpServletRequest request, HttpServletResponse response,
			HandlerExecutionChain mappedHandler, ModelAndView mv, Exception exception) throws Exception {

		boolean errorView = false;

		if (exception != null) {
			if (exception instanceof ModelAndViewDefiningException) {
				logger.debug("ModelAndViewDefiningException encountered", exception);
				mv = ((ModelAndViewDefiningException) exception).getModelAndView();
			}
			else {
				Object handler = (mappedHandler != null ? mappedHandler.getHandler() : null);
				mv = processHandlerException(request, response, handler, exception);
				errorView = (mv != null);
			}
		}

		// Did the handler return a view to render?
		if (mv != null && !mv.wasCleared()) {
			render(mv, request, response);
			if (errorView) {
				WebUtils.clearErrorRequestAttributes(request);
			}
		}
		else {
			if (logger.isDebugEnabled()) {
				logger.debug("Null ModelAndView returned to DispatcherServlet with name '" + getServletName() +
						"': assuming HandlerAdapter completed request handling");
			}
		}

		if (WebAsyncUtils.getAsyncManager(request).isConcurrentHandlingStarted()) {
			// Concurrent handling started during a forward
			return;
		}

		if (mappedHandler != null) {
			mappedHandler.triggerAfterCompletion(request, response, null);
		}
	}

	/**
	 * Build a LocaleContext for the given request, exposing the request's primary locale as current locale.
	 * <p>The default implementation uses the dispatcher's LocaleResolver to obtain the current locale,
	 * which might change during a request.
	 * <p>
	 * 为给定的请求构建LocaleContext,将请求的主区域显示为当前区域设置<p>默认实现使用调度程序的LocaleResolver来获取当前语言环境,这可能在请求期间发生更改
	 * 
	 * 
	 * @param request current HTTP request
	 * @return the corresponding LocaleContext
	 */
	@Override
	protected LocaleContext buildLocaleContext(final HttpServletRequest request) {
		if (this.localeResolver instanceof LocaleContextResolver) {
			return ((LocaleContextResolver) this.localeResolver).resolveLocaleContext(request);
		}
		else {
			return new LocaleContext() {
				@Override
				public Locale getLocale() {
					return localeResolver.resolveLocale(request);
				}
			};
		}
	}

	/**
	 * Convert the request into a multipart request, and make multipart resolver available.
	 * <p>If no multipart resolver is set, simply use the existing request.
	 * <p>
	 *  将请求转换为多部分请求,并使多部分解析器可用<p>如果没有设置多部分解析器,只需使用现有请求
	 * 
	 * 
	 * @param request current HTTP request
	 * @return the processed request (multipart wrapper if necessary)
	 * @see MultipartResolver#resolveMultipart
	 */
	protected HttpServletRequest checkMultipart(HttpServletRequest request) throws MultipartException {
		if (this.multipartResolver != null && this.multipartResolver.isMultipart(request)) {
			if (WebUtils.getNativeRequest(request, MultipartHttpServletRequest.class) != null) {
				logger.debug("Request is already a MultipartHttpServletRequest - if not in a forward, " +
						"this typically results from an additional MultipartFilter in web.xml");
			}
			else if (request.getAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE) instanceof MultipartException) {
				logger.debug("Multipart resolution failed for current request before - " +
						"skipping re-resolution for undisturbed error rendering");
			}
			else {
				return this.multipartResolver.resolveMultipart(request);
			}
		}
		// If not returned before: return original request.
		return request;
	}

	/**
	 * Clean up any resources used by the given multipart request (if any).
	 * <p>
	 *  清理给定的多部分请求使用的任何资源(如果有的话)
	 * 
	 * 
	 * @param request current HTTP request
	 * @see MultipartResolver#cleanupMultipart
	 */
	protected void cleanupMultipart(HttpServletRequest request) {
		MultipartHttpServletRequest multipartRequest =
				WebUtils.getNativeRequest(request, MultipartHttpServletRequest.class);
		if (multipartRequest != null) {
			this.multipartResolver.cleanupMultipart(multipartRequest);
		}
	}

	/**
	 * Return the HandlerExecutionChain for this request.
	 * <p>Tries all handler mappings in order.
	 * <p>
	 *  返回此请求的HandlerExecutionChain <p>按顺序尝试所有处理程序映射
	 * 
	 * 
	 * @param request current HTTP request
	 * @return the HandlerExecutionChain, or {@code null} if no handler could be found
	 */
	protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
		for (HandlerMapping hm : this.handlerMappings) {
			if (logger.isTraceEnabled()) {
				logger.trace(
						"Testing handler map [" + hm + "] in DispatcherServlet with name '" + getServletName() + "'");
			}
			HandlerExecutionChain handler = hm.getHandler(request);
			if (handler != null) {
				return handler;
			}
		}
		return null;
	}

	/**
	 * No handler found -> set appropriate HTTP response status.
	 * <p>
	 *  找不到处理程序 - >设置适当的HTTP响应状态
	 * 
	 * 
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @throws Exception if preparing the response failed
	 */
	protected void noHandlerFound(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (pageNotFoundLogger.isWarnEnabled()) {
			pageNotFoundLogger.warn("No mapping found for HTTP request with URI [" + getRequestUri(request) +
					"] in DispatcherServlet with name '" + getServletName() + "'");
		}
		if (this.throwExceptionIfNoHandlerFound) {
			throw new NoHandlerFoundException(request.getMethod(), getRequestUri(request),
					new ServletServerHttpRequest(request).getHeaders());
		}
		else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	/**
	 * Return the HandlerAdapter for this handler object.
	 * <p>
	 *  返回此处理程序对象的HandlerAdapter
	 * 
	 * 
	 * @param handler the handler object to find an adapter for
	 * @throws ServletException if no HandlerAdapter can be found for the handler. This is a fatal error.
	 */
	protected HandlerAdapter getHandlerAdapter(Object handler) throws ServletException {
		for (HandlerAdapter ha : this.handlerAdapters) {
			if (logger.isTraceEnabled()) {
				logger.trace("Testing handler adapter [" + ha + "]");
			}
			if (ha.supports(handler)) {
				return ha;
			}
		}
		throw new ServletException("No adapter for handler [" + handler +
				"]: The DispatcherServlet configuration needs to include a HandlerAdapter that supports this handler");
	}

	/**
	 * Determine an error ModelAndView via the registered HandlerExceptionResolvers.
	 * <p>
	 *  通过注册的HandlerExceptionResolvers确定ModelAndView的错误
	 * 
	 * 
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler the executed handler, or {@code null} if none chosen at the time of the exception
	 * (for example, if multipart resolution failed)
	 * @param ex the exception that got thrown during handler execution
	 * @return a corresponding ModelAndView to forward to
	 * @throws Exception if no error ModelAndView found
	 */
	protected ModelAndView processHandlerException(HttpServletRequest request, HttpServletResponse response,
			Object handler, Exception ex) throws Exception {

		// Check registered HandlerExceptionResolvers...
		ModelAndView exMv = null;
		for (HandlerExceptionResolver handlerExceptionResolver : this.handlerExceptionResolvers) {
			exMv = handlerExceptionResolver.resolveException(request, response, handler, ex);
			if (exMv != null) {
				break;
			}
		}
		if (exMv != null) {
			if (exMv.isEmpty()) {
				request.setAttribute(EXCEPTION_ATTRIBUTE, ex);
				return null;
			}
			// We might still need view name translation for a plain error model...
			if (!exMv.hasView()) {
				exMv.setViewName(getDefaultViewName(request));
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Handler execution resulted in exception - forwarding to resolved error view: " + exMv, ex);
			}
			WebUtils.exposeErrorRequestAttributes(request, ex, getServletName());
			return exMv;
		}

		throw ex;
	}

	/**
	 * Render the given ModelAndView.
	 * <p>This is the last stage in handling a request. It may involve resolving the view by name.
	 * <p>
	 * 渲染给定的ModelAndView <p>这是处理请求的最后阶段它可能涉及按名称解析视图
	 * 
	 * 
	 * @param mv the ModelAndView to render
	 * @param request current HTTP servlet request
	 * @param response current HTTP servlet response
	 * @throws ServletException if view is missing or cannot be resolved
	 * @throws Exception if there's a problem rendering the view
	 */
	protected void render(ModelAndView mv, HttpServletRequest request, HttpServletResponse response) throws Exception {
		// Determine locale for request and apply it to the response.
		Locale locale = this.localeResolver.resolveLocale(request);
		response.setLocale(locale);

		View view;
		if (mv.isReference()) {
			// We need to resolve the view name.
			view = resolveViewName(mv.getViewName(), mv.getModelInternal(), locale, request);
			if (view == null) {
				throw new ServletException("Could not resolve view with name '" + mv.getViewName() +
						"' in servlet with name '" + getServletName() + "'");
			}
		}
		else {
			// No need to lookup: the ModelAndView object contains the actual View object.
			view = mv.getView();
			if (view == null) {
				throw new ServletException("ModelAndView [" + mv + "] neither contains a view name nor a " +
						"View object in servlet with name '" + getServletName() + "'");
			}
		}

		// Delegate to the View object for rendering.
		if (logger.isDebugEnabled()) {
			logger.debug("Rendering view [" + view + "] in DispatcherServlet with name '" + getServletName() + "'");
		}
		try {
			if (mv.getStatus() != null) {
				response.setStatus(mv.getStatus().value());
			}
			view.render(mv.getModelInternal(), request, response);
		}
		catch (Exception ex) {
			if (logger.isDebugEnabled()) {
				logger.debug("Error rendering view [" + view + "] in DispatcherServlet with name '" +
						getServletName() + "'", ex);
			}
			throw ex;
		}
	}

	/**
	 * Translate the supplied request into a default view name.
	 * <p>
	 *  将提供的请求转换为默认视图名称
	 * 
	 * 
	 * @param request current HTTP servlet request
	 * @return the view name (or {@code null} if no default found)
	 * @throws Exception if view name translation failed
	 */
	protected String getDefaultViewName(HttpServletRequest request) throws Exception {
		return this.viewNameTranslator.getViewName(request);
	}

	/**
	 * Resolve the given view name into a View object (to be rendered).
	 * <p>The default implementations asks all ViewResolvers of this dispatcher.
	 * Can be overridden for custom resolution strategies, potentially based on
	 * specific model attributes or request parameters.
	 * <p>
	 *  将给定的视图名称解析为View对象(要呈现)<p>默认实现请求此调度程序的所有ViewResolvers可以根据具体的模型属性或请求参数自定义分辨率策略进行覆盖
	 * 
	 * 
	 * @param viewName the name of the view to resolve
	 * @param model the model to be passed to the view
	 * @param locale the current locale
	 * @param request current HTTP servlet request
	 * @return the View object, or {@code null} if none found
	 * @throws Exception if the view cannot be resolved
	 * (typically in case of problems creating an actual View object)
	 * @see ViewResolver#resolveViewName
	 */
	protected View resolveViewName(String viewName, Map<String, Object> model, Locale locale,
			HttpServletRequest request) throws Exception {

		for (ViewResolver viewResolver : this.viewResolvers) {
			View view = viewResolver.resolveViewName(viewName, locale);
			if (view != null) {
				return view;
			}
		}
		return null;
	}

	private void triggerAfterCompletion(HttpServletRequest request, HttpServletResponse response,
			HandlerExecutionChain mappedHandler, Exception ex) throws Exception {

		if (mappedHandler != null) {
			mappedHandler.triggerAfterCompletion(request, response, ex);
		}
		throw ex;
	}

	/**
	 * Restore the request attributes after an include.
	 * <p>
	 *  在包含后恢复请求属性
	 * 
	 * @param request current HTTP request
	 * @param attributesSnapshot the snapshot of the request attributes before the include
	 */
	@SuppressWarnings("unchecked")
	private void restoreAttributesAfterInclude(HttpServletRequest request, Map<?,?> attributesSnapshot) {
		// Need to copy into separate Collection here, to avoid side effects
		// on the Enumeration when removing attributes.
		Set<String> attrsToCheck = new HashSet<String>();
		Enumeration<?> attrNames = request.getAttributeNames();
		while (attrNames.hasMoreElements()) {
			String attrName = (String) attrNames.nextElement();
			if (this.cleanupAfterInclude || attrName.startsWith("org.springframework.web.servlet")) {
				attrsToCheck.add(attrName);
			}
		}

		// Add attributes that may have been removed
		attrsToCheck.addAll((Set<String>) attributesSnapshot.keySet());

		// Iterate over the attributes to check, restoring the original value
		// or removing the attribute, respectively, if appropriate.
		for (String attrName : attrsToCheck) {
			Object attrValue = attributesSnapshot.get(attrName);
			if (attrValue == null){
				request.removeAttribute(attrName);
			}
			else if (attrValue != request.getAttribute(attrName)) {
				request.setAttribute(attrName, attrValue);
			}
		}
	}

	private static String getRequestUri(HttpServletRequest request) {
		String uri = (String) request.getAttribute(WebUtils.INCLUDE_REQUEST_URI_ATTRIBUTE);
		if (uri == null) {
			uri = request.getRequestURI();
		}
		return uri;
	}

}
