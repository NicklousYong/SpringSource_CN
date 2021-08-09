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

package org.springframework.beans.factory.wiring;

/**
 * Strategy interface to be implemented by objects than can resolve bean name
 * information, given a newly instantiated bean object. Invocations to the
 * {@link #resolveWiringInfo} method on this interface will be driven by
 * the AspectJ pointcut in the relevant concrete aspect.
 *
 * <p>Metadata resolution strategy can be pluggable. A good default is
 * {@link ClassNameBeanWiringInfoResolver}, which uses the fully-qualified
 * class name as bean name.
 *
 * <p>
 * 
 * @author Rod Johnson
 * @since 2.0
 * @see BeanWiringInfo
 * @see ClassNameBeanWiringInfoResolver
 * @see org.springframework.beans.factory.annotation.AnnotationBeanWiringInfoResolver
 */
public interface BeanWiringInfoResolver {

	/**
	 * Resolve the BeanWiringInfo for the given bean instance.
	 * <p>
	 * 由对象执行的策略接口可以解析bean名称信息,给定一个新实例化的bean对象此接口上的{@link #resolveWiringInfo}方法的调用将由相关具体方面的AspectJ切入点
	 * 
	 *  元数据解析策略可以插拔一个很好的默认值是{@link ClassNameBeanWiringInfoResolver},它使用完全限定的类名作为bean名称
	 * 
	 * 
	 * @param beanInstance the bean instance to resolve info for
	 * @return the BeanWiringInfo, or {@code null} if not found
	 */
	BeanWiringInfo resolveWiringInfo(Object beanInstance);

}
