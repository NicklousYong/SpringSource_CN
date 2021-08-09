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

package org.springframework.context.support;

import groovy.lang.GroovyObject;
import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.groovy.GroovyBeanDefinitionReader;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * An {@link org.springframework.context.ApplicationContext} implementation that extends
 * {@link GenericApplicationContext} and implements {@link GroovyObject} such that beans
 * can be retrieved with the dot de-reference syntax instead of using {@link #getBean}.
 *
 * <p>Consider this as the equivalent of {@link GenericXmlApplicationContext} for
 * Groovy bean definitions, or even an upgrade thereof since it seamlessly understands
 * XML bean definition files as well. The main difference is that, within a Groovy
 * script, the context can be used with an inline bean definition closure as follows:
 *
 * <pre class="code">
 * import org.hibernate.SessionFactory
 * import org.apache.commons.dbcp.BasicDataSource
 *
 * def context = new GenericGroovyApplicationContext()
 * context.reader.beans {
 *     dataSource(BasicDataSource) {                  // <--- invokeMethod
 *         driverClassName = "org.hsqldb.jdbcDriver"
 *         url = "jdbc:hsqldb:mem:grailsDB"
 *         username = "sa"                            // <-- setProperty
 *         password = ""
 *         settings = [mynew:"setting"]
 *     }
 *     sessionFactory(SessionFactory) {
 *         dataSource = dataSource                    // <-- getProperty for retrieving references
 *     }
 *     myService(MyService) {
 *         nestedBean = { AnotherBean bean ->         // <-- setProperty with closure for nested bean
 *             dataSource = dataSource
 *         }
 *     }
 * }
 * context.refresh()
 * </pre>
 *
 * <p>Alternatively, load a Groovy bean definition script like the following
 * from an external resource (e.g. an "applicationContext.groovy" file):
 *
 * <pre class="code">
 * import org.hibernate.SessionFactory
 * import org.apache.commons.dbcp.BasicDataSource
 *
 * beans {
 *     dataSource(BasicDataSource) {
 *         driverClassName = "org.hsqldb.jdbcDriver"
 *         url = "jdbc:hsqldb:mem:grailsDB"
 *         username = "sa"
 *         password = ""
 *         settings = [mynew:"setting"]
 *     }
 *     sessionFactory(SessionFactory) {
 *         dataSource = dataSource
 *     }
 *     myService(MyService) {
 *         nestedBean = { AnotherBean bean ->
 *             dataSource = dataSource
 *         }
 *     }
 * }
 * </pre>
 *
 * <p>With the following Java code creating the {@code GenericGroovyApplicationContext}
 * (potentially using Ant-style '*'/'**' location patterns):
 *
 * <pre class="code">
 * GenericGroovyApplicationContext context = new GenericGroovyApplicationContext();
 * context.load("org/myapp/applicationContext.groovy");
 * context.refresh();
 * </pre>
 *
 * <p>Or even more concise, provided that no extra configuration is needed:
 *
 * <pre class="code">
 * ApplicationContext context = new GenericGroovyApplicationContext("org/myapp/applicationContext.groovy");
 * </pre>
 *
 * <p><b>This application context also understands XML bean definition files,
 * allowing for seamless mixing and matching with Groovy bean definition files.</b>
 * ".xml" files will be parsed as XML content; all other kinds of resources will
 * be parsed as Groovy scripts.
 *
 * <p>
 * {@link orgspringframeworkcontextApplicationContext}实现,扩展{@link GenericApplicationContext}并实现{@link GroovyObject}
 * ,以便可以使用点取消引用语法检索bean,而不是使用{@link #getBean}。
 * 
 *  <p>将其视为相当于用于Groovy bean定义的{@link GenericXmlApplicationContext},或者甚至升级,因为它可以无缝地了解XML bean定义文件。
 * 主要区别在于,在Groovy脚本中,可以使用上下文与内联bean定义关闭如下：。
 * 
 * <pre class="code">
 *  import orghibernateSessionFactory import orgapachecommonsdbcpBasicDataSource
 * 
 * def context = new GenericGroovyApplicationContext()contextreaderbeans {dataSource(BasicDataSource){// <--- invokeMethod driverClassName ="orghsqldbjdbcDriver"url ="jdbc：hsqldb：mem：grailsDB"username ="sa"// < -  setProperty password = ""settings = [mynew："setting"]} sessionFactory(SessionFactory){dataSource = dataSource // < - 获取引用的getProperty} myService(MyService){nestedBean = {AnotherBean bean  - > // < -  setProperty with closure对于嵌套的bean dataSource = dataSource}}} contextrefresh()。
 * </pre>
 * 
 *  <p>或者,从外部资源(例如"applicationContextgroovy")中加载Groovy bean定义脚本,如下所示：
 * 
 * <pre class="code">
 * import orghibernateSessionFactory import orgapachecommonsdbcpBasicDataSource
 * 
 *  bean {dataSource(BasicDataSource){driverClassName ="orghsqldbjdbcDriver"url ="jdbc：hsqldb：mem：grailsDB"username ="sa"password =""settings = [mynew："setting"]}
 *  sessionFactory(SessionFactory){dataSource = dataSource } myService(MyService){。
 * nestedBean = { AnotherBean bean ->
 *  dataSource = dataSource}}}
 * </pre>
 * 
 *  使用以下Java代码创建{@code GenericGroovyApplicationContext}(可能使用Ant风格的'*'/'**'位置模式)：
 * 
 * <pre class="code">
 *  GenericGroovyApplicationContext context = new GenericGroovyApplicationContext(); contextload( "组织/ M
 * yApp的/ applicationContextgroovy"); contextrefresh();。
 * </pre>
 * 
 *  <p>或者更简洁,只要不需要额外的配置：
 * 
 * <pre class="code">
 * ApplicationContext context = new GenericGroovyApplicationContext("org / myapp / applicationContextgro
 * 
 * @author Juergen Hoeller
 * @author Jeff Brown
 * @since 4.0
 * @see org.springframework.beans.factory.groovy.GroovyBeanDefinitionReader
 */
public class GenericGroovyApplicationContext extends GenericApplicationContext implements GroovyObject {

	private final GroovyBeanDefinitionReader reader = new GroovyBeanDefinitionReader(this);

	private final BeanWrapper contextWrapper = new BeanWrapperImpl(this);

    private MetaClass metaClass = GroovySystem.getMetaClassRegistry().getMetaClass(getClass());


	/**
	 * Create a new GenericGroovyApplicationContext that needs to be
	 * {@link #load loaded} and then manually {@link #refresh refreshed}.
	 * <p>
	 * ovy");。
	 * </pre>
	 * 
	 *  <p> <b>此应用程序上下文还可以了解XML bean定义文件,允许与Groovy bean定义文件进行无缝混合和匹配</b>"xml"文件将被解析为XML内容;所有其他类型的资源将被解析为Groo
	 * vy脚本。
	 * 
	 */
	public GenericGroovyApplicationContext() {
	}

	/**
	 * Create a new GenericGroovyApplicationContext, loading bean definitions
	 * from the given resources and automatically refreshing the context.
	 * <p>
	 *  创建一个新的GenericGroovyApplicationContext,需要{@link #load loaded}然后手动{@link #refresh refreshs}
	 * 
	 * 
	 * @param resources the resources to load from
	 */
	public GenericGroovyApplicationContext(Resource... resources) {
		load(resources);
		refresh();
	}

	/**
	 * Create a new GenericGroovyApplicationContext, loading bean definitions
	 * from the given resource locations and automatically refreshing the context.
	 * <p>
	 *  创建一个新的GenericGroovyApplicationContext,从给定的资源加载bean定义并自动刷新上下文
	 * 
	 * 
	 * @param resourceLocations the resources to load from
	 */
	public GenericGroovyApplicationContext(String... resourceLocations) {
		load(resourceLocations);
		refresh();
	}

	/**
	 * Create a new GenericGroovyApplicationContext, loading bean definitions
	 * from the given resource locations and automatically refreshing the context.
	 * <p>
	 *  创建一个新的GenericGroovyApplicationContext,从给定的资源位置加载bean定义并自动刷新上下文
	 * 
	 * 
	 * @param relativeClass class whose package will be used as a prefix when
	 * loading each specified resource name
	 * @param resourceNames relatively-qualified names of resources to load
	 */
	public GenericGroovyApplicationContext(Class<?> relativeClass, String... resourceNames) {
		load(relativeClass, resourceNames);
		refresh();
	}


	/**
	 * Exposes the underlying {@link GroovyBeanDefinitionReader} for convenient access
	 * to the {@code loadBeanDefinition} methods on it as well as the ability
	 * to specify an inline Groovy bean definition closure.
	 * <p>
	 * 创建一个新的GenericGroovyApplicationContext,从给定的资源位置加载bean定义并自动刷新上下文
	 * 
	 * 
	 * @see GroovyBeanDefinitionReader#loadBeanDefinitions(org.springframework.core.io.Resource...)
	 * @see GroovyBeanDefinitionReader#loadBeanDefinitions(String...)
	 */
	public final GroovyBeanDefinitionReader getReader() {
		return this.reader;
	}

	/**
	 * Delegates the given environment to underlying {@link GroovyBeanDefinitionReader}.
	 * Should be called before any call to {@code #load}.
	 * <p>
	 *  暴露底层的{@link GroovyBeanDefinitionReader},方便访问{@code loadBeanDefinition}方法,以及指定内联Groovy bean定义关闭的能力
	 * 
	 */
	@Override
	public void setEnvironment(ConfigurableEnvironment environment) {
		super.setEnvironment(environment);
		this.reader.setEnvironment(getEnvironment());
	}

	/**
	 * Load bean definitions from the given Groovy scripts or XML files.
	 * <p>Note that ".xml" files will be parsed as XML content; all other kinds
	 * of resources will be parsed as Groovy scripts.
	 * <p>
	 *  将给定的环境委托给底层的{@link GroovyBeanDefinitionReader}在调用{@code #load}之前应该调用它
	 * 
	 * 
	 * @param resources one or more resources to load from
	 */
	public void load(Resource... resources) {
		this.reader.loadBeanDefinitions(resources);
	}

	/**
	 * Load bean definitions from the given Groovy scripts or XML files.
	 * <p>Note that ".xml" files will be parsed as XML content; all other kinds
	 * of resources will be parsed as Groovy scripts.
	 * <p>
	 *  从给定的Groovy脚本或XML文件加载bean定义<p>请注意,"xml"文件将被解析为XML内容;所有其他类型的资源将被解析为Groovy脚本
	 * 
	 * 
	 * @param resourceLocations one or more resource locations to load from
	 */
	public void load(String... resourceLocations) {
		this.reader.loadBeanDefinitions(resourceLocations);
	}

	/**
	 * Load bean definitions from the given Groovy scripts or XML files.
	 * <p>Note that ".xml" files will be parsed as XML content; all other kinds
	 * of resources will be parsed as Groovy scripts.
	 * <p>
	 * 从给定的Groovy脚本或XML文件加载bean定义<p>请注意,"xml"文件将被解析为XML内容;所有其他类型的资源将被解析为Groovy脚本
	 * 
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
		load(resources);
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
		if (newValue instanceof BeanDefinition) {
			registerBeanDefinition(property, (BeanDefinition) newValue);
		}
		else {
			this.metaClass.setProperty(this, property, newValue);
		}
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
