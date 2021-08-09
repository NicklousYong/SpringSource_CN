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

package org.springframework.messaging.tcp;

import org.springframework.messaging.Message;

/**
 * A contract for managing lifecycle events for a TCP connection including
 * the handling of incoming messages.
 *
 * <p>
 *  用于管理TCP连接的生命周期事件的合同,包括处理传入消息
 * 
 * 
 * @param <P> the type of payload for in and outbound messages
 *
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public interface TcpConnectionHandler<P> {

	/**
	 * Invoked after a connection is successfully established.
	 * <p>
	 *  连接成功建立后调用
	 * 
	 * 
	 * @param connection the connection
	 */
	void afterConnected(TcpConnection<P> connection);

	/**
	 * Invoked on failure to connect.
	 * <p>
	 *  无法连接时调用
	 * 
	 * 
	 * @param ex the exception
	 */
	void afterConnectFailure(Throwable ex);

	/**
	 * Handle a message received from the remote host.
	 * <p>
	 * 处理从远程主机收到的消息
	 * 
	 * 
	 * @param message the message
	 */
	void handleMessage(Message<P> message);

	/**
	 * Handle a failure on the connection.
	 * <p>
	 *  处理连接失败
	 * 
	 * 
	 * @param ex the exception
	 */
	void handleFailure(Throwable ex);

	/**
	 * Invoked after the connection is closed.
	 * <p>
	 *  连接关闭后调用
	 */
	void afterConnectionClosed();

}
