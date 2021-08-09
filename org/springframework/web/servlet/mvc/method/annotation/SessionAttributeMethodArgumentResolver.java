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

import javax.servlet.ServletException;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver;

/**
 * Resolves method arguments annotated with an @{@link SessionAttribute}.
 *
 * <p>
 *  除非适用法律要求或以书面形式同意,根据许可证分发的软件以"按原样"分发,不附带任何明示或暗示的担保或条件,请参阅许可证中有关特定语言的权限和限制许可证
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 4.3
 */
public class SessionAttributeMethodArgumentResolver extends AbstractNamedValueMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(SessionAttribute.class);
	}

	@Override
	protected NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
		SessionAttribute ann = parameter.getParameterAnnotation(SessionAttribute.class);
		return new NamedValueInfo(ann.name(), ann.required(), ValueConstants.DEFAULT_NONE);
	}

	@Override
	protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request){
		return request.getAttribute(name, RequestAttributes.SCOPE_SESSION);
	}

	@Override
	protected void handleMissingValue(String name, MethodParameter parameter) throws ServletException {
		throw new ServletRequestBindingException("Missing session attribute '" + name +
				"' of type " +  parameter.getNestedParameterType().getSimpleName());
	}

}
