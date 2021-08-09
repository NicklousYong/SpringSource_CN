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

package org.springframework.oxm.castor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.util.ObjectFactory;
import org.exolab.castor.xml.IDResolver;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.ResolverException;
import org.exolab.castor.xml.UnmarshalHandler;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.exolab.castor.xml.XMLClassDescriptorResolver;
import org.exolab.castor.xml.XMLContext;
import org.exolab.castor.xml.XMLException;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.oxm.MarshallingFailureException;
import org.springframework.oxm.UncategorizedMappingException;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.oxm.ValidationFailureException;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.support.AbstractMarshaller;
import org.springframework.oxm.support.SaxResourceUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.xml.DomUtils;
import org.springframework.util.xml.StaxUtils;

/**
 * Implementation of the {@code Marshaller} interface for Castor. By default, Castor does
 * not require any further configuration, though setting target classes, target packages or
 * providing a mapping file can be used to have more control over the behavior of Castor.
 *
 * <p>If a target class is specified using {@code setTargetClass}, the {@code CastorMarshaller}
 * can only be used to unmarshal XML that represents that specific class. If you want to unmarshal
 * multiple classes, you have to provide a mapping file using {@code setMappingLocations}.
 *
 * <p>Due to limitations of Castor's API, it is required to set the encoding used for
 * writing to output streams. It defaults to {@code UTF-8}.
 *
 * <p>
 * Castor的{@code Marshaller}接口的实现默认情况下,Castor不需要任何进一步的配置,尽管设置目标类,目标包或提供映射文件可用于更好地控制Castor的行为
 * 
 *  <p>如果使用{@code setTargetClass}指定了目标类,则{@code CastorMarshaller}只能用于解组表示该特定类的XML。
 * 如果要解组多个类,则必须使用{@code setMappingLocations}。
 * 
 *  <p>由于Castor的API的限制,需要将用于写入的编码设置为输出流默认为{@code UTF-8}
 * 
 * 
 * @author Arjen Poutsma
 * @author Jakub Narloch
 * @author Juergen Hoeller
 * @since 3.0
 * @see #setEncoding(String)
 * @see #setTargetClass(Class)
 * @see #setTargetPackages(String[])
 * @see #setMappingLocation(Resource)
 * @see #setMappingLocations(Resource[])
 */
public class CastorMarshaller extends AbstractMarshaller implements InitializingBean, BeanClassLoaderAware {

	/**
	 * The default encoding used for stream access: UTF-8.
	 * <p>
	 *  用于流访问的默认编码：UTF-8
	 * 
	 */
	public static final String DEFAULT_ENCODING = "UTF-8";


	private Resource[] mappingLocations;

	private String encoding = DEFAULT_ENCODING;

	private Class<?>[] targetClasses;

	private String[] targetPackages;

	private boolean validating = false;

	private boolean suppressNamespaces = false;

	private boolean suppressXsiType = false;

	private boolean marshalAsDocument = true;

	private boolean marshalExtendedType = true;

	private String rootElement;

	private String noNamespaceSchemaLocation;

	private String schemaLocation;

	private boolean useXSITypeAtRoot = false;

	private boolean whitespacePreserve = false;

	private boolean ignoreExtraAttributes = true;

	private boolean ignoreExtraElements = false;

	private Object rootObject;

	private boolean reuseObjects = false;

	private boolean clearCollections = false;

	private Map<String, String> castorProperties;

	private Map<String, String> doctypes;

	private Map<String, String> processingInstructions;

	private Map<String, String> namespaceMappings;

	private Map<String, String> namespaceToPackageMapping;

	private EntityResolver entityResolver;

	private XMLClassDescriptorResolver classDescriptorResolver;

	private IDResolver idResolver;

	private ObjectFactory objectFactory;

	private ClassLoader beanClassLoader;

	private XMLContext xmlContext;


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
	 * Set the locations of the Castor XML mapping files.
	 * <p>
	 * 设置Castor XML映射文件的位置
	 * 
	 */
	public void setMappingLocation(Resource mappingLocation) {
		this.mappingLocations = new Resource[]{mappingLocation};
	}

