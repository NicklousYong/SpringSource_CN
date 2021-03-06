/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2016 the original author or authors.
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * A speedy alternative to {@link java.io.ByteArrayOutputStream}. Note that
 * this variant does <i>not</i> extend {@code ByteArrayOutputStream}, unlike
 * its sibling {@link ResizableByteArrayOutputStream}.
 *
 * <p>Unlike {@link java.io.ByteArrayOutputStream}, this implementation is backed
 * by a {@link java.util.LinkedList} of {@code byte[]} instead of 1 constantly
 * resizing {@code byte[]}. It does not copy buffers when it gets expanded.
 *
 * <p>The initial buffer is only created when the stream is first written.
 * There is also no copying of the internal buffer if its contents is extracted
 * with the {@link #writeTo(OutputStream)} method.
 *
 * <p>
 *  {@link javaioByteArrayOutputStream}的一个快速替代方案请注意,此变体不会扩展{@code ByteArrayOutputStream},而不像其兄弟{@link ResizableByteArrayOutputStream}
 * 。
 * 
 * <p>与{@link javaioByteArrayOutputStream}不同,此实现由{@code byte []}的{@link javautilLinkedList}支持,而不是1不断调整大小
 * {@code byte []}它不会在扩展时复制缓冲区。
 * 
 *  初始缓冲区仅在流首次写入时创建。如果内容缓冲区的内容使用{@link #writeTo(OutputStream)}方法提取,那么也不会复制内部缓冲区
 * 
 * 
 * @author Craig Andrews
 * @author Juergen Hoeller
 * @since 4.2
 * @see #resize
 * @see ResizableByteArrayOutputStream
 */
public class FastByteArrayOutputStream extends OutputStream {

	private static final int DEFAULT_BLOCK_SIZE = 256;


	// The buffers used to store the content bytes
	private final LinkedList<byte[]> buffers = new LinkedList<byte[]>();

	// The size, in bytes, to use when allocating the first byte[]
	private final int initialBlockSize;

	// The size, in bytes, to use when allocating the next byte[]
	private int nextBlockSize = 0;

	// The number of bytes in previous buffers.
	// (The number of bytes in the current buffer is in 'index'.)
	private int alreadyBufferedSize = 0;

	// The index in the byte[] found at buffers.getLast() to be written next
	private int index = 0;

	// Is the stream closed?
	private boolean closed = false;


	/**
	 * Create a new <code>FastByteArrayOutputStream</code>
	 * with the default initial capacity of 256 bytes.
	 * <p>
	 *  创建一个新的<code> FastByteArrayOutputStream </code>,默认的初始容量为256字节
	 * 
	 */
	public FastByteArrayOutputStream() {
		this(DEFAULT_BLOCK_SIZE);
	}

	/**
	 * Create a new <code>FastByteArrayOutputStream</code>
	 * with the specified initial capacity.
	 * <p>
	 *  创建具有指定的初始容量的新的<code> FastByteArrayOutputStream </code>
	 * 
	 * 
	 * @param initialBlockSize the initial buffer size in bytes
	 */
	public FastByteArrayOutputStream(int initialBlockSize) {
		Assert.isTrue(initialBlockSize > 0, "Initial block size must be greater than 0");
		this.initialBlockSize = initialBlockSize;
		this.nextBlockSize = initialBlockSize;
	}


	// Overridden methods

	@Override
	public void write(int datum) throws IOException {
		if (this.closed) {
			throw new IOException("Stream closed");
		}
		else {
			if (this.buffers.peekLast() == null || this.buffers.getLast().length == this.index) {
				addBuffer(1);
			}
			// store the byte
			this.buffers.getLast()[this.index++] = (byte) datum;
		}
	}

	@Override
	public void write(byte[] data, int offset, int length) throws IOException {
		if (data == null) {
			throw new NullPointerException();
		}
		else if (offset < 0 || offset + length > data.length || length < 0) {
			throw new IndexOutOfBoundsException();
		}
		else if (this.closed) {
			throw new IOException("Stream closed");
		}
		else {
			if (this.buffers.peekLast() == null || this.buffers.getLast().length == this.index) {
				addBuffer(length);
			}
			if (this.index + length > this.buffers.getLast().length) {
				int pos = offset;
				do {
					if (this.index == this.buffers.getLast().length) {
						addBuffer(length);
					}
					int copyLength = this.buffers.getLast().length - this.index;
					if (length < copyLength) {
						copyLength = length;
					}
					System.arraycopy(data, pos, this.buffers.getLast(), this.index, copyLength);
					pos += copyLength;
					this.index += copyLength;
					length -= copyLength;
				}
				while (length > 0);
			}
			else {
				// copy in the sub-array
				System.arraycopy(data, offset, this.buffers.getLast(), this.index, length);
				this.index += length;
			}
		}
	}

	@Override
	public void close() {
		this.closed = true;
	}

	/**
	 * Convert the buffer's contents into a string decoding bytes using the
	 * platform's default character set. The length of the new <tt>String</tt>
	 * is a function of the character set, and hence may not be equal to the
	 * size of the buffer.
	 * <p>This method always replaces malformed-input and unmappable-character
	 * sequences with the default replacement string for the platform's
	 * default character set. The {@linkplain java.nio.charset.CharsetDecoder}
	 * class should be used when more control over the decoding process is
	 * required.
	 * <p>
	 * 使用平台的默认字符集将缓冲区的内容转换为字符串解码字节新的<tt> String </tt>的长度是字符集的函数,因此可能不等于缓冲区的大小<p >该方法总是用缺省的替换字符串替换不正确的输入和不可映射
	 * 的字符序列,用于平台默认字符集。
	 * 当需要对解码过程进行更多的控制时,应使用{@linkplain javaniocharsetCharsetDecoder}类。
	 * 
	 * 
	 * @return a String decoded from the buffer's contents
	 */
	@Override
	public String toString() {
		return new String(toByteArrayUnsafe());
	}


	// Custom methods

	/**
	 * Return the number of bytes stored in this <code>FastByteArrayOutputStream</code>.
	 * <p>
	 *  返回存储在此<code> FastByteArrayOutputStream </code>中的字节数
	 * 
	 */
	public int size() {
		return (this.alreadyBufferedSize + this.index);
	}

	/**
	 * Convert the stream's data to a byte array and return the byte array.
	 * <p>Also replaces the internal structures with the byte array to conserve memory:
	 * if the byte array is being made anyways, mind as well as use it. This approach
	 * also means that if this method is called twice without any writes in between,
	 * the second call is a no-op.
	 * <p>This method is "unsafe" as it returns the internal buffer.
	 * Callers should not modify the returned buffer.
	 * <p>
	 * 将流的数据转换为字节数组并返回字节数组<p>还用字节数组替换内部结构以节省内存：如果正在进行字节数组,还要注意使用这种方法也意味着如果这个方法被称为两次,没有任何写入,第二个调用是一个no-op <p>
	 * 这个方法是"不安全的",因为它返回内部缓冲区调用者不应该修改返回的缓冲区。
	 * 
	 * 
	 * @return the current contents of this output stream, as a byte array.
	 * @see #size()
	 * @see #toByteArray()
	 */
	public byte[] toByteArrayUnsafe() {
		int totalSize = size();
		if (totalSize == 0) {
			return new byte[0];
		}
		resize(totalSize);
		return this.buffers.getFirst();
	}

	/**
	 * Creates a newly allocated byte array.
	 * <p>Its size is the current
	 * size of this output stream and the valid contents of the buffer
	 * have been copied into it.</p>
	 * <p>
	 *  创建新分配的字节数组<p>其大小是此输出流的当前大小,缓冲区的有效内容已被复制到其中</p>
	 * 
	 * 
	 * @return the current contents of this output stream, as a byte array.
	 * @see #size()
	 * @see #toByteArrayUnsafe()
	 */
	public byte[] toByteArray() {
		byte[] bytesUnsafe = toByteArrayUnsafe();
		byte[] ret = new byte[bytesUnsafe.length];
		System.arraycopy(bytesUnsafe, 0, ret, 0, bytesUnsafe.length);
		return ret;
	}

	/**
	 * Reset the contents of this <code>FastByteArrayOutputStream</code>.
	 * <p>All currently accumulated output in the output stream is discarded.
	 * The output stream can be used again.
	 * <p>
	 *  重置此代码的内容FastByteArrayOutputStream </code> <p>输出流中所有当前累积的输出都被丢弃输出流可以再次使用
	 * 
	 */
	public void reset() {
		this.buffers.clear();
		this.nextBlockSize = this.initialBlockSize;
		this.closed = false;
		this.index = 0;
		this.alreadyBufferedSize = 0;
	}

	/**
	 * Get an {@link InputStream} to retrieve the data in this OutputStream.
	 * <p>Note that if any methods are called on the OutputStream
	 * (including, but not limited to, any of the write methods, {@link #reset()},
	 * {@link #toByteArray()}, and {@link #toByteArrayUnsafe()}) then the
	 * {@link java.io.InputStream}'s behavior is undefined.
	 * <p>
	 * 获取{@link InputStream}以检索此OutputStream中的数据<p>请注意,如果在OutputStream上调用了任何方法(包括但不限于任何写入方法{@link #reset()})
	 *  {@link #toByteArray()}和{@link #toByteArrayUnsafe()}),那么{@link javaioInputStream}的行为是未定义的。
	 * 
	 * 
	 * @return {@link InputStream} of the contents of this OutputStream
	 */
	public InputStream getInputStream() {
		return new FastByteArrayInputStream(this);
	}

	/**
	 * Write the buffers content to the given OutputStream.
	 * <p>
	 *  将缓冲区内容写入给定的OutputStream
	 * 
	 * 
	 * @param out the OutputStream to write to
	 */
	public void writeTo(OutputStream out) throws IOException {
		Iterator<byte[]> it = this.buffers.iterator();
		while (it.hasNext()) {
			byte[] bytes = it.next();
			if (it.hasNext()) {
				out.write(bytes, 0, bytes.length);
			}
			else {
				out.write(bytes, 0, this.index);
			}
		}
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
	 * @see FastByteArrayOutputStream#size()
	 */
	public void resize(int targetCapacity) {
		Assert.isTrue(targetCapacity >= size(), "New capacity must not be smaller than current size");
		if (this.buffers.peekFirst() == null) {
			this.nextBlockSize = targetCapacity - size();
		}
		else if (size() == targetCapacity && this.buffers.getFirst().length == targetCapacity) {
			// do nothing - already at the targetCapacity
		}
		else {
			int totalSize = size();
			byte[] data = new byte[targetCapacity];
			int pos = 0;
			Iterator<byte[]> it = this.buffers.iterator();
			while (it.hasNext()) {
				byte[] bytes = it.next();
				if (it.hasNext()) {
					System.arraycopy(bytes, 0, data, pos, bytes.length);
					pos += bytes.length;
				}
				else {
					System.arraycopy(bytes, 0, data, pos, this.index);
				}
			}
			this.buffers.clear();
			this.buffers.add(data);
			this.index = totalSize;
			this.alreadyBufferedSize = 0;
		}
	}

	/**
	 * Create a new buffer and store it in the LinkedList
	 * <p>Adds a new buffer that can store at least {@code minCapacity} bytes.
	 * <p>
	 *  创建一个新的缓冲区并将其存储在LinkedList中<p>添加一个可以至少存储{@code minCapacity}字节的新缓冲区
	 * 
	 */
	private void addBuffer(int minCapacity) {
		if (this.buffers.peekLast() != null) {
			this.alreadyBufferedSize += this.index;
			this.index = 0;
		}
		if (this.nextBlockSize < minCapacity) {
			this.nextBlockSize = nextPowerOf2(minCapacity);
		}
		this.buffers.add(new byte[this.nextBlockSize]);
		this.nextBlockSize *= 2;  // block size doubles each time
	}

	/**
	 * Get the next power of 2 of a number (ex, the next power of 2 of 119 is 128).
	 * <p>
	 *  获得2的数字的下一个权力(例如,119的下一个权力是128)
	 * 
	 */
	private static int nextPowerOf2(int val) {
		val--;
		val = (val >> 1) | val;
		val = (val >> 2) | val;
		val = (val >> 4) | val;
		val = (val >> 8) | val;
		val = (val >> 16) | val;
		val++;
		return val;
	}


	/**
	 * An implementation of {@link java.io.InputStream} that reads from a given
	 * <code>FastByteArrayOutputStream</code>.
	 * <p>
	 *  从一个给定的<code> FastByteArrayOutputStream </code>读取的{@link javaioInputStream}的实现
	 * 
	 */
	private static final class FastByteArrayInputStream extends UpdateMessageDigestInputStream {

		private final FastByteArrayOutputStream fastByteArrayOutputStream;

		private final Iterator<byte[]> buffersIterator;

		private byte[] currentBuffer;

		private int currentBufferLength = 0;

		private int nextIndexInCurrentBuffer = 0;

		private int totalBytesRead = 0;

		/**
		 * Create a new <code>FastByteArrayOutputStreamInputStream</code> backed
		 * by the given <code>FastByteArrayOutputStream</code>.
		 * <p>
		 * 创建由给定的<code> FastByteArrayOutputStream </code>支持的新的<code> FastByteArrayOutputStreamInputStream </code>
		 * 。
		 * 
		 */
		public FastByteArrayInputStream(FastByteArrayOutputStream fastByteArrayOutputStream) {
			this.fastByteArrayOutputStream = fastByteArrayOutputStream;
			this.buffersIterator = fastByteArrayOutputStream.buffers.iterator();
			if (this.buffersIterator.hasNext()) {
				this.currentBuffer = this.buffersIterator.next();
				if (this.currentBuffer == fastByteArrayOutputStream.buffers.getLast()) {
					this.currentBufferLength = fastByteArrayOutputStream.index;
				}
				else {
					this.currentBufferLength = this.currentBuffer.length;
				}
			}
		}

		@Override
		public int read() {
			if (this.currentBuffer == null) {
				// This stream doesn't have any data in it...
				return -1;
			}
			else {
				if (this.nextIndexInCurrentBuffer < this.currentBufferLength) {
					this.totalBytesRead++;
					return this.currentBuffer[this.nextIndexInCurrentBuffer++];
				}
				else {
					if (this.buffersIterator.hasNext()) {
						this.currentBuffer = this.buffersIterator.next();
						if (this.currentBuffer == this.fastByteArrayOutputStream.buffers.getLast()) {
							this.currentBufferLength = this.fastByteArrayOutputStream.index;
						}
						else {
							this.currentBufferLength = this.currentBuffer.length;
						}
						this.nextIndexInCurrentBuffer = 0;
					}
					else {
						this.currentBuffer = null;
					}
					return read();
				}
			}
		}

		@Override
		public int read(byte[] b) {
			return read(b, 0, b.length);
		}

		@Override
		public int read(byte[] b, int off, int len) {
			if (b == null) {
				throw new NullPointerException();
			}
			else if (off < 0 || len < 0 || len > b.length - off) {
				throw new IndexOutOfBoundsException();
			}
			else if (len == 0) {
				return 0;
			}
			else if (len < 0) {
				throw new IllegalArgumentException("len must be 0 or greater: " + len);
			}
			else if (off < 0) {
				throw new IllegalArgumentException("off must be 0 or greater: " + off);
			}
			else {
				if (this.currentBuffer == null) {
					// This stream doesn't have any data in it...
					return -1;
				}
				else {
					if (this.nextIndexInCurrentBuffer < this.currentBufferLength) {
						int bytesToCopy = Math.min(len, this.currentBufferLength - this.nextIndexInCurrentBuffer);
						System.arraycopy(this.currentBuffer, this.nextIndexInCurrentBuffer, b, off, bytesToCopy);
						this.totalBytesRead += bytesToCopy;
						this.nextIndexInCurrentBuffer += bytesToCopy;
						int remaining = read(b, off + bytesToCopy, len - bytesToCopy);
						return bytesToCopy + Math.max(remaining, 0);
					}
					else {
						if (this.buffersIterator.hasNext()) {
							this.currentBuffer = this.buffersIterator.next();
							if (this.currentBuffer == this.fastByteArrayOutputStream.buffers.getLast()) {
								this.currentBufferLength = this.fastByteArrayOutputStream.index;
							}
							else {
								this.currentBufferLength = this.currentBuffer.length;
							}
							this.nextIndexInCurrentBuffer = 0;
						}
						else {
							this.currentBuffer = null;
						}
						return read(b, off, len);
					}
				}
			}
		}

		@Override
		public long skip(long n) throws IOException {
			if (n > Integer.MAX_VALUE) {
				throw new IllegalArgumentException("n exceeds maximum (" + Integer.MAX_VALUE + "): " + n);
			}
			else if (n == 0) {
				return 0;
			}
			else if (n < 0) {
				throw new IllegalArgumentException("n must be 0 or greater: " + n);
			}
			int len = (int) n;
			if (this.currentBuffer == null) {
				// This stream doesn't have any data in it...
				return 0;
			}
			else {
				if (this.nextIndexInCurrentBuffer < this.currentBufferLength) {
					int bytesToSkip = Math.min(len, this.currentBufferLength - this.nextIndexInCurrentBuffer);
					this.totalBytesRead += bytesToSkip;
					this.nextIndexInCurrentBuffer += bytesToSkip;
					return (bytesToSkip + skip(len - bytesToSkip));
				}
				else {
					if (this.buffersIterator.hasNext()) {
						this.currentBuffer = this.buffersIterator.next();
						if (this.currentBuffer == this.fastByteArrayOutputStream.buffers.getLast()) {
							this.currentBufferLength = this.fastByteArrayOutputStream.index;
						}
						else {
							this.currentBufferLength = this.currentBuffer.length;
						}
						this.nextIndexInCurrentBuffer = 0;
					}
					else {
						this.currentBuffer = null;
					}
					return skip(len);
				}
			}
		}

		@Override
		public int available() {
			return (this.fastByteArrayOutputStream.size() - this.totalBytesRead);
		}

		/**
		 * Update the message digest with the remaining bytes in this stream.
		 * <p>
		 *  使用此流中剩余的字节更新消息摘要
		 * 
		 * 
		 * @param messageDigest The message digest to update
		 */
		public void updateMessageDigest(MessageDigest messageDigest) {
			updateMessageDigest(messageDigest, available());
		}

		/**
		 * Update the message digest with the next len bytes in this stream.
		 * Avoids creating new byte arrays and use internal buffers for performance.
		 * <p>
		 *  使用此流中的下一个len字节更新消息摘要避免创建新的字节数组并使用内部缓冲区来执行性能
		 * 
		 * @param messageDigest The message digest to update
		 * @param len how many bytes to read from this stream and use to update the message digest
		 */
		public void updateMessageDigest(MessageDigest messageDigest, int len) {
			if (this.currentBuffer == null) {
				// This stream doesn't have any data in it...
				return;
			}
			else if (len == 0) {
				return;
			}
			else if (len < 0) {
				throw new IllegalArgumentException("len must be 0 or greater: " + len);
			}
			else {
				if (this.nextIndexInCurrentBuffer < this.currentBufferLength) {
					int bytesToCopy = Math.min(len, this.currentBufferLength - this.nextIndexInCurrentBuffer);
					messageDigest.update(this.currentBuffer, this.nextIndexInCurrentBuffer, bytesToCopy);
					this.nextIndexInCurrentBuffer += bytesToCopy;
					updateMessageDigest(messageDigest, len - bytesToCopy);
				}
				else {
					if (this.buffersIterator.hasNext()) {
						this.currentBuffer = this.buffersIterator.next();
						if (this.currentBuffer == this.fastByteArrayOutputStream.buffers.getLast()) {
							this.currentBufferLength = this.fastByteArrayOutputStream.index;
						}
						else {
							this.currentBufferLength = this.currentBuffer.length;
						}
						this.nextIndexInCurrentBuffer = 0;
					}
					else {
						this.currentBuffer = null;
					}
					updateMessageDigest(messageDigest, len);
				}
			}
		}
	}

}
