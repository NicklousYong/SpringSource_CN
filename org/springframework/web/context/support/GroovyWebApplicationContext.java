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

package org.springframework.web.context.support;

import java.io.IOException;

import groovy.lang.GroovyObject;
import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.groovy.GroovyBeanDefinitionReader;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * {@link org.springframework.web.context.WebApplicationContext} implementation which takes
 * its configuration from Groovy bean definition scripts and/or XML files, as understood by
 * an {@link org.springframework.beans.factory.groovy.GroovyBeanDefinitionReader}.
 * This is essentially the equivalent of
 * {@link org.springframework.context.support.GenericGroovyApplicationContext}
 * for a web environment.
 *
 * <p>By default, the configuration will be taken from "/WEB-INF/applicationContext.groovy"
 * for the root context, and "/WEB-INF/test-servlet.groovy" for a context with the namespace
 * "test-servlet" (like for a DispatcherServlet instance with the servlet-name "test").
 *
 * <p>The config location defaults can be overridden via the "contextConfigLocation"
 * context-param of {@link org.springframework.web.context.ContextLoader} and servlet
 * init-param of {@link org.springframework.web.servlet.FrameworkServlet}. Config locations
 * can either denote concrete files like "/WEB-INF/context.groovy" or Ant-style patterns
 * like "/WEB-INF/*-context.groovy" (see {@link org.springframework.util.PathMatcher}
 * javadoc for pattern details). Note that ".xml" files will be parsed as XML content;
 * all other kinds of resources will be parsed as Groovy scripts.
 *
 * <p>Note: In case of multiple config locations, later bean definitions will
 * override ones defined in earlier loaded files. This can be leveraged to
 * deliberately override certain bean definitions via an extra Groovy script.
 *
 * <p><b>For a WebApplicationContext that reads in a different bean definition format,
 * create an analogous subclass of {@link AbstractRefreshableWebApplicationContext}.</b>
 * Such a context implementation can be specified as "contextClass" context-param
 * for ContextLoader or "contextClass" init-param for FrameworkServlet.
 *
 * <p>
 * {@link orgspringframeworkwebcontextWebApplicationContext}实现,它从Groovy bean定义脚本和/或XML文件中获取其配置,如{@link orgspringframeworkbeansfactorygroovyGroovyBeanDefinitionReader}
 * 所理解的这本质上相当于Web环境的{@link orgspringframeworkcontextsupportGenericGroovyApplicationContext}。
 * 
 *  <p>默认情况下,配置将取自"/ WEB-INF / applicationContextgroovy"作为根上下文,并且"/ WEB-INF / test-servletgroovy"用于命名空间"
 * test-servlet"(如具有servlet名称"test"的DispatcherServlet实例)。
 * 
 * <p>可以通过{@link orgspringframeworkwebcontextContextLoader}的"contextConfigLocation"上下文参数和{@link orgspringframeworkwebservletFrameworkServlet}
 * 的servlet init-param来覆盖配置位置默认值。
 * 配置位置可以表示具体的文件,如"/ WEB-INF / contextgroovy "或Ant风格的模式,如"/ WEB-INF / *  -  contextgroovy"(请参阅{@link orgspringframeworkutilPathMatcher}
 *  javadoc的模式详细信息)请注意,"xml"文件将被解析为XML内容;所有其他类型的资源将被解析为Groovy脚本。
 * 
 *  注意：在多个配置位置的情况下,稍后的bean定义将覆盖在早期加载的文件中定义的定义。这可以用于通过额外的Groovy脚本故意覆盖某些bean定义
 * 
 * <p> <b>对于读取不同的bean定义格式的WebApplicationContext,创建一个类似于{@link AbstractRefreshableWebApplicationContext}的
 * 子类</b>这样一个上下文实现可以被指定为ContextLoader的"contextClass"上下文参数或" ContextClass"FrameworkServlet的init-param。
 * 
 * @author Juergen Hoeller
 * @since 4.1
 * @see #setNamespace
 * @see #setConfigLocations
 * @see org.springframework.beans.factory.groovy.GroovyBeanDefinitionReader
 * @see org.springframework.web.context.ContextLoader#initWebApplicationContext
 * @see org.springframework.web.servlet.FrameworkServlet#initWebApplicationContext
 */
public class GroovyWebApplicationContext extends AbstractRefreshableWebApplicationContext implements GroovyObject {

	/** Default config location for the root context */
	public static final String DEFAULT_CONFIG_LOCATION = "/WEB-INF/applicationContext.groovy";

