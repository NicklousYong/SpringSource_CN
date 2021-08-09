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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.mvc.condition.HeadersRequestCondition.HeaderExpression;

/**
 * A logical disjunction (' || ') request condition to match a request's
 * 'Content-Type' header to a list of media type expressions. Two kinds of
 * media type expressions are supported, which are described in
 * {@link RequestMapping#consumes()} and {@link RequestMapping#headers()}
 * where the header name is 'Content-Type'. Regardless of which syntax is
 * used, the semantics are the same.
 *
 * <p>
 * 将请求的"Content-Type"标头与媒体类型表达式匹配的逻辑分离('||')请求条件支持两种媒体类型表达式,这些描述在{@link RequestMapping#consumption()}中和{@link RequestMapping#headers()}
 * ,其中头名称是"Content-Type",无论使用哪种语法,语义是相同的。
 * 
 * 
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public final class ConsumesRequestCondition extends AbstractRequestCondition<ConsumesRequestCondition> {

	private final static ConsumesRequestCondition PRE_FLIGHT_MATCH = new ConsumesRequestCondition();


	private final List<ConsumeMediaTypeExpression> expressions;


	/**
	 * Creates a new instance from 0 or more "consumes" expressions.
	 * <p>
	 *  从0或更多的"消耗"表达式创建一个新的实例
	 * 
	 * 
	 * @param consumes expressions with the syntax described in
	 * {@link RequestMapping#consumes()}; if 0 expressions are provided,
	 * the condition will match to every request
	 */
	public ConsumesRequestCondition(String... consumes) {
		this(consumes, null);
	}

	/**
	 * Creates a new instance with "consumes" and "header" expressions.
	 * "Header" expressions where the header name is not 'Content-Type' or have
	 * no header value defined are ignored. If 0 expressions are provided in
	 * total, the condition will match to every request
	 * <p>
	 *  使用"消耗"和"头"表达式创建一个新实例,其中标题名称不是"Content-Type"或没有标题值定义的表达式"Header"表达式如果总共提供了0个表达式,则该条件将匹配每个请求
	 * 
	 * 
	 * @param consumes as described in {@link RequestMapping#consumes()}
	 * @param headers as described in {@link RequestMapping#headers()}
	 */
	public ConsumesRequestCondition(String[] consumes, String[] headers) {
		this(parseExpressions(consumes, headers));
	}

	/**
	 * Private constructor accepting parsed media type expressions.
	 * <p>
	 *  接受解析的媒体类型表达式的私有构造函数
	 * 
	 */
	private ConsumesRequestCondition(Collection<ConsumeMediaTypeExpression> expressions) {
		this.expressions = new ArrayList<ConsumeMediaTypeExpression>(expressions);
		Collections.sort(this.expressions);
	}


	private static Set<ConsumeMediaTypeExpression> parseExpressions(String[] consumes, String[] headers) {
		Set<ConsumeMediaTypeExpression> result = new LinkedHashSet<ConsumeMediaTypeExpression>();
		if (headers != null) {
			for (String header : headers) {
				HeaderExpression expr = new HeaderExpression(header);
				if ("Content-Type".equalsIgnoreCase(expr.name)) {
					for (MediaType mediaType : MediaType.parseMediaTypes(expr.value)) {
						result.add(new ConsumeMediaTypeExpression(mediaType, expr.isNegated));
					}
				}
			}
		}
		if (consumes != null) {
			for (String consume : consumes) {
				result.add(new ConsumeMediaTypeExpression(consume));
			}
		}
		return result;
	}


	/**
	 * Return the contained MediaType expressions.
	 * <p>
	 * 返回包含的MediaType表达式
	 * 
	 */
	public Set<MediaTypeExpression> getExpressions() {
		return new LinkedHashSet<MediaTypeExpression>(this.expressions);
	}

	/**
	 * Returns the media types for this condition excluding negated expressions.
	 * <p>
	 *  返回此条件的媒体类型,不包括否定表达式
	 * 
	 */
	public Set<MediaType> getConsumableMediaTypes() {
		Set<MediaType> result = new LinkedHashSet<MediaType>();
		for (ConsumeMediaTypeExpression expression : this.expressions) {
			if (!expression.isNegated()) {
				result.add(expression.getMediaType());
			}
		}
		return result;
	}

	/**
	 * Whether the condition has any media type expressions.
	 * <p>
	 *  条件是否具有任何媒体类型表达式
	 * 
	 */
	public boolean isEmpty() {
		return this.expressions.isEmpty();
	}

	@Override
	protected Collection<ConsumeMediaTypeExpression> getContent() {
		return this.expressions;
	}

	@Override
	protected String getToStringInfix() {
		return " || ";
	}

	/**
	 * Returns the "other" instance if it has any expressions; returns "this"
	 * instance otherwise. Practically that means a method-level "consumes"
	 * overrides a type-level "consumes" condition.
	 * <p>
	 *  如果具有任何表达式,则返回"其他"实例;返回"此"实例否则实际上这意味着方法级"消耗"覆盖类型级别"消耗"条件
	 * 
	 */
	@Override
	public ConsumesRequestCondition combine(ConsumesRequestCondition other) {
		return !other.expressions.isEmpty() ? other : this;
	}

	/**
	 * Checks if any of the contained media type expressions match the given
	 * request 'Content-Type' header and returns an instance that is guaranteed
	 * to contain matching expressions only. The match is performed via
	 * {@link MediaType#includes(MediaType)}.
	 * <p>
	 *  检查所包含的媒体类型表达式是否与给定的请求"Content-Type"头匹配,并返回一个保证包含匹配表达式的实例。
	 * 该匹配通过{@link MediaType#includes(MediaType)}执行。
	 * 
	 * 
	 * @param request the current request
	 * @return the same instance if the condition contains no expressions;
	 * or a new condition with matching expressions only;
	 * or {@code null} if no expressions match.
	 */
	@Override
	public ConsumesRequestCondition getMatchingCondition(HttpServletRequest request) {
		if (CorsUtils.isPreFlightRequest(request)) {
			return PRE_FLIGHT_MATCH;
		}
		if (isEmpty()) {
			return this;
		}
		MediaType contentType;
		try {
			contentType = StringUtils.hasLength(request.getContentType()) ?
					MediaType.parseMediaType(request.getContentType()) :
					MediaType.APPLICATION_OCTET_STREAM;
		}
		catch (InvalidMediaTypeException ex) {
			return null;
		}
		Set<ConsumeMediaTypeExpression> result = new LinkedHashSet<ConsumeMediaTypeExpression>(this.expressions);
		for (Iterator<ConsumeMediaTypeExpression> iterator = result.iterator(); iterator.hasNext();) {
			ConsumeMediaTypeExpression expression = iterator.next();
			if (!expression.match(contentType)) {
				iterator.remove();
			}
		}
		return (result.isEmpty()) ? null : new ConsumesRequestCondition(result);
	}

	/**
	 * Returns:
	 * <ul>
	 * <li>0 if the two conditions have the same number of expressions
	 * <li>Less than 0 if "this" has more or more specific media type expressions
	 * <li>Greater than 0 if "other" has more or more specific media type expressions
	 * </ul>
	 * <p>It is assumed that both instances have been obtained via
	 * {@link #getMatchingCondition(HttpServletRequest)} and each instance contains
	 * the matching consumable media type expression only or is otherwise empty.
	 * <p>
	 *  返回：
	 * <ul>
	 * 如果两个条件具有相同的表达式数量,则<li> 0 <li>如果"this"具有更多或更多特定的媒体类型表达式,则小于0 <li>如果"other"具有更多或更多特定的媒体类型表达式,则大于0
	 * </ul>
	 *  <p>假设这两个实例都是通过{@link #getMatchingCondition(HttpServletRequest)}获得的,每个实例只包含匹配的可消耗介质类型表达式,否则为空
	 */
	@Override
	public int compareTo(ConsumesRequestCondition other, HttpServletRequest request) {
		if (this.expressions.isEmpty() && other.expressions.isEmpty()) {
			return 0;
		}
		else if (this.expressions.isEmpty()) {
			return 1;
		}
		else if (other.expressions.isEmpty()) {
			return -1;
		}
		else {
			return this.expressions.get(0).compareTo(other.expressions.get(0));
		}
	}


	/**
	 * Parses and matches a single media type expression to a request's 'Content-Type' header.
	 * <p>
	 * 
	 */
	static class ConsumeMediaTypeExpression extends AbstractMediaTypeExpression {

		ConsumeMediaTypeExpression(String expression) {
			super(expression);
		}

		ConsumeMediaTypeExpression(MediaType mediaType, boolean negated) {
			super(mediaType, negated);
		}

		public final boolean match(MediaType contentType) {
			boolean match = getMediaType().includes(contentType);
			return (!isNegated() ? match : !match);
		}
	}

}
