/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2014 the original author or authors.
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

package org.springframework.http.client;

import java.io.Closeable;
import java.io.IOException;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;

/**
 * Represents a client-side HTTP response.
 * Obtained via an calling of the {@link ClientHttpRequest#execute()}.
 *
 * <p>A {@code ClientHttpResponse} must be {@linkplain #close() closed},
 * typically in a {@code finally} block.
 *
 * <p>
 *  表示客户端HTTP响应通过调用{@link ClientHttpRequest#execute()}获取
 * 
 *  <p> {@code ClientHttpResponse}必须{@linkplain #close()closed},通常在{@code finally}块中
 * 
 * 
 * @author Arjen Poutsma
 * @since 3.0
 */
public interface ClientHttpResponse extends HttpInputMessage, Closeable {

	/**
	 * Return the HTTP status code of the response.
	 * <p>
	 * 返回响应的HTTP状态代码
	 * 
	 * 
	 * @return the HTTP status as an HttpStatus enum value
	 * @throws IOException in case of I/O errors
	 */
	HttpStatus getStatusCode() throws IOException;

	/**
	 * Return the HTTP status code of the response as integer
	 * <p>
	 *  将响应的HTTP状态代码返回为整数
	 * 
	 * 
	 * @return the HTTP status as an integer
	 * @throws IOException in case of I/O errors
	 */
	int getRawStatusCode() throws IOException;

	/**
	 * Return the HTTP status text of the response.
	 * <p>
	 *  返回响应的HTTP状态文本
	 * 
	 * 
	 * @return the HTTP status text
	 * @throws IOException in case of I/O errors
	 */
	String getStatusText() throws IOException;

	/**
	 * Close this response, freeing any resources created.
	 * <p>
	 *  关闭此响应,释放所创建的所有资源
	 */
	@Override
	void close();

}
