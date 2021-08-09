/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2014 the original author or authors.
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

import org.springframework.core.convert.ConversionService;

/**
 * Interface that encapsulates configuration methods for a PropertyAccessor.
 * Also extends the PropertyEditorRegistry interface, which defines methods
 * for PropertyEditor management.
 *
 * <p>Serves as base interface for {@link BeanWrapper}.
 *
 * <p>
 *  封装PropertyAccessor的配置方法的接口还扩展了PropertyEditorRegistry接口,该接口定义了PropertyEditor管理的方法
 * 
 * <p>作为{@link BeanWrapper}的基础界面
 * 
 * 
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 * @since 2.0
 * @see BeanWrapper
 */
public interface ConfigurablePropertyAccessor extends PropertyAccessor, PropertyEditorRegistry, TypeConverter {

	/**
	 * Specify a Spring 3.0 ConversionService to use for converting
	 * property values, as an alternative to JavaBeans PropertyEditors.
	 * <p>
	 *  指定用于转换属性值的Spring 30 ConversionService,作为JavaBeans PropertyEditor的替代方法
	 * 
	 */
	void setConversionService(ConversionService conversionService);

	/**
	 * Return the associated ConversionService, if any.
	 * <p>
	 *  返回相关的ConversionService(如果有)
	 * 
	 */
	ConversionService getConversionService();

	/**
	 * Set whether to extract the old property value when applying a
	 * property editor to a new value for a property.
	 * <p>
	 *  设置在将属性编辑器应用于属性的新值时是否提取旧属性值
	 * 
	 */
	void setExtractOldValueForEditor(boolean extractOldValueForEditor);

	/**
	 * Return whether to extract the old property value when applying a
	 * property editor to a new value for a property.
	 * <p>
	 *  返回是否在将属性编辑器应用于属性的新值时提取旧属性值
	 * 
	 */
	boolean isExtractOldValueForEditor();

	/**
	 * Set whether this instance should attempt to "auto-grow" a
	 * nested path that contains a {@code null} value.
	 * <p>If {@code true}, a {@code null} path location will be populated
	 * with a default object value and traversed instead of resulting in a
	 * {@link NullValueInNestedPathException}.
	 * <p>Default is {@code false} on a plain PropertyAccessor instance.
	 * <p>
	 * 设置此实例是否应尝试"自动增长"包含{@code null}值的嵌套路径<p>如果{@code true},将使用默认对象值填充{@code null}路径位置并且遍历,而不是在普通的PropertyA
	 * ccessor实例上导致{@link NullValueInNestedPathException} <p>默认值为{@code false}。
	 * 
	 */
	void setAutoGrowNestedPaths(boolean autoGrowNestedPaths);

	/**
	 * Return whether "auto-growing" of nested paths has been activated.
	 * <p>
	 */
	boolean isAutoGrowNestedPaths();

}
