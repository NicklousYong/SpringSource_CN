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

package org.springframework.beans.factory;

import org.springframework.beans.BeansException;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * Exception thrown when a {@code BeanFactory} is asked for a bean instance for which it
 * cannot find a definition. This may point to a non-existing bean, a non-unique bean,
 * or a manually registered singleton instance without an associated bean definition.
 *
 * <p>
 * 当一个{@code BeanFactory}被要求找不到定义的bean实例时抛出异常这可能指向一个不存在的bean,一个非唯一的bean或者没有关联的bean定义的手动注册的singleton实例
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see BeanFactory#getBean(String)
 * @see BeanFactory#getBean(Class)
 * @see NoUniqueBeanDefinitionException
 */
@SuppressWarnings("serial")
public class NoSuchBeanDefinitionException extends BeansException {

	/** Name of the missing bean */
	private String beanName;

	/** Required type of the missing bean */
	private Class<?> beanType;


	/**
	 * Create a new {@code NoSuchBeanDefinitionException}.
	 * <p>
	 *  创建一个新的{@code NoSuchBeanDefinitionException}
	 * 
	 * 
	 * @param name the name of the missing bean
	 */
	public NoSuchBeanDefinitionException(String name) {
		super("No bean named '" + name + "' is defined");
		this.beanName = name;
	}

	/**
	 * Create a new {@code NoSuchBeanDefinitionException}.
	 * <p>
	 *  创建一个新的{@code NoSuchBeanDefinitionException}
	 * 
	 * 
	 * @param name the name of the missing bean
	 * @param message detailed message describing the problem
	 */
	public NoSuchBeanDefinitionException(String name, String message) {
		super("No bean named '" + name + "' is defined: " + message);
		this.beanName = name;
	}

	/**
	 * Create a new {@code NoSuchBeanDefinitionException}.
	 * <p>
	 *  创建一个新的{@code NoSuchBeanDefinitionException}
	 * 
	 * 
	 * @param type required type of the missing bean
	 */
	public NoSuchBeanDefinitionException(Class<?> type) {
		super("No qualifying bean of type [" + type.getName() + "] is defined");
		this.beanType = type;
	}

	/**
	 * Create a new {@code NoSuchBeanDefinitionException}.
	 * <p>
	 *  创建一个新的{@code NoSuchBeanDefinitionException}
	 * 
	 * 
	 * @param type required type of the missing bean
	 * @param message detailed message describing the problem
	 */
	public NoSuchBeanDefinitionException(Class<?> type, String message) {
		super("No qualifying bean of type [" + ClassUtils.getQualifiedName(type) + "] is defined: " + message);
		this.beanType = type;
	}

	/**
	 * Create a new {@code NoSuchBeanDefinitionException}.
	 * <p>
	 *  创建一个新的{@code NoSuchBeanDefinitionException}
	 * 
	 * 
	 * @param type required type of the missing bean
	 * @param dependencyDescription a description of the originating dependency
	 * @param message detailed message describing the problem
	 */
	public NoSuchBeanDefinitionException(Class<?> type, String dependencyDescription, String message) {
		super("No qualifying bean" + (!StringUtils.hasLength(dependencyDescription) ?
				" of type [" + ClassUtils.getQualifiedName(type) + "]" : "") + " found for dependency" +
				(StringUtils.hasLength(dependencyDescription) ? " [" + dependencyDescription + "]" : "") +
				": " + message);
		this.beanType = type;
	}


	/**
	 * Return the name of the missing bean, if it was a lookup <em>by name</em> that failed.
	 * <p>
	 *  返回丢失的bean的名称,如果它是按名称</em>查找失败的
	 * 
	 */
	public String getBeanName() {
		return this.beanName;
	}

	/**
	 * Return the required type of the missing bean, if it was a lookup <em>by type</em> that failed.
	 * <p>
	 *  返回缺少的bean的必需类型,如果它是按类型</em>查找失败的
	 * 
	 */
	public Class<?> getBeanType() {
		return this.beanType;
	}

	/**
	 * Return the number of beans found when only one matching bean was expected.
	 * For a regular NoSuchBeanDefinitionException, this will always be 0.
	 * <p>
	 * 返回当预期只有一个匹配的bean时发现的bean的数量对于常规的NoSuchBeanDefinitionException,这将始终为0
	 * 
	 * @see NoUniqueBeanDefinitionException
	 */
	public int getNumberOfBeansFound() {
		return 0;
	}

}
