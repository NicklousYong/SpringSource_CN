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

package org.springframework.cache.annotation;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.cache.interceptor.AbstractFallbackCacheOperationSource;
import org.springframework.cache.interceptor.CacheOperation;
import org.springframework.util.Assert;

/**
 * Implementation of the {@link org.springframework.cache.interceptor.CacheOperationSource
 * CacheOperationSource} interface for working with caching metadata in annotation format.
 *
 * <p>This class reads Spring's {@link Cacheable}, {@link CachePut} and {@link CacheEvict}
 * annotations and exposes corresponding caching operation definition to Spring's cache
 * infrastructure. This class may also serve as base class for a custom
 * {@code CacheOperationSource}.
 *
 * <p>
 *  实现使用注释格式缓存元数据的{@link orgspringframeworkcacheinterceptorCacheOperationSource CacheOperationSource}接口。
 * 
 * <p>此类读取Spring的{@link Cacheable},{@link CachePut}和{@link CacheEvict}注释,并将相应的缓存操作定义公开到Spring的缓存基础架构此类也可
 * 以作为自定义{@code CacheOperationSource }。
 * 
 * 
 * @author Costin Leau
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 * @since 3.1
 */
@SuppressWarnings("serial")
public class AnnotationCacheOperationSource extends AbstractFallbackCacheOperationSource
		implements Serializable {

	private final boolean publicMethodsOnly;

	private final Set<CacheAnnotationParser> annotationParsers;


	/**
	 * Create a default AnnotationCacheOperationSource, supporting public methods
	 * that carry the {@code Cacheable} and {@code CacheEvict} annotations.
	 * <p>
	 *  创建一个默认的AnnotationCacheOperationSource,支持携带{@code Cacheable}和{@code CacheEvict}注释的公共方法
	 * 
	 */
	public AnnotationCacheOperationSource() {
		this(true);
	}

	/**
	 * Create a default {@code AnnotationCacheOperationSource}, supporting public methods
	 * that carry the {@code Cacheable} and {@code CacheEvict} annotations.
	 * <p>
	 *  创建一个默认的{@code AnnotationCacheOperationSource},支持携带{@code Cacheable}和{@code CacheEvict}注释的公共方法
	 * 
	 * 
	 * @param publicMethodsOnly whether to support only annotated public methods
	 * typically for use with proxy-based AOP), or protected/private methods as well
	 * (typically used with AspectJ class weaving)
	 */
	public AnnotationCacheOperationSource(boolean publicMethodsOnly) {
		this.publicMethodsOnly = publicMethodsOnly;
		this.annotationParsers = new LinkedHashSet<CacheAnnotationParser>(1);
		this.annotationParsers.add(new SpringCacheAnnotationParser());
	}

	/**
	 * Create a custom AnnotationCacheOperationSource.
	 * <p>
	 *  创建自定义AnnotationCacheOperationSource
	 * 
	 * 
	 * @param annotationParser the CacheAnnotationParser to use
	 */
	public AnnotationCacheOperationSource(CacheAnnotationParser annotationParser) {
		this.publicMethodsOnly = true;
		Assert.notNull(annotationParser, "CacheAnnotationParser must not be null");
		this.annotationParsers = Collections.singleton(annotationParser);
	}

	/**
	 * Create a custom AnnotationCacheOperationSource.
	 * <p>
	 *  创建自定义AnnotationCacheOperationSource
	 * 
	 * 
	 * @param annotationParsers the CacheAnnotationParser to use
	 */
	public AnnotationCacheOperationSource(CacheAnnotationParser... annotationParsers) {
		this.publicMethodsOnly = true;
		Assert.notEmpty(annotationParsers, "At least one CacheAnnotationParser needs to be specified");
		Set<CacheAnnotationParser> parsers = new LinkedHashSet<CacheAnnotationParser>(annotationParsers.length);
		Collections.addAll(parsers, annotationParsers);
		this.annotationParsers = parsers;
	}

	/**
	 * Create a custom AnnotationCacheOperationSource.
	 * <p>
	 *  创建自定义AnnotationCacheOperationSource
	 * 
	 * 
	 * @param annotationParsers the CacheAnnotationParser to use
	 */
	public AnnotationCacheOperationSource(Set<CacheAnnotationParser> annotationParsers) {
		this.publicMethodsOnly = true;
		Assert.notEmpty(annotationParsers, "At least one CacheAnnotationParser needs to be specified");
		this.annotationParsers = annotationParsers;
	}


	@Override
	protected Collection<CacheOperation> findCacheOperations(final Class<?> clazz) {
		return determineCacheOperations(new CacheOperationProvider() {
			@Override
			public Collection<CacheOperation> getCacheOperations(CacheAnnotationParser parser) {
				return parser.parseCacheAnnotations(clazz);
			}
		});

	}

	@Override
	protected Collection<CacheOperation> findCacheOperations(final Method method) {
		return determineCacheOperations(new CacheOperationProvider() {
			@Override
			public Collection<CacheOperation> getCacheOperations(CacheAnnotationParser parser) {
				return parser.parseCacheAnnotations(method);
			}
		});
	}

	/**
	 * Determine the cache operation(s) for the given {@link CacheOperationProvider}.
	 * <p>This implementation delegates to configured
	 * {@link CacheAnnotationParser}s for parsing known annotations into
	 * Spring's metadata attribute class.
	 * <p>Can be overridden to support custom annotations that carry
	 * caching metadata.
	 * <p>
	 * 确定给定的{@link CacheOperationProvider}的缓存操作<p>此实现委托配置的{@link CacheAnnotationParser}用于将已知注释解析为Spring的元数据属
	 * 性类<p>可以覆盖以支持携带的自定义注释缓存元数据。
	 * 
	 * 
	 * @param provider the cache operation provider to use
	 * @return the configured caching operations, or {@code null} if none found
	 */
	protected Collection<CacheOperation> determineCacheOperations(CacheOperationProvider provider) {
		Collection<CacheOperation> ops = null;
		for (CacheAnnotationParser annotationParser : this.annotationParsers) {
			Collection<CacheOperation> annOps = provider.getCacheOperations(annotationParser);
			if (annOps != null) {
				if (ops == null) {
					ops = new ArrayList<CacheOperation>();
				}
				ops.addAll(annOps);
			}
		}
		return ops;
	}

	/**
	 * By default, only public methods can be made cacheable.
	 * <p>
	 *  默认情况下,只有公共方法可以缓存
	 * 
	 */
	@Override
	protected boolean allowPublicMethodsOnly() {
		return this.publicMethodsOnly;
	}


	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof AnnotationCacheOperationSource)) {
			return false;
		}
		AnnotationCacheOperationSource otherCos = (AnnotationCacheOperationSource) other;
		return (this.annotationParsers.equals(otherCos.annotationParsers) &&
				this.publicMethodsOnly == otherCos.publicMethodsOnly);
	}

	@Override
	public int hashCode() {
		return this.annotationParsers.hashCode();
	}

	/**
	 * Callback interface providing {@link CacheOperation} instance(s) based on
	 * a given {@link CacheAnnotationParser}.
	 * <p>
	 *  基于给定的{@link CacheAnnotationParser}的回调接口提供{@link CacheOperation}实例
	 * 
	 */
	protected interface CacheOperationProvider {

		/**
		 * Returns the {@link CacheOperation} instance(s) provided by the specified parser.
		 *
		 * <p>
		 *  返回指定解析器提供的{@link CacheOperation}实例
		 * 
		 * @param parser the parser to use
		 * @return the cache operations or {@code null} if none is found
		 */
		Collection<CacheOperation> getCacheOperations(CacheAnnotationParser parser);
	}

}
