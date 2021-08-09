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

package org.springframework.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * MVC framework SPI, allowing parameterization of the core MVC workflow.
 *
 * <p>Interface that must be implemented for each handler type to handle a request.
 * This interface is used to allow the {@link DispatcherServlet} to be indefinitely
 * extensible. The {@code DispatcherServlet} accesses all installed handlers through
 * this interface, meaning that it does not contain code specific to any handler type.
 *
 * <p>Note that a handler can be of type {@code Object}. This is to enable
 * handlers from other frameworks to be integrated with this framework without
 * custom coding, as well as to allow for annotation-driven handler objects that
 * do not obey any specific Java interface.
 *
 * <p>This interface is not intended for application developers. It is available
 * to handlers who want to develop their own web workflow.
 *
 * <p>Note: {@code HandlerAdapter} implementors may implement the {@link
 * org.springframework.core.Ordered} interface to be able to specify a sorting
 * order (and thus a priority) for getting applied by the {@code DispatcherServlet}.
 * Non-Ordered instances get treated as lowest priority.
 *
 * <p>
 *  MVC框架SPI,允许参数化核心MVC工作流
 * 
 * <p>必须为每个处理程序类型实现的接口来处理请求此接口用于允许{@link DispatcherServlet}无限可扩展{@code DispatcherServlet}通过此界面访问所有已安装的处理
 * 程序,这意味着它不包含特定于任何处理程序类型的代码。
 * 
 *  <p>请注意,处理程序可以是{@code Object}类型。这是为了使其他框架的处理程序能够与此框架集成而无需自定义编码,并允许注释驱动的处理程序对象不遵守任何具体的Java接口
 * 
 *  <p>此接口不适用于应用程序开发人员它可用于希望开发自己的Web工作流的处理程序
 * 
 * <p>注意：{@code HandlerAdapter}实现者可以实现{@link orgspringframeworkcoreOrdered}接口,以便能够指定排序顺序(因此是优先级),以便通过{@code DispatcherServlet}
 * 非有序实例获得应用程序作为最低优先级。
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter
 * @see org.springframework.web.servlet.handler.SimpleServletHandlerAdapter
 */
public interface HandlerAdapter {

	/**
	 * Given a handler instance, return whether or not this {@code HandlerAdapter}
	 * can support it. Typical HandlerAdapters will base the decision on the handler
	 * type. HandlerAdapters will usually only support one handler type each.
	 * <p>A typical implementation:
	 * <p>{@code
	 * return (handler instanceof MyHandler);
	 * }
	 * <p>
	 * 
	 * 
	 * @param handler handler object to check
	 * @return whether or not this object can use the given handler
	 */
	boolean supports(Object handler);

	/**
	 * Use the given handler to handle this request.
	 * The workflow that is required may vary widely.
	 * <p>
	 *  给定一个处理程序实例,返回这个{@code HandlerAdapter}是否可以支持它典型的HandlerAdapters将基于处理器类型的决定HandlerAdapters通常只支持一个处理程序类
	 * 型每个<p>典型的实现：<p> {@ code return(MyHandler的handler instanceof); }。
	 * 
	 * 
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler handler to use. This object must have previously been passed
	 * to the {@code supports} method of this interface, which must have
	 * returned {@code true}.
	 * @throws Exception in case of errors
	 * @return ModelAndView object with the name of the view and the required
	 * model data, or {@code null} if the request has been handled directly
	 */
	ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception;

	/**
	 * Same contract as for HttpServlet's {@code getLastModified} method.
	 * Can simply return -1 if there's no support in the handler class.
	 * <p>
	 *  使用给定的处理程序来处理此请求所需的工作流程可能会有很大差异
	 * 
	 * 
	 * @param request current HTTP request
	 * @param handler handler to use
	 * @return the lastModified value for the given handler
	 * @see javax.servlet.http.HttpServlet#getLastModified
	 * @see org.springframework.web.servlet.mvc.LastModified#getLastModified
	 */
	long getLastModified(HttpServletRequest request, Object handler);

}
