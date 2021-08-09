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

package org.springframework.http.server;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;

import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpStatus;

/**
 * Represents a server-side HTTP response.
 *
 * <p>
 *  表示服务器端HTTP响应
 * 
 * 
 * @author Arjen Poutsma
 * @since 3.0
 */
public interface ServerHttpResponse extends HttpOutputMessage, Flushable, Closeable {

	/**
	 * Set the HTTP status code of the response.
	 * <p>
	 *  设置响应的HTTP状态代码
	 * 
	 * 
	 * @param status the HTTP status as an HttpStatus enum value
	 */
	void setStatusCode(HttpStatus status);

	/**
	 * Ensure that the headers and the content of the response are written out.
	 * <p>After the first flush, headers can no longer be changed.
	 * Only further content writing and content flushing is possible.
	 * <p>
	 * 确保标题和响应的内容被写出<p>第一次刷新后,标题不能再被更改只有进一步的内容写入和内容可以被刷新
	 * 
	 */
	@Override
	void flush() throws IOException;

	/**
	 * Close this response, freeing any resources created.
	 * <p>
	 *  关闭此响应,释放所创建的所有资源
	 */
	@Override
	void close();

}
