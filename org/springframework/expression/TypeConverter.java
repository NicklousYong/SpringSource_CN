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

package org.springframework.expression;

import org.springframework.core.convert.TypeDescriptor;

/**
 * A type converter can convert values between different types encountered during
 * expression evaluation. This is an SPI for the expression parser; see
 * {@link org.springframework.core.convert.ConversionService} for the primary
 * user API to Spring's conversion facilities.
 *
 * <p>
 * 类型转换器可以转换表达式求值期间遇到的不同类型之间的值这是表达式解析器的SPI;请参阅Spring的转换工具的主用户API的{@link orgspringframeworkcoreconvertConversionService}
 * 。
 * 
 * 
 * @author Andy Clement
 * @author Juergen Hoeller
 * @since 3.0
 */
public interface TypeConverter {

	/**
	 * Return {@code true} if the type converter can convert the specified type
	 * to the desired target type.
	 * <p>
	 *  如果类型转换器可以将指定的类型转换为所需的目标类型,则返回{@code true}
	 * 
	 * 
	 * @param sourceType a type descriptor that describes the source type
	 * @param targetType a type descriptor that describes the requested result type
	 * @return {@code true} if that conversion can be performed
	 */
	boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType);

	/**
	 * Convert (or coerce) a value from one type to another, for example from a
	 * {@code boolean} to a {@code String}.
	 * <p>The {@link TypeDescriptor} parameters enable support for typed collections:
	 * A caller may prefer a {@code List&lt;Integer&gt;}, for example, rather than
	 * simply any {@code List}.
	 * <p>
	 *  将值从一种类型转换(或强制)到另一种类型,例如从{@code布尔值}到{@code String} <p> {@link TypeDescriptor}参数启用对类型集合的支持：呼叫者可能更喜欢例如{@code List&lt; Integer&gt;}
	 * ,而不是简单的任何{@code List}。
	 * 
	 * @param value the value to be converted
	 * @param sourceType a type descriptor that supplies extra information about the
	 * source object
	 * @param targetType a type descriptor that supplies extra information about the
	 * requested result type
	 * @return the converted value
	 * @throws EvaluationException if conversion failed or is not possible to begin with
	 */
	Object convertValue(Object value, TypeDescriptor sourceType, TypeDescriptor targetType);

}
