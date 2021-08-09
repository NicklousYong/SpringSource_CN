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

package org.springframework.format.datetime.joda;

import java.util.TimeZone;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.util.StringUtils;

/**
 * Factory that creates a Joda-Time {@link DateTimeFormatter}.
 *
 * <p>Formatters will be created using the defined {@link #setPattern pattern},
 * {@link #setIso ISO}, and {@link #setStyle style} methods (considered in that order).
 *
 * <p>
 *  创建Joda-Time {@link DateTimeFormatter}的工厂
 * 
 *  <p>使用定义的{@link #setPattern模式},{@link #setIso ISO}和{@link #setStyle style}方法(以该顺序考虑)创建格式化程序。
 * 
 * 
 * @author Phillip Webb
 * @author Sam Brannen
 * @since 3.2
 * @see #createDateTimeFormatter()
 * @see #createDateTimeFormatter(DateTimeFormatter)
 * @see #setPattern
 * @see #setStyle
 * @see #setIso
 * @see DateTimeFormatterFactoryBean
 */
public class DateTimeFormatterFactory {

	private String pattern;

	private ISO iso;

	private String style;

	private TimeZone timeZone;


	/**
	 * Create a new {@code DateTimeFormatterFactory} instance.
	 * <p>
	 * 创建一个新的{@code DateTimeFormatterFactory}实例
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
	 * <p>
	 *  设置用于格式化日期值的两个字符,在Joda-Time样式中<p>第一个字符用于日期样式;第二个是时间风格支持的字符是：
	 * <ul>
	 *  <li>'S'=小</li> <li>'M'=中</li> <li>'L'= Long </li> <li>'F'= Full </li> >' - '=略过</li>
	 * </ul>
	 * 
	 * @param style two characters from the set {"S", "M", "L", "F", "-"}
	 */
	public void setStyle(String style) {
		this.style = style;
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
	 * {@link DateTimeFormat#mediumDateTime() medium date time format} will be used.
	 * <p>
	 *  使用这个工厂创建一个新的{@code DateTimeFormatter} <p>如果没有定义特定的模式或样式,将使用{@link DateTimeFormat#mediumDateTime()medium date time format}
	 * 。
	 * 
	 * 
	 * @return a new date time formatter
	 * @see #createDateTimeFormatter(DateTimeFormatter)
	 */
	public DateTimeFormatter createDateTimeFormatter() {
		return createDateTimeFormatter(DateTimeFormat.mediumDateTime());
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
			dateTimeFormatter = DateTimeFormat.forPattern(this.pattern);
		}
		else if (this.iso != null && this.iso != ISO.NONE) {
			switch (this.iso) {
				case DATE:
					dateTimeFormatter = ISODateTimeFormat.date();
					break;
				case TIME:
					dateTimeFormatter = ISODateTimeFormat.time();
					break;
				case DATE_TIME:
					dateTimeFormatter = ISODateTimeFormat.dateTime();
					break;
				case NONE:
					/* no-op */
					break;
				default:
					throw new IllegalStateException("Unsupported ISO format: " + this.iso);
			}
		}
		else if (StringUtils.hasLength(this.style)) {
			dateTimeFormatter = DateTimeFormat.forStyle(this.style);
		}

		if (dateTimeFormatter != null && this.timeZone != null) {
			dateTimeFormatter = dateTimeFormatter.withZone(DateTimeZone.forTimeZone(this.timeZone));
		}
		return (dateTimeFormatter != null ? dateTimeFormatter : fallbackFormatter);
	}

}
