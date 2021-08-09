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

package org.springframework.messaging.simp.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for mapping subscription messages onto specific handler methods based
 * on the destination of a subscription. Supported with STOMP over WebSocket only
 * (e.g. STOMP SUBSCRIBE frame).
 *
 * <p>This is a method-level annotations that can be combined with a type-level
 * {@link org.springframework.messaging.handler.annotation.MessageMapping @MessageMapping}
 *
 * <p>Supports the same method arguments as
 * {@link org.springframework.messaging.handler.annotation.MessageMapping}, however
 * subscription messages typically do not have a body.
 *
 * <p>The return value also follows the same rules as for
 * {@link org.springframework.messaging.handler.annotation.MessageMapping} except if
 * the method is not annotated with
 * {@link org.springframework.messaging.handler.annotation.SendTo} or {@link SendToUser},
 * the message is sent directly back to the connected user and does not pass through
 * the message broker. This is useful for implementing a request-reply pattern.
 *
 * <p>
 *  基于订阅目的地将订阅消息映射到特定处理程序方法的注释仅通过WebSocket支持STOMP(例如STOMP SUBSCRIBE框架)
 * 
 * <p>这是一种方法级注释,可以与类型级别{@link orgspringframeworkmessaginghandlerannotationMessageMapping @MessageMapping}
 * 结合使用。
 * 
 *  <p>支持与{@link orgspringframeworkmessaginghandlerannotationMessageMapping}相同的方法参数,但订阅消息通常不具有正文
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 4.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SubscribeMapping {

	/**
	 * Destination-based mapping expressed by this annotation.
	 * <p>For STOMP over WebSocket messages: this is the destination of the STOMP message
	 * (e.g. "/positions"). Ant-style path patterns (e.g. "/price.stock.*") are supported
	 * and so are path template variables (e.g. "/price.stock.{ticker}"").
	 * <p>
	 *  <p>返回值还遵循与{@link orgspringframeworkmessaginghandlerannotationMessageMapping}相同的规则,除非该方法未使用{@link orgspringframeworkmessaginghandlerannotationSendTo}
	 * 或{@link SendToUser}进行注释,否则将直接将消息发送回连接的用户,并执行不通过消息代理这对于实现请求回复模式很有用。
	 * 
	 */
	String[] value() default {};

}
