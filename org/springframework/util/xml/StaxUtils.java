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

package org.springframework.util.xml;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stax.StAXResult;
import javax.xml.transform.stax.StAXSource;

import org.xml.sax.ContentHandler;
import org.xml.sax.XMLReader;

/**
 * Convenience methods for working with the StAX API. Partly historic due to JAXP 1.3 compatibility;
 * as of Spring 4.0, relying on JAXP 1.4 as included in JDK 1.6 and higher.
 *
 * <p>In particular, methods for using StAX ({@code javax.xml.stream}) in combination with the TrAX API
 * ({@code javax.xml.transform}), and converting StAX readers/writers into SAX readers/handlers and vice-versa.
 *
 * <p>
 *  使用StAX API的方便方法由于JAXP 13兼容性,部分历史;截至春季40,依靠JAXP 14包含在JDK 16及更高版本
 * 
 * 特别是使用StAX({@code javaxxmlstream})与TrAX API({@code javaxxmltransform})结合使用的方法,并将StAX读者/作者转换为SAX阅读器/处理程
 * 序,反之亦然。
 * 
 * 
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @since 3.0
 */
public abstract class StaxUtils {

	/**
	 * Create a JAXP 1.4 {@link StAXSource} for the given {@link XMLStreamReader}.
	 * <p>
	 *  为给定的{@link XMLStreamReader}创建一个JAXP 14 {@link StAXSource}
	 * 
	 * 
	 * @param streamReader the StAX stream reader
	 * @return a source wrapping the {@code streamReader}
	 */
	public static Source createStaxSource(XMLStreamReader streamReader) {
		return new StAXSource(streamReader);
	}

	/**
	 * Create a JAXP 1.4 a {@link StAXSource} for the given {@link XMLEventReader}.
	 * <p>
	 *  为给定的{@link XMLEventReader}创建一个JAXP 14一个{@link StAXSource}
	 * 
	 * 
	 * @param eventReader the StAX event reader
	 * @return a source wrapping the {@code eventReader}
	 */
	public static Source createStaxSource(XMLEventReader eventReader) throws XMLStreamException {
		return new StAXSource(eventReader);
	}

	/**
	 * Create a custom, non-JAXP 1.4 StAX {@link Source} for the given {@link XMLStreamReader}.
	 * <p>
	 *  为给定的{@link XMLStreamReader}创建自定义非JAXP 14 StAX {@link Source}
	 * 
	 * 
	 * @param streamReader the StAX stream reader
	 * @return a source wrapping the {@code streamReader}
	 */
	public static Source createCustomStaxSource(XMLStreamReader streamReader) {
		return new StaxSource(streamReader);
	}

	/**
	 * Create a custom, non-JAXP 1.4 StAX {@link Source} for the given {@link XMLEventReader}.
	 * <p>
	 *  为给定的{@link XMLEventReader}创建自定义非JAXP 14 StAX {@link Source}
	 * 
	 * 
	 * @param eventReader the StAX event reader
	 * @return a source wrapping the {@code eventReader}
	 */
	public static Source createCustomStaxSource(XMLEventReader eventReader) {
		return new StaxSource(eventReader);
	}

	/**
	 * Indicate whether the given {@link Source} is a JAXP 1.4 StAX Source or
	 * custom StAX Source.
	 * <p>
	 *  指示给定的{@link Source}是否是JAXP 14 StAX源或自定义StAX源
	 * 
	 * 
	 * @return {@code true} if {@code source} is a JAXP 1.4 {@link StAXSource} or
	 * custom StAX Source; {@code false} otherwise
	 */
	public static boolean isStaxSource(Source source) {
		return (source instanceof StAXSource || source instanceof StaxSource);
	}

	/**
	 * Return the {@link XMLStreamReader} for the given StAX Source.
	 * <p>
	 *  返回给定StAX源的{@link XMLStreamReader}
	 * 
	 * 
	 * @param source a JAXP 1.4 {@link StAXSource}
	 * @return the {@link XMLStreamReader}
	 * @throws IllegalArgumentException if {@code source} isn't a JAXP 1.4 {@link StAXSource}
	 * or custom StAX Source
	 */
	public static XMLStreamReader getXMLStreamReader(Source source) {
		if (source instanceof StAXSource) {
			return ((StAXSource) source).getXMLStreamReader();
		}
		else if (source instanceof StaxSource) {
			return ((StaxSource) source).getXMLStreamReader();
		}
		else {
			throw new IllegalArgumentException("Source '" + source + "' is neither StaxSource nor StAXSource");
		}
	}

