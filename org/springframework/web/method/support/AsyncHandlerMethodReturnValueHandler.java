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

package org.springframework.web.method.support;

import org.springframework.core.MethodParameter;

/**
 * A {@link HandlerMethodReturnValueHandler} that handles return values that
 * represent asynchronous computation. Such handlers need to be invoked with
 * precedence over other handlers that might otherwise match the return value
 * type: e.g. a method that returns a Promise type that is also annotated with
 * {@code @ResponseBody}.
 *
 * <p>In {@link #handleReturnValue}, implementations of this class should create
 * a {@link org.springframework.web.context.request.async.DeferredResult} or
 * adapt to it and then invoke {@code WebAsyncManager} to start async processing.
 * For example:
 * <pre>
 * DeferredResult<?> deferredResult = (DeferredResult<?>) returnValue;
 * WebAsyncUtils.getAsyncManager(webRequest).startDeferredResultProcessing(deferredResult, mavContainer);
 * </pre>
 *
 * <p>
 * 处理代表异步计算的返回值的{@link HandlerMethodReturnValueHandler}这样的处理程序需要优先于其他可能匹配返回值类型的处理程序的调用方式：例如,返回还使用{@code注释的Promise类型的方法}
 *  @ResponseBody。
 * 
 *  <p>在{@link #handleReturnValue}中,此类的实现应该创建一个{@link orgspringframeworkwebcontextrequestasyncDeferredResult}
 * 或适应它,然后调用{@code WebAsyncManager}来启动异步处理例如：。
 * <pre>
 *  deferredResult <?> deferredResult =(DeferredResult <?>)returnValue; WebAsyncUtilsgetAsyncManager(web
 * 
 * @author Rossen Stoyanchev
 * @since 4.2
 */
public interface AsyncHandlerMethodReturnValueHandler extends HandlerMethodReturnValueHandler {

	/**
	 * Whether the given return value represents asynchronous computation.
	 * <p>
	 * Request)startDeferredResultProcessing(deferredResult,mavContainer);。
	 * </pre>
	 * 
	 * 
	 * @param returnValue the return value
	 * @param returnType the return type
	 * @return {@code true} if the return value is asynchronous.
	 */
	boolean isAsyncReturnValue(Object returnValue, MethodParameter returnType);

}
