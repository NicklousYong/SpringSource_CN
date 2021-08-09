/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2014 the original author or authors.
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

package org.springframework.oxm.xstream;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.stream.StreamSource;

import com.thoughtworks.xstream.MarshallingStrategy;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.ConverterMatcher;
import com.thoughtworks.xstream.converters.ConverterRegistry;
import com.thoughtworks.xstream.converters.DataHolder;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.core.DefaultConverterLookup;
import com.thoughtworks.xstream.core.util.CompositeClassLoader;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.xml.CompactWriter;
import com.thoughtworks.xstream.io.xml.DomReader;
import com.thoughtworks.xstream.io.xml.DomWriter;
import com.thoughtworks.xstream.io.xml.QNameMap;
import com.thoughtworks.xstream.io.xml.SaxWriter;
import com.thoughtworks.xstream.io.xml.StaxReader;
import com.thoughtworks.xstream.io.xml.StaxWriter;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.thoughtworks.xstream.mapper.CannotResolveClassException;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.oxm.MarshallingFailureException;
import org.springframework.oxm.UncategorizedMappingException;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.support.AbstractMarshaller;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.StaxUtils;

/**
 * Implementation of the {@code Marshaller} interface for XStream.
 *
 * <p>By default, XStream does not require any further configuration and can (un)marshal
 * any class on the classpath. As such, it is <b>not recommended to use the
 * {@code XStreamMarshaller} to unmarshal XML from external sources</b> (i.e. the Web),
 * as this can result in <b>security vulnerabilities</b>. If you do use the
 * {@code XStreamMarshaller} to unmarshal external XML, set the
 * {@link #setSupportedClasses(Class[]) supportedClasses} and
 * {@link #setConverters(ConverterMatcher[]) converters} properties (possibly using
 * a {@link CatchAllConverter}) or override the {@link #customizeXStream(XStream)}
 * method to make sure it only accepts the classes you want it to support.
 *
 * <p>Due to XStream's API, it is required to set the encoding used for writing to
 * OutputStreams. It defaults to {@code UTF-8}.
 *
 * <p><b>NOTE:</b> XStream is an XML serialization library, not a data binding library.
 * Therefore, it has limited namespace support. As such, it is rather unsuitable for
 * usage within Web Services.
 *
 * <p>This marshaller requires XStream 1.4 or higher, as of Spring 4.0.
 * Note that {@link XStream} construction has been reworked in 4.0, with the
 * stream driver and the class loader getting passed into XStream itself now.
 *
 * <p>
 *  实现XStream的{@code Marshaller}界面
 * 
 * <p>默认情况下,XStream不需要任何进一步的配置,并且可以(或)组播类路径上的任何类。
 * 因此,不建议使用{@code XStreamMarshaller}从外部源解组XML < b>(即Web),因为这可能导致<b>安全漏洞</b>如果您使用{@code XStreamMarshaller}
 * 解组外部XML,请设置{@link #setSupportedClasses(Class [])supportedClasses }和{@link #setConverters(ConverterMatcher [])转换器}
 * 属性(可能使用{@link CatchAllConverter})或覆盖{@link #customizeXStream(XStream)}方法,以确保它只接受您想要的类支持。
 * <p>默认情况下,XStream不需要任何进一步的配置,并且可以(或)组播类路径上的任何类。
 * 
 *  <p>由于XStream的API,需要将用于写入的编码设置为OutputStreams默认为{@code UTF-8}
 * 
 * <p> <b>注意：</b> XStream是一个XML序列化库,而不是数据绑定库因此,它具有有限的命名空间支持因此,它不适合在Web服务中使用
 * 
 *  <p>这个编组者需要XStream 14或更高版本,从Spring 40开始注意,{@link XStream}构造已经在40中重新编译,流驱动程序和类加载器现在被传入XStream
 * 
 * 
 * @author Peter Meijer
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @since 3.0
 */
public class XStreamMarshaller extends AbstractMarshaller implements InitializingBean, BeanClassLoaderAware {

