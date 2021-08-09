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

package org.springframework.http.server;

/**
 * A control that can put the processing of an HTTP request in asynchronous mode during
 * which the response remains open until explicitly closed.
 *
 * <p>
 *  一个可以将HTTP请求处理为异步模式的控件,在该模式下,响应保持打开状态,直到显式关闭
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public interface ServerHttpAsyncRequestControl {

	/**
	 * Enable asynchronous processing after which the response remains open until a call
	 * to {@link #complete()} is made or the server times out the request. Once enabled,
	 * additional calls to this method are ignored.
	 * <p>
	 * 启用异步处理,之后响应将保持打开,直到调用{@link #complete()}或服务器超时请求。一旦启用,将忽略对此方法的其他调用
	 * 
	 */
	void start();

	/**
	 * A variation on {@link #start()} that allows specifying a timeout value to use to
	 * use for asynchronous processing. If {@link #complete()} is not called within the
	 * specified value, the request times out.
	 * <p>
	 *  {@link #start()}的变体,允许指定用于异步处理的超时值如果{@link #complete()}未在指定值内调用,则请求超时
	 * 
	 */
	void start(long timeout);

	/**
	 * Return whether asynchronous request processing has been started.
	 * <p>
	 *  返回异步请求处理是否已经启动
	 * 
	 */
	boolean isStarted();

	/**
	 * Mark asynchronous request processing as completed.
	 * <p>
	 *  将异步请求处理标记为已完成
	 * 
	 */
	void complete();

	/**
	 * Return whether asynchronous request processing has been completed.
	 * <p>
	 *  返回异步请求处理是否已完成
	 */
	boolean isCompleted();

}
