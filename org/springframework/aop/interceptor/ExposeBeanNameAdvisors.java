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

package org.springframework.aop.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import org.springframework.aop.Advisor;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.aop.support.DefaultIntroductionAdvisor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;
import org.springframework.beans.factory.NamedBean;

/**
 * Convenient methods for creating advisors that may be used when autoproxying beans
 * created with the Spring IoC container, binding the bean name to the current
 * invocation. May support a {@code bean()} pointcut designator with AspectJ.
 *
 * <p>Typically used in Spring auto-proxying, where the bean name is known
 * at proxy creation time.
 *
 * <p>
 * 在使用Spring IoC容器创建的bean自动编写bean时可以使用的方便的方法,将bean名称绑定到当前的调用可以支持带有AspectJ的{@code bean()}切入点指示符
 * 
 *  通常在Spring自动代理中使用,其中bean名称在代理创建时间已知
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 * @see org.springframework.beans.factory.NamedBean
 */
public abstract class ExposeBeanNameAdvisors {

	/**
	 * Binding for the bean name of the bean which is currently being invoked
	 * in the ReflectiveMethodInvocation userAttributes Map.
	 * <p>
	 *  绑定目前正在ReflectiveMethodInvocation中调用的bean的bean名称userAttributes Map
	 * 
	 */
	private static final String BEAN_NAME_ATTRIBUTE = ExposeBeanNameAdvisors.class.getName() + ".BEAN_NAME";


	/**
	 * Find the bean name for the current invocation. Assumes that an ExposeBeanNameAdvisor
	 * has been included in the interceptor chain, and that the invocation is exposed
	 * with ExposeInvocationInterceptor.
	 * <p>
	 *  查找当前调用的bean名称假设ExposeBeanNameAdvisor已经包含在拦截器链中,并且调用使用ExposeInvocationInterceptor
	 * 
	 * 
	 * @return the bean name (never {@code null})
	 * @throws IllegalStateException if the bean name has not been exposed
	 */
	public static String getBeanName() throws IllegalStateException {
		return getBeanName(ExposeInvocationInterceptor.currentInvocation());
	}

	/**
	 * Find the bean name for the given invocation. Assumes that an ExposeBeanNameAdvisor
	 * has been included in the interceptor chain.
	 * <p>
	 *  查找给定调用的bean名称假设ExposeBeanNameAdvisor已包含在拦截器链中
	 * 
	 * 
	 * @param mi MethodInvocation that should contain the bean name as an attribute
	 * @return the bean name (never {@code null})
	 * @throws IllegalStateException if the bean name has not been exposed
	 */
	public static String getBeanName(MethodInvocation mi) throws IllegalStateException {
		if (!(mi instanceof ProxyMethodInvocation)) {
			throw new IllegalArgumentException("MethodInvocation is not a Spring ProxyMethodInvocation: " + mi);
		}
		ProxyMethodInvocation pmi = (ProxyMethodInvocation) mi;
		String beanName = (String) pmi.getUserAttribute(BEAN_NAME_ATTRIBUTE);
		if (beanName == null) {
			throw new IllegalStateException("Cannot get bean name; not set on MethodInvocation: " + mi);
		}
		return beanName;
	}

	/**
	 * Create a new advisor that will expose the given bean name,
	 * with no introduction
	 * <p>
	 * 创建一个新的顾问,将公开给定的bean名称,而不需要介绍
	 * 
	 * 
	 * @param beanName bean name to expose
	 */
	public static Advisor createAdvisorWithoutIntroduction(String beanName) {
		return new DefaultPointcutAdvisor(new ExposeBeanNameInterceptor(beanName));
	}

	/**
	 * Create a new advisor that will expose the given bean name, introducing
	 * the NamedBean interface to make the bean name accessible without forcing
	 * the target object to be aware of this Spring IoC concept.
	 * <p>
	 *  创建一个新的顾问程序,将公开给定的bean名称,引入NamedBean接口以使bean名称可访问,而不强制目标对象注意此Spring IoC概念
	 * 
	 * 
	 * @param beanName the bean name to expose
	 */
	public static Advisor createAdvisorIntroducingNamedBean(String beanName) {
		return new DefaultIntroductionAdvisor(new ExposeBeanNameIntroduction(beanName));
	}


	/**
	 * Interceptor that exposes the specified bean name as invocation attribute.
	 * <p>
	 *  Interceptor将指定的bean名称作为调用属性公开
	 * 
	 */
	private static class ExposeBeanNameInterceptor implements MethodInterceptor {

		private final String beanName;

		public ExposeBeanNameInterceptor(String beanName) {
			this.beanName = beanName;
		}

		@Override
		public Object invoke(MethodInvocation mi) throws Throwable {
			if (!(mi instanceof ProxyMethodInvocation)) {
				throw new IllegalStateException("MethodInvocation is not a Spring ProxyMethodInvocation: " + mi);
			}
			ProxyMethodInvocation pmi = (ProxyMethodInvocation) mi;
			pmi.setUserAttribute(BEAN_NAME_ATTRIBUTE, this.beanName);
			return mi.proceed();
		}
	}


	/**
	 * Introduction that exposes the specified bean name as invocation attribute.
	 * <p>
	 *  引用将指定的bean名称作为调用属性
	 */
	@SuppressWarnings("serial")
	private static class ExposeBeanNameIntroduction extends DelegatingIntroductionInterceptor implements NamedBean {

		private final String beanName;

		public ExposeBeanNameIntroduction(String beanName) {
			this.beanName = beanName;
		}

		@Override
		public Object invoke(MethodInvocation mi) throws Throwable {
			if (!(mi instanceof ProxyMethodInvocation)) {
				throw new IllegalStateException("MethodInvocation is not a Spring ProxyMethodInvocation: " + mi);
			}
			ProxyMethodInvocation pmi = (ProxyMethodInvocation) mi;
			pmi.setUserAttribute(BEAN_NAME_ATTRIBUTE, this.beanName);
			return super.invoke(mi);
		}

		@Override
		public String getBeanName() {
			return this.beanName;
		}
	}

}
