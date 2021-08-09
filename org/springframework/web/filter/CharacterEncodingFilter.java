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

package org.springframework.web.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.Assert;

/**
 * Servlet Filter that allows one to specify a character encoding for requests.
 * This is useful because current browsers typically do not set a character
 * encoding even if specified in the HTML page or form.
 *
 * <p>This filter can either apply its encoding if the request does not already
 * specify an encoding, or enforce this filter's encoding in any case
 * ("forceEncoding"="true"). In the latter case, the encoding will also be
 * applied as default response encoding (although this will usually be overridden
 * by a full content type set in the view).
 *
 * <p>
 *  Servlet过滤器,允许为请求指定字符编码这是有用的,因为当前浏览器通常不设置字符编码,即使在HTML页面或表单中指定
 * 
 * <p>如果请求尚未指定编码,则此过滤器可以应用其编码,或者在任何情况下强制执行此过滤器的编码("forceEncoding"="true")在后一种情况下,编码也将作为默认值应用响应编码(虽然这通常会被
 * 视图中设置的完整内容类型覆盖)。
 * 
 * 
 * @author Juergen Hoeller
 * @since 15.03.2004
 * @see #setEncoding
 * @see #setForceEncoding
 * @see javax.servlet.http.HttpServletRequest#setCharacterEncoding
 * @see javax.servlet.http.HttpServletResponse#setCharacterEncoding
 */
public class CharacterEncodingFilter extends OncePerRequestFilter {

	private String encoding;

	private boolean forceRequestEncoding = false;

	private boolean forceResponseEncoding = false;


	/**
	 * Create a default {@code CharacterEncodingFilter},
	 * with the encoding to be set via {@link #setEncoding}.
	 * <p>
	 *  创建一个默认的{@code CharacterEncodingFilter},其编码将通过{@link #setEncoding}设置
	 * 
	 * 
	 * @see #setEncoding
	 */
	public CharacterEncodingFilter() {
	}

	/**
	 * Create a {@code CharacterEncodingFilter} for the given encoding.
	 * <p>
	 *  为给定的编码创建一个{@code CharacterEncodingFilter}
	 * 
	 * 
	 * @param encoding the encoding to apply
	 * @since 4.2.3
	 * @see #setEncoding
	 */
	public CharacterEncodingFilter(String encoding) {
		this(encoding, false);
	}

	/**
	 * Create a {@code CharacterEncodingFilter} for the given encoding.
	 * <p>
	 *  为给定的编码创建一个{@code CharacterEncodingFilter}
	 * 
	 * 
	 * @param encoding the encoding to apply
	 * @param forceEncoding whether the specified encoding is supposed to
	 * override existing request and response encodings
	 * @since 4.2.3
	 * @see #setEncoding
	 * @see #setForceEncoding
	 */
	public CharacterEncodingFilter(String encoding, boolean forceEncoding) {
		this(encoding, forceEncoding, forceEncoding);
	}

	/**
	 * Create a {@code CharacterEncodingFilter} for the given encoding.
	 * <p>
	 *  为给定的编码创建一个{@code CharacterEncodingFilter}
	 * 
	 * 
	 * @param encoding the encoding to apply
	 * @param forceRequestEncoding whether the specified encoding is supposed to
	 * override existing request encodings
	 * @param forceResponseEncoding whether the specified encoding is supposed to
	 * override existing response encodings
	 * @since 4.3
	 * @see #setEncoding
	 * @see #setForceRequestEncoding(boolean)
	 * @see #setForceResponseEncoding(boolean)
	 */
	public CharacterEncodingFilter(String encoding, boolean forceRequestEncoding, boolean forceResponseEncoding) {
		Assert.hasLength(encoding, "Encoding must not be empty");
		this.encoding = encoding;
		this.forceRequestEncoding = forceRequestEncoding;
		this.forceResponseEncoding = forceResponseEncoding;
	}


	/**
	 * Set the encoding to use for requests. This encoding will be passed into a
	 * {@link javax.servlet.http.HttpServletRequest#setCharacterEncoding} call.
	 * <p>Whether this encoding will override existing request encodings
	 * (and whether it will be applied as default response encoding as well)
	 * depends on the {@link #setForceEncoding "forceEncoding"} flag.
	 * <p>
	 * 设置用于请求的编码此编码将被传递到{@link javaxservlethttpHttpServletRequest#setCharacterEncoding}调用<p>此编码是否将覆盖现有的请求编码(
	 * 以及是否将其应用为默认响应编码)取决于{@link #setForceEncoding"forceEncoding"}标志。
	 * 
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * Return the configured encoding for requests and/or responses
	 * <p>
	 *  返回配置的请求和/或响应的编码
	 * 
	 * 
	 * @since 4.3
	 */
	public String getEncoding() {
		return this.encoding;
	}

