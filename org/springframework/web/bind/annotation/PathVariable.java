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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

/**
 * Annotation which indicates that a method parameter should be bound to a URI template
 * variable. Supported for {@link RequestMapping} annotated handler methods in Servlet
 * environments.
 *
 * <p>If the method parameter is {@link java.util.Map Map&lt;String, String&gt;} or
 * {@link org.springframework.util.MultiValueMap MultiValueMap&lt;String, String&gt;}
 * then the map is populated with all path variable names and values.
 *
 * <p>
 *  表示方法参数应绑定到URI模板变量的注释在Servlet环境中支持{@link RequestMapping}注释处理程序方法
 * 
 * <p>如果方法参数是{@link javautilMap Map&lt; String,String&gt;}或{@link orgspringframeworkutilMultiValueMap MultiValueMap&lt; String,String&gt;}
 * ,则映射将填充所有路径变量名称和值。
 * 
 * 
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @since 3.0
 * @see RequestMapping
 * @see org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
 * @see org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PathVariable {

	/**
	 * Alias for {@link #name}.
	 * <p>
	 *  {@link #name}的别名
	 * 
	 */
	@AliasFor("name")
	String value() default "";

	/**
	 * The name of the path variable to bind to.
	 * <p>
	 *  要绑定的路径变量的名称
	 * 
	 * 
	 * @since 4.3.3
	 */
	@AliasFor("value")
	String name() default "";

	/**
	 * Whether the path variable is required.
	 * <p>Defaults to {@code true}, leading to an exception being thrown if the path
	 * variable is missing in the incoming request. Switch this to {@code false} if
	 * you prefer a {@code null} or Java 8 {@code java.util.Optional} in this case.
	 * e.g. on a {@code ModelAttribute} method which serves for different requests.
	 * <p>
	 *  路径变量是否需要<p>默认为{@code true},导致在传入请求中缺少路径变量时抛出异常如果您希望使用{@code null},将其切换为{@code false}或Java 8 {@code javautilOptional}
	 * 在这种情况下,例如对于不同请求的{@code ModelAttribute}方法。
	 * 
	 * @since 4.3.3
	 */
	boolean required() default true;

}
