/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2013 the original author or authors.
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

package org.springframework.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;

/**
 * Strategy interface for creating {@link BeanInfo} instances for Spring beans.
 * Can be used to plug in custom bean property resolution strategies (e.g. for other
 * languages on the JVM) or more efficient {@link BeanInfo} retrieval algorithms.
 *
 * <p>BeanInfoFactories are instantiated by the {@link CachedIntrospectionResults},
 * by using the {@link org.springframework.core.io.support.SpringFactoriesLoader}
 * utility class.
 *
 * When a {@link BeanInfo} is to be created, the {@code CachedIntrospectionResults}
 * will iterate through the discovered factories, calling {@link #getBeanInfo(Class)}
 * on each one. If {@code null} is returned, the next factory will be queried.
 * If none of the factories support the class, a standard {@link BeanInfo} will be
 * created as a default.
 *
 * <p>Note that the {@link org.springframework.core.io.support.SpringFactoriesLoader}
 * sorts the {@code BeanInfoFactory} instances by
 * {@link org.springframework.core.annotation.Order @Order}, so that ones with a
 * higher precedence come first.
 *
 * <p>
 * 用于为Spring bean创建{@link BeanInfo}实例的策略界面可用于插入自定义bean属性解析策略(例如,对于JVM上的其他语言)或更高效的{@link BeanInfo}检索算法
 * 
 *  <p> BeanInfoFactories由{@link CachedIntrospectionResults}实例化,通过使用{@link orgspringframeworkcoreiosupportSpringFactoriesLoader}
 * 实用程序类。
 * 
 *  当{@link BeanInfo}被创建时,{@code CachedIntrospectionResults}将遍历发现的工厂,每个都调用{@link #getBeanInfo(Class)}如果返
 * 回{@code null},下一个工厂将被查询如果没有一个工厂支持该类,则将创建一个标准的{@link BeanInfo}作为默认值。
 * 
 * @author Arjen Poutsma
 * @since 3.2
 * @see CachedIntrospectionResults
 * @see org.springframework.core.io.support.SpringFactoriesLoader
 */
public interface BeanInfoFactory {

	/**
	 * Return the bean info for the given class, if supported.
	 * <p>
	 * 
	 * <p>请注意,{@link orgspringframeworkcoreiosupportSpringFactoriesLoader}通过{@link orgspringframeworkcoreannotationOrder @Order}
	 * 对{@code BeanInfoFactory}实例进行排序,以便优先级更高的优先级先于。
	 * 
	 * 
	 * @param beanClass the bean class
	 * @return the BeanInfo, or {@code null} if the given class is not supported
	 * @throws IntrospectionException in case of exceptions
	 */
	BeanInfo getBeanInfo(Class<?> beanClass) throws IntrospectionException;

}
