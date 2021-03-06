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

package org.springframework.web.servlet;

import java.util.HashMap;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * A FlashMap provides a way for one request to store attributes intended for
 * use in another. This is most commonly needed when redirecting from one URL
 * to another -- e.g. the Post/Redirect/Get pattern. A FlashMap is saved before
 * the redirect (typically in the session) and is made available after the
 * redirect and removed immediately.
 *
 * <p>A FlashMap can be set up with a request path and request parameters to
 * help identify the target request. Without this information, a FlashMap is
 * made available to the next request, which may or may not be the intended
 * recipient. On a redirect, the target URL is known and a FlashMap can be
 * updated with that information. This is done automatically when the
 * {@code org.springframework.web.servlet.view.RedirectView} is used.
 *
 * <p>Note: annotated controllers will usually not use FlashMap directly.
 * See {@code org.springframework.web.servlet.mvc.support.RedirectAttributes}
 * for an overview of using flash attributes in annotated controllers.
 *
 * <p>
 * FlashMap提供了一种存储要用于另一个的属性的请求的方法。
 * 当从一个URL重定向到另一个URL时最常用到 - 例如Post / Redirect / Get模式FlashMap在重定向之前保存(通常在会话中),并在重定向后立即可用,并立即删除。
 * 
 *  <p>可以使用请求路径和请求参数设置FlashMap以帮助识别目标请求。
 * 没有此信息,FlashMap将可用于下一个请求,这可能是或可能不是预期的收件人在重定向上,目标网址是已知的,并且可以使用该信息更新FlashMap。
 * 当使用{@code orgspringframeworkwebservletviewRedirectView}时,这是自动完成的。
 * 
 * 注意：注释的控制器通常不会直接使用FlashMap参见{@code orgspringframeworkwebservletmvcsupportRedirectAttributes},了解在注释控制器中
 * 使用Flash属性的概述。
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 3.1
 * @see FlashMapManager
 */
@SuppressWarnings("serial")
public final class FlashMap extends HashMap<String, Object> implements Comparable<FlashMap> {

	private String targetRequestPath;

	private final MultiValueMap<String, String> targetRequestParams = new LinkedMultiValueMap<String, String>(4);

	private long expirationTime = -1;


	/**
	 * Provide a URL path to help identify the target request for this FlashMap.
	 * <p>The path may be absolute (e.g. "/application/resource") or relative to the
	 * current request (e.g. "../resource").
	 * <p>
	 *  提供URL路径来帮助识别此FlashMap的目标请求<p>该路径可能是绝对的(例如"/ application / resource")或相对于当前请求(例如"/ resource")
	 * 
	 */
	public void setTargetRequestPath(String path) {
		this.targetRequestPath = path;
	}

	/**
	 * Return the target URL path (or {@code null} if none specified).
	 * <p>
	 *  返回目标网址路径(或{@code null},如果没有指定)
	 * 
	 */
	public String getTargetRequestPath() {
		return this.targetRequestPath;
	}

	/**
	 * Provide request parameters identifying the request for this FlashMap.
	 * <p>
	 *  提供标识此FlashMap请求的请求参数
	 * 
	 * 
	 * @param params a Map with the names and values of expected parameters
	 */
	public FlashMap addTargetRequestParams(MultiValueMap<String, String> params) {
		if (params != null) {
			for (String key : params.keySet()) {
				for (String value : params.get(key)) {
					addTargetRequestParam(key, value);
				}
			}
		}
		return this;
	}

	/**
	 * Provide a request parameter identifying the request for this FlashMap.
	 * <p>
	 *  提供标识此FlashMap请求的请求参数
	 * 
	 * 
	 * @param name the expected parameter name (skipped if empty or {@code null})
	 * @param value the expected value (skipped if empty or {@code null})
	 */
	public FlashMap addTargetRequestParam(String name, String value) {
		if (StringUtils.hasText(name) && StringUtils.hasText(value)) {
			this.targetRequestParams.add(name, value);
		}
		return this;
	}

	/**
	 * Return the parameters identifying the target request, or an empty map.
	 * <p>
	 *  返回标识目标请求的参数,或返回空的地图
	 * 
	 */
	public MultiValueMap<String, String> getTargetRequestParams() {
		return this.targetRequestParams;
	}

	/**
	 * Start the expiration period for this instance.
	 * <p>
	 *  开始此实例的到期期限
	 * 
	 * 
	 * @param timeToLive the number of seconds before expiration
	 */
	public void startExpirationPeriod(int timeToLive) {
		this.expirationTime = System.currentTimeMillis() + timeToLive * 1000;
	}

	/**
	 * Set the expiration time for the FlashMap. This is provided for serialization
	 * purposes but can also be used instead {@link #startExpirationPeriod(int)}.
	 * <p>
	 * 设置FlashMap的到期时间这是提供用于序列化的目的,但也可以使用{@link #startExpirationPeriod(int)}
	 * 
	 * 
	 * @since 4.2
	 */
	public void setExpirationTime(long expirationTime) {
		this.expirationTime = expirationTime;
	}

	/**
	 * Return the expiration time for the FlashMap or -1 if the expiration
	 * period has not started.
	 * <p>
	 *  返回FlashMap的到期时间,如果有效期尚未开始,则返回-1
	 * 
	 * 
	 * @since 4.2
	 */
	public long getExpirationTime() {
		return this.expirationTime;
	}

	/**
	 * Return whether this instance has expired depending on the amount of
	 * elapsed time since the call to {@link #startExpirationPeriod}.
	 * <p>
	 *  根据从{@link #startExpirationPeriod}的调用以来经过的时间量,返回此实例是否已过期
	 * 
	 */
	public boolean isExpired() {
		return (this.expirationTime != -1 && System.currentTimeMillis() > this.expirationTime);
	}


	/**
	 * Compare two FlashMaps and prefer the one that specifies a target URL
	 * path or has more target URL parameters. Before comparing FlashMap
	 * instances ensure that they match a given request.
	 * <p>
	 *  比较两个FlashMaps,并选择指定目标URL路径或具有更多目标URL参数的FlashMaps。比较FlashMap实例之前确保它们与给定请求匹配
	 */
	@Override
	public int compareTo(FlashMap other) {
		int thisUrlPath = (this.targetRequestPath != null ? 1 : 0);
		int otherUrlPath = (other.targetRequestPath != null ? 1 : 0);
		if (thisUrlPath != otherUrlPath) {
			return otherUrlPath - thisUrlPath;
		}
		else {
			return other.targetRequestParams.size() - this.targetRequestParams.size();
		}
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FlashMap)) {
			return false;
		}
		FlashMap otherFlashMap = (FlashMap) other;
		return (super.equals(otherFlashMap) &&
				ObjectUtils.nullSafeEquals(this.targetRequestPath, otherFlashMap.targetRequestPath) &&
				this.targetRequestParams.equals(otherFlashMap.targetRequestParams));
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + ObjectUtils.nullSafeHashCode(this.targetRequestPath);
		result = 31 * result + this.targetRequestParams.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "FlashMap [attributes=" + super.toString() + ", targetRequestPath=" +
				this.targetRequestPath + ", targetRequestParams=" + this.targetRequestParams + "]";
	}

}
