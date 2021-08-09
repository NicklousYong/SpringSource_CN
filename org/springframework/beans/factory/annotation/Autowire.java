/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2009 the original author or authors.
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

package org.springframework.beans.factory.annotation;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

/**
 * Enumeration determining autowiring status: that is, whether a bean should
 * have its dependencies automatically injected by the Spring container using
 * setter injection. This is a core concept in Spring DI.
 *
 * <p>Available for use in annotation-based configurations, such as for the
 * AspectJ AnnotationBeanConfigurer aspect.
 *
 * <p>
 *  枚举确定自动接线状态：即,一个bean是否应该使用setter注入使用Spring容器自动注入它的依赖项这是Spring DI中的一个核心概念
 * 
 * <p>可用于基于注释的配置,例如对于AspectJ AnnotationBeanConfigurer方面
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 * @see org.springframework.beans.factory.annotation.Configurable
 * @see org.springframework.beans.factory.config.AutowireCapableBeanFactory
 */
public enum Autowire {

	/**
	 * Constant that indicates no autowiring at all.
	 * <p>
	 *  常数,表示没有自动连线
	 * 
	 */
	NO(AutowireCapableBeanFactory.AUTOWIRE_NO),

	/**
	 * Constant that indicates autowiring bean properties by name.
	 * <p>
	 *  常数表示按名称自动连线bean属性
	 * 
	 */
	BY_NAME(AutowireCapableBeanFactory.AUTOWIRE_BY_NAME),

	/**
	 * Constant that indicates autowiring bean properties by type.
	 * <p>
	 *  指示类型自动连接bean属性的常数
	 * 
	 */
	BY_TYPE(AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE);


	private final int value;


	Autowire(int value) {
		this.value = value;
	}

	public int value() {
		return this.value;
	}

	/**
	 * Return whether this represents an actual autowiring value.
	 * <p>
	 *  返回是否表示实际的自动连线值
	 * 
	 * @return whether actual autowiring was specified
	 * (either BY_NAME or BY_TYPE)
	 */
	public boolean isAutowire() {
		return (this == BY_NAME || this == BY_TYPE);
	}

}
