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

package org.springframework.messaging.simp.annotation.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.handler.invocation.HandlerMethodReturnValueHandler;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.support.MessageHeaderInitializer;
import org.springframework.util.Assert;

/**
 * A {@link HandlerMethodReturnValueHandler} for replying directly to a subscription.
 * It is supported on methods annotated with
 * {@link org.springframework.messaging.simp.annotation.SubscribeMapping}
 * unless they're also annotated with {@link SendTo} or {@link SendToUser} in
 * which case a message is sent to the broker instead.
 *
 * <p>The value returned from the method is converted, and turned to a {@link Message}
 * and then enriched with the sessionId, subscriptionId, and destination of the
 * input message. The message is then sent directly back to the connected client.
 *
 * <p>
 * 用于直接回复到订阅的{@link HandlerMethodReturnValueHandler}在{@link orgspringframeworkmessagingsimpannotationSubscribeMapping}
 * 注释的方法上支持它,除非它们还带有{@link SendTo}或{@link SendToUser}注释,在这种情况下,发送一条消息代理商。
 * 
 *  <p>从方法返回的值被转换,并转换为{@link消息},然后丰富输入消息的sessionId,subscriptionId和目标消息然后直接发送回连接的客户端
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public class SubscriptionMethodReturnValueHandler implements HandlerMethodReturnValueHandler {

	private static Log logger = LogFactory.getLog(SubscriptionMethodReturnValueHandler.class);


	private final MessageSendingOperations<String> messagingTemplate;

	private MessageHeaderInitializer headerInitializer;


	/**
	 * Construct a new SubscriptionMethodReturnValueHandler.
	 * <p>
	 *  构造一个新的SubscriptionMethodReturnValueHandler
	 * 
	 * 
	 * @param messagingTemplate a messaging template to send messages to,
	 * most likely the "clientOutboundChannel" (must not be {@code null})
	 */
	public SubscriptionMethodReturnValueHandler(MessageSendingOperations<String> messagingTemplate) {
		Assert.notNull(messagingTemplate, "messagingTemplate must not be null");
		this.messagingTemplate = messagingTemplate;
	}


	/**
	 * Configure a {@link MessageHeaderInitializer} to apply to the headers of all
	 * messages sent to the client outbound channel.
	 * <p>By default this property is not set.
	 * <p>
	 *  配置{@link MessageHeaderInitializer}以应用于发送到客户端出站通道的所有邮件的标头<p>默认情况下,此属性未设置
	 * 
	 */
	public void setHeaderInitializer(MessageHeaderInitializer headerInitializer) {
		this.headerInitializer = headerInitializer;
	}

	/**
	 * Return the configured header initializer.
	 * <p>
	 * 返回配置的标头初始化程序
	 */
	public MessageHeaderInitializer getHeaderInitializer() {
		return this.headerInitializer;
	}


	@Override
	public boolean supportsReturnType(MethodParameter returnType) {
		return (returnType.getMethodAnnotation(SubscribeMapping.class) != null &&
				returnType.getMethodAnnotation(SendTo.class) == null &&
				returnType.getMethodAnnotation(SendToUser.class) == null);
	}

	@Override
	public void handleReturnValue(Object returnValue, MethodParameter returnType, Message<?> message) throws Exception {
		if (returnValue == null) {
			return;
		}

		MessageHeaders headers = message.getHeaders();
		String destination = SimpMessageHeaderAccessor.getDestination(headers);
		String sessionId = SimpMessageHeaderAccessor.getSessionId(headers);
		String subscriptionId = SimpMessageHeaderAccessor.getSubscriptionId(headers);

		Assert.state(subscriptionId != null,
				"No subscriptionId in message=" + message + ", method=" + returnType.getMethod());

		if (logger.isDebugEnabled()) {
			logger.debug("Reply to @SubscribeMapping: " + returnValue);
		}

		this.messagingTemplate.convertAndSend(destination, returnValue, createHeaders(sessionId, subscriptionId));
	}

	private MessageHeaders createHeaders(String sessionId, String subscriptionId) {
		SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
		if (getHeaderInitializer() != null) {
			getHeaderInitializer().initHeaders(headerAccessor);
		}
		headerAccessor.setSessionId(sessionId);
		headerAccessor.setSubscriptionId(subscriptionId);
		headerAccessor.setLeaveMutable(true);
		return headerAccessor.getMessageHeaders();
	}

}
