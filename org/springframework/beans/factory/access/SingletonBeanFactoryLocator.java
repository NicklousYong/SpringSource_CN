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

package org.springframework.beans.factory.access;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;

/**
 * <p>Keyed-singleton implementation of {@link BeanFactoryLocator},
 * which accesses shared Spring {@link BeanFactory} instances.</p>
 *
 * <p>Please see the warning in BeanFactoryLocator's javadoc about appropriate usage
 * of singleton style BeanFactoryLocator implementations. It is the opinion of the
 * Spring team that the use of this class and similar classes is unnecessary except
 * (sometimes) for a small amount of glue code. Excessive usage will lead to code
 * that is more tightly coupled, and harder to modify or test.</p>
 *
 * <p>In this implementation, a BeanFactory is built up from one or more XML
 * definition file fragments, accessed as resources. The default resource name
 * searched for is 'classpath*:beanRefFactory.xml', with the Spring-standard
 * 'classpath*:' prefix ensuring that if the classpath contains multiple copies
 * of this file (perhaps one in each component jar) they will be combined. To
 * override the default resource name, instead of using the no-arg
 * {@link #getInstance()} method, use the {@link #getInstance(String selector)}
 * variant, which will treat the 'selector' argument as the resource name to
 * search for.</p>
 *
 * <p>The purpose of this 'outer' BeanFactory is to create and hold a copy of one
 * or more 'inner' BeanFactory or ApplicationContext instances, and allow those
 * to be obtained either directly or via an alias. As such, this class provides
 * both singleton style access to one or more BeanFactories/ApplicationContexts,
 * and also a level of indirection, allowing multiple pieces of code, which are
 * not able to work in a Dependency Injection fashion, to refer to and use the
 * same target BeanFactory/ApplicationContext instance(s), by different names.<p>
 *
 * <p>Consider an example application scenario:
 *
 * <ul>
 * <li>{@code com.mycompany.myapp.util.applicationContext.xml} -
 * ApplicationContext definition file which defines beans for 'util' layer.
 * <li>{@code com.mycompany.myapp.dataaccess-applicationContext.xml} -
 * ApplicationContext definition file which defines beans for 'data access' layer.
 * Depends on the above.
 * <li>{@code com.mycompany.myapp.services.applicationContext.xml} -
 * ApplicationContext definition file which defines beans for 'services' layer.
 * Depends on the above.
 * </ul>
 *
 * <p>In an ideal scenario, these would be combined to create one ApplicationContext,
 * or created as three hierarchical ApplicationContexts, by one piece of code
 * somewhere at application startup (perhaps a Servlet filter), from which all other
 * code in the application would flow, obtained as beans from the context(s). However
 * when third party code enters into the picture, things can get problematic. If the
 * third party code needs to create user classes, which should normally be obtained
 * from a Spring BeanFactory/ApplicationContext, but can handle only newInstance()
 * style object creation, then some extra work is required to actually access and
 * use object from a BeanFactory/ApplicationContext. One solutions is to make the
 * class created by the third party code be just a stub or proxy, which gets the
 * real object from a BeanFactory/ApplicationContext, and delegates to it. However,
 * it is not normally workable for the stub to create the BeanFactory on each
 * use, as depending on what is inside it, that can be an expensive operation.
 * Additionally, there is a fairly tight coupling between the stub and the name of
 * the definition resource for the BeanFactory/ApplicationContext. This is where
 * SingletonBeanFactoryLocator comes in. The stub can obtain a
 * SingletonBeanFactoryLocator instance, which is effectively a singleton, and
 * ask it for an appropriate BeanFactory. A subsequent invocation (assuming the
 * same class loader is involved) by the stub or another piece of code, will obtain
 * the same instance. The simple aliasing mechanism allows the context to be asked
 * for by a name which is appropriate for (or describes) the user. The deployer can
 * match alias names to actual context names.
 *
 * <p>Another use of SingletonBeanFactoryLocator, is to demand-load/use one or more
 * BeanFactories/ApplicationContexts. Because the definition can contain one of more
 * BeanFactories/ApplicationContexts, which can be independent or in a hierarchy, if
 * they are set to lazy-initialize, they will only be created when actually requested
 * for use.
 *
 * <p>Given the above-mentioned three ApplicationContexts, consider the simplest
 * SingletonBeanFactoryLocator usage scenario, where there is only one single
 * {@code beanRefFactory.xml} definition file:
 *
 * <pre class="code">&lt;?xml version="1.0" encoding="UTF-8"?>
 * &lt;!DOCTYPE beans PUBLIC "-//SPRING//DTD BEAN 2.0//EN" "http://www.springframework.org/dtd/spring-beans-2.0.dtd">
 *
 * &lt;beans>
 *
 *   &lt;bean id="com.mycompany.myapp"
 *         class="org.springframework.context.support.ClassPathXmlApplicationContext">
 *     &lt;constructor-arg>
 *       &lt;list>
 *         &lt;value>com/mycompany/myapp/util/applicationContext.xml&lt;/value>
 *         &lt;value>com/mycompany/myapp/dataaccess/applicationContext.xml&lt;/value>
 *         &lt;value>com/mycompany/myapp/dataaccess/services.xml&lt;/value>
 *       &lt;/list>
 *     &lt;/constructor-arg>
 *   &lt;/bean>
 *
 * &lt;/beans>
 * </pre>
 *
 * The client code is as simple as:
 *
 * <pre class="code">
 * BeanFactoryLocator bfl = SingletonBeanFactoryLocator.getInstance();
 * BeanFactoryReference bf = bfl.useBeanFactory("com.mycompany.myapp");
 * // now use some bean from factory
 * MyClass zed = bf.getFactory().getBean("mybean");
 * </pre>
 *
 * Another relatively simple variation of the {@code beanRefFactory.xml} definition file could be:
 *
 * <pre class="code">&lt;?xml version="1.0" encoding="UTF-8"?>
 * &lt;!DOCTYPE beans PUBLIC "-//SPRING//DTD BEAN 2.0//EN" "http://www.springframework.org/dtd/spring-beans-2.0.dtd">
 *
 * &lt;beans>
 *
 *   &lt;bean id="com.mycompany.myapp.util" lazy-init="true"
 *         class="org.springframework.context.support.ClassPathXmlApplicationContext">
 *     &lt;constructor-arg>
 *       &lt;value>com/mycompany/myapp/util/applicationContext.xml&lt;/value>
 *     &lt;/constructor-arg>
 *   &lt;/bean>
 *
 *   &lt;!-- child of above -->
 *   &lt;bean id="com.mycompany.myapp.dataaccess" lazy-init="true"
 *         class="org.springframework.context.support.ClassPathXmlApplicationContext">
 *     &lt;constructor-arg>
 *       &lt;list>&lt;value>com/mycompany/myapp/dataaccess/applicationContext.xml&lt;/value>&lt;/list>
 *     &lt;/constructor-arg>
 *     &lt;constructor-arg>
 *       &lt;ref bean="com.mycompany.myapp.util"/>
 *     &lt;/constructor-arg>
 *   &lt;/bean>
 *
 *   &lt;!-- child of above -->
 *   &lt;bean id="com.mycompany.myapp.services" lazy-init="true"
 *         class="org.springframework.context.support.ClassPathXmlApplicationContext">
 *     &lt;constructor-arg>
 *       &lt;list>&lt;value>com/mycompany/myapp/dataaccess.services.xml&lt;/value>&lt;/value>
 *     &lt;/constructor-arg>
 *     &lt;constructor-arg>
 *       &lt;ref bean="com.mycompany.myapp.dataaccess"/>
 *     &lt;/constructor-arg>
 *   &lt;/bean>
 *
 *   &lt;!-- define an alias -->
 *   &lt;bean id="com.mycompany.myapp.mypackage"
 *         class="java.lang.String">
 *     &lt;constructor-arg>
 *       &lt;value>com.mycompany.myapp.services&lt;/value>
 *     &lt;/constructor-arg>
 *   &lt;/bean>
 *
 * &lt;/beans>
 * </pre>
 *
 * <p>In this example, there is a hierarchy of three contexts created. The (potential)
 * advantage is that if the lazy flag is set to true, a context will only be created
 * if it's actually used. If there is some code that is only needed some of the time,
 * this mechanism can save some resources. Additionally, an alias to the last context
 * has been created. Aliases allow usage of the idiom where client code asks for a
 * context with an id which represents the package or module the code is in, and the
 * actual definition file(s) for the SingletonBeanFactoryLocator maps that id to
 * a real context id.
 *
 * <p>A final example is more complex, with a {@code beanRefFactory.xml} for every module.
 * All the files are automatically combined to create the final definition.
 *
 * <p>{@code beanRefFactory.xml} file inside jar for util module:
 *
 * <pre class="code">&lt;?xml version="1.0" encoding="UTF-8"?>
 * &lt;!DOCTYPE beans PUBLIC "-//SPRING//DTD BEAN 2.0//EN" "http://www.springframework.org/dtd/spring-beans-2.0.dtd">
 *
 * &lt;beans>
 *   &lt;bean id="com.mycompany.myapp.util" lazy-init="true"
 *        class="org.springframework.context.support.ClassPathXmlApplicationContext">
 *     &lt;constructor-arg>
 *       &lt;value>com/mycompany/myapp/util/applicationContext.xml&lt;/value>
 *     &lt;/constructor-arg>
 *   &lt;/bean>
 * &lt;/beans>
 * </pre>
 *
 * {@code beanRefFactory.xml} file inside jar for data-access module:<br>
 *
 * <pre class="code">&lt;?xml version="1.0" encoding="UTF-8"?>
 * &lt;!DOCTYPE beans PUBLIC "-//SPRING//DTD BEAN 2.0//EN" "http://www.springframework.org/dtd/spring-beans-2.0.dtd">
 *
 * &lt;beans>
 *   &lt;!-- child of util -->
 *   &lt;bean id="com.mycompany.myapp.dataaccess" lazy-init="true"
 *        class="org.springframework.context.support.ClassPathXmlApplicationContext">
 *     &lt;constructor-arg>
 *       &lt;list>&lt;value>com/mycompany/myapp/dataaccess/applicationContext.xml&lt;/value>&lt;/list>
 *     &lt;/constructor-arg>
 *     &lt;constructor-arg>
 *       &lt;ref bean="com.mycompany.myapp.util"/>
 *     &lt;/constructor-arg>
 *   &lt;/bean>
 * &lt;/beans>
 * </pre>
 *
 * {@code beanRefFactory.xml} file inside jar for services module:
 *
 * <pre class="code">&lt;?xml version="1.0" encoding="UTF-8"?>
 * &lt;!DOCTYPE beans PUBLIC "-//SPRING//DTD BEAN 2.0//EN" "http://www.springframework.org/dtd/spring-beans-2.0.dtd">
 *
 * &lt;beans>
 *   &lt;!-- child of data-access -->
 *   &lt;bean id="com.mycompany.myapp.services" lazy-init="true"
 *        class="org.springframework.context.support.ClassPathXmlApplicationContext">
 *     &lt;constructor-arg>
 *       &lt;list>&lt;value>com/mycompany/myapp/dataaccess/services.xml&lt;/value>&lt;/list>
 *     &lt;/constructor-arg>
 *     &lt;constructor-arg>
 *       &lt;ref bean="com.mycompany.myapp.dataaccess"/>
 *     &lt;/constructor-arg>
 *   &lt;/bean>
 * &lt;/beans>
 * </pre>
 *
 * {@code beanRefFactory.xml} file inside jar for mypackage module. This doesn't
 * create any of its own contexts, but allows the other ones to be referred to be
 * a name known to this module:
 *
 * <pre class="code">&lt;?xml version="1.0" encoding="UTF-8"?>
 * &lt;!DOCTYPE beans PUBLIC "-//SPRING//DTD BEAN 2.0//EN" "http://www.springframework.org/dtd/spring-beans-2.0.dtd">
 *
 * &lt;beans>
 *   &lt;!-- define an alias for "com.mycompany.myapp.services" -->
 *   &lt;alias name="com.mycompany.myapp.services" alias="com.mycompany.myapp.mypackage"/&gt;
 * &lt;/beans>
 * </pre>
 *
 * <p>
 *  <p> {@link BeanFactoryLocator}的密钥单例实现,它访问共享的Spring {@link BeanFactory}实例</p>
 * 
 * <p>请参阅BeanFactoryLocator的javadoc中关于单例BeanFactoryLocator实现的适当用法的警告。
 * Spring团队认为,使用此类和类似类是不必要的,除了(有时)少量的粘贴代码过多使用将导致更紧密耦合的代码,更难修改或测试</p>。
 * 
 * <p>在此实现中,BeanFactory是从一个或多个XML定义文件片段构建的,作为资源访问。
 * 搜索的默认资源名称是'classpath *：beanRefFactoryxml',Spring标准的'classpath *如果类路径包含此文件的多个副本(可能在每个组件jar中都有一个),则它们将被
 * 组合要覆盖默认资源名称,而不是使用no-arg {@link #getInstance()}方法,请使用{@链接#getInstance(String selector)}变体,它将把"selector
 * "参数当作资源名称来搜索</p>。
 * <p>在此实现中,BeanFactory是从一个或多个XML定义文件片段构建的,作为资源访问。
 * 
 * <p>这个"外部"BeanFactory的目的是创建和保存一个或多个"内部"BeanFactory或ApplicationContext实例的副本,并允许直接或通过别名获取这些副本。
 * 因此,此类提供单个一个或多个BeanFactory / ApplicationContexts的样式访问,以及一个间接级别,允许不能以依赖注入方式工作的多个代码段来引用并使用相同的目标BeanFacto
 * ry / ApplicationContext实例, ,不同的名字<p>。
 * <p>这个"外部"BeanFactory的目的是创建和保存一个或多个"内部"BeanFactory或ApplicationContext实例的副本,并允许直接或通过别名获取这些副本。
 * 
 *  考虑应用场景示例：
 * 
 * <ul>
 * <li> {@ code commycompanymyapputilapplicationContextxml}  - 定义"util"层的bean的ApplicationContext定义文件<li>
 *  {@ code commycompanymyappdataaccess-applicationContextxml}  - 定义"数据访问"层的bean的ApplicationContext定义文件取
 * 决于上述<li> {@code commycompanymyappservicesapplicationContextxml}  - 定义"服务"层的bean的ApplicationContext定义文
 * 件取决于上述。
 * </ul>
 * 
 * 在理想情况下,这些将被组合以创建一个ApplicationContext,或者通过应用程序启动时的某个代码(可能是一个Servlet过滤器)创建一个ApplicationContext,或者创建为三个分
 * 层的ApplicationContexts,应用程序中的所有其他代码将从该代码,从上下文中获取为bean但是当第三方代码进入图片时,事情可能会遇到问题如果第三方代码需要创建用户类,通常是从Spring 
 * BeanFactory / ApplicationContext获取的,但可以处理只有newInstance()样式对象创建,那么需要一些额外的工作来实际访问和使用BeanFactory / Appli
 * cationContext中的对象一个解决方案是使由第三方代码创建的类只是一个存根或代理,它从BeanFactory / ApplicationContext获取真实对象,并委托给它然而,对于存根创建B
 * eanFactory通常不可行每个使用,取决于它内部的内容,这可能是一个昂贵的操作。
 * 另外,存根与BeanFactory / ApplicationContext的定义资源的名称之间存在相当紧密的联系。这是SingletonBeanFactoryLocator来自的位置。
 * 存根可以获取一个SingletonBeanFactoryLocator实例,它实际上是一个单例,并要求它一个适当的BeanFactory通过存根或另一段代码进行的后续调用(假设相同的类加载器)将获得相同
 * 的实例简单的混叠机制允许通过适当(或描述)用户的名称来询问上下文部署者可以将别名与实际的上下文名称相匹配。
 * 另外,存根与BeanFactory / ApplicationContext的定义资源的名称之间存在相当紧密的联系。这是SingletonBeanFactoryLocator来自的位置。
 * 
 * <p> SingletonBeanFactoryLocator的另一个用途是要求加载/使用一个或多个BeanFactories / ApplicationContext因为定义可以包含一个BeanFac
 * tories / ApplicationContexts,它们可以是独立的,也可以是一个层次结构,如果它们被设置为lazy-initialize ,它们将仅在实际请求使用时创建。
 * 
 *  考虑到上述三个ApplicationContexts,考虑最简单的SingletonBeanFactoryLocator使用场景,其中只有一个{@code beanRefFactoryxml}定义文件
 * ：。
 * 
 * <pre class="code">&lt;?xml version="1.0" encoding="UTF-8"?>
 * &lt;!DOCTYPE beans PUBLIC "-//SPRING//DTD BEAN 2.0//EN" "http://www.springframework.org/dtd/spring-beans-2.0.dtd">
 * 
 * &lt;beans>
 * 
 *  &lt; bean id ="commycompanymyapp"
 * class="org.springframework.context.support.ClassPathXmlApplicationContext">
 * &lt;constructor-arg>
 * &lt;list>
 * &lt;value>com/mycompany/myapp/util/applicationContext.xml&lt;/value>
 * &lt;value>com/mycompany/myapp/dataaccess/applicationContext.xml&lt;/value>
 * &lt;value>com/mycompany/myapp/dataaccess/services.xml&lt;/value>
 * &lt;/list>
 * &lt;/constructor-arg>
 * &lt;/bean>
 * 
 * &lt;/beans>
 * </pre>
 * 
 *  客户端代码简单如下：
 * 
 * <pre class="code">
 * BeanFactoryLocator bfl = SingletonBeanFactoryLocatorgetInstance(); BeanFactoryReference bf = bfluseBe
 * anFactory("commycompanymyapp"); //现在从工厂使用一些bean MyClass zed = bfgetFactory()getBean("mybean");。
 * </pre>
 * 
 *  {@code beanRefFactoryxml}定义文件的另一个相对简单的变体可能是：
 * 
 * <pre class="code">&lt;?xml version="1.0" encoding="UTF-8"?>
 * &lt;!DOCTYPE beans PUBLIC "-//SPRING//DTD BEAN 2.0//EN" "http://www.springframework.org/dtd/spring-beans-2.0.dtd">
 * 
 * &lt;beans>
 * 
 *  &lt; bean id ="commycompanymyapputil"lazy-init ="true"
 * class="org.springframework.context.support.ClassPathXmlApplicationContext">
 * &lt;constructor-arg>
 * &lt;value>com/mycompany/myapp/util/applicationContext.xml&lt;/value>
 * &lt;/constructor-arg>
 * &lt;/bean>
 * 
 * &lt;!-- child of above -->
 *  &lt; bean id ="commycompanymyappdataaccess"lazy-init ="true"
 * class="org.springframework.context.support.ClassPathXmlApplicationContext">
 * &lt;constructor-arg>
 * &lt;list>&lt;value>com/mycompany/myapp/dataaccess/applicationContext.xml&lt;/value>&lt;/list>
 * &lt;/constructor-arg>
 * &lt;constructor-arg>
 * &lt;ref bean="com.mycompany.myapp.util"/>
 * &lt;/constructor-arg>
 * &lt;/bean>
 * 
 * &lt;!-- child of above -->
 *  &lt; bean id ="commycompanymyappservices"lazy-init ="true"
 * class="org.springframework.context.support.ClassPathXmlApplicationContext">
 * &lt;constructor-arg>
 * &lt;list>&lt;value>com/mycompany/myapp/dataaccess.services.xml&lt;/value>&lt;/value>
 * &lt;/constructor-arg>
 * &lt;constructor-arg>
 * &lt;ref bean="com.mycompany.myapp.dataaccess"/>
 * &lt;/constructor-arg>
 * &lt;/bean>
 * 
 * &lt;!-- define an alias -->
 *  &lt; bean id ="commycompanymyappmypackage"
 * class="java.lang.String">
 * &lt;constructor-arg>
 * &lt;value>com.mycompany.myapp.services&lt;/value>
 * 
 * @author Colin Sampaleanu
 * @author Juergen Hoeller
 * @see org.springframework.context.access.ContextSingletonBeanFactoryLocator
 * @see org.springframework.context.access.DefaultLocatorFactory
 */
