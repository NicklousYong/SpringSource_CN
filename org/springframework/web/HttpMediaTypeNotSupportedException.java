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

package org.springframework.web;

import java.util.List;

import org.springframework.http.MediaType;

/**
 * Exception thrown when a client POSTs, PUTs, or PATCHes content of a type
 * not supported by request handler.
 *
 * <p>
 *  当客户端POST,PUT或PATCHes请求处理程序不支持的类型的内容时抛出异常
 * 
 * 
 * @author Arjen Poutsma
 * @since 3.0
 */
@SuppressWarnings("serial")
public class HttpMediaTypeNotSupportedException extends HttpMediaTypeException {

	private final MediaType contentType;


	/**
	 * Create a new HttpMediaTypeNotSupportedException.
	 * <p>
	 *  创建一个新的HttpMediaTypeNotSupportedException
	 * 
	 * 
	 * @param message the exception message
	 */
	public HttpMediaTypeNotSupportedException(String message) {
		super(message);
		this.contentType = null;
	}

	/**
	 * Create a new HttpMediaTypeNotSupportedException.
	 * <p>
	 *  创建一个新的HttpMediaTypeNotSupportedException
	 * 
	 * 
	 * @param contentType the unsupported content type
	 * @param supportedMediaTypes the list of supported media types
	 */
	public HttpMediaTypeNotSupportedException(MediaType contentType, List<MediaType> supportedMediaTypes) {
		this(contentType, supportedMediaTypes, "Content type '" + contentType + "' not supported");
	}

	/**
	 * Create a new HttpMediaTypeNotSupportedException.
	 * <p>
	 * 创建一个新的HttpMediaTypeNotSupportedException
	 * 
	 * 
	 * @param contentType the unsupported content type
	 * @param supportedMediaTypes the list of supported media types
	 * @param msg the detail message
	 */
	public HttpMediaTypeNotSupportedException(MediaType contentType, List<MediaType> supportedMediaTypes, String msg) {
		super(msg, supportedMediaTypes);
		this.contentType = contentType;
	}


	/**
	 * Return the HTTP request content type method that caused the failure.
	 * <p>
	 *  返回导致失败的HTTP请求内容类型方法
	 */
	public MediaType getContentType() {
		return this.contentType;
	}

}
