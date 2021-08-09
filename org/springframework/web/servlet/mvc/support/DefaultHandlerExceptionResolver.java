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

package org.springframework.web.servlet.mvc.support;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

/**
 * Default implementation of the {@link org.springframework.web.servlet.HandlerExceptionResolver
 * HandlerExceptionResolver} interface that resolves standard Spring exceptions and translates
 * them to corresponding HTTP status codes.
 *
 * <p>This exception resolver is enabled by default in the common Spring
 * {@link org.springframework.web.servlet.DispatcherServlet}.
 *
 * <p>
 * {@link orgspringframeworkwebservletHandlerExceptionResolver HandlerExceptionResolver}接口的默认实现,可解决标准Spr
 * ing异常并将其转换为相应的HTTP状态代码。
 * 
 *  <p>这个异常解析器默认情况下在常用的Spring {@link orgspringframeworkwebservletDispatcherServlet}中启用
 * 
 * 
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @since 3.0
 * @see org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
 * @see #handleNoSuchRequestHandlingMethod
 * @see #handleHttpRequestMethodNotSupported
 * @see #handleHttpMediaTypeNotSupported
 * @see #handleMissingServletRequestParameter
 * @see #handleServletRequestBindingException
 * @see #handleTypeMismatch
 * @see #handleHttpMessageNotReadable
 * @see #handleHttpMessageNotWritable
 * @see #handleMethodArgumentNotValidException
 * @see #handleMissingServletRequestParameter
 * @see #handleMissingServletRequestPartException
 * @see #handleBindException
 */
public class DefaultHandlerExceptionResolver extends AbstractHandlerExceptionResolver {

	/**
	 * Log category to use when no mapped handler is found for a request.
	 * <p>
	 *  没有找到请求的映射处理程序时使用的日志类别
	 * 
	 * 
	 * @see #pageNotFoundLogger
	 */
	public static final String PAGE_NOT_FOUND_LOG_CATEGORY = "org.springframework.web.servlet.PageNotFound";

	/**
	 * Additional logger to use when no mapped handler is found for a request.
	 * <p>
	 *  未找到请求的映射处理程序时要使用的附加记录器
	 * 
	 * 
	 * @see #PAGE_NOT_FOUND_LOG_CATEGORY
	 */
	protected static final Log pageNotFoundLogger = LogFactory.getLog(PAGE_NOT_FOUND_LOG_CATEGORY);


	/**
	 * Sets the {@linkplain #setOrder(int) order} to {@link #LOWEST_PRECEDENCE}.
	 * <p>
	 *  将{@linkplain #setOrder(int)order}设置为{@link #LOWEST_PRECEDENCE}
	 * 
	 */
	public DefaultHandlerExceptionResolver() {
		setOrder(Ordered.LOWEST_PRECEDENCE);
	}


