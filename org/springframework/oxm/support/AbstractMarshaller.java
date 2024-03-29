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

package org.springframework.oxm.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.oxm.XmlMappingException;
import org.springframework.util.Assert;
import org.springframework.util.xml.StaxUtils;

/**
 * Abstract implementation of the {@code Marshaller} and {@code Unmarshaller} interface.
 * This implementation inspects the given {@code Source} or {@code Result}, and
 * delegates further handling to overridable template methods.
 *
 * <p>
 *  {@code Marshaller}和{@code Unmarshaller}接口的抽象实现该实现检查给定的{@code Source}或{@code Result},并将进一步处理委托给可覆盖的模板
 * 方法。
 * 
 * 
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @since 3.0
 */
public abstract class AbstractMarshaller implements Marshaller, Unmarshaller {

	/** Logger available to subclasses */
	protected final Log logger = LogFactory.getLog(getClass());

	private boolean supportDtd = false;

	private boolean processExternalEntities = false;

	private DocumentBuilderFactory documentBuilderFactory;

	private final Object documentBuilderFactoryMonitor = new Object();


	/**
	 * Indicates whether DTD parsing should be supported.
	 * <p>Default is {@code false} meaning that DTD is disabled.
	 * <p>
	 * 指示是否应支持DTD解析<p>默认值为{@code false},表示禁用DTD
	 * 
	 */
	public void setSupportDtd(boolean supportDtd) {
		this.supportDtd = supportDtd;
	}

	/**
	 * Whether DTD parsing is supported.
	 * <p>
	 *  是否支持DTD解析
	 * 
	 */
	public boolean isSupportDtd() {
		return this.supportDtd;
	}

	/**
	 * Indicates whether external XML entities are processed when unmarshalling.
	 * <p>Default is {@code false}, meaning that external entities are not resolved.
	 * Note that processing of external entities will only be enabled/disabled when the
	 * {@code Source} passed to {@link #unmarshal(Source)} is a {@link SAXSource} or
	 * {@link StreamSource}. It has no effect for {@link DOMSource} or {@link StAXSource}
	 * instances.
	 * <p><strong>Note:</strong> setting this option to {@code true} also
	 * automatically sets {@link #setSupportDtd} to {@code true}.
	 * <p>
	 *  指示在解组时是否处理外部XML实体<p>默认值为{@code false},意味着外部实体未解析注意,只有当{@code Source}传递给{@code}时,才会启用/禁用外部实体的处理链接#unm
	 * arshal(Source)}是{@link SAXSource}或{@link StreamSource}对{@link DOMSource}或{@link StAXSource}实例<p> <strong>
	 * 注意</strong>设置没有影响{@code true}的此选项也会自动将{@link #setSupportDtd}设置为{@code true}。
	 * 
	 */
	public void setProcessExternalEntities(boolean processExternalEntities) {
		this.processExternalEntities = processExternalEntities;
		if (processExternalEntities) {
			setSupportDtd(true);
		}
	}

	/**
	 * Returns the configured value for whether XML external entities are allowed.
	 * <p>
	 *  返回配置的值是否允许XML外部实体
	 * 
	 * 
	 * @see #createXmlReader()
	 */
	public boolean isProcessExternalEntities() {
		return this.processExternalEntities;
	}


	/**
	 * Build a new {@link Document} from this marshaller's {@link DocumentBuilderFactory},
	 * as a placeholder for a DOM node.
	 * <p>
	 * 从这个编组者的{@link DocumentBuilderFactory}中构建一个新的{@link Document},作为DOM节点的占位符
	 * 
	 * 
	 * @see #createDocumentBuilderFactory()
	 * @see #createDocumentBuilder(DocumentBuilderFactory)
	 */
	protected Document buildDocument() {
		try {
			DocumentBuilder documentBuilder;
			synchronized (this.documentBuilderFactoryMonitor) {
				if (this.documentBuilderFactory == null) {
					this.documentBuilderFactory = createDocumentBuilderFactory();
				}
				documentBuilder = createDocumentBuilder(this.documentBuilderFactory);
			}
			return documentBuilder.newDocument();
		}
		catch (ParserConfigurationException ex) {
			throw new UnmarshallingFailureException("Could not create document placeholder: " + ex.getMessage(), ex);
		}
	}

