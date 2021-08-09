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
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.io.support.PropertySourceFactory;

/**
 * Annotation providing a convenient and declarative mechanism for adding a
 * {@link org.springframework.core.env.PropertySource PropertySource} to Spring's
 * {@link org.springframework.core.env.Environment Environment}. To be used in
 * conjunction with @{@link Configuration} classes.
 *
 * <h3>Example usage</h3>
 *
 * <p>Given a file {@code app.properties} containing the key/value pair
 * {@code testbean.name=myTestBean}, the following {@code @Configuration} class
 * uses {@code @PropertySource} to contribute {@code app.properties} to the
 * {@code Environment}'s set of {@code PropertySources}.
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;PropertySource("classpath:/com/myco/app.properties")
 * public class AppConfig {
 *     &#064;Autowired
 *     Environment env;
 *
 *     &#064;Bean
 *     public TestBean testBean() {
 *         TestBean testBean = new TestBean();
 *         testBean.setName(env.getProperty("testbean.name"));
 *         return testBean;
 *     }
 * }</pre>
 *
 * Notice that the {@code Environment} object is @{@link
 * org.springframework.beans.factory.annotation.Autowired Autowired} into the
 * configuration class and then used when populating the {@code TestBean} object. Given
 * the configuration above, a call to {@code testBean.getName()} will return "myTestBean".
 *
 * <h3>Resolving ${...} placeholders in {@code <bean>} and {@code @Value} annotations</h3>
 *
 * In order to resolve ${...} placeholders in {@code <bean>} definitions or {@code @Value}
 * annotations using properties from a {@code PropertySource}, one must register
 * a {@code PropertySourcesPlaceholderConfigurer}. This happens automatically when using
 * {@code <context:property-placeholder>} in XML, but must be explicitly registered using
 * a {@code static} {@code @Bean} method when using {@code @Configuration} classes. See
 * the "Working with externalized values" section of @{@link Configuration}'s javadoc and
 * "a note on BeanFactoryPostProcessor-returning @Bean methods" of @{@link Bean}'s javadoc
 * for details and examples.
 *
 * <h3>Resolving ${...} placeholders within {@code @PropertySource} resource locations</h3>
 *
 * Any ${...} placeholders present in a {@code @PropertySource} {@linkplain #value()
 * resource location} will be resolved against the set of property sources already
 * registered against the environment. For example:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;PropertySource("classpath:/com/${my.placeholder:default/path}/app.properties")
 * public class AppConfig {
 *     &#064;Autowired
 *     Environment env;
 *
 *     &#064;Bean
 *     public TestBean testBean() {
 *         TestBean testBean = new TestBean();
 *         testBean.setName(env.getProperty("testbean.name"));
 *         return testBean;
 *     }
 * }</pre>
 *
 * Assuming that "my.placeholder" is present in one of the property sources already
 * registered, e.g. system properties or environment variables, the placeholder will
 * be resolved to the corresponding value. If not, then "default/path" will be used as a
 * default. Expressing a default value (delimited by colon ":") is optional.  If no
 * default is specified and a property cannot be resolved, an {@code
 * IllegalArgumentException} will be thrown.
 *
 * <h3>A note on property overriding with @PropertySource</h3>
 *
 * In cases where a given property key exists in more than one {@code .properties}
 * file, the last {@code @PropertySource} annotation processed will 'win' and override.
 *
 * For example, given two properties files {@code a.properties} and
 * {@code b.properties}, consider the following two configuration classes
 * that reference them with {@code @PropertySource} annotations:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;PropertySource("classpath:/com/myco/a.properties")
 * public class ConfigA { }
 *
 * &#064;Configuration
 * &#064;PropertySource("classpath:/com/myco/b.properties")
 * public class ConfigB { }
 * </pre>
 *
 * The override ordering depends on the order in which these classes are registered
 * with the application context.
 * <pre class="code">
 * AnnotationConfigApplicationContext ctx =
 *     new AnnotationConfigApplicationContext();
 * ctx.register(ConfigA.class);
 * ctx.register(ConfigB.class);
 * ctx.refresh();
 * </pre>
 *
 * In the scenario above, the properties in {@code b.properties} will override any
 * duplicates that exist in {@code a.properties}, because {@code ConfigB} was registered
 * last.
 *
 * <p>In certain situations, it may not be possible or practical to tightly control
 * property source ordering when using {@code @ProperySource} annotations. For example,
 * if the {@code @Configuration} classes above were registered via component-scanning,
 * the ordering is difficult to predict. In such cases - and if overriding is important -
 * it is recommended that the user fall back to using the programmatic PropertySource API.
 * See {@link org.springframework.core.env.ConfigurableEnvironment ConfigurableEnvironment}
 * and {@link org.springframework.core.env.MutablePropertySources MutablePropertySources}
 * javadocs for details.
 *
 * <p>
 * 注释为Spring的{@link orgspringframeworkcoreenv环境环境}添加{@link orgspringframeworkcoreenvPropertySource PropertySource}
 * 提供了一种方便的声明机制,以便与@ {@ link Configuration}类配合使用。
 * 
 *  <h3>使用示例</h3>
 * 
 *  给定一个包含键/值对{@code testbeanname = myTestBean}的文件{@code appproperties},以下{@code @Configuration}类使用{@code @PropertySource}
 * 将{@code appproperties}贡献给{@code Environment}的{@code PropertySources}。
 * 
 * <pre class="code">
 *  @Configuration @PropertySource("classpath：/ com / myco / appproperties")public class AppConfig {@Autowired Environment env;。
 * 
 * @Bean public TestBean testBean(){TestBean testBean = new TestBean(); testBeansetName(envgetProperty( "testbeanname"));返回testBean; }
 * } </pre>。
 * 
 *  请注意,{@code Environment}对象是@ {@ link orgspringframeworkbeansfactoryannotationAutowired Autowired}到配置类
 * 中,然后在填充{@code TestBean}对象时使用给定上述配置,调用{@code testBeangetName()}将返回"myTestBean"。
 * 
 *  <h3>在{@code <bean>}和{@code @Value}注解</h3>中解决$ {}占位符
 * 
 * 为了使用{@code PropertySource}中的属性解析{@code <bean>}定义或{@code @Value}注释中的$ {}占位符,必须注册一个{@code PropertySourcesPlaceholderConfigurer}
 * ,当使用{ @code <context：property-placeholder>},但使用{@code @Configuration}类时,必须使用{@code static} {@code @Bean}
 * 方法显式注册请参阅"使用外部化值"部分的@ {@ link Configuration}的javadoc和"@Bean BeanButton方法的一个注释@ @ Bean Bean的javadoc的详细
 * 信息和示例。
 * 
 *  <h3>在{@code @PropertySource}资源位置</h3>内解决$ {}占位符
 * 
 * {@code @PropertySource} {@linkplain #value()资源位置}中的任何$ {}占位符将针对已针对环境注册的一组属性源解析。例如：
 * 
 * <pre class="code">
 *  @Configuration @PropertySource("classpath：/ com / $ {myplaceholder：default / path} / appproperties")
 * public class AppConfig {@Autowired Environment env;。
 * 
 *  @Bean public TestBean testBean(){TestBean testBean = new TestBean(); testBeansetName(envgetProperty( "testbeanname"));返回testBean; }
 * } </pre>。
 * 
 * 假设"myplaceholder"存在于已经注册的其中一个属性源中,例如系统属性或环境变量,占位符将被解析为相应的值如果不是,那么将使用"default / path"作为默认值表达默认值值(由冒号"：
 * "分隔)是可选的如果未指定默认值且无法解析属性,则将抛出{@code IllegalArgumentException}。
 * 
 *  <h3>关于使用@PropertySource </h3>覆盖属性的注释
 * 
 *  在多个{@code属性}文件中存在给定的属性密钥的情况下,处理的最后一个{@code @PropertySource}注释将"赢"并覆盖
 * 
 * @author Chris Beams
 * @author Juergen Hoeller
 * @author Phillip Webb
 * @since 3.1
 * @see PropertySources
 * @see Configuration
 * @see org.springframework.core.env.PropertySource
 * @see org.springframework.core.env.ConfigurableEnvironment#getPropertySources()
 * @see org.springframework.core.env.MutablePropertySources
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(PropertySources.class)
public @interface PropertySource {

	/**
	 * Indicate the name of this property source. If omitted, a name will
	 * be generated based on the description of the underlying resource.
	 * <p>
	 * 
	 * 例如,给定两个属性文件{@code aproperties}和{@code bproperties},请考虑以下两个使用{@code @PropertySource}注释引用它们的配置类：
	 * 
	 * <pre class="code">
	 *  @Configuration @PropertySource("classpath：/ com / myco / aproperties")公共类ConfigA {}
	 * 
	 *  @Configuration @PropertySource("classpath：/ com / myco / bproperties")public class ConfigB {}
	 * </pre>
	 * 
	 *  覆盖顺序取决于这些类在应用程序上下文中的注册顺序
	 * <pre class="code">
	 *  AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(); ctxregister(Confi
	 * gAclass); ctxregister(ConfigBclass); ctxrefresh();。
	 * </pre>
	 * 
	 * 在上述情况下,{@code bproperties}中的属性将覆盖{@code aproperties}中存在的任何重复项,因为{@code ConfigB}已被注册
	 * 
	 *  <p>在某些情况下,使用{@code @ProperySource}注释时,严格控制属性源排序可能是不可行或不切实际的。
	 * 例如,如果上述{@code @Configuration}类通过组件扫描进行了注册,那么排序很难预测在这种情况下 - 如果覆盖很重要 - 建议用户回退使用编程的PropertySource API有关详
	 * 细信息,请参阅{@link orgspringframeworkcoreenvConfigurableEnvironment ConfigurableEnvironment}和{@link orgspringframeworkcoreenvMutablePropertySources MutablePropertySources}
	 * 
	 * @see org.springframework.core.env.PropertySource#getName()
	 * @see org.springframework.core.io.Resource#getDescription()
	 */
	String name() default "";

	/**
	 * Indicate the resource location(s) of the properties file to be loaded.
	 * For example, {@code "classpath:/com/myco/app.properties"} or
	 * {@code "file:/path/to/file"}.
	 * <p>Resource location wildcards (e.g. *&#42;/*.properties) are not permitted;
	 * each location must evaluate to exactly one {@code .properties} resource.
	 * <p>${...} placeholders will be resolved against any/all property sources already
	 * registered with the {@code Environment}. See {@linkplain PropertySource above}
	 * for examples.
	 * <p>Each location will be added to the enclosing {@code Environment} as its own
	 * property source, and in the order declared.
	 * <p>
	 *  javadocs。
	 *  <p>在某些情况下,使用{@code @ProperySource}注释时,严格控制属性源排序可能是不可行或不切实际的。
	 * 
	 */
	String[] value();

	/**
	 * Indicate if failure to find the a {@link #value() property resource} should be
	 * ignored.
	 * <p>{@code true} is appropriate if the properties file is completely optional.
	 * Default is {@code false}.
	 * <p>
	 * 指示此属性源的名称如果省略,将基于底层资源的描述生成名称
	 * 
	 * 
	 * @since 4.0
	 */
	boolean ignoreResourceNotFound() default false;

	/**
	 * A specific character encoding for the given resources, e.g. "UTF-8".
	 * <p>
	 *  指示要加载的属性文件的资源位置例如,{@code"classpath：/ com / myco / appproperties"}或{@code"file：/ path / to / file"} <p>
	 * 资源不允许使用位置通配符(例如** / *属性)每个位置必须评估一个{@code属性}资源<p> $ {}占位符将针对已经在{@code环境}中注册的任何/所有属性源进行解析,请参见上述{@linkplain PropertySource}
	 * 中的示例<p>每个位置将被添加到封闭的{@code环境}中作为其自己的属性源,并以已声明的顺序添加。
	 * 
	 * 
	 * @since 4.3
	 */
	String encoding() default "";

	/**
	 * Specify a custom {@link PropertySourceFactory}, if any.
	 * <p>By default, a default factory for standard resource files will be used.
	 * <p>
	 * 指示是否无法找到{@link #value()属性资源}应该被忽略<p> {@ code true}适用于属性文件是完全可选的默认是{@code false}
	 * 
	 * 
	 * @since 4.3
	 * @see org.springframework.core.io.support.DefaultPropertySourceFactory
	 * @see org.springframework.core.io.support.ResourcePropertySource
	 */
	Class<? extends PropertySourceFactory> factory() default PropertySourceFactory.class;

}