	/**
	 * Set the locations of the Castor XML mapping files.
	 * <p>
	 *  设置Castor XML映射文件的位置
	 * 
	 */
	public void setMappingLocations(Resource... mappingLocations) {
		this.mappingLocations = mappingLocations;
	}

	/**
	 * Set the Castor target class.
	 * <p>
	 *  设置Castor目标类
	 * 
	 * 
	 * @see #setTargetPackage
	 * @see #setMappingLocation
	 */
	public void setTargetClass(Class<?> targetClass) {
		this.targetClasses = new Class<?>[] {targetClass};
	}

	/**
	 * Set the Castor target classes.
	 * <p>
	 *  设置Castor目标类
	 * 
	 * 
	 * @see #setTargetPackages
	 * @see #setMappingLocations
	 */
	public void setTargetClasses(Class<?>... targetClasses) {
		this.targetClasses = targetClasses;
	}

	/**
	 * Set the name of a package with the Castor descriptor classes.
	 * <p>
	 *  使用Castor描述符类设置包的名称
	 * 
	 */
	public void setTargetPackage(String targetPackage) {
		this.targetPackages = new String[] {targetPackage};
	}

	/**
	 * Set the names of packages with the Castor descriptor classes.
	 * <p>
	 *  使用Castor描述符类设置包的名称
	 * 
	 */
	public void setTargetPackages(String... targetPackages) {
		this.targetPackages = targetPackages;
	}

	/**
	 * Set whether this marshaller should validate in- and outgoing documents.
	 * <p>Default is {@code false}.
	 * <p>
	 *  设置此编组者是否应验证输入和输出文档<p>默认值为{@code false}
	 * 
	 * 
	 * @see Marshaller#setValidation(boolean)
	 */
	public void setValidating(boolean validating) {
		this.validating = validating;
	}

	/**
	 * Sets whether this marshaller should output namespaces.
	 * <p>The default is {@code false}, i.e. namespaces are written.
	 * <p>
	 *  设置此编组器是否应输出命名空间<p>默认值为{@code false},即命名空间被写入
	 * 
	 * 
	 * @see org.exolab.castor.xml.Marshaller#setSuppressNamespaces(boolean)
	 */
	public void setSuppressNamespaces(boolean suppressNamespaces) {
		this.suppressNamespaces = suppressNamespaces;
	}

	/**
	 * Set whether this marshaller should output the {@code xsi:type} attribute.
	 * <p>The default is {@code false}, i.e. the {@code xsi:type} is written.
	 * <p>
	 *  设置此编组器是否应输出{@code xsi：type}属性<p>默认值为{@code false},即{@code xsi：type}被写入
	 * 
	 * 
	 * @see org.exolab.castor.xml.Marshaller#setSuppressXSIType(boolean)
	 */
	public void setSuppressXsiType(boolean suppressXsiType) {
		this.suppressXsiType = suppressXsiType;
	}

	/**
	 * Set whether this marshaller should output the xml declaration.
	 * <p>The default is {@code true}, the XML declaration will be written.
	 * <p>
	 *  设置这个编组器是否应该输出xml声明<p>默认是{@code true},XML声明将被写入
	 * 
	 * 
	 * @see org.exolab.castor.xml.Marshaller#setMarshalAsDocument(boolean)
	 */
	public void setMarshalAsDocument(boolean marshalAsDocument) {
		this.marshalAsDocument = marshalAsDocument;
	}

	/**
	 * Set whether this marshaller should output for given type the {@code xsi:type} attribute.
	 * <p>The default is {@code true}, the {@code xsi:type} attribute will be written.
	 * <p>
	 * 设置这个编组器是否应该为给定类型输出{@code xsi：type}属性<p>默认值为{@code true},{@code xsi：type}属性将被写入
	 * 
	 * 
	 * @see org.exolab.castor.xml.Marshaller#setMarshalExtendedType(boolean)
	 */
	public void setMarshalExtendedType(boolean marshalExtendedType) {
		this.marshalExtendedType = marshalExtendedType;
	}

