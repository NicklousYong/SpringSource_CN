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

import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.core.annotation.AliasFor;
import org.springframework.core.type.filter.TypeFilter;

/**
 * Configures component scanning directives for use with @{@link Configuration} classes.
 * Provides support parallel with Spring XML's {@code <context:component-scan>} element.
 *
 * <p>Either {@link #basePackageClasses} or {@link #basePackages} (or its alias
 * {@link #value}) may be specified to define specific packages to scan. If specific
 * packages are not defined, scanning will occur from the package of the
 * class that declares this annotation.
 *
 * <p>Note that the {@code <context:component-scan>} element has an
 * {@code annotation-config} attribute; however, this annotation does not. This is because
 * in almost all cases when using {@code @ComponentScan}, default annotation config
 * processing (e.g. processing {@code @Autowired} and friends) is assumed. Furthermore,
 * when using {@link AnnotationConfigApplicationContext}, annotation config processors are
 * always registered, meaning that any attempt to disable them at the
 * {@code @ComponentScan} level would be ignored.
 *
 * <p>See {@link Configuration @Configuration}'s Javadoc for usage examples.
 *
 * <p>
 *  配置用于@ {@ link Configuration}的组件扫描指令与Spring XML的{@code <context：component-scan>}元素并行提供支持
 * 
 * <p>可以指定{@link #basePackageClasses}或{@link #basePackages}(或其别名{@link #value})定义要扫描的特定软件包如果未定义特定软件包,则将从
 * 软件包中进行扫描的类声明这个注释。
 * 
 *  <p>请注意,{@code <context：component-scan>}元素具有{@code annotation-config}属性;但是,这个注释不是这样的,因为在使用{@code @ComponentScan}
 * 的几乎所有情况下,都假定默认注释配置处理(例如处理{@code @Autowired}和朋友))此外,当使用{@link AnnotationConfigApplicationContext}时,注释配
 * 置处理器始终被注册,这意味着任何在{@code @ComponentScan}级别禁用它们的尝试都将被忽略。
 * 
 * <p>有关使用示例,请参阅{@link Configuration @Configuration}的Javadoc
 * 
 * 
 * @author Chris Beams
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 3.1
 * @see Configuration
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Repeatable(ComponentScans.class)
public @interface ComponentScan {

	/**
	 * Alias for {@link #basePackages}.
	 * <p>Allows for more concise annotation declarations if no other attributes
	 * are needed &mdash; for example, {@code @ComponentScan("org.my.pkg")}
	 * instead of {@code @ComponentScan(basePackages = "org.my.pkg")}.
	 * <p>
	 *  {@link #basePackages}的别名<p>如果不需要其他属性,允许更简洁的注释声明&mdash;例如{@code @ComponentScan("orgmypkg")},而不是{@code @ComponentScan(basePackages ="orgmypkg")}
	 * 。
	 * 
	 */
	@AliasFor("basePackages")
	String[] value() default {};

	/**
	 * Base packages to scan for annotated components.
	 * <p>{@link #value} is an alias for (and mutually exclusive with) this
	 * attribute.
	 * <p>Use {@link #basePackageClasses} for a type-safe alternative to
	 * String-based package names.
	 * <p>
	 *  用于扫描注释组件的基本包<p> {@ link #value}是此属性的(并且互斥)的别名<p>使用{@link #basePackageClasses}作为基于字符串的包名称的类型安全替代
	 * 
	 */
	@AliasFor("value")
	String[] basePackages() default {};

	/**
	 * Type-safe alternative to {@link #basePackages} for specifying the packages
	 * to scan for annotated components. The package of each class specified will be scanned.
	 * <p>Consider creating a special no-op marker class or interface in each package
	 * that serves no purpose other than being referenced by this attribute.
	 * <p>
	 * 用于指定要扫描注释组件的软件包的{@link #basePackages}的类型安全替代方法将扫描指定的每个类的软件包<p>请考虑在不用于任何目的的每个软件包中创建一个特殊的no-op标记类或接口除了被
	 * 该属性引用。
	 * 
	 */
	Class<?>[] basePackageClasses() default {};

	/**
	 * The {@link BeanNameGenerator} class to be used for naming detected components
	 * within the Spring container.
	 * <p>The default value of the {@link BeanNameGenerator} interface itself indicates
	 * that the scanner used to process this {@code @ComponentScan} annotation should
	 * use its inherited bean name generator, e.g. the default
	 * {@link AnnotationBeanNameGenerator} or any custom instance supplied to the
	 * application context at bootstrap time.
	 * <p>
	 *  用于在Spring容器中命名检测到的组件的{@link BeanNameGenerator}类<p> {@link BeanNameGenerator}界面的默认值表示用于处理此{@code @ComponentScan}
	 * 注释的扫描程序应该使用其继承的bean名称生成器,例如默认的{@link AnnotationBeanNameGenerator}或在引导时间提供给应用程序上下文的任何自定义实例。
	 * 
	 * 
	 * @see AnnotationConfigApplicationContext#setBeanNameGenerator(BeanNameGenerator)
	 */
	Class<? extends BeanNameGenerator> nameGenerator() default BeanNameGenerator.class;

	/**
	 * The {@link ScopeMetadataResolver} to be used for resolving the scope of detected components.
	 * <p>
	 * 用于解析检测到的组件范围的{@link ScopeMetadataResolver}
	 * 
	 */
	Class<? extends ScopeMetadataResolver> scopeResolver() default AnnotationScopeMetadataResolver.class;

	/**
	 * Indicates whether proxies should be generated for detected components, which may be
	 * necessary when using scopes in a proxy-style fashion.
	 * <p>The default is defer to the default behavior of the component scanner used to
	 * execute the actual scan.
	 * <p>Note that setting this attribute overrides any value set for {@link #scopeResolver}.
	 * <p>
	 *  指示是否应为检测到的组件生成代理,这在以代理方式使用作用域时可能是必需的。
	 * <p>默认值取决于用于执行实际扫描的组件扫描器的默认行为<p>请注意,设置此属性将覆盖为{@link #scopeResolver}设置的任何值。
	 * 
	 * 
	 * @see ClassPathBeanDefinitionScanner#setScopedProxyMode(ScopedProxyMode)
	 */
	ScopedProxyMode scopedProxy() default ScopedProxyMode.DEFAULT;

	/**
	 * Controls the class files eligible for component detection.
	 * <p>Consider use of {@link #includeFilters} and {@link #excludeFilters}
	 * for a more flexible approach.
	 * <p>
	 *  控制适合组件检测的类文件<p>考虑使用{@link #includeFilters}和{@link #excludeFilters}来实现更灵活的方法
	 * 
	 */
	String resourcePattern() default ClassPathScanningCandidateComponentProvider.DEFAULT_RESOURCE_PATTERN;

	/**
	 * Indicates whether automatic detection of classes annotated with {@code @Component}
	 * {@code @Repository}, {@code @Service}, or {@code @Controller} should be enabled.
	 * <p>
	 *  指示是否应启用使用{@code @Component} {@code @Repository},{@code @Service}或{@code @Controller}注释的类的自动检测
	 * 
	 */
	boolean useDefaultFilters() default true;

	/**
	 * Specifies which types are eligible for component scanning.
	 * <p>Further narrows the set of candidate components from everything in {@link #basePackages}
	 * to everything in the base packages that matches the given filter or filters.
	 * <p>Note that these filters will be applied in addition to the default filters, if specified.
	 * Any type under the specified base packages which matches a given filter will be included,
	 * even if it does not match the default filters (i.e. is not annotated with {@code @Component}).
	 * <p>
	 * 指定哪些类型有资格进行组件扫描<p>进一步将{@link #basePackages}中的所有候选组件集合到基本包中与给定过滤器或过滤器匹配的所有内容<p>请注意,这些过滤器将被应用除了默认过滤器之外,
	 * 如果指定了与指定的过滤器匹配的指定的基本包下的任何类型,即使它与默认过滤器不匹配(即不使用{@code @Component}注释)也将被包含)。
	 * 
	 * 
	 * @see #resourcePattern()
	 * @see #useDefaultFilters()
	 */
	Filter[] includeFilters() default {};

	/**
	 * Specifies which types are not eligible for component scanning.
	 * <p>
	 *  指定哪些类型不符合组件扫描的资格
	 * 
	 * 
	 * @see #resourcePattern
	 */
	Filter[] excludeFilters() default {};

	/**
	 * Specify whether scanned beans should be registered for lazy initialization.
	 * <p>Default is {@code false}; switch this to {@code true} when desired.
	 * <p>
	 *  指定扫描的bean是否应该被注册进行延迟初始化<p>默认值为{@code false};如果需要,将其切换为{@code true}
	 * 
	 * 
	 * @since 4.1
	 */
	boolean lazyInit() default false;


	/**
	 * Declares the type filter to be used as an {@linkplain ComponentScan#includeFilters
	 * include filter} or {@linkplain ComponentScan#excludeFilters exclude filter}.
	 * <p>
	 * 声明类型过滤器用作{@linkplain ComponentScan#includeFilters include filter}或{@linkplain ComponentScan#excludeFilters exclude filter}
	 * 。
	 * 
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({})
	@interface Filter {

		/**
		 * The type of filter to use.
		 * <p>Default is {@link FilterType#ANNOTATION}.
		 * <p>
		 *  使用过滤器的类型<p>默认值为{@link FilterType#ANNOTATION}
		 * 
		 * 
		 * @see #classes
		 * @see #pattern
		 */
		FilterType type() default FilterType.ANNOTATION;

		/**
		 * Alias for {@link #classes}.
		 * <p>
		 *  别名为{@link #classes}
		 * 
		 * 
		 * @see #classes
		 */
		@AliasFor("classes")
		Class<?>[] value() default {};

		/**
		 * The class or classes to use as the filter.
		 * <p>The following table explains how the classes will be interpreted
		 * based on the configured value of the {@link #type} attribute.
		 * <table border="1">
		 * <tr><th>{@code FilterType}</th><th>Class Interpreted As</th></tr>
		 * <tr><td>{@link FilterType#ANNOTATION ANNOTATION}</td>
		 * <td>the annotation itself</td></tr>
		 * <tr><td>{@link FilterType#ASSIGNABLE_TYPE ASSIGNABLE_TYPE}</td>
		 * <td>the type that detected components should be assignable to</td></tr>
		 * <tr><td>{@link FilterType#CUSTOM CUSTOM}</td>
		 * <td>an implementation of {@link TypeFilter}</td></tr>
		 * </table>
		 * <p>When multiple classes are specified, <em>OR</em> logic is applied
		 * &mdash; for example, "include types annotated with {@code @Foo} OR {@code @Bar}".
		 * <p>Custom {@link TypeFilter TypeFilters} may optionally implement any of the
		 * following {@link org.springframework.beans.factory.Aware Aware} interfaces, and
		 * their respective methods will be called prior to {@link TypeFilter#match match}:
		 * <ul>
		 * <li>{@link org.springframework.context.EnvironmentAware EnvironmentAware}</li>
		 * <li>{@link org.springframework.beans.factory.BeanFactoryAware BeanFactoryAware}
		 * <li>{@link org.springframework.beans.factory.BeanClassLoaderAware BeanClassLoaderAware}
		 * <li>{@link org.springframework.context.ResourceLoaderAware ResourceLoaderAware}
		 * </ul>
		 * <p>Specifying zero classes is permitted but will have no effect on component
		 * scanning.
		 * <p>
		 *  用作过滤器的类或类<p>下表说明了如何根据{@link #type}属性的配置值来解释类
		 * <table border="1">
		 * </t> </t> </tr> <tr> <td> {@ code FilterType} </th> <th>类型解释为</th> </tr> <tr> <td> {@ link FilterType#ANNOTATION ANNOTATION}
		 *  </td>注释本身</td> </tr> <tr> <td> {@ link FilterType#ASSIGNABLE_TYPE ASSIGNABLE_TYPE} </td> <td>检测到组件的类
		 * 型应该分配给</td> </tr> < tr> <td> {@ link FilterType#CUSTOM CUSTOM} </td> <td> {@link TypeFilter}的实现</td> 
		 * </tr>。
		 * </table>
		 *  <p>当指定了多个类时,逻辑应用于&lt; em /例如,"包含使用{@code @Foo} OR {@code @Bar}"注释的类型"自定义{@link TypeFilter TypeFilters}
		 * 可以可选地实现以下任何{@link orgspringframeworkbeansfactoryAware Aware}接口及其各自的方法将在{@link TypeFilter#match match}
		 * 之前调用：。
		 * <ul>
		 * 
		 * @since 4.2
		 * @see #value
		 * @see #type
		 */
		@AliasFor("value")
		Class<?>[] classes() default {};

		/**
		 * The pattern (or patterns) to use for the filter, as an alternative
		 * to specifying a Class {@link #value}.
		 * <p>If {@link #type} is set to {@link FilterType#ASPECTJ ASPECTJ},
		 * this is an AspectJ type pattern expression. If {@link #type} is
		 * set to {@link FilterType#REGEX REGEX}, this is a regex pattern
		 * for the fully-qualified class names to match.
		 * <p>
		 * <li> {@ link orgspringframeworkcontextEnvironmentAware EnvironmentAware} </li> <li> {@ link orgspringframeworkbeansfactoryBeanFactoryAware BeanFactoryAware}
		 *  <li> {@ link orgspringframeworkbeansfactoryBeanClassLoaderAware BeanClassLoaderAware} <li> {@ link orgspringframeworkcontextResourceLoaderAware ResourceLoaderAware}
		 * 。
		 * </ul>
		 *  <p>允许指定零类,但对组件扫描没有影响
		 * 
		 * @see #type
		 * @see #classes
		 */
		String[] pattern() default {};

	}

}
