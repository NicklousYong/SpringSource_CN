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
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.springframework.core.MethodParameter;

/**
 * A {@link MethodParameter} variant which synthesizes annotations that
 * declare attribute aliases via {@link AliasFor @AliasFor}.
 *
 * <p>
 *  一种{@link MethodParameter}变体,它通过{@link AliasFor @AliasFor}合成了声明属性别名的注释
 * 
 * 
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 4.2
 * @see AnnotationUtils#synthesizeAnnotation
 * @see AnnotationUtils#synthesizeAnnotationArray
 */
public class SynthesizingMethodParameter extends MethodParameter {

	/**
	 * Create a new {@code SynthesizingMethodParameter} for the given method,
	 * with nesting level 1.
	 * <p>
	 *  为给定的方法创建一个新的{@code SynthesizingMethodParameter},嵌套级别为1
	 * 
	 * 
	 * @param method the Method to specify a parameter for
	 * @param parameterIndex the index of the parameter: -1 for the method
	 * return type; 0 for the first method parameter; 1 for the second method
	 * parameter, etc.
	 */
	public SynthesizingMethodParameter(Method method, int parameterIndex) {
		super(method, parameterIndex);
	}

	/**
	 * Create a new {@code SynthesizingMethodParameter} for the given method.
	 * <p>
	 * 为给定的方法创建一个新的{@code SynthesizingMethodParameter}
	 * 
	 * 
	 * @param method the Method to specify a parameter for
	 * @param parameterIndex the index of the parameter: -1 for the method
	 * return type; 0 for the first method parameter; 1 for the second method
	 * parameter, etc.
	 * @param nestingLevel the nesting level of the target type
	 * (typically 1; e.g. in case of a List of Lists, 1 would indicate the
	 * nested List, whereas 2 would indicate the element of the nested List)
	 */
	public SynthesizingMethodParameter(Method method, int parameterIndex, int nestingLevel) {
		super(method, parameterIndex, nestingLevel);
	}

	/**
	 * Create a new {@code SynthesizingMethodParameter} for the given constructor,
	 * with nesting level 1.
	 * <p>
	 *  为给定的构造函数创建一个新的{@code SynthesizingMethodParameter},嵌套级别为1
	 * 
	 * 
	 * @param constructor the Constructor to specify a parameter for
	 * @param parameterIndex the index of the parameter
	 */
	public SynthesizingMethodParameter(Constructor<?> constructor, int parameterIndex) {
		super(constructor, parameterIndex);
	}

	/**
	 * Create a new {@code SynthesizingMethodParameter} for the given constructor.
	 * <p>
	 *  为给定的构造函数创建一个新的{@code SynthesizingMethodParameter}
	 * 
	 * 
	 * @param constructor the Constructor to specify a parameter for
	 * @param parameterIndex the index of the parameter
	 * @param nestingLevel the nesting level of the target type
	 * (typically 1; e.g. in case of a List of Lists, 1 would indicate the
	 * nested List, whereas 2 would indicate the element of the nested List)
	 */
	public SynthesizingMethodParameter(Constructor<?> constructor, int parameterIndex, int nestingLevel) {
		super(constructor, parameterIndex, nestingLevel);
	}

	/**
	 * Copy constructor, resulting in an independent {@code SynthesizingMethodParameter}
	 * based on the same metadata and cache state that the original object was in.
	 * <p>
	 *  复制构造函数,根据原始对象所在的元数据和缓存状态,导致独立的{@code SynthesizingMethodParameter}
	 * 
	 * @param original the original SynthesizingMethodParameter object to copy from
	 */
	protected SynthesizingMethodParameter(SynthesizingMethodParameter original) {
		super(original);
	}


	@Override
	protected <A extends Annotation> A adaptAnnotation(A annotation) {
		return AnnotationUtils.synthesizeAnnotation(annotation, getAnnotatedElement());
	}

	@Override
	protected Annotation[] adaptAnnotationArray(Annotation[] annotations) {
		return AnnotationUtils.synthesizeAnnotationArray(annotations, getAnnotatedElement());
	}


	@Override
	public SynthesizingMethodParameter clone() {
		return new SynthesizingMethodParameter(this);
	}

}
