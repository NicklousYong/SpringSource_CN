/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2013 the original author or authors.
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

package org.springframework.expression;

import org.springframework.core.convert.TypeDescriptor;

/**
 * An expression capable of evaluating itself against context objects. Encapsulates the
 * details of a previously parsed expression string. Provides a common abstraction for
 * expression evaluation independent of any language like OGNL or the Unified EL.
 *
 * <p>
 * 能够针对上下文对象评估自己的表达式封装先前解析的表达式字符串的详细信息提供独立于任何语言(如OGNL或Unified EL)的表达式求值的常见抽象
 * 
 * 
 * @author Keith Donald
 * @author Andy Clement
 * @since 3.0
 */
public interface Expression {

	/**
	 * Evaluate this expression in the default standard context.
	 * <p>
	 *  在默认标准上下文中评估此表达式
	 * 
	 * 
	 * @return the evaluation result
	 * @throws EvaluationException if there is a problem during evaluation
	 */
	Object getValue() throws EvaluationException;

	/**
	 * Evaluate this expression against the specified root object
	 * <p>
	 *  根据指定的根对象评估此表达式
	 * 
	 * 
	 * @param rootObject the root object against which properties/etc will be resolved
	 * @return the evaluation result
	 * @throws EvaluationException if there is a problem during evaluation
	 */
	Object getValue(Object rootObject) throws EvaluationException;

	/**
	 * Evaluate the expression in the default context. If the result of the evaluation does not match (and
	 * cannot be converted to) the expected result type then an exception will be returned.
	 * <p>
	 *  评估默认上下文中的表达式如果评估结果与预期结果类型不匹配(且不能转换为),则将返回异常
	 * 
	 * 
	 * @param desiredResultType the class the caller would like the result to be
	 * @return the evaluation result
	 * @throws EvaluationException if there is a problem during evaluation
	 */
	<T> T getValue(Class<T> desiredResultType) throws EvaluationException;

	/**
	 * Evaluate the expression in the default context against the specified root object. If the
	 * result of the evaluation does not match (and cannot be converted to) the expected result type
	 * then an exception will be returned.
	 * <p>
	 *  根据指定的根对象评估默认上下文中的表达式如果评估结果与预期的结果类型不匹配(并且不能转换为),则将返回异常
	 * 
	 * 
	 * @param rootObject the root object against which properties/etc will be resolved
	 * @param desiredResultType the class the caller would like the result to be
	 * @return the evaluation result
	 * @throws EvaluationException if there is a problem during evaluation
	 */
	<T> T getValue(Object rootObject, Class<T> desiredResultType) throws EvaluationException;

	/**
	 * Evaluate this expression in the provided context and return the result of evaluation.
	 * <p>
	 * 在提供的上下文中评估此表达式并返回评估结果
	 * 
	 * 
	 * @param context the context in which to evaluate the expression
	 * @return the evaluation result
	 * @throws EvaluationException if there is a problem during evaluation
	 */
	Object getValue(EvaluationContext context) throws EvaluationException;

	/**
	 * Evaluate this expression in the provided context and return the result of evaluation, but use
	 * the supplied root context as an override for any default root object specified in the context.
	 * <p>
	 *  在提供的上下文中评估此表达式并返回评估结果,但使用提供的根上下文作为上下文中指定的任何默认根对象的覆盖
	 * 
	 * 
	 * @param context the context in which to evaluate the expression
	 * @param rootObject the root object against which properties/etc will be resolved
	 * @return the evaluation result
	 * @throws EvaluationException if there is a problem during evaluation
	 */
	Object getValue(EvaluationContext context, Object rootObject) throws EvaluationException;

	/**
	 * Evaluate the expression in a specified context which can resolve references to properties, methods, types, etc -
	 * the type of the evaluation result is expected to be of a particular class and an exception will be thrown if it
	 * is not and cannot be converted to that type.
	 * <p>
	 *  评估指定上下文中的表达式,可以解析对属性,方法,类型等的引用 - 评估结果的类型预计是特定类,如果不是,则抛出异常,并且不能转换为类型
	 * 
	 * 
	 * @param context the context in which to evaluate the expression
	 * @param desiredResultType the class the caller would like the result to be
	 * @return the evaluation result
	 * @throws EvaluationException if there is a problem during evaluation
	 */
	<T> T getValue(EvaluationContext context, Class<T> desiredResultType) throws EvaluationException;

