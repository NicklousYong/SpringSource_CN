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

package org.springframework.messaging.simp.stomp;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.messaging.simp.SimpMessageType;

/**
 * Represents a STOMP command.
 *
 * <p>
 *  除非适用法律要求或以书面形式同意,根据许可证分发的软件以"按原样"分发,不附带任何明示或暗示的担保或条件,请参阅许可证中有关特定语言的权限和限制许可证
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public enum StompCommand {

	// client
	CONNECT,
	STOMP,
	DISCONNECT,
	SUBSCRIBE,
	UNSUBSCRIBE,
	SEND,
	ACK,
	NACK,
	BEGIN,
	COMMIT,
	ABORT,

	// server
	CONNECTED,
	MESSAGE,
	RECEIPT,
	ERROR;


	private static Map<StompCommand, SimpMessageType> messageTypes = new HashMap<StompCommand, SimpMessageType>();
	static {
		messageTypes.put(StompCommand.CONNECT, SimpMessageType.CONNECT);
		messageTypes.put(StompCommand.STOMP, SimpMessageType.CONNECT);
		messageTypes.put(StompCommand.SEND, SimpMessageType.MESSAGE);
		messageTypes.put(StompCommand.MESSAGE, SimpMessageType.MESSAGE);
		messageTypes.put(StompCommand.SUBSCRIBE, SimpMessageType.SUBSCRIBE);
		messageTypes.put(StompCommand.UNSUBSCRIBE, SimpMessageType.UNSUBSCRIBE);
		messageTypes.put(StompCommand.DISCONNECT, SimpMessageType.DISCONNECT);
	}

	private static Collection<StompCommand> destinationRequired = Arrays.asList(SEND, SUBSCRIBE, MESSAGE);
	private static Collection<StompCommand> subscriptionIdRequired = Arrays.asList(SUBSCRIBE, UNSUBSCRIBE, MESSAGE);
	private static Collection<StompCommand> contentLengthRequired = Arrays.asList(SEND, MESSAGE, ERROR);
	private static Collection<StompCommand> bodyAllowed = Arrays.asList(SEND, MESSAGE, ERROR);



	public SimpMessageType getMessageType() {
		SimpMessageType type = messageTypes.get(this);
		return (type != null) ? type : SimpMessageType.OTHER;
	}

	public boolean requiresDestination() {
		return destinationRequired.contains(this);
	}

	public boolean requiresSubscriptionId() {
		return subscriptionIdRequired.contains(this);
	}

	public boolean requiresContentLength() {
		return contentLengthRequired.contains(this);
	}

	public boolean isBodyAllowed() {
		return bodyAllowed.contains(this);
	}

}

