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

package org.springframework.context;

import java.io.Closeable;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ProtocolResolver;

/**
 * SPI interface to be implemented by most if not all application contexts.
 * Provides facilities to configure an application context in addition
 * to the application context client methods in the
 * {@link org.springframework.context.ApplicationContext} interface.
 *
 * <p>Configuration and lifecycle methods are encapsulated here to avoid
 * making them obvious to ApplicationContext client code. The present
 * methods should only be used by startup and shutdown code.
 *
 * <p>
 * 由大多数(如果不是全部)应用程序上下文实现的SPI接口除了在{@link orgspringframeworkcontextApplicationContext}接口中的应用程序上下文客户端方法之外,还
 * 提供配置应用程序上下文的功能。
 * 
 *  <p>配置和生命周期方法被封装在这里,以避免使它们对ApplicationContext客户端代码显而易见本方法应仅由启动和关闭代码使用
 * 
 * 
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 03.11.2003
 */
public interface ConfigurableApplicationContext extends ApplicationContext, Lifecycle, Closeable {

	/**
	 * Any number of these characters are considered delimiters between
	 * multiple context config paths in a single String value.
	 * <p>
	 *  任何数量的这些字符在单个String值中被认为是多个上下文配置路径之间的分隔符
	 * 
	 * 
	 * @see org.springframework.context.support.AbstractXmlApplicationContext#setConfigLocation
	 * @see org.springframework.web.context.ContextLoader#CONFIG_LOCATION_PARAM
	 * @see org.springframework.web.servlet.FrameworkServlet#setContextConfigLocation
	 */
	String CONFIG_LOCATION_DELIMITERS = ",; \t\n";

	/**
	 * Name of the ConversionService bean in the factory.
	 * If none is supplied, default conversion rules apply.
	 * <p>
	 *  出厂时的ConversionService bean的名称如果没有提供,则适用默认转换规则
	 * 
	 * 
	 * @see org.springframework.core.convert.ConversionService
	 */
	String CONVERSION_SERVICE_BEAN_NAME = "conversionService";

	/**
	 * Name of the LoadTimeWeaver bean in the factory. If such a bean is supplied,
	 * the context will use a temporary ClassLoader for type matching, in order
	 * to allow the LoadTimeWeaver to process all actual bean classes.
	 * <p>
	 * 工厂中LoadTimeWeaver bean的名称如果提供了这样的bean,则上下文将使用临时ClassLoader进行类型匹配,以便允许LoadTimeWeaver处理所有实际的bean类
	 * 
	 * 
	 * @see org.springframework.instrument.classloading.LoadTimeWeaver
	 */
	String LOAD_TIME_WEAVER_BEAN_NAME = "loadTimeWeaver";

	/**
	 * Name of the {@link Environment} bean in the factory.
	 * <p>
	 *  工厂中的{@link Environment} bean的名称
	 * 
	 */
	String ENVIRONMENT_BEAN_NAME = "environment";

	/**
	 * Name of the System properties bean in the factory.
	 * <p>
	 *  工厂中系统属性bean的名称
	 * 
	 * 
	 * @see java.lang.System#getProperties()
	 */
	String SYSTEM_PROPERTIES_BEAN_NAME = "systemProperties";

	/**
	 * Name of the System environment bean in the factory.
	 * <p>
	 *  工厂中系统环境bean的名称
	 * 
	 * 
	 * @see java.lang.System#getenv()
	 */
	String SYSTEM_ENVIRONMENT_BEAN_NAME = "systemEnvironment";


	/**
	 * Set the unique id of this application context.
	 * <p>
	 *  设置此应用程序上下文的唯一ID
	 * 
	 */
	void setId(String id);

	/**
	 * Set the parent of this application context.
	 * <p>Note that the parent shouldn't be changed: It should only be set outside
	 * a constructor if it isn't available when an object of this class is created,
	 * for example in case of WebApplicationContext setup.
	 * <p>
	 *  设置此应用程序上下文的父级<p>请注意,父级不应该被更改：如果在创建此类的对象时不可用,则应该在构造函数之外设置,例如在WebApplicationContext设置的情况下
	 * 
	 * 
	 * @param parent the parent context
	 * @see org.springframework.web.context.ConfigurableWebApplicationContext
	 */
	void setParent(ApplicationContext parent);

	/**
	 * Return the Environment for this application context in configurable form.
	 * <p>
	 *  以可配置的形式返回此应用程序环境
	 * 
	 */
	@Override
	ConfigurableEnvironment getEnvironment();

	/**
	 * Set the {@code Environment} for this application context.
	 * <p>
	 * 为此应用程序上下文设置{@code Environment}
	 * 
	 * 
	 * @param environment the new environment
	 */
	void setEnvironment(ConfigurableEnvironment environment);

