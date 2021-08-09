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

package org.springframework.web.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HierarchicalUriComponents.PathComponent;

/**
 * Builder for {@link UriComponents}.
 *
 * <p>Typical usage involves:
 * <ol>
 * <li>Create a {@code UriComponentsBuilder} with one of the static factory methods
 * (such as {@link #fromPath(String)} or {@link #fromUri(URI)})</li>
 * <li>Set the various URI components through the respective methods ({@link #scheme(String)},
 * {@link #userInfo(String)}, {@link #host(String)}, {@link #port(int)}, {@link #path(String)},
 * {@link #pathSegment(String...)}, {@link #queryParam(String, Object...)}, and
 * {@link #fragment(String)}.</li>
 * <li>Build the {@link UriComponents} instance with the {@link #build()} method.</li>
 * </ol>
 *
 * <p>
 *  {@link UriComponents}的制作工具
 * 
 *  典型用法包括：
 * <ol>
 * <li>使用静态工厂方法(例如{@link #fromPath(String)}或{@link #fromUri(URI)})创建一个{@code UriComponentsBuilder})</li>
 *  <li>设置各种URI组件通过各自的方法({@link #scheme(String)},{@link #userInfo(String)},{@link #host(String)},{@link #port(int)}
 * ,{@link #path(String)},{@link #pathSegment(String)},{@link #queryParam(String,Object)}和{@link #fragment(String)}
 *  </li> <li>构建{@链接UriComponents}实例与{@link #build()}方法</li>。
 * </ol>
 * 
 * 
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @author Phillip Webb
 * @author Oliver Gierke
 * @since 3.1
 * @see #newInstance()
 * @see #fromPath(String)
 * @see #fromUri(URI)
 */
public class UriComponentsBuilder implements Cloneable {

	private static final Pattern QUERY_PARAM_PATTERN = Pattern.compile("([^&=]+)(=?)([^&]+)?");

	private static final String SCHEME_PATTERN = "([^:/?#]+):";

	private static final String HTTP_PATTERN = "(?i)(http|https):";

	private static final String USERINFO_PATTERN = "([^@\\[/?#]*)";

	private static final String HOST_IPV4_PATTERN = "[^\\[/?#:]*";

	private static final String HOST_IPV6_PATTERN = "\\[[\\p{XDigit}\\:\\.]*[%\\p{Alnum}]*\\]";

	private static final String HOST_PATTERN = "(" + HOST_IPV6_PATTERN + "|" + HOST_IPV4_PATTERN + ")";

	private static final String PORT_PATTERN = "(\\d*(?:\\{[^/]+?\\})?)";

	private static final String PATH_PATTERN = "([^?#]*)";

	private static final String QUERY_PATTERN = "([^#]*)";

	private static final String LAST_PATTERN = "(.*)";

	// Regex patterns that matches URIs. See RFC 3986, appendix B
	private static final Pattern URI_PATTERN = Pattern.compile(
			"^(" + SCHEME_PATTERN + ")?" + "(//(" + USERINFO_PATTERN + "@)?" + HOST_PATTERN + "(:" + PORT_PATTERN +
					")?" + ")?" + PATH_PATTERN + "(\\?" + QUERY_PATTERN + ")?" + "(#" + LAST_PATTERN + ")?");

	private static final Pattern HTTP_URL_PATTERN = Pattern.compile(
			"^" + HTTP_PATTERN + "(//(" + USERINFO_PATTERN + "@)?" + HOST_PATTERN + "(:" + PORT_PATTERN + ")?" + ")?" +
					PATH_PATTERN + "(\\?" + LAST_PATTERN + ")?");

	private static final Pattern FORWARDED_HOST_PATTERN = Pattern.compile("host=\"?([^;,\"]+)\"?");

	private static final Pattern FORWARDED_PROTO_PATTERN = Pattern.compile("proto=\"?([^;,\"]+)\"?");


	private String scheme;

	private String ssp;

	private String userInfo;

	private String host;

	private String port;

	private CompositePathComponentBuilder pathBuilder;

	private final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();

	private String fragment;


	/**
	 * Default constructor. Protected to prevent direct instantiation.
	 * <p>
	 *  默认构造函数保护以防止直接实例化
	 * 
	 * 
	 * @see #newInstance()
	 * @see #fromPath(String)
	 * @see #fromUri(URI)
	 */
	protected UriComponentsBuilder() {
		this.pathBuilder = new CompositePathComponentBuilder();
	}

