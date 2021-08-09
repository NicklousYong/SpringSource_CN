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

package org.springframework.context;

import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * Central interface to provide configuration for an application.
 * This is read-only while the application is running, but may be
 * reloaded if the implementation supports this.
 *
 * <p>An ApplicationContext provides:
 * <ul>
 * <li>Bean factory methods for accessing application components.
 * Inherited from {@link org.springframework.beans.factory.ListableBeanFactory}.
 * <li>The ability to load file resources in a generic fashion.
 * Inherited from the {@link org.springframework.core.io.ResourceLoader} interface.
 * <li>The ability to publish events to registered listeners.
 * Inherited from the {@link ApplicationEventPublisher} interface.
 * <li>The ability to resolve messages, supporting internationalization.
 * Inherited from the {@link MessageSource} interface.
 * <li>Inheritance from a parent context. Definitions in a descendant context
 * will always take priority. This means, for example, that a single parent
 * context can be used by an entire web application, while each servlet has
 * its own child context that is independent of that of any other servlet.
 * </ul>
 *
 * <p>In addition to standard {@link org.springframework.beans.factory.BeanFactory}
 * lifecycle capabilities, ApplicationContext implementations detect and invoke
 * {@link ApplicationContextAware} beans as well as {@link ResourceLoaderAware},
 * {@link ApplicationEventPublisherAware} and {@link MessageSourceAware} beans.
 *
 * <p>
 *  为应用程序提供配置的中央接口在应用程序运行时,这是只读的,但如果实现支持此操作,则可能会重新加载
 * 
 *  一个ApplicationContext提供：
 * <ul>
 * <li>用于访问应用程序组件的Bean工厂方法从{@link orgspringframeworkbeansfactoryListableBeanFactory}继承<li>以通用方式加载文件资源的能力
 * 从{@link orgspringframeworkcoreioResourceLoader}界面继承<li>将事件发布到注册的监听器从{@link ApplicationEventPublisher}
 * 界面继承<li>解决消息的能力,支持国际化从{@link MessageSource}界面继承<li>从父上下文继承在后代上下文中的定义将始终优先这意味着,例如,整个Web应用程序可以使用单个父上下文,
 * 而每个servlet都有自己的独立于任何其他servlet的子上下文。
 * </ul>
 * 
 * <p>除了标准的{@link orgspringframeworkbeansfactoryBeanFactory}生命周期功能之外,ApplicationContext实现还会检测并调用{@link ApplicationContextAware}
 *  bean以及{@link ResourceLoaderAware},{@link ApplicationEventPublisherAware}和{@link MessageSourceAware} 
 * bean。
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see ConfigurableApplicationContext
 * @see org.springframework.beans.factory.BeanFactory
 * @see org.springframework.core.io.ResourceLoader
 */
public interface ApplicationContext extends EnvironmentCapable, ListableBeanFactory, HierarchicalBeanFactory,
		MessageSource, ApplicationEventPublisher, ResourcePatternResolver {

	/**
	 * Return the unique id of this application context.
	 * <p>
	 *  返回此应用程序上下文的唯一ID
	 * 
	 * 
	 * @return the unique id of the context, or {@code null} if none
	 */
	String getId();

	/**
	 * Return a name for the deployed application that this context belongs to.
	 * <p>
	 *  返回此上下文所属的部署应用程序的名称
	 * 
	 * 
	 * @return a name for the deployed application, or the empty String by default
	 */
	String getApplicationName();

	/**
	 * Return a friendly name for this context.
	 * <p>
	 *  为此上下文返回友好名称
	 * 
	 * 
	 * @return a display name for this context (never {@code null})
	 */
	String getDisplayName();

	/**
	 * Return the timestamp when this context was first loaded.
	 * <p>
	 *  在首次加载上下文时返回时间戳
	 * 
	 * 
	 * @return the timestamp (ms) when this context was first loaded
	 */
	long getStartupDate();

	/**
	 * Return the parent context, or {@code null} if there is no parent
	 * and this is the root of the context hierarchy.
	 * <p>
	 *  返回父上下文,如果没有父项,则返回{@code null},而这是上下文层次结构的根
	 * 
	 * 
	 * @return the parent context, or {@code null} if there is no parent
	 */
	ApplicationContext getParent();

	/**
	 * Expose AutowireCapableBeanFactory functionality for this context.
	 * <p>This is not typically used by application code, except for the purpose of
	 * initializing bean instances that live outside of the application context,
	 * applying the Spring bean lifecycle (fully or partly) to them.
	 * <p>Alternatively, the internal BeanFactory exposed by the
	 * {@link ConfigurableApplicationContext} interface offers access to the
	 * {@link AutowireCapableBeanFactory} interface too. The present method mainly
	 * serves as a convenient, specific facility on the ApplicationContext interface.
	 * <p><b>NOTE: As of 4.2, this method will consistently throw IllegalStateException
	 * after the application context has been closed.</b> In current Spring Framework
	 * versions, only refreshable application contexts behave that way; as of 4.2,
	 * all application context implementations will be required to comply.
	 * <p>
	 * 为此上下文提供AutowireCapableBeanFactory功能<p>应用程序代码通常不会使用这种方法,除了初始化生活在应用程序上下文之外的bean实例的目的之外,将Spring bean生命周期
	 * (完全或部分)应用于它们。
	 *  ,由{@link ConfigurableApplicationContext}接口公开的内部BeanFactory也提供对{@link AutowireCapableBeanFactory}接口的访
	 * 问。
	 * 
	 * @return the AutowireCapableBeanFactory for this context
	 * @throws IllegalStateException if the context does not support the
	 * {@link AutowireCapableBeanFactory} interface, or does not hold an
	 * autowire-capable bean factory yet (e.g. if {@code refresh()} has
	 * never been called), or if the context has been closed already
	 * @see ConfigurableApplicationContext#refresh()
	 * @see ConfigurableApplicationContext#getBeanFactory()
	 */
	AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException;

}
