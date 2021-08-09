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

package org.springframework.web.servlet.mvc.method.annotation;

import org.springframework.core.MethodParameter;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.SmartView;
import org.springframework.web.servlet.View;

/**
 * Handles return values of type {@link ModelAndView} copying view and model
 * information to the {@link ModelAndViewContainer}.
 *
 * <p>If the return value is {@code null}, the
 * {@link ModelAndViewContainer#setRequestHandled(boolean)} flag is set to
 * {@code true} to indicate the request was handled directly.
 *
 * <p>A {@link ModelAndView} return type has a set purpose. Therefore this
 * handler should be configured ahead of handlers that support any return
 * value type annotated with {@code @ModelAttribute} or {@code @ResponseBody}
 * to ensure they don't take over.
 *
 * <p>
 *  处理类型{@link ModelAndView}的返回值将视图和模型信息复制到{@link ModelAndViewContainer}
 * 
 * <p>如果返回值为{@code null},则{@link ModelAndViewContainer#setRequestHandled(boolean)}标志设置为{@code true}以指示请求
 * 被直接处理。
 * 
 *  <p> {@link ModelAndView}返回类型有一个设定目的因此,这个处理程序应该配置在处理程序之前,支持使用{@code @ModelAttribute}或{@code @ResponseBody}
 * 注释的任何返回值类型,以确保它们不会"接管。
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class ModelAndViewMethodReturnValueHandler implements HandlerMethodReturnValueHandler {

	private String[] redirectPatterns;


	/**
	 * Configure one more simple patterns (as described in
	 * {@link org.springframework.util.PatternMatchUtils#simpleMatch}) to use in order to recognize
	 * custom redirect prefixes in addition to "redirect:".
	 * <p>Note that simply configuring this property will not make a custom
	 * redirect prefix work. There must be a custom View that recognizes the
	 * prefix as well.
	 * <p>
	 *  配置一个更简单的模式(如{@link orgspringframeworkutilPatternMatchUtils#simpleMatch}中所述)用于识别除"redirect："之外的自定义重定向
	 * 前缀<p>请注意,只需配置此属性将不会使自定义重定向前缀正常工作必须有一个自定义视图,也可以识别前缀。
	 * 
	 * 
	 * @since 4.1
	 */
	public void setRedirectPatterns(String... redirectPatterns) {
		this.redirectPatterns = redirectPatterns;
	}

	/**
	 * The configured redirect patterns, if any.
	 * <p>
	 * 配置的重定向模式(如果有)
	 * 
	 */
	public String[] getRedirectPatterns() {
		return this.redirectPatterns;
	}


	@Override
	public boolean supportsReturnType(MethodParameter returnType) {
		return ModelAndView.class.isAssignableFrom(returnType.getParameterType());
	}

	@Override
	public void handleReturnValue(Object returnValue, MethodParameter returnType,
			ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {

		if (returnValue == null) {
			mavContainer.setRequestHandled(true);
			return;
		}

		ModelAndView mav = (ModelAndView) returnValue;
		if (mav.isReference()) {
			String viewName = mav.getViewName();
			mavContainer.setViewName(viewName);
			if (viewName != null && isRedirectViewName(viewName)) {
				mavContainer.setRedirectModelScenario(true);
			}
		}
		else {
			View view = mav.getView();
			mavContainer.setView(view);
			if (view instanceof SmartView) {
				if (((SmartView) view).isRedirectView()) {
					mavContainer.setRedirectModelScenario(true);
				}
			}
		}
		mavContainer.setStatus(mav.getStatus());
		mavContainer.addAllAttributes(mav.getModel());
	}

	/**
	 * Whether the given view name is a redirect view reference.
	 * The default implementation checks the configured redirect patterns and
	 * also if the view name starts with the "redirect:" prefix.
	 * <p>
	 *  给定的视图名称是否是重定向视图引用默认实现检查配置的重定向模式,并且视图名称以"redirect："前缀开头
	 * 
	 * @param viewName the view name to check, never {@code null}
	 * @return "true" if the given view name is recognized as a redirect view
	 * reference; "false" otherwise.
	 */
	protected boolean isRedirectViewName(String viewName) {
		if (PatternMatchUtils.simpleMatch(this.redirectPatterns, viewName)) {
			return true;
		}
		return viewName.startsWith("redirect:");
	}

}
