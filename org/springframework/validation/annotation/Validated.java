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

package org.springframework.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Variant of JSR-303's {@link javax.validation.Valid}, supporting the
 * specification of validation groups. Designed for convenient use with
 * Spring's JSR-303 support but not JSR-303 specific.
 *
 * <p>Can be used e.g. with Spring MVC handler methods arguments.
 * Supported through {@link org.springframework.validation.SmartValidator}'s
 * validation hint concept, with validation group classes acting as hint objects.
 *
 * <p>Can also be used with method level validation, indicating that a specific
 * class is supposed to be validated at the method level (acting as a pointcut
 * for the corresponding validation interceptor), but also optionally specifying
 * the validation groups for method-level validation in the annotated class.
 * Applying this annotation at the method level allows for overriding the
 * validation groups for a specific method but does not serve as a pointcut;
 * a class-level annotation is nevertheless necessary to trigger method validation
 * for a specific bean to begin with. Can also be used as a meta-annotation on a
 * custom stereotype annotation or a custom group-specific validated annotation.
 *
 * <p>
 *  支持验证组规范的JSR-303的{@link javaxvalidationValid}的变体为了方便使用Spring的JSR-303而不是JSR-303的特定设计
 * 
 * <p>可以使用例如与Spring MVC处理程序方法参数通过{@link orgspringframeworkvalidationSmartValidator}的验证提示概念支持,验证组类作为提示对象。
 * 
 * <p>也可以与方法级验证一起使用,表明应该在方法级别(作为相应的验证拦截器的切入点)验证特定的类,还可以选择指定方法级验证的验证组在注释类中在方法级应用此注释允许覆盖特定方法的验证组,但不作为切入点;然
 * 而,为了触发特定的bean开始的方法验证,类级注释是必要的也可以用作自定义构造型注释或自定义组特定的有效注释的元注释。
 * 
 * @author Juergen Hoeller
 * @since 3.1
 * @see javax.validation.Validator#validate(Object, Class[])
 * @see org.springframework.validation.SmartValidator#validate(Object, org.springframework.validation.Errors, Object...)
 * @see org.springframework.validation.beanvalidation.SpringValidatorAdapter
 * @see org.springframework.validation.beanvalidation.MethodValidationPostProcessor
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Validated {

	/**
	 * Specify one or more validation groups to apply to the validation step
	 * kicked off by this annotation.
	 * <p>JSR-303 defines validation groups as custom annotations which an application declares
	 * for the sole purpose of using them as type-safe group arguments, as implemented in
	 * {@link org.springframework.validation.beanvalidation.SpringValidatorAdapter}.
	 * <p>Other {@link org.springframework.validation.SmartValidator} implementations may
	 * support class arguments in other ways as well.
	 * <p>
	 * 
	 */
	Class<?>[] value() default {};

}
