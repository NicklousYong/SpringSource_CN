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

package org.springframework.web.servlet.resource;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/**
 * A {@link org.springframework.web.servlet.resource.ResourceTransformer} that checks a
 * {@link org.springframework.cache.Cache} to see if a previously transformed resource
 * exists in the cache and returns it if found, and otherwise delegates to the resolver
 * chain and saves the result in the cache.
 *
 * <p>
 * 
 * @author Rossen Stoyanchev
 * @since 4.1
 */
public class CachingResourceTransformer implements ResourceTransformer {

	private static final Log logger = LogFactory.getLog(CachingResourceTransformer.class);

	private final Cache cache;


	public CachingResourceTransformer(CacheManager cacheManager, String cacheName) {
		this(cacheManager.getCache(cacheName));
	}

	public CachingResourceTransformer(Cache cache) {
		Assert.notNull(cache, "Cache is required");
		this.cache = cache;
	}


	/**
	 * Return the configured {@code Cache}.
	 * <p>
	 * 一个{@link orgspringframeworkwebservletresourceResourceTransformer}检查{@link orgspringframeworkcacheCache}
	 * 以查看缓存中是否存在先前转换的资源,如果找到,则返回它,否则委派给解析器链并将结果保存在缓存中。
	 * 
	 */
	public Cache getCache() {
		return this.cache;
	}


	@Override
	public Resource transform(HttpServletRequest request, Resource resource, ResourceTransformerChain transformerChain)
			throws IOException {

		Resource transformed = this.cache.get(resource, Resource.class);
		if (transformed != null) {
			if (logger.isTraceEnabled()) {
				logger.trace("Found match: " + transformed);
			}
			return transformed;
		}

		transformed = transformerChain.transform(request, resource);

		if (logger.isTraceEnabled()) {
			logger.trace("Putting transformed resource in cache: " + transformed);
		}
		this.cache.put(resource, transformed);

		return transformed;
	}

}
