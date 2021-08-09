/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

/**
 * Interface specifying a basic set of RESTful operations. Implemented by {@link RestTemplate}.
 * Not often used directly, but a useful option to enhance testability, as it can easily
 * be mocked or stubbed.
 *
 * <p>
 *  指定RESTful操作的基本集合的接口{@link RestTemplate}实现不经常直接使用,而是一个有用的选项来增强可测试性,因为它可以很容易地被嘲笑或者被扼杀
 * 
 * 
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @since 3.0
 * @see RestTemplate
 * @see AsyncRestOperations
 */
public interface RestOperations {

	// GET

	/**
	 * Retrieve a representation by doing a GET on the specified URL.
	 * The response (if any) is converted and returned.
	 * <p>URI Template variables are expanded using the given URI variables, if any.
	 * <p>
	 * 通过在指定的URL上执行GET获取表示通过转换和返回响应(如果有的话)<p> URI使用给定的URI变量扩展URI模板变量(如果有的话)
	 * 
	 * 
	 * @param url the URL
	 * @param responseType the type of the return value
	 * @param uriVariables the variables to expand the template
	 * @return the converted object
	 */
	<T> T getForObject(String url, Class<T> responseType, Object... uriVariables) throws RestClientException;

	/**
	 * Retrieve a representation by doing a GET on the URI template.
	 * The response (if any) is converted and returned.
	 * <p>URI Template variables are expanded using the given map.
	 * <p>
	 *  通过在URI模板上执行GET获取表示通过转换和返回响应(如果有)返回<p> URI使用给定的映射扩展模板变量
	 * 
	 * 
	 * @param url the URL
	 * @param responseType the type of the return value
	 * @param uriVariables the map containing variables for the URI template
	 * @return the converted object
	 */
	<T> T getForObject(String url, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException;

	/**
	 * Retrieve a representation by doing a GET on the URL .
	 * The response (if any) is converted and returned.
	 * <p>
	 *  通过在URL上执行GET获取表示通过转换和返回响应(如果有)
	 * 
	 * 
	 * @param url the URL
	 * @param responseType the type of the return value
	 * @return the converted object
	 */
	<T> T getForObject(URI url, Class<T> responseType) throws RestClientException;

	/**
	 * Retrieve an entity by doing a GET on the specified URL.
	 * The response is converted and stored in an {@link ResponseEntity}.
	 * <p>URI Template variables are expanded using the given URI variables, if any.
	 * <p>
	 *  通过在指定的URL上执行GET来获取实体响应被转换并存储在{@link ResponseEntity} <p> URI中使用给定的URI变量扩展模板变量(如果有的话)
	 * 
	 * 
	 * @param url the URL
	 * @param responseType the type of the return value
	 * @param uriVariables the variables to expand the template
	 * @return the entity
	 * @since 3.0.2
	 */
	<T> ResponseEntity<T> getForEntity(String url, Class<T> responseType, Object... uriVariables) throws RestClientException;

	/**
	 * Retrieve a representation by doing a GET on the URI template.
	 * The response is converted and stored in an {@link ResponseEntity}.
	 * <p>URI Template variables are expanded using the given map.
	 * <p>
	 * 通过在URI模板上执行GET获取表示响应被转换并存储在{@link ResponseEntity} <p> URI中使用给定的映射展开模板变量
	 * 
	 * 
	 * @param url the URL
	 * @param responseType the type of the return value
	 * @param uriVariables the map containing variables for the URI template
	 * @return the converted object
	 * @since 3.0.2
	 */
	<T> ResponseEntity<T> getForEntity(String url, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException;

	/**
	 * Retrieve a representation by doing a GET on the URL .
	 * The response is converted and stored in an {@link ResponseEntity}.
	 * <p>
	 *  通过在URL上执行GET来获取表示响应被转换并存储在{@link ResponseEntity}
	 * 
	 * 
	 * @param url the URL
	 * @param responseType the type of the return value
	 * @return the converted object
	 * @since 3.0.2
	 */
	<T> ResponseEntity<T> getForEntity(URI url, Class<T> responseType) throws RestClientException;


	// HEAD

	/**
	 * Retrieve all headers of the resource specified by the URI template.
	 * <p>URI Template variables are expanded using the given URI variables, if any.
	 * <p>
	 *  检索由URI模板指定的资源的所有标题<p> URI使用给定的URI变量(如果有)扩展模板变量
	 * 
	 * 
	 * @param url the URL
	 * @param uriVariables the variables to expand the template
	 * @return all HTTP headers of that resource
	 */
	HttpHeaders headForHeaders(String url, Object... uriVariables) throws RestClientException;

	/**
	 * Retrieve all headers of the resource specified by the URI template.
	 * <p>URI Template variables are expanded using the given map.
	 * <p>
	 *  检索由URI模板指定的资源的所有标题<p> URI使用给定的映射扩展模板变量
	 * 
	 * 
	 * @param url the URL
	 * @param uriVariables the map containing variables for the URI template
	 * @return all HTTP headers of that resource
	 */
	HttpHeaders headForHeaders(String url, Map<String, ?> uriVariables) throws RestClientException;

	/**
	 * Retrieve all headers of the resource specified by the URL.
	 * <p>
	 *  检索由URL指定的资源的所有头文件
	 * 
	 * 
	 * @param url the URL
	 * @return all HTTP headers of that resource
	 */
	HttpHeaders headForHeaders(URI url) throws RestClientException;


	// POST

	/**
	 * Create a new resource by POSTing the given object to the URI template, and returns the value of the
	 * {@code Location} header. This header typically indicates where the new resource is stored.
	 * <p>URI Template variables are expanded using the given URI variables, if any.
	 * <p>The {@code request} parameter can be a {@link HttpEntity} in order to
	 * add additional HTTP headers to the request.
	 * <p>
	 * 通过将给定的对象POST到URI模板来创建新资源,并返回{@code位置}头的值此标题通常指示新资源的存储位置<p> URI模板变量使用给定的URI变量进行扩展,如果有任何<p> {@code request}
	 * 参数可以是{@link HttpEntity},以便向请求添加其他HTTP标头。
	 * 
	 * 
	 * @param url the URL
	 * @param request the Object to be POSTed, may be {@code null}
	 * @param uriVariables the variables to expand the template
	 * @return the value for the {@code Location} header
	 * @see HttpEntity
	 */
	URI postForLocation(String url, Object request, Object... uriVariables) throws RestClientException;

	/**
	 * Create a new resource by POSTing the given object to the URI template, and returns the value of the
	 * {@code Location} header. This header typically indicates where the new resource is stored.
	 * <p>URI Template variables are expanded using the given map.
	 * <p>The {@code request} parameter can be a {@link HttpEntity} in order to
	 * add additional HTTP headers to the request.
	 * <p>
	 *  通过将给定的对象POST到URI模板来创建新资源,并返回{@code位置}头的值此标题通常指示新资源的存储位置。
	 * <p> URI模板变量使用给定的映射<p > {@code request}参数可以是{@link HttpEntity},以便向请求添加其他HTTP标头。
	 * 
	 * 
	 * @param url the URL
	 * @param request the Object to be POSTed, may be {@code null}
	 * @param uriVariables the variables to expand the template
	 * @return the value for the {@code Location} header
	 * @see HttpEntity
	 */
	URI postForLocation(String url, Object request, Map<String, ?> uriVariables) throws RestClientException;

	/**
	 * Create a new resource by POSTing the given object to the URL, and returns the value of the
	 * {@code Location} header. This header typically indicates where the new resource is stored.
	 * <p>The {@code request} parameter can be a {@link HttpEntity} in order to
	 * add additional HTTP headers to the request.
	 * <p>
	 * 通过将给定的对象发布到URL来创建新资源,并返回{@code位置}头的值此标题通常指示存储新资源的位置<p> {@code request}参数可以是{@链接HttpEntity},以便向请求添加其他H
	 * TTP标头。
	 * 
	 * 
	 * @param url the URL
	 * @param request the Object to be POSTed, may be {@code null}
	 * @return the value for the {@code Location} header
	 * @see HttpEntity
	 */
	URI postForLocation(URI url, Object request) throws RestClientException;

	/**
	 * Create a new resource by POSTing the given object to the URI template,
	 * and returns the representation found in the response.
	 * <p>URI Template variables are expanded using the given URI variables, if any.
	 * <p>The {@code request} parameter can be a {@link HttpEntity} in order to
	 * add additional HTTP headers to the request.
	 * <p>
	 *  通过将给定的对象POST到URI模板来创建新资源,并返回在响应中找到的表示<p> URI使用给定的URI变量扩展模板变量(如果有)<p> {@code request}参数可以一个{@link HttpEntity}
	 * ,以便向请求添加其他HTTP标头。
	 * 
	 * 
	 * @param url the URL
	 * @param request the Object to be POSTed, may be {@code null}
	 * @param responseType the type of the return value
	 * @param uriVariables the variables to expand the template
	 * @return the converted object
	 * @see HttpEntity
	 */
	<T> T postForObject(String url, Object request, Class<T> responseType, Object... uriVariables)
			throws RestClientException;

	/**
	 * Create a new resource by POSTing the given object to the URI template,
	 * and returns the representation found in the response.
	 * <p>URI Template variables are expanded using the given map.
	 * <p>The {@code request} parameter can be a {@link HttpEntity} in order to
	 * add additional HTTP headers to the request.
	 * <p>
	 * 通过将给定的对象POST到URI模板来创建新资源,并返回在响应中找到的表示<p> URI使用给定的映射扩展模板变量<p> {@code request}参数可以是{@link HttpEntity},以
	 * 便为请求添加额外的HTTP头。
	 * 
	 * 
	 * @param url the URL
	 * @param request the Object to be POSTed, may be {@code null}
	 * @param responseType the type of the return value
	 * @param uriVariables the variables to expand the template
	 * @return the converted object
	 * @see HttpEntity
	 */
	<T> T postForObject(String url, Object request, Class<T> responseType, Map<String, ?> uriVariables)
			throws RestClientException;

	/**
	 * Create a new resource by POSTing the given object to the URL,
	 * and returns the representation found in the response.
	 * <p>The {@code request} parameter can be a {@link HttpEntity} in order to
	 * add additional HTTP headers to the request.
	 * <p>
	 *  通过将给定的对象POST到URL来创建新资源,并返回响应中找到的表示<p> {@code request}参数可以是{@link HttpEntity},以便向请求添加其他HTTP标头
	 * 
	 * 
	 * @param url the URL
	 * @param request the Object to be POSTed, may be {@code null}
	 * @param responseType the type of the return value
	 * @return the converted object
	 * @see HttpEntity
	 */
	<T> T postForObject(URI url, Object request, Class<T> responseType) throws RestClientException;

	/**
	 * Create a new resource by POSTing the given object to the URI template,
	 * and returns the response as {@link ResponseEntity}.
	 * <p>URI Template variables are expanded using the given URI variables, if any.
	 * <p>The {@code request} parameter can be a {@link HttpEntity} in order to
	 * add additional HTTP headers to the request.
	 * <p>
	 * 通过将给定的对象POST到URI模板来创建一个新的资源,并返回响应{@link ResponseEntity} <p> URI使用给定的URI变量扩展模板变量,如果有的话,{@code request}
	 * 参数可以是{@link HttpEntity},以便向请求添加其他HTTP标头。
	 * 
	 * 
	 * @param url the URL
	 * @param request the Object to be POSTed, may be {@code null}
	 * @param uriVariables the variables to expand the template
	 * @return the converted object
	 * @see HttpEntity
	 * @since 3.0.2
	 */
	<T> ResponseEntity<T> postForEntity(String url, Object request, Class<T> responseType, Object... uriVariables)
			throws RestClientException;

	/**
	 * Create a new resource by POSTing the given object to the URI template,
	 * and returns the response as {@link HttpEntity}.
	 * <p>URI Template variables are expanded using the given map.
	 * <p>The {@code request} parameter can be a {@link HttpEntity} in order to
	 * add additional HTTP headers to the request.
	 * <p>
	 *  通过将给定对象POST到URI模板来创建新资源,并以{@link HttpEntity} <p>返回响应URI使用给定的映射扩展模板变量<p> {@code request}参数可以是{ @link HttpEntity}
	 * ,以便向请求添加额外的HTTP头。
	 * 
	 * 
	 * @param url the URL
	 * @param request the Object to be POSTed, may be {@code null}
	 * @param uriVariables the variables to expand the template
	 * @return the converted object
	 * @see HttpEntity
	 * @since 3.0.2
	 */
	<T> ResponseEntity<T> postForEntity(String url, Object request, Class<T> responseType, Map<String, ?> uriVariables)
			throws RestClientException;

	/**
	 * Create a new resource by POSTing the given object to the URL,
	 * and returns the response as {@link ResponseEntity}.
	 * <p>The {@code request} parameter can be a {@link HttpEntity} in order to
	 * add additional HTTP headers to the request.
	 * <p>
	 * 通过将给定对象发布到URL来创建新资源,并以{@link ResponseEntity}返回响应<p> {@code request}参数可以是{@link HttpEntity},以便向其添加其他HT
	 * TP标头请求。
	 * 
	 * 
	 * @param url the URL
	 * @param request the Object to be POSTed, may be {@code null}
	 * @return the converted object
	 * @see HttpEntity
	 * @since 3.0.2
	 */
	<T> ResponseEntity<T> postForEntity(URI url, Object request, Class<T> responseType) throws RestClientException;


	// PUT

	/**
	 * Create or update a resource by PUTting the given object to the URI.
	 * <p>URI Template variables are expanded using the given URI variables, if any.
	 * <p>The {@code request} parameter can be a {@link HttpEntity} in order to
	 * add additional HTTP headers to the request.
	 * <p>
	 *  创建或更新资源通过将给定的对象PUT引用到URI <p> URI使用给定的URI变量扩展模板变量,如果有的话,{@code request}参数可以是{@link HttpEntity},以便向请求添
	 * 加其他HTTP标头。
	 * 
	 * 
	 * @param url the URL
	 * @param request the Object to be PUT, may be {@code null}
	 * @param uriVariables the variables to expand the template
	 * @see HttpEntity
	 */
	void put(String url, Object request, Object... uriVariables) throws RestClientException;

	/**
	 * Creates a new resource by PUTting the given object to URI template.
	 * <p>URI Template variables are expanded using the given map.
	 * <p>The {@code request} parameter can be a {@link HttpEntity} in order to
	 * add additional HTTP headers to the request.
	 * <p>
	 *  通过将给定的对象打印到URI模板来创建新资源<p> URI使用给定的映射扩展模板变量<p> {@code request}参数可以是{@link HttpEntity},以便添加额外的HTTP标头请求
	 * 。
	 * 
	 * 
	 * @param url the URL
	 * @param request the Object to be PUT, may be {@code null}
	 * @param uriVariables the variables to expand the template
	 * @see HttpEntity
	 */
	void put(String url, Object request, Map<String, ?> uriVariables) throws RestClientException;

	/**
	 * Creates a new resource by PUTting the given object to URL.
	 * <p>The {@code request} parameter can be a {@link HttpEntity} in order to
	 * add additional HTTP headers to the request.
	 * <p>
	 * 通过将给定对象输入到URL来创建新资源<p> {@code request}参数可以是{@link HttpEntity},以便为请求添加额外的HTTP头
	 * 
	 * 
	 * @param url the URL
	 * @param request the Object to be PUT, may be {@code null}
	 * @see HttpEntity
	 */
	void put(URI url, Object request) throws RestClientException;


	// DELETE

	/**
	 * Delete the resources at the specified URI.
	 * <p>URI Template variables are expanded using the given URI variables, if any.
	 * <p>
	 *  删除指定URI的资源<p> URI使用给定的URI变量(如果有)扩展模板变量
	 * 
	 * 
	 * @param url the URL
	 * @param uriVariables the variables to expand in the template
	 */
	void delete(String url, Object... uriVariables) throws RestClientException;

	/**
	 * Delete the resources at the specified URI.
	 * <p>URI Template variables are expanded using the given map.
	 *
	 * <p>
	 *  删除指定URI的资源<p> URI使用给定的映射扩展模板变量
	 * 
	 * 
	 * @param url the URL
	 * @param uriVariables the variables to expand the template
	 */
	void delete(String url, Map<String, ?> uriVariables) throws RestClientException;

	/**
	 * Delete the resources at the specified URL.
	 * <p>
	 *  删除指定URL的资源
	 * 
	 * 
	 * @param url the URL
	 */
	void delete(URI url) throws RestClientException;


	// OPTIONS

	/**
	 * Return the value of the Allow header for the given URI.
	 * <p>URI Template variables are expanded using the given URI variables, if any.
	 * <p>
	 *  返回给定URI的Allow标头的值<p> URI使用给定的URI变量(如果有的话)扩展模板变量
	 * 
	 * 
	 * @param url the URL
	 * @param uriVariables the variables to expand in the template
	 * @return the value of the allow header
	 */
	Set<HttpMethod> optionsForAllow(String url, Object... uriVariables) throws RestClientException;

	/**
	 * Return the value of the Allow header for the given URI.
	 * <p>URI Template variables are expanded using the given map.
	 * <p>
	 *  返回给定URI的"允许"头的值<p> URI使用给定的映射扩展模板变量
	 * 
	 * 
	 * @param url the URL
	 * @param uriVariables the variables to expand in the template
	 * @return the value of the allow header
	 */
	Set<HttpMethod> optionsForAllow(String url, Map<String, ?> uriVariables) throws RestClientException;

	/**
	 * Return the value of the Allow header for the given URL.
	 * <p>
	 *  返回给定URL的Allow标头的值
	 * 
	 * 
	 * @param url the URL
	 * @return the value of the allow header
	 */
	Set<HttpMethod> optionsForAllow(URI url) throws RestClientException;


	// exchange

	/**
	 * Execute the HTTP method to the given URI template, writing the given request entity to the request, and
	 * returns the response as {@link ResponseEntity}.
	 * <p>URI Template variables are expanded using the given URI variables, if any.
	 * <p>
	 * 对给定的URI模板执行HTTP方法,将给定的请求实体写入请求,并返回响应{@link ResponseEntity} <p> URI使用给定的URI变量扩展模板变量(如果有)
	 * 
	 * 
	 * @param url the URL
	 * @param method the HTTP method (GET, POST, etc)
	 * @param requestEntity the entity (headers and/or body) to write to the request, may be {@code null}
	 * @param responseType the type of the return value
	 * @param uriVariables the variables to expand in the template
	 * @return the response as entity
	 * @since 3.0.2
	 */
	<T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity,
			Class<T> responseType, Object... uriVariables) throws RestClientException;

	/**
	 * Execute the HTTP method to the given URI template, writing the given request entity to the request, and
	 * returns the response as {@link ResponseEntity}.
	 * <p>URI Template variables are expanded using the given URI variables, if any.
	 * <p>
	 *  对给定的URI模板执行HTTP方法,将给定的请求实体写入请求,并返回响应{@link ResponseEntity} <p> URI使用给定的URI变量扩展模板变量(如果有)
	 * 
	 * 
	 * @param url the URL
	 * @param method the HTTP method (GET, POST, etc)
	 * @param requestEntity the entity (headers and/or body) to write to the request, may be {@code null}
	 * @param responseType the type of the return value
	 * @param uriVariables the variables to expand in the template
	 * @return the response as entity
	 * @since 3.0.2
	 */
	<T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity,
			Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException;

	/**
	 * Execute the HTTP method to the given URI template, writing the given request entity to the request, and
	 * returns the response as {@link ResponseEntity}.
	 * <p>
	 *  对给定的URI模板执行HTTP方法,将给定的请求实体写入请求,并将响应返回为{@link ResponseEntity}
	 * 
	 * 
	 * @param url the URL
	 * @param method the HTTP method (GET, POST, etc)
	 * @param requestEntity the entity (headers and/or body) to write to the request, may be {@code null}
	 * @param responseType the type of the return value
	 * @return the response as entity
	 * @since 3.0.2
	 */
	<T> ResponseEntity<T> exchange(URI url, HttpMethod method, HttpEntity<?> requestEntity,
			Class<T> responseType) throws RestClientException;

	/**
	 * Execute the HTTP method to the given URI template, writing the given
	 * request entity to the request, and returns the response as {@link ResponseEntity}.
	 * The given {@link ParameterizedTypeReference} is used to pass generic type information:
	 * <pre class="code">
	 * ParameterizedTypeReference&lt;List&lt;MyBean&gt;&gt; myBean = new ParameterizedTypeReference&lt;List&lt;MyBean&gt;&gt;() {};
	 * ResponseEntity&lt;List&lt;MyBean&gt;&gt; response = template.exchange(&quot;http://example.com&quot;,HttpMethod.GET, null, myBean);
	 * </pre>
	 * <p>
	 * 对给定的URI模板执行HTTP方法,将给定的请求实体写入请求,并将响应返回为{@link ResponseEntity}。
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
	 * @return the response as entity
	 * @since 3.2
	 */
	<T> ResponseEntity<T> exchange(String url,HttpMethod method, HttpEntity<?> requestEntity,
			ParameterizedTypeReference<T> responseType, Object... uriVariables) throws RestClientException;

	/**
	 * Execute the HTTP method to the given URI template, writing the given
	 * request entity to the request, and returns the response as {@link ResponseEntity}.
	 * The given {@link ParameterizedTypeReference} is used to pass generic type information:
	 * <pre class="code">
	 * ParameterizedTypeReference&lt;List&lt;MyBean&gt;&gt; myBean = new ParameterizedTypeReference&lt;List&lt;MyBean&gt;&gt;() {};
	 * ResponseEntity&lt;List&lt;MyBean&gt;&gt; response = template.exchange(&quot;http://example.com&quot;,HttpMethod.GET, null, myBean);
	 * </pre>
	 * <p>
	 *  对给定的URI模板执行HTTP方法,将给定的请求实体写入请求,并将响应返回为{@link ResponseEntity}。
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
	 * @return the response as entity
	 * @since 3.2
	 */
	<T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity,
			ParameterizedTypeReference<T> responseType, Map<String, ?> uriVariables) throws RestClientException;

