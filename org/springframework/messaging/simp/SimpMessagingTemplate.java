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

import java.util.Map;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.core.AbstractMessageSendingTemplate;
import org.springframework.messaging.core.MessagePostProcessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderInitializer;
import org.springframework.messaging.support.NativeMessageHeaderAccessor;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * An implementation of
 * {@link org.springframework.messaging.simp.SimpMessageSendingOperations}.
 *
 * <p>Also provides methods for sending messages to a user. See
 * {@link org.springframework.messaging.simp.user.UserDestinationResolver
 * UserDestinationResolver}
 * for more on user destinations.
 *
 * <p>
 *  执行{@link orgspringframeworkmessagingsimpSimpMessageSendingOperations}
 * 
 * <p>还提供了向用户发送消息的方法有关用户目的地的更多信息,请参阅{@link orgspringframeworkmessagingsimpuserUserDestinationResolver UserDestinationResolver}
 * 。
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public class SimpMessagingTemplate extends AbstractMessageSendingTemplate<String>
		implements SimpMessageSendingOperations {

	private final MessageChannel messageChannel;

	private String destinationPrefix = "/user/";

	private volatile long sendTimeout = -1;

	private MessageHeaderInitializer headerInitializer;


	/**
	 * Create a new {@link SimpMessagingTemplate} instance.
	 * <p>
	 *  创建一个新的{@link SimpMessagingTemplate}实例
	 * 
	 * 
	 * @param messageChannel the message channel (must not be {@code null})
	 */
	public SimpMessagingTemplate(MessageChannel messageChannel) {
		Assert.notNull(messageChannel, "'messageChannel' must not be null");
		this.messageChannel = messageChannel;
	}


	/**
	 * Return the configured message channel.
	 * <p>
	 *  返回配置的消息通道
	 * 
	 */
	public MessageChannel getMessageChannel() {
		return this.messageChannel;
	}

	/**
	 * Configure the prefix to use for destinations targeting a specific user.
	 * <p>The default value is "/user/".
	 * <p>
	 *  配置用于指定特定用户的目的地的前缀<p>默认值为"/ user /"
	 * 
	 * 
	 * @see org.springframework.messaging.simp.user.UserDestinationMessageHandler
	 */
	public void setUserDestinationPrefix(String prefix) {
		Assert.hasText(prefix, "'destinationPrefix' must not be empty");
		this.destinationPrefix = prefix.endsWith("/") ? prefix : prefix + "/";

	}

	/**
	 * Return the configured user destination prefix.
	 * <p>
	 *  返回配置的用户目标前缀
	 * 
	 */
	public String getUserDestinationPrefix() {
		return this.destinationPrefix;
	}

	/**
	 * Specify the timeout value to use for send operations (in milliseconds).
	 * <p>
	 *  指定用于发送操作的超时值(以毫秒为单位)
	 * 
	 */
	public void setSendTimeout(long sendTimeout) {
		this.sendTimeout = sendTimeout;
	}

	/**
	 * Return the configured send timeout (in milliseconds).
	 * <p>
	 *  返回配置的发送超时(以毫秒为单位)
	 * 
	 */
	public long getSendTimeout() {
		return this.sendTimeout;
	}

	/**
	 * Configure a {@link MessageHeaderInitializer} to apply to the headers of all
	 * messages created through the {@code SimpMessagingTemplate}.
	 * <p>By default, this property is not set.
	 * <p>
	 *  配置{@link MessageHeaderInitializer}以应用于通过{@code SimpMessagingTemplate} <p>创建的所有邮件的标头。默认情况下,此属性未设置
	 * 
	 */
	public void setHeaderInitializer(MessageHeaderInitializer headerInitializer) {
		this.headerInitializer = headerInitializer;
	}

	/**
	 * Return the configured header initializer.
	 * <p>
	 *  返回配置的标头初始化程序
	 * 
	 */
	public MessageHeaderInitializer getHeaderInitializer() {
		return this.headerInitializer;
	}


	/**
	 * If the headers of the given message already contain a
	 * {@link org.springframework.messaging.simp.SimpMessageHeaderAccessor#DESTINATION_HEADER
	 * SimpMessageHeaderAccessor#DESTINATION_HEADER} then the message is sent without
	 * further changes.
	 * <p>If a destination header is not already present ,the message is sent
	 * to the configured {@link #setDefaultDestination(Object) defaultDestination}
	 * or an exception an {@code IllegalStateException} is raised if that isn't
	 * configured.
	 * <p>
	 * 如果给定消息的标头已经包含一个{@link orgspringframeworkmessagingsimpSimpMessageHeaderAccessor#DESTINATION_HEADER SimpMessageHeaderAccessor#DESTINATION_HEADER}
	 * ,那么该消息将不进一步更改而被发送<p>如果目标头不存在,则该消息将发送到已配置的{@link #setDefaultDestination(Object)defaultDestination}或一个异
	 * 常,如果没有配置,则引发{@code IllegalStateException}。
	 * 
	 * 
	 * @param message the message to send (never {@code null})
	 */
	@Override
	public void send(Message<?> message) {
		Assert.notNull(message, "'message' is required");
		String destination = SimpMessageHeaderAccessor.getDestination(message.getHeaders());
		if (destination != null) {
			sendInternal(message);
			return;
		}
		doSend(getRequiredDefaultDestination(), message);
	}

	@Override
	protected void doSend(String destination, Message<?> message) {
		Assert.notNull(destination, "Destination must not be null");

		SimpMessageHeaderAccessor simpAccessor =
				MessageHeaderAccessor.getAccessor(message, SimpMessageHeaderAccessor.class);

		if (simpAccessor != null) {
			if (simpAccessor.isMutable()) {
				simpAccessor.setDestination(destination);
				simpAccessor.setMessageTypeIfNotSet(SimpMessageType.MESSAGE);
				simpAccessor.setImmutable();
				sendInternal(message);
				return;
			}
			else {
				// Try and keep the original accessor type
				simpAccessor = (SimpMessageHeaderAccessor) MessageHeaderAccessor.getMutableAccessor(message);
				initHeaders(simpAccessor);
			}
		}
		else {
			simpAccessor = SimpMessageHeaderAccessor.wrap(message);
			initHeaders(simpAccessor);
		}

		simpAccessor.setDestination(destination);
		simpAccessor.setMessageTypeIfNotSet(SimpMessageType.MESSAGE);
		message = MessageBuilder.createMessage(message.getPayload(), simpAccessor.getMessageHeaders());
		sendInternal(message);
	}

	private void sendInternal(Message<?> message) {
		String destination = SimpMessageHeaderAccessor.getDestination(message.getHeaders());
		Assert.notNull(destination);

		long timeout = this.sendTimeout;
		boolean sent = (timeout >= 0 ? this.messageChannel.send(message, timeout) : this.messageChannel.send(message));

		if (!sent) {
			throw new MessageDeliveryException(message,
					"Failed to send message to destination '" + destination + "' within timeout: " + timeout);
		}
	}

	private void initHeaders(SimpMessageHeaderAccessor simpAccessor) {
		if (getHeaderInitializer() != null) {
			getHeaderInitializer().initHeaders(simpAccessor);
		}
	}


	@Override
	public void convertAndSendToUser(String user, String destination, Object payload) throws MessagingException {
		convertAndSendToUser(user, destination, payload, (MessagePostProcessor) null);
	}

	@Override
	public void convertAndSendToUser(String user, String destination, Object payload,
			Map<String, Object> headers) throws MessagingException {

		convertAndSendToUser(user, destination, payload, headers, null);
	}

	@Override
	public void convertAndSendToUser(String user, String destination, Object payload,
			MessagePostProcessor postProcessor) throws MessagingException {

		convertAndSendToUser(user, destination, payload, null, postProcessor);
	}

	@Override
	public void convertAndSendToUser(String user, String destination, Object payload, Map<String, Object> headers,
			MessagePostProcessor postProcessor) throws MessagingException {

		Assert.notNull(user, "User must not be null");
		user = StringUtils.replace(user, "/", "%2F");
		super.convertAndSend(this.destinationPrefix + user + destination, payload, headers, postProcessor);
	}


	/**
	 * Creates a new map and puts the given headers under the key
	 * {@link org.springframework.messaging.support.NativeMessageHeaderAccessor#NATIVE_HEADERS NATIVE_HEADERS NATIVE_HEADERS NATIVE_HEADERS}.
	 * effectively treats the input header map as headers to be sent out to the
	 * destination.
	 * <p>However if the given headers already contain the key
	 * {@code NATIVE_HEADERS NATIVE_HEADERS} then the same headers instance is
	 * returned without changes.
	 * <p>Also if the given headers were prepared and obtained with
	 * {@link SimpMessageHeaderAccessor#getMessageHeaders()} then the same headers
	 * instance is also returned without changes.
	 * <p>
	 * 创建一个新的地图并将给定的标题放在关键字下{@link orgspringframeworkmessagingsupportNativeMessageHeaderAccessor#NATIVE_HEADERS NATIVE_HEADERS NATIVE_HEADERS NATIVE_HEADERS}
	 * 有效地将输入标题地图视为要发送到目的地的标题<p>但是,如果给定的标头已经包含密钥{ @code NATIVE_HEADERS NATIVE_HEADERS}然后返回相同的头部实例,而不会发生变化<p>
	 */
	@Override
	protected Map<String, Object> processHeadersToSend(Map<String, Object> headers) {
		if (headers == null) {
			SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
			initHeaders(headerAccessor);
			headerAccessor.setLeaveMutable(true);
			return headerAccessor.getMessageHeaders();
		}
		if (headers.containsKey(NativeMessageHeaderAccessor.NATIVE_HEADERS)) {
			return headers;
		}
		if (headers instanceof MessageHeaders) {
			SimpMessageHeaderAccessor accessor =
					MessageHeaderAccessor.getAccessor((MessageHeaders) headers, SimpMessageHeaderAccessor.class);
			if (accessor != null) {
				return headers;
			}
		}

		SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
		initHeaders(headerAccessor);
		for (String key : headers.keySet()) {
			Object value = headers.get(key);
			headerAccessor.setNativeHeader(key, (value != null ? value.toString() : null));
		}
		return headerAccessor.getMessageHeaders();
	}

}
