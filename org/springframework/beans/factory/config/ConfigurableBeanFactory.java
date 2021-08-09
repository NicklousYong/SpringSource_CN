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

import java.beans.PropertyEditor;
import java.security.AccessControlContext;

import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.StringValueResolver;

/**
 * Configuration interface to be implemented by most bean factories. Provides
 * facilities to configure a bean factory, in addition to the bean factory
 * client methods in the {@link org.springframework.beans.factory.BeanFactory}
 * interface.
 *
 * <p>This bean factory interface is not meant to be used in normal application
 * code: Stick to {@link org.springframework.beans.factory.BeanFactory} or
 * {@link org.springframework.beans.factory.ListableBeanFactory} for typical
 * needs. This extended interface is just meant to allow for framework-internal
 * plug'n'play and for special access to bean factory configuration methods.
 *
 * <p>
 * 大多数bean工厂实现的配置界面除了在{@link orgspringframeworkbeansfactoryBeanFactory}界面中的bean工厂客户端方法之外,还提供配置bean工厂的功能。
 * 
 *  <p>这个bean工厂接口并不意味着在正常的应用程序代码中使用：针对典型需求,坚持使用{@link orgspringframeworkbeansfactoryBeanFactory}或{@link orgspringframeworkbeansfactoryListableBeanFactory}
 * 这个扩展接口只是为了允许框架内部插件'播放和特殊访问bean factory配置方法。
 * 
 * 
 * @author Juergen Hoeller
 * @since 03.11.2003
 * @see org.springframework.beans.factory.BeanFactory
 * @see org.springframework.beans.factory.ListableBeanFactory
 * @see ConfigurableListableBeanFactory
 */
public interface ConfigurableBeanFactory extends HierarchicalBeanFactory, SingletonBeanRegistry {

	/**
	 * Scope identifier for the standard singleton scope: "singleton".
	 * Custom scopes can be added via {@code registerScope}.
	 * <p>
	 *  标准单例范围的范围标识符："singleton"可以通过{@code registerScope}添加自定义范围
	 * 
	 * 
	 * @see #registerScope
	 */
	String SCOPE_SINGLETON = "singleton";

	/**
	 * Scope identifier for the standard prototype scope: "prototype".
	 * Custom scopes can be added via {@code registerScope}.
	 * <p>
	 * 标准原型范围的范围标识符："原型"可以通过{@code registerScope}添加自定义范围
	 * 
	 * 
	 * @see #registerScope
	 */
	String SCOPE_PROTOTYPE = "prototype";


	/**
	 * Set the parent of this bean factory.
	 * <p>Note that the parent cannot be changed: It should only be set outside
	 * a constructor if it isn't available at the time of factory instantiation.
	 * <p>
	 *  设置此bean工厂的父级<p>请注意,父级不能更改：只有在构造函数外部,如果在出厂实例化时不可用
	 * 
	 * 
	 * @param parentBeanFactory the parent BeanFactory
	 * @throws IllegalStateException if this factory is already associated with
	 * a parent BeanFactory
	 * @see #getParentBeanFactory()
	 */
	void setParentBeanFactory(BeanFactory parentBeanFactory) throws IllegalStateException;

	/**
	 * Set the class loader to use for loading bean classes.
	 * Default is the thread context class loader.
	 * <p>Note that this class loader will only apply to bean definitions
	 * that do not carry a resolved bean class yet. This is the case as of
	 * Spring 2.0 by default: Bean definitions only carry bean class names,
	 * to be resolved once the factory processes the bean definition.
	 * <p>
	 *  设置用于加载bean类的类加载器默认是线程上下文类加载器<p>请注意,此类加载器将仅适用于不携带已解析的bean类的bean定义。
	 * 默认情况下为Spring 20 ：Bean定义只能携带bean类名,一旦工厂处理bean定义,就要解决。
	 * 
	 * 
	 * @param beanClassLoader the class loader to use,
	 * or {@code null} to suggest the default class loader
	 */
	void setBeanClassLoader(ClassLoader beanClassLoader);

	/**
	 * Return this factory's class loader for loading bean classes.
	 * <p>
	 *  返回此工厂的类加载器以加载bean类
	 * 
	 */
	ClassLoader getBeanClassLoader();

