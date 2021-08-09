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

package org.springframework.beans.factory.config;

import java.util.Properties;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.core.Constants;
import org.springframework.core.SpringProperties;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.PropertyPlaceholderHelper.PlaceholderResolver;
import org.springframework.util.StringValueResolver;

/**
 * {@link PlaceholderConfigurerSupport} subclass that resolves ${...} placeholders
 * against {@link #setLocation local} {@link #setProperties properties} and/or system properties
 * and environment variables.
 *
 * <p>As of Spring 3.1, {@link org.springframework.context.support.PropertySourcesPlaceholderConfigurer
 * PropertySourcesPlaceholderConfigurer} should be used preferentially over this implementation; it is
 * more flexible through taking advantage of the {@link org.springframework.core.env.Environment Environment} and
 * {@link org.springframework.core.env.PropertySource PropertySource} mechanisms also made available in Spring 3.1.
 *
 * <p>{@link PropertyPlaceholderConfigurer} is still appropriate for use when:
 * <ul>
 * <li>the {@code spring-context} module is not available (i.e., one is using Spring's
 * {@code BeanFactory} API as opposed to {@code ApplicationContext}).
 * <li>existing configuration makes use of the {@link #setSystemPropertiesMode(int) "systemPropertiesMode"} and/or
 * {@link #setSystemPropertiesModeName(String) "systemPropertiesModeName"} properties. Users are encouraged to move
 * away from using these settings, and rather configure property source search order through the container's
 * {@code Environment}; however, exact preservation of functionality may be maintained by continuing to
 * use {@code PropertyPlaceholderConfigurer}.
 * </ul>
 *
 * <p>Prior to Spring 3.1, the {@code <context:property-placeholder/>} namespace element
 * registered an instance of {@code PropertyPlaceholderConfigurer}. It will still do so if
 * using the {@code spring-context-3.0.xsd} definition of the namespace. That is, you can preserve
 * registration of {@code PropertyPlaceholderConfigurer} through the namespace, even if using Spring 3.1;
 * simply do not update your {@code xsi:schemaLocation} and continue using the 3.0 XSD.
 *
 * <p>
 *  {@link PlaceholderConfigurerSupport}子类,根据{@link #setLocation local} {@link #setProperties属性}和/或系统属性和
 * 环境变量解析$ {}占位符。
 * 
 * <p>截至Spring 31,应优先使用{@link orgspringframeworkcontextsupportPropertySourcesPlaceholderConfigurer PropertySourcesPlaceholderConfigurer}
 * ;通过利用Spring 31中也提供的{@link orgspringframeworkcoreenvEnvironment Environment}和{@link orgspringframeworkcoreenvPropertySource PropertySource}
 * 机制,它更加灵活。
 * 
 *  <p> {@ link PropertyPlaceholderConfigurer}在下列情况下仍然适用：
 * <ul>
 * <li> {@code spring-context}模块不可用(即,一个使用Spring的{@code BeanFactory} API而不是{@code ApplicationContext})<li>
 * 现有配置使用{@link# setSystemPropertiesMode(int)"systemPropertiesMode"}和/或{@link #setSystemPropertiesModeName(String)"systemPropertiesModeName"}
 * 属性鼓励用户远离使用这些设置,而是通过容器的{@code Environment}配置属性源搜索顺序;然而,通过继续使用{@code PropertyPlaceholderConfigurer}可以保持
 * 功能的精确保存。
 * </ul>
 * 
 * 在Spring 31之前,{@code <context：property-placeholder />}命名空间元素注册了一个{@code PropertyPlaceholderConfigurer}
 * 的实例。
 * 如果使用{@code spring-context-30xsd}定义,它仍然会这样做的命名空间即使使用Spring 31,您也可以通过名称空间来保留{@code PropertyPlaceholderConfigurer}
 * 的注册;只需不要更新你的{@code xsi：schemaLocation}并继续使用30 XSD。
 * 
 * 
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 02.10.2003
 * @see #setSystemPropertiesModeName
 * @see PlaceholderConfigurerSupport
 * @see PropertyOverrideConfigurer
 * @see org.springframework.context.support.PropertySourcesPlaceholderConfigurer
 */
public class PropertyPlaceholderConfigurer extends PlaceholderConfigurerSupport {

	/** Never check system properties. */
	public static final int SYSTEM_PROPERTIES_MODE_NEVER = 0;

	/**
	 * Check system properties if not resolvable in the specified properties.
	 * This is the default.
	 * <p>
	 *  检查系统属性(如果在指定的属性中不可解析)这是默认值
	 * 
	 */
	public static final int SYSTEM_PROPERTIES_MODE_FALLBACK = 1;

	/**
	 * Check system properties first, before trying the specified properties.
	 * This allows system properties to override any other property source.
	 * <p>
	 *  在尝试指定的属性之前先检查系统属性这允许系统属性覆盖任何其他属性源
	 * 
	 */
	public static final int SYSTEM_PROPERTIES_MODE_OVERRIDE = 2;


	private static final Constants constants = new Constants(PropertyPlaceholderConfigurer.class);

