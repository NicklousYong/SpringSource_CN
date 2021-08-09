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
import java.io.UnsupportedEncodingException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.WebUtils;

/**
 * Base class for {@code Filter}s that perform logging operations before and after a request
 * is processed.
 *
 * <p>Subclasses should override the {@code beforeRequest(HttpServletRequest, String)} and
 * {@code afterRequest(HttpServletRequest, String)} methods to perform the actual logging
 * around the request.
 *
 * <p>Subclasses are passed the message to write to the log in the {@code beforeRequest} and
 * {@code afterRequest} methods. By default, only the URI of the request is logged. However,
 * setting the {@code includeQueryString} property to {@code true} will cause the query string
 * of the request to be included also. The payload (body) of the request can be logged via the
 * {@code includePayload} flag. Note that this will only log that which is read, which might
 * not be the entire payload.
 *
 * <p>Prefixes and suffixes for the before and after messages can be configured using the
 * {@code beforeMessagePrefix}, {@code afterMessagePrefix}, {@code beforeMessageSuffix} and
 * {@code afterMessageSuffix} properties.
 *
 * <p>
 *  在处理请求之前和之后执行日志记录操作的{@code Filter}的基类
 * 
 * <p>子类应该覆盖{@code beforeRequest(HttpServletRequest,String)}和{@code afterRequest(HttpServletRequest,String)}
 * 方法来执行请求的实际日志记录。
 * 
 *  <p>子类传递消息以写入{@code beforeRequest}和{@code afterRequest}方法中的日志默认情况下,只会记录请求的URI但是,将{@code includeQueryString}
 * 属性设置为{ @code true}将导致请求的查询字符串。
 * 请求的payload(body)可以通过{@code includePayload}标志记录注意,这只会记录被读取的内容,这可能不是整个有效载荷。
 * 
 * <p>可以使用{@code beforeMessagePrefix},{@code afterMessagePrefix},{@code beforeMessageSuffix}和{@code afterMessageSuffix}
 * 属性来配置前后消息的前缀和后缀。
 * 
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Rossen Stoyanchev
 * @since 1.2.5
 * @see #beforeRequest
 * @see #afterRequest
 */
public abstract class AbstractRequestLoggingFilter extends OncePerRequestFilter {

	public static final String DEFAULT_BEFORE_MESSAGE_PREFIX = "Before request [";

	public static final String DEFAULT_BEFORE_MESSAGE_SUFFIX = "]";

	public static final String DEFAULT_AFTER_MESSAGE_PREFIX = "After request [";

	public static final String DEFAULT_AFTER_MESSAGE_SUFFIX = "]";

	private static final int DEFAULT_MAX_PAYLOAD_LENGTH = 50;


	private boolean includeQueryString = false;

	private boolean includeClientInfo = false;

	private boolean includeHeaders = false;

	private boolean includePayload = false;

	private int maxPayloadLength = DEFAULT_MAX_PAYLOAD_LENGTH;

	private String beforeMessagePrefix = DEFAULT_BEFORE_MESSAGE_PREFIX;

	private String beforeMessageSuffix = DEFAULT_BEFORE_MESSAGE_SUFFIX;

	private String afterMessagePrefix = DEFAULT_AFTER_MESSAGE_PREFIX;

	private String afterMessageSuffix = DEFAULT_AFTER_MESSAGE_SUFFIX;


	/**
	 * Set whether the query string should be included in the log message.
	 * <p>Should be configured using an {@code <init-param>} for parameter name
	 * "includeQueryString" in the filter definition in {@code web.xml}.
	 * <p>
	 *  设置查询字符串是否应包含在日志消息中<p>应在{@code webxml}的过滤器定义中使用{@code <init-param>}配置参数名称"includeQueryString"
	 * 
	 */
	public void setIncludeQueryString(boolean includeQueryString) {
		this.includeQueryString = includeQueryString;
	}

	/**
	 * Return whether the query string should be included in the log message.
	 * <p>
	 *  返回查询字符串是否应该包含在日志消息中
	 * 
	 */
	protected boolean isIncludeQueryString() {
		return this.includeQueryString;
	}

	/**
	 * Set whether the client address and session id should be included in the
	 * log message.
	 * <p>Should be configured using an {@code <init-param>} for parameter name
	 * "includeClientInfo" in the filter definition in {@code web.xml}.
	 * <p>
	 *  设置客户端地址和会话ID是否应包含在日志消息中<p>应在{@code webxml}中的过滤器定义中使用{@code <init-param>}配置参数名称"includeClientInfo"
	 * 
	 */
	public void setIncludeClientInfo(boolean includeClientInfo) {
		this.includeClientInfo = includeClientInfo;
	}

	/**
	 * Return whether the client address and session id should be included in the
	 * log message.
	 * <p>
	 * 返回客户端地址和会话ID是否应该包含在日志消息中
	 * 
	 */
	protected boolean isIncludeClientInfo() {
		return this.includeClientInfo;
	}

