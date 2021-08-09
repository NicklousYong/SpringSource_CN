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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeansException;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.HandlerExecutionChain;

/**
 * Abstract base class for URL-mapped {@link org.springframework.web.servlet.HandlerMapping}
 * implementations. Provides infrastructure for mapping handlers to URLs and configurable
 * URL lookup. For information on the latter, see "alwaysUseFullPath" property.
 *
 * <p>Supports direct matches, e.g. a registered "/test" matches "/test", and
 * various Ant-style pattern matches, e.g. a registered "/t*" pattern matches
 * both "/test" and "/team", "/test/*" matches all paths in the "/test" directory,
 * "/test/**" matches all paths below "/test". For details, see the
 * {@link org.springframework.util.AntPathMatcher AntPathMatcher} javadoc.
 *
 * <p>Will search all path patterns to find the most exact match for the
 * current request path. The most exact match is defined as the longest
 * path pattern that matches the current request path.
 *
 * <p>
 * URL映射的抽象基类{@link orgspringframeworkwebservletHandlerMapping}实现提供了将处理程序映射到URL和可配置URL查找的基础结构有关后者的信息,请参见
 * "alwaysUseFullPath"属性。
 * 
 *  <p>支持直接匹配,例如注册的"/ test"匹配"/ test"和各种Ant样式模式匹配,例如注册的"/ t *"模式匹配"/ test"和"/ team"," / test / *"匹配"/ te
 * st"目录中的所有路径,"/ test / **"匹配"/ test"下面的所有路径有关详细信息,请参阅{@link orgspringframeworkutilAntPathMatcher AntPathMatcher}
 *  javadoc。
 * 
 * <p>将搜索所有路径模式以找到当前请求路径的最精确匹配最精确的匹配被定义为与当前请求路径匹配的最长路径模式
 * 
 * 
 * @author Juergen Hoeller
 * @author Arjen Poutsma
 * @since 16.04.2003
 */
public abstract class AbstractUrlHandlerMapping extends AbstractHandlerMapping implements MatchableHandlerMapping {

	private Object rootHandler;

	private boolean useTrailingSlashMatch = false;

	private boolean lazyInitHandlers = false;

	private final Map<String, Object> handlerMap = new LinkedHashMap<String, Object>();


	/**
	 * Set the root handler for this handler mapping, that is,
	 * the handler to be registered for the root path ("/").
	 * <p>Default is {@code null}, indicating no root handler.
	 * <p>
	 *  设置此处理程序映射的根处理程序,即要为根路径注册的处理程序("/")<p>默认值为{@code null},表示没有根处理程序
	 * 
	 */
	public void setRootHandler(Object rootHandler) {
		this.rootHandler = rootHandler;
	}

	/**
	 * Return the root handler for this handler mapping (registered for "/"),
	 * or {@code null} if none.
	 * <p>
	 *  返回此处理程序映射的根处理程序(注册为"/"),否则返回{@code null}
	 * 
	 */
	public Object getRootHandler() {
		return this.rootHandler;
	}

	/**
	 * Whether to match to URLs irrespective of the presence of a trailing slash.
	 * If enabled a URL pattern such as "/users" also matches to "/users/".
	 * <p>The default value is {@code false}.
	 * <p>
	 *  是否匹配URL,无论是否存在尾部斜杠如果启用,"/ users"等URL模式也与"/ users /"匹配<p>默认值为{@code false}
	 * 
	 */
	public void setUseTrailingSlashMatch(boolean useTrailingSlashMatch) {
		this.useTrailingSlashMatch = useTrailingSlashMatch;
	}

	/**
	 * Whether to match to URLs irrespective of the presence of a trailing slash.
	 * <p>
	 *  是否匹配URL,而不管尾部斜线是否存在
	 * 
	 */
	public boolean useTrailingSlashMatch() {
		return this.useTrailingSlashMatch;
	}

