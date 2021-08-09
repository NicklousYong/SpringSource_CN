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
 * Interface representing the environment in which the current application is running.
 * Models two key aspects of the application environment: <em>profiles</em> and
 * <em>properties</em>. Methods related to property access are exposed via the
 * {@link PropertyResolver} superinterface.
 *
 * <p>A <em>profile</em> is a named, logical group of bean definitions to be registered
 * with the container only if the given profile is <em>active</em>. Beans may be assigned
 * to a profile whether defined in XML or via annotations; see the spring-beans 3.1 schema
 * or the {@link org.springframework.context.annotation.Profile @Profile} annotation for
 * syntax details. The role of the {@code Environment} object with relation to profiles is
 * in determining which profiles (if any) are currently {@linkplain #getActiveProfiles
 * active}, and which profiles (if any) should be {@linkplain #getDefaultProfiles active
 * by default}.
 *
 * <p><em>Properties</em> play an important role in almost all applications, and may
 * originate from a variety of sources: properties files, JVM system properties, system
 * environment variables, JNDI, servlet context parameters, ad-hoc Properties objects,
 * Maps, and so on. The role of the environment object with relation to properties is to
 * provide the user with a convenient service interface for configuring property sources
 * and resolving properties from them.
 *
 * <p>Beans managed within an {@code ApplicationContext} may register to be {@link
 * org.springframework.context.EnvironmentAware EnvironmentAware} or {@code @Inject} the
 * {@code Environment} in order to query profile state or resolve properties directly.
 *
 * <p>In most cases, however, application-level beans should not need to interact with the
 * {@code Environment} directly but instead may have to have {@code ${...}} property
 * values replaced by a property placeholder configurer such as
 * {@link org.springframework.context.support.PropertySourcesPlaceholderConfigurer
 * PropertySourcesPlaceholderConfigurer}, which itself is {@code EnvironmentAware} and
 * as of Spring 3.1 is registered by default when using
 * {@code <context:property-placeholder/>}.
 *
 * <p>Configuration of the environment object must be done through the
 * {@code ConfigurableEnvironment} interface, returned from all
 * {@code AbstractApplicationContext} subclass {@code getEnvironment()} methods. See
 * {@link ConfigurableEnvironment} Javadoc for usage examples demonstrating manipulation
 * of property sources prior to application context {@code refresh()}.
 *
 * <p>
 * 表示当前应用程序运行环境的接口模型应用程序环境的两个关键方面：<em>配置文件</em>和<em>属性</em>与属性访问相关的方法通过{@link PropertyResolver }超级接口
 * 
 * <p> <em> </em> </em> </em> </em> </em> </em> </em> </em> </em> </em>只有在给定的配置文件为<em>以XML或通过注释;有关语法细节,请
 * 参阅spring-beans 31模式或{@link orgspringframeworkcontextannotationProfile @Profile}注释与配置文件有关的{@code Environment}
 * 对象的作用在于确定当前使用哪些配置文件(如果有){@linkplain #getActiveProfiles活动},哪些配置文件(如果有的话)应该是{@linkplain #getDefaultProfiles默认激活}
 * 。
 * 
 * 属性</em>在几乎所有应用程序中起着重要的作用,可能来自各种来源：属性文件,JVM系统属性,系统环境变量,JNDI,servlet上下文参数,特别属性对象,地图等环境对象与属性关系的作用是为用户提供方
 * 便的服务界面,用于配置属性源并从中解析属性。
 * 
 *  <p>在{@code ApplicationContext}内管理的Bean可以注册为{@link orgspringframeworkcontextEnvironmentAware EnvironmentAware}
 * 或{@code @Inject} {@code Environment},以便直接查询配置文件状态或解析属性。
 * 
 * <p>然而,在大多数情况下,应用级别的bean不需要直接与{@code环境}进行交互,而是可能必须将{@code $ {}}属性值替换为属性占位符配置,例如{ @link orgspringframeworkcontextsupportPropertySourcesPlaceholderConfigurer PropertySourcesPlaceholderConfigurer}
 * ,它本身是{@code EnvironmentAware},截至Spring 31,默认情况下使用{@code <context：property-placeholder />}注册。
 * 
 * 
 * @author Chris Beams
 * @since 3.1
 * @see PropertyResolver
 * @see EnvironmentCapable
 * @see ConfigurableEnvironment
 * @see AbstractEnvironment
 * @see StandardEnvironment
 * @see org.springframework.context.EnvironmentAware
 * @see org.springframework.context.ConfigurableApplicationContext#getEnvironment
 * @see org.springframework.context.ConfigurableApplicationContext#setEnvironment
 * @see org.springframework.context.support.AbstractApplicationContext#createEnvironment
 */
public interface Environment extends PropertyResolver {

	/**
	 * Return the set of profiles explicitly made active for this environment. Profiles
	 * are used for creating logical groupings of bean definitions to be registered
	 * conditionally, for example based on deployment environment.  Profiles can be
	 * activated by setting {@linkplain AbstractEnvironment#ACTIVE_PROFILES_PROPERTY_NAME
	 * "spring.profiles.active"} as a system property or by calling
	 * {@link ConfigurableEnvironment#setActiveProfiles(String...)}.
	 * <p>If no profiles have explicitly been specified as active, then any {@linkplain
	 * #getDefaultProfiles() default profiles} will automatically be activated.
	 * <p>
	 * <p>环境对象的配置必须通过从{@code AbstractApplicationContext}子类{@code getEnvironment()}方法返回的{@code ConfigurableEnvironment}
	 * 接口进行配置参见{@link ConfigurableEnvironment} Javadoc,用于演示操作应用程序上下文之前的属性源{@code refresh()}。
	 * 
	 * 
	 * @see #getDefaultProfiles
	 * @see ConfigurableEnvironment#setActiveProfiles
	 * @see AbstractEnvironment#ACTIVE_PROFILES_PROPERTY_NAME
	 */
	String[] getActiveProfiles();

	/**
	 * Return the set of profiles to be active by default when no active profiles have
	 * been set explicitly.
	 * <p>
	 * 返回明确为此环境设置的配置文件配置文件用于创建有条件注册的bean定义的逻辑分组,例如基于部署环境可以通过将{@linkplain AbstractEnvironment#ACTIVE_PROFILES_PROPERTY_NAME"springprofilesactive"}
	 * 设置为"激活"来配置文件系统属性或调用{@link ConfigurableEnvironment#setActiveProfiles(String)} <p>如果没有将配置文件明确指定为活动状态,则任
	 * 何{@linkplain #getDefaultProfiles()默认配置文件}将自动激活。
	 * 
	 * 
	 * @see #getActiveProfiles
	 * @see ConfigurableEnvironment#setDefaultProfiles
	 * @see AbstractEnvironment#DEFAULT_PROFILES_PROPERTY_NAME
	 */
	String[] getDefaultProfiles();

	/**
	 * Return whether one or more of the given profiles is active or, in the case of no
	 * explicit active profiles, whether one or more of the given profiles is included in
	 * the set of default profiles. If a profile begins with '!' the logic is inverted,
	 * i.e. the method will return true if the given profile is <em>not</em> active.
	 * For example, <pre class="code">env.acceptsProfiles("p1", "!p2")</pre> will
	 * return {@code true} if profile 'p1' is active or 'p2' is not active.
	 * <p>
	 *  在没有显式设置活动配置文件时,默认情况下将配置文件集恢复为活动状态
	 * 
	 * 
	 * @throws IllegalArgumentException if called with zero arguments
	 * or if any profile is {@code null}, empty or whitespace-only
	 * @see #getActiveProfiles
	 * @see #getDefaultProfiles
	 */
	boolean acceptsProfiles(String... profiles);

}
