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

package org.springframework.beans.factory.config;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.Mergeable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

/**
 * Holder for constructor argument values, typically as part of a bean definition.
 *
 * <p>Supports values for a specific index in the constructor argument list
 * as well as for generic argument matches by type.
 *
 * <p>
 *  构造函数参数值的Holder,通常作为bean定义的一部分
 * 
 *  <p>支持构造函数参数列表中特定索引的值以及类型的泛型参数匹配
 * 
 * 
 * @author Juergen Hoeller
 * @since 09.11.2003
 * @see BeanDefinition#getConstructorArgumentValues
 */
public class ConstructorArgumentValues {

	private final Map<Integer, ValueHolder> indexedArgumentValues = new LinkedHashMap<Integer, ValueHolder>(0);

	private final List<ValueHolder> genericArgumentValues = new LinkedList<ValueHolder>();


	/**
	 * Create a new empty ConstructorArgumentValues object.
	 * <p>
	 * 创建一个新的空的ConstructorArgumentValues对象
	 * 
	 */
	public ConstructorArgumentValues() {
	}

	/**
	 * Deep copy constructor.
	 * <p>
	 *  深层复制构造函数
	 * 
	 * 
	 * @param original the ConstructorArgumentValues to copy
	 */
	public ConstructorArgumentValues(ConstructorArgumentValues original) {
		addArgumentValues(original);
	}


	/**
	 * Copy all given argument values into this object, using separate holder
	 * instances to keep the values independent from the original object.
	 * <p>Note: Identical ValueHolder instances will only be registered once,
	 * to allow for merging and re-merging of argument value definitions. Distinct
	 * ValueHolder instances carrying the same content are of course allowed.
	 * <p>
	 *  将所有给定的参数值复制到此对象中,使用单独的保持器实例来保持值与原始对象的独立性<p>注意：相同的ValueHolder实例只会被注册一次,以允许合并和重新合并参数值定义Distinct ValueH
	 * older当然允许携带相同内容的实例。
	 * 
	 */
	public void addArgumentValues(ConstructorArgumentValues other) {
		if (other != null) {
			for (Map.Entry<Integer, ValueHolder> entry : other.indexedArgumentValues.entrySet()) {
				addOrMergeIndexedArgumentValue(entry.getKey(), entry.getValue().copy());
			}
			for (ValueHolder valueHolder : other.genericArgumentValues) {
				if (!this.genericArgumentValues.contains(valueHolder)) {
					addOrMergeGenericArgumentValue(valueHolder.copy());
				}
			}
		}
	}


	/**
	 * Add an argument value for the given index in the constructor argument list.
	 * <p>
	 *  在构造函数参数列表中添加给定索引的参数值
	 * 
	 * 
	 * @param index the index in the constructor argument list
	 * @param value the argument value
	 */
	public void addIndexedArgumentValue(int index, Object value) {
		addIndexedArgumentValue(index, new ValueHolder(value));
	}

	/**
	 * Add an argument value for the given index in the constructor argument list.
	 * <p>
	 *  在构造函数参数列表中添加给定索引的参数值
	 * 
	 * 
	 * @param index the index in the constructor argument list
	 * @param value the argument value
	 * @param type the type of the constructor argument
	 */
	public void addIndexedArgumentValue(int index, Object value, String type) {
		addIndexedArgumentValue(index, new ValueHolder(value, type));
	}

	/**
	 * Add an argument value for the given index in the constructor argument list.
	 * <p>
	 *  在构造函数参数列表中添加给定索引的参数值
	 * 
	 * 
	 * @param index the index in the constructor argument list
	 * @param newValue the argument value in the form of a ValueHolder
	 */
	public void addIndexedArgumentValue(int index, ValueHolder newValue) {
		Assert.isTrue(index >= 0, "Index must not be negative");
		Assert.notNull(newValue, "ValueHolder must not be null");
		addOrMergeIndexedArgumentValue(index, newValue);
	}

