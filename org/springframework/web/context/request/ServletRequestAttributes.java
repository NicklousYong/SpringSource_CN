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

package org.springframework.web.context.request;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.util.Assert;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

/**
 * Servlet-based implementation of the {@link RequestAttributes} interface.
 *
 * <p>Accesses objects from servlet request and HTTP session scope,
 * with no distinction between "session" and "global session".
 *
 * <p>
 *  基于Servlet的{@link RequestAttributes}接口的实现
 * 
 *  <p>从servlet请求和HTTP会话范围访问对象,而不区分"会话"和"全局会话"
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.0
 * @see javax.servlet.ServletRequest#getAttribute
 * @see javax.servlet.http.HttpSession#getAttribute
 */
public class ServletRequestAttributes extends AbstractRequestAttributes {

	/**
	 * Constant identifying the {@link String} prefixed to the name of a
	 * destruction callback when it is stored in a {@link HttpSession}.
	 * <p>
	 * 当{@link HttpSession}存储在{@link HttpSession}中时,会将{@link String}标识为销毁回调名称的前缀
	 * 
	 */
	public static final String DESTRUCTION_CALLBACK_NAME_PREFIX =
			ServletRequestAttributes.class.getName() + ".DESTRUCTION_CALLBACK.";

	protected static final Set<Class<?>> immutableValueTypes = new HashSet<Class<?>>(16);

	static {
		immutableValueTypes.addAll(NumberUtils.STANDARD_NUMBER_TYPES);
		immutableValueTypes.add(Boolean.class);
		immutableValueTypes.add(Character.class);
		immutableValueTypes.add(String.class);
	}


	private final HttpServletRequest request;

	private HttpServletResponse response;

	private volatile HttpSession session;

	private final Map<String, Object> sessionAttributesToUpdate = new ConcurrentHashMap<String, Object>(1);


	/**
	 * Create a new ServletRequestAttributes instance for the given request.
	 * <p>
	 *  为给定的请求创建一个新的ServletRequestAttributes实例
	 * 
	 * 
	 * @param request current HTTP request
	 */
	public ServletRequestAttributes(HttpServletRequest request) {
		Assert.notNull(request, "Request must not be null");
		this.request = request;
	}

	/**
	 * Create a new ServletRequestAttributes instance for the given request.
	 * <p>
	 *  为给定的请求创建一个新的ServletRequestAttributes实例
	 * 
	 * 
	 * @param request current HTTP request
	 * @param response current HTTP response (for optional exposure)
	 */
	public ServletRequestAttributes(HttpServletRequest request, HttpServletResponse response) {
		this(request);
		this.response = response;
	}


	/**
	 * Exposes the native {@link HttpServletRequest} that we're wrapping.
	 * <p>
	 *  暴露我们正在包装的本机{@link HttpServletRequest}
	 * 
	 */
	public final HttpServletRequest getRequest() {
		return this.request;
	}

	/**
	 * Exposes the native {@link HttpServletResponse} that we're wrapping (if any).
	 * <p>
	 *  暴露我们正在包装的本机{@link HttpServletResponse}(如果有的话)
	 * 
	 */
	public final HttpServletResponse getResponse() {
		return this.response;
	}

	/**
	 * Exposes the {@link HttpSession} that we're wrapping.
	 * <p>
	 *  暴露我们正在包装的{@link HttpSession}
	 * 
	 * 
	 * @param allowCreate whether to allow creation of a new session if none exists yet
	 */
	protected final HttpSession getSession(boolean allowCreate) {
		if (isRequestActive()) {
			HttpSession session = this.request.getSession(allowCreate);
			this.session = session;
			return session;
		}
		else {
			// Access through stored session reference, if any...
			HttpSession session = this.session;
			if (session == null) {
				if (allowCreate) {
					throw new IllegalStateException(
							"No session found and request already completed - cannot create new session!");
				}
				else {
					session = this.request.getSession(false);
					this.session = session;
				}
			}
			return session;
		}
	}


	@Override
	public Object getAttribute(String name, int scope) {
		if (scope == SCOPE_REQUEST) {
			if (!isRequestActive()) {
				throw new IllegalStateException(
						"Cannot ask for request attribute - request is not active anymore!");
			}
			return this.request.getAttribute(name);
		}
		else {
			HttpSession session = getSession(false);
			if (session != null) {
				try {
					Object value = session.getAttribute(name);
					if (value != null) {
						this.sessionAttributesToUpdate.put(name, value);
					}
					return value;
				}
				catch (IllegalStateException ex) {
					// Session invalidated - shouldn't usually happen.
				}
			}
			return null;
		}
	}

	@Override
	public void setAttribute(String name, Object value, int scope) {
		if (scope == SCOPE_REQUEST) {
			if (!isRequestActive()) {
				throw new IllegalStateException(
						"Cannot set request attribute - request is not active anymore!");
			}
			this.request.setAttribute(name, value);
		}
		else {
			HttpSession session = getSession(true);
			this.sessionAttributesToUpdate.remove(name);
			session.setAttribute(name, value);
		}
	}