	/**
	 * Specify a temporary ClassLoader to use for type matching purposes.
	 * Default is none, simply using the standard bean ClassLoader.
	 * <p>A temporary ClassLoader is usually just specified if
	 * <i>load-time weaving</i> is involved, to make sure that actual bean
	 * classes are loaded as lazily as possible. The temporary loader is
	 * then removed once the BeanFactory completes its bootstrap phase.
	 * <p>
	 * 指定用于类型匹配目的的临时ClassLoader默认值为none,只需使用标准bean ClassLoader <p>如果涉及加载时编织</i>,通常只指定临时ClassLoader,以确保实际bean
	 * 类尽可能地懒惰地加载。
	 * 一旦BeanFactory完成其引导阶段,临时加载器就被删除。
	 * 
	 * 
	 * @since 2.5
	 */
	void setTempClassLoader(ClassLoader tempClassLoader);

	/**
	 * Return the temporary ClassLoader to use for type matching purposes,
	 * if any.
	 * <p>
	 *  返回临时ClassLoader以用于类型匹配目的(如果有)
	 * 
	 * 
	 * @since 2.5
	 */
	ClassLoader getTempClassLoader();

	/**
	 * Set whether to cache bean metadata such as given bean definitions
	 * (in merged fashion) and resolved bean classes. Default is on.
	 * <p>Turn this flag off to enable hot-refreshing of bean definition objects
	 * and in particular bean classes. If this flag is off, any creation of a bean
	 * instance will re-query the bean class loader for newly resolved classes.
	 * <p>
	 * 设置是否缓存bean元数据,例如给定的bean定义(以合并方式)和解析的bean类默认值在<p>关闭此标志以启用bean定义对象,特别是Bean类的热刷新如果此标志为关闭,任何bean实例的创建将重新查
	 * 询bean类加载器以获得新解决的类。
	 * 
	 */
	void setCacheBeanMetadata(boolean cacheBeanMetadata);

	/**
	 * Return whether to cache bean metadata such as given bean definitions
	 * (in merged fashion) and resolved bean classes.
	 * <p>
	 *  返回是否缓存bean元数据,例如给定的bean定义(以合并方式)和已解析的bean类
	 * 
	 */
	boolean isCacheBeanMetadata();

	/**
	 * Specify the resolution strategy for expressions in bean definition values.
	 * <p>There is no expression support active in a BeanFactory by default.
	 * An ApplicationContext will typically set a standard expression strategy
	 * here, supporting "#{...}" expressions in a Unified EL compatible style.
	 * <p>
	 *  指定bean定义值中表达式的分辨率策略<p>默认情况下,BeanFactory中没有表达式支持活动。
	 * ApplicationContext通常会在此设置标准表达式策略,在Unified EL兼容样式中支持"#{}"表达式。
	 * 
	 * 
	 * @since 3.0
	 */
	void setBeanExpressionResolver(BeanExpressionResolver resolver);

	/**
	 * Return the resolution strategy for expressions in bean definition values.
	 * <p>
	 * 返回bean定义值中表达式的分辨率策略
	 * 
	 * 
	 * @since 3.0
	 */
	BeanExpressionResolver getBeanExpressionResolver();

	/**
	 * Specify a Spring 3.0 ConversionService to use for converting
	 * property values, as an alternative to JavaBeans PropertyEditors.
	 * <p>
	 *  指定用于转换属性值的Spring 30 ConversionService,作为JavaBeans PropertyEditor的替代方法
	 * 
	 * 
	 * @since 3.0
	 */
	void setConversionService(ConversionService conversionService);

	/**
	 * Return the associated ConversionService, if any.
	 * <p>
	 *  返回相关的ConversionService(如果有)
	 * 
	 * 
	 * @since 3.0
	 */
	ConversionService getConversionService();

	/**
	 * Add a PropertyEditorRegistrar to be applied to all bean creation processes.
	 * <p>Such a registrar creates new PropertyEditor instances and registers them
	 * on the given registry, fresh for each bean creation attempt. This avoids
	 * the need for synchronization on custom editors; hence, it is generally
	 * preferable to use this method instead of {@link #registerCustomEditor}.
	 * <p>
	 *  添加一个适用于所有bean创建过程的PropertyEditorRegistrar <p>这样的注册器创建新的PropertyEditor实例,并在给定的注册表中注册它们,每个bean创建尝试都是新鲜
	 * 的。
	 * 这避免了在自定义编辑器上同步的需要;因此,通常最好使用这种方法代替{@link #registerCustomEditor}。
	 * 
	 * 
	 * @param registrar the PropertyEditorRegistrar to register
	 */
	void addPropertyEditorRegistrar(PropertyEditorRegistrar registrar);

