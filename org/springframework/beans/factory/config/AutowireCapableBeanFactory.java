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

import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;

/**
 * Extension of the {@link org.springframework.beans.factory.BeanFactory}
 * interface to be implemented by bean factories that are capable of
 * autowiring, provided that they want to expose this functionality for
 * existing bean instances.
 *
 * <p>This subinterface of BeanFactory is not meant to be used in normal
 * application code: stick to {@link org.springframework.beans.factory.BeanFactory}
 * or {@link org.springframework.beans.factory.ListableBeanFactory} for
 * typical use cases.
 *
 * <p>Integration code for other frameworks can leverage this interface to
 * wire and populate existing bean instances that Spring does not control
 * the lifecycle of. This is particularly useful for WebWork Actions and
 * Tapestry Page objects, for example.
 *
 * <p>Note that this interface is not implemented by
 * {@link org.springframework.context.ApplicationContext} facades,
 * as it is hardly ever used by application code. That said, it is available
 * from an application context too, accessible through ApplicationContext's
 * {@link org.springframework.context.ApplicationContext#getAutowireCapableBeanFactory()}
 * method.
 *
 * <p>You may also implement the {@link org.springframework.beans.factory.BeanFactoryAware}
 * interface, which exposes the internal BeanFactory even when running in an
 * ApplicationContext, to get access to an AutowireCapableBeanFactory:
 * simply cast the passed-in BeanFactory to AutowireCapableBeanFactory.
 *
 * <p>
 * 扩展要由能够自动连线的bean工厂实现的{@link orgspringframeworkbeansfactoryBeanFactory}接口,前提是它们要为现有的bean实例公开此功能
 * 
 *  <p> BeanFactory的这个子界面并不意味着在正常的应用程序代码中使用：对于典型的用例,坚持使用{@link orgspringframeworkbeansfactoryBeanFactory}
 * 或{@link orgspringframeworkbeansfactoryListableBeanFactory}。
 * 
 *  其他框架的集成代码可以利用此接口来连接和填充Spring不能控制生命周期的现有bean实例。这对于WebWork操作和Tapestry Page对象尤其有用,例如
 * 
 * <p>请注意,这个接口不是由{@link orgspringframeworkcontextApplicationContext}外观实现的,因为它几乎不被应用程序代码使用。
 * 这表示,它也可以从应用程序上下文获得,可以通过ApplicationContext的{@link orgspringframeworkcontextApplicationContext#getAutowireCapableBeanFactory() }
 *  方法。
 * <p>请注意,这个接口不是由{@link orgspringframeworkcontextApplicationContext}外观实现的,因为它几乎不被应用程序代码使用。
 * 
 *  <p>您还可以实现{@link orgspringframeworkbeansfactoryBeanFactoryAware}接口,即使在ApplicationContext中运行时也暴露内部Bean
 * Factory,以访问AutowireCapableBeanFactory：只需将传入的BeanFactory转换为AutowireCapableBeanFactory。
 * 
 * 
 * @author Juergen Hoeller
 * @since 04.12.2003
 * @see org.springframework.beans.factory.BeanFactoryAware
 * @see org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 * @see org.springframework.context.ApplicationContext#getAutowireCapableBeanFactory()
 */
public interface AutowireCapableBeanFactory extends BeanFactory {

	/**
	 * Constant that indicates no externally defined autowiring. Note that
	 * BeanFactoryAware etc and annotation-driven injection will still be applied.
	 * <p>
	 * 指示没有外部定义的自动接线的常量注意,BeanFactoryAware等和注释驱动的注入将仍然被应用
	 * 
	 * 
	 * @see #createBean
	 * @see #autowire
	 * @see #autowireBeanProperties
	 */
	int AUTOWIRE_NO = 0;

	/**
	 * Constant that indicates autowiring bean properties by name
	 * (applying to all bean property setters).
	 * <p>
	 *  常数表示按名称自动连接bean属性(适用于所有bean属性设置器)
	 * 
	 * 
	 * @see #createBean
	 * @see #autowire
	 * @see #autowireBeanProperties
	 */
	int AUTOWIRE_BY_NAME = 1;

	/**
	 * Constant that indicates autowiring bean properties by type
	 * (applying to all bean property setters).
	 * <p>
	 *  常量,表示按类型自动连线bean属性(适用于所有bean属性设置器)
	 * 
	 * 
	 * @see #createBean
	 * @see #autowire
	 * @see #autowireBeanProperties
	 */
	int AUTOWIRE_BY_TYPE = 2;

