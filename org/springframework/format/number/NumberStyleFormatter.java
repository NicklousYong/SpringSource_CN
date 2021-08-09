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

package org.springframework.format.number;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * A general-purpose number formatter using NumberFormat's number style.
 *
 * <p>Delegates to {@link java.text.NumberFormat#getInstance(Locale)}.
 * Configures BigDecimal parsing so there is no loss in precision.
 * Allows configuration over the decimal number pattern.
 * The {@link #parse(String, Locale)} routine always returns a BigDecimal.
 *
 * <p>
 *  使用NumberFormat的数字样式的通用数字格式化程序
 * 
 * <p>代表{@link javatextNumberFormat#getInstance(Locale)}配置BigDecimal解析,所以精度没有损失允许配置超过十进制数模式{@link #parse(String,Locale)}
 * 例程总是返回一个BigDecimal。
 * 
 * 
 * @author Keith Donald
 * @author Juergen Hoeller
 * @since 4.2
 * @see #setPattern
 * @see #setLenient
 */
public class NumberStyleFormatter extends AbstractNumberFormatter {

	private String pattern;


	/**
	 * Create a new NumberStyleFormatter without a pattern.
	 * <p>
	 *  创建一个没有模式的新的NumberStyleFormatter
	 * 
	 */
	public NumberStyleFormatter() {
	}

	/**
	 * Create a new NumberStyleFormatter with the specified pattern.
	 * <p>
	 *  用指定的模式创建一个新的NumberStyleFormatter
	 * 
	 * 
	 * @param pattern the format pattern
	 * @see #setPattern
	 */
	public NumberStyleFormatter(String pattern) {
		this.pattern = pattern;
	}


	/**
	 * Sets the pattern to use to format number values.
	 * If not specified, the default DecimalFormat pattern is used.
	 * <p>
	 *  设置用于格式化数字值的模式如果未指定,则使用默认的DecimalFormat模式
	 * 
	 * @see java.text.DecimalFormat#applyPattern(String)
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}


	@Override
	public NumberFormat getNumberFormat(Locale locale) {
		NumberFormat format = NumberFormat.getInstance(locale);
		if (!(format instanceof DecimalFormat)) {
			if (this.pattern != null) {
				throw new IllegalStateException("Cannot support pattern for non-DecimalFormat: " + format);
			}
			return format;
		}
		DecimalFormat decimalFormat = (DecimalFormat) format;
		decimalFormat.setParseBigDecimal(true);
		if (this.pattern != null) {
			decimalFormat.applyPattern(this.pattern);
		}
		return decimalFormat;
	}

}
