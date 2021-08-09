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

package org.springframework.web.servlet.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

/**
 * Base Controller interface, representing a component that receives
 * {@code HttpServletRequest} and {@code HttpServletResponse}
 * instances just like a {@code HttpServlet} but is able to
 * participate in an MVC workflow. Controllers are comparable to the
 * notion of a Struts {@code Action}.
 *
 * <p>Any implementation of the Controller interface should be a
 * <i>reusable, thread-safe</i> class, capable of handling multiple
 * HTTP requests throughout the lifecycle of an application. To be able to
 * configure a Controller easily, Controller implementations are encouraged
 * to be (and usually are) JavaBeans.
 *
 * <h3><a name="workflow">Workflow</a></h3>
 *
 * <p>After a {@code DispatcherServlet} has received a request and has
 * done its work to resolve locales, themes, and suchlike, it then tries
 * to resolve a Controller, using a
 * {@link org.springframework.web.servlet.HandlerMapping HandlerMapping}.
 * When a Controller has been found to handle the request, the
 * {@link #handleRequest(HttpServletRequest, HttpServletResponse) handleRequest}
 * method of the located Controller will be invoked; the located Controller
 * is then responsible for handling the actual request and &mdash; if applicable
 * &mdash; returning an appropriate
 * {@link org.springframework.web.servlet.ModelAndView ModelAndView}.
 * So actually, this method is the main entry point for the
 * {@link org.springframework.web.servlet.DispatcherServlet DispatcherServlet}
 * which delegates requests to controllers.
 *
 * <p>So basically any <i>direct</i> implementation of the {@code Controller} interface
 * just handles HttpServletRequests and should return a ModelAndView, to be further
 * interpreted by the DispatcherServlet. Any additional functionality such as
 * optional validation, form handling, etc. should be obtained through extending
 * {@link org.springframework.web.servlet.mvc.AbstractController AbstractController}
 * or one of its subclasses.
 *
 * <h3>Notes on design and testing</h3>
 *
 * <p>The Controller interface is explicitly designed to operate on HttpServletRequest
 * and HttpServletResponse objects, just like an HttpServlet. It does not aim to
 * decouple itself from the Servlet API, in contrast to, for example, WebWork, JSF or Tapestry.
 * Instead, the full power of the Servlet API is available, allowing Controllers to be
 * general-purpose: a Controller is able to not only handle web user interface
 * requests but also to process remoting protocols or to generate reports on demand.
 *
 * <p>Controllers can easily be tested by passing in mock objects for the
 * HttpServletRequest and HttpServletResponse objects as parameters to the
 * {@link #handleRequest(HttpServletRequest, HttpServletResponse) handleRequest}
 * method. As a convenience, Spring ships with a set of Servlet API mocks
 * that are suitable for testing any kind of web components, but are particularly
 * suitable for testing Spring web controllers. In contrast to a Struts Action,
 * there is no need to mock the ActionServlet or any other infrastructure;
 * mocking HttpServletRequest and HttpServletResponse is sufficient.
 *
 * <p>If Controllers need to be aware of specific environment references, they can
 * choose to implement specific awareness interfaces, just like any other bean in a
 * Spring (web) application context can do, for example:
 * <ul>
 * <li>{@code org.springframework.context.ApplicationContextAware}</li>
 * <li>{@code org.springframework.context.ResourceLoaderAware}</li>
 * <li>{@code org.springframework.web.context.ServletContextAware}</li>
 * </ul>
 *
 * <p>Such environment references can easily be passed in testing environments,
 * through the corresponding setters defined in the respective awareness interfaces.
 * In general, it is recommended to keep the dependencies as minimal as possible:
 * for example, if all you need is resource loading, implement ResourceLoaderAware only.
 * Alternatively, derive from the WebApplicationObjectSupport base class, which gives
 * you all those references through convenient accessors but requires an
 * ApplicationContext reference on initialization.
 *
 * <p>Controllers can optionally implement the {@link LastModified} interface.
 *
 * <p>
 * Base Controller接口,代表一个接收{@code HttpServletRequest}和{@code HttpServletResponse}实例的组件,就像一个{@code HttpServlet}
 * ,但是能够参与MVC工作流控制器可以与Struts {@code的概念相媲美行动}。
 * 
 *  控制器接口的任何实现应该是一个可重用的线程安全的类,能够在应用程序的整个生命周期中处理多个HTTP请求。为了能够轻松配置控制器,控制器实现是鼓励(通常是)JavaBeans
 * 
 *  <h3> <a name=\"workflow\">工作流程</a> </h3>
 * 
 * <p>在{@code DispatcherServlet}收到请求并已完成其工作以解决语言环境,主题等之后,它会尝试使用{@link orgspringframeworkwebservletHandlerMapping HandlerMapping}
 * 解析控制器,当控制器已被发现时要处理请求,将调用位于Controller的{@link #handleRequest(HttpServletRequest,HttpServletResponse)handleRequest}
 * 方法;所在的控制器然后负责处理实际的请求和&mdash;如果适用&mdash;返回一个合适的{@link orgspringframeworkwebservletModelAndView ModelAndView}
 * 实际上,这个方法是{@link orgspringframeworkwebservletDispatcherServlet DispatcherServlet}的主要入口点,它将请求委托给控制器。
 * 
 * <p>因此,基本上{@code Controller}接口的任何<i>直接</i>实现只需处理HttpServletRequests,并返回一个ModelAndView,由DispatcherServl
 * et进一步解释任何其他功能,如可选验证,表单处理,应该通过扩展{@link orgspringframeworkwebservletmvcAbstractController AbstractController}
 * 或其一个子类来获得等等。
 * 
 *  <h3>设计和测试注意事项</h3>
 * 
 * <p> Controller接口被明确设计为在HttpServletRequest和HttpServletResponse对象上运行,就像HttpServlet它不旨在使其与Servlet API分离,
 * 与WebWork,JSF或Tapestry相反,而是全功率的Servlet API可用,允许控制器是通用的：控制器不仅可以处理Web用户界面请求,还可以处理远程处理协议或根据需要生成报告。
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see LastModified
 * @see SimpleControllerHandlerAdapter
 * @see AbstractController
 * @see org.springframework.mock.web.MockHttpServletRequest
 * @see org.springframework.mock.web.MockHttpServletResponse
 * @see org.springframework.context.ApplicationContextAware
 * @see org.springframework.context.ResourceLoaderAware
 * @see org.springframework.web.context.ServletContextAware
 * @see org.springframework.web.context.support.WebApplicationObjectSupport
 */
