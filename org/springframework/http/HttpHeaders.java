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

package org.springframework.http;

import java.io.Serializable;
import java.net.URI;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.Assert;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

/**
 * Represents HTTP request and response headers, mapping string header names to a list of string values.
 *
 * <p>In addition to the normal methods defined by {@link Map}, this class offers the following
 * convenience methods:
 * <ul>
 * <li>{@link #getFirst(String)} returns the first value associated with a given header name</li>
 * <li>{@link #add(String, String)} adds a header value to the list of values for a header name</li>
 * <li>{@link #set(String, String)} sets the header value to a single string value</li>
 * </ul>
 *
 * <p>Inspired by {@code com.sun.net.httpserver.Headers}.
 *
 * <p>
 *  表示HTTP请求和响应头,将字符串头名称映射到字符串值列表
 * 
 *  <p>除了{@link Map}定义的常规方法外,此类还提供了以下便利方法：
 * <ul>
 * <li> {@ link #getFirst(String)}返回与给定头名称相关联的第一个值</li> <li> {@ link #add(String,String)}将头值添加到值列表中标题名称</li>
 *  <li> {@ link #set(String,String)}将头值设置为单个字符串值</li>。
 * </ul>
 * 
 *  <p>灵感来自{@code comsunnethttpserverHeaders}
 * 
 * 
 * @author Arjen Poutsma
 * @author Sebastien Deleuze
 * @author Brian Clozel
 * @author Juergen Hoeller
 * @since 3.0
 */
public class HttpHeaders implements MultiValueMap<String, String>, Serializable {

	private static final long serialVersionUID = -8578554704772377436L;

