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

package org.springframework.web.servlet.mvc.annotation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Source;

import org.springframework.core.ExceptionDepthComparator;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.SynthesizingMethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.ui.Model;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * Implementation of the {@link org.springframework.web.servlet.HandlerExceptionResolver} interface that handles
 * exceptions through the {@link ExceptionHandler} annotation.
 *
 * <p>This exception resolver is enabled by default in the {@link org.springframework.web.servlet.DispatcherServlet}.
 *
 * <p>
 *  实现通过{@link ExceptionHandler}注释处理异常的{@link orgspringframeworkwebservletHandlerExceptionResolver}接口
 * 
 * <p>此异常解析器默认情况下在{@link orgspringframeworkwebservletDispatcherServlet}中启用
 * 
 * 
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @since 3.0
 * @deprecated as of Spring 3.2, in favor of
 * {@link org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver ExceptionHandlerExceptionResolver}
 */
@Deprecated
public class AnnotationMethodHandlerExceptionResolver extends AbstractHandlerExceptionResolver {

	/**
	 * Arbitrary {@link Method} reference, indicating no method found in the cache.
	 * <p>
	 *  任意{@link方法}引用,表示缓存中没有找到方法
	 * 
	 */
	private static final Method NO_METHOD_FOUND = ClassUtils.getMethodIfAvailable(System.class, "currentTimeMillis");


	private final Map<Class<?>, Map<Class<? extends Throwable>, Method>> exceptionHandlerCache =
			new ConcurrentHashMap<Class<?>, Map<Class<? extends Throwable>, Method>>(64);

	private WebArgumentResolver[] customArgumentResolvers;

	private HttpMessageConverter<?>[] messageConverters =
			new HttpMessageConverter<?>[] {new ByteArrayHttpMessageConverter(), new StringHttpMessageConverter(),
			new SourceHttpMessageConverter<Source>(),
			new org.springframework.http.converter.xml.XmlAwareFormHttpMessageConverter()};


	/**
	 * Set a custom ArgumentResolvers to use for special method parameter types.
	 * <p>Such a custom ArgumentResolver will kick in first, having a chance to resolve
	 * an argument value before the standard argument handling kicks in.
	 * <p>
	 *  设置一个自定义的ArgumentResolvers用于特殊的方法参数类型<p>这样一个自定义的ArgumentResolver将首先启动,有机会在标准参数处理开始前解析参数值
	 * 
	 */
	public void setCustomArgumentResolver(WebArgumentResolver argumentResolver) {
		this.customArgumentResolvers = new WebArgumentResolver[]{argumentResolver};
	}

	/**
	 * Set one or more custom ArgumentResolvers to use for special method parameter types.
	 * <p>Any such custom ArgumentResolver will kick in first, having a chance to resolve
	 * an argument value before the standard argument handling kicks in.
	 * <p>
	 *  设置一个或多个自定义ArgumentResolvers用于特殊的方法参数类型<p>任何这样的自定义ArgumentResolver将首先启动,有机会在标准参数处理开始之前解析参数值
	 * 
	 */
	public void setCustomArgumentResolvers(WebArgumentResolver[] argumentResolvers) {
		this.customArgumentResolvers = argumentResolvers;
	}

	/**
	 * Set the message body converters to use.
	 * <p>These converters are used to convert from and to HTTP requests and responses.
	 * <p>
	 *  将消息体转换器设置为使用<p>这些转换器用于从HTTP请求和响应转换
	 * 
	 */
	public void setMessageConverters(HttpMessageConverter<?>[] messageConverters) {
		this.messageConverters = messageConverters;
	}


	@Override
	protected ModelAndView doResolveException(
			HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

		if (handler != null) {
			Method handlerMethod = findBestExceptionHandlerMethod(handler, ex);
			if (handlerMethod != null) {
				ServletWebRequest webRequest = new ServletWebRequest(request, response);
				try {
					Object[] args = resolveHandlerArguments(handlerMethod, handler, webRequest, ex);
					if (logger.isDebugEnabled()) {
						logger.debug("Invoking request handler method: " + handlerMethod);
					}
					Object retVal = doInvokeMethod(handlerMethod, handler, args);
					return getModelAndView(handlerMethod, retVal, webRequest);
				}
				catch (Exception invocationEx) {
					logger.error("Invoking request method resulted in exception : " + handlerMethod, invocationEx);
				}
			}
		}
		return null;
	}

