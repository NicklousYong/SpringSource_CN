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

package org.springframework.validation.beanvalidation;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.validation.ConstraintViolation;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstraintDescriptor;

import org.springframework.beans.NotReadablePropertyException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.SmartValidator;

/**
 * Adapter that takes a JSR-303 {@code javax.validator.Validator}
 * and exposes it as a Spring {@link org.springframework.validation.Validator}
 * while also exposing the original JSR-303 Validator interface itself.
 *
 * <p>Can be used as a programmatic wrapper. Also serves as base class for
 * {@link CustomValidatorBean} and {@link LocalValidatorFactoryBean}.
 *
 * <p>
 *  使用JSR-303 {@code javaxvalidatorValidator}并将其公开为Spring {@link orgspringframeworkvalidationValidator}的
 * 适配器,同时还会暴露原始的JSR-303验证器接口本身。
 * 
 * <p>可以用作程序包装也可以作为{@link CustomValidatorBean}和{@link LocalValidatorFactoryBean}的基类
 * 
 * 
 * @author Juergen Hoeller
 * @since 3.0
 */
public class SpringValidatorAdapter implements SmartValidator, javax.validation.Validator {

	private static final Set<String> internalAnnotationAttributes = new HashSet<String>(3);

	static {
		internalAnnotationAttributes.add("message");
		internalAnnotationAttributes.add("groups");
		internalAnnotationAttributes.add("payload");
	}

	private javax.validation.Validator targetValidator;


	/**
	 * Create a new SpringValidatorAdapter for the given JSR-303 Validator.
	 * <p>
	 *  为给定的JSR-303验证器创建一个新的SpringValidatorAdapter
	 * 
	 * 
	 * @param targetValidator the JSR-303 Validator to wrap
	 */
	public SpringValidatorAdapter(javax.validation.Validator targetValidator) {
		Assert.notNull(targetValidator, "Target Validator must not be null");
		this.targetValidator = targetValidator;
	}

	SpringValidatorAdapter() {
	}

	void setTargetValidator(javax.validation.Validator targetValidator) {
		this.targetValidator = targetValidator;
	}


	//---------------------------------------------------------------------
	// Implementation of Spring Validator interface
	//---------------------------------------------------------------------

	@Override
	public boolean supports(Class<?> clazz) {
		return (this.targetValidator != null);
	}

	@Override
	public void validate(Object target, Errors errors) {
		if (this.targetValidator != null) {
			processConstraintViolations(this.targetValidator.validate(target), errors);
		}
	}

	@Override
	public void validate(Object target, Errors errors, Object... validationHints) {
		if (this.targetValidator != null) {
			Set<Class<?>> groups = new LinkedHashSet<Class<?>>();
			if (validationHints != null) {
				for (Object hint : validationHints) {
					if (hint instanceof Class) {
						groups.add((Class<?>) hint);
					}
				}
			}
			processConstraintViolations(
					this.targetValidator.validate(target, groups.toArray(new Class<?>[groups.size()])), errors);
		}
	}

	/**
	 * Process the given JSR-303 ConstraintViolations, adding corresponding errors to
	 * the provided Spring {@link Errors} object.
	 * <p>
	 *  处理给定的JSR-303 ConstraintViolations,向提供的Spring {@link Errors}对象添加相应的错误
	 * 
	 * 
	 * @param violations the JSR-303 ConstraintViolation results
	 * @param errors the Spring errors object to register to
	 */
	protected void processConstraintViolations(Set<ConstraintViolation<Object>> violations, Errors errors) {
		for (ConstraintViolation<Object> violation : violations) {
			String field = determineField(violation);
			FieldError fieldError = errors.getFieldError(field);
			if (fieldError == null || !fieldError.isBindingFailure()) {
				try {
					ConstraintDescriptor<?> cd = violation.getConstraintDescriptor();
					String errorCode = determineErrorCode(cd);
					Object[] errorArgs = getArgumentsForConstraint(errors.getObjectName(), field, cd);
					if (errors instanceof BindingResult) {
						// Can do custom FieldError registration with invalid value from ConstraintViolation,
						// as necessary for Hibernate Validator compatibility (non-indexed set path in field)
						BindingResult bindingResult = (BindingResult) errors;
						String nestedField = bindingResult.getNestedPath() + field;
						if ("".equals(nestedField)) {
							String[] errorCodes = bindingResult.resolveMessageCodes(errorCode);
							bindingResult.addError(new ObjectError(
									errors.getObjectName(), errorCodes, errorArgs, violation.getMessage()));
						}
						else {
							Object rejectedValue = getRejectedValue(field, violation, bindingResult);
							String[] errorCodes = bindingResult.resolveMessageCodes(errorCode, field);
							bindingResult.addError(new FieldError(
									errors.getObjectName(), nestedField, rejectedValue, false,
									errorCodes, errorArgs, violation.getMessage()));
						}
					}
					else {
						// got no BindingResult - can only do standard rejectValue call
						// with automatic extraction of the current field value
						errors.rejectValue(field, errorCode, errorArgs, violation.getMessage());
					}
				}
				catch (NotReadablePropertyException ex) {
					throw new IllegalStateException("JSR-303 validated property '" + field +
							"' does not have a corresponding accessor for Spring data binding - " +
							"check your DataBinder's configuration (bean property versus direct field access)", ex);
				}
			}
		}
	}

