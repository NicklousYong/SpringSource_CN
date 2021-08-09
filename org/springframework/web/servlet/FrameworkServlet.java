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
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.SourceFilteringListener;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.HttpMethod;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ConfigurableWebEnvironment;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.async.CallableProcessingInterceptorAdapter;
import org.springframework.web.context.request.async.WebAsyncManager;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.context.support.ServletRequestHandledEvent;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.util.NestedServletException;
import org.springframework.web.util.WebUtils;

/**
 * Base servlet for Spring's web framework. Provides integration with
 * a Spring application context, in a JavaBean-based overall solution.
 *
 * <p>This class offers the following functionality:
 * <ul>
 * <li>Manages a {@link org.springframework.web.context.WebApplicationContext
 * WebApplicationContext} instance per servlet. The servlet's configuration is determined
 * by beans in the servlet's namespace.
 * <li>Publishes events on request processing, whether or not a request is
 * successfully handled.
 * </ul>
 *
 * <p>Subclasses must implement {@link #doService} to handle requests. Because this extends
 * {@link HttpServletBean} rather than HttpServlet directly, bean properties are
 * automatically mapped onto it. Subclasses can override {@link #initFrameworkServlet()}
 * for custom initialization.
 *
 * <p>Detects a "contextClass" parameter at the servlet init-param level,
 * falling back to the default context class,
 * {@link org.springframework.web.context.support.XmlWebApplicationContext
 * XmlWebApplicationContext}, if not found. Note that, with the default
 * {@code FrameworkServlet}, a custom context class needs to implement the
 * {@link org.springframework.web.context.ConfigurableWebApplicationContext
 * ConfigurableWebApplicationContext} SPI.
 *
 * <p>Accepts an optional "contextInitializerClasses" servlet init-param that
 * specifies one or more {@link org.springframework.context.ApplicationContextInitializer
 * ApplicationContextInitializer} classes. The managed web application context will be
 * delegated to these initializers, allowing for additional programmatic configuration,
 * e.g. adding property sources or activating profiles against the {@linkplain
 * org.springframework.context.ConfigurableApplicationContext#getEnvironment() context's
 * environment}. See also {@link org.springframework.web.context.ContextLoader} which
 * supports a "contextInitializerClasses" context-param with identical semantics for
 * the "root" web application context.
 *
 * <p>Passes a "contextConfigLocation" servlet init-param to the context instance,
 * parsing it into potentially multiple file paths which can be separated by any
 * number of commas and spaces, like "test-servlet.xml, myServlet.xml".
 * If not explicitly specified, the context implementation is supposed to build a
 * default location from the namespace of the servlet.
 *
 * <p>Note: In case of multiple config locations, later bean definitions will
 * override ones defined in earlier loaded files, at least when using Spring's
 * default ApplicationContext implementation. This can be leveraged to
 * deliberately override certain bean definitions via an extra XML file.
 *
 * <p>The default namespace is "'servlet-name'-servlet", e.g. "test-servlet" for a
 * servlet-name "test" (leading to a "/WEB-INF/test-servlet.xml" default location
 * with XmlWebApplicationContext). The namespace can also be set explicitly via
 * the "namespace" servlet init-param.
 *
 * <p>As of Spring 3.1, {@code FrameworkServlet} may now be injected with a web
 * application context, rather than creating its own internally. This is useful in Servlet
 * 3.0+ environments, which support programmatic registration of servlet instances. See
 * {@link #FrameworkServlet(WebApplicationContext)} Javadoc for details.
 *
 * <p>
 *  Spring的Web框架的基本servlet在基于JavaBean的整体解决方案中提供与Spring应用程序上下文的集成
 * 
 *  <p>此类提供以下功能：
 * <ul>
 * 管理每个servlet的{@link orgspringframeworkwebcontextWebApplicationContext WebApplicationContext}实例servlet的
 * 配置由servlet的命名空间中的bean决定。
 * <li>无论请求是否成功处理,都会发出请求处理中的事件。
 * </ul>
 * 
 *  <p>子类必须实现{@link #doService}来处理请求因为这直接扩展了{@link HttpServletBean}而不是HttpServlet,bean属性会自动映射到它上面子类可以覆盖{@link #initFrameworkServlet()}
 * 来进行自定义初始化。
 * 
 * <p>在servlet init-param级别检测"contextClass"参数,返回默认上下文类{@link orgspringframeworkwebcontextsupportXmlWebApplicationContext XmlWebApplicationContext}
 * (如果未找到)请注意,使用默认的{@code FrameworkServlet},自定义上下文类需要实现{@link orgspringframeworkwebcontextConfigurableWebApplicationContext ConfigurableWebApplicationContext}
 *  SPI。
 * 
 * 接受可选的"contextInitializerClasses"servlet init-param,它指定一个或多个{@link orgspringframeworkcontextApplicationContextInitializer ApplicationContextInitializer}
 * 类。
 * 托管Web应用程序上下文将被委派给这些初始化程序,允许进行其他编程配置,例如添加属性源或激活配置文件反对{@linkplain orgspringframeworkcontextConfigurableApplicationContext#getEnvironment()上下文的环境}
 * 另请参见{@link orgspringframeworkwebcontextContextLoader},它支持"contextInitializerClasses"上下文参数,具有与"root"We
 * b应用程序上下文相同的语义。
 * 
 * 将"contextConfigLocation"servlet init-param传递给上下文实例,将其解析为可能由多个逗号和空格分隔的多个文件路径,如"test-servletxml,myServl
 * etxml"。
 * 如果未明确指定,上下文实现应该从servlet的命名空间构建一个默认位置。
 * 
 *  注意：在多个配置位置的情况下,稍后的bean定义将覆盖早先加载的文件中定义的定义,至少在使用Spring的默认ApplicationContext实现时可以利用此功能,通过额外的XML文件故意覆盖某些
 * bean定义。
 * 
 * <p>默认命名空间是"servlet-name"-servlet",例如servlet-name"test"的"test-servlet"(导致使用XmlWebApplicationContext的"/
 *  WEB-INF / test-servletxml"默认位置)命名空间也可以通过"namespace"servlet init-param来显式设置。
 * 
 *  <p>截至春季31,{@code FrameworkServlet}现在可以注入一个Web应用程序上下文,而不是内部创建它在Servlet 30+环境中很有用,这些环境支持servlet实例的编程注册
 * 参见{@link# FrameworkServlet(WebApplicationContext)} Javadoc的详细信息。
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Chris Beams
 * @author Rossen Stoyanchev
 * @author Phillip Webb
 * @see #doService
 * @see #setContextClass
 * @see #setContextConfigLocation
 * @see #setContextInitializerClasses
 * @see #setNamespace
 */
@SuppressWarnings("serial")
public abstract class FrameworkServlet extends HttpServletBean implements ApplicationContextAware {

	/**
	 * Suffix for WebApplicationContext namespaces. If a servlet of this class is
	 * given the name "test" in a context, the namespace used by the servlet will
	 * resolve to "test-servlet".
	 * <p>
	 *  WebApplicationContext命名空间的后缀如果在上下文中给这个类的servlet命名为"test",则servlet使用的命名空间将解析为"test-servlet"
	 * 
	 */
	public static final String DEFAULT_NAMESPACE_SUFFIX = "-servlet";

