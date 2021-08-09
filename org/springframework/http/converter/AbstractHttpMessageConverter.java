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
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.util.Assert;

/**
 * Abstract base class for most {@link HttpMessageConverter} implementations.
 *
 * <p>This base class adds support for setting supported {@code MediaTypes}, through the
 * {@link #setSupportedMediaTypes(List) supportedMediaTypes} bean property. It also adds
 * support for {@code Content-Type} and {@code Content-Length} when writing to output messages.
 *
 * <p>
 *  大多数{@link HttpMessageConverter}实现的抽象基类
 * 
 * <p>此基础类通过{@link #setSupportedMediaTypes(List)supportedMediaTypes} bean属性添加对支持的{@code MediaTypes}设置的支持
 * 。
 * 它还添加了对{@code Content-Type}和{@code Content-Length }写入输出消息时。
 * 
 * 
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @author Sebastien Deleuze
 * @since 3.0
 */
public abstract class AbstractHttpMessageConverter<T> implements HttpMessageConverter<T> {

	/** Logger available to subclasses */
	protected final Log logger = LogFactory.getLog(getClass());

	private List<MediaType> supportedMediaTypes = Collections.emptyList();

	private Charset defaultCharset;


	/**
	 * Construct an {@code AbstractHttpMessageConverter} with no supported media types.
	 * <p>
	 *  构造一个不支持媒体类型的{@code AbstractHttpMessageConverter}
	 * 
	 * 
	 * @see #setSupportedMediaTypes
	 */
	protected AbstractHttpMessageConverter() {
	}

	/**
	 * Construct an {@code AbstractHttpMessageConverter} with one supported media type.
	 * <p>
	 *  使用一种支持的媒体类型构建{@code AbstractHttpMessageConverter}
	 * 
	 * 
	 * @param supportedMediaType the supported media type
	 */
	protected AbstractHttpMessageConverter(MediaType supportedMediaType) {
		setSupportedMediaTypes(Collections.singletonList(supportedMediaType));
	}

	/**
	 * Construct an {@code AbstractHttpMessageConverter} with multiple supported media types.
	 * <p>
	 *  构造具有多种支持的媒体类型的{@code AbstractHttpMessageConverter}
	 * 
	 * 
	 * @param supportedMediaTypes the supported media types
	 */
	protected AbstractHttpMessageConverter(MediaType... supportedMediaTypes) {
		setSupportedMediaTypes(Arrays.asList(supportedMediaTypes));
	}

	/**
	 * Construct an {@code AbstractHttpMessageConverter} with a default charset and
	 * multiple supported media types.
	 * <p>
	 *  使用默认字符集和多种支持的媒体类型构造{@code AbstractHttpMessageConverter}
	 * 
	 * 
	 * @param defaultCharset the default character set
	 * @param supportedMediaTypes the supported media types
	 * @since 4.3
	 */
	protected AbstractHttpMessageConverter(Charset defaultCharset, MediaType... supportedMediaTypes) {
		this.defaultCharset = defaultCharset;
		setSupportedMediaTypes(Arrays.asList(supportedMediaTypes));
	}


	/**
	 * Set the list of {@link MediaType} objects supported by this converter.
	 * <p>
	 *  设置此转换器支持的{@link MediaType}对象列表
	 * 
	 */
	public void setSupportedMediaTypes(List<MediaType> supportedMediaTypes) {
		Assert.notEmpty(supportedMediaTypes, "'supportedMediaTypes' must not be empty");
		this.supportedMediaTypes = new ArrayList<MediaType>(supportedMediaTypes);
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return Collections.unmodifiableList(this.supportedMediaTypes);
	}

	/**
	 * Set the default character set, if any.
	 * <p>
	 *  设置默认字符集(如果有)
	 * 
	 * 
	 * @since 4.3
	 */
	public void setDefaultCharset(Charset defaultCharset) {
		this.defaultCharset = defaultCharset;
	}

	/**
	 * Return the default character set, if any.
	 * <p>
	 *  返回默认字符集(如果有)
	 * 
	 * 
	 * @since 4.3
	 */
	public Charset getDefaultCharset() {
		return this.defaultCharset;
	}


