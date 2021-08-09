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

package org.springframework.context.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.jmx.export.annotation.AnnotationMBeanExporter;
import org.springframework.jmx.support.RegistrationPolicy;

/**
 * Enables default exporting of all standard {@code MBean}s from the Spring context, as
 * well as well all {@code @ManagedResource} annotated beans.
 *
 * <p>The resulting {@link org.springframework.jmx.export.MBeanExporter MBeanExporter}
 * bean is defined under the name "mbeanExporter". Alternatively, consider defining a
 * custom {@link AnnotationMBeanExporter} bean explicitly.
 *
 * <p>This annotation is modeled after and functionally equivalent to Spring XML's
 * {@code <context:mbean-export/>} element.
 *
 * <p>
 *  允许从Spring上下文中默认导出所有标准的{@code MBean},以及所有{@code @ManagedResource}注释的bean
 * 
 * <p>生成的{@link orgspringframeworkjmxexportMBeanExporter MBeanExporter} bean在名称"mbeanExporter"下定义。
 * 或者,考虑定义一个定制的{@link AnnotationMBeanExporter} bean。
 * 
 *  <p>这个注解在Spring XML的{@code <context：mbean-export />}元素之后和功能上被建模
 * 
 * 
 * @author Phillip Webb
 * @since 3.2
 * @see MBeanExportConfiguration
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(MBeanExportConfiguration.class)
public @interface EnableMBeanExport {

	/**
	 * The default domain to use when generating JMX ObjectNames.
	 * <p>
	 *  生成JMX ObjectNames时使用的默认域
	 * 
	 */
	String defaultDomain() default "";

	/**
	 * The bean name of the MBeanServer to which MBeans should be exported. Default is to
	 * use the platform's default MBeanServer.
	 * <p>
	 *  要导出MBean的MBeanServer的bean名称默认是使用平台的默认MBeanServer
	 * 
	 */
	String server() default "";

	/**
	 * The policy to use when attempting to register an MBean under an
	 * {@link javax.management.ObjectName} that already exists. Defaults to
	 * {@link RegistrationPolicy#FAIL_ON_EXISTING}.
	 * <p>
	 *  尝试在已经存在的{@link javaxmanagementObjectName}下注册MBean时使用的策略默认为{@link RegistrationPolicy#FAIL_ON_EXISTING}
	 * 。
	 */
	RegistrationPolicy registration() default RegistrationPolicy.FAIL_ON_EXISTING;
}
