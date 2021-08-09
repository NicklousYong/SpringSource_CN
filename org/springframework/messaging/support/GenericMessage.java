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

package org.springframework.messaging.support;

import java.io.Serializable;
import java.util.Map;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * An implementation of {@link Message} with a generic payload.
 * Once created, a GenericMessage is immutable.
 *
 * <p>
 *  通用有效载荷的{@link Message}的实现一旦创建,GenericMessage就是不可变的
 * 
 * 
 * @author Mark Fisher
 * @since 4.0
 * @see MessageBuilder
 */
public class GenericMessage<T> implements Message<T>, Serializable {

	private static final long serialVersionUID = 4268801052358035098L;


	private final T payload;

	private final MessageHeaders headers;


	/**
	 * Create a new message with the given payload.
	 * <p>
	 *  使用给定的有效载荷创建新消息
	 * 
	 * 
	 * @param payload the message payload (never {@code null})
	 */
	public GenericMessage(T payload) {
		this(payload, new MessageHeaders(null));
	}

	/**
	 * Create a new message with the given payload and headers.
	 * The content of the given header map is copied.
	 * <p>
	 * 使用给定的有效载荷和标题创​​建新消息给定标题映射的内容被复制
	 * 
	 * 
	 * @param payload the message payload (never {@code null})
	 * @param headers message headers to use for initialization
	 */
	public GenericMessage(T payload, Map<String, Object> headers) {
		this(payload, new MessageHeaders(headers));
	}

	/**
	 * A constructor with the {@link MessageHeaders} instance to use.
	 * <p><strong>Note:</strong> the given {@code MessageHeaders} instance is used
	 * directly in the new message, i.e. it is not copied.
	 * <p>
	 *  使用{@link MessageHeaders}实例的构造函数使用<p> <strong>注意：</strong>给定的{@code MessageHeaders}实例直接用于新消息,即不复制
	 * 
	 * @param payload the message payload (never {@code null})
	 * @param headers message headers
	 */
	public GenericMessage(T payload, MessageHeaders headers) {
		Assert.notNull(payload, "Payload must not be null");
		Assert.notNull(headers, "MessageHeaders must not be null");
		this.payload = payload;
		this.headers = headers;
	}


	public T getPayload() {
		return this.payload;
	}

	public MessageHeaders getHeaders() {
		return this.headers;
	}


	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof GenericMessage)) {
			return false;
		}
		GenericMessage<?> otherMsg = (GenericMessage<?>) other;
		// Using nullSafeEquals for proper array equals comparisons
		return (ObjectUtils.nullSafeEquals(this.payload, otherMsg.payload) && this.headers.equals(otherMsg.headers));
	}

	public int hashCode() {
		// Using nullSafeHashCode for proper array hashCode handling
		return (ObjectUtils.nullSafeHashCode(this.payload) * 23 + this.headers.hashCode());
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(getClass().getSimpleName());
		sb.append(" [payload=");
		if (this.payload instanceof byte[]) {
			sb.append("byte[").append(((byte[]) this.payload).length).append("]");
		}
		else {
			sb.append(this.payload);
		}
		sb.append(", headers=").append(this.headers).append("]");
		return sb.toString();
	}

}
