/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2015 the original author or authors.
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

import java.util.Iterator;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

/**
 * Configuration interface to be implemented by most listable bean factories.
 * In addition to {@link ConfigurableBeanFactory}, it provides facilities to
 * analyze and modify bean definitions, and to pre-instantiate singletons.
 *
 * <p>This subinterface of {@link org.springframework.beans.factory.BeanFactory}
 * is not meant to be used in normal application code: Stick to
 * {@link org.springframework.beans.factory.BeanFactory} or
 * {@link org.springframework.beans.factory.ListableBeanFactory} for typical
 * use cases. This interface is just meant to allow for framework-internal
 * plug'n'play even when needing access to bean factory configuration methods.
 *
 * <p>
 *  大多数可列出的bean工厂要实现的配置界面除了{@link ConfigurableBeanFactory}之外,它还提供了分析和修改bean定义的功能,并且可以预先实例化单例
 * 
 * <p> {@link orgspringframeworkbeansfactoryBeanFactory}的这个子界面并不意味着在正常的应用程序代码中使用：对于典型的用例,坚持使用{@link orgspringframeworkbeansfactoryBeanFactory}
 * 或{@link orgspringframeworkbeansfactoryListableBeanFactory}此接口只是为了允许框架内部即使在需要访问bean工厂配置方法时即插即用。
 * 
 * 
 * @author Juergen Hoeller
 * @since 03.11.2003
 * @see org.springframework.context.support.AbstractApplicationContext#getBeanFactory()
 */