	/**
	 * Set the name of the root element.
	 * <p>
	 *  设置根元素的名称
	 * 
	 * 
	 * @see org.exolab.castor.xml.Marshaller#setRootElement(String)
	 */
	public void setRootElement(String rootElement) {
		this.rootElement = rootElement;
	}

	/**
	 * Set the value of {@code xsi:noNamespaceSchemaLocation} attribute. When set, the
	 * {@code xsi:noNamespaceSchemaLocation} attribute will be written for the root element.
	 * <p>
	 *  设置{@code xsi：noNamespaceSchemaLocation}属性的值设置时,将为根元素写入{@code xsi：noNamespaceSchemaLocation}属性
	 * 
	 * 
	 * @see org.exolab.castor.xml.Marshaller#setNoNamespaceSchemaLocation(String)
	 */
	public void setNoNamespaceSchemaLocation(String noNamespaceSchemaLocation) {
		this.noNamespaceSchemaLocation = noNamespaceSchemaLocation;
	}

	/**
	 * Set the value of {@code xsi:schemaLocation} attribute. When set, the
	 * {@code xsi:schemaLocation} attribute will be written for the root element.
	 * <p>
	 *  设置{@code xsi：schemaLocation}属性的值设置时,将为根元素写入{@code xsi：schemaLocation}属性
	 * 
	 * 
	 * @see org.exolab.castor.xml.Marshaller#setSchemaLocation(String)
	 */
	public void setSchemaLocation(String schemaLocation) {
		this.schemaLocation = schemaLocation;
	}

	/**
	 * Sets whether this marshaller should output the {@code xsi:type} attribute for the root element.
	 * This can be useful when the type of the element can not be simply determined from the element name.
	 * <p>The default is {@code false}: The {@code xsi:type} attribute for the root element won't be written.
	 * <p>
	 * 设置此编组器是否应输出根元素的{@code xsi：type}属性当元素类型不能从元素名称<p>中简单确定时,这可能非常有用缺省值为{@code false}：根元素的{@code xsi：type}属
	 * 性将不会被写入。
	 * 
	 * 
	 * @see org.exolab.castor.xml.Marshaller#setUseXSITypeAtRoot(boolean)
	 */
	public void setUseXSITypeAtRoot(boolean useXSITypeAtRoot) {
		this.useXSITypeAtRoot = useXSITypeAtRoot;
	}

	/**
	 * Set whether the Castor {@link Unmarshaller} should preserve "ignorable" whitespace.
	 * <p>Default is {@code false}.
	 * <p>
	 *  设置Castor {@link Unmarshaller}是否应保留"可忽略"空格<p>默认值为{@code false}
	 * 
	 * 
	 * @see org.exolab.castor.xml.Unmarshaller#setWhitespacePreserve(boolean)
	 */
	public void setWhitespacePreserve(boolean whitespacePreserve) {
		this.whitespacePreserve = whitespacePreserve;
	}

	/**
	 * Set whether the Castor {@link Unmarshaller} should ignore attributes that do not match a specific field.
	 * <p>Default is {@code true}: Extra attributes are ignored.
	 * <p>
	 *  设置Castor {@link Unmarshaller}是否应忽略与特定字段不匹配的属性<p>默认值为{@code true}：忽略额外的属性
	 * 
	 * 
	 * @see org.exolab.castor.xml.Unmarshaller#setIgnoreExtraAttributes(boolean)
	 */
	public void setIgnoreExtraAttributes(boolean ignoreExtraAttributes) {
		this.ignoreExtraAttributes = ignoreExtraAttributes;
	}

	/**
	 * Set whether the Castor {@link Unmarshaller} should ignore elements that do not match a specific field.
	 * <p>Default is {@code false}: Extra elements are flagged as an error.
	 * <p>
	 *  设置Castor {@link Unmarshaller}是否应该忽略与特定字段不匹配的元素<p>默认值为{@code false}：额外的元素被标记为错误
	 * 
	 * 
	 * @see org.exolab.castor.xml.Unmarshaller#setIgnoreExtraElements(boolean)
	 */
	public void setIgnoreExtraElements(boolean ignoreExtraElements) {
		this.ignoreExtraElements = ignoreExtraElements;
	}

