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

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Executor;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.handler.HandlerResolver;

import org.springframework.core.io.Resource;
import org.springframework.lang.UsesJava7;
import org.springframework.util.Assert;

/**
 * Factory for locally defined JAX-WS {@link javax.xml.ws.Service} references.
 * Uses the JAX-WS {@link javax.xml.ws.Service#create} factory API underneath.
 *
 * <p>Serves as base class for {@link LocalJaxWsServiceFactoryBean} as well as
 * {@link JaxWsPortClientInterceptor} and {@link JaxWsPortProxyFactoryBean}.
 *
 * <p>
 *  用于本地定义的JAX-WS的工厂{@link javaxxmlwsService}引用使用下面的JAX-WS {@link javaxxmlwsService#create}工厂API
 * 
 * <p>作为{@link LocalJaxWsServiceFactoryBean}的基类以及{@link JaxWsPortClientInterceptor}和{@link JaxWsPortProxyFactoryBean}
 * 。
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.5
 * @see javax.xml.ws.Service
 * @see LocalJaxWsServiceFactoryBean
 * @see JaxWsPortClientInterceptor
 * @see JaxWsPortProxyFactoryBean
 */
public class LocalJaxWsServiceFactory {

	private URL wsdlDocumentUrl;

	private String namespaceUri;

	private String serviceName;

	private WebServiceFeature[] serviceFeatures;

	private Executor executor;

	private HandlerResolver handlerResolver;


	/**
	 * Set the URL of the WSDL document that describes the service.
	 * <p>
	 *  设置描述服务的WSDL文档的URL
	 * 
	 * 
	 * @see #setWsdlDocumentResource(Resource)
	 */
	public void setWsdlDocumentUrl(URL wsdlDocumentUrl) {
		this.wsdlDocumentUrl = wsdlDocumentUrl;
	}

	/**
	 * Set the WSDL document URL as a {@link Resource}.
	 * <p>
	 *  将WSDL文档URL设置为{@link资源}
	 * 
	 * 
	 * @throws IOException
	 * @since 3.2
	 */
	public void setWsdlDocumentResource(Resource wsdlDocumentResource) throws IOException {
		Assert.notNull(wsdlDocumentResource, "WSDL Resource must not be null.");
		this.wsdlDocumentUrl = wsdlDocumentResource.getURL();
	}

	/**
	 * Return the URL of the WSDL document that describes the service.
	 * <p>
	 *  返回描述该服务的WSDL文档的URL
	 * 
	 */
	public URL getWsdlDocumentUrl() {
		return this.wsdlDocumentUrl;
	}

	/**
	 * Set the namespace URI of the service.
	 * Corresponds to the WSDL "targetNamespace".
	 * <p>
	 *  设置服务的命名空间URI对应于WSDL"targetNamespace"
	 * 
	 */
	public void setNamespaceUri(String namespaceUri) {
		this.namespaceUri = (namespaceUri != null ? namespaceUri.trim() : null);
	}

	/**
	 * Return the namespace URI of the service.
	 * <p>
	 *  返回服务的命名空间URI
	 * 
	 */
	public String getNamespaceUri() {
		return this.namespaceUri;
	}

	/**
	 * Set the name of the service to look up.
	 * Corresponds to the "wsdl:service" name.
	 * <p>
	 *  设置要查找的服务的名称对应于"wsdl：service"名称
	 * 
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	/**
	 * Return the name of the service.
	 * <p>
	 *  返回服务的名称
	 * 
	 */
	public String getServiceName() {
		return this.serviceName;
	}

	/**
	 * Specify WebServiceFeature objects (e.g. as inner bean definitions)
	 * to apply to JAX-WS service creation.
	 * <p>Note: This mechanism requires JAX-WS 2.2 or higher.
	 * <p>
	 *  指定WebServiceFeature对象(例如作为内部bean定义)应用于JAX-WS服务创建<p>注意：此机制需要JAX-WS 22或更高版本
	 * 
	 * 
	 * @since 4.0
	 * @see Service#create(QName, WebServiceFeature...)
	 */
	public void setServiceFeatures(WebServiceFeature... serviceFeatures) {
		this.serviceFeatures = serviceFeatures;
	}

	/**
	 * Set the JDK concurrent executor to use for asynchronous executions
	 * that require callbacks.
	 * <p>
	 * 将JDK并发执行程序设置为需要回调的异步执行
	 * 
	 * 
	 * @see javax.xml.ws.Service#setExecutor
	 */
	public void setExecutor(Executor executor) {
		this.executor = executor;
	}

	/**
	 * Set the JAX-WS HandlerResolver to use for all proxies and dispatchers
	 * created through this factory.
	 * <p>
	 *  将JAX-WS HandlerResolver设置为用于通过此工厂创建的所有代理和调度程序
	 * 
	 * 
	 * @see javax.xml.ws.Service#setHandlerResolver
	 */
	public void setHandlerResolver(HandlerResolver handlerResolver) {
		this.handlerResolver = handlerResolver;
	}


	/**
	 * Create a JAX-WS Service according to the parameters of this factory.
	 * <p>
	 *  根据本厂的参数创建JAX-WS服务
	 * 
	 * 
	 * @see #setServiceName
	 * @see #setWsdlDocumentUrl
	 */
	@UsesJava7  // optional use of Service#create with WebServiceFeature[]
	public Service createJaxWsService() {
		Assert.notNull(this.serviceName, "No service name specified");
		Service service;

		if (this.serviceFeatures != null) {
			service = (this.wsdlDocumentUrl != null ?
				Service.create(this.wsdlDocumentUrl, getQName(this.serviceName), this.serviceFeatures) :
				Service.create(getQName(this.serviceName), this.serviceFeatures));
		}
		else {
			service = (this.wsdlDocumentUrl != null ?
					Service.create(this.wsdlDocumentUrl, getQName(this.serviceName)) :
					Service.create(getQName(this.serviceName)));
		}

		if (this.executor != null) {
			service.setExecutor(this.executor);
		}
		if (this.handlerResolver != null) {
			service.setHandlerResolver(this.handlerResolver);
		}

		return service;
	}

	/**
	 * Return a QName for the given name, relative to the namespace URI
	 * of this factory, if given.
	 * <p>
	 *  如果给出,返回相对于此工厂的命名空间URI的给定名称的QName
	 * 
	 * @see #setNamespaceUri
	 */
	protected QName getQName(String name) {
		return (getNamespaceUri() != null ? new QName(getNamespaceUri(), name) : new QName(name));
	}

}
