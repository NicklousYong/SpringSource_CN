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

package org.springframework.web.servlet.resource;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.webjars.MultipleMatchesException;
import org.webjars.WebJarAssetLocator;

import org.springframework.core.io.Resource;

/**
 * A {@code ResourceResolver} that delegates to the chain to locate a resource and then
 * attempts to find a matching versioned resource contained in a WebJar JAR file.
 *
 * <p>This allows WebJars.org users to write version agnostic paths in their templates,
 * like {@code <script src="/jquery/jquery.min.js"/>}.
 * This path will be resolved to the unique version {@code <script src="/jquery/1.2.0/jquery.min.js"/>},
 * which is a better fit for HTTP caching and version management in applications.
 *
 * <p>This also resolves resources for version agnostic HTTP requests {@code "GET /jquery/jquery.min.js"}.
 *
 * <p>This resolver requires the "org.webjars:webjars-locator" library on classpath,
 * and is automatically registered if that library is present.
 *
 * <p>
 *  委托给链的{@code ResourceResolver}来定位资源,然后尝试找到包含在WebJar JAR文件中的匹配版本化资源
 * 
 * <p>这允许WebJarsorg用户在其模板中编写版本不可知路径,例如{@code <script src ="/ jquery / jqueryminjs"/>}此路径将被解析为唯一版本{@code <script src =" / jquery / 120 / jqueryminjs"/>}
 * ,这更适合应用程序中的HTTP缓存和版本管理。
 * 
 *  <p>这也解决了版本不可知HTTP请求的资源{@code"GET / jquery / jqueryminjs"}
 * 
 *  <p>此解析器需要在类路径上使用"orgwebjars：webjars-locator"库,如果该库存在,则会自动注册
 * 
 * @author Brian Clozel
 * @since 4.2
 * @see org.springframework.web.servlet.config.annotation.ResourceChainRegistration
 * @see <a href="http://www.webjars.org">webjars.org</a>
 */
public class WebJarsResourceResolver extends AbstractResourceResolver {

	private final static String WEBJARS_LOCATION = "META-INF/resources/webjars/";

	private final static int WEBJARS_LOCATION_LENGTH = WEBJARS_LOCATION.length();


	private final WebJarAssetLocator webJarAssetLocator;


	/**
	 * Create a {@code WebJarsResourceResolver} with a default {@code WebJarAssetLocator} instance.
	 * <p>
	 * 
	 */
	public WebJarsResourceResolver() {
		this(new WebJarAssetLocator());
	}

	/**
	 * Create a {@code WebJarsResourceResolver} with a custom {@code WebJarAssetLocator} instance,
	 * e.g. with a custom index.
	 * <p>
	 *  使用默认的{@code WebJarAssetLocator}实例创建一个{@code WebJarsResourceResolver}
	 * 
	 * 
	 * @since 4.3
	 */
	public WebJarsResourceResolver(WebJarAssetLocator webJarAssetLocator) {
		this.webJarAssetLocator = webJarAssetLocator;
	}


	@Override
	protected Resource resolveResourceInternal(HttpServletRequest request, String requestPath,
			List<? extends Resource> locations, ResourceResolverChain chain) {

		Resource resolved = chain.resolveResource(request, requestPath, locations);
		if (resolved == null) {
			String webJarResourcePath = findWebJarResourcePath(requestPath);
			if (webJarResourcePath != null) {
				return chain.resolveResource(request, webJarResourcePath, locations);
			}
		}
		return resolved;
	}

	@Override
	protected String resolveUrlPathInternal(String resourceUrlPath,
			List<? extends Resource> locations, ResourceResolverChain chain) {

		String path = chain.resolveUrlPath(resourceUrlPath, locations);
		if (path == null) {
			String webJarResourcePath = findWebJarResourcePath(resourceUrlPath);
			if (webJarResourcePath != null) {
				return chain.resolveUrlPath(webJarResourcePath, locations);
			}
		}
		return path;
	}

	protected String findWebJarResourcePath(String path) {
		try {
			int startOffset = (path.startsWith("/") ? 1 : 0);
			int endOffset = path.indexOf("/", 1);
			if (endOffset != -1) {
				String webjar = path.substring(startOffset, endOffset);
				String partialPath = path.substring(endOffset);
				String webJarPath = webJarAssetLocator.getFullPath(webjar, partialPath);
				return webJarPath.substring(WEBJARS_LOCATION_LENGTH);
			}
		}
		catch (MultipleMatchesException ex) {
			if (logger.isWarnEnabled()) {
				logger.warn("WebJar version conflict for \"" + path + "\"", ex);
			}
		}
		catch (IllegalArgumentException ex) {
			if (logger.isTraceEnabled()) {
				logger.trace("No WebJar resource found for \"" + path + "\"");
			}
		}
		return null;
	}

}
