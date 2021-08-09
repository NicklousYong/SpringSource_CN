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

package org.springframework.web.servlet.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.UrlPathHelper;

/**
 * Abstract base class for {@code Controllers} that return a view name
 * based on the request URL.
 *
 * <p>Provides infrastructure for determining view names from URLs and configurable
 * URL lookup. For information on the latter, see {@code alwaysUseFullPath}
 * and {@code urlDecode} properties.
 *
 * <p>
 *  {@code控制器}的抽象基类,根据请求URL返回视图名称
 * 
 * <p>提供从URL和可配置URL查找中查看名称的基础设施有关后者的信息,请参阅{@code alwaysUseFullPath}和{@code urlDecode}属性
 * 
 * 
 * @author Juergen Hoeller
 * @since 1.2.6
 * @see #setAlwaysUseFullPath
 * @see #setUrlDecode
 */
public abstract class AbstractUrlViewController extends AbstractController {

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
	 * Set if ";" (semicolon) content should be stripped from the request URI.
	 * <p>
	 *  设置如果";" (分号)内容应从请求URI中删除
	 * 
	 * 
	 * @see org.springframework.web.util.UrlPathHelper#setRemoveSemicolonContent(boolean)
	 */
	public void setRemoveSemicolonContent(boolean removeSemicolonContent) {
		this.urlPathHelper.setRemoveSemicolonContent(removeSemicolonContent);
	}

	/**
	 * Set the UrlPathHelper to use for the resolution of lookup paths.
	 * <p>Use this to override the default UrlPathHelper with a custom subclass,
	 * or to share common UrlPathHelper settings across multiple MethodNameResolvers
	 * and HandlerMappings.
	 * <p>
	 * 设置UrlPathHelper用于查找路径的解析<p>使用此方法可以使用自定义子类覆盖默认的UrlPathHelper,或者在多个MethodNameResolvers和HandlerMappings之
	 * 间共享通用的UrlPathHelper设置。
	 * 
	 * 
	 * @see org.springframework.web.servlet.handler.AbstractUrlHandlerMapping#setUrlPathHelper
	 */
	public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
		Assert.notNull(urlPathHelper, "UrlPathHelper must not be null");
		this.urlPathHelper = urlPathHelper;
	}

	/**
	 * Return the UrlPathHelper to use for the resolution of lookup paths.
	 * <p>
	 *  返回UrlPathHelper以用于查找路径的分辨率
	 * 
	 */
	protected UrlPathHelper getUrlPathHelper() {
		return this.urlPathHelper;
	}


	/**
	 * Retrieves the URL path to use for lookup and delegates to
	 * {@link #getViewNameForRequest}. Also adds the content of
	 * {@link RequestContextUtils#getInputFlashMap} to the model.
	 * <p>
	 *  检索用于查找的URL路径并委托{@link #getViewNameForRequest}将{@link RequestContextUtils#getInputFlashMap}的内容添加到模型中。
	 * 
	 */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) {
		String lookupPath = getUrlPathHelper().getLookupPathForRequest(request);
		String viewName = getViewNameForRequest(request);
		if (logger.isDebugEnabled()) {
			logger.debug("Returning view name '" + viewName + "' for lookup path [" + lookupPath + "]");
		}
		return new ModelAndView(viewName, RequestContextUtils.getInputFlashMap(request));
	}

	/**
	 * Return the name of the view to render for this request, based on the
	 * given lookup path. Called by {@link #handleRequestInternal}.
	 * <p>
	 *  根据给定的查找路径返回要为此请求呈现的视图的名称,由{@link #handleRequestInternal}调用
	 * 
	 * @param request current HTTP request
	 * @return a view name for this request (never {@code null})
	 * @see #handleRequestInternal
	 * @see #setAlwaysUseFullPath
	 * @see #setUrlDecode
	 */
	protected abstract String getViewNameForRequest(HttpServletRequest request);

}
