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

package org.springframework.web.cors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * A container for CORS configuration that also provides methods to check
 * the actual or requested origin, HTTP methods, and headers.
 *
 * <p>
 *  用于CORS配置的容器还提供了检查实际或请求的来源,HTTP方法和标头的方法
 * 
 * 
 * @author Sebastien Deleuze
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 4.2
 * @see <a href="http://www.w3.org/TR/cors/">CORS W3C recommendation</a>
 */
public class CorsConfiguration {

	/**
	 * Wildcard representing <em>all</em> origins, methods, or headers.
	 * <p>
	 *  表示<em>全部</em>起源,方法或标题的通配符
	 * 
	 */
	public static final String ALL = "*";

	private static final List<HttpMethod> DEFAULT_METHODS;

	static {
		List<HttpMethod> rawMethods = new ArrayList<HttpMethod>(2);
		rawMethods.add(HttpMethod.GET);
		rawMethods.add(HttpMethod.HEAD);
		DEFAULT_METHODS = Collections.unmodifiableList(rawMethods);
	}


	private List<String> allowedOrigins;

	private List<String> allowedMethods;

	private List<HttpMethod> resolvedMethods = DEFAULT_METHODS;

	private List<String> allowedHeaders;

	private List<String> exposedHeaders;

	private Boolean allowCredentials;

	private Long maxAge;


	/**
	 * Construct a new, empty {@code CorsConfiguration} instance.
	 * <p>
	 * 构造一个新的,空的{@code CorsConfiguration}实例
	 * 
	 */
	public CorsConfiguration() {
	}

	/**
	 * Construct a new {@code CorsConfiguration} instance by copying all
	 * values from the supplied {@code CorsConfiguration}.
	 * <p>
	 *  通过复制所提供的{@code CorsConfiguration}中的所有值构造新的{@code CorsConfiguration}实例
	 * 
	 */
	public CorsConfiguration(CorsConfiguration other) {
		this.allowedOrigins = other.allowedOrigins;
		this.allowedMethods = other.allowedMethods;
		this.resolvedMethods = other.resolvedMethods;
		this.allowedHeaders = other.allowedHeaders;
		this.exposedHeaders = other.exposedHeaders;
		this.allowCredentials = other.allowCredentials;
		this.maxAge = other.maxAge;
	}


	/**
	 * Combine the supplied {@code CorsConfiguration} with this one.
	 * <p>Properties of this configuration are overridden by any non-null
	 * properties of the supplied one.
	 * <p>
	 *  将提供的{@code CorsConfiguration}与此一起组合<p>此配置的属性将被提供的一个的非空属性覆盖
	 * 
	 * 
	 * @return the combined {@code CorsConfiguration} or {@code this}
	 * configuration if the supplied configuration is {@code null}
	 */
	public CorsConfiguration combine(CorsConfiguration other) {
		if (other == null) {
			return this;
		}
		CorsConfiguration config = new CorsConfiguration(this);
		config.setAllowedOrigins(combine(getAllowedOrigins(), other.getAllowedOrigins()));
		config.setAllowedMethods(combine(getAllowedMethods(), other.getAllowedMethods()));
		config.setAllowedHeaders(combine(getAllowedHeaders(), other.getAllowedHeaders()));
		config.setExposedHeaders(combine(getExposedHeaders(), other.getExposedHeaders()));
		Boolean allowCredentials = other.getAllowCredentials();
		if (allowCredentials != null) {
			config.setAllowCredentials(allowCredentials);
		}
		Long maxAge = other.getMaxAge();
		if (maxAge != null) {
			config.setMaxAge(maxAge);
		}
		return config;
	}

	private List<String> combine(List<String> source, List<String> other) {
		if (other == null || other.contains(ALL)) {
			return source;
		}
		if (source == null || source.contains(ALL)) {
			return other;
		}
		List<String> combined = new ArrayList<String>(source);
		combined.addAll(other);
		return combined;
	}


