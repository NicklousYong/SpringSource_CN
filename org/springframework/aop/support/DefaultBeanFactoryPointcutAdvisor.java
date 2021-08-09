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

import org.springframework.aop.Pointcut;

/**
 * Concrete BeanFactory-based PointcutAdvisor that allows for any Advice
 * to be configured as reference to an Advice bean in the BeanFactory,
 * as well as the Pointcut to be configured through a bean property.
 *
 * <p>Specifying the name of an advice bean instead of the advice object itself
 * (if running within a BeanFactory) increases loose coupling at initialization time,
 * in order to not initialize the advice object until the pointcut actually matches.
 *
 * <p>
 * 
 * @author Juergen Hoeller
 * @since 2.0.2
 * @see #setPointcut
 * @see #setAdviceBeanName
 */
@SuppressWarnings("serial")
public class DefaultBeanFactoryPointcutAdvisor extends AbstractBeanFactoryPointcutAdvisor {

	private Pointcut pointcut = Pointcut.TRUE;


	/**
	 * Specify the pointcut targeting the advice.
	 * <p>Default is {@code Pointcut.TRUE}.
	 * <p>
	 *  具体的基于BeanFactory的PointcutAdvisor,允许将任何建议配置为BeanFactory中的Advice bean的引用,以及通过bean属性进行配置的Pointcut
	 * 
	 * <p>指定建议bean的名称而不是建议对象本身(如果在BeanFactory中运行)会在初始化时增加松耦合,以便在切入点实际匹配之前不会初始化该建议对象
	 * 
	 * 
	 * @see #setAdviceBeanName
	 */
	public void setPointcut(Pointcut pointcut) {
		this.pointcut = (pointcut != null ? pointcut : Pointcut.TRUE);
	}

	@Override
	public Pointcut getPointcut() {
		return this.pointcut;
	}


	@Override
	public String toString() {
		return getClass().getName() + ": pointcut [" + getPointcut() + "]; advice bean '" + getAdviceBeanName() + "'";
	}

}
