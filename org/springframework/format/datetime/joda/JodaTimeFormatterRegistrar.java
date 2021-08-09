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

package org.springframework.format.datetime.joda;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.MonthDay;
import org.joda.time.Period;
import org.joda.time.ReadableInstant;
import org.joda.time.YearMonth;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.Parser;
import org.springframework.format.Printer;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.util.ClassUtils;

/**
 * Configures Joda-Time's formatting system for use with Spring.
 *
 * <p><b>NOTE:</b> Spring's Joda-Time support requires Joda-Time 2.x, as of Spring 4.0.
 *
 * <p>
 *  配置Joda-Time的格式系统与Spring一起使用
 * 
 *  <p> <b>注意：</b> Spring的Joda-Time支持需要Joda-Time 2x,截至春季40
 * 
 * 
 * @author Keith Donald
 * @author Juergen Hoeller
 * @author Phillip Webb
 * @since 3.1
 * @see #setDateStyle
 * @see #setTimeStyle
 * @see #setDateTimeStyle
 * @see #setUseIsoFormat
 * @see FormatterRegistrar#registerFormatters
 * @see org.springframework.format.datetime.DateFormatterRegistrar
 * @see DateTimeFormatterFactoryBean
 */
public class JodaTimeFormatterRegistrar implements FormatterRegistrar {

	private enum Type {DATE, TIME, DATE_TIME}


	/**
	 * Strictly speaking, this should not be necessary since we formally require JodaTime 2.x.
	 * However, since Joda-Time formatters are being registered automatically, we defensively
	 * adapt to Joda-Time 1.x when encountered on the classpath. To be removed in Spring 5.0.
	 * <p>
	 * 严格来说,这不是必要的,因为我们正式要求JodaTime 2x但是,由于Joda-Time格式化程序正在自动注册,所以我们在Classpath遇到时防守适应Joda-Time 1x要在Spring 50
	 * 中删除。
	 * 
	 */
	private static final boolean jodaTime2Available = ClassUtils.isPresent(
			"org.joda.time.YearMonth", JodaTimeFormatterRegistrar.class.getClassLoader());

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


