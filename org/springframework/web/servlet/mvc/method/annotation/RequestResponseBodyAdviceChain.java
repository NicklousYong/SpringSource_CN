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

package org.springframework.web.servlet.mvc.method.annotation;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.CollectionUtils;
import org.springframework.web.method.ControllerAdviceBean;


/**
 * Invokes {@link RequestBodyAdvice} and {@link ResponseBodyAdvice} where each
 * instance may be (and is most likely) wrapped with
 * {@link org.springframework.web.method.ControllerAdviceBean ControllerAdviceBean}.
 *
 * <p>
 * 
 * @author Rossen Stoyanchev
 * @since 4.2
 */
class RequestResponseBodyAdviceChain implements RequestBodyAdvice, ResponseBodyAdvice<Object> {

	private final List<Object> requestBodyAdvice = new ArrayList<Object>(4);

	private final List<Object> responseBodyAdvice = new ArrayList<Object>(4);


	/**
	 * Create an instance from a list of objects that are either of type
	 * {@code ControllerAdviceBean} or {@code RequestBodyAdvice}.
	 * <p>
	 *  调用{@link RequestBodyAdvice}和{@link ResponseBodyAdvice},其中每个实例可能(并且很有可能)用{@link orgspringframeworkwebmethodControllerAdviceBean ControllerAdviceBean}
	 * 包装。
	 * 
	 */
	public RequestResponseBodyAdviceChain(List<Object> requestResponseBodyAdvice) {
		initAdvice(requestResponseBodyAdvice);
	}

	private void initAdvice(List<Object> requestResponseBodyAdvice) {
		if (requestResponseBodyAdvice == null) {
			return;
		}
		for (Object advice : requestResponseBodyAdvice) {
			Class<?> beanType = (advice instanceof ControllerAdviceBean ?
					((ControllerAdviceBean) advice).getBeanType() : advice.getClass());
			if (RequestBodyAdvice.class.isAssignableFrom(beanType)) {
				this.requestBodyAdvice.add(advice);
			}
			else if (ResponseBodyAdvice.class.isAssignableFrom(beanType)) {
				this.responseBodyAdvice.add(advice);
			}
		}
	}

	private List<Object> getAdvice(Class<?> adviceType) {
		if (RequestBodyAdvice.class == adviceType) {
			return this.requestBodyAdvice;
		}
		else if (ResponseBodyAdvice.class == adviceType) {
			return this.responseBodyAdvice;
		}
		else {
			throw new IllegalArgumentException("Unexpected adviceType: " + adviceType);
		}
	}


	@Override
	public boolean supports(MethodParameter param, Type type, Class<? extends HttpMessageConverter<?>> converterType) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter,
			Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {

		for (RequestBodyAdvice advice : getMatchingAdvice(parameter, RequestBodyAdvice.class)) {
			if (advice.supports(parameter, targetType, converterType)) {
				body = advice.handleEmptyBody(body, inputMessage, parameter, targetType, converterType);
			}
		}
		return body;
	}

	@Override
	public HttpInputMessage beforeBodyRead(HttpInputMessage request, MethodParameter parameter,
			Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {

		for (RequestBodyAdvice advice : getMatchingAdvice(parameter, RequestBodyAdvice.class)) {
			if (advice.supports(parameter, targetType, converterType)) {
				request = advice.beforeBodyRead(request, parameter, targetType, converterType);
			}
		}
		return request;
	}

	@Override
	public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter,
			Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {

		for (RequestBodyAdvice advice : getMatchingAdvice(parameter, RequestBodyAdvice.class)) {
			if (advice.supports(parameter, targetType, converterType)) {
				body = advice.afterBodyRead(body, inputMessage, parameter, targetType, converterType);
			}
		}
		return body;
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType contentType,
			Class<? extends HttpMessageConverter<?>> converterType,
			ServerHttpRequest request, ServerHttpResponse response) {

		return processBody(body, returnType, contentType, converterType, request, response);
	}

	@SuppressWarnings("unchecked")
	private <T> Object processBody(Object body, MethodParameter returnType, MediaType contentType,
			Class<? extends HttpMessageConverter<?>> converterType,
			ServerHttpRequest request, ServerHttpResponse response) {

		for (ResponseBodyAdvice<?> advice : getMatchingAdvice(returnType, ResponseBodyAdvice.class)) {
			if (advice.supports(returnType, converterType)) {
				body = ((ResponseBodyAdvice<T>) advice).beforeBodyWrite((T) body, returnType,
						contentType, converterType, request, response);
			}
		}
		return body;
	}

	@SuppressWarnings("unchecked")
	private <A> List<A> getMatchingAdvice(MethodParameter parameter, Class<? extends A> adviceType) {
		List<Object> availableAdvice = getAdvice(adviceType);
		if (CollectionUtils.isEmpty(availableAdvice)) {
			return Collections.emptyList();
		}
		List<A> result = new ArrayList<A>(availableAdvice.size());
		for (Object advice : availableAdvice) {
			if (advice instanceof ControllerAdviceBean) {
				ControllerAdviceBean adviceBean = (ControllerAdviceBean) advice;
				if (!adviceBean.isApplicableToBeanType(parameter.getContainingClass())) {
					continue;
				}
				advice = adviceBean.resolveBean();
			}
			if (adviceType.isAssignableFrom(advice.getClass())) {
				result.add((A) advice);
			}
		}
		return result;
	}

}
