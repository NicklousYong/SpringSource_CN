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
import org.springframework.http.HttpStatus;

/**
 * Marks a method or exception class with the status {@link #code} and
 * {@link #reason} that should be returned.
 *
 * <p>The status code is applied to the HTTP response when the handler
 * method is invoked and overrides status information set by other means,
 * like {@code ResponseEntity} or {@code "redirect:"}.
 *
 * <p><strong>Warning</strong>: when using this annotation on an exception
 * class, or when setting the {@code reason} attribute of this annotation,
 * the {@code HttpServletResponse.sendError} method will be used.
 *
 * <p>With {@code HttpServletResponse.sendError}, the response is considered
 * complete and should not be written to any further. Furthermore, the Servlet
 * container will typically write an HTML error page therefore making the
 * use of a {@code reason} unsuitable for REST APIs. For such cases it is
 * preferable to use a {@link org.springframework.http.ResponseEntity} as
 * a return type and avoid the use of {@code @ResponseStatus} altogether.
 *
 * <p>Note that a controller class may also be annotated with
 * {@code @ResponseStatus} and is then inherited by all {@code @RequestMapping}
 * methods.
 *
 * <p>
 *  标记应该返回状态为{@link #code}和{@link #reason}的方法或异常类
 * 
 * <p>当调用处理程序方法时,状态代码将应用于HTTP响应,并覆盖通过其他方式设置的状态信息,如{@code ResponseEntity}或{@code"redirect："}
 * 
 *  <p> <strong>警告</strong>：当在异常类上使用此注释时,或者在设置此注释的{@code reason}属性时,将使用{@code HttpServletResponsesendError}
 * 方法。
 * 
 * <p>使用{@code HttpServletResponsesendError},响应被认为是完整的,不应该进一步写入。
 * 此外,Servlet容器通常会写入一个HTML错误页面,因此使用{@code reason}不适用于REST API对于这种情况,最好使用{@link orgspringframeworkhttpResponseEntity}
 * 作为返回类型,并避免使用{@code @ResponseStatus}。
 * <p>使用{@code HttpServletResponsesendError},响应被认为是完整的,不应该进一步写入。
 * 
 * 
 * @author Arjen Poutsma
 * @author Sam Brannen
 * @see org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver
 * @see javax.servlet.http.HttpServletResponse#sendError(int, String)
 * @since 3.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseStatus {

	/**
	 * Alias for {@link #code}.
	 * <p>
	 *  <p>请注意,控制器类也可以用{@code @ResponseStatus}注释,然后被所有{@code @RequestMapping}方法继承
	 * 
	 */
	@AliasFor("code")
	HttpStatus value() default HttpStatus.INTERNAL_SERVER_ERROR;

	/**
	 * The status <em>code</em> to use for the response.
	 * <p>Default is {@link HttpStatus#INTERNAL_SERVER_ERROR}, which should
	 * typically be changed to something more appropriate.
	 * <p>
	 *  别名为{@link #code}
	 * 
	 * 
	 * @since 4.2
	 * @see javax.servlet.http.HttpServletResponse#setStatus(int)
	 * @see javax.servlet.http.HttpServletResponse#sendError(int)
	 */
	@AliasFor("value")
	HttpStatus code() default HttpStatus.INTERNAL_SERVER_ERROR;

	/**
	 * The <em>reason</em> to be used for the response.
	 * <p>
	 *  用于响应的状态<em>代码</em> <p>默认值是{@link HttpStatus#INTERNAL_SERVER_ERROR},通常应该更改为更合适的
	 * 
	 * 
	 * @see javax.servlet.http.HttpServletResponse#sendError(int, String)
	 */
	String reason() default "";

}