	private int systemPropertiesMode = SYSTEM_PROPERTIES_MODE_FALLBACK;

	private boolean searchSystemEnvironment =
			!SpringProperties.getFlag(AbstractEnvironment.IGNORE_GETENV_PROPERTY_NAME);


	/**
	 * Set the system property mode by the name of the corresponding constant,
	 * e.g. "SYSTEM_PROPERTIES_MODE_OVERRIDE".
	 * <p>
	 * 通过相应常量的名称设置系统属性模式,例如"SYSTEM_PROPERTIES_MODE_OVERRIDE"
	 * 
	 * 
	 * @param constantName name of the constant
	 * @throws java.lang.IllegalArgumentException if an invalid constant was specified
	 * @see #setSystemPropertiesMode
	 */
	public void setSystemPropertiesModeName(String constantName) throws IllegalArgumentException {
		this.systemPropertiesMode = constants.asNumber(constantName).intValue();
	}

	/**
	 * Set how to check system properties: as fallback, as override, or never.
	 * For example, will resolve ${user.dir} to the "user.dir" system property.
	 * <p>The default is "fallback": If not being able to resolve a placeholder
	 * with the specified properties, a system property will be tried.
	 * "override" will check for a system property first, before trying the
	 * specified properties. "never" will not check system properties at all.
	 * <p>
	 *  设置如何检查系统属性：作为备用,作为覆盖或永远不会例如,将$ {userdir}解析为"userdir"系统属性<p>默认值为"fallback"：如果无法解析占位符指定的属性,系统属性将被尝试"覆盖
	 * "将首先检查系统属性,然后再尝试指定的属性"从不"将不会检查系统属性。
	 * 
	 * 
	 * @see #SYSTEM_PROPERTIES_MODE_NEVER
	 * @see #SYSTEM_PROPERTIES_MODE_FALLBACK
	 * @see #SYSTEM_PROPERTIES_MODE_OVERRIDE
	 * @see #setSystemPropertiesModeName
	 */
	public void setSystemPropertiesMode(int systemPropertiesMode) {
		this.systemPropertiesMode = systemPropertiesMode;
	}

	/**
	 * Set whether to search for a matching system environment variable
	 * if no matching system property has been found. Only applied when
	 * "systemPropertyMode" is active (i.e. "fallback" or "override"), right
	 * after checking JVM system properties.
	 * <p>Default is "true". Switch this setting off to never resolve placeholders
	 * against system environment variables. Note that it is generally recommended
	 * to pass external values in as JVM system properties: This can easily be
	 * achieved in a startup script, even for existing environment variables.
	 * <p><b>NOTE:</b> Access to environment variables does not work on the
	 * Sun VM 1.4, where the corresponding {@link System#getenv} support was
	 * disabled - before it eventually got re-enabled for the Sun VM 1.5.
	 * Please upgrade to 1.5 (or higher) if you intend to rely on the
	 * environment variable support.
	 * <p>
	 * 设置是否搜索匹配的系统环境变量,如果没有找到匹配的系统属性只有在"systemPropertyMode"处于活动状态(即"fallback"或"override")时才应用,在检查JVM系统属性之后<p>
	 * 默认为"true" "将此设置切换为从不针对系统环境变量解析占位符请注意,通常建议将外部值作为JVM系统属性传递：即使对于现有的环境变量,也可以在启动脚本中轻松实现此功能<p> <b>注意：</b>访问
	 * 环境变量在Sun VM 14上不起作用,在此之前,相应的{@link System#getenv}支持被禁用 - 在Sun VM 15最终重新启用之前,请升级到15(或更高)如果你打算依靠环境变量支持。
	 * 
	 * 
	 * @see #setSystemPropertiesMode
	 * @see java.lang.System#getProperty(String)
	 * @see java.lang.System#getenv(String)
	 */
	public void setSearchSystemEnvironment(boolean searchSystemEnvironment) {
		this.searchSystemEnvironment = searchSystemEnvironment;
	}

	/**
	 * Resolve the given placeholder using the given properties, performing
	 * a system properties check according to the given mode.
	 * <p>The default implementation delegates to {@code resolvePlaceholder
	 * (placeholder, props)} before/after the system properties check.
	 * <p>Subclasses can override this for custom resolution strategies,
	 * including customized points for the system properties check.
	 * <p>
	 * 使用给定的属性解决给定的占位符,根据给定的模式执行系统属性检查<p>在系统属性检查之前/之后,默认实现委托{@code resolvePlaceholder(placeholder,props)} <p>
	 * 子类可以覆盖这对于自定义分辨率策略,包括定制点的系统属性检查。
	 * 
	 * 
	 * @param placeholder the placeholder to resolve
	 * @param props the merged properties of this configurer
	 * @param systemPropertiesMode the system properties mode,
	 * according to the constants in this class
	 * @return the resolved value, of null if none
	 * @see #setSystemPropertiesMode
	 * @see System#getProperty
	 * @see #resolvePlaceholder(String, java.util.Properties)
	 */
	protected String resolvePlaceholder(String placeholder, Properties props, int systemPropertiesMode) {
		String propVal = null;
		if (systemPropertiesMode == SYSTEM_PROPERTIES_MODE_OVERRIDE) {
			propVal = resolveSystemProperty(placeholder);
		}
		if (propVal == null) {
			propVal = resolvePlaceholder(placeholder, props);
		}
		if (propVal == null && systemPropertiesMode == SYSTEM_PROPERTIES_MODE_FALLBACK) {
			propVal = resolveSystemProperty(placeholder);
		}
		return propVal;
	}

