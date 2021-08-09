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

package org.springframework.core.env;

import org.springframework.core.convert.support.ConfigurableConversionService;

/**
 * Configuration interface to be implemented by most if not all {@link PropertyResolver}
 * types. Provides facilities for accessing and customizing the
 * {@link org.springframework.core.convert.ConversionService ConversionService}
 * used when converting property values from one type to another.
 *
 * <p>
 * 要由大多数{@link PropertyResolver}类型实现的配置界面提供访问和自定义将属性值从一种类型转换为另一类时使用的{@link orgspringframeworkcoreconvertConversionService ConversionService}
 * 的功能。
 * 
 * 
 * @author Chris Beams
 * @since 3.1
 */
public interface ConfigurablePropertyResolver extends PropertyResolver {

	/**
	 * Return the {@link ConfigurableConversionService} used when performing type
	 * conversions on properties.
	 * <p>The configurable nature of the returned conversion service allows for
	 * the convenient addition and removal of individual {@code Converter} instances:
	 * <pre class="code">
	 * ConfigurableConversionService cs = env.getConversionService();
	 * cs.addConverter(new FooConverter());
	 * </pre>
	 * <p>
	 *  返回在属性上执行类型转换时使用的{@link ConfigurableConversionService} <p>返回的转换服务的可配置属性允许方便地添加和删除单个{@code Converter}实
	 * 例：。
	 * <pre class="code">
	 *  ConfigurableConversionService cs = envgetConversionService(); csaddConverter(new FooConverter());
	 * </pre>
	 * 
	 * @see PropertyResolver#getProperty(String, Class)
	 * @see org.springframework.core.convert.converter.ConverterRegistry#addConverter
	 */
	ConfigurableConversionService getConversionService();

	/**
	 * Set the {@link ConfigurableConversionService} to be used when performing type
	 * conversions on properties.
	 * <p><strong>Note:</strong> as an alternative to fully replacing the
	 * {@code ConversionService}, consider adding or removing individual
	 * {@code Converter} instances by drilling into {@link #getConversionService()}
	 * and calling methods such as {@code #addConverter}.
	 * <p>
	 * 设置在属性<p> <strong>上执行类型转换时使用的{@link ConfigurableConversionService}注意：</strong>作为完全替换{@code ConversionService}
	 * 的替代方法,考虑添加或删除单个{@code Converter }实例通过钻取到{@link #getConversionService()}并调用方法,如{@code #addConverter}。
	 * 
	 * 
	 * @see PropertyResolver#getProperty(String, Class)
	 * @see #getConversionService()
	 * @see org.springframework.core.convert.converter.ConverterRegistry#addConverter
	 */
	void setConversionService(ConfigurableConversionService conversionService);

	/**
	 * Set the prefix that placeholders replaced by this resolver must begin with.
	 * <p>
	 *  设置由此解析器替换的占位符的前缀必须以
	 * 
	 */
	void setPlaceholderPrefix(String placeholderPrefix);

	/**
	 * Set the suffix that placeholders replaced by this resolver must end with.
	 * <p>
	 *  设置由此解析器替换的占位符的后缀必须以
	 * 
	 */
	void setPlaceholderSuffix(String placeholderSuffix);

	/**
	 * Specify the separating character between the placeholders replaced by this
	 * resolver and their associated default value, or {@code null} if no such
	 * special character should be processed as a value separator.
	 * <p>
	 *  指定由此解析器替换的占位符与其关联的默认值之间的分隔符,或者如果不将此类特殊字符作为值分隔符处理,则为{@code null}
	 * 
	 */
	void setValueSeparator(String valueSeparator);

	/**
	 * Set whether to throw an exception when encountering an unresolvable placeholder
	 * nested within the value of a given property. A {@code false} value indicates strict
	 * resolution, i.e. that an exception will be thrown. A {@code true} value indicates
	 * that unresolvable nested placeholders should be passed through in their unresolved
	 * ${...} form.
	 * <p>Implementations of {@link #getProperty(String)} and its variants must inspect
	 * the value set here to determine correct behavior when property values contain
	 * unresolvable placeholders.
	 * <p>
	 * 设置是否在遇到嵌套在给定属性值中的不可解析占位符时抛出异常A {@code false}值表示严格解析,即将抛出异常A {@code true}值表示无法解析的嵌套占位符应在他们未解决的$ {}表单中传
	 * 递{p} {@link #getProperty(String)}的实现,其变体必须检查此处设置的值,以确定属性值包含不可解析占位符时的正确行为。
	 * 
	 * 
	 * @since 3.2
	 */
	void setIgnoreUnresolvableNestedPlaceholders(boolean ignoreUnresolvableNestedPlaceholders);

	/**
	 * Specify which properties must be present, to be verified by
	 * {@link #validateRequiredProperties()}.
	 * <p>
	 *  指定哪些属性必须存在,由{@link #validateRequiredProperties()}验证
	 * 
	 */
	void setRequiredProperties(String... requiredProperties);

	/**
	 * Validate that each of the properties specified by
	 * {@link #setRequiredProperties} is present and resolves to a
	 * non-{@code null} value.
	 * <p>
	 *  验证{@link #setRequiredProperties}指定的每个属性是否存在,并解析为非{@ code null}值
	 * 
	 * @throws MissingRequiredPropertiesException if any of the required
	 * properties are not resolvable.
	 */
	void validateRequiredProperties() throws MissingRequiredPropertiesException;

}
