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

package org.springframework.web.servlet.view;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.FlashMapManager;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.SmartView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.support.RequestDataValueProcessor;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

/**
 * View that redirects to an absolute, context relative, or current request
 * relative URL. The URL may be a URI template in which case the URI template
 * variables will be replaced with values available in the model. By default
 * all primitive model attributes (or collections thereof) are exposed as HTTP
 * query parameters (assuming they've not been used as URI template variables),
 * but this behavior can be changed by overriding the
 * {@link #isEligibleProperty(String, Object)} method.
 *
 * <p>A URL for this view is supposed to be a HTTP redirect URL, i.e.
 * suitable for HttpServletResponse's {@code sendRedirect} method, which
 * is what actually does the redirect if the HTTP 1.0 flag is on, or via sending
 * back an HTTP 303 code - if the HTTP 1.0 compatibility flag is off.
 *
 * <p>Note that while the default value for the "contextRelative" flag is off,
 * you will probably want to almost always set it to true. With the flag off,
 * URLs starting with "/" are considered relative to the web server root, while
 * with the flag on, they are considered relative to the web application root.
 * Since most web applications will never know or care what their context path
 * actually is, they are much better off setting this flag to true, and submitting
 * paths which are to be considered relative to the web application root.
 *
 * <p><b>NOTE when using this redirect view in a Portlet environment:</b> Make sure
 * that your controller respects the Portlet {@code sendRedirect} constraints.
 *
 * <p>
 * 查看重定向到绝对,上下文相对或当前请求相对URL URL可能是URI模板,在这种情况下,URI模板变量将替换为模型中可用的值默认情况下,所有原始模型属性(或其集合)为暴露为HTTP查询参数(假设它们未被
 * 用作URI模板变量),但是可以通过覆盖{@link #isEligibleProperty(String,Object)}方法来更改此行为。
 * 
 *  <p>此视图的URL应该是HTTP重定向URL,即适用于HttpServletResponse的{@code sendRedirect}方法,如果HTTP 10标志处于打开状态,则重定向是实际的,或者
 * 通过发回HTTP 303代码 - 如果HTTP 10兼容性标志关闭。
 * 
 * <p>请注意,虽然"contextRelative"标志的默认值已关闭,但您可能希望几乎总是将其设置为true关闭标志,以"/"开头的URL将被视为相对于Web服务器根目录,而标记在上面,它们被认为是相
 * 对于Web应用程序根源由于大多数Web应用程序将永远不会知道或关心他们的上下文路径实际上是什么,他们更好地将此标志设置为true,并提交被认为是相对的路径到Web应用程序根目录。
 * 
 *  <p> <b>在Portlet环境中使用此重定向视图时,请注意：</b>确保您的控制器遵守Portlet {@code sendRedirect}约束条件
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Colin Sampaleanu
 * @author Sam Brannen
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @see #setContextRelative
 * @see #setHttp10Compatible
 * @see #setExposeModelAttributes
 * @see javax.servlet.http.HttpServletResponse#sendRedirect
 */
public class RedirectView extends AbstractUrlBasedView implements SmartView {

	private static final Pattern URI_TEMPLATE_VARIABLE_PATTERN = Pattern.compile("\\{([^/]+?)\\}");


	private boolean contextRelative = false;

	private boolean http10Compatible = true;

	private boolean exposeModelAttributes = true;

	private String encodingScheme;

	private HttpStatus statusCode;

	private boolean expandUriTemplateVariables = true;

	private boolean propagateQueryParams = false;

	private String[] hosts;


	/**
	 * Constructor for use as a bean.
	 * <p>
	 *  用作bean的构造方法
	 * 
	 */
	public RedirectView() {
		setExposePathVariables(false);
	}

	/**
	 * Create a new RedirectView with the given URL.
	 * <p>The given URL will be considered as relative to the web server,
	 * not as relative to the current ServletContext.
	 * <p>
	 * 使用给定的URL创建一个新的RedirectView <p>给定的URL将被认为是相对于Web服务器,而不是相对于当前的ServletContext
	 * 
	 * 
	 * @param url the URL to redirect to
	 * @see #RedirectView(String, boolean)
	 */
	public RedirectView(String url) {
		super(url);
		setExposePathVariables(false);
	}