	/**
	 * Default context class for FrameworkServlet.
	 * <p>
	 * FrameworkServlet的默认上下文类
	 * 
	 * 
	 * @see org.springframework.web.context.support.XmlWebApplicationContext
	 */
	public static final Class<?> DEFAULT_CONTEXT_CLASS = XmlWebApplicationContext.class;

	/**
	 * Prefix for the ServletContext attribute for the WebApplicationContext.
	 * The completion is the servlet name.
	 * <p>
	 *  WebApplicationContext的ServletContext属性的前缀完成是servlet名称
	 * 
	 */
	public static final String SERVLET_CONTEXT_PREFIX = FrameworkServlet.class.getName() + ".CONTEXT.";

	/**
	 * Any number of these characters are considered delimiters between
	 * multiple values in a single init-param String value.
	 * <p>
	 *  在单个init-param String值中,任意数量的这些字符都被认为是多个值之间的分隔符
	 * 
	 */
	private static final String INIT_PARAM_DELIMITERS = ",; \t\n";


	/** Checking for Servlet 3.0+ HttpServletResponse.getStatus() */
	private static final boolean responseGetStatusAvailable =
			ClassUtils.hasMethod(HttpServletResponse.class, "getStatus");


	/** ServletContext attribute to find the WebApplicationContext in */
	private String contextAttribute;

	/** WebApplicationContext implementation class to create */
	private Class<?> contextClass = DEFAULT_CONTEXT_CLASS;

	/** WebApplicationContext id to assign */
	private String contextId;

	/** Namespace for this servlet */
	private String namespace;

	/** Explicit context config location */
	private String contextConfigLocation;

	/** Actual ApplicationContextInitializer instances to apply to the context */
	private final List<ApplicationContextInitializer<ConfigurableApplicationContext>> contextInitializers =
			new ArrayList<ApplicationContextInitializer<ConfigurableApplicationContext>>();

	/** Comma-delimited ApplicationContextInitializer class names set through init param */
	private String contextInitializerClasses;

	/** Should we publish the context as a ServletContext attribute? */
	private boolean publishContext = true;

	/** Should we publish a ServletRequestHandledEvent at the end of each request? */
	private boolean publishEvents = true;

	/** Expose LocaleContext and RequestAttributes as inheritable for child threads? */
	private boolean threadContextInheritable = false;

	/** Should we dispatch an HTTP OPTIONS request to {@link #doService}? */
	private boolean dispatchOptionsRequest = false;

	/** Should we dispatch an HTTP TRACE request to {@link #doService}? */
	private boolean dispatchTraceRequest = false;

	/** WebApplicationContext for this servlet */
	private WebApplicationContext webApplicationContext;

	/** If the WebApplicationContext was injected via {@link #setApplicationContext} */
	private boolean webApplicationContextInjected = false;

	/** Flag used to detect whether onRefresh has already been called */
	private boolean refreshEventReceived = false;


	/**
	 * Create a new {@code FrameworkServlet} that will create its own internal web
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
	 * indicates which {@link ApplicationContextInitializer} classes should be used to
	 * further configure the internal application context prior to refresh().
	 * <p>
	 * 创建一个新的{@code FrameworkServlet},它将基于通过servlet init-params提供的默认值和值创建自己的内部Web应用程序上下文通常用于Servlet 25或更早版本的
	 * 环境中,其中servlet注册的唯一选项是通过{@code webxml }需要使用无参数构造函数调用{@link #setContextConfigLocation}(init-param'conte
	 * xtConfigLocation)将决定哪些XML文件将由{@linkplain #DEFAULT_CONTEXT_CLASS默认XmlWebApplicationContext}加载到<p>调用{@link #setContextClass}
	 * (init-param'contextClass')将覆盖默认的{@code XmlWebApplicationContext},并允许指定一个替代类,例如{@code AnnotationConfigWebApplicationContext}
	 * <p>调用{@link #setContextInitializerClasses}(init-param'contextInitializerClasses')指示在刷新之前应使用哪些{@link ApplicationContextInitializer}
	 * 类进一步配置内部应用程序上下文。
	 * 
	 * 
	 * @see #FrameworkServlet(WebApplicationContext)
	 */
	public FrameworkServlet() {
	}

	/**
	 * Create a new {@code FrameworkServlet} with the given web application context. This
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
	 * ConfigurableApplicationContext#refresh() refreshed}. If it (a) is an implementation
	 * of {@link ConfigurableWebApplicationContext} and (b) has <strong>not</strong>
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
	 * <li>Any {@link ApplicationContextInitializer}s specified through the
	 * "contextInitializerClasses" init-param or through the {@link
	 * #setContextInitializers} property will be applied.</li>
	 * <li>{@link ConfigurableApplicationContext#refresh refresh()} will be called</li>
	 * </ul>
	 * If the context has already been refreshed or does not implement
	 * {@code ConfigurableWebApplicationContext}, none of the above will occur under the
	 * assumption that the user has performed these actions (or not) per his or her
	 * specific needs.
	 * <p>See {@link org.springframework.web.WebApplicationInitializer} for usage examples.
	 * <p>
	 * 使用给定的Web应用程序上下文创建一个新的{@code FrameworkServlet}该构造函数在Servlet 30+环境中很有用,通过{@link ServletContext#addServlet}
	 *  API可以实现基于实例的servlet注册<p>使用此构造函数表示将忽略以下属性/ init-params：。
	 * <ul>
	 *  <li> {@ link #setContextClass(Class)} /'contextClass'</li> <li> {@ link #setContextConfigLocation(String)}
	 *  /'contextConfigLocation'</li> <li> {@ link #setContextAttribute )} /'contextAttribute'</li> <li> {@ link #setNamespace(String)}
	 *  /'namespace'</li>。
	 * </ul>
	 * 如果(a)是{@link ConfigurableWebApplicationContext}的实现,而(b)具有<strong>不</strong>,那么给定的Web应用程序上下文可能是也可能还不是{@linkplain ConfigurableApplicationContext#refresh() >已经刷新(推荐的方法),则会发生以下情况：。
	 * <ul>
	 * <li>如果给定的上下文没有{@linkplain ConfigurableApplicationContext#setParent parent},则根应用程序上下文将被设置为父</li> <li>如
	 * 果给定的上下文尚未分配{@ linkplain ConfigurableApplicationContext#setId id},将分配一个</li> <li> {@ code ServletContext}
	 * 和{@code ServletConfig}对象将被委派给应用程序上下文</li> <li> {@ link# postProcessWebApplicationContext}将被调用</li> <li>
	 * 通过"contextInitializerClasses"init-param或通过{@link #setContextInitializers}属性指定的任何{@link ApplicationContextInitializer}
	 * 将被应用</li> <li> {@链接ConfigurableApplicationContext#refresh refresh()}将被调用</li>。
	 * </ul>
	 * 如果上下文已经被刷新或者没有实现{@code ConfigurableWebApplicationContext},那么在假设用户根据他或她的具体需求执行这些操作(或不)的情况下,不会出现上述情况<p>
	 * 请参见{@link orgspringframeworkwebWebApplicationInitializer}用于使用示例。
	 * 
	 * 
	 * @param webApplicationContext the context to use
	 * @see #initWebApplicationContext
	 * @see #configureAndRefreshWebApplicationContext
	 * @see org.springframework.web.WebApplicationInitializer
	 */
	public FrameworkServlet(WebApplicationContext webApplicationContext) {
		this.webApplicationContext = webApplicationContext;
	}


