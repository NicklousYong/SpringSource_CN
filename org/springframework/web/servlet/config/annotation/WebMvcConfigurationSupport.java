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

package org.springframework.web.servlet.config.annotation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Source;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.feed.AtomFeedHttpMessageConverter;
import org.springframework.http.converter.feed.RssChannelHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ClassUtils;
import org.springframework.util.PathMatcher;
import org.springframework.validation.Errors;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.method.support.CompositeUriComponentsContributor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping;
import org.springframework.web.servlet.handler.ConversionServiceExposingInterceptor;
import org.springframework.web.servlet.handler.HandlerExceptionResolverComposite;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter;
import org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter;
import org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.JsonViewRequestBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.JsonViewResponseBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;
import org.springframework.web.servlet.resource.ResourceUrlProvider;
import org.springframework.web.servlet.resource.ResourceUrlProviderExposingInterceptor;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.ViewResolverComposite;
import org.springframework.web.util.UrlPathHelper;

/**
 * This is the main class providing the configuration behind the MVC Java config.
 * It is typically imported by adding {@link EnableWebMvc @EnableWebMvc} to an
 * application {@link Configuration @Configuration} class. An alternative more
 * advanced option is to extend directly from this class and override methods as
 * necessary remembering to add {@link Configuration @Configuration} to the
 * subclass and {@link Bean @Bean} to overridden {@link Bean @Bean} methods.
 * For more details see the Javadoc of {@link EnableWebMvc @EnableWebMvc}.
 *
 * <p>This class registers the following {@link HandlerMapping}s:</p>
 * <ul>
 * <li>{@link RequestMappingHandlerMapping}
 * ordered at 0 for mapping requests to annotated controller methods.
 * <li>{@link HandlerMapping}
 * ordered at 1 to map URL paths directly to view names.
 * <li>{@link BeanNameUrlHandlerMapping}
 * ordered at 2 to map URL paths to controller bean names.
 * <li>{@link HandlerMapping}
 * ordered at {@code Integer.MAX_VALUE-1} to serve static resource requests.
 * <li>{@link HandlerMapping}
 * ordered at {@code Integer.MAX_VALUE} to forward requests to the default servlet.
 * </ul>
 *
 * <p>Registers these {@link HandlerAdapter}s:
 * <ul>
 * <li>{@link RequestMappingHandlerAdapter}
 * for processing requests with annotated controller methods.
 * <li>{@link HttpRequestHandlerAdapter}
 * for processing requests with {@link HttpRequestHandler}s.
 * <li>{@link SimpleControllerHandlerAdapter}
 * for processing requests with interface-based {@link Controller}s.
 * </ul>
 *
 * <p>Registers a {@link HandlerExceptionResolverComposite} with this chain of
 * exception resolvers:
 * <ul>
 * <li>{@link ExceptionHandlerExceptionResolver} for handling exceptions
 * through @{@link ExceptionHandler} methods.
 * <li>{@link ResponseStatusExceptionResolver} for exceptions annotated
 * with @{@link ResponseStatus}.
 * <li>{@link DefaultHandlerExceptionResolver} for resolving known Spring
 * exception types
 * </ul>
 *
 * <p>Registers an {@link AntPathMatcher} and a {@link UrlPathHelper}
 * to be used by:
 * <ul>
 * <li>the {@link RequestMappingHandlerMapping},
 * <li>the {@link HandlerMapping} for ViewControllers
 * <li>and the {@link HandlerMapping} for serving resources
 * </ul>
 * Note that those beans can be configured with a {@link PathMatchConfigurer}.
 *
 * <p>Both the {@link RequestMappingHandlerAdapter} and the
 * {@link ExceptionHandlerExceptionResolver} are configured with default
 * instances of the following by default:
 * <ul>
 * <li>a {@link ContentNegotiationManager}
 * <li>a {@link DefaultFormattingConversionService}
 * <li>a {@link org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean}
 * if a JSR-303 implementation is available on the classpath
 * <li>a range of {@link HttpMessageConverter}s depending on the third-party
 * libraries available on the classpath.
 * </ul>
 *
 * <p>
 * 这是提供MVC Java配置背后的配置的主要类型通常通过将{@link EnableWebMvc @EnableWebMvc}添加到应用程序{@link Configuration @Configuration}
 * 类中来进行导入。
 * 另一种更高级的选项是直接从该类扩展,覆盖方法根据需要记住将{@link Configuration @Configuration}添加到子类和{@link Bean @Bean}以覆盖{@link Bean @Bean}
 * 方法有关详细信息,请参阅{@link EnableWebMvc @EnableWebMvc}的Javadoc。
 * 
 *  <p>此类注册以下{@link HandlerMapping}：</p>
 * <ul>
 * <li> {@ link RequestMappingHandlerMapping}在0处排序,将请求映射到带注释的控制器方法<li> {@ link HandlerMapping},以1的顺序排列,以
 * 直接映射URL路径以查看名称<li> {@ link BeanNameUrlHandlerMapping}通过{@code IntegerMAX_VALUE-1}排序的控制器bean名称<li> {@ link HandlerMapping}
 * 的URL路径以提供{@code IntegerMAX_VALUE}排序的静态资源请求<li> {@ link HandlerMapping}将请求转发到默认servlet。
 * </ul>
 * 
 *  <p>注册这些{@link HandlerAdapter}：
 * <ul>
 * <li> {@ link RequestMappingHandlerAdapter}用于使用带注释的控制器方法处理请求<li> {@ link HttpRequestHandlerAdapter}用{@link HttpRequestHandler}
 *  <li> {@ link SimpleControllerHandlerAdapter}处理请求,以处理基于界面的{@链接控制器} s。
 * </ul>
 * 
 *  <p>使用此异常解析器链注册{@link HandlerExceptionResolverComposite}：
 * <ul>
 *  通过@ {@ link ExceptionHandler}方法来处理异常的<li> {@ link ExceptionHandlerExceptionResolver}用于解决已知的Spring异常的
 * @ {@ link ResponseStatus} <li> {@ link DefaultHandlerExceptionResolver}注释的异常的<li> {@ link ResponseStatusExceptionResolver}
 * 类型。
 * </ul>
 * 
 *  <p>注册{@link AntPathMatcher}和{@link UrlPathHelper}以供以下用途使用：
 * <ul>
 * <li>用于ViewController的{@link RequestMappingHandlerMapping},<li>用于ViewControllers的{@link HandlerMapping}
 * 和用于投放资源的{@link HandlerMapping}。
 * </ul>
 *  请注意,这些bean可以使用{@link PathMatchConfigurer}
 * 
 *  <p>默认情况下,{@link RequestMappingHandlerAdapter}和{@link ExceptionHandlerExceptionResolver}都配置有以下默认实例：
 * <ul>
 *  <li>一个{@link ContentNegotiationManager} <li>一个{@link DefaultFormattingConversionService} <li>一个{@link orgspringframeworkvalidationbeanvalidationOptionalValidatorFactoryBean}
 * 如果JSR-303实现在类路径上可用<li>一系列{@link HttpMessageConverter}这取决于类路径上可用的第三方库。
 * </ul>
 * 
 * 
 * @author Rossen Stoyanchev
 * @author Brian Clozel
 * @author Sebastien Deleuze
 * @since 3.1
 * @see EnableWebMvc
 * @see WebMvcConfigurer
 * @see WebMvcConfigurerAdapter
 */
