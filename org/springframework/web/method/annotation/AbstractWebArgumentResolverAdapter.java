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

package org.springframework.web.method.annotation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * An abstract base class adapting a {@link WebArgumentResolver} to the
 * {@link HandlerMethodArgumentResolver} contract.
 *
 * <p><strong>Note:</strong> This class is provided for backwards compatibility.
 * However it is recommended to re-write a {@code WebArgumentResolver} as
 * {@code HandlerMethodArgumentResolver}. Since {@link #supportsParameter}
 * can only be implemented by actually resolving the value and then checking
 * the result is not {@code WebArgumentResolver#UNRESOLVED} any exceptions
 * raised must be absorbed and ignored since it's not clear whether the adapter
 * doesn't support the parameter or whether it failed for an internal reason.
 * The {@code HandlerMethodArgumentResolver} contract also provides access to
 * model attributes and to {@code WebDataBinderFactory} (for type conversion).
 *
 * <p>
 *  将{@link WebArgumentResolver}修改为{@link HandlerMethodArgumentResolver}合同的抽象基类
 * 
 * <p> <strong>注意：</strong>此类提供向后兼容性但是建议将{@code WebArgumentResolver}重写为{@code HandlerMethodArgumentResolver}
 * 由于{@link #supportsParameter}只能实现通过实际解析该值,然后检查结果不是{@code WebArgumentResolver#UNRESOLVED}引发的任何异常都必须被吸收和
 * 忽略,因为不清楚适配器是否不支持该参数,或者是否因内部原因而失败{ @code HandlerMethodArgumentResolver}合同还提供对模型属性的访问和{@code WebDataBinderFactory}
 * (用于类型转换)。
 * 
 * 
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public abstract class AbstractWebArgumentResolverAdapter implements HandlerMethodArgumentResolver {

	private final Log logger = LogFactory.getLog(getClass());

	private final WebArgumentResolver adaptee;


	/**
	 * Create a new instance.
	 * <p>
	 *  创建一个新的实例
	 * 
	 */
	public AbstractWebArgumentResolverAdapter(WebArgumentResolver adaptee) {
		Assert.notNull(adaptee, "'adaptee' must not be null");
		this.adaptee = adaptee;
	}


	/**
	 * Actually resolve the value and check the resolved value is not
	 * {@link WebArgumentResolver#UNRESOLVED} absorbing _any_ exceptions.
	 * <p>
	 * 实际解析值并检查解析的值不是{@link WebArgumentResolver#UNRESOLVED}吸收_any_异常
	 * 
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		try {
			NativeWebRequest webRequest = getWebRequest();
			Object result = this.adaptee.resolveArgument(parameter, webRequest);
			if (result == WebArgumentResolver.UNRESOLVED) {
				return false;
			}
			else {
				return ClassUtils.isAssignableValue(parameter.getParameterType(), result);
			}
		}
		catch (Exception ex) {
			// ignore (see class-level doc)
			logger.debug("Error in checking support for parameter [" + parameter + "], message: " + ex.getMessage());
			return false;
		}
	}

	/**
	 * Delegate to the {@link WebArgumentResolver} instance.
	 * <p>
	 *  委托给{@link WebArgumentResolver}实例
	 * 
	 * 
	 * @exception IllegalStateException if the resolved value is not assignable
	 * to the method parameter.
	 */
	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

		Class<?> paramType = parameter.getParameterType();
		Object result = this.adaptee.resolveArgument(parameter, webRequest);
		if (result == WebArgumentResolver.UNRESOLVED || !ClassUtils.isAssignableValue(paramType, result)) {
			throw new IllegalStateException(
					"Standard argument type [" + paramType.getName() + "] in method " + parameter.getMethod() +
					"resolved to incompatible value of type [" + (result != null ? result.getClass() : null) +
					"]. Consider declaring the argument type in a less specific fashion.");
		}
		return result;
	}


	/**
	 * Required for access to NativeWebRequest in {@link #supportsParameter}.
	 * <p>
	 *  需要在{@link #supportsParameter}中访问NativeWebRequest
	 */
	protected abstract NativeWebRequest getWebRequest();

}
