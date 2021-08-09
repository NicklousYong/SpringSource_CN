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

package org.springframework.beans;

import java.io.Serializable;

import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * Object to hold information and value for an individual bean property.
 * Using an object here, rather than just storing all properties in
 * a map keyed by property name, allows for more flexibility, and the
 * ability to handle indexed properties etc in an optimized way.
 *
 * <p>Note that the value doesn't need to be the final required type:
 * A {@link BeanWrapper} implementation should handle any necessary conversion,
 * as this object doesn't know anything about the objects it will be applied to.
 *
 * <p>
 * 保存单个bean属性的信息和值的对象在此处使用一个对象,而不仅仅是将所有属性存储在一个由属性名称键入的地图中,这样可以更有弹性,并以最佳的方式处理索引的属性等
 * 
 *  <p>请注意,该值不需要是最终必需的类型：{@link BeanWrapper}实现应该处理任何必要的转换,因为该对象不知道将被应用于的对象的任何内容
 * 
 * 
 * @author Rod Johnson
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 13 May 2001
 * @see PropertyValues
 * @see BeanWrapper
 */
@SuppressWarnings("serial")
public class PropertyValue extends BeanMetadataAttributeAccessor implements Serializable {

	private final String name;

	private final Object value;

	private boolean optional = false;

	private boolean converted = false;

	private Object convertedValue;

	/** Package-visible field that indicates whether conversion is necessary */
	volatile Boolean conversionNecessary;

	/** Package-visible field for caching the resolved property path tokens */
	transient volatile Object resolvedTokens;


	/**
	 * Create a new PropertyValue instance.
	 * <p>
	 *  创建一个新的PropertyValue实例
	 * 
	 * 
	 * @param name the name of the property (never {@code null})
	 * @param value the value of the property (possibly before type conversion)
	 */
	public PropertyValue(String name, Object value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * Copy constructor.
	 * <p>
	 *  复制构造函数
	 * 
	 * 
	 * @param original the PropertyValue to copy (never {@code null})
	 */
	public PropertyValue(PropertyValue original) {
		Assert.notNull(original, "Original must not be null");
		this.name = original.getName();
		this.value = original.getValue();
		this.optional = original.isOptional();
		this.converted = original.converted;
		this.convertedValue = original.convertedValue;
		this.conversionNecessary = original.conversionNecessary;
		this.resolvedTokens = original.resolvedTokens;
		setSource(original.getSource());
		copyAttributesFrom(original);
	}

	/**
	 * Constructor that exposes a new value for an original value holder.
	 * The original holder will be exposed as source of the new holder.
	 * <p>
	 *  为原始持有人公开新值的构造方原始持有人将作为新持有人的来源曝光
	 * 
	 * 
	 * @param original the PropertyValue to link to (never {@code null})
	 * @param newValue the new value to apply
	 */
	public PropertyValue(PropertyValue original, Object newValue) {
		Assert.notNull(original, "Original must not be null");
		this.name = original.getName();
		this.value = newValue;
		this.optional = original.isOptional();
		this.conversionNecessary = original.conversionNecessary;
		this.resolvedTokens = original.resolvedTokens;
		setSource(original);
		copyAttributesFrom(original);
	}


	/**
	 * Return the name of the property.
	 * <p>
	 *  返回属性的名称
	 * 
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Return the value of the property.
	 * <p>Note that type conversion will <i>not</i> have occurred here.
	 * It is the responsibility of the BeanWrapper implementation to
	 * perform type conversion.
	 * <p>
	 * 返回属性的值<p>请注意,类型转换将不会</i>发生在这里BeanWrapper实现的责任是执行类型转换
	 * 
	 */
	public Object getValue() {
		return this.value;
	}

	/**
	 * Return the original PropertyValue instance for this value holder.
	 * <p>
	 *  返回此值持有者的原始PropertyValue实例
	 * 
	 * 
	 * @return the original PropertyValue (either a source of this
	 * value holder or this value holder itself).
	 */
	public PropertyValue getOriginalPropertyValue() {
		PropertyValue original = this;
		Object source = getSource();
		while (source instanceof PropertyValue && source != original) {
			original = (PropertyValue) source;
			source = original.getSource();
		}
		return original;
	}

	/**
	 * Set whether this is an optional value, that is, to be ignored
	 * when no corresponding property exists on the target class.
	 * <p>
	 *  设置这是否是可选值,即在目标类上没有相应的属性时被忽略
	 * 
	 * 
	 * @since 3.0
	 */
	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	/**
	 * Return whether this is an optional value, that is, to be ignored
	 * when no corresponding property exists on the target class.
	 * <p>
	 *  返回是否是可选值,即在目标类上不存在相应的属性时被忽略
	 * 
	 * 
	 * @since 3.0
	 */
	public boolean isOptional() {
		return this.optional;
	}

	/**
	 * Return whether this holder contains a converted value already ({@code true}),
	 * or whether the value still needs to be converted ({@code false}).
	 * <p>
	 *  返回此持有人是否已经包含已转换的值({@code true}),还是需要转换该值({@code false})
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
	 * 在处理类型转换后返回构造函数参数的转换值
	 */
	public synchronized Object getConvertedValue() {
		return this.convertedValue;
	}


	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof PropertyValue)) {
			return false;
		}
		PropertyValue otherPv = (PropertyValue) other;
		return (this.name.equals(otherPv.name) &&
				ObjectUtils.nullSafeEquals(this.value, otherPv.value) &&
				ObjectUtils.nullSafeEquals(getSource(), otherPv.getSource()));
	}

	@Override
	public int hashCode() {
		return this.name.hashCode() * 29 + ObjectUtils.nullSafeHashCode(this.value);
	}

	@Override
	public String toString() {
		return "bean property '" + this.name + "'";
	}

}
