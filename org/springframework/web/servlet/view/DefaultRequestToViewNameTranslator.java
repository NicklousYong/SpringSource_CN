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

package org.springframework.web.servlet.view;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.RequestToViewNameTranslator;
import org.springframework.web.util.UrlPathHelper;

/**
 * {@link RequestToViewNameTranslator} that simply transforms the URI of
 * the incoming request into a view name.
 *
 * <p>Can be explicitly defined as the {@code viewNameTranslator} bean in a
 * {@link org.springframework.web.servlet.DispatcherServlet} context.
 * Otherwise, a plain default instance will be used.
 *
 * <p>The default transformation simply strips leading and trailing slashes
 * as well as the file extension of the URI, and returns the result as the
 * view name with the configured {@link #setPrefix prefix} and a
 * {@link #setSuffix suffix} added as appropriate.
 *
 * <p>The stripping of the leading slash and file extension can be disabled
 * using the {@link #setStripLeadingSlash stripLeadingSlash} and
 * {@link #setStripExtension stripExtension} properties, respectively.
 *
 * <p>Find below some examples of request to view name translation.
 * <ul>
 * <li>{@code http://localhost:8080/gamecast/display.html} &raquo; {@code display}</li>
 * <li>{@code http://localhost:8080/gamecast/displayShoppingCart.html} &raquo; {@code displayShoppingCart}</li>
 * <li>{@code http://localhost:8080/gamecast/admin/index.html} &raquo; {@code admin/index}</li>
 * </ul>
 *
 * <p>
 *  {@link RequestToViewNameTranslator},简单地将传入请求的URI转换为视图名称
 * 
 * <p>可以在{@link orgspringframeworkwebservletDispatcherServlet}上下文中明确定义为{@code viewNameTranslator} bean否则
 * ,将使用一个普通的默认实例。
 * 
 *  <p>默认变换只是前导和尾部斜杠以及URI的文件扩展名,并返回结果作为视图名称与配置的{@link #setPrefix前缀}并添加了{@link #setSuffix后缀}作为适当的
 * 
 *  <p>可以使用{@link #setStripLeadingSlash stripLeadingSlash}和{@link #setStripExtension stripExtension}属性来禁
 * 用前导斜杠和文件扩展名的剥离。
 * 
 *  <p>在下面的一些示例中,查看名称翻译的请求
 * <ul>
 * <li> {@ code http：// localhost：8080 / gamecast / displayhtml}&raquo; {@code display} </li> <li> {@ code http：// localhost：8080 / gamecast / displayShoppingCarthtml}
 * &raquo; {@code displayShoppingCart} </li> <li> {@ code http：// localhost：8080 / gamecast / admin / indexhtml}
 * &raquo; {@code admin / index} </li>。
 * </ul>
 * 
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 * @see org.springframework.web.servlet.RequestToViewNameTranslator
 * @see org.springframework.web.servlet.ViewResolver
 */
public class DefaultRequestToViewNameTranslator implements RequestToViewNameTranslator {

	private static final String SLASH = "/";


	private String prefix = "";

	private String suffix = "";

	private String separator = SLASH;

	private boolean stripLeadingSlash = true;

	private boolean stripTrailingSlash = true;

	private boolean stripExtension = true;

	private UrlPathHelper urlPathHelper = new UrlPathHelper();


	/**
	 * Set the prefix to prepend to generated view names.
	 * <p>
	 *  设置前缀以生成视图名称
	 * 
	 * 
	 * @param prefix the prefix to prepend to generated view names
	 */
	public void setPrefix(String prefix) {
		this.prefix = (prefix != null ? prefix : "");
	}

	/**
	 * Set the suffix to append to generated view names.
	 * <p>
	 *  设置后缀附加到生成的视图名称
	 * 
	 * 
	 * @param suffix the suffix to append to generated view names
	 */
	public void setSuffix(String suffix) {
		this.suffix = (suffix != null ? suffix : "");
	}

	/**
	 * Set the value that will replace '{@code /}' as the separator
	 * in the view name. The default behavior simply leaves '{@code /}'
	 * as the separator.
	 * <p>
	 *  设置将在视图名称中替换"{@code /}"作为分隔符的值默认行为只是将"{@code /}"作为分隔符
	 * 
	 */
	public void setSeparator(String separator) {
		this.separator = separator;
	}

	/**
	 * Set whether or not leading slashes should be stripped from the URI when
	 * generating the view name. Default is "true".
	 * <p>
	 *  设置在生成视图名称时是否从URI中删除前导斜杠默认值为"true"
	 * 
	 */
	public void setStripLeadingSlash(boolean stripLeadingSlash) {
		this.stripLeadingSlash = stripLeadingSlash;
	}

