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

package org.springframework.context.support;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Assert;

/**
 * Generic ApplicationContext implementation that holds a single internal
 * {@link org.springframework.beans.factory.support.DefaultListableBeanFactory}
 * instance and does not assume a specific bean definition format. Implements
 * the {@link org.springframework.beans.factory.support.BeanDefinitionRegistry}
 * interface in order to allow for applying any bean definition readers to it.
 *
 * <p>Typical usage is to register a variety of bean definitions via the
 * {@link org.springframework.beans.factory.support.BeanDefinitionRegistry}
 * interface and then call {@link #refresh()} to initialize those beans
 * with application context semantics (handling
 * {@link org.springframework.context.ApplicationContextAware}, auto-detecting
 * {@link org.springframework.beans.factory.config.BeanFactoryPostProcessor BeanFactoryPostProcessors},
 * etc).
 *
 * <p>In contrast to other ApplicationContext implementations that create a new
 * internal BeanFactory instance for each refresh, the internal BeanFactory of
 * this context is available right from the start, to be able to register bean
 * definitions on it. {@link #refresh()} may only be called once.
 *
 * <p>Usage example:
 *
 * <pre class="code">
 * GenericApplicationContext ctx = new GenericApplicationContext();
 * XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(ctx);
 * xmlReader.loadBeanDefinitions(new ClassPathResource("applicationContext.xml"));
 * PropertiesBeanDefinitionReader propReader = new PropertiesBeanDefinitionReader(ctx);
 * propReader.loadBeanDefinitions(new ClassPathResource("otherBeans.properties"));
 * ctx.refresh();
 *
 * MyBean myBean = (MyBean) ctx.getBean("myBean");
 * ...</pre>
 *
 * For the typical case of XML bean definitions, simply use
 * {@link ClassPathXmlApplicationContext} or {@link FileSystemXmlApplicationContext},
 * which are easier to set up - but less flexible, since you can just use standard
 * resource locations for XML bean definitions, rather than mixing arbitrary bean
 * definition formats. The equivalent in a web environment is
 * {@link org.springframework.web.context.support.XmlWebApplicationContext}.
 *
 * <p>For custom application context implementations that are supposed to read
 * special bean definition formats in a refreshable manner, consider deriving
 * from the {@link AbstractRefreshableApplicationContext} base class.
 *
 * <p>
 * 通用ApplicationContext实现,它包含单个内部{@link orgspringframeworkbeansfactorysupportDefaultListableBeanFactory}
 * 实例,并且不承担特定的bean定义格式实现{@link orgspringframeworkbeansfactorysupportBeanDefinitionRegistry}接口,以便允许将任何bea
 * n定义阅读器应用于它。
 * 
 * <p>典型用法是通过{@link orgspringframeworkbeansfactorysupportBeanDefinitionRegistry}接口注册各种bean定义,然后调用{@link #refresh()}
 * 以应用程序上下文语义来初始化这些bean(处理{@link orgspringframeworkcontextApplicationContextAware}检测{@link orgspringframeworkbeansfactoryconfigBeanFactoryPostProcessor BeanFactoryPostProcessors}
 * 等)。
 * 
 *  <p>与其他针对每次刷新创建新的内部BeanFactory实例的ApplicationContext实现相反,此上下文的内部BeanFactory从一开始就可以使用,以便能够在其上注册bean定义{@link #refresh()}
 * 可能只能叫一次。
 * 
 *  <p>使用示例：
 * 
 * <pre class="code">
 * GenericApplicationContext ctx = new GenericApplicationContext(); XmlBeanDefinitionReader xmlReader = 
 * new XmlBeanDefinitionReader(ctx); xmlReaderloadBeanDefinitions(new ClassPathResource("applicationCont
 * extxml")); PropertiesBeanDefinitionReader propReader = new PropertiesBeanDefinitionReader(ctx); propR
 * eaderloadBeanDefinitions(new ClassPathResource("otherBeansproperties")); ctxrefresh();。
 * 
 *  MyBean myBean =(MyBean)ctxgetBean("myBean"); </PRE>
 * 
 * 对于XML bean定义的典型情况,只需使用易于设置的{@link ClassPathXmlApplicationContext}或{@link FileSystemXmlApplicationContext}
 * ,但不太灵活,因为您只能使用标准资源位置进行XML bean定义,而不是混合任意bean定义格式Web环境中的等价物是{@link orgspringframeworkwebcontextsupportXmlWebApplicationContext}
 * 。
 * 
 *  <p>对于应该以可刷新方式读取特殊的bean定义格式的自定义应用程序上下文实现,请考虑从{@link AbstractRefreshableApplicationContext}基类派生
 * 
 * 
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 1.1.2
 * @see #registerBeanDefinition
 * @see #refresh()
 * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader
 * @see org.springframework.beans.factory.support.PropertiesBeanDefinitionReader
 */
