/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2013 the original author or authors.
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

package org.springframework.messaging.core;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.messaging.MessageChannel;
import org.springframework.util.Assert;

/**
 * An implementation of {@link DestinationResolver} that interprets a destination
 * name as the bean name of a {@link MessageChannel} and looks up the bean in
 * the configured {@link BeanFactory}.
 *
 * <p>
 *  将目标名称解释为{@link MessageChannel}的bean名称的{@link DestinationResolver}的实现,并在配置的{@link BeanFactory}中查找该bea
 * n,。
 * 
 * 
 * @author Mark Fisher
 * @since 4.0
 */
public class BeanFactoryMessageChannelDestinationResolver
		implements DestinationResolver<MessageChannel>, BeanFactoryAware {

	private BeanFactory beanFactory;


	/**
	 * A default constructor that can be used when the resolver itself is configured
	 * as a Spring bean and will have the {@code BeanFactory} injected as a result
	 * of ing having implemented {@link BeanFactoryAware}.
	 * <p>
	 * 当解析器本身被配置为Spring bean并且由于已经实现了{@link BeanFactoryAware}而被注入{@code BeanFactory}时,可以使用的默认构造函数
	 * 
	 */
	public BeanFactoryMessageChannelDestinationResolver() {
	}

	/**
	 * A constructor that accepts a {@link BeanFactory} useful if instantiating this
	 * resolver manually rather than having it defined as a Spring-managed bean.
	 * <p>
	 *  接受{@link BeanFactory}的构造函数,如果手动实例化此解析器,而不是将其定义为Spring管理的bean
	 * 
	 * @param beanFactory the bean factory to perform lookups against
	 */
	public BeanFactoryMessageChannelDestinationResolver(BeanFactory beanFactory) {
		Assert.notNull(beanFactory, "beanFactory must not be null");
		this.beanFactory = beanFactory;
	}


	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}


	@Override
	public MessageChannel resolveDestination(String name) {
		Assert.state(this.beanFactory != null, "No BeanFactory configured");
		try {
			return this.beanFactory.getBean(name, MessageChannel.class);
		}
		catch (BeansException ex) {
			throw new DestinationResolutionException(
					"Failed to find MessageChannel bean with name '" + name + "'", ex);
		}
	}

}
