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

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.TypeConverter;
import org.springframework.core.GenericCollectionTypeResolver;

/**
 * Simple factory for shared Map instances. Allows for central setup
 * of Maps via the "map" element in XML bean definitions.
 *
 * <p>
 *  共享映射实例的简单工厂允许通过XML bean定义中的"map"元素进行中央设置
 * 
 * 
 * @author Juergen Hoeller
 * @since 09.12.2003
 * @see SetFactoryBean
 * @see ListFactoryBean
 */
public class MapFactoryBean extends AbstractFactoryBean<Map<Object, Object>> {

	private Map<?, ?> sourceMap;

	@SuppressWarnings("rawtypes")
	private Class<? extends Map> targetMapClass;


	/**
	 * Set the source Map, typically populated via XML "map" elements.
	 * <p>
	 *  设置源Map,通常通过XML"map"元素填充
	 * 
	 */
	public void setSourceMap(Map<?, ?> sourceMap) {
		this.sourceMap = sourceMap;
	}

	/**
	 * Set the class to use for the target Map. Can be populated with a fully
	 * qualified class name when defined in a Spring application context.
	 * <p>Default is a linked HashMap, keeping the registration order.
	 * <p>
	 * 设置用于目标的类Map当在Spring应用程序上下文中定义时,可以使用完全限定的类名填充<p>默认是一个链接的HashMap,保持注册顺序
	 * 
	 * @see java.util.LinkedHashMap
	 */
	@SuppressWarnings("rawtypes")
	public void setTargetMapClass(Class<? extends Map> targetMapClass) {
		if (targetMapClass == null) {
			throw new IllegalArgumentException("'targetMapClass' must not be null");
		}
		if (!Map.class.isAssignableFrom(targetMapClass)) {
			throw new IllegalArgumentException("'targetMapClass' must implement [java.util.Map]");
		}
		this.targetMapClass = targetMapClass;
	}


	@Override
	@SuppressWarnings("rawtypes")
	public Class<Map> getObjectType() {
		return Map.class;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Map<Object, Object> createInstance() {
		if (this.sourceMap == null) {
			throw new IllegalArgumentException("'sourceMap' is required");
		}
		Map<Object, Object> result = null;
		if (this.targetMapClass != null) {
			result = BeanUtils.instantiateClass(this.targetMapClass);
		}
		else {
			result = new LinkedHashMap<Object, Object>(this.sourceMap.size());
		}
		Class<?> keyType = null;
		Class<?> valueType = null;
		if (this.targetMapClass != null) {
			keyType = GenericCollectionTypeResolver.getMapKeyType(this.targetMapClass);
			valueType = GenericCollectionTypeResolver.getMapValueType(this.targetMapClass);
		}
		if (keyType != null || valueType != null) {
			TypeConverter converter = getBeanTypeConverter();
			for (Map.Entry<?, ?> entry : this.sourceMap.entrySet()) {
				Object convertedKey = converter.convertIfNecessary(entry.getKey(), keyType);
				Object convertedValue = converter.convertIfNecessary(entry.getValue(), valueType);
				result.put(convertedKey, convertedValue);
			}
		}
		else {
			result.putAll(this.sourceMap);
		}
		return result;
	}

}
