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

/**
 * Interface that defines a registry for shared bean instances.
 * Can be implemented by {@link org.springframework.beans.factory.BeanFactory}
 * implementations in order to expose their singleton management facility
 * in a uniform manner.
 *
 * <p>The {@link ConfigurableBeanFactory} interface extends this interface.
 *
 * <p>
 * 为共享bean实例定义注册表的接口可以由{@link orgspringframeworkbeansfactoryBeanFactory}实现来实现,以便以统一的方式公开他们的单身管理工具
 * 
 *  <p> {@link ConfigurableBeanFactory}界面扩展了此界面
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.0
 * @see ConfigurableBeanFactory
 * @see org.springframework.beans.factory.support.DefaultSingletonBeanRegistry
 * @see org.springframework.beans.factory.support.AbstractBeanFactory
 */
public interface SingletonBeanRegistry {

	/**
	 * Register the given existing object as singleton in the bean registry,
	 * under the given bean name.
	 * <p>The given instance is supposed to be fully initialized; the registry
	 * will not perform any initialization callbacks (in particular, it won't
	 * call InitializingBean's {@code afterPropertiesSet} method).
	 * The given instance will not receive any destruction callbacks
	 * (like DisposableBean's {@code destroy} method) either.
	 * <p>When running within a full BeanFactory: <b>Register a bean definition
	 * instead of an existing instance if your bean is supposed to receive
	 * initialization and/or destruction callbacks.</b>
	 * <p>Typically invoked during registry configuration, but can also be used
	 * for runtime registration of singletons. As a consequence, a registry
	 * implementation should synchronize singleton access; it will have to do
	 * this anyway if it supports a BeanFactory's lazy initialization of singletons.
	 * <p>
	 * 在给定的bean名称下,在bean注册表中注册给定的现有对象作为单例,给定的实例应该被完全初始化;注册表不会执行任何初始化回调(特别是它不会调用InitializingBean的{@code afterPropertiesSet}
	 * 方法)给定的实例将不会收到任何销毁回调(如DisposableBean的{@code destroy}方法))<p>运行时在一个完整的BeanFactory中：<b>注册一个bean定义而不是现有的实例
	 * ,如果你的bean应该接收初始化和/或破坏回调</b> <p>通常在注册表配置期间被调用,但也可以用于运行时注册单身人士因此,注册表实现应该同步单例访问;如果它支持BeanFactory对单例的懒惰初始
	 * 化,它将不得不这样做。
	 * 
	 * 
	 * @param beanName the name of the bean
	 * @param singletonObject the existing singleton object
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet
	 * @see org.springframework.beans.factory.DisposableBean#destroy
	 * @see org.springframework.beans.factory.support.BeanDefinitionRegistry#registerBeanDefinition
	 */
	void registerSingleton(String beanName, Object singletonObject);

	/**
	 * Return the (raw) singleton object registered under the given name.
	 * <p>Only checks already instantiated singletons; does not return an Object
	 * for singleton bean definitions which have not been instantiated yet.
	 * <p>The main purpose of this method is to access manually registered singletons
	 * (see {@link #registerSingleton}). Can also be used to access a singleton
	 * defined by a bean definition that already been created, in a raw fashion.
	 * <p><b>NOTE:</b> This lookup method is not aware of FactoryBean prefixes or aliases.
	 * You need to resolve the canonical bean name first before obtaining the singleton instance.
	 * <p>
	 * 返回以给定名称注册的(原始)单例对象<p>只检查已经实例化的单例;不返回尚未实例化的单例bean定义的对象<p>此方法的主要目的是访问手动注册的单例(请参阅{@link #registerSingleton}
	 * )也可以用于访问由已经创建的bean定义,以原始方式<p> <b>注意：</b>此查找方法不知道FactoryBean前缀或别名您需要在获取单例实例之前首先解析规范bean名称。
	 * 
	 * 
	 * @param beanName the name of the bean to look for
	 * @return the registered singleton object, or {@code null} if none found
	 * @see ConfigurableListableBeanFactory#getBeanDefinition
	 */
	Object getSingleton(String beanName);

