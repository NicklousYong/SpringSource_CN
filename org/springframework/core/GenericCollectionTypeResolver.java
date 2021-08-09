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

package org.springframework.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

/**
 * Helper class for determining element types of collections and maps.
 *
 * <p>Mainly intended for usage within the framework, determining the
 * target type of values to be added to a collection or map
 * (to be able to attempt type conversion if appropriate).
 *
 * <p>
 *  帮助类确定集合和地图的元素类型
 * 
 * <p>主要是为了在框架内使用,确定要添加到集合或映射中的值的目标类型(如果适当,可以尝试类型转换)
 * 
 * 
 * @author Juergen Hoeller
 * @author Phillip Webb
 * @since 2.0
 * @see ResolvableType
 */
public abstract class GenericCollectionTypeResolver {

	/**
	 * Determine the generic element type of the given Collection class
	 * (if it declares one through a generic superclass or generic interface).
	 * <p>
	 *  确定给定Collection类的通用元素类型(如果通过通用超类或通用接口声明一个)
	 * 
	 * 
	 * @param collectionClass the collection class to introspect
	 * @return the generic type, or {@code null} if none
	 */
	@SuppressWarnings("rawtypes")
	public static Class<?> getCollectionType(Class<? extends Collection> collectionClass) {
		return ResolvableType.forClass(collectionClass).asCollection().resolveGeneric();
	}

	/**
	 * Determine the generic key type of the given Map class
	 * (if it declares one through a generic superclass or generic interface).
	 * <p>
	 *  确定给定Map类的通用键类型(如果通过通用超类或通用接口声明一个)
	 * 
	 * 
	 * @param mapClass the map class to introspect
	 * @return the generic type, or {@code null} if none
	 */
	@SuppressWarnings("rawtypes")
	public static Class<?> getMapKeyType(Class<? extends Map> mapClass) {
		return ResolvableType.forClass(mapClass).asMap().resolveGeneric(0);
	}

	/**
	 * Determine the generic value type of the given Map class
	 * (if it declares one through a generic superclass or generic interface).
	 * <p>
	 *  确定给定Map类的通用值类型(如果通过通用超类或通用接口声明一个)
	 * 
	 * 
	 * @param mapClass the map class to introspect
	 * @return the generic type, or {@code null} if none
	 */
	@SuppressWarnings("rawtypes")
	public static Class<?> getMapValueType(Class<? extends Map> mapClass) {
		return ResolvableType.forClass(mapClass).asMap().resolveGeneric(1);
	}

	/**
	 * Determine the generic element type of the given Collection field.
	 * <p>
	 *  确定给定集合字段的通用元素类型
	 * 
	 * 
	 * @param collectionField the collection field to introspect
	 * @return the generic type, or {@code null} if none
	 */
	public static Class<?> getCollectionFieldType(Field collectionField) {
		return ResolvableType.forField(collectionField).asCollection().resolveGeneric();
	}

	/**
	 * Determine the generic element type of the given Collection field.
	 * <p>
	 *  确定给定集合字段的通用元素类型
	 * 
	 * 
	 * @param collectionField the collection field to introspect
	 * @param nestingLevel the nesting level of the target type
	 * (typically 1; e.g. in case of a List of Lists, 1 would indicate the
	 * nested List, whereas 2 would indicate the element of the nested List)
	 * @return the generic type, or {@code null} if none
	 */
	public static Class<?> getCollectionFieldType(Field collectionField, int nestingLevel) {
		return ResolvableType.forField(collectionField).getNested(nestingLevel).asCollection().resolveGeneric();
	}

	/**
	 * Determine the generic element type of the given Collection field.
	 * <p>
	 *  确定给定集合字段的通用元素类型
	 * 
	 * 
	 * @param collectionField the collection field to introspect
	 * @param nestingLevel the nesting level of the target type
	 * (typically 1; e.g. in case of a List of Lists, 1 would indicate the
	 * nested List, whereas 2 would indicate the element of the nested List)
	 * @param typeIndexesPerLevel Map keyed by nesting level, with each value
	 * expressing the type index for traversal at that level
	 * @return the generic type, or {@code null} if none
	 * @deprecated as of 4.0, in favor of using {@link ResolvableType} for arbitrary nesting levels
	 */
	@Deprecated
	public static Class<?> getCollectionFieldType(Field collectionField, int nestingLevel, Map<Integer, Integer> typeIndexesPerLevel) {
		return ResolvableType.forField(collectionField).getNested(nestingLevel, typeIndexesPerLevel).asCollection().resolveGeneric();
	}

