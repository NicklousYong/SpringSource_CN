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

package org.springframework.messaging.handler.annotation.support;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.messaging.converter.GenericMessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolverComposite;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * The default {@link MessageHandlerMethodFactory} implementation creating an
 * {@link InvocableHandlerMethod} with the necessary
 * {@link HandlerMethodArgumentResolver} instances to detect and process
 * most of  the use cases defined by
 * {@link org.springframework.messaging.handler.annotation.MessageMapping MessageMapping}
 *
 * <p>Extra method argument resolvers can be added to customize the method
 * signature that can be handled.
 *
 * <p>By default, the validation process redirects to a no-op implementation, see
 * {@link #setValidator(Validator)} to customize it. The {@link ConversionService}
 * can be customized in a similar manner to tune how the message payload
 * can be converted
 *
 * <p>
 * 使用必要的{@link HandlerMethodArgumentResolver}实例创建{@link InvocableHandlerMethod}的默认{@link MessageHandlerMethodFactory}
 * 实现来检测和处理由{@link orgspringframeworkmessaginghandlerannotationMessageMapping MessageMapping}定义的大多数用例。
 * 
 *  <p>可以添加额外的方法参数解析器来自定义可以处理的方法签名
 * 
 *  <p>默认情况下,验证过程重定向到无操作实现,请参阅{@link #setValidator(Validator)}自定义它可以以类似的方式定制{@link ConversionService},以调
 * 整消息有效负载的方式被转换。
 * 
 * 
 * @author Stephane Nicoll
 * @author Juergen Hoeller
 * @since 4.1
 * @see #setConversionService
 * @see #setValidator
 * @see #setCustomArgumentResolvers
 */
public class DefaultMessageHandlerMethodFactory implements MessageHandlerMethodFactory, BeanFactoryAware, InitializingBean {

	private ConversionService conversionService = new DefaultFormattingConversionService();

	private MessageConverter messageConverter;

	private Validator validator = new NoOpValidator();

	private List<HandlerMethodArgumentResolver> customArgumentResolvers;

	private final HandlerMethodArgumentResolverComposite argumentResolvers =
			new HandlerMethodArgumentResolverComposite();

	private BeanFactory beanFactory;


	/**
	 * Set the {@link ConversionService} to use to convert the original
	 * message payload or headers.
	 * <p>
	 *  将{@link ConversionService}设置为用于转换原始邮件有效内容或标头
	 * 
	 * 
	 * @see HeaderMethodArgumentResolver
	 * @see GenericMessageConverter
	 */
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	/**
	 * Set the {@link MessageConverter} to use. By default a {@link GenericMessageConverter}
	 * is used.
	 * <p>
	 * 将{@link MessageConverter}设置为使用默认情况下,使用{@link GenericMessageConverter}
	 * 
	 * 
	 * @see GenericMessageConverter
	 */
	public void setMessageConverter(MessageConverter messageConverter) {
		this.messageConverter = messageConverter;
	}

	/**
	 * Set the Validator instance used for validating @Payload arguments
	 * <p>
	 *  设置用于验证@Payload参数的Validator实例
	 * 
	 * 
	 * @see org.springframework.validation.annotation.Validated
	 * @see org.springframework.messaging.handler.annotation.support.PayloadArgumentResolver
	 */
	public void setValidator(Validator validator) {
		this.validator = validator;
	}

	/**
	 * Set the list of custom {@code HandlerMethodArgumentResolver}s that will be used
	 * after resolvers for supported argument type.
	 * <p>
	 *  设置自定义{@code HandlerMethodArgumentResolver}的列表,将在解析器之后使用支持的参数类型
	 * 
	 * 
	 * @param customArgumentResolvers the list of resolvers (never {@code null})
	 */
	public void setCustomArgumentResolvers(List<HandlerMethodArgumentResolver> customArgumentResolvers) {
		this.customArgumentResolvers = customArgumentResolvers;
	}

	/**
	 * Configure the complete list of supported argument types effectively overriding
	 * the ones configured by default. This is an advanced option. For most use cases
	 * it should be sufficient to use {@link #setCustomArgumentResolvers(java.util.List)}.
	 * <p>
	 *  配置支持的参数类型的完整列表有效地覆盖默认配置的参数类型这是一个高级选项对于大多数用例,使用{@link #setCustomArgumentResolvers(javautilList)}应该是足够
	 * 的。
	 * 
	 */
	public void setArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		if (argumentResolvers == null) {
			this.argumentResolvers.clear();
			return;
		}
		this.argumentResolvers.addResolvers(argumentResolvers);
	}

	/**
	 * A {@link BeanFactory} only needs to be available for placeholder resolution
	 * in handler method arguments; it's optional otherwise.
	 * <p>
	 */
	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	@Override
	public void afterPropertiesSet() {
		if (this.messageConverter == null) {
			this.messageConverter = new GenericMessageConverter(this.conversionService);
		}
		if (this.argumentResolvers.getResolvers().isEmpty()) {
			this.argumentResolvers.addResolvers(initArgumentResolvers());
		}
	}


	@Override
	public InvocableHandlerMethod createInvocableHandlerMethod(Object bean, Method method) {
		InvocableHandlerMethod handlerMethod = new InvocableHandlerMethod(bean, method);
		handlerMethod.setMessageMethodArgumentResolvers(argumentResolvers);
		return handlerMethod;
	}

	protected List<HandlerMethodArgumentResolver> initArgumentResolvers() {
		List<HandlerMethodArgumentResolver> resolvers = new ArrayList<HandlerMethodArgumentResolver>();
		ConfigurableBeanFactory cbf = (this.beanFactory instanceof ConfigurableBeanFactory ?
				(ConfigurableBeanFactory) this.beanFactory : null);

		// Annotation-based argument resolution
		resolvers.add(new HeaderMethodArgumentResolver(this.conversionService, cbf));
		resolvers.add(new HeadersMethodArgumentResolver());

		// Type-based argument resolution
		resolvers.add(new MessageMethodArgumentResolver());

		if (this.customArgumentResolvers != null) {
			resolvers.addAll(this.customArgumentResolvers);
		}
		resolvers.add(new PayloadArgumentResolver(this.messageConverter, this.validator));

		return resolvers;
	}


	private static final class NoOpValidator implements Validator {

		@Override
		public boolean supports(Class<?> clazz) {
			return false;
		}

		@Override
		public void validate(Object target, Errors errors) {
		}
	}

}