	/**
	 * Set the origins to allow, e.g. {@code "http://domain1.com"}.
	 * <p>The special value {@code "*"} allows all domains.
	 * <p>By default this is not set.
	 * <p>
	 *  设置原点允许,例如{@code"http：// domain1com"} <p>特殊值{@code"*"}允许所有域<p>默认情况下未设置
	 * 
	 */
	public void setAllowedOrigins(List<String> allowedOrigins) {
		this.allowedOrigins = (allowedOrigins != null ? new ArrayList<String>(allowedOrigins) : null);
	}

	/**
	 * Return the configured origins to allow, possibly {@code null}.
	 * <p>
	 *  返回配置的原点以允许{@code null}
	 * 
	 * 
	 * @see #addAllowedOrigin(String)
	 * @see #setAllowedOrigins(List)
	 */
	public List<String> getAllowedOrigins() {
		return this.allowedOrigins;
	}

	/**
	 * Add an origin to allow.
	 * <p>
	 *  添加原点以允许
	 * 
	 */
	public void addAllowedOrigin(String origin) {
		if (this.allowedOrigins == null) {
			this.allowedOrigins = new ArrayList<String>(4);
		}
		this.allowedOrigins.add(origin);
	}

	/**
	 * Set the HTTP methods to allow, e.g. {@code "GET"}, {@code "POST"},
	 * {@code "PUT"}, etc.
	 * <p>The special value {@code "*"} allows all methods.
	 * <p>If not set, only {@code "GET"} and {@code "HEAD"} are allowed.
	 * <p>By default this is not set.
	 * <p>
	 * 设置HTTP方法来允许,例如{@code"GET"},{@code"POST"},{@code"PUT"}等等<p>特殊值{@code"*"}允许所有方法< p>如果未设置,则只允许{@code"GET"}
	 * 和{@code"HEAD"} <p>默认情况下,该设置未设置。
	 * 
	 */
	public void setAllowedMethods(List<String> allowedMethods) {
		this.allowedMethods = (allowedMethods != null ? new ArrayList<String>(allowedMethods) : null);
		if (!CollectionUtils.isEmpty(allowedMethods)) {
			this.resolvedMethods = new ArrayList<HttpMethod>(allowedMethods.size());
			for (String method : allowedMethods) {
				if (ALL.equals(method)) {
					this.resolvedMethods = null;
					break;
				}
				this.resolvedMethods.add(HttpMethod.resolve(method));
			}
		}
		else {
			this.resolvedMethods = DEFAULT_METHODS;
		}
	}

	/**
	 * Return the allowed HTTP methods, possibly {@code null} in which case
	 * only {@code "GET"} and {@code "HEAD"} allowed.
	 * <p>
	 *  返回允许的HTTP方法,可能{@code null}在这种情况下{@code"GET"}和{@code"HEAD"}允许
	 * 
	 * 
	 * @see #addAllowedMethod(HttpMethod)
	 * @see #addAllowedMethod(String)
	 * @see #setAllowedMethods(List)
	 */
	public List<String> getAllowedMethods() {
		return this.allowedMethods;
	}

	/**
	 * Add an HTTP method to allow.
	 * <p>
	 *  添加一个HTTP方法来允许
	 * 
	 */
	public void addAllowedMethod(HttpMethod method) {
		if (method != null) {
			addAllowedMethod(method.name());
		}
	}

	/**
	 * Add an HTTP method to allow.
	 * <p>
	 *  添加一个HTTP方法来允许
	 * 
	 */
	public void addAllowedMethod(String method) {
		if (StringUtils.hasText(method)) {
			if (this.allowedMethods == null) {
				this.allowedMethods = new ArrayList<String>(4);
				this.resolvedMethods = new ArrayList<HttpMethod>(4);
			}
			this.allowedMethods.add(method);
			if (ALL.equals(method)) {
				this.resolvedMethods = null;
			}
			else if (this.resolvedMethods != null) {
				this.resolvedMethods.add(HttpMethod.resolve(method));
			}
		}
	}

