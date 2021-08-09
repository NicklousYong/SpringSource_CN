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

package org.springframework.context.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.annotation.AliasFor;

/**
 * When used as a type-level annotation in conjunction with
 * {@link org.springframework.stereotype.Component @Component},
 * {@code @Scope} indicates the name of a scope to use for instances of
 * the annotated type.
 *
 * <p>When used as a method-level annotation in conjunction with
 * {@link Bean @Bean}, {@code @Scope} indicates the name of a scope to use
 * for the instance returned from the method.
 *
 * <p>In this context, <em>scope</em> means the lifecycle of an instance,
 * such as {@code singleton}, {@code prototype}, and so forth. Scopes
 * provided out of the box in Spring may be referred to using the
 * {@code SCOPE_*} constants available in the {@link ConfigurableBeanFactory}
 * and {@code WebApplicationContext} interfaces.
 *
 * <p>To register additional custom scopes, see
 * {@link org.springframework.beans.factory.config.CustomScopeConfigurer
 * CustomScopeConfigurer}.
 *
 * <p>
 *  当与{@link orgspringframeworkstereotypeComponent @Component}一起用作类型级注释时,{@code @Scope}指示用于注释类型实例的作用域的名称
 * 。
 * 
 * <p>当与{@link Bean @Bean}一起用作方法级注释时,{@code @Scope}表示用于从方法返回的实例的作用域的名称
 * 
 *  <p>在这种情况下,<em>范围</em>意味着一个实例的生命周期,例如{@code singleton},{@code prototype}等等。
 * 在Spring中提供的Scopes可以被引用使用{@link ConfigurableBeanFactory}和{@code WebApplicationContext}接口中可用的{@code SCOPE_ *}
 * 常量。
 *  <p>在这种情况下,<em>范围</em>意味着一个实例的生命周期,例如{@code singleton},{@code prototype}等等。
 * 
 *  <p>要注册其他自定义作用域,请参阅{@link orgspringframeworkbeansfactoryconfigCustomScopeConfigurer CustomScopeConfigurer}
 * 。
 * 
 * @author Mark Fisher
 * @author Chris Beams
 * @author Sam Brannen
 * @since 2.5
 * @see org.springframework.stereotype.Component
 * @see org.springframework.context.annotation.Bean
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Scope {

	/**
	 * Alias for {@link #scopeName}.
	 * <p>
	 * 
	 * 
	 * @see #scopeName
	 */
	@AliasFor("scopeName")
	String value() default "";

	/**
	 * Specifies the name of the scope to use for the annotated component/bean.
	 * <p>Defaults to an empty string ({@code ""}) which implies
	 * {@link ConfigurableBeanFactory#SCOPE_SINGLETON SCOPE_SINGLETON}.
	 * <p>
	 *  {@link #scopeName}的别名
	 * 
	 * 
	 * @since 4.2
	 * @see ConfigurableBeanFactory#SCOPE_PROTOTYPE
	 * @see ConfigurableBeanFactory#SCOPE_SINGLETON
	 * @see org.springframework.web.context.WebApplicationContext#SCOPE_REQUEST
	 * @see org.springframework.web.context.WebApplicationContext#SCOPE_SESSION
	 * @see #value
	 */
	@AliasFor("value")
	String scopeName() default "";

	/**
	 * Specifies whether a component should be configured as a scoped proxy
	 * and if so, whether the proxy should be interface-based or subclass-based.
	 * <p>Defaults to {@link ScopedProxyMode#DEFAULT}, which typically indicates
	 * that no scoped proxy should be created unless a different default
	 * has been configured at the component-scan instruction level.
	 * <p>Analogous to {@code <aop:scoped-proxy/>} support in Spring XML.
	 * <p>
	 * 指定要用于注释的组件/ bean的作用域的名称<p>默认为空字符串({@code""}),这意味着{@link ConfigurableBeanFactory#SCOPE_SINGLETON SCOPE_SINGLETON}
	 * 。
	 * 
	 * 
	 * @see ScopedProxyMode
	 */
	ScopedProxyMode proxyMode() default ScopedProxyMode.DEFAULT;

}
