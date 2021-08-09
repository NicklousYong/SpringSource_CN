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

package org.springframework.web.client;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.AsyncClientHttpRequest;
import org.springframework.http.client.AsyncClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.client.support.InterceptingAsyncHttpAccessor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureAdapter;
import org.springframework.web.util.AbstractUriTemplateHandler;
import org.springframework.web.util.UriTemplateHandler;

/**
 * <strong>Spring's central class for asynchronous client-side HTTP access.</strong>
 * Exposes similar methods as {@link RestTemplate}, but returns {@link ListenableFuture}
 * wrappers as opposed to concrete results.
 *
 * <p>The {@code AsyncRestTemplate} exposes a synchronous {@link RestTemplate} via the
 * {@link #getRestOperations()} method, and it shares its
 * {@linkplain #setErrorHandler(ResponseErrorHandler) error handler} and
 * {@linkplain #setMessageConverters(List) message converters} with this
 * {@code RestTemplate}.
 *
 * <p><strong>Note:</strong> by default {@code AsyncRestTemplate} relies on
 * standard JDK facilities to establish HTTP connections. You can switch to use
 * a different HTTP library such as Apache HttpComponents, Netty, and OkHttp by
 * using a constructor accepting an {@link AsyncClientHttpRequestFactory}.
 *
 * <p>For more information, please refer to the {@link RestTemplate} API documentation.
 *
 * <p>
 *  <strong> Spring的异步客户端HTTP访问的中心类</strong>类似于{@link RestTemplate}的方法,但返回{@link ListenableFuture}包装器而不是
 * 具体的结果。
 * 
 * <p> {@code AsyncRestTemplate}通过{@link #getRestOperations()}方法公开了一个同步的{@link RestTemplate},它共享其{@linkplain #setErrorHandler(ResponseErrorHandler)错误处理程序}
 * 和{@linkplain #setMessageConverters (列表)消息转换器}与此{@code RestTemplate}。
 * 
 *  <p> <strong>注意：</strong>默认情况下,{@code AsyncRestTemplate}依赖于标准JDK功能来建立HTTP连接您可以通过使用构造函数切换到使用不同的HTTP库,如
 * Apache HttpComponents,Netty和OkHttp接受{@link AsyncClientHttpRequestFactory}。
 * 
 *  <p>有关详细信息,请参阅{@link RestTemplate} API文档
 * 
 * 
 * @author Arjen Poutsma
 * @since 4.0
 * @see RestTemplate
 */
public class AsyncRestTemplate extends InterceptingAsyncHttpAccessor implements AsyncRestOperations {

	private final RestTemplate syncTemplate;


	/**
	 * Create a new instance of the {@code AsyncRestTemplate} using default settings.
	 * <p>This constructor uses a {@link SimpleClientHttpRequestFactory} in combination
	 * with a {@link SimpleAsyncTaskExecutor} for asynchronous execution.
	 * <p>
	 * 使用默认设置创建{@code AsyncRestTemplate}的新实例<p>此构造函数与{@link SimpleAsyncTaskExecutor}组合使用{@link SimpleClientHttpRequestFactory}
	 * 进行异步执行。
	 * 
	 */
	public AsyncRestTemplate() {
		this(new SimpleAsyncTaskExecutor());
	}

	/**
	 * Create a new instance of the {@code AsyncRestTemplate} using the given
	 * {@link AsyncTaskExecutor}.
	 * <p>This constructor uses a {@link SimpleClientHttpRequestFactory} in combination
	 * with the given {@code AsyncTaskExecutor} for asynchronous execution.
	 * <p>
	 *  使用给定的{@link AsyncTaskExecutor}创建{@code AsyncRestTemplate}的新实例此构造函数与给定的{@code AsyncTaskExecutor}组合使用{@link SimpleClientHttpRequestFactory}
	 * 进行异步执行。
	 * 
	 */
	public AsyncRestTemplate(AsyncListenableTaskExecutor taskExecutor) {
		Assert.notNull(taskExecutor, "AsyncTaskExecutor must not be null");
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		requestFactory.setTaskExecutor(taskExecutor);
		this.syncTemplate = new RestTemplate(requestFactory);
		setAsyncRequestFactory(requestFactory);
	}

