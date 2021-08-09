/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2012 the original author or authors.
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

package org.springframework.beans.factory.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation at the field or method/constructor parameter level
 * that indicates a default value expression for the affected argument.
 *
 * <p>Typically used for expression-driven dependency injection. Also supported
 * for dynamic resolution of handler method parameters, e.g. in Spring MVC.
 *
 * <p>A common use case is to assign default field values using
 * "#{systemProperties.myProp}" style expressions.
 *
 * <p>Note that actual processing of the {@code @Value} annotation is performed
 * by a {@link org.springframework.beans.factory.config.BeanPostProcessor
 * BeanPostProcessor} which in turn means that you <em>cannot</em> use
 * {@code @Value} within
 * {@link org.springframework.beans.factory.config.BeanPostProcessor
 * BeanPostProcessor} or
 * {@link org.springframework.beans.factory.config.BeanFactoryPostProcessor BeanFactoryPostProcessor}
 * types. Please consult the javadoc for the {@link AutowiredAnnotationBeanPostProcessor}
 * class (which, by default, checks for the presence of this annotation).
 *
 * <p>
 *  在指定受影响参数的默认值表达式的字段或方法/构造函数参数级别的注释
 * 
 * 通常用于表达式驱动的依赖注入也支持动态解析处理程序方法参数,例如在Spring MVC中
 * 
 *  <p>常见的用例是使用"#{systemPropertiesmyProp}"样式表达式分配默认字段值
 * 
 * <p>请注意,{@code @Value}注释的实际处理由{@link orgspringframeworkbeansfactoryconfigBeanPostProcessor BeanPostProcessor}
 * 
 * @author Juergen Hoeller
 * @since 3.0
 * @see AutowiredAnnotationBeanPostProcessor
 * @see Autowired
 * @see org.springframework.beans.factory.config.BeanExpressionResolver
 * @see org.springframework.beans.factory.support.AutowireCandidateResolver#getSuggestedValue
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Value {

	/**
	 * The actual value expression: e.g. "#{systemProperties.myProp}".
	 * <p>
	 * 执行,这反过来意味着您不能</em>在{@code中使用{@code @Value}链接orgspringframeworkbeansfactoryconfigBeanPostProcessor Bea
	 * nPostProcessor}或{@link orgspringframeworkbeansfactoryconfigBeanFactoryPostProcessor BeanFactoryPostProcessor}
	 * 类型请参阅{@link AutowiredAnnotationBeanPostProcessor}类的javadoc(默认情况下,它检查此注释的存在)。
	 * 
	 */
	String value();

}
