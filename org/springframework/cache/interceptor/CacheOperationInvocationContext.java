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
 * Representation of the context of the invocation of a cache operation.
 *
 * <p>The cache operation is static and independent of a particular invocation;
 * this interface gathers the operation and a particular invocation.
 *
 * <p>
 *  表示高速缓存操作调用的上下文
 * 
 *  <p>缓存操作是静态的,与特定调用无关。此接口收集操作和特定调用
 * 
 * 
 * @author Stephane Nicoll
 * @since 4.1
 */
public interface CacheOperationInvocationContext<O extends BasicOperation> {

	/**
	 * Return the cache operation.
	 * <p>
	 * 返回缓存操作
	 * 
	 */
	O getOperation();

	/**
	 * Return the target instance on which the method was invoked.
	 * <p>
	 *  返回调用该方法的目标实例
	 * 
	 */
	Object getTarget();

	/**
	 * Return the method which was invoked.
	 * <p>
	 *  返回被调用的方法
	 * 
	 */
	Method getMethod();

	/**
	 * Return the argument list used to invoke the method.
	 * <p>
	 *  返回用于调用该方法的参数列表
	 */
	Object[] getArgs();

}
