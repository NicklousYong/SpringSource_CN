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

package org.springframework.cache.transaction;

import java.util.concurrent.Callable;

import org.springframework.cache.Cache;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

/**
 * Cache decorator which synchronizes its {@link #put}, {@link #evict} and {@link #clear}
 * operations with Spring-managed transactions (through Spring's {@link TransactionSynchronizationManager},
 * performing the actual cache put/evict/clear operation only in the after-commit phase of a
 * successful transaction. If no transaction is active, {@link #put}, {@link #evict} and
 * {@link #clear} operations will be performed immediately, as usual.
 *
 * <p>Use of more aggressive operations such as {@link #putIfAbsent} cannot be deferred
 * to the after-commit phase of a running transaction. Use these with care.
 *
 * <p>
 * 缓存装饰器使用Spring管理的事务(通过Spring的{@link TransactionSynchronizationManager})将其{@link #put},{@link #evict}和{@link #clear}
 * 操作同步,执行实际的缓存放置/清除/清除操作只有在成功交易的提交后阶段如果没有事务处于活动状态,{@link #put},{@link #evict}和{@link #clear}操作将立即执行,像往常
 * 一样。
 * 
 *  <p>使用更积极的操作(如{@link #putIfAbsent})不能推迟到正在运行的事务的提交后阶段请谨慎使用这些
 * 
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 * @author Stas Volsky
 * @since 3.2
 * @see TransactionAwareCacheManagerProxy
 */
public class TransactionAwareCacheDecorator implements Cache {

	private final Cache targetCache;


	/**
	 * Create a new TransactionAwareCache for the given target Cache.
	 * <p>
	 * 
	 * 
	 * @param targetCache the target Cache to decorate
	 */
	public TransactionAwareCacheDecorator(Cache targetCache) {
		Assert.notNull(targetCache, "Target Cache must not be null");
		this.targetCache = targetCache;
	}


	@Override
	public String getName() {
		return this.targetCache.getName();
	}

	@Override
	public Object getNativeCache() {
		return this.targetCache.getNativeCache();
	}

	@Override
	public ValueWrapper get(Object key) {
		return this.targetCache.get(key);
	}

	@Override
	public <T> T get(Object key, Class<T> type) {
		return this.targetCache.get(key, type);
	}

	@Override
	public <T> T get(Object key, Callable<T> valueLoader) {
		return this.targetCache.get(key, valueLoader);
	}

	@Override
	public void put(final Object key, final Object value) {
		if (TransactionSynchronizationManager.isSynchronizationActive()) {
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
				@Override
				public void afterCommit() {
					targetCache.put(key, value);
				}
			});
		}
		else {
			this.targetCache.put(key, value);
		}
	}

	@Override
	public ValueWrapper putIfAbsent(final Object key, final Object value) {
		return this.targetCache.putIfAbsent(key, value);
	}

	@Override
	public void evict(final Object key) {
		if (TransactionSynchronizationManager.isSynchronizationActive()) {
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
				@Override
				public void afterCommit() {
					targetCache.evict(key);
				}
			});
		}
		else {
			this.targetCache.evict(key);
		}
	}

	@Override
	public void clear() {
		if (TransactionSynchronizationManager.isSynchronizationActive()) {
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
				@Override
				public void afterCommit() {
					targetCache.clear();
				}
			});
		}
		else {
			this.targetCache.clear();
		}
	}

}
