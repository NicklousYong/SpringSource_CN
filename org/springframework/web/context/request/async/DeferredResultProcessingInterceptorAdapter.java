/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2015 the original author or authors.
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

package org.springframework.web.context.request.async;

import org.springframework.web.context.request.NativeWebRequest;

/**
 * Abstract adapter class for the {@link DeferredResultProcessingInterceptor}
 * interface for simplified implementation of individual methods.
 *
 * <p>
 *  {@link DeferredResultProcessingInterceptor}接口的抽象适配器类,用于简化各个方法的实现
 * 
 * 
 * @author Rossen Stoyanchev
 * @author Rob Winch
 * @since 3.2
 */
public abstract class DeferredResultProcessingInterceptorAdapter implements DeferredResultProcessingInterceptor {

	/**
	 * This implementation is empty.
	 * <p>
	 *  这个实现是空的
	 * 
	 */
	@Override
	public <T> void beforeConcurrentHandling(NativeWebRequest request, DeferredResult<T> deferredResult)
			throws Exception {
	}

	/**
	 * This implementation is empty.
	 * <p>
	 *  这个实现是空的
	 * 
	 */
	@Override
	public <T> void preProcess(NativeWebRequest request, DeferredResult<T> deferredResult) throws Exception {
	}

	/**
	 * This implementation is empty.
	 * <p>
	 * 这个实现是空的
	 * 
	 */
	@Override
	public <T> void postProcess(NativeWebRequest request, DeferredResult<T> deferredResult,
			Object concurrentResult) throws Exception {
	}

	/**
	 * This implementation returns {@code true} by default allowing other interceptors
	 * to be given a chance to handle the timeout.
	 * <p>
	 *  此实现默认返回{@code true},默认情况下允许其他拦截器有机会处理超时
	 * 
	 */
	@Override
	public <T> boolean handleTimeout(NativeWebRequest request, DeferredResult<T> deferredResult) throws Exception {
		return true;
	}

	/**
	 * This implementation is empty.
	 * <p>
	 *  这个实现是空的
	 */
	@Override
	public <T> void afterCompletion(NativeWebRequest request, DeferredResult<T> deferredResult) throws Exception {
	}

}
