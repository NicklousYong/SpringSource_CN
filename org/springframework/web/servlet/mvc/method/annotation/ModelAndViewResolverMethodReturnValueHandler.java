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

package org.springframework.web.servlet.mvc.method.annotation;

import java.lang.reflect.Method;
import java.util.List;

import org.springframework.core.MethodParameter;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.ModelAttributeMethodProcessor;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.ModelAndViewResolver;

/**
 * This return value handler is intended to be ordered after all others as it
 * attempts to handle _any_ return value type (i.e. returns {@code true} for
 * all return types).
 *
 * <p>The return value is handled either with a {@link ModelAndViewResolver}
 * or otherwise by regarding it as a model attribute if it is a non-simple
 * type. If neither of these succeeds (essentially simple type other than
 * String), {@link UnsupportedOperationException} is raised.
 *
 * <p><strong>Note:</strong> This class is primarily needed to support
 * {@link ModelAndViewResolver}, which unfortunately cannot be properly
 * adapted to the {@link HandlerMethodReturnValueHandler} contract since the
 * {@link HandlerMethodReturnValueHandler#supportsReturnType} method
 * cannot be implemented. Hence {@code ModelAndViewResolver}s are limited
 * to always being invoked at the end after all other return value
 * handlers have been given a chance. It is recommended to re-implement
 * a {@code ModelAndViewResolver} as {@code HandlerMethodReturnValueHandler},
 * which also provides better access to the return type and method information.
 *
 * <p>
 *  该返回值处理程序旨在在所有其他方法之后排序,因为它尝试处理_any_返回值类型(即返回所有返回类型的{@code true})
 * 
 * <p>返回值使用{@link ModelAndViewResolver}处理,或者通过将其作为模型属性(如果它是非简单类型)来处理。
 * 如果这两个都不成功(基本上是String以外的简单类型),{@link UnsupportedOperationException}被提出。
 * 
 * <p> <strong>注意：</strong>此类主要用于支持{@link ModelAndViewResolver},由于{@link HandlerMethodReturnValueHandler#supportsReturnType}
 * 方法无法适用,因此不幸地无法适应{@link HandlerMethodReturnValueHandler}合约因此,{@code ModelAndViewResolver}被限制为在所有其他返回值处
 * 理程序被赋予机会之后始终被调用。
 * 
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class ModelAndViewResolverMethodReturnValueHandler implements HandlerMethodReturnValueHandler {

	private final List<ModelAndViewResolver> mavResolvers;

	private final ModelAttributeMethodProcessor modelAttributeProcessor = new ModelAttributeMethodProcessor(true);


	/**
	 * Create a new instance.
	 * <p>
	 * 建议将{@code ModelAndViewResolver}重新实现为{@code HandlerMethodReturnValueHandler},其中还可以更好地访问返回类型和方法信息。
	 * 
	 */
	public ModelAndViewResolverMethodReturnValueHandler(List<ModelAndViewResolver> mavResolvers) {
		this.mavResolvers = mavResolvers;
	}


	/**
	 * Always returns {@code true}. See class-level note.
	 * <p>
	 *  创建一个新的实例
	 * 
	 */
	@Override
	public boolean supportsReturnType(MethodParameter returnType) {
		return true;
	}

	@Override
	public void handleReturnValue(Object returnValue, MethodParameter returnType,
			ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {

		if (this.mavResolvers != null) {
			for (ModelAndViewResolver mavResolver : this.mavResolvers) {
				Class<?> handlerType = returnType.getContainingClass();
				Method method = returnType.getMethod();
				ExtendedModelMap model = (ExtendedModelMap) mavContainer.getModel();
				ModelAndView mav = mavResolver.resolveModelAndView(method, handlerType, returnValue, model, webRequest);
				if (mav != ModelAndViewResolver.UNRESOLVED) {
					mavContainer.addAllAttributes(mav.getModel());
					mavContainer.setViewName(mav.getViewName());
					if (!mav.isReference()) {
						mavContainer.setView(mav.getView());
					}
					return;
				}
			}
		}

		// No suitable ModelAndViewResolver...
		if (this.modelAttributeProcessor.supportsReturnType(returnType)) {
			this.modelAttributeProcessor.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
		}
		else {
			throw new UnsupportedOperationException("Unexpected return type: " +
					returnType.getParameterType().getName() + " in method: " + returnType.getMethod());
		}
	}

}
