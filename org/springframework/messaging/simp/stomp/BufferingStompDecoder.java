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

package org.springframework.messaging.simp.stomp;


import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.messaging.Message;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


/**
 * An extension of {@link org.springframework.messaging.simp.stomp.StompDecoder}
 * that buffers content remaining in the input ByteBuffer after the parent
 * class has read all (complete) STOMP frames from it. The remaining content
 * represents an incomplete STOMP frame. When called repeatedly with additional
 * data, the decode method returns one or more messages or, if there is not
 * enough data still, continues to buffer.
 *
 * <p>A single instance of this decoder can be invoked repeatedly to read all
 * messages from a single stream (e.g. WebSocket session) as long as decoding
 * does not fail. If there is an exception, StompDecoder instance should not
 * be used any more as its internal state is not guaranteed to be consistent.
 * It is expected that the underlying session is closed at that point.
 *
 * <p>
 * 在父类从其读取所有(完整的)STOMP帧之后,缓存在输入ByteBuffer中剩余的内容的{@link orgspringframeworkmessagingsimpstompStompDecoder}
 * 的扩展剩余内容表示不完整的STOMP帧当使用附加数据重复调用时,decode方法返回一个或更多的消息,或者如果没有足够的数据仍然继续缓冲。
 * 
 *  <p>只要解码不失败,可以重复调用该解码器的单个实例来读取来自单个流(例如WebSocket会话)的所有消息如果存在异常,则不应再使用StompDecoder实例作为其内部状态不能保证一致预期底层会话
 * 在此时关闭。
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 4.0.3
 */
public class BufferingStompDecoder {

	private final StompDecoder stompDecoder;

	private final int bufferSizeLimit;

	private final Queue<ByteBuffer> chunks = new LinkedBlockingQueue<ByteBuffer>();

	private volatile Integer expectedContentLength;


	public BufferingStompDecoder(StompDecoder stompDecoder, int bufferSizeLimit) {
		Assert.notNull(stompDecoder, "'stompDecoder' is required");
		Assert.isTrue(bufferSizeLimit > 0, "Buffer size must be greater than 0");
		this.stompDecoder = stompDecoder;
		this.bufferSizeLimit = bufferSizeLimit;
	}


	/**
	 * Return the wrapped
	 * {@link org.springframework.messaging.simp.stomp.StompDecoder}.
	 * <p>
	 * 返回包装的{@link orgspringframeworkmessagingsimpstompStompDecoder}
	 * 
	 */
	public StompDecoder getStompDecoder() {
		return this.stompDecoder;
	}

	/**
	 * Return the configured buffer size limit.
	 * <p>
	 *  返回配置的缓冲区大小限制
	 * 
	 */
	public int getBufferSizeLimit() {
		return this.bufferSizeLimit;
	}

	/**
	 * Calculate the current buffer size.
	 * <p>
	 *  计算当前的缓冲区大小
	 * 
	 */
	public int getBufferSize() {
		int size = 0;
		for (ByteBuffer buffer : this.chunks) {
			size = size + buffer.remaining();
		}
		return size;
	}

	/**
	 * Get the expected content length of the currently buffered, incomplete STOMP frame.
	 * <p>
	 *  获取当前缓冲的,不完整的STOMP帧的预期内容长度
	 * 
	 */
	public Integer getExpectedContentLength() {
		return this.expectedContentLength;
	}


	/**
	 * Decodes one or more STOMP frames from the given {@code ByteBuffer} into a
	 * list of {@link Message}s.
	 *
	 * <p>If there was enough data to parse a "content-length" header, then the
	 * value is used to determine how much more data is needed before a new
	 * attempt to decode is made.
	 *
	 * <p>If there was not enough data to parse the "content-length", or if there
	 * is "content-length" header, every subsequent call to decode attempts to
	 * parse again with all available data. Therefore the presence of a "content-length"
	 * header helps to optimize the decoding of large messages.
	 *
	 * <p>
	 *  将一个或多个STOMP帧从给定的{@code ByteBuffer}解码为{@link Message}的列表
	 * 
	 *  <p>如果有足够的数据来解析"内容长度"标题,则该值用于确定在进行新的解码尝试之前需要多少数据
	 * 
	 * @param newBuffer a buffer containing new data to decode
	 *
	 * @return decoded messages or an empty list
	 * @throws StompConversionException raised in case of decoding issues
	 */
	public List<Message<byte[]>> decode(ByteBuffer newBuffer) {

		this.chunks.add(newBuffer);

		checkBufferLimits();

		if (getExpectedContentLength() != null && getBufferSize() < this.expectedContentLength) {
			return Collections.<Message<byte[]>>emptyList();
		}

		ByteBuffer bufferToDecode = assembleChunksAndReset();

		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		List<Message<byte[]>> messages = this.stompDecoder.decode(bufferToDecode, headers);

		if (bufferToDecode.hasRemaining()) {
			this.chunks.add(bufferToDecode);
			this.expectedContentLength = StompHeaderAccessor.getContentLength(headers);
		}

		return messages;
	}

	private void checkBufferLimits() {
		if (getExpectedContentLength() != null) {
			if (getExpectedContentLength() > getBufferSizeLimit()) {
				throw new StompConversionException(
						"The 'content-length' header " + getExpectedContentLength() +
								"  exceeds the configured message buffer size limit " + getBufferSizeLimit());
			}
		}
		if (getBufferSize() > getBufferSizeLimit()) {
			throw new StompConversionException("The configured stomp frame buffer size limit of " +
					getBufferSizeLimit() + " bytes has been exceeded");

		}
	}

	private ByteBuffer assembleChunksAndReset() {
		ByteBuffer result;
		if (this.chunks.size() == 1) {
			result = this.chunks.remove();
		}
		else {
			result = ByteBuffer.allocate(getBufferSize());
			for (ByteBuffer partial : this.chunks) {
				result.put(partial);
			}
			result.flip();
		}
		this.chunks.clear();
		this.expectedContentLength = null;
		return result;
	}

}
