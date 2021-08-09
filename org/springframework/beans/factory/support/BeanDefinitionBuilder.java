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

import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.util.ObjectUtils;

/**
 * Programmatic means of constructing
 * {@link org.springframework.beans.factory.config.BeanDefinition BeanDefinitions}
 * using the builder pattern. Intended primarily for use when implementing Spring 2.0
 * {@link org.springframework.beans.factory.xml.NamespaceHandler NamespaceHandlers}.
 *
 * <p>
 * 使用构建器模式构建{@link orgspringframeworkbeansfactoryconfigBeanDefinition BeanDefinitions}的编程方法主要用于实现Spring 
 * 20时使用{@link orgspringframeworkbeansfactoryxmlNamespaceHandler NamespaceHandlers}。
 * 
 * 
 * @author Rod Johnson
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 */
public class BeanDefinitionBuilder {

	/**
	 * Create a new {@code BeanDefinitionBuilder} used to construct a {@link GenericBeanDefinition}.
	 * <p>
	 *  创建一个新的{@code BeanDefinitionBuilder},用于构造一个{@link GenericBeanDefinition}
	 * 
	 */
	public static BeanDefinitionBuilder genericBeanDefinition() {
		BeanDefinitionBuilder builder = new BeanDefinitionBuilder();
		builder.beanDefinition = new GenericBeanDefinition();
		return builder;
	}

	/**
	 * Create a new {@code BeanDefinitionBuilder} used to construct a {@link GenericBeanDefinition}.
	 * <p>
	 *  创建一个新的{@code BeanDefinitionBuilder},用于构造一个{@link GenericBeanDefinition}
	 * 
	 * 
	 * @param beanClass the {@code Class} of the bean that the definition is being created for
	 */
	public static BeanDefinitionBuilder genericBeanDefinition(Class<?> beanClass) {
		BeanDefinitionBuilder builder = new BeanDefinitionBuilder();
		builder.beanDefinition = new GenericBeanDefinition();
		builder.beanDefinition.setBeanClass(beanClass);
		return builder;
	}

	/**
	 * Create a new {@code BeanDefinitionBuilder} used to construct a {@link GenericBeanDefinition}.
	 * <p>
	 *  创建一个新的{@code BeanDefinitionBuilder},用于构造一个{@link GenericBeanDefinition}
	 * 
	 * 
	 * @param beanClassName the class name for the bean that the definition is being created for
	 */
	public static BeanDefinitionBuilder genericBeanDefinition(String beanClassName) {
		BeanDefinitionBuilder builder = new BeanDefinitionBuilder();
		builder.beanDefinition = new GenericBeanDefinition();
		builder.beanDefinition.setBeanClassName(beanClassName);
		return builder;
	}

	/**
	 * Create a new {@code BeanDefinitionBuilder} used to construct a {@link RootBeanDefinition}.
	 * <p>
	 *  创建一个新的{@code BeanDefinitionBuilder},用于构造一个{@link RootBeanDefinition}
	 * 
	 * 
	 * @param beanClass the {@code Class} of the bean that the definition is being created for
	 */
	public static BeanDefinitionBuilder rootBeanDefinition(Class<?> beanClass) {
		return rootBeanDefinition(beanClass, null);
	}

	/**
	 * Create a new {@code BeanDefinitionBuilder} used to construct a {@link RootBeanDefinition}.
	 * <p>
	 *  创建一个新的{@code BeanDefinitionBuilder},用于构造一个{@link RootBeanDefinition}
	 * 
	 * 
	 * @param beanClass the {@code Class} of the bean that the definition is being created for
	 * @param factoryMethodName the name of the method to use to construct the bean instance
	 */
	public static BeanDefinitionBuilder rootBeanDefinition(Class<?> beanClass, String factoryMethodName) {
		BeanDefinitionBuilder builder = new BeanDefinitionBuilder();
		builder.beanDefinition = new RootBeanDefinition();
		builder.beanDefinition.setBeanClass(beanClass);
		builder.beanDefinition.setFactoryMethodName(factoryMethodName);
		return builder;
	}

	/**
	 * Create a new {@code BeanDefinitionBuilder} used to construct a {@link RootBeanDefinition}.
	 * <p>
	 * 创建一个新的{@code BeanDefinitionBuilder},用于构造一个{@link RootBeanDefinition}
	 * 
	 * 
	 * @param beanClassName the class name for the bean that the definition is being created for
	 */
	public static BeanDefinitionBuilder rootBeanDefinition(String beanClassName) {
		return rootBeanDefinition(beanClassName, null);
	}

