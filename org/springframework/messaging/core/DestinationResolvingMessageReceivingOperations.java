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

import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;

/**
 * Extends {@link MessageReceivingOperations} and adds operations for receiving messages
 * from a destination specified as a (resolvable) String name.
 *
 * <p>
 *  扩展{@link MessageReceivingOperations}并添加从指定为(可解析的)字符串名称的目标接收邮件的操作
 * 
 * 
 * @author Mark Fisher
 * @author Rossen Stoyanchev
 * @since 4.0
 * @see DestinationResolver
 */
public interface DestinationResolvingMessageReceivingOperations<D> extends MessageReceivingOperations<D> {

	/**
	 * Resolve the given destination name and receive a message from it.
	 * <p>
	 *  解决给定的目的地名称并从中接收消息
	 * 
	 * 
	 * @param destinationName the destination name to resolve
	 */
	Message<?> receive(String destinationName) throws MessagingException;

	/**
	 * Resolve the given destination name, receive a message from it, convert the
	 * payload to the specified target type.
	 * <p>
	 * 解决给定的目的地名称,从中接收消息,将有效载荷转换为指定的目标类型
	 * 
	 * @param destinationName the destination name to resolve
	 * @param targetClass the target class for the converted payload
	 */
	<T> T receiveAndConvert(String destinationName, Class<T> targetClass) throws MessagingException;

}
