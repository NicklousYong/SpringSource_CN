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

package org.springframework.scheduling.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that marks a method to be scheduled. Exactly one of
 * the {@link #cron()}, {@link #fixedDelay()}, or {@link #fixedRate()}
 * attributes must be specified.
 *
 * <p>The annotated method must expect no arguments. It will typically have
 * a {@code void} return type; if not, the returned value will be ignored
 * when called through the scheduler.
 *
 * <p>Processing of {@code @Scheduled} annotations is performed by
 * registering a {@link ScheduledAnnotationBeanPostProcessor}. This can be
 * done manually or, more conveniently, through the {@code <task:annotation-driven/>}
 * element or @{@link EnableScheduling} annotation.
 *
 * <p>This annotation may be used as a <em>meta-annotation</em> to create custom
 * <em>composed annotations</em> with attribute overrides.
 *
 * <p>
 *  标记要调度的方法的注释必须指定{@link #cron()},{@link #fixedDelay()}或{@link #fixedRate()}属性之一
 * 
 * <p>注释方法不能指定任何参数它通常具有{@code void}返回类型;如果没有,则通过调度程序调用时返回的值将被忽略
 * 
 *  {@code @Scheduled}注解的处理是通过注册一个{@link ScheduledAnnotationBeanPostProcessor}执行的。
 * 这可以通过{@code <task：annotation-driven />}元素或@ { @link EnableScheduling}注释。
 * 
 *  <p>此注释可以用作元标注</em>,以创建具有属性覆盖的自定义<组合注释</em>
 * 
 * 
 * @author Mark Fisher
 * @author Dave Syer
 * @author Chris Beams
 * @since 3.0
 * @see EnableScheduling
 * @see ScheduledAnnotationBeanPostProcessor
 * @see Schedules
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(Schedules.class)
public @interface Scheduled {

	/**
	 * A cron-like expression, extending the usual UN*X definition to include
	 * triggers on the second as well as minute, hour, day of month, month
	 * and day of week.  e.g. {@code "0 * * * * MON-FRI"} means once per minute on
	 * weekdays (at the top of the minute - the 0th second).
	 * <p>
	 * 一个类似cron的表达式,扩展了通常的UN * X定义,以包括第二个触发器,以及分钟,小时,月份,月份和星期几,例如{@code"0 * * * * MON-FRI"}意味着平日每分钟一次(在第一分钟的
	 * 第一秒)。
	 * 
	 * 
	 * @return an expression that can be parsed to a cron schedule
	 * @see org.springframework.scheduling.support.CronSequenceGenerator
	 */
	String cron() default "";

	/**
	 * A time zone for which the cron expression will be resolved. By default, this
	 * attribute is the empty String (i.e. the server's local time zone will be used).
	 * <p>
	 *  cron表达式将被解析的时区默认情况下,该属性为空字符串(即将使用服务器的本地时区)
	 * 
	 * 
	 * @return a zone id accepted by {@link java.util.TimeZone#getTimeZone(String)},
	 * or an empty String to indicate the server's default time zone
	 * @since 4.0
	 * @see org.springframework.scheduling.support.CronTrigger#CronTrigger(String, java.util.TimeZone)
	 * @see java.util.TimeZone
	 */
	String zone() default "";

	/**
	 * Execute the annotated method with a fixed period in milliseconds between the
	 * end of the last invocation and the start of the next.
	 * <p>
	 *  在上次调用结束和下一次调用的开始之间以毫秒为单位执行注释方法
	 * 
	 * 
	 * @return the delay in milliseconds
	 */
	long fixedDelay() default -1;

	/**
	 * Execute the annotated method with a fixed period in milliseconds between the
	 * end of the last invocation and the start of the next.
	 * <p>
	 *  在上次调用结束和下一次调用的开始之间以毫秒为单位执行注释方法
	 * 
	 * 
	 * @return the delay in milliseconds as a String value, e.g. a placeholder
	 * @since 3.2.2
	 */
	String fixedDelayString() default "";

	/**
	 * Execute the annotated method with a fixed period in milliseconds between
	 * invocations.
	 * <p>
	 *  在调用之间以毫秒为单位的固定周期执行带注释的方法
	 * 
	 * 
	 * @return the period in milliseconds
	 */
	long fixedRate() default -1;

	/**
	 * Execute the annotated method with a fixed period in milliseconds between
	 * invocations.
	 * <p>
	 * 在调用之间以毫秒为单位的固定周期执行带注释的方法
	 * 
	 * 
	 * @return the period in milliseconds as a String value, e.g. a placeholder
	 * @since 3.2.2
	 */
	String fixedRateString() default "";

	/**
	 * Number of milliseconds to delay before the first execution of a
	 * {@link #fixedRate()} or {@link #fixedDelay()} task.
	 * <p>
	 *  首次执行{@link #fixedRate()}或{@link #fixedDelay()}任务之前延迟的毫秒数
	 * 
	 * 
	 * @return the initial delay in milliseconds
	 * @since 3.2
	 */
	long initialDelay() default -1;

	/**
	 * Number of milliseconds to delay before the first execution of a
	 * {@link #fixedRate()} or {@link #fixedDelay()} task.
	 * <p>
	 *  首次执行{@link #fixedRate()}或{@link #fixedDelay()}任务之前延迟的毫秒数
	 * 
	 * @return the initial delay in milliseconds as a String value, e.g. a placeholder
	 * @since 3.2.2
	 */
	String initialDelayString() default "";

}