	/**
	 * Create a new {@code BeanDefinitionBuilder} used to construct a {@link RootBeanDefinition}.
	 * <p>
	 *  创建一个新的{@code BeanDefinitionBuilder},用于构造一个{@link RootBeanDefinition}
	 * 
	 * 
	 * @param beanClassName the class name for the bean that the definition is being created for
	 * @param factoryMethodName the name of the method to use to construct the bean instance
	 */
	public static BeanDefinitionBuilder rootBeanDefinition(String beanClassName, String factoryMethodName) {
		BeanDefinitionBuilder builder = new BeanDefinitionBuilder();
		builder.beanDefinition = new RootBeanDefinition();
		builder.beanDefinition.setBeanClassName(beanClassName);
		builder.beanDefinition.setFactoryMethodName(factoryMethodName);
		return builder;
	}

	/**
	 * Create a new {@code BeanDefinitionBuilder} used to construct a {@link ChildBeanDefinition}.
	 * <p>
	 *  创建一个新的{@code BeanDefinitionBuilder},用于构造一个{@link ChildBeanDefinition}
	 * 
	 * 
	 * @param parentName the name of the parent bean
	 */
	public static BeanDefinitionBuilder childBeanDefinition(String parentName) {
		BeanDefinitionBuilder builder = new BeanDefinitionBuilder();
		builder.beanDefinition = new ChildBeanDefinition(parentName);
		return builder;
	}


	/**
	 * The {@code BeanDefinition} instance we are creating.
	 * <p>
	 *  我们正在创建的{@code BeanDefinition}实例
	 * 
	 */
	private AbstractBeanDefinition beanDefinition;

	/**
	 * Our current position with respect to constructor args.
	 * <p>
	 *  我们目前在构造方面的立场
	 * 
	 */
	private int constructorArgIndex;


	/**
	 * Enforce the use of factory methods.
	 * <p>
	 *  强制使用工厂方法
	 * 
	 */
	private BeanDefinitionBuilder() {
	}

	/**
	 * Return the current BeanDefinition object in its raw (unvalidated) form.
	 * <p>
	 *  以原始(未验证)的形式返回当前的BeanDefinition对象
	 * 
	 * 
	 * @see #getBeanDefinition()
	 */
	public AbstractBeanDefinition getRawBeanDefinition() {
		return this.beanDefinition;
	}

	/**
	 * Validate and return the created BeanDefinition object.
	 * <p>
	 *  验证并返回创建的BeanDefinition对象
	 * 
	 */
	public AbstractBeanDefinition getBeanDefinition() {
		this.beanDefinition.validate();
		return this.beanDefinition;
	}


	/**
	 * Set the name of the parent definition of this bean definition.
	 * <p>
	 *  设置此bean定义的父定义的名称
	 * 
	 */
	public BeanDefinitionBuilder setParentName(String parentName) {
		this.beanDefinition.setParentName(parentName);
		return this;
	}

	/**
	 * Set the name of the factory method to use for this definition.
	 * <p>
	 *  设置用于此定义的工厂方法的名称
	 * 
	 */
	public BeanDefinitionBuilder setFactoryMethod(String factoryMethod) {
		this.beanDefinition.setFactoryMethodName(factoryMethod);
		return this;
	}

	/**
	 * Add an indexed constructor arg value. The current index is tracked internally
	 * and all additions are at the present point.
	 * <p>
	 * 添加索引构造函数arg值当前索引在内部进行跟踪,所有添加项目都在当前位置
	 * 
	 * 
	 * @deprecated since Spring 2.5, in favor of {@link #addConstructorArgValue}.
	 * This variant just remains around for Spring Security 2.x compatibility.
	 */
	@Deprecated
	public BeanDefinitionBuilder addConstructorArg(Object value) {
		return addConstructorArgValue(value);
	}

	/**
	 * Add an indexed constructor arg value. The current index is tracked internally
	 * and all additions are at the present point.
	 * <p>
	 *  添加索引构造函数arg值当前索引在内部进行跟踪,所有添加项目都在当前位置
	 * 
	 */
	public BeanDefinitionBuilder addConstructorArgValue(Object value) {
		this.beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(
				this.constructorArgIndex++, value);
		return this;
	}