	/**
	 * Create a new RedirectView with the given URL.
	 * <p>
	 *  使用给定的URL创建一个新的RedirectView
	 * 
	 * 
	 * @param url the URL to redirect to
	 * @param contextRelative whether to interpret the given URL as
	 * relative to the current ServletContext
	 */
	public RedirectView(String url, boolean contextRelative) {
		super(url);
		this.contextRelative = contextRelative;
		setExposePathVariables(false);
	}

	/**
	 * Create a new RedirectView with the given URL.
	 * <p>
	 *  使用给定的URL创建一个新的RedirectView
	 * 
	 * 
	 * @param url the URL to redirect to
	 * @param contextRelative whether to interpret the given URL as
	 * relative to the current ServletContext
	 * @param http10Compatible whether to stay compatible with HTTP 1.0 clients
	 */
	public RedirectView(String url, boolean contextRelative, boolean http10Compatible) {
		super(url);
		this.contextRelative = contextRelative;
		this.http10Compatible = http10Compatible;
		setExposePathVariables(false);
	}

	/**
	 * Create a new RedirectView with the given URL.
	 * <p>
	 *  使用给定的URL创建一个新的RedirectView
	 * 
	 * 
	 * @param url the URL to redirect to
	 * @param contextRelative whether to interpret the given URL as
	 * relative to the current ServletContext
	 * @param http10Compatible whether to stay compatible with HTTP 1.0 clients
	 * @param exposeModelAttributes whether or not model attributes should be
	 * exposed as query parameters
	 */
	public RedirectView(String url, boolean contextRelative, boolean http10Compatible, boolean exposeModelAttributes) {
		super(url);
		this.contextRelative = contextRelative;
		this.http10Compatible = http10Compatible;
		this.exposeModelAttributes = exposeModelAttributes;
		setExposePathVariables(false);
	}


	/**
	 * Set whether to interpret a given URL that starts with a slash ("/")
	 * as relative to the current ServletContext, i.e. as relative to the
	 * web application root.
	 * <p>Default is "false": A URL that starts with a slash will be interpreted
	 * as absolute, i.e. taken as-is. If "true", the context path will be
	 * prepended to the URL in such a case.
	 * <p>
	 *  设置是否解释以斜杠("/")开头的给定URL相对于当前的ServletContext,即相对于Web应用程序根目录<p>默认值为"false"：以斜杠开头的URL将解释为绝对的,即取为原样如果为"tr
	 * ue",则在这种情况下,上下文路径将被添加到URL中。
	 * 
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getContextPath
	 */
	public void setContextRelative(boolean contextRelative) {
		this.contextRelative = contextRelative;
	}

	/**
	 * Set whether to stay compatible with HTTP 1.0 clients.
	 * <p>In the default implementation, this will enforce HTTP status code 302
	 * in any case, i.e. delegate to {@code HttpServletResponse.sendRedirect}.
	 * Turning this off will send HTTP status code 303, which is the correct
	 * code for HTTP 1.1 clients, but not understood by HTTP 1.0 clients.
	 * <p>Many HTTP 1.1 clients treat 302 just like 303, not making any
	 * difference. However, some clients depend on 303 when redirecting
	 * after a POST request; turn this flag off in such a scenario.
	 * <p>
	 * 设置是否与HTTP 10客户端保持兼容<p>在默认实现中,这将在任何情况下执行HTTP状态代码302,即委托给{@code HttpServletResponsesendRedirect}关闭将发送HT
	 * TP状态代码303,这是正确的HTTP 11客户端的代码,但不被HTTP 10客户端所理解许多HTTP 11客户端对302只像303一样,没有任何区别但是,一些客户端在POST请求之后重定向时依赖于30
	 * 3;在这种情况下关闭此标志。
	 * 
	 * 
	 * @see javax.servlet.http.HttpServletResponse#sendRedirect
	 */
	public void setHttp10Compatible(boolean http10Compatible) {
		this.http10Compatible = http10Compatible;
	}