	/**
	 * Check if this registry contains a singleton instance with the given name.
	 * <p>Only checks already instantiated singletons; does not return {@code true}
	 * for singleton bean definitions which have not been instantiated yet.
	 * <p>The main purpose of this method is to check manually registered singletons
	 * (see {@link #registerSingleton}). Can also be used to check whether a
	 * singleton defined by a bean definition has already been created.
	 * <p>To check whether a bean factory contains a bean definition with a given name,
	 * use ListableBeanFactory's {@code containsBeanDefinition}. Calling both
	 * {@code containsBeanDefinition} and {@code containsSingleton} answers
	 * whether a specific bean factory contains a local bean instance with the given name.
	 * <p>Use BeanFactory's {@code containsBean} for general checks whether the
	 * factory knows about a bean with a given name (whether manually registered singleton
	 * instance or created by bean definition), also checking ancestor factories.
	 * <p><b>NOTE:</b> This lookup method is not aware of FactoryBean prefixes or aliases.
	 * You need to resolve the canonical bean name first before checking the singleton status.
	 * <p>
	 * 检查此注册表是否包含具有给定名称的单例实例<p>仅检查已实例化的单例;对于尚未实例化的单例bean定义,不返回{@code true} <p>此方法的主要目的是检查手动注册的单例(请参阅{@link #registerSingleton}
	 * )也可用于检查是否由bean定义的单例已经被创建<p>要检查bean工厂是否包含具有给定名称的bean定义,请使用ListableBeanFactory的{@code containsBeanDefinition}
	 * 调用{@code containsBeanDefinition}和{@code containsSingleton}答案是否一个特定的bean工厂包含一个具有给定名称的本地bean实例<p>使用Bean
	 * Factory的{@code containsBean}进行通用检查,是否知道工厂是否知道具有给定名称的bean(无论是手动注册的单例实例还是由bean定义创建),还检查祖先工厂<p> <b>注意： /
	 *  b>此查找方法不知道FactoryBean前缀或别名您需要首先解析规范bean名称,然后再检查单例状态。
	 * 
	 * 
	 * @param beanName the name of the bean to look for
	 * @return if this bean factory contains a singleton instance with the given name
	 * @see #registerSingleton
	 * @see org.springframework.beans.factory.ListableBeanFactory#containsBeanDefinition
	 * @see org.springframework.beans.factory.BeanFactory#containsBean
	 */
	boolean containsSingleton(String beanName);

	/**
	 * Return the names of singleton beans registered in this registry.
	 * <p>Only checks already instantiated singletons; does not return names
	 * for singleton bean definitions which have not been instantiated yet.
	 * <p>The main purpose of this method is to check manually registered singletons
	 * (see {@link #registerSingleton}). Can also be used to check which singletons
	 * defined by a bean definition have already been created.
	 * <p>
	 * 返回在此注册表中注册的单例bean的名称<p>只检查已经实例化的单例;不返回尚未实例化的单例bean定义的名称<p>此方法的主要目的是检查手动注册的单例(请参阅{@link #registerSingleton}
	 * )也可以用于检查由bean定义的单例定义已经创建。
	 * 
	 * 
	 * @return the list of names as a String array (never {@code null})
	 * @see #registerSingleton
	 * @see org.springframework.beans.factory.support.BeanDefinitionRegistry#getBeanDefinitionNames
	 * @see org.springframework.beans.factory.ListableBeanFactory#getBeanDefinitionNames
	 */
	String[] getSingletonNames();

	/**
	 * Return the number of singleton beans registered in this registry.
	 * <p>Only checks already instantiated singletons; does not count
	 * singleton bean definitions which have not been instantiated yet.
	 * <p>The main purpose of this method is to check manually registered singletons
	 * (see {@link #registerSingleton}). Can also be used to count the number of
	 * singletons defined by a bean definition that have already been created.
	 * <p>
	 * 返回在此注册表中注册的单例Bean的数量<p>只检查已实例化的单例;不计算尚未实例化的单例bean定义<p>此方法的主要目的是检查手动注册的单例(请参阅{@link #registerSingleton}
	 * )也可用于计算由bean定义的单例数已经创建的定义。
	 * 
	 * 
	 * @return the number of singleton beans
	 * @see #registerSingleton
	 * @see org.springframework.beans.factory.support.BeanDefinitionRegistry#getBeanDefinitionCount
	 * @see org.springframework.beans.factory.ListableBeanFactory#getBeanDefinitionCount
	 */
	int getSingletonCount();

	/**
	 * Return the singleton mutex used by this registry (for external collaborators).
	 * <p>
	 * 
	 * @return the mutex object (never {@code null})
	 * @since 4.2
	 */
	Object getSingletonMutex();

}