	/**
	 * Set the expected root object for the unmarshaller, into which the source will be unmarshalled.
	 * <p>
	 * 设置unmarshaller的预期根对象,源将被解组
	 * 
	 * 
	 * @see org.exolab.castor.xml.Unmarshaller#setObject(Object)
	 */
	public void setRootObject(Object root) {
		this.rootObject = root;
	}

	/**
	 * Set whether this unmarshaller should re-use objects.
	 * This will be only used when unmarshalling to an existing object.
	 * <p>The default is {@code false}, which means that the objects won't be re-used.
	 * <p>
	 *  设置此解组器是否应重新使用对象这将仅在解组到现有对象时使用<p>默认值为{@code false},这意味着对象不会被重新使用
	 * 
	 * 
	 * @see org.exolab.castor.xml.Unmarshaller#setReuseObjects(boolean)
	 */
	public void setReuseObjects(boolean reuseObjects) {
		this.reuseObjects = reuseObjects;
	}

	/**
	 * Sets whether this unmarshaller should clear collections upon the first use.
	 * <p>The default is {@code false} which means that marshaller won't clear collections.
	 * <p>
	 *  设置这个解组器是否应该在首次使用时清除集合<p>默认是{@code false},这意味着编组者不会清除集合
	 * 
	 * 
	 * @see org.exolab.castor.xml.Unmarshaller#setClearCollections(boolean)
	 */
	public void setClearCollections(boolean clearCollections) {
		this.clearCollections = clearCollections;
	}

	/**
	 * Set Castor-specific properties for marshalling and unmarshalling.
	 * Each entry key is considered the property name and each value the property value.
	 * <p>
	 *  设置用于编组和解组合的特定于Castor的属性每个条目键都被认为是属性名称,每个值都是属性值
	 * 
	 * 
	 * @see org.exolab.castor.xml.Marshaller#setProperty(String, String)
	 * @see org.exolab.castor.xml.Unmarshaller#setProperty(String, String)
	 */
	public void setCastorProperties(Map<String, String> castorProperties) {
		this.castorProperties = castorProperties;
	}

	/**
	 * Set the map containing document type definition for the marshaller.
	 * Each entry has system id as key and public id as value.
	 * <p>
	 *  设置包含marshaller的文档类型定义的地图每个条目的系统ID为key,public id为value
	 * 
	 * 
	 * @see org.exolab.castor.xml.Marshaller#setDoctype(String, String)
	 */
	public void setDoctypes(Map<String, String> doctypes) {
		this.doctypes = doctypes;
	}

	/**
	 * Sets the processing instructions that will be used by during marshalling.
	 * Keys are the processing targets and values contain the processing data.
	 * <p>
	 * 设置编组期间将使用的处理指令。键是处理目标和值包含处理数据
	 * 
	 * 
	 * @see org.exolab.castor.xml.Marshaller#addProcessingInstruction(String, String)
	 */
	public void setProcessingInstructions(Map<String, String> processingInstructions) {
		this.processingInstructions = processingInstructions;
	}

	/**
	 * Set the namespace mappings.
	 * Property names are interpreted as namespace prefixes; values are namespace URIs.
	 * <p>
	 *  设置命名空间映射属性名称被解释为命名空间前缀;值是命名空间URI
	 * 
	 * 
	 * @see org.exolab.castor.xml.Marshaller#setNamespaceMapping(String, String)
	 */
	public void setNamespaceMappings(Map<String, String> namespaceMappings) {
		this.namespaceMappings = namespaceMappings;
	}

	/**
	 * Set the namespace to package mappings. Property names are represents the namespaces URI, values are packages.
	 * <p>
	 *  将命名空间设置为包映射属性名称表示命名空间URI,值是包
	 * 
	 * 
	 * @see org.exolab.castor.xml.Marshaller#setNamespaceMapping(String, String)
	 */
	public void setNamespaceToPackageMapping(Map<String, String> namespaceToPackageMapping) {
		this.namespaceToPackageMapping = namespaceToPackageMapping;
	}