public class WebMvcConfigurationSupport implements ApplicationContextAware, ServletContextAware {

	private static boolean romePresent =
			ClassUtils.isPresent("com.rometools.rome.feed.WireFeed", WebMvcConfigurationSupport.class.getClassLoader());

	private static final boolean jaxb2Present =
			ClassUtils.isPresent("javax.xml.bind.Binder", WebMvcConfigurationSupport.class.getClassLoader());

	private static final boolean jackson2Present =
			ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", WebMvcConfigurationSupport.class.getClassLoader()) &&
					ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator", WebMvcConfigurationSupport.class.getClassLoader());

	private static final boolean jackson2XmlPresent =
			ClassUtils.isPresent("com.fasterxml.jackson.dataformat.xml.XmlMapper", WebMvcConfigurationSupport.class.getClassLoader());

	private static final boolean gsonPresent =
			ClassUtils.isPresent("com.google.gson.Gson", WebMvcConfigurationSupport.class.getClassLoader());


	private ApplicationContext applicationContext;

	private ServletContext servletContext;

	private List<Object> interceptors;

	private PathMatchConfigurer pathMatchConfigurer;

	private ContentNegotiationManager contentNegotiationManager;

	private List<HandlerMethodArgumentResolver> argumentResolvers;

	private List<HandlerMethodReturnValueHandler> returnValueHandlers;

	private List<HttpMessageConverter<?>> messageConverters;

	private Map<String, CorsConfiguration> corsConfigurations;


	/**
	 * Set the Spring {@link ApplicationContext}, e.g. for resource loading.
	 * <p>
	 * 设置Spring {@link ApplicationContext},例如用于资源加载
	 * 
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	/**
	 * Return the associated Spring {@link ApplicationContext}.
	 * <p>
	 *  返回相关联的Spring {@link ApplicationContext}
	 * 
	 * 
	 * @since 4.2
	 */
	public ApplicationContext getApplicationContext() {
		return this.applicationContext;
	}

	/**
	 * Set the {@link javax.servlet.ServletContext}, e.g. for resource handling,
	 * looking up file extensions, etc.
	 * <p>
	 *  设置{@link javaxservletServletContext},例如资源处理,查找文件扩展名等
	 * 
	 */
	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	/**
	 * Return the associated {@link javax.servlet.ServletContext}.
	 * <p>
	 *  返回相关联的{@link javaxservletServletContext}
	 * 
	 * 
	 * @since 4.2
	 */
	public ServletContext getServletContext() {
		return this.servletContext;
	}


	/**
	 * Return a {@link RequestMappingHandlerMapping} ordered at 0 for mapping
	 * requests to annotated controllers.
	 * <p>
	 *  返回一个订单为0的{@link RequestMappingHandlerMapping},以将请求映射到注释控制器
	 * 
	 */
	@Bean
	public RequestMappingHandlerMapping requestMappingHandlerMapping() {
		RequestMappingHandlerMapping handlerMapping = createRequestMappingHandlerMapping();
		handlerMapping.setOrder(0);
		handlerMapping.setInterceptors(getInterceptors());
		handlerMapping.setContentNegotiationManager(mvcContentNegotiationManager());
		handlerMapping.setCorsConfigurations(getCorsConfigurations());

		PathMatchConfigurer configurer = getPathMatchConfigurer();
		if (configurer.isUseSuffixPatternMatch() != null) {
			handlerMapping.setUseSuffixPatternMatch(configurer.isUseSuffixPatternMatch());
		}
		if (configurer.isUseRegisteredSuffixPatternMatch() != null) {
			handlerMapping.setUseRegisteredSuffixPatternMatch(configurer.isUseRegisteredSuffixPatternMatch());
		}
		if (configurer.isUseTrailingSlashMatch() != null) {
			handlerMapping.setUseTrailingSlashMatch(configurer.isUseTrailingSlashMatch());
		}
		UrlPathHelper pathHelper = configurer.getUrlPathHelper();
		if (pathHelper != null) {
			handlerMapping.setUrlPathHelper(pathHelper);
		}
		PathMatcher pathMatcher = configurer.getPathMatcher();
		if (pathMatcher != null) {
			handlerMapping.setPathMatcher(pathMatcher);
		}

		return handlerMapping;
	}

	/**
	 * Protected method for plugging in a custom subclass of
	 * {@link RequestMappingHandlerMapping}.
	 * <p>
	 *  插入{@link RequestMappingHandlerMapping}的自定义子类的受保护方法
	 * 
	 * 
	 * @since 4.0
	 */
	protected RequestMappingHandlerMapping createRequestMappingHandlerMapping() {
		return new RequestMappingHandlerMapping();
	}

