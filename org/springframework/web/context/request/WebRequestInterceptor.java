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

package org.springframework.web.context.request;

import org.springframework.ui.ModelMap;

/**
 * Interface for general web request interception. Allows for being applied
 * to Servlet request as well as Portlet request environments, by building
 * on the {@link WebRequest} abstraction.
 *
 * <p>This interface assumes MVC-style request processing: A handler gets executed,
 * exposes a set of model objects, then a view gets rendered based on that model.
 * Alternatively, a handler may also process the request completely, with no
 * view to be rendered.
 *
 * <p>In an async processing scenario, the handler may be executed in a separate
 * thread while the main thread exits without rendering or invoking the
 * {@code postHandle} and {@code afterCompletion} callbacks. When concurrent
 * handler execution completes, the request is dispatched back in order to
 * proceed with rendering the model and all methods of this contract are invoked
 * again. For further options and comments see
 * {@code org.springframework.web.context.request.async.AsyncWebRequestInterceptor}
 *
 * <p>This interface is deliberately minimalistic to keep the dependencies of
 * generic request interceptors as minimal as feasible.
 *
 * <p><b>NOTE:</b> While this interceptor is applied to the entire request processing
 * in a Servlet environment, it is by default only applied to the <i>render</i> phase
 * in a Portlet environment, preparing and rendering a Portlet view. To apply
 * WebRequestInterceptors to the <i>action</i> phase as well, set the HandlerMapping's
 * "applyWebRequestInterceptorsToRenderPhaseOnly" flag to "false". Alternatively,
 * consider using the Portlet-specific HandlerInterceptor mechanism for such needs.
 *
 * <p>
 *  通用Web请求拦截的接口允许通过在{@link WebRequest}抽象上构建来应用于Servlet请求以及Portlet请求环境
 * 
 * <p>此接口假定MVC样式的请求处理：处理程序被执行,公开一组模型对象,然后基于该模型呈现视图。或者,处理程序也可以完全处理请求,而不会呈现
 * 
 *  在异步处理场景中,处理程序可以在单独的线程中执行,而主线程退出而不呈现或调用{@code postHandle}和{@code afterCompletion}回调当并发处理程序执行完成时,请求被调度
 * 返回以继续呈现模型,并重新调用此合同的所有方法对于更多选项和注释,请参阅{@code orgspringframeworkwebcontextrequestasyncAsyncWebRequestInterceptor}
 * 。
 * 
 * <p>这个接口是故意简约的,以保持通用请求拦截器的依赖性尽可能的最小
 * 
 *  <p> <b>注意：</b>虽然此拦截器适用于Servlet环境中的整个请求处理,但默认情况下仅适用于Portlet环境中的<i> render </i>阶段,准备并呈现Portlet视图为了将Web
 * RequestInterceptors应用于<i>动作</i>阶段,请将HandlerMapping的"applyWebRequestInterceptorsToRenderPhaseOnly"标志设置
 * 
 * @author Juergen Hoeller
 * @since 2.0
 * @see ServletWebRequest
 * @see org.springframework.web.servlet.DispatcherServlet
 * @see org.springframework.web.servlet.handler.AbstractHandlerMapping#setInterceptors
 * @see org.springframework.web.servlet.HandlerInterceptor
 * @see org.springframework.web.portlet.context.PortletWebRequest
 * @see org.springframework.web.portlet.DispatcherPortlet
 * @see org.springframework.web.portlet.handler.AbstractHandlerMapping#setInterceptors
 * @see org.springframework.web.portlet.handler.AbstractHandlerMapping#setApplyWebRequestInterceptorsToRenderPhaseOnly
 * @see org.springframework.web.portlet.HandlerInterceptor
 */
public interface WebRequestInterceptor {

	/**
	 * Intercept the execution of a request handler <i>before</i> its invocation.
	 * <p>Allows for preparing context resources (such as a Hibernate Session)
	 * and expose them as request attributes or as thread-local objects.
	 * <p>
	 * 为"false"。
	 * 或者,考虑使用特定于Portlet的HandlerInterceptor机制进行此类需求。
	 * 
	 * 
	 * @param request the current web request
	 * @throws Exception in case of errors
	 */
	void preHandle(WebRequest request) throws Exception;

	/**
	 * Intercept the execution of a request handler <i>after</i> its successful
	 * invocation, right before view rendering (if any).
	 * <p>Allows for modifying context resources after successful handler
	 * execution (for example, flushing a Hibernate Session).
	 * <p>
	 * 在调用</i>之前拦截请求处理程序<i>的执行<p>允许准备上下文资源(如Hibernate Session),并将它们作为请求属性或线程本地对象
	 * 
	 * 
	 * @param request the current web request
	 * @param model the map of model objects that will be exposed to the view
	 * (may be {@code null}). Can be used to analyze the exposed model
	 * and/or to add further model attributes, if desired.
	 * @throws Exception in case of errors
	 */
	void postHandle(WebRequest request, ModelMap model) throws Exception;

	/**
	 * Callback after completion of request processing, that is, after rendering
	 * the view. Will be called on any outcome of handler execution, thus allows
	 * for proper resource cleanup.
	 * <p>Note: Will only be called if this interceptor's {@code preHandle}
	 * method has successfully completed!
	 * <p>
	 *  在成功的调用之后拦截请求处理程序<i>的执行,就在视图呈现之前(如果有的话)<p>允许在执行成功的处理程序之后修改上下文资源(例如,刷新Hibernate Session)
	 * 
	 * 
	 * @param request the current web request
	 * @param ex exception thrown on handler execution, if any
	 * @throws Exception in case of errors
	 */
	void afterCompletion(WebRequest request, Exception ex) throws Exception;

}
