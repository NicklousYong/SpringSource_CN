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

package org.springframework.web.servlet.mvc.method.annotation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Source;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils.MethodFilter;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.DefaultDataBinderFactory;
import org.springframework.web.bind.support.DefaultSessionAttributeStore;
import org.springframework.web.bind.support.SessionAttributeStore;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncWebRequest;
import org.springframework.web.context.request.async.CallableProcessingInterceptor;
import org.springframework.web.context.request.async.DeferredResultProcessingInterceptor;
import org.springframework.web.context.request.async.WebAsyncManager;
import org.springframework.web.context.request.async.WebAsyncTask;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.method.ControllerAdviceBean;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ErrorsMethodArgumentResolver;
import org.springframework.web.method.annotation.ExpressionValueMethodArgumentResolver;
import org.springframework.web.method.annotation.InitBinderDataBinderFactory;
import org.springframework.web.method.annotation.MapMethodProcessor;
import org.springframework.web.method.annotation.ModelAttributeMethodProcessor;
import org.springframework.web.method.annotation.ModelFactory;
import org.springframework.web.method.annotation.ModelMethodProcessor;
import org.springframework.web.method.annotation.RequestHeaderMapMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestHeaderMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestParamMapMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;
import org.springframework.web.method.annotation.SessionAttributesHandler;
import org.springframework.web.method.annotation.SessionStatusMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import org.springframework.web.method.support.InvocableHandlerMethod;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.annotation.ModelAndViewResolver;
import org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.WebUtils;

/**
 * An {@link AbstractHandlerMethodAdapter} that supports {@link HandlerMethod}s
 * with their method argument and return type signature, as defined via
 * {@code @RequestMapping}.
 *
 * <p>Support for custom argument and return value types can be added via
 * {@link #setCustomArgumentResolvers} and {@link #setCustomReturnValueHandlers}.
 * Or alternatively, to re-configure all argument and return value types,
 * use {@link #setArgumentResolvers} and {@link #setReturnValueHandlers}.
 *
 * <p>
 *  一个{@link AbstractHandlerMethodAdapter},通过{@code @RequestMapping}定义,支持{@link HandlerMethod}的方法参数和返回类型
 * 签名,。
 * 
 * <p>可以通过{@link #setCustomArgumentResolvers}和{@link #setCustomReturnValueHandlers}添加对自定义参数和返回值类型的支持,或者,
 * 要重新配置所有参数和返回值类型,请使用{@link #setArgumentResolvers}和{@link #setReturnValueHandlers}。
 * 
 * 
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @since 3.1
 * @see HandlerMethodArgumentResolver
 * @see HandlerMethodReturnValueHandler
 */