	/**
	 * Constant that indicates autowiring the greediest constructor that
	 * can be satisfied (involves resolving the appropriate constructor).
	 * <p>
	 *  常数表示自动连接可以满足的最贪婪的构造函数(涉及解析适当的构造函数)
	 * 
	 * 
	 * @see #createBean
	 * @see #autowire
	 */
	int AUTOWIRE_CONSTRUCTOR = 3;

	/**
	 * Constant that indicates determining an appropriate autowire strategy
	 * through introspection of the bean class.
	 * <p>
	 *  常数,表示通过内省bean类确定适当的自动线路策略
	 * 
	 * 
	 * @see #createBean
	 * @see #autowire
	 * @deprecated as of Spring 3.0: If you are using mixed autowiring strategies,
	 * prefer annotation-based autowiring for clearer demarcation of autowiring needs.
	 */
	@Deprecated
	int AUTOWIRE_AUTODETECT = 4;


	//-------------------------------------------------------------------------
	// Typical methods for creating and populating external bean instances
	//-------------------------------------------------------------------------

	/**
	 * Fully create a new bean instance of the given class.
	 * <p>Performs full initialization of the bean, including all applicable
	 * {@link BeanPostProcessor BeanPostProcessors}.
	 * <p>Note: This is intended for creating a fresh instance, populating annotated
	 * fields and methods as well as applying all standard bean initialization callbacks.
	 * It does <i>not</> imply traditional by-name or by-type autowiring of properties;
	 * use {@link #createBean(Class, int, boolean)} for those purposes.
	 * <p>
	 * 完全创建给定类的新bean实例<p>执行bean的完全初始化,包括所有适用的{@link BeanPostProcessor BeanPostProcessors} <p>注意：这是为了创建一个新的实例
	 * ,填充注释的字段和方法作为应用所有标准bean初始化回调它<i>不</>暗示传统的按名称或按类型自动布线的属性;为了这些目的使用{@link #createBean(Class,int,boolean)}
	 * 。
	 * 
	 * 
	 * @param beanClass the class of the bean to create
	 * @return the new bean instance
	 * @throws BeansException if instantiation or wiring failed
	 */
	<T> T createBean(Class<T> beanClass) throws BeansException;

	/**
	 * Populate the given bean instance through applying after-instantiation callbacks
	 * and bean property post-processing (e.g. for annotation-driven injection).
	 * <p>Note: This is essentially intended for (re-)populating annotated fields and
	 * methods, either for new instances or for deserialized instances. It does
	 * <i>not</i> imply traditional by-name or by-type autowiring of properties;
	 * use {@link #autowireBeanProperties} for those purposes.
	 * <p>
	 * 通过应用后实例化回调和bean属性后处理(例如注释驱动的注入)来填充给定的bean实例注意：这主要是为了(重新)填充注释的字段和方法,无论是对于新的实例还是对于反序列化实例,它不是</i>意味着传统的按
	 * 名称或按类型自动装配属性;为此而使用{@link #autowireBeanProperties}。
	 * 
	 * 
	 * @param existingBean the existing bean instance
	 * @throws BeansException if wiring failed
	 */
	void autowireBean(Object existingBean) throws BeansException;

	/**
	 * Configure the given raw bean: autowiring bean properties, applying
	 * bean property values, applying factory callbacks such as {@code setBeanName}
	 * and {@code setBeanFactory}, and also applying all bean post processors
	 * (including ones which might wrap the given raw bean).
	 * <p>This is effectively a superset of what {@link #initializeBean} provides,
	 * fully applying the configuration specified by the corresponding bean definition.
	 * <b>Note: This method requires a bean definition for the given name!</b>
	 * <p>
	 * 配置给定的raw bean：自动连接bean属性,应用bean属性值,应用工厂回调(如{@code setBeanName}和{@code setBeanFactory}),还应用所有bean后处理器(
	 * 包括可能包装给定的原始bean) <p>这实际上是{@link #initializeBean}提供的超集,完全应用由相应的bean定义指定的配置<b>注意：此方法需要给定名称的bean定义！</b>。
	 * 
	 * 
	 * @param existingBean the existing bean instance
	 * @param beanName the name of the bean, to be passed to it if necessary
	 * (a bean definition of that name has to be available)
	 * @return the bean instance to use, either the original or a wrapped one
	 * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException
	 * if there is no bean definition with the given name
	 * @throws BeansException if the initialization failed
	 * @see #initializeBean
	 */
	Object configureBean(Object existingBean, String beanName) throws BeansException;


	//-------------------------------------------------------------------------
	// Specialized methods for fine-grained control over the bean lifecycle
	//-------------------------------------------------------------------------

