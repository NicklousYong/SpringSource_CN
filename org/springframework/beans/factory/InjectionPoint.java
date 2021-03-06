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

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;

import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;

/**
 * A simple descriptor for an injection point, pointing to a method/constructor
 * parameter or a field. Exposed by {@link UnsatisfiedDependencyException}.
 *
 * <p>
 *  一个注入点的简单描述符,指向一个方法/构造函数参数或一个字段曝光{@link UnsatisfiedDependencyException}
 * 
 * 
 * @author Juergen Hoeller
 * @since 4.3
 * @see UnsatisfiedDependencyException#getInjectionPoint()
 * @see org.springframework.beans.factory.config.DependencyDescriptor
 */
public class InjectionPoint {

	protected MethodParameter methodParameter;

	protected Field field;

	private volatile Annotation[] fieldAnnotations;


	/**
	 * Create an injection point descriptor for a method or constructor parameter.
	 * <p>
	 * 为方法或构造函数参数创建注入点描述符
	 * 
	 * 
	 * @param methodParameter the MethodParameter to wrap
	 */
	public InjectionPoint(MethodParameter methodParameter) {
		Assert.notNull(methodParameter, "MethodParameter must not be null");
		this.methodParameter = methodParameter;
	}

	/**
	 * Create an injection point descriptor for a field.
	 * <p>
	 *  创建一个字段的注入点描述符
	 * 
	 * 
	 * @param field the field to wrap
	 */
	public InjectionPoint(Field field) {
		Assert.notNull(field, "Field must not be null");
		this.field = field;
	}

	/**
	 * Copy constructor.
	 * <p>
	 *  复制构造函数
	 * 
	 * 
	 * @param original the original descriptor to create a copy from
	 */
	protected InjectionPoint(InjectionPoint original) {
		this.methodParameter = (original.methodParameter != null ?
				new MethodParameter(original.methodParameter) : null);
		this.field = original.field;
		this.fieldAnnotations = original.fieldAnnotations;
	}

	/**
	 * Just available for serialization purposes in subclasses.
	 * <p>
	 *  仅用于子类中的序列化目的
	 * 
	 */
	protected InjectionPoint() {
	}


	/**
	 * Return the wrapped MethodParameter, if any.
	 * <p>Note: Either MethodParameter or Field is available.
	 * <p>
	 *  返回包装的MethodParameter(如果有的话)注意：MethodParameter或Field可用
	 * 
	 * 
	 * @return the MethodParameter, or {@code null} if none
	 */
	public MethodParameter getMethodParameter() {
		return this.methodParameter;
	}

	/**
	 * Return the wrapped Field, if any.
	 * <p>Note: Either MethodParameter or Field is available.
	 * <p>
	 *  返回包装的字段(如果有)<p>注意：方法参数或字段可用
	 * 
	 * 
	 * @return the Field, or {@code null} if none
	 */
	public Field getField() {
		return this.field;
	}

	/**
	 * Obtain the annotations associated with the wrapped field or method/constructor parameter.
	 * <p>
	 *  获取与包装字段或方法/构造函数参数相关联的注释
	 * 
	 */
	public Annotation[] getAnnotations() {
		if (this.field != null) {
			if (this.fieldAnnotations == null) {
				this.fieldAnnotations = this.field.getAnnotations();
			}
			return this.fieldAnnotations;
		}
		else {
			return this.methodParameter.getParameterAnnotations();
		}
	}

	/**
	 * Return the type declared by the underlying field or method/constructor parameter,
	 * indicating the injection type.
	 * <p>
	 *  返回基础字段或方法/构造函数参数声明的类型,指示注入类型
	 * 
	 */
	public Class<?> getDeclaredType() {
		return (this.field != null ? this.field.getType() : this.methodParameter.getParameterType());
	}

	/**
	 * Returns the wrapped member, containing the injection point.
	 * <p>
	 *  返回包装成员,包含注入点
	 * 
	 * 
	 * @return the Field / Method / Constructor as Member
	 */
	public Member getMember() {
		return (this.field != null ? this.field : this.methodParameter.getMember());
	}

	/**
	 * Return the wrapped annotated element.
	 * <p>Note: In case of a method/constructor parameter, this exposes
	 * the annotations declared on the method or constructor itself
	 * (i.e. at the method/constructor level, not at the parameter level).
	 * Use {@link #getAnnotations()} to obtain parameter-level annotations in
	 * such a scenario, transparently with corresponding field annotations.
	 * <p>
	 * 返回包装的注释元素<p>注意：在方法/构造函数参数的情况下,这会公开方法或构造函数本身声明的注释(即方法/构造函数级别,而不是参数级别)使用{@link# getAnnotations()}在这种情况下
	 * 获取参数级注解,透明地使用相应的字段注释。
	 * 
	 * @return the Field / Method / Constructor as AnnotatedElement
	 */
	public AnnotatedElement getAnnotatedElement() {
		return (this.field != null ? this.field : this.methodParameter.getAnnotatedElement());
	}


	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (getClass() != other.getClass()) {
			return false;
		}
		InjectionPoint otherPoint = (InjectionPoint) other;
		return (this.field != null ? this.field.equals(otherPoint.field) :
				this.methodParameter.equals(otherPoint.methodParameter));
	}

	@Override
	public int hashCode() {
		return (this.field != null ? this.field.hashCode() : this.methodParameter.hashCode());
	}

	@Override
	public String toString() {
		return (this.field != null ? "field '" + this.field.getName() + "'" : this.methodParameter.toString());
	}

}
