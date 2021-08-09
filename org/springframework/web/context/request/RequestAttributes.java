/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2013 the original author or authors.
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

/**
 * Abstraction for accessing attribute objects associated with a request.
 * Supports access to request-scoped attributes as well as to session-scoped
 * attributes, with the optional notion of a "global session".
 *
 * <p>Can be implemented for any kind of request/session mechanism,
 * in particular for servlet requests and portlet requests.
 *
 * <p>
 *  用于访问与请求相关联的属性对象的抽象支持对请求范围的属性以及会话范围的属性的访问,可选的概念是"全局会话"
 * 
 * <p>可以实现任何类型的请求/会话机制,特别是对于servlet请求和portlet请求
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.0
 * @see ServletRequestAttributes
 * @see org.springframework.web.portlet.context.PortletRequestAttributes
 */
public interface RequestAttributes {

	/**
	 * Constant that indicates request scope.
	 * <p>
	 *  表示请求范围的常数
	 * 
	 */
	int SCOPE_REQUEST = 0;

	/**
	 * Constant that indicates session scope.
	 * <p>This preferably refers to a locally isolated session, if such
	 * a distinction is available (for example, in a Portlet environment).
	 * Else, it simply refers to the common session.
	 * <p>
	 *  指示会话范围的常量<p>如果这样的区别可用(例如,在Portlet环境中),则这最好是指本地隔离的会话,否则,它仅仅是指公共会话
	 * 
	 */
	int SCOPE_SESSION = 1;

	/**
	 * Constant that indicates global session scope.
	 * <p>This explicitly refers to a globally shared session, if such
	 * a distinction is available (for example, in a Portlet environment).
	 * Else, it simply refers to the common session.
	 * <p>
	 *  指示全局会话范围的常量<p>如果这样的区别可用(例如,在Portlet环境中),则显式地引用全局共享会话,否则,它仅仅是引用通用会话
	 * 
	 */
	int SCOPE_GLOBAL_SESSION = 2;


	/**
	 * Name of the standard reference to the request object: "request".
	 * <p>
	 *  对请求对象的标准引用的名称："请求"
	 * 
	 * 
	 * @see #resolveReference
	 */
	String REFERENCE_REQUEST = "request";

	/**
	 * Name of the standard reference to the session object: "session".
	 * <p>
	 *  会话对象的标准引用的名称："会话"
	 * 
	 * 
	 * @see #resolveReference
	 */
	String REFERENCE_SESSION = "session";


	/**
	 * Return the value for the scoped attribute of the given name, if any.
	 * <p>
	 * 返回给定名称的作用域属性的值(如果有)
	 * 
	 * 
	 * @param name the name of the attribute
	 * @param scope the scope identifier
	 * @return the current attribute value, or {@code null} if not found
	 */
	Object getAttribute(String name, int scope);

	/**
	 * Set the value for the scoped attribute of the given name,
	 * replacing an existing value (if any).
	 * <p>
	 *  设置给定名称的作用域属性的值,替换现有值(如果有)
	 * 
	 * 
	 * @param name the name of the attribute
	 * @param scope the scope identifier
	 * @param value the value for the attribute
	 */
	void setAttribute(String name, Object value, int scope);

	/**
	 * Remove the scoped attribute of the given name, if it exists.
	 * <p>Note that an implementation should also remove a registered destruction
	 * callback for the specified attribute, if any. It does, however, <i>not</i>
	 * need to <i>execute</i> a registered destruction callback in this case,
	 * since the object will be destroyed by the caller (if appropriate).
	 * <p>
	 *  删除给定名称的范围属性(如果存在)<p>请注意,实现还应删除指定属性的注册的销毁回调(如果有),但是,<i>不需要<i在这种情况下,执行</i>注册的销毁回调,因为对象将被调用者销毁(如果适用)
	 * 
	 * 
	 * @param name the name of the attribute
	 * @param scope the scope identifier
	 */
	void removeAttribute(String name, int scope);

	/**
	 * Retrieve the names of all attributes in the scope.
	 * <p>
	 *  检索范围内所有属性的名称
	 * 
	 * 
	 * @param scope the scope identifier
	 * @return the attribute names as String array
	 */
	String[] getAttributeNames(int scope);

	/**
	 * Register a callback to be executed on destruction of the
	 * specified attribute in the given scope.
	 * <p>Implementations should do their best to execute the callback
	 * at the appropriate time: that is, at request completion or session
	 * termination, respectively. If such a callback is not supported by the
	 * underlying runtime environment, the callback <i>must be ignored</i>
	 * and a corresponding warning should be logged.
	 * <p>Note that 'destruction' usually corresponds to destruction of the
	 * entire scope, not to the individual attribute having been explicitly
	 * removed by the application. If an attribute gets removed via this
	 * facade's {@link #removeAttribute(String, int)} method, any registered
	 * destruction callback should be disabled as well, assuming that the
	 * removed object will be reused or manually destroyed.
	 * <p><b>NOTE:</b> Callback objects should generally be serializable if
	 * they are being registered for a session scope. Otherwise the callback
	 * (or even the entire session) might not survive web app restarts.
	 * <p>
	 * 注册要在给定范围内销毁指定属性时执行的回调<p>实现应尽可能在适当的时间执行回调：即在请求完成或会话终止时分别如果这样的回调不是由基础运行时环境支持,回调<i>必须被忽略</i>,并且应该记录相应的警告
	 * <p>请注意,"销毁"通常对应于整个范围的破坏,而不是对单个属性的破坏由应用程序显式删除如果通过此外观的{@link #removeAttribute(String,int)}方法删除了某个属性,那么任
	 * 何已注册的销毁回调也应该被禁用,假设删除的对象将被重新使用或手动销毁<p> <b>注意： </b>如果正在为会话范围注册回调对象通常是可序列化的。
	 * 否则,回调(甚至整个会话)可能无法在Web应用程序重新启动。
	 * 
	 * 
	 * @param name the name of the attribute to register the callback for
	 * @param callback the destruction callback to be executed
	 * @param scope the scope identifier
	 */
	void registerDestructionCallback(String name, Runnable callback, int scope);

	/**
	 * Resolve the contextual reference for the given key, if any.
	 * <p>At a minimum: the HttpServletRequest/PortletRequest reference for key
	 * "request", and the HttpSession/PortletSession reference for key "session".
	 * <p>
	 * 解决给定键的上下文引用(如果有的话)至少：键"请求"的HttpServletRequest / PortletRequest引用,以及关键"会话"的HttpSession / PortletSessio
	 * n引用。
	 * 
	 * 
	 * @param key the contextual key
	 * @return the corresponding object, or {@code null} if none found
	 */
	Object resolveReference(String key);

	/**
	 * Return an id for the current underlying session.
	 * <p>
	 *  返回当前基础会话的ID
	 * 
	 * 
	 * @return the session id as String (never {@code null})
	 */
	String getSessionId();

	/**
	 * Expose the best available mutex for the underlying session:
	 * that is, an object to synchronize on for the underlying session.
	 * <p>
	 *  暴露底层会话的最佳可用互斥体：即,为基础会话同步的对象
	 * 
	 * @return the session mutex to use (never {@code null})
	 */
	Object getSessionMutex();

}
