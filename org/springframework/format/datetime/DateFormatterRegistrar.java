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

package org.springframework.format.datetime;

import java.util.Calendar;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.util.Assert;

/**
 * Configures Date formatting for use with Spring.
 *
 * <p>Designed for direct instantiation but also exposes the static
 * {@link #addDateConverters(ConverterRegistry)} utility method for ad hoc use
 * against any {@code ConverterRegistry} instance.
 *
 * <p>
 *  配置与Spring一起使用的日期格式
 * 
 * <p>设计用于直接实例化,但还暴露了静态{@link #addDateConverters(ConverterRegistry)}实用方法,用于对任何{@code ConverterRegistry}实
 * 例进行临时使用。
 * 
 * 
 * @author Phillip Webb
 * @since 3.2
 * @see org.springframework.format.datetime.joda.JodaTimeFormatterRegistrar
 * @see FormatterRegistrar#registerFormatters
 */
public class DateFormatterRegistrar implements FormatterRegistrar {

	private DateFormatter dateFormatter;


	/**
	 * Set the date formatter to register. If not specified no formatter is registered.
	 * This method can be used if global formatter configuration is required.
	 * <p>
	 *  将日期格式化程序设置为注册如果未指定,则不指定格式化程序如果需要全局格式化程序配置,则可以使用此方法
	 * 
	 * 
	 * @param dateFormatter the date formatter
	 */
	public void setFormatter(DateFormatter dateFormatter) {
		Assert.notNull(dateFormatter, "DateFormatter must not be null");
		this.dateFormatter = dateFormatter;
	}


	@Override
	public void registerFormatters(FormatterRegistry registry) {
		addDateConverters(registry);
		registry.addFormatterForFieldAnnotation(new DateTimeFormatAnnotationFormatterFactory());

		// In order to retain back compatibility we only register Date/Calendar
		// types when a user defined formatter is specified (see SPR-10105)
		if (this.dateFormatter != null) {
			registry.addFormatter(this.dateFormatter);
			registry.addFormatterForFieldType(Calendar.class, this.dateFormatter);
		}
	}

	/**
	 * Add date converters to the specified registry.
	 * <p>
	 *  将日期转换器添加到指定的注册表
	 * 
	 * @param converterRegistry the registry of converters to add to
	 */
	public static void addDateConverters(ConverterRegistry converterRegistry) {
		converterRegistry.addConverter(new DateToLongConverter());
		converterRegistry.addConverter(new DateToCalendarConverter());
		converterRegistry.addConverter(new CalendarToDateConverter());
		converterRegistry.addConverter(new CalendarToLongConverter());
		converterRegistry.addConverter(new LongToDateConverter());
		converterRegistry.addConverter(new LongToCalendarConverter());
	}


	private static class DateToLongConverter implements Converter<Date, Long> {

		@Override
		public Long convert(Date source) {
			return source.getTime();
		}
	}


	private static class DateToCalendarConverter implements Converter<Date, Calendar> {

		@Override
		public Calendar convert(Date source) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(source);
			return calendar;
		}
	}


	private static class CalendarToDateConverter implements Converter<Calendar, Date> {

		@Override
		public Date convert(Calendar source) {
			return source.getTime();
		}
	}


	private static class CalendarToLongConverter implements Converter<Calendar, Long> {

		@Override
		public Long convert(Calendar source) {
			return source.getTimeInMillis();
		}
	}


	private static class LongToDateConverter implements Converter<Long, Date> {

		@Override
		public Date convert(Long source) {
			return new Date(source);
		}
	}


	private static class LongToCalendarConverter implements Converter<Long, Calendar> {

		@Override
		public Calendar convert(Long source) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(source);
			return calendar;
		}
	}

}