	/**
	 * Return the {@link XMLEventReader} for the given StAX Source.
	 * <p>
	 *  返回给定StAX源的{@link XMLEventReader}
	 * 
	 * 
	 * @param source a JAXP 1.4 {@link StAXSource}
	 * @return the {@link XMLEventReader}
	 * @throws IllegalArgumentException if {@code source} isn't a JAXP 1.4 {@link StAXSource}
	 * or custom StAX Source
	 */
	public static XMLEventReader getXMLEventReader(Source source) {
		if (source instanceof StAXSource) {
			return ((StAXSource) source).getXMLEventReader();
		}
		else if (source instanceof StaxSource) {
			return ((StaxSource) source).getXMLEventReader();
		}
		else {
			throw new IllegalArgumentException("Source '" + source + "' is neither StaxSource nor StAXSource");
		}
	}

	/**
	 * Create a JAXP 1.4 {@link StAXResult} for the given {@link XMLStreamWriter}.
	 * <p>
	 * 为给定的{@link XMLStreamWriter}创建一个JAXP 14 {@link StAXResult}
	 * 
	 * 
	 * @param streamWriter the StAX stream writer
	 * @return a result wrapping the {@code streamWriter}
	 */
	public static Result createStaxResult(XMLStreamWriter streamWriter) {
		return new StAXResult(streamWriter);
	}

	/**
	 * Create a JAXP 1.4 {@link StAXResult} for the given {@link XMLEventWriter}.
	 * <p>
	 *  为给定的{@link XMLEventWriter}创建一个JAXP 14 {@link StAXResult}
	 * 
	 * 
	 * @param eventWriter the StAX event writer
	 * @return a result wrapping {@code streamReader}
	 */
	public static Result createStaxResult(XMLEventWriter eventWriter) {
		return new StAXResult(eventWriter);
	}

	/**
	 * Create a custom, non-JAXP 1.4 StAX {@link Result} for the given {@link XMLStreamWriter}.
	 * <p>
	 *  为给定的{@link XMLStreamWriter}创建自定义非JAXP 14 StAX {@link Result}
	 * 
	 * 
	 * @param streamWriter the StAX stream writer
	 * @return a source wrapping the {@code streamWriter}
	 */
	public static Result createCustomStaxResult(XMLStreamWriter streamWriter) {
		return new StaxResult(streamWriter);
	}

	/**
	 * Create a custom, non-JAXP 1.4 StAX {@link Result} for the given {@link XMLEventWriter}.
	 * <p>
	 *  为给定的{@link XMLEventWriter}创建自定义非JAXP 14 StAX {@link Result}
	 * 
	 * 
	 * @param eventWriter the StAX event writer
	 * @return a source wrapping the {@code eventWriter}
	 */
	public static Result createCustomStaxResult(XMLEventWriter eventWriter) {
		return new StaxResult(eventWriter);
	}

	/**
	 * Indicate whether the given {@link Result} is a JAXP 1.4 StAX Result or
	 * custom StAX Result.
	 * <p>
	 *  指示给定的{@link结果}是否为JAXP 14 StAX结果或自定义StAX结果
	 * 
	 * 
	 * @return {@code true} if {@code result} is a JAXP 1.4 {@link StAXResult} or
	 * custom StAX Result; {@code false} otherwise
	 */
	public static boolean isStaxResult(Result result) {
		return (result instanceof StAXResult || result instanceof StaxResult);
	}

	/**
	 * Return the {@link XMLStreamWriter} for the given StAX Result.
	 * <p>
	 *  返回给定StAX结果的{@link XMLStreamWriter}
	 * 
	 * 
	 * @param result a JAXP 1.4 {@link StAXResult}
	 * @return the {@link XMLStreamReader}
	 * @throws IllegalArgumentException if {@code source} isn't a JAXP 1.4 {@link StAXResult}
	 * or custom StAX Result
	 */
	public static XMLStreamWriter getXMLStreamWriter(Result result) {
		if (result instanceof StAXResult) {
			return ((StAXResult) result).getXMLStreamWriter();
		}
		else if (result instanceof StaxResult) {
			return ((StaxResult) result).getXMLStreamWriter();
		}
		else {
			throw new IllegalArgumentException("Result '" + result + "' is neither StaxResult nor StAXResult");
		}
	}

