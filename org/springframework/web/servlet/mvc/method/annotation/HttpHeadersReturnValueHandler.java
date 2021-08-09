/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2014 the original author or authors.
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

import javax.servlet.http.HttpServletResponse;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.util.Assert;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Handles {@link HttpHeaders} return values.
 *
 * <p>
 *  除非适用法律要求或以书面形式同意,根据许可证分发的软件以"按原样"分发,不附带任何明示或暗示的担保或条件,请参阅许可证中有关特定语言的权限和限制许可证
 * 
 * 
 * @author Stephane Nicoll
 * @since 4.0.1
 */
public class HttpHeadersReturnValueHandler implements HandlerMethodReturnValueHandler {

	@Override
	public boolean supportsReturnType(MethodParameter returnType) {
		return HttpHeaders.class.isAssignableFrom(returnType.getParameterType());
	}

	@Override
	@SuppressWarnings("resource")
	public void handleReturnValue(Object returnValue, MethodParameter returnType,
			ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {

		mavContainer.setRequestHandled(true);

		Assert.isInstanceOf(HttpHeaders.class, returnValue);
		HttpHeaders headers = (HttpHeaders) returnValue;

		if (!headers.isEmpty()) {
			HttpServletResponse servletResponse = webRequest.getNativeResponse(HttpServletResponse.class);
			ServletServerHttpResponse outputMessage = new ServletServerHttpResponse(servletResponse);
			outputMessage.getHeaders().putAll(headers);
			outputMessage.getBody(); // flush headers
		}
	}

}