	/**
	 * Set the list of headers that a pre-flight request can list as allowed
	 * for use during an actual request.
	 * <p>The special value {@code "*"} allows actual requests to send any
	 * header.
	 * <p>A header name is not required to be listed if it is one of:
	 * {@code Cache-Control}, {@code Content-Language}, {@code Expires},
	 * {@code Last-Modified}, or {@code Pragma}.
	 * <p>By default this is not set.
	 * <p>
	 *  设置飞行前请求可以在实际请求期间允许使用的头部列表的列表<p>特殊值{@code"*"}允许实际请求发送任何标题<p>不需要标题名称如果它是以下之一：{@code Cache-Control},{@code Content-Language}
	 * ,{@code Expires},{@code Last-Modified}或{@code Pragma} <p>默认情况下这没有设置。
	 * 
	 */
	public void setAllowedHeaders(List<String> allowedHeaders) {
		this.allowedHeaders = (allowedHeaders != null ? new ArrayList<String>(allowedHeaders) : null);
	}

	/**
	 * Return the allowed actual request headers, possibly {@code null}.
	 * <p>
	 * 返回允许的实际请求标头,可能{@code null}
	 * 
	 * 
	 * @see #addAllowedHeader(String)
	 * @see #setAllowedHeaders(List)
	 */
	public List<String> getAllowedHeaders() {
		return this.allowedHeaders;
	}

	/**
	 * Add an actual request header to allow.
	 * <p>
	 *  添加一个实际的请求头允许
	 * 
	 */
	public void addAllowedHeader(String allowedHeader) {
		if (this.allowedHeaders == null) {
			this.allowedHeaders = new ArrayList<String>(4);
		}
		this.allowedHeaders.add(allowedHeader);
	}

	/**
	 * Set the list of response headers other than simple headers (i.e.
	 * {@code Cache-Control}, {@code Content-Language}, {@code Content-Type},
	 * {@code Expires}, {@code Last-Modified}, or {@code Pragma}) that an
	 * actual response might have and can be exposed.
	 * <p>Note that {@code "*"} is not a valid exposed header value.
	 * <p>By default this is not set.
	 * <p>
	 *  设置除简单标题之外的响应头列表(即{@code Cache-Control},{@code Content-Language},{@code Content-Type},{@code Expires}
	 * ,{@code Last-Modified}或{@code Pragma}),实际的响应可能具有并且可以被暴露<p>请注意,{@code"*"}不是有效的显示标头值<p>默认情况下,这不是设置的。
	 * 
	 */
	public void setExposedHeaders(List<String> exposedHeaders) {
		if (exposedHeaders != null && exposedHeaders.contains(ALL)) {
			throw new IllegalArgumentException("'*' is not a valid exposed header value");
		}
		this.exposedHeaders = (exposedHeaders != null ? new ArrayList<String>(exposedHeaders) : null);
	}

	/**
	 * Return the configured response headers to expose, possibly {@code null}.
	 * <p>
	 *  将配置的响应头返回给公开,可能{@code null}
	 * 
	 * 
	 * @see #addExposedHeader(String)
	 * @see #setExposedHeaders(List)
	 */
	public List<String> getExposedHeaders() {
		return this.exposedHeaders;
	}

	/**
	 * Add a response header to expose.
	 * <p>Note that {@code "*"} is not a valid exposed header value.
	 * <p>
	 *  添加一个响应头来公开<p>请注意,{@code"*"}不是有效的公开头值
	 * 
	 */
	public void addExposedHeader(String exposedHeader) {
		if (ALL.equals(exposedHeader)) {
			throw new IllegalArgumentException("'*' is not a valid exposed header value");
		}
		if (this.exposedHeaders == null) {
			this.exposedHeaders = new ArrayList<String>(4);
		}
		this.exposedHeaders.add(exposedHeader);
	}

	/**
	 * Whether user credentials are supported.
	 * <p>By default this is not set (i.e. user credentials are not supported).
	 * <p>
	 *  是否支持用户凭据<p>默认情况下未设置(即不支持用户凭据)
	 * 
	 */
	public void setAllowCredentials(Boolean allowCredentials) {
		this.allowCredentials = allowCredentials;
	}

	/**
	 * Return the configured {@code allowCredentials} flag, possibly {@code null}.
	 * <p>
	 * 返回配置的{@code allowCredentials}标志,可能{@code null}
	 * 
	 * 
	 * @see #setAllowCredentials(Boolean)
	 */
	public Boolean getAllowCredentials() {
		return this.allowCredentials;
	}