	@Override
	@SuppressWarnings("deprecation")
	protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response,
			Object handler, Exception ex) {

		try {
			if (ex instanceof org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException) {
				return handleNoSuchRequestHandlingMethod((org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException) ex,
						request, response, handler);
			}
			else if (ex instanceof HttpRequestMethodNotSupportedException) {
				return handleHttpRequestMethodNotSupported((HttpRequestMethodNotSupportedException) ex, request,
						response, handler);
			}
			else if (ex instanceof HttpMediaTypeNotSupportedException) {
				return handleHttpMediaTypeNotSupported((HttpMediaTypeNotSupportedException) ex, request, response,
						handler);
			}
			else if (ex instanceof HttpMediaTypeNotAcceptableException) {
				return handleHttpMediaTypeNotAcceptable((HttpMediaTypeNotAcceptableException) ex, request, response,
						handler);
			}
			else if (ex instanceof MissingPathVariableException) {
				return handleMissingPathVariable((MissingPathVariableException) ex, request,
						response, handler);
			}
			else if (ex instanceof MissingServletRequestParameterException) {
				return handleMissingServletRequestParameter((MissingServletRequestParameterException) ex, request,
						response, handler);
			}
			else if (ex instanceof ServletRequestBindingException) {
				return handleServletRequestBindingException((ServletRequestBindingException) ex, request, response,
						handler);
			}
			else if (ex instanceof ConversionNotSupportedException) {
				return handleConversionNotSupported((ConversionNotSupportedException) ex, request, response, handler);
			}
			else if (ex instanceof TypeMismatchException) {
				return handleTypeMismatch((TypeMismatchException) ex, request, response, handler);
			}
			else if (ex instanceof HttpMessageNotReadableException) {
				return handleHttpMessageNotReadable((HttpMessageNotReadableException) ex, request, response, handler);
			}
			else if (ex instanceof HttpMessageNotWritableException) {
				return handleHttpMessageNotWritable((HttpMessageNotWritableException) ex, request, response, handler);
			}
			else if (ex instanceof MethodArgumentNotValidException) {
				return handleMethodArgumentNotValidException((MethodArgumentNotValidException) ex, request, response,
						handler);
			}
			else if (ex instanceof MissingServletRequestPartException) {
				return handleMissingServletRequestPartException((MissingServletRequestPartException) ex, request,
						response, handler);
			}
			else if (ex instanceof BindException) {
				return handleBindException((BindException) ex, request, response, handler);
			}
			else if (ex instanceof NoHandlerFoundException) {
				return handleNoHandlerFoundException((NoHandlerFoundException) ex, request, response, handler);
			}
			else if (ex instanceof AsyncRequestTimeoutException) {
				return handleAsyncRequestTimeoutException(
						(AsyncRequestTimeoutException) ex, request, response, handler);
			}
		}
		catch (Exception handlerException) {
			if (logger.isWarnEnabled()) {
				logger.warn("Handling of [" + ex.getClass().getName() + "] resulted in Exception", handlerException);
			}
		}
		return null;
	}

	/**
	 * Handle the case where no request handler method was found.
	 * <p>The default implementation logs a warning, sends an HTTP 404 error, and returns
	 * an empty {@code ModelAndView}. Alternatively, a fallback view could be chosen,
	 * or the NoSuchRequestHandlingMethodException could be rethrown as-is.
	 * <p>
	 * 处理没有请求处理程序方法的情况<p>默认实现记录一个警告,发送一个HTTP 404错误,并返回一个空的{@code ModelAndView}或者,可以选择一个后备视图,否则可以重新抛出NoSuchRe
	 * questHandlingMethodException按原样。
	 * 
	 * 
	 * @param ex the NoSuchRequestHandlingMethodException to be handled
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler the executed handler, or {@code null} if none chosen
	 * at the time of the exception (for example, if multipart resolution failed)
	 * @return an empty ModelAndView indicating the exception was handled
	 * @throws IOException potentially thrown from response.sendError()
	 * @deprecated as of 4.3, along with {@link org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException}
	 */
	@Deprecated
	protected ModelAndView handleNoSuchRequestHandlingMethod(org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException ex,
			HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

		pageNotFoundLogger.warn(ex.getMessage());
		response.sendError(HttpServletResponse.SC_NOT_FOUND);
		return new ModelAndView();
	}

	/**
	 * Handle the case where no request handler method was found for the particular HTTP request method.
	 * <p>The default implementation logs a warning, sends an HTTP 405 error, sets the "Allow" header,
	 * and returns an empty {@code ModelAndView}. Alternatively, a fallback view could be chosen,
	 * or the HttpRequestMethodNotSupportedException could be rethrown as-is.
	 * <p>
	 *  处理没有为特定HTTP请求方法找到请求处理程序方法的情况<p>默认实现记录一个警告,发送HTTP 405错误,设置"允许"头,并返回一个空的{@code ModelAndView}或者,可以选择回退视
	 * 图,或者HttpRequestMethodNotSupportedException可以按原样重新抛出。
	 * 
	 * 
	 * @param ex the HttpRequestMethodNotSupportedException to be handled
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler the executed handler, or {@code null} if none chosen
	 * at the time of the exception (for example, if multipart resolution failed)
	 * @return an empty ModelAndView indicating the exception was handled
	 * @throws IOException potentially thrown from response.sendError()
	 */
	protected ModelAndView handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
			HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

		pageNotFoundLogger.warn(ex.getMessage());
		String[] supportedMethods = ex.getSupportedMethods();
		if (supportedMethods != null) {
			response.setHeader("Allow", StringUtils.arrayToDelimitedString(supportedMethods, ", "));
		}
		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, ex.getMessage());
		return new ModelAndView();
	}

	/**
	 * Handle the case where no {@linkplain org.springframework.http.converter.HttpMessageConverter message converters}
	 * were found for the PUT or POSTed content.
	 * <p>The default implementation sends an HTTP 415 error, sets the "Accept" header,
	 * and returns an empty {@code ModelAndView}. Alternatively, a fallback view could
	 * be chosen, or the HttpMediaTypeNotSupportedException could be rethrown as-is.
	 * <p>
	 * 处理没有为PUT或POSTed内容找到{@linkplain orgspringframeworkhttpconverterHttpMessageConverter消息转换器}的情况<p>默认实现发送H
	 * TTP 415错误,设置"Accept"标头,并返回一个空的{@code ModelAndView}或者,可以选择回退视图,或者HttpMediaTypeNotSupportedException可以按
	 * 原样重新抛出。
	 * 
	 * 
	 * @param ex the HttpMediaTypeNotSupportedException to be handled
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler the executed handler
	 * @return an empty ModelAndView indicating the exception was handled
	 * @throws IOException potentially thrown from response.sendError()
	 */
	protected ModelAndView handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
			HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

		response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
		List<MediaType> mediaTypes = ex.getSupportedMediaTypes();
		if (!CollectionUtils.isEmpty(mediaTypes)) {
			response.setHeader("Accept", MediaType.toString(mediaTypes));
		}
		return new ModelAndView();
	}

	/**
	 * Handle the case where no {@linkplain org.springframework.http.converter.HttpMessageConverter message converters}
	 * were found that were acceptable for the client (expressed via the {@code Accept} header.
	 * <p>The default implementation sends an HTTP 406 error and returns an empty {@code ModelAndView}.
	 * Alternatively, a fallback view could be chosen, or the HttpMediaTypeNotAcceptableException
	 * could be rethrown as-is.
	 * <p>
	 * 处理没有发现客户端可以接受的{@linkplain orgspringframeworkhttpconverterHttpMessageConverter消息转换器}的情况(通过{@code Accept}
	 * 标头表示)<p>默认实现发送HTTP 406错误,并返回一个空的{@code ModelAndView}或者,可以选择回退视图,或者HttpMediaTypeNotAcceptableException
	 * 可以按原样重新抛出。
	 * 
	 * 
	 * @param ex the HttpMediaTypeNotAcceptableException to be handled
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler the executed handler
	 * @return an empty ModelAndView indicating the exception was handled
	 * @throws IOException potentially thrown from response.sendError()
	 */
	protected ModelAndView handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex,
			HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

		response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
		return new ModelAndView();
	}

	/**
	 * Handle the case when a declared path variable does not match any extracted URI variable.
	 * <p>The default implementation sends an HTTP 500 error, and returns an empty {@code ModelAndView}.
	 * Alternatively, a fallback view could be chosen, or the MissingPathVariableException
	 * could be rethrown as-is.
	 * <p>
	 *  当声明的路径变量与任何提取的URI变量不匹配时,处理这种情况<p>默认实现发送一个HTTP 500错误,并返回一个空的{@code ModelAndView}或者,可以选择一个回退视图,或者Missi
	 * ngPathVariableException可以是按原样推翻。
	 * 
	 * 
	 * @param ex the MissingPathVariableException to be handled
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler the executed handler
	 * @return an empty ModelAndView indicating the exception was handled
	 * @throws IOException potentially thrown from response.sendError()
	 * @since 4.2
	 */
	protected ModelAndView handleMissingPathVariable(MissingPathVariableException ex,
			HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
		return new ModelAndView();
	}

	/**
	 * Handle the case when a required parameter is missing.
	 * <p>The default implementation sends an HTTP 400 error, and returns an empty {@code ModelAndView}.
	 * Alternatively, a fallback view could be chosen, or the MissingServletRequestParameterException
	 * could be rethrown as-is.
	 * <p>
	 * 当需要的参数丢失时处理案例<p>默认实现发送HTTP 400错误,并返回一个空的{@code ModelAndView}或者,可以选择一个后备视图,或者MissingServletRequestPara
	 * meterException可以按原样重新抛出。
	 * 
	 * 
	 * @param ex the MissingServletRequestParameterException to be handled
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler the executed handler
	 * @return an empty ModelAndView indicating the exception was handled
	 * @throws IOException potentially thrown from response.sendError()
	 */
	protected ModelAndView handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
			HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

		response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
		return new ModelAndView();
	}

	/**
	 * Handle the case when an unrecoverable binding exception occurs - e.g. required header, required cookie.
	 * <p>The default implementation sends an HTTP 400 error, and returns an empty {@code ModelAndView}.
	 * Alternatively, a fallback view could be chosen, or the exception could be rethrown as-is.
	 * <p>
	 *  处理这种情况时发生不可恢复的绑定异常 - 例如所需的头,所需的cookie <p>默认实现发送一个HTTP 400错误,并返回一个空的{@code ModelAndView}或者,可以选择回退视图,或
	 * 异常按原样推翻。
	 * 
	 * 
	 * @param ex the exception to be handled
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler the executed handler
	 * @return an empty ModelAndView indicating the exception was handled
	 * @throws IOException potentially thrown from response.sendError()
	 */
	protected ModelAndView handleServletRequestBindingException(ServletRequestBindingException ex,
			HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

		response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
		return new ModelAndView();
	}

	/**
	 * Handle the case when a {@link org.springframework.web.bind.WebDataBinder} conversion cannot occur.
	 * <p>The default implementation sends an HTTP 500 error, and returns an empty {@code ModelAndView}.
	 * Alternatively, a fallback view could be chosen, or the TypeMismatchException could be rethrown as-is.
	 * <p>
	 * 当{@link orgspringframeworkwebbindWebDataBinder}转换不能发生时处理这种情况<p>默认实现发送HTTP 500错误,并返回一个空的{@code ModelAndView}
	 * 或者,可以选择一个回退视图,或者可以将TypeMismatchException重新抛出为-is。
	 * 
	 * 
	 * @param ex the ConversionNotSupportedException to be handled
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler the executed handler
	 * @return an empty ModelAndView indicating the exception was handled
	 * @throws IOException potentially thrown from response.sendError()
	 */
	protected ModelAndView handleConversionNotSupported(ConversionNotSupportedException ex,
			HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

		if (logger.isWarnEnabled()) {
			logger.warn("Failed to convert request element: " + ex);
		}
		sendServerError(ex, request, response);
		return new ModelAndView();
	}

	/**
	 * Handle the case when a {@link org.springframework.web.bind.WebDataBinder} conversion error occurs.
	 * <p>The default implementation sends an HTTP 400 error, and returns an empty {@code ModelAndView}.
	 * Alternatively, a fallback view could be chosen, or the TypeMismatchException could be rethrown as-is.
	 * <p>
	 *  当发生{@link orgspringframeworkwebbindWebDataBinder}转换错误时处理这种情况<p>默认实现发送HTTP 400错误,并返回一个空的{@code ModelAndView}
	 * 或者,可以选择回退视图,或者可以将TypeMismatchException重新抛出为-is。
	 * 
	 * 
	 * @param ex the TypeMismatchException to be handled
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler the executed handler
	 * @return an empty ModelAndView indicating the exception was handled
	 * @throws IOException potentially thrown from response.sendError()
	 */
	protected ModelAndView handleTypeMismatch(TypeMismatchException ex,
			HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

		if (logger.isWarnEnabled()) {
			logger.warn("Failed to bind request element: " + ex);
		}
		response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		return new ModelAndView();
	}

	/**
	 * Handle the case where a {@linkplain org.springframework.http.converter.HttpMessageConverter message converter}
	 * cannot read from a HTTP request.
	 * <p>The default implementation sends an HTTP 400 error, and returns an empty {@code ModelAndView}.
	 * Alternatively, a fallback view could be chosen, or the HttpMediaTypeNotSupportedException could be
	 * rethrown as-is.
	 * <p>
	 * 处理{@linkplain orgspringframeworkhttpconverterHttpMessageConverter消息转换器}无法从HTTP请求中读取的情况<p>默认实现发送HTTP 4
	 * 00错误,并返回一个空的{@code ModelAndView}或者,可以选择回退视图,或者HttpMediaTypeNotSupportedException可以按原样重新抛出。
	 * 
	 * 
	 * @param ex the HttpMessageNotReadableException to be handled
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler the executed handler
	 * @return an empty ModelAndView indicating the exception was handled
	 * @throws IOException potentially thrown from response.sendError()
	 */
	protected ModelAndView handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

		if (logger.isWarnEnabled()) {
			logger.warn("Failed to read HTTP message: " + ex);
		}
		response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		return new ModelAndView();
	}

	/**
	 * Handle the case where a {@linkplain org.springframework.http.converter.HttpMessageConverter message converter}
	 * cannot write to a HTTP request.
	 * <p>The default implementation sends an HTTP 500 error, and returns an empty {@code ModelAndView}.
	 * Alternatively, a fallback view could be chosen, or the HttpMediaTypeNotSupportedException could be
	 * rethrown as-is.
	 * <p>
	 *  处理{@linkplain orgspringframeworkhttpconverterHttpMessageConverter消息转换器}无法写入HTTP请求的情况<p>默认实现发送HTTP 50
	 * 0错误,并返回一个空的{@code ModelAndView}或者,可以选择回退视图,或者HttpMediaTypeNotSupportedException可以按原样重新抛出。
	 * 
	 * 
	 * @param ex the HttpMessageNotWritableException to be handled
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler the executed handler
	 * @return an empty ModelAndView indicating the exception was handled
	 * @throws IOException potentially thrown from response.sendError()
	 */
	protected ModelAndView handleHttpMessageNotWritable(HttpMessageNotWritableException ex,
			HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

		if (logger.isWarnEnabled()) {
			logger.warn("Failed to write HTTP message: " + ex);
		}
		sendServerError(ex, request, response);
		return new ModelAndView();
	}

	/**
	 * Handle the case where an argument annotated with {@code @Valid} such as
	 * an {@link RequestBody} or {@link RequestPart} argument fails validation.
	 * An HTTP 400 error is sent back to the client.
	 * <p>
	 * 处理使用{@code @Valid}注释的参数(例如{@link RequestBody}或{@link RequestPart}参数)验证失败的情况)将HTTP 400错误发送回客户端
	 * 
	 * 
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler the executed handler
	 * @return an empty ModelAndView indicating the exception was handled
	 * @throws IOException potentially thrown from response.sendError()
	 */
	protected ModelAndView handleMethodArgumentNotValidException(MethodArgumentNotValidException ex,
			HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

 		response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		return new ModelAndView();
	}

	/**
	 * Handle the case where an {@linkplain RequestPart @RequestPart}, a {@link MultipartFile},
	 * or a {@code javax.servlet.http.Part} argument is required but is missing.
	 * An HTTP 400 error is sent back to the client.
	 * <p>
	 *  处理需要{@linkplain RequestPart @RequestPart},{@link MultipartFile}或{@code javaxservlethttpPart}参数但缺少的情况
	 * 。
	 * 将HTTP 400错误发送回客户端。
	 * 
	 * 
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler the executed handler
	 * @return an empty ModelAndView indicating the exception was handled
	 * @throws IOException potentially thrown from response.sendError()
	 */
	protected ModelAndView handleMissingServletRequestPartException(MissingServletRequestPartException ex,
			HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

		response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
		return new ModelAndView();
	}

	/**
	 * Handle the case where an {@linkplain ModelAttribute @ModelAttribute} method
	 * argument has binding or validation errors and is not followed by another
	 * method argument of type {@link BindingResult}.
	 * By default, an HTTP 400 error is sent back to the client.
	 * <p>
	 *  处理{@linkplain ModelAttribute @ModelAttribute}方法参数具有绑定或验证错误的情况,并且后面不是另一个类型为{@link BindingResult}的方法参数
	 * 。
	 * 默认情况下,HTTP 400错误将发送回客户端。
	 * 
	 * 
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler the executed handler
	 * @return an empty ModelAndView indicating the exception was handled
	 * @throws IOException potentially thrown from response.sendError()
	 */
	protected ModelAndView handleBindException(BindException ex, HttpServletRequest request,
			HttpServletResponse response, Object handler) throws IOException {

		response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		return new ModelAndView();
	}

	/**
	 * Handle the case where no handler was found during the dispatch.
	 * <p>The default implementation sends an HTTP 404 error and returns an empty
	 * {@code ModelAndView}. Alternatively, a fallback view could be chosen,
	 * or the NoHandlerFoundException could be rethrown as-is.
	 * <p>
	 * 处理在调度期间没有找到处理程序的情况<p>默认实现发送HTTP 404错误并返回一个空的{@code ModelAndView}或者,可以选择回退视图,否则NoHandlerFoundException
	 * 可以按原样重新抛出。
	 * 
	 * 
	 * @param ex the NoHandlerFoundException to be handled
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler the executed handler, or {@code null} if none chosen
	 * at the time of the exception (for example, if multipart resolution failed)
	 * @return an empty ModelAndView indicating the exception was handled
	 * @throws IOException potentially thrown from response.sendError()
	 * @since 4.0
	 */
	protected ModelAndView handleNoHandlerFoundException(NoHandlerFoundException ex,
			HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

		response.sendError(HttpServletResponse.SC_NOT_FOUND);
		return new ModelAndView();
	}

	/**
	 * Handle the case where an async request timed out.
	 * <p>The default implementation sends an HTTP 503 error.
	 * <p>
	 *  处理异步请求超时的情况<p>默认实现发送HTTP 503错误
	 * 
	 * 
	 * @param ex the {@link AsyncRequestTimeoutException }to be handled
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler the executed handler, or {@code null} if none chosen
	 * at the time of the exception (for example, if multipart resolution failed)
	 * @return an empty ModelAndView indicating the exception was handled
	 * @throws IOException potentially thrown from response.sendError()
	 * @since 4.2.8
	 */
	protected ModelAndView handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex,
			HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

		response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
		return new ModelAndView();
	}


	/**
	 * Invoked to send a server error. Sets the status to 500 and also sets the
	 * request attribute "javax.servlet.error.exception" to the Exception.
	 * <p>
	 *  调用发送服务器错误将状态设置为500,并将请求属性"javaxservleterrorexception"设置为异常
	 */
	protected void sendServerError(Exception ex, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		request.setAttribute("javax.servlet.error.exception", ex);
		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}

}
