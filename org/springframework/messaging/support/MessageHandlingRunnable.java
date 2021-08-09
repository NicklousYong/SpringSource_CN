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

package org.springframework.messaging.support;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;

/**
 * Extension of the {@link Runnable} interface with methods to obtain the
 * {@link MessageHandler} and {@link Message} to be handled.
 *
 * <p>
 *  使用方法扩展{@link Runnable}接口,以获取要处理的{@link MessageHandler}和{@link Message}
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 4.1.1
 */
public interface MessageHandlingRunnable extends Runnable {

	/**
	 * Return the Message that will be handled.
	 * <p>
	 *  返回将被处理的消息
	 * 
	 */
	Message<?> getMessage();

	/**
	 * Return the MessageHandler that will be used to handle the message.
	 * <p>
	 * 返回将用于处理消息的MessageHandler
	 */
	MessageHandler getMessageHandler();

}
