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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * {@link Resource} implementation for a given byte array.
 * <p>Creates a {@link ByteArrayInputStream} for the given byte array.
 *
 * <p>Useful for loading content from any given byte array,
 * without having to resort to a single-use {@link InputStreamResource}.
 * Particularly useful for creating mail attachments from local content,
 * where JavaMail needs to be able to read the stream multiple times.
 *
 * <p>
 *  给定字节数组的{@link Resource}实现<p>为给定的字节数组创建{@link ByteArrayInputStream}
 * 
 * <p>适用于从任何给定的字节数组加载内容,而无需使用单次使用{@link InputStreamResource}特别适用于从本地内容创建邮件附件,JavaMail需要能够多次读取流
 * 
 * 
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 1.2.3
 * @see java.io.ByteArrayInputStream
 * @see InputStreamResource
 * @see org.springframework.mail.javamail.MimeMessageHelper#addAttachment(String, InputStreamSource)
 */
public class ByteArrayResource extends AbstractResource {

	private final byte[] byteArray;

	private final String description;


	/**
	 * Create a new ByteArrayResource.
	 * <p>
	 *  创建一个新的ByteArrayResource
	 * 
	 * 
	 * @param byteArray the byte array to wrap
	 */
	public ByteArrayResource(byte[] byteArray) {
		this(byteArray, "resource loaded from byte array");
	}

	/**
	 * Create a new ByteArrayResource.
	 * <p>
	 *  创建一个新的ByteArrayResource
	 * 
	 * 
	 * @param byteArray the byte array to wrap
	 * @param description where the byte array comes from
	 */
	public ByteArrayResource(byte[] byteArray, String description) {
		if (byteArray == null) {
			throw new IllegalArgumentException("Byte array must not be null");
		}
		this.byteArray = byteArray;
		this.description = (description != null ? description : "");
	}

	/**
	 * Return the underlying byte array.
	 * <p>
	 *  返回底层字节数组
	 * 
	 */
	public final byte[] getByteArray() {
		return this.byteArray;
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
	 * This implementation returns the length of the underlying byte array.
	 * <p>
	 *  此实现返回底层字节数组的长度
	 * 
	 */
	@Override
	public long contentLength() {
		return this.byteArray.length;
	}

	/**
	 * This implementation returns a ByteArrayInputStream for the
	 * underlying byte array.
	 * <p>
	 *  此实现为底层字节数组返回ByteArrayInputStream
	 * 
	 * 
	 * @see java.io.ByteArrayInputStream
	 */
	@Override
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(this.byteArray);
	}

	/**
	 * This implementation returns a description that includes the passed-in
	 * {@code description}, if any.
	 * <p>
	 *  此实现返回包含传入的{@code描述}的描述(如果有)
	 * 
	 */
	@Override
	public String getDescription() {
		return "Byte array resource [" + this.description + "]";
	}


	/**
	 * This implementation compares the underlying byte array.
	 * <p>
	 *  这个实现比较了底层字节数组
	 * 
	 * 
	 * @see java.util.Arrays#equals(byte[], byte[])
	 */
	@Override
	public boolean equals(Object obj) {
		return (obj == this ||
			(obj instanceof ByteArrayResource && Arrays.equals(((ByteArrayResource) obj).byteArray, this.byteArray)));
	}

	/**
	 * This implementation returns the hash code based on the
	 * underlying byte array.
	 * <p>
	 * 此实现返回基于底层字节数组的哈希码
	 */
	@Override
	public int hashCode() {
		return (byte[].class.hashCode() * 29 * this.byteArray.length);
	}

}
