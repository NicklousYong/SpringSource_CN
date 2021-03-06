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

import org.springframework.http.MediaType;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.mvc.condition.HeadersRequestCondition.HeaderExpression;

/**
 * A logical disjunction (' || ') request condition to match a request's 'Accept' header
 * to a list of media type expressions. Two kinds of media type expressions are
 * supported, which are described in {@link RequestMapping#produces()} and
 * {@link RequestMapping#headers()} where the header name is 'Accept'.
 * Regardless of which syntax is used, the semantics are the same.
 *
 * <p>
 * 将请求的"Accept"头匹配到媒体类型表达式的逻辑分离('||')请求条件支持两种媒体类型表达式,这些表达式在{@link RequestMapping#produce()}和{ @link RequestMapping#headers()}
 * 其中标题名称为"Accept"无论使用哪种语法,语义是相同的。
 * 
 * 
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public final class ProducesRequestCondition extends AbstractRequestCondition<ProducesRequestCondition> {

	private final static ProducesRequestCondition PRE_FLIGHT_MATCH = new ProducesRequestCondition();

	private static final ProducesRequestCondition EMPTY_CONDITION = new ProducesRequestCondition();


	private final List<ProduceMediaTypeExpression> MEDIA_TYPE_ALL_LIST =
			Collections.singletonList(new ProduceMediaTypeExpression("*/*"));

	private final List<ProduceMediaTypeExpression> expressions;

	private final ContentNegotiationManager contentNegotiationManager;


	/**
	 * Creates a new instance from "produces" expressions. If 0 expressions
	 * are provided in total, this condition will match to any request.
	 * <p>
	 *  私人最终列表<ProduceMediaTypeExpression>表达式;
	 * 
	 *  ContentNegotiationManager contentNegotiationManager;
	 * 
	 *  / **从"produce"表达式创建一个新的实例如果总共提供了0个表达式,则该条件将匹配任何请求
	 * 
	 * 
	 * @param produces expressions with syntax defined by {@link RequestMapping#produces()}
	 */
	public ProducesRequestCondition(String... produces) {
		this(produces, (String[]) null);
	}

	/**
	 * Creates a new instance with "produces" and "header" expressions. "Header"
	 * expressions where the header name is not 'Accept' or have no header value
	 * defined are ignored. If 0 expressions are provided in total, this condition
	 * will match to any request.
	 * <p>
	 * 使用"produce"和"header"表达式创建一个新的实例,其中标题名称不是"Accept"或没有标头标识定义的表达式"Header"表达式如果总共提供了0个表达式,则该条件将匹配任何请求
	 * 
	 * 
	 * @param produces expressions with syntax defined by {@link RequestMapping#produces()}
	 * @param headers expressions with syntax defined by {@link RequestMapping#headers()}
	 */
	public ProducesRequestCondition(String[] produces, String[] headers) {
		this(produces, headers, null);
	}

	/**
	 * Same as {@link #ProducesRequestCondition(String[], String[])} but also
	 * accepting a {@link ContentNegotiationManager}.
	 * <p>
	 *  与{@link #ProducesRequestCondition(String [],String [])}相同,但也接受{@link ContentNegotiationManager}
	 * 
	 * 
	 * @param produces expressions with syntax defined by {@link RequestMapping#produces()}
	 * @param headers expressions with syntax defined by {@link RequestMapping#headers()}
	 * @param manager used to determine requested media types
	 */
	public ProducesRequestCondition(String[] produces, String[] headers, ContentNegotiationManager manager) {
		this.expressions = new ArrayList<ProduceMediaTypeExpression>(parseExpressions(produces, headers));
		Collections.sort(this.expressions);
		this.contentNegotiationManager = (manager != null ? manager : new ContentNegotiationManager());
	}

	/**
	 * Private constructor with already parsed media type expressions.
	 * <p>
	 *  具有已解析的媒体类型表达式的私有构造函数
	 * 
	 */
	private ProducesRequestCondition(Collection<ProduceMediaTypeExpression> expressions, ContentNegotiationManager manager) {
		this.expressions = new ArrayList<ProduceMediaTypeExpression>(expressions);
		Collections.sort(this.expressions);
		this.contentNegotiationManager = (manager != null ? manager : new ContentNegotiationManager());
	}


	private Set<ProduceMediaTypeExpression> parseExpressions(String[] produces, String[] headers) {
		Set<ProduceMediaTypeExpression> result = new LinkedHashSet<ProduceMediaTypeExpression>();
		if (headers != null) {
			for (String header : headers) {
				HeaderExpression expr = new HeaderExpression(header);
				if ("Accept".equalsIgnoreCase(expr.name)) {
					for (MediaType mediaType : MediaType.parseMediaTypes(expr.value)) {
						result.add(new ProduceMediaTypeExpression(mediaType, expr.isNegated));
					}
				}
			}
		}
		if (produces != null) {
			for (String produce : produces) {
				result.add(new ProduceMediaTypeExpression(produce));
			}
		}
		return result;
	}

	/**
	 * Return the contained "produces" expressions.
	 * <p>
	 *  返回包含的"生成"表达式
	 * 
	 */
	public Set<MediaTypeExpression> getExpressions() {
		return new LinkedHashSet<MediaTypeExpression>(this.expressions);
	}

	/**
	 * Return the contained producible media types excluding negated expressions.
	 * <p>
	 *  返回包含的可生成的媒体类型,不包括否定表达式
	 * 
	 */
	public Set<MediaType> getProducibleMediaTypes() {
		Set<MediaType> result = new LinkedHashSet<MediaType>();
		for (ProduceMediaTypeExpression expression : this.expressions) {
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
	protected List<ProduceMediaTypeExpression> getContent() {
		return this.expressions;
	}

	@Override
	protected String getToStringInfix() {
		return " || ";
	}

	/**
	 * Returns the "other" instance if it has any expressions; returns "this"
	 * instance otherwise. Practically that means a method-level "produces"
	 * overrides a type-level "produces" condition.
	 * <p>
	 *  如果具有任何表达式,则返回"其他"实例;返回"this"实例,否则实际上这意味着一个方法级别"生成"覆盖类型级别"生成"条件
	 * 
	 */
	@Override
	public ProducesRequestCondition combine(ProducesRequestCondition other) {
		return (!other.expressions.isEmpty() ? other : this);
	}

	/**
	 * Checks if any of the contained media type expressions match the given
	 * request 'Content-Type' header and returns an instance that is guaranteed
	 * to contain matching expressions only. The match is performed via
	 * {@link MediaType#isCompatibleWith(MediaType)}.
	 * <p>
	 * 检查所包含的媒体类型表达式是否与给定请求"Content-Type"头匹配,并返回一个保证包含匹配表达式的实例。
	 * 该匹配通过{@link MediaType#isCompatibleWith(MediaType)}执行)。
	 * 
	 * 
	 * @param request the current request
	 * @return the same instance if there are no expressions;
	 * or a new condition with matching expressions;
	 * or {@code null} if no expressions match.
	 */
	@Override
	public ProducesRequestCondition getMatchingCondition(HttpServletRequest request) {
		if (CorsUtils.isPreFlightRequest(request)) {
			return PRE_FLIGHT_MATCH;
		}
		if (isEmpty()) {
			return this;
		}
		List<MediaType> acceptedMediaTypes;
		try {
			acceptedMediaTypes = getAcceptedMediaTypes(request);
		}
		catch (HttpMediaTypeException ex) {
			return null;
		}
		Set<ProduceMediaTypeExpression> result = new LinkedHashSet<ProduceMediaTypeExpression>(expressions);
		for (Iterator<ProduceMediaTypeExpression> iterator = result.iterator(); iterator.hasNext();) {
			ProduceMediaTypeExpression expression = iterator.next();
			if (!expression.match(acceptedMediaTypes)) {
				iterator.remove();
			}
		}
		if (!result.isEmpty()) {
			return new ProducesRequestCondition(result, this.contentNegotiationManager);
		}
		else if (acceptedMediaTypes.contains(MediaType.ALL)) {
			return EMPTY_CONDITION;
		}
		else {
			return null;
		}
	}

	/**
	 * Compares this and another "produces" condition as follows:
	 * <ol>
	 * <li>Sort 'Accept' header media types by quality value via
	 * {@link MediaType#sortByQualityValue(List)} and iterate the list.
	 * <li>Get the first index of matching media types in each "produces"
	 * condition first matching with {@link MediaType#equals(Object)} and
	 * then with {@link MediaType#includes(MediaType)}.
	 * <li>If a lower index is found, the condition at that index wins.
	 * <li>If both indexes are equal, the media types at the index are
	 * compared further with {@link MediaType#SPECIFICITY_COMPARATOR}.
	 * </ol>
	 * <p>It is assumed that both instances have been obtained via
	 * {@link #getMatchingCondition(HttpServletRequest)} and each instance
	 * contains the matching producible media type expression only or
	 * is otherwise empty.
	 * <p>
	 *  比较这个和另一个"生成"条件如下：
	 * <ol>
	 * <li>通过{@link MediaType#sortByQualityValue(List)}对质量值进行"接受"头文件类型,并迭代列表<li>在每个"生成"条件中获取匹配的媒体类型的第一个索引首先与
	 * {@链接MediaType#equals(Object)},然后使用{@link MediaType#includes(MediaType)}} <li>如果找到较低的索引,则该索引的条件将赢得<li>
	 * 如果两个索引相等,则媒体类型该索引进一步与{@link MediaType#SPECIFICITY_COMPARATOR}进行比较。
	 * </ol>
	 *  假设这两个实例都是通过{@link #getMatchingCondition(HttpServletRequest)}获得的,每个实例只包含匹配的可生产的媒体类型表达式,否则为空
	 */
	@Override
	public int compareTo(ProducesRequestCondition other, HttpServletRequest request) {
		try {
			List<MediaType> acceptedMediaTypes = getAcceptedMediaTypes(request);
			for (MediaType acceptedMediaType : acceptedMediaTypes) {
				int thisIndex = this.indexOfEqualMediaType(acceptedMediaType);
				int otherIndex = other.indexOfEqualMediaType(acceptedMediaType);
				int result = compareMatchingMediaTypes(this, thisIndex, other, otherIndex);
				if (result != 0) {
					return result;
				}
				thisIndex = this.indexOfIncludedMediaType(acceptedMediaType);
				otherIndex = other.indexOfIncludedMediaType(acceptedMediaType);
				result = compareMatchingMediaTypes(this, thisIndex, other, otherIndex);
				if (result != 0) {
					return result;
				}
			}
			return 0;
		}
		catch (HttpMediaTypeNotAcceptableException ex) {
			// should never happen
			throw new IllegalStateException("Cannot compare without having any requested media types", ex);
		}
	}

	private List<MediaType> getAcceptedMediaTypes(HttpServletRequest request) throws HttpMediaTypeNotAcceptableException {
		List<MediaType> mediaTypes = this.contentNegotiationManager.resolveMediaTypes(new ServletWebRequest(request));
		return mediaTypes.isEmpty() ? Collections.singletonList(MediaType.ALL) : mediaTypes;
	}

	private int indexOfEqualMediaType(MediaType mediaType) {
		for (int i = 0; i < getExpressionsToCompare().size(); i++) {
			MediaType currentMediaType = getExpressionsToCompare().get(i).getMediaType();
			if (mediaType.getType().equalsIgnoreCase(currentMediaType.getType()) &&
					mediaType.getSubtype().equalsIgnoreCase(currentMediaType.getSubtype())) {
				return i;
			}
		}
		return -1;
	}

	private int indexOfIncludedMediaType(MediaType mediaType) {
		for (int i = 0; i < getExpressionsToCompare().size(); i++) {
			if (mediaType.includes(getExpressionsToCompare().get(i).getMediaType())) {
				return i;
			}
		}
		return -1;
	}

	private int compareMatchingMediaTypes(ProducesRequestCondition condition1, int index1,
			ProducesRequestCondition condition2, int index2) {

		int result = 0;
		if (index1 != index2) {
			result = index2 - index1;
		}
		else if (index1 != -1) {
			ProduceMediaTypeExpression expr1 = condition1.getExpressionsToCompare().get(index1);
			ProduceMediaTypeExpression expr2 = condition2.getExpressionsToCompare().get(index2);
			result = expr1.compareTo(expr2);
			result = (result != 0) ? result : expr1.getMediaType().compareTo(expr2.getMediaType());
		}
		return result;
	}

	/**
	 * Return the contained "produces" expressions or if that's empty, a list
	 * with a {@code MediaType_ALL} expression.
	 * <p>
	 * 
	 */
	private List<ProduceMediaTypeExpression> getExpressionsToCompare() {
		return (this.expressions.isEmpty() ? MEDIA_TYPE_ALL_LIST : this.expressions);
	}


	/**
	 * Parses and matches a single media type expression to a request's 'Accept' header.
	 * <p>
	 * 返回包含的"生成"表达式,或者如果该表达式为空,则使用{@code MediaType_ALL}表达式的列表
	 * 
	 */
	class ProduceMediaTypeExpression extends AbstractMediaTypeExpression {

		ProduceMediaTypeExpression(MediaType mediaType, boolean negated) {
			super(mediaType, negated);
		}

		ProduceMediaTypeExpression(String expression) {
			super(expression);
		}

		public final boolean match(List<MediaType> acceptedMediaTypes) {
			boolean match = matchMediaType(acceptedMediaTypes);
			return (!isNegated() ? match : !match);
		}

		private boolean matchMediaType(List<MediaType> acceptedMediaTypes) {
			for (MediaType acceptedMediaType : acceptedMediaTypes) {
				if (getMediaType().isCompatibleWith(acceptedMediaType)) {
					return true;
				}
			}
			return false;
		}
	}

}
