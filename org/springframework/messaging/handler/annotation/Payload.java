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

import org.springframework.messaging.converter.MessageConverter;

/**
 * Annotation that binds a method parameter to the payload of a message. Can also
 * be used to associate a payload to a method invocation. The payload may be passed
 * through a {@link MessageConverter} to convert it from serialized form with a
 * specific MIME type to an Object matching the target method parameter.
 *
 * <p>
 * 将方法参数绑定到消息的有效负载的注释也可以用于将有效负载与方法调用相关联可以通过{@link MessageConverter}传递有效负载,以将其从具有特定MIME类型的序列化形式转换为对象匹配目标方
 * 法的参数。
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 4.0
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Payload {

	/**
	 * A SpEL expression to be evaluated against the payload object as the root context.
	 * This attribute may or may not be supported depending on whether the message being
	 * handled contains a non-primitive Object as its payload or is in serialized form
	 * and requires message conversion.
	 * <p>When processing STOMP over WebSocket messages this attribute is not supported.
	 * <p>
	 *  要根据有效载荷对象进行评估的Spel表达式作为根上下文根据正在处理的消息是否包含非原始对象作为其有效负载或以序列化形式并且需要消息转换,可以支持或不支持该属性<p>当通过WebSocket消息处理ST
	 * OMP时,不支持此属性。
	 * 
	 */
	String value() default "";

	/**
	 * Whether payload content is required.
	 * <p>Default is {@code true}, leading to an exception if there is no payload. Switch
	 * to {@code false} to have {@code null} passed when there is no payload.
	 * <p>
	 */
	boolean required() default true;

}
