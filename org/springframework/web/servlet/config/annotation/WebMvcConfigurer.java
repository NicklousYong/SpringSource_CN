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

import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

/**
 * Defines callback methods to customize the Java-based configuration for
 * Spring MVC enabled via {@code @EnableWebMvc}.
 *
 * <p>{@code @EnableWebMvc}-annotated configuration classes may implement
 * this interface to be called back and given a chance to customize the
 * default configuration. Consider extending {@link WebMvcConfigurerAdapter},
 * which provides a stub implementation of all interface methods.
 *
 * <p>
 *  定义回调方法,通过{@code @EnableWebMvc}自定义Spring MVC的基于Java的配置
 * 
 * 假设配置类可以实现该接口被回调,并有机会自定义默认配置考虑扩展{@link WebMvcConfigurerAdapter},它提供了所有接口方法的存根实现
 * 
 * 
 * @author Rossen Stoyanchev
 * @author Keith Donald
 * @author David Syer
 * @since 3.1
 */
public interface WebMvcConfigurer {

	/**
	 * Helps with configuring HandlerMappings path matching options such as trailing slash match,
	 * suffix registration, path matcher and path helper.
	 * Configured path matcher and path helper instances are shared for:
	 * <ul>
	 * <li>RequestMappings</li>
	 * <li>ViewControllerMappings</li>
	 * <li>ResourcesMappings</li>
	 * </ul>
	 * <p>
	 *  帮助配置HandlerMappings路径匹配选项,如尾部斜杠匹配,后缀注册,路径匹配器和路径助手配置路径匹配器和路径助手实例共享：
	 * <ul>
	 *  <li> RequestMappings </li> <li> ViewControllerMappings </li> <li> ResourcesMappings </li>
	 * </ul>
	 * 
	 * @since 4.0.3
	 */
	void configurePathMatch(PathMatchConfigurer configurer);

	/**
	 * Configure content negotiation options.
	 * <p>
	 *  配置内容协商选项
	 * 
	 */
	void configureContentNegotiation(ContentNegotiationConfigurer configurer);

	/**
	 * Configure asynchronous request handling options.
	 * <p>
	 *  配置异步请求处理选项
	 * 
	 */
	void configureAsyncSupport(AsyncSupportConfigurer configurer);

