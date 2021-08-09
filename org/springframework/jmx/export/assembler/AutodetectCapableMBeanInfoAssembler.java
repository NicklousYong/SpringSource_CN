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

package org.springframework.jmx.export.assembler;

/**
 * Extends the {@code MBeanInfoAssembler} to add autodetection logic.
 * Implementations of this interface are given the opportunity by the
 * {@code MBeanExporter} to include additional beans in the registration process.
 *
 * <p>The exact mechanism for deciding which beans to include is left to
 * implementing classes.
 *
 * <p>
 * 
 * @author Rob Harrop
 * @since 1.2
 * @see org.springframework.jmx.export.MBeanExporter
 */
public interface AutodetectCapableMBeanInfoAssembler extends MBeanInfoAssembler {

	/**
	 * Indicate whether a particular bean should be included in the registration
	 * process, if it is not specified in the {@code beans} map of the
	 * {@code MBeanExporter}.
	 * <p>
	 *  扩展{@code MBeanInfoAssembler}以添加自动检测逻辑此接口的实现由{@code MBeanExporter}提供机会,以在注册过程中包含其他bean
	 * 
	 * <p>确定要包含哪些bean的确切机制留给实现类
	 * 
	 * 
	 * @param beanClass the class of the bean (might be a proxy class)
	 * @param beanName the name of the bean in the bean factory
	 */
	boolean includeBean(Class<?> beanClass, String beanName);

}
