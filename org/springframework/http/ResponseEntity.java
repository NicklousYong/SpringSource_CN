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

package org.springframework.http;

import java.net.URI;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

/**
 * Extension of {@link HttpEntity} that adds a {@link HttpStatus} status code.
 * Used in {@code RestTemplate} as well {@code @Controller} methods.
 *
 * <p>In {@code RestTemplate}, this class is returned by
 * {@link org.springframework.web.client.RestTemplate#getForEntity getForEntity()} and
 * {@link org.springframework.web.client.RestTemplate#exchange exchange()}:
 * <pre class="code">
 * ResponseEntity&lt;String&gt; entity = template.getForEntity("http://example.com", String.class);
 * String body = entity.getBody();
 * MediaType contentType = entity.getHeaders().getContentType();
 * HttpStatus statusCode = entity.getStatusCode();
 * </pre>
 *
 * <p>Can also be used in Spring MVC, as the return value from a @Controller method:
 * <pre class="code">
 * &#64;RequestMapping("/handle")
 * public ResponseEntity&lt;String&gt; handle() {
 *   URI location = ...;
 *   HttpHeaders responseHeaders = new HttpHeaders();
 *   responseHeaders.setLocation(location);
 *   responseHeaders.set("MyResponseHeader", "MyValue");
 *   return new ResponseEntity&lt;String&gt;("Hello World", responseHeaders, HttpStatus.CREATED);
 * }
 * </pre>
 * Or, by using a builder accessible via static methods:
 * <pre class="code">
 * &#64;RequestMapping("/handle")
 * public ResponseEntity&lt;String&gt; handle() {
 *   URI location = ...;
 *   return ResponseEntity.created(location).header("MyResponseHeader", "MyValue").body("Hello World");
 * }
 * </pre>
 *
 * <p>
 *  在{@code RestTemplate}中添加{@link HttpStatus}状态代码的{@link HttpEntity}扩展{@code @Controller}方法
 * 
 * <p>在{@code RestTemplate}中,此类由{@link orgspringframeworkwebclientRestTemplate#getForEntity getForEntity()}
 * 和{@link orgspringframeworkwebclientRestTemplate#exchange exchange()}返回：。
 * <pre class="code">
 *  ResponseEntity&LT;字符串&GT; entity = templategetForEntity("http：// examplecom",Stringclass); String bo
 * dy = entitygetBody(); MediaType contentType = entitygetHeaders()getContentType(); HttpStatus statusCo
 * de = entitygetStatusCode();。
 * </pre>
 * 
 *  <p>也可以在Spring MVC中使用,作为@Controller方法的返回值：
 * <pre class="code">
 * @RequestMapping("/ handle")public ResponseEntity&lt; String&gt; handle(){URI location =; HttpHeaders responseHeaders = new HttpHeaders(); responseHeaderssetLocation(位置); responseHeadersset("MyResponseHeader","MyValue");返回新的ResponseEntity&lt; String&gt;("Hello World",responseHeaders,HttpStatusCREATED); }
 * 。
 * </pre>
 *  或者,通过使用可通过静态方法访问的构建器：
 * <pre class="code">
 *  @RequestMapping("/ handle")public ResponseEntity&lt; String&gt; handle(){URI location =;返回ResponseEntitycreated(location)头("MyResponseHeader","MyValue")body("Hello World"); }
 * 。
 * </pre>
 * 
 * 
 * @author Arjen Poutsma
 * @author Brian Clozel
 * @since 3.0.2
 * @see #getStatusCode()
 */
public class ResponseEntity<T> extends HttpEntity<T> {

	private final Object statusCode;


	/**
	 * Create a new {@code ResponseEntity} with the given status code, and no body nor headers.
	 * <p>
	 *  用给定的状态代码创建一个新的{@code ResponseEntity},没有body或header
	 * 
	 * 
	 * @param status the status code
	 */
	public ResponseEntity(HttpStatus status) {
		this(null, null, status);
	}

