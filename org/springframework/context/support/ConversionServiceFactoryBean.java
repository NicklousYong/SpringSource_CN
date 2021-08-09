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

package org.springframework.context.support;

import java.util.Set;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.ConversionServiceFactory;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.convert.support.GenericConversionService;

/**
 * A factory providing convenient access to a ConversionService configured with
 * converters appropriate for most environments. Set the {@link #setConverters
 * "converters"} property to supplement the default converters.
 *
 * <p>This implementation creates a {@link DefaultConversionService}. Subclasses
 * may override {@link #createConversionService()} in order to return a
 * {@link GenericConversionService} instance of their choosing.
 *
 * <p>Like all {@code FactoryBean} implementations, this class is suitable for
 * use when configuring a Spring application context using Spring {@code <beans>}
 * XML. When configuring the container with
 * {@link org.springframework.context.annotation.Configuration @Configuration}
 * classes, simply instantiate, configure and return the appropriate
 * {@code ConversionService} object from a {@link
 * org.springframework.context.annotation.Bean @Bean} method.
 *
 * <p>
 *  一个工厂提供方便的访问ConversionService,配置有适合大多数环境的转换器设置{@link #setConverters"转换器"}属性以补充默认转换器
 * 
 * <p>此实现创建一个{@link DefaultConversionService}子类可以覆盖{@link #createConversionService()},以返回他们选择的{@link GenericConversionService}
 * 实例。
 * 
 *  与所有{@code FactoryBean}实现一样,当使用Spring {@code <beans>} XML配置Spring应用程序上下文时,此类适用于使用{@link orgspringframeworkcontextannotationConfiguration @Configuration}
 * 类配置容器时,只需实例化,从{@link orgspringframeworkcontextannotationBean @Bean}方法配置并返回相应的{@code ConversionService}
 * 对象。
 * 
 * 
 * @author Keith Donald
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 3.0
 */
public class ConversionServiceFactoryBean implements FactoryBean<ConversionService>, InitializingBean {

	private Set<?> converters;

	private GenericConversionService conversionService;


	/**
	 * Configure the set of custom converter objects that should be added:
	 * implementing {@link org.springframework.core.convert.converter.Converter},
	 * {@link org.springframework.core.convert.converter.ConverterFactory},
	 * or {@link org.springframework.core.convert.converter.GenericConverter}.
	 * <p>
	 */
	public void setConverters(Set<?> converters) {
		this.converters = converters;
	}

	@Override
	public void afterPropertiesSet() {
		this.conversionService = createConversionService();
		ConversionServiceFactory.registerConverters(this.converters, this.conversionService);
	}

	/**
	 * Create the ConversionService instance returned by this factory bean.
	 * <p>Creates a simple {@link GenericConversionService} instance by default.
	 * Subclasses may override to customize the ConversionService instance that
	 * gets created.
	 * <p>
	 * 配置应该添加的一组自定义转换器对象：实现{@link orgspringframeworkcoreconvertconverterConverter},{@link orgspringframeworkcoreconvertconverterConverterFactory}
	 * 或{@link orgspringframeworkcoreconvertconverterGenericConverter}。
	 * 
	 */
	protected GenericConversionService createConversionService() {
		return new DefaultConversionService();
	}


	// implementing FactoryBean

	@Override
	public ConversionService getObject() {
		return this.conversionService;
	}

	@Override
	public Class<? extends ConversionService> getObjectType() {
		return GenericConversionService.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
