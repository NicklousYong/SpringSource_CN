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

import java.util.List;

import org.springframework.beans.PropertyAccessor;

/**
 * Stores and exposes information about data-binding and validation
 * errors for a specific object.
 *
 * <p>Field names can be properties of the target object (e.g. "name"
 * when binding to a customer object), or nested fields in case of
 * subobjects (e.g. "address.street"). Supports subtree navigation
 * via {@link #setNestedPath(String)}: for example, an
 * {@code AddressValidator} validates "address", not being aware
 * that this is a subobject of customer.
 *
 * <p>Note: {@code Errors} objects are single-threaded.
 *
 * <p>
 *  存储和公开有关特定对象的数据绑定和验证错误的信息
 * 
 * <p>字段名称可以是目标对象的属性(例如,当绑定到客户对象时为"name"),或者在子对象(例如"addressstreet")的情况下嵌套字段)通过{@link #setNestedPath(String))支持子树导航}
 * ：例如,{@code AddressValidator}验证"地址",不知道这是客户的子对象。
 * 
 *  <p>注意：{@code错误}对象是单线程的
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see #setNestedPath
 * @see BindException
 * @see DataBinder
 * @see ValidationUtils
 */
public interface Errors {

	/**
	 * The separator between path elements in a nested path,
	 * for example in "customer.name" or "customer.address.street".
	 * <p>"." = same as the
	 * {@link org.springframework.beans.PropertyAccessor#NESTED_PROPERTY_SEPARATOR nested property separator}
	 * in the beans package.
	 * <p>
	 *  嵌套路径中的路径元素之间的分隔符,例如"customername"或"customeraddressstreet"<p>""=与Bean包中的{@link orgspringframeworkbeansPropertyAccessor#NESTED_PROPERTY_SEPARATOR嵌套属性分隔符相同)。
	 * 
	 */
	String NESTED_PATH_SEPARATOR = PropertyAccessor.NESTED_PROPERTY_SEPARATOR;


	/**
	 * Return the name of the bound root object.
	 * <p>
	 *  返回绑定根对象的名称
	 * 
	 */
	String getObjectName();

	/**
	 * Allow context to be changed so that standard validators can validate
	 * subtrees. Reject calls prepend the given path to the field names.
	 * <p>For example, an address validator could validate the subobject
	 * "address" of a customer object.
	 * <p>
	 * 允许上下文更改,以便标准验证器可以验证子树拒绝调用将给定的路径添加到字段名称<p>例如,地址验证器可以验证客户对象的子对象"地址"
	 * 
	 * 
	 * @param nestedPath nested path within this object,
	 * e.g. "address" (defaults to "", {@code null} is also acceptable).
	 * Can end with a dot: both "address" and "address." are valid.
	 */
	void setNestedPath(String nestedPath);

	/**
	 * Return the current nested path of this {@link Errors} object.
	 * <p>Returns a nested path with a dot, i.e. "address.", for easy
	 * building of concatenated paths. Default is an empty String.
	 * <p>
	 *  返回此{@link错误}对象的当前嵌套路径<p>返回具有点的嵌套路径,即"地址",以便于构建连接路径默认为空字符串
	 * 
	 */
	String getNestedPath();

	/**
	 * Push the given sub path onto the nested path stack.
	 * <p>A {@link #popNestedPath()} call will reset the original
	 * nested path before the corresponding
	 * {@code pushNestedPath(String)} call.
	 * <p>Using the nested path stack allows to set temporary nested paths
	 * for subobjects without having to worry about a temporary path holder.
	 * <p>For example: current path "spouse.", pushNestedPath("child") ->
	 * result path "spouse.child."; popNestedPath() -> "spouse." again.
	 * <p>
	 *  将给定的子路径推送到嵌套路径堆栈<p> {@link #popNestedPath()}调用将在相应的{@code pushNestedPath(String)}调用之前重置原始嵌套路径<p>使用嵌套
	 * 路径堆栈允许为子对象设置临时嵌套路径,而无需担心临时路径持有者。
	 * <p>For example: current path "spouse.", pushNestedPath("child") ->
	 * 结果路径"spousechild"; popNestedPath() - >"配偶"
	 * 
	 * 
	 * @param subPath the sub path to push onto the nested path stack
	 * @see #popNestedPath
	 */
	void pushNestedPath(String subPath);

	/**
	 * Pop the former nested path from the nested path stack.
	 * <p>
	 *  从嵌套的路径堆栈弹出以前的嵌套路径
	 * 
	 * 
	 * @throws IllegalStateException if there is no former nested path on the stack
	 * @see #pushNestedPath
	 */
	void popNestedPath() throws IllegalStateException;

