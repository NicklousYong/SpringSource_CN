/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2013 the original author or authors.
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

package org.springframework.core.env;

/**
 * {@link Environment} implementation suitable for use in 'standard' (i.e. non-web)
 * applications.
 *
 * <p>In addition to the usual functions of a {@link ConfigurableEnvironment} such as
 * property resolution and profile-related operations, this implementation configures two
 * default property sources, to be searched in the following order:
 * <ul>
 * <li>{@linkplain AbstractEnvironment#getSystemProperties() system properties}
 * <li>{@linkplain AbstractEnvironment#getSystemEnvironment() system environment variables}
 * </ul>
 *
 * That is, if the key "xyz" is present both in the JVM system properties as well as in
 * the set of environment variables for the current process, the value of key "xyz" from
 * system properties will return from a call to {@code environment.getProperty("xyz")}.
 * This ordering is chosen by default because system properties are per-JVM, while
 * environment variables may be the same across many JVMs on a given system.  Giving
 * system properties precedence allows for overriding of environment variables on a
 * per-JVM basis.
 *
 * <p>These default property sources may be removed, reordered, or replaced; and
 * additional property sources may be added using the {@link MutablePropertySources}
 * instance available from {@link #getPropertySources()}. See
 * {@link ConfigurableEnvironment} Javadoc for usage examples.
 *
 * <p>See {@link SystemEnvironmentPropertySource} javadoc for details on special handling
 * of property names in shell environments (e.g. Bash) that disallow period characters in
 * variable names.
 *
 * <p>
 *  {@link Environment}实现适用于"标准"(即非Web)应用程序
 * 
 * <p>除了属性解析和配置文件相关操作之类的{@link ConfigurableEnvironment}的常规功能外,此实现还将配置两个默认属性源,以下列顺序进行搜索：
 * <ul>
 *  <li> {@ linkplain AbstractEnvironment#getSystemProperties()系统属性} <li> {@ linkplain AbstractEnvironment#getSystemEnvironment()系统环境变量}
 * 。
 * </ul>
 * 
 * 也就是说,如果关键字"xyz"存在于JVM系统属性以及当前进程的一组环境变量中,则系统属性的"xyz"键的值将从调用返回到{@code environmentgetProperty("xyz")}默认情
 * 况下选择此排序,因为系统属性是每个JVM,而给定系统上的许多JVM的环境变量可能相同。
 * 允许系统属性优先级允许覆盖每个JVM上的环境变量基础。
 * 
 *  <p>这些默认属性源可能会被删除,重新排序或替换;并且可以使用{@link #getPropertySources())中提供的{@link MutablePropertySources}实例添加其他
 * 属性源。
 * 
 * @author Chris Beams
 * @since 3.1
 * @see ConfigurableEnvironment
 * @see SystemEnvironmentPropertySource
 * @see org.springframework.web.context.support.StandardServletEnvironment
 */
public class StandardEnvironment extends AbstractEnvironment {

	/** System environment property source name: {@value} */
	public static final String SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME = "systemEnvironment";

	/** JVM system properties property source name: {@value} */
	public static final String SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME = "systemProperties";


	/**
	 * Customize the set of property sources with those appropriate for any standard
	 * Java environment:
	 * <ul>
	 * <li>{@value #SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME}
	 * <li>{@value #SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME}
	 * </ul>
	 * <p>Properties present in {@value #SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME} will
	 * take precedence over those in {@value #SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME}.
	 * <p>
	 * 有关使用示例,请参阅{@link ConfigurableEnvironment} Javadoc。
	 * 
	 * <p>请参阅{@link SystemEnvironmentPropertySource} javadoc以获取有关在shell环境中特殊处理属性名称(例如Bash)的详细信息,以禁止变量名称中的句点字
	 * 符。
	 * 
	 * 
	 * @see AbstractEnvironment#customizePropertySources(MutablePropertySources)
	 * @see #getSystemProperties()
	 * @see #getSystemEnvironment()
	 */
	@Override
	protected void customizePropertySources(MutablePropertySources propertySources) {
		propertySources.addLast(new MapPropertySource(SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME, getSystemProperties()));
		propertySources.addLast(new SystemEnvironmentPropertySource(SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, getSystemEnvironment()));
	}

}
