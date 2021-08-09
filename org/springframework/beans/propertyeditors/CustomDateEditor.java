/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2012 the original author or authors.
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

package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import org.springframework.util.StringUtils;

/**
 * Property editor for {@code java.util.Date},
 * supporting a custom {@code java.text.DateFormat}.
 *
 * <p>This is not meant to be used as system PropertyEditor but rather
 * as locale-specific date editor within custom controller code,
 * parsing user-entered number strings into Date properties of beans
 * and rendering them in the UI form.
 *
 * <p>In web MVC code, this editor will typically be registered with
 * {@code binder.registerCustomEditor}.
 *
 * <p>
 *  {@code javautilDate}的属性编辑器,支持自定义{@code javatextDateFormat}
 * 
 * <p>这不意味着用作系统PropertyEditor,而是用作自定义控制器代码中的特定于区域设置的日期编辑器,将用户输入的数字字符串解析为Bean的Date属性并将其呈现在UI窗体中
 * 
 *  <p>在Web MVC代码中,此编辑器通常会注册到{@code binderregisterCustomEditor}
 * 
 * 
 * @author Juergen Hoeller
 * @since 28.04.2003
 * @see java.util.Date
 * @see java.text.DateFormat
 * @see org.springframework.validation.DataBinder#registerCustomEditor
 */
public class CustomDateEditor extends PropertyEditorSupport {

	private final DateFormat dateFormat;

	private final boolean allowEmpty;

	private final int exactDateLength;


	/**
	 * Create a new CustomDateEditor instance, using the given DateFormat
	 * for parsing and rendering.
	 * <p>The "allowEmpty" parameter states if an empty String should
	 * be allowed for parsing, i.e. get interpreted as null value.
	 * Otherwise, an IllegalArgumentException gets thrown in that case.
	 * <p>
	 *  创建一个新的CustomDateEditor实例,使用给定的DateFormat进行解析和呈现<p>"allowEmpty"参数指定是否允许空字符串进行解析,即将其解释为空值否则,会引发Illegal
	 * ArgumentException异常。
	 * 
	 * 
	 * @param dateFormat DateFormat to use for parsing and rendering
	 * @param allowEmpty if empty strings should be allowed
	 */
	public CustomDateEditor(DateFormat dateFormat, boolean allowEmpty) {
		this.dateFormat = dateFormat;
		this.allowEmpty = allowEmpty;
		this.exactDateLength = -1;
	}

	/**
	 * Create a new CustomDateEditor instance, using the given DateFormat
	 * for parsing and rendering.
	 * <p>The "allowEmpty" parameter states if an empty String should
	 * be allowed for parsing, i.e. get interpreted as null value.
	 * Otherwise, an IllegalArgumentException gets thrown in that case.
	 * <p>The "exactDateLength" parameter states that IllegalArgumentException gets
	 * thrown if the String does not exactly match the length specified. This is useful
	 * because SimpleDateFormat does not enforce strict parsing of the year part,
	 * not even with {@code setLenient(false)}. Without an "exactDateLength"
	 * specified, the "01/01/05" would get parsed to "01/01/0005". However, even
	 * with an "exactDateLength" specified, prepended zeros in the day or month
	 * part may still allow for a shorter year part, so consider this as just
	 * one more assertion that gets you closer to the intended date format.
	 * <p>
	 * 创建一个新的CustomDateEditor实例,使用给定的DateFormat进行解析和呈现<p>"allowEmpty"参数说明如果一个空字符串应该被允许用于解析,即被解释为空值否则,在这种情况下抛
	 * 出一个IllegalArgumentException >"exactDateLength"参数指出如果String与指定的长度不完全匹配,则会抛出IllegalArgumentException。
	 * 这是有用的,因为SimpleDateFormat不强制严格解析年份部分,甚至不使用{@code setLenient(false)}没有" exactDateLength","01/01/05"将被解析
	 * 为"01/01/0005"然而,即使指定了"exactDateLength",日期或月份部分中的前置零仍可能允许较短的一部分,因此,将其视为只能让您更接近预期日期格式的一个断言。
	 * 
	 * 
	 * @param dateFormat DateFormat to use for parsing and rendering
	 * @param allowEmpty if empty strings should be allowed
	 * @param exactDateLength the exact expected length of the date String
	 */
	public CustomDateEditor(DateFormat dateFormat, boolean allowEmpty, int exactDateLength) {
		this.dateFormat = dateFormat;
		this.allowEmpty = allowEmpty;
		this.exactDateLength = exactDateLength;
	}


	/**
	 * Parse the Date from the given text, using the specified DateFormat.
	 * <p>
	 * 使用指定的DateFormat解析给定文本中的Date
	 * 
	 */
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (this.allowEmpty && !StringUtils.hasText(text)) {
			// Treat empty String as null value.
			setValue(null);
		}
		else if (text != null && this.exactDateLength >= 0 && text.length() != this.exactDateLength) {
			throw new IllegalArgumentException(
					"Could not parse date: it is not exactly" + this.exactDateLength + "characters long");
		}
		else {
			try {
				setValue(this.dateFormat.parse(text));
			}
			catch (ParseException ex) {
				throw new IllegalArgumentException("Could not parse date: " + ex.getMessage(), ex);
			}
		}
	}

	/**
	 * Format the Date as String, using the specified DateFormat.
	 * <p>
	 *  使用指定的DateFormat格式化日期为字符串
	 */
	@Override
	public String getAsText() {
		Date value = (Date) getValue();
		return (value != null ? this.dateFormat.format(value) : "");
	}

}
