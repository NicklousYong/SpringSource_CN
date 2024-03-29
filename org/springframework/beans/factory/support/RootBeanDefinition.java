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

package org.springframework.beans.factory.support;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;

/**
 * A root bean definition represents the merged bean definition that backs
 * a specific bean in a Spring BeanFactory at runtime. It might have been created
 * from multiple original bean definitions that inherit from each other,
 * typically registered as {@link GenericBeanDefinition GenericBeanDefinitions}.
 * A root bean definition is essentially the 'unified' bean definition view at runtime.
 *
 * <p>Root bean definitions may also be used for registering individual bean definitions
 * in the configuration phase. However, since Spring 2.5, the preferred way to register
 * bean definitions programmatically is the {@link GenericBeanDefinition} class.
 * GenericBeanDefinition has the advantage that it allows to dynamically define
 * parent dependencies, not 'hard-coding' the role as a root bean definition.
 *
 * <p>
 * 根bean定义表示在运行时支持Spring BeanFactory中的特定bean的合并bean定义它可能是从相互继承的多个原始bean定义创建的,通常注册为{@link GenericBeanDefinition GenericBeanDefinitions}
 * 根bean定义是基本上是运行时的'统一'bean定义视图。
 * 
 *  <p>根bean定义也可以用于在配置阶段注册单个bean定义。
 * 但是,由于Spring 25,以编程方式注册bean定义的首选方式是{@link GenericBeanDefinition}类GenericBeanDefinition具有允许动态定义父依赖关系,而不
 * 是"硬编码"作为根bean定义的角色。
 *  <p>根bean定义也可以用于在配置阶段注册单个bean定义。
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see GenericBeanDefinition
 * @see ChildBeanDefinition
 */
@SuppressWarnings("serial")
public class RootBeanDefinition extends AbstractBeanDefinition {

	private BeanDefinitionHolder decoratedDefinition;

	private AnnotatedElement qualifiedElement;

	boolean allowCaching = true;

	boolean isFactoryMethodUnique = false;

	volatile ResolvableType targetType;

	/** Package-visible field for caching the determined Class of a given bean definition */
	volatile Class<?> resolvedTargetType;

	/** Package-visible field for caching the return type of a generically typed factory method */
	volatile Class<?> resolvedFactoryMethodReturnType;

	/** Common lock for the four constructor fields below */
	final Object constructorArgumentLock = new Object();

	/** Package-visible field for caching the resolved constructor or factory method */
	Object resolvedConstructorOrFactoryMethod;

	/** Package-visible field that marks the constructor arguments as resolved */
	boolean constructorArgumentsResolved = false;

	/** Package-visible field for caching fully resolved constructor arguments */
	Object[] resolvedConstructorArguments;

	/** Package-visible field for caching partly prepared constructor arguments */
	Object[] preparedConstructorArguments;

	/** Common lock for the two post-processing fields below */
	final Object postProcessingLock = new Object();

	/** Package-visible field that indicates MergedBeanDefinitionPostProcessor having been applied */
	boolean postProcessed = false;

	/** Package-visible field that indicates a before-instantiation post-processor having kicked in */
	volatile Boolean beforeInstantiationResolved;

	private Set<Member> externallyManagedConfigMembers;

	private Set<String> externallyManagedInitMethods;

	private Set<String> externallyManagedDestroyMethods;


	/**
	 * Create a new RootBeanDefinition, to be configured through its bean
	 * properties and configuration methods.
	 * <p>
	 * 创建一个新的RootBeanDefinition,通过其bean属性和配置方法进行配置
	 * 
	 * 
	 * @see #setBeanClass
	 * @see #setBeanClassName
	 * @see #setScope
	 * @see #setAutowireMode
	 * @see #setDependencyCheck
	 * @see #setConstructorArgumentValues
	 * @see #setPropertyValues
	 */
	public RootBeanDefinition() {
		super();
	}

	/**
	 * Create a new RootBeanDefinition for a singleton.
	 * <p>
	 *  为单例创建一个新的RootBeanDefinition
	 * 
	 * 
	 * @param beanClass the class of the bean to instantiate
	 */
	public RootBeanDefinition(Class<?> beanClass) {
		super();
		setBeanClass(beanClass);
	}

	/**
	 * Create a new RootBeanDefinition for a singleton,
	 * using the given autowire mode.
	 * <p>
	 *  使用给定的自动线模式为单例创建一个新的RootBeanDefinition
	 * 
	 * 
	 * @param beanClass the class of the bean to instantiate
	 * @param autowireMode by name or type, using the constants in this interface
	 * @param dependencyCheck whether to perform a dependency check for objects
	 * (not applicable to autowiring a constructor, thus ignored there)
	 */
	public RootBeanDefinition(Class<?> beanClass, int autowireMode, boolean dependencyCheck) {
		super();
		setBeanClass(beanClass);
		setAutowireMode(autowireMode);
		if (dependencyCheck && getResolvedAutowireMode() != AUTOWIRE_CONSTRUCTOR) {
			setDependencyCheck(DEPENDENCY_CHECK_OBJECTS);
		}
	}

