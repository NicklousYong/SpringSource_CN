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

package org.springframework.web.client;

import java.nio.charset.Charset;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

/**
 * Abstract base class for exceptions based on an {@link HttpStatus}.
 *
 * <p>
 *  基于{@link HttpStatus}的异常抽象基类
 * 
 * 
 * @author Arjen Poutsma
 * @author Chris Beams
 * @author Rossen Stoyanchev
 * @since 3.0
 */
public abstract class HttpStatusCodeException extends RestClientResponseException {

	private static final long serialVersionUID = 5696801857651587810L;


	private final HttpStatus statusCode;


	/**
	 * Construct a new instance with an {@link HttpStatus}.
	 * <p>
	 *  使用{@link HttpStatus}构造一个新的实例
	 * 
	 * 
	 * @param statusCode the status code
	 */
	protected HttpStatusCodeException(HttpStatus statusCode) {
		this(statusCode, statusCode.name(), null, null, null);
	}

	/**
	 * Construct a new instance with an {@link HttpStatus} and status text.
	 * <p>
	 *  使用{@link HttpStatus}和状态文本构造新的实例
	 * 
	 * 
	 * @param statusCode the status code
	 * @param statusText the status text
	 */
	protected HttpStatusCodeException(HttpStatus statusCode, String statusText) {
		this(statusCode, statusText, null, null, null);
	}

	/**
	 * Construct instance with an {@link HttpStatus}, status text, and content.
	 * <p>
	 * 使用{@link HttpStatus}构建实例,状态文本和内容
	 * 
	 * 
	 * @param statusCode the status code
	 * @param statusText the status text
	 * @param responseBody the response body content, may be {@code null}
	 * @param responseCharset the response body charset, may be {@code null}
	 * @since 3.0.5
	 */
	protected HttpStatusCodeException(HttpStatus statusCode, String statusText,
			byte[] responseBody, Charset responseCharset) {

		this(statusCode, statusText, null, responseBody, responseCharset);
	}

	/**
	 * Construct instance with an {@link HttpStatus}, status text, content, and
	 * a response charset.
	 * <p>
	 *  使用{@link HttpStatus}构建实例,状态文本,内容和响应字符集
	 * 
	 * 
	 * @param statusCode the status code
	 * @param statusText the status text
	 * @param responseHeaders the response headers, may be {@code null}
	 * @param responseBody the response body content, may be {@code null}
	 * @param responseCharset the response body charset, may be {@code null}
	 * @since 3.1.2
	 */
	protected HttpStatusCodeException(HttpStatus statusCode, String statusText,
			HttpHeaders responseHeaders, byte[] responseBody, Charset responseCharset) {

		super(statusCode.value() + " " + statusText, statusCode.value(), statusText,
				responseHeaders, responseBody, responseCharset);
		this.statusCode = statusCode;
	}


	/**
	 * Return the HTTP status code.
	 * <p>
	 *  返回HTTP状态代码
	 */
	public HttpStatus getStatusCode() {
		return this.statusCode;
	}

}
