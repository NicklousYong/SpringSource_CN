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

package org.springframework.cache.jcache.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.cache.annotation.CacheDefaults;
import javax.cache.annotation.CacheKeyGenerator;
import javax.cache.annotation.CacheMethodDetails;
import javax.cache.annotation.CachePut;
import javax.cache.annotation.CacheRemove;
import javax.cache.annotation.CacheRemoveAll;
import javax.cache.annotation.CacheResolverFactory;
import javax.cache.annotation.CacheResult;

import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.util.StringUtils;

/**
 * Implementation of the {@link JCacheOperationSource} interface that reads
 * the JSR-107 {@link CacheResult}, {@link CachePut}, {@link CacheRemove} and
 * {@link CacheRemoveAll} annotations.
 *
 * <p>
 *  执行读取JSR-107 {@link CacheResult},{@link CachePut},{@link CacheRemove}和{@link CacheRemoveAll}注释的{@link JCacheOperationSource}
 * 接口。
 * 
 * 
 * @author Stephane Nicoll
 * @since 4.1
 */
public abstract class AnnotationJCacheOperationSource extends AbstractFallbackJCacheOperationSource {

	@Override
	protected JCacheOperation<?> findCacheOperation(Method method, Class<?> targetType) {
		CacheResult cacheResult = method.getAnnotation(CacheResult.class);
		CachePut cachePut = method.getAnnotation(CachePut.class);
		CacheRemove cacheRemove = method.getAnnotation(CacheRemove.class);
		CacheRemoveAll cacheRemoveAll = method.getAnnotation(CacheRemoveAll.class);

		int found = countNonNull(cacheResult, cachePut, cacheRemove, cacheRemoveAll);
		if (found == 0) {
			return null;
		}
		if (found > 1) {
			throw new IllegalStateException("More than one cache annotation found on '" + method + "'");
		}

		CacheDefaults defaults = getCacheDefaults(method, targetType);
		if (cacheResult != null) {
			return createCacheResultOperation(method, defaults, cacheResult);
		}
		else if (cachePut != null) {
			return createCachePutOperation(method, defaults, cachePut);
		}
		else if (cacheRemove != null) {
			return createCacheRemoveOperation(method, defaults, cacheRemove);
		}
		else {
			return createCacheRemoveAllOperation(method, defaults, cacheRemoveAll);
		}
	}

	protected CacheDefaults getCacheDefaults(Method method, Class<?> targetType) {
		CacheDefaults annotation = method.getDeclaringClass().getAnnotation(CacheDefaults.class);
		if (annotation != null) {
			return annotation;
		}
		return targetType.getAnnotation(CacheDefaults.class);
	}

	protected CacheResultOperation createCacheResultOperation(Method method, CacheDefaults defaults, CacheResult ann) {
		String cacheName = determineCacheName(method, defaults, ann.cacheName());
		CacheResolverFactory cacheResolverFactory =
				determineCacheResolverFactory(defaults, ann.cacheResolverFactory());
		KeyGenerator keyGenerator = determineKeyGenerator(defaults, ann.cacheKeyGenerator());

		CacheMethodDetails<CacheResult> methodDetails = createMethodDetails(method, ann, cacheName);

		CacheResolver cacheResolver = getCacheResolver(cacheResolverFactory, methodDetails);
		CacheResolver exceptionCacheResolver = null;
		final String exceptionCacheName = ann.exceptionCacheName();
		if (StringUtils.hasText(exceptionCacheName)) {
			exceptionCacheResolver = getExceptionCacheResolver(cacheResolverFactory, methodDetails);
		}

		return new CacheResultOperation(methodDetails, cacheResolver, keyGenerator, exceptionCacheResolver);
	}

	protected CachePutOperation createCachePutOperation(Method method, CacheDefaults defaults, CachePut ann) {
		String cacheName = determineCacheName(method, defaults, ann.cacheName());
		CacheResolverFactory cacheResolverFactory =
				determineCacheResolverFactory(defaults, ann.cacheResolverFactory());
		KeyGenerator keyGenerator = determineKeyGenerator(defaults, ann.cacheKeyGenerator());

		CacheMethodDetails<CachePut> methodDetails = createMethodDetails(method, ann, cacheName);
		CacheResolver cacheResolver = getCacheResolver(cacheResolverFactory, methodDetails);
		return new CachePutOperation(methodDetails, cacheResolver, keyGenerator);
	}

	protected CacheRemoveOperation createCacheRemoveOperation(Method method, CacheDefaults defaults, CacheRemove ann) {
		String cacheName = determineCacheName(method, defaults, ann.cacheName());
		CacheResolverFactory cacheResolverFactory =
				determineCacheResolverFactory(defaults, ann.cacheResolverFactory());
		KeyGenerator keyGenerator = determineKeyGenerator(defaults, ann.cacheKeyGenerator());

		CacheMethodDetails<CacheRemove> methodDetails = createMethodDetails(method, ann, cacheName);
		CacheResolver cacheResolver = getCacheResolver(cacheResolverFactory, methodDetails);
		return new CacheRemoveOperation(methodDetails, cacheResolver, keyGenerator);
	}

