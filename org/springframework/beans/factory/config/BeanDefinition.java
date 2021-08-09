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

package org.springframework.beans.factory.config;

import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.AttributeAccessor;

/**
 * A BeanDefinition describes a bean instance, which has property values,
 * constructor argument values, and further information supplied by
 * concrete implementations.
 *
 * <p>This is just a minimal interface: The main intention is to allow a
 * {@link BeanFactoryPostProcessor} such as {@link PropertyPlaceholderConfigurer}
 * to introspect and modify property values and other bean metadata.
 *
 * <p>
 *  BeanDefinition描述了一个bean实例,它具有属性值,构造函数参数值以及具体实现提供的进一步信息
 * 
 * <p>这只是一个最小的界面：主要目的是允许一个{@link BeanFactoryPostProcessor},例如{@link PropertyPlaceholderConfigurer}来内省和修改
 * 属性值和其他bean元数据。
 * 
 * 
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 19.03.2004
 * @see ConfigurableListableBeanFactory#getBeanDefinition
 * @see org.springframework.beans.factory.support.RootBeanDefinition
 * @see org.springframework.beans.factory.support.ChildBeanDefinition
 */
public interface BeanDefinition extends AttributeAccessor, BeanMetadataElement {

	/**
	 * Scope identifier for the standard singleton scope: "singleton".
	 * <p>Note that extended bean factories might support further scopes.
	 * <p>
	 *  标准单例范围的范围标识符："singleton"<p>请注意,扩展bean工厂可能会支持进一步的范围
	 * 
	 * 
	 * @see #setScope
	 */
	String SCOPE_SINGLETON = ConfigurableBeanFactory.SCOPE_SINGLETON;

	/**
	 * Scope identifier for the standard prototype scope: "prototype".
	 * <p>Note that extended bean factories might support further scopes.
	 * <p>
	 *  标准原型范围的范围标识符："prototype"<p>请注意,扩展bean工厂可能会支持进一步的范围
	 * 
	 * 
	 * @see #setScope
	 */
	String SCOPE_PROTOTYPE = ConfigurableBeanFactory.SCOPE_PROTOTYPE;


	/**
	 * Role hint indicating that a {@code BeanDefinition} is a major part
	 * of the application. Typically corresponds to a user-defined bean.
	 * <p>
	 *  角色提示,指示{@code BeanDefinition}是应用程序的主要部分通常对应于用户定义的bean
	 * 
	 */
	int ROLE_APPLICATION = 0;

	/**
	 * Role hint indicating that a {@code BeanDefinition} is a supporting
	 * part of some larger configuration, typically an outer
	 * {@link org.springframework.beans.factory.parsing.ComponentDefinition}.
	 * {@code SUPPORT} beans are considered important enough to be aware
	 * of when looking more closely at a particular
	 * {@link org.springframework.beans.factory.parsing.ComponentDefinition},
	 * but not when looking at the overall configuration of an application.
	 * <p>
	 * 角色提示,表明{@code BeanDefinition}是一些较大配置的支持部分,通常是外部{@link orgspringframeworkbeansfactoryparsingComponentDefinition}
	 *  {@code SUPPORT} bean被认为是足够重要的,以便在更仔细地查看特定的{@链接orgspringframeworkbeansfactoryparsingComponentDefinition}
	 * ,但不是在查看应用程序的整体配置。
	 * 
	 */
	int ROLE_SUPPORT = 1;

	/**
	 * Role hint indicating that a {@code BeanDefinition} is providing an
	 * entirely background role and has no relevance to the end-user. This hint is
	 * used when registering beans that are completely part of the internal workings
	 * of a {@link org.springframework.beans.factory.parsing.ComponentDefinition}.
	 * <p>
	 *  角色提示,指出{@code BeanDefinition}提供了完全的后台角色,与最终用户无关。
	 * 当注册完全属于{@link orgspringframeworkbeansfactoryparsingComponentDefinition}的内部工作的bean时,将使用此提示。
	 * 
	 */
	int ROLE_INFRASTRUCTURE = 2;


	/**
	 * Return the name of the parent definition of this bean definition, if any.
	 * <p>
	 * 返回此bean定义的父定义的名称(如果有)
	 * 
	 */
	String getParentName();

	/**
	 * Set the name of the parent definition of this bean definition, if any.
	 * <p>
	 *  设置此bean定义的父定义的名称(如果有)
	 * 
	 */
	void setParentName(String parentName);

