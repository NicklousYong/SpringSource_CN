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

package org.springframework.beans.factory.annotation;

import org.springframework.beans.factory.wiring.BeanWiringInfo;
import org.springframework.beans.factory.wiring.BeanWiringInfoResolver;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * {@link org.springframework.beans.factory.wiring.BeanWiringInfoResolver} that
 * uses the Configurable annotation to identify which classes need autowiring.
 * The bean name to look up will be taken from the {@link Configurable} annotation
 * if specified; otherwise the default will be the fully-qualified name of the
 * class being configured.
 *
 * <p>
 * {@link orgspringframeworkbeansfactorywiringBeanWiringInfoResolver}使用可配置注释来识别哪些类需要自动连线要查找的bean名称将从{@link Configurable}
 * 注释中获取;否则默认将是正在配置的类的全限定名称。
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 * @see Configurable
 * @see org.springframework.beans.factory.wiring.ClassNameBeanWiringInfoResolver
 */
public class AnnotationBeanWiringInfoResolver implements BeanWiringInfoResolver {

	@Override
	public BeanWiringInfo resolveWiringInfo(Object beanInstance) {
		Assert.notNull(beanInstance, "Bean instance must not be null");
		Configurable annotation = beanInstance.getClass().getAnnotation(Configurable.class);
		return (annotation != null ? buildWiringInfo(beanInstance, annotation) : null);
	}

	/**
	 * Build the BeanWiringInfo for the given Configurable annotation.
	 * <p>
	 *  为给定的可配置注释构建BeanWiringInfo
	 * 
	 * 
	 * @param beanInstance the bean instance
	 * @param annotation the Configurable annotation found on the bean class
	 * @return the resolved BeanWiringInfo
	 */
	protected BeanWiringInfo buildWiringInfo(Object beanInstance, Configurable annotation) {
		if (!Autowire.NO.equals(annotation.autowire())) {
			return new BeanWiringInfo(annotation.autowire().value(), annotation.dependencyCheck());
		}
		else {
			if (!"".equals(annotation.value())) {
				// explicitly specified bean name
				return new BeanWiringInfo(annotation.value(), false);
			}
			else {
				// default bean name
				return new BeanWiringInfo(getDefaultBeanName(beanInstance), true);
			}
		}
	}

	/**
	 * Determine the default bean name for the specified bean instance.
	 * <p>The default implementation returns the superclass name for a CGLIB
	 * proxy and the name of the plain bean class else.
	 * <p>
	 *  确定指定的bean实例的默认bean名称<p>默认实现返回CGLIB代理的超类名称,其他类别的bean的名称
	 * 
	 * @param beanInstance the bean instance to build a default name for
	 * @return the default bean name to use
	 * @see org.springframework.util.ClassUtils#getUserClass(Class)
	 */
	protected String getDefaultBeanName(Object beanInstance) {
		return ClassUtils.getUserClass(beanInstance).getName();
	}

}