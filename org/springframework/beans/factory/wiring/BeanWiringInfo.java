/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2012 the original author or authors.
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

package org.springframework.beans.factory.wiring;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.util.Assert;

/**
 * Holder for bean wiring metadata information about a particular class. Used in
 * conjunction with the {@link org.springframework.beans.factory.annotation.Configurable}
 * annotation and the AspectJ {@code AnnotationBeanConfigurerAspect}.
 *
 * <p>
 * 关于特定类的bean连接元数据信息的持有人与{@link orgspringframeworkbeansfactoryannotationConfigurable}注释和AspectJ {@code AnnotationBeanConfigurerAspect}
 * 结合使用。
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 * @see BeanWiringInfoResolver
 * @see org.springframework.beans.factory.config.AutowireCapableBeanFactory
 * @see org.springframework.beans.factory.annotation.Configurable
 */
public class BeanWiringInfo {

	/**
	 * Constant that indicates autowiring bean properties by name.
	 * <p>
	 *  常数表示按名称自动连线bean属性
	 * 
	 * 
	 * @see #BeanWiringInfo(int, boolean)
	 * @see org.springframework.beans.factory.config.AutowireCapableBeanFactory#AUTOWIRE_BY_NAME
	 */
	public static final int AUTOWIRE_BY_NAME = AutowireCapableBeanFactory.AUTOWIRE_BY_NAME;

	/**
	 * Constant that indicates autowiring bean properties by type.
	 * <p>
	 *  指示类型自动连接bean属性的常数
	 * 
	 * 
	 * @see #BeanWiringInfo(int, boolean)
	 * @see org.springframework.beans.factory.config.AutowireCapableBeanFactory#AUTOWIRE_BY_TYPE
	 */
	public static final int AUTOWIRE_BY_TYPE = AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE;


	private String beanName = null;

	private boolean isDefaultBeanName = false;

	private int autowireMode = AutowireCapableBeanFactory.AUTOWIRE_NO;

	private boolean dependencyCheck = false;


	/**
	 * Create a default BeanWiringInfo that suggests plain initialization of
	 * factory and post-processor callbacks that the bean class may expect.
	 * <p>
	 *  创建一个默认的BeanWiringInfo,建议bean类可能期望的工厂和后处理器回调的初始化
	 * 
	 */
	public BeanWiringInfo() {
	}

	/**
	 * Create a new BeanWiringInfo that points to the given bean name.
	 * <p>
	 *  创建一个指向给定bean名称的BeanWiringInfo
	 * 
	 * 
	 * @param beanName the name of the bean definition to take the property values from
	 * @throws IllegalArgumentException if the supplied beanName is {@code null},
	 * is empty, or consists wholly of whitespace
	 */
	public BeanWiringInfo(String beanName) {
		this(beanName, false);
	}

	/**
	 * Create a new BeanWiringInfo that points to the given bean name.
	 * <p>
	 *  创建一个指向给定bean名称的BeanWiringInfo
	 * 
	 * 
	 * @param beanName the name of the bean definition to take the property values from
	 * @param isDefaultBeanName whether the given bean name is a suggested
	 * default bean name, not necessarily matching an actual bean definition
	 * @throws IllegalArgumentException if the supplied beanName is {@code null},
	 * is empty, or consists wholly of whitespace
	 */
	public BeanWiringInfo(String beanName, boolean isDefaultBeanName) {
		Assert.hasText(beanName, "'beanName' must not be empty");
		this.beanName = beanName;
		this.isDefaultBeanName = isDefaultBeanName;
	}

	/**
	 * Create a new BeanWiringInfo that indicates autowiring.
	 * <p>
	 *  创建一个新的BeanWiringInfo,指示自动布线
	 * 
	 * 
	 * @param autowireMode one of the constants {@link #AUTOWIRE_BY_NAME} /
	 * {@link #AUTOWIRE_BY_TYPE}
	 * @param dependencyCheck whether to perform a dependency check for object
	 * references in the bean instance (after autowiring)
	 * @throws IllegalArgumentException if the supplied {@code autowireMode}
	 * is not one of the allowed values
	 * @see #AUTOWIRE_BY_NAME
	 * @see #AUTOWIRE_BY_TYPE
	 */
	public BeanWiringInfo(int autowireMode, boolean dependencyCheck) {
		if (autowireMode != AUTOWIRE_BY_NAME && autowireMode != AUTOWIRE_BY_TYPE) {
			throw new IllegalArgumentException("Only constants AUTOWIRE_BY_NAME and AUTOWIRE_BY_TYPE supported");
		}
		this.autowireMode = autowireMode;
		this.dependencyCheck = dependencyCheck;
	}


	/**
	 * Return whether this BeanWiringInfo indicates autowiring.
	 * <p>
	 *  返回BeanWiringInfo是否指示自动连线
	 * 
	 */
	public boolean indicatesAutowiring() {
		return (this.beanName == null);
	}

	/**
	 * Return the specific bean name that this BeanWiringInfo points to, if any.
	 * <p>
	 * 返回BeanWiringInfo指向的特定bean名称,如果有的话
	 * 
	 */
	public String getBeanName() {
		return this.beanName;
	}

	/**
	 * Return whether the specific bean name is a suggested default bean name,
	 * not necessarily matching an actual bean definition in the factory.
	 * <p>
	 *  返回特定bean名称是否是建议的默认bean名称,而不一定与工厂中的实际bean定义相匹配
	 * 
	 */
	public boolean isDefaultBeanName() {
		return this.isDefaultBeanName;
	}

	/**
	 * Return one of the constants {@link #AUTOWIRE_BY_NAME} /
	 * {@link #AUTOWIRE_BY_TYPE}, if autowiring is indicated.
	 * <p>
	 *  返回一个常量{@link #AUTOWIRE_BY_NAME} / {@link #AUTOWIRE_BY_TYPE},如果指定了自动连线
	 * 
	 */
	public int getAutowireMode() {
		return this.autowireMode;
	}

	/**
	 * Return whether to perform a dependency check for object references
	 * in the bean instance (after autowiring).
	 * <p>
	 *  返回是否对bean实例中的对象引用执行依赖性检查(自动连线后)
	 */
	public boolean getDependencyCheck() {
		return this.dependencyCheck;
	}

}
