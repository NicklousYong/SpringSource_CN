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

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.Configurable;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * {@link org.springframework.http.client.ClientHttpRequestFactory} implementation that
 * uses <a href="http://hc.apache.org/httpcomponents-client-ga/">Apache HttpComponents
 * HttpClient</a> to create requests.
 *
 * <p>Allows to use a pre-configured {@link HttpClient} instance -
 * potentially with authentication, HTTP connection pooling, etc.
 *
 * <p><b>NOTE:</b> Requires Apache HttpComponents 4.3 or higher, as of Spring 4.0.
 *
 * <p>
 *  使用<a href=\"http://hcapacheorg/httpcomponents-client-ga/\"> Apache HttpComponents HttpClient </a>创建请
 * 求的{@link orgspringframeworkhttpclientClientHttpRequestFactory}实现。
 * 
 * <p>允许使用预配置的{@link HttpClient}实例 - 潜在的身份验证,HTTP连接池等
 * 
 *  <p> <b>注意：</b>自Spring 40起,需要Apache HttpComponents 43或更高版本
 * 
 * 
 * @author Oleg Kalnichevski
 * @author Arjen Poutsma
 * @author Stephane Nicoll
 * @author Juergen Hoeller
 * @since 3.1
 */
public class HttpComponentsClientHttpRequestFactory implements ClientHttpRequestFactory, DisposableBean {

	private static Class<?> abstractHttpClientClass;

	static {
		try {
			// Looking for AbstractHttpClient class (deprecated as of HttpComponents 4.3)
			abstractHttpClientClass = ClassUtils.forName("org.apache.http.impl.client.AbstractHttpClient",
					HttpComponentsClientHttpRequestFactory.class.getClassLoader());
		}
		catch (ClassNotFoundException ex) {
			// Probably removed from HttpComponents in the meantime...
		}
	}


	private HttpClient httpClient;

	private RequestConfig requestConfig;

	private boolean bufferRequestBody = true;


	/**
	 * Create a new instance of the {@code HttpComponentsClientHttpRequestFactory}
	 * with a default {@link HttpClient}.
	 * <p>
	 *  使用默认{@link HttpClient}创建{@code HttpComponentsClientHttpRequestFactory}的新实例
	 * 
	 */
	public HttpComponentsClientHttpRequestFactory() {
		this(HttpClients.createSystem());
	}

	/**
	 * Create a new instance of the {@code HttpComponentsClientHttpRequestFactory}
	 * with the given {@link HttpClient} instance.
	 * <p>
	 *  使用给定的{@link HttpClient}实例创建{@code HttpComponentsClientHttpRequestFactory}的新实例
	 * 
	 * 
	 * @param httpClient the HttpClient instance to use for this request factory
	 */
	public HttpComponentsClientHttpRequestFactory(HttpClient httpClient) {
		Assert.notNull(httpClient, "HttpClient must not be null");
		this.httpClient = httpClient;
	}


	/**
	 * Set the {@code HttpClient} used for
	 * {@linkplain #createRequest(URI, HttpMethod) synchronous execution}.
	 * <p>
	 *  设置用于{@linkplain #createRequest(URI,HttpMethod)同步执行)的{@code HttpClient}
	 * 
	 */
	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	/**
	 * Return the {@code HttpClient} used for
	 * {@linkplain #createRequest(URI, HttpMethod) synchronous execution}.
	 * <p>
	 *  返回用于{@linkplain #createRequest(URI,HttpMethod)同步执行的{@code HttpClient}}
	 * 
	 */
	public HttpClient getHttpClient() {
		return this.httpClient;
	}

	/**
	 * Set the connection timeout for the underlying HttpClient.
	 * A timeout value of 0 specifies an infinite timeout.
	 * <p>Additional properties can be configured by specifying a
	 * {@link RequestConfig} instance on a custom {@link HttpClient}.
	 * <p>
	 * 设置基础HttpClient的连接超时值超时值为0指定无限超时<p>可以通过在自定义{@link HttpClient}上指定{@link RequestConfig}实例来配置其他属性,
	 * 
	 * 
	 * @param timeout the timeout value in milliseconds
	 * @see RequestConfig#getConnectTimeout()
	 */
	public void setConnectTimeout(int timeout) {
		Assert.isTrue(timeout >= 0, "Timeout must be a non-negative value");
		this.requestConfig = requestConfigBuilder().setConnectTimeout(timeout).build();
		setLegacyConnectionTimeout(getHttpClient(), timeout);
	}

