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

package org.springframework.web.bind.support;

import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.core.convert.ConversionService;
import org.springframework.validation.BindingErrorProcessor;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.WebRequest;

/**
 * Convenient {@link WebBindingInitializer} for declarative configuration
 * in a Spring application context. Allows for reusing pre-configured
 * initializers with multiple controller/handlers.
 *
 * <p>
 *  方便的{@link WebBindingInitializer}用于Spring应用程序上下文中的声明性配置允许使用多个控制器/处理程序重新使用预配置的初始化程序
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.5
 * @see #setDirectFieldAccess
 * @see #setMessageCodesResolver
 * @see #setBindingErrorProcessor
 * @see #setValidator(Validator)
 * @see #setConversionService(ConversionService)
 * @see #setPropertyEditorRegistrar
 */
public class ConfigurableWebBindingInitializer implements WebBindingInitializer {

	private boolean autoGrowNestedPaths = true;

	private boolean directFieldAccess = false;

	private MessageCodesResolver messageCodesResolver;

	private BindingErrorProcessor bindingErrorProcessor;

	private Validator validator;

	private ConversionService conversionService;

	private PropertyEditorRegistrar[] propertyEditorRegistrars;


	/**
	 * Set whether a binder should attempt to "auto-grow" a nested path that contains a null value.
	 * <p>If "true", a null path location will be populated with a default object value and traversed
	 * instead of resulting in an exception. This flag also enables auto-growth of collection elements
	 * when accessing an out-of-bounds index.
	 * <p>Default is "true" on a standard DataBinder. Note that this feature is only supported
	 * for bean property access (DataBinder's default mode), not for field access.
	 * <p>
	 * 设置绑定器是否应尝试"自动增长"包含空值的嵌套路径<p>如果为"true",则空路径位置将使用默认对象值进行填充,而不是导致异常。
	 * 此标志还允许在访问超出索引时采集元素的自动增长<p>标准DataBinder上的默认值为"true"请注意,此功能仅支持bean属性访问(DataBinder的默认模式),而不适用于现场访问。
	 * 
	 * 
	 * @see org.springframework.validation.DataBinder#initBeanPropertyAccess()
	 * @see org.springframework.validation.DataBinder#setAutoGrowNestedPaths
	 */
	public void setAutoGrowNestedPaths(boolean autoGrowNestedPaths) {
		this.autoGrowNestedPaths = autoGrowNestedPaths;
	}

	/**
	 * Return whether a binder should attempt to "auto-grow" a nested path that contains a null value.
	 * <p>
	 *  返回绑定器是否应尝试"自动增长"包含空值的嵌套路径
	 * 
	 */
	public boolean isAutoGrowNestedPaths() {
		return this.autoGrowNestedPaths;
	}

	/**
	 * Set whether to use direct field access instead of bean property access.
	 * <p>Default is {@code false}, using bean property access.
	 * Switch this to {@code true} in order to enforce direct field access.
	 * <p>
	 *  设置是否使用直接字段访问而不是bean属性访问<p>默认是{@code false},使用bean属性访问将其切换到{@code true},以便强制直接访问域
	 * 
	 * 
	 * @see org.springframework.validation.DataBinder#initDirectFieldAccess()
	 * @see org.springframework.validation.DataBinder#initBeanPropertyAccess()
	 */
	public final void setDirectFieldAccess(boolean directFieldAccess) {
		this.directFieldAccess = directFieldAccess;
	}

	/**
	 * Return whether to use direct field access instead of bean property access.
	 * <p>
	 * 返回是否使用直接的字段访问而不是bean属性访问
	 * 
	 */
	public boolean isDirectFieldAccess() {
		return directFieldAccess;
	}

	/**
	 * Set the strategy to use for resolving errors into message codes.
	 * Applies the given strategy to all data binders used by this controller.
	 * <p>Default is {@code null}, i.e. using the default strategy of
	 * the data binder.
	 * <p>
	 *  设置用于将错误解决为消息代码的策略将给定的策略应用于此控制器使用的所有数据绑定器<p>默认值为{@code null},即使用数据绑定器的默认策略
	 * 
	 * 
	 * @see org.springframework.validation.DataBinder#setMessageCodesResolver
	 */
	public final void setMessageCodesResolver(MessageCodesResolver messageCodesResolver) {
		this.messageCodesResolver = messageCodesResolver;
	}

