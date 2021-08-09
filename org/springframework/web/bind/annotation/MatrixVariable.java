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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

/**
 * Annotation which indicates that a method parameter should be bound to a
 * name-value pair within a path segment. Supported for {@link RequestMapping}
 * annotated handler methods in Servlet environments.
 *
 * <p>If the method parameter type is {@link java.util.Map} and a matrix variable
 * name is specified, then the matrix variable value is converted to a
 * {@link java.util.Map} assuming an appropriate conversion strategy is available.
 *
 * <p>If the method parameter is {@link java.util.Map Map&lt;String, String&gt;} or
 * {@link org.springframework.util.MultiValueMap MultiValueMap&lt;String, String&gt;}
 * and a variable name is not specified, then the map is populated with all
 * matrix variable names and values.
 *
 * <p>
 *  指示方法参数应绑定到路径段中的名称 - 值对的注释在Servlet环境中支持{@link RequestMapping}注释处理程序方法
 * 
 * <p>如果方法参数类型为{@link javautilMap},并且指定了一个矩阵变量名称,则假设适用的转换策略可用,矩阵变量值将转换为{@link javautilMap}
 * 
 *  <p>如果方法参数是{@link javautilMap Map&lt; String,String&gt;}或{@link orgspringframeworkutilMultiValueMap MultiValueMap&lt; String,String&gt;}
 * ,并且未指定变量名称,则映射将填充所有矩阵变量名称,值。
 * 
 * 
 * @author Rossen Stoyanchev
 * @author Sam Brannen
 * @since 3.2
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MatrixVariable {

	/**
	 * Alias for {@link #name}.
	 * <p>
	 *  {@link #name}的别名
	 * 
	 */
	@AliasFor("name")
	String value() default "";

	/**
	 * The name of the matrix variable.
	 * <p>
	 *  矩阵变量的名称
	 * 
	 * 
	 * @since 4.2
	 * @see #value
	 */
	@AliasFor("value")
	String name() default "";

	/**
	 * The name of the URI path variable where the matrix variable is located,
	 * if necessary for disambiguation (e.g. a matrix variable with the same
	 * name present in more than one path segment).
	 * <p>
	 *  如果需要消除歧义,则矩阵变量所在的URI路径变量的名称(例如,具有相同名称的矩阵变量存在于多个路径段中)
	 * 
	 */
	String pathVar() default ValueConstants.DEFAULT_NONE;

	/**
	 * Whether the matrix variable is required.
	 * <p>Default is {@code true}, leading to an exception being thrown in
	 * case the variable is missing in the request. Switch this to {@code false}
	 * if you prefer a {@code null} if the variable is missing.
	 * <p>Alternatively, provide a {@link #defaultValue}, which implicitly sets
	 * this flag to {@code false}.
	 * <p>
	 * 是否需要矩阵变量<p>默认值为{@code true},导致抛出异常,以防在请求中丢失变量将其转换为{@code false},如果您喜欢{@code null} if该变量缺少<p>或者,提供一个{@link #defaultValue}
	 * ,它将该标志隐含地设置为{@code false}。
	 * 
	 */
	boolean required() default true;

	/**
	 * The default value to use as a fallback.
	 * <p>Supplying a default value implicitly sets {@link #required} to
	 * {@code false}.
	 * <p>
	 */
	String defaultValue() default ValueConstants.DEFAULT_NONE;

}