public class SingletonBeanFactoryLocator implements BeanFactoryLocator {

	private static final String DEFAULT_RESOURCE_LOCATION = "classpath*:beanRefFactory.xml";

	protected static final Log logger = LogFactory.getLog(SingletonBeanFactoryLocator.class);

	/** The keyed BeanFactory instances */
	private static final Map<String, BeanFactoryLocator> instances = new HashMap<String, BeanFactoryLocator>();


	/**
	 * Returns an instance which uses the default "classpath*:beanRefFactory.xml",
	 * as the name of the definition file(s). All resources returned by calling the
	 * current thread context ClassLoader's {@code getResources} method with
	 * this name will be combined to create a BeanFactory definition set.
	 * <p>
	 * &lt;/constructor-arg>
	 * &lt;/bean>
	 * 
	 * &lt;/beans>
	 * </pre>
	 * 
	 * <p>在这个例子中,创建了三个上下文的层次结构。
	 * (潜在的)优点是,如果懒惰标志设置为true,则只有在实际使用时才会创建上下文如果有一些仅需要的代码一些时候,这种机制可以节省一些资源另外,最后一个上下文的别名已经被创建了别名允许使用成语,其中客户端代
	 * 码要求具有代表代码所在的包或模块的id的上下文,以及SingletonBeanFactoryLocator的实际定义文件将该id映射到真实的上下文id。
	 * <p>在这个例子中,创建了三个上下文的层次结构。
	 * 
	 *  <p>最后一个例子比较复杂,每个模块都有一个{@code beanRefFactoryxml}所有的文件都会自动组合起来,以创建最终定义
	 * 
	 * <p> {@ code beanRefFactoryxml}文件里面的jar for util模块：
	 * 
	 * <pre class="code">&lt;?xml version="1.0" encoding="UTF-8"?>
	 * &lt;!DOCTYPE beans PUBLIC "-//SPRING//DTD BEAN 2.0//EN" "http://www.springframework.org/dtd/spring-beans-2.0.dtd">
	 * 
	 * &lt;beans>
	 *  &lt; bean id ="commycompanymyapputil"lazy-init ="true"
	 * class="org.springframework.context.support.ClassPathXmlApplicationContext">
	 * &lt;constructor-arg>
	 * &lt;value>com/mycompany/myapp/util/applicationContext.xml&lt;/value>
	 * &lt;/constructor-arg>
	 * &lt;/bean>
	 * &lt;/beans>
	 * </pre>
	 * 
	 *  {@code beanRefFactoryxml}文件在jar内为数据访问模块：<br>
	 * 
	 * <pre class="code">&lt;?xml version="1.0" encoding="UTF-8"?>
	 * &lt;!DOCTYPE beans PUBLIC "-//SPRING//DTD BEAN 2.0//EN" "http://www.springframework.org/dtd/spring-beans-2.0.dtd">
	 * 
	 * &lt;beans>
	 * &lt;!-- child of util -->
	 *  &lt; bean id ="commycompanymyappdataaccess"lazy-init ="true"
	 * class="org.springframework.context.support.ClassPathXmlApplicationContext">
	 * &lt;constructor-arg>
	 * &lt;list>&lt;value>com/mycompany/myapp/dataaccess/applicationContext.xml&lt;/value>&lt;/list>
	 * &lt;/constructor-arg>
	 * &lt;constructor-arg>
	 * &lt;ref bean="com.mycompany.myapp.util"/>
	 * &lt;/constructor-arg>
	 * &lt;/bean>
	 * &lt;/beans>
	 * </pre>
	 * 
	 *  {@code beanRefFactoryxml}文件内的jar for services模块：
	 * 
	 * <pre class="code">&lt;?xml version="1.0" encoding="UTF-8"?>
	 * &lt;!DOCTYPE beans PUBLIC "-//SPRING//DTD BEAN 2.0//EN" "http://www.springframework.org/dtd/spring-beans-2.0.dtd">
	 * 
	 * &lt;beans>
	 * &lt;!-- child of data-access -->
	 * 
	 * @return the corresponding BeanFactoryLocator instance
	 * @throws BeansException in case of factory loading failure
	 */
	public static BeanFactoryLocator getInstance() throws BeansException {
		return getInstance(null);
	}

