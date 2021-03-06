/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2015 the original author or authors.
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

package org.springframework.web.servlet.mvc.annotation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

/**
 * A {@link org.springframework.web.servlet.HandlerExceptionResolver
 * HandlerExceptionResolver} that uses the {@link ResponseStatus @ResponseStatus}
 * annotation to map exceptions to HTTP status codes.
 *
 * <p>This exception resolver is enabled by default in the
 * {@link org.springframework.web.servlet.DispatcherServlet DispatcherServlet}
 * and the MVC Java config and the MVC namespace.
 *
 * <p>As of 4.2 this resolver also looks recursively for {@code @ResponseStatus}
 * present on cause exceptions, and as of 4.2.2 this resolver supports
 * attribute overrides for {@code @ResponseStatus} in custom composed annotations.
 *
 * <p>
 *  使用{@link ResponseStatus @ResponseStatus}注释将异常映射到HTTP状态代码的{@link orgspringframeworkwebservletHandlerExceptionResolver HandlerExceptionResolver}
 * 。
 * 
 * <p>此异常解析器默认情况下在{@link orgspringframeworkwebservletDispatcherServlet DispatcherServlet}和MVC Java配置和MVC
 * 命名空间中启用。
 * 
 * 
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @author Sam Brannen
 * @since 3.0
 * @see AnnotatedElementUtils#findMergedAnnotation
 */
public class ResponseStatusExceptionResolver extends AbstractHandlerExceptionResolver implements MessageSourceAware {

	private MessageSource messageSource;


	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}


	@Override
	protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response,
			Object handler, Exception ex) {

		ResponseStatus responseStatus = AnnotatedElementUtils.findMergedAnnotation(ex.getClass(), ResponseStatus.class);
		if (responseStatus != null) {
			try {
				return resolveResponseStatus(responseStatus, request, response, handler, ex);
			}
			catch (Exception resolveEx) {
				logger.warn("Handling of @ResponseStatus resulted in Exception", resolveEx);
			}
		}
		else if (ex.getCause() instanceof Exception) {
			ex = (Exception) ex.getCause();
			return doResolveException(request, response, handler, ex);
		}
		return null;
	}

	/**
	 * Template method that handles {@link ResponseStatus @ResponseStatus} annotation.
	 * <p>The default implementation sends a response error using
	 * {@link HttpServletResponse#sendError(int)} or
	 * {@link HttpServletResponse#sendError(int, String)} if the annotation has a
	 * {@linkplain ResponseStatus#reason() reason} and then returns an empty ModelAndView.
	 * <p>
	 *  <p>截至42,该解析器对递归原因异常的{@code @ResponseStatus}递归展示,而在此解析器中,解析器支持自定义组合注释中{@code @ResponseStatus}的属性覆盖
	 * 
	 * 
	 * @param responseStatus the annotation
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler the executed handler, or {@code null} if none chosen at the
	 * time of the exception (for example, if multipart resolution failed)
	 * @param ex the exception that got thrown during handler execution or the
	 * exception that has the ResponseStatus annotation if found on the cause.
	 * @return a corresponding ModelAndView to forward to, or {@code null}
	 * for default processing
	 */
	protected ModelAndView resolveResponseStatus(ResponseStatus responseStatus, HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) throws Exception {

		int statusCode = responseStatus.code().value();
		String reason = responseStatus.reason();
		if (this.messageSource != null) {
			reason = this.messageSource.getMessage(reason, null, reason, LocaleContextHolder.getLocale());
		}
		if (!StringUtils.hasLength(reason)) {
			response.sendError(statusCode);
		}
		else {
			response.sendError(statusCode, reason);
		}
		return new ModelAndView();
	}

}
