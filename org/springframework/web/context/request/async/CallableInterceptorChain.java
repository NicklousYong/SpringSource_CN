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

import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.web.context.request.NativeWebRequest;

/**
 * Assists with the invocation of {@link CallableProcessingInterceptor}'s.
 *
 * <p>
 *  除非适用法律要求或以书面形式同意,根据许可证分发的软件以"按原样"分发,不附带任何明示或暗示的担保或条件,请参阅许可证中有关特定语言的权限和限制许可证
 * 
 * 
 * @author Rossen Stoyanchev
 * @author Rob Winch
 * @since 3.2
 */
class CallableInterceptorChain {

	private static final Log logger = LogFactory.getLog(CallableInterceptorChain.class);

	private final List<CallableProcessingInterceptor> interceptors;

	private int preProcessIndex = -1;


	public CallableInterceptorChain(List<CallableProcessingInterceptor> interceptors) {
		this.interceptors = interceptors;
	}

	public void applyBeforeConcurrentHandling(NativeWebRequest request, Callable<?> task) throws Exception {
		for (CallableProcessingInterceptor interceptor : this.interceptors) {
			interceptor.beforeConcurrentHandling(request, task);
		}
	}

	public void applyPreProcess(NativeWebRequest request, Callable<?> task) throws Exception {
		for (CallableProcessingInterceptor interceptor : this.interceptors) {
			interceptor.preProcess(request, task);
			this.preProcessIndex++;
		}
	}

	public Object applyPostProcess(NativeWebRequest request, Callable<?> task, Object concurrentResult) {
		Throwable exceptionResult = null;
		for (int i = this.preProcessIndex; i >= 0; i--) {
			try {
				this.interceptors.get(i).postProcess(request, task, concurrentResult);
			}
			catch (Throwable t) {
				// Save the first exception but invoke all interceptors
				if (exceptionResult != null) {
					logger.error("postProcess error", t);
				}
				else {
					exceptionResult = t;
				}
			}
		}
		return (exceptionResult != null) ? exceptionResult : concurrentResult;
	}

	public Object triggerAfterTimeout(NativeWebRequest request, Callable<?> task) {
		for (CallableProcessingInterceptor interceptor : this.interceptors) {
			try {
				Object result = interceptor.handleTimeout(request, task);
				if (result == CallableProcessingInterceptor.RESPONSE_HANDLED) {
					break;
				}
				else if (result != CallableProcessingInterceptor.RESULT_NONE) {
					return result;
				}
			}
			catch (Throwable t) {
				return t;
			}
		}
		return CallableProcessingInterceptor.RESULT_NONE;
	}

	public void triggerAfterCompletion(NativeWebRequest request, Callable<?> task) {
		for (int i = this.interceptors.size()-1; i >= 0; i--) {
			try {
				this.interceptors.get(i).afterCompletion(request, task);
			}
			catch (Throwable t) {
				logger.error("afterCompletion error", t);
			}
		}
	}

}
