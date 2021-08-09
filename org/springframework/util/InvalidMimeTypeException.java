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

package org.springframework.util;

/**
 * Exception thrown from {@link MimeTypeUtils#parseMimeType(String)} in case of
 * encountering an invalid content type specification String.
 *
 * <p>
 *  遇到无效内容类型规范时,从{@link MimeTypeUtils#parseMimeType(String)}抛出的异常String
 * 
 * 
 * @author Juergen Hoeller
 * @author Rossen Stoyanchev
 * @since 4.0
 */
@SuppressWarnings("serial")
public class InvalidMimeTypeException extends IllegalArgumentException {

	private String mimeType;


	/**
	 * Create a new InvalidContentTypeException for the given content type.
	 * <p>
	 *  为给定的内容类型创建一个新的InvalidContentTypeException
	 * 
	 * 
	 * @param mimeType the offending media type
	 * @param message a detail message indicating the invalid part
	 */
	public InvalidMimeTypeException(String mimeType, String message) {
		super("Invalid mime type \"" + mimeType + "\": " + message);
		this.mimeType = mimeType;

	}


	/**
	 * Return the offending content type.
	 * <p>
	 * 返回违规内容类型
	 */
	public String getMimeType() {
		return this.mimeType;
	}

}
