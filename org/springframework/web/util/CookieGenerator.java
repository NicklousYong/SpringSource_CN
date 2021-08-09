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

package org.springframework.web.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.util.Assert;

/**
 * Helper class for cookie generation, carrying cookie descriptor settings
 * as bean properties and being able to add and remove cookie to/from a
 * given response.
 *
 * <p>Can serve as base class for components that generate specific cookies,
 * such as CookieLocaleResolver and CookieThemeResolver.
 *
 * <p>
 *  用于生成cookie的Helper类,将cookie描述符设置作为bean属性,并且能够向/从给定响应添加和删除cookie
 * 
 * <p>可以作为生成特定Cookie的组件的基类,例如CookieLocaleResolver和CookieThemeResolver
 * 
 * 
 * @author Juergen Hoeller
 * @since 1.1.4
 * @see #addCookie
 * @see #removeCookie
 * @see org.springframework.web.servlet.i18n.CookieLocaleResolver
 * @see org.springframework.web.servlet.theme.CookieThemeResolver
 */
public class CookieGenerator {

	/**
	 * Default path that cookies will be visible to: "/", i.e. the entire server.
	 * <p>
	 *  Cookie可以看到的默认路径为"/",即整个服务器
	 * 
	 */
	public static final String DEFAULT_COOKIE_PATH = "/";


	protected final Log logger = LogFactory.getLog(getClass());

	private String cookieName;

	private String cookieDomain;

	private String cookiePath = DEFAULT_COOKIE_PATH;

	private Integer cookieMaxAge = null;

	private boolean cookieSecure = false;

	private boolean cookieHttpOnly = false;


	/**
	 * Use the given name for cookies created by this generator.
	 * <p>
	 *  使用此生成器创建的Cookie的给定名称
	 * 
	 * 
	 * @see javax.servlet.http.Cookie#getName()
	 */
	public void setCookieName(String cookieName) {
		this.cookieName = cookieName;
	}

	/**
	 * Return the given name for cookies created by this generator.
	 * <p>
	 *  返回此生成器创建的Cookie的给定名称
	 * 
	 */
	public String getCookieName() {
		return this.cookieName;
	}

	/**
	 * Use the given domain for cookies created by this generator.
	 * The cookie is only visible to servers in this domain.
	 * <p>
	 *  将给定的域用于由此生成器创建的cookie仅对该域中的服务器可见
	 * 
	 * 
	 * @see javax.servlet.http.Cookie#setDomain
	 */
	public void setCookieDomain(String cookieDomain) {
		this.cookieDomain = cookieDomain;
	}

	/**
	 * Return the domain for cookies created by this generator, if any.
	 * <p>
	 *  返回由此生成器创建的Cookie的域(如果有)
	 * 
	 */
	public String getCookieDomain() {
		return this.cookieDomain;
	}

	/**
	 * Use the given path for cookies created by this generator.
	 * The cookie is only visible to URLs in this path and below.
	 * <p>
	 *  使用此生成器创建的Cookie的给定路径Cookie仅在此路径及以下的URL中可见
	 * 
	 * 
	 * @see javax.servlet.http.Cookie#setPath
	 */
	public void setCookiePath(String cookiePath) {
		this.cookiePath = cookiePath;
	}

	/**
	 * Return the path for cookies created by this generator.
	 * <p>
	 *  返回此生成器创建的Cookie的路径
	 * 
	 */
	public String getCookiePath() {
		return this.cookiePath;
	}

	/**
	 * Use the given maximum age (in seconds) for cookies created by this generator.
	 * Useful special value: -1 ... not persistent, deleted when client shuts down
	 * <p>
	 * 使用此生成器创建的Cookie的给定最大年龄(以秒为单位)有用的特殊值：-1不持久,客户机关闭时被删除
	 * 
	 * 
	 * @see javax.servlet.http.Cookie#setMaxAge
	 */
	public void setCookieMaxAge(Integer cookieMaxAge) {
		this.cookieMaxAge = cookieMaxAge;
	}

	/**
	 * Return the maximum age for cookies created by this generator.
	 * <p>
	 *  返回此生成器创建的Cookie的最大年龄
	 * 
	 */
	public Integer getCookieMaxAge() {
		return this.cookieMaxAge;
	}

	/**
	 * Set whether the cookie should only be sent using a secure protocol,
	 * such as HTTPS (SSL). This is an indication to the receiving browser,
	 * not processed by the HTTP server itself. Default is "false".
	 * <p>
	 *  设置是否只应使用安全协议(如HTTPS(SSL))发送cookie这是对接收浏览器的指示,而不是由HTTP服务器本身进行处理默认为"false"
	 * 
	 * 
	 * @see javax.servlet.http.Cookie#setSecure
	 */
	public void setCookieSecure(boolean cookieSecure) {
		this.cookieSecure = cookieSecure;
	}