public class GenericApplicationContext extends AbstractApplicationContext implements BeanDefinitionRegistry {

	private final DefaultListableBeanFactory beanFactory;

	private ResourceLoader resourceLoader;

	private boolean customClassLoader = false;

	private final AtomicBoolean refreshed = new AtomicBoolean();


	/**
	 * Create a new GenericApplicationContext.
	 * <p>
	 *  创建一个新的GenericApplicationContext
	 * 
	 * 
	 * @see #registerBeanDefinition
	 * @see #refresh
	 */
	public GenericApplicationContext() {
		this.beanFactory = new DefaultListableBeanFactory();
	}

	/**
	 * Create a new GenericApplicationContext with the given DefaultListableBeanFactory.
	 * <p>
	 *  使用给定的DefaultListableBeanFactory创建一个新的GenericApplicationContext
	 * 
	 * 
	 * @param beanFactory the DefaultListableBeanFactory instance to use for this context
	 * @see #registerBeanDefinition
	 * @see #refresh
	 */
	public GenericApplicationContext(DefaultListableBeanFactory beanFactory) {
		Assert.notNull(beanFactory, "BeanFactory must not be null");
		this.beanFactory = beanFactory;
	}

	/**
	 * Create a new GenericApplicationContext with the given parent.
	 * <p>
	 * 与给定的父对象创建一个新的GenericApplicationContext
	 * 
	 * 
	 * @param parent the parent application context
	 * @see #registerBeanDefinition
	 * @see #refresh
	 */
	public GenericApplicationContext(ApplicationContext parent) {
		this();
		setParent(parent);
	}

	/**
	 * Create a new GenericApplicationContext with the given DefaultListableBeanFactory.
	 * <p>
	 *  使用给定的DefaultListableBeanFactory创建一个新的GenericApplicationContext
	 * 
	 * 
	 * @param beanFactory the DefaultListableBeanFactory instance to use for this context
	 * @param parent the parent application context
	 * @see #registerBeanDefinition
	 * @see #refresh
	 */
	public GenericApplicationContext(DefaultListableBeanFactory beanFactory, ApplicationContext parent) {
		this(beanFactory);
		setParent(parent);
	}


	/**
	 * Set the parent of this application context, also setting
	 * the parent of the internal BeanFactory accordingly.
	 * <p>
	 *  设置此应用程序上下文的父级,并相应地设置内部BeanFactory的父级
	 * 
	 * 
	 * @see org.springframework.beans.factory.config.ConfigurableBeanFactory#setParentBeanFactory
	 */
	@Override
	public void setParent(ApplicationContext parent) {
		super.setParent(parent);
		this.beanFactory.setParentBeanFactory(getInternalParentBeanFactory());
	}

	@Override
	public void setId(String id) {
		super.setId(id);
	}