	/**
	 * The default encoding used for stream access: UTF-8.
	 * <p>
	 *  用于流访问的默认编码：UTF-8
	 * 
	 */
	public static final String DEFAULT_ENCODING = "UTF-8";


	private ReflectionProvider reflectionProvider;

	private HierarchicalStreamDriver streamDriver;

	private HierarchicalStreamDriver defaultDriver;

	private Mapper mapper;

	private Class<?>[] mapperWrappers;

	private ConverterLookup converterLookup = new DefaultConverterLookup();

	private ConverterRegistry converterRegistry = (ConverterRegistry) this.converterLookup;

	private ConverterMatcher[] converters;

	private MarshallingStrategy marshallingStrategy;

	private Integer mode;

	private Map<String, ?> aliases;

	private Map<String, ?> aliasesByType;

	private Map<String, String> fieldAliases;

	private Class<?>[] useAttributeForTypes;

	private Map<?, ?> useAttributeFor;

	private Map<Class<?>, String> implicitCollections;

	private Map<Class<?>, String> omittedFields;

	private Class<?>[] annotatedClasses;

	private boolean autodetectAnnotations;

	private String encoding = DEFAULT_ENCODING;

	private NameCoder nameCoder = new XmlFriendlyNameCoder();

	private Class<?>[] supportedClasses;

	private ClassLoader beanClassLoader = new CompositeClassLoader();

	private XStream xstream;


	/**
	 * Set a custom XStream {@link ReflectionProvider} to use.
	 * <p>
	 *  设置一个自定义的XStream {@link ReflectionProvider}来使用
	 * 
	 * 
	 * @since 4.0
	 */
	public void setReflectionProvider(ReflectionProvider reflectionProvider) {
		this.reflectionProvider = reflectionProvider;
	}

	/**
	 * Set a XStream {@link HierarchicalStreamDriver} to be used for readers and writers.
	 * <p>As of Spring 4.0, this stream driver will also be passed to the {@link XStream}
	 * constructor and therefore used by streaming-related native API methods themselves.
	 * <p>
	 *  设置一个XStream {@link HierarchicalStreamDriver}以供读者和作者使用<p>自Spring 40开始,此流驱动程序也将被传递给{@link XStream}构造函数
	 * ,因此由流式相关的本机API方法本身使用。
	 * 
	 */
	public void setStreamDriver(HierarchicalStreamDriver streamDriver) {
		this.streamDriver = streamDriver;
		this.defaultDriver = streamDriver;
	}

	private HierarchicalStreamDriver getDefaultDriver() {
		if (this.defaultDriver == null) {
			this.defaultDriver = new XppDriver();
		}
		return this.defaultDriver;
	}

	/**
	 * Set a custom XStream {@link Mapper} to use.
	 * <p>
	 * 设置一个自定义的XStream {@link Mapper}来使用
	 * 
	 * 
	 * @since 4.0
	 */
	public void setMapper(Mapper mapper) {
		this.mapper = mapper;
	}

	/**
	 * Set one or more custom XStream {@link MapperWrapper} classes.
	 * Each of those classes needs to have a constructor with a single argument
	 * of type {@link Mapper} or {@link MapperWrapper}.
	 * <p>
	 *  设置一个或多个自定义XStream {@link MapperWrapper}类每个类都需要一个构造函数,其中一个参数类型为{@link Mapper}或{@link MapperWrapper}
	 * 
	 * 
	 * @since 4.0
	 */
	public void setMapperWrappers(Class<?>... mapperWrappers) {
		this.mapperWrappers = mapperWrappers;
	}

	/**
	 * Set a custom XStream {@link ConverterLookup} to use.
	 * Also used as {@link ConverterRegistry} if the given reference implements it as well.
	 * <p>
	 *  设置一个自定义的XStream {@link ConverterLookup}使用如果给定的引用也一样使用也用作{@link ConverterRegistry}
	 * 
	 * 
	 * @since 4.0
	 * @see DefaultConverterLookup
	 */
	public void setConverterLookup(ConverterLookup converterLookup) {
		this.converterLookup = converterLookup;
		if (converterLookup instanceof ConverterRegistry) {
			this.converterRegistry = (ConverterRegistry) converterLookup;
		}
	}

