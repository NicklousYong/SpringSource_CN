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

package org.springframework.aop.target.dynamic;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.Assert;

/**
 * Refreshable TargetSource that fetches fresh target beans from a BeanFactory.
 *
 * <p>Can be subclassed to override {@code requiresRefresh()} to suppress
 * unnecessary refreshes. By default, a refresh will be performed every time
 * the "refreshCheckDelay" has elapsed.
 *
 * <p>
 *  可刷新的TargetSource,它从BeanFactory中获取新的目标bean
 * 
 * <p>可以将子类覆盖{@code requiresRefresh()}以抑制不必要的刷新默认情况下,每当"refreshCheckDelay"已经过去时,将执行刷新
 * 
 * 
 * @author Rob Harrop
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @since 2.0
 * @see org.springframework.beans.factory.BeanFactory
 * @see #requiresRefresh()
 * @see #setRefreshCheckDelay
 */
public class BeanFactoryRefreshableTargetSource extends AbstractRefreshableTargetSource {

	private final BeanFactory beanFactory;

	private final String beanName;


	/**
	 * Create a new BeanFactoryRefreshableTargetSource for the given
	 * bean factory and bean name.
	 * <p>Note that the passed-in BeanFactory should have an appropriate
	 * bean definition set up for the given bean name.
	 * <p>
	 *  为给定的bean工厂和bean名称创建一个新的BeanFactoryRefreshableTargetSource <p>注意,传入的BeanFactory应该为给定的bean名称设置一个适当的bea
	 * n定义。
	 * 
	 * 
	 * @param beanFactory the BeanFactory to fetch beans from
	 * @param beanName the name of the target bean
	 */
	public BeanFactoryRefreshableTargetSource(BeanFactory beanFactory, String beanName) {
		Assert.notNull(beanFactory, "BeanFactory is required");
		Assert.notNull(beanName, "Bean name is required");
		this.beanFactory = beanFactory;
		this.beanName = beanName;
	}


	/**
	 * Retrieve a fresh target object.
	 * <p>
	 *  检索一个新的目标对象
	 * 
	 */
	@Override
	protected final Object freshTarget() {
		return this.obtainFreshBean(this.beanFactory, this.beanName);
	}

	/**
	 * A template method that subclasses may override to provide a
	 * fresh target object for the given bean factory and bean name.
	 * <p>This default implementation fetches a new target bean
	 * instance from the bean factory.
	 * <p>
	 *  子类可以覆盖的模板方法来为给定的bean工厂和bean名称提供一个新的目标对象。<p>此默认实现从bean工厂获取一个新的目标bean实例
	 * 
	 * @see org.springframework.beans.factory.BeanFactory#getBean
	 */
	protected Object obtainFreshBean(BeanFactory beanFactory, String beanName) {
		return beanFactory.getBean(beanName);
	}

}
