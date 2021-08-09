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

import org.springframework.messaging.Message;

/**
 * An extension of {@link AbstractMessageReceivingTemplate} that adds support for
 * request-reply style operations as defined by {@link MessageRequestReplyOperations}.
 *
 * <p>
 *  除非适用法律要求或以书面形式同意,根据许可证分发的软件以"按原样"分发,不附带任何明示或暗示的担保或条件,请参阅许可证中有关特定语言的权限和限制许可证
 * 
 * 
 * @author Mark Fisher
 * @author Rossen Stoyanchev
 * @author Stephane Nicoll
 * @since 4.0
 */
public abstract class AbstractMessagingTemplate<D> extends AbstractMessageReceivingTemplate<D>
		implements MessageRequestReplyOperations<D> {

	@Override
	public Message<?> sendAndReceive(Message<?> requestMessage) {
		return sendAndReceive(getRequiredDefaultDestination(), requestMessage);
	}

	@Override
	public Message<?> sendAndReceive(D destination, Message<?> requestMessage) {
		return doSendAndReceive(destination, requestMessage);
	}

	protected abstract Message<?> doSendAndReceive(D destination, Message<?> requestMessage);


	@Override
	public <T> T convertSendAndReceive(Object request, Class<T> targetClass) {
		return convertSendAndReceive(getRequiredDefaultDestination(), request, targetClass);
	}

	@Override
	public <T> T convertSendAndReceive(D destination, Object request, Class<T> targetClass) {
		return convertSendAndReceive(destination, request, null, targetClass);
	}

	@Override
	public <T> T convertSendAndReceive(D destination, Object request, Map<String, Object> headers, Class<T> targetClass) {
		return convertSendAndReceive(destination, request, headers, targetClass, null);
	}

	@Override
	public <T> T convertSendAndReceive(Object request, Class<T> targetClass, MessagePostProcessor postProcessor) {
		return convertSendAndReceive(getRequiredDefaultDestination(), request, targetClass, postProcessor);
	}

	@Override
	public <T> T convertSendAndReceive(D destination, Object request, Class<T> targetClass, MessagePostProcessor postProcessor) {
		return convertSendAndReceive(destination, request, null, targetClass, postProcessor);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T convertSendAndReceive(D destination, Object request, Map<String, Object> headers,
			Class<T> targetClass, MessagePostProcessor postProcessor) {

		Message<?> requestMessage = doConvert(request, headers, postProcessor);
		Message<?> replyMessage = sendAndReceive(destination, requestMessage);
		return (replyMessage != null ? (T) getMessageConverter().fromMessage(replyMessage, targetClass) : null);
	}

}
