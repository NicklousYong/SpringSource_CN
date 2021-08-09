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

import java.io.IOException;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;

/**
 * Base class for {@link org.springframework.context.ApplicationContext}
 * implementations which are supposed to support multiple calls to {@link #refresh()},
 * creating a new internal bean factory instance every time.
 * Typically (but not necessarily), such a context will be driven by
 * a set of config locations to load bean definitions from.
 *
 * <p>The only method to be implemented by subclasses is {@link #loadBeanDefinitions},
 * which gets invoked on each refresh. A concrete implementation is supposed to load
 * bean definitions into the given
 * {@link org.springframework.beans.factory.support.DefaultListableBeanFactory},
 * typically delegating to one or more specific bean definition readers.
 *
 * <p><b>Note that there is a similar base class for WebApplicationContexts.</b>
 * {@link org.springframework.web.context.support.AbstractRefreshableWebApplicationContext}
 * provides the same subclassing strategy, but additionally pre-implements
 * all context functionality for web environments. There is also a
 * pre-defined way to receive config locations for a web context.
 *
 * <p>Concrete standalone subclasses of this base class, reading in a
 * specific bean definition format, are {@link ClassPathXmlApplicationContext}
 * and {@link FileSystemXmlApplicationContext}, which both derive from the
 * common {@link AbstractXmlApplicationContext} base class;
 * {@link org.springframework.context.annotation.AnnotationConfigApplicationContext}
 * supports {@code @Configuration}-annotated classes as a source of bean definitions.
 *
 * <p>
 * 应该支持多次调用{@link #refresh()}的{@link orgspringframeworkcontextApplicationContext}实现的基类,每次都创建一个新的内部bean工厂
 * 实例通常(但不一定),这样的上下文将由一组配置位置来加载bean定义。
 * 
 *  <p>子类实现的唯一方法是{@link #loadBeanDefinitions},它在每次刷新时被调用。
 * 具体的实现应该是将bean定义加载到给定的{@link orgspringframeworkbeansfactorysupportDefaultListableBeanFactory}中,通常委托给一个
 * 或多个特定的bean定义阅读器。
 *  <p>子类实现的唯一方法是{@link #loadBeanDefinitions},它在每次刷新时被调用。
 * 
 * <p> <b>请注意,WebApplicationContexts有一个类似的基类。
 * </b> {@link orgspringframeworkwebcontextsupportAbstractRefreshableWebApplicationContext}提供了相同的子类化策略,但
 * 另外预先实现了Web环境的所有上下文功能还有一个预定义的方式接收Web上下文的配置位置。
 * <p> <b>请注意,WebApplicationContexts有一个类似的基类。
 * 
 * <p>这个基类的具体独立子类,以特定的bean定义格式读取,是{@link ClassPathXmlApplicationContext}和{@link FileSystemXmlApplicationContext}
 * ,它们都是从普通的{@link AbstractXmlApplicationContext}基类派生出来的。
 *  {@link orgspringframeworkcontextannotationAnnotationConfigApplicationContext}支持{@code @Configuration}
 * 注释类作为bean定义的源。
 * 
 * 
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 1.1.3
 * @see #loadBeanDefinitions
 * @see org.springframework.beans.factory.support.DefaultListableBeanFactory
 * @see org.springframework.web.context.support.AbstractRefreshableWebApplicationContext
 * @see AbstractXmlApplicationContext
 * @see ClassPathXmlApplicationContext
 * @see FileSystemXmlApplicationContext
 * @see org.springframework.context.annotation.AnnotationConfigApplicationContext
 */
public abstract class AbstractRefreshableApplicationContext extends AbstractApplicationContext {

	private Boolean allowBeanDefinitionOverriding;

	private Boolean allowCircularReferences;

	/** Bean factory for this context */
	private DefaultListableBeanFactory beanFactory;

	/** Synchronization monitor for the internal BeanFactory */
	private final Object beanFactoryMonitor = new Object();


	/**
	 * Create a new AbstractRefreshableApplicationContext with no parent.
	 * <p>
	 *  创建一个没有父项的AbstractRefreshableApplicationContext
	 * 
	 */
	public AbstractRefreshableApplicationContext() {
	}

