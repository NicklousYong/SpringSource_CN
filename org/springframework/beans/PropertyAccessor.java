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

import java.util.Map;

import org.springframework.core.convert.TypeDescriptor;

/**
 * Common interface for classes that can access named properties
 * (such as bean properties of an object or fields in an object)
 * Serves as base interface for {@link BeanWrapper}.
 *
 * <p>
 *  可以访问命名属性的类的公共接口(例如对象中的对象的bean属性或对象中的字段)作为{@link BeanWrapper}的基本接口
 * 
 * 
 * @author Juergen Hoeller
 * @since 1.1
 * @see BeanWrapper
 * @see PropertyAccessorFactory#forBeanPropertyAccess
 * @see PropertyAccessorFactory#forDirectFieldAccess
 */
public interface PropertyAccessor {

	/**
	 * Path separator for nested properties.
	 * Follows normal Java conventions: getFoo().getBar() would be "foo.bar".
	 * <p>
	 * 嵌套属性的路径分隔符遵循普通的Java约定：getFoo()getBar()将为"foobar"
	 * 
	 */
	String NESTED_PROPERTY_SEPARATOR = ".";
	char NESTED_PROPERTY_SEPARATOR_CHAR = '.';

	/**
	 * Marker that indicates the start of a property key for an
	 * indexed or mapped property like "person.addresses[0]".
	 * <p>
	 *  标记指示索引或映射属性的属性键的开始,如"personaddresses [0]"
	 * 
	 */
	String PROPERTY_KEY_PREFIX = "[";
	char PROPERTY_KEY_PREFIX_CHAR = '[';

	/**
	 * Marker that indicates the end of a property key for an
	 * indexed or mapped property like "person.addresses[0]".
	 * <p>
	 *  指示索引或映射属性的属性键的结束的标记,如"personaddresses [0]"
	 * 
	 */
	String PROPERTY_KEY_SUFFIX = "]";
	char PROPERTY_KEY_SUFFIX_CHAR = ']';


	/**
	 * Determine whether the specified property is readable.
	 * <p>Returns {@code false} if the property doesn't exist.
	 * <p>
	 *  确定指定的属性是否可读<p>如果属性不存在,返回{@code false}
	 * 
	 * 
	 * @param propertyName the property to check
	 * (may be a nested path and/or an indexed/mapped property)
	 * @return whether the property is readable
	 */
	boolean isReadableProperty(String propertyName);

	/**
	 * Determine whether the specified property is writable.
	 * <p>Returns {@code false} if the property doesn't exist.
	 * <p>
	 *  确定指定的属性是否可写<p>如果属性不存在,返回{@code false}
	 * 
	 * 
	 * @param propertyName the property to check
	 * (may be a nested path and/or an indexed/mapped property)
	 * @return whether the property is writable
	 */
	boolean isWritableProperty(String propertyName);

	/**
	 * Determine the property type for the specified property,
	 * either checking the property descriptor or checking the value
	 * in case of an indexed or mapped element.
	 * <p>
	 *  确定指定属性的属性类型,检查属性描述符或在索引或映射元素的情况下检查值
	 * 
	 * 
	 * @param propertyName the property to check
	 * (may be a nested path and/or an indexed/mapped property)
	 * @return the property type for the particular property,
	 * or {@code null} if not determinable
	 * @throws InvalidPropertyException if there is no such property or
	 * if the property isn't readable
	 * @throws PropertyAccessException if the property was valid but the
	 * accessor method failed
	 */
	Class<?> getPropertyType(String propertyName) throws BeansException;

	/**
	 * Return a type descriptor for the specified property:
	 * preferably from the read method, falling back to the write method.
	 * <p>
	 * 返回指定属性的类型描述符：最好从read方法返回到write方法
	 * 
	 * 
	 * @param propertyName the property to check
	 * (may be a nested path and/or an indexed/mapped property)
	 * @return the property type for the particular property,
	 * or {@code null} if not determinable
	 * @throws InvalidPropertyException if there is no such property or
	 * if the property isn't readable
	 */
	TypeDescriptor getPropertyTypeDescriptor(String propertyName) throws BeansException;

	/**
	 * Get the current value of the specified property.
	 * <p>
	 *  获取指定属性的当前值
	 * 
	 * 
	 * @param propertyName the name of the property to get the value of
	 * (may be a nested path and/or an indexed/mapped property)
	 * @return the value of the property
	 * @throws InvalidPropertyException if there is no such property or
	 * if the property isn't readable
	 * @throws PropertyAccessException if the property was valid but the
	 * accessor method failed
	 */
	Object getPropertyValue(String propertyName) throws BeansException;

	/**
	 * Set the specified value as current property value.
	 * <p>
	 *  将指定的值设置为当前属性值
	 * 
	 * 
	 * @param propertyName the name of the property to set the value of
	 * (may be a nested path and/or an indexed/mapped property)
	 * @param value the new value
	 * @throws InvalidPropertyException if there is no such property or
	 * if the property isn't writable
	 * @throws PropertyAccessException if the property was valid but the
	 * accessor method failed or a type mismatch occurred
	 */
	void setPropertyValue(String propertyName, Object value) throws BeansException;

	/**
	 * Set the specified value as current property value.
	 * <p>
	 *  将指定的值设置为当前属性值
	 * 
	 * 
	 * @param pv an object containing the new property value
	 * @throws InvalidPropertyException if there is no such property or
	 * if the property isn't writable
	 * @throws PropertyAccessException if the property was valid but the
	 * accessor method failed or a type mismatch occurred
	 */
	void setPropertyValue(PropertyValue pv) throws BeansException;

