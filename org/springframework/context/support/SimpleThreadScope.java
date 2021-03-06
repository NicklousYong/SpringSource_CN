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

package org.springframework.context.support;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.core.NamedThreadLocal;

/**
 * A simple thread-backed {@link Scope} implementation.
 *
 * <p><b>NOTE:</b> This thread scope is not registered by default in common contexts.
 * Instead, you need to explicitly assign it to a scope key in your setup, either through
 * {@link org.springframework.beans.factory.config.ConfigurableBeanFactory#registerScope}
 * or through a {@link org.springframework.beans.factory.config.CustomScopeConfigurer} bean.
 *
 * <p>{@code SimpleThreadScope} <em>does not clean up any objects</em> associated with it.
 * As such, it is typically preferable to use
 * {@link org.springframework.web.context.request.RequestScope RequestScope}
 * in web environments.
 *
 * <p>For an implementation of a thread-based {@code Scope} with support for
 * destruction callbacks, refer to the
 * <a href="http://www.springbyexample.org/examples/custom-thread-scope-module.html">
*  Spring by Example Custom Thread Scope Module</a>.
 *
 * <p>Thanks to Eugene Kuleshov for submitting the original prototype for a thread scope!
 *
 * <p>
 *  一个简单的线程支持{@link Scope}实现
 * 
 * <p> <b>注意：</b>这个线程作用域在常见的上下文中未被默认注册,而需要通过{@link orgspringframeworkbeansfactoryconfigConfigurableBeanFactory#registerScope}
 * 或通过一个{@link orgspringframeworkbeansfactoryconfigCustomScopeConfigurer} bean。
 * 
 *  <p> {@ code SimpleThreadScope} <em>不会清除与之相关联的任何对象。
 * 因此,通常最好在Web环境中使用{@link orgspringframeworkwebcontextrequestRequestScope RequestScope}。
 * 
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @since 3.0
 * @see org.springframework.web.context.request.RequestScope
 */
public class SimpleThreadScope implements Scope {

	private static final Log logger = LogFactory.getLog(SimpleThreadScope.class);

	private final ThreadLocal<Map<String, Object>> threadScope =
			new NamedThreadLocal<Map<String, Object>>("SimpleThreadScope") {
				@Override
				protected Map<String, Object> initialValue() {
					return new HashMap<String, Object>();
				}
			};


	@Override
	public Object get(String name, ObjectFactory<?> objectFactory) {
		Map<String, Object> scope = this.threadScope.get();
		Object object = scope.get(name);
		if (object == null) {
			object = objectFactory.getObject();
			scope.put(name, object);
		}
		return object;
	}

	@Override
	public Object remove(String name) {
		Map<String, Object> scope = this.threadScope.get();
		return scope.remove(name);
	}

	@Override
	public void registerDestructionCallback(String name, Runnable callback) {
		logger.warn("SimpleThreadScope does not support destruction callbacks. " +
				"Consider using RequestScope in a web environment.");
	}

	@Override
	public Object resolveContextualObject(String key) {
		return null;
	}

	@Override
	public String getConversationId() {
		return Thread.currentThread().getName();
	}

}
