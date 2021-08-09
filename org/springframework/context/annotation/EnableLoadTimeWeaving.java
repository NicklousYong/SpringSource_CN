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

import org.springframework.context.weaving.DefaultContextLoadTimeWeaver;
import org.springframework.instrument.classloading.LoadTimeWeaver;

/**
 * Activates a Spring {@link LoadTimeWeaver} for this application context, available as
 * a bean with the name "loadTimeWeaver", similar to the {@code <context:load-time-weaver>}
 * element in Spring XML.
 *
 * <p>To be used on @{@link org.springframework.context.annotation.Configuration Configuration} classes;
 * the simplest possible example of which follows:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableLoadTimeWeaving
 * public class AppConfig {
 *
 *     // application-specific &#064;Bean definitions ...
 * }</pre>
 *
 * The example above is equivalent to the following Spring XML configuration:
 *
 * <pre class="code">
 * {@code
 * <beans>
 *
 *     <context:load-time-weaver/>
 *
 *     <!-- application-specific <bean> definitions -->
 *
 * </beans>
 * }</pre>
 *
 * <h2>The {@code LoadTimeWeaverAware} interface</h2>
 * Any bean that implements the {@link
 * org.springframework.context.weaving.LoadTimeWeaverAware LoadTimeWeaverAware} interface
 * will then receive the {@code LoadTimeWeaver} reference automatically; for example,
 * Spring's JPA bootstrap support.
 *
 * <h2>Customizing the {@code LoadTimeWeaver}</h2>
 * The default weaver is determined automatically: see {@link DefaultContextLoadTimeWeaver}.
 *
 * <p>To customize the weaver used, the {@code @Configuration} class annotated with
 * {@code @EnableLoadTimeWeaving} may also implement the {@link LoadTimeWeavingConfigurer}
 * interface and return a custom {@code LoadTimeWeaver} instance through the
 * {@code #getLoadTimeWeaver} method:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableLoadTimeWeaving
 * public class AppConfig implements LoadTimeWeavingConfigurer {
 *
 *     &#064;Override
 *     public LoadTimeWeaver getLoadTimeWeaver() {
 *         MyLoadTimeWeaver ltw = new MyLoadTimeWeaver();
 *         ltw.addClassTransformer(myClassFileTransformer);
 *         // ...
 *         return ltw;
 *     }
 * }</pre>
 *
 * <p>The example above can be compared to the following Spring XML configuration:
 *
 * <pre class="code">
 * {@code
 * <beans>
 *
 *     <context:load-time-weaver weaverClass="com.acme.MyLoadTimeWeaver"/>
 *
 * </beans>
 * }</pre>
 *
 * <p>The code example differs from the XML example in that it actually instantiates the
 * {@code MyLoadTimeWeaver} type, meaning that it can also configure the instance, e.g.
 * calling the {@code #addClassTransformer} method. This demonstrates how the code-based
 * configuration approach is more flexible through direct programmatic access.
 *
 * <h2>Enabling AspectJ-based weaving</h2>
 * AspectJ load-time weaving may be enabled with the {@link #aspectjWeaving()}
 * attribute, which will cause the {@linkplain
 * org.aspectj.weaver.loadtime.ClassPreProcessorAgentAdapter AspectJ class transformer} to
 * be registered through {@link LoadTimeWeaver#addTransformer}. AspectJ weaving will be
 * activated by default if a "META-INF/aop.xml" resource is present on the classpath.
 * Example:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableLoadTimeWeaving(aspectjWeaving=ENABLED)
 * public class AppConfig {
 * }</pre>
 *
 * <p>The example above can be compared to the following Spring XML configuration:
 *
 * <pre class="code">
 * {@code
 * <beans>
 *
 *     <context:load-time-weaver aspectj-weaving="on"/>
 *
 * </beans>
 * }</pre>
 *
 * <p>The two examples are equivalent with one significant exception: in the XML case,
 * the functionality of {@code <context:spring-configured>} is implicitly enabled when
 * {@code aspectj-weaving} is "on".  This does not occur when using
 * {@code @EnableLoadTimeWeaving(aspectjWeaving=ENABLED)}. Instead you must explicitly add
 * {@code @EnableSpringConfigured} (included in the {@code spring-aspects} module)
 *
 * <p>
 *  为此应用程序上下文激活一个Spring {@link LoadTimeWeaver},可以作为名为"loadTimeWeaver"的bean使用,类似于Spring XML中的{@code <context：load-time-weaver>}
 * 元素。
 * 
 * <p>要用于@ {@ link orgspringframeworkcontextannotationConfiguration Configuration}类;最简单的例子如下：
 * 
 * <pre class="code">
 *  @Configuration @EnableLoadTimeWeaving public class AppConfig {
 * 
 *  // application-specific @Bean definitions} </pre>
 * 
 *  上面的示例等同于以下Spring XML配置：
 * 
 * <pre class="code">
 *  {@码
 * <beans>
 * 
 * <context:load-time-weaver/>
 * 
 *  <！ - 应用程序特定的<bean>定义 - >
 * 
 * </beans>
 *  } </PRE>
 * 
 *  <h2> {@code LoadTimeWeaverAware}接口</h2>任何实现{@link orgspringframeworkcontextweavingLoadTimeWeaverAware LoadTimeWeaverAware}
 * 接口的bean都将自动收到{@code LoadTimeWeaver}引用;例如,Spring的JPA引导支持。
 * 
 * <h2>自定义{@code LoadTimeWeaver} </h2>默认的weaver是自动确定的：请参阅{@link DefaultContextLoadTimeWeaver}
 * 
 *  <p>要自定义使用的编织器,使用{@code @EnableLoadTimeWeaving}注释的{@code @Configuration}类也可以实现{@link LoadTimeWeavingConfigurer}
 * 接口,并通过{@code返回自定义{@code LoadTimeWeaver}实例#getLoadTimeWeaver}方法：。
 * 
 * <pre class="code">
 *  @Configuration @EnableLoadTimeWeaving public class AppConfig implements LoadTimeWeavingConfigurer {。
 * 
 *  @Override public LoadTimeWeaver getLoadTimeWeaver(){MyLoadTimeWeaver ltw = new MyLoadTimeWeaver(); ltwaddClassTransformer(myClassFileTransformer); // return ltw; }
 * } </pre>。
 * 
 *  <p>上面的示例可以与下面的Spring XML配置进行比较：
 * 
 * <pre class="code">
 * {@码
 * <beans>
 * 
 * 
 * @author Chris Beams
 * @since 3.1
 * @see LoadTimeWeaver
 * @see DefaultContextLoadTimeWeaver
 * @see org.aspectj.weaver.loadtime.ClassPreProcessorAgentAdapter
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(LoadTimeWeavingConfiguration.class)
public @interface EnableLoadTimeWeaving {

	/**
	 * Whether AspectJ weaving should be enabled.
	 * <p>
	 * <context:load-time-weaver weaverClass="com.acme.MyLoadTimeWeaver"/>
	 * 
	 * </beans>
	 *  } </PRE>
	 * 
	 *  <p>代码示例与XML示例不同,它实际上实例化了{@code MyLoadTimeWeaver}类型,这意味着它也可以配置实例,例如调用{@code #addClassTransformer}方法这演
	 * 示了基于代码的配置方式通过直接编程访问更加灵活。
	 * 
	 *  <h2>启用基于AspectJ的编织</h2>可以使用{@link #aspectjWeaving()}属性启用AspectJ加载时编织,这将导致{@linkplain orgaspectjweaverloadtimeClassPreProcessorAgentAdapter AspectJ类变换器}
	 * 通过{@链接LoadTimeWeaver#addTransformer}如果类路径中存在"META-INF / aopxml"资源,则默认情况下将激活AspectJ编织示例：。
	 * 
	 * <pre class="code">
	 * @Configuration @EnableLoadTimeWeaving(aspectjWeaving = ENABLED)public class AppConfig {} </pre>
	 * 
	 *  <p>上面的示例可以与下面的Spring XML配置进行比较：
	 * 
	 * <pre class="code">
	 *  {@码
	 * <beans>
	 * 
	 * <context:load-time-weaver aspectj-weaving="on"/>
	 */
	AspectJWeaving aspectjWeaving() default AspectJWeaving.AUTODETECT;


	enum AspectJWeaving {

		/**
		 * Switches on Spring-based AspectJ load-time weaving.
		 * <p>
		 * 
		 * </beans>
		 *  } </PRE>
		 * 
		 *  <p>这两个示例与一个重要的例外相当：在XML的情况下,{@code <context：spring-configured>}的功能在{@code aspectj-weaving}为"on"时被隐式启
		 * 用。
		 * 发生在使用{@code @EnableLoadTimeWeaving(aspectjWeaving = ENABLED)}时),而是必须显式添加{@code @EnableSpringConfigured}
		 * (包含在{@code spring-aspects}模块中)。
		 * 
		 */
		ENABLED,

		/**
		 * Switches off Spring-based AspectJ load-time weaving (even if a
		 * "META-INF/aop.xml" resource is present on the classpath).
		 * <p>
		 *  是否应启用AspectJ编织
		 * 
		 */
		DISABLED,

		/**
		 * Switches on AspectJ load-time weaving if a "META-INF/aop.xml" resource
		 * is present in the classpath. If there is no such resource, then AspectJ
		 * load-time weaving will be switched off.
		 * <p>
		 *  基于Spring的AspectJ加载时间编织
		 * 
		 */
		AUTODETECT;
	}

}
