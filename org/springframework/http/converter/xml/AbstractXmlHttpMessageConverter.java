/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2010 the original author or authors.
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

package org.springframework.http.converter.xml;

import java.io.IOException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConversionException;

/**
 * Abstract base class for {@link org.springframework.http.converter.HttpMessageConverter HttpMessageConverters}
 * that convert from/to XML.
 *
 * <p>By default, subclasses of this converter support {@code text/xml}, {@code application/xml}, and {@code
 * application/*-xml}. This can be overridden by setting the {@link #setSupportedMediaTypes(java.util.List)
 * supportedMediaTypes} property.
 *
 * <p>
 *  从/转换为XML的{@link orgspringframeworkhttpconverterHttpMessageConverter HttpMessageConverters}的抽象基类
 * 
 * <p>默认情况下,此转换器的子类支持{@code text / xml},{@code application / xml}和{@code application / *  -  xml}这可以通过设置
 * {@link #setSupportedMediaTypes( javautilList)supportedMediaTypes}属性。
 * 
 * 
 * @author Arjen Poutsma
 * @since 3.0
 */
public abstract class AbstractXmlHttpMessageConverter<T> extends AbstractHttpMessageConverter<T> {

	private final TransformerFactory transformerFactory = TransformerFactory.newInstance();


	/**
	 * Protected constructor that sets the {@link #setSupportedMediaTypes(java.util.List) supportedMediaTypes}
	 * to {@code text/xml} and {@code application/xml}, and {@code application/*-xml}.
	 * <p>
	 *  受保护的构造函数将{@link #setSupportedMediaTypes(javautilList)supportedMediaTypes}设置为{@code text / xml}和{@code application / xml}
	 * 和{@code application / *  -  xml}。
	 * 
	 */
	protected AbstractXmlHttpMessageConverter() {
		super(MediaType.APPLICATION_XML, MediaType.TEXT_XML, new MediaType("application", "*+xml"));
	}


	@Override
	public final T readInternal(Class<? extends T> clazz, HttpInputMessage inputMessage) throws IOException {
		return readFromSource(clazz, inputMessage.getHeaders(), new StreamSource(inputMessage.getBody()));
	}

	@Override
	protected final void writeInternal(T t, HttpOutputMessage outputMessage) throws IOException {
		writeToResult(t, outputMessage.getHeaders(), new StreamResult(outputMessage.getBody()));
	}

	/**
	 * Transforms the given {@code Source} to the {@code Result}.
	 * <p>
	 *  将给定的{@code源}转换为{@code结果}
	 * 
	 * 
	 * @param source the source to transform from
	 * @param result the result to transform to
	 * @throws TransformerException in case of transformation errors
	 */
	protected void transform(Source source, Result result) throws TransformerException {
		this.transformerFactory.newTransformer().transform(source, result);
	}


	/**
	 * Abstract template method called from {@link #read(Class, HttpInputMessage)}.
	 * <p>
	 *  从{@link #read(Class,HttpInputMessage))调用抽象模板方法}
	 * 
	 * 
	 * @param clazz the type of object to return
	 * @param headers the HTTP input headers
	 * @param source the HTTP input body
	 * @return the converted object
	 * @throws IOException in case of I/O errors
	 * @throws org.springframework.http.converter.HttpMessageConversionException in case of conversion errors
	 */
	protected abstract T readFromSource(Class<? extends T> clazz, HttpHeaders headers, Source source)
			throws IOException;

	/**
	 * Abstract template method called from {@link #writeInternal(Object, HttpOutputMessage)}.
	 * <p>
	 *  从{@link #writeInternal(Object,HttpOutputMessage)}调用的抽象模板方法}
	 * 
	 * @param t the object to write to the output message
	 * @param headers the HTTP output headers
	 * @param result the HTTP output body
	 * @throws IOException in case of I/O errors
	 * @throws HttpMessageConversionException in case of conversion errors
	 */
	protected abstract void writeToResult(T t, HttpHeaders headers, Result result)
			throws IOException;

}
