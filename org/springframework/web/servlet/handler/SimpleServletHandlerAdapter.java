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

package org.springframework.web.servlet.handler;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;

/**
 * Adapter to use the Servlet interface with the generic DispatcherServlet.
 * Calls the Servlet's {@code service} method to handle a request.
 *
 * <p>Last-modified checking is not explicitly supported: This is typically
 * handled by the Servlet implementation itself (usually deriving from
 * the HttpServlet base class).
 *
 * <p>This adapter is not activated by default; it needs to be defined as a
 * bean in the DispatcherServlet context. It will automatically apply to
 * mapped handler beans that implement the Servlet interface then.
 *
 * <p>Note that Servlet instances defined as bean will not receive initialization
 * and destruction callbacks, unless a special post-processor such as
 * SimpleServletPostProcessor is defined in the DispatcherServlet context.
 *
 * <p><b>Alternatively, consider wrapping a Servlet with Spring's
 * ServletWrappingController.</b> This is particularly appropriate for
 * existing Servlet classes, allowing to specify Servlet initialization
 * parameters etc.
 *
 * <p>
 *  适配器使用Servlet接口与通用DispatcherServlet调用Servlet的{@code服务}方法来处理请求
 * 
 * 最后修改的检查没有明确的支持：这通常由Servlet实现本身(通常来自HttpServlet基类派生)
 * 
 *  <p>默认情况下,此适配器未激活;它需要在DispatcherServlet上下文中定义为一个bean它将自动应用于实现Servlet接口的映射处理程序bean
 * 
 * 
 * @author Juergen Hoeller
 * @since 1.1.5
 * @see javax.servlet.Servlet
 * @see javax.servlet.http.HttpServlet
 * @see SimpleServletPostProcessor
 * @see org.springframework.web.servlet.mvc.ServletWrappingController
 */
public class SimpleServletHandlerAdapter implements HandlerAdapter {

	@Override
	public boolean supports(Object handler) {
		return (handler instanceof Servlet);
	}

	@Override
	public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		((Servlet) handler).service(request, response);
		return null;
	}

	@Override
	public long getLastModified(HttpServletRequest request, Object handler) {
		return -1;
	}

}
