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

package org.springframework.messaging;

/**
 * A generic message representation with headers and body.
 *
 * <p>
 *  具有标题和正文的通用消息表示
 * 
 * 
 * @author Mark Fisher
 * @author Arjen Poutsma
 * @since 4.0
 * @see org.springframework.messaging.support.MessageBuilder
 */
public interface Message<T> {

	/**
	 * Return the message payload.
	 * <p>
	 *  返回消息有效载荷
	 * 
	 */
	T getPayload();

	/**
	 * Return message headers for the message (never {@code null} but may be empty).
	 * <p>
	 *  返回邮件的邮件标题(从不{@code null}但可能为空)
	 */
	MessageHeaders getHeaders();

}
