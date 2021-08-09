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

package org.springframework.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;

/**
 * Workflow interface that allows for customized handler execution chains.
 * Applications can register any number of existing or custom interceptors
 * for certain groups of handlers, to add common preprocessing behavior
 * without needing to modify each handler implementation.
 *
 * <p>A HandlerInterceptor gets called before the appropriate HandlerAdapter
 * triggers the execution of the handler itself. This mechanism can be used
 * for a large field of preprocessing aspects, e.g. for authorization checks,
 * or common handler behavior like locale or theme changes. Its main purpose
 * is to allow for factoring out repetitive handler code.
 *
 * <p>In an asynchronous processing scenario, the handler may be executed in a
 * separate thread while the main thread exits without rendering or invoking the
 * {@code postHandle} and {@code afterCompletion} callbacks. When concurrent
 * handler execution completes, the request is dispatched back in order to
 * proceed with rendering the model and all methods of this contract are invoked
 * again. For further options and details see
 * {@code org.springframework.web.servlet.AsyncHandlerInterceptor}
 *
 * <p>Typically an interceptor chain is defined per HandlerMapping bean,
 * sharing its granularity. To be able to apply a certain interceptor chain
 * to a group of handlers, one needs to map the desired handlers via one
 * HandlerMapping bean. The interceptors themselves are defined as beans
 * in the application context, referenced by the mapping bean definition
 * via its "interceptors" property (in XML: a &lt;list&gt; of &lt;ref&gt;).
 *
 * <p>HandlerInterceptor is basically similar to a Servlet Filter, but in
 * contrast to the latter it just allows custom pre-processing with the option
 * of prohibiting the execution of the handler itself, and custom post-processing.
 * Filters are more powerful, for example they allow for exchanging the request
 * and response objects that are handed down the chain. Note that a filter
 * gets configured in web.xml, a HandlerInterceptor in the application context.
 *
 * <p>As a basic guideline, fine-grained handler-related preprocessing tasks are
 * candidates for HandlerInterceptor implementations, especially factored-out
 * common handler code and authorization checks. On the other hand, a Filter
 * is well-suited for request content and view content handling, like multipart
 * forms and GZIP compression. This typically shows when one needs to map the
 * filter to certain content types (e.g. images), or to all requests.
 *
 * <p>
 * 允许自定义处理程序执行链的工作流界面应用程序可以为某些处理程序组注册任意数量的现有或自定义拦截器,以添加常见的预处理行为,而无需修改每个处理程序实现
 * 
 *  <p>在适当的HandlerAdapter触发执行处理程序本身之前调用HandlerInterceptor此机制可用于预处理方面的大型领域,例如用于授权检查,或常见的处理程序行为,如区域设置或主题更改
 * 其主要目的是允许分解重复的处理程序代码。
 * 
 * 在异步处理情况下,处理程序可以在单独的线程中执行,而主线程不退出或调用{@code postHandle}和{@code afterCompletion}回调而退出当并发处理程序执行完成时,请求被调度返
 * 回以继续呈现模型,并重新调用此合同的所有方法。
 * 有关更多选项和详细信息,请参阅{@code orgspringframeworkwebservletAsyncHandlerInterceptor}。
 * 
 * 通常,每个HandlerMapping bean定义一个拦截器链,共享其粒度为了能够将某个拦截器链应用于一组处理程序,需要通过一个HandlerMapping bean映射所需的处理程序拦截器本身定义为
 * 应用程序上下文由映射bean定义通过其"interceptors"属性引用(在XML中：&lt; ref&gt;的&lt; list&gt;)。
 * 
 * HandlerInterceptor基本上类似于Servlet Filter,但与后者相反,它只允许自定义预处理,可以禁止执行处理程序本身,并且自定义后处理过滤器更强大,例如允许交换链中传递的请求和响应
 * 对象请注意,过滤器在webxml中配置,应用程序上下文中的HandlerInterceptor。
 * 
 * <p>作为基本指导原则,细粒度处理程序相关的预处理任务是HandlerInterceptor实现的候选者,特别是考虑不了的常见处理程序代码和授权检查另一方面,Filter非常适合于请求内容和查看内容处理
 * ,如多部分表单和GZIP压缩通常显示何时需要将过滤器映射到某些内容类型(例如图像)或所有请求。
 * 
 * @author Juergen Hoeller
 * @since 20.06.2003
 * @see HandlerExecutionChain#getInterceptors
 * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter
 * @see org.springframework.web.servlet.handler.AbstractHandlerMapping#setInterceptors
 * @see org.springframework.web.servlet.handler.UserRoleAuthorizationInterceptor
 * @see org.springframework.web.servlet.i18n.LocaleChangeInterceptor
 * @see org.springframework.web.servlet.theme.ThemeChangeInterceptor
 * @see javax.servlet.Filter
 */