	/**
	 * Register a global error for the entire target object,
	 * using the given error description.
	 * <p>
	 *  使用给定的错误描述为整个目标对象注册全局错误
	 * 
	 * 
	 * @param errorCode error code, interpretable as a message key
	 */
	void reject(String errorCode);

	/**
	 * Register a global error for the entire target object,
	 * using the given error description.
	 * <p>
	 *  使用给定的错误描述为整个目标对象注册全局错误
	 * 
	 * 
	 * @param errorCode error code, interpretable as a message key
	 * @param defaultMessage fallback default message
	 */
	void reject(String errorCode, String defaultMessage);

	/**
	 * Register a global error for the entire target object,
	 * using the given error description.
	 * <p>
	 *  使用给定的错误描述为整个目标对象注册全局错误
	 * 
	 * 
	 * @param errorCode error code, interpretable as a message key
	 * @param errorArgs error arguments, for argument binding via MessageFormat
	 * (can be {@code null})
	 * @param defaultMessage fallback default message
	 */
	void reject(String errorCode, Object[] errorArgs, String defaultMessage);

	/**
	 * Register a field error for the specified field of the current object
	 * (respecting the current nested path, if any), using the given error
	 * description.
	 * <p>The field name may be {@code null} or empty String to indicate
	 * the current object itself rather than a field of it. This may result
	 * in a corresponding field error within the nested object graph or a
	 * global error if the current object is the top object.
	 * <p>
	 *  使用给定的错误描述注册当前对象的指定字段(如果有的话)的字段错误字段名称可能是{@code null}或空字符串以指示当前对象本身而不是一个字段它可能导致嵌套对象图中的相应字段错误或全局错误,如果当前
	 * 对象是顶部对象。
	 * 
	 * 
	 * @param field the field name (may be {@code null} or empty String)
	 * @param errorCode error code, interpretable as a message key
	 * @see #getNestedPath()
	 */
	void rejectValue(String field, String errorCode);

	/**
	 * Register a field error for the specified field of the current object
	 * (respecting the current nested path, if any), using the given error
	 * description.
	 * <p>The field name may be {@code null} or empty String to indicate
	 * the current object itself rather than a field of it. This may result
	 * in a corresponding field error within the nested object graph or a
	 * global error if the current object is the top object.
	 * <p>
	 * 使用给定的错误描述注册当前对象的指定字段(如果有的话)的字段错误字段名称可能是{@code null}或空字符串以指示当前对象本身而不是一个字段它可能导致嵌套对象图中的相应字段错误或全局错误,如果当前对
	 * 象是顶部对象。
	 * 
	 * 
	 * @param field the field name (may be {@code null} or empty String)
	 * @param errorCode error code, interpretable as a message key
	 * @param defaultMessage fallback default message
	 * @see #getNestedPath()
	 */
	void rejectValue(String field, String errorCode, String defaultMessage);

	/**
	 * Register a field error for the specified field of the current object
	 * (respecting the current nested path, if any), using the given error
	 * description.
	 * <p>The field name may be {@code null} or empty String to indicate
	 * the current object itself rather than a field of it. This may result
	 * in a corresponding field error within the nested object graph or a
	 * global error if the current object is the top object.
	 * <p>
	 * 使用给定的错误描述注册当前对象的指定字段(如果有的话)的字段错误字段名称可能是{@code null}或空字符串以指示当前对象本身而不是一个字段它可能导致嵌套对象图中的相应字段错误或全局错误,如果当前对
	 * 象是顶部对象。
	 * 
	 * 
	 * @param field the field name (may be {@code null} or empty String)
	 * @param errorCode error code, interpretable as a message key
	 * @param errorArgs error arguments, for argument binding via MessageFormat
	 * (can be {@code null})
	 * @param defaultMessage fallback default message
	 * @see #getNestedPath()
	 */
	void rejectValue(String field, String errorCode, Object[] errorArgs, String defaultMessage);

	/**
	 * Add all errors from the given {@code Errors} instance to this
	 * {@code Errors} instance.
	 * <p>This is a onvenience method to avoid repeated {@code reject(..)}
	 * calls for merging an {@code Errors} instance into another
	 * {@code Errors} instance.
	 * <p>Note that the passed-in {@code Errors} instance is supposed
	 * to refer to the same target object, or at least contain compatible errors
	 * that apply to the target object of this {@code Errors} instance.
	 * <p>
	 * 将所有错误从给定的{@code错误}实例添加到此{@code错误}实例<p>这是一个简单的方法,以避免{@code reject()}调用将{@code错误}实例合并到另一个{@code错误}实例<p>
	 * 请注意,传入的{@code错误}实例应该引用相同的目标对象,或至少包含适用于此{@code错误}的目标对象的兼容错误例。
	 * 
	 * 
	 * @param errors the {@code Errors} instance to merge in
	 */
	void addAllErrors(Errors errors);

