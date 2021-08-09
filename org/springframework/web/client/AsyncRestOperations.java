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

package org.springframework.web.client;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * Interface specifying a basic set of asynchronous RESTful operations. Implemented by
 * {@link AsyncRestTemplate}. Not often used directly, but a useful option to enhance
 * testability, as it can easily be mocked or stubbed.
 *
 * <p>
 *  指定异步RESTful操作的基本集合的接口由{@link AsyncRestTemplate}实现不经常直接使用,但是增强可测性的一个有用选项,因为它可以很容易地被嘲笑或者被扼杀
 * 
 * 
 * @author Arjen Poutsma
 * @since 4.0
 */
public interface AsyncRestOperations {

	/**
	 * Expose the synchronous Spring RestTemplate to allow synchronous invocation.
	 * <p>
	 * 公开同步的Spring RestTemplate以允许同步调用
	 * 
	 */
	RestOperations getRestOperations();


	// GET

	/**
	 * Asynchronously retrieve an entity by doing a GET on the specified URL. The response is
	 * converted and stored in an {@link ResponseEntity}.
	 * <p>URI Template variables are expanded using the given URI variables, if any.
	 * <p>
	 *  通过在指定的URL上执行GET异步检索实体响应被转换并存储在{@link ResponseEntity} <p> URI中使用给定的URI变量扩展模板变量(如果有的话)
	 * 
	 * 
	 * @param url the URL
	 * @param responseType the type of the return value
	 * @param uriVariables the variables to expand the template
	 * @return the entity wrapped in a {@link Future}
	 */
	<T> ListenableFuture<ResponseEntity<T>> getForEntity(String url, Class<T> responseType,
			Object... uriVariables) throws RestClientException;

	/**
	 * Asynchronously retrieve a representation by doing a GET on the URI template. The
	 * response is converted and stored in an {@link ResponseEntity}.
	 * <p>URI Template variables are expanded using the given map.
	 * <p>
	 *  通过在URI模板上执行GET异步检索表示响应被转换并存储在{@link ResponseEntity} <p> URI中使用给定的映射扩展模板变量
	 * 
	 * 
	 * @param url the URL
	 * @param responseType the type of the return value
	 * @param uriVariables the map containing variables for the URI template
	 * @return the entity wrapped in a {@link Future}
	 */
	<T> ListenableFuture<ResponseEntity<T>> getForEntity(String url, Class<T> responseType,
			Map<String, ?> uriVariables) throws RestClientException;

	/**
	 * Asynchronously retrieve a representation by doing a GET on the URL.
	 * The response is converted and stored in an {@link ResponseEntity}.
	 * <p>
	 *  通过在URL上执行GET异步检索表示响应被转换并存储在{@link ResponseEntity}
	 * 
	 * 
	 * @param url the URL
	 * @param responseType the type of the return value
	 * @return the entity wrapped in a {@link Future}
	 */
	<T> ListenableFuture<ResponseEntity<T>> getForEntity(URI url, Class<T> responseType)
			throws RestClientException;

	// HEAD

	/**
	 * Asynchronously retrieve all headers of the resource specified by the URI template.
	 * <p>URI Template variables are expanded using the given URI variables, if any.
	 * <p>
	 *  异步检索由URI模板指定的资源的所有标头<p> URI使用给定的URI变量扩展模板变量(如果有)
	 * 
	 * 
	 * @param url the URL
	 * @param uriVariables the variables to expand the template
	 * @return all HTTP headers of that resource wrapped in a {@link Future}
	 */
	ListenableFuture<HttpHeaders> headForHeaders(String url, Object... uriVariables)
			throws RestClientException;

	/**
	 * Asynchronously retrieve all headers of the resource specified by the URI template.
	 * <p>URI Template variables are expanded using the given map.
	 * <p>
	 * 异步检索由URI模板指定的资源的所有头文件使用给定的映射扩展模板变量
	 * 
	 * 
	 * @param url the URL
	 * @param uriVariables the map containing variables for the URI template
	 * @return all HTTP headers of that resource wrapped in a {@link Future}
	 */
	ListenableFuture<HttpHeaders> headForHeaders(String url, Map<String, ?> uriVariables)
			throws RestClientException;