	/**
	 * Create a deep copy of the given UriComponentsBuilder.
	 * <p>
	 *  创建给定UriComponentsBuilder的深层副本
	 * 
	 * 
	 * @param other the other builder to copy from
	 * @since 4.1.3
	 */
	protected UriComponentsBuilder(UriComponentsBuilder other) {
		this.scheme = other.scheme;
		this.ssp = other.ssp;
		this.userInfo = other.userInfo;
		this.host = other.host;
		this.port = other.port;
		this.pathBuilder = other.pathBuilder.cloneBuilder();
		this.queryParams.putAll(other.queryParams);
		this.fragment = other.fragment;
	}


	// Factory methods

	/**
	 * Create a new, empty builder.
	 * <p>
	 *  创建一个新的,空的构建器
	 * 
	 * 
	 * @return the new {@code UriComponentsBuilder}
	 */
	public static UriComponentsBuilder newInstance() {
		return new UriComponentsBuilder();
	}

	/**
	 * Create a builder that is initialized with the given path.
	 * <p>
	 *  创建一个使用给定路径初始化的构建器
	 * 
	 * 
	 * @param path the path to initialize with
	 * @return the new {@code UriComponentsBuilder}
	 */
	public static UriComponentsBuilder fromPath(String path) {
		UriComponentsBuilder builder = new UriComponentsBuilder();
		builder.path(path);
		return builder;
	}

	/**
	 * Create a builder that is initialized with the given {@code URI}.
	 * <p>
	 *  创建一个使用给定的{@code URI}初始化的构建器
	 * 
	 * 
	 * @param uri the URI to initialize with
	 * @return the new {@code UriComponentsBuilder}
	 */
	public static UriComponentsBuilder fromUri(URI uri) {
		UriComponentsBuilder builder = new UriComponentsBuilder();
		builder.uri(uri);
		return builder;
	}

	/**
	 * Create a builder that is initialized with the given URI string.
	 * <p><strong>Note:</strong> The presence of reserved characters can prevent
	 * correct parsing of the URI string. For example if a query parameter
	 * contains {@code '='} or {@code '&'} characters, the query string cannot
	 * be parsed unambiguously. Such values should be substituted for URI
	 * variables to enable correct parsing:
	 * <pre class="code">
	 * String uriString = &quot;/hotels/42?filter={value}&quot;;
	 * UriComponentsBuilder.fromUriString(uriString).buildAndExpand(&quot;hot&amp;cold&quot;);
	 * </pre>
	 * <p>
	 * 创建一个使用给定URI字符串初始化的构建器<p> <strong>注意：</strong>保留字符的存在可能阻止对URI字符串的正确解析例如,如果查询参数包含{@code'='}或{@code'&'}字
	 * 符,查询字符串无法明确解析这些值应替代URI变量来启用正确解析：。
	 * <pre class="code">
	 *  String uriString =&quot; / hotels / 42?filter = {value}"; UriComponentsBuilderfromUriString(uriStrin
	 * g中)buildAndExpand(QUOT;热&安培;冷QUOT);。
	 * </pre>
	 * 
	 * @param uri the URI string to initialize with
	 * @return the new {@code UriComponentsBuilder}
	 */
	public static UriComponentsBuilder fromUriString(String uri) {
		Assert.notNull(uri, "URI must not be null");
		Matcher matcher = URI_PATTERN.matcher(uri);
		if (matcher.matches()) {
			UriComponentsBuilder builder = new UriComponentsBuilder();
			String scheme = matcher.group(2);
			String userInfo = matcher.group(5);
			String host = matcher.group(6);
			String port = matcher.group(8);
			String path = matcher.group(9);
			String query = matcher.group(11);
			String fragment = matcher.group(13);
			boolean opaque = false;
			if (StringUtils.hasLength(scheme)) {
				String rest = uri.substring(scheme.length());
				if (!rest.startsWith(":/")) {
					opaque = true;
				}
			}
			builder.scheme(scheme);
			if (opaque) {
				String ssp = uri.substring(scheme.length()).substring(1);
				if (StringUtils.hasLength(fragment)) {
					ssp = ssp.substring(0, ssp.length() - (fragment.length() + 1));
				}
				builder.schemeSpecificPart(ssp);
			}
			else {
				builder.userInfo(userInfo);
				builder.host(host);
				if (StringUtils.hasLength(port)) {
					builder.port(port);
				}
				builder.path(path);
				builder.query(query);
			}
			if (StringUtils.hasText(fragment)) {
				builder.fragment(fragment);
			}
			return builder;
		}
		else {
			throw new IllegalArgumentException("[" + uri + "] is not a valid URI");
		}
	}

