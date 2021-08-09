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

package org.springframework.format.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares that a field should be formatted as a date time.
 *
 * <p>Supports formatting by style pattern, ISO date time pattern, or custom format pattern string.
 * Can be applied to {@code java.util.Date}, {@code java.util.Calendar}, {@code java.long.Long},
 * Joda-Time value types; and as of Spring 4 and JDK 8, to JSR-310 <code>java.time</code> types too.
 *
 * <p>For style-based formatting, set the {@link #style} attribute to be the style pattern code.
 * The first character of the code is the date style, and the second character is the time style.
 * Specify a character of 'S' for short style, 'M' for medium, 'L' for long, and 'F' for full.
 * A date or time may be omitted by specifying the style character '-'.
 *
 * <p>For ISO-based formatting, set the {@link #iso} attribute to be the desired {@link ISO} format,
 * such as {@link ISO#DATE}. For custom formatting, set the {@link #pattern()} attribute to be the
 * DateTime pattern, such as {@code yyyy/MM/dd hh:mm:ss a}.
 *
 * <p>Each attribute is mutually exclusive, so only set one attribute per annotation instance
 * (the one most convenient one for your formatting needs).
 * When the pattern attribute is specified, it takes precedence over both the style and ISO attribute.
 * When the {@link #iso} attribute is specified, it takes precedence over the style attribute.
 * When no annotation attributes are specified, the default format applied is style-based
 * with a style code of 'SS' (short date, short time).
 *
 * <p>
 *  声明一个字段应该被格式化为日期时间
 * 
 * <p>支持通过样式模式,ISO日期时间模式或自定义格式模式字符串进行格式化可以应用于{@code javautilDate},{@code javautilCalendar},{@code javalongLong}
 * ,Joda-Time值类型;并且从Spring 4和JDK 8,到JSR-310 <code> javatime </code>类型。
 * 
 *  <p>对于基于样式的格式设置,将{@link #style}属性设置为样式模式代码代码的第一个字符是日期样式,第二个字符是时间样式指定"S"对于短款式,"M"为中等,"L"为长,"F"为全A日期或时间
 * 可以通过指定样式字符" - "。
 * 
 * <p>对于基于ISO的格式,请将{@link #iso}属性设置为{@link ISO#格式,例如{@link ISO#DATE})。
 * 对于自定义格式,请将{@link #pattern )}属性为DateTime模式,例如{@code yyyy / MM / dd hh：mm：ss a}。
 * 
 *  每个属性是互斥的,所以只为每个注释实例设置一个属性(最适合你的格式化实例)当指定了pattern属性时,它优先于样式和ISO属性。
 * 当{@link #iso}属性被指定,它优先于样式属性当没有指定注释属性时,应用的默认格式是基于样式的样式代码为"SS"(短日期,短时间)。
 * 
 * 
 * @author Keith Donald
 * @author Juergen Hoeller
 * @since 3.0
 * @see org.joda.time.format.DateTimeFormat
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
public @interface DateTimeFormat {

	/**
	 * The style pattern to use to format the field.
	 * <p>Defaults to 'SS' for short date time. Set this attribute when you wish to format
	 * your field in accordance with a common style other than the default style.
	 * <p>
	 * 用于格式化字段的样式模式<p>短日期时间默认为"SS"当您希望根据默认样式以外的常见样式格式化字段时设置此属性
	 * 
	 */
	String style() default "SS";

	/**
	 * The ISO pattern to use to format the field.
	 * The possible ISO patterns are defined in the {@link ISO} enum.
	 * <p>Defaults to {@link ISO#NONE}, indicating this attribute should be ignored.
	 * Set this attribute when you wish to format your field in accordance with an ISO format.
	 * <p>
	 *  用于格式化字段的ISO模式可以在{@link ISO}枚举中定义可能的ISO模式<p>默认为{@link ISO#NONE},表示此属性应被忽略当您希望格式化时设置此属性您的领域符合ISO格式
	 * 
	 */
	ISO iso() default ISO.NONE;

	/**
	 * The custom pattern to use to format the field.
	 * <p>Defaults to empty String, indicating no custom pattern String has been specified.
	 * Set this attribute when you wish to format your field in accordance with a custom
	 * date time pattern not represented by a style or ISO format.
	 * <p>Note: This pattern follows the original {@link java.text.SimpleDateFormat} style,
	 * as also supported by Joda-Time, with strict parsing semantics towards overflows
	 * (e.g. rejecting a Feb 29 value for a non-leap-year). As a consequence, 'yy'
	 * characters indicate a year in the traditional style, not a "year-of-era" as in the
	 * {@link java.time.format.DateTimeFormatter} specification (i.e. 'yy' turns into 'uu'
	 * when going through that {@code DateTimeFormatter} with strict resolution mode).
	 * <p>
	 * 用于格式化字段的自定义模式<p>默认为空字符串,表示未指定自定义模式String当您希望根据不由样式或ISO表示的自定义日期时间模式格式化字段时设置此属性格式<p>注意：此模式遵循Joda-Time支持
	 * 的原始{@link javatextSimpleDateFormat}样式,具有严格的解析语义到溢出(例如,拒绝非闰年的2月29日值)结果"yy"字符表示传统风格的一年,而不是像{@link javatimeformatDateTimeFormatter}
	 * 规范中的"年龄"(即"yy"变成"uu",当通过{@code DateTimeFormatter}具有严格的分辨率模式)。
	 * 
	 */
	String pattern() default "";


	/**
	 * Common ISO date time format patterns.
	 * <p>
	 * 常见的ISO日期格式模式
	 * 
	 */
	enum ISO {

		/**
		 * The most common ISO Date Format {@code yyyy-MM-dd},
		 * e.g. "2000-10-31".
		 * <p>
		 *  最常见的ISO日期格式{@code yyyy-MM-dd},例如"2000-10-31"
		 * 
		 */
		DATE,

		/**
		 * The most common ISO Time Format {@code HH:mm:ss.SSSZ},
		 * e.g. "01:30:00.000-05:00".
		 * <p>
		 *  最常见的ISO时间格式{@code HH：mm：ssSSSZ},例如"01：30：00000-05：00"
		 * 
		 */
		TIME,

		/**
		 * The most common ISO DateTime Format {@code yyyy-MM-dd'T'HH:mm:ss.SSSZ},
		 * e.g. "2000-10-31T01:30:00.000-05:00".
		 * <p>This is the default if no annotation value is specified.
		 * <p>
		 *  最常见的ISO日期格式{@code yyyy-MM-dd'T'HH：mm：ssSSSZ},例如"2000-10-31T01：30：00000-05：00"<p>如果没有注释,这是默认值值被指定
		 * 
		 */
		DATE_TIME,

		/**
		 * Indicates that no ISO-based format pattern should be applied.
		 * <p>
		 *  表示不应用基于ISO的格式模式
		 */
		NONE
	}

}
