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

package org.springframework.remoting.httpinvoker;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.springframework.lang.UsesSunHttpServer;
import org.springframework.remoting.rmi.RemoteInvocationSerializingExporter;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationResult;

/**
 * HTTP request handler that exports the specified service bean as
 * HTTP invoker service endpoint, accessible via an HTTP invoker proxy.
 * Designed for Sun's JRE 1.6 HTTP server, implementing the
 * {@link com.sun.net.httpserver.HttpHandler} interface.
 *
 * <p>Deserializes remote invocation objects and serializes remote invocation
 * result objects. Uses Java serialization just like RMI, but provides the
 * same ease of setup as Caucho's HTTP-based Hessian and Burlap protocols.
 *
 * <p><b>HTTP invoker is the recommended protocol for Java-to-Java remoting.</b>
 * It is more powerful and more extensible than Hessian and Burlap, at the
 * expense of being tied to Java. Nevertheless, it is as easy to set up as
 * Hessian and Burlap, which is its main advantage compared to RMI.
 *
 * <p>
 * HTTP请求处理程序,将指定的服务bean导出为HTTP调用服务端点,可通过HTTP调用器代理访问为Sun的JRE 16 HTTP服务器设计,实现{@link comsunnethttpserverHttpHandler}
 * 接口。
 * 
 *  反序列化远程调用对象并序列化远程调用结果对象像RMI一样使用Java序列化,但提供了与Caucho基于HTTP的Hessian和Burlap协议相同的设置方式
 * 
 *  <p> <b> HTTP调用器是Java到Java远程处理的推荐协议</b>它比Hessian和Burlap更强大,更可扩展,而不考虑与Java的关系尽管如此,它同样容易成立为Hessian和Burl
 * ap,这是与RMI相比的主要优势。
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.5.1
 * @see org.springframework.remoting.httpinvoker.HttpInvokerClientInterceptor
 * @see org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean
 * @see org.springframework.remoting.caucho.SimpleHessianServiceExporter
 * @see org.springframework.remoting.caucho.SimpleBurlapServiceExporter
 */
