/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2013 the original author or authors.
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

package org.springframework.messaging.handler;

import java.util.Collection;
import java.util.Iterator;

/**
 * A base class for {@link MessageCondition} types providing implementations of
 * {@link #equals(Object)}, {@link #hashCode()}, and {@link #toString()}.
 *
 * <p>
 * 
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public abstract class AbstractMessageCondition<T extends AbstractMessageCondition<T>> implements MessageCondition<T> {

	/**
	/* <p>
	/*  提供{@link #equals(Object)},{@link #hashCode()}和{@link #toString()}的实现的{@link MessageCondition}类型的基类
	/* 
	/* 
	 * @return the collection of objects the message condition is composed of
	 * .g. destination patterns), never {@code null}
	 */
	protected abstract Collection<?> getContent();


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj != null && getClass().equals(obj.getClass())) {
			AbstractMessageCondition<?> other = (AbstractMessageCondition<?>) obj;
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
	 * The notation to use when printing discrete items of content.
	 * For example " || " for URL patterns or " && " for param expressions.
	 * <p>
	 */
	protected abstract String getToStringInfix();

}
