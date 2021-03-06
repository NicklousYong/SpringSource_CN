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

package org.springframework.messaging.simp.broker;

import java.util.Collection;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderInitializer;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.util.PathMatcher;

/**
 * A "simple" message broker that recognizes the message types defined in
 * {@link SimpMessageType}, keeps track of subscriptions with the help of a
 * {@link SubscriptionRegistry} and sends messages to subscribers.
 *
 * <p>
 *  识别{@link SimpMessageType}中定义的消息类型的"简单"消息代理,在{@link SubscriptionRegistry}的帮助下跟踪订阅,并向订阅者发送消息
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public class SimpleBrokerMessageHandler extends AbstractBrokerMessageHandler {

	private static final byte[] EMPTY_PAYLOAD = new byte[0];

	private SubscriptionRegistry subscriptionRegistry;

	private PathMatcher pathMatcher;

	private MessageHeaderInitializer headerInitializer;


	/**
	 * Create a SimpleBrokerMessageHandler instance with the given message channels
	 * and destination prefixes.
	 * <p>
	 * 使用给定的消息通道和目标前缀创建一个SimpleBrokerMessageHandler实例
	 * 
	 * 
	 * @param clientInboundChannel the channel for receiving messages from clients (e.g. WebSocket clients)
	 * @param clientOutboundChannel the channel for sending messages to clients (e.g. WebSocket clients)
	 * @param brokerChannel the channel for the application to send messages to the broker
	 * @param destinationPrefixes prefixes to use to filter out messages
	 */
	public SimpleBrokerMessageHandler(SubscribableChannel clientInboundChannel, MessageChannel clientOutboundChannel,
			SubscribableChannel brokerChannel, Collection<String> destinationPrefixes) {

		super(clientInboundChannel, clientOutboundChannel, brokerChannel, destinationPrefixes);
		this.subscriptionRegistry = new DefaultSubscriptionRegistry();
	}


	/**
	 * Configure a custom SubscriptionRegistry to use for storing subscriptions.
	 *
	 * <p><strong>Note</strong> that when a custom PathMatcher is configured via
	 * {@link #setPathMatcher}, if the custom registry is not an instance of
	 * {@link DefaultSubscriptionRegistry}, the provided PathMatcher is not used
	 * and must be configured directly on the custom registry.
	 * <p>
	 *  配置用于存储订阅的自定义SubscriptionRegistry
	 * 
	 *  <p> <strong>注意</strong>：当通过{@link #setPathMatcher}配置自定义PathMatcher时,如果自定义注册表不是{@link DefaultSubscriptionRegistry}
	 * 的实例,则不会使用提供的PathMatcher,并且必须直接在自定义注册表上配置。
	 * 
	 */
	public void setSubscriptionRegistry(SubscriptionRegistry subscriptionRegistry) {
		Assert.notNull(subscriptionRegistry, "SubscriptionRegistry must not be null");
		this.subscriptionRegistry = subscriptionRegistry;
		initPathMatcherToUse();
	}

	private void initPathMatcherToUse() {
		if (this.pathMatcher != null) {
			if (this.subscriptionRegistry instanceof DefaultSubscriptionRegistry) {
				((DefaultSubscriptionRegistry) this.subscriptionRegistry).setPathMatcher(this.pathMatcher);
			}
		}
	}

	public SubscriptionRegistry getSubscriptionRegistry() {
		return this.subscriptionRegistry;
	}

	/**
	 * When configured, the given PathMatcher is passed down to the
	 * SubscriptionRegistry to use for matching destination to subscriptions.
	 * <p>
	 *  配置后,给定的PathMatcher将传递给SubscriptionRegistry,以用于匹配目标到订阅
	 * 
	 */
	public void setPathMatcher(PathMatcher pathMatcher) {
		this.pathMatcher = pathMatcher;
		initPathMatcherToUse();
	}

	/**
	 * Configure a {@link MessageHeaderInitializer} to apply to the headers of all
	 * messages sent to the client outbound channel.
	 *
	 * <p>By default this property is not set.
	 * <p>
	 *  配置{@link MessageHeaderInitializer}以应用于发送到客户端出站通道的所有邮件的标头
	 * 
	 *  <p>默认情况下,此属性未设置
	 */
	public void setHeaderInitializer(MessageHeaderInitializer headerInitializer) {
		this.headerInitializer = headerInitializer;
	}

	/**
	/* <p>
	/* 
	/* 
	 * @return the configured header initializer.
	 */
	public MessageHeaderInitializer getHeaderInitializer() {
		return this.headerInitializer;
	}


	@Override
	public void startInternal() {
		publishBrokerAvailableEvent();
	}

	@Override
	public void stopInternal() {
		publishBrokerUnavailableEvent();
	}

	@Override
	protected void handleMessageInternal(Message<?> message) {

		MessageHeaders headers = message.getHeaders();
		SimpMessageType messageType = SimpMessageHeaderAccessor.getMessageType(headers);
		String destination = SimpMessageHeaderAccessor.getDestination(headers);
		String sessionId = SimpMessageHeaderAccessor.getSessionId(headers);

		if (!checkDestinationPrefix(destination)) {
			return;
		}

		SimpMessageHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, SimpMessageHeaderAccessor.class);
		if (accessor == null) {
			throw new IllegalStateException(
					"No header accessor (not using the SimpMessagingTemplate?): " + message);
		}

		if (SimpMessageType.MESSAGE.equals(messageType)) {
			logMessage(message);
			sendMessageToSubscribers(destination, message);
		}
		else if (SimpMessageType.CONNECT.equals(messageType)) {
			logMessage(message);
			SimpMessageHeaderAccessor connectAck = SimpMessageHeaderAccessor.create(SimpMessageType.CONNECT_ACK);
			initHeaders(connectAck);
			connectAck.setSessionId(sessionId);
			connectAck.setUser(SimpMessageHeaderAccessor.getUser(headers));
			connectAck.setHeader(SimpMessageHeaderAccessor.CONNECT_MESSAGE_HEADER, message);
			Message<byte[]> messageOut = MessageBuilder.createMessage(EMPTY_PAYLOAD, connectAck.getMessageHeaders());
			getClientOutboundChannel().send(messageOut);
		}
		else if (SimpMessageType.DISCONNECT.equals(messageType)) {
			logMessage(message);
			this.subscriptionRegistry.unregisterAllSubscriptions(sessionId);
			SimpMessageHeaderAccessor disconnectAck = SimpMessageHeaderAccessor.create(SimpMessageType.DISCONNECT_ACK);
			initHeaders(disconnectAck);
			disconnectAck.setSessionId(sessionId);
			disconnectAck.setUser(SimpMessageHeaderAccessor.getUser(headers));
			Message<byte[]> messageOut = MessageBuilder.createMessage(EMPTY_PAYLOAD, disconnectAck.getMessageHeaders());
			getClientOutboundChannel().send(messageOut);
		}
		else if (SimpMessageType.SUBSCRIBE.equals(messageType)) {
			logMessage(message);
			this.subscriptionRegistry.registerSubscription(message);
		}
		else if (SimpMessageType.UNSUBSCRIBE.equals(messageType)) {
			logMessage(message);
			this.subscriptionRegistry.unregisterSubscription(message);
		}
	}

	private void logMessage(Message<?> message) {
		if (logger.isDebugEnabled()) {
			SimpMessageHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, SimpMessageHeaderAccessor.class);
			accessor = (accessor != null ? accessor : SimpMessageHeaderAccessor.wrap(message));
			logger.debug("Processing " + accessor.getShortLogMessage(message.getPayload()));
		}
	}

	private void initHeaders(SimpMessageHeaderAccessor accessor) {
		if (getHeaderInitializer() != null) {
			getHeaderInitializer().initHeaders(accessor);
		}
	}

	protected void sendMessageToSubscribers(String destination, Message<?> message) {
		MultiValueMap<String,String> subscriptions = this.subscriptionRegistry.findSubscriptions(message);
		if ((subscriptions.size() > 0) && logger.isDebugEnabled()) {
			logger.debug("Broadcasting to " + subscriptions.size() + " sessions.");
		}
		for (String sessionId : subscriptions.keySet()) {
			for (String subscriptionId : subscriptions.get(sessionId)) {
				SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
				initHeaders(headerAccessor);
				headerAccessor.setSessionId(sessionId);
				headerAccessor.setSubscriptionId(subscriptionId);
				headerAccessor.copyHeadersIfAbsent(message.getHeaders());
				Object payload = message.getPayload();
				Message<?> reply = MessageBuilder.createMessage(payload, headerAccessor.getMessageHeaders());
				try {
					getClientOutboundChannel().send(reply);
				}
				catch (Throwable ex) {
					logger.error("Failed to send " + message, ex);
				}
			}
		}
	}

	@Override
	public String toString() {
		return "SimpleBroker[" + this.subscriptionRegistry + "]";
	}

}