	/**
	 * Set the name of the ServletContext attribute which should be used to retrieve the
	 * {@link WebApplicationContext} that this servlet is supposed to use.
	 * <p>
	 *  设置ServletContext属性的名称,该属性应该用于检索该servlet应该使用的{@link WebApplicationContext}
	 * 
	 */
	public void setContextAttribute(String contextAttribute) {
		this.contextAttribute = contextAttribute;
	}

	/**
	 * Return the name of the ServletContext attribute which should be used to retrieve the
	 * {@link WebApplicationContext} that this servlet is supposed to use.
	 * <p>
	 *  返回ServletContext属性的名称,该属性应用于检索该servlet应该使用的{@link WebApplicationContext}
	 * 
	 */
	public String getContextAttribute() {
		return this.contextAttribute;
	}

	/**
	 * Set a custom context class. This class must be of type
	 * {@link org.springframework.web.context.WebApplicationContext}.
	 * <p>When using the default FrameworkServlet implementation,
	 * the context class must also implement the
	 * {@link org.springframework.web.context.ConfigurableWebApplicationContext}
	 * interface.
	 * <p>
	 * 设置自定义上下文类此类必须是类型{@link orgspringframeworkwebcontextWebApplicationContext} <p>当使用默认的FrameworkServlet实现
	 * 时,上下文类还必须实现{@link orgspringframeworkwebcontextConfigurableWebApplicationContext}接口。
	 * 
	 * 
	 * @see #createWebApplicationContext
	 */
	public void setContextClass(Class<?> contextClass) {
		this.contextClass = contextClass;
	}

	/**
	 * Return the custom context class.
	 * <p>
	 *  返回自定义上下文类
	 * 
	 */
	public Class<?> getContextClass() {
		return this.contextClass;
	}

	/**
	 * Specify a custom WebApplicationContext id,
	 * to be used as serialization id for the underlying BeanFactory.
	 * <p>
	 *  指定一个自定义WebApplicationContext id,用作底层BeanFactory的序列化ID
	 * 
	 */
	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

	/**
	 * Return the custom WebApplicationContext id, if any.
	 * <p>
	 *  返回自定义WebApplicationContext id,如果有的话
	 * 
	 */
	public String getContextId() {
		return this.contextId;
	}

	/**
	 * Set a custom namespace for this servlet,
	 * to be used for building a default context config location.
	 * <p>
	 *  为此servlet设置自定义命名空间,用于构建默认上下文配置位置
	 * 
	 */
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	/**
	 * Return the namespace for this servlet, falling back to default scheme if
	 * no custom namespace was set: e.g. "test-servlet" for a servlet named "test".
	 * <p>
	 *  返回此Servlet的命名空间,如果没有设置自定义命名空间,则返回默认方案：例如,名为"test"的servlet的"test-servlet"
	 * 
	 */
	public String getNamespace() {
		return (this.namespace != null ? this.namespace : getServletName() + DEFAULT_NAMESPACE_SUFFIX);
	}

	/**
	 * Set the context config location explicitly, instead of relying on the default
	 * location built from the namespace. This location string can consist of
	 * multiple locations separated by any number of commas and spaces.
	 * <p>
	 * 明确设置上下文配置位置,而不是依赖于从命名空间构建的默认位置。此位置字符串可以由多个位置组成,并以任意数量的逗号和空格分隔
	 * 
	 */
	public void setContextConfigLocation(String contextConfigLocation) {
		this.contextConfigLocation = contextConfigLocation;
	}

	/**
	 * Return the explicit context config location, if any.
	 * <p>
	 *  返回显式上下文配置位置,如果有的话
	 * 
	 */
	public String getContextConfigLocation() {
		return this.contextConfigLocation;
	}

	/**
	 * Specify which {@link ApplicationContextInitializer} instances should be used
	 * to initialize the application context used by this {@code FrameworkServlet}.
	 * <p>
	 *  指定哪些{@link ApplicationContextInitializer}实例应用于初始化此{@code FrameworkServlet}使用的应用程序上下文
	 * 
	 * 
	 * @see #configureAndRefreshWebApplicationContext
	 * @see #applyInitializers
	 */
	@SuppressWarnings("unchecked")
	public void setContextInitializers(ApplicationContextInitializer<?>... initializers) {
		if (initializers != null) {
			for (ApplicationContextInitializer<?> initializer : initializers) {
				this.contextInitializers.add((ApplicationContextInitializer<ConfigurableApplicationContext>) initializer);
			}
		}
	}

	/**
	 * Specify the set of fully-qualified {@link ApplicationContextInitializer} class
	 * names, per the optional "contextInitializerClasses" servlet init-param.
	 * <p>
	 *  指定一组完全限定的{@link ApplicationContextInitializer}类名称,根据可选的"contextInitializerClasses"servlet init-param
	 * 。
	 * 
	 * 
	 * @see #configureAndRefreshWebApplicationContext(ConfigurableWebApplicationContext)
	 * @see #applyInitializers(ConfigurableApplicationContext)
	 */
	public void setContextInitializerClasses(String contextInitializerClasses) {
		this.contextInitializerClasses = contextInitializerClasses;
	}

	/**
	 * Set whether to publish this servlet's context as a ServletContext attribute,
	 * available to all objects in the web container. Default is "true".
	 * <p>This is especially handy during testing, although it is debatable whether
	 * it's good practice to let other application objects access the context this way.
	 * <p>
	 * 设置是否将Servlet上下文发布为ServletContext属性,可用于Web容器中的所有对象默认为"true"<p>这在测试期间特别方便,尽管有争议的是让其他应用程序对象访问这样的上下文
	 * 
	 */
	public void setPublishContext(boolean publishContext) {
		this.publishContext = publishContext;
	}

	/**
	 * Set whether this servlet should publish a ServletRequestHandledEvent at the end
	 * of each request. Default is "true"; can be turned off for a slight performance
	 * improvement, provided that no ApplicationListeners rely on such events.
	 * <p>
	 *  设置这个servlet是否应该在每个请求结束时发布一个ServletRequestHandledEvent默认是"true";可以关闭轻微的性能改进,前提是没有ApplicationListeners
	 * 依赖这种事件。
	 * 
	 * 
	 * @see org.springframework.web.context.support.ServletRequestHandledEvent
	 */
	public void setPublishEvents(boolean publishEvents) {
		this.publishEvents = publishEvents;
	}

