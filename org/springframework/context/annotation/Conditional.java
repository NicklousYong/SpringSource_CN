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

package org.springframework.context.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a component is only eligible for registration when all
 * {@linkplain #value specified conditions} match.
 *
 * <p>A <em>condition</em> is any state that can be determined programmatically
 * before the bean definition is due to be registered (see {@link Condition} for details).
 *
 * <p>The {@code @Conditional} annotation may be used in any of the following ways:
 * <ul>
 * <li>as a type-level annotation on any class directly or indirectly annotated with
 * {@code @Component}, including {@link Configuration @Configuration} classes</li>
 * <li>as a meta-annotation, for the purpose of composing custom stereotype
 * annotations</li>
 * <li>as a method-level annotation on any {@link Bean @Bean} method</li>
 * </ul>
 *
 * <p>If a {@code @Configuration} class is marked with {@code @Conditional},
 * all of the {@code @Bean} methods, {@link Import @Import} annotations, and
 * {@link ComponentScan @ComponentScan} annotations associated with that
 * class will be subject to the conditions.
 *
 * <p><strong>NOTE</strong>: Inheritance of {@code @Conditional} annotations
 * is not supported; any conditions from superclasses or from overridden
 * methods will not be considered. In order to enforce these semantics,
 * {@code @Conditional} itself is not declared as
 * {@link java.lang.annotation.Inherited @Inherited}; furthermore, any
 * custom <em>composed annotation</em> that is meta-annotated with
 * {@code @Conditional} must not be declared as {@code @Inherited}.
 *
 * <p>
 *  表示当所有{@linkplain #value指定条件}匹配时,组件仅符合注册条件
 * 
 * <p>条件</em>是可以在bean定义被注册之前以编程方式确定的任何状态(详见{@link条件})
 * 
 *  <p> {@code @Conditional}注释可以以下列任何方式使用：
 * <ul>
 *  <li>作为使用{@code @Component}直接或间接注释的任何类的类型级注释,包括{@link Configuration @Configuration}类</li> <li>作为元注释,用
 * 于在任何{@link Bean @Bean}方法中,将自定义原型注释</li> <li>作为方法级注解; </li>。
 * </ul>
 * 
 * 
 * @author Phillip Webb
 * @author Sam Brannen
 * @since 4.0
 * @see Condition
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Conditional {

	/**
	 * All {@link Condition}s that must {@linkplain Condition#matches match}
	 * in order for the component to be registered.
	 * <p>
	 * <p>如果一个{@code @Configuration}类标有{@code @Conditional},所有{@code @Bean}方法,{@link Import @Import}注释和{@link ComponentScan @ComponentScan}
	 * 与该类关联的注释将受到条件的约束。
	 * 
	 *  <p> <strong>注意</strong>：不支持{@code @Conditional}注释的继承;不会考虑超类或覆盖方法的任何条件为了强制执行这些语义,{@code @Conditional}
	 * 本身不被声明为{@link javalangannotationInherited @Inherited};此外,任何使用{@code @Conditional}进行元注释的自定义<em>组合注释</em>
	 */
	Class<? extends Condition>[] value();

}