	/**
	 * Create a new instance of the {@code AsyncRestTemplate} using the given
	 * {@link AsyncClientHttpRequestFactory}.
	 * <p>This constructor will cast the given asynchronous
	 * {@code AsyncClientHttpRequestFactory} to a {@link ClientHttpRequestFactory}. Since
	 * all implementations of {@code ClientHttpRequestFactory} provided in Spring also
	 * implement {@code AsyncClientHttpRequestFactory}, this should not result in a
	 * {@code ClassCastException}.
	 * <p>
	 * 使用给定的{@link AsyncClientHttpRequestFactory}创建{@code AsyncRestTemplate}的新实例<p>此构造函数会将给定的异步{@code AsyncClientHttpRequestFactory}
	 * 转换为{@link ClientHttpRequestFactory}。
	 * 由于{@code ClientHttpRequestFactory}的所有实现Spring提供的还实现了{@code AsyncClientHttpRequestFactory},这不应该导致{@code ClassCastException}
	 * 。
	 * 
	 */
	public AsyncRestTemplate(AsyncClientHttpRequestFactory asyncRequestFactory) {
		this(asyncRequestFactory, (ClientHttpRequestFactory) asyncRequestFactory);
	}

	/**
	 * Creates a new instance of the {@code AsyncRestTemplate} using the given
	 * asynchronous and synchronous request factories.
	 * <p>
	 *  使用给定的异步和同步请求工厂创建{@code AsyncRestTemplate}的新实例
	 * 
	 * 
	 * @param asyncRequestFactory the asynchronous request factory
	 * @param syncRequestFactory the synchronous request factory
	 */
	public AsyncRestTemplate(
			AsyncClientHttpRequestFactory asyncRequestFactory, ClientHttpRequestFactory syncRequestFactory) {

		this(asyncRequestFactory, new RestTemplate(syncRequestFactory));
	}

	/**
	 * Create a new instance of the {@code AsyncRestTemplate} using the given
	 * {@link AsyncClientHttpRequestFactory} and synchronous {@link RestTemplate}.
	 * <p>
	 *  使用给定的{@link AsyncClientHttpRequestFactory}和同步{@link RestTemplate}创建{@code AsyncRestTemplate}的新实例
	 * 
	 * 
	 * @param requestFactory the asynchronous request factory to use
	 * @param restTemplate the synchronous template to use
	 */
	public AsyncRestTemplate(AsyncClientHttpRequestFactory requestFactory, RestTemplate restTemplate) {
		Assert.notNull(restTemplate, "RestTemplate must not be null");
		this.syncTemplate = restTemplate;
		setAsyncRequestFactory(requestFactory);
	}


	/**
	 * Set the error handler.
	 * <p>By default, AsyncRestTemplate uses a
	 * {@link org.springframework.web.client.DefaultResponseErrorHandler}.
	 * <p>
	 * 设置错误处理程序<p>默认情况下,AsyncRestTemplate使用{@link orgspringframeworkwebclientDefaultResponseErrorHandler}
	 * 
	 */
	public void setErrorHandler(ResponseErrorHandler errorHandler) {
		this.syncTemplate.setErrorHandler(errorHandler);
	}

	/**
	 * Return the error handler.
	 * <p>
	 *  返回错误处理程序
	 * 
	 */
	public ResponseErrorHandler getErrorHandler() {
		return this.syncTemplate.getErrorHandler();
	}