	/**
	 * Return the current bean class name of this bean definition.
	 * <p>Note that this does not have to be the actual class name used at runtime, in
	 * case of a child definition overriding/inheriting the class name from its parent.
	 * Hence, do <i>not</i> consider this to be the definitive bean type at runtime but
	 * rather only use it for parsing purposes at the individual bean definition level.
	 * <p>
	 *  返回此bean定义的当前bean类名称<p>请注意,这不一定是运行时使用的实际类名,以防子级定义从其父级覆盖/继承类名称因此,请执行<i>不认为这是在运行时是确定的bean类型,而是仅在各个bean定
	 * 义级别使用它来解析目的。
	 * 
	 */
	String getBeanClassName();

	/**
	 * Override the bean class name of this bean definition.
	 * <p>The class name can be modified during bean factory post-processing,
	 * typically replacing the original class name with a parsed variant of it.
	 * <p>
	 *  覆盖此bean定义的bean类名称<p>可以在bean工厂后处理期间修改类名,通常用原始类名称替换其解析变体
	 * 
	 */
	void setBeanClassName(String beanClassName);

	/**
	 * Return the factory bean name, if any.
	 * <p>
	 *  返回工厂bean名称,如果有的话
	 * 
	 */
	String getFactoryBeanName();

	/**
	 * Specify the factory bean to use, if any.
	 * <p>
	 * 指定要使用的工厂bean(如果有)
	 * 
	 */
	void setFactoryBeanName(String factoryBeanName);

	/**
	 * Return a factory method, if any.
	 * <p>
	 *  返回工厂方法(如果有)
	 * 
	 */
	String getFactoryMethodName();

	/**
	 * Specify a factory method, if any. This method will be invoked with
	 * constructor arguments, or with no arguments if none are specified.
	 * The method will be invoked on the specified factory bean, if any,
	 * or otherwise as a static method on the local bean class.
	 * <p>
	 *  指定一个工厂方法(如果有的话)该方法将使用构造函数参数进行调用,如果未指定,则不使用任何参数。该方法将在指定的工厂bean(如果有)或以其他方式作为本地bean类上的静态方法
	 * 
	 * 
	 * @param factoryMethodName static factory method name,
	 * or {@code null} if normal constructor creation should be used
	 * @see #getBeanClassName()
	 */
	void setFactoryMethodName(String factoryMethodName);

	/**
	 * Return the name of the current target scope for this bean,
	 * or {@code null} if not known yet.
	 * <p>
	 *  返回此bean的当前目标范围的名称,如果尚未知道,则返回{@code null}
	 * 
	 */
	String getScope();

	/**
	 * Override the target scope of this bean, specifying a new scope name.
	 * <p>
	 *  覆盖此bean的目标范围,指定一个新的范围名称
	 * 
	 * 
	 * @see #SCOPE_SINGLETON
	 * @see #SCOPE_PROTOTYPE
	 */
	void setScope(String scope);

	/**
	 * Return whether this bean should be lazily initialized, i.e. not
	 * eagerly instantiated on startup. Only applicable to a singleton bean.
	 * <p>
	 *  返回这个bean是否应该被懒惰地初始化,即在启动时不是急切的实例化只适用于单例bean
	 * 
	 */
	boolean isLazyInit();

	/**
	 * Set whether this bean should be lazily initialized.
	 * <p>If {@code false}, the bean will get instantiated on startup by bean
	 * factories that perform eager initialization of singletons.
	 * <p>
	 * 设置这个bean是否应该被延迟初始化<p>如果{@code false},bean将在启动时由Bean工厂实例化,这些bean工厂执行急速初始化单例
	 * 
	 */
	void setLazyInit(boolean lazyInit);

	/**
	 * Return the bean names that this bean depends on.
	 * <p>
	 *  返回该bean依赖的bean名称
	 * 
	 */
	String[] getDependsOn();

	/**
	 * Set the names of the beans that this bean depends on being initialized.
	 * The bean factory will guarantee that these beans get initialized first.
	 * <p>
	 *  设置bean所依赖的bean的名称被初始化bean工厂将保证这些bean首先被初始化
	 * 
	 */
	void setDependsOn(String... dependsOn);

	/**
	 * Return whether this bean is a candidate for getting autowired into some other bean.
	 * <p>
	 *  返回这个bean是否是自动连线到其他bean的候选项
	 * 
	 */
	boolean isAutowireCandidate();