	/**
	 * Set whether or not trailing slashes should be stripped from the URI when
	 * generating the view name. Default is "true".
	 * <p>
	 *  设置在生成视图名称时是否从URI中删除尾部斜杠默认值为"true"
	 * 
	 */
	public void setStripTrailingSlash(boolean stripTrailingSlash) {
		this.stripTrailingSlash = stripTrailingSlash;
	}

	/**
	 * Set whether or not file extensions should be stripped from the URI when
	 * generating the view name. Default is "true".
	 * <p>
	 * 设置生成视图名称时是否从URI中删除文件扩展名默认值为"true"
	 * 
	 */
	public void setStripExtension(boolean stripExtension) {
		this.stripExtension = stripExtension;
	}

	/**
	 * Set if URL lookup should always use the full path within the current servlet
	 * context. Else, the path within the current servlet mapping is used
	 * if applicable (i.e. in the case of a ".../*" servlet mapping in web.xml).
	 * Default is "false".
	 * <p>
	 *  设置URL查找是否应始终使用当前servlet上下文中的完整路径Else,如果适用,则使用当前servlet映射中的路径(即在webxml中使用"/ *"servlet映射)的情况下,默认为"fals
	 * e"。
	 * 
	 * 
	 * @see org.springframework.web.util.UrlPathHelper#setAlwaysUseFullPath
	 */
	public void setAlwaysUseFullPath(boolean alwaysUseFullPath) {
		this.urlPathHelper.setAlwaysUseFullPath(alwaysUseFullPath);
	}

	/**
	 * Set if the context path and request URI should be URL-decoded.
	 * Both are returned <i>undecoded</i> by the Servlet API,
	 * in contrast to the servlet path.
	 * <p>Uses either the request encoding or the default encoding according
	 * to the Servlet spec (ISO-8859-1).
	 * <p>
	 *  设置上下文路径和请求URI是否应进行URL解码Servlet API与Servlet API相反返回<i>未解码</i> <p>使用请求编码或默认编码根据Servlet规范(ISO-8859-1)
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
	 * Set the {@link org.springframework.web.util.UrlPathHelper} to use for
	 * the resolution of lookup paths.
	 * <p>Use this to override the default UrlPathHelper with a custom subclass,
	 * or to share common UrlPathHelper settings across multiple web components.
	 * <p>
	 * 将{@link orgspringframeworkwebutilUrlPathHelper}设置为用于查找路径的解析<p>使用此方法可以使用自定义子类覆盖默认的UrlPathHelper,或者在多个W
	 * eb组件之间共享通用的UrlPathHelper设置。
	 * 
	 */
	public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
		Assert.notNull(urlPathHelper, "UrlPathHelper must not be null");
		this.urlPathHelper = urlPathHelper;
	}


	/**
	 * Translates the request URI of the incoming {@link HttpServletRequest}
	 * into the view name based on the configured parameters.
	 * <p>
	 *  根据配置的参数将传入的{@link HttpServletRequest}的请求URI转换为视图名称
	 * 
	 * 
	 * @see org.springframework.web.util.UrlPathHelper#getLookupPathForRequest
	 * @see #transformPath
	 */
	@Override
	public String getViewName(HttpServletRequest request) {
		String lookupPath = this.urlPathHelper.getLookupPathForRequest(request);
		return (this.prefix + transformPath(lookupPath) + this.suffix);
	}

	/**
	 * Transform the request URI (in the context of the webapp) stripping
	 * slashes and extensions, and replacing the separator as required.
	 * <p>
	 *  转换请求URI(在webapp的上下文中)剥离斜杠和扩展名,并根据需要替换分隔符
	 * 
	 * @param lookupPath the lookup path for the current request,
	 * as determined by the UrlPathHelper
	 * @return the transformed path, with slashes and extensions stripped
	 * if desired
	 */
	protected String transformPath(String lookupPath) {
		String path = lookupPath;
		if (this.stripLeadingSlash && path.startsWith(SLASH)) {
			path = path.substring(1);
		}
		if (this.stripTrailingSlash && path.endsWith(SLASH)) {
			path = path.substring(0, path.length() - 1);
		}
		if (this.stripExtension) {
			path = StringUtils.stripFilenameExtension(path);
		}
		if (!SLASH.equals(this.separator)) {
			path = StringUtils.replace(path, SLASH, this.separator);
		}
		return path;
	}

}
