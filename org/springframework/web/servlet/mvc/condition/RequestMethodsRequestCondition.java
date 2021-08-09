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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.cors.CorsUtils;

/**
 * A logical disjunction (' || ') request condition that matches a request
 * against a set of {@link RequestMethod}s.
 *
 * <p>
 *  与{@link RequestMethod}一组的请求相匹配的逻辑分离('||')请求条件
 * 
 * 
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public final class RequestMethodsRequestCondition extends AbstractRequestCondition<RequestMethodsRequestCondition> {

	private static final RequestMethodsRequestCondition GET_CONDITION =
			new RequestMethodsRequestCondition(RequestMethod.GET);


	private final Set<RequestMethod> methods;


	/**
	 * Create a new instance with the given request methods.
	 * <p>
	 *  使用给定的请求方法创建一个新的实例
	 * 
	 * 
	 * @param requestMethods 0 or more HTTP request methods;
	 * if, 0 the condition will match to every request
	 */
	public RequestMethodsRequestCondition(RequestMethod... requestMethods) {
		this(asList(requestMethods));
	}

	private RequestMethodsRequestCondition(Collection<RequestMethod> requestMethods) {
		this.methods = Collections.unmodifiableSet(new LinkedHashSet<RequestMethod>(requestMethods));
	}


	private static List<RequestMethod> asList(RequestMethod... requestMethods) {
		return (requestMethods != null ? Arrays.asList(requestMethods) : Collections.<RequestMethod>emptyList());
	}


	/**
	 * Returns all {@link RequestMethod}s contained in this condition.
	 * <p>
	 * 返回此条件中包含的所有{@link RequestMethod}
	 * 
	 */
	public Set<RequestMethod> getMethods() {
		return this.methods;
	}

	@Override
	protected Collection<RequestMethod> getContent() {
		return this.methods;
	}

	@Override
	protected String getToStringInfix() {
		return " || ";
	}

	/**
	 * Returns a new instance with a union of the HTTP request methods
	 * from "this" and the "other" instance.
	 * <p>
	 *  使用"this"和"other"实例的HTTP请求方法的并集返回一个新的实例
	 * 
	 */
	@Override
	public RequestMethodsRequestCondition combine(RequestMethodsRequestCondition other) {
		Set<RequestMethod> set = new LinkedHashSet<RequestMethod>(this.methods);
		set.addAll(other.methods);
		return new RequestMethodsRequestCondition(set);
	}

	/**
	 * Check if any of the HTTP request methods match the given request and
	 * return an instance that contains the matching HTTP request method only.
	 * <p>
	 *  检查是否有任何HTTP请求方法与给定的请求匹配,并返回仅包含匹配的HTTP请求方法的实例
	 * 
	 * 
	 * @param request the current request
	 * @return the same instance if the condition is empty (unless the request
	 * method is HTTP OPTIONS), a new condition with the matched request method,
	 * or {@code null} if there is no match or the condition is empty and the
	 * request method is OPTIONS.
	 */
	@Override
	public RequestMethodsRequestCondition getMatchingCondition(HttpServletRequest request) {
		if (CorsUtils.isPreFlightRequest(request)) {
			return matchPreFlight(request);
		}

		if (getMethods().isEmpty()) {
			if (RequestMethod.OPTIONS.name().equals(request.getMethod()) &&
					!DispatcherType.ERROR.equals(request.getDispatcherType())) {

				return null; // No implicit match for OPTIONS (we handle it)
			}
			return this;
		}

		return matchRequestMethod(request.getMethod());
	}

	/**
	 * On a pre-flight request match to the would-be, actual request.
	 * Hence empty conditions is a match, otherwise try to match to the HTTP
	 * method in the "Access-Control-Request-Method" header.
	 * <p>
	 *  在飞行前请求匹配到实际请求,因此空条件是匹配,否则尝试匹配"访问控制请求方法"头中的HTTP方法
	 * 
	 */
	private RequestMethodsRequestCondition matchPreFlight(HttpServletRequest request) {
		if (getMethods().isEmpty()) {
			return this;
		}
		String expectedMethod = request.getHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD);
		return matchRequestMethod(expectedMethod);
	}

	private RequestMethodsRequestCondition matchRequestMethod(String httpMethodValue) {
		HttpMethod httpMethod = HttpMethod.resolve(httpMethodValue);
		if (httpMethod != null) {
			for (RequestMethod method : getMethods()) {
				if (httpMethod.matches(method.name())) {
					return new RequestMethodsRequestCondition(method);
				}
			}
			if (httpMethod == HttpMethod.HEAD && getMethods().contains(RequestMethod.GET)) {
				return GET_CONDITION;
			}
		}
		return null;
	}

	/**
	 * Returns:
	 * <ul>
	 * <li>0 if the two conditions contain the same number of HTTP request methods
	 * <li>Less than 0 if "this" instance has an HTTP request method but "other" doesn't
	 * <li>Greater than 0 "other" has an HTTP request method but "this" doesn't
	 * </ul>
	 * <p>It is assumed that both instances have been obtained via
	 * {@link #getMatchingCondition(HttpServletRequest)} and therefore each instance
	 * contains the matching HTTP request method only or is otherwise empty.
	 * <p>
	 *  返回：
	 * <ul>
	 *  如果两个条件包含相同数量的HTTP请求方法,则<li> 0 <li>如果"this"实例具有HTTP请求方法,但"other"不<li>,则小于0"other"具有HTTP请求方法,但是"这个"不是。
	 */
	@Override
	public int compareTo(RequestMethodsRequestCondition other, HttpServletRequest request) {
		if (other.methods.size() != this.methods.size()) {
			return other.methods.size() - this.methods.size();
		}
		else if (this.methods.size() == 1) {
			if (this.methods.contains(RequestMethod.HEAD) && other.methods.contains(RequestMethod.GET)) {
				return -1;
			}
			else if (this.methods.contains(RequestMethod.GET) && other.methods.contains(RequestMethod.HEAD)) {
				return 1;
			}
		}
		return 0;
	}

}
