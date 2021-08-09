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
 * Annotation to bind a method parameter to a session attribute.
 *
 * <p>The main motivation is to provide convenient access to existing, permanent
 * session attributes (e.g. user authentication object) with an optional/required
 * check and a cast to the target method parameter type.
 *
 * <p>For use cases that require adding or removing session attributes consider
 * injecting {@code org.springframework.web.context.request.WebRequest} or
 * {@code javax.servlet.http.HttpSession} into the controller method.
 *
 * <p>For temporary storage of model attributes in the session as part of the
 * workflow for a controller, consider using {@link SessionAttributes} instead.
 *
 * <p>
 *  将方法参数绑定到会话属性的注释
 * 
 * 主要动机是提供方便的访问现有的永久性会话属性(例如,用户认证对象)与可选/必需的检查和转换为目标方法参数类型
 * 
 *  <p>对于需要添加或删除会话属性的用例,请考虑将{@code orgspringframeworkwebcontextrequestWebRequest}或{@code javaxservlethttpHttpSession}
 * 注入到控制器方法中。
 * 
 *  <p>为了在会话中临时存储模型属性作为控制器工作流程的一部分,请考虑使用{@link SessionAttributes}代替
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 4.3
 * @see RequestMapping
 * @see SessionAttributes
 * @see RequestAttribute
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SessionAttribute {

	/**
	 * Alias for {@link #name}.
	 * <p>
	 *  {@link #name}的别名
	 * 
	 */
	@AliasFor("name")
	String value() default "";

	/**
	 * The name of the session attribute to bind to.
	 * <p>The default name is inferred from the method parameter name.
	 * <p>
	 *  要绑定到<p>的会话属性的名称从方法参数名称推断出默认名称
	 * 
	 */
	@AliasFor("value")
	String name() default "";

	/**
	 * Whether the session attribute is required.
	 * <p>Defaults to {@code true}, leading to an exception being thrown
	 * if the attribute is missing in the session or there is no session.
	 * Switch this to {@code false} if you prefer a {@code null} or Java 8
	 * {@code java.util.Optional} if the attribute doesn't exist.
	 * <p>
	 * 是否需要会话属性<p>默认为{@code true},导致在会话中缺少属性或没有会话时抛出异常如果您希望使用{@code false},将其切换为{@code false}如果属性不存在,则代码为空}或
	 * Java 8 {@code javautilOptional}。
	 */
	boolean required() default true;

}