	/**
	 * Set a custom XStream {@link ConverterRegistry} to use.
	 * <p>
	 *  设置一个自定义XStream {@link ConverterRegistry}来使用
	 * 
	 * 
	 * @since 4.0
	 * @see #setConverterLookup
	 * @see DefaultConverterLookup
	 */
	public void setConverterRegistry(ConverterRegistry converterRegistry) {
		this.converterRegistry = converterRegistry;
	}

	/**
	 * Set the {@code Converters} or {@code SingleValueConverters} to be registered
	 * with the {@code XStream} instance.
	 * <p>
	 *  将{@code转换器}或{@code SingleValueConverters}设置为要在{@code XStream}实例中注册
	 * 
	 * 
	 * @see Converter
	 * @see SingleValueConverter
	 */
	public void setConverters(ConverterMatcher... converters) {
		this.converters = converters;
	}

	/**
	 * Set a custom XStream {@link MarshallingStrategy} to use.
	 * <p>
	 *  设置一个自定义的XStream {@link MarshallingStrategy}来使用
	 * 
	 * 
	 * @since 4.0
	 */
	public void setMarshallingStrategy(MarshallingStrategy marshallingStrategy) {
		this.marshallingStrategy = marshallingStrategy;
	}

	/**
	 * Set the XStream mode to use.
	 * <p>
	 *  设置要使用的XStream模式
	 * 
	 * 
	 * @see XStream#ID_REFERENCES
	 * @see XStream#NO_REFERENCES
	 */
	public void setMode(int mode) {
		this.mode = mode;
	}

	/**
	 * Set the alias/type map, consisting of string aliases mapped to classes.
	 * <p>Keys are aliases; values are either {@code Class} instances, or String class names.
	 * <p>
	 *  设置别名/类型映射,由映射到类的字符串别名组成<p>键是别名;值是{@code Class}实例或String类名
	 * 
	 * 
	 * @see XStream#alias(String, Class)
	 */
	public void setAliases(Map<String, ?> aliases) {
		this.aliases = aliases;
	}

	/**
	 * Set the <em>aliases by type</em> map, consisting of string aliases mapped to classes.
	 * <p>Any class that is assignable to this type will be aliased to the same name.
	 * Keys are aliases; values are either {@code Class} instances, or String class names.
	 * <p>
	 * 通过类型</em>映射设置<em>别名,由映射到类的字符串别名组成<p>任何可分配给此类型的类将被别名为相同的名称Keys是别名;值是{@code Class}实例或String类名
	 * 
	 * 
	 * @see XStream#aliasType(String, Class)
	 */
	public void setAliasesByType(Map<String, ?> aliasesByType) {
		this.aliasesByType = aliasesByType;
	}

	/**
	 * Set the field alias/type map, consisting of field names.
	 * <p>
	 *  设置字段别名/类型映射,由字段名称组成
	 * 
	 * 
	 * @see XStream#aliasField(String, Class, String)
	 */
	public void setFieldAliases(Map<String, String> fieldAliases) {
		this.fieldAliases = fieldAliases;
	}

	/**
	 * Set types to use XML attributes for.
	 * <p>
	 *  将类型设置为使用XML属性
	 * 
	 * 
	 * @see XStream#useAttributeFor(Class)
	 */
	public void setUseAttributeForTypes(Class<?>... useAttributeForTypes) {
		this.useAttributeForTypes = useAttributeForTypes;
	}

	/**
	 * Set the types to use XML attributes for. The given map can contain
	 * either {@code <String, Class>} pairs, in which case
	 * {@link XStream#useAttributeFor(String, Class)} is called.
	 * Alternatively, the map can contain {@code <Class, String>}
	 * or {@code <Class, List<String>>} pairs, which results
	 * in {@link XStream#useAttributeFor(Class, String)} calls.
	 * <p>
	 *  将类型设置为使用XML属性给定的映射可以包含{@code <String,Class>}对,在这种情况下{@link XStream#useAttributeFor(String,Class)}也可以
	 * 包含{@code代码<Class,String>}或{@code <Class,List <String >>}对,这导致{@link XStream#useAttributeFor(Class,String)}
	 * 调用。
	 * 
	 */
	public void setUseAttributeFor(Map<?, ?> useAttributeFor) {
		this.useAttributeFor = useAttributeFor;
	}

