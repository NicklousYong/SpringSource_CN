/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2012 the original author or authors.
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

package org.springframework.web.filter;

import java.io.IOException;
import java.util.Locale;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * {@link javax.servlet.Filter} that converts posted method parameters into HTTP methods,
 * retrievable via {@link HttpServletRequest#getMethod()}. Since browsers currently only
 * support GET and POST, a common technique - used by the Prototype library, for instance -
 * is to use a normal POST with an additional hidden form field ({@code _method})
 * to pass the "real" HTTP method along. This filter reads that parameter and changes
 * the {@link HttpServletRequestWrapper#getMethod()} return value accordingly.
 *
 * <p>The name of the request parameter defaults to {@code _method}, but can be
 * adapted via the {@link #setMethodParam(String) methodParam} property.
 *
 * <p><b>NOTE: This filter needs to run after multipart processing in case of a multipart
 * POST request, due to its inherent need for checking a POST body parameter.</b>
 * So typically, put a Spring {@link org.springframework.web.multipart.support.MultipartFilter}
 * <i>before</i> this HiddenHttpMethodFilter in your {@code web.xml} filter chain.
 *
 * <p>
 * {@link javaxservletFilter}将发布的方法参数转换为HTTP方法,可通过{@link HttpServletRequest#getMethod())进行检索。
 * 由于浏览器目前仅支持GET和POST,因此Prototype库使用的常用技术是使用一个正常的POST与一个额外的隐藏表单域({@code _method})传递"真正的"HTTP方法沿着这个过滤器读取该
 * 参数并相应地更改{@link HttpServletRequestWrapper#getMethod()}返回值。
 * {@link javaxservletFilter}将发布的方法参数转换为HTTP方法,可通过{@link HttpServletRequest#getMethod())进行检索。
 * 
 *  <p>请求参数的名称默认为{@code _method},但可以通过{@link #setMethodParam(String)methodParam}属性进行修改
 * 
 * <p> <b>注意：由于其固有的检查POST主体参数</b>的需要,此过滤器需要在多部分处理之后运行,因此通常会将Spring {@link orgspringframeworkwebmultipartsupportMultipartFilter}
 * 
 * @author Arjen Poutsma
 * @since 3.0
 */
public class HiddenHttpMethodFilter extends OncePerRequestFilter {

	/** Default method parameter: {@code _method} */
	public static final String DEFAULT_METHOD_PARAM = "_method";

	private String methodParam = DEFAULT_METHOD_PARAM;


	/**
	 * Set the parameter name to look for HTTP methods.
	 * <p>
	 *  <i>在您的{@code webxml}过滤器链中</i>此HiddenHttpMethodFilter之前。
	 * 
	 * 
	 * @see #DEFAULT_METHOD_PARAM
	 */
	public void setMethodParam(String methodParam) {
		Assert.hasText(methodParam, "'methodParam' must not be empty");
		this.methodParam = methodParam;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String paramValue = request.getParameter(this.methodParam);
		if ("POST".equals(request.getMethod()) && StringUtils.hasLength(paramValue)) {
			String method = paramValue.toUpperCase(Locale.ENGLISH);
			HttpServletRequest wrapper = new HttpMethodRequestWrapper(request, method);
			filterChain.doFilter(wrapper, response);
		}
		else {
			filterChain.doFilter(request, response);
		}
	}


	/**
	 * Simple {@link HttpServletRequest} wrapper that returns the supplied method for
	 * {@link HttpServletRequest#getMethod()}.
	 * <p>
	 *  设置参数名称以查找HTTP方法
	 * 
	 */
	private static class HttpMethodRequestWrapper extends HttpServletRequestWrapper {

		private final String method;

		public HttpMethodRequestWrapper(HttpServletRequest request, String method) {
			super(request);
			this.method = method;
		}

		@Override
		public String getMethod() {
			return this.method;
		}
	}

}