	/**
	 * Configure how long, in seconds, the response from a pre-flight request
	 * can be cached by clients.
	 * <p>By default this is not set.
	 * <p>
	 *  配置可以在几秒钟内客户端缓存飞行前请求的响应时间<p>默认情况下,该设置未设置
	 * 
	 */
	public void setMaxAge(Long maxAge) {
		this.maxAge = maxAge;
	}

	/**
	 * Return the configured {@code maxAge} value, possibly {@code null}.
	 * <p>
	 *  返回配置的{@code maxAge}值,可能{@code null}
	 * 
	 * 
	 * @see #setMaxAge(Long)
	 */
	public Long getMaxAge() {
		return this.maxAge;
	}


	/**
	 * Check the origin of the request against the configured allowed origins.
	 * <p>
	 *  根据配置的允许来源检查请求的来源
	 * 
	 * 
	 * @param requestOrigin the origin to check
	 * @return the origin to use for the response, possibly {@code null} which
	 * means the request origin is not allowed
	 */
	public String checkOrigin(String requestOrigin) {
		if (!StringUtils.hasText(requestOrigin)) {
			return null;
		}
		if (ObjectUtils.isEmpty(this.allowedOrigins)) {
			return null;
		}

		if (this.allowedOrigins.contains(ALL)) {
			if (this.allowCredentials != Boolean.TRUE) {
				return ALL;
			}
			else {
				return requestOrigin;
			}
		}
		for (String allowedOrigin : this.allowedOrigins) {
			if (requestOrigin.equalsIgnoreCase(allowedOrigin)) {
				return requestOrigin;
			}
		}

		return null;
	}

	/**
	 * Check the HTTP request method (or the method from the
	 * {@code Access-Control-Request-Method} header on a pre-flight request)
	 * against the configured allowed methods.
	 * <p>
	 *  根据配置的允许方法检查HTTP请求方法(或者在飞行前请求中的{@code Access-Control-Request-Method}头部的方法)
	 * 
	 * 
	 * @param requestMethod the HTTP request method to check
	 * @return the list of HTTP methods to list in the response of a pre-flight
	 * request, or {@code null} if the supplied {@code requestMethod} is not allowed
	 */
	public List<HttpMethod> checkHttpMethod(HttpMethod requestMethod) {
		if (requestMethod == null) {
			return null;
		}
		if (this.resolvedMethods == null) {
			return Collections.singletonList(requestMethod);
		}
		return (this.resolvedMethods.contains(requestMethod) ? this.resolvedMethods : null);
	}

	/**
	 * Check the supplied request headers (or the headers listed in the
	 * {@code Access-Control-Request-Headers} of a pre-flight request) against
	 * the configured allowed headers.
	 * <p>
	 *  检查所提供的请求标头(或者在{@code Access-Control-Request-Headers}中列出的前置请求报头中的标题)对已配置的允许标题
	 * 
	 * @param requestHeaders the request headers to check
	 * @return the list of allowed headers to list in the response of a pre-flight
	 * request, or {@code null} if none of the supplied request headers is allowed
	 */
	public List<String> checkHeaders(List<String> requestHeaders) {
		if (requestHeaders == null) {
			return null;
		}
		if (requestHeaders.isEmpty()) {
			return Collections.emptyList();
		}
		if (ObjectUtils.isEmpty(this.allowedHeaders)) {
			return null;
		}

		boolean allowAnyHeader = this.allowedHeaders.contains(ALL);
		List<String> result = new ArrayList<String>(requestHeaders.size());
		for (String requestHeader : requestHeaders) {
			if (StringUtils.hasText(requestHeader)) {
				requestHeader = requestHeader.trim();
				if (allowAnyHeader) {
					result.add(requestHeader);
				}
				else {
					for (String allowedHeader : this.allowedHeaders) {
						if (requestHeader.equalsIgnoreCase(allowedHeader)) {
							result.add(requestHeader);
							break;
						}
					}
				}
			}
		}
		return (result.isEmpty() ? null : result);
	}

}