	/**
	 * Specify implicit collection fields, as a Map consisting of {@code Class} instances
	 * mapped to comma separated collection field names.
	 * <p>
	 * 指定隐式收集字段,作为映射到逗号分隔的集合字段名称的{@code Class}实例的映射
	 * 
	 * 
	 * @see XStream#addImplicitCollection(Class, String)
	 */
	public void setImplicitCollections(Map<Class<?>, String> implicitCollections) {
		this.implicitCollections = implicitCollections;
	}

	/**
	 * Specify omitted fields, as a Map consisting of {@code Class} instances
	 * mapped to comma separated field names.
	 * <p>
	 *  指定省略的字段,作为映射到逗号分隔字段名称的{@code Class}实例组成的映射
	 * 
	 * 
	 * @see XStream#omitField(Class, String)
	 */
	public void setOmittedFields(Map<Class<?>, String> omittedFields) {
		this.omittedFields = omittedFields;
	}

	/**
	 * Set annotated classes for which aliases will be read from class-level annotation metadata.
	 * <p>
	 *  设置从类级别注释元数据中读取别名的注释类
	 * 
	 * 
	 * @see XStream#processAnnotations(Class[])
	 */
	public void setAnnotatedClasses(Class<?>... annotatedClasses) {
		this.annotatedClasses = annotatedClasses;
	}

	/**
	 * Activate XStream's autodetection mode.
	 * <p><b>Note</b>: Autodetection implies that the XStream instance is being configured while
	 * it is processing the XML streams, and thus introduces a potential concurrency problem.
	 * <p>
	 *  激活XStream的自动检测模式<p> <b>注意</b>：自动检测意味着在处理XML流时正在配置XStream实例,从而引入潜在的并发问题
	 * 
	 * 
	 * @see XStream#autodetectAnnotations(boolean)
	 */
	public void setAutodetectAnnotations(boolean autodetectAnnotations) {
		this.autodetectAnnotations = autodetectAnnotations;
	}

	/**
	 * Set the encoding to be used for stream access.
	 * <p>
	 *  设置要用于流访问的编码
	 * 
	 * 
	 * @see #DEFAULT_ENCODING
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	@Override
	protected String getDefaultEncoding() {
		return this.encoding;
	}

	/**
	 * Set a custom XStream {@link NameCoder} to use.
	 * The default is an {@link XmlFriendlyNameCoder}.
	 * <p>
	 *  设置自定义XStream {@link NameCoder}使用默认是一个{@link XmlFriendlyNameCoder}
	 * 
	 * 
	 * @since 4.0.4
	 */
	public void setNameCoder(NameCoder nameCoder) {
		this.nameCoder = nameCoder;
	}

	/**
	 * Set the classes supported by this marshaller.
	 * <p>If this property is empty (the default), all classes are supported.
	 * <p>
	 * 设置此编组器支持的类<p>如果此属性为空(默认值),则支持所有类
	 * 
	 * 
	 * @see #supports(Class)
	 */
	public void setSupportedClasses(Class<?>... supportedClasses) {
		this.supportedClasses = supportedClasses;
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.beanClassLoader = classLoader;
	}


	@Override
	public void afterPropertiesSet() {
		this.xstream = buildXStream();
	}

	/**
	 * Build the native XStream delegate to be used by this marshaller,
	 * delegating to {@link #constructXStream()}, {@link #configureXStream}
	 * and {@link #customizeXStream}.
	 * <p>
	 *  构建本机XStream代理以供此编组人员使​​用,委托{@link #constructXStream()},{@link #configureXStream}和{@link #customizeXStream}
	 * 。
	 * 
	 */
	protected XStream buildXStream() {
		XStream xstream = constructXStream();
		configureXStream(xstream);
		customizeXStream(xstream);
		return xstream;
	}

