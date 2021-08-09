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

package org.springframework.core.env;

import java.security.AccessControlException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.core.SpringProperties;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * Abstract base class for {@link Environment} implementations. Supports the notion of
 * reserved default profile names and enables specifying active and default profiles
 * through the {@link #ACTIVE_PROFILES_PROPERTY_NAME} and
 * {@link #DEFAULT_PROFILES_PROPERTY_NAME} properties.
 *
 * <p>Concrete subclasses differ primarily on which {@link PropertySource} objects they
 * add by default. {@code AbstractEnvironment} adds none. Subclasses should contribute
 * property sources through the protected {@link #customizePropertySources(MutablePropertySources)}
 * hook, while clients should customize using {@link ConfigurableEnvironment#getPropertySources()}
 * and working against the {@link MutablePropertySources} API.
 * See {@link ConfigurableEnvironment} javadoc for usage examples.
 *
 * <p>
 * {@link Environment}实现的抽象基类支持保留的默认配置文件名称的概念,并通过{@link #ACTIVE_PROFILES_PROPERTY_NAME}和{@link #DEFAULT_PROFILES_PROPERTY_NAME}
 * 属性指定活动和默认配置文件。
 * 
 *  <p>具体的子类主要在于默认添加的{@link PropertySource}对象{@code AbstractEnvironment}添加无子类应通过受保护的{@link #customizePropertySources(MutablePropertySources)}
 * 钩子提供属性源,而客户端应自定义使用{@link ConfigurableEnvironment#getPropertySources()}并针对{@link MutablePropertySources}
 *  API查看{@link ConfigurableEnvironment} javadoc的使用示例。
 * 
 * 
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 * @see ConfigurableEnvironment
 * @see StandardEnvironment
 */
public abstract class AbstractEnvironment implements ConfigurableEnvironment {

	/**
	 * System property that instructs Spring to ignore system environment variables,
	 * i.e. to never attempt to retrieve such a variable via {@link System#getenv()}.
	 * <p>The default is "false", falling back to system environment variable checks if a
	 * Spring environment property (e.g. a placeholder in a configuration String) isn't
	 * resolvable otherwise. Consider switching this flag to "true" if you experience
	 * log warnings from {@code getenv} calls coming from Spring, e.g. on WebSphere
	 * with strict SecurityManager settings and AccessControlExceptions warnings.
	 * <p>
	 * 系统属性指示Spring忽略系统环境变量,即永远不会通过{@link System#getenv()}来检索这样一个变量。
	 * <p>默认值为"false",返回到系统环境变量检查如果一个Spring环境属性(例如配置字符串中的占位符)不可解析如果遇到来自Spring的{@code getenv}调用的日志警告,请考虑将此标志切
	 * 换为"true",例如在具有严格SecurityManager设置和AccessControlExceptions警告的WebSphere上。
	 * 系统属性指示Spring忽略系统环境变量,即永远不会通过{@link System#getenv()}来检索这样一个变量。
	 * 
	 * 
	 * @see #suppressGetenvAccess()
	 */
	public static final String IGNORE_GETENV_PROPERTY_NAME = "spring.getenv.ignore";

	/**
	 * Name of property to set to specify active profiles: {@value}. Value may be comma
	 * delimited.
	 * <p>Note that certain shell environments such as Bash disallow the use of the period
	 * character in variable names. Assuming that Spring's {@link SystemEnvironmentPropertySource}
	 * is in use, this property may be specified as an environment variable as
	 * {@code SPRING_PROFILES_ACTIVE}.
	 * <p>
	 * 要设置为指定活动配置文件的属性名称：{@value}值可以以逗号分隔<p>请注意,某些shell环境(如Bash)不允许使用变量名称中的句点字符假设Spring的{@link SystemEnvironmentPropertySource}
	 * 处于使用,此属性可以被指定为环境变量{@code SPRING_PROFILES_ACTIVE}。
	 * 
	 * 
	 * @see ConfigurableEnvironment#setActiveProfiles
	 */
	public static final String ACTIVE_PROFILES_PROPERTY_NAME = "spring.profiles.active";

	/**
	 * Name of property to set to specify profiles active by default: {@value}. Value may
	 * be comma delimited.
	 * <p>Note that certain shell environments such as Bash disallow the use of the period
	 * character in variable names. Assuming that Spring's {@link SystemEnvironmentPropertySource}
	 * is in use, this property may be specified as an environment variable as
	 * {@code SPRING_PROFILES_DEFAULT}.
	 * <p>
	 *  要设置的属性名称,以指定默认情况下活动的配置文件：{@value}值可能以逗号分隔<p>请注意,某些shell环境(如Bash)不允许在变量名称中使用句点字符假设Spring的{@link SystemEnvironmentPropertySource}
	 * 正在使用中,此属性可能被指定为环境变量{@code SPRING_PROFILES_DEFAULT}。
	 * 
	 * 
	 * @see ConfigurableEnvironment#setDefaultProfiles
	 */
	public static final String DEFAULT_PROFILES_PROPERTY_NAME = "spring.profiles.default";

	/**
	 * Name of reserved default profile name: {@value}. If no default profile names are
	 * explicitly and no active profile names are explicitly set, this profile will
	 * automatically be activated by default.
	 * <p>
	 * 保留的默认配置文件名称的名称：{@value}如果未显式设置默认配置文件名称,并且没有显式设置活动配置文件名称,则默认情况下将自动激活此配置文件
	 * 
	 * 
	 * @see #getReservedDefaultProfiles
	 * @see ConfigurableEnvironment#setDefaultProfiles
	 * @see ConfigurableEnvironment#setActiveProfiles
	 * @see AbstractEnvironment#DEFAULT_PROFILES_PROPERTY_NAME
	 * @see AbstractEnvironment#ACTIVE_PROFILES_PROPERTY_NAME
	 */
	protected static final String RESERVED_DEFAULT_PROFILE_NAME = "default";


	protected final Log logger = LogFactory.getLog(getClass());

	private final Set<String> activeProfiles = new LinkedHashSet<String>();

	private final Set<String> defaultProfiles = new LinkedHashSet<String>(getReservedDefaultProfiles());

	private final MutablePropertySources propertySources = new MutablePropertySources(this.logger);

	private final ConfigurablePropertyResolver propertyResolver =
			new PropertySourcesPropertyResolver(this.propertySources);


	/**
	 * Create a new {@code Environment} instance, calling back to
	 * {@link #customizePropertySources(MutablePropertySources)} during construction to
	 * allow subclasses to contribute or manipulate {@link PropertySource} instances as
	 * appropriate.
	 * <p>
	 *  创建一个新的{@code Environment}实例,在构造期间调用{@link #customizePropertySources(MutablePropertySources)},以允许子类根据
	 * 需要贡献或操纵{@link PropertySource}实例。
	 * 
	 * 
	 * @see #customizePropertySources(MutablePropertySources)
	 */
	public AbstractEnvironment() {
		customizePropertySources(this.propertySources);
		if (this.logger.isDebugEnabled()) {
			this.logger.debug(String.format(
					"Initialized %s with PropertySources %s", getClass().getSimpleName(), this.propertySources));
		}
	}


	/**
	 * Customize the set of {@link PropertySource} objects to be searched by this
	 * {@code Environment} during calls to {@link #getProperty(String)} and related
	 * methods.
	 *
	 * <p>Subclasses that override this method are encouraged to add property
	 * sources using {@link MutablePropertySources#addLast(PropertySource)} such that
	 * further subclasses may call {@code super.customizePropertySources()} with
	 * predictable results. For example:
	 * <pre class="code">
	 * public class Level1Environment extends AbstractEnvironment {
	 *     &#064;Override
	 *     protected void customizePropertySources(MutablePropertySources propertySources) {
	 *         super.customizePropertySources(propertySources); // no-op from base class
	 *         propertySources.addLast(new PropertySourceA(...));
	 *         propertySources.addLast(new PropertySourceB(...));
	 *     }
	 * }
	 *
	 * public class Level2Environment extends Level1Environment {
	 *     &#064;Override
	 *     protected void customizePropertySources(MutablePropertySources propertySources) {
	 *         super.customizePropertySources(propertySources); // add all from superclass
	 *         propertySources.addLast(new PropertySourceC(...));
	 *         propertySources.addLast(new PropertySourceD(...));
	 *     }
	 * }
	 * </pre>
	 * In this arrangement, properties will be resolved against sources A, B, C, D in that
	 * order. That is to say that property source "A" has precedence over property source
	 * "D". If the {@code Level2Environment} subclass wished to give property sources C
	 * and D higher precedence than A and B, it could simply call
	 * {@code super.customizePropertySources} after, rather than before adding its own:
	 * <pre class="code">
	 * public class Level2Environment extends Level1Environment {
	 *     &#064;Override
	 *     protected void customizePropertySources(MutablePropertySources propertySources) {
	 *         propertySources.addLast(new PropertySourceC(...));
	 *         propertySources.addLast(new PropertySourceD(...));
	 *         super.customizePropertySources(propertySources); // add all from superclass
	 *     }
	 * }
	 * </pre>
	 * The search order is now C, D, A, B as desired.
	 *
	 * <p>Beyond these recommendations, subclasses may use any of the {@code add&#42;},
	 * {@code remove}, or {@code replace} methods exposed by {@link MutablePropertySources}
	 * in order to create the exact arrangement of property sources desired.
	 *
	 * <p>The base implementation registers no property sources.
	 *
	 * <p>Note that clients of any {@link ConfigurableEnvironment} may further customize
	 * property sources via the {@link #getPropertySources()} accessor, typically within
	 * an {@link org.springframework.context.ApplicationContextInitializer
	 * ApplicationContextInitializer}. For example:
	 * <pre class="code">
	 * ConfigurableEnvironment env = new StandardEnvironment();
	 * env.getPropertySources().addLast(new PropertySourceX(...));
	 * </pre>
	 *
	 * <h2>A warning about instance variable access</h2>
	 * Instance variables declared in subclasses and having default initial values should
	 * <em>not</em> be accessed from within this method. Due to Java object creation
	 * lifecycle constraints, any initial value will not yet be assigned when this
	 * callback is invoked by the {@link #AbstractEnvironment()} constructor, which may
	 * lead to a {@code NullPointerException} or other problems. If you need to access
	 * default values of instance variables, leave this method as a no-op and perform
	 * property source manipulation and instance variable access directly within the
	 * subclass constructor. Note that <em>assigning</em> values to instance variables is
	 * not problematic; it is only attempting to read default values that must be avoided.
	 *
	 * <p>
	 *  在{@link #getProperty(String)}和相关方法的调用期间,自定义此{@code环境}要搜索的{@link PropertySource}对象集
	 * 
	 * 鼓励覆盖此方法的子类使用{@link MutablePropertySources#addLast(PropertySource)}添加属性源,以便其他子类可以使用可预测的结果调用{@code supercustomizePropertySources()}
	 * )例如：。
	 * <pre class="code">
	 *  public class Level1Environment extends AbstractEnvironment {@Override protected void customizePropertySources(MutablePropertySources propertySources){supercustomizePropertySources(propertySources); // no-op from base class propertySourcesaddLast(new PropertySourceA()); propertySourcesaddLast(new PropertySourceB()); }
	 * }。
	 * 
	 * public class Level2Environment扩展Level1Environment {@Override protected void customizePropertySources(MutablePropertySources propertySources){supercustomizePropertySources(propertySources); //从superclass添加全部propertySourcesaddLast(new PropertySourceC()); propertySourcesaddLast(new PropertySourceD()); }
	 * }。
	 * </pre>
	 *  在这种安排中,属性将按照顺序解决源A,B,C,D。
	 * 也就是说,属性源"A"优先于属性源"D"如果{@code Level2Environment}子类希望赋予属性源C和D的优先级高于A和B,它可以简单地调用{@code supercustomizePropertySources}
	 * ,而不是在添加自己的之前：。
	 *  在这种安排中,属性将按照顺序解决源A,B,C,D。
	 * <pre class="code">
	 * public class Level2Environment extends Level1Environment {@Override protected void customizePropertySources(MutablePropertySources propertySources){propertySourcesaddLast(new PropertySourceC()); propertySourcesaddLast(new PropertySourceD()); supercustomizePropertySources(propertySources); //从超类添加全部}
	 * }。
	 * </pre>
	 *  搜索顺序现在是C,D,A,B
	 * 
	 *  <p>除了这些建议,子类可以使用{@link MutablePropertySources}公开的任何{@code add *},{@code remove}或{@code replace}方法,以创
	 * 建属性源的确切安排期望。
	 * 
	 *  <p>基本实现不注册资源
	 * 
	 * <p>请注意,任何{@link ConfigurableEnvironment}的客户端可以通过{@link #getPropertySources()}访问器进一步自定义属性源,通常位于{@link orgspringframeworkcontextApplicationContextInitializer ApplicationContextInitializer}
	 * 中。
	 * 例如：。
	 * <pre class="code">
	 *  ConfigurableEnvironment env = new StandardEnvironment(); envgetPropertySources()addLast(new Property
	 * SourceX());。
	 * 
	 * @see MutablePropertySources
	 * @see PropertySourcesPropertyResolver
	 * @see org.springframework.context.ApplicationContextInitializer
	 */
	protected void customizePropertySources(MutablePropertySources propertySources) {
	}

	/**
	 * Return the set of reserved default profile names. This implementation returns
	 * {@value #RESERVED_DEFAULT_PROFILE_NAME}. Subclasses may override in order to
	 * customize the set of reserved names.
	 * <p>
	 * </pre>
	 * 
	 * <h2>关于实例变量访问的警告</h2>在子类中声明并具有默认初始值的实例变量应该在此方法内被访问由于Java对象创建生命周期约束,任何初始值将当这个回调由{@link #AbstractEnvironment()}
	 * 构造函数调用时,还没有被分配,这可能导致{@code NullPointerException}或其他问题如果您需要访问实例变量的默认值,请将此方法作为no-op并在子类构造函数中直接执行属性源操作和实
	 * 例变量访问注意,将<em>值赋值给实例变量是没有问题的;它只是尝试读取必须避免的默认值。
	 * 
	 * 
	 * @see #RESERVED_DEFAULT_PROFILE_NAME
	 * @see #doGetDefaultProfiles()
	 */
	protected Set<String> getReservedDefaultProfiles() {
		return Collections.singleton(RESERVED_DEFAULT_PROFILE_NAME);
	}


	//---------------------------------------------------------------------
	// Implementation of ConfigurableEnvironment interface
	//---------------------------------------------------------------------

	@Override
	public String[] getActiveProfiles() {
		return StringUtils.toStringArray(doGetActiveProfiles());
	}

	/**
	 * Return the set of active profiles as explicitly set through
	 * {@link #setActiveProfiles} or if the current set of active profiles
	 * is empty, check for the presence of the {@value #ACTIVE_PROFILES_PROPERTY_NAME}
	 * property and assign its value to the set of active profiles.
	 * <p>
	 * 返回一组保留的默认配置文件名称此实现返回{@value #RESERVED_DEFAULT_PROFILE_NAME}子类可以覆盖以自定义一组保留名称
	 * 
	 * 
	 * @see #getActiveProfiles()
	 * @see #ACTIVE_PROFILES_PROPERTY_NAME
	 */
	protected Set<String> doGetActiveProfiles() {
		synchronized (this.activeProfiles) {
			if (this.activeProfiles.isEmpty()) {
				String profiles = getProperty(ACTIVE_PROFILES_PROPERTY_NAME);
				if (StringUtils.hasText(profiles)) {
					setActiveProfiles(StringUtils.commaDelimitedListToStringArray(
							StringUtils.trimAllWhitespace(profiles)));
				}
			}
			return this.activeProfiles;
		}
	}

	@Override
	public void setActiveProfiles(String... profiles) {
		Assert.notNull(profiles, "Profile array must not be null");
		synchronized (this.activeProfiles) {
			this.activeProfiles.clear();
			for (String profile : profiles) {
				validateProfile(profile);
				this.activeProfiles.add(profile);
			}
		}
	}

	@Override
	public void addActiveProfile(String profile) {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug(String.format("Activating profile '%s'", profile));
		}
		validateProfile(profile);
		doGetActiveProfiles();
		synchronized (this.activeProfiles) {
			this.activeProfiles.add(profile);
		}
	}


	@Override
	public String[] getDefaultProfiles() {
		return StringUtils.toStringArray(doGetDefaultProfiles());
	}

	/**
	 * Return the set of default profiles explicitly set via
	 * {@link #setDefaultProfiles(String...)} or if the current set of default profiles
	 * consists only of {@linkplain #getReservedDefaultProfiles() reserved default
	 * profiles}, then check for the presence of the
	 * {@value #DEFAULT_PROFILES_PROPERTY_NAME} property and assign its value (if any)
	 * to the set of default profiles.
	 * <p>
	 *  返回通过{@link #setActiveProfiles}显式设置的活动配置文件集,或者如果当前活动配置文件集为空,请检查是否存在{@value #ACTIVE_PROFILES_PROPERTY_NAME}
	 * 属性,并将其值分配给活动配置文件集。
	 * 
	 * 
	 * @see #AbstractEnvironment()
	 * @see #getDefaultProfiles()
	 * @see #DEFAULT_PROFILES_PROPERTY_NAME
	 * @see #getReservedDefaultProfiles()
	 */
	protected Set<String> doGetDefaultProfiles() {
		synchronized (this.defaultProfiles) {
			if (this.defaultProfiles.equals(getReservedDefaultProfiles())) {
				String profiles = getProperty(DEFAULT_PROFILES_PROPERTY_NAME);
				if (StringUtils.hasText(profiles)) {
					setDefaultProfiles(StringUtils.commaDelimitedListToStringArray(
							StringUtils.trimAllWhitespace(profiles)));
				}
			}
			return this.defaultProfiles;
		}
	}

	/**
	 * Specify the set of profiles to be made active by default if no other profiles
	 * are explicitly made active through {@link #setActiveProfiles}.
	 * <p>Calling this method removes overrides any reserved default profiles
	 * that may have been added during construction of the environment.
	 * <p>
	 * 返回通过{@link #setDefaultProfiles(String)}显式设置的一组默认配置文件,或者如果当前的一组默认配置文件仅由{@linkplain #getReservedDefaultProfiles()保留默认配置文件}
	 * 组成),则检查是否存在{@值#DEFAULT_PROFILES_PROPERTY_NAME}属性,并将其值(如果有)分配给默认配置文件集。
	 * 
	 * 
	 * @see #AbstractEnvironment()
	 * @see #getReservedDefaultProfiles()
	 */
	@Override
	public void setDefaultProfiles(String... profiles) {
		Assert.notNull(profiles, "Profile array must not be null");
		synchronized (this.defaultProfiles) {
			this.defaultProfiles.clear();
			for (String profile : profiles) {
				validateProfile(profile);
				this.defaultProfiles.add(profile);
			}
		}
	}

	@Override
	public boolean acceptsProfiles(String... profiles) {
		Assert.notEmpty(profiles, "Must specify at least one profile");
		for (String profile : profiles) {
			if (StringUtils.hasLength(profile) && profile.charAt(0) == '!') {
				if (!isProfileActive(profile.substring(1))) {
					return true;
				}
			}
			else if (isProfileActive(profile)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Return whether the given profile is active, or if active profiles are empty
	 * whether the profile should be active by default.
	 * <p>
	 *  如果没有其他配置文件通过{@link #setActiveProfiles}显式生效,则默认情况下指定要设置为有效的配置文件集<p>调用此方法将覆盖在构建环境过程中可能添加的任何预留默认配置文件
	 * 
	 * 
	 * @throws IllegalArgumentException per {@link #validateProfile(String)}
	 */
	protected boolean isProfileActive(String profile) {
		validateProfile(profile);
		Set<String> currentActiveProfiles = doGetActiveProfiles();
		return (currentActiveProfiles.contains(profile) ||
				(currentActiveProfiles.isEmpty() && doGetDefaultProfiles().contains(profile)));
	}

	/**
	 * Validate the given profile, called internally prior to adding to the set of
	 * active or default profiles.
	 * <p>Subclasses may override to impose further restrictions on profile syntax.
	 * <p>
	 *  返回给定的配置文件是否处于活动状态,或者活动配置文件为空,默认情况下配置文件是否处于活动状态
	 * 
	 * 
	 * @throws IllegalArgumentException if the profile is null, empty, whitespace-only or
	 * begins with the profile NOT operator (!).
	 * @see #acceptsProfiles
	 * @see #addActiveProfile
	 * @see #setDefaultProfiles
	 */
	protected void validateProfile(String profile) {
		if (!StringUtils.hasText(profile)) {
			throw new IllegalArgumentException("Invalid profile [" + profile + "]: must contain text");
		}
		if (profile.charAt(0) == '!') {
			throw new IllegalArgumentException("Invalid profile [" + profile + "]: must not begin with ! operator");
		}
	}

	@Override
	public MutablePropertySources getPropertySources() {
		return this.propertySources;
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public Map<String, Object> getSystemEnvironment() {
		if (suppressGetenvAccess()) {
			return Collections.emptyMap();
		}
		try {
			return (Map) System.getenv();
		}
		catch (AccessControlException ex) {
			return (Map) new ReadOnlySystemAttributesMap() {
				@Override
				protected String getSystemAttribute(String attributeName) {
					try {
						return System.getenv(attributeName);
					}
					catch (AccessControlException ex) {
						if (logger.isInfoEnabled()) {
							logger.info(String.format("Caught AccessControlException when accessing system " +
									"environment variable [%s]; its value will be returned [null]. Reason: %s",
									attributeName, ex.getMessage()));
						}
						return null;
					}
				}
			};
		}
	}

	/**
	 * Determine whether to suppress {@link System#getenv()}/{@link System#getenv(String)}
	 * access for the purposes of {@link #getSystemEnvironment()}.
	 * <p>If this method returns {@code true}, an empty dummy Map will be used instead
	 * of the regular system environment Map, never even trying to call {@code getenv}
	 * and therefore avoiding security manager warnings (if any).
	 * <p>The default implementation checks for the "spring.getenv.ignore" system property,
	 * returning {@code true} if its value equals "true" in any case.
	 * <p>
	 * 验证给定的配置文件,在添加到一组活动或默认配置文件之前称为内部<p>子类可以覆盖以对配置文件语法进一步限制
	 * 
	 * 
	 * @see #IGNORE_GETENV_PROPERTY_NAME
	 * @see SpringProperties#getFlag
	 */
	protected boolean suppressGetenvAccess() {
		return SpringProperties.getFlag(IGNORE_GETENV_PROPERTY_NAME);
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public Map<String, Object> getSystemProperties() {
		try {
			return (Map) System.getProperties();
		}
		catch (AccessControlException ex) {
			return (Map) new ReadOnlySystemAttributesMap() {
				@Override
				protected String getSystemAttribute(String attributeName) {
					try {
						return System.getProperty(attributeName);
					}
					catch (AccessControlException ex) {
						if (logger.isInfoEnabled()) {
							logger.info(String.format("Caught AccessControlException when accessing system " +
									"property [%s]; its value will be returned [null]. Reason: %s",
									attributeName, ex.getMessage()));
						}
						return null;
					}
				}
			};
		}
	}

	@Override
	public void merge(ConfigurableEnvironment parent) {
		for (PropertySource<?> ps : parent.getPropertySources()) {
			if (!this.propertySources.contains(ps.getName())) {
				this.propertySources.addLast(ps);
			}
		}
		String[] parentActiveProfiles = parent.getActiveProfiles();
		if (!ObjectUtils.isEmpty(parentActiveProfiles)) {
			synchronized (this.activeProfiles) {
				for (String profile : parentActiveProfiles) {
					this.activeProfiles.add(profile);
				}
			}
		}
		String[] parentDefaultProfiles = parent.getDefaultProfiles();
		if (!ObjectUtils.isEmpty(parentDefaultProfiles)) {
			synchronized (this.defaultProfiles) {
				this.defaultProfiles.remove(RESERVED_DEFAULT_PROFILE_NAME);
				for (String profile : parentDefaultProfiles) {
					this.defaultProfiles.add(profile);
				}
			}
		}
	}


	//---------------------------------------------------------------------
	// Implementation of ConfigurablePropertyResolver interface
	//---------------------------------------------------------------------

	@Override
	public ConfigurableConversionService getConversionService() {
		return this.propertyResolver.getConversionService();
	}

	@Override
	public void setConversionService(ConfigurableConversionService conversionService) {
		this.propertyResolver.setConversionService(conversionService);
	}

	@Override
	public void setPlaceholderPrefix(String placeholderPrefix) {
		this.propertyResolver.setPlaceholderPrefix(placeholderPrefix);
	}

	@Override
	public void setPlaceholderSuffix(String placeholderSuffix) {
		this.propertyResolver.setPlaceholderSuffix(placeholderSuffix);
	}

	@Override
	public void setValueSeparator(String valueSeparator) {
		this.propertyResolver.setValueSeparator(valueSeparator);
	}

	@Override
	public void setIgnoreUnresolvableNestedPlaceholders(boolean ignoreUnresolvableNestedPlaceholders) {
		this.propertyResolver.setIgnoreUnresolvableNestedPlaceholders(ignoreUnresolvableNestedPlaceholders);
	}

	@Override
	public void setRequiredProperties(String... requiredProperties) {
		this.propertyResolver.setRequiredProperties(requiredProperties);
	}

	@Override
	public void validateRequiredProperties() throws MissingRequiredPropertiesException {
		this.propertyResolver.validateRequiredProperties();
	}


	//---------------------------------------------------------------------
	// Implementation of PropertyResolver interface
	//---------------------------------------------------------------------

	@Override
	public boolean containsProperty(String key) {
		return this.propertyResolver.containsProperty(key);
	}

	@Override
	public String getProperty(String key) {
		return this.propertyResolver.getProperty(key);
	}

	@Override
	public String getProperty(String key, String defaultValue) {
		return this.propertyResolver.getProperty(key, defaultValue);
	}

	@Override
	public <T> T getProperty(String key, Class<T> targetType) {
		return this.propertyResolver.getProperty(key, targetType);
	}

	@Override
	public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
		return this.propertyResolver.getProperty(key, targetType, defaultValue);
	}

	@Override
	@Deprecated
	public <T> Class<T> getPropertyAsClass(String key, Class<T> targetType) {
		return this.propertyResolver.getPropertyAsClass(key, targetType);
	}

	@Override
	public String getRequiredProperty(String key) throws IllegalStateException {
		return this.propertyResolver.getRequiredProperty(key);
	}

	@Override
	public <T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException {
		return this.propertyResolver.getRequiredProperty(key, targetType);
	}

	@Override
	public String resolvePlaceholders(String text) {
		return this.propertyResolver.resolvePlaceholders(text);
	}

	@Override
	public String resolveRequiredPlaceholders(String text) throws IllegalArgumentException {
		return this.propertyResolver.resolveRequiredPlaceholders(text);
	}


	@Override
	public String toString() {
		return String.format("%s {activeProfiles=%s, defaultProfiles=%s, propertySources=%s}",
				getClass().getSimpleName(), this.activeProfiles, this.defaultProfiles,
				this.propertySources);
	}

}
