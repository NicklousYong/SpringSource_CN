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

import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

/**
 * Exception to be thrown when a method argument fails validation perhaps as a
 * result of {@code @Valid} style validation, or perhaps because it is required.
 *
 * <p>
 *  当方法参数验证失败时可能会抛出异常,这可能是{@code @Valid}样式验证的结果,或者因为需要
 * 
 * 
 * @author Brian Clozel
 * @author Rossen Stoyanchev
 * @since 4.0.1
 */
@SuppressWarnings("serial")
public class MethodArgumentNotValidException extends AbstractMethodArgumentResolutionException {

	private final BindingResult bindingResult;


	/**
	 * Create a new instance with the invalid {@code MethodParameter}.
	 * <p>
	 *  使用无效的{@code MethodParameter}创建一个新的实例
	 * 
	 */
	public MethodArgumentNotValidException(Message<?> message, MethodParameter parameter) {
		this(message, parameter, null);
	}

	/**
	 * Create a new instance with the invalid {@code MethodParameter} and a
	 * {@link org.springframework.validation.BindingResult}.
	 * <p>
	 * 使用无效的{@code MethodParameter}和{@link orgspringframeworkvalidationBindingResult}创建一个新的实例
	 * 
	 */
	public MethodArgumentNotValidException(Message<?> message, MethodParameter parameter,
			BindingResult bindingResult) {

		super(message, parameter, getMethodParamMessage(parameter) +
				getValidationErrorMessage(parameter, bindingResult));

		this.bindingResult = bindingResult;
	}


	/**
	 * Return the BindingResult if the failure is validation-related or {@code null}.
	 * <p>
	 *  如果失败与验证相关或返回BindingResult {@code null}
	 */
	public BindingResult getBindingResult() {
		return this.bindingResult;
	}


	private static String getValidationErrorMessage(MethodParameter parameter, BindingResult bindingResult) {
		if (bindingResult != null) {
			StringBuilder sb = new StringBuilder();
			sb.append(", with ").append(bindingResult.getErrorCount()).append(" error(s): ");
			for (ObjectError error : bindingResult.getAllErrors()) {
				sb.append("[").append(error).append("] ");
			}
			return sb.toString();
		}
		else {
			return "";
		}
	}

}
