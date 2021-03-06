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

package org.springframework.messaging.core;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.util.Assert;

/**
 * A messaging template that resolves destinations names to {@link MessageChannel}'s
 * to send and receive messages from.
 *
 * <p>
 *  将目的地名称解析为{@link MessageChannel}以发送和接收邮件的消息传递模板
 * 
 * 
 * @author Mark Fisher
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public class GenericMessagingTemplate extends AbstractDestinationResolvingMessagingTemplate<MessageChannel>
		implements BeanFactoryAware {

	private volatile long sendTimeout = -1;

	private volatile long receiveTimeout = -1;

	private volatile boolean throwExceptionOnLateReply = false;


	/**
	 * Configure the timeout value to use for send operations.
	 * <p>
	 *  配置用于发送操作的超时值
	 * 
	 * 
	 * @param sendTimeout the send timeout in milliseconds
	 */
	public void setSendTimeout(long sendTimeout) {
		this.sendTimeout = sendTimeout;
	}

	/**
	 * Return the configured send operation timeout value.
	 * <p>
	 * 返回配置的发送操作超时值
	 * 
	 */
	public long getSendTimeout() {
		return this.sendTimeout;
	}

	/**
	 * Configure the timeout value to use for receive operations.
	 * <p>
	 *  配置用于接收操作的超时值
	 * 
	 * 
	 * @param receiveTimeout the receive timeout in milliseconds
	 */
	public void setReceiveTimeout(long receiveTimeout) {
		this.receiveTimeout = receiveTimeout;
	}

	/**
	 * Return the configured receive operation timeout value.
	 * <p>
	 *  返回配置的接收操作超时值
	 * 
	 */
	public long getReceiveTimeout() {
		return this.receiveTimeout;
	}

	/**
	 * Whether the thread sending a reply should have an exception raised if the
	 * receiving thread isn't going to receive the reply either because it timed out,
	 * or because it already received a reply, or because it got an exception while
	 * sending the request message.
	 * <p>The default value is {@code false} in which case only a WARN message is logged.
	 * If set to {@code true} a {@link MessageDeliveryException} is raised in addition
	 * to the log message.
	 * <p>
	 *  发送回复的线程是否应该发生异常,如果接收线程不会收到回复,因为它已经超时,或者因为它已经收到回复,或者因为在发送请求消息时发生异常>默认值为{@code false},在这种情况下,仅记录WARN消息
	 * 如果设置为{@code true},除了日志消息之外还引发了{@link MessageDeliveryException}。
	 * 
	 * 
	 * @param throwExceptionOnLateReply whether to throw an exception or not
	 */
	public void setThrowExceptionOnLateReply(boolean throwExceptionOnLateReply) {
		this.throwExceptionOnLateReply = throwExceptionOnLateReply;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		super.setDestinationResolver(new BeanFactoryMessageChannelDestinationResolver(beanFactory));
	}


	@Override
	protected final void doSend(MessageChannel channel, Message<?> message) {
		Assert.notNull(channel, "'channel' is required");

		MessageHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, MessageHeaderAccessor.class);
		if (accessor != null && accessor.isMutable()) {
			accessor.setImmutable();
		}

		long timeout = this.sendTimeout;
		boolean sent = (timeout >= 0 ? channel.send(message, timeout) : channel.send(message));

		if (!sent) {
			throw new MessageDeliveryException(message,
					"failed to send message to channel '" + channel + "' within timeout: " + timeout);
		}
	}

	@Override
	protected final Message<?> doReceive(MessageChannel channel) {
		Assert.notNull(channel, "'channel' is required");
		Assert.state(channel instanceof PollableChannel, "A PollableChannel is required to receive messages");

		long timeout = this.receiveTimeout;
		Message<?> message = (timeout >= 0 ?
				((PollableChannel) channel).receive(timeout) : ((PollableChannel) channel).receive());

		if (message == null && this.logger.isTraceEnabled()) {
			this.logger.trace("Failed to receive message from channel '" + channel + "' within timeout: " + timeout);
		}

		return message;
	}

	@Override
	protected final Message<?> doSendAndReceive(MessageChannel channel, Message<?> requestMessage) {
		Assert.notNull(channel, "'channel' is required");
		Object originalReplyChannelHeader = requestMessage.getHeaders().getReplyChannel();
		Object originalErrorChannelHeader = requestMessage.getHeaders().getErrorChannel();

		TemporaryReplyChannel tempReplyChannel = new TemporaryReplyChannel();
		requestMessage = MessageBuilder.fromMessage(requestMessage).setReplyChannel(tempReplyChannel).
				setErrorChannel(tempReplyChannel).build();

		try {
			doSend(channel, requestMessage);
		}
		catch (RuntimeException ex) {
			tempReplyChannel.setSendFailed(true);
			throw ex;
		}

		Message<?> replyMessage = this.doReceive(tempReplyChannel);
		if (replyMessage != null) {
			replyMessage = MessageBuilder.fromMessage(replyMessage)
					.setHeader(MessageHeaders.REPLY_CHANNEL, originalReplyChannelHeader)
					.setHeader(MessageHeaders.ERROR_CHANNEL, originalErrorChannelHeader)
					.build();
		}

		return replyMessage;
	}


	/**
	 * A temporary channel for receiving a single reply message.
	 * <p>
	 */
	private class TemporaryReplyChannel implements PollableChannel {

		private final Log logger = LogFactory.getLog(TemporaryReplyChannel.class);

		private final CountDownLatch replyLatch = new CountDownLatch(1);

		private volatile Message<?> replyMessage;

		private volatile boolean hasReceived;

		private volatile boolean hasTimedOut;

		private volatile boolean hasSendFailed;

		public void setSendFailed(boolean hasSendError) {
			this.hasSendFailed = hasSendError;
		}

		@Override
		public Message<?> receive() {
			return this.receive(-1);
		}

		@Override
		public Message<?> receive(long timeout) {
			try {
				if (GenericMessagingTemplate.this.receiveTimeout < 0) {
					this.replyLatch.await();
					this.hasReceived = true;
				}
				else {
					if (this.replyLatch.await(GenericMessagingTemplate.this.receiveTimeout, TimeUnit.MILLISECONDS)) {
						this.hasReceived = true;
					}
					else {
						this.hasTimedOut = true;
					}
				}
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			return this.replyMessage;
		}

		@Override
		public boolean send(Message<?> message) {
			return this.send(message, -1);
		}

		@Override
		public boolean send(Message<?> message, long timeout) {
			this.replyMessage = message;
			boolean alreadyReceivedReply = this.hasReceived;
			this.replyLatch.countDown();

			String errorDescription = null;
			if (this.hasTimedOut) {
				errorDescription = "Reply message received but the receiving thread has exited due to a timeout";
			}
			else if (alreadyReceivedReply) {
				errorDescription = "Reply message received but the receiving thread has already received a reply";
			}
			else if (this.hasSendFailed) {
				errorDescription = "Reply message received but the receiving thread has exited due to " +
						"an exception while sending the request message";
			}

			if (errorDescription != null) {
				if (logger.isWarnEnabled()) {
					logger.warn(errorDescription + ":" + message);
				}
				if (GenericMessagingTemplate.this.throwExceptionOnLateReply) {
					throw new MessageDeliveryException(message, errorDescription);
				}
			}

			return true;
		}
	}

}
