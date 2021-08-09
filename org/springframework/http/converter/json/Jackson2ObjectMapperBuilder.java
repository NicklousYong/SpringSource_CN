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
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLResolver;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
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
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.FatalBeanException;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

/**
 * A builder used to create {@link ObjectMapper} instances with a fluent API.
 *
 * <p>It customizes Jackson's default properties with the following ones:
 * <ul>
 * <li>{@link MapperFeature#DEFAULT_VIEW_INCLUSION} is disabled</li>
 * <li>{@link DeserializationFeature#FAIL_ON_UNKNOWN_PROPERTIES} is disabled</li>
 * </ul>
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
 * <p>Compatible with Jackson 2.6 and higher, as of Spring 4.3.
 *
 * <p>
 *  用于使用流畅的API创建{@link ObjectMapper}实例的构建器
 * 
 *  <p>它使用以下命令自定义Jackson的默认属性：
 * <ul>
 * <li> {@ link MapperFeature#DEFAULT_VIEW_INCLUSION}被禁用</li> <li> {@ link DeserializationFeature#FAIL_ON_UNKNOWN_PROPERTIES}
 * 被禁用</li>。
 * </ul>
 * 
 *  <p>如果在类路径中检测到它们,它还会自动注册以下众所周知的模块：
 * <ul>
 * <li> <a href=\"https://githubcom/FasterXML/jackson-datatype-jdk7\"> jackson-datatype-jdk7 </a>：支持Java
 *  7类型,如{@link javaniofilePath} </li> <li > <a href=\"https://githubcom/FasterXML/jackson-datatype-jdk8\">
 *  jackson-datatype-jdk8 </a>：支持其他Java 8类型,如{@link javautilOptional} </li> <li> <a href=\"https://githubcom/FasterXML/jackson-datatype-jsr310\">
 *  jackson-datatype-jsr310 </a>：支持Java 8日期和时间API类型</li> <li> <a href = "https：// githubcom / FasterXML / jackson-datatype-joda">
 *  jackson-datatype-joda </a>：支持Joda-Time类型</li> <li> <a href ="https：// githubcom / fasterXML / jackson-module-kotlin">
 *  jackson-module-kotlin </a>：支持Kotlin类和数据类</li>。
 * </ul>
 * 
 *  <p>兼容于Jackson 26及更高版本,截至春季43
 * 
 * 
 * @author Sebastien Deleuze
 * @author Juergen Hoeller
 * @author Tadaya Tsuyukubo
 * @since 4.1.1
 * @see #build()
 * @see #configure(ObjectMapper)
 * @see Jackson2ObjectMapperFactoryBean
 */
public class Jackson2ObjectMapperBuilder {

	private boolean createXmlMapper = false;

	private DateFormat dateFormat;

	private Locale locale;

	private TimeZone timeZone;

	private AnnotationIntrospector annotationIntrospector;

	private PropertyNamingStrategy propertyNamingStrategy;

	private TypeResolverBuilder<?> defaultTyping;

	private JsonInclude.Include serializationInclusion;

	private FilterProvider filters;

	private final Map<Class<?>, Class<?>> mixIns = new HashMap<Class<?>, Class<?>>();

	private final Map<Class<?>, JsonSerializer<?>> serializers = new LinkedHashMap<Class<?>, JsonSerializer<?>>();

	private final Map<Class<?>, JsonDeserializer<?>> deserializers = new LinkedHashMap<Class<?>, JsonDeserializer<?>>();

	private final Map<Object, Boolean> features = new HashMap<Object, Boolean>();

	private List<Module> modules;

	private Class<? extends Module>[] moduleClasses;

	private boolean findModulesViaServiceLoader = false;

	private boolean findWellKnownModules = true;

	private ClassLoader moduleClassLoader = getClass().getClassLoader();

	private HandlerInstantiator handlerInstantiator;

	private ApplicationContext applicationContext;

	private Boolean defaultUseWrapper;


	/**
	 * If set to {@code true}, an {@link XmlMapper} will be created using its
	 * default constructor. This is only applicable to {@link #build()} calls,
	 * not to {@link #configure} calls.
	 * <p>
	 * 如果设置为{@code true},将使用其默认构造函数创建一个{@link XmlMapper}这仅适用于{@link #build()}调用,而不适用于{@link #configure}调用
	 * 
	 */
	public Jackson2ObjectMapperBuilder createXmlMapper(boolean createXmlMapper) {
		this.createXmlMapper = createXmlMapper;
		return this;
	}

