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

package org.springframework.core.convert;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.UsesJava8;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

/**
 * Context about a type to convert from or to.
 *
 * <p>
 *  关于要转换的类型的上下文
 * 
 * 
 * @author Keith Donald
 * @author Andy Clement
 * @author Juergen Hoeller
 * @author Phillip Webb
 * @author Sam Brannen
 * @author Stephane Nicoll
 * @since 3.0
 */
@SuppressWarnings("serial")
public class TypeDescriptor implements Serializable {

	static final Annotation[] EMPTY_ANNOTATION_ARRAY = new Annotation[0];

	private static final boolean streamAvailable = ClassUtils.isPresent(
			"java.util.stream.Stream", TypeDescriptor.class.getClassLoader());

	private static final Map<Class<?>, TypeDescriptor> commonTypesCache = new HashMap<Class<?>, TypeDescriptor>(18);

	private static final Class<?>[] CACHED_COMMON_TYPES = {
			boolean.class, Boolean.class, byte.class, Byte.class, char.class, Character.class,
			double.class, Double.class, int.class, Integer.class, long.class, Long.class,
			float.class, Float.class, short.class, Short.class, String.class, Object.class};

	static {
		for (Class<?> preCachedClass : CACHED_COMMON_TYPES) {
			commonTypesCache.put(preCachedClass, valueOf(preCachedClass));
		}
	}


	private final Class<?> type;

	private final ResolvableType resolvableType;

	private final Annotation[] annotations;


	/**
	 * Create a new type descriptor from a {@link MethodParameter}.
	 * <p>Use this constructor when a source or target conversion point is a
	 * constructor parameter, method parameter, or method return value.
	 * <p>
	 * 从{@link MethodParameter}创建一个新的类型描述符<p>当源或目标转换点是构造函数参数,方法参数或方法返回值时,使用此构造函数
	 * 
	 * 
	 * @param methodParameter the method parameter
	 */
	public TypeDescriptor(MethodParameter methodParameter) {
		Assert.notNull(methodParameter, "MethodParameter must not be null");
		this.resolvableType = ResolvableType.forMethodParameter(methodParameter);
		this.type = this.resolvableType.resolve(methodParameter.getParameterType());
		this.annotations = (methodParameter.getParameterIndex() == -1 ?
				nullSafeAnnotations(methodParameter.getMethodAnnotations()) :
				nullSafeAnnotations(methodParameter.getParameterAnnotations()));
	}

	/**
	 * Create a new type descriptor from a {@link Field}.
	 * <p>Use this constructor when a source or target conversion point is a field.
	 * <p>
	 *  从{@link Field}创建一个新的类型描述符<p>当源或目标转换点是一个字段时,使用此构造函数
	 * 
	 * 
	 * @param field the field
	 */
	public TypeDescriptor(Field field) {
		Assert.notNull(field, "Field must not be null");
		this.resolvableType = ResolvableType.forField(field);
		this.type = this.resolvableType.resolve(field.getType());
		this.annotations = nullSafeAnnotations(field.getAnnotations());
	}

	/**
	 * Create a new type descriptor from a {@link Property}.
	 * <p>Use this constructor when a source or target conversion point is a
	 * property on a Java class.
	 * <p>
	 *  从{@link属性} <p>创建新的类型描述符当源或目标转换点是Java类上的属性时,使用此构造函数
	 * 
	 * 
	 * @param property the property
	 */
	public TypeDescriptor(Property property) {
		Assert.notNull(property, "Property must not be null");
		this.resolvableType = ResolvableType.forMethodParameter(property.getMethodParameter());
		this.type = this.resolvableType.resolve(property.getType());
		this.annotations = nullSafeAnnotations(property.getAnnotations());
	}

	/**
	 * Create a new type descriptor from a {@link ResolvableType}. This protected
	 * constructor is used internally and may also be used by subclasses that support
	 * non-Java languages with extended type systems.
	 * <p>
	 *  从{@link ResolvableType}创建一个新的类型描述符该受保护的构造函数在内部使用,也可以由支持具有扩展类型系统的非Java语言的子类使用
	 * 
	 * 
	 * @param resolvableType the resolvable type
	 * @param type the backing type (or {@code null} if it should get resolved)
	 * @param annotations the type annotations
	 */
	protected TypeDescriptor(ResolvableType resolvableType, Class<?> type, Annotation[] annotations) {
		this.resolvableType = resolvableType;
		this.type = (type != null ? type : resolvableType.resolve(Object.class));
		this.annotations = nullSafeAnnotations(annotations);
	}


	private Annotation[] nullSafeAnnotations(Annotation[] annotations) {
		return (annotations != null ? annotations : EMPTY_ANNOTATION_ARRAY);
	}

	/**
	 * Variation of {@link #getType()} that accounts for a primitive type by
	 * returning its object wrapper type.
	 * <p>This is useful for conversion service implementations that wish to
	 * normalize to object-based types and not work with primitive types directly.
	 * <p>
	 * 通过返回其对象包装器类型<p>来解释原始类型的{@link #getType()}的变化对于希望将其归一化为基于对象的类型并且不直接使用原始类型的转换服务实现来说,这是有用的
	 * 
	 */
	public Class<?> getObjectType() {
		return ClassUtils.resolvePrimitiveIfNecessary(getType());
	}

