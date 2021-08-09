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

package org.springframework.web.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.springframework.util.FastByteArrayOutputStream;

/**
 * {@link javax.servlet.http.HttpServletResponse} wrapper that caches all content written to
 * the {@linkplain #getOutputStream() output stream} and {@linkplain #getWriter() writer},
 * and allows this content to be retrieved via a {@link #getContentAsByteArray() byte array}.
 *
 * <p>Used e.g. by {@link org.springframework.web.filter.ShallowEtagHeaderFilter}.
 *
 * <p>
 * {@link javaxservlethttpHttpServletResponse}包装器,用于缓存写入{@linkplain #getOutputStream()输出流}和{@linkplain #getWriter()writer}
 * 的所有内容),并允许通过{@link #getContentAsByteArray()字节数组}。
 * 
 *  <p>例如通过{@link orgspringframeworkwebfilterShallowEtagHeaderFilter}
 * 
 * 
 * @author Juergen Hoeller
 * @since 4.1.3
 * @see ContentCachingRequestWrapper
 */
public class ContentCachingResponseWrapper extends HttpServletResponseWrapper {

	private final FastByteArrayOutputStream content = new FastByteArrayOutputStream(1024);

	private final ServletOutputStream outputStream = new ResponseServletOutputStream();

	private PrintWriter writer;

	private int statusCode = HttpServletResponse.SC_OK;

	private Integer contentLength;


	/**
	 * Create a new ContentCachingResponseWrapper for the given servlet response.
	 * <p>
	 *  为给定的servlet响应创建一个新的ContentCachingResponseWrapper
	 * 
	 * 
	 * @param response the original servlet response
	 */
	public ContentCachingResponseWrapper(HttpServletResponse response) {
		super(response);
	}


	@Override
	public void setStatus(int sc) {
		super.setStatus(sc);
		this.statusCode = sc;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setStatus(int sc, String sm) {
		super.setStatus(sc, sm);
		this.statusCode = sc;
	}

	@Override
	public void sendError(int sc) throws IOException {
		copyBodyToResponse(false);
		try {
			super.sendError(sc);
		}
		catch (IllegalStateException ex) {
			// Possibly on Tomcat when called too late: fall back to silent setStatus
			super.setStatus(sc);
		}
		this.statusCode = sc;
	}

	@Override
	@SuppressWarnings("deprecation")
	public void sendError(int sc, String msg) throws IOException {
		copyBodyToResponse(false);
		try {
			super.sendError(sc, msg);
		}
		catch (IllegalStateException ex) {
			// Possibly on Tomcat when called too late: fall back to silent setStatus
			super.setStatus(sc, msg);
		}
		this.statusCode = sc;
	}

	@Override
	public void sendRedirect(String location) throws IOException {
		copyBodyToResponse(false);
		super.sendRedirect(location);
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return this.outputStream;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		if (this.writer == null) {
			String characterEncoding = getCharacterEncoding();
			this.writer = (characterEncoding != null ? new ResponsePrintWriter(characterEncoding) :
					new ResponsePrintWriter(WebUtils.DEFAULT_CHARACTER_ENCODING));
		}
		return this.writer;
	}

	@Override
	public void flushBuffer() throws IOException {
		// do not flush the underlying response as the content as not been copied to it yet
	}

	@Override
	public void setContentLength(int len) {
		if (len > this.content.size()) {
			this.content.resize(len);
		}
		this.contentLength = len;
	}

	// Overrides Servlet 3.1 setContentLengthLong(long) at runtime
	public void setContentLengthLong(long len) {
		if (len > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("Content-Length exceeds ContentCachingResponseWrapper's maximum (" +
					Integer.MAX_VALUE + "): " + len);
		}
		int lenInt = (int) len;
		if (lenInt > this.content.size()) {
			this.content.resize(lenInt);
		}
		this.contentLength = lenInt;
	}

	@Override
	public void setBufferSize(int size) {
		if (size > this.content.size()) {
			this.content.resize(size);
		}
	}

	@Override
	public void resetBuffer() {
		this.content.reset();
	}

	@Override
	public void reset() {
		super.reset();
		this.content.reset();
	}

	/**
	 * Return the status code as specified on the response.
	 * <p>
	 *  返回响应中指定的状态代码
	 * 
	 */
	public int getStatusCode() {
		return this.statusCode;
	}

	/**
	 * Return the cached response content as a byte array.
	 * <p>
	 *  将缓存的响应内容作为字节数组返回
	 * 
	 */
	public byte[] getContentAsByteArray() {
		return this.content.toByteArray();
	}

	/**
	 * Return an {@link InputStream} to the cached content.
	 * <p>
	 *  将{@link InputStream}返回到缓存的内容
	 * 
	 * 
	 * @since 4.2
	 */
	public InputStream getContentInputStream() {
		return this.content.getInputStream();
	}

	/**
	 * Return the current size of the cached content.
	 * <p>
	 *  返回缓存内容的当前大小
	 * 
	 * 
	 * @since 4.2
	 */
	public int getContentSize() {
		return this.content.size();
	}

	/**
	 * Copy the complete cached body content to the response.
	 * <p>
	 *  将完整的缓存的正文内容复制到响应
	 * 
	 * 
	 * @since 4.2
	 */
	public void copyBodyToResponse() throws IOException {
		copyBodyToResponse(true);
	}

	/**
	 * Copy the cached body content to the response.
	 * <p>
	 *  将缓存的正文内容复制到响应
	 * 
	 * @param complete whether to set a corresponding content length
	 * for the complete cached body content
	 * @since 4.2
	 */
	protected void copyBodyToResponse(boolean complete) throws IOException {
		if (this.content.size() > 0) {
			HttpServletResponse rawResponse = (HttpServletResponse) getResponse();
			if ((complete || this.contentLength != null) && !rawResponse.isCommitted()) {
				rawResponse.setContentLength(complete ? this.content.size() : this.contentLength);
				this.contentLength = null;
			}
			this.content.writeTo(rawResponse.getOutputStream());
			this.content.reset();
			if (complete) {
				super.flushBuffer();
			}
		}
	}


	private class ResponseServletOutputStream extends ServletOutputStream {

		@Override
		public void write(int b) throws IOException {
			content.write(b);
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			content.write(b, off, len);
		}
	}


	private class ResponsePrintWriter extends PrintWriter {

		public ResponsePrintWriter(String characterEncoding) throws UnsupportedEncodingException {
			super(new OutputStreamWriter(content, characterEncoding));
		}

		@Override
		public void write(char buf[], int off, int len) {
			super.write(buf, off, len);
			super.flush();
		}

		@Override
		public void write(String s, int off, int len) {
			super.write(s, off, len);
			super.flush();
		}

		@Override
		public void write(int c) {
			super.write(c);
			super.flush();
		}
	}

}