	/**
	 * The HTTP {@code Accept} header field name.
	 * <p>
	 *  HTTP {@code Accept}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-5.3.2">Section 5.3.2 of RFC 7231</a>
	 */
	public static final String ACCEPT = "Accept";
	/**
	 * The HTTP {@code Accept-Charset} header field name.
	 * <p>
	 *  HTTP {@code Accept-Charset}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-5.3.3">Section 5.3.3 of RFC 7231</a>
	 */
	public static final String ACCEPT_CHARSET = "Accept-Charset";
	/**
	 * The HTTP {@code Accept-Encoding} header field name.
	 * <p>
	 *  HTTP {@code Accept-Encoding}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-5.3.4">Section 5.3.4 of RFC 7231</a>
	 */
	public static final String ACCEPT_ENCODING = "Accept-Encoding";
	/**
	 * The HTTP {@code Accept-Language} header field name.
	 * <p>
	 *  HTTP {@code Accept-Language}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-5.3.5">Section 5.3.5 of RFC 7231</a>
	 */
	public static final String ACCEPT_LANGUAGE = "Accept-Language";
	/**
	 * The HTTP {@code Accept-Ranges} header field name.
	 * <p>
	 *  HTTP {@code Accept-Ranges}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7233#section-2.3">Section 5.3.5 of RFC 7233</a>
	 */
	public static final String ACCEPT_RANGES = "Accept-Ranges";
	/**
	 * The CORS {@code Access-Control-Allow-Credentials} response header field name.
	 * <p>
	 *  CORS {@code Access-Control-Allow-Credentials}响应头字段名称
	 * 
	 * 
	 * @see <a href="http://www.w3.org/TR/cors/">CORS W3C recommandation</a>
	 */
	public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
	/**
	 * The CORS {@code Access-Control-Allow-Headers} response header field name.
	 * <p>
	 *  CORS {@code Access-Control-Allow-Headers}响应头字段名称
	 * 
	 * 
	 * @see <a href="http://www.w3.org/TR/cors/">CORS W3C recommandation</a>
	 */
	public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
	/**
	 * The CORS {@code Access-Control-Allow-Methods} response header field name.
	 * <p>
	 * CORS {@code Access-Control-Allow-Methods}响应头字段名称
	 * 
	 * 
	 * @see <a href="http://www.w3.org/TR/cors/">CORS W3C recommandation</a>
	 */
	public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
	/**
	 * The CORS {@code Access-Control-Allow-Origin} response header field name.
	 * <p>
	 *  CORS {@code Access-Control-Allow-Origin}响应头字段名称
	 * 
	 * 
	 * @see <a href="http://www.w3.org/TR/cors/">CORS W3C recommandation</a>
	 */
	public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
	/**
	 * The CORS {@code Access-Control-Expose-Headers} response header field name.
	 * <p>
	 *  CORS {@code Access-Control-Expose-Headers}响应头字段名称
	 * 
	 * 
	 * @see <a href="http://www.w3.org/TR/cors/">CORS W3C recommandation</a>
	 */
	public static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
	/**
	 * The CORS {@code Access-Control-Max-Age} response header field name.
	 * <p>
	 *  CORS {@code Access-Control-Max-Age}响应头字段名称
	 * 
	 * 
	 * @see <a href="http://www.w3.org/TR/cors/">CORS W3C recommandation</a>
	 */
	public static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";
	/**
	 * The CORS {@code Access-Control-Request-Headers} request header field name.
	 * <p>
	 *  CORS {@code Access-Control-Request-Headers}请求头字段名称
	 * 
	 * 
	 * @see <a href="http://www.w3.org/TR/cors/">CORS W3C recommandation</a>
	 */
	public static final String ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";
	/**
	 * The CORS {@code Access-Control-Request-Method} request header field name.
	 * <p>
	 *  CORS {@code Access-Control-Request-Method}请求头字段名称
	 * 
	 * 
	 * @see <a href="http://www.w3.org/TR/cors/">CORS W3C recommandation</a>
	 */
	public static final String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";
	/**
	 * The HTTP {@code Age} header field name.
	 * <p>
	 *  HTTP {@code Age}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7234#section-5.1">Section 5.1 of RFC 7234</a>
	 */
	public static final String AGE = "Age";
	/**
	 * The HTTP {@code Allow} header field name.
	 * <p>
	 *  HTTP {@code Allow}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-7.4.1">Section 7.4.1 of RFC 7231</a>
	 */
	public static final String ALLOW = "Allow";
	/**
	 * The HTTP {@code Authorization} header field name.
	 * <p>
	 *  HTTP {@code授权}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7235#section-4.2">Section 4.2 of RFC 7235</a>
	 */
	public static final String AUTHORIZATION = "Authorization";
	/**
	 * The HTTP {@code Cache-Control} header field name.
	 * <p>
	 *  HTTP {@code Cache-Control}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7234#section-5.2">Section 5.2 of RFC 7234</a>
	 */
	public static final String CACHE_CONTROL = "Cache-Control";
	/**
	 * The HTTP {@code Connection} header field name.
	 * <p>
	 *  HTTP {@code连接}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7230#section-6.1">Section 6.1 of RFC 7230</a>
	 */
	public static final String CONNECTION = "Connection";
	/**
	 * The HTTP {@code Content-Encoding} header field name.
	 * <p>
	 *  HTTP {@code Content-Encoding}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-3.1.2.2">Section 3.1.2.2 of RFC 7231</a>
	 */
	public static final String CONTENT_ENCODING = "Content-Encoding";
	/**
	 * The HTTP {@code Content-Disposition} header field name
	 * <p>
	 *  HTTP {@code Content-Disposition}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc6266">RFC 6266</a>
	 */
	public static final String CONTENT_DISPOSITION = "Content-Disposition";
	/**
	 * The HTTP {@code Content-Language} header field name.
	 * <p>
	 * HTTP {@code Content-Language}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-3.1.3.2">Section 3.1.3.2 of RFC 7231</a>
	 */
	public static final String CONTENT_LANGUAGE = "Content-Language";
	/**
	 * The HTTP {@code Content-Length} header field name.
	 * <p>
	 *  HTTP {@code Content-Length}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7230#section-3.3.2">Section 3.3.2 of RFC 7230</a>
	 */
	public static final String CONTENT_LENGTH = "Content-Length";
	/**
	 * The HTTP {@code Content-Location} header field name.
	 * <p>
	 *  HTTP {@code Content-Location}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-3.1.4.2">Section 3.1.4.2 of RFC 7231</a>
	 */
	public static final String CONTENT_LOCATION = "Content-Location";
	/**
	 * The HTTP {@code Content-Range} header field name.
	 * <p>
	 *  HTTP {@code Content-Range}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7233#section-4.2">Section 4.2 of RFC 7233</a>
	 */
	public static final String CONTENT_RANGE = "Content-Range";
	/**
	 * The HTTP {@code Content-Type} header field name.
	 * <p>
	 *  HTTP {@code Content-Type}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-3.1.1.5">Section 3.1.1.5 of RFC 7231</a>
	 */
	public static final String CONTENT_TYPE = "Content-Type";
	/**
	 * The HTTP {@code Cookie} header field name.
	 * <p>
	 *  HTTP {@code Cookie}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc2109#section-4.3.4">Section 4.3.4 of RFC 2109</a>
	 */
	public static final String COOKIE = "Cookie";
	/**
	 * The HTTP {@code Date} header field name.
	 * <p>
	 *  HTTP {@code Date}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-7.1.1.2">Section 7.1.1.2 of RFC 7231</a>
	 */
	public static final String DATE = "Date";
	/**
	 * The HTTP {@code ETag} header field name.
	 * <p>
	 *  HTTP {@code ETag}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7232#section-2.3">Section 2.3 of RFC 7232</a>
	 */
	public static final String ETAG = "ETag";
	/**
	 * The HTTP {@code Expect} header field name.
	 * <p>
	 *  HTTP {@code Expect}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-5.1.1">Section 5.1.1 of RFC 7231</a>
	 */
	public static final String EXPECT = "Expect";
	/**
	 * The HTTP {@code Expires} header field name.
	 * <p>
	 *  HTTP {@code Expires}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7234#section-5.3">Section 5.3 of RFC 7234</a>
	 */
	public static final String EXPIRES = "Expires";
	/**
	 * The HTTP {@code From} header field name.
	 * <p>
	 *  HTTP {@code From}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-5.5.1">Section 5.5.1 of RFC 7231</a>
	 */
	public static final String FROM = "From";
	/**
	 * The HTTP {@code Host} header field name.
	 * <p>
	 *  HTTP {@code Host}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7230#section-5.4">Section 5.4 of RFC 7230</a>
	 */
	public static final String HOST = "Host";
	/**
	 * The HTTP {@code If-Match} header field name.
	 * <p>
	 *  HTTP {@code If-Match}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7232#section-3.1">Section 3.1 of RFC 7232</a>
	 */
	public static final String IF_MATCH = "If-Match";
	/**
	 * The HTTP {@code If-Modified-Since} header field name.
	 * <p>
	 *  HTTP {@code If-Modified-Since}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7232#section-3.3">Section 3.3 of RFC 7232</a>
	 */
	public static final String IF_MODIFIED_SINCE = "If-Modified-Since";
	/**
	 * The HTTP {@code If-None-Match} header field name.
	 * <p>
	 *  HTTP {@code If-None-Match}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7232#section-3.2">Section 3.2 of RFC 7232</a>
	 */
	public static final String IF_NONE_MATCH = "If-None-Match";
	/**
	 * The HTTP {@code If-Range} header field name.
	 * <p>
	 *  HTTP {@code If-Range}头字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7233#section-3.2">Section 3.2 of RFC 7233</a>
	 */
	public static final String IF_RANGE = "If-Range";
	/**
	 * The HTTP {@code If-Unmodified-Since} header field name.
	 * <p>
	 * HTTP {@code If-Unmodified-Since}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7232#section-3.4">Section 3.4 of RFC 7232</a>
	 */
	public static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
	/**
	 * The HTTP {@code Last-Modified} header field name.
	 * <p>
	 *  HTTP {@code Last-Modified}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7232#section-2.2">Section 2.2 of RFC 7232</a>
	 */
	public static final String LAST_MODIFIED = "Last-Modified";
	/**
	 * The HTTP {@code Link} header field name.
	 * <p>
	 *  HTTP {@code Link}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc5988">RFC 5988</a>
	 */
	public static final String LINK = "Link";
	/**
	 * The HTTP {@code Location} header field name.
	 * <p>
	 *  HTTP {@code位置}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-7.1.2">Section 7.1.2 of RFC 7231</a>
	 */
	public static final String LOCATION = "Location";
	/**
	 * The HTTP {@code Max-Forwards} header field name.
	 * <p>
	 *  HTTP {@code Max-Forwards}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-5.1.2">Section 5.1.2 of RFC 7231</a>
	 */
	public static final String MAX_FORWARDS = "Max-Forwards";
	/**
	 * The HTTP {@code Origin} header field name.
	 * <p>
	 *  HTTP {@code Origin}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc6454">RFC 6454</a>
	 */
	public static final String ORIGIN = "Origin";
	/**
	 * The HTTP {@code Pragma} header field name.
	 * <p>
	 *  HTTP {@code Pragma}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7234#section-5.4">Section 5.4 of RFC 7234</a>
	 */
	public static final String PRAGMA = "Pragma";
	/**
	 * The HTTP {@code Proxy-Authenticate} header field name.
	 * <p>
	 *  HTTP {@code Proxy-Authenticate}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7235#section-4.3">Section 4.3 of RFC 7235</a>
	 */
	public static final String PROXY_AUTHENTICATE = "Proxy-Authenticate";
	/**
	 * The HTTP {@code Proxy-Authorization} header field name.
	 * <p>
	 *  HTTP {@code Proxy-Authorization}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7235#section-4.4">Section 4.4 of RFC 7235</a>
	 */
	public static final String PROXY_AUTHORIZATION = "Proxy-Authorization";
	/**
	 * The HTTP {@code Range} header field name.
	 * <p>
	 *  HTTP {@code范围}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7233#section-3.1">Section 3.1 of RFC 7233</a>
	 */
	public static final String RANGE = "Range";
	/**
	 * The HTTP {@code Referer} header field name.
	 * <p>
	 *  HTTP {@code Referer}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-5.5.2">Section 5.5.2 of RFC 7231</a>
	 */
	public static final String REFERER = "Referer";
	/**
	 * The HTTP {@code Retry-After} header field name.
	 * <p>
	 *  HTTP {@code Retry-After}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-7.1.3">Section 7.1.3 of RFC 7231</a>
	 */
	public static final String RETRY_AFTER = "Retry-After";
	/**
	 * The HTTP {@code Server} header field name.
	 * <p>
	 *  HTTP {@code Server}头域名
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-7.4.2">Section 7.4.2 of RFC 7231</a>
	 */
	public static final String SERVER = "Server";
	/**
	 * The HTTP {@code Set-Cookie} header field name.
	 * <p>
	 *  HTTP {@code Set-Cookie}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc2109#section-4.2.2">Section 4.2.2 of RFC 2109</a>
	 */
	public static final String SET_COOKIE = "Set-Cookie";
	/**
	 * The HTTP {@code Set-Cookie2} header field name.
	 * <p>
	 *  HTTP {@code Set-Cookie2}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc2965">RFC 2965</a>
	 */
	public static final String SET_COOKIE2 = "Set-Cookie2";
	/**
	 * The HTTP {@code TE} header field name.
	 * <p>
	 *  HTTP {@code TE}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7230#section-4.3">Section 4.3 of RFC 7230</a>
	 */
	public static final String TE = "TE";
	/**
	 * The HTTP {@code Trailer} header field name.
	 * <p>
	 * HTTP {@code Trailer}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7230#section-4.4">Section 4.4 of RFC 7230</a>
	 */
	public static final String TRAILER = "Trailer";
	/**
	 * The HTTP {@code Transfer-Encoding} header field name.
	 * <p>
	 *  HTTP {@code Transfer-Encoding}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7230#section-3.3.1">Section 3.3.1 of RFC 7230</a>
	 */
	public static final String TRANSFER_ENCODING = "Transfer-Encoding";
	/**
	 * The HTTP {@code Upgrade} header field name.
	 * <p>
	 *  HTTP {@code Upgrade}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7230#section-6.7">Section 6.7 of RFC 7230</a>
	 */
	public static final String UPGRADE = "Upgrade";
	/**
	 * The HTTP {@code User-Agent} header field name.
	 * <p>
	 *  HTTP {@code User-Agent}头字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-5.5.3">Section 5.5.3 of RFC 7231</a>
	 */
	public static final String USER_AGENT = "User-Agent";
	/**
	 * The HTTP {@code Vary} header field name.
	 * <p>
	 *  HTTP {@code Vary}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-7.1.4">Section 7.1.4 of RFC 7231</a>
	 */
	public static final String VARY = "Vary";
	/**
	 * The HTTP {@code Via} header field name.
	 * <p>
	 *  HTTP {@code Via}头字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7230#section-5.7.1">Section 5.7.1 of RFC 7230</a>
	 */
	public static final String VIA = "Via";
	/**
	 * The HTTP {@code Warning} header field name.
	 * <p>
	 *  HTTP {@code Warning}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7234#section-5.5">Section 5.5 of RFC 7234</a>
	 */
	public static final String WARNING = "Warning";
	/**
	 * The HTTP {@code WWW-Authenticate} header field name.
	 * <p>
	 *  HTTP {@code WWW-Authenticate}标题字段名称
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7235#section-4.1">Section 4.1 of RFC 7235</a>
	 */
	public static final String WWW_AUTHENTICATE = "WWW-Authenticate";

