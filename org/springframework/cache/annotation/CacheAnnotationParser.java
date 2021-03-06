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

import java.lang.reflect.Method;
import java.util.Collection;

import org.springframework.cache.interceptor.CacheOperation;

/**
 * Strategy interface for parsing known caching annotation types.
 * {@link AnnotationCacheOperationSource} delegates to such
 * parsers for supporting specific annotation types such as Spring's own
 * {@link Cacheable}, {@link CachePut} or {@link CacheEvict}.
 *
 * <p>
 * 用于解析已知高速缓存注释类型的策略界面{@link AnnotationCacheOperationSource}委托给此类解析器,以支持特定的注释类型,如Spring自己的{@link Cacheable}
 * ,{@link CachePut}或{@link CacheEvict}。
 * 
 * 
 * @author Costin Leau
 * @author Stephane Nicoll
 * @since 3.1
 */
public interface CacheAnnotationParser {

	/**
	 * Parses the cache definition for the given class,
	 * based on a known annotation type.
	 * <p>This essentially parses a known cache annotation into Spring's
	 * metadata attribute class. Returns {@code null} if the class
	 * is not cacheable.
	 * <p>
	 *  根据已知的注释类型解析给定类的缓存定义<p>这实际上将已知的缓存注释解析为Spring的元数据属性类返回{@code null},如果类不可缓存
	 * 
	 * 
	 * @param type the annotated class
	 * @return CacheOperation the configured caching operation,
	 * or {@code null} if none was found
	 * @see AnnotationCacheOperationSource#findCacheOperations(Class)
	 */
	Collection<CacheOperation> parseCacheAnnotations(Class<?> type);

	/**
	 * Parses the cache definition for the given method,
	 * based on a known annotation type.
	 * <p>This essentially parses a known cache annotation into Spring's
	 * metadata attribute class. Returns {@code null} if the method
	 * is not cacheable.
	 * <p>
	 *  根据已知的注释类型解析给定方法的缓存定义<p>这本质上将已知的缓存注释解析为Spring的元数据属性类返回{@code null},如果该方法不可缓存
	 * 
	 * @param method the annotated method
	 * @return CacheOperation the configured caching operation,
	 * or {@code null} if none was found
	 * @see AnnotationCacheOperationSource#findCacheOperations(Method)
	 */
	Collection<CacheOperation> parseCacheAnnotations(Method method);
}
