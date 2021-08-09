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

package org.springframework.util;

import java.util.List;
import java.util.Map;

/**
 * Extension of the {@code Map} interface that stores multiple values.
 *
 * <p>
 *  扩展存储多个值的{@code Map}接口
 * 
 * 
 * @author Arjen Poutsma
 * @since 3.0
 */
public interface MultiValueMap<K, V> extends Map<K, List<V>> {

	/**
	 * Return the first value for the given key.
	 * <p>
	 *  返回给定键的第一个值
	 * 
	 * 
	 * @param key the key
	 * @return the first value for the specified key, or {@code null}
	 */
	V getFirst(K key);

	/**
	 * Add the given single value to the current list of values for the given key.
	 * <p>
	 *  将给定的单个值添加到给定键的当前值列表中
	 * 
	 * 
	 * @param key the key
	 * @param value the value to be added
	 */
	void add(K key, V value);

	/**
	 * Set the given single value under the given key.
	 * <p>
	 * 在给定的键下设置给定的单个值
	 * 
	 * 
	 * @param key the key
	 * @param value the value to set
	 */
	void set(K key, V value);

	/**
	 * Set the given values under.
	 * <p>
	 *  设置下面的给定值
	 * 
	 * 
	 * @param values the values.
	 */
	void setAll(Map<K, V> values);

	/**
	 * Returns the first values contained in this {@code MultiValueMap}.
	 * <p>
	 *  返回此{@code MultiValueMap}中包含的第一个值
	 * 
	 * @return a single value representation of this map
	 */
	Map<K, V> toSingleValueMap();

}
