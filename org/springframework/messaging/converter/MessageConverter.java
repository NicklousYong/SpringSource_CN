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

package org.springframework.messaging.converter;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

/**
 * A converter to turn the payload of a {@link Message} from serialized form to a typed
 * Object and vice versa. The {@link MessageHeaders#CONTENT_TYPE} message header may be
 * used to specify the media type of the message content.
 *
 * <p>
 *  将{@link Message}的有效载荷从序列化形式转换为类型化对象的转换器,反之亦然。{@link MessageHeaders#CONTENT_TYPE}消息头可用于指定消息内容的媒体类型
 * 
 * 
 * @author Mark Fisher
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public interface MessageConverter {

	/**
	 * Convert the payload of a {@link Message} from serialized form to a typed Object of
	 * the specified target class. The {@link MessageHeaders#CONTENT_TYPE} header should
	 * indicate the MIME type to convert from.
	 * <p>If the converter does not support the specified media type or cannot perform the
	 * conversion, it should return {@code null}.
	 * <p>
	 * 将{@link Message}的有效载荷从序列化形式转换为指定目标类的类型化对象{@link MessageHeaders#CONTENT_TYPE}头应指示要从<p>转换的MIME类型如果转换器不支
	 * 持指定媒体类型或无法执行转换,应返回{@code null}。
	 * 
	 * 
	 * @param message the input message
	 * @param targetClass the target class for the conversion
	 * @return the result of the conversion, or {@code null} if the converter cannot
	 * perform the conversion
	 */
	Object fromMessage(Message<?> message, Class<?> targetClass);

	/**
	 * Create a {@link Message} whose payload is the result of converting the given
	 * payload Object to serialized form. The optional {@link MessageHeaders} parameter
	 * may contain a {@link MessageHeaders#CONTENT_TYPE} header to specify the target
	 * media type for the conversion and it may contain additional headers to be added to
	 * the message.
	 * <p>If the converter does not support the specified media type or cannot perform the
	 * conversion, it should return {@code null}.
	 * <p>
	 *  创建一个{@link Message},其有效载荷是将给定的有效载荷对象转换为序列化形式。
	 * 可选的{@link MessageHeaders}参数可能包含一个{@link MessageHeaders#CONTENT_TYPE}标题,用于指定转换的目标媒体类型,它可能包含要添加到消息中的附加标
	 * 题<p>如果转换器不支持指定的媒体类型或无法执行转换,则应返回{@code null}。
	 * 
	 * @param payload the Object to convert
	 * @param header optional headers for the message, may be {@code null}
	 * @return the new message or {@code null} if the converter does not support the
	 * Object type or the target media type
	 */
	Message<?> toMessage(Object payload, MessageHeaders header);

}
