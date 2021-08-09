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

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.util.StringValueResolver;

/**
 * Abstract base class for property resource configurers that resolve placeholders
 * in bean definition property values. Implementations <em>pull</em> values from a
 * properties file or other {@linkplain org.springframework.core.env.PropertySource
 * property source} into bean definitions.
 *
 * <p>The default placeholder syntax follows the Ant / Log4J / JSP EL style:
 *
 * <pre class="code">${...}</pre>
 *
 * Example XML bean definition:
 *
 * <pre class="code">
 * <bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource"/>
 *   <property name="driverClassName" value="${driver}"/>
 *   <property name="url" value="jdbc:${dbname}"/>
 * </bean>
 * </pre>
 *
 * Example properties file:
 *
 * <pre class="code">driver=com.mysql.jdbc.Driver
 * dbname=mysql:mydb</pre>
 *
 * Annotated bean definitions may take advantage of property replacement using
 * the {@link org.springframework.beans.factory.annotation.Value @Value} annotation:
 *
 * <pre class="code">@Value("${person.age}")</pre>
 *
 * Implementations check simple property values, lists, maps, props, and bean names
 * in bean references. Furthermore, placeholder values can also cross-reference
 * other placeholders, like:
 *
 * <pre class="code">rootPath=myrootdir
 * subPath=${rootPath}/subdir</pre>
 *
 * In contrast to {@link PropertyOverrideConfigurer}, subclasses of this type allow
 * filling in of explicit placeholders in bean definitions.
 *
 * <p>If a configurer cannot resolve a placeholder, a {@link BeanDefinitionStoreException}
 * will be thrown. If you want to check against multiple properties files, specify multiple
 * resources via the {@link #setLocations locations} property. You can also define multiple
 * configurers, each with its <em>own</em> placeholder syntax. Use {@link
 * #ignoreUnresolvablePlaceholders} to intentionally suppress throwing an exception if a
 * placeholder cannot be resolved.
 *
 * <p>Default property values can be defined globally for each configurer instance
 * via the {@link #setProperties properties} property, or on a property-by-property basis
 * using the default value separator which is {@code ":"} by default and
 * customizable via {@link #setValueSeparator(String)}.
 *
 * <p>Example XML property with default value:
 *
 * <pre class="code">
 *   <property name="url" value="jdbc:${dbname:defaultdb}"/>
 * </pre>
 *
 * <p>
 * 在bean定义属性值中解析占位符的属性资源配置器的抽象基类实现从属性文件或其他{@linkplain orgspringframeworkcoreenvPropertySource属性源}将值</em>
 * 拉取到bean定义。
 * 
 *  <p>默认占位符语法遵循Ant / Log4J / JSP EL样式：
 * 
 *  <pre class ="code"> $ {} </pre>
 * 
 *  示例XML bean定义：
 * 
 * <pre class="code">
 * <bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource"/>
 * <property name="driverClassName" value="${driver}"/>
 * <property name="url" value="jdbc:${dbname}"/>
 * </bean>
 * </pre>
 * 
 *  示例属性文件：
 * 
 *  <pre class ="code"> driver = commysqljdbcDriver dbname = mysql：mydb </pre>
 * 
 *  注释的bean定义可以使用{@link orgspringframeworkbeansfactoryannotationValue @Value}注释来利用属性替换：
 * 
 *  <pre class ="code"> @ Value("$ {personage}")</pre>
 * 
 * 实现检查bean引用中的简单属性值,列表,地图,道具和bean名称此外,占位符值还可以交叉引用其他占位符,如：
 * 
 *  <pre class ="code"> rootPath = myrootdir subPath = $ {rootPath} / subdir </pre>
 * 
 *  与{@link PropertyOverrideConfigurer}相反,此类型的子类允许在bean定义中填充显式占位符
 * 
 * <p>如果配置程序无法解析占位符,则会抛出{@link BeanDefinitionStoreException}如果要检查多个属性文件,请通过{@link #setLocations locations}
 * 属性指定多个资源您还可以定义多个配置程序,每个都有自己的</em>占位符语法使用{@link #ignoreUnresolvablePlaceholders}有意地抑制如果占位符无法解析,则抛出异常。
 * 
 *  <p>默认属性值可以通过{@link #setProperties属性}属性为全局配置实例定义,也可以使用默认值为{@code"："}的默认值分隔符为属性逐个定义并可通过{@link #setValueSeparator(String)}
 * 
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 * @see PropertyPlaceholderConfigurer
 * @see org.springframework.context.support.PropertySourcesPlaceholderConfigurer
 */
