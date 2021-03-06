/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2008 the original author or authors.
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

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.TypeConverter;
import org.springframework.core.GenericCollectionTypeResolver;

/**
 * Simple factory for shared Set instances. Allows for central setup
 * of Sets via the "set" element in XML bean definitions.
 *
 * <p>
 *  共享集实例的简单工厂允许通过XML bean定义中的"set"元素进行集中设置
 * 
 * 
 * @author Juergen Hoeller
 * @since 09.12.2003
 * @see ListFactoryBean
 * @see MapFactoryBean
 */
public class SetFactoryBean extends AbstractFactoryBean<Set<Object>> {

	private Set<?> sourceSet;

	@SuppressWarnings("rawtypes")
	private Class<? extends Set> targetSetClass;


	/**
	 * Set the source Set, typically populated via XML "set" elements.
	 * <p>
	 *  设置源集,通常通过XML"set"元素填充
	 * 
	 */
	public void setSourceSet(Set<?> sourceSet) {
		this.sourceSet = sourceSet;
	}

	/**
	 * Set the class to use for the target Set. Can be populated with a fully
	 * qualified class name when defined in a Spring application context.
	 * <p>Default is a linked HashSet, keeping the registration order.
	 * <p>
	 * 设置用于目标的类Set当在Spring应用程序上下文中定义时,可以使用完全限定类名填充<p>默认是一个链接的HashSet,保持注册顺序
	 * 
	 * @see java.util.LinkedHashSet
	 */
	@SuppressWarnings("rawtypes")
	public void setTargetSetClass(Class<? extends Set> targetSetClass) {
		if (targetSetClass == null) {
			throw new IllegalArgumentException("'targetSetClass' must not be null");
		}
		if (!Set.class.isAssignableFrom(targetSetClass)) {
			throw new IllegalArgumentException("'targetSetClass' must implement [java.util.Set]");
		}
		this.targetSetClass = targetSetClass;
	}


	@Override
	@SuppressWarnings("rawtypes")
	public Class<Set> getObjectType() {
		return Set.class;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Set<Object> createInstance() {
		if (this.sourceSet == null) {
			throw new IllegalArgumentException("'sourceSet' is required");
		}
		Set<Object> result = null;
		if (this.targetSetClass != null) {
			result = BeanUtils.instantiateClass(this.targetSetClass);
		}
		else {
			result = new LinkedHashSet<Object>(this.sourceSet.size());
		}
		Class<?> valueType = null;
		if (this.targetSetClass != null) {
			valueType = GenericCollectionTypeResolver.getCollectionType(this.targetSetClass);
		}
		if (valueType != null) {
			TypeConverter converter = getBeanTypeConverter();
			for (Object elem : this.sourceSet) {
				result.add(converter.convertIfNecessary(elem, valueType));
			}
		}
		else {
			result.addAll(this.sourceSet);
		}
		return result;
	}

}
