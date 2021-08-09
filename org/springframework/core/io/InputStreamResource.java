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

package org.springframework.core.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * {@link Resource} implementation for a given {@link InputStream}.
 * <p>Should only be used if no other specific {@code Resource} implementation
 * is applicable. In particular, prefer {@link ByteArrayResource} or any of the
 * file-based {@code Resource} implementations where possible.
 *
 * <p>In contrast to other {@code Resource} implementations, this is a descriptor
 * for an <i>already opened</i> resource - therefore returning {@code true} from
 * {@link #isOpen()}. Do not use an {@code InputStreamResource} if you need to
 * keep the resource descriptor somewhere, or if you need to read from a stream
 * multiple times.
 *
 * <p>
 * 一个给定的{@link InputStream} <p>的{@link资源}实现应该只有在没有其他特定的{@code Resource}实现适用的情况下才可以使用。
 * 特别地,更喜欢{@link ByteArrayResource}或任何基于文件的{ @code资源}实现。
 * 
 *  <p>与其他{@code Resource}实现相反,这是<i>已经打开的资源的描述符,因此从{@link #isOpen()}返回{@code true}不要使用如果您需要将资源描述符保留在某个地方
 * ,或者您需要多次读取流,则需要{@code InputStreamResource}。
 * 
 * 
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 28.12.2003
 * @see ByteArrayResource
 * @see ClassPathResource
 * @see FileSystemResource
 * @see UrlResource
 */
public class InputStreamResource extends AbstractResource {

	private final InputStream inputStream;

	private final String description;

	private boolean read = false;


	/**
	 * Create a new InputStreamResource.
	 * <p>
	 *  创建一个新的InputStreamResource
	 * 
	 * 
	 * @param inputStream the InputStream to use
	 */
	public InputStreamResource(InputStream inputStream) {
		this(inputStream, "resource loaded through InputStream");
	}

	/**
	 * Create a new InputStreamResource.
	 * <p>
	 *  创建一个新的InputStreamResource
	 * 
	 * 
	 * @param inputStream the InputStream to use
	 * @param description where the InputStream comes from
	 */
	public InputStreamResource(InputStream inputStream, String description) {
		if (inputStream == null) {
			throw new IllegalArgumentException("InputStream must not be null");
		}
		this.inputStream = inputStream;
		this.description = (description != null ? description : "");
	}


	/**
	 * This implementation always returns {@code true}.
	 * <p>
	 *  这个实现总是返回{@code true}
	 * 
	 */
	@Override
	public boolean exists() {
		return true;
	}

	/**
	 * This implementation always returns {@code true}.
	 * <p>
	 *  这个实现总是返回{@code true}
	 * 
	 */
	@Override
	public boolean isOpen() {
		return true;
	}

	/**
	 * This implementation throws IllegalStateException if attempting to
	 * read the underlying stream multiple times.
	 * <p>
	 * 如果尝试多次读取底层流,则此实现会引发IllegalStateException
	 * 
	 */
	@Override
	public InputStream getInputStream() throws IOException, IllegalStateException {
		if (this.read) {
			throw new IllegalStateException("InputStream has already been read - " +
					"do not use InputStreamResource if a stream needs to be read multiple times");
		}
		this.read = true;
		return this.inputStream;
	}

	/**
	 * This implementation returns a description that includes the passed-in
	 * description, if any.
	 * <p>
	 *  此实现返回包含传入描述(如果有)的描述
	 * 
	 */
	@Override
	public String getDescription() {
		return "InputStream resource [" + this.description + "]";
	}


	/**
	 * This implementation compares the underlying InputStream.
	 * <p>
	 *  这个实现比较了底层的InputStream
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		return (obj == this ||
			(obj instanceof InputStreamResource && ((InputStreamResource) obj).inputStream.equals(this.inputStream)));
	}

	/**
	 * This implementation returns the hash code of the underlying InputStream.
	 * <p>
	 *  此实现返回底层InputStream的哈希码
	 */
	@Override
	public int hashCode() {
		return this.inputStream.hashCode();
	}

}