	/**
	 * Define the format for date/time with the given {@link DateFormat}.
	 * <p>Note: Setting this property makes the exposed {@link ObjectMapper}
	 * non-thread-safe, according to Jackson's thread safety rules.
	 * <p>
	 *  使用给定的{@link DateFormat} <p>定义日期/时间的格式注意：根据杰克逊的线程安全规则,设置此属性使得显示的{@link ObjectMapper}非线程安全
	 * 
	 * 
	 * @see #simpleDateFormat(String)
	 */
	public Jackson2ObjectMapperBuilder dateFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
		return this;
	}

	/**
	 * Define the date/time format with a {@link SimpleDateFormat}.
	 * <p>Note: Setting this property makes the exposed {@link ObjectMapper}
	 * non-thread-safe, according to Jackson's thread safety rules.
	 * <p>
	 *  使用{@link SimpleDateFormat} <p>定义日期/时间格式注意：根据杰克逊的线程安全规则,设置此属性使得显示的{@link ObjectMapper}非线程安全
	 * 
	 * 
	 * @see #dateFormat(DateFormat)
	 */
	public Jackson2ObjectMapperBuilder simpleDateFormat(String format) {
		this.dateFormat = new SimpleDateFormat(format);
		return this;
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
	public Jackson2ObjectMapperBuilder locale(Locale locale) {
		this.locale = locale;
		return this;
	}

	/**
	 * Override the default {@link Locale} to use for formatting.
	 * Default value used is {@link Locale#getDefault()}.
	 * <p>
	 *  覆盖默认{@link Locale}用于格式化使用的默认值为{@link Locale#getDefault()}
	 * 
	 * 
	 * @param localeString the locale ID as a String representation
	 * @since 4.1.5
	 */
	public Jackson2ObjectMapperBuilder locale(String localeString) {
		this.locale = StringUtils.parseLocaleString(localeString);
		return this;
	}

	/**
	 * Override the default {@link TimeZone} to use for formatting.
	 * Default value used is UTC (NOT local timezone).
	 * <p>
	 * 覆盖默认的{@link TimeZone}用于格式化使用的默认值为UTC(非本地时区)
	 * 
	 * 
	 * @since 4.1.5
	 */
	public Jackson2ObjectMapperBuilder timeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
		return this;
	}

	/**
	 * Override the default {@link TimeZone} to use for formatting.
	 * Default value used is UTC (NOT local timezone).
	 * <p>
	 *  覆盖默认的{@link TimeZone}用于格式化使用的默认值为UTC(非本地时区)
	 * 
	 * 
	 * @param timeZoneString the zone ID as a String representation
	 * @since 4.1.5
	 */
	public Jackson2ObjectMapperBuilder timeZone(String timeZoneString) {
		this.timeZone = StringUtils.parseTimeZoneString(timeZoneString);
		return this;
	}

	/**
	 * Set an {@link AnnotationIntrospector} for both serialization and deserialization.
	 * <p>
	 *  设置一个{@link AnnotationIntrospector}用于序列化和反序列化
	 * 
	 */
	public Jackson2ObjectMapperBuilder annotationIntrospector(AnnotationIntrospector annotationIntrospector) {
		this.annotationIntrospector = annotationIntrospector;
		return this;
	}

	/**
	 * Specify a {@link com.fasterxml.jackson.databind.PropertyNamingStrategy} to
	 * configure the {@link ObjectMapper} with.
	 * <p>
	 *  指定一个{@link comfasterxmljacksondatabindPropertyNamingStrategy}来配置{@link ObjectMapper}与
	 * 
	 */
	public Jackson2ObjectMapperBuilder propertyNamingStrategy(PropertyNamingStrategy propertyNamingStrategy) {
		this.propertyNamingStrategy = propertyNamingStrategy;
		return this;
	}

	/**
	 * Specify a {@link TypeResolverBuilder} to use for Jackson's default typing.
	 * <p>
	 *  指定一个{@link TypeResolverBuilder}用于Jackson的默认输入
	 * 
	 * 
	 * @since 4.2.2
	 */
	public Jackson2ObjectMapperBuilder defaultTyping(TypeResolverBuilder<?> typeResolverBuilder) {
		this.defaultTyping = typeResolverBuilder;
		return this;
	}

	/**
	 * Set a custom inclusion strategy for serialization.
	 * <p>
	 *  为序列化设置自定义包含策略
	 * 
	 * 
	 * @see com.fasterxml.jackson.annotation.JsonInclude.Include
	 */
	public Jackson2ObjectMapperBuilder serializationInclusion(JsonInclude.Include serializationInclusion) {
		this.serializationInclusion = serializationInclusion;
		return this;
	}

	/**
	 * Set the global filters to use in order to support {@link JsonFilter @JsonFilter} annotated POJO.
	 * <p>
	 *  设置全局过滤器以便支持{@link JsonFilter @JsonFilter}注释的POJO
	 * 
	 * 
	 * @since 4.2
	 * @see MappingJacksonValue#setFilters(FilterProvider)
	 */
	public Jackson2ObjectMapperBuilder filters(FilterProvider filters) {
		this.filters = filters;
		return this;
	}

	/**
	 * Add mix-in annotations to use for augmenting specified class or interface.
	 * <p>
	 *  添加混合注释以用于扩充指定的类或接口
	 * 
	 * 
	 * @param target class (or interface) whose annotations to effectively override
	 * @param mixinSource class (or interface) whose annotations are to be "added"
	 * to target's annotations as value
	 * @since 4.1.2
	 * @see com.fasterxml.jackson.databind.ObjectMapper#addMixInAnnotations(Class, Class)
	 */
	public Jackson2ObjectMapperBuilder mixIn(Class<?> target, Class<?> mixinSource) {
		if (mixinSource != null) {
			this.mixIns.put(target, mixinSource);
		}
		return this;
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
	public Jackson2ObjectMapperBuilder mixIns(Map<Class<?>, Class<?>> mixIns) {
		if (mixIns != null) {
			this.mixIns.putAll(mixIns);
		}
		return this;
	}

	/**
	 * Configure custom serializers. Each serializer is registered for the type
	 * returned by {@link JsonSerializer#handledType()}, which must not be {@code null}.
	 * <p>
	 * 配置自定义序列化器每个序列化器都是由{@link JsonSerializer#processedType()}返回的类型注册的,不能为{@code null}
	 * 
	 * 
	 * @see #serializersByType(Map)
	 */
	public Jackson2ObjectMapperBuilder serializers(JsonSerializer<?>... serializers) {
		if (serializers != null) {
			for (JsonSerializer<?> serializer : serializers) {
				Class<?> handledType = serializer.handledType();
				if (handledType == null || handledType == Object.class) {
					throw new IllegalArgumentException("Unknown handled type in " + serializer.getClass().getName());
				}
				this.serializers.put(serializer.handledType(), serializer);
			}
		}
		return this;
	}

	/**
	 * Configure a custom serializer for the given type.
	 * <p>
	 *  为给定类型配置自定义序列化程序
	 * 
	 * 
	 * @see #serializers(JsonSerializer...)
	 * @since 4.1.2
	 */
	public Jackson2ObjectMapperBuilder serializerByType(Class<?> type, JsonSerializer<?> serializer) {
		if (serializer != null) {
			this.serializers.put(type, serializer);
		}
		return this;
	}

	/**
	 * Configure custom serializers for the given types.
	 * <p>
	 *  为给定类型配置自定义序列化程序
	 * 
	 * 
	 * @see #serializers(JsonSerializer...)
	 */
	public Jackson2ObjectMapperBuilder serializersByType(Map<Class<?>, JsonSerializer<?>> serializers) {
		if (serializers != null) {
			this.serializers.putAll(serializers);
		}
		return this;
	}

	/**
	 * Configure custom deserializers. Each deserializer is registered for the type
	 * returned by {@link JsonDeserializer#handledType()}, which must not be {@code null}.
	 * <p>
	 *  配置自定义解串器每个解串器都注册为{@link JsonDeserializer#processedType()}返回的类型,不能为{@code null}
	 * 
	 * 
	 * @since 4.3
	 * @see #deserializersByType(Map)
	 */
	public Jackson2ObjectMapperBuilder deserializers(JsonDeserializer<?>... deserializers) {
		if (deserializers != null) {
			for (JsonDeserializer<?> deserializer : deserializers) {
				Class<?> handledType = deserializer.handledType();
				if (handledType == null || handledType == Object.class) {
					throw new IllegalArgumentException("Unknown handled type in " + deserializer.getClass().getName());
				}
				this.deserializers.put(deserializer.handledType(), deserializer);
			}
		}
		return this;
	}

	/**
	 * Configure a custom deserializer for the given type.
	 * <p>
	 *  为给定类型配置自定义解串器
	 * 
	 * 
	 * @since 4.1.2
	 */
	public Jackson2ObjectMapperBuilder deserializerByType(Class<?> type, JsonDeserializer<?> deserializer) {
		if (deserializer != null) {
			this.deserializers.put(type, deserializer);
		}
		return this;
	}

	/**
	 * Configure custom deserializers for the given types.
	 * <p>
	 *  为给定类型配置自定义解串器
	 * 
	 */
	public Jackson2ObjectMapperBuilder deserializersByType(Map<Class<?>, JsonDeserializer<?>> deserializers) {
		if (deserializers != null) {
			this.deserializers.putAll(deserializers);
		}
		return this;
	}

	/**
	 * Shortcut for {@link MapperFeature#AUTO_DETECT_FIELDS} option.
	 * <p>
	 *  快捷方式{@link MapperFeature#AUTO_DETECT_FIELDS}选项
	 * 
	 */
	public Jackson2ObjectMapperBuilder autoDetectFields(boolean autoDetectFields) {
		this.features.put(MapperFeature.AUTO_DETECT_FIELDS, autoDetectFields);
		return this;
	}

	/**
	 * Shortcut for {@link MapperFeature#AUTO_DETECT_SETTERS}/
	 * {@link MapperFeature#AUTO_DETECT_GETTERS}/{@link MapperFeature#AUTO_DETECT_IS_GETTERS}
	 * options.
	 * <p>
	 *  {@link MapperFeature#AUTO_DETECT_SETTERS} / {@link MapperFeature#AUTO_DETECT_GETTERS} / {@ link MapperFeature#AUTO_DETECT_IS_GETTERS}
	 * 选项的快捷方式。
	 * 
	 */
	public Jackson2ObjectMapperBuilder autoDetectGettersSetters(boolean autoDetectGettersSetters) {
		this.features.put(MapperFeature.AUTO_DETECT_GETTERS, autoDetectGettersSetters);
		this.features.put(MapperFeature.AUTO_DETECT_SETTERS, autoDetectGettersSetters);
		this.features.put(MapperFeature.AUTO_DETECT_IS_GETTERS, autoDetectGettersSetters);
		return this;
	}

	/**
	 * Shortcut for {@link MapperFeature#DEFAULT_VIEW_INCLUSION} option.
	 * <p>
	 * 快捷方式{@link MapperFeature#DEFAULT_VIEW_INCLUSION}选项
	 * 
	 */
	public Jackson2ObjectMapperBuilder defaultViewInclusion(boolean defaultViewInclusion) {
		this.features.put(MapperFeature.DEFAULT_VIEW_INCLUSION, defaultViewInclusion);
		return this;
	}

	/**
	 * Shortcut for {@link DeserializationFeature#FAIL_ON_UNKNOWN_PROPERTIES} option.
	 * <p>
	 *  快捷方式{@link DeserializationFeature#FAIL_ON_UNKNOWN_PROPERTIES}选项
	 * 
	 */
	public Jackson2ObjectMapperBuilder failOnUnknownProperties(boolean failOnUnknownProperties) {
		this.features.put(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, failOnUnknownProperties);
		return this;
	}

	/**
	 * Shortcut for {@link SerializationFeature#FAIL_ON_EMPTY_BEANS} option.
	 * <p>
	 *  快捷方式{@link SerializationFeature#FAIL_ON_EMPTY_BEANS}选项
	 * 
	 */
	public Jackson2ObjectMapperBuilder failOnEmptyBeans(boolean failOnEmptyBeans) {
		this.features.put(SerializationFeature.FAIL_ON_EMPTY_BEANS, failOnEmptyBeans);
		return this;
	}

	/**
	 * Shortcut for {@link SerializationFeature#INDENT_OUTPUT} option.
	 * <p>
	 *  {@link SerializationFeature#INDENT_OUTPUT}选项的快捷方式
	 * 
	 */
	public Jackson2ObjectMapperBuilder indentOutput(boolean indentOutput) {
		this.features.put(SerializationFeature.INDENT_OUTPUT, indentOutput);
		return this;
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
	public Jackson2ObjectMapperBuilder defaultUseWrapper(boolean defaultUseWrapper) {
		this.defaultUseWrapper = defaultUseWrapper;
		return this;
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
	public Jackson2ObjectMapperBuilder featuresToEnable(Object... featuresToEnable) {
		if (featuresToEnable != null) {
			for (Object feature : featuresToEnable) {
				this.features.put(feature, Boolean.TRUE);
			}
		}
		return this;
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
	public Jackson2ObjectMapperBuilder featuresToDisable(Object... featuresToDisable) {
		if (featuresToDisable != null) {
			for (Object feature : featuresToDisable) {
				this.features.put(feature, Boolean.FALSE);
			}
		}
		return this;
	}

	/**
	 * Specify one or more modules to be registered with the {@link ObjectMapper}.
	 * <p>Note: If this is set, no finding of modules is going to happen - not by
	 * Jackson, and not by Spring either (see {@link #findModulesViaServiceLoader}).
	 * As a consequence, specifying an empty list here will suppress any kind of
	 * module detection.
	 * <p>Specify either this or {@link #modulesToInstall}, not both.
	 * <p>
	 * 指定要向{@link ObjectMapper}注册的一个或多个模块注意：如果这样设置,则不会发现模块的发现 - 而不是由Jackson,而不是Spring(请参阅{@link #findModulesViaServiceLoader }
	 * )因此,在这里指定一个空列表将禁止任何类型的模块检测<p>指定这个或{@link #modulesToInstall},而不是两者。
	 * 
	 * 
	 * @since 4.1.5
	 * @see #modules(List)
	 * @see com.fasterxml.jackson.databind.Module
	 */
	public Jackson2ObjectMapperBuilder modules(Module... modules) {
		return modules(Arrays.asList(modules));
	}

	/**
	 * Set a complete list of modules to be registered with the {@link ObjectMapper}.
	 * <p>Note: If this is set, no finding of modules is going to happen - not by
	 * Jackson, and not by Spring either (see {@link #findModulesViaServiceLoader}).
	 * As a consequence, specifying an empty list here will suppress any kind of
	 * module detection.
	 * <p>Specify either this or {@link #modulesToInstall}, not both.
	 * <p>
	 *  设置要在{@link ObjectMapper} <p>注册的模块的完整列表注意：如果这样设置,则不会发现模块的发现 - 而不是由Jackson,而不是Spring(请参阅{@link# findModulesViaServiceLoader}
	 * )因此,在此处指定一个空列表将会禁止任何类型的模块检测<p>指定此或{@link #modulesToInstall},而不是两者。
	 * 
	 * 
	 * @see #modules(Module...)
	 * @see com.fasterxml.jackson.databind.Module
	 */
	public Jackson2ObjectMapperBuilder modules(List<Module> modules) {
		this.modules = new LinkedList<Module>(modules);
		this.findModulesViaServiceLoader = false;
		this.findWellKnownModules = false;
		return this;
	}

	/**
	 * Specify one or more modules to be registered with the {@link ObjectMapper}.
	 * <p>Modules specified here will be registered after
	 * Spring's autodetection of JSR-310 and Joda-Time, or Jackson's
	 * finding of modules (see {@link #findModulesViaServiceLoader}),
	 * allowing to eventually override their configuration.
	 * <p>Specify either this or {@link #modules}, not both.
	 * <p>
	 * 指定要在{@link ObjectMapper}注册的一个或多个模块<p>此处指定的模块将在Spring自动检测JSR-310和Joda-Time之后注册,或者Jackson的模块查找(请参阅{@link #findModulesViaServiceLoader}
	 * ) ,允许最终覆盖其配置<p>指定此或{@link #modules},而不是两者。
	 * 
	 * 
	 * @since 4.1.5
	 * @see com.fasterxml.jackson.databind.Module
	 */
	public Jackson2ObjectMapperBuilder modulesToInstall(Module... modules) {
		this.modules = Arrays.asList(modules);
		this.findWellKnownModules = true;
		return this;
	}

	/**
	 * Specify one or more modules by class to be registered with
	 * the {@link ObjectMapper}.
	 * <p>Modules specified here will be registered after
	 * Spring's autodetection of JSR-310 and Joda-Time, or Jackson's
	 * finding of modules (see {@link #findModulesViaServiceLoader}),
	 * allowing to eventually override their configuration.
	 * <p>Specify either this or {@link #modules}, not both.
	 * <p>
	 *  按类别指定一个或多个模块以注册{@link ObjectMapper} <p>此处指定的模块将在Spring自动检测JSR-310和Joda-Time之后注册,或者Jackson的模块查找(请参阅{@link #findModulesViaServiceLoader }
	 * ),允许最终覆盖其配置<p>指定这个或{@link #modules},而不是两者。
	 * 
	 * 
	 * @see #modulesToInstall(Module...)
	 * @see com.fasterxml.jackson.databind.Module
	 */
	@SuppressWarnings("unchecked")
	public Jackson2ObjectMapperBuilder modulesToInstall(Class<? extends Module>... modules) {
		this.moduleClasses = modules;
		this.findWellKnownModules = true;
		return this;
	}

	/**
	 * Set whether to let Jackson find available modules via the JDK ServiceLoader,
	 * based on META-INF metadata in the classpath. Requires Jackson 2.2 or higher.
	 * <p>If this mode is not set, Spring's Jackson2ObjectMapperBuilder itself
	 * will try to find the JSR-310 and Joda-Time support modules on the classpath -
	 * provided that Java 8 and Joda-Time themselves are available, respectively.
	 * <p>
	 * 设置是否让Jackson通过JDK ServiceLoader找到可用的模块,基于类路径中的META-INF元数据需要Jackson 22或更高版本如果没有设置此模式,Spring的Jackson2Ob
	 * jectMapperBuilder本身将尝试找到JSR-310和Joda -time支持类路径上的模块,只要Java 8和Joda-Time本身分别可用。
	 * 
	 * 
	 * @see com.fasterxml.jackson.databind.ObjectMapper#findModules()
	 */
	public Jackson2ObjectMapperBuilder findModulesViaServiceLoader(boolean findModules) {
		this.findModulesViaServiceLoader = findModules;
		return this;
	}

	/**
	 * Set the ClassLoader to use for loading Jackson extension modules.
	 * <p>
	 *  设置ClassLoader用于加载Jackson扩展模块
	 * 
	 */
	public Jackson2ObjectMapperBuilder moduleClassLoader(ClassLoader moduleClassLoader) {
		this.moduleClassLoader = moduleClassLoader;
		return this;
	}

	/**
	 * Customize the construction of Jackson handlers ({@link JsonSerializer}, {@link JsonDeserializer},
	 * {@link KeyDeserializer}, {@code TypeResolverBuilder} and {@code TypeIdResolver}).
	 * <p>
	 *  定制杰克逊处理程序({@link JsonSerializer},{@link JsonDeserializer},{@link KeyDeserializer},{@code TypeResolverBuilder}
	 * 和{@code TypeIdResolver}))的构建。
	 * 
	 * 
	 * @since 4.1.3
	 * @see Jackson2ObjectMapperBuilder#applicationContext(ApplicationContext)
	 */
	public Jackson2ObjectMapperBuilder handlerInstantiator(HandlerInstantiator handlerInstantiator) {
		this.handlerInstantiator = handlerInstantiator;
		return this;
	}

	/**
	 * Set the Spring {@link ApplicationContext} in order to autowire Jackson handlers ({@link JsonSerializer},
	 * {@link JsonDeserializer}, {@link KeyDeserializer}, {@code TypeResolverBuilder} and {@code TypeIdResolver}).
	 * <p>
	 * 设置Spring {@link ApplicationContext},以便自动连接Jackson处理程序({@link JsonSerializer},{@link JsonDeserializer}
	 * ,{@link KeyDeserializer},{@code TypeResolverBuilder}和{@code TypeIdResolver}))。
	 * 
	 * 
	 * @since 4.1.3
	 * @see SpringHandlerInstantiator
	 */
	public Jackson2ObjectMapperBuilder applicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
		return this;
	}


	/**
	 * Build a new {@link ObjectMapper} instance.
	 * <p>Each build operation produces an independent {@link ObjectMapper} instance.
	 * The builder's settings can get modified, with a subsequent build operation
	 * then producing a new {@link ObjectMapper} based on the most recent settings.
	 * <p>
	 *  构建新的{@link ObjectMapper}实例<p>每个构建操作都会生成一个独立的{@link ObjectMapper}实例可以修改构建器的设置,随后进行构建操作,然后根据最近的生成一个新的{@link ObjectMapper}
	 * 设置。
	 * 
	 * 
	 * @return the newly built ObjectMapper
	 */
	@SuppressWarnings("unchecked")
	public <T extends ObjectMapper> T build() {
		ObjectMapper mapper;
		if (this.createXmlMapper) {
			mapper = (this.defaultUseWrapper != null ?
					new XmlObjectMapperInitializer().create(this.defaultUseWrapper) :
					new XmlObjectMapperInitializer().create());
		}
		else {
			mapper = new ObjectMapper();
		}
		configure(mapper);
		return (T) mapper;
	}

	/**
	 * Configure an existing {@link ObjectMapper} instance with this builder's
	 * settings. This can be applied to any number of {@code ObjectMappers}.
	 * <p>
	 *  使用此构建器的设置配置现有的{@link ObjectMapper}实例可以将其应用于任意数量的{@code ObjectMappers}
	 * 
	 * 
	 * @param objectMapper the ObjectMapper to configure
	 */
	public void configure(ObjectMapper objectMapper) {
		Assert.notNull(objectMapper, "ObjectMapper must not be null");

		if (this.findModulesViaServiceLoader) {
			// Jackson 2.2+
			objectMapper.registerModules(ObjectMapper.findModules(this.moduleClassLoader));
		}
		else if (this.findWellKnownModules) {
			registerWellKnownModulesIfAvailable(objectMapper);
		}

		if (this.modules != null) {
			for (Module module : this.modules) {
				// Using Jackson 2.0+ registerModule method, not Jackson 2.2+ registerModules
				objectMapper.registerModule(module);
			}
		}
		if (this.moduleClasses != null) {
			for (Class<? extends Module> module : this.moduleClasses) {
				objectMapper.registerModule(BeanUtils.instantiate(module));
			}
		}

		if (this.dateFormat != null) {
			objectMapper.setDateFormat(this.dateFormat);
		}
		if (this.locale != null) {
			objectMapper.setLocale(this.locale);
		}
		if (this.timeZone != null) {
			objectMapper.setTimeZone(this.timeZone);
		}

		if (this.annotationIntrospector != null) {
			objectMapper.setAnnotationIntrospector(this.annotationIntrospector);
		}
		if (this.propertyNamingStrategy != null) {
			objectMapper.setPropertyNamingStrategy(this.propertyNamingStrategy);
		}
		if (this.defaultTyping != null) {
			objectMapper.setDefaultTyping(this.defaultTyping);
		}
		if (this.serializationInclusion != null) {
			objectMapper.setSerializationInclusion(this.serializationInclusion);
		}

		if (this.filters != null) {
			objectMapper.setFilterProvider(this.filters);
		}

		for (Class<?> target : this.mixIns.keySet()) {
			objectMapper.addMixIn(target, this.mixIns.get(target));
		}

		if (!this.serializers.isEmpty() || !this.deserializers.isEmpty()) {
			SimpleModule module = new SimpleModule();
			addSerializers(module);
			addDeserializers(module);
			objectMapper.registerModule(module);
		}

		customizeDefaultFeatures(objectMapper);
		for (Object feature : this.features.keySet()) {
			configureFeature(objectMapper, feature, this.features.get(feature));
		}

		if (this.handlerInstantiator != null) {
			objectMapper.setHandlerInstantiator(this.handlerInstantiator);
		}
		else if (this.applicationContext != null) {
			objectMapper.setHandlerInstantiator(
					new SpringHandlerInstantiator(this.applicationContext.getAutowireCapableBeanFactory()));
		}
	}


	// Any change to this method should be also applied to spring-jms and spring-messaging
	// MappingJackson2MessageConverter default constructors
	private void customizeDefaultFeatures(ObjectMapper objectMapper) {
		if (!this.features.containsKey(MapperFeature.DEFAULT_VIEW_INCLUSION)) {
			configureFeature(objectMapper, MapperFeature.DEFAULT_VIEW_INCLUSION, false);
		}
		if (!this.features.containsKey(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)) {
			configureFeature(objectMapper, DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> void addSerializers(SimpleModule module) {
		for (Class<?> type : this.serializers.keySet()) {
			module.addSerializer((Class<? extends T>) type, (JsonSerializer<T>) this.serializers.get(type));
		}
	}

	@SuppressWarnings("unchecked")
	private <T> void addDeserializers(SimpleModule module) {
		for (Class<?> type : this.deserializers.keySet()) {
			module.addDeserializer((Class<T>) type, (JsonDeserializer<? extends T>) this.deserializers.get(type));
		}
	}

	private void configureFeature(ObjectMapper objectMapper, Object feature, boolean enabled) {
		if (feature instanceof JsonParser.Feature) {
			objectMapper.configure((JsonParser.Feature) feature, enabled);
		}
		else if (feature instanceof JsonGenerator.Feature) {
			objectMapper.configure((JsonGenerator.Feature) feature, enabled);
		}
		else if (feature instanceof SerializationFeature) {
			objectMapper.configure((SerializationFeature) feature, enabled);
		}
		else if (feature instanceof DeserializationFeature) {
			objectMapper.configure((DeserializationFeature) feature, enabled);
		}
		else if (feature instanceof MapperFeature) {
			objectMapper.configure((MapperFeature) feature, enabled);
		}
		else {
			throw new FatalBeanException("Unknown feature class: " + feature.getClass().getName());
		}
	}

	@SuppressWarnings("unchecked")
	private void registerWellKnownModulesIfAvailable(ObjectMapper objectMapper) {
		// Java 7 java.nio.file.Path class present?
		if (ClassUtils.isPresent("java.nio.file.Path", this.moduleClassLoader)) {
			try {
				Class<? extends Module> jdk7Module = (Class<? extends Module>)
						ClassUtils.forName("com.fasterxml.jackson.datatype.jdk7.Jdk7Module", this.moduleClassLoader);
				objectMapper.registerModule(BeanUtils.instantiateClass(jdk7Module));
			}
			catch (ClassNotFoundException ex) {
				// jackson-datatype-jdk7 not available
			}
		}

		// Java 8 java.util.Optional class present?
		if (ClassUtils.isPresent("java.util.Optional", this.moduleClassLoader)) {
			try {
				Class<? extends Module> jdk8Module = (Class<? extends Module>)
						ClassUtils.forName("com.fasterxml.jackson.datatype.jdk8.Jdk8Module", this.moduleClassLoader);
				objectMapper.registerModule(BeanUtils.instantiateClass(jdk8Module));
			}
			catch (ClassNotFoundException ex) {
				// jackson-datatype-jdk8 not available
			}
		}

		// Java 8 java.time package present?
		if (ClassUtils.isPresent("java.time.LocalDate", this.moduleClassLoader)) {
			try {
				Class<? extends Module> javaTimeModule = (Class<? extends Module>)
						ClassUtils.forName("com.fasterxml.jackson.datatype.jsr310.JavaTimeModule", this.moduleClassLoader);
				objectMapper.registerModule(BeanUtils.instantiateClass(javaTimeModule));
			}
			catch (ClassNotFoundException ex) {
				// jackson-datatype-jsr310 not available
			}
		}

		// Joda-Time present?
		if (ClassUtils.isPresent("org.joda.time.LocalDate", this.moduleClassLoader)) {
			try {
				Class<? extends Module> jodaModule = (Class<? extends Module>)
						ClassUtils.forName("com.fasterxml.jackson.datatype.joda.JodaModule", this.moduleClassLoader);
				objectMapper.registerModule(BeanUtils.instantiateClass(jodaModule));
			}
			catch (ClassNotFoundException ex) {
				// jackson-datatype-joda not available
			}
		}

		// Kotlin present?
		if (ClassUtils.isPresent("kotlin.Unit", this.moduleClassLoader)) {
			try {
				Class<? extends Module> kotlinModule = (Class<? extends Module>)
						ClassUtils.forName("com.fasterxml.jackson.module.kotlin.KotlinModule", this.moduleClassLoader);
				objectMapper.registerModule(BeanUtils.instantiateClass(kotlinModule));
			}
			catch (ClassNotFoundException ex) {
				// jackson-module-kotlin not available
			}
		}
	}


	// Convenience factory methods

	/**
	 * Obtain a {@link Jackson2ObjectMapperBuilder} instance in order to
	 * build a regular JSON {@link ObjectMapper} instance.
	 * <p>
	 *  获取{@link Jackson2ObjectMapperBuilder}实例,以构建一个常规的JSON {@link ObjectMapper}实例
	 * 
	 */
	public static Jackson2ObjectMapperBuilder json() {
		return new Jackson2ObjectMapperBuilder();
	}

	/**
	 * Obtain a {@link Jackson2ObjectMapperBuilder} instance in order to
	 * build an {@link XmlMapper} instance.
	 * <p>
	 * 获取{@link Jackson2ObjectMapperBuilder}实例以构建一个{@link XmlMapper}实例
	 */
	public static Jackson2ObjectMapperBuilder xml() {
		return new Jackson2ObjectMapperBuilder().createXmlMapper(true);
	}


	private static class XmlObjectMapperInitializer {

		public ObjectMapper create() {
			return new XmlMapper(xmlInputFactory());
		}

		public ObjectMapper create(boolean defaultUseWrapper) {
			JacksonXmlModule module = new JacksonXmlModule();
			module.setDefaultUseWrapper(defaultUseWrapper);
			return new XmlMapper(new XmlFactory(xmlInputFactory()), module);
		}

		private static XMLInputFactory xmlInputFactory() {
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			inputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
			inputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
			inputFactory.setXMLResolver(NO_OP_XML_RESOLVER);
			return inputFactory;
		}

		private static final XMLResolver NO_OP_XML_RESOLVER = new XMLResolver() {
			@Override
			public Object resolveEntity(String publicID, String systemID, String base, String ns) {
				return StreamUtils.emptyInput();
			}
		};
	}

}