	/**
	 * Register the given custom property editor for all properties of the
	 * given type. To be invoked during factory configuration.
	 * <p>Note that this method will register a shared custom editor instance;
	 * access to that instance will be synchronized for thread-safety. It is
	 * generally preferable to use {@link #addPropertyEditorRegistrar} instead
	 * of this method, to avoid for the need for synchronization on custom editors.
	 * <p>
	 * 为给定类型的所有属性注册给定的自定义属性编辑器在工厂配置期间调用<p>请注意,此方法将注册共享自定义编辑器实例;对该实例的访问将被线程安全同步。
	 * 通常最好使用{@link #addPropertyEditorRegistrar}而不是此方法,以避免需要在自定义编辑器上进行同步。
	 * 
	 * 
	 * @param requiredType type of the property
	 * @param propertyEditorClass the {@link PropertyEditor} class to register
	 */
	void registerCustomEditor(Class<?> requiredType, Class<? extends PropertyEditor> propertyEditorClass);

	/**
	 * Initialize the given PropertyEditorRegistry with the custom editors
	 * that have been registered with this BeanFactory.
	 * <p>
	 *  使用已在此BeanFactory注册的自定义编辑器初始化给定的PropertyEditorRegistry
	 * 
	 * 
	 * @param registry the PropertyEditorRegistry to initialize
	 */
	void copyRegisteredEditorsTo(PropertyEditorRegistry registry);

	/**
	 * Set a custom type converter that this BeanFactory should use for converting
	 * bean property values, constructor argument values, etc.
	 * <p>This will override the default PropertyEditor mechanism and hence make
	 * any custom editors or custom editor registrars irrelevant.
	 * <p>
	 *  设置一个自定义类型转换器,该BeanFactory应该用于转换bean属性值,构造函数参数值等。<p>这将覆盖默认的PropertyEditor机制,因此使任何自定义编辑器或自定义编辑器注册器无关
	 * 
	 * 
	 * @see #addPropertyEditorRegistrar
	 * @see #registerCustomEditor
	 * @since 2.5
	 */
	void setTypeConverter(TypeConverter typeConverter);

	/**
	 * Obtain a type converter as used by this BeanFactory. This may be a fresh
	 * instance for each call, since TypeConverters are usually <i>not</i> thread-safe.
	 * <p>If the default PropertyEditor mechanism is active, the returned
	 * TypeConverter will be aware of all custom editors that have been registered.
	 * <p>
	 * 获取此BeanFactory使用的类型转换器这可能是每个调用的新实例,因为TypeConverters通常不是</i>线程安全<p>如果默认的PropertyEditor机制处于活动状态,则返回的Typ
	 * eConverter将为了解所有已注册的自定义编辑器。
	 * 
	 * 
	 * @since 2.5
	 */
	TypeConverter getTypeConverter();

	/**
	 * Add a String resolver for embedded values such as annotation attributes.
	 * <p>
	 *  为嵌入式值(如注释属性)添加String解析器
	 * 
	 * 
	 * @param valueResolver the String resolver to apply to embedded values
	 * @since 3.0
	 */
	void addEmbeddedValueResolver(StringValueResolver valueResolver);

	/**
	 * Determine whether an embedded value resolver has been registered with this
	 * bean factory, to be applied through {@link #resolveEmbeddedValue(String)}.
	 * <p>
	 *  确定嵌入式值解析器是否已经在此bean工厂注册,以通过{@link #resolveEmbeddedValue(String)}应用
	 * 
	 * 
	 * @since 4.3
	 */
	boolean hasEmbeddedValueResolver();

	/**
	 * Resolve the given embedded value, e.g. an annotation attribute.
	 * <p>
	 *  解决给定的嵌入值,例如注释属性
	 * 
	 * 
	 * @param value the value to resolve
	 * @return the resolved value (may be the original value as-is)
	 * @since 3.0
	 */
	String resolveEmbeddedValue(String value);

