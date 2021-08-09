/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2015 the original author or authors.
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

package org.springframework.format.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares that a field should be formatted as a number.
 *
 * <p>Supports formatting by style or custom pattern string.
 * Can be applied to any JDK {@code java.lang.Number} type.
 *
 * <p>For style-based formatting, set the {@link #style} attribute to be the
 * desired {@link Style}. For custom formatting, set the {@link #pattern}
 * attribute to be the number pattern, such as {@code #, ###.##}.
 *
 * <p>Each attribute is mutually exclusive, so only set one attribute per
 * annotation instance (the one most convenient one for your formatting needs).
 * When the {@link #pattern} attribute is specified, it takes precedence over
 * the {@link #style} attribute. When no annotation attributes are specified,
 * the default format applied is style-based for either number of currency,
 * depending on the annotated field type.
 *
 * <p>
 *  声明一个字段应该被格式化为一个数字
 * 
 *  <p>支持按样式或自定义模式字符串进行格式化可应用于任何JDK {@code javalangNumber}类型
 * 
 * <p>对于基于样式的格式设置,将{@link #style}属性设置为所需的{@link样式}对于自定义格式,将{@link #pattern}属性设置为数字模式,例如{@代码#,#####}
 * 
 *  每个属性是互斥的,所以只为每个注释实例设置一个属性(一个最适合你的格式化实例)当指定{@link #pattern}属性时,它优先于{@link #style }属性当没有指定注释属性时,应用的默认格
 * 式是基于样式的任意数量的货币,具体取决于注释的字段类型。
 * 
 * 
 * @author Keith Donald
 * @author Juergen Hoeller
 * @since 3.0
 * @see java.text.NumberFormat
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
public @interface NumberFormat {

	/**
	 * The style pattern to use to format the field.
	 * <p>Defaults to {@link Style#DEFAULT} for general-purpose number formatting
	 * for most annotated types, except for money types which default to currency
	 * formatting. Set this attribute when you wish to format your field in
	 * accordance with a common style other than the default style.
	 * <p>
	 * 用于格式化字段的样式模式<p>默认为{@link Style#DEFAULT},用于大多数注释类型的通用数字格式,除了默认为货币格式的货币类型。当您想格式化您的字段按照默认风格以外的普通风格
	 * 
	 */
	Style style() default Style.DEFAULT;

	/**
	 * The custom pattern to use to format the field.
	 * <p>Defaults to empty String, indicating no custom pattern String has been specified.
	 * Set this attribute when you wish to format your field in accordance with a
	 * custom number pattern not represented by a style.
	 * <p>
	 *  用于格式化字段的自定义模式<p>默认为空字符串,表示没有自定义模式已指定字符串当您希望根据不由样式表示的自定义数字模式格式化字段时设置此属性
	 * 
	 */
	String pattern() default "";


	/**
	 * Common number format styles.
	 * <p>
	 *  通用数字格式样式
	 * 
	 */
	enum Style {

		/**
		 * The default format for the annotated type: typically 'number' but possibly
		 * 'currency' for a money type (e.g. {@code javax.money.MonetaryAmount)}.
		 * <p>
		 *  注释类型的默认格式：通常是"数字",但可能是货币类型的"货币"(例如{@code javaxmoneyMonetaryAmount)}
		 * 
		 * 
		 * @since 4.2
		 */
		DEFAULT,

		/**
		 * The general-purpose number format for the current locale.
		 * <p>
		 * 当前语言环境的通用数字格式
		 * 
		 */
		NUMBER,

		/**
		 * The percent format for the current locale.
		 * <p>
		 *  当前语言环境的百分比格式
		 * 
		 */
		PERCENT,

		/**
		 * The currency format for the current locale.
		 * <p>
		 *  当前语言环境的货币格式
		 */
		CURRENCY
	}

}