	/**
	 * Set the {@link EntityResolver} to be used during unmarshalling.
	 * This resolver will used to resolve system and public ids.
	 * <p>
	 *  设置在解组期间使用的{@link EntityResolver}此解析器将用于解析系统和公共ID
	 * 
	 * 
	 * @see org.exolab.castor.xml.Unmarshaller#setEntityResolver(EntityResolver)
	 */
	public void setEntityResolver(EntityResolver entityResolver) {
		this.entityResolver = entityResolver;
	}

	/**
	 * Set the {@link XMLClassDescriptorResolver} to be used during unmarshalling.
	 * This resolver will used to resolve class descriptors.
	 * <p>
	 *  设置在解组期间使用的{@link XMLClassDescriptorResolver}此解析器将用于解析类描述符
	 * 
	 * 
	 * @see org.exolab.castor.xml.Unmarshaller#setResolver(XMLClassDescriptorResolver)
	 */
	public void setClassDescriptorResolver(XMLClassDescriptorResolver classDescriptorResolver) {
		this.classDescriptorResolver = classDescriptorResolver;
	}

	/**
	 * Set the Castor {@link IDResolver} to be used during unmarshalling.
	 * <p>
	 *  设置在解组时使用的Castor {@link IDResolver}
	 * 
	 * 
	 * @see org.exolab.castor.xml.Unmarshaller#setIDResolver(IDResolver)
	 */
	public void setIdResolver(IDResolver idResolver) {
		this.idResolver = idResolver;
	}

	/**
	 * Set the Castor {@link ObjectFactory} to be used during unmarshalling.
	 * <p>
	 *  设置在解组期间使用的Castor {@link ObjectFactory}
	 * 
	 * 
	 * @see org.exolab.castor.xml.Unmarshaller#setObjectFactory(ObjectFactory)
	 */
	public void setObjectFactory(ObjectFactory objectFactory) {
		this.objectFactory = objectFactory;
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.beanClassLoader = classLoader;
	}


	@Override
	public void afterPropertiesSet() throws CastorMappingException, IOException {
		try {
			this.xmlContext = createXMLContext(this.mappingLocations, this.targetClasses, this.targetPackages);
		}
		catch (MappingException ex) {
			throw new CastorMappingException("Could not load Castor mapping", ex);
		}
		catch (ResolverException ex) {
			throw new CastorMappingException("Could not resolve Castor mapping", ex);
		}
	}

	/**
	 * Create the Castor {@code XMLContext}. Subclasses can override this to create a custom context.
	 * <p>The default implementation loads mapping files if defined, or the target class or packages if defined.
	 * <p>
	 * 创建Castor {@code XMLContext}子类可以覆盖此值以创建自定义上下文<p>如果已定义,默认实现将加载映射文件,或者如果定义了目标类或包
	 * 
	 * 
	 * @return the created resolver
	 * @throws MappingException when the mapping file cannot be loaded
	 * @throws IOException in case of I/O errors
	 * @see XMLContext#addMapping(org.exolab.castor.mapping.Mapping)
	 * @see XMLContext#addClass(Class)
	 */
	protected XMLContext createXMLContext(Resource[] mappingLocations, Class<?>[] targetClasses,
			String[] targetPackages) throws MappingException, ResolverException, IOException {

		XMLContext context = new XMLContext();
		if (!ObjectUtils.isEmpty(mappingLocations)) {
			Mapping mapping = new Mapping();
			for (Resource mappingLocation : mappingLocations) {
				mapping.loadMapping(SaxResourceUtils.createInputSource(mappingLocation));
			}
			context.addMapping(mapping);
		}
		if (!ObjectUtils.isEmpty(targetClasses)) {
			context.addClasses(targetClasses);
		}
		if (!ObjectUtils.isEmpty(targetPackages)) {
			context.addPackages(targetPackages);
		}
		if (this.castorProperties != null) {
			for (Map.Entry<String, String> property : this.castorProperties.entrySet()) {
				context.setProperty(property.getKey(), property.getValue());
			}
		}
		return context;
	}