	/**
	 * Add an argument value for the given index in the constructor argument list,
	 * merging the new value (typically a collection) with the current value
	 * if demanded: see {@link org.springframework.beans.Mergeable}.
	 * <p>
	 * 在构造函数参数列表中添加给定索引的参数值,如果需要,将新值(通常为集合)与当前值合并,请参阅{@link orgspringframeworkbeansMergeable}
	 * 
	 * 
	 * @param key the index in the constructor argument list
	 * @param newValue the argument value in the form of a ValueHolder
	 */
	private void addOrMergeIndexedArgumentValue(Integer key, ValueHolder newValue) {
		ValueHolder currentValue = this.indexedArgumentValues.get(key);
		if (currentValue != null && newValue.getValue() instanceof Mergeable) {
			Mergeable mergeable = (Mergeable) newValue.getValue();
			if (mergeable.isMergeEnabled()) {
				newValue.setValue(mergeable.merge(currentValue.getValue()));
			}
		}
		this.indexedArgumentValues.put(key, newValue);
	}

	/**
	 * Check whether an argument value has been registered for the given index.
	 * <p>
	 *  检查给定索引是否已注册参数值
	 * 
	 * 
	 * @param index the index in the constructor argument list
	 */
	public boolean hasIndexedArgumentValue(int index) {
		return this.indexedArgumentValues.containsKey(index);
	}

	/**
	 * Get argument value for the given index in the constructor argument list.
	 * <p>
	 *  在构造函数参数列表中获取给定索引的参数值
	 * 
	 * 
	 * @param index the index in the constructor argument list
	 * @param requiredType the type to match (can be {@code null} to match
	 * untyped values only)
	 * @return the ValueHolder for the argument, or {@code null} if none set
	 */
	public ValueHolder getIndexedArgumentValue(int index, Class<?> requiredType) {
		return getIndexedArgumentValue(index, requiredType, null);
	}

	/**
	 * Get argument value for the given index in the constructor argument list.
	 * <p>
	 *  在构造函数参数列表中获取给定索引的参数值
	 * 
	 * 
	 * @param index the index in the constructor argument list
	 * @param requiredType the type to match (can be {@code null} to match
	 * untyped values only)
	 * @param requiredName the type to match (can be {@code null} to match
	 * unnamed values only, or empty String to match any name)
	 * @return the ValueHolder for the argument, or {@code null} if none set
	 */
	public ValueHolder getIndexedArgumentValue(int index, Class<?> requiredType, String requiredName) {
		Assert.isTrue(index >= 0, "Index must not be negative");
		ValueHolder valueHolder = this.indexedArgumentValues.get(index);
		if (valueHolder != null &&
				(valueHolder.getType() == null ||
						(requiredType != null && ClassUtils.matchesTypeName(requiredType, valueHolder.getType()))) &&
				(valueHolder.getName() == null || "".equals(requiredName) ||
						(requiredName != null && requiredName.equals(valueHolder.getName())))) {
			return valueHolder;
		}
		return null;
	}

	/**
	 * Return the map of indexed argument values.
	 * <p>
	 *  返回索引参数值的地图
	 * 
	 * 
	 * @return unmodifiable Map with Integer index as key and ValueHolder as value
	 * @see ValueHolder
	 */
	public Map<Integer, ValueHolder> getIndexedArgumentValues() {
		return Collections.unmodifiableMap(this.indexedArgumentValues);
	}


	/**
	 * Add a generic argument value to be matched by type.
	 * <p>Note: A single generic argument value will just be used once,
	 * rather than matched multiple times.
	 * <p>
	 *  添加通过类型<p>匹配的泛型参数值注意：单个泛型参数值将仅使用一次,而不是多次匹配
	 * 
	 * 
	 * @param value the argument value
	 */
	public void addGenericArgumentValue(Object value) {
		this.genericArgumentValues.add(new ValueHolder(value));
	}