	/**
	 * Create a URI components builder from the given HTTP URL String.
	 * <p><strong>Note:</strong> The presence of reserved characters can prevent
	 * correct parsing of the URI string. For example if a query parameter
	 * contains {@code '='} or {@code '&'} characters, the query string cannot
	 * be parsed unambiguously. Such values should be substituted for URI
	 * variables to enable correct parsing:
	 * <pre class="code">
	 * String uriString = &quot;/hotels/42?filter={value}&quot;;
	 * UriComponentsBuilder.fromUriString(uriString).buildAndExpand(&quot;hot&amp;cold&quot;);
	 * </pre>
	 * <p>
	 * 从给定的HTTP URL创建一个URI组件构建器String <p> <strong>注意：</strong>保留字符的存在可以阻止正确解析URI字符串例如,如果查询参数包含{@code'='}或{@code'&'}
	 * 字符,查询字符串无法明确解析这些值应替代URI变量来启用正确解析：。
	 * <pre class="code">
	 *  String uriString =&quot; / hotels / 42?filter = {value}"; UriComponentsBuilderfromUriString(uriStrin
	 * g中)buildAndExpand(QUOT;热&安培;冷QUOT);。
	 * </pre>
	 * 
	 * @param httpUrl the source URI
	 * @return the URI components of the URI
	 */
	public static UriComponentsBuilder fromHttpUrl(String httpUrl) {
		Assert.notNull(httpUrl, "HTTP URL must not be null");
		Matcher matcher = HTTP_URL_PATTERN.matcher(httpUrl);
		if (matcher.matches()) {
			UriComponentsBuilder builder = new UriComponentsBuilder();
			String scheme = matcher.group(1);
			builder.scheme(scheme != null ? scheme.toLowerCase() : null);
			builder.userInfo(matcher.group(4));
			String host = matcher.group(5);
			if (StringUtils.hasLength(scheme) && !StringUtils.hasLength(host)) {
				throw new IllegalArgumentException("[" + httpUrl + "] is not a valid HTTP URL");
			}
			builder.host(host);
			String port = matcher.group(7);
			if (StringUtils.hasLength(port)) {
				builder.port(port);
			}
			builder.path(matcher.group(8));
			builder.query(matcher.group(10));
			return builder;
		}
		else {
			throw new IllegalArgumentException("[" + httpUrl + "] is not a valid HTTP URL");
		}
	}

	/**
	 * Create a new {@code UriComponents} object from the URI associated with
	 * the given HttpRequest while also overlaying with values from the headers
	 * "Forwarded" (<a href="http://tools.ietf.org/html/rfc7239">RFC 7239</a>,
	 * or "X-Forwarded-Host", "X-Forwarded-Port", and "X-Forwarded-Proto" if
	 * "Forwarded" is not found.
	 * <p>
	 * 从与给定的HttpRequest相关联的URI中创建一个新的{@code UriComponents}对象,同时也覆盖标题"转发"中的值(<a href=\"http://toolsietforg/html/rfc7239\">
	 *  RFC 7239 </a >或"X-Forwarded-Host","X-Forwarded-Port"和"X-Forwarded-Proto",如果没有找到"转发"。
	 * 
	 * 
	 * @param request the source request
	 * @return the URI components of the URI
	 * @since 4.1.5
	 */
	public static UriComponentsBuilder fromHttpRequest(HttpRequest request) {
		return fromUri(request.getURI()).adaptFromForwardedHeaders(request.getHeaders());
	}

	/**
	 * Create an instance by parsing the "Origin" header of an HTTP request.
	 * <p>
	 *  通过解析HTTP请求的"Origin"头来创建一个实例
	 * 
	 * 
	 * @see <a href="https://tools.ietf.org/html/rfc6454">RFC 6454</a>
	 */
	public static UriComponentsBuilder fromOriginHeader(String origin) {
		Matcher matcher = URI_PATTERN.matcher(origin);
		if (matcher.matches()) {
			UriComponentsBuilder builder = new UriComponentsBuilder();
			String scheme = matcher.group(2);
			String host = matcher.group(6);
			String port = matcher.group(8);
			if (StringUtils.hasLength(scheme)) {
				builder.scheme(scheme);
			}
			builder.host(host);
			if (StringUtils.hasLength(port)) {
				builder.port(port);
			}
			return builder;
		}
		else {
			throw new IllegalArgumentException("[" + origin + "] is not a valid \"Origin\" header value");
		}
	}


	// build methods

	/**
	 * Build a {@code UriComponents} instance from the various components contained in this builder.
	 * <p>
	 *  从此构建器中包含的各种组件构建{@code UriComponents}实例
	 * 
	 * 
	 * @return the URI components
	 */
	public UriComponents build() {
		return build(false);
	}

