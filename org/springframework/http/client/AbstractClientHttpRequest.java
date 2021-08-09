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

import java.io.IOException;
import java.io.OutputStream;

import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;

/**
 * Abstract base for {@link ClientHttpRequest} that makes sure that headers
 * and body are not written multiple times.
 *
 * <p>
 *  {@link ClientHttpRequest}的抽象基础,确保标题和正文不被多次写入
 * 
 * 
 * @author Arjen Poutsma
 * @since 3.0
 */
public abstract class AbstractClientHttpRequest implements ClientHttpRequest {

	private final HttpHeaders headers = new HttpHeaders();

	private boolean executed = false;


	@Override
	public final HttpHeaders getHeaders() {
		return (this.executed ? HttpHeaders.readOnlyHttpHeaders(this.headers) : this.headers);
	}

	@Override
	public final OutputStream getBody() throws IOException {
		assertNotExecuted();
		return getBodyInternal(this.headers);
	}

	@Override
	public final ClientHttpResponse execute() throws IOException {
		assertNotExecuted();
		ClientHttpResponse result = executeInternal(this.headers);
		this.executed = true;
		return result;
	}

	/**
	 * Assert that this request has not been {@linkplain #execute() executed} yet.
	 * <p>
	 *  断言此请求尚未{@linkplain #execute()执行}
	 * 
	 * 
	 * @throws IllegalStateException if this request has been executed
	 */
	protected void assertNotExecuted() {
		Assert.state(!this.executed, "ClientHttpRequest already executed");
	}


	/**
	 * Abstract template method that returns the body.
	 * <p>
	 * 抽象模板方法返回身体
	 * 
	 * 
	 * @param headers the HTTP headers
	 * @return the body output stream
	 */
	protected abstract OutputStream getBodyInternal(HttpHeaders headers) throws IOException;

	/**
	 * Abstract template method that writes the given headers and content to the HTTP request.
	 * <p>
	 *  将给定的头文件和内容写入HTTP请求的抽象模板方法
	 * 
	 * @param headers the HTTP headers
	 * @return the response object for the executed request
	 */
	protected abstract ClientHttpResponse executeInternal(HttpHeaders headers) throws IOException;

}
