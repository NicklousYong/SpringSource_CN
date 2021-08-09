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

package org.springframework.web.servlet.mvc.multiaction;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.util.Assert;
import org.springframework.web.util.UrlPathHelper;

/**
 * Abstract base class for URL-based {@link MethodNameResolver} implementations.
 *
 * <p>Provides infrastructure for mapping handlers to URLs and configurable
 * URL lookup. For information on the latter, see the
 * {@link #setAlwaysUseFullPath} "alwaysUseFullPath"}
 * and {@link #setUrlDecode "urlDecode"} properties.
 *
 * <p>
 *  基于URL的{@link MethodNameResolver}实现的抽象基类
 * 
 * <p>提供将处理程序映射到URL和可配置URL查找的基础设施有关后者的信息,请参阅{@link #setAlwaysUseFullPath}"alwaysUseFullPath"}和{@link #setUrlDecode"urlDecode"}
 * 属性。
 * 
 * 
 * @author Juergen Hoeller
 * @since 14.01.2004
 * @deprecated as of 4.3, in favor of annotation-driven handler methods
 */
@Deprecated
public abstract class AbstractUrlMethodNameResolver implements MethodNameResolver {

	/** Logger available to subclasses */
	protected final Log logger = LogFactory.getLog(getClass());

	private UrlPathHelper urlPathHelper = new UrlPathHelper();


	/**
	 * Set if URL lookup should always use full path within current servlet
	 * context. Else, the path within the current servlet mapping is used
	 * if applicable (i.e. in the case of a ".../*" servlet mapping in web.xml).
	 * Default is "false".
	 * <p>
	 *  设置URL查找是否应始终使用当前servlet上下文中的完整路径否则,如果适用,则使用当前servlet映射中的路径(即在webxml中使用"/ *"servlet映射)的情况下,默认为"false"
	 * 。
	 * 
	 * 
	 * @see org.springframework.web.util.UrlPathHelper#setAlwaysUseFullPath
	 */
	public void setAlwaysUseFullPath(boolean alwaysUseFullPath) {
		this.urlPathHelper.setAlwaysUseFullPath(alwaysUseFullPath);
	}

	/**
	 * Set if context path and request URI should be URL-decoded.
	 * Both are returned <i>undecoded</i> by the Servlet API,
	 * in contrast to the servlet path.
	 * <p>Uses either the request encoding or the default encoding according
	 * to the Servlet spec (ISO-8859-1).
	 * <p>
	 *  设置上下文路径和请求URI是否应进行URL解码Servlet API返回<i>未解码</i>与servlet路径相反<p>根据Servlet使用请求编码或默认编码规格(ISO-8859-1)
	 * 
	 * 
	 * @see org.springframework.web.util.UrlPathHelper#setUrlDecode
	 */
	public void setUrlDecode(boolean urlDecode) {
		this.urlPathHelper.setUrlDecode(urlDecode);
	}

	/**
	 * Set the UrlPathHelper to use for resolution of lookup paths.
	 * <p>Use this to override the default UrlPathHelper with a custom subclass,
	 * or to share common UrlPathHelper settings across multiple MethodNameResolvers
	 * and HandlerMappings.
	 * <p>
	 * 将UrlPathHelper设置为用于解析查找路径<p>使用此方法可以使用自定义子类覆盖默认的UrlPathHelper,或者在多个MethodNameResolvers和HandlerMappings
	 * 之间共享通用的UrlPathHelper设置。
	 * 
	 * 
	 * @see org.springframework.web.servlet.handler.AbstractUrlHandlerMapping#setUrlPathHelper
	 */
	public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
		Assert.notNull(urlPathHelper, "UrlPathHelper must not be null");
		this.urlPathHelper = urlPathHelper;
	}


	/**
	 * Retrieves the URL path to use for lookup and delegates to
	 * {@code getHandlerMethodNameForUrlPath}.
	 * Converts {@code null} values to NoSuchRequestHandlingMethodExceptions.
	 * <p>
	 *  检索用于查找的URL路径并委托{@code getHandlerMethodNameForUrlPath}将{@code null}值转换为NoSuchRequestHandlingMethodExc
	 * eptions。
	 * 
	 * 
	 * @see #getHandlerMethodNameForUrlPath
	 */
	@Override
	public final String getHandlerMethodName(HttpServletRequest request)
			throws NoSuchRequestHandlingMethodException {

		String urlPath = this.urlPathHelper.getLookupPathForRequest(request);
		String name = getHandlerMethodNameForUrlPath(urlPath);
		if (name == null) {
			throw new NoSuchRequestHandlingMethodException(urlPath, request.getMethod(), request.getParameterMap());
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Returning handler method name '" + name + "' for lookup path: " + urlPath);
		}
		return name;
	}

	/**
	 * Return a method name that can handle this request, based on the
	 * given lookup path. Called by {@code getHandlerMethodName}.
	 * <p>
	 * 
	 * @param urlPath the URL path to use for lookup,
	 * according to the settings in this class
	 * @return a method name that can handle this request.
	 * Should return null if no matching method found.
	 * @see #getHandlerMethodName
	 * @see #setAlwaysUseFullPath
	 * @see #setUrlDecode
	 */
	protected abstract String getHandlerMethodNameForUrlPath(String urlPath);

}