	/**
	 * Build a {@code UriComponents} instance from the various components
	 * contained in this builder.
	 * <p>
	 *  从此构建器中包含的各种组件构建{@code UriComponents}实例
	 * 
	 * 
	 * @param encoded whether all the components set in this builder are
	 * encoded ({@code true}) or not ({@code false})
	 * @return the URI components
	 */
	public UriComponents build(boolean encoded) {
		if (this.ssp != null) {
			return new OpaqueUriComponents(this.scheme, this.ssp, this.fragment);
		}
		else {
			return new HierarchicalUriComponents(this.scheme, this.userInfo, this.host, this.port,
					this.pathBuilder.build(), this.queryParams, this.fragment, encoded, true);
		}
	}

	/**
	 * Build a {@code UriComponents} instance and replaces URI template variables
	 * with the values from a map. This is a shortcut method which combines
	 * calls to {@link #build()} and then {@link UriComponents#expand(Map)}.
	 * <p>
	 *  构建一个{@code UriComponents}实例,并使用地图中的值替换URI模板变量这是一种将调用绑定到{@link #build()},然后{@link UriComponents#expand(Map)}
	 * 的快捷方式。
	 * 
	 * 
	 * @param uriVariables the map of URI variables
	 * @return the URI components with expanded values
	 */
	public UriComponents buildAndExpand(Map<String, ?> uriVariables) {
		return build(false).expand(uriVariables);
	}

	/**
	 * Build a {@code UriComponents} instance and replaces URI template variables
	 * with the values from an array. This is a shortcut method which combines
	 * calls to {@link #build()} and then {@link UriComponents#expand(Object...)}.
	 * <p>
	 * 构建一个{@code UriComponents}实例,并使用数组中的值替换URI模板变量这是一种将调用绑定到{@link #build()},然后{@link UriComponents#expand(Object)}
	 * 的快捷方式。
	 * 
	 * 
	 * @param uriVariableValues URI variable values
	 * @return the URI components with expanded values
	 */
	public UriComponents buildAndExpand(Object... uriVariableValues) {
		return build(false).expand(uriVariableValues);
	}

	/**
	 * Build a URI String. This is a shortcut method which combines calls
	 * to {@link #build()}, then {@link UriComponents#encode()} and finally
	 * {@link UriComponents#toUriString()}.
	 * <p>
	 *  构建一个URI字符串这是一个快捷方式,它结合了对{@link #build()}的调用,然后{@link UriComponents#encode()},最后{@link UriComponents#toUriString()}
	 * )。
	 * 
	 * 
	 * @since 4.1
	 * @see UriComponents#toUriString()
	 */
	public String toUriString() {
		return build(false).encode().toUriString();
	}


	// URI components methods

	/**
	 * Initialize all components of this URI builder with the components of the given URI.
	 * <p>
	 *  使用给定URI的组件初始化此URI构建器的所有组件
	 * 
	 * 
	 * @param uri the URI
	 * @return this UriComponentsBuilder
	 */
	public UriComponentsBuilder uri(URI uri) {
		Assert.notNull(uri, "URI must not be null");
		this.scheme = uri.getScheme();
		if (uri.isOpaque()) {
			this.ssp = uri.getRawSchemeSpecificPart();
			resetHierarchicalComponents();
		}
		else {
			if (uri.getRawUserInfo() != null) {
				this.userInfo = uri.getRawUserInfo();
			}
			if (uri.getHost() != null) {
				this.host = uri.getHost();
			}
			if (uri.getPort() != -1) {
				this.port = String.valueOf(uri.getPort());
			}
			if (StringUtils.hasLength(uri.getRawPath())) {
				this.pathBuilder = new CompositePathComponentBuilder(uri.getRawPath());
			}
			if (StringUtils.hasLength(uri.getRawQuery())) {
				this.queryParams.clear();
				query(uri.getRawQuery());
			}
			resetSchemeSpecificPart();
		}
		if (uri.getRawFragment() != null) {
			this.fragment = uri.getRawFragment();
		}
		return this;
	}

	/**
	 * Set the URI scheme. The given scheme may contain URI template variables,
	 * and may also be {@code null} to clear the scheme of this builder.
	 * <p>
	 *  设置URI方案给定的方案可能包含URI模板变量,也可能是{@code null}来清除此构建器的方案
	 * 
	 * 
	 * @param scheme the URI scheme
	 * @return this UriComponentsBuilder
	 */
	public UriComponentsBuilder scheme(String scheme) {
		this.scheme = scheme;
		return this;
	}

	/**
	 * Set all components of this URI builder from the given {@link UriComponents}.
	 * <p>
	 *  从给定的{@link UriComponents}设置此URI构建器的所有组件
	 * 
	 * 
	 * @param uriComponents the UriComponents instance
	 * @return this UriComponentsBuilder
	 */
	public UriComponentsBuilder uriComponents(UriComponents uriComponents) {
		Assert.notNull(uriComponents, "UriComponents must not be null");
		uriComponents.copyToUriComponentsBuilder(this);
		return this;
	}

