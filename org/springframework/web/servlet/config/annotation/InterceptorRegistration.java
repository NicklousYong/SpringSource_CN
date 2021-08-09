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

package org.springframework.web.servlet.config.annotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.MappedInterceptor;

/**
 * Assists with the creation of a {@link MappedInterceptor}.
 *
 * <p>
 *  协助创建{@link MappedInterceptor}
 * 
 * 
 * @author Rossen Stoyanchev
 * @author Keith Donald
 * @since 3.1
 */
public class InterceptorRegistration {

	private final HandlerInterceptor interceptor;

	private final List<String> includePatterns = new ArrayList<String>();

	private final List<String> excludePatterns = new ArrayList<String>();

	private PathMatcher pathMatcher;


	/**
	 * Creates an {@link InterceptorRegistration} instance.
	 * <p>
	 *  创建一个{@link InterceptorRegistration}实例
	 * 
	 */
	public InterceptorRegistration(HandlerInterceptor interceptor) {
		Assert.notNull(interceptor, "Interceptor is required");
		this.interceptor = interceptor;
	}

	/**
	 * Add URL patterns to which the registered interceptor should apply to.
	 * <p>
	 *  添加注册拦截器应适用的URL模式
	 * 
	 */
	public InterceptorRegistration addPathPatterns(String... patterns) {
		this.includePatterns.addAll(Arrays.asList(patterns));
		return this;
	}

	/**
	 * Add URL patterns to which the registered interceptor should not apply to.
	 * <p>
	 * 添加注册拦截器不应用于的URL模式
	 * 
	 */
	public InterceptorRegistration excludePathPatterns(String... patterns) {
		this.excludePatterns.addAll(Arrays.asList(patterns));
		return this;
	}

	/**
	 * A PathMatcher implementation to use with this interceptor. This is an optional,
	 * advanced property required only if using custom PathMatcher implementations
	 * that support mapping metadata other than the Ant path patterns supported
	 * by default.
	 * <p>
	 *  与此拦截器一起使用的PathMatcher实现只有在使用自定义的PathMatcher实现时才需要使用此高级属性,该实现支持除了默认支持的Ant路径模式之外的映射元数据
	 * 
	 */
	public InterceptorRegistration pathMatcher(PathMatcher pathMatcher) {
		this.pathMatcher = pathMatcher;
		return this;
	}

	/**
	 * Returns the underlying interceptor. If URL patterns are provided the returned type is
	 * {@link MappedInterceptor}; otherwise {@link HandlerInterceptor}.
	 * <p>
	 *  返回底层拦截器如果提供URL模式,则返回的类型为{@link MappedInterceptor};否则{@link HandlerInterceptor}
	 */
	protected Object getInterceptor() {
		if (this.includePatterns.isEmpty() && this.excludePatterns.isEmpty()) {
			return this.interceptor;
		}

		String[] include = toArray(this.includePatterns);
		String[] exclude = toArray(this.excludePatterns);
		MappedInterceptor mappedInterceptor = new MappedInterceptor(include, exclude, this.interceptor);

		if (this.pathMatcher != null) {
			mappedInterceptor.setPathMatcher(this.pathMatcher);
		}

		return mappedInterceptor;
	}

	private static String[] toArray(List<String> list) {
		return (CollectionUtils.isEmpty(list) ? null : list.toArray(new String[list.size()]));
	}

}