	/**
	 * Create a new AbstractRefreshableApplicationContext with the given parent context.
	 * <p>
	 *  使用给定的父上下文创建一个新的AbstractRefreshableApplicationContext
	 * 
	 * 
	 * @param parent the parent context
	 */
	public AbstractRefreshableApplicationContext(ApplicationContext parent) {
		super(parent);
	}


	/**
	 * Set whether it should be allowed to override bean definitions by registering
	 * a different definition with the same name, automatically replacing the former.
	 * If not, an exception will be thrown. Default is "true".
	 * <p>
	 * 设置是否允许通过注册具有相同名称的不同定义来覆盖bean定义,自动替换前者如果不是,将抛出异常默认为"true"
	 * 
	 * 
	 * @see org.springframework.beans.factory.support.DefaultListableBeanFactory#setAllowBeanDefinitionOverriding
	 */
	public void setAllowBeanDefinitionOverriding(boolean allowBeanDefinitionOverriding) {
		this.allowBeanDefinitionOverriding = allowBeanDefinitionOverriding;
	}

	/**
	 * Set whether to allow circular references between beans - and automatically
	 * try to resolve them.
	 * <p>Default is "true". Turn this off to throw an exception when encountering
	 * a circular reference, disallowing them completely.
	 * <p>
	 *  设置是否允许bean之间的循环引用 - 并自动尝试解决它们<p>默认值为"true"在遇到循环引用时将其关闭以抛出异常,不允许它们完全
	 * 
	 * 
	 * @see org.springframework.beans.factory.support.DefaultListableBeanFactory#setAllowCircularReferences
	 */
	public void setAllowCircularReferences(boolean allowCircularReferences) {
		this.allowCircularReferences = allowCircularReferences;
	}


	/**
	 * This implementation performs an actual refresh of this context's underlying
	 * bean factory, shutting down the previous bean factory (if any) and
	 * initializing a fresh bean factory for the next phase of the context's lifecycle.
	 * <p>
	 *  此实现对该上下文的基础bean工厂进行实际刷新,关闭以前的bean工厂(如果有),并为上下文生命周期的下一阶段初始化一个新鲜bean工厂
	 * 
	 */
	@Override
	protected final void refreshBeanFactory() throws BeansException {
		if (hasBeanFactory()) {
			destroyBeans();
			closeBeanFactory();
		}
		try {
			DefaultListableBeanFactory beanFactory = createBeanFactory();
			beanFactory.setSerializationId(getId());
			customizeBeanFactory(beanFactory);
			loadBeanDefinitions(beanFactory);
			synchronized (this.beanFactoryMonitor) {
				this.beanFactory = beanFactory;
			}
		}
		catch (IOException ex) {
			throw new ApplicationContextException("I/O error parsing bean definition source for " + getDisplayName(), ex);
		}
	}

	@Override
	protected void cancelRefresh(BeansException ex) {
		synchronized (this.beanFactoryMonitor) {
			if (this.beanFactory != null)
				this.beanFactory.setSerializationId(null);
		}
		super.cancelRefresh(ex);
	}

	@Override
	protected final void closeBeanFactory() {
		synchronized (this.beanFactoryMonitor) {
			this.beanFactory.setSerializationId(null);
			this.beanFactory = null;
		}
	}

	/**
	 * Determine whether this context currently holds a bean factory,
	 * i.e. has been refreshed at least once and not been closed yet.
	 * <p>
	 *  确定这个上下文当前是否持有一个bean工厂,即至少已经刷新一次并且还没有被关闭
	 * 
	 */
	protected final boolean hasBeanFactory() {
		synchronized (this.beanFactoryMonitor) {
			return (this.beanFactory != null);
		}
	}

	@Override
	public final ConfigurableListableBeanFactory getBeanFactory() {
		synchronized (this.beanFactoryMonitor) {
			if (this.beanFactory == null) {
				throw new IllegalStateException("BeanFactory not initialized or already closed - " +
						"call 'refresh' before accessing beans via the ApplicationContext");
			}
			return this.beanFactory;
		}
	}

