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

import org.springframework.messaging.MessageHeaders;
import org.springframework.util.MimeType;

/**
 * Resolve the content type for a message.
 *
 * <p>
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public interface ContentTypeResolver {

	/**
	 * Determine the {@link MimeType} of a message from the given MessageHeaders.
	 *
	 * <p>
	 *  解决消息的内容类型
	 * 
	 * 
	 * @param headers the headers to use for the resolution
	 * @return the resolved {@code MimeType} of {@code null} if none found
	 *
	 * @throws org.springframework.util.InvalidMimeTypeException if the content type
	 * 	is a String that cannot be parsed
	 * @throws java.lang.IllegalArgumentException if there is a content type but
	 * 	its type is unknown
	 */
	MimeType resolve(MessageHeaders headers);

}
