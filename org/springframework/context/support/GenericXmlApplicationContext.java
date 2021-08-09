/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2013 the original author or authors.
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

package org.springframework.context.support;

import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Convenient application context with built-in XML support.
 * This is a flexible alternative to {@link ClassPathXmlApplicationContext}
 * and {@link FileSystemXmlApplicationContext}, to be configured via setters,
 * with an eventual {@link #refresh()} call activating the context.
 *
 * <p>In case of multiple configuration files, bean definitions in later files
 * will override those defined in earlier files. This can be leveraged to
 * deliberately override certain bean definitions via an extra configuration file.
 *
 * <p>
 * 具有内置XML支持的方便的应用程序上下文这是通过设置器进行配置的{@link ClassPathXmlApplicationContext}和{@link FileSystemXmlApplicationContext}
 * 的灵活替代方法,最终{@link #refresh())调用激活上下文。
 * 
 *  <p>在多个配置文件的情况下,稍后文件中的bean定义将覆盖早期文件中定义的那些。可以通过额外的配置文件故意覆盖某些bean定义
 * 
 * 
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 3.0
 * @see #load
 * @see XmlBeanDefinitionReader
 * @see org.springframework.context.annotation.AnnotationConfigApplicationContext
 */
public class GenericXmlApplicationContext extends GenericApplicationContext {

	private final XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(this);


	/**
	 * Create a new GenericXmlApplicationContext that needs to be
	 * {@link #load loaded} and then manually {@link #refresh refreshed}.
	 * <p>
	 *  创建一个新的GenericXmlApplicationContext,需要{@link #load loaded}然后手动{@link #refresh refreshs}
	 * 
	 */
	public GenericXmlApplicationContext() {
	}

	/**
	 * Create a new GenericXmlApplicationContext, loading bean definitions
	 * from the given resources and automatically refreshing the context.
	 * <p>
	 *  创建一个新的GenericXmlApplicationContext,从给定的资源加载bean定义并自动刷新上下文
	 * 
	 * 
	 * @param resources the resources to load from
	 */
	public GenericXmlApplicationContext(Resource... resources) {
		load(resources);
		refresh();
	}

	/**
	 * Create a new GenericXmlApplicationContext, loading bean definitions
	 * from the given resource locations and automatically refreshing the context.
	 * <p>
	 * 创建一个新的GenericXmlApplicationContext,从给定的资源位置加载bean定义并自动刷新上下文
	 * 
	 * 
	 * @param resourceLocations the resources to load from
	 */
	public GenericXmlApplicationContext(String... resourceLocations) {
		load(resourceLocations);
		refresh();
	}

	/**
	 * Create a new GenericXmlApplicationContext, loading bean definitions
	 * from the given resource locations and automatically refreshing the context.
	 * <p>
	 *  创建一个新的GenericXmlApplicationContext,从给定的资源位置加载bean定义并自动刷新上下文
	 * 
	 * 
	 * @param relativeClass class whose package will be used as a prefix when
	 * loading each specified resource name
	 * @param resourceNames relatively-qualified names of resources to load
	 */
	public GenericXmlApplicationContext(Class<?> relativeClass, String... resourceNames) {
		load(relativeClass, resourceNames);
		refresh();
	}


	/**
	 * Exposes the underlying {@link XmlBeanDefinitionReader} for additional
	 * configuration facilities and {@code loadBeanDefinition} variations.
	 * <p>
	 *  暴露基础的{@link XmlBeanDefinitionReader}以获得其他配置功能和{@code loadBeanDefinition}变体
	 * 
	 */
	public final XmlBeanDefinitionReader getReader() {
		return this.reader;
	}

	/**
	 * Set whether to use XML validation. Default is {@code true}.
	 * <p>
	 *  设置是否使用XML验证默认是{@code true}
	 * 
	 */
	public void setValidating(boolean validating) {
		this.reader.setValidating(validating);
	}

	/**
	 * Delegates the given environment to underlying {@link XmlBeanDefinitionReader}.
	 * Should be called before any call to {@code #load}.
	 * <p>
	 *  将给定的环境委托给底层的{@link XmlBeanDefinitionReader}在任何调用{@code #load}之前都应该调用它
	 * 
	 */
	@Override
	public void setEnvironment(ConfigurableEnvironment environment) {
		super.setEnvironment(environment);
		this.reader.setEnvironment(getEnvironment());
	}

	/**
	 * Load bean definitions from the given XML resources.
	 * <p>
	 *  从给定的XML资源加载bean定义
	 * 
	 * 
	 * @param resources one or more resources to load from
	 */
	public void load(Resource... resources) {
		this.reader.loadBeanDefinitions(resources);
	}

	/**
	 * Load bean definitions from the given XML resources.
	 * <p>
	 *  从给定的XML资源加载bean定义
	 * 
	 * 
	 * @param resourceLocations one or more resource locations to load from
	 */
	public void load(String... resourceLocations) {
		this.reader.loadBeanDefinitions(resourceLocations);
	}

	/**
	 * Load bean definitions from the given XML resources.
	 * <p>
	 *  从给定的XML资源加载bean定义
	 * 
	 * @param relativeClass class whose package will be used as a prefix when
	 * loading each specified resource name
	 * @param resourceNames relatively-qualified names of resources to load
	 */
	public void load(Class<?> relativeClass, String... resourceNames) {
		Resource[] resources = new Resource[resourceNames.length];
		for (int i = 0; i < resourceNames.length; i++) {
			resources[i] = new ClassPathResource(resourceNames[i], relativeClass);
		}
		this.load(resources);
	}

}
