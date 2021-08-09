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

/**
 * A service interface for type conversion. This is the entry point into the convert system.
 * Call {@link #convert(Object, Class)} to perform a thread-safe type conversion using this system.
 *
 * <p>
 *  用于类型转换的服务接口这是转换系统的入口点Call {@link #convert(Object,Class)},以使用此系统执行线程安全类型转换
 * 
 * 
 * @author Keith Donald
 * @author Phillip Webb
 * @since 3.0
 */
public interface ConversionService {

	/**
	 * Return {@code true} if objects of {@code sourceType} can be converted to the {@code targetType}.
	 * <p>If this method returns {@code true}, it means {@link #convert(Object, Class)} is capable
	 * of converting an instance of {@code sourceType} to {@code targetType}.
	 * <p>Special note on collections, arrays, and maps types:
	 * For conversion between collection, array, and map types, this method will return {@code true}
	 * even though a convert invocation may still generate a {@link ConversionException} if the
	 * underlying elements are not convertible. Callers are expected to handle this exceptional case
	 * when working with collections and maps.
	 * <p>
	 * 如果{@code sourceType}的对象可以转换为{@code targetType} <p>,则返回{@code true}如果此方法返回{@code true},则表示{@link #convert(Object,Class)}
	 * 能够将{@code sourceType}的实例转换为{@code targetType} <p>集合,数组和映射类型的特殊注意事项：对于集合,数组和映射类型之间的转换,此方法将返回{@code true}
	 * 即使转换调用仍然可能会生成{@link ConversionException},如果基础元素不可转换在处理集合和映射时,调用者将会处理此异常情况。
	 * 
	 * 
	 * @param sourceType the source type to convert from (may be {@code null} if source is {@code null})
	 * @param targetType the target type to convert to (required)
	 * @return {@code true} if a conversion can be performed, {@code false} if not
	 * @throws IllegalArgumentException if {@code targetType} is {@code null}
	 */
	boolean canConvert(Class<?> sourceType, Class<?> targetType);

	/**
	 * Return {@code true} if objects of {@code sourceType} can be converted to the {@code targetType}.
	 * The TypeDescriptors provide additional context about the source and target locations
	 * where conversion would occur, often object fields or property locations.
	 * <p>If this method returns {@code true}, it means {@link #convert(Object, TypeDescriptor, TypeDescriptor)}
	 * is capable of converting an instance of {@code sourceType} to {@code targetType}.
	 * <p>Special note on collections, arrays, and maps types:
	 * For conversion between collection, array, and map types, this method will return {@code true}
	 * even though a convert invocation may still generate a {@link ConversionException} if the
	 * underlying elements are not convertible. Callers are expected to handle this exceptional case
	 * when working with collections and maps.
	 * <p>
	 * 如果{@code sourceType}的对象可以转换为{@code targetType},则返回{@code true} TypeDescriptors提供有关将发生转换的源和目标位置的其他上下文,
	 * 通常是对象字段或属性位置<p>如果该方法返回{@code true},这意味着{@link #convert(Object,TypeDescriptor,TypeDescriptor)}能够将{@code sourceType}
	 * 的实例转换为{@code targetType} <p>集合的特别说明,数组和映射类型：对于集合,数组和映射类型之间的转换,即使转换调用仍然可能会生成{@link ConversionException}
	 * ,如果基础元素不可转换,则此方法将返回{@code true}在使用收藏和地图时,来电者应该处理这种特殊情况。
	 * 
	 * 
	 * @param sourceType context about the source type to convert from
	 * (may be {@code null} if source is {@code null})
	 * @param targetType context about the target type to convert to (required)
	 * @return {@code true} if a conversion can be performed between the source and target types,
	 * {@code false} if not
	 * @throws IllegalArgumentException if {@code targetType} is {@code null}
	 */
	boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType);

	/**
	 * Convert the given {@code source} to the specified {@code targetType}.
	 * <p>
	 * 将给定的{@code source}转换为指定的{@code targetType}
	 * 
	 * 
	 * @param source the source object to convert (may be {@code null})
	 * @param targetType the target type to convert to (required)
	 * @return the converted object, an instance of targetType
	 * @throws ConversionException if a conversion exception occurred
	 * @throws IllegalArgumentException if targetType is {@code null}
	 */
	<T> T convert(Object source, Class<T> targetType);

	/**
	 * Convert the given {@code source} to the specified {@code targetType}.
	 * The TypeDescriptors provide additional context about the source and target locations
	 * where conversion will occur, often object fields or property locations.
	 * <p>
	 *  将给定的{@code source}转换为指定的{@code targetType} TypeDescriptors提供了关于将发生转换的源和目标位置的其他上下文,通常是对象字段或属性位置
	 * 
	 * @param source the source object to convert (may be {@code null})
	 * @param sourceType context about the source type to convert from
	 * (may be {@code null} if source is {@code null})
	 * @param targetType context about the target type to convert to (required)
	 * @return the converted object, an instance of {@link TypeDescriptor#getObjectType() targetType}
	 * @throws ConversionException if a conversion exception occurred
	 * @throws IllegalArgumentException if targetType is {@code null},
	 * or {@code sourceType} is {@code null} but source is not {@code null}
	 */
	Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType);

}
