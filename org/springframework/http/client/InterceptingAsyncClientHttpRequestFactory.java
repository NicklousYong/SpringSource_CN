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

package org.springframework.http.client;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpMethod;

/**
 * Wrapper for a {@link AsyncClientHttpRequestFactory} that has support for
 * {@link AsyncClientHttpRequestInterceptor}s.
 *
 * <p>
 * 
 * 
 * @author Jakub Narloch
 * @since 4.3
 * @see InterceptingAsyncClientHttpRequest
 */
public class InterceptingAsyncClientHttpRequestFactory implements AsyncClientHttpRequestFactory {

	private AsyncClientHttpRequestFactory delegate;

	private List<AsyncClientHttpRequestInterceptor> interceptors;


	/**
	 * Create new instance of {@link InterceptingAsyncClientHttpRequestFactory}
	 * with delegated request factory and list of interceptors.
	 * <p>
	 *  支持{@link AsyncClientHttpRequestInterceptor}的{@link AsyncClientHttpRequestFactory}的包装器
	 * 
	 * 
	 * @param delegate the request factory to delegate to
	 * @param interceptors the list of interceptors to use
	 */
	public InterceptingAsyncClientHttpRequestFactory(AsyncClientHttpRequestFactory delegate,
			List<AsyncClientHttpRequestInterceptor> interceptors) {

		this.delegate = delegate;
		this.interceptors = (interceptors != null ? interceptors : Collections.<AsyncClientHttpRequestInterceptor>emptyList());
	}


	@Override
	public AsyncClientHttpRequest createAsyncRequest(URI uri, HttpMethod method) {
		return new InterceptingAsyncClientHttpRequest(this.delegate, this.interceptors, uri, method);
	}

}