	/**
	 * Add a new BeanPostProcessor that will get applied to beans created
	 * by this factory. To be invoked during factory configuration.
	 * <p>Note: Post-processors submitted here will be applied in the order of
	 * registration; any ordering semantics expressed through implementing the
	 * {@link org.springframework.core.Ordered} interface will be ignored. Note
	 * that autodetected post-processors (e.g. as beans in an ApplicationContext)
	 * will always be applied after programmatically registered ones.
	 * <p>
	 * 添加一个新的BeanPostProcessor应用于此工厂创建的bean在工厂配置期间调用<p>注意：此处提交的后处理器将按照注册顺序进行应用;通过实现{@link orgspringframeworkcoreOrdered}
	 * 接口表达的任何排序语义将被忽略注意,自动检测的后处理器(例如,作为ApplicationContext中的bean)将始终应用于以编程方式注册的。
	 * 
	 * 
	 * @param beanPostProcessor the post-processor to register
	 */
	void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);

	/**
	 * Return the current number of registered BeanPostProcessors, if any.
	 * <p>
	 *  返回当前注册的BeanPostProcessors数量(如果有的话)
	 * 
	 */
	int getBeanPostProcessorCount();

	/**
	 * Register the given scope, backed by the given Scope implementation.
	 * <p>
	 *  注册给定范围,由给定的Scope实现支持
	 * 
	 * 
	 * @param scopeName the scope identifier
	 * @param scope the backing Scope implementation
	 */
	void registerScope(String scopeName, Scope scope);

	/**
	 * Return the names of all currently registered scopes.
	 * <p>This will only return the names of explicitly registered scopes.
	 * Built-in scopes such as "singleton" and "prototype" won't be exposed.
	 * <p>
	 * 返回所有当前注册的范围的名称<p>这将只返回明确注册的范围的名称内置的范围,如"单身人士"和"原型"将不会被公开
	 * 
	 * 
	 * @return the array of scope names, or an empty array if none
	 * @see #registerScope
	 */
	String[] getRegisteredScopeNames();

	/**
	 * Return the Scope implementation for the given scope name, if any.
	 * <p>This will only return explicitly registered scopes.
	 * Built-in scopes such as "singleton" and "prototype" won't be exposed.
	 * <p>
	 *  返回给定范围名称的范围实现(如果有的话)<p>这将只返回明确注册的范围内置的范围,如"singleton"和"prototype"将不会被公开
	 * 
	 * 
	 * @param scopeName the name of the scope
	 * @return the registered Scope implementation, or {@code null} if none
	 * @see #registerScope
	 */
	Scope getRegisteredScope(String scopeName);

	/**
	 * Provides a security access control context relevant to this factory.
	 * <p>
	 *  提供与该工厂相关的安全访问控制上下文
	 * 
	 * 
	 * @return the applicable AccessControlContext (never {@code null})
	 * @since 3.0
	 */
	AccessControlContext getAccessControlContext();

	/**
	 * Copy all relevant configuration from the given other factory.
	 * <p>Should include all standard configuration settings as well as
	 * BeanPostProcessors, Scopes, and factory-specific internal settings.
	 * Should not include any metadata of actual bean definitions,
	 * such as BeanDefinition objects and bean name aliases.
	 * <p>
	 *  复制所有相关的配置从指定的其他工厂<p>应包括所有标准配置设置以及BeanPostProcessors,Scopes和出厂特定内部设置不应包含实际bean定义的任何元数据,例如BeanDefiniti
	 * on对象和bean名称别名。
	 * 
	 * 
	 * @param otherFactory the other BeanFactory to copy from
	 */
	void copyConfigurationFrom(ConfigurableBeanFactory otherFactory);

	/**
	 * Given a bean name, create an alias. We typically use this method to
	 * support names that are illegal within XML ids (used for bean names).
	 * <p>Typically invoked during factory configuration, but can also be
	 * used for runtime registration of aliases. Therefore, a factory
	 * implementation should synchronize alias access.
	 * <p>
	 * 给定一个bean名称,创建别名我们通常使用此方法来支持在XML ids中非法的名称(用于bean名称)<p>通常在出厂配置期间调用,但也可用于别名的运行时注册因此,工厂实现应该同步别名访问
	 * 
	 * 
	 * @param beanName the canonical name of the target bean
	 * @param alias the alias to be registered for the bean
	 * @throws BeanDefinitionStoreException if the alias is already in use
	 */
	void registerAlias(String beanName, String alias) throws BeanDefinitionStoreException;

	/**
	 * Resolve all alias target names and aliases registered in this
	 * factory, applying the given StringValueResolver to them.
	 * <p>The value resolver may for example resolve placeholders
	 * in target bean names and even in alias names.
	 * <p>
	 *  解决在此工厂中注册的所有别名目标名称和别名,将给定的StringValueResolver应用于他们<p>值解析器可能例如解析目标bean名称中的占位符,甚至在别名中解析占位符
	 * 
	 * 
	 * @param valueResolver the StringValueResolver to apply
	 * @since 2.5
	 */
	void resolveAliases(StringValueResolver valueResolver);

	/**
	 * Return a merged BeanDefinition for the given bean name,
	 * merging a child bean definition with its parent if necessary.
	 * Considers bean definitions in ancestor factories as well.
	 * <p>
	 *  返回给定bean名称的合并BeanDefinition,如果需要,则将子bean定义与其父代合并,以及祖先工厂中的bean定义
	 * 
	 * 
	 * @param beanName the name of the bean to retrieve the merged definition for
	 * @return a (potentially merged) BeanDefinition for the given bean
	 * @throws NoSuchBeanDefinitionException if there is no bean definition with the given name
	 * @since 2.5
	 */
	BeanDefinition getMergedBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

	/**
	 * Determine whether the bean with the given name is a FactoryBean.
	 * <p>
	 *  确定给定名称的bean是否为FactoryBean
	 * 
	 * 
	 * @param name the name of the bean to check
	 * @return whether the bean is a FactoryBean
	 * ({@code false} means the bean exists but is not a FactoryBean)
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 2.5
	 */
	boolean isFactoryBean(String name) throws NoSuchBeanDefinitionException;

	/**
	 * Explicitly control the current in-creation status of the specified bean.
	 * For container-internal use only.
	 * <p>
	 * 明确控制指定bean的当前创建状态仅供容器内部使用
	 * 
	 * 
	 * @param beanName the name of the bean
	 * @param inCreation whether the bean is currently in creation
	 * @since 3.1
	 */
	void setCurrentlyInCreation(String beanName, boolean inCreation);

	/**
	 * Determine whether the specified bean is currently in creation.
	 * <p>
	 *  确定指定的bean当前是否正在创建
	 * 
	 * 
	 * @param beanName the name of the bean
	 * @return whether the bean is currently in creation
	 * @since 2.5
	 */
	boolean isCurrentlyInCreation(String beanName);

	/**
	 * Register a dependent bean for the given bean,
	 * to be destroyed before the given bean is destroyed.
	 * <p>
	 *  注册给定bean的依赖bean,在给定的bean被销毁之前被销毁
	 * 
	 * 
	 * @param beanName the name of the bean
	 * @param dependentBeanName the name of the dependent bean
	 * @since 2.5
	 */
	void registerDependentBean(String beanName, String dependentBeanName);

	/**
	 * Return the names of all beans which depend on the specified bean, if any.
	 * <p>
	 *  返回依赖于指定bean的所有bean的名称(如果有)
	 * 
	 * 
	 * @param beanName the name of the bean
	 * @return the array of dependent bean names, or an empty array if none
	 * @since 2.5
	 */
	String[] getDependentBeans(String beanName);

	/**
	 * Return the names of all beans that the specified bean depends on, if any.
	 * <p>
	 *  返回指定的bean依赖的所有bean的名称(如果有的话)
	 * 
	 * 
	 * @param beanName the name of the bean
	 * @return the array of names of beans which the bean depends on,
	 * or an empty array if none
	 * @since 2.5
	 */
	String[] getDependenciesForBean(String beanName);

	/**
	 * Destroy the given bean instance (usually a prototype instance
	 * obtained from this factory) according to its bean definition.
	 * <p>Any exception that arises during destruction should be caught
	 * and logged instead of propagated to the caller of this method.
	 * <p>
	 *  销毁给定的bean实例(通常是从这个工厂获得的原型实例)根据其bean定义<p>在销毁期间产生的任何异常都应该被捕获并记录,而不是传播到此方法的调用者
	 * 
	 * 
	 * @param beanName the name of the bean definition
	 * @param beanInstance the bean instance to destroy
	 */
	void destroyBean(String beanName, Object beanInstance);

	/**
	 * Destroy the specified scoped bean in the current target scope, if any.
	 * <p>Any exception that arises during destruction should be caught
	 * and logged instead of propagated to the caller of this method.
	 * <p>
	 * 销毁当前目标作用域中的指定作用域bean,如果有的话,那么在销毁过程中出现的任何异常都应该被捕获和记录,而不是传播给这个方法的调用者
	 * 
	 * 
	 * @param beanName the name of the scoped bean
	 */
	void destroyScopedBean(String beanName);

	/**
	 * Destroy all singleton beans in this factory, including inner beans that have
	 * been registered as disposable. To be called on shutdown of a factory.
	 * <p>Any exception that arises during destruction should be caught
	 * and logged instead of propagated to the caller of this method.
	 * <p>
	 *  销毁这个工厂中的所有单例bean,包括已经注册为一次性的内部bean在关闭工厂时被调用<p>在销毁期间出现的任何异常都应该被捕获并记录,而不是传播到此方法的调用者
	 */
	void destroySingletons();

}