	@Override
	public void removeAttribute(String name, int scope) {
		if (scope == SCOPE_REQUEST) {
			if (isRequestActive()) {
				this.request.removeAttribute(name);
				removeRequestDestructionCallback(name);
			}
		}
		else {
			HttpSession session = getSession(false);
			if (session != null) {
				this.sessionAttributesToUpdate.remove(name);
				try {
					session.removeAttribute(name);
					// Remove any registered destruction callback as well.
					session.removeAttribute(DESTRUCTION_CALLBACK_NAME_PREFIX + name);
				}
				catch (IllegalStateException ex) {
					// Session invalidated - shouldn't usually happen.
				}
			}
		}
	}

	@Override
	public String[] getAttributeNames(int scope) {
		if (scope == SCOPE_REQUEST) {
			if (!isRequestActive()) {
				throw new IllegalStateException(
						"Cannot ask for request attributes - request is not active anymore!");
			}
			return StringUtils.toStringArray(this.request.getAttributeNames());
		}
		else {
			HttpSession session = getSession(false);
			if (session != null) {
				try {
					return StringUtils.toStringArray(session.getAttributeNames());
				}
				catch (IllegalStateException ex) {
					// Session invalidated - shouldn't usually happen.
				}
			}
			return new String[0];
		}
	}

	@Override
	public void registerDestructionCallback(String name, Runnable callback, int scope) {
		if (scope == SCOPE_REQUEST) {
			registerRequestDestructionCallback(name, callback);
		}
		else {
			registerSessionDestructionCallback(name, callback);
		}
	}

	@Override
	public Object resolveReference(String key) {
		if (REFERENCE_REQUEST.equals(key)) {
			return this.request;
		}
		else if (REFERENCE_SESSION.equals(key)) {
			return getSession(true);
		}
		else {
			return null;
		}
	}

	@Override
	public String getSessionId() {
		return getSession(true).getId();
	}

	@Override
	public Object getSessionMutex() {
		return WebUtils.getSessionMutex(getSession(true));
	}


	/**
	 * Update all accessed session attributes through {@code session.setAttribute}
	 * calls, explicitly indicating to the container that they might have been modified.
	 * <p>
	 *  通过{@code sessionsetAttribute}调用更新所有访问的会话属性,向容器明确指出可能已被修改
	 * 
	 */
	@Override
	protected void updateAccessedSessionAttributes() {
		if (!this.sessionAttributesToUpdate.isEmpty()) {
			// Update all affected session attributes.
			HttpSession session = getSession(false);
			if (session != null) {
				try {
					for (Map.Entry<String, Object> entry : this.sessionAttributesToUpdate.entrySet()) {
						String name = entry.getKey();
						Object newValue = entry.getValue();
						Object oldValue = session.getAttribute(name);
						if (oldValue == newValue && !isImmutableSessionAttribute(name, newValue)) {
							session.setAttribute(name, newValue);
						}
					}
				}
				catch (IllegalStateException ex) {
					// Session invalidated - shouldn't usually happen.
				}
			}
			this.sessionAttributesToUpdate.clear();
		}
	}

	/**
	 * Determine whether the given value is to be considered as an immutable session
	 * attribute, that is, doesn't have to be re-set via {@code session.setAttribute}
	 * since its value cannot meaningfully change internally.
	 * <p>The default implementation returns {@code true} for {@code String},
	 * {@code Character}, {@code Boolean} and standard {@code Number} values.
	 * <p>
	 * 确定给定的值是否被认为是不可变的会话属性,也就是说,不必通过{@code sessionsetAttribute}重新设置,因为它的值不能在内部有意义地改变<p>默认的实现返回{@code {@code String}
	 * ,{@code Character},{@code Boolean}和标准{@code Number}值的true}。
	 * 
	 * 
	 * @param name the name of the attribute
	 * @param value the corresponding value to check
	 * @return {@code true} if the value is to be considered as immutable for the
	 * purposes of session attribute management; {@code false} otherwise
	 * @see #updateAccessedSessionAttributes()
	 */
	protected boolean isImmutableSessionAttribute(String name, Object value) {
		return (value == null || immutableValueTypes.contains(value.getClass()));
	}

	/**
	 * Register the given callback as to be executed after session termination.
	 * <p>Note: The callback object should be serializable in order to survive
	 * web app restarts.
	 * <p>
	 * 
	 * @param name the name of the attribute to register the callback for
	 * @param callback the callback to be executed for destruction
	 */
	protected void registerSessionDestructionCallback(String name, Runnable callback) {
		HttpSession session = getSession(true);
		session.setAttribute(DESTRUCTION_CALLBACK_NAME_PREFIX + name,
				new DestructionCallbackBindingListener(callback));
	}


	@Override
	public String toString() {
		return this.request.toString();
	}

}