	/**
	 * Execute the HTTP method to the given URI template, writing the given
	 * request entity to the request, and returns the response as {@link ResponseEntity}.
	 * The given {@link ParameterizedTypeReference} is used to pass generic type information:
	 * <pre class="code">
	 * ParameterizedTypeReference&lt;List&lt;MyBean&gt;&gt; myBean = new ParameterizedTypeReference&lt;List&lt;MyBean&gt;&gt;() {};
	 * ResponseEntity&lt;List&lt;MyBean&gt;&gt; response = template.exchange(&quot;http://example.com&quot;,HttpMethod.GET, null, myBean);
	 * </pre>
	 * <p>
	 *  对给定的URI模板执行HTTP方法,将给定的请求实体写入请求,并将响应返回为{@link ResponseEntity}。
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
	 * @return the response as entity
	 * @since 3.2
	 */
	<T> ResponseEntity<T> exchange(URI url, HttpMethod method, HttpEntity<?> requestEntity,
			ParameterizedTypeReference<T> responseType) throws RestClientException;

	/**
	 * Execute the request specified in the given {@link RequestEntity} and return
	 * the response as {@link ResponseEntity}. Typically used in combination
	 * with the static builder methods on {@code RequestEntity}, for instance:
	 * <pre class="code">
	 * MyRequest body = ...
	 * RequestEntity request = RequestEntity.post(new URI(&quot;http://example.com/foo&quot;)).accept(MediaType.APPLICATION_JSON).body(body);
	 * ResponseEntity&lt;MyResponse&gt; response = template.exchange(request, MyResponse.class);
	 * </pre>
	 * <p>
	 * 执行给定的{@link RequestEntity}中指定的请求并返回响应{@link ResponseEntity}通常与{@code RequestEntity}中的静态构建器方法结合使用,例如：。
	 * <pre class="code">
	 *  MyRequest body = RequestEntity request = RequestEntitypost(new URI("http：// examplecom / foo"))accep
	 * t(MediaTypeAPPLICATION_JSON)body(body); ResponseEntity&LT; MyResponse&GT; response = templateexchange
	 * (request,MyResponseclass);。
	 * </pre>
	 * 
	 * @param requestEntity the entity to write to the request
	 * @param responseType the type of the return value
	 * @return the response as entity
	 * @since 4.1
	 */
	<T> ResponseEntity<T> exchange(RequestEntity<?> requestEntity, Class<T> responseType) throws RestClientException;

