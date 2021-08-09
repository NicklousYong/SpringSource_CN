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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates one or more {@link Configuration @Configuration} classes to import.
 *
 * <p>Provides functionality equivalent to the {@code <import/>} element in Spring XML.
 * Allows for importing {@code @Configuration} classes, {@link ImportSelector} and
 * {@link ImportBeanDefinitionRegistrar} implementations, as well as regular component
 * classes (as of 4.2; analogous to {@link AnnotationConfigApplicationContext#register}).
 *
 * <p>{@code @Bean} definitions declared in imported {@code @Configuration} classes should be
 * accessed by using {@link org.springframework.beans.factory.annotation.Autowired @Autowired}
 * injection. Either the bean itself can be autowired, or the configuration class instance
 * declaring the bean can be autowired. The latter approach allows for explicit, IDE-friendly
 * navigation between {@code @Configuration} class methods.
 *
 * <p>May be declared at the class level or as a meta-annotation.
 *
 * <p>If XML or other non-{@code @Configuration} bean definition resources need to be
 * imported, use the {@link ImportResource @ImportResource} annotation instead.
 *
 * <p>
 *  表示要导入的一个或多个{@link Configuration @Configuration}类
 * 
 * <p>提供与Spring XML中的{@code <import />}元素相当的功能允许导入{@code @Configuration}类,{@link ImportSelector}和{@link ImportBeanDefinitionRegistrar}
 * 实现以及常规组件类(从42开始;类似于{@link AnnotationConfigApplicationContext#register})。
 * 
 *  应该使用{@link orgspringframeworkbeansfactoryannotationAutowired @Autowired}注入来访问在导入的{@code @Configuration}
 * 类中声明的<p> {@ code @Bean}定义。
 * bean本身可以是自动连线的,也可以是声明bean的配置类实例自动连线后一种方法允许在{@code @Configuration}类方法之间进行显式的IDE友好导航。
 * 
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.0
 * @see Configuration
 * @see ImportSelector
 * @see ImportResource
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Import {

	/**
	 * {@link Configuration}, {@link ImportSelector}, {@link ImportBeanDefinitionRegistrar}
	 * or regular component classes to import.
	 * <p>
	 * 
	 * <p>可以在类级别或作为元注释声明
	 * 
	 *  <p>如果需要导入XML或其他非{@ code @Configuration} bean定义资源,请使用{@link ImportResource @ImportResource}注释
	 */
	Class<?>[] value();

}
