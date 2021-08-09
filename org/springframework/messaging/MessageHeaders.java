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

package org.springframework.messaging;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.util.AlternativeJdkIdGenerator;
import org.springframework.util.IdGenerator;

/**
 * The headers for a {@link Message}.
 *
 * <p><b>IMPORTANT</b>: This class is immutable. Any mutating operation such as
 * {@code put(..)}, {@code putAll(..)} and others will throw
 * {@link UnsupportedOperationException}.
 * <p>Subclasses do have access to the raw headers, however, via {@link #getRawHeaders()}.
 *
 * <p>One way to create message headers is to use the
 * {@link org.springframework.messaging.support.MessageBuilder MessageBuilder}:
 * <pre class="code">
 * MessageBuilder.withPayload("foo").setHeader("key1", "value1").setHeader("key2", "value2");
 * </pre>
 *
 * A second option is to create {@link org.springframework.messaging.support.GenericMessage}
 * passing a payload as {@link Object} and headers as a {@link Map java.util.Map}:
 * <pre class="code">
 * Map headers = new HashMap();
 * headers.put("key1", "value1");
 * headers.put("key2", "value2");
 * new GenericMessage("foo", headers);
 * </pre>
 *
 * A third option is to use {@link org.springframework.messaging.support.MessageHeaderAccessor}
 * or one of its subclasses to create specific categories of headers.
 *
 * <p>
 *  {@link Message}的标题
 * 
 * <p> <b>重要</b>：此类是不可变的任何异常操作(如{@code put()},{@code putAll()}等)将抛出{@link UnsupportedOperationException}
 *  <p>子类可以通过{@link #getRawHeaders()}访问原始标题。
 * 
 *  <p>创建消息头的一种方法是使用{@link orgspringframeworkmessagingsupportMessageBuilder MessageBuilder}：
 * <pre class="code">
 *  MessageBuilderwithPayload("foo")setHeader("key1","value1")setHeader("key2","value2");
 * </pre>
 * 
 *  第二个选项是创建{@link orgspringframeworkmessagingsupportGenericMessage},将{@link Object}的页面传递为{@link Map javautilMap}
 * ：。
 * <pre class="code">
 *  Map headers = new HashMap(); headersput("key1","value1"); headersput("key2","value2"); new GenericMe
 * ssage("foo",headers);。
 * </pre>
 * 
 * 第三个选项是使用{@link orgspringframeworkmessagingsupportMessageHeaderAccessor}或其一个子类来创建特定类别的头文件
 * 
 * 
 * @author Arjen Poutsma
 * @author Mark Fisher
 * @author Gary Russell
 * @since 4.0
 * @see org.springframework.messaging.support.MessageBuilder
 * @see org.springframework.messaging.support.MessageHeaderAccessor
 */
public class MessageHeaders implements Map<String, Object>, Serializable {

	private static final long serialVersionUID = 7035068984263400920L;

	private static final Log logger = LogFactory.getLog(MessageHeaders.class);

	public static final UUID ID_VALUE_NONE = new UUID(0,0);

	private static volatile IdGenerator idGenerator = null;

	private static final IdGenerator defaultIdGenerator = new AlternativeJdkIdGenerator();

	/**
	 * The key for the Message ID. This is an automatically generated UUID and
	 * should never be explicitly set in the header map <b>except</b> in the
	 * case of Message deserialization where the serialized Message's generated
	 * UUID is being restored.
	 * <p>
	 *  消息ID的密钥这是一个自动生成的UUID,在消息序列化的情况下,除序列化消息的生成的UUID正在还原之外,不应该在标头映射<b>中除</b>之外明确设置
	 * 
	 */
	public static final String ID = "id";

	public static final String TIMESTAMP = "timestamp";

	public static final String REPLY_CHANNEL = "replyChannel";

	public static final String ERROR_CHANNEL = "errorChannel";

	public static final String CONTENT_TYPE = "contentType";


	private final Map<String, Object> headers;


	/**
	 * Construct a {@link MessageHeaders} with the given headers. An {@link #ID} and
	 * {@link #TIMESTAMP} headers will also be added, overriding any existing values.
	 * <p>
	 *  使用给定的标头构建一个{@link MessageHeaders}一个{@link #ID}和{@link #TIMESTAMP}标题也将被添加,覆盖任何现有的值
	 * 
	 * 
	 * @param headers a map with headers to add
	 */
	public MessageHeaders(Map<String, Object> headers) {
		this(headers, null, null);
	}

