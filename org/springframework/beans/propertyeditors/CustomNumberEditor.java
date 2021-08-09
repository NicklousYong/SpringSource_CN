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
import java.text.NumberFormat;

import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

/**
 * Property editor for any Number subclass such as Short, Integer, Long,
 * BigInteger, Float, Double, BigDecimal. Can use a given NumberFormat for
 * (locale-specific) parsing and rendering, or alternatively the default
 * {@code decode} / {@code valueOf} / {@code toString} methods.
 *
 * <p>This is not meant to be used as system PropertyEditor but rather
 * as locale-specific number editor within custom controller code,
 * parsing user-entered number strings into Number properties of beans
 * and rendering them in the UI form.
 *
 * <p>In web MVC code, this editor will typically be registered with
 * {@code binder.registerCustomEditor} calls.
 *
 * <p>
 * 任何Number子类的属性编辑器,如Short,Integer,Long,BigInteger,Float,Double,BigDecimal可以使用给定的NumberFormat(特定于语言环境)进行
 * 解析和渲染,或者使用默认的{@code decode} / {@code valueOf } / {@code toString}方法。
 * 
 *  <p>这不意味着用作系统PropertyEditor,而是作为自定义控制器代码中的特定于区域设置的编号编辑器,将用户输入的数字字符串解析为Bean的Number属性并将其呈现在UI窗体中
 * 
 *  <p>在Web MVC代码中,此编辑器通常会使用{@code binderregisterCustomEditor}注册
 * 
 * 
 * @author Juergen Hoeller
 * @since 06.06.2003
 * @see Number
 * @see java.text.NumberFormat
 * @see org.springframework.validation.DataBinder#registerCustomEditor
 */
public class CustomNumberEditor extends PropertyEditorSupport {

	private final Class<? extends Number> numberClass;

	private final NumberFormat numberFormat;

	private final boolean allowEmpty;


	/**
	 * Create a new CustomNumberEditor instance, using the default
	 * {@code valueOf} methods for parsing and {@code toString}
	 * methods for rendering.
	 * <p>The "allowEmpty" parameter states if an empty String should
	 * be allowed for parsing, i.e. get interpreted as {@code null} value.
	 * Else, an IllegalArgumentException gets thrown in that case.
	 * <p>
	 * 创建一个新的CustomNumberEditor实例,使用默认的{@code valueOf}方法进行解析和{@code toString}方法进行渲染<p>"allowEmpty"参数指定是否允许空字
	 * 符串进行解析,即解释为{ @code null} value Else,在这种情况下抛出一个IllegalArgumentException。
	 * 
	 * 
	 * @param numberClass Number subclass to generate
	 * @param allowEmpty if empty strings should be allowed
	 * @throws IllegalArgumentException if an invalid numberClass has been specified
	 * @see org.springframework.util.NumberUtils#parseNumber(String, Class)
	 * @see Integer#valueOf
	 * @see Integer#toString
	 */
	public CustomNumberEditor(Class<? extends Number> numberClass, boolean allowEmpty) throws IllegalArgumentException {
		this(numberClass, null, allowEmpty);
	}

	/**
	 * Create a new CustomNumberEditor instance, using the given NumberFormat
	 * for parsing and rendering.
	 * <p>The allowEmpty parameter states if an empty String should
	 * be allowed for parsing, i.e. get interpreted as {@code null} value.
	 * Else, an IllegalArgumentException gets thrown in that case.
	 * <p>
	 *  创建一个新的CustomNumberEditor实例,使用给定的NumberFormat进行解析和呈现<p> allowEmpty参数说明如果允许一个空字符串进行解析,即解释为{@code null}
	 *  value Else,则在这种情况下抛出一个IllegalArgumentException。
	 * 
	 * 
	 * @param numberClass Number subclass to generate
	 * @param numberFormat NumberFormat to use for parsing and rendering
	 * @param allowEmpty if empty strings should be allowed
	 * @throws IllegalArgumentException if an invalid numberClass has been specified
	 * @see org.springframework.util.NumberUtils#parseNumber(String, Class, java.text.NumberFormat)
	 * @see java.text.NumberFormat#parse
	 * @see java.text.NumberFormat#format
	 */
	public CustomNumberEditor(Class<? extends Number> numberClass,
			NumberFormat numberFormat, boolean allowEmpty) throws IllegalArgumentException {

		if (numberClass == null || !Number.class.isAssignableFrom(numberClass)) {
			throw new IllegalArgumentException("Property class must be a subclass of Number");
		}
		this.numberClass = numberClass;
		this.numberFormat = numberFormat;
		this.allowEmpty = allowEmpty;
	}


	/**
	 * Parse the Number from the given text, using the specified NumberFormat.
	 * <p>
	 *  使用指定的NumberFormat解析给定文本中的Number
	 * 
	 */
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (this.allowEmpty && !StringUtils.hasText(text)) {
			// Treat empty String as null value.
			setValue(null);
		}
		else if (this.numberFormat != null) {
			// Use given NumberFormat for parsing text.
			setValue(NumberUtils.parseNumber(text, this.numberClass, this.numberFormat));
		}
		else {
			// Use default valueOf methods for parsing text.
			setValue(NumberUtils.parseNumber(text, this.numberClass));
		}
	}

	/**
	 * Coerce a Number value into the required target class, if necessary.
	 * <p>
	 *  如果需要,将数值强制到所需的目标类中
	 * 
	 */
	@Override
	public void setValue(Object value) {
		if (value instanceof Number) {
			super.setValue(NumberUtils.convertNumberToTargetClass((Number) value, this.numberClass));
		}
		else {
			super.setValue(value);
		}
	}

	/**
	 * Format the Number as String, using the specified NumberFormat.
	 * <p>
	 * 使用指定的NumberFormat将Number格式化为String
	 */
	@Override
	public String getAsText() {
		Object value = getValue();
		if (value == null) {
			return "";
		}
		if (this.numberFormat != null) {
			// Use NumberFormat for rendering value.
			return this.numberFormat.format(value);
		}
		else {
			// Use toString method for rendering value.
			return value.toString();
		}
	}

}
