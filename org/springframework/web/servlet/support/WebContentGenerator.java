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

package org.springframework.web.servlet.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.context.support.WebApplicationObjectSupport;

/**
 * Convenient superclass for any kind of web content generator,
 * like {@link org.springframework.web.servlet.mvc.AbstractController}
 * and {@link org.springframework.web.servlet.mvc.WebContentInterceptor}.
 * Can also be used for custom handlers that have their own
 * {@link org.springframework.web.servlet.HandlerAdapter}.
 *
 * <p>Supports HTTP cache control options. The usage of corresponding HTTP
 * headers can be controlled via the {@link #setCacheSeconds "cacheSeconds"}
 * and {@link #setCacheControl "cacheControl"} properties.
 *
 * <p><b>NOTE:</b> As of Spring 4.2, this generator's default behavior changed when
 * using only {@link #setCacheSeconds}, sending HTTP response headers that are in line
 * with current browsers and proxies implementations (i.e. no HTTP 1.0 headers anymore)
 * Reverting to the previous behavior can be easily done by using one of the newly
 * deprecated methods {@link #setUseExpiresHeader}, {@link #setUseCacheControlHeader},
 * {@link #setUseCacheControlNoStore} or {@link #setAlwaysMustRevalidate}.
 *
 * <p>
 * 任何类型的Web内容生成器的方便的超类,如{@link orgspringframeworkwebservletmvcAbstractController}和{@link orgspringframeworkwebservletmvcWebContentInterceptor}
 * 也可以用于具有自己的{@link orgspringframeworkwebservletHandlerAdapter}的自定义处理程序。
 * 
 *  <p>支持HTTP缓存控制选项可以通过{@link #setCacheSeconds"cacheSeconds"}和{@link #setCacheControl"cacheControl"}属性控制
 * 对应的HTTP头的使用。
 * 
 * <p> <b>注意：</b>从Spring 42开始,仅使用{@link #setCacheSeconds}时,此发生器的默认行为发生变化,发送符合当前浏览器和代理实现的HTTP响应头(即没有HTTP再
 * 次使用以前的行为,可以通过使用新弃用的方法{@link #setUseExpiresHeader},{@link #setUseCacheControlHeader},{@link #setUseCacheControlNoStore}
 * 或{@link #setAlwaysMustRevalidate}轻松完成。
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Brian Clozel
 * @author Rossen Stoyanchev
 * @see #setCacheSeconds
 * @see #setCacheControl
 * @see #setRequireSession
 */
public abstract class WebContentGenerator extends WebApplicationObjectSupport {

	/** HTTP method "GET" */
	public static final String METHOD_GET = "GET";

	/** HTTP method "HEAD" */
	public static final String METHOD_HEAD = "HEAD";

	/** HTTP method "POST" */
	public static final String METHOD_POST = "POST";

	private static final String HEADER_PRAGMA = "Pragma";

	private static final String HEADER_EXPIRES = "Expires";

	protected static final String HEADER_CACHE_CONTROL = "Cache-Control";

	/** Checking for Servlet 3.0+ HttpServletResponse.getHeaders(String) */
	private static final boolean servlet3Present =
			ClassUtils.hasMethod(HttpServletResponse.class, "getHeaders", String.class);


	/** Set of supported HTTP methods */
	private Set<String> supportedMethods;

	private String allowHeader;

	private boolean requireSession = false;

	private CacheControl cacheControl;

	private int cacheSeconds = -1;

	private String[] varyByRequestHeaders;


	// deprecated fields

	/** Use HTTP 1.0 expires header? */
	private boolean useExpiresHeader = false;

	/** Use HTTP 1.1 cache-control header? */
	private boolean useCacheControlHeader = true;

	/** Use HTTP 1.1 cache-control header value "no-store"? */
	private boolean useCacheControlNoStore = true;

	private boolean alwaysMustRevalidate = false;


