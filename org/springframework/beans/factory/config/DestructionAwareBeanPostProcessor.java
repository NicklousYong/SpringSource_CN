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

package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;

/**
 * Subinterface of {@link BeanPostProcessor} that adds a before-destruction callback.
 *
 * <p>The typical usage will be to invoke custom destruction callbacks on
 * specific bean types, matching corresponding initialization callbacks.
 *
 * <p>
 *  {@link BeanPostProcessor}的子界面,添加了破坏前的回调
 * 
 *  <p>典型的用法是调用特定bean类型的自定义销毁回调,匹配相应的初始化回调
 * 
 * 
 * @author Juergen Hoeller
 * @since 1.0.1
 */
public interface DestructionAwareBeanPostProcessor extends BeanPostProcessor {

	/**
	 * Apply this BeanPostProcessor to the given bean instance before
	 * its destruction. Can invoke custom destruction callbacks.
	 * <p>Like DisposableBean's {@code destroy} and a custom destroy method,
	 * this callback just applies to singleton beans in the factory (including
	 * inner beans).
	 * <p>
	 * 将此BeanPostProcessor应用于给定的bean实例,在其销毁之前可以调用自定义破坏回调<p>像DisposableBean的{@code destroy}和一个自定义的destroy方法一样
	 * ,这个回调函数只适用于工厂中的单例bean(包括内部bean)。
	 * 
	 * 
	 * @param bean the bean instance to be destroyed
	 * @param beanName the name of the bean
	 * @throws org.springframework.beans.BeansException in case of errors
	 * @see org.springframework.beans.factory.DisposableBean
	 * @see org.springframework.beans.factory.support.AbstractBeanDefinition#setDestroyMethodName
	 */
	void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException;

	/**
	 * Determine whether the given bean instance requires destruction by this
	 * post-processor.
	 * <p><b>NOTE:</b> Even as a late addition, this method has been introduced on
	 * {@code DestructionAwareBeanPostProcessor} itself instead of on a SmartDABPP
	 * subinterface. This allows existing {@code DestructionAwareBeanPostProcessor}
	 * implementations to easily provide {@code requiresDestruction} logic while
	 * retaining compatibility with Spring <4.3, and it is also an easier onramp to
	 * declaring {@code requiresDestruction} as a Java 8 default method in Spring 5.
	 * <p>If an implementation of {@code DestructionAwareBeanPostProcessor} does
	 * not provide a concrete implementation of this method, Spring's invocation
	 * mechanism silently assumes a method returning {@code true} (the effective
	 * default before 4.3, and the to-be-default in the Java 8 method in Spring 5).
	 * <p>
	 * 确定给定的b​​ean实例是否需要这个后处理程序的破坏<p> <b>注意：</b>即使是一个晚期的添加,这个方法已经在{@code DestructionAwareBeanPostProcessor}本
	 * 身而不是在SmartDABPP子接口上引入允许现有的{@code DestructionAwareBeanPostProcessor}实现容易地提供{@code requiresDestruction}
	 * 逻辑,同时保持与Spring <43的兼容性,并且在Spring 5中将声明{@code requireDestruction}作为Java 8默认方法也是一个更简单的方法。
	 * 
	 * @param bean the bean instance to check
	 * @return {@code true} if {@link #postProcessBeforeDestruction} is supposed to
	 * be called for this bean instance eventually, or {@code false} if not needed
	 * @since 4.3
	 */
	boolean requiresDestruction(Object bean);

}
