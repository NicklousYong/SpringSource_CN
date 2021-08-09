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
import java.nio.charset.Charset;

import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;

/**
 * An {@code HttpMessageConverter} that uses {@link StringHttpMessageConverter}
 * for reading and writing content and a {@link ConversionService} for converting
 * the String content to and from the target object type.
 *
 * <p>By default, this converter supports the media type {@code text/plain} only.
 * This can be overridden through the {@link #setSupportedMediaTypes supportedMediaTypes}
 * property.
 *
 * <p>A usage example:
 *
 * <pre class="code">
 * &lt;bean class="org.springframework.http.converter.ObjectToStringHttpMessageConverter">
 *   &lt;constructor-arg>
 *     &lt;bean class="org.springframework.context.support.ConversionServiceFactoryBean"/>
 *   &lt;/constructor-arg>
 * &lt;/bean>
 * </pre>
 *
 * <p>
 *  使用{@link StringHttpMessageConverter}读取和写入内容的{@code HttpMessageConverter}和用于将String内容转换为目标对象类型和从目标对象类
 * 型转换的{@link ConversionService}。
 * 
 * <p>默认情况下,此转换器仅支持媒体类型{@code text / plain}。
 * 可以通过{@link #setSupportedMediaTypes supportedMediaTypes}属性覆盖它。
 * 
 *  <p>使用示例：
 * 
 * <pre class="code">
 * &lt;bean class="org.springframework.http.converter.ObjectToStringHttpMessageConverter">
 * &lt;constructor-arg>
 * &lt;bean class="org.springframework.context.support.ConversionServiceFactoryBean"/>
 * &lt;/constructor-arg>
 * 
 * @author <a href="mailto:dmitry.katsubo@gmail.com">Dmitry Katsubo</a>
 * @author Rossen Stoyanchev
 * @since 3.2
 */
public class ObjectToStringHttpMessageConverter extends AbstractHttpMessageConverter<Object> {

	private final ConversionService conversionService;

	private final StringHttpMessageConverter stringHttpMessageConverter;


	/**
	 * A constructor accepting a {@code ConversionService} to use to convert the
	 * (String) message body to/from the target class type. This constructor uses
	 * {@link StringHttpMessageConverter#DEFAULT_CHARSET} as the default charset.
	 * <p>
	 * &lt;/bean>
	 * </pre>
	 * 
	 * 
	 * @param conversionService the conversion service
	 */
	public ObjectToStringHttpMessageConverter(ConversionService conversionService) {
		this(conversionService, StringHttpMessageConverter.DEFAULT_CHARSET);
	}

	/**
	 * A constructor accepting a {@code ConversionService} as well as a default charset.
	 * <p>
	 *  接受{@code ConversionService}用于将(String)消息体转换为/从目标类类型的构造方法此构造函数使用{@link StringHttpMessageConverter#DEFAULT_CHARSET}
	 * 作为默认字符集。
	 * 
	 * 
	 * @param conversionService the conversion service
	 * @param defaultCharset the default charset
	 */
	public ObjectToStringHttpMessageConverter(ConversionService conversionService, Charset defaultCharset) {
		super(defaultCharset, MediaType.TEXT_PLAIN);

		Assert.notNull(conversionService, "ConversionService is required");
		this.conversionService = conversionService;
		this.stringHttpMessageConverter = new StringHttpMessageConverter(defaultCharset);
	}


	/**
	 * Indicates whether the {@code Accept-Charset} should be written to any outgoing request.
	 * <p>Default is {@code true}.
	 * <p>
	 *  接受{@code ConversionService}的构造函数以及默认字符集
	 * 
	 */
	public void setWriteAcceptCharset(boolean writeAcceptCharset) {
		this.stringHttpMessageConverter.setWriteAcceptCharset(writeAcceptCharset);
	}


	@Override
	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		return this.conversionService.canConvert(String.class, clazz) && canRead(mediaType);
	}

	@Override
	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		return this.conversionService.canConvert(clazz, String.class) && canWrite(mediaType);
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		// should not be called, since we override canRead/Write
		throw new UnsupportedOperationException();
	}

	@Override
	protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException {
		String value = this.stringHttpMessageConverter.readInternal(String.class, inputMessage);
		return this.conversionService.convert(value, clazz);
	}

	@Override
	protected void writeInternal(Object obj, HttpOutputMessage outputMessage) throws IOException {
		String value = this.conversionService.convert(obj, String.class);
		this.stringHttpMessageConverter.writeInternal(value, outputMessage);
	}

	@Override
	protected Long getContentLength(Object obj, MediaType contentType) {
		String value = this.conversionService.convert(obj, String.class);
		return this.stringHttpMessageConverter.getContentLength(value, contentType);
	}

}
