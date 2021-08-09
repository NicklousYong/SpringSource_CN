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

package org.springframework.core.env;

import java.util.Map;

/**
 * Configuration interface to be implemented by most if not all {@link Environment} types.
 * Provides facilities for setting active and default profiles and manipulating underlying
 * property sources. Allows clients to set and validate required properties, customize the
 * conversion service and more through the {@link ConfigurablePropertyResolver}
 * superinterface.
 *
 * <h2>Manipulating property sources</h2>
 * <p>Property sources may be removed, reordered, or replaced; and additional
 * property sources may be added using the {@link MutablePropertySources}
 * instance returned from {@link #getPropertySources()}. The following examples
 * are against the {@link StandardEnvironment} implementation of
 * {@code ConfigurableEnvironment}, but are generally applicable to any implementation,
 * though particular default property sources may differ.
 *
 * <h4>Example: adding a new property source with highest search priority</h4>
 * <pre class="code">
 *   ConfigurableEnvironment environment = new StandardEnvironment();
 *   MutablePropertySources propertySources = environment.getPropertySources();
 *   Map<String, String> myMap = new HashMap<String, String>();
 *   myMap.put("xyz", "myValue");
 *   propertySources.addFirst(new MapPropertySource("MY_MAP", myMap));
 * </pre>
 *
 * <h4>Example: removing the default system properties property source</h4>
 * <pre class="code">
 *   MutablePropertySources propertySources = environment.getPropertySources();
 *   propertySources.remove(StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME)
 * </pre>
 *
 * <h4>Example: mocking the system environment for testing purposes</h4>
 * <pre class="code">
 *   MutablePropertySources propertySources = environment.getPropertySources();
 *   MockPropertySource mockEnvVars = new MockPropertySource().withProperty("xyz", "myValue");
 *   propertySources.replace(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, mockEnvVars);
 * </pre>
 *
 * When an {@link Environment} is being used by an {@code ApplicationContext}, it is
 * important that any such {@code PropertySource} manipulations be performed
 * <em>before</em> the context's {@link
 * org.springframework.context.support.AbstractApplicationContext#refresh() refresh()}
 * method is called. This ensures that all property sources are available during the
 * container bootstrap process, including use by {@linkplain
 * org.springframework.context.support.PropertySourcesPlaceholderConfigurer property
 * placeholder configurers}.
 *
 *
 * <p>
 * 配置界面由大多数(即使不是全部){@link Environment}类型实现提供用于设置活动和默认配置文件和操作底层属性源的功能允许客户端设置和验证所需的属性,通过{@link ConfigurablePropertyResolver定制转换服务等等}
 * 超级接口。
 * 
 * <h2>操纵资源来源</h2> <p>可能会移除,重新排序或替换资源来源;并且可以使用从{@link #getPropertySources())返回的{@link MutablePropertySources}
 * 实例添加其他属性源。
 * 以下示例针对{@code ConfigurableEnvironment}的{@link StandardEnvironment}实现,但通常适用于任何实现,虽然特定的默认属性源可能不同。
 * 
 *  <h4>示例：添加具有最高搜索优先级的新属性源</h4>
 * <pre class="code">
 * ConfigurableEnvironment environment = new StandardEnvironment(); MutablePropertySources propertySourc
 * es = environmentgetPropertySources(); Map <String,String> myMap = new HashMap <String,String>(); myMa
 * pput("xyz","myValue"); propertySourcesaddFirst(new MapPropertySource("MY_MAP",myMap));。
 * </pre>
 * 
 *  <h4>示例：删除默认的系统属性属性源</h4>
 * <pre class="code">
 *  MutablePropertySources propertySources = environmentgetPropertySources(); propertySourcesremove(Stan
 * dardEnvironmentSYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME)。
 * </pre>
 * 
 *  <h4>示例：嘲笑系统环境进行测试目的</h4>
 * <pre class="code">
 * MutablePropertySources propertySources = environmentgetPropertySources(); MockPropertySource mockEnvV
 * ars = new MockPropertySource()withProperty("xyz","myValue"); propertySourcesreplace(StandardEnvironme
 * ntSYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,mockEnvVars);。
 * </pre>
 * 
 *  当{@code ApplicationContext}正在使用{@link Environment}时,在上下文的{@link orgspringframeworkcontextsupportAbstractApplicationContext#refresh()之前执行任何这样的{@code PropertySource}
 * 操作很重要, refresh()}方法被调用这确保所有属性源在容器引导过程中可用,包括使用{@linkplain orgspringframeworkcontextsupportPropertySourcesPlaceholderConfigurer属性占位符配置}
 * 。
 * 
 * 
 * @author Chris Beams
 * @since 3.1
 * @see StandardEnvironment
 * @see org.springframework.context.ConfigurableApplicationContext#getEnvironment
 */
