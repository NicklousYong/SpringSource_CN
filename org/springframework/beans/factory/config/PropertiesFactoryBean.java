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

package org.springframework.beans.factory.config;

import java.io.IOException;
import java.util.Properties;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.support.PropertiesLoaderSupport;

/**
 * Allows for making a properties file from a classpath location available
 * as Properties instance in a bean factory. Can be used to populate
 * any bean property of type Properties via a bean reference.
 *
 * <p>Supports loading from a properties file and/or setting local properties
 * on this FactoryBean. The created Properties instance will be merged from
 * loaded and local values. If neither a location nor local properties are set,
 * an exception will be thrown on initialization.
 *
 * <p>Can create a singleton or a new object on each request.
 * Default is a singleton.
 *
 * <p>
 *  允许从类路径位置创建属性文件,作为bean工厂中的属性实例可以用于通过bean引用来填充类型为Properties的bean属性
 * 
 * <p>支持从属性文件加载和/或在此FactoryBean上设置本地属性创建的属性实例将从加载和本地值合并如果既不设置位置又不设置本地属性,则初始化时将抛出异常
 * 
 *  <p>可以在每个请求上创建一个单例或一个新对象默认是单例
 * 
 * 
 * @author Juergen Hoeller
 * @see #setLocation
 * @see #setProperties
 * @see #setLocalOverride
 * @see java.util.Properties
 */
public class PropertiesFactoryBean extends PropertiesLoaderSupport
		implements FactoryBean<Properties>, InitializingBean {

	private boolean singleton = true;

	private Properties singletonInstance;


	/**
	 * Set whether a shared 'singleton' Properties instance should be
	 * created, or rather a new Properties instance on each request.
	 * <p>Default is "true" (a shared singleton).
	 * <p>
	 */
	public final void setSingleton(boolean singleton) {
		this.singleton = singleton;
	}

	@Override
	public final boolean isSingleton() {
		return this.singleton;
	}


	@Override
	public final void afterPropertiesSet() throws IOException {
		if (this.singleton) {
			this.singletonInstance = createProperties();
		}
	}

	@Override
	public final Properties getObject() throws IOException {
		if (this.singleton) {
			return this.singletonInstance;
		}
		else {
			return createProperties();
		}
	}

	@Override
	public Class<Properties> getObjectType() {
		return Properties.class;
	}


	/**
	 * Template method that subclasses may override to construct the object
	 * returned by this factory. The default implementation returns the
	 * plain merged Properties instance.
	 * <p>Invoked on initialization of this FactoryBean in case of a
	 * shared singleton; else, on each {@link #getObject()} call.
	 * <p>
	 *  设置是否应该创建一个共享的'singleton'属性实例,或者是每个请求上的一个新的Properties实例<p> Default是"true"(共享单例)
	 * 
	 * 
	 * @return the object returned by this factory
	 * @throws IOException if an exception occurred during properties loading
	 * @see #mergeProperties()
	 */
	protected Properties createProperties() throws IOException {
		return mergeProperties();
	}

}
