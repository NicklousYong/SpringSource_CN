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

package org.springframework.util.xml;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;

import org.springframework.util.StringUtils;

/**
 * Abstract base class for SAX {@code XMLReader} implementations that use StAX as a basis.
 *
 * <p>
 *  使用StAX作为基础的SAX {@code XMLReader}实现的抽象基类
 * 
 * 
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @since 3.0
 * @see #setContentHandler(org.xml.sax.ContentHandler)
 * @see #setDTDHandler(org.xml.sax.DTDHandler)
 * @see #setEntityResolver(org.xml.sax.EntityResolver)
 * @see #setErrorHandler(org.xml.sax.ErrorHandler)
 */
abstract class AbstractStaxXMLReader extends AbstractXMLReader {

	private static final String NAMESPACES_FEATURE_NAME = "http://xml.org/sax/features/namespaces";

	private static final String NAMESPACE_PREFIXES_FEATURE_NAME = "http://xml.org/sax/features/namespace-prefixes";

	private static final String IS_STANDALONE_FEATURE_NAME = "http://xml.org/sax/features/is-standalone";


	private boolean namespacesFeature = true;

	private boolean namespacePrefixesFeature = false;

	private Boolean isStandalone;

	private final Map<String, String> namespaces = new LinkedHashMap<String, String>();


	@Override
	public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
		if (NAMESPACES_FEATURE_NAME.equals(name)) {
			return this.namespacesFeature;
		}
		else if (NAMESPACE_PREFIXES_FEATURE_NAME.equals(name)) {
			return this.namespacePrefixesFeature;
		}
		else if (IS_STANDALONE_FEATURE_NAME.equals(name)) {
			if (this.isStandalone != null) {
				return this.isStandalone;
			}
			else {
				throw new SAXNotSupportedException("startDocument() callback not completed yet");
			}
		}
		else {
			return super.getFeature(name);
		}
	}

	@Override
	public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
		if (NAMESPACES_FEATURE_NAME.equals(name)) {
			this.namespacesFeature = value;
		}
		else if (NAMESPACE_PREFIXES_FEATURE_NAME.equals(name)) {
			this.namespacePrefixesFeature = value;
		}
		else {
			super.setFeature(name, value);
		}
	}

	protected void setStandalone(boolean standalone) {
		this.isStandalone = standalone;
	}

	/**
	 * Indicates whether the SAX feature {@code http://xml.org/sax/features/namespaces} is turned on.
	 * <p>
	 *  指示是否打开SAX功能{@code http：// xmlorg / sax / features / namespaces}
	 * 
	 */
	protected boolean hasNamespacesFeature() {
		return this.namespacesFeature;
	}

	/**
	 * Indicates whether the SAX feature {@code http://xml.org/sax/features/namespaces-prefixes} is turned on.
	 * <p>
	 * 指示是否打开SAX功能{@code http：// xmlorg / sax / features / namespaces-prefixes}
	 * 
	 */
	protected boolean hasNamespacePrefixesFeature() {
		return this.namespacePrefixesFeature;
	}

	/**
	 * Convert a {@code QName} to a qualified name, as used by DOM and SAX.
	 * The returned string has a format of {@code prefix:localName} if the
	 * prefix is set, or just {@code localName} if not.
	 * <p>
	 *  将{@code QName}转换为DOM和SAX使用的限定名称如果前缀已设置,则返回的字符串格式为{@code prefix：localName},如果不是,则返回{@code localName}。
	 * 
	 * 
	 * @param qName the {@code QName}
	 * @return the qualified name
	 */
	protected String toQualifiedName(QName qName) {
		String prefix = qName.getPrefix();
		if (!StringUtils.hasLength(prefix)) {
			return qName.getLocalPart();
		}
		else {
			return prefix + ":" + qName.getLocalPart();
		}
	}


	/**
	 * Parse the StAX XML reader passed at construction-time.
	 * <p><b>NOTE:</b>: The given {@code InputSource} is not read, but ignored.
	 * <p>
	 *  解析在构建时传递的StAX XML阅读器<p> <b>注意：</b>：给定的{@code InputSource}不被读取,但被忽略
	 * 
	 * 
	 * @param ignored is ignored
	 * @throws SAXException a SAX exception, possibly wrapping a {@code XMLStreamException}
	 */
	@Override
	public final void parse(InputSource ignored) throws SAXException {
		parse();
	}

	/**
	 * Parse the StAX XML reader passed at construction-time.
	 * <p><b>NOTE:</b>: The given system identifier is not read, but ignored.
	 * <p>
	 *  解析在构建时传递的StAX XML阅读器<p> <b>注意：</b>：给定的系统标识符未读,但忽略
	 * 
	 * 
	 * @param ignored is ignored
	 * @throws SAXException A SAX exception, possibly wrapping a {@code XMLStreamException}
	 */
	@Override
	public final void parse(String ignored) throws SAXException {
		parse();
	}

	private void parse() throws SAXException {
		try {
			parseInternal();
		}
		catch (XMLStreamException ex) {
			Locator locator = null;
			if (ex.getLocation() != null) {
				locator = new StaxLocator(ex.getLocation());
			}
			SAXParseException saxException = new SAXParseException(ex.getMessage(), locator, ex);
			if (getErrorHandler() != null) {
				getErrorHandler().fatalError(saxException);
			}
			else {
				throw saxException;
			}
		}
	}

	/**
	 * Template method that parses the StAX reader passed at construction-time.
	 * <p>
	 *  分析StAX阅读器在构建时通过的模板方法
	 * 
	 */
	protected abstract void parseInternal() throws SAXException, XMLStreamException;


	/**
	 * Start the prefix mapping for the given prefix.
	 * <p>
	 *  启动给定前缀的前缀映射
	 * 
	 * 
	 * @see org.xml.sax.ContentHandler#startPrefixMapping(String, String)
	 */
	protected void startPrefixMapping(String prefix, String namespace) throws SAXException {
		if (getContentHandler() != null) {
			if (prefix == null) {
				prefix = "";
			}
			if (!StringUtils.hasLength(namespace)) {
				return;
			}
			if (!namespace.equals(this.namespaces.get(prefix))) {
				getContentHandler().startPrefixMapping(prefix, namespace);
				this.namespaces.put(prefix, namespace);
			}
		}
	}

	/**
	 * End the prefix mapping for the given prefix.
	 * <p>
	 *  结束给定前缀的前缀映射
	 * 
	 * 
	 * @see org.xml.sax.ContentHandler#endPrefixMapping(String)
	 */
	protected void endPrefixMapping(String prefix) throws SAXException {
		if (getContentHandler() != null) {
			if (this.namespaces.containsKey(prefix)) {
				getContentHandler().endPrefixMapping(prefix);
				this.namespaces.remove(prefix);
			}
		}
	}


	/**
	 * Implementation of the {@code Locator} interface based on a given StAX {@code Location}.
	 * <p>
	 * 根据给定的StAX {@code位置}实现{@code Locator}界面
	 * 
	 * @see Locator
	 * @see Location
	 */
	private static class StaxLocator implements Locator {

		private final Location location;

		public StaxLocator(Location location) {
			this.location = location;
		}

		@Override
		public String getPublicId() {
			return this.location.getPublicId();
		}

		@Override
		public String getSystemId() {
			return this.location.getSystemId();
		}

		@Override
		public int getLineNumber() {
			return this.location.getLineNumber();
		}

		@Override
		public int getColumnNumber() {
			return this.location.getColumnNumber();
		}
	}

}
