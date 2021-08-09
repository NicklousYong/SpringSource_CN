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

package org.springframework.cache.interceptor;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.ParameterNameDiscoverer;

/**
 * Cache specific evaluation context that adds a method parameters as SpEL
 * variables, in a lazy manner. The lazy nature eliminates unneeded
 * parsing of classes byte code for parameter discovery.
 *
 * <p>Also define a set of "unavailable variables" (i.e. variables that should
 * lead to an exception right the way when they are accessed). This can be useful
 * to verify a condition does not match even when not all potential variables
 * are present.
 *
 * <p>To limit the creation of objects, an ugly constructor is used
 * (rather then a dedicated 'closure'-like class for deferred execution).
 *
 * <p>
 *  缓存特定的评估上下文,以懒惰的方式将方法参数添加为SpEL变量懒惰的本质消除了不必要的解析类参数发现的字节码
 * 
 * <p>还定义了一组"不可用变量"(即,在访问时应该导致异常的变量)这对于验证条件不匹配(即使不是所有潜在变量都不存在)也是有用的
 * 
 *  <p>为了限制对象的创建,使用了一个丑陋的构造函数(而不是一个专门的"闭包"类用于延迟执行)
 * 
 * 
 * @author Costin Leau
 * @author Stephane Nicoll
 * @author Juergen Hoeller
 * @since 3.1
 */
class CacheEvaluationContext extends MethodBasedEvaluationContext {

	private final Set<String> unavailableVariables = new HashSet<String>(1);


	CacheEvaluationContext(Object rootObject, Method method, Object[] arguments,
			ParameterNameDiscoverer parameterNameDiscoverer) {

		super(rootObject, method, arguments, parameterNameDiscoverer);
	}


	/**
	 * Add the specified variable name as unavailable for that context.
	 * Any expression trying to access this variable should lead to an exception.
	 * <p>This permits the validation of expressions that could potentially a
	 * variable even when such variable isn't available yet. Any expression
	 * trying to use that variable should therefore fail to evaluate.
	 * <p>
	 */
	public void addUnavailableVariable(String name) {
		this.unavailableVariables.add(name);
	}


	/**
	 * Load the param information only when needed.
	 * <p>
	 *  将指定的变量名称添加为该上下文不可用任何尝试访问此变量的表达式都将导致异常<p>这样可以验证可能会有变量的表达式,即使这样的变量不可用还有任何试图使用的表达式那个变量应该不能评估
	 * 
	 */
	@Override
	public Object lookupVariable(String name) {
		if (this.unavailableVariables.contains(name)) {
			throw new VariableNotAvailableException(name);
		}
		return super.lookupVariable(name);
	}

}