	/**
	 * Evaluate the expression in a specified context which can resolve references to properties, methods, types, etc -
	 * the type of the evaluation result is expected to be of a particular class and an exception will be thrown if it
	 * is not and cannot be converted to that type.  The supplied root object overrides any default specified on the
	 * supplied context.
	 * <p>
	 * 评估指定上下文中的表达式,可以解析对属性,方法,类型等的引用 - 评估结果的类型预计是特定类,如果不是,则抛出异常,并且不能转换为类型提供的根对象覆盖在提供的上下文上指定的任何默认值
	 * 
	 * 
	 * @param context the context in which to evaluate the expression
	 * @param rootObject the root object against which properties/etc will be resolved
	 * @param desiredResultType the class the caller would like the result to be
	 * @return the evaluation result
	 * @throws EvaluationException if there is a problem during evaluation
	 */
	<T> T getValue(EvaluationContext context, Object rootObject, Class<T> desiredResultType) throws EvaluationException;

	/**
	 * Returns the most general type that can be passed to the {@link #setValue(EvaluationContext, Object)}
	 * method using the default context.
	 * <p>
	 *  返回可以使用默认上下文传递给{@link #setValue(EvaluationContext,Object)}方法的最通用类型
	 * 
	 * 
	 * @return the most general type of value that can be set on this context
	 * @throws EvaluationException if there is a problem determining the type
	 */
	Class<?> getValueType() throws EvaluationException;

	/**
	 * Returns the most general type that can be passed to the {@link #setValue(EvaluationContext, Object)}
	 * method using the default context.
	 * <p>
	 *  返回可以使用默认上下文传递给{@link #setValue(EvaluationContext,Object)}方法的最通用类型
	 * 
	 * 
	 * @param rootObject the root object against which to evaluate the expression
	 * @return the most general type of value that can be set on this context
	 * @throws EvaluationException if there is a problem determining the type
	 */
	Class<?> getValueType(Object rootObject) throws EvaluationException;

	/**
	 * Returns the most general type that can be passed to the {@link #setValue(EvaluationContext, Object)}
	 * method for the given context.
	 * <p>
	 *  返回可以传递给给定上下文的{@link #setValue(EvaluationContext,Object)}方法的最常见类型
	 * 
	 * 
	 * @param context the context in which to evaluate the expression
	 * @return the most general type of value that can be set on this context
	 * @throws EvaluationException if there is a problem determining the type
	 */
	Class<?> getValueType(EvaluationContext context) throws EvaluationException;

	/**
	 * Returns the most general type that can be passed to the {@link #setValue(EvaluationContext, Object)}
	 * method for the given context. The supplied root object overrides any specified in the context.
	 * <p>
	 * 返回可以传递给给定上下文的{@link #setValue(EvaluationContext,Object)}方法的最通用类型提供的根对象覆盖上下文中指定的任何内容
	 * 
	 * 
	 * @param context the context in which to evaluate the expression
	 * @param rootObject the root object against which to evaluate the expression
	 * @return the most general type of value that can be set on this context
	 * @throws EvaluationException if there is a problem determining the type
	 */
	Class<?> getValueType(EvaluationContext context, Object rootObject) throws EvaluationException;

	/**
	 * Returns the most general type that can be passed to the {@link #setValue(EvaluationContext, Object)}
	 * method using the default context.
	 * <p>
	 *  返回可以使用默认上下文传递给{@link #setValue(EvaluationContext,Object)}方法的最通用类型
	 * 
	 * 
	 * @return a type descriptor for the most general type of value that can be set on this context
	 * @throws EvaluationException if there is a problem determining the type
	 */
	TypeDescriptor getValueTypeDescriptor() throws EvaluationException;

	/**
	 * Returns the most general type that can be passed to the {@link #setValue(EvaluationContext, Object)}
	 * method using the default context.
	 * <p>
	 *  返回可以使用默认上下文传递给{@link #setValue(EvaluationContext,Object)}方法的最通用类型
	 * 
	 * 
	 * @param rootObject the root object against which to evaluate the expression
	 * @return a type descriptor for the most general type of value that can be set on this context
	 * @throws EvaluationException if there is a problem determining the type
	 */
	TypeDescriptor getValueTypeDescriptor(Object rootObject) throws EvaluationException;

