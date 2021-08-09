/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2016 the original author or authors.
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

package org.springframework.core.style;

import org.springframework.util.Assert;

/**
 * Utility class that builds pretty-printing {@code toString()} methods
 * with pluggable styling conventions. By default, ToStringCreator adheres
 * to Spring's {@code toString()} styling conventions.
 *
 * <p>
 *  使用可插拔样式约定构建漂亮打印{@code toString()}方法的实用程序类默认情况下,ToStringCreator遵守Spring的{@code toString()}样式约定
 * 
 * 
 * @author Keith Donald
 * @author Juergen Hoeller
 * @since 1.2.2
 */
public class ToStringCreator {

	/**
	 * Default ToStringStyler instance used by this ToStringCreator.
	 * <p>
	 * 此ToStringCreator使用的默认ToStringStyler实例
	 * 
	 */
	private static final ToStringStyler DEFAULT_TO_STRING_STYLER =
			new DefaultToStringStyler(StylerUtils.DEFAULT_VALUE_STYLER);


	private final StringBuilder buffer = new StringBuilder(256);

	private final ToStringStyler styler;

	private final Object object;

	private boolean styledFirstField;


	/**
	 * Create a ToStringCreator for the given object.
	 * <p>
	 *  为给定的对象创建ToStringCreator
	 * 
	 * 
	 * @param obj the object to be stringified
	 */
	public ToStringCreator(Object obj) {
		this(obj, (ToStringStyler) null);
	}

	/**
	 * Create a ToStringCreator for the given object, using the provided style.
	 * <p>
	 *  使用提供的样式为给定对象创建ToStringCreator
	 * 
	 * 
	 * @param obj the object to be stringified
	 * @param styler the ValueStyler encapsulating pretty-print instructions
	 */
	public ToStringCreator(Object obj, ValueStyler styler) {
		this(obj, new DefaultToStringStyler(styler != null ? styler : StylerUtils.DEFAULT_VALUE_STYLER));
	}

	/**
	 * Create a ToStringCreator for the given object, using the provided style.
	 * <p>
	 *  使用提供的样式为给定对象创建ToStringCreator
	 * 
	 * 
	 * @param obj the object to be stringified
	 * @param styler the ToStringStyler encapsulating pretty-print instructions
	 */
	public ToStringCreator(Object obj, ToStringStyler styler) {
		Assert.notNull(obj, "The object to be styled must not be null");
		this.object = obj;
		this.styler = (styler != null ? styler : DEFAULT_TO_STRING_STYLER);
		this.styler.styleStart(this.buffer, this.object);
	}


	/**
	 * Append a byte field value.
	 * <p>
	 *  追加字节字段值
	 * 
	 * 
	 * @param fieldName the name of the field, usually the member variable name
	 * @param value the field value
	 * @return this, to support call-chaining
	 */
	public ToStringCreator append(String fieldName, byte value) {
		return append(fieldName, Byte.valueOf(value));
	}

	/**
	 * Append a short field value.
	 * <p>
	 *  追加一个短的字段值
	 * 
	 * 
	 * @param fieldName the name of the field, usually the member variable name
	 * @param value the field value
	 * @return this, to support call-chaining
	 */
	public ToStringCreator append(String fieldName, short value) {
		return append(fieldName, Short.valueOf(value));
	}

	/**
	 * Append a integer field value.
	 * <p>
	 *  追加整数字段值
	 * 
	 * 
	 * @param fieldName the name of the field, usually the member variable name
	 * @param value the field value
	 * @return this, to support call-chaining
	 */
	public ToStringCreator append(String fieldName, int value) {
		return append(fieldName, Integer.valueOf(value));
	}

	/**
	 * Append a long field value.
	 * <p>
	 *  追加长字段值
	 * 
	 * 
	 * @param fieldName the name of the field, usually the member variable name
	 * @param value the field value
	 * @return this, to support call-chaining
	 */
	public ToStringCreator append(String fieldName, long value) {
		return append(fieldName, Long.valueOf(value));
	}

	/**
	 * Append a float field value.
	 * <p>
	 *  追加一个浮点值
	 * 
	 * 
	 * @param fieldName the name of the field, usually the member variable name
	 * @param value the field value
	 * @return this, to support call-chaining
	 */
	public ToStringCreator append(String fieldName, float value) {
		return append(fieldName, Float.valueOf(value));
	}

	/**
	 * Append a double field value.
	 * <p>
	 *  追加一个双字段值
	 * 
	 * 
	 * @param fieldName the name of the field, usually the member variable name
	 * @param value the field value
	 * @return this, to support call-chaining
	 */
	public ToStringCreator append(String fieldName, double value) {
		return append(fieldName, Double.valueOf(value));
	}

	/**
	 * Append a boolean field value.
	 * <p>
	 *  追加一个布尔字段值
	 * 
	 * 
	 * @param fieldName the name of the field, usually the member variable name
	 * @param value the field value
	 * @return this, to support call-chaining
	 */
	public ToStringCreator append(String fieldName, boolean value) {
		return append(fieldName, Boolean.valueOf(value));
	}

	/**
	 * Append a field value.
	 * <p>
	 *  追加一个字段值
	 * 
	 * 
	 * @param fieldName the name of the field, usually the member variable name
	 * @param value the field value
	 * @return this, to support call-chaining
	 */
	public ToStringCreator append(String fieldName, Object value) {
		printFieldSeparatorIfNecessary();
		this.styler.styleField(this.buffer, fieldName, value);
		return this;
	}

	private void printFieldSeparatorIfNecessary() {
		if (this.styledFirstField) {
			this.styler.styleFieldSeparator(this.buffer);
		}
		else {
			this.styledFirstField = true;
		}
	}

	/**
	 * Append the provided value.
	 * <p>
	 *  追加提供的值
	 * 
	 * 
	 * @param value The value to append
	 * @return this, to support call-chaining.
	 */
	public ToStringCreator append(Object value) {
		this.styler.styleValue(this.buffer, value);
		return this;
	}


	/**
	 * Return the String representation that this ToStringCreator built.
	 * <p>
	 *  返回此ToStringCreator构建的String表示形式
	 */
	@Override
	public String toString() {
		this.styler.styleEnd(this.buffer, this.object);
		return this.buffer.toString();
	}

}
