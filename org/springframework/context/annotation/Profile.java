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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Indicates that a component is eligible for registration when one or more
 * {@linkplain #value specified profiles} are active.
 *
 * <p>A <em>profile</em> is a named logical grouping that may be activated
 * programmatically via {@link ConfigurableEnvironment#setActiveProfiles} or declaratively
 * by setting the {@link AbstractEnvironment#ACTIVE_PROFILES_PROPERTY_NAME
 * spring.profiles.active} property as a JVM system property, as an
 * environment variable, or as a Servlet context parameter in {@code web.xml}
 * for web applications. Profiles may also be activated declaratively in
 * integration tests via the {@code @ActiveProfiles} annotation.
 *
 * <p>The {@code @Profile} annotation may be used in any of the following ways:
 * <ul>
 * <li>as a type-level annotation on any class directly or indirectly annotated with
 * {@code @Component}, including {@link Configuration @Configuration} classes</li>
 * <li>as a meta-annotation, for the purpose of composing custom stereotype annotations</li>
 * <li>as a method-level annotation on any {@link Bean @Bean} method</li>
 * </ul>
 *
 * <p>If a {@code @Configuration} class is marked with {@code @Profile}, all of the
 * {@code @Bean} methods and {@link Import @Import} annotations associated with that class
 * will be bypassed unless one or more of the specified profiles are active. This is
 * analogous to the behavior in Spring XML: if the {@code profile} attribute of the
 * {@code beans} element is supplied e.g., {@code <beans profile="p1,p2">}, the
 * {@code beans} element will not be parsed unless at least profile 'p1' or 'p2' has been
 * activated. Likewise, if a {@code @Component} or {@code @Configuration} class is marked
 * with {@code @Profile({"p1", "p2"})}, that class will not be registered or processed unless
 * at least profile 'p1' or 'p2' has been activated.
 *
 * <p>If a given profile is prefixed with the NOT operator ({@code !}), the annotated
 * component will be registered if the profile is <em>not</em> active &mdash; for example,
 * given {@code @Profile({"p1", "!p2"})}, registration will occur if profile 'p1' is active or
 * if profile 'p2' is <em>not</em> active.
 *
 * <p>If the {@code @Profile} annotation is omitted, registration will occur regardless
 * of which (if any) profiles are active.
 *
 * <p>When defining Spring beans via XML, the {@code "profile"} attribute of the
 * {@code <beans>} element may be used. See the documentation in the
 * {@code spring-beans} XSD (version 3.1 or greater) for details.
 *
 * <p>
 *  表示当一个或多个{@linkplain #value指定的配置文件}处于活动状态时,组件有资格注册
 * 
 * <p> A <em>配置文件</em>是一种命名的逻辑分组,可以通过{@link ConfigurableEnvironment#setActiveProfiles}以编程方式激活或通过将{@link AbstractEnvironment#ACTIVE_PROFILES_PROPERTY_NAME springprofilesactive}
 * 属性设置为JVM系统属性来声明式地进行激活,作为环境变量,或作为Web应用程序的{@code webxml}中的Servlet上下文参数通过{@code @ActiveProfiles}注释在集成测试中
 * 也可以声明地激活配置文件。
 * 
 *  <p> {@code @Profile}注释可以以下列任何方式使用：
 * <ul>
 * <li>作为使用{@code @Component}直接或间接注释的任何类的类型级注释,包括{@link Configuration @Configuration}类</li> <li>作为元注释,用于
 * 在任何{@link Bean @Bean}方法中,将自定义原型注释</li> <li>作为方法级注解; </li>。
 * </ul>
 * 
 * <p>如果{@code @Configuration}类标有{@code @Profile},则与该类关联的所有{@code @Bean}方法和{@link Import @Import}注释将被忽略,
 * 除非或更多的指定配置文件是活动的这类似于Spring XML中的行为：如果提供{@code beans}元素的{@code profile}属性,例如{@code <beans profile ="p1,p2" >}
 * 
 * @author Chris Beams
 * @author Phillip Webb
 * @author Sam Brannen
 * @since 3.1
 * @see ConfigurableEnvironment#setActiveProfiles
 * @see ConfigurableEnvironment#setDefaultProfiles
 * @see AbstractEnvironment#ACTIVE_PROFILES_PROPERTY_NAME
 * @see AbstractEnvironment#DEFAULT_PROFILES_PROPERTY_NAME
 * @see Conditional
 * @see org.springframework.test.context.ActiveProfiles
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Conditional(ProfileCondition.class)
public @interface Profile {

	/**
	 * The set of profiles for which the annotated component should be registered.
	 * <p>
	 * ,{@code beans}元素将不会被解析,除非至少配置文件'p1'或'p2'已被激活同样,如果{@code @Component}或{@code @Configuration}类标记为{ @code @Profile({"p1","p2"}
	 * )},该类不会被注册或处理,除非至少配置文件'p1'或'p2'已被激活。
	 * 
	 * <p>如果给定的配置文件带有NOT运算符({@code！})的前缀,则如果配置文件不是<em>,则注释的组件将被注册为活动&mdash;例如,给定{@code @Profile({"p1","！p2"}
	 * )},如果配置文件"p1"处于活动状态或者如果配置文件"p2"不<em>不</em>活动。
	 * 
	 *  <p>如果省略{@code @Profile}注释,则无论哪个(如果有)配置文件处于活动状态,都将进行注册
	 */
	String[] value();

}
