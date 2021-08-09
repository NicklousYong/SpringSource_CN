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

package org.springframework.cache.transaction;

import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;

/**
 * Base class for CacheManager implementations that want to support built-in
 * awareness of Spring-managed transactions. This usually needs to be switched
 * on explicitly through the {@link #setTransactionAware} bean property.
 *
 * <p>
 *  希望支持Spring管理事务内置意识的CacheManager实现的基类这通常需要通过{@link #setTransactionAware} bean属性显式切换
 * 
 * 
 * @author Juergen Hoeller
 * @since 3.2
 * @see #setTransactionAware
 * @see TransactionAwareCacheDecorator
 * @see TransactionAwareCacheManagerProxy
 */
public abstract class AbstractTransactionSupportingCacheManager extends AbstractCacheManager {

	private boolean transactionAware = false;


	/**
	 * Set whether this CacheManager should expose transaction-aware Cache objects.
	 * <p>Default is "false". Set this to "true" to synchronize cache put/evict
	 * operations with ongoing Spring-managed transactions, performing the actual cache
	 * put/evict operation only in the after-commit phase of a successful transaction.
	 * <p>
	 * 设置这个CacheManager是否应该暴露事务感知缓存对象<p>默认值为"false"将此设置为"true"以将缓存放置/排除操作与正在进行的Spring管理事务同步,仅执行实际的缓存放置/驱逐操作成
	 * 功交易的提交后阶段。
	 * 
	 */
	public void setTransactionAware(boolean transactionAware) {
		this.transactionAware = transactionAware;
	}

	/**
	 * Return whether this CacheManager has been configured to be transaction-aware.
	 * <p>
	 */
	public boolean isTransactionAware() {
		return this.transactionAware;
	}


	@Override
	protected Cache decorateCache(Cache cache) {
		return (isTransactionAware() ? new TransactionAwareCacheDecorator(cache) : cache);
	}

}
