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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.core.Ordered;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.context.request.WebRequestInterceptor;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsProcessor;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.DefaultCorsProcessor;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.UrlPathHelper;

/**
 * Abstract base class for {@link org.springframework.web.servlet.HandlerMapping}
 * implementations. Supports ordering, a default handler, handler interceptors,
 * including handler interceptors mapped by path patterns.
 *
 * <p>Note: This base class does <i>not</i> support exposure of the
 * {@link #PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE}. Support for this attribute
 * is up to concrete subclasses, typically based on request URL mappings.
 *
 * <p>
 *  {@link orgspringframeworkwebservletHandlerMapping}实现的抽象基类支持排序,默认处理程序,处理程序拦截器,包括由路径模式映射的处理程序拦截器
 * 
 * <p>注意：此基类不支持{@link #PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE}的支持,因为对此属性的支持取决于具体的子类,通常基于请求URL映射
 * 
 * 
 * @author Juergen Hoeller
 * @author Rossen Stoyanchev
 * @since 07.04.2003
 * @see #getHandlerInternal
 * @see #setDefaultHandler
 * @see #setAlwaysUseFullPath
 * @see #setUrlDecode
 * @see org.springframework.util.AntPathMatcher
 * @see #setInterceptors
 * @see org.springframework.web.servlet.HandlerInterceptor
 */
public abstract class AbstractHandlerMapping extends WebApplicationObjectSupport implements HandlerMapping, Ordered {

	private int order = Integer.MAX_VALUE;  // default: same as non-Ordered

	private Object defaultHandler;

	private UrlPathHelper urlPathHelper = new UrlPathHelper();

	private PathMatcher pathMatcher = new AntPathMatcher();

	private final List<Object> interceptors = new ArrayList<Object>();

	private final List<HandlerInterceptor> adaptedInterceptors = new ArrayList<HandlerInterceptor>();

	private CorsProcessor corsProcessor = new DefaultCorsProcessor();

	private final UrlBasedCorsConfigurationSource corsConfigSource = new UrlBasedCorsConfigurationSource();


	/**
	 * Specify the order value for this HandlerMapping bean.
	 * <p>Default value is {@code Integer.MAX_VALUE}, meaning that it's non-ordered.
	 * <p>
	 *  指定此HandlerMapping bean的订单值<p>默认值为{@code IntegerMAX_VALUE},这意味着它是无序的
	 * 
	 * 
	 * @see org.springframework.core.Ordered#getOrder()
	 */
	public final void setOrder(int order) {
	  this.order = order;
	}

	@Override
	public final int getOrder() {
	  return this.order;
	}

	/**
	 * Set the default handler for this handler mapping.
	 * This handler will be returned if no specific mapping was found.
	 * <p>Default is {@code null}, indicating no default handler.
	 * <p>
	 *  设置此处理程序映射的默认处理程序如果没有找到特定的映射,则返回此处理程序<p>默认值为{@code null},表示没有默认处理程序
	 * 
	 */
	public void setDefaultHandler(Object defaultHandler) {
		this.defaultHandler = defaultHandler;
	}

	/**
	 * Return the default handler for this handler mapping,
	 * or {@code null} if none.
	 * <p>
	 *  返回此处理程序映射的默认处理程序,否则返回{@code null}
	 * 
	 */
	public Object getDefaultHandler() {
		return this.defaultHandler;
	}

	/**
	 * Set if URL lookup should always use the full path within the current servlet
	 * context. Else, the path within the current servlet mapping is used if applicable
	 * (that is, in the case of a ".../*" servlet mapping in web.xml).
	 * <p>Default is "false".
	 * <p>
	 * 设置URL查找是否应始终使用当前servlet上下文中的完整路径Else,如果适用,则使用当前servlet映射中的路径(即,在webxml中为"/ *"servlet映射的情况)<p>默认值是"假"。
	 * 
	 * 
	 * @see org.springframework.web.util.UrlPathHelper#setAlwaysUseFullPath
	 */
	public void setAlwaysUseFullPath(boolean alwaysUseFullPath) {
		this.urlPathHelper.setAlwaysUseFullPath(alwaysUseFullPath);
		this.corsConfigSource.setAlwaysUseFullPath(alwaysUseFullPath);
	}