	/**
	 * Asynchronously retrieve all headers of the resource specified by the URL.
	 * <p>
	 *  异步检索由URL指定的资源的所有头文件
	 * 
	 * 
	 * @param url the URL
	 * @return all HTTP headers of that resource wrapped in a {@link Future}
	 */
	ListenableFuture<HttpHeaders> headForHeaders(URI url) throws RestClientException;

	// POST

	/**
	 * Create a new resource by POSTing the given object to the URI template, and
	 * asynchronously returns the value of the {@code Location} header. This header
	 * typically indicates where the new resource is stored.
	 * <p>URI Template variables are expanded using the given URI variables, if any.
	 * <p>
	 *  通过将给定的对象POST到URI模板来创建新资源,并异步返回{@code位置}头的值此标题通常指示存储新资源的位置<p> URI模板变量使用给定的URI变量进行扩展如果有的话
	 * 
	 * 
	 * @param url the URL
	 * @param request the Object to be POSTed, may be {@code null}
	 * @param uriVariables the variables to expand the template
	 * @return the value for the {@code Location} header wrapped in a {@link Future}
	 * @see org.springframework.http.HttpEntity
	 */
	ListenableFuture<URI> postForLocation(String url, HttpEntity<?> request, Object... uriVariables)
			throws RestClientException;

	/**
	 * Create a new resource by POSTing the given object to the URI template, and
	 * asynchronously returns the value of the {@code Location} header. This header
	 * typically indicates where the new resource is stored.
	 * <p>URI Template variables are expanded using the given map.
	 * <p>
	 *  通过将给定的对象发布到URI模板来创建新资源,并异步返回{@code位置}头的值此标题通常指示新资源的存储位置。<p> URI使用给定的映射扩展模板变量
	 * 
	 * 
	 * @param url the URL
	 * @param request the Object to be POSTed, may be {@code null}
	 * @param uriVariables the variables to expand the template
	 * @return the value for the {@code Location} header wrapped in a {@link Future}
	 * @see org.springframework.http.HttpEntity
	 */
	ListenableFuture<URI> postForLocation(String url, HttpEntity<?> request, Map<String, ?> uriVariables)
			throws RestClientException;

	/**
	 * Create a new resource by POSTing the given object to the URL, and asynchronously
	 * returns the value of the {@code Location} header. This header typically indicates
	 * where the new resource is stored.
	 * <p>
	 * 通过将给定对象发布到URL来创建新资源,并异步返回{@code位置}头的值。此标头通常指示新资源的存储位置
	 * 
	 * 
	 * @param url the URL
	 * @param request the Object to be POSTed, may be {@code null}
	 * @return the value for the {@code Location} header wrapped in a {@link Future}
	 * @see org.springframework.http.HttpEntity
	 */
	ListenableFuture<URI> postForLocation(URI url, HttpEntity<?> request) throws RestClientException;

	/**
	 * Create a new resource by POSTing the given object to the URI template,
	 * and asynchronously returns the response as {@link ResponseEntity}.
	 * <p>URI Template variables are expanded using the given URI variables, if any.
	 * <p>
	 *  通过将给定的对象POST到URI模板来创建新资源,并异步返回响应{@link ResponseEntity} <p> URI使用给定的URI变量扩展模板变量(如果有)
	 * 
	 * 
	 * @param url the URL
	 * @param request the Object to be POSTed, may be {@code null}
	 * @param uriVariables the variables to expand the template
	 * @return the entity wrapped in a {@link Future}
	 * @see org.springframework.http.HttpEntity
	 */
	<T> ListenableFuture<ResponseEntity<T>> postForEntity(String url, HttpEntity<?> request,
			Class<T> responseType, Object... uriVariables) throws RestClientException;

	/**
	 * Create a new resource by POSTing the given object to the URI template,
	 * and asynchronously returns the response as {@link ResponseEntity}.
	 * <p>URI Template variables are expanded using the given map.
	 * <p>
	 *  通过将给定对象发布到URI模板来创建新资源,并异步返回响应{@link ResponseEntity} <p> URI使用给定的映射扩展模板变量
	 * 
	 * 
	 * @param url the URL
	 * @param request the Object to be POSTed, may be {@code null}
	 * @param uriVariables the variables to expand the template
	 * @return the entity wrapped in a {@link Future}
	 * @see org.springframework.http.HttpEntity
	 */
	<T> ListenableFuture<ResponseEntity<T>> postForEntity(String url, HttpEntity<?> request,
			Class<T> responseType, Map<String, ?> uriVariables)
			throws RestClientException;

