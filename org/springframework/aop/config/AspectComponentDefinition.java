/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2007 the original author or authors.
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

package org.springframework.aop.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanReference;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;

/**
 * {@link org.springframework.beans.factory.parsing.ComponentDefinition}
 * that holds an aspect definition, including its nested pointcuts.
 *
 * <p>
 *  除非适用法律要求或以书面形式同意,根据许可证分发的软件以"按原样"分发,不附带任何明示或暗示的担保或条件,请参阅许可证中有关特定语言的权限和限制许可证
 * 
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 * @see #getNestedComponents()
 * @see PointcutComponentDefinition
 */
public class AspectComponentDefinition extends CompositeComponentDefinition {

	private final BeanDefinition[] beanDefinitions;

	private final BeanReference[] beanReferences;


	public AspectComponentDefinition(
			String aspectName, BeanDefinition[] beanDefinitions, BeanReference[] beanReferences, Object source) {

		super(aspectName, source);
		this.beanDefinitions = (beanDefinitions != null ? beanDefinitions : new BeanDefinition[0]);
		this.beanReferences = (beanReferences != null ? beanReferences : new BeanReference[0]);
	}


	@Override
	public BeanDefinition[] getBeanDefinitions() {
		return this.beanDefinitions;
	}

	@Override
	public BeanReference[] getBeanReferences() {
		return this.beanReferences;
	}

}
