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
import org.springframework.ui.Model;

/**
 * Annotation that binds a method parameter or method return value
 * to a named model attribute, exposed to a web view. Supported
 * for controller classes with {@link RequestMapping @RequestMapping}
 * methods.
 *
 * <p>Can be used to expose command objects to a web view, using
 * specific attribute names, through annotating corresponding
 * parameters of an {@link RequestMapping @RequestMapping} method.
 *
 * <p>Can also be used to expose reference data to a web view
 * through annotating accessor methods in a controller class with
 * {@link RequestMapping @RequestMapping} methods. Such accessor
 * methods are allowed to have any arguments that
 * {@link RequestMapping @RequestMapping} methods support, returning
 * the model attribute value to expose.
 *
 * <p>Note however that reference data and all other model content is
 * not available to web views when request processing results in an
 * {@code Exception} since the exception could be raised at any time
 * making the content of the model unreliable. For this reason
 * {@link ExceptionHandler @ExceptionHandler} methods do not provide
 * access to a {@link Model} argument.
 *
 * <p>
 *  将方法参数或方法返回值绑定到暴露于Web视图的命名模型属性的注释支持具有{@link RequestMapping @RequestMapping}方法的控制器类
 * 
 * <p>可以使用特定的属性名称将命令对象公开到一个{@link RequestMapping @RequestMapping}方法的相应参数中
 * 
 *  <p>也可以通过使用{@link RequestMapping @RequestMapping}方法通过注释控制器类中的访问器方法来将参考数据公开给Web视图。
 * 这样的访问器方法允许有任何参数,{@link RequestMapping @RequestMapping}方法支持,返回模型属性值以显示。
 * 
 * <p>请注意,当请求处理导致{@code异常}时,引用数据和所有其他模型内容不可用于Web视图,因为可以随时提出异常,从而使模型的内容不可靠因此{ @link ExceptionHandler @ExceptionHandler}
 * 方法不提供对{@link Model}参数的访问。
 * 
 * 
 * @author Juergen Hoeller
 * @author Rossen Stoyanchev
 * @since 2.5
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ModelAttribute {

	/**
	 * Alias for {@link #name}.
	 * <p>
	 *  {@link #name}的别名
	 * 
	 */
	@AliasFor("name")
	String value() default "";

	/**
	 * The name of the model attribute to bind to.
	 * <p>The default model attribute name is inferred from the declared
	 * attribute type (i.e. the method parameter type or method return type),
	 * based on the non-qualified class name:
	 * e.g. "orderAddress" for class "mypackage.OrderAddress",
	 * or "orderAddressList" for "List&lt;mypackage.OrderAddress&gt;".
	 * <p>
	 *  要绑定到<p>的模型属性的名称默认的模型属性名称是根据非限定类名称从声明的属性类型(即方法参数类型或方法返回类型)推断出来的：例如"orderAddress"类"mypackageOrderAddre
	 * ss"或"orderAddressList"用于"List&lt; mypackageOrderAddress&gt;"。
	 * 
	 * 
	 * @since 4.3
	 */
	@AliasFor("value")
	String name() default "";

	/**
	 * Allows declaring data binding disabled directly on an {@code @ModelAttribute}
	 * method parameter or on the attribute returned from an {@code @ModelAttribute}
	 * method, both of which would prevent data binding for that attribute.
	 * <p>By default this is set to {@code true} in which case data binding applies.
	 * Set this to {@code false} to disable data binding.
	 * <p>
	 * 允许声明数据绑定在{@code @ModelAttribute}方法参数或从{@code @ModelAttribute}方法返回的属性上直接禁用,这两种方法都将阻止该属性的数据绑定<p>默认情况下将其
	 * 设置为{@code true}在这种情况下数据绑定应用将此设置为{@code false}以禁用数据绑定。
	 * 
	 * @since 4.3
	 */
	boolean binding() default true;

}
