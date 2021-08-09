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

package org.springframework.cache;

import java.util.concurrent.Callable;

/**
 * Interface that defines common cache operations.
 *
 * <b>Note:</b> Due to the generic use of caching, it is recommended that
 * implementations allow storage of <tt>null</tt> values (for example to
 * cache methods that return {@code null}).
 *
 * <p>
 *  定义公共缓存操作的接口
 * 
 * <b>注意：</b>由于通用缓存,建议实现允许存储<tt> null </tt>值(例如,缓存返回{@code null}的方法)
 * 
 * 
 * @author Costin Leau
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 * @since 3.1
 */
public interface Cache {

	/**
	 * Return the cache name.
	 * <p>
	 *  返回缓存名称
	 * 
	 */
	String getName();

	/**
	 * Return the underlying native cache provider.
	 * <p>
	 *  返回底层本机缓存提供程序
	 * 
	 */
	Object getNativeCache();

	/**
	 * Return the value to which this cache maps the specified key.
	 * <p>Returns {@code null} if the cache contains no mapping for this key;
	 * otherwise, the cached value (which may be {@code null} itself) will
	 * be returned in a {@link ValueWrapper}.
	 * <p>
	 *  返回此缓存映射指定键的值<p>如果缓存不包含此键的映射,则返回{@code null}否则,缓存的值(可能是{@code null}本身))将返回到{@link ValueWrapper}
	 * 
	 * 
	 * @param key the key whose associated value is to be returned
	 * @return the value to which this cache maps the specified key,
	 * contained within a {@link ValueWrapper} which may also hold
	 * a cached {@code null} value. A straight {@code null} being
	 * returned means that the cache contains no mapping for this key.
	 * @see #get(Object, Class)
	 */
	ValueWrapper get(Object key);

	/**
	 * Return the value to which this cache maps the specified key,
	 * generically specifying a type that return value will be cast to.
	 * <p>Note: This variant of {@code get} does not allow for differentiating
	 * between a cached {@code null} value and no cache entry found at all.
	 * Use the standard {@link #get(Object)} variant for that purpose instead.
	 * <p>
	 * 返回此缓存映射指定键的值,通常指定返回值将被转换为<p>的类型注意：{@code get}的此变体不允许区分缓存的{@code null}值并且没有找到缓存条目为此而使用标准的{@link #get(Object)}
	 * 变体。
	 * 
	 * 
	 * @param key the key whose associated value is to be returned
	 * @param type the required type of the returned value (may be
	 * {@code null} to bypass a type check; in case of a {@code null}
	 * value found in the cache, the specified type is irrelevant)
	 * @return the value to which this cache maps the specified key
	 * (which may be {@code null} itself), or also {@code null} if
	 * the cache contains no mapping for this key
	 * @throws IllegalStateException if a cache entry has been found
	 * but failed to match the specified type
	 * @since 4.0
	 * @see #get(Object)
	 */
	<T> T get(Object key, Class<T> type);

	/**
	 * Return the value to which this cache maps the specified key, obtaining
	 * that value from {@code valueLoader} if necessary. This method provides
	 * a simple substitute for the conventional "if cached, return; otherwise
	 * create, cache and return" pattern.
	 * <p>If possible, implementations should ensure that the loading operation
	 * is synchronized so that the specified {@code valueLoader} is only called
	 * once in case of concurrent access on the same key.
	 * <p>If the {@code valueLoader} throws an exception, it is wrapped in
	 * a {@link ValueRetrievalException}
	 * <p>
	 * 返回此缓存映射指定键的值,如果需要,从{@code valueLoader}获取该值该方法提供了传统的"如果缓存,返回;否则创建,缓存和返回"模式的简单替代方式<p>如果可能的是,实现应该确保加载操作是
	 * 同步的,以便在同一个密钥<p>上并发访问的情况下,仅调用指定的{@code valueLoader}一次。
	 * 如果{@code valueLoader}抛出异常,则它被包装在{@link ValueRetrievalException}。
	 * 
	 * 
	 * @param key the key whose associated value is to be returned
	 * @return the value to which this cache maps the specified key
	 * @throws ValueRetrievalException if the {@code valueLoader} throws an exception
	 * @since 4.3
	 */
	<T> T get(Object key, Callable<T> valueLoader);

