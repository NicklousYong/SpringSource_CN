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

package org.springframework.context.event;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.ApplicationEvent;
import org.springframework.core.annotation.AliasFor;

/**
 * Annotation that marks a method as a listener for application events.
 *
 * <p>If an annotated method supports a single event type, the method may
 * declare a single parameter that reflects the event type to listen to.
 * If an annotated method supports multiple event types, this annotation
 * may refer to one or more supported event types using the {@code classes}
 * attribute. See the {@link #classes} javadoc for further details.
 *
 * <p>Events can be {@link ApplicationEvent} instances as well as arbitrary
 * objects.
 *
 * <p>Processing of {@code @EventListener} annotations is performed via
 * the internal {@link EventListenerMethodProcessor} bean which gets
 * registered automatically when using Java config or manually via the
 * {@code <context:annotation-config/>} or {@code <context:component-scan/>}
 * element when using XML config.
 *
 * <p>Annotated methods may have a non-{@code void} return type. When they
 * do, the result of the method invocation is sent as a new event. If the
 * return type is either an array or a collection, each element is sent
 * as a new individual event.
 *
 * <p>It is also possible to define the order in which listeners for a
 * certain event are to be invoked. To do so, add Spring's common
 * {@link org.springframework.core.annotation.Order @Order} annotation
 * alongside this event listener annotation.
 *
 * <p>While it is possible for an event listener to declare that it
 * throws arbitrary exception types, any checked exceptions thrown
 * from an event listener will be wrapped in an
 * {@link java.lang.reflect.UndeclaredThrowableException}
 * since the event publisher can only handle runtime exceptions.
 *
 * <p>
 *  将方法标记为应用程序事件的侦听器的注释
 * 
 * <p>如果注释的方法支持单个事件类型,则该方法可以声明反映要侦听的事件类型的单个参数。
 * 如果注释方法支持多个事件类型,则此注释可以引用一个或多个支持的事件类型, {@code classes} attribute有关详细信息,请参阅{@link #classes} javadoc。
 * 
 *  事件可以是{@link ApplicationEvent}实例以及任意对象
 * 
 *  {@code @EventListener}注释的处理通过内部{@link EventListenerMethodProcessor} bean执行,该内部使用Java配置时自动注册,或通过{@code <context：annotation-config />}
 * 或{@使用XML配置时的代码<context：component-scan />}元素。
 * 
 * 注释方法可能具有非{@ code void}返回类型当它们这样做时,方法调用的结果将作为新事件发送如果返回类型是数组或集合,则每个元素都将作为一个新的个人活动
 * 
 *  <p>还可以定义要调用某个事件的侦听器的顺序为此,请在此事件侦听器注释旁添加Spring的常见{@link orgspringframeworkcoreannotationOrder @Order}注
 * 释。
 * 
 * @author Stephane Nicoll
 * @since 4.2
 * @see EventListenerMethodProcessor
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EventListener {

	/**
	 * Alias for {@link #classes}.
	 * <p>
	 * 
	 *  <p>尽管事件侦听器可以声明它抛出任意的异常类型,但事件侦听器抛出的任何检查的异常都将被包装在{@link javalangreflectUndeclaredThrowableException}中,
	 * 因为事件发布者只能处理运行时异常。
	 * 
	 */
	@AliasFor("classes")
	Class<?>[] value() default {};

	/**
	 * The event classes that this listener handles.
	 * <p>If this attribute is specified with a single value, the
	 * annotated method may optionally accept a single parameter.
	 * However, if this attribute is specified with multiple values,
	 * the annotated method must <em>not</em> declare any parameters.
	 * <p>
	 * 别名为{@link #classes}
	 * 
	 */
	@AliasFor("value")
	Class<?>[] classes() default {};

	/**
	 * Spring Expression Language (SpEL) attribute used for making the
	 * event handling conditional.
	 * <p>Default is "", meaning the event is always handled.
	 * <p>
	 *  该侦听器处理的事件类<p>如果使用单个值指定此属性,则带注释的方法可以可选地接受单个参数。但是,如果此属性使用多个值指定,则注释方法必须<em>不< em>声明任何参数
	 * 
	 */
	String condition() default "";

}
