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

package org.springframework.messaging.handler.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.messaging.Message;

/**
 * Annotation for mapping a {@link Message} onto message-handling methods by matching
 * to the message destination. This annotation can also be used on the type-level in
 * which case it defines a common destination prefix or pattern for all method-level
 * annotations including method-level
 * {@link org.springframework.messaging.simp.annotation.SubscribeMapping @SubscribeMapping}
 * annotations.
 *
 * <p>Handler methods which are annotated with this annotation are allowed to have
 * flexible signatures. They may have arguments of the following types, in arbitrary
 * order:
 * <ul>
 * <li>{@link Message} to get access to the complete message being processed.</li>
 * <li>{@link Payload}-annotated method arguments to extract the payload of
 * a message and optionally convert it using a
 * {@link org.springframework.messaging.converter.MessageConverter}.
 * The presence of the annotation is not required since it is assumed by default
 * for method arguments that are not annotated. Payload method arguments annotated
 * with Validation annotations (like
 * {@link org.springframework.validation.annotation.Validated}) will be subject to
 * JSR-303 validation.</li>
 * <li>{@link Header}-annotated method arguments to extract a specific
 * header value along with type conversion with a
 * {@link org.springframework.core.convert.converter.Converter} if necessary.</li>
 * <li>{@link Headers}-annotated argument that must also be assignable to
 * {@link java.util.Map} for getting access to all headers.</li>
 * <li>{@link org.springframework.messaging.MessageHeaders} arguments for
 * getting access to all headers.</li>
 * <li>{@link org.springframework.messaging.support.MessageHeaderAccessor} or
 * with STOMP over WebSocket support also sub-classes such as
 * {@link org.springframework.messaging.simp.SimpMessageHeaderAccessor}
 * for convenient access to all method arguments.</li>
 * <li>{@link DestinationVariable}-annotated arguments for access to template
 * variable values extracted from the message destination (e.g. /hotels/{hotel}).
 * Variable values will be converted to the declared method argument type.</li>
 * <li>{@link java.security.Principal} method arguments are supported with
 * STOMP over WebSocket messages. It reflects the user logged in to the
 * WebSocket session on which the message was received. Regular HTTP-based
 * authentication (e.g. Spring Security based) can be used to secure the
 * HTTP handshake that initiates WebSocket sessions.</li>
 * </ul>
 * <p>By default the return value is wrapped as a message and sent to the destination
 * specified with an {@link SendTo} method-level annotation.
 *
 * <p>STOMP over WebSocket: an {@link SendTo} annotation is not strictly required --
 * by default the message will be sent to the same destination as the incoming
 * message but with an additional prefix ("/topic" by default). It is also possible
 * to use {@link org.springframework.messaging.simp.annotation.SendToUser} to
 * have the message directed to a specific user only if connected.
 * Also the return value is converted with a
 * {@link org.springframework.messaging.converter.MessageConverter}.
 *
 * <p>
 * 通过匹配消息目的地将{@link消息}映射到消息处理方法的注释此注释也可以在类型级别上使用,在这种情况下,它可以为所有方法级注释定义公共目标前缀或模式,包括方法-level {@link orgspringframeworkmessagingsimpannotationSubscribeMapping @SubscribeMapping}
 * 注释。
 * 
 *  使用此注释注释的处理程序方法允许具有灵活的签名他们可以具有以下类型的参数,按任意顺序：
 * <ul>
 * <li> {@ link Message}以访问正在处理的完整邮件</li> <li> {@ link Payload}  - 注释方法参数来提取邮件的有效内容,并可以使用{@link orgspringframeworkmessagingconverterMessageConverter }
 * 注释的存在不是必需的,因为默认情况下,未注释的方法参数假定使用验证注释注释的有效载荷方法参数(如{@link orgspringframeworkvalidationannotationValidated}
 * )将受JSR-303验证</li> < li> {@ link Header}  - 如果需要,通过{@link orgspringframeworkcoreconvertconverterConverter}
 * 提取特定头值以及类型转换</li> <li> {@ link Headers} -annotated参数,必须也可以转让给{@link javautilMap},以获取访问所有标头的信息</li> <li>
 *  {@ link orgspringframeworkmessagingMessageHeaders}标头</li> <li> {@ link orgspringframeworkmessagingsupportMessageHeaderAccessor}
 * 或通过WebSocket支持STOMP还支持诸如{@link orgspringframeworkmessagingsimpSimpMessageHeaderAccessor}的子类,方便访问所有方法参
 * 数</li> <li> {@ link DestinationVariable}  - 用于访问从消息目的地提取的模板变量值的注释参数(例如/ hotels / {hotel})变量值将被转换为声明的方
 * 
 * @author Rossen Stoyanchev
 * @since 4.0
 * @see org.springframework.messaging.simp.annotation.support.SimpAnnotationMethodMessageHandler
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MessageMapping {

	/**
	 * Destination-based mapping expressed by this annotation.
	 * <p>For STOMP over WebSocket messages: this is the destination of the STOMP message
	 * (e.g. "/positions"). Ant-style path patterns (e.g. "/price.stock.*") are supported
	 * and so are path template variables (e.g. "/price.stock.{ticker}"").
	 * <p>
	 * 法参数类型</li> <li> {@ link javasecurityPrincipal}方法参数通过WebSocket消息支持STOMP它反映了登录到接收到消息的WebSocket会话的用户常规基于
	 * HTTP的身份验证(例如基于Spring Security)可用于保护启动WebSocket会话的HTTP握手</LI>。
	 * </ul>
	 * <p>默认情况下,返回值作为消息包装,并发送到使用{@link SendTo}方法级注释指定的目标
	 * 
	 *  <p>在WebSocket上的STOMP：不严格要求{@link SendTo}注释 - 默认情况下,该消息将被发送到与传入消息相同的目的地,但附加前缀(默认为"/ topic"))也可以使用{@link orgspringframeworkmessagingsimpannotationSendToUser}
	 */
	String[] value() default {};

}