	/**
	 * Fully create a new bean instance of the given class with the specified
	 * autowire strategy. All constants defined in this interface are supported here.
	 * <p>Performs full initialization of the bean, including all applicable
	 * {@link BeanPostProcessor BeanPostProcessors}. This is effectively a superset
	 * of what {@link #autowire} provides, adding {@link #initializeBean} behavior.
	 * <p>
	 * 使用指定的自动线路策略完全创建给定类的新Bean实例此处支持在此接口中定义的所有常量。
	 * <p>执行完全初始化的bean,包括所有适用的{@link BeanPostProcessor BeanPostProcessors}这实际上是一个超集{@link #autowire}提供添加{@link #initializeBean}
	 * 行为。
	 * 使用指定的自动线路策略完全创建给定类的新Bean实例此处支持在此接口中定义的所有常量。
	 * 
	 * 
	 * @param beanClass the class of the bean to create
	 * @param autowireMode by name or type, using the constants in this interface
	 * @param dependencyCheck whether to perform a dependency check for objects
	 * (not applicable to autowiring a constructor, thus ignored there)
	 * @return the new bean instance
	 * @throws BeansException if instantiation or wiring failed
	 * @see #AUTOWIRE_NO
	 * @see #AUTOWIRE_BY_NAME
	 * @see #AUTOWIRE_BY_TYPE
	 * @see #AUTOWIRE_CONSTRUCTOR
	 */
	Object createBean(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException;

	/**
	 * Instantiate a new bean instance of the given class with the specified autowire
	 * strategy. All constants defined in this interface are supported here.
	 * Can also be invoked with {@code AUTOWIRE_NO} in order to just apply
	 * before-instantiation callbacks (e.g. for annotation-driven injection).
	 * <p>Does <i>not</i> apply standard {@link BeanPostProcessor BeanPostProcessors}
	 * callbacks or perform any further initialization of the bean. This interface
	 * offers distinct, fine-grained operations for those purposes, for example
	 * {@link #initializeBean}. However, {@link InstantiationAwareBeanPostProcessor}
	 * callbacks are applied, if applicable to the construction of the instance.
	 * <p>
	 * 使用指定的自动线路策略实例化给定类的新bean实例此接口中定义的所有常量都支持此处也可以使用{@code AUTOWIRE_NO}调用,以便仅应用实例化回调(例如注释驱动的注入) <p> <i>不</i>
	 * 应用标准的{@link BeanPostProcessor BeanPostProcessors}回调或对bean进行任何进一步的初始化此接口为这些目的提供了不同的细粒度操作,例如{@link #initializeBean}
	 * 但是,如果适用于实例的构造,则应用{@link InstantiationAwareBeanPostProcessor}回调。
	 * 
	 * 
	 * @param beanClass the class of the bean to instantiate
	 * @param autowireMode by name or type, using the constants in this interface
	 * @param dependencyCheck whether to perform a dependency check for object
	 * references in the bean instance (not applicable to autowiring a constructor,
	 * thus ignored there)
	 * @return the new bean instance
	 * @throws BeansException if instantiation or wiring failed
	 * @see #AUTOWIRE_NO
	 * @see #AUTOWIRE_BY_NAME
	 * @see #AUTOWIRE_BY_TYPE
	 * @see #AUTOWIRE_CONSTRUCTOR
	 * @see #AUTOWIRE_AUTODETECT
	 * @see #initializeBean
	 * @see #applyBeanPostProcessorsBeforeInitialization
	 * @see #applyBeanPostProcessorsAfterInitialization
	 */
	Object autowire(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException;

	/**
	 * Autowire the bean properties of the given bean instance by name or type.
	 * Can also be invoked with {@code AUTOWIRE_NO} in order to just apply
	 * after-instantiation callbacks (e.g. for annotation-driven injection).
	 * <p>Does <i>not</i> apply standard {@link BeanPostProcessor BeanPostProcessors}
	 * callbacks or perform any further initialization of the bean. This interface
	 * offers distinct, fine-grained operations for those purposes, for example
	 * {@link #initializeBean}. However, {@link InstantiationAwareBeanPostProcessor}
	 * callbacks are applied, if applicable to the configuration of the instance.
	 * <p>
	 * 通过名称或类型自动连接给定bean实例的bean属性也可以使用{@code AUTOWIRE_NO}调用,以便仅应用实例化后回调(例如注释驱动的注入)<p> <i>不</我应用标准的{@link BeanPostProcessor BeanPostProcessors}回调或执行bean的任何进一步的初始化这个接口为这些目的提供了不同的,细粒度的操作,例如{@link #initializeBean}但是,{@link InstantiationAwareBeanPostProcessor}回调被应用,如果适用于实例的配置。
	 * 
	 * 
	 * @param existingBean the existing bean instance
	 * @param autowireMode by name or type, using the constants in this interface
	 * @param dependencyCheck whether to perform a dependency check for object
	 * references in the bean instance
	 * @throws BeansException if wiring failed
	 * @see #AUTOWIRE_BY_NAME
	 * @see #AUTOWIRE_BY_TYPE
	 * @see #AUTOWIRE_NO
	 */
	void autowireBeanProperties(Object existingBean, int autowireMode, boolean dependencyCheck)
			throws BeansException;

	/**
	 * Apply the property values of the bean definition with the given name to
	 * the given bean instance. The bean definition can either define a fully
	 * self-contained bean, reusing its property values, or just property values
	 * meant to be used for existing bean instances.
	 * <p>This method does <i>not</i> autowire bean properties; it just applies
	 * explicitly defined property values. Use the {@link #autowireBeanProperties}
	 * method to autowire an existing bean instance.
	 * <b>Note: This method requires a bean definition for the given name!</b>
	 * <p>Does <i>not</i> apply standard {@link BeanPostProcessor BeanPostProcessors}
	 * callbacks or perform any further initialization of the bean. This interface
	 * offers distinct, fine-grained operations for those purposes, for example
	 * {@link #initializeBean}. However, {@link InstantiationAwareBeanPostProcessor}
	 * callbacks are applied, if applicable to the configuration of the instance.
	 * <p>
	 * 将具有给定名称的bean定义的属性值应用于给定的bean实例bean定义可以定义一个完全自包含的bean,重用其属性值,或者仅仅是用于现有bean实例的属性值<p>此方法不</i> autowire b
	 * ean属性;它只是应用明确定义的属性值使用{@link #autowireBeanProperties}方法自动连接现有的bean实例<b>注意：此方法需要给定名称的bean定义！</b> <p> <i>
	 * 不</i>应用标准的{@link BeanPostProcessor BeanPostProcessors}回调或执行bean的任何进一步的初始化这个接口为这些目的提供了不同的,细粒度的操作,例如{@link #initializeBean}
	 * 但是,如果适用于实例的配置,则会应用{@link InstantiationAwareBeanPostProcessor}回调。
	 * 
	 * 
	 * @param existingBean the existing bean instance
	 * @param beanName the name of the bean definition in the bean factory
	 * (a bean definition of that name has to be available)
	 * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException
	 * if there is no bean definition with the given name
	 * @throws BeansException if applying the property values failed
	 * @see #autowireBeanProperties
	 */
	void applyBeanPropertyValues(Object existingBean, String beanName) throws BeansException;

	/**
	 * Initialize the given raw bean, applying factory callbacks
	 * such as {@code setBeanName} and {@code setBeanFactory},
	 * also applying all bean post processors (including ones which
	 * might wrap the given raw bean).
	 * <p>Note that no bean definition of the given name has to exist
	 * in the bean factory. The passed-in bean name will simply be used
	 * for callbacks but not checked against the registered bean definitions.
	 * <p>
	 * 初始化给定的原始bean,应用诸如{@code setBeanName}和{@code setBeanFactory}之类的工厂回调,还应用所有bean后处理器(包括可能包装给定的原始bean)<p>注
	 * 意,没有bean定义给定的名称必须存在于bean工厂中传入的bean名称将简单地用于回调,但不会对已注册的bean定义进行检查。
	 * 
	 * 
	 * @param existingBean the existing bean instance
	 * @param beanName the name of the bean, to be passed to it if necessary
	 * (only passed to {@link BeanPostProcessor BeanPostProcessors})
	 * @return the bean instance to use, either the original or a wrapped one
	 * @throws BeansException if the initialization failed
	 */
	Object initializeBean(Object existingBean, String beanName) throws BeansException;

	/**
	 * Apply {@link BeanPostProcessor BeanPostProcessors} to the given existing bean
	 * instance, invoking their {@code postProcessBeforeInitialization} methods.
	 * The returned bean instance may be a wrapper around the original.
	 * <p>
	 *  将{@link BeanPostProcessor BeanPostProcessors}应用于给定的现有bean实例,调用其{@code postProcessBeforeInitialization}
	 * 方法返回的bean实例可能是原始的。
	 * 
	 * 
	 * @param existingBean the new bean instance
	 * @param beanName the name of the bean
	 * @return the bean instance to use, either the original or a wrapped one
	 * @throws BeansException if any post-processing failed
	 * @see BeanPostProcessor#postProcessBeforeInitialization
	 */
	Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
			throws BeansException;

	/**
	 * Apply {@link BeanPostProcessor BeanPostProcessors} to the given existing bean
	 * instance, invoking their {@code postProcessAfterInitialization} methods.
	 * The returned bean instance may be a wrapper around the original.
	 * <p>
	 * 将{@link BeanPostProcessor BeanPostProcessors}应用于给定的现有bean实例,调用其{@code postProcessAfterInitialization}
	 * 方法返回的bean实例可能是原始的。
	 * 
	 * 
	 * @param existingBean the new bean instance
	 * @param beanName the name of the bean
	 * @return the bean instance to use, either the original or a wrapped one
	 * @throws BeansException if any post-processing failed
	 * @see BeanPostProcessor#postProcessAfterInitialization
	 */
	Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
			throws BeansException;

	/**
	 * Destroy the given bean instance (typically coming from {@link #createBean}),
	 * applying the {@link org.springframework.beans.factory.DisposableBean} contract as well as
	 * registered {@link DestructionAwareBeanPostProcessor DestructionAwareBeanPostProcessors}.
	 * <p>Any exception that arises during destruction should be caught
	 * and logged instead of propagated to the caller of this method.
	 * <p>
	 *  销毁给定的bean实例(通常来自{@link #createBean}),应用{@link orgspringframeworkbeansfactoryDisposableBean}合同以及注册的{@link DestructionAwareBeanPostProcessor DestructionAwareBeanPostProcessors}
	 *  <p>在销毁过程中出现的任何异常都应该被捕获并记录下来传播给这个方法的调用者。
	 * 
	 * 
	 * @param existingBean the bean instance to destroy
	 */
	void destroyBean(Object existingBean);


	//-------------------------------------------------------------------------
	// Delegate methods for resolving injection points
	//-------------------------------------------------------------------------

	/**
	 * Resolve the bean instance that uniquely matches the given object type, if any,
	 * including its bean name.
	 * <p>This is effectively a variant of {@link #getBean(Class)} which preserves the
	 * bean name of the matching instance.
	 * <p>
	 * 解决唯一匹配给定对象类型(如果有的话)的bean实例,包括其bean名称<p>这实际上是一个{@link #getBean(Class)}的变体,它保留了匹配实例的bean名称
	 * 
	 * 
	 * @param requiredType type the bean must match; can be an interface or superclass.
	 * {@code null} is disallowed.
	 * @return the bean name plus bean instance
	 * @throws NoSuchBeanDefinitionException if no matching bean was found
	 * @throws NoUniqueBeanDefinitionException if more than one matching bean was found
	 * @throws BeansException if the bean could not be created
	 * @since 4.3.3
	 * @see #getBean(Class)
	 */
	<T> NamedBeanHolder<T> resolveNamedBean(Class<T> requiredType) throws BeansException;

	/**
	 * Resolve the specified dependency against the beans defined in this factory.
	 * <p>
	 *  解决与此工厂中定义的bean的指定依赖关系
	 * 
	 * 
	 * @param descriptor the descriptor for the dependency (field/method/constructor)
	 * @param requestingBeanName the name of the bean which declares the given dependency
	 * @return the resolved object, or {@code null} if none found
	 * @throws NoSuchBeanDefinitionException if no matching bean was found
	 * @throws NoUniqueBeanDefinitionException if more than one matching bean was found
	 * @throws BeansException if dependency resolution failed for any other reason
	 * @since 2.5
	 * @see #resolveDependency(DependencyDescriptor, String, Set, TypeConverter)
	 */
	Object resolveDependency(DependencyDescriptor descriptor, String requestingBeanName) throws BeansException;

	/**
	 * Resolve the specified dependency against the beans defined in this factory.
	 * <p>
	 *  解决与此工厂中定义的bean的指定依赖关系
	 * 
	 * @param descriptor the descriptor for the dependency (field/method/constructor)
	 * @param requestingBeanName the name of the bean which declares the given dependency
	 * @param autowiredBeanNames a Set that all names of autowired beans (used for
	 * resolving the given dependency) are supposed to be added to
	 * @param typeConverter the TypeConverter to use for populating arrays and collections
	 * @return the resolved object, or {@code null} if none found
	 * @throws NoSuchBeanDefinitionException if no matching bean was found
	 * @throws NoUniqueBeanDefinitionException if more than one matching bean was found
	 * @throws BeansException if dependency resolution failed for any other reason
	 * @since 2.5
	 * @see DependencyDescriptor
	 */
	Object resolveDependency(DependencyDescriptor descriptor, String requestingBeanName,
			Set<String> autowiredBeanNames, TypeConverter typeConverter) throws BeansException;

}