	/**
	 * Set whether to expose the LocaleContext and RequestAttributes as inheritable
	 * for child threads (using an {@link java.lang.InheritableThreadLocal}).
	 * <p>Default is "false", to avoid side effects on spawned background threads.
	 * Switch this to "true" to enable inheritance for custom child threads which
	 * are spawned during request processing and only used for this request
	 * (that is, ending after their initial task, without reuse of the thread).
	 * <p><b>WARNING:</b> Do not use inheritance for child threads if you are
	 * accessing a thread pool which is configured to potentially add new threads
	 * on demand (e.g. a JDK {@link java.util.concurrent.ThreadPoolExecutor}),
	 * since this will expose the inherited context to such a pooled thread.
	 * <p>
	 * 设置是否将LocaleContext和RequestAttributes公开为子线程可继承(使用{@link javalangInheritableThreadLocal})<p>默认为"false",
	 * 以避免对生成的后台线程的副作用将其切换为"true"以启用自定义继承在请求处理期间产生的子线程,仅用于此请求(即,在其初始任务结束后,不重新使用线程)<p> <b>警告：</b>不要对子线程使用继承,如
	 * 果您正在访问一个线程池,该池被配置为可以根据需要添加新线程(例如JDK {@link javautilconcurrentThreadPoolExecutor}),因为这会将继承的上下文暴露于这样一个合
	 * 并的线程。
	 * 
	 */
	public void setThreadContextInheritable(boolean threadContextInheritable) {
		this.threadContextInheritable = threadContextInheritable;
	}

	/**
	 * Set whether this servlet should dispatch an HTTP OPTIONS request to
	 * the {@link #doService} method.
	 * <p>Default in the {@code FrameworkServlet} is "false", applying
	 * {@link javax.servlet.http.HttpServlet}'s default behavior (i.e.enumerating
	 * all standard HTTP request methods as a response to the OPTIONS request).
	 * Note however that as of 4.3 the {@code DispatcherServlet} sets this
	 * property to "true" by default due to its built-in support for OPTIONS.
	 * <p>Turn this flag on if you prefer OPTIONS requests to go through the
	 * regular dispatching chain, just like other HTTP requests. This usually
	 * means that your controllers will receive those requests; make sure
	 * that those endpoints are actually able to handle an OPTIONS request.
	 * <p>Note that HttpServlet's default OPTIONS processing will be applied
	 * in any case if your controllers happen to not set the 'Allow' header
	 * (as required for an OPTIONS response).
	 * <p>
	 * 设置此servlet是否应该向{@link #doService}方法发送HTTP OPTIONS请求<p> {@code FrameworkServlet}中的默认值为"false",应用{@link javaxservlethttpHttpServlet}
	 * 的默认行为(ieenumerating all standard HTTP请求方法作为对OPTIONS请求的响应)请注意,从43开始,{@code DispatcherServlet}由于内置对OPTI
	 * ONS的支持,默认将此属性设置为"true"。
	 *  OPTIONS请求通过正常的调度链,就像其他HTTP请求一样通常意味着您的控制器将收到这些请求;确保这些端点实际上能够处理OPTIONS请求注意,如果您的控制器发生不设置"允许"标头(根据OPTION
	 * S响应的要求),HttpServlet的默认OPTIONS处理将适用于任何情况,。
	 * 
	 */
	public void setDispatchOptionsRequest(boolean dispatchOptionsRequest) {
		this.dispatchOptionsRequest = dispatchOptionsRequest;
	}

	/**
	 * Set whether this servlet should dispatch an HTTP TRACE request to
	 * the {@link #doService} method.
	 * <p>Default is "false", applying {@link javax.servlet.http.HttpServlet}'s
	 * default behavior (i.e. reflecting the message received back to the client).
	 * <p>Turn this flag on if you prefer TRACE requests to go through the
	 * regular dispatching chain, just like other HTTP requests. This usually
	 * means that your controllers will receive those requests; make sure
	 * that those endpoints are actually able to handle a TRACE request.
	 * <p>Note that HttpServlet's default TRACE processing will be applied
	 * in any case if your controllers happen to not generate a response
	 * of content type 'message/http' (as required for a TRACE response).
	 * <p>
	 * 设置这个servlet是否应该向{@link #doService}方法发送HTTP TRACE请求<p>默认为"false",应用{@link javaxservlethttpHttpServlet}
	 * 的默认行为(即反映收到的消息回到客户端)如果您喜欢TRACE请求通过正常调度链,就像打开此标志一样,就像其他HTTP请求一样通常意味着您的控制器将收到这些请求;请确保这些端点实际上能够处理TRACE请求
	 * <p>请注意,HttpServlet的默认TRACE处理将在任何情况下应用,如果您的控制器发生不生成内容类型"message / http"的响应(根据需要TRACE回应)。
	 * 
	 */
	public void setDispatchTraceRequest(boolean dispatchTraceRequest) {
		this.dispatchTraceRequest = dispatchTraceRequest;
	}

	/**
	 * Called by Spring via {@link ApplicationContextAware} to inject the current
	 * application context. This method allows FrameworkServlets to be registered as
	 * Spring beans inside an existing {@link WebApplicationContext} rather than
	 * {@link #findWebApplicationContext() finding} a
	 * {@link org.springframework.web.context.ContextLoaderListener bootstrapped} context.
	 * <p>Primarily added to support use in embedded servlet containers.
	 * <p>
	 * 由Spring通过{@link ApplicationContextAware}调用以注入当前应用程序上下文此方法允许FrameworkServlets在现有的{@link WebApplicationContext}
	 * 中注册为Spring Bean,而不是{@link #findWebApplicationContext()查找} {@link orgspringframeworkwebcontextContextLoaderListener bootstrapped }
	 *  context <p>主要添加以支持在嵌入式servlet容器中使用。
	 * 
	 * 
	 * @since 4.0
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		if (this.webApplicationContext == null && applicationContext instanceof WebApplicationContext) {
			this.webApplicationContext = (WebApplicationContext) applicationContext;
			this.webApplicationContextInjected = true;
		}
	}


	/**
	 * Overridden method of {@link HttpServletBean}, invoked after any bean properties
	 * have been set. Creates this servlet's WebApplicationContext.
	 * <p>
	 *  已设置任何bean属性后调用的{@link HttpServletBean}的重写方法创建此servlet的WebApplicationContext
	 * 
	 */
	@Override
	protected final void initServletBean() throws ServletException {
		getServletContext().log("Initializing Spring FrameworkServlet '" + getServletName() + "'");
		if (this.logger.isInfoEnabled()) {
			this.logger.info("FrameworkServlet '" + getServletName() + "': initialization started");
		}
		long startTime = System.currentTimeMillis();

		try {
			this.webApplicationContext = initWebApplicationContext();
			initFrameworkServlet();
		}
		catch (ServletException ex) {
			this.logger.error("Context initialization failed", ex);
			throw ex;
		}
		catch (RuntimeException ex) {
			this.logger.error("Context initialization failed", ex);
			throw ex;
		}

		if (this.logger.isInfoEnabled()) {
			long elapsedTime = System.currentTimeMillis() - startTime;
			this.logger.info("FrameworkServlet '" + getServletName() + "': initialization completed in " +
					elapsedTime + " ms");
		}
	}