	/**
	 * Returns the most general type that can be passed to the {@link #setValue(EvaluationContext, Object)}
	 * method for the given context.
	 * <p>
	 *  返回可以传递给给定上下文的{@link #setValue(EvaluationContext,Object)}方法的最常见类型
	 * 
	 * 
	 * @param context the context in which to evaluate the expression
	 * @return a type descriptor for the most general type of value that can be set on this context
	 * @throws EvaluationException if there is a problem determining the type
	 */
	TypeDescriptor getValueTypeDescriptor(EvaluationContext context) throws EvaluationException;

	/**
	 * Returns the most general type that can be passed to the {@link #setValue(EvaluationContext, Object)} method for
	 * the given context. The supplied root object overrides any specified in the context.
	 * <p>
	 *  返回可以传递给给定上下文的{@link #setValue(EvaluationContext,Object)}方法的最通用类型提供的根对象覆盖上下文中指定的任何内容
	 * 
	 * 
	 * @param context the context in which to evaluate the expression
	 * @param rootObject the root object against which to evaluate the expression
	 * @return a type descriptor for the most general type of value that can be set on this context
	 * @throws EvaluationException if there is a problem determining the type
	 */
	TypeDescriptor getValueTypeDescriptor(EvaluationContext context, Object rootObject) throws EvaluationException;

	/**
	 * Determine if an expression can be written to, i.e. setValue() can be called.
	 * <p>
	 * 确定一个表达式是否可以写入,即可以调用setValue()
	 * 
	 * 
	 * @param context the context in which the expression should be checked
	 * @return true if the expression is writable
	 * @throws EvaluationException if there is a problem determining if it is writable
	 */
	boolean isWritable(EvaluationContext context) throws EvaluationException;

	/**
	 * Determine if an expression can be written to, i.e. setValue() can be called.
	 * The supplied root object overrides any specified in the context.
	 * <p>
	 *  确定表达式是否可以被写入,即可以调用setValue()。提供的根对象覆盖上下文中指定的任何内容
	 * 
	 * 
	 * @param context the context in which the expression should be checked
	 * @param rootObject the root object against which to evaluate the expression
	 * @return true if the expression is writable
	 * @throws EvaluationException if there is a problem determining if it is writable
	 */
	boolean isWritable(EvaluationContext context, Object rootObject) throws EvaluationException;

	/**
	 * Determine if an expression can be written to, i.e. setValue() can be called.
	 * <p>
	 *  确定一个表达式是否可以写入,即可以调用setValue()
	 * 
	 * 
	 * @param rootObject the root object against which to evaluate the expression
	 * @return true if the expression is writable
	 * @throws EvaluationException if there is a problem determining if it is writable
	 */
	boolean isWritable(Object rootObject) throws EvaluationException;

	/**
	 * Set this expression in the provided context to the value provided.
	 *
	 * <p>
	 *  在提供的上下文中将此表达式设置为提供的值
	 * 
	 * 
	 * @param context the context in which to set the value of the expression
	 * @param value the new value
	 * @throws EvaluationException if there is a problem during evaluation
	 */
	void setValue(EvaluationContext context, Object value) throws EvaluationException;

	/**
	 * Set this expression in the provided context to the value provided.
	 * <p>
	 *  将提供的上下文中的此表达式设置为提供的值
	 * 
	 * 
	 * @param rootObject the root object against which to evaluate the expression
	 * @param value the new value
	 * @throws EvaluationException if there is a problem during evaluation
	 */
	void setValue(Object rootObject, Object value) throws EvaluationException;

	/**
	 * Set this expression in the provided context to the value provided.
	 * The supplied root object overrides any specified in the context.
	 * <p>
	 *  在提供的上下文中将此表达式设置为提供的值。提供的根对象覆盖上下文中指定的任何内容
	 * 
	 * 
	 * @param context the context in which to set the value of the expression
	 * @param rootObject the root object against which to evaluate the expression
	 * @param value the new value
	 * @throws EvaluationException if there is a problem during evaluation
	 */
	void setValue(EvaluationContext context, Object rootObject, Object value) throws EvaluationException;

	/**
	 * Returns the original string used to create this expression, unmodified.
	 * <p>
	 *  返回用于创建此表达式的原始字符串,未修改
	 * 
	 * @return the original expression string
	 */
	String getExpressionString();

}
