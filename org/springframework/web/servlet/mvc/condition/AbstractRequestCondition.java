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
import java.util.Iterator;

/**
 * A base class for {@link RequestCondition} types providing implementations of
 * {@link #equals(Object)}, {@link #hashCode()}, and {@link #toString()}.
 *
 * <p>
 *  提供{@link #equals(Object)},{@link #hashCode()}和{@link #toString()}的实现的{@link RequestCondition}类型的基类
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public abstract class AbstractRequestCondition<T extends AbstractRequestCondition<T>> implements RequestCondition<T> {

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj != null && getClass() == obj.getClass()) {
			AbstractRequestCondition<?> other = (AbstractRequestCondition<?>) obj;
			return getContent().equals(other.getContent());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getContent().hashCode();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("[");
		for (Iterator<?> iterator = getContent().iterator(); iterator.hasNext();) {
			Object expression = iterator.next();
			builder.append(expression.toString());
			if (iterator.hasNext()) {
				builder.append(getToStringInfix());
			}
		}
		builder.append("]");
		return builder.toString();
	}

	/**
	 * Indicates whether this condition is empty, i.e. whether or not it
	 * contains any discrete items.
	 * <p>
	 * 指示此条件是否为空,即是否包含任何离散项
	 * 
	 * 
	 * @return {@code true} if empty; {@code false} otherwise
	 */
	public boolean isEmpty() {
		return getContent().isEmpty();
	}


	/**
	 * Return the discrete items a request condition is composed of.
	 * <p>For example URL patterns, HTTP request methods, param expressions, etc.
	 * <p>
	 *  返回离散项目请求条件由<p>组成,例如URL模式,HTTP请求方法,参数表达式等
	 * 
	 * 
	 * @return a collection of objects, never {@code null}
	 */
	protected abstract Collection<?> getContent();

	/**
	 * The notation to use when printing discrete items of content.
	 * <p>For example {@code " || "} for URL patterns or {@code " && "}
	 * for param expressions.
	 * <p>
	 *  打印离散项目内容时使用的符号<p>例如{@code"||"}用于URL模式或{@code"&&"}用于参数表达式
	 */
	protected abstract String getToStringInfix();

}
