/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.http.converter.xml;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Result;
import javax.xml.transform.Source;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.StreamUtils;

/**
 * An {@code HttpMessageConverter} that can read XML collections using JAXB2.
 *
 * <p>This converter can read {@linkplain Collection collections} that contain classes
 * annotated with {@link XmlRootElement} and {@link XmlType}. Note that this converter
 * does not support writing.
 *
 * <p>
 *  可以使用JAXB2读取XML集合的{@code HttpMessageConverter}
 * 
 * <p>此转换器可以读取包含使用{@link XmlRootElement}和{@link XmlType}注释的类的{@linkplain Collection collections}。
 * 请注意,此转换器不支持写入。
 * 
 * 
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @since 3.2
 */
@SuppressWarnings("rawtypes")
public class Jaxb2CollectionHttpMessageConverter<T extends Collection>
		extends AbstractJaxb2HttpMessageConverter<T> implements GenericHttpMessageConverter<T> {

	private final XMLInputFactory inputFactory = createXmlInputFactory();


	/**
	 * Always returns {@code false} since Jaxb2CollectionHttpMessageConverter
	 * required generic type information in order to read a Collection.
	 * <p>
	 *  始终返回{@code false},因为Jaxb2CollectionHttpMessageConverter需要通用类型信息才能读取集合
	 * 
	 */
	@Override
	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * <p>Jaxb2CollectionHttpMessageConverter can read a generic
	 * {@link Collection} where the generic type is a JAXB type annotated with
	 * {@link XmlRootElement} or {@link XmlType}.
	 * <p>
	 *  {@inheritDoc} <p> Jaxb2CollectionHttpMessageConverter可以读取通用{@link Collection},其中通用类型是使用{@link XmlRootElement}
	 * 或{@link XmlType}注释的JAXB类型。
	 * 
	 */
	@Override
	public boolean canRead(Type type, Class<?> contextClass, MediaType mediaType) {
		if (!(type instanceof ParameterizedType)) {
			return false;
		}
		ParameterizedType parameterizedType = (ParameterizedType) type;
		if (!(parameterizedType.getRawType() instanceof Class)) {
			return false;
		}
		Class<?> rawType = (Class<?>) parameterizedType.getRawType();
		if (!(Collection.class.isAssignableFrom(rawType))) {
			return false;
		}
		if (parameterizedType.getActualTypeArguments().length != 1) {
			return false;
		}
		Type typeArgument = parameterizedType.getActualTypeArguments()[0];
		if (!(typeArgument instanceof Class)) {
			return false;
		}
		Class<?> typeArgumentClass = (Class<?>) typeArgument;
		return (typeArgumentClass.isAnnotationPresent(XmlRootElement.class) ||
				typeArgumentClass.isAnnotationPresent(XmlType.class)) && canRead(mediaType);
	}

	/**
	 * Always returns {@code false} since Jaxb2CollectionHttpMessageConverter
	 * does not convert collections to XML.
	 * <p>
	 *  始终返回{@code false},因为Jaxb2CollectionHttpMessageConverter不将集合转换为XML
	 * 
	 */
	@Override
	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		return false;
	}

	/**
	 * Always returns {@code false} since Jaxb2CollectionHttpMessageConverter
	 * does not convert collections to XML.
	 * <p>
	 *  始终返回{@code false},因为Jaxb2CollectionHttpMessageConverter不将集合转换为XML
	 * 
	 */
	@Override
	public boolean canWrite(Type type, Class<?> clazz, MediaType mediaType) {
		return false;
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		// should not be called, since we override canRead/Write
		throw new UnsupportedOperationException();
	}

	@Override
	protected T readFromSource(Class<? extends T> clazz, HttpHeaders headers, Source source) throws IOException {
		// should not be called, since we return false for canRead(Class)
		throw new UnsupportedOperationException();
	}

	@Override
	@SuppressWarnings("unchecked")
	public T read(Type type, Class<?> contextClass, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {

		ParameterizedType parameterizedType = (ParameterizedType) type;
		T result = createCollection((Class<?>) parameterizedType.getRawType());
		Class<?> elementClass = (Class<?>) parameterizedType.getActualTypeArguments()[0];

		try {
			Unmarshaller unmarshaller = createUnmarshaller(elementClass);
			XMLStreamReader streamReader = this.inputFactory.createXMLStreamReader(inputMessage.getBody());
			int event = moveToFirstChildOfRootElement(streamReader);

			while (event != XMLStreamReader.END_DOCUMENT) {
				if (elementClass.isAnnotationPresent(XmlRootElement.class)) {
					result.add(unmarshaller.unmarshal(streamReader));
				}
				else if (elementClass.isAnnotationPresent(XmlType.class)) {
					result.add(unmarshaller.unmarshal(streamReader, elementClass).getValue());
				}
				else {
					// should not happen, since we check in canRead(Type)
					throw new HttpMessageConversionException("Could not unmarshal to [" + elementClass + "]");
				}
				event = moveToNextElement(streamReader);
			}
			return result;
		}
		catch (UnmarshalException ex) {
			throw new HttpMessageNotReadableException("Could not unmarshal to [" + elementClass + "]: " + ex.getMessage(), ex);
		}
		catch (JAXBException ex) {
			throw new HttpMessageConversionException("Could not instantiate JAXBContext: " + ex.getMessage(), ex);
		}
		catch (XMLStreamException ex) {
			throw new HttpMessageConversionException(ex.getMessage(), ex);
		}
	}

	/**
	 * Create a Collection of the given type, with the given initial capacity
	 * (if supported by the Collection type).
	 * <p>
	 * 创建给定类型的集合,具有给定的初始容量(如果集合类型支持)
	 * 
	 * 
	 * @param collectionClass the type of Collection to instantiate
	 * @return the created Collection instance
	 */
	@SuppressWarnings("unchecked")
	protected T createCollection(Class<?> collectionClass) {
		if (!collectionClass.isInterface()) {
			try {
				return (T) collectionClass.newInstance();
			}
			catch (Exception ex) {
				throw new IllegalArgumentException(
						"Could not instantiate collection class [" +
								collectionClass.getName() + "]: " + ex.getMessage());
			}
		}
		else if (List.class == collectionClass) {
			return (T) new ArrayList();
		}
		else if (SortedSet.class == collectionClass) {
			return (T) new TreeSet();
		}
		else {
			return (T) new LinkedHashSet();
		}
	}

	private int moveToFirstChildOfRootElement(XMLStreamReader streamReader) throws XMLStreamException {
		// root
		int event = streamReader.next();
		while (event != XMLStreamReader.START_ELEMENT) {
			event = streamReader.next();
		}

		// first child
		event = streamReader.next();
		while ((event != XMLStreamReader.START_ELEMENT) && (event != XMLStreamReader.END_DOCUMENT)) {
			event = streamReader.next();
		}
		return event;
	}

	private int moveToNextElement(XMLStreamReader streamReader) throws XMLStreamException {
		int event = streamReader.getEventType();
		while (event != XMLStreamReader.START_ELEMENT && event != XMLStreamReader.END_DOCUMENT) {
			event = streamReader.next();
		}
		return event;
	}

	@Override
	public void write(T t, Type type, MediaType contentType, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void writeToResult(T t, HttpHeaders headers, Result result) throws IOException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Create a {@code XMLInputFactory} that this converter will use to create {@link
	 * javax.xml.stream.XMLStreamReader} and {@link javax.xml.stream.XMLEventReader} objects.
	 * <p>Can be overridden in subclasses, adding further initialization of the factory.
	 * The resulting factory is cached, so this method will only be called once.
	 * <p>
	 *  创建一个{@code XMLInputFactory},该转换器将用于创建{@link javaxxmlstreamXMLStreamReader}和{@link javaxxmlstreamXMLEventReader}
	 * 对象<p>可以在子类中覆盖,添加工厂的进一步初始化生成的工厂已被缓存,因此此方法只会叫一次。
	 */
	protected XMLInputFactory createXmlInputFactory() {
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