	/**
	 * Create a new RootBeanDefinition for a singleton,
	 * providing constructor arguments and property values.
	 * <p>
	 *  为单例创建一个新的RootBeanDefinition,提供构造函数参数和属性值
	 * 
	 * 
	 * @param beanClass the class of the bean to instantiate
	 * @param cargs the constructor argument values to apply
	 * @param pvs the property values to apply
	 */
	public RootBeanDefinition(Class<?> beanClass, ConstructorArgumentValues cargs, MutablePropertyValues pvs) {
		super(cargs, pvs);
		setBeanClass(beanClass);
	}

	/**
	 * Create a new RootBeanDefinition for a singleton,
	 * providing constructor arguments and property values.
	 * <p>Takes a bean class name to avoid eager loading of the bean class.
	 * <p>
	 *  为单例创建一个新的RootBeanDefinition,提供构造函数参数和属性值<p>获取一个bean类名,以避免加载bean类
	 * 
	 * 
	 * @param beanClassName the name of the class to instantiate
	 */
	public RootBeanDefinition(String beanClassName) {
		setBeanClassName(beanClassName);
	}

	/**
	 * Create a new RootBeanDefinition for a singleton,
	 * providing constructor arguments and property values.
	 * <p>Takes a bean class name to avoid eager loading of the bean class.
	 * <p>
	 *  为单例创建一个新的RootBeanDefinition,提供构造函数参数和属性值<p>获取一个bean类名,以避免加载bean类
	 * 
	 * 
	 * @param beanClassName the name of the class to instantiate
	 * @param cargs the constructor argument values to apply
	 * @param pvs the property values to apply
	 */
	public RootBeanDefinition(String beanClassName, ConstructorArgumentValues cargs, MutablePropertyValues pvs) {
		super(cargs, pvs);
		setBeanClassName(beanClassName);
	}

	/**
	 * Create a new RootBeanDefinition as deep copy of the given
	 * bean definition.
	 * <p>
	 *  创建一个新的RootBeanDefinition作为给定bean定义的深层副本
	 * 
	 * 
	 * @param original the original bean definition to copy from
	 */
	public RootBeanDefinition(RootBeanDefinition original) {
		super(original);
		this.decoratedDefinition = original.decoratedDefinition;
		this.qualifiedElement = original.qualifiedElement;
		this.allowCaching = original.allowCaching;
		this.isFactoryMethodUnique = original.isFactoryMethodUnique;
		this.targetType = original.targetType;
	}

	/**
	 * Create a new RootBeanDefinition as deep copy of the given
	 * bean definition.
	 * <p>
	 * 创建一个新的RootBeanDefinition作为给定bean定义的深层副本
	 * 
	 * 
	 * @param original the original bean definition to copy from
	 */
	RootBeanDefinition(BeanDefinition original) {
		super(original);
	}


	@Override
	public String getParentName() {
		return null;
	}

	@Override
	public void setParentName(String parentName) {
		if (parentName != null) {
			throw new IllegalArgumentException("Root bean cannot be changed into a child bean with parent reference");
		}
	}

	/**
	 * Register a target definition that is being decorated by this bean definition.
	 * <p>
	 *  注册正在通过此bean定义进行装饰的目标定义
	 * 
	 */
	public void setDecoratedDefinition(BeanDefinitionHolder decoratedDefinition) {
		this.decoratedDefinition = decoratedDefinition;
	}

	/**
	 * Return the target definition that is being decorated by this bean definition, if any.
	 * <p>
	 *  返回正在被这个bean定义装饰的目标定义(如果有的话)
	 * 
	 */
	public BeanDefinitionHolder getDecoratedDefinition() {
		return this.decoratedDefinition;
	}

	/**
	 * Specify the {@link AnnotatedElement} defining qualifiers,
	 * to be used instead of the target class or factory method.
	 * <p>
	 *  指定{@link AnnotatedElement}定义限定符,而不是目标类或工厂方法
	 * 
	 * 
	 * @since 4.3.3
	 * @see #setTargetType(ResolvableType)
	 * @see #getResolvedFactoryMethod()
	 */
	public void setQualifiedElement(AnnotatedElement qualifiedElement) {
		this.qualifiedElement = qualifiedElement;
	}