	/**
	 * Finds the handler method that matches the thrown exception best.
	 * <p>
	 * 找到最适合匹配抛出的异常的处理程序方法
	 * 
	 * 
	 * @param handler the handler object
	 * @param thrownException the exception to be handled
	 * @return the best matching method; or {@code null} if none is found
	 */
	private Method findBestExceptionHandlerMethod(Object handler, final Exception thrownException) {
		final Class<?> handlerType = ClassUtils.getUserClass(handler);
		final Class<? extends Throwable> thrownExceptionType = thrownException.getClass();
		Method handlerMethod = null;

		Map<Class<? extends Throwable>, Method> handlers = this.exceptionHandlerCache.get(handlerType);
		if (handlers != null) {
			handlerMethod = handlers.get(thrownExceptionType);
			if (handlerMethod != null) {
				return (handlerMethod == NO_METHOD_FOUND ? null : handlerMethod);
			}
		}
		else {
			handlers = new ConcurrentHashMap<Class<? extends Throwable>, Method>(16);
			this.exceptionHandlerCache.put(handlerType, handlers);
		}

		final Map<Class<? extends Throwable>, Method> matchedHandlers = new HashMap<Class<? extends Throwable>, Method>();

		ReflectionUtils.doWithMethods(handlerType, new ReflectionUtils.MethodCallback() {
			@Override
			public void doWith(Method method) {
				method = ClassUtils.getMostSpecificMethod(method, handlerType);
				List<Class<? extends Throwable>> handledExceptions = getHandledExceptions(method);
				for (Class<? extends Throwable> handledException : handledExceptions) {
					if (handledException.isAssignableFrom(thrownExceptionType)) {
						if (!matchedHandlers.containsKey(handledException)) {
							matchedHandlers.put(handledException, method);
						}
						else {
							Method oldMappedMethod = matchedHandlers.get(handledException);
							if (!oldMappedMethod.equals(method)) {
								throw new IllegalStateException(
										"Ambiguous exception handler mapped for " + handledException + "]: {" +
												oldMappedMethod + ", " + method + "}.");
							}
						}
					}
				}
			}
		});

		handlerMethod = getBestMatchingMethod(matchedHandlers, thrownException);
		handlers.put(thrownExceptionType, (handlerMethod == null ? NO_METHOD_FOUND : handlerMethod));
		return handlerMethod;
	}

	/**
	 * Returns all the exception classes handled by the given method.
	 * <p>The default implementation looks for exceptions in the annotation,
	 * or - if that annotation element is empty - any exceptions listed in the method parameters if the method
	 * is annotated with {@code @ExceptionHandler}.
	 * <p>
	 *  返回由给定方法处理的所有异常类<p>默认实现在注释中查找异常,或者 - 如果注释元素为空 - 方法参数中列出的任何异常,如果方法用{@code @ExceptionHandler }
	 * 
	 * 
	 * @param method the method
	 * @return the handled exceptions
	 */
	@SuppressWarnings("unchecked")
	protected List<Class<? extends Throwable>> getHandledExceptions(Method method) {
		List<Class<? extends Throwable>> result = new ArrayList<Class<? extends Throwable>>();
		ExceptionHandler exceptionHandler = AnnotationUtils.findAnnotation(method, ExceptionHandler.class);
		if (exceptionHandler != null) {
			if (!ObjectUtils.isEmpty(exceptionHandler.value())) {
				result.addAll(Arrays.asList(exceptionHandler.value()));
			}
			else {
				for (Class<?> param : method.getParameterTypes()) {
					if (Throwable.class.isAssignableFrom(param)) {
						result.add((Class<? extends Throwable>) param);
					}
				}
			}
		}
		return result;
	}

	/**
	 * Uses the {@link ExceptionDepthComparator} to find the best matching method.
	 * <p>
	 *  使用{@link ExceptionDepthComparator}找到最佳匹配方法
	 * 
	 * 
	 * @return the best matching method, or {@code null} if none found
	 */
	private Method getBestMatchingMethod(
			Map<Class<? extends Throwable>, Method> resolverMethods, Exception thrownException) {

		if (resolverMethods.isEmpty()) {
			return null;
		}
		Class<? extends Throwable> closestMatch =
				ExceptionDepthComparator.findClosestMatch(resolverMethods.keySet(), thrownException);
		Method method = resolverMethods.get(closestMatch);
		return ((method == null) || (NO_METHOD_FOUND == method)) ? null : method;
	}

