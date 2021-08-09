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

package org.springframework.beans.factory.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.TypeConverter;
import org.springframework.core.GenericCollectionTypeResolver;

/**
 * Simple factory for shared List instances. Allows for central setup
 * of Lists via the "list" element in XML bean definitions.
 *
 * <p>
 *  共享列表实例的简单工厂允许通过XML bean定义中的"列表"元素集中设置列表
 * 
 * 
 * @author Juergen Hoeller
 * @since 09.12.2003
 * @see SetFactoryBean
 * @see MapFactoryBean
 */
public class ListFactoryBean extends AbstractFactoryBean<List<Object>> {

	private List<?> sourceList;

	@SuppressWarnings("rawtypes")
	private Class<? extends List> targetListClass;


	/**
	 * Set the source List, typically populated via XML "list" elements.
	 * <p>
	 *  设置源列表,通常通过XML"列表"元素填充
	 * 
	 */
	public void setSourceList(List<?> sourceList) {
		this.sourceList = sourceList;
	}

	/**
	 * Set the class to use for the target List. Can be populated with a fully
	 * qualified class name when defined in a Spring application context.
	 * <p>Default is a {@code java.util.ArrayList}.
	 * <p>
	 * 设置用于目标的类List当在Spring应用程序上下文中定义时,可以使用完全限定的类名填充<p> Default是一个{@code javautilArrayList}
	 * 
	 * @see java.util.ArrayList
	 */
	@SuppressWarnings("rawtypes")
	public void setTargetListClass(Class<? extends List> targetListClass) {
		if (targetListClass == null) {
			throw new IllegalArgumentException("'targetListClass' must not be null");
		}
		if (!List.class.isAssignableFrom(targetListClass)) {
			throw new IllegalArgumentException("'targetListClass' must implement [java.util.List]");
		}
		this.targetListClass = targetListClass;
	}


	@Override
	@SuppressWarnings("rawtypes")
	public Class<List> getObjectType() {
		return List.class;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected List<Object> createInstance() {
		if (this.sourceList == null) {
			throw new IllegalArgumentException("'sourceList' is required");
		}
		List<Object> result = null;
		if (this.targetListClass != null) {
			result = BeanUtils.instantiateClass(this.targetListClass);
		}
		else {
			result = new ArrayList<Object>(this.sourceList.size());
		}
		Class<?> valueType = null;
		if (this.targetListClass != null) {
			valueType = GenericCollectionTypeResolver.getCollectionType(this.targetListClass);
		}
		if (valueType != null) {
			TypeConverter converter = getBeanTypeConverter();
			for (Object elem : this.sourceList) {
				result.add(converter.convertIfNecessary(elem, valueType));
			}
		}
		else {
			result.addAll(this.sourceList);
		}
		return result;
	}

}