	/**
	 * Returns {@code true} for all classes, i.e. Castor supports arbitrary classes.
	 * <p>
	 *  返回所有类的{@code true},即Castor支持任意类
	 * 
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return true;
	}


	// Marshalling

	@Override
	protected void marshalDomNode(Object graph, Node node) throws XmlMappingException {
		marshalSaxHandlers(graph, DomUtils.createContentHandler(node), null);
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
		ContentHandler contentHandler = StaxUtils.createContentHandler(streamWriter);
		LexicalHandler lexicalHandler = null;
		if (contentHandler instanceof LexicalHandler) {
			lexicalHandler = (LexicalHandler) contentHandler;
		}
		marshalSaxHandlers(graph, StaxUtils.createContentHandler(streamWriter), lexicalHandler);
	}

	@Override
	protected void marshalSaxHandlers(Object graph, ContentHandler contentHandler, LexicalHandler lexicalHandler)
			throws XmlMappingException {

		Marshaller marshaller = xmlContext.createMarshaller();
		marshaller.setContentHandler(contentHandler);
		doMarshal(graph, marshaller);
	}

	@Override
	protected void marshalOutputStream(Object graph, OutputStream outputStream) throws XmlMappingException, IOException {
		marshalWriter(graph, new OutputStreamWriter(outputStream, encoding));
	}

	@Override
	protected void marshalWriter(Object graph, Writer writer) throws XmlMappingException, IOException {
		Marshaller marshaller = xmlContext.createMarshaller();
		marshaller.setWriter(writer);
		doMarshal(graph, marshaller);
	}

	private void doMarshal(Object graph, Marshaller marshaller) {
		try {
			customizeMarshaller(marshaller);
			marshaller.marshal(graph);
		}
		catch (XMLException ex) {
			throw convertCastorException(ex, true);
		}
	}

	/**
	 * Template method that allows for customizing of the given Castor {@link Marshaller}.
	 * <p>
	 *  允许定制给定Castor的模板方法{@link Marshaller}
	 * 
	 */
	protected void customizeMarshaller(Marshaller marshaller) {
		marshaller.setValidation(this.validating);
		marshaller.setSuppressNamespaces(this.suppressNamespaces);
		marshaller.setSuppressXSIType(this.suppressXsiType);
		marshaller.setMarshalAsDocument(this.marshalAsDocument);
		marshaller.setMarshalExtendedType(this.marshalExtendedType);
		marshaller.setRootElement(this.rootElement);
		marshaller.setNoNamespaceSchemaLocation(this.noNamespaceSchemaLocation);
		marshaller.setSchemaLocation(this.schemaLocation);
		marshaller.setUseXSITypeAtRoot(this.useXSITypeAtRoot);
		if (this.doctypes != null) {
			for (Map.Entry<String, String> doctype : this.doctypes.entrySet()) {
				marshaller.setDoctype(doctype.getKey(), doctype.getValue());
			}
		}
		if (this.processingInstructions != null) {
			for (Map.Entry<String, String> processingInstruction : this.processingInstructions.entrySet()) {
				marshaller.addProcessingInstruction(processingInstruction.getKey(), processingInstruction.getValue());
			}
		}
		if (this.namespaceMappings != null) {
			for (Map.Entry<String, String> entry : this.namespaceMappings.entrySet()) {
				marshaller.setNamespaceMapping(entry.getKey(), entry.getValue());
			}
		}
	}


	// Unmarshalling

	@Override
	protected Object unmarshalDomNode(Node node) throws XmlMappingException {
		try {
			return createUnmarshaller().unmarshal(node);
		}
		catch (XMLException ex) {
			throw convertCastorException(ex, false);
		}
	}

	@Override
	protected Object unmarshalXmlEventReader(XMLEventReader eventReader) {
		try {
			return createUnmarshaller().unmarshal(eventReader);
		}
		catch (XMLException ex) {
			throw convertCastorException(ex, false);
		}
	}

	@Override
	protected Object unmarshalXmlStreamReader(XMLStreamReader streamReader) {
		try {
			return createUnmarshaller().unmarshal(streamReader);
		}
		catch (XMLException ex) {
			throw convertCastorException(ex, false);
		}
	}

