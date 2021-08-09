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

package org.springframework.remoting.jaxws;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import javax.xml.ws.WebServiceProvider;

/**
 * Simple exporter for JAX-WS services, autodetecting annotated service beans
 * (through the JAX-WS {@link javax.jws.WebService} annotation) and exporting
 * them with a configured base address (by default "http://localhost:8080/")
 * using the JAX-WS provider's built-in publication support. The full address
 * for each service will consist of the base address with the service name
 * appended (e.g. "http://localhost:8080/OrderService").
 *
 * <p>Note that this exporter will only work if the JAX-WS runtime actually
 * supports publishing with an address argument, i.e. if the JAX-WS runtime
 * ships an internal HTTP server. This is the case with the JAX-WS runtime
 * that's included in Sun's JDK 6 but not with the standalone JAX-WS 2.1 RI.
 *
 * <p>For explicit configuration of JAX-WS endpoints with Sun's JDK 6
 * HTTP server, consider using {@link SimpleHttpServerJaxWsServiceExporter}!
 *
 * <p>
 * 使用JAX-WS服务的简单导出器,通过JAX-WS {@link javaxjwsWebService}注释自动检测注释服务bean,并使用配置的基地址(默认为"http：// localhost：80
 * 80 /")导出它们-WS提供商的内置发布支持每个服务的完整地址将包含附加服务名称的基址(例如"http：// localhost：8080 / OrderService")。
 * 
 *  <p>请注意,如果JAX-WS运行时实际上支持使用地址参数发布,即JAX-WS运行时可以运行内部HTTP服务器,则此导出器将仅起作用。Sun的JDK 6而不是独立的JAX-WS 21 RI
 * 
 * <p>要使用Sun的JDK 6 HTTP服务器显式配置JAX-WS端点,请考虑使用{@link SimpleHttpServerJaxWsServiceExporter}！
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.5
 * @see javax.jws.WebService
 * @see javax.xml.ws.Endpoint#publish(String)
 * @see SimpleHttpServerJaxWsServiceExporter
 */
public class SimpleJaxWsServiceExporter extends AbstractJaxWsServiceExporter {

	public static final String DEFAULT_BASE_ADDRESS = "http://localhost:8080/";

	private String baseAddress = DEFAULT_BASE_ADDRESS;


	/**
	 * Set the base address for exported services.
	 * Default is "http://localhost:8080/".
	 * <p>For each actual publication address, the service name will be
	 * appended to this base address. E.g. service name "OrderService"
	 * -> "http://localhost:8080/OrderService".
	 * <p>
	 * 
	 * @see javax.xml.ws.Endpoint#publish(String)
	 * @see javax.jws.WebService#serviceName()
	 */
	public void setBaseAddress(String baseAddress) {
		this.baseAddress = baseAddress;
	}


	@Override
	protected void publishEndpoint(Endpoint endpoint, WebService annotation) {
		endpoint.publish(calculateEndpointAddress(endpoint, annotation.serviceName()));
	}

	@Override
	protected void publishEndpoint(Endpoint endpoint, WebServiceProvider annotation) {
		endpoint.publish(calculateEndpointAddress(endpoint, annotation.serviceName()));
	}

	/**
	 * Calculate the full endpoint address for the given endpoint.
	 * <p>
	 *  设置导出服务的基址默认值为"http：// localhost：8080 /"<p>对于每个实际的发布地址,服务名称将附加到该基地址,例如服务名称"OrderService" - >"http： /本
	 * 地主机：8080 / OrderService"。
	 * 
	 * 
	 * @param endpoint the JAX-WS Provider Endpoint object
	 * @param serviceName the given service name
	 * @return the full endpoint address
	 */
	protected String calculateEndpointAddress(Endpoint endpoint, String serviceName) {
		String fullAddress = this.baseAddress + serviceName;
		if (endpoint.getClass().getName().startsWith("weblogic.")) {
			// Workaround for WebLogic 10.3
			fullAddress = fullAddress + "/";
		}
		return fullAddress;
	}

}
