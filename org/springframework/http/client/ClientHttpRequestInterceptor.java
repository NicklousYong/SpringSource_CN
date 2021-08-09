/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2012 the original author or authors.
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

import org.springframework.http.HttpRequest;

/**
 * Intercepts client-side HTTP requests. Implementations of this interface can be {@linkplain
 * org.springframework.web.client.RestTemplate#setInterceptors(java.util.List) registered} with the
 * {@link org.springframework.web.client.RestTemplate RestTemplate}, as to modify the outgoing {@link ClientHttpRequest}
 * and/or the incoming {@link ClientHttpResponse}.
 *
 * <p>The main entry point for interceptors is {@link #intercept(HttpRequest, byte[], ClientHttpRequestExecution)}.
 *
 * <p>
 * 拦截客户端HTTP请求通过{@link orgspringframeworkwebclientRestTemplate RestTemplate},该接口的实现可以是{@linkplain orgspringframeworkwebclientRestTemplate#setInterceptors(javautilList)),以修改传出的{@link ClientHttpRequest}
 * 和/或传入的{@link ClientHttpResponse}。
 * 
 *  拦截器的主要入口点是{@link #intercept(HttpRequest,byte [],ClientHttpRequestExecution)}
 * 
 * 
 * @author Arjen Poutsma
 * @since 3.1
 */
public interface ClientHttpRequestInterceptor {

	/**
	 * Intercept the given request, and return a response. The given {@link ClientHttpRequestExecution} allows
	 * the interceptor to pass on the request and response to the next entity in the chain.
	 *
	 * <p>A typical implementation of this method would follow the following pattern:
	 * <ol>
	 * <li>Examine the {@linkplain HttpRequest request} and body</li>
	 * <li>Optionally {@linkplain org.springframework.http.client.support.HttpRequestWrapper wrap} the request to filter HTTP attributes.</li>
	 * <li>Optionally modify the body of the request.</li>
	 * <li><strong>Either</strong>
	 * <ul>
	 * <li>execute the request using {@link ClientHttpRequestExecution#execute(org.springframework.http.HttpRequest, byte[])},</li>
	 * <strong>or</strong>
	 * <li>do not execute the request to block the execution altogether.</li>
	 * </ul>
	 * <li>Optionally wrap the response to filter HTTP attributes.</li>
	 * </ol>
	 *
	 * <p>
	 *  拦截给定的请求并返回响应给定的{@link ClientHttpRequestExecution}允许拦截器将请求和响应传递给链中的下一个实体
	 * 
	 *  <p>此方法的典型实现将遵循以下模式：
	 * <ol>
	 * <li>检查{@linkplain HttpRequest请求}和正文</li> <li>可选{@linkplain orgspringframeworkhttpclientsupportHttpRequestWrapper wrap}
	 * 过滤HTTP属性的请求</li> <li>可选择修改请求的正文</li > <li> <strong> </strong>。
	 * <ul>
	 * 
	 * @param request the request, containing method, URI, and headers
	 * @param body the body of the request
	 * @param execution the request execution
	 * @return the response
	 * @throws IOException in case of I/O errors
	 */
	ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException;

}