	/**
	 * Return the strategy to use for resolving errors into message codes.
	 * <p>
	 *  返回用于将错误解决为消息代码的策略
	 * 
	 */
	public final MessageCodesResolver getMessageCodesResolver() {
		return this.messageCodesResolver;
	}

	/**
	 * Set the strategy to use for processing binding errors, that is,
	 * required field errors and {@code PropertyAccessException}s.
	 * <p>Default is {@code null}, that is, using the default strategy
	 * of the data binder.
	 * <p>
	 *  设置用于处理绑定错误的策略,即必需的字段错误和{@code PropertyAccessException} s <p>默认值为{@code null},即使用数据绑定的默认策略
	 * 
	 * 
	 * @see org.springframework.validation.DataBinder#setBindingErrorProcessor
	 */
	public final void setBindingErrorProcessor(BindingErrorProcessor bindingErrorProcessor) {
		this.bindingErrorProcessor = bindingErrorProcessor;
	}

	/**
	 * Return the strategy to use for processing binding errors.
	 * <p>
	 *  返回用于处理绑定错误的策略
	 * 
	 */
	public final BindingErrorProcessor getBindingErrorProcessor() {
		return this.bindingErrorProcessor;
	}

	/**
	 * Set the Validator to apply after each binding step.
	 * <p>
	 *  将验证器设置为在每个装订步骤之后应用
	 * 
	 */
	public final void setValidator(Validator validator) {
		this.validator = validator;
	}

	/**
	 * Return the Validator to apply after each binding step, if any.
	 * <p>
	 *  在每个绑定步骤(如果有的话)后返回验证器
	 * 
	 */
	public final Validator getValidator() {
		return this.validator;
	}

	/**
	 * Specify a ConversionService which will apply to every DataBinder.
	 * <p>
	 * 指定一个适用于每个DataBinder的ConversionService
	 * 
	 * 
	 * @since 3.0
	 */
	public final void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	/**
	 * Return the ConversionService which will apply to every DataBinder.
	 * <p>
	 *  返回将适用于每个DataBinder的ConversionService
	 * 
	 */
	public final ConversionService getConversionService() {
		return this.conversionService;
	}

	/**
	 * Specify a single PropertyEditorRegistrar to be applied to every DataBinder.
	 * <p>
	 *  指定一个PropertyEditorRegistrar应用于每个DataBinder
	 * 
	 */
	public final void setPropertyEditorRegistrar(PropertyEditorRegistrar propertyEditorRegistrar) {
		this.propertyEditorRegistrars = new PropertyEditorRegistrar[] {propertyEditorRegistrar};
	}

	/**
	 * Specify multiple PropertyEditorRegistrars to be applied to every DataBinder.
	 * <p>
	 *  指定多个PropertyEditorRegistrars应用于每个DataBinder
	 * 
	 */
	public final void setPropertyEditorRegistrars(PropertyEditorRegistrar[] propertyEditorRegistrars) {
		this.propertyEditorRegistrars = propertyEditorRegistrars;
	}

	/**
	 * Return the PropertyEditorRegistrars to be applied to every DataBinder.
	 * <p>
	 *  返回要应用于每个DataBinder的PropertyEditorRegistrars
	 */
	public final PropertyEditorRegistrar[] getPropertyEditorRegistrars() {
		return this.propertyEditorRegistrars;
	}


	@Override
	public void initBinder(WebDataBinder binder, WebRequest request) {
		binder.setAutoGrowNestedPaths(this.autoGrowNestedPaths);
		if (this.directFieldAccess) {
			binder.initDirectFieldAccess();
		}
		if (this.messageCodesResolver != null) {
			binder.setMessageCodesResolver(this.messageCodesResolver);
		}
		if (this.bindingErrorProcessor != null) {
			binder.setBindingErrorProcessor(this.bindingErrorProcessor);
		}
		if (this.validator != null && binder.getTarget() != null &&
				this.validator.supports(binder.getTarget().getClass())) {
			binder.setValidator(this.validator);
		}
		if (this.conversionService != null) {
			binder.setConversionService(this.conversionService);
		}
		if (this.propertyEditorRegistrars != null) {
			for (PropertyEditorRegistrar propertyEditorRegistrar : this.propertyEditorRegistrars) {
				propertyEditorRegistrar.registerCustomEditors(binder);
			}
		}
	}

}
