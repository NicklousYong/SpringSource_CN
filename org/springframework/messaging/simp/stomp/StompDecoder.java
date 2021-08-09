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

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderInitializer;
import org.springframework.messaging.support.NativeMessageHeaderAccessor;
import org.springframework.util.InvalidMimeTypeException;
import org.springframework.util.MultiValueMap;

/**
 * Decodes one or more STOMP frames contained in a {@link ByteBuffer}.
 *
 * <p>An attempt is made to read all complete STOMP frames from the buffer, which
 * could be zero, one, or more. If there is any left-over content, i.e. an incomplete
 * STOMP frame, at the end the buffer is reset to point to the beginning of the
 * partial content. The caller is then responsible for dealing with that
 * incomplete content by buffering until there is more input available.
 *
 * <p>
 *  解码{@link ByteBuffer}中包含的一个或多个STOMP帧
 * 
 * <p>尝试从缓冲区中读取所有完整的STOMP帧,可能为零,一个或更多如果存在任何遗留内容,即不完整的STOMP帧,则在缓冲区重置为点到部分内容的开始当调用者负责通过缓冲来处理不完整的内容,直到有更多的输
 * 入可用。
 * 
 * 
 * @author Andy Wilkinson
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public class StompDecoder {

	static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

	static final byte[] HEARTBEAT_PAYLOAD = new byte[] {'\n'};

	private static final Log logger = LogFactory.getLog(StompDecoder.class);


	private MessageHeaderInitializer headerInitializer;


	/**
	 * Configure a
	 * {@link org.springframework.messaging.support.MessageHeaderInitializer MessageHeaderInitializer}
	 * to apply to the headers of {@link Message}s from decoded STOMP frames.
	 * <p>
	 *  配置{@link orgspringframeworkmessagingsupportMessageHeaderInitializer MessageHeaderInitializer}以应用于解码的
	 * STOMP帧中的{@link消息}的标头。
	 * 
	 */
	public void setHeaderInitializer(MessageHeaderInitializer headerInitializer) {
		this.headerInitializer = headerInitializer;
	}

	/**
	 * Return the configured {@code MessageHeaderInitializer}, if any.
	 * <p>
	 *  返回配置的{@code MessageHeaderInitializer}(如果有)
	 * 
	 */
	public MessageHeaderInitializer getHeaderInitializer() {
		return this.headerInitializer;
	}


	/**
	 * Decodes one or more STOMP frames from the given {@code ByteBuffer} into a
	 * list of {@link Message}s. If the input buffer contains partial STOMP frame
	 * content, or additional content with a partial STOMP frame, the buffer is
	 * reset and {@code null} is returned.
	 * <p>
	 * 将一个或多个STOMP帧从给定的{@code ByteBuffer}解码为{@link消息}的列表。
	 * 如果输入缓冲区包含部分STOMP帧内容或具有部分STOMP帧的附加内容,则重置缓冲区,{@代码null}被返回。
	 * 
	 * 
	 * @param buffer the buffer to decode the STOMP frame from
	 * @return the decoded messages, or an empty list if none
	 * @throws StompConversionException raised in case of decoding issues
	 */
	public List<Message<byte[]>> decode(ByteBuffer buffer) {
		return decode(buffer, null);
	}

	/**
	 * Decodes one or more STOMP frames from the given {@code buffer} and returns
	 * a list of {@link Message}s.
	 * <p>If the given ByteBuffer contains only partial STOMP frame content and no
	 * complete STOMP frames, an empty list is returned, and the buffer is reset to
	 * to where it was.
	 * <p>If the buffer contains one ore more STOMP frames, those are returned and
	 * the buffer reset to point to the beginning of the unused partial content.
	 * <p>The output partialMessageHeaders map is used to store successfully parsed
	 * headers in case of partial content. The caller can then check if a
	 * "content-length" header was read, which helps to determine how much more
	 * content is needed before the next attempt to decode.
	 * <p>
	 * 从给定的{@code缓冲区}中解码一个或多个STOMP帧,并返回{@link消息}的列表。
	 * 如果给定的ByteBuffer仅包含部分STOMP帧内容,并且没有完整的STOMP帧,则返回空列表,并且缓冲区被重置为其中的位置<p>如果缓冲区包含一个以上的STOMP帧,则返回这些帧,并将缓冲区重置为
	 * 指向未使用的部分内容的开始<p>输出partialMessageHeaders映射用于在部分内容的情况下存储成功解析的头文件主叫方可以检查是否读取了"内容长度"头,这有助于在下次尝试解码之前确定需要多少
	 * 内容。
	 * 从给定的{@code缓冲区}中解码一个或多个STOMP帧,并返回{@link消息}的列表。
	 * 
	 * 
	 * @param buffer the buffer to decode the STOMP frame from
	 * @param partialMessageHeaders an empty output map that will store the last
	 * successfully parsed partialMessageHeaders in case of partial message content
	 * in cases where the partial buffer ended with a partial STOMP frame
	 * @return the decoded messages, or an empty list if none
	 * @throws StompConversionException raised in case of decoding issues
	 */
	public List<Message<byte[]>> decode(ByteBuffer buffer, MultiValueMap<String, String> partialMessageHeaders) {
		List<Message<byte[]>> messages = new ArrayList<Message<byte[]>>();
		while (buffer.hasRemaining()) {
			Message<byte[]> message = decodeMessage(buffer, partialMessageHeaders);
			if (message != null) {
				messages.add(message);
			}
			else {
				break;
			}
		}
		return messages;
	}

	/**
	 * Decode a single STOMP frame from the given {@code buffer} into a {@link Message}.
	 * <p>
	 *  将单个STOMP帧从给定的{@code缓冲区}解码为{@link消息}
	 * 
	 */
	private Message<byte[]> decodeMessage(ByteBuffer buffer, MultiValueMap<String, String> headers) {
		Message<byte[]> decodedMessage = null;
		skipLeadingEol(buffer);
		buffer.mark();

		String command = readCommand(buffer);
		if (command.length() > 0) {
			StompHeaderAccessor headerAccessor = null;
			byte[] payload = null;
			if (buffer.remaining() > 0) {
				StompCommand stompCommand = StompCommand.valueOf(command);
				headerAccessor = StompHeaderAccessor.create(stompCommand);
				initHeaders(headerAccessor);
				readHeaders(buffer, headerAccessor);
				payload = readPayload(buffer, headerAccessor);
			}
			if (payload != null) {
				if (payload.length > 0 && !headerAccessor.getCommand().isBodyAllowed()) {
					throw new StompConversionException(headerAccessor.getCommand() +
							" shouldn't have a payload: length=" + payload.length + ", headers=" + headers);
				}
				headerAccessor.updateSimpMessageHeadersFromStompHeaders();
				headerAccessor.setLeaveMutable(true);
				decodedMessage = MessageBuilder.createMessage(payload, headerAccessor.getMessageHeaders());
				if (logger.isTraceEnabled()) {
					logger.trace("Decoded " + headerAccessor.getDetailedLogMessage(payload));
				}
			}
			else {
				if (logger.isTraceEnabled()) {
					logger.trace("Incomplete frame, resetting input buffer...");
				}
				if (headers != null && headerAccessor != null) {
					String name = NativeMessageHeaderAccessor.NATIVE_HEADERS;
					@SuppressWarnings("unchecked")
					MultiValueMap<String, String> map = (MultiValueMap<String, String>) headerAccessor.getHeader(name);
					if (map != null) {
						headers.putAll(map);
					}
				}
				buffer.reset();
			}
		}
		else {
			StompHeaderAccessor headerAccessor = StompHeaderAccessor.createForHeartbeat();
			initHeaders(headerAccessor);
			headerAccessor.setLeaveMutable(true);
			decodedMessage = MessageBuilder.createMessage(HEARTBEAT_PAYLOAD, headerAccessor.getMessageHeaders());
			if (logger.isTraceEnabled()) {
				logger.trace("Decoded " + headerAccessor.getDetailedLogMessage(null));
			}
		}

		return decodedMessage;
	}

	private void initHeaders(StompHeaderAccessor headerAccessor) {
		MessageHeaderInitializer initializer = getHeaderInitializer();
		if (initializer != null) {
			initializer.initHeaders(headerAccessor);
		}
	}

	/**
	 * Skip one ore more EOL characters at the start of the given ByteBuffer.
	 * Those are STOMP heartbeat frames.
	 * <p>
	 * 在给定的ByteBuffer的开头跳过一个更多的EOL字符这些是STOMP心跳帧
	 * 
	 */
	protected void skipLeadingEol(ByteBuffer buffer) {
		while (true) {
			if (!tryConsumeEndOfLine(buffer)) {
				break;
			}
		}
	}

	private String readCommand(ByteBuffer buffer) {
		ByteArrayOutputStream command = new ByteArrayOutputStream(256);
		while (buffer.remaining() > 0 && !tryConsumeEndOfLine(buffer)) {
			command.write(buffer.get());
		}
		return new String(command.toByteArray(), UTF8_CHARSET);
	}

	private void readHeaders(ByteBuffer buffer, StompHeaderAccessor headerAccessor) {
		while (true) {
			ByteArrayOutputStream headerStream = new ByteArrayOutputStream(256);
			while (buffer.remaining() > 0 && !tryConsumeEndOfLine(buffer)) {
				headerStream.write(buffer.get());
			}
			if (headerStream.size() > 0) {
				String header = new String(headerStream.toByteArray(), UTF8_CHARSET);
				int colonIndex = header.indexOf(':');
				if (colonIndex <= 0 || colonIndex == header.length() - 1) {
					if (buffer.remaining() > 0) {
						throw new StompConversionException("Illegal header: '" + header +
								"'. A header must be of the form <name>:<value>.");
					}
				}
				else {
					String headerName = unescape(header.substring(0, colonIndex));
					String headerValue = unescape(header.substring(colonIndex + 1));
					try {
						headerAccessor.addNativeHeader(headerName, headerValue);
					}
					catch (InvalidMimeTypeException ex) {
						if (buffer.remaining() > 0) {
							throw ex;
						}
					}
				}
			}
			else {
				break;
			}
		}
	}

	/**
	 * See STOMP Spec 1.2:
	 * <a href="http://stomp.github.io/stomp-specification-1.2.html#Value_Encoding">"Value Encoding"</a>.
	 * <p>
	 *  请参阅STOMP规范12：<a href=\"http://stompgithubio/stomp-specification-12html#Value_Encoding\">"值编码"</a>
	 * 
	 */
	private String unescape(String inString) {
		StringBuilder sb = new StringBuilder(inString.length());
		int pos = 0;  // position in the old string
		int index = inString.indexOf("\\");

		while (index >= 0) {
			sb.append(inString.substring(pos, index));
			if (index + 1 >= inString.length()) {
				throw new StompConversionException("Illegal escape sequence at index " + index + ": " + inString);
			}
			Character c = inString.charAt(index + 1);
			if (c == 'r') {
				sb.append('\r');
			}
			else if (c == 'n') {
				sb.append('\n');
			}
			else if (c == 'c') {
				sb.append(':');
			}
			else if (c == '\\') {
				sb.append('\\');
			}
			else {
				// should never happen
				throw new StompConversionException("Illegal escape sequence at index " + index + ": " + inString);
			}
			pos = index + 2;
			index = inString.indexOf("\\", pos);
		}

		sb.append(inString.substring(pos));
		return sb.toString();
	}

	private byte[] readPayload(ByteBuffer buffer, StompHeaderAccessor headerAccessor) {
		Integer contentLength;
		try {
			contentLength = headerAccessor.getContentLength();
		}
		catch (NumberFormatException ex) {
			logger.warn("Ignoring invalid content-length: '" + headerAccessor);
			contentLength = null;
		}

		if (contentLength != null && contentLength >= 0) {
			if (buffer.remaining() > contentLength) {
				byte[] payload = new byte[contentLength];
				buffer.get(payload);
				if (buffer.get() != 0) {
					throw new StompConversionException("Frame must be terminated with a null octet");
				}
				return payload;
			}
			else {
				return null;
			}
		}
		else {
			ByteArrayOutputStream payload = new ByteArrayOutputStream(256);
			while (buffer.remaining() > 0) {
				byte b = buffer.get();
				if (b == 0) {
					return payload.toByteArray();
				}
				else {
					payload.write(b);
				}
			}
		}
		return null;
	}

	/**
	 * Try to read an EOL incrementing the buffer position if successful.
	 * <p>
	 *  如果成功,尝试阅读EOL增加缓冲位置
	 * 
	 * @return whether an EOL was consumed
	 */
	private boolean tryConsumeEndOfLine(ByteBuffer buffer) {
		if (buffer.remaining() > 0) {
			byte b = buffer.get();
			if (b == '\n') {
				return true;
			}
			else if (b == '\r') {
				if (buffer.remaining() > 0 && buffer.get() == '\n') {
					return true;
				}
				else {
					throw new StompConversionException("'\\r' must be followed by '\\n'");
				}
			}
			buffer.position(buffer.position() - 1);
		}
		return false;
	}

}
