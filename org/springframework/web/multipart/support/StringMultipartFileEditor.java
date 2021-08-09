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

package org.springframework.web.multipart.support;

import java.beans.PropertyEditorSupport;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

/**
 * Custom {@link java.beans.PropertyEditor} for converting
 * {@link MultipartFile MultipartFiles} to Strings.
 *
 * <p>Allows one to specify the charset to use.
 *
 * <p>
 *  用于将{@link MultipartFile MultipartFiles}转换为字符串的自定义{@link javabeansPropertyEditor}
 * 
 *  <p>允许用户指定要使用的字符集
 * 
 * 
 * @author Juergen Hoeller
 * @since 13.10.2003
 */
public class StringMultipartFileEditor extends PropertyEditorSupport {

	private final String charsetName;


	/**
	 * Create a new {@link StringMultipartFileEditor}, using the default charset.
	 * <p>
	 * 使用默认字符集创建一个新的{@link StringMultipartFileEditor}
	 * 
	 */
	public StringMultipartFileEditor() {
		this.charsetName = null;
	}

	/**
	 * Create a new {@link StringMultipartFileEditor}, using the given charset.
	 * <p>
	 *  使用给定的字符集创建一个新的{@link StringMultipartFileEditor}
	 * 
	 * @param charsetName valid charset name
	 * @see java.lang.String#String(byte[],String)
	 */
	public StringMultipartFileEditor(String charsetName) {
		this.charsetName = charsetName;
	}


	@Override
	public void setAsText(String text) {
		setValue(text);
	}

	@Override
	public void setValue(Object value) {
		if (value instanceof MultipartFile) {
			MultipartFile multipartFile = (MultipartFile) value;
			try {
				super.setValue(this.charsetName != null ?
						new String(multipartFile.getBytes(), this.charsetName) :
						new String(multipartFile.getBytes()));
			}
			catch (IOException ex) {
				throw new IllegalArgumentException("Cannot read contents of multipart file", ex);
			}
		}
		else {
			super.setValue(value);
		}
	}

}