	/**
	 * Set the URI scheme-specific-part. When invoked, this method overwrites
	 * {@linkplain #userInfo(String) user-info}, {@linkplain #host(String) host},
	 * {@linkplain #port(int) port}, {@linkplain #path(String) path}, and
	 * {@link #query(String) query}.
	 * <p>
	 * 设置URI scheme-specific-part调用时,此方法将覆盖{@linkplain #userInfo(String)user-info},{@linkplain #host(String)host}
	 * ,{@linkplain #port(int)port},{ @linkplain #path(String)path}和{@link #query(String)query}。
	 * 
	 * 
	 * @param ssp the URI scheme-specific-part, may contain URI template parameters
	 * @return this UriComponentsBuilder
	 */
	public UriComponentsBuilder schemeSpecificPart(String ssp) {
		this.ssp = ssp;
		resetHierarchicalComponents();
		return this;
	}

	/**
	 * Set the URI user info. The given user info may contain URI template variables,
	 * and may also be {@code null} to clear the user info of this builder.
	 * <p>
	 *  设置URI用户信息给定的用户信息可能包含URI模板变量,也可能是{@code null}以清除此构建器的用户信息
	 * 
	 * 
	 * @param userInfo the URI user info
	 * @return this UriComponentsBuilder
	 */
	public UriComponentsBuilder userInfo(String userInfo) {
		this.userInfo = userInfo;
		resetSchemeSpecificPart();
		return this;
	}

	/**
	 * Set the URI host. The given host may contain URI template variables,
	 * and may also be {@code null} to clear the host of this builder.
	 * <p>
	 *  设置URI主机给定的主机可能包含URI模板变量,也可能是{@code null}来清除此构建器的主机
	 * 
	 * 
	 * @param host the URI host
	 * @return this UriComponentsBuilder
	 */
	public UriComponentsBuilder host(String host) {
		this.host = host;
		resetSchemeSpecificPart();
		return this;
	}

	/**
	 * Set the URI port. Passing {@code -1} will clear the port of this builder.
	 * <p>
	 *  设置URI端口Passing {@code -1}将清除此构建器的端口
	 * 
	 * 
	 * @param port the URI port
	 * @return this UriComponentsBuilder
	 */
	public UriComponentsBuilder port(int port) {
		Assert.isTrue(port >= -1, "Port must be >= -1");
		this.port = String.valueOf(port);
		resetSchemeSpecificPart();
		return this;
	}

	/**
	 * Set the URI port. Use this method only when the port needs to be
	 * parameterized with a URI variable. Otherwise use {@link #port(int)}.
	 * Passing {@code null} will clear the port of this builder.
	 * <p>
	 *  设置URI端口仅当端口需要使用URI变量进行参数化时使用此方法否则使用{@link #port(int)}传递{@code null}将清除此构建器的端口
	 * 
	 * 
	 * @param port the URI port
	 * @return this UriComponentsBuilder
	 */
	public UriComponentsBuilder port(String port) {
		this.port = port;
		resetSchemeSpecificPart();
		return this;
	}

	/**
	 * Append the given path to the existing path of this builder.
	 * The given path may contain URI template variables.
	 * <p>
	 * 将给定路径附加到此构建器的现有路径给定路径可能包含URI模板变量
	 * 
	 * 
	 * @param path the URI path
	 * @return this UriComponentsBuilder
	 */
	public UriComponentsBuilder path(String path) {
		this.pathBuilder.addPath(path);
		resetSchemeSpecificPart();
		return this;
	}

	/**
	 * Set the path of this builder overriding all existing path and path segment values.
	 * <p>
	 *  设置此构建器的路径,覆盖所有现有路径和路径段值
	 * 
	 * 
	 * @param path the URI path; a {@code null} value results in an empty path.
	 * @return this UriComponentsBuilder
	 */
	public UriComponentsBuilder replacePath(String path) {
		this.pathBuilder = new CompositePathComponentBuilder(path);
		resetSchemeSpecificPart();
		return this;
	}

	/**
	 * Append path segments to the existing path. Each path segment may contain
	 * URI template variables and should not contain any slashes.
	 * Use {@code path("/")} subsequently to ensure a trailing slash.
	 * <p>
	 *  将路径段附加到现有路径每个路径段可能包含URI模板变量,并且不应包含任何斜杠。使用{@code path("/")},以确保尾随斜杠
	 * 
	 * 
	 * @param pathSegments the URI path segments
	 * @return this UriComponentsBuilder
	 */
	public UriComponentsBuilder pathSegment(String... pathSegments) throws IllegalArgumentException {
		this.pathBuilder.addPathSegments(pathSegments);
		resetSchemeSpecificPart();
		return this;
	}