	/**
	 * Set if context path and request URI should be URL-decoded. Both are returned
	 * <i>undecoded</i> by the Servlet API, in contrast to the servlet path.
	 * <p>Uses either the request encoding or the default encoding according
	 * to the Servlet spec (ISO-8859-1).
	 * <p>
	 *  设置上下文路径和请求URI是否应进行URL解码Servlet API返回<i>未解码</i>与servlet路径相反<p>根据Servlet使用请求编码或默认编码规格(ISO-8859-1)
	 * 
	 * 
	 * @see org.springframework.web.util.UrlPathHelper#setUrlDecode
	 */
	public void setUrlDecode(boolean urlDecode) {
		this.urlPathHelper.setUrlDecode(urlDecode);
		this.corsConfigSource.setUrlDecode(urlDecode);
	}

	/**
	 * Set if ";" (semicolon) content should be stripped from the request URI.
	 * <p>The default value is {@code true}.
	 * <p>
	 *  设置如果";" (分号)内容应从请求URI中删除<p>默认值为{@code true}
	 * 
	 * 
	 * @see org.springframework.web.util.UrlPathHelper#setRemoveSemicolonContent(boolean)
	 */
	public void setRemoveSemicolonContent(boolean removeSemicolonContent) {
		this.urlPathHelper.setRemoveSemicolonContent(removeSemicolonContent);
		this.corsConfigSource.setRemoveSemicolonContent(removeSemicolonContent);
	}

