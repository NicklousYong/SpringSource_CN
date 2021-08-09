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

package org.springframework.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.util.ObjectUtils;

/**
 * {@link Comparator} implementation for {@link Ordered} objects, sorting
 * by order value ascending, respectively by priority descending.
 *
 * <h3>Same Order Objects</h3>
 * <p>Objects that have the same order value will be sorted with arbitrary
 * ordering with respect to other objects with the same order value.
 *
 * <h3>Non-ordered Objects</h3>
 * <p>Any object that does not provide its own order value is implicitly
 * assigned a value of {@link Ordered#LOWEST_PRECEDENCE}, thus ending up
 * at the end of a sorted collection in arbitrary order with respect to
 * other objects with the same order value.
 *
 * <p>
 *  针对{@link Ordered}对象的{@link比较器}实现,按顺序排序升序,分别按优先级降序排列
 * 
 * <h3>相同顺序对象</h3> <p>具有相同顺序值的对象将按照相同顺序值的其他对象的任意排序进行排序
 * 
 *  <h3>非有序对象</h3> <p>任何不提供自己的订单值的对象都隐含地分配了一个值{@link Ordered#LOWEST_PRECEDENCE},因此结束于任意排序集合的末尾相对于具有相同顺序值
 * 的其他对象的顺序。
 * 
 * 
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 07.04.2003
 * @see Ordered
 * @see org.springframework.core.annotation.AnnotationAwareOrderComparator
 * @see java.util.Collections#sort(java.util.List, java.util.Comparator)
 * @see java.util.Arrays#sort(Object[], java.util.Comparator)
 */
public class OrderComparator implements Comparator<Object> {

	/**
	 * Shared default instance of {@code OrderComparator}.
	 * <p>
	 *  {@code OrderComparator}的共享默认实例
	 * 
	 */
	public static final OrderComparator INSTANCE = new OrderComparator();


	/**
	 * Build an adapted order comparator with the given source provider.
	 * <p>
	 *  使用给定的源提供程序构建一个适应的顺序比较器
	 * 
	 * 
	 * @param sourceProvider the order source provider to use
	 * @return the adapted comparator
	 * @since 4.1
	 */
	public Comparator<Object> withSourceProvider(final OrderSourceProvider sourceProvider) {
		return new Comparator<Object>() {
			@Override
			public int compare(Object o1, Object o2) {
				return doCompare(o1, o2, sourceProvider);
			}
		};
	}

	@Override
	public int compare(Object o1, Object o2) {
		return doCompare(o1, o2, null);
	}

	private int doCompare(Object o1, Object o2, OrderSourceProvider sourceProvider) {
		boolean p1 = (o1 instanceof PriorityOrdered);
		boolean p2 = (o2 instanceof PriorityOrdered);
		if (p1 && !p2) {
			return -1;
		}
		else if (p2 && !p1) {
			return 1;
		}

		// Direct evaluation instead of Integer.compareTo to avoid unnecessary object creation.
		int i1 = getOrder(o1, sourceProvider);
		int i2 = getOrder(o2, sourceProvider);
		return (i1 < i2) ? -1 : (i1 > i2) ? 1 : 0;
	}

	/**
	 * Determine the order value for the given object.
	 * <p>The default implementation checks against the given {@link OrderSourceProvider}
	 * using {@link #findOrder} and falls back to a regular {@link #getOrder(Object)} call.
	 * <p>
	 *  确定给定对象的顺序值<p>默认实现使用{@link #findOrder}针对给定的{@link OrderSourceProvider}进行检查,并返回常规{@link #getOrder(Object)}
	 * 调用。
	 * 
	 * 
	 * @param obj the object to check
	 * @return the order value, or {@code Ordered.LOWEST_PRECEDENCE} as fallback
	 */
	private int getOrder(Object obj, OrderSourceProvider sourceProvider) {
		Integer order = null;
		if (sourceProvider != null) {
			Object orderSource = sourceProvider.getOrderSource(obj);
			if (orderSource != null && orderSource.getClass().isArray()) {
				Object[] sources = ObjectUtils.toObjectArray(orderSource);
				for (Object source : sources) {
					order = findOrder(source);
					if (order != null) {
						break;
					}
				}
			}
			else {
				order = findOrder(orderSource);
			}
		}
		return (order != null ? order : getOrder(obj));
	}

	/**
	 * Determine the order value for the given object.
	 * <p>The default implementation checks against the {@link Ordered} interface
	 * through delegating to {@link #findOrder}. Can be overridden in subclasses.
	 * <p>
	 * 确定给定对象的订单值<p>默认实现通过委托{@link #findOrder}针对{@link Ordered}接口进行检查可以在子类中覆盖
	 * 
	 * 
	 * @param obj the object to check
	 * @return the order value, or {@code Ordered.LOWEST_PRECEDENCE} as fallback
	 */
	protected int getOrder(Object obj) {
		Integer order = findOrder(obj);
		return (order != null ? order : Ordered.LOWEST_PRECEDENCE);
	}