@UsesSunHttpServer
public class SimpleHttpInvokerServiceExporter extends RemoteInvocationSerializingExporter
		implements HttpHandler {

	/**
	 * Reads a remote invocation from the request, executes it,
	 * and writes the remote invocation result to the response.
	 * <p>
	 * 从请求中读取远程调用,执行它,并将远程调用结果写入响应
	 * 
	 * 
	 * @see #readRemoteInvocation(com.sun.net.httpserver.HttpExchange)
	 * @see #invokeAndCreateResult(org.springframework.remoting.support.RemoteInvocation, Object)
	 * @see #writeRemoteInvocationResult(com.sun.net.httpserver.HttpExchange, org.springframework.remoting.support.RemoteInvocationResult)
	 */
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		try {
			RemoteInvocation invocation = readRemoteInvocation(exchange);
			RemoteInvocationResult result = invokeAndCreateResult(invocation, getProxy());
			writeRemoteInvocationResult(exchange, result);
			exchange.close();
		}
		catch (ClassNotFoundException ex) {
			exchange.sendResponseHeaders(500, -1);
			logger.error("Class not found during deserialization", ex);
		}
	}

	/**
	 * Read a RemoteInvocation from the given HTTP request.
	 * <p>Delegates to
	 * {@link #readRemoteInvocation(com.sun.net.httpserver.HttpExchange, java.io.InputStream)}
	 * with the
	 * {@link com.sun.net.httpserver.HttpExchange#getRequestBody()} request's input stream}.
	 * <p>
	 *  从给定的HTTP请求中读取RemoteInvocation <p>使用{@link comsunnethttpserverHttpExchange#getRequestBody()}请求的输入流将{@link #readRemoteInvocation(comsunnethttpserverHttpExchange,javaioInputStream)}
	 * 委托给}。
	 * 
	 * 
	 * @param exchange current HTTP request/response
	 * @return the RemoteInvocation object
	 * @throws java.io.IOException in case of I/O failure
	 * @throws ClassNotFoundException if thrown by deserialization
	 */
	protected RemoteInvocation readRemoteInvocation(HttpExchange exchange)
			throws IOException, ClassNotFoundException {

		return readRemoteInvocation(exchange, exchange.getRequestBody());
	}

	/**
	 * Deserialize a RemoteInvocation object from the given InputStream.
	 * <p>Gives {@link #decorateInputStream} a chance to decorate the stream
	 * first (for example, for custom encryption or compression). Creates a
	 * {@link org.springframework.remoting.rmi.CodebaseAwareObjectInputStream}
	 * and calls {@link #doReadRemoteInvocation} to actually read the object.
	 * <p>Can be overridden for custom serialization of the invocation.
	 * <p>
	 *  从给定的InputStream反序列化RemoteInvocation对象<p>使{@link #decorateInputStream}有机会首先装饰流(例如,用于自定义加密或压缩)创建{@link orgspringframeworkremotingrmiCodebaseAwareObjectInputStream}
	 * 并调用{@link #doReadRemoteInvocation}实际读取对象<p>可以覆盖调用的自定义序列化。
	 * 
	 * 
	 * @param exchange current HTTP request/response
	 * @param is the InputStream to read from
	 * @return the RemoteInvocation object
	 * @throws java.io.IOException in case of I/O failure
	 * @throws ClassNotFoundException if thrown during deserialization
	 */
	protected RemoteInvocation readRemoteInvocation(HttpExchange exchange, InputStream is)
			throws IOException, ClassNotFoundException {

		ObjectInputStream ois = createObjectInputStream(decorateInputStream(exchange, is));
		return doReadRemoteInvocation(ois);
	}

	/**
	 * Return the InputStream to use for reading remote invocations,
	 * potentially decorating the given original InputStream.
	 * <p>The default implementation returns the given stream as-is.
	 * Can be overridden, for example, for custom encryption or compression.
	 * <p>
	 * 返回InputStream以用于读取远程调用,可能装饰给定的原始InputStream <p>默认实现返回给定流,因为可以被覆盖,例如用于自定义加密或压缩
	 * 
	 * 
	 * @param exchange current HTTP request/response
	 * @param is the original InputStream
	 * @return the potentially decorated InputStream
	 * @throws java.io.IOException in case of I/O failure
	 */
	protected InputStream decorateInputStream(HttpExchange exchange, InputStream is) throws IOException {
		return is;
	}

	/**
	 * Write the given RemoteInvocationResult to the given HTTP response.
	 * <p>
	 *  将给定的RemoteInvocationResult写入给定的HTTP响应
	 * 
	 * 
	 * @param exchange current HTTP request/response
	 * @param result the RemoteInvocationResult object
	 * @throws java.io.IOException in case of I/O failure
	 */
	protected void writeRemoteInvocationResult(HttpExchange exchange, RemoteInvocationResult result)
			throws IOException {

		exchange.getResponseHeaders().set("Content-Type", getContentType());
		exchange.sendResponseHeaders(200, 0);
		writeRemoteInvocationResult(exchange, result, exchange.getResponseBody());
	}

	/**
	 * Serialize the given RemoteInvocation to the given OutputStream.
	 * <p>The default implementation gives {@link #decorateOutputStream} a chance
	 * to decorate the stream first (for example, for custom encryption or compression).
	 * Creates an {@link java.io.ObjectOutputStream} for the final stream and calls
	 * {@link #doWriteRemoteInvocationResult} to actually write the object.
	 * <p>Can be overridden for custom serialization of the invocation.
	 * <p>
	 *  将给定的RemoteInvocation序列化到给定的OutputStream <p>默认实现使{@link #decorateOutputStream}有机会首先装饰流(例如,用于自定义加密或压缩)
	 * 为最终流创建一个{@link javaioObjectOutputStream}调用{@link #doWriteRemoteInvocationResult}来实际写入对象<p>可以覆盖调用的自定义序
	 * 列化。
	 * 
	 * 
	 * @param exchange current HTTP request/response
	 * @param result the RemoteInvocationResult object
	 * @param os the OutputStream to write to
	 * @throws java.io.IOException in case of I/O failure
	 * @see #decorateOutputStream
	 * @see #doWriteRemoteInvocationResult
	 */
	protected void writeRemoteInvocationResult(
			HttpExchange exchange, RemoteInvocationResult result, OutputStream os) throws IOException {

		ObjectOutputStream oos = createObjectOutputStream(decorateOutputStream(exchange, os));
		doWriteRemoteInvocationResult(result, oos);
		oos.flush();
	}

	/**
	 * Return the OutputStream to use for writing remote invocation results,
	 * potentially decorating the given original OutputStream.
	 * <p>The default implementation returns the given stream as-is.
	 * Can be overridden, for example, for custom encryption or compression.
	 * <p>
	 * 
	 * @param exchange current HTTP request/response
	 * @param os the original OutputStream
	 * @return the potentially decorated OutputStream
	 * @throws java.io.IOException in case of I/O failure
	 */
	protected OutputStream decorateOutputStream(HttpExchange exchange, OutputStream os) throws IOException {
		return os;
	}

}
