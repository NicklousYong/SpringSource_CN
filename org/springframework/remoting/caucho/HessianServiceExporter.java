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
 * as Hessian service endpoint, accessible via a Hessian proxy.
 *
 * <p><b>Note:</b> Spring also provides an alternative version of this exporter,
 * for Sun's JRE 1.6 HTTP server: {@link SimpleHessianServiceExporter}.
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
 *  基于Servlet-API的HTTP请求处理程序将指定的服务bean导出为Hessian服务端点,可通过Hessian代理访问
 * 
 * <p> <b>注意：</b> Spring还提供了这个导出器的替代版本,适用于Sun的JRE 16 HTTP服务器：{@link SimpleHessianServiceExporter}
 * 
 *  <p> Hessian是一个超薄的二进制RPC协议有关Hessian的信息,请参阅<a href=\"http://wwwcauchocom/hessian\"> Hessian网站</a> <b>注
 * 意：截至春季40,该出口商要求Hessian 40以上</b>。
 * 
 * @author Juergen Hoeller
 * @since 13.05.2003
 * @see HessianClientInterceptor
 * @see HessianProxyFactoryBean
 * @see org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter
 * @see org.springframework.remoting.rmi.RmiServiceExporter
 */
public class HessianServiceExporter extends HessianExporter implements HttpRequestHandler {

	/**
	 * Processes the incoming Hessian request and creates a Hessian response.
	 * <p>
	 * 
	 *  <p> Hessian服务可以由任何Hessian客户端访问,因为没有任何特殊处理
	 * 
	 */
	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		if (!"POST".equals(request.getMethod())) {
			throw new HttpRequestMethodNotSupportedException(request.getMethod(),
					new String[] {"POST"}, "HessianServiceExporter only supports POST requests");
		}

		response.setContentType(CONTENT_TYPE_HESSIAN);
		try {
		  invoke(request.getInputStream(), response.getOutputStream());
		}
		catch (Throwable ex) {
		  throw new NestedServletException("Hessian skeleton invocation failed", ex);
		}
	}

}
