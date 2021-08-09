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

package org.springframework.beans.factory.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that indicates 'lookup' methods, to be overridden by the container
 * to redirect them back to the {@link org.springframework.beans.factory.BeanFactory}
 * for a {@code getBean} call. This is essentially an annotation-based version of the
 * XML {@code lookup-method} attribute, resulting in the same runtime arrangement.
 *
 * <p>The resolution of the target bean can either be based on the return type
 * ({@code getBean(Class)}) or on a suggested bean name ({@code getBean(String)}),
 * in both cases passing the method's arguments to the {@code getBean} call
 * for applying them as target factory method arguments or constructor arguments.
 *
 * <p>Such lookup methods can have default (stub) implementations that will simply
 * get replaced by the container, or they can be declared as abstract - for the
 * container to fill them in at runtime. In both cases, the container will generate
 * runtime subclasses of the method's containing class via CGLIB, which is why such
 * lookup methods can only work on beans that the container instantiates through
 * regular constructors: i.e. lookup methods cannot get replaced on beans returned
 * from factory methods where we cannot dynamically provide a subclass for them.
 *
 * <p><b>Concrete limitations in typical Spring configuration scenarios:</b>
 * When used with component scanning or any other mechanism that filters out abstract
 * beans, provide stub implementations of your lookup methods to be able to declare
 * them as concrete classes. And please remember that lookup methods won't work on
 * beans returned from {@code @Bean} methods in configuration classes; you'll have
 * to resort to {@code @Inject Provider&lt;TargetBean&gt;} or the like instead.
 *
 * <p>
 * 一个指示"查找"方法的注释,被容器覆盖以重定向到{@code getBean}调用的{@link orgspringframeworkbeansfactoryBeanFactory}这本质上是一个基于注
 * 释的XML {@code lookup-方法}属性,导致相同的运行时安排。
 * 
 *  <p>目标bean的分辨率可以基于返回类型({@code getBean(Class)})或建议的bean名称({@code getBean(String)}),在这两种情况下,传递方法的{@code getBean}
 * 调用的参数,用于将其应用为目标工厂方法参数或构造函数参数。
 * 
 * <p>这样的查找方法可以具有简单地被容器替换的默认(存根)实现,或者它们可以被声明为抽象的 - 容器在运行时填充它们在这两种情况下,容器将生成运行时子类该方法通过CGLIB包含类,这就是为什么这样的查找
 * 方法只能在容器通过常规构造函数实例化的bean上工作：即,查找方法不能被从工厂方法返回的bean替换,我们不能为它们动态提供一个子类。
 * 
 * @author Juergen Hoeller
 * @since 4.1
 * @see org.springframework.beans.factory.BeanFactory#getBean(Class, Object...)
 * @see org.springframework.beans.factory.BeanFactory#getBean(String, Object...)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Lookup {

	/**
	 * This annotation attribute may suggest a target bean name to look up.
	 * If not specified, the target bean will be resolved based on the
	 * annotated method's return type declaration.
	 * <p>
	 * 
	 * <p> <b>典型的Spring配置方案中的具体限制：</b>当使用组件扫描或任何其他过滤抽象bean的机制时,提供查找方法的存根实现,以便将其声明为具体类和请记住,查找方法将无法在配置类中从{@code @Bean}
	 * 方法返回的bean起作用;您必须诉诸{@code @Inject Provider&lt; TargetBean&gt;}等。
	 * 
	 */
	String value() default "";

}