	/**
	 * Add a generic argument value to be matched by type.
	 * <p>Note: A single generic argument value will just be used once,
	 * rather than matched multiple times.
	 * <p>
	 *  添加通过类型<p>匹配的泛型参数值注意：单个泛型参数值将仅使用一次,而不是多次匹配
	 * 
	 * 
	 * @param value the argument value
	 * @param type the type of the constructor argument
	 */
	public void addGenericArgumentValue(Object value, String type) {
		this.genericArgumentValues.add(new ValueHolder(value, type));
	}

	/**
	 * Add a generic argument value to be matched by type or name (if available).
	 * <p>Note: A single generic argument value will just be used once,
	 * rather than matched multiple times.
	 * <p>
	 * 添加通过类型或名称匹配的通用参数值(如果可用)<p>注意：单个泛型参数值将仅使用一次,而不是多次匹配
	 * 
	 * 
	 * @param newValue the argument value in the form of a ValueHolder
	 * <p>Note: Identical ValueHolder instances will only be registered once,
	 * to allow for merging and re-merging of argument value definitions. Distinct
	 * ValueHolder instances carrying the same content are of course allowed.
	 */
	public void addGenericArgumentValue(ValueHolder newValue) {
		Assert.notNull(newValue, "ValueHolder must not be null");
		if (!this.genericArgumentValues.contains(newValue)) {
			addOrMergeGenericArgumentValue(newValue);
		}
	}

	/**
	 * Add a generic argument value, merging the new value (typically a collection)
	 * with the current value if demanded: see {@link org.springframework.beans.Mergeable}.
	 * <p>
	 *  添加通用参数值,将新值(通常为集合)与当前值(如果需要)合并：请参阅{@link orgspringframeworkbeansMergeable}
	 * 
	 * 
	 * @param newValue the argument value in the form of a ValueHolder
	 */
	private void addOrMergeGenericArgumentValue(ValueHolder newValue) {
		if (newValue.getName() != null) {
			for (Iterator<ValueHolder> it = this.genericArgumentValues.iterator(); it.hasNext();) {
				ValueHolder currentValue = it.next();
				if (newValue.getName().equals(currentValue.getName())) {
					if (newValue.getValue() instanceof Mergeable) {
						Mergeable mergeable = (Mergeable) newValue.getValue();
						if (mergeable.isMergeEnabled()) {
							newValue.setValue(mergeable.merge(currentValue.getValue()));
						}
					}
					it.remove();
				}
			}
		}
		this.genericArgumentValues.add(newValue);
	}

	/**
	 * Look for a generic argument value that matches the given type.
	 * <p>
	 *  寻找与给定类型匹配的泛型参数值
	 * 
	 * 
	 * @param requiredType the type to match
	 * @return the ValueHolder for the argument, or {@code null} if none set
	 */
	public ValueHolder getGenericArgumentValue(Class<?> requiredType) {
		return getGenericArgumentValue(requiredType, null, null);
	}

	/**
	 * Look for a generic argument value that matches the given type.
	 * <p>
	 *  寻找与给定类型匹配的泛型参数值
	 * 
	 * 
	 * @param requiredType the type to match
	 * @param requiredName the name to match
	 * @return the ValueHolder for the argument, or {@code null} if none set
	 */
	public ValueHolder getGenericArgumentValue(Class<?> requiredType, String requiredName) {
		return getGenericArgumentValue(requiredType, requiredName, null);
	}

