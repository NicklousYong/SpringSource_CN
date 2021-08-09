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

package org.springframework.messaging.converter;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.util.MimeTypeUtils;

/**
 * A {@link MessageConverter} that supports MIME type "application/octet-stream" with the
 * payload converted to and from a byte[].
 *
 * <p>
 *  除非适用法律要求或以书面形式同意,根据许可证分发的软件以"按原样"分发,不附带任何明示或暗示的担保或条件,请参阅许可证中有关特定语言的权限和限制许可证
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public class ByteArrayMessageConverter extends AbstractMessageConverter {


	public ByteArrayMessageConverter() {
		super(MimeTypeUtils.APPLICATION_OCTET_STREAM);
	}


	@Override
	protected boolean supports(Class<?> clazz) {
		return byte[].class.equals(clazz);
	}

	@Override
	public Object convertFromInternal(Message<?> message, Class<?> targetClass) {
		return message.getPayload();
	}

	@Override
	public Object convertToInternal(Object payload, MessageHeaders headers) {
		return payload;
	}

}
