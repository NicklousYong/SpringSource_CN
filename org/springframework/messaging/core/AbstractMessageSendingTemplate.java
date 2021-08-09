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

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.SimpleMessageConverter;
import org.springframework.util.Assert;

/**
 * Abstract base class for implementations of {@link MessageSendingOperations}.
 *
 * <p>
 *  抽象基类实现{@link MessageSendingOperations}
 * 
 * 
 * @author Mark Fisher
 * @author Rossen Stoyanchev
 * @author Stephane Nicoll
 * @since 4.0
 */
public abstract class AbstractMessageSendingTemplate<D> implements MessageSendingOperations<D> {

	protected final Log logger = LogFactory.getLog(getClass());

	private volatile D defaultDestination;

	private volatile MessageConverter converter = new SimpleMessageConverter();


	/**
	 * Configure the default destination to use in send methods that don't have
	 * a destination argument. If a default destination is not configured, send methods
	 * without a destination argument will raise an exception if invoked.
	 * <p>
	 * 配置不具有目标参数的发送方法中使用的默认目标如果未配置默认目标,则没有目标参数的发送方法会引发异常
	 * 
	 */
	public void setDefaultDestination(D defaultDestination) {
		this.defaultDestination = defaultDestination;
	}

	/**
	 * Return the configured default destination.
	 * <p>
	 *  返回配置的默认目标
	 * 
	 */
	public D getDefaultDestination() {
		return this.defaultDestination;
	}

	/**
	 * Set the {@link MessageConverter} to use in {@code convertAndSend} methods.
	 * <p>By default, {@link SimpleMessageConverter} is used.
	 * <p>
	 *  将{@link MessageConverter}设置为在{@code convertAndSend}方法中使用<p>默认情况下,使用{@link SimpleMessageConverter}
	 * 
	 * 
	 * @param messageConverter the message converter to use
	 */
	public void setMessageConverter(MessageConverter messageConverter) {
		Assert.notNull(messageConverter, "'messageConverter' must not be null");
		this.converter = messageConverter;
	}

	/**
	 * Return the configured {@link MessageConverter}.
	 * <p>
	 *  返回配置的{@link MessageConverter}
	 * 
	 */
	public MessageConverter getMessageConverter() {
		return this.converter;
	}


	@Override
	public void send(Message<?> message) {
		send(getRequiredDefaultDestination(), message);
	}

	protected final D getRequiredDefaultDestination() {
		Assert.state(this.defaultDestination != null, "No 'defaultDestination' configured");
		return this.defaultDestination;
	}

	@Override
	public void send(D destination, Message<?> message) {
		doSend(destination, message);
	}

	protected abstract void doSend(D destination, Message<?> message);


	@Override
	public void convertAndSend(Object payload) throws MessagingException {
		convertAndSend(payload, null);
	}

	@Override
	public void convertAndSend(D destination, Object payload) throws MessagingException {
		convertAndSend(destination, payload, (Map<String, Object>) null);
	}

	@Override
	public void convertAndSend(D destination, Object payload, Map<String, Object> headers) throws MessagingException {
		convertAndSend(destination, payload, headers, null);
	}

	@Override
	public void convertAndSend(Object payload, MessagePostProcessor postProcessor) throws MessagingException {
		convertAndSend(getRequiredDefaultDestination(), payload, postProcessor);
	}

	@Override
	public void convertAndSend(D destination, Object payload, MessagePostProcessor postProcessor)
			throws MessagingException {

		convertAndSend(destination, payload, null, postProcessor);
	}

	@Override
	public void convertAndSend(D destination, Object payload, Map<String, Object> headers,
			MessagePostProcessor postProcessor) throws MessagingException {

		Message<?> message = doConvert(payload, headers, postProcessor);
		send(destination, message);
	}

	/**
	 * Convert the given Object to serialized form, possibly using a
	 * {@link MessageConverter}, wrap it as a message with the given
	 * headers and apply the given post processor.
	 * <p>
	 *  将给定的对象转换为序列化形式,可能使用{@link MessageConverter}将其作为包含给定标题的消息进行包装,并应用给定的后处理器
	 * 
	 * 
	 * @param payload the Object to use as payload
	 * @param headers headers for the message to send
	 * @param postProcessor the post processor to apply to the message
	 * @return the converted message
	 */
	protected Message<?> doConvert(Object payload, Map<String, Object> headers, MessagePostProcessor postProcessor) {
		MessageHeaders messageHeaders = null;
		Map<String, Object> headersToUse = processHeadersToSend(headers);
		if (headersToUse != null) {
			if (headersToUse instanceof MessageHeaders) {
				messageHeaders = (MessageHeaders) headersToUse;
			}
			else {
				messageHeaders = new MessageHeaders(headersToUse);
			}
		}

		Message<?> message = getMessageConverter().toMessage(payload, messageHeaders);
		if (message == null) {
			String payloadType = (payload != null ? payload.getClass().getName() : null);
			Object contentType = (messageHeaders != null ? messageHeaders.get(MessageHeaders.CONTENT_TYPE) : null);
			throw new MessageConversionException("Unable to convert payload with type='" + payloadType +
					"', contentType='" + contentType + "', converter=[" + getMessageConverter() + "]");
		}
		if (postProcessor != null) {
			message = postProcessor.postProcessMessage(message);
		}
		return message;
	}

	/**
	 * Provides access to the map of input headers before a send operation. Sub-classes
	 * can modify the headers and then return the same or a different map.
	 * <p>This default implementation in this class returns the input map.
	 * <p>
	 * 在发送操作之前提供对输入头的映射的访问子类可以修改标题,然后返回相同或不同的映射<p>此类中的默认实现返回输入映射
	 * 
	 * @param headers the headers to send or {@code null}
	 * @return the actual headers to send or {@code null}
	 */
	protected Map<String, Object> processHeadersToSend(Map<String, Object> headers) {
		return headers;
	}

}