	/**
	 * Apply the specified connection timeout to deprecated {@link HttpClient}
	 * implementations.
	 * <p>As of HttpClient 4.3, default parameters have to be exposed through a
	 * {@link RequestConfig} instance instead of setting the parameters on the
	 * client. Unfortunately, this behavior is not backward-compatible and older
	 * {@link HttpClient} implementations will ignore the {@link RequestConfig}
	 * object set in the context.
	 * <p>If the specified client is an older implementation, we set the custom
	 * connection timeout through the deprecated API. Otherwise, we just return
	 * as it is set through {@link RequestConfig} with newer clients.
	 * <p>
	 * 将指定的连接超时应用于已弃用的{@link HttpClient}实现<p>从HttpClient 43开始,默认参数必须通过{@link RequestConfig}实例进行公开,而不是在客户端上设置
	 * 参数不幸的是,此行为不是向后兼容和旧的{@link HttpClient}实现将忽略上下文<p>中的{@link RequestConfig}对象集合如果指定的客户端是较旧的实现,则将自定义连接超时设置
	 * 为不推荐的API否则,我们刚刚返回因为它是通过{@link RequestConfig}与较新的客户端设置的。
	 * 
	 * 
	 * @param client the client to configure
	 * @param timeout the custom connection timeout
	 */
	@SuppressWarnings("deprecation")
	private void setLegacyConnectionTimeout(HttpClient client, int timeout) {
		if (abstractHttpClientClass != null && abstractHttpClientClass.isInstance(client)) {
			client.getParams().setIntParameter(org.apache.http.params.CoreConnectionPNames.CONNECTION_TIMEOUT, timeout);
		}
	}

	/**
	 * Set the timeout in milliseconds used when requesting a connection from the connection
	 * manager using the underlying HttpClient.
	 * A timeout value of 0 specifies an infinite timeout.
	 * <p>Additional properties can be configured by specifying a
	 * {@link RequestConfig} instance on a custom {@link HttpClient}.
	 * <p>
	 * 设置从使用底层HttpClient的连接管理器请求连接时使用的超时(以毫秒为单位)超时值为0指定无限超时<p>可以通过在自定义{@link上指定{@link RequestConfig}实例来配置其他属
	 * 性HttpClient的}。
	 * 
	 * 
	 * @param connectionRequestTimeout the timeout value to request a connection in milliseconds
	 * @see RequestConfig#getConnectionRequestTimeout()
	 */
	public void setConnectionRequestTimeout(int connectionRequestTimeout) {
		this.requestConfig = requestConfigBuilder().setConnectionRequestTimeout(connectionRequestTimeout).build();
	}

	/**
	 * Set the socket read timeout for the underlying HttpClient.
	 * A timeout value of 0 specifies an infinite timeout.
	 * <p>Additional properties can be configured by specifying a
	 * {@link RequestConfig} instance on a custom {@link HttpClient}.
	 * <p>
	 *  设置基础HttpClient的套接字读取超时值超时值0指定无限超时<p>可以通过在自定义{@link HttpClient}上指定{@link RequestConfig}实例来配置其他属性,
	 * 
	 * 
	 * @param timeout the timeout value in milliseconds
	 * @see RequestConfig#getSocketTimeout()
	 */
	public void setReadTimeout(int timeout) {
		Assert.isTrue(timeout >= 0, "Timeout must be a non-negative value");
		this.requestConfig = requestConfigBuilder().setSocketTimeout(timeout).build();
		setLegacySocketTimeout(getHttpClient(), timeout);
	}

	/**
	 * Apply the specified socket timeout to deprecated {@link HttpClient}
	 * implementations. See {@link #setLegacyConnectionTimeout}.
	 * <p>
	 *  将指定的套接字超时应用于已弃用的{@link HttpClient}实现请参见{@link #setLegacyConnectionTimeout}
	 * 
	 * 
	 * @param client the client to configure
	 * @param timeout the custom socket timeout
	 * @see #setLegacyConnectionTimeout
	 */
	@SuppressWarnings("deprecation")
	private void setLegacySocketTimeout(HttpClient client, int timeout) {
		if (abstractHttpClientClass != null && abstractHttpClientClass.isInstance(client)) {
			client.getParams().setIntParameter(org.apache.http.params.CoreConnectionPNames.SO_TIMEOUT, timeout);
		}
	}