	/**
	 * Returns an instance which uses the specified selector, as the name of the
	 * definition file(s). In the case of a name with a Spring 'classpath*:' prefix,
	 * or with no prefix, which is treated the same, the current thread context
	 * ClassLoader's {@code getResources} method will be called with this value
	 * to get all resources having that name. These resources will then be combined to
	 * form a definition. In the case where the name uses a Spring 'classpath:' prefix,
	 * or a standard URL prefix, then only one resource file will be loaded as the
	 * definition.
	 * <p>
	 *  &lt; bean id ="commycompanymyappservices"lazy-init ="true"
	 * class="org.springframework.context.support.ClassPathXmlApplicationContext">
	 * &lt;constructor-arg>
	 * &lt;list>&lt;value>com/mycompany/myapp/dataaccess/services.xml&lt;/value>&lt;/list>
	 * &lt;/constructor-arg>
	 * &lt;constructor-arg>
	 * &lt;ref bean="com.mycompany.myapp.dataaccess"/>
	 * &lt;/constructor-arg>
	 * &lt;/bean>
	 * &lt;/beans>
	 * </pre>
	 * 
	 *  {@code beanRefFactoryxml}文件在jar中为mypackage模块这不会创建任何自己的上下文,但允许其他的被引用为此模块已知的名称：
	 * 
	 * <pre class="code">&lt;?xml version="1.0" encoding="UTF-8"?>
	 * &lt;!DOCTYPE beans PUBLIC "-//SPRING//DTD BEAN 2.0//EN" "http://www.springframework.org/dtd/spring-beans-2.0.dtd">
	 * 
	 * &lt;beans>
	 * &lt;!-- define an alias for "com.mycompany.myapp.services" -->
	 *  &lt; alias name ="commycompanymyappservices"alias ="commycompanymyappmypackage"/&gt;
	 * &lt;/beans>
	 * </pre>
	 * 
	 * 
	 * @param selector the name of the resource(s) which will be read and
	 * combined to form the definition for the BeanFactoryLocator instance.
	 * Any such files must form a valid BeanFactory definition.
	 * @return the corresponding BeanFactoryLocator instance
	 * @throws BeansException in case of factory loading failure
	 */
	public static BeanFactoryLocator getInstance(String selector) throws BeansException {
		String resourceLocation = selector;
		if (resourceLocation == null) {
			resourceLocation = DEFAULT_RESOURCE_LOCATION;
		}

		// For backwards compatibility, we prepend 'classpath*:' to the selector name if there
		// is no other prefix (i.e. classpath*:, classpath:, or some URL prefix.
		if (!ResourcePatternUtils.isUrl(resourceLocation)) {
			resourceLocation = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + resourceLocation;
		}

		synchronized (instances) {
			if (logger.isTraceEnabled()) {
				logger.trace("SingletonBeanFactoryLocator.getInstance(): instances.hashCode=" +
						instances.hashCode() + ", instances=" + instances);
			}
			BeanFactoryLocator bfl = instances.get(resourceLocation);
			if (bfl == null) {
				bfl = new SingletonBeanFactoryLocator(resourceLocation);
				instances.put(resourceLocation, bfl);
			}
			return bfl;
		}
	}