	/**
	 * Create a new WebContentGenerator which supports
	 * HTTP methods GET, HEAD and POST by default.
	 * <p>
	 *  创建一个新的WebContentGenerator,它默认支持HTTP方法GET,HEAD和POST
	 * 
	 */
	public WebContentGenerator() {
		this(true);
	}

	/**
	 * Create a new WebContentGenerator.
	 * <p>
	 *  创建新的WebContentGenerator
	 * 
	 * 
	 * @param restrictDefaultSupportedMethods {@code true} if this
	 * generator should support HTTP methods GET, HEAD and POST by default,
	 * or {@code false} if it should be unrestricted
	 */
	public WebContentGenerator(boolean restrictDefaultSupportedMethods) {
		if (restrictDefaultSupportedMethods) {
			this.supportedMethods = new LinkedHashSet<String>(4);
			this.supportedMethods.add(METHOD_GET);
			this.supportedMethods.add(METHOD_HEAD);
			this.supportedMethods.add(METHOD_POST);
		}
		initAllowHeader();
	}

	/**
	 * Create a new WebContentGenerator.
	 * <p>
	 *  创建一个新的WebContentGenerator
	 * 
	 * 
	 * @param supportedMethods the supported HTTP methods for this content generator
	 */
	public WebContentGenerator(String... supportedMethods) {
		setSupportedMethods(supportedMethods);
	}


	/**
	 * Set the HTTP methods that this content generator should support.
	 * <p>Default is GET, HEAD and POST for simple form controller types;
	 * unrestricted for general controllers and interceptors.
	 * <p>
	 * 设置此内容生成器应支持的HTTP方法<p>默认为GET,HEAD和POST,用于简单的表单控制器类型;一般控制器和拦截器不受限制
	 * 
	 */
	public final void setSupportedMethods(String... methods) {
		if (!ObjectUtils.isEmpty(methods)) {
			this.supportedMethods = new LinkedHashSet<String>(Arrays.asList(methods));
		}
		else {
			this.supportedMethods = null;
		}
		initAllowHeader();
	}

	/**
	 * Return the HTTP methods that this content generator supports.
	 * <p>
	 *  返回此内容生成器支持的HTTP方法
	 * 
	 */
	public final String[] getSupportedMethods() {
		return StringUtils.toStringArray(this.supportedMethods);
	}

	private void initAllowHeader() {
		Collection<String> allowedMethods;
		if (this.supportedMethods == null) {
			allowedMethods = new ArrayList<String>(HttpMethod.values().length - 1);
			for (HttpMethod method : HttpMethod.values()) {
				if (!HttpMethod.TRACE.equals(method)) {
					allowedMethods.add(method.name());
				}
			}
		}
		else if (this.supportedMethods.contains(HttpMethod.OPTIONS.name())) {
			allowedMethods = this.supportedMethods;
		}
		else {
			allowedMethods = new ArrayList<String>(this.supportedMethods);
			allowedMethods.add(HttpMethod.OPTIONS.name());

		}
		this.allowHeader = StringUtils.collectionToCommaDelimitedString(allowedMethods);
	}

	/**
	 * Return the "Allow" header value to use in response to an HTTP OPTIONS
	 * request based on the configured {@link #setSupportedMethods supported
	 * methods} also automatically adding "OPTIONS" to the list even if not
	 * present as a supported method. This means sub-classes don't have to
	 * explicitly list "OPTIONS" as a supported method as long as HTTP OPTIONS
	 * requests are handled before making a call to
	 * {@link #checkRequest(HttpServletRequest)}.
	 * <p>
	 *  返回"允许"标头值以响应基于配置的{@link #setSupportedMethods支持的方法的HTTP OPTIONS请求而使用}也自动将"OPTIONS"添加到列表,即使不存在作为支持的方法这
	 * 意味着子类只要在调用{@link #checkRequest(HttpServletRequest)}之前处理HTTP OPTIONS请求时,不必将"OPTIONS"显式列为支持的方法。
	 * 
	 */
	protected String getAllowHeader() {
		return this.allowHeader;
	}