	/**
	 * Date formats as specified in the HTTP RFC
	 * <p>
	 *  HTTP RFC中指定的日期格式
	 * 
	 * 
	 * @see <a href="https://tools.ietf.org/html/rfc7231#section-7.1.1.1">Section 7.1.1.1 of RFC 7231</a>
	 */
	private static final String[] DATE_FORMATS = new String[] {
			"EEE, dd MMM yyyy HH:mm:ss zzz",
			"EEE, dd-MMM-yy HH:mm:ss zzz",
			"EEE MMM dd HH:mm:ss yyyy"
	};

	/**
	 * Pattern matching ETag multiple field values in headers such as "If-Match", "If-None-Match"
	 * <p>
	 *  模式匹配ETag中的多个字段值,如"If-Match","If-None-Match"
	 * 
	 * 
	 * @see <a href="https://tools.ietf.org/html/rfc7232#section-2.3">Section 2.3 of RFC 7232</a>
	 */
	private static final Pattern ETAG_HEADER_VALUE_PATTERN = Pattern.compile("\\*|\\s*((W\\/)?(\"[^\"]*\"))\\s*,?");

	private static TimeZone GMT = TimeZone.getTimeZone("GMT");


	private final Map<String, List<String>> headers;


	/**
	 * Constructs a new, empty instance of the {@code HttpHeaders} object.
	 * <p>
	 *  构造{@code HttpHeaders}对象的新的空的实例
	 * 
	 */
	public HttpHeaders() {
		this(new LinkedCaseInsensitiveMap<List<String>>(8, Locale.ENGLISH), false);
	}

	/**
	 * Private constructor that can create read-only {@code HttpHeader} instances.
	 * <p>
	 *  可以创建只读{@code HttpHeader}实例的私有构造函数
	 * 
	 */
	private HttpHeaders(Map<String, List<String>> headers, boolean readOnly) {
		Assert.notNull(headers, "'headers' must not be null");
		if (readOnly) {
			Map<String, List<String>> map =
					new LinkedCaseInsensitiveMap<List<String>>(headers.size(), Locale.ENGLISH);
			for (Entry<String, List<String>> entry : headers.entrySet()) {
				List<String> values = Collections.unmodifiableList(entry.getValue());
				map.put(entry.getKey(), values);
			}
			this.headers = Collections.unmodifiableMap(map);
		}
		else {
			this.headers = headers;
		}
	}


	/**
	 * Set the list of acceptable {@linkplain MediaType media types},
	 * as specified by the {@code Accept} header.
	 * <p>
	 *  根据{@code Accept}标题指定,设置可接受的{@linkplain MediaType媒体类型}列表
	 * 
	 */
	public void setAccept(List<MediaType> acceptableMediaTypes) {
		set(ACCEPT, MediaType.toString(acceptableMediaTypes));
	}

	/**
	 * Return the list of acceptable {@linkplain MediaType media types},
	 * as specified by the {@code Accept} header.
	 * <p>Returns an empty list when the acceptable media types are unspecified.
	 * <p>
	 * 返回由{@code Accept}标题指定的可接受{@linkplain MediaType媒体类型}的列表<p>当可接受的媒体类型未指定时,返回一个空列表
	 * 
	 */
	public List<MediaType> getAccept() {
		return MediaType.parseMediaTypes(get(ACCEPT));
	}

	/**
	 * Set the (new) value of the {@code Access-Control-Allow-Credentials} response header.
	 * <p>
	 *  设置{@code Access-Control-Allow-Credentials}响应头的(新)值
	 * 
	 */
	public void setAccessControlAllowCredentials(boolean allowCredentials) {
		set(ACCESS_CONTROL_ALLOW_CREDENTIALS, Boolean.toString(allowCredentials));
	}

	/**
	 * Return the value of the {@code Access-Control-Allow-Credentials} response header.
	 * <p>
	 *  返回{@code Access-Control-Allow-Credentials}响应头的值
	 * 
	 */
	public boolean getAccessControlAllowCredentials() {
		return Boolean.parseBoolean(getFirst(ACCESS_CONTROL_ALLOW_CREDENTIALS));
	}