	/**
	 * Provide access to the shared handler interceptors used to configure
	 * {@link HandlerMapping} instances with. This method cannot be overridden,
	 * use {@link #addInterceptors(InterceptorRegistry)} instead.
	 * <p>
	 *  提供访问用于配置{@link HandlerMapping}实例的共享处理程序拦截器,此方法不能被覆盖,而是使用{@link #addInterceptors(InterceptorRegistry)}
	 * 代替。
	 * 
	 */
	protected final Object[] getInterceptors() {
		if (this.interceptors == null) {
			InterceptorRegistry registry = new InterceptorRegistry();
			addInterceptors(registry);
			registry.addInterceptor(new ConversionServiceExposingInterceptor(mvcConversionService()));
			registry.addInterceptor(new ResourceUrlProviderExposingInterceptor(mvcResourceUrlProvider()));
			this.interceptors = registry.getInterceptors();
		}
		return this.interceptors.toArray();
	}

	/**
	 * Override this method to add Spring MVC interceptors for
	 * pre- and post-processing of controller invocation.
	 * <p>
	 * 覆盖此方法以添加用于控制器调用的前处理和后处理的Spring MVC拦截器
	 * 
	 * 
	 * @see InterceptorRegistry
	 */
	protected void addInterceptors(InterceptorRegistry registry) {
	}

	/**
	 * Callback for building the {@link PathMatchConfigurer}.
	 * Delegates to {@link #configurePathMatch}.
	 * <p>
	 *  回调建立{@link PathMatchConfigurer}代理到{@link #configurePathMatch}
	 * 
	 * 
	 * @since 4.1
	 */
	protected PathMatchConfigurer getPathMatchConfigurer() {
		if (this.pathMatchConfigurer == null) {
			this.pathMatchConfigurer = new PathMatchConfigurer();
			configurePathMatch(this.pathMatchConfigurer);
		}
		return this.pathMatchConfigurer;
	}

	/**
	 * Override this method to configure path matching options.
	 * <p>
	 *  覆盖此方法以配置路径匹配选项
	 * 
	 * 
	 * @see PathMatchConfigurer
	 * @since 4.0.3
	 */
	protected void configurePathMatch(PathMatchConfigurer configurer) {
	}

	/**
	 * Return a global {@link PathMatcher} instance for path matching
	 * patterns in {@link HandlerMapping}s.
	 * This instance can be configured using the {@link PathMatchConfigurer}
	 * in {@link #configurePathMatch(PathMatchConfigurer)}.
	 * <p>
	 *  在{@link HandlerMapping}中返回用于路径匹配模式的全局{@link PathMatcher}实例此实例可以使用{@link #configurePathMatch(PathMatchConfigurer)}
	 * 中的{@link PathMatchConfigurer}进行配置。
	 * 
	 * 
	 * @since 4.1
	 */
	@Bean
	public PathMatcher mvcPathMatcher() {
		PathMatcher pathMatcher = getPathMatchConfigurer().getPathMatcher();
		return (pathMatcher != null ? pathMatcher : new AntPathMatcher());
	}

	/**
	 * Return a global {@link UrlPathHelper} instance for path matching
	 * patterns in {@link HandlerMapping}s.
	 * This instance can be configured using the {@link PathMatchConfigurer}
	 * in {@link #configurePathMatch(PathMatchConfigurer)}.
	 * <p>
	 *  在{@link HandlerMapping}中返回用于路径匹配模式的全局{@link UrlPathHelper}实例此实例可以使用{@link #configurePathMatch(PathMatchConfigurer)}
	 * 中的{@link PathMatchConfigurer}进行配置。
	 * 
	 * 
	 * @since 4.1
	 */
	@Bean
	public UrlPathHelper mvcUrlPathHelper() {
		UrlPathHelper pathHelper = getPathMatchConfigurer().getUrlPathHelper();
		return (pathHelper != null ? pathHelper : new UrlPathHelper());
	}

	/**
	 * Return a {@link ContentNegotiationManager} instance to use to determine
	 * requested {@linkplain MediaType media types} in a given request.
	 * <p>
	 * 返回一个{@link ContentNegotiationManager}实例,用于确定给定请求中的请求的{@linkplain MediaType媒体类型}
	 * 
	 */
	@Bean
	public ContentNegotiationManager mvcContentNegotiationManager() {
		if (this.contentNegotiationManager == null) {
			ContentNegotiationConfigurer configurer = new ContentNegotiationConfigurer(this.servletContext);
			configurer.mediaTypes(getDefaultMediaTypes());
			configureContentNegotiation(configurer);
			try {
				this.contentNegotiationManager = configurer.getContentNegotiationManager();
			}
			catch (Exception ex) {
				throw new BeanInitializationException("Could not create ContentNegotiationManager", ex);
			}
		}
		return this.contentNegotiationManager;
	}

	protected Map<String, MediaType> getDefaultMediaTypes() {
		Map<String, MediaType> map = new HashMap<String, MediaType>(4);
		if (romePresent) {
			map.put("atom", MediaType.APPLICATION_ATOM_XML);
			map.put("rss", MediaType.valueOf("application/rss+xml"));
		}
		if (jaxb2Present || jackson2XmlPresent) {
			map.put("xml", MediaType.APPLICATION_XML);
		}
		if (jackson2Present || gsonPresent) {
			map.put("json", MediaType.APPLICATION_JSON);
		}
		return map;
	}