	/**
	 * Find an order value indicated by the given object.
	 * <p>The default implementation checks against the {@link Ordered} interface.
	 * Can be overridden in subclasses.
	 * <p>
	 *  查找给定对象指示的订单值<p>默认实现对{@link Ordered}接口进行检查可以在子类中覆盖
	 * 
	 * 
	 * @param obj the object to check
	 * @return the order value, or {@code null} if none found
	 */
	protected Integer findOrder(Object obj) {
		return (obj instanceof Ordered ? ((Ordered) obj).getOrder() : null);
	}

	/**
	 * Determine a priority value for the given object, if any.
	 * <p>The default implementation always returns {@code null}.
	 * Subclasses may override this to give specific kinds of values a
	 * 'priority' characteristic, in addition to their 'order' semantics.
	 * A priority indicates that it may be used for selecting one object over
	 * another, in addition to serving for ordering purposes in a list/array.
	 * <p>
	 *  确定给定对象的优先级值,如果有的话,默认实现总是返回{@code null}子类可以覆盖它,以给出特定类型的值"优先级"特性,除了它们的"顺序"语义优先级表示它可以用于在另一个上选择一个对象,以及用于
	 * 列表/数组中的排序目的。
	 * 
	 * 
	 * @param obj the object to check
	 * @return the priority value, or {@code null} if none
	 * @since 4.1
	 */
	public Integer getPriority(Object obj) {
		return null;
	}


	/**
	 * Sort the given List with a default OrderComparator.
	 * <p>Optimized to skip sorting for lists with size 0 or 1,
	 * in order to avoid unnecessary array extraction.
	 * <p>
	 * 使用默认的OrderComparator对给定的列进行排序<p>针对大小为0或1的列表优化排序,以避免不必要的数组提取
	 * 
	 * 
	 * @param list the List to sort
	 * @see java.util.Collections#sort(java.util.List, java.util.Comparator)
	 */
	public static void sort(List<?> list) {
		if (list.size() > 1) {
			Collections.sort(list, INSTANCE);
		}
	}

	/**
	 * Sort the given array with a default OrderComparator.
	 * <p>Optimized to skip sorting for lists with size 0 or 1,
	 * in order to avoid unnecessary array extraction.
	 * <p>
	 *  使用默认的OrderComparator对给定的数组进行排序<p>针对大小为0或1的列表进行优化,以避免不必要的数组提取
	 * 
	 * 
	 * @param array the array to sort
	 * @see java.util.Arrays#sort(Object[], java.util.Comparator)
	 */
	public static void sort(Object[] array) {
		if (array.length > 1) {
			Arrays.sort(array, INSTANCE);
		}
	}

	/**
	 * Sort the given array or List with a default OrderComparator,
	 * if necessary. Simply skips sorting when given any other value.
	 * <p>Optimized to skip sorting for lists with size 0 or 1,
	 * in order to avoid unnecessary array extraction.
	 * <p>
	 *  使用默认的OrderComparator对给定的数组或List进行排序只要在给定任何其他值时就可以跳过排序<p>针对大小为0或1的列表进行优化以跳过排序,以避免不必要的数组提取
	 * 
	 * 
	 * @param value the array or List to sort
	 * @see java.util.Arrays#sort(Object[], java.util.Comparator)
	 */
	public static void sortIfNecessary(Object value) {
		if (value instanceof Object[]) {
			sort((Object[]) value);
		}
		else if (value instanceof List) {
			sort((List<?>) value);
		}
	}


	/**
	 * Strategy interface to provide an order source for a given object.
	 * <p>
	 *  策略界面提供给定对象的订单源
	 * 
	 * 
	 * @since 4.1
	 */
	public interface OrderSourceProvider {

		/**
		 * Return an order source for the specified object, i.e. an object that
		 * should be checked for an order value as a replacement to the given object.
		 * <p>Can also be an array of order source objects.
		 * <p>If the returned object does not indicate any order, the comparator
		 * will fall back to checking the original object.
		 * <p>
		 * 返回指定对象的订单源,即应检查订单值作为给定对象的替换的对象<p>也可以是订单源对象的数组<p>如果返回的对象不指示任何比较器将退回检查原始对象
		 * 
		 * @param obj the object to find an order source for
		 * @return the order source for that object, or {@code null} if none found
		 */
		Object getOrderSource(Object obj);
	}

}
