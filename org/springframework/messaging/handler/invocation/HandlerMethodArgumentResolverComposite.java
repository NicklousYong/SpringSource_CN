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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.util.Assert;

/**
 * Resolves method parameters by delegating to a list of registered
 * {@link HandlerMethodArgumentResolver}. Previously resolved method parameters are cached
 * for faster lookups.
 *
 * <p>
 *  通过委托到注册的{@link HandlerMethodArgumentResolver}的列表来解析方法参数先前解析的方法参数被缓存以便更快的查找
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public class HandlerMethodArgumentResolverComposite implements HandlerMethodArgumentResolver {

	private final List<HandlerMethodArgumentResolver> argumentResolvers = new LinkedList<HandlerMethodArgumentResolver>();

	private final Map<MethodParameter, HandlerMethodArgumentResolver> argumentResolverCache =
			new ConcurrentHashMap<MethodParameter, HandlerMethodArgumentResolver>(256);


	/**
	 * Return a read-only list with the contained resolvers, or an empty list.
	 * <p>
	 * 返回包含解析器的只读列表或空列表
	 * 
	 */
	public List<HandlerMethodArgumentResolver> getResolvers() {
		return Collections.unmodifiableList(this.argumentResolvers);
	}

	/**
	 * Clear the list of configured resolvers.
	 * <p>
	 *  清除配置的解析器列表
	 * 
	 */
	public void clear() {
		this.argumentResolvers.clear();
	}

	/**
	 * Whether the given {@linkplain MethodParameter method parameter} is supported by any registered
	 * {@link HandlerMethodArgumentResolver}.
	 * <p>
	 *  是否由任何已注册的{@link HandlerMethodArgumentResolver}支持给定的{@linkplain MethodParameter方法参数}
	 * 
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return getArgumentResolver(parameter) != null;
	}

	/**
	 * Iterate over registered {@link HandlerMethodArgumentResolver}s and invoke the one that supports it.
	 * <p>
	 *  迭代已注册的{@link HandlerMethodArgumentResolver},并调用支持它的
	 * 
	 * 
	 * @exception IllegalStateException if no suitable {@link HandlerMethodArgumentResolver} is found.
	 */
	@Override
	public Object resolveArgument(MethodParameter parameter, Message<?> message) throws Exception {

		HandlerMethodArgumentResolver resolver = getArgumentResolver(parameter);
		Assert.notNull(resolver, "Unknown parameter type [" + parameter.getParameterType().getName() + "]");
		return resolver.resolveArgument(parameter, message);
	}

	/**
	 * Find a registered {@link HandlerMethodArgumentResolver} that supports the given method parameter.
	 * <p>
	 *  找到一个支持给定方法参数的已注册的{@link HandlerMethodArgumentResolver}
	 * 
	 */
	private HandlerMethodArgumentResolver getArgumentResolver(MethodParameter parameter) {
		HandlerMethodArgumentResolver result = this.argumentResolverCache.get(parameter);
		if (result == null) {
			for (HandlerMethodArgumentResolver resolver : this.argumentResolvers) {
				if (resolver.supportsParameter(parameter)) {
					result = resolver;
					this.argumentResolverCache.put(parameter, result);
					break;
				}
			}
		}
		return result;
	}

	/**
	 * Add the given {@link HandlerMethodArgumentResolver}.
	 * <p>
	 *  添加给定的{@link HandlerMethodArgumentResolver}
	 * 
	 */
	public HandlerMethodArgumentResolverComposite addResolver(HandlerMethodArgumentResolver argumentResolver) {
		this.argumentResolvers.add(argumentResolver);
		return this;
	}

	/**
	 * Add the given {@link HandlerMethodArgumentResolver}s.
	 * <p>
	 *  添加给定的{@link HandlerMethodArgumentResolver}
	 */
	public HandlerMethodArgumentResolverComposite addResolvers(List<? extends HandlerMethodArgumentResolver> argumentResolvers) {
		if (argumentResolvers != null) {
			for (HandlerMethodArgumentResolver resolver : argumentResolvers) {
				this.argumentResolvers.add(resolver);
			}
		}
		return this;
	}

}
