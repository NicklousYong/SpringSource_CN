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

import com.fasterxml.jackson.annotation.JsonView;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;

/**
 * A {@link ResponseBodyAdvice} implementation that adds support for Jackson's
 * {@code @JsonView} annotation declared on a Spring MVC {@code @RequestMapping}
 * or {@code @ExceptionHandler} method.
 *
 * <p>The serialization view specified in the annotation will be passed in to the
 * {@link org.springframework.http.converter.json.MappingJackson2HttpMessageConverter}
 * which will then use it to serialize the response body.
 *
 * <p>Note that despite {@code @JsonView} allowing for more than one class to
 * be specified, the use for a response body advice is only supported with
 * exactly one class argument. Consider the use of a composite interface.
 *
 * <p>
 *  一个{@link ResponseBodyAdvice}实现,增加了对Spring MVC {@code @RequestMapping}或{@code @ExceptionHandler}方法中声明
 * 的Jackson的{@code @JsonView}注释的支持。
 * 
 * <p>注释中指定的序列化视图将被传递到{@link orgspringframeworkhttpconverterjsonMappingJackson2HttpMessageConverter},然后使
 * 
 * @author Rossen Stoyanchev
 * @since 4.1
 * @see com.fasterxml.jackson.annotation.JsonView
 * @see com.fasterxml.jackson.databind.ObjectMapper#writerWithView(Class)
 */
public class JsonViewResponseBodyAdvice extends AbstractMappingJacksonResponseBodyAdvice {

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return super.supports(returnType, converterType) && returnType.hasMethodAnnotation(JsonView.class);
	}

	@Override
	protected void beforeBodyWriteInternal(MappingJacksonValue bodyContainer, MediaType contentType,
			MethodParameter returnType, ServerHttpRequest request, ServerHttpResponse response) {

		JsonView annotation = returnType.getMethodAnnotation(JsonView.class);
		Class<?>[] classes = annotation.value();
		if (classes.length != 1) {
			throw new IllegalArgumentException(
					"@JsonView only supported for response body advice with exactly 1 class argument: " + returnType);
		}
		bodyContainer.setSerializationView(classes[0]);
	}

}
