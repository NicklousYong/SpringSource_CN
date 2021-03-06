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

package org.springframework.beans.factory;

/**
 * Exception thrown when a bean instance has been requested for
 * a bean definition which has been marked as abstract.
 *
 * <p>
 * 
 * 
 * @author Juergen Hoeller
 * @since 1.1
 * @see org.springframework.beans.factory.support.AbstractBeanDefinition#setAbstract
 */
@SuppressWarnings("serial")
public class BeanIsAbstractException extends BeanCreationException {

	/**
	 * Create a new BeanIsAbstractException.
	 * <p>
	 *  当bean实例被请求一个已被标记为抽象的bean定义时抛出异常
	 * 
	 * 
	 * @param beanName the name of the bean requested
	 */
	public BeanIsAbstractException(String beanName) {
		super(beanName, "Bean definition is abstract");
	}

}
