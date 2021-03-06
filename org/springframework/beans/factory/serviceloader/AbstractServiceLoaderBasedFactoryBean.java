/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2007 the original author or authors.
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

package org.springframework.beans.factory.serviceloader;

import java.util.ServiceLoader;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * Abstract base class for FactoryBeans operating on the
 * JDK 1.6 {@link java.util.ServiceLoader} facility.
 *
 * <p>
 *  在JDK 16 {@link javautilServiceLoader}工具上运行的FactoryBeans的抽象基类
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.5
 * @see java.util.ServiceLoader
 */
public abstract class AbstractServiceLoaderBasedFactoryBean extends AbstractFactoryBean<Object>
		implements BeanClassLoaderAware {

	private Class<?> serviceType;

	private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();


	/**
	 * Specify the desired service type (typically the service's public API).
	 * <p>
	 *  指定所需的服务类型(通常是服务的公共API)
	 * 
	 */
	public void setServiceType(Class<?> serviceType) {
		this.serviceType = serviceType;
	}

	/**
	 * Return the desired service type.
	 * <p>
	 *  返回所需的服务类型
	 * 
	 */
	public Class<?> getServiceType() {
		return this.serviceType;
	}

	@Override
	public void setBeanClassLoader(ClassLoader beanClassLoader) {
		this.beanClassLoader = beanClassLoader;
	}


	/**
	 * Delegates to {@link #getObjectToExpose(java.util.ServiceLoader)}.
	 * <p>
	 * 代表{@link #getObjectToExpose(javautilServiceLoader)}
	 * 
	 * 
	 * @return the object to expose
	 */
	@Override
	protected Object createInstance() {
		Assert.notNull(getServiceType(), "Property 'serviceType' is required");
		return getObjectToExpose(ServiceLoader.load(getServiceType(), this.beanClassLoader));
	}

	/**
	 * Determine the actual object to expose for the given ServiceLoader.
	 * <p>Left to concrete subclasses.
	 * <p>
	 *  确定给定ServiceLoader <p>的实际对象暴露给具体的子类
	 * 
	 * @param serviceLoader the ServiceLoader for the configured service class
	 * @return the object to expose
	 */
	protected abstract Object getObjectToExpose(ServiceLoader<?> serviceLoader);

}