	/** Default prefix for building a config location for a namespace */
	public static final String DEFAULT_CONFIG_LOCATION_PREFIX = "/WEB-INF/";

	/** Default suffix for building a config location for a namespace */
	public static final String DEFAULT_CONFIG_LOCATION_SUFFIX = ".groovy";


	private final BeanWrapper contextWrapper = new BeanWrapperImpl(this);

	private MetaClass metaClass = GroovySystem.getMetaClassRegistry().getMetaClass(getClass());


	/**
	 * Loads the bean definitions via an GroovyBeanDefinitionReader.
	 * <p>
	 * 
	 * 
	 * @see org.springframework.beans.factory.groovy.GroovyBeanDefinitionReader
	 * @see #initBeanDefinitionReader
	 * @see #loadBeanDefinitions
	 */
	@Override
	protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException, IOException {
		// Create a new XmlBeanDefinitionReader for the given BeanFactory.
		GroovyBeanDefinitionReader beanDefinitionReader = new GroovyBeanDefinitionReader(beanFactory);

		// Configure the bean definition reader with this context's
		// resource loading environment.
		beanDefinitionReader.setEnvironment(getEnvironment());
		beanDefinitionReader.setResourceLoader(this);

		// Allow a subclass to provide custom initialization of the reader,
		// then proceed with actually loading the bean definitions.
		initBeanDefinitionReader(beanDefinitionReader);
		loadBeanDefinitions(beanDefinitionReader);
	}

	/**
	 * Initialize the bean definition reader used for loading the bean
	 * definitions of this context. Default implementation is empty.
	 * <p>Can be overridden in subclasses.
	 * <p>
	 *  通过GroovyBeanDefinitionReader加载bean定义
	 * 
	 * 
	 * @param beanDefinitionReader the bean definition reader used by this context
	 */
	protected void initBeanDefinitionReader(GroovyBeanDefinitionReader beanDefinitionReader) {
	}

	/**
	 * Load the bean definitions with the given GroovyBeanDefinitionReader.
	 * <p>The lifecycle of the bean factory is handled by the refreshBeanFactory method;
	 * therefore this method is just supposed to load and/or register bean definitions.
	 * <p>Delegates to a ResourcePatternResolver for resolving location patterns
	 * into Resource instances.
	 * <p>
	 *  初始化用于加载此上下文的bean定义的bean定义阅读器默认实现为空<p>可以在子类中覆盖
	 * 
	 * 
	 * @throws IOException if the required Groovy script or XML file isn't found
	 * @see #refreshBeanFactory
	 * @see #getConfigLocations
	 * @see #getResources
	 * @see #getResourcePatternResolver
	 */
	protected void loadBeanDefinitions(GroovyBeanDefinitionReader reader) throws IOException {
		String[] configLocations = getConfigLocations();
		if (configLocations != null) {
			for (String configLocation : configLocations) {
				reader.loadBeanDefinitions(configLocation);
			}
		}
	}

	/**
	 * The default location for the root context is "/WEB-INF/applicationContext.groovy",
	 * and "/WEB-INF/test-servlet.groovy" for a context with the namespace "test-servlet"
	 * (like for a DispatcherServlet instance with the servlet-name "test").
	 * <p>
	 * 使用给定的GroovyBeanDefinitionReader加载bean定义<p> bean factory的生命周期由refreshBeanFactory方法处理;因此,此方法只是将加载和/或注册b
	 * ean定义<p>将ResourcePatternResolver的代理用于将位置模式解析为资源实例。
	 * 
	 */
	@Override
	protected String[] getDefaultConfigLocations() {
		if (getNamespace() != null) {
			return new String[] {DEFAULT_CONFIG_LOCATION_PREFIX + getNamespace() + DEFAULT_CONFIG_LOCATION_SUFFIX};
		}
		else {
			return new String[] {DEFAULT_CONFIG_LOCATION};
		}
	}


	// Implementation of the GroovyObject interface

	public void setMetaClass(MetaClass metaClass) {
		this.metaClass = metaClass;
	}

	public MetaClass getMetaClass() {
		return this.metaClass;
	}

	public Object invokeMethod(String name, Object args) {
		return this.metaClass.invokeMethod(this, name, args);
	}

	public void setProperty(String property, Object newValue) {
		this.metaClass.setProperty(this, property, newValue);
	}

	public Object getProperty(String property) {
		if (containsBean(property)) {
			return getBean(property);
		}
		else if (this.contextWrapper.isReadableProperty(property)) {
			return this.contextWrapper.getPropertyValue(property);
		}
		throw new NoSuchBeanDefinitionException(property);
	}

}
