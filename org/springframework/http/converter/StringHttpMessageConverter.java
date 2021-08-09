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

package org.springframework.http.converter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;

/**
 * Implementation of {@link HttpMessageConverter} that can read and write strings.
 *
 * <p>By default, this converter supports all media types ({@code &#42;&#47;&#42;}),
 * and writes with a {@code Content-Type} of {@code text/plain}. This can be overridden
 * by setting the {@link #setSupportedMediaTypes supportedMediaTypes} property.
 *
 * <p>
 *  实现可以读写字符串的{@link HttpMessageConverter}
 * 
 * <p>默认情况下,此转换器支持所有媒体类型({@code * / *}),并使用{@code text / plain}的{@code Content-Type}进行写入可以通过设置{@链接#setSupportedMediaTypes supportedMediaTypes}
 * 属性。
 * 
 * 
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @since 3.0
 */
public class StringHttpMessageConverter extends AbstractHttpMessageConverter<String> {

	public static final Charset DEFAULT_CHARSET = Charset.forName("ISO-8859-1");


	private final List<Charset> availableCharsets;

	private boolean writeAcceptCharset = true;


	/**
	 * A default constructor that uses {@code "ISO-8859-1"} as the default charset.
	 * <p>
	 *  使用{@code"ISO-8859-1"}作为默认字符集的默认构造函数
	 * 
	 * 
	 * @see #StringHttpMessageConverter(Charset)
	 */
	public StringHttpMessageConverter() {
		this(DEFAULT_CHARSET);
	}

	/**
	 * A constructor accepting a default charset to use if the requested content
	 * type does not specify one.
	 * <p>
	 *  如果请求的内容类型没有指定一个,则接受默认字符集使用的构造函数
	 * 
	 */
	public StringHttpMessageConverter(Charset defaultCharset) {
		super(defaultCharset, MediaType.TEXT_PLAIN, MediaType.ALL);
		this.availableCharsets = new ArrayList<Charset>(Charset.availableCharsets().values());
	}


	/**
	 * Indicates whether the {@code Accept-Charset} should be written to any outgoing request.
	 * <p>Default is {@code true}.
	 * <p>
	 *  指示是否应将{@code Accept-Charset}写入任何外发请求<p>默认值为{@code true}
	 * 
	 */
	public void setWriteAcceptCharset(boolean writeAcceptCharset) {
		this.writeAcceptCharset = writeAcceptCharset;
	}


	@Override
	public boolean supports(Class<?> clazz) {
		return String.class == clazz;
	}

	@Override
	protected String readInternal(Class<? extends String> clazz, HttpInputMessage inputMessage) throws IOException {
		Charset charset = getContentTypeCharset(inputMessage.getHeaders().getContentType());
		return StreamUtils.copyToString(inputMessage.getBody(), charset);
	}

	@Override
	protected Long getContentLength(String str, MediaType contentType) {
		Charset charset = getContentTypeCharset(contentType);
		try {
			return (long) str.getBytes(charset.name()).length;
		}
		catch (UnsupportedEncodingException ex) {
			// should not occur
			throw new IllegalStateException(ex);
		}
	}

	@Override
	protected void writeInternal(String str, HttpOutputMessage outputMessage) throws IOException {
		if (this.writeAcceptCharset) {
			outputMessage.getHeaders().setAcceptCharset(getAcceptedCharsets());
		}
		Charset charset = getContentTypeCharset(outputMessage.getHeaders().getContentType());
		StreamUtils.copy(str, charset, outputMessage.getBody());
	}


	/**
	 * Return the list of supported {@link Charset}s.
	 * <p>By default, returns {@link Charset#availableCharsets()}.
	 * Can be overridden in subclasses.
	 * <p>
	 *  返回支持的{@link Charset}的列表<p>默认情况下,返回{@link Charset#availableCharsets()}可以在子类中覆盖
	 * 
	 * @return the list of accepted charsets
	 */
	protected List<Charset> getAcceptedCharsets() {
		return this.availableCharsets;
	}

	private Charset getContentTypeCharset(MediaType contentType) {
		if (contentType != null && contentType.getCharset() != null) {
			return contentType.getCharset();
		}
		else {
			return getDefaultCharset();
		}
	}

}
