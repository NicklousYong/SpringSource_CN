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

package org.springframework.remoting.httpinvoker;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.remoting.rmi.RemoteInvocationSerializingExporter;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationResult;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.util.NestedServletException;

/**
 * Servlet-API-based HTTP request handler that exports the specified service bean
 * as HTTP invoker service endpoint, accessible via an HTTP invoker proxy.
 *
 * <p><b>Note:</b> Spring also provides an alternative version of this exporter,
 * for Sun's JRE 1.6 HTTP server: {@link SimpleHttpInvokerServiceExporter}.
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
 * <p><b>WARNING: Be aware of vulnerabilities due to unsafe Java deserialization:
 * Manipulated input streams could lead to unwanted code execution on the server
 * during the deserialization step. As a consequence, do not expose HTTP invoker
 * endpoints to untrusted clients but rather just between your own services.</b>
 *
 * <p>
 *  基于Servlet-API的HTTP请求处理程序将指定的服务bean导出为HTTP调用服务端点,可通过HTTP调用器代理访问
 * 
 * <p> <b>注意：</b> Spring还为Sun的JRE 16 HTTP服务器提供了此导出程序的替代版本：{@link SimpleHttpInvokerServiceExporter}
 * 
 *  反序列化远程调用对象并序列化远程调用结果对象像RMI一样使用Java序列化,但提供了与Caucho基于HTTP的Hessian和Burlap协议相同的设置方式
 * 
 *  <p> <b> HTTP调用器是Java到Java远程处理的推荐协议</b>它比Hessian和Burlap更强大,更可扩展,而不考虑与Java的关系尽管如此,它同样容易成立为Hessian和Burl
 * ap,这是与RMI相比的主要优势。
 * 
 * 警告：请注意由于不安全的Java反序列化引起的漏洞：操作的输入流可能导致在反序列化步骤期间在服务器上执行不必要的代码因此,不要将HTTP调用者端点暴露给不受信任的客户端,而只是您自己的服务</b>之间。
 * 
 * 
 * @author Juergen Hoeller
 * @since 1.1
 * @see HttpInvokerClientInterceptor
 * @see HttpInvokerProxyFactoryBean
 * @see org.springframework.remoting.rmi.RmiServiceExporter
 * @see org.springframework.remoting.caucho.HessianServiceExporter
 * @see org.springframework.remoting.caucho.BurlapServiceExporter
 */