	protected CacheRemoveAllOperation createCacheRemoveAllOperation(Method method, CacheDefaults defaults, CacheRemoveAll ann) {
		String cacheName = determineCacheName(method, defaults, ann.cacheName());
		CacheResolverFactory cacheResolverFactory =
				determineCacheResolverFactory(defaults, ann.cacheResolverFactory());

		CacheMethodDetails<CacheRemoveAll> methodDetails = createMethodDetails(method, ann, cacheName);
		CacheResolver cacheResolver = getCacheResolver(cacheResolverFactory, methodDetails);
		return new CacheRemoveAllOperation(methodDetails, cacheResolver);
	}

	private <A extends Annotation> CacheMethodDetails<A> createMethodDetails(Method method, A annotation, String cacheName) {
		return new DefaultCacheMethodDetails<A>(method, annotation, cacheName);
	}

	protected CacheResolver getCacheResolver(CacheResolverFactory factory, CacheMethodDetails<?> details) {
		if (factory != null) {
			javax.cache.annotation.CacheResolver cacheResolver = factory.getCacheResolver(details);
			return new CacheResolverAdapter(cacheResolver);
		}
		else {
			return getDefaultCacheResolver();
		}
	}

	protected CacheResolver getExceptionCacheResolver(CacheResolverFactory factory,
			CacheMethodDetails<CacheResult> details) {

		if (factory != null) {
			javax.cache.annotation.CacheResolver cacheResolver = factory.getExceptionCacheResolver(details);
			return new CacheResolverAdapter(cacheResolver);
		}
		else {
			return getDefaultExceptionCacheResolver();
		}
	}

	protected CacheResolverFactory determineCacheResolverFactory(CacheDefaults defaults,
			Class<? extends CacheResolverFactory> candidate) {

		if (CacheResolverFactory.class != candidate) {
			return getBean(candidate);
		}
		else if (defaults != null && CacheResolverFactory.class != defaults.cacheResolverFactory()) {
			return getBean(defaults.cacheResolverFactory());
		}
		else {
			return null;
		}
	}

	protected KeyGenerator determineKeyGenerator(CacheDefaults defaults, Class<? extends CacheKeyGenerator> candidate) {
		if (CacheKeyGenerator.class != candidate) {
			return new KeyGeneratorAdapter(this, getBean(candidate));
		}
		else if (defaults != null && CacheKeyGenerator.class != defaults.cacheKeyGenerator()) {
			return new KeyGeneratorAdapter(this, getBean(defaults.cacheKeyGenerator()));
		}
		else {
			return getDefaultKeyGenerator();
		}
	}

	protected String determineCacheName(Method method, CacheDefaults defaults, String candidate) {
		if (StringUtils.hasText(candidate)) {
			return candidate;
		}
		if (defaults != null && StringUtils.hasText(defaults.cacheName())) {
			return defaults.cacheName();
		}
		return generateDefaultCacheName(method);
	}

	/**
	 * Generate a default cache name for the specified {@link Method}.
	 * <p>
	 * 为指定的{@link方法}生成默认缓存名称
	 * 
	 * 
	 * @param method the annotated method
	 * @return the default cache name, according to JSR-107
	 */
	protected String generateDefaultCacheName(Method method) {
		Class<?>[] parameterTypes = method.getParameterTypes();
		List<String> parameters = new ArrayList<String>(parameterTypes.length);
		for (Class<?> parameterType : parameterTypes) {
			parameters.add(parameterType.getName());
		}

		StringBuilder sb = new StringBuilder(method.getDeclaringClass().getName());
		sb.append(".").append(method.getName());
		sb.append("(").append(StringUtils.collectionToCommaDelimitedString(parameters)).append(")");
		return sb.toString();
	}

	private int countNonNull(Object... instances) {
		int result = 0;
		for (Object instance : instances) {
			if (instance != null) {
				result += 1;
			}
		}
		return result;
	}


	/**
	 * Locate or create an instance of the specified cache strategy {@code type}.
	 * <p>
	 *  查找或创建指定缓存策略的实例{@code type}
	 * 
	 * 
	 * @param type the type of the bean to manage
	 * @return the required bean
	 */
	protected abstract <T> T getBean(Class<T> type);

	/**
	 * Return the default {@link CacheResolver} if none is set.
	 * <p>
	 *  如果没有设置,返回默认的{@link CacheResolver}
	 * 
	 */
	protected abstract CacheResolver getDefaultCacheResolver();

	/**
	 * Return the default exception {@link CacheResolver} if none is set.
	 * <p>
	 *  如果没有设置异常,返回默认异常{@link CacheResolver}
	 * 
	 */
	protected abstract CacheResolver getDefaultExceptionCacheResolver();

	/**
	 * Return the default {@link KeyGenerator} if none is set.
	 * <p>
	 *  如果没有设置,返回默认的{@link KeyGenerator}
	 */
	protected abstract KeyGenerator getDefaultKeyGenerator();

}