	@Override
	protected Object unmarshalSaxReader(XMLReader xmlReader, InputSource inputSource)
			throws XmlMappingException, IOException {

		UnmarshalHandler unmarshalHandler = createUnmarshaller().createHandler();
		try {
			ContentHandler contentHandler = Unmarshaller.getContentHandler(unmarshalHandler);
			xmlReader.setContentHandler(contentHandler);
			xmlReader.parse(inputSource);
			return unmarshalHandler.getObject();
		}
		catch (SAXException ex) {
			throw new UnmarshallingFailureException("SAX reader exception", ex);
		}
	}

	@Override
	protected Object unmarshalInputStream(InputStream inputStream) throws XmlMappingException, IOException {
		try {
			return createUnmarshaller().unmarshal(new InputSource(inputStream));
		}
		catch (XMLException ex) {
			throw convertCastorException(ex, false);
		}
	}

	@Override
	protected Object unmarshalReader(Reader reader) throws XmlMappingException, IOException {
		try {
			return createUnmarshaller().unmarshal(new InputSource(reader));
		}
		catch (XMLException ex) {
			throw convertCastorException(ex, false);
		}
	}

	private Unmarshaller createUnmarshaller() {
		Unmarshaller unmarshaller = this.xmlContext.createUnmarshaller();
		customizeUnmarshaller(unmarshaller);
		return unmarshaller;
	}

	/**
	 * Template method that allows for customizing of the given Castor {@link Unmarshaller}.
	 * <p>
	 *  模板方法,允许定制给定的Castor {@link Unmarshaller}
	 * 
	 */
	protected void customizeUnmarshaller(Unmarshaller unmarshaller) {
		unmarshaller.setValidation(this.validating);
		unmarshaller.setWhitespacePreserve(this.whitespacePreserve);
		unmarshaller.setIgnoreExtraAttributes(this.ignoreExtraAttributes);
		unmarshaller.setIgnoreExtraElements(this.ignoreExtraElements);
		unmarshaller.setObject(this.rootObject);
		unmarshaller.setReuseObjects(this.reuseObjects);
		unmarshaller.setClearCollections(this.clearCollections);
		if (this.namespaceToPackageMapping != null) {
			for (Map.Entry<String, String> mapping : this.namespaceToPackageMapping.entrySet()) {
				unmarshaller.addNamespaceToPackageMapping(mapping.getKey(), mapping.getValue());
			}
		}
		if (this.entityResolver != null) {
			unmarshaller.setEntityResolver(this.entityResolver);
		}
		if (this.classDescriptorResolver != null) {
			unmarshaller.setResolver(this.classDescriptorResolver);
		}
		if (this.idResolver != null) {
			unmarshaller.setIDResolver(this.idResolver);
		}
		if (this.objectFactory != null) {
			unmarshaller.setObjectFactory(this.objectFactory);
		}
		if (this.beanClassLoader != null) {
			unmarshaller.setClassLoader(this.beanClassLoader);
		}
	}


	/**
	 * Convert the given {@code XMLException} to an appropriate exception from the
	 * {@code org.springframework.oxm} hierarchy.
	 * <p>A boolean flag is used to indicate whether this exception occurs during marshalling or
	 * unmarshalling, since Castor itself does not make this distinction in its exception hierarchy.
	 * <p>
	 *  将给定的{@code XMLException}转换为{@code orgspringframeworkoxm}层次结构中的适当异常<p>布尔标志用于指示在编组或解组时是否发生此异常,因为Castor
	 * 本身在其异常中没有区分等级制度。
	 * 
	 * @param ex Castor {@code XMLException} that occurred
	 * @param marshalling indicates whether the exception occurs during marshalling ({@code true}),
	 * or unmarshalling ({@code false})
	 * @return the corresponding {@code XmlMappingException}
	 */
	protected XmlMappingException convertCastorException(XMLException ex, boolean marshalling) {
		if (ex instanceof ValidationException) {
			return new ValidationFailureException("Castor validation exception", ex);
		}
		else if (ex instanceof MarshalException) {
			if (marshalling) {
				return new MarshallingFailureException("Castor marshalling exception", ex);
			}
			else {
				return new UnmarshallingFailureException("Castor unmarshalling exception", ex);
			}
		}
		else {
			// fallback
			return new UncategorizedMappingException("Unknown Castor exception", ex);
		}
	}

}
