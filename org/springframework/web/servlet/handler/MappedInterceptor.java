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

package org.springframework.web.servlet.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.PathMatcher;
import org.springframework.web.context.request.WebRequestInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * Contains and delegates calls to a {@link HandlerInterceptor} along with
 * include (and optionally exclude) path patterns to which the interceptor should apply.
 * Also provides matching logic to test if the interceptor applies to a given request path.
 *
 * <p>A MappedInterceptor can be registered directly with any
 * {@link org.springframework.web.servlet.handler.AbstractHandlerMethodMapping
 * AbstractHandlerMethodMapping}. Furthermore, beans of type MappedInterceptor
 * are automatically detected by {@code AbstractHandlerMethodMapping} (including
 * ancestor ApplicationContext's) which effectively means the interceptor is
 * registered "globally" with all handler mappings.
 *
 * <p>
 * 包含和委派对{@link HandlerInterceptor}的调用以及拦截器应该应用的include(和可选排除)路径模式同时提供匹配逻辑来测试拦截器是否应用于给定的请求路径
 * 
 *  一个MappedInterceptor可以直接与任何{@link orgspringframeworkwebservlethandlerAbstractHandlerMethodMapping AbstractHandlerMethodMapping}
 * 一起注册。
 * 此外,MappedInterceptor类型的bean会被{@code AbstractHandlerMethodMapping}(包括祖先ApplicationContext)自动检测到,这有效地意味
 * 着拦截器在所有处理程序中被注册为"全局"映射。
 * 
 * 
 * @author Keith Donald
 * @author Rossen Stoyanchev
 * @author Brian Clozel
 * @since 3.0
 */
public final class MappedInterceptor implements HandlerInterceptor {

	private final String[] includePatterns;

	private final String[] excludePatterns;

	private final HandlerInterceptor interceptor;

	private PathMatcher pathMatcher;


	/**
	 * Create a new MappedInterceptor instance.
	 * <p>
	 *  创建一个新的MappedInterceptor实例
	 * 
	 * 
	 * @param includePatterns the path patterns to map with a {@code null} value matching to all paths
	 * @param interceptor the HandlerInterceptor instance to map to the given patterns
	 */
	public MappedInterceptor(String[] includePatterns, HandlerInterceptor interceptor) {
		this(includePatterns, null, interceptor);
	}

	/**
	 * Create a new MappedInterceptor instance.
	 * <p>
	 *  创建一个新的MappedInterceptor实例
	 * 
	 * 
	 * @param includePatterns the path patterns to map with a {@code null} value matching to all paths
	 * @param excludePatterns the path patterns to exclude
	 * @param interceptor the HandlerInterceptor instance to map to the given patterns
	 */
	public MappedInterceptor(String[] includePatterns, String[] excludePatterns, HandlerInterceptor interceptor) {
		this.includePatterns = includePatterns;
		this.excludePatterns = excludePatterns;
		this.interceptor = interceptor;
	}


	/**
	 * Create a new MappedInterceptor instance.
	 * <p>
	 *  创建一个新的MappedInterceptor实例
	 * 
	 * 
	 * @param includePatterns the path patterns to map with a {@code null} value matching to all paths
	 * @param interceptor the WebRequestInterceptor instance to map to the given patterns
	 */
	public MappedInterceptor(String[] includePatterns, WebRequestInterceptor interceptor) {
		this(includePatterns, null, interceptor);
	}

	/**
	 * Create a new MappedInterceptor instance.
	 * <p>
	 * 创建一个新的MappedInterceptor实例
	 * 
	 * 
	 * @param includePatterns the path patterns to map with a {@code null} value matching to all paths
	 * @param interceptor the WebRequestInterceptor instance to map to the given patterns
	 */
	public MappedInterceptor(String[] includePatterns, String[] excludePatterns, WebRequestInterceptor interceptor) {
		this(includePatterns, excludePatterns, new WebRequestHandlerInterceptorAdapter(interceptor));
	}


	/**
	 * Configure a PathMatcher to use with this MappedInterceptor instead of the
	 * one passed by default to the {@link #matches(String, org.springframework.util.PathMatcher)}
	 * method. This is an advanced property that is only required when using custom
	 * PathMatcher implementations that support mapping metadata other than the
	 * Ant-style path patterns supported by default.
	 *
	 * <p>
	 *  配置PathMatcher以与此MappedInterceptor配合使用,而不是默认传递给{@link #matches(String,orgspringframeworkutilPathMatcher)}
	 * 方法的PathHatcher方法这是一种高级属性,仅在使用自定义PathMatcher实现时才需要,该实现支持除默认支持的Ant样式路径模式。
	 * 
	 * 
	 * @param pathMatcher the path matcher to use
	 */
	public void setPathMatcher(PathMatcher pathMatcher) {
		this.pathMatcher = pathMatcher;
	}

	/**
	 * The configured PathMatcher, or {@code null}.
	 * <p>
	 *  配置的PathMatcher或{@code null}
	 * 
	 */
	public PathMatcher getPathMatcher() {
		return this.pathMatcher;
	}

	/**
	 * The path into the application the interceptor is mapped to.
	 * <p>
	 *  拦截器映射到应用程序的路径
	 * 
	 */
	public String[] getPathPatterns() {
		return this.includePatterns;
	}

	/**
	 * The actual Interceptor reference.
	 * <p>
	 *  实际的拦截器引用
	 * 
	 */
	public HandlerInterceptor getInterceptor() {
		return this.interceptor;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		return this.interceptor.preHandle(request, response, handler);
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		this.interceptor.postHandle(request, response, handler, modelAndView);
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		this.interceptor.afterCompletion(request, response, handler, ex);
	}

	/**
	 * Returns {@code true} if the interceptor applies to the given request path.
	 * <p>
	 *  如果拦截器适用于给定的请求路径,则返回{@code true}
	 * 
	 * @param lookupPath the current request path
	 * @param pathMatcher a path matcher for path pattern matching
	 */
	public boolean matches(String lookupPath, PathMatcher pathMatcher) {
		PathMatcher pathMatcherToUse = (this.pathMatcher != null) ? this.pathMatcher : pathMatcher;
		if (this.excludePatterns != null) {
			for (String pattern : this.excludePatterns) {
				if (pathMatcherToUse.match(pattern, lookupPath)) {
					return false;
				}
			}
		}
		if (this.includePatterns == null) {
			return true;
		}
		else {
			for (String pattern : this.includePatterns) {
				if (pathMatcherToUse.match(pattern, lookupPath)) {
					return true;
				}
			}
			return false;
		}
	}
}