	/**
	 * Resolves the arguments for the given method. Delegates to {@link #resolveCommonArgument}.
	 * <p>
	 *  解析给定方法的参数委派给{@link #resolveCommonArgument}
	 * 
	 */
	private Object[] resolveHandlerArguments(Method handlerMethod, Object handler,
			NativeWebRequest webRequest, Exception thrownException) throws Exception {

		Class<?>[] paramTypes = handlerMethod.getParameterTypes();
		Object[] args = new Object[paramTypes.length];
		Class<?> handlerType = handler.getClass();
		for (int i = 0; i < args.length; i++) {
			MethodParameter methodParam = new SynthesizingMethodParameter(handlerMethod, i);
			GenericTypeResolver.resolveParameterType(methodParam, handlerType);
			Class<?> paramType = methodParam.getParameterType();
			Object argValue = resolveCommonArgument(methodParam, webRequest, thrownException);
			if (argValue != WebArgumentResolver.UNRESOLVED) {
				args[i] = argValue;
			}
			else {
				throw new IllegalStateException("Unsupported argument [" + paramType.getName() +
						"] for @ExceptionHandler method: " + handlerMethod);
			}
		}
		return args;
	}

	/**
	 * Resolves common method arguments. Delegates to registered {@link #setCustomArgumentResolver(WebArgumentResolver)
	 * argumentResolvers} first, then checking {@link #resolveStandardArgument}.
	 * <p>
	 *  解决常用的方法参数首先注册{@link #setCustomArgumentResolver(WebArgumentResolver)argumentResolvers}的代理,然后检查{@link #resolveStandardArgument}
	 * 。
	 * 
	 * 
	 * @param methodParameter the method parameter
	 * @param webRequest the request
	 * @param thrownException the exception thrown
	 * @return the argument value, or {@link WebArgumentResolver#UNRESOLVED}
	 */
	protected Object resolveCommonArgument(MethodParameter methodParameter, NativeWebRequest webRequest,
			Exception thrownException) throws Exception {

		// Invoke custom argument resolvers if present...
		if (this.customArgumentResolvers != null) {
			for (WebArgumentResolver argumentResolver : this.customArgumentResolvers) {
				Object value = argumentResolver.resolveArgument(methodParameter, webRequest);
				if (value != WebArgumentResolver.UNRESOLVED) {
					return value;
				}
			}
		}

		// Resolution of standard parameter types...
		Class<?> paramType = methodParameter.getParameterType();
		Object value = resolveStandardArgument(paramType, webRequest, thrownException);
		if (value != WebArgumentResolver.UNRESOLVED && !ClassUtils.isAssignableValue(paramType, value)) {
			throw new IllegalStateException(
					"Standard argument type [" + paramType.getName() + "] resolved to incompatible value of type [" +
							(value != null ? value.getClass() : null) +
							"]. Consider declaring the argument type in a less specific fashion.");
		}
		return value;
	}

	/**
	 * Resolves standard method arguments. The default implementation handles {@link NativeWebRequest},
	 * {@link ServletRequest}, {@link ServletResponse}, {@link HttpSession}, {@link Principal},
	 * {@link Locale}, request {@link InputStream}, request {@link Reader}, response {@link OutputStream},
	 * response {@link Writer}, and the given {@code thrownException}.
	 * <p>
	 * 解决标准方法参数默认实现处理{@link NativeWebRequest},{@link ServletRequest},{@link ServletResponse},{@link HttpSession}
	 * ,{@link Principal},{@link Locale},请求{@link InputStream} ,请求{@link Reader},回复{@link OutputStream},回复{@link Writer}
	 * 
	 * @param parameterType the method parameter type
	 * @param webRequest the request
	 * @param thrownException the exception thrown
	 * @return the argument value, or {@link WebArgumentResolver#UNRESOLVED}
	 */
	protected Object resolveStandardArgument(Class<?> parameterType, NativeWebRequest webRequest,
			Exception thrownException) throws Exception {

		if (parameterType.isInstance(thrownException)) {
			return thrownException;
		}
		else if (WebRequest.class.isAssignableFrom(parameterType)) {
			return webRequest;
		}

		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
		HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);

