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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Currency;
import java.util.Locale;

/**
 * A BigDecimal formatter for number values in currency style.
 *
 * <p>Delegates to {@link java.text.NumberFormat#getCurrencyInstance(Locale)}.
 * Configures BigDecimal parsing so there is no loss of precision.
 * Can apply a specified {@link java.math.RoundingMode} to parsed values.
 *
 * <p>
 *  一个用于货币风格的数值的BigDecimal格式化程序
 * 
 * <p> {@link javatextNumberFormat#getCurrencyInstance(Locale)}的代理)配置BigDecimal解析,因此没有精度损失可以将指定的{@link javamathRoundingMode}
 * 应用于解析的值。
 * 
 * 
 * @author Keith Donald
 * @author Juergen Hoeller
 * @since 4.2
 * @see #setLenient
 * @see #setRoundingMode
 */
public class CurrencyStyleFormatter extends AbstractNumberFormatter {

	private int fractionDigits = 2;

	private RoundingMode roundingMode;

	private Currency currency;

	private String pattern;


	/**
	 * Specify the desired number of fraction digits.
	 * Default is 2.
	 * <p>
	 *  指定所需的小数位数,默认值为2
	 * 
	 */
	public void setFractionDigits(int fractionDigits) {
		this.fractionDigits = fractionDigits;
	}

	/**
	 * Specify the rounding mode to use for decimal parsing.
	 * Default is {@link java.math.RoundingMode#UNNECESSARY}.
	 * <p>
	 *  指定用于十进制解析的舍入模式默认为{@link javamathRoundingMode#UNNECESSARY}
	 * 
	 */
	public void setRoundingMode(RoundingMode roundingMode) {
		this.roundingMode = roundingMode;
	}

	/**
	 * Specify the currency, if known.
	 * <p>
	 *  指定货币(如果已知)
	 * 
	 */
	public void setCurrency(Currency currency) {
		this.currency = currency;
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
	public BigDecimal parse(String text, Locale locale) throws ParseException {
		BigDecimal decimal = (BigDecimal) super.parse(text, locale);
		if (decimal != null) {
			if (this.roundingMode != null) {
				decimal = decimal.setScale(this.fractionDigits, this.roundingMode);
			}
			else {
				decimal = decimal.setScale(this.fractionDigits);
			}
		}
		return decimal;
	}

	@Override
	protected NumberFormat getNumberFormat(Locale locale) {
		DecimalFormat format = (DecimalFormat) NumberFormat.getCurrencyInstance(locale);
		format.setParseBigDecimal(true);
		format.setMaximumFractionDigits(this.fractionDigits);
		format.setMinimumFractionDigits(this.fractionDigits);
		if (this.roundingMode != null) {
			format.setRoundingMode(this.roundingMode);
		}
		if (this.currency != null) {
			format.setCurrency(this.currency);
		}
		if (this.pattern != null) {
			format.applyPattern(this.pattern);
		}
		return format;
	}

}
