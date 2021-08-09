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

package org.springframework.cache.interceptor;

/**
 * Abstract the invocation of a cache operation.
 *
 * <p>Does not provide a way to transmit checked exceptions but
 * provide a special exception that should be used to wrap any
 * exception that was thrown by the underlying invocation.
 * Callers are expected to handle this issue type specifically.
 *
 * <p>
 *  抽象调用缓存操作
 * 
 * <p>不提供传输检查异常的方法,但提供了一个特殊的异常,应该用于包装底层调用引发的任何异常。调用者预期会专门处理此问题类型
 * 
 * 
 * @author Stephane Nicoll
 * @since 4.1
 */
public interface CacheOperationInvoker {

	/**
	 * Invoke the cache operation defined by this instance. Wraps any exception
	 * that is thrown during the invocation in a {@link ThrowableWrapper}.
	 * <p>
	 *  调用此实例定义的缓存操作将{@link ThrowableWrapper}中的调用期间抛出的任何异常进行包装
	 * 
	 * 
	 * @return the result of the operation
	 * @throws ThrowableWrapper if an error occurred while invoking the operation
	 */
	Object invoke() throws ThrowableWrapper;


	/**
	 * Wrap any exception thrown while invoking {@link #invoke()}.
	 * <p>
	 *  调用{@link #invoke()}时抛出所有异常
	 */
	@SuppressWarnings("serial")
	class ThrowableWrapper extends RuntimeException {

		private final Throwable original;

		public ThrowableWrapper(Throwable original) {
			super(original.getMessage(), original);
			this.original = original;
		}

		public Throwable getOriginal() {
			return this.original;
		}
	}

}