		if (ServletRequest.class.isAssignableFrom(parameterType)) {
			return request;
		}
		else if (ServletResponse.class.isAssignableFrom(parameterType)) {
			return response;
		}
		else if (HttpSession.class.isAssignableFrom(parameterType)) {
			return request.getSession();
		}
		else if (Principal.class.isAssignableFrom(parameterType)) {
			return request.getUserPrincipal();
		}
		else if (Locale.class == parameterType) {
			return RequestContextUtils.getLocale(request);
		}
		else if (InputStream.class.isAssignableFrom(parameterType)) {
			return request.getInputStream();
		}
		else if (Reader.class.isAssignableFrom(parameterType)) {
			return request.getReader();
		}
		else if (OutputStream.class.isAssignableFrom(parameterType)) {
			return response.getOutputStream();
		}
		else if (Writer.class.isAssignableFrom(parameterType)) {
			return response.getWriter();
		}
		else {
			return WebArgumentResolver.UNRESOLVED;

		}
	}

	private Object doInvokeMethod(Method method, Object target, Object[] args) throws Exception {
		ReflectionUtils.makeAccessible(method);
		try {
			return method.invoke(target, args);
		}
		catch (InvocationTargetException ex) {
			ReflectionUtils.rethrowException(ex.getTargetException());
		}
		throw new IllegalStateException("Should never get here");
	}

	@SuppressWarnings("unchecked")
	private ModelAndView getModelAndView(Method handlerMethod, Object returnValue, ServletWebRequest webRequest)
			throws Exception {

		ResponseStatus responseStatus = AnnotatedElementUtils.findMergedAnnotation(handlerMethod, ResponseStatus.class);
		if (responseStatus != null) {
			HttpStatus statusCode = responseStatus.code();
			String reason = responseStatus.reason();
			if (!StringUtils.hasText(reason)) {
				webRequest.getResponse().setStatus(statusCode.value());
			}
			else {
				webRequest.getResponse().sendError(statusCode.value(), reason);
			}
		}

		if (returnValue != null && AnnotationUtils.findAnnotation(handlerMethod, ResponseBody.class) != null) {
			return handleResponseBody(returnValue, webRequest);
		}

		if (returnValue instanceof ModelAndView) {
			return (ModelAndView) returnValue;
		}
		else if (returnValue instanceof Model) {
			return new ModelAndView().addAllObjects(((Model) returnValue).asMap());
		}
		else if (returnValue instanceof Map) {
			return new ModelAndView().addAllObjects((Map<String, Object>) returnValue);
		}
		else if (returnValue instanceof View) {
			return new ModelAndView((View) returnValue);
		}
		else if (returnValue instanceof String) {
			return new ModelAndView((String) returnValue);
		}
		else if (returnValue == null) {
			return new ModelAndView();
		}
		else {
			throw new IllegalArgumentException("Invalid handler method return value: " + returnValue);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes", "resource" })
	private ModelAndView handleResponseBody(Object returnValue, ServletWebRequest webRequest)
			throws ServletException, IOException {

		HttpInputMessage inputMessage = new ServletServerHttpRequest(webRequest.getRequest());
		List<MediaType> acceptedMediaTypes = inputMessage.getHeaders().getAccept();
		if (acceptedMediaTypes.isEmpty()) {
			acceptedMediaTypes = Collections.singletonList(MediaType.ALL);
		}
		MediaType.sortByQualityValue(acceptedMediaTypes);
		HttpOutputMessage outputMessage = new ServletServerHttpResponse(webRequest.getResponse());
		Class<?> returnValueType = returnValue.getClass();
		if (this.messageConverters != null) {
			for (MediaType acceptedMediaType : acceptedMediaTypes) {
				for (HttpMessageConverter messageConverter : this.messageConverters) {
					if (messageConverter.canWrite(returnValueType, acceptedMediaType)) {
						messageConverter.write(returnValue, acceptedMediaType, outputMessage);
						return new ModelAndView();
					}
				}
			}
		}
		if (logger.isWarnEnabled()) {
			logger.warn("Could not find HttpMessageConverter that supports return type [" + returnValueType + "] and " +
					acceptedMediaTypes);
		}
		return null;
	}

}
