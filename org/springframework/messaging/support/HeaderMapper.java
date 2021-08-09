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

import org.springframework.messaging.MessageHeaders;

/**
 * Generic strategy interface for mapping {@link MessageHeaders} to and from other
 * types of objects. This would typically be used by adapters where the "other type"
 * has a concept of headers or properties (HTTP, JMS, AMQP, etc).
 *
 * <p>
 *  用于将{@link MessageHeaders}映射到其他类型对象的通用策略接口通常由"其他类型"具有头或属性(HTTP,JMS,AMQP等)概念的适配器使用,
 * 
 * 
 * @author Mark Fisher
 * @since 4.1
 * @param <T> type of the instance to and from which headers will be mapped
 */
public interface HeaderMapper<T> {

	/**
	 * Map from the given {@link MessageHeaders} to the specified target message.
	 * <p>
	 * 从给定的{@link MessageHeaders}映射到指定的目标消息
	 * 
	 * 
	 * @param headers the abstracted MessageHeaders
	 * @param target the native target message
	 */
	void fromHeaders(MessageHeaders headers, T target);

	/**
	 * Map from the given target message to abstracted {@link MessageHeaders}.
	 * <p>
	 *  从给定的目标消息映射到抽象的{@link MessageHeaders}
	 * 
	 * @param source the native target message
	 * @return the abstracted MessageHeaders
	 */
	MessageHeaders toHeaders(T source);

}
