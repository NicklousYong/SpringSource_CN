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

import org.springframework.http.HttpRequest;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * Represents the context of a client-side HTTP request execution.
 *
 * <p>Used to invoke the next interceptor in the interceptor chain, or -
 * if the calling interceptor is last - execute the request itself.
 *
 * <p>
 * 
 * @author Jakub Narloch
 * @author Rossen Stoyanchev
 * @since 4.3
 * @see AsyncClientHttpRequestInterceptor
 */
public interface AsyncClientHttpRequestExecution {

    /**
     * Resume the request execution by invoking the next interceptor in the chain
     * or executing the request to the remote service.
     * <p>
     *  表示客户端HTTP请求执行的上下文
     * 
     *  <p>用于调用拦截器链中的下一个拦截器,或者 - 如果调用拦截器是最后一个 - 执行请求本身
     * 
     * 
     * @param request the HTTP request, containing the HTTP method and headers
     * @param body the body of the request
     * @return a corresponding future handle
     * @throws IOException in case of I/O errors
     */
    ListenableFuture<ClientHttpResponse> executeAsync(HttpRequest request, byte[] body) throws IOException;

}
