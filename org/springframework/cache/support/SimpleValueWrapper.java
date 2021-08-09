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

package org.springframework.cache.support;

import org.springframework.cache.Cache.ValueWrapper;

/**
 * Straightforward implementation of {@link org.springframework.cache.Cache.ValueWrapper},
 * simply holding the value as given at construction and returning it from {@link #get()}.
 *
 * <p>
 *  直接实现{@link orgspringframeworkcacheCacheValueWrapper},只需保留在构造中给出的值,并将其从{@link #get()}返回
 * 
 * 
 * @author Costin Leau
 * @since 3.1
 */
public class SimpleValueWrapper implements ValueWrapper {

	private final Object value;


	/**
	 * Create a new SimpleValueWrapper instance for exposing the given value.
	 * <p>
	 * 创建一个新的SimpleValueWrapper实例来暴露给定的值
	 * 
	 * 
	 * @param value the value to expose (may be {@code null})
	 */
	public SimpleValueWrapper(Object value) {
		this.value = value;
	}


	/**
	 * Simply returns the value as given at construction time.
	 * <p>
	 *  简单地返回施工时给出的值
	 */
	@Override
	public Object get() {
		return this.value;
	}

}