	/**
	 * Set whether to lazily initialize handlers. Only applicable to
	 * singleton handlers, as prototypes are always lazily initialized.
	 * Default is "false", as eager initialization allows for more efficiency
	 * through referencing the controller objects directly.
	 * <p>If you want to allow your controllers to be lazily initialized,
	 * make them "lazy-init" and set this flag to true. Just making them
	 * "lazy-init" will not work, as they are initialized through the
	 * references from the handler mapping in this case.
	 * <p>
	 * 设置是否懒惰地初始化处理程序只适用于单例处理程序,因为原型总是延迟初始化默认为"false",因为初始化允许通过直接引用控制器对象来提高效率<p>如果要允许控制器懒惰初始化,使它们"lazy-init"
	 * 并将此标志设置为true只要使它们"lazy-init"不起作用,因为在这种情况下通过处理程序映射的引用来初始化它们。
	 * 
	 */
	public void setLazyInitHandlers(boolean lazyInitHandlers) {
		this.lazyInitHandlers = lazyInitHandlers;
	}

	/**
	 * Look up a handler for the URL path of the given request.
	 * <p>
	 *  查找给定请求的URL路径的处理程序
	 * 
	 * 
	 * @param request current HTTP request
	 * @return the handler instance, or {@code null} if none found
	 */
	@Override
	protected Object getHandlerInternal(HttpServletRequest request) throws Exception {
		String lookupPath = getUrlPathHelper().getLookupPathForRequest(request);
		Object handler = lookupHandler(lookupPath, request);
		if (handler == null) {
			// We need to care for the default handler directly, since we need to
			// expose the PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE for it as well.
			Object rawHandler = null;
			if ("/".equals(lookupPath)) {
				rawHandler = getRootHandler();
			}
			if (rawHandler == null) {
				rawHandler = getDefaultHandler();
			}
			if (rawHandler != null) {
				// Bean name or resolved handler?
				if (rawHandler instanceof String) {
					String handlerName = (String) rawHandler;
					rawHandler = getApplicationContext().getBean(handlerName);
				}
				validateHandler(rawHandler, request);
				handler = buildPathExposingHandler(rawHandler, lookupPath, lookupPath, null);
			}
		}
		if (handler != null && logger.isDebugEnabled()) {
			logger.debug("Mapping [" + lookupPath + "] to " + handler);
		}
		else if (handler == null && logger.isTraceEnabled()) {
			logger.trace("No handler mapping found for [" + lookupPath + "]");
		}
		return handler;
	}