	/**
	 * Perform a batch update from a Map.
	 * <p>Bulk updates from PropertyValues are more powerful: This method is
	 * provided for convenience. Behavior will be identical to that of
	 * the {@link #setPropertyValues(PropertyValues)} method.
	 * <p>
	 *  从地图执行批量更新<p>从PropertyValues批量更新更强大：为方便起见提供此方法行为将与{@link #setPropertyValues(PropertyValues)}方法的行为相同
	 * 
	 * 
	 * @param map Map to take properties from. Contains property value objects,
	 * keyed by property name
	 * @throws InvalidPropertyException if there is no such property or
	 * if the property isn't writable
	 * @throws PropertyBatchUpdateException if one or more PropertyAccessExceptions
	 * occurred for specific properties during the batch update. This exception bundles
	 * all individual PropertyAccessExceptions. All other properties will have been
	 * successfully updated.
	 */
	void setPropertyValues(Map<?, ?> map) throws BeansException;

	/**
	 * The preferred way to perform a batch update.
	 * <p>Note that performing a batch update differs from performing a single update,
	 * in that an implementation of this class will continue to update properties
	 * if a <b>recoverable</b> error (such as a type mismatch, but <b>not</b> an
	 * invalid field name or the like) is encountered, throwing a
	 * {@link PropertyBatchUpdateException} containing all the individual errors.
	 * This exception can be examined later to see all binding errors.
	 * Properties that were successfully updated remain changed.
	 * <p>Does not allow unknown fields or invalid fields.
	 * <p>
	 * 执行批量更新的首选方式<p>请注意,执行批量更新与执行单个更新不同之处在于,如果<b>可恢复</b>错误(例如类型不匹配,但是<b>不是</b>无效的字段名称等),抛出包含所有单个错误的{@link PropertyBatchUpdateException}
	 * 可以稍后检查此异常以查看所有绑定错误成功更新保留更改<p>不允许未知字段或无效字段。
	 * 
	 * 
	 * @param pvs PropertyValues to set on the target object
	 * @throws InvalidPropertyException if there is no such property or
	 * if the property isn't writable
	 * @throws PropertyBatchUpdateException if one or more PropertyAccessExceptions
	 * occurred for specific properties during the batch update. This exception bundles
	 * all individual PropertyAccessExceptions. All other properties will have been
	 * successfully updated.
	 * @see #setPropertyValues(PropertyValues, boolean, boolean)
	 */
	void setPropertyValues(PropertyValues pvs) throws BeansException;

	/**
	 * Perform a batch update with more control over behavior.
	 * <p>Note that performing a batch update differs from performing a single update,
	 * in that an implementation of this class will continue to update properties
	 * if a <b>recoverable</b> error (such as a type mismatch, but <b>not</b> an
	 * invalid field name or the like) is encountered, throwing a
	 * {@link PropertyBatchUpdateException} containing all the individual errors.
	 * This exception can be examined later to see all binding errors.
	 * Properties that were successfully updated remain changed.
	 * <p>
	 * 请执行批处理更新以更好地控制行为<p>请注意,执行批量更新与执行单个更新不同之处在于,如果<b>可恢复</b>错误(如此类),此类的实现将继续更新属性作为类型不匹配,但是<b>不是</b>无效的字段名称
	 * 等),抛出包含所有单个错误的{@link PropertyBatchUpdateException}此异常可以稍后检查以查看所有绑定错误属性成功更新仍然改变。
	 * 
	 * 
	 * @param pvs PropertyValues to set on the target object
	 * @param ignoreUnknown should we ignore unknown properties (not found in the bean)
	 * @throws InvalidPropertyException if there is no such property or
	 * if the property isn't writable
	 * @throws PropertyBatchUpdateException if one or more PropertyAccessExceptions
	 * occurred for specific properties during the batch update. This exception bundles
	 * all individual PropertyAccessExceptions. All other properties will have been
	 * successfully updated.
	 * @see #setPropertyValues(PropertyValues, boolean, boolean)
	 */
	void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown)
			throws BeansException;

	/**
	 * Perform a batch update with full control over behavior.
	 * <p>Note that performing a batch update differs from performing a single update,
	 * in that an implementation of this class will continue to update properties
	 * if a <b>recoverable</b> error (such as a type mismatch, but <b>not</b> an
	 * invalid field name or the like) is encountered, throwing a
	 * {@link PropertyBatchUpdateException} containing all the individual errors.
	 * This exception can be examined later to see all binding errors.
	 * Properties that were successfully updated remain changed.
	 * <p>
	 * 执行完全控制行为的批量更新<p>请注意,执行批量更新与执行单个更新不同之处在于,如果<b>可恢复</b>错误(如此类),此类的实现将继续更新属性作为类型不匹配,但是<b>不是</b>无效的字段名称等),
	 * 抛出包含所有单个错误的{@link PropertyBatchUpdateException}此异常可以稍后检查以查看所有绑定错误属性成功更新仍然改变。
	 * 
	 * @param pvs PropertyValues to set on the target object
	 * @param ignoreUnknown should we ignore unknown properties (not found in the bean)
	 * @param ignoreInvalid should we ignore invalid properties (found but not accessible)
	 * @throws InvalidPropertyException if there is no such property or
	 * if the property isn't writable
	 * @throws PropertyBatchUpdateException if one or more PropertyAccessExceptions
	 * occurred for specific properties during the batch update. This exception bundles
	 * all individual PropertyAccessExceptions. All other properties will have been
	 * successfully updated.
	 */
	void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown, boolean ignoreInvalid)
			throws BeansException;

}
