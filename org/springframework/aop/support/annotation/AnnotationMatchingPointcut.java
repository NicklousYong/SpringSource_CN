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

package org.springframework.aop.support.annotation;

import java.lang.annotation.Annotation;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * Simple Pointcut that looks for a specific Java 5 annotation
 * being present on a {@link #forClassAnnotation class} or
 * {@link #forMethodAnnotation method}.
 *
 * <p>
 *  在{@link #forClassAnnotation class}或{@link #forMethodAnnotation方法}中存在的查找特定Java 5注释的简单切入点
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.0
 * @see AnnotationClassFilter
 * @see AnnotationMethodMatcher
 */
public class AnnotationMatchingPointcut implements Pointcut {

	private final ClassFilter classFilter;

	private final MethodMatcher methodMatcher;


	/**
	 * Create a new AnnotationMatchingPointcut for the given annotation type.
	 * <p>
	 *  为给定的注释类型创建一个新的AnnotationMatchingPointcut
	 * 
	 * 
	 * @param classAnnotationType the annotation type to look for at the class level
	 */
	public AnnotationMatchingPointcut(Class<? extends Annotation> classAnnotationType) {
		this.classFilter = new AnnotationClassFilter(classAnnotationType);
		this.methodMatcher = MethodMatcher.TRUE;
	}

	/**
	 * Create a new AnnotationMatchingPointcut for the given annotation type.
	 * <p>
	 * 为给定的注释类型创建一个新的AnnotationMatchingPointcut
	 * 
	 * 
	 * @param classAnnotationType the annotation type to look for at the class level
	 * @param checkInherited whether to explicitly check the superclasses and
	 * interfaces for the annotation type as well (even if the annotation type
	 * is not marked as inherited itself)
	 */
	public AnnotationMatchingPointcut(Class<? extends Annotation> classAnnotationType, boolean checkInherited) {
		this.classFilter = new AnnotationClassFilter(classAnnotationType, checkInherited);
		this.methodMatcher = MethodMatcher.TRUE;
	}

	/**
	 * Create a new AnnotationMatchingPointcut for the given annotation type.
	 * <p>
	 *  为给定的注释类型创建一个新的AnnotationMatchingPointcut
	 * 
	 * 
	 * @param classAnnotationType the annotation type to look for at the class level
	 * (can be {@code null})
	 * @param methodAnnotationType the annotation type to look for at the method level
	 * (can be {@code null})
	 */
	public AnnotationMatchingPointcut(
			Class<? extends Annotation> classAnnotationType, Class<? extends Annotation> methodAnnotationType) {

		Assert.isTrue((classAnnotationType != null || methodAnnotationType != null),
				"Either Class annotation type or Method annotation type needs to be specified (or both)");

		if (classAnnotationType != null) {
			this.classFilter = new AnnotationClassFilter(classAnnotationType);
		}
		else {
			this.classFilter = ClassFilter.TRUE;
		}

		if (methodAnnotationType != null) {
			this.methodMatcher = new AnnotationMethodMatcher(methodAnnotationType);
		}
		else {
			this.methodMatcher = MethodMatcher.TRUE;
		}
	}


	@Override
	public ClassFilter getClassFilter() {
		return this.classFilter;
	}

	@Override
	public MethodMatcher getMethodMatcher() {
		return this.methodMatcher;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof AnnotationMatchingPointcut)) {
			return false;
		}
		AnnotationMatchingPointcut that = (AnnotationMatchingPointcut) other;
		return ObjectUtils.nullSafeEquals(that.classFilter, this.classFilter) &&
				ObjectUtils.nullSafeEquals(that.methodMatcher, this.methodMatcher);
	}

	@Override
	public int hashCode() {
		int code = 17;
		if (this.classFilter != null) {
			code = 37 * code + this.classFilter.hashCode();
		}
		if (this.methodMatcher != null) {
			code = 37 * code + this.methodMatcher.hashCode();
		}
		return code;
	}

	@Override
	public String toString() {
		return "AnnotationMatchingPointcut: " + this.classFilter + ", " +this.methodMatcher;
	}


	/**
	 * Factory method for an AnnotationMatchingPointcut that matches
	 * for the specified annotation at the class level.
	 * <p>
	 *  在类级别与指定注释匹配的AnnotationMatchingPointcut的工厂方法
	 * 
	 * 
	 * @param annotationType the annotation type to look for at the class level
	 * @return the corresponding AnnotationMatchingPointcut
	 */
	public static AnnotationMatchingPointcut forClassAnnotation(Class<? extends Annotation> annotationType) {
		Assert.notNull(annotationType, "Annotation type must not be null");
		return new AnnotationMatchingPointcut(annotationType);
	}

	/**
	 * Factory method for an AnnotationMatchingPointcut that matches
	 * for the specified annotation at the method level.
	 * <p>
	 *  用于在方法级别匹配指定注释的AnnotationMatchingPointcut的工厂方法
	 * 
	 * @param annotationType the annotation type to look for at the method level
	 * @return the corresponding AnnotationMatchingPointcut
	 */
	public static AnnotationMatchingPointcut forMethodAnnotation(Class<? extends Annotation> annotationType) {
		Assert.notNull(annotationType, "Annotation type must not be null");
		return new AnnotationMatchingPointcut(null, annotationType);
	}

}
