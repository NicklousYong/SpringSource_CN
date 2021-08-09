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

package org.springframework.cache.interceptor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.MethodClassKey;
import org.springframework.util.ClassUtils;

/**
 * Abstract implementation of {@link CacheOperation} that caches attributes
 * for methods and implements a fallback policy: 1. specific target method;
 * 2. target class; 3. declaring method; 4. declaring class/interface.
 *
 * <p>Defaults to using the target class's caching attribute if none is
 * associated with the target method. Any caching attribute associated with
 * the target method completely overrides a class caching attribute.
 * If none found on the target class, the interface that the invoked method
 * has been called through (in case of a JDK proxy) will be checked.
 *
 * <p>This implementation caches attributes by method after they are first
 * used. If it is ever desirable to allow dynamic changing of cacheable
 * attributes (which is very unlikely), caching could be made configurable.
 *
 * <p>
 *  抽象实现{@link CacheOperation},缓存方法的属性并实现后备策略：1特定目标方法; 2目标班; 3申报方法; 4声明类/接口
 * 
 * <p>如果没有与目标方法相关联,则默认使用目标类的缓存属性与目标方法相关联的任何缓存属性完全覆盖类缓存属性如果目标类没有找到调用方法的接口,通过(在JDK代理的情况下)将被检查
 * 
 *  <p>此实现在首次使用后通过方法缓存属性如果希望允许动态更改可缓存属性(这不太可能),缓存可以配置
 * 
 * 
 * @author Costin Leau
 * @author Juergen Hoeller
 * @since 3.1
 */
public abstract class AbstractFallbackCacheOperationSource implements CacheOperationSource {

	/**
	 * Canonical value held in cache to indicate no caching attribute was
	 * found for this method and we don't need to look again.
	 * <p>
	 *  在缓存中保存的规范值表示没有缓存属性被找到这种方法,我们不需要再看
	 * 
	 */
	private final static Collection<CacheOperation> NULL_CACHING_ATTRIBUTE = Collections.emptyList();


	/**
	 * Logger available to subclasses.
	 * <p>As this base class is not marked Serializable, the logger will be recreated
	 * after serialization - provided that the concrete subclass is Serializable.
	 * <p>
	 * Logger可用于子类<p>由于此基类没有标记为Serializable,所以在序列化后将重新创建记录器 - 只要具体的子类是Serializable
	 * 
	 */
	protected final Log logger = LogFactory.getLog(getClass());

	/**
	 * Cache of CacheOperations, keyed by method on a specific target class.
	 * <p>As this base class is not marked Serializable, the cache will be recreated
	 * after serialization - provided that the concrete subclass is Serializable.
	 * <p>
	 *  CacheOperations缓存,由特定目标类上的方法键入<p>由于此基类没有标记为Serializable,所以缓存将在序列化后重新创建 - 只要具体的子类是Serializable
	 * 
	 */
	private final Map<Object, Collection<CacheOperation>> attributeCache =
			new ConcurrentHashMap<Object, Collection<CacheOperation>>(1024);


	/**
	 * Determine the caching attribute for this method invocation.
	 * <p>Defaults to the class's caching attribute if no method attribute is found.
	 * <p>
	 *  确定此方法调用的缓存属性<p>如果没有找到方法属性,则默认为类的缓存属性
	 * 
	 * 
	 * @param method the method for the current invocation (never {@code null})
	 * @param targetClass the target class for this invocation (may be {@code null})
	 * @return {@link CacheOperation} for this method, or {@code null} if the method
	 * is not cacheable
	 */
	@Override
	public Collection<CacheOperation> getCacheOperations(Method method, Class<?> targetClass) {
		Object cacheKey = getCacheKey(method, targetClass);
		Collection<CacheOperation> cached = this.attributeCache.get(cacheKey);

		if (cached != null) {
			return (cached != NULL_CACHING_ATTRIBUTE ? cached : null);
		}
		else {
			Collection<CacheOperation> cacheOps = computeCacheOperations(method, targetClass);
			if (cacheOps != null) {
				if (logger.isDebugEnabled()) {
					logger.debug("Adding cacheable method '" + method.getName() + "' with attribute: " + cacheOps);
				}
				this.attributeCache.put(cacheKey, cacheOps);
			}
			else {
				this.attributeCache.put(cacheKey, NULL_CACHING_ATTRIBUTE);
			}
			return cacheOps;
		}
	}

	/**
	 * Determine a cache key for the given method and target class.
	 * <p>Must not produce same key for overloaded methods.
	 * Must produce same key for different instances of the same method.
	 * <p>
	 *  确定给定方法和目标类的缓存密钥<p>不能为重载方法生成相同的密钥必须为相同方法的不同实例生成相同的密钥
	 * 
	 * 
	 * @param method the method (never {@code null})
	 * @param targetClass the target class (may be {@code null})
	 * @return the cache key (never {@code null})
	 */
	protected Object getCacheKey(Method method, Class<?> targetClass) {
		return new MethodClassKey(method, targetClass);
	}

	private Collection<CacheOperation> computeCacheOperations(Method method, Class<?> targetClass) {
		// Don't allow no-public methods as required.
		if (allowPublicMethodsOnly() && !Modifier.isPublic(method.getModifiers())) {
			return null;
		}

		// The method may be on an interface, but we need attributes from the target class.
		// If the target class is null, the method will be unchanged.
		Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
		// If we are dealing with method with generic parameters, find the original method.
		specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);

		// First try is the method in the target class.
		Collection<CacheOperation> opDef = findCacheOperations(specificMethod);
		if (opDef != null) {
			return opDef;
		}

		// Second try is the caching operation on the target class.
		opDef = findCacheOperations(specificMethod.getDeclaringClass());
		if (opDef != null && ClassUtils.isUserLevelMethod(method)) {
			return opDef;
		}

		if (specificMethod != method) {
			// Fallback is to look at the original method.
			opDef = findCacheOperations(method);
			if (opDef != null) {
				return opDef;
			}
			// Last fallback is the class of the original method.
			opDef = findCacheOperations(method.getDeclaringClass());
			if (opDef != null && ClassUtils.isUserLevelMethod(method)) {
				return opDef;
			}
		}

		return null;
	}


	/**
	 * Subclasses need to implement this to return the caching attribute
	 * for the given method, if any.
	 * <p>
	 * 子类需要实现这一点,以返回给定方法的缓存属性(如果有的话)
	 * 
	 * 
	 * @param method the method to retrieve the attribute for
	 * @return all caching attribute associated with this method
	 * (or {@code null} if none)
	 */
	protected abstract Collection<CacheOperation> findCacheOperations(Method method);

	/**
	 * Subclasses need to implement this to return the caching attribute
	 * for the given class, if any.
	 * <p>
	 *  子类需要实现这一点,以返回给定类的缓存属性(如果有的话)
	 * 
	 * 
	 * @param clazz the class to retrieve the attribute for
	 * @return all caching attribute associated with this class
	 * (or {@code null} if none)
	 */
	protected abstract Collection<CacheOperation> findCacheOperations(Class<?> clazz);

	/**
	 * Should only public methods be allowed to have caching semantics?
	 * <p>The default implementation returns {@code false}.
	 * <p>
	 *  是否只允许公共方法具有缓存语义? <p>默认实现返回{@code false}
	 */
	protected boolean allowPublicMethodsOnly() {
		return false;
	}

}
