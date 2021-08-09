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

package org.springframework.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.Ordered;

/**
 * {@code @Order} defines the sort order for an annotated component.
 *
 * <p>The {@link #value} is optional and represents an order value as defined
 * in the {@link Ordered} interface. Lower values have higher priority. The
 * default value is {@code Ordered.LOWEST_PRECEDENCE}, indicating
 * lowest priority (losing to any other specified order value).
 *
 * <p>Since Spring 4.1, the standard {@link javax.annotation.Priority}
 * annotation can be used as a drop-in replacement for this annotation.
 *
 * <p><b>NOTE</b>: Annotation-based ordering is supported for specific kinds
 * of components only &mdash; for example, for annotation-based AspectJ
 * aspects. Ordering strategies within the Spring container, on the other
 * hand, are typically based on the {@link Ordered} interface in order to
 * allow for programmatically configurable ordering of each <i>instance</i>.
 *
 * <p>Consult the Javadoc for {@link org.springframework.core.OrderComparator
 * OrderComparator} for details on the sort semantics for non-ordered objects.
 *
 * <p>
 *  {@code @Order}定义了注释组件的排序顺序
 * 
 * <p> {@link #value}是可选的,表示{@link Ordered}界面中定义的订单值低价值具有较高优先级默认值为{@code OrderedLOWEST_PRECEDENCE},表示最低优
 * 先级(丢失任何其他指定订单值)。
 * 
 *  <p>自Spring 41以来,标准的{@link javaxannotationPriority}注释可以用作此注释的替代替换
 * 
 *  <p> <b>注意</b>：仅针对特定类型的组件支持基于注释的排序&mdash;例如,对于基于注释的AspectJ方面,另一方面,Spring容器中的排序策略通常基于{@link Ordered}接口
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 * @see org.springframework.core.Ordered
 * @see AnnotationAwareOrderComparator
 * @see OrderUtils
 * @see javax.annotation.Priority
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Documented
public @interface Order {

	/**
	 * The order value.
	 * <p>Default is {@link Ordered#LOWEST_PRECEDENCE}.
	 * <p>
	 * ,以便允许每个<i>实例</i>的编程配置排序。
	 * 
	 * <p>有关非有序对象的排序语义的详细信息,请参阅{@link orgspringframeworkcoreOrderComparator OrderComparator}的Javadoc
	 * 
	 * 
	 * @see Ordered#getOrder()
	 */
	int value() default Ordered.LOWEST_PRECEDENCE;

}
