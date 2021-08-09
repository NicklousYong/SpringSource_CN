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

package org.springframework.messaging.simp;

import java.util.Map;

import org.springframework.messaging.MessagingException;
import org.springframework.messaging.core.MessagePostProcessor;
import org.springframework.messaging.core.MessageSendingOperations;

/**
 * A specialization of {@link MessageSendingOperations} with methods for use with
 * the Spring Framework support for Simple Messaging Protocols (like STOMP).
 *
 * <p>For more on user destinations see
 * {@link org.springframework.messaging.simp.user.UserDestinationResolver
 * UserDestinationResolver}.
 *
 * <p>Generally it is expected the user is the one authenticated with the
 * WebSocket session (or by extension the user authenticated with the
 * handshake request that started the session). However if the session is
 * not authenticated, it is also possible to pass the session id (if known)
 * in place of the user name. Keep in mind though that in that scenario,
 * you must use one of the overloaded methods that accept headers making sure the
 * {@link org.springframework.messaging.simp.SimpMessageHeaderAccessor#setSessionId
 * sessionId} header has been set accordingly.
 *
 * <p>
 *  使用Spring Framework支持简单消息传递协议(如STOMP)的方法专门化{@link MessageSendingOperations}
 * 
 * <p>有关用户目标的更多信息,请参阅{@link orgspringframeworkmessagingsimpuserUserDestinationResolver UserDestinationResolver}
 * 。
 * 
 *  通常预期用户是通过WebSocket会话进行身份验证的用户(或通过用户通过启动会话的握手请求进行身份验证的用户),但是如果会话未通过身份验证,也可以传递会话ID (如果已知)代替用户名请记住,在这种情
 * 况下,您必须使用接受标头的重载方法之一,确保{@link orgspringframeworkmessagingsimpSimpMessageHeaderAccessor#setSessionId sessionId}
 * 标头已相应设置。
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public interface SimpMessageSendingOperations extends MessageSendingOperations<String> {

	/**
	 * Send a message to the given user.
	 *
	 * <p>
	 *  向给定用户发送消息
	 * 
	 * 
	 * @param user the user that should receive the message.
	 * @param destination the destination to send the message to.
	 * @param payload the payload to send
	 */
	void convertAndSendToUser(String user, String destination, Object payload) throws MessagingException;

	/**
	 * Send a message to the given user.
	 *
	 * <p>By default headers are interpreted as native headers (e.g. STOMP) and
	 * are saved under a special key in the resulting Spring
	 * {@link org.springframework.messaging.Message Message}. In effect when the
	 * message leaves the application, the provided headers are included with it
	 * and delivered to the destination (e.g. the STOMP client or broker).
	 *
	 * <p>If the map already contains the key
	 * {@link org.springframework.messaging.support.NativeMessageHeaderAccessor#NATIVE_HEADERS "nativeHeaders"}
	 * or was prepared with
	 * {@link org.springframework.messaging.simp.SimpMessageHeaderAccessor SimpMessageHeaderAccessor}
	 * then the headers are used directly. A common expected case is providing a
	 * content type (to influence the message conversion) and native headers.
	 * This may be done as follows:
	 *
	 * <pre class="code">
	 * SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create();
	 * accessor.setContentType(MimeTypeUtils.TEXT_PLAIN);
	 * accessor.setNativeHeader("foo", "bar");
	 * accessor.setLeaveMutable(true);
	 * MessageHeaders headers = accessor.getMessageHeaders();
	 *
	 * messagingTemplate.convertAndSendToUser(user, destination, payload, headers);
	 * </pre>
	 *
	 * <p><strong>Note:</strong> if the {@code MessageHeaders} are mutable as in
	 * the above example, implementations of this interface should take notice and
	 * update the headers in the same instance (rather than copy or re-create it)
	 * and then set it immutable before sending the final message.
	 *
	 * <p>
	 *  向给定用户发送消息
	 * 
	 * <p>默认情况下,标头被解释为本机头(例如STOMP),并保存在生成​​的Spring {@link orgspringframeworkmessagingMessage Message}中的特殊键下。
	 * 当消息离开应用程序时,提供的标头随附并提供到目的地(例如STOMP客户端或代理)。
	 * 
	 *  <p>如果地图已经包含密钥{@link orgspringframeworkmessagingsupportNativeMessageHeaderAccessor#NATIVE_HEADERS"nativeHeaders"}
	 * ,或者使用{@link orgspringframeworkmessagingsimpSimpMessageHeaderAccessor SimpMessageHeaderAccessor}准备好,那么
	 * 这些标题是直接使用的常见的情况是提供内容类型(影响消息转换)和本机头可以这样做：。
	 * 
	 * <pre class="code">
	 * SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessorcreate(); accessorsetContentType(MimeTy
	 * peUtilsTEXT_PLAIN); accessorsetNativeHeader("foo","bar"); accessorsetLeaveMutable(真); MessageHeaders 
	 * headers = accessorgetMessageHeaders();。
	 * 
	 * 
	 * @param user the user that should receive the message, must not be {@code null}
	 * @param destination the destination to send the message to, must not be {@code null}
	 * @param payload the payload to send, may be {@code null}
	 * @param headers the message headers, may be {@code null}
	 */
	void convertAndSendToUser(String user, String destination, Object payload, Map<String, Object> headers)
			throws MessagingException;

	/**
	 * Send a message to the given user.
	 *
	 * <p>
	 *  messagingTemplateconvertAndSendToUser(用户,目的地,有效载荷,标题);
	 * </pre>
	 * 
	 *  <p> <strong>注意：</strong>如果{@code MessageHeaders}在上面的示例中是可变的,则该接口的实现应该注意并更新同一个实例中的头(而不是复制或重新创建)它),然后在
	 * 发送最终消息之前将其设置为不可变。
	 * 
	 * 
	 * @param user the user that should receive the message, must not be {@code null}
	 * @param destination the destination to send the message to, must not be {@code null}
	 * @param payload the payload to send, may be {@code null}
	 * @param postProcessor a postProcessor to post-process or modify the created message
	 */
	void convertAndSendToUser(String user, String destination, Object payload,
			MessagePostProcessor postProcessor) throws MessagingException;

	/**
	 * Send a message to the given user.
	 *
	 * <p>See {@link #convertAndSend(Object, Object, java.util.Map)} for important
	 * notes regarding the input headers.
	 *
	 * <p>
	 *  向给定用户发送消息
	 * 
	 * 
	 * @param user the user that should receive the message.
	 * @param destination the destination to send the message to.
	 * @param payload the payload to send
	 * @param headers the message headers
	 * @param postProcessor a postProcessor to post-process or modify the created message
	 */
	void convertAndSendToUser(String user, String destination, Object payload, Map<String, Object> headers,
			MessagePostProcessor postProcessor) throws MessagingException;

}