	/**
	 * Set the {@code exposeModelAttributes} flag which denotes whether
	 * or not model attributes should be exposed as HTTP query parameters.
	 * <p>Defaults to {@code true}.
	 * <p>
	 *  设置{@code exposeModelAttributes}标志,表示模型属性是否应作为HTTP查询参数公开<p>默认为{@code true}
	 * 
	 */
	public void setExposeModelAttributes(final boolean exposeModelAttributes) {
		this.exposeModelAttributes = exposeModelAttributes;
	}

	/**
	 * Set the encoding scheme for this view.
	 * <p>Default is the request's encoding scheme
	 * (which is ISO-8859-1 if not specified otherwise).
	 * <p>
	 * 设置此视图的编码方案<p>默认值是请求的编码方案(如果不另外指定,则为ISO-8859-1)
	 * 
	 */
	public void setEncodingScheme(String encodingScheme) {
		this.encodingScheme = encodingScheme;
	}

	/**
	 * Set the status code for this view.
	 * <p>Default is to send 302/303, depending on the value of the
	 * {@link #setHttp10Compatible(boolean) http10Compatible} flag.
	 * <p>
	 *  设置此视图的状态代码<p>默认为发送302/303,具体取决于{@link#setHttp10Compatible(boolean)http10Compatible}标志的值
	 * 
	 */
	public void setStatusCode(HttpStatus statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * Whether to treat the redirect URL as a URI template.
	 * Set this flag to {@code false} if the redirect URL contains open
	 * and close curly braces "{", "}" and you don't want them interpreted
	 * as URI variables.
	 * <p>Defaults to {@code true}.
	 * <p>
	 *  是否将重定向URL视为URI模板如果重定向网址包含打开和关闭的大括号"{","}",并且不希望将其解释为URI变量<p>,请将此标志设置为{@code false}默认为{@code true}
	 * 
	 */
	public void setExpandUriTemplateVariables(boolean expandUriTemplateVariables) {
		this.expandUriTemplateVariables = expandUriTemplateVariables;
	}

	/**
	 * When set to {@code true} the query string of the current URL is appended
	 * and thus propagated through to the redirected URL.
	 * <p>Defaults to {@code false}.
	 * <p>
	 *  当设置为{@code true}时,会添加当前URL的查询字符串,从而传播到重定向的URL <p>默认为{@code false}
	 * 
	 * 
	 * @since 4.1
	 */
	public void setPropagateQueryParams(boolean propagateQueryParams) {
		this.propagateQueryParams = propagateQueryParams;
	}

	/**
	 * Whether to propagate the query params of the current URL.
	 * <p>
	 *  是否传播当前URL的查询参数
	 * 
	 * 
	 * @since 4.1
	 */
	public boolean isPropagateQueryProperties() {
		return this.propagateQueryParams;
	}

	/**
	 * Configure one or more hosts associated with the application.
	 * All other hosts will be considered external hosts.
	 * <p>In effect, this property provides a way turn off encoding via
	 * {@link HttpServletResponse#encodeRedirectURL} for URLs that have a
	 * host and that host is not listed as a known host.
	 * <p>If not set (the default) all URLs are encoded through the response.
	 * <p>
	 * 配置与应用程序关联的一个或多个主机所有其他主机将被视为外部主机<p>实际上,此属性提供了通过{@link HttpServletResponse#encodeRedirectURL}关闭具有主机的URL
	 * 的方式,并且该主机不是列为已知主机<p>如果未设置(默认),所有URL都将通过响应进行编码。
	 * 
	 * 
	 * @param hosts one or more application hosts
	 * @since 4.3
	 */
	public void setHosts(String... hosts) {
		this.hosts = hosts;
	}

	/**
	 * Return the configured application hosts.
	 * <p>
	 *  返回配置的应用程序主机
	 * 
	 * 
	 * @since 4.3
	 */
	public String[] getHosts() {
		return this.hosts;
	}

	/**
	 * Returns "true" indicating this view performs a redirect.
	 * <p>
	 *  返回"true"表示此视图执行重定向
	 * 
	 */
	@Override
	public boolean isRedirectView() {
		return true;
	}

	/**
	 * An ApplicationContext is not strictly required for RedirectView.
	 * <p>
	 *  RedirectView不需要ApplicationContext
	 * 
	 */
	@Override
	protected boolean isContextRequired() {
		return false;
	}


	/**
	 * Convert model to request parameters and redirect to the given URL.
	 * <p>
	 *  转换模型以请求参数并重定向到给定的URL
	 * 
	 * 
	 * @see #appendQueryProperties
	 * @see #sendRedirect
	 */
	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		String targetUrl = createTargetUrl(model, request);
		targetUrl = updateTargetUrl(targetUrl, model, request, response);

		FlashMap flashMap = RequestContextUtils.getOutputFlashMap(request);
		if (!CollectionUtils.isEmpty(flashMap)) {
			UriComponents uriComponents = UriComponentsBuilder.fromUriString(targetUrl).build();
			flashMap.setTargetRequestPath(uriComponents.getPath());
			flashMap.addTargetRequestParams(uriComponents.getQueryParams());
			FlashMapManager flashMapManager = RequestContextUtils.getFlashMapManager(request);
			if (flashMapManager == null) {
				throw new IllegalStateException("FlashMapManager not found despite output FlashMap having been set");
			}
			flashMapManager.saveOutputFlashMap(flashMap, request, response);
		}

		sendRedirect(request, response, targetUrl, this.http10Compatible);
	}