	/**
	 * Append the given query to the existing query of this builder.
	 * The given query may contain URI template variables.
	 * <p><strong>Note:</strong> The presence of reserved characters can prevent
	 * correct parsing of the URI string. For example if a query parameter
	 * contains {@code '='} or {@code '&'} characters, the query string cannot
	 * be parsed unambiguously. Such values should be substituted for URI
	 * variables to enable correct parsing:
	 * <pre class="code">
	 * UriComponentsBuilder.fromUriString(&quot;/hotels/42&quot;)
	 * 	.query(&quot;filter={value}&quot;)
	 * 	.buildAndExpand(&quot;hot&amp;cold&quot;);
	 * </pre>
	 * <p>
	 * 将给定的查询附加到此构建器的现有查询给定查询可能包含URI模板变量<p> <strong>注意：</strong>保留字符的存在可能阻止正确解析URI字符串例如,如果查询参数包含{@code'='}或{@code'&'}
	 * 字符,查询字符串不能被明确解析这些值应该替代URI变量来启用正确的解析：。
	 * <pre class="code">
	 *  UriComponentsBuilderfromUriString("/ hotels / 42")查询("filter = {value}")buildAndExpand("hot&amp; col
	 * d");。
	 * </pre>
	 * 
	 * @param query the query string
	 * @return this UriComponentsBuilder
	 */
	public UriComponentsBuilder query(String query) {
		if (query != null) {
			Matcher matcher = QUERY_PARAM_PATTERN.matcher(query);
			while (matcher.find()) {
				String name = matcher.group(1);
				String eq = matcher.group(2);
				String value = matcher.group(3);
				queryParam(name, (value != null ? value : (StringUtils.hasLength(eq) ? "" : null)));
			}
		}
		else {
			this.queryParams.clear();
		}
		resetSchemeSpecificPart();
		return this;
	}

	/**
	 * Set the query of this builder overriding all existing query parameters.
	 * <p>
	 *  设置此构建器的查询覆盖所有现有的查询参数
	 * 
	 * 
	 * @param query the query string; a {@code null} value removes all query parameters.
	 * @return this UriComponentsBuilder
	 */
	public UriComponentsBuilder replaceQuery(String query) {
		this.queryParams.clear();
		query(query);
		resetSchemeSpecificPart();
		return this;
	}

	/**
	 * Append the given query parameter to the existing query parameters. The
	 * given name or any of the values may contain URI template variables. If no
	 * values are given, the resulting URI will contain the query parameter name
	 * only (i.e. {@code ?foo} instead of {@code ?foo=bar}.
	 * <p>
	 * 将给定的查询参数附加到现有查询参数给定的名称或任何值可能包含URI模板变量如果没有给出值,则生成的URI将仅包含查询参数名称(即{@code?foo}而不是{ @code?foo = bar}
	 * 
	 * 
	 * @param name the query parameter name
	 * @param values the query parameter values
	 * @return this UriComponentsBuilder
	 */
	public UriComponentsBuilder queryParam(String name, Object... values) {
		Assert.notNull(name, "Name must not be null");
		if (!ObjectUtils.isEmpty(values)) {
			for (Object value : values) {
				String valueAsString = (value != null ? value.toString() : null);
				this.queryParams.add(name, valueAsString);
			}
		}
		else {
			this.queryParams.add(name, null);
		}
		resetSchemeSpecificPart();
		return this;
	}

	/**
	 * Add the given query parameters.
	 * <p>
	 *  添加给定的查询参数
	 * 
	 * 
	 * @param params the params
	 * @return this UriComponentsBuilder
	 * @since 4.0
	 */
	public UriComponentsBuilder queryParams(MultiValueMap<String, String> params) {
		if (params != null) {
			this.queryParams.putAll(params);
		}
		return this;
	}

	/**
	 * Set the query parameter values overriding all existing query values for
	 * the same parameter. If no values are given, the query parameter is removed.
	 * <p>
	 *  设置覆盖相同参数的所有现有查询值的查询参数值如果没有给出值,则会删除查询参数
	 * 
	 * 
	 * @param name the query parameter name
	 * @param values the query parameter values
	 * @return this UriComponentsBuilder
	 */
	public UriComponentsBuilder replaceQueryParam(String name, Object... values) {
		Assert.notNull(name, "Name must not be null");
		this.queryParams.remove(name);
		if (!ObjectUtils.isEmpty(values)) {
			queryParam(name, values);
		}
		resetSchemeSpecificPart();
		return this;
	}

