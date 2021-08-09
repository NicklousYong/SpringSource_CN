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

package org.springframework.format.datetime.standard;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.Map;

import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.lang.UsesJava8;

/**
 * Configures the JSR-310 <code>java.time</code> formatting system for use with Spring.
 *
 * <p>
 *  配置与Spring一起使用的JSR-310 <code> javatime </code>格式化系统
 * 
 * 
 * @author Juergen Hoeller
 * @author Phillip Webb
 * @since 4.0
 * @see #setDateStyle
 * @see #setTimeStyle
 * @see #setDateTimeStyle
 * @see #setUseIsoFormat
 * @see org.springframework.format.FormatterRegistrar#registerFormatters
 * @see org.springframework.format.datetime.DateFormatterRegistrar
 * @see org.springframework.format.datetime.joda.DateTimeFormatterFactoryBean
 */
@UsesJava8
public class DateTimeFormatterRegistrar implements FormatterRegistrar {

	private enum Type {DATE, TIME, DATE_TIME}


	/**
	 * User defined formatters.
	 * <p>
	 *  用户定义的格式化程序
	 * 
	 */
	private final Map<Type, DateTimeFormatter> formatters = new HashMap<Type, DateTimeFormatter>();

	/**
	 * Factories used when specific formatters have not been specified.
	 * <p>
	 *  当没有指定特定格式化程序时使用的工厂
	 * 
	 */
	private final Map<Type, DateTimeFormatterFactory> factories;


	public DateTimeFormatterRegistrar() {
		this.factories = new HashMap<Type, DateTimeFormatterFactory>();
		for (Type type : Type.values()) {
			this.factories.put(type, new DateTimeFormatterFactory());
		}
	}


	/**
	 * Set whether standard ISO formatting should be applied to all date/time types.
	 * Default is "false" (no).
	 * <p>If set to "true", the "dateStyle", "timeStyle" and "dateTimeStyle"
	 * properties are effectively ignored.
	 * <p>
	 * 设置是否将标准ISO格式应用于所有日期/时间类型默认值为"false"(否)<p>如果设置为"true",则会有效地忽略"dateStyle","timeStyle"和"dateTimeStyle"属性
	 * 。
	 * 
	 */
	public void setUseIsoFormat(boolean useIsoFormat) {
		this.factories.get(Type.DATE).setIso(useIsoFormat ? ISO.DATE : null);
		this.factories.get(Type.TIME).setIso(useIsoFormat ? ISO.TIME : null);
		this.factories.get(Type.DATE_TIME).setIso(useIsoFormat ? ISO.DATE_TIME : null);
	}

	/**
	 * Set the default format style of {@link java.time.LocalDate} objects.
	 * Default is {@link java.time.format.FormatStyle#SHORT}.
	 * <p>
	 *  设置{@link javatimeLocalDate}对象的默认格式样式默认为{@link javatimeformatFormatStyle#SHORT}
	 * 
	 */
	public void setDateStyle(FormatStyle dateStyle) {
		this.factories.get(Type.DATE).setDateStyle(dateStyle);
	}

	/**
	 * Set the default format style of {@link java.time.LocalTime} objects.
	 * Default is {@link java.time.format.FormatStyle#SHORT}.
	 * <p>
	 *  设置{@link javatimeLocalTime}对象的默认格式样式默认为{@link javatimeformatFormatStyle#SHORT}
	 * 
	 */
	public void setTimeStyle(FormatStyle timeStyle) {
		this.factories.get(Type.TIME).setTimeStyle(timeStyle);
	}

	/**
	 * Set the default format style of {@link java.time.LocalDateTime} objects.
	 * Default is {@link java.time.format.FormatStyle#SHORT}.
	 * <p>
	 *  设置{@link javatimeLocalDateTime}对象的默认格式样式默认为{@link javatimeformatFormatStyle#SHORT}
	 * 
	 */
	public void setDateTimeStyle(FormatStyle dateTimeStyle) {
		this.factories.get(Type.DATE_TIME).setDateTimeStyle(dateTimeStyle);
	}

	/**
	 * Set the formatter that will be used for objects representing date values.
	 * <p>This formatter will be used for the {@link LocalDate} type.
	 * When specified, the {@link #setDateStyle dateStyle} and
	 * {@link #setUseIsoFormat useIsoFormat} properties will be ignored.
	 * <p>
	 * 设置将用于表示日期值的对象的格式化程序<p>此格式化程序将用于{@link LocalDate}类型指定时,{@link #setDateStyle dateStyle}和{@link #setUseIsoFormat useIsoFormat}
	 * 属性将忽视。
	 * 
	 * 
	 * @param formatter the formatter to use
	 * @see #setTimeFormatter
	 * @see #setDateTimeFormatter
	 */
	public void setDateFormatter(DateTimeFormatter formatter) {
		this.formatters.put(Type.DATE, formatter);
	}