	/**
	 * The type of the backing class, method parameter, field, or property
	 * described by this TypeDescriptor.
	 * <p>Returns primitive types as-is. See {@link #getObjectType()} for a
	 * variation of this operation that resolves primitive types to their
	 * corresponding Object types if necessary.
	 * <p>
	 *  此TypeDescriptor <p>描述的支持类,方法参数,字段或属性的类型按原样返回基本类型查看{@link #getObjectType()},以便将此类操作的变体解析为其对应的对象类型如有必要
	 * 。
	 * 
	 * 
	 * @see #getObjectType()
	 */
	public Class<?> getType() {
		return this.type;
	}

	/**
	 * Return the underlying {@link ResolvableType}.
	 * <p>
	 *  返回底层{@link ResolvableType}
	 * 
	 * 
	 * @since 4.0
	 */
	public ResolvableType getResolvableType() {
		return this.resolvableType;
	}

	/**
	 * Return the underlying source of the descriptor. Will return a {@link Field},
	 * {@link MethodParameter} or {@link Type} depending on how the {@link TypeDescriptor}
	 * was constructed. This method is primarily to provide access to additional
	 * type information or meta-data that alternative JVM languages may provide.
	 * <p>
	 * 返回描述符的底层源将根据{@link TypeDescriptor}的构造方式返回{@link Field},{@link MethodParameter}或{@link Type}。
	 * 此方法主要用于访问附加类型信息或替代JVM语言可能提供的元数据。
	 * 
	 * 
	 * @since 4.0
	 */
	public Object getSource() {
		return (this.resolvableType != null ? this.resolvableType.getSource() : null);
	}

	/**
	 * Narrows this {@link TypeDescriptor} by setting its type to the class of the
	 * provided value.
	 * <p>If the value is {@code null}, no narrowing is performed and this TypeDescriptor
	 * is returned unchanged.
	 * <p>Designed to be called by binding frameworks when they read property, field,
	 * or method return values. Allows such frameworks to narrow a TypeDescriptor built
	 * from a declared property, field, or method return value type. For example, a field
	 * declared as {@code java.lang.Object} would be narrowed to {@code java.util.HashMap}
	 * if it was set to a {@code java.util.HashMap} value. The narrowed TypeDescriptor
	 * can then be used to convert the HashMap to some other type. Annotation and nested
	 * type context is preserved by the narrowed copy.
	 * <p>
	 * 通过将其类型设置为提供的值的类别来缩小{@link TypeDescriptor} <p>如果值为{@code null},则不执行缩小,并且此TypeDescriptor不会更改<p>设计为通过绑定调
	 * 用框架,当它们读取属性,字段或方法返回值允许这样的框架缩小从声明的属性,字段或方法返回值类型构建的TypeDescriptor例如,声明为{@code javalangObject}的字段将被缩小为{@代码javautilHashMap}
	 * 如果它被设置为{@code javautilHashMap}值,则可以使用缩小的TypeDescriptor将HashMap转换为其他类型的注释,并且嵌套类型上下文由缩小的副本保留。
	 * 
	 * 
	 * @param value the value to use for narrowing this type descriptor
	 * @return this TypeDescriptor narrowed (returns a copy with its type updated to the
	 * class of the provided value)
	 */
	public TypeDescriptor narrow(Object value) {
		if (value == null) {
			return this;
		}
		ResolvableType narrowed = ResolvableType.forType(value.getClass(), this.resolvableType);
		return new TypeDescriptor(narrowed, null, this.annotations);
	}

	/**
	 * Cast this {@link TypeDescriptor} to a superclass or implemented interface
	 * preserving annotations and nested type context.
	 * <p>
	 * 将此{@link TypeDescriptor}转换为超类或实现的界面,保留注释和嵌套类型上下文
	 * 
	 * 
	 * @param superType the super type to cast to (can be {@code null})
	 * @return a new TypeDescriptor for the up-cast type
	 * @throws IllegalArgumentException if this type is not assignable to the super-type
	 * @since 3.2
	 */
	public TypeDescriptor upcast(Class<?> superType) {
		if (superType == null) {
			return null;
		}
		Assert.isAssignable(superType, getType());
		return new TypeDescriptor(this.resolvableType.as(superType), superType, this.annotations);
	}

	/**
	 * Returns the name of this type: the fully qualified class name.
	 * <p>
	 *  返回此类型的名称：全限定类名
	 * 
	 */
	public String getName() {
		return ClassUtils.getQualifiedName(getType());
	}

	/**
	 * Is this type a primitive type?
	 * <p>
	 *  这种类型是原始类型吗?
	 * 
	 */
	public boolean isPrimitive() {
		return getType().isPrimitive();
	}

	/**
	 * The annotations associated with this type descriptor, if any.
	 * <p>
	 *  与此类型描述符相关联的注释(如果有)
	 * 
	 * 
	 * @return the annotations, or an empty array if none
	 */
	public Annotation[] getAnnotations() {
		return this.annotations;
	}

