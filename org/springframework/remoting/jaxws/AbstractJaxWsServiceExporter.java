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

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.WebServiceProvider;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.CannotLoadBeanClassException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.lang.UsesJava7;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * Abstract exporter for JAX-WS services, autodetecting annotated service beans
 * (through the JAX-WS {@link javax.jws.WebService} annotation). Compatible with
 * JAX-WS 2.1 and 2.2, as included in JDK 6 update 4+ and Java 7/8.
 *
 * <p>Subclasses need to implement the {@link #publishEndpoint} template methods
 * for actual endpoint exposure.
 *
 * <p>
 *  JAX-WS服务的抽象导出器,自动检测注释服务bean(通过JAX-WS {@link javaxjwsWebService}注释)兼容JAX-WS 21和22,包含在JDK 6 update 4+和
 * Java 7/8中。
 * 
 * <p>子类需要实现实际端点曝光的{@link #publishEndpoint}模板方法
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.5.5
 * @see javax.jws.WebService
 * @see javax.xml.ws.Endpoint
 * @see SimpleJaxWsServiceExporter
 * @see SimpleHttpServerJaxWsServiceExporter
 */
public abstract class AbstractJaxWsServiceExporter implements BeanFactoryAware, InitializingBean, DisposableBean {

	private Map<String, Object> endpointProperties;

	private Executor executor;

	private String bindingType;

	private WebServiceFeature[] endpointFeatures;

	private Object[] webServiceFeatures;

	private ListableBeanFactory beanFactory;

	private final Set<Endpoint> publishedEndpoints = new LinkedHashSet<Endpoint>();


	/**
	 * Set the property bag for the endpoint, including properties such as
	 * "javax.xml.ws.wsdl.service" or "javax.xml.ws.wsdl.port".
	 * <p>
	 *  设置端点的属性包,包括诸如"javaxxmlwswsdlservice"或"javaxxmlwswsdlport"之类的属性
	 * 
	 * 
	 * @see javax.xml.ws.Endpoint#setProperties
	 * @see javax.xml.ws.Endpoint#WSDL_SERVICE
	 * @see javax.xml.ws.Endpoint#WSDL_PORT
	 */
	public void setEndpointProperties(Map<String, Object> endpointProperties) {
		this.endpointProperties = endpointProperties;
	}

	/**
	 * Set the JDK concurrent executor to use for dispatching incoming requests
	 * to exported service instances.
	 * <p>
	 *  将JDK并发执行程序设置为用于将传入请求分派到导出的服务实例
	 * 
	 * 
	 * @see javax.xml.ws.Endpoint#setExecutor
	 */
	public void setExecutor(Executor executor) {
		this.executor = executor;
	}

	/**
	 * Specify the binding type to use, overriding the value of
	 * the JAX-WS {@link javax.xml.ws.BindingType} annotation.
	 * <p>
	 *  指定要使用的绑定类型,覆盖JAX-WS {@link javaxxmlwsBindingType}注释的值
	 * 
	 */
	public void setBindingType(String bindingType) {
		this.bindingType = bindingType;
	}

	/**
	 * Specify WebServiceFeature objects (e.g. as inner bean definitions)
	 * to apply to JAX-WS endpoint creation.
	 * <p>
	 *  指定WebServiceFeature对象(例如作为内部bean定义)应用于JAX-WS端点创建
	 * 
	 * 
	 * @since 4.0
	 */
	public void setEndpointFeatures(WebServiceFeature... endpointFeatures) {
		this.endpointFeatures = endpointFeatures;
	}

	/**
	 * Allows for providing JAX-WS 2.2 WebServiceFeature specifications:
	 * in the form of actual {@link javax.xml.ws.WebServiceFeature} objects,
	 * WebServiceFeature Class references, or WebServiceFeature class names.
	 * <p>As of Spring 4.0, this is effectively just an alternative way of
	 * specifying {@link #setEndpointFeatures "endpointFeatures"}. Do not specify
	 * both properties at the same time; prefer "endpointFeatures" moving forward.
	 * <p>
	 * 允许提供JAX-WS 22 WebServiceFeature规范：以实际的{@link javaxxmlwsWebServiceFeature}对象,WebServiceFeature Class引用
	 * 或WebServiceFeature类名称的形式<p>从Spring 40开始,这实际上只是指定{@link #setEndpointFeatures"endpointFeatures"}不要同时指定两
	 * 个属性;倾向于"终点特征"。
	 * 
	 * 
	 * @deprecated as of Spring 4.0, in favor of {@link #setEndpointFeatures}
	 */
	@Deprecated
	public void setWebServiceFeatures(Object[] webServiceFeatures) {
		this.webServiceFeatures = webServiceFeatures;
	}

	/**
	 * Obtains all web service beans and publishes them as JAX-WS endpoints.
	 * <p>
	 *  获取所有Web服务bean并将其作为JAX-WS端点发布
	 * 
	 */
	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		if (!(beanFactory instanceof ListableBeanFactory)) {
			throw new IllegalStateException(getClass().getSimpleName() + " requires a ListableBeanFactory");
		}
		this.beanFactory = (ListableBeanFactory) beanFactory;
	}


	/**
	 * Immediately publish all endpoints when fully configured.
	 * <p>
	 *  完全配置后立即发布所有端点
	 * 
	 * 
	 * @see #publishEndpoints()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		publishEndpoints();
	}

	/**
	 * Publish all {@link javax.jws.WebService} annotated beans in the
	 * containing BeanFactory.
	 * <p>
	 *  在包含的BeanFactory中发布所有{@link javaxjwsWebService}注释的bean
	 * 
	 * 
	 * @see #publishEndpoint
	 */
	public void publishEndpoints() {
		Set<String> beanNames = new LinkedHashSet<String>(this.beanFactory.getBeanDefinitionCount());
		beanNames.addAll(Arrays.asList(this.beanFactory.getBeanDefinitionNames()));
		if (this.beanFactory instanceof ConfigurableBeanFactory) {
			beanNames.addAll(Arrays.asList(((ConfigurableBeanFactory) this.beanFactory).getSingletonNames()));
		}
		for (String beanName : beanNames) {
			try {
				Class<?> type = this.beanFactory.getType(beanName);
				if (type != null && !type.isInterface()) {
					WebService wsAnnotation = type.getAnnotation(WebService.class);
					WebServiceProvider wsProviderAnnotation = type.getAnnotation(WebServiceProvider.class);
					if (wsAnnotation != null || wsProviderAnnotation != null) {
						Endpoint endpoint = createEndpoint(this.beanFactory.getBean(beanName));
						if (this.endpointProperties != null) {
							endpoint.setProperties(this.endpointProperties);
						}
						if (this.executor != null) {
							endpoint.setExecutor(this.executor);
						}
						if (wsAnnotation != null) {
							publishEndpoint(endpoint, wsAnnotation);
						}
						else {
							publishEndpoint(endpoint, wsProviderAnnotation);
						}
						this.publishedEndpoints.add(endpoint);
					}
				}
			}
			catch (CannotLoadBeanClassException ex) {
				// ignore beans where the class is not resolvable
			}
		}
	}

	/**
	 * Create the actual Endpoint instance.
	 * <p>
	 *  创建实际的端点实例
	 * 
	 * 
	 * @param bean the service object to wrap
	 * @return the Endpoint instance
	 * @see Endpoint#create(Object)
	 * @see Endpoint#create(String, Object)
	 */
	@UsesJava7  // optional use of Endpoint#create with WebServiceFeature[]
	protected Endpoint createEndpoint(Object bean) {
		if (this.endpointFeatures != null || this.webServiceFeatures != null) {
			WebServiceFeature[] endpointFeaturesToUse = this.endpointFeatures;
			if (endpointFeaturesToUse == null) {
				endpointFeaturesToUse = new WebServiceFeature[this.webServiceFeatures.length];
				for (int i = 0; i < this.webServiceFeatures.length; i++) {
					endpointFeaturesToUse[i] = convertWebServiceFeature(this.webServiceFeatures[i]);
				}
			}
			return Endpoint.create(this.bindingType, bean, endpointFeaturesToUse);
		}
		else {
			return Endpoint.create(this.bindingType, bean);
		}
	}

	private WebServiceFeature convertWebServiceFeature(Object feature) {
		Assert.notNull(feature, "WebServiceFeature specification object must not be null");
		if (feature instanceof WebServiceFeature) {
			return (WebServiceFeature) feature;
		}
		else if (feature instanceof Class) {
			return (WebServiceFeature) BeanUtils.instantiate((Class<?>) feature);
		}
		else if (feature instanceof String) {
			try {
				Class<?> featureClass = getBeanClassLoader().loadClass((String) feature);
				return (WebServiceFeature) BeanUtils.instantiate(featureClass);
			}
			catch (ClassNotFoundException ex) {
				throw new IllegalArgumentException("Could not load WebServiceFeature class [" + feature + "]");
			}
		}
		else {
			throw new IllegalArgumentException("Unknown WebServiceFeature specification type: " + feature.getClass());
		}
	}

	private ClassLoader getBeanClassLoader() {
		return (beanFactory instanceof ConfigurableBeanFactory ?
				((ConfigurableBeanFactory) beanFactory).getBeanClassLoader() : ClassUtils.getDefaultClassLoader());
	}


	/**
	 * Actually publish the given endpoint. To be implemented by subclasses.
	 * <p>
	 *  实际发布给定的端点要由子类实现
	 * 
	 * 
	 * @param endpoint the JAX-WS Endpoint object
	 * @param annotation the service bean's WebService annotation
	 */
	protected abstract void publishEndpoint(Endpoint endpoint, WebService annotation);

	/**
	 * Actually publish the given provider endpoint. To be implemented by subclasses.
	 * <p>
	 * 实际发布给定的提供者端点要由子类实现
	 * 
	 * 
	 * @param endpoint the JAX-WS Provider Endpoint object
	 * @param annotation the service bean's WebServiceProvider annotation
	 */
	protected abstract void publishEndpoint(Endpoint endpoint, WebServiceProvider annotation);


	/**
	 * Stops all published endpoints, taking the web services offline.
	 * <p>
	 *  停止所有发布的端点,使Web服务脱机
	 */
	@Override
	public void destroy() {
		for (Endpoint endpoint : this.publishedEndpoints) {
			endpoint.stop();
		}
	}

}
