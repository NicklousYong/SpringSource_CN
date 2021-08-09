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

package org.springframework.aop.support;

import java.io.IOException;
import java.io.ObjectInputStream;

import org.aopalliance.aop.Advice;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.util.Assert;

/**
 * Abstract BeanFactory-based PointcutAdvisor that allows for any Advice
 * to be configured as reference to an Advice bean in a BeanFactory.
 *
 * <p>Specifying the name of an advice bean instead of the advice object itself
 * (if running within a BeanFactory) increases loose coupling at initialization time,
 * in order to not initialize the advice object until the pointcut actually matches.
 *
 * <p>
 *  抽象的基于BeanFactory的PointcutAdvisor,允许任何建议被配置为对BeanFactory中的一个Advice bean的引用
 * 
 * <p>指定建议bean的名称而不是建议对象本身(如果在BeanFactory中运行)会在初始化时增加松耦合,以便在切入点实际匹配之前不会初始化该建议对象
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.0.2
 * @see #setAdviceBeanName
 * @see DefaultBeanFactoryPointcutAdvisor
 */
@SuppressWarnings("serial")
public abstract class AbstractBeanFactoryPointcutAdvisor extends AbstractPointcutAdvisor implements BeanFactoryAware {

	private String adviceBeanName;

	private BeanFactory beanFactory;

	private transient Advice advice;

	private transient volatile Object adviceMonitor = new Object();


	/**
	 * Specify the name of the advice bean that this advisor should refer to.
	 * <p>An instance of the specified bean will be obtained on first access
	 * of this advisor's advice. This advisor will only ever obtain at most one
	 * single instance of the advice bean, caching the instance for the lifetime
	 * of the advisor.
	 * <p>
	 *  指定该顾问程序应该参考的建议bean的名称<p>指定的bean的实例将在首次访问此顾问的建议时获得。该顾问只能获取至多一个单个实例的建议bean,缓存顾问的一生的实例
	 * 
	 * 
	 * @see #getAdvice()
	 */
	public void setAdviceBeanName(String adviceBeanName) {
		this.adviceBeanName = adviceBeanName;
	}

	/**
	 * Return the name of the advice bean that this advisor refers to, if any.
	 * <p>
	 *  返回此顾问引用的建议bean的名称(如果有)
	 * 
	 */
	public String getAdviceBeanName() {
		return this.adviceBeanName;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
		resetAdviceMonitor();
	}

	private void resetAdviceMonitor() {
		if (this.beanFactory instanceof ConfigurableBeanFactory) {
			this.adviceMonitor = ((ConfigurableBeanFactory) this.beanFactory).getSingletonMutex();
		}
		else {
			this.adviceMonitor = new Object();
		}
	}

	/**
	 * Specify a particular instance of the target advice directly,
	 * avoiding lazy resolution in {@link #getAdvice()}.
	 * <p>
	 *  直接指定目标通知的特定实例,避免在{@link #getAdvice()}中进行懒惰解析
	 * 
	 * @since 3.1
	 */
	public void setAdvice(Advice advice) {
		synchronized (this.adviceMonitor) {
			this.advice = advice;
		}
	}

	@Override
	public Advice getAdvice() {
		synchronized (this.adviceMonitor) {
			if (this.advice == null && this.adviceBeanName != null) {
				Assert.state(this.beanFactory != null, "BeanFactory must be set to resolve 'adviceBeanName'");
				this.advice = this.beanFactory.getBean(this.adviceBeanName, Advice.class);
			}
			return this.advice;
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(getClass().getName());
		sb.append(": advice ");
		if (this.adviceBeanName != null) {
			sb.append("bean '").append(this.adviceBeanName).append("'");
		}
		else {
			sb.append(this.advice);
		}
		return sb.toString();
	}


	//---------------------------------------------------------------------
	// Serialization support
	//---------------------------------------------------------------------

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		// Rely on default serialization, just initialize state after deserialization.
		ois.defaultReadObject();

		// Initialize transient fields.
		resetAdviceMonitor();
	}

}