	/**
	 * Determine if this type descriptor has the specified annotation.
	 * <p>As of Spring Framework 4.2, this method supports arbitrary levels
	 * of meta-annotations.
	 * <p>
	 *  确定此类型描述符是否具有指定的注释<p>从Spring Framework 42开始,此方法支持任意级别的元注释
	 * 
	 * 
	 * @param annotationType the annotation type
	 * @return <tt>true</tt> if the annotation is present
	 */
	public boolean hasAnnotation(Class<? extends Annotation> annotationType) {
		return (getAnnotation(annotationType) != null);
	}

	/**
	 * Obtain the annotation of the specified {@code annotationType} that is on this type descriptor.
	 * <p>As of Spring Framework 4.2, this method supports arbitrary levels of meta-annotations.
	 * <p>
	 *  获取此类型描述符上指定的{@code注释类型}的注释<p>从Spring Framework 42开始,此方法支持任意级别的元注释
	 * 
	 * 
	 * @param annotationType the annotation type
	 * @return the annotation, or {@code null} if no such annotation exists on this type descriptor
	 */
	@SuppressWarnings("unchecked")
	public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
		// Search in annotations that are "present" (i.e., locally declared or inherited)
		// NOTE: this unfortunately favors inherited annotations over locally declared composed annotations.
		for (Annotation annotation : getAnnotations()) {
			if (annotation.annotationType() == annotationType) {
				return (T) annotation;
			}
		}

