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

package org.springframework.web.servlet.support;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.FlashMapManager;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UrlPathHelper;


/**
 * A base class for {@link FlashMapManager} implementations.
 *
 * <p>
 *  {@link FlashMapManager}实现的基类
 * 
 * 
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @since 3.1.1
 */
public abstract class AbstractFlashMapManager implements FlashMapManager {

	private static final Object DEFAULT_FLASH_MAPS_MUTEX = new Object();


	protected final Log logger = LogFactory.getLog(getClass());

	private int flashMapTimeout = 180;

	private UrlPathHelper urlPathHelper = new UrlPathHelper();


	/**
	 * Set the amount of time in seconds after a {@link FlashMap} is saved
	 * (at request completion) and before it expires.
	 * <p>The default value is 180 seconds.
	 * <p>
	 *  设置保存{@link FlashMap}(请求完成后)并在其过期之前的时间数(秒)<p>默认值为180秒
	 * 
	 */
	public void setFlashMapTimeout(int flashMapTimeout) {
		this.flashMapTimeout = flashMapTimeout;
	}

	/**
	 * Return the amount of time in seconds before a FlashMap expires.
	 * <p>
	 * 返回FlashMap到期之前的秒数
	 * 
	 */
	public int getFlashMapTimeout() {
		return this.flashMapTimeout;
	}

