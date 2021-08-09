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

package org.springframework.web.cors;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;
import org.springframework.web.util.UrlPathHelper;

/**
 * Provide a per request {@link CorsConfiguration} instance based on a
 * collection of {@link CorsConfiguration} mapped on path patterns.
 *
 * <p>Exact path mapping URIs (such as {@code "/admin"}) are supported
 * as well as Ant-style path patterns (such as {@code "/admin/**"}).
 *
 * <p>
 *  根据路径模式映射的{@link CorsConfiguration}集合提供每个请求{@link CorsConfiguration}实例
 * 
 * <p>支持精确路径映射URI(如{@code"/ admin"})以及Ant样式路径模式(例如{@code"/ admin / **"})
 * 
 * 
 * @author Sebastien Deleuze
 * @since 4.2
 */
public class UrlBasedCorsConfigurationSource implements CorsConfigurationSource {

	private final Map<String, CorsConfiguration> corsConfigurations = new LinkedHashMap<String, CorsConfiguration>();

	private PathMatcher pathMatcher = new AntPathMatcher();

	private UrlPathHelper urlPathHelper = new UrlPathHelper();


	/**
	 * Set the PathMatcher implementation to use for matching URL paths
	 * against registered URL patterns. Default is AntPathMatcher.
	 * <p>
	 *  设置PathMatcher实现用于匹配URL路径与注册的URL模式默认为AntPathMatcher
	 * 
	 * 
	 * @see org.springframework.util.AntPathMatcher
	 */
	public void setPathMatcher(PathMatcher pathMatcher) {
		Assert.notNull(pathMatcher, "PathMatcher must not be null");
		this.pathMatcher = pathMatcher;
	}

	/**
	 * Set if URL lookup should always use the full path within the current servlet
	 * context. Else, the path within the current servlet mapping is used if applicable
	 * (that is, in the case of a ".../*" servlet mapping in web.xml).
	 * <p>Default is "false".
	 * <p>
	 *  设置URL查找是否应始终使用当前servlet上下文中的完整路径Else,如果适用,则使用当前servlet映射中的路径(即,在webxml中为"/ *"servlet映射的情况)<p>默认值是"假"
	 * 。
	 * 
	 * 
	 * @see org.springframework.web.util.UrlPathHelper#setAlwaysUseFullPath
	 */
	public void setAlwaysUseFullPath(boolean alwaysUseFullPath) {
		this.urlPathHelper.setAlwaysUseFullPath(alwaysUseFullPath);
	}

	/**
	 * Set if context path and request URI should be URL-decoded. Both are returned
	 * <i>undecoded</i> by the Servlet API, in contrast to the servlet path.
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
	 * <p>The default value is {@code true}.
	 * <p>
	 * 设置如果";" (分号)内容应从请求URI中删除<p>默认值为{@code true}
	 * 
	 * 
	 * @see org.springframework.web.util.UrlPathHelper#setRemoveSemicolonContent(boolean)
	 */
	public void setRemoveSemicolonContent(boolean removeSemicolonContent) {
		this.urlPathHelper.setRemoveSemicolonContent(removeSemicolonContent);
	}

	/**
	 * Set the UrlPathHelper to use for resolution of lookup paths.
	 * <p>Use this to override the default UrlPathHelper with a custom subclass.
	 * <p>
	 *  设置UrlPathHelper用于解析查找路径<p>使用此方法可以使用自定义子类来覆盖默认的UrlPathHelper
	 * 
	 */
	public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
		Assert.notNull(urlPathHelper, "UrlPathHelper must not be null");
		this.urlPathHelper = urlPathHelper;
	}

	/**
	 * Set CORS configuration based on URL patterns.
	 * <p>
	 *  根据URL模式设置CORS配置
	 * 
	 */
	public void setCorsConfigurations(Map<String, CorsConfiguration> corsConfigurations) {
		this.corsConfigurations.clear();
		if (corsConfigurations != null) {
			this.corsConfigurations.putAll(corsConfigurations);
		}
	}

	/**
	 * Get the CORS configuration.
	 * <p>
	 *  获取CORS配置
	 * 
	 */
	public Map<String, CorsConfiguration> getCorsConfigurations() {
		return Collections.unmodifiableMap(this.corsConfigurations);
	}

	/**
	 * Register a {@link CorsConfiguration} for the specified path pattern.
	 * <p>
	 *  为指定的路径模式注册{@link CorsConfiguration}
	 */
	public void registerCorsConfiguration(String path, CorsConfiguration config) {
		this.corsConfigurations.put(path, config);
	}


	@Override
	public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
		String lookupPath = this.urlPathHelper.getLookupPathForRequest(request);
		for (Map.Entry<String, CorsConfiguration> entry : this.corsConfigurations.entrySet()) {
			if (this.pathMatcher.match(entry.getKey(), lookupPath)) {
				return entry.getValue();
			}
		}
		return null;
	}

}
