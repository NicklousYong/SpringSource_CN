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

package org.springframework.messaging.simp;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.IdTimestampMessageHeaderInitializer;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.support.NativeMessageHeaderAccessor;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * A base class for working with message headers in simple messaging protocols that
 * support basic messaging patterns. Provides uniform access to specific values common
 * across protocols such as a destination, message type (e.g. publish, subscribe, etc),
 * session id, and others.
 *
 * <p>Use one of the static factory method in this class, then call getters and setters,
 * and at the end if necessary call {@link #toMap()} to obtain the updated headers.
 *
 * <p>
 * 在支持基本消息传递模式的简单消息协议中处理消息头的基类提供对诸如目的地,消息类型(例如,发布,订阅等),会话ID等的协议通用的特定值的统一访问
 * 
 *  <p>使用此类中的静态工厂方法之一,然后调用getter和setter,并在必要时调用{@link #toMap()}结束获取更新的头文件
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public class SimpMessageHeaderAccessor extends NativeMessageHeaderAccessor {

	private static final IdTimestampMessageHeaderInitializer headerInitializer;

	static {
		headerInitializer = new IdTimestampMessageHeaderInitializer();
		headerInitializer.setDisableIdGeneration();
		headerInitializer.setEnableTimestamp(false);
	}

	// SiMP header names

	public static final String CONNECT_MESSAGE_HEADER = "simpConnectMessage";

	public static final String DESTINATION_HEADER = "simpDestination";

	public static final String MESSAGE_TYPE_HEADER = "simpMessageType";

	public static final String SESSION_ID_HEADER = "simpSessionId";

	public static final String SESSION_ATTRIBUTES = "simpSessionAttributes";

	public static final String SUBSCRIPTION_ID_HEADER = "simpSubscriptionId";

	public static final String USER_HEADER = "simpUser";

	/**
	 * For internal use.
	 * <p>The original destination used by a client when subscribing. Such a
	 * destination may have been modified (e.g. user destinations) on the server
	 * side. This header provides a hint so messages sent to clients may have
	 * a destination matching to their original subscription.
	 * <p>
	 *  用于内部使用<p>客户端在订阅时使用的原始目的地可能已在服务器端修改(例如用户目的地)此标题提供提示,因此发送到客户端的消息可能具有与其原始订阅的目的地匹配
	 * 
	 */
	public static final String ORIGINAL_DESTINATION = "simpOrigDestination";


	/**
	 * A constructor for creating new message headers.
	 * This constructor is protected. See factory methods in this and sub-classes.
	 * <p>
	 * 用于创建新消息头的构造函数此构造方法受保护请参阅此子类中的工厂方法
	 * 
	 */
	protected SimpMessageHeaderAccessor(SimpMessageType messageType, Map<String, List<String>> externalSourceHeaders) {
		super(externalSourceHeaders);
		Assert.notNull(messageType, "MessageType must not be null");
		setHeader(MESSAGE_TYPE_HEADER, messageType);
		headerInitializer.initHeaders(this);
	}

	/**
	 * A constructor for accessing and modifying existing message headers. This
	 * constructor is protected. See factory methods in this and sub-classes.
	 * <p>
	 *  用于访问和修改现有消息头的构造函数此构造方法受保护请参阅此子类中的工厂方法
	 * 
	 */
	protected SimpMessageHeaderAccessor(Message<?> message) {
		super(message);
		headerInitializer.initHeaders(this);
	}


	@Override
	protected MessageHeaderAccessor createAccessor(Message<?> message) {
		return wrap(message);
	}

	public void setMessageTypeIfNotSet(SimpMessageType messageType) {
		if (getMessageType() == null) {
			setHeader(MESSAGE_TYPE_HEADER, messageType);
		}
	}

	public SimpMessageType getMessageType() {
		return (SimpMessageType) getHeader(MESSAGE_TYPE_HEADER);
	}

	public void setDestination(String destination) {
		Assert.notNull(destination, "Destination must not be null");
		setHeader(DESTINATION_HEADER, destination);
	}

	public String getDestination() {
		return (String) getHeader(DESTINATION_HEADER);
	}

	public void setSubscriptionId(String subscriptionId) {
		setHeader(SUBSCRIPTION_ID_HEADER, subscriptionId);
	}

	public String getSubscriptionId() {
		return (String) getHeader(SUBSCRIPTION_ID_HEADER);
	}

	public void setSessionId(String sessionId) {
		setHeader(SESSION_ID_HEADER, sessionId);
	}

	/**
	/* <p>
	/* 
	 * @return the id of the current session
	 */
	public String getSessionId() {
		return (String) getHeader(SESSION_ID_HEADER);
	}

	/**
	 * A static alternative for access to the session attributes header.
	 * <p>
	 *  用于访问会话属性头的静态替代方法
	 * 
	 */
	public void setSessionAttributes(Map<String, Object> attributes) {
		setHeader(SESSION_ATTRIBUTES, attributes);
	}

	/**
	 * Return the attributes associated with the current session.
	 * <p>
	 *  返回与当前会话关联的属性
	 * 
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getSessionAttributes() {
		return (Map<String, Object>) getHeader(SESSION_ATTRIBUTES);
	}

	public void setUser(Principal principal) {
		setHeader(USER_HEADER, principal);
	}

	/**
	 * Return the user associated with the current session.
	 * <p>
	 *  返回与当前会话关联的用户
	 * 
	 */
	public Principal getUser() {
		return (Principal) getHeader(USER_HEADER);
	}

	@Override
	public String getShortLogMessage(Object payload) {
		if (getMessageType() == null) {
			return super.getDetailedLogMessage(payload);
		}
		StringBuilder sb = getBaseLogMessage();
		if (!CollectionUtils.isEmpty(getSessionAttributes())) {
			sb.append(" attributes[").append(getSessionAttributes().size()).append("]");
		}
		sb.append(getShortPayloadLogMessage(payload));
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getDetailedLogMessage(Object payload) {
		if (getMessageType() == null) {
			return super.getDetailedLogMessage(payload);
		}
		StringBuilder sb = getBaseLogMessage();
		if (!CollectionUtils.isEmpty(getSessionAttributes())) {
			sb.append(" attributes=").append(getSessionAttributes());
		}
		if (!CollectionUtils.isEmpty((Map<String, List<String>>) getHeader(NATIVE_HEADERS))) {
			sb.append(" nativeHeaders=").append(getHeader(NATIVE_HEADERS));
		}
		sb.append(getDetailedPayloadLogMessage(payload));
		return sb.toString();
	}

	private StringBuilder getBaseLogMessage() {
		StringBuilder sb = new StringBuilder();
		sb.append(getMessageType().name());
		if (getDestination() != null) {
			sb.append(" destination=").append(getDestination());
		}
		if (getSubscriptionId() != null) {
			sb.append(" subscriptionId=").append(getSubscriptionId());
		}
		sb.append(" session=").append(getSessionId());
		if (getUser() != null) {
			sb.append(" user=").append(getUser().getName());
		}
		return sb;
	}


	// Static factory methods and accessors

	/**
	 * Create an instance with
	 * {@link org.springframework.messaging.simp.SimpMessageType} {@code MESSAGE}.
	 * <p>
	 *  使用{@link orgspringframeworkmessagingsimpSimpMessageType} {@code MESSAGE}创建一个实例
	 * 
	 */
	public static SimpMessageHeaderAccessor create() {
		return new SimpMessageHeaderAccessor(SimpMessageType.MESSAGE, null);
	}

	/**
	 * Create an instance with the given
	 * {@link org.springframework.messaging.simp.SimpMessageType}.
	 * <p>
	 *  使用给定的{@link orgspringframeworkmessagingsimpSimpMessageType}创建一个实例
	 * 
	 */
	public static SimpMessageHeaderAccessor create(SimpMessageType messageType) {
		return new SimpMessageHeaderAccessor(messageType, null);
	}

	/**
	 * Create an instance from the payload and headers of the given Message.
	 * <p>
	 *  从有效载荷和给定消息的头部创建一个实例
	 */
	public static SimpMessageHeaderAccessor wrap(Message<?> message) {
		return new SimpMessageHeaderAccessor(message);
	}

	public static SimpMessageType getMessageType(Map<String, Object> headers) {
		return (SimpMessageType) headers.get(MESSAGE_TYPE_HEADER);
	}

	public static String getDestination(Map<String, Object> headers) {
		return (String) headers.get(DESTINATION_HEADER);
	}

	public static String getSubscriptionId(Map<String, Object> headers) {
		return (String) headers.get(SUBSCRIPTION_ID_HEADER);
	}

	public static String getSessionId(Map<String, Object> headers) {
		return (String) headers.get(SESSION_ID_HEADER);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getSessionAttributes(Map<String, Object> headers) {
		return (Map<String, Object>) headers.get(SESSION_ATTRIBUTES);
	}

	public static Principal getUser(Map<String, Object> headers) {
		return (Principal) headers.get(USER_HEADER);
	}

}
