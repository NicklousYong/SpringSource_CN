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

package org.springframework.util;

import java.util.Collection;
import java.util.Collections;

/**
 * A simple instance filter that checks if a given instance match based on
 * a collection of includes and excludes element.
 *
 * <p>Subclasses may want to override {@link #match(Object, Object)} to provide
 * a custom matching algorithm.
 *
 * <p>
 *  一个简单的实例过滤器,用于检查给定实例是否基于include和excludes元素的集合进行匹配
 * 
 *  <p>子类可能需要覆盖{@link #match(Object,Object)}以提供自定义匹配算法
 * 
 * 
 * @author Stephane Nicoll
 * @since 4.1
 */
public class InstanceFilter<T> {

	private final Collection<? extends T> includes;

	private final Collection<? extends T> excludes;

	private final boolean matchIfEmpty;


	/**
	 * Create a new instance based on includes/excludes collections.
	 * <p>A particular element will match if it "matches" the one of the element in the
	 * includes list and  does not match one of the element in the excludes list.
	 * <p>Subclasses may redefine what matching means. By default, an element match with
	 * another if it is equals according to {@link Object#equals(Object)}
	 * <p>If both collections are empty, {@code matchIfEmpty} defines if
	 * an element matches or not.
	 * <p>
	 * 根据includes / excludes集合创建新实例<p>如果特定元素"匹配"包含列表中的一个元素,并且与排除列表中的一个元素不匹配,则子句可能会重新定义什么匹配手段默认情况下,一个元素与另一个元素
	 * 匹配,如果它是根据{@link Object#equals(Object)}等于的值;如果两个集合都是空的,{@code matchIfEmpty}定义一个元素是否匹配。
	 * 
	 * 
	 * @param includes the collection of includes
	 * @param excludes the collection of excludes
	 * @param matchIfEmpty the matching result if both the includes and the excludes
	 * collections are empty
	 */
	public InstanceFilter(Collection<? extends T> includes,
			Collection<? extends T> excludes, boolean matchIfEmpty) {

		this.includes = includes != null ? includes : Collections.<T>emptyList();
		this.excludes = excludes != null ? excludes : Collections.<T>emptyList();
		this.matchIfEmpty = matchIfEmpty;
	}


	/**
	 * Determine if the specified {code instance} matches this filter.
	 * <p>
	 *  确定指定的{code instance}是否匹配此过滤器
	 * 
	 */
	public boolean match(T instance) {
		Assert.notNull(instance, "The instance to match is mandatory");

		boolean includesSet = !this.includes.isEmpty();
		boolean excludesSet = !this.excludes.isEmpty();
		if (!includesSet && !excludesSet) {
			return this.matchIfEmpty;
		}

		boolean matchIncludes = match(instance, this.includes);
		boolean matchExcludes = match(instance, this.excludes);

		if (!includesSet) {
			return !matchExcludes;
		}

		if (!excludesSet) {
			return matchIncludes;
		}
		return matchIncludes && !matchExcludes;
	}

	/**
	 * Determine if the specified {@code instance} is equal to the
	 * specified {@code candidate}.
	 * <p>
	 *  确定指定的{@code实例}是否等于指定的{@code候选者}
	 * 
	 * 
	 * @param instance the instance to handle
	 * @param candidate a candidate defined by this filter
	 * @return {@code true} if the instance matches the candidate
	 */
	protected boolean match(T instance, T candidate) {
		return instance.equals(candidate);
	}

	/**
	 * Determine if the specified {@code instance} matches one of the candidates.
	 * <p>If the candidates collection is {@code null}, returns {@code false}.
	 * <p>
	 *  确定指定的{@code实例}是否匹配候选者之一<p>如果候选集合是{@code null},则返回{@code false}
	 * 
	 * @param instance the instance to check
	 * @param candidates a list of candidates
	 * @return {@code true} if the instance match or the candidates collection is null
	 */
	protected boolean match(T instance, Collection<? extends T> candidates) {
		for (T candidate : candidates) {
			if (match(instance, candidate)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(getClass().getSimpleName());
		sb.append(": includes=").append(this.includes);
		sb.append(", excludes=").append(this.excludes);
		sb.append(", matchIfEmpty=").append(this.matchIfEmpty);
		return sb.toString();
	}

}
