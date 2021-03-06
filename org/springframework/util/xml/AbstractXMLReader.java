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

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

/**
 * Abstract base class for SAX {@code XMLReader} implementations.
 * Contains properties as defined in {@link XMLReader}, and does not recognize any features.
 *
 * <p>
 *  SAX {@code XMLReader}实现的抽象基类包含{@link XMLReader}中定义的属性,并且不识别任何功能
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
abstract class AbstractXMLReader implements XMLReader {

	private DTDHandler dtdHandler;

	private ContentHandler contentHandler;

	private EntityResolver entityResolver;

	private ErrorHandler errorHandler;

	private LexicalHandler lexicalHandler;


	@Override
	public void setContentHandler(ContentHandler contentHandler) {
		this.contentHandler = contentHandler;
	}

	@Override
	public ContentHandler getContentHandler() {
		return this.contentHandler;
	}

	@Override
	public void setDTDHandler(DTDHandler dtdHandler) {
		this.dtdHandler = dtdHandler;
	}

	@Override
	public DTDHandler getDTDHandler() {
		return this.dtdHandler;
	}

	@Override
	public void setEntityResolver(EntityResolver entityResolver) {
		this.entityResolver = entityResolver;
	}

	@Override
	public EntityResolver getEntityResolver() {
		return this.entityResolver;
	}

	@Override
	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	@Override
	public ErrorHandler getErrorHandler() {
		return this.errorHandler;
	}

	protected LexicalHandler getLexicalHandler() {
		return this.lexicalHandler;
	}


	/**
	 * This implementation throws a {@code SAXNotRecognizedException} exception
	 * for any feature outside of the "http://xml.org/sax/features/" namespace
	 * and returns {@code false} for any feature within.
	 * <p>
	 * 此实现会为"http：// xmlorg / sax / features /"命名空间之外的任何功能引发{@code SAXNotRecognizedException}异常,并返回{@code false}
	 * 。
	 * 
	 */
	@Override
	public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
		if (name.startsWith("http://xml.org/sax/features/")) {
			return false;
		}
		else {
			throw new SAXNotRecognizedException(name);
		}
	}

	/**
	 * This implementation throws a {@code SAXNotRecognizedException} exception
	 * for any feature outside of the "http://xml.org/sax/features/" namespace
	 * and accepts a {@code false} value for any feature within.
	 * <p>
	 *  此实现会为"http：// xmlorg / sax / features /"命名空间之外的任何功能引发{@code SAXNotRecognizedException}异常,并为其中的任何功能接受
	 * {@code false}值。
	 * 
	 */
	@Override
	public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
		if (name.startsWith("http://xml.org/sax/features/")) {
			if (value) {
				throw new SAXNotSupportedException(name);
			}
		}
		else {
			throw new SAXNotRecognizedException(name);
		}
	}

	/**
	 * Throws a {@code SAXNotRecognizedException} exception when the given property does not signify a lexical
	 * handler. The property name for a lexical handler is {@code http://xml.org/sax/properties/lexical-handler}.
	 * <p>
	 *  当给定属性不表示词法处理程序时,抛出{@code SAXNotRecognizedException}异常。
	 * 词法处理程序的属性名称为{@code http：// xmlorg / sax / properties / lexical-handler}。
	 * 
	 */
	@Override
	public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
		if ("http://xml.org/sax/properties/lexical-handler".equals(name)) {
			return this.lexicalHandler;
		}
		else {
			throw new SAXNotRecognizedException(name);
		}
	}

	/**
	 * Throws a {@code SAXNotRecognizedException} exception when the given property does not signify a lexical
	 * handler. The property name for a lexical handler is {@code http://xml.org/sax/properties/lexical-handler}.
	 * <p>
	 * 当给定属性不表示词法处理程序时,抛出{@code SAXNotRecognizedException}异常。
	 * 词法处理程序的属性名称为{@code http：// xmlorg / sax / properties / lexical-handler}。
	 */
	@Override
	public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
		if ("http://xml.org/sax/properties/lexical-handler".equals(name)) {
			this.lexicalHandler = (LexicalHandler) value;
		}
		else {
			throw new SAXNotRecognizedException(name);
		}
	}

}