	/**
	 * Look up a handler instance for the given URL path.
	 * <p>Supports direct matches, e.g. a registered "/test" matches "/test",
	 * and various Ant-style pattern matches, e.g. a registered "/t*" matches
	 * both "/test" and "/team". For details, see the AntPathMatcher class.
	 * <p>Looks for the most exact pattern, where most exact is defined as
	 * the longest path pattern.
	 * <p>
	 * 查找给定URL路径的处理程序实例<p>支持直接匹配,例如注册的"/ test"匹配"/ test",以及各种Ant样式模式匹配,例如注册的"/ t *"匹配"/测试"和"/ team"有关详细信息,请参
	 * 阅AntPathMatcher类<p>查找最精确的模式,其中最精确的定义为最长的路径模式。
	 * 
	 * 
	 * @param urlPath URL the bean is mapped to
	 * @param request current HTTP request (to expose the path within the mapping to)
	 * @return the associated handler instance, or {@code null} if not found
	 * @see #exposePathWithinMapping
	 * @see org.springframework.util.AntPathMatcher
	 */
	protected Object lookupHandler(String urlPath, HttpServletRequest request) throws Exception {
		// Direct match?
		Object handler = this.handlerMap.get(urlPath);
		if (handler != null) {
			// Bean name or resolved handler?
			if (handler instanceof String) {
				String handlerName = (String) handler;
				handler = getApplicationContext().getBean(handlerName);
			}
			validateHandler(handler, request);
			return buildPathExposingHandler(handler, urlPath, urlPath, null);
		}
		// Pattern match?
		List<String> matchingPatterns = new ArrayList<String>();
		for (String registeredPattern : this.handlerMap.keySet()) {
			if (getPathMatcher().match(registeredPattern, urlPath)) {
				matchingPatterns.add(registeredPattern);
			}
			else if (useTrailingSlashMatch()) {
				if (!registeredPattern.endsWith("/") && getPathMatcher().match(registeredPattern + "/", urlPath)) {
					matchingPatterns.add(registeredPattern +"/");
				}
			}
		}
		String bestPatternMatch = null;
		Comparator<String> patternComparator = getPathMatcher().getPatternComparator(urlPath);
		if (!matchingPatterns.isEmpty()) {
			Collections.sort(matchingPatterns, patternComparator);
			if (logger.isDebugEnabled()) {
				logger.debug("Matching patterns for request [" + urlPath + "] are " + matchingPatterns);
			}
			bestPatternMatch = matchingPatterns.get(0);
		}
		if (bestPatternMatch != null) {
			handler = this.handlerMap.get(bestPatternMatch);
			if (handler == null) {
				Assert.isTrue(bestPatternMatch.endsWith("/"));
				handler = this.handlerMap.get(bestPatternMatch.substring(0, bestPatternMatch.length() - 1));
			}
			// Bean name or resolved handler?
			if (handler instanceof String) {
				String handlerName = (String) handler;
				handler = getApplicationContext().getBean(handlerName);
			}
			validateHandler(handler, request);
			String pathWithinMapping = getPathMatcher().extractPathWithinPattern(bestPatternMatch, urlPath);

			// There might be multiple 'best patterns', let's make sure we have the correct URI template variables
			// for all of them
			Map<String, String> uriTemplateVariables = new LinkedHashMap<String, String>();
			for (String matchingPattern : matchingPatterns) {
				if (patternComparator.compare(bestPatternMatch, matchingPattern) == 0) {
					Map<String, String> vars = getPathMatcher().extractUriTemplateVariables(matchingPattern, urlPath);
					Map<String, String> decodedVars = getUrlPathHelper().decodePathVariables(request, vars);
					uriTemplateVariables.putAll(decodedVars);
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("URI Template variables for request [" + urlPath + "] are " + uriTemplateVariables);
			}
			return buildPathExposingHandler(handler, bestPatternMatch, pathWithinMapping, uriTemplateVariables);
		}
		// No handler found...
		return null;
	}

	/**
	 * Validate the given handler against the current request.
	 * <p>The default implementation is empty. Can be overridden in subclasses,
	 * for example to enforce specific preconditions expressed in URL mappings.
	 * <p>
	 *  根据当前请求验证给定的处理程序<p>默认实现为空可以在子类中覆盖,例如以强制以URL映射表示的特定前提条件
	 * 
	 * 
	 * @param handler the handler object to validate
	 * @param request current HTTP request
	 * @throws Exception if validation failed
	 */
	protected void validateHandler(Object handler, HttpServletRequest request) throws Exception {
	}

	/**
	 * Build a handler object for the given raw handler, exposing the actual
	 * handler, the {@link #PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE}, as well as
	 * the {@link #URI_TEMPLATE_VARIABLES_ATTRIBUTE} before executing the handler.
	 * <p>The default implementation builds a {@link HandlerExecutionChain}
	 * with a special interceptor that exposes the path attribute and uri template variables
	 * <p>
	 * 在执行处理程序之前,为给定的原始处理程序构建一个处理程序对象,揭示实际处理程序,{@link #PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE}以及{@link #URI_TEMPLATE_VARIABLES_ATTRIBUTE}
	 * 默认实现构建一个{@link HandlerExecutionChain}具有暴露路径属性和uri模板变量的特殊拦截器。
	 * 
	 * 
	 * @param rawHandler the raw handler to expose
	 * @param pathWithinMapping the path to expose before executing the handler
	 * @param uriTemplateVariables the URI template variables, can be {@code null} if no variables found
	 * @return the final handler object
	 */
	protected Object buildPathExposingHandler(Object rawHandler, String bestMatchingPattern,
			String pathWithinMapping, Map<String, String> uriTemplateVariables) {

		HandlerExecutionChain chain = new HandlerExecutionChain(rawHandler);
		chain.addInterceptor(new PathExposingHandlerInterceptor(bestMatchingPattern, pathWithinMapping));
		if (!CollectionUtils.isEmpty(uriTemplateVariables)) {
			chain.addInterceptor(new UriTemplateVariablesHandlerInterceptor(uriTemplateVariables));
		}
		return chain;
	}

	/**
	 * Expose the path within the current mapping as request attribute.
	 * <p>
	 *  将当前映射中的路径显示为请求属性
	 * 
	 * 
	 * @param pathWithinMapping the path within the current mapping
	 * @param request the request to expose the path to
	 * @see #PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE
	 */
	protected void exposePathWithinMapping(String bestMatchingPattern, String pathWithinMapping, HttpServletRequest request) {
		request.setAttribute(BEST_MATCHING_PATTERN_ATTRIBUTE, bestMatchingPattern);
		request.setAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, pathWithinMapping);
	}

