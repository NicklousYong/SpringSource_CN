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

package org.springframework.web.method.support;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * Resolves method parameters by delegating to a list of registered {@link HandlerMethodArgumentResolver}s.
 * Previously resolved method parameters are cached for faster lookups.
 *
 * <p>
 *  通过委托到已注册的{@link HandlerMethodArgumentResolver}的列表来解析方法参数先前解析的方法参数被缓存以便更快的查找
 * 
 * 
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @since 3.1
 */
public class HandlerMethodArgumentResolverComposite implements HandlerMethodArgumentResolver {

	protected final Log logger = LogFactory.getLog(getClass());

	private final List<HandlerMethodArgumentResolver> argumentResolvers =
			new LinkedList<HandlerMethodArgumentResolver>();

	private final Map<MethodParameter, HandlerMethodArgumentResolver> argumentResolverCache =
			new ConcurrentHashMap<MethodParameter, HandlerMethodArgumentResolver>(256);


	/**
	 * Add the given {@link HandlerMethodArgumentResolver}.
	 * <p>
	 * 添加给定的{@link HandlerMethodArgumentResolver}
	 * 
	 */
	public HandlerMethodArgumentResolverComposite addResolver(HandlerMethodArgumentResolver resolver) {
		this.argumentResolvers.add(resolver);
		return this;
	}

	/**
	 * Add the given {@link HandlerMethodArgumentResolver}s.
	 * <p>
	 *  添加给定的{@link HandlerMethodArgumentResolver}
	 * 
	 * 
	 * @since 4.3
	 */
	public HandlerMethodArgumentResolverComposite addResolvers(HandlerMethodArgumentResolver... resolvers) {
		if (resolvers != null) {
			for (HandlerMethodArgumentResolver resolver : resolvers) {
				this.argumentResolvers.add(resolver);
			}
		}
		return this;
	}

	/**
	 * Add the given {@link HandlerMethodArgumentResolver}s.
	 * <p>
	 *  添加给定的{@link HandlerMethodArgumentResolver}
	 * 
	 */
	public HandlerMethodArgumentResolverComposite addResolvers(List<? extends HandlerMethodArgumentResolver> resolvers) {
		if (resolvers != null) {
			for (HandlerMethodArgumentResolver resolver : resolvers) {
				this.argumentResolvers.add(resolver);
			}
		}
		return this;
	}

	/**
	 * Return a read-only list with the contained resolvers, or an empty list.
	 * <p>
	 *  返回包含解析器的只读列表或空列表
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
	 * 
	 * @since 4.3
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
		return (getArgumentResolver(parameter) != null);
	}

	/**
	 * Iterate over registered {@link HandlerMethodArgumentResolver}s and invoke the one that supports it.
	 * <p>
	 *  迭代已注册的{@link HandlerMethodArgumentResolver},并调用支持它的
	 * 
	 * 
	 * @throws IllegalStateException if no suitable {@link HandlerMethodArgumentResolver} is found.
	 */
	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

		HandlerMethodArgumentResolver resolver = getArgumentResolver(parameter);
		if (resolver == null) {
			throw new IllegalArgumentException("Unknown parameter type [" + parameter.getParameterType().getName() + "]");
		}
		return resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
	}

	/**
	 * Find a registered {@link HandlerMethodArgumentResolver} that supports the given method parameter.
	 * <p>
	 *  找到一个支持给定方法参数的已注册的{@link HandlerMethodArgumentResolver}
	 */
	private HandlerMethodArgumentResolver getArgumentResolver(MethodParameter parameter) {
		HandlerMethodArgumentResolver result = this.argumentResolverCache.get(parameter);
		if (result == null) {
			for (HandlerMethodArgumentResolver methodArgumentResolver : this.argumentResolvers) {
				if (logger.isTraceEnabled()) {
					logger.trace("Testing if argument resolver [" + methodArgumentResolver + "] supports [" +
							parameter.getGenericParameterType() + "]");
				}
				if (methodArgumentResolver.supportsParameter(parameter)) {
					result = methodArgumentResolver;
					this.argumentResolverCache.put(parameter, result);
					break;
				}
			}
		}
		return result;
	}

}