	/**
	 * Return if there were any errors.
	 * <p>
	 *  如有任何错误返回
	 * 
	 */
	boolean hasErrors();

	/**
	 * Return the total number of errors.
	 * <p>
	 *  返回错误总数
	 * 
	 */
	int getErrorCount();

	/**
	 * Get all errors, both global and field ones.
	 * <p>
	 *  获取全部和外部错误的所有错误
	 * 
	 * 
	 * @return List of {@link ObjectError} instances
	 */
	List<ObjectError> getAllErrors();

	/**
	 * Are there any global errors?
	 * <p>
	 *  是否有全球性的错误?
	 * 
	 * 
	 * @return {@code true} if there are any global errors
	 * @see #hasFieldErrors()
	 */
	boolean hasGlobalErrors();

	/**
	 * Return the number of global errors.
	 * <p>
	 *  返回全局错误的数量
	 * 
	 * 
	 * @return the number of global errors
	 * @see #getFieldErrorCount()
	 */
	int getGlobalErrorCount();

	/**
	 * Get all global errors.
	 * <p>
	 *  获取所有全局错误
	 * 
	 * 
	 * @return List of ObjectError instances
	 */
	List<ObjectError> getGlobalErrors();

	/**
	 * Get the <i>first</i> global error, if any.
	 * <p>
	 *  获取<i>第一个</i>全局错误(如果有的话)
	 * 
	 * 
	 * @return the global error, or {@code null}
	 */
	ObjectError getGlobalError();

	/**
	 * Are there any field errors?
	 * <p>
	 *  是否有任何字段错误?
	 * 
	 * 
	 * @return {@code true} if there are any errors associated with a field
	 * @see #hasGlobalErrors()
	 */
	boolean hasFieldErrors();

	/**
	 * Return the number of errors associated with a field.
	 * <p>
	 *  返回与字段相关联的错误数
	 * 
	 * 
	 * @return the number of errors associated with a field
	 * @see #getGlobalErrorCount()
	 */
	int getFieldErrorCount();

	/**
	 * Get all errors associated with a field.
	 * <p>
	 * 获取与字段相关联的所有错误
	 * 
	 * 
	 * @return a List of {@link FieldError} instances
	 */
	List<FieldError> getFieldErrors();

	/**
	 * Get the <i>first</i> error associated with a field, if any.
	 * <p>
	 *  获取与字段相关联的<i>第一个</i>错误(如果有)
	 * 
	 * 
	 * @return the field-specific error, or {@code null}
	 */
	FieldError getFieldError();

	/**
	 * Are there any errors associated with the given field?
	 * <p>
	 *  与给定字段有关吗?
	 * 
	 * 
	 * @param field the field name
	 * @return {@code true} if there were any errors associated with the given field
	 */
	boolean hasFieldErrors(String field);

	/**
	 * Return the number of errors associated with the given field.
	 * <p>
	 *  返回与给定字段相关联的错误数
	 * 
	 * 
	 * @param field the field name
	 * @return the number of errors associated with the given field
	 */
	int getFieldErrorCount(String field);

	/**
	 * Get all errors associated with the given field.
	 * <p>Implementations should support not only full field names like
	 * "name" but also pattern matches like "na*" or "address.*".
	 * <p>
	 *  获取与给定字段相关联的所有错误<p>实现不仅应支持诸如"name"的完整字段名称,还应支持"na *"或"address *"等模式匹配
	 * 
	 * 
	 * @param field the field name
	 * @return a List of {@link FieldError} instances
	 */
	List<FieldError> getFieldErrors(String field);

	/**
	 * Get the first error associated with the given field, if any.
	 * <p>
	 *  获取与给定字段相关联的第一个错误(如果有)
	 * 
	 * 
	 * @param field the field name
	 * @return the field-specific error, or {@code null}
	 */
	FieldError getFieldError(String field);

	/**
	 * Return the current value of the given field, either the current
	 * bean property value or a rejected update from the last binding.
	 * <p>Allows for convenient access to user-specified field values,
	 * even if there were type mismatches.
	 * <p>
	 *  返回给定字段的当前值,或者最后一个绑定的当前bean属性值或拒绝的更新<p>允许方便地访问用户指定的字段值,即使存在类型不匹配
	 * 
	 * 
	 * @param field the field name
	 * @return the current value of the given field
	 */
	Object getFieldValue(String field);

	/**
	 * Return the type of a given field.
	 * <p>Implementations should be able to determine the type even
	 * when the field value is {@code null}, for example from some
	 * associated descriptor.
	 * <p>
	 * 返回给定字段的类型<p>即使字段值为{@code null},实现也应能够确定类型,例如来自某个关联描述符
	 * 
	 * @param field the field name
	 * @return the type of the field, or {@code null} if not determinable
	 */
	Class<?> getFieldType(String field);

}
