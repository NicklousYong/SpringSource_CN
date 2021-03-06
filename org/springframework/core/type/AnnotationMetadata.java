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

package org.springframework.core.type;

import java.util.Set;

/**
 * Interface that defines abstract access to the annotations of a specific
 * class, in a form that does not require that class to be loaded yet.
 *
 * <p>
 *  定义对特定类的注释的抽象访问的接口,以不需要加载该类的形式
 * 
 * 
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @author Phillip Webb
 * @author Sam Brannen
 * @since 2.5
 * @see StandardAnnotationMetadata
 * @see org.springframework.core.type.classreading.MetadataReader#getAnnotationMetadata()
 * @see AnnotatedTypeMetadata
 */
public interface AnnotationMetadata extends ClassMetadata, AnnotatedTypeMetadata {

	/**
	 * Get the fully qualified class names of all annotation types that
	 * are <em>present</em> on the underlying class.
	 * <p>
	 * 获取基础类上<em> </em>的所有注释类型的完全限定类名
	 * 
	 * 
	 * @return the annotation type names
	 */
	Set<String> getAnnotationTypes();

	/**
	 * Get the fully qualified class names of all meta-annotation types that
	 * are <em>present</em> on the given annotation type on the underlying class.
	 * <p>
	 *  在基础类上的给定注释类型上获取<em>存在</em>的所有元注释类型的完全限定类名
	 * 
	 * 
	 * @param annotationName the fully qualified class name of the meta-annotation
	 * type to look for
	 * @return the meta-annotation type names
	 */
	Set<String> getMetaAnnotationTypes(String annotationName);

	/**
	 * Determine whether an annotation of the given type is <em>present</em> on
	 * the underlying class.
	 * <p>
	 *  确定给定类型的注释是否在基础类上是<em>存在</em>
	 * 
	 * 
	 * @param annotationName the fully qualified class name of the annotation
	 * type to look for
	 * @return {@code true} if a matching annotation is present
	 */
	boolean hasAnnotation(String annotationName);

	/**
	 * Determine whether the underlying class has an annotation that is itself
	 * annotated with the meta-annotation of the given type.
	 * <p>
	 *  确定底层类是否具有本身使用给定类型的元注释注释的注释
	 * 
	 * 
	 * @param metaAnnotationName the fully qualified class name of the
	 * meta-annotation type to look for
	 * @return {@code true} if a matching meta-annotation is present
	 */
	boolean hasMetaAnnotation(String metaAnnotationName);

	/**
	 * Determine whether the underlying class has any methods that are
	 * annotated (or meta-annotated) with the given annotation type.
	 * <p>
	 *  确定底层类是否具有使用给定注释类型进行注释(或元注释)的任何方法
	 * 
	 * 
	 * @param annotationName the fully qualified class name of the annotation
	 * type to look for
	 */
	boolean hasAnnotatedMethods(String annotationName);

	/**
	 * Retrieve the method metadata for all methods that are annotated
	 * (or meta-annotated) with the given annotation type.
	 * <p>For any returned method, {@link MethodMetadata#isAnnotated} will
	 * return {@code true} for the given annotation type.
	 * <p>
	 * 检索所有使用给定注释类型(或元注释)的方法的方法元数据对于任何返回的方法,{@link MethodMetadata#isAnnotated}将为给定的注释类型返回{@code true}
	 * 
	 * @param annotationName the fully qualified class name of the annotation
	 * type to look for
	 * @return a set of {@link MethodMetadata} for methods that have a matching
	 * annotation. The return value will be an empty set if no methods match
	 * the annotation type.
	 */
	Set<MethodMetadata> getAnnotatedMethods(String annotationName);

}