		// Search in annotation hierarchy
		for (Annotation composedAnnotation : getAnnotations()) {
			T ann = AnnotationUtils.findAnnotation(composedAnnotation.annotationType(), annotationType);
			if (ann != null) {
				return ann;
			}
		}
		return null;
	}

	/**
	 * Returns true if an object of this type descriptor can be assigned to the location
	 * described by the given type descriptor.
	 * <p>For example, {@code valueOf(String.class).isAssignableTo(valueOf(CharSequence.class))}
	 * returns {@code true} because a String value can be assigned to a CharSequence variable.
	 * On the other hand, {@code valueOf(Number.class).isAssignableTo(valueOf(Integer.class))}
	 * returns {@code false} because, while all Integers are Numbers, not all Numbers are Integers.
	 * <p>For arrays, collections, and maps, element and key/value types are checked if declared.
	 * For example, a List&lt;String&gt; field value is assignable to a Collection&lt;CharSequence&gt;
	 * field, but List&lt;Number&gt; is not assignable to List&lt;Integer&gt;.
	 * <p>
	 * 如果此类型描述符的对象可以分配给给定类型描述符<p>描述的位置,则返回true。
	 * 例如,{@code valueOf(Stringclass)isAssignableTo(valueOf(CharSequenceclass))}返回{@code true},因为字符串值可以分配给Ch
	 * arSequence变量另一方面,{@code valueOf(Numberclass)isAssignableTo(valueOf(Integerclass))}返回{@code false},因为所
	 * 有整数都是数字,而不是所有数字都是整数<p >对于数组,集合和映射,元素和键/值类型将被检查(如果已声明)例如,List&lt; String&gt;字段值可分配给Collection&lt; Char
	 * Sequence&gt;字段,但是列表&lt; Number&gt;不能分配给List&lt; Integer&gt;。
	 * 如果此类型描述符的对象可以分配给给定类型描述符<p>描述的位置,则返回true。
	 * 
	 * 
	 * @return {@code true} if this type is assignable to the type represented by the provided
	 * type descriptor
	 * @see #getObjectType()
	 */
	public boolean isAssignableTo(TypeDescriptor typeDescriptor) {
		boolean typesAssignable = typeDescriptor.getObjectType().isAssignableFrom(getObjectType());
		if (!typesAssignable) {
			return false;
		}
		if (isArray() && typeDescriptor.isArray()) {
			return getElementTypeDescriptor().isAssignableTo(typeDescriptor.getElementTypeDescriptor());
		}
		else if (isCollection() && typeDescriptor.isCollection()) {
			return isNestedAssignable(getElementTypeDescriptor(), typeDescriptor.getElementTypeDescriptor());
		}
		else if (isMap() && typeDescriptor.isMap()) {
			return isNestedAssignable(getMapKeyTypeDescriptor(), typeDescriptor.getMapKeyTypeDescriptor()) &&
				isNestedAssignable(getMapValueTypeDescriptor(), typeDescriptor.getMapValueTypeDescriptor());
		}
		else {
			return true;
		}
	}

	private boolean isNestedAssignable(TypeDescriptor nestedTypeDescriptor, TypeDescriptor otherNestedTypeDescriptor) {
		if (nestedTypeDescriptor == null || otherNestedTypeDescriptor == null) {
			return true;
		}
		return nestedTypeDescriptor.isAssignableTo(otherNestedTypeDescriptor);
	}

	/**
	 * Is this type a {@link Collection} type?
	 * <p>
	 *  这种类型是{@link Collection}类型吗?
	 * 
	 */
	public boolean isCollection() {
		return Collection.class.isAssignableFrom(getType());
	}

	/**
	 * Is this type an array type?
	 * <p>
	 * 这种类型是数组类型吗?
	 * 
	 */
	public boolean isArray() {
		return getType().isArray();
	}

	/**
	 * If this type is an array, returns the array's component type.
	 * If this type is a {@code Stream}, returns the stream's component type.
	 * If this type is a {@link Collection} and it is parameterized, returns the Collection's element type.
	 * If the Collection is not parameterized, returns {@code null} indicating the element type is not declared.
	 * <p>
	 *  如果此类型是数组,则返回数组的组件类型如果此类型为{@code Stream},则返回流的组件类型如果此类型为{@link Collection}并且被参数化,则返回集合的元素类型如果集合未被参数化,
	 * 返回{@code null}表示元素类型未声明。
	 * 
	 * 
	 * @return the array component type or Collection element type, or {@code null} if this type is a
	 * Collection but its element type is not parameterized
	 * @throws IllegalStateException if this type is not a {@code java.util.Collection} or array type
	 */
	public TypeDescriptor getElementTypeDescriptor() {
		if (this.resolvableType.isArray()) {
			return new TypeDescriptor(this.resolvableType.getComponentType(), null, this.annotations);
		}
		if (streamAvailable && StreamDelegate.isStream(this.type)) {
			return StreamDelegate.getStreamElementType(this);
		}
		return getRelatedIfResolvable(this, this.resolvableType.asCollection().getGeneric(0));
	}

	/**
	 * If this type is a {@link Collection} or an array, creates a element TypeDescriptor
	 * from the provided collection or array element.
	 * <p>Narrows the {@link #getElementTypeDescriptor() elementType} property to the class
	 * of the provided collection or array element. For example, if this describes a
	 * {@code java.util.List&lt;java.lang.Number&lt;} and the element argument is an
	 * {@code java.lang.Integer}, the returned TypeDescriptor will be {@code java.lang.Integer}.
	 * If this describes a {@code java.util.List&lt;?&gt;} and the element argument is an
	 * {@code java.lang.Integer}, the returned TypeDescriptor will be {@code java.lang.Integer}
	 * as well.
	 * <p>Annotation and nested type context will be preserved in the narrowed
	 * TypeDescriptor that is returned.
	 * <p>
	 * 如果此类型是{@link Collection}或数组,则从提供的集合或数组元素中创建一个元素TypeDescriptor <p>将{@link #getElementTypeDescriptor()elementType}
	 * 属性缩小为提供的集合或数组元素的类例如,如果这描述了一个{@code javautilList&lt; javalangNumber&lt;},并且element参数是一个{@code javalangInteger}
	 * ,则返回的TypeDescriptor将是{@code javalangInteger}。
	 * 如果这描述了一个{@code javautilList&lt;?&gt;}并且element参数是一个{@code javalangInteger},返回的TypeDescriptor也将是{@code javalangInteger}
	 * 以及<p>注释和嵌套类型上下文将被保留在返回的缩小的TypeDescriptor中。
	 * 
	 * 
	 * @param element the collection or array element
	 * @return a element type descriptor, narrowed to the type of the provided element
	 * @throws IllegalStateException if this type is not a {@code java.util.Collection}
	 * or array type
	 * @see #narrow(Object)
	 */
	public TypeDescriptor elementTypeDescriptor(Object element) {
		return narrow(element, getElementTypeDescriptor());
	}

	/**
	 * Is this type a {@link Map} type?
	 * <p>
	 *  这种类型是{@link Map}类型吗?
	 * 
	 */
	public boolean isMap() {
		return Map.class.isAssignableFrom(getType());
	}

	/**
	 * If this type is a {@link Map} and its key type is parameterized,
	 * returns the map's key type. If the Map's key type is not parameterized,
	 * returns {@code null} indicating the key type is not declared.
	 * <p>
	 * 如果此类型是{@link Map},并且其键类型被参数化,则返回地图的键类型如果Map的键类型未被参数化,则返回{@code null},表示未声明键类型
	 * 
	 * 
	 * @return the Map key type, or {@code null} if this type is a Map
	 * but its key type is not parameterized
	 * @throws IllegalStateException if this type is not a {@code java.util.Map}
	 */
	public TypeDescriptor getMapKeyTypeDescriptor() {
		Assert.state(isMap(), "Not a java.util.Map");
		return getRelatedIfResolvable(this, this.resolvableType.asMap().getGeneric(0));
	}

	/**
	 * If this type is a {@link Map}, creates a mapKey {@link TypeDescriptor}
	 * from the provided map key.
	 * <p>Narrows the {@link #getMapKeyTypeDescriptor() mapKeyType} property
	 * to the class of the provided map key. For example, if this describes a
	 * {@code java.util.Map&lt;java.lang.Number, java.lang.String&lt;} and the key
	 * argument is a {@code java.lang.Integer}, the returned TypeDescriptor will be
	 * {@code java.lang.Integer}. If this describes a {@code java.util.Map&lt;?, ?&gt;}
	 * and the key argument is a {@code java.lang.Integer}, the returned
	 * TypeDescriptor will be {@code java.lang.Integer} as well.
	 * <p>Annotation and nested type context will be preserved in the narrowed
	 * TypeDescriptor that is returned.
	 * <p>
	 * 如果此类型是{@link Map},则从提供的地图键创建一个mapKey {@link TypeDescriptor} <p>将{@link #getMapKeyTypeDescriptor()mapKeyType}
	 * 属性缩小为提供的地图键的类例如,如果这描述了一个{@code javautilMap&lt; javalangNumber,javalangString&lt;},并且key参数是一个{@code javalangInteger}
	 * ,则返回的TypeDescriptor将是{@code javalangInteger}。
	 * 如果这描述了一个{@code javautangMap&lt;?,?&gt; }和key参数是一个{@code javalangInteger},返回的TypeDescriptor也将是{@code javalangInteger}
	 * 以及<p>注释和嵌套类型上下文将被保留在被返回的缩小的TypeDescriptor中。
	 * 
	 * 
	 * @param mapKey the map key
	 * @return the map key type descriptor
	 * @throws IllegalStateException if this type is not a {@code java.util.Map}
	 * @see #narrow(Object)
	 */
	public TypeDescriptor getMapKeyTypeDescriptor(Object mapKey) {
		return narrow(mapKey, getMapKeyTypeDescriptor());
	}

	/**
	 * If this type is a {@link Map} and its value type is parameterized,
	 * returns the map's value type.
	 * <p>If the Map's value type is not parameterized, returns {@code null}
	 * indicating the value type is not declared.
	 * <p>
	 * 如果此类型是{@link Map},并且其值类型参数化,则返回地图的值类型<p>如果Map的值类型未参数化,则返回{@code null},表示值类型未声明
	 * 
	 * 
	 * @return the Map value type, or {@code null} if this type is a Map
	 * but its value type is not parameterized
	 * @throws IllegalStateException if this type is not a {@code java.util.Map}
	 */
	public TypeDescriptor getMapValueTypeDescriptor() {
		Assert.state(isMap(), "Not a java.util.Map");
		return getRelatedIfResolvable(this, this.resolvableType.asMap().getGeneric(1));
	}

	/**
	 * If this type is a {@link Map}, creates a mapValue {@link TypeDescriptor}
	 * from the provided map value.
	 * <p>Narrows the {@link #getMapValueTypeDescriptor() mapValueType} property
	 * to the class of the provided map value. For example, if this describes a
	 * {@code java.util.Map&lt;java.lang.String, java.lang.Number&lt;} and the value
	 * argument is a {@code java.lang.Integer}, the returned TypeDescriptor will be
	 * {@code java.lang.Integer}. If this describes a {@code java.util.Map&lt;?, ?&gt;}
	 * and the value argument is a {@code java.lang.Integer}, the returned
	 * TypeDescriptor will be {@code java.lang.Integer} as well.
	 * <p>Annotation and nested type context will be preserved in the narrowed
	 * TypeDescriptor that is returned.
	 * <p>
	 * 如果此类型是{@link Map},则从提供的地图值创建一个mapValue {@link TypeDescriptor} <p>将{@link #getMapValueTypeDescriptor()mapValueType}
	 * 属性缩小为提供的地图值的类别例如,如果这描述了一个{@code javautilMap&lt; javalangString,javalangNumber&lt;},value参数是一个{@code javalangInteger}
	 * ,则返回的TypeDescriptor将是{@code javalangInteger}。
	 * 如果这描述了一个{@code javautangMap&lt;?,?&gt; }并且value参数是一个{@code javalangInteger},返回的TypeDescriptor也将是{@code javalangInteger}
	 * 以及<p>注释和嵌套类型上下文将被保留在被返回的缩小的TypeDescriptor中。
	 * 
	 * 
	 * @param mapValue the map value
	 * @return the map value type descriptor
	 * @throws IllegalStateException if this type is not a {@code java.util.Map}
	 * @see #narrow(Object)
	 */
	public TypeDescriptor getMapValueTypeDescriptor(Object mapValue) {
		return narrow(mapValue, getMapValueTypeDescriptor());
	}

	private TypeDescriptor narrow(Object value, TypeDescriptor typeDescriptor) {
		if (typeDescriptor != null) {
			return typeDescriptor.narrow(value);
		}
		return (value != null ? new TypeDescriptor(this.resolvableType, value.getClass(), this.annotations) : null);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof TypeDescriptor)) {
			return false;
		}
		TypeDescriptor other = (TypeDescriptor) obj;
		if (!ObjectUtils.nullSafeEquals(this.type, other.type)) {
			return false;
		}
		if (getAnnotations().length != other.getAnnotations().length) {
			return false;
		}
		for (Annotation ann : getAnnotations()) {
			if (!ann.equals(other.getAnnotation(ann.annotationType()))) {
				return false;
			}
		}
		if (isCollection() || isArray()) {
			return ObjectUtils.nullSafeEquals(getElementTypeDescriptor(), other.getElementTypeDescriptor());
		}
		else if (isMap()) {
			return ObjectUtils.nullSafeEquals(getMapKeyTypeDescriptor(), other.getMapKeyTypeDescriptor()) &&
					ObjectUtils.nullSafeEquals(getMapValueTypeDescriptor(), other.getMapValueTypeDescriptor());
		}
		else {
			return true;
		}
	}

	@Override
	public int hashCode() {
		return getType().hashCode();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (Annotation ann : getAnnotations()) {
			builder.append("@").append(ann.annotationType().getName()).append(' ');
		}
		builder.append(this.resolvableType.toString());
		return builder.toString();
	}

	/**
	 * Create a new type descriptor from the given type.
	 * <p>Use this to instruct the conversion system to convert an object to a
	 * specific target type, when no type location such as a method parameter or
	 * field is available to provide additional conversion context.
	 * <p>Generally prefer use of {@link #forObject(Object)} for constructing type
	 * descriptors from source objects, as it handles the {@code null} object case.
	 * <p>
	 * 从给定的类型创建一个新的类型描述符<p>使用此命令可指示转换系统将对象转换为特定的目标类型,当没有类型位置(如方法参数或字段)可用于提供额外的转换上下文<p>通常更喜欢使用{@link #forObject(Object)}
	 * 来构建源对象的类型描述符,因为它处理{@code null}对象事件。
	 * 
	 * 
	 * @param type the class (may be {@code null} to indicate {@code Object.class})
	 * @return the corresponding type descriptor
	 */
	public static TypeDescriptor valueOf(Class<?> type) {
		if (type == null) {
			type = Object.class;
		}
		TypeDescriptor desc = commonTypesCache.get(type);
		return (desc != null ? desc : new TypeDescriptor(ResolvableType.forClass(type), null, null));
	}

	/**
	 * Create a new type descriptor from a {@link java.util.Collection} type.
	 * <p>Useful for converting to typed Collections.
	 * <p>For example, a {@code List<String>} could be converted to a
	 * {@code List<EmailAddress>} by converting to a targetType built with this method.
	 * The method call to construct such a {@code TypeDescriptor} would look something
	 * like: {@code collection(List.class, TypeDescriptor.valueOf(EmailAddress.class));}
	 * <p>
	 * 从{@link javautilCollection}类型创建一个新的类型描述符<p>有用于转换为类型集合<p>例如,{@code List <String>}可以转换为{@code List <EmailAddress>}
	 * 通过转换为使用此方法构建的targetType构造此类{@code TypeDescriptor}的方法调用将如下所示：{@code collection(Listclass,TypeDescriptorvalueOf(EmailAddressclass));}
	 * 。
	 * 
	 * 
	 * @param collectionType the collection type, which must implement {@link Collection}.
	 * @param elementTypeDescriptor a descriptor for the collection's element type,
	 * used to convert collection elements
	 * @return the collection type descriptor
	 */
	public static TypeDescriptor collection(Class<?> collectionType, TypeDescriptor elementTypeDescriptor) {
		Assert.notNull(collectionType, "collectionType must not be null");
		if (!Collection.class.isAssignableFrom(collectionType)) {
			throw new IllegalArgumentException("collectionType must be a java.util.Collection");
		}
		ResolvableType element = (elementTypeDescriptor != null ? elementTypeDescriptor.resolvableType : null);
		return new TypeDescriptor(ResolvableType.forClassWithGenerics(collectionType, element), null, null);
	}

	/**
	 * Create a new type descriptor from a {@link java.util.Map} type.
	 * <p>Useful for converting to typed Maps.
	 * <p>For example, a Map&lt;String, String&gt; could be converted to a Map&lt;Id, EmailAddress&gt;
	 * by converting to a targetType built with this method:
	 * The method call to construct such a TypeDescriptor would look something like:
	 * <pre class="code">
	 * map(Map.class, TypeDescriptor.valueOf(Id.class), TypeDescriptor.valueOf(EmailAddress.class));
	 * </pre>
	 * <p>
	 *  从{@link javautilMap}类型创建一个新的类型描述符<p>有用于转换为类型的地图<p>例如,Map&lt; String,String&gt;可以转换为地图&lt; Id,EmailAd
	 * dress&gt;通过转换为使用此方法构建的targetType：构造此类型描述符的方法调用将如下所示：。
	 * <pre class="code">
	 * map(Mapclass,TypeDescriptorvalueOf(Idclass),TypeDescriptorvalueOf(EmailAddressclass));
	 * </pre>
	 * 
	 * @param mapType the map type, which must implement {@link Map}
	 * @param keyTypeDescriptor a descriptor for the map's key type, used to convert map keys
	 * @param valueTypeDescriptor the map's value type, used to convert map values
	 * @return the map type descriptor
	 */
	public static TypeDescriptor map(Class<?> mapType, TypeDescriptor keyTypeDescriptor, TypeDescriptor valueTypeDescriptor) {
		if (!Map.class.isAssignableFrom(mapType)) {
			throw new IllegalArgumentException("mapType must be a java.util.Map");
		}
		ResolvableType key = (keyTypeDescriptor != null ? keyTypeDescriptor.resolvableType : null);
		ResolvableType value = (valueTypeDescriptor != null ? valueTypeDescriptor.resolvableType : null);
		return new TypeDescriptor(ResolvableType.forClassWithGenerics(mapType, key, value), null, null);
	}

	/**
	 * Create a new type descriptor as an array of the specified type.
	 * <p>For example to create a {@code Map<String,String>[]} use:
	 * <pre class="code">
	 * TypeDescriptor.array(TypeDescriptor.map(Map.class, TypeDescriptor.value(String.class), TypeDescriptor.value(String.class)));
	 * </pre>
	 * <p>
	 *  创建一个新的类型描述符作为指定类型的数组<p>例如,创建一个{@code Map <String,String> []}使用：
	 * <pre class="code">
	 *  TypeDescriptorarray(TypeDescriptorap(Mapclass,TypeDescriptorvalue(Stringclass),TypeDescriptorvalue(S
	 * tringclass)));。
	 * </pre>
	 * 
	 * @param elementTypeDescriptor the {@link TypeDescriptor} of the array element or {@code null}
	 * @return an array {@link TypeDescriptor} or {@code null} if {@code elementTypeDescriptor} is {@code null}
	 * @since 3.2.1
	 */
	public static TypeDescriptor array(TypeDescriptor elementTypeDescriptor) {
		if (elementTypeDescriptor == null) {
			return null;
		}
		return new TypeDescriptor(ResolvableType.forArrayComponent(elementTypeDescriptor.resolvableType),
				null, elementTypeDescriptor.getAnnotations());
	}

	/**
	 * Creates a type descriptor for a nested type declared within the method parameter.
	 * <p>For example, if the methodParameter is a {@code List<String>} and the
	 * nesting level is 1, the nested type descriptor will be String.class.
	 * <p>If the methodParameter is a {@code List<List<String>>} and the nesting
	 * level is 2, the nested type descriptor will also be a String.class.
	 * <p>If the methodParameter is a {@code Map<Integer, String>} and the nesting
	 * level is 1, the nested type descriptor will be String, derived from the map value.
	 * <p>If the methodParameter is a {@code List<Map<Integer, String>>} and the
	 * nesting level is 2, the nested type descriptor will be String, derived from the map value.
	 * <p>Returns {@code null} if a nested type cannot be obtained because it was not declared.
	 * For example, if the method parameter is a {@code List<?>}, the nested type
	 * descriptor returned will be {@code null}.
	 * <p>
	 * 为方法参数<p>中声明的嵌套类型创建一个类型描述符例如,如果methodParameter为{@code List <String>}而嵌套级别为1,则嵌套类型描述符将为Stringclass <p>如
	 * 果methodParameter是一个{@code List <List <String >>},嵌套级别为2,嵌套类型描述符也将是一个Stringclass <p>如果methodParameter是
	 * 一个{@code Map <Integer,String>},嵌套级别为1,嵌套类型描述符将为String,从映射值派生<p>如果methodParameter为{@code List <Map <Integer,String >>}
	 * ,嵌套级别为2,则嵌套类型描述符将是String,从映射值派生<p>返回{@code null}如果无法获取嵌套类型,因为未声明例如,如果method参数是{@code List <?>},返回的嵌套类
	 * 型描述符将是{@code null}。
	 * 
	 * 
	 * @param methodParameter the method parameter with a nestingLevel of 1
	 * @param nestingLevel the nesting level of the collection/array element or
	 * map key/value declaration within the method parameter
	 * @return the nested type descriptor at the specified nesting level,
	 * or {@code null} if it could not be obtained
	 * @throws IllegalArgumentException if the nesting level of the input
	 * {@link MethodParameter} argument is not 1, or if the types up to the
	 * specified nesting level are not of collection, array, or map types
	 */
	public static TypeDescriptor nested(MethodParameter methodParameter, int nestingLevel) {
		if (methodParameter.getNestingLevel() != 1) {
			throw new IllegalArgumentException("MethodParameter nesting level must be 1: " +
					"use the nestingLevel parameter to specify the desired nestingLevel for nested type traversal");
		}
		return nested(new TypeDescriptor(methodParameter), nestingLevel);
	}

	/**
	 * Creates a type descriptor for a nested type declared within the field.
	 * <p>For example, if the field is a {@code List<String>} and the nesting
	 * level is 1, the nested type descriptor will be {@code String.class}.
	 * <p>If the field is a {@code List<List<String>>} and the nesting level is
	 * 2, the nested type descriptor will also be a {@code String.class}.
	 * <p>If the field is a {@code Map<Integer, String>} and the nesting level
	 * is 1, the nested type descriptor will be String, derived from the map value.
	 * <p>If the field is a {@code List<Map<Integer, String>>} and the nesting
	 * level is 2, the nested type descriptor will be String, derived from the map value.
	 * <p>Returns {@code null} if a nested type cannot be obtained because it was not declared.
	 * For example, if the field is a {@code List<?>}, the nested type descriptor returned will be {@code null}.
	 * <p>
	 * 为字段<p>中声明的嵌套类型创建一个类型描述符例如,如果该字段是{@code List <String>}并且嵌套级别为1,则嵌套类型描述符将为{@code Stringclass} < p>如果字段是
	 * {@code List <List <String >>}并且嵌套级别为2,则嵌套类型描述符也将是{@code Stringclass} <p>如果该字段是{@code Map <整数,字符串>}并且嵌
	 * 套级别为1,嵌套类型描述符将为String,从映射值派生<p>如果该字段是{@code列表<Map <Integer,String >>}和嵌套级别为2,嵌套类型描述符将为String,从映射值派生<p>
	 * 返回{@code null}如果无法获取嵌套类型,因为未声明例如,如果该字段是{@code List <?>},返回的嵌套类型描述符将为{@code null}。
	 * 
	 * 
	 * @param field the field
	 * @param nestingLevel the nesting level of the collection/array element or
	 * map key/value declaration within the field
	 * @return the nested type descriptor at the specified nesting level,
	 * or {@code null} if it could not be obtained
	 * @throws IllegalArgumentException if the types up to the specified nesting
	 * level are not of collection, array, or map types
	 */
	public static TypeDescriptor nested(Field field, int nestingLevel) {
		return nested(new TypeDescriptor(field), nestingLevel);
	}

	/**
	 * Creates a type descriptor for a nested type declared within the property.
	 * <p>For example, if the property is a {@code List<String>} and the nesting
	 * level is 1, the nested type descriptor will be {@code String.class}.
	 * <p>If the property is a {@code List<List<String>>} and the nesting level
	 * is 2, the nested type descriptor will also be a {@code String.class}.
	 * <p>If the property is a {@code Map<Integer, String>} and the nesting level
	 * is 1, the nested type descriptor will be String, derived from the map value.
	 * <p>If the property is a {@code List<Map<Integer, String>>} and the nesting
	 * level is 2, the nested type descriptor will be String, derived from the map value.
	 * <p>Returns {@code null} if a nested type cannot be obtained because it was not declared.
	 * For example, if the property is a {@code List<?>}, the nested type descriptor
	 * returned will be {@code null}.
	 * <p>
	 * 为属性<p>中声明的嵌套类型创建一个类型描述符例如,如果属性是{@code List <String>}并且嵌套级别为1,则嵌套类型描述符将为{@code Stringclass} < p>如果属性是{@code List <List <String >>}
	 * ,嵌套级别为2,嵌套类型描述符也将是{@code Stringclass} <p>如果属性是{@code Map <整数,字符串>},嵌套级别为1,嵌套类型描述符将为String,从映射值<p>派生如果
	 * 属性为{@code List <Map <Integer,String >>}和嵌套级别为2,嵌套类型描述符将为String,从映射值派生<p>返回{@code null}如果无法获取嵌套类型,因为未声
	 * 明例如,如果属性是{@code List <?>},返回的嵌套类型描述符将是{@code null}。
	 * 
	 * 
	 * @param property the property
	 * @param nestingLevel the nesting level of the collection/array element or
	 * map key/value declaration within the property
	 * @return the nested type descriptor at the specified nesting level, or
	 * {@code null} if it could not be obtained
	 * @throws IllegalArgumentException if the types up to the specified nesting
	 * level are not of collection, array, or map types
	 */
	public static TypeDescriptor nested(Property property, int nestingLevel) {
		return nested(new TypeDescriptor(property), nestingLevel);
	}

	/**
	 * Create a new type descriptor for an object.
	 * <p>Use this factory method to introspect a source object before asking the
	 * conversion system to convert it to some another type.
	 * <p>If the provided object is {@code null}, returns {@code null}, else calls
	 * {@link #valueOf(Class)} to build a TypeDescriptor from the object's class.
	 * <p>
	 * 为对象创建一个新的类型描述符<p>在要求转换系统将其转换为其他类型之前,请使用此工厂方法来内省源对象。
	 * 如果提供的对象为{@code null},则返回{@code null},否则调用{@link #valueOf(Class)}从对象的类中构建一个TypeDescriptor。
	 * 
	 * 
	 * @param source the source object
	 * @return the type descriptor
	 */
	public static TypeDescriptor forObject(Object source) {
		return (source != null ? valueOf(source.getClass()) : null);
	}

	private static TypeDescriptor nested(TypeDescriptor typeDescriptor, int nestingLevel) {
		ResolvableType nested = typeDescriptor.resolvableType;
		for (int i = 0; i < nestingLevel; i++) {
			if (Object.class == nested.getType()) {
				// Could be a collection type but we don't know about its element type,
				// so let's just assume there is an element type of type Object...
			}
			else {
				nested = nested.getNested(2);
			}
		}
		if (nested == ResolvableType.NONE) {
			return null;
		}
		return getRelatedIfResolvable(typeDescriptor, nested);
	}

	private static TypeDescriptor getRelatedIfResolvable(TypeDescriptor source, ResolvableType type) {
		if (type.resolve() == null) {
			return null;
		}
		return new TypeDescriptor(type, null, source.annotations);
	}


	/**
	 * Inner class to avoid a hard dependency on Java 8.
	 * <p>
	 */
	@UsesJava8
	private static class StreamDelegate {

		public static boolean isStream(Class<?> type) {
			return Stream.class.isAssignableFrom(type);
		}

		public static TypeDescriptor getStreamElementType(TypeDescriptor source) {
			return getRelatedIfResolvable(source, source.resolvableType.as(Stream.class).getGeneric(0));
		}
	}

}
