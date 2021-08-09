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

package org.springframework.cache.support;

import org.springframework.cache.Cache;

/**
 * Common base class for {@link Cache} implementations that need to adapt
 * {@code null} values (and potentially other such special values) before
 * passing them on to the underlying store.
 *
 * <p>Transparently replaces given {@code null} user values with an internal
 * {@link NullValue#INSTANCE}, if configured to support {@code null} values
 * (as indicated by {@link #isAllowNullValues()}.
 *
 * <p>
 *  {@link Cache}实现的通用基类,需要在将{@code null}值(以及潜在的其他此类特殊值)传递到底层商店之前进行调整
 * 
 * 如果配置为支持{@code null}值(如{@link #isAllowNullValues())所示,透明地将{@code null}用户值替换为内部{@link NullValue#INSTANCE}
 * 。
 * 
 * 
 * @author Juergen Hoeller
 * @since 4.2.2
 */
public abstract class AbstractValueAdaptingCache implements Cache {

	private final boolean allowNullValues;


	/**
	 * Create an {@code AbstractValueAdaptingCache} with the given setting.
	 * <p>
	 *  使用给定的设置创建一个{@code AbstractValueAdaptingCache}
	 * 
	 * 
	 * @param allowNullValues whether to allow for {@code null} values
	 */
	protected AbstractValueAdaptingCache(boolean allowNullValues) {
		this.allowNullValues = allowNullValues;
	}


	/**
	 * Return whether {@code null} values are allowed in this cache.
	 * <p>
	 *  返回此缓存中是否允许使用{@code null}值
	 * 
	 */
	public final boolean isAllowNullValues() {
		return this.allowNullValues;
	}

	@Override
	public ValueWrapper get(Object key) {
		Object value = lookup(key);
		return toValueWrapper(value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(Object key, Class<T> type) {
		Object value = fromStoreValue(lookup(key));
		if (value != null && type != null && !type.isInstance(value)) {
			throw new IllegalStateException("Cached value is not of required type [" + type.getName() + "]: " + value);
		}
		return (T) value;
	}

	/**
	 * Perform an actual lookup in the underlying store.
	 * <p>
	 *  在底层商店中执行实际查找
	 * 
	 * 
	 * @param key the key whose associated value is to be returned
	 * @return the raw store value for the key
	 */
	protected abstract Object lookup(Object key);


	/**
	 * Convert the given value from the internal store to a user value
	 * returned from the get method (adapting {@code null}).
	 * <p>
	 *  将给定值从内部存储转换为从get方法返回的用户值(适应{@code null})
	 * 
	 * 
	 * @param storeValue the store value
	 * @return the value to return to the user
	 */
	protected Object fromStoreValue(Object storeValue) {
		if (this.allowNullValues && storeValue == NullValue.INSTANCE) {
			return null;
		}
		return storeValue;
	}

	/**
	 * Convert the given user value, as passed into the put method,
	 * to a value in the internal store (adapting {@code null}).
	 * <p>
	 *  将传递给put方法的给定用户值转换为内部存储中的值(适应{@code null})
	 * 
	 * 
	 * @param userValue the given user value
	 * @return the value to store
	 */
	protected Object toStoreValue(Object userValue) {
		if (this.allowNullValues && userValue == null) {
			return NullValue.INSTANCE;
		}
		return userValue;
	}

	/**
	 * Wrap the given store value with a {@link SimpleValueWrapper}, also going
	 * through {@link #fromStoreValue} conversion. Useful for {@link #get(Object)}
	 * and {@link #putIfAbsent(Object, Object)} implementations.
	 * <p>
	 * 使用{@link SimpleValueWrapper}将给定的存储值转换为{@link #fromStoreValue}转换,适用于{@link #get(Object)}和{@link #putIfAbsent(Object,Object)}
	 * 实现。
	 * 
	 * @param storeValue the original value
	 * @return the wrapped value
	 */
	protected Cache.ValueWrapper toValueWrapper(Object storeValue) {
		return (storeValue != null ? new SimpleValueWrapper(fromStoreValue(storeValue)) : null);
	}


}
