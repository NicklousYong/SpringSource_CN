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

package org.springframework.cache.concurrent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.core.serializer.support.SerializationDelegate;
import org.springframework.util.Assert;

/**
 * Simple {@link org.springframework.cache.Cache} implementation based on the
 * core JDK {@code java.util.concurrent} package.
 *
 * <p>Useful for testing or simple caching scenarios, typically in combination
 * with {@link org.springframework.cache.support.SimpleCacheManager} or
 * dynamically through {@link ConcurrentMapCacheManager}.
 *
 * <p><b>Note:</b> As {@link ConcurrentHashMap} (the default implementation used)
 * does not allow for {@code null} values to be stored, this class will replace
 * them with a predefined internal object. This behavior can be changed through the
 * {@link #ConcurrentMapCache(String, ConcurrentMap, boolean)} constructor.
 *
 * <p>
 *  基于JDK {@code javautilconcurrent}包的简单{@link orgspringframeworkcacheCache}实现
 * 
 * <p>通常与{@link orgspringframeworkcachesupportSimpleCacheManager}结合使用或通过{@link ConcurrentMapCacheManager}
 * 动态执行测试或简单缓存场景,。
 * 
 *  <p> <b>注意：</b>由于{@link ConcurrentHashMap}(使用的默认实现)不允许存储{@code null}值,所以此类将用预定义的内部对象替换它们此行为可以通过{@link #ConcurrentMapCache(String,ConcurrentMap,boolean)}
 * 构造函数进行更改。
 * 
 * 
 * @author Costin Leau
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 * @since 3.1
 */
public class ConcurrentMapCache extends AbstractValueAdaptingCache {

	private final String name;

	private final ConcurrentMap<Object, Object> store;

	private final SerializationDelegate serialization;


	/**
	 * Create a new ConcurrentMapCache with the specified name.
	 * <p>
	 *  创建一个具有指定名称的新ConcurrentMapCache
	 * 
	 * 
	 * @param name the name of the cache
	 */
	public ConcurrentMapCache(String name) {
		this(name, new ConcurrentHashMap<Object, Object>(256), true);
	}

	/**
	 * Create a new ConcurrentMapCache with the specified name.
	 * <p>
	 *  创建一个具有指定名称的新ConcurrentMapCache
	 * 
	 * 
	 * @param name the name of the cache
	 * @param allowNullValues whether to accept and convert {@code null}
	 * values for this cache
	 */
	public ConcurrentMapCache(String name, boolean allowNullValues) {
		this(name, new ConcurrentHashMap<Object, Object>(256), allowNullValues);
	}

	/**
	 * Create a new ConcurrentMapCache with the specified name and the
	 * given internal {@link ConcurrentMap} to use.
	 * <p>
	 *  创建一个新的ConcurrentMapCache,具有指定的名称和给定的内部{@link ConcurrentMap}使用
	 * 
	 * 
	 * @param name the name of the cache
	 * @param store the ConcurrentMap to use as an internal store
	 * @param allowNullValues whether to allow {@code null} values
	 * (adapting them to an internal null holder value)
	 */
	public ConcurrentMapCache(String name, ConcurrentMap<Object, Object> store, boolean allowNullValues) {
		this(name, store, allowNullValues, null);
	}

	/**
	 * Create a new ConcurrentMapCache with the specified name and the
	 * given internal {@link ConcurrentMap} to use. If the
	 * {@link SerializationDelegate} is specified,
	 * {@link #isStoreByValue() store-by-value} is enabled
	 * <p>
	 * 创建一个新的具有指定名称的ConcurrentMapCache和给定的内部{@link ConcurrentMap}使用如果指定了{@link SerializationDelegate},{@link #isStoreByValue()store-by-value}
	 * 被启用。
	 * 
	 * 
	 * @param name the name of the cache
	 * @param store the ConcurrentMap to use as an internal store
	 * @param allowNullValues whether to allow {@code null} values
	 * (adapting them to an internal null holder value)
	 * @param serialization the {@link SerializationDelegate} to use
	 * to serialize cache entry or {@code null} to store the reference
	 * @since 4.3
	 */
	protected ConcurrentMapCache(String name, ConcurrentMap<Object, Object> store,
			boolean allowNullValues, SerializationDelegate serialization) {

		super(allowNullValues);
		Assert.notNull(name, "Name must not be null");
		Assert.notNull(store, "Store must not be null");
		this.name = name;
		this.store = store;
		this.serialization = serialization;
	}


	/**
	 * Return whether this cache stores a copy of each entry ({@code true}) or
	 * a reference ({@code false}, default). If store by value is enabled, each
	 * entry in the cache must be serializable.
	 * <p>
	 * 
	 * @since 4.3
	 */
	public final boolean isStoreByValue() {
		return (this.serialization != null);
	}

	@Override
	public final String getName() {
		return this.name;
	}

	@Override
	public final ConcurrentMap<Object, Object> getNativeCache() {
		return this.store;
	}

	@Override
	protected Object lookup(Object key) {
		return this.store.get(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(Object key, Callable<T> valueLoader) {
		if (this.store.containsKey(key)) {
			return (T) get(key).get();
		}
		else {
			synchronized (this.store) {
				if (this.store.containsKey(key)) {
					return (T) get(key).get();
				}
				T value;
				try {
					value = valueLoader.call();
				}
				catch (Exception ex) {
					throw new ValueRetrievalException(key, valueLoader, ex);
				}
				put(key, value);
				return value;
			}
		}
	}

	@Override
	public void put(Object key, Object value) {
		this.store.put(key, toStoreValue(value));
	}

	@Override
	public ValueWrapper putIfAbsent(Object key, Object value) {
		Object existing = this.store.putIfAbsent(key, toStoreValue(value));
		return toValueWrapper(existing);
	}

	@Override
	public void evict(Object key) {
		this.store.remove(key);
	}

	@Override
	public void clear() {
		this.store.clear();
	}

	@Override
	protected Object toStoreValue(Object userValue) {
		Object storeValue = super.toStoreValue(userValue);
		if (this.serialization != null) {
			try {
				return serializeValue(storeValue);
			}
			catch (Exception ex) {
				throw new IllegalArgumentException("Failed to serialize cache value '"
						+ userValue + "'. Does it implement Serializable?", ex);
			}
		}
		else {
			return storeValue;
		}
	}

	private Object serializeValue(Object storeValue) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			this.serialization.serialize(storeValue, out);
			return out.toByteArray();
		}
		finally {
			out.close();
		}
	}

	@Override
	protected Object fromStoreValue(Object storeValue) {
		if (this.serialization != null) {
			try {
				return super.fromStoreValue(deserializeValue(storeValue));
			}
			catch (Exception ex) {
				throw new IllegalArgumentException("Failed to deserialize cache value '" +
						storeValue + "'", ex);
			}
		}
		else {
			return super.fromStoreValue(storeValue);
		}

	}

	private Object deserializeValue(Object storeValue) throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream((byte[]) storeValue);
		try {
			return this.serialization.deserialize(in);
		}
		finally {
			in.close();
		}
	}

}
