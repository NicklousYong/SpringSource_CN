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

import java.io.InputStream;
import java.io.Reader;
import java.security.Principal;
import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.lang.UsesJava8;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * Resolves request-related method argument values of the following types:
 * <ul>
 * <li>{@link WebRequest}
 * <li>{@link ServletRequest}
 * <li>{@link MultipartRequest}
 * <li>{@link HttpSession}
 * <li>{@link Principal}
 * <li>{@link Locale}
 * <li>{@link TimeZone} (as of Spring 4.0)
 * <li>{@link java.time.ZoneId} (as of Spring 4.0 and Java 8)</li>
 * <li>{@link InputStream}
 * <li>{@link Reader}
 * <li>{@link org.springframework.http.HttpMethod} (as of Spring 4.0)</li>
 * </ul>
 *
 * <p>
 *  解决与以下类型的请求相关的方法参数值：
 * <ul>
 * <li> {@ link WebRequest} <li> {@ link ServletRequest} <li> {@ link MultipartRequest} <li> {@ link HttpSession}
 *  <li> {@ link Principal} <li> {@ link Locale} <li > {@ link TimeZone}(截至春季40)<li> {@ link javatimeZoneId}
 * (截至Spring 40和Java 8)</li> <li> {@ link InputStream} <li> {@ link Reader} < li> {@ link orgspringframeworkhttpHttpMethod}
 * (截至春季40)</li>。
 * 
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @since 3.1
 */
public class ServletRequestMethodArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		Class<?> paramType = parameter.getParameterType();
		return (WebRequest.class.isAssignableFrom(paramType) ||
				ServletRequest.class.isAssignableFrom(paramType) ||
				MultipartRequest.class.isAssignableFrom(paramType) ||
				HttpSession.class.isAssignableFrom(paramType) ||
				Principal.class.isAssignableFrom(paramType) ||
				Locale.class == paramType ||
				TimeZone.class == paramType ||
				"java.time.ZoneId".equals(paramType.getName()) ||
				InputStream.class.isAssignableFrom(paramType) ||
				Reader.class.isAssignableFrom(paramType) ||
				HttpMethod.class == paramType);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

		Class<?> paramType = parameter.getParameterType();
		if (WebRequest.class.isAssignableFrom(paramType)) {
			return webRequest;
		}

		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
		if (ServletRequest.class.isAssignableFrom(paramType) || MultipartRequest.class.isAssignableFrom(paramType)) {
			Object nativeRequest = webRequest.getNativeRequest(paramType);
			if (nativeRequest == null) {
				throw new IllegalStateException(
						"Current request is not of type [" + paramType.getName() + "]: " + request);
			}
			return nativeRequest;
		}
		else if (HttpSession.class.isAssignableFrom(paramType)) {
			return request.getSession();
		}
		else if (HttpMethod.class == paramType) {
			return ((ServletWebRequest) webRequest).getHttpMethod();
		}
		else if (Principal.class.isAssignableFrom(paramType)) {
			return request.getUserPrincipal();
		}
		else if (Locale.class == paramType) {
			return RequestContextUtils.getLocale(request);
		}
		else if (TimeZone.class == paramType) {
			TimeZone timeZone = RequestContextUtils.getTimeZone(request);
			return (timeZone != null ? timeZone : TimeZone.getDefault());
		}
		else if ("java.time.ZoneId".equals(paramType.getName())) {
			return ZoneIdResolver.resolveZoneId(request);
		}
		else if (InputStream.class.isAssignableFrom(paramType)) {
			return request.getInputStream();
		}
		else if (Reader.class.isAssignableFrom(paramType)) {
			return request.getReader();
		}
		else {
			// should never happen...
			throw new UnsupportedOperationException(
					"Unknown parameter type: " + paramType + " in method: " + parameter.getMethod());
		}
	}


	/**
	 * Inner class to avoid a hard-coded dependency on Java 8's {@link java.time.ZoneId}.
	 * <p>
	 * </ul>
	 * 
	 */
	@UsesJava8
	private static class ZoneIdResolver {

		public static Object resolveZoneId(HttpServletRequest request) {
			TimeZone timeZone = RequestContextUtils.getTimeZone(request);
			return (timeZone != null ? timeZone.toZoneId() : ZoneId.systemDefault());
		}
	}

}
