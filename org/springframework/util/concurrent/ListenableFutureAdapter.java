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

import java.util.concurrent.ExecutionException;

/**
 * Abstract class that adapts a {@link ListenableFuture} parameterized over S into a
 * {@code ListenableFuture} parameterized over T. All methods are delegated to the
 * adaptee, where {@link #get()}, {@link #get(long, java.util.concurrent.TimeUnit)},
 * and {@link ListenableFutureCallback#onSuccess(Object)} call {@link #adapt(Object)}
 * on the adaptee's result.
 *
 * <p>
 * 
 * @author Arjen Poutsma
 * @since 4.0
 * @param <T> the type of this {@code Future}
 * @param <S> the type of the adaptee's {@code Future}
 */
public abstract class ListenableFutureAdapter<T, S> extends FutureAdapter<T, S> implements ListenableFuture<T> {

	/**
	 * Construct a new {@code ListenableFutureAdapter} with the given adaptee.
	 * <p>
	 * 将通过S参数化的{@link ListenableFuture}适配为通过T All方法参数化的{@code ListenableFuture}的抽象类被委派给适配器,其中{@link #get()},
	 * {@link #get(long,javautilconcurrentTimeUnit )}和{@link ListenableFutureCallback#onSuccess(Object)}调用{@link #adapt(Object)}
	 * 对适应者的结果。
	 * 
	 * 
	 * @param adaptee the future to adapt to
	 */
	protected ListenableFutureAdapter(ListenableFuture<S> adaptee) {
		super(adaptee);
	}


	@Override
	public void addCallback(final ListenableFutureCallback<? super T> callback) {
		addCallback(callback, callback);
	}

	@Override
	public void addCallback(final SuccessCallback<? super T> successCallback, final FailureCallback failureCallback) {
		ListenableFuture<S> listenableAdaptee = (ListenableFuture<S>) getAdaptee();
		listenableAdaptee.addCallback(new ListenableFutureCallback<S>() {
			@Override
			public void onSuccess(S result) {
				T adapted;
				try {
					adapted = adaptInternal(result);
				}
				catch (ExecutionException ex) {
					Throwable cause = ex.getCause();
					onFailure(cause != null ? cause : ex);
					return;
				}
				catch (Throwable ex) {
					onFailure(ex);
					return;
				}
				successCallback.onSuccess(adapted);
			}
			@Override
			public void onFailure(Throwable ex) {
				failureCallback.onFailure(ex);
			}
		});
	}

}