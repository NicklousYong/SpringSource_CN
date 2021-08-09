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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.util.Assert;

/**
 * A comparator that chains a sequence of one or more Comparators.
 *
 * <p>A compound comparator calls each Comparator in sequence until a single
 * Comparator returns a non-zero result, or the comparators are exhausted and
 * zero is returned.
 *
 * <p>This facilitates in-memory sorting similar to multi-column sorting in SQL.
 * The order of any single Comparator in the list can also be reversed.
 *
 * <p>
 *  比较器,链接一个或多个比较器的序列
 * 
 * <p>复合比较器按顺序调用每个比较器,直到单个比较器返回非零结果,或比较器耗尽并返回零。
 * 
 *  <p>这有助于内存中排序,类似于SQL中的多列排序。列表中任何单个Comparator的顺序也可以颠倒
 * 
 * 
 * @author Keith Donald
 * @author Juergen Hoeller
 * @since 1.2.2
 */
@SuppressWarnings({ "serial", "rawtypes" })
public class CompoundComparator<T> implements Comparator<T>, Serializable {

	private final List<InvertibleComparator> comparators;


	/**
	 * Construct a CompoundComparator with initially no Comparators. Clients
	 * must add at least one Comparator before calling the compare method or an
	 * IllegalStateException is thrown.
	 * <p>
	 *  构造一个最初没有比较器的CompoundComparator客户端在调用compare方法之前必须至少添加一个Comparator,或者抛出IllegalStateException
	 * 
	 */
	public CompoundComparator() {
		this.comparators = new ArrayList<InvertibleComparator>();
	}

	/**
	 * Construct a CompoundComparator from the Comparators in the provided array.
	 * <p>All Comparators will default to ascending sort order,
	 * unless they are InvertibleComparators.
	 * <p>
	 *  从提供的数组中的Comparator构造一个CompoundComparator <p>所有比较器将默认为升序排列顺序,除非它们是InvertibleComparators
	 * 
	 * 
	 * @param comparators the comparators to build into a compound comparator
	 * @see InvertibleComparator
	 */
	@SuppressWarnings("unchecked")
	public CompoundComparator(Comparator... comparators) {
		Assert.notNull(comparators, "Comparators must not be null");
		this.comparators = new ArrayList<InvertibleComparator>(comparators.length);
		for (Comparator comparator : comparators) {
			this.addComparator(comparator);
		}
	}


	/**
	 * Add a Comparator to the end of the chain.
	 * <p>The Comparator will default to ascending sort order,
	 * unless it is a InvertibleComparator.
	 * <p>
	 *  将比较器添加到链的末尾<p>比较器将默认为升序排序顺序,除非它是InvertibleComparator
	 * 
	 * 
	 * @param comparator the Comparator to add to the end of the chain
	 * @see InvertibleComparator
	 */
	@SuppressWarnings("unchecked")
	public void addComparator(Comparator<? extends T> comparator) {
		if (comparator instanceof InvertibleComparator) {
			this.comparators.add((InvertibleComparator) comparator);
		}
		else {
			this.comparators.add(new InvertibleComparator(comparator));
		}
	}

	/**
	 * Add a Comparator to the end of the chain using the provided sort order.
	 * <p>
	 * 使用提供的排序顺序将比较器添加到链的末尾
	 * 
	 * 
	 * @param comparator the Comparator to add to the end of the chain
	 * @param ascending the sort order: ascending (true) or descending (false)
	 */
	@SuppressWarnings("unchecked")
	public void addComparator(Comparator<? extends T> comparator, boolean ascending) {
		this.comparators.add(new InvertibleComparator(comparator, ascending));
	}

	/**
	 * Replace the Comparator at the given index.
	 * <p>The Comparator will default to ascending sort order,
	 * unless it is a InvertibleComparator.
	 * <p>
	 *  将比较器替换为给定的索引<p>比较器将默认为升序排序顺序,除非是InvertibleComparator
	 * 
	 * 
	 * @param index the index of the Comparator to replace
	 * @param comparator the Comparator to place at the given index
	 * @see InvertibleComparator
	 */
	@SuppressWarnings("unchecked")
	public void setComparator(int index, Comparator<? extends T> comparator) {
		if (comparator instanceof InvertibleComparator) {
			this.comparators.set(index, (InvertibleComparator) comparator);
		}
		else {
			this.comparators.set(index, new InvertibleComparator(comparator));
		}
	}

	/**
	 * Replace the Comparator at the given index using the given sort order.
	 * <p>
	 *  使用给定的排序顺序替换给定索引的比较器
	 * 
	 * 
	 * @param index the index of the Comparator to replace
	 * @param comparator the Comparator to place at the given index
	 * @param ascending the sort order: ascending (true) or descending (false)
	 */
	public void setComparator(int index, Comparator<T> comparator, boolean ascending) {
		this.comparators.set(index, new InvertibleComparator<T>(comparator, ascending));
	}

	/**
	 * Invert the sort order of each sort definition contained by this compound
	 * comparator.
	 * <p>
	 *  反转此复合比较器所包含的每种排序定义的排序顺序
	 * 
	 */
	public void invertOrder() {
		for (InvertibleComparator comparator : this.comparators) {
			comparator.invertOrder();
		}
	}

	/**
	 * Invert the sort order of the sort definition at the specified index.
	 * <p>
	 *  在指定的索引处反转排序定义的排序顺序
	 * 
	 * 
	 * @param index the index of the comparator to invert
	 */
	public void invertOrder(int index) {
		this.comparators.get(index).invertOrder();
	}

	/**
	 * Change the sort order at the given index to ascending.
	 * <p>
	 *  将给定索引处的排序顺序更改为升序
	 * 
	 * 
	 * @param index the index of the comparator to change
	 */
	public void setAscendingOrder(int index) {
		this.comparators.get(index).setAscending(true);
	}

	/**
	 * Change the sort order at the given index to descending sort.
	 * <p>
	 *  将给定索引处的排序顺序更改为降序排序
	 * 
	 * 
	 * @param index the index of the comparator to change
	 */
	public void setDescendingOrder(int index) {
		this.comparators.get(index).setAscending(false);
	}

	/**
	 * Returns the number of aggregated comparators.
	 * <p>
	 *  返回聚合比较器的数量
	 */
	public int getComparatorCount() {
		return this.comparators.size();
	}

	@Override
	@SuppressWarnings("unchecked")
	public int compare(T o1, T o2) {
		Assert.state(this.comparators.size() > 0,
				"No sort definitions have been added to this CompoundComparator to compare");
		for (InvertibleComparator comparator : this.comparators) {
			int result = comparator.compare(o1, o2);
			if (result != 0) {
				return result;
			}
		}
		return 0;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof CompoundComparator)) {
			return false;
		}
		CompoundComparator<T> other = (CompoundComparator<T>) obj;
		return this.comparators.equals(other.comparators);
	}

	@Override
	public int hashCode() {
		return this.comparators.hashCode();
	}

	@Override
	public String toString() {
		return "CompoundComparator: " + this.comparators;
	}

}
