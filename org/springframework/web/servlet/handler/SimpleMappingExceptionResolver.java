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

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

/**
 * {@link org.springframework.web.servlet.HandlerExceptionResolver} implementation
 * that allows for mapping exception class names to view names, either for a set of
 * given handlers or for all handlers in the DispatcherServlet.
 *
 * <p>Error views are analogous to error page JSPs, but can be used with any kind of
 * exception including any checked one, with fine-granular mappings for specific handlers.
 *
 * <p>
 *  {@link orgspringframeworkwebservletHandlerExceptionResolver}实现,允许映射异常类名来查看一组给定的处理程序或DispatcherServle
 * t中的所有处理程序的名称。
 * 
 * <p>错误视图类似于错误页面JSP,但可以用于任何类型的异常,包括任何已检查的异常,并针对特定的处理程序进行精细的映射
 * 
 * 
 * @author Juergen Hoeller
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @since 22.11.2003
 * @see org.springframework.web.servlet.DispatcherServlet
 */
public class SimpleMappingExceptionResolver extends AbstractHandlerExceptionResolver {

	/** The default name of the exception attribute: "exception". */
	public static final String DEFAULT_EXCEPTION_ATTRIBUTE = "exception";


	private Properties exceptionMappings;

	private Class<?>[] excludedExceptions;

	private String defaultErrorView;

	private Integer defaultStatusCode;

	private Map<String, Integer> statusCodes = new HashMap<String, Integer>();

	private String exceptionAttribute = DEFAULT_EXCEPTION_ATTRIBUTE;


	/**
	 * Set the mappings between exception class names and error view names.
	 * The exception class name can be a substring, with no wildcard support at present.
	 * A value of "ServletException" would match {@code javax.servlet.ServletException}
	 * and subclasses, for example.
	 * <p><b>NB:</b> Consider carefully how
	 * specific the pattern is, and whether to include package information (which isn't mandatory).
	 * For example, "Exception" will match nearly anything, and will probably hide other rules.
	 * "java.lang.Exception" would be correct if "Exception" was meant to define a rule for all
	 * checked exceptions. With more unusual exception names such as "BaseBusinessException"
	 * there's no need to use a FQN.
	 * <p>
	 * 设置异常类名和错误视图名之间的映射异常类名称可以是一个子字符串,目前没有通配符支持"ServletException"的值将匹配{@code javaxservletServletException}和
	 * 子类,例如<p> <b>注意：</b>仔细考虑模式的具体含义,以及是否包括软件包信息(不是强制性的)例如,"异常"几乎会匹配任何东西,并且可能会隐藏其他规则"javalangException"将是正确
	 * 的如果"异常"意味着为所有检查的异常定义规则使用诸如"BaseBusinessException"之类的异常异常名称,则无需使用FQN。
	 * 
	 * 
	 * @param mappings exception patterns (can also be fully qualified class names) as keys,
	 * and error view names as values
	 */
	public void setExceptionMappings(Properties mappings) {
		this.exceptionMappings = mappings;
	}

	/**
	 * Set one or more exceptions to be excluded from the exception mappings.
	 * Excluded exceptions are checked first and if one of them equals the actual
	 * exception, the exception will remain unresolved.
	 * <p>
	 * 设置要从异常映射中排除的一个或多个异常排除的异常首先检查,如果其中一个异常等于实际异常,则异常将保持未解决
	 * 
	 * 
	 * @param excludedExceptions one or more excluded exception types
	 */
	public void setExcludedExceptions(Class<?>... excludedExceptions) {
		this.excludedExceptions = excludedExceptions;
	}

	/**
	 * Set the name of the default error view.
	 * This view will be returned if no specific mapping was found.
	 * <p>Default is none.
	 * <p>
	 *  设置默认错误视图的名称如果没有发现特定的映射,将返回此视图<p>默认值为none
	 * 
	 */
	public void setDefaultErrorView(String defaultErrorView) {
		this.defaultErrorView = defaultErrorView;
	}

