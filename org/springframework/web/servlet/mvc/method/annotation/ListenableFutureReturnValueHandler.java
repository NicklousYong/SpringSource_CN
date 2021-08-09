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

import org.springframework.core.MethodParameter;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.method.support.AsyncHandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Handles return values of type
 * {@link org.springframework.util.concurrent.ListenableFuture}.
 *
 * <p>
 *  除非适用法律要求或以书面形式同意,根据许可证分发的软件以"按原样"分发,不附带任何明示或暗示的担保或条件,请参阅许可证中有关特定语言的权限和限制许可证
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 4.1
 * @deprecated as of 4.3 {@link DeferredResultMethodReturnValueHandler} supports
 * ListenableFuture return values via an adapter mechanism.
 */
@Deprecated
public class ListenableFutureReturnValueHandler implements AsyncHandlerMethodReturnValueHandler {

	@Override
	public boolean supportsReturnType(MethodParameter returnType) {
		return ListenableFuture.class.isAssignableFrom(returnType.getParameterType());
	}

	@Override
	public boolean isAsyncReturnValue(Object returnValue, MethodParameter returnType) {
		return (returnValue != null && returnValue instanceof ListenableFuture);
	}

	@Override
	public void handleReturnValue(Object returnValue, MethodParameter returnType,
			ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {

		if (returnValue == null) {
			mavContainer.setRequestHandled(true);
			return;
		}

		final DeferredResult<Object> deferredResult = new DeferredResult<Object>();
		WebAsyncUtils.getAsyncManager(webRequest).startDeferredResultProcessing(deferredResult, mavContainer);

		ListenableFuture<?> future = (ListenableFuture<?>) returnValue;
		future.addCallback(new ListenableFutureCallback<Object>() {
			@Override
			public void onSuccess(Object result) {
				deferredResult.setResult(result);
			}
			@Override
			public void onFailure(Throwable ex) {
				deferredResult.setErrorResult(ex);
			}
		});
	}

}
