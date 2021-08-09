/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2014 the original author or authors.
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

import org.springframework.util.StringUtils;

/**
 * Editor for a {@link Character}, to populate a property
 * of type {@code Character} or {@code char} from a String value.
 *
 * <p>Note that the JDK does not contain a default
 * {@link java.beans.PropertyEditor property editor} for {@code char}!
 * {@link org.springframework.beans.BeanWrapperImpl} will register this
 * editor by default.
 *
 * <p>Also supports conversion from a Unicode character sequence; e.g.
 * {@code u0041} ('A').
 *
 * <p>
 *  编辑{@link Character},从String值填充类型为{@code Character}或{@code char}的属性
 * 
 * <p>请注意,JDK不包含{@code char}的默认{@link javabeansPropertyEditor属性编辑器}！默认情况下,{@link orgspringframeworkbeansBeanWrapperImpl}
 * 将注册此编辑器。
 * 
 *  <p>还支持从Unicode字符序列转换;例如{@code u0041}('A')
 * 
 * 
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Rick Evans
 * @since 1.2
 * @see Character
 * @see org.springframework.beans.BeanWrapperImpl
 */
public class CharacterEditor extends PropertyEditorSupport {

	/**
	 * The prefix that identifies a string as being a Unicode character sequence.
	 * <p>
	 *  将字符串标识为Unicode字符序列的前缀
	 * 
	 */
	private static final String UNICODE_PREFIX = "\\u";

	/**
	 * The length of a Unicode character sequence.
	 * <p>
	 *  Unicode字符序列的长度
	 * 
	 */
	private static final int UNICODE_LENGTH = 6;


	private final boolean allowEmpty;


	/**
	 * Create a new CharacterEditor instance.
	 * <p>The "allowEmpty" parameter controls whether an empty String is to be
	 * allowed in parsing, i.e. be interpreted as the {@code null} value when
	 * {@link #setAsText(String) text is being converted}. If {@code false},
	 * an {@link IllegalArgumentException} will be thrown at that time.
	 * <p>
	 *  创建一个新的CharacterEditor实例<p>"allowEmpty"参数控制在解析时是否允许一个空字符串,即当{@link #setAsText(String)文本被转换时)解释为{@code null}
	 * 值}如果{@code false},那么当时将抛出一个{@link IllegalArgumentException}。
	 * 
	 * @param allowEmpty if empty strings are to be allowed
	 */
	public CharacterEditor(boolean allowEmpty) {
		this.allowEmpty = allowEmpty;
	}


	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (this.allowEmpty && !StringUtils.hasLength(text)) {
			// Treat empty String as null value.
			setValue(null);
		}
		else if (text == null) {
			throw new IllegalArgumentException("null String cannot be converted to char type");
		}
		else if (isUnicodeCharacterSequence(text)) {
			setAsUnicode(text);
		}
		else if (text.length() == 1) {
			setValue(Character.valueOf(text.charAt(0)));
		}
		else {
			throw new IllegalArgumentException("String [" + text + "] with length " +
					text.length() + " cannot be converted to char type: neither Unicode nor single character");
		}
	}

	@Override
	public String getAsText() {
		Object value = getValue();
		return (value != null ? value.toString() : "");
	}


	private boolean isUnicodeCharacterSequence(String sequence) {
		return (sequence.startsWith(UNICODE_PREFIX) && sequence.length() == UNICODE_LENGTH);
	}

	private void setAsUnicode(String text) {
		int code = Integer.parseInt(text.substring(UNICODE_PREFIX.length()), 16);
		setValue(Character.valueOf((char) code));
	}

}