	/**
	 * Set the (new) value of the {@code Access-Control-Allow-Headers} response header.
	 * <p>
	 *  设置{@code Access-Control-Allow-Headers}响应头的(新)值
	 * 
	 */
	public void setAccessControlAllowHeaders(List<String> allowedHeaders) {
		set(ACCESS_CONTROL_ALLOW_HEADERS, toCommaDelimitedString(allowedHeaders));
	}

	/**
	 * Return the value of the {@code Access-Control-Allow-Headers} response header.
	 * <p>
	 *  返回{@code Access-Control-Allow-Headers}响应头的值
	 * 
	 */
	public List<String> getAccessControlAllowHeaders() {
		return getValuesAsList(ACCESS_CONTROL_ALLOW_HEADERS);
	}

	/**
	 * Set the (new) value of the {@code Access-Control-Allow-Methods} response header.
	 * <p>
	 *  设置{@code Access-Control-Allow-Methods}响应头的(新)值
	 * 
	 */
	public void setAccessControlAllowMethods(List<HttpMethod> allowedMethods) {
		set(ACCESS_CONTROL_ALLOW_METHODS, StringUtils.collectionToCommaDelimitedString(allowedMethods));
	}

	/**
	 * Return the value of the {@code Access-Control-Allow-Methods} response header.
	 * <p>
	 *  返回{@code Access-Control-Allow-Methods}响应头的值
	 * 
	 */
	public List<HttpMethod> getAccessControlAllowMethods() {
		List<HttpMethod> result = new ArrayList<HttpMethod>();
		String value = getFirst(ACCESS_CONTROL_ALLOW_METHODS);
		if (value != null) {
			String[] tokens = StringUtils.tokenizeToStringArray(value, ",");
			for (String token : tokens) {
				HttpMethod resolved = HttpMethod.resolve(token);
				if (resolved != null) {
					result.add(resolved);
				}
			}
		}
		return result;
	}

	/**
	 * Set the (new) value of the {@code Access-Control-Allow-Origin} response header.
	 * <p>
	 *  设置{@code Access-Control-Allow-Origin}响应头的(新)值
	 * 
	 */
	public void setAccessControlAllowOrigin(String allowedOrigin) {
		set(ACCESS_CONTROL_ALLOW_ORIGIN, allowedOrigin);
	}

	/**
	 * Return the value of the {@code Access-Control-Allow-Origin} response header.
	 * <p>
	 * 返回{@code Access-Control-Allow-Origin}响应头的值
	 * 
	 */
	public String getAccessControlAllowOrigin() {
		return getFieldValues(ACCESS_CONTROL_ALLOW_ORIGIN);
	}

	/**
	 * Set the (new) value of the {@code Access-Control-Expose-Headers} response header.
	 * <p>
	 *  设置{@code Access-Control-Expose-Headers}响应头的(新)值
	 * 
	 */
	public void setAccessControlExposeHeaders(List<String> exposedHeaders) {
		set(ACCESS_CONTROL_EXPOSE_HEADERS, toCommaDelimitedString(exposedHeaders));
	}

	/**
	 * Return the value of the {@code Access-Control-Expose-Headers} response header.
	 * <p>
	 *  返回{@code Access-Control-Expose-Headers}响应头的值
	 * 
	 */
	public List<String> getAccessControlExposeHeaders() {
		return getValuesAsList(ACCESS_CONTROL_EXPOSE_HEADERS);
	}

	/**
	 * Set the (new) value of the {@code Access-Control-Max-Age} response header.
	 * <p>
	 *  设置{@code Access-Control-Max-Age}响应头的(新)值
	 * 
	 */
	public void setAccessControlMaxAge(long maxAge) {
		set(ACCESS_CONTROL_MAX_AGE, Long.toString(maxAge));
	}

	/**
	 * Return the value of the {@code Access-Control-Max-Age} response header.
	 * <p>Returns -1 when the max age is unknown.
	 * <p>
	 *  返回{@code Access-Control-Max-Age}响应标头的值<p>最大年龄未知时返回-1
	 * 
	 */
	public long getAccessControlMaxAge() {
		String value = getFirst(ACCESS_CONTROL_MAX_AGE);
		return (value != null ? Long.parseLong(value) : -1);
	}

	/**
	 * Set the (new) value of the {@code Access-Control-Request-Headers} request header.
	 * <p>
	 *  设置{@code Access-Control-Request-Headers}请求头的(新)值
	 * 
	 */
	public void setAccessControlRequestHeaders(List<String> requestHeaders) {
		set(ACCESS_CONTROL_REQUEST_HEADERS, toCommaDelimitedString(requestHeaders));
	}

	/**
	 * Return the value of the {@code Access-Control-Request-Headers} request header.
	 * <p>
	 *  返回{@code Access-Control-Request-Headers}请求标头的值
	 * 
	 */
	public List<String> getAccessControlRequestHeaders() {
		return getValuesAsList(ACCESS_CONTROL_REQUEST_HEADERS);
	}

	/**
	 * Set the (new) value of the {@code Access-Control-Request-Method} request header.
	 * <p>
	 *  设置{@code Access-Control-Request-Method}请求头的(新)值
	 * 
	 */
	public void setAccessControlRequestMethod(HttpMethod requestMethod) {
		set(ACCESS_CONTROL_REQUEST_METHOD, requestMethod.name());
	}

	/**
	 * Return the value of the {@code Access-Control-Request-Method} request header.
	 * <p>
	 *  返回{@code Access-Control-Request-Method}请求标头的值
	 * 
	 */
	public HttpMethod getAccessControlRequestMethod() {
		return HttpMethod.resolve(getFirst(ACCESS_CONTROL_REQUEST_METHOD));
	}

	/**
	 * Set the list of acceptable {@linkplain Charset charsets},
	 * as specified by the {@code Accept-Charset} header.
	 * <p>
	 * 根据{@code Accept-Charset}标题指定,设置可接受的{@linkplain Charset charsets}列表
	 * 
	 */
	public void setAcceptCharset(List<Charset> acceptableCharsets) {
		StringBuilder builder = new StringBuilder();
		for (Iterator<Charset> iterator = acceptableCharsets.iterator(); iterator.hasNext();) {
			Charset charset = iterator.next();
			builder.append(charset.name().toLowerCase(Locale.ENGLISH));
			if (iterator.hasNext()) {
				builder.append(", ");
			}
		}
		set(ACCEPT_CHARSET, builder.toString());
	}

	/**
	 * Return the list of acceptable {@linkplain Charset charsets},
	 * as specified by the {@code Accept-Charset} header.
	 * <p>
	 *  返回{@code Accept-Charset}标题指定的可接受的{@linkplain Charset charsets}列表
	 * 
	 */
	public List<Charset> getAcceptCharset() {
		String value = getFirst(ACCEPT_CHARSET);
		if (value != null) {
			String[] tokens = StringUtils.tokenizeToStringArray(value, ",");
			List<Charset> result = new ArrayList<Charset>(tokens.length);
			for (String token : tokens) {
				int paramIdx = token.indexOf(';');
				String charsetName;
				if (paramIdx == -1) {
					charsetName = token;
				}
				else {
					charsetName = token.substring(0, paramIdx);
				}
				if (!charsetName.equals("*")) {
					result.add(Charset.forName(charsetName));
				}
			}
			return result;
		}
		else {
			return Collections.emptyList();
		}
	}

