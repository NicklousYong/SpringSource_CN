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

package org.springframework.web.servlet.mvc.method.annotation;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;

import org.springframework.core.MethodParameter;
import org.springframework.lang.UsesJava8;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.method.support.AsyncHandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Handler for return values of type {@link DeferredResult}, {@link ListenableFuture},
 * {@link CompletionStage} and any other async type with a {@link #getAdapterMap()
 * registered adapter}.
 *
 * <p>
 *  处理程序返回值类型为{@link DeferredResult},{@link ListenableFuture},{@link CompletionStage}和任何其他异步类型与{@link #getAdapterMap()注册适配器}
 * 。
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 3.2
 */
public class DeferredResultMethodReturnValueHandler implements AsyncHandlerMethodReturnValueHandler {

	private final Map<Class<?>, DeferredResultAdapter> adapterMap;


	public DeferredResultMethodReturnValueHandler() {
		this.adapterMap = new HashMap<Class<?>, DeferredResultAdapter>(5);
		this.adapterMap.put(DeferredResult.class, new SimpleDeferredResultAdapter());
		this.adapterMap.put(ListenableFuture.class, new ListenableFutureAdapter());
		if (ClassUtils.isPresent("java.util.concurrent.CompletionStage", getClass().getClassLoader())) {
			this.adapterMap.put(CompletionStage.class, new CompletionStageAdapter());
		}
	}


	/**
	 * Return the map with {@code DeferredResult} adapters.
	 * <p>By default the map contains adapters for {@code DeferredResult}, which
	 * simply downcasts, {@link ListenableFuture}, and {@link CompletionStage}.
	 * <p>
	 * 使用{@code DeferredResult}适配器返回地图<p>默认情况下,该地图包含适用于{@code DeferredResult}的适配器,简单地介绍{@link ListenableFuture}
	 * 和{@link CompletionStage}。
	 * 
	 * 
	 * @return the map of adapters
	 */
	public Map<Class<?>, DeferredResultAdapter> getAdapterMap() {
		return this.adapterMap;
	}

	private DeferredResultAdapter getAdapterFor(Class<?> type) {
		for (Class<?> adapteeType : getAdapterMap().keySet()) {
			if (adapteeType.isAssignableFrom(type)) {
				return getAdapterMap().get(adapteeType);
			}
		}
		return null;
	}


	@Override
	public boolean supportsReturnType(MethodParameter returnType) {
		return (getAdapterFor(returnType.getParameterType()) != null);
	}

	@Override
	public boolean isAsyncReturnValue(Object returnValue, MethodParameter returnType) {
		return (returnValue != null && (getAdapterFor(returnValue.getClass()) != null));
	}

	@Override
	public void handleReturnValue(Object returnValue, MethodParameter returnType,
			ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {

		if (returnValue == null) {
			mavContainer.setRequestHandled(true);
			return;
		}

		DeferredResultAdapter adapter = getAdapterFor(returnValue.getClass());
		Assert.notNull(adapter);
		DeferredResult<?> result = adapter.adaptToDeferredResult(returnValue);
		WebAsyncUtils.getAsyncManager(webRequest).startDeferredResultProcessing(result, mavContainer);
	}


	/**
	 * Adapter for {@code DeferredResult} return values.
	 * <p>
	 *  适用于{@code DeferredResult}的返回值
	 * 
	 */
	private static class SimpleDeferredResultAdapter implements DeferredResultAdapter {

		@Override
		public DeferredResult<?> adaptToDeferredResult(Object returnValue) {
			Assert.isInstanceOf(DeferredResult.class, returnValue);
			return (DeferredResult<?>) returnValue;
		}
	}


	/**
	 * Adapter for {@code ListenableFuture} return values.
	 * <p>
	 *  适用于{@code ListenableFuture}的返回值
	 * 
	 */
	private static class ListenableFutureAdapter implements DeferredResultAdapter {

		@Override
		public DeferredResult<?> adaptToDeferredResult(Object returnValue) {
			Assert.isInstanceOf(ListenableFuture.class, returnValue);
			final DeferredResult<Object> result = new DeferredResult<Object>();
			((ListenableFuture<?>) returnValue).addCallback(new ListenableFutureCallback<Object>() {
				@Override
				public void onSuccess(Object value) {
					result.setResult(value);
				}
				@Override
				public void onFailure(Throwable ex) {
					result.setErrorResult(ex);
				}
			});
			return result;
		}
	}


	/**
	 * Adapter for {@code CompletionStage} return values.
	 * <p>
	 *  {@code CompletionStage}的适配器返回值
	 */
	@UsesJava8
	private static class CompletionStageAdapter implements DeferredResultAdapter {

		@Override
		public DeferredResult<?> adaptToDeferredResult(Object returnValue) {
			Assert.isInstanceOf(CompletionStage.class, returnValue);
			final DeferredResult<Object> result = new DeferredResult<Object>();
			@SuppressWarnings("unchecked")
			CompletionStage<?> future = (CompletionStage<?>) returnValue;
			future.handle(new BiFunction<Object, Throwable, Object>() {
				@Override
				public Object apply(Object value, Throwable ex) {
					if (ex != null) {
						result.setErrorResult(ex);
					}
					else {
						result.setResult(value);
					}
					return null;
				}
			});
			return result;
		}
	}

}
