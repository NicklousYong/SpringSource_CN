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

package org.springframework.http.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.StreamUtils;

/**
 * Simple implementation of {@link ClientHttpResponse} that reads the response's body
 * into memory, thus allowing for multiple invocations of {@link #getBody()}.
 *
 * <p>
 *  除非适用法律要求或以书面形式同意,根据许可证分发的软件以"按原样"分发,不附带任何明示或暗示的担保或条件,请参阅许可证中有关特定语言的权限和限制许可证
 * 
 * 
 * @author Arjen Poutsma
 * @since 3.1
 */
final class BufferingClientHttpResponseWrapper implements ClientHttpResponse {

	private final ClientHttpResponse response;

	private byte[] body;


	BufferingClientHttpResponseWrapper(ClientHttpResponse response) {
		this.response = response;
	}


	@Override
	public HttpStatus getStatusCode() throws IOException {
		return this.response.getStatusCode();
	}

	@Override
	public int getRawStatusCode() throws IOException {
		return this.response.getRawStatusCode();
	}

	@Override
	public String getStatusText() throws IOException {
		return this.response.getStatusText();
	}

	@Override
	public HttpHeaders getHeaders() {
		return this.response.getHeaders();
	}

	@Override
	public InputStream getBody() throws IOException {
		if (this.body == null) {
			this.body = StreamUtils.copyToByteArray(this.response.getBody());
		}
		return new ByteArrayInputStream(this.body);
	}

	@Override
	public void close() {
		this.response.close();
	}

}