	/**
	 * Set the set of allowed {@link HttpMethod HTTP methods},
	 * as specified by the {@code Allow} header.
	 * <p>
	 *  根据{@code Allow}标题指定,设置允许的{@link HttpMethod HTTP方法}集合
	 * 
	 */
	public void setAllow(Set<HttpMethod> allowedMethods) {
		set(ALLOW, StringUtils.collectionToCommaDelimitedString(allowedMethods));
	}

	/**
	 * Return the set of allowed {@link HttpMethod HTTP methods},
	 * as specified by the {@code Allow} header.
	 * <p>Returns an empty set when the allowed methods are unspecified.
	 * <p>
	 *  返回一组允许的{@link HttpMethod HTTP方法},如{@code Allow}头指定的<p>当允许的方法未指定时返回一个空集
	 * 
	 */
	public Set<HttpMethod> getAllow() {
		String value = getFirst(ALLOW);
		if (!StringUtils.isEmpty(value)) {
			String[] tokens = StringUtils.tokenizeToStringArray(value, ",");
			List<HttpMethod> result = new ArrayList<HttpMethod>(tokens.length);
			for (String token : tokens) {
				HttpMethod resolved = HttpMethod.resolve(token);
				if (resolved != null) {
					result.add(resolved);
				}
			}
			return EnumSet.copyOf(result);
		}
		else {
			return EnumSet.noneOf(HttpMethod.class);
		}
	}

	/**
	 * Set the (new) value of the {@code Cache-Control} header.
	 * <p>
	 *  设置{@code Cache-Control}标题的(新)值
	 * 
	 */
	public void setCacheControl(String cacheControl) {
		set(CACHE_CONTROL, cacheControl);
	}

	/**
	 * Return the value of the {@code Cache-Control} header.
	 * <p>
	 *  返回{@code Cache-Control}标头的值
	 * 
	 */
	public String getCacheControl() {
		return getFieldValues(CACHE_CONTROL);
	}

	/**
	 * Set the (new) value of the {@code Connection} header.
	 * <p>
	 *  设置{@code连接}标题的(新)值
	 * 
	 */
	public void setConnection(String connection) {
		set(CONNECTION, connection);
	}

	/**
	 * Set the (new) value of the {@code Connection} header.
	 * <p>
	 *  设置{@code连接}标题的(新)值
	 * 
	 */
	public void setConnection(List<String> connection) {
		set(CONNECTION, toCommaDelimitedString(connection));
	}

	/**
	 * Return the value of the {@code Connection} header.
	 * <p>
	 *  返回{@code连接}标题的值
	 * 
	 */
	public List<String> getConnection() {
		return getValuesAsList(CONNECTION);
	}

	/**
	 * Set the (new) value of the {@code Content-Disposition} header
	 * for {@code form-data}.
	 * <p>
	 * 为{@code form-data}设置{@code Content-Disposition}标题的(新)值
	 * 
	 * 
	 * @param name the control name
	 * @param filename the filename (may be {@code null})
	 */
	public void setContentDispositionFormData(String name, String filename) {
		setContentDispositionFormData(name, filename, null);
	}

	/**
	 * Set the (new) value of the {@code Content-Disposition} header
	 * for {@code form-data}, optionally encoding the filename using the RFC 5987.
	 * <p>Only the US-ASCII, UTF-8 and ISO-8859-1 charsets are supported.
	 * <p>
	 *  设置{@code form-data}的{@code Content-Disposition}标题的(新)值,可以使用RFC 5987 <p>选择性地对文件名进行编码只有US-ASCII,UTF-8和
	 * ISO-8859-支持1个字符集。
	 * 
	 * 
	 * @param name the control name
	 * @param filename the filename (may be {@code null})
	 * @param charset the charset used for the filename (may be {@code null})
	 * @since 4.3.3
	 * @see #setContentDispositionFormData(String, String)
	 * @see <a href="https://tools.ietf.org/html/rfc7230#section-3.2.4">RFC 7230 Section 3.2.4</a>
	 */
	public void setContentDispositionFormData(String name, String filename, Charset charset) {
		Assert.notNull(name, "'name' must not be null");
		StringBuilder builder = new StringBuilder("form-data; name=\"");
		builder.append(name).append('\"');
		if (filename != null) {
			if (charset == null || charset.name().equals("US-ASCII")) {
				builder.append("; filename=\"");
				builder.append(filename).append('\"');
			}
			else {
				builder.append("; filename*=");
				builder.append(encodeHeaderFieldParam(filename, charset));
			}
		}
		set(CONTENT_DISPOSITION, builder.toString());
	}

	/**
	 * Set the length of the body in bytes, as specified by the
	 * {@code Content-Length} header.
	 * <p>
	 *  按照{@code Content-Length}标题指定的字节设置身体的长度
	 * 
	 */
	public void setContentLength(long contentLength) {
		set(CONTENT_LENGTH, Long.toString(contentLength));
	}

	/**
	 * Return the length of the body in bytes, as specified by the
	 * {@code Content-Length} header.
	 * <p>Returns -1 when the content-length is unknown.
	 * <p>
	 *  返回身体的长度,以{@code Content-Length}标头指定的字节为单位<p>当内容长度未知时返回-1
	 * 
	 */
	public long getContentLength() {
		String value = getFirst(CONTENT_LENGTH);
		return (value != null ? Long.parseLong(value) : -1);
	}

	/**
	 * Set the {@linkplain MediaType media type} of the body,
	 * as specified by the {@code Content-Type} header.
	 * <p>
	 *  根据{@code Content-Type}标题指定,设置正文的{@linkplain MediaType媒体类型}
	 * 
	 */
	public void setContentType(MediaType mediaType) {
		Assert.isTrue(!mediaType.isWildcardType(), "'Content-Type' cannot contain wildcard type '*'");
		Assert.isTrue(!mediaType.isWildcardSubtype(), "'Content-Type' cannot contain wildcard subtype '*'");
		set(CONTENT_TYPE, mediaType.toString());
	}

	/**
	 * Return the {@linkplain MediaType media type} of the body, as specified
	 * by the {@code Content-Type} header.
	 * <p>Returns {@code null} when the content-type is unknown.
	 * <p>
	 *  返回正文的{@linkplain MediaType媒体类型},如{@code Content-Type}标头指定的<p>返回{@code null},当内容类型未知
	 * 
	 */
	public MediaType getContentType() {
		String value = getFirst(CONTENT_TYPE);
		return (StringUtils.hasLength(value) ? MediaType.parseMediaType(value) : null);
	}

	/**
	 * Set the date and time at which the message was created, as specified
	 * by the {@code Date} header.
	 * <p>The date should be specified as the number of milliseconds since
	 * January 1, 1970 GMT.
	 * <p>
	 * 设置消息创建的日期和时间,由{@code Date}头<p>指定。日期应指定为自1970年1月1日GMT以来的毫秒数
	 * 
	 */
	public void setDate(long date) {
		setDate(DATE, date);
	}

