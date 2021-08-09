/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2013 the original author or authors.
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
 * Extends {@link MessageSendingOperations} and adds operations for sending messages
 * to a destination specified as a (resolvable) String name.
 *
 * <p>
 *  扩展{@link MessageSendingOperations}并添加将消息发送到指定为(可解析的)字符串名称的目标的操作
 * 
 * 
 * @author Mark Fisher
 * @author Rossen Stoyanchev
 * @since 4.0
 * @see DestinationResolver
 */
public interface DestinationResolvingMessageSendingOperations<D> extends MessageSendingOperations<D> {

	/**
	 * Resolve the given destination name to a destination and send a message to it.
	 * <p>
	 *  将给定的目的地名称解析为目的地并向其发送消息
	 * 
	 * 
	 * @param destinationName the destination name to resolve
	 * @param message the message to send
	 */
	void send(String destinationName, Message<?> message) throws MessagingException;

	/**
	 * Resolve the given destination name to a destination, convert the payload Object
	 * to serialized form, possibly using a
	 * {@link org.springframework.messaging.converter.MessageConverter},
	 * wrap it as a message and send it to the resolved destination.
	 * <p>
	 * 将给定的目标名称解析为目标,将有效载荷对象转换为序列化形式,可能使用{@link orgspringframeworkmessagingconverterMessageConverter}将其作为消息包
	 * 装并发送到已解析的目标。
	 * 
	 * 
	 * @param destinationName the destination name to resolve
   	 * @param payload the Object to use as payload
	 */
	<T> void convertAndSend(String destinationName, T payload) throws MessagingException;

	/**
	 * Resolve the given destination name to a destination, convert the payload
	 * Object to serialized form, possibly using a
	 * {@link org.springframework.messaging.converter.MessageConverter},
	 * wrap it as a message with the given headers and send it to the resolved
	 * destination.
	 * <p>
	 *  将给定的目标名称解析为目标,将有效载荷对象转换为序列化形式,可能使用{@link orgspringframeworkmessagingconverterMessageConverter}将其作为包含
	 * 给定标题的消息进行包装,并将其发送到已解析的目标。
	 * 
	 * 
	 * @param destinationName the destination name to resolve
	 * @param payload the Object to use as payload
 	 * @param headers headers for the message to send
	 */
	<T> void convertAndSend(String destinationName, T payload, Map<String, Object> headers)
			throws MessagingException;

	/**
	 * Resolve the given destination name to a destination, convert the payload
	 * Object to serialized form, possibly using a
	 * {@link org.springframework.messaging.converter.MessageConverter},
	 * wrap it as a message, apply the given post processor, and send the resulting
	 * message to the resolved destination.
	 * <p>
	 * 将给定的目的地名称解析为目标,将有效载荷对象转换为序列化形式,可能使用{@link orgspringframeworkmessagingconverterMessageConverter}将其包装为消
	 * 息,应用给定的后处理器,并将结果消息发送到已解析的目标。
	 * 
	 * 
	 * @param destinationName the destination name to resolve
	 * @param payload the Object to use as payload
	 * @param postProcessor the post processor to apply to the message
	 */
	<T> void convertAndSend(String destinationName, T payload, MessagePostProcessor postProcessor)
			throws MessagingException;

	/**
	 * Resolve the given destination name to a destination, convert the payload
	 * Object to serialized form, possibly using a
	 * {@link org.springframework.messaging.converter.MessageConverter},
	 * wrap it as a message with the given headers, apply the given post processor,
	 * and send the resulting message to the resolved destination.
	 * <p>
	 *  将给定的目标名称解析为目标,将有效载荷对象转换为序列化形式,可能使用{@link orgspringframeworkmessagingconverterMessageConverter}将其包装为带
	 * 有给定标题的消息,应用给定的后处理器,并将生成的消息发送到已解析的目的地。
	 * 
	 * @param destinationName the destination name to resolve
	 * @param payload the Object to use as payload
	 * @param headers headers for the message to send
	 * @param postProcessor the post processor to apply to the message
	 */
	<T> void convertAndSend(String destinationName, T payload, Map<String, Object> headers,
			MessagePostProcessor postProcessor) throws MessagingException;

}
