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
 * Operations for sending messages to and receiving the reply from a destination.
 *
 * <p>
 *  从目的地发送消息并从目的地接收回复的操作
 * 
 * 
 * @param <D> the type of destination
 *
 * @author Mark Fisher
 * @author Rossen Stoyanchev
 * @since 4.0
 * @see GenericMessagingTemplate
 */
public interface MessageRequestReplyOperations<D> {

	/**
	 * Send a request message and receive the reply from a default destination.
	 * <p>
	 *  发送请求消息并从默认目标接收回复
	 * 
	 * 
	 * @param requestMessage the message to send
	 * @return the reply, possibly {@code null} if the message could not be received,
	 * for example due to a timeout
	 */
	Message<?> sendAndReceive(Message<?> requestMessage) throws MessagingException;

	/**
	 * Send a request message and receive the reply from the given destination.
	 * <p>
	 * 发送请求消息并从给定目的地接收回复
	 * 
	 * 
	 * @param destination the target destination
	 * @param requestMessage the message to send
	 * @return the reply, possibly {@code null} if the message could not be received,
	 * for example due to a timeout
	 */
	Message<?> sendAndReceive(D destination, Message<?> requestMessage) throws MessagingException;

	/**
	 * Convert the given request Object to serialized form, possibly using a
	 * {@link org.springframework.messaging.converter.MessageConverter}, send
	 * it as a {@link Message} to a default destination, receive the reply and convert
	 * its body of the specified target class.
	 * <p>
	 *  将给定的请求对象转换为序列化形式,可能使用{@link orgspringframeworkmessagingconverterMessageConverter}将其作为{@link Message}
	 * 发送到默认目标位置,接收回复并转换其指定目标类的正文。
	 * 
	 * 
	 * @param request payload for the request message to send
	 * @param targetClass the target type to convert the payload of the reply to
	 * @return the payload of the reply message, possibly {@code null} if the message
	 * could not be received, for example due to a timeout
	 */
	<T> T convertSendAndReceive(Object request, Class<T> targetClass) throws MessagingException;

	/**
	 * Convert the given request Object to serialized form, possibly using a
	 * {@link org.springframework.messaging.converter.MessageConverter}, send
	 * it as a {@link Message} to the given destination, receive the reply and convert
	 * its body of the specified target class.
	 * <p>
	 *  将给定的请求对象转换为序列化形式,可能使用{@link orgspringframeworkmessagingconverterMessageConverter}将其作为{@link Message}
	 * 发送到给定目标,接收回复并转换其指定目标类的正文。
	 * 
	 * 
	 * @param destination the target destination
	 * @param request payload for the request message to send
	 * @param targetClass the target type to convert the payload of the reply to
	 * @return the payload of the reply message, possibly {@code null} if the message
	 * could not be received, for example due to a timeout
	 */
	<T> T convertSendAndReceive(D destination, Object request, Class<T> targetClass) throws MessagingException;

	/**
	 * Convert the given request Object to serialized form, possibly using a
	 * {@link org.springframework.messaging.converter.MessageConverter}, send
	 * it as a {@link Message} with the given headers, to the specified destination,
	 * receive the reply and convert its body of the specified target class.
	 * <p>
	 * 将给定的请求对象转换为序列化形式,可能使用{@link orgspringframeworkmessagingconverterMessageConverter}将其作为具有给定标题的{@link Message}
	 * 发送到指定的目标,接收回复并转换其指定目标类的正文。
	 * 
	 * 
	 * @param destination the target destination
	 * @param request payload for the request message to send
	 * @param headers headers for the request message to send
	 * @param targetClass the target type to convert the payload of the reply to
	 * @return the payload of the reply message, possibly {@code null} if the message
	 * could not be received, for example due to a timeout
	 */
	<T> T convertSendAndReceive(D destination, Object request, Map<String, Object> headers, Class<T> targetClass)
			throws MessagingException;

	/**
	 * Convert the given request Object to serialized form, possibly using a
	 * {@link org.springframework.messaging.converter.MessageConverter},
	 * apply the given post processor and send the resulting {@link Message} to a
	 * default destination, receive the reply and convert its body of the given
	 * target class.
	 * <p>
	 *  将给定的请求对象转换为序列化形式,可能使用{@link orgspringframeworkmessagingconverterMessageConverter},应用给定的后处理器并将生成的{@link Message}
	 * 发送到默认目标位置,接收回复并转换其给定目标类的身体。
	 * 
	 * 
	 * @param request payload for the request message to send
	 * @param targetClass the target type to convert the payload of the reply to
	 * @param requestPostProcessor post process to apply to the request message
	 * @return the payload of the reply message, possibly {@code null} if the message
	 * could not be received, for example due to a timeout
	 */
	<T> T convertSendAndReceive(Object request, Class<T> targetClass, MessagePostProcessor requestPostProcessor)
			throws MessagingException;

	/**
	 * Convert the given request Object to serialized form, possibly using a
	 * {@link org.springframework.messaging.converter.MessageConverter},
	 * apply the given post processor and send the resulting {@link Message} to the
	 * given destination, receive the reply and convert its body of the given
	 * target class.
	 * <p>
	 * 将给定的请求对象转换为序列化形式,可能使用{@link orgspringframeworkmessagingconverterMessageConverter},应用给定的后处理器并将生成的{@link Message}
	 * 发送到给定的目的地,接收回复并转换其给定目标类的身体。
	 * 
	 * 
	 * @param destination the target destination
	 * @param request payload for the request message to send
	 * @param targetClass the target type to convert the payload of the reply to
	 * @param requestPostProcessor post process to apply to the request message
	 * @return the payload of the reply message, possibly {@code null} if the message
	 * could not be received, for example due to a timeout
	 */
	<T> T convertSendAndReceive(D destination, Object request, Class<T> targetClass,
			MessagePostProcessor requestPostProcessor) throws MessagingException;

	/**
	 * Convert the given request Object to serialized form, possibly using a
	 * {@link org.springframework.messaging.converter.MessageConverter},
	 * wrap it as a message with the given headers, apply the given post processor
	 * and send the resulting {@link Message} to the specified destination, receive
	 * the reply and convert its body of the given target class.
	 * <p>
	 *  将给定的请求对象转换为序列化形式,可能使用{@link orgspringframeworkmessagingconverterMessageConverter}将其作为具有给定标题的消息进行包装,应
	 * 用给定的后处理器并将生成的{@link消息}发送到指定的目标,接收回复并转换其给定目标类的身体。
	 * 
	 * @param destination the target destination
	 * @param request payload for the request message to send
	 * @param targetClass the target type to convert the payload of the reply to
	 * @param requestPostProcessor post process to apply to the request message
	 * @return the payload of the reply message, possibly {@code null} if the message
	 * could not be received, for example due to a timeout
	 */
	<T> T convertSendAndReceive(D destination, Object request, Map<String, Object> headers,
			Class<T> targetClass, MessagePostProcessor requestPostProcessor) throws MessagingException;

}