	/**
	 * Set whether the configured {@link #setEncoding encoding} of this filter
	 * is supposed to override existing request and response encodings.
	 * <p>Default is "false", i.e. do not modify the encoding if
	 * {@link javax.servlet.http.HttpServletRequest#getCharacterEncoding()}
	 * returns a non-null value. Switch this to "true" to enforce the specified
	 * encoding in any case, applying it as default response encoding as well.
	 * <p>This is the equivalent to setting both {@link #setForceRequestEncoding(boolean)}
	 * and {@link #setForceResponseEncoding(boolean)}.
	 * <p>
	 * 设置此过滤器的配置{@link #setEncoding encoding}是否应该覆盖现有的请求和响应编码<p>默认值为"false",即如果{@link javaxservlethttpHttpServletRequest#getCharacterEncoding())返回一个值,则不修改编码非空值将其切换为"true"以在任何情况下强制执行指定的编码,将其应用为默认响应编码<p>这等同于设置{@link #setForceRequestEncoding(boolean)}
	 * 和{@link #setForceResponseEncoding(布尔值)}。
	 * 
	 * 
	 * @see #setForceRequestEncoding(boolean)
	 * @see #setForceResponseEncoding(boolean)
	 */
	public void setForceEncoding(boolean forceEncoding) {
		this.forceRequestEncoding = forceEncoding;
		this.forceResponseEncoding = forceEncoding;
	}

	/**
	 * Set whether the configured {@link #setEncoding encoding} of this filter
	 * is supposed to override existing request encodings.
	 * <p>Default is "false", i.e. do not modify the encoding if
	 * {@link javax.servlet.http.HttpServletRequest#getCharacterEncoding()}
	 * returns a non-null value. Switch this to "true" to enforce the specified
	 * encoding in any case.
	 * <p>
	 * 设置此过滤器的配置{@link #setEncoding encoding}是否应该覆盖现有的请求编码<p>默认值为"false",即如果{@link javaxservlethttpHttpServletRequest#getCharacterEncoding())返回非空值,则不修改编码, null值将其切换为"true"以强制执行指定的编码。
	 * 
	 * 
	 * @since 4.3
	 */
	public void setForceRequestEncoding(boolean forceRequestEncoding) {
		this.forceRequestEncoding = forceRequestEncoding;
	}

	/**
	 * Return whether the encoding should be forced on requests
	 * <p>
	 *  返回是否应该强制要求编码
	 * 
	 * 
	 * @since 4.3
	 */
	public boolean isForceRequestEncoding() {
		return this.forceRequestEncoding;
	}

	/**
	 * Set whether the configured {@link #setEncoding encoding} of this filter
	 * is supposed to override existing response encodings.
	 * <p>Default is "false", i.e. do not modify the encoding.
	 * Switch this to "true" to enforce the specified encoding
	 * for responses in any case.
	 * <p>
	 *  设置此过滤器的配置{@link #setEncoding encoding}是否应该覆盖现有的响应编码<p>默认值为"false",即不要修改编码将其切换为"true",以强制执行指定的响应编码任何情
	 * 况。
	 * 
	 * 
	 * @since 4.3
	 */
	public void setForceResponseEncoding(boolean forceResponseEncoding) {
		this.forceResponseEncoding = forceResponseEncoding;
	}

	/**
	 * Return whether the encoding should be forced on responses.
	 * <p>
	 * 
	 * @since 4.3
	 */
	public boolean isForceResponseEncoding() {
		return this.forceResponseEncoding;
	}


	@Override
	protected void doFilterInternal(
			HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String encoding = getEncoding();
		if (encoding != null) {
			if (isForceRequestEncoding() || request.getCharacterEncoding() == null) {
				request.setCharacterEncoding(encoding);
			}
			if (isForceResponseEncoding()) {
				response.setCharacterEncoding(encoding);
			}
		}
		filterChain.doFilter(request, response);
	}

}
