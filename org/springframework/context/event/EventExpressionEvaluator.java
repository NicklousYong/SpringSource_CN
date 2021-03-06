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

package org.springframework.context.event;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

/**
 * Utility class handling the SpEL expression parsing. Meant to be used
 * as a reusable, thread-safe component.
 *
 * <p>
 *  处理Spel表达式的实用程序类解析Meant被用作可重用,线程安全的组件
 * 
 * 
 * @author Stephane Nicoll
 * @since 4.2
 * @see CachedExpressionEvaluator
 */
class EventExpressionEvaluator extends CachedExpressionEvaluator {

	private final Map<ExpressionKey, Expression> conditionCache = new ConcurrentHashMap<ExpressionKey, Expression>(64);

	private final Map<AnnotatedElementKey, Method> targetMethodCache = new ConcurrentHashMap<AnnotatedElementKey, Method>(64);


	/**
	 * Create the suitable {@link EvaluationContext} for the specified event handling
	 * on the specified method.
	 * <p>
	 *  为指定的方法为指定的事件处理创建合适的{@link EvaluationContext}
	 * 
	 */
	public EvaluationContext createEvaluationContext(ApplicationEvent event, Class<?> targetClass,
			Method method, Object[] args, BeanFactory beanFactory) {

		Method targetMethod = getTargetMethod(targetClass, method);
		EventExpressionRootObject root = new EventExpressionRootObject(event, args);
		MethodBasedEvaluationContext evaluationContext = new MethodBasedEvaluationContext(
				root, targetMethod, args, getParameterNameDiscoverer());
		if (beanFactory != null) {
			evaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));
		}
		return evaluationContext;
	}

	/**
	 * Specify if the condition defined by the specified expression matches.
	 * <p>
	 * 指定由指定表达式定义的条件是否匹配
	 */
	public boolean condition(String conditionExpression,
			AnnotatedElementKey elementKey, EvaluationContext evalContext) {

		return getExpression(this.conditionCache, elementKey, conditionExpression)
				.getValue(evalContext, boolean.class);
	}

	private Method getTargetMethod(Class<?> targetClass, Method method) {
		AnnotatedElementKey methodKey = new AnnotatedElementKey(method, targetClass);
		Method targetMethod = this.targetMethodCache.get(methodKey);
		if (targetMethod == null) {
			targetMethod = AopUtils.getMostSpecificMethod(method, targetClass);
			if (targetMethod == null) {
				targetMethod = method;
			}
			this.targetMethodCache.put(methodKey, targetMethod);
		}
		return targetMethod;
	}

}
