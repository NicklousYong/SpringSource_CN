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

import java.beans.PropertyEditor;
import java.util.List;
import java.util.Map;

import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.util.Assert;

/**
 * Thrown when binding errors are considered fatal. Implements the
 * {@link BindingResult} interface (and its super-interface {@link Errors})
 * to allow for the direct analysis of binding errors.
 *
 * <p>As of Spring 2.0, this is a special-purpose class. Normally,
 * application code will work with the {@link BindingResult} interface,
 * or with a {@link DataBinder} that in turn exposes a BindingResult via
 * {@link org.springframework.validation.DataBinder#getBindingResult()}.
 *
 * <p>
 *  绑定错误时抛出被认为是致命的实施{@link BindingResult}接口(及其超级界面{@link错误})以允许对绑定错误的直接分析
 * 
 * <p>从Spring 20开始,这是一个特殊目的的类通常,应用程序代码将与{@link BindingResult}接口一起使用,或者通过{@link DataSpider getBindingResult()}
 * 。
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @see BindingResult
 * @see DataBinder#getBindingResult()
 * @see DataBinder#close()
 */
@SuppressWarnings("serial")
public class BindException extends Exception implements BindingResult {

	private final BindingResult bindingResult;


	/**
	 * Create a new BindException instance for a BindingResult.
	 * <p>
	 *  为BindingResult创建一个新的BindException实例
	 * 
	 * 
	 * @param bindingResult the BindingResult instance to wrap
	 */
	public BindException(BindingResult bindingResult) {
		Assert.notNull(bindingResult, "BindingResult must not be null");
		this.bindingResult = bindingResult;
	}

	/**
	 * Create a new BindException instance for a target bean.
	 * <p>
	 *  为目标bean创建一个新的BindException实例
	 * 
	 * 
	 * @param target target bean to bind onto
	 * @param objectName the name of the target object
	 * @see BeanPropertyBindingResult
	 */
	public BindException(Object target, String objectName) {
		Assert.notNull(target, "Target object must not be null");
		this.bindingResult = new BeanPropertyBindingResult(target, objectName);
	}


	/**
	 * Return the BindingResult that this BindException wraps.
	 * Will typically be a BeanPropertyBindingResult.
	 * <p>
	 *  返回此BindException包装的BindingResult将通常是一个BeanPropertyBindingResult
	 * 
	 * 
	 * @see BeanPropertyBindingResult
	 */
	public final BindingResult getBindingResult() {
		return this.bindingResult;
	}


	@Override
	public String getObjectName() {
		return this.bindingResult.getObjectName();
	}

	@Override
	public void setNestedPath(String nestedPath) {
		this.bindingResult.setNestedPath(nestedPath);
	}

	@Override
	public String getNestedPath() {
		return this.bindingResult.getNestedPath();
	}

	@Override
	public void pushNestedPath(String subPath) {
		this.bindingResult.pushNestedPath(subPath);
	}

	@Override
	public void popNestedPath() throws IllegalStateException {
		this.bindingResult.popNestedPath();
	}


	@Override
	public void reject(String errorCode) {
		this.bindingResult.reject(errorCode);
	}

	@Override
	public void reject(String errorCode, String defaultMessage) {
		this.bindingResult.reject(errorCode, defaultMessage);
	}

	@Override
	public void reject(String errorCode, Object[] errorArgs, String defaultMessage) {
		this.bindingResult.reject(errorCode, errorArgs, defaultMessage);
	}

	@Override
	public void rejectValue(String field, String errorCode) {
		this.bindingResult.rejectValue(field, errorCode);
	}

	@Override
	public void rejectValue(String field, String errorCode, String defaultMessage) {
		this.bindingResult.rejectValue(field, errorCode, defaultMessage);
	}

	@Override
	public void rejectValue(String field, String errorCode, Object[] errorArgs, String defaultMessage) {
		this.bindingResult.rejectValue(field, errorCode, errorArgs, defaultMessage);
	}

	@Override
	public void addAllErrors(Errors errors) {
		this.bindingResult.addAllErrors(errors);
	}


	@Override
	public boolean hasErrors() {
		return this.bindingResult.hasErrors();
	}

	@Override
	public int getErrorCount() {
		return this.bindingResult.getErrorCount();
	}

	@Override
	public List<ObjectError> getAllErrors() {
		return this.bindingResult.getAllErrors();
	}

	@Override
	public boolean hasGlobalErrors() {
		return this.bindingResult.hasGlobalErrors();
	}

	@Override
	public int getGlobalErrorCount() {
		return this.bindingResult.getGlobalErrorCount();
	}

	@Override
	public List<ObjectError> getGlobalErrors() {
		return this.bindingResult.getGlobalErrors();
	}

	@Override
	public ObjectError getGlobalError() {
		return this.bindingResult.getGlobalError();
	}

	@Override
	public boolean hasFieldErrors() {
		return this.bindingResult.hasFieldErrors();
	}

	@Override
	public int getFieldErrorCount() {
		return this.bindingResult.getFieldErrorCount();
	}

	@Override
	public List<FieldError> getFieldErrors() {
		return this.bindingResult.getFieldErrors();
	}

	@Override
	public FieldError getFieldError() {
		return this.bindingResult.getFieldError();
	}

	@Override
	public boolean hasFieldErrors(String field) {
		return this.bindingResult.hasFieldErrors(field);
	}

	@Override
	public int getFieldErrorCount(String field) {
		return this.bindingResult.getFieldErrorCount(field);
	}

	@Override
	public List<FieldError> getFieldErrors(String field) {
		return this.bindingResult.getFieldErrors(field);
	}

	@Override
	public FieldError getFieldError(String field) {
		return this.bindingResult.getFieldError(field);
	}

	@Override
	public Object getFieldValue(String field) {
		return this.bindingResult.getFieldValue(field);
	}

	@Override
	public Class<?> getFieldType(String field) {
		return this.bindingResult.getFieldType(field);
	}

	@Override
	public Object getTarget() {
		return this.bindingResult.getTarget();
	}

	@Override
	public Map<String, Object> getModel() {
		return this.bindingResult.getModel();
	}

	@Override
	public Object getRawFieldValue(String field) {
		return this.bindingResult.getRawFieldValue(field);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public PropertyEditor findEditor(String field, Class valueType) {
		return this.bindingResult.findEditor(field, valueType);
	}

	@Override
	public PropertyEditorRegistry getPropertyEditorRegistry() {
		return this.bindingResult.getPropertyEditorRegistry();
	}

	@Override
	public void addError(ObjectError error) {
		this.bindingResult.addError(error);
	}

	@Override
	public String[] resolveMessageCodes(String errorCode) {
		return this.bindingResult.resolveMessageCodes(errorCode);
	}

	@Override
	public String[] resolveMessageCodes(String errorCode, String field) {
		return this.bindingResult.resolveMessageCodes(errorCode, field);
	}

	@Override
	public void recordSuppressedField(String field) {
		this.bindingResult.recordSuppressedField(field);
	}

	@Override
	public String[] getSuppressedFields() {
		return this.bindingResult.getSuppressedFields();
	}


	/**
	 * Returns diagnostic information about the errors held in this object.
	 * <p>
	 *  返回有关此对象中保留的错误的诊断信息
	 */
	@Override
	public String getMessage() {
		return this.bindingResult.toString();
	}

	@Override
	public boolean equals(Object other) {
		return (this == other || this.bindingResult.equals(other));
	}

	@Override
	public int hashCode() {
		return this.bindingResult.hashCode();
	}

}