public interface ConfigurableListableBeanFactory
		extends ListableBeanFactory, AutowireCapableBeanFactory, ConfigurableBeanFactory {

	/**
	 * Ignore the given dependency type for autowiring:
	 * for example, String. Default is none.
	 * <p>
	 *  忽略给定的依赖关系类型进行自动布线：例如,String Default为none
	 * 
	 * 
	 * @param type the dependency type to ignore
	 */
	void ignoreDependencyType(Class<?> type);

	/**
	 * Ignore the given dependency interface for autowiring.
	 * <p>This will typically be used by application contexts to register
	 * dependencies that are resolved in other ways, like BeanFactory through
	 * BeanFactoryAware or ApplicationContext through ApplicationContextAware.
	 * <p>By default, only the BeanFactoryAware interface is ignored.
	 * For further types to ignore, invoke this method for each type.
	 * <p>
	 * 忽略给定的依赖接口进行自动布线<p>这通常由应用程序上下文用于注册以其他方式解析的依赖关系,例如BeanFactory通过BeanFactoryAware或ApplicationContext通过App
	 * licationContextAware <p>默认情况下,仅忽略BeanFactoryAware接口进一步要忽略的类型,请为每种类型调用此方法。
	 * 
	 * 
	 * @param ifc the dependency interface to ignore
	 * @see org.springframework.beans.factory.BeanFactoryAware
	 * @see org.springframework.context.ApplicationContextAware
	 */
	void ignoreDependencyInterface(Class<?> ifc);

	/**
	 * Register a special dependency type with corresponding autowired value.
	 * <p>This is intended for factory/context references that are supposed
	 * to be autowirable but are not defined as beans in the factory:
	 * e.g. a dependency of type ApplicationContext resolved to the
	 * ApplicationContext instance that the bean is living in.
	 * <p>Note: There are no such default types registered in a plain BeanFactory,
	 * not even for the BeanFactory interface itself.
	 * <p>
	 * 注册具有相应自动连线值的特殊依赖类型<p>这适用于应该是可自动布线的工厂/上下文引用,但未定义为工厂中的bean：例如将ApplicationContext类型的依赖关系解析为ApplicationCo
	 * ntext实例住在<p>注意：没有这样的默认类型在一个简单的BeanFactory中注册,甚至没有BeanFactory接口本身。
	 * 
	 * 
	 * @param dependencyType the dependency type to register. This will typically
	 * be a base interface such as BeanFactory, with extensions of it resolved
	 * as well if declared as an autowiring dependency (e.g. ListableBeanFactory),
	 * as long as the given value actually implements the extended interface.
	 * @param autowiredValue the corresponding autowired value. This may also be an
	 * implementation of the {@link org.springframework.beans.factory.ObjectFactory}
	 * interface, which allows for lazy resolution of the actual target value.
	 */
	void registerResolvableDependency(Class<?> dependencyType, Object autowiredValue);

	/**
	 * Determine whether the specified bean qualifies as an autowire candidate,
	 * to be injected into other beans which declare a dependency of matching type.
	 * <p>This method checks ancestor factories as well.
	 * <p>
	 *  确定指定的bean是否符合自动线路候选项,被注入到声明匹配类型的依赖关系的其他bean中<p>此方法也检查祖先工厂
	 * 
	 * 
	 * @param beanName the name of the bean to check
	 * @param descriptor the descriptor of the dependency to resolve
	 * @return whether the bean should be considered as autowire candidate
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 */
	boolean isAutowireCandidate(String beanName, DependencyDescriptor descriptor)
			throws NoSuchBeanDefinitionException;

	/**
	 * Return the registered BeanDefinition for the specified bean, allowing access
	 * to its property values and constructor argument value (which can be
	 * modified during bean factory post-processing).
	 * <p>A returned BeanDefinition object should not be a copy but the original
	 * definition object as registered in the factory. This means that it should
	 * be castable to a more specific implementation type, if necessary.
	 * <p><b>NOTE:</b> This method does <i>not</i> consider ancestor factories.
	 * It is only meant for accessing local bean definitions of this factory.
	 * <p>
	 * 返回指定bean的注册BeanDefinition,允许访问其属性值和构造函数参数值(可以在bean工厂后处理期间修改)<p>返回的BeanDefinition对象不应是副本,而是原始定义对象注册在工厂
	 * 中这意味着它应该可以转换为更具体的实现类型,如果需要<p> <b>注意：</b>此方法<i>不</i>考虑祖先工厂它只适用于访问此工厂的本地bean定义。
	 * 
	 * 
	 * @param beanName the name of the bean
	 * @return the registered BeanDefinition
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * defined in this factory
	 */
	BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

	/**
	 * Return a unified view over all bean names managed by this factory.
	 * <p>Includes bean definition names as well as names of manually registered
	 * singleton instances, with bean definition names consistently coming first,
	 * analogous to how type/annotation specific retrieval of bean names works.
	 * <p>
	 * 返回由该工厂管理的所有bean名称的统一视图<p>包括bean定义名称以及手动注册的单例实例的名称,其中bean定义名称始终保持先前,类似于bean名称的类型/注释特定检索的工作原理
	 * 
	 * 
	 * @return the composite iterator for the bean names view
	 * @since 4.1.2
	 * @see #containsBeanDefinition
	 * @see #registerSingleton
	 * @see #getBeanNamesForType
	 * @see #getBeanNamesForAnnotation
	 */
	Iterator<String> getBeanNamesIterator();

	/**
	 * Clear the merged bean definition cache, removing entries for beans
	 * which are not considered eligible for full metadata caching yet.
	 * <p>Typically triggered after changes to the original bean definitions,
	 * e.g. after applying a {@link BeanFactoryPostProcessor}. Note that metadata
	 * for beans which have already been created at this point will be kept around.
	 * <p>
	 *  清除合并的bean定义缓存,删除不符合资格进行完整元数据缓存的bean的条目<p>通常在更改原始bean定义后触发,例如在应用{@link BeanFactoryPostProcessor}后注意到具
	 * 有在这一点上已经被创造出来将被围绕着。
	 * 
	 * 
	 * @since 4.2
	 * @see #getBeanDefinition
	 * @see #getMergedBeanDefinition
	 */
	void clearMetadataCache();

	/**
	 * Freeze all bean definitions, signalling that the registered bean definitions
	 * will not be modified or post-processed any further.
	 * <p>This allows the factory to aggressively cache bean definition metadata.
	 * <p>
	 * 冻结所有bean定义,表明注册的bean定义将不会被修改或后处理任何进一步的<p>这允许工厂积极地缓存bean定义元数据
	 * 
	 */
	void freezeConfiguration();

	/**
	 * Return whether this factory's bean definitions are frozen,
	 * i.e. are not supposed to be modified or post-processed any further.
	 * <p>
	 *  返回此工厂的bean定义是否被冻结,即不应进一步修改或后处理
	 * 
	 * 
	 * @return {@code true} if the factory's configuration is considered frozen
	 */
	boolean isConfigurationFrozen();

	/**
	 * Ensure that all non-lazy-init singletons are instantiated, also considering
	 * {@link org.springframework.beans.factory.FactoryBean FactoryBeans}.
	 * Typically invoked at the end of factory setup, if desired.
	 * <p>
	 *  确保所有非懒惰初始化单例都被实例化,同时考虑到{@link orgspringframeworkbeansfactoryFactoryBean FactoryBeans}通常在出厂设置结束时调用,如果
	 * 需要的话。
	 * 
	 * @throws BeansException if one of the singleton beans could not be created.
	 * Note: This may have left the factory with some beans already initialized!
	 * Call {@link #destroySingletons()} for full cleanup in this case.
	 * @see #destroySingletons()
	 */
	void preInstantiateSingletons() throws BeansException;

}