	/**
	 * Set whether this bean is a candidate for getting autowired into some other bean.
	 * <p>
	 *  设置这个bean是否是自动连线到其他bean的候选项
	 * 
	 */
	void setAutowireCandidate(boolean autowireCandidate);

	/**
	 * Return whether this bean is a primary autowire candidate.
	 * If this value is true for exactly one bean among multiple
	 * matching candidates, it will serve as a tie-breaker.
	 * <p>
	 *  返回这个bean是否是主要的自动线路候选者如果这个值对于多个匹配候选者中的一个bean是真实的,它将用作一个连接器
	 * 
	 */
	boolean isPrimary();

	/**
	 * Set whether this bean is a primary autowire candidate.
	 * <p>If this value is true for exactly one bean among multiple
	 * matching candidates, it will serve as a tie-breaker.
	 * <p>
	 * 设置这个bean是否是主要的自动线路候选者<p>如果这个值对于多个匹配候选者中的一个bean是真实的,则它将用作连接器
	 * 
	 */
	void setPrimary(boolean primary);


	/**
	 * Return the constructor argument values for this bean.
	 * <p>The returned instance can be modified during bean factory post-processing.
	 * <p>
	 *  返回此bean的构造函数参数值<p>可以在bean工厂后处理期间修改返回的实例
	 * 
	 * 
	 * @return the ConstructorArgumentValues object (never {@code null})
	 */
	ConstructorArgumentValues getConstructorArgumentValues();

	/**
	 * Return the property values to be applied to a new instance of the bean.
	 * <p>The returned instance can be modified during bean factory post-processing.
	 * <p>
	 *  返回要应用于bean的新实例的属性值<p>可以在bean工厂后处理期间修改返回的实例
	 * 
	 * 
	 * @return the MutablePropertyValues object (never {@code null})
	 */
	MutablePropertyValues getPropertyValues();


	/**
	 * Return whether this a <b>Singleton</b>, with a single, shared instance
	 * returned on all calls.
	 * <p>
	 *  返回这是否是一个<b> Singleton </b>,在所有调用上返回一个共享的实例
	 * 
	 * 
	 * @see #SCOPE_SINGLETON
	 */
	boolean isSingleton();

	/**
	 * Return whether this a <b>Prototype</b>, with an independent instance
	 * returned for each call.
	 * <p>
	 *  返回这是否为原型</b>,每个调用返回一个独立的实例
	 * 
	 * 
	 * @see #SCOPE_PROTOTYPE
	 */
	boolean isPrototype();

	/**
	 * Return whether this bean is "abstract", that is, not meant to be instantiated.
	 * <p>
	 *  返回这个bean是否是"抽象的",也就是说,不是要实例化的
	 * 
	 */
	boolean isAbstract();

	/**
	 * Get the role hint for this {@code BeanDefinition}. The role hint
	 * provides the frameworks as well as tools with an indication of
	 * the role and importance of a particular {@code BeanDefinition}.
	 * <p>
	 * 获取{@code BeanDefinition}的角色提示角色提示提供框架以及工具,指出特定{@code BeanDefinition}的角色和重要性
	 * 
	 * 
	 * @see #ROLE_APPLICATION
	 * @see #ROLE_SUPPORT
	 * @see #ROLE_INFRASTRUCTURE
	 */
	int getRole();

	/**
	 * Return a human-readable description of this bean definition.
	 * <p>
	 *  返回这个bean定义的可读描述
	 * 
	 */
	String getDescription();

	/**
	 * Return a description of the resource that this bean definition
	 * came from (for the purpose of showing context in case of errors).
	 * <p>
	 *  返回此bean定义来源的资源描述(出于错误的情况下显示上下文的目的)
	 * 
	 */
	String getResourceDescription();

	/**
	 * Return the originating BeanDefinition, or {@code null} if none.
	 * Allows for retrieving the decorated bean definition, if any.
	 * <p>Note that this method returns the immediate originator. Iterate through the
	 * originator chain to find the original BeanDefinition as defined by the user.
	 * <p>
	 *  返回原始BeanDefinition或{@code null} if none允许检索装饰的bean定义,如果有任何<p>请注意,此方法返回立即发起者通过始发者链迭代以查找由用户定义的原始BeanDe
	 * finition。
	 */
	BeanDefinition getOriginatingBeanDefinition();

}