public class HttpInvokerServiceExporter extends RemoteInvocationSerializingExporter
		implements HttpRequestHandler {

	/**
	 * Reads a remote invocation from the request, executes it,
	 * and writes the remote invocation result to the response.
	 * <p>
	 *  从请求中读取远程调用,执行它,并将远程调用结果写入响应
	 * 
	 * 
	 * @see #readRemoteInvocation(HttpServletRequest)
	 * @see #invokeAndCreateResult(org.springframework.remoting.support.RemoteInvocation, Object)
	 * @see #writeRemoteInvocationResult(HttpServletRequest, HttpServletResponse, RemoteInvocationResult)
	 */
	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			RemoteInvocation invocation = readRemoteInvocation(request);
			RemoteInvocationResult result = invokeAndCreateResult(invocation, getProxy());
			writeRemoteInvocationResult(request, response, result);
		}
		catch (ClassNotFoundException ex) {
			throw new NestedServletException("Class not found during deserialization", ex);
		}
	}

	/**
	 * Read a RemoteInvocation from the given HTTP request.
	 * <p>Delegates to
	 * {@link #readRemoteInvocation(javax.servlet.http.HttpServletRequest, java.io.InputStream)}
	 * with the
	 * {@link javax.servlet.ServletRequest#getInputStream() servlet request's input stream}.
	 * <p>
	 *  从给定的HTTP请求中读取RemoteInvocation <p>使用{@link javaxservletServletRequest#getInputStream()servlet请求的输入流)将{@link #readRemoteInvocation(javaxservlethttpHttpServletRequest,javaioInputStream)。
	 * 
	 * 
	 * @param request current HTTP request
	 * @return the RemoteInvocation object
	 * @throws IOException in case of I/O failure
	 * @throws ClassNotFoundException if thrown by deserialization
	 */
	protected RemoteInvocation readRemoteInvocation(HttpServletRequest request)
			throws IOException, ClassNotFoundException {

		return readRemoteInvocation(request, request.getInputStream());
	}

	/**
	 * Deserialize a RemoteInvocation object from the given InputStream.
	 * <p>Gives {@link #decorateInputStream} a chance to decorate the stream
	 * first (for example, for custom encryption or compression). Creates a
	 * {@link org.springframework.remoting.rmi.CodebaseAwareObjectInputStream}
	 * and calls {@link #doReadRemoteInvocation} to actually read the object.
	 * <p>Can be overridden for custom serialization of the invocation.
	 * <p>
	 * 从给定的InputStream反序列化RemoteInvocation对象<p>使{@link #decorateInputStream}有机会首先装饰流(例如,用于自定义加密或压缩)创建{@link orgspringframeworkremotingrmiCodebaseAwareObjectInputStream}
	 * 并调用{@link #doReadRemoteInvocation}实际读取对象<p>可以覆盖调用的自定义序列化。
	 * 
	 * 
	 * @param request current HTTP request
	 * @param is the InputStream to read from
	 * @return the RemoteInvocation object
	 * @throws IOException in case of I/O failure
	 * @throws ClassNotFoundException if thrown during deserialization
	 */
	protected RemoteInvocation readRemoteInvocation(HttpServletRequest request, InputStream is)
			throws IOException, ClassNotFoundException {

		ObjectInputStream ois = createObjectInputStream(decorateInputStream(request, is));
		try {
			return doReadRemoteInvocation(ois);
		}
		finally {
			ois.close();
		}
	}

	/**
	 * Return the InputStream to use for reading remote invocations,
	 * potentially decorating the given original InputStream.
	 * <p>The default implementation returns the given stream as-is.
	 * Can be overridden, for example, for custom encryption or compression.
	 * <p>
	 *  返回InputStream以用于读取远程调用,可能装饰给定的原始InputStream <p>默认实现返回给定流,因为可以被覆盖,例如用于自定义加密或压缩
	 * 
	 * 
	 * @param request current HTTP request
	 * @param is the original InputStream
	 * @return the potentially decorated InputStream
	 * @throws IOException in case of I/O failure
	 */
	protected InputStream decorateInputStream(HttpServletRequest request, InputStream is) throws IOException {
		return is;
	}

	/**
	 * Write the given RemoteInvocationResult to the given HTTP response.
	 * <p>
	 *  将给定的RemoteInvocationResult写入给定的HTTP响应
	 * 
	 * 
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param result the RemoteInvocationResult object
	 * @throws IOException in case of I/O failure
	 */
	protected void writeRemoteInvocationResult(
			HttpServletRequest request, HttpServletResponse response, RemoteInvocationResult result)
			throws IOException {

		response.setContentType(getContentType());
		writeRemoteInvocationResult(request, response, result, response.getOutputStream());
	}

	/**
	 * Serialize the given RemoteInvocation to the given OutputStream.
	 * <p>The default implementation gives {@link #decorateOutputStream} a chance
	 * to decorate the stream first (for example, for custom encryption or compression).
	 * Creates an {@link java.io.ObjectOutputStream} for the final stream and calls
	 * {@link #doWriteRemoteInvocationResult} to actually write the object.
	 * <p>Can be overridden for custom serialization of the invocation.
	 * <p>
	 * 将给定的RemoteInvocation序列化到给定的OutputStream <p>默认实现使{@link #decorateOutputStream}有机会首先装饰流(例如,用于自定义加密或压缩)为
	 * 最终流创建一个{@link javaioObjectOutputStream}调用{@link #doWriteRemoteInvocationResult}来实际写入对象<p>可以覆盖调用的自定义序列
	 * 化。
	 * 
	 * 
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param result the RemoteInvocationResult object
	 * @param os the OutputStream to write to
	 * @throws IOException in case of I/O failure
	 * @see #decorateOutputStream
	 * @see #doWriteRemoteInvocationResult
	 */
	protected void writeRemoteInvocationResult(
			HttpServletRequest request, HttpServletResponse response, RemoteInvocationResult result, OutputStream os)
			throws IOException {

		ObjectOutputStream oos =
				createObjectOutputStream(new FlushGuardedOutputStream(decorateOutputStream(request, response, os)));
		try {
			doWriteRemoteInvocationResult(result, oos);
		}
		finally {
			oos.close();
		}
	}

	/**
	 * Return the OutputStream to use for writing remote invocation results,
	 * potentially decorating the given original OutputStream.
	 * <p>The default implementation returns the given stream as-is.
	 * Can be overridden, for example, for custom encryption or compression.
	 * <p>
	 *  返回OutputStream用于编写远程调用结果,可能装饰给定的原始OutputStream <p>默认实现返回给定的流,因为可以被覆盖,例如自定义加密或压缩
	 * 
	 * 
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param os the original OutputStream
	 * @return the potentially decorated OutputStream
	 * @throws IOException in case of I/O failure
	 */
	protected OutputStream decorateOutputStream(
			HttpServletRequest request, HttpServletResponse response, OutputStream os) throws IOException {

		return os;
	}


	/**
	 * Decorate an {@code OutputStream} to guard against {@code flush()} calls,
	 * which are turned into no-ops.
	 *
	 * <p>Because {@link ObjectOutputStream#close()} will in fact flush/drain
	 * the underlying stream twice, this {@link FilterOutputStream} will
	 * guard against individual flush calls. Multiple flush calls can lead
	 * to performance issues, since writes aren't gathered as they should be.
	 *
	 * <p>
	 *  装饰一个{@code OutputStream}来防范{@code flush()}调用,这些调用变成了no-ops
	 * 
	 * <p>因为{@link ObjectOutputStream#close()}实际上会冲洗/排除底层流两次,这个{@link FilterOutputStream}将防范单个刷新调用多次刷新调用可能会导
	 * 
	 * @see <a href="https://jira.spring.io/browse/SPR-14040">SPR-14040</a>
	 */
	private static class FlushGuardedOutputStream extends FilterOutputStream {

		public FlushGuardedOutputStream(OutputStream out) {
			super(out);
		}

		@Override
		public void flush() throws IOException {
			// Do nothing on flush
		}
	}

}
