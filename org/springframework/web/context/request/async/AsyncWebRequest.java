/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2012 the original author or authors.
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
 * Extends {@link NativeWebRequest} with methods for asynchronous request processing.
 *
 * <p>
 *  使用异步请求处理方法扩展{@link NativeWebRequest}
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 3.2
 */
public interface AsyncWebRequest extends NativeWebRequest {

	/**
	 * Set the time required for concurrent handling to complete.
	 * This property should not be set when concurrent handling is in progress,
	 * i.e. when {@link #isAsyncStarted()} is {@code true}.
	 * <p>
	 * 设置并发处理所需的时间完成当并发处理正在进行时,不应设置此属性,即{@link #isAsyncStarted()}为{@code true}
	 * 
	 * 
	 * @param timeout amount of time in milliseconds; {@code null} means no
	 * 	timeout, i.e. rely on the default timeout of the container.
	 */
	void setTimeout(Long timeout);

	/**
	 * Add a handler to invoke when concurrent handling has timed out.
	 * <p>
	 *  当并发处理超时时,添加一个处理程序进行调用
	 * 
	 */
	void addTimeoutHandler(Runnable runnable);

	/**
	 * Add a handle to invoke when request processing completes.
	 * <p>
	 *  在请求处理完成时添加一个句柄来调用
	 * 
	 */
	void addCompletionHandler(Runnable runnable);

	/**
	 * Mark the start of asynchronous request processing so that when the main
	 * processing thread exits, the response remains open for further processing
	 * in another thread.
	 * <p>
	 *  标记异步请求处理的开始,以便当主处理线程退出时,响应保持打开以在另一个线程中进一步处理
	 * 
	 * 
	 * @throws IllegalStateException if async processing has completed or is not supported
	 */
	void startAsync();

	/**
	 * Whether the request is in async mode following a call to {@link #startAsync()}.
	 * Returns "false" if asynchronous processing never started, has completed,
	 * or the request was dispatched for further processing.
	 * <p>
	 *  在{@link #startAsync()}调用后请求是否处于异步模式如果异步处理从未启动,已完成或请求进行进一步处理,则返回"false"
	 * 
	 */
	boolean isAsyncStarted();

	/**
	 * Dispatch the request to the container in order to resume processing after
	 * concurrent execution in an application thread.
	 * <p>
	 * 将请求发送到容器,以便在应用程序线程中并发执行后恢复处理
	 * 
	 */
	void dispatch();

	/**
	 * Whether asynchronous processing has completed.
	 * <p>
	 *  异步处理是否完成
	 */
	boolean isAsyncComplete();

}
