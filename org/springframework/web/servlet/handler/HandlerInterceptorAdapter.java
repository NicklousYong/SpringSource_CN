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

package org.springframework.web.servlet.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * Abstract adapter class for the {@link AsyncHandlerInterceptor} interface,
 * for simplified implementation of pre-only/post-only interceptors.
 *
 * <p>
 *  用于{@link AsyncHandlerInterceptor}接口的抽象适配器类,用于简化仅前/后唯一拦截器的实现
 * 
 * 
 * @author Juergen Hoeller
 * @since 05.12.2003
 */
public abstract class HandlerInterceptorAdapter implements AsyncHandlerInterceptor {

	/**
	 * This implementation always returns {@code true}.
	 * <p>
	 *  这个实现总是返回{@code true}
	 * 
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		return true;
	}

	/**
	 * This implementation is empty.
	 * <p>
	 *  这个实现是空的
	 * 
	 */
	@Override
	public void postHandle(
			HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
			throws Exception {
	}

	/**
	 * This implementation is empty.
	 * <p>
	 * 这个实现是空的
	 * 
	 */
	@Override
	public void afterCompletion(
			HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}

	/**
	 * This implementation is empty.
	 * <p>
	 *  这个实现是空的
	 */
	@Override
	public void afterConcurrentHandlingStarted(
			HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
	}

}