	/**
	 * Determine a field for the given constraint violation.
	 * <p>The default implementation returns the stringified property path.
	 * <p>
	 *  确定给定约束违例的字段<p>默认实现返回字符串化属性路径
	 * 
	 * 
	 * @param violation the current JSR-303 ConstraintViolation
	 * @return the Spring-reported field (for use with {@link Errors})
	 * @since 4.2
	 * @see javax.validation.ConstraintViolation#getPropertyPath()
	 * @see org.springframework.validation.FieldError#getField()
	 */
	protected String determineField(ConstraintViolation<Object> violation) {
		return violation.getPropertyPath().toString();
	}

	/**
	 * Determine a Spring-reported error code for the given constraint descriptor.
	 * <p>The default implementation returns the simple class name of the descriptor's
	 * annotation type. Note that the configured
	 * {@link org.springframework.validation.MessageCodesResolver} will automatically
	 * generate error code variations which include the object name and the field name.
	 * <p>
	 * 确定给定约束描述符的Spring报告的错误代码<p>默认实现返回描述符注释类型的简单类名注意,配置的{@link orgspringframeworkvalidationMessageCodesResolver}
	 * 将自动生成错误代码变体,其中包括对象名称和字段名称。
	 * 
	 * 
	 * @param descriptor the JSR-303 ConstraintDescriptor for the current violation
	 * @return a corresponding error code (for use with {@link Errors})
	 * @since 4.2
	 * @see javax.validation.metadata.ConstraintDescriptor#getAnnotation()
	 * @see org.springframework.validation.MessageCodesResolver
	 */
	protected String determineErrorCode(ConstraintDescriptor<?> descriptor) {
		return descriptor.getAnnotation().annotationType().getSimpleName();
	}

	/**
	 * Return FieldError arguments for a validation error on the given field.
	 * Invoked for each violated constraint.
	 * <p>The default implementation returns a first argument indicating the field name
	 * (see {@link #getResolvableField}). Afterwards, it adds all actual constraint
	 * annotation attributes (i.e. excluding "message", "groups" and "payload") in
	 * alphabetical order of their attribute names.
	 * <p>Can be overridden to e.g. add further attributes from the constraint descriptor.
	 * <p>
	 * 返回给定字段上的验证错误的FieldError参数为每个违规约束调用<p>默认实现返回指示字段名称的第一个参数(请参阅{@link #getResolvableField}),然后添加所有实际的约束注释
	 * 属性(即排除"消息","组"和"有效负载")按其属性名称的字母顺序排列<p>可以被覆盖,例如从约束描述符添加其他属性。
	 * 
	 * 
	 * @param objectName the name of the target object
	 * @param field the field that caused the binding error
	 * @param descriptor the JSR-303 constraint descriptor
	 * @return the Object array that represents the FieldError arguments
	 * @see org.springframework.validation.FieldError#getArguments
	 * @see org.springframework.context.support.DefaultMessageSourceResolvable
	 * @see org.springframework.validation.DefaultBindingErrorProcessor#getArgumentsForBindError
	 */
	protected Object[] getArgumentsForConstraint(String objectName, String field, ConstraintDescriptor<?> descriptor) {
		List<Object> arguments = new LinkedList<Object>();
		arguments.add(getResolvableField(objectName, field));
		// Using a TreeMap for alphabetical ordering of attribute names
		Map<String, Object> attributesToExpose = new TreeMap<String, Object>();
		for (Map.Entry<String, Object> entry : descriptor.getAttributes().entrySet()) {
			String attributeName = entry.getKey();
			Object attributeValue = entry.getValue();
			if (!internalAnnotationAttributes.contains(attributeName)) {
				if (attributeValue instanceof String) {
					attributeValue = new ResolvableAttribute(attributeValue.toString());
				}
				attributesToExpose.put(attributeName, attributeValue);
			}
		}
		arguments.addAll(attributesToExpose.values());
		return arguments.toArray(new Object[arguments.size()]);
	}

