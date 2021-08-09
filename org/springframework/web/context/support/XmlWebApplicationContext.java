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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;

/**
 * {@link org.springframework.web.context.WebApplicationContext} implementation
 * which takes its configuration from XML documents, understood by an
 * {@link org.springframework.beans.factory.xml.XmlBeanDefinitionReader}.
 * This is essentially the equivalent of
 * {@link org.springframework.context.support.GenericXmlApplicationContext}
 * for a web environment.
 *
 * <p>By default, the configuration will be taken from "/WEB-INF/applicationContext.xml"
 * for the root context, and "/WEB-INF/test-servlet.xml" for a context with the namespace
 * "test-servlet" (like for a DispatcherServlet instance with the servlet-name "test").
 *
 * <p>The config location defaults can be overridden via the "contextConfigLocation"
 * context-param of {@link org.springframework.web.context.ContextLoader} and servlet
 * init-param of {@link org.springframework.web.servlet.FrameworkServlet}. Config locations
 * can either denote concrete files like "/WEB-INF/context.xml" or Ant-style patterns
 * like "/WEB-INF/*-context.xml" (see {@link org.springframework.util.PathMatcher}
 * javadoc for pattern details).
 *
 * <p>Note: In case of multiple config locations, later bean definitions will
 * override ones defined in earlier loaded files. This can be leveraged to
 * deliberately override certain bean definitions via an extra XML file.
 *
 * <p><b>For a WebApplicationContext that reads in a different bean definition format,
 * create an analogous subclass of {@link AbstractRefreshableWebApplicationContext}.</b>
 * Such a context implementation can be specified as "contextClass" context-param
 * for ContextLoader or "contextClass" init-param for FrameworkServlet.
 *
 * <p>
 * {@link orgspringframeworkwebcontextWebApplicationContext}实现,它从XML文档中进行配置,由{@link orgspringframeworkbeansfactoryxmlXmlBeanDefinitionReader}
 * 理解。
 * 这本质上相当于Web环境的{@link orgspringframeworkcontextsupportGenericXmlApplicationContext}。
 * 
 *  <p>默认情况下,配置将取自"/ WEB-INF / applicationContextxml"作为根上下文,"/ WEB-INF / test-servletxml"用于命名空间"test-ser
 * vlet"(如具有servlet名称"test"的DispatcherServlet实例)。
 * 
 * <p>可以通过{@link orgspringframeworkwebcontextContextLoader}的"contextConfigLocation"上下文参数和{@link orgspringframeworkwebservletFrameworkServlet}
 * 的servlet init-param来覆盖配置位置默认值配置位置可以表示具体的文件,如"/ WEB-INF / contextxml "或Ant样式模式,如"/ WEB-INF / *  -  con
 * textxml"(有关模式详细信息,请参阅{@link orgspringframeworkutilPathMatcher} javadoc)。
 * 
 *  注意：在多个配置位置的情况下,稍后的bean定义将覆盖在早期加载的文件中定义的定义。可以利用此方法通过额外的XML文件故意覆盖某些bean定义
 * 
 * <p> <b>对于读取不同的bean定义格式的WebApplicationContext,创建一个类似于{@link AbstractRefreshableWebApplicationContext}的
 * 子类</b>这样一个上下文实现可以被指定为ContextLoader的"contextClass"上下文参数或" ContextClass"FrameworkServlet的init-param。
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see #setNamespace
 * @see #setConfigLocations
 * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader
 * @see org.springframework.web.context.ContextLoader#initWebApplicationContext
 * @see org.springframework.web.servlet.FrameworkServlet#initWebApplicationContext
 */
public class XmlWebApplicationContext extends AbstractRefreshableWebApplicationContext {

	/** Default config location for the root context */
	public static final String DEFAULT_CONFIG_LOCATION = "/WEB-INF/applicationContext.xml";

	/** Default prefix for building a config location for a namespace */
	public static final String DEFAULT_CONFIG_LOCATION_PREFIX = "/WEB-INF/";

	/** Default suffix for building a config location for a namespace */
	public static final String DEFAULT_CONFIG_LOCATION_SUFFIX = ".xml";


	/**
	 * Loads the bean definitions via an XmlBeanDefinitionReader.
	 * <p>
	 * 
	 * 
	 * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader
	 * @see #initBeanDefinitionReader
	 * @see #loadBeanDefinitions
	 */
	@Override
	protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException, IOException {
		// Create a new XmlBeanDefinitionReader for the given BeanFactory.
		XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);

		// Configure the bean definition reader with this context's
		// resource loading environment.
		beanDefinitionReader.setEnvironment(getEnvironment());
		beanDefinitionReader.setResourceLoader(this);
		beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(this));

		// Allow a subclass to provide custom initialization of the reader,
		// then proceed with actually loading the bean definitions.
		initBeanDefinitionReader(beanDefinitionReader);
		loadBeanDefinitions(beanDefinitionReader);
	}

	/**
	 * Initialize the bean definition reader used for loading the bean
	 * definitions of this context. Default implementation is empty.
	 * <p>Can be overridden in subclasses, e.g. for turning off XML validation
	 * or using a different XmlBeanDefinitionParser implementation.
	 * <p>
	 *  通过XmlBeanDefinitionReader加载bean定义
	 * 
	 * 
	 * @param beanDefinitionReader the bean definition reader used by this context
	 * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader#setValidationMode
	 * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader#setDocumentReaderClass
	 */
	protected void initBeanDefinitionReader(XmlBeanDefinitionReader beanDefinitionReader) {
	}

	/**
	 * Load the bean definitions with the given XmlBeanDefinitionReader.
	 * <p>The lifecycle of the bean factory is handled by the refreshBeanFactory method;
	 * therefore this method is just supposed to load and/or register bean definitions.
	 * <p>Delegates to a ResourcePatternResolver for resolving location patterns
	 * into Resource instances.
	 * <p>
	 *  初始化用于加载此上下文的bean定义的bean定义阅读器默认实现为空<p>可以在子类中覆盖,例如关闭XML验证或使用不同的XmlBeanDefinitionParser实现
	 * 
	 * 
	 * @throws IOException if the required XML document isn't found
	 * @see #refreshBeanFactory
	 * @see #getConfigLocations
	 * @see #getResources
	 * @see #getResourcePatternResolver
	 */
	protected void loadBeanDefinitions(XmlBeanDefinitionReader reader) throws IOException {
		String[] configLocations = getConfigLocations();
		if (configLocations != null) {
			for (String configLocation : configLocations) {
				reader.loadBeanDefinitions(configLocation);
			}
		}
	}

	/**
	 * The default location for the root context is "/WEB-INF/applicationContext.xml",
	 * and "/WEB-INF/test-servlet.xml" for a context with the namespace "test-servlet"
	 * (like for a DispatcherServlet instance with the servlet-name "test").
	 * <p>
	 * 使用给定的XmlBeanDefinitionReader加载bean定义<p> bean factory的生命周期由refreshBeanFactory方法处理;因此,此方法只是将加载和/或注册bean
	 * 定义<p>将ResourcePatternResolver的代理用于将位置模式解析为资源实例。
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

}