	/**
	 * Create a {@code DocumentBuilder} that this marshaller will use for creating
	 * DOM documents when passed an empty {@code DOMSource}.
	 * <p>The resulting {@code DocumentBuilderFactory} is cached, so this method
	 * will only be called once.
	 * <p>
	 *  创建一个{@code DocumentBuilder},这个编组者将用于在传递一个空的{@code DOMSource}时用于创建DOM文档<p>生成的{@code DocumentBuilderFactory}
	 * 被缓存,所以这种方法只会被调用一次。
	 * 
	 * 
	 * @return the DocumentBuilderFactory
	 * @throws ParserConfigurationException if thrown by JAXP methods
	 */
	protected DocumentBuilderFactory createDocumentBuilderFactory() throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(true);
		factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", !isSupportDtd());
		factory.setFeature("http://xml.org/sax/features/external-general-entities", isProcessExternalEntities());
		return factory;
	}

	/**
	 * Create a {@code DocumentBuilder} that this marshaller will use for creating
	 * DOM documents when passed an empty {@code DOMSource}.
	 * <p>Can be overridden in subclasses, adding further initialization of the builder.
	 * <p>
	 *  创建一个{@code DocumentBuilder},这个编组者将在传递一个空的{@code DOMSource}时用于创建DOM文档<p>可以在子类中覆盖,添加进一步初始化构建器
	 * 
	 * 
	 * @param factory the {@code DocumentBuilderFactory} that the DocumentBuilder should be created with
	 * @return the {@code DocumentBuilder}
	 * @throws ParserConfigurationException if thrown by JAXP methods
	 */
	protected DocumentBuilder createDocumentBuilder(DocumentBuilderFactory factory)
			throws ParserConfigurationException {

		DocumentBuilder documentBuilder = factory.newDocumentBuilder();
		if (!isProcessExternalEntities()) {
			documentBuilder.setEntityResolver(NO_OP_ENTITY_RESOLVER);
		}
		return documentBuilder;
	}

	/**
	 * Create an {@code XMLReader} that this marshaller will when passed an empty {@code SAXSource}.
	 * <p>
	 *  创建一个{@code XMLReader},当这个编组者传递一个空的{@code SAXSource}
	 * 
	 * 
	 * @return the XMLReader
	 * @throws SAXException if thrown by JAXP methods
	 */
	protected XMLReader createXmlReader() throws SAXException {
		XMLReader xmlReader = XMLReaderFactory.createXMLReader();
		xmlReader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", !isSupportDtd());
		xmlReader.setFeature("http://xml.org/sax/features/external-general-entities", isProcessExternalEntities());
		if (!isProcessExternalEntities()) {
			xmlReader.setEntityResolver(NO_OP_ENTITY_RESOLVER);
		}
		return xmlReader;
	}

	/**
	 * Determine the default encoding to use for marshalling or unmarshalling from
	 * a byte stream, or {@code null} if none.
	 * <p>The default implementation returns {@code null}.
	 * <p>
	 * 确定用于从字节流进行编组或解组的默认编码,否则{@code null}如果没有<p>默认实现返回{@code null}
	 * 
	 */
	protected String getDefaultEncoding() {
		return null;
	}


	// Marshalling

	/**
	 * Marshals the object graph with the given root into the provided {@code javax.xml.transform.Result}.
	 * <p>This implementation inspects the given result, and calls {@code marshalDomResult},
	 * {@code marshalSaxResult}, or {@code marshalStreamResult}.
	 * <p>
	 *  将具有给定根的对象图传送到提供的{@code javaxxmltransformResult}中<p>此实现检查给定的结果,并调用{@code marshalDomResult},{@code marshalSaxResult}
	 * 或{@code marshalStreamResult}。
	 * 
	 * 
	 * @param graph the root of the object graph to marshal
	 * @param result the result to marshal to
	 * @throws IOException if an I/O exception occurs
	 * @throws XmlMappingException if the given object cannot be marshalled to the result
	 * @throws IllegalArgumentException if {@code result} if neither a {@code DOMResult},
	 * a {@code SAXResult}, nor a {@code StreamResult}
	 * @see #marshalDomResult(Object, javax.xml.transform.dom.DOMResult)
	 * @see #marshalSaxResult(Object, javax.xml.transform.sax.SAXResult)
	 * @see #marshalStreamResult(Object, javax.xml.transform.stream.StreamResult)
	 */
	@Override
	public final void marshal(Object graph, Result result) throws IOException, XmlMappingException {
		if (result instanceof DOMResult) {
			marshalDomResult(graph, (DOMResult) result);
		}
		else if (StaxUtils.isStaxResult(result)) {
			marshalStaxResult(graph, result);
		}
		else if (result instanceof SAXResult) {
			marshalSaxResult(graph, (SAXResult) result);
		}
		else if (result instanceof StreamResult) {
			marshalStreamResult(graph, (StreamResult) result);
		}
		else {
			throw new IllegalArgumentException("Unknown Result type: " + result.getClass());
		}
	}

	/**
	 * Template method for handling {@code DOMResult}s.
	 * <p>This implementation delegates to {@code marshalDomNode}.
	 * <p>
	 *  用于处理{@code DOMResult}的模板方法<p>此实现委托给{@code marshalDomNode}
	 * 
	 * 
	 * @param graph the root of the object graph to marshal
	 * @param domResult the {@code DOMResult}
	 * @throws XmlMappingException if the given object cannot be marshalled to the result
	 * @throws IllegalArgumentException if the {@code domResult} is empty
	 * @see #marshalDomNode(Object, org.w3c.dom.Node)
	 */
	protected void marshalDomResult(Object graph, DOMResult domResult) throws XmlMappingException {
		if (domResult.getNode() == null) {
			domResult.setNode(buildDocument());
		}
		marshalDomNode(graph, domResult.getNode());
	}

	/**
	 * Template method for handling {@code StaxResult}s.
	 * <p>This implementation delegates to {@code marshalXMLSteamWriter} or
	 * {@code marshalXMLEventConsumer}, depending on what is contained in the
	 * {@code StaxResult}.
	 * <p>
	 *  用于处理{@code StaxResult}的模板方法<p>根据{@code StaxResult}中包含的内容,此实现将委托给{@code marshalXMLSteamWriter}或{@code marshalXMLEventConsumer}
	 * 。
	 * 
	 * 
	 * @param graph the root of the object graph to marshal
	 * @param staxResult a JAXP 1.4 {@link StAXSource}
	 * @throws XmlMappingException if the given object cannot be marshalled to the result
	 * @throws IllegalArgumentException if the {@code domResult} is empty
	 * @see #marshalDomNode(Object, org.w3c.dom.Node)
	 */
	protected void marshalStaxResult(Object graph, Result staxResult) throws XmlMappingException {
		XMLStreamWriter streamWriter = StaxUtils.getXMLStreamWriter(staxResult);
		if (streamWriter != null) {
			marshalXmlStreamWriter(graph, streamWriter);
		}
		else {
			XMLEventWriter eventWriter = StaxUtils.getXMLEventWriter(staxResult);
			if (eventWriter != null) {
				marshalXmlEventWriter(graph, eventWriter);
			}
			else {
				throw new IllegalArgumentException("StaxResult contains neither XMLStreamWriter nor XMLEventConsumer");
			}
		}
	}

	/**
	 * Template method for handling {@code SAXResult}s.
	 * <p>This implementation delegates to {@code marshalSaxHandlers}.
	 * <p>
	 * 用于处理{@code SAXResult}的模板方法<p>此实现委托给{@code marshalSaxHandlers}
	 * 
	 * 
	 * @param graph the root of the object graph to marshal
	 * @param saxResult the {@code SAXResult}
	 * @throws XmlMappingException if the given object cannot be marshalled to the result
	 * @see #marshalSaxHandlers(Object, org.xml.sax.ContentHandler, org.xml.sax.ext.LexicalHandler)
	 */
	protected void marshalSaxResult(Object graph, SAXResult saxResult) throws XmlMappingException {
		ContentHandler contentHandler = saxResult.getHandler();
		Assert.notNull(contentHandler, "ContentHandler not set on SAXResult");
		LexicalHandler lexicalHandler = saxResult.getLexicalHandler();
		marshalSaxHandlers(graph, contentHandler, lexicalHandler);
	}

	/**
	 * Template method for handling {@code StreamResult}s.
	 * <p>This implementation delegates to {@code marshalOutputStream} or {@code marshalWriter},
	 * depending on what is contained in the {@code StreamResult}
	 * <p>
	 *  用于处理{@code StreamResult}的模板方法<p>根据{@code StreamResult}中包含的内容,此实现将委托给{@code marshalOutputStream}或{@code marshalWriter}
	 * 。
	 * 
	 * 
	 * @param graph the root of the object graph to marshal
	 * @param streamResult the {@code StreamResult}
	 * @throws IOException if an I/O Exception occurs
	 * @throws XmlMappingException if the given object cannot be marshalled to the result
	 * @throws IllegalArgumentException if {@code streamResult} does neither
	 * contain an {@code OutputStream} nor a {@code Writer}
	 */
	protected void marshalStreamResult(Object graph, StreamResult streamResult)
			throws XmlMappingException, IOException {

		if (streamResult.getOutputStream() != null) {
			marshalOutputStream(graph, streamResult.getOutputStream());
		}
		else if (streamResult.getWriter() != null) {
			marshalWriter(graph, streamResult.getWriter());
		}
		else {
			throw new IllegalArgumentException("StreamResult contains neither OutputStream nor Writer");
		}
	}


	// Unmarshalling

	/**
	 * Unmarshals the given provided {@code javax.xml.transform.Source} into an object graph.
	 * <p>This implementation inspects the given result, and calls {@code unmarshalDomSource},
	 * {@code unmarshalSaxSource}, or {@code unmarshalStreamSource}.
	 * <p>
	 *  将给定的{@code javaxxmltransformSource}解组合到对象图中<p>此实现检查给定的结果,并调用{@code unmarshalDomSource},{@code unmarshalSaxSource}
	 * 或{@code unmarshalStreamSource}。
	 * 
	 * 
	 * @param source the source to marshal from
	 * @return the object graph
	 * @throws IOException if an I/O Exception occurs
	 * @throws XmlMappingException if the given source cannot be mapped to an object
	 * @throws IllegalArgumentException if {@code source} is neither a {@code DOMSource},
	 * a {@code SAXSource}, nor a {@code StreamSource}
	 * @see #unmarshalDomSource(javax.xml.transform.dom.DOMSource)
	 * @see #unmarshalSaxSource(javax.xml.transform.sax.SAXSource)
	 * @see #unmarshalStreamSource(javax.xml.transform.stream.StreamSource)
	 */
	@Override
	public final Object unmarshal(Source source) throws IOException, XmlMappingException {
		if (source instanceof DOMSource) {
			return unmarshalDomSource((DOMSource) source);
		}
		else if (StaxUtils.isStaxSource(source)) {
			return unmarshalStaxSource(source);
		}
		else if (source instanceof SAXSource) {
			return unmarshalSaxSource((SAXSource) source);
		}
		else if (source instanceof StreamSource) {
			return unmarshalStreamSource((StreamSource) source);
		}
		else {
			throw new IllegalArgumentException("Unknown Source type: " + source.getClass());
		}
	}

	/**
	 * Template method for handling {@code DOMSource}s.
	 * <p>This implementation delegates to {@code unmarshalDomNode}.
	 * If the given source is empty, an empty source {@code Document}
	 * will be created as a placeholder.
	 * <p>
	 *  用于处理{@code DOMSource}的模板方法<p>这个实现委托给{@code unmarshalDomNode}如果给定的源是空的,将创建一个空的源{@code Document}作为占位符。
	 * 
	 * 
	 * @param domSource the {@code DOMSource}
	 * @return the object graph
	 * @throws XmlMappingException if the given source cannot be mapped to an object
	 * @throws IllegalArgumentException if the {@code domSource} is empty
	 * @see #unmarshalDomNode(org.w3c.dom.Node)
	 */
	protected Object unmarshalDomSource(DOMSource domSource) throws XmlMappingException {
		if (domSource.getNode() == null) {
			domSource.setNode(buildDocument());
		}
		try {
			return unmarshalDomNode(domSource.getNode());
		}
		catch (NullPointerException ex) {
			if (!isSupportDtd()) {
				throw new UnmarshallingFailureException("NPE while unmarshalling. " +
						"This can happen on JDK 1.6 due to the presence of DTD " +
						"declarations, which are disabled.", ex);
			}
			throw ex;
		}
	}

	/**
	 * Template method for handling {@code StaxSource}s.
	 * <p>This implementation delegates to {@code unmarshalXmlStreamReader} or
	 * {@code unmarshalXmlEventReader}.
	 * <p>
	 * 用于处理{@code StaxSource}的模板方法<p>此实现委托{@code unmarshalXmlStreamReader}或{@code unmarshalXmlEventReader}
	 * 
	 * 
	 * @param staxSource the {@code StaxSource}
	 * @return the object graph
	 * @throws XmlMappingException if the given source cannot be mapped to an object
	 */
	protected Object unmarshalStaxSource(Source staxSource) throws XmlMappingException {
		XMLStreamReader streamReader = StaxUtils.getXMLStreamReader(staxSource);
		if (streamReader != null) {
			return unmarshalXmlStreamReader(streamReader);
		}
		else {
			XMLEventReader eventReader = StaxUtils.getXMLEventReader(staxSource);
			if (eventReader != null) {
				return unmarshalXmlEventReader(eventReader);
			}
			else {
				throw new IllegalArgumentException("StaxSource contains neither XMLStreamReader nor XMLEventReader");
			}
		}
	}

	/**
	 * Template method for handling {@code SAXSource}s.
	 * <p>This implementation delegates to {@code unmarshalSaxReader}.
	 * <p>
	 *  用于处理{@code SAXSource}的模板方法<p>此实现委托{@code unmarshalSaxReader}
	 * 
	 * 
	 * @param saxSource the {@code SAXSource}
	 * @return the object graph
	 * @throws XmlMappingException if the given source cannot be mapped to an object
	 * @throws IOException if an I/O Exception occurs
	 * @see #unmarshalSaxReader(org.xml.sax.XMLReader, org.xml.sax.InputSource)
	 */
	protected Object unmarshalSaxSource(SAXSource saxSource) throws XmlMappingException, IOException {
		if (saxSource.getXMLReader() == null) {
			try {
				saxSource.setXMLReader(createXmlReader());
			}
			catch (SAXException ex) {
				throw new UnmarshallingFailureException("Could not create XMLReader for SAXSource", ex);
			}
		}
		if (saxSource.getInputSource() == null) {
			saxSource.setInputSource(new InputSource());
		}
		try {
			return unmarshalSaxReader(saxSource.getXMLReader(), saxSource.getInputSource());
		}
		catch (NullPointerException ex) {
			if (!isSupportDtd()) {
				throw new UnmarshallingFailureException("NPE while unmarshalling. " +
						"This can happen on JDK 1.6 due to the presence of DTD " +
						"declarations, which are disabled.");
			}
			throw ex;
		}
	}

	/**
	 * Template method for handling {@code StreamSource}s.
	 * <p>This implementation delegates to {@code unmarshalInputStream} or {@code unmarshalReader}.
	 * <p>
	 *  用于处理{@code StreamSource}的模板方法<p>此实现委托{@code unmarshalInputStream}或{@code unmarshalReader}
	 * 
	 * 
	 * @param streamSource the {@code StreamSource}
	 * @return the object graph
	 * @throws IOException if an I/O exception occurs
	 * @throws XmlMappingException if the given source cannot be mapped to an object
	 */
	protected Object unmarshalStreamSource(StreamSource streamSource) throws XmlMappingException, IOException {
		if (streamSource.getInputStream() != null) {
			if (isProcessExternalEntities() && isSupportDtd()) {
				return unmarshalInputStream(streamSource.getInputStream());
			}
			else {
				InputSource inputSource = new InputSource(streamSource.getInputStream());
				inputSource.setEncoding(getDefaultEncoding());
				return unmarshalSaxSource(new SAXSource(inputSource));
			}
		}
		else if (streamSource.getReader() != null) {
			if (isProcessExternalEntities() && isSupportDtd()) {
				return unmarshalReader(streamSource.getReader());
			}
			else {
				return unmarshalSaxSource(new SAXSource(new InputSource(streamSource.getReader())));
			}
		}
		else {
			return unmarshalSaxSource(new SAXSource(new InputSource(streamSource.getSystemId())));
		}
	}


	// Abstract template methods

	/**
	 * Abstract template method for marshalling the given object graph to a DOM {@code Node}.
	 * <p>In practice, node is be a {@code Document} node, a {@code DocumentFragment} node,
	 * or a {@code Element} node. In other words, a node that accepts children.
	 * <p>
	 *  抽象模板方法将给定的对象图编组到DOM {@code Node} <p>实际上,节点是{@code Document}节点,{@code DocumentFragment}节点或{@code元素}节点
	 * 换句话说,一个接受孩子的节点。
	 * 
	 * 
	 * @param graph the root of the object graph to marshal
	 * @param node the DOM node that will contain the result tree
	 * @throws XmlMappingException if the given object cannot be marshalled to the DOM node
	 * @see org.w3c.dom.Document
	 * @see org.w3c.dom.DocumentFragment
	 * @see org.w3c.dom.Element
	 */
	protected abstract void marshalDomNode(Object graph, Node node)
			throws XmlMappingException;

	/**
	 * Abstract template method for marshalling the given object to a StAX {@code XMLEventWriter}.
	 * <p>
	 *  将给定对象编组到StAX的抽象模板方法{@code XMLEventWriter}
	 * 
	 * 
	 * @param graph the root of the object graph to marshal
	 * @param eventWriter the {@code XMLEventWriter} to write to
	 * @throws XmlMappingException if the given object cannot be marshalled to the DOM node
	 */
	protected abstract void marshalXmlEventWriter(Object graph, XMLEventWriter eventWriter)
			throws XmlMappingException;

	/**
	 * Abstract template method for marshalling the given object to a StAX {@code XMLStreamWriter}.
	 * <p>
	 * 将给定对象编组到StAX的抽象模板方法{@code XMLStreamWriter}
	 * 
	 * 
	 * @param graph the root of the object graph to marshal
	 * @param streamWriter the {@code XMLStreamWriter} to write to
	 * @throws XmlMappingException if the given object cannot be marshalled to the DOM node
	 */
	protected abstract void marshalXmlStreamWriter(Object graph, XMLStreamWriter streamWriter)
			throws XmlMappingException;

	/**
	 * Abstract template method for marshalling the given object graph to a SAX {@code ContentHandler}.
	 * <p>
	 *  将给定对象图编组为SAX的抽象模板方法{@code ContentHandler}
	 * 
	 * 
	 * @param graph the root of the object graph to marshal
	 * @param contentHandler the SAX {@code ContentHandler}
	 * @param lexicalHandler the SAX2 {@code LexicalHandler}. Can be {@code null}.
	 * @throws XmlMappingException if the given object cannot be marshalled to the handlers
	 */
	protected abstract void marshalSaxHandlers(
			Object graph, ContentHandler contentHandler, LexicalHandler lexicalHandler)
			throws XmlMappingException;

	/**
	 * Abstract template method for marshalling the given object graph to a {@code OutputStream}.
	 * <p>
	 *  抽象模板方法将给定的对象图编组到{@code OutputStream}
	 * 
	 * 
	 * @param graph the root of the object graph to marshal
	 * @param outputStream the {@code OutputStream} to write to
	 * @throws XmlMappingException if the given object cannot be marshalled to the writer
	 * @throws IOException if an I/O exception occurs
	 */
	protected abstract void marshalOutputStream(Object graph, OutputStream outputStream)
			throws XmlMappingException, IOException;

	/**
	 * Abstract template method for marshalling the given object graph to a {@code Writer}.
	 * <p>
	 *  将给定对象图组织到{@code Writer}的抽象模板方法
	 * 
	 * 
	 * @param graph the root of the object graph to marshal
	 * @param writer the {@code Writer} to write to
	 * @throws XmlMappingException if the given object cannot be marshalled to the writer
	 * @throws IOException if an I/O exception occurs
	 */
	protected abstract void marshalWriter(Object graph, Writer writer)
			throws XmlMappingException, IOException;

	/**
	 * Abstract template method for unmarshalling from a given DOM {@code Node}.
	 * <p>
	 *  从给定的DOM {@code Node}解组的抽象模板方法
	 * 
	 * 
	 * @param node the DOM node that contains the objects to be unmarshalled
	 * @return the object graph
	 * @throws XmlMappingException if the given DOM node cannot be mapped to an object
	 */
	protected abstract Object unmarshalDomNode(Node node) throws XmlMappingException;

	/**
	 * Abstract template method for unmarshalling from a given Stax {@code XMLEventReader}.
	 * <p>
	 *  从给定的Stax {@code XMLEventReader}解集的抽象模板方法
	 * 
	 * 
	 * @param eventReader the {@code XMLEventReader} to read from
	 * @return the object graph
	 * @throws XmlMappingException if the given event reader cannot be converted to an object
	 */
	protected abstract Object unmarshalXmlEventReader(XMLEventReader eventReader)
			throws XmlMappingException;

	/**
	 * Abstract template method for unmarshalling from a given Stax {@code XMLStreamReader}.
	 * <p>
	 *  从给定的Stax {@code XMLStreamReader}解集的抽象模板方法
	 * 
	 * 
	 * @param streamReader the {@code XMLStreamReader} to read from
	 * @return the object graph
	 * @throws XmlMappingException if the given stream reader cannot be converted to an object
	 */
	protected abstract Object unmarshalXmlStreamReader(XMLStreamReader streamReader)
			throws XmlMappingException;

	/**
	 * Abstract template method for unmarshalling using a given SAX {@code XMLReader}
	 * and {@code InputSource}.
	 * <p>
	 *  使用给定的SAX {@code XMLReader}和{@code InputSource}进行解组的抽象模板方法
	 * 
	 * 
	 * @param xmlReader the SAX {@code XMLReader} to parse with
	 * @param inputSource the input source to parse from
	 * @return the object graph
	 * @throws XmlMappingException if the given reader and input source cannot be converted to an object
	 * @throws IOException if an I/O exception occurs
	 */
	protected abstract Object unmarshalSaxReader(XMLReader xmlReader, InputSource inputSource)
			throws XmlMappingException, IOException;

	/**
	 * Abstract template method for unmarshalling from a given {@code InputStream}.
	 * <p>
	 *  从给定的{@code InputStream}解组的抽象模板方法
	 * 
	 * 
	 * @param inputStream the {@code InputStreamStream} to read from
	 * @return the object graph
	 * @throws XmlMappingException if the given stream cannot be converted to an object
	 * @throws IOException if an I/O exception occurs
	 */
	protected abstract Object unmarshalInputStream(InputStream inputStream)
			throws XmlMappingException, IOException;

	/**
	 * Abstract template method for unmarshalling from a given {@code Reader}.
	 * <p>
	 * 抽象模板方法用于从给定的{@code Reader}解组合
	 * 
	 * @param reader the {@code Reader} to read from
	 * @return the object graph
	 * @throws XmlMappingException if the given reader cannot be converted to an object
	 * @throws IOException if an I/O exception occurs
	 */
	protected abstract Object unmarshalReader(Reader reader)
			throws XmlMappingException, IOException;


	private static final EntityResolver NO_OP_ENTITY_RESOLVER = new EntityResolver() {
		@Override
		public InputSource resolveEntity(String publicId, String systemId) {
			return new InputSource(new StringReader(""));
		}
	};

}