	/**
	 * Set the UrlPathHelper to use for resolution of lookup paths.
	 * <p>Use this to override the default UrlPathHelper with a custom subclass,
	 * or to share common UrlPathHelper settings across multiple HandlerMappings
	 * and MethodNameResolvers.
	 * <p>
	 * 设置UrlPathHelper以用于解析查找路径<p>使用此方法可以使用自定义子类覆盖默认的UrlPathHelper,或者在多个HandlerMappings和MethodNameResolvers之
	 * 间共享通用的UrlPathHelper设置。
	 * 
	 */
	public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
		Assert.notNull(urlPathHelper, "UrlPathHelper must not be null");
		this.urlPathHelper = urlPathHelper;
		this.corsConfigSource.setUrlPathHelper(urlPathHelper);
	}

	/**
	 * Return the UrlPathHelper implementation to use for resolution of lookup paths.
	 * <p>
	 *  返回UrlPathHelper实现以用于查找路径的解析
	 * 
	 */
	public UrlPathHelper getUrlPathHelper() {
		return urlPathHelper;
	}

	/**
	 * Set the PathMatcher implementation to use for matching URL paths
	 * against registered URL patterns. Default is AntPathMatcher.
	 * <p>
	 *  设置PathMatcher实现用于匹配URL路径与注册的URL模式默认为AntPathMatcher
	 * 
	 * 
	 * @see org.springframework.util.AntPathMatcher
	 */
	public void setPathMatcher(PathMatcher pathMatcher) {
		Assert.notNull(pathMatcher, "PathMatcher must not be null");
		this.pathMatcher = pathMatcher;
		this.corsConfigSource.setPathMatcher(pathMatcher);
	}

	/**
	 * Return the PathMatcher implementation to use for matching URL paths
	 * against registered URL patterns.
	 * <p>
	 *  返回PathMatcher实现以用于匹配URL路径与注册的URL模式
	 * 
	 */
	public PathMatcher getPathMatcher() {
		return this.pathMatcher;
	}

	/**
	 * Set the interceptors to apply for all handlers mapped by this handler mapping.
	 * <p>Supported interceptor types are HandlerInterceptor, WebRequestInterceptor, and MappedInterceptor.
	 * Mapped interceptors apply only to request URLs that match its path patterns.
	 * Mapped interceptor beans are also detected by type during initialization.
	 * <p>
	 * 设置拦截器以应用此处理程序映射映射的所有处理程序<p>支持的拦截器类型为HandlerInterceptor,WebRequestInterceptor和MappedInterceptor映射拦截器仅适
	 * 用于与其路径模式匹配的请求URL映射的拦截器bean也在初始化期间通过类型检测。
	 * 
	 * 
	 * @param interceptors array of handler interceptors, or {@code null} if none
	 * @see #adaptInterceptor
	 * @see org.springframework.web.servlet.HandlerInterceptor
	 * @see org.springframework.web.context.request.WebRequestInterceptor
	 */
	public void setInterceptors(Object... interceptors) {
		this.interceptors.addAll(Arrays.asList(interceptors));
	}

	/**
	 * Configure a custom {@link CorsProcessor} to use to apply the matched
	 * {@link CorsConfiguration} for a request.
	 * <p>By default {@link DefaultCorsProcessor} is used.
	 * <p>
	 *  配置自定义{@link CorsProcessor}用于对请求应用匹配的{@link CorsConfiguration} <p>默认使用{@link DefaultCorsProcessor}
	 * 
	 * 
	 * @since 4.2
	 */
	public void setCorsProcessor(CorsProcessor corsProcessor) {
		Assert.notNull(corsProcessor, "CorsProcessor must not be null");
		this.corsProcessor = corsProcessor;
	}

	/**
	 * Return the configured {@link CorsProcessor}.
	 * <p>
	 *  返回配置的{@link CorsProcessor}
	 * 
	 */
	public CorsProcessor getCorsProcessor() {
		return this.corsProcessor;
	}

	/**
	 * Set "global" CORS configuration based on URL patterns. By default the first
	 * matching URL pattern is combined with the CORS configuration for the
	 * handler, if any.
	 * <p>
	 *  根据URL模式设置"全局"CORS配置默认情况下,第一个匹配的URL模式与处理程序的CORS配置相结合(如果有)
	 * 
	 * 
	 * @since 4.2
	 */
	public void setCorsConfigurations(Map<String, CorsConfiguration> corsConfigurations) {
		this.corsConfigSource.setCorsConfigurations(corsConfigurations);
	}

	/**
	 * Get the CORS configuration.
	 * <p>
	 *  获取CORS配置
	 * 
	 */
	public Map<String, CorsConfiguration> getCorsConfigurations() {
		return this.corsConfigSource.getCorsConfigurations();
	}


	/**
	 * Initializes the interceptors.
	 * <p>
	 *  初始化拦截器
	 * 
	 * 
	 * @see #extendInterceptors(java.util.List)
	 * @see #initInterceptors()
	 */
	@Override
	protected void initApplicationContext() throws BeansException {
		extendInterceptors(this.interceptors);
		detectMappedInterceptors(this.adaptedInterceptors);
		initInterceptors();
	}

	/**
	 * Extension hook that subclasses can override to register additional interceptors,
	 * given the configured interceptors (see {@link #setInterceptors}).
	 * <p>Will be invoked before {@link #initInterceptors()} adapts the specified
	 * interceptors into {@link HandlerInterceptor} instances.
	 * <p>The default implementation is empty.
	 * <p>
	 * 给定配置的拦截器(见{@link #setInterceptors})),子类可以覆盖的扩展钩子可以覆盖其他拦截器(参见{@link #setInterceptors})<p>将在{@link #initInterceptors())将调用指定的拦截器之前调用{@link HandlerInterceptor}
	 * 实例<p>默认实现为空。
	 * 
	 * 
	 * @param interceptors the configured interceptor List (never {@code null}), allowing
	 * to add further interceptors before as well as after the existing interceptors
	 */
	protected void extendInterceptors(List<Object> interceptors) {
	}

	/**
	 * Detect beans of type {@link MappedInterceptor} and add them to the list of mapped interceptors.
	 * <p>This is called in addition to any {@link MappedInterceptor}s that may have been provided
	 * via {@link #setInterceptors}, by default adding all beans of type {@link MappedInterceptor}
	 * from the current context and its ancestors. Subclasses can override and refine this policy.
	 * <p>
	 *  检测类型为{@link MappedInterceptor}的bean,并将它们添加到映射拦截器列表<p>除了可以通过{@link #setInterceptors}提供的任何{@link MappedInterceptor}
	 * 之外,默认添加来自当前上下文及其祖先子类的所有类型为{@link MappedInterceptor}的bean都可以覆盖并优化此策略。
	 * 
	 * 
	 * @param mappedInterceptors an empty list to add {@link MappedInterceptor} instances to
	 */
	protected void detectMappedInterceptors(List<HandlerInterceptor> mappedInterceptors) {
		mappedInterceptors.addAll(
				BeanFactoryUtils.beansOfTypeIncludingAncestors(
						getApplicationContext(), MappedInterceptor.class, true, false).values());
	}

	/**
	 * Initialize the specified interceptors, checking for {@link MappedInterceptor}s and
	 * adapting {@link HandlerInterceptor}s and {@link WebRequestInterceptor}s if necessary.
	 * <p>
	 * 初始化指定的拦截器,如果需要,检查{@link MappedInterceptor}并调整{@link HandlerInterceptor}和{@link WebRequestInterceptor}
	 * 。
	 * 
	 * 
	 * @see #setInterceptors
	 * @see #adaptInterceptor
	 */
	protected void initInterceptors() {
		if (!this.interceptors.isEmpty()) {
			for (int i = 0; i < this.interceptors.size(); i++) {
				Object interceptor = this.interceptors.get(i);
				if (interceptor == null) {
					throw new IllegalArgumentException("Entry number " + i + " in interceptors array is null");
				}
				this.adaptedInterceptors.add(adaptInterceptor(interceptor));
			}
		}
	}

	/**
	 * Adapt the given interceptor object to the {@link HandlerInterceptor} interface.
	 * <p>By default, the supported interceptor types are {@link HandlerInterceptor}
	 * and {@link WebRequestInterceptor}. Each given {@link WebRequestInterceptor}
	 * will be wrapped in a {@link WebRequestHandlerInterceptorAdapter}.
	 * Can be overridden in subclasses.
	 * <p>
	 *  将给定的拦截器对象适配到{@link HandlerInterceptor}接口<p>默认情况下,支持的拦截器类型为{@link HandlerInterceptor}和{@link WebRequestInterceptor}
	 * 每个给定的{@link WebRequestInterceptor}将被包装在{@link WebRequestHandlerInterceptorAdapter}可以在子类中被覆盖。
	 * 
	 * 
	 * @param interceptor the specified interceptor object
	 * @return the interceptor wrapped as HandlerInterceptor
	 * @see org.springframework.web.servlet.HandlerInterceptor
	 * @see org.springframework.web.context.request.WebRequestInterceptor
	 * @see WebRequestHandlerInterceptorAdapter
	 */
	protected HandlerInterceptor adaptInterceptor(Object interceptor) {
		if (interceptor instanceof HandlerInterceptor) {
			return (HandlerInterceptor) interceptor;
		}
		else if (interceptor instanceof WebRequestInterceptor) {
			return new WebRequestHandlerInterceptorAdapter((WebRequestInterceptor) interceptor);
		}
		else {
			throw new IllegalArgumentException("Interceptor type not supported: " + interceptor.getClass().getName());
		}
	}

	/**
	 * Return the adapted interceptors as {@link HandlerInterceptor} array.
	 * <p>
	 *  将适配的拦截器返回为{@link HandlerInterceptor}数组
	 * 
	 * 
	 * @return the array of {@link HandlerInterceptor}s, or {@code null} if none
	 */
	protected final HandlerInterceptor[] getAdaptedInterceptors() {
		int count = this.adaptedInterceptors.size();
		return (count > 0 ? this.adaptedInterceptors.toArray(new HandlerInterceptor[count]) : null);
	}

	/**
	 * Return all configured {@link MappedInterceptor}s as an array.
	 * <p>
	 *  将所有配置的{@link MappedInterceptor}作为数组返回
	 * 
	 * 
	 * @return the array of {@link MappedInterceptor}s, or {@code null} if none
	 */
	protected final MappedInterceptor[] getMappedInterceptors() {
		List<MappedInterceptor> mappedInterceptors = new ArrayList<MappedInterceptor>();
		for (HandlerInterceptor interceptor : this.adaptedInterceptors) {
			if (interceptor instanceof MappedInterceptor) {
				mappedInterceptors.add((MappedInterceptor) interceptor);
			}
		}
		int count = mappedInterceptors.size();
		return (count > 0 ? mappedInterceptors.toArray(new MappedInterceptor[count]) : null);
	}


	/**
	 * Look up a handler for the given request, falling back to the default
	 * handler if no specific one is found.
	 * <p>
	 *  查找给定请求的处理程序,如果没有找到特定请求,则返回到默认处理程序
	 * 
	 * 
	 * @param request current HTTP request
	 * @return the corresponding handler instance, or the default handler
	 * @see #getHandlerInternal
	 */
	@Override
	public final HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
		Object handler = getHandlerInternal(request);
		if (handler == null) {
			handler = getDefaultHandler();
		}
		if (handler == null) {
			return null;
		}
		// Bean name or resolved handler?
		if (handler instanceof String) {
			String handlerName = (String) handler;
			handler = getApplicationContext().getBean(handlerName);
		}

		HandlerExecutionChain executionChain = getHandlerExecutionChain(handler, request);
		if (CorsUtils.isCorsRequest(request)) {
			CorsConfiguration globalConfig = this.corsConfigSource.getCorsConfiguration(request);
			CorsConfiguration handlerConfig = getCorsConfiguration(handler, request);
			CorsConfiguration config = (globalConfig != null ? globalConfig.combine(handlerConfig) : handlerConfig);
			executionChain = getCorsHandlerExecutionChain(request, executionChain, config);
		}
		return executionChain;
	}

	/**
	 * Look up a handler for the given request, returning {@code null} if no
	 * specific one is found. This method is called by {@link #getHandler};
	 * a {@code null} return value will lead to the default handler, if one is set.
	 * <p>On CORS pre-flight requests this method should return a match not for
	 * the pre-flight request but for the expected actual request based on the URL
	 * path, the HTTP methods from the "Access-Control-Request-Method" header, and
	 * the headers from the "Access-Control-Request-Headers" header thus allowing
	 * the CORS configuration to be obtained via {@link #getCorsConfigurations},
	 * <p>Note: This method may also return a pre-built {@link HandlerExecutionChain},
	 * combining a handler object with dynamically determined interceptors.
	 * Statically specified interceptors will get merged into such an existing chain.
	 * <p>
	 * 查找给定请求的处理程序,如果没有找到特定的请求,返回{@code null}此方法由{@link #getHandler}调用; {@code null}返回值将导致默认处理程序,如果设置为<p>在CO
	 * RS预飞航请求之前,此方法应返回不适用于飞行前请求的匹配项,但对于预期的实际请求,基于URL路径,"Access-Control-Request-Method"头中的HTTP方法以及"Access-Co
	 * ntrol-Request-Headers"头中的头部,从而允许通过{@link #getCorsConfigurations}获取CORS配置,注意：此方法也可能返回一个预处理的{@link HandlerExecutionChain}
	 * ,将处理程序对象与动态确定的拦截器相结合静态指定的拦截器将被合并到这样一个现有的链中。
	 * 
	 * 
	 * @param request current HTTP request
	 * @return the corresponding handler instance, or {@code null} if none found
	 * @throws Exception if there is an internal error
	 */
	protected abstract Object getHandlerInternal(HttpServletRequest request) throws Exception;

	/**
	 * Build a {@link HandlerExecutionChain} for the given handler, including
	 * applicable interceptors.
	 * <p>The default implementation builds a standard {@link HandlerExecutionChain}
	 * with the given handler, the handler mapping's common interceptors, and any
	 * {@link MappedInterceptor}s matching to the current request URL. Interceptors
	 * are added in the order they were registered. Subclasses may override this
	 * in order to extend/rearrange the list of interceptors.
	 * <p><b>NOTE:</b> The passed-in handler object may be a raw handler or a
	 * pre-built {@link HandlerExecutionChain}. This method should handle those
	 * two cases explicitly, either building a new {@link HandlerExecutionChain}
	 * or extending the existing chain.
	 * <p>For simply adding an interceptor in a custom subclass, consider calling
	 * {@code super.getHandlerExecutionChain(handler, request)} and invoking
	 * {@link HandlerExecutionChain#addInterceptor} on the returned chain object.
	 * <p>
	 * 为给定的处理程序构建一个{@link HandlerExecutionChain},包括适用的拦截器。
	 * <p>默认实现使用给定的处理程序,处理程序映射的常见拦截器以及与{@link MappedInterceptor}匹配的标准{@link HandlerExecutionChain}当前请求URL拦截器
	 * 按照注册的顺序添加子类可以覆盖此值,以便扩展/重新排列拦截器列表<p> <b>注意：</b>传入的处理程序对象可能是一个原始处理程序或者一个预先构建的{@link HandlerExecutionChain}
	 * 这个方法应该明确地处理这两种情况,构建一个新的{@link HandlerExecutionChain}或扩展现有的链<p>为了简单地在自定义子类中添加一个拦截器,可以考虑调用{@代码超级getHandlerExecutionChain(handler,request)}
	 * 并在返回的链对象上调用{@link HandlerExecutionChain#addInterceptor}。
	 * 为给定的处理程序构建一个{@link HandlerExecutionChain},包括适用的拦截器。
	 * 
	 * 
	 * @param handler the resolved handler instance (never {@code null})
	 * @param request current HTTP request
	 * @return the HandlerExecutionChain (never {@code null})
	 * @see #getAdaptedInterceptors()
	 */
	protected HandlerExecutionChain getHandlerExecutionChain(Object handler, HttpServletRequest request) {
		HandlerExecutionChain chain = (handler instanceof HandlerExecutionChain ?
				(HandlerExecutionChain) handler : new HandlerExecutionChain(handler));

		String lookupPath = this.urlPathHelper.getLookupPathForRequest(request);
		for (HandlerInterceptor interceptor : this.adaptedInterceptors) {
			if (interceptor instanceof MappedInterceptor) {
				MappedInterceptor mappedInterceptor = (MappedInterceptor) interceptor;
				if (mappedInterceptor.matches(lookupPath, this.pathMatcher)) {
					chain.addInterceptor(mappedInterceptor.getInterceptor());
				}
			}
			else {
				chain.addInterceptor(interceptor);
			}
		}
		return chain;
	}

	/**
	 * Retrieve the CORS configuration for the given handler.
	 * <p>
	 * 
	 * @param handler the handler to check (never {@code null}).
	 * @param request the current request.
	 * @return the CORS configuration for the handler or {@code null}.
	 * @since 4.2
	 */
	protected CorsConfiguration getCorsConfiguration(Object handler, HttpServletRequest request) {
		if (handler instanceof HandlerExecutionChain) {
			handler = ((HandlerExecutionChain) handler).getHandler();
		}
		if (handler instanceof CorsConfigurationSource) {
			return ((CorsConfigurationSource) handler).getCorsConfiguration(request);
		}
		return null;
	}

	/**
	 * Update the HandlerExecutionChain for CORS-related handling.
	 * <p>For pre-flight requests, the default implementation replaces the selected
	 * handler with a simple HttpRequestHandler that invokes the configured
	 * {@link #setCorsProcessor}.
	 * <p>For actual requests, the default implementation inserts a
	 * HandlerInterceptor that makes CORS-related checks and adds CORS headers.
	 * <p>
	 * 检索给定处理程序的CORS配置
	 * 
	 * 
	 * @param request the current request
	 * @param chain the handler chain
	 * @param config the applicable CORS configuration, possibly {@code null}
	 * @since 4.2
	 */
	protected HandlerExecutionChain getCorsHandlerExecutionChain(HttpServletRequest request,
			HandlerExecutionChain chain, CorsConfiguration config) {

		if (CorsUtils.isPreFlightRequest(request)) {
			HandlerInterceptor[] interceptors = chain.getInterceptors();
			chain = new HandlerExecutionChain(new PreFlightHandler(config), interceptors);
		}
		else {
			chain.addInterceptor(new CorsInterceptor(config));
		}
		return chain;
	}


	private class PreFlightHandler implements HttpRequestHandler, CorsConfigurationSource {

		private final CorsConfiguration config;

		public PreFlightHandler(CorsConfiguration config) {
			this.config = config;
		}

		@Override
		public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
			corsProcessor.processRequest(this.config, request, response);
		}

		@Override
		public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
			return this.config;
		}
	}


	private class CorsInterceptor extends HandlerInterceptorAdapter implements CorsConfigurationSource {

		private final CorsConfiguration config;

		public CorsInterceptor(CorsConfiguration config) {
			this.config = config;
		}

		@Override
		public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
				throws Exception {

			return corsProcessor.processRequest(this.config, request, response);
		}

		@Override
		public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
			return this.config;
		}
	}

}
