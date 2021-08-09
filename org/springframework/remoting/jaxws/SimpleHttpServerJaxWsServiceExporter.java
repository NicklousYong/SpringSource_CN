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

package org.springframework.remoting.jaxws;

import java.net.InetSocketAddress;
import java.util.List;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import javax.xml.ws.WebServiceProvider;

import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.lang.UsesSunHttpServer;

/**
 * Simple exporter for JAX-WS services, autodetecting annotated service beans
 * (through the JAX-WS {@link javax.jws.WebService} annotation) and exporting
 * them through the HTTP server included in Sun's JDK 1.6. The full address
 * for each service will consist of the server's base address with the
 * service name appended (e.g. "http://localhost:8080/OrderService").
 *
 * <p>Note that this exporter will only work on Sun's JDK 1.6 or higher, as well
 * as on JDKs that ship Sun's entire class library as included in the Sun JDK.
 * For a portable JAX-WS exporter, have a look at {@link SimpleJaxWsServiceExporter}.
 *
 * <p>
 * 用于JAX-WS服务的简单导出器,通过JAX-WS {@link javaxjwsWebService}注释自动检测注释服务bean,并通过Sun JDK 16中包含的HTTP服务器导出它们每个服务的完
 * 整地址将包含服务器的基址附加服务名称(例如"http：// localhost：8080 / OrderService")。
 * 
 *  <p>请注意,此导出程序仅适用于Sun的JDK 16或更高版本,以及Sun Sun所包含的Sun的整个类库的JDK对于便携式JAX-WS导出器,请查看{@link SimpleJaxWsServiceExporter }
 * 。
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.5.5
 * @see javax.jws.WebService
 * @see javax.xml.ws.Endpoint#publish(Object)
 * @see SimpleJaxWsServiceExporter
 */
@UsesSunHttpServer
public class SimpleHttpServerJaxWsServiceExporter extends AbstractJaxWsServiceExporter {

	protected final Log logger = LogFactory.getLog(getClass());

	private HttpServer server;

	private int port = 8080;

	private String hostname;

	private int backlog = -1;

	private int shutdownDelay = 0;

	private String basePath = "/";

	private List<Filter> filters;

	private Authenticator authenticator;

	private boolean localServer = false;


	/**
	 * Specify an existing HTTP server to register the web service contexts
	 * with. This will typically be a server managed by the general Spring
	 * {@link org.springframework.remoting.support.SimpleHttpServerFactoryBean}.
	 * <p>Alternatively, configure a local HTTP server through the
	 * {@link #setPort "port"}, {@link #setHostname "hostname"} and
	 * {@link #setBacklog "backlog"} properties (or rely on the defaults there).
	 * <p>
	 * 指定一个现有的HTTP服务器来注册Web服务上下文这通常是由一般Spring管理的服务器{@link orgspringframeworkremotingsupportSimpleHttpServerFactoryBean}
	 *  <p>或者,通过{@link #setPort"端口"}配置本地HTTP服务器, {@link #setHostname"hostname"}和{@link #setBacklog"backlog"}
	 * 属性(或依赖于默认值)。
	 * 
	 */
	public void setServer(HttpServer server) {
		this.server = server;
	}

	/**
	 * Specify the HTTP server's port. Default is 8080.
	 * <p>Only applicable for a locally configured HTTP server.
	 * Ignored when the {@link #setServer "server"} property has been specified.
	 * <p>
	 *  指定HTTP服务器的端口默认值为8080 <p>仅适用于本地配置的HTTP服务器当指定了{@link #setServer"server"}属性时被忽略
	 * 
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Specify the HTTP server's hostname to bind to. Default is localhost;
	 * can be overridden with a specific network address to bind to.
	 * <p>Only applicable for a locally configured HTTP server.
	 * Ignored when the {@link #setServer "server"} property has been specified.
	 * <p>
	 * 指定要绑定的HTTP服务器的主机名为localhost;可以用特定的网络地址覆盖以绑定到<p>仅适用于本地配置的HTTP服务器当指定了{@link #setServer"服务器"}属性时被忽略
	 * 
	 */
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	/**
	 * Specify the HTTP server's TCP backlog. Default is -1,
	 * indicating the system's default value.
	 * <p>Only applicable for a locally configured HTTP server.
	 * Ignored when the {@link #setServer "server"} property has been specified.
	 * <p>
	 *  指定HTTP服务器的TCP备份默认值为-1,表示系统的默认值<p>仅适用于本地配置的HTTP服务器当指定了{@link #setServer"服务器"}属性时被忽略
	 * 
	 */
	public void setBacklog(int backlog) {
		this.backlog = backlog;
	}

