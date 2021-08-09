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

package org.springframework.validation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * Utility class offering convenient methods for invoking a {@link Validator}
 * and for rejecting empty fields.
 *
 * <p>Checks for an empty field in {@code Validator} implementations can become
 * one-liners when using {@link #rejectIfEmpty} or {@link #rejectIfEmptyOrWhitespace}.
 *
 * <p>
 *  实用程序类提供了方便的方法来调用{@link Validator}和拒绝空字段
 * 
 * <p>使用{@link #rejectIfEmpty}或{@link #rejectIfEmptyOrWhitespace}时,检查{@code Validator}实现中的空字段可能会成为一个行
 * 
 * 
 * @author Juergen Hoeller
 * @author Dmitriy Kopylenko
 * @since 06.05.2003
 * @see Validator
 * @see Errors
 */
public abstract class ValidationUtils {

	private static final Log logger = LogFactory.getLog(ValidationUtils.class);


	/**
	 * Invoke the given {@link Validator} for the supplied object and
	 * {@link Errors} instance.
	 * <p>
	 *  调用给定的{@link验证器}提供的对象和{@link错误}实例
	 * 
	 * 
	 * @param validator the {@code Validator} to be invoked (must not be {@code null})
	 * @param obj the object to bind the parameters to
	 * @param errors the {@link Errors} instance that should store the errors (must not be {@code null})
	 * @throws IllegalArgumentException if either of the {@code Validator} or {@code Errors} arguments is
	 * {@code null}, or if the supplied {@code Validator} does not {@link Validator#supports(Class) support}
	 * the validation of the supplied object's type
	 */
	public static void invokeValidator(Validator validator, Object obj, Errors errors) {
		invokeValidator(validator, obj, errors, (Object[]) null);
	}

	/**
	 * Invoke the given {@link Validator}/{@link SmartValidator} for the supplied object and
	 * {@link Errors} instance.
	 * <p>
	 *  调用给定的{@link验证器} / {@ link SmartValidator}提供的对象和{@link错误}实例
	 * 
	 * 
	 * @param validator the {@code Validator} to be invoked (must not be {@code null})
	 * @param obj the object to bind the parameters to
	 * @param errors the {@link Errors} instance that should store the errors (must not be {@code null})
	 * @param validationHints one or more hint objects to be passed to the validation engine
	 * @throws IllegalArgumentException if either of the {@code Validator} or {@code Errors} arguments is
	 * {@code null}, or if the supplied {@code Validator} does not {@link Validator#supports(Class) support}
	 * the validation of the supplied object's type
	 */
	public static void invokeValidator(Validator validator, Object obj, Errors errors, Object... validationHints) {
		Assert.notNull(validator, "Validator must not be null");
		Assert.notNull(errors, "Errors object must not be null");
		if (logger.isDebugEnabled()) {
			logger.debug("Invoking validator [" + validator + "]");
		}
		if (obj != null && !validator.supports(obj.getClass())) {
			throw new IllegalArgumentException(
					"Validator [" + validator.getClass() + "] does not support [" + obj.getClass() + "]");
		}
		if (!ObjectUtils.isEmpty(validationHints) && validator instanceof SmartValidator) {
			((SmartValidator) validator).validate(obj, errors, validationHints);
		}
		else {
			validator.validate(obj, errors);
		}
		if (logger.isDebugEnabled()) {
			if (errors.hasErrors()) {
				logger.debug("Validator found " + errors.getErrorCount() + " errors");
			}
			else {
				logger.debug("Validator found no errors");
			}
		}
	}


	/**
	 * Reject the given field with the given error code if the value is empty.
	 * <p>An 'empty' value in this context means either {@code null} or
	 * the empty string "".
	 * <p>The object whose field is being validated does not need to be passed
	 * in because the {@link Errors} instance can resolve field values by itself
	 * (it will usually hold an internal reference to the target object).
	 * <p>
	 *  如果值为空,则拒绝带有给定错误代码的给定字段<p>此上下文中的"空"值表示{@code null}或空字符串""<p>正在验证其字段的对象不需要传入,因为{@link错误}实例本身可以解析字段值(它通
	 * 常会保存对目标对象的内部引用)。
	 * 
	 * 
	 * @param errors the {@code Errors} instance to register errors on
	 * @param field the field name to check
	 * @param errorCode the error code, interpretable as message key
	 */
	public static void rejectIfEmpty(Errors errors, String field, String errorCode) {
		rejectIfEmpty(errors, field, errorCode, null, null);
	}

	/**
	 * Reject the given field with the given error code and default message
	 * if the value is empty.
	 * <p>An 'empty' value in this context means either {@code null} or
	 * the empty string "".
	 * <p>The object whose field is being validated does not need to be passed
	 * in because the {@link Errors} instance can resolve field values by itself
	 * (it will usually hold an internal reference to the target object).
	 * <p>
	 * 如果值为空,则使用给定的错误代码和默认消息拒绝给定字段<p>此上下文中的"空"值表示{@code null}或空字符串""<p>其字段为验证不需要传入,因为{@link Errors}实例可以自己解析字
	 * 段值(它通常会保存对目标对象的内部引用)。
	 * 
	 * 
	 * @param errors the {@code Errors} instance to register errors on
	 * @param field the field name to check
	 * @param errorCode error code, interpretable as message key
	 * @param defaultMessage fallback default message
	 */
	public static void rejectIfEmpty(Errors errors, String field, String errorCode, String defaultMessage) {
		rejectIfEmpty(errors, field, errorCode, null, defaultMessage);
	}

	/**
	 * Reject the given field with the given error codea nd error arguments
	 * if the value is empty.
	 * <p>An 'empty' value in this context means either {@code null} or
	 * the empty string "".
	 * <p>The object whose field is being validated does not need to be passed
	 * in because the {@link Errors} instance can resolve field values by itself
	 * (it will usually hold an internal reference to the target object).
	 * <p>
	 *  如果值为空,则使用给定的错误代码和错误参数拒绝给定字段<p>此上下文中的"空"值表示{@code null}或空字符串""<p>其字段为验证不需要传入,因为{@link Errors}实例可以自己解析
	 * 字段值(它通常会保存对目标对象的内部引用)。
	 * 
	 * 
	 * @param errors the {@code Errors} instance to register errors on
	 * @param field the field name to check
	 * @param errorCode the error code, interpretable as message key
	 * @param errorArgs the error arguments, for argument binding via MessageFormat
	 * (can be {@code null})
	 */
	public static void rejectIfEmpty(Errors errors, String field, String errorCode, Object[] errorArgs) {
		rejectIfEmpty(errors, field, errorCode, errorArgs, null);
	}

	/**
	 * Reject the given field with the given error code, error arguments
	 * and default message if the value is empty.
	 * <p>An 'empty' value in this context means either {@code null} or
	 * the empty string "".
	 * <p>The object whose field is being validated does not need to be passed
	 * in because the {@link Errors} instance can resolve field values by itself
	 * (it will usually hold an internal reference to the target object).
	 * <p>
	 * 如果值为空,则使用给定的错误代码,错误参数和默认消息拒绝给定字段。
	 * 此上下文中的"空"值表示{@code null}或空字符串""<p>字段被验证不需要传入,因为{@link Errors}实例可以自己解析字段值(它通常会保存对目标对象的内部引用)。
	 * 
	 * 
	 * @param errors the {@code Errors} instance to register errors on
	 * @param field the field name to check
	 * @param errorCode the error code, interpretable as message key
	 * @param errorArgs the error arguments, for argument binding via MessageFormat
	 * (can be {@code null})
	 * @param defaultMessage fallback default message
	 */
	public static void rejectIfEmpty(
			Errors errors, String field, String errorCode, Object[] errorArgs, String defaultMessage) {

		Assert.notNull(errors, "Errors object must not be null");
		Object value = errors.getFieldValue(field);
		if (value == null || !StringUtils.hasLength(value.toString())) {
			errors.rejectValue(field, errorCode, errorArgs, defaultMessage);
		}
	}

	/**
	 * Reject the given field with the given error code if the value is empty
	 * or just contains whitespace.
	 * <p>An 'empty' value in this context means either {@code null},
	 * the empty string "", or consisting wholly of whitespace.
	 * <p>The object whose field is being validated does not need to be passed
	 * in because the {@link Errors} instance can resolve field values by itself
	 * (it will usually hold an internal reference to the target object).
	 * <p>
	 * 如果值为空或仅包含空格,则拒绝给定的字段<p>此上下文中的"空"值表示{@code null},空字符串"",或完全由空格<p >它的字段正在被验证的对象不需要传入,因为{@link错误}实例可以自己解
	 * 析字段值(它通常会保存对目标对象的内部引用)。
	 * 
	 * 
	 * @param errors the {@code Errors} instance to register errors on
	 * @param field the field name to check
	 * @param errorCode the error code, interpretable as message key
	 */
	public static void rejectIfEmptyOrWhitespace(Errors errors, String field, String errorCode) {
		rejectIfEmptyOrWhitespace(errors, field, errorCode, null, null);
	}

	/**
	 * Reject the given field with the given error code and default message
	 * if the value is empty or just contains whitespace.
	 * <p>An 'empty' value in this context means either {@code null},
	 * the empty string "", or consisting wholly of whitespace.
	 * <p>The object whose field is being validated does not need to be passed
	 * in because the {@link Errors} instance can resolve field values by itself
	 * (it will usually hold an internal reference to the target object).
	 * <p>
	 * 如果该值为空或仅包含空格,则使用给定的错误代码和默认消息拒绝给定字段<p>此上下文中的"空"值表示{@code null},空字符串"",或完全由空白<p>其字段正在被验证的对象不需要传入,因为{@link Errors}
	 * 实例可以自己解析字段值(它通常会保存对目标对象的内部引用)。
	 * 
	 * 
	 * @param errors the {@code Errors} instance to register errors on
	 * @param field the field name to check
	 * @param errorCode the error code, interpretable as message key
	 * @param defaultMessage fallback default message
	 */
	public static void rejectIfEmptyOrWhitespace(
			Errors errors, String field, String errorCode, String defaultMessage) {

		rejectIfEmptyOrWhitespace(errors, field, errorCode, null, defaultMessage);
	}

	/**
	 * Reject the given field with the given error code and error arguments
	 * if the value is empty or just contains whitespace.
	 * <p>An 'empty' value in this context means either {@code null},
	 * the empty string "", or consisting wholly of whitespace.
	 * <p>The object whose field is being validated does not need to be passed
	 * in because the {@link Errors} instance can resolve field values by itself
	 * (it will usually hold an internal reference to the target object).
	 * <p>
	 * 如果该值为空或仅包含空格,则使用给定的错误代码和错误参数拒绝给定字段<p>此上下文中的"空"值表示{@code null},空字符串"",或完全由空白<p>其字段正在被验证的对象不需要传入,因为{@link Errors}
	 * 实例可以自己解析字段值(它通常会保存对目标对象的内部引用)。
	 * 
	 * 
	 * @param errors the {@code Errors} instance to register errors on
	 * @param field the field name to check
	 * @param errorCode the error code, interpretable as message key
	 * @param errorArgs the error arguments, for argument binding via MessageFormat
	 * (can be {@code null})
	 */
	public static void rejectIfEmptyOrWhitespace(
			Errors errors, String field, String errorCode, Object[] errorArgs) {

		rejectIfEmptyOrWhitespace(errors, field, errorCode, errorArgs, null);
	}

	/**
	 * Reject the given field with the given error code, error arguments
	 * and default message if the value is empty or just contains whitespace.
	 * <p>An 'empty' value in this context means either {@code null},
	 * the empty string "", or consisting wholly of whitespace.
	 * <p>The object whose field is being validated does not need to be passed
	 * in because the {@link Errors} instance can resolve field values by itself
	 * (it will usually hold an internal reference to the target object).
	 * <p>
	 * 如果该值为空或仅包含空格,则使用给定的错误代码,错误参数和默认消息拒绝给定字段<p>此上下文中的"空"值意味着{@code null},空字符串""或完全由空格组成<p>其字段正在被验证的对象不需要传入
	 * ,因为{@link错误}实例可以自己解析字段值(通常会保存对目标对象的内部引用)。
	 * 
	 * @param errors the {@code Errors} instance to register errors on
	 * @param field the field name to check
	 * @param errorCode the error code, interpretable as message key
	 * @param errorArgs the error arguments, for argument binding via MessageFormat
	 * (can be {@code null})
	 * @param defaultMessage fallback default message
	 */
	public static void rejectIfEmptyOrWhitespace(
			Errors errors, String field, String errorCode, Object[] errorArgs, String defaultMessage) {

		Assert.notNull(errors, "Errors object must not be null");
		Object value = errors.getFieldValue(field);
		if (value == null ||!StringUtils.hasText(value.toString())) {
			errors.rejectValue(field, errorCode, errorArgs, defaultMessage);
		}
	}

}
