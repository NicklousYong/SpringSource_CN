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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.ValueConstants;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.util.ClassUtils;

/**
 * Abstract base class for resolving method arguments from a named value. Message headers,
 * and path variables are examples of named values. Each may have a name, a required flag,
 * and a default value.
 *
 * <p>Subclasses define how to do the following:
 * <ul>
 * <li>Obtain named value information for a method parameter
 * <li>Resolve names into argument values
 * <li>Handle missing argument values when argument values are required
 * <li>Optionally handle a resolved value
 * </ul>
 *
 * <p>A default value string can contain ${...} placeholders and Spring Expression
 * Language {@code #{...}} expressions. For this to work a {@link ConfigurableBeanFactory}
 * must be supplied to the class constructor.
 *
 * <p>A {@link ConversionService} may be used to apply type conversion to the resolved
 * argument value if it doesn't match the method parameter type.
 *
 * <p>
 *  用于从命名值解析方法参数的抽象基类消息头和路径变量是命名值的示例每个可以具有名称,必需标志和默认值
 * 
 * <p>子类定义如何执行以下操作：
 * <ul>
 *  <li>获取方法参数的命名值信息<li>将名称解析为参数值<li>需要参数值时处理缺失的参数值<li>可选地,处理已解析的值
 * </ul>
 * 
 *  <p>默认值字符串可以包含$ {}占位符和Spring表达式语言{@code#{}}表达式为此,必须将{@link ConfigurableBeanFactory}提供给类构造函数
 * 
 *  <p>如果{@link ConversionService}与方法参数类型不匹配,则可以使用{@link ConversionService}将类型转换应用于解析的参数值
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public abstract class AbstractNamedValueMethodArgumentResolver implements HandlerMethodArgumentResolver {

	private final ConversionService conversionService;

	private final ConfigurableBeanFactory configurableBeanFactory;

	private final BeanExpressionContext expressionContext;

	private final Map<MethodParameter, NamedValueInfo> namedValueInfoCache =
			new ConcurrentHashMap<MethodParameter, NamedValueInfo>(256);


	/**
	 * Constructor with a {@link ConversionService} and a {@link BeanFactory}.
	 * <p>
	 *  具有{@link ConversionService}和{@link BeanFactory}的构造方法
	 * 
	 * 
	 * @param cs conversion service for converting values to match the
	 * target method parameter type
	 * @param beanFactory a bean factory to use for resolving {@code ${...}} placeholder
	 * and {@code #{...}} SpEL expressions in default values, or {@code null} if default
	 * values are not expected to contain expressions
	 */
	protected AbstractNamedValueMethodArgumentResolver(ConversionService cs, ConfigurableBeanFactory beanFactory) {
		this.conversionService = (cs != null ? cs : new DefaultConversionService());
		this.configurableBeanFactory = beanFactory;
		this.expressionContext = (beanFactory != null ? new BeanExpressionContext(beanFactory, null) : null);
	}


	@Override
	public Object resolveArgument(MethodParameter parameter, Message<?> message) throws Exception {
		Class<?> paramType = parameter.getParameterType();
		NamedValueInfo namedValueInfo = getNamedValueInfo(parameter);

		Object arg = resolveArgumentInternal(parameter, message, namedValueInfo.name);
		if (arg == null) {
			if (namedValueInfo.defaultValue != null) {
				arg = resolveDefaultValue(namedValueInfo.defaultValue);
			}
			else if (namedValueInfo.required && !parameter.getParameterType().getName().equals("java.util.Optional")) {
				handleMissingValue(namedValueInfo.name, parameter, message);
			}
			arg = handleNullValue(namedValueInfo.name, arg, paramType);
		}
		else if ("".equals(arg) && namedValueInfo.defaultValue != null) {
			arg = resolveDefaultValue(namedValueInfo.defaultValue);
		}

		if (!ClassUtils.isAssignableValue(paramType, arg)) {
			arg = this.conversionService.convert(
					arg, TypeDescriptor.valueOf(arg.getClass()), new TypeDescriptor(parameter));
		}

		handleResolvedValue(arg, namedValueInfo.name, parameter, message);

		return arg;
	}

	/**
	 * Obtain the named value for the given method parameter.
	 * <p>
	 *  获取给定方法参数的命名值
	 * 
	 */
	private NamedValueInfo getNamedValueInfo(MethodParameter parameter) {
		NamedValueInfo namedValueInfo = this.namedValueInfoCache.get(parameter);
		if (namedValueInfo == null) {
			namedValueInfo = createNamedValueInfo(parameter);
			namedValueInfo = updateNamedValueInfo(parameter, namedValueInfo);
			this.namedValueInfoCache.put(parameter, namedValueInfo);
		}
		return namedValueInfo;
	}

	/**
	 * Create the {@link NamedValueInfo} object for the given method parameter. Implementations typically
	 * retrieve the method annotation by means of {@link MethodParameter#getParameterAnnotation(Class)}.
	 * <p>
	 * 为给定的方法参数创建{@link NamedValueInfo}对象实现通常通过{@link MethodParameter#getParameterAnnotation(Class))检索方法注释
	 * 
	 * 
	 * @param parameter the method parameter
	 * @return the named value information
	 */
	protected abstract NamedValueInfo createNamedValueInfo(MethodParameter parameter);

	/**
	 * Create a new NamedValueInfo based on the given NamedValueInfo with sanitized values.
	 * <p>
	 *  基于给定的具有消毒值的NamedValueInfo创建一个新的NamedValueInfo
	 * 
	 */
	private NamedValueInfo updateNamedValueInfo(MethodParameter parameter, NamedValueInfo info) {
		String name = info.name;
		if (info.name.length() == 0) {
			name = parameter.getParameterName();
			if (name == null) {
				throw new IllegalArgumentException("Name for argument type [" + parameter.getParameterType().getName() +
						"] not available, and parameter name information not found in class file either.");
			}
		}
		String defaultValue = (ValueConstants.DEFAULT_NONE.equals(info.defaultValue) ? null : info.defaultValue);
		return new NamedValueInfo(name, info.required, defaultValue);
	}

	/**
	 * Resolves the given parameter type and value name into an argument value.
	 * <p>
	 *  将给定的参数类型和值名称解析为参数值
	 * 
	 * 
	 * @param parameter the method parameter to resolve to an argument value
	 * @param message the current request
	 * @param name the name of the value being resolved
	 * @return the resolved argument. May be {@code null}
	 * @throws Exception in case of errors
	 */
	protected abstract Object resolveArgumentInternal(MethodParameter parameter, Message<?> message, String name)
			throws Exception;

	/**
	 * Resolves the given default value into an argument value.
	 * <p>
	 *  将给定的默认值解析为参数值
	 * 
	 */
	private Object resolveDefaultValue(String defaultValue) {
		if (this.configurableBeanFactory == null) {
			return defaultValue;
		}
		String placeholdersResolved = this.configurableBeanFactory.resolveEmbeddedValue(defaultValue);
		BeanExpressionResolver exprResolver = this.configurableBeanFactory.getBeanExpressionResolver();
		if (exprResolver == null) {
			return defaultValue;
		}
		return exprResolver.evaluate(placeholdersResolved, this.expressionContext);
	}

	/**
	 * Invoked when a named value is required, but
	 * {@link #resolveArgumentInternal(MethodParameter, Message, String)} returned {@code null} and
	 * there is no default value. Subclasses typically throw an exception in this case.
	 * <p>
	 *  当需要命名值时调用{@link #resolveArgumentInternal(MethodParameter,Message,String)}返回{@code null},并且没有默认值子类在这种
	 * 情况下通常会引发异常。
	 * 
	 * 
	 * @param name the name for the value
	 * @param parameter the method parameter
	 * @param message the message being processed
	 */
	protected abstract void handleMissingValue(String name, MethodParameter parameter, Message<?> message);

	/**
	 * A {@code null} results in a {@code false} value for {@code boolean}s or an
	 * exception for other primitives.
	 * <p>
	 *  {@code null}导致{@code boolean}的{@code false}值或其他图元的异常
	 * 
	 */
	private Object handleNullValue(String name, Object value, Class<?> paramType) {
		if (value == null) {
			if (Boolean.TYPE.equals(paramType)) {
				return Boolean.FALSE;
			}
			else if (paramType.isPrimitive()) {
				throw new IllegalStateException("Optional " + paramType + " parameter '" + name +
						"' is present but cannot be translated into a null value due to being " +
						"declared as a primitive type. Consider declaring it as object wrapper " +
						"for the corresponding primitive type.");
			}
		}
		return value;
	}

	/**
	 * Invoked after a value is resolved.
	 * <p>
	 *  在值解析后调用
	 * 
	 * 
	 * @param arg the resolved argument value
	 * @param name the argument name
	 * @param parameter the argument parameter type
	 * @param message the message
	 */
	protected void handleResolvedValue(Object arg, String name, MethodParameter parameter, Message<?> message) {
	}


	/**
	 * Represents the information about a named value, including name, whether it's
	 * required and a default value.
	 * <p>
	 * 表示有关命名值的信息,包括名称,是否需要和默认值
	 */
	protected static class NamedValueInfo {

		private final String name;

		private final boolean required;

		private final String defaultValue;

		protected NamedValueInfo(String name, boolean required, String defaultValue) {
			this.name = name;
			this.required = required;
			this.defaultValue = defaultValue;
		}
	}

}