	/**
	 * Set whether a session should be required to handle requests.
	 * <p>
	 *  设置是否需要会话来处理请求
	 * 
	 */
	public final void setRequireSession(boolean requireSession) {
		this.requireSession = requireSession;
	}

	/**
	 * Return whether a session is required to handle requests.
	 * <p>
	 * 返回是否需要会话来处理请求
	 * 
	 */
	public final boolean isRequireSession() {
		return this.requireSession;
	}

	/**
	 * Set the {@link org.springframework.http.CacheControl} instance to build
	 * the Cache-Control HTTP response header.
	 * <p>
	 *  设置{@link orgspringframeworkhttpCacheControl}实例来构建Cache-Control HTTP响应头
	 * 
	 * 
	 * @since 4.2
	 */
	public final void setCacheControl(CacheControl cacheControl) {
		this.cacheControl = cacheControl;
	}

	/**
	 * Get the {@link org.springframework.http.CacheControl} instance
	 * that builds the Cache-Control HTTP response header.
	 * <p>
	 *  获取构建Cache-Control HTTP响应头的{@link orgspringframeworkhttpCacheControl}实例
	 * 
	 * 
	 * @since 4.2
	 */
	public final CacheControl getCacheControl() {
		return this.cacheControl;
	}

	/**
	 * Cache content for the given number of seconds, by writing
	 * cache-related HTTP headers to the response:
	 * <ul>
	 * <li>seconds == -1 (default value): no generation cache-related headers</li>
	 * <li>seconds == 0: "Cache-Control: no-store" will prevent caching</li>
	 * <li>seconds > 0: "Cache-Control: max-age=seconds" will ask to cache content</li>
	 * </ul>
	 * <p>For more specific needs, a custom {@link org.springframework.http.CacheControl}
	 * should be used.
	 * <p>
	 *  缓存内容给定的秒数,通过将缓存相关的HTTP头写入响应：
	 * <ul>
	 *  <li>秒== -1(默认值)：no generation cache-related headers </li> <li> seconds == 0："Cache-Control：no-store"
	 * 将防止缓存</li> <li>秒> 0："Cache-Control：max-age = seconds"将要求缓存内容</li>。
	 * </ul>
	 *  <p>对于更具体的需求,应使用自定义{@link orgspringframeworkhttpCacheControl}
	 * 
	 * 
	 * @see #setCacheControl
	 */
	public final void setCacheSeconds(int seconds) {
		this.cacheSeconds = seconds;
	}

	/**
	 * Return the number of seconds that content is cached.
	 * <p>
	 *  返回缓存内容的秒数
	 * 
	 */
	public final int getCacheSeconds() {
		return this.cacheSeconds;
	}

	/**
	 * Configure one or more request header names (e.g. "Accept-Language") to
	 * add to the "Vary" response header to inform clients that the response is
	 * subject to content negotiation and variances based on the value of the
	 * given request headers. The configured request header names are added only
	 * if not already present in the response "Vary" header.
	 * <p><strong>Note:</strong> This property is only supported on Servlet 3.0+
	 * which allows checking existing response header values.
	 * <p>
	 * 配置一个或多个请求头名称(例如"Accept-Language")以添加到"Vary"响应头,以通知客户端响应内容协商和基于给定请求头的值的方差配置的请求头只有在响应"Vary"header <p> <strong>
	 * 中不存在名称时,才添加名称</strong> </strong>仅在Servlet 30+中支持此属性,这允许检查现有的响应头值。
	 * 
	 * 
	 * @param varyByRequestHeaders one or more request header names
	 * @since 4.3
	 */
	public final void setVaryByRequestHeaders(String... varyByRequestHeaders) {
		this.varyByRequestHeaders = varyByRequestHeaders;
	}

