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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.ControllerAdviceBean;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.method.annotation.MapMethodProcessor;
import org.springframework.web.method.annotation.ModelAttributeMethodProcessor;
import org.springframework.web.method.annotation.ModelMethodProcessor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.handler.AbstractHandlerMethodExceptionResolver;

/**
 * An {@link AbstractHandlerMethodExceptionResolver} that resolves exceptions
 * through {@code @ExceptionHandler} methods.
 *
 * <p>Support for custom argument and return value types can be added via
 * {@link #setCustomArgumentResolvers} and {@link #setCustomReturnValueHandlers}.
 * Or alternatively to re-configure all argument and return value types use
 * {@link #setArgumentResolvers} and {@link #setReturnValueHandlers(List)}.
 *
 * <p>
 *  通过{@code @ExceptionHandler}方法解决异常的{@link AbstractHandlerMethodExceptionResolver}
 * 
 * <p>可以通过{@link #setCustomArgumentResolvers}和{@link #setCustomReturnValueHandlers}添加自定义参数和返回值类型的支持,或者重新
 * 配置所有参数和返回值类型,使用{@link #setArgumentResolvers}和{@ link #setReturnValueHandlers(List)}。
 * 
 * 
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @since 3.1
 */
public class ExceptionHandlerExceptionResolver extends AbstractHandlerMethodExceptionResolver
		implements ApplicationContextAware, InitializingBean {

	private List<HandlerMethodArgumentResolver> customArgumentResolvers;

	private HandlerMethodArgumentResolverComposite argumentResolvers;

	private List<HandlerMethodReturnValueHandler> customReturnValueHandlers;

	private HandlerMethodReturnValueHandlerComposite returnValueHandlers;

	private List<HttpMessageConverter<?>> messageConverters;

	private ContentNegotiationManager contentNegotiationManager = new ContentNegotiationManager();

	private final List<Object> responseBodyAdvice = new ArrayList<Object>();

	private ApplicationContext applicationContext;

	private final Map<Class<?>, ExceptionHandlerMethodResolver> exceptionHandlerCache =
			new ConcurrentHashMap<Class<?>, ExceptionHandlerMethodResolver>(64);

	private final Map<ControllerAdviceBean, ExceptionHandlerMethodResolver> exceptionHandlerAdviceCache =
			new LinkedHashMap<ControllerAdviceBean, ExceptionHandlerMethodResolver>();


	public ExceptionHandlerExceptionResolver() {
		StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
		stringHttpMessageConverter.setWriteAcceptCharset(false);  // see SPR-7316

		this.messageConverters = new ArrayList<HttpMessageConverter<?>>();
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
		this.customArgumentResolvers= argumentResolvers;
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
	public HandlerMethodArgumentResolverComposite getArgumentResolvers() {
		return this.argumentResolvers;
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
	 *  返回配置的处理程序,或者{@code null}(如果尚未通过{@link #afterPropertiesSet()}初始化)
	 * 
	 */
	public HandlerMethodReturnValueHandlerComposite getReturnValueHandlers() {
		return this.returnValueHandlers;
	}

	/**
	 * Set the message body converters to use.
	 * <p>These converters are used to convert from and to HTTP requests and responses.
	 * <p>
	 *  将消息体转换器设置为使用<p>这些转换器用于从HTTP请求和响应转换
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
	 * Set the {@link ContentNegotiationManager} to use to determine requested media types.
	 * If not set, the default constructor is used.
	 * <p>
	 * 设置{@link ContentNegotiationManager}以用于确定请求的媒体类型如果未设置,则使用默认构造函数
	 * 
	 */
	public void setContentNegotiationManager(ContentNegotiationManager contentNegotiationManager) {
		this.contentNegotiationManager = contentNegotiationManager;
	}

	/**
	 * Return the configured {@link ContentNegotiationManager}.
	 * <p>
	 *  返回配置的{@link ContentNegotiationManager}
	 * 
	 */
	public ContentNegotiationManager getContentNegotiationManager() {
		return this.contentNegotiationManager;
	}

	/**
	 * Add one or more components to be invoked after the execution of a controller
	 * method annotated with {@code @ResponseBody} or returning {@code ResponseEntity}
	 * but before the body is written to the response with the selected
	 * {@code HttpMessageConverter}.
	 * <p>
	 *  在执行使用{@code @ResponseBody}注释的控制器方法或返回{@code ResponseEntity}之后添加要调用的一个或多个组件,但在使用所选的{@code HttpMessageConverter}
	 * 将身体写入响应之前,。
	 * 
	 */
	public void setResponseBodyAdvice(List<ResponseBodyAdvice<?>> responseBodyAdvice) {
		this.responseBodyAdvice.clear();
		if (responseBodyAdvice != null) {
			this.responseBodyAdvice.addAll(responseBodyAdvice);
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public ApplicationContext getApplicationContext() {
		return this.applicationContext;
	}


	@Override
	public void afterPropertiesSet() {
		// Do this first, it may add ResponseBodyAdvice beans
		initExceptionHandlerAdviceCache();

		if (this.argumentResolvers == null) {
			List<HandlerMethodArgumentResolver> resolvers = getDefaultArgumentResolvers();
			this.argumentResolvers = new HandlerMethodArgumentResolverComposite().addResolvers(resolvers);
		}
		if (this.returnValueHandlers == null) {
			List<HandlerMethodReturnValueHandler> handlers = getDefaultReturnValueHandlers();
			this.returnValueHandlers = new HandlerMethodReturnValueHandlerComposite().addHandlers(handlers);
		}
	}

	private void initExceptionHandlerAdviceCache() {
		if (getApplicationContext() == null) {
			return;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Looking for exception mappings: " + getApplicationContext());
		}

		List<ControllerAdviceBean> adviceBeans = ControllerAdviceBean.findAnnotatedBeans(getApplicationContext());
		AnnotationAwareOrderComparator.sort(adviceBeans);

		for (ControllerAdviceBean adviceBean : adviceBeans) {
			ExceptionHandlerMethodResolver resolver = new ExceptionHandlerMethodResolver(adviceBean.getBeanType());
			if (resolver.hasExceptionMappings()) {
				this.exceptionHandlerAdviceCache.put(adviceBean, resolver);
				if (logger.isInfoEnabled()) {
					logger.info("Detected @ExceptionHandler methods in " + adviceBean);
				}
			}
			if (ResponseBodyAdvice.class.isAssignableFrom(adviceBean.getBeanType())) {
				this.responseBodyAdvice.add(adviceBean);
				if (logger.isInfoEnabled()) {
					logger.info("Detected ResponseBodyAdvice implementation in " + adviceBean);
				}
			}
		}
	}

	/**
	 * Return an unmodifiable Map with the {@link ControllerAdvice @ControllerAdvice}
	 * beans discovered in the ApplicationContext. The returned map will be empty if
	 * the method is invoked before the bean has been initialized via
	 * {@link #afterPropertiesSet()}.
	 * <p>
	 *  使用ApplicationContext中发现的{@link ControllerAdvice @ControllerAdvice} bean返回一个不可修改的Map返回的映射将为空,如果在通过{@link #afterPropertiesSet())初始化bean之前调用该方法,。
	 * 
	 */
	public Map<ControllerAdviceBean, ExceptionHandlerMethodResolver> getExceptionHandlerAdviceCache() {
		return Collections.unmodifiableMap(this.exceptionHandlerAdviceCache);
	}

	/**
	 * Return the list of argument resolvers to use including built-in resolvers
	 * and custom resolvers provided via {@link #setCustomArgumentResolvers}.
	 * <p>
	 * 返回要使用的参数解析器列表,包括通过{@link #setCustomArgumentResolvers}提供的内置解析器和自定义解析器
	 * 
	 */
	protected List<HandlerMethodArgumentResolver> getDefaultArgumentResolvers() {
		List<HandlerMethodArgumentResolver> resolvers = new ArrayList<HandlerMethodArgumentResolver>();

		// Annotation-based argument resolution
		resolvers.add(new SessionAttributeMethodArgumentResolver());
		resolvers.add(new RequestAttributeMethodArgumentResolver());

		// Type-based argument resolution
		resolvers.add(new ServletRequestMethodArgumentResolver());
		resolvers.add(new ServletResponseMethodArgumentResolver());
		resolvers.add(new ModelMethodProcessor());

		// Custom arguments
		if (getCustomArgumentResolvers() != null) {
			resolvers.addAll(getCustomArgumentResolvers());
		}

		return resolvers;
	}

	/**
	 * Return the list of return value handlers to use including built-in and
	 * custom handlers provided via {@link #setReturnValueHandlers}.
	 * <p>
	 *  返回返回值处理程序列表,使用包括通过{@link #setReturnValueHandlers}提供的内置和自定义处理程序
	 * 
	 */
	protected List<HandlerMethodReturnValueHandler> getDefaultReturnValueHandlers() {
		List<HandlerMethodReturnValueHandler> handlers = new ArrayList<HandlerMethodReturnValueHandler>();

		// Single-purpose return value types
		handlers.add(new ModelAndViewMethodReturnValueHandler());
		handlers.add(new ModelMethodProcessor());
		handlers.add(new ViewMethodReturnValueHandler());
		handlers.add(new HttpEntityMethodProcessor(
				getMessageConverters(), this.contentNegotiationManager, this.responseBodyAdvice));

		// Annotation-based return value types
		handlers.add(new ModelAttributeMethodProcessor(false));
		handlers.add(new RequestResponseBodyMethodProcessor(
				getMessageConverters(), this.contentNegotiationManager, this.responseBodyAdvice));

		// Multi-purpose return value types
		handlers.add(new ViewNameMethodReturnValueHandler());
		handlers.add(new MapMethodProcessor());

		// Custom return value types
		if (getCustomReturnValueHandlers() != null) {
			handlers.addAll(getCustomReturnValueHandlers());
		}

		// Catch-all
		handlers.add(new ModelAttributeMethodProcessor(true));

		return handlers;
	}


	/**
	 * Find an {@code @ExceptionHandler} method and invoke it to handle the raised exception.
	 * <p>
	 *  找到{@code @ExceptionHandler}方法并调用它来处理引发的异常
	 * 
	 */
	@Override
	protected ModelAndView doResolveHandlerMethodException(HttpServletRequest request,
			HttpServletResponse response, HandlerMethod handlerMethod, Exception exception) {

		ServletInvocableHandlerMethod exceptionHandlerMethod = getExceptionHandlerMethod(handlerMethod, exception);
		if (exceptionHandlerMethod == null) {
			return null;
		}

		exceptionHandlerMethod.setHandlerMethodArgumentResolvers(this.argumentResolvers);
		exceptionHandlerMethod.setHandlerMethodReturnValueHandlers(this.returnValueHandlers);

		ServletWebRequest webRequest = new ServletWebRequest(request, response);
		ModelAndViewContainer mavContainer = new ModelAndViewContainer();

		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Invoking @ExceptionHandler method: " + exceptionHandlerMethod);
			}
			Throwable cause = exception.getCause();
			if (cause != null) {
				// Expose cause as provided argument as well
				exceptionHandlerMethod.invokeAndHandle(webRequest, mavContainer, exception, cause, handlerMethod);
			}
			else {
				// Otherwise, just the given exception as-is
				exceptionHandlerMethod.invokeAndHandle(webRequest, mavContainer, exception, handlerMethod);
			}
		}
		catch (Exception invocationEx) {
			if (logger.isDebugEnabled()) {
				logger.debug("Failed to invoke @ExceptionHandler method: " + exceptionHandlerMethod, invocationEx);
			}
			return null;
		}

		if (mavContainer.isRequestHandled()) {
			return new ModelAndView();
		}
		else {
			ModelAndView mav = new ModelAndView().addAllObjects(mavContainer.getModel());
			mav.setViewName(mavContainer.getViewName());
			if (!mavContainer.isViewReference()) {
				mav.setView((View) mavContainer.getView());
			}
			return mav;
		}
	}

	/**
	 * Find an {@code @ExceptionHandler} method for the given exception. The default
	 * implementation searches methods in the class hierarchy of the controller first
	 * and if not found, it continues searching for additional {@code @ExceptionHandler}
	 * methods assuming some {@linkplain ControllerAdvice @ControllerAdvice}
	 * Spring-managed beans were detected.
	 * <p>
	 *  找到给定异常的{@code @ExceptionHandler}方法默认实现首先搜索控制器类层次结构中的方法,如果没有找到,则继续搜索额外的{@code @ExceptionHandler}方法,假设
	 * 一些{@linkplain ControllerAdvice @ ControllerAdvice}检测到Spring管理的bean。
	 * 
	 * @param handlerMethod the method where the exception was raised (may be {@code null})
	 * @param exception the raised exception
	 * @return a method to handle the exception, or {@code null}
	 */
	protected ServletInvocableHandlerMethod getExceptionHandlerMethod(HandlerMethod handlerMethod, Exception exception) {
		Class<?> handlerType = (handlerMethod != null ? handlerMethod.getBeanType() : null);

		if (handlerMethod != null) {
			ExceptionHandlerMethodResolver resolver = this.exceptionHandlerCache.get(handlerType);
			if (resolver == null) {
				resolver = new ExceptionHandlerMethodResolver(handlerType);
				this.exceptionHandlerCache.put(handlerType, resolver);
			}
			Method method = resolver.resolveMethod(exception);
			if (method != null) {
				return new ServletInvocableHandlerMethod(handlerMethod.getBean(), method);
			}
		}

		for (Entry<ControllerAdviceBean, ExceptionHandlerMethodResolver> entry : this.exceptionHandlerAdviceCache.entrySet()) {
			if (entry.getKey().isApplicableToBeanType(handlerType)) {
				ExceptionHandlerMethodResolver resolver = entry.getValue();
				Method method = resolver.resolveMethod(exception);
				if (method != null) {
					return new ServletInvocableHandlerMethod(entry.getKey().resolveBean(), method);
				}
			}
		}

		return null;
	}

}