	/**
	 * Set whether the request headers should be included in the log message.
	 * <p>Should be configured using an {@code <init-param>} for parameter name
	 * "includeHeaders" in the filter definition in {@code web.xml}.
	 * <p>
	 *  设置请求标头是否应包含在日志消息中<p>应在{@code webxml}中的过滤器定义中使用参数名称"includeHeaders"的{@code <init-param>}进行配置
	 * 
	 * 
	 * @since 4.3
	 */
	public void setIncludeHeaders(boolean includeHeaders) {
		this.includeHeaders = includeHeaders;
	}

	/**
	 * Return whether the request headers should be included in the log message.
	 * <p>
	 *  返回请求标头是否应该包含在日志消息中
	 * 
	 * 
	 * @since 4.3
	 */
	public boolean isIncludeHeaders() {
		return this.includeHeaders;
	}

	/**
	 * Set whether the request payload (body) should be included in the log message.
	 * <p>Should be configured using an {@code <init-param>} for parameter name
	 * "includePayload" in the filter definition in {@code web.xml}.
	 * <p>
	 *  设置请求有效负载(正文)是否应包含在日志消息中<p>应在{@code webxml}中的过滤器定义中使用{@code <init-param>}配置参数名称"includePayload"
	 * 
	 */
	public void setIncludePayload(boolean includePayload) {
		this.includePayload = includePayload;
	}

	/**
	 * Return whether the request payload (body) should be included in the log message.
	 * <p>
	 *  返回请求有效负载(正文)是否应包含在日志消息中
	 * 
	 */
	protected boolean isIncludePayload() {
		return this.includePayload;
	}

	/**
	 * Sets the maximum length of the payload body to be included in the log message.
	 * Default is 50 characters.
	 * <p>
	 *  设置要包括在日志消息中的有效载荷主体的最大长度默认值为50个字符
	 * 
	 */
	public void setMaxPayloadLength(int maxPayloadLength) {
		Assert.isTrue(maxPayloadLength >= 0, "'maxPayloadLength' should be larger than or equal to 0");
		this.maxPayloadLength = maxPayloadLength;
	}

	/**
	 * Return the maximum length of the payload body to be included in the log message.
	 * <p>
	 * 返回要包括在日志消息中的有效载荷主体的最大长度
	 * 
	 */
	protected int getMaxPayloadLength() {
		return this.maxPayloadLength;
	}

	/**
	 * Set the value that should be prepended to the log message written
	 * <i>before</i> a request is processed.
	 * <p>
	 *  将</i>之前写入的日志消息中应该添加的值设置为处理请求
	 * 
	 */
	public void setBeforeMessagePrefix(String beforeMessagePrefix) {
		this.beforeMessagePrefix = beforeMessagePrefix;
	}

	/**
	 * Set the value that should be appended to the log message written
	 * <i>before</i> a request is processed.
	 * <p>
	 *  设置在</i>处理请求之前写入的日志消息中应附加的值
	 * 
	 */
	public void setBeforeMessageSuffix(String beforeMessageSuffix) {
		this.beforeMessageSuffix = beforeMessageSuffix;
	}

	/**
	 * Set the value that should be prepended to the log message written
	 * <i>after</i> a request is processed.
	 * <p>
	 *  将</i>之后写入的日志消息中应该添加的值设置为处理请求
	 * 
	 */
	public void setAfterMessagePrefix(String afterMessagePrefix) {
		this.afterMessagePrefix = afterMessagePrefix;
	}

	/**
	 * Set the value that should be appended to the log message written
	 * <i>after</i> a request is processed.
	 * <p>
	 *  设置在</i>处理请求后写入的日志消息中应附加的值
	 * 
	 */
	public void setAfterMessageSuffix(String afterMessageSuffix) {
		this.afterMessageSuffix = afterMessageSuffix;
	}


	/**
	 * The default value is "false" so that the filter may log a "before" message
	 * at the start of request processing and an "after" message at the end from
	 * when the last asynchronously dispatched thread is exiting.
	 * <p>
	 *  默认值为"false",以便过滤器可以在请求处理开始时记录"before"消息,并在最后一个异步调度的线程正在退出时从最后一个"after"消息
	 * 
	 */
	@Override
	protected boolean shouldNotFilterAsyncDispatch() {
		return false;
	}

