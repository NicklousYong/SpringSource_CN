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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerMapping;

/**
 * Simple {@code Controller} implementation that transforms the virtual
 * path of a URL into a view name and returns that view.
 *
 * <p>Can optionally prepend a {@link #setPrefix prefix} and/or append a
 * {@link #setSuffix suffix} to build the viewname from the URL filename.
 *
 * <p>Find some examples below:
 * <ol>
 * <li>{@code "/index" -> "index"}</li>
 * <li>{@code "/index.html" -> "index"}</li>
 * <li>{@code "/index.html"} + prefix {@code "pre_"} and suffix {@code "_suf" -> "pre_index_suf"}</li>
 * <li>{@code "/products/view.html" -> "products/view"}</li>
 * </ol>
 *
 * <p>Thanks to David Barri for suggesting prefix/suffix support!
 *
 * <p>
 *  简单的{@code控制器}实现,将URL的虚拟路径转换为视图名称并返回该视图
 * 
 * <p>可以选择前缀{@link #setPrefix前缀}和/或附加{@link #setSuffix后缀}以从URL文件名构建视图名称
 * 
 *  <p>查找以下示例：
 * <ol>
 *  <li> {@ code"/ index" - >"index"} </li> <li> {@ code"/ indexhtml" - >"index"} </li> <li> {@ code"/ indexhtml" }
 * }前缀{@code"pre_"}和后缀{@code"_suf" - >"pre_index_suf"} </li> <li> {@ code"/ products / viewhtml" - >"products / view"}
 *  < LI>。
 * </ol>
 * 
 *  感谢David Barri建议前缀/后缀支持！
 * 
 * 
 * @author Alef Arendsen
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @see #setPrefix
 * @see #setSuffix
 */
public class UrlFilenameViewController extends AbstractUrlViewController {

	private String prefix = "";

	private String suffix = "";

	/** Request URL path String --> view name String */
	private final Map<String, String> viewNameCache = new ConcurrentHashMap<String, String>(256);


	/**
	 * Set the prefix to prepend to the request URL filename
	 * to build a view name.
	 * <p>
	 *  将前缀设置为前缀到请求URL文件名以构建视图名称
	 * 
	 */
	public void setPrefix(String prefix) {
		this.prefix = (prefix != null ? prefix : "");
	}

	/**
	 * Return the prefix to prepend to the request URL filename.
	 * <p>
	 *  返回前缀到前缀到请求URL文件名
	 * 
	 */
	protected String getPrefix() {
		return this.prefix;
	}

	/**
	 * Set the suffix to append to the request URL filename
	 * to build a view name.
	 * <p>
	 *  设置后缀附加到请求URL文件名以构建视图名称
	 * 
	 */
	public void setSuffix(String suffix) {
		this.suffix = (suffix != null ? suffix : "");
	}

	/**
	 * Return the suffix to append to the request URL filename.
	 * <p>
	 *  返回后缀附加到请求URL文件名
	 * 
	 */
	protected String getSuffix() {
		return this.suffix;
	}


	/**
	 * Returns view name based on the URL filename,
	 * with prefix/suffix applied when appropriate.
	 * <p>
	 * 根据URL文件名返回视图名称,适当时应用前缀/后缀
	 * 
	 * 
	 * @see #extractViewNameFromUrlPath
	 * @see #setPrefix
	 * @see #setSuffix
	 */
	@Override
	protected String getViewNameForRequest(HttpServletRequest request) {
		String uri = extractOperableUrl(request);
		return getViewNameForUrlPath(uri);
	}

	/**
	 * Extract a URL path from the given request,
	 * suitable for view name extraction.
	 * <p>
	 *  从给定的请求中提取URL路径,适用于查看名称提取
	 * 
	 * 
	 * @param request current HTTP request
	 * @return the URL to use for view name extraction
	 */
	protected String extractOperableUrl(HttpServletRequest request) {
		String urlPath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		if (!StringUtils.hasText(urlPath)) {
			urlPath = getUrlPathHelper().getLookupPathForRequest(request);
		}
		return urlPath;
	}

	/**
	 * Returns view name based on the URL filename,
	 * with prefix/suffix applied when appropriate.
	 * <p>
	 *  根据URL文件名返回视图名称,适当时应用前缀/后缀
	 * 
	 * 
	 * @param uri the request URI; for example {@code "/index.html"}
	 * @return the extracted URI filename; for example {@code "index"}
	 * @see #extractViewNameFromUrlPath
	 * @see #postProcessViewName
	 */
	protected String getViewNameForUrlPath(String uri) {
		String viewName = this.viewNameCache.get(uri);
		if (viewName == null) {
			viewName = extractViewNameFromUrlPath(uri);
			viewName = postProcessViewName(viewName);
			this.viewNameCache.put(uri, viewName);
		}
		return viewName;
	}

	/**
	 * Extract the URL filename from the given request URI.
	 * <p>
	 *  从给定的请求URI中提取URL文件名
	 * 
	 * 
	 * @param uri the request URI; for example {@code "/index.html"}
	 * @return the extracted URI filename; for example {@code "index"}
	 */
	protected String extractViewNameFromUrlPath(String uri) {
		int start = (uri.charAt(0) == '/' ? 1 : 0);
		int lastIndex = uri.lastIndexOf(".");
		int end = (lastIndex < 0 ? uri.length() : lastIndex);
		return uri.substring(start, end);
	}

	/**
	 * Build the full view name based on the given view name
	 * as indicated by the URL path.
	 * <p>The default implementation simply applies prefix and suffix.
	 * This can be overridden, for example, to manipulate upper case
	 * / lower case, etc.
	 * <p>
	 *  根据URL路径指定的给定视图名称构建完整视图名称<p>默认实现仅适用前缀和后缀可以覆盖,例如,操作大写/小写字母等
	 * 
	 * @param viewName the original view name, as indicated by the URL path
	 * @return the full view name to use
	 * @see #getPrefix()
	 * @see #getSuffix()
	 */
	protected String postProcessViewName(String viewName) {
		return getPrefix() + viewName + getSuffix();
	}

}
