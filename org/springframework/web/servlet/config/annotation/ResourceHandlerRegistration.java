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

package org.springframework.web.servlet.config.annotation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cache.Cache;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

/**
 * Encapsulates information required to create a resource handler.
 *
 * <p>
 *  封装创建资源处理程序所需的信息
 * 
 * 
 * @author Rossen Stoyanchev
 * @author Keith Donald
 * @author Brian Clozel
 * @since 3.1
 */
public class ResourceHandlerRegistration {

	private final ResourceLoader resourceLoader;

	private final String[] pathPatterns;

	private final List<Resource> locations = new ArrayList<Resource>();

	private Integer cachePeriod;

	private CacheControl cacheControl;

	private ResourceChainRegistration resourceChainRegistration;


	/**
	 * Create a {@link ResourceHandlerRegistration} instance.
	 * <p>
	 *  创建{@link ResourceHandlerRegistration}实例
	 * 
	 * 
	 * @param resourceLoader a resource loader for turning a String location into a {@link Resource}
	 * @param pathPatterns one or more resource URL path patterns
	 */
	public ResourceHandlerRegistration(ResourceLoader resourceLoader, String... pathPatterns) {
		Assert.notEmpty(pathPatterns, "At least one path pattern is required for resource handling.");
		this.resourceLoader = resourceLoader;
		this.pathPatterns = pathPatterns;
	}


	/**
	 * Add one or more resource locations from which to serve static content. Each location must point to a valid
	 * directory. Multiple locations may be specified as a comma-separated list, and the locations will be checked
	 * for a given resource in the order specified.
	 * <p>For example, {{@code "/"}, {@code "classpath:/META-INF/public-web-resources/"}} allows resources to
	 * be served both from the web application root and from any JAR on the classpath that contains a
	 * {@code /META-INF/public-web-resources/} directory, with resources in the web application root taking precedence.
	 * <p>
	 * 添加从其提供静态内容的一个或多个资源位置每个位置必须指向一个有效的目录多个位置可以指定为逗号分隔的列表,并且将按照指定的顺序检查给定资源的位置<p>对于例如,{{@code"/"},{@code"classpath：/ META-INF / public-web-resources /"}
	 * }允许从Web应用程序根和类路径上的任何JAR提供资源,包含{@code / META-INF / public-web-resources /}目录,Web应用程序根目录中的资源优先。
	 * 
	 * 
	 * @return the same {@link ResourceHandlerRegistration} instance, for chained method invocation
	 */
	public ResourceHandlerRegistration addResourceLocations(String... resourceLocations) {
		for (String location : resourceLocations) {
			this.locations.add(resourceLoader.getResource(location));
		}
		return this;
	}

	/**
	 * Specify the cache period for the resources served by the resource handler, in seconds. The default is to not
	 * send any cache headers but to rely on last-modified timestamps only. Set to 0 in order to send cache headers
	 * that prevent caching, or to a positive number of seconds to send cache headers with the given max-age value.
	 * <p>
	 * 为资源处理程序提供的资源指定高速缓存期间,以秒为单位默认为不发送任何缓存头,但仅依赖于最后修改的时间戳设置为0,以便发送缓存头,以防止缓存或正向发送具有给定max-age值的缓存头的秒数
	 * 
	 * 
	 * @param cachePeriod the time to cache resources in seconds
	 * @return the same {@link ResourceHandlerRegistration} instance, for chained method invocation
	 */
	public ResourceHandlerRegistration setCachePeriod(Integer cachePeriod) {
		this.cachePeriod = cachePeriod;
		return this;
	}

	/**
	 * Specify the {@link org.springframework.http.CacheControl} which should be used
	 * by the resource handler.
	 *
	 * <p>Setting a custom value here will override the configuration set with {@link #setCachePeriod}.
	 *
	 * <p>
	 *  指定资源处理程序应使用的{@link orgspringframeworkhttpCacheControl}
	 * 
	 *  <p>在此设置自定义值将覆盖使用{@link #setCachePeriod}的配置集
	 * 
	 * 
	 * @param cacheControl the CacheControl configuration to use
	 * @return the same {@link ResourceHandlerRegistration} instance, for chained method invocation
	 * @since 4.2
	 */
	public ResourceHandlerRegistration setCacheControl(CacheControl cacheControl) {
		this.cacheControl = cacheControl;
		return this;
	}

