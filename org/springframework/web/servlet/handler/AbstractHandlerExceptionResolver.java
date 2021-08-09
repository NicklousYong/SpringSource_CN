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

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

/**
 * Abstract base class for {@link HandlerExceptionResolver} implementations.
 *
 * <p>Supports mapped {@linkplain #setMappedHandlers handlers} and
 * {@linkplain #setMappedHandlerClasses handler classes} that the resolver
 * should be applied to and implements the {@link Ordered} interface.
 *
 * <p>
 *  {@link HandlerExceptionResolver}实现的抽象基类
 * 
 * <p>支持映射的{@linkplain #setMappedHandlers处理程序}和{@linkplain #setMappedHandlerClasses处理程序类}解析器应该应用于并实现{@link Ordered}
 * 接口。
 * 
 * 
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 3.0
 */
public abstract class AbstractHandlerExceptionResolver implements HandlerExceptionResolver, Ordered {

	private static final String HEADER_CACHE_CONTROL = "Cache-Control";


	/** Logger available to subclasses */
	protected final Log logger = LogFactory.getLog(getClass());

	private int order = Ordered.LOWEST_PRECEDENCE;

	private Set<?> mappedHandlers;

	private Class<?>[] mappedHandlerClasses;

	private Log warnLogger;

	private boolean preventResponseCaching = false;


	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public int getOrder() {
		return this.order;
	}

	/**
	 * Specify the set of handlers that this exception resolver should apply to.
	 * <p>The exception mappings and the default error view will only apply to the specified handlers.
	 * <p>If no handlers or handler classes are set, the exception mappings and the default error
	 * view will apply to all handlers. This means that a specified default error view will be used
	 * as a fallback for all exceptions; any further HandlerExceptionResolvers in the chain will be
	 * ignored in this case.
	 * <p>
	 *  指定此异常解析器应用于<p>的处理程序集异常映射和默认错误视图将仅应用于指定的处理程序<p>如果没有处理程序或处理程序类设置,则异常映射和默认错误视图将适用于所有处理程序这意味着指定的默认错误视图将被
	 * 用作所有异常的回退;在这种情况下,链中任何进一步的HandlerExceptionResolvers将被忽略。
	 * 
	 */
	public void setMappedHandlers(Set<?> mappedHandlers) {
		this.mappedHandlers = mappedHandlers;
	}

	/**
	 * Specify the set of classes that this exception resolver should apply to.
	 * <p>The exception mappings and the default error view will only apply to handlers of the
	 * specified types; the specified types may be interfaces or superclasses of handlers as well.
	 * <p>If no handlers or handler classes are set, the exception mappings and the default error
	 * view will apply to all handlers. This means that a specified default error view will be used
	 * as a fallback for all exceptions; any further HandlerExceptionResolvers in the chain will be
	 * ignored in this case.
	 * <p>
	 * 指定此异常解析器应用于<p>的类集,异常映射和默认错误视图将仅适用于指定类型的处理程序;指定的类型可以是处理程序的接口或超类;以及<p>如果没有设置处理程序或处理程序类,则异常映射和默认错误视图将应用于
	 * 所有处理程序。
	 * 这意味着指定的默认错误视图将用作所有例外的回退;在这种情况下,链中任何进一步的HandlerExceptionResolvers将被忽略。
	 * 
	 */
	public void setMappedHandlerClasses(Class<?>... mappedHandlerClasses) {
		this.mappedHandlerClasses = mappedHandlerClasses;
	}

	/**
	 * Set the log category for warn logging. The name will be passed to the underlying logger
	 * implementation through Commons Logging, getting interpreted as a log category according
	 * to the logger's configuration.
	 * <p>Default is no warn logging. Specify this setting to activate warn logging into a specific
	 * category. Alternatively, override the {@link #logException} method for custom logging.
	 * <p>
	 * 设置警报日志的日志类别名称将通过Commons Logging传递到底层日志记录器实现,根据记录器的配置将其解释为日志类别<p>默认值为无警告日志记录指定此设置以激活警告日志记录到特定的类别或者替换自定
	 * 义日志记录的{@link #logException}方法。
	 * 
	 * 
	 * @see org.apache.commons.logging.LogFactory#getLog(String)
	 * @see org.apache.log4j.Logger#getLogger(String)
	 * @see java.util.logging.Logger#getLogger(String)
	 */
	public void setWarnLogCategory(String loggerName) {
		this.warnLogger = LogFactory.getLog(loggerName);
	}

	/**
	 * Specify whether to prevent HTTP response caching for any view resolved
	 * by this exception resolver.
	 * <p>Default is {@code false}. Switch this to {@code true} in order to
	 * automatically generate HTTP response headers that suppress response caching.
	 * <p>
	 *  指定是否阻止对此异常解析器解析的任何视图的HTTP响应缓存<p>默认值为{@code false}将其切换为{@code true},以便自动生成抑制响应缓存的HTTP响应头
	 * 
	 */
	public void setPreventResponseCaching(boolean preventResponseCaching) {
		this.preventResponseCaching = preventResponseCaching;
	}


