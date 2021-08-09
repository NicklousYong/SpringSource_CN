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

package org.springframework.web.servlet.mvc.condition;

import javax.servlet.http.HttpServletRequest;

/**
 * Contract for request mapping conditions.
 *
 * <p>Request conditions can be combined via {@link #combine(Object)}, matched to
 * a request via {@link #getMatchingCondition(HttpServletRequest)}, and compared
 * to each other via {@link #compareTo(Object, HttpServletRequest)} to determine
 * which is a closer match for a given request.
 *
 * <p>
 *  请求映射条件的合同
 * 
 * <p>请求条件可以通过{@link #combine(Object)}进行组合,通过{@link #getMatchingCondition(HttpServletRequest)})与请求相匹配,并通
 * 过{@link #compareTo(Object,HttpServletRequest))彼此进行比较}来确定哪个是给定请求的更接近的匹配。
 * 
 * 
 * @author Rossen Stoyanchev
 * @author Arjen Poutsma
 * @since 3.1
 * @param <T> the type of objects that this RequestCondition can be combined
 * with and compared to
 */
public interface RequestCondition<T> {

	/**
	 * Combine this condition with another such as conditions from a
	 * type-level and method-level {@code @RequestMapping} annotation.
	 * <p>
	 *  将此条件与另一个条件组合,例如类型级别和方法级{@code @RequestMapping}注释的条件
	 * 
	 * 
	 * @param other the condition to combine with.
	 * @return a request condition instance that is the result of combining
	 * the two condition instances.
	 */
	T combine(T other);

	/**
	 * Check if the condition matches the request returning a potentially new
	 * instance created for the current request. For example a condition with
	 * multiple URL patterns may return a new instance only with those patterns
	 * that match the request.
	 * <p>For CORS pre-flight requests, conditions should match to the would-be,
	 * actual request (e.g. URL pattern, query parameters, and the HTTP method
	 * from the "Access-Control-Request-Method" header). If a condition cannot
	 * be matched to a pre-flight request it should return an instance with
	 * empty content thus not causing a failure to match.
	 * <p>
	 * 检查条件是否匹配请求,返回为当前请求创建的可能的新实例例如,具有多个URL模式的条件可能会返回一个新的实例,只有符合请求的那些模式<p>对于CORS预先请求,条件应该匹配实际的请求(例如,URL模式,查
	 * 询参数和"访问控制 - 请求 - 方法")头中的HTTP方法如果条件不能与飞行前请求匹配,则应返回具有空的内容的实例,因此不会导致失败匹配。
	 * 
	 * 
	 * @return a condition instance in case of a match or {@code null} otherwise.
	 */
	T getMatchingCondition(HttpServletRequest request);

	/**
	 * Compare this condition to another condition in the context of
	 * a specific request. This method assumes both instances have
	 * been obtained via {@link #getMatchingCondition(HttpServletRequest)}
	 * to ensure they have content relevant to current request only.
	 * <p>
	 * 在特定请求的上下文中将此条件与另一个条件进行比较此方法假设通过{@link #getMatchingCondition(HttpServletRequest)}获取两个实例,以确保它们具有与当前请求相关
	 * 的内容。
	 */
	int compareTo(T other, HttpServletRequest request);

}
