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

import java.util.List;

/**
 * Expressions are executed in an evaluation context. It is in this context that
 * references are resolved when encountered during expression evaluation.
 *
 * <p>There is a default implementation of the EvaluationContext,
 * {@link org.springframework.expression.spel.support.StandardEvaluationContext} that can
 * be extended, rather than having to implement everything.
 *
 * <p>
 *  表达式在评估上下文中执行在这种情况下,在表达式求值期间遇到引用时会被解析
 * 
 * <p>有一个可以扩展的EvaluationContext {@link orgspringframeworkexpressionspelsupportStandardEvaluationContext}
 * 的默认实现,而不是实现一切。
 * 
 * 
 * @author Andy Clement
 * @author Juergen Hoeller
 * @since 3.0
 */
public interface EvaluationContext {

	/**
	 * Return the default root context object against which unqualified
	 * properties/methods/etc should be resolved. This can be overridden
	 * when evaluating an expression.
	 * <p>
	 *  返回默认的根上下文对象,该对象应该被解析为不合格的属性/方法/ etc。这可以在评估表达式时被覆盖
	 * 
	 */
	TypedValue getRootObject();

	/**
	 * Return a list of resolvers that will be asked in turn to locate a constructor.
	 * <p>
	 *  返回一个解析器的列表,这些列表将被轮询询问以查找构造函数
	 * 
	 */
	List<ConstructorResolver> getConstructorResolvers();

	/**
	 * Return a list of resolvers that will be asked in turn to locate a method.
	 * <p>
	 *  返回一个解析器的列表,这些列表将被依次询问以找到一个方法
	 * 
	 */
	List<MethodResolver> getMethodResolvers();

	/**
	 * Return a list of accessors that will be asked in turn to read/write a property.
	 * <p>
	 *  返回一个将被要求读/写一个属性的访问器列表
	 * 
	 */
	List<PropertyAccessor> getPropertyAccessors();

	/**
	 * Return a type locator that can be used to find types, either by short or
	 * fully qualified name.
	 * <p>
	 *  返回一个可用于查找类型的类型定位器,通过短或完全限定名称
	 * 
	 */
	TypeLocator getTypeLocator();

	/**
	 * Return a type converter that can convert (or coerce) a value from one type to another.
	 * <p>
	 *  返回一个类型转换器,可以将值从一种类型转换(或强制)到另一种类型
	 * 
	 */
	TypeConverter getTypeConverter();

	/**
	 * Return a type comparator for comparing pairs of objects for equality.
	 * <p>
	 * 返回一个类型比较器,用于比较对象对象的相等性
	 * 
	 */
	TypeComparator getTypeComparator();

	/**
	 * Return an operator overloader that may support mathematical operations
	 * between more than the standard set of types.
	 * <p>
	 *  返回一个可能支持超过标准类型的数学运算的运算符重载
	 * 
	 */
	OperatorOverloader getOperatorOverloader();

	/**
	 * Return a bean resolver that can look up beans by name.
	 * <p>
	 *  返回一个可以通过名称查找bean的bean解析器
	 * 
	 */
	BeanResolver getBeanResolver();

	/**
	 * Set a named variable within this evaluation context to a specified value.
	 * <p>
	 *  将此评估上下文中的命名变量设置为指定值
	 * 
	 * 
	 * @param name variable to set
	 * @param value value to be placed in the variable
	 */
	void setVariable(String name, Object value);

	/**
	 * Look up a named variable within this evaluation context.
	 * <p>
	 *  在此评估上下文中查找一个命名变量
	 * 
	 * @param name variable to lookup
	 * @return the value of the variable
	 */
	Object lookupVariable(String name);

}