	/**
	 * Return the configured request header names for the "Vary" response header.
	 * <p>
	 *  返回"Vary"响应头的配置的请求头名称
	 * 
	 * 
	 * @since 4.3
	 */
	public final String[] getVaryByRequestHeaders() {
		return this.varyByRequestHeaders;
	}

	/**
	 * Set whether to use the HTTP 1.0 expires header. Default is "false",
	 * as of 4.2.
	 * <p>Note: Cache headers will only get applied if caching is enabled
	 * (or explicitly prevented) for the current request.
	 * <p>
	 *  设置是否使用HTTP 10 expires标头默认为"false",从42 <p>注意：只有当缓存启用(或显式地阻止)当前请求时,缓存头将被应用
	 * 
	 * 
	 * @deprecated as of 4.2, since going forward, the HTTP 1.1 cache-control
	 * header will be required, with the HTTP 1.0 headers disappearing
	 */
	@Deprecated
	public final void setUseExpiresHeader(boolean useExpiresHeader) {
		this.useExpiresHeader = useExpiresHeader;
	}

	/**
	 * Return whether the HTTP 1.0 expires header is used.
	 * <p>
	 *  返回是否使用HTTP 10过期标头
	 * 
	 * 
	 * @deprecated as of 4.2, in favor of {@link #getCacheControl()}
	 */
	@Deprecated
	public final boolean isUseExpiresHeader() {
		return this.useExpiresHeader;
	}

	/**
	 * Set whether to use the HTTP 1.1 cache-control header. Default is "true".
	 * <p>Note: Cache headers will only get applied if caching is enabled
	 * (or explicitly prevented) for the current request.
	 * <p>
	 * 设置是否使用HTTP 11缓存控制头默认值为"true"<p>注意：只有缓存已被启用(或明确地阻止)当前请求时,缓存头将被应用
	 * 
	 * 
	 * @deprecated as of 4.2, since going forward, the HTTP 1.1 cache-control
	 * header will be required, with the HTTP 1.0 headers disappearing
	 */
	@Deprecated
	public final void setUseCacheControlHeader(boolean useCacheControlHeader) {
		this.useCacheControlHeader = useCacheControlHeader;
	}

	/**
	 * Return whether the HTTP 1.1 cache-control header is used.
	 * <p>
	 *  返回是否使用HTTP 11缓存控制头
	 * 
	 * 
	 * @deprecated as of 4.2, in favor of {@link #getCacheControl()}
	 */
	@Deprecated
	public final boolean isUseCacheControlHeader() {
		return this.useCacheControlHeader;
	}

	/**
	 * Set whether to use the HTTP 1.1 cache-control header value "no-store"
	 * when preventing caching. Default is "true".
	 * <p>
	 *  设置是否在防止缓存时使用HTTP 11缓存控制头值"无存储"默认为"true"
	 * 
	 * 
	 * @deprecated as of 4.2, in favor of {@link #setCacheControl}
	 */
	@Deprecated
	public final void setUseCacheControlNoStore(boolean useCacheControlNoStore) {
		this.useCacheControlNoStore = useCacheControlNoStore;
	}

	/**
	 * Return whether the HTTP 1.1 cache-control header value "no-store" is used.
	 * <p>
	 *  返回是否使用HTTP 11缓存控制头值"no-store"
	 * 
	 * 
	 * @deprecated as of 4.2, in favor of {@link #getCacheControl()}
	 */
	@Deprecated
	public final boolean isUseCacheControlNoStore() {
		return this.useCacheControlNoStore;
	}

	/**
	 * An option to add 'must-revalidate' to every Cache-Control header.
	 * This may be useful with annotated controller methods, which can
	 * programmatically do a last-modified calculation as described in
	 * {@link org.springframework.web.context.request.WebRequest#checkNotModified(long)}.
	 * <p>Default is "false".
	 * <p>
	 *  对每个Cache-Control头添加"must-revalidate"的选项这对于注释控制器方法来说可能很有用,它可以通过编程方式进行最后修改的计算,如{@link orgspringframeworkwebcontextrequestWebRequest#checkNotModified(long)}
	 *  <p>中所述。
	 * 是"假"。
	 * 
	 * 
	 * @deprecated as of 4.2, in favor of {@link #setCacheControl}
	 */
	@Deprecated
	public final void setAlwaysMustRevalidate(boolean mustRevalidate) {
		this.alwaysMustRevalidate = mustRevalidate;
	}