public abstract class PlaceholderConfigurerSupport extends PropertyResourceConfigurer
		implements BeanNameAware, BeanFactoryAware {

	/** Default placeholder prefix: {@value} */
	public static final String DEFAULT_PLACEHOLDER_PREFIX = "${";

	/** Default placeholder suffix: {@value} */
	public static final String DEFAULT_PLACEHOLDER_SUFFIX = "}";

	/** Default value separator: {@value} */
	public static final String DEFAULT_VALUE_SEPARATOR = ":";


	/** Defaults to {@value #DEFAULT_PLACEHOLDER_PREFIX} */
	protected String placeholderPrefix = DEFAULT_PLACEHOLDER_PREFIX;

	/** Defaults to {@value #DEFAULT_PLACEHOLDER_SUFFIX} */
	protected String placeholderSuffix = DEFAULT_PLACEHOLDER_SUFFIX;

	/** Defaults to {@value #DEFAULT_VALUE_SEPARATOR} */
	protected String valueSeparator = DEFAULT_VALUE_SEPARATOR;

	protected boolean trimValues = false;

	protected String nullValue;

	protected boolean ignoreUnresolvablePlaceholders = false;

	private String beanName;

	private BeanFactory beanFactory;


	/**
	 * Set the prefix that a placeholder string starts with.
	 * The default is {@value #DEFAULT_PLACEHOLDER_PREFIX}.
	 * <p>
	 * 定制。
	 * 
	 *  <p>具有默认值的示例XML属性：
	 * 
	 * <pre class="code">
	 * <property name="url" value="jdbc:${dbname:defaultdb}"/>
	 * </pre>
	 * 
	 */
	public void setPlaceholderPrefix(String placeholderPrefix) {
		this.placeholderPrefix = placeholderPrefix;
	}

	/**
	 * Set the suffix that a placeholder string ends with.
	 * The default is {@value #DEFAULT_PLACEHOLDER_SUFFIX}.
	 * <p>
	 * 设置占位符字符串开始的前缀默认值为{@value #DEFAULT_PLACEHOLDER_PREFIX}
	 * 
	 */
	public void setPlaceholderSuffix(String placeholderSuffix) {
		this.placeholderSuffix = placeholderSuffix;
	}

	/**
	 * Specify the separating character between the placeholder variable
	 * and the associated default value, or {@code null} if no such
	 * special character should be processed as a value separator.
	 * The default is {@value #DEFAULT_VALUE_SEPARATOR}.
	 * <p>
	 *  设置占位符字符串结尾的后缀默认值为{@value #DEFAULT_PLACEHOLDER_SUFFIX}
	 * 
	 */
	public void setValueSeparator(String valueSeparator) {
		this.valueSeparator = valueSeparator;
	}

	/**
	 * Specify whether to trim resolved values before applying them,
	 * removing superfluous whitespace from the beginning and end.
	 * <p>Default is {@code false}.
	 * <p>
	 *  指定占位符变量和关联的默认值之间的分隔符,或者如果不将此类特殊字符作为值分隔符处理,则为{@code null}默认值为{@value #DEFAULT_VALUE_SEPARATOR}
	 * 
	 * 
	 * @since 4.3
	 */
	public void setTrimValues(boolean trimValues) {
		this.trimValues = trimValues;
	}

	/**
	 * Set a value that should be treated as {@code null} when resolved
	 * as a placeholder value: e.g. "" (empty String) or "null".
	 * <p>Note that this will only apply to full property values,
	 * not to parts of concatenated values.
	 * <p>By default, no such null value is defined. This means that
	 * there is no way to express {@code null} as a property value
	 * unless you explicitly map a corresponding value here.
	 * <p>
	 *  指定是否在应用之前修剪已解析的值,从头开始删除多余的空格和结束<p>默认值为{@code false}
	 * 
	 */
	public void setNullValue(String nullValue) {
		this.nullValue = nullValue;
	}

	/**
	 * Set whether to ignore unresolvable placeholders.
	 * <p>Default is "false": An exception will be thrown if a placeholder fails
	 * to resolve. Switch this flag to "true" in order to preserve the placeholder
	 * String as-is in such a case, leaving it up to other placeholder configurers
	 * to resolve it.
	 * <p>
	 * 设置一个值作为占位符值解析时应被视为{@code null}：例如""(空字符串)或"null"<p>请注意,这仅适用于全属性值,而不适用于连接的部分值<p>默认情况下,不定义这样的空值。
	 * 这意味着无法将{@code null}表示为属性值,除非您在此显式映射相应的值。
	 * 
	 */
	public void setIgnoreUnresolvablePlaceholders(boolean ignoreUnresolvablePlaceholders) {
		this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
	}

	/**
	 * Only necessary to check that we're not parsing our own bean definition,
	 * to avoid failing on unresolvable placeholders in properties file locations.
	 * The latter case can happen with placeholders for system properties in
	 * resource locations.
	 * <p>
	 *  设置是否忽略不可解决的占位符<p>默认值为"false"：如果占位符无法解析将此标志切换为"true",则将抛出异常,以便在这种情况下保留占位符字符串的原样。直到其他占位符配置程序来解决它
	 * 
	 * 
	 * @see #setLocations
	 * @see org.springframework.core.io.ResourceEditor
	 */
	@Override
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	/**
	 * Only necessary to check that we're not parsing our own bean definition,
	 * to avoid failing on unresolvable placeholders in properties file locations.
	 * The latter case can happen with placeholders for system properties in
	 * resource locations.
	 * <p>
	 * 只需检查我们不解析我们自己的bean定义,以避免属性文件位置中的不可解决的占位符发生故障后一种情况可能发生在资源位置的系统属性的占位符
	 * 
	 * 
	 * @see #setLocations
	 * @see org.springframework.core.io.ResourceEditor
	 */
	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}


	protected void doProcessProperties(ConfigurableListableBeanFactory beanFactoryToProcess,
			StringValueResolver valueResolver) {

		BeanDefinitionVisitor visitor = new BeanDefinitionVisitor(valueResolver);

		String[] beanNames = beanFactoryToProcess.getBeanDefinitionNames();
		for (String curName : beanNames) {
			// Check that we're not parsing our own bean definition,
			// to avoid failing on unresolvable placeholders in properties file locations.
			if (!(curName.equals(this.beanName) && beanFactoryToProcess.equals(this.beanFactory))) {
				BeanDefinition bd = beanFactoryToProcess.getBeanDefinition(curName);
				try {
					visitor.visitBeanDefinition(bd);
				}
				catch (Exception ex) {
					throw new BeanDefinitionStoreException(bd.getResourceDescription(), curName, ex.getMessage(), ex);
				}
			}
		}

		// New in Spring 2.5: resolve placeholders in alias target names and aliases as well.
		beanFactoryToProcess.resolveAliases(valueResolver);

		// New in Spring 3.0: resolve placeholders in embedded values such as annotation attributes.
		beanFactoryToProcess.addEmbeddedValueResolver(valueResolver);
	}

}
