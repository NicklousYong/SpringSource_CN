/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2014 the original author or authors.
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A strategy interface for retrieving and saving FlashMap instances.
 * See {@link FlashMap} for a general overview of flash attributes.
 *
 * <p>
 *  用于检索和保存FlashMap实例的策略界面有关Flash属性的概述,请参阅{@link FlashMap}
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 3.1
 * @see FlashMap
 */
public interface FlashMapManager {

	/**
	 * Find a FlashMap saved by a previous request that matches to the current
	 * request, remove it from underlying storage, and also remove other
	 * expired FlashMap instances.
	 * <p>This method is invoked in the beginning of every request in contrast
	 * to {@link #saveOutputFlashMap}, which is invoked only when there are
	 * flash attributes to be saved - i.e. before a redirect.
	 * <p>
	 * 查找由先前请求保存的FlashMap,与当前请求匹配,将其从底层存储中删除,并删除其他过期的FlashMap实例<p>与{@link #saveOutputFlashMap}相反,此方法在每个请求开始时
	 * 被调用, ,仅当存在要保存的Flash属性(即在重定向之前)才调用该属性。
	 * 
	 * 
	 * @param request the current request
	 * @param response the current response
	 * @return a FlashMap matching the current request or {@code null}
	 */
	FlashMap retrieveAndUpdate(HttpServletRequest request, HttpServletResponse response);

	/**
	 * Save the given FlashMap, in some underlying storage and set the start
	 * of its expiration period.
	 * <p><strong>NOTE:</strong> Invoke this method prior to a redirect in order
	 * to allow saving the FlashMap in the HTTP session or in a response
	 * cookie before the response is committed.
	 * <p>
	 *  将给定的FlashMap保存在某些底层存储中并设置其过期时间段的开始<p> <strong>注意：</strong>在重定向之前调用此方法,以便允许将FlashMap保存在HTTP会话或响应cooki
	 * e在响应提交之前。
	 * 
	 * @param flashMap the FlashMap to save
	 * @param request the current request
	 * @param response the current response
	 */
	void saveOutputFlashMap(FlashMap flashMap, HttpServletRequest request, HttpServletResponse response);

}
