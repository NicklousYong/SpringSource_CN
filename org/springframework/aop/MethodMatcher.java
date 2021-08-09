/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2015 the original author or authors.
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

package org.springframework.aop;

import java.lang.reflect.Method;

/**
 * Part of a {@link Pointcut}: Checks whether the target method is eligible for advice.
 *
 * <p>A MethodMatcher may be evaluated <b>statically</b> or at <b>runtime</b> (dynamically).
 * Static matching involves method and (possibly) method attributes. Dynamic matching
 * also makes arguments for a particular call available, and any effects of running
 * previous advice applying to the joinpoint.
 *
 * <p>If an implementation returns {@code false} from its {@link #isRuntime()}
 * method, evaluation can be performed statically, and the result will be the same
 * for all invocations of this method, whatever their arguments. This means that
 * if the {@link #isRuntime()} method returns {@code false}, the 3-arg
 * {@link #matches(java.lang.reflect.Method, Class, Object[])} method will never be invoked.
 *
 * <p>If an implementation returns {@code true} from its 2-arg
 * {@link #matches(java.lang.reflect.Method, Class)} method and its {@link #isRuntime()} method
 * returns {@code true}, the 3-arg {@link #matches(java.lang.reflect.Method, Class, Object[])}
 * method will be invoked <i>immediately before each potential execution of the related advice</i>,
 * to decide whether the advice should run. All previous advice, such as earlier interceptors
 * in an interceptor chain, will have run, so any state changes they have produced in
 * parameters or ThreadLocal state will be available at the time of evaluation.
 *
 * <p>
 *  {@link Pointcut}的一部分：检查目标方法是否有资格获得建议
 * 
 * <p>可以静态</b>或<b>运行时</b>(动态)评估MethodMatcher静态匹配涉及方法和(可能)方法属性动态匹配还使特定调用的参数可用,以及运行以前的建议的任何效果应用于连接点
 * 
 *  <p>如果一个实现从{@link #isRuntime()}方法返回{@code false},则可以静态执行评估,对于此方法的所有调用,结果将是相同的,无论其参数如何如果{@link #isRuntime()}
 * 方法返回{@code false},那么3-arg {@link #matches(javalangreflectMethod,Class,Object [])}方法将永远不会被调用。
 * 
 * <p>如果一个实现从其2-arg {@link #matches(javalangreflectMethod,Class))方法返回{@code true},并且其{@link #isRuntime())方法返回{@code true}
 * ,则3- arg {@link #matches(javalangreflectMethod,Class,Object [])}方法将在每个潜在执行相关建议之前立即调用<i>,以决定该建议是否应该运行所
 * 有以前的建议,例如拦截器链中的早期拦截器将运行,因此在参数或ThreadLocal状态下生成的任何状态更改将在评估时可用。
 * 
 * 
 * @author Rod Johnson
 * @since 11.11.2003
 * @see Pointcut
 * @see ClassFilter
 */
public interface MethodMatcher {

	/**
	 * Perform static checking whether the given method matches. If this
	 * returns {@code false} or if the {@link #isRuntime()} method
	 * returns {@code false}, no runtime check (i.e. no.
	 * {@link #matches(java.lang.reflect.Method, Class, Object[])} call) will be made.
	 * <p>
	 * 执行静态检查是否给定方法匹配如果这返回{@code false},或者如果{@link #isRuntime())方法返回{@code false},则不执行运行时检查(即没有{@link #matches(javalangreflectMethod,Class ,Object [])}
	 * 调用)。
	 * 
	 * 
	 * @param method the candidate method
	 * @param targetClass the target class (may be {@code null}, in which case
	 * the candidate class must be taken to be the method's declaring class)
	 * @return whether or not this method matches statically
	 */
	boolean matches(Method method, Class<?> targetClass);

	/**
	 * Is this MethodMatcher dynamic, that is, must a final call be made on the
	 * {@link #matches(java.lang.reflect.Method, Class, Object[])} method at
	 * runtime even if the 2-arg matches method returns {@code true}?
	 * <p>Can be invoked when an AOP proxy is created, and need not be invoked
	 * again before each method invocation,
	 * <p>
	 *  这个MethodMatcher是动态的,也就是说,即使2-arg匹配方法返回{@code true},运行时也必须在{@link #matches(javalangreflectMethod,Class,Object [])}
	 * 方法进行最终调用。
	 *  <p>可以在创建AOP代理时调用,并且不必在每个方法调用之前再次调用它,。
	 * 
	 * 
	 * @return whether or not a runtime match via the 3-arg
	 * {@link #matches(java.lang.reflect.Method, Class, Object[])} method
	 * is required if static matching passed
	 */
	boolean isRuntime();

	/**
	 * Check whether there a runtime (dynamic) match for this method,
	 * which must have matched statically.
	 * <p>This method is invoked only if the 2-arg matches method returns
	 * {@code true} for the given method and target class, and if the
	 * {@link #isRuntime()} method returns {@code true}. Invoked
	 * immediately before potential running of the advice, after any
	 * advice earlier in the advice chain has run.
	 * <p>
	 * 检查这个方法是否有运行时(动态)匹配,它必须静态匹配<p>只有在给定方法和目标类的2-arg matches方法返回{@code true}时,才调用此方法,如果{@link #isRuntime()}
	 * 方法返回{@code true}在潜在运行建议之前立即调用,在建议链中早先的任何建议运行后。
	 * 
	 * 
	 * @param method the candidate method
	 * @param targetClass the target class (may be {@code null}, in which case
	 * the candidate class must be taken to be the method's declaring class)
	 * @param args arguments to the method
	 * @return whether there's a runtime match
	 * @see MethodMatcher#matches(Method, Class)
	 */
	boolean matches(Method method, Class<?> targetClass, Object... args);


	/**
	 * Canonical instance that matches all methods.
	 * <p>
	 */
	MethodMatcher TRUE = TrueMethodMatcher.INSTANCE;

}
