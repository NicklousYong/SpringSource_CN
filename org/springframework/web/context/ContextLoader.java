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

package org.springframework.web.context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.access.ContextSingletonBeanFactoryLocator;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * Performs the actual initialization work for the root application context.
 * Called by {@link ContextLoaderListener}.
 *
 * <p>Looks for a {@link #CONTEXT_CLASS_PARAM "contextClass"} parameter at the
 * {@code web.xml} context-param level to specify the context class type, falling
 * back to {@link org.springframework.web.context.support.XmlWebApplicationContext}
 * if not found. With the default ContextLoader implementation, any context class
 * specified needs to implement the {@link ConfigurableWebApplicationContext} interface.
 *
 * <p>Processes a {@link #CONFIG_LOCATION_PARAM "contextConfigLocation"} context-param
 * and passes its value to the context instance, parsing it into potentially multiple
 * file paths which can be separated by any number of commas and spaces, e.g.
 * "WEB-INF/applicationContext1.xml, WEB-INF/applicationContext2.xml".
 * Ant-style path patterns are supported as well, e.g.
 * "WEB-INF/*Context.xml,WEB-INF/spring*.xml" or "WEB-INF/&#42;&#42;/*Context.xml".
 * If not explicitly specified, the context implementation is supposed to use a
 * default location (with XmlWebApplicationContext: "/WEB-INF/applicationContext.xml").
 *
 * <p>Note: In case of multiple config locations, later bean definitions will
 * override ones defined in previously loaded files, at least when using one of
 * Spring's default ApplicationContext implementations. This can be leveraged
 * to deliberately override certain bean definitions via an extra XML file.
 *
 * <p>Above and beyond loading the root application context, this class can optionally
 * load or obtain and hook up a shared parent context to the root application context.
 * See the {@link #loadParentContext(ServletContext)} method for more information.
 *
 * <p>As of Spring 3.1, {@code ContextLoader} supports injecting the root web
 * application context via the {@link #ContextLoader(WebApplicationContext)}
 * constructor, allowing for programmatic configuration in Servlet 3.0+ environments.
 * See {@link org.springframework.web.WebApplicationInitializer} for usage examples.
 *
 * <p>
 *  执行根应用程序上下文的实际初始化工作由{@link ContextLoaderListener}调用
 * 
 * <p>在{@code webxml}上下文参数级别查找{@link #CONTEXT_CLASS_PARAM"contextClass"}参数,以指定上下文类类型,如果未找到则返回到{@link orgspringframeworkwebcontextsupportXmlWebApplicationContext}
 * 使用默认的ContextLoader实现指定的任何上下文类都需要实现{@link ConfigurableWebApplicationContext}接口。
 * 
 * <p>处理{@link #CONFIG_LOCATION_PARAM"contextConfigLocation"}上下文参数,并将其值传递给上下文实例,将其解析为可能由多个逗号和空格分隔的潜在的多个文
 * 件路径,例如"WEB-INF / applicationContext1xml,WEB-INF / applicationContext2xml"也支持Ant风格的路径模式,例如"WEB-INF / *
 *  Contextxml,WEB-INF / spring * xml"或"WEB-INF / ** / * Contextxml"明确指定,上下文实现应该使用默认位置(使用XmlWebApplicati
 * onContext："/ WEB-INF / applicationContextxml")。
 * 
 * 注意：在多个配置位置的情况下,稍后的bean定义将覆盖先前加载的文件中定义的bean定义,至少在使用Spring的默认ApplicationContext实现之一时可以利用此功能,通过额外的XML文件故
 * 意覆盖某些bean定义。
 * 
 *  <p>除了加载根应用程序上下文,此类可以可选地加载或获取并将共享父上下文连接到根应用程序上下文有关更多信息,请参阅{@link #loadParentContext(ServletContext))方法。
 * 
 * <p>截至春季31,{@code ContextLoader}支持通过{@link #ContextLoader(WebApplicationContext)}构造函数注入根Web应用程序上下文,允许在
 * Servlet 30+环境中进行编程配置有关使用情况,请参阅{@link orgspringframeworkwebWebApplicationInitializer}例子。
 * 
 * 
 * @author Juergen Hoeller
 * @author Colin Sampaleanu
 * @author Sam Brannen
 * @since 17.02.2003
 * @see ContextLoaderListener
 * @see ConfigurableWebApplicationContext
 * @see org.springframework.web.context.support.XmlWebApplicationContext
 */
public class ContextLoader {

	/**
	 * Config param for the root WebApplicationContext id,
	 * to be used as serialization id for the underlying BeanFactory: {@value}
	 * <p>
	 *  根WebApplicationContext id的配置参数,用作底层BeanFactory的序列化ID：{@value}
	 * 
	 */
	public static final String CONTEXT_ID_PARAM = "contextId";

	/**
	 * Name of servlet context parameter (i.e., {@value}) that can specify the
	 * config location for the root context, falling back to the implementation's
	 * default otherwise.
	 * <p>
	 *  可以指定根上下文的配置位置的servlet上下文参数(即{@value})的名称,否则返回到实现的默认值
	 * 
	 * 
	 * @see org.springframework.web.context.support.XmlWebApplicationContext#DEFAULT_CONFIG_LOCATION
	 */
	public static final String CONFIG_LOCATION_PARAM = "contextConfigLocation";

	/**
	 * Config param for the root WebApplicationContext implementation class to use: {@value}
	 * <p>
	 *  使用根WebApplicationContext实现类的配置参数：{@value}
	 * 
	 * 
	 * @see #determineContextClass(ServletContext)
	 */
	public static final String CONTEXT_CLASS_PARAM = "contextClass";

	/**
	 * Config param for {@link ApplicationContextInitializer} classes to use
	 * for initializing the root web application context: {@value}
	 * <p>
	 * 用于初始化根Web应用程序上下文的{@link ApplicationContextInitializer}类的配置参数：{@value}
	 * 
	 * 
	 * @see #customizeContext(ServletContext, ConfigurableWebApplicationContext)
	 */
	public static final String CONTEXT_INITIALIZER_CLASSES_PARAM = "contextInitializerClasses";

	/**
	 * Config param for global {@link ApplicationContextInitializer} classes to use
	 * for initializing all web application contexts in the current application: {@value}
	 * <p>
	 *  全局{@link ApplicationContextInitializer}类的配置参数用于初始化当前应用程序中的所有Web应用程序上下文：{@value}
	 * 
	 * 
	 * @see #customizeContext(ServletContext, ConfigurableWebApplicationContext)
	 */
	public static final String GLOBAL_INITIALIZER_CLASSES_PARAM = "globalInitializerClasses";

	/**
	 * Optional servlet context parameter (i.e., "{@code locatorFactorySelector}")
	 * used only when obtaining a parent context using the default implementation
	 * of {@link #loadParentContext(ServletContext servletContext)}.
	 * Specifies the 'selector' used in the
	 * {@link ContextSingletonBeanFactoryLocator#getInstance(String selector)}
	 * method call, which is used to obtain the BeanFactoryLocator instance from
	 * which the parent context is obtained.
	 * <p>The default is {@code classpath*:beanRefContext.xml},
	 * matching the default applied for the
	 * {@link ContextSingletonBeanFactoryLocator#getInstance()} method.
	 * Supplying the "parentContextKey" parameter is sufficient in this case.
	 * <p>
	 * 仅使用{@link #loadParentContext(ServletContext servletContext)}的默认实现获取父上下文时使用的可选servlet上下文参数(即"{@code locatorFactorySelector}
	 * "))指定{@link ContextSingletonBeanFactoryLocator# getInstance(String selector)}方法调用,用于获取从中获取父上下文的BeanFa
	 * ctoryLocator实例<p>默认值为{@code classpath *：beanRefContextxml},与{@link ContextSingletonBeanFactoryLocator# getInstance()}
	 * 方法在这种情况下提供"parentContextKey"参数就足够了。
	 * 
	 */
	public static final String LOCATOR_FACTORY_SELECTOR_PARAM = "locatorFactorySelector";

	/**
	 * Optional servlet context parameter (i.e., "{@code parentContextKey}")
	 * used only when obtaining a parent context using the default implementation
	 * of {@link #loadParentContext(ServletContext servletContext)}.
	 * Specifies the 'factoryKey' used in the
	 * {@link BeanFactoryLocator#useBeanFactory(String factoryKey)} method call,
	 * obtaining the parent application context from the BeanFactoryLocator instance.
	 * <p>Supplying this "parentContextKey" parameter is sufficient when relying
	 * on the default {@code classpath*:beanRefContext.xml} selector for
	 * candidate factory references.
	 * <p>
	 * 仅在使用{@link #loadParentContext(ServletContext servletContext)}的默认实现获取父上下文时使用的可选servlet上下文参数(即"{@code parentContextKey}
	 * ")指定{@link BeanFactoryLocator# useBeanFactory(String factoryKey)}方法调用,从BeanFactoryLocator实例获取父应用程序上下文
	 * 当依赖于默认的{@code classpath *：beanRefContextxml}选择器以供候选工厂引用时,提供此"parentContextKey"参数就足够了。
	 * 
	 */
	public static final String LOCATOR_FACTORY_KEY_PARAM = "parentContextKey";

	/**
	 * Any number of these characters are considered delimiters between
	 * multiple values in a single init-param String value.
	 * <p>
	 *  在单个init-param String值中,任意数量的这些字符都被认为是多个值之间的分隔符
	 * 
	 */
	private static final String INIT_PARAM_DELIMITERS = ",; \t\n";

	/**
	 * Name of the class path resource (relative to the ContextLoader class)
	 * that defines ContextLoader's default strategy names.
	 * <p>
	 * 定义ContextLoader默认策略名称的类路径资源(相对于ContextLoader类)的名称
	 * 
	 */
	private static final String DEFAULT_STRATEGIES_PATH = "ContextLoader.properties";


	private static final Properties defaultStrategies;

	static {
		// Load default strategy implementations from properties file.
		// This is currently strictly internal and not meant to be customized
		// by application developers.
		try {
			ClassPathResource resource = new ClassPathResource(DEFAULT_STRATEGIES_PATH, ContextLoader.class);
			defaultStrategies = PropertiesLoaderUtils.loadProperties(resource);
		}
		catch (IOException ex) {
			throw new IllegalStateException("Could not load 'ContextLoader.properties': " + ex.getMessage());
		}
	}


	/**
	 * Map from (thread context) ClassLoader to corresponding 'current' WebApplicationContext.
	 * <p>
	 *  映射从(线程上下文)ClassLoader到相应的'当前'WebApplicationContext
	 * 
	 */
	private static final Map<ClassLoader, WebApplicationContext> currentContextPerThread =
			new ConcurrentHashMap<ClassLoader, WebApplicationContext>(1);

	/**
	 * The 'current' WebApplicationContext, if the ContextLoader class is
	 * deployed in the web app ClassLoader itself.
	 * <p>
	 *  "当前"WebApplicationContext,如果ContextLoader类部署在Web应用程序ClassLoader本身中
	 * 
	 */
	private static volatile WebApplicationContext currentContext;


	/**
	 * The root WebApplicationContext instance that this loader manages.
	 * <p>
	 *  此加载程序管理的根WebApplicationContext实例
	 * 
	 */
	private WebApplicationContext context;

	/**
	 * Holds BeanFactoryReference when loading parent factory via
	 * ContextSingletonBeanFactoryLocator.
	 * <p>
	 *  通过ContextSingletonBeanFactoryLocator加载父工厂时,保留BeanFactoryReference
	 * 
	 */
	private BeanFactoryReference parentContextRef;

	/** Actual ApplicationContextInitializer instances to apply to the context */
	private final List<ApplicationContextInitializer<ConfigurableApplicationContext>> contextInitializers =
			new ArrayList<ApplicationContextInitializer<ConfigurableApplicationContext>>();


	/**
	 * Create a new {@code ContextLoader} that will create a web application context
	 * based on the "contextClass" and "contextConfigLocation" servlet context-params.
	 * See class-level documentation for details on default values for each.
	 * <p>This constructor is typically used when declaring the {@code
	 * ContextLoaderListener} subclass as a {@code <listener>} within {@code web.xml}, as
	 * a no-arg constructor is required.
	 * <p>The created application context will be registered into the ServletContext under
	 * the attribute name {@link WebApplicationContext#ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE}
	 * and subclasses are free to call the {@link #closeWebApplicationContext} method on
	 * container shutdown to close the application context.
	 * <p>
	 * 创建一个新的{@code ContextLoader},它将基于"contextClass"和"contextConfigLocation"servlet context-params创建一个Web应用
	 * 程序上下文。
	 * 有关每个<p>的默认值的详细信息,请参阅类级别的文档。
	 * 此构造函数通常用于在{@code webxml}中声明{@code ContextLoaderListener}子类为{@code <listener>},因为需要使用无参数构造函数<p>创建的应用程序
	 * 上下文将被注册到ServletContext的属性名称{ @link WebApplicationContext#ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE},子类可以
	 * 在容器关闭时调用{@link #closeWebApplicationContext}方法来关闭应用程序上下文。
	 * 有关每个<p>的默认值的详细信息,请参阅类级别的文档。
	 * 
	 * 
	 * @see #ContextLoader(WebApplicationContext)
	 * @see #initWebApplicationContext(ServletContext)
	 * @see #closeWebApplicationContext(ServletContext)
	 */
	public ContextLoader() {
	}

	/**
	 * Create a new {@code ContextLoader} with the given application context. This
	 * constructor is useful in Servlet 3.0+ environments where instance-based
	 * registration of listeners is possible through the {@link ServletContext#addListener}
	 * API.
	 * <p>The context may or may not yet be {@linkplain
	 * ConfigurableApplicationContext#refresh() refreshed}. If it (a) is an implementation
	 * of {@link ConfigurableWebApplicationContext} and (b) has <strong>not</strong>
	 * already been refreshed (the recommended approach), then the following will occur:
	 * <ul>
	 * <li>If the given context has not already been assigned an {@linkplain
	 * ConfigurableApplicationContext#setId id}, one will be assigned to it</li>
	 * <li>{@code ServletContext} and {@code ServletConfig} objects will be delegated to
	 * the application context</li>
	 * <li>{@link #customizeContext} will be called</li>
	 * <li>Any {@link ApplicationContextInitializer}s specified through the
	 * "contextInitializerClasses" init-param will be applied.</li>
	 * <li>{@link ConfigurableApplicationContext#refresh refresh()} will be called</li>
	 * </ul>
	 * If the context has already been refreshed or does not implement
	 * {@code ConfigurableWebApplicationContext}, none of the above will occur under the
	 * assumption that the user has performed these actions (or not) per his or her
	 * specific needs.
	 * <p>See {@link org.springframework.web.WebApplicationInitializer} for usage examples.
	 * <p>In any case, the given application context will be registered into the
	 * ServletContext under the attribute name {@link
	 * WebApplicationContext#ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE} and subclasses are
	 * free to call the {@link #closeWebApplicationContext} method on container shutdown
	 * to close the application context.
	 * <p>
	 * 使用给定的应用程序上下文创建一个新的{@code ContextLoader}这个构造函数在Servlet 30+环境中很有用,通过{@link ServletContext#addListener} 
	 * API可以实现基于实例的监听器注册<p>上下文可能或不可能但是{@linkplain ConfigurableApplicationContext#refresh()刷新}如果(a)是{@link ConfigurableWebApplicationContext}
	 * 的实现,(b)已经刷新了<strong>不</strong>(推荐的方法),那么将会发生以下情况：。
	 * <ul>
	 * <li>如果给定的上下文尚未被分配一个{@linkplain ConfigurableApplicationContext#setId id},那么将分配一个上下文</li> <li> {@ code ServletContext}
	 * ,并且{@code ServletConfig}对象将被委派应用程序上下文</li> <li> {@ link #customizeContext}将被调用</li> <li>将通过"contextIn
	 * itializerClasses"init-param指定的任何{@link ApplicationContextInitializer}将被应用</li> <li> {@ link ConfigurableApplicationContext#refresh refresh()}
	 * 将被调用</li>。
	 * </ul>
	 * 如果上下文已经被刷新或者没有实现{@code ConfigurableWebApplicationContext},那么在假设用户根据他或她的具体需求执行这些操作(或不)的情况下,不会出现上述情况<p>
	 * 请参见{@link orgspringframeworkwebWebApplicationInitializer}用于使用示例<p>在任何情况下,给定的应用程序上下文将被注册到属性名为{@link WebApplicationContext#ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE}
	 * 的ServletContext中,子类可以在容器关闭时自由调用{@link #closeWebApplicationContext}方法关闭应用程序上下文。
	 * 
	 * 
	 * @param context the application context to manage
	 * @see #initWebApplicationContext(ServletContext)
	 * @see #closeWebApplicationContext(ServletContext)
	 */
	public ContextLoader(WebApplicationContext context) {
		this.context = context;
	}


	/**
	 * Specify which {@link ApplicationContextInitializer} instances should be used
	 * to initialize the application context used by this {@code ContextLoader}.
	 * <p>
	 *  指定哪些{@link ApplicationContextInitializer}实例应用于初始化此{@code ContextLoader}使用的应用程序上下文
	 * 
	 * 
	 * @since 4.2
	 * @see #configureAndRefreshWebApplicationContext
	 * @see #customizeContext
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
	 * Initialize Spring's web application context for the given servlet context,
	 * using the application context provided at construction time, or creating a new one
	 * according to the "{@link #CONTEXT_CLASS_PARAM contextClass}" and
	 * "{@link #CONFIG_LOCATION_PARAM contextConfigLocation}" context-params.
	 * <p>
	 * 根据"{@link #CONTEXT_CLASS_PARAM contextClass}"和"{@link #CONFIG_LOCATION_PARAM contextConfigLocation}"上
	 * 下文参数初始化给定servlet上下文的Spring Web应用程序上下文,使用在构建时提供的应用程序上下文,或创建一个新的。
	 * 
	 * 
	 * @param servletContext current servlet context
	 * @return the new WebApplicationContext
	 * @see #ContextLoader(WebApplicationContext)
	 * @see #CONTEXT_CLASS_PARAM
	 * @see #CONFIG_LOCATION_PARAM
	 */
	public WebApplicationContext initWebApplicationContext(ServletContext servletContext) {
		if (servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE) != null) {
			throw new IllegalStateException(
					"Cannot initialize context because there is already a root application context present - " +
					"check whether you have multiple ContextLoader* definitions in your web.xml!");
		}

		Log logger = LogFactory.getLog(ContextLoader.class);
		servletContext.log("Initializing Spring root WebApplicationContext");
		if (logger.isInfoEnabled()) {
			logger.info("Root WebApplicationContext: initialization started");
		}
		long startTime = System.currentTimeMillis();

		try {
			// Store context in local instance variable, to guarantee that
			// it is available on ServletContext shutdown.
			if (this.context == null) {
				this.context = createWebApplicationContext(servletContext);
			}
			if (this.context instanceof ConfigurableWebApplicationContext) {
				ConfigurableWebApplicationContext cwac = (ConfigurableWebApplicationContext) this.context;
				if (!cwac.isActive()) {
					// The context has not yet been refreshed -> provide services such as
					// setting the parent context, setting the application context id, etc
					if (cwac.getParent() == null) {
						// The context instance was injected without an explicit parent ->
						// determine parent for root web application context, if any.
						ApplicationContext parent = loadParentContext(servletContext);
						cwac.setParent(parent);
					}
					configureAndRefreshWebApplicationContext(cwac, servletContext);
				}
			}
			servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, this.context);

			ClassLoader ccl = Thread.currentThread().getContextClassLoader();
			if (ccl == ContextLoader.class.getClassLoader()) {
				currentContext = this.context;
			}
			else if (ccl != null) {
				currentContextPerThread.put(ccl, this.context);
			}

			if (logger.isDebugEnabled()) {
				logger.debug("Published root WebApplicationContext as ServletContext attribute with name [" +
						WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE + "]");
			}
			if (logger.isInfoEnabled()) {
				long elapsedTime = System.currentTimeMillis() - startTime;
				logger.info("Root WebApplicationContext: initialization completed in " + elapsedTime + " ms");
			}

			return this.context;
		}
		catch (RuntimeException ex) {
			logger.error("Context initialization failed", ex);
			servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, ex);
			throw ex;
		}
		catch (Error err) {
			logger.error("Context initialization failed", err);
			servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, err);
			throw err;
		}
	}

	/**
	 * Instantiate the root WebApplicationContext for this loader, either the
	 * default context class or a custom context class if specified.
	 * <p>This implementation expects custom contexts to implement the
	 * {@link ConfigurableWebApplicationContext} interface.
	 * Can be overridden in subclasses.
	 * <p>In addition, {@link #customizeContext} gets called prior to refreshing the
	 * context, allowing subclasses to perform custom modifications to the context.
	 * <p>
	 *  实例化此加载器的根WebApplicationContext,默认上下文类或自定义上下文类(如果指定)<p>此实现期望自定义上下文实现{@link ConfigurableWebApplicationContext}
	 * 接口可以在子类中覆盖<p>此外,{@链接#customizeContext}在刷新上下文之前被调用,允许子类对上下文执行自定义修改。
	 * 
	 * 
	 * @param sc current servlet context
	 * @return the root WebApplicationContext
	 * @see ConfigurableWebApplicationContext
	 */
	protected WebApplicationContext createWebApplicationContext(ServletContext sc) {
		Class<?> contextClass = determineContextClass(sc);
		if (!ConfigurableWebApplicationContext.class.isAssignableFrom(contextClass)) {
			throw new ApplicationContextException("Custom context class [" + contextClass.getName() +
					"] is not of type [" + ConfigurableWebApplicationContext.class.getName() + "]");
		}
		return (ConfigurableWebApplicationContext) BeanUtils.instantiateClass(contextClass);
	}

	/**
	 * Return the WebApplicationContext implementation class to use, either the
	 * default XmlWebApplicationContext or a custom context class if specified.
	 * <p>
	 * 返回要使用的WebApplicationContext实现类,默认的XmlWebApplicationContext或指定的上下文类
	 * 
	 * 
	 * @param servletContext current servlet context
	 * @return the WebApplicationContext implementation class to use
	 * @see #CONTEXT_CLASS_PARAM
	 * @see org.springframework.web.context.support.XmlWebApplicationContext
	 */
	protected Class<?> determineContextClass(ServletContext servletContext) {
		String contextClassName = servletContext.getInitParameter(CONTEXT_CLASS_PARAM);
		if (contextClassName != null) {
			try {
				return ClassUtils.forName(contextClassName, ClassUtils.getDefaultClassLoader());
			}
			catch (ClassNotFoundException ex) {
				throw new ApplicationContextException(
						"Failed to load custom context class [" + contextClassName + "]", ex);
			}
		}
		else {
			contextClassName = defaultStrategies.getProperty(WebApplicationContext.class.getName());
			try {
				return ClassUtils.forName(contextClassName, ContextLoader.class.getClassLoader());
			}
			catch (ClassNotFoundException ex) {
				throw new ApplicationContextException(
						"Failed to load default context class [" + contextClassName + "]", ex);
			}
		}
	}

	protected void configureAndRefreshWebApplicationContext(ConfigurableWebApplicationContext wac, ServletContext sc) {
		if (ObjectUtils.identityToString(wac).equals(wac.getId())) {
			// The application context id is still set to its original default value
			// -> assign a more useful id based on available information
			String idParam = sc.getInitParameter(CONTEXT_ID_PARAM);
			if (idParam != null) {
				wac.setId(idParam);
			}
			else {
				// Generate default id...
				wac.setId(ConfigurableWebApplicationContext.APPLICATION_CONTEXT_ID_PREFIX +
						ObjectUtils.getDisplayString(sc.getContextPath()));
			}
		}

		wac.setServletContext(sc);
		String configLocationParam = sc.getInitParameter(CONFIG_LOCATION_PARAM);
		if (configLocationParam != null) {
			wac.setConfigLocation(configLocationParam);
		}

		// The wac environment's #initPropertySources will be called in any case when the context
		// is refreshed; do it eagerly here to ensure servlet property sources are in place for
		// use in any post-processing or initialization that occurs below prior to #refresh
		ConfigurableEnvironment env = wac.getEnvironment();
		if (env instanceof ConfigurableWebEnvironment) {
			((ConfigurableWebEnvironment) env).initPropertySources(sc, null);
		}

		customizeContext(sc, wac);
		wac.refresh();
	}

	/**
	 * Customize the {@link ConfigurableWebApplicationContext} created by this
	 * ContextLoader after config locations have been supplied to the context
	 * but before the context is <em>refreshed</em>.
	 * <p>The default implementation {@linkplain #determineContextInitializerClasses(ServletContext)
	 * determines} what (if any) context initializer classes have been specified through
	 * {@linkplain #CONTEXT_INITIALIZER_CLASSES_PARAM context init parameters} and
	 * {@linkplain ApplicationContextInitializer#initialize invokes each} with the
	 * given web application context.
	 * <p>Any {@code ApplicationContextInitializers} implementing
	 * {@link org.springframework.core.Ordered Ordered} or marked with @{@link
	 * org.springframework.core.annotation.Order Order} will be sorted appropriately.
	 * <p>
	 * 自定义此ContextLoader创建的{@link ConfigurableWebApplicationContext},在配置位置已提供给上下文之后,但上下文被刷新</em> <p>默认实现{@linkplain #determineContextInitializerClasses(ServletContext))确定(如果有的话)上下文初始化程序类已经通过{@linkplain #CONTEXT_INITIALIZER_CLASSES_PARAM上下文初始化参数}
	 * 和{@linkplain ApplicationContextInitializer#initialize调用每个}使用给定的Web应用程序上下文来指定任何{@code ApplicationContextInitializers}
	 * 实现{@link orgspringframeworkcoreOrdered有序}或者标记为@ {@ link orgspringframeworkcoreannotationOrder Order}将
	 * 被适当排序。
	 * 
	 * 
	 * @param sc the current servlet context
	 * @param wac the newly created application context
	 * @see #CONTEXT_INITIALIZER_CLASSES_PARAM
	 * @see ApplicationContextInitializer#initialize(ConfigurableApplicationContext)
	 */
	protected void customizeContext(ServletContext sc, ConfigurableWebApplicationContext wac) {
		List<Class<ApplicationContextInitializer<ConfigurableApplicationContext>>> initializerClasses =
				determineContextInitializerClasses(sc);

		for (Class<ApplicationContextInitializer<ConfigurableApplicationContext>> initializerClass : initializerClasses) {
			Class<?> initializerContextClass =
					GenericTypeResolver.resolveTypeArgument(initializerClass, ApplicationContextInitializer.class);
			if (initializerContextClass != null && !initializerContextClass.isInstance(wac)) {
				throw new ApplicationContextException(String.format(
						"Could not apply context initializer [%s] since its generic parameter [%s] " +
						"is not assignable from the type of application context used by this " +
						"context loader: [%s]", initializerClass.getName(), initializerContextClass.getName(),
						wac.getClass().getName()));
			}
			this.contextInitializers.add(BeanUtils.instantiateClass(initializerClass));
		}

		AnnotationAwareOrderComparator.sort(this.contextInitializers);
		for (ApplicationContextInitializer<ConfigurableApplicationContext> initializer : this.contextInitializers) {
			initializer.initialize(wac);
		}
	}

	/**
	 * Return the {@link ApplicationContextInitializer} implementation classes to use
	 * if any have been specified by {@link #CONTEXT_INITIALIZER_CLASSES_PARAM}.
	 * <p>
	 * 返回{@link ApplicationContextInitializer}实现类,以使用{@link #CONTEXT_INITIALIZER_CLASSES_PARAM}指定的任何类
	 * 
	 * 
	 * @param servletContext current servlet context
	 * @see #CONTEXT_INITIALIZER_CLASSES_PARAM
	 */
	protected List<Class<ApplicationContextInitializer<ConfigurableApplicationContext>>>
			determineContextInitializerClasses(ServletContext servletContext) {

		List<Class<ApplicationContextInitializer<ConfigurableApplicationContext>>> classes =
				new ArrayList<Class<ApplicationContextInitializer<ConfigurableApplicationContext>>>();

		String globalClassNames = servletContext.getInitParameter(GLOBAL_INITIALIZER_CLASSES_PARAM);
		if (globalClassNames != null) {
			for (String className : StringUtils.tokenizeToStringArray(globalClassNames, INIT_PARAM_DELIMITERS)) {
				classes.add(loadInitializerClass(className));
			}
		}

		String localClassNames = servletContext.getInitParameter(CONTEXT_INITIALIZER_CLASSES_PARAM);
		if (localClassNames != null) {
			for (String className : StringUtils.tokenizeToStringArray(localClassNames, INIT_PARAM_DELIMITERS)) {
				classes.add(loadInitializerClass(className));
			}
		}

		return classes;
	}

	@SuppressWarnings("unchecked")
	private Class<ApplicationContextInitializer<ConfigurableApplicationContext>> loadInitializerClass(String className) {
		try {
			Class<?> clazz = ClassUtils.forName(className, ClassUtils.getDefaultClassLoader());
			Assert.isAssignable(ApplicationContextInitializer.class, clazz);
			return (Class<ApplicationContextInitializer<ConfigurableApplicationContext>>) clazz;
		}
		catch (ClassNotFoundException ex) {
			throw new ApplicationContextException("Failed to load context initializer class [" + className + "]", ex);
		}
	}

	/**
	 * Template method with default implementation (which may be overridden by a
	 * subclass), to load or obtain an ApplicationContext instance which will be
	 * used as the parent context of the root WebApplicationContext. If the
	 * return value from the method is null, no parent context is set.
	 * <p>The main reason to load a parent context here is to allow multiple root
	 * web application contexts to all be children of a shared EAR context, or
	 * alternately to also share the same parent context that is visible to
	 * EJBs. For pure web applications, there is usually no need to worry about
	 * having a parent context to the root web application context.
	 * <p>The default implementation uses
	 * {@link org.springframework.context.access.ContextSingletonBeanFactoryLocator},
	 * configured via {@link #LOCATOR_FACTORY_SELECTOR_PARAM} and
	 * {@link #LOCATOR_FACTORY_KEY_PARAM}, to load a parent context
	 * which will be shared by all other users of ContextsingletonBeanFactoryLocator
	 * which also use the same configuration parameters.
	 * <p>
	 * 具有默认实现(可能被子类覆盖)的模板方法,以加载或获取将用作根WebApplicationContext的父上下文的ApplicationContext实例如果方法的返回值为空,则不设置父上下文< p>
	 * 加载父上下文的主要原因是允许多个根Web应用程序上下文都是共享EAR上下文的子节点,或者交替地共享EJB可见的相同父上下文对于纯Web应用程序,有通常不需要担心有一个父上下文到根Web应用程序上下文<p>
	 * 默认实现使用{@link orgspringframeworkcontextaccessContextSingletonBeanFactoryLocator},通过{@link #LOCATOR_FACTORY_SELECTOR_PARAM}
	 * 和{@link #LOCATOR_FACTORY_KEY_PARAM}进行配置,以加载将由ContextsingletonBeanFactoryLocator的所有其他用户共享的父上下文,该用户也使用相
	 * 同的配置参数。
	 * 
	 * 
	 * @param servletContext current servlet context
	 * @return the parent application context, or {@code null} if none
	 * @see org.springframework.context.access.ContextSingletonBeanFactoryLocator
	 */
	protected ApplicationContext loadParentContext(ServletContext servletContext) {
		ApplicationContext parentContext = null;
		String locatorFactorySelector = servletContext.getInitParameter(LOCATOR_FACTORY_SELECTOR_PARAM);
		String parentContextKey = servletContext.getInitParameter(LOCATOR_FACTORY_KEY_PARAM);

		if (parentContextKey != null) {
			// locatorFactorySelector may be null, indicating the default "classpath*:beanRefContext.xml"
			BeanFactoryLocator locator = ContextSingletonBeanFactoryLocator.getInstance(locatorFactorySelector);
			Log logger = LogFactory.getLog(ContextLoader.class);
			if (logger.isDebugEnabled()) {
				logger.debug("Getting parent context definition: using parent context key of '" +
						parentContextKey + "' with BeanFactoryLocator");
			}
			this.parentContextRef = locator.useBeanFactory(parentContextKey);
			parentContext = (ApplicationContext) this.parentContextRef.getFactory();
		}

		return parentContext;
	}

	/**
	 * Close Spring's web application context for the given servlet context. If
	 * the default {@link #loadParentContext(ServletContext)} implementation,
	 * which uses ContextSingletonBeanFactoryLocator, has loaded any shared
	 * parent context, release one reference to that shared parent context.
	 * <p>If overriding {@link #loadParentContext(ServletContext)}, you may have
	 * to override this method as well.
	 * <p>
	 * 关闭给定servlet上下文的Spring的Web应用程序上下文如果使用ContextSingletonBeanFactoryLocator的默认{@link #loadParentContext(ServletContext))实现已加载任何共享父上下文,请释放对该共享父上下文的一个引用<p>如果覆盖{@链接#loadParentContext(ServletContext)}
	 * ,您可能也必须重写此方法。
	 * 
	 * 
	 * @param servletContext the ServletContext that the WebApplicationContext runs in
	 */
	public void closeWebApplicationContext(ServletContext servletContext) {
		servletContext.log("Closing Spring root WebApplicationContext");
		try {
			if (this.context instanceof ConfigurableWebApplicationContext) {
				((ConfigurableWebApplicationContext) this.context).close();
			}
		}
		finally {
			ClassLoader ccl = Thread.currentThread().getContextClassLoader();
			if (ccl == ContextLoader.class.getClassLoader()) {
				currentContext = null;
			}
			else if (ccl != null) {
				currentContextPerThread.remove(ccl);
			}
			servletContext.removeAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
			if (this.parentContextRef != null) {
				this.parentContextRef.release();
			}
		}
	}


	/**
	 * Obtain the Spring root web application context for the current thread
	 * (i.e. for the current thread's context ClassLoader, which needs to be
	 * the web application's ClassLoader).
	 * <p>
	 * 
	 * @return the current root web application context, or {@code null}
	 * if none found
	 * @see org.springframework.web.context.support.SpringBeanAutowiringSupport
	 */
	public static WebApplicationContext getCurrentWebApplicationContext() {
		ClassLoader ccl = Thread.currentThread().getContextClassLoader();
		if (ccl != null) {
			WebApplicationContext ccpt = currentContextPerThread.get(ccl);
			if (ccpt != null) {
				return ccpt;
			}
		}
		return currentContext;
	}

}
