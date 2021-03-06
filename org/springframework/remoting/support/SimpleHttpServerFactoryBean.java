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

package org.springframework.remoting.support;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.UsesSunHttpServer;

/**
 * {@link org.springframework.beans.factory.FactoryBean} that creates a simple
 * HTTP server, based on the HTTP server that is included in Sun's JRE 1.6.
 * Starts the HTTP server on initialization and stops it on destruction.
 * Exposes the resulting {@link com.sun.net.httpserver.HttpServer} object.
 *
 * <p>Allows for registering {@link com.sun.net.httpserver.HttpHandler HttpHandlers}
 * for specific {@link #setContexts context paths}. Alternatively,
 * register such context-specific handlers programmatically on the
 * {@link com.sun.net.httpserver.HttpServer} itself.
 *
 * <p>
 * {@link orgspringframeworkbeansfactoryFactoryBean},创建一个简单的HTTP服务器,基于Sun的JRE中包含的HTTP服务器16在初始化时启动HTTP服务器
 * 并在销毁时停止它暴露产生的{@link comsunnethttpserverHttpServer}对象。
 * 
 *  <p>允许为特定的{@link #setContexts上下文路径}注册{@link comsunnethttpserverHttpHandler HttpHandler}或者,通过编程方式在{@link comsunnethttpserverHttpServer}
 * 本身上注册上下文特定的处理程序。
 * 
 * 
 * @author Juergen Hoeller
 * @author Arjen Poutsma
 * @since 2.5.1
 * @see #setPort
 * @see #setContexts
 */
@UsesSunHttpServer
public class SimpleHttpServerFactoryBean implements FactoryBean<HttpServer>, InitializingBean, DisposableBean {

	protected final Log logger = LogFactory.getLog(getClass());

	private int port = 8080;

	private String hostname;

	private int backlog = -1;

	private int shutdownDelay = 0;

	private Executor executor;

	private Map<String, HttpHandler> contexts;

	private List<Filter> filters;

	private Authenticator authenticator;

	private HttpServer server;


	/**
	 * Specify the HTTP server's port. Default is 8080.
	 * <p>
	 *  指定HTTP服务器的端口默认值为8080
	 * 
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Specify the HTTP server's hostname to bind to. Default is localhost;
	 * can be overridden with a specific network address to bind to.
	 * <p>
	 *  指定要绑定的HTTP服务器的主机名为localhost;可以用特定的网络地址覆盖来绑定
	 * 
	 */
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	/**
	 * Specify the HTTP server's TCP backlog. Default is -1,
	 * indicating the system's default value.
	 * <p>
	 * 指定HTTP服务器的TCP备份默认值为-1,表示系统的默认值
	 * 
	 */
	public void setBacklog(int backlog) {
		this.backlog = backlog;
	}

	/**
	 * Specify the number of seconds to wait until HTTP exchanges have
	 * completed when shutting down the HTTP server. Default is 0.
	 * <p>
	 *  指定在关闭HTTP服务器之前等待HTTP交换完成的秒数默认值为0
	 * 
	 */
	public void setShutdownDelay(int shutdownDelay) {
		this.shutdownDelay = shutdownDelay;
	}

	/**
	 * Set the JDK concurrent executor to use for dispatching incoming requests.
	 * <p>
	 *  设置用于调度传入请求的JDK并发执行程序
	 * 
	 * 
	 * @see com.sun.net.httpserver.HttpServer#setExecutor
	 */
	public void setExecutor(Executor executor) {
		this.executor = executor;
	}

	/**
	 * Register {@link com.sun.net.httpserver.HttpHandler HttpHandlers}
	 * for specific context paths.
	 * <p>
	 *  为特定的上下文路径注册{@link comsunnethttpserverHttpHandler HttpHandler}
	 * 
	 * 
	 * @param contexts a Map with context paths as keys and HttpHandler
	 * objects as values
	 * @see org.springframework.remoting.httpinvoker.SimpleHttpInvokerServiceExporter
	 * @see org.springframework.remoting.caucho.SimpleHessianServiceExporter
	 * @see org.springframework.remoting.caucho.SimpleBurlapServiceExporter
	 */
	public void setContexts(Map<String, HttpHandler> contexts) {
		this.contexts = contexts;
	}

	/**
	 * Register common {@link com.sun.net.httpserver.Filter Filters} to be
	 * applied to all locally registered {@link #setContexts contexts}.
	 * <p>
	 *  注册常用的{@link comsunnethttpserverFilter Filters}以适用于所有本地注册的{@link #setContexts上下文}
	 * 
	 */
	public void setFilters(List<Filter> filters) {
		this.filters = filters;
	}

	/**
	 * Register a common {@link com.sun.net.httpserver.Authenticator} to be
	 * applied to all locally registered {@link #setContexts contexts}.
	 * <p>
	 *  注册一个常见的{@link comsunnethttpserverAuthenticator}以应用于所有本地注册的{@link #setContexts上下文}
	 */
	public void setAuthenticator(Authenticator authenticator) {
		this.authenticator = authenticator;
	}


	@Override
	public void afterPropertiesSet() throws IOException {
		InetSocketAddress address = (this.hostname != null ?
				new InetSocketAddress(this.hostname, this.port) : new InetSocketAddress(this.port));
		this.server = HttpServer.create(address, this.backlog);
		if (this.executor != null) {
			this.server.setExecutor(this.executor);
		}
		if (this.contexts != null) {
			for (String key : this.contexts.keySet()) {
				HttpContext httpContext = this.server.createContext(key, this.contexts.get(key));
				if (this.filters != null) {
					httpContext.getFilters().addAll(this.filters);
				}
				if (this.authenticator != null) {
					httpContext.setAuthenticator(this.authenticator);
				}
			}
		}
		if (this.logger.isInfoEnabled()) {
			this.logger.info("Starting HttpServer at address " + address);
		}
		this.server.start();
	}

	@Override
	public HttpServer getObject() {
		return this.server;
	}

	@Override
	public Class<? extends HttpServer> getObjectType() {
		return (this.server != null ? this.server.getClass() : HttpServer.class);
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void destroy() {
		logger.info("Stopping HttpServer");
		this.server.stop(this.shutdownDelay);
	}

}
