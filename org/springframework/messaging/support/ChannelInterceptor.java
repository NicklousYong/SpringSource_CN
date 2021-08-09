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
import org.springframework.messaging.MessageChannel;

/**
 * Interface for interceptors that are able to view and/or modify the
 * {@link Message Messages} being sent-to and/or received-from a
 * {@link MessageChannel}.
 *
 * <p>
 *  能够查看和/或修改从{@link MessageChannel}发送和/或接收的{@link消息消息}的拦截器的接口,
 * 
 * 
 * @author Mark Fisher
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public interface ChannelInterceptor {

	/**
	 * Invoked before the Message is actually sent to the channel.
	 * This allows for modification of the Message if necessary.
	 * If this method returns {@code null} then the actual
	 * send invocation will not occur.
	 * <p>
	 * 在消息实际发送到通道之前调用这允许修改消息(如果需要)如果此方法返回{@code null},那么实际的发送调用将不会发生
	 * 
	 */
	Message<?> preSend(Message<?> message, MessageChannel channel);

	/**
	 * Invoked immediately after the send invocation. The boolean
	 * value argument represents the return value of that invocation.
	 * <p>
	 *  发送调用后立即调用布尔值参数表示该调用的返回值
	 * 
	 */
	void postSend(Message<?> message, MessageChannel channel, boolean sent);

	/**
	 * Invoked after the completion of a send regardless of any exception that
	 * have been raised thus allowing for proper resource cleanup.
	 * <p>Note that this will be invoked only if {@link #preSend} successfully
	 * completed and returned a Message, i.e. it did not return {@code null}.
	 * <p>
	 *  在完成发送后调用,无论任何异常已被提升,从而允许正确的资源清除<p>请注意,只有在{@link #preSend}成功完成并返回消息后才会调用此操作,即它没有返回{@code null}
	 * 
	 * 
	 * @since 4.1
	 */
	void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex);

	/**
	 * Invoked as soon as receive is called and before a Message is
	 * actually retrieved. If the return value is 'false', then no
	 * Message will be retrieved. This only applies to PollableChannels.
	 * <p>
	 *  一旦接收被调用并且在实际检索到一个消息之前调用如果返回值为"false",则不会检索到消息这仅适用于PollableChannels
	 * 
	 */
	boolean preReceive(MessageChannel channel);

	/**
	 * Invoked immediately after a Message has been retrieved but before
	 * it is returned to the caller. The Message may be modified if
	 * necessary. This only applies to PollableChannels.
	 * <p>
	 * 在邮件被检索之后,但在它被返回给调用者之前立即调用该消息可能被修改如果需要这仅适用于PollableChannels
	 * 
	 */
	Message<?> postReceive(Message<?> message, MessageChannel channel);

	/**
	 * Invoked after the completion of a receive regardless of any exception that
	 * have been raised thus allowing for proper resource cleanup.
	 * <p>Note that this will be invoked only if {@link #preReceive} successfully
	 * completed and returned {@code true}.
	 * <p>
	 *  在完成接收后调用,无论任何异常已被提升,从而允许正确的资源清理<p>请注意,只有{@link #preReceive}成功完成并返回{@code true}
	 * 
	 * @since 4.1
	 */
	void afterReceiveCompletion(Message<?> message, MessageChannel channel, Exception ex);

}
