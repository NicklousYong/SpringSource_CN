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

package org.springframework.scheduling.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;

/**
 * Enables Spring's asynchronous method execution capability, similar to functionality
 * found in Spring's {@code <task:*>} XML namespace.
 *
 * <p>To be used on @{@link Configuration} classes as follows, where {@code MyAsyncBean}
 * is a user-defined type with one or more methods annotated with either Spring's
 * {@code @Async} annotation, the EJB 3.1 {@code @javax.ejb.Asynchronous} annotation,
 * or any custom annotation specified via the {@link #annotation} attribute.
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableAsync
 * public class AppConfig {
 *
 *     &#064;Bean
 *     public MyAsyncBean asyncBean() {
 *         return new MyAsyncBean();
 *     }
 * }</pre>
 *
 * <p>The {@link #mode} attribute controls how advice is applied; if the mode is
 * {@link AdviceMode#PROXY} (the default), then the other attributes control the behavior
 * of the proxying.
 *
 * <p>Note that if the {@linkplain #mode} is set to {@link AdviceMode#ASPECTJ}, then the
 * value of the {@link #proxyTargetClass} attribute will be ignored. Note also that in
 * this case the {@code spring-aspects} module JAR must be present on the classpath.
 *
 * <p>By default, Spring will be searching for an associated thread pool definition:
 * either a unique {@link org.springframework.core.task.TaskExecutor} bean in the context,
 * or an {@link java.util.concurrent.Executor} bean named "taskExecutor" otherwise. If
 * neither of the two is resolvable, a {@link org.springframework.core.task.SimpleAsyncTaskExecutor}
 * will be used to process async method invocations. Besides, annotated methods having a
 * {@code void} return type cannot transmit any exception back to the caller. By default,
 * such uncaught exceptions are only logged.
 *
 * <p>To customize all this, implement {@link AsyncConfigurer} and provide:
 * <ul>
 * <li>your own {@link java.util.concurrent.Executor Executor} through the
 * {@link AsyncConfigurer#getAsyncExecutor getAsyncExecutor()} method, and</li>
 * <li>your own {@link org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
 * AsyncUncaughtExceptionHandler} through the {@link AsyncConfigurer#getAsyncUncaughtExceptionHandler
 * getAsyncUncaughtExceptionHandler()}
 * method.</li>
 * </ul>
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableAsync
 * public class AppConfig implements AsyncConfigurer {
 *
 *     &#064;Bean
 *     public MyAsyncBean asyncBean() {
 *         return new MyAsyncBean();
 *     }
 *
 *     &#064;Override
 *     public Executor getAsyncExecutor() {
 *         ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
 *         executor.setCorePoolSize(7);
 *         executor.setMaxPoolSize(42);
 *         executor.setQueueCapacity(11);
 *         executor.setThreadNamePrefix("MyExecutor-");
 *         executor.initialize();
 *         return executor;
 *     }
 *
 *     &#064;Override
 *     public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
 *         return MyAsyncUncaughtExceptionHandler();
 *     }
 * }</pre>
 *
 * <p>If only one item needs to be customized, {@code null} can be returned to
 * keep the default settings. Consider also extending from {@link AsyncConfigurerSupport}
 * when possible.
 *
 * <p>Note: In the above example the {@code ThreadPoolTaskExecutor} is not a fully managed
 * Spring bean. Add the {@code @Bean} annotation to the {@code getAsyncExecutor()} method
 * if you want a fully managed bean. In such circumstances it is no longer necessary to
 * manually call the {@code executor.initialize()} method as this will be invoked
 * automatically when the bean is initialized.
 *
 * <p>For reference, the example above can be compared to the following Spring XML
 * configuration:
 *
 * <pre class="code">
 * {@code
 * <beans>
 *
 *     <task:annotation-driven executor="myExecutor" exception-handler="exceptionHandler"/>
 *
 *     <task:executor id="myExecutor" pool-size="7-42" queue-capacity="11"/>
 *
 *     <bean id="asyncBean" class="com.foo.MyAsyncBean"/>
 *
 *     <bean id="exceptionHandler" class="com.foo.MyAsyncUncaughtExceptionHandler"/>
 *
 * </beans>
 * }</pre>
 *
 * The above XML-based and JavaConfig-based examples are equivalent except for the
 * setting of the <em>thread name prefix</em> of the {@code Executor}; this is because
 * the {@code <task:executor>} element does not expose such an attribute. This
 * demonstrates how the JavaConfig-based approach allows for maximum configurability
 * through direct access to actual componentry.
 *
 * <p>
 *  启用S​​pring的异步方法执行功能,类似于Spring的{@code <task：*>} XML命名空间中的功能
 * 
 * <p>要在@ {@ link Configuration}类中使用如下,其中{@code MyAsyncBean}是一个用户定义的类型,其中包含一个或多个使用Spring的{@code @Async}注
 * 释注释的方法,EJB 31 { @code @javaxejbAsynchronous}注释或通过{@link #annotation}属性指定的任何自定义注释。
 * 
 * <pre class="code">
 *  @Configuration @EnableAsync public class AppConfig {
 * 
 *  @Bean public MyAsyncBean asyncBean(){return new MyAsyncBean(); }} </pre>
 * 
 *  <p> {@link #mode}属性控制如何应用建议;如果模式是{@link AdviceMode#PROXY}(默认),则其他属性控制代理的行为
 * 
 * <p>请注意,如果{@linkplain #mode}设置为{@link AdviceMode#ASPECTJ},则{@link #proxyTargetClass}属性的值将被忽略注意,在这种情况下,
 * {@code spring -aspects}模块JAR必须存在于类路径上。
 * 
 * <p>默认情况下,Spring将搜索关联的线程池定义：上下文中的唯一{@link orgspringframeworkcoretaskTaskExecutor} bean,否则命名为"taskExecu
 * tor"的{@link javautilconcurrentExecutor} bean如果两者都不可解析,{@link orgspringframeworkcoretaskSimpleAsyncTaskExecutor}
 * 将用于处理异步方法调用此外,具有{@code void}返回类型的注释方法无法将任何异常发送回调用者默认情况下,仅记录此类未捕获的异常。
 * 
 *  <p>要自定义所有这些,请执行{@link AsyncConfigurer}并提供：
 * <ul>
 * 通过{@link AsyncConfigurer#getAsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler {@link AsyncConfigurer#getAsyncExecutor getAsyncExecutor()}
 * 方法和</li> <li>您自己的{@link orgspringframeworkaopinterceptorAsyncUncaughtExceptionHandler AsyncUncaughtExceptionHandler}
 * ,您的自己的{@link javautilconcurrentExecutor Executor} ()}方法</li>。
 * </ul>
 * 
 * <pre class="code">
 *  @Configuration @EnableAsync public class AppConfig implements AsyncConfigurer {
 * 
 *  @Bean public MyAsyncBean asyncBean(){return new MyAsyncBean(); }
 * 
 * @Override public Executor getAsyncExecutor(){ThreadPoolTask​​Executor executor = new ThreadPoolTask​​Executor(); executorsetCorePoolSize(7); executorsetMaxPoolSize(42); executorsetQueueCapacity(11); executorsetThreadNamePrefix( "MyExecutor-"); executorinitialize();退货执行人}
 * 。
 * 
 *  @Override public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler(){return MyAsyncUncaughtExceptionHandler(); }
 * } </pre>。
 * 
 *  <p>如果只需要自定义一个项目,可以返回{@code null}以保持默认设置,尽可能考虑从{@link AsyncConfigurerSupport}延伸
 * 
 * 注意：在上面的例子中,{@code ThreadPoolTask​​Executor}不是一个完全管理的Spring bean如果你想要一个完全托管的bean,可以在{@code getAsyncExecutor()}
 * 
 * @author Chris Beams
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 * @author Sam Brannen
 * @since 3.1
 * @see Async
 * @see AsyncConfigurer
 * @see AsyncConfigurationSelector
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(AsyncConfigurationSelector.class)
public @interface EnableAsync {

	/**
	 * Indicate the 'async' annotation type to be detected at either class
	 * or method level.
	 * <p>By default, both Spring's @{@link Async} annotation and the EJB 3.1
	 * {@code @javax.ejb.Asynchronous} annotation will be detected.
	 * <p>This attribute exists so that developers can provide their own
	 * custom annotation type to indicate that a method (or all methods of
	 * a given class) should be invoked asynchronously.
	 * <p>
	 * 方法中添加{@code @Bean}注释。
	 * 情况不再需要手动调用{@code executorinitialize()}方法,因为这将在bean初始化时自动调用。
	 * 
	 *  <p>为了参考,可以将以上示例与以下Spring XML配置进行比较：
	 * 
	 * <pre class="code">
	 *  {@码
	 * <beans>
	 * 
	 * <task:annotation-driven executor="myExecutor" exception-handler="exceptionHandler"/>
	 * 
	 * <task:executor id="myExecutor" pool-size="7-42" queue-capacity="11"/>
	 * 
	 * <bean id="asyncBean" class="com.foo.MyAsyncBean"/>
	 * 
	 * <bean id="exceptionHandler" class="com.foo.MyAsyncUncaughtExceptionHandler"/>
	 * 
	 * </beans>
	 *  } </PRE>
	 * 
	 */
	Class<? extends Annotation> annotation() default Annotation.class;

	/**
	 * Indicate whether subclass-based (CGLIB) proxies are to be created as opposed
	 * to standard Java interface-based proxies.
	 * <p><strong>Applicable only if the {@link #mode} is set to {@link AdviceMode#PROXY}</strong>.
	 * <p>The default is {@code false}.
	 * <p>Note that setting this attribute to {@code true} will affect <em>all</em>
	 * Spring-managed beans requiring proxying, not just those marked with {@code @Async}.
	 * For example, other beans marked with Spring's {@code @Transactional} annotation
	 * will be upgraded to subclass proxying at the same time. This approach has no
	 * negative impact in practice unless one is explicitly expecting one type of proxy
	 * vs. another &mdash; for example, in tests.
	 * <p>
	 * 上述基于XML和JavaConfig的示例是等效的,除了{@code Executor}的<em>线程名称前缀</em>的设置之外;这是因为{@code <task：executor>}元素没有公开这样
	 * 的属性这说明了基于JavaConfig的方法如何通过直接访问实际组件来实现最大的可配置性。
	 * 
	 */
	boolean proxyTargetClass() default false;

	/**
	 * Indicate how async advice should be applied.
	 * <p>The default is {@link AdviceMode#PROXY}.
	 * <p>
	 *  指示要在类或方法级别检测到的"异步"注释类型<p>默认情况下,将检测到Spring的@ {@ link Async}注释和EJB 31 {@code @javaxejbAsynchronous}注释<p>
	 * 此属性存在,以便开发人员可以提供自己的自定义注释类型来指示异步调用方法(或给定类的所有方法)。
	 * 
	 * 
	 * @see AdviceMode
	 */
	AdviceMode mode() default AdviceMode.PROXY;

	/**
	 * Indicate the order in which the {@link AsyncAnnotationBeanPostProcessor}
	 * should be applied.
	 * <p>The default is {@link Ordered#LOWEST_PRECEDENCE} in order to run
	 * after all other post-processors, so that it can add an advisor to
	 * existing proxies rather than double-proxy.
	 * <p>
	 * 指示是否创建基于子类的(CGLIB)代理,而不是基于标准的基于Java接口的代理<p> <strong>仅适用于{@link #mode}设置为{@link AdviceMode#PROXY} </strong>
	 *  <p>默认值为{@code false} <p>请注意,将此属性设置为{@code true}将影响所有</em>需要代理的Spring管理的bean,而不仅仅是标记为{ @code @Async}例
	 * 如,标有Spring的{@code @Transactional}注释的其他bean将同时升级到子类代理。
	 * 这种方法在实践中没有负面影响,除非明确地期望一种类型的代理与另一种代理;例如,在测试中。
	 * 
	 */
	int order() default Ordered.LOWEST_PRECEDENCE;

}
