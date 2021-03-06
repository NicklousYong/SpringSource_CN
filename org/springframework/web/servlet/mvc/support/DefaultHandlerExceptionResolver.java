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
 * {@link orgspringframeworkwebservletHandlerExceptionResolver HandlerExceptionResolver}?????????????????????,???????????????Spr
 * ing?????????????????????????????????HTTP???????????????
 * 
 * ??<p>????????????????????????????????????????????????Spring {@link orgspringframeworkwebservletDispatcherServlet}?????????
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
	 * ?????????????????????????????????????????????????????????????????
	 * 
	 * 
	 * @see #pageNotFoundLogger
	 */
	public static final String PAGE_NOT_FOUND_LOG_CATEGORY = "org.springframework.web.servlet.PageNotFound";

	/**
	 * Additional logger to use when no mapped handler is found for a request.
	 * <p>
	 * ????????????????????????????????????????????????????????????????????
	 * 
	 * 
	 * @see #PAGE_NOT_FOUND_LOG_CATEGORY
	 */
	protected static final Log pageNotFoundLogger = LogFactory.getLog(PAGE_NOT_FOUND_LOG_CATEGORY);


	/**
	 * Sets the {@linkplain #setOrder(int) order} to {@link #LOWEST_PRECEDENCE}.
	 * <p>
	 * ?????{@linkplain #setOrder(int)order}?????????{@link #LOWEST_PRECEDENCE}
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
	 * ?????????????????????????????????????????????<p>??????????????????????????????,????????????HTTP 404??????,?????????????????????{@code ModelAndView}??????,??????????????????????????????,????????????????????????NoSuchRe
	 * questHandlingMethodException????????????
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
	 * ???????????????????????HTTP???????????????????????????????????????????????????<p>??????????????????????????????,??????HTTP 405??????,??????"??????"???,?????????????????????{@code ModelAndView}??????,?????????????????????
	 * ???,??????HttpRequestMethodNotSupportedException??????????????????????????????
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
	 * ???????????????PUT???POSTed????????????{@linkplain orgspringframeworkhttpconverterHttpMessageConverter???????????????}?????????<p>??????????????????H
	 * TTP 415??????,??????"Accept"??????,?????????????????????{@code ModelAndView}??????,????????????????????????,??????HttpMediaTypeNotSupportedException?????????
	 * ?????????????????????
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
	 * ??????????????????????????????????????????{@linkplain orgspringframeworkhttpconverterHttpMessageConverter???????????????}?????????(??????{@code Accept}
	 * ????????????)<p>??????????????????HTTP 406??????,?????????????????????{@code ModelAndView}??????,????????????????????????,??????HttpMediaTypeNotAcceptableException
	 * ??????????????????????????????
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
	 * ????????????????????????????????????????????URI??????????????????,??????????????????<p>????????????????????????HTTP 500??????,?????????????????????{@code ModelAndView}??????,??????????????????????????????,??????Missi
	 * ngPathVariableException???????????????????????????
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
	 * ???????????????????????????????????????<p>??????????????????HTTP 400??????,?????????????????????{@code ModelAndView}??????,??????????????????????????????,??????MissingServletRequestPara
	 * meterException??????????????????????????????
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
	 * ???????????????????????????????????????????????????????? - ??????????????????,?????????cookie <p>????????????????????????HTTP 400??????,?????????????????????{@code ModelAndView}??????,????????????????????????,???
	 * ????????????????????????
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
	 * ???{@link orgspringframeworkwebbindWebDataBinder}???????????????????????????????????????<p>??????????????????HTTP 500??????,?????????????????????{@code ModelAndView}
	 * ??????,??????????????????????????????,???????????????TypeMismatchException???????????????-is???
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
	 * ???????????{@link orgspringframeworkwebbindWebDataBinder}?????????????????????????????????<p>??????????????????HTTP 400??????,?????????????????????{@code ModelAndView}
	 * ??????,????????????????????????,???????????????TypeMismatchException???????????????-is???
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
	 * ??????{@linkplain orgspringframeworkhttpconverterHttpMessageConverter???????????????}?????????HTTP????????????????????????<p>??????????????????HTTP 4
	 * 00??????,?????????????????????{@code ModelAndView}??????,????????????????????????,??????HttpMediaTypeNotSupportedException??????????????????????????????
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
	 * ????????{@linkplain orgspringframeworkhttpconverterHttpMessageConverter???????????????}????????????HTTP???????????????<p>??????????????????HTTP 50
	 * 0??????,?????????????????????{@code ModelAndView}??????,????????????????????????,??????HttpMediaTypeNotSupportedException??????????????????????????????
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
	 * ????????????{@code @Valid}???????????????(??????{@link RequestBody}???{@link RequestPart}??????)?????????????????????)???HTTP 400????????????????????????
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
	 * ??????????????{@linkplain RequestPart @RequestPart},{@link MultipartFile}???{@code javaxservlethttpPart}????????????????????????
	 * ???
	 * ???HTTP 400???????????????????????????
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
	 * ????????{@linkplain ModelAttribute @ModelAttribute}????????????????????????????????????????????????,????????????????????????????????????{@link BindingResult}???????????????
	 * ???
	 * ???????????????,HTTP 400??????????????????????????????
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
	 * ??????????????????????????????????????????????????????<p>??????????????????HTTP 404???????????????????????????{@code ModelAndView}??????,????????????????????????,??????NoHandlerFoundException
	 * ??????????????????????????????
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
	 * ???????????????????????????????????<p>??????????????????HTTP 503??????
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
	 * ???????????????????????????????????????????????500,??????????????????"javaxservleterrorexception"???????????????
	 */
	protected void sendServerError(Exception ex, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		request.setAttribute("javax.servlet.error.exception", ex);
		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}

}
