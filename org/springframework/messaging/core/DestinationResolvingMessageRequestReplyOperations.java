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
 * Extends {@link MessageRequestReplyOperations} and adds operations for sending and
 * receiving messages to and from a destination specified as a (resolvable) String name.
 *
 * <p>
 *  扩展{@link MessageRequestReplyOperations},并添加用于向指定为(可解析的)字符串名称的目的地发送和接收消息的操作
 * 
 * 
 * @author Mark Fisher
 * @author Rossen Stoyanchev
 * @since 4.0
 * @see DestinationResolver
 */
public interface DestinationResolvingMessageRequestReplyOperations<D> extends MessageRequestReplyOperations<D> {

	/**
	 * Resolve the given destination name to a destination and send the given message,
	 * receive a reply and return it.
	 * <p>
	 * 将给定的目的地名称解析为目的地并发送给定消息,接收回复并返回
	 * 
	 * 
	 * @param destinationName the name of the target destination
	 * @param requestMessage the mesage to send
	 * @return the received message, possibly {@code null} if the message could not
	 * be received, for example due to a timeout
	 */
	Message<?> sendAndReceive(String destinationName, Message<?> requestMessage) throws MessagingException;

	/**
	 * Resolve the given destination name, convert the payload request Object
	 * to serialized form, possibly using a
	 * {@link org.springframework.messaging.converter.MessageConverter},
	 * wrap it as a message and send it to the resolved destination, receive a reply
	 * and convert its body to the specified target class.
	 * <p>
	 *  解决给定的目的地名称,将有效载荷请求对象转换为序列化形式,可能使用{@link orgspringframeworkmessagingconverterMessageConverter}将其作为消息包
	 * 装,并将其发送到已解析的目标位置,接收回复并将其身体转换为指定的目标类。
	 * 
	 * 
	 * @param destinationName the name of the target destination
	 * @param request the payload for the request message to send
	 * @param targetClass the target class to convert the payload of the reply to
	 * @return the converted payload of the reply message, possibly {@code null} if
	 * the message could not be received, for example due to a timeout
	 */
	<T> T convertSendAndReceive(String destinationName, Object request, Class<T> targetClass)
			throws MessagingException;

	/**
	 * Resolve the given destination name, convert the payload request Object
	 * to serialized form, possibly using a
	 * {@link org.springframework.messaging.converter.MessageConverter},
	 * wrap it as a message with the given headers and send it to the resolved destination,
	 * receive a reply and convert its body to the specified target class.
	 * <p>
	 *  解决给定的目的地名称,将有效载荷请求对象转换为序列化形式,可能使用{@link orgspringframeworkmessagingconverterMessageConverter}将其作为包含给
	 * 定标题的消息进行包装,并将其发送到已解析的目标位置,接收回复并将其身体转换为指定目标类。
	 * 
	 * 
	 * @param destinationName the name of the target destination
	 * @param request the payload for the request message to send
	 * @param headers the headers for the request message to send
	 * @param targetClass the target class to convert the payload of the reply to
	 * @return the converted payload of the reply message, possibly {@code null} if
	 * the message could not be received, for example due to a timeout
	 */
	<T> T convertSendAndReceive(String destinationName, Object request, Map<String, Object> headers,
			Class<T> targetClass) throws MessagingException;

	/**
	 * Resolve the given destination name, convert the payload request Object
	 * to serialized form, possibly using a
	 * {@link org.springframework.messaging.converter.MessageConverter},
	 * wrap it as a message, apply the given post process, and send the resulting
	 * message to the resolved destination, then receive a reply and convert its
	 * body to the specified target class.
	 * <p>
	 * 解决给定的目的地名称,将有效载荷请求对象转换为序列化形式,可能使用{@link orgspringframeworkmessagingconverterMessageConverter}将其包装为消息,
	 * 应用给定的后处理,并将结果消息发送到已解析的目标,然后接收回复并将其身体转换为指定的目标类。
	 * 
	 * 
	 * @param destinationName the name of the target destination
	 * @param request the payload for the request message to send
	 * @param targetClass the target class to convert the payload of the reply to
	 * @param requestPostProcessor post process for the request message
	 * @return the converted payload of the reply message, possibly {@code null} if
	 * the message could not be received, for example due to a timeout
	 */
	<T> T convertSendAndReceive(String destinationName, Object request,
			Class<T> targetClass, MessagePostProcessor requestPostProcessor) throws MessagingException;

	/**
	 * Resolve the given destination name, convert the payload request Object
	 * to serialized form, possibly using a
	 * {@link org.springframework.messaging.converter.MessageConverter},
	 * wrap it as a message with the given headers, apply the given post process,
	 * and send the resulting message to the resolved destination, then receive
	 * a reply and convert its body to the specified target class.
	 * <p>
	 *  解决给定的目的地名称,将有效载荷请求对象转换为序列化形式,可能使用{@link orgspringframeworkmessagingconverterMessageConverter}将其作为包含给
	 * 定标题的消息进行包装,应用给定的后置进程,并将生成的消息发送到已解析的目标,然后收到回复并将其身体转换为指定的目标类。
	 * 
	 * @param destinationName the name of the target destination
	 * @param request the payload for the request message to send
	 * @param headers the headers for the request message to send
	 * @param targetClass the target class to convert the payload of the reply to
	 * @param requestPostProcessor post process for the request message
	 * @return the converted payload of the reply message, possibly {@code null} if
	 * the message could not be received, for example due to a timeout
	 */
	<T> T convertSendAndReceive(String destinationName, Object request, Map<String, Object> headers,
			Class<T> targetClass, MessagePostProcessor requestPostProcessor) throws MessagingException;

}
