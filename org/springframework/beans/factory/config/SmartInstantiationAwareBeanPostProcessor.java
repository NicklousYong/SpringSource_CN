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

package org.springframework.beans.factory.config;

import java.lang.reflect.Constructor;

import org.springframework.beans.BeansException;

/**
 * Extension of the {@link InstantiationAwareBeanPostProcessor} interface,
 * adding a callback for predicting the eventual type of a processed bean.
 *
 * <p><b>NOTE:</b> This interface is a special purpose interface, mainly for
 * internal use within the framework. In general, application-provided
 * post-processors should simply implement the plain {@link BeanPostProcessor}
 * interface or derive from the {@link InstantiationAwareBeanPostProcessorAdapter}
 * class. New methods might be added to this interface even in point releases.
 *
 * <p>
 *  扩展{@link InstantiationAwareBeanPostProcessor}接口,添加回调以预测已处理的bean的最终类型
 * 
 * <p> <b>注意：</b>此接口是一个专用接口,主要用于框架内部使用通常,应用程序提供的后处理器应该简单地实现简单的{@link BeanPostProcessor}接口或从即使在点版本中,也可以将{@link InstantiateAwareBeanPostProcessorAdapter}
 * 类新方法添加到此接口。
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.0.3
 * @see InstantiationAwareBeanPostProcessorAdapter
 */
public interface SmartInstantiationAwareBeanPostProcessor extends InstantiationAwareBeanPostProcessor {

	/**
	 * Predict the type of the bean to be eventually returned from this
	 * processor's {@link #postProcessBeforeInstantiation} callback.
	 * <p>
	 *  预测最终从该处理器的{@link #postProcessBeforeInstantiation}回调返回的bean的类型
	 * 
	 * 
	 * @param beanClass the raw class of the bean
	 * @param beanName the name of the bean
	 * @return the type of the bean, or {@code null} if not predictable
	 * @throws org.springframework.beans.BeansException in case of errors
	 */
	Class<?> predictBeanType(Class<?> beanClass, String beanName) throws BeansException;

	/**
	 * Determine the candidate constructors to use for the given bean.
	 * <p>
	 *  确定用于给定bean的候选构造函数
	 * 
	 * 
	 * @param beanClass the raw class of the bean (never {@code null})
	 * @param beanName the name of the bean
	 * @return the candidate constructors, or {@code null} if none specified
	 * @throws org.springframework.beans.BeansException in case of errors
	 */
	Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, String beanName) throws BeansException;

	/**
	 * Obtain a reference for early access to the specified bean,
	 * typically for the purpose of resolving a circular reference.
	 * <p>This callback gives post-processors a chance to expose a wrapper
	 * early - that is, before the target bean instance is fully initialized.
	 * The exposed object should be equivalent to the what
	 * {@link #postProcessBeforeInitialization} / {@link #postProcessAfterInitialization}
	 * would expose otherwise. Note that the object returned by this method will
	 * be used as bean reference unless the post-processor returns a different
	 * wrapper from said post-process callbacks. In other words: Those post-process
	 * callbacks may either eventually expose the same reference or alternatively
	 * return the raw bean instance from those subsequent callbacks (if the wrapper
	 * for the affected bean has been built for a call to this method already,
	 * it will be exposes as final bean reference by default).
	 * <p>
	 * 获取早期访问指定bean的引用,通常用于解决循环引用<p>此回调给后处理器提供了早期公开包装器的机会 - 也就是在目标bean实例完全初始化之前暴露对象应等同于{@link #postProcessBeforeInitialization}
	 *  / {@link #postProcessAfterInitialization}将公开的内容注意,此方法返回的对象将被用作bean引用,除非后处理程序从所述后处理程序返回不同的包装器,进程回调换句话
	 * 
	 * @param bean the raw bean instance
	 * @param beanName the name of the bean
	 * @return the object to expose as bean reference
	 * (typically with the passed-in bean instance as default)
	 * @throws org.springframework.beans.BeansException in case of errors
	 */
	Object getEarlyBeanReference(Object bean, String beanName) throws BeansException;

}
