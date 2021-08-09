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

package org.springframework.aop.aspectj;

import org.springframework.aop.framework.AopConfigException;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;

/**
 * Implementation of {@link AspectInstanceFactory} that creates a new instance
 * of the specified aspect class for every {@link #getAspectInstance()} call.
 *
 * <p>
 *  实施{@link AspectInstanceFactory},为每个{@link #getAspectInstance()}调用创建指定方面类的新实例
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.0.4
 */
public class SimpleAspectInstanceFactory implements AspectInstanceFactory {

	private final Class<?> aspectClass;


	/**
	 * Create a new SimpleAspectInstanceFactory for the given aspect class.
	 * <p>
	 *  为给定的方面类创建一个新的SimpleAspectInstanceFactory
	 * 
	 * 
	 * @param aspectClass the aspect class
	 */
	public SimpleAspectInstanceFactory(Class<?> aspectClass) {
		Assert.notNull(aspectClass, "Aspect class must not be null");
		this.aspectClass = aspectClass;
	}

	/**
	 * Return the specified aspect class (never {@code null}).
	 * <p>
	 * 返回指定的方面类(从不{@code null})
	 * 
	 */
	public final Class<?> getAspectClass() {
		return this.aspectClass;
	}


	@Override
	public final Object getAspectInstance() {
		try {
			return this.aspectClass.newInstance();
		}
		catch (InstantiationException ex) {
			throw new AopConfigException(
					"Unable to instantiate aspect class: " + this.aspectClass.getName(), ex);
		}
		catch (IllegalAccessException ex) {
			throw new AopConfigException(
					"Could not access aspect constructor: " + this.aspectClass.getName(), ex);
		}
	}

	@Override
	public ClassLoader getAspectClassLoader() {
		return this.aspectClass.getClassLoader();
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
		return getOrderForAspectClass(this.aspectClass);
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