	/**
	 * Execute the request specified in the given {@link RequestEntity} and return
	 * the response as {@link ResponseEntity}. The given
	 * {@link ParameterizedTypeReference} is used to pass generic type information:
	 * <pre class="code">
	 * MyRequest body = ...
	 * RequestEntity request = RequestEntity.post(new URI(&quot;http://example.com/foo&quot;)).accept(MediaType.APPLICATION_JSON).body(body);
	 * ParameterizedTypeReference&lt;List&lt;MyResponse&gt;&gt; myBean = new ParameterizedTypeReference&lt;List&lt;MyResponse&gt;&gt;() {};
	 * ResponseEntity&lt;List&lt;MyResponse&gt;&gt; response = template.exchange(request, myBean);
	 * </pre>
	 * <p>
	 *  执行给定的{@link RequestEntity}中指定的请求,并将响应作为{@link ResponseEntity}返回。
	 * 给定的{@link ParameterizedTypeReference}用于传递通用类型信息：。
	 * <pre class="code">
	 * MyRequest body = RequestEntity request = RequestEntitypost(new URI("http：// examplecom / foo"))accept
	 * (MediaTypeAPPLICATION_JSON)body(body); ParameterizedTypeReference&LT;列表与LT; MyResponse&GT;&GT; myBean
	 *  = new ParameterizedTypeReference&lt; List&lt; MyResponse&gt;&gt;(){}; ResponseEntity&LT;列表与LT; MyRes
	 * ponse&GT;&GT; response = templateexchange(request,myBean);。
	 * </pre>
	 * 
	 * @param requestEntity the entity to write to the request
	 * @param responseType the type of the return value
	 * @return the response as entity
	 * @since 4.1
	 */
	<T> ResponseEntity<T> exchange(RequestEntity<?> requestEntity, ParameterizedTypeReference<T> responseType)
			throws RestClientException;


