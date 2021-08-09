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

package org.springframework.format.support;

import java.util.Set;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.core.convert.support.ConversionServiceFactory;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.Parser;
import org.springframework.format.Printer;
import org.springframework.util.StringValueResolver;

/**
 * A factory providing convenient access to a {@code FormattingConversionService}
 * configured with converters and formatters for common types such as numbers and
 * datetimes.
 *
 * <p>Additional converters and formatters can be registered declaratively through
 * {@link #setConverters(Set)} and {@link #setFormatters(Set)}. Another option
 * is to register converters and formatters in code by implementing the
 * {@link FormatterRegistrar} interface. You can then configure provide the set
 * of registrars to use through {@link #setFormatterRegistrars(Set)}.
 *
 * <p>A good example for registering converters and formatters in code is
 * {@code JodaTimeFormatterRegistrar}, which registers a number of
 * date-related formatters and converters. For a more detailed list of cases
 * see {@link #setFormatterRegistrars(Set)}
 *
 * <p>Like all {@code FactoryBean} implementations, this class is suitable for
 * use when configuring a Spring application context using Spring {@code <beans>}
 * XML. When configuring the container with
 * {@link org.springframework.context.annotation.Configuration @Configuration}
 * classes, simply instantiate, configure and return the appropriate
 * {@code FormattingConversionService} object from a
 * {@link org.springframework.context.annotation.Bean @Bean} method.
 *
 * <p>
 *  一个工厂可方便地访问配置有转换器和格式化程序的常用类型(如数字和数据时间)的{@code FormattingConversionService}
 * 
 * 可以通过{@link #setConverters(Set)}和{@link #setFormatters(Set)}声明地注册附加转换器和格式化器。
 * 另一个选项是通过实现{@link FormatterRegistrar}接口在代码中注册转换器和格式化器然后,您可以配置提供通过{@link #setFormatterRegistrars(Set)}使
 * 用的一组注册表。
 * 可以通过{@link #setConverters(Set)}和{@link #setFormatters(Set)}声明地注册附加转换器和格式化器。
 * 
 *  <p>在代码中注册转换器和格式化器的一个很好的例子是{@code JodaTimeFormatterRegistrar},它注册了一些与日期相关的格式化器和转换器。
 * 有关更多详细的案例列表,请参阅{@link #setFormatterRegistrars(Set)}。
 * 
 * 与所有{@code FactoryBean}实现一样,当使用Spring {@code <beans>} XML配置Spring应用程序上下文时,此类适用于使用{@link orgspringframeworkcontextannotationConfiguration @Configuration}
 * 类配置容器时,只需实例化,从{@link orgspringframeworkcontextannotationBean @Bean}方法配置并返回相应的{@code FormattingConversionService}
 * 对象。
 * 
 * 
 * @author Keith Donald
 * @author Juergen Hoeller
 * @author Rossen Stoyanchev
 * @author Chris Beams
 * @since 3.0
 */
public class FormattingConversionServiceFactoryBean
		implements FactoryBean<FormattingConversionService>, EmbeddedValueResolverAware, InitializingBean {

	private Set<?> converters;

	private Set<?> formatters;

	private Set<FormatterRegistrar> formatterRegistrars;

	private boolean registerDefaultFormatters = true;

	private StringValueResolver embeddedValueResolver;

	private FormattingConversionService conversionService;


	/**
	 * Configure the set of custom converter objects that should be added.
	 * <p>
	 *  配置应添加的一组自定义转换器对象
	 * 
	 * 
	 * @param converters instances of any of the following:
	 * {@link org.springframework.core.convert.converter.Converter},
	 * {@link org.springframework.core.convert.converter.ConverterFactory},
	 * {@link org.springframework.core.convert.converter.GenericConverter}
	 */
	public void setConverters(Set<?> converters) {
		this.converters = converters;
	}

	/**
	 * Configure the set of custom formatter objects that should be added.
	 * <p>
	 *  配置应该添加的一组自定义格式化对象
	 * 
	 * 
	 * @param formatters instances of {@link Formatter} or {@link AnnotationFormatterFactory}
	 */
	public void setFormatters(Set<?> formatters) {
		this.formatters = formatters;
	}

	/**
	 * <p>Configure the set of FormatterRegistrars to invoke to register
	 * Converters and Formatters in addition to those added declaratively
	 * via {@link #setConverters(Set)} and {@link #setFormatters(Set)}.
	 * <p>FormatterRegistrars are useful when registering multiple related
	 * converters and formatters for a formatting category, such as Date
	 * formatting. All types related needed to support the formatting
	 * category can be registered from one place.
	 * <p>FormatterRegistrars can also be used to register Formatters
	 * indexed under a specific field type different from its own &lt;T&gt;,
	 * or when registering a Formatter from a Printer/Parser pair.
	 * <p>
	 * <p>配置FormatterRegistrars的集合以调用注册转换器和格式化器,除了通过{@link #setConverters(Set)}和{@link #setFormatters(Set)}声
	 * 明方式添加的格式。
	 * 在注册时,FormatterRegistrars非常有用用于格式化类别的相关转换器和格式化器,例如日期格式化支持格式化类别所需的所有类型都可以从一个地方注册<p> FormatterRegistrars
	 * 也可以用于注册在与其自身不同的特定字段类型下索引的格式化器; T&gt;或从打印机/解析器对注册格式化程序时。
	 * 
	 * @see FormatterRegistry#addFormatterForFieldType(Class, Formatter)
	 * @see FormatterRegistry#addFormatterForFieldType(Class, Printer, Parser)
	 */
	public void setFormatterRegistrars(Set<FormatterRegistrar> formatterRegistrars) {
		this.formatterRegistrars = formatterRegistrars;
	}

	/**
	 * Indicate whether default formatters should be registered or not.
	 * <p>By default, built-in formatters are registered. This flag can be used
	 * to turn that off and rely on explicitly registered formatters only.
	 * <p>
	 * 
	 * 
	 * @see #setFormatters(Set)
	 * @see #setFormatterRegistrars(Set)
	 */
	public void setRegisterDefaultFormatters(boolean registerDefaultFormatters) {
		this.registerDefaultFormatters = registerDefaultFormatters;
	}

	@Override
	public void setEmbeddedValueResolver(StringValueResolver embeddedValueResolver) {
		this.embeddedValueResolver = embeddedValueResolver;
	}


	@Override
	public void afterPropertiesSet() {
		this.conversionService = new DefaultFormattingConversionService(this.embeddedValueResolver, this.registerDefaultFormatters);
		ConversionServiceFactory.registerConverters(this.converters, this.conversionService);
		registerFormatters();
	}

	private void registerFormatters() {
		if (this.formatters != null) {
			for (Object formatter : this.formatters) {
				if (formatter instanceof Formatter<?>) {
					this.conversionService.addFormatter((Formatter<?>) formatter);
				}
				else if (formatter instanceof AnnotationFormatterFactory<?>) {
					this.conversionService.addFormatterForFieldAnnotation((AnnotationFormatterFactory<?>) formatter);
				}
				else {
					throw new IllegalArgumentException(
							"Custom formatters must be implementations of Formatter or AnnotationFormatterFactory");
				}
			}
		}
		if (this.formatterRegistrars != null) {
			for (FormatterRegistrar registrar : this.formatterRegistrars) {
				registrar.registerFormatters(this.conversionService);
			}
		}
	}


	@Override
	public FormattingConversionService getObject() {
		return this.conversionService;
	}

	@Override
	public Class<? extends FormattingConversionService> getObjectType() {
		return FormattingConversionService.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