	/**
	 * Set the HTTP status code that this exception resolver will apply for a given
	 * resolved error view. Keys are view names; values are status codes.
	 * <p>Note that this error code will only get applied in case of a top-level request.
	 * It will not be set for an include request, since the HTTP status cannot be modified
	 * from within an include.
	 * <p>If not specified, the default status code will be applied.
	 * <p>
	 *  设置此异常解析器将应用于给定解析的错误视图的HTTP状态代码键是视图名称;值是状态代码<p>请注意,此错误代码仅适用于顶级请求的情况。
	 * 不会为包含请求设置此错误代码,因为HTTP状态不能从include <p>中修改指定,将应用默认状态代码。
	 * 
	 * 
	 * @see #setDefaultStatusCode(int)
	 */
	public void setStatusCodes(Properties statusCodes) {
		for (Enumeration<?> enumeration = statusCodes.propertyNames(); enumeration.hasMoreElements();) {
			String viewName = (String) enumeration.nextElement();
			Integer statusCode = Integer.valueOf(statusCodes.getProperty(viewName));
			this.statusCodes.put(viewName, statusCode);
		}
	}

	/**
	 * An alternative to {@link #setStatusCodes(Properties)} for use with
	 * Java-based configuration.
	 * <p>
	 * 用于使用基于Java的配置的{@link #setStatusCodes(Properties)}的替代方法
	 * 
	 */
	public void addStatusCode(String viewName, int statusCode) {
		this.statusCodes.put(viewName, statusCode);
	}

	/**
	 * Returns the HTTP status codes provided via {@link #setStatusCodes(Properties)}.
	 * Keys are view names; values are status codes.
	 * <p>
	 *  返回通过{@link #setStatusCodes(Properties)}提供的HTTP状态代码}键是视图名称;值是状态码
	 * 
	 */
	public Map<String, Integer> getStatusCodesAsMap() {
		return Collections.unmodifiableMap(statusCodes);
	}

	/**
	 * Set the default HTTP status code that this exception resolver will apply
	 * if it resolves an error view and if there is no status code mapping defined.
	 * <p>Note that this error code will only get applied in case of a top-level request.
	 * It will not be set for an include request, since the HTTP status cannot be modified
	 * from within an include.
	 * <p>If not specified, no status code will be applied, either leaving this to the
	 * controller or view, or keeping the servlet engine's default of 200 (OK).
	 * <p>
	 *  如果解决错误视图并且没有定义状态代码映射,则设置此异常解析器将应用的默认HTTP状态代码<p>请注意,此错误代码仅适用于顶级请求的情况不会被设置为包含请求,因为HTTP状态不能从include <p>
	 * 中修改如果未指定,则不会应用状态代码,将其留给控制器或视图,或保持servlet引擎的默认值为200(好)。
	 * 
	 * 
	 * @param defaultStatusCode HTTP status code value, for example 500
	 * ({@link HttpServletResponse#SC_INTERNAL_SERVER_ERROR}) or 404 ({@link HttpServletResponse#SC_NOT_FOUND})
	 * @see #setStatusCodes(Properties)
	 */
	public void setDefaultStatusCode(int defaultStatusCode) {
		this.defaultStatusCode = defaultStatusCode;
	}

	/**
	 * Set the name of the model attribute as which the exception should be exposed.
	 * Default is "exception".
	 * <p>This can be either set to a different attribute name or to {@code null}
	 * for not exposing an exception attribute at all.
	 * <p>
	 * 设置要暴露异常的模型属性的名称默认为"异常"<p>可以将其设置为不同的属性名称,也可以设置为{@code null},以便不显示异常属性
	 * 
	 * 
	 * @see #DEFAULT_EXCEPTION_ATTRIBUTE
	 */
	public void setExceptionAttribute(String exceptionAttribute) {
		this.exceptionAttribute = exceptionAttribute;
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
	 * 
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler the executed handler, or {@code null} if none chosen at the time
	 * of the exception (for example, if multipart resolution failed)
	 * @param ex the exception that got thrown during handler execution
	 * @return a corresponding ModelAndView to forward to, or {@code null} for default processing
	 */
	@Override
	protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response,
			Object handler, Exception ex) {

		// Expose ModelAndView for chosen error view.
		String viewName = determineViewName(ex, request);
		if (viewName != null) {
			// Apply HTTP status code for error views, if specified.
			// Only apply it if we're processing a top-level request.
			Integer statusCode = determineStatusCode(request, viewName);
			if (statusCode != null) {
				applyStatusCodeIfPossible(request, response, statusCode);
			}
			return getModelAndView(viewName, ex, request);
		}
		else {
			return null;
		}
	}

