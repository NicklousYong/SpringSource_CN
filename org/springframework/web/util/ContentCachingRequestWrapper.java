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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.springframework.http.HttpMethod;

/**
 * {@link javax.servlet.http.HttpServletRequest} wrapper that caches all content read from
 * the {@linkplain #getInputStream() input stream} and {@linkplain #getReader() reader},
 * and allows this content to be retrieved via a {@link #getContentAsByteArray() byte array}.
 *
 * <p>Used e.g. by {@link org.springframework.web.filter.AbstractRequestLoggingFilter}.
 *
 * <p>
 * {@link javaxservlethttpHttpServletRequest}包装器,用于缓存从{@linkplain #getInputStream()输入流}和{@linkplain #getReader()reader}
 * 读取的所有内容,并允许通过{@link #getContentAsByteArray()字节数组}。
 * 
 *  <p>例如通过{@link orgspringframeworkwebfilterAbstractRequestLoggingFilter}
 * 
 * 
 * @author Juergen Hoeller
 * @author Brian Clozel
 * @since 4.1.3
 * @see ContentCachingResponseWrapper
 */
public class ContentCachingRequestWrapper extends HttpServletRequestWrapper {

	private static final String FORM_CONTENT_TYPE = "application/x-www-form-urlencoded";


	private final ByteArrayOutputStream cachedContent;

	private ServletInputStream inputStream;

	private BufferedReader reader;


	/**
	 * Create a new ContentCachingRequestWrapper for the given servlet request.
	 * <p>
	 *  为给定的servlet请求创建一个新的ContentCachingRequestWrapper
	 * 
	 * 
	 * @param request the original servlet request
	 */
	public ContentCachingRequestWrapper(HttpServletRequest request) {
		super(request);
		int contentLength = request.getContentLength();
		this.cachedContent = new ByteArrayOutputStream(contentLength >= 0 ? contentLength : 1024);
	}


	@Override
	public ServletInputStream getInputStream() throws IOException {
		if (this.inputStream == null) {
			this.inputStream = new ContentCachingInputStream(getRequest().getInputStream());
		}
		return this.inputStream;
	}

	@Override
	public String getCharacterEncoding() {
		String enc = super.getCharacterEncoding();
		return (enc != null ? enc : WebUtils.DEFAULT_CHARACTER_ENCODING);
	}

	@Override
	public BufferedReader getReader() throws IOException {
		if (this.reader == null) {
			this.reader = new BufferedReader(new InputStreamReader(getInputStream(), getCharacterEncoding()));
		}
		return this.reader;
	}

	@Override
	public String getParameter(String name) {
		if (this.cachedContent.size() == 0 && isFormPost()) {
			writeRequestParametersToCachedContent();
		}
		return super.getParameter(name);
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		if (this.cachedContent.size() == 0 && isFormPost()) {
			writeRequestParametersToCachedContent();
		}
		return super.getParameterMap();
	}

	@Override
	public Enumeration<String> getParameterNames() {
		if (this.cachedContent.size() == 0 && isFormPost()) {
			writeRequestParametersToCachedContent();
		}
		return super.getParameterNames();
	}

	@Override
	public String[] getParameterValues(String name) {
		if (this.cachedContent.size() == 0 && isFormPost()) {
			writeRequestParametersToCachedContent();
		}
		return super.getParameterValues(name);
	}


	private boolean isFormPost() {
		String contentType = getContentType();
		return (contentType != null && contentType.contains(FORM_CONTENT_TYPE) &&
				HttpMethod.POST.matches(getMethod()));
	}

	private void writeRequestParametersToCachedContent() {
		try {
			if (this.cachedContent.size() == 0) {
				String requestEncoding = getCharacterEncoding();
				Map<String, String[]> form = super.getParameterMap();
				for (Iterator<String> nameIterator = form.keySet().iterator(); nameIterator.hasNext(); ) {
					String name = nameIterator.next();
					List<String> values = Arrays.asList(form.get(name));
					for (Iterator<String> valueIterator = values.iterator(); valueIterator.hasNext(); ) {
						String value = valueIterator.next();
						this.cachedContent.write(URLEncoder.encode(name, requestEncoding).getBytes());
						if (value != null) {
							this.cachedContent.write('=');
							this.cachedContent.write(URLEncoder.encode(value, requestEncoding).getBytes());
							if (valueIterator.hasNext()) {
								this.cachedContent.write('&');
							}
						}
					}
					if (nameIterator.hasNext()) {
						this.cachedContent.write('&');
					}
				}
			}
		}
		catch (IOException ex) {
			throw new IllegalStateException("Failed to write request parameters to cached content", ex);
		}
	}

	/**
	 * Return the cached request content as a byte array.
	 * <p>
	 *  将缓存的请求内容作为字节数组返回
	 */
	public byte[] getContentAsByteArray() {
		return this.cachedContent.toByteArray();
	}


	private class ContentCachingInputStream extends ServletInputStream {

		private final ServletInputStream is;

		public ContentCachingInputStream(ServletInputStream is) {
			this.is = is;
		}

		@Override
		public int read() throws IOException {
			int ch = this.is.read();
			if (ch != -1) {
				cachedContent.write(ch);
			}
			return ch;
		}
	}

}
