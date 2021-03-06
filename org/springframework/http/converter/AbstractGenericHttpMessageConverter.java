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

package org.springframework.http.converter;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.StreamingHttpOutputMessage;

/**
 * Abstract base class for most {@link GenericHttpMessageConverter} implementations.
 *
 * <p>
 *  大多数{@link GenericHttpMessageConverter}实现的抽象基类
 * 
 * 
 * @author Sebastien Deleuze
 * @since 4.2
 */
public abstract class AbstractGenericHttpMessageConverter<T> extends AbstractHttpMessageConverter<T>
		implements GenericHttpMessageConverter<T> {

	/**
	 * Construct an {@code AbstractGenericHttpMessageConverter} with no supported media types.
	 * <p>
	 *  构造一个没有支持的媒体类型的{@code AbstractGenericHttpMessageConverter}
	 * 
	 * 
	 * @see #setSupportedMediaTypes
	 */
	protected AbstractGenericHttpMessageConverter() {
	}

	/**
	 * Construct an {@code AbstractGenericHttpMessageConverter} with one supported media type.
	 * <p>
	 * 构造一个支持的媒体类型的{@code AbstractGenericHttpMessageConverter}
	 * 
	 * 
	 * @param supportedMediaType the supported media type
	 */
	protected AbstractGenericHttpMessageConverter(MediaType supportedMediaType) {
		super(supportedMediaType);
	}

	/**
	 * Construct an {@code AbstractGenericHttpMessageConverter} with multiple supported media type.
	 * <p>
	 *  构造具有多种支持的媒体类型的{@code AbstractGenericHttpMessageConverter}
	 * 
	 * 
	 * @param supportedMediaTypes the supported media types
	 */
	protected AbstractGenericHttpMessageConverter(MediaType... supportedMediaTypes) {
		super(supportedMediaTypes);
	}


	@Override
	public boolean canRead(Type type, Class<?> contextClass, MediaType mediaType) {
		return canRead(contextClass, mediaType);
	}

	@Override
	public boolean canWrite(Type type, Class<?> clazz, MediaType mediaType) {
		return canWrite(clazz, mediaType);
	}

	/**
	 * This implementation sets the default headers by calling {@link #addDefaultHeaders},
	 * and then calls {@link #writeInternal}.
	 * <p>
	 *  此实现通过调用{@link #addDefaultHeaders}来设置默认标头,然后调用{@link #writeInternal}
	 * 
	 */
	public final void write(final T t, final Type type, MediaType contentType, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {

		final HttpHeaders headers = outputMessage.getHeaders();
		addDefaultHeaders(headers, t, contentType);

		if (outputMessage instanceof StreamingHttpOutputMessage) {
			StreamingHttpOutputMessage streamingOutputMessage = (StreamingHttpOutputMessage) outputMessage;
			streamingOutputMessage.setBody(new StreamingHttpOutputMessage.Body() {
				@Override
				public void writeTo(final OutputStream outputStream) throws IOException {
					writeInternal(t, type, new HttpOutputMessage() {
						@Override
						public OutputStream getBody() throws IOException {
							return outputStream;
						}
						@Override
						public HttpHeaders getHeaders() {
							return headers;
						}
					});
				}
			});
		}
		else {
			writeInternal(t, type, outputMessage);
			outputMessage.getBody().flush();
		}
	}


	@Override
	protected void writeInternal(T t, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {

		writeInternal(t, null, outputMessage);
	}

	/**
	 * Abstract template method that writes the actual body. Invoked from {@link #write}.
	 * <p>
	 *  抽象模板方法写入实体从{@link #write}调用
	 * 
	 * @param t the object to write to the output message
	 * @param type the type of object to write, can be {@code null} if not specified.
	 * @param outputMessage the HTTP output message to write to
	 * @throws IOException in case of I/O errors
	 * @throws HttpMessageNotWritableException in case of conversion errors
	 */
	protected abstract void writeInternal(T t, Type type, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException;

}