public interface ConfigurableEnvironment extends Environment, ConfigurablePropertyResolver {

	/**
	 * Specify the set of profiles active for this {@code Environment}. Profiles are
	 * evaluated during container bootstrap to determine whether bean definitions
	 * should be registered with the container.
	 * <p>Any existing active profiles will be replaced with the given arguments; call
	 * with zero arguments to clear the current set of active profiles. Use
	 * {@link #addActiveProfile} to add a profile while preserving the existing set.
	 * <p>
	 * 指定此{@code Environment}活动的配置文件集合在容器引导期间评估配置文件,以确定是否应将bean定义注册到容器中。
	 * <p>任何现有的活动配置文件将被替换为给定的参数;调用零参数以清除当前活动配置文件集使用{@link #addActiveProfile}添加配置文件同时保留现有集。
	 * 
	 * 
	 * @see #addActiveProfile
	 * @see #setDefaultProfiles
	 * @see org.springframework.context.annotation.Profile
	 * @see AbstractEnvironment#ACTIVE_PROFILES_PROPERTY_NAME
	 * @throws IllegalArgumentException if any profile is null, empty or whitespace-only
	 */
	void setActiveProfiles(String... profiles);

	/**
	 * Add a profile to the current set of active profiles.
	 * <p>
	 *  将配置文件添加到当前活动配置文件集中
	 * 
	 * 
	 * @see #setActiveProfiles
	 * @throws IllegalArgumentException if the profile is null, empty or whitespace-only
	 */
	void addActiveProfile(String profile);

	/**
	 * Specify the set of profiles to be made active by default if no other profiles
	 * are explicitly made active through {@link #setActiveProfiles}.
	 * <p>
	 *  如果没有其他配置文件通过{@link #setActiveProfiles}显式生效,则默认指定要设置为活动的配置文件集
	 * 
	 * 
	 * @see AbstractEnvironment#DEFAULT_PROFILES_PROPERTY_NAME
	 * @throws IllegalArgumentException if any profile is null, empty or whitespace-only
	 */
	void setDefaultProfiles(String... profiles);

	/**
	 * Return the {@link PropertySources} for this {@code Environment} in mutable form,
	 * allowing for manipulation of the set of {@link PropertySource} objects that should
	 * be searched when resolving properties against this {@code Environment} object.
	 * The various {@link MutablePropertySources} methods such as
	 * {@link MutablePropertySources#addFirst addFirst},
	 * {@link MutablePropertySources#addFirst addLast},
	 * {@link MutablePropertySources#addFirst addBefore} and
	 * {@link MutablePropertySources#addFirst addAfter} allow for fine-grained control
	 * over property source ordering. This is useful, for example, in ensuring that
	 * certain user-defined property sources have search precedence over default property
	 * sources such as the set of system properties or the set of system environment
	 * variables.
	 * <p>
	 * 以可变形式返回此{@code环境}的{@link PropertySources},允许在对此{@code Environment}对象解析属性时操纵应该搜索的{@link PropertySource}
	 * 对象集合各种{@链接MutablePropertySources}方法,例如{@link MutablePropertySources#addFirst addFirst},{@link MutablePropertySources#addFirst addLast}
	 * ,{@link MutablePropertySources#addFirst addBefore}和{@link MutablePropertySources#addFirst addAfter}允许
	 * 对属性源进行细粒度控制排序这是有用的,例如,确保某些用户定义的属性源的搜索优先于默认属性源,例如系统属性集或系统环境变量集。
	 * 
	 * 
	 * @see AbstractEnvironment#customizePropertySources
	 */
	MutablePropertySources getPropertySources();

