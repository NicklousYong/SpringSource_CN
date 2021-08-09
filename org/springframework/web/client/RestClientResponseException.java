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

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.springframework.http.HttpHeaders;

/**
 * Common base class for exceptions that contain actual HTTP response data.
 *
 * <p>
 *  包含实际HTTP响应数据的异常的公共基类
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 4.3
 */
public class RestClientResponseException extends RestClientException {

	private static final long serialVersionUID = -8803556342728481792L;

	private static final String DEFAULT_CHARSET = "ISO-8859-1";


	private final int rawStatusCode;

	private final String statusText;

	private final byte[] responseBody;

	private final HttpHeaders responseHeaders;

	private final String responseCharset;


	/**
	 * Construct a new instance of with the given response data.
	 * <p>
	 *  使用给定的响应数据构造新的实例
	 * 
	 * 
	 * @param statusCode the raw status code value
	 * @param statusText the status text
	 * @param responseHeaders the response headers (may be {@code null})
	 * @param responseBody the response body content (may be {@code null})
	 * @param responseCharset the response body charset (may be {@code null})
	 */
	public RestClientResponseException(String message, int statusCode, String statusText,
			HttpHeaders responseHeaders, byte[] responseBody, Charset responseCharset) {

		super(message);
		this.rawStatusCode = statusCode;
		this.statusText = statusText;
		this.responseHeaders = responseHeaders;
		this.responseBody = (responseBody != null ? responseBody : new byte[0]);
		this.responseCharset = (responseCharset != null ? responseCharset.name() : DEFAULT_CHARSET);
	}


	/**
	 * Return the raw HTTP status code value.
	 * <p>
	 *  返回原始的HTTP状态代码值
	 * 
	 */
	public int getRawStatusCode() {
		return this.rawStatusCode;
	}

	/**
	 * Return the HTTP status text.
	 * <p>
	 *  返回HTTP状态文本
	 * 
	 */
	public String getStatusText() {
		return this.statusText;
	}

	/**
	 * Return the HTTP response headers.
	 * <p>
	 * 返回HTTP响应头
	 * 
	 */
	public HttpHeaders getResponseHeaders() {
		return this.responseHeaders;
	}

	/**
	 * Return the response body as a byte array.
	 * <p>
	 *  将响应体返回为字节数组
	 * 
	 */
	public byte[] getResponseBodyAsByteArray() {
		return this.responseBody;
	}

	/**
	 * Return the response body as a string.
	 * <p>
	 *  将响应体返回为字符串
	 */
	public String getResponseBodyAsString() {
		try {
			return new String(this.responseBody, this.responseCharset);
		}
		catch (UnsupportedEncodingException ex) {
			// should not occur
			throw new IllegalStateException(ex);
		}
	}

}
