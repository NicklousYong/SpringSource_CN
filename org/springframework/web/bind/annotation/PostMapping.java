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
 * Annotation for mapping HTTP {@code POST} requests onto specific handler
 * methods.
 *
 * <p>Specifically, {@code @PostMapping} is a <em>composed annotation</em> that
 * acts as a shortcut for {@code @RequestMapping(method = RequestMethod.POST)}.
 *
 * <p>
 *  将HTTP {@code POST}请求映射到特定处理程序方法的注释
 * 
 * <p>具体来说,{@code @PostMapping}是一个组成的<em>注释</em>,作为{@code @RequestMapping(method = RequestMethodPOST)}的快
 * 捷方式。
 * 
 * 
 * @author Sam Brannen
 * @since 4.3
 * @see GetMapping
 * @see PutMapping
 * @see DeleteMapping
 * @see PatchMapping
 * @see RequestMapping
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RequestMapping(method = RequestMethod.POST)
public @interface PostMapping {

	/**
	 * Alias for {@link RequestMapping#name}.
	 * <p>
	 *  {@link RequestMapping#name}的别名
	 * 
	 */
	@AliasFor(annotation = RequestMapping.class)
	String name() default "";

	/**
	 * Alias for {@link RequestMapping#value}.
	 * <p>
	 *  {@link RequestMapping#value}的别名
	 * 
	 */
	@AliasFor(annotation = RequestMapping.class)
	String[] value() default {};

	/**
	 * Alias for {@link RequestMapping#path}.
	 * <p>
	 *  {@link RequestMapping#path}的别名
	 * 
	 */
	@AliasFor(annotation = RequestMapping.class)
	String[] path() default {};

	/**
	 * Alias for {@link RequestMapping#params}.
	 * <p>
	 *  {@link RequestMapping#params}的别名
	 * 
	 */
	@AliasFor(annotation = RequestMapping.class)
	String[] params() default {};

	/**
	 * Alias for {@link RequestMapping#headers}.
	 * <p>
	 *  {@link RequestMapping#headers}的别名
	 * 
	 */
	@AliasFor(annotation = RequestMapping.class)
	String[] headers() default {};

	/**
	 * Alias for {@link RequestMapping#consumes}.
	 * <p>
	 *  {@link RequestMapping#消耗}的别名
	 * 
	 */
	@AliasFor(annotation = RequestMapping.class)
	String[] consumes() default {};

	/**
	 * Alias for {@link RequestMapping#produces}.
	 * <p>
	 *  {@link RequestMapping#生成}的别名
	 */
	@AliasFor(annotation = RequestMapping.class)
	String[] produces() default {};

}
