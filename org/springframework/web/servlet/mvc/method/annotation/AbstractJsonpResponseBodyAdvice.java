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

import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * A convenient base class for a {@code ResponseBodyAdvice} to instruct the
 * {@link org.springframework.http.converter.json.MappingJackson2HttpMessageConverter}
 * to serialize with JSONP formatting.
 *
 * <p>Sub-classes must specify the query parameter name(s) to check for the name
 * of the JSONP callback function.
 *
 * <p>Sub-classes are likely to be annotated with the {@code @ControllerAdvice}
 * annotation and auto-detected or otherwise must be registered directly with the
 * {@code RequestMappingHandlerAdapter} and {@code ExceptionHandlerExceptionResolver}.
 *
 * <p>
 *  {@code ResponseBodyAdvice}的一个方便的基类,用于指示{@link orgspringframeworkhttpconverterjsonMappingJackson2HttpMessageConverter}
 * 使用JSONP格式化序列化。
 * 
 * <p>子类必须指定查询参数名称以检查JSONP回调函数的名称
 * 
 *  <p>子类可能会使用{@code @ControllerAdvice}注释进行注释,并自动检测,否则必须直接使用{@code RequestMappingHandlerAdapter}和{@code ExceptionHandlerExceptionResolver}
 * 注册。
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 4.1
 */
public abstract class AbstractJsonpResponseBodyAdvice extends AbstractMappingJacksonResponseBodyAdvice {

	/**
	 * Pattern for validating jsonp callback parameter values.
	 * <p>
	 *  用于验证jsonp回调参数值的模式
	 * 
	 */
	private static final Pattern CALLBACK_PARAM_PATTERN = Pattern.compile("[0-9A-Za-z_\\.]*");


	private final Log logger = LogFactory.getLog(getClass());

	private final String[] jsonpQueryParamNames;


	protected AbstractJsonpResponseBodyAdvice(String... queryParamNames) {
		Assert.isTrue(!ObjectUtils.isEmpty(queryParamNames), "At least one query param name is required");
		this.jsonpQueryParamNames = queryParamNames;
	}


	@Override
	protected void beforeBodyWriteInternal(MappingJacksonValue bodyContainer, MediaType contentType,
			MethodParameter returnType, ServerHttpRequest request, ServerHttpResponse response) {

		HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();

		for (String name : this.jsonpQueryParamNames) {
			String value = servletRequest.getParameter(name);
			if (value != null) {
				if (!isValidJsonpQueryParam(value)) {
					if (logger.isDebugEnabled()) {
						logger.debug("Ignoring invalid jsonp parameter value: " + value);
					}
					continue;
				}
				MediaType contentTypeToUse = getContentType(contentType, request, response);
				response.getHeaders().setContentType(contentTypeToUse);
				bodyContainer.setJsonpFunction(value);
				break;
			}
		}
	}

	/**
	 * Validate the jsonp query parameter value. The default implementation
	 * returns true if it consists of digits, letters, or "_" and ".".
	 * Invalid parameter values are ignored.
	 * <p>
	 *  验证jsonp查询参数值如果由数字,字母或"_"和""无效参数值被忽略,默认实现将返回true
	 * 
	 * 
	 * @param value the query param value, never {@code null}
	 * @since 4.1.8
	 */
	protected boolean isValidJsonpQueryParam(String value) {
		return CALLBACK_PARAM_PATTERN.matcher(value).matches();
	}

	/**
	 * Return the content type to set the response to.
	 * This implementation always returns "application/javascript".
	 * <p>
	 *  返回内容类型设置响应此实现总是返回"application / javascript"
	 * 
	 * @param contentType the content type selected through content negotiation
	 * @param request the current request
	 * @param response the current response
	 * @return the content type to set the response to
	 */
	protected MediaType getContentType(MediaType contentType, ServerHttpRequest request, ServerHttpResponse response) {
		return new MediaType("application", "javascript");
	}

}