	/**
	 * Initialize and publish the WebApplicationContext for this servlet.
	 * <p>Delegates to {@link #createWebApplicationContext} for actual creation
	 * of the context. Can be overridden in subclasses.
	 * <p>
	 *  初始化并发布此servlet的WebApplicationContext <p>为实际创建上下文的{@link #createWebApplicationContext}的代理可以在子类中覆盖
	 * 
	 * 
	 * @return the WebApplicationContext instance
	 * @see #FrameworkServlet(WebApplicationContext)
	 * @see #setContextClass
	 * @see #setContextConfigLocation
	 */
	protected WebApplicationContext initWebApplicationContext() {
		WebApplicationContext rootContext =
				WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		WebApplicationContext wac = null;

		if (this.webApplicationContext != null) {
			// A context instance was injected at construction time -> use it
			wac = this.webApplicationContext;
			if (wac instanceof ConfigurableWebApplicationContext) {
				ConfigurableWebApplicationContext cwac = (ConfigurableWebApplicationContext) wac;
				if (!cwac.isActive()) {
					// The context has not yet been refreshed -> provide services such as
					// setting the parent context, setting the application context id, etc
					if (cwac.getParent() == null) {
						// The context instance was injected without an explicit parent -> set
						// the root application context (if any; may be null) as the parent
						cwac.setParent(rootContext);
					}
					configureAndRefreshWebApplicationContext(cwac);
				}
			}
		}
		if (wac == null) {
			// No context instance was injected at construction time -> see if one
			// has been registered in the servlet context. If one exists, it is assumed
			// that the parent context (if any) has already been set and that the
			// user has performed any initialization such as setting the context id
			wac = findWebApplicationContext();
		}
		if (wac == null) {
			// No context instance is defined for this servlet -> create a local one
			wac = createWebApplicationContext(rootContext);
		}

		if (!this.refreshEventReceived) {
			// Either the context is not a ConfigurableApplicationContext with refresh
			// support or the context injected at construction time had already been
			// refreshed -> trigger initial onRefresh manually here.
			onRefresh(wac);
		}

		if (this.publishContext) {
			// Publish the context as a servlet context attribute.
			String attrName = getServletContextAttributeName();
			getServletContext().setAttribute(attrName, wac);
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Published WebApplicationContext of servlet '" + getServletName() +
						"' as ServletContext attribute with name [" + attrName + "]");
			}
		}

		return wac;
	}

	/**
	 * Retrieve a {@code WebApplicationContext} from the {@code ServletContext}
	 * attribute with the {@link #setContextAttribute configured name}. The
	 * {@code WebApplicationContext} must have already been loaded and stored in the
	 * {@code ServletContext} before this servlet gets initialized (or invoked).
	 * <p>Subclasses may override this method to provide a different
	 * {@code WebApplicationContext} retrieval strategy.
	 * <p>
	 * 使用{@link #setContextAttribute配置的名称}从{@code ServletContext}属性检索{@code WebApplicationContext} {@code WebApplicationContext}
	 * 必须已经加载并存储在{@code ServletContext}之前,此servlet初始化(或调用)<p>子类可以覆盖此方法以提供不同的{@code WebApplicationContext}检索策
	 * 略。
	 * 
	 * 
	 * @return the WebApplicationContext for this servlet, or {@code null} if not found
	 * @see #getContextAttribute()
	 */
	protected WebApplicationContext findWebApplicationContext() {
		String attrName = getContextAttribute();
		if (attrName == null) {
			return null;
		}
		WebApplicationContext wac =
				WebApplicationContextUtils.getWebApplicationContext(getServletContext(), attrName);
		if (wac == null) {
			throw new IllegalStateException("No WebApplicationContext found: initializer not registered?");
		}
		return wac;
	}

	/**
	 * Instantiate the WebApplicationContext for this servlet, either a default
	 * {@link org.springframework.web.context.support.XmlWebApplicationContext}
	 * or a {@link #setContextClass custom context class}, if set.
	 * <p>This implementation expects custom contexts to implement the
	 * {@link org.springframework.web.context.ConfigurableWebApplicationContext}
	 * interface. Can be overridden in subclasses.
	 * <p>Do not forget to register this servlet instance as application listener on the
	 * created context (for triggering its {@link #onRefresh callback}, and to call
	 * {@link org.springframework.context.ConfigurableApplicationContext#refresh()}
	 * before returning the context instance.
	 * <p>
	 * 实例化此Servlet的WebApplicationContext,默认{@link orgspringframeworkwebcontextsupportXmlWebApplicationContext}
	 * 或{@link #setContextClass自定义上下文类},如果设置为<p>,则此实现期望自定义上下文实现{@link orgspringframeworkwebcontextConfigurableWebApplicationContext}
	 * 接口可以覆盖子类<p>不要忘记在创建的上下文中注册此servlet实例作为应用程序侦听器(用于触发其{@link #onRefresh回调}),并在返回上下文实例之前调用{@link orgspringframeworkcontextConfigurableApplicationContext#refresh()}
	 * 。
	 * 
	 * 
	 * @param parent the parent ApplicationContext to use, or {@code null} if none
	 * @return the WebApplicationContext for this servlet
	 * @see org.springframework.web.context.support.XmlWebApplicationContext
	 */
	protected WebApplicationContext createWebApplicationContext(ApplicationContext parent) {
		Class<?> contextClass = getContextClass();
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Servlet with name '" + getServletName() +
					"' will try to create custom WebApplicationContext context of class '" +
					contextClass.getName() + "'" + ", using parent context [" + parent + "]");
		}
		if (!ConfigurableWebApplicationContext.class.isAssignableFrom(contextClass)) {
			throw new ApplicationContextException(
					"Fatal initialization error in servlet with name '" + getServletName() +
					"': custom WebApplicationContext class [" + contextClass.getName() +
					"] is not of type ConfigurableWebApplicationContext");
		}
		ConfigurableWebApplicationContext wac =
				(ConfigurableWebApplicationContext) BeanUtils.instantiateClass(contextClass);

		wac.setEnvironment(getEnvironment());
		wac.setParent(parent);
		wac.setConfigLocation(getContextConfigLocation());

		configureAndRefreshWebApplicationContext(wac);

		return wac;
	}

	protected void configureAndRefreshWebApplicationContext(ConfigurableWebApplicationContext wac) {
		if (ObjectUtils.identityToString(wac).equals(wac.getId())) {
			// The application context id is still set to its original default value
			// -> assign a more useful id based on available information
			if (this.contextId != null) {
				wac.setId(this.contextId);
			}
			else {
				// Generate default id...
				wac.setId(ConfigurableWebApplicationContext.APPLICATION_CONTEXT_ID_PREFIX +
						ObjectUtils.getDisplayString(getServletContext().getContextPath()) + "/" + getServletName());
			}
		}

		wac.setServletContext(getServletContext());
		wac.setServletConfig(getServletConfig());
		wac.setNamespace(getNamespace());
		wac.addApplicationListener(new SourceFilteringListener(wac, new ContextRefreshListener()));

		// The wac environment's #initPropertySources will be called in any case when the context
		// is refreshed; do it eagerly here to ensure servlet property sources are in place for
		// use in any post-processing or initialization that occurs below prior to #refresh
		ConfigurableEnvironment env = wac.getEnvironment();
		if (env instanceof ConfigurableWebEnvironment) {
			((ConfigurableWebEnvironment) env).initPropertySources(getServletContext(), getServletConfig());
		}

		postProcessWebApplicationContext(wac);
		applyInitializers(wac);
		wac.refresh();
	}

	/**
	 * Instantiate the WebApplicationContext for this servlet, either a default
	 * {@link org.springframework.web.context.support.XmlWebApplicationContext}
	 * or a {@link #setContextClass custom context class}, if set.
	 * Delegates to #createWebApplicationContext(ApplicationContext).
	 * <p>
	 * 如果将Delegate设置为#createWebApplicationContext(ApplicationContext),则将此Servlet的WebApplicationContext实例化为默认
	 * {@link orgspringframeworkwebcontextsupportXmlWebApplicationContext}或{@link #setContextClass自定义上下文类}。
	 * 
	 * 
	 * @param parent the parent WebApplicationContext to use, or {@code null} if none
	 * @return the WebApplicationContext for this servlet
	 * @see org.springframework.web.context.support.XmlWebApplicationContext
	 * @see #createWebApplicationContext(ApplicationContext)
	 */
	protected WebApplicationContext createWebApplicationContext(WebApplicationContext parent) {
		return createWebApplicationContext((ApplicationContext) parent);
	}

	/**
	 * Post-process the given WebApplicationContext before it is refreshed
	 * and activated as context for this servlet.
	 * <p>The default implementation is empty. {@code refresh()} will
	 * be called automatically after this method returns.
	 * <p>Note that this method is designed to allow subclasses to modify the application
	 * context, while {@link #initWebApplicationContext} is designed to allow
	 * end-users to modify the context through the use of
	 * {@link ApplicationContextInitializer}s.
	 * <p>
	 *  在给定的WebApplicationContext进行刷新和激活之前,对该servlet进行后处理<p>默认实现为空{@code refresh()}将在此方法返回<p>后自动调用。
	 * 请注意,此方法旨在允许子类修改应用程序上下文,而{@link #initWebApplicationContext}旨在允许最终用户通过使用{@link ApplicationContextInitializer}
	 * 来修改上下文。
	 *  在给定的WebApplicationContext进行刷新和激活之前,对该servlet进行后处理<p>默认实现为空{@code refresh()}将在此方法返回<p>后自动调用。
	 * 
	 * 
	 * @param wac the configured WebApplicationContext (not refreshed yet)
	 * @see #createWebApplicationContext
	 * @see #initWebApplicationContext
	 * @see ConfigurableWebApplicationContext#refresh()
	 */
	protected void postProcessWebApplicationContext(ConfigurableWebApplicationContext wac) {
	}

	/**
	 * Delegate the WebApplicationContext before it is refreshed to any
	 * {@link ApplicationContextInitializer} instances specified by the
	 * "contextInitializerClasses" servlet init-param.
	 * <p>See also {@link #postProcessWebApplicationContext}, which is designed to allow
	 * subclasses (as opposed to end-users) to modify the application context, and is
	 * called immediately before this method.
	 * <p>
	 * 在WebApplicationContext被刷新到由"contextInitializerClasses"指定的任何{@link ApplicationContextInitializer}实例之前,
	 * 委派WebApplicationContext。
	 * init-param <p>另请参见{@link #postProcessWebApplicationContext},该目标旨在允许子类(而不是最终用户) )修改应用程序上下文,并在此方法之前立即调用
	 * 。
	 * 
	 * 
	 * @param wac the configured WebApplicationContext (not refreshed yet)
	 * @see #createWebApplicationContext
	 * @see #postProcessWebApplicationContext
	 * @see ConfigurableApplicationContext#refresh()
	 */
	protected void applyInitializers(ConfigurableApplicationContext wac) {
		String globalClassNames = getServletContext().getInitParameter(ContextLoader.GLOBAL_INITIALIZER_CLASSES_PARAM);
		if (globalClassNames != null) {
			for (String className : StringUtils.tokenizeToStringArray(globalClassNames, INIT_PARAM_DELIMITERS)) {
				this.contextInitializers.add(loadInitializer(className, wac));
			}
		}

		if (this.contextInitializerClasses != null) {
			for (String className : StringUtils.tokenizeToStringArray(this.contextInitializerClasses, INIT_PARAM_DELIMITERS)) {
				this.contextInitializers.add(loadInitializer(className, wac));
			}
		}

		AnnotationAwareOrderComparator.sort(this.contextInitializers);
		for (ApplicationContextInitializer<ConfigurableApplicationContext> initializer : this.contextInitializers) {
			initializer.initialize(wac);
		}
	}

	@SuppressWarnings("unchecked")
	private ApplicationContextInitializer<ConfigurableApplicationContext> loadInitializer(
			String className, ConfigurableApplicationContext wac) {
		try {
			Class<?> initializerClass = ClassUtils.forName(className, wac.getClassLoader());
			Class<?> initializerContextClass =
					GenericTypeResolver.resolveTypeArgument(initializerClass, ApplicationContextInitializer.class);
			if (initializerContextClass != null && !initializerContextClass.isInstance(wac)) {
				throw new ApplicationContextException(String.format(
						"Could not apply context initializer [%s] since its generic parameter [%s] " +
						"is not assignable from the type of application context used by this " +
						"framework servlet: [%s]", initializerClass.getName(), initializerContextClass.getName(),
						wac.getClass().getName()));
			}
			return BeanUtils.instantiateClass(initializerClass, ApplicationContextInitializer.class);
		}
		catch (ClassNotFoundException ex) {
			throw new ApplicationContextException(String.format("Could not load class [%s] specified " +
					"via 'contextInitializerClasses' init-param", className), ex);
		}
	}

	/**
	 * Return the ServletContext attribute name for this servlet's WebApplicationContext.
	 * <p>The default implementation returns
	 * {@code SERVLET_CONTEXT_PREFIX + servlet name}.
	 * <p>
	 *  返回此Servlet的WebApplicationContext的ServletContext属性名称<p>默认实现返回{@code SERVLET_CONTEXT_PREFIX + servlet name}
	 * 。
	 * 
	 * 
	 * @see #SERVLET_CONTEXT_PREFIX
	 * @see #getServletName
	 */
	public String getServletContextAttributeName() {
		return SERVLET_CONTEXT_PREFIX + getServletName();
	}

	/**
	 * Return this servlet's WebApplicationContext.
	 * <p>
	 *  返回这个servlet的WebApplicationContext
	 * 
	 */
	public final WebApplicationContext getWebApplicationContext() {
		return this.webApplicationContext;
	}


	/**
	 * This method will be invoked after any bean properties have been set and
	 * the WebApplicationContext has been loaded. The default implementation is empty;
	 * subclasses may override this method to perform any initialization they require.
	 * <p>
	 * 在设置任何bean属性并加载WebApplicationContext后,将调用此方法。默认实现为空;子类可以覆盖此方法来执行所需的任何初始化
	 * 
	 * 
	 * @throws ServletException in case of an initialization exception
	 */
	protected void initFrameworkServlet() throws ServletException {
	}

	/**
	 * Refresh this servlet's application context, as well as the
	 * dependent state of the servlet.
	 * <p>
	 *  刷新此servlet的应用程序上下文以及servlet的依赖状态
	 * 
	 * 
	 * @see #getWebApplicationContext()
	 * @see org.springframework.context.ConfigurableApplicationContext#refresh()
	 */
	public void refresh() {
		WebApplicationContext wac = getWebApplicationContext();
		if (!(wac instanceof ConfigurableApplicationContext)) {
			throw new IllegalStateException("WebApplicationContext does not support refresh: " + wac);
		}
		((ConfigurableApplicationContext) wac).refresh();
	}

	/**
	 * Callback that receives refresh events from this servlet's WebApplicationContext.
	 * <p>The default implementation calls {@link #onRefresh},
	 * triggering a refresh of this servlet's context-dependent state.
	 * <p>
	 *  从该servlet的WebApplicationContext接收刷新事件的回调<p>默认实现调用{@link #onRefresh},触发刷新此servlet的上下文相关状态
	 * 
	 * 
	 * @param event the incoming ApplicationContext event
	 */
	public void onApplicationEvent(ContextRefreshedEvent event) {
		this.refreshEventReceived = true;
		onRefresh(event.getApplicationContext());
	}

	/**
	 * Template method which can be overridden to add servlet-specific refresh work.
	 * Called after successful context refresh.
	 * <p>This implementation is empty.
	 * <p>
	 *  可以覆盖添加servlet特定刷新工作的模板方法成功上下文刷新后调用<p>此实现为空
	 * 
	 * 
	 * @param context the current WebApplicationContext
	 * @see #refresh()
	 */
	protected void onRefresh(ApplicationContext context) {
		// For subclasses: do nothing by default.
	}

	/**
	 * Close the WebApplicationContext of this servlet.
	 * <p>
	 *  关闭此servlet的WebApplicationContext
	 * 
	 * 
	 * @see org.springframework.context.ConfigurableApplicationContext#close()
	 */
	@Override
	public void destroy() {
		getServletContext().log("Destroying Spring FrameworkServlet '" + getServletName() + "'");
		// Only call close() on WebApplicationContext if locally managed...
		if (this.webApplicationContext instanceof ConfigurableApplicationContext && !this.webApplicationContextInjected) {
			((ConfigurableApplicationContext) this.webApplicationContext).close();
		}
	}


	/**
	 * Override the parent class implementation in order to intercept PATCH requests.
	 * <p>
	 * 覆盖父类实现以拦截PATCH请求
	 * 
	 */
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpMethod httpMethod = HttpMethod.resolve(request.getMethod());
		if (HttpMethod.PATCH == httpMethod || httpMethod == null) {
			processRequest(request, response);
		}
		else {
			super.service(request, response);
		}
	}

	/**
	 * Delegate GET requests to processRequest/doService.
	 * <p>Will also be invoked by HttpServlet's default implementation of {@code doHead},
	 * with a {@code NoBodyResponse} that just captures the content length.
	 * <p>
	 *  代理GET请求到processRequest / doService <p>还将由HttpServlet的{@code doHead}的默认实现调用,{@code NoBodyResponse}只捕获
	 * 内容长度。
	 * 
	 * 
	 * @see #doService
	 * @see #doHead
	 */
	@Override
	protected final void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		processRequest(request, response);
	}

	/**
	 * Delegate POST requests to {@link #processRequest}.
	 * <p>
	 *  将POST请求委派给{@link #processRequest}
	 * 
	 * 
	 * @see #doService
	 */
	@Override
	protected final void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		processRequest(request, response);
	}

	/**
	 * Delegate PUT requests to {@link #processRequest}.
	 * <p>
	 *  将PUT请求委派给{@link #processRequest}
	 * 
	 * 
	 * @see #doService
	 */
	@Override
	protected final void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		processRequest(request, response);
	}

	/**
	 * Delegate DELETE requests to {@link #processRequest}.
	 * <p>
	 *  将DELETE请求委派给{@link #processRequest}
	 * 
	 * 
	 * @see #doService
	 */
	@Override
	protected final void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		processRequest(request, response);
	}

	/**
	 * Delegate OPTIONS requests to {@link #processRequest}, if desired.
	 * <p>Applies HttpServlet's standard OPTIONS processing otherwise,
	 * and also if there is still no 'Allow' header set after dispatching.
	 * <p>
	 *  将OPTIONS请求委托给{@link #processRequest}(如果需要)<p>否则应用HttpServlet的标准OPTIONS处理,并且如果在调度后仍然没有设置"允许"标题
	 * 
	 * 
	 * @see #doService
	 */
	@Override
	protected void doOptions(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		if (this.dispatchOptionsRequest || CorsUtils.isPreFlightRequest(request)) {
			processRequest(request, response);
			if (response.containsHeader("Allow")) {
				// Proper OPTIONS response coming from a handler - we're done.
				return;
			}
		}

		// Use response wrapper for Servlet 2.5 compatibility where
		// the getHeader() method does not exist
		super.doOptions(request, new HttpServletResponseWrapper(response) {
			@Override
			public void setHeader(String name, String value) {
				if ("Allow".equals(name)) {
					value = (StringUtils.hasLength(value) ? value + ", " : "") + HttpMethod.PATCH.name();
				}
				super.setHeader(name, value);
			}
		});
	}

	/**
	 * Delegate TRACE requests to {@link #processRequest}, if desired.
	 * <p>Applies HttpServlet's standard TRACE processing otherwise.
	 * <p>
	 *  将TRACE请求委托给{@link #processRequest}(如果需要)<p>应用HttpServlet的标准TRACE处理
	 * 
	 * 
	 * @see #doService
	 */
	@Override
	protected void doTrace(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		if (this.dispatchTraceRequest) {
			processRequest(request, response);
			if ("message/http".equals(response.getContentType())) {
				// Proper TRACE response coming from a handler - we're done.
				return;
			}
		}
		super.doTrace(request, response);
	}

	/**
	 * Process this request, publishing an event regardless of the outcome.
	 * <p>The actual event handling is performed by the abstract
	 * {@link #doService} template method.
	 * <p>
	 * 处理此请求,发布事件而不考虑结果<p>实际的事件处理由抽象的{@link #doService}模板方法执行
	 * 
	 */
	protected final void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		long startTime = System.currentTimeMillis();
		Throwable failureCause = null;

		LocaleContext previousLocaleContext = LocaleContextHolder.getLocaleContext();
		LocaleContext localeContext = buildLocaleContext(request);

		RequestAttributes previousAttributes = RequestContextHolder.getRequestAttributes();
		ServletRequestAttributes requestAttributes = buildRequestAttributes(request, response, previousAttributes);

		WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);
		asyncManager.registerCallableInterceptor(FrameworkServlet.class.getName(), new RequestBindingInterceptor());

		initContextHolders(request, localeContext, requestAttributes);

		try {
			doService(request, response);
		}
		catch (ServletException ex) {
			failureCause = ex;
			throw ex;
		}
		catch (IOException ex) {
			failureCause = ex;
			throw ex;
		}
		catch (Throwable ex) {
			failureCause = ex;
			throw new NestedServletException("Request processing failed", ex);
		}

		finally {
			resetContextHolders(request, previousLocaleContext, previousAttributes);
			if (requestAttributes != null) {
				requestAttributes.requestCompleted();
			}

			if (logger.isDebugEnabled()) {
				if (failureCause != null) {
					this.logger.debug("Could not complete request", failureCause);
				}
				else {
					if (asyncManager.isConcurrentHandlingStarted()) {
						logger.debug("Leaving response open for concurrent processing");
					}
					else {
						this.logger.debug("Successfully completed request");
					}
				}
			}

			publishRequestHandledEvent(request, response, startTime, failureCause);
		}
	}

	/**
	 * Build a LocaleContext for the given request, exposing the request's
	 * primary locale as current locale.
	 * <p>
	 *  为给定的请求构建LocaleContext,将请求的主区域显示为当前区域
	 * 
	 * 
	 * @param request current HTTP request
	 * @return the corresponding LocaleContext, or {@code null} if none to bind
	 * @see LocaleContextHolder#setLocaleContext
	 */
	protected LocaleContext buildLocaleContext(HttpServletRequest request) {
		return new SimpleLocaleContext(request.getLocale());
	}

	/**
	 * Build ServletRequestAttributes for the given request (potentially also
	 * holding a reference to the response), taking pre-bound attributes
	 * (and their type) into consideration.
	 * <p>
	 *  为给定的请求构建ServletRequestAttributes(可能还保留对响应的引用),考虑预先绑定的属性(及其类型)
	 * 
	 * 
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param previousAttributes pre-bound RequestAttributes instance, if any
	 * @return the ServletRequestAttributes to bind, or {@code null} to preserve
	 * the previously bound instance (or not binding any, if none bound before)
	 * @see RequestContextHolder#setRequestAttributes
	 */
	protected ServletRequestAttributes buildRequestAttributes(
			HttpServletRequest request, HttpServletResponse response, RequestAttributes previousAttributes) {

		if (previousAttributes == null || previousAttributes instanceof ServletRequestAttributes) {
			return new ServletRequestAttributes(request, response);
		}
		else {
			return null;  // preserve the pre-bound RequestAttributes instance
		}
	}

	private void initContextHolders(
			HttpServletRequest request, LocaleContext localeContext, RequestAttributes requestAttributes) {

		if (localeContext != null) {
			LocaleContextHolder.setLocaleContext(localeContext, this.threadContextInheritable);
		}
		if (requestAttributes != null) {
			RequestContextHolder.setRequestAttributes(requestAttributes, this.threadContextInheritable);
		}
		if (logger.isTraceEnabled()) {
			logger.trace("Bound request context to thread: " + request);
		}
	}

	private void resetContextHolders(HttpServletRequest request,
			LocaleContext prevLocaleContext, RequestAttributes previousAttributes) {

		LocaleContextHolder.setLocaleContext(prevLocaleContext, this.threadContextInheritable);
		RequestContextHolder.setRequestAttributes(previousAttributes, this.threadContextInheritable);
		if (logger.isTraceEnabled()) {
			logger.trace("Cleared thread-bound request context: " + request);
		}
	}

	private void publishRequestHandledEvent(
			HttpServletRequest request, HttpServletResponse response, long startTime, Throwable failureCause) {

		if (this.publishEvents) {
			// Whether or not we succeeded, publish an event.
			long processingTime = System.currentTimeMillis() - startTime;
			int statusCode = (responseGetStatusAvailable ? response.getStatus() : -1);
			this.webApplicationContext.publishEvent(
					new ServletRequestHandledEvent(this,
							request.getRequestURI(), request.getRemoteAddr(),
							request.getMethod(), getServletConfig().getServletName(),
							WebUtils.getSessionId(request), getUsernameForRequest(request),
							processingTime, failureCause, statusCode));
		}
	}

	/**
	 * Determine the username for the given request.
	 * <p>The default implementation takes the name of the UserPrincipal, if any.
	 * Can be overridden in subclasses.
	 * <p>
	 *  确定给定请求的用户名<p>默认实现使用UserPrincipal的名称(如果有)可以在子类中覆盖
	 * 
	 * 
	 * @param request current HTTP request
	 * @return the username, or {@code null} if none found
	 * @see javax.servlet.http.HttpServletRequest#getUserPrincipal()
	 */
	protected String getUsernameForRequest(HttpServletRequest request) {
		Principal userPrincipal = request.getUserPrincipal();
		return (userPrincipal != null ? userPrincipal.getName() : null);
	}


	/**
	 * Subclasses must implement this method to do the work of request handling,
	 * receiving a centralized callback for GET, POST, PUT and DELETE.
	 * <p>The contract is essentially the same as that for the commonly overridden
	 * {@code doGet} or {@code doPost} methods of HttpServlet.
	 * <p>This class intercepts calls to ensure that exception handling and
	 * event publication takes place.
	 * <p>
	 * 子类必须实现此方法来执行请求处理工作,接收GET,POST,PUT和DELETE的集中式回调<p>合同与通常被覆盖的{@code doGet}或{@code doPost } HttpServlet的方
	 * 法<p>此类拦截调用以确保异常处理和事件发布。
	 * 
	 * 
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @throws Exception in case of any kind of processing failure
	 * @see javax.servlet.http.HttpServlet#doGet
	 * @see javax.servlet.http.HttpServlet#doPost
	 */
	protected abstract void doService(HttpServletRequest request, HttpServletResponse response)
			throws Exception;


	/**
	 * ApplicationListener endpoint that receives events from this servlet's WebApplicationContext
	 * only, delegating to {@code onApplicationEvent} on the FrameworkServlet instance.
	 * <p>
	 *  ApplicationListener端点仅接收来自此Servlet WebApplicationContext的事件,在FrameworkServlet实例上委托给{@code onApplicationEvent}
	 * 。
	 * 
	 */
	private class ContextRefreshListener implements ApplicationListener<ContextRefreshedEvent> {

		@Override
		public void onApplicationEvent(ContextRefreshedEvent event) {
			FrameworkServlet.this.onApplicationEvent(event);
		}
	}


	/**
	 * CallableProcessingInterceptor implementation that initializes and resets
	 * FrameworkServlet's context holders, i.e. LocaleContextHolder and RequestContextHolder.
	 * <p>
	 *  CallableProcessingInterceptor实现,用于初始化和重置FrameworkServlet的上下文持有者,即LocaleContextHolder和RequestContextH
	 * older。
	 */
	private class RequestBindingInterceptor extends CallableProcessingInterceptorAdapter {

		@Override
		public <T> void preProcess(NativeWebRequest webRequest, Callable<T> task) {
			HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
			if (request != null) {
				HttpServletResponse response = webRequest.getNativeRequest(HttpServletResponse.class);
				initContextHolders(request, buildLocaleContext(request), buildRequestAttributes(request, response, null));
			}
		}
		@Override
		public <T> void postProcess(NativeWebRequest webRequest, Callable<T> task, Object concurrentResult) {
			HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
			if (request != null) {
				resetContextHolders(request, null, null);
			}
		}
	}

}
