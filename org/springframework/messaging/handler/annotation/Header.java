/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2014 the original author or authors.
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

package org.springframework.messaging.handler.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation which indicates that a method parameter should be bound to a message header.
 *
 * <p>
 *  指示方法参数应绑定到消息头的注释
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 4.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Header {

	/**
	 * The name of the request header to bind to.
	 * <p>
	 *  要绑定到的请求头的名称
	 * 
	 */
	String value() default "";

	/**
	 * Whether the header is required.
	 * <p>Default is {@code true}, leading to an exception if the header missing. Switch this
	 * to {@code false} if you prefer a {@code null} in case of the header missing.
	 * <p>
	 * 是否需要标题<p>默认是{@code true},导致一个异常,如果标题缺少将此切换为{@code false},如果您希望标题缺少{@code null}
	 * 
	 */
	boolean required() default true;

	/**
	 * The default value to use as a fallback. Supplying a default value implicitly
	 * sets {@link #required} to {@code false}.
	 * <p>
	 *  用作回退的默认值提供默认值隐式将{@link #required}设置为{@code false}
	 */
	String defaultValue() default ValueConstants.DEFAULT_NONE;

}
