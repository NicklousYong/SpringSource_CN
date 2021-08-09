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

package org.springframework.aop.framework.autoproxy;

import org.springframework.aop.TargetSource;

/**
 * Implementations can create special target sources, such as pooling target
 * sources, for particular beans. For example, they may base their choice
 * on attributes, such as a pooling attribute, on the target class.
 *
 * <p>AbstractAutoProxyCreator can support a number of TargetSourceCreators,
 * which will be applied in order.
 *
 * <p>
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public interface TargetSourceCreator {

	/**
	 * Create a special TargetSource for the given bean, if any.
	 * <p>
	 *  实现可以为特定的bean创建特殊的目标源,例如汇集目标源例如,它们可以将它们的选择作为目标类上的属性(如pooling属性)
	 * 
	 * <p> AbstractAutoProxyCreator可以支持一些TargetSourceCreators,它将按顺序应用
	 * 
	 * 
	 * @param beanClass the class of the bean to create a TargetSource for
	 * @param beanName the name of the bean
	 * @return a special TargetSource or {@code null} if this TargetSourceCreator isn't
	 * interested in the particular bean
	 */
	TargetSource getTargetSource(Class<?> beanClass, String beanName);

}
