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

import java.io.IOException;
import java.net.URI;

import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;

/**
 * Abstract base class for {@link ClientHttpRequestFactory} implementations
 * that decorate another request factory.
 *
 * <p>
 *  {@link ClientHttpRequestFactory}实现的抽象基类,用于装饰另一个请求工厂
 * 
 * 
 * @author Arjen Poutsma
 * @since 3.1
 */
public abstract class AbstractClientHttpRequestFactoryWrapper implements ClientHttpRequestFactory {

	private final ClientHttpRequestFactory requestFactory;


	/**
	 * Create a {@code AbstractClientHttpRequestFactoryWrapper} wrapping the given request factory.
	 * <p>
	 *  创建包装给定请求工厂的{@code AbstractClientHttpRequestFactoryWrapper}
	 * 
	 * 
	 * @param requestFactory the request factory to be wrapped
	 */
	protected AbstractClientHttpRequestFactoryWrapper(ClientHttpRequestFactory requestFactory) {
		Assert.notNull(requestFactory, "ClientHttpRequestFactory must not be null");
		this.requestFactory = requestFactory;
	}


	/**
	 * This implementation simply calls {@link #createRequest(URI, HttpMethod, ClientHttpRequestFactory)}
	 * with the wrapped request factory provided to the
	 * {@linkplain #AbstractClientHttpRequestFactoryWrapper(ClientHttpRequestFactory) constructor}.
	 * <p>
	 * 该实现简单地将包装的请求工厂提供给{@linkplain #AbstractClientHttpRequestFactoryWrapper(ClientHttpRequestFactory)构造函数)的{@link #createRequest(URI,HttpMethod,ClientHttpRequestFactory)}
	 * )。
	 * 
	 */
	@Override
	public final ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
		return createRequest(uri, httpMethod, this.requestFactory);
	}

	/**
	 * Create a new {@link ClientHttpRequest} for the specified URI and HTTP method
	 * by using the passed-on request factory.
	 * <p>Called from {@link #createRequest(URI, HttpMethod)}.
	 * <p>
	 * 
	 * @param uri the URI to create a request for
	 * @param httpMethod the HTTP method to execute
	 * @param requestFactory the wrapped request factory
	 * @return the created request
	 * @throws IOException in case of I/O errors
	 */
	protected abstract ClientHttpRequest createRequest(
			URI uri, HttpMethod httpMethod, ClientHttpRequestFactory requestFactory) throws IOException;

}
