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

import org.springframework.stereotype.Controller;

/**
 * A convenience annotation that is itself annotated with
 * {@link Controller @Controller} and {@link ResponseBody @ResponseBody}.
 * <p>
 * Types that carry this annotation are treated as controllers where
 * {@link RequestMapping @RequestMapping} methods assume
 * {@link ResponseBody @ResponseBody} semantics by default.
 *
 * <p><b>NOTE:</b> {@code @RestController} is processed if an appropriate
 * {@code HandlerMapping}-{@code HandlerAdapter} pair is configured such as the
 * {@code RequestMappingHandlerMapping}-{@code RequestMappingHandlerAdapter}
 * pair which are the default in the MVC Java config and the MVC namespace.
 * In particular {@code @RestController} is not supported with the
 * {@code DefaultAnnotationHandlerMapping}-{@code AnnotationMethodHandlerAdapter}
 * pair both of which are also deprecated.
 *
 * <p>
 *  一个方便的注释本身用{@link Controller @Controller}和{@link ResponseBody @ResponseBody}注释
 * <p>
 * 携带此注释的类型被视为控制器,其中{@link RequestMapping @RequestMapping}方法默认假定为{@link ResponseBody @ResponseBody}语义
 * 
 *  注意：如果配置了适当的{@code HandlerMapping}  -  {@ code HandlerAdapter}对,则会处理</b> {@code @RestController},例如{@code RequestMappingHandlerMapping}
 *   -  {@ code RequestMappingHandlerAdapter }对,它们是MVC Java配置和MVC命名空间中的默认值。
 * 
 * @author Rossen Stoyanchev
 * @author Sam Brannen
 * @since 4.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Controller
@ResponseBody
public @interface RestController {

	/**
	 * The value may indicate a suggestion for a logical component name,
	 * to be turned into a Spring bean in case of an autodetected component.
	 * <p>
	 * {@code DefaultAnnotationHandlerMapping}  -  {@ code AnnotationMethodMandhodAdapter}对不再支持{@code @RestController}
	 * ,这两者都不推荐使用。
	 * 
	 * 
	 * @return the suggested component name, if any
	 * @since 4.0.1
	 */
	String value() default "";

}
