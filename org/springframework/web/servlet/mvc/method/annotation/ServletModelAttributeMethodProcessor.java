/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2015 the original author or authors.
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

package org.springframework.web.servlet.mvc.method.annotation;

import java.util.Collections;
import java.util.Map;
import javax.servlet.ServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.annotation.ModelAttributeMethodProcessor;
import org.springframework.web.servlet.HandlerMapping;

/**
 * A Servlet-specific {@link ModelAttributeMethodProcessor} that applies data
 * binding through a WebDataBinder of type {@link ServletRequestDataBinder}.
 *
 * <p>Also adds a fall-back strategy to instantiate the model attribute from a
 * URI template variable or from a request parameter if the name matches the
 * model attribute name and there is an appropriate type conversion strategy.
 *
 * <p>
 *  一个特定于Servlet的{@link ModelAttributeMethodProcessor},它通过一种类型为{@link ServletRequestDataBinder}的WebDataB
 * inder应用数据绑定。
 * 
 * <p>还添加了一个后退策略,从URI模板变量或请求参数实例化模型属性,如果名称与模型属性名称相匹配,并且有适当的类型转换策略
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class ServletModelAttributeMethodProcessor extends ModelAttributeMethodProcessor {

	/**
	/* <p>
	/* 
	 * @param annotationNotRequired if "true", non-simple method arguments and
	 * return values are considered model attributes with or without a
	 * {@code @ModelAttribute} annotation
	 */
	public ServletModelAttributeMethodProcessor(boolean annotationNotRequired) {
		super(annotationNotRequired);
	}


	/**
	 * Instantiate the model attribute from a URI template variable or from a
	 * request parameter if the name matches to the model attribute name and
	 * if there is an appropriate type conversion strategy. If none of these
	 * are true delegate back to the base class.
	 * <p>
	 *  如果名称与模型属性名称匹配,并且如果存在适当的类型转换策略,则从URI模板变量或请求参数实例化模型属性如果没有这些属性转换为基类
	 * 
	 * 
	 * @see #createAttributeFromRequestValue
	 */
	@Override
	protected final Object createAttribute(String attributeName, MethodParameter methodParam,
			WebDataBinderFactory binderFactory, NativeWebRequest request) throws Exception {

		String value = getRequestValueForAttribute(attributeName, request);
		if (value != null) {
			Object attribute = createAttributeFromRequestValue(
					value, attributeName, methodParam, binderFactory, request);
			if (attribute != null) {
				return attribute;
			}
		}

		return super.createAttribute(attributeName, methodParam, binderFactory, request);
	}

	/**
	 * Obtain a value from the request that may be used to instantiate the
	 * model attribute through type conversion from String to the target type.
	 * <p>The default implementation looks for the attribute name to match
	 * a URI variable first and then a request parameter.
	 * <p>
	 *  从可能用于通过从String转换为目标类型的类型转换来实例化模型属性的请求获取值<p>默认实现将首先查找属性名称以匹配URI变量,然后查找请求参数
	 * 
	 * 
	 * @param attributeName the model attribute name
	 * @param request the current request
	 * @return the request value to try to convert or {@code null}
	 */
	protected String getRequestValueForAttribute(String attributeName, NativeWebRequest request) {
		Map<String, String> variables = getUriTemplateVariables(request);
		if (StringUtils.hasText(variables.get(attributeName))) {
			return variables.get(attributeName);
		}
		else if (StringUtils.hasText(request.getParameter(attributeName))) {
			return request.getParameter(attributeName);
		}
		else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	protected final Map<String, String> getUriTemplateVariables(NativeWebRequest request) {
		Map<String, String> variables = (Map<String, String>) request.getAttribute(
				HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
		return (variables != null ? variables : Collections.<String, String>emptyMap());
	}

	/**
	 * Create a model attribute from a String request value (e.g. URI template
	 * variable, request parameter) using type conversion.
	 * <p>The default implementation converts only if there a registered
	 * {@link Converter} that can perform the conversion.
	 * <p>
	 * 使用类型转换从String请求值(例如URI模板变量,请求参数)创建模型属性<p>默认实现只有在可以执行转换的注册的{@link Converter}时才转换
	 * 
	 * 
	 * @param sourceValue the source value to create the model attribute from
	 * @param attributeName the name of the attribute, never {@code null}
	 * @param methodParam the method parameter
	 * @param binderFactory for creating WebDataBinder instance
	 * @param request the current request
	 * @return the created model attribute, or {@code null}
	 * @throws Exception
	 */
	protected Object createAttributeFromRequestValue(String sourceValue, String attributeName,
			MethodParameter methodParam, WebDataBinderFactory binderFactory, NativeWebRequest request)
			throws Exception {

		DataBinder binder = binderFactory.createBinder(request, null, attributeName);
		ConversionService conversionService = binder.getConversionService();
		if (conversionService != null) {
			TypeDescriptor source = TypeDescriptor.valueOf(String.class);
			TypeDescriptor target = new TypeDescriptor(methodParam);
			if (conversionService.canConvert(source, target)) {
				return binder.convertIfNecessary(sourceValue, methodParam.getParameterType(), methodParam);
			}
		}
		return null;
	}

	/**
	 * This implementation downcasts {@link WebDataBinder} to
	 * {@link ServletRequestDataBinder} before binding.
	 * <p>
	 *  这个实现在绑定之前将{@link WebDataBinder}压缩成{@link ServletRequestDataBinder}
	 * 
	 * @see ServletRequestDataBinderFactory
	 */
	@Override
	protected void bindRequestParameters(WebDataBinder binder, NativeWebRequest request) {
		ServletRequest servletRequest = request.getNativeRequest(ServletRequest.class);
		ServletRequestDataBinder servletBinder = (ServletRequestDataBinder) binder;
		servletBinder.bind(servletRequest);
	}

}
