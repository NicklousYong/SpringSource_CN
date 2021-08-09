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

package org.springframework.web.servlet.mvc.method.annotation;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.Assert;

/**
 * A controller method return value type for asynchronous request processing
 * where one or more objects are written to the response.
 *
 * <p>While {@link org.springframework.web.context.request.async.DeferredResult}
 * is used to produce a single result, a {@code ResponseBodyEmitter} can be used
 * to send multiple objects where each object is written with a compatible
 * {@link org.springframework.http.converter.HttpMessageConverter}.
 *
 * <p>Supported as a return type on its own as well as within a
 * {@link org.springframework.http.ResponseEntity}.
 *
 * <pre>
 * &#064;RequestMapping(value="/stream", method=RequestMethod.GET)
 * public ResponseBodyEmitter handle() {
 * 	   ResponseBodyEmitter emitter = new ResponseBodyEmitter();
 * 	   // Pass the emitter to another component...
 * 	   return emitter;
 * }
 *
 * // in another thread
 * emitter.send(foo1);
 *
 * // and again
 * emitter.send(foo2);
 *
 * // and done
 * emitter.complete();
 * </pre>
 *
 * <p>
 *  用于异步请求处理的控制器方法返回值类型,其中一个或多个对象被写入响应
 * 
 * <p>虽然{@link orgspringframeworkwebcontextrequestasyncDeferredResult}用于产生单个结果,但可以使用{@code ResponseBodyEmitter}
 * 发送多个对象,其中每个对象都使用兼容的{@link orgspringframeworkhttpconverterHttpMessageConverter}。
 * 
 *  <p>作为返回类型自己以及{@link orgspringframeworkhttpResponseEntity}中的支持
 * 
 * <pre>
 *  @RequestMapping(value ="/ stream",method = RequestMethodGET)public ResponseBodyEmitter handle(){ResponseBodyEmitter emitter = new ResponseBodyEmitter(); //将发射器传递到另一个组件返回发射器; }
 * 。
 * 
 *  //在另一个线程emitoutsend(foo1);
 * 
 *  //再次发射端(foo2);
 * 
 *  //并完成了emitcomplete();
 * </pre>
 * 
 * 
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @since 4.2
 */
public class ResponseBodyEmitter {

	private final Long timeout;

	private final Set<DataWithMediaType> earlySendAttempts = new LinkedHashSet<DataWithMediaType>(8);

	private Handler handler;

	private boolean complete;

	private Throwable failure;

	private final DefaultCallback timeoutCallback = new DefaultCallback();

	private final DefaultCallback completionCallback = new DefaultCallback();


	/**
	 * Create a new ResponseBodyEmitter instance.
	 * <p>
	 *  创建一个新的ResponseBodyEmitter实例
	 * 
	 */
	public ResponseBodyEmitter() {
		this.timeout = null;
	}

	/**
	 * Create a ResponseBodyEmitter with a custom timeout value.
	 * <p>By default not set in which case the default configured in the MVC
	 * Java Config or the MVC namespace is used, or if that's not set, then the
	 * timeout depends on the default of the underlying server.
	 * <p>
	 * 创建一个具有自定义超时值的ResponseBodyEmitter <p>默认情况下不设置在MVC Java Config或MVC命名空间中配置的默认值,或者如果未设置,则超时取决于底层服务器的默认值
	 * 
	 * 
	 * @param timeout timeout value in milliseconds
	 */
	public ResponseBodyEmitter(Long timeout) {
		this.timeout = timeout;
	}


	/**
	 * Return the configured timeout value, if any.
	 * <p>
	 *  返回配置的超时值(如果有)
	 * 
	 */
	public Long getTimeout() {
		return this.timeout;
	}


	synchronized void initialize(Handler handler) throws IOException {
		this.handler = handler;

		for (DataWithMediaType sendAttempt : this.earlySendAttempts) {
			sendInternal(sendAttempt.getData(), sendAttempt.getMediaType());
		}
		this.earlySendAttempts.clear();

		if (this.complete) {
			if (this.failure != null) {
				this.handler.completeWithError(this.failure);
			}
			else {
				this.handler.complete();
			}
		}
		else {
			this.handler.onTimeout(this.timeoutCallback);
			this.handler.onCompletion(this.completionCallback);
		}
	}

	/**
	 * Invoked after the response is updated with the status code and headers,
	 * if the ResponseBodyEmitter is wrapped in a ResponseEntity, but before the
	 * response is committed, i.e. before the response body has been written to.
	 * <p>The default implementation is empty.
	 * <p>
	 *  如果ResponseBodyEmitter被包装在ResponseEntity中,但在响应提交之前,即在响应正文被写入<p>之前,状态代码和标题更新响应后调用。默认实现为空
	 * 
	 */
	protected void extendResponse(ServerHttpResponse outputMessage) {
	}

