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

package org.springframework.web.servlet.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.WebContentGenerator;
import org.springframework.web.util.WebUtils;

/**
 * Convenient superclass for controller implementations, using the Template Method
 * design pattern.
 *
 * <p><b><a name="workflow">Workflow
 * (<a href="Controller.html#workflow">and that defined by interface</a>):</b><br>
 * <ol>
 * <li>{@link #handleRequest(HttpServletRequest, HttpServletResponse) handleRequest()}
 * will be called by the DispatcherServlet</li>
 * <li>Inspection of supported methods (ServletException if request method
 * is not support)</li>
 * <li>If session is required, try to get it (ServletException if not found)</li>
 * <li>Set caching headers if needed according to the cacheSeconds property</li>
 * <li>Call abstract method {@link #handleRequestInternal(HttpServletRequest, HttpServletResponse) handleRequestInternal()}
 * (optionally synchronizing around the call on the HttpSession),
 * which should be implemented by extending classes to provide actual
 * functionality to return {@link org.springframework.web.servlet.ModelAndView ModelAndView} objects.</li>
 * </ol>
 *
 * <p><b><a name="config">Exposed configuration properties</a>
 * (<a href="Controller.html#config">and those defined by interface</a>):</b><br>
 * <table border="1">
 * <tr>
 * <td><b>name</b></th>
 * <td><b>default</b></td>
 * <td><b>description</b></td>
 * </tr>
 * <tr>
 * <td>supportedMethods</td>
 * <td>GET,POST</td>
 * <td>comma-separated (CSV) list of methods supported by this controller,
 * such as GET, POST and PUT</td>
 * </tr>
 * <tr>
 * <td>requireSession</td>
 * <td>false</td>
 * <td>whether a session should be required for requests to be able to
 * be handled by this controller. This ensures that derived controller
 * can - without fear of null pointers - call request.getSession() to
 * retrieve a session. If no session can be found while processing
 * the request, a ServletException will be thrown</td>
 * </tr>
 * <tr>
 * <td>cacheSeconds</td>
 * <td>-1</td>
 * <td>indicates the amount of seconds to include in the cache header
 * for the response following on this request. 0 (zero) will include
 * headers for no caching at all, -1 (the default) will not generate
 * <i>any headers</i> and any positive number will generate headers
 * that state the amount indicated as seconds to cache the content</td>
 * </tr>
 * <tr>
 * <td>synchronizeOnSession</td>
 * <td>false</td>
 * <td>whether the call to {@code handleRequestInternal} should be
 * synchronized around the HttpSession, to serialize invocations
 * from the same client. No effect if there is no HttpSession.
 * </td>
 * </tr>
 * </table>
 *
 * <p>
 *  控制器实现方便的超类,使用模板方法设计模式
 * 
 *  <p> <b> <a name=\"workflow\">工作流程(<a href=\"Controllerhtml#workflow\">和由界面定义的)</a>：</b> <br>
 * <ol>
 * <li> {@ link #handleRequest(HttpServletRequest,HttpServletResponse)handleRequest()}将由DispatcherServle
 * t调用</li> <li>检查支持的方法(如果请求方法不支持,则为ServletException)</li> <li>如果需要会话,尝试获取它(如果没有找到ServletException)</li>
 *  <li>根据cacheSeconds属性设置缓存头(</li> <li>调用抽象方法{@link #handleRequestInternal(HttpServletRequest, HttpServletResponse)handleRequestInternal()}
 * (可选地同步在HttpSession上的调用),这应该通过扩展类来实现,以提供实际的功能来返回{@link orgspringframeworkwebservletModelAndView ModelAndView}
 * 对象</li>。
 * </ol>
 * 
 * <p> <b> <a name=\"config\">暴露的配置属性</a>(<a href=\"Controllerhtml#config\">和由界面定义的那些</a>)：</b> <br>
 * <table border="1">
 * <tr>
 *  <td> <b>名称</b> </th> <td> <b>默认</b> </td> <td> <b>描述</b> </td>
 * </tr>
 * <tr>
 *  <td> supportedMethods </td> <td> GET,POST </td> <td>此控制器支持的方法的逗号分隔(CSV)列表,例如GET,POST和PUT </td>
 * </tr>
 * <tr>
 *  <td> requireSession </td> <td> false </td> <td>是否需要会话才能使该控制器能够处理请求。
 * 这确保派生控制器可以 - 不用担心空指针 - 调用requestgetSession()来检索会话如果在处理请求时没有找到会话,将抛出一个ServletException </td>。
 * </tr>
 * <tr>
 * <td> cacheSeconds </td> <td> </td> <td>表示在该请求后面的响应的缓存头中包含的秒数0(零)将包括根本不缓存的头,-1(默认值)将不会生成任何头文件</i>,任何正数
 * 都会生成头文件,这些头文件指示缓存内容的秒数量。
 * </td>。
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Rossen Stoyanchev
 * @see WebContentInterceptor
 */