	/**
	 * Create the target URL by checking if the redirect string is a URI template first,
	 * expanding it with the given model, and then optionally appending simple type model
	 * attributes as query String parameters.
	 * <p>
	 * 通过首先检查重定向字符串是否是URI模板来创建目标URL,然后使用给定的模型进行扩展,然后可选地将简单类型模型属性作为查询字符串参数
	 * 
	 */
	protected final String createTargetUrl(Map<String, Object> model, HttpServletRequest request)
			throws UnsupportedEncodingException {

		// Prepare target URL.
		StringBuilder targetUrl = new StringBuilder();
		if (this.contextRelative && getUrl().startsWith("/")) {
			// Do not apply context path to relative URLs.
			targetUrl.append(request.getContextPath());
		}
		targetUrl.append(getUrl());

		String enc = this.encodingScheme;
		if (enc == null) {
			enc = request.getCharacterEncoding();
		}
		if (enc == null) {
			enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
		}

		if (this.expandUriTemplateVariables && StringUtils.hasText(targetUrl)) {
			Map<String, String> variables = getCurrentRequestUriVariables(request);
			targetUrl = replaceUriTemplateVariables(targetUrl.toString(), model, variables, enc);
		}
		if (isPropagateQueryProperties()) {
		 	appendCurrentQueryParams(targetUrl, request);
		}
		if (this.exposeModelAttributes) {
			appendQueryProperties(targetUrl, model, enc);
		}

		return targetUrl.toString();
	}

	/**
	 * Replace URI template variables in the target URL with encoded model
	 * attributes or URI variables from the current request. Model attributes
	 * referenced in the URL are removed from the model.
	 * <p>
	 *  将目标URL中的URI模板变量替换为当前请求中编码的模型属性或URI变量URL中引用的模型属性将从模型中删除
	 * 
	 * 
	 * @param targetUrl the redirect URL
	 * @param model Map that contains model attributes
	 * @param currentUriVariables current request URI variables to use
	 * @param encodingScheme the encoding scheme to use
	 * @throws UnsupportedEncodingException if string encoding failed
	 */
	protected StringBuilder replaceUriTemplateVariables(
			String targetUrl, Map<String, Object> model, Map<String, String> currentUriVariables, String encodingScheme)
			throws UnsupportedEncodingException {

		StringBuilder result = new StringBuilder();
		Matcher matcher = URI_TEMPLATE_VARIABLE_PATTERN.matcher(targetUrl);
		int endLastMatch = 0;
		while (matcher.find()) {
			String name = matcher.group(1);
			Object value = (model.containsKey(name) ? model.remove(name) : currentUriVariables.get(name));
			if (value == null) {
				throw new IllegalArgumentException("Model has no value for key '" + name + "'");
			}
			result.append(targetUrl.substring(endLastMatch, matcher.start()));
			result.append(UriUtils.encodePathSegment(value.toString(), encodingScheme));
			endLastMatch = matcher.end();
		}
		result.append(targetUrl.substring(endLastMatch, targetUrl.length()));
		return result;
	}

