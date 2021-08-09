/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2012 the original author or authors.
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

package org.springframework.jmx.support;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.target.AbstractLazyCreationTargetSource;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

/**
 * {@link FactoryBean} that creates a JMX 1.2 {@code MBeanServerConnection}
 * to a remote {@code MBeanServer} exposed via a {@code JMXServerConnector}.
 * Exposes the {@code MBeanServer} for bean references.
 *
 * <p>
 *  {@link FactoryBean}为通过{@code JMXServerConnector}公开的远程{@code MBeanServer}创建了一个JMX 12 {@code MBeanServerConnection}
 * 。
 * 为Bean引用了{@code MBeanServer}。
 * 
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 1.2
 * @see MBeanServerFactoryBean
 * @see ConnectorServerFactoryBean
 * @see org.springframework.jmx.access.MBeanClientInterceptor#setServer
 * @see org.springframework.jmx.access.NotificationListenerRegistrar#setServer
 */
public class MBeanServerConnectionFactoryBean
		implements FactoryBean<MBeanServerConnection>, BeanClassLoaderAware, InitializingBean, DisposableBean {

	private JMXServiceURL serviceUrl;

	private Map<String, Object> environment = new HashMap<String, Object>();

	private boolean connectOnStartup = true;

	private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

	private JMXConnector connector;

	private MBeanServerConnection connection;

	private JMXConnectorLazyInitTargetSource connectorTargetSource;


	/**
	 * Set the service URL of the remote {@code MBeanServer}.
	 * <p>
	 * 设置远程{@code MBeanServer}的服务URL
	 * 
	 */
	public void setServiceUrl(String url) throws MalformedURLException {
		this.serviceUrl = new JMXServiceURL(url);
	}

	/**
	 * Set the environment properties used to construct the {@code JMXConnector}
	 * as {@code java.util.Properties} (String key/value pairs).
	 * <p>
	 *  将用于构造{@code JMXConnector}的环境属性设置为{@code javautilProperties}(String key / value pairs)
	 * 
	 */
	public void setEnvironment(Properties environment) {
		CollectionUtils.mergePropertiesIntoMap(environment, this.environment);
	}

	/**
	 * Set the environment properties used to construct the {@code JMXConnector}
	 * as a {@code Map} of String keys and arbitrary Object values.
	 * <p>
	 *  将用于构造{@code JMXConnector}的环境属性设置为字符串键和任意对象值的{@code Map}
	 * 
	 */
	public void setEnvironmentMap(Map<String, ?> environment) {
		if (environment != null) {
			this.environment.putAll(environment);
		}
	}

	/**
	 * Set whether to connect to the server on startup. Default is "true".
	 * <p>Can be turned off to allow for late start of the JMX server.
	 * In this case, the JMX connector will be fetched on first access.
	 * <p>
	 *  设置是否在启动时连接到服务器默认为"true"<p>可以关闭以允许JMX服务器的后期启动在这种情况下,JMX连接器将在第一次访问时获取
	 * 
	 */
	public void setConnectOnStartup(boolean connectOnStartup) {
		this.connectOnStartup = connectOnStartup;
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.beanClassLoader = classLoader;
	}


	/**
	 * Creates a {@code JMXConnector} for the given settings
	 * and exposes the associated {@code MBeanServerConnection}.
	 * <p>
	 *  为给定的设置创建一个{@code JMXConnector}并公开相关的{@code MBeanServerConnection}
	 * 
	 */
	@Override
	public void afterPropertiesSet() throws IOException {
		if (this.serviceUrl == null) {
			throw new IllegalArgumentException("Property 'serviceUrl' is required");
		}

		if (this.connectOnStartup) {
			connect();
		}
		else {
			createLazyConnection();
		}
	}

	/**
	 * Connects to the remote {@code MBeanServer} using the configured service URL and
	 * environment properties.
	 * <p>
	 *  使用配置的服务URL和环境属性连接到远程{@code MBeanServer}
	 * 
	 */
	private void connect() throws IOException {
		this.connector = JMXConnectorFactory.connect(this.serviceUrl, this.environment);
		this.connection = this.connector.getMBeanServerConnection();
	}

	/**
	 * Creates lazy proxies for the {@code JMXConnector} and {@code MBeanServerConnection}
	 * <p>
	 * 为{@code JMXConnector}和{@code MBeanServerConnection}创建懒惰代理
	 * 
	 */
	private void createLazyConnection() {
		this.connectorTargetSource = new JMXConnectorLazyInitTargetSource();
		TargetSource connectionTargetSource = new MBeanServerConnectionLazyInitTargetSource();

		this.connector = (JMXConnector)
				new ProxyFactory(JMXConnector.class, this.connectorTargetSource).getProxy(this.beanClassLoader);
		this.connection = (MBeanServerConnection)
				new ProxyFactory(MBeanServerConnection.class, connectionTargetSource).getProxy(this.beanClassLoader);
	}


	@Override
	public MBeanServerConnection getObject() {
		return this.connection;
	}

	@Override
	public Class<? extends MBeanServerConnection> getObjectType() {
		return (this.connection != null ? this.connection.getClass() : MBeanServerConnection.class);
	}

	@Override
	public boolean isSingleton() {
		return true;
	}


	/**
	 * Closes the underlying {@code JMXConnector}.
	 * <p>
	 *  关闭底层的{@code JMXConnector}
	 * 
	 */
	@Override
	public void destroy() throws IOException {
		if (this.connectorTargetSource == null || this.connectorTargetSource.isInitialized()) {
			this.connector.close();
		}
	}


	/**
	 * Lazily creates a {@code JMXConnector} using the configured service URL
	 * and environment properties.
	 * <p>
	 *  Lazily使用配置的服务URL和环境属性创建一个{@code JMXConnector}
	 * 
	 * 
	 * @see MBeanServerConnectionFactoryBean#setServiceUrl(String)
	 * @see MBeanServerConnectionFactoryBean#setEnvironment(java.util.Properties)
	 */
	private class JMXConnectorLazyInitTargetSource extends AbstractLazyCreationTargetSource {

		@Override
		protected Object createObject() throws Exception {
			return JMXConnectorFactory.connect(serviceUrl, environment);
		}

		@Override
		public Class<?> getTargetClass() {
			return JMXConnector.class;
		}
	}


	/**
	 * Lazily creates an {@code MBeanServerConnection}.
	 * <p>
	 *  Lazily创建一个{@code MBeanServerConnection}
	 */
	private class MBeanServerConnectionLazyInitTargetSource extends AbstractLazyCreationTargetSource {

		@Override
		protected Object createObject() throws Exception {
			return connector.getMBeanServerConnection();
		}

		@Override
		public Class<?> getTargetClass() {
			return MBeanServerConnection.class;
		}
	}

}
