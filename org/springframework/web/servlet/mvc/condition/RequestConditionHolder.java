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

package org.springframework.web.servlet.mvc.condition;

import java.util.Collection;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;

/**
 * A holder for a {@link RequestCondition} useful when the type of the request
 * condition is not known ahead of time, e.g. custom condition. Since this
 * class is also an implementation of {@code RequestCondition}, effectively it
 * decorates the held request condition and allows it to be combined and compared
 * with other request conditions in a type and null safe way.
 *
 * <p>When two {@code RequestConditionHolder} instances are combined or compared
 * with each other, it is expected the conditions they hold are of the same type.
 * If they are not, a {@link ClassCastException} is raised.
 *
 * <p>
 * 当请求条件的类型未提前知道时,{@link RequestCondition}的持有人有用,例如自定义条件由于此类也是{@code RequestCondition}的实现,因此它有效地装饰保持的请求条
 * 件,并允许它被组合并与类型和空安全方式的其他请求条件进行比较。
 * 
 *  <p>当两个{@code RequestConditionHolder}实例相互组合或比较时,预期它们所持有的条件是相同的类型如果不是,则会引发{@link ClassCastException}
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public final class RequestConditionHolder extends AbstractRequestCondition<RequestConditionHolder> {

	private final RequestCondition<Object> condition;


	/**
	 * Create a new holder to wrap the given request condition.
	 * <p>
	 *  创建一个新的持有者来包装给定的请求条件
	 * 
	 * 
	 * @param requestCondition the condition to hold, may be {@code null}
	 */
	@SuppressWarnings("unchecked")
	public RequestConditionHolder(RequestCondition<?> requestCondition) {
		this.condition = (RequestCondition<Object>) requestCondition;
	}


	/**
	 * Return the held request condition, or {@code null} if not holding one.
	 * <p>
	 *  返回持有的请求条件,否则{@code null}如果不持有请求条件
	 * 
	 */
	public RequestCondition<?> getCondition() {
		return this.condition;
	}

	@Override
	protected Collection<?> getContent() {
		return (this.condition != null ? Collections.singleton(this.condition) : Collections.emptyList());
	}

	@Override
	protected String getToStringInfix() {
		return " ";
	}

	/**
	 * Combine the request conditions held by the two RequestConditionHolder
	 * instances after making sure the conditions are of the same type.
	 * Or if one holder is empty, the other holder is returned.
	 * <p>
	 * 在确定条件相同的类型后,组合两个RequestConditionHolder实例所持有的请求条件或如果一个保持器为空,则返回另一个持有者
	 * 
	 */
	@Override
	public RequestConditionHolder combine(RequestConditionHolder other) {
		if (this.condition == null && other.condition == null) {
			return this;
		}
		else if (this.condition == null) {
			return other;
		}
		else if (other.condition == null) {
			return this;
		}
		else {
			assertEqualConditionTypes(other);
			RequestCondition<?> combined = (RequestCondition<?>) this.condition.combine(other.condition);
			return new RequestConditionHolder(combined);
		}
	}

	/**
	 * Ensure the held request conditions are of the same type.
	 * <p>
	 *  确保持有的请求条件是相同的类型
	 * 
	 */
	private void assertEqualConditionTypes(RequestConditionHolder other) {
		Class<?> clazz = this.condition.getClass();
		Class<?> otherClazz = other.condition.getClass();
		if (!clazz.equals(otherClazz)) {
			throw new ClassCastException("Incompatible request conditions: " + clazz + " and " + otherClazz);
		}
	}

	/**
	 * Get the matching condition for the held request condition wrap it in a
	 * new RequestConditionHolder instance. Or otherwise if this is an empty
	 * holder, return the same holder instance.
	 * <p>
	 *  获取保持的请求条件的匹配条件将其包装在新的RequestConditionHolder实例中或否则如果这是空持有者,则返回相同的持有者实例
	 * 
	 */
	@Override
	public RequestConditionHolder getMatchingCondition(HttpServletRequest request) {
		if (this.condition == null) {
			return this;
		}
		RequestCondition<?> match = (RequestCondition<?>) this.condition.getMatchingCondition(request);
		return (match != null ? new RequestConditionHolder(match) : null);
	}

	/**
	 * Compare the request conditions held by the two RequestConditionHolder
	 * instances after making sure the conditions are of the same type.
	 * Or if one holder is empty, the other holder is preferred.
	 * <p>
	 *  比较两个RequestConditionHolder实例所保持的请求条件,确定条件是相同的类型或如果一个持有人是空的,另一个持有者是首选的
	 */
	@Override
	public int compareTo(RequestConditionHolder other, HttpServletRequest request) {
		if (this.condition == null && other.condition == null) {
			return 0;
		}
		else if (this.condition == null) {
			return 1;
		}
		else if (other.condition == null) {
			return -1;
		}
		else {
			assertEqualConditionTypes(other);
			return this.condition.compareTo(other.condition, request);
		}
	}

}
