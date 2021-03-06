/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2014 the original author or authors.
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

package org.springframework.core.convert;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.core.GenericTypeResolver;
import org.springframework.core.MethodParameter;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * A description of a JavaBeans Property that allows us to avoid a dependency on
 * {@code java.beans.PropertyDescriptor}. The {@code java.beans} package
 * is not available in a number of environments (e.g. Android, Java ME), so this is
 * desirable for portability of Spring's core conversion facility.
 *
 * <p>Used to build a TypeDescriptor from a property location.
 * The built TypeDescriptor can then be used to convert from/to the property type.
 *
 * <p>
 * JavaBeans属性的描述,允许我们避免依赖于{@code javabeansPropertyDescriptor} {@code javabeans}包在许多环境(例如Android,Java ME
 * )中不可用,因此这对Spring的可移植性是可取的核心转换设施。
 * 
 *  <p>用于从属性位置构建一个TypeDescriptor然后可以使用构建的TypeDescriptor将其从/转换为属性类型
 * 
 * 
 * @author Keith Donald
 * @author Phillip Webb
 * @since 3.1
 * @see TypeDescriptor#TypeDescriptor(Property)
 * @see TypeDescriptor#nested(Property, int)
 */
public final class Property {

	private static Map<Property, Annotation[]> annotationCache =
			new ConcurrentReferenceHashMap<Property, Annotation[]>();

	private final Class<?> objectType;

	private final Method readMethod;

	private final Method writeMethod;

	private final String name;

	private final MethodParameter methodParameter;

	private Annotation[] annotations;


	public Property(Class<?> objectType, Method readMethod, Method writeMethod) {
		this(objectType, readMethod, writeMethod, null);
	}

	public Property(Class<?> objectType, Method readMethod, Method writeMethod, String name) {
		this.objectType = objectType;
		this.readMethod = readMethod;
		this.writeMethod = writeMethod;
		this.methodParameter = resolveMethodParameter();
		this.name = (name == null ? resolveName() : name);
	}


	/**
	 * The object declaring this property, either directly or in a superclass the object extends.
	 * <p>
	 *  对象声明该属性,直接或超类对象扩展
	 * 
	 */
	public Class<?> getObjectType() {
		return this.objectType;
	}

	/**
	 * The name of the property: e.g. 'foo'
	 * <p>
	 *  属性的名称：eg'foo'
	 * 
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * The property type: e.g. {@code java.lang.String}
	 * <p>
	 *  属性类型：例如{@code javalangString}
	 * 
	 */
	public Class<?> getType() {
		return this.methodParameter.getParameterType();
	}

	/**
	 * The property getter method: e.g. {@code getFoo()}
	 * <p>
	 *  属性getter方法：例如{@code getFoo()}
	 * 
	 */
	public Method getReadMethod() {
		return this.readMethod;
	}

	/**
	 * The property setter method: e.g. {@code setFoo(String)}
	 * <p>
	 *  属性设置器方法：例如{@code setFoo(String)}
	 */
	public Method getWriteMethod() {
		return this.writeMethod;
	}


	// package private

	MethodParameter getMethodParameter() {
		return this.methodParameter;
	}

	Annotation[] getAnnotations() {
		if (this.annotations == null) {
			this.annotations = resolveAnnotations();
		}
		return this.annotations;
	}


	// internal helpers

	private String resolveName() {
		if (this.readMethod != null) {
			int index = this.readMethod.getName().indexOf("get");
			if (index != -1) {
				index += 3;
			}
			else {
				index = this.readMethod.getName().indexOf("is");
				if (index == -1) {
					throw new IllegalArgumentException("Not a getter method");
				}
				index += 2;
			}
			return StringUtils.uncapitalize(this.readMethod.getName().substring(index));
		}
		else {
			int index = this.writeMethod.getName().indexOf("set") + 3;
			if (index == -1) {
				throw new IllegalArgumentException("Not a setter method");
			}
			return StringUtils.uncapitalize(this.writeMethod.getName().substring(index));
		}
	}

	private MethodParameter resolveMethodParameter() {
		MethodParameter read = resolveReadMethodParameter();
		MethodParameter write = resolveWriteMethodParameter();
		if (write == null) {
			if (read == null) {
				throw new IllegalStateException("Property is neither readable nor writeable");
			}
			return read;
		}
		if (read != null) {
			Class<?> readType = read.getParameterType();
			Class<?> writeType = write.getParameterType();
			if (!writeType.equals(readType) && writeType.isAssignableFrom(readType)) {
				return read;
			}
		}
		return write;
	}

	private MethodParameter resolveReadMethodParameter() {
		if (getReadMethod() == null) {
			return null;
		}
		return resolveParameterType(new MethodParameter(getReadMethod(), -1));
	}

	private MethodParameter resolveWriteMethodParameter() {
		if (getWriteMethod() == null) {
			return null;
		}
		return resolveParameterType(new MethodParameter(getWriteMethod(), 0));
	}

	private MethodParameter resolveParameterType(MethodParameter parameter) {
		// needed to resolve generic property types that parameterized by sub-classes e.g. T getFoo();
		GenericTypeResolver.resolveParameterType(parameter, getObjectType());
		return parameter;
	}

	private Annotation[] resolveAnnotations() {
		Annotation[] annotations = annotationCache.get(this);
		if (annotations == null) {
			Map<Class<? extends Annotation>, Annotation> annotationMap = new LinkedHashMap<Class<? extends Annotation>, Annotation>();
			addAnnotationsToMap(annotationMap, getReadMethod());
			addAnnotationsToMap(annotationMap, getWriteMethod());
			addAnnotationsToMap(annotationMap, getField());
			annotations = annotationMap.values().toArray(new Annotation[annotationMap.size()]);
			annotationCache.put(this, annotations);
		}
		return annotations;
	}

	private void addAnnotationsToMap(
		Map<Class<? extends Annotation>, Annotation> annotationMap,
		AnnotatedElement object) {
		if (object != null) {
			for (Annotation annotation : object.getAnnotations()) {
				annotationMap.put(annotation.annotationType(), annotation);
			}
		}
	}

	private Field getField() {
		String name = getName();
		if (!StringUtils.hasLength(name)) {
			return null;
		}
		Class<?> declaringClass = declaringClass();
		Field field = ReflectionUtils.findField(declaringClass, name);
		if (field == null) {
			// Same lenient fallback checking as in CachedIntrospectionResults...
			field = ReflectionUtils.findField(declaringClass,
					name.substring(0, 1).toLowerCase() + name.substring(1));
			if (field == null) {
				field = ReflectionUtils.findField(declaringClass,
						name.substring(0, 1).toUpperCase() + name.substring(1));
			}
		}
		return field;
	}

	private Class<?> declaringClass() {
		if (getReadMethod() != null) {
			return getReadMethod().getDeclaringClass();
		}
		else {
			return getWriteMethod().getDeclaringClass();
		}
	}


	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof Property)) {
			return false;
		}
		Property otherProperty = (Property) other;
		return (ObjectUtils.nullSafeEquals(this.objectType, otherProperty.objectType) &&
				ObjectUtils.nullSafeEquals(this.name, otherProperty.name) &&
				ObjectUtils.nullSafeEquals(this.readMethod, otherProperty.readMethod) &&
				ObjectUtils.nullSafeEquals(this.writeMethod, otherProperty.writeMethod));
	}

	@Override
	public int hashCode() {
		return (ObjectUtils.nullSafeHashCode(this.objectType) * 31 + ObjectUtils.nullSafeHashCode(this.name));
	}

}
