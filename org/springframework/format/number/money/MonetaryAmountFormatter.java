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

package org.springframework.format.number.money;

import java.util.Locale;
import javax.money.MonetaryAmount;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;

import org.springframework.format.Formatter;

/**
 * Formatter for JSR-354 {@link javax.money.MonetaryAmount} values,
 * delegating to {@link javax.money.format.MonetaryAmountFormat#format}
 * and {@link javax.money.format.MonetaryAmountFormat#parse}.
 *
 * <p>
 *  对于JSR-354 {@link javaxmoneyMonetaryAmount}值的Formatter,委托给{@link javaxmoneyformatMonetaryAmountFormat#format}
 * 和{@link javaxmoneyformatMonetaryAmountFormat#parse}。
 * 
 * 
 * @author Juergen Hoeller
 * @since 4.2
 * @see #getMonetaryAmountFormat
 */
public class MonetaryAmountFormatter implements Formatter<MonetaryAmount> {

	private String formatName;


	/**
	 * Create a locale-driven MonetaryAmountFormatter.
	 * <p>
	 * 创建一个由区域设置驱动的MonetaryAmountFormatter
	 * 
	 */
	public MonetaryAmountFormatter() {
	}

	/**
	 * Create a new MonetaryAmountFormatter for the given format name.
	 * <p>
	 *  为给定的格式名称创建一个新的MonetaryAmountFormatter
	 * 
	 * 
	 * @param formatName the format name, to be resolved by the JSR-354
	 * provider at runtime
	 */
	public MonetaryAmountFormatter(String formatName) {
		this.formatName = formatName;
	}


	/**
	 * Specify the format name, to be resolved by the JSR-354 provider
	 * at runtime.
	 * <p>Default is none, obtaining a {@link MonetaryAmountFormat}
	 * based on the current locale.
	 * <p>
	 *  指定在运行时由JSR-354提供程序解析的格式名称<p>默认值为none,根据当前语言环境获取{@link MonetaryAmountFormat}
	 * 
	 */
	public void setFormatName(String formatName) {
		this.formatName = formatName;
	}


	@Override
	public String print(MonetaryAmount object, Locale locale) {
		return getMonetaryAmountFormat(locale).format(object);
	}

	@Override
	public MonetaryAmount parse(String text, Locale locale) {
		return getMonetaryAmountFormat(locale).parse(text);
	}


	/**
	 * Obtain a MonetaryAmountFormat for the given locale.
	 * <p>The default implementation simply calls
	 * {@link javax.money.format.MonetaryFormats#getAmountFormat}
	 * with either the configured format name or the given locale.
	 * <p>
	 *  获取给定语言环境的MonetaryAmountFormat <p>默认实现只需使用配置的格式名称或给定的语言环境调用{@link javaxmoneyformatMonetaryFormats#getAmountFormat}
	 * 。
	 * 
	 * @param locale the current locale
	 * @return the MonetaryAmountFormat (never {@code null})
	 * @see #setFormatName
	 */
	protected MonetaryAmountFormat getMonetaryAmountFormat(Locale locale) {
		if (this.formatName != null) {
			return MonetaryFormats.getAmountFormat(this.formatName);
		}
		else {
			return MonetaryFormats.getAmountFormat(locale);
		}
	}

}
