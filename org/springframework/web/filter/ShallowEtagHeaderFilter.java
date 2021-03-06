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

package org.springframework.web.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

/**
 * {@link javax.servlet.Filter} that generates an {@code ETag} value based on the
 * content on the response. This ETag is compared to the {@code If-None-Match}
 * header of the request. If these headers are equal, the response content is
 * not sent, but rather a {@code 304 "Not Modified"} status instead.
 *
 * <p>Since the ETag is based on the response content, the response
 * (e.g. a {@link org.springframework.web.servlet.View}) is still rendered.
 * As such, this filter only saves bandwidth, not server performance.
 *
 * <p>
 * {@link javaxservletFilter}根据响应中的内容生成一个{@code ETag}值该ETag与请求的{@code If-None-Match}标头进行比较如果这些头相同,则响应内容为
 * 不是发送,而是{@code 304"未修改"}状态。
 * 
 *  由于ETag基于响应内容,响应(例如{@link orgspringframeworkwebservletView})仍然呈现。因此,此过滤器仅节省带宽,而不是服务器性能
 * 
 * 
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @since 3.0
 */
public class ShallowEtagHeaderFilter extends OncePerRequestFilter {

	private static final String HEADER_ETAG = "ETag";

	private static final String HEADER_IF_NONE_MATCH = "If-None-Match";

	private static final String HEADER_CACHE_CONTROL = "Cache-Control";

	private static final String DIRECTIVE_NO_STORE = "no-store";

	private static final String STREAMING_ATTRIBUTE = ShallowEtagHeaderFilter.class.getName() + ".STREAMING";

	/** Checking for Servlet 3.0+ HttpServletResponse.getHeader(String) */
	private static final boolean servlet3Present =
			ClassUtils.hasMethod(HttpServletResponse.class, "getHeader", String.class);

	private boolean writeWeakETag = false;

	/**
	 * Set whether the ETag value written to the response should be weak, as per rfc7232.
	 * <p>Should be configured using an {@code <init-param>} for parameter name
	 * "writeWeakETag" in the filter definition in {@code web.xml}.
	 * <p>
	 *  设置写入响应的ETag值是否应该弱,根据rfc7232 <p>应在{@code webxml}中的过滤器定义中使用{@code <init-param>}配置参数名称"writeWeakETag"
	 * 
	 * 
	 * @see  <a href="https://tools.ietf.org/html/rfc7232#section-2.3">rfc7232 section-2.3</a>
	 */
	public boolean isWriteWeakETag() {
		return writeWeakETag;
	}

	/**
	 * Return whether the ETag value written to the response should be weak, as per rfc7232.
	 * <p>
	 * 返回写入响应的ETag值是否应该弱,按照rfc7232
	 * 
	 */
	public void setWriteWeakETag(boolean writeWeakETag) {
		this.writeWeakETag = writeWeakETag;
	}

	/**
	 * The default value is "false" so that the filter may delay the generation of
	 * an ETag until the last asynchronously dispatched thread.
	 * <p>
	 *  默认值为"false",以便过滤器可能延迟生成ETag直到最后一个异步调度的线程
	 * 
	 */
	@Override
	protected boolean shouldNotFilterAsyncDispatch() {
		return false;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		HttpServletResponse responseToUse = response;
		if (!isAsyncDispatch(request) && !(response instanceof ContentCachingResponseWrapper)) {
			responseToUse = new HttpStreamingAwareContentCachingResponseWrapper(response, request);
		}

		filterChain.doFilter(request, responseToUse);

		if (!isAsyncStarted(request) && !isContentCachingDisabled(request)) {
			updateResponse(request, responseToUse);
		}
	}

	private void updateResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ContentCachingResponseWrapper responseWrapper =
				WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
		Assert.notNull(responseWrapper, "ContentCachingResponseWrapper not found");
		HttpServletResponse rawResponse = (HttpServletResponse) responseWrapper.getResponse();
		int statusCode = responseWrapper.getStatusCode();