	/**
	 * Configure a handler to delegate unhandled requests by forwarding to the
	 * Servlet container's "default" servlet. A common use case for this is when
	 * the {@link DispatcherServlet} is mapped to "/" thus overriding the
	 * Servlet container's default handling of static resources.
	 * <p>
	 * 配置一个处理程序通过转发到Servlet容器的"默认"servlet来委托未处理的请求一个常见的用例是将{@link DispatcherServlet}映射到"/",因此覆盖了Servlet容器对静态
	 * 资源的默认处理。
	 * 
	 */
	void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer);

	/**
	 * Add {@link Converter}s and {@link Formatter}s in addition to the ones
	 * registered by default.
	 * <p>
	 *  添加{@link转换器}和{@link格式化器}以及默认情况下注册的
	 * 
	 */
	void addFormatters(FormatterRegistry registry);

	/**
	 * Add Spring MVC lifecycle interceptors for pre- and post-processing of
	 * controller method invocations. Interceptors can be registered to apply
	 * to all requests or be limited to a subset of URL patterns.
	 * <p><strong>Note</strong> that interceptors registered here only apply to
	 * controllers and not to resource handler requests. To intercept requests for
	 * static resources either declare a
	 * {@link org.springframework.web.servlet.handler.MappedInterceptor MappedInterceptor}
	 * bean or switch to advanced configuration mode by extending
	 * {@link org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
	 * WebMvcConfigurationSupport} and then override {@code resourceHandlerMapping}.
	 * <p>
	 * 添加Spring MVC生命周期拦截器,用于控制器方法调用的前处理和后处理拦截器可以注册为适用于所有请求,也可以被限制在URL模式的一个子集<p> <strong>注意</strong>适用于控制器而不
	 * 是资源处理程序请求拦截对静态资源的请求,通过扩展{@link orgspringframeworkwebservletconfigannotationWebMvcConfigurationSupport WebMvcConfigurationSupport}
	 * 声明一个{@link orgspringframeworkwebservlethandlerMappedInterceptor MappedInterceptor} bean或切换到高级配置模式,然后覆
	 * 盖{@code resourceHandlerMapping}。
	 * 
	 */
	void addInterceptors(InterceptorRegistry registry);

	/**
	 * Add handlers to serve static resources such as images, js, and, css
	 * files from specific locations under web application root, the classpath,
	 * and others.
	 * <p>
	 * 添加处理程序来提供静态资源,如web应用程序根目录下的特定位置的图像,js和css文件,类路径和其他
	 * 
	 */
	void addResourceHandlers(ResourceHandlerRegistry registry);

	/**
	 * Configure cross origin requests processing.
	 * <p>
	 *  配置交叉原点请求处理
	 * 
	 * 
	 * @since 4.2
	 */
	void addCorsMappings(CorsRegistry registry);

	/**
	 * Configure simple automated controllers pre-configured with the response
	 * status code and/or a view to render the response body. This is useful in
	 * cases where there is no need for custom controller logic -- e.g. render a
	 * home page, perform simple site URL redirects, return a 404 status with
	 * HTML content, a 204 with no content, and more.
	 * <p>
	 *  配置使用响应状态代码预配置的简单自动控制器和/或呈现响应体的视图在不需要自定义控制器逻辑的情况下(例如,渲染主页,执行简单的站点URL重定向)使用HTML内容返回404状态,无内容的204状态等
	 * 
	 */
	void addViewControllers(ViewControllerRegistry registry);

	/**
	 * Configure view resolvers to translate String-based view names returned from
	 * controllers into concrete {@link org.springframework.web.servlet.View}
	 * implementations to perform rendering with.
	 * <p>
	 *  配置视图解析器以将从控制器返回的基于String的视图名称转换为具体的{@link orgspringframeworkwebservletView}实现,以执行渲染
	 * 
	 * 
	 * @since 4.1
	 */
	void configureViewResolvers(ViewResolverRegistry registry);

	/**
	 * Add resolvers to support custom controller method argument types.
	 * <p>This does not override the built-in support for resolving handler
	 * method arguments. To customize the built-in support for argument
	 * resolution, configure {@link RequestMappingHandlerAdapter} directly.
	 * <p>
	 * 添加解析器以支持自定义控制器方法参数类型<p>这不会覆盖解析处理程序方法参数的内置支持要自定义内置的参数解析支持,请直接配置{@link RequestMappingHandlerAdapter}
	 * 
	 * 
	 * @param argumentResolvers initially an empty list
	 */
	void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers);

	/**
	 * Add handlers to support custom controller method return value types.
	 * <p>Using this option does not override the built-in support for handling
	 * return values. To customize the built-in support for handling return
	 * values, configure RequestMappingHandlerAdapter directly.
	 * <p>
	 *  添加处理程序来支持自定义控制器方法返回值类型<p>使用此选项不会覆盖内置的处理返回值的支持要自定义内置的处理返回值的支持,请直接配置RequestMappingHandlerAdapter
	 * 
	 * 
	 * @param returnValueHandlers initially an empty list
	 */
	void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers);

	/**
	 * Configure the {@link HttpMessageConverter}s to use for reading or writing
	 * to the body of the request or response. If no converters are added, a
	 * default list of converters is registered.
	 * <p><strong>Note</strong> that adding converters to the list, turns off
	 * default converter registration. To simply add a converter without impacting
	 * default registration, consider using the method
	 * {@link #extendMessageConverters(java.util.List)} instead.
	 * <p>
	 * 配置{@link HttpMessageConverter}用于读取或写入请求或响应的正文如果没有添加转换器,则会注册默认的转换器列表<p> <strong>注意</strong>将转换器添加到列表,关
	 * 闭默认转换器注册要简单地添加转换器而不影响默认注册,请考虑使用方法{@link #extendMessageConverters(javautilList)})。
	 * 
	 * 
	 * @param converters initially an empty list of converters
	 */
	void configureMessageConverters(List<HttpMessageConverter<?>> converters);

	/**
	 * A hook for extending or modifying the list of converters after it has been
	 * configured. This may be useful for example to allow default converters to
	 * be registered and then insert a custom converter through this method.
	 * <p>
	 *  用于在配置之后扩展或修改转换器列表的钩子这可能是有用的,例如允许默认转换器注册,然后通过此方法插入自定义转换器
	 * 
	 * 
	 * @param converters the list of configured converters to extend.
	 * @since 4.1.3
	 */
	void extendMessageConverters(List<HttpMessageConverter<?>> converters);

	/**
	 * Configure the {@link HandlerExceptionResolver}s to handle unresolved
	 * controller exceptions. If no resolvers are added to the list, default
	 * exception resolvers are added instead.
	 * <p>
	 * 配置{@link HandlerExceptionResolver}以处理未解决的控制器异常如果没有将解析器添加到列表中,则会添加默认异常解析程序
	 * 
	 * 
	 * @param exceptionResolvers initially an empty list
	 */
	void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers);

	/**
	 * A hook for extending or modifying the list of {@link HandlerExceptionResolver}s
	 * after it has been configured. This may be useful for example to allow default
	 * resolvers to be registered and then insert a custom one through this method.
	 * <p>
	 *  配置后,用于扩展或修改{@link HandlerExceptionResolver}列表的钩子例如,可以注册允许默认解析器,然后通过此方法插入自定义解析器
	 * 
	 * 
	 * @param exceptionResolvers the list of configured resolvers to extend
	 * @since 4.3
	 */
	void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers);

	/**
	 * Provide a custom {@link Validator} instead of the one created by default.
	 * The default implementation, assuming JSR-303 is on the classpath, is:
	 * {@link org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean}.
	 * Leave the return value as {@code null} to keep the default.
	 * <p>
	 *  提供自定义{@link验证器},而不是默认创建的默认实现,假设JSR-303位于类路径上,是：{@link orgspringframeworkvalidationbeanvalidationOptionalValidatorFactoryBean}
	 * 将返回值保留为{@code null}以保持默认值。
	 * 
	 */
	Validator getValidator();

	/**
	 * Provide a custom {@link MessageCodesResolver} for building message codes
	 * from data binding and validation error codes. Leave the return value as
	 * {@code null} to keep the default.
	 * <p>
	 */
	MessageCodesResolver getMessageCodesResolver();

}