	/**
	 * Return the {@link XMLEventWriter} for the given StAX Result.
	 * <p>
	 *  返回给定StAX结果的{@link XMLEventWriter}
	 * 
	 * 
	 * @param result a JAXP 1.4 {@link StAXResult}
	 * @return the {@link XMLStreamReader}
	 * @throws IllegalArgumentException if {@code source} isn't a JAXP 1.4 {@link StAXResult}
	 * or custom StAX Result
	 */
	public static XMLEventWriter getXMLEventWriter(Result result) {
		if (result instanceof StAXResult) {
			return ((StAXResult) result).getXMLEventWriter();
		}
		else if (result instanceof StaxResult) {
			return ((StaxResult) result).getXMLEventWriter();
		}
		else {
			throw new IllegalArgumentException("Result '" + result + "' is neither StaxResult nor StAXResult");
		}
	}

	/**
	 * Create a SAX {@link ContentHandler} that writes to the given StAX {@link XMLStreamWriter}.
	 * <p>
	 *  创建一个SAX {@link ContentHandler},写入给定的StAX {@link XMLStreamWriter}
	 * 
	 * 
	 * @param streamWriter the StAX stream writer
	 * @return a content handler writing to the {@code streamWriter}
	 */
	public static ContentHandler createContentHandler(XMLStreamWriter streamWriter) {
		return new StaxStreamHandler(streamWriter);
	}

	/**
	 * Create a SAX {@link ContentHandler} that writes events to the given StAX {@link XMLEventWriter}.
	 * <p>
	 *  创建一个SAX {@link ContentHandler},将事件写入给定的StAX {@link XMLEventWriter}
	 * 
	 * 
	 * @param eventWriter the StAX event writer
	 * @return a content handler writing to the {@code eventWriter}
	 */
	public static ContentHandler createContentHandler(XMLEventWriter eventWriter) {
		return new StaxEventHandler(eventWriter);
	}

	/**
	 * Create a SAX {@link XMLReader} that reads from the given StAX {@link XMLStreamReader}.
	 * <p>
	 * 创建从给定的StAX {@link XMLStreamReader}读取的SAX {@link XMLReader}
	 * 
	 * 
	 * @param streamReader the StAX stream reader
	 * @return a XMLReader reading from the {@code streamWriter}
	 */
	public static XMLReader createXMLReader(XMLStreamReader streamReader) {
		return new StaxStreamXMLReader(streamReader);
	}

	/**
	 * Create a SAX {@link XMLReader} that reads from the given StAX {@link XMLEventReader}.
	 * <p>
	 *  创建从给定的StAX {@link XMLEventReader}读取的SAX {@link XMLReader}
	 * 
	 * 
	 * @param eventReader the StAX event reader
	 * @return a XMLReader reading from the {@code eventWriter}
	 */
	public static XMLReader createXMLReader(XMLEventReader eventReader) {
		return new StaxEventXMLReader(eventReader);
	}

	/**
	 * Return a {@link XMLStreamReader} that reads from a {@link XMLEventReader}.
	 * Useful because the StAX {@code XMLInputFactory} allows one to create an
	 * event reader from a stream reader, but not vice-versa.
	 * <p>
	 *  返回一个从{@link XMLEventReader}读取的{@link XMLStreamReader},因为StAX {@code XMLInputFactory}允许从流读取器创建事件读取器,但
	 * 反之亦然。
	 * 
	 * 
	 * @return a stream reader that reads from an event reader
	 */
	public static XMLStreamReader createEventStreamReader(XMLEventReader eventReader) throws XMLStreamException {
		return new XMLEventStreamReader(eventReader);
	}

	/**
	 * Return a {@link XMLStreamWriter} that writes to a {@link XMLEventWriter}.
	 * <p>
	 *  返回写入{@link XMLEventWriter}的{@link XMLStreamWriter}
	 * 
	 * 
	 * @return a stream writer that writes to an event writer
	 * @since 3.2
	 */
	public static XMLStreamWriter createEventStreamWriter(XMLEventWriter eventWriter) {
		return new XMLEventStreamWriter(eventWriter, XMLEventFactory.newFactory());
	}

	/**
	 * Return a {@link XMLStreamWriter} that writes to a {@link XMLEventWriter}.
	 * <p>
	 *  返回写入{@link XMLEventWriter}的{@link XMLStreamWriter}
	 * 
	 * @return a stream writer that writes to an event writer
	 * @since 3.0.5
	 */
	public static XMLStreamWriter createEventStreamWriter(XMLEventWriter eventWriter, XMLEventFactory eventFactory) {
		return new XMLEventStreamWriter(eventWriter, eventFactory);
	}

}
