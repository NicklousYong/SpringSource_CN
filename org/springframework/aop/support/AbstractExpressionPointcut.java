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

package org.springframework.aop.support;

import java.io.Serializable;

/**
 * Abstract superclass for expression pointcuts,
 * offering location and expression properties.
 *
 * <p>
 *  抽象超类用于表达切入点,提供位置和表达属性
 * 
 * 
 * @author Rod Johnson
 * @author Rob Harrop
 * @since 2.0
 * @see #setLocation
 * @see #setExpression
 */
@SuppressWarnings("serial")
public abstract class AbstractExpressionPointcut implements ExpressionPointcut, Serializable {

	private String location;

	private String expression;


	/**
	 * Set the location for debugging.
	 * <p>
	 *  设置调试位置
	 * 
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * Return location information about the pointcut expression
	 * if available. This is useful in debugging.
	 * <p>
	 *  返回有关切入点表达式的位置信息(如果可用)这在调试中非常有用
	 * 
	 * 
	 * @return location information as a human-readable String,
	 * or {@code null} if none is available
	 */
	public String getLocation() {
		return this.location;
	}

	public void setExpression(String expression) {
		this.expression = expression;
		try {
			onSetExpression(expression);
		}
		catch (IllegalArgumentException ex) {
			// Fill in location information if possible.
			if (this.location != null) {
				throw new IllegalArgumentException("Invalid expression at location [" + this.location + "]: " + ex);
			}
			else {
				throw ex;
			}
		}
	}

	/**
	 * Called when a new pointcut expression is set.
	 * The expression should be parsed at this point if possible.
	 * <p>This implementation is empty.
	 * <p>
	 * 当设置一个新的切入点表达式时调用如果可能,应该在此处解析表达式<p>此实现为空
	 * 
	 * 
	 * @param expression expression to set
	 * @throws IllegalArgumentException if the expression is invalid
	 * @see #setExpression
	 */
	protected void onSetExpression(String expression) throws IllegalArgumentException {
	}

	/**
	 * Return this pointcut's expression.
	 * <p>
	 *  返回这个切入点的表达式
	 */
	@Override
	public String getExpression() {
		return this.expression;
	}

}