public class RequestMappingHandlerAdapter extends AbstractHandlerMethodAdapter
		implements BeanFactoryAware, InitializingBean {

	private List<HandlerMethodArgumentResolver> customArgumentResolvers;

	private HandlerMethodArgumentResolverComposite argumentResolvers;

	private HandlerMethodArgumentResolverComposite initBinderArgumentResolvers;

	private List<HandlerMethodReturnValueHandler> customReturnValueHandlers;

	private HandlerMethodReturnValueHandlerComposite returnValueHandlers;

	private List<ModelAndViewResolver> modelAndViewResolvers;

	private ContentNegotiationManager contentNegotiationManager = new ContentNegotiationManager();

	private List<HttpMessageConverter<?>> messageConverters;

	private List<Object> requestResponseBodyAdvice = new ArrayList<Object>();

	private WebBindingInitializer webBindingInitializer;

	private AsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor("MvcAsync");

	private Long asyncRequestTimeout;

	private CallableProcessingInterceptor[] callableInterceptors = new CallableProcessingInterceptor[0];

	private DeferredResultProcessingInterceptor[] deferredResultInterceptors = new DeferredResultProcessingInterceptor[0];

	private boolean ignoreDefaultModelOnRedirect = false;

	private int cacheSecondsForSessionAttributeHandlers = 0;

	private boolean synchronizeOnSession = false;

	private SessionAttributeStore sessionAttributeStore = new DefaultSessionAttributeStore();

	private ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

	private ConfigurableBeanFactory beanFactory;


	private final Map<Class<?>, SessionAttributesHandler> sessionAttributesHandlerCache =
			new ConcurrentHashMap<Class<?>, SessionAttributesHandler>(64);

	private final Map<Class<?>, Set<Method>> initBinderCache = new ConcurrentHashMap<Class<?>, Set<Method>>(64);

	private final Map<ControllerAdviceBean, Set<Method>> initBinderAdviceCache =
			new LinkedHashMap<ControllerAdviceBean, Set<Method>>();

	private final Map<Class<?>, Set<Method>> modelAttributeCache = new ConcurrentHashMap<Class<?>, Set<Method>>(64);

	private final Map<ControllerAdviceBean, Set<Method>> modelAttributeAdviceCache =
			new LinkedHashMap<ControllerAdviceBean, Set<Method>>();


	public RequestMappingHandlerAdapter() {
		StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
		stringHttpMessageConverter.setWriteAcceptCharset(false);  // see SPR-7316

		this.messageConverters = new ArrayList<HttpMessageConverter<?>>(4);
		this.messageConverters.add(new ByteArrayHttpMessageConverter());
		this.messageConverters.add(stringHttpMessageConverter);
		this.messageConverters.add(new SourceHttpMessageConverter<Source>());
		this.messageConverters.add(new AllEncompassingFormHttpMessageConverter());
	}


	/**
	 * Provide resolvers for custom argument types. Custom resolvers are ordered
	 * after built-in ones. To override the built-in support for argument
	 * resolution use {@link #setArgumentResolvers} instead.
	 * <p>
	 *  为自定义参数类型提供解析器自定义解析器在内置之后排序要替代内置的参数解析支持,请使用{@link #setArgumentResolvers}
	 * 
	 */
	public void setCustomArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		this.customArgumentResolvers = argumentResolvers;
	}

	/**
	 * Return the custom argument resolvers, or {@code null}.
	 * <p>
	 *  返回自定义参数解析器,或{@code null}
	 * 
	 */
	public List<HandlerMethodArgumentResolver> getCustomArgumentResolvers() {
		return this.customArgumentResolvers;
	}

	/**
	 * Configure the complete list of supported argument types thus overriding
	 * the resolvers that would otherwise be configured by default.
	 * <p>
	 *  配置支持的参数类型的完整列表,从而覆盖默认情况下配置的解析器
	 * 
	 */
	public void setArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		if (argumentResolvers == null) {
			this.argumentResolvers = null;
		}
		else {
			this.argumentResolvers = new HandlerMethodArgumentResolverComposite();
			this.argumentResolvers.addResolvers(argumentResolvers);
		}
	}

	/**
	 * Return the configured argument resolvers, or possibly {@code null} if
	 * not initialized yet via {@link #afterPropertiesSet()}.
	 * <p>
	 * 返回配置的参数解析器,或者{@code null}(如果尚未通过{@link #afterPropertiesSet()}初始化)
	 * 
	 */
	public List<HandlerMethodArgumentResolver> getArgumentResolvers() {
		return (this.argumentResolvers != null) ? this.argumentResolvers.getResolvers() : null;
	}

	/**
	 * Configure the supported argument types in {@code @InitBinder} methods.
	 * <p>
	 *  在{@code @InitBinder}方法中配置支持的参数类型
	 * 
	 */
	public void setInitBinderArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		if (argumentResolvers == null) {
			this.initBinderArgumentResolvers = null;
		}
		else {
			this.initBinderArgumentResolvers = new HandlerMethodArgumentResolverComposite();
			this.initBinderArgumentResolvers.addResolvers(argumentResolvers);
		}
	}

	/**
	 * Return the argument resolvers for {@code @InitBinder} methods, or possibly
	 * {@code null} if not initialized yet via {@link #afterPropertiesSet()}.
	 * <p>
	 *  返回{@code @InitBinder}方法的参数解析器,或者如果没有通过{@link #afterPropertiesSet())初始化{@code null}
	 * 
	 */
	public List<HandlerMethodArgumentResolver> getInitBinderArgumentResolvers() {
		return (this.initBinderArgumentResolvers != null) ? this.initBinderArgumentResolvers.getResolvers() : null;
	}

	/**
	 * Provide handlers for custom return value types. Custom handlers are
	 * ordered after built-in ones. To override the built-in support for
	 * return value handling use {@link #setReturnValueHandlers}.
	 * <p>
	 *  为自定义返回值类型提供处理程序自定义处理程序在内置的后面进行排序要覆盖返回值处理的内置支持,请使用{@link #setReturnValueHandlers}
	 * 
	 */
	public void setCustomReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
		this.customReturnValueHandlers = returnValueHandlers;
	}

	/**
	 * Return the custom return value handlers, or {@code null}.
	 * <p>
	 *  返回自定义返回值处理程序,或{@code null}
	 * 
	 */
	public List<HandlerMethodReturnValueHandler> getCustomReturnValueHandlers() {
		return this.customReturnValueHandlers;
	}

	/**
	 * Configure the complete list of supported return value types thus
	 * overriding handlers that would otherwise be configured by default.
	 * <p>
	 *  配置支持的返回值类型的完整列表,从而覆盖默认情况下配置的处理程序
	 * 
	 */
	public void setReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
		if (returnValueHandlers == null) {
			this.returnValueHandlers = null;
		}
		else {
			this.returnValueHandlers = new HandlerMethodReturnValueHandlerComposite();
			this.returnValueHandlers.addHandlers(returnValueHandlers);
		}
	}

	/**
	 * Return the configured handlers, or possibly {@code null} if not
	 * initialized yet via {@link #afterPropertiesSet()}.
	 * <p>
	 * 返回配置的处理程序,或者{@code null}(如果尚未通过{@link #afterPropertiesSet()}初始化)
	 * 
	 */
	public List<HandlerMethodReturnValueHandler> getReturnValueHandlers() {
		return this.returnValueHandlers.getHandlers();
	}

	/**
	 * Provide custom {@link ModelAndViewResolver}s.
	 * <p><strong>Note:</strong> This method is available for backwards
	 * compatibility only. However, it is recommended to re-write a
	 * {@code ModelAndViewResolver} as {@link HandlerMethodReturnValueHandler}.
	 * An adapter between the two interfaces is not possible since the
	 * {@link HandlerMethodReturnValueHandler#supportsReturnType} method
	 * cannot be implemented. Hence {@code ModelAndViewResolver}s are limited
	 * to always being invoked at the end after all other return value
	 * handlers have been given a chance.
	 * <p>A {@code HandlerMethodReturnValueHandler} provides better access to
	 * the return type and controller method information and can be ordered
	 * freely relative to other return value handlers.
	 * <p>
	 * 提供自定义{@link ModelAndViewResolver} s <p> <strong>注意：</strong>此方法仅适用于向后兼容性但是,建议将{@code ModelAndViewResolver}
	 * 重写为{@link HandlerMethodReturnValueHandler} An两个接口之间的适配器是不可能的,因为{@link HandlerMethodReturnValueHandler#supportsReturnType}
	 * 方法无法实现。
	 * 因此,{@code ModelAndViewResolver}被限制为在所有其他返回值处理程序被赋予机会之后最后一直被调用> A {@code HandlerMethodReturnValueHandler}
	 * 可以更好地访问返回类型和控制器方法信息,并可以相对于其他返回值处理程序自由排序。
	 * 
	 */
	public void setModelAndViewResolvers(List<ModelAndViewResolver> modelAndViewResolvers) {
		this.modelAndViewResolvers = modelAndViewResolvers;
	}

	/**
	 * Return the configured {@link ModelAndViewResolver}s, or {@code null}.
	 * <p>
	 * 返回配置的{@link ModelAndViewResolver}或{@code null}
	 * 
	 */
	public List<ModelAndViewResolver> getModelAndViewResolvers() {
		return modelAndViewResolvers;
	}

	/**
	 * Set the {@link ContentNegotiationManager} to use to determine requested media types.
	 * If not set, the default constructor is used.
	 * <p>
	 *  设置{@link ContentNegotiationManager}以用于确定请求的媒体类型如果未设置,则使用默认构造函数
	 * 
	 */
	public void setContentNegotiationManager(ContentNegotiationManager contentNegotiationManager) {
		this.contentNegotiationManager = contentNegotiationManager;
	}

	/**
	 * Provide the converters to use in argument resolvers and return value
	 * handlers that support reading and/or writing to the body of the
	 * request and response.
	 * <p>
	 *  提供转换器用于参数解析器和返回值处理程序,以支持读取和/或写入请求和响应的正文
	 * 
	 */
	public void setMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
		this.messageConverters = messageConverters;
	}

	/**
	 * Return the configured message body converters.
	 * <p>
	 *  返回配置的消息体转换器
	 * 
	 */
	public List<HttpMessageConverter<?>> getMessageConverters() {
		return this.messageConverters;
	}

	/**
	 * Add one or more {@code RequestBodyAdvice} instances to intercept the
	 * request before it is read and converted for {@code @RequestBody} and
	 * {@code HttpEntity} method arguments.
	 * <p>
	 *  在{@code @RequestBody}和{@code HttpEntity}方法参数读取和转换之前,添加一个或多个{@code RequestBodyAdvice}实例来截取请求
	 * 
	 */
	public void setRequestBodyAdvice(List<RequestBodyAdvice> requestBodyAdvice) {
		if (requestBodyAdvice != null) {
			this.requestResponseBodyAdvice.addAll(requestBodyAdvice);
		}
	}

	/**
	 * Add one or more {@code ResponseBodyAdvice} instances to intercept the
	 * response before {@code @ResponseBody} or {@code ResponseEntity} return
	 * values are written to the response body.
	 * <p>
	 *  在{@code @ResponseBody}或{@code ResponseEntity}返回值写入响应主体之前添加一个或多个{@code ResponseBodyAdvice}实例来截取响应
	 * 
	 */
	public void setResponseBodyAdvice(List<ResponseBodyAdvice<?>> responseBodyAdvice) {
		if (responseBodyAdvice != null) {
			this.requestResponseBodyAdvice.addAll(responseBodyAdvice);
		}
	}

	/**
	 * Provide a WebBindingInitializer with "global" initialization to apply
	 * to every DataBinder instance.
	 * <p>
	 * 为WebBindingInitializer提供"全局"初始化以应用于每个DataBinder实例
	 * 
	 */
	public void setWebBindingInitializer(WebBindingInitializer webBindingInitializer) {
		this.webBindingInitializer = webBindingInitializer;
	}

	/**
	 * Return the configured WebBindingInitializer, or {@code null} if none.
	 * <p>
	 *  返回配置的WebBindingInitializer,否则返回{@code null}
	 * 
	 */
	public WebBindingInitializer getWebBindingInitializer() {
		return this.webBindingInitializer;
	}

	/**
	 * Set the default {@link AsyncTaskExecutor} to use when a controller method
	 * return a {@link Callable}. Controller methods can override this default on
	 * a per-request basis by returning an {@link WebAsyncTask}.
	 * <p>By default a {@link SimpleAsyncTaskExecutor} instance is used.
	 * It's recommended to change that default in production as the simple executor
	 * does not re-use threads.
	 * <p>
	 *  设置默认{@link AsyncTaskExecutor}以在控制器方法返回{@link Callable}控制器方法时可以通过返回{@link WebAsyncTask} <p>根据每个请求覆盖此默
	 * 认值。
	 * 默认情况下,{@link SimpleAsyncTaskExecutor}实例被使用建议在生产中更改默认值,因为简单执行器不会重新使用线程。
	 * 
	 */
	public void setTaskExecutor(AsyncTaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	/**
	 * Specify the amount of time, in milliseconds, before concurrent handling
	 * should time out. In Servlet 3, the timeout begins after the main request
	 * processing thread has exited and ends when the request is dispatched again
	 * for further processing of the concurrently produced result.
	 * <p>If this value is not set, the default timeout of the underlying
	 * implementation is used, e.g. 10 seconds on Tomcat with Servlet 3.
	 * <p>
	 * 指定在并发处理应超时之前的时间量(以毫秒为单位)在Servlet 3中,超时在主请求处理线程退出之后开始,并在再次调度请求以进一步处理并发产生的结果时结束<p> If此值未设置,使用底层实现的默认超时时
	 * 间,例如Tomcat上使用Servlet 3的10秒。
	 * 
	 * 
	 * @param timeout the timeout value in milliseconds
	 */
	public void setAsyncRequestTimeout(long timeout) {
		this.asyncRequestTimeout = timeout;
	}

	/**
	 * Configure {@code CallableProcessingInterceptor}'s to register on async requests.
	 * <p>
	 *  配置{@code CallableProcessingInterceptor}以注册异步请求
	 * 
	 * 
	 * @param interceptors the interceptors to register
	 */
	public void setCallableInterceptors(List<CallableProcessingInterceptor> interceptors) {
		Assert.notNull(interceptors);
		this.callableInterceptors = interceptors.toArray(new CallableProcessingInterceptor[interceptors.size()]);
	}

	/**
	 * Configure {@code DeferredResultProcessingInterceptor}'s to register on async requests.
	 * <p>
	 *  配置{@code DeferredResultProcessingInterceptor}以注册异步请求
	 * 
	 * 
	 * @param interceptors the interceptors to register
	 */
	public void setDeferredResultInterceptors(List<DeferredResultProcessingInterceptor> interceptors) {
		Assert.notNull(interceptors);
		this.deferredResultInterceptors = interceptors.toArray(new DeferredResultProcessingInterceptor[interceptors.size()]);
	}

	/**
	 * By default the content of the "default" model is used both during
	 * rendering and redirect scenarios. Alternatively a controller method
	 * can declare a {@link RedirectAttributes} argument and use it to provide
	 * attributes for a redirect.
	 * <p>Setting this flag to {@code true} guarantees the "default" model is
	 * never used in a redirect scenario even if a RedirectAttributes argument
	 * is not declared. Setting it to {@code false} means the "default" model
	 * may be used in a redirect if the controller method doesn't declare a
	 * RedirectAttributes argument.
	 * <p>The default setting is {@code false} but new applications should
	 * consider setting it to {@code true}.
	 * <p>
	 * 默认情况下,在渲染和重定向情况下都使用"默认"模型的内容。
	 * 或者,控制器方法可以声明一个{@link RedirectAttributes}参数,并使用它来为重定向提供属性<p>将此标志设置为{@code true}保证在重定向方案中从未使用"默认"模型,即使没
	 * 有声明RedirectAttributes参数将其设置为{@code false}也就是说,如果控制器方法不会在重定向中使用"默认"模式声明RedirectAttributes参数<p>默认设置为{@code false}
	 * ,但新应用程序应考虑将其设置为{@code true}。
	 * 默认情况下,在渲染和重定向情况下都使用"默认"模型的内容。
	 * 
	 * 
	 * @see RedirectAttributes
	 */
	public void setIgnoreDefaultModelOnRedirect(boolean ignoreDefaultModelOnRedirect) {
		this.ignoreDefaultModelOnRedirect = ignoreDefaultModelOnRedirect;
	}

	/**
	 * Specify the strategy to store session attributes with. The default is
	 * {@link org.springframework.web.bind.support.DefaultSessionAttributeStore},
	 * storing session attributes in the HttpSession with the same attribute
	 * name as in the model.
	 * <p>
	 * 指定存储会话属性的策略默认为{@link orgspringframeworkwebbindsupportDefaultSessionAttributeStore},将会话属性存储在HttpSessio
	 * n中,具有与模型中相同的属性名称。
	 * 
	 */
	public void setSessionAttributeStore(SessionAttributeStore sessionAttributeStore) {
		this.sessionAttributeStore = sessionAttributeStore;
	}

	/**
	 * Cache content produced by {@code @SessionAttributes} annotated handlers
	 * for the given number of seconds.
	 * <p>Possible values are:
	 * <ul>
	 * <li>-1: no generation of cache-related headers</li>
	 * <li>0 (default value): "Cache-Control: no-store" will prevent caching</li>
	 * <li>1 or higher: "Cache-Control: max-age=seconds" will ask to cache content;
	 * not advised when dealing with session attributes</li>
	 * </ul>
	 * <p>In contrast to the "cacheSeconds" property which will apply to all general
	 * handlers (but not to {@code @SessionAttributes} annotated handlers),
	 * this setting will apply to {@code @SessionAttributes} handlers only.
	 * <p>
	 *  由{@code @SessionAttributes}标注的处理程序为给定的秒数生成的缓存内容<p>可能的值为：
	 * <ul>
	 *  <li> -1：不生成缓存相关的标头</li> <li> 0(默认值)："Cache-Control：no-store"将阻止高速缓存</li> <li> 1或更高：缓存控制：max-age = se
	 * conds"将要求缓存内容;在处理会话属性时不建议</li>。
	 * </ul>
	 * <p>与"cacheSeconds"属性相反,该属性将适用于所有常规处理程序(但不适用于{@code @SessionAttributes}注释处理程序),但此设置仅适用于{@code @SessionAttributes}
	 * 处理程序。
	 * 
	 * 
	 * @see #setCacheSeconds
	 * @see org.springframework.web.bind.annotation.SessionAttributes
	 */
	public void setCacheSecondsForSessionAttributeHandlers(int cacheSecondsForSessionAttributeHandlers) {
		this.cacheSecondsForSessionAttributeHandlers = cacheSecondsForSessionAttributeHandlers;
	}

	/**
	 * Set if controller execution should be synchronized on the session,
	 * to serialize parallel invocations from the same client.
	 * <p>More specifically, the execution of the {@code handleRequestInternal}
	 * method will get synchronized if this flag is "true". The best available
	 * session mutex will be used for the synchronization; ideally, this will
	 * be a mutex exposed by HttpSessionMutexListener.
	 * <p>The session mutex is guaranteed to be the same object during
	 * the entire lifetime of the session, available under the key defined
	 * by the {@code SESSION_MUTEX_ATTRIBUTE} constant. It serves as a
	 * safe reference to synchronize on for locking on the current session.
	 * <p>In many cases, the HttpSession reference itself is a safe mutex
	 * as well, since it will always be the same object reference for the
	 * same active logical session. However, this is not guaranteed across
	 * different servlet containers; the only 100% safe way is a session mutex.
	 * <p>
	 * 设置如果控制器执行应该在会话上同步,以串行化来自同一客户端的并行调用<p>更具体地说,如果该标志为"true",则{@code handleRequestInternal}方法的执行将被同步。
	 * 最佳可用会话互斥体将用于同步;理想情况下,这将是HttpSessionMutexListener暴露的互斥体<p>会话互斥体在会话的整个生命周期内保证是相同的对象,可在{@code SESSION_MUTEX_ATTRIBUTE}
	 * 常量定义的关键字下使用它作为安全引用在当前会话上进行锁定同步<p>在许多情况下,HttpSession引用本身也是一个安全的互斥体,因为它将始终与同一个活动逻辑会话相同的对象引用。
	 * 设置如果控制器执行应该在会话上同步,以串行化来自同一客户端的并行调用<p>更具体地说,如果该标志为"true",则{@code handleRequestInternal}方法的执行将被同步。
	 * 但是,不能保证跨不同的servlet容器;唯一的100％安全方式是会话互斥体。
	 * 
	 * 
	 * @see org.springframework.web.util.HttpSessionMutexListener
	 * @see org.springframework.web.util.WebUtils#getSessionMutex(javax.servlet.http.HttpSession)
	 */
	public void setSynchronizeOnSession(boolean synchronizeOnSession) {
		this.synchronizeOnSession = synchronizeOnSession;
	}

	/**
	 * Set the ParameterNameDiscoverer to use for resolving method parameter names if needed
	 * (e.g. for default attribute names).
	 * <p>Default is a {@link org.springframework.core.DefaultParameterNameDiscoverer}.
	 * <p>
	 * 如果需要,设置ParameterNameDiscoverer用于解析方法参数名称(例如,对于默认属性名称)<p>默认值为{@link orgspringframeworkcoreDefaultParameterNameDiscoverer}
	 * 。
	 * 
	 */
	public void setParameterNameDiscoverer(ParameterNameDiscoverer parameterNameDiscoverer) {
		this.parameterNameDiscoverer = parameterNameDiscoverer;
	}

	/**
	 * A {@link ConfigurableBeanFactory} is expected for resolving expressions
	 * in method argument default values.
	 * <p>
	 *  预期在方法参数默认值中解析表达式的{@link ConfigurableBeanFactory}
	 * 
	 */
	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		if (beanFactory instanceof ConfigurableBeanFactory) {
			this.beanFactory = (ConfigurableBeanFactory) beanFactory;
		}
	}

	/**
	 * Return the owning factory of this bean instance, or {@code null} if none.
	 * <p>
	 *  返回该bean实例的所有工厂,否则返回{@code null}
	 * 
	 */
	protected ConfigurableBeanFactory getBeanFactory() {
		return this.beanFactory;
	}


	@Override
	public void afterPropertiesSet() {
		// Do this first, it may add ResponseBody advice beans
		initControllerAdviceCache();

		if (this.argumentResolvers == null) {
			List<HandlerMethodArgumentResolver> resolvers = getDefaultArgumentResolvers();
			this.argumentResolvers = new HandlerMethodArgumentResolverComposite().addResolvers(resolvers);
		}
		if (this.initBinderArgumentResolvers == null) {
			List<HandlerMethodArgumentResolver> resolvers = getDefaultInitBinderArgumentResolvers();
			this.initBinderArgumentResolvers = new HandlerMethodArgumentResolverComposite().addResolvers(resolvers);
		}
		if (this.returnValueHandlers == null) {
			List<HandlerMethodReturnValueHandler> handlers = getDefaultReturnValueHandlers();
			this.returnValueHandlers = new HandlerMethodReturnValueHandlerComposite().addHandlers(handlers);
		}
	}

	private void initControllerAdviceCache() {
		if (getApplicationContext() == null) {
			return;
		}
		if (logger.isInfoEnabled()) {
			logger.info("Looking for @ControllerAdvice: " + getApplicationContext());
		}

		List<ControllerAdviceBean> beans = ControllerAdviceBean.findAnnotatedBeans(getApplicationContext());
		AnnotationAwareOrderComparator.sort(beans);

		List<Object> requestResponseBodyAdviceBeans = new ArrayList<Object>();

		for (ControllerAdviceBean bean : beans) {
			Set<Method> attrMethods = MethodIntrospector.selectMethods(bean.getBeanType(), MODEL_ATTRIBUTE_METHODS);
			if (!attrMethods.isEmpty()) {
				this.modelAttributeAdviceCache.put(bean, attrMethods);
				if (logger.isInfoEnabled()) {
					logger.info("Detected @ModelAttribute methods in " + bean);
				}
			}
			Set<Method> binderMethods = MethodIntrospector.selectMethods(bean.getBeanType(), INIT_BINDER_METHODS);
			if (!binderMethods.isEmpty()) {
				this.initBinderAdviceCache.put(bean, binderMethods);
				if (logger.isInfoEnabled()) {
					logger.info("Detected @InitBinder methods in " + bean);
				}
			}
			if (RequestBodyAdvice.class.isAssignableFrom(bean.getBeanType())) {
				requestResponseBodyAdviceBeans.add(bean);
				if (logger.isInfoEnabled()) {
					logger.info("Detected RequestBodyAdvice bean in " + bean);
				}
			}
			if (ResponseBodyAdvice.class.isAssignableFrom(bean.getBeanType())) {
				requestResponseBodyAdviceBeans.add(bean);
				if (logger.isInfoEnabled()) {
					logger.info("Detected ResponseBodyAdvice bean in " + bean);
				}
			}
		}

		if (!requestResponseBodyAdviceBeans.isEmpty()) {
			this.requestResponseBodyAdvice.addAll(0, requestResponseBodyAdviceBeans);
		}
	}

	/**
	 * Return the list of argument resolvers to use including built-in resolvers
	 * and custom resolvers provided via {@link #setCustomArgumentResolvers}.
	 * <p>
	 *  返回要使用的参数解析器列表,包括通过{@link #setCustomArgumentResolvers}提供的内置解析器和自定义解析器
	 * 
	 */
	private List<HandlerMethodArgumentResolver> getDefaultArgumentResolvers() {
		List<HandlerMethodArgumentResolver> resolvers = new ArrayList<HandlerMethodArgumentResolver>();

		// Annotation-based argument resolution
		resolvers.add(new RequestParamMethodArgumentResolver(getBeanFactory(), false));
		resolvers.add(new RequestParamMapMethodArgumentResolver());
		resolvers.add(new PathVariableMethodArgumentResolver());
		resolvers.add(new PathVariableMapMethodArgumentResolver());
		resolvers.add(new MatrixVariableMethodArgumentResolver());
		resolvers.add(new MatrixVariableMapMethodArgumentResolver());
		resolvers.add(new ServletModelAttributeMethodProcessor(false));
		resolvers.add(new RequestResponseBodyMethodProcessor(getMessageConverters(), this.requestResponseBodyAdvice));
		resolvers.add(new RequestPartMethodArgumentResolver(getMessageConverters(), this.requestResponseBodyAdvice));
		resolvers.add(new RequestHeaderMethodArgumentResolver(getBeanFactory()));
		resolvers.add(new RequestHeaderMapMethodArgumentResolver());
		resolvers.add(new ServletCookieValueMethodArgumentResolver(getBeanFactory()));
		resolvers.add(new ExpressionValueMethodArgumentResolver(getBeanFactory()));
		resolvers.add(new SessionAttributeMethodArgumentResolver());
		resolvers.add(new RequestAttributeMethodArgumentResolver());

		// Type-based argument resolution
		resolvers.add(new ServletRequestMethodArgumentResolver());
		resolvers.add(new ServletResponseMethodArgumentResolver());
		resolvers.add(new HttpEntityMethodProcessor(getMessageConverters(), this.requestResponseBodyAdvice));
		resolvers.add(new RedirectAttributesMethodArgumentResolver());
		resolvers.add(new ModelMethodProcessor());
		resolvers.add(new MapMethodProcessor());
		resolvers.add(new ErrorsMethodArgumentResolver());
		resolvers.add(new SessionStatusMethodArgumentResolver());
		resolvers.add(new UriComponentsBuilderMethodArgumentResolver());

		// Custom arguments
		if (getCustomArgumentResolvers() != null) {
			resolvers.addAll(getCustomArgumentResolvers());
		}

		// Catch-all
		resolvers.add(new RequestParamMethodArgumentResolver(getBeanFactory(), true));
		resolvers.add(new ServletModelAttributeMethodProcessor(true));

		return resolvers;
	}

	/**
	 * Return the list of argument resolvers to use for {@code @InitBinder}
	 * methods including built-in and custom resolvers.
	 * <p>
	 *  返回参数解析器列表,用于{@code @InitBinder}方法,包括内置和自定义解析器
	 * 
	 */
	private List<HandlerMethodArgumentResolver> getDefaultInitBinderArgumentResolvers() {
		List<HandlerMethodArgumentResolver> resolvers = new ArrayList<HandlerMethodArgumentResolver>();

		// Annotation-based argument resolution
		resolvers.add(new RequestParamMethodArgumentResolver(getBeanFactory(), false));
		resolvers.add(new RequestParamMapMethodArgumentResolver());
		resolvers.add(new PathVariableMethodArgumentResolver());
		resolvers.add(new PathVariableMapMethodArgumentResolver());
		resolvers.add(new MatrixVariableMethodArgumentResolver());
		resolvers.add(new MatrixVariableMapMethodArgumentResolver());
		resolvers.add(new ExpressionValueMethodArgumentResolver(getBeanFactory()));
		resolvers.add(new SessionAttributeMethodArgumentResolver());
		resolvers.add(new RequestAttributeMethodArgumentResolver());

		// Type-based argument resolution
		resolvers.add(new ServletRequestMethodArgumentResolver());
		resolvers.add(new ServletResponseMethodArgumentResolver());

		// Custom arguments
		if (getCustomArgumentResolvers() != null) {
			resolvers.addAll(getCustomArgumentResolvers());
		}

		// Catch-all
		resolvers.add(new RequestParamMethodArgumentResolver(getBeanFactory(), true));

		return resolvers;
	}

	/**
	 * Return the list of return value handlers to use including built-in and
	 * custom handlers provided via {@link #setReturnValueHandlers}.
	 * <p>
	 *  返回返回值处理程序列表,使用包括通过{@link #setReturnValueHandlers}提供的内置和自定义处理程序
	 * 
	 */
	private List<HandlerMethodReturnValueHandler> getDefaultReturnValueHandlers() {
		List<HandlerMethodReturnValueHandler> handlers = new ArrayList<HandlerMethodReturnValueHandler>();

		// Single-purpose return value types
		handlers.add(new ModelAndViewMethodReturnValueHandler());
		handlers.add(new ModelMethodProcessor());
		handlers.add(new ViewMethodReturnValueHandler());
		handlers.add(new ResponseBodyEmitterReturnValueHandler(getMessageConverters()));
		handlers.add(new StreamingResponseBodyReturnValueHandler());
		handlers.add(new HttpEntityMethodProcessor(getMessageConverters(),
				this.contentNegotiationManager, this.requestResponseBodyAdvice));
		handlers.add(new HttpHeadersReturnValueHandler());
		handlers.add(new CallableMethodReturnValueHandler());
		handlers.add(new DeferredResultMethodReturnValueHandler());
		handlers.add(new AsyncTaskMethodReturnValueHandler(this.beanFactory));

		// Annotation-based return value types
		handlers.add(new ModelAttributeMethodProcessor(false));
		handlers.add(new RequestResponseBodyMethodProcessor(getMessageConverters(),
				this.contentNegotiationManager, this.requestResponseBodyAdvice));

		// Multi-purpose return value types
		handlers.add(new ViewNameMethodReturnValueHandler());
		handlers.add(new MapMethodProcessor());

		// Custom return value types
		if (getCustomReturnValueHandlers() != null) {
			handlers.addAll(getCustomReturnValueHandlers());
		}

		// Catch-all
		if (!CollectionUtils.isEmpty(getModelAndViewResolvers())) {
			handlers.add(new ModelAndViewResolverMethodReturnValueHandler(getModelAndViewResolvers()));
		}
		else {
			handlers.add(new ModelAttributeMethodProcessor(true));
		}

		return handlers;
	}


	/**
	 * Always return {@code true} since any method argument and return value
	 * type will be processed in some way. A method argument not recognized
	 * by any HandlerMethodArgumentResolver is interpreted as a request parameter
	 * if it is a simple type, or as a model attribute otherwise. A return value
	 * not recognized by any HandlerMethodReturnValueHandler will be interpreted
	 * as a model attribute.
	 * <p>
	 * 始终返回{@code true},因为任何方法参数和返回值类型都将以某种方式处理。
	 * 如果一个方法参数不被任何HandlerMethodArgumentResolver识别,它将被解释为一个请求参数,如果它是一个简单类型,或者作为一个模型属性返回任何HandlerMethodReturn
	 * ValueHandler无法识别的值将被解释为模型属性。
	 * 始终返回{@code true},因为任何方法参数和返回值类型都将以某种方式处理。
	 * 
	 */
	@Override
	protected boolean supportsInternal(HandlerMethod handlerMethod) {
		return true;
	}

	@Override
	protected ModelAndView handleInternal(HttpServletRequest request,
			HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {

		ModelAndView mav;
		checkRequest(request);

		// Execute invokeHandlerMethod in synchronized block if required.
		if (this.synchronizeOnSession) {
			HttpSession session = request.getSession(false);
			if (session != null) {
				Object mutex = WebUtils.getSessionMutex(session);
				synchronized (mutex) {
					mav = invokeHandlerMethod(request, response, handlerMethod);
				}
			}
			else {
				// No HttpSession available -> no mutex necessary
				mav = invokeHandlerMethod(request, response, handlerMethod);
			}
		}
		else {
			// No synchronization on session demanded at all...
			mav = invokeHandlerMethod(request, response, handlerMethod);
		}

		if (!response.containsHeader(HEADER_CACHE_CONTROL)) {
			if (getSessionAttributesHandler(handlerMethod).hasSessionAttributes()) {
				applyCacheSeconds(response, this.cacheSecondsForSessionAttributeHandlers);
			}
			else {
				prepareResponse(response);
			}
		}

		return mav;
	}

	/**
	 * This implementation always returns -1. An {@code @RequestMapping} method can
	 * calculate the lastModified value, call {@link WebRequest#checkNotModified(long)},
	 * and return {@code null} if the result of that call is {@code true}.
	 * <p>
	 *  这个实现总是返回-1一个{@code @RequestMapping}方法可以计算lastModified值,调用{@link WebRequest#checkNotModified(long)},并
	 * 返回{@code null},如果该调用的结果是{@code true }。
	 * 
	 */
	@Override
	protected long getLastModifiedInternal(HttpServletRequest request, HandlerMethod handlerMethod) {
		return -1;
	}


	/**
	 * Return the {@link SessionAttributesHandler} instance for the given handler type
	 * (never {@code null}).
	 * <p>
	 *  返回给定处理程序类型的{@link SessionAttributesHandler}实例(从不{@code null})
	 * 
	 */
	private SessionAttributesHandler getSessionAttributesHandler(HandlerMethod handlerMethod) {
		Class<?> handlerType = handlerMethod.getBeanType();
		SessionAttributesHandler sessionAttrHandler = this.sessionAttributesHandlerCache.get(handlerType);
		if (sessionAttrHandler == null) {
			synchronized (this.sessionAttributesHandlerCache) {
				sessionAttrHandler = this.sessionAttributesHandlerCache.get(handlerType);
				if (sessionAttrHandler == null) {
					sessionAttrHandler = new SessionAttributesHandler(handlerType, sessionAttributeStore);
					this.sessionAttributesHandlerCache.put(handlerType, sessionAttrHandler);
				}
			}
		}
		return sessionAttrHandler;
	}

	/**
	 * Invoke the {@link RequestMapping} handler method preparing a {@link ModelAndView}
	 * if view resolution is required.
	 * <p>
	 * 如果需要视图分辨率,则调用{@link RequestMapping}处理程序方法,准备一个{@link ModelAndView}
	 * 
	 * 
	 * @since 4.2
	 * @see #createInvocableHandlerMethod(HandlerMethod)
	 */
	protected ModelAndView invokeHandlerMethod(HttpServletRequest request,
			HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {

		ServletWebRequest webRequest = new ServletWebRequest(request, response);
		try {
			WebDataBinderFactory binderFactory = getDataBinderFactory(handlerMethod);
			ModelFactory modelFactory = getModelFactory(handlerMethod, binderFactory);

			ServletInvocableHandlerMethod invocableMethod = createInvocableHandlerMethod(handlerMethod);
			invocableMethod.setHandlerMethodArgumentResolvers(this.argumentResolvers);
			invocableMethod.setHandlerMethodReturnValueHandlers(this.returnValueHandlers);
			invocableMethod.setDataBinderFactory(binderFactory);
			invocableMethod.setParameterNameDiscoverer(this.parameterNameDiscoverer);

			ModelAndViewContainer mavContainer = new ModelAndViewContainer();
			mavContainer.addAllAttributes(RequestContextUtils.getInputFlashMap(request));
			modelFactory.initModel(webRequest, mavContainer, invocableMethod);
			mavContainer.setIgnoreDefaultModelOnRedirect(this.ignoreDefaultModelOnRedirect);

			AsyncWebRequest asyncWebRequest = WebAsyncUtils.createAsyncWebRequest(request, response);
			asyncWebRequest.setTimeout(this.asyncRequestTimeout);

			WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);
			asyncManager.setTaskExecutor(this.taskExecutor);
			asyncManager.setAsyncWebRequest(asyncWebRequest);
			asyncManager.registerCallableInterceptors(this.callableInterceptors);
			asyncManager.registerDeferredResultInterceptors(this.deferredResultInterceptors);

			if (asyncManager.hasConcurrentResult()) {
				Object result = asyncManager.getConcurrentResult();
				mavContainer = (ModelAndViewContainer) asyncManager.getConcurrentResultContext()[0];
				asyncManager.clearConcurrentResult();
				if (logger.isDebugEnabled()) {
					logger.debug("Found concurrent result value [" + result + "]");
				}
				invocableMethod = invocableMethod.wrapConcurrentResult(result);
			}

			invocableMethod.invokeAndHandle(webRequest, mavContainer);
			if (asyncManager.isConcurrentHandlingStarted()) {
				return null;
			}

			return getModelAndView(mavContainer, modelFactory, webRequest);
		}
		finally {
			webRequest.requestCompleted();
		}
	}

	/**
	 * Create a {@link ServletInvocableHandlerMethod} from the given {@link HandlerMethod} definition.
	 * <p>
	 *  从给定的{@link HandlerMethod}定义创建{@link ServletInvocableHandlerMethod}
	 * 
	 * 
	 * @param handlerMethod the {@link HandlerMethod} definition
	 * @return the corresponding {@link ServletInvocableHandlerMethod} (or custom subclass thereof)
	 * @since 4.2
	 */
	protected ServletInvocableHandlerMethod createInvocableHandlerMethod(HandlerMethod handlerMethod) {
		return new ServletInvocableHandlerMethod(handlerMethod);
	}

	private ModelFactory getModelFactory(HandlerMethod handlerMethod, WebDataBinderFactory binderFactory) {
		SessionAttributesHandler sessionAttrHandler = getSessionAttributesHandler(handlerMethod);
		Class<?> handlerType = handlerMethod.getBeanType();
		Set<Method> methods = this.modelAttributeCache.get(handlerType);
		if (methods == null) {
			methods = MethodIntrospector.selectMethods(handlerType, MODEL_ATTRIBUTE_METHODS);
			this.modelAttributeCache.put(handlerType, methods);
		}
		List<InvocableHandlerMethod> attrMethods = new ArrayList<InvocableHandlerMethod>();
		// Global methods first
		for (Entry<ControllerAdviceBean, Set<Method>> entry : this.modelAttributeAdviceCache.entrySet()) {
			if (entry.getKey().isApplicableToBeanType(handlerType)) {
				Object bean = entry.getKey().resolveBean();
				for (Method method : entry.getValue()) {
					attrMethods.add(createModelAttributeMethod(binderFactory, bean, method));
				}
			}
		}
		for (Method method : methods) {
			Object bean = handlerMethod.getBean();
			attrMethods.add(createModelAttributeMethod(binderFactory, bean, method));
		}
		return new ModelFactory(attrMethods, binderFactory, sessionAttrHandler);
	}

	private InvocableHandlerMethod createModelAttributeMethod(WebDataBinderFactory factory, Object bean, Method method) {
		InvocableHandlerMethod attrMethod = new InvocableHandlerMethod(bean, method);
		attrMethod.setHandlerMethodArgumentResolvers(this.argumentResolvers);
		attrMethod.setParameterNameDiscoverer(this.parameterNameDiscoverer);
		attrMethod.setDataBinderFactory(factory);
		return attrMethod;
	}

	private WebDataBinderFactory getDataBinderFactory(HandlerMethod handlerMethod) throws Exception {
		Class<?> handlerType = handlerMethod.getBeanType();
		Set<Method> methods = this.initBinderCache.get(handlerType);
		if (methods == null) {
			methods = MethodIntrospector.selectMethods(handlerType, INIT_BINDER_METHODS);
			this.initBinderCache.put(handlerType, methods);
		}
		List<InvocableHandlerMethod> initBinderMethods = new ArrayList<InvocableHandlerMethod>();
		// Global methods first
		for (Entry<ControllerAdviceBean, Set<Method>> entry : this.initBinderAdviceCache.entrySet()) {
			if (entry.getKey().isApplicableToBeanType(handlerType)) {
				Object bean = entry.getKey().resolveBean();
				for (Method method : entry.getValue()) {
					initBinderMethods.add(createInitBinderMethod(bean, method));
				}
			}
		}
		for (Method method : methods) {
			Object bean = handlerMethod.getBean();
			initBinderMethods.add(createInitBinderMethod(bean, method));
		}
		return createDataBinderFactory(initBinderMethods);
	}

	private InvocableHandlerMethod createInitBinderMethod(Object bean, Method method) {
		InvocableHandlerMethod binderMethod = new InvocableHandlerMethod(bean, method);
		binderMethod.setHandlerMethodArgumentResolvers(this.initBinderArgumentResolvers);
		binderMethod.setDataBinderFactory(new DefaultDataBinderFactory(this.webBindingInitializer));
		binderMethod.setParameterNameDiscoverer(this.parameterNameDiscoverer);
		return binderMethod;
	}

	/**
	 * Template method to create a new InitBinderDataBinderFactory instance.
	 * <p>The default implementation creates a ServletRequestDataBinderFactory.
	 * This can be overridden for custom ServletRequestDataBinder subclasses.
	 * <p>
	 *  创建一个新的InitBinderDataBinderFactory实例的模板方法<p>默认实现创建一个ServletRequestDataBinderFactory这可以覆盖自定义ServletReq
	 * uestDataBinder子类。
	 * 
	 * 
	 * @param binderMethods {@code @InitBinder} methods
	 * @return the InitBinderDataBinderFactory instance to use
	 * @throws Exception in case of invalid state or arguments
	 */
	protected InitBinderDataBinderFactory createDataBinderFactory(List<InvocableHandlerMethod> binderMethods)
			throws Exception {

		return new ServletRequestDataBinderFactory(binderMethods, getWebBindingInitializer());
	}

	private ModelAndView getModelAndView(ModelAndViewContainer mavContainer,
			ModelFactory modelFactory, NativeWebRequest webRequest) throws Exception {

		modelFactory.updateModel(webRequest, mavContainer);
		if (mavContainer.isRequestHandled()) {
			return null;
		}
		ModelMap model = mavContainer.getModel();
		ModelAndView mav = new ModelAndView(mavContainer.getViewName(), model, mavContainer.getStatus());
		if (!mavContainer.isViewReference()) {
			mav.setView((View) mavContainer.getView());
		}
		if (model instanceof RedirectAttributes) {
			Map<String, ?> flashAttributes = ((RedirectAttributes) model).getFlashAttributes();
			HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
			RequestContextUtils.getOutputFlashMap(request).putAll(flashAttributes);
		}
		return mav;
	}


	/**
	 * MethodFilter that matches {@link InitBinder @InitBinder} methods.
	 * <p>
	 *  MethodFilter匹配{@link InitBinder @InitBinder}方法
	 * 
	 */
	public static final MethodFilter INIT_BINDER_METHODS = new MethodFilter() {
		@Override
		public boolean matches(Method method) {
			return AnnotationUtils.findAnnotation(method, InitBinder.class) != null;
		}
	};

	/**
	 * MethodFilter that matches {@link ModelAttribute @ModelAttribute} methods.
	 * <p>
	 *  MethodFilter匹配{@link ModelAttribute @ModelAttribute}方法
	 */
	public static final MethodFilter MODEL_ATTRIBUTE_METHODS = new MethodFilter() {
		@Override
		public boolean matches(Method method) {
			return ((AnnotationUtils.findAnnotation(method, RequestMapping.class) == null) &&
					(AnnotationUtils.findAnnotation(method, ModelAttribute.class) != null));
		}
	};

}