	/**
	 * Look for the next generic argument value that matches the given type,
	 * ignoring argument values that have already been used in the current
	 * resolution process.
	 * <p>
	 *  查找与给定类型匹配的下一个泛型参数值,忽略已在当前解析过程中使用的参数值
	 * 
	 * 
	 * @param requiredType the type to match (can be {@code null} to find
	 * an arbitrary next generic argument value)
	 * @param requiredName the name to match (can be {@code null} to not
	 * match argument values by name, or empty String to match any name)
	 * @param usedValueHolders a Set of ValueHolder objects that have already been used
	 * in the current resolution process and should therefore not be returned again
	 * @return the ValueHolder for the argument, or {@code null} if none found
	 */
	public ValueHolder getGenericArgumentValue(Class<?> requiredType, String requiredName, Set<ValueHolder> usedValueHolders) {
		for (ValueHolder valueHolder : this.genericArgumentValues) {
			if (usedValueHolders != null && usedValueHolders.contains(valueHolder)) {
				continue;
			}
			if (valueHolder.getName() != null && !"".equals(requiredName) &&
					(requiredName == null || !valueHolder.getName().equals(requiredName))) {
				continue;
			}
			if (valueHolder.getType() != null &&
					(requiredType == null || !ClassUtils.matchesTypeName(requiredType, valueHolder.getType()))) {
				continue;
			}
			if (requiredType != null && valueHolder.getType() == null && valueHolder.getName() == null &&
					!ClassUtils.isAssignableValue(requiredType, valueHolder.getValue())) {
				continue;
			}
			return valueHolder;
		}
		return null;
	}

	/**
	 * Return the list of generic argument values.
	 * <p>
	 *  返回泛型参数值的列表
	 * 
	 * 
	 * @return unmodifiable List of ValueHolders
	 * @see ValueHolder
	 */
	public List<ValueHolder> getGenericArgumentValues() {
		return Collections.unmodifiableList(this.genericArgumentValues);
	}


	/**
	 * Look for an argument value that either corresponds to the given index
	 * in the constructor argument list or generically matches by type.
	 * <p>
	 * 查找与构造函数参数列表中的给定索引对应的参数值,或者按类型一致地匹配
	 * 
	 * 
	 * @param index the index in the constructor argument list
	 * @param requiredType the parameter type to match
	 * @return the ValueHolder for the argument, or {@code null} if none set
	 */
	public ValueHolder getArgumentValue(int index, Class<?> requiredType) {
		return getArgumentValue(index, requiredType, null, null);
	}

	/**
	 * Look for an argument value that either corresponds to the given index
	 * in the constructor argument list or generically matches by type.
	 * <p>
	 *  查找与构造函数参数列表中的给定索引对应的参数值,或者按类型一致地匹配
	 * 
	 * 
	 * @param index the index in the constructor argument list
	 * @param requiredType the parameter type to match
	 * @param requiredName the parameter name to match
	 * @return the ValueHolder for the argument, or {@code null} if none set
	 */
	public ValueHolder getArgumentValue(int index, Class<?> requiredType, String requiredName) {
		return getArgumentValue(index, requiredType, requiredName, null);
	}

	/**
	 * Look for an argument value that either corresponds to the given index
	 * in the constructor argument list or generically matches by type.
	 * <p>
	 *  查找与构造函数参数列表中的给定索引对应的参数值,或者按类型一致地匹配
	 * 
	 * 
	 * @param index the index in the constructor argument list
	 * @param requiredType the parameter type to match (can be {@code null}
	 * to find an untyped argument value)
	 * @param requiredName the parameter name to match (can be {@code null}
	 * to find an unnamed argument value, or empty String to match any name)
	 * @param usedValueHolders a Set of ValueHolder objects that have already
	 * been used in the current resolution process and should therefore not
	 * be returned again (allowing to return the next generic argument match
	 * in case of multiple generic argument values of the same type)
	 * @return the ValueHolder for the argument, or {@code null} if none set
	 */
	public ValueHolder getArgumentValue(int index, Class<?> requiredType, String requiredName, Set<ValueHolder> usedValueHolders) {
		Assert.isTrue(index >= 0, "Index must not be negative");
		ValueHolder valueHolder = getIndexedArgumentValue(index, requiredType, requiredName);
		if (valueHolder == null) {
			valueHolder = getGenericArgumentValue(requiredType, requiredName, usedValueHolders);
		}
		return valueHolder;
	}

	/**
	 * Return the number of argument values held in this instance,
	 * counting both indexed and generic argument values.
	 * <p>
	 *  返回在此实例中保留的参数值的数量,计数索引和通用参数值
	 * 
	 */
	public int getArgumentCount() {
		return (this.indexedArgumentValues.size() + this.genericArgumentValues.size());
	}