	/**
	 * Set the query parameter values overriding all existing query values.
	 * <p>
	 *  设置覆盖所有现有查询值的查询参数值
	 * 
	 * 
	 * @param params the query parameter name
	 * @return this UriComponentsBuilder
	 * @since 4.2
	 */
	public UriComponentsBuilder replaceQueryParams(MultiValueMap<String, String> params) {
		this.queryParams.clear();
		if (params != null) {
			this.queryParams.putAll(params);
		}
		return this;
	}

	/**
	 * Set the URI fragment. The given fragment may contain URI template variables,
	 * and may also be {@code null} to clear the fragment of this builder.
	 * <p>
	 *  设置URI片段给定片段可能包含URI模板变量,也可能是{@code null}来清除此构建器的片段
	 * 
	 * 
	 * @param fragment the URI fragment
	 * @return this UriComponentsBuilder
	 */
	public UriComponentsBuilder fragment(String fragment) {
		if (fragment != null) {
			Assert.hasLength(fragment, "Fragment must not be empty");
			this.fragment = fragment;
		}
		else {
			this.fragment = null;
		}
		return this;
	}

	/**
	 * Adapt this builder's scheme+host+port from the given headers, specifically
	 * "Forwarded" (<a href="http://tools.ietf.org/html/rfc7239">RFC 7239</a>,
	 * or "X-Forwarded-Host", "X-Forwarded-Port", and "X-Forwarded-Proto" if
	 * "Forwarded" is not found.
	 * <p>
	 * 从给定的标题,特别是"转发"(<a href=\"http://toolsietforg/html/rfc7239\"> RFC 7239 </a>或"X-Forwarded-Host")调整此构建器的
	 * 方案+主机+端口, ","Adapt this builder's scheme+host+port from the given headers, specifically \"Forwarded\"
	 *  (<a href=\"http://toolsietforg/html/rfc7239\">RFC 7239</a>, or \"X-Forwarded-Host\"X-Forwarded-Port"
	 * 和"X-Forwarded-Proto"如果没有找到"转发"。
	 * 
	 * 
	 * @param headers the HTTP headers to consider
	 * @return this UriComponentsBuilder
	 * @since 4.2.7
	 */
	UriComponentsBuilder adaptFromForwardedHeaders(HttpHeaders headers) {
		String forwardedHeader = headers.getFirst("Forwarded");
		if (StringUtils.hasText(forwardedHeader)) {
			String forwardedToUse = StringUtils.tokenizeToStringArray(forwardedHeader, ",")[0];
			Matcher matcher = FORWARDED_HOST_PATTERN.matcher(forwardedToUse);
			if (matcher.find()) {
				host(matcher.group(1).trim());
			}
			matcher = FORWARDED_PROTO_PATTERN.matcher(forwardedToUse);
			if (matcher.find()) {
				scheme(matcher.group(1).trim());
			}
		}
		else {
			String hostHeader = headers.getFirst("X-Forwarded-Host");
			if (StringUtils.hasText(hostHeader)) {
				String hostToUse = StringUtils.tokenizeToStringArray(hostHeader, ",")[0];
				String[] hostAndPort = StringUtils.split(hostToUse, ":");
				if (hostAndPort != null) {
					host(hostAndPort[0]);
					port(Integer.parseInt(hostAndPort[1]));
				}
				else {
					host(hostToUse);
					port(null);
				}
			}

			String portHeader = headers.getFirst("X-Forwarded-Port");
			if (StringUtils.hasText(portHeader)) {
				port(Integer.parseInt(StringUtils.tokenizeToStringArray(portHeader, ",")[0]));
			}

			String protocolHeader = headers.getFirst("X-Forwarded-Proto");
			if (StringUtils.hasText(protocolHeader)) {
				scheme(StringUtils.tokenizeToStringArray(protocolHeader, ",")[0]);
			}
		}

		if ((this.scheme.equals("http") && "80".equals(this.port)) ||
				(this.scheme.equals("https") && "443".equals(this.port))) {
			this.port = null;
		}

		return this;
	}

	private void resetHierarchicalComponents() {
		this.userInfo = null;
		this.host = null;
		this.port = null;
		this.pathBuilder = new CompositePathComponentBuilder();
		this.queryParams.clear();
	}

	private void resetSchemeSpecificPart() {
		this.ssp = null;
	}


	/**
	 * Public declaration of Object's {@code clone()} method.
	 * Delegates to {@link #cloneBuilder()}.
	 * <p>
	 *  Object的{@code clone()}方法的公开声明委托{@link #cloneBuilder()}
	 * 
	 * 
	 * @see Object#clone()
	 */
	@Override
	public Object clone() {
		return cloneBuilder();
	}

	/**
	 * Clone this {@code UriComponentsBuilder}.
	 * <p>
	 *  克隆这个{@code UriComponentsBuilder}
	 * 
	 * @return the cloned {@code UriComponentsBuilder} object
	 * @since 4.2.7
	 */
	public UriComponentsBuilder cloneBuilder() {
		return new UriComponentsBuilder(this);
	}


