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

package org.springframework.aop.interceptor;

import java.lang.reflect.Method;

/**
 * A strategy for handling uncaught exceptions thrown from asynchronous methods.
 *
 * <p>An asynchronous method usually returns a {@link java.util.concurrent.Future}
 * instance that gives access to the underlying exception. When the method does
 * not provide that return type, this handler can be used to managed such
 * uncaught exceptions.
 *
 * <p>
 * 
 * @author Stephane Nicoll
 * @since 4.1
 */
public interface AsyncUncaughtExceptionHandler {

	/**
	 * Handle the given uncaught exception thrown from an asynchronous method.
	 * <p>
	 *  处理从异步方法抛出的未捕获异常的策略
	 * 
	 * <p>异步方法通常返回一个{@link javautilconcurrentFuture}实例,可以访问底层异常当该方法不提供该返回类型时,该处理程序可用于管理此类未捕获的异常
	 * 
	 * 
	 * @param ex the exception thrown from the asynchronous method
	 * @param method the asynchronous method
	 * @param params the parameters used to invoked the method
	 */
	void handleUncaughtException(Throwable ex, Method method, Object... params);

}
