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
 * Annotation which indicates that a method parameter should be bound to a web request header.
 *
 * <p>Supported for annotated handler methods in Servlet and Portlet environments.
 *
 * <p>If the method parameter is {@link java.util.Map Map&lt;String, String&gt;},
 * {@link org.springframework.util.MultiValueMap MultiValueMap&lt;String, String&gt;},
 * or {@link org.springframework.http.HttpHeaders HttpHeaders} then the map is
 * populated with all header names and values.
 *
 * <p>
 *  表示方法参数应绑定到Web请求头的注释
 * 
 *  <p>在Servlet和Portlet环境中支持注释处理程序方法
 * 
 * <p>如果方法参数是{@link javautilMap Map&lt; String,String&gt;},{@link orgspringframeworkutilMultiValueMap MultiValueMap&lt; String,String&gt;}
 * 或{@link orgspringframeworkhttpHttpHeaders HttpHeaders},则映射将填充所有头名称和值。
 * 
 * 
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 3.0
 * @see RequestMapping
 * @see RequestParam
 * @see CookieValue
 * @see org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
 * @see org.springframework.web.portlet.mvc.annotation.AnnotationMethodHandlerAdapter
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestHeader {

	/**
	 * Alias for {@link #name}.
	 * <p>
	 *  {@link #name}的别名
	 * 
	 */
	@AliasFor("name")
	String value() default "";

	/**
	 * The name of the request header to bind to.
	 * <p>
	 *  要绑定到的请求头的名称
	 * 
	 * 
	 * @since 4.2
	 */
	@AliasFor("value")
	String name() default "";

	/**
	 * Whether the header is required.
	 * <p>Defaults to {@code true}, leading to an exception being thrown
	 * if the header is missing in the request. Switch this to
	 * {@code false} if you prefer a {@code null} value if the header is
	 * not present in the request.
	 * <p>Alternatively, provide a {@link #defaultValue}, which implicitly
	 * sets this flag to {@code false}.
	 * <p>
	 *  是否需要标题<p>默认为{@code true},如果请求中缺少标题,则会导致异常将其转换为{@code false},如果您喜欢{@code null}值,则头文件不存在于请求中。
	 * 或者,提供一个{@link #defaultValue},它将该标志隐含地设置为{@code false}。
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