	/**
	 * Write the given object to the response.
	 * <p>If any exception occurs a dispatch is made back to the app server where
	 * Spring MVC will pass the exception through its exception handling mechanism.
	 * <p>
	 *  将给定对象写入响应<p>如果发生任何异常,调度将返回到应用程序服务器,Spring MVC将通过异常处理机制传递异常
	 * 
	 * 
	 * @param object the object to write
	 * @throws IOException raised when an I/O error occurs
	 * @throws java.lang.IllegalStateException wraps any other errors
	 */
	public void send(Object object) throws IOException {
		send(object, null);
	}

	/**
	 * Write the given object to the response also using a MediaType hint.
	 * <p>If any exception occurs a dispatch is made back to the app server where
	 * Spring MVC will pass the exception through its exception handling mechanism.
	 * <p>
	 * 使用MediaType提示将给定对象写入响应<p>如果发生任何异常,调度将返回到应用程序服务器,Spring MVC将通过其异常处理机制传递异常
	 * 
	 * 
	 * @param object the object to write
	 * @param mediaType a MediaType hint for selecting an HttpMessageConverter
	 * @throws IOException raised when an I/O error occurs
	 * @throws java.lang.IllegalStateException wraps any other errors
	 */
	public synchronized void send(Object object, MediaType mediaType) throws IOException {
		Assert.state(!this.complete, "ResponseBodyEmitter is already set complete");
		sendInternal(object, mediaType);
	}

	private void sendInternal(Object object, MediaType mediaType) throws IOException {
		if (object != null) {
			if (this.handler != null) {
				try {
					this.handler.send(object, mediaType);
				}
				catch (IOException ex) {
					completeWithError(ex);
					throw ex;
				}
				catch (Throwable ex) {
					completeWithError(ex);
					throw new IllegalStateException("Failed to send " + object, ex);
				}
			}
			else {
				this.earlySendAttempts.add(new DataWithMediaType(object, mediaType));
			}
		}
	}

	/**
	 * Complete request processing.
	 * <p>A dispatch is made into the app server where Spring MVC completes
	 * asynchronous request processing.
	 * <p>
	 *  完成请求处理<p>在Spring MVC完成异步请求处理的应用服务器中进行调度
	 * 
	 */
	public synchronized void complete() {
		this.complete = true;
		if (this.handler != null) {
			this.handler.complete();
		}
	}

	/**
	 * Complete request processing with an error.
	 * <p>A dispatch is made into the app server where Spring MVC will pass the
	 * exception through its exception handling mechanism.
	 * <p>
	 *  完整的请求处理与错误<p>调度到应用程序服务器,Spring MVC将通过其异常处理机制传递异常
	 * 
	 */
	public synchronized void completeWithError(Throwable ex) {
		this.complete = true;
		this.failure = ex;
		if (this.handler != null) {
			this.handler.completeWithError(ex);
		}
	}

	/**
	 * Register code to invoke when the async request times out. This method is
	 * called from a container thread when an async request times out.
	 * <p>
	 *  注册异步请求超时时调用的代码当异步请求超时时,该方法从容器线程调用
	 * 
	 */
	public synchronized void onTimeout(Runnable callback) {
		this.timeoutCallback.setDelegate(callback);
	}

	/**
	 * Register code to invoke when the async request completes. This method is
	 * called from a container thread when an async request completed for any
	 * reason including timeout and network error. This method is useful for
	 * detecting that a {@code ResponseBodyEmitter} instance is no longer usable.
	 * <p>
	 * 注册异步请求完成时调用的代码当异步请求以任何原因(包括超时和网络错误)完成时,从容器线程调用此方法此方法对于检测到{@code ResponseBodyEmitter}实例不再可用
	 * 
	 */
	public synchronized void onCompletion(Runnable callback) {
		this.completionCallback.setDelegate(callback);
	}


	/**
	 * Handle sent objects and complete request processing.
	 * <p>
	 *  处理发送对象并完成请求处理
	 * 
	 */
	interface Handler {

		void send(Object data, MediaType mediaType) throws IOException;

		void complete();

		void completeWithError(Throwable failure);

		void onTimeout(Runnable callback);

		void onCompletion(Runnable callback);
	}


	/**
	 * A simple holder of data to be written along with a MediaType hint for
	 * selecting a message converter to write with.
	 * <p>
	 *  一个简单的数据持有者将要连同MediaType提示一起选择要写入的消息转换器
	 */
	public static class DataWithMediaType {

		private final Object data;

		private final MediaType mediaType;

		public DataWithMediaType(Object data, MediaType mediaType) {
			this.data = data;
			this.mediaType = mediaType;
		}

		public Object getData() {
			return this.data;
		}

		public MediaType getMediaType() {
			return this.mediaType;
		}
	}


	private class DefaultCallback implements Runnable {

		private Runnable delegate;

		public void setDelegate(Runnable delegate) {
			this.delegate = delegate;
		}

		@Override
		public void run() {
			ResponseBodyEmitter.this.complete = true;
			if (this.delegate != null) {
				this.delegate.run();
			}
		}
	}

}