	/**
	 * Associate the specified value with the specified key in this cache.
	 * <p>If the cache previously contained a mapping for this key, the old
	 * value is replaced by the specified value.
	 * <p>
	 *  将指定的值与此高速缓存中的指定键相关联<p>如果缓存先前包含此密钥的映射,则旧值将替换为指定的值
	 * 
	 * 
	 * @param key the key with which the specified value is to be associated
	 * @param value the value to be associated with the specified key
	 */
	void put(Object key, Object value);

	/**
	 * Atomically associate the specified value with the specified key in this cache
	 * if it is not set already.
	 * <p>This is equivalent to:
	 * <pre><code>
	 * Object existingValue = cache.get(key);
	 * if (existingValue == null) {
	 *     cache.put(key, value);
	 *     return null;
	 * } else {
	 *     return existingValue;
	 * }
	 * </code></pre>
	 * except that the action is performed atomically. While all out-of-the-box
	 * {@link CacheManager} implementations are able to perform the put atomically,
	 * the operation may also be implemented in two steps, e.g. with a check for
	 * presence and a subsequent put, in a non-atomic way. Check the documentation
	 * of the native cache implementation that you are using for more details.
	 * <p>
	 * 如果尚未设置此缓存,则将指定的值与指定的键原子关联<p>这相当于：<pre> <code> Object existingValue = cacheget(key); if(existingValue 
	 * == null){cacheput(key,value);返回null; } else {return existingValue; } </code> </pre>,除了操作是原子性的。
	 * 尽管所有开箱即用的{@link CacheManager}实现都能够以原子方式执行put操作,但是操作也可以分两个步骤来实现,例如以非原子的方式检查存在和后续放置检查您正在使用的本机缓存实现的文档以获取
	 * 更多详细信息。
	 * 
	 * 
	 * @param key the key with which the specified value is to be associated
	 * @param value the value to be associated with the specified key
	 * @return the value to which this cache maps the specified key (which may be
	 * {@code null} itself), or also {@code null} if the cache did not contain any
	 * mapping for that key prior to this call. Returning {@code null} is therefore
	 * an indicator that the given {@code value} has been associated with the key.
	 * @since 4.1
	 */
	ValueWrapper putIfAbsent(Object key, Object value);

	/**
	 * Evict the mapping for this key from this cache if it is present.
	 * <p>
	 *  如果存在,则从此缓存中取出该密钥的映射
	 * 
	 * 
	 * @param key the key whose mapping is to be removed from the cache
	 */
	void evict(Object key);

	/**
	 * Remove all mappings from the cache.
	 * <p>
	 *  从缓存中删除所有映射
	 * 
	 */
	void clear();


	/**
	 * A (wrapper) object representing a cache value.
	 * <p>
	 * 表示缓存值的(包装器)对象
	 * 
	 */
	interface ValueWrapper {

		/**
		 * Return the actual value in the cache.
		 * <p>
		 *  返回缓存中的实际值
		 * 
		 */
		Object get();
	}


	/**
	 * Wrapper exception to be thrown from {@link #get(Object, Callable)}
	 * in case of the value loader callback failing with an exception.
	 * <p>
	 *  如果值装载程序回调失败,则会从{@link #get(Object,Callable)}抛出包装器异常
	 * 
	 * @since 4.3
	 */
	@SuppressWarnings("serial")
	class ValueRetrievalException extends RuntimeException {

		private final Object key;

		public ValueRetrievalException(Object key, Callable<?> loader, Throwable ex) {
			super(String.format("Value for key '%s' could not be loaded using '%s'", key, loader), ex);
			this.key = key;
		}

		public Object getKey() {
			return this.key;
		}
	}

}
