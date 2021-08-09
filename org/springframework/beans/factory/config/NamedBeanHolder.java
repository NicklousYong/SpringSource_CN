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

package org.springframework.beans.factory.config;

import org.springframework.beans.factory.NamedBean;
import org.springframework.util.Assert;

/**
 * A simple holder for a given bean name plus bean instance.
 *
 * <p>
 *  一个给定bean名称和bean实例的简单持有者
 * 
 * 
 * @author Juergen Hoeller
 * @since 4.3.3
 * @see AutowireCapableBeanFactory#resolveNamedBean(Class)
 */
public class NamedBeanHolder<T> implements NamedBean {

	private final String beanName;

	private final T beanInstance;


	/**
	 * Create a new holder for the given bean name plus instance.
	 * <p>
	 *  为给定的bean名称加实例创建一个新的持有者
	 * 
	 * 
	 * @param beanName the name of the bean
	 * @param beanInstance the corresponding bean instance
	 */
	public NamedBeanHolder(String beanName, T beanInstance) {
		Assert.notNull(beanName, "Bean name must not be null");
		this.beanName = beanName;
		this.beanInstance = beanInstance;
	}


	/**
	 * Return the name of the bean (never {@code null}).
	 * <p>
	 *  返回bean的名称(从不{@code null})
	 * 
	 */
	@Override
	public String getBeanName() {
		return this.beanName;
	}

	/**
	 * Return the corresponding bean instance (can be {@code null}).
	 * <p>
	 * 返回相应的bean实例(可以是{@code null})
	 */
	public T getBeanInstance() {
		return this.beanInstance;
	}

}