	// general execution

	/**
	 * Execute the HTTP method to the given URI template, preparing the request with the
	 * {@link RequestCallback}, and reading the response with a {@link ResponseExtractor}.
	 * <p>URI Template variables are expanded using the given URI variables, if any.
	 * <p>
	 *  对给定的URI模板执行HTTP方法,使用{@link RequestCallback}准备请求,并使用{@link ResponseExtractor} <p> URI读取响应使用给定的URI变量扩展
	 * 模板变量(如果有)。
	 * 
	 * 
	 * @param url the URL
	 * @param method the HTTP method (GET, POST, etc)
	 * @param requestCallback object that prepares the request
	 * @param responseExtractor object that extracts the return value from the response
	 * @param uriVariables the variables to expand in the template
	 * @return an arbitrary object, as returned by the {@link ResponseExtractor}
	 */
	<T> T execute(String url, HttpMethod method, RequestCallback requestCallback,
			ResponseExtractor<T> responseExtractor, Object... uriVariables) throws RestClientException;

	/**
	 * Execute the HTTP method to the given URI template, preparing the request with the
	 * {@link RequestCallback}, and reading the response with a {@link ResponseExtractor}.
	 * <p>URI Template variables are expanded using the given URI variables map.
	 * <p>
	 * 对给定的URI模板执行HTTP方法,使用{@link RequestCallback}准备请求,并使用{@link ResponseExtractor} <p> URI读取响应使用给定的URI变量映射扩
	 * 展模板变量。
	 * 
	 * 
	 * @param url the URL
	 * @param method the HTTP method (GET, POST, etc)
	 * @param requestCallback object that prepares the request
	 * @param responseExtractor object that extracts the return value from the response
	 * @param uriVariables the variables to expand in the template
	 * @return an arbitrary object, as returned by the {@link ResponseExtractor}
	 */
	<T> T execute(String url, HttpMethod method, RequestCallback requestCallback,
			ResponseExtractor<T> responseExtractor, Map<String, ?> uriVariables) throws RestClientException;

	/**
	 * Execute the HTTP method to the given URL, preparing the request with the
	 * {@link RequestCallback}, and reading the response with a {@link ResponseExtractor}.
	 * <p>
	 * 
	 * @param url the URL
	 * @param method the HTTP method (GET, POST, etc)
	 * @param requestCallback object that prepares the request
	 * @param responseExtractor object that extracts the return value from the response
	 * @return an arbitrary object, as returned by the {@link ResponseExtractor}
	 */
	<T> T execute(URI url, HttpMethod method, RequestCallback requestCallback,
			ResponseExtractor<T> responseExtractor) throws RestClientException;

}
