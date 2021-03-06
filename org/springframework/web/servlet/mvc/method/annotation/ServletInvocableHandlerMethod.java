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

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.concurrent.Callable;

import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import org.springframework.web.method.support.InvocableHandlerMethod;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.View;
import org.springframework.web.util.NestedServletException;

/**
 * Extends {@link InvocableHandlerMethod} with the ability to handle return
 * values through a registered {@link HandlerMethodReturnValueHandler} and
 * also supports setting the response status based on a method-level
 * {@code @ResponseStatus} annotation.
 *
 * <p>A {@code null} return value (including void) may be interpreted as the
 * end of request processing in combination with a {@code @ResponseStatus}
 * annotation, a not-modified check condition
 * (see {@link ServletWebRequest#checkNotModified(long)}), or
 * a method argument that provides access to the response stream.
 *
 * <p>
 * ??????{@link InvocableHandlerMethod}?????????????????????{@link HandlerMethodReturnValueHandler}???????????????,??????????????????????????????{@code @ResponseStatus}
 * ???????????????????????????
 * 
 * ??<p> {@code null}?????????(??????void)??????????????????{@code @ResponseStatus}??????(????????????????????????)??????????????????????????????(?????????{@link ServletWebRequest#checkNotModified (long)}
 * )??????????????????????????????????????????????????????
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class ServletInvocableHandlerMethod extends InvocableHandlerMethod {

	private static final Method CALLABLE_METHOD = ClassUtils.getMethod(Callable.class, "call");


	private HttpStatus responseStatus;

	private String responseReason;

	private HandlerMethodReturnValueHandlerComposite returnValueHandlers;


	/**
	 * Creates an instance from the given handler and method.
	 * <p>
	 * ?????????????????????????????????????????????????????
	 * 
	 */
	public ServletInvocableHandlerMethod(Object handler, Method method) {
		super(handler, method);
		initResponseStatus();
	}

	/**
	 * Create an instance from a {@code HandlerMethod}.
	 * <p>
	 * ?????{@code HandlerMethod}??????????????????
	 * 
	 */
	public ServletInvocableHandlerMethod(HandlerMethod handlerMethod) {
		super(handlerMethod);
		initResponseStatus();
	}

	private void initResponseStatus() {
		ResponseStatus annotation = getMethodAnnotation(ResponseStatus.class);
		if (annotation == null) {
			annotation = AnnotatedElementUtils.findMergedAnnotation(getBeanType(), ResponseStatus.class);
		}
		if (annotation != null) {
			this.responseStatus = annotation.code();
			this.responseReason = annotation.reason();
		}
	}


	/**
	 * Register {@link HandlerMethodReturnValueHandler} instances to use to
	 * handle return values.
	 * <p>
	 * ????????????????????????????????{@link HandlerMethodReturnValueHandler}??????
	 * 
	 */
	public void setHandlerMethodReturnValueHandlers(HandlerMethodReturnValueHandlerComposite returnValueHandlers) {
		this.returnValueHandlers = returnValueHandlers;
	}

	/**
	 * Invokes the method and handles the return value through one of the
	 * configured {@link HandlerMethodReturnValueHandler}s.
	 * <p>
	 * ?????????????????????????????????{@link HandlerMethodReturnValueHandler}????????????????????????
	 * 
	 * 
	 * @param webRequest the current request
	 * @param mavContainer the ModelAndViewContainer for this request
	 * @param providedArgs "given" arguments matched by type (not resolved)
	 */
	public void invokeAndHandle(ServletWebRequest webRequest,
			ModelAndViewContainer mavContainer, Object... providedArgs) throws Exception {

		Object returnValue = invokeForRequest(webRequest, mavContainer, providedArgs);
		setResponseStatus(webRequest);

		if (returnValue == null) {
			if (isRequestNotModified(webRequest) || hasResponseStatus() || mavContainer.isRequestHandled()) {
				mavContainer.setRequestHandled(true);
				return;
			}
		}
		else if (StringUtils.hasText(this.responseReason)) {
			mavContainer.setRequestHandled(true);
			return;
		}

		mavContainer.setRequestHandled(false);
		try {
			this.returnValueHandlers.handleReturnValue(
					returnValue, getReturnValueType(returnValue), mavContainer, webRequest);
		}
		catch (Exception ex) {
			if (logger.isTraceEnabled()) {
				logger.trace(getReturnValueHandlingErrorMessage("Error handling return value", returnValue), ex);
			}
			throw ex;
		}
	}

	/**
	 * Set the response status according to the {@link ResponseStatus} annotation.
	 * <p>
	 * ????????{@link ResponseStatus}????????????????????????
	 * 
	 */
	private void setResponseStatus(ServletWebRequest webRequest) throws IOException {
		if (this.responseStatus == null) {
			return;
		}
		if (StringUtils.hasText(this.responseReason)) {
			webRequest.getResponse().sendError(this.responseStatus.value(), this.responseReason);
		}
		else {
			webRequest.getResponse().setStatus(this.responseStatus.value());
		}
		// to be picked up by the RedirectView
		webRequest.getRequest().setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, this.responseStatus);
	}

	/**
	 * Does the given request qualify as "not modified"?
	 * <p>
	 * ?????????????????????????????"?????????"??????????
	 * 
	 * 
	 * @see ServletWebRequest#checkNotModified(long)
	 * @see ServletWebRequest#checkNotModified(String)
	 */
	private boolean isRequestNotModified(ServletWebRequest webRequest) {
		return webRequest.isNotModified();
	}

	/**
	 * Does this method have the response status instruction?
	 * <p>
	 * ??????????????????????????????????????????
	 * 
	 */
	private boolean hasResponseStatus() {
		return (this.responseStatus != null);
	}

	private String getReturnValueHandlingErrorMessage(String message, Object returnValue) {
		StringBuilder sb = new StringBuilder(message);
		if (returnValue != null) {
			sb.append(" [type=").append(returnValue.getClass().getName()).append("]");
		}
		sb.append(" [value=").append(returnValue).append("]");
		return getDetailedErrorMessage(sb.toString());
	}

	/**
	 * Create a nested ServletInvocableHandlerMethod subclass that returns the
	 * the given value (or raises an Exception if the value is one) rather than
	 * actually invoking the controller method. This is useful when processing
	 * async return values (e.g. Callable, DeferredResult, ListenableFuture).
	 * <p>
	 * ??????????????????????????????????????ServletInvocableHandlerMethod??????(????????????1,???????????????),???????????????????????????????????????????????????????????????(??????Callable,DeferredRe
	 * sult,ListenableFuture)???
	 * 
	 */
	ServletInvocableHandlerMethod wrapConcurrentResult(Object result) {
		return new ConcurrentResultHandlerMethod(result, new ConcurrentResultMethodParameter(result));
	}


	/**
	 * A nested subclass of {@code ServletInvocableHandlerMethod} that uses a
	 * simple {@link Callable} instead of the original controller as the handler in
	 * order to return the fixed (concurrent) result value given to it. Effectively
	 * "resumes" processing with the asynchronously produced return value.
	 * <p>
	 * {@code ServletInvocableHandlerMethod}???????????????,???????????????{@link Callable}??????????????????????????????????????????,????????????????????????(??????)????????????
	 * ?????????"????????????"?????????"??????"????????????
	 * 
	 */
	private class ConcurrentResultHandlerMethod extends ServletInvocableHandlerMethod {

		private final MethodParameter returnType;

		public ConcurrentResultHandlerMethod(final Object result, ConcurrentResultMethodParameter returnType) {
			super(new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					if (result instanceof Exception) {
						throw (Exception) result;
					}
					else if (result instanceof Throwable) {
						throw new NestedServletException("Async processing failed", (Throwable) result);
					}
					return result;
				}
			}, CALLABLE_METHOD);
			setHandlerMethodReturnValueHandlers(ServletInvocableHandlerMethod.this.returnValueHandlers);
			this.returnType = returnType;
		}

		/**
		 * Bridge to actual controller type-level annotations.
		 * <p>
		 * ????????????????????????????????????????????
		 * 
		 */
		@Override
		public Class<?> getBeanType() {
			return ServletInvocableHandlerMethod.this.getBeanType();
		}

		/**
		 * Bridge to actual return value or generic type within the declared
		 * async return type, e.g. Foo instead of {@code DeferredResult<Foo>}.
		 * <p>
		 * ?????????????????????????????????????????????????????????????????????????????,??????Foo?????????{@code DeferredResult <Foo>}
		 * 
		 */
		@Override
		public MethodParameter getReturnValueType(Object returnValue) {
			return this.returnType;
		}

		/**
		 * Bridge to controller method-level annotations.
		 * <p>
		 * ???????????????????????????????????
		 * 
		 */
		@Override
		public <A extends Annotation> A getMethodAnnotation(Class<A> annotationType) {
			return ServletInvocableHandlerMethod.this.getMethodAnnotation(annotationType);
		}

		/**
		 * Bridge to controller method-level annotations.
		 * <p>
		 * ???????????????????????????????????
		 * 
		 */
		@Override
		public <A extends Annotation> boolean hasMethodAnnotation(Class<A> annotationType) {
			return ServletInvocableHandlerMethod.this.hasMethodAnnotation(annotationType);
		}
	}


	/**
	 * MethodParameter subclass based on the actual return value type or if
	 * that's null falling back on the generic type within the declared async
	 * return type, e.g. Foo instead of {@code DeferredResult<Foo>}.
	 * <p>
	 * ??MethodParameter????????????????????????????????????,?????????????????????null,????????????????????????????????????????????????????????????,??????Foo,?????????{@code DeferredResult <Foo>}
	 */
	private class ConcurrentResultMethodParameter extends HandlerMethodParameter {

		private final Object returnValue;

		private final ResolvableType returnType;

		public ConcurrentResultMethodParameter(Object returnValue) {
			super(-1);
			this.returnValue = returnValue;
			this.returnType = ResolvableType.forType(super.getGenericParameterType()).getGeneric(0);
		}

		public ConcurrentResultMethodParameter(ConcurrentResultMethodParameter original) {
			super(original);
			this.returnValue = original.returnValue;
			this.returnType = original.returnType;
		}

		@Override
		public Class<?> getParameterType() {
			if (this.returnValue != null) {
				return this.returnValue.getClass();
			}
			if (!ResolvableType.NONE.equals(this.returnType)) {
				return this.returnType.getRawClass();
			}
			return super.getParameterType();
		}

		@Override
		public Type getGenericParameterType() {
			return this.returnType.getType();
		}

		@Override
		public ConcurrentResultMethodParameter clone() {
			return new ConcurrentResultMethodParameter(this);
		}
	}

}