	/**
	 * Configure default URI variable values. This is a shortcut for:
	 * <pre class="code">
	 * DefaultUriTemplateHandler handler = new DefaultUriTemplateHandler();
	 * handler.setDefaultUriVariables(...);
	 *
	 * AsyncRestTemplate restTemplate = new AsyncRestTemplate();
	 * restTemplate.setUriTemplateHandler(handler);
	 * </pre>
	 * <p>
	 *  配置默认URI变量值这是一个快捷方式：
	 * <pre class="code">
	 *  DefaultUriTemplateHandler handler = new DefaultUriTemplateHandler(); handlersetDefaultUriVariables()
	 * ;。
	 * 
	 *  AsyncRestTemplate restTemplate = new AsyncRestTemplate(); restTemplatesetUriTemplateHandler(处理程序);
	 * </pre>
	 * 
	 * @param defaultUriVariables the default URI variable values
	 * @since 4.3
	 */
	public void setDefaultUriVariables(Map<String, ?> defaultUriVariables) {
		UriTemplateHandler handler = this.syncTemplate.getUriTemplateHandler();
		Assert.isInstanceOf(AbstractUriTemplateHandler.class, handler,
				"Can only use this property in conjunction with a DefaultUriTemplateHandler");
		((AbstractUriTemplateHandler) handler).setDefaultUriVariables(defaultUriVariables);
	}

	/**
	 * This property has the same purpose as the corresponding property on the
	 * {@code RestTemplate}. For more details see
	 * {@link RestTemplate#setUriTemplateHandler}.
	 * <p>
	 *  该属性与{@code RestTemplate}中的相应属性具有相同的目的。有关详细信息,请参阅{@link RestTemplate#setUriTemplateHandler}
	 * 
	 * 
	 * @param handler the URI template handler to use
	 */
	public void setUriTemplateHandler(UriTemplateHandler handler) {
		this.syncTemplate.setUriTemplateHandler(handler);
	}

	/**
	 * Return the configured URI template handler.
	 * <p>
	 *  返回配置的URI模板处理程序
	 * 
	 */
	public UriTemplateHandler getUriTemplateHandler() {
		return this.syncTemplate.getUriTemplateHandler();
	}

	@Override
	public RestOperations getRestOperations() {
		return this.syncTemplate;
	}

