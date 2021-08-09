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

import org.springframework.core.MethodParameter;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.RequestToViewNameTranslator;

/**
 * Handles return values of types {@code void} and {@code String} interpreting them
 * as view name reference. As of 4.2, it also handles general {@code CharSequence}
 * types, e.g. {@code StringBuilder} or Groovy's {@code GString}, as view names.
 *
 * <p>A {@code null} return value, either due to a {@code void} return type or
 * as the actual return value is left as-is allowing the configured
 * {@link RequestToViewNameTranslator} to select a view name by convention.
 *
 * <p>A String return value can be interpreted in more than one ways depending
 * on the presence of annotations like {@code @ModelAttribute} or
 * {@code @ResponseBody}. Therefore this handler should be configured after
 * the handlers that support these annotations.
 *
 * <p>
 * 处理返回的类型{@code void}和{@code String}的值将其解释为视图名称引用从42开始,它还处理一般的{@code CharSequence}类型,例如{@code StringBuilder}
 * 或Groovy的{@code GString} ,作为视图名称。
 * 
 *  由于{@code void}返回类型或实际返回值是原样允许配置的{@link RequestToViewNameTranslator}按照惯例选择视图名称的{@code null}返回值
 * 
 *  <p>根据{@code @ModelAttribute}或{@code @ResponseBody}等注释的存在,可以多种方式解释字符串返回值。因此,在处理程序支持这些注释之后,应该配置此处理程序
 * 
 * 
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @since 3.1
 */
public class ViewNameMethodReturnValueHandler implements HandlerMethodReturnValueHandler {

	private String[] redirectPatterns;


	/**
	 * Configure one more simple patterns (as described in
	 * {@link PatternMatchUtils#simpleMatch}) to use in order to recognize
	 * custom redirect prefixes in addition to "redirect:".
	 * <p>Note that simply configuring this property will not make a custom
	 * redirect prefix work. There must be a custom View that recognizes the
	 * prefix as well.
	 * <p>
	 * 配置一个更简单的模式(如{@link PatternMatchUtils#simpleMatch}中所述)用于除了"redirect："之外还可以识别自定义重定向前缀<p>请注意,只需配置此属性将不会使
	 * 自定义重定向前缀正常工作必须有一个自定义视图,也可以识别前缀。
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
	 *  配置的重定向模式(如果有)
	 * 
	 */
	public String[] getRedirectPatterns() {
		return this.redirectPatterns;
	}


	@Override
	public boolean supportsReturnType(MethodParameter returnType) {
		Class<?> paramType = returnType.getParameterType();
		return (void.class == paramType || CharSequence.class.isAssignableFrom(paramType));
	}

	@Override
	public void handleReturnValue(Object returnValue, MethodParameter returnType,
			ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {

		if (returnValue instanceof CharSequence) {
			String viewName = returnValue.toString();
			mavContainer.setViewName(viewName);
			if (isRedirectViewName(viewName)) {
				mavContainer.setRedirectModelScenario(true);
			}
		}
		else if (returnValue != null){
			// should not happen
			throw new UnsupportedOperationException("Unexpected return type: " +
					returnType.getParameterType().getName() + " in method: " + returnType.getMethod());
		}
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
		return (PatternMatchUtils.simpleMatch(this.redirectPatterns, viewName) || viewName.startsWith("redirect:"));
	}

}