	/**
	 * Check whether this resolver is supposed to apply (i.e. if the supplied handler
	 * matches any of the configured {@linkplain #setMappedHandlers handlers} or
	 * {@linkplain #setMappedHandlerClasses handler classes}), and then delegate
	 * to the {@link #doResolveException} template method.
	 * <p>
	 * 检查此解析器是否应用(即,如果提供的处理程序与任何已配置的{@linkplain #setMappedHandlers处理程序}或{@linkplain #setMappedHandlerClasses处理程序类}
	 * 匹配),然后委托给{@link #doResolveException}模板方法。
	 * 
	 */
	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
			Object handler, Exception ex) {

		if (shouldApplyTo(request, handler)) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Resolving exception from handler [" + handler + "]: " + ex);
			}
			prepareResponse(ex, response);
			ModelAndView result = doResolveException(request, response, handler, ex);
			if (result != null) {
				logException(ex, request);
			}
			return result;
		}
		else {
			return null;
		}
	}

	/**
	 * Check whether this resolver is supposed to apply to the given handler.
	 * <p>The default implementation checks against the configured
	 * {@linkplain #setMappedHandlers handlers} and
	 * {@linkplain #setMappedHandlerClasses handler classes}, if any.
	 * <p>
	 *  检查此解析器是否应用于给定的处理程序<p>默认实现检查已配置的{@linkplain #setMappedHandlers处理程序}和{@linkplain #setMappedHandlerClasses处理程序类}
	 * (如果有)。
	 * 
	 * 
	 * @param request current HTTP request
	 * @param handler the executed handler, or {@code null} if none chosen
	 * at the time of the exception (for example, if multipart resolution failed)
	 * @return whether this resolved should proceed with resolving the exception
	 * for the given request and handler
	 * @see #setMappedHandlers
	 * @see #setMappedHandlerClasses
	 */
	protected boolean shouldApplyTo(HttpServletRequest request, Object handler) {
		if (handler != null) {
			if (this.mappedHandlers != null && this.mappedHandlers.contains(handler)) {
				return true;
			}
			if (this.mappedHandlerClasses != null) {
				for (Class<?> handlerClass : this.mappedHandlerClasses) {
					if (handlerClass.isInstance(handler)) {
						return true;
					}
				}
			}
		}
		// Else only apply if there are no explicit handler mappings.
		return (this.mappedHandlers == null && this.mappedHandlerClasses == null);
	}

	/**
	 * Log the given exception at warn level, provided that warn logging has been
	 * activated through the {@link #setWarnLogCategory "warnLogCategory"} property.
	 * <p>Calls {@link #buildLogMessage} in order to determine the concrete message to log.
	 * <p>
	 *  通过{@link #setWarnLogCategory"warnLogCategory"}属性<p>调用{@link #buildLogMessage}来激活警告日志记录,以确定具体的记录日志
	 * 
	 * 
	 * @param ex the exception that got thrown during handler execution
	 * @param request current HTTP request (useful for obtaining metadata)
	 * @see #setWarnLogCategory
	 * @see #buildLogMessage
	 * @see org.apache.commons.logging.Log#warn(Object, Throwable)
	 */
	protected void logException(Exception ex, HttpServletRequest request) {
		if (this.warnLogger != null && this.warnLogger.isWarnEnabled()) {
			this.warnLogger.warn(buildLogMessage(ex, request));
		}
	}

	/**
	 * Build a log message for the given exception, occurred during processing the given request.
	 * <p>
	 * 为给定的异常构建日志消息,在处理给定的请求期间发生
	 * 
	 * 
	 * @param ex the exception that got thrown during handler execution
	 * @param request current HTTP request (useful for obtaining metadata)
	 * @return the log message to use
	 */
	protected String buildLogMessage(Exception ex, HttpServletRequest request) {
		return "Resolved exception caused by Handler execution: " + ex;
	}

	/**
	 * Prepare the response for the exceptional case.
	 * <p>The default implementation prevents the response from being cached,
	 * if the {@link #setPreventResponseCaching "preventResponseCaching"} property
	 * has been set to "true".
	 * <p>
	 *  为特殊情况准备响应<p>如果将{@link #setPreventResponseCaching"preventResponseCaching"}属性设置为"true",则默认实现会阻止响应被缓存。
	 * 
	 * 
	 * @param ex the exception that got thrown during handler execution
	 * @param response current HTTP response
	 * @see #preventCaching
	 */
	protected void prepareResponse(Exception ex, HttpServletResponse response) {
		if (this.preventResponseCaching) {
			preventCaching(response);
		}
	}

	/**
	 * Prevents the response from being cached, through setting corresponding
	 * HTTP {@code Cache-Control: no-store} header.
	 * <p>
	 *  通过设置相应的HTTP {@code Cache-Control：no-store}标头来防止响应被缓存
	 * 
	 * 
	 * @param response current HTTP response
	 */
	protected void preventCaching(HttpServletResponse response) {
		response.addHeader(HEADER_CACHE_CONTROL, "no-store");
	}


	/**
	 * Actually resolve the given exception that got thrown during handler execution,
	 * returning a {@link ModelAndView} that represents a specific error page if appropriate.
	 * <p>May be overridden in subclasses, in order to apply specific exception checks.
	 * Note that this template method will be invoked <i>after</i> checking whether this
	 * resolved applies ("mappedHandlers" etc), so an implementation may simply proceed
	 * with its actual exception handling.
	 * <p>
	 * 实际上解决在处理程序执行期间抛出的给定异常,返回一个表示特定错误页面的{@link ModelAndView},如果适用<p>可能会在子类中被覆盖,以便应用特定的异常检查请注意,此模板方法将在</i>检
	 * 查此解决是否适用("mappedHandlers"等)之后调用<i>,所以实现可以简单地继续其实际的异常处理。
	 * 
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler the executed handler, or {@code null} if none chosen at the time
	 * of the exception (for example, if multipart resolution failed)
	 * @param ex the exception that got thrown during handler execution
	 * @return a corresponding {@code ModelAndView} to forward to, or {@code null} for default processing
	 */
	protected abstract ModelAndView doResolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex);

}