	/**
	 * Construct an XStream instance, either using one of the
	 * standard constructors or creating a custom subclass.
	 * <p>
	 *  构造XStream实例,使用其中一个标准构造函数或创建自定义子类
	 * 
	 * 
	 * @return the {@code XStream} instance
	 */
	@SuppressWarnings("deprecation")
	protected XStream constructXStream() {
		// The referenced XStream constructor has been deprecated as of 1.4.5.
		// We're preserving this call for broader XStream 1.4.x compatibility.
		return new XStream(this.reflectionProvider, getDefaultDriver(),
				this.beanClassLoader, this.mapper, this.converterLookup, this.converterRegistry) {
			@Override
			protected MapperWrapper wrapMapper(MapperWrapper next) {
				MapperWrapper mapperToWrap = next;
				if (mapperWrappers != null) {
					for (Class<?> mapperWrapper : mapperWrappers) {
						Assert.isAssignable(MapperWrapper.class, mapperWrapper);
						Constructor<?> ctor;
						try {
							ctor = mapperWrapper.getConstructor(Mapper.class);
						}
						catch (NoSuchMethodException ex) {
							try {
								ctor = mapperWrapper.getConstructor(MapperWrapper.class);
							}
							catch (NoSuchMethodException ex2) {
								throw new IllegalStateException("No appropriate MapperWrapper constructor found: " + mapperWrapper);
							}
						}
						try {
							mapperToWrap = (MapperWrapper) ctor.newInstance(mapperToWrap);
						}
						catch (Exception ex) {
							throw new IllegalStateException("Failed to construct MapperWrapper: " + mapperWrapper);
						}
					}
				}
				return mapperToWrap;
			}
		};
	}

	/**
	 * Configure the XStream instance with this marshaller's bean properties.
	 * <p>
	 *  使用此编组器的bean属性配置XStream实例
	 * 
	 * 
	 * @param xstream the {@code XStream} instance
	 */
	protected void configureXStream(XStream xstream) {
		if (this.converters != null) {
			for (int i = 0; i < this.converters.length; i++) {
				if (this.converters[i] instanceof Converter) {
					xstream.registerConverter((Converter) this.converters[i], i);
				}
				else if (this.converters[i] instanceof SingleValueConverter) {
					xstream.registerConverter((SingleValueConverter) this.converters[i], i);
				}
				else {
					throw new IllegalArgumentException("Invalid ConverterMatcher [" + this.converters[i] + "]");
				}
			}
		}

		if (this.marshallingStrategy != null) {
			xstream.setMarshallingStrategy(this.marshallingStrategy);
		}
		if (this.mode != null) {
			xstream.setMode(this.mode);
		}

		try {
			if (this.aliases != null) {
				Map<String, Class<?>> classMap = toClassMap(this.aliases);
				for (Map.Entry<String, Class<?>> entry : classMap.entrySet()) {
					xstream.alias(entry.getKey(), entry.getValue());
				}
			}
			if (this.aliasesByType != null) {
				Map<String, Class<?>> classMap = toClassMap(this.aliasesByType);
				for (Map.Entry<String, Class<?>> entry : classMap.entrySet()) {
					xstream.aliasType(entry.getKey(), entry.getValue());
				}
			}
			if (this.fieldAliases != null) {
				for (Map.Entry<String, String> entry : this.fieldAliases.entrySet()) {
					String alias = entry.getValue();
					String field = entry.getKey();
					int idx = field.lastIndexOf('.');
					if (idx != -1) {
						String className = field.substring(0, idx);
						Class<?> clazz = ClassUtils.forName(className, this.beanClassLoader);
						String fieldName = field.substring(idx + 1);
						xstream.aliasField(alias, clazz, fieldName);
					}
					else {
						throw new IllegalArgumentException("Field name [" + field + "] does not contain '.'");
					}
				}
			}
		}
		catch (ClassNotFoundException ex) {
			throw new IllegalStateException("Failed to load specified alias class", ex);
		}

		if (this.useAttributeForTypes != null) {
			for (Class<?> type : this.useAttributeForTypes) {
				xstream.useAttributeFor(type);
			}
		}
		if (this.useAttributeFor != null) {
			for (Map.Entry<?, ?> entry : this.useAttributeFor.entrySet()) {
				if (entry.getKey() instanceof String) {
					if (entry.getValue() instanceof Class) {
						xstream.useAttributeFor((String) entry.getKey(), (Class<?>) entry.getValue());
					}
					else {
						throw new IllegalArgumentException(
								"'useAttributesFor' takes Map<String, Class> when using a map key of type String");
					}
				}
				else if (entry.getKey() instanceof Class) {
					Class<?> key = (Class<?>) entry.getKey();
					if (entry.getValue() instanceof String) {
						xstream.useAttributeFor(key, (String) entry.getValue());
					}
					else if (entry.getValue() instanceof List) {
						@SuppressWarnings("unchecked")
						List<Object> listValue = (List<Object>) entry.getValue();
						for (Object element : listValue) {
							if (element instanceof String) {
								xstream.useAttributeFor(key, (String) element);
							}
						}
					}
					else {
						throw new IllegalArgumentException("'useAttributesFor' property takes either Map<Class, String> " +
								"or Map<Class, List<String>> when using a map key of type Class");
					}
				}
				else {
					throw new IllegalArgumentException(
							"'useAttributesFor' property takes either a map key of type String or Class");
				}
			}
		}

		if (this.implicitCollections != null) {
			for (Map.Entry<Class<?>, String> entry : this.implicitCollections.entrySet()) {
				String[] collectionFields = StringUtils.commaDelimitedListToStringArray(entry.getValue());
				for (String collectionField : collectionFields) {
					xstream.addImplicitCollection(entry.getKey(), collectionField);
				}
			}
		}
		if (this.omittedFields != null) {
			for (Map.Entry<Class<?>, String> entry : this.omittedFields.entrySet()) {
				String[] fields = StringUtils.commaDelimitedListToStringArray(entry.getValue());
				for (String field : fields) {
					xstream.omitField(entry.getKey(), field);
				}
			}
		}

		if (this.annotatedClasses != null) {
			xstream.processAnnotations(this.annotatedClasses);
		}
		if (this.autodetectAnnotations) {
			xstream.autodetectAnnotations(true);
		}
	}

