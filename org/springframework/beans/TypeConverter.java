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

package org.springframework.beans;

import java.lang.reflect.Field;

import org.springframework.core.MethodParameter;

/**
 * Interface that defines type conversion methods. Typically (but not necessarily)
 * implemented in conjunction with the {@link PropertyEditorRegistry} interface.
 *
 * <p><b>Note:</b> Since TypeConverter implementations are typically based on
 * {@link java.beans.PropertyEditor PropertyEditors} which aren't thread-safe,
 * TypeConverters themselves are <em>not</em> to be considered as thread-safe either.
 *
 * <p>
 *  定义类型转换方法的接口通常(但不一定)与{@link PropertyEditorRegistry}接口一起实现
 * 
 * <p> <b>注意：</b>由于TypeConverter实现通常基于不是线程安全的{@link javabeansPropertyEditor PropertyEditors},所以TypeConve
 * rters本身不被</em>认为是线程安全,也安全。
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.0
 * @see SimpleTypeConverter
 * @see BeanWrapperImpl
 */
public interface TypeConverter {

	/**
	 * Convert the value to the required type (if necessary from a String).
	 * <p>Conversions from String to any type will typically use the {@code setAsText}
	 * method of the PropertyEditor class, or a Spring Converter in a ConversionService.
	 * <p>
	 *  将值转换为必需的类型(如果需要,从String)<p>从String到任何类型的转换通常将使用PropertyEditor类的{@code setAsText}方法或ConversionService
	 * 中的Spring Converter。
	 * 
	 * 
	 * @param value the value to convert
	 * @param requiredType the type we must convert to
	 * (or {@code null} if not known, for example in case of a collection element)
	 * @return the new value, possibly the result of type conversion
	 * @throws TypeMismatchException if type conversion failed
	 * @see java.beans.PropertyEditor#setAsText(String)
	 * @see java.beans.PropertyEditor#getValue()
	 * @see org.springframework.core.convert.ConversionService
	 * @see org.springframework.core.convert.converter.Converter
	 */
	<T> T convertIfNecessary(Object value, Class<T> requiredType) throws TypeMismatchException;

	/**
	 * Convert the value to the required type (if necessary from a String).
	 * <p>Conversions from String to any type will typically use the {@code setAsText}
	 * method of the PropertyEditor class, or a Spring Converter in a ConversionService.
	 * <p>
	 *  将值转换为必需的类型(如果需要,从String)<p>从String到任何类型的转换通常将使用PropertyEditor类的{@code setAsText}方法或ConversionService
	 * 中的Spring Converter。
	 * 
	 * 
	 * @param value the value to convert
	 * @param requiredType the type we must convert to
	 * (or {@code null} if not known, for example in case of a collection element)
	 * @param methodParam the method parameter that is the target of the conversion
	 * (for analysis of generic types; may be {@code null})
	 * @return the new value, possibly the result of type conversion
	 * @throws TypeMismatchException if type conversion failed
	 * @see java.beans.PropertyEditor#setAsText(String)
	 * @see java.beans.PropertyEditor#getValue()
	 * @see org.springframework.core.convert.ConversionService
	 * @see org.springframework.core.convert.converter.Converter
	 */
	<T> T convertIfNecessary(Object value, Class<T> requiredType, MethodParameter methodParam)
			throws TypeMismatchException;

	/**
	 * Convert the value to the required type (if necessary from a String).
	 * <p>Conversions from String to any type will typically use the {@code setAsText}
	 * method of the PropertyEditor class, or a Spring Converter in a ConversionService.
	 * <p>
	 * 将值转换为必需的类型(如果需要,从String)<p>从String到任何类型的转换通常将使用PropertyEditor类的{@code setAsText}方法或ConversionService中
	 * 的Spring Converter。
	 * 
	 * @param value the value to convert
	 * @param requiredType the type we must convert to
	 * (or {@code null} if not known, for example in case of a collection element)
	 * @param field the reflective field that is the target of the conversion
	 * (for analysis of generic types; may be {@code null})
	 * @return the new value, possibly the result of type conversion
	 * @throws TypeMismatchException if type conversion failed
	 * @see java.beans.PropertyEditor#setAsText(String)
	 * @see java.beans.PropertyEditor#getValue()
	 * @see org.springframework.core.convert.ConversionService
	 * @see org.springframework.core.convert.converter.Converter
	 */
	<T> T convertIfNecessary(Object value, Class<T> requiredType, Field field)
			throws TypeMismatchException;

}
