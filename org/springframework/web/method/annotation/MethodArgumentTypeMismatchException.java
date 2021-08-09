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

package org.springframework.web.method.annotation;

import org.springframework.beans.TypeMismatchException;
import org.springframework.core.MethodParameter;

/**
 * A TypeMismatchException raised while resolving a controller method argument.
 * Provides access to the target {@link org.springframework.core.MethodParameter
 * MethodParameter}.
 *
 * <p>
 *  解析控制器方法参数时引发的TypeMismatchException提供对目标的访问{@link orgspringframeworkcoreMethodParameter MethodParameter}
 * 。
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 4.2
 */
@SuppressWarnings("serial")
public class MethodArgumentTypeMismatchException extends TypeMismatchException {

	private final String name;

	private final MethodParameter parameter;


	public MethodArgumentTypeMismatchException(Object value, Class<?> requiredType,
			String name, MethodParameter param, Throwable cause) {

		super(value, requiredType, cause);
		this.name = name;
		this.parameter = param;
	}


	/**
	 * Return the name of the method argument.
	 * <p>
	 *  返回方法参数的名称
	 * 
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Return the target method parameter.
	 * <p>
	 * 返回目标方法参数
	 */
	public MethodParameter getParameter() {
		return this.parameter;
	}

}