	/**
	 * Build a resolvable wrapper for the specified field, allowing to resolve the field's
	 * name in a {@code MessageSource}.
	 * <p>The default implementation returns a first argument indicating the field:
	 * of type {@code DefaultMessageSourceResolvable}, with "objectName.field" and "field"
	 * as codes, and with the plain field name as default message.
	 * <p>
	 * 为指定的字段构建一个可解析的包装器,允许在{@code MessageSource} <p>中解析字段的名称。
	 * 默认实现返回一个第一个参数,指示类型为{@code DefaultMessageSourceResolvable}的字段,其中"objectNamefield"和"字段"作为代码,并将普通字段名称作为默
	 * 认消息。
	 * 为指定的字段构建一个可解析的包装器,允许在{@code MessageSource} <p>中解析字段的名称。
	 * 
	 * 
	 * @param objectName the name of the target object
	 * @param field the field that caused the binding error
	 * @return a corresponding {@code MessageSourceResolvable} for the specified field
	 * @since 4.3
	 */
	protected MessageSourceResolvable getResolvableField(String objectName, String field) {
		String[] codes = new String[] {objectName + Errors.NESTED_PATH_SEPARATOR + field, field};
		return new DefaultMessageSourceResolvable(codes, field);
	}

	/**
	 * Extract the rejected value behind the given constraint violation,
	 * for exposure through the Spring errors representation.
	 * <p>
	 *  在给定的约束违规后提取被拒绝的值,以便通过Spring错误表示进行曝光
	 * 
	 * 
	 * @param field the field that caused the binding error
	 * @param violation the corresponding JSR-303 ConstraintViolation
	 * @param bindingResult a Spring BindingResult for the backing object
	 * which contains the current field's value
	 * @return the invalid value to expose as part of the field error
	 * @since 4.2
	 * @see javax.validation.ConstraintViolation#getInvalidValue()
	 * @see org.springframework.validation.FieldError#getRejectedValue()
	 */
	protected Object getRejectedValue(String field, ConstraintViolation<Object> violation, BindingResult bindingResult) {
		Object invalidValue = violation.getInvalidValue();
		if (!"".equals(field) && (invalidValue == violation.getLeafBean() ||
				(field.contains(".") && !field.contains("[]")))) {
			// Possibly a bean constraint with property path: retrieve the actual property value.
			// However, explicitly avoid this for "address[]" style paths that we can't handle.
			invalidValue = bindingResult.getRawFieldValue(field);
		}
		return invalidValue;
	}


	//---------------------------------------------------------------------
	// Implementation of JSR-303 Validator interface
	//---------------------------------------------------------------------

	@Override
	public <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
		Assert.state(this.targetValidator != null, "No target Validator set");
		return this.targetValidator.validate(object, groups);
	}

	@Override
	public <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName, Class<?>... groups) {
		Assert.state(this.targetValidator != null, "No target Validator set");
		return this.targetValidator.validateProperty(object, propertyName, groups);
	}

	@Override
	public <T> Set<ConstraintViolation<T>> validateValue(
			Class<T> beanType, String propertyName, Object value, Class<?>... groups) {

		Assert.state(this.targetValidator != null, "No target Validator set");
		return this.targetValidator.validateValue(beanType, propertyName, value, groups);
	}

	@Override
	public BeanDescriptor getConstraintsForClass(Class<?> clazz) {
		Assert.state(this.targetValidator != null, "No target Validator set");
		return this.targetValidator.getConstraintsForClass(clazz);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T unwrap(Class<T> type) {
		Assert.state(this.targetValidator != null, "No target Validator set");
		return (type != null ? this.targetValidator.unwrap(type) : (T) this.targetValidator);
	}


	/**
	 * Wrapper for a String attribute which can be resolved via a {@code MessageSource},
	 * falling back to the original attribute as a default value otherwise.
	 * <p>
	 *  可以通过{@code MessageSource}解析的String属性的Wrapper,否则返回到原始属性作为默认值
	 */
	private static class ResolvableAttribute implements MessageSourceResolvable {

		private final String resolvableString;

		public ResolvableAttribute(String resolvableString) {
			this.resolvableString = resolvableString;
		}

		@Override
		public String[] getCodes() {
			return new String[] {this.resolvableString};
		}

		@Override
		public Object[] getArguments() {
			return null;
		}

		@Override
		public String getDefaultMessage() {
			return this.resolvableString;
		}
	}

}