	private Map<String, Class<?>> toClassMap(Map<String, ?> map) throws ClassNotFoundException {
		Map<String, Class<?>> result = new LinkedHashMap<String, Class<?>>(map.size());
		for (Map.Entry<String, ?> entry : map.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			Class<?> type;
			if (value instanceof Class) {
				type = (Class<?>) value;
			}
			else if (value instanceof String) {
				String className = (String) value;
				type = ClassUtils.forName(className, this.beanClassLoader);
			}
			else {
				throw new IllegalArgumentException("Unknown value [" + value + "] - expected String or Class");
			}
			result.put(key, type);
		}
		return result;
	}

	/**
	 * Template to allow for customizing the given {@link XStream}.
	 * <p>The default implementation is empty.
	 * <p>
	 *  允许定制给定的{@link XStream} <p>的模板默认实现为空
	 * 
	 * 
	 * @param xstream the {@code XStream} instance
	 */
	protected void customizeXStream(XStream xstream) {
	}

	/**
	 * Return the native XStream delegate used by this marshaller.
	 * <p><b>NOTE: This method has been marked as final as of Spring 4.0.</b>
	 * It can be used to access the fully configured XStream for marshalling
	 * but not configuration purposes anymore.
	 * <p>
	 * 返回此编组器使用的本地XStream代理<p> <b>注意：此方法已被标记为最终的弹簧40 </b>它可用于访问完全配置的XStream进行编组,但不再配置目的
	 * 
	 */
	public final XStream getXStream() {
		if (this.xstream == null) {
			this.xstream = buildXStream();
		}
		return this.xstream;
	}


	@Override
	public boolean supports(Class<?> clazz) {
		if (ObjectUtils.isEmpty(this.supportedClasses)) {
			return true;
		}
		else {
			for (Class<?> supportedClass : this.supportedClasses) {
				if (supportedClass.isAssignableFrom(clazz)) {
					return true;
				}
			}
			return false;
		}
	}


	// Marshalling

