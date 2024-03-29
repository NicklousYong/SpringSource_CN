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

package org.springframework.context.annotation;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Interface to be implemented by types that register additional bean definitions when
 * processing @{@link Configuration} classes. Useful when operating at the bean definition
 * level (as opposed to {@code @Bean} method/instance level) is desired or necessary.
 *
 * <p>Along with {@code @Configuration} and {@link ImportSelector}, classes of this type
 * may be provided to the @{@link Import} annotation (or may also be returned from an
 * {@code ImportSelector}).
 *
 * <p>An {@link ImportBeanDefinitionRegistrar} may implement any of the following
 * {@link org.springframework.beans.factory.Aware Aware} interfaces, and their respective
 * methods will be called prior to {@link #registerBeanDefinitions}:
 * <ul>
 * <li>{@link org.springframework.context.EnvironmentAware EnvironmentAware}</li>
 * <li>{@link org.springframework.beans.factory.BeanFactoryAware BeanFactoryAware}
 * <li>{@link org.springframework.beans.factory.BeanClassLoaderAware BeanClassLoaderAware}
 * <li>{@link org.springframework.context.ResourceLoaderAware ResourceLoaderAware}
 * </ul>
 *
 * <p>See implementations and associated unit tests for usage examples.
 *
 * <p>
 * 在处理@ {@ link Configuration}类时注册其他bean定义的类型实现的接口是在bean定义级别(而不是{@code @Bean}方法/实例级别)上运行时有用的)是有用的
 * 
 *  <p>除了{@code @Configuration}和{@link ImportSelector}之外,这种类型的类可能会提供给@ {@ link Import}注释(或者也可以从{@code ImportSelector}
 * 返回)。
 * 
 *  <p> {@link ImportBeanDefinitionRegistrar}可以实现以下任何{@link orgspringframeworkbeansfactoryAware Aware}接口
 * ,并且它们各自的方法将在{@link #registerBeanDefinitions}之前调用：。
 * <ul>
 * <li> {@ link orgspringframeworkcontextEnvironmentAware EnvironmentAware} </li> <li> {@ link orgspringframeworkbeansfactoryBeanFactoryAware BeanFactoryAware}
 *  <li> {@ link orgspringframeworkbeansfactoryBeanClassLoaderAware BeanClassLoaderAware} <li> {@ link orgspringframeworkcontextResourceLoaderAware ResourceLoaderAware}
 * 
 * @author Chris Beams
 * @since 3.1
 * @see Import
 * @see ImportSelector
 * @see Configuration
 */
public interface ImportBeanDefinitionRegistrar {

	/**
	 * Register bean definitions as necessary based on the given annotation metadata of
	 * the importing {@code @Configuration} class.
	 * <p>Note that {@link BeanDefinitionRegistryPostProcessor} types may <em>not</em> be
	 * registered here, due to lifecycle constraints related to {@code @Configuration}
	 * class processing.
	 * <p>
	 * 。
	 * </ul>
	 * 
	 *  <p>请参阅实施和相关单元测试的使用示例
	 * 
	 * 
	 * @param importingClassMetadata annotation metadata of the importing class
	 * @param registry current bean definition registry
	 */
	public void registerBeanDefinitions(
			AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry);

}