	/**
	 * Set the formatter that will be used for objects representing time values.
	 * <p>This formatter will be used for the {@link LocalTime} and {@link OffsetTime}
	 * types. When specified, the {@link #setTimeStyle timeStyle} and
	 * {@link #setUseIsoFormat useIsoFormat} properties will be ignored.
	 * <p>
	 *  设置将用于表示时间值的对象的格式化程序<p>此格式化程序将用于{@link LocalTime}和{@link OffsetTime}类型指定时,{@link #setTimeStyle timeStyle}
	 * 和{@link# setUseIsoFormat useIsoFormat}属性将被忽略。
	 * 
	 * 
	 * @param formatter the formatter to use
	 * @see #setDateFormatter
	 * @see #setDateTimeFormatter
	 */
	public void setTimeFormatter(DateTimeFormatter formatter) {
		this.formatters.put(Type.TIME, formatter);
	}

	/**
	 * Set the formatter that will be used for objects representing date and time values.
	 * <p>This formatter will be used for {@link LocalDateTime}, {@link ZonedDateTime}
	 * and {@link OffsetDateTime} types. When specified, the
	 * {@link #setDateTimeStyle dateTimeStyle} and
	 * {@link #setUseIsoFormat useIsoFormat} properties will be ignored.
	 * <p>
	 * 设置将用于表示日期和时间值的对象的格式化程序<p>此格式化程序将用于{@link LocalDateTime},{@link ZonedDateTime}和{@link OffsetDateTime}类
	 * 型指定时,{@link #setDateTimeStyle dateTimeStyle}和{@link #setUseIsoFormat useIsoFormat}属性将被忽略。
	 * 
	 * @param formatter the formatter to use
	 * @see #setDateFormatter
	 * @see #setTimeFormatter
	 */
	public void setDateTimeFormatter(DateTimeFormatter formatter) {
		this.formatters.put(Type.DATE_TIME, formatter);
	}


	@Override
	public void registerFormatters(FormatterRegistry registry) {
		DateTimeConverters.registerConverters(registry);

		DateTimeFormatter dateFormatter = getFormatter(Type.DATE);
		DateTimeFormatter timeFormatter = getFormatter(Type.TIME);
		DateTimeFormatter dateTimeFormatter = getFormatter(Type.DATE_TIME);

		registry.addFormatterForFieldType(LocalDate.class,
				new TemporalAccessorPrinter(dateFormatter),
				new TemporalAccessorParser(LocalDate.class, dateFormatter));

		registry.addFormatterForFieldType(LocalTime.class,
				new TemporalAccessorPrinter(timeFormatter),
				new TemporalAccessorParser(LocalTime.class, timeFormatter));

		registry.addFormatterForFieldType(LocalDateTime.class,
				new TemporalAccessorPrinter(dateTimeFormatter),
				new TemporalAccessorParser(LocalDateTime.class, dateTimeFormatter));

		registry.addFormatterForFieldType(ZonedDateTime.class,
				new TemporalAccessorPrinter(dateTimeFormatter),
				new TemporalAccessorParser(ZonedDateTime.class, dateTimeFormatter));

		registry.addFormatterForFieldType(OffsetDateTime.class,
				new TemporalAccessorPrinter(dateTimeFormatter),
				new TemporalAccessorParser(OffsetDateTime.class, dateTimeFormatter));

		registry.addFormatterForFieldType(OffsetTime.class,
				new TemporalAccessorPrinter(timeFormatter),
				new TemporalAccessorParser(OffsetTime.class, timeFormatter));

		registry.addFormatterForFieldType(Instant.class, new InstantFormatter());
		registry.addFormatterForFieldType(Period.class, new PeriodFormatter());
		registry.addFormatterForFieldType(Duration.class, new DurationFormatter());
		registry.addFormatterForFieldType(YearMonth.class, new YearMonthFormatter());
		registry.addFormatterForFieldType(MonthDay.class, new MonthDayFormatter());

		registry.addFormatterForFieldAnnotation(new Jsr310DateTimeFormatAnnotationFormatterFactory());
	}

	private DateTimeFormatter getFormatter(Type type) {
		DateTimeFormatter formatter = this.formatters.get(type);
		if (formatter != null) {
			return formatter;
		}
		DateTimeFormatter fallbackFormatter = getFallbackFormatter(type);
		return this.factories.get(type).createDateTimeFormatter(fallbackFormatter);
	}

	private DateTimeFormatter getFallbackFormatter(Type type) {
		switch (type) {
			case DATE: return DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
			case TIME: return DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
			default: return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
		}
	}

}
