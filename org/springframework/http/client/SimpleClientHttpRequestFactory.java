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
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;

/**
 * {@link ClientHttpRequestFactory} implementation that uses standard JDK facilities.
 *
 * <p>
 *  {@link ClientHttpRequestFactory}实现使用标准的JDK功能
 * 
 * 
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @since 3.0
 * @see java.net.HttpURLConnection
 * @see HttpComponentsClientHttpRequestFactory
 */
public class SimpleClientHttpRequestFactory implements ClientHttpRequestFactory, AsyncClientHttpRequestFactory {

	private static final int DEFAULT_CHUNK_SIZE = 4096;


	private Proxy proxy;

	private boolean bufferRequestBody = true;

	private int chunkSize = DEFAULT_CHUNK_SIZE;

	private int connectTimeout = -1;

	private int readTimeout = -1;

	private boolean outputStreaming = true;

	private AsyncListenableTaskExecutor taskExecutor;


	/**
	 * Set the {@link Proxy} to use for this request factory.
	 * <p>
	 *  将{@link Proxy}设置为用于此请求工厂
	 * 
	 */
	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
	}

	/**
	 * Indicates whether this request factory should buffer the {@linkplain ClientHttpRequest#getBody() request body}
	 * internally.
	 * <p>Default is {@code true}. When sending large amounts of data via POST or PUT, it is recommended
	 * to change this property to {@code false}, so as not to run out of memory. This will result in a
	 * {@link ClientHttpRequest} that either streams directly to the underlying {@link HttpURLConnection}
	 * (if the {@link org.springframework.http.HttpHeaders#getContentLength() Content-Length} is known in advance),
	 * or that will use "Chunked transfer encoding" (if the {@code Content-Length} is not known in advance).
	 * <p>
	 * 指示此请求工厂是否应缓冲{@linkplain ClientHttpRequest#getBody()请求正文}内部<p>默认值为{@code true}通过POST或PUT发送大量数据时,建议将此属性
	 * 更改为{ @code false},以免不用内存这将导致{@link ClientHttpRequest}直接流向底层的{@link HttpURLConnection}(如果{@link orgspringframeworkhttpHttpHeaders#getContentLength()Content-Length}
	 * 提前知道),或者将使用"分块传输编码"(如果{@code Content-Length}未提前知道)。
	 * 
	 * 
	 * @see #setChunkSize(int)
	 * @see HttpURLConnection#setFixedLengthStreamingMode(int)
	 */
	public void setBufferRequestBody(boolean bufferRequestBody) {
		this.bufferRequestBody = bufferRequestBody;
	}

	/**
	 * Sets the number of bytes to write in each chunk when not buffering request bodies locally.
	 * <p>Note that this parameter is only used when {@link #setBufferRequestBody(boolean) bufferRequestBody} is set
	 * to {@code false}, and the {@link org.springframework.http.HttpHeaders#getContentLength() Content-Length}
	 * is not known in advance.
	 * <p>
	 * 设置不在本地缓存请求体时在每个块中写入的字节数<p>请注意,只有当{@link #setBufferRequestBody(boolean)bufferRequestBody}设置为{@code false}
	 * 时,才使用此参数,而{@ link orgspringframeworkhttpHttpHeaders#getContentLength()Content-Length}提前不知道。
	 * 
	 * 
	 * @see #setBufferRequestBody(boolean)
	 */
	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}

	/**
	 * Set the underlying URLConnection's connect timeout (in milliseconds).
	 * A timeout value of 0 specifies an infinite timeout.
	 * <p>Default is the system's default timeout.
	 * <p>
	 *  设置基本的URLConnection的连接超时(以毫秒为单位)超时值为0指定无限超时<p>默认是系统的默认超时
	 * 
	 * 
	 * @see URLConnection#setConnectTimeout(int)
	 */
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	/**
	 * Set the underlying URLConnection's read timeout (in milliseconds).
	 * A timeout value of 0 specifies an infinite timeout.
	 * <p>Default is the system's default timeout.
	 * <p>
	 *  设置底层URLConnection的读取超时(以毫秒为单位)超时值为0指定无限超时<p>默认值是系统的默认超时
	 * 
	 * 
	 * @see URLConnection#setReadTimeout(int)
	 */
	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	/**
	 * Set if the underlying URLConnection can be set to 'output streaming' mode.
	 * Default is {@code true}.
	 * <p>When output streaming is enabled, authentication and redirection cannot be handled automatically.
	 * If output streaming is disabled, the {@link HttpURLConnection#setFixedLengthStreamingMode} and
	 * {@link HttpURLConnection#setChunkedStreamingMode} methods of the underlying connection will never
	 * be called.
	 * <p>
	 * 设置底层URLConnection是否可以设置为"输出流"模式默认值为{@code true} <p>当启用输出流时,无法自动处理身份验证和重定向如果禁用输出流,则{@link HttpURLConnection#setFixedLengthStreamingMode }
	 * 和底层连接的{@link HttpURLConnection#setChunkedStreamingMode}方法将永远不会被调用。
	 * 
	 * 
	 * @param outputStreaming if output streaming is enabled
	 */
	public void setOutputStreaming(boolean outputStreaming) {
		this.outputStreaming = outputStreaming;
	}

	/**
	 * Set the task executor for this request factory. Setting this property is required
	 * for {@linkplain #createAsyncRequest(URI, HttpMethod) creating asynchronous requests}.
	 * <p>
	 *  设置此请求工厂的任务执行程序设置此属性是{@linkplain #createAsyncRequest(URI,HttpMethod)创建异步请求)所必需的}
	 * 
	 * 
	 * @param taskExecutor the task executor
	 */
	public void setTaskExecutor(AsyncListenableTaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}


	@Override
	public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
		HttpURLConnection connection = openConnection(uri.toURL(), this.proxy);
		prepareConnection(connection, httpMethod.name());

		if (this.bufferRequestBody) {
			return new SimpleBufferingClientHttpRequest(connection, this.outputStreaming);
		}
		else {
			return new SimpleStreamingClientHttpRequest(connection, this.chunkSize, this.outputStreaming);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>Setting the {@link #setTaskExecutor taskExecutor} property is required before calling this method.
	 * <p>
	 *  {@inheritDoc} <p>在调用此方法之前,需要设置{@link #setTaskExecutor taskExecutor}属性
	 * 
	 */
	@Override
	public AsyncClientHttpRequest createAsyncRequest(URI uri, HttpMethod httpMethod) throws IOException {
		Assert.state(this.taskExecutor != null,
				"Asynchronous execution requires an AsyncTaskExecutor to be set");

		HttpURLConnection connection = openConnection(uri.toURL(), this.proxy);
		prepareConnection(connection, httpMethod.name());

		if (this.bufferRequestBody) {
			return new SimpleBufferingAsyncClientHttpRequest(
					connection, this.outputStreaming, this.taskExecutor);
		}
		else {
			return new SimpleStreamingAsyncClientHttpRequest(
					connection, this.chunkSize, this.outputStreaming, this.taskExecutor);
		}
	}

	/**
	 * Opens and returns a connection to the given URL.
	 * <p>The default implementation uses the given {@linkplain #setProxy(java.net.Proxy) proxy} -
	 * if any - to open a connection.
	 * <p>
	 * 打开并返回与给定URL的连接<p>默认实现使用给定的{@linkplain #setProxy(javanetProxy)proxy}  - 如果有的话)打开连接
	 * 
	 * 
	 * @param url the URL to open a connection to
	 * @param proxy the proxy to use, may be {@code null}
	 * @return the opened connection
	 * @throws IOException in case of I/O errors
	 */
	protected HttpURLConnection openConnection(URL url, Proxy proxy) throws IOException {
		URLConnection urlConnection = (proxy != null ? url.openConnection(proxy) : url.openConnection());
		Assert.isInstanceOf(HttpURLConnection.class, urlConnection);
		return (HttpURLConnection) urlConnection;
	}

	/**
	 * Template method for preparing the given {@link HttpURLConnection}.
	 * <p>The default implementation prepares the connection for input and output, and sets the HTTP method.
	 * <p>
	 *  用于准备给定的{@link HttpURLConnection}的模板方法<p>默认实现为输入和输出准备连接,并设置HTTP方法
	 * 
	 * @param connection the connection to prepare
	 * @param httpMethod the HTTP request method ({@code GET}, {@code POST}, etc.)
	 * @throws IOException in case of I/O errors
	 */
	protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
		if (this.connectTimeout >= 0) {
			connection.setConnectTimeout(this.connectTimeout);
		}
		if (this.readTimeout >= 0) {
			connection.setReadTimeout(this.readTimeout);
		}

		connection.setDoInput(true);

		if ("GET".equals(httpMethod)) {
			connection.setInstanceFollowRedirects(true);
		}
		else {
			connection.setInstanceFollowRedirects(false);
		}

		if ("POST".equals(httpMethod) || "PUT".equals(httpMethod) ||
				"PATCH".equals(httpMethod) || "DELETE".equals(httpMethod)) {
			connection.setDoOutput(true);
		}
		else {
			connection.setDoOutput(false);
		}

		connection.setRequestMethod(httpMethod);
	}

}