	/**
	 * Return the {@link AnnotatedElement} defining qualifiers, if any.
	 * Otherwise, the factory method and target class will be checked.
	 * <p>
	 *  返回{@link AnnotatedElement}定义限定符,否则,将检查工厂方法和目标类
	 * 
	 * 
	 * @since 4.3.3
	 */
	public AnnotatedElement getQualifiedElement() {
		return this.qualifiedElement;
	}

	/**
	 * Specify a generics-containing target type of this bean definition, if known in advance.
	 * <p>
	 *  如果提前知道,请指定此bean定义的泛型包含目标类型
	 * 
	 * 
	 * @since 4.3.3
	 */
	public void setTargetType(ResolvableType targetType) {
		this.targetType = targetType;
	}

	/**
	 * Specify the target type of this bean definition, if known in advance.
	 * <p>
	 *  指定此bean定义的目标类型,如果事先知道的话
	 * 
	 * 
	 * @since 3.2.2
	 */
	public void setTargetType(Class<?> targetType) {
		this.targetType = (targetType != null ? ResolvableType.forClass(targetType) : null);
	}

	/**
	 * Return the target type of this bean definition, if known
	 * (either specified in advance or resolved on first instantiation).
	 * <p>
	 *  返回此bean定义的目标类型,如果已知(预先指定或首次实例化时解析)
	 * 
	 * 
	 * @since 3.2.2
	 */
	public Class<?> getTargetType() {
		if (this.resolvedTargetType != null) {
			return this.resolvedTargetType;
		}
		return (this.targetType != null ? this.targetType.resolve() : null);
	}

	/**
	 * Specify a factory method name that refers to a non-overloaded method.
	 * <p>
	 * 指定引用非重载方法的工厂方法名称
	 * 
	 */
	public void setUniqueFactoryMethodName(String name) {
		Assert.hasText(name, "Factory method name must not be empty");
		setFactoryMethodName(name);
		this.isFactoryMethodUnique = true;
	}

	/**
	 * Check whether the given candidate qualifies as a factory method.
	 * <p>
	 *  检查给定的候选人是否符合工厂方法
	 * 
	 */
	public boolean isFactoryMethod(Method candidate) {
		return (candidate != null && candidate.getName().equals(getFactoryMethodName()));
	}

	/**
	 * Return the resolved factory method as a Java Method object, if available.
	 * <p>
	 *  将解析的工厂方法作为Java Method对象返回(如果可用)
	 * 
	 * @return the factory method, or {@code null} if not found or not resolved yet
	 */
	public Method getResolvedFactoryMethod() {
		synchronized (this.constructorArgumentLock) {
			Object candidate = this.resolvedConstructorOrFactoryMethod;
			return (candidate instanceof Method ? (Method) candidate : null);
		}
	}

	public void registerExternallyManagedConfigMember(Member configMember) {
		synchronized (this.postProcessingLock) {
			if (this.externallyManagedConfigMembers == null) {
				this.externallyManagedConfigMembers = new HashSet<Member>(1);
			}
			this.externallyManagedConfigMembers.add(configMember);
		}
	}

	public boolean isExternallyManagedConfigMember(Member configMember) {
		synchronized (this.postProcessingLock) {
			return (this.externallyManagedConfigMembers != null &&
					this.externallyManagedConfigMembers.contains(configMember));
		}
	}

	public void registerExternallyManagedInitMethod(String initMethod) {
		synchronized (this.postProcessingLock) {
			if (this.externallyManagedInitMethods == null) {
				this.externallyManagedInitMethods = new HashSet<String>(1);
			}
			this.externallyManagedInitMethods.add(initMethod);
		}
	}

	public boolean isExternallyManagedInitMethod(String initMethod) {
		synchronized (this.postProcessingLock) {
			return (this.externallyManagedInitMethods != null &&
					this.externallyManagedInitMethods.contains(initMethod));
		}
	}

	public void registerExternallyManagedDestroyMethod(String destroyMethod) {
		synchronized (this.postProcessingLock) {
			if (this.externallyManagedDestroyMethods == null) {
				this.externallyManagedDestroyMethods = new HashSet<String>(1);
			}
			this.externallyManagedDestroyMethods.add(destroyMethod);
		}
	}

	public boolean isExternallyManagedDestroyMethod(String destroyMethod) {
		synchronized (this.postProcessingLock) {
			return (this.externallyManagedDestroyMethods != null &&
					this.externallyManagedDestroyMethods.contains(destroyMethod));
		}
	}


	@Override
	public RootBeanDefinition cloneBeanDefinition() {
		return new RootBeanDefinition(this);
	}

	@Override
	public boolean equals(Object other) {
		return (this == other || (other instanceof RootBeanDefinition && super.equals(other)));
	}

	@Override
	public String toString() {
		return "Root bean: " + super.toString();
	}

}
