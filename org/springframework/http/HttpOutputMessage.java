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

import java.io.IOException;
import java.io.OutputStream;

/**
 * Represents an HTTP output message, consisting of {@linkplain #getHeaders() headers}
 * and a writable {@linkplain #getBody() body}.
 *
 * <p>Typically implemented by an HTTP request handle on the client side,
 * or an HTTP response handle on the server side.
 *
 * <p>
 * 
 * @author Arjen Poutsma
 * @since 3.0
 */
public interface HttpOutputMessage extends HttpMessage {

	/**
	 * Return the body of the message as an output stream.
	 * <p>
	 *  表示一个HTTP输出消息,由{@linkplain #getHeaders()标头}和可写{@linkplain #getBody()body}组成}
	 * 
	 * <p>通常由客户端上的HTTP请求句柄或服务器端的HTTP响应句柄实现
	 * 
	 * 
	 * @return the output stream body (never {@code null})
	 * @throws IOException in case of I/O Errors
	 */
	OutputStream getBody() throws IOException;

}
