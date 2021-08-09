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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.messaging.Message;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.ObjectUtils;

/**
 * An extension of {@link MessageHeaderAccessor} that also stores and provides read/write
 * access to message headers from an external source -- e.g. a Spring {@link Message}
 * created to represent a STOMP message received from a STOMP client or message broker.
 * Native message headers are kept in a {@code Map<String, List<String>>} under the key
 * {@link #NATIVE_HEADERS}.
 * <p>
 * This class is not intended for direct use but is rather expected to be used
 * indirectly through protocol-specific sub-classes such as
 * {@link org.springframework.messaging.simp.stomp.StompHeaderAccessor StompHeaderAccessor}.
 * Such sub-classes may provide factory methods to translate message headers from
 * an external messaging source (e.g. STOMP) to Spring {@link Message} headers and
 * reversely to translate Spring {@link Message} headers to a message to send to an
 * external source.
 *
 * <p>
 * {@link MessageHeaderAccessor}的扩展,它还存储并提供来自外部源的消息头的读/写访问 - 例如创建用于表示从STOMP客户端或消息代理接收的STOMP消息的Spring {@link消息}
 * 本地消息标题保存在{@link #NATIVE_HEADERS}下的{@code Map <String,List <String >>}中。
 * <p>
 * 这个类不是直接使用的,而是期望通过特定于协议的子类(如{@link orgspringframeworkmessagingsimpstompStompHeaderAccessor StompHeaderAccessor}
 * )间接使用。
 * 这些子类可以提供工厂方法来从外部消息传递源翻译消息头(例如STOMP)到Spring {@link Message}标题,并将Spring {@link Message}标头翻译成要发送到外部源的消息。
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public class NativeMessageHeaderAccessor extends MessageHeaderAccessor {

	public static final String NATIVE_HEADERS = "nativeHeaders";


	/**
	 * A protected constructor to create new headers.
	 * <p>
	 *  一个受保护的构造函数来创建新的头
	 * 
	 */
	protected NativeMessageHeaderAccessor() {
		this((Map<String, List<String>>) null);
	}

	/**
	 * A protected constructor to create new headers.
	 * <p>
	 *  一个受保护的构造函数来创建新的头
	 * 
	 * 
	 * @param nativeHeaders native headers to create the message with, may be {@code null}
	 */
	protected NativeMessageHeaderAccessor(Map<String, List<String>> nativeHeaders) {
		if (!CollectionUtils.isEmpty(nativeHeaders)) {
			setHeader(NATIVE_HEADERS, new LinkedMultiValueMap<String, String>(nativeHeaders));
		}
	}

	/**
	 * A protected constructor accepting the headers of an existing message to copy.
	 * <p>
	 *  受保护的构造函数接受要复制的现有消息的标题
	 * 
	 */
	protected NativeMessageHeaderAccessor(Message<?> message) {
		super(message);
		if (message != null) {
			@SuppressWarnings("unchecked")
			Map<String, List<String>> map = (Map<String, List<String>>) getHeader(NATIVE_HEADERS);
			if (map != null) {
				// Force removal since setHeader checks for equality
				removeHeader(NATIVE_HEADERS);
				setHeader(NATIVE_HEADERS, new LinkedMultiValueMap<String, String>(map));
			}
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, List<String>> getNativeHeaders() {
		return (Map<String, List<String>>) getHeader(NATIVE_HEADERS);
	}

	/**
	 * Return a copy of the native header values or an empty map.
	 * <p>
	 *  返回本机头值的副本或空的地图
	 * 
	 */
	public Map<String, List<String>> toNativeHeaderMap() {
		Map<String, List<String>> map = getNativeHeaders();
		return (map != null ? new LinkedMultiValueMap<String, String>(map) : Collections.<String, List<String>>emptyMap());
	}

	@Override
	public void setImmutable() {
		if (isMutable()) {
			Map<String, List<String>> map = getNativeHeaders();
			if (map != null) {
				// Force removal since setHeader checks for equality
				removeHeader(NATIVE_HEADERS);
				setHeader(NATIVE_HEADERS, Collections.<String, List<String>>unmodifiableMap(map));
			}
			super.setImmutable();
		}
	}

	/**
	 * Whether the native header map contains the give header name.
	 * <p>
	 *  本机标头映射是否包含给定标题名称
	 * 
	 */
	public boolean containsNativeHeader(String headerName) {
		Map<String, List<String>> map = getNativeHeaders();
		return (map != null ? map.containsKey(headerName) : false);
	}

	/**
	/* <p>
	/* 
	 * @return all values for the specified native header or {@code null}.
	 */
	public List<String> getNativeHeader(String headerName) {
		Map<String, List<String>> map = getNativeHeaders();
		return (map != null ? map.get(headerName) : null);
	}

	/**
	/* <p>
	/* 
	 * @return the first value for the specified native header of {@code null}.
	 */
	public String getFirstNativeHeader(String headerName) {
		Map<String, List<String>> map = getNativeHeaders();
		if (map != null) {
			List<String> values = map.get(headerName);
			if (values != null) {
				return values.get(0);
			}
		}
		return null;
	}

	/**
	 * Set the specified native header value replacing existing values.
	 * <p>
	 * 设置指定的本机头值替换现有值
	 * 
	 */
	public void setNativeHeader(String name, String value) {
		Assert.state(isMutable(), "Already immutable");
		Map<String, List<String>> map = getNativeHeaders();
		if (value == null) {
			if (map != null && map.get(name) != null) {
				setModified(true);
				map.remove(name);
			}
			return;
		}
		if (map == null) {
			map = new LinkedMultiValueMap<String, String>(4);
			setHeader(NATIVE_HEADERS, map);
		}
		List<String> values = new LinkedList<String>();
		values.add(value);
		if (!ObjectUtils.nullSafeEquals(values, getHeader(name))) {
			setModified(true);
			map.put(name, values);
		}
	}

	/**
	 * Add the specified native header value to existing values.
	 * <p>
	 *  将指定的本机头值添加到现有值
	 */
	public void addNativeHeader(String name, String value) {
		Assert.state(isMutable(), "Already immutable");
		if (value == null) {
			return;
		}
		Map<String, List<String>> nativeHeaders = getNativeHeaders();
		if (nativeHeaders == null) {
			nativeHeaders = new LinkedMultiValueMap<String, String>(4);
			setHeader(NATIVE_HEADERS, nativeHeaders);
		}
		List<String> values = nativeHeaders.get(name);
		if (values == null) {
			values = new LinkedList<String>();
			nativeHeaders.put(name, values);
		}
		values.add(value);
		setModified(true);
	}

	public List<String> removeNativeHeader(String name) {
		Assert.state(isMutable(), "Already immutable");
		Map<String, List<String>> nativeHeaders = getNativeHeaders();
		if (nativeHeaders == null) {
			return null;
		}
		return nativeHeaders.remove(name);
	}

}
