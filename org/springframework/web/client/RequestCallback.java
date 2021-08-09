/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2009 the original author or authors.
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

import org.springframework.http.client.ClientHttpRequest;

/**
 * Callback interface for code that operates on a {@link ClientHttpRequest}. Allows to manipulate the request
 * headers, and write to the request body.
 *
 * <p>Used internally by the {@link RestTemplate}, but also useful for application code.
 *
 * <p>
 * 
 * @author Arjen Poutsma
 * @see RestTemplate#execute
 * @since 3.0
 */
public interface RequestCallback {

	/**
	 * Gets called by {@link RestTemplate#execute} with an opened {@code ClientHttpRequest}.
	 * Does not need to care about closing the request or about handling errors:
	 * this will all be handled by the {@code RestTemplate}.
	 * <p>
	 *  在{@link ClientHttpRequest}上运行的代码的回调接口允许操纵请求标头,并写入请求主体
	 * 
	 * <p>在{@link RestTemplate}内部使用,但对应用程序代码也很有用
	 * 
	 * 
	 * @param request the active HTTP request
	 * @throws IOException in case of I/O errors
	 */
	void doWithRequest(ClientHttpRequest request) throws IOException;

}