	private interface PathComponentBuilder {

		PathComponent build();

		PathComponentBuilder cloneBuilder();
	}


	private static class CompositePathComponentBuilder implements PathComponentBuilder {

		private final LinkedList<PathComponentBuilder> builders = new LinkedList<PathComponentBuilder>();

		public CompositePathComponentBuilder() {
		}

		public CompositePathComponentBuilder(String path) {
			addPath(path);
		}

		public void addPathSegments(String... pathSegments) {
			if (!ObjectUtils.isEmpty(pathSegments)) {
				PathSegmentComponentBuilder psBuilder = getLastBuilder(PathSegmentComponentBuilder.class);
				FullPathComponentBuilder fpBuilder = getLastBuilder(FullPathComponentBuilder.class);
				if (psBuilder == null) {
					psBuilder = new PathSegmentComponentBuilder();
					this.builders.add(psBuilder);
					if (fpBuilder != null) {
						fpBuilder.removeTrailingSlash();
					}
				}
				psBuilder.append(pathSegments);
			}
		}

		public void addPath(String path) {
			if (StringUtils.hasText(path)) {
				PathSegmentComponentBuilder psBuilder = getLastBuilder(PathSegmentComponentBuilder.class);
				FullPathComponentBuilder fpBuilder = getLastBuilder(FullPathComponentBuilder.class);
				if (psBuilder != null) {
					path = path.startsWith("/") ? path : "/" + path;
				}
				if (fpBuilder == null) {
					fpBuilder = new FullPathComponentBuilder();
					this.builders.add(fpBuilder);
				}
				fpBuilder.append(path);
			}
		}

		@SuppressWarnings("unchecked")
		private <T> T getLastBuilder(Class<T> builderClass) {
			if (!this.builders.isEmpty()) {
				PathComponentBuilder last = this.builders.getLast();
				if (builderClass.isInstance(last)) {
					return (T) last;
				}
			}
			return null;
		}

		@Override
		public PathComponent build() {
			int size = this.builders.size();
			List<PathComponent> components = new ArrayList<PathComponent>(size);
			for (PathComponentBuilder componentBuilder : this.builders) {
				PathComponent pathComponent = componentBuilder.build();
				if (pathComponent != null) {
					components.add(pathComponent);
				}
			}
			if (components.isEmpty()) {
				return HierarchicalUriComponents.NULL_PATH_COMPONENT;
			}
			if (components.size() == 1) {
				return components.get(0);
			}
			return new HierarchicalUriComponents.PathComponentComposite(components);
		}

		@Override
		public CompositePathComponentBuilder cloneBuilder() {
			CompositePathComponentBuilder compositeBuilder = new CompositePathComponentBuilder();
			for (PathComponentBuilder builder : this.builders) {
				compositeBuilder.builders.add(builder.cloneBuilder());
			}
			return compositeBuilder;
		}
	}


	private static class FullPathComponentBuilder implements PathComponentBuilder {

		private final StringBuilder path = new StringBuilder();

		public void append(String path) {
			this.path.append(path);
		}

		@Override
		public PathComponent build() {
			if (this.path.length() == 0) {
				return null;
			}
			String path = this.path.toString();
			while (true) {
				int index = path.indexOf("//");
				if (index == -1) {
					break;
				}
				path = path.substring(0, index) + path.substring(index + 1);
			}
			return new HierarchicalUriComponents.FullPathComponent(path);
		}

		public void removeTrailingSlash() {
			int index = this.path.length() - 1;
			if (this.path.charAt(index) == '/') {
				this.path.deleteCharAt(index);
			}
		}

		@Override
		public FullPathComponentBuilder cloneBuilder() {
			FullPathComponentBuilder builder = new FullPathComponentBuilder();
			builder.append(this.path.toString());
			return builder;
		}
	}


	private static class PathSegmentComponentBuilder implements PathComponentBuilder {

		private final List<String> pathSegments = new LinkedList<String>();

		public void append(String... pathSegments) {
			for (String pathSegment : pathSegments) {
				if (StringUtils.hasText(pathSegment)) {
					this.pathSegments.add(pathSegment);
				}
			}
		}

		@Override
		public PathComponent build() {
			return (this.pathSegments.isEmpty() ? null :
					new HierarchicalUriComponents.PathSegmentComponent(this.pathSegments));
		}

		@Override
		public PathSegmentComponentBuilder cloneBuilder() {
			PathSegmentComponentBuilder builder = new PathSegmentComponentBuilder();
			builder.pathSegments.addAll(this.pathSegments);
			return builder;
		}
	}

}
