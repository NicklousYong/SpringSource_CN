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

package org.springframework.web.servlet.support;

import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.jstl.core.Config;

/**
 * JSP-aware (and JSTL-aware) subclass of RequestContext, allowing for
 * population of the context from a {@code javax.servlet.jsp.PageContext}.
 *
 * <p>This context will detect a JSTL locale attribute in page/request/session/application
 * scope, in addition to the fallback locale strategy provided by the base class.
 *
 * <p>
 *  RequestContext的JSP感知(和JSTL感知)子类,允许从{@code javaxservletjspPageContext}
 * 
 * <p>除了基类提供的回退区域设置策略之外,此上下文将检测页面/请求/会话/应用程序范围中的JSTL区域设置属性
 * 
 * 
 * @author Juergen Hoeller
 * @since 1.1.4
 * @see #getFallbackLocale
 */
public class JspAwareRequestContext extends RequestContext {

	private PageContext pageContext;


	/**
	 * Create a new JspAwareRequestContext for the given page context,
	 * using the request attributes for Errors retrieval.
	 * <p>
	 *  为给定的页面上下文创建一个新的JspAwareRequestContext,使用Errors检索的请求属性
	 * 
	 * 
	 * @param pageContext current JSP page context
	 */
	public JspAwareRequestContext(PageContext pageContext) {
		initContext(pageContext, null);
	}

	/**
	 * Create a new JspAwareRequestContext for the given page context,
	 * using the given model attributes for Errors retrieval.
	 * <p>
	 *  为给定的页面上下文创建一个新的JspAwareRequestContext,使用给定的模型属性进行错误检索
	 * 
	 * 
	 * @param pageContext current JSP page context
	 * @param model the model attributes for the current view
	 * (can be {@code null}, using the request attributes for Errors retrieval)
	 */
	public JspAwareRequestContext(PageContext pageContext, Map<String, Object> model) {
		initContext(pageContext, model);
	}

	/**
	 * Initialize this context with the given page context,
	 * using the given model attributes for Errors retrieval.
	 * <p>
	 *  使用给定的页面上下文初始化此上下文,使用给定的模型属性进行错误检索
	 * 
	 * 
	 * @param pageContext current JSP page context
	 * @param model the model attributes for the current view
	 * (can be {@code null}, using the request attributes for Errors retrieval)
	 */
	protected void initContext(PageContext pageContext, Map<String, Object> model) {
		if (!(pageContext.getRequest() instanceof HttpServletRequest)) {
			throw new IllegalArgumentException("RequestContext only supports HTTP requests");
		}
		this.pageContext = pageContext;
		initContext((HttpServletRequest) pageContext.getRequest(), (HttpServletResponse) pageContext.getResponse(),
				pageContext.getServletContext(), model);
	}


	/**
	 * Return the underlying PageContext.
	 * Only intended for cooperating classes in this package.
	 * <p>
	 *  返回底层的PageContext仅适用于此包中的协作类
	 * 
	 */
	protected final PageContext getPageContext() {
		return this.pageContext;
	}

	/**
	 * This implementation checks for a JSTL locale attribute
	 * in page, request, session or application scope; if not found,
	 * returns the {@code HttpServletRequest.getLocale()}.
	 * <p>
	 *  此实现将检查页面,请求,会话或应用程序范围中的JSTL区域设置属性;如果没有找到,返回{@code HttpServletRequestgetLocale()}
	 * 
	 */
	@Override
	protected Locale getFallbackLocale() {
		if (jstlPresent) {
			Locale locale = JstlPageLocaleResolver.getJstlLocale(getPageContext());
			if (locale != null) {
				return locale;
			}
		}
		return getRequest().getLocale();
	}


	/**
	 * Inner class that isolates the JSTL dependency.
	 * Just called to resolve the fallback locale if the JSTL API is present.
	 * <p>
	 * 内部类隔离JSTL依赖关系如果JSTL API存在,则调用它来解析后备区域设置
	 */
	private static class JstlPageLocaleResolver {

		public static Locale getJstlLocale(PageContext pageContext) {
			Object localeObject = Config.find(pageContext, Config.FMT_LOCALE);
			return (localeObject instanceof Locale ? (Locale) localeObject : null);
		}
	}

}
