/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2012 the original author or authors.
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

package org.springframework.validation;

import org.springframework.beans.PropertyAccessException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * Default {@link BindingErrorProcessor} implementation.
 *
 * <p>Uses the "required" error code and the field name to resolve message codes
 * for a missing field error.
 *
 * <p>Creates a {@code FieldError} for each {@code PropertyAccessException}
 * given, using the {@code PropertyAccessException}'s error code ("typeMismatch",
 * "methodInvocation") for resolving message codes.
 *
 * <p>
 *  默认{@link BindingErrorProcessor}实现
 * 
 *  <p>使用"必需"错误代码和字段名称来解决缺少字段错误的消息代码
 * 
 * <p>使用{@code PropertyAccessException}的错误代码("typeMismatch","methodInvocation")为每个{@code PropertyAccessException}
 * 创建{@code FieldError},以解决消息代码。
 * 
 * 
 * @author Alef Arendsen
 * @author Juergen Hoeller
 * @since 1.2
 * @see #MISSING_FIELD_ERROR_CODE
 * @see DataBinder#setBindingErrorProcessor
 * @see BeanPropertyBindingResult#addError
 * @see BeanPropertyBindingResult#resolveMessageCodes
 * @see org.springframework.beans.PropertyAccessException#getErrorCode
 * @see org.springframework.beans.TypeMismatchException#ERROR_CODE
 * @see org.springframework.beans.MethodInvocationException#ERROR_CODE
 */
public class DefaultBindingErrorProcessor implements BindingErrorProcessor {

	/**
	 * Error code that a missing field error (i.e. a required field not
	 * found in the list of property values) will be registered with:
	 * "required".
	 * <p>
	 */
	public static final String MISSING_FIELD_ERROR_CODE = "required";


	@Override
	public void processMissingFieldError(String missingField, BindingResult bindingResult) {
		// Create field error with code "required".
		String fixedField = bindingResult.getNestedPath() + missingField;
		String[] codes = bindingResult.resolveMessageCodes(MISSING_FIELD_ERROR_CODE, missingField);
		Object[] arguments = getArgumentsForBindError(bindingResult.getObjectName(), fixedField);
		bindingResult.addError(new FieldError(
				bindingResult.getObjectName(), fixedField, "", true,
				codes, arguments, "Field '" + fixedField + "' is required"));
	}

	@Override
	public void processPropertyAccessException(PropertyAccessException ex, BindingResult bindingResult) {
		// Create field error with the exceptions's code, e.g. "typeMismatch".
		String field = ex.getPropertyName();
		String[] codes = bindingResult.resolveMessageCodes(ex.getErrorCode(), field);
		Object[] arguments = getArgumentsForBindError(bindingResult.getObjectName(), field);
		Object rejectedValue = ex.getValue();
		if (rejectedValue != null && rejectedValue.getClass().isArray()) {
			rejectedValue = StringUtils.arrayToCommaDelimitedString(ObjectUtils.toObjectArray(rejectedValue));
		}
		bindingResult.addError(new FieldError(
				bindingResult.getObjectName(), field, rejectedValue, true,
				codes, arguments, ex.getLocalizedMessage()));
	}

	/**
	 * Return FieldError arguments for a binding error on the given field.
	 * Invoked for each missing required field and each type mismatch.
	 * <p>The default implementation returns a single argument indicating the field name
	 * (of type DefaultMessageSourceResolvable, with "objectName.field" and "field" as codes).
	 * <p>
	 *  缺少字段错误(即属性值列表中未找到的必填字段)的错误代码将被注册为："required"
	 * 
	 * 
	 * @param objectName the name of the target object
	 * @param field the field that caused the binding error
	 * @return the Object array that represents the FieldError arguments
	 * @see org.springframework.validation.FieldError#getArguments
	 * @see org.springframework.context.support.DefaultMessageSourceResolvable
	 */
	protected Object[] getArgumentsForBindError(String objectName, String field) {
		String[] codes = new String[] {objectName + Errors.NESTED_PATH_SEPARATOR + field, field};
		return new Object[] {new DefaultMessageSourceResolvable(codes, field)};
	}

}
