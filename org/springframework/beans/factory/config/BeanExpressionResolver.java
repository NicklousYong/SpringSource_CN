/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2008 the original author or authors.
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

package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;

/**
 * Strategy interface for resolving a value through evaluating it
 * as an expression, if applicable.
 *
 * <p>A raw {@link org.springframework.beans.factory.BeanFactory} does not
 * contain a default implementation of this strategy. However,
 * {@link org.springframework.context.ApplicationContext} implementations
 * will provide expression support out of the box.
 *
 * <p>
 *  通过将其评估为表达式(如果适用)来解析值的策略界面
 * 
 * <p>原始{@link orgspringframeworkbeansfactoryBeanFactory}不包含此策略的默认实现但是,{@link orgspringframeworkcontextApplicationContext}
 * 实现将提供开箱即用的表达式支持。
 * 
 * @author Juergen Hoeller
 * @since 3.0
 */
public interface BeanExpressionResolver {

	/**
	 * Evaluate the given value as an expression, if applicable;
	 * return the value as-is otherwise.
	 * <p>
	 * 
	 * 
	 * @param value the value to check
	 * @param evalContext the evaluation context
	 * @return the resolved value (potentially the given value as-is)
	 * @throws BeansException if evaluation failed
	 */
	Object evaluate(String value, BeanExpressionContext evalContext) throws BeansException;

}
