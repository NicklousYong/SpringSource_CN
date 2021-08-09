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
 * Marks the annotated method or type as permitting cross origin requests.
 *
 * <p>By default, all origins and headers are permitted.
 *
 * <p><b>NOTE:</b> {@code @CrossOrigin} is processed if an appropriate
 * {@code HandlerMapping}-{@code HandlerAdapter} pair is configured such as the
 * {@code RequestMappingHandlerMapping}-{@code RequestMappingHandlerAdapter}
 * pair which are the default in the MVC Java config and the MVC namespace.
 * In particular {@code @CrossOrigin} is not supported with the
 * {@code DefaultAnnotationHandlerMapping}-{@code AnnotationMethodHandlerAdapter}
 * pair both of which are also deprecated.
 *
 * <p>
 *  将注释的方法或类型标记为允许跨源请求
 * 
 *  <p>默认情况下,允许所有起始和标题
 * 
 * <p> <b>注意：如果配置了适当的{@code HandlerMapping}  -  {@ code HandlerAdapter}对,则会处理</b> {@code @CrossOrigin},例
 * 如{@code RequestMappingHandlerMapping}  -  {@ code RequestMappingHandlerAdapter }对,它们是MVC Java配置和MVC命名
 * 空间中的默认值。
 * {@code DefaultAnnotationHandlerMapping}  -  {@ code AnnotationMethodMandhodAdapter}对不再支持{@code @CrossOrigin}
 * ,这两者都不推荐使用。
 * 
 * 
 * @author Russell Allen
 * @author Sebastien Deleuze
 * @author Sam Brannen
 * @since 4.2
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CrossOrigin {

	String[] DEFAULT_ORIGINS = { "*" };

	String[] DEFAULT_ALLOWED_HEADERS = { "*" };

	boolean DEFAULT_ALLOW_CREDENTIALS = true;

	long DEFAULT_MAX_AGE = 1800;


	/**
	 * Alias for {@link #origins}.
	 * <p>
	 *  {@link #origins}的别名
	 * 
	 */
	@AliasFor("origins")
	String[] value() default {};

	/**
	 * List of allowed origins, e.g. {@code "http://domain1.com"}.
	 * <p>These values are placed in the {@code Access-Control-Allow-Origin}
	 * header of both the pre-flight response and the actual response.
	 * {@code "*"} means that all origins are allowed.
	 * <p>If undefined, all origins are allowed.
	 * <p>
	 *  允许的起始列表,例如{@code"http：// domain1com"} <p>这些值被放置在飞行前响应和实际响应的{@code Access-Control-Allow-Origin}标题中@co
	 * de"*"}表示允许所有来源<p>如果未定义,则允许所有来源。
	 * 
	 * 
	 * @see #value
	 */
	@AliasFor("value")
	String[] origins() default {};

	/**
	 * List of request headers that can be used during the actual request.
	 * <p>This property controls the value of the pre-flight response's
	 * {@code Access-Control-Allow-Headers} header.
	 * {@code "*"}  means that all headers requested by the client are allowed.
	 * <p>If undefined, all requested headers are allowed.
	 * <p>
	 * 在实际请求中可以使用的请求标头列表<p>此属性控制飞行前响应的{@code访问控制允许头文件}标头{@code"*"}的值,意味着请求的所有标头允许客户端<p>如果未定义,则允许所有请求的头文件
	 * 
	 */
	String[] allowedHeaders() default {};

	/**
	 * List of response headers that the user-agent will allow the client to access.
	 * <p>This property controls the value of actual response's
	 * {@code Access-Control-Expose-Headers} header.
	 * <p>If undefined, an empty exposed header list is used.
	 * <p>
	 *  用户代理允许客户端访问的响应头列表<p>此属性控制实际响应的{@code Access-Control-Expose-Headers}标头的值<p>如果未定义,则一个空的公开头列表是用过的
	 * 
	 */
	String[] exposedHeaders() default {};

	/**
	 * List of supported HTTP request methods, e.g.
	 * {@code "{RequestMethod.GET, RequestMethod.POST}"}.
	 * <p>Methods specified here override those specified via {@code RequestMapping}.
	 * <p>If undefined, methods defined by {@link RequestMapping} annotation
	 * are used.
	 * <p>
	 *  支持的HTTP请求方法列表,例如{@code"{RequestMethodGET,RequestMethodPOST}"} <p>此处指定的方法将覆盖通过{@code RequestMapping}指
	 * 定的方法<p>如果未定义,{@link RequestMapping}注释定义的方法为用过的。
	 * 
	 */
	RequestMethod[] methods() default {};

	/**
	 * Whether the browser should include any cookies associated with the
	 * domain of the request being annotated.
	 * <p>Set to {@code "false"} if such cookies should not included.
	 * An empty string ({@code ""}) means <em>undefined</em>.
	 * {@code "true"} means that the pre-flight response will include the header
	 * {@code Access-Control-Allow-Credentials=true}.
	 * <p>If undefined, credentials are allowed.
	 * <p>
	 * 浏览器是否应该包含与要注释的请求的域相关联的任何cookie <p>如果不包含此类Cookie,则设置为{@code"false"}空字符串({@code""})表示<em>未定义</em> {@code"true"}
	 * 表示飞行前响应将包含标题{@code Access-Control-Allow-Credentials = true} <p>如果未定义,则允许凭据。
	 * 
	 */
	String allowCredentials() default "";

	/**
	 * The maximum age (in seconds) of the cache duration for pre-flight responses.
	 * <p>This property controls the value of the {@code Access-Control-Max-Age}
	 * header in the pre-flight response.
	 * <p>Setting this to a reasonable value can reduce the number of pre-flight
	 * request/response interactions required by the browser.
	 * A negative value means <em>undefined</em>.
	 * <p>If undefined, max age is set to {@code 1800} seconds (i.e., 30 minutes).
	 * <p>
	 * 飞行前响应的缓存持续时间的最大年龄(以秒为单位)<p>此属性控制飞行前响应中的{@code Access-Control-Max-Age}标头的值<p>将其设置为合理的值可以减少浏览器所需的飞行前请求/
	 * 响应交互次数负值意味着未定义</em> <p>如果未定义,最大年龄设置为{@code 1800}秒(即, 30分钟)。
	 */
	long maxAge() default -1;

}
