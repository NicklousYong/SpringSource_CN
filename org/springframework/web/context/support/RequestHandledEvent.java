/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2012 the original author or authors.
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

package org.springframework.web.context.support;

import org.springframework.context.ApplicationEvent;

/**
 * Event raised when a request is handled within an ApplicationContext.
 *
 * <p>Supported by Spring's own FrameworkServlet (through a specific
 * ServletRequestHandledEvent subclass), but can also be raised by any
 * other web component. Used, for example, by Spring's out-of-the-box
 * PerformanceMonitorListener.
 *
 * <p>
 *  在ApplicationContext中处理请求时引发的事件
 * 
 * <p>由Spring自己的FrameworkServlet(通过特定的ServletRequestHandledEvent子类)支持,但也可以由任何其他Web组件引发使用,例如,通过Spring的开箱即
 * 用的PerformanceMonitorListener。
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since January 17, 2001
 * @see ServletRequestHandledEvent
 * @see org.springframework.web.servlet.FrameworkServlet
 * @see org.springframework.context.ApplicationContext#publishEvent
 */
@SuppressWarnings("serial")
public class RequestHandledEvent extends ApplicationEvent {

	/** Session id that applied to the request, if any */
	private String sessionId;

	/** Usually the UserPrincipal */
	private String userName;

	/** Request processing time */
	private final long processingTimeMillis;

	/** Cause of failure, if any */
	private Throwable failureCause;


	/**
	 * Create a new RequestHandledEvent with session information.
	 * <p>
	 *  使用会话信息创建一个新的RequestHandledEvent
	 * 
	 * 
	 * @param source the component that published the event
	 * @param sessionId the id of the HTTP session, if any
	 * @param userName the name of the user that was associated with the
	 * request, if any (usually the UserPrincipal)
	 * @param processingTimeMillis the processing time of the request in milliseconds
	 */
	public RequestHandledEvent(Object source, String sessionId, String userName, long processingTimeMillis) {
		super(source);
		this.sessionId = sessionId;
		this.userName = userName;
		this.processingTimeMillis = processingTimeMillis;
	}

	/**
	 * Create a new RequestHandledEvent with session information.
	 * <p>
	 *  使用会话信息创建一个新的RequestHandledEvent
	 * 
	 * 
	 * @param source the component that published the event
	 * @param sessionId the id of the HTTP session, if any
	 * @param userName the name of the user that was associated with the
	 * request, if any (usually the UserPrincipal)
	 * @param processingTimeMillis the processing time of the request in milliseconds
	 * @param failureCause the cause of failure, if any
	 */
	public RequestHandledEvent(
			Object source, String sessionId, String userName, long processingTimeMillis, Throwable failureCause) {

		this(source, sessionId, userName, processingTimeMillis);
		this.failureCause = failureCause;
	}


	/**
	 * Return the processing time of the request in milliseconds.
	 * <p>
	 *  以毫秒为单位返回请求的处理时间
	 * 
	 */
	public long getProcessingTimeMillis() {
		return this.processingTimeMillis;
	}

	/**
	 * Return the id of the HTTP session, if any.
	 * <p>
	 *  返回HTTP会话的ID(如果有)
	 * 
	 */
	public String getSessionId() {
		return this.sessionId;
	}

	/**
	 * Return the name of the user that was associated with the request
	 * (usually the UserPrincipal).
	 * <p>
	 *  返回与请求相关联的用户的名称(通常是UserPrincipal)
	 * 
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getUserPrincipal()
	 */
	public String getUserName() {
		return this.userName;
	}

	/**
	 * Return whether the request failed.
	 * <p>
	 *  返回请求是否失败
	 * 
	 */
	public boolean wasFailure() {
		return (this.failureCause != null);
	}

	/**
	 * Return the cause of failure, if any.
	 * <p>
	 *  返回失败的原因,如果有的话
	 * 
	 */
	public Throwable getFailureCause() {
		return this.failureCause;
	}


	/**
	 * Return a short description of this event, only involving
	 * the most important context data.
	 * <p>
	 *  返回此事件的简短描述,仅涉及最重要的上下文数据
	 * 
	 */
	public String getShortDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append("session=[").append(this.sessionId).append("]; ");
		sb.append("user=[").append(this.userName).append("]; ");
		return sb.toString();
	}

	/**
	 * Return a full description of this event, involving
	 * all available context data.
	 * <p>
	 *  返回此事件的完整描述,涉及所有可用的上下文数据
	 */
	public String getDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append("session=[").append(this.sessionId).append("]; ");
		sb.append("user=[").append(this.userName).append("]; ");
		sb.append("time=[").append(this.processingTimeMillis).append("ms]; ");
		sb.append("status=[");
		if (!wasFailure()) {
			sb.append("OK");
		}
		else {
			sb.append("failed: ").append(this.failureCause);
		}
		sb.append(']');
		return sb.toString();
	}

	@Override
	public String toString() {
		return ("RequestHandledEvent: " + getDescription());
	}

}
