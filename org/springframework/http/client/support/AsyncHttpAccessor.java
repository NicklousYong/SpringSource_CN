/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2013 the original author or authors.
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

package org.springframework.http.client.support;

import java.io.IOException;
import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.AsyncClientHttpRequest;
import org.springframework.http.client.AsyncClientHttpRequestFactory;
import org.springframework.util.Assert;

/**
 * Base class for {@link org.springframework.web.client.AsyncRestTemplate}
 * and other HTTP accessing gateway helpers, defining common properties
 * such as the {@link AsyncClientHttpRequestFactory} to operate on.
 *
 * <p>Not intended to be used directly. See
 * {@link org.springframework.web.client.AsyncRestTemplate}.
 *
 * <p>
 *  用于{@link orgspringframeworkwebclientAsyncRestTemplate}和其他HTTP访问网关助手的基类,定义常用属性,例如{@link AsyncClientHttpRequestFactory}
 * 来操作。
 * 
 * <p>不打算直接使用请参阅{@link orgspringframeworkwebclientAsyncRestTemplate}
 * 
 * 
 * @author Arjen Poutsma
 * @since 4.0
 * @see org.springframework.web.client.AsyncRestTemplate
 */
public class AsyncHttpAccessor {

	/** Logger available to subclasses. */
	protected final Log logger = LogFactory.getLog(getClass());

	private AsyncClientHttpRequestFactory asyncRequestFactory;

	/**
	 * Set the request factory that this accessor uses for obtaining {@link
	 * org.springframework.http.client.ClientHttpRequest HttpRequests}.
	 * <p>
	 *  设置此访问者用于获取{@link orgspringframeworkhttpclientClientHttpRequest HttpRequests}的请求工厂
	 * 
	 */
	public void setAsyncRequestFactory(AsyncClientHttpRequestFactory asyncRequestFactory) {
		Assert.notNull(asyncRequestFactory, "'asyncRequestFactory' must not be null");
		this.asyncRequestFactory = asyncRequestFactory;
	}

	/**
	 * Return the request factory that this accessor uses for obtaining {@link
	 * org.springframework.http.client.ClientHttpRequest HttpRequests}.
	 * <p>
	 *  返回此访问者用于获取{@link orgspringframeworkhttpclientClientHttpRequest HttpRequests}的请求工厂
	 * 
	 */
	public AsyncClientHttpRequestFactory getAsyncRequestFactory() {
		return this.asyncRequestFactory;
	}

	/**
	 * Create a new {@link AsyncClientHttpRequest} via this template's {@link
	 * AsyncClientHttpRequestFactory}.
	 * <p>
	 *  通过此模板的{@link AsyncClientHttpRequestFactory}创建一个新的{@link AsyncClientHttpRequest}
	 * 
	 * @param url the URL to connect to
	 * @param method the HTTP method to execute (GET, POST, etc.)
	 * @return the created request
	 * @throws IOException in case of I/O errors
	 */
	protected AsyncClientHttpRequest createAsyncRequest(URI url, HttpMethod method)
			throws IOException {
		AsyncClientHttpRequest request = getAsyncRequestFactory().createAsyncRequest(url, method);
		if (logger.isDebugEnabled()) {
			logger.debug("Created asynchronous " + method.name() + " request for \"" + url + "\"");
		}
		return request;
	}

}
