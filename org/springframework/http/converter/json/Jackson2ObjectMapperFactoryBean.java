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

package org.springframework.http.converter.json;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * A {@link FactoryBean} for creating a Jackson 2.x {@link ObjectMapper} (default) or
 * {@link XmlMapper} ({@code createXmlMapper} property set to true) with setters
 * to enable or disable Jackson features from within XML configuration.
 *
 * <p>It customizes Jackson defaults properties with the following ones:
 * <ul>
 * <li>{@link MapperFeature#DEFAULT_VIEW_INCLUSION} is disabled</li>
 * <li>{@link DeserializationFeature#FAIL_ON_UNKNOWN_PROPERTIES} is disabled</li>
 * </ul>
 *
 * <p>Example usage with
 * {@link MappingJackson2HttpMessageConverter}:
 *
 * <pre class="code">
 * &lt;bean class="org.springframework.http.converter.json.MappingJackson2HttpMessageConverter">
 *   &lt;property name="objectMapper">
 *     &lt;bean class="org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean"
 *       p:autoDetectFields="false"
 *       p:autoDetectGettersSetters="false"
 *       p:annotationIntrospector-ref="jaxbAnnotationIntrospector" />
 *   &lt;/property>
 * &lt;/bean>
 * </pre>
 *
 * <p>Example usage with MappingJackson2JsonView:
 *
 * <pre class="code">
 * &lt;bean class="org.springframework.web.servlet.view.json.MappingJackson2JsonView">
 *   &lt;property name="objectMapper">
 *     &lt;bean class="org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean"
 *       p:failOnEmptyBeans="false"
 *       p:indentOutput="true">
 *       &lt;property name="serializers">
 *         &lt;array>
 *           &lt;bean class="org.mycompany.MyCustomSerializer" />
 *         &lt;/array>
 *       &lt;/property>
 *     &lt;/bean>
 *   &lt;/property>
 * &lt;/bean>
 * </pre>
 *
 * <p>In case there are no specific setters provided (for some rarely used options),
 * you can still use the more general methods  {@link #setFeaturesToEnable} and
 * {@link #setFeaturesToDisable}.
 *
 * <pre class="code">
 * &lt;bean class="org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean">
 *   &lt;property name="featuresToEnable">
 *     &lt;array>
 *       &lt;util:constant static-field="com.fasterxml.jackson.databind.SerializationFeature.WRAP_ROOT_VALUE"/>
 *       &lt;util:constant static-field="com.fasterxml.jackson.databind.SerializationFeature.CLOSE_CLOSEABLE"/>
 *     &lt;/array>
 *   &lt;/property>
 *   &lt;property name="featuresToDisable">
 *     &lt;array>
 *       &lt;util:constant static-field="com.fasterxml.jackson.databind.MapperFeature.USE_ANNOTATIONS"/>
 *     &lt;/array>
 *   &lt;/property>
 * &lt;/bean>
 * </pre>
 *
 * <p>It also automatically registers the following well-known modules if they are
 * detected on the classpath:
 * <ul>
 * <li><a href="https://github.com/FasterXML/jackson-datatype-jdk7">jackson-datatype-jdk7</a>: support for Java 7 types like {@link java.nio.file.Path}</li>
 * <li><a href="https://github.com/FasterXML/jackson-datatype-jdk8">jackson-datatype-jdk8</a>: support for other Java 8 types like {@link java.util.Optional}</li>
 * <li><a href="https://github.com/FasterXML/jackson-datatype-jsr310">jackson-datatype-jsr310</a>: support for Java 8 Date & Time API types</li>
 * <li><a href="https://github.com/FasterXML/jackson-datatype-joda">jackson-datatype-joda</a>: support for Joda-Time types</li>
 * <li><a href="https://github.com/FasterXML/jackson-module-kotlin">jackson-module-kotlin</a>: support for Kotlin classes and data classes</li>
 * </ul>
 *
 * <p>In case you want to configure Jackson's {@link ObjectMapper} with a custom {@link Module},
 * you can register one or more such Modules by class name via {@link #setModulesToInstall}:
 *
 * <pre class="code">
 * &lt;bean class="org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean">
 *   &lt;property name="modulesToInstall" value="myapp.jackson.MySampleModule,myapp.jackson.MyOtherModule"/>
 * &lt;/bean
 * </pre>
 *
 * <p>Compatible with Jackson 2.6 and higher, as of Spring 4.3.
 *
 * <p>
 * 使用setter创建Jackson 2x {@link ObjectMapper}(默认)或{@link XmlMapper}({@code createXmlMapper}属性设置为true))的{@link FactoryBean}
 * ,以在XML配置中启用或禁用Jackson功能。
 * 
 *  <p>它使用以下命令自定义Jackson默认属性：
 * <ul>
 *  <li> {@ link MapperFeature#DEFAULT_VIEW_INCLUSION}被禁用</li> <li> {@ link DeserializationFeature#FAIL_ON_UNKNOWN_PROPERTIES}
 * 被禁用</li>。
 * </ul>
 * 
 *  <p> {@link MappingJackson2HttpMessageConverter}的使用示例：
 * 
 * <pre class="code">
 * &lt;bean class="org.springframework.http.converter.json.MappingJackson2HttpMessageConverter">
 * &lt;property name="objectMapper">
 *  &lt; bean class ="orgspringframeworkhttpconverterjsonJackson2ObjectMapperFactoryBean"p：autoDetectFie
 * lds ="false"p：autoDetectGettersSetters ="false"。
 * p:annotationIntrospector-ref="jaxbAnnotationIntrospector" />
 * &lt;/property>
 * &lt;/bean>
 * </pre>
 * 
 *  <p> MappingJackson2JsonView的使用示例：
 * 
 * <pre class="code">
 * &lt;bean class="org.springframework.web.servlet.view.json.MappingJackson2JsonView">
 * &lt;property name="objectMapper">
 * &lt; bean class ="orgspringframeworkhttpconverterjsonJackson2ObjectMapperFactoryBean"p：failOnEmptyBea
 * ns ="false"。
 * p:indentOutput="true">
 * &lt;property name="serializers">
 * &lt;array>
 * &lt;bean class="org.mycompany.MyCustomSerializer" />
 * &lt;/array>
 * &lt;/property>
 * &lt;/bean>
 * &lt;/property>
 * &lt;/bean>
 * </pre>
 * 
 *  <p>如果没有提供特定的设置器(对于一些很少使用的选项),您仍然可以使用更一般的方法{@link #setFeaturesToEnable}和{@link #setFeaturesToDisable}
 * 。
 * 
 * <pre class="code">
 * &lt;bean class="org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean">
 * &lt;property name="featuresToEnable">
 * &lt;array>
 * &lt;util:constant static-field="com.fasterxml.jackson.databind.SerializationFeature.WRAP_ROOT_VALUE"/>
 * &lt;util:constant static-field="com.fasterxml.jackson.databind.SerializationFeature.CLOSE_CLOSEABLE"/>
 * &lt;/array>
 * &lt;/property>
 * &lt;property name="featuresToDisable">
 * &lt;array>
 * &lt;util:constant static-field="com.fasterxml.jackson.databind.MapperFeature.USE_ANNOTATIONS"/>
 * &lt;/array>
 * &lt;/property>
 * &lt;/bean>
 * </pre>
 * 
 *  <p>如果在类路径中检测到它们,它还会自动注册以下众所周知的模块：
 * <ul>
 * <li> <a href=\"https://githubcom/FasterXML/jackson-datatype-jdk7\"> jackson-datatype-jdk7 </a>：支持Java
 *  7类型,如{@link javaniofilePath} </li> <li > <a href=\"https://githubcom/FasterXML/jackson-datatype-jdk8\">
 *  jackson-datatype-jdk8 </a>：支持其他Java 8类型,例如{@link javautilOptional} </li> <li> <a href=\"https://githubcom/FasterXML/jackson-datatype-jsr310\">
 *  jackson-datatype-jsr310 </a>：支持Java 8日期和时间API类型</li> <li> <a href = "https：// githubcom / FasterXML / jackson-datatype-joda">
 *  jackson-datatype-joda </a>：支持Joda-Time类型</li> <li> <a href ="https：// githubcom / fasterXML / jackson-module-kotlin">
 *  jackson-module-kotlin </a>：支持Kotlin类和数据类</li>。
 * </ul>
 * 
 * <p>如果您想使用自定义{@link模块}配置Jackson的{@link ObjectMapper},则可以通过{@link #setModulesToInstall}通过类名注册一个或多个此类模块：
 * 。
 * 
 * <pre class="code">
 * &lt;bean class="org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean">
 * &lt;property name="modulesToInstall" value="myapp.jackson.MySampleModule,myapp.jackson.MyOtherModule"/>
 *  &LT; /豆腐
 * </pre>
 * 
 *  <p>兼容于Jackson 26及更高版本,截至春季43
 * 
 * 
 * @author <a href="mailto:dmitry.katsubo@gmail.com">Dmitry Katsubo</a>
 * @author Rossen Stoyanchev
 * @author Brian Clozel
 * @author Juergen Hoeller
 * @author Tadaya Tsuyukubo
 * @author Sebastien Deleuze
 * @since 3.2
 */
public class Jackson2ObjectMapperFactoryBean implements FactoryBean<ObjectMapper>, BeanClassLoaderAware,
		ApplicationContextAware, InitializingBean {

	private final Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();

	private ObjectMapper objectMapper;


	/**
	 * Set the {@link ObjectMapper} instance to use. If not set, the {@link ObjectMapper}
	 * will be created using its default constructor.
	 * <p>
	 *  将{@link ObjectMapper}实例设置为使用如果未设置,将使用其默认构造函数创建{@link ObjectMapper}
	 * 
	 */
	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	/**
	 * If set to true and no custom {@link ObjectMapper} has been set, a {@link XmlMapper}
	 * will be created using its default constructor.
	 * <p>
	 *  如果设置为true,并且未设置自定义{@link ObjectMapper},则将使用其默认构造函数创建{@link XmlMapper}
	 * 
	 * 
	 * @since 4.1
	 */
	public void setCreateXmlMapper(boolean createXmlMapper) {
		this.builder.createXmlMapper(createXmlMapper);
	}

	/**
	 * Define the format for date/time with the given {@link DateFormat}.
	 * <p>Note: Setting this property makes the exposed {@link ObjectMapper}
	 * non-thread-safe, according to Jackson's thread safety rules.
	 * <p>
	 *  使用给定的{@link DateFormat} <p>定义日期/时间的格式注意：根据杰克逊的线程安全规则,设置此属性使得显示的{@link ObjectMapper}非线程安全
	 * 
	 * 
	 * @see #setSimpleDateFormat(String)
	 */
	public void setDateFormat(DateFormat dateFormat) {
		this.builder.dateFormat(dateFormat);
	}

	/**
	 * Define the date/time format with a {@link SimpleDateFormat}.
	 * <p>Note: Setting this property makes the exposed {@link ObjectMapper}
	 * non-thread-safe, according to Jackson's thread safety rules.
	 * <p>
	 * 使用{@link SimpleDateFormat} <p>定义日期/时间格式注意：根据杰克逊的线程安全规则,设置此属性使得显示的{@link ObjectMapper}非线程安全
	 * 
	 * 
	 * @see #setDateFormat(DateFormat)
	 */
	public void setSimpleDateFormat(String format) {
		this.builder.simpleDateFormat(format);
	}

	/**
	 * Override the default {@link Locale} to use for formatting.
	 * Default value used is {@link Locale#getDefault()}.
	 * <p>
	 *  覆盖默认{@link Locale}用于格式化使用的默认值为{@link Locale#getDefault()}
	 * 
	 * 
	 * @since 4.1.5
	 */
	public void setLocale(Locale locale) {
		this.builder.locale(locale);
	}

	/**
	 * Override the default {@link TimeZone} to use for formatting.
	 * Default value used is UTC (NOT local timezone).
	 * <p>
	 *  覆盖默认的{@link TimeZone}用于格式化使用的默认值为UTC(非本地时区)
	 * 
	 * 
	 * @since 4.1.5
	 */
	public void setTimeZone(TimeZone timeZone) {
		this.builder.timeZone(timeZone);
	}

	/**
	 * Set an {@link AnnotationIntrospector} for both serialization and deserialization.
	 * <p>
	 *  设置一个{@link AnnotationIntrospector}用于序列化和反序列化
	 * 
	 */
	public void setAnnotationIntrospector(AnnotationIntrospector annotationIntrospector) {
		this.builder.annotationIntrospector(annotationIntrospector);
	}

	/**
	 * Specify a {@link com.fasterxml.jackson.databind.PropertyNamingStrategy} to
	 * configure the {@link ObjectMapper} with.
	 * <p>
	 *  指定一个{@link comfasterxmljacksondatabindPropertyNamingStrategy}来配置{@link ObjectMapper}与
	 * 
	 * 
	 * @since 4.0.2
	 */
	public void setPropertyNamingStrategy(PropertyNamingStrategy propertyNamingStrategy) {
		this.builder.propertyNamingStrategy(propertyNamingStrategy);
	}

	/**
	 * Specify a {@link TypeResolverBuilder} to use for Jackson's default typing.
	 * <p>
	 *  指定一个{@link TypeResolverBuilder}用于Jackson的默认输入
	 * 
	 * 
	 * @since 4.2.2
	 */
	public void setDefaultTyping(TypeResolverBuilder<?> typeResolverBuilder) {
		this.builder.defaultTyping(typeResolverBuilder);
	}

	/**
	 * Set a custom inclusion strategy for serialization.
	 * <p>
	 *  为序列化设置自定义包含策略
	 * 
	 * 
	 * @see com.fasterxml.jackson.annotation.JsonInclude.Include
	 */
	public void setSerializationInclusion(JsonInclude.Include serializationInclusion) {
		this.builder.serializationInclusion(serializationInclusion);
	}

	/**
	 * Set the global filters to use in order to support {@link JsonFilter @JsonFilter} annotated POJO.
	 * <p>
	 * 设置全局过滤器以便支持{@link JsonFilter @JsonFilter}注释的POJO
	 * 
	 * 
	 * @since 4.2
	 * @see Jackson2ObjectMapperBuilder#filters(FilterProvider)
	 */
	public void setFilters(FilterProvider filters) {
		this.builder.filters(filters);
	}

	/**
	 * Add mix-in annotations to use for augmenting specified class or interface.
	 * <p>
	 *  添加混合注释以用于扩充指定的类或接口
	 * 
	 * 
	 * @param mixIns Map of entries with target classes (or interface) whose annotations
	 * to effectively override as key and mix-in classes (or interface) whose
	 * annotations are to be "added" to target's annotations as value.
	 * @since 4.1.2
	 * @see com.fasterxml.jackson.databind.ObjectMapper#addMixInAnnotations(Class, Class)
	 */
	public void setMixIns(Map<Class<?>, Class<?>> mixIns) {
		this.builder.mixIns(mixIns);
	}

	/**
	 * Configure custom serializers. Each serializer is registered for the type
	 * returned by {@link JsonSerializer#handledType()}, which must not be {@code null}.
	 * <p>
	 *  配置自定义序列化器每个序列化器都是由{@link JsonSerializer#processedType()}返回的类型注册的,不能为{@code null}
	 * 
	 * 
	 * @see #setSerializersByType(Map)
	 */
	public void setSerializers(JsonSerializer<?>... serializers) {
		this.builder.serializers(serializers);
	}

	/**
	 * Configure custom serializers for the given types.
	 * <p>
	 *  为给定类型配置自定义序列化程序
	 * 
	 * 
	 * @see #setSerializers(JsonSerializer...)
	 */
	public void setSerializersByType(Map<Class<?>, JsonSerializer<?>> serializers) {
		this.builder.serializersByType(serializers);
	}

	/**
	 * Configure custom deserializers. Each deserializer is registered for the type
	 * returned by {@link JsonDeserializer#handledType()}, which must not be {@code null}.
	 * <p>
	 *  配置自定义解串器每个解串器都注册为{@link JsonDeserializer#processedType()}返回的类型,不能为{@code null}
	 * 
	 * 
	 * @since 4.3
	 * @see #setDeserializersByType(Map)
	 */
	public void setDeserializers(JsonDeserializer<?>... deserializers) {
		this.builder.deserializers(deserializers);
	}

	/**
	 * Configure custom deserializers for the given types.
	 * <p>
	 *  为给定类型配置自定义解串器
	 * 
	 */
	public void setDeserializersByType(Map<Class<?>, JsonDeserializer<?>> deserializers) {
		this.builder.deserializersByType(deserializers);
	}

	/**
	 * Shortcut for {@link MapperFeature#AUTO_DETECT_FIELDS} option.
	 * <p>
	 *  快捷方式{@link MapperFeature#AUTO_DETECT_FIELDS}选项
	 * 
	 */
	public void setAutoDetectFields(boolean autoDetectFields) {
		this.builder.autoDetectFields(autoDetectFields);
	}

	/**
	 * Shortcut for {@link MapperFeature#AUTO_DETECT_SETTERS}/
	 * {@link MapperFeature#AUTO_DETECT_GETTERS}/{@link MapperFeature#AUTO_DETECT_IS_GETTERS}
	 * options.
	 * <p>
	 * {@link MapperFeature#AUTO_DETECT_SETTERS} / {@link MapperFeature#AUTO_DETECT_GETTERS} / {@ link MapperFeature#AUTO_DETECT_IS_GETTERS}
	 * 选项的快捷方式。
	 * 
	 */
	public void setAutoDetectGettersSetters(boolean autoDetectGettersSetters) {
		this.builder.autoDetectGettersSetters(autoDetectGettersSetters);
	}

	/**
	 * Shortcut for {@link MapperFeature#DEFAULT_VIEW_INCLUSION} option.
	 * <p>
	 *  快捷方式{@link MapperFeature#DEFAULT_VIEW_INCLUSION}选项
	 * 
	 * 
	 * @since 4.1
	 */
	public void setDefaultViewInclusion(boolean defaultViewInclusion) {
		this.builder.defaultViewInclusion(defaultViewInclusion);
	}

	/**
	 * Shortcut for {@link DeserializationFeature#FAIL_ON_UNKNOWN_PROPERTIES} option.
	 * <p>
	 *  快捷方式{@link DeserializationFeature#FAIL_ON_UNKNOWN_PROPERTIES}选项
	 * 
	 * 
	 * @since 4.1.1
	 */
	public void setFailOnUnknownProperties(boolean failOnUnknownProperties) {
		this.builder.failOnUnknownProperties(failOnUnknownProperties);
	}

	/**
	 * Shortcut for {@link SerializationFeature#FAIL_ON_EMPTY_BEANS} option.
	 * <p>
	 *  快捷方式{@link SerializationFeature#FAIL_ON_EMPTY_BEANS}选项
	 * 
	 */
	public void setFailOnEmptyBeans(boolean failOnEmptyBeans) {
		this.builder.failOnEmptyBeans(failOnEmptyBeans);
	}

	/**
	 * Shortcut for {@link SerializationFeature#INDENT_OUTPUT} option.
	 * <p>
	 *  {@link SerializationFeature#INDENT_OUTPUT}选项的快捷方式
	 * 
	 */
	public void setIndentOutput(boolean indentOutput) {
		this.builder.indentOutput(indentOutput);
	}

	/**
	 * Define if a wrapper will be used for indexed (List, array) properties or not by
	 * default (only applies to {@link XmlMapper}).
	 * <p>
	 *  定义如果默认情况下是否将包装器用于索引(List,数组)属性(仅适用于{@link XmlMapper})
	 * 
	 * 
	 * @since 4.3
	 */
	public void setDefaultUseWrapper(boolean defaultUseWrapper) {
		this.builder.defaultUseWrapper(defaultUseWrapper);
	}

	/**
	 * Specify features to enable.
	 * <p>
	 *  指定要启用的功能
	 * 
	 * 
	 * @see com.fasterxml.jackson.core.JsonParser.Feature
	 * @see com.fasterxml.jackson.core.JsonGenerator.Feature
	 * @see com.fasterxml.jackson.databind.SerializationFeature
	 * @see com.fasterxml.jackson.databind.DeserializationFeature
	 * @see com.fasterxml.jackson.databind.MapperFeature
	 */
	public void setFeaturesToEnable(Object... featuresToEnable) {
		this.builder.featuresToEnable(featuresToEnable);
	}

	/**
	 * Specify features to disable.
	 * <p>
	 *  指定要禁用的功能
	 * 
	 * 
	 * @see com.fasterxml.jackson.core.JsonParser.Feature
	 * @see com.fasterxml.jackson.core.JsonGenerator.Feature
	 * @see com.fasterxml.jackson.databind.SerializationFeature
	 * @see com.fasterxml.jackson.databind.DeserializationFeature
	 * @see com.fasterxml.jackson.databind.MapperFeature
	 */
	public void setFeaturesToDisable(Object... featuresToDisable) {
		this.builder.featuresToDisable(featuresToDisable);
	}

	/**
	 * Set a complete list of modules to be registered with the {@link ObjectMapper}.
	 * <p>Note: If this is set, no finding of modules is going to happen - not by
	 * Jackson, and not by Spring either (see {@link #setFindModulesViaServiceLoader}).
	 * As a consequence, specifying an empty list here will suppress any kind of
	 * module detection.
	 * <p>Specify either this or {@link #setModulesToInstall}, not both.
	 * <p>
	 * 设置要在{@link ObjectMapper} <p>注册的模块的完整列表注意：如果这样设置,则不会发现模块的发现 - 而不是由Jackson,而不是Spring(请参阅{@link# setFindModulesViaServiceLoader}
	 * )因此,在此处指定一个空列表将禁止任何类型的模块检测<p>指定此或{@link #setModulesToInstall},而不是两者。
	 * 
	 * 
	 * @since 4.0
	 * @see com.fasterxml.jackson.databind.Module
	 */
	public void setModules(List<Module> modules) {
		this.builder.modules(modules);
	}

	/**
	 * Specify one or more modules by class (or class name in XML)
	 * to be registered with the {@link ObjectMapper}.
	 * <p>Modules specified here will be registered after
	 * Spring's autodetection of JSR-310 and Joda-Time, or Jackson's
	 * finding of modules (see {@link #setFindModulesViaServiceLoader}),
	 * allowing to eventually override their configuration.
	 * <p>Specify either this or {@link #setModules}, not both.
	 * <p>
	 *  通过类(或XML中的类名称)指定一个或多个模块,以注册到{@link ObjectMapper} <p>此处指定的模块将在Spring自动检测JSR-310和Joda-Time之后注册,或者Jacks
	 * on的模块查找(请参见{@link #setFindModulesViaServiceLoader}),允许最终覆盖其配置<p>指定此或{@link #setModules},而不是两者。
	 * 
	 * 
	 * @since 4.0.1
	 * @see com.fasterxml.jackson.databind.Module
	 */
	@SuppressWarnings("unchecked")
	public void setModulesToInstall(Class<? extends Module>... modules) {
		this.builder.modulesToInstall(modules);
	}

	/**
	 * Set whether to let Jackson find available modules via the JDK ServiceLoader,
	 * based on META-INF metadata in the classpath. Requires Jackson 2.2 or higher.
	 * <p>If this mode is not set, Spring's Jackson2ObjectMapperFactoryBean itself
	 * will try to find the JSR-310 and Joda-Time support modules on the classpath -
	 * provided that Java 8 and Joda-Time themselves are available, respectively.
	 * <p>
	 * 设置是否让Jackson通过JDK ServiceLoader找到可用的模块,基于类路径中的META-INF元数据需要Jackson 22或更高版本如果没有设置此模式,Spring的Jackson2Ob
	 * jectMapperFactoryBean本身将尝试找到JSR-310和Joda -time支持类路径上的模块,只要Java 8和Joda-Time本身分别可用。
	 * 
	 * 
	 * @since 4.0.1
	 * @see com.fasterxml.jackson.databind.ObjectMapper#findModules()
	 */
	public void setFindModulesViaServiceLoader(boolean findModules) {
		this.builder.findModulesViaServiceLoader(findModules);
	}

	@Override
	public void setBeanClassLoader(ClassLoader beanClassLoader) {
		this.builder.moduleClassLoader(beanClassLoader);
	}

	/**
	 * Customize the construction of Jackson handlers
	 * ({@link JsonSerializer}, {@link JsonDeserializer}, {@link KeyDeserializer},
	 * {@code TypeResolverBuilder} and {@code TypeIdResolver}).
	 * <p>
	 *  定制杰克逊处理程序({@link JsonSerializer},{@link JsonDeserializer},{@link KeyDeserializer},{@code TypeResolverBuilder}
	 * 和{@code TypeIdResolver}))的构建。
	 * 
	 * 
	 * @since 4.1.3
	 * @see Jackson2ObjectMapperFactoryBean#setApplicationContext(ApplicationContext)
	 */
	public void setHandlerInstantiator(HandlerInstantiator handlerInstantiator) {
		this.builder.handlerInstantiator(handlerInstantiator);
	}

	/**
	 * Set the builder {@link ApplicationContext} in order to autowire Jackson handlers
	 * ({@link JsonSerializer}, {@link JsonDeserializer}, {@link KeyDeserializer},
	 * {@code TypeResolverBuilder} and {@code TypeIdResolver}).
	 * <p>
	 *  设置构建器{@link ApplicationContext},以便自动连接杰克逊处理程序({@link JsonSerializer},{@link JsonDeserializer},{@link KeyDeserializer}
	 * ,{@code TypeResolverBuilder}和{@code TypeIdResolver})。
	 * 
	 * 
	 * @since 4.1.3
	 * @see Jackson2ObjectMapperBuilder#applicationContext(ApplicationContext)
	 * @see SpringHandlerInstantiator
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.builder.applicationContext(applicationContext);
	}


	@Override
	public void afterPropertiesSet() {
		if (this.objectMapper != null) {
			this.builder.configure(this.objectMapper);
		}
		else {
			this.objectMapper = this.builder.build();
		}
	}

	/**
	 * Return the singleton ObjectMapper.
	 * <p>
	 */
	@Override
	public ObjectMapper getObject() {
		return this.objectMapper;
	}

	@Override
	public Class<?> getObjectType() {
		return (this.objectMapper != null ? this.objectMapper.getClass() : null);
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
