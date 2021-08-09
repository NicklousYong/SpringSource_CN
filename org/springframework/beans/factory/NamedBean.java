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

package org.springframework.beans.factory;

/**
 * Counterpart of {@link BeanNameAware}. Returns the bean name of an object.
 *
 * <p>This interface can be introduced to avoid a brittle dependence on
 * bean name in objects used with Spring IoC and Spring AOP.
 *
 * <p>
 * 
 * @author Rod Johnson
 * @since 2.0
 * @see BeanNameAware
 */
public interface NamedBean {

	/**
	 * Return the name of this bean in a Spring bean factory, if known.
	 * <p>
	 *  {@link BeanNameAware}的对应方返回对象的bean名称
	 * 
	 *  <p>可以引入此界面,以避免与Spring IoC和Spring AOP中使用的对象中的bean名称有脆弱的依赖关系
	 * 
	 */
	String getBeanName();

}
