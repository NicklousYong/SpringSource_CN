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

package org.springframework.web.bind.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

/**
 * Indicates the annotated class assists a "Controller".
 *
 * <p>Serves as a specialization of {@link Component @Component}, allowing for
 * implementation classes to be autodetected through classpath scanning.
 *
 * <p>It is typically used to define {@link ExceptionHandler @ExceptionHandler},
 * {@link InitBinder @InitBinder}, and {@link ModelAttribute @ModelAttribute}
 * methods that apply to all {@link RequestMapping @RequestMapping} methods.
 *
 * <p>One of {@link #annotations()}, {@link #basePackageClasses()},
 * {@link #basePackages()} or its alias {@link #value()}
 * may be specified to define specific subsets of Controllers
 * to assist. When multiple selectors are applied, OR logic is applied -
 * meaning selected Controllers should match at least one selector.
 *
 * <p>The default behavior (i.e. if used without any selector),
 * the {@code @ControllerAdvice} annotated class will
 * assist all known Controllers.
 *
 * <p>Note that those checks are done at runtime, so adding many attributes and using
 * multiple strategies may have negative impacts (complexity, performance).
 *
 * <p>
 *  表示注释类帮助"控制器"
 * 
 *  <p>作为{@link Component @Component}的专业化,允许通过类路径扫描自动检测实现类
 * 
 * <p>它通常用于定义适用于所有{@link RequestMapping @RequestMapping}方法的{@link ExceptionHandler @ExceptionHandler},{@link InitBinder @InitBinder}
 * 和{@link ModelAttribute @ModelAttribute}方法。
 * 
 *  <p>可以指定{@link #annotations()},{@link #basePackageClasses()},{@link #basePackages()}或其别名{@link #value()}
 * 之一来定义特定的子集要辅助的控制器当应用多个选择器时,应用OR逻辑 - 意思是选择控制器应至少匹配一个选择器。
 * 
 *  <p>默认行为(即如果没有任何选择器使用),{@code @ControllerAdvice}注释类将帮助所有已知的控制器
 * 
 * 请注意,这些检查在运行时完成,因此添加许多属性并使用多个策略可能会产生负面影响(复杂性,性能)
 * 
 * 
 * @author Rossen Stoyanchev
 * @author Brian Clozel
 * @author Sam Brannen
 * @since 3.2
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface ControllerAdvice {

	/**
	 * Alias for the {@link #basePackages} attribute.
	 * <p>Allows for more concise annotation declarations e.g.:
	 * {@code @ControllerAdvice("org.my.pkg")} is equivalent to
	 * {@code @ControllerAdvice(basePackages="org.my.pkg")}.
	 * <p>
	 *  {@link #basePackages}属性的别名<p>允许更简洁的注释声明,例如：{@code @ControllerAdvice("orgmypkg")}相当于{@code @ControllerAdvice(basePackages ="orgmypkg")}
	 * 。
	 * 
	 * 
	 * @since 4.0
	 * @see #basePackages()
	 */
	@AliasFor("basePackages")
	String[] value() default {};

	/**
	 * Array of base packages.
	 * <p>Controllers that belong to those base packages or sub-packages thereof
	 * will be included, e.g.: {@code @ControllerAdvice(basePackages="org.my.pkg")}
	 * or {@code @ControllerAdvice(basePackages={"org.my.pkg", "org.my.other.pkg"})}.
	 * <p>{@link #value} is an alias for this attribute, simply allowing for
	 * more concise use of the annotation.
	 * <p>Also consider using {@link #basePackageClasses()} as a type-safe
	 * alternative to String-based package names.
	 * <p>
	 * 基础包数组<p>将包含属于这些基本包或其子包的控制器,例如：{@code @ControllerAdvice(basePackages ="orgmypkg")}或{@code @ControllerAdvice(basePackages = {"orgmypkg ","orgmyotherpkg"}
	 * )} <p> {@ link #value}是此属性的别名,只是允许更简洁地使用注释<p>还要考虑使用{@link #basePackageClasses()}作为类型-safe替代基于String的包
	 * 名称。
	 * 
	 * 
	 * @since 4.0
	 */
	@AliasFor("value")
	String[] basePackages() default {};

	/**
	 * Type-safe alternative to {@link #value()} for specifying the packages
	 * to select Controllers to be assisted by the {@code @ControllerAdvice}
	 * annotated class.
	 * <p>Consider creating a special no-op marker class or interface in each package
	 * that serves no purpose other than being referenced by this attribute.
	 * <p>
	 *  用于指定要选择的软件包的{@link #value()}的类型安全替代方法{@code @ControllerAdvice}注释类<p>要协助的控制器考虑在每个包中创建一个特殊的无操作标记类或接口除了
	 * 被该属性引用之外,它不起作用。
	 * 
	 * 
	 * @since 4.0
	 */
	Class<?>[] basePackageClasses() default {};

	/**
	 * Array of classes.
	 * <p>Controllers that are assignable to at least one of the given types
	 * will be assisted by the {@code @ControllerAdvice} annotated class.
	 * <p>
	 * 类的数组<p>可以分配给至少一个给定类型的控制器将由{@code @ControllerAdvice}注释类辅助
	 * 
	 * 
	 * @since 4.0
	 */
	Class<?>[] assignableTypes() default {};

	/**
	 * Array of annotations.
	 * <p>Controllers that are annotated with this/one of those annotation(s)
	 * will be assisted by the {@code @ControllerAdvice} annotated class.
	 * <p>Consider creating a special annotation or use a predefined one,
	 * like {@link RestController @RestController}.
	 * <p>
	 *  注释数组<p>使用这个/这些注释之一注释的控制器将由{@code @ControllerAdvice}注释的类<p>辅助考虑创建一个特殊的注释或使用预定义的注释,如{@链接RestController @RestController}
	 * 。
	 * 
	 * @since 4.0
	 */
	Class<? extends Annotation>[] annotations() default {};

}