	/**
	 * Create a new {@code ResponseEntity} with the given body and status code, and no headers.
	 * <p>
	 *  使用给定的正文和状态代码创建一个新的{@code ResponseEntity},没有标题
	 * 
	 * 
	 * @param body the entity body
	 * @param status the status code
	 */
	public ResponseEntity(T body, HttpStatus status) {
		this(body, null, status);
	}

	/**
	 * Create a new {@code HttpEntity} with the given headers and status code, and no body.
	 * <p>
	 * 使用给定的标题和状态代码创建一个新的{@code HttpEntity},没有正文
	 * 
	 * 
	 * @param headers the entity headers
	 * @param status the status code
	 */
	public ResponseEntity(MultiValueMap<String, String> headers, HttpStatus status) {
		this(null, headers, status);
	}

	/**
	 * Create a new {@code HttpEntity} with the given body, headers, and status code.
	 * <p>
	 *  使用给定的正文,标题和状态代码创建一个新的{@code HttpEntity}
	 * 
	 * 
	 * @param body the entity body
	 * @param headers the entity headers
	 * @param status the status code
	 */
	public ResponseEntity(T body, MultiValueMap<String, String> headers, HttpStatus status) {
		super(body, headers);
		Assert.notNull(status, "HttpStatus must not be null");
		this.statusCode = status;
	}

	/**
	 * Create a new {@code HttpEntity} with the given body, headers, and status code.
	 * Just used behind the nested builder API.
	 * <p>
	 *  使用给定的主体,标题和状态代码创建一个新的{@code HttpEntity}刚刚在嵌套构建器API后面使用
	 * 
	 * 
	 * @param body the entity body
	 * @param headers the entity headers
	 * @param statusCode the status code (as {@code HttpStatus} or as {@code Integer} value)
	 */
	private ResponseEntity(T body, MultiValueMap<String, String> headers, Object statusCode) {
		super(body, headers);
		this.statusCode = statusCode;
	}


	/**
	 * Return the HTTP status code of the response.
	 * <p>
	 *  返回响应的HTTP状态代码
	 * 
	 * 
	 * @return the HTTP status as an HttpStatus enum entry
	 */
	public HttpStatus getStatusCode() {
		if (this.statusCode instanceof HttpStatus) {
			return (HttpStatus) this.statusCode;
		}
		else {
			return HttpStatus.valueOf((Integer) this.statusCode);
		}
	}