	/**
	 * Return the date and time at which the message was created, as specified
	 * by the {@code Date} header.
	 * <p>The date is returned as the number of milliseconds since
	 * January 1, 1970 GMT. Returns -1 when the date is unknown.
	 * <p>
	 *  返回消息创建的日期和时间,由{@code Date}头<p>指定的日期返回为1970年1月1日以来的毫秒数GMT日期未知时返回-1
	 * 
	 * 
	 * @throws IllegalArgumentException if the value can't be converted to a date
	 */
	public long getDate() {
		return getFirstDate(DATE);
	}

	/**
	 * Set the (new) entity tag of the body, as specified by the {@code ETag} header.
	 * <p>
	 *  根据{@code ETag}标题指定,设置正文的(新)实体标签
	 * 
	 */
	public void setETag(String eTag) {
		if (eTag != null) {
			Assert.isTrue(eTag.startsWith("\"") || eTag.startsWith("W/"),
					"Invalid eTag, does not start with W/ or \"");
			Assert.isTrue(eTag.endsWith("\""), "Invalid eTag, does not end with \"");
		}
		set(ETAG, eTag);
	}

	/**
	 * Return the entity tag of the body, as specified by the {@code ETag} header.
	 * <p>
	 *  返回主体的实体标记,如{@code ETag}标题所指定
	 * 
	 */
	public String getETag() {
		return getFirst(ETAG);
	}

	/**
	 * Set the date and time at which the message is no longer valid,
	 * as specified by the {@code Expires} header.
	 * <p>The date should be specified as the number of milliseconds since
	 * January 1, 1970 GMT.
	 * <p>
	 *  设置消息不再有效的日期和时间,如{@code Expires}头<p>所指定的日期应指定为自1970年1月1日GMT以来的毫秒数
	 * 
	 */
	public void setExpires(long expires) {
		setDate(EXPIRES, expires);
	}

	/**
	 * Return the date and time at which the message is no longer valid,
	 * as specified by the {@code Expires} header.
	 * <p>The date is returned as the number of milliseconds since
	 * January 1, 1970 GMT. Returns -1 when the date is unknown.
	 * <p>
	 * 返回消息不再有效的日期和时间,由{@code Expires}头<p>指定的日期返回为自1970年1月1日以来的毫秒数GMT日期未知时返回-1
	 * 
	 */
	public long getExpires() {
		return getFirstDate(EXPIRES, false);
	}

	/**
	 * Set the (new) value of the {@code If-Match} header.
	 * <p>
	 *  设置{@code If-Match}标题的(新)值
	 * 
	 * 
	 * @since 4.3
	 */
	public void setIfMatch(String ifMatch) {
		set(IF_MATCH, ifMatch);
	}

	/**
	 * Set the (new) value of the {@code If-Match} header.
	 * <p>
	 *  设置{@code If-Match}标题的(新)值
	 * 
	 * 
	 * @since 4.3
	 */
	public void setIfMatch(List<String> ifMatchList) {
		set(IF_MATCH, toCommaDelimitedString(ifMatchList));
	}

	/**
	 * Return the value of the {@code If-Match} header.
	 * <p>
	 *  返回{@code If-Match}标题的值
	 * 
	 * 
	 * @since 4.3
	 */
	public List<String> getIfMatch() {
		return getETagValuesAsList(IF_MATCH);
	}

	/**
	 * Set the (new) value of the {@code If-Modified-Since} header.
	 * <p>The date should be specified as the number of milliseconds since
	 * January 1, 1970 GMT.
	 * <p>
	 *  设置{@code If-Modified-Since}标题的(新)值<p>日期应指定为自1970年1月1日GMT以来的毫秒数
	 * 
	 */
	public void setIfModifiedSince(long ifModifiedSince) {
		setDate(IF_MODIFIED_SINCE, ifModifiedSince);
	}

	/**
	 * Return the value of the {@code If-Modified-Since} header.
	 * <p>The date is returned as the number of milliseconds since
	 * January 1, 1970 GMT. Returns -1 when the date is unknown.
	 * <p>
	 *  返回{@code If-Modified-Since}标题的值<p>从1970年1月1日以来的毫秒数返回日期GMT返回-1当日期不明时
	 * 
	 */
	public long getIfModifiedSince() {
		return getFirstDate(IF_MODIFIED_SINCE, false);
	}

	/**
	 * Set the (new) value of the {@code If-None-Match} header.
	 * <p>
	 *  设置{@code If-None-Match}标题的(新)值
	 * 
	 */
	public void setIfNoneMatch(String ifNoneMatch) {
		set(IF_NONE_MATCH, ifNoneMatch);
	}

	/**
	 * Set the (new) values of the {@code If-None-Match} header.
	 * <p>
	 * 设置{@code If-None-Match}标题的(新)值
	 * 
	 */
	public void setIfNoneMatch(List<String> ifNoneMatchList) {
		set(IF_NONE_MATCH, toCommaDelimitedString(ifNoneMatchList));
	}

	/**
	 * Return the value of the {@code If-None-Match} header.
	 * <p>
	 *  返回{@code If-None-Match}标题的值
	 * 
	 */
	public List<String> getIfNoneMatch() {
		return getETagValuesAsList(IF_NONE_MATCH);
	}

	/**
	 * Set the (new) value of the {@code If-Unmodified-Since} header.
	 * <p>The date should be specified as the number of milliseconds since
	 * January 1, 1970 GMT.
	 * <p>
	 *  设置{@code If-Unmodified-Since}标题的(新)值<p>日期应指定为自1970年1月1日GMT以来的毫秒数
	 * 
	 * 
	 * @since 4.3
	 */
	public void setIfUnmodifiedSince(long ifUnmodifiedSince) {
		setDate(IF_UNMODIFIED_SINCE, ifUnmodifiedSince);
	}

	/**
	 * Return the value of the {@code If-Unmodified-Since} header.
	 * <p>The date is returned as the number of milliseconds since
	 * January 1, 1970 GMT. Returns -1 when the date is unknown.
	 * <p>
	 *  返回{@code If-Unmodified-Since}标题的值<p>从1970年1月1日以来的毫秒数返回日期GMT返回-1,当日期未知
	 * 
	 * 
	 * @since 4.3
	 */
	public long getIfUnmodifiedSince() {
		return getFirstDate(IF_UNMODIFIED_SINCE, false);
	}

	/**
	 * Set the time the resource was last changed, as specified by the
	 * {@code Last-Modified} header.
	 * <p>The date should be specified as the number of milliseconds since
	 * January 1, 1970 GMT.
	 * <p>
	 *  设置最后更改资源的时间,如{@code Last-Modified}标题<p>所规定的日期应指定为自1970年1月1日GMT以来的毫秒数
	 * 
	 */
	public void setLastModified(long lastModified) {
		setDate(LAST_MODIFIED, lastModified);
	}

	/**
	 * Return the time the resource was last changed, as specified by the
	 * {@code Last-Modified} header.
	 * <p>The date is returned as the number of milliseconds since
	 * January 1, 1970 GMT. Returns -1 when the date is unknown.
	 * <p>
	 * 返回最后更改资源的时间,如{@code Last-Modified}头文件所指定的<p>日期返回为1970年1月1日以来的毫秒数GMT日期未知时返回-1
	 * 
	 */
	public long getLastModified() {
		return getFirstDate(LAST_MODIFIED, false);
	}