	/**
	 * Create a new resource by POSTing the given object to the URL,
	 * and asynchronously returns the response as {@link ResponseEntity}.
	 * <p>
	 *  通过将给定对象发布到URL来创建新资源,并异步返回响应{@link ResponseEntity}
	 * 
	 * 
	 * @param url the URL
	 * @param request the Object to be POSTed, may be {@code null}
	 * @return the entity wrapped in a {@link Future}
	 * @see org.springframework.http.HttpEntity
	 */
	<T> ListenableFuture<ResponseEntity<T>> postForEntity(URI url, HttpEntity<?> request,
			Class<T> responseType) throws RestClientException;

	// PUT

	/**
	 * Create or update a resource by PUTting the given object to the URI.
	 * <p>URI Template variables are expanded using the given URI variables, if any.
	 * <p>The Future will return a {@code null} result upon completion.
	 * <p>
	 * 创建或更新资源通过PUT给定的对象到URI <p> URI模板变量使用给定的URI变量进行扩展,如果有的话,"未来"将在完成后返回一个{@code null}结果
	 * 
	 * 
	 * @param url the URL
	 * @param request the Object to be PUT, may be {@code null}
	 * @param uriVariables the variables to expand the template
	 * @see HttpEntity
	 */
	ListenableFuture<?> put(String url, HttpEntity<?> request, Object... uriVariables)
			throws RestClientException;

	/**
	 * Creates a new resource by PUTting the given object to URI template.
	 * <p>URI Template variables are expanded using the given map.
	 * <p>The Future will return a {@code null} result upon completion.
	 * <p>
	 *  通过将给定的对象打印到URI模板来创建一个新的资源<p> URI使用给定的地图扩展模板变量<p>未来将在完成后返回{@code null}结果
	 * 
	 * 
	 * @param url the URL
	 * @param request the Object to be PUT, may be {@code null}
	 * @param uriVariables the variables to expand the template
	 * @see HttpEntity
	 */
	ListenableFuture<?> put(String url, HttpEntity<?> request, Map<String, ?> uriVariables)
			throws RestClientException;

	/**
	 * Creates a new resource by PUTting the given object to URL.
	 * <p>The Future will return a {@code null} result upon completion.
	 * <p>
	 *  通过将给定的对象输入到URL来创建一个新的资源<p>未来将在完成后返回{@code null}结果
	 * 
	 * 
	 * @param url the URL
	 * @param request the Object to be PUT, may be {@code null}
	 * @see HttpEntity
	 */
	ListenableFuture<?> put(URI url, HttpEntity<?> request) throws RestClientException;

	// DELETE

	/**
	 * Asynchronously delete the resources at the specified URI.
	 * <p>URI Template variables are expanded using the given URI variables, if any.
	 * <p>The Future will return a {@code null} result upon completion.
	 * <p>
	 *  异步地删除指定URI的资源<p> URI使用给定的URI变量扩展模板变量,如果有的话,"未来"将在完成后返回{@code null}结果
	 * 
	 * 
	 * @param url the URL
	 * @param uriVariables the variables to expand in the template
	 */
	ListenableFuture<?> delete(String url, Object... uriVariables) throws RestClientException;

	/**
	 * Asynchronously delete the resources at the specified URI.
	 * <p>URI Template variables are expanded using the given URI variables, if any.
	 * <p>The Future will return a {@code null} result upon completion.
	 * <p>
	 * 异步地删除指定URI的资源<p> URI使用给定的URI变量扩展模板变量,如果有的话,"未来"将在完成后返回{@code null}结果
	 * 
	 * 
	 * @param url the URL
	 * @param uriVariables the variables to expand in the template
	 */
	ListenableFuture<?> delete(String url, Map<String, ?> uriVariables) throws RestClientException;

	/**
	 * Asynchronously delete the resources at the specified URI.
	 * <p>URI Template variables are expanded using the given URI variables, if any.
	 * <p>The Future will return a {@code null} result upon completion.
	 * <p>
	 *  异步地删除指定URI的资源<p> URI使用给定的URI变量扩展模板变量,如果有的话,"未来"将在完成后返回{@code null}结果
	 * 
	 * 
	 * @param url the URL
	 */
	ListenableFuture<?> delete(URI url) throws RestClientException;

