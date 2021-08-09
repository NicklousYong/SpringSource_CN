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

package org.springframework.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * {@link LinkedHashMap} variant that stores String keys in a case-insensitive
 * manner, for example for key-based access in a results table.
 *
 * <p>Preserves the original order as well as the original casing of keys,
 * while allowing for contains, get and remove calls with any case of key.
 *
 * <p>Does <i>not</i> support {@code null} keys.
 *
 * <p>
 *  {@link LinkedHashMap}变体,以不区分大小写的方式存储String键,例如在结果表中的基于键的访问
 * 
 * <p>保留原始订单以及钥匙的原始外壳,同时允许包含,获取和删除任何钥匙的通话
 * 
 *  <p> <i>不</i>支持{@code null}键
 * 
 * 
 * @author Juergen Hoeller
 * @since 3.0
 */
@SuppressWarnings("serial")
public class LinkedCaseInsensitiveMap<V> extends LinkedHashMap<String, V> {

	private Map<String, String> caseInsensitiveKeys;

	private final Locale locale;


	/**
	 * Create a new LinkedCaseInsensitiveMap for the default Locale.
	 * <p>
	 *  为默认语言环境创建一个新的LinkedCaseInsensitiveMap
	 * 
	 * 
	 * @see java.lang.String#toLowerCase()
	 */
	public LinkedCaseInsensitiveMap() {
		this(null);
	}

	/**
	 * Create a new LinkedCaseInsensitiveMap that stores lower-case keys
	 * according to the given Locale.
	 * <p>
	 *  创建一个新的LinkedCaseInsensitiveMap,根据给定的区域设置存储小写的键
	 * 
	 * 
	 * @param locale the Locale to use for lower-case conversion
	 * @see java.lang.String#toLowerCase(java.util.Locale)
	 */
	public LinkedCaseInsensitiveMap(Locale locale) {
		super();
		this.caseInsensitiveKeys = new HashMap<String, String>();
		this.locale = (locale != null ? locale : Locale.getDefault());
	}

	/**
	 * Create a new LinkedCaseInsensitiveMap that wraps a {@link LinkedHashMap}
	 * with the given initial capacity and stores lower-case keys according
	 * to the default Locale.
	 * <p>
	 *  创建一个新的LinkedCaseInsensitiveMap,它将{@link LinkedHashMap}包含给定的初始容量,并根据默认的区域设置存储小写密钥
	 * 
	 * 
	 * @param initialCapacity the initial capacity
	 * @see java.lang.String#toLowerCase()
	 */
	public LinkedCaseInsensitiveMap(int initialCapacity) {
		this(initialCapacity, null);
	}

	/**
	 * Create a new LinkedCaseInsensitiveMap that wraps a {@link LinkedHashMap}
	 * with the given initial capacity and stores lower-case keys according
	 * to the given Locale.
	 * <p>
	 *  创建一个新的LinkedCaseInsensitiveMap,将带有给定初始容量的{@link LinkedHashMap}包装起来,并根据给定的区域设置存储小写密钥
	 * 
	 * 
	 * @param initialCapacity the initial capacity
	 * @param locale the Locale to use for lower-case conversion
	 * @see java.lang.String#toLowerCase(java.util.Locale)
	 */
	public LinkedCaseInsensitiveMap(int initialCapacity, Locale locale) {
		super(initialCapacity);
		this.caseInsensitiveKeys = new HashMap<String, String>(initialCapacity);
		this.locale = (locale != null ? locale : Locale.getDefault());
	}


	@Override
	public V put(String key, V value) {
		String oldKey = this.caseInsensitiveKeys.put(convertKey(key), key);
		if (oldKey != null && !oldKey.equals(key)) {
			super.remove(oldKey);
		}
		return super.put(key, value);
	}

	@Override
	public void putAll(Map<? extends String, ? extends V> map) {
		if (map.isEmpty()) {
			return;
		}
		for (Map.Entry<? extends String, ? extends V> entry : map.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public boolean containsKey(Object key) {
		return (key instanceof String && this.caseInsensitiveKeys.containsKey(convertKey((String) key)));
	}

	@Override
	public V get(Object key) {
		if (key instanceof String) {
			String caseInsensitiveKey = this.caseInsensitiveKeys.get(convertKey((String) key));
			if (caseInsensitiveKey != null) {
				return super.get(caseInsensitiveKey);
			}
		}
		return null;
	}

	// Overridden to avoid LinkedHashMap's own hash computation in its getOrDefault impl
	@Override
	public V getOrDefault(Object key, V defaultValue) {
		if (key instanceof String) {
			String caseInsensitiveKey = this.caseInsensitiveKeys.get(convertKey((String) key));
			if (caseInsensitiveKey != null) {
				return super.get(caseInsensitiveKey);
			}
		}
		return defaultValue;
	}

	@Override
	public V remove(Object key) {
		if (key instanceof String) {
			String caseInsensitiveKey = this.caseInsensitiveKeys.remove(convertKey((String) key));
			if (caseInsensitiveKey != null) {
				return super.remove(caseInsensitiveKey);
			}
		}
		return null;
	}

	@Override
	public void clear() {
		this.caseInsensitiveKeys.clear();
		super.clear();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object clone() {
		LinkedCaseInsensitiveMap<V> copy = (LinkedCaseInsensitiveMap<V>) super.clone();
		copy.caseInsensitiveKeys = new HashMap<String, String>(this.caseInsensitiveKeys);
		return copy;
	}


	/**
	 * Convert the given key to a case-insensitive key.
	 * <p>The default implementation converts the key
	 * to lower-case according to this Map's Locale.
	 * <p>
	 * 将给定的键转换为不区分大小写的键<p>默认实现根据此Map的区域设置将键转换为小写
	 * 
	 * @param key the user-specified key
	 * @return the key to use for storing
	 * @see java.lang.String#toLowerCase(java.util.Locale)
	 */
	protected String convertKey(String key) {
		return key.toLowerCase(this.locale);
	}

}
