/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2012 the original author or authors.
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

package org.springframework.util.comparator;

import java.util.Comparator;

import org.springframework.util.Assert;

/**
 * A Comparator that will safely compare nulls to be lower or higher than
 * other objects. Can decorate a given Comparator or work on Comparables.
 *
 * <p>
 *  一个比较器,将安全地比较空值低于或高于其他对象可以装饰给定的比较器或在Comparables上工作
 * 
 * 
 * @author Keith Donald
 * @author Juergen Hoeller
 * @since 1.2.2
 * @see Comparable
 */
public class NullSafeComparator<T> implements Comparator<T> {

	/**
	 * A shared default instance of this comparator, treating nulls lower
	 * than non-null objects.
	 * <p>
	 * 此比较器的共享默认实例,处理低于非空对象的空值
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public static final NullSafeComparator NULLS_LOW = new NullSafeComparator<Object>(true);

	/**
	 * A shared default instance of this comparator, treating nulls higher
	 * than non-null objects.
	 * <p>
	 *  此比较器的共享默认实例,处理高于非空对象的空值
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public static final NullSafeComparator NULLS_HIGH = new NullSafeComparator<Object>(false);

	private final Comparator<T> nonNullComparator;

	private final boolean nullsLow;


	/**
	 * Create a NullSafeComparator that sorts {@code null} based on
	 * the provided flag, working on Comparables.
	 * <p>When comparing two non-null objects, their Comparable implementation
	 * will be used: this means that non-null elements (that this Comparator
	 * will be applied to) need to implement Comparable.
	 * <p>As a convenience, you can use the default shared instances:
	 * {@code NullSafeComparator.NULLS_LOW} and
	 * {@code NullSafeComparator.NULLS_HIGH}.
	 * <p>
	 *  创建一个NullSafeComparator,根据提供的标志排序{@code null},使用Comparables <p>比较两个非空对象时,将使用它们的Comparable实现：这意味着非空元素(
	 * 该比较器将为应用于)需要实现Comparable <p>为方便起见,您可以使用默认的共享实例：{@code NullSafeComparatorNULLS_LOW}和{@code NullSafeComparatorNULLS_HIGH}
	 * 。
	 * 
	 * 
	 * @param nullsLow whether to treat nulls lower or higher than non-null objects
	 * @see Comparable
	 * @see #NULLS_LOW
	 * @see #NULLS_HIGH
	 */
	@SuppressWarnings({ "unchecked", "rawtypes"})
	private NullSafeComparator(boolean nullsLow) {
		this.nonNullComparator = new ComparableComparator();
		this.nullsLow = nullsLow;
	}

	/**
	 * Create a NullSafeComparator that sorts {@code null} based on the
	 * provided flag, decorating the given Comparator.
	 * <p>When comparing two non-null objects, the specified Comparator will be used.
	 * The given underlying Comparator must be able to handle the elements that this
	 * Comparator will be applied to.
	 * <p>
	 * 
	 * @param comparator the comparator to use when comparing two non-null objects
	 * @param nullsLow whether to treat nulls lower or higher than non-null objects
	 */
	public NullSafeComparator(Comparator<T> comparator, boolean nullsLow) {
		Assert.notNull(comparator, "The non-null comparator is required");
		this.nonNullComparator = comparator;
		this.nullsLow = nullsLow;
	}


	@Override
	public int compare(T o1, T o2) {
		if (o1 == o2) {
			return 0;
		}
		if (o1 == null) {
			return (this.nullsLow ? -1 : 1);
		}
		if (o2 == null) {
			return (this.nullsLow ? 1 : -1);
		}
		return this.nonNullComparator.compare(o1, o2);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof NullSafeComparator)) {
			return false;
		}
		NullSafeComparator<T> other = (NullSafeComparator<T>) obj;
		return (this.nonNullComparator.equals(other.nonNullComparator) && this.nullsLow == other.nullsLow);
	}

	@Override
	public int hashCode() {
		return (this.nullsLow ? -1 : 1) * this.nonNullComparator.hashCode();
	}

	@Override
	public String toString() {
		return "NullSafeComparator: non-null comparator [" + this.nonNullComparator + "]; " +
				(this.nullsLow ? "nulls low" : "nulls high");
	}

}
