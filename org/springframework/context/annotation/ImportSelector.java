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

import org.springframework.core.type.AnnotationMetadata;

/**
 * Interface to be implemented by types that determine which @{@link Configuration}
 * class(es) should be imported based on a given selection criteria, usually one or more
 * annotation attributes.
 *
 * <p>An {@link ImportSelector} may implement any of the following
 * {@link org.springframework.beans.factory.Aware Aware} interfaces, and their respective
 * methods will be called prior to {@link #selectImports}:
 * <ul>
 * <li>{@link org.springframework.context.EnvironmentAware EnvironmentAware}</li>
 * <li>{@link org.springframework.beans.factory.BeanFactoryAware BeanFactoryAware}</li>
 * <li>{@link org.springframework.beans.factory.BeanClassLoaderAware BeanClassLoaderAware}</li>
 * <li>{@link org.springframework.context.ResourceLoaderAware ResourceLoaderAware}</li>
 * </ul>
 *
 * <p>ImportSelectors are usually processed in the same way as regular {@code @Import}
 * annotations, however, it is also possible to defer selection of imports until all
 * {@code @Configuration} classes have been processed (see {@link DeferredImportSelector}
 * for details).
 *
 * <p>
 *  根据确定应该根据给定选择标准导入哪个@ {@ link Configuration}类的接口,通常是一个或多个注释属性
 * 
 * <p> {@link ImportSelector}可以实现以下任何{@link orgspringframeworkbeansfactoryAware Aware}界面,并且它们各自的方法将在{@link #selectImports}
 * 之前调用：。
 * <ul>
 *  <li> {@ link orgspringframeworkcontextEnvironmentAware EnvironmentAware} </li> <li> {@ link orgspringframeworkbeansfactoryBeanFactoryAware BeanFactoryAware}
 *  </li> <li> {@ link orgspringframeworkbeansfactoryBeanClassLoaderAware BeanClassLoaderAware} </li> <li>
 *  {@ link orgspringframeworkcontextResourceLoaderAware ResourceLoaderAware} LI>。
 * 
 * @author Chris Beams
 * @since 3.1
 * @see DeferredImportSelector
 * @see Import
 * @see ImportBeanDefinitionRegistrar
 * @see Configuration
 */
public interface ImportSelector {

	/**
	 * Select and return the names of which class(es) should be imported based on
	 * the {@link AnnotationMetadata} of the importing @{@link Configuration} class.
	 * <p>
	 * </ul>
	 * 
	 * 通常使用与常规{@code @Import}注释相同的方式处理ImportSelectors,但是,直到所有{@code @Configuration}类已被处理,也可以推迟导入选择(请参见{@link DeferredImportSelector }
	 * 详细资料)。
	 */
	String[] selectImports(AnnotationMetadata importingClassMetadata);

}
