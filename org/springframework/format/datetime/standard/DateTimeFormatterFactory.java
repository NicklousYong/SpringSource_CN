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

package org.springframework.format.datetime.standard;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.ResolverStyle;
import java.util.TimeZone;

import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.lang.UsesJava8;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Factory that creates a JSR-310 {@link java.time.format.DateTimeFormatter}.
 *
 * <p>Formatters will be created using the defined {@link #setPattern pattern},
 * {@link #setIso ISO}, and <code>xxxStyle</code> methods (considered in that order).
 *
 * <p>
 *  创建JSR-310的工厂{@link javatimeformatDateTimeFormatter}
 * 
 * <p>使用定义的{@link #setPattern模式},{@link #setIso ISO}和<code> xxxStyle </code>方法(以该顺序考虑)创建格式化器。
 * 
 * 
 * @author Juergen Hoeller
 * @author Phillip Webb
 * @since 4.0
 * @see #createDateTimeFormatter()
 * @see #createDateTimeFormatter(DateTimeFormatter)
 * @see #setPattern
 * @see #setIso
 * @see #setDateStyle
 * @see #setTimeStyle
 * @see #setDateTimeStyle
 * @see DateTimeFormatterFactoryBean
 */
@UsesJava8
public class DateTimeFormatterFactory {

	private String pattern;

	private ISO iso;

	private FormatStyle dateStyle;

	private FormatStyle timeStyle;

	private TimeZone timeZone;


	/**
	 * Create a new {@code DateTimeFormatterFactory} instance.
	 * <p>
	 *  创建一个新的{@code DateTimeFormatterFactory}实例
	 * 
	 */
	public DateTimeFormatterFactory() {
	}

	/**
	 * Create a new {@code DateTimeFormatterFactory} instance.
	 * <p>
	 *  创建一个新的{@code DateTimeFormatterFactory}实例
	 * 
	 * 
	 * @param pattern the pattern to use to format date values
	 */
	public DateTimeFormatterFactory(String pattern) {
		this.pattern = pattern;
	}


	/**
	 * Set the pattern to use to format date values.
	 * <p>
	 *  设置用于格式化日期值的模式
	 * 
	 * 
	 * @param pattern the format pattern
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	/**
	 * Set the ISO format used to format date values.
	 * <p>
	 *  设置用于格式化日期值的ISO格式
	 * 
	 * 
	 * @param iso the ISO format
	 */
	public void setIso(ISO iso) {
		this.iso = iso;
	}

	/**
	 * Set the style to use for date types.
	 * <p>
	 *  设置要用于日期类型的样式
	 * 
	 */
	public void setDateStyle(FormatStyle dateStyle) {
		this.dateStyle = dateStyle;
	}

	/**
	 * Set the style to use for time types.
	 * <p>
	 *  设置用于时间类型的样式
	 * 
	 */
	public void setTimeStyle(FormatStyle timeStyle) {
		this.timeStyle = timeStyle;
	}

	/**
	 * Set the style to use for date and time types.
	 * <p>
	 *  设置用于日期和时间类型的样式
	 * 
	 */
	public void setDateTimeStyle(FormatStyle dateTimeStyle) {
		this.dateStyle = dateTimeStyle;
		this.timeStyle = dateTimeStyle;
	}

	/**
	 * Set the two characters to use to format date values, in Joda-Time style.
	 * <p>The first character is used for the date style; the second is for
	 * the time style. Supported characters are:
	 * <ul>
	 * <li>'S' = Small</li>
	 * <li>'M' = Medium</li>
	 * <li>'L' = Long</li>
	 * <li>'F' = Full</li>
	 * <li>'-' = Omitted</li>
	 * </ul>
	 * <p>This method mimics the styles supported by Joda-Time. Note that
	 * JSR-310 natively favors {@link java.time.format.FormatStyle} as used for
	 * {@link #setDateStyle}, {@link #setTimeStyle} and {@link #setDateTimeStyle}.
	 * <p>
	 *  设置用于格式化日期值的两个字符,在Joda-Time样式中<p>第一个字符用于日期样式;第二个是时间风格支持的字符是：
	 * <ul>
	 *  <li>'S'=小</li> <li>'M'=中</li> <li>'L'= Long </li> <li>'F'= Full </li> >' - '=略过</li>
	 * </ul>
	 * <p>此方法模仿Joda-Time支持的样式注意,JSR-310本身有利于{@link #setDateStyle},{@link #setTimeStyle}和{@link #setDateTimeStyle}
	 * 中使用的{@link javatimeformatFormatStyle}。
	 * 
	 * 
	 * @param style two characters from the set {"S", "M", "L", "F", "-"}
	 */
	public void setStylePattern(String style) {
		Assert.isTrue(style != null && style.length() == 2);
		this.dateStyle = convertStyleCharacter(style.charAt(0));
		this.timeStyle = convertStyleCharacter(style.charAt(1));
	}

	private FormatStyle convertStyleCharacter(char c) {
		switch (c) {
			case 'S': return FormatStyle.SHORT;
			case 'M': return FormatStyle.MEDIUM;
			case 'L': return FormatStyle.LONG;
			case 'F': return FormatStyle.FULL;
			case '-': return null;
			default: throw new IllegalArgumentException("Invalid style character '" + c + "'");
		}
	}

	/**
	 * Set the {@code TimeZone} to normalize the date values into, if any.
	 * <p>
	 *  设置{@code TimeZone}以将日期值归一化为(如果有)
	 * 
	 * 
	 * @param timeZone the time zone
	 */
	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}


	/**
	 * Create a new {@code DateTimeFormatter} using this factory.
	 * <p>If no specific pattern or style has been defined,
	 * {@link FormatStyle#MEDIUM medium date time format} will be used.
	 * <p>
	 *  使用这个工厂创建一个新的{@code DateTimeFormatter} <p>如果没有定义特定的模式或样式,将使用{@link FormatStyle#MEDIUM medium date time format}
	 * 。
	 * 
	 * 
	 * @return a new date time formatter
	 * @see #createDateTimeFormatter(DateTimeFormatter)
	 */
	public DateTimeFormatter createDateTimeFormatter() {
		return createDateTimeFormatter(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
	}

	/**
	 * Create a new {@code DateTimeFormatter} using this factory.
	 * <p>If no specific pattern or style has been defined,
	 * the supplied {@code fallbackFormatter} will be used.
	 * <p>
	 * 
	 * @param fallbackFormatter the fall-back formatter to use when no specific
	 * factory properties have been set (can be {@code null}).
	 * @return a new date time formatter
	 */
	public DateTimeFormatter createDateTimeFormatter(DateTimeFormatter fallbackFormatter) {
		DateTimeFormatter dateTimeFormatter = null;
		if (StringUtils.hasLength(this.pattern)) {
			// Using strict parsing to align with Joda-Time and standard DateFormat behavior:
			// otherwise, an overflow like e.g. Feb 29 for a non-leap-year wouldn't get rejected.
			// However, with strict parsing, a year digit needs to be specified as 'u'...
			String patternToUse = this.pattern.replace("yy", "uu");
			dateTimeFormatter = DateTimeFormatter.ofPattern(patternToUse).withResolverStyle(ResolverStyle.STRICT);
		}
		else if (this.iso != null && this.iso != ISO.NONE) {
			switch (this.iso) {
				case DATE:
					dateTimeFormatter = DateTimeFormatter.ISO_DATE;
					break;
				case TIME:
					dateTimeFormatter = DateTimeFormatter.ISO_TIME;
					break;
				case DATE_TIME:
					dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;
					break;
				case NONE:
					/* no-op */
					break;
				default:
					throw new IllegalStateException("Unsupported ISO format: " + this.iso);
			}
		}
		else if (this.dateStyle != null && this.timeStyle != null) {
			dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(this.dateStyle, this.timeStyle);
		}
		else if (this.dateStyle != null) {
			dateTimeFormatter = DateTimeFormatter.ofLocalizedDate(this.dateStyle);
		}
		else if (this.timeStyle != null) {
			dateTimeFormatter = DateTimeFormatter.ofLocalizedTime(this.timeStyle);
		}

		if (dateTimeFormatter != null && this.timeZone != null) {
			dateTimeFormatter = dateTimeFormatter.withZone(this.timeZone.toZoneId());
		}
		return (dateTimeFormatter != null ? dateTimeFormatter : fallbackFormatter);
	}

}
