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
 * A {@link MessageChannel} from which messages may be actively received through polling.
 *
 * <p>
 *  可以通过轮询从中积极地接收消息的{@link MessageChannel}
 * 
 * 
 * @author Mark Fisher
 * @since 4.0
 */
public interface PollableChannel extends MessageChannel {

	/**
	 * Receive a message from this channel, blocking indefinitely if necessary.
	 * <p>
	 *  接收来自此频道的消息,必要时无限制地阻止
	 * 
	 * 
	 * @return the next available {@link Message} or {@code null} if interrupted
	 */
	Message<?> receive();

	/**
	 * Receive a message from this channel, blocking until either a message is available
	 * or the specified timeout period elapses.
	 * <p>
	 * 从该频道接收消息,阻塞直到消息可用或指定的超时时间段过去
	 * 
	 * @param timeout the timeout in milliseconds or {@link MessageChannel#INDEFINITE_TIMEOUT}.
	 * @return the next available {@link Message} or {@code null} if the specified timeout
	 * period elapses or the message reception is interrupted
	 */
	Message<?> receive(long timeout);

}
