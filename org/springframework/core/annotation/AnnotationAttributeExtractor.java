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

package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * An {@code AnnotationAttributeExtractor} is responsible for
 * {@linkplain #getAttributeValue extracting} annotation attribute values
 * from an underlying {@linkplain #getSource source} such as an
 * {@code Annotation} or a {@code Map}.
 *
 * <p>
 * {@code AnnotationAttributeExtractor}负责来自底层{@linkplain #getSource源}(例如{@code注释}或{@code Map})的{@linkplain #getAttributeValue提取}
 * 注释属性值。
 * 
 * 
 * @author Sam Brannen
 * @since 4.2
 * @param <S> the type of source supported by this extractor
 * @see SynthesizedAnnotationInvocationHandler
 */
interface AnnotationAttributeExtractor<S> {

	/**
	 * Get the type of annotation that this extractor extracts attribute
	 * values for.
	 * <p>
	 *  获取此提取器提取属性值的注释类型
	 * 
	 */
	Class<? extends Annotation> getAnnotationType();

	/**
	 * Get the element that is annotated with an annotation of the annotation
	 * type supported by this extractor.
	 * <p>
	 *  使用此提取器支持的注释类型的注释获取注释的元素
	 * 
	 * 
	 * @return the annotated element, or {@code null} if unknown
	 */
	Object getAnnotatedElement();

	/**
	 * Get the underlying source of annotation attributes.
	 * <p>
	 *  获取注释属性的基础来源
	 * 
	 */
	S getSource();

	/**
	 * Get the attribute value from the underlying {@linkplain #getSource source}
	 * that corresponds to the supplied attribute method.
	 * <p>
	 *  从与提供的属性方法对应的底层{@linkplain #getSource source}获取属性值
	 * 
	 * @param attributeMethod an attribute method from the annotation type
	 * supported by this extractor
	 * @return the value of the annotation attribute
	 */
	Object getAttributeValue(Method attributeMethod);

}