	/**
	 * Constructor providing control over the ID and TIMESTAMP header values.
	 * <p>
	 *  提供对ID和TIMESTAMP头值的控制的构造函数
	 * 
	 * 
	 * @param headers a map with headers to add
	 * @param id the {@link #ID} header value
	 * @param timestamp the {@link #TIMESTAMP} header value
	 */
	protected MessageHeaders(Map<String, Object> headers, UUID id, Long timestamp) {
		this.headers = (headers != null ? new HashMap<String, Object>(headers) : new HashMap<String, Object>());

		if (id == null) {
			this.headers.put(ID, getIdGenerator().generateId());
		}
		else if (id == ID_VALUE_NONE) {
			this.headers.remove(ID);
		}
		else {
			this.headers.put(ID, id);
		}

		if (timestamp == null) {
			this.headers.put(TIMESTAMP, System.currentTimeMillis());
		}
		else if (timestamp < 0) {
			this.headers.remove(TIMESTAMP);
		}
		else {
			this.headers.put(TIMESTAMP, timestamp);
		}
	}


	protected Map<String, Object> getRawHeaders() {
		return this.headers;
	}

	protected static IdGenerator getIdGenerator() {
		return (idGenerator != null ? idGenerator : defaultIdGenerator);
	}

	public UUID getId() {
		return get(ID, UUID.class);
	}

	public Long getTimestamp() {
		return get(TIMESTAMP, Long.class);
	}

	public Object getReplyChannel() {
		return get(REPLY_CHANNEL);
	}

	public Object getErrorChannel() {
		return get(ERROR_CHANNEL);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Object key, Class<T> type) {
		Object value = this.headers.get(key);
		if (value == null) {
			return null;
		}
		if (!type.isAssignableFrom(value.getClass())) {
			throw new IllegalArgumentException("Incorrect type specified for header '" +
					key + "'. Expected [" + type + "] but actual type is [" + value.getClass() + "]");
		}
		return (T) value;
	}


	@Override
	public boolean equals(Object other) {
		return (this == other ||
				(other instanceof MessageHeaders && this.headers.equals(((MessageHeaders) other).headers)));
	}

	@Override
	public int hashCode() {
		return this.headers.hashCode();
	}

	@Override
	public String toString() {
		return this.headers.toString();
	}


	// Delegating Map implementation

	public boolean containsKey(Object key) {
		return this.headers.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return this.headers.containsValue(value);
	}

	public Set<Map.Entry<String, Object>> entrySet() {
		return Collections.unmodifiableSet(this.headers.entrySet());
	}

	public Object get(Object key) {
		return this.headers.get(key);
	}

	public boolean isEmpty() {
		return this.headers.isEmpty();
	}

	public Set<String> keySet() {
		return Collections.unmodifiableSet(this.headers.keySet());
	}

	public int size() {
		return this.headers.size();
	}

	public Collection<Object> values() {
		return Collections.unmodifiableCollection(this.headers.values());
	}


	// Unsupported Map operations

	/**
	 * Since MessageHeaders are immutable, the call to this method
	 * will result in {@link UnsupportedOperationException}.
	 * <p>
	 *  由于MessageHeaders是不可变的,所以调用此方法会导致{@link UnsupportedOperationException}
	 * 
	 */
	public Object put(String key, Object value) {
		throw new UnsupportedOperationException("MessageHeaders is immutable");
	}

	/**
	 * Since MessageHeaders are immutable, the call to this method
	 * will result in {@link UnsupportedOperationException}.
	 * <p>
	 * 由于MessageHeaders是不可变的,所以调用此方法会导致{@link UnsupportedOperationException}
	 * 
	 */
	public void putAll(Map<? extends String, ? extends Object> map) {
		throw new UnsupportedOperationException("MessageHeaders is immutable");
	}

	/**
	 * Since MessageHeaders are immutable, the call to this method
	 * will result in {@link UnsupportedOperationException}.
	 * <p>
	 *  由于MessageHeaders是不可变的,所以调用此方法会导致{@link UnsupportedOperationException}
	 * 
	 */
	public Object remove(Object key) {
		throw new UnsupportedOperationException("MessageHeaders is immutable");
	}

	/**
	 * Since MessageHeaders are immutable, the call to this method
	 * will result in {@link UnsupportedOperationException}.
	 * <p>
	 *  由于MessageHeaders是不可变的,所以调用此方法会导致{@link UnsupportedOperationException}
	 */
	public void clear() {
		throw new UnsupportedOperationException("MessageHeaders is immutable");
	}


	// Serialization methods

	private void writeObject(ObjectOutputStream out) throws IOException {
		List<String> keysToRemove = new ArrayList<String>();
		for (Map.Entry<String, Object> entry : this.headers.entrySet()) {
			if (!(entry.getValue() instanceof Serializable)) {
				keysToRemove.add(entry.getKey());
			}
		}
		for (String key : keysToRemove) {
			if (logger.isInfoEnabled()) {
				logger.info("Removing non-serializable header: " + key);
			}
			this.headers.remove(key);
		}
		out.defaultWriteObject();
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
	}

}