	/**
	 * Overridden to turn it into a no-op: With AbstractRefreshableApplicationContext,
	 * {@link #getBeanFactory()} serves a strong assertion for an active context anyway.
	 * <p>
	 * 被覆盖将其转换为无操作：使用AbstractRefreshableApplicationContext,{@link #getBeanFactory()}无论如何还为活动上下文提供强大的断言
	 * 
	 */
	@Override
	protected void assertBeanFactoryActive() {
	}

	/**
	 * Create an internal bean factory for this context.
	 * Called for each {@link #refresh()} attempt.
	 * <p>The default implementation creates a
	 * {@link org.springframework.beans.factory.support.DefaultListableBeanFactory}
	 * with the {@linkplain #getInternalParentBeanFactory() internal bean factory} of this
	 * context's parent as parent bean factory. Can be overridden in subclasses,
	 * for example to customize DefaultListableBeanFactory's settings.
	 * <p>
	 *  为此上下文创建一个内部bean工厂为每个{@link #refresh()}尝试而调用<p>默认实现使用该上下文父文件的{@linkplain #getInternalParentBeanFactory()内部bean工厂}
	 * 创建一个{@link orgspringframeworkbeansfactorysupportDefaultListableBeanFactory}作为父bean工厂可以在子类中覆盖,例如自定义Def
	 * aultListableBeanFactory的设置。
	 * 
	 * 
	 * @return the bean factory for this context
	 * @see org.springframework.beans.factory.support.DefaultListableBeanFactory#setAllowBeanDefinitionOverriding
	 * @see org.springframework.beans.factory.support.DefaultListableBeanFactory#setAllowEagerClassLoading
	 * @see org.springframework.beans.factory.support.DefaultListableBeanFactory#setAllowCircularReferences
	 * @see org.springframework.beans.factory.support.DefaultListableBeanFactory#setAllowRawInjectionDespiteWrapping
	 */
	protected DefaultListableBeanFactory createBeanFactory() {
		return new DefaultListableBeanFactory(getInternalParentBeanFactory());
	}

	/**
	 * Customize the internal bean factory used by this context.
	 * Called for each {@link #refresh()} attempt.
	 * <p>The default implementation applies this context's
	 * {@linkplain #setAllowBeanDefinitionOverriding "allowBeanDefinitionOverriding"}
	 * and {@linkplain #setAllowCircularReferences "allowCircularReferences"} settings,
	 * if specified. Can be overridden in subclasses to customize any of
	 * {@link DefaultListableBeanFactory}'s settings.
	 * <p>
	 * 自定义此上下文使用的内部bean工厂调用每个{@link #refresh()}尝试<p>默认实现应用此上下文的{@linkplain #setAllowBeanDefinitionOverriding"allowBeanDefinitionOverriding"}
	 * 和{@linkplain #setAllowCircularReferences"allowCircularReferences"}设置,如果指定可以在子类中覆盖以自定义任何{@link DefaultListableBeanFactory}
	 * 的设置。
	 * 
	 * 
	 * @param beanFactory the newly created bean factory for this context
	 * @see DefaultListableBeanFactory#setAllowBeanDefinitionOverriding
	 * @see DefaultListableBeanFactory#setAllowCircularReferences
	 * @see DefaultListableBeanFactory#setAllowRawInjectionDespiteWrapping
	 * @see DefaultListableBeanFactory#setAllowEagerClassLoading
	 */
	protected void customizeBeanFactory(DefaultListableBeanFactory beanFactory) {
		if (this.allowBeanDefinitionOverriding != null) {
			beanFactory.setAllowBeanDefinitionOverriding(this.allowBeanDefinitionOverriding);
		}
		if (this.allowCircularReferences != null) {
			beanFactory.setAllowCircularReferences(this.allowCircularReferences);
		}
	}

	/**
	 * Load bean definitions into the given bean factory, typically through
	 * delegating to one or more bean definition readers.
	 * <p>
	 * 
	 * @param beanFactory the bean factory to load bean definitions into
	 * @throws BeansException if parsing of the bean definitions failed
	 * @throws IOException if loading of bean definition files failed
	 * @see org.springframework.beans.factory.support.PropertiesBeanDefinitionReader
	 * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader
	 */
	protected abstract void loadBeanDefinitions(DefaultListableBeanFactory beanFactory)
			throws BeansException, IOException;

}
