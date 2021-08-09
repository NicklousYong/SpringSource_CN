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

package org.springframework.core.convert.converter;

import org.springframework.core.convert.TypeDescriptor;

/**
 * Allows a {@link Converter}, {@link GenericConverter} or {@link ConverterFactory} to
 * conditionally execute based on attributes of the {@code source} and {@code target}
 * {@link TypeDescriptor}.
 *
 * <p>Often used to selectively match custom conversion logic based on the presence of a
 * field or class-level characteristic, such as an annotation or method. For example, when
 * converting from a String field to a Date field, an implementation might return
 * {@code true} if the target field has also been annotated with {@code @DateTimeFormat}.
 *
 * <p>As another example, when converting from a String field to an {@code Account} field,
 * an implementation might return {@code true} if the target Account class defines a
 * {@code public static findAccount(String)} method.
 *
 * <p>
 *  允许{@link转换器},{@link GenericConverter}或{@link ConverterFactory}根据{@code source}和{@code target} {@link TypeDescriptor}
 * 的属性有条件地执行。
 * 
 * 通常用于根据字段或类级特性(如注释或方法)的存在选择性地匹配自定义转换逻辑例如,当从String字段转换为Date字段时,实现可能会返回{@如果目标字段也已经用{@code @DateTimeFormat}
 * 注释,则代码为true}。
 * 
 * 
 * @author Phillip Webb
 * @author Keith Donald
 * @since 3.2
 * @see Converter
 * @see GenericConverter
 * @see ConverterFactory
 * @see ConditionalGenericConverter
 */
public interface ConditionalConverter {

	/**
	 * Should the conversion from {@code sourceType} to {@code targetType} currently under
	 * consideration be selected?
	 * <p>
	 *  另一个例子是,当从String字段转换为{@code Account}字段时,如果目标Account类定义了一个{@code public static findAccount(String)}方法,
	 * 则实现可能返回{@code true}。
	 * 
	 * 
	 * @param sourceType the type descriptor of the field we are converting from
	 * @param targetType the type descriptor of the field we are converting to
	 * @return true if conversion should be performed, false otherwise
	 */
	boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType);

}