	/**
	 * Indicates whether this request factory should buffer the request body internally.
	 * <p>Default is {@code true}. When sending large amounts of data via POST or PUT, it is
	 * recommended to change this property to {@code false}, so as not to run out of memory.
	 * <p>
	 * 指示此请求工厂是否应该在内部缓冲请求主体。<p>默认值为{@code true}通过POST或PUT发送大量数据时,建议将此属性更改为{@code false},以免内存不足
	 * 
	 */
	public void setBufferRequestBody(boolean bufferRequestBody) {
		this.bufferRequestBody = bufferRequestBody;
	}


	@Override
	public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
		HttpClient client = getHttpClient();
		Assert.state(client != null, "Synchronous execution requires an HttpClient to be set");

		HttpUriRequest httpRequest = createHttpUriRequest(httpMethod, uri);
		postProcessHttpRequest(httpRequest);
		HttpContext context = createHttpContext(httpMethod, uri);
		if (context == null) {
			context = HttpClientContext.create();
		}

		// Request configuration not set in the context
		if (context.getAttribute(HttpClientContext.REQUEST_CONFIG) == null) {
			// Use request configuration given by the user, when available
			RequestConfig config = null;
			if (httpRequest instanceof Configurable) {
				config = ((Configurable) httpRequest).getConfig();
			}
			if (config == null) {
				config = createRequestConfig(client);
			}
			if (config != null) {
				context.setAttribute(HttpClientContext.REQUEST_CONFIG, config);
			}
		}

