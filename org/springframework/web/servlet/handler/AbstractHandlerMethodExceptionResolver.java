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

package org.springframework.web.servlet.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Abstract base class for
 * {@link org.springframework.web.servlet.HandlerExceptionResolver HandlerExceptionResolver}
 * implementations that support handling exceptions from handlers of type {@link HandlerMethod}.
 *
 * <p>
 *  {@link orgspringframeworkwebservletHandlerExceptionResolver HandlerExceptionResolver}的抽象基类,支持处理类型为{@link HandlerMethod}
 * 的处理程序的异常。
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public abstract class AbstractHandlerMethodExceptionResolver extends AbstractHandlerExceptionResolver {

	/**
	 * Checks if the handler is a {@link HandlerMethod} and then delegates to the
	 * base class implementation of {@code #shouldApplyTo(HttpServletRequest, Object)}
	 * passing the bean of the {@code HandlerMethod}. Otherwise returns {@code false}.
	 * <p>
	 * 检查处理程序是否是{@link HandlerMethod},然后委托给{@code HandlerMethod}的bean的{@code #shouldApplyTo(HttpServletRequest,Object))的基类实现,否则返回{@code false}
	 * 。
	 * 
	 */
	@Override
	protected boolean shouldApplyTo(HttpServletRequest request, Object handler) {
		if (handler == null) {
			return super.shouldApplyTo(request, handler);
		}
		else if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			handler = handlerMethod.getBean();
			return super.shouldApplyTo(request, handler);
		}
		else {
			return false;
		}
	}

	@Override
	protected final ModelAndView doResolveException(
			HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

		return doResolveHandlerMethodException(request, response, (HandlerMethod) handler, ex);
	}

	/**
	 * Actually resolve the given exception that got thrown during on handler execution,
	 * returning a ModelAndView that represents a specific error page if appropriate.
	 * <p>May be overridden in subclasses, in order to apply specific exception checks.
	 * Note that this template method will be invoked <i>after</i> checking whether this
	 * resolved applies ("mappedHandlers" etc), so an implementation may simply proceed
	 * with its actual exception handling.
	 * <p>
	 *  实际上解决在处理程序执行期间抛出的给定异常,返回一个表示特定错误页面的ModelAndView(如果适用)<p>可能会在子类中被覆盖,以便应用特定的异常检查请注意,此模板方法将被调用<i >在</i>
	 * 检查此解决是否适用("mappedHandlers"等)之后,所以实现可以简单地继续其实际的异常处理。
	 * 
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handlerMethod the executed handler method, or {@code null} if none chosen at the time
	 * of the exception (for example, if multipart resolution failed)
	 * @param ex the exception that got thrown during handler execution
	 * @return a corresponding ModelAndView to forward to, or {@code null} for default processing
	 */
	protected abstract ModelAndView doResolveHandlerMethodException(
			HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod, Exception ex);

}
