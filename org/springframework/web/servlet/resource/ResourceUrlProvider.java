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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.util.UrlPathHelper;

/**
 * A central component to use to obtain the public URL path that clients should
 * use to access a static resource.
 *
 * <p>This class is aware of Spring MVC handler mappings used to serve static
 * resources and uses the {@code ResourceResolver} chains of the configured
 * {@code ResourceHttpRequestHandler}s to make its decisions.
 *
 * <p>
 *  用于获取客户端应用于访问静态资源的公共URL路径的中心组件
 * 
 * <p>此类意识到用于提供静态资源的Spring MVC处理程序映射,并使用配置的{@code ResourceHttpRequestHandler}的{@code ResourceResolver}链做
 * 出决定。
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 4.1
 */
public class ResourceUrlProvider implements ApplicationListener<ContextRefreshedEvent> {

	protected final Log logger = LogFactory.getLog(getClass());

	private UrlPathHelper urlPathHelper = new UrlPathHelper();

	private PathMatcher pathMatcher = new AntPathMatcher();

	private final Map<String, ResourceHttpRequestHandler> handlerMap = new LinkedHashMap<String, ResourceHttpRequestHandler>();

	private boolean autodetect = true;


	/**
	 * Configure a {@code UrlPathHelper} to use in
	 * {@link #getForRequestUrl(javax.servlet.http.HttpServletRequest, String)}
	 * in order to derive the lookup path for a target request URL path.
	 * <p>
	 *  在{@link #getForRequestUrl(javaxservlethttpHttpServletRequest,String)}中配置{@code UrlPathHelper},以便导出目标
	 * 请求URL路径的查找路径。
	 * 
	 */
	public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
		this.urlPathHelper = urlPathHelper;
	}

	/**
	 * Return the configured {@code UrlPathHelper}.
	 * <p>
	 *  返回配置的{@code UrlPathHelper}
	 * 
	 * 
	 * @since 4.2.8
	 */
	public UrlPathHelper getUrlPathHelper() {
		return this.urlPathHelper;
	}

	/**
	/* <p>
	/* 
	 * @deprecated as of Spring 4.2.8, in favor of {@link #getUrlPathHelper}
	 */
	@Deprecated
	public UrlPathHelper getPathHelper() {
		return this.urlPathHelper;
	}

	/**
	 * Configure a {@code PathMatcher} to use when comparing target lookup path
	 * against resource mappings.
	 * <p>
	 *  在将目标查找路径与资源映射进行比较时,请配置{@code PathMatcher}
	 * 
	 */
	public void setPathMatcher(PathMatcher pathMatcher) {
		this.pathMatcher = pathMatcher;
	}

	/**
	 * Return the configured {@code PathMatcher}.
	 * <p>
	 *  返回配置的{@code PathMatcher}
	 * 
	 */
	public PathMatcher getPathMatcher() {
		return this.pathMatcher;
	}

	/**
	 * Manually configure the resource mappings.
	 * <p><strong>Note:</strong> by default resource mappings are auto-detected
	 * from the Spring {@code ApplicationContext}. However if this property is
	 * used, the auto-detection is turned off.
	 * <p>
	 * 手动配置资源映射<p> <strong>注意：</strong>默认情况下,资源映射将从Spring {@code ApplicationContext}自动检测。
	 * 但是,如果使用此属性,则自动检测被关闭。
	 * 
	 */
	public void setHandlerMap(Map<String, ResourceHttpRequestHandler> handlerMap) {
		if (handlerMap != null) {
			this.handlerMap.clear();
			this.handlerMap.putAll(handlerMap);
			this.autodetect = false;
		}
	}

	/**
	 * Return the resource mappings, either manually configured or auto-detected
	 * when the Spring {@code ApplicationContext} is refreshed.
	 * <p>
	 *  当Spring {@code ApplicationContext}刷新时,返回资源映射,手动配置或自动检测
	 * 
	 */
	public Map<String, ResourceHttpRequestHandler> getHandlerMap() {
		return this.handlerMap;
	}

	/**
	 * Return {@code false} if resource mappings were manually configured,
	 * {@code true} otherwise.
	 * <p>
	 *  如果手动配置资源映射,则返回{@code false},否则返回{@code true}
	 * 
	 */
	public boolean isAutodetect() {
		return this.autodetect;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (isAutodetect()) {
			this.handlerMap.clear();
			detectResourceHandlers(event.getApplicationContext());
			if (this.handlerMap.isEmpty() && logger.isDebugEnabled()) {
				logger.debug("No resource handling mappings found");
			}
			if (!this.handlerMap.isEmpty()) {
				this.autodetect = false;
			}
		}
	}


	protected void detectResourceHandlers(ApplicationContext appContext) {
		logger.debug("Looking for resource handler mappings");

		Map<String, SimpleUrlHandlerMapping> map = appContext.getBeansOfType(SimpleUrlHandlerMapping.class);
		List<SimpleUrlHandlerMapping> handlerMappings = new ArrayList<SimpleUrlHandlerMapping>(map.values());
		AnnotationAwareOrderComparator.sort(handlerMappings);

		for (SimpleUrlHandlerMapping hm : handlerMappings) {
			for (String pattern : hm.getHandlerMap().keySet()) {
				Object handler = hm.getHandlerMap().get(pattern);
				if (handler instanceof ResourceHttpRequestHandler) {
					ResourceHttpRequestHandler resourceHandler = (ResourceHttpRequestHandler) handler;
					if (logger.isDebugEnabled()) {
						logger.debug("Found resource handler mapping: URL pattern=\"" + pattern + "\", " +
								"locations=" + resourceHandler.getLocations() + ", " +
								"resolvers=" + resourceHandler.getResourceResolvers());
					}
					this.handlerMap.put(pattern, resourceHandler);
				}
			}
		}
	}

	/**
	 * A variation on {@link #getForLookupPath(String)} that accepts a full request
	 * URL path (i.e. including context and servlet path) and returns the full request
	 * URL path to expose for public use.
	 * <p>
	 *  接受完整请求URL路径(即包含上下文和servlet路径)的{@link #getForLookupPath(String)}的变体,并返回完整的请求URL路径以供公开使用
	 * 
	 * 
	 * @param request the current request
	 * @param requestUrl the request URL path to resolve
	 * @return the resolved public URL path, or {@code null} if unresolved
	 */
	public final String getForRequestUrl(HttpServletRequest request, String requestUrl) {
		if (logger.isTraceEnabled()) {
			logger.trace("Getting resource URL for request URL \"" + requestUrl + "\"");
		}
		int prefixIndex = getLookupPathIndex(request);
		int suffixIndex = getQueryParamsIndex(requestUrl);
		String prefix = requestUrl.substring(0, prefixIndex);
		String suffix = requestUrl.substring(suffixIndex);
		String lookupPath = requestUrl.substring(prefixIndex, suffixIndex);
		String resolvedLookupPath = getForLookupPath(lookupPath);
		return (resolvedLookupPath != null ? prefix + resolvedLookupPath + suffix : null);
	}

	private int getLookupPathIndex(HttpServletRequest request) {
		UrlPathHelper pathHelper = getUrlPathHelper();
		String requestUri = pathHelper.getRequestUri(request);
		String lookupPath = pathHelper.getLookupPathForRequest(request);
		return requestUri.indexOf(lookupPath);
	}

	private int getQueryParamsIndex(String lookupPath) {
		int index = lookupPath.indexOf("?");
		return index > 0 ? index : lookupPath.length();
	}

	/**
	 * Compare the given path against configured resource handler mappings and
	 * if a match is found use the {@code ResourceResolver} chain of the matched
	 * {@code ResourceHttpRequestHandler} to resolve the URL path to expose for
	 * public use.
	 * <p>It is expected that the given path is what Spring MVC would use for
	 * request mapping purposes, i.e. excluding context and servlet path portions.
	 * <p>If several handler mappings match, the handler used will be the one
	 * configured with the most specific pattern.
	 * <p>
	 * 将给定路径与配置的资源处理程序映射进行比较,如果找到匹配项,则使用匹配的{@code ResourceHttpRequestHandler}的{@code ResourceResolver}链来解析公开
	 * 用于公开的URL路径<p>预期给定的路径是Spring MVC将用于请求映射的目的,即排除上下文和servlet路径部分<p>如果几个处理程序映射匹配,则使用的处理程序将是配置为最特定模式的处理程序。
	 * 
	 * @param lookupPath the lookup path to check
	 * @return the resolved public URL path, or {@code null} if unresolved
	 */
	public final String getForLookupPath(String lookupPath) {
		if (logger.isTraceEnabled()) {
			logger.trace("Getting resource URL for lookup path \"" + lookupPath + "\"");
		}

		List<String> matchingPatterns = new ArrayList<String>();
		for (String pattern : this.handlerMap.keySet()) {
			if (getPathMatcher().match(pattern, lookupPath)) {
				matchingPatterns.add(pattern);
			}
		}

		if (!matchingPatterns.isEmpty()) {
			Comparator<String> patternComparator = getPathMatcher().getPatternComparator(lookupPath);
			Collections.sort(matchingPatterns, patternComparator);
			for (String pattern : matchingPatterns) {
				String pathWithinMapping = getPathMatcher().extractPathWithinPattern(pattern, lookupPath);
				String pathMapping = lookupPath.substring(0, lookupPath.indexOf(pathWithinMapping));
				if (logger.isTraceEnabled()) {
					logger.trace("Invoking ResourceResolverChain for URL pattern \"" + pattern + "\"");
				}
				ResourceHttpRequestHandler handler = this.handlerMap.get(pattern);
				ResourceResolverChain chain = new DefaultResourceResolverChain(handler.getResourceResolvers());
				String resolved = chain.resolveUrlPath(pathWithinMapping, handler.getLocations());
				if (resolved == null) {
					continue;
				}
				if (logger.isTraceEnabled()) {
					logger.trace("Resolved public resource URL path \"" + resolved + "\"");
				}
				return pathMapping + resolved;
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("No matching resource mapping for lookup path \"" + lookupPath + "\"");
		}
		return null;
	}

}