		if (rawResponse.isCommitted()) {
			responseWrapper.copyBodyToResponse();
		}
		else if (isEligibleForEtag(request, responseWrapper, statusCode, responseWrapper.getContentInputStream())) {
			String responseETag = generateETagHeaderValue(responseWrapper.getContentInputStream(), this.writeWeakETag);
			rawResponse.setHeader(HEADER_ETAG, responseETag);
			String requestETag = request.getHeader(HEADER_IF_NONE_MATCH);
			if (requestETag != null
					&& (responseETag.equals(requestETag)
					|| responseETag.replaceFirst("^W/", "").equals(requestETag.replaceFirst("^W/", ""))
					|| "*".equals(requestETag))) {
				if (logger.isTraceEnabled()) {
					logger.trace("ETag [" + responseETag + "] equal to If-None-Match, sending 304");
				}
				rawResponse.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			}
			else {
				if (logger.isTraceEnabled()) {
					logger.trace("ETag [" + responseETag + "] not equal to If-None-Match [" + requestETag +
							"], sending normal response");
				}
				responseWrapper.copyBodyToResponse();
			}
		}
		else {
			if (logger.isTraceEnabled()) {
				logger.trace("Response with status code [" + statusCode + "] not eligible for ETag");
			}
			responseWrapper.copyBodyToResponse();
		}
	}

	/**
	 * Indicates whether the given request and response are eligible for ETag generation.
	 * <p>The default implementation returns {@code true} if all conditions match:
	 * <ul>
	 * <li>response status codes in the {@code 2xx} series</li>
	 * <li>request method is a GET</li>
	 * <li>response Cache-Control header is not set or does not contain a "no-store" directive</li>
	 * </ul>
	 * <p>
	 *  指示给定的请求和响应是否符合ETag生成的条件<p>如果所有条件都匹配,默认实现将返回{@code true}
	 * <ul>
	 *  <li>请求方法中的{@code 2xx}系列</li> <li>响应状态代码是GET </li> <li>响应Cache-Control头没有设置或不包含"no-store "指令</li>
	 * </ul>
	 * 
	 * @param request the HTTP request
	 * @param response the HTTP response
	 * @param responseStatusCode the HTTP response status code
	 * @param inputStream the response body
	 * @return {@code true} if eligible for ETag generation; {@code false} otherwise
	 */
	protected boolean isEligibleForEtag(HttpServletRequest request, HttpServletResponse response,
			int responseStatusCode, InputStream inputStream) {

		String method = request.getMethod();
		if (responseStatusCode >= 200 && responseStatusCode < 300 &&
				(HttpMethod.GET.matches(method) || HttpMethod.HEAD.matches(method))) {

			String cacheControl = null;
			if (servlet3Present) {
				cacheControl = response.getHeader(HEADER_CACHE_CONTROL);
			}
			if (cacheControl == null || !cacheControl.contains(DIRECTIVE_NO_STORE)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Generate the ETag header value from the given response body byte array.
	 * <p>The default implementation generates an MD5 hash.
	 * <p>
	 *  从给定的响应体字节数组生成ETag头值<p>默认实现生成一个MD5散列
	 * 
	 * 
	 * @param inputStream the response body as an InputStream
	 * @param isWeak whether the generated ETag should be weak
	 * @return the ETag header value
	 * @see org.springframework.util.DigestUtils
	 */
	protected String generateETagHeaderValue(InputStream inputStream, boolean isWeak) throws IOException {
		// length of W/ + 0 + " + 32bits md5 hash + "
		StringBuilder builder = new StringBuilder(37);
		if (isWeak) {
			builder.append("W/");
		}
		builder.append("\"0");
		DigestUtils.appendMd5DigestAsHex(inputStream, builder);
		builder.append('"');
		return builder.toString();
	}


	/**
	 * This method can be used to disable the content caching response wrapper
	 * of the ShallowEtagHeaderFilter. This can be done before the start of HTTP
	 * streaming for example where the response will be written to asynchronously
	 * and not in the context of a Servlet container thread.
	 * <p>
	 * 该方法可用于禁用ShallowEtagHeaderFilter的内容缓存响应包装器。这可以在HTTP流的开始之前完成,例如,响应将被写入异步而不是在Servlet容器线程的上下文中
	 * 
	 * @since 4.2
	 */
	public static void disableContentCaching(ServletRequest request) {
		Assert.notNull(request, "ServletRequest must not be null");
		request.setAttribute(STREAMING_ATTRIBUTE, true);
	}

	private static boolean isContentCachingDisabled(HttpServletRequest request) {
		return (request.getAttribute(STREAMING_ATTRIBUTE) != null);
	}


	private static class HttpStreamingAwareContentCachingResponseWrapper extends ContentCachingResponseWrapper {

		private final HttpServletRequest request;

		public HttpStreamingAwareContentCachingResponseWrapper(HttpServletResponse response, HttpServletRequest request) {
			super(response);
			this.request = request;
		}

		@Override
		public ServletOutputStream getOutputStream() throws IOException {
			return (useRawResponse() ? getResponse().getOutputStream() : super.getOutputStream());
		}

		@Override
		public PrintWriter getWriter() throws IOException {
			return (useRawResponse() ? getResponse().getWriter() : super.getWriter());
		}

		private boolean useRawResponse() {
			return isContentCachingDisabled(this.request);
		}
	}

}
