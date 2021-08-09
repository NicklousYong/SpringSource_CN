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

package org.springframework.format.support;

import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.DateFormatterRegistrar;
import org.springframework.format.datetime.joda.JodaTimeFormatterRegistrar;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.format.number.money.CurrencyUnitFormatter;
import org.springframework.format.number.money.Jsr354NumberFormatAnnotationFormatterFactory;
import org.springframework.format.number.money.MonetaryAmountFormatter;
import org.springframework.format.number.NumberFormatAnnotationFormatterFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringValueResolver;

/**
 * A specialization of {@link FormattingConversionService} configured by default with
 * converters and formatters appropriate for most applications.
 *
 * <p>Designed for direct instantiation but also exposes the static {@link #addDefaultFormatters}
 * utility method for ad hoc use against any {@code FormatterRegistry} instance, just
 * as {@code DefaultConversionService} exposes its own
 * {@link DefaultConversionService#addDefaultConverters addDefaultConverters} method.
 *
 * <p>Automatically registers formatters for JSR-354 Money & Currency, JSR-310 Date-Time
 * and/or Joda-Time, depending on the presence of the corresponding API on the classpath.
 *
 * <p>
 *  默认情况下配置适用于大多数应用程序的转换器和格式化程序的{@link FormattingConversionService}的专业化
 * 
 * <p>设计用于直接实例化,但还暴露了静态{@link #addDefaultFormatters}实用程序,用于对任何{@code FormatterRegistry}实例进行临时使用,就像{@code DefaultConversionService}
 * 公开自己的{@link DefaultConversionService#addDefaultConverters addDefaultConverters } 方法。
 * 
 *  <p>根据类路径中相应API的存在,自动注册JSR-354 Money&Currency,JSR-310 Date-Time和/或Joda-Time的格式化程序
 * 
 * 
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 */
public class DefaultFormattingConversionService extends FormattingConversionService {

	private static final boolean jsr354Present = ClassUtils.isPresent(
			"javax.money.MonetaryAmount", DefaultFormattingConversionService.class.getClassLoader());

	private static final boolean jsr310Present = ClassUtils.isPresent(
			"java.time.LocalDate", DefaultFormattingConversionService.class.getClassLoader());

	private static final boolean jodaTimePresent = ClassUtils.isPresent(
			"org.joda.time.LocalDate", DefaultFormattingConversionService.class.getClassLoader());


	/**
	 * Create a new {@code DefaultFormattingConversionService} with the set of
	 * {@linkplain DefaultConversionService#addDefaultConverters default converters} and
	 * {@linkplain #addDefaultFormatters default formatters}.
	 * <p>
	 *  使用一组{@linkplain DefaultConversionService#addDefaultConverters默认转换器}和{@linkplain #addDefaultFormatters默认格式化程序}
	 * 创建一个新的{@code DefaultFormattingConversionService}。
	 * 
	 */
	public DefaultFormattingConversionService() {
		this(null, true);
	}

	/**
	 * Create a new {@code DefaultFormattingConversionService} with the set of
	 * {@linkplain DefaultConversionService#addDefaultConverters default converters} and,
	 * based on the value of {@code registerDefaultFormatters}, the set of
	 * {@linkplain #addDefaultFormatters default formatters}.
	 * <p>
	 * 使用{@linkplain DefaultConversionService#addDefaultConverters默认转换器}创建一个新的{@code DefaultFormattingConversionService}
	 * ,并且根据{@code registerDefaultFormatters}的值,{@linkplain #addDefaultFormatters默认格式化程序}。
	 * 
	 * 
	 * @param registerDefaultFormatters whether to register default formatters
	 */
	public DefaultFormattingConversionService(boolean registerDefaultFormatters) {
		this(null, registerDefaultFormatters);
	}

	/**
	 * Create a new {@code DefaultFormattingConversionService} with the set of
	 * {@linkplain DefaultConversionService#addDefaultConverters default converters} and,
	 * based on the value of {@code registerDefaultFormatters}, the set of
	 * {@linkplain #addDefaultFormatters default formatters}
	 * <p>
	 *  使用{@linkplain DefaultConversionService#addDefaultConverters默认转换器}创建一个新的{@code DefaultFormattingConversionService}
	 * ,并且根据{@code registerDefaultFormatters}的值,{@linkplain #addDefaultFormatters默认格式化程序}。
	 * 
	 * 
	 * @param embeddedValueResolver delegated to {@link #setEmbeddedValueResolver(StringValueResolver)}
	 * prior to calling {@link #addDefaultFormatters}.
	 * @param registerDefaultFormatters whether to register default formatters
	 */
	public DefaultFormattingConversionService(StringValueResolver embeddedValueResolver, boolean registerDefaultFormatters) {
		setEmbeddedValueResolver(embeddedValueResolver);
		DefaultConversionService.addDefaultConverters(this);
		if (registerDefaultFormatters) {
			addDefaultFormatters(this);
		}
	}


	/**
	 * Add formatters appropriate for most environments: including number formatters,
	 * JSR-354 Money & Currency formatters, JSR-310 Date-Time and/or Joda-Time formatters,
	 * depending on the presence of the corresponding API on the classpath.
	 * <p>
	 * 
	 * @param formatterRegistry the service to register default formatters with
	 */
	public static void addDefaultFormatters(FormatterRegistry formatterRegistry) {
		// Default handling of number values
		formatterRegistry.addFormatterForFieldAnnotation(new NumberFormatAnnotationFormatterFactory());

		// Default handling of monetary values
		if (jsr354Present) {
			formatterRegistry.addFormatter(new CurrencyUnitFormatter());
			formatterRegistry.addFormatter(new MonetaryAmountFormatter());
			formatterRegistry.addFormatterForFieldAnnotation(new Jsr354NumberFormatAnnotationFormatterFactory());
		}

		// Default handling of date-time values
		if (jsr310Present) {
			// just handling JSR-310 specific date and time types
			new DateTimeFormatterRegistrar().registerFormatters(formatterRegistry);
		}
		if (jodaTimePresent) {
			// handles Joda-specific types as well as Date, Calendar, Long
			new JodaTimeFormatterRegistrar().registerFormatters(formatterRegistry);
		}
		else {
			// regular DateFormat-based Date, Calendar, Long converters
			new DateFormatterRegistrar().registerFormatters(formatterRegistry);
		}
	}

}
