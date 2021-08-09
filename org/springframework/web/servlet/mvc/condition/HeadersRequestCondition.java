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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.cors.CorsUtils;

/**
 * A logical conjunction (' && ') request condition that matches a request against
 * a set of header expressions with syntax defined in {@link RequestMapping#headers()}.
 *
 * <p>Expressions passed to the constructor with header names 'Accept' or
 * 'Content-Type' are ignored. See {@link ConsumesRequestCondition} and
 * {@link ProducesRequestCondition} for those.
 *
 * <p>
 *  与{@link RequestMapping#headers()}中定义的语法的一组标头表达式匹配请求的逻辑连接('&&')请求条件
 * 
 * <p>传递给名称为"Accept"或"Content-Type"的构造函数的表达式将被忽略。
 * 对于那些,请参阅{@link ConsumesRequestCondition}和{@link ProducesRequestCondition。
 * 
 * 
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public final class HeadersRequestCondition extends AbstractRequestCondition<HeadersRequestCondition> {

	private final static HeadersRequestCondition PRE_FLIGHT_MATCH = new HeadersRequestCondition();


	private final Set<HeaderExpression> expressions;


	/**
	 * Create a new instance from the given header expressions. Expressions with
	 * header names 'Accept' or 'Content-Type' are ignored. See {@link ConsumesRequestCondition}
	 * and {@link ProducesRequestCondition} for those.
	 * <p>
	 *  从给定的头部表达式创建一个新的实例忽略标题名称为"Accept"或"Content-Type"的表达式忽略了对于那些的{@link ConsumesRequestCondition}和{@link ProducesRequestCondition}
	 * 。
	 * 
	 * 
	 * @param headers media type expressions with syntax defined in {@link RequestMapping#headers()};
	 * if 0, the condition will match to every request
	 */
	public HeadersRequestCondition(String... headers) {
		this(parseExpressions(headers));
	}

	private HeadersRequestCondition(Collection<HeaderExpression> conditions) {
		this.expressions = Collections.unmodifiableSet(new LinkedHashSet<HeaderExpression>(conditions));
	}


	private static Collection<HeaderExpression> parseExpressions(String... headers) {
		Set<HeaderExpression> expressions = new LinkedHashSet<HeaderExpression>();
		if (headers != null) {
			for (String header : headers) {
				HeaderExpression expr = new HeaderExpression(header);
				if ("Accept".equalsIgnoreCase(expr.name) || "Content-Type".equalsIgnoreCase(expr.name)) {
					continue;
				}
				expressions.add(expr);
			}
		}
		return expressions;
	}

	/**
	 * Return the contained request header expressions.
	 * <p>
	 *  返回包含的请求头表达式
	 * 
	 */
	public Set<NameValueExpression<String>> getExpressions() {
		return new LinkedHashSet<NameValueExpression<String>>(this.expressions);
	}

	@Override
	protected Collection<HeaderExpression> getContent() {
		return this.expressions;
	}

	@Override
	protected String getToStringInfix() {
		return " && ";
	}

	/**
	 * Returns a new instance with the union of the header expressions
	 * from "this" and the "other" instance.
	 * <p>
	 *  使用"this"和"other"实例的标题表达式的并集返回一个新的实例
	 * 
	 */
	@Override
	public HeadersRequestCondition combine(HeadersRequestCondition other) {
		Set<HeaderExpression> set = new LinkedHashSet<HeaderExpression>(this.expressions);
		set.addAll(other.expressions);
		return new HeadersRequestCondition(set);
	}

	/**
	 * Returns "this" instance if the request matches all expressions;
	 * or {@code null} otherwise.
	 * <p>
	 *  如果请求匹配所有表达式,则返回"this"实例;或{@code null}否则
	 * 
	 */
	@Override
	public HeadersRequestCondition getMatchingCondition(HttpServletRequest request) {
		if (CorsUtils.isPreFlightRequest(request)) {
			return PRE_FLIGHT_MATCH;
		}
		for (HeaderExpression expression : expressions) {
			if (!expression.match(request)) {
				return null;
			}
		}
		return this;
	}

	/**
	 * Returns:
	 * <ul>
	 * <li>0 if the two conditions have the same number of header expressions
	 * <li>Less than 0 if "this" instance has more header expressions
	 * <li>Greater than 0 if the "other" instance has more header expressions
	 * </ul>
	 * <p>It is assumed that both instances have been obtained via
	 * {@link #getMatchingCondition(HttpServletRequest)} and each instance
	 * contains the matching header expression only or is otherwise empty.
	 * <p>
	 *  返回：
	 * <ul>
	 * 如果两个条件具有相同数量的标题表达式,则<li> <li>小于0如果"此"实例具有更多的标题表达式<li>如果"其他"实例具有更多标题表达式,则大于0
	 * </ul>
	 *  假设这两个实例都是通过{@link #getMatchingCondition(HttpServletRequest)}获得的,每个实例只包含匹配的头部表达式,否则为空
	 */
	@Override
	public int compareTo(HeadersRequestCondition other, HttpServletRequest request) {
		return other.expressions.size() - this.expressions.size();
	}


	/**
	 * Parses and matches a single header expression to a request.
	 * <p>
	 * 
	 */
	static class HeaderExpression extends AbstractNameValueExpression<String> {

		public HeaderExpression(String expression) {
			super(expression);
		}

		@Override
		protected boolean isCaseSensitiveName() {
			return false;
		}

		@Override
		protected String parseValue(String valueExpression) {
			return valueExpression;
		}

		@Override
		protected boolean matchName(HttpServletRequest request) {
			return request.getHeader(name) != null;
		}

		@Override
		protected boolean matchValue(HttpServletRequest request) {
			return value.equals(request.getHeader(name));
		}
	}

}
