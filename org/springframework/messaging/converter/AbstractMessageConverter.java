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

package org.springframework.messaging.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.util.Assert;
import org.springframework.util.MimeType;

/**
 * Abstract base class for {@link MessageConverter} implementations including support for
 * common properties and a partial implementation of the conversion methods mainly to
 * check if the converter supports the conversion based on the payload class and MIME
 * type.
 *
 * <p>
 * {@link MessageConverter}实现的抽象基类,包括对常用属性的支持和转换方法的部分实现,主要是检查转换器是否支持基于有效载荷类和MIME类型的转换
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public abstract class AbstractMessageConverter implements MessageConverter {

	protected final Log logger = LogFactory.getLog(getClass());


	private final List<MimeType> supportedMimeTypes;

	private ContentTypeResolver contentTypeResolver = new DefaultContentTypeResolver();

	private boolean strictContentTypeMatch = false;

	private Class<?> serializedPayloadClass = byte[].class;


	/**
	 * Construct an {@code AbstractMessageConverter} supporting a single MIME type.
	 * <p>
	 *  构造一个支持单一MIME类型的{@code AbstractMessageConverter}
	 * 
	 * 
	 * @param supportedMimeType the supported MIME type
	 */
	protected AbstractMessageConverter(MimeType supportedMimeType) {
		Assert.notNull(supportedMimeType, "supportedMimeType is required");
		this.supportedMimeTypes = Collections.<MimeType>singletonList(supportedMimeType);
	}

	/**
	 * Construct an {@code AbstractMessageConverter} supporting multiple MIME types.
	 * <p>
	 *  构造一个支持多种MIME类型的{@code AbstractMessageConverter}
	 * 
	 * 
	 * @param supportedMimeTypes the supported MIME types
	 */
	protected AbstractMessageConverter(Collection<MimeType> supportedMimeTypes) {
		Assert.notNull(supportedMimeTypes, "SupportedMimeTypes must not be null");
		this.supportedMimeTypes = new ArrayList<MimeType>(supportedMimeTypes);
	}


	/**
	 * Return the supported MIME types.
	 * <p>
	 *  返回支持的MIME类型
	 * 
	 */
	public List<MimeType> getSupportedMimeTypes() {
		return Collections.unmodifiableList(this.supportedMimeTypes);
	}

	/**
	 * Configure the {@link ContentTypeResolver} to use to resolve the content
	 * type of an input message.
	 * <p>
	 * Note that if no resolver is configured, then
	 * {@link #setStrictContentTypeMatch(boolean) strictContentTypeMatch} should
	 * be left as {@code false} (the default) or otherwise this converter will
	 * ignore all messages.
	 * <p>
	 * By default, a {@code DefaultContentTypeResolver} instance is used.
	 * <p>
	 *  配置{@link ContentTypeResolver}以用于解析输入消息的内容类型
	 * <p>
	 *  请注意,如果没有配置解析器,则{@link #setStrictContentTypeMatch(boolean)strictContentTypeMatch}应保留为{@code false}(默认
	 * 值),否则此转换器将忽略所有消息。
	 * <p>
	 * 默认情况下,使用{@code DefaultContentTypeResolver}实例
	 * 
	 */
	public void setContentTypeResolver(ContentTypeResolver resolver) {
		this.contentTypeResolver = resolver;
	}

	/**
	 * Return the configured {@link ContentTypeResolver}.
	 * <p>
	 *  返回配置的{@link ContentTypeResolver}
	 * 
	 */
	public ContentTypeResolver getContentTypeResolver() {
		return this.contentTypeResolver;
	}

	/**
	 * Whether this converter should convert messages for which no content type
	 * could be resolved through the configured
	 * {@link org.springframework.messaging.converter.ContentTypeResolver}.
	 * A converter can configured to be strict only when a
	 * {@link #setContentTypeResolver(ContentTypeResolver) contentTypeResolver}
	 * is  configured and the list of {@link #getSupportedMimeTypes() supportedMimeTypes}
	 * is not be empty.
	 *
	 * then requires the content type of a message to be resolved
	 *
	 * When set to true, #supportsMimeType(MessageHeaders) will return false if the
	 * contentTypeResolver is not defined or if no content-type header is present.
	 * <p>
	 *  无论此转换器是否可以通过配置的{@link orgspringframeworkmessagingconverterContentTypeResolver} A转换器转换不能解析内容类型的消息,只能配
	 * 置{@link #setContentTypeResolver(ContentTypeResolver)contentTypeResolver}并且{@链接#getSupportedMimeTypes()supportedMimeTypes}
	 * 不是空的。
	 * 
	 *  然后要求消息的内容类型被解析
	 * 
	 *  当设置为true时,如果没有定义contentTypeResolver或者没有内容类型的头存在,则#supportsMimeType(MessageHeaders)将返回false
	 * 
	 */
	public void setStrictContentTypeMatch(boolean strictContentTypeMatch) {
		if (strictContentTypeMatch) {
			Assert.notEmpty(getSupportedMimeTypes(), "Strict match requires non-empty list of supported mime types.");
			Assert.notNull(getContentTypeResolver(), "Strict match requires ContentTypeResolver.");
		}
		this.strictContentTypeMatch = strictContentTypeMatch;
	}

	/**
	 * Whether content type resolution must produce a value that matches one of
	 * the supported MIME types.
	 * <p>
	 * 内容类型分辨率是否必须产生与支持的MIME类型匹配的值
	 * 
	 */
	public boolean isStrictContentTypeMatch() {
		return this.strictContentTypeMatch;
	}

	/**
	 * Configure the preferred serialization class to use (byte[] or String) when
	 * converting an Object payload to a {@link Message}.
	 * <p>The default value is byte[].
	 * <p>
	 *  将对象有效负载转换为{@link消息} <p>时,将首选序列化类配置为使用(byte []或String)。默认值为byte []
	 * 
	 * 
	 * @param payloadClass either byte[] or String
	 */
	public void setSerializedPayloadClass(Class<?> payloadClass) {
		Assert.isTrue(byte[].class.equals(payloadClass) || String.class.equals(payloadClass),
				"Payload class must be byte[] or String: " + payloadClass);
		this.serializedPayloadClass = payloadClass;
	}

	/**
	 * Return the configured preferred serialization payload class.
	 * <p>
	 *  返回配置的优选序列化有效载荷类
	 * 
	 */
	public Class<?> getSerializedPayloadClass() {
		return this.serializedPayloadClass;
	}


	/**
	 * Returns the default content type for the payload. Called when
	 * {@link #toMessage(Object, MessageHeaders)} is invoked without message headers or
	 * without a content type header.
	 * <p>By default, this returns the first element of the {@link #getSupportedMimeTypes()
	 * supportedMimeTypes}, if any. Can be overridden in sub-classes.
	 * <p>
	 *  返回当没有消息头或没有内容类型标头调用{@link #toMessage(Object,MessageHeaders)}时调用的负载的默认内容类型默认情况下,这将返回{@link #getSupportedMimeTypes ()supportedMimeTypes}
	 * (如果有)可以在子类中覆盖。
	 * 
	 * 
	 * @param payload the payload being converted to message
	 * @return the content type, or {@code null} if not known
	 */
	protected MimeType getDefaultContentType(Object payload) {
		List<MimeType> mimeTypes = getSupportedMimeTypes();
		return (!mimeTypes.isEmpty() ? mimeTypes.get(0) : null);
	}

	/**
	 * Whether the given class is supported by this converter.
	 * <p>
	 *  该转换器是否支持给定的类
	 * 
	 * 
	 * @param clazz the class to test for support
	 * @return {@code true} if supported; {@code false} otherwise
	 */
	protected abstract boolean supports(Class<?> clazz);


	@Override
	public final Object fromMessage(Message<?> message, Class<?> targetClass) {
		if (!canConvertFrom(message, targetClass)) {
			return null;
		}
		return convertFromInternal(message, targetClass);
	}

	protected boolean canConvertFrom(Message<?> message, Class<?> targetClass) {
		return (supports(targetClass) && supportsMimeType(message.getHeaders()));
	}

	/**
	 * Convert the message payload from serialized form to an Object.
	 * <p>
	 *  将消息有效载荷从序列化形式转换为对象
	 * 
	 */
	public abstract Object convertFromInternal(Message<?> message, Class<?> targetClass);

	@Override
	public final Message<?> toMessage(Object payload, MessageHeaders headers) {

		if (!canConvertTo(payload, headers)) {
			return null;
		}

		payload = convertToInternal(payload, headers);
		MimeType mimeType = getDefaultContentType(payload);

		if (headers != null) {
			MessageHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(headers, MessageHeaderAccessor.class);
			if (accessor != null && accessor.isMutable()) {
				accessor.setHeaderIfAbsent(MessageHeaders.CONTENT_TYPE, mimeType);
				return MessageBuilder.createMessage(payload, accessor.getMessageHeaders());
			}
		}

		MessageBuilder<?> builder = MessageBuilder.withPayload(payload);
		if (headers != null) {
			builder.copyHeaders(headers);
		}
		builder.setHeaderIfAbsent(MessageHeaders.CONTENT_TYPE, mimeType);
		return builder.build();
	}

	protected boolean canConvertTo(Object payload, MessageHeaders headers) {
		Class<?> clazz = (payload != null) ? payload.getClass() : null;
		return (supports(clazz) && supportsMimeType(headers));
	}

	/**
	 * Convert the payload object to serialized form.
	 * <p>
	 * 将有效载荷对象转换为序列化形式
	 */
	public abstract Object convertToInternal(Object payload, MessageHeaders headers);

	protected boolean supportsMimeType(MessageHeaders headers) {
		if (getSupportedMimeTypes().isEmpty()) {
			return true;
		}
		MimeType mimeType = getMimeType(headers);
		if (mimeType == null) {
			if (isStrictContentTypeMatch()) {
				return false;
			}
			else {
				return true;
			}
		}
		for (MimeType current : getSupportedMimeTypes()) {
			if (current.getType().equals(mimeType.getType()) && current.getSubtype().equals(mimeType.getSubtype())) {
				return true;
			}
		}
		return false;
	}

	protected MimeType getMimeType(MessageHeaders headers) {
		return (this.contentTypeResolver != null) ? this.contentTypeResolver.resolve(headers) : null;
	}

}