	/**
	 * Set the message body converters to use.
	 * <p>These converters are used to convert from and to HTTP requests and responses.
	 * <p>
	 *  将消息体转换器设置为使用<p>这些转换器用于从HTTP请求和响应转换
	 * 
	 */
	public void setMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
		this.syncTemplate.setMessageConverters(messageConverters);
	}

	/**
	 * Return the message body converters.
	 * <p>
	 *  返回消息体转换器
	 * 
	 */
	public List<HttpMessageConverter<?>> getMessageConverters() {
		return this.syncTemplate.getMessageConverters();
	}


	// GET

	@Override
	public <T> ListenableFuture<ResponseEntity<T>> getForEntity(String url, Class<T> responseType, Object... uriVariables)
			throws RestClientException {

		AsyncRequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, HttpMethod.GET, requestCallback, responseExtractor, uriVariables);
	}

	@Override
	public <T> ListenableFuture<ResponseEntity<T>> getForEntity(String url, Class<T> responseType,
			Map<String, ?> urlVariables) throws RestClientException {

		AsyncRequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, HttpMethod.GET, requestCallback, responseExtractor, urlVariables);
	}

	@Override
	public <T> ListenableFuture<ResponseEntity<T>> getForEntity(URI url, Class<T> responseType) throws RestClientException {
		AsyncRequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, HttpMethod.GET, requestCallback, responseExtractor);
	}


	// HEAD

	@Override
	public ListenableFuture<HttpHeaders> headForHeaders(String url, Object... uriVariables) throws RestClientException {
		ResponseExtractor<HttpHeaders> headersExtractor = headersExtractor();
		return execute(url, HttpMethod.HEAD, null, headersExtractor, uriVariables);
	}

	@Override
	public ListenableFuture<HttpHeaders> headForHeaders(String url, Map<String, ?> uriVariables) throws RestClientException {
		ResponseExtractor<HttpHeaders> headersExtractor = headersExtractor();
		return execute(url, HttpMethod.HEAD, null, headersExtractor, uriVariables);
	}

	@Override
	public ListenableFuture<HttpHeaders> headForHeaders(URI url) throws RestClientException {
		ResponseExtractor<HttpHeaders> headersExtractor = headersExtractor();
		return execute(url, HttpMethod.HEAD, null, headersExtractor);
	}


	// POST

	@Override
	public ListenableFuture<URI> postForLocation(String url, HttpEntity<?> request, Object... uriVars)
			throws RestClientException {

		AsyncRequestCallback callback = httpEntityCallback(request);
		ResponseExtractor<HttpHeaders> extractor = headersExtractor();
		ListenableFuture<HttpHeaders> future = execute(url, HttpMethod.POST, callback, extractor, uriVars);
		return adaptToLocationHeader(future);
	}

	@Override
	public ListenableFuture<URI> postForLocation(String url, HttpEntity<?> request, Map<String, ?> uriVars)
			throws RestClientException {

		AsyncRequestCallback callback = httpEntityCallback(request);
		ResponseExtractor<HttpHeaders> extractor = headersExtractor();
		ListenableFuture<HttpHeaders> future = execute(url, HttpMethod.POST, callback, extractor, uriVars);
		return adaptToLocationHeader(future);
	}

	@Override
	public ListenableFuture<URI> postForLocation(URI url, HttpEntity<?> request) throws RestClientException {
		AsyncRequestCallback callback = httpEntityCallback(request);
		ResponseExtractor<HttpHeaders> extractor = headersExtractor();
		ListenableFuture<HttpHeaders> future = execute(url, HttpMethod.POST, callback, extractor);
		return adaptToLocationHeader(future);
	}

	private static ListenableFuture<URI> adaptToLocationHeader(ListenableFuture<HttpHeaders> future) {
		return new ListenableFutureAdapter<URI, HttpHeaders>(future) {
			@Override
			protected URI adapt(HttpHeaders headers) throws ExecutionException {
				return headers.getLocation();
			}
		};
	}

	@Override
	public <T> ListenableFuture<ResponseEntity<T>> postForEntity(String url, HttpEntity<?> request,
			Class<T> responseType, Object... uriVariables) throws RestClientException {

		AsyncRequestCallback requestCallback = httpEntityCallback(request, responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables);
	}

	@Override
	public <T> ListenableFuture<ResponseEntity<T>> postForEntity(String url, HttpEntity<?> request,
			Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {

		AsyncRequestCallback requestCallback = httpEntityCallback(request, responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables);
	}

	@Override
	public <T> ListenableFuture<ResponseEntity<T>> postForEntity(URI url, HttpEntity<?> request, Class<T> responseType)
			throws RestClientException {

		AsyncRequestCallback requestCallback = httpEntityCallback(request, responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, HttpMethod.POST, requestCallback, responseExtractor);
	}


	// PUT

	@Override
	public ListenableFuture<?> put(String url, HttpEntity<?> request, Object... uriVariables) throws RestClientException {
		AsyncRequestCallback requestCallback = httpEntityCallback(request);
		return execute(url, HttpMethod.PUT, requestCallback, null, uriVariables);
	}

	@Override
	public ListenableFuture<?> put(String url, HttpEntity<?> request, Map<String, ?> uriVariables) throws RestClientException {
		AsyncRequestCallback requestCallback = httpEntityCallback(request);
		return execute(url, HttpMethod.PUT, requestCallback, null, uriVariables);
	}

	@Override
	public ListenableFuture<?> put(URI url, HttpEntity<?> request) throws RestClientException {
		AsyncRequestCallback requestCallback = httpEntityCallback(request);
		return execute(url, HttpMethod.PUT, requestCallback, null);
	}


	// DELETE

	@Override
	public ListenableFuture<?> delete(String url, Object... urlVariables) throws RestClientException {
		return execute(url, HttpMethod.DELETE, null, null, urlVariables);
	}

	@Override
	public ListenableFuture<?> delete(String url, Map<String, ?> urlVariables) throws RestClientException {
		return execute(url, HttpMethod.DELETE, null, null, urlVariables);
	}

	@Override
	public ListenableFuture<?> delete(URI url) throws RestClientException {
		return execute(url, HttpMethod.DELETE, null, null);
	}


	// OPTIONS

	@Override
	public ListenableFuture<Set<HttpMethod>> optionsForAllow(String url, Object... uriVars) throws RestClientException {
		ResponseExtractor<HttpHeaders> extractor = headersExtractor();
		ListenableFuture<HttpHeaders> future = execute(url, HttpMethod.OPTIONS, null, extractor, uriVars);
		return adaptToAllowHeader(future);
	}

	@Override
	public ListenableFuture<Set<HttpMethod>> optionsForAllow(String url, Map<String, ?> uriVars) throws RestClientException {
		ResponseExtractor<HttpHeaders> extractor = headersExtractor();
		ListenableFuture<HttpHeaders> future = execute(url, HttpMethod.OPTIONS, null, extractor, uriVars);
		return adaptToAllowHeader(future);
	}

	@Override
	public ListenableFuture<Set<HttpMethod>> optionsForAllow(URI url) throws RestClientException {
		ResponseExtractor<HttpHeaders> extractor = headersExtractor();
		ListenableFuture<HttpHeaders> future = execute(url, HttpMethod.OPTIONS, null, extractor);
		return adaptToAllowHeader(future);
	}

	private static ListenableFuture<Set<HttpMethod>> adaptToAllowHeader(ListenableFuture<HttpHeaders> future) {
		return new ListenableFutureAdapter<Set<HttpMethod>, HttpHeaders>(future) {
			@Override
			protected Set<HttpMethod> adapt(HttpHeaders headers) throws ExecutionException {
				return headers.getAllow();
			}
		};
	}

	// exchange

	@Override
	public <T> ListenableFuture<ResponseEntity<T>> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity,
			Class<T> responseType, Object... uriVariables) throws RestClientException {

		AsyncRequestCallback requestCallback = httpEntityCallback(requestEntity, responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, method, requestCallback, responseExtractor, uriVariables);
	}

	@Override
	public <T> ListenableFuture<ResponseEntity<T>> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity,
			Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {

		AsyncRequestCallback requestCallback = httpEntityCallback(requestEntity, responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, method, requestCallback, responseExtractor, uriVariables);
	}

	@Override
	public <T> ListenableFuture<ResponseEntity<T>> exchange(URI url, HttpMethod method, HttpEntity<?> requestEntity,
			Class<T> responseType) throws RestClientException {

		AsyncRequestCallback requestCallback = httpEntityCallback(requestEntity, responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, method, requestCallback, responseExtractor);
	}

	@Override
	public <T> ListenableFuture<ResponseEntity<T>> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity,
			ParameterizedTypeReference<T> responseType, Object... uriVariables) throws RestClientException {

		Type type = responseType.getType();
		AsyncRequestCallback requestCallback = httpEntityCallback(requestEntity, type);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(type);
		return execute(url, method, requestCallback, responseExtractor, uriVariables);
	}

	@Override
	public <T> ListenableFuture<ResponseEntity<T>> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity,
			ParameterizedTypeReference<T> responseType, Map<String, ?> uriVariables) throws RestClientException {

		Type type = responseType.getType();
		AsyncRequestCallback requestCallback = httpEntityCallback(requestEntity, type);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(type);
		return execute(url, method, requestCallback, responseExtractor, uriVariables);
	}

	@Override
	public <T> ListenableFuture<ResponseEntity<T>> exchange(URI url, HttpMethod method, HttpEntity<?> requestEntity,
			ParameterizedTypeReference<T> responseType) throws RestClientException {

		Type type = responseType.getType();
		AsyncRequestCallback requestCallback = httpEntityCallback(requestEntity, type);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(type);
		return execute(url, method, requestCallback, responseExtractor);
	}


	// general execution

	@Override
	public <T> ListenableFuture<T> execute(String url, HttpMethod method, AsyncRequestCallback requestCallback,
			ResponseExtractor<T> responseExtractor, Object... urlVariables) throws RestClientException {

		URI expanded = getUriTemplateHandler().expand(url, urlVariables);
		return doExecute(expanded, method, requestCallback, responseExtractor);
	}

	@Override
	public <T> ListenableFuture<T> execute(String url, HttpMethod method, AsyncRequestCallback requestCallback,
			ResponseExtractor<T> responseExtractor, Map<String, ?> urlVariables) throws RestClientException {

		URI expanded = getUriTemplateHandler().expand(url, urlVariables);
		return doExecute(expanded, method, requestCallback, responseExtractor);
	}

	@Override
	public <T> ListenableFuture<T> execute(URI url, HttpMethod method, AsyncRequestCallback requestCallback,
			ResponseExtractor<T> responseExtractor) throws RestClientException {

		return doExecute(url, method, requestCallback, responseExtractor);
	}

	/**
	 * Execute the given method on the provided URI. The
	 * {@link org.springframework.http.client.ClientHttpRequest}
	 * is processed using the {@link RequestCallback}; the response with
	 * the {@link ResponseExtractor}.
	 * <p>
	 * 在提供的URI上执行给定的方法使用{@link RequestCallback}处理{@link orgspringframeworkhttpclientClientHttpRequest};使用{@link ResponseExtractor}
	 * 的回复。
	 * 
	 * 
	 * @param url the fully-expanded URL to connect to
	 * @param method the HTTP method to execute (GET, POST, etc.)
	 * @param requestCallback object that prepares the request (can be {@code null})
	 * @param responseExtractor object that extracts the return value from the response (can
	 * be {@code null})
	 * @return an arbitrary object, as returned by the {@link ResponseExtractor}
	 */
	protected <T> ListenableFuture<T> doExecute(URI url, HttpMethod method, AsyncRequestCallback requestCallback,
			ResponseExtractor<T> responseExtractor) throws RestClientException {

		Assert.notNull(url, "'url' must not be null");
		Assert.notNull(method, "'method' must not be null");
		try {
			AsyncClientHttpRequest request = createAsyncRequest(url, method);
			if (requestCallback != null) {
				requestCallback.doWithRequest(request);
			}
			ListenableFuture<ClientHttpResponse> responseFuture = request.executeAsync();
			return new ResponseExtractorFuture<T>(method, url, responseFuture, responseExtractor);
		}
		catch (IOException ex) {
			throw new ResourceAccessException("I/O error on " + method.name() +
					" request for \"" + url + "\":" + ex.getMessage(), ex);
		}
	}

	private void logResponseStatus(HttpMethod method, URI url, ClientHttpResponse response) {
		if (logger.isDebugEnabled()) {
			try {
				logger.debug("Async " + method.name() + " request for \"" + url + "\" resulted in " +
						response.getStatusCode() + " (" + response.getStatusText() + ")");
			}
			catch (IOException ex) {
				// ignore
			}
		}
	}

	private void handleResponseError(HttpMethod method, URI url, ClientHttpResponse response) throws IOException {
		if (logger.isWarnEnabled()) {
			try {
				logger.warn("Async " + method.name() + " request for \"" + url + "\" resulted in " +
						response.getStatusCode() + " (" + response.getStatusText() + "); invoking error handler");
			}
			catch (IOException ex) {
				// ignore
			}
		}
		getErrorHandler().handleError(response);
	}

	/**
	 * Returns a request callback implementation that prepares the request {@code Accept}
	 * headers based on the given response type and configured {@linkplain
	 * #getMessageConverters() message converters}.
	 * <p>
	 *  返回一个请求回调实现,它根据给定的响应类型和配置的{@linkplain #getMessageConverters()消息转换器)准备请求{@code Accept}标头}
	 * 
	 */
	protected <T> AsyncRequestCallback acceptHeaderRequestCallback(Class<T> responseType) {
		return new AsyncRequestCallbackAdapter(this.syncTemplate.acceptHeaderRequestCallback(responseType));
	}

	/**
	 * Returns a request callback implementation that writes the given object to the
	 * request stream.
	 * <p>
	 *  返回将给定对象写入请求流的请求回调实现
	 * 
	 */
	protected <T> AsyncRequestCallback httpEntityCallback(HttpEntity<T> requestBody) {
		return new AsyncRequestCallbackAdapter(this.syncTemplate.httpEntityCallback(requestBody));
	}

	/**
	 * Returns a request callback implementation that writes the given object to the
	 * request stream.
	 * <p>
	 *  返回将给定对象写入请求流的请求回调实现
	 * 
	 */
	protected <T> AsyncRequestCallback httpEntityCallback(HttpEntity<T> request, Type responseType) {
		return new AsyncRequestCallbackAdapter(this.syncTemplate.httpEntityCallback(request, responseType));
	}

	/**
	 * Returns a response extractor for {@link ResponseEntity}.
	 * <p>
	 *  返回{@link ResponseEntity}的响应提取器
	 * 
	 */
	protected <T> ResponseExtractor<ResponseEntity<T>> responseEntityExtractor(Type responseType) {
		return this.syncTemplate.responseEntityExtractor(responseType);
	}

	/**
	 * Returns a response extractor for {@link HttpHeaders}.
	 * <p>
	 *  返回{@link HttpHeaders}的响应提取器
	 * 
	 */
	protected ResponseExtractor<HttpHeaders> headersExtractor() {
		return this.syncTemplate.headersExtractor();
	}


	/**
	 * Future returned from
	 * {@link #doExecute(URI, HttpMethod, AsyncRequestCallback, ResponseExtractor)}
	 * <p>
	 * 未来从{@link #doExecute(URI,HttpMethod,AsyncRequestCallback,ResponseExtractor))返回}
	 * 
	 */
	private class ResponseExtractorFuture<T> extends ListenableFutureAdapter<T, ClientHttpResponse> {

		private final HttpMethod method;

		private final URI url;

		private final ResponseExtractor<T> responseExtractor;

		public ResponseExtractorFuture(HttpMethod method, URI url,
				ListenableFuture<ClientHttpResponse> clientHttpResponseFuture, ResponseExtractor<T> responseExtractor) {
			super(clientHttpResponseFuture);
			this.method = method;
			this.url = url;
			this.responseExtractor = responseExtractor;
		}

		@Override
		protected final T adapt(ClientHttpResponse response) throws ExecutionException {
			try {
				if (!getErrorHandler().hasError(response)) {
					logResponseStatus(this.method, this.url, response);
				}
				else {
					handleResponseError(this.method, this.url, response);
				}
				return convertResponse(response);
			}
			catch (Throwable ex) {
				throw new ExecutionException(ex);
			}
			finally {
				if (response != null) {
					response.close();
				}
			}
		}

		protected T convertResponse(ClientHttpResponse response) throws IOException {
			return (this.responseExtractor != null ? this.responseExtractor.extractData(response) : null);
		}
	}


	/**
	 * Adapts a {@link RequestCallback} to the {@link AsyncRequestCallback} interface.
	 * <p>
	 *  将{@link RequestCallback}适配到{@link AsyncRequestCallback}界面
	 * 
	 */
	private static class AsyncRequestCallbackAdapter implements AsyncRequestCallback {

		private final RequestCallback adaptee;

		/**
		 * Create a new {@code AsyncRequestCallbackAdapter} from the given
		 * {@link RequestCallback}.
		 * <p>
		 *  从给定的{@link RequestCallback}创建一个新的{@code AsyncRequestCallbackAdapter}
		 * 
		 * @param requestCallback the callback to base this adapter on
		 */
		public AsyncRequestCallbackAdapter(RequestCallback requestCallback) {
			this.adaptee = requestCallback;
		}

		@Override
		public void doWithRequest(final AsyncClientHttpRequest request) throws IOException {
			if (this.adaptee != null) {
				this.adaptee.doWithRequest(new ClientHttpRequest() {
					@Override
					public ClientHttpResponse execute() throws IOException {
						throw new UnsupportedOperationException("execute not supported");
					}
					@Override
					public OutputStream getBody() throws IOException {
						return request.getBody();
					}
					@Override
					public HttpMethod getMethod() {
						return request.getMethod();
					}
					@Override
					public URI getURI() {
						return request.getURI();
					}
					@Override
					public HttpHeaders getHeaders() {
						return request.getHeaders();
					}
				});
			}
		}
	}

}