public abstract class AbstractController extends WebContentGenerator implements Controller {

	private boolean synchronizeOnSession = false;


	/**
	 * Create a new AbstractController which supports
	 * HTTP methods GET, HEAD and POST by default.
	 * <p>
	 * </tr>
	 * <tr>
	 *  <td> synchronizeOnSession </td> <td> false </td> <td>是否应该在HttpSession周围调用{@code handleRequestInternal}
	 * ,以便从同一客户端序列化调用没有HttpSession的效果。
	 * </td>
	 * </tr>
	 * </table>
	 * 
	 */
	public AbstractController() {
		this(true);
	}

	/**
	 * Create a new AbstractController.
	 * <p>
	 *  创建一个新的AbstractController,它默认支持HTTP方法GET,HEAD和POST
	 * 
	 * 
	 * @param restrictDefaultSupportedMethods {@code true} if this
	 * controller should support HTTP methods GET, HEAD and POST by default,
	 * or {@code false} if it should be unrestricted
	 * @since 4.3
	 */
	public AbstractController(boolean restrictDefaultSupportedMethods) {
		super(restrictDefaultSupportedMethods);
	}


	/**
	 * Set if controller execution should be synchronized on the session,
	 * to serialize parallel invocations from the same client.
	 * <p>More specifically, the execution of the {@code handleRequestInternal}
	 * method will get synchronized if this flag is "true". The best available
	 * session mutex will be used for the synchronization; ideally, this will
	 * be a mutex exposed by HttpSessionMutexListener.
	 * <p>The session mutex is guaranteed to be the same object during
	 * the entire lifetime of the session, available under the key defined
	 * by the {@code SESSION_MUTEX_ATTRIBUTE} constant. It serves as a
	 * safe reference to synchronize on for locking on the current session.
	 * <p>In many cases, the HttpSession reference itself is a safe mutex
	 * as well, since it will always be the same object reference for the
	 * same active logical session. However, this is not guaranteed across
	 * different servlet containers; the only 100% safe way is a session mutex.
	 * <p>
	 *  创建一个新的AbstractController
	 * 
	 * 
	 * @see AbstractController#handleRequestInternal
	 * @see org.springframework.web.util.HttpSessionMutexListener
	 * @see org.springframework.web.util.WebUtils#getSessionMutex(javax.servlet.http.HttpSession)
	 */
	public final void setSynchronizeOnSession(boolean synchronizeOnSession) {
		this.synchronizeOnSession = synchronizeOnSession;
	}

	/**
	 * Return whether controller execution should be synchronized on the session.
	 * <p>
	 * 设置如果控制器执行应该在会话上同步,以串行化来自同一客户端的并行调用<p>更具体地说,如果该标志为"true",则{@code handleRequestInternal}方法的执行将被同步。
	 * 最佳可用会话互斥体将用于同步;理想情况下,这将是HttpSessionMutexListener暴露的互斥体<p>会话互斥体在会话的整个生命周期内保证是相同的对象,可在{@code SESSION_MUTEX_ATTRIBUTE}
	 * 常量定义的关键字下使用它作为安全引用在当前会话上进行锁定同步<p>在许多情况下,HttpSession引用本身也是一个安全的互斥体,因为它将始终与同一个活动逻辑会话相同的对象引用。
	 * 设置如果控制器执行应该在会话上同步,以串行化来自同一客户端的并行调用<p>更具体地说,如果该标志为"true",则{@code handleRequestInternal}方法的执行将被同步。
	 * 但是,不能保证跨不同的servlet容器;唯一的100％安全方式是会话互斥体。
	 * 
	 */
	public final boolean isSynchronizeOnSession() {
		return this.synchronizeOnSession;
	}


	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		if (HttpMethod.OPTIONS.matches(request.getMethod())) {
			response.setHeader("Allow", getAllowHeader());
			return null;
		}

		// Delegate to WebContentGenerator for checking and preparing.
		checkRequest(request);
		prepareResponse(response);

		// Execute handleRequestInternal in synchronized block if required.
		if (this.synchronizeOnSession) {
			HttpSession session = request.getSession(false);
			if (session != null) {
				Object mutex = WebUtils.getSessionMutex(session);
				synchronized (mutex) {
					return handleRequestInternal(request, response);
				}
			}
		}

		return handleRequestInternal(request, response);
	}

	/**
	 * Template method. Subclasses must implement this.
	 * The contract is the same as for {@code handleRequest}.
	 * <p>
	 * 
	 * @see #handleRequest
	 */
	protected abstract ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
			throws Exception;

}
