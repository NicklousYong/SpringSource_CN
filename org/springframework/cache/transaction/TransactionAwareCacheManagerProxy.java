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

package org.springframework.cache.transaction;

import java.util.Collection;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.util.Assert;

/**
 * Proxy for a target {@link CacheManager}, exposing transaction-aware {@link Cache} objects
 * which synchronize their {@link Cache#put} operations with Spring-managed transactions
 * (through Spring's {@link org.springframework.transaction.support.TransactionSynchronizationManager},
 * performing the actual cache put operation only in the after-commit phase of a successful transaction.
 * If no transaction is active, {@link Cache#put} operations will be performed immediately, as usual.
 *
 * <p>
 * 针对目标的代理{@link CacheManager},将事件感知的{@link Cache}对象暴露给Spring管理的事务(通过Spring的{@link orgspringframeworktransactionsupportTransactionSynchronizationManager}
 * )执行实际的缓存放置仅在成功事务的提交后阶段进行操作如果没有事务处于活动状态,则将按照惯例立即执行{@link Cache#put}操作。
 * 
 * 
 * @author Juergen Hoeller
 * @since 3.2
 * @see #setTargetCacheManager
 * @see TransactionAwareCacheDecorator
 * @see org.springframework.transaction.support.TransactionSynchronizationManager
 */
public class TransactionAwareCacheManagerProxy implements CacheManager, InitializingBean {

	private CacheManager targetCacheManager;


	/**
	 * Create a new TransactionAwareCacheManagerProxy, setting the target CacheManager
	 * through the {@link #setTargetCacheManager} bean property.
	 * <p>
	 *  创建一个新的TransactionAwareCacheManagerProxy,通过{@link #setTargetCacheManager} bean属性设置目标CacheManager
	 * 
	 */
	public TransactionAwareCacheManagerProxy() {
	}

	/**
	 * Create a new TransactionAwareCacheManagerProxy for the given target CacheManager.
	 * <p>
	 *  为给定的目标CacheManager创建一个新的TransactionAwareCacheManagerProxy
	 * 
	 * 
	 * @param targetCacheManager the target CacheManager to proxy
	 */
	public TransactionAwareCacheManagerProxy(CacheManager targetCacheManager) {
		Assert.notNull(targetCacheManager, "Target CacheManager must not be null");
		this.targetCacheManager = targetCacheManager;
	}


	/**
	 * Set the target CacheManager to proxy.
	 * <p>
	 *  将目标CacheManager设置为代理
	 */
	public void setTargetCacheManager(CacheManager targetCacheManager) {
		this.targetCacheManager = targetCacheManager;
	}

	@Override
	public void afterPropertiesSet() {
		if (this.targetCacheManager == null) {
			throw new IllegalArgumentException("Property 'targetCacheManager' is required");
		}
	}


	@Override
	public Cache getCache(String name) {
		return new TransactionAwareCacheDecorator(this.targetCacheManager.getCache(name));
	}

	@Override
	public Collection<String> getCacheNames() {
		return this.targetCacheManager.getCacheNames();
	}

}
