/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2008 the original author or authors.
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

import org.apache.log4j.Logger;
import org.apache.log4j.NDC;

/**
 * Request logging filter that adds the request log message to the Log4J
 * nested diagnostic context (NDC) before the request is processed,
 * removing it again after the request is processed.
 *
 * <p>
 *  请求记录过滤器,在请求被处理之前将请求日志消息添加到Log4J嵌套诊断上下文(NDC)中,在请求被处理之后再次删除它
 * 
 * 
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 1.2.5
 * @see #setIncludeQueryString
 * @see #setBeforeMessagePrefix
 * @see #setBeforeMessageSuffix
 * @see #setAfterMessagePrefix
 * @see #setAfterMessageSuffix
 * @see org.apache.log4j.NDC#push(String)
 * @see org.apache.log4j.NDC#pop()
 * @deprecated as of Spring 4.2.1, in favor of Apache Log4j 2
 * (following Apache's EOL declaration for log4j 1.x)
 */
@Deprecated
public class Log4jNestedDiagnosticContextFilter extends AbstractRequestLoggingFilter {

	/** Logger available to subclasses */
	protected final Logger log4jLogger = Logger.getLogger(getClass());


	/**
	 * Logs the before-request message through Log4J and
	 * adds a message the Log4J NDC before the request is processed.
	 * <p>
	 * 通过Log4J记录请求前的消息,并在处理请求之前添加一条消息Log4J NDC
	 * 
	 */
	@Override
	protected void beforeRequest(HttpServletRequest request, String message) {
		if (log4jLogger.isDebugEnabled()) {
			log4jLogger.debug(message);
		}
		NDC.push(getNestedDiagnosticContextMessage(request));
	}

	/**
	 * Determine the message to be pushed onto the Log4J nested diagnostic context.
	 * <p>Default is a plain request log message without prefix or suffix.
	 * <p>
	 *  确定要推送到Log4J嵌套诊断上下文的消息<p>默认是没有前缀或后缀的简单请求日志消息
	 * 
	 * 
	 * @param request current HTTP request
	 * @return the message to be pushed onto the Log4J NDC
	 * @see #createMessage
	 */
	protected String getNestedDiagnosticContextMessage(HttpServletRequest request) {
		return createMessage(request, "", "");
	}

	/**
	 * Removes the log message from the Log4J NDC after the request is processed
	 * and logs the after-request message through Log4J.
	 * <p>
	 *  在处理请求后从Log4J NDC中删除日志消息,并通过Log4J记录请求后消息
	 */
	@Override
	protected void afterRequest(HttpServletRequest request, String message) {
		NDC.pop();
		if (NDC.getDepth() == 0) {
			NDC.remove();
		}
		if (log4jLogger.isDebugEnabled()) {
			log4jLogger.debug(message);
		}
	}

}
