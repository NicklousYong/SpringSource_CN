/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2014 the original author or authors.
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

package org.springframework.remoting.caucho;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.springframework.lang.UsesSunHttpServer;
import org.springframework.util.FileCopyUtils;

/**
 * HTTP request handler that exports the specified service bean as
 * Hessian service endpoint, accessible via a Hessian proxy.
 * Designed for Sun's JRE 1.6 HTTP server, implementing the
 * {@link com.sun.net.httpserver.HttpHandler} interface.
 *
 * <p>Hessian is a slim, binary RPC protocol.
 * For information on Hessian, see the
 * <a href="http://www.caucho.com/hessian">Hessian website</a>.
 * <b>Note: As of Spring 4.0, this exporter requires Hessian 4.0 or above.</b>
 *
 * <p>Hessian services exported with this class can be accessed by
 * any Hessian client, as there isn't any special handling involved.
 *
 * <p>
 * 将指定的服务bean导出为Hessian服务端点的HTTP请求处理程序,可通过Hessian代理访问为Sun的JRE 16 HTTP服务器设计,实现{@link comsunnethttpserverHttpHandler}
 * 接口。
 * 
 *  <p> Hessian是一个超薄的二进制RPC协议有关Hessian的信息,请参阅<a href=\"http://wwwcauchocom/hessian\"> Hessian网站</a> <b>注
 * 意：截至春季40,该出口商要求Hessian 40以上</b>。
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.5.1
 * @see org.springframework.remoting.caucho.HessianClientInterceptor
 * @see org.springframework.remoting.caucho.HessianProxyFactoryBean
 * @see org.springframework.remoting.httpinvoker.SimpleHttpInvokerServiceExporter
 */
@UsesSunHttpServer
public class SimpleHessianServiceExporter extends HessianExporter implements HttpHandler {

	/**
	 * Processes the incoming Hessian request and creates a Hessian response.
	 * <p>
	 *  <p> Hessian服务可以由任何Hessian客户端访问,因为没有任何特殊处理
	 * 
	 */
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		if (!"POST".equals(exchange.getRequestMethod())) {
			exchange.getResponseHeaders().set("Allow", "POST");
			exchange.sendResponseHeaders(405, -1);
			return;
		}

		ByteArrayOutputStream output = new ByteArrayOutputStream(1024);
		try {
			invoke(exchange.getRequestBody(), output);
		}
		catch (Throwable ex) {
			exchange.sendResponseHeaders(500, -1);
			logger.error("Hessian skeleton invocation failed", ex);
			return;
		}

		exchange.getResponseHeaders().set("Content-Type", CONTENT_TYPE_HESSIAN);
		exchange.sendResponseHeaders(200, output.size());
		FileCopyUtils.copy(output.toByteArray(), exchange.getResponseBody());
	}

}
