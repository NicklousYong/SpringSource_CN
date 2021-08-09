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
 * Annotation which indicates that a method parameter should be bound to an HTTP cookie.
 *
 * <p>Supported for annotated handler methods in Servlet and Portlet environments.
 *
 * <p>The method parameter may be declared as type {@link javax.servlet.http.Cookie}
 * or as cookie value type (String, int, etc.).
 *
 * <p>
 *  表示方法参数应绑定到HTTP cookie的注释
 * 
 *  <p>在Servlet和Portlet环境中支持注释处理程序方法
 * 
 * <p>方法参数可以声明为类型{@link javaxservlethttpCookie}或作为cookie值类型(String,int等)
 * 
 * 
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 3.0
 * @see RequestMapping
 * @see RequestParam
 * @see RequestHeader
 * @see org.springframework.web.bind.annotation.RequestMapping
 * @see org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
 * @see org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter
 * @see org.springframework.web.portlet.mvc.annotation.AnnotationMethodHandlerAdapter
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CookieValue {

	/**
	 * Alias for {@link #name}.
	 * <p>
	 *  {@link #name}的别名
	 * 
	 */
	@AliasFor("name")
	String value() default "";

	/**
	 * The name of the cookie to bind to.
	 * <p>
	 *  要绑定到的cookie的名称
	 * 
	 * 
	 * @since 4.2
	 */
	@AliasFor("value")
	String name() default "";

	/**
	 * Whether the cookie is required.
	 * <p>Defaults to {@code true}, leading to an exception being thrown
	 * if the cookie is missing in the request. Switch this to
	 * {@code false} if you prefer a {@code null} value if the cookie is
	 * not present in the request.
	 * <p>Alternatively, provide a {@link #defaultValue}, which implicitly
	 * sets this flag to {@code false}.
	 * <p>
	 *  是否需要cookie <p>默认为{@code true},导致在请求中缺少cookie时抛出异常如果您希望使用{@code null}值,则将其转换为{@code false},如果请求中不存在co
	 * okie <p>或者,提供一个{@link #defaultValue},它将该标志隐含地设置为{@code false}。
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