	// OPTIONS

	/**
	 * Asynchronously return the value of the Allow header for the given URI.
	 * <p>URI Template variables are expanded using the given URI variables, if any.
	 * <p>
	 *  异步返回给定URI的Allow标头的值<p> URI使用给定的URI变量扩展模板变量,如果有的话
	 * 
	 * 
	 * @param url the URL
	 * @param uriVariables the variables to expand in the template
	 * @return the value of the allow header wrapped in a {@link Future}
	 */
	ListenableFuture<Set<HttpMethod>> optionsForAllow(String url, Object... uriVariables)
			throws RestClientException;

	/**
	 * Asynchronously return the value of the Allow header for the given URI.
	 * <p>URI Template variables are expanded using the given map.
	 * <p>
	 *  异步返回给定URI的"允许"头的值<p> URI使用给定的映射扩展模板变量
	 * 
	 * 
	 * @param url the URL
	 * @param uriVariables the variables to expand in the template
	 * @return the value of the allow header wrapped in a {@link Future}
	 */
	ListenableFuture<Set<HttpMethod>> optionsForAllow(String url, Map<String, ?> uriVariables)
			throws RestClientException;

	/**
	 * Asynchronously return the value of the Allow header for the given URL.
	 * <p>
	 *  异步返回给定URL的Allow标头的值
	 * 
	 * 
	 * @param url the URL
	 * @return the value of the allow header wrapped in a {@link Future}
	 */
	ListenableFuture<Set<HttpMethod>> optionsForAllow(URI url) throws RestClientException;


	// exchange

	/**
	 * Asynchronously execute the HTTP method to the given URI template, writing the
	 * given request entity to the request, and returns the response as
	 * {@link ResponseEntity}.
	 * <p>URI Template variables are expanded using the given URI variables, if any.
	 * <p>
	 * 异步地向给定的URI模板执行HTTP方法,将给定的请求实体写入请求,并返回响应为{@link ResponseEntity} <p> URI使用给定的URI变量扩展模板变量(如果有)
	 * 
	 * 
	 * @param url the URL
	 * @param method the HTTP method (GET, POST, etc)
	 * @param requestEntity the entity (headers and/or body) to write to the request
	 * (may be {@code null})
	 * @param responseType the type of the return value
	 * @param uriVariables the variables to expand in the template
	 * @return the response as entity wrapped in a {@link Future}
	 */
	<T> ListenableFuture<ResponseEntity<T>> exchange(String url, HttpMethod method,
			HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables)
			throws RestClientException;

	/**
	 * Asynchronously execute the HTTP method to the given URI template, writing the
	 * given request entity to the request, and returns the response as
	 * {@link ResponseEntity}.
	 * <p>URI Template variables are expanded using the given URI variables, if any.
	 * <p>
	 *  异步地向给定的URI模板执行HTTP方法,将给定的请求实体写入请求,并返回响应为{@link ResponseEntity} <p> URI使用给定的URI变量扩展模板变量(如果有)
	 * 
	 * 
	 * @param url the URL
	 * @param method the HTTP method (GET, POST, etc)
	 * @param requestEntity the entity (headers and/or body) to write to the request
	 * (may be {@code null})
	 * @param responseType the type of the return value
	 * @param uriVariables the variables to expand in the template
	 * @return the response as entity wrapped in a {@link Future}
	 */
	<T> ListenableFuture<ResponseEntity<T>> exchange(String url, HttpMethod method,
			HttpEntity<?> requestEntity, Class<T> responseType,
			Map<String, ?> uriVariables) throws RestClientException;

	/**
	 * Asynchronously execute the HTTP method to the given URI template, writing the
	 * given request entity to the request, and returns the response as
	 * {@link ResponseEntity}.
	 * <p>
	 *  将HTTP方法异步执行到给定的URI模板,将给定的请求实体写入请求,并将响应返回为{@link ResponseEntity}
	 * 
	 * 
	 * @param url the URL
	 * @param method the HTTP method (GET, POST, etc)
	 * @param requestEntity the entity (headers and/or body) to write to the request
	 * (may be {@code null})
	 * @param responseType the type of the return value
	 * @return the response as entity wrapped in a {@link Future}
	 */
	<T> ListenableFuture<ResponseEntity<T>> exchange(URI url, HttpMethod method,
			HttpEntity<?> requestEntity, Class<T> responseType)
			throws RestClientException;

