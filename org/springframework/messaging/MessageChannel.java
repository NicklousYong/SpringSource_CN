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

package org.springframework.messaging;

/**
 * Defines methods for sending messages.
 *
 * <p>
 *  定义发送消息的方法
 * 
 * 
 * @author Mark Fisher
 * @since 4.0
 */
public interface MessageChannel {

	/**
	 * Constant for sending a message without a prescribed timeout.
	 * <p>
	 *  用于发送没有规定超时的消息的常数
	 * 
	 */
	public static final long INDEFINITE_TIMEOUT = -1;


	/**
	 * Send a {@link Message} to this channel. If the message is sent successfully,
	 * the method returns {@code true}. If the message cannot be sent due to a
	 * non-fatal reason, the method returns {@code false}. The method may also
	 * throw a RuntimeException in case of non-recoverable errors.
	 * <p>This method may block indefinitely, depending on the implementation.
	 * To provide a maximum wait time, use {@link #send(Message, long)}.
	 * <p>
	 * 发送{@link Message}到这个频道如果消息成功发送,该方法返回{@code true}如果由于非致命原因而无法发送消息,该方法返回{@code false}该方法可能在不可恢复的错误的情况下也
	 * 会抛出RuntimeException <p>此方法可能无限期地阻止,具体取决于实现要提供最大等待时间,请使用{@link #send(Message,long)}。
	 * 
	 * 
	 * @param message the message to send
	 * @return whether or not the message was sent
	 */
	boolean send(Message<?> message);

	/**
	 * Send a message, blocking until either the message is accepted or the
	 * specified timeout period elapses.
	 * <p>
	 * 
	 * @param message the message to send
	 * @param timeout the timeout in milliseconds or {@link #INDEFINITE_TIMEOUT}
	 * @return {@code true} if the message is sent, {@code false} if not
	 * including a timeout of an interrupt of the send
	 */
	boolean send(Message<?> message, long timeout);

}