	/**
	 * Return whether the cookie should only be sent using a secure protocol,
	 * such as HTTPS (SSL).
	 * <p>
	 *  返回cookie是否应该仅使用安全协议发送,如HTTPS(SSL)
	 * 
	 */
	public boolean isCookieSecure() {
		return this.cookieSecure;
	}

	/**
	 * Set whether the cookie is supposed to be marked with the "HttpOnly" attribute.
	 * <p>Note that this feature is only available on Servlet 3.0 and higher.
	 * <p>
	 *  设置cookie是否应该被标记为"HttpOnly"属性<p>请注意,此功能仅适用于Servlet 30及更高版本
	 * 
	 * 
	 * @see javax.servlet.http.Cookie#setHttpOnly
	 */
	public void setCookieHttpOnly(boolean cookieHttpOnly) {
		this.cookieHttpOnly = cookieHttpOnly;
	}

	/**
	 * Return whether the cookie is supposed to be marked with the "HttpOnly" attribute.
	 * <p>
	 *  返回cookie是否应该被标记为"HttpOnly"属性
	 * 
	 */
	public boolean isCookieHttpOnly() {
		return this.cookieHttpOnly;
	}


	/**
	 * Add a cookie with the given value to the response,
	 * using the cookie descriptor settings of this generator.
	 * <p>Delegates to {@link #createCookie} for cookie creation.
	 * <p>
	 * 使用给定值添加一个cookie给响应,使用该生成器的cookie描述符设置<p> {@link #createCookie}的代表创建Cookie
	 * 
	 * 
	 * @param response the HTTP response to add the cookie to
	 * @param cookieValue the value of the cookie to add
	 * @see #setCookieName
	 * @see #setCookieDomain
	 * @see #setCookiePath
	 * @see #setCookieMaxAge
	 */
	public void addCookie(HttpServletResponse response, String cookieValue) {
		Assert.notNull(response, "HttpServletResponse must not be null");
		Cookie cookie = createCookie(cookieValue);
		Integer maxAge = getCookieMaxAge();
		if (maxAge != null) {
			cookie.setMaxAge(maxAge);
		}
		if (isCookieSecure()) {
			cookie.setSecure(true);
		}
		if (isCookieHttpOnly()) {
			cookie.setHttpOnly(true);
		}
		response.addCookie(cookie);
		if (logger.isDebugEnabled()) {
			logger.debug("Added cookie with name [" + getCookieName() + "] and value [" + cookieValue + "]");
		}
	}

	/**
	 * Remove the cookie that this generator describes from the response.
	 * Will generate a cookie with empty value and max age 0.
	 * <p>Delegates to {@link #createCookie} for cookie creation.
	 * <p>
	 *  从响应中删除此生成器描述的cookie将生成一个空值为0的cookie,并且最大年龄为0 <p>委托{@link #createCookie}创建Cookie
	 * 
	 * 
	 * @param response the HTTP response to remove the cookie from
	 * @see #setCookieName
	 * @see #setCookieDomain
	 * @see #setCookiePath
	 */
	public void removeCookie(HttpServletResponse response) {
		Assert.notNull(response, "HttpServletResponse must not be null");
		Cookie cookie = createCookie("");
		cookie.setMaxAge(0);
		if (isCookieSecure()) {
			cookie.setSecure(true);
		}
		if (isCookieHttpOnly()) {
			cookie.setHttpOnly(true);
		}
		response.addCookie(cookie);
		if (logger.isDebugEnabled()) {
			logger.debug("Removed cookie with name [" + getCookieName() + "]");
		}
	}

	/**
	 * Create a cookie with the given value, using the cookie descriptor
	 * settings of this generator (except for "cookieMaxAge").
	 * <p>
	 *  使用该生成器的cookie描述符设置创建一个具有给定值的cookie,但"cookieMaxAge"除外)
	 * 
	 * @param cookieValue the value of the cookie to crate
	 * @return the cookie
	 * @see #setCookieName
	 * @see #setCookieDomain
	 * @see #setCookiePath
	 */
	protected Cookie createCookie(String cookieValue) {
		Cookie cookie = new Cookie(getCookieName(), cookieValue);
		if (getCookieDomain() != null) {
			cookie.setDomain(getCookieDomain());
		}
		cookie.setPath(getCookiePath());
		return cookie;
	}

}