	/**
	 * Return whether 'must-revalidate' is added to every Cache-Control header.
	 * <p>
	 * 返回是否将"must-revalidate"添加到每个Cache-Control头
	 * 
	 * 
	 * @deprecated as of 4.2, in favor of {@link #getCacheControl()}
	 */
	@Deprecated
	public final boolean isAlwaysMustRevalidate() {
		return this.alwaysMustRevalidate;
	}


	/**
	 * Check the given request for supported methods and a required session, if any.
	 * <p>
	 *  检查给定的请求是否支持的方法和所需的会话(如果有的话)
	 * 
	 * 
	 * @param request current HTTP request
	 * @throws ServletException if the request cannot be handled because a check failed
	 * @since 4.2
	 */
	protected final void checkRequest(HttpServletRequest request) throws ServletException {
		// Check whether we should support the request method.
		String method = request.getMethod();
		if (this.supportedMethods != null && !this.supportedMethods.contains(method)) {
			throw new HttpRequestMethodNotSupportedException(
					method, StringUtils.toStringArray(this.supportedMethods));
		}

		// Check whether a session is required.
		if (this.requireSession && request.getSession(false) == null) {
			throw new HttpSessionRequiredException("Pre-existing session required but none found");
		}
	}

	/**
	 * Prepare the given response according to the settings of this generator.
	 * Applies the number of cache seconds specified for this generator.
	 * <p>
	 *  根据此生成器的设置准备给定的响应应用为此生成器指定的缓存秒数
	 * 
	 * 
	 * @param response current HTTP response
	 * @since 4.2
	 */
	protected final void prepareResponse(HttpServletResponse response) {
		if (this.cacheControl != null) {
			applyCacheControl(response, this.cacheControl);
		}
		else {
			applyCacheSeconds(response, this.cacheSeconds);
		}
		if (servlet3Present && this.varyByRequestHeaders != null) {
			for (String value : getVaryRequestHeadersToAdd(response)) {
				response.addHeader("Vary", value);
			}
		}
	}

	/**
	 * Set the HTTP Cache-Control header according to the given settings.
	 * <p>
	 *  根据给定的设置设置HTTP Cache-Control标头
	 * 
	 * 
	 * @param response current HTTP response
	 * @param cacheControl the pre-configured cache control settings
	 * @since 4.2
	 */
	protected final void applyCacheControl(HttpServletResponse response, CacheControl cacheControl) {
		String ccValue = cacheControl.getHeaderValue();
		if (ccValue != null) {
			// Set computed HTTP 1.1 Cache-Control header
			response.setHeader(HEADER_CACHE_CONTROL, ccValue);

			if (response.containsHeader(HEADER_PRAGMA)) {
				// Reset HTTP 1.0 Pragma header if present
				response.setHeader(HEADER_PRAGMA, "");
			}
			if (response.containsHeader(HEADER_EXPIRES)) {
				// Reset HTTP 1.0 Expires header if present
				response.setHeader(HEADER_EXPIRES, "");
			}
		}
	}