	/**
	 * Add a reference to a named bean as a constructor arg.
	 * <p>
	 *  作为构造函数arg添加对命名bean的引用
	 * 
	 * 
	 * @see #addConstructorArgValue(Object)
	 */
	public BeanDefinitionBuilder addConstructorArgReference(String beanName) {
		this.beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(
				this.constructorArgIndex++, new RuntimeBeanReference(beanName));
		return this;
	}

	/**
	 * Add the supplied property value under the given name.
	 * <p>
	 *  在给定的名称下添加提供的属性值
	 * 
	 */
	public BeanDefinitionBuilder addPropertyValue(String name, Object value) {
		this.beanDefinition.getPropertyValues().add(name, value);
		return this;
	}

	/**
	 * Add a reference to the specified bean name under the property specified.
	 * <p>
	 *  在指定的属性下添加对指定bean名称的引用
	 * 
	 * 
	 * @param name the name of the property to add the reference to
	 * @param beanName the name of the bean being referenced
	 */
	public BeanDefinitionBuilder addPropertyReference(String name, String beanName) {
		this.beanDefinition.getPropertyValues().add(name, new RuntimeBeanReference(beanName));
		return this;
	}

	/**
	 * Set the init method for this definition.
	 * <p>
	 *  设置此定义的init方法
	 * 
	 */
	public BeanDefinitionBuilder setInitMethodName(String methodName) {
		this.beanDefinition.setInitMethodName(methodName);
		return this;
	}

	/**
	 * Set the destroy method for this definition.
	 * <p>
	 *  设置此定义的销毁方法
	 * 
	 */
	public BeanDefinitionBuilder setDestroyMethodName(String methodName) {
		this.beanDefinition.setDestroyMethodName(methodName);
		return this;
	}


	/**
	 * Set the scope of this definition.
	 * <p>
	 *  设置此定义的范围
	 * 
	 * 
	 * @see org.springframework.beans.factory.config.BeanDefinition#SCOPE_SINGLETON
	 * @see org.springframework.beans.factory.config.BeanDefinition#SCOPE_PROTOTYPE
	 */
	public BeanDefinitionBuilder setScope(String scope) {
		this.beanDefinition.setScope(scope);
		return this;
	}

	/**
	 * Set whether or not this definition is abstract.
	 * <p>
	 *  设定这个定义是否是抽象的
	 * 
	 */
	public BeanDefinitionBuilder setAbstract(boolean flag) {
		this.beanDefinition.setAbstract(flag);
		return this;
	}

	/**
	 * Set whether beans for this definition should be lazily initialized or not.
	 * <p>
	 *  设置此定义的bean是否应该被延迟初始化
	 * 
	 */
	public BeanDefinitionBuilder setLazyInit(boolean lazy) {
		this.beanDefinition.setLazyInit(lazy);
		return this;
	}

	/**
	 * Set the autowire mode for this definition.
	 * <p>
	 *  设置此定义的自动连接模式
	 * 
	 */
	public BeanDefinitionBuilder setAutowireMode(int autowireMode) {
		beanDefinition.setAutowireMode(autowireMode);
		return this;
	}

	/**
	 * Set the depency check mode for this definition.
	 * <p>
	 *  设置此定义的二级检查模式
	 * 
	 */
	public BeanDefinitionBuilder setDependencyCheck(int dependencyCheck) {
		beanDefinition.setDependencyCheck(dependencyCheck);
		return this;
	}

	/**
	 * Append the specified bean name to the list of beans that this definition
	 * depends on.
	 * <p>
	 * 将指定的bean名称附加到此定义所依赖的bean列表中
	 * 
	 */
	public BeanDefinitionBuilder addDependsOn(String beanName) {
		if (this.beanDefinition.getDependsOn() == null) {
			this.beanDefinition.setDependsOn(beanName);
		}
		else {
			String[] added = ObjectUtils.addObjectToArray(this.beanDefinition.getDependsOn(), beanName);
			this.beanDefinition.setDependsOn(added);
		}
		return this;
	}

	/**
	 * Set the role of this definition.
	 * <p>
	 *  设定这个定义的作用
	 */
	public BeanDefinitionBuilder setRole(int role) {
		this.beanDefinition.setRole(role);
		return this;
	}

}