	public JodaTimeFormatterRegistrar() {
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
	 *  设置是否将标准ISO格式应用于所有日期/时间类型默认值为"false"(否)<p>如果设置为"true",则会有效地忽略"dateStyle","timeStyle"和"dateTimeStyle"属
	 * 性。
	 * 
	 */
	public void setUseIsoFormat(boolean useIsoFormat) {
		this.factories.get(Type.DATE).setIso(useIsoFormat ? ISO.DATE : null);
		this.factories.get(Type.TIME).setIso(useIsoFormat ? ISO.TIME : null);
		this.factories.get(Type.DATE_TIME).setIso(useIsoFormat ? ISO.DATE_TIME : null);
	}

	/**
	 * Set the default format style of Joda {@link LocalDate} objects.
	 * Default is {@link DateTimeFormat#shortDate()}.
	 * <p>
	 *  设置Joda {@link LocalDate}对象的默认格式样式默认为{@link DateTimeFormat#shortDate()}
	 * 
	 */
	public void setDateStyle(String dateStyle) {
		this.factories.get(Type.DATE).setStyle(dateStyle + "-");
	}

	/**
	 * Set the default format style of Joda {@link LocalTime} objects.
	 * Default is {@link DateTimeFormat#shortTime()}.
	 * <p>
	 *  设置Joda {@link LocalTime}对象的默认格式样式默认为{@link DateTimeFormat#shortTime()}
	 * 
	 */
	public void setTimeStyle(String timeStyle) {
		this.factories.get(Type.TIME).setStyle("-" + timeStyle);
	}

	/**
	 * Set the default format style of Joda {@link LocalDateTime} and {@link DateTime} objects,
	 * as well as JDK {@link Date} and {@link Calendar} objects.
	 * Default is {@link DateTimeFormat#shortDateTime()}.
	 * <p>
	 * 设置Joda {@link LocalDateTime}和{@link DateTime}对象的默认格式样式,以及JDK {@link Date}和{@link Calendar}对象的默认格式为{@link DateTimeFormat#shortDateTime()}
	 * 。
	 * 
	 */
	public void setDateTimeStyle(String dateTimeStyle) {
		this.factories.get(Type.DATE_TIME).setStyle(dateTimeStyle);
	}

	/**
	 * Set the formatter that will be used for objects representing date values.
	 * <p>This formatter will be used for the {@link LocalDate} type. When specified
	 * the {@link #setDateStyle(String) dateStyle} and
	 * {@link #setUseIsoFormat(boolean) useIsoFormat} properties will be ignored.
	 * <p>
	 *  设置将用于表示日期值的对象的格式化程序<p>此格式化程序将用于{@link LocalDate}类型指定{@link #setDateStyle(String)dateStyle}和{@link #setUseIsoFormat(boolean) useIsoFormat}
	 * 属性将被忽略。
	 * 
	 * 
	 * @param formatter the formatter to use
	 * @since 3.2
	 * @see #setTimeFormatter
	 * @see #setDateTimeFormatter
	 */
	public void setDateFormatter(DateTimeFormatter formatter) {
		this.formatters.put(Type.DATE, formatter);
	}

	/**
	 * Set the formatter that will be used for objects representing time values.
	 * <p>This formatter will be used for the {@link LocalTime} type. When specified
	 * the {@link #setTimeStyle(String) timeStyle} and
	 * {@link #setUseIsoFormat(boolean) useIsoFormat} properties will be ignored.
	 * <p>
	 *  设置将用于表示时间值的对象的格式化程序<p>此格式化程序将用于{@link LocalTime}类型指定{@link #setTimeStyle(String)timeStyle}和{@link #setUseIsoFormat(boolean) useIsoFormat}
	 * 属性将被忽略。
	 * 
	 * 
	 * @param formatter the formatter to use
	 * @since 3.2
	 * @see #setDateFormatter
	 * @see #setDateTimeFormatter
	 */
	public void setTimeFormatter(DateTimeFormatter formatter) {
		this.formatters.put(Type.TIME, formatter);
	}

	/**
	 * Set the formatter that will be used for objects representing date and time values.
	 * <p>This formatter will be used for {@link LocalDateTime}, {@link ReadableInstant},
	 * {@link Date} and {@link Calendar} types. When specified
	 * the {@link #setDateTimeStyle(String) dateTimeStyle} and
	 * {@link #setUseIsoFormat(boolean) useIsoFormat} properties will be ignored.
	 * <p>
	 * 设置将用于表示日期和时间值的对象的格式化程序<p>此格式化程序将用于{@link LocalDateTime},{@link可读维护},{@link日期}和{@link日历}类型指定时{@link #setDateTimeStyle(String)dateTimeStyle}
	 * 和{@link #setUseIsoFormat(boolean)useIsoFormat}属性将被忽略。
	 * 
	 * 
	 * @param formatter the formatter to use
	 * @since 3.2
	 * @see #setDateFormatter
	 * @see #setTimeFormatter
	 */
	public void setDateTimeFormatter(DateTimeFormatter formatter) {
		this.formatters.put(Type.DATE_TIME, formatter);
	}


	@Override
	public void registerFormatters(FormatterRegistry registry) {
		JodaTimeConverters.registerConverters(registry);

		DateTimeFormatter dateFormatter = getFormatter(Type.DATE);
		DateTimeFormatter timeFormatter = getFormatter(Type.TIME);
		DateTimeFormatter dateTimeFormatter = getFormatter(Type.DATE_TIME);

		addFormatterForFields(registry,
				new ReadablePartialPrinter(dateFormatter),
				new LocalDateParser(dateFormatter),
				LocalDate.class);

		addFormatterForFields(registry,
				new ReadablePartialPrinter(timeFormatter),
				new LocalTimeParser(timeFormatter),
				LocalTime.class);

		addFormatterForFields(registry,
				new ReadablePartialPrinter(dateTimeFormatter),
				new LocalDateTimeParser(dateTimeFormatter),
				LocalDateTime.class);

		addFormatterForFields(registry,
				new ReadableInstantPrinter(dateTimeFormatter),
				new DateTimeParser(dateTimeFormatter),
				ReadableInstant.class);

		// In order to retain backwards compatibility we only register Date/Calendar
		// types when a user defined formatter is specified (see SPR-10105)
		if (this.formatters.containsKey(Type.DATE_TIME)) {
			addFormatterForFields(registry,
					new ReadableInstantPrinter(dateTimeFormatter),
					new DateTimeParser(dateTimeFormatter),
					Date.class, Calendar.class);
		}

		registry.addFormatterForFieldType(Period.class, new PeriodFormatter());
		registry.addFormatterForFieldType(Duration.class, new DurationFormatter());
		if (jodaTime2Available) {
			JodaTime2Delegate.registerAdditionalFormatters(registry);
		}

		registry.addFormatterForFieldAnnotation(new JodaDateTimeFormatAnnotationFormatterFactory());
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
			case DATE: return DateTimeFormat.shortDate();
			case TIME: return DateTimeFormat.shortTime();
			default: return DateTimeFormat.shortDateTime();
		}
	}

	private void addFormatterForFields(FormatterRegistry registry, Printer<?> printer,
			Parser<?> parser, Class<?>... fieldTypes) {

		for (Class<?> fieldType : fieldTypes) {
			registry.addFormatterForFieldType(fieldType, printer, parser);
		}
	}


	/**
	 * Inner class to avoid a hard dependency on Joda-Time 2.x.
	 * <p>
	 */
	private static class JodaTime2Delegate {

		public static void registerAdditionalFormatters(FormatterRegistry registry) {
			registry.addFormatterForFieldType(YearMonth.class, new YearMonthFormatter());
			registry.addFormatterForFieldType(MonthDay.class, new MonthDayFormatter());
		}
	}

}
