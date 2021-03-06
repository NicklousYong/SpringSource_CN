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

package org.springframework.messaging.handler.invocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.util.Assert;

/**
 * A HandlerMethodReturnValueHandler that wraps and delegates to others.
 *
 * <p>
 *  一个HandlerMethodReturnValueHandler,用于包装和委托给他人
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public class HandlerMethodReturnValueHandlerComposite implements HandlerMethodReturnValueHandler {

	private static final Log logger = LogFactory.getLog(HandlerMethodReturnValueHandlerComposite.class);

	private final List<HandlerMethodReturnValueHandler> returnValueHandlers = new ArrayList<HandlerMethodReturnValueHandler>();


	/**
	 * Return a read-only list with the configured handlers.
	 * <p>
	 *  返回一个只读列表与配置的处理程序
	 * 
	 */
	public List<HandlerMethodReturnValueHandler> getReturnValueHandlers() {
		return Collections.unmodifiableList(this.returnValueHandlers);
	}

	/**
	 * Clear the list of configured handlers.
	 * <p>
	 *  清除已配置的处理程序列表
	 * 
	 */
	public void clear() {
		this.returnValueHandlers.clear();
	}

	/**
	 * Add the given {@link HandlerMethodReturnValueHandler}.
	 * <p>
	 *  添加给定的{@link HandlerMethodReturnValueHandler}
	 * 
	 */
	public HandlerMethodReturnValueHandlerComposite addHandler(HandlerMethodReturnValueHandler returnValuehandler) {
		this.returnValueHandlers.add(returnValuehandler);
		return this;
	}

	/**
	 * Add the given {@link HandlerMethodReturnValueHandler}s.
	 * <p>
	 * 添加给定的{@link HandlerMethodReturnValueHandler}
	 */
	public HandlerMethodReturnValueHandlerComposite addHandlers(List<? extends HandlerMethodReturnValueHandler> handlers) {
		if (handlers != null) {
			for (HandlerMethodReturnValueHandler handler : handlers) {
				this.returnValueHandlers.add(handler);
			}
		}
		return this;
	}

	@Override
	public boolean supportsReturnType(MethodParameter returnType) {
		return getReturnValueHandler(returnType) != null;
	}

	private HandlerMethodReturnValueHandler getReturnValueHandler(MethodParameter returnType) {
		for (HandlerMethodReturnValueHandler handler : this.returnValueHandlers) {
			if (handler.supportsReturnType(returnType)) {
				return handler;
			}
		}
		return null;
	}

	@Override
	public void handleReturnValue(Object returnValue, MethodParameter returnType, Message<?> message)
			throws Exception {

		HandlerMethodReturnValueHandler handler = getReturnValueHandler(returnType);
		Assert.notNull(handler, "No handler for return value type [" + returnType.getParameterType().getName() + "]");
		if (logger.isTraceEnabled()) {
			logger.trace("Processing return value with " + handler);
		}
		handler.handleReturnValue(returnValue, returnType, message);
	}

}