public interface Controller {

	/**
	 * Process the request and return a ModelAndView object which the DispatcherServlet
	 * will render. A {@code null} return value is not an error: it indicates that
	 * this object completed request processing itself and that there is therefore no
	 * ModelAndView to render.
	 * <p>
	 * 通过将HttpServletRequest和HttpServletResponse对象的模拟对象传递给{@link #handleRequest(HttpServletRequest,HttpServletResponse)handleRequest}
	 * 方法的参数,可以轻松地测试控制器。
	 * 为方便起见,Spring附带了一组Servlet API模拟适用于测试任何类型的Web组件,但特别适用于测试Spring Web控制器与Struts Action相反,不需要模拟ActionServle
	 * t或任何其他基础架构;嘲笑HttpServletRequest和HttpServletResponse就足够了。
	 * 
	 * <p>如果控制器需要了解特定的环境引用,则可以选择实现特定的感知界面,就像Spring(Web)应用程序上下文中的任何其他bean一样,例如：
	 * <ul>
	 *  <li> {@ code orgspringframeworkcontextApplicationContextAware} </li> <li> {@ code orgspringframeworkcontextResourceLoaderAware}
	 *  </li> <li> {@ code orgspringframeworkwebcontextServletContextAware} </li>。
	 * </ul>
	 * 
	 * 
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @return a ModelAndView to render, or {@code null} if handled directly
	 * @throws Exception in case of errors
	 */
	ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception;

}