	/**
	 * Determine the generic key type of the given Map field.
	 * <p>
	 * 确定给定的Map字段的通用键类型
	 * 
	 * 
	 * @param mapField the map field to introspect
	 * @return the generic type, or {@code null} if none
	 */
	public static Class<?> getMapKeyFieldType(Field mapField) {
		return ResolvableType.forField(mapField).asMap().resolveGeneric(0);
	}

	/**
	 * Determine the generic key type of the given Map field.
	 * <p>
	 *  确定给定的Map字段的通用键类型
	 * 
	 * 
	 * @param mapField the map field to introspect
	 * @param nestingLevel the nesting level of the target type
	 * (typically 1; e.g. in case of a List of Lists, 1 would indicate the
	 * nested List, whereas 2 would indicate the element of the nested List)
	 * @return the generic type, or {@code null} if none
	 */
	public static Class<?> getMapKeyFieldType(Field mapField, int nestingLevel) {
		return ResolvableType.forField(mapField).getNested(nestingLevel).asMap().resolveGeneric(0);
	}

	/**
	 * Determine the generic key type of the given Map field.
	 * <p>
	 *  确定给定的Map字段的通用键类型
	 * 
	 * 
	 * @param mapField the map field to introspect
	 * @param nestingLevel the nesting level of the target type
	 * (typically 1; e.g. in case of a List of Lists, 1 would indicate the
	 * nested List, whereas 2 would indicate the element of the nested List)
	 * @param typeIndexesPerLevel Map keyed by nesting level, with each value
	 * expressing the type index for traversal at that level
	 * @return the generic type, or {@code null} if none
	 * @deprecated as of 4.0, in favor of using {@link ResolvableType} for arbitrary nesting levels
	 */
	@Deprecated
	public static Class<?> getMapKeyFieldType(Field mapField, int nestingLevel, Map<Integer, Integer> typeIndexesPerLevel) {
		return ResolvableType.forField(mapField).getNested(nestingLevel, typeIndexesPerLevel).asMap().resolveGeneric(0);
	}

	/**
	 * Determine the generic value type of the given Map field.
	 * <p>
	 *  确定给定的Map字段的通用值类型
	 * 
	 * 
	 * @param mapField the map field to introspect
	 * @return the generic type, or {@code null} if none
	 */
	public static Class<?> getMapValueFieldType(Field mapField) {
		return ResolvableType.forField(mapField).asMap().resolveGeneric(1);
	}

	/**
	 * Determine the generic value type of the given Map field.
	 * <p>
	 *  确定给定的Map字段的通用值类型
	 * 
	 * 
	 * @param mapField the map field to introspect
	 * @param nestingLevel the nesting level of the target type
	 * (typically 1; e.g. in case of a List of Lists, 1 would indicate the
	 * nested List, whereas 2 would indicate the element of the nested List)
	 * @return the generic type, or {@code null} if none
	 */
	public static Class<?> getMapValueFieldType(Field mapField, int nestingLevel) {
		return ResolvableType.forField(mapField).getNested(nestingLevel).asMap().resolveGeneric(1);
	}

	/**
	 * Determine the generic value type of the given Map field.
	 * <p>
	 *  确定给定的Map字段的通用值类型
	 * 
	 * 
	 * @param mapField the map field to introspect
	 * @param nestingLevel the nesting level of the target type
	 * (typically 1; e.g. in case of a List of Lists, 1 would indicate the
	 * nested List, whereas 2 would indicate the element of the nested List)
	 * @param typeIndexesPerLevel Map keyed by nesting level, with each value
	 * expressing the type index for traversal at that level
	 * @return the generic type, or {@code null} if none
	 * @deprecated as of 4.0, in favor of using {@link ResolvableType} for arbitrary nesting levels
	 */
	@Deprecated
	public static Class<?> getMapValueFieldType(Field mapField, int nestingLevel, Map<Integer, Integer> typeIndexesPerLevel) {
		return ResolvableType.forField(mapField).getNested(nestingLevel, typeIndexesPerLevel).asMap().resolveGeneric(1);
	}

	/**
	 * Determine the generic element type of the given Collection parameter.
	 * <p>
	 *  确定给定Collection参数的通用元素类型
	 * 
	 * 
	 * @param methodParam the method parameter specification
	 * @return the generic type, or {@code null} if none
	 */
	public static Class<?> getCollectionParameterType(MethodParameter methodParam) {
		return ResolvableType.forMethodParameter(methodParam).asCollection().resolveGeneric();
	}