public interface HandlerInterceptor {

	/**
	 * Intercept the execution of a handler. Called after HandlerMapping determined
	 * an appropriate handler object, but before HandlerAdapter invokes the handler.
	 * <p>DispatcherServlet processes a handler in an execution chain, consisting
	 * of any number of interceptors, with the handler itself at the end.
	 * With this method, each interceptor can decide to abort the execution chain,
	 * typically sending a HTTP error or writing a custom response.
	 * <p><strong>Note:</strong> special considerations apply for asynchronous
	 * request processing. For more details see
	 * {@link org.springframework.web.servlet.AsyncHandlerInterceptor}.
	 * <p>
	 * 
	 * 
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler chosen handler to execute, for type and/or instance evaluation
	 * @return {@code true} if the execution chain should proceed with the
	 * next interceptor or the handler itself. Else, DispatcherServlet assumes
	 * that this interceptor has already dealt with the response itself.
	 * @throws Exception in case of errors
	 */
	boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception;

	/**
	 * Intercept the execution of a handler. Called after HandlerAdapter actually
	 * invoked the handler, but before the DispatcherServlet renders the view.
	 * Can expose additional model objects to the view via the given ModelAndView.
	 * <p>DispatcherServlet processes a handler in an execution chain, consisting
	 * of any number of interceptors, with the handler itself at the end.
	 * With this method, each interceptor can post-process an execution,
	 * getting applied in inverse order of the execution chain.
	 * <p><strong>Note:</strong> special considerations apply for asynchronous
	 * request processing. For more details see
	 * {@link org.springframework.web.servlet.AsyncHandlerInterceptor}.
	 * <p>
	 * 拦截处理程序的执行HandlerMapping之后调用,确定了一个适当的处理程序对象,但在HandlerAdapter调用处理程序之前,DispatcherServlet处理执行链中由处理程序本身结束的
	 * 任意数量的拦截器的处理程序。
	 *  ,每个拦截器可以决定中止执行链,通常发送HTTP错误或编写自定义响应<p> <strong>注意：</strong>特殊注意事项适用于异步请求处理有关详细信息,请参阅{@link orgspringframeworkwebservletAsyncHandlerInterceptor}
	 * 。
	 * 
	 * 
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler handler (or {@link HandlerMethod}) that started asynchronous
	 * execution, for type and/or instance examination
	 * @param modelAndView the {@code ModelAndView} that the handler returned
	 * (can also be {@code null})
	 * @throws Exception in case of errors
	 */
	void postHandle(
			HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
			throws Exception;

	/**
	 * Callback after completion of request processing, that is, after rendering
	 * the view. Will be called on any outcome of handler execution, thus allows
	 * for proper resource cleanup.
	 * <p>Note: Will only be called if this interceptor's {@code preHandle}
	 * method has successfully completed and returned {@code true}!
	 * <p>As with the {@code postHandle} method, the method will be invoked on each
	 * interceptor in the chain in reverse order, so the first interceptor will be
	 * the last to be invoked.
	 * <p><strong>Note:</strong> special considerations apply for asynchronous
	 * request processing. For more details see
	 * {@link org.springframework.web.servlet.AsyncHandlerInterceptor}.
	 * <p>
	 * 拦截处理程序的执行HandlerAdapter实际调用处理程序之后调用,但在DispatcherServlet呈现视图之前,可以通过给定的ModelAndView DispatcherServlet将附
	 * 加的模型对象暴露给视图,处理执行链中的处理程序,其中包含任何数量的拦截器,处理程序本身在最后使用这种方法,每个拦截器可以后处理执行,按照执行链的相反顺序应用<p> <strong>注意：</strong>
	 * 特殊注意事项适用于异步请求处理有关详细信息,请参阅{@link orgspringframeworkwebservletAsyncHandlerInterceptor}。
	 * 
	 * 
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler handler (or {@link HandlerMethod}) that started asynchronous
	 * execution, for type and/or instance examination
	 * @param ex exception thrown on handler execution, if any
	 * @throws Exception in case of errors
	 */
	void afterCompletion(
			HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception;

}
