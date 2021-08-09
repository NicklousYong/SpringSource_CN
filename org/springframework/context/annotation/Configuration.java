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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Indicates that a class declares one or more {@link Bean @Bean} methods and
 * may be processed by the Spring container to generate bean definitions and
 * service requests for those beans at runtime, for example:
 *
 * <pre class="code">
 * &#064;Configuration
 * public class AppConfig {
 *
 *     &#064;Bean
 *     public MyBean myBean() {
 *         // instantiate, configure and return bean ...
 *     }
 * }</pre>
 *
 * <h2>Bootstrapping {@code @Configuration} classes</h2>
 *
 * <h3>Via {@code AnnotationConfigApplicationContext}</h3>
 *
 * {@code @Configuration} classes are typically bootstrapped using either
 * {@link AnnotationConfigApplicationContext} or its web-capable variant,
 * {@link org.springframework.web.context.support.AnnotationConfigWebApplicationContext
 * AnnotationConfigWebApplicationContext}. A simple example with the former follows:
 *
 * <pre class="code">
 * AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
 * ctx.register(AppConfig.class);
 * ctx.refresh();
 * MyBean myBean = ctx.getBean(MyBean.class);
 * // use myBean ...
 * </pre>
 *
 * See {@link AnnotationConfigApplicationContext} Javadoc for further details and see
 * {@link org.springframework.web.context.support.AnnotationConfigWebApplicationContext
 * AnnotationConfigWebApplicationContext} for {@code web.xml} configuration instructions.
 *
 * <h3>Via Spring {@code <beans>} XML</h3>
 *
 * <p>As an alternative to registering {@code @Configuration} classes directly against an
 * {@code AnnotationConfigApplicationContext}, {@code @Configuration} classes may be
 * declared as normal {@code <bean>} definitions within Spring XML files:
 * <pre class="code">
 * {@code
 * <beans>
 *    <context:annotation-config/>
 *    <bean class="com.acme.AppConfig"/>
 * </beans>}</pre>
 *
 * In the example above, {@code <context:annotation-config/>} is required in order to
 * enable {@link ConfigurationClassPostProcessor} and other annotation-related
 * post processors that facilitate handling {@code @Configuration} classes.
 *
 * <h3>Via component scanning</h3>
 *
 * <p>{@code @Configuration} is meta-annotated with {@link Component @Component}, therefore
 * {@code @Configuration} classes are candidates for component scanning (typically using
 * Spring XML's {@code <context:component-scan/>} element) and therefore may also take
 * advantage of {@link Autowired @Autowired}/{@link javax.inject.Inject @Inject}
 * like any regular {@code @Component}. In particular, if a single constructor is present
 * autowiring semantics will be applied transparently:
 *
 * <pre class="code">
 * &#064;Configuration
 * public class AppConfig {
 *     private final SomeBean someBean;
 *
 *     public AppConfig(SomeBean someBean) {
 *         this.someBean = someBean;
 *     }
 *
 *     // &#064;Bean definition using "SomeBean"
 *
 * }</pre>
 *
 * <p>{@code @Configuration} classes may not only be bootstrapped using
 * component scanning, but may also themselves <em>configure</em> component scanning using
 * the {@link ComponentScan @ComponentScan} annotation:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;ComponentScan("com.acme.app.services")
 * public class AppConfig {
 *     // various &#064;Bean definitions ...
 * }</pre>
 *
 * See the {@link ComponentScan @ComponentScan} javadoc for details.
 *
 * <h2>Working with externalized values</h2>
 *
 * <h3>Using the {@code Environment} API</h3>
 *
 * Externalized values may be looked up by injecting the Spring
 * {@link org.springframework.core.env.Environment} into a {@code @Configuration}
 * class the usual (e.g. using the {@code @Autowired} annotation):
 *
 * <pre class="code">
 * &#064;Configuration
 * public class AppConfig {
 *
 *     &#064Autowired Environment env;
 *
 *     &#064;Bean
 *     public MyBean myBean() {
 *         MyBean myBean = new MyBean();
 *         myBean.setName(env.getProperty("bean.name"));
 *         return myBean;
 *     }
 * }</pre>
 *
 * Properties resolved through the {@code Environment} reside in one or more "property
 * source" objects, and {@code @Configuration} classes may contribute property sources to
 * the {@code Environment} object using
 * the {@link org.springframework.core.env.PropertySources @PropertySources} annotation:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;PropertySource("classpath:/com/acme/app.properties")
 * public class AppConfig {
 *
 *     &#064Inject Environment env;
 *
 *     &#064;Bean
 *     public MyBean myBean() {
 *         return new MyBean(env.getProperty("bean.name"));
 *     }
 * }</pre>
 *
 * See {@link org.springframework.core.env.Environment Environment}
 * and {@link PropertySource @PropertySource} Javadoc for further details.
 *
 * <h3>Using the {@code @Value} annotation</h3>
 *
 * Externalized values may be 'wired into' {@code @Configuration} classes using
 * the {@link Value @Value} annotation:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;PropertySource("classpath:/com/acme/app.properties")
 * public class AppConfig {
 *
 *     &#064Value("${bean.name}") String beanName;
 *
 *     &#064;Bean
 *     public MyBean myBean() {
 *         return new MyBean(beanName);
 *     }
 * }</pre>
 *
 * This approach is most useful when using Spring's
 * {@link org.springframework.context.support.PropertySourcesPlaceholderConfigurer
 * PropertySourcesPlaceholderConfigurer}, usually enabled via XML with
 * {@code <context:property-placeholder/>}.  See the section below on composing
 * {@code @Configuration} classes with Spring XML using {@code @ImportResource},
 * see {@link Value @Value} Javadoc, and see {@link Bean @Bean} Javadoc for details on working with
 * {@code BeanFactoryPostProcessor} types such as
 * {@code PropertySourcesPlaceholderConfigurer}.
 *
 * <h2>Composing {@code @Configuration} classes</h2>
 *
 * <h3>With the {@code @Import} annotation</h3>
 *
 * <p>{@code @Configuration} classes may be composed using the {@link Import @Import} annotation,
 * not unlike the way that {@code <import>} works in Spring XML. Because
 * {@code @Configuration} objects are managed as Spring beans within the container,
 * imported configurations may be injected the usual way (e.g. via constructor injection):
 *
 * <pre class="code">
 * &#064;Configuration
 * public class DatabaseConfig {
 *
 *     &#064;Bean
 *     public DataSource dataSource() {
 *         // instantiate, configure and return DataSource
 *     }
 * }
 *
 * &#064;Configuration
 * &#064;Import(DatabaseConfig.class)
 * public class AppConfig {
 *
 *     private final DatabaseConfig dataConfig;
 *
 *     public AppConfig(DatabaseConfig dataConfig) {
 *         this.dataConfig = dataConfig;
 *     }
 *
 *     &#064;Bean
 *     public MyBean myBean() {
 *         // reference the dataSource() bean method
 *         return new MyBean(dataConfig.dataSource());
 *     }
 * }</pre>
 *
 * Now both {@code AppConfig} and the imported {@code DatabaseConfig} can be bootstrapped
 * by registering only {@code AppConfig} against the Spring context:
 *
 * <pre class="code">
 * new AnnotationConfigApplicationContext(AppConfig.class);</pre>
 *
 * <h3>With the {@code @Profile} annotation</h3>
 *
 * {@code @Configuration} classes may be marked with the {@link Profile @Profile} annotation to
 * indicate they should be processed only if a given profile or profiles are <em>active</em>:
 *
 * <pre class="code">
 * &#064;Profile("embedded")
 * &#064;Configuration
 * public class EmbeddedDatabaseConfig {
 *
 *     &#064;Bean
 *     public DataSource dataSource() {
 *         // instantiate, configure and return embedded DataSource
 *     }
 * }
 *
 * &#064;Profile("production")
 * &#064;Configuration
 * public class ProductionDatabaseConfig {
 *
 *     &#064;Bean
 *     public DataSource dataSource() {
 *         // instantiate, configure and return production DataSource
 *     }
 * }</pre>
 *
 * See the {@link Profile @Profile} and {@link org.springframework.core.env.Environment}
 * javadocs for further details.
 *
 * <h3>With Spring XML using the {@code @ImportResource} annotation</h3>
 *
 * As mentioned above, {@code @Configuration} classes may be declared as regular Spring
 * {@code <bean>} definitions within Spring XML files. It is also possible to
 * import Spring XML configuration files into {@code @Configuration} classes using
 * the {@link ImportResource @ImportResource} annotation. Bean definitions imported from
 * XML can be injected the usual way (e.g. using the {@code Inject} annotation):
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;ImportResource("classpath:/com/acme/database-config.xml")
 * public class AppConfig {
 *
 *     &#064Inject DataSource dataSource; // from XML
 *
 *     &#064;Bean
 *     public MyBean myBean() {
 *         // inject the XML-defined dataSource bean
 *         return new MyBean(this.dataSource);
 *     }
 * }</pre>
 *
 * <h3>With nested {@code @Configuration} classes</h3>
 *
 * {@code @Configuration} classes may be nested within one another as follows:
 *
 * <pre class="code">
 * &#064;Configuration
 * public class AppConfig {
 *
 *     &#064;Inject DataSource dataSource;
 *
 *     &#064;Bean
 *     public MyBean myBean() {
 *         return new MyBean(dataSource);
 *     }
 *
 *     &#064;Configuration
 *     static class DatabaseConfig {
 *         &#064;Bean
 *         DataSource dataSource() {
 *             return new EmbeddedDatabaseBuilder().build();
 *         }
 *     }
 * }</pre>
 *
 * When bootstrapping such an arrangement, only {@code AppConfig} need be registered
 * against the application context. By virtue of being a nested {@code @Configuration}
 * class, {@code DatabaseConfig} <em>will be registered automatically</em>. This avoids
 * the need to use an {@code @Import} annotation when the relationship between
 * {@code AppConfig} {@code DatabaseConfig} is already implicitly clear.
 *
 * <p>Note also that nested {@code @Configuration} classes can be used to good effect
 * with the {@code @Profile} annotation to provide two options of the same bean to the
 * enclosing {@code @Configuration} class.
 *
 * <h2>Configuring lazy initialization</h2>
 *
 * <p>By default, {@code @Bean} methods will be <em>eagerly instantiated</em> at container
 * bootstrap time.  To avoid this, {@code @Configuration} may be used in conjunction with
 * the {@link Lazy @Lazy} annotation to indicate that all {@code @Bean} methods declared within
 * the class are by default lazily initialized. Note that {@code @Lazy} may be used on
 * individual {@code @Bean} methods as well.
 *
 * <h2>Testing support for {@code @Configuration} classes</h2>
 *
 * The Spring <em>TestContext framework</em> available in the {@code spring-test} module
 * provides the {@code @ContextConfiguration} annotation, which as of Spring 3.1 can
 * accept an array of {@code @Configuration} {@code Class} objects:
 *
 * <pre class="code">
 * &#064;RunWith(SpringJUnit4ClassRunner.class)
 * &#064;ContextConfiguration(classes={AppConfig.class, DatabaseConfig.class})
 * public class MyTests {
 *
 *     &#064;Autowired MyBean myBean;
 *
 *     &#064;Autowired DataSource dataSource;
 *
 *     &#064;Test
 *     public void test() {
 *         // assertions against myBean ...
 *     }
 * }</pre>
 *
 * See TestContext framework reference documentation for details.
 *
 * <h2>Enabling built-in Spring features using {@code @Enable} annotations</h2>
 *
 * Spring features such as asynchronous method execution, scheduled task execution,
 * annotation driven transaction management, and even Spring MVC can be enabled and
 * configured from {@code @Configuration}
 * classes using their respective "{@code @Enable}" annotations. See
 * {@link org.springframework.scheduling.annotation.EnableAsync @EnableAsync},
 * {@link org.springframework.scheduling.annotation.EnableScheduling @EnableScheduling},
 * {@link org.springframework.transaction.annotation.EnableTransactionManagement @EnableTransactionManagement},
 * {@link org.springframework.context.annotation.EnableAspectJAutoProxy @EnableAspectJAutoProxy},
 * and {@link org.springframework.web.servlet.config.annotation.EnableWebMvc @EnableWebMvc}
 * for details.
 *
 * <h2>Constraints when authoring {@code @Configuration} classes</h2>
 *
 * <ul>
 * <li>&#064;Configuration classes must be non-final
 * <li>&#064;Configuration classes must be non-local (may not be declared within a method)
 * <li>Any nested configuration classes must be {@code static}.
 * </ul>
 *
 * <p>
 *  表示一个类声明一个或多个{@link Bean @Bean}方法,并且可以由Spring容器处理以在运行时为这些bean生成bean定义和服务请求,例如：
 * 
 * <pre class="code">
 * @Configuration public class AppConfig {
 * 
 *  @Bean public MyBean myBean(){//实例化,配置和返回bean}} </pre>
 * 
 *  <h2>引导{@code @Configuration}类</h2>
 * 
 *  <h3>通过{@code AnnotationConfigApplicationContext} </h3>
 * 
 *  {@code @Configuration}类通常使用{@link AnnotationConfigApplicationContext}或其Web功能变体{@link orgspringframeworkwebcontextsupportAnnotationConfigWebApplicationContext AnnotationConfigWebApplicationContext}
 * 进行引导,一个简单的例子如下：。
 * 
 * <pre class="code">
 *  AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(); ctxregister(AppCo
 * nfigclass); ctxrefresh(); MyBean myBean = ctxgetBean(MyBeanclass); //使用myBean。
 * </pre>
 * 
 * 有关详细信息,请参阅{@link AnnotationConfigApplicationContext} Javadoc,并参阅{@code webxml}配置说明的{@link orgspringframeworkwebcontextsupportAnnotationConfigWebApplicationContext AnnotationConfigWebApplicationContext}
 * 。
 * 
 *  <h3>通过Spring {@code <beans>} XML </h3>
 * 
 *  <p>作为直接针对{@code AnnotationConfigApplicationContext}注册{@code @Configuration}类的替代方法,{@code @Configuration}
 * 类可以在Spring XML文件中声明为正常的{@code <bean>}定义：。
 * <pre class="code">
 *  {@码
 * <beans>
 * <context:annotation-config/>
 * <bean class="com.acme.AppConfig"/>
 *  </豆>} </PRE>
 * 
 *  在上面的示例中,需要{@code <context：annotation-config />}才能启用{@link ConfigurationClassPostProcessor}和其他注释相关的后处
 * 理器,以便于处理{@code @Configuration}类。
 * 
 * <h3>通过组件扫描</h3>
 * 
 *  <p> {@ code @Configuration}使用{@link Component @Component}进行元注释,因此{@code @Configuration}类是组件扫描的候选项(通常
 * 使用Spring XML的{@code <context：component-scan / >}元素),因此也可以像任何常规的{@code @Component}一样利用{@link Autowired @Autowired}
 *  / {@ link javaxinjectInject @Inject}的优势。
 * 特别是,如果一个构造函数出现,那么自动连线语义将被透明地应用：。
 * 
 * <pre class="code">
 *  @Configuration public class AppConfig {private final SomeBean someBean;
 * 
 *  public AppConfig(SomeBean someBean){thissomeBean = someBean; }
 * 
 *  // @Bean定义使用"SomeBean"
 * 
 *  } </PRE>
 * 
 * <p> {@ code @Configuration}类不仅可以使用组件扫描进行引导,还可以使用{@link ComponentScan @ComponentScan}注释自己配置</em>组件扫描：。
 * 
 * <pre class="code">
 *  @Configuration @ComponentScan("comacmeappservices")public class AppConfig {//各种@Bean定义} </pre>
 * 
 *  有关详细信息,请参阅{@link ComponentScan @ComponentScan} javadoc
 * 
 *  <h2>使用外部化值</h2>
 * 
 *  <h3>使用{@code Environment} API </h3>
 * 
 *  可以通过将Spring {@link orgspringframeworkcoreenvEnvironment}注入到{@code @Configuration}类中(例如使用{@code @Autowired}
 * 注释)来查找外部化值)：。
 * 
 * <pre class="code">
 *  @Configuration public class AppConfig {
 * 
 *  &#064环境环境
 * 
 * @Bean public MyBean myBean(){MyBean myBean = new MyBean(); myBeansetName(envgetProperty( "beanname"));返回myBean; }
 * } </pre>。
 * 
 *  通过{@code Environment}解析的属性驻留在一个或多个"属性源"对象中,{@code @Configuration}类可以使用{@link orgspringframeworkcoreenvPropertySources @PropertySources}
 * 注释向{@code Environment}对象提供属性源：。
 * 
 * <pre class="code">
 *  @Configuration @PropertySource("classpath：/ com / acme / appproperties")public class AppConfig {
 * 
 *  &#064注入环境env;
 * 
 *  @Bean public MyBean myBean(){return new MyBean(envgetProperty("beanname")); }} </pre>
 * 
 * 有关详细信息,请参阅{@link orgspringframeworkcoreenvEnvironment Environment}和{@link PropertySource @PropertySource}
 *  Javadoc。
 * 
 *  <h3>使用{@code @Value}注释</h3>
 * 
 *  外部值可以使用{@link Value @Value}注释"连接到"{@code @Configuration}类中：
 * 
 * <pre class="code">
 *  @Configuration @PropertySource("classpath：/ com / acme / appproperties")public class AppConfig {
 * 
 *  &#064Value("$ {beanname}")String beanName;
 * 
 *  @Bean public MyBean myBean(){return new MyBean(beanName); }} </pre>
 * 
 * 使用Spring的{@link orgspringframeworkcontextsupportPropertySourcesPlaceholderConfigurer PropertySourcesPlaceholderConfigurer}
 * ,通常通过XML启用{@code <context：property-placeholder />},这种方法是非常有用的。
 * 请参阅下面关于使用Spring XML编写{@code @Configuration}类的部分{ @code @ImportResource},请参阅{@link Value @Value} Javad
 * oc,并参阅{@link Bean @Bean} Javadoc了解使用{@code BeanFactoryPostProcessor}类型的详细信息,例如{@code PropertySourcesPlaceholderConfigurer}
 * 。
 * 
 *  <h2>组合{@code @Configuration}类</h2>
 * 
 *  使用{@code @Import}注释</h3>
 * 
 * 可以使用{@link Import @Import}注释来组合{@code @Configuration}类,与{@code <import>}在Spring XML中的工作方式不同{{@code @Configuration}
 * 对象被管理作为容器内的Spring bean,导入的配置可以以通常的方式注入(例如通过构造器注入)：。
 * 
 * 
 * @author Rod Johnson
 * @author Chris Beams
 * @since 3.0
 * @see Bean
 * @see Profile
 * @see Import
 * @see ImportResource
 * @see ComponentScan
 * @see Lazy
 * @see PropertySource
 * @see AnnotationConfigApplicationContext
 * @see ConfigurationClassPostProcessor
 * @see org.springframework.core.env.Environment
 * @see org.springframework.test.context.ContextConfiguration
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Configuration {

	/**
	 * Explicitly specify the name of the Spring bean definition associated
	 * with this Configuration class.  If left unspecified (the common case),
	 * a bean name will be automatically generated.
	 * <p>The custom name applies only if the Configuration class is picked up via
	 * component scanning or supplied directly to a {@link AnnotationConfigApplicationContext}.
	 * If the Configuration class is registered as a traditional XML bean definition,
	 * the name/id of the bean element will take precedence.
	 * <p>
	 * <pre class="code">
	 *  @Configuration public class DatabaseConfig {
	 * 
	 *  @Bean public DataSource dataSource(){//实例化,配置和返回DataSource}}
	 * 
	 *  @Configuration @Import(DatabaseConfigclass)public class AppConfig {
	 * 
	 *  private final DatabaseConfig dataConfig;
	 * 
	 *  public AppConfig(DatabaseConfig dataConfig){thisdataConfig = dataConfig; }
	 * 
	 * @Bean public MyBean myBean(){//引用dataSource()bean方法返回新的MyBean(dataConfigdataSource()); }} </pre>
	 * 
	 *  现在,只有{@code AppConfig}可以通过仅针对Spring上下文来注册{@code AppConfig}和导入的{@code DatabaseConfig}：
	 * 
	 * <pre class="code">
	 *  新的AnnotationConfigApplicationContext(AppConfigclass); </pre>
	 * 
	 *  使用{@code @Profile}注释</h3>
	 * 
	 *  {@code @Configuration}类可能会被标记为{@link Profile @Profile}注释,以表示只有在给定的配置文件或配置文件<em>活动时才应处理它们。</em>：
	 * 
	 * <pre class="code">
	 *  @Profile("embedded")@Configuration public class EmbeddedDatabaseConfig {
	 * 
	 *  @Bean public DataSource dataSource(){//实例化,配置和返回嵌入式DataSource}}
	 * 
	 * @Profile("production")@Configuration public class ProductionDatabaseConfig {
	 * 
	 *  @Bean public DataSource dataSource(){//实例化,配置和返回生产DataSource}} </pre>
	 * 
	 *  有关详细信息,请参阅{@link Profile @Profile}和{@link orgspringframeworkcoreenvEnvironment} javadocs
	 * 
	 *  使用Spring XML使用{@code @ImportResource}注释</h3>
	 * 
	 *  如上所述,在Spring XML文件中,{@code @Configuration}类可以被定义为常规的Spring {@code <bean>}定义。
	 * 还可以将Spring XML配置文件导入到{@code @Configuration}类中,使用{ @link ImportResource @ImportResource}注释从XML导入的Bean定
	 * 义可以以通常的方式注入(例如使用{@code Inject}注释)：。
	 *  如上所述,在Spring XML文件中,{@code @Configuration}类可以被定义为常规的Spring {@code <bean>}定义。
	 * 
	 * <pre class="code">
	 * @Configuration @ImportResource("classpath：/ com / acme / database-configxml")public class AppConfig {。
	 * 
	 *  &#064Inject DataSource dataSource; //从XML
	 * 
	 *  @Bean public MyBean myBean(){//注入XML定义的dataSource bean返回新的MyBean(thisdataSource); }} </pre>
	 * 
	 *  使用嵌套的{@code @Configuration}类</h3>
	 * 
	 *  {@code @Configuration}类可以彼此嵌套,如下所示：
	 * 
	 * <pre class="code">
	 *  @Configuration public class AppConfig {
	 * 
	 *  @Inject DataSource dataSource;
	 * 
	 * @return the specified bean name, if any
	 * @see org.springframework.beans.factory.support.DefaultBeanNameGenerator
	 */
	String value() default "";

}