	@SuppressWarnings("unchecked")
	private Map<String, String> getCurrentRequestUriVariables(HttpServletRequest request) {
		String name = HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE;
		Map<String, String> uriVars = (Map<String, String>) request.getAttribute(name);
		return (uriVars != null) ? uriVars : Collections.<String, String> emptyMap();
	}

	/**
	 * Append the query string of the current request to the target redirect URL.
	 * <p>
	 *  将当前请求的查询字符串附加到目标重定向URL
	 * 
	 * 
	 * @param targetUrl the StringBuilder to append the properties to
	 * @param request the current request
	 * @since 4.1
	 */
	protected void appendCurrentQueryParams(StringBuilder targetUrl, HttpServletRequest request) {
		String query = request.getQueryString();
		if (StringUtils.hasText(query)) {
			// Extract anchor fragment, if any.
			String fragment = null;
			int anchorIndex = targetUrl.indexOf("#");
			if (anchorIndex > -1) {
				fragment = targetUrl.substring(anchorIndex);
				targetUrl.delete(anchorIndex, targetUrl.length());
			}

			if (targetUrl.toString().indexOf('?') < 0) {
				targetUrl.append('?').append(query);
			}
			else {
				targetUrl.append('&').append(query);
			}
			// Append anchor fragment, if any, to end of URL.
			if (fragment != null) {
				targetUrl.append(fragment);
			}
		}
	}

	/**
	 * Append query properties to the redirect URL.
	 * Stringifies, URL-encodes and formats model attributes as query properties.
	 * <p>
	 *  将查询属性附加到重定向URL将字符串,URL编码和格式化为模型属性作为查询属性
	 * 
	 * 
	 * @param targetUrl the StringBuilder to append the properties to
	 * @param model Map that contains model attributes
	 * @param encodingScheme the encoding scheme to use
	 * @throws UnsupportedEncodingException if string encoding failed
	 * @see #queryProperties
	 */
	@SuppressWarnings("unchecked")
	protected void appendQueryProperties(StringBuilder targetUrl, Map<String, Object> model, String encodingScheme)
			throws UnsupportedEncodingException {

		// Extract anchor fragment, if any.
		String fragment = null;
		int anchorIndex = targetUrl.indexOf("#");
		if (anchorIndex > -1) {
			fragment = targetUrl.substring(anchorIndex);
			targetUrl.delete(anchorIndex, targetUrl.length());
		}

		// If there aren't already some parameters, we need a "?".
		boolean first = (targetUrl.toString().indexOf('?') < 0);
		for (Map.Entry<String, Object> entry : queryProperties(model).entrySet()) {
			Object rawValue = entry.getValue();
			Iterator<Object> valueIter;
			if (rawValue != null && rawValue.getClass().isArray()) {
				valueIter = Arrays.asList(ObjectUtils.toObjectArray(rawValue)).iterator();
			}
			else if (rawValue instanceof Collection) {
				valueIter = ((Collection<Object>) rawValue).iterator();
			}
			else {
				valueIter = Collections.singleton(rawValue).iterator();
			}
			while (valueIter.hasNext()) {
				Object value = valueIter.next();
				if (first) {
					targetUrl.append('?');
					first = false;
				}
				else {
					targetUrl.append('&');
				}
				String encodedKey = urlEncode(entry.getKey(), encodingScheme);
				String encodedValue = (value != null ? urlEncode(value.toString(), encodingScheme) : "");
				targetUrl.append(encodedKey).append('=').append(encodedValue);
			}
		}

		// Append anchor fragment, if any, to end of URL.
		if (fragment != null) {
			targetUrl.append(fragment);
		}
	}

	/**
	 * Determine name-value pairs for query strings, which will be stringified,
	 * URL-encoded and formatted by {@link #appendQueryProperties}.
	 * <p>This implementation filters the model through checking
	 * {@link #isEligibleProperty(String, Object)} for each element,
	 * by default accepting Strings, primitives and primitive wrappers only.
	 * <p>
	 * 确定查询字符串的名称 - 值对,这将通过{@link #appendQueryProperties}进行字符串化,URL编码和格式化<p>此实现通过为每个元素检查{@link #isEligibleProperty(String,Object)}
	 * 过滤模型,默认情况下接受字符串,基元和原始包装器。
	 * 
	 * 
	 * @param model the original model Map
	 * @return the filtered Map of eligible query properties
	 * @see #isEligibleProperty(String, Object)
	 */
	protected Map<String, Object> queryProperties(Map<String, Object> model) {
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		for (Map.Entry<String, Object> entry : model.entrySet()) {
			if (isEligibleProperty(entry.getKey(), entry.getValue())) {
				result.put(entry.getKey(), entry.getValue());
			}
		}
		return result;
	}