	/**
	 * Apply the given cache seconds and generate corresponding HTTP headers,
	 * i.e. allow caching for the given number of seconds in case of a positive
	 * value, prevent caching if given a 0 value, do nothing else.
	 * Does not tell the browser to revalidate the resource.
	 * <p>
	 *  应用给定的缓存秒数并生成相应的HTTP标头,即如果为正值,则允许缓存给定的秒数,如果给定0值则阻止缓存,不执行任何操作不会告诉浏览器重新验证资源
	 * 
	 * 
	 * @param response current HTTP response
	 * @param cacheSeconds positive number of seconds into the future that the
	 * response should be cacheable for, 0 to prevent caching
	 */
	@SuppressWarnings("deprecation")
	protected final void applyCacheSeconds(HttpServletResponse response, int cacheSeconds) {
		if (this.useExpiresHeader || !this.useCacheControlHeader) {
			// Deprecated HTTP 1.0 cache behavior, as in previous Spring versions
			if (cacheSeconds > 0) {
				cacheForSeconds(response, cacheSeconds);
			}
			else if (cacheSeconds == 0) {
				preventCaching(response);
			}
		}
		else {
			CacheControl cControl;
			if (cacheSeconds > 0) {
				cControl = CacheControl.maxAge(cacheSeconds, TimeUnit.SECONDS);
				if (this.alwaysMustRevalidate) {
					cControl = cControl.mustRevalidate();
				}
			}
			else if (cacheSeconds == 0) {
				cControl = (this.useCacheControlNoStore ? CacheControl.noStore() : CacheControl.noCache());
			}
			else {
				cControl = CacheControl.empty();
			}
			applyCacheControl(response, cControl);
		}
	}


	/**
	/* <p>
	/* 
	 * @see #checkRequest(HttpServletRequest)
	 * @see #prepareResponse(HttpServletResponse)
	 * @deprecated as of 4.2, since the {@code lastModified} flag is effectively ignored,
	 * with a must-revalidate header only generated if explicitly configured
	 */
	@Deprecated
	protected final void checkAndPrepare(
			HttpServletRequest request, HttpServletResponse response, boolean lastModified) throws ServletException {

		checkRequest(request);
		prepareResponse(response);
	}

	/**
	/* <p>
	/* 
	 * @see #checkRequest(HttpServletRequest)
	 * @see #applyCacheSeconds(HttpServletResponse, int)
	 * @deprecated as of 4.2, since the {@code lastModified} flag is effectively ignored,
	 * with a must-revalidate header only generated if explicitly configured
	 */
	@Deprecated
	protected final void checkAndPrepare(
			HttpServletRequest request, HttpServletResponse response, int cacheSeconds, boolean lastModified)
			throws ServletException {

		checkRequest(request);
		applyCacheSeconds(response, cacheSeconds);
	}

	/**
	 * Apply the given cache seconds and generate respective HTTP headers.
	 * <p>That is, allow caching for the given number of seconds in the
	 * case of a positive value, prevent caching if given a 0 value, else
	 * do nothing (i.e. leave caching to the client).
	 * <p>
	 * 应用给定的缓存秒数并生成相应的HTTP标头<p>即,在正值的情况下允许缓存给定的秒数,如果给定0值则阻止缓存,否则不执行任何操作(即将缓存留给客户端)
	 * 
	 * 
	 * @param response the current HTTP response
	 * @param cacheSeconds the (positive) number of seconds into the future
	 * that the response should be cacheable for; 0 to prevent caching; and
	 * a negative value to leave caching to the client.
	 * @param mustRevalidate whether the client should revalidate the resource
	 * (typically only necessary for controllers with last-modified support)
	 * @deprecated as of 4.2, in favor of {@link #applyCacheControl}
	 */
	@Deprecated
	protected final void applyCacheSeconds(HttpServletResponse response, int cacheSeconds, boolean mustRevalidate) {
		if (cacheSeconds > 0) {
			cacheForSeconds(response, cacheSeconds, mustRevalidate);
		}
		else if (cacheSeconds == 0) {
			preventCaching(response);
		}
	}

	/**
	 * Set HTTP headers to allow caching for the given number of seconds.
	 * Does not tell the browser to revalidate the resource.
	 * <p>
	 *  设置HTTP标头以允许缓存给定的秒数不告诉浏览器重新验证资源
	 * 
	 * 
	 * @param response current HTTP response
	 * @param seconds number of seconds into the future that the response
	 * should be cacheable for
	 * @deprecated as of 4.2, in favor of {@link #applyCacheControl}
	 */
	@Deprecated
	protected final void cacheForSeconds(HttpServletResponse response, int seconds) {
		cacheForSeconds(response, seconds, false);
	}

