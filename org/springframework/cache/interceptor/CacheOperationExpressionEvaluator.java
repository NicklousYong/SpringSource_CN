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
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.cache.Cache;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

/**
 * Utility class handling the SpEL expression parsing.
 * Meant to be used as a reusable, thread-safe component.
 *
 * <p>Performs internal caching for performance reasons
 * using {@link AnnotatedElementKey}.
 *
 * <p>
 *  处理Spel表达式的实用程序类解析Meant被用作可重用,线程安全的组件
 * 
 *  <p>出于性能原因,使用{@link AnnotatedElementKey}执行内部缓存
 * 
 * 
 * @author Costin Leau
 * @author Phillip Webb
 * @author Sam Brannen
 * @author Stephane Nicoll
 * @since 3.1
 */
class CacheOperationExpressionEvaluator extends CachedExpressionEvaluator {

	/**
	 * Indicate that there is no result variable.
	 * <p>
	 * 表明没有结果变量
	 * 
	 */
	public static final Object NO_RESULT = new Object();

	/**
	 * Indicate that the result variable cannot be used at all.
	 * <p>
	 *  表明结果变量根本无法使用
	 * 
	 */
	public static final Object RESULT_UNAVAILABLE = new Object();

	/**
	 * The name of the variable holding the result object.
	 * <p>
	 *  保存结果对象的变量的名称
	 * 
	 */
	public static final String RESULT_VARIABLE = "result";


	private final Map<ExpressionKey, Expression> keyCache = new ConcurrentHashMap<ExpressionKey, Expression>(64);

	private final Map<ExpressionKey, Expression> conditionCache = new ConcurrentHashMap<ExpressionKey, Expression>(64);

	private final Map<ExpressionKey, Expression> unlessCache = new ConcurrentHashMap<ExpressionKey, Expression>(64);

	private final Map<AnnotatedElementKey, Method> targetMethodCache =
			new ConcurrentHashMap<AnnotatedElementKey, Method>(64);


	/**
	 * Create an {@link EvaluationContext} without a return value.
	 * <p>
	 *  创建一个没有返回值的{@link EvaluationContext}
	 * 
	 * 
	 * @see #createEvaluationContext(Collection, Method, Object[], Object, Class, Object, BeanFactory)
	 */
	public EvaluationContext createEvaluationContext(Collection<? extends Cache> caches,
			Method method, Object[] args, Object target, Class<?> targetClass, BeanFactory beanFactory) {

		return createEvaluationContext(caches, method, args, target, targetClass, NO_RESULT, beanFactory);
	}

	/**
	 * Create an {@link EvaluationContext}.
	 * <p>
	 *  创建一个{@link EvaluationContext}
	 * 
	 * 
	 * @param caches the current caches
	 * @param method the method
	 * @param args the method arguments
	 * @param target the target object
	 * @param targetClass the target class
	 * @param result the return value (can be {@code null}) or
	 * {@link #NO_RESULT} if there is no return at this time
	 * @return the evaluation context
	 */
	public EvaluationContext createEvaluationContext(Collection<? extends Cache> caches,
			Method method, Object[] args, Object target, Class<?> targetClass, Object result,
			BeanFactory beanFactory) {

		CacheExpressionRootObject rootObject = new CacheExpressionRootObject(
				caches, method, args, target, targetClass);
		Method targetMethod = getTargetMethod(targetClass, method);
		CacheEvaluationContext evaluationContext = new CacheEvaluationContext(
				rootObject, targetMethod, args, getParameterNameDiscoverer());
		if (result == RESULT_UNAVAILABLE) {
			evaluationContext.addUnavailableVariable(RESULT_VARIABLE);
		}
		else if (result != NO_RESULT) {
			evaluationContext.setVariable(RESULT_VARIABLE, result);
		}
		if (beanFactory != null) {
			evaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));
		}
		return evaluationContext;
	}

	public Object key(String keyExpression, AnnotatedElementKey methodKey, EvaluationContext evalContext) {
		return getExpression(this.keyCache, methodKey, keyExpression).getValue(evalContext);
	}

	public boolean condition(String conditionExpression, AnnotatedElementKey methodKey, EvaluationContext evalContext) {
		return getExpression(this.conditionCache, methodKey, conditionExpression).getValue(evalContext, boolean.class);
	}

	public boolean unless(String unlessExpression, AnnotatedElementKey methodKey, EvaluationContext evalContext) {
		return getExpression(this.unlessCache, methodKey, unlessExpression).getValue(evalContext, boolean.class);
	}

	/**
	 * Clear all caches.
	 * <p>
	 *  清除所有缓存
	 */
	void clear() {
		this.keyCache.clear();
		this.conditionCache.clear();
		this.unlessCache.clear();
		this.targetMethodCache.clear();
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
