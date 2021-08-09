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

package org.springframework.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/**
 * A {@link org.springframework.util.concurrent.ListenableFuture ListenableFuture}
 * whose value can be set via {@link #set(Object)} or
 * {@link #setException(Throwable)}. It may also be cancelled.
 *
 * <p>Inspired by {@code com.google.common.util.concurrent.SettableFuture}.
 *
 * <p>
 *  可以通过{@link #set(Object)}或{@link #setException(Throwable)}设置其值的{@link orgspringframeworkutilconcurrentListenableFuture ListenableFuture}
 * 它也可能被取消。
 * 
 * <p>灵感来自{@code comgooglecommonutilconcurrentSettableFuture}
 * 
 * 
 * @author Mattias Severson
 * @author Rossen Stoyanchev
 * @since 4.1
 */
public class SettableListenableFuture<T> implements ListenableFuture<T> {

	private final SettableTask<T> settableTask;

	private final ListenableFutureTask<T> listenableFuture;


	public SettableListenableFuture() {
		this.settableTask = new SettableTask<T>();
		this.listenableFuture = new ListenableFutureTask<T>(this.settableTask);
	}


	/**
	 * Set the value of this future. This method will return {@code true} if the
	 * value was set successfully, or {@code false} if the future has already been
	 * set or cancelled.
	 * <p>
	 *  设置未来的值如果值设置成功,则此方法将返回{@code true},如果将来已设置或取消,则返回{@code false}
	 * 
	 * 
	 * @param value the value that will be set
	 * @return {@code true} if the value was successfully set, else {@code false}
	 */
	public boolean set(T value) {
		boolean success = this.settableTask.setValue(value);
		if (success) {
			this.listenableFuture.run();
		}
		return success;
	}

	/**
	 * Set the exception of this future. This method will return {@code true} if the
	 * exception was set successfully, or {@code false} if the future has already been
	 * set or cancelled.
	 * <p>
	 *  设置未来的例外如果异常设置成功,则此方法将返回{@code true},如果将来已设置或取消,则返回{@code false}
	 * 
	 * 
	 * @param exception the value that will be set
	 * @return {@code true} if the exception was successfully set, else {@code false}
	 */
	public boolean setException(Throwable exception) {
		Assert.notNull(exception, "Exception must not be null");
		boolean success = this.settableTask.setException(exception);
		if (success) {
			this.listenableFuture.run();
		}
		return success;
	}

	@Override
	public void addCallback(ListenableFutureCallback<? super T> callback) {
		this.listenableFuture.addCallback(callback);
	}

	@Override
	public void addCallback(SuccessCallback<? super T> successCallback, FailureCallback failureCallback) {
		this.listenableFuture.addCallback(successCallback, failureCallback);
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		this.settableTask.setCancelled();
		boolean cancelled = this.listenableFuture.cancel(mayInterruptIfRunning);
		if (cancelled && mayInterruptIfRunning) {
			interruptTask();
		}
		return cancelled;
	}

	@Override
	public boolean isCancelled() {
		return this.listenableFuture.isCancelled();
	}

	@Override
	public boolean isDone() {
		return this.listenableFuture.isDone();
	}

	/**
	 * Retrieve the value.
	 * <p>Will return the value if it has been set via {@link #set(Object)},
	 * throw an {@link java.util.concurrent.ExecutionException} if it has been
	 * set via {@link #setException(Throwable)} or  throw a
	 * {@link java.util.concurrent.CancellationException} if it has been cancelled.
	 * <p>
	 *  检索值<p>如果已通过{@link #set(Object)}设置值,将返回值,如果已通过{@link #setException(Throwable)}设置,则抛出{@link javautilconcurrentExecutionException}
	 * 或抛出如果已被取消,则为{@link javautilconcurrentCancellationException}。
	 * 
	 * 
	 * @return The value associated with this future.
	 */
	@Override
	public T get() throws InterruptedException, ExecutionException {
		return this.listenableFuture.get();
	}

	/**
	 * Retrieve the value.
	 * <p>Will return the value if it has been set via {@link #set(Object)},
	 * throw an {@link java.util.concurrent.ExecutionException} if it has been
	 * set via {@link #setException(Throwable)} or  throw a
	 * {@link java.util.concurrent.CancellationException} if it has been cancelled.
	 * <p>
	 * 检索值<p>如果已通过{@link #set(Object)}设置值,将返回值,如果已通过{@link #setException(Throwable)}设置,则抛出{@link javautilconcurrentExecutionException}
	 * 或抛出如果已被取消,则为{@link javautilconcurrentCancellationException}。
	 * 
	 * 
	 * @param timeout the maximum time to wait.
	 * @param unit the time unit of the timeout argument.
	 * @return The value associated with this future.
	 */
	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return this.listenableFuture.get(timeout, unit);
	}

	/**
	 * Subclasses can override this method to implement interruption of the future's
	 * computation. The method is invoked automatically by a successful call to
	 * {@link #cancel(boolean) cancel(true)}.
	 * <p>The default implementation does nothing.
	 * <p>
	 */
	protected void interruptTask() {
	}


	private static class SettableTask<T> implements Callable<T> {

		private static final Object NO_VALUE = new Object();

		private final AtomicReference<Object> value = new AtomicReference<Object>(NO_VALUE);

		private volatile boolean cancelled = false;

		public boolean setValue(T value) {
			if (this.cancelled) {
				return false;
			}
			return this.value.compareAndSet(NO_VALUE, value);
		}

		public boolean setException(Throwable exception) {
			if (this.cancelled) {
				return false;
			}
			return this.value.compareAndSet(NO_VALUE, exception);
		}

		public void setCancelled() {
			this.cancelled = true;
		}

		@SuppressWarnings("unchecked")
		@Override
		public T call() throws Exception {
			Object val = this.value.get();
			if (val instanceof Throwable) {
				ReflectionUtils.rethrowException((Throwable) val);
			}
			return (T) val;
		}
	}

}