	/**
	 * Asynchronously execute the HTTP method to the given URI template, writing the given
	 * request entity to the request, and returns the response as {@link ResponseEntity}.
	 * The given {@link ParameterizedTypeReference} is used to pass generic type
	 * information:
	 * <pre class="code">
	 * ParameterizedTypeReference&lt;List&lt;MyBean&gt;&gt; myBean = new ParameterizedTypeReference&lt;List&lt;MyBean&gt;&gt;() {};
	 * ResponseEntity&lt;List&lt;MyBean&gt;&gt; response = template.exchange(&quot;http://example.com&quot;,HttpMethod.GET, null, myBean);
	 * </pre>
	 * <p>
	 * 异步地向给定的URI模板执行HTTP方法,将给定的请求实体写入请求,并将响应作为{@link ResponseEntity}返回。
	 * 给定的{@link ParameterizedTypeReference}用于传递通用类型信息：。
	 * <pre class="code">
	 *  ParameterizedTypeReference&LT;列表与LT;为myBean&GT;&GT; myBean = new ParameterizedTypeReference&lt; List
	 * &MyBean&gt;&gt;(){}; ResponseEntity&LT;列表与LT;为myBean&GT;&GT; response = templateexchange("http：// exa
	 * mplecom",HttpMethodGET,null,myBean);。
	 * </pre>
	 * 
	 * @param url the URL
	 * @param method the HTTP method (GET, POST, etc)
	 * @param requestEntity the entity (headers and/or body) to write to the
	 * request, may be {@code null}
	 * @param responseType the type of the return value
	 * @param uriVariables the variables to expand in the template
	 * @return the response as entity wrapped in a {@link Future}
	 */
	<T> ListenableFuture<ResponseEntity<T>> exchange(String url, HttpMethod method,
			HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType,
			Object... uriVariables) throws RestClientException;

	/**
	 * Asynchronously execute the HTTP method to the given URI template, writing the given
	 * request entity to the request, and returns the response as {@link ResponseEntity}.
	 * The given {@link ParameterizedTypeReference} is used to pass generic type
	 * information:
	 * <pre class="code">
	 * ParameterizedTypeReference&lt;List&lt;MyBean&gt;&gt; myBean = new ParameterizedTypeReference&lt;List&lt;MyBean&gt;&gt;() {};
	 * ResponseEntity&lt;List&lt;MyBean&gt;&gt; response = template.exchange(&quot;http://example.com&quot;,HttpMethod.GET, null, myBean);
	 * </pre>
	 * <p>
	 *  异步地向给定的URI模板执行HTTP方法,将给定的请求实体写入请求,并将响应作为{@link ResponseEntity}返回。
	 * 给定的{@link ParameterizedTypeReference}用于传递通用类型信息：。
	 * <pre class="code">
	 * ParameterizedTypeReference&LT;列表与LT;为myBean&GT;&GT; myBean = new ParameterizedTypeReference&lt; List&
	 * MyBean&gt;&gt;(){}; ResponseEntity&LT;列表与LT;为myBean&GT;&GT; response = templateexchange("http：// exam
	 * plecom",HttpMethodGET,null,myBean);。
	 * </pre>
	 * 
	 * @param url the URL
	 * @param method the HTTP method (GET, POST, etc)
	 * @param requestEntity the entity (headers and/or body) to write to the request, may be {@code null}
	 * @param responseType the type of the return value
	 * @param uriVariables the variables to expand in the template
	 * @return the response as entity wrapped in a {@link Future}
	 */
	<T> ListenableFuture<ResponseEntity<T>> exchange(String url, HttpMethod method,
			HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType,
			Map<String, ?> uriVariables) throws RestClientException;

