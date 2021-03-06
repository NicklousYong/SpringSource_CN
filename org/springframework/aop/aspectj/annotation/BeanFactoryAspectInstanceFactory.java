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

package org.springframework.aop.aspectj.annotation;

import java.io.Serializable;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.OrderUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * {@link org.springframework.aop.aspectj.AspectInstanceFactory} implementation
 * backed by a Spring {@link org.springframework.beans.factory.BeanFactory}.
 *
 * <p>Note that this may instantiate multiple times if using a prototype,
 * which probably won't give the semantics you expect.
 * Use a {@link LazySingletonAspectInstanceFactoryDecorator}
 * to wrap this to ensure only one new aspect comes back.
 *
 * <p>
 *  {@link orgspringframeworkaopaspectjAspectInstanceFactory}由Spring支持的实现{@link orgspringframeworkbeansfactoryBeanFactory}
 * 。
 * 
 * <p>请注意,如果使用原型,这可能会多次实例化,这可能不会给出您期望的语义使用{@link LazySingletonAspectInstanceFactoryDe​​corator}来包装,以确保只有
 * 一个新的方面回来。
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 * @see org.springframework.beans.factory.BeanFactory
 * @see LazySingletonAspectInstanceFactoryDecorator
 */
@SuppressWarnings("serial")
public class BeanFactoryAspectInstanceFactory implements MetadataAwareAspectInstanceFactory, Serializable {

	private final BeanFactory beanFactory;

	private final String name;

	private final AspectMetadata aspectMetadata;


	/**
	 * Create a BeanFactoryAspectInstanceFactory. AspectJ will be called to
	 * introspect to create AJType metadata using the type returned for the
	 * given bean name from the BeanFactory.
	 * <p>
	 *  创建一个BeanFactoryAspectInstanceFactory将调用AspectJ来内省使用从BeanFactory返回的给定bean名称的类型来创建AJType元数据
	 * 
	 * 
	 * @param beanFactory BeanFactory to obtain instance(s) from
	 * @param name name of the bean
	 */
	public BeanFactoryAspectInstanceFactory(BeanFactory beanFactory, String name) {
		this(beanFactory, name, beanFactory.getType(name));
	}

	/**
	 * Create a BeanFactoryAspectInstanceFactory, providing a type that AspectJ should
	 * introspect to create AJType metadata. Use if the BeanFactory may consider the type
	 * to be a subclass (as when using CGLIB), and the information should relate to a superclass.
	 * <p>
	 *  创建一个BeanFactoryAspectInstanceFactory,提供一个AspectJ应该内省以创建AJType元数据的类型。
	 * 如果BeanFactory可以将该类型视为一个子类(如使用CGLIB时),则该信息应该与超类相关。
	 * 
	 * 
	 * @param beanFactory BeanFactory to obtain instance(s) from
	 * @param name the name of the bean
	 * @param type the type that should be introspected by AspectJ
	 */
	public BeanFactoryAspectInstanceFactory(BeanFactory beanFactory, String name, Class<?> type) {
		Assert.notNull(beanFactory, "BeanFactory must not be null");
		Assert.notNull(name, "Bean name must not be null");
		this.beanFactory = beanFactory;
		this.name = name;
		this.aspectMetadata = new AspectMetadata(type, name);
	}


	@Override
	public Object getAspectInstance() {
		return this.beanFactory.getBean(this.name);
	}

	@Override
	public ClassLoader getAspectClassLoader() {
		return (this.beanFactory instanceof ConfigurableBeanFactory ?
				((ConfigurableBeanFactory) this.beanFactory).getBeanClassLoader() :
				ClassUtils.getDefaultClassLoader());
	}

	@Override
	public AspectMetadata getAspectMetadata() {
		return this.aspectMetadata;
	}

	@Override
	public Object getAspectCreationMutex() {
		return (this.beanFactory instanceof ConfigurableBeanFactory ?
				((ConfigurableBeanFactory) this.beanFactory).getSingletonMutex() : this);
	}

	/**
	 * Determine the order for this factory's target aspect, either
	 * an instance-specific order expressed through implementing the
	 * {@link org.springframework.core.Ordered} interface (only
	 * checked for singleton beans), or an order expressed through the
	 * {@link org.springframework.core.annotation.Order} annotation
	 * at the class level.
	 * <p>
	 * 确定此工厂目标方面的顺序,无论是通过实施{@link orgspringframeworkcoreOrdered}接口(仅检查单例bean)表达的实例特定顺序,还是通过类级别上的{@link orgspringframeworkcoreannotationOrder}
	 * 注释表示的顺序。
	 * 
	 * @see org.springframework.core.Ordered
	 * @see org.springframework.core.annotation.Order
	 */
	@Override
	public int getOrder() {
		Class<?> type = this.beanFactory.getType(this.name);
		if (type != null) {
			if (Ordered.class.isAssignableFrom(type) && this.beanFactory.isSingleton(this.name)) {
				return ((Ordered) this.beanFactory.getBean(this.name)).getOrder();
			}
			return OrderUtils.getOrder(type, Ordered.LOWEST_PRECEDENCE);
		}
		return Ordered.LOWEST_PRECEDENCE;
	}


	@Override
	public String toString() {
		return getClass().getSimpleName() + ": bean name '" + this.name + "'";
	}

}
