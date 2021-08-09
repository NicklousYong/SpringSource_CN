/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2014 the original author or authors.
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

package org.springframework.web;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Plain handler interface for components that process HTTP requests,
 * analogous to a Servlet. Only declares {@link javax.servlet.ServletException}
 * and {@link java.io.IOException}, to allow for usage within any
 * {@link javax.servlet.http.HttpServlet}. This interface is essentially the
 * direct equivalent of an HttpServlet, reduced to a central handle method.
 *
 * <p>The easiest way to expose an HttpRequestHandler bean in Spring style
 * is to define it in Spring's root web application context and define
 * an {@link org.springframework.web.context.support.HttpRequestHandlerServlet}
 * in {@code web.xml}, pointing to the target HttpRequestHandler bean
 * through its {@code servlet-name} which needs to match the target bean name.
 *
 * <p>Supported as a handler type within Spring's
 * {@link org.springframework.web.servlet.DispatcherServlet}, being able
 * to interact with the dispatcher's advanced mapping and interception
 * facilities. This is the recommended way of exposing an HttpRequestHandler,
 * while keeping the handler implementations free of direct dependencies
 * on a DispatcherServlet environment.
 *
 * <p>Typically implemented to generate binary responses directly,
 * with no separate view resource involved. This differentiates it from a
 * {@link org.springframework.web.servlet.mvc.Controller} within Spring's Web MVC
 * framework. The lack of a {@link org.springframework.web.servlet.ModelAndView}
 * return value gives a clearer signature to callers other than the
 * DispatcherServlet, indicating that there will never be a view to render.
 *
 * <p>As of Spring 2.0, Spring's HTTP-based remote exporters, such as
 * {@link org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter}
 * and {@link org.springframework.remoting.caucho.HessianServiceExporter},
 * implement this interface rather than the more extensive Controller interface,
 * for minimal dependencies on Spring-specific web infrastructure.
 *
 * <p>Note that HttpRequestHandlers may optionally implement the
 * {@link org.springframework.web.servlet.mvc.LastModified} interface,
 * just like Controllers can, <i>provided that they run within Spring's
 * DispatcherServlet</i>. However, this is usually not necessary, since
 * HttpRequestHandlers typically only support POST requests to begin with.
 * Alternatively, a handler may implement the "If-Modified-Since" HTTP
 * header processing manually within its {@code handle} method.
 *
 * <p>
 * 处理HTTP请求的组件的纯处理程序接口类似于Servlet,仅声明{@link javaxservletServletException}和{@link javaioIOException},以允许在任
 * 何{@link javaxservlethttpHttpServlet}中使用该接口。
 * 此接口基本上与HttpServlet的直接相当,减少到中央手柄方法。
 * 
 *  在Spring样式中公开HttpRequestHandler bean的最简单的方法是在Spring的根Web应用程序上下文中定义它,并在{@code webxml}中定义一个{@link orgspringframeworkwebcontextsupportHttpRequestHandlerServlet}
 * ,通过其{@code指向目标HttpRequestHandler bean servlet-name},需要匹配目标bean名称。
 * 
 * <p>作为Spring的{@link orgspringframeworkwebservletDispatcherServlet}中的处理程序类型进行支持,能够与调度程序的高级映射和拦截功能进行交互这是
 * 暴露HttpRequestHandler的推荐方法,同时保持处理程序实现不受DispatcherServlet的直接依赖环境。
 * 
 *  通常实现直接生成二进制响应,没有单独的视图资源。它与Spring的Web MVC框架中的{@link orgspringframeworkwebservletmvcController}区分开来。
 * 缺少一个{@link orgspringframeworkwebservletModelAndView}返回值可以为呼叫者提供更清晰的签名而不是DispatcherServlet,表明永远不会有渲染的视
 * 图。
 *  通常实现直接生成二进制响应,没有单独的视图资源。它与Spring的Web MVC框架中的{@link orgspringframeworkwebservletmvcController}区分开来。
 * 
 * @author Juergen Hoeller
 * @since 2.0
 * @see org.springframework.web.context.support.HttpRequestHandlerServlet
 * @see org.springframework.web.servlet.DispatcherServlet
 * @see org.springframework.web.servlet.ModelAndView
 * @see org.springframework.web.servlet.mvc.Controller
 * @see org.springframework.web.servlet.mvc.LastModified
 * @see org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter
 * @see org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter
 * @see org.springframework.remoting.caucho.HessianServiceExporter
 * @see org.springframework.remoting.caucho.BurlapServiceExporter
 */
public interface HttpRequestHandler {

	/**
	 * Process the given request, generating a response.
	 * <p>
	 * 
	 * 从Spring 20开始,Spring的基于HTTP的远程出口商(如{@link orgspringframeworkremotinghttpinvokerHttpInvokerServiceExporter}
	 * 和{@link orgspringframeworkremotingcauchoHessianServiceExporter})实现了这个接口,而不是更广泛的Controller接口,用于对Spring
	 * 特定的Web基础架构的最小依赖。
	 * 
	 * <p>注意,HttpRequestHandlers可以可选地实现{@link orgspringframeworkwebservletmvcLastModified}接口,就像Controllers可以
	 * ,<i>,只要它们在Spring的DispatcherServlet </i>中运行。
	 * 
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @throws ServletException in case of general errors
	 * @throws IOException in case of I/O errors
	 */
	void handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException;

}