	/**
	 * Set the (new) location of a resource,
	 * as specified by the {@code Location} header.
	 * <p>
	 *  设置资源的(新)位置,由{@code位置}标题指定
	 * 
	 */
	public void setLocation(URI location) {
		set(LOCATION, location.toASCIIString());
	}

	/**
	 * Return the (new) location of a resource
	 * as specified by the {@code Location} header.
	 * <p>Returns {@code null} when the location is unknown.
	 * <p>
	 *  返回由{@code位置}标题指定的资源的(新)位置<p>当位置未知时返回{@code null}
	 * 
	 */
	public URI getLocation() {
		String value = getFirst(LOCATION);
		return (value != null ? URI.create(value) : null);
	}

	/**
	 * Set the (new) value of the {@code Origin} header.
	 * <p>
	 *  设置{@code Origin}标题的(新)值
	 * 
	 */
	public void setOrigin(String origin) {
		set(ORIGIN, origin);
	}

	/**
	 * Return the value of the {@code Origin} header.
	 * <p>
	 *  返回{@code Origin}标题的值
	 * 
	 */
	public String getOrigin() {
		return getFirst(ORIGIN);
	}

	/**
	 * Set the (new) value of the {@code Pragma} header.
	 * <p>
	 *  设置{@code Pragma}标题的(新)值
	 * 
	 */
	public void setPragma(String pragma) {
		set(PRAGMA, pragma);
	}

	/**
	 * Return the value of the {@code Pragma} header.
	 * <p>
	 *  返回{@code Pragma}标题的值
	 * 
	 */
	public String getPragma() {
		return getFirst(PRAGMA);
	}

	/**
	 * Sets the (new) value of the {@code Range} header.
	 * <p>
	 *  设置{@code Range}标题的(新)值
	 * 
	 */
	public void setRange(List<HttpRange> ranges) {
		String value = HttpRange.toString(ranges);
		set(RANGE, value);
	}

	/**
	 * Return the value of the {@code Range} header.
	 * <p>Returns an empty list when the range is unknown.
	 * <p>
	 *  返回{@code Range}标题的值<p>当范围未知时返回一个空列表
	 * 
	 */
	public List<HttpRange> getRange() {
		String value = getFirst(RANGE);
		return HttpRange.parseRanges(value);
	}

	/**
	 * Set the (new) value of the {@code Upgrade} header.
	 * <p>
	 * 设置{@code Upgrade}标题的(新)值
	 * 
	 */
	public void setUpgrade(String upgrade) {
		set(UPGRADE, upgrade);
	}

	/**
	 * Return the value of the {@code Upgrade} header.
	 * <p>
	 *  返回{@code Upgrade}标题的值
	 * 
	 */
	public String getUpgrade() {
		return getFirst(UPGRADE);
	}

	/**
	 * Set the request header names (e.g. "Accept-Language") for which the
	 * response is subject to content negotiation and variances based on the
	 * value of those request headers.
	 * <p>
	 *  根据这些请求标头的值设置响应需要进行内容协商的请求头名称(例如"Accept-Language")
	 * 
	 * 
	 * @param requestHeaders the request header names
	 * @since 4.3
	 */
	public void setVary(List<String> requestHeaders) {
		set(VARY, toCommaDelimitedString(requestHeaders));
	}

	/**
	 * Return the request header names subject to content negotiation.
	 * <p>
	 *  返回请求头名称进行内容协商
	 * 
	 * 
	 * @since 4.3
	 */
	public List<String> getVary() {
		return getValuesAsList(VARY);
	}

