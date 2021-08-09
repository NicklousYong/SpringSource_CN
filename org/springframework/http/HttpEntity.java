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

package org.springframework.http;

import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

/**
 * Represents an HTTP request or response entity, consisting of headers and body.
 *
 * <p>Typically used in combination with the {@link org.springframework.web.client.RestTemplate},
 * like so:
 * <pre class="code">
 * HttpHeaders headers = new HttpHeaders();
 * headers.setContentType(MediaType.TEXT_PLAIN);
 * HttpEntity&lt;String&gt; entity = new HttpEntity&lt;String&gt;(helloWorld, headers);
 * URI location = template.postForLocation("http://example.com", entity);
 * </pre>
 * or
 * <pre class="code">
 * HttpEntity&lt;String&gt; entity = template.getForEntity("http://example.com", String.class);
 * String body = entity.getBody();
 * MediaType contentType = entity.getHeaders().getContentType();
 * </pre>
 * Can also be used in Spring MVC, as a return value from a @Controller method:
 * <pre class="code">
 * &#64;RequestMapping("/handle")
 * public HttpEntity&lt;String&gt; handle() {
 *   HttpHeaders responseHeaders = new HttpHeaders();
 *   responseHeaders.set("MyResponseHeader", "MyValue");
 *   return new HttpEntity&lt;String&gt;("Hello World", responseHeaders);
 * }
 * </pre>
 *
 * <p>
 *  表示HTTP请求或响应实体,由头和主体组成
 * 
 *  通常与{@link orgspringframeworkwebclientRestTemplate}结合使用,像这样：
 * <pre class="code">
 * HttpHeaders headers = new HttpHeaders(); headerssetContentType(MediaTypeTEXT_PLAIN); HttpEntity&LT;字符
 * 串&GT; entity = new HttpEntity&lt; String&gt;(helloWorld,headers); URI位置= templatepostForLocation("htt
 * p：// examplecom",entity);。
 * </pre>
 *  要么
 * <pre class="code">
 *  HttpEntity&LT;字符串&GT; entity = templategetForEntity("http：// examplecom",Stringclass); String body =
 *  entitygetBody(); MediaType contentType = entitygetHeaders()getContentType();。
 * </pre>
 *  也可以在Spring MVC中使用,作为@Controller方法的返回值：
 * <pre class="code">
 *  @RequestMapping("/ handle")public HttpEntity&lt; String&gt; handle(){HttpHeaders responseHeaders = new HttpHeaders(); responseHeadersset("MyResponseHeader","MyValue");返回新的HttpEntity&lt; String&gt;("Hello World",responseHeaders); }
 * 。
 * </pre>
 * 
 * 
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @since 3.0.2
 * @see org.springframework.web.client.RestTemplate
 * @see #getBody()
 * @see #getHeaders()
 */
public class HttpEntity<T> {

	/**
	 * The empty {@code HttpEntity}, with no body or headers.
	 * <p>
	 * 空的{@code HttpEntity},没有正文或标题
	 * 
	 */
	public static final HttpEntity<?> EMPTY = new HttpEntity<Object>();


	private final HttpHeaders headers;

	private final T body;


	/**
	 * Create a new, empty {@code HttpEntity}.
	 * <p>
	 *  创建一个新的,空的{@code HttpEntity}
	 * 
	 */
	protected HttpEntity() {
		this(null, null);
	}

	/**
	 * Create a new {@code HttpEntity} with the given body and no headers.
	 * <p>
	 *  创建一个新的{@code HttpEntity}与给定的身体和没有标题
	 * 
	 * 
	 * @param body the entity body
	 */
	public HttpEntity(T body) {
		this(body, null);
	}

	/**
	 * Create a new {@code HttpEntity} with the given headers and no body.
	 * <p>
	 *  创建一个新的{@code HttpEntity}与给定的标题,没有正文
	 * 
	 * 
	 * @param headers the entity headers
	 */
	public HttpEntity(MultiValueMap<String, String> headers) {
		this(null, headers);
	}

	/**
	 * Create a new {@code HttpEntity} with the given body and headers.
	 * <p>
	 *  使用给定的正文和标题创建一个新的{@code HttpEntity}
	 * 
	 * 
	 * @param body the entity body
	 * @param headers the entity headers
	 */
	public HttpEntity(T body, MultiValueMap<String, String> headers) {
		this.body = body;
		HttpHeaders tempHeaders = new HttpHeaders();
		if (headers != null) {
			tempHeaders.putAll(headers);
		}
		this.headers = HttpHeaders.readOnlyHttpHeaders(tempHeaders);
	}


	/**
	 * Returns the headers of this entity.
	 * <p>
	 *  返回此实体的标题
	 * 
	 */
	public HttpHeaders getHeaders() {
		return this.headers;
	}

	/**
	 * Returns the body of this entity.
	 * <p>
	 *  返回此实体的正文
	 * 
	 */
	public T getBody() {
		return this.body;
	}

	/**
	 * Indicates whether this entity has a body.
	 * <p>
	 *  指示该实体是否具有正文
	 */
	public boolean hasBody() {
		return (this.body != null);
	}


	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || other.getClass() != getClass()) {
			return false;
		}
		HttpEntity<?> otherEntity = (HttpEntity<?>) other;
		return (ObjectUtils.nullSafeEquals(this.headers, otherEntity.headers) &&
				ObjectUtils.nullSafeEquals(this.body, otherEntity.body));
	}

	@Override
	public int hashCode() {
		return (ObjectUtils.nullSafeHashCode(this.headers) * 29 + ObjectUtils.nullSafeHashCode(this.body));
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("<");
		if (this.body != null) {
			builder.append(this.body);
			if (this.headers != null) {
				builder.append(',');
			}
		}
		if (this.headers != null) {
			builder.append(this.headers);
		}
		builder.append('>');
		return builder.toString();
	}

}
