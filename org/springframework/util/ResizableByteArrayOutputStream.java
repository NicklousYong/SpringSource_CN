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

package org.springframework.util;

import java.io.ByteArrayOutputStream;

/**
 * An extension of {@link java.io.ByteArrayOutputStream} that:
 * <ul>
 * <li>has public {@link org.springframework.util.ResizableByteArrayOutputStream#grow(int)}
 * and {@link org.springframework.util.ResizableByteArrayOutputStream#resize(int)} methods
 * to get more control over the size of the internal buffer</li>
 * <li>has a higher initial capacity (256) by default</li>
 * </ul>
 *
 * <p>As of 4.2, this class has been superseded by {@link FastByteArrayOutputStream}
 * for Spring's internal use where no assignability to {@link ByteArrayOutputStream}
 * is needed (since {@link FastByteArrayOutputStream} is more efficient with buffer
 * resize management but doesn't extend the standard {@link ByteArrayOutputStream}).
 *
 * <p>
 *  {@link javaioByteArrayOutputStream}的扩展名为：
 * <ul>
 * <li>具有公开的{@link orgspringframeworkutilResizableByteArrayOutputStream#grow(int)}和{@link orgspringframeworkutilResizableByteArrayOutputStream#resize(int)}
 * 方法来获得对内部缓冲区大小的更多控制</li> <li>具有较高的初始容量(256)默认情况下</li>。
 * </ul>
 * 
 *  <p>截至42,该类已经被Spring的内部使用{@link FastByteArrayOutputStream}所取代,因为不需要对{@link ByteArrayOutputStream}进行可分
 * 配性(因为{@link FastByteArrayOutputStream}对缓冲区大小管理更有效,扩展标准{@link ByteArrayOutputStream})。
 * 
 * 
 * @author Brian Clozel
 * @author Juergen Hoeller
 * @since 4.0.3
 * @see #resize
 * @see FastByteArrayOutputStream
 */
public class ResizableByteArrayOutputStream extends ByteArrayOutputStream {

	private static final int DEFAULT_INITIAL_CAPACITY = 256;


	/**
	 * Create a new <code>ResizableByteArrayOutputStream</code>
	 * with the default initial capacity of 256 bytes.
	 * <p>
	 *  创建一个新的<code> ResizableByteArrayOutputStream </code>,默认初始容量为256字节
	 * 
	 */
	public ResizableByteArrayOutputStream() {
		super(DEFAULT_INITIAL_CAPACITY);
	}

	/**
	 * Create a new <code>ResizableByteArrayOutputStream</code>
	 * with the specified initial capacity.
	 * <p>
	 * 创建一个新的具有指定初始容量的<code> ResizableByteArrayOutputStream </code>
	 * 
	 * 
	 * @param initialCapacity the initial buffer size in bytes
	 */
	public ResizableByteArrayOutputStream(int initialCapacity) {
		super(initialCapacity);
	}


	/**
	 * Resize the internal buffer size to a specified capacity.
	 * <p>
	 *  将内部缓冲区大小调整为指定的容量
	 * 
	 * 
	 * @param targetCapacity the desired size of the buffer
	 * @throws IllegalArgumentException if the given capacity is smaller than
	 * the actual size of the content stored in the buffer already
	 * @see ResizableByteArrayOutputStream#size()
	 */
	public synchronized void resize(int targetCapacity) {
		Assert.isTrue(targetCapacity >= this.count, "New capacity must not be smaller than current size");
		byte[] resizedBuffer = new byte[targetCapacity];
		System.arraycopy(this.buf, 0, resizedBuffer, 0, this.count);
		this.buf = resizedBuffer;
	}

	/**
	 * Grow the internal buffer size.
	 * <p>
	 *  增加内部缓冲区大小
	 * 
	 * 
	 * @param additionalCapacity the number of bytes to add to the current buffer size
	 * @see ResizableByteArrayOutputStream#size()
	 */
	public synchronized void grow(int additionalCapacity) {
		Assert.isTrue(additionalCapacity >= 0, "Additional capacity must be 0 or higher");
		if (this.count + additionalCapacity > this.buf.length) {
			int newCapacity = Math.max(this.buf.length * 2, this.count + additionalCapacity);
			resize(newCapacity);
		}
	}

	/**
	 * Return the current size of this stream's internal buffer.
	 * <p>
	 *  返回此流的内部缓冲区的当前大小
	 */
	public synchronized int capacity() {
		return this.buf.length;
	}

}