	/**
	 * Determine the generic key type of the given Map parameter.
	 * <p>
	 *  确定给定Map参数的通用键类型
	 * 
	 * 
	 * @param methodParam the method parameter specification
	 * @return the generic type, or {@code null} if none
	 */
	public static Class<?> getMapKeyParameterType(MethodParameter methodParam) {
		return ResolvableType.forMethodParameter(methodParam).asMap().resolveGeneric(0);
	}

	/**
	 * Determine the generic value type of the given Map parameter.
	 * <p>
	 *  确定给定的Map参数的通用值类型
	 * 
	 * 
	 * @param methodParam the method parameter specification
	 * @return the generic type, or {@code null} if none
	 */
	public static Class<?> getMapValueParameterType(MethodParameter methodParam) {
		return ResolvableType.forMethodParameter(methodParam).asMap().resolveGeneric(1);
	}

	/**
	 * Determine the generic element type of the given Collection return type.
	 * <p>
	 *  确定给定集合返回类型的通用元素类型
	 * 
	 * 
	 * @param method the method to check the return type for
	 * @return the generic type, or {@code null} if none
	 */
	public static Class<?> getCollectionReturnType(Method method) {
		return ResolvableType.forMethodReturnType(method).asCollection().resolveGeneric();
	}

	/**
	 * Determine the generic element type of the given Collection return type.
	 * <p>If the specified nesting level is higher than 1, the element type of
	 * a nested Collection/Map will be analyzed.
	 * <p>
	 *  确定给定集合返回类型的通用元素类型<p>如果指定的嵌套级别高于1,将分析嵌套集合/映射的元素类型
	 * 
	 * 
	 * @param method the method to check the return type for
	 * @param nestingLevel the nesting level of the target type
	 * (typically 1; e.g. in case of a List of Lists, 1 would indicate the
	 * nested List, whereas 2 would indicate the element of the nested List)
	 * @return the generic type, or {@code null} if none
	 */
	public static Class<?> getCollectionReturnType(Method method, int nestingLevel) {
		return ResolvableType.forMethodReturnType(method).getNested(nestingLevel).asCollection().resolveGeneric();
	}

	/**
	 * Determine the generic key type of the given Map return type.
	 * <p>
	 * 确定给定Map返回类型的通用键类型
	 * 
	 * 
	 * @param method the method to check the return type for
	 * @return the generic type, or {@code null} if none
	 */
	public static Class<?> getMapKeyReturnType(Method method) {
		return ResolvableType.forMethodReturnType(method).asMap().resolveGeneric(0);
	}

	/**
	 * Determine the generic key type of the given Map return type.
	 * <p>
	 *  确定给定Map返回类型的通用键类型
	 * 
	 * 
	 * @param method the method to check the return type for
	 * @param nestingLevel the nesting level of the target type
	 * (typically 1; e.g. in case of a List of Lists, 1 would indicate the
	 * nested List, whereas 2 would indicate the element of the nested List)
	 * @return the generic type, or {@code null} if none
	 */
	public static Class<?> getMapKeyReturnType(Method method, int nestingLevel) {
		return ResolvableType.forMethodReturnType(method).getNested(nestingLevel).asMap().resolveGeneric(0);
	}

	/**
	 * Determine the generic value type of the given Map return type.
	 * <p>
	 *  确定给定Map返回类型的通用值类型
	 * 
	 * 
	 * @param method the method to check the return type for
	 * @return the generic type, or {@code null} if none
	 */
	public static Class<?> getMapValueReturnType(Method method) {
		return ResolvableType.forMethodReturnType(method).asMap().resolveGeneric(1);
	}

	/**
	 * Determine the generic value type of the given Map return type.
	 * <p>
	 *  确定给定Map返回类型的通用值类型
	 * 
	 * @param method the method to check the return type for
	 * @param nestingLevel the nesting level of the target type
	 * (typically 1; e.g. in case of a List of Lists, 1 would indicate the
	 * nested List, whereas 2 would indicate the element of the nested List)
	 * @return the generic type, or {@code null} if none
	 */
	public static Class<?> getMapValueReturnType(Method method, int nestingLevel) {
		return ResolvableType.forMethodReturnType(method).getNested(nestingLevel).asMap().resolveGeneric(1);
	}

}