	/**
	 * Set the UrlPathHelper to use to match FlashMap instances to requests.
	 * <p>
	 *  设置UrlPathHelper以将FlashMap实例与请求相匹配
	 * 
	 */
	public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
		Assert.notNull(urlPathHelper, "UrlPathHelper must not be null");
		this.urlPathHelper = urlPathHelper;
	}

	/**
	 * Return the UrlPathHelper implementation to use.
	 * <p>
	 *  返回要使用的UrlPathHelper实现
	 * 
	 */
	public UrlPathHelper getUrlPathHelper() {
		return this.urlPathHelper;
	}


	@Override
	public final FlashMap retrieveAndUpdate(HttpServletRequest request, HttpServletResponse response) {
		List<FlashMap> allFlashMaps = retrieveFlashMaps(request);
		if (CollectionUtils.isEmpty(allFlashMaps)) {
			return null;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Retrieved FlashMap(s): " + allFlashMaps);
		}
		List<FlashMap> mapsToRemove = getExpiredFlashMaps(allFlashMaps);
		FlashMap match = getMatchingFlashMap(allFlashMaps, request);
		if (match != null) {
			mapsToRemove.add(match);
		}

		if (!mapsToRemove.isEmpty()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Removing FlashMap(s): " + mapsToRemove);
			}
			Object mutex = getFlashMapsMutex(request);
			if (mutex != null) {
				synchronized (mutex) {
					allFlashMaps = retrieveFlashMaps(request);
					if (allFlashMaps != null) {
						allFlashMaps.removeAll(mapsToRemove);
						updateFlashMaps(allFlashMaps, request, response);
					}
				}
			}
			else {
				allFlashMaps.removeAll(mapsToRemove);
				updateFlashMaps(allFlashMaps, request, response);
			}
		}

		return match;
	}

	/**
	 * Return a list of expired FlashMap instances contained in the given list.
	 * <p>
	 *  返回给定列表中包含的过期FlashMap实例的列表
	 * 
	 */
	private List<FlashMap> getExpiredFlashMaps(List<FlashMap> allMaps) {
		List<FlashMap> result = new LinkedList<FlashMap>();
		for (FlashMap map : allMaps) {
			if (map.isExpired()) {
				result.add(map);
			}
		}
		return result;
	}

	/**
	 * Return a FlashMap contained in the given list that matches the request.
	 * <p>
	 *  返回与请求匹配的给定列表中包含的FlashMap
	 * 
	 * 
	 * @return a matching FlashMap or {@code null}
	 */
	private FlashMap getMatchingFlashMap(List<FlashMap> allMaps, HttpServletRequest request) {
		List<FlashMap> result = new LinkedList<FlashMap>();
		for (FlashMap flashMap : allMaps) {
			if (isFlashMapForRequest(flashMap, request)) {
				result.add(flashMap);
			}
		}
		if (!result.isEmpty()) {
			Collections.sort(result);
			if (logger.isDebugEnabled()) {
				logger.debug("Found matching FlashMap(s): " + result);
			}
			return result.get(0);
		}
		return null;
	}

	/**
	 * Whether the given FlashMap matches the current request.
	 * Uses the expected request path and query parameters saved in the FlashMap.
	 * <p>
	 *  给定的FlashMap是否与当前请求匹配使用FlashMap中保存的预期请求路径和查询参数
	 * 
	 */
	protected boolean isFlashMapForRequest(FlashMap flashMap, HttpServletRequest request) {
		String expectedPath = flashMap.getTargetRequestPath();
		if (expectedPath != null) {
			String requestUri = getUrlPathHelper().getOriginatingRequestUri(request);
			if (!requestUri.equals(expectedPath) && !requestUri.equals(expectedPath + "/")) {
				return false;
			}
		}
		UriComponents uriComponents = ServletUriComponentsBuilder.fromRequest(request).build();
		MultiValueMap<String, String> actualParams = uriComponents.getQueryParams();
		MultiValueMap<String, String> expectedParams = flashMap.getTargetRequestParams();
		for (String expectedName : expectedParams.keySet()) {
			List<String> actualValues = actualParams.get(expectedName);
			if (actualValues == null) {
				return false;
			}
			for (String expectedValue : expectedParams.get(expectedName)) {
				if (!actualValues.contains(expectedValue)) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public final void saveOutputFlashMap(FlashMap flashMap, HttpServletRequest request, HttpServletResponse response) {
		if (CollectionUtils.isEmpty(flashMap)) {
			return;
		}

		String path = decodeAndNormalizePath(flashMap.getTargetRequestPath(), request);
		flashMap.setTargetRequestPath(path);

		if (logger.isDebugEnabled()) {
			logger.debug("Saving FlashMap=" + flashMap);
		}
		flashMap.startExpirationPeriod(getFlashMapTimeout());

		Object mutex = getFlashMapsMutex(request);
		if (mutex != null) {
			synchronized (mutex) {
				List<FlashMap> allFlashMaps = retrieveFlashMaps(request);
				allFlashMaps = (allFlashMaps != null ? allFlashMaps : new CopyOnWriteArrayList<FlashMap>());
				allFlashMaps.add(flashMap);
				updateFlashMaps(allFlashMaps, request, response);
			}
		}
		else {
			List<FlashMap> allFlashMaps = retrieveFlashMaps(request);
			allFlashMaps = (allFlashMaps != null ? allFlashMaps : new LinkedList<FlashMap>());
			allFlashMaps.add(flashMap);
			updateFlashMaps(allFlashMaps, request, response);
		}
	}

	private String decodeAndNormalizePath(String path, HttpServletRequest request) {
		if (path != null) {
			path = getUrlPathHelper().decodeRequestString(request, path);
			if (path.charAt(0) != '/') {
				String requestUri = getUrlPathHelper().getRequestUri(request);
				path = requestUri.substring(0, requestUri.lastIndexOf('/') + 1) + path;
				path = StringUtils.cleanPath(path);
			}
		}
		return path;
	}

	/**
	 * Retrieve saved FlashMap instances from the underlying storage.
	 * <p>
	 *  从底层存储中检索保存的FlashMap实例
	 * 
	 * 
	 * @param request the current request
	 * @return a List with FlashMap instances, or {@code null} if none found
	 */
	protected abstract List<FlashMap> retrieveFlashMaps(HttpServletRequest request);

	/**
	 * Update the FlashMap instances in the underlying storage.
	 * <p>
	 *  更新底层存储中的FlashMap实例
	 * 
	 * 
	 * @param flashMaps a (potentially empty) list of FlashMap instances to save
	 * @param request the current request
	 * @param response the current response
	 */
	protected abstract void updateFlashMaps(
			List<FlashMap> flashMaps, HttpServletRequest request, HttpServletResponse response);

	/**
	 * Obtain a mutex for modifying the FlashMap List as handled by
	 * {@link #retrieveFlashMaps} and {@link #updateFlashMaps},
	 * <p>The default implementation returns a shared static mutex.
	 * Subclasses are encouraged to return a more specific mutex, or
	 * {@code null} to indicate that no synchronization is necessary.
	 * <p>
	 * 获取用于修改由{@link #retrieveFlashMaps}和{@link #updateFlashMaps}处理的FlashMap列表的互斥体,默认实现返回一个共享静态互斥体鼓励子类返回更具体的
	 * 互斥体,或{@code null}表示不需要同步。
	 * 
	 * @param request the current request
	 * @return the mutex to use (may be {@code null} if none applicable)
	 * @since 4.0.3
	 */
	protected Object getFlashMapsMutex(HttpServletRequest request) {
		return DEFAULT_FLASH_MAPS_MUTEX;
	}

}
