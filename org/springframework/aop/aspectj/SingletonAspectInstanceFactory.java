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

package org.springframework.aop.aspectj;

import java.io.Serializable;

import org.springframework.core.Ordered;
import org.springframework.util.Assert;

/**
 * Implementation of {@link AspectInstanceFactory} that is backed by a
 * specified singleton object, returning the same instance for every
 * {@link #getAspectInstance()} call.
 *
 * <p>
 *  执行由指定的单例对象支持的{@link AspectInstanceFactory},为每个{@link #getAspectInstance()}调用返回相同的实例
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 * @see SimpleAspectInstanceFactory
 */
@SuppressWarnings("serial")
public class SingletonAspectInstanceFactory implements AspectInstanceFactory, Serializable {

	private final Object aspectInstance;


	/**
	 * Create a new SingletonAspectInstanceFactory for the given aspect instance.
	 * <p>
	 * 为给定的方面实例创建一个新的SingletonAspectInstanceFactory
	 * 
	 * 
	 * @param aspectInstance the singleton aspect instance
	 */
	public SingletonAspectInstanceFactory(Object aspectInstance) {
		Assert.notNull(aspectInstance, "Aspect instance must not be null");
		this.aspectInstance = aspectInstance;
	}


	@Override
	public final Object getAspectInstance() {
		return this.aspectInstance;
	}

	@Override
	public ClassLoader getAspectClassLoader() {
		return this.aspectInstance.getClass().getClassLoader();
	}

	/**
	 * Determine the order for this factory's aspect instance,
	 * either an instance-specific order expressed through implementing
	 * the {@link org.springframework.core.Ordered} interface,
	 * or a fallback order.
	 * <p>
	 *  确定此工厂方面实例的顺序,无论是通过实现{@link orgspringframeworkcoreOrdered}接口表达的实例特定顺序,还是回退顺序
	 * 
	 * 
	 * @see org.springframework.core.Ordered
	 * @see #getOrderForAspectClass
	 */
	@Override
	public int getOrder() {
		if (this.aspectInstance instanceof Ordered) {
			return ((Ordered) this.aspectInstance).getOrder();
		}
		return getOrderForAspectClass(this.aspectInstance.getClass());
	}

	/**
	 * Determine a fallback order for the case that the aspect instance
	 * does not express an instance-specific order through implementing
	 * the {@link org.springframework.core.Ordered} interface.
	 * <p>The default implementation simply returns {@code Ordered.LOWEST_PRECEDENCE}.
	 * <p>
	 *  确定方面实例不通过实现{@link orgspringframeworkcoreOrdered}接口表达实例特定顺序的情况下的备用顺序。
	 * 默认实现只返回{@code OrderedLOWEST_PRECEDENCE}。
	 * 
	 * @param aspectClass the aspect class
	 */
	protected int getOrderForAspectClass(Class<?> aspectClass) {
		return Ordered.LOWEST_PRECEDENCE;
	}

}