	/**
	 * Resolve the given placeholder using the given properties.
	 * The default implementation simply checks for a corresponding property key.
	 * <p>Subclasses can override this for customized placeholder-to-key mappings
	 * or custom resolution strategies, possibly just using the given properties
	 * as fallback.
	 * <p>Note that system properties will still be checked before respectively
	 * after this method is invoked, according to the system properties mode.
	 * <p>
	 * 使用给定的属性解决给定的占位符默认实现只需检查相应的属性键<p>子类可以覆盖自定义占位符到键映射或自定义分辨率策略,可能只是使用给定的属性作为备用根据系统属性模式,在分配此方法后,系统属性仍将被分别检查
	 * 。
	 * 
	 * 
	 * @param placeholder the placeholder to resolve
	 * @param props the merged properties of this configurer
	 * @return the resolved value, of {@code null} if none
	 * @see #setSystemPropertiesMode
	 */
	protected String resolvePlaceholder(String placeholder, Properties props) {
		return props.getProperty(placeholder);
	}

	/**
	 * Resolve the given key as JVM system property, and optionally also as
	 * system environment variable if no matching system property has been found.
	 * <p>
	 *  将给定的键解析为JVM系统属性,如果没有找到匹配的系统属性,还可以将其作为系统环境变量
	 * 
	 * 
	 * @param key the placeholder to resolve as system property key
	 * @return the system property value, or {@code null} if not found
	 * @see #setSearchSystemEnvironment
	 * @see System#getProperty(String)
	 * @see System#getenv(String)
	 */
	protected String resolveSystemProperty(String key) {
		try {
			String value = System.getProperty(key);
			if (value == null && this.searchSystemEnvironment) {
				value = System.getenv(key);
			}
			return value;
		}
		catch (Throwable ex) {
			if (logger.isDebugEnabled()) {
				logger.debug("Could not access system property '" + key + "': " + ex);
			}
			return null;
		}
	}


	/**
	 * Visit each bean definition in the given bean factory and attempt to replace ${...} property
	 * placeholders with values from the given properties.
	 * <p>
	 *  访问给定bean工厂中的每个bean定义,并尝试使用给定属性的值替换$ {}属性占位符
	 * 
	 */
	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props)
			throws BeansException {

		StringValueResolver valueResolver = new PlaceholderResolvingStringValueResolver(props);
		doProcessProperties(beanFactoryToProcess, valueResolver);
	}

	/**
	 * Parse the given String value for placeholder resolution.
	 * <p>
	 *  解析给定的String值以进行占位符解析
	 * 
	 * @param strVal the String value to parse
	 * @param props the Properties to resolve placeholders against
	 * @param visitedPlaceholders the placeholders that have already been visited
	 * during the current resolution attempt (ignored in this version of the code)
	 * @deprecated as of Spring 3.0, in favor of using {@link #resolvePlaceholder}
	 * with {@link org.springframework.util.PropertyPlaceholderHelper}.
	 * Only retained for compatibility with Spring 2.5 extensions.
	 */
	@Deprecated
	protected String parseStringValue(String strVal, Properties props, Set<?> visitedPlaceholders) {
		PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper(
				placeholderPrefix, placeholderSuffix, valueSeparator, ignoreUnresolvablePlaceholders);
		PlaceholderResolver resolver = new PropertyPlaceholderConfigurerResolver(props);
		return helper.replacePlaceholders(strVal, resolver);
	}


	private class PlaceholderResolvingStringValueResolver implements StringValueResolver {

		private final PropertyPlaceholderHelper helper;

		private final PlaceholderResolver resolver;

		public PlaceholderResolvingStringValueResolver(Properties props) {
			this.helper = new PropertyPlaceholderHelper(
					placeholderPrefix, placeholderSuffix, valueSeparator, ignoreUnresolvablePlaceholders);
			this.resolver = new PropertyPlaceholderConfigurerResolver(props);
		}

		@Override
		public String resolveStringValue(String strVal) throws BeansException {
			String resolved = this.helper.replacePlaceholders(strVal, this.resolver);
			if (trimValues) {
				resolved = resolved.trim();
			}
			return (resolved.equals(nullValue) ? null : resolved);
		}
	}


	private class PropertyPlaceholderConfigurerResolver implements PlaceholderResolver {

		private final Properties props;

		private PropertyPlaceholderConfigurerResolver(Properties props) {
			this.props = props;
		}

		@Override
		public String resolvePlaceholder(String placeholderName) {
			return PropertyPlaceholderConfigurer.this.resolvePlaceholder(placeholderName, props, systemPropertiesMode);
		}
	}

}