	/**
	 * Configure a chain of resource resolvers and transformers to use. This
	 * can be useful, for example, to apply a version strategy to resource URLs.
	 *
	 * <p>If this method is not invoked, by default only a simple
	 * {@link PathResourceResolver} is used in order to match URL paths to
	 * resources under the configured locations.
	 *
	 * <p>
	 *  配置一系列资源解析器和变压器以使用这可能是有用的,例如,将版本策略应用于资源URL
	 * 
	 * <p>如果未调用此方法,则默认情况下仅使用简单的{@link PathResourceResolver},以便将URL路径与配置位置下的资源进行匹配
	 * 
	 * 
	 * @param cacheResources whether to cache the result of resource resolution;
	 * setting this to "true" is recommended for production (and "false" for
	 * development, especially when applying a version strategy)
	 * @return the same {@link ResourceHandlerRegistration} instance, for chained method invocation
	 * @since 4.1
	 */
	public ResourceChainRegistration resourceChain(boolean cacheResources) {
		this.resourceChainRegistration = new ResourceChainRegistration(cacheResources);
		return this.resourceChainRegistration;
	}

	/**
	 * Configure a chain of resource resolvers and transformers to use. This
	 * can be useful, for example, to apply a version strategy to resource URLs.
	 *
	 * <p>If this method is not invoked, by default only a simple
	 * {@link PathResourceResolver} is used in order to match URL paths to
	 * resources under the configured locations.
	 *
	 * <p>
	 *  配置一系列资源解析器和变压器以使用这可能是有用的,例如,将版本策略应用于资源URL
	 * 
	 *  <p>如果未调用此方法,则默认情况下仅使用简单的{@link PathResourceResolver},以便将URL路径与配置位置下的资源进行匹配
	 * 
	 * 
	 * @param cacheResources whether to cache the result of resource resolution;
	 * setting this to "true" is recommended for production (and "false" for
	 * development, especially when applying a version strategy
	 * @param cache the cache to use for storing resolved and transformed resources;
	 * by default a {@link org.springframework.cache.concurrent.ConcurrentMapCache}
	 * is used. Since Resources aren't serializable and can be dependent on the
	 * application host, one should not use a distributed cache but rather an
	 * in-memory cache.
	 * @return the same {@link ResourceHandlerRegistration} instance, for chained method invocation
	 * @since 4.1
	 */
	public ResourceChainRegistration resourceChain(boolean cacheResources, Cache cache) {
		this.resourceChainRegistration = new ResourceChainRegistration(cacheResources, cache);
		return this.resourceChainRegistration;
	}

	/**
	 * Returns the URL path patterns for the resource handler.
	 * <p>
	 *  返回资源处理程序的URL路径模式
	 * 
	 */
	protected String[] getPathPatterns() {
		return this.pathPatterns;
	}

	/**
	 * Returns a {@link ResourceHttpRequestHandler} instance.
	 * <p>
	 *  返回{@link ResourceHttpRequestHandler}实例
	 */
	protected ResourceHttpRequestHandler getRequestHandler() {
		ResourceHttpRequestHandler handler = new ResourceHttpRequestHandler();
		if (this.resourceChainRegistration != null) {
			handler.setResourceResolvers(this.resourceChainRegistration.getResourceResolvers());
			handler.setResourceTransformers(this.resourceChainRegistration.getResourceTransformers());
		}
		handler.setLocations(this.locations);
		if (this.cacheControl != null) {
			handler.setCacheControl(this.cacheControl);
		}
		else if (this.cachePeriod != null) {
			handler.setCacheSeconds(this.cachePeriod);
		}
		return handler;
	}

}