	/**
	 * Return the HTTP status code of the response.
	 * <p>
	 *  返回响应的HTTP状态代码
	 * 
	 * 
	 * @return the HTTP status as an int value
	 * @since 4.3
	 */
	public int getStatusCodeValue() {
		if (this.statusCode instanceof HttpStatus) {
			return ((HttpStatus) this.statusCode).value();
		}
		else {
			return (Integer) this.statusCode;
		}
	}


	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!super.equals(other)) {
			return false;
		}
		ResponseEntity<?> otherEntity = (ResponseEntity<?>) other;
		return ObjectUtils.nullSafeEquals(this.statusCode, otherEntity.statusCode);
	}

	@Override
	public int hashCode() {
		return (super.hashCode() * 29 + ObjectUtils.nullSafeHashCode(this.statusCode));
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("<");
		builder.append(this.statusCode.toString());
		if (this.statusCode instanceof HttpStatus) {
			builder.append(' ');
			builder.append(((HttpStatus) this.statusCode).getReasonPhrase());
		}
		builder.append(',');
		T body = getBody();
		HttpHeaders headers = getHeaders();
		if (body != null) {
			builder.append(body);
			if (headers != null) {
				builder.append(',');
			}
		}
		if (headers != null) {
			builder.append(headers);
		}
		builder.append('>');
		return builder.toString();
	}


	// Static builder methods

	/**
	 * Create a builder with the given status.
	 * <p>
	 *  创建具有给定状态的构建器
	 * 
	 * 
	 * @param status the response status
	 * @return the created builder
	 * @since 4.1
	 */
	public static BodyBuilder status(HttpStatus status) {
		Assert.notNull(status, "HttpStatus must not be null");
		return new DefaultBuilder(status);
	}

	/**
	 * Create a builder with the given status.
	 * <p>
	 *  创建具有给定状态的构建器
	 * 
	 * 
	 * @param status the response status
	 * @return the created builder
	 * @since 4.1
	 */
	public static BodyBuilder status(int status) {
		return new DefaultBuilder(status);
	}

	/**
	 * Create a builder with the status set to {@linkplain HttpStatus#OK OK}.
	 * <p>
	 *  创建状态设置为{@linkplain HttpStatus#OK OK}的构建器
	 * 
	 * 
	 * @return the created builder
	 * @since 4.1
	 */
	public static BodyBuilder ok() {
		return status(HttpStatus.OK);
	}

	/**
	 * A shortcut for creating a {@code ResponseEntity} with the given body and
	 * the status set to {@linkplain HttpStatus#OK OK}.
	 * <p>
	 *  创建具有给定正文的{@code ResponseEntity}的快捷方式,状态设置为{@linkplain HttpStatus#OK OK}
	 * 
	 * 
	 * @return the created {@code ResponseEntity}
	 * @since 4.1
	 */
	public static <T> ResponseEntity<T> ok(T body) {
		BodyBuilder builder = ok();
		return builder.body(body);
	}

	/**
	 * Create a new builder with a {@linkplain HttpStatus#CREATED CREATED} status
	 * and a location header set to the given URI.
	 * <p>
	 *  创建一个具有{@linkplain HttpStatus#CREATED CREATED}状态的新构建器,并将位置头设置为给定的URI
	 * 
	 * 
	 * @param location the location URI
	 * @return the created builder
	 * @since 4.1
	 */
	public static BodyBuilder created(URI location) {
		BodyBuilder builder = status(HttpStatus.CREATED);
		return builder.location(location);
	}

	/**
	 * Create a builder with an {@linkplain HttpStatus#ACCEPTED ACCEPTED} status.
	 * <p>
	 * 创建一个具有{@linkplain HttpStatus#ACCEPTED ACCEPTED}状态的构建器
	 * 
	 * 
	 * @return the created builder
	 * @since 4.1
	 */
	public static BodyBuilder accepted() {
		return status(HttpStatus.ACCEPTED);
	}

	/**
	 * Create a builder with a {@linkplain HttpStatus#NO_CONTENT NO_CONTENT} status.
	 * <p>
	 *  创建一个{@linkplain HttpStatus#NO_CONTENT NO_CONTENT}状态的构建器
	 * 
	 * 
	 * @return the created builder
	 * @since 4.1
	 */
	public static HeadersBuilder<?> noContent() {
		return status(HttpStatus.NO_CONTENT);
	}

	/**
	 * Create a builder with a {@linkplain HttpStatus#BAD_REQUEST BAD_REQUEST} status.
	 * <p>
	 *  创建一个具有{@linkplain HttpStatus#BAD_REQUEST BAD_REQUEST}状态的构建器
	 * 
	 * 
	 * @return the created builder
	 * @since 4.1
	 */
	public static BodyBuilder badRequest() {
		return status(HttpStatus.BAD_REQUEST);
	}

	/**
	 * Create a builder with a {@linkplain HttpStatus#NOT_FOUND NOT_FOUND} status.
	 * <p>
	 *  创建一个{@linkplain HttpStatus#NOT_FOUND NOT_FOUND}状态的构建器
	 * 
	 * 
	 * @return the created builder
	 * @since 4.1
	 */
	public static HeadersBuilder<?> notFound() {
		return status(HttpStatus.NOT_FOUND);
	}

	/**
	 * Create a builder with an
	 * {@linkplain HttpStatus#UNPROCESSABLE_ENTITY UNPROCESSABLE_ENTITY} status.
	 * <p>
	 *  创建一个{@linkplain HttpStatus#UNPROCESSABLE_ENTITY UNPROCESSABLE_ENTITY}状态的构建器
	 * 
	 * 
	 * @return the created builder
	 * @since 4.1.3
	 */
	public static BodyBuilder unprocessableEntity() {
		return status(HttpStatus.UNPROCESSABLE_ENTITY);
	}


	/**
	 * Defines a builder that adds headers to the response entity.
	 * <p>
	 *  定义一个向响应实体添加标题的构建器
	 * 
	 * 
	 * @param <B> the builder subclass
	 * @since 4.1
	 */
	public interface HeadersBuilder<B extends HeadersBuilder<B>> {

		/**
		 * Add the given, single header value under the given name.
		 * <p>
		 *  在给定的名称下添加给定的单个头值
		 * 
		 * 
		 * @param headerName the header name
		 * @param headerValues the header value(s)
		 * @return this builder
		 * @see HttpHeaders#add(String, String)
		 */
		B header(String headerName, String... headerValues);

		/**
		 * Copy the given headers into the entity's headers map.
		 * <p>
		 *  将给定的标题复制到实体的标题地图中
		 * 
		 * 
		 * @param headers the existing HttpHeaders to copy from
		 * @return this builder
		 * @since 4.1.2
		 * @see HttpHeaders#add(String, String)
		 */
		B headers(HttpHeaders headers);

		/**
		 * Set the set of allowed {@link HttpMethod HTTP methods}, as specified
		 * by the {@code Allow} header.
		 * <p>
		 *  根据{@code Allow}标题指定,设置允许的{@link HttpMethod HTTP方法}集合
		 * 
		 * 
		 * @param allowedMethods the allowed methods
		 * @return this builder
		 * @see HttpHeaders#setAllow(Set)
		 */
		B allow(HttpMethod... allowedMethods);

		/**
		 * Set the entity tag of the body, as specified by the {@code ETag} header.
		 * <p>
		 *  根据{@code ETag}标题指定,设置正文的实体标签
		 * 
		 * 
		 * @param eTag the new entity tag
		 * @return this builder
		 * @see HttpHeaders#setETag(String)
		 */
		B eTag(String eTag);

		/**
		 * Set the time the resource was last changed, as specified by the
		 * {@code Last-Modified} header.
		 * <p>The date should be specified as the number of milliseconds since
		 * January 1, 1970 GMT.
		 * <p>
		 * 设置最后更改资源的时间,如{@code Last-Modified}标题<p>所规定的日期应指定为自1970年1月1日GMT以来的毫秒数
		 * 
		 * 
		 * @param lastModified the last modified date
		 * @return this builder
		 * @see HttpHeaders#setLastModified(long)
		 */
		B lastModified(long lastModified);

		/**
		 * Set the location of a resource, as specified by the {@code Location} header.
		 * <p>
		 *  根据{@code位置}标题指定设置资源的位置
		 * 
		 * 
		 * @param location the location
		 * @return this builder
		 * @see HttpHeaders#setLocation(URI)
		 */
		B location(URI location);

		/**
		 * Set the caching directives for the resource, as specified by the HTTP 1.1
		 * {@code Cache-Control} header.
		 * <p>A {@code CacheControl} instance can be built like
		 * {@code CacheControl.maxAge(3600).cachePublic().noTransform()}.
		 * <p>
		 *  为资源设置缓存指令,如HTTP 11 {@code Cache-Control}头部所指定的。
		 * {@code CacheControl}实例可以像{@code CacheControlmaxAge(3600)cachePublic()noTransform())一样构建。
		 * 
		 * 
		 * @param cacheControl a builder for cache-related HTTP response headers
		 * @return this builder
		 * @since 4.2
		 * @see <a href="https://tools.ietf.org/html/rfc7234#section-5.2">RFC-7234 Section 5.2</a>
		 */
		B cacheControl(CacheControl cacheControl);

		/**
		 * Configure one or more request header names (e.g. "Accept-Language") to
		 * add to the "Vary" response header to inform clients that the response is
		 * subject to content negotiation and variances based on the value of the
		 * given request headers. The configured request header names are added only
		 * if not already present in the response "Vary" header.
		 * <p>
		 * 配置一个或多个请求头名称(例如"Accept-Language")以添加到"Vary"响应头,以通知客户端响应内容协商和基于给定请求头的值的方差配置的请求头名称只有在响应"Vary"标题中不存在时才会添
		 * 加。
		 * 
		 * 
		 * @param requestHeaders request header names
		 * @since 4.3
		 */
		B varyBy(String... requestHeaders);

		/**
		 * Build the response entity with no body.
		 * <p>
		 *  建立没有身体的响应实体
		 * 
		 * 
		 * @return the response entity
		 * @see BodyBuilder#body(Object)
		 */
		ResponseEntity<Void> build();
	}


	/**
	 * Defines a builder that adds a body to the response entity.
	 * <p>
	 *  定义将身体添加到响应实体的构建器
	 * 
	 * 
	 * @since 4.1
	 */
	public interface BodyBuilder extends HeadersBuilder<BodyBuilder> {

		/**
		 * Set the length of the body in bytes, as specified by the
		 * {@code Content-Length} header.
		 * <p>
		 *  按照{@code Content-Length}标题指定的字节设置身体的长度
		 * 
		 * 
		 * @param contentLength the content length
		 * @return this builder
		 * @see HttpHeaders#setContentLength(long)
		 */
		BodyBuilder contentLength(long contentLength);

		/**
		 * Set the {@linkplain MediaType media type} of the body, as specified by the
		 * {@code Content-Type} header.
		 * <p>
		 *  根据{@code Content-Type}标题指定,设置正文的{@linkplain MediaType媒体类型}
		 * 
		 * 
		 * @param contentType the content type
		 * @return this builder
		 * @see HttpHeaders#setContentType(MediaType)
		 */
		BodyBuilder contentType(MediaType contentType);

		/**
		 * Set the body of the response entity and returns it.
		 * <p>
		 *  设置响应实体的主体并返回它
		 * 
		 * @param <T> the type of the body
		 * @param body the body of the response entity
		 * @return the built response entity
		 */
		<T> ResponseEntity<T> body(T body);
	}


	private static class DefaultBuilder implements BodyBuilder {

		private final Object statusCode;

		private final HttpHeaders headers = new HttpHeaders();

		public DefaultBuilder(Object statusCode) {
			this.statusCode = statusCode;
		}

		@Override
		public BodyBuilder header(String headerName, String... headerValues) {
			for (String headerValue : headerValues) {
				this.headers.add(headerName, headerValue);
			}
			return this;
		}

		@Override
		public BodyBuilder headers(HttpHeaders headers) {
			if (headers != null) {
				this.headers.putAll(headers);
			}
			return this;
		}

		@Override
		public BodyBuilder allow(HttpMethod... allowedMethods) {
			this.headers.setAllow(new LinkedHashSet<HttpMethod>(Arrays.asList(allowedMethods)));
			return this;
		}

		@Override
		public BodyBuilder contentLength(long contentLength) {
			this.headers.setContentLength(contentLength);
			return this;
		}

		@Override
		public BodyBuilder contentType(MediaType contentType) {
			this.headers.setContentType(contentType);
			return this;
		}

		@Override
		public BodyBuilder eTag(String eTag) {
			if (eTag != null) {
				if (!eTag.startsWith("\"") && !eTag.startsWith("W/\"")) {
					eTag = "\"" + eTag;
				}
				if (!eTag.endsWith("\"")) {
					eTag = eTag + "\"";
				}
			}
			this.headers.setETag(eTag);
			return this;
		}

		@Override
		public BodyBuilder lastModified(long date) {
			this.headers.setLastModified(date);
			return this;
		}

		@Override
		public BodyBuilder location(URI location) {
			this.headers.setLocation(location);
			return this;
		}

		@Override
		public BodyBuilder cacheControl(CacheControl cacheControl) {
			String ccValue = cacheControl.getHeaderValue();
			if (ccValue != null) {
				this.headers.setCacheControl(cacheControl.getHeaderValue());
			}
			return this;
		}

		@Override
		public BodyBuilder varyBy(String... requestHeaders) {
			this.headers.setVary(Arrays.asList(requestHeaders));
			return this;
		}

		@Override
		public ResponseEntity<Void> build() {
			return body(null);
		}

		@Override
		public <T> ResponseEntity<T> body(T body) {
			return new ResponseEntity<T>(body, this.headers, this.statusCode);
		}
	}

}
