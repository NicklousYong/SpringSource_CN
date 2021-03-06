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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MimeType;

/**
 * A Jackson 2 based {@link MessageConverter} implementation.
 *
 * <p>It customizes Jackson's default properties with the following ones:
 * <ul>
 * <li>{@link MapperFeature#DEFAULT_VIEW_INCLUSION} is disabled</li>
 * <li>{@link DeserializationFeature#FAIL_ON_UNKNOWN_PROPERTIES} is disabled</li>
 * </ul>
 *
 * <p>Compatible with Jackson 2.1 and higher.
 *
 * <p>
 *  基于Jackson 2的{@link MessageConverter}实现
 * 
 *  <p>它使用以下命令自定义Jackson的默认属性：
 * <ul>
 * <li> {@ link MapperFeature#DEFAULT_VIEW_INCLUSION}被禁用</li> <li> {@ link DeserializationFeature#FAIL_ON_UNKNOWN_PROPERTIES}
 * 被禁用</li>。
 * </ul>
 * 
 *  <p>与Jackson 21及更高版本兼容
 * 
 * 
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @since 4.0
 */
public class MappingJackson2MessageConverter extends AbstractMessageConverter {

	// Check for Jackson 2.3's overloaded canDeserialize/canSerialize variants with cause reference
	private static final boolean jackson23Available =
			ClassUtils.hasMethod(ObjectMapper.class, "canDeserialize", JavaType.class, AtomicReference.class);


	private ObjectMapper objectMapper;

	private Boolean prettyPrint;


