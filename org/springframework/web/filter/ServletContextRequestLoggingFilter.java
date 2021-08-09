/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2005 the original author or authors.
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

import javax.servlet.http.HttpServletRequest;

/**
 * Simple request logging filter that writes the request URI
 * (and optionally the query string) to the ServletContext log.
 *
 * <p>
 *  将请求URI(和可选的查询字符串)写入ServletContext日志的简单请求记录筛选器
 * 
 * 
 * @author Juergen Hoeller
 * @since 1.2.5
 * @see #setIncludeQueryString
 * @see #setBeforeMessagePrefix
 * @see #setBeforeMessageSuffix
 * @see #setAfterMessagePrefix
 * @see #setAfterMessageSuffix
 * @see javax.servlet.ServletContext#log(String)
 */
public class ServletContextRequestLoggingFilter extends AbstractRequestLoggingFilter {

	/**
	 * Writes a log message before the request is processed.
	 * <p>
	 *  在处理请求之前写入日志消息
	 * 
	 */
	@Override
	protected void beforeRequest(HttpServletRequest request, String message) {
		getServletContext().log(message);
	}

	/**
	 * Writes a log message after the request is processed.
	 * <p>
	 * 处理请求后写入日志消息
	 */
	@Override
	protected void afterRequest(HttpServletRequest request, String message) {
		getServletContext().log(message);
	}

}
