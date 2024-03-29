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

package org.springframework.web.method.annotation;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.bind.support.WebRequestDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Resolve {@code @ModelAttribute} annotated method arguments and handle
 * return values from {@code @ModelAttribute} annotated methods.
 *
 * <p>Model attributes are obtained from the model or created with a default
 * constructor (and then added to the model). Once created the attribute is
 * populated via data binding to Servlet request parameters. Validation may be
 * applied if the argument is annotated with {@code @javax.validation.Valid}.
 * or Spring's own {@code @org.springframework.validation.annotation.Validated}.
 *
 * <p>When this handler is created with {@code annotationNotRequired=true}
 * any non-simple type argument and return value is regarded as a model
 * attribute with or without the presence of an {@code @ModelAttribute}.
 *
 * <p>
 *  解析{@code @ModelAttribute}注释方法参数,并处理来自{@code @ModelAttribute}注释方法的返回值
 * 
 * 模型属性是从模型中获取的,或者使用默认构造函数创建(然后添加到模型中)创建后,通过数据绑定到Servlet请求参数来填充该属性。
 * 如果参数用{@code @javaxvalidationValid}或者Spring自己的{@code @orgspringframeworkvalidationannotationValidated}
 * 。
 * 模型属性是从模型中获取的,或者使用默认构造函数创建(然后添加到模型中)创建后,通过数据绑定到Servlet请求参数来填充该属性。
 * 
 *  <p>当使用{@code annotationNotRequired = true}创建此处理程序时,任何非简单类型参数和返回值都被视为具有或不存在{@code @ModelAttribute}的模型
 * 属性,。
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class ModelAttributeMethodProcessor
		implements HandlerMethodArgumentResolver, HandlerMethodReturnValueHandler {

	protected final Log logger = LogFactory.getLog(getClass());

	private final boolean annotationNotRequired;


	/**
	 * Class constructor.
	 * <p>
	 *  类构造函数
	 * 
	 * 
	 * @param annotationNotRequired if "true", non-simple method arguments and
	 * return values are considered model attributes with or without a
	 * {@code @ModelAttribute} annotation.
	 */
	public ModelAttributeMethodProcessor(boolean annotationNotRequired) {
		this.annotationNotRequired = annotationNotRequired;
	}


	/**
	 * Returns {@code true} if the parameter is annotated with
	 * {@link ModelAttribute} or, if in default resolution mode, for any
	 * method parameter that is not a simple type.
	 * <p>
	 *  如果参数用{@link ModelAttribute}注释,或者如果在默认分辨率模式下,对于不是简单类型的任何方法参数,返回{@code true}
	 * 
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return (parameter.hasParameterAnnotation(ModelAttribute.class) ||
				(this.annotationNotRequired && !BeanUtils.isSimpleProperty(parameter.getParameterType())));
	}

	/**
	 * Resolve the argument from the model or if not found instantiate it with
	 * its default if it is available. The model attribute is then populated
	 * with request values via data binding and optionally validated
	 * if {@code @java.validation.Valid} is present on the argument.
	 * <p>
	 * 从模型中解析参数,或者如果未找到,则将其默认值实例化为可用模型属性然后通过数据绑定填充请求值,如果参数中存在{@code @javavalidationValid},则可选地验证
	 * 
	 * 
	 * @throws BindException if data binding and validation result in an error
	 * and the next method parameter is not of type {@link Errors}.
	 * @throws Exception if WebDataBinder initialization fails.
	 */
	@Override
	public final Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

		String name = ModelFactory.getNameForParameter(parameter);
		Object attribute = (mavContainer.containsAttribute(name) ? mavContainer.getModel().get(name) :
				createAttribute(name, parameter, binderFactory, webRequest));

		if (!mavContainer.isBindingDisabled(name)) {
			ModelAttribute ann = parameter.getParameterAnnotation(ModelAttribute.class);
			if (ann != null && !ann.binding()) {
				mavContainer.setBindingDisabled(name);
			}
		}

		WebDataBinder binder = binderFactory.createBinder(webRequest, attribute, name);
		if (binder.getTarget() != null) {
			if (!mavContainer.isBindingDisabled(name)) {
				bindRequestParameters(binder, webRequest);
			}
			validateIfApplicable(binder, parameter);
			if (binder.getBindingResult().hasErrors() && isBindExceptionRequired(binder, parameter)) {
				throw new BindException(binder.getBindingResult());
			}
		}

		// Add resolved attribute and BindingResult at the end of the model
		Map<String, Object> bindingResultModel = binder.getBindingResult().getModel();
		mavContainer.removeAttributes(bindingResultModel);
		mavContainer.addAllAttributes(bindingResultModel);

		return binder.convertIfNecessary(binder.getTarget(), parameter.getParameterType(), parameter);
	}

	/**
	 * Extension point to create the model attribute if not found in the model.
	 * The default implementation uses the default constructor.
	 * <p>
	 *  扩展点创建模型属性(如果在模型中未找到)默认实现使用默认构造函数
	 * 
	 * 
	 * @param attributeName the name of the attribute (never {@code null})
	 * @param methodParam the method parameter
	 * @param binderFactory for creating WebDataBinder instance
	 * @param request the current request
	 * @return the created model attribute (never {@code null})
	 */
	protected Object createAttribute(String attributeName, MethodParameter methodParam,
			WebDataBinderFactory binderFactory, NativeWebRequest request) throws Exception {

		return BeanUtils.instantiateClass(methodParam.getParameterType());
	}

	/**
	 * Extension point to bind the request to the target object.
	 * <p>
	 *  扩展点将请求绑定到目标对象
	 * 
	 * 
	 * @param binder the data binder instance to use for the binding
	 * @param request the current request
	 */
	protected void bindRequestParameters(WebDataBinder binder, NativeWebRequest request) {
		((WebRequestDataBinder) binder).bind(request);
	}

	/**
	 * Validate the model attribute if applicable.
	 * <p>The default implementation checks for {@code @javax.validation.Valid},
	 * Spring's {@link org.springframework.validation.annotation.Validated},
	 * and custom annotations whose name starts with "Valid".
	 * <p>
	 *  验证模型属性(如果适用)<p>默认实现检查{@code @javaxvalidationValid},Spring的{@link orgspringframeworkvalidationannotationValidated}
	 * 和自定义注释,其名称以"有效"开头。
	 * 
	 * 
	 * @param binder the DataBinder to be used
	 * @param methodParam the method parameter
	 */
	protected void validateIfApplicable(WebDataBinder binder, MethodParameter methodParam) {
		Annotation[] annotations = methodParam.getParameterAnnotations();
		for (Annotation ann : annotations) {
			Validated validatedAnn = AnnotationUtils.getAnnotation(ann, Validated.class);
			if (validatedAnn != null || ann.annotationType().getSimpleName().startsWith("Valid")) {
				Object hints = (validatedAnn != null ? validatedAnn.value() : AnnotationUtils.getValue(ann));
				Object[] validationHints = (hints instanceof Object[] ? (Object[]) hints : new Object[] {hints});
				binder.validate(validationHints);
				break;
			}
		}
	}

	/**
	 * Whether to raise a fatal bind exception on validation errors.
	 * <p>
	 *  是否对验证错误提出致命的绑定异常
	 * 
	 * 
	 * @param binder the data binder used to perform data binding
	 * @param methodParam the method argument
	 * @return {@code true} if the next method argument is not of type {@link Errors}
	 */
	protected boolean isBindExceptionRequired(WebDataBinder binder, MethodParameter methodParam) {
		int i = methodParam.getParameterIndex();
		Class<?>[] paramTypes = methodParam.getMethod().getParameterTypes();
		boolean hasBindingResult = (paramTypes.length > (i + 1) && Errors.class.isAssignableFrom(paramTypes[i + 1]));
		return !hasBindingResult;
	}

	/**
	 * Return {@code true} if there is a method-level {@code @ModelAttribute}
	 * or, in default resolution mode, for any return value type that is not
	 * a simple type.
	 * <p>
	 * 如果有方法级{@code @ModelAttribute},或者在默认分辨率模式下返回不是简单类型的任何返回值类型,返回{@code true}
	 * 
	 */
	@Override
	public boolean supportsReturnType(MethodParameter returnType) {
		return (returnType.hasMethodAnnotation(ModelAttribute.class) ||
				(this.annotationNotRequired && !BeanUtils.isSimpleProperty(returnType.getParameterType())));
	}

	/**
	 * Add non-null return values to the {@link ModelAndViewContainer}.
	 * <p>
	 *  将非空返回值添加到{@link ModelAndViewContainer}
	 */
	@Override
	public void handleReturnValue(Object returnValue, MethodParameter returnType,
			ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {

		if (returnValue != null) {
			String name = ModelFactory.getNameForReturnValue(returnValue, returnType);
			mavContainer.addAttribute(name, returnValue);
		}
	}

}