	@Override
	protected void marshalDomNode(Object graph, Node node) throws XmlMappingException {
		HierarchicalStreamWriter streamWriter;
		if (node instanceof Document) {
			streamWriter = new DomWriter((Document) node, this.nameCoder);
		}
		else if (node instanceof Element) {
			streamWriter = new DomWriter((Element) node, node.getOwnerDocument(), this.nameCoder);
		}
		else {
			throw new IllegalArgumentException("DOMResult contains neither Document nor Element");
		}
		doMarshal(graph, streamWriter, null);
	}

	@Override
	protected void marshalXmlEventWriter(Object graph, XMLEventWriter eventWriter) throws XmlMappingException {
		ContentHandler contentHandler = StaxUtils.createContentHandler(eventWriter);
		LexicalHandler lexicalHandler = null;
		if (contentHandler instanceof LexicalHandler) {
			lexicalHandler = (LexicalHandler) contentHandler;
		}
		marshalSaxHandlers(graph, contentHandler, lexicalHandler);
	}

	@Override
	protected void marshalXmlStreamWriter(Object graph, XMLStreamWriter streamWriter) throws XmlMappingException {
		try {
			doMarshal(graph, new StaxWriter(new QNameMap(), streamWriter, this.nameCoder), null);
		}
		catch (XMLStreamException ex) {
			throw convertXStreamException(ex, true);
		}
	}

	@Override
	protected void marshalSaxHandlers(Object graph, ContentHandler contentHandler, LexicalHandler lexicalHandler)
			throws XmlMappingException {

		SaxWriter saxWriter = new SaxWriter(this.nameCoder);
		saxWriter.setContentHandler(contentHandler);
		doMarshal(graph, saxWriter, null);
	}

	@Override
	public void marshalOutputStream(Object graph, OutputStream outputStream) throws XmlMappingException, IOException {
		marshalOutputStream(graph, outputStream, null);
	}

	public void marshalOutputStream(Object graph, OutputStream outputStream, DataHolder dataHolder)
			throws XmlMappingException, IOException {

		if (this.streamDriver != null) {
			doMarshal(graph, this.streamDriver.createWriter(outputStream), dataHolder);
		}
		else {
			marshalWriter(graph, new OutputStreamWriter(outputStream, this.encoding), dataHolder);
		}
	}

	@Override
	public void marshalWriter(Object graph, Writer writer) throws XmlMappingException, IOException {
		marshalWriter(graph, writer, null);
	}

	public void marshalWriter(Object graph, Writer writer, DataHolder dataHolder)
			throws XmlMappingException, IOException {

		if (this.streamDriver != null) {
			doMarshal(graph, this.streamDriver.createWriter(writer), dataHolder);
		}
		else {
			doMarshal(graph, new CompactWriter(writer), dataHolder);
		}
	}

	/**
	 * Marshals the given graph to the given XStream HierarchicalStreamWriter.
	 * Converts exceptions using {@link #convertXStreamException}.
	 * <p>
	 *  将给定的图形传递给给定的XStream HierarchicalStreamWriter使用{@link #convertXStreamException}转换异常
	 * 
	 */
	private void doMarshal(Object graph, HierarchicalStreamWriter streamWriter, DataHolder dataHolder) {
		try {
			getXStream().marshal(graph, streamWriter, dataHolder);
		}
		catch (Exception ex) {
			throw convertXStreamException(ex, true);
		}
		finally {
			try {
				streamWriter.flush();
			}
			catch (Exception ex) {
				logger.debug("Could not flush HierarchicalStreamWriter", ex);
			}
		}
	}


	// Unmarshalling

	@Override
	protected Object unmarshalStreamSource(StreamSource streamSource) throws XmlMappingException, IOException {
		if (streamSource.getInputStream() != null) {
			return unmarshalInputStream(streamSource.getInputStream());
		}
		else if (streamSource.getReader() != null) {
			return unmarshalReader(streamSource.getReader());
		}
		else {
			throw new IllegalArgumentException("StreamSource contains neither InputStream nor Reader");
		}
	}

