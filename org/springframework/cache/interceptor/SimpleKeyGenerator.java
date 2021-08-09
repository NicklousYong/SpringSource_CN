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

package org.springframework.cache.interceptor;

import java.lang.reflect.Method;

/**
 * Simple key generator. Returns the parameter itself if a single non-null value
 * is given, otherwise returns a {@link SimpleKey} of the parameters.
 *
 * <p>Unlike {@link DefaultKeyGenerator}, no collisions will occur with the keys
 * generated by this class. The returned {@link SimpleKey} object can be safely
 * used with a {@link org.springframework.cache.concurrent.ConcurrentMapCache},
 * however, might not be suitable for all {@link org.springframework.cache.Cache}
 * implementations.
 *
 * <p>
 *  简单的键生成器如果给定了一个非空值,返回参数本身,否则返回一个参数{@link SimpleKey}
 * 
 * <p>与{@link DefaultKeyGenerator}不同,此类生成的密钥不会发生冲突返回的{@link SimpleKey}对象可以安全地与{@link orgspringframeworkcacheconcurrentConcurrentMapCache}
 * 一起使用,但是可能不适用于所有{ @link orgspringframeworkcacheCache}实现。
 * 
 * @author Phillip Webb
 * @author Juergen Hoeller
 * @since 4.0
 * @see SimpleKey
 * @see DefaultKeyGenerator
 * @see org.springframework.cache.annotation.CachingConfigurer
 */
public class SimpleKeyGenerator implements KeyGenerator {

	@Override
	public Object generate(Object target, Method method, Object... params) {
		return generateKey(params);
	}

	/**
	 * Generate a key based on the specified parameters.
	 * <p>
	 * 
	 */
	public static Object generateKey(Object... params) {
		if (params.length == 0) {
			return SimpleKey.EMPTY;
		}
		if (params.length == 1) {
			Object param = params[0];
			if (param != null && !param.getClass().isArray()) {
				return param;
			}
		}
		return new SimpleKey(params);
	}

}