	// We map BeanFactoryGroup objects by String keys, and by the definition object.
	private final Map<String, BeanFactoryGroup> bfgInstancesByKey = new HashMap<String, BeanFactoryGroup>();

	private final Map<BeanFactory, BeanFactoryGroup> bfgInstancesByObj = new HashMap<BeanFactory, BeanFactoryGroup>();

	private final String resourceLocation;


	/**
	 * Constructor which uses the specified name as the resource name
	 * of the definition file(s).
	 * <p>
	 * 返回一个使用默认"classpath *：beanRefFactoryxml"的实例,作为定义文件的名称通过调用当前线程上下文返回的所有资源使用此名称的ClassLoader的{@code getResources}
	 * 方法将被组合以创建一个BeanFactory定义集。
	 * 
	 * 
	 * @param resourceLocation the Spring resource location to use
	 * (either a URL or a "classpath:" / "classpath*:" pseudo URL)
	 */
	protected SingletonBeanFactoryLocator(String resourceLocation) {
		this.resourceLocation = resourceLocation;
	}

	@Override
	public BeanFactoryReference useBeanFactory(String factoryKey) throws BeansException {
		synchronized (this.bfgInstancesByKey) {
			BeanFactoryGroup bfg = this.bfgInstancesByKey.get(this.resourceLocation);

			if (bfg != null) {
				bfg.refCount++;
			}
			else {
				// This group definition doesn't exist, we need to try to load it.
				if (logger.isTraceEnabled()) {
					logger.trace("Factory group with resource name [" + this.resourceLocation +
							"] requested. Creating new instance.");
				}

				// Create the BeanFactory but don't initialize it.
				BeanFactory groupContext = createDefinition(this.resourceLocation, factoryKey);

				// Record its existence now, before instantiating any singletons.
				bfg = new BeanFactoryGroup();
				bfg.definition = groupContext;
				bfg.refCount = 1;
				this.bfgInstancesByKey.put(this.resourceLocation, bfg);
				this.bfgInstancesByObj.put(groupContext, bfg);

				// Now initialize the BeanFactory. This may cause a re-entrant invocation
				// of this method, but since we've already added the BeanFactory to our
				// mappings, the next time it will be found and simply have its
				// reference count incremented.
				try {
					initializeDefinition(groupContext);
				}
				catch (BeansException ex) {
					this.bfgInstancesByKey.remove(this.resourceLocation);
					this.bfgInstancesByObj.remove(groupContext);
					throw new BootstrapException("Unable to initialize group definition. " +
							"Group resource name [" + this.resourceLocation + "], factory key [" + factoryKey + "]", ex);
				}
			}

			try {
				BeanFactory beanFactory;
				if (factoryKey != null) {
					beanFactory = bfg.definition.getBean(factoryKey, BeanFactory.class);
				}
				else {
					beanFactory = bfg.definition.getBean(BeanFactory.class);
				}
				return new CountingBeanFactoryReference(beanFactory, bfg.definition);
			}
			catch (BeansException ex) {
				throw new BootstrapException("Unable to return specified BeanFactory instance: factory key [" +
						factoryKey + "], from group with resource name [" + this.resourceLocation + "]", ex);
			}

		}
	}

