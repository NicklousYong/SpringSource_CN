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

package org.springframework.web.bind.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

/**
 * A convenience annotation that is itself annotated with
 * {@link ControllerAdvice @ControllerAdvice}
 * and {@link ResponseBody @ResponseBody}.
 *
 * <p>Types that carry this annotation are treated as controller advice where
 * {@link ExceptionHandler @ExceptionHandler} methods assume
 * {@link ResponseBody @ResponseBody} semantics by default.
 *
 * <p><b>NOTE:</b> {@code @RestControllerAdvice} is processed if an appropriate
 * {@code HandlerMapping}-{@code HandlerAdapter} pair is configured such as the
 * {@code RequestMappingHandlerMapping}-{@code RequestMappingHandlerAdapter} pair
 * which are the default in the MVC Java config and the MVC namespace.
 * In particular {@code @RestControllerAdvice} is not supported with the
 * {@code DefaultAnnotationHandlerMapping}-{@code AnnotationMethodHandlerAdapter}
 * pair both of which are also deprecated.
 *
 * <p>
 *  本身使用{@link ControllerAdvice @ControllerAdvice}和{@link ResponseBody @ResponseBody}注释的方便注释
 * 
 * <p>携带此注释的类型被视为控制器建议,其中{@link ExceptionHandler @ExceptionHandler}方法默认假定为{@link ResponseBody @ResponseBody}
 * 语义。
 * 
 *  <p> <b>注意：如果配置了适当的{@code HandlerMapping}  -  {@ code HandlerAdapter}对,则{@code @RestControllerAdvice}
 * 被处理,例如{@code RequestMappingHandlerMapping}  -  {@ code RequestMappingHandlerAdapter }对,它们是MVC Java配置和
 * MVC命名空间中的默认值。
 * {@code DefaultAnnotationHandlerMapping}  -  {@ code AnnotationMethodMandhodAdapter}对特定的{@code @RestControllerAdvice}
 * 不支持这两个都不推荐使用。
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 4.3
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ControllerAdvice
@ResponseBody
public @interface RestControllerAdvice {

	/**
	 * Alias for the {@link #basePackages} attribute.
	 * <p>Allows for more concise annotation declarations e.g.:
	 * {@code @ControllerAdvice("org.my.pkg")} is equivalent to
	 * {@code @ControllerAdvice(basePackages="org.my.pkg")}.
	 * <p>
	 * {@link #basePackages}属性的别名<p>允许更简洁的注释声明,例如：{@code @ControllerAdvice("orgmypkg")}相当于{@code @ControllerAdvice(basePackages ="orgmypkg")}
	 * 。
	 * 
	 * 
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
	 *  基础包数组<p>将包含属于这些基本包或其子包的控制器,例如：{@code @ControllerAdvice(basePackages ="orgmypkg")}或{@code @ControllerAdvice(basePackages = {"orgmypkg ","orgmyotherpkg"}
	 * )} <p> {@ link #value}是此属性的别名,只是允许更简洁地使用注释<p>还要考虑使用{@link #basePackageClasses()}作为类型-safe替代基于String的包
	 * 名称。
	 * 
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
	 * 用于指定要选择的软件包的{@link #value()}的类型安全替代方法{@code @ControllerAdvice}注释类<p>要协助的控制器考虑在每个包中创建一个特殊的无操作标记类或接口除了被
	 * 该属性引用之外,它不起作用。
	 * 
	 */
	Class<?>[] basePackageClasses() default {};

	/**
	 * Array of classes.
	 * <p>Controllers that are assignable to at least one of the given types
	 * will be assisted by the {@code @ControllerAdvice} annotated class.
	 * <p>
	 *  类的数组<p>可以分配给至少一个给定类型的控制器将由{@code @ControllerAdvice}注释类辅助
	 * 
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
	 */
	Class<? extends Annotation>[] annotations() default {};

}