	/**
	 * This implementation checks if the given class is {@linkplain #supports(Class) supported},
	 * and if the {@linkplain #getSupportedMediaTypes() supported media types}
	 * {@linkplain MediaType#includes(MediaType) include} the given media type.
	 * <p>
	 * 此实现检查给定类是否支持{@linkplain #supports(Class)},如果{@linkplain #getSupportedMediaTypes()支持的媒体类型} {@linkplain MediaType#includes(MediaType)include}
	 * 给定的媒体类型。
	 * 
	 */
	@Override
	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		return supports(clazz) && canRead(mediaType);
	}

	/**
	 * Returns {@code true} if any of the {@linkplain #setSupportedMediaTypes(List)
	 * supported} media types {@link MediaType#includes(MediaType) include} the
	 * given media type.
	 * <p>
	 *  如果{@linkplain #setSupportedMediaTypes(List)支持的}媒体类型{@link MediaType#includes(MediaType)include}包含给定的
	 * 媒体类型,则返回{@code true}。
	 * 
	 * 
	 * @param mediaType the media type to read, can be {@code null} if not specified.
	 * Typically the value of a {@code Content-Type} header.
	 * @return {@code true} if the supported media types include the media type,
	 * or if the media type is {@code null}
	 */
	protected boolean canRead(MediaType mediaType) {
		if (mediaType == null) {
			return true;
		}
		for (MediaType supportedMediaType : getSupportedMediaTypes()) {
			if (supportedMediaType.includes(mediaType)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This implementation checks if the given class is
	 * {@linkplain #supports(Class) supported}, and if the
	 * {@linkplain #getSupportedMediaTypes() supported} media types
	 * {@linkplain MediaType#includes(MediaType) include} the given media type.
	 * <p>
	 *  此实现检查给定类是否支持{@linkplain #supports(Class)},如果{@linkplain #getSupportedMediaTypes()支持}媒体类型{@linkplain MediaType#includes(MediaType)include}
	 * )给定的媒体类型。
	 * 
	 */
	@Override
	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		return supports(clazz) && canWrite(mediaType);
	}

	/**
	 * Returns {@code true} if the given media type includes any of the
	 * {@linkplain #setSupportedMediaTypes(List) supported media types}.
	 * <p>
	 *  如果给定的媒体类型包含任何{@linkplain #setSupportedMediaTypes(List)支持的媒体类型),则返回{@code true}
	 * 
	 * 
	 * @param mediaType the media type to write, can be {@code null} if not specified.
	 * Typically the value of an {@code Accept} header.
	 * @return {@code true} if the supported media types are compatible with the media type,
	 * or if the media type is {@code null}
	 */
	protected boolean canWrite(MediaType mediaType) {
		if (mediaType == null || MediaType.ALL.equals(mediaType)) {
			return true;
		}
		for (MediaType supportedMediaType : getSupportedMediaTypes()) {
			if (supportedMediaType.isCompatibleWith(mediaType)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This implementation simple delegates to {@link #readInternal(Class, HttpInputMessage)}.
	 * Future implementations might add some default behavior, however.
	 * <p>
	 * 这个实现简单地委托给{@link #readInternal(Class,HttpInputMessage)}未来的实现可能会添加一些默认行为,但是
	 * 
	 */
	@Override
	public final T read(Class<? extends T> clazz, HttpInputMessage inputMessage) throws IOException {
		return readInternal(clazz, inputMessage);
	}

	/**
	 * This implementation sets the default headers by calling {@link #addDefaultHeaders},
	 * and then calls {@link #writeInternal}.
	 * <p>
	 *  此实现通过调用{@link #addDefaultHeaders}来设置默认标头,然后调用{@link #writeInternal}
	 * 
	 */
	@Override
	public final void write(final T t, MediaType contentType, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {

		final HttpHeaders headers = outputMessage.getHeaders();
		addDefaultHeaders(headers, t, contentType);

		if (outputMessage instanceof StreamingHttpOutputMessage) {
			StreamingHttpOutputMessage streamingOutputMessage =
					(StreamingHttpOutputMessage) outputMessage;
			streamingOutputMessage.setBody(new StreamingHttpOutputMessage.Body() {
				@Override
				public void writeTo(final OutputStream outputStream) throws IOException {
					writeInternal(t, new HttpOutputMessage() {
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
			writeInternal(t, outputMessage);
			outputMessage.getBody().flush();
		}
	}

	/**
	 * Add default headers to the output message.
	 * <p>This implementation delegates to {@link #getDefaultContentType(Object)} if a content
	 * type was not provided, set if necessary the default character set, calls
	 * {@link #getContentLength}, and sets the corresponding headers.
	 * <p>
	 *  将默认标头添加到输出消息<p>如果未提供内容类型,则此实现将委托给{@link #getDefaultContentType(Object)},如有必要,请设置默认字符集,调用{@link #getContentLength}
	 * 并设置相应的标题。
	 * 
	 * 
	 * @since 4.2
	 */
	protected void addDefaultHeaders(HttpHeaders headers, T t, MediaType contentType) throws IOException{
		if (headers.getContentType() == null) {
			MediaType contentTypeToUse = contentType;
			if (contentType == null || contentType.isWildcardType() || contentType.isWildcardSubtype()) {
				contentTypeToUse = getDefaultContentType(t);
			}
			else if (MediaType.APPLICATION_OCTET_STREAM.equals(contentType)) {
				MediaType mediaType = getDefaultContentType(t);
				contentTypeToUse = (mediaType != null ? mediaType : contentTypeToUse);
			}
			if (contentTypeToUse != null) {
				if (contentTypeToUse.getCharset() == null) {
					Charset defaultCharset = getDefaultCharset();
					if (defaultCharset != null) {
						contentTypeToUse = new MediaType(contentTypeToUse, defaultCharset);
					}
				}
				headers.setContentType(contentTypeToUse);
			}
		}
		if (headers.getContentLength() < 0) {
			Long contentLength = getContentLength(t, headers.getContentType());
			if (contentLength != null) {
				headers.setContentLength(contentLength);
			}
		}
	}

	/**
	 * Returns the default content type for the given type. Called when {@link #write}
	 * is invoked without a specified content type parameter.
	 * <p>By default, this returns the first element of the
	 * {@link #setSupportedMediaTypes(List) supportedMediaTypes} property, if any.
	 * Can be overridden in subclasses.
	 * <p>
	 * 返回给定类型的默认内容类型调用{@link #write}时没有指定内容类型参数<p>默认情况下,返回{@link #setSupportedMediaTypes(List)supportedMediaTypes}
	 * 属性的第一个元素,如果有的话可以在子类中被覆盖。
	 * 
	 * 
	 * @param t the type to return the content type for
	 * @return the content type, or {@code null} if not known
	 */
	protected MediaType getDefaultContentType(T t) throws IOException {
		List<MediaType> mediaTypes = getSupportedMediaTypes();
		return (!mediaTypes.isEmpty() ? mediaTypes.get(0) : null);
	}

	/**
	 * Returns the content length for the given type.
	 * <p>By default, this returns {@code null}, meaning that the content length is unknown.
	 * Can be overridden in subclasses.
	 * <p>
	 *  返回给定类型的内容长度<p>默认情况下,返回{@code null},表示内容长度未知可以在子类中覆盖
	 * 
	 * 
	 * @param t the type to return the content length for
	 * @return the content length, or {@code null} if not known
	 */
	protected Long getContentLength(T t, MediaType contentType) throws IOException {
		return null;
	}


	/**
	 * Indicates whether the given class is supported by this converter.
	 * <p>
	 *  指示此转换器是否支持给定的类
	 * 
	 * 
	 * @param clazz the class to test for support
	 * @return {@code true} if supported; {@code false} otherwise
	 */
	protected abstract boolean supports(Class<?> clazz);

	/**
	 * Abstract template method that reads the actual object. Invoked from {@link #read}.
	 * <p>
	 *  抽象模板方法读取实际对象从{@link #read}调用
	 * 
	 * 
	 * @param clazz the type of object to return
	 * @param inputMessage the HTTP input message to read from
	 * @return the converted object
	 * @throws IOException in case of I/O errors
	 * @throws HttpMessageNotReadableException in case of conversion errors
	 */
	protected abstract T readInternal(Class<? extends T> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException;

	/**
	 * Abstract template method that writes the actual body. Invoked from {@link #write}.
	 * <p>
	 *  抽象模板方法写入实体从{@link #write}调用
	 * 
	 * @param t the object to write to the output message
	 * @param outputMessage the HTTP output message to write to
	 * @throws IOException in case of I/O errors
	 * @throws HttpMessageNotWritableException in case of conversion errors
	 */
	protected abstract void writeInternal(T t, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException;

}