	/**
	 * Override this method to configure content negotiation.
	 * <p>
	 *  覆盖此方法来配置内容协商
	 * 
	 * 
	 * @see DefaultServletHandlerConfigurer
	 */
	protected void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
	}

	/**
	 * Return a handler mapping ordered at 1 to map URL paths directly to
	 * view names. To configure view controllers, override
	 * {@link #addViewControllers}.
	 * <p>
	 *  返回一个排序为1的处理程序映射,将URL路径直接映射到视图名称要配置视图控制器,请覆盖{@link #addViewControllers}
	 * 
	 */
	@Bean
	public HandlerMapping viewControllerHandlerMapping() {
		ViewControllerRegistry registry = new ViewControllerRegistry();
		registry.setApplicationContext(this.applicationContext);
		addViewControllers(registry);

		AbstractHandlerMapping handlerMapping = registry.getHandlerMapping();
		handlerMapping = (handlerMapping != null ? handlerMapping : new EmptyHandlerMapping());
		handlerMapping.setPathMatcher(mvcPathMatcher());
		handlerMapping.setUrlPathHelper(mvcUrlPathHelper());
		handlerMapping.setInterceptors(getInterceptors());
		handlerMapping.setCorsConfigurations(getCorsConfigurations());
		return handlerMapping;
	}

	/**
	 * Override this method to add view controllers.
	 * <p>
	 *  覆盖此方法以添加视图控制器
	 * 
	 * 
	 * @see ViewControllerRegistry
	 */
	protected void addViewControllers(ViewControllerRegistry registry) {
	}

	/**
	 * Return a {@link BeanNameUrlHandlerMapping} ordered at 2 to map URL
	 * paths to controller bean names.
	 * <p>
	 *  返回一个以2的顺序排列的{@link BeanNameUrlHandlerMapping},将URL路径映射到控制器bean名称
	 * 
	 */
	@Bean
	public BeanNameUrlHandlerMapping beanNameHandlerMapping() {
		BeanNameUrlHandlerMapping mapping = new BeanNameUrlHandlerMapping();
		mapping.setOrder(2);
		mapping.setInterceptors(getInterceptors());
		mapping.setCorsConfigurations(getCorsConfigurations());
		return mapping;
	}

	/**
	 * Return a handler mapping ordered at Integer.MAX_VALUE-1 with mapped
	 * resource handlers. To configure resource handling, override
	 * {@link #addResourceHandlers}.
	 * <p>
	 *  使用映射资源处理程序返回以IntegerMAX_VALUE-1排序的处理程序映射要配置资源处理,请覆盖{@link #addResourceHandlers}
	 * 
	 */
	@Bean
	public HandlerMapping resourceHandlerMapping() {
		ResourceHandlerRegistry registry = new ResourceHandlerRegistry(this.applicationContext,
				this.servletContext, mvcContentNegotiationManager());
		addResourceHandlers(registry);

		AbstractHandlerMapping handlerMapping = registry.getHandlerMapping();
		if (handlerMapping != null) {
			handlerMapping.setPathMatcher(mvcPathMatcher());
			handlerMapping.setUrlPathHelper(mvcUrlPathHelper());
			handlerMapping.setInterceptors(new ResourceUrlProviderExposingInterceptor(mvcResourceUrlProvider()));
			handlerMapping.setCorsConfigurations(getCorsConfigurations());
		}
		else {
			handlerMapping = new EmptyHandlerMapping();
		}
		return handlerMapping;
	}

	/**
	 * Override this method to add resource handlers for serving static resources.
	 * <p>
	 *  覆盖此方法以添加用于提供静态资源的资源处理程序
	 * 
	 * 
	 * @see ResourceHandlerRegistry
	 */
	protected void addResourceHandlers(ResourceHandlerRegistry registry) {
	}

	/**
	 * A {@link ResourceUrlProvider} bean for use with the MVC dispatcher.
	 * <p>
	 *  用于MVC调度程序的{@link ResourceUrlProvider} bean
	 * 
	 * 
	 * @since 4.1
	 */
	@Bean
	public ResourceUrlProvider mvcResourceUrlProvider() {
		ResourceUrlProvider urlProvider = new ResourceUrlProvider();
		UrlPathHelper pathHelper = getPathMatchConfigurer().getUrlPathHelper();
		if (pathHelper != null) {
			urlProvider.setUrlPathHelper(pathHelper);
		}
		PathMatcher pathMatcher = getPathMatchConfigurer().getPathMatcher();
		if (pathMatcher != null) {
			urlProvider.setPathMatcher(pathMatcher);
		}
		return urlProvider;
	}

	/**
	 * Return a handler mapping ordered at Integer.MAX_VALUE with a mapped
	 * default servlet handler. To configure "default" Servlet handling,
	 * override {@link #configureDefaultServletHandling}.
	 * <p>
	 * 返回以IntegerMAX_VALUE排序的处理器映射与映射的缺省servlet处理程序要配置"默认"Servlet处理,请覆盖{@link #configureDefaultServletHandling}
	 * 。
	 * 
	 */
	@Bean
	public HandlerMapping defaultServletHandlerMapping() {
		DefaultServletHandlerConfigurer configurer = new DefaultServletHandlerConfigurer(servletContext);
		configureDefaultServletHandling(configurer);
		AbstractHandlerMapping handlerMapping = configurer.getHandlerMapping();
		handlerMapping = handlerMapping != null ? handlerMapping : new EmptyHandlerMapping();
		return handlerMapping;
	}

	/**
	 * Override this method to configure "default" Servlet handling.
	 * <p>
	 *  覆盖此方法配置"默认"Servlet处理
	 * 
	 * 
	 * @see DefaultServletHandlerConfigurer
	 */
	protected void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
	}

	/**
	 * Returns a {@link RequestMappingHandlerAdapter} for processing requests
	 * through annotated controller methods. Consider overriding one of these
	 * other more fine-grained methods:
	 * <ul>
	 * <li>{@link #addArgumentResolvers} for adding custom argument resolvers.
	 * <li>{@link #addReturnValueHandlers} for adding custom return value handlers.
	 * <li>{@link #configureMessageConverters} for adding custom message converters.
	 * </ul>
	 * <p>
	 *  通过带注释的控制器方法返回一个{@link RequestMappingHandlerAdapter}来处理请求考虑覆盖其他更细粒度的方法之一：
	 * <ul>
	 *  <li> {@ link #addArgumentResolvers}添加自定义参数解析器<li> {@ link #addReturnValueHandlers}添加自定义返回值处理程序<li> {@ link #configureMessageConverters}
	 * 添加自定义消息转换器。
	 * </ul>
	 */
	@Bean
	public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
		RequestMappingHandlerAdapter adapter = createRequestMappingHandlerAdapter();
		adapter.setContentNegotiationManager(mvcContentNegotiationManager());
		adapter.setMessageConverters(getMessageConverters());
		adapter.setWebBindingInitializer(getConfigurableWebBindingInitializer());
		adapter.setCustomArgumentResolvers(getArgumentResolvers());
		adapter.setCustomReturnValueHandlers(getReturnValueHandlers());

		if (jackson2Present) {
			adapter.setRequestBodyAdvice(
					Collections.<RequestBodyAdvice>singletonList(new JsonViewRequestBodyAdvice()));
			adapter.setResponseBodyAdvice(
					Collections.<ResponseBodyAdvice<?>>singletonList(new JsonViewResponseBodyAdvice()));
		}

		AsyncSupportConfigurer configurer = new AsyncSupportConfigurer();
		configureAsyncSupport(configurer);
		if (configurer.getTaskExecutor() != null) {
			adapter.setTaskExecutor(configurer.getTaskExecutor());
		}
		if (configurer.getTimeout() != null) {
			adapter.setAsyncRequestTimeout(configurer.getTimeout());
		}
		adapter.setCallableInterceptors(configurer.getCallableInterceptors());
		adapter.setDeferredResultInterceptors(configurer.getDeferredResultInterceptors());

		return adapter;
	}

	/**
	 * Protected method for plugging in a custom subclass of
	 * {@link RequestMappingHandlerAdapter}.
	 * <p>
	 *  插入{@link RequestMappingHandlerAdapter}的自定义子类的受保护方法
	 * 
	 * 
	 * @since 4.3
	 */
	protected RequestMappingHandlerAdapter createRequestMappingHandlerAdapter() {
		return new RequestMappingHandlerAdapter();
	}

	/**
	 * Return the {@link ConfigurableWebBindingInitializer} to use for
	 * initializing all {@link WebDataBinder} instances.
	 * <p>
	 * 返回{@link ConfigurableWebBindingInitializer},用于初始化所有{@link WebDataBinder}实例
	 * 
	 */
	protected ConfigurableWebBindingInitializer getConfigurableWebBindingInitializer() {
		ConfigurableWebBindingInitializer initializer = new ConfigurableWebBindingInitializer();
		initializer.setConversionService(mvcConversionService());
		initializer.setValidator(mvcValidator());
		initializer.setMessageCodesResolver(getMessageCodesResolver());
		return initializer;
	}

	/**
	 * Override this method to provide a custom {@link MessageCodesResolver}.
	 * <p>
	 *  覆盖此方法以提供自定义{@link MessageCodesResolver}
	 * 
	 */
	protected MessageCodesResolver getMessageCodesResolver() {
		return null;
	}

	/**
	 * Override this method to configure asynchronous request processing options.
	 * <p>
	 *  覆盖此方法来配置异步请求处理选项
	 * 
	 * 
	 * @see AsyncSupportConfigurer
	 */
	protected void configureAsyncSupport(AsyncSupportConfigurer configurer) {
	}

	/**
	 * Return a {@link FormattingConversionService} for use with annotated
	 * controller methods and the {@code spring:eval} JSP tag.
	 * Also see {@link #addFormatters} as an alternative to overriding this method.
	 * <p>
	 *  返回一个{@link FormattingConversionService},用于带注释的控制器方法和{@code spring：eval} JSP标记另请参阅{@link #addFormatters}
	 * 作为覆盖此方法的替代方法。
	 * 
	 */
	@Bean
	public FormattingConversionService mvcConversionService() {
		FormattingConversionService conversionService = new DefaultFormattingConversionService();
		addFormatters(conversionService);
		return conversionService;
	}

	/**
	 * Override this method to add custom {@link Converter}s and {@link Formatter}s.
	 * <p>
	 *  覆盖此方法以添加自定义{@link转换器}和{@link格式化程序}
	 * 
	 */
	protected void addFormatters(FormatterRegistry registry) {
	}

	/**
	 * Return a global {@link Validator} instance for example for validating
	 * {@code @ModelAttribute} and {@code @RequestBody} method arguments.
	 * Delegates to {@link #getValidator()} first and if that returns {@code null}
	 * checks the classpath for the presence of a JSR-303 implementations
	 * before creating a {@code OptionalValidatorFactoryBean}.If a JSR-303
	 * implementation is not available, a no-op {@link Validator} is returned.
	 * <p>
	 * 返回一个全局{@link Validator}实例,例如用于验证{@code @ModelAttribute}和{@code @RequestBody}方法参数首先委派给{@link #getValidator()}
	 * ,如果返回{@code null}则检查在创建{@code OptionalValidatorFactoryBean}之前存在JSR-303实现的类路径如果JSR-303实现不可用,则返回no操作{@link Validator}
	 * 。
	 * 
	 */
	@Bean
	public Validator mvcValidator() {
		Validator validator = getValidator();
		if (validator == null) {
			if (ClassUtils.isPresent("javax.validation.Validator", getClass().getClassLoader())) {
				Class<?> clazz;
				try {
					String className = "org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean";
					clazz = ClassUtils.forName(className, WebMvcConfigurationSupport.class.getClassLoader());
				}
				catch (ClassNotFoundException ex) {
					throw new BeanInitializationException("Could not find default validator class", ex);
				}
				catch (LinkageError ex) {
					throw new BeanInitializationException("Could not load default validator class", ex);
				}
				validator = (Validator) BeanUtils.instantiateClass(clazz);
			}
			else {
				validator = new NoOpValidator();
			}
		}
		return validator;
	}

	/**
	 * Override this method to provide a custom {@link Validator}.
	 * <p>
	 *  覆盖此方法以提供自定义{@link Validator}
	 * 
	 */
	protected Validator getValidator() {
		return null;
	}

	/**
	 * Provide access to the shared custom argument resolvers used by the
	 * {@link RequestMappingHandlerAdapter} and the
	 * {@link ExceptionHandlerExceptionResolver}. This method cannot be
	 * overridden, use {@link #addArgumentResolvers(List)} instead.
	 * <p>
	 *  提供对{@link RequestMappingHandlerAdapter}和{@link ExceptionHandlerExceptionResolver}使用的共享自定义参数解析器的访问此方法
	 * 不能被覆盖,而是使用{@link #addArgumentResolvers(List)}。
	 * 
	 * 
	 * @since 4.3
	 */
	protected final List<HandlerMethodArgumentResolver> getArgumentResolvers() {
		if (this.argumentResolvers == null) {
			this.argumentResolvers = new ArrayList<HandlerMethodArgumentResolver>();
			addArgumentResolvers(this.argumentResolvers);
		}
		return this.argumentResolvers;
	}

	/**
	 * Add custom {@link HandlerMethodArgumentResolver}s to use in addition to
	 * the ones registered by default.
	 * <p>Custom argument resolvers are invoked before built-in resolvers
	 * except for those that rely on the presence of annotations (e.g.
	 * {@code @RequestParameter}, {@code @PathVariable}, etc.).
	 * The latter can  be customized by configuring the
	 * {@link RequestMappingHandlerAdapter} directly.
	 * <p>
	 * 添加自定义{@link HandlerMethodArgumentResolver}以用于默认情况下注册的定制<p>自定义参数解析器在内置解析器之前被调用,除了依赖于注释的那些(例如{@code @RequestParameter}
	 * , {@code @PathVariable}等)后者可以通过直接配置{@link RequestMappingHandlerAdapter}进行定制。
	 * 
	 * 
	 * @param argumentResolvers the list of custom converters;
	 * 	initially an empty list.
	 */
	protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
	}

	/**
	 * Provide access to the shared return value handlers used by the
	 * {@link RequestMappingHandlerAdapter} and the
	 * {@link ExceptionHandlerExceptionResolver}. This method cannot be
	 * overridden, use {@link #addReturnValueHandlers(List)} instead.
	 * <p>
	 *  提供对{@link RequestMappingHandlerAdapter}和{@link ExceptionHandlerExceptionResolver}使用的共享返回值处理程序的访问此方法不
	 * 能被覆盖,而是使用{@link #addReturnValueHandlers(List)}。
	 * 
	 * 
	 * @since 4.3
	 */
	protected final List<HandlerMethodReturnValueHandler> getReturnValueHandlers() {
		if (this.returnValueHandlers == null) {
			this.returnValueHandlers = new ArrayList<HandlerMethodReturnValueHandler>();
			addReturnValueHandlers(this.returnValueHandlers);
		}
		return this.returnValueHandlers;
	}

	/**
	 * Add custom {@link HandlerMethodReturnValueHandler}s in addition to the
	 * ones registered by default.
	 * <p>Custom return value handlers are invoked before built-in ones except
	 * for those that rely on the presence of annotations (e.g.
	 * {@code @ResponseBody}, {@code @ModelAttribute}, etc.).
	 * The latter can be customized by configuring the
	 * {@link RequestMappingHandlerAdapter} directly.
	 * <p>
	 * 添加自定义{@link HandlerMethodReturnValueHandler}以及默认情况下注册的自定义{@link HandlerMethodReturnValueHandler} <p>自
	 * 定义返回值处理程序在内置之前被调用,除了那些依赖于注释(例如{@code @ResponseBody},{ @code @ModelAttribute}等)后者可以通过直接配置{@link RequestMappingHandlerAdapter}
	 * 进行定制。
	 * 
	 * 
	 * @param returnValueHandlers the list of custom handlers;
	 * initially an empty list.
	 */
	protected void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
	}

	/**
	 * Provides access to the shared {@link HttpMessageConverter}s used by the
	 * {@link RequestMappingHandlerAdapter} and the
	 * {@link ExceptionHandlerExceptionResolver}.
	 * This method cannot be overridden.
	 * Use {@link #configureMessageConverters(List)} instead.
	 * Also see {@link #addDefaultHttpMessageConverters(List)} that can be
	 * used to add default message converters.
	 * <p>
	 *  提供访问{@link RequestMappingHandlerAdapter}使用的共享{@link HttpMessageConverter}和{@link ExceptionHandlerExceptionResolver}
	 * 此方法不能被覆盖使用{@link #configureMessageConverters(List)}另请参见{@link #addDefaultHttpMessageConverters(列表)},可
	 * 用于添加默认消息转换器。
	 * 
	 */
	protected final List<HttpMessageConverter<?>> getMessageConverters() {
		if (this.messageConverters == null) {
			this.messageConverters = new ArrayList<HttpMessageConverter<?>>();
			configureMessageConverters(this.messageConverters);
			if (this.messageConverters.isEmpty()) {
				addDefaultHttpMessageConverters(this.messageConverters);
			}
			extendMessageConverters(this.messageConverters);
		}
		return this.messageConverters;
	}

	/**
	 * Override this method to add custom {@link HttpMessageConverter}s to use
	 * with the {@link RequestMappingHandlerAdapter} and the
	 * {@link ExceptionHandlerExceptionResolver}. Adding converters to the
	 * list turns off the default converters that would otherwise be registered
	 * by default. Also see {@link #addDefaultHttpMessageConverters(List)} that
	 * can be used to add default message converters.
	 * <p>
	 * 覆盖此方法以添加与{@link RequestMappingHandlerAdapter}和{@link ExceptionHandlerExceptionResolver}一起使用的自定义{@link HttpMessageConverter}
	 * ,将转换器添加到列表中将关闭默认情况下注册的默认转换器。
	 * 另请参阅{@link #addDefaultHttpMessageConverters(List)},可用于添加默认消息转换器。
	 * 
	 * 
	 * @param converters a list to add message converters to;
	 * initially an empty list.
	 */
	protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
	}

	/**
	 * Override this method to extend or modify the list of converters after it
	 * has been configured. This may be useful for example to allow default
	 * converters to be registered and then insert a custom converter through
	 * this method.
	 * <p>
	 *  覆盖此方法以在配置之后扩展或修改转换器列表这可能有用,例如允许注册默认转换器,然后通过此方法插入自定义转换器
	 * 
	 * 
	 * @param converters the list of configured converters to extend.
	 * @since 4.1.3
	 */
	protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
	}

	/**
	 * Adds a set of default HttpMessageConverter instances to the given list.
	 * Subclasses can call this method from {@link #configureMessageConverters(List)}.
	 * <p>
	 *  向给定列表添加一组默认的HttpMessageConverter实例子类可以从{@link #configureMessageConverters(List))调用此方法
	 * 
	 * 
	 * @param messageConverters the list to add the default message converters to
	 */
	protected final void addDefaultHttpMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
		StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
		stringConverter.setWriteAcceptCharset(false);

		messageConverters.add(new ByteArrayHttpMessageConverter());
		messageConverters.add(stringConverter);
		messageConverters.add(new ResourceHttpMessageConverter());
		messageConverters.add(new SourceHttpMessageConverter<Source>());
		messageConverters.add(new AllEncompassingFormHttpMessageConverter());

		if (romePresent) {
			messageConverters.add(new AtomFeedHttpMessageConverter());
			messageConverters.add(new RssChannelHttpMessageConverter());
		}

		if (jackson2XmlPresent) {
			ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.xml().applicationContext(this.applicationContext).build();
			messageConverters.add(new MappingJackson2XmlHttpMessageConverter(objectMapper));
		}
		else if (jaxb2Present) {
			messageConverters.add(new Jaxb2RootElementHttpMessageConverter());
		}

		if (jackson2Present) {
			ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().applicationContext(this.applicationContext).build();
			messageConverters.add(new MappingJackson2HttpMessageConverter(objectMapper));
		}
		else if (gsonPresent) {
			messageConverters.add(new GsonHttpMessageConverter());
		}
	}

	/**
	 * Return an instance of {@link CompositeUriComponentsContributor} for use with
	 * {@link org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder}.
	 * <p>
	 * 返回{@link CompositeUriComponentsContributor}的一个实例,用于{@link orgspringframeworkwebservletmvcmethodannotationMvcUriComponentsBuilder}
	 * 。
	 * 
	 * 
	 * @since 4.0
	 */
	@Bean
	public CompositeUriComponentsContributor mvcUriComponentsContributor() {
		return new CompositeUriComponentsContributor(
				requestMappingHandlerAdapter().getArgumentResolvers(), mvcConversionService());
	}

	/**
	 * Returns a {@link HttpRequestHandlerAdapter} for processing requests
	 * with {@link HttpRequestHandler}s.
	 * <p>
	 *  返回一个{@link HttpRequestHandlerAdapter},用{@link HttpRequestHandler}处理请求
	 * 
	 */
	@Bean
	public HttpRequestHandlerAdapter httpRequestHandlerAdapter() {
		return new HttpRequestHandlerAdapter();
	}

	/**
	 * Returns a {@link SimpleControllerHandlerAdapter} for processing requests
	 * with interface-based controllers.
	 * <p>
	 *  返回一个{@link SimpleControllerHandlerAdapter},用于使用基于界面的控制器处理请求
	 * 
	 */
	@Bean
	public SimpleControllerHandlerAdapter simpleControllerHandlerAdapter() {
		return new SimpleControllerHandlerAdapter();
	}

	/**
	 * Returns a {@link HandlerExceptionResolverComposite} containing a list
	 * of exception resolvers obtained either through
	 * {@link #configureHandlerExceptionResolvers(List)} or through
	 * {@link #addDefaultHandlerExceptionResolvers(List)}.
	 * <p><strong>Note:</strong> This method cannot be made final due to CGLib
	 * constraints. Rather than overriding it, consider overriding
	 * {@link #configureHandlerExceptionResolvers(List)}, which allows
	 * providing a list of resolvers.
	 * <p>
	 * 返回一个{@link HandlerExceptionResolverComposite},其中包含通过{@link #configureHandlerExceptionResolvers(List)}
	 * 或{@link #addDefaultHandlerExceptionResolvers(List))获得的异常解析器列表<p> <strong>注意：</strong>此方法由于CGLib约束,不能做最后的决定而不是覆盖它,考虑覆盖{@link #configureHandlerExceptionResolvers(List)}
	 * ,这允许提供一个解析器列表。
	 * 
	 */
	@Bean
	public HandlerExceptionResolver handlerExceptionResolver() {
		List<HandlerExceptionResolver> exceptionResolvers = new ArrayList<HandlerExceptionResolver>();
		configureHandlerExceptionResolvers(exceptionResolvers);
		if (exceptionResolvers.isEmpty()) {
			addDefaultHandlerExceptionResolvers(exceptionResolvers);
		}
		extendHandlerExceptionResolvers(exceptionResolvers);
		HandlerExceptionResolverComposite composite = new HandlerExceptionResolverComposite();
		composite.setOrder(0);
		composite.setExceptionResolvers(exceptionResolvers);
		return composite;
	}

	/**
	 * Override this method to configure the list of
	 * {@link HandlerExceptionResolver}s to use. Adding resolvers to the list
	 * turns off the default resolvers that would otherwise be registered by
	 * default. Also see {@link #addDefaultHandlerExceptionResolvers(List)}
	 * that can be used to add the default exception resolvers.
	 * <p>
	 *  覆盖此方法以配置{@link HandlerExceptionResolver}列表以使用向列表中添加解析器将关闭默认情况下注册的解析器。
	 * 另请参阅可用于添加的{@link #addDefaultHandlerExceptionResolvers(List)}默认异常解析器。
	 * 
	 * 
	 * @param exceptionResolvers a list to add exception resolvers to;
	 * initially an empty list.
	 */
	protected void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
	}

	/**
	 * Override this method to extend or modify the list of
	 * {@link HandlerExceptionResolver}s after it has been configured. This may
	 * be useful for example to allow default resolvers to be registered and then
	 * insert a custom one through this method.
	 * <p>
	 * 覆盖此方法以在配置之后扩展或修改{@link HandlerExceptionResolver}列表这可能是有用的,例如允许注册默认解析器,然后通过此方法插入自定义解析器
	 * 
	 * 
	 * @param exceptionResolvers the list of configured resolvers to extend.
	 * @since 4.3
	 */
	protected void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
	}

	/**
	 * A method available to subclasses for adding default {@link HandlerExceptionResolver}s.
	 * <p>Adds the following exception resolvers:
	 * <ul>
	 * <li>{@link ExceptionHandlerExceptionResolver}
	 * for handling exceptions through @{@link ExceptionHandler} methods.
	 * <li>{@link ResponseStatusExceptionResolver}
	 * for exceptions annotated with @{@link ResponseStatus}.
	 * <li>{@link DefaultHandlerExceptionResolver}
	 * for resolving known Spring exception types
	 * </ul>
	 * <p>
	 *  可用于添加默认{@link HandlerExceptionResolver}的子类的方法<p>添加以下异常解析器：
	 * <ul>
	 *  通过@ {@ link ExceptionHandler}方法来处理异常的<li> {@ link ExceptionHandlerExceptionResolver}用于解决已知的Spring异常的
	 * @ {@ link ResponseStatus} <li> {@ link DefaultHandlerExceptionResolver}注释的异常的<li> {@ link ResponseStatusExceptionResolver}
	 * 类型。
	 * </ul>
	 */
	protected final void addDefaultHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
		ExceptionHandlerExceptionResolver exceptionHandlerResolver = createExceptionHandlerExceptionResolver();
		exceptionHandlerResolver.setContentNegotiationManager(mvcContentNegotiationManager());
		exceptionHandlerResolver.setMessageConverters(getMessageConverters());
		exceptionHandlerResolver.setCustomArgumentResolvers(getArgumentResolvers());
		exceptionHandlerResolver.setCustomReturnValueHandlers(getReturnValueHandlers());
		if (jackson2Present) {
			exceptionHandlerResolver.setResponseBodyAdvice(
					Collections.<ResponseBodyAdvice<?>>singletonList(new JsonViewResponseBodyAdvice()));
		}
		exceptionHandlerResolver.setApplicationContext(this.applicationContext);
		exceptionHandlerResolver.afterPropertiesSet();
		exceptionResolvers.add(exceptionHandlerResolver);

		ResponseStatusExceptionResolver responseStatusResolver = new ResponseStatusExceptionResolver();
		responseStatusResolver.setMessageSource(this.applicationContext);
		exceptionResolvers.add(responseStatusResolver);

		exceptionResolvers.add(new DefaultHandlerExceptionResolver());
	}

	/**
	 * Protected method for plugging in a custom subclass of
	 * {@link ExceptionHandlerExceptionResolver}.
	 * <p>
	 *  插入{@link ExceptionHandlerExceptionResolver}的自定义子类的受保护方法
	 * 
	 * 
	 * @since 4.3
	 */
	protected ExceptionHandlerExceptionResolver createExceptionHandlerExceptionResolver() {
		return new ExceptionHandlerExceptionResolver();
	}

	/**
	 * Register a {@link ViewResolverComposite} that contains a chain of view resolvers
	 * to use for view resolution.
	 * By default this resolver is ordered at 0 unless content negotiation view
	 * resolution is used in which case the order is raised to
	 * {@link org.springframework.core.Ordered#HIGHEST_PRECEDENCE
	 * Ordered.HIGHEST_PRECEDENCE}.
	 * <p>If no other resolvers are configured,
	 * {@link ViewResolverComposite#resolveViewName(String, Locale)} returns null in order
	 * to allow other potential {@link ViewResolver} beans to resolve views.
	 * <p>
	 * 注册一个{@link ViewResolverComposite},其中包含用于视图解析的视图解析器链默认情况下,此解析器按0排序,除非使用内容协商视图分辨率,在这种情况下,订单将提交到{@link orgspringframeworkcoreOrdered#HIGHEST_PRECEDENCE OrderedHIGHEST_PRECEDENCE}
	 *  <p>如果没有配置其他解析器,{@link ViewResolverComposite#resolveViewName(String,Locale)}返回null,以便允许其他潜在的{@link ViewResolver}
	 *  bean解析视图。
	 * 
	 * 
	 * @since 4.1
	 */
	@Bean
	public ViewResolver mvcViewResolver() {
		ViewResolverRegistry registry = new ViewResolverRegistry();
		registry.setContentNegotiationManager(mvcContentNegotiationManager());
		registry.setApplicationContext(this.applicationContext);
		configureViewResolvers(registry);

		if (registry.getViewResolvers().isEmpty()) {
			String[] names = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
					this.applicationContext, ViewResolver.class, true, false);
			if (names.length == 1) {
				registry.getViewResolvers().add(new InternalResourceViewResolver());
			}
		}

		ViewResolverComposite composite = new ViewResolverComposite();
		composite.setOrder(registry.getOrder());
		composite.setViewResolvers(registry.getViewResolvers());
		composite.setApplicationContext(this.applicationContext);
		composite.setServletContext(this.servletContext);
		return composite;
	}

	/**
	 * Override this method to configure view resolution.
	 * <p>
	 *  覆盖此方法配置视图分辨率
	 * 
	 * 
	 * @see ViewResolverRegistry
	 */
	protected void configureViewResolvers(ViewResolverRegistry registry) {
	}

	/**
	/* <p>
	/* 
	 * @since 4.2
	 */
	protected final Map<String, CorsConfiguration> getCorsConfigurations() {
		if (this.corsConfigurations == null) {
			CorsRegistry registry = new CorsRegistry();
			addCorsMappings(registry);
			this.corsConfigurations = registry.getCorsConfigurations();
		}
		return this.corsConfigurations;
	}

	/**
	 * Override this method to configure cross origin requests processing.
	 * <p>
	 *  覆盖此方法来配置交叉原点请求处理
	 * 
	 * @since 4.2
	 * @see CorsRegistry
	 */
	protected void addCorsMappings(CorsRegistry registry) {
	}


	private static final class EmptyHandlerMapping extends AbstractHandlerMapping {

		@Override
		protected Object getHandlerInternal(HttpServletRequest request) {
			return null;
		}
	}


	private static final class NoOpValidator implements Validator {

		@Override
		public boolean supports(Class<?> clazz) {
			return false;
		}

		@Override
		public void validate(Object target, Errors errors) {
		}
	}

}
