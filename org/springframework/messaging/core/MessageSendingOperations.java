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
import org.springframework.messaging.MessagingException;

/**
 * Operations for sending messages to a destination.
 *
 * <p>
 *  将消息发送到目的地的操作
 * 
 * 
 * @param <D> the type of destination to send messages to
 *
 * @author Mark Fisher
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public interface MessageSendingOperations<D> {

	/**
	 * Send a message to a default destination.
	 * <p>
	 *  发送消息到默认目的地
	 * 
	 * 
	 * @param message the message to send
	 */
	void send(Message<?> message) throws MessagingException;

	/**
	 * Send a message to the given destination.
	 * <p>
	 *  向给定目的地发送消息
	 * 
	 * 
	 * @param destination the target destination
	 * @param message the message to send
	 */
	void send(D destination, Message<?> message) throws MessagingException;

	/**
	 * Convert the given Object to serialized form, possibly using a
	 * {@link org.springframework.messaging.converter.MessageConverter},
	 * wrap it as a message and send it to a default destination.
	 * <p>
	 * 将给定的对象转换为序列化形式,可能使用{@link orgspringframeworkmessagingconverterMessageConverter}将其包装为消息并将其发送到默认目标
	 * 
	 * 
	 * @param payload the Object to use as payload
	 */
	void convertAndSend(Object payload) throws MessagingException;

	/**
	 * Convert the given Object to serialized form, possibly using a
	 * {@link org.springframework.messaging.converter.MessageConverter},
	 * wrap it as a message and send it to the given destination.
	 * <p>
	 *  将给定的对象转换为序列化形式,可能使用{@link orgspringframeworkmessagingconverterMessageConverter}将其作为消息包装,并将其发送到给定的目标。
	 * 
	 * 
	 * @param destination the target destination
	 * @param payload the Object to use as payload
	 */
	void convertAndSend(D destination, Object payload) throws MessagingException;

	/**
	 * Convert the given Object to serialized form, possibly using a
	 * {@link org.springframework.messaging.converter.MessageConverter},
	 * wrap it as a message with the given headers and send it to
	 * the given destination.
	 * <p>
	 *  将给定的对象转换为序列化形式,可能使用{@link orgspringframeworkmessagingconverterMessageConverter}将其作为包含给定标题的消息进行包装,并将其
	 * 发送到给定的目标。
	 * 
	 * 
	 * @param destination the target destination
	 * @param payload the Object to use as payload
	 * @param headers headers for the message to send
	 */
	void convertAndSend(D destination, Object payload, Map<String, Object> headers) throws MessagingException;

	/**
	 * Convert the given Object to serialized form, possibly using a
	 * {@link org.springframework.messaging.converter.MessageConverter},
	 * wrap it as a message, apply the given post processor, and send
	 * the resulting message to a default destination.
	 * <p>
	 * 将给定的对象转换为序列化形式,可能使用{@link orgspringframeworkmessagingconverterMessageConverter}将其包装为消息,应用给定的后处理器,并将生成
	 * 的消息发送到默认目标。
	 * 
	 * 
	 * @param payload the Object to use as payload
	 * @param postProcessor the post processor to apply to the message
	 */
	void convertAndSend(Object payload, MessagePostProcessor postProcessor) throws MessagingException;

	/**
	 * Convert the given Object to serialized form, possibly using a
	 * {@link org.springframework.messaging.converter.MessageConverter},
	 * wrap it as a message, apply the given post processor, and send
	 * the resulting message to the given destination.
	 * <p>
	 *  将给定的对象转换为序列化形式,可能使用{@link orgspringframeworkmessagingconverterMessageConverter}将其作为消息包装,应用给定的后处理器,并将
	 * 生成的消息发送到给定的目标。
	 * 
	 * 
	 * @param destination the target destination
	 * @param payload the Object to use as payload
	 * @param postProcessor the post processor to apply to the message
	 */
	void convertAndSend(D destination, Object payload, MessagePostProcessor postProcessor) throws MessagingException;

	/**
	 * Convert the given Object to serialized form, possibly using a
	 * {@link org.springframework.messaging.converter.MessageConverter},
	 * wrap it as a message with the given headers, apply the given post processor,
	 * and send the resulting message to the given destination.
	 * <p>
	 *  将给定的对象转换为序列化形式,可能使用{@link orgspringframeworkmessagingconverterMessageConverter}将其包装为具有给定标题的消息,应用给定的后
	 * 处理器,并将生成的消息发送到给定的目标。
	 * 
	 * @param destination the target destination
	 * @param payload the Object to use as payload
	 * @param headers headers for the message to send
	 * @param postProcessor the post processor to apply to the message
	 */
	void convertAndSend(D destination, Object payload, Map<String, Object> headers, MessagePostProcessor postProcessor)
			throws MessagingException;

}