	/**
	 * Determine whether the given model element should be exposed
	 * as a query property.
	 * <p>The default implementation considers Strings and primitives
	 * as eligible, and also arrays and Collections/Iterables with
	 * corresponding elements. This can be overridden in subclasses.
	 * <p>
	 *  确定给定的模型元素是否应该作为查询属性公开<p>默认实现将Strings和原语视为符合条件,还可以将数组和Collections / Iterables与相应的元素进行比较可以在子类中被覆盖
	 * 
	 * 
	 * @param key the key of the model element
	 * @param value the value of the model element
	 * @return whether the element is eligible as query property
	 */
	protected boolean isEligibleProperty(String key, Object value) {
		if (value == null) {
			return false;
		}
		if (isEligibleValue(value)) {
			return true;
		}
		if (value.getClass().isArray()) {
			int length = Array.getLength(value);
			if (length == 0) {
				return false;
			}
			for (int i = 0; i < length; i++) {
				Object element = Array.get(value, i);
				if (!isEligibleValue(element)) {
					return false;
				}
			}
			return true;
		}
		if (value instanceof Collection) {
			Collection<?> coll = (Collection<?>) value;
			if (coll.isEmpty()) {
				return false;
			}
			for (Object element : coll) {
				if (!isEligibleValue(element)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Determine whether the given model element value is eligible for exposure.
	 * <p>The default implementation considers primitives, Strings, Numbers, Dates,
	 * URIs, URLs and Locale objects as eligible. This can be overridden in subclasses.
	 * <p>
	 * 确定给定的模型元素值是否符合资格曝光<p>默认实现将基元,字符串,数字,日期,URI,URL和区域设置对象视为合格这可以在子类中被覆盖
	 * 
	 * 
	 * @param value the model element value
	 * @return whether the element value is eligible
	 * @see BeanUtils#isSimpleValueType
	 */
	protected boolean isEligibleValue(Object value) {
		return (value != null && BeanUtils.isSimpleValueType(value.getClass()));
	}

	/**
	 * URL-encode the given input String with the given encoding scheme.
	 * <p>The default implementation uses {@code URLEncoder.encode(input, enc)}.
	 * <p>
	 *  使用给定的编码方案对给定的输入字符串进行URL编码<p>默认实现使用{@code URLEncoderencode(input,enc)}
	 * 
	 * 
	 * @param input the unencoded input String
	 * @param encodingScheme the encoding scheme
	 * @return the encoded output String
	 * @throws UnsupportedEncodingException if thrown by the JDK URLEncoder
	 * @see java.net.URLEncoder#encode(String, String)
	 * @see java.net.URLEncoder#encode(String)
	 */
	protected String urlEncode(String input, String encodingScheme) throws UnsupportedEncodingException {
		return (input != null ? URLEncoder.encode(input, encodingScheme) : null);
	}

	/**
	 * Find the registered {@link RequestDataValueProcessor}, if any, and allow
	 * it to update the redirect target URL.
	 * <p>
	 *  找到注册的{@link RequestDataValueProcessor}(如果有),并允许它更新重定向目标网址
	 * 
	 * 
	 * @param targetUrl the given redirect URL
	 * @return the updated URL or the same as URL as the one passed in
	 */
	protected String updateTargetUrl(String targetUrl, Map<String, Object> model,
			HttpServletRequest request, HttpServletResponse response) {

		WebApplicationContext wac = getWebApplicationContext();
		if (wac == null) {
			wac = RequestContextUtils.findWebApplicationContext(request, getServletContext());
		}

		if (wac != null && wac.containsBean(RequestContextUtils.REQUEST_DATA_VALUE_PROCESSOR_BEAN_NAME)) {
			RequestDataValueProcessor processor = wac.getBean(
					RequestContextUtils.REQUEST_DATA_VALUE_PROCESSOR_BEAN_NAME, RequestDataValueProcessor.class);
			return processor.processUrl(request, targetUrl);
		}

		return targetUrl;
	}

	/**
	 * Send a redirect back to the HTTP client
	 * <p>
	 *  发送重定向回HTTP客户端
	 * 
	 * 
	 * @param request current HTTP request (allows for reacting to request method)
	 * @param response current HTTP response (for sending response headers)
	 * @param targetUrl the target URL to redirect to
	 * @param http10Compatible whether to stay compatible with HTTP 1.0 clients
	 * @throws IOException if thrown by response methods
	 */
	protected void sendRedirect(HttpServletRequest request, HttpServletResponse response,
			String targetUrl, boolean http10Compatible) throws IOException {

		String encodedURL = (isRemoteHost(targetUrl) ? targetUrl : response.encodeRedirectURL(targetUrl));
		if (http10Compatible) {
			HttpStatus attributeStatusCode = (HttpStatus) request.getAttribute(View.RESPONSE_STATUS_ATTRIBUTE);
			if (this.statusCode != null) {
				response.setStatus(this.statusCode.value());
				response.setHeader("Location", encodedURL);
			}
			else if (attributeStatusCode != null) {
				response.setStatus(attributeStatusCode.value());
				response.setHeader("Location", encodedURL);
			}
			else {
				// Send status code 302 by default.
				response.sendRedirect(encodedURL);
			}
		}
		else {
			HttpStatus statusCode = getHttp11StatusCode(request, response, targetUrl);
			response.setStatus(statusCode.value());
			response.setHeader("Location", encodedURL);
		}
	}

	/**
	 * Whether the given targetUrl has a host that is a "foreign" system in which
	 * case {@link HttpServletResponse#encodeRedirectURL} will not be applied.
	 * This method returns {@code true} if the {@link #setHosts(String[])}
	 * property is configured and the target URL has a host that does not match.
	 * <p>
	 * 给定的targetUrl是否具有作为"外部"系统的主机,在这种情况下{@link HttpServletResponse#encodeRedirectURL}将不被应用。
	 * 如果{@link #setHosts(String [])}属性,此方法返回{@code true}已配置,并且目标URL具有不匹配的主机。
	 * 
	 * 
	 * @param targetUrl the target redirect URL
	 * @return {@code true} the target URL has a remote host, {@code false} if it
	 * the URL does not have a host or the "host" property is not configured.
	 * @since 4.3
	 */
	protected boolean isRemoteHost(String targetUrl) {
		if (ObjectUtils.isEmpty(getHosts())) {
			return false;
		}
		String targetHost = UriComponentsBuilder.fromUriString(targetUrl).build().getHost();
		if (StringUtils.isEmpty(targetHost)) {
			return false;
		}
		for (String host : getHosts()) {
			if (targetHost.equals(host)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Determines the status code to use for HTTP 1.1 compatible requests.
	 * <p>The default implementation returns the {@link #setStatusCode(HttpStatus) statusCode}
	 * property if set, or the value of the {@link #RESPONSE_STATUS_ATTRIBUTE} attribute.
	 * If neither are set, it defaults to {@link HttpStatus#SEE_OTHER} (303).
	 * <p>
	 *  确定要用于HTTP 11兼容请求的状态代码<p>默认实现返回{@link #setStatusCode(HttpStatus)statusCode}属性(如果设置)或{@link #RESPONSE_STATUS_ATTRIBUTE}
	 * 属性的值如果没有设置,它默认为{@link HttpStatus#SEE_OTHER}(303)。
	 * 
	 * @param request the request to inspect
	 * @param response the servlet response
	 * @param targetUrl the target URL
	 * @return the response status
	 */
	protected HttpStatus getHttp11StatusCode(
			HttpServletRequest request, HttpServletResponse response, String targetUrl) {

		if (this.statusCode != null) {
			return this.statusCode;
		}
		HttpStatus attributeStatusCode = (HttpStatus) request.getAttribute(View.RESPONSE_STATUS_ATTRIBUTE);
		if (attributeStatusCode != null) {
			return attributeStatusCode;
		}
		return HttpStatus.SEE_OTHER;
	}

}