	/**
	 * Set HTTP headers to allow caching for the given number of seconds.
	 * Tells the browser to revalidate the resource if mustRevalidate is
	 * {@code true}.
	 * <p>
	 *  设置HTTP标头以允许缓存给定的秒数告诉浏览器重新验证资源,如果mustRevalidate是{@code true}
	 * 
	 * 
	 * @param response the current HTTP response
	 * @param seconds number of seconds into the future that the response
	 * should be cacheable for
	 * @param mustRevalidate whether the client should revalidate the resource
	 * (typically only necessary for controllers with last-modified support)
	 * @deprecated as of 4.2, in favor of {@link #applyCacheControl}
	 */
	@Deprecated
	protected final void cacheForSeconds(HttpServletResponse response, int seconds, boolean mustRevalidate) {
		if (this.useExpiresHeader) {
			// HTTP 1.0 header
			response.setDateHeader(HEADER_EXPIRES, System.currentTimeMillis() + seconds * 1000L);
		}
		else if (response.containsHeader(HEADER_EXPIRES)) {
			// Reset HTTP 1.0 Expires header if present
			response.setHeader(HEADER_EXPIRES, "");
		}

		if (this.useCacheControlHeader) {
			// HTTP 1.1 header
			String headerValue = "max-age=" + seconds;
			if (mustRevalidate || this.alwaysMustRevalidate) {
				headerValue += ", must-revalidate";
			}
			response.setHeader(HEADER_CACHE_CONTROL, headerValue);
		}

		if (response.containsHeader(HEADER_PRAGMA)) {
			// Reset HTTP 1.0 Pragma header if present
			response.setHeader(HEADER_PRAGMA, "");
		}
	}

	/**
	 * Prevent the response from being cached.
	 * Only called in HTTP 1.0 compatibility mode.
	 * <p>See {@code http://www.mnot.net/cache_docs}.
	 * <p>
	 *  防止响应缓存仅在HTTP 10兼容模式下调用<p>请参阅{@code http：// wwwmnotnet / cache_docs}
	 * 
	 * @deprecated as of 4.2, in favor of {@link #applyCacheControl}
	 */
	@Deprecated
	protected final void preventCaching(HttpServletResponse response) {
		response.setHeader(HEADER_PRAGMA, "no-cache");

		if (this.useExpiresHeader) {
			// HTTP 1.0 Expires header
			response.setDateHeader(HEADER_EXPIRES, 1L);
		}

		if (this.useCacheControlHeader) {
			// HTTP 1.1 Cache-Control header: "no-cache" is the standard value,
			// "no-store" is necessary to prevent caching on Firefox.
			response.setHeader(HEADER_CACHE_CONTROL, "no-cache");
			if (this.useCacheControlNoStore) {
				response.addHeader(HEADER_CACHE_CONTROL, "no-store");
			}
		}
	}


	private Collection<String> getVaryRequestHeadersToAdd(HttpServletResponse response) {
		if (!response.containsHeader(HttpHeaders.VARY)) {
			return Arrays.asList(getVaryByRequestHeaders());
		}
		Collection<String> result = new ArrayList<String>(getVaryByRequestHeaders().length);
		Collections.addAll(result, getVaryByRequestHeaders());
		for (String header : response.getHeaders(HttpHeaders.VARY)) {
			for (String existing : StringUtils.tokenizeToStringArray(header, ",")) {
				if ("*".equals(existing)) {
					return Collections.emptyList();
				}
				for (String value : getVaryByRequestHeaders()) {
					if (value.equalsIgnoreCase(existing)) {
						result.remove(value);
					}
				}
			}
		}
		return result;
	}

}