	/**
	 * Set the given date under the given header name after formatting it as a string
	 * using the pattern {@code "EEE, dd MMM yyyy HH:mm:ss zzz"}. The equivalent of
	 * {@link #set(String, String)} but for date headers.
	 * <p>
	 *  使用{@code"EEE,dd MMM yyyy HH：mm：ss zzz"}将其格式化为字符串后,将给定日期设置为{@link #set(String,String)}但相当于日期标题
	 * 
	 * 
	 * @since 3.2.4
	 */
	public void setDate(String headerName, long date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMATS[0], Locale.US);
		dateFormat.setTimeZone(GMT);
		set(headerName, dateFormat.format(new Date(date)));
	}

	/**
	 * Parse the first header value for the given header name as a date,
	 * return -1 if there is no value, or raise {@link IllegalArgumentException}
	 * if the value cannot be parsed as a date.
	 * <p>
	 *  将给定标题名称的第一个标头值解析为日期,如果没有值,则返回-1,如果该值不能被解析为日期,则引发{@link IllegalArgumentException}
	 * 
	 * 
	 * @param headerName the header name
	 * @return the parsed date header, or -1 if none
	 * @since 3.2.4
	 */
	public long getFirstDate(String headerName) {
		return getFirstDate(headerName, true);
	}

	/**
	 * Parse the first header value for the given header name as a date,
	 * return -1 if there is no value or also in case of an invalid value
	 * (if {@code rejectInvalid=false}), or raise {@link IllegalArgumentException}
	 * if the value cannot be parsed as a date.
	 * <p>
	 * 将给定头名称的第一个头值解析为日期,如果没有值返回-1,或者在无效值的情况下返回-1(如果{@code rejectInvalid = false}),或者如果值不能被解析为日期
	 * 
	 * 
	 * @param headerName the header name
	 * @param rejectInvalid whether to reject invalid values with an
	 * {@link IllegalArgumentException} ({@code true}) or rather return -1
	 * in that case ({@code false})
	 * @return the parsed date header, or -1 if none (or invalid)
 	 */
	private long getFirstDate(String headerName, boolean rejectInvalid) {
		String headerValue = getFirst(headerName);
		if (headerValue == null) {
			// No header value sent at all
			return -1;
		}
		if (headerValue.length() >= 3) {
			// Short "0" or "-1" like values are never valid HTTP date headers...
			// Let's only bother with SimpleDateFormat parsing for long enough values.
			for (String dateFormat : DATE_FORMATS) {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
				simpleDateFormat.setTimeZone(GMT);
				try {
					return simpleDateFormat.parse(headerValue).getTime();
				}
				catch (ParseException ex) {
					// ignore
				}
			}
		}
		if (rejectInvalid) {
			throw new IllegalArgumentException("Cannot parse date value \"" + headerValue +
					"\" for \"" + headerName + "\" header");
		}
		return -1;
	}

	/**
	 * Return all values of a given header name,
	 * even if this header is set multiple times.
	 * <p>
	 *  返回给定标题名的所有值,即使此标题多次设置
	 * 
	 * 
	 * @param headerName the header name
	 * @return all associated values
	 * @since 4.3
	 */
	public List<String> getValuesAsList(String headerName) {
		List<String> values = get(headerName);
		if (values != null) {
			List<String> result = new ArrayList<String>();
			for (String value : values) {
				if (value != null) {
					String[] tokens = StringUtils.tokenizeToStringArray(value, ",");
					for (String token : tokens) {
						result.add(token);
					}
				}
			}
			return result;
		}
		return Collections.emptyList();
	}

	/**
	 * Retrieve a combined result from the field values of the ETag header.
	 * <p>
	 *  从ETag标题的字段值中检索组合结果
	 * 
	 * 
	 * @param headerName the header name
	 * @return the combined result
	 * @since 4.3
	 */
	protected List<String> getETagValuesAsList(String headerName) {
		List<String> values = get(headerName);
		if (values != null) {
			List<String> result = new ArrayList<String>();
			for (String value : values) {
				if (value != null) {
					Matcher matcher = ETAG_HEADER_VALUE_PATTERN.matcher(value);
					while (matcher.find()) {
						if ("*".equals(matcher.group())) {
							result.add(matcher.group());
						}
						else {
							result.add(matcher.group(1));
						}
					}
					if (result.isEmpty()) {
						throw new IllegalArgumentException(
								"Could not parse header '" + headerName + "' with value '" + value + "'");
					}
				}
			}
			return result;
		}
		return Collections.emptyList();
	}

	/**
	 * Retrieve a combined result from the field values of multi-valued headers.
	 * <p>
	 *  从多值头的字段值检索组合结果
	 * 
	 * 
	 * @param headerName the header name
	 * @return the combined result
	 * @since 4.3
	 */
	protected String getFieldValues(String headerName) {
		List<String> headerValues = get(headerName);
		return (headerValues != null ? toCommaDelimitedString(headerValues) : null);
	}

	/**
	 * Turn the given list of header values into a comma-delimited result.
	 * <p>
	 *  将标题值的给定列表转换为逗号分隔的结果
	 * 
	 * 
	 * @param headerValues the list of header values
	 * @return a combined result with comma delimitation
	 */
	protected String toCommaDelimitedString(List<String> headerValues) {
		StringBuilder builder = new StringBuilder();
		for (Iterator<String> it = headerValues.iterator(); it.hasNext(); ) {
			String val = it.next();
			builder.append(val);
			if (it.hasNext()) {
				builder.append(", ");
			}
		}
		return builder.toString();
	}


	// MultiValueMap implementation

	/**
	 * Return the first header value for the given header name, if any.
	 * <p>
	 *  返回给定头名称的第一个头值,如果有的话
	 * 
	 * 
	 * @param headerName the header name
	 * @return the first header value, or {@code null} if none
	 */
	@Override
	public String getFirst(String headerName) {
		List<String> headerValues = this.headers.get(headerName);
		return (headerValues != null ? headerValues.get(0) : null);
	}

	/**
	 * Add the given, single header value under the given name.
	 * <p>
	 *  在给定的名称下添加给定的单个头值
	 * 
	 * 
	 * @param headerName the header name
	 * @param headerValue the header value
	 * @throws UnsupportedOperationException if adding headers is not supported
	 * @see #put(String, List)
	 * @see #set(String, String)
	 */
	@Override
	public void add(String headerName, String headerValue) {
		List<String> headerValues = this.headers.get(headerName);
		if (headerValues == null) {
			headerValues = new LinkedList<String>();
			this.headers.put(headerName, headerValues);
		}
		headerValues.add(headerValue);
	}

	/**
	 * Set the given, single header value under the given name.
	 * <p>
	 *  在给定的名称下设置给定的单个头值
	 * 
	 * 
	 * @param headerName the header name
	 * @param headerValue the header value
	 * @throws UnsupportedOperationException if adding headers is not supported
	 * @see #put(String, List)
	 * @see #add(String, String)
	 */
	@Override
	public void set(String headerName, String headerValue) {
		List<String> headerValues = new LinkedList<String>();
		headerValues.add(headerValue);
		this.headers.put(headerName, headerValues);
	}

	@Override
	public void setAll(Map<String, String> values) {
		for (Entry<String, String> entry : values.entrySet()) {
			set(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public Map<String, String> toSingleValueMap() {
		LinkedHashMap<String, String> singleValueMap = new LinkedHashMap<String,String>(this.headers.size());
		for (Entry<String, List<String>> entry : this.headers.entrySet()) {
			singleValueMap.put(entry.getKey(), entry.getValue().get(0));
		}
		return singleValueMap;
	}


	// Map implementation

	@Override
	public int size() {
		return this.headers.size();
	}

	@Override
	public boolean isEmpty() {
		return this.headers.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return this.headers.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return this.headers.containsValue(value);
	}

	@Override
	public List<String> get(Object key) {
		return this.headers.get(key);
	}

	@Override
	public List<String> put(String key, List<String> value) {
		return this.headers.put(key, value);
	}

	@Override
	public List<String> remove(Object key) {
		return this.headers.remove(key);
	}

	@Override
	public void putAll(Map<? extends String, ? extends List<String>> map) {
		this.headers.putAll(map);
	}

	@Override
	public void clear() {
		this.headers.clear();
	}

	@Override
	public Set<String> keySet() {
		return this.headers.keySet();
	}

	@Override
	public Collection<List<String>> values() {
		return this.headers.values();
	}

	@Override
	public Set<Entry<String, List<String>>> entrySet() {
		return this.headers.entrySet();
	}


	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof HttpHeaders)) {
			return false;
		}
		HttpHeaders otherHeaders = (HttpHeaders) other;
		return this.headers.equals(otherHeaders.headers);
	}

	@Override
	public int hashCode() {
		return this.headers.hashCode();
	}

	@Override
	public String toString() {
		return this.headers.toString();
	}


	/**
	 * Return a {@code HttpHeaders} object that can only be read, not written to.
	 * <p>
	 * 返回一个只能被读取而不写入的{@code HttpHeaders}对象
	 * 
	 */
	public static HttpHeaders readOnlyHttpHeaders(HttpHeaders headers) {
		return new HttpHeaders(headers, true);
	}

	/**
	 * Encode the given header field param as describe in RFC 5987.
	 * <p>
	 *  对RFC 5987中的描述编码给定的头域参数
	 * 
	 * @param input the header field param
	 * @param charset the charset of the header field param string
	 * @return the encoded header field param
	 * @see <a href="https://tools.ietf.org/html/rfc5987">RFC 5987</a>
	 */
	static String encodeHeaderFieldParam(String input, Charset charset) {
		Assert.notNull(input, "Input String should not be null");
		Assert.notNull(charset, "Charset should not be null");
		if (charset.name().equals("US-ASCII")) {
			return input;
		}
		Assert.isTrue(charset.name().equals("UTF-8") || charset.name().equals("ISO-8859-1"),
				"Charset should be UTF-8 or ISO-8859-1");
		byte[] source = input.getBytes(charset);
		int len = source.length;
		StringBuilder sb = new StringBuilder(len << 1);
		sb.append(charset.name());
		sb.append("''");
		for (byte b : source) {
			if (isRFC5987AttrChar(b)) {
				sb.append((char) b);
			}
			else {
				sb.append('%');
				char hex1 = Character.toUpperCase(Character.forDigit((b >> 4) & 0xF, 16));
				char hex2 = Character.toUpperCase(Character.forDigit(b & 0xF, 16));
				sb.append(hex1);
				sb.append(hex2);
			}
		}
		return sb.toString();
	}

	private static boolean isRFC5987AttrChar(byte c) {
		return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') ||
				c == '!' || c == '#' || c == '$' || c == '&' || c == '+' || c == '-' ||
				c == '.' || c == '^' || c == '_' || c == '`' || c == '|' || c == '~';
	}

}
