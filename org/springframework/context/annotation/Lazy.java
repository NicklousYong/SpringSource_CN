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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates whether a bean is to be lazily initialized.
 *
 * <p>May be used on any class directly or indirectly annotated with {@link
 * org.springframework.stereotype.Component @Component} or on methods annotated with
 * {@link Bean @Bean}.
 *
 * <p>If this annotation is not present on a {@code @Component} or {@code @Bean} definition,
 * eager initialization will occur. If present and set to {@code true}, the {@code @Bean} or
 * {@code @Component} will not be initialized until referenced by another bean or explicitly
 * retrieved from the enclosing {@link org.springframework.beans.factory.BeanFactory
 * BeanFactory}. If present and set to {@code false}, the bean will be instantiated on
 * startup by bean factories that perform eager initialization of singletons.
 *
 * <p>If Lazy is present on a {@link Configuration @Configuration} class, this
 * indicates that all {@code @Bean} methods within that {@code @Configuration}
 * should be lazily initialized. If {@code @Lazy} is present and false on a {@code @Bean}
 * method within a {@code @Lazy}-annotated {@code @Configuration} class, this indicates
 * overriding the 'default lazy' behavior and that the bean should be eagerly initialized.
 *
 * <p>In addition to its role for component initialization, this annotation may also be placed
 * on injection points marked with {@link org.springframework.beans.factory.annotation.Autowired}
 * or {@link javax.inject.Inject}: In that context, it leads to the creation of a
 * lazy-resolution proxy for all affected dependencies, as an alternative to using
 * {@link org.springframework.beans.factory.ObjectFactory} or {@link javax.inject.Provider}.
 *
 * <p>
 *  指示一个bean是否被懒惰地初始化
 * 
 * <p>可以在任何使用{@link orgspringframeworkstereotypeComponent @Component}或使用{@link Bean @Bean}注释的方法上直接或间接注释的
 * 类使用。
 * 
 *  <p>如果{@code @Component}或{@code @Bean}定义中不存在此注释,则将会发生急切的初始化。
 * 如果存在并设置为{@code true},则{@code @Bean}或{ @code @Component}将不会被初始化,直到被另一个bean引用或从封闭的{@link orgspringframeworkbeansfactoryBeanFactory BeanFactory}
 * 中显式检索出来。
 *  <p>如果{@code @Component}或{@code @Bean}定义中不存在此注释,则将会发生急切的初始化。
 * 如果存在并设置为{@code false},则bean将在启动时由bean工厂实例化,单身初始化。
 * 
 * 
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.0
 * @see Primary
 * @see Bean
 * @see Configuration
 * @see org.springframework.stereotype.Component
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Lazy {

	/**
	 * Whether lazy initialization should occur.
	 * <p>
	 * <p>如果Lazy存在于{@link Configuration @Configuration}类中,则表示{@code @Configuration}中的所有{@code @Bean}方法应该被延迟初
	 * 始化如果{@code @Lazy}存在在{@code @Lazy}注释的{@code @Configuration}类中的{@code @Bean}方法中为false,这表示覆盖"默认懒惰"行为,并且该
	 * bean应该被急切地初始化。
	 * 
	 * <p>除了组件初始化的角色之外,此注释还可以放置在标有{@link orgspringframeworkbeansfactoryannotationAutowired}或{@link javaxinjectInject}
	 * 的注入点上：在这种情况下,它导致创建一个延迟解析代理对于所有受影响的依赖关系,作为使用{@link orgspringframeworkbeansfactoryObjectFactory}或{@link javaxinjectProvider}
	 */
	boolean value() default true;

}