	/**
	 * Determine the view name for the given exception, first checking against the
	 * {@link #setExcludedExceptions(Class[]) "excludedExecptions"}, then searching the
	 * {@link #setExceptionMappings "exceptionMappings"}, and finally using the
	 * {@link #setDefaultErrorView "defaultErrorView"} as a fallback.
	 * <p>
	 * 确定给定异常的视图名称,首先检查{@link #setExcludedExceptions(Class [])"excludedExecptions"},然后搜索{@link #setExceptionMappings"exceptionMappings"}
	 * ,最后使用{@link #setDefaultErrorView "defaultErrorView"}作为回退。
	 * 
	 * 
	 * @param ex the exception that got thrown during handler execution
	 * @param request current HTTP request (useful for obtaining metadata)
	 * @return the resolved view name, or {@code null} if excluded or none found
	 */
	protected String determineViewName(Exception ex, HttpServletRequest request) {
		String viewName = null;
		if (this.excludedExceptions != null) {
			for (Class<?> excludedEx : this.excludedExceptions) {
				if (excludedEx.equals(ex.getClass())) {
					return null;
				}
			}
		}
		// Check for specific exception mappings.
		if (this.exceptionMappings != null) {
			viewName = findMatchingViewName(this.exceptionMappings, ex);
		}
		// Return default error view else, if defined.
		if (viewName == null && this.defaultErrorView != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Resolving to default view '" + this.defaultErrorView + "' for exception of type [" +
						ex.getClass().getName() + "]");
			}
			viewName = this.defaultErrorView;
		}
		return viewName;
	}

	/**
	 * Find a matching view name in the given exception mappings.
	 * <p>
	 *  在给定的异常映射中找到匹配的视图名称
	 * 
	 * 
	 * @param exceptionMappings mappings between exception class names and error view names
	 * @param ex the exception that got thrown during handler execution
	 * @return the view name, or {@code null} if none found
	 * @see #setExceptionMappings
	 */
	protected String findMatchingViewName(Properties exceptionMappings, Exception ex) {
		String viewName = null;
		String dominantMapping = null;
		int deepest = Integer.MAX_VALUE;
		for (Enumeration<?> names = exceptionMappings.propertyNames(); names.hasMoreElements();) {
			String exceptionMapping = (String) names.nextElement();
			int depth = getDepth(exceptionMapping, ex);
			if (depth >= 0 && (depth < deepest || (depth == deepest &&
					dominantMapping != null && exceptionMapping.length() > dominantMapping.length()))) {
				deepest = depth;
				dominantMapping = exceptionMapping;
				viewName = exceptionMappings.getProperty(exceptionMapping);
			}
		}
		if (viewName != null && logger.isDebugEnabled()) {
			logger.debug("Resolving to view '" + viewName + "' for exception of type [" + ex.getClass().getName() +
					"], based on exception mapping [" + dominantMapping + "]");
		}
		return viewName;
	}

	/**
	 * Return the depth to the superclass matching.
	 * <p>0 means ex matches exactly. Returns -1 if there's no match.
	 * Otherwise, returns depth. Lowest depth wins.
	 * <p>
	 *  将深度返回到超类匹配<p> 0意味着ex匹配精确返回-1如果没有匹配否则返回深度最低深度赢
	 * 
	 */
	protected int getDepth(String exceptionMapping, Exception ex) {
		return getDepth(exceptionMapping, ex.getClass(), 0);
	}

	private int getDepth(String exceptionMapping, Class<?> exceptionClass, int depth) {
		if (exceptionClass.getName().contains(exceptionMapping)) {
			// Found it!
			return depth;
		}
		// If we've gone as far as we can go and haven't found it...
		if (exceptionClass == Throwable.class) {
			return -1;
		}
		return getDepth(exceptionMapping, exceptionClass.getSuperclass(), depth + 1);
	}

	/**
	 * Determine the HTTP status code to apply for the given error view.
	 * <p>The default implementation returns the status code for the given view name (specified through the
	 * {@link #setStatusCodes(Properties) statusCodes} property), or falls back to the
	 * {@link #setDefaultStatusCode defaultStatusCode} if there is no match.
	 * <p>Override this in a custom subclass to customize this behavior.
	 * <p>
	 * 确定要应用于给定错误视图的HTTP状态代码<p>默认实现返回给定视图名称的状态代码(通过{@link #setStatusCodes(Properties)statusCodes}属性指定),或者返回到
	 * { @link #setDefaultStatusCode defaultStatusCode}如果没有匹配<p>在自定义子类中覆盖此值以自定义此行为。
	 * 
	 * 
	 * @param request current HTTP request
	 * @param viewName the name of the error view
	 * @return the HTTP status code to use, or {@code null} for the servlet container's default
	 * (200 in case of a standard error view)
	 * @see #setDefaultStatusCode
	 * @see #applyStatusCodeIfPossible
	 */
	protected Integer determineStatusCode(HttpServletRequest request, String viewName) {
		if (this.statusCodes.containsKey(viewName)) {
			return this.statusCodes.get(viewName);
		}
		return this.defaultStatusCode;
	}

	/**
	 * Apply the specified HTTP status code to the given response, if possible (that is,
	 * if not executing within an include request).
	 * <p>
	 *  如果可能,将指定的HTTP状态代码应用于给定的响应(即,如果不在include请求中执行)
	 * 
	 * 
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param statusCode the status code to apply
	 * @see #determineStatusCode
	 * @see #setDefaultStatusCode
	 * @see HttpServletResponse#setStatus
	 */
	protected void applyStatusCodeIfPossible(HttpServletRequest request, HttpServletResponse response, int statusCode) {
		if (!WebUtils.isIncludeRequest(request)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Applying HTTP status code " + statusCode);
			}
			response.setStatus(statusCode);
			request.setAttribute(WebUtils.ERROR_STATUS_CODE_ATTRIBUTE, statusCode);
		}
	}

	/**
	 * Return a ModelAndView for the given request, view name and exception.
	 * <p>The default implementation delegates to {@link #getModelAndView(String, Exception)}.
	 * <p>
	 *  返回给定请求的ModelAndView,查看名称和异常<p>默认实现委托给{@link #getModelAndView(String,Exception)}
	 * 
	 * 
	 * @param viewName the name of the error view
	 * @param ex the exception that got thrown during handler execution
	 * @param request current HTTP request (useful for obtaining metadata)
	 * @return the ModelAndView instance
	 */
	protected ModelAndView getModelAndView(String viewName, Exception ex, HttpServletRequest request) {
		return getModelAndView(viewName, ex);
	}

	/**
	 * Return a ModelAndView for the given view name and exception.
	 * <p>The default implementation adds the specified exception attribute.
	 * Can be overridden in subclasses.
	 * <p>
	 * 返回给定视图名称和异常的ModelAndView <p>默认实现添加指定的异常属性可以在子类中覆盖
	 * 
	 * @param viewName the name of the error view
	 * @param ex the exception that got thrown during handler execution
	 * @return the ModelAndView instance
	 * @see #setExceptionAttribute
	 */
	protected ModelAndView getModelAndView(String viewName, Exception ex) {
		ModelAndView mv = new ModelAndView(viewName);
		if (this.exceptionAttribute != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Exposing Exception as model attribute '" + this.exceptionAttribute + "'");
			}
			mv.addObject(this.exceptionAttribute, ex);
		}
		return mv;
	}

}
