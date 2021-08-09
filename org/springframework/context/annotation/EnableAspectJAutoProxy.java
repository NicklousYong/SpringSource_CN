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
 * Enables support for handling components marked with AspectJ's {@code @Aspect} annotation,
 * similar to functionality found in Spring's {@code <aop:aspectj-autoproxy>} XML element.
 * To be used on @{@link Configuration} classes as follows:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableAspectJAutoProxy
 * public class AppConfig {
 *
 *     &#064;Bean
 *     public FooService fooService() {
 *         return new FooService();
 *     }
 *
 *     &#064;Bean
 *     public MyAspect myAspect() {
 *         return new MyAspect();
 *     }
 * }</pre>
 *
 * Where {@code FooService} is a typical POJO component and {@code MyAspect} is an
 * {@code @Aspect}-style aspect:
 *
 * <pre class="code">
 * public class FooService {
 *
 *     // various methods
 * }</pre>
 *
 * <pre class="code">
 * &#064;Aspect
 * public class MyAspect {
 *
 *     &#064;Before("execution(* FooService+.*(..))")
 *     public void advice() {
 *         // advise FooService methods as appropriate
 *     }
 * }</pre>
 *
 * In the scenario above, {@code @EnableAspectJAutoProxy} ensures that {@code MyAspect}
 * will be properly processed and that {@code FooService} will be proxied mixing in the
 * advice that it contributes.
 *
 * <p>Users can control the type of proxy that gets created for {@code FooService} using
 * the {@link #proxyTargetClass()} attribute. The following enables CGLIB-style 'subclass'
 * proxies as opposed to the default interface-based JDK proxy approach.
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableAspectJAutoProxy(proxyTargetClass=true)
 * public class AppConfig {
 *     // ...
 * }</pre>
 *
 * <p>Note that {@code @Aspect} beans may be component-scanned like any other. Simply
 * mark the aspect with both {@code @Aspect} and {@code @Component}:
 *
 * <pre class="code">
 * package com.foo;
 *
 * &#064;Component
 * public class FooService { ... }
 *
 * &#064;Aspect
 * &#064;Component
 * public class MyAspect { ... }</pre>
 *
 * Then use the @{@link ComponentScan} annotation to pick both up:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;ComponentScan("com.foo")
 * &#064;EnableAspectJAutoProxy
 * public class AppConfig {
 *
 *     // no explicit &#064Bean definitions required
 * }</pre>
 *
 * <p>
 * 支持处理标有AspectJ的{@code @Aspect}注释的组件,类似于Spring的{@code <aop：aspectj-autoproxy>} XML元素中的功能。
 * 要在@ {@ link Configuration}类中使用如下所示的功能：。
 * 
 * <pre class="code">
 *  @Configuration @EnableAspectJAutoProxy public class AppConfig {
 * 
 *  @Bean public FooService fooService(){return new FooService(); }
 * 
 *  @Bean public MyAspect myAspect(){return new MyAspect(); }} </pre>
 * 
 *  其中{@code FooService}是一个典型的POJO组件,{@code MyAspect}是一个{@code @Aspect}风格的方面：
 * 
 * <pre class="code">
 *  公共类FooService {
 * 
 *  //各种方法} </pre>
 * 
 * <pre class="code">
 *  @Aspect public class MyAspect {
 * 
 *  @Before("execution(* FooService + *())")public void advice(){//酌情提供FooService方法}} </pre>
 * 
 * 在上述情况下,{@code @EnableAspectJAutoProxy}可以确保正确处理{@code MyAspect},并且{@code FooService}将被代理混淆其提供的建议
 * 
 *  <p>用户可以使用{@link #proxyTargetClass()}属性来控制为{@code FooService}创建的代理类型。
 * 以下启用CGLIB风格的"子类"代理,而不是基于默认的基于接口的JDK代理途径。
 * 
 * 
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 * @see org.aspectj.lang.annotation.Aspect
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(AspectJAutoProxyRegistrar.class)
public @interface EnableAspectJAutoProxy {

	/**
	 * Indicate whether subclass-based (CGLIB) proxies are to be created as opposed
	 * to standard Java interface-based proxies. The default is {@code false}.
	 * <p>
	 * <pre class="code">
	 *  @Configuration @EnableAspectJAutoProxy(proxyTargetClass = true)public class AppConfig {//} </pre>
	 * 
	 *  请注意,{@code @Aspect} bean可能像任何其他组件扫描一样简单地标记{@code @Aspect}和{@code @Component}的方面：
	 * 
	 * <pre class="code">
	 *  包装comfoo;
	 * 
	 *  @Component public class FooService {}
	 * 
	 * @Aspect @Component public class MyAspect {} </pre>
	 * 
	 *  然后使用@ {@ link ComponentScan}注释来同时选择：
	 */
	boolean proxyTargetClass() default false;

	/**
	 * Indicate that the proxy should be exposed by the AOP framework as a {@code ThreadLocal}
	 * for retrieval via the {@link org.springframework.aop.framework.AopContext} class.
	 * Off by default, i.e. no guarantees that {@code AopContext} access will work.
	 * <p>
	 * 
	 * <pre class="code">
	 *  @Configuration @ComponentScan("comfoo")@EnableAspectJAutoProxy public class AppConfig {
	 * 
	 *  //不需要明确的&#064Bean定义} </pre>
	 * 
	 * 
	 * @since 4.3.1
	 */
	boolean exposeProxy() default false;

}
