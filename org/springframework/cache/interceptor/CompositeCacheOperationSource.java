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

package org.springframework.cache.interceptor;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.util.Assert;

/**
 * Composite {@link CacheOperationSource} implementation that iterates
 * over a given array of {@code CacheOperationSource} instances.
 *
 * <p>
 *  复合{@link CacheOperationSource}实现,它遍历给定数组的{@code CacheOperationSource}实例
 * 
 * 
 * @author Costin Leau
 * @since 3.1
 */
@SuppressWarnings("serial")
public class CompositeCacheOperationSource implements CacheOperationSource, Serializable {

	private final CacheOperationSource[] cacheOperationSources;


	/**
	 * Create a new CompositeCacheOperationSource for the given sources.
	 * <p>
	 *  为给定的源创建一个新的CompositeCacheOperationSource
	 * 
	 * 
	 * @param cacheOperationSources the CacheOperationSource instances to combine
	 */
	public CompositeCacheOperationSource(CacheOperationSource... cacheOperationSources) {
		Assert.notEmpty(cacheOperationSources, "cacheOperationSources array must not be empty");
		this.cacheOperationSources = cacheOperationSources;
	}

	/**
	 * Return the {@code CacheOperationSource} instances that this
	 * {@code CompositeCacheOperationSource} combines.
	 * <p>
	 * 返回此{@code CompositeCacheOperationSource}组合的{@code CacheOperationSource}实例
	 */
	public final CacheOperationSource[] getCacheOperationSources() {
		return this.cacheOperationSources;
	}

	@Override
	public Collection<CacheOperation> getCacheOperations(Method method, Class<?> targetClass) {
		Collection<CacheOperation> ops = null;

		for (CacheOperationSource source : this.cacheOperationSources) {
			Collection<CacheOperation> cacheOperations = source.getCacheOperations(method, targetClass);
			if (cacheOperations != null) {
				if (ops == null) {
					ops = new ArrayList<CacheOperation>();
				}

				ops.addAll(cacheOperations);
			}
		}
		return ops;
	}
}
