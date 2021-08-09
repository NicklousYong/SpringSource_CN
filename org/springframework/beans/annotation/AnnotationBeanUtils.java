/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2015 the original author or authors.
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

package org.springframework.beans.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringValueResolver;

/**
 * General utility methods for working with annotations in JavaBeans style.
 *
 * <p>
 *  使用JavaBeans样式的注释的一般实用方法
 * 
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 */
public abstract class AnnotationBeanUtils {

	/**
	 * Copy the properties of the supplied {@link Annotation} to the supplied target bean.
	 * Any properties defined in {@code excludedProperties} will not be copied.
	 * <p>
	 * 将提供的{@link注释}的属性复制到提供的目标bean在{@code excludedProperties}中定义的任何属性将不被复制
	 * 
	 * 
	 * @param ann the annotation to copy from
	 * @param bean the bean instance to copy to
	 * @param excludedProperties the names of excluded properties, if any
	 * @see org.springframework.beans.BeanWrapper
	 */
	public static void copyPropertiesToBean(Annotation ann, Object bean, String... excludedProperties) {
		copyPropertiesToBean(ann, bean, null, excludedProperties);
	}

	/**
	 * Copy the properties of the supplied {@link Annotation} to the supplied target bean.
	 * Any properties defined in {@code excludedProperties} will not be copied.
	 * <p>A specified value resolver may resolve placeholders in property values, for example.
	 * <p>
	 *  将提供的{@link注释}的属性复制到提供的目标bean不会复制{@code excludedProperties}中定义的任何属性<p>指定的值解析器可能会解析属性值中的占位符,例如
	 * 
	 * @param ann the annotation to copy from
	 * @param bean the bean instance to copy to
	 * @param valueResolver a resolve to post-process String property values (may be {@code null})
	 * @param excludedProperties the names of excluded properties, if any
	 * @see org.springframework.beans.BeanWrapper
	 */
	public static void copyPropertiesToBean(Annotation ann, Object bean, StringValueResolver valueResolver, String... excludedProperties) {
		Set<String> excluded = new HashSet<String>(Arrays.asList(excludedProperties));
		Method[] annotationProperties = ann.annotationType().getDeclaredMethods();
		BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(bean);
		for (Method annotationProperty : annotationProperties) {
			String propertyName = annotationProperty.getName();
			if (!excluded.contains(propertyName) && bw.isWritableProperty(propertyName)) {
				Object value = ReflectionUtils.invokeMethod(annotationProperty, ann);
				if (valueResolver != null && value instanceof String) {
					value = valueResolver.resolveStringValue((String) value);
				}
				bw.setPropertyValue(propertyName, value);
			}
		}
	}

}
