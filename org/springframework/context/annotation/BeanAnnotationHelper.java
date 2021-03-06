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

package org.springframework.context.annotation;

import java.lang.reflect.Method;

import org.springframework.core.annotation.AnnotatedElementUtils;

/**
 * Utilities for processing {@link Bean}-annotated methods.
 *
 * <p>
 *  除非适用法律要求或以书面形式同意,根据许可证分发的软件以"按原样"分发,不附带任何明示或暗示的担保或条件,请参阅许可证中有关特定语言的权限和限制许可证
 * 
 * 
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 */
class BeanAnnotationHelper {

	public static boolean isBeanAnnotated(Method method) {
		return AnnotatedElementUtils.hasAnnotation(method, Bean.class);
	}

	public static String determineBeanNameFor(Method beanMethod) {
		// By default, the bean name is the name of the @Bean-annotated method
		String beanName = beanMethod.getName();

		// Check to see if the user has explicitly set a custom bean name...
		Bean bean = AnnotatedElementUtils.findMergedAnnotation(beanMethod, Bean.class);
		if (bean != null && bean.name().length > 0) {
			beanName = bean.name()[0];
		}

		return beanName;
	}

}