	/**
	 * Asynchronously execute the HTTP method to the given URI template, writing the given
	 * request entity to the request, and returns the response as {@link ResponseEntity}.
	 * The given {@link ParameterizedTypeReference} is used to pass generic type
	 * information:
	 * <pre class="code">
	 * ParameterizedTypeReference&lt;List&lt;MyBean&gt;&gt; myBean = new ParameterizedTypeReference&lt;List&lt;MyBean&gt;&gt;() {};
	 * ResponseEntity&lt;List&lt;MyBean&gt;&gt; response = template.exchange(&quot;http://example.com&quot;,HttpMethod.GET, null, myBean);
	 * </pre>
	 * <p>
	 *  异步地向给定的URI模板执行HTTP方法,将给定的请求实体写入请求,并将响应作为{@link ResponseEntity}返回。
	 * 给定的{@link ParameterizedTypeReference}用于传递通用类型信息：。
	 * <pre class="code">
	 *  ParameterizedTypeReference&LT;列表与LT;为myBean&GT;&GT; myBean = new ParameterizedTypeReference&lt; List
	 * &MyBean&gt;&gt;(){}; ResponseEntity&LT;列表与LT;为myBean&GT;&GT; response = templateexchange("http：// exa
	 * mplecom",HttpMethodGET,null,myBean);。
	 * </pre>
	 * 
	 * @param url the URL
	 * @param method the HTTP method (GET, POST, etc)
	 * @param requestEntity the entity (headers and/or body) to write to the request, may be {@code null}
	 * @param responseType the type of the return value
	 * @return the response as entity wrapped in a {@link Future}
	 */
	<T> ListenableFuture<ResponseEntity<T>> exchange(URI url, HttpMethod method,
			HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType)
			throws RestClientException;


	// general execution

	/**
	 * Asynchronously execute the HTTP method to the given URI template, preparing the
	 * request with the {@link AsyncRequestCallback}, and reading the response with a
	 * {@link ResponseExtractor}.
	 * <p>URI Template variables are expanded using the given URI variables, if any.
	 * <p>
	 * 异步地向给定的URI模板执行HTTP方法,使用{@link AsyncRequestCallback}准备请求,并使用{@link ResponseExtractor} <p> URI读取响应模板变量使
	 * 用给定的URI变量进行扩展(如果有)。
	 * 
	 * 
	 * @param url the URL
	 * @param method the HTTP method (GET, POST, etc)
	 * @param requestCallback object that prepares the request
	 * @param responseExtractor object that extracts the return value from the response
	 * @param uriVariables the variables to expand in the template
	 * @return an arbitrary object, as returned by the {@link ResponseExtractor}
	 */
	<T> ListenableFuture<T> execute(String url, HttpMethod method,
			AsyncRequestCallback requestCallback, ResponseExtractor<T> responseExtractor,
			Object... uriVariables) throws RestClientException;

	/**
	 * Asynchronously execute the HTTP method to the given URI template, preparing the
	 * request with the {@link AsyncRequestCallback}, and reading the response with a
	 * {@link ResponseExtractor}.
	 * <p>URI Template variables are expanded using the given URI variables map.
	 * <p>
	 *  异步地向给定的URI模板执行HTTP方法,使用{@link AsyncRequestCallback}准备请求,并使用{@link ResponseExtractor} <p> URI读取响应模板变量
	 * 使用给定的URI变量映射。
	 * 
	 * 
	 * @param url the URL
	 * @param method the HTTP method (GET, POST, etc)
	 * @param requestCallback object that prepares the request
	 * @param responseExtractor object that extracts the return value from the response
	 * @param uriVariables the variables to expand in the template
	 * @return an arbitrary object, as returned by the {@link ResponseExtractor}
	 */
	<T> ListenableFuture<T> execute(String url, HttpMethod method,
			AsyncRequestCallback requestCallback, ResponseExtractor<T> responseExtractor,
			Map<String, ?> uriVariables) throws RestClientException;

	/**
	 * Asynchronously execute the HTTP method to the given URL, preparing the request
	 * with the {@link AsyncRequestCallback}, and reading the response with a
	 * {@link ResponseExtractor}.
	 * <p>
	 * 
	 * @param url the URL
	 * @param method the HTTP method (GET, POST, etc)
	 * @param requestCallback object that prepares the request
	 * @param responseExtractor object that extracts the return value from the response
	 * @return an arbitrary object, as returned by the {@link ResponseExtractor}
	 */
	<T> ListenableFuture<T> execute(URI url, HttpMethod method,
			AsyncRequestCallback requestCallback, ResponseExtractor<T> responseExtractor)
			throws RestClientException;

}