		if (this.bufferRequestBody) {
			return new HttpComponentsClientHttpRequest(client, httpRequest, context);
		}
		else {
			return new HttpComponentsStreamingClientHttpRequest(client, httpRequest, context);
		}
	}


	/**
	 * Return a builder for modifying the factory-level {@link RequestConfig}.
	 * <p>
	 *  返回一个修改工厂级{@link RequestConfig}的构建器
	 * 
	 * 
	 * @since 4.2
	 */
	private RequestConfig.Builder requestConfigBuilder() {
		return (this.requestConfig != null ? RequestConfig.copy(this.requestConfig) : RequestConfig.custom());
	}

	/**
	 * Create a default {@link RequestConfig} to use with the given client.
	 * Can return {@code null} to indicate that no custom request config should
	 * be set and the defaults of the {@link HttpClient} should be used.
	 * <p>The default implementation tries to merge the defaults of the client
	 * with the local customizations of this factory instance, if any.
	 * <p>
	 *  创建与给定客户端一起使用的默认{@link RequestConfig}可以返回{@code null}以指示不应设置自定义请求配置,并且应使用默认的{@link HttpClient} <p>默认实
	 * 现尝试将客户端的默认值与此工厂实例的本地自定义(如果有)合并。
	 * 
	 * 
	 * @param client the {@link HttpClient} (or {@code HttpAsyncClient}) to check
	 * @return the actual RequestConfig to use (may be {@code null})
	 * @since 4.2
	 * @see #mergeRequestConfig(RequestConfig)
	 */
	protected RequestConfig createRequestConfig(Object client) {
		if (client instanceof Configurable) {
			RequestConfig clientRequestConfig = ((Configurable) client).getConfig();
			return mergeRequestConfig(clientRequestConfig);
		}
		return this.requestConfig;
	}

	/**
	 * Merge the given {@link HttpClient}-level {@link RequestConfig} with
	 * the factory-level {@link RequestConfig}, if necessary.
	 * <p>
	 *  如果需要,将给定的{@link HttpClient}级{@link RequestConfig}与工厂级{@link RequestConfig}合并
	 * 
	 * 
	 * @param clientConfig the config held by the current
	 * @return the merged request config
	 * (may be {@code null} if the given client config is {@code null})
	 * @since 4.2
	 */
	protected RequestConfig mergeRequestConfig(RequestConfig clientConfig) {
		if (this.requestConfig == null) {  // nothing to merge
			return clientConfig;
		}

		RequestConfig.Builder builder = RequestConfig.copy(clientConfig);
		int connectTimeout = this.requestConfig.getConnectTimeout();
		if (connectTimeout >= 0) {
			builder.setConnectTimeout(connectTimeout);
		}
		int connectionRequestTimeout = this.requestConfig.getConnectionRequestTimeout();
		if (connectionRequestTimeout >= 0) {
			builder.setConnectionRequestTimeout(connectionRequestTimeout);
		}
		int socketTimeout = this.requestConfig.getSocketTimeout();
		if (socketTimeout >= 0) {
			builder.setSocketTimeout(socketTimeout);
		}
		return builder.build();
	}

	/**
	 * Create a Commons HttpMethodBase object for the given HTTP method and URI specification.
	 * <p>
	 * 为给定的HTTP方法和URI规范创建一个Commons HttpMethodBase对象
	 * 
	 * 
	 * @param httpMethod the HTTP method
	 * @param uri the URI
	 * @return the Commons HttpMethodBase object
	 */
	protected HttpUriRequest createHttpUriRequest(HttpMethod httpMethod, URI uri) {
		switch (httpMethod) {
			case GET:
				return new HttpGet(uri);
			case HEAD:
				return new HttpHead(uri);
			case POST:
				return new HttpPost(uri);
			case PUT:
				return new HttpPut(uri);
			case PATCH:
				return new HttpPatch(uri);
			case DELETE:
				return new HttpDelete(uri);
			case OPTIONS:
				return new HttpOptions(uri);
			case TRACE:
				return new HttpTrace(uri);
			default:
				throw new IllegalArgumentException("Invalid HTTP method: " + httpMethod);
		}
	}

	/**
	 * Template method that allows for manipulating the {@link HttpUriRequest} before it is
	 * returned as part of a {@link HttpComponentsClientHttpRequest}.
	 * <p>The default implementation is empty.
	 * <p>
	 *  在{@link HttpComponentsClientHttpRequest}返回之前,允许操作{@link HttpUriRequest}的模板方法<p>默认实现为空
	 * 
	 * 
	 * @param request the request to process
	 */
	protected void postProcessHttpRequest(HttpUriRequest request) {
	}

	/**
	 * Template methods that creates a {@link HttpContext} for the given HTTP method and URI.
	 * <p>The default implementation returns {@code null}.
	 * <p>
	 *  为给定的HTTP方法和URI创建{@link HttpContext}的模板方法默认实现返回{@code null}
	 * 
	 * 
	 * @param httpMethod the HTTP method
	 * @param uri the URI
	 * @return the http context
	 */
	protected HttpContext createHttpContext(HttpMethod httpMethod, URI uri) {
		return null;
	}


	/**
	 * Shutdown hook that closes the underlying
	 * {@link org.apache.http.conn.HttpClientConnectionManager ClientConnectionManager}'s
	 * connection pool, if any.
	 * <p>
	 *  关闭钩子,关闭底层的{@link orgapachehttpconnHttpClientConnectionManager ClientConnectionManager}的连接池,如果有的话
	 * 
	 */
	@Override
	public void destroy() throws Exception {
		if (this.httpClient instanceof Closeable) {
			((Closeable) this.httpClient).close();
		}
	}


	/**
	 * An alternative to {@link org.apache.http.client.methods.HttpDelete} that
	 * extends {@link org.apache.http.client.methods.HttpEntityEnclosingRequestBase}
	 * rather than {@link org.apache.http.client.methods.HttpRequestBase} and
	 * hence allows HTTP delete with a request body. For use with the RestTemplate
	 * exchange methods which allow the combination of HTTP DELETE with entity.
	 * <p>
	 * {@link orgapachehttpclientmethodsHttpDelete}的一种替代方法是扩展{@link orgapachehttpclientmethodsHttpEntityEnclosingRequestBase}
	 * 而不是{@link orgapachehttpclientmethodsHttpRequestBase},因此允许使用请求体进行HTTP删除用于与允许将HTTP DELETE与实体组合的RestTemp
	 * 
	 * @since 4.1.2
	 */
	private static class HttpDelete extends HttpEntityEnclosingRequestBase {

		public HttpDelete(URI uri) {
			super();
			setURI(uri);
		}

		@Override
		public String getMethod() {
			return "DELETE";
		}
	}

}