	/**
	 * Return the value of {@link System#getenv()} if allowed by the current
	 * {@link SecurityManager}, otherwise return a map implementation that will attempt
	 * to access individual keys using calls to {@link System#getenv(String)}.
	 * <p>Note that most {@link Environment} implementations will include this system
	 * environment map as a default {@link PropertySource} to be searched. Therefore, it
	 * is recommended that this method not be used directly unless bypassing other
	 * property sources is expressly intended.
	 * <p>Calls to {@link Map#get(Object)} on the Map returned will never throw
	 * {@link IllegalAccessException}; in cases where the SecurityManager forbids access
	 * to a property, {@code null} will be returned and an INFO-level log message will be
	 * issued noting the exception.
	 * <p>
	 * 如果当前的{@link SecurityManager}允许,返回{@link System#getenv()}的值,否则返回一个映射实现,它将尝试使用对{@link System#getenv(String)}
	 *  < p>请注意,大多数{@link Environment}实现将包括此系统环境映射作为默认{@link PropertySource}进行搜索因此,建议不要直接使用此方法,除非明示意图绕过其他属性源
	 * <p >在返回的Map上调用{@link Map#get(Object)}将不会抛出{@link IllegalAccessException};在SecurityManager禁止访问属性的情况下,将
	 * 返回{@code null},并将发出INFO级别的日志消息,指出异常。
	 * 
	 */
	Map<String, Object> getSystemEnvironment();

	/**
	 * Return the value of {@link System#getProperties()} if allowed by the current
	 * {@link SecurityManager}, otherwise return a map implementation that will attempt
	 * to access individual keys using calls to {@link System#getProperty(String)}.
	 * <p>Note that most {@code Environment} implementations will include this system
	 * properties map as a default {@link PropertySource} to be searched. Therefore, it is
	 * recommended that this method not be used directly unless bypassing other property
	 * sources is expressly intended.
	 * <p>Calls to {@link Map#get(Object)} on the Map returned will never throw
	 * {@link IllegalAccessException}; in cases where the SecurityManager forbids access
	 * to a property, {@code null} will be returned and an INFO-level log message will be
	 * issued noting the exception.
	 * <p>
	 * 如果当前的{@link SecurityManager}允许,返回{@link System#getProperties()}的值,否则返回一个映射实现,它将尝试使用对{@link System#getProperty(String)}
	 *  < p>请注意,大多数{@code环境}实现将包括此系统属性映射作为默认{@link PropertySource}进行搜索因此,建议不要直接使用此方法,除非明示意图绕过其他属性源<p >在返回的Ma
	 * p上调用{@link Map#get(Object)}将不会抛出{@link IllegalAccessException};在SecurityManager禁止访问属性的情况下,将返回{@code null}
	 * ,并将发出INFO级别的日志消息,指出异常。
	 * 
	 */
	Map<String, Object> getSystemProperties();

	/**
	 * Append the given parent environment's active profiles, default profiles and
	 * property sources to this (child) environment's respective collections of each.
	 * <p>For any identically-named {@code PropertySource} instance existing in both
	 * parent and child, the child instance is to be preserved and the parent instance
	 * discarded. This has the effect of allowing overriding of property sources by the
	 * child as well as avoiding redundant searches through common property source types,
	 * e.g. system environment and system properties.
	 * <p>Active and default profile names are also filtered for duplicates, to avoid
	 * confusion and redundant storage.
	 * <p>The parent environment remains unmodified in any case. Note that any changes to
	 * the parent environment occurring after the call to {@code merge} will not be
	 * reflected in the child. Therefore, care should be taken to configure parent
	 * property sources and profile information prior to calling {@code merge}.
	 * <p>
	 * 将给定的父环境的活动配置文件,默认配置文件和属性源附加到每个<p>的这个(子)环境的各自集合对于父和子中存在的任何同名命名的{@code PropertySource}实例,子实例将为保留并且父实例被丢
	 * 弃这具有允许子级覆盖属性源的效果,以及通过公共属性源类型避免冗余搜索,例如系统环境和系统属性<p>活动和默认配置文件名称也被过滤为重复的,以避免混淆和冗余存储<p>父环境在任何情况下都未修改请注意,在调
	 * 用{@code merge}之后发生的父环境的任何更改都不会反映在子级中。
	 * 
	 * @param parent the environment to merge with
	 * @since 3.1.2
	 * @see org.springframework.context.support.AbstractApplicationContext#setParent
	 */
	void merge(ConfigurableEnvironment parent);

}