	@Override
	protected Object unmarshalDomNode(Node node) throws XmlMappingException {
		HierarchicalStreamReader streamReader;
		if (node instanceof Document) {
			streamReader = new DomReader((Document) node, this.nameCoder);
		}
		else if (node instanceof Element) {
			streamReader = new DomReader((Element) node, this.nameCoder);
		}
		else {
			throw new IllegalArgumentException("DOMSource contains neither Document nor Element");
		}
        return doUnmarshal(streamReader, null);
	}

	@Override
	protected Object unmarshalXmlEventReader(XMLEventReader eventReader) throws XmlMappingException {
		try {
			XMLStreamReader streamReader = StaxUtils.createEventStreamReader(eventReader);
			return unmarshalXmlStreamReader(streamReader);
		}
		catch (XMLStreamException ex) {
			throw convertXStreamException(ex, false);
		}
	}

	@Override
	protected Object unmarshalXmlStreamReader(XMLStreamReader streamReader) throws XmlMappingException {
        return doUnmarshal(new StaxReader(new QNameMap(), streamReader, this.nameCoder), null);
	}

	@Override
	protected Object unmarshalSaxReader(XMLReader xmlReader, InputSource inputSource)
			throws XmlMappingException, IOException {

		throw new UnsupportedOperationException(
				"XStreamMarshaller does not support unmarshalling using SAX XMLReaders");
	}

	@Override
	public Object unmarshalInputStream(InputStream inputStream) throws XmlMappingException, IOException {
		return unmarshalInputStream(inputStream, null);
	}

	public Object unmarshalInputStream(InputStream inputStream, DataHolder dataHolder) throws XmlMappingException, IOException {
        if (this.streamDriver != null) {
            return doUnmarshal(this.streamDriver.createReader(inputStream), dataHolder);
        }
        else {
		    return unmarshalReader(new InputStreamReader(inputStream, this.encoding), dataHolder);
        }
	}

	@Override
	public Object unmarshalReader(Reader reader) throws XmlMappingException, IOException {
		return unmarshalReader(reader, null);
	}

	public Object unmarshalReader(Reader reader, DataHolder dataHolder) throws XmlMappingException, IOException {
		return doUnmarshal(getDefaultDriver().createReader(reader), dataHolder);
	}

    /**
     * Unmarshals the given graph to the given XStream HierarchicalStreamWriter.
     * Converts exceptions using {@link #convertXStreamException}.
     * <p>
     *  将给定的图解组给给定的XStream HierarchicalStreamWriter使用{@link #convertXStreamException}转换异常
     * 
     */
    private Object doUnmarshal(HierarchicalStreamReader streamReader, DataHolder dataHolder) {
        try {
            return getXStream().unmarshal(streamReader, null, dataHolder);
        }
        catch (Exception ex) {
            throw convertXStreamException(ex, false);
        }
    }


    /**
     * Convert the given XStream exception to an appropriate exception from the
     * {@code org.springframework.oxm} hierarchy.
     * <p>A boolean flag is used to indicate whether this exception occurs during marshalling or
     * unmarshalling, since XStream itself does not make this distinction in its exception hierarchy.
     * <p>
     * 将给定的XStream异常转换为{@code orgspringframeworkoxm}层次结构中的适当异常<p>布尔标志用于指示在编组或解组期间是否发生此异常,因为XStream本身不会在其异常层次
     * 结构中进行区分。
     * 
     * @param ex XStream exception that occurred
     * @param marshalling indicates whether the exception occurs during marshalling ({@code true}),
     * or unmarshalling ({@code false})
     * @return the corresponding {@code XmlMappingException}
     */
	protected XmlMappingException convertXStreamException(Exception ex, boolean marshalling) {
		if (ex instanceof StreamException || ex instanceof CannotResolveClassException ||
				ex instanceof ConversionException) {
			if (marshalling) {
				return new MarshallingFailureException("XStream marshalling exception",  ex);
			}
			else {
				return new UnmarshallingFailureException("XStream unmarshalling exception", ex);
			}
		}
		else {
			// fallback
			return new UncategorizedMappingException("Unknown XStream exception", ex);
		}
	}

}