	/**
	 * Expose the URI templates variables as request attribute.
	 * <p>
	 *  将URI模板变量显示为请求属性
	 * 
	 * 
	 * @param uriTemplateVariables the URI template variables
	 * @param request the request to expose the path to
	 * @see #PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE
	 */
	protected void exposeUriTemplateVariables(Map<String, String> uriTemplateVariables, HttpServletRequest request) {
		request.setAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE, uriTemplateVariables);
	}

	@Override
	public RequestMatchResult match(HttpServletRequest request, String pattern) {
		String lookupPath = getUrlPathHelper().getLookupPathForRequest(request);
		if (getPathMatcher().match(pattern, lookupPath)) {
			return new RequestMatchResult(pattern, lookupPath, getPathMatcher());
		}
		else if (useTrailingSlashMatch()) {
			if (!pattern.endsWith("/") && getPathMatcher().match(pattern + "/", lookupPath)) {
				return new RequestMatchResult(pattern + "/", lookupPath, getPathMatcher());
			}
		}
		return null;
	}

	/**
	 * Register the specified handler for the given URL paths.
	 * <p>
	 *  为指定的URL路径注册指定的处理程序
	 * 
	 * 
	 * @param urlPaths the URLs that the bean should be mapped to
	 * @param beanName the name of the handler bean
	 * @throws BeansException if the handler couldn't be registered
	 * @throws IllegalStateException if there is a conflicting handler registered
	 */
	protected void registerHandler(String[] urlPaths, String beanName) throws BeansException, IllegalStateException {
		Assert.notNull(urlPaths, "URL path array must not be null");
		for (String urlPath : urlPaths) {
			registerHandler(urlPath, beanName);
		}
	}

	/**
	 * Register the specified handler for the given URL path.
	 * <p>
	 *  注册给定URL路径的指定处理程序
	 * 
	 * 
	 * @param urlPath the URL the bean should be mapped to
	 * @param handler the handler instance or handler bean name String
	 * (a bean name will automatically be resolved into the corresponding handler bean)
	 * @throws BeansException if the handler couldn't be registered
	 * @throws IllegalStateException if there is a conflicting handler registered
	 */
	protected void registerHandler(String urlPath, Object handler) throws BeansException, IllegalStateException {
		Assert.notNull(urlPath, "URL path must not be null");
		Assert.notNull(handler, "Handler object must not be null");
		Object resolvedHandler = handler;

		// Eagerly resolve handler if referencing singleton via name.
		if (!this.lazyInitHandlers && handler instanceof String) {
			String handlerName = (String) handler;
			if (getApplicationContext().isSingleton(handlerName)) {
				resolvedHandler = getApplicationContext().getBean(handlerName);
			}
		}

		Object mappedHandler = this.handlerMap.get(urlPath);
		if (mappedHandler != null) {
			if (mappedHandler != resolvedHandler) {
				throw new IllegalStateException(
						"Cannot map " + getHandlerDescription(handler) + " to URL path [" + urlPath +
						"]: There is already " + getHandlerDescription(mappedHandler) + " mapped.");
			}
		}
		else {
			if (urlPath.equals("/")) {
				if (logger.isInfoEnabled()) {
					logger.info("Root mapping to " + getHandlerDescription(handler));
				}
				setRootHandler(resolvedHandler);
			}
			else if (urlPath.equals("/*")) {
				if (logger.isInfoEnabled()) {
					logger.info("Default mapping to " + getHandlerDescription(handler));
				}
				setDefaultHandler(resolvedHandler);
			}
			else {
				this.handlerMap.put(urlPath, resolvedHandler);
				if (logger.isInfoEnabled()) {
					logger.info("Mapped URL path [" + urlPath + "] onto " + getHandlerDescription(handler));
				}
			}
		}
	}

	private String getHandlerDescription(Object handler) {
		return "handler " + (handler instanceof String ? "'" + handler + "'" : "of type [" + handler.getClass() + "]");
	}


	/**
	 * Return the registered handlers as an unmodifiable Map, with the registered path
	 * as key and the handler object (or handler bean name in case of a lazy-init handler)
	 * as value.
	 * <p>
	 * if(loggerisInfoEnabled()){loggerinfo("默认映射到"+ getHandlerDescription(handler)); } setDefaultHandler(re
	 * solvedHandler); } else {thishandlerMapput(urlPath,resolvedHandler); if(loggerisInfoEnabled()){loggerinfo("映射URL路径["+ urlPath +"]到"+ getHandlerDescription(handler)); }
	 * }}}。
	 * 
	 *  private String getHandlerDescription(Object handler){return"handler"+(handler instanceof String?"'"+ handler +"'"："of type ["+ handlergetClass()+"]"); }
	 * 。
	 * 
	 *  / **将注册的处理程序作为不可修改的Map返回,注册的路径为key,处理程序对象(或者在lazy-init处理程序的情况下为处理程序bean名称)为值
	 * 
	 * 
	 * @see #getDefaultHandler()
	 */
	public final Map<String, Object> getHandlerMap() {
		return Collections.unmodifiableMap(this.handlerMap);
	}

	/**
	 * Indicates whether this handler mapping support type-level mappings. Default to {@code false}.
	 * <p>
	 */
	protected boolean supportsTypeLevelMappings() {
		return false;
	}


	/**
	 * Special interceptor for exposing the
	 * {@link AbstractUrlHandlerMapping#PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE} attribute.
	 * <p>
	 *  指示此处理程序映射是否支持类型级映射默认为{@code false}
	 * 
	 * 
	 * @see AbstractUrlHandlerMapping#exposePathWithinMapping
	 */
	private class PathExposingHandlerInterceptor extends HandlerInterceptorAdapter {

		private final String bestMatchingPattern;

		private final String pathWithinMapping;

		public PathExposingHandlerInterceptor(String bestMatchingPattern, String pathWithinMapping) {
			this.bestMatchingPattern = bestMatchingPattern;
			this.pathWithinMapping = pathWithinMapping;
		}

		@Override
		public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
			exposePathWithinMapping(this.bestMatchingPattern, this.pathWithinMapping, request);
			request.setAttribute(INTROSPECT_TYPE_LEVEL_MAPPING, supportsTypeLevelMappings());
			return true;
		}

	}

	/**
	 * Special interceptor for exposing the
	 * {@link AbstractUrlHandlerMapping#URI_TEMPLATE_VARIABLES_ATTRIBUTE} attribute.
	 * <p>
	 * 用于公开{@link AbstractUrlHandlerMapping#PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE}属性的特殊拦截器
	 * 
	 * 
	 * @see AbstractUrlHandlerMapping#exposePathWithinMapping
	 */
	private class UriTemplateVariablesHandlerInterceptor extends HandlerInterceptorAdapter {

		private final Map<String, String> uriTemplateVariables;

		public UriTemplateVariablesHandlerInterceptor(Map<String, String> uriTemplateVariables) {
			this.uriTemplateVariables = uriTemplateVariables;
		}

		@Override
		public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
			exposeUriTemplateVariables(this.uriTemplateVariables, request);
			return true;
		}
	}

}
