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

package org.springframework.scheduling.annotation;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.util.concurrent.SuccessCallback;

/**
 * A pass-through {@code Future} handle that can be used for method signatures
 * which are declared with a {@code Future} return type for asynchronous execution.
 *
 * <p>As of Spring 4.1, this class implements {@link ListenableFuture}, not just
 * plain {@link java.util.concurrent.Future}, along with the corresponding support
 * in {@code @Async} processing.
 *
 * <p>As of Spring 4.2, this class also supports passing execution exceptions back
 * to the caller.
 *
 * <p>
 *  可以使用用于异步执行的{@code Future}返回类型声明的方法签名的传递{@code Future}句柄
 * 
 * <p>从Spring 41开始,该类实现{@link ListenableFuture},而不仅仅是简单的{@link javautilconcurrentFuture},以及{@code @Async}
 * 处理中的相应支持。
 * 
 *  <p>从Spring 42开始,该类还支持将执行异常传递给调用者
 * 
 * 
 * @author Juergen Hoeller
 * @author Rossen Stoyanchev
 * @since 3.0
 * @see Async
 * @see #forValue(Object)
 * @see #forExecutionException(Throwable)
 */
public class AsyncResult<V> implements ListenableFuture<V> {

	private final V value;

	private final ExecutionException executionException;


	/**
	 * Create a new AsyncResult holder.
	 * <p>
	 *  创建一个新的AsyncResult持有者
	 * 
	 * 
	 * @param value the value to pass through
	 */
	public AsyncResult(V value) {
		this(value, null);
	}

	/**
	 * Create a new AsyncResult holder.
	 * <p>
	 *  创建一个新的AsyncResult持有者
	 * 
	 * 
	 * @param value the value to pass through
	 */
	private AsyncResult(V value, ExecutionException ex) {
		this.value = value;
		this.executionException = ex;
	}


	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public boolean isDone() {
		return true;
	}

	@Override
	public V get() throws ExecutionException {
		if (this.executionException != null) {
			throw this.executionException;
		}
		return this.value;
	}

	@Override
	public V get(long timeout, TimeUnit unit) throws ExecutionException {
		return get();
	}

	@Override
	public void addCallback(ListenableFutureCallback<? super V> callback) {
		addCallback(callback, callback);
	}

	@Override
	public void addCallback(SuccessCallback<? super V> successCallback, FailureCallback failureCallback) {
		try {
			if (this.executionException != null) {
				Throwable cause = this.executionException.getCause();
				failureCallback.onFailure(cause != null ? cause : this.executionException);
			}
			else {
				successCallback.onSuccess(this.value);
			}
		}
		catch (Throwable ex) {
			// Ignore
		}
	}


	/**
	 * Create a new async result which exposes the given value from {@link Future#get()}.
	 * <p>
	 *  创建一个新的异步结果,从{@link Future#get()}中公开给定的值
	 * 
	 * 
	 * @param value the value to expose
	 * @since 4.2
	 * @see Future#get()
	 */
	public static <V> ListenableFuture<V> forValue(V value) {
		return new AsyncResult<V>(value, null);
	}

	/**
	 * Create a new async result which exposes the given exception as an
	 * {@link ExecutionException} from {@link Future#get()}.
	 * <p>
	 *  创建一个新的异步结果,将{@link Future#get()}中的给定异常公开为{@link ExecutionException}
	 * 
	 * @param ex the exception to expose (either an pre-built {@link ExecutionException}
	 * or a cause to be wrapped in an {@link ExecutionException})
	 * @since 4.2
	 * @see ExecutionException
	 */
	public static <V> ListenableFuture<V> forExecutionException(Throwable ex) {
		return new AsyncResult<V>(null,
				(ex instanceof ExecutionException ? (ExecutionException) ex : new ExecutionException(ex)));
	}

}
