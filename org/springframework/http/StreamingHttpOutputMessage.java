/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2015 the original author or authors.
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

package org.springframework.http;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Represents a HTTP output message that allows for setting a streaming body.
 * Note that such messages typically do not support {@link #getBody()} access.
 *
 * <p>
 *  表示允许设置流主体的HTTP输出消息注意,这样的消息通常不支持{@link #getBody()}访问
 * 
 * 
 * @author Arjen Poutsma
 * @since 4.0
 * @see #setBody
 */
public interface StreamingHttpOutputMessage extends HttpOutputMessage {

	/**
	 * Set the streaming body for this message.
	 * <p>
	 *  为此消息设置流媒体
	 * 
	 * 
	 * @param body the streaming body
	 */
	void setBody(Body body);


	/**
	 * Defines the contract for bodies that can be written directly to an {@link OutputStream}.
	 * It is useful with HTTP client libraries that provide indirect access to an
	 * {@link OutputStream} via a callback mechanism.
	 * <p>
	 * 定义可以直接写入{@link OutputStream}的正文的合同对于通过回调机制间接访问{@link OutputStream}的HTTP客户端库非常有用
	 * 
	 */
	interface Body {

		/**
		 * Write this body to the given {@link OutputStream}.
		 * <p>
		 *  将此正文写入给定的{@link OutputStream}
		 * 
		 * @param outputStream the output stream to write to
		 * @throws IOException in case of errors
		 */
		void writeTo(OutputStream outputStream) throws IOException;
	}

}
