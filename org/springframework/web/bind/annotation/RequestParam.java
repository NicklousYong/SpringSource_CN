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
import java.util.Map;

import org.springframework.core.annotation.AliasFor;

/**
 * Annotation which indicates that a method parameter should be bound to a web
 * request parameter.
 *
 * <p>Supported for annotated handler methods in Servlet and Portlet environments.
 *
 * <p>If the method parameter type is {@link Map} and a request parameter name
 * is specified, then the request parameter value is converted to a {@link Map}
 * assuming an appropriate conversion strategy is available.
 *
 * <p>If the method parameter is {@link java.util.Map Map&lt;String, String&gt;} or
 * {@link org.springframework.util.MultiValueMap MultiValueMap&lt;String, String&gt;}
 * and a parameter name is not specified, then the map parameter is populated
 * with all request parameter names and values.
 *
 * <p>
 *  表示方法参数应绑定到Web请求参数的注释
 * 
 *  <p>在Servlet和Portlet环境中支持注释处理程序方法
 * 
 * <p>如果方法参数类型为{@link Map},并且指定了请求参数名称,则假设适当的转换策略可用,请求参数值将转换为{@link Map}
 * 
 *  <p>如果方法参数是{@link javautilMap Map&lt; String,String&gt;}或{@link orgspringframeworkutilMultiValueMap MultiValueMap&lt; String,String&gt;}
 * ,并且未指定参数名称,则map参数将填充所有请求参数名称和价值观。
 * 
 * 
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 2.5
 * @see RequestMapping
 * @see RequestHeader
 * @see CookieValue
 * @see org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
 * @see org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter
 * @see org.springframework.web.portlet.mvc.annotation.AnnotationMethodHandlerAdapter
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {

	/**
	 * Alias for {@link #name}.
	 * <p>
	 *  {@link #name}的别名
	 * 
	 */
	@AliasFor("name")
	String value() default "";

	/**
	 * The name of the request parameter to bind to.
	 * <p>
	 *  要绑定的请求参数的名称
	 * 
	 * 
	 * @since 4.2
	 */
	@AliasFor("value")
	String name() default "";

	/**
	 * Whether the parameter is required.
	 * <p>Defaults to {@code true}, leading to an exception being thrown
	 * if the parameter is missing in the request. Switch this to
	 * {@code false} if you prefer a {@code null} value if the parameter is
	 * not present in the request.
	 * <p>Alternatively, provide a {@link #defaultValue}, which implicitly
	 * sets this flag to {@code false}.
	 * <p>
	 * 是否需要参数<p>默认为{@code true},导致在请求中缺少参数时抛出异常如果您希望使用{@code null}值,则将该代码切换为{@code false},如果参数不存在于请求中<p>或者,提
	 * 供一个{@link #defaultValue},它将该标志隐含地设置为{@code false}。
	 * 
	 */
	boolean required() default true;

	/**
	 * The default value to use as a fallback when the request parameter is
	 * not provided or has an empty value.
	 * <p>Supplying a default value implicitly sets {@link #required} to
	 * {@code false}.
	 * <p>
	 */
	String defaultValue() default ValueConstants.DEFAULT_NONE;

}
