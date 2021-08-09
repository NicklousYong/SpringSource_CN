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

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.core.annotation.AliasFor;

/**
 * Indicates that a method produces a bean to be managed by the Spring container.
 *
 * <h3>Overview</h3>
 *
 * <p>The names and semantics of the attributes to this annotation are intentionally
 * similar to those of the {@code <bean/>} element in the Spring XML schema. For
 * example:
 *
 * <pre class="code">
 *     &#064;Bean
 *     public MyBean myBean() {
 *         // instantiate and configure MyBean obj
 *         return obj;
 *     }
 * </pre>
 *
 * <h3>Bean Names</h3>
 *
 * <p>While a {@link #name} attribute is available, the default strategy for
 * determining the name of a bean is to use the name of the {@code @Bean} method.
 * This is convenient and intuitive, but if explicit naming is desired, the
 * {@code name} attribute (or its alias {@code value}) may be used. Also note
 * that {@code name} accepts an array of Strings, allowing for multiple names
 * (i.e. a primary bean name plus one or more aliases) for a single bean.
 *
 * <pre class="code">
 *     &#064;Bean({"b1", "b2"}) // bean available as 'b1' and 'b2', but not 'myBean'
 *     public MyBean myBean() {
 *         // instantiate and configure MyBean obj
 *         return obj;
 *     }
 * </pre>
 *
 * <h3>Scope, DependsOn, Primary, and Lazy</h3>
 *
 * <p>Note that the {@code @Bean} annotation does not provide attributes for scope,
 * depends-on, primary, or lazy. Rather, it should be used in conjunction with
 * {@link Scope @Scope}, {@link DependsOn @DependsOn}, {@link Primary @Primary},
 * and {@link Lazy @Lazy} annotations to achieve those semantics. For example:
 *
 * <pre class="code">
 *     &#064;Bean
 *     &#064;Scope("prototype")
 *     public MyBean myBean() {
 *         // instantiate and configure MyBean obj
 *         return obj;
 *     }
 * </pre>
 *
 * <h3>{@code @Bean} Methods in {@code @Configuration} Classes</h3>
 *
 * <p>Typically, {@code @Bean} methods are declared within {@code @Configuration}
 * classes. In this case, bean methods may reference other {@code @Bean} methods in the
 * same class by calling them <i>directly</i>. This ensures that references between beans
 * are strongly typed and navigable. Such so-called <em>'inter-bean references'</em> are
 * guaranteed to respect scoping and AOP semantics, just like {@code getBean()} lookups
 * would. These are the semantics known from the original 'Spring JavaConfig' project
 * which require CGLIB subclassing of each such configuration class at runtime. As a
 * consequence, {@code @Configuration} classes and their factory methods must not be
 * marked as final or private in this mode. For example:
 *
 * <pre class="code">
 * &#064;Configuration
 * public class AppConfig {
 *
 *     &#064;Bean
 *     public FooService fooService() {
 *         return new FooService(fooRepository());
 *     }
 *
 *     &#064;Bean
 *     public FooRepository fooRepository() {
 *         return new JdbcFooRepository(dataSource());
 *     }
 *
 *     // ...
 * }</pre>
 *
 * <h3>{@code @Bean} <em>Lite</em> Mode</h3>
 *
 * <p>{@code @Bean} methods may also be declared within classes that are <em>not</em>
 * annotated with {@code @Configuration}. For example, bean methods may be declared
 * in a {@code @Component} class or even in a <em>plain old class</em>. In such cases,
 * a {@code @Bean} method will get processed in a so-called <em>'lite'</em> mode.
 *
 * <p>Bean methods in <em>lite</em> mode will be treated as plain <em>factory
 * methods</em> by the container (similar to {@code factory-method} declarations
 * in XML), with scoping and lifecycle callbacks properly applied. The containing
 * class remains unmodified in this case, and there are no unusual constraints for
 * the containing class or the factory methods.
 *
 * <p>In contrast to the semantics for bean methods in {@code @Configuration} classes,
 * <em>'inter-bean references'</em> are not supported in <em>lite</em> mode. Instead,
 * when one {@code @Bean}-method invokes another {@code @Bean}-method in <em>lite</em>
 * mode, the invocation is a standard Java method invocation; Spring does not intercept
 * the invocation via a CGLIB proxy. This is analogous to inter-{@code @Transactional}
 * method calls where in proxy mode, Spring does not intercept the invocation &mdash;
 * Spring does so only in AspectJ mode.
 *
 * <p>For example:
 *
 * <pre class="code">
 * &#064;Component
 * public class Calculator {
 *     public int sum(int a, int b) {
 *         return a+b;
 *     }
 *
 *     &#064;Bean
 *     public MyBean myBean() {
 *         return new MyBean();
 *     }
 * }</pre>
 *
 * <h3>Bootstrapping</h3>
 *
 * <p>See @{@link Configuration} Javadoc for further details including how to bootstrap
 * the container using {@link AnnotationConfigApplicationContext} and friends.
 *
 * <h3>{@code BeanFactoryPostProcessor}-returning {@code @Bean} methods</h3>
 *
 * <p>Special consideration must be taken for {@code @Bean} methods that return Spring
 * {@link org.springframework.beans.factory.config.BeanFactoryPostProcessor BeanFactoryPostProcessor}
 * ({@code BFPP}) types. Because {@code BFPP} objects must be instantiated very early in the
 * container lifecycle, they can interfere with processing of annotations such as {@code @Autowired},
 * {@code @Value}, and {@code @PostConstruct} within {@code @Configuration} classes. To avoid these
 * lifecycle issues, mark {@code BFPP}-returning {@code @Bean} methods as {@code static}. For example:
 *
 * <pre class="code">
 *     &#064;Bean
 *     public static PropertyPlaceholderConfigurer ppc() {
 *         // instantiate, configure and return ppc ...
 *     }
 * </pre>
 *
 * By marking this method as {@code static}, it can be invoked without causing instantiation of its
 * declaring {@code @Configuration} class, thus avoiding the above-mentioned lifecycle conflicts.
 * Note however that {@code static} {@code @Bean} methods will not be enhanced for scoping and AOP
 * semantics as mentioned above. This works out in {@code BFPP} cases, as they are not typically
 * referenced by other {@code @Bean} methods. As a reminder, a WARN-level log message will be
 * issued for any non-static {@code @Bean} methods having a return type assignable to
 * {@code BeanFactoryPostProcessor}.
 *
 * <p>
 *  表示方法生成要由Spring容器管理的bean
 * 
 *  <H3>概述</H3>
 * 
 * <p>此注释的属性的名称和语义有意与Spring XML模式中{@code <bean />}元素的名称和语义相似例如：
 * 
 * <pre class="code">
 *  @Bean public MyBean myBean(){//实例化和配置MyBean对象返回obj; }
 * </pre>
 * 
 *  <h3> Bean名称</h3>
 * 
 *  <p>虽然{@link #name}属性可用,但确定bean名称的默认策略是使用{@code @Bean}方法的名称这是方便直观的,但是如果显式命名是可以使用{@code name}属性(或其别名{@code值}
 * )。
 * 另请注意,{@code name}接受一个字符串数组,允许多个名称(即主bean名称加上一个或多个别名)。
 * 
 * <pre class="code">
 * @Bean({"b1","b2"})// bean可用作"b1"和"b2",但不是"myBean"public MyBean myBean(){//实例化和配置MyBean obj return obj; }
 * 。
 * </pre>
 * 
 *  <h3>范围,DependsOn,Primary和Lazy </h3>
 * 
 *  <p>请注意,{@code @Bean}注释不提供范围,依赖,主要或懒惰的属性,而应与{@link Scope @Scope} {@link DependsOn @ DependsOn},{@link Primary @Primary}
 * 和{@link Lazy @Lazy}注释来实现这些语义例如：。
 * 
 * <pre class="code">
 *  @Bean @Scope("prototype")public MyBean myBean(){//实例化并配置MyBean对象返回obj; }
 * </pre>
 * 
 *  </h3> {@code @Bean} {@code @Configuration}类中的方法
 * 
 * 通常,{@code @Bean}方法在{@code @Configuration}类中声明在这种情况下,bean方法可以通过直接调用它们来引用同一类中的其他{@code @Bean}方法< / i>这样
 * 可以确保bean之间的引用是强类型的和可导航的。
 * 这样所谓的"inter-bean引用"</em>被保证尊重范围和AOP语义,就像{@code getBean()}查找一样这些是从原始"Spring JavaConfig"项目中已知的语义,它需要在运行
 * 时对每个这样的配置类进行CGLIB子类化。
 * 因此,{@code @Configuration}类及其工厂方法在此不能被标记为final或private模式例如：。
 * 
 * <pre class="code">
 *  @Configuration public class AppConfig {
 * 
 * @Bean public FooService fooService(){return new FooService(fooRepository()); }
 * 
 *  @Bean public FooRepository fooRepository(){return new JdbcFooRepository(dataSource()); }
 * 
 *  //} </pre>
 * 
 *  <h3> {@ code @Bean} <em> </em>模式</h3>
 * 
 *  {@code @Bean}方法也可以在使用{@code @Configuration}注释的类中声明。
 * 例如,bean方法可以在{@code @Component}中声明,类或甚至在普通旧类中</em>在这种情况下,{@code @Bean}方法将以所谓的<em>'lite'</em>模式进行处理。
 * 
 * <p> </em>模式中的Bean方法将被容器(类似于XML中的{@code factory-method}声明)视为简单的工厂方法),其范围和生命周期回调正确应用在这种情况下,包含类保持未修改,并且对
 * 于包含类或工厂方法没有不寻常的约束。
 * 
 * 
 * @author Rod Johnson
 * @author Costin Leau
 * @author Chris Beams
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 3.0
 * @see Configuration
 * @see Scope
 * @see DependsOn
 * @see Lazy
 * @see Primary
 * @see org.springframework.stereotype.Component
 * @see org.springframework.beans.factory.annotation.Autowired
 * @see org.springframework.beans.factory.annotation.Value
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Bean {

	/**
	 * Alias for {@link #name}.
	 * <p>Intended to be used when no other attributes are needed, for example:
	 * {@code @Bean("customBeanName")}.
	 * <p>
	 * <p>与{@code @Configuration}类中的bean方法的语义相反,<em> lite </em>模式不支持<em>'inter-bean references'</em> {@code @Bean}
	 *  -method在<em> lite </em>模式中调用另一个{@code @Bean}方法,调用是标准的Java方法调用; Spring不会通过CGLIB代理拦截调用这类似于代理模式下的{@ code @Transactional}
	 * 方法调用,Spring不会拦截调用&mdash; Spring只在AspectJ模式下执行此操作。
	 * 
	 *  例如：
	 * 
	 * <pre class="code">
	 *  @Component public class Calculator {public int sum(int a,int b){return a + b; }
	 * 
	 *  @Bean public MyBean myBean(){return new MyBean(); }} </pre>
	 * 
	 *  <H3>自举</H3>
	 * 
	 * <p>请参阅@ {@ link Configuration} Javadoc了解更多详细信息,包括如何使用{@link AnnotationConfigApplicationContext}和朋友引导容
	 * 器。
	 * 
	 *  <h3> {@ code BeanFactoryPostProcessor}  - 返回{@code @Bean}方法</h3>
	 * 
	 * 必须特别考虑返回Spring {@link orgspringframeworkbeansfactoryconfigBeanFactoryPostProcessor BeanFactoryPostProcessor}
	 * ({@code BFPP})类型的{@code @Bean}方法因为{@code BFPP}对象必须在容器生命周期的早期被实例化,他们可以干扰在{@code @Configuration}类中处理诸如{@code @Autowired}
	 * ,{@code @Value}和{@code @PostConstruct}的注释。
	 * 为了避免这些生命周期问题,请标记{@code BFPP}将{@code @Bean}方法归结为{@code static}例如：。
	 * 
	 * <pre class="code">
	 *  @Bean public static PropertyPlaceholderConfigurer ppc(){//实例化,配置和返回ppc}
	 * </pre>
	 * 
	 * 通过将此方法标记为{@code static},可以调用它,而不会导致声明{@code @Configuration}类的实例化,从而避免上述生命周期冲突注意,{@code static} {@code @Bean }
	 * 
	 * @since 4.3.3
	 * @see #name
	 */
	@AliasFor("name")
	String[] value() default {};

	/**
	 * The name of this bean, or if several names, a primary bean name plus aliases.
	 * <p>If left unspecified, the name of the bean is the name of the annotated method.
	 * If specified, the method name is ignored.
	 * <p>The bean name and aliases may also be configured via the {@link #value}
	 * attribute if no other attributes are declared.
	 * <p>
	 * 方法将不会被提升为范围和AOP语义,如上所述这在{@code BFPP}案例中有效,因为它们通常不被其他{@code @Bean}方法引用。
	 * 作为提醒,WARN级别的日志消息将发布任何具有可分配给{@code BeanFactoryPostProcessor}的返回类型的非静态{@code @Bean}方法。
	 * 
	 * 
	 * @see #value
	 */
	@AliasFor("value")
	String[] name() default {};

	/**
	 * Are dependencies to be injected via convention-based autowiring by name or type?
	 * <p>Note that this autowire mode is just about externally driven autowiring based
	 * on bean property setter methods by convention, analogous to XML bean definitions.
	 * <p>The default mode does allow for annotation-driven autowiring. "no" refers to
	 * externally driven autowiring only, not affecting any autowiring demands that the
	 * bean class itself expresses through annotations.
	 * <p>
	 *  别名{@link #name} <p>如果不需要其他属性,则可以使用,例如：{@code @Bean("customBeanName")}
	 * 
	 * 
	 * @see Autowire#BY_NAME
	 * @see Autowire#BY_TYPE
	 */
	Autowire autowire() default Autowire.NO;

	/**
	 * The optional name of a method to call on the bean instance during initialization.
	 * Not commonly used, given that the method may be called programmatically directly
	 * within the body of a Bean-annotated method.
	 * <p>The default value is {@code ""}, indicating no init method to be called.
	 * <p>
	 * 这个bean的名称,或者如果有几个名字,一个主bean名称加上别名<p>如果未指定,则该bean的名称是注释方法的名称如果指定,则忽略该方法名称<p>该bean名称如果没有声明其他属性,也可以通过{@link #value}
	 * 属性配置别名。
	 * 
	 */
	String initMethod() default "";

	/**
	 * The optional name of a method to call on the bean instance upon closing the
	 * application context, for example a {@code close()} method on a JDBC
	 * {@code DataSource} implementation, or a Hibernate {@code SessionFactory} object.
	 * The method must have no arguments but may throw any exception.
	 * <p>As a convenience to the user, the container will attempt to infer a destroy
	 * method against an object returned from the {@code @Bean} method. For example, given
	 * an {@code @Bean} method returning an Apache Commons DBCP {@code BasicDataSource},
	 * the container will notice the {@code close()} method available on that object and
	 * automatically register it as the {@code destroyMethod}. This 'destroy method
	 * inference' is currently limited to detecting only public, no-arg methods named
	 * 'close' or 'shutdown'. The method may be declared at any level of the inheritance
	 * hierarchy and will be detected regardless of the return type of the {@code @Bean}
	 * method (i.e., detection occurs reflectively against the bean instance itself at
	 * creation time).
	 * <p>To disable destroy method inference for a particular {@code @Bean}, specify an
	 * empty string as the value, e.g. {@code @Bean(destroyMethod="")}. Note that the
	 * {@link org.springframework.beans.factory.DisposableBean} and the
	 * {@link java.io.Closeable}/{@link java.lang.AutoCloseable} interfaces will
	 * nevertheless get detected and the corresponding destroy/close method invoked.
	 * <p>Note: Only invoked on beans whose lifecycle is under the full control of the
	 * factory, which is always the case for singletons but not guaranteed for any
	 * other scope.
	 * <p>
	 *  通过名称或类型通过基于惯例的自动装配注入依赖关系?注意,这种自动布线模式只是基于bean属性设置器方法的外部驱动自动布线,类似于XML bean定义<p>默认模式允许注释驱动的自动布线"否"仅指外部驱
	 * 动的自动布线,不影响bean类本身通过注释表示的任何自动连线要求。
	 * 
	 * 
	 * @see org.springframework.context.ConfigurableApplicationContext#close()
	 */
	String destroyMethod() default AbstractBeanDefinition.INFER_METHOD;

}
