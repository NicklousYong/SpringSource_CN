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

package org.springframework.beans.factory.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a constructor, field, setter method or config method as to be
 * autowired by Spring's dependency injection facilities.
 *
 * <p>Only one constructor (at max) of any given bean class may carry this
 * annotation, indicating the constructor to autowire when used as a Spring
 * bean. Such a constructor does not have to be public.
 *
 * <p>Fields are injected right after construction of a bean, before any
 * config methods are invoked. Such a config field does not have to be public.
 *
 * <p>Config methods may have an arbitrary name and any number of arguments;
 * each of those arguments will be autowired with a matching bean in the
 * Spring container. Bean property setter methods are effectively just
 * a special case of such a general config method. Such config methods
 * do not have to be public.
 *
 * <p>In the case of multiple argument methods, the 'required' parameter is
 * applicable for all arguments.
 *
 * <p>In case of a {@link java.util.Collection} or {@link java.util.Map}
 * dependency type, the container will autowire all beans matching the
 * declared value type. In case of a Map, the keys must be declared as
 * type String and will be resolved to the corresponding bean names.
 *
 * <p>Note that actual injection is performed through a
 * {@link org.springframework.beans.factory.config.BeanPostProcessor
 * BeanPostProcessor} which in turn means that you <em>cannot</em>
 * use {@code @Autowired} to inject references into
 * {@link org.springframework.beans.factory.config.BeanPostProcessor
 * BeanPostProcessor} or
 * {@link org.springframework.beans.factory.config.BeanFactoryPostProcessor BeanFactoryPostProcessor}
 * types. Please consult the javadoc for the {@link AutowiredAnnotationBeanPostProcessor}
 * class (which, by default, checks for the presence of this annotation).
 *
 * <p>
 *  标记一个构造函数,字段,setter方法或配置方法,以便由Spring的依赖注入设备自动连接
 * 
 * <p>任何给定bean类的只有一个构造函数(at max)可能会携带此注释,指示用作Spring bean时自动连接的构造函数。这样的构造函数不必是public
 * 
 *  在构造bean之后,在调用任何配置方法之前,字段将被注入。这样的配置字段不必是公开的
 * 
 *  配置方法可能具有任意名称和任意数量的参数;每个这些参数将在Spring容器中与一个匹配的bean自动连接Bean属性setter方法实际上只是一个这样的一般配置方法的一种特殊情况这样的配置方法不必被公
 * 开。
 * 
 *  <p>在多个参数方法的情况下,'required'参数适用于所有参数
 * 
 * 
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @since 2.5
 * @see AutowiredAnnotationBeanPostProcessor
 * @see Qualifier
 * @see Value
 */
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {

	/**
	 * Declares whether the annotated dependency is required.
	 * <p>Defaults to {@code true}.
	 * <p>
	 * <p>在{@link javautilCollection}或{@link javautilMap}依赖关系类型的情况下,容器将自动连接与声明的值类型匹配的所有Bean。
	 * 在Map的情况下,该键必须声明为String类型并将被解析到相应的bean名称。
	 * 
	 * 请注意,实际注入是通过{@link orgspringframeworkbeansfactoryconfigBeanPostProcessor BeanPostProcessor}执行的,这反过来意味着
	 * 您不能使用{@code @Autowired}将引用注入{@link orgspringframeworkbeansfactoryconfigBeanPostProcessor BeanPostProcessor}
	 * 或{ @link orgspringframeworkbeansfactoryconfigBeanFactoryPostProcessor BeanFactoryPostProcessor}类型请参阅{@link AutowiredAnnotationBeanPostProcessor}
	 */
	boolean required() default true;

}