	/**
	 * Actually creates definition in the form of a BeanFactory, given a resource name
	 * which supports standard Spring resource prefixes ('classpath:', 'classpath*:', etc.)
	 * This is split out as a separate method so that subclasses can override the actual
	 * type used (to be an ApplicationContext, for example).
	 * <p>The default implementation simply builds a
	 * {@link org.springframework.beans.factory.support.DefaultListableBeanFactory}
	 * and populates it using an
	 * {@link org.springframework.beans.factory.xml.XmlBeanDefinitionReader}.
	 * <p>This method should not instantiate any singletons. That function is performed
	 * by {@link #initializeDefinition initializeDefinition()}, which should also be
	 * overridden if this method is.
	 * <p>
	 * 返回一个使用指定的选择器的实例,作为定义文件的名称在具有Spring'classpath *：'前缀的名称的情况下,或者没有前缀,被视为相同的当前线程上下文将使用此值调用ClassLoader的{@code getResources}
	 * 方法来获取具有该名称的所有资源。
	 * 这些资源将被组合以形成定义。在名称使用Spring'classpath：'前缀或标准URL前缀的情况下,那么只会将一个资源文件作为定义加载。
	 * 
	 * 
	 * @param resourceLocation the resource location for this factory group
	 * @param factoryKey the bean name of the factory to obtain
	 * @return the corresponding BeanFactory reference
	 */
	protected BeanFactory createDefinition(String resourceLocation, String factoryKey) {
		DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

		try {
			Resource[] configResources = resourcePatternResolver.getResources(resourceLocation);
			if (configResources.length == 0) {
				throw new FatalBeanException("Unable to find resource for specified definition. " +
						"Group resource name [" + this.resourceLocation + "], factory key [" + factoryKey + "]");
			}
			reader.loadBeanDefinitions(configResources);
		}
		catch (IOException ex) {
			throw new BeanDefinitionStoreException(
					"Error accessing bean definition resource [" + this.resourceLocation + "]", ex);
		}
		catch (BeanDefinitionStoreException ex) {
			throw new FatalBeanException("Unable to load group definition: " +
					"group resource name [" + this.resourceLocation + "], factory key [" + factoryKey + "]", ex);
		}

		return factory;
	}

