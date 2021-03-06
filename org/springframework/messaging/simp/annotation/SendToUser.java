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

package org.springframework.messaging.simp.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that indicates the return value of a message-handling method should
 * be sent as a {@link org.springframework.messaging.Message} to the specified
 * destination(s) prepended with {@code "/user/{username}"} where the user
 * name is extracted from the headers of the input message being handled.
 *
 * <p>
 * 指示消息处理方法的返回值的注释应作为{@link orgspringframeworkmessagingMessage}发送到前面提到的{@code"/ user / {username}"}的指定目的
 * 地,其中提取用户名正在处理输入消息的标题。
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 4.0
 * @see org.springframework.messaging.simp.annotation.support.SendToMethodReturnValueHandler
 * @see org.springframework.messaging.simp.user.UserDestinationMessageHandler
 * @see org.springframework.messaging.simp.SimpMessageHeaderAccessor#getUser()
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SendToUser {

	/**
	 * One or more destinations to send a message to. If left unspecified, a
	 * default destination is selected based on the destination of the input
	 * message being handled.
	 * <p>
	 *  发送消息的一个或多个目的地如果未指定,则基于正在处理的输入消息的目的地选择默认目的地
	 * 
	 * 
	 * @see org.springframework.messaging.simp.annotation.support.SendToMethodReturnValueHandler
	 */
	String[] value() default {};

	/**
	 * Whether messages should be sent to all sessions associated with the user
	 * or only to the session of the input message being handled.
	 *
	 * <p>By default this is set to {@code true} in which case messages are
	 * broadcast to all sessions.
	 * <p>
	 *  是否应将消息发送到与用户相关联的所有会话,或仅发送到正在处理的输入消息的会话
	 * 
     */
    boolean broadcast() default true;

}
