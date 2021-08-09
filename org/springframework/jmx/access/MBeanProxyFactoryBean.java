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

package org.springframework.jmx.access;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jmx.MBeanServerNotFoundException;
import org.springframework.util.ClassUtils;

/**
 * Creates a proxy to a managed resource running either locally or remotely.
 * The "proxyInterface" property defines the interface that the generated
 * proxy is supposed to implement. This interface should define methods and
 * properties that correspond to operations and attributes in the management
 * interface of the resource you wish to proxy.
 *
 * <p>There is no need for the managed resource to implement the proxy interface,
 * although you may find it convenient to do. It is not required that every
 * operation and attribute in the management interface is matched by a
 * corresponding property or method in the proxy interface.
 *
 * <p>Attempting to invoke or access any method or property on the proxy
 * interface that does not correspond to the management interface will lead
 * to an {@code InvalidInvocationException}.
 *
 * <p>
 * 为本地或远程运行的托管资源创建代理"proxyInterface"属性定义生成的代理应实现的接口该接口应定义与所需资源的管理界面中的操作和属性相对应的方法和属性代理
 * 
 *  <p>管理资源不需要实现代理接口,尽管您可能会发现方便的做法不需要管理接口中的每个操作和属性都通过代理接口中相应的属性或方法进行匹配
 * 
 * <p>尝试调用或访问代理接口上与管理界面不对应的任何方法或属性将导致{@code InvalidInvocationException}
 * 
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 1.2
 * @see MBeanClientInterceptor
 * @see InvalidInvocationException
 */
public class MBeanProxyFactoryBean extends MBeanClientInterceptor
		implements FactoryBean<Object>, BeanClassLoaderAware, InitializingBean {

	private Class<?> proxyInterface;

	private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

	private Object mbeanProxy;


	/**
	 * Set the interface that the generated proxy will implement.
	 * <p>This will usually be a management interface that matches the target MBean,
	 * exposing bean property setters and getters for MBean attributes and
	 * conventional Java methods for MBean operations.
	 * <p>
	 * 
	 * @see #setObjectName
	 */
	public void setProxyInterface(Class<?> proxyInterface) {
		this.proxyInterface = proxyInterface;
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.beanClassLoader = classLoader;
	}

	/**
	 * Checks that the {@code proxyInterface} has been specified and then
	 * generates the proxy for the target MBean.
	 * <p>
	 *  设置生成的代理将实现的接口<p>这通常是与目标MBean匹配的管理界面,为MBean操作显示MBean属性和常规Java方法的bean属性设置器和getter
	 * 
	 */
	@Override
	public void afterPropertiesSet() throws MBeanServerNotFoundException, MBeanInfoRetrievalException {
		super.afterPropertiesSet();

		if (this.proxyInterface == null) {
			this.proxyInterface = getManagementInterface();
			if (this.proxyInterface == null) {
				throw new IllegalArgumentException("Property 'proxyInterface' or 'managementInterface' is required");
			}
		}
		else {
			if (getManagementInterface() == null) {
				setManagementInterface(this.proxyInterface);
			}
		}
		this.mbeanProxy = new ProxyFactory(this.proxyInterface, this).getProxy(this.beanClassLoader);
	}


	@Override
	public Object getObject() {
		return this.mbeanProxy;
	}

	@Override
	public Class<?> getObjectType() {
		return this.proxyInterface;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
