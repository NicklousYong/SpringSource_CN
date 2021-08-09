/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2013 the original author or authors.
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

package org.springframework.context.annotation;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * A single {@code condition} that must be {@linkplain #matches matched} in order
 * for a component to be registered.
 *
 * <p>Conditions are checked immediately before the bean-definition is due to be
 * registered and are free to veto registration based on any criteria that can
 * be determined at that point.
 *
 * <p>Conditions must follow the same restrictions as {@link BeanFactoryPostProcessor}
 * and take care to never interact with bean instances. For more fine-grained control
 * of conditions that interact with {@code @Configuration} beans consider the
 * {@link ConfigurationCondition} interface.
 *
 * <p>
 *  单个{@code条件}必须{@linkplain #matches matched}才能注册组件
 * 
 * <p>在bean定义被注册之前立即检查条件,并且可以根据在该点可以确定的任何标准自由否决注册
 * 
 *  条件必须遵循与{@link BeanFactoryPostProcessor}相同的限制,并注意永远不要与bean实例交互。
 * 
 * @author Phillip Webb
 * @since 4.0
 * @see ConfigurationCondition
 * @see Conditional
 * @see ConditionContext
 */
public interface Condition {

	/**
	 * Determine if the condition matches.
	 * <p>
	 * 对于与{@code @Configuration} bean交互的条件进行更细粒度的控制,请考虑{@link ConfigurationCondition}接口。
	 * 
	 * 
	 * @param context the condition context
	 * @param metadata metadata of the {@link org.springframework.core.type.AnnotationMetadata class}
	 * or {@link org.springframework.core.type.MethodMetadata method} being checked.
	 * @return {@code true} if the condition matches and the component can be registered
	 * or {@code false} to veto registration.
	 */
	boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata);

}
