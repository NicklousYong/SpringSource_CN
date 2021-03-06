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

package org.springframework.web.servlet.support;

import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.UrlPathHelper;

/**
 * A UriComponentsBuilder that extracts information from the HttpServletRequest.
 *
 * <p>
 *  一个从HttpServletRequest中提取信息的UriComponentsBuilder
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class ServletUriComponentsBuilder extends UriComponentsBuilder {

	private String originalPath;


	/**
	 * Default constructor. Protected to prevent direct instantiation.
	 * <p>
	 *  默认构造函数保护以防止直接实例化
	 * 
	 * 
	 * @see #fromContextPath(HttpServletRequest)
	 * @see #fromServletMapping(HttpServletRequest)
	 * @see #fromRequest(HttpServletRequest)
	 * @see #fromCurrentContextPath()
	 * @see #fromCurrentServletMapping()
 	 * @see #fromCurrentRequest()
	 */
	protected ServletUriComponentsBuilder() {
	}

	/**
	 * Create a deep copy of the given ServletUriComponentsBuilder.
	 * <p>
	 *  创建给定ServletUriComponentsBuilder的深层副本
	 * 
	 * 
	 * @param other the other builder to copy from
	 */
	protected ServletUriComponentsBuilder(ServletUriComponentsBuilder other) {
		super(other);
		this.originalPath = other.originalPath;
	}


	// Factory methods based on a HttpServletRequest

	/**
	 * Prepare a builder from the host, port, scheme, and context path of the
	 * given HttpServletRequest.
	 * <p>
	 * 从给定的HttpServletRequest的主机,端口,方案和上下文路径准备构建器
	 * 
	 */
	public static ServletUriComponentsBuilder fromContextPath(HttpServletRequest request) {
		ServletUriComponentsBuilder builder = initFromRequest(request);
		builder.replacePath(prependForwardedPrefix(request, request.getContextPath()));
		return builder;
	}

	/**
	 * Prepare a builder from the host, port, scheme, context path, and
	 * servlet mapping of the given HttpServletRequest.
	 * <p>If the servlet is mapped by name, e.g. {@code "/main/*"}, the path
	 * will end with "/main". If the servlet is mapped otherwise, e.g.
	 * {@code "/"} or {@code "*.do"}, the result will be the same as
	 * if calling {@link #fromContextPath(HttpServletRequest)}.
	 * <p>
	 *  从给定的HttpServletRequest <p>的主机,端口,方案,上下文路径和servlet映射准备构建器如果servlet按名称映射,例如{@code"/ main / *"},路径将以" /
	 *  main"如果servlet被其他方式映射,例如{@code"/"}或{@code"* do"},结果将与调用{@link #fromContextPath(HttpServletRequest)}一
	 * 样)。
	 * 
	 */
	public static ServletUriComponentsBuilder fromServletMapping(HttpServletRequest request) {
		ServletUriComponentsBuilder builder = fromContextPath(request);
		if (StringUtils.hasText(new UrlPathHelper().getPathWithinServletMapping(request))) {
			builder.path(request.getServletPath());
		}
		return builder;
	}

	/**
	 * Prepare a builder from the host, port, scheme, and path (but not the query)
	 * of the HttpServletRequest.
	 * <p>
	 *  从HttpServletRequest的主机,端口,方案和路径(但不是查询)准备构建器
	 * 
	 */
	public static ServletUriComponentsBuilder fromRequestUri(HttpServletRequest request) {
		ServletUriComponentsBuilder builder = initFromRequest(request);
		builder.initPath(prependForwardedPrefix(request, request.getRequestURI()));
		return builder;
	}

	/**
	 * Prepare a builder by copying the scheme, host, port, path, and
	 * query string of an HttpServletRequest.
	 * <p>
	 *  通过复制HttpServletRequest的方案,主机,端口,路径和查询字符串来准备构建器
	 * 
	 */
	public static ServletUriComponentsBuilder fromRequest(HttpServletRequest request) {
		ServletUriComponentsBuilder builder = initFromRequest(request);
		builder.initPath(prependForwardedPrefix(request, request.getRequestURI()));
		builder.query(request.getQueryString());
		return builder;
	}

	/**
	 * Initialize a builder with a scheme, host,and port (but not path and query).
	 * <p>
	 *  使用方案,主机和端口(但不是路径和查询)初始化构建器
	 * 
	 */
	private static ServletUriComponentsBuilder initFromRequest(HttpServletRequest request) {
		HttpRequest httpRequest = new ServletServerHttpRequest(request);
		UriComponents uriComponents = UriComponentsBuilder.fromHttpRequest(httpRequest).build();
		String scheme = uriComponents.getScheme();
		String host = uriComponents.getHost();
		int port = uriComponents.getPort();

		ServletUriComponentsBuilder builder = new ServletUriComponentsBuilder();
		builder.scheme(scheme);
		builder.host(host);
		if (("http".equals(scheme) && port != 80) || ("https".equals(scheme) && port != 443)) {
			builder.port(port);
		}
		return builder;
	}

	private static String prependForwardedPrefix(HttpServletRequest request, String path) {
		String prefix = null;
		Enumeration<String> names = request.getHeaderNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			if ("X-Forwarded-Prefix".equalsIgnoreCase(name)) {
				prefix = request.getHeader(name);
			}
		}
		if (prefix != null) {
			path = prefix + path;
		}
		return path;
	}


	// Alternative methods relying on RequestContextHolder to find the request

	/**
	 * Same as {@link #fromContextPath(HttpServletRequest)} except the
	 * request is obtained through {@link RequestContextHolder}.
	 * <p>
	 * 与{@link #fromContextPath(HttpServletRequest)}相同),除了请求是通过{@link RequestContextHolder}获得的
	 * 
	 */
	public static ServletUriComponentsBuilder fromCurrentContextPath() {
		return fromContextPath(getCurrentRequest());
	}

	/**
	 * Same as {@link #fromServletMapping(HttpServletRequest)} except the
	 * request is obtained through {@link RequestContextHolder}.
	 * <p>
	 *  与{@link #fromServletMapping(HttpServletRequest)})相同,除了请求是通过{@link RequestContextHolder}获得的
	 * 
	 */
	public static ServletUriComponentsBuilder fromCurrentServletMapping() {
		return fromServletMapping(getCurrentRequest());
	}

	/**
	 * Same as {@link #fromRequestUri(HttpServletRequest)} except the
	 * request is obtained through {@link RequestContextHolder}.
	 * <p>
	 *  与{@link #fromRequestUri(HttpServletRequest)}相同),除了请求是通过{@link RequestContextHolder}获得的
	 * 
	 */
	public static ServletUriComponentsBuilder fromCurrentRequestUri() {
		return fromRequestUri(getCurrentRequest());
	}

	/**
	 * Same as {@link #fromRequest(HttpServletRequest)} except the
	 * request is obtained through {@link RequestContextHolder}.
	 * <p>
	 *  与{@link #fromRequest(HttpServletRequest)}相同),除了请求是通过{@link RequestContextHolder}获得的
	 * 
	 */
	public static ServletUriComponentsBuilder fromCurrentRequest() {
		return fromRequest(getCurrentRequest());
	}

	/**
	 * Obtain current request through {@link RequestContextHolder}.
	 * <p>
	 *  通过{@link RequestContextHolder}获取当前请求
	 * 
	 */
	protected static HttpServletRequest getCurrentRequest() {
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		Assert.state(requestAttributes != null, "Could not find current request via RequestContextHolder");
		Assert.isInstanceOf(ServletRequestAttributes.class, requestAttributes);
		HttpServletRequest servletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
		Assert.state(servletRequest != null, "Could not find current HttpServletRequest");
		return servletRequest;
	}


	private void initPath(String path) {
		this.originalPath = path;
		replacePath(path);
	}

	/**
	 * Remove any path extension from the {@link HttpServletRequest#getRequestURI()
	 * requestURI}. This method must be invoked before any calls to {@link #path(String)}
	 * or {@link #pathSegment(String...)}.
	 * <pre>
	 * GET http://foo.com/rest/books/6.json
	 *
	 * ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromRequestUri(this.request);
	 * String ext = builder.removePathExtension();
	 * String uri = builder.path("/pages/1.{ext}").buildAndExpand(ext).toUriString();
	 * assertEquals("http://foo.com/rest/books/6/pages/1.json", result);
	 * </pre>
	 * <p>
	 *  从{@link HttpServletRequest#getRequestURI()requestURI}中删除任何路径扩展名在{@link #path(String)}或{@link #pathSegment(String)}
	 * 的任何调用之前,必须调用此方法。
	 * <pre>
	 *  GET http：// foocom / rest / books / 6json
	 * 
	 * 
	 * @return the removed path extension for possible re-use, or {@code null}
	 * @since 4.0
	 */
	public String removePathExtension() {
		String extension = null;
		if (this.originalPath != null) {
			extension = UriUtils.extractFileExtension(this.originalPath);
			if (!StringUtils.isEmpty(extension)) {
				int end = this.originalPath.length() - (extension.length() + 1);
				replacePath(this.originalPath.substring(0, end));
			}
			this.originalPath = null;
		}
		return extension;
	}

	@Override
	public ServletUriComponentsBuilder cloneBuilder() {
		return new ServletUriComponentsBuilder(this);
	}

}
