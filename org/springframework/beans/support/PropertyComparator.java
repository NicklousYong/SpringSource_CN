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

package org.springframework.beans.support;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.util.StringUtils;

/**
 * PropertyComparator performs a comparison of two beans,
 * evaluating the specified bean property via a BeanWrapper.
 *
 * <p>
 *  PropertyComparator执行两个bean的比较,通过BeanWrapper评估指定的bean属性
 * 
 * 
 * @author Juergen Hoeller
 * @author Jean-Pierre Pawlak
 * @since 19.05.2003
 * @see org.springframework.beans.BeanWrapper
 */
public class PropertyComparator<T> implements Comparator<T> {

	protected final Log logger = LogFactory.getLog(getClass());

	private final SortDefinition sortDefinition;

	private final BeanWrapperImpl beanWrapper = new BeanWrapperImpl(false);


	/**
	 * Create a new PropertyComparator for the given SortDefinition.
	 * <p>
	 *  为给定的SortDefinition创建一个新的PropertyComparator
	 * 
	 * 
	 * @see MutableSortDefinition
	 */
	public PropertyComparator(SortDefinition sortDefinition) {
		this.sortDefinition = sortDefinition;
	}

	/**
	 * Create a PropertyComparator for the given settings.
	 * <p>
	 * 为给定的设置创建一个PropertyComparator
	 * 
	 * 
	 * @param property the property to compare
	 * @param ignoreCase whether upper and lower case in String values should be ignored
	 * @param ascending whether to sort ascending (true) or descending (false)
	 */
	public PropertyComparator(String property, boolean ignoreCase, boolean ascending) {
		this.sortDefinition = new MutableSortDefinition(property, ignoreCase, ascending);
	}

	/**
	 * Return the SortDefinition that this comparator uses.
	 * <p>
	 *  返回此比较器使用的SortDefinition
	 * 
	 */
	public final SortDefinition getSortDefinition() {
		return this.sortDefinition;
	}


	@Override
	@SuppressWarnings("unchecked")
	public int compare(T o1, T o2) {
		Object v1 = getPropertyValue(o1);
		Object v2 = getPropertyValue(o2);
		if (this.sortDefinition.isIgnoreCase() && (v1 instanceof String) && (v2 instanceof String)) {
			v1 = ((String) v1).toLowerCase();
			v2 = ((String) v2).toLowerCase();
		}

		int result;

		// Put an object with null property at the end of the sort result.
		try {
			if (v1 != null) {
				result = (v2 != null ? ((Comparable<Object>) v1).compareTo(v2) : -1);
			}
			else {
				result = (v2 != null ? 1 : 0);
			}
		}
		catch (RuntimeException ex) {
			if (logger.isWarnEnabled()) {
				logger.warn("Could not sort objects [" + o1 + "] and [" + o2 + "]", ex);
			}
			return 0;
		}

		return (this.sortDefinition.isAscending() ? result : -result);
	}

	/**
	 * Get the SortDefinition's property value for the given object.
	 * <p>
	 *  获取给定对象的SortDefinition属性值
	 * 
	 * 
	 * @param obj the object to get the property value for
	 * @return the property value
	 */
	private Object getPropertyValue(Object obj) {
		// If a nested property cannot be read, simply return null
		// (similar to JSTL EL). If the property doesn't exist in the
		// first place, let the exception through.
		try {
			this.beanWrapper.setWrappedInstance(obj);
			return this.beanWrapper.getPropertyValue(this.sortDefinition.getProperty());
		}
		catch (BeansException ex) {
			logger.info("PropertyComparator could not access property - treating as null for sorting", ex);
			return null;
		}
	}


	/**
	 * Sort the given List according to the given sort definition.
	 * <p>Note: Contained objects have to provide the given property
	 * in the form of a bean property, i.e. a getXXX method.
	 * <p>
	 *  按照给定的排序定义排序给定的列表<p>注意：包含的对象必须以bean属性的形式提供给定的属性,即getXXX方法
	 * 
	 * 
	 * @param source the input List
	 * @param sortDefinition the parameters to sort by
	 * @throws java.lang.IllegalArgumentException in case of a missing propertyName
	 */
	public static void sort(List<?> source, SortDefinition sortDefinition) throws BeansException {
		if (StringUtils.hasText(sortDefinition.getProperty())) {
			Collections.sort(source, new PropertyComparator<Object>(sortDefinition));
		}
	}

	/**
	 * Sort the given source according to the given sort definition.
	 * <p>Note: Contained objects have to provide the given property
	 * in the form of a bean property, i.e. a getXXX method.
	 * <p>
	 *  根据给定的排序定义对给定的源进行排序<p>注意：包含的对象必须以bean属性的形式提供给定的属性,即getXXX方法
	 * 
	 * @param source input source
	 * @param sortDefinition the parameters to sort by
	 * @throws java.lang.IllegalArgumentException in case of a missing propertyName
	 */
	public static void sort(Object[] source, SortDefinition sortDefinition) throws BeansException {
		if (StringUtils.hasText(sortDefinition.getProperty())) {
			Arrays.sort(source, new PropertyComparator<Object>(sortDefinition));
		}
	}

}
