/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2012 the original author or authors.
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

package org.springframework.core.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * Simple interface for objects that are sources for an {@link InputStream}.
 *
 * <p>This is the base interface for Spring's more extensive {@link Resource} interface.
 *
 * <p>For single-use streams, {@link InputStreamResource} can be used for any
 * given {@code InputStream}. Spring's {@link ByteArrayResource} or any
 * file-based {@code Resource} implementation can be used as a concrete
 * instance, allowing one to read the underlying content stream multiple times.
 * This makes this interface useful as an abstract content source for mail
 * attachments, for example.
 *
 * <p>
 *  用于{@link InputStream}的源的对象的简单界面
 * 
 *  <p>这是Spring更广泛的{@link Resource}界面的基础界面
 * 
 * <p>对于一次性流,{@link InputStreamResource}可用于任何给定的{@code InputStream} Spring的{@link ByteArrayResource},或者任
 * 何基于文件的{@code Resource}实现都可以用作具体实例,允许人们多次读取底层内容流这使得该接口作为例如邮件附件的抽象内容源很有用。
 * 
 * @author Juergen Hoeller
 * @since 20.01.2004
 * @see java.io.InputStream
 * @see Resource
 * @see InputStreamResource
 * @see ByteArrayResource
 */
public interface InputStreamSource {

	/**
	 * Return an {@link InputStream}.
	 * <p>It is expected that each call creates a <i>fresh</i> stream.
	 * <p>This requirement is particularly important when you consider an API such
	 * as JavaMail, which needs to be able to read the stream multiple times when
	 * creating mail attachments. For such a use case, it is <i>required</i>
	 * that each {@code getInputStream()} call returns a fresh stream.
	 * <p>
	 * 
	 * 
	 * @return the input stream for the underlying resource (must not be {@code null})
	 * @throws IOException if the stream could not be opened
	 * @see org.springframework.mail.javamail.MimeMessageHelper#addAttachment(String, InputStreamSource)
	 */
	InputStream getInputStream() throws IOException;

}
