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

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.UsesJava8;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.multipart.support.MultipartResolutionDelegate;
import org.springframework.web.multipart.support.RequestPartServletServerHttpRequest;

/**
 * Resolves the following method arguments:
 * <ul>
 * <li>Annotated with {@code @RequestPart}
 * <li>Of type {@link MultipartFile} in conjunction with Spring's {@link MultipartResolver} abstraction
 * <li>Of type {@code javax.servlet.http.Part} in conjunction with Servlet 3.0 multipart requests
 * </ul>
 *
 * <p>When a parameter is annotated with {@code @RequestPart}, the content of the part is
 * passed through an {@link HttpMessageConverter} to resolve the method argument with the
 * 'Content-Type' of the request part in mind. This is analogous to what @{@link RequestBody}
 * does to resolve an argument based on the content of a regular request.
 *
 * <p>When a parameter is not annotated or the name of the part is not specified,
 * it is derived from the name of the method argument.
 *
 * <p>Automatic validation may be applied if the argument is annotated with
 * {@code @javax.validation.Valid}. In case of validation failure, a {@link MethodArgumentNotValidException}
 * is raised and a 400 response status code returned if
 * {@link org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver} is configured.
 *
 * <p>
 *  解决以下方法参数：
 * <ul>
 * 结合使用Spring的{@link MultipartResolver}抽象<li>类型为{@code @RequestPart} <li>类型为{@link MultipartFile}的类型{@code javaxservlethttpPart}
 * 结合Servlet 30多部分请求。
 * </ul>
 * 
 *  <p>当使用{@code @RequestPart}注释参数时,该部分的内容将通过{@link HttpMessageConverter}传递,以解决与请求部分的"Content-Type"相关的方法
 * 参数。
 * 类似于@ {@ link RequestBody}根据常规请求的内容解析参数。
 * 
 *  <p>当一个参数未被注释或未指定零件的名称时,它将从方法参数的名称派生
 * 
 * 如果使用{@code @javaxvalidationValid}注释参数,则可以应用自动验证。
 * 如果验证失败,则会引发{@link MethodArgumentNotValidException},并且如果配置了{@link orgspringframeworkwebservletmvcsupportDefaultHandlerExceptionResolver}
 * ,则会返回400个响应状态代码。
 * 如果使用{@code @javaxvalidationValid}注释参数,则可以应用自动验证。
 * 
 * @author Rossen Stoyanchev
 * @author Brian Clozel
 * @author Juergen Hoeller
 * @since 3.1
 */
public class RequestPartMethodArgumentResolver extends AbstractMessageConverterMethodArgumentResolver {

	/**
	 * Basic constructor with converters only.
	 * <p>
	 * 
	 */
	public RequestPartMethodArgumentResolver(List<HttpMessageConverter<?>> messageConverters) {
		super(messageConverters);
	}

	/**
	 * Constructor with converters and {@code Request~} and
	 * {@code ResponseBodyAdvice}.
	 * <p>
	 *  只有转换器的基本构造函数
	 * 
	 */
	public RequestPartMethodArgumentResolver(List<HttpMessageConverter<?>> messageConverters,
			List<Object> requestResponseBodyAdvice) {

		super(messageConverters, requestResponseBodyAdvice);
	}


	/**
	 * Supports the following:
	 * <ul>
	 * <li>annotated with {@code @RequestPart}
	 * <li>of type {@link MultipartFile} unless annotated with {@code @RequestParam}
	 * <li>of type {@code javax.servlet.http.Part} unless annotated with {@code @RequestParam}
	 * </ul>
	 * <p>
	 *  具有转换器和{@code Request〜}和{@code ResponseBodyAdvice}的构造方法
	 * 
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		if (parameter.hasParameterAnnotation(RequestPart.class)) {
			return true;
		}
		else {
			if (parameter.hasParameterAnnotation(RequestParam.class)) {
				return false;
			}
			return MultipartResolutionDelegate.isMultipartArgument(parameter.nestedIfOptional());
		}
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest request, WebDataBinderFactory binderFactory) throws Exception {

		HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);
		RequestPart requestPart = parameter.getParameterAnnotation(RequestPart.class);
		boolean isRequired = ((requestPart == null || requestPart.required()) && !parameter.isOptional());

		String name = getPartName(parameter, requestPart);
		parameter = parameter.nestedIfOptional();
		Object arg = null;

		Object mpArg = MultipartResolutionDelegate.resolveMultipartArgument(name, parameter, servletRequest);
		if (mpArg != MultipartResolutionDelegate.UNRESOLVABLE) {
			arg = mpArg;
		}
		else {
			try {
				HttpInputMessage inputMessage = new RequestPartServletServerHttpRequest(servletRequest, name);
				arg = readWithMessageConverters(inputMessage, parameter, parameter.getNestedGenericParameterType());
				WebDataBinder binder = binderFactory.createBinder(request, arg, name);
				if (arg != null) {
					validateIfApplicable(binder, parameter);
					if (binder.getBindingResult().hasErrors() && isBindExceptionRequired(binder, parameter)) {
						throw new MethodArgumentNotValidException(parameter, binder.getBindingResult());
					}
				}
				mavContainer.addAttribute(BindingResult.MODEL_KEY_PREFIX + name, binder.getBindingResult());
			}
			catch (MissingServletRequestPartException ex) {
				if (isRequired) {
					throw ex;
				}
			}
			catch (MultipartException ex) {
				if (isRequired) {
					throw ex;
				}
			}
		}

		if (arg == null && isRequired) {
			if (!MultipartResolutionDelegate.isMultipartRequest(servletRequest)) {
				throw new MultipartException("Current request is not a multipart request");
			}
			else {
				throw new MissingServletRequestPartException(name);
			}
		}
		if (parameter.isOptional()) {
			arg = OptionalResolver.resolveValue(arg);
		}

		return arg;
	}

	private String getPartName(MethodParameter methodParam, RequestPart requestPart) {
		String partName = (requestPart != null ? requestPart.name() : "");
		if (partName.length() == 0) {
			partName = methodParam.getParameterName();
			if (partName == null) {
				throw new IllegalArgumentException("Request part name for argument type [" +
						methodParam.getNestedParameterType().getName() +
						"] not specified, and parameter name information not found in class file either.");
			}
		}
		return partName;
	}


	/**
	 * Inner class to avoid hard-coded dependency on Java 8 Optional type...
	 * <p>
	 *  支持以下内容：
	 * <ul>
	 *  除了使用{@code javaxservlethttpPart}类型的{@code @RequestParam} <li>注释,除非使用{@code @RequestParam}注释,否则<li>使用
	 * {@code @RequestPart} <li>注释类型为{@link MultipartFile}。
	 */
	@UsesJava8
	private static class OptionalResolver {

		public static Object resolveValue(Object value) {
			if (value == null || (value instanceof Collection && ((Collection) value).isEmpty()) ||
					(value instanceof Object[] && ((Object[]) value).length == 0)) {
				return Optional.empty();
			}
			return Optional.of(value);
		}
	}

}