	/**
	 * Instantiate singletons and do any other normal initialization of the factory.
	 * Subclasses that override {@link #createDefinition createDefinition()} should
	 * also override this method.
	 * <p>
	 *  使用指定名称作为定义文件的资源名称的构造方法
	 * 
	 * 
	 * @param groupDef the factory returned by {@link #createDefinition createDefinition()}
	 */
	protected void initializeDefinition(BeanFactory groupDef) {
		if (groupDef instanceof ConfigurableListableBeanFactory) {
			((ConfigurableListableBeanFactory) groupDef).preInstantiateSingletons();
		}
	}

	/**
	 * Destroy definition in separate method so subclass may work with other definition types.
	 * <p>
	 * 实际上以一个BeanFactory的形式创建定义,给出一个资源名称,它支持标准的Spring资源前缀('classpath：','classpath *：'等等)。
	 * 这被分解为一个单独的方法,以便子类可以覆盖实际的类型使用(作为例如ApplicationContext)<p>默认实现只需构建一个{@link orgspringframeworkbeansfactorysupportDefaultListableBeanFactory}
	 * 并使用{@link orgspringframeworkbeansfactoryxmlXmlBeanDefinitionReader}填充<p>此方法不应实例化任何单例,该功能由{ @link #initializeDefinition initializeDefinition()}
	 * ,如果这个方法是。
	 * 实际上以一个BeanFactory的形式创建定义,给出一个资源名称,它支持标准的Spring资源前缀('classpath：','classpath *：'等等)。
	 * 
	 * 
	 * @param groupDef the factory returned by {@link #createDefinition createDefinition()}
	 * @param selector the resource location for this factory group
	 */
	protected void destroyDefinition(BeanFactory groupDef, String selector) {
		if (groupDef instanceof ConfigurableBeanFactory) {
			if (logger.isTraceEnabled()) {
				logger.trace("Factory group with selector '" + selector +
						"' being released, as there are no more references to it");
			}
			((ConfigurableBeanFactory) groupDef).destroySingletons();
		}
	}