	/**
	 * Set whether it should be allowed to override bean definitions by registering
	 * a different definition with the same name, automatically replacing the former.
	 * If not, an exception will be thrown. Default is "true".
	 * <p>
	 *  设置是否允许通过注册具有相同名称的不同定义来覆盖bean定义,自动替换前者如果不是,将抛出异常默认为"true"
	 * 
	 * 
	 * @see org.springframework.beans.factory.support.DefaultListableBeanFactory#setAllowBeanDefinitionOverriding
	 */
	public void setAllowBeanDefinitionOverriding(boolean allowBeanDefinitionOverriding) {
		this.beanFactory.setAllowBeanDefinitionOverriding(allowBeanDefinitionOverriding);
	}

	/**
	 * Set whether to allow circular references between beans - and automatically
	 * try to resolve them.
	 * <p>Default is "true". Turn this off to throw an exception when encountering
	 * a circular reference, disallowing them completely.
	 * <p>
	 *  设置是否允许bean之间的循环引用 - 并自动尝试解决它们<p>默认值为"true"将其关闭以在遇到循环引用时抛出异常,不允许它们完全
	 * 
	 * 
	 * @see org.springframework.beans.factory.support.DefaultListableBeanFactory#setAllowCircularReferences
	 */
	public void setAllowCircularReferences(boolean allowCircularReferences) {
		this.beanFactory.setAllowCircularReferences(allowCircularReferences);
	}

