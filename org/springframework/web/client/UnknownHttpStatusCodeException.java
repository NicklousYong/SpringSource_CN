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
 * Exception thrown when an unknown (or custom) HTTP status code is received.
 *
 * <p>
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 3.2
 */
public class UnknownHttpStatusCodeException extends RestClientResponseException {

	private static final long serialVersionUID = 7103980251635005491L;


	/**
	 * Construct a new instance of {@code HttpStatusCodeException} based on an
	 * {@link HttpStatus}, status text, and response body content.
	 * <p>
	 *  收到未知(或自定义)HTTP状态代码时抛出异常
	 * 
	 * 
	 * @param rawStatusCode the raw status code value
	 * @param statusText the status text
	 * @param responseHeaders the response headers, may be {@code null}
	 * @param responseBody the response body content, may be {@code null}
	 * @param responseCharset the response body charset, may be {@code null}
	 */
	public UnknownHttpStatusCodeException(int rawStatusCode, String statusText,
			HttpHeaders responseHeaders, byte[] responseBody, Charset responseCharset) {

		super("Unknown status code [" + String.valueOf(rawStatusCode) + "]" + " " + statusText,
				rawStatusCode, statusText, responseHeaders, responseBody, responseCharset);
	}

}
