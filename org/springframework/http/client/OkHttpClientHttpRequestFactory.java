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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * {@link ClientHttpRequestFactory} implementation that uses
 * <a href="http://square.github.io/okhttp/">OkHttp</a> 2.x to create requests.
 *
 * <p>
 *  {@link ClientHttpRequestFactory}实现,使用<a href=\"http://squaregithubio/okhttp/\"> OkHttp </a> 2x创建请求
 * 
 * 
 * @author Luciano Leggieri
 * @author Arjen Poutsma
 * @since 4.2
 * @see org.springframework.http.client.OkHttp3ClientHttpRequestFactory
 */
public class OkHttpClientHttpRequestFactory
		implements ClientHttpRequestFactory, AsyncClientHttpRequestFactory, DisposableBean {

	private final OkHttpClient client;

	private final boolean defaultClient;


	/**
	 * Create a factory with a default {@link OkHttpClient} instance.
	 * <p>
	 *  使用默认的{@link OkHttpClient}实例创建工厂
	 * 
	 */
	public OkHttpClientHttpRequestFactory() {
		this.client = new OkHttpClient();
		this.defaultClient = true;
	}

	/**
	 * Create a factory with the given {@link OkHttpClient} instance.
	 * <p>
	 * 使用给定的{@link OkHttpClient}实例创建工厂
	 * 
	 * 
	 * @param client the client to use
	 */
	public OkHttpClientHttpRequestFactory(OkHttpClient client) {
		Assert.notNull(client, "OkHttpClient must not be null");
		this.client = client;
		this.defaultClient = false;
	}


	/**
	 * Sets the underlying read timeout in milliseconds.
	 * A value of 0 specifies an infinite timeout.
	 * <p>
	 *  设置底层读取超时(以毫秒为单位)值0指定无限超时
	 * 
	 * 
	 * @see OkHttpClient#setReadTimeout(long, TimeUnit)
	 */
	public void setReadTimeout(int readTimeout) {
		this.client.setReadTimeout(readTimeout, TimeUnit.MILLISECONDS);
	}

	/**
	 * Sets the underlying write timeout in milliseconds.
	 * A value of 0 specifies an infinite timeout.
	 * <p>
	 *  设置底层写入超时(以毫秒为单位)值0指定无限超时
	 * 
	 * 
	 * @see OkHttpClient#setWriteTimeout(long, TimeUnit)
	 */
	public void setWriteTimeout(int writeTimeout) {
		this.client.setWriteTimeout(writeTimeout, TimeUnit.MILLISECONDS);
	}

	/**
	 * Sets the underlying connect timeout in milliseconds.
	 * A value of 0 specifies an infinite timeout.
	 * <p>
	 *  设置底层连接超时(以毫秒为单位)值0指定无限超时
	 * 
	 * @see OkHttpClient#setConnectTimeout(long, TimeUnit)
	 */
	public void setConnectTimeout(int connectTimeout) {
		this.client.setConnectTimeout(connectTimeout, TimeUnit.MILLISECONDS);
	}


	@Override
	public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) {
		return new OkHttpClientHttpRequest(this.client, uri, httpMethod);
	}

	@Override
	public AsyncClientHttpRequest createAsyncRequest(URI uri, HttpMethod httpMethod) {
		return new OkHttpAsyncClientHttpRequest(this.client, uri, httpMethod);
	}


	@Override
	public void destroy() throws IOException {
		if (this.defaultClient) {
			// Clean up the client if we created it in the constructor
			if (this.client.getCache() != null) {
				this.client.getCache().close();
			}
			this.client.getDispatcher().getExecutorService().shutdown();
		}
	}


	static Request buildRequest(HttpHeaders headers, byte[] content, URI uri,
			HttpMethod method) throws MalformedURLException {

		com.squareup.okhttp.MediaType contentType = getContentType(headers);
		RequestBody body = (content.length > 0 ? RequestBody.create(contentType, content) : null);

		URL url = uri.toURL();
		String methodName = method.name();
		Request.Builder builder = new Request.Builder().url(url).method(methodName, body);

		for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
			String headerName = entry.getKey();
			for (String headerValue : entry.getValue()) {
				builder.addHeader(headerName, headerValue);
			}
		}

		return builder.build();
	}

	private static com.squareup.okhttp.MediaType getContentType(HttpHeaders headers) {
		String rawContentType = headers.getFirst(HttpHeaders.CONTENT_TYPE);
		return (StringUtils.hasText(rawContentType) ?
				com.squareup.okhttp.MediaType.parse(rawContentType) : null);
	}

}
