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

import java.net.URI;

/**
 * Represents an HTTP request message, consisting of
 * {@linkplain #getMethod() method} and {@linkplain #getURI() uri}.
 *
 * <p>
 *  代表HTTP请求消息,由{@linkplain #getMethod()方法}和{@linkplain #getURI()uri}组成)
 * 
 * 
 * @author Arjen Poutsma
 * @since 3.1
 */
public interface HttpRequest extends HttpMessage {

	/**
	 * Return the HTTP method of the request.
	 * <p>
	 *  返回请求的HTTP方法
	 * 
	 * 
	 * @return the HTTP method as an HttpMethod enum value, or {@code null}
	 * if not resolvable (e.g. in case of a non-standard HTTP method)
	 */
	HttpMethod getMethod();

	/**
	 * Return the URI of the request.
	 * <p>
	 *  返回请求的URI
	 * 
	 * @return the URI of the request (never {@code null})
	 */
	URI getURI();

}