	public MappingJackson2MessageConverter() {
		super(new MimeType("application", "json", Charset.forName("UTF-8")));
		this.objectMapper = new ObjectMapper();
		this.objectMapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
		this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	/**
	 * Set the {@code ObjectMapper} for this converter.
	 * If not set, a default {@link ObjectMapper#ObjectMapper() ObjectMapper} is used.
	 * <p>Setting a custom-configured {@code ObjectMapper} is one way to take further
	 * control of the JSON serialization process. For example, an extended
	 * {@link com.fasterxml.jackson.databind.ser.SerializerFactory} can be
	 * configured that provides custom serializers for specific types. The other
	 * option for refining the serialization process is to use Jackson's provided
	 * annotations on the types to be serialized, in which case a custom-configured
	 * ObjectMapper is unnecessary.
	 * <p>
	 *  设置此转换器的{@code ObjectMapper}如果未设置,则使用默认{@link ObjectMapper#ObjectMapper()ObjectMapper} <p>设置自定义配置的{@code ObjectMapper}
	 * 是进一步控制JSON序列化过程例如,可以配置扩展的{@link comfasterxmljacksondatabindserSerializerFactory},为特定类型提供自定义序列化。
	 * 另一个用于改进序列化过程的选项是使用Jackson提供的注释来对要进行序列化的类型,在这种情况下,配置的ObjectMapper是不必要的。
	 * 
	 */
	public void setObjectMapper(ObjectMapper objectMapper) {
		Assert.notNull(objectMapper, "ObjectMapper must not be null");
		this.objectMapper = objectMapper;
		configurePrettyPrint();
	}

	/**
	 * Return the underlying {@code ObjectMapper} for this converter.
	 * <p>
	 * 返回此转换器的底层{@code ObjectMapper}
	 * 
	 */
	public ObjectMapper getObjectMapper() {
		return this.objectMapper;
	}

	/**
	 * Whether to use the {@link DefaultPrettyPrinter} when writing JSON.
	 * This is a shortcut for setting up an {@code ObjectMapper} as follows:
	 * <pre class="code">
	 * ObjectMapper mapper = new ObjectMapper();
	 * mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
	 * converter.setObjectMapper(mapper);
	 * </pre>
	 * <p>
	 *  是否在编写JSON时使用{@link DefaultPrettyPrinter}这是一个设置{@code ObjectMapper}的快捷方式,如下所示：
	 * <pre class="code">
	 *  ObjectMapper mapper = new ObjectMapper(); mapperconfigure(SerializationFeatureINDENT_OUTPUT,true); c
	 * onvertersetObjectMapper(映射器);。
	 */
	public void setPrettyPrint(boolean prettyPrint) {
		this.prettyPrint = prettyPrint;
		configurePrettyPrint();
	}

	private void configurePrettyPrint() {
		if (this.prettyPrint != null) {
			this.objectMapper.configure(SerializationFeature.INDENT_OUTPUT, this.prettyPrint);
		}
	}

	@Override
	protected boolean canConvertFrom(Message<?> message, Class<?> targetClass) {
		if (targetClass == null) {
			return false;
		}
		JavaType javaType = this.objectMapper.constructType(targetClass);
		if (!jackson23Available || !logger.isWarnEnabled()) {
			return (this.objectMapper.canDeserialize(javaType) && supportsMimeType(message.getHeaders()));
		}
		AtomicReference<Throwable> causeRef = new AtomicReference<Throwable>();
		if (this.objectMapper.canDeserialize(javaType, causeRef) && supportsMimeType(message.getHeaders())) {
			return true;
		}
		Throwable cause = causeRef.get();
		if (cause != null) {
			String msg = "Failed to evaluate deserialization for type " + javaType;
			if (logger.isDebugEnabled()) {
				logger.warn(msg, cause);
			}
			else {
				logger.warn(msg + ": " + cause);
			}
		}
		return false;
	}

	@Override
	protected boolean canConvertTo(Object payload, MessageHeaders headers) {
		if (!jackson23Available || !logger.isWarnEnabled()) {
			return (this.objectMapper.canSerialize(payload.getClass()) && supportsMimeType(headers));
		}
		AtomicReference<Throwable> causeRef = new AtomicReference<Throwable>();
		if (this.objectMapper.canSerialize(payload.getClass(), causeRef) && supportsMimeType(headers)) {
			return true;
		}
		Throwable cause = causeRef.get();
		if (cause != null) {
			String msg = "Failed to evaluate serialization for type [" + payload.getClass() + "]";
			if (logger.isDebugEnabled()) {
				logger.warn(msg, cause);
			}
			else {
				logger.warn(msg + ": " + cause);
			}
		}
		return false;
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		// should not be called, since we override canConvertFrom/canConvertTo instead
		throw new UnsupportedOperationException();
	}

	@Override
	public Object convertFromInternal(Message<?> message, Class<?> targetClass) {
		JavaType javaType = this.objectMapper.constructType(targetClass);
		Object payload = message.getPayload();
		try {
			if (payload instanceof byte[]) {
				return this.objectMapper.readValue((byte[]) payload, javaType);
			}
			else {
				return this.objectMapper.readValue((String) payload, javaType);
			}
		}
		catch (IOException ex) {
			throw new MessageConversionException(message, "Could not read JSON: " + ex.getMessage(), ex);
		}
	}

	@Override
	public Object convertToInternal(Object payload, MessageHeaders headers) {
		try {
			if (byte[].class.equals(getSerializedPayloadClass())) {
				ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
				JsonEncoding encoding = getJsonEncoding(getMimeType(headers));
				JsonGenerator generator = this.objectMapper.getFactory().createGenerator(out, encoding);
				this.objectMapper.writeValue(generator, payload);
				payload = out.toByteArray();
			}
			else {
				Writer writer = new StringWriter();
				this.objectMapper.writeValue(writer, payload);
				payload = writer.toString();
			}
		}
		catch (IOException ex) {
			throw new MessageConversionException("Could not write JSON: " + ex.getMessage(), ex);
		}
		return payload;
	}

	/**
	 * Determine the JSON encoding to use for the given content type.
	 * <p>
	 * </pre>
	 * 
	 * @param contentType the MIME type from the MessageHeaders, if any
	 * @return the JSON encoding to use (never {@code null})
	 */
	protected JsonEncoding getJsonEncoding(MimeType contentType) {
		if ((contentType != null) && (contentType.getCharSet() != null)) {
			Charset charset = contentType.getCharSet();
			for (JsonEncoding encoding : JsonEncoding.values()) {
				if (charset.name().equals(encoding.getJavaName())) {
					return encoding;
				}
			}
		}
		return JsonEncoding.UTF8;
	}

}