	/**
	 * Return if this holder does not contain any argument values,
	 * neither indexed ones nor generic ones.
	 * <p>
	 *  如果此持有者不包含任何参数值,则不返回索引值,也不包含通用值
	 * 
	 */
	public boolean isEmpty() {
		return (this.indexedArgumentValues.isEmpty() && this.genericArgumentValues.isEmpty());
	}

	/**
	 * Clear this holder, removing all argument values.
	 * <p>
	 *  清除此持有人,删除所有参数值
	 * 
	 */
	public void clear() {
		this.indexedArgumentValues.clear();
		this.genericArgumentValues.clear();
	}


	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ConstructorArgumentValues)) {
			return false;
		}
		ConstructorArgumentValues that = (ConstructorArgumentValues) other;
		if (this.genericArgumentValues.size() != that.genericArgumentValues.size() ||
				this.indexedArgumentValues.size() != that.indexedArgumentValues.size()) {
			return false;
		}
		Iterator<ValueHolder> it1 = this.genericArgumentValues.iterator();
		Iterator<ValueHolder> it2 = that.genericArgumentValues.iterator();
		while (it1.hasNext() && it2.hasNext()) {
			ValueHolder vh1 = it1.next();
			ValueHolder vh2 = it2.next();
			if (!vh1.contentEquals(vh2)) {
				return false;
			}
		}
		for (Map.Entry<Integer, ValueHolder> entry : this.indexedArgumentValues.entrySet()) {
			ValueHolder vh1 = entry.getValue();
			ValueHolder vh2 = that.indexedArgumentValues.get(entry.getKey());
			if (!vh1.contentEquals(vh2)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hashCode = 7;
		for (ValueHolder valueHolder : this.genericArgumentValues) {
			hashCode = 31 * hashCode + valueHolder.contentHashCode();
		}
		hashCode = 29 * hashCode;
		for (Map.Entry<Integer, ValueHolder> entry : this.indexedArgumentValues.entrySet()) {
			hashCode = 31 * hashCode + (entry.getValue().contentHashCode() ^ entry.getKey().hashCode());
		}
		return hashCode;
	}


	/**
	 * Holder for a constructor argument value, with an optional type
	 * attribute indicating the target type of the actual constructor argument.
	 * <p>
	 * 一个构造函数参数值的持有者,一个可选的type属性指示实际的构造函数参数的目标类型
	 * 
	 */
	public static class ValueHolder implements BeanMetadataElement {

		private Object value;

		private String type;

		private String name;

		private Object source;

		private boolean converted = false;

		private Object convertedValue;

		/**
		 * Create a new ValueHolder for the given value.
		 * <p>
		 *  为给定的值创建一个新的ValueHolder
		 * 
		 * 
		 * @param value the argument value
		 */
		public ValueHolder(Object value) {
			this.value = value;
		}

		/**
		 * Create a new ValueHolder for the given value and type.
		 * <p>
		 *  为给定的值和类型创建一个新的ValueHolder
		 * 
		 * 
		 * @param value the argument value
		 * @param type the type of the constructor argument
		 */
		public ValueHolder(Object value, String type) {
			this.value = value;
			this.type = type;
		}

		/**
		 * Create a new ValueHolder for the given value, type and name.
		 * <p>
		 *  为给定的值,类型和名称创建一个新的ValueHolder
		 * 
		 * 
		 * @param value the argument value
		 * @param type the type of the constructor argument
		 * @param name the name of the constructor argument
		 */
		public ValueHolder(Object value, String type, String name) {
			this.value = value;
			this.type = type;
			this.name = name;
		}

		/**
		 * Set the value for the constructor argument.
		 * <p>
		 *  设置构造函数参数的值
		 * 
		 * 
		 * @see PropertyPlaceholderConfigurer
		 */
		public void setValue(Object value) {
			this.value = value;
		}

		/**
		 * Return the value for the constructor argument.
		 * <p>
		 *  返回构造函数参数的值
		 * 
		 */
		public Object getValue() {
			return this.value;
		}

		/**
		 * Set the type of the constructor argument.
		 * <p>
		 *  设置构造函数参数的类型
		 * 
		 */
		public void setType(String type) {
			this.type = type;
		}

		/**
		 * Return the type of the constructor argument.
		 * <p>
		 *  返回构造函数参数的类型
		 * 
		 */
		public String getType() {
			return this.type;
		}

		/**
		 * Set the name of the constructor argument.
		 * <p>
		 *  设置构造函数参数的名称
		 * 
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * Return the name of the constructor argument.
		 * <p>
		 *  返回构造函数参数的名称
		 * 
		 */
		public String getName() {
			return this.name;
		}

		/**
		 * Set the configuration source {@code Object} for this metadata element.
		 * <p>The exact type of the object will depend on the configuration mechanism used.
		 * <p>
		 *  为此元数据元素设置配置源{@code Object} <p>对象的确切类型将取决于所使用的配置机制
		 * 
		 */
		public void setSource(Object source) {
			this.source = source;
		}

		@Override
		public Object getSource() {
			return this.source;
		}

		/**
		 * Return whether this holder contains a converted value already ({@code true}),
		 * or whether the value still needs to be converted ({@code false}).
		 * <p>
		 * 返回此持有人是否已经包含已转换的值({@code true}),还是需要转换该值({@code false})
		 * 
		 */
		public synchronized boolean isConverted() {
			return this.converted;
		}

		/**
		 * Set the converted value of the constructor argument,
		 * after processed type conversion.
		 * <p>
		 *  在处理类型转换后,设置构造函数参数的转换值
		 * 
		 */
		public synchronized void setConvertedValue(Object value) {
			this.converted = true;
			this.convertedValue = value;
		}

		/**
		 * Return the converted value of the constructor argument,
		 * after processed type conversion.
		 * <p>
		 *  在处理类型转换后返回构造函数参数的转换值
		 * 
		 */
		public synchronized Object getConvertedValue() {
			return this.convertedValue;
		}

		/**
		 * Determine whether the content of this ValueHolder is equal
		 * to the content of the given other ValueHolder.
		 * <p>Note that ValueHolder does not implement {@code equals}
		 * directly, to allow for multiple ValueHolder instances with the
		 * same content to reside in the same Set.
		 * <p>
		 *  确定此ValueHolder的内容是否等于给定其他ValueHolder的内容<p>请注意,ValueHolder不直接实现{@code equals},以允许具有相同内容的多个ValueHolder
		 * 实例驻留在同一集合中。
		 * 
		 */
		private boolean contentEquals(ValueHolder other) {
			return (this == other ||
					(ObjectUtils.nullSafeEquals(this.value, other.value) && ObjectUtils.nullSafeEquals(this.type, other.type)));
		}

		/**
		 * Determine whether the hash code of the content of this ValueHolder.
		 * <p>Note that ValueHolder does not implement {@code hashCode}
		 * directly, to allow for multiple ValueHolder instances with the
		 * same content to reside in the same Set.
		 * <p>
		 * 确定此ValueHolder <p>的内容的哈希码是否直接注明ValueHolder不实现{@code hashCode},以允许具有相同内容的多个ValueHolder实例驻留在同一集合中
		 * 
		 */
		private int contentHashCode() {
			return ObjectUtils.nullSafeHashCode(this.value) * 29 + ObjectUtils.nullSafeHashCode(this.type);
		}

		/**
		 * Create a copy of this ValueHolder: that is, an independent
		 * ValueHolder instance with the same contents.
		 * <p>
		 *  创建此ValueHolder的副本：即具有相同内容的独立ValueHolder实例
		 */
		public ValueHolder copy() {
			ValueHolder copy = new ValueHolder(this.value, this.type, this.name);
			copy.setSource(this.source);
			return copy;
		}
	}

}
