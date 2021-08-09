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

package org.springframework.validation;

import java.beans.PropertyEditor;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.beans.PropertyAccessorUtils;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.ConvertingPropertyEditorAdapter;
import org.springframework.util.Assert;

/**
 * Abstract base class for {@link BindingResult} implementations that work with
 * Spring's {@link org.springframework.beans.PropertyAccessor} mechanism.
 * Pre-implements field access through delegation to the corresponding
 * PropertyAccessor methods.
 *
 * <p>
 * 使用Spring的{@link orgspringframeworkbeansPropertyAccessor}机制的{@link BindingResult}实现的抽象基类通过委派到相应的Proper
 * tyAccessor方法来预实现字段访问。
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.0
 * @see #getPropertyAccessor()
 * @see org.springframework.beans.PropertyAccessor
 * @see org.springframework.beans.ConfigurablePropertyAccessor
 */
@SuppressWarnings("serial")
public abstract class AbstractPropertyBindingResult extends AbstractBindingResult {

	private ConversionService conversionService;


	/**
	 * Create a new AbstractPropertyBindingResult instance.
	 * <p>
	 *  创建一个新的AbstractPropertyBindingResult实例
	 * 
	 * 
	 * @param objectName the name of the target object
	 * @see DefaultMessageCodesResolver
	 */
	protected AbstractPropertyBindingResult(String objectName) {
		super(objectName);
	}


	public void initConversion(ConversionService conversionService) {
		Assert.notNull(conversionService, "ConversionService must not be null");
		this.conversionService = conversionService;
		if (getTarget() != null) {
			getPropertyAccessor().setConversionService(conversionService);
		}
	}

	/**
	 * Returns the underlying PropertyAccessor.
	 * <p>
	 *  返回底层的PropertyAccessor
	 * 
	 * 
	 * @see #getPropertyAccessor()
	 */
	@Override
	public PropertyEditorRegistry getPropertyEditorRegistry() {
		return getPropertyAccessor();
	}

	/**
	 * Returns the canonical property name.
	 * <p>
	 *  返回规范属性名称
	 * 
	 * 
	 * @see org.springframework.beans.PropertyAccessorUtils#canonicalPropertyName
	 */
	@Override
	protected String canonicalFieldName(String field) {
		return PropertyAccessorUtils.canonicalPropertyName(field);
	}

	/**
	 * Determines the field type from the property type.
	 * <p>
	 *  从属性类型确定字段类型
	 * 
	 * 
	 * @see #getPropertyAccessor()
	 */
	@Override
	public Class<?> getFieldType(String field) {
		return getPropertyAccessor().getPropertyType(fixedField(field));
	}

	/**
	 * Fetches the field value from the PropertyAccessor.
	 * <p>
	 *  从PropertyAccessor获取字段值
	 * 
	 * 
	 * @see #getPropertyAccessor()
	 */
	@Override
	protected Object getActualFieldValue(String field) {
		return getPropertyAccessor().getPropertyValue(field);
	}

	/**
	 * Formats the field value based on registered PropertyEditors.
	 * <p>
	 *  根据注册的PropertyEditor格式化字段值
	 * 
	 * 
	 * @see #getCustomEditor
	 */
	@Override
	protected Object formatFieldValue(String field, Object value) {
		String fixedField = fixedField(field);
		// Try custom editor...
		PropertyEditor customEditor = getCustomEditor(fixedField);
		if (customEditor != null) {
			customEditor.setValue(value);
			String textValue = customEditor.getAsText();
			// If the PropertyEditor returned null, there is no appropriate
			// text representation for this value: only use it if non-null.
			if (textValue != null) {
				return textValue;
			}
		}
		if (this.conversionService != null) {
			// Try custom converter...
			TypeDescriptor fieldDesc = getPropertyAccessor().getPropertyTypeDescriptor(fixedField);
			TypeDescriptor strDesc = TypeDescriptor.valueOf(String.class);
			if (fieldDesc != null && this.conversionService.canConvert(fieldDesc, strDesc)) {
				return this.conversionService.convert(value, fieldDesc, strDesc);
			}
		}
		return value;
	}

	/**
	 * Retrieve the custom PropertyEditor for the given field, if any.
	 * <p>
	 *  检索给定字段的自定义PropertyEditor(如果有)
	 * 
	 * 
	 * @param fixedField the fully qualified field name
	 * @return the custom PropertyEditor, or {@code null}
	 */
	protected PropertyEditor getCustomEditor(String fixedField) {
		Class<?> targetType = getPropertyAccessor().getPropertyType(fixedField);
		PropertyEditor editor = getPropertyAccessor().findCustomEditor(targetType, fixedField);
		if (editor == null) {
			editor = BeanUtils.findEditorByConvention(targetType);
		}
		return editor;
	}

	/**
	 * This implementation exposes a PropertyEditor adapter for a Formatter,
	 * if applicable.
	 * <p>
	 *  此实现公开了FormEdit的PropertyEditor适配器(如果适用)
	 * 
	 */
	@Override
	public PropertyEditor findEditor(String field, Class<?> valueType) {
		Class<?> valueTypeForLookup = valueType;
		if (valueTypeForLookup == null) {
			valueTypeForLookup = getFieldType(field);
		}
		PropertyEditor editor = super.findEditor(field, valueTypeForLookup);
		if (editor == null && this.conversionService != null) {
			TypeDescriptor td = null;
			if (field != null) {
				TypeDescriptor ptd = getPropertyAccessor().getPropertyTypeDescriptor(fixedField(field));
				if (valueType == null || valueType.isAssignableFrom(ptd.getType())) {
					td = ptd;
				}
			}
			if (td == null) {
				td = TypeDescriptor.valueOf(valueTypeForLookup);
			}
			if (this.conversionService.canConvert(TypeDescriptor.valueOf(String.class), td)) {
				editor = new ConvertingPropertyEditorAdapter(this.conversionService, td);
			}
		}
		return editor;
	}


	/**
	 * Provide the PropertyAccessor to work with, according to the
	 * concrete strategy of access.
	 * <p>Note that a PropertyAccessor used by a BindingResult should
	 * always have its "extractOldValueForEditor" flag set to "true"
	 * by default, since this is typically possible without side effects
	 * for model objects that serve as data binding target.
	 * <p>
	 * 根据访问的具体策略,提供PropertyAccessor来处理<p>注意,由BindingResult使用的PropertyAccessor应该始终将其"extractOldValueForEditor
	 * "标志设置为"true",因为这通常可以没有副作用作为数据绑定目标的模型对象。
	 * 
	 * @see ConfigurablePropertyAccessor#setExtractOldValueForEditor
	 */
	public abstract ConfigurablePropertyAccessor getPropertyAccessor();

}