	/**
	 * Specify the number of seconds to wait until HTTP exchanges have
	 * completed when shutting down the HTTP server. Default is 0.
	 * <p>Only applicable for a locally configured HTTP server.
	 * Ignored when the {@link #setServer "server"} property has been specified.
	 * <p>
	 *  指定在关闭HTTP服务器之前等待HTTP交换完成的秒数默认值为0 <p>仅适用于本地配置的HTTP服务器当指定了{@link #setServer"server"}属性时被忽略
	 * 
	 */
	public void setShutdownDelay(int shutdownDelay) {
		this.shutdownDelay = shutdownDelay;
	}

	/**
	 * Set the base path for context publication. Default is "/".
	 * <p>For each context publication path, the service name will be
	 * appended to this base address. E.g. service name "OrderService"
	 * -> "/OrderService".
	 * <p>
	 * 设置上下文发布的基本路径默认为"/"<p>对于每个上下文发布路径,服务名称将被附加到该基地址,例如服务名称"OrderService" - >"/ OrderService"
	 * 
	 * 
	 * @see javax.xml.ws.Endpoint#publish(Object)
	 * @see javax.jws.WebService#serviceName()
	 */
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	/**
	 * Register common {@link com.sun.net.httpserver.Filter Filters} to be
	 * applied to all detected {@link javax.jws.WebService} annotated beans.
	 * <p>
	 *  注册常用的{@link comsunnethttpserverFilter Filters}以应用于所有检测到的{@link javaxjwsWebService}注释的bean
	 * 
	 */
	public void setFilters(List<Filter> filters) {
		this.filters = filters;
	}

	/**
	 * Register a common {@link com.sun.net.httpserver.Authenticator} to be
	 * applied to all detected {@link javax.jws.WebService} annotated beans.
	 * <p>
	 *  注册一个常见的{@link comsunnethttpserverAuthenticator}来应用于所有检测到的{@link javaxjwsWebService}注释的bean
	 * 
	 */
	public void setAuthenticator(Authenticator authenticator) {
		this.authenticator = authenticator;
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		if (this.server == null) {
			InetSocketAddress address = (this.hostname != null ?
					new InetSocketAddress(this.hostname, this.port) : new InetSocketAddress(this.port));
			this.server = HttpServer.create(address, this.backlog);
			if (this.logger.isInfoEnabled()) {
				this.logger.info("Starting HttpServer at address " + address);
			}
			this.server.start();
			this.localServer = true;
		}
		super.afterPropertiesSet();
	}

	@Override
	protected void publishEndpoint(Endpoint endpoint, WebService annotation) {
		endpoint.publish(buildHttpContext(endpoint, annotation.serviceName()));
	}

	@Override
	protected void publishEndpoint(Endpoint endpoint, WebServiceProvider annotation) {
		endpoint.publish(buildHttpContext(endpoint, annotation.serviceName()));
	}

	/**
	 * Build the HttpContext for the given endpoint.
	 * <p>
	 *  构建给定端点的HttpContext
	 * 
	 * 
	 * @param endpoint the JAX-WS Provider Endpoint object
	 * @param serviceName the given service name
	 * @return the fully populated HttpContext
	 */
	protected HttpContext buildHttpContext(Endpoint endpoint, String serviceName) {
		String fullPath = calculateEndpointPath(endpoint, serviceName);
		HttpContext httpContext = this.server.createContext(fullPath);
		if (this.filters != null) {
			httpContext.getFilters().addAll(this.filters);
		}
		if (this.authenticator != null) {
			httpContext.setAuthenticator(this.authenticator);
		}
		return httpContext;
	}

	/**
	 * Calculate the full endpoint path for the given endpoint.
	 * <p>
	 *  计算给定端点的完整端点路径
	 * 
	 * @param endpoint the JAX-WS Provider Endpoint object
	 * @param serviceName the given service name
	 * @return the full endpoint path
	 */
	protected String calculateEndpointPath(Endpoint endpoint, String serviceName) {
		return this.basePath + serviceName;
	}


	@Override
	public void destroy() {
		super.destroy();
		if (this.localServer) {
			logger.info("Stopping HttpServer");
			this.server.stop(this.shutdownDelay);
		}
	}

}