	/**
	 * Set a ResourceLoader to use for this context. If set, the context will
	 * delegate all {@code getResource} calls to the given ResourceLoader.
	 * If not set, default resource loading will apply.
	 * <p>The main reason to specify a custom ResourceLoader is to resolve
	 * resource paths (without URL prefix) in a specific fashion.
	 * The default behavior is to resolve such paths as class path locations.
	 * To resolve resource paths as file system locations, specify a
	 * FileSystemResourceLoader here.
	 * <p>You can also pass in a full ResourcePatternResolver, which will
	 * be autodetected by the context and used for {@code getResources}
	 * calls as well. Else, default resource pattern matching will apply.
	 * <p>
	 * 设置一个ResourceLoader以用于此上下文如果设置,上下文将委托所有{@code getResource}调用给给定的ResourceLoader如果未设置,将默认资源加载将应用<p>指定自定义
	 * ResourceLoader的主要原因是解决资源路径(不含URL前缀)的特定方式默认行为是解析像路径位置这样的路径要解析作为文件系统位置的资源路径,请在这里指定一个FileSystemResourceL
	 * oader <p>您还可以传入一个完整的ResourcePatternResolver由上下文自动检测并用于{@code getResources}调用,否则,将应用默认资源模式匹配。
	 * 
	 * 
	 * @see #getResource
	 * @see org.springframework.core.io.DefaultResourceLoader
	 * @see org.springframework.core.io.FileSystemResourceLoader
	 * @see org.springframework.core.io.support.ResourcePatternResolver
	 * @see #getResources
	 */
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}


	//---------------------------------------------------------------------
	// ResourceLoader / ResourcePatternResolver override if necessary
	//---------------------------------------------------------------------

	/**
	 * This implementation delegates to this context's ResourceLoader if set,
	 * falling back to the default superclass behavior else.
	 * <p>
	 * 如果设置,此实现将委托给该上下文的ResourceLoader,否则返回到默认的超类别行为
	 * 
	 * 
	 * @see #setResourceLoader
	 */
	@Override
	public Resource getResource(String location) {
		if (this.resourceLoader != null) {
			return this.resourceLoader.getResource(location);
		}
		return super.getResource(location);
	}

	/**
	 * This implementation delegates to this context's ResourceLoader if it
	 * implements the ResourcePatternResolver interface, falling back to the
	 * default superclass behavior else.
	 * <p>
	 *  如果实现了ResourcePatternResolver接口,则该实现将委托给该上下文的ResourceLoader,否则返回到默认的超类别行为
	 * 
	 * 
	 * @see #setResourceLoader
	 */
	@Override
	public Resource[] getResources(String locationPattern) throws IOException {
		if (this.resourceLoader instanceof ResourcePatternResolver) {
			return ((ResourcePatternResolver) this.resourceLoader).getResources(locationPattern);
		}
		return super.getResources(locationPattern);
	}

	@Override
	public void setClassLoader(ClassLoader classLoader) {
		super.setClassLoader(classLoader);
		this.customClassLoader = true;
	}

	@Override
	public ClassLoader getClassLoader() {
		if (this.resourceLoader != null && !this.customClassLoader) {
			return this.resourceLoader.getClassLoader();
		}
		return super.getClassLoader();
	}


	//---------------------------------------------------------------------
	// Implementations of AbstractApplicationContext's template methods
	//---------------------------------------------------------------------

	/**
	 * Do nothing: We hold a single internal BeanFactory and rely on callers
	 * to register beans through our public methods (or the BeanFactory's).
	 * <p>
	 *  不做任何事情：我们持有一个单一的内部BeanFactory,并依靠调用者通过我们的公共方法(或BeanFactory的)来注册bean
	 * 
	 * 
	 * @see #registerBeanDefinition
	 */
	@Override
	protected final void refreshBeanFactory() throws IllegalStateException {
		if (!this.refreshed.compareAndSet(false, true)) {
			throw new IllegalStateException(
					"GenericApplicationContext does not support multiple refresh attempts: just call 'refresh' once");
		}
		this.beanFactory.setSerializationId(getId());
	}

	@Override
	protected void cancelRefresh(BeansException ex) {
		this.beanFactory.setSerializationId(null);
		super.cancelRefresh(ex);
	}

	/**
	 * Not much to do: We hold a single internal BeanFactory that will never
	 * get released.
	 * <p>
	 *  没有太多的工作：我们持有一个内部BeanFactory,永远不会被释放
	 * 
	 */
	@Override
	protected final void closeBeanFactory() {
		this.beanFactory.setSerializationId(null);
	}

	/**
	 * Return the single internal BeanFactory held by this context
	 * (as ConfigurableListableBeanFactory).
	 * <p>
	 *  返回此上下文所持有的单个内部BeanFactory(作为ConfigurableListableBeanFactory)
	 * 
	 */
	@Override
	public final ConfigurableListableBeanFactory getBeanFactory() {
		return this.beanFactory;
	}

	/**
	 * Return the underlying bean factory of this context,
	 * available for registering bean definitions.
	 * <p><b>NOTE:</b> You need to call {@link #refresh()} to initialize the
	 * bean factory and its contained beans with application context semantics
	 * (autodetecting BeanFactoryPostProcessors, etc).
	 * <p>
	 * 返回此上下文的底层bean工厂,可用于注册bean定义<p> <b>注意：</b>您需要调用{@link #refresh()}来初始化bean工厂及其包含的应用程序上下文的bean语义(自动检测Bea
	 * nFactoryPostProcessors等)。
	 * 
	 * @return the internal bean factory (as DefaultListableBeanFactory)
	 */
	public final DefaultListableBeanFactory getDefaultListableBeanFactory() {
		return this.beanFactory;
	}

	@Override
	public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
		assertBeanFactoryActive();
		return this.beanFactory;
	}


	//---------------------------------------------------------------------
	// Implementation of BeanDefinitionRegistry
	//---------------------------------------------------------------------

	@Override
	public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition)
			throws BeanDefinitionStoreException {

		this.beanFactory.registerBeanDefinition(beanName, beanDefinition);
	}

	@Override
	public void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
		this.beanFactory.removeBeanDefinition(beanName);
	}

	@Override
	public BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
		return this.beanFactory.getBeanDefinition(beanName);
	}

	@Override
	public boolean isBeanNameInUse(String beanName) {
		return this.beanFactory.isBeanNameInUse(beanName);
	}

	@Override
	public void registerAlias(String beanName, String alias) {
		this.beanFactory.registerAlias(beanName, alias);
	}

	@Override
	public void removeAlias(String alias) {
		this.beanFactory.removeAlias(alias);
	}

	@Override
	public boolean isAlias(String beanName) {
		return this.beanFactory.isAlias(beanName);
	}

}