	/**
	 * Forwards the request to the next filter in the chain and delegates down to the subclasses
	 * to perform the actual request logging both before and after the request is processed.
	 * <p>
	 * 将请求转发到链中的下一个过滤器,并将其委托给子类,以在处理请求之前和之后执行实际请求记录
	 * 
	 * 
	 * @see #beforeRequest
	 * @see #afterRequest
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		boolean isFirstRequest = !isAsyncDispatch(request);
		HttpServletRequest requestToUse = request;

		if (isIncludePayload() && isFirstRequest && !(request instanceof ContentCachingRequestWrapper)) {
			requestToUse = new ContentCachingRequestWrapper(request);
		}

		boolean shouldLog = shouldLog(requestToUse);
		if (shouldLog && isFirstRequest) {
			beforeRequest(requestToUse, getBeforeMessage(requestToUse));
		}
		try {
			filterChain.doFilter(requestToUse, response);
		}
		finally {
			if (shouldLog && !isAsyncStarted(requestToUse)) {
				afterRequest(requestToUse, getAfterMessage(requestToUse));
			}
		}
	}

	/**
	 * Get the message to write to the log before the request.
	 * <p>
	 *  在请求之前获取写入日志的消息
	 * 
	 * 
	 * @see #createMessage
	 */
	private String getBeforeMessage(HttpServletRequest request) {
		return createMessage(request, this.beforeMessagePrefix, this.beforeMessageSuffix);
	}

	/**
	 * Get the message to write to the log after the request.
	 * <p>
	 *  在请求之后获取写入日志的消息
	 * 
	 * 
	 * @see #createMessage
	 */
	private String getAfterMessage(HttpServletRequest request) {
		return createMessage(request, this.afterMessagePrefix, this.afterMessageSuffix);
	}

	/**
	 * Create a log message for the given request, prefix and suffix.
	 * <p>If {@code includeQueryString} is {@code true}, then the inner part
	 * of the log message will take the form {@code request_uri?query_string};
	 * otherwise the message will simply be of the form {@code request_uri}.
	 * <p>The final message is composed of the inner part as described and
	 * the supplied prefix and suffix.
	 * <p>
	 *  创建给定请求的日志消息,前缀和后缀<p>如果{@code includeQueryString}是{@code true},则日志消息的内部部分将采用{@code request_uri?query_string}
	 * 的形式;否则该消息将简单地形式为{@code request_uri} <p>最终消息由所描述的内部部分组成,并且提供的前缀和后缀。
	 * 
	 */
	protected String createMessage(HttpServletRequest request, String prefix, String suffix) {
		StringBuilder msg = new StringBuilder();
		msg.append(prefix);
		msg.append("uri=").append(request.getRequestURI());

		if (isIncludeQueryString()) {
			String queryString = request.getQueryString();
			if (queryString != null) {
				msg.append('?').append(queryString);
			}
		}

		if (isIncludeClientInfo()) {
			String client = request.getRemoteAddr();
			if (StringUtils.hasLength(client)) {
				msg.append(";client=").append(client);
			}
			HttpSession session = request.getSession(false);
			if (session != null) {
				msg.append(";session=").append(session.getId());
			}
			String user = request.getRemoteUser();
			if (user != null) {
				msg.append(";user=").append(user);
			}
		}

		if (isIncludeHeaders()) {
			msg.append(";headers=").append(new ServletServerHttpRequest(request).getHeaders());
		}

		if (isIncludePayload()) {
			ContentCachingRequestWrapper wrapper =
					WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
			if (wrapper != null) {
				byte[] buf = wrapper.getContentAsByteArray();
				if (buf.length > 0) {
					int length = Math.min(buf.length, getMaxPayloadLength());
					String payload;
					try {
						payload = new String(buf, 0, length, wrapper.getCharacterEncoding());
					}
					catch (UnsupportedEncodingException ex) {
						payload = "[unknown]";
					}
					msg.append(";payload=").append(payload);
				}
			}
		}

		msg.append(suffix);
		return msg.toString();
	}


	/**
	 * Determine whether to call the {@link #beforeRequest}/{@link #afterRequest}
	 * methods for the current request, i.e. whether logging is currently active
	 * (and the log message is worth building).
	 * <p>The default implementation always returns {@code true}. Subclasses may
	 * override this with a log level check.
	 * <p>
	 * 确定是否为当前请求调用{@link #beforeRequest} / {@ link #afterRequest}方法,即日志记录当前是否处于活动状态(并且日志消息值得构建)<p>默认实现始终返回{@code true}
	 * 子类可以使用日志级别检查覆盖此类。
	 * 
	 * 
	 * @param request current HTTP request
	 * @return {@code true} if the before/after method should get called;
	 * {@code false} otherwise
	 * @since 4.1.5
	 */
	protected boolean shouldLog(HttpServletRequest request) {
		return true;
	}

	/**
	 * Concrete subclasses should implement this method to write a log message
	 * <i>before</i> the request is processed.
	 * <p>
	 *  具体的子类应该实现这个方法,在</i>请求被处理之前编写一个日志消息<i>
	 * 
	 * 
	 * @param request current HTTP request
	 * @param message the message to log
	 */
	protected abstract void beforeRequest(HttpServletRequest request, String message);

	/**
	 * Concrete subclasses should implement this method to write a log message
	 * <i>after</i> the request is processed.
	 * <p>
	 *  具体的子类应该实现这个方法,在</i>处理请求后写入日志消息<i>
	 * 
	 * @param request current HTTP request
	 * @param message the message to log
	 */
	protected abstract void afterRequest(HttpServletRequest request, String message);

}
