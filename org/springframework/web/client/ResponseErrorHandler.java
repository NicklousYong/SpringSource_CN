/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2012 the original author or authors.
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

import java.io.IOException;

import org.springframework.http.client.ClientHttpResponse;

/**
 * Strategy interface used by the {@link RestTemplate} to determine whether a particular response has an error or not.
 *
 * <p>
 *  {@link RestTemplate}用于确定特定响应是否有错误的策略界面
 * 
 * 
 * @author Arjen Poutsma
 * @since 3.0
 */
public interface ResponseErrorHandler {

	/**
	 * Indicates whether the given response has any errors.
	 * Implementations will typically inspect the {@link ClientHttpResponse#getStatusCode() HttpStatus}
	 * of the response.
	 * <p>
	 * 指示给定的响应是否有任何错误实现通常会检查响应的{@link ClientHttpResponse#getStatusCode()HttpStatus}
	 * 
	 * 
	 * @param response the response to inspect
	 * @return {@code true} if the response has an error; {@code false} otherwise
	 * @throws IOException in case of I/O errors
	 */
	boolean hasError(ClientHttpResponse response) throws IOException;

	/**
	 * Handles the error in the given response.
	 * This method is only called when {@link #hasError(ClientHttpResponse)} has returned {@code true}.
	 * <p>
	 *  处理给定响应中的错误此方法仅在{@link #hasError(ClientHttpResponse)}返回{@code true}时调用
	 * 
	 * @param response the response with the error
	 * @throws IOException in case of I/O errors
	 */
	void handleError(ClientHttpResponse response) throws IOException;
}