	/**
	 * Add a new BeanFactoryPostProcessor that will get applied to the internal
	 * bean factory of this application context on refresh, before any of the
	 * bean definitions get evaluated. To be invoked during context configuration.
	 * <p>
	 *  添加一个新的BeanFactoryPostProcessor,将在应用于此应用程序上下文的内部bean工厂之前刷新,之前任何bean定义被评估要在上下文配置期间被调用
	 * 
	 * 
	 * @param postProcessor the factory processor to register
	 */
	void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor);

	/**
	 * Add a new ApplicationListener that will be notified on context events
	 * such as context refresh and context shutdown.
	 * <p>Note that any ApplicationListener registered here will be applied
	 * on refresh if the context is not active yet, or on the fly with the
	 * current event multicaster in case of a context that is already active.
	 * <p>
	 *  添加一个将在上下文事件(例如上下文更新和上下文关闭)上通知的新的ApplicationListener。
	 * 请注意,如果上下文尚未激活,则在此处注册的任何ApplicationListener将被应用于刷新,或者与当前事件多节点已经活跃的上下文的情况。
	 * 
	 * 
	 * @param listener the ApplicationListener to register
	 * @see org.springframework.context.event.ContextRefreshedEvent
	 * @see org.springframework.context.event.ContextClosedEvent
	 */
	void addApplicationListener(ApplicationListener<?> listener);

	/**
	 * Register the given protocol resolver with this application context,
	 * allowing for additional resource protocols to be handled.
	 * <p>Any such resolver will be invoked ahead of this context's standard
	 * resolution rules. It may therefore also override any default rules.
	 * <p>
	 * 使用此应用程序上下文注册给定的协议解析器,允许处理其他资源协议<p>任何此类解析器将在此上下文的标准分辨率规则之前被调用。因此,它也可以覆盖任何默认规则
	 * 
	 * 
	 * @since 4.3
	 */
	void addProtocolResolver(ProtocolResolver resolver);

	/**
	 * Load or refresh the persistent representation of the configuration,
	 * which might an XML file, properties file, or relational database schema.
	 * <p>As this is a startup method, it should destroy already created singletons
	 * if it fails, to avoid dangling resources. In other words, after invocation
	 * of that method, either all or no singletons at all should be instantiated.
	 * <p>
	 *  加载或刷新配置的持久表示,可能是XML文件,属性文件或关系数据库模式。
	 * 由于这是启动方法,所以应该销毁已经创建的单例,如果它失败了,以避免资源悬空换句话说在调用该方法后,全部或者全部没有单例应该被实例化。
	 * 
	 * 
	 * @throws BeansException if the bean factory could not be initialized
	 * @throws IllegalStateException if already initialized and multiple refresh
	 * attempts are not supported
	 */
	void refresh() throws BeansException, IllegalStateException;

	/**
	 * Register a shutdown hook with the JVM runtime, closing this context
	 * on JVM shutdown unless it has already been closed at that time.
	 * <p>This method can be called multiple times. Only one shutdown hook
	 * (at max) will be registered for each context instance.
	 * <p>
	 * 使用JVM运行时注册一个关机钩子,关闭JVM关闭时的上下文,除非它已经被关闭了<p>这个方法可以被多次调用只会为每个上下文实例注册一个关闭钩子(at max)
	 * 
	 * 
	 * @see java.lang.Runtime#addShutdownHook
	 * @see #close()
	 */
	void registerShutdownHook();

	/**
	 * Close this application context, releasing all resources and locks that the
	 * implementation might hold. This includes destroying all cached singleton beans.
	 * <p>Note: Does <i>not</i> invoke {@code close} on a parent context;
	 * parent contexts have their own, independent lifecycle.
	 * <p>This method can be called multiple times without side effects: Subsequent
	 * {@code close} calls on an already closed context will be ignored.
	 * <p>
	 *  关闭这个应用程序上下文,释放实现可能保存的所有资源和锁定这包括摧毁所有缓存的单例bean <p>注意：<i>不是</i>在父上下文中调用{@code close}父上下文有自己的独立生命周期<p>此方
	 * 法可以多次调用而无副作用：已关闭的上下文的后续{@code close}调用将被忽略。
	 * 
	 */
	@Override
	void close();

	/**
	 * Determine whether this application context is active, that is,
	 * whether it has been refreshed at least once and has not been closed yet.
	 * <p>
	 * 确定此应用程序上下文是否处于活动状态,即是否已刷新至少一次,尚未关闭
	 * 
	 * 
	 * @return whether the context is still active
	 * @see #refresh()
	 * @see #close()
	 * @see #getBeanFactory()
	 */
	boolean isActive();

	/**
	 * Return the internal bean factory of this application context.
	 * Can be used to access specific functionality of the underlying factory.
	 * <p>Note: Do not use this to post-process the bean factory; singletons
	 * will already have been instantiated before. Use a BeanFactoryPostProcessor
	 * to intercept the BeanFactory setup process before beans get touched.
	 * <p>Generally, this internal factory will only be accessible while the context
	 * is active, that is, inbetween {@link #refresh()} and {@link #close()}.
	 * The {@link #isActive()} flag can be used to check whether the context
	 * is in an appropriate state.
	 * <p>
	 *  返回此应用程序上下文的内部bean工厂可以用于访问底层工厂的特定功能<p>注意：不要使用它来后台处理bean工厂;单个已经被实例化之前使用BeanFactoryPostProcessor拦截BeanF
	 * actory设置过程之前bean被触摸<p>通常,这个内部工厂只有在上下文活动时才可访问,也就是{@link #refresh()}和{@link #close()}可以使用{@link #isActive()}
	 * 
	 * @return the underlying bean factory
	 * @throws IllegalStateException if the context does not hold an internal
	 * bean factory (usually if {@link #refresh()} hasn't been called yet or
	 * if {@link #close()} has already been called)
	 * @see #isActive()
	 * @see #refresh()
	 * @see #close()
	 * @see #addBeanFactoryPostProcessor
	 */
	ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;

}