	/**
	 * We track BeanFactory instances with this class.
	 * <p>
	 * 实例化单例并进行工厂的任何其他正常初始化覆盖{@link #createDefinition createDefinition()}的子类也应该覆盖此方法
	 * 
	 */
	private static class BeanFactoryGroup {

		private BeanFactory definition;

		private int refCount = 0;
	}


	/**
	 * BeanFactoryReference implementation for this locator.
	 * <p>
	 *  在单独的方法中破坏定义,所以子类可以与其他定义类型一起使用
	 * 
	 */
	private class CountingBeanFactoryReference implements BeanFactoryReference {

		private BeanFactory beanFactory;

		private BeanFactory groupContextRef;

		public CountingBeanFactoryReference(BeanFactory beanFactory, BeanFactory groupContext) {
			this.beanFactory = beanFactory;
			this.groupContextRef = groupContext;
		}

		@Override
		public BeanFactory getFactory() {
			return this.beanFactory;
		}

		// Note that it's legal to call release more than once!
		@Override
		public void release() throws FatalBeanException {
			synchronized (bfgInstancesByKey) {
				BeanFactory savedRef = this.groupContextRef;
				if (savedRef != null) {
					this.groupContextRef = null;
					BeanFactoryGroup bfg = bfgInstancesByObj.get(savedRef);
					if (bfg != null) {
						bfg.refCount--;
						if (bfg.refCount == 0) {
							destroyDefinition(savedRef, resourceLocation);
							bfgInstancesByKey.remove(resourceLocation);
							bfgInstancesByObj.remove(savedRef);
						}
					}
					else {
						// This should be impossible.
						logger.warn("Tried to release a SingletonBeanFactoryLocator group definition " +
								"more times than it has actually been used. Resource name [" + resourceLocation + "]");
					}
				}
			}
		}
	}

}
