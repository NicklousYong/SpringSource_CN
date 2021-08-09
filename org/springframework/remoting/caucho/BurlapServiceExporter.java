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

package org.springframework.remoting.caucho;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.HttpRequestHandler;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.util.NestedServletException;

/**
 * Servlet-API-based HTTP request handler that exports the specified service bean
 * as Burlap service endpoint, accessible via a Burlap proxy.
 *
 * <p><b>Note:</b> Spring also provides an alternative version of this exporter,
 * for Sun's JRE 1.6 HTTP server: {@link SimpleBurlapServiceExporter}.
 *
 * <p>Burlap is a slim, XML-based RPC protocol.
 * For information on Burlap, see the
 * <a href="http://www.caucho.com/burlap">Burlap website</a>.
 * This exporter requires Burlap 3.x.
 *
 * <p>Note: Burlap services exported with this class can be accessed by
 * any Burlap client, as there isn't any special handling involved.
 *
 * <p>
 *  基于Servlet-API的HTTP请求处理程序将指定的服务bean导出为Burlap服务端点,可通过Burlap代理访问
 * 
 * <p> <b>注意：</b> Spring还提供了这个导出器的替代版本,适用于Sun的JRE 16 HTTP服务器：{@link SimpleBurlapServiceExporter}
 * 
 *  <p> Burlap是一个纤薄的基于XML的RPC协议有关Burlap的信息,请参阅<a href=\"http://wwwcauchocom/burlap\"> Burlap网站</a>此出口商需要
 * Burlap 3x。
 * 
 * @author Juergen Hoeller
 * @since 13.05.2003
 * @see BurlapClientInterceptor
 * @see BurlapProxyFactoryBean
 * @see org.springframework.remoting.caucho.HessianServiceExporter
 * @see org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter
 * @see org.springframework.remoting.rmi.RmiServiceExporter
 * @deprecated as of Spring 4.0, since Burlap hasn't evolved in years
 * and is effectively retired (in contrast to its sibling Hessian)
 */
@Deprecated
public class BurlapServiceExporter extends BurlapExporter implements HttpRequestHandler {

	/**
	 * Processes the incoming Burlap request and creates a Burlap response.
	 * <p>
	 * 
	 *  注意：使用此类导出的重叠服务可由任何Burlap客户端访问,因为没有任何特殊处理
	 * 
	 */
	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		if (!"POST".equals(request.getMethod())) {
			throw new HttpRequestMethodNotSupportedException(request.getMethod(),
					new String[] {"POST"}, "BurlapServiceExporter only supports POST requests");
		}

		try {
		  invoke(request.getInputStream(), response.getOutputStream());
		}
		catch (Throwable ex) {
		  throw new NestedServletException("Burlap skeleton invocation failed", ex);
		}
	}

}
