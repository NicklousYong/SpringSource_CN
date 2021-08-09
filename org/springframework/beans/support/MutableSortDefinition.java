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

import java.io.Serializable;

import org.springframework.util.StringUtils;

/**
 * Mutable implementation of the {@link SortDefinition} interface.
 * Supports toggling the ascending value on setting the same property again.
 *
 * <p>
 *  {@link SortDefinition}接口的可变实现支持在设置相同属性时重新启动升序值
 * 
 * 
 * @author Juergen Hoeller
 * @author Jean-Pierre Pawlak
 * @since 26.05.2003
 * @see #setToggleAscendingOnProperty
 */
@SuppressWarnings("serial")
public class MutableSortDefinition implements SortDefinition, Serializable {

	private String property = "";

	private boolean ignoreCase = true;

	private boolean ascending = true;

	private boolean toggleAscendingOnProperty = false;


	/**
	 * Create an empty MutableSortDefinition,
	 * to be populated via its bean properties.
	 * <p>
	 *  创建一个空的MutableSortDefinition,通过它的bean属性来填充
	 * 
	 * 
	 * @see #setProperty
	 * @see #setIgnoreCase
	 * @see #setAscending
	 */
	public MutableSortDefinition() {
	}

	/**
	 * Copy constructor: create a new MutableSortDefinition
	 * that mirrors the given sort definition.
	 * <p>
	 * 复制构造函数：创建一个新的MutableSortDefinition,它反映给定的排序定义
	 * 
	 * 
	 * @param source the original sort definition
	 */
	public MutableSortDefinition(SortDefinition source) {
		this.property = source.getProperty();
		this.ignoreCase = source.isIgnoreCase();
		this.ascending = source.isAscending();
	}

	/**
	 * Create a MutableSortDefinition for the given settings.
	 * <p>
	 *  为给定的设置创建一个MutableSortDefinition
	 * 
	 * 
	 * @param property the property to compare
	 * @param ignoreCase whether upper and lower case in String values should be ignored
	 * @param ascending whether to sort ascending (true) or descending (false)
	 */
	public MutableSortDefinition(String property, boolean ignoreCase, boolean ascending) {
		this.property = property;
		this.ignoreCase = ignoreCase;
		this.ascending = ascending;
	}

	/**
	 * Create a new MutableSortDefinition.
	 * <p>
	 *  创建一个新的MutableSortDefinition
	 * 
	 * 
	 * @param toggleAscendingOnSameProperty whether to toggle the ascending flag
	 * if the same property gets set again (that is, {@code setProperty} gets
	 * called with already set property name again).
	 */
	public MutableSortDefinition(boolean toggleAscendingOnSameProperty) {
		this.toggleAscendingOnProperty = toggleAscendingOnSameProperty;
	}


	/**
	 * Set the property to compare.
	 * <p>If the property was the same as the current, the sort is reversed if
	 * "toggleAscendingOnProperty" is activated, else simply ignored.
	 * <p>
	 *  将属性设置为比较<p>如果属性与当前属性相同,则如果激活"toggleAscendingOnProperty",则排序将反转,否则将被忽略
	 * 
	 * 
	 * @see #setToggleAscendingOnProperty
	 */
	public void setProperty(String property) {
		if (!StringUtils.hasLength(property)) {
			this.property = "";
		}
		else {
			// Implicit toggling of ascending?
			if (isToggleAscendingOnProperty()) {
				this.ascending = (!property.equals(this.property) || !this.ascending);
			}
			this.property = property;
		}
	}

	@Override
	public String getProperty() {
		return this.property;
	}

	/**
	 * Set whether upper and lower case in String values should be ignored.
	 * <p>
	 *  设置字符串值中的大小写是否应该被忽略
	 * 
	 */
	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

	@Override
	public boolean isIgnoreCase() {
		return this.ignoreCase;
	}

	/**
	 * Set whether to sort ascending (true) or descending (false).
	 * <p>
	 *  设置是排序升序(true)还是降序(false)
	 * 
	 */
	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	@Override
	public boolean isAscending() {
		return this.ascending;
	}

	/**
	 * Set whether to toggle the ascending flag if the same property gets set again
	 * (that is, {@link #setProperty} gets called with already set property name again).
	 * <p>This is particularly useful for parameter binding through a web request,
	 * where clicking on the field header again might be supposed to trigger a
	 * resort for the same field but opposite order.
	 * <p>
	 * 设置是否重新设置相同属性(即,{@link #setProperty}被再次调用已设置属性名称)的升序标志。
	 * <p>这对通过Web请求进行参数绑定特别有用,其中点击在场上的标题再次可能应该触发一个相同领域的手段,但是相反的顺序。
	 * 
	 */
	public void setToggleAscendingOnProperty(boolean toggleAscendingOnProperty) {
		this.toggleAscendingOnProperty = toggleAscendingOnProperty;
	}

	/**
	 * Return whether to toggle the ascending flag if the same property gets set again
	 * (that is, {@code setProperty} gets called with already set property name again).
	 * <p>
	 */
	public boolean isToggleAscendingOnProperty() {
		return this.toggleAscendingOnProperty;
	}


	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof SortDefinition)) {
			return false;
		}
		SortDefinition otherSd = (SortDefinition) other;
		return (getProperty().equals(otherSd.getProperty()) &&
				isAscending() == otherSd.isAscending() &&
				isIgnoreCase() == otherSd.isIgnoreCase());
	}

	@Override
	public int hashCode() {
		int hashCode = getProperty().hashCode();
		hashCode = 29 * hashCode + (isIgnoreCase() ? 1 : 0);
		hashCode = 29 * hashCode + (isAscending() ? 1 : 0);
		return hashCode;
	}

}
