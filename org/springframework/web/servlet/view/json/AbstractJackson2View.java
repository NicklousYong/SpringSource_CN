/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2015 the original author or authors.
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

package org.springframework.web.servlet.view.json;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.FilterProvider;

import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.util.Assert;
import org.springframework.web.servlet.view.AbstractView;

/**
 * Abstract base class for Jackson based and content type independent
 * {@link AbstractView} implementations.
 *
 * <p>Compatible with Jackson 2.6 and higher, as of Spring 4.3.
 *
 * <p>
 *  基于Jackson和内容类型独立的{@link AbstractView}实现的抽象基类
 * 
 *  <p>兼容于Jackson 26及更高版本,截至春季43
 * 
 * 
 * @author Jeremy Grelle
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @author Sebastien Deleuze
 * @since 4.1
 */
public abstract class AbstractJackson2View extends AbstractView {

	private ObjectMapper objectMapper;

	private JsonEncoding encoding = JsonEncoding.UTF8;

	private Boolean prettyPrint;

	private boolean disableCaching = true;

	protected boolean updateContentLength = false;


	protected AbstractJackson2View(ObjectMapper objectMapper, String contentType) {
		setObjectMapper(objectMapper);
		setContentType(contentType);
		setExposePathVariables(false);
	}

	/**
	 * Set the {@code ObjectMapper} for this view.
	 * If not set, a default {@link ObjectMapper#ObjectMapper() ObjectMapper} will be used.
	 * <p>Setting a custom-configured {@code ObjectMapper} is one way to take further control of
	 * the JSON serialization process. The other option is to use Jackson's provided annotations
	 * on the types to be serialized, in which case a custom-configured ObjectMapper is unnecessary.
	 * <p>
	 * 设置此视图的{@code ObjectMapper}如果未设置,将使用默认{@link ObjectMapper#ObjectMapper()ObjectMapper} <p>设置自定义配置的{@code ObjectMapper}
	 * 是进一步控制JSON序列化过程另一个选项是使用杰克逊提供的关于要序列化的类型的注释,在这种情况下,自定义配置的ObjectMapper是不必要的。
	 * 
	 */
	public void setObjectMapper(ObjectMapper objectMapper) {
		Assert.notNull(objectMapper, "'objectMapper' must not be null");
		this.objectMapper = objectMapper;
		configurePrettyPrint();
	}

	/**
	 * Return the {@code ObjectMapper} for this view.
	 * <p>
	 *  返回此视图的{@code ObjectMapper}
	 * 
	 */
	public final ObjectMapper getObjectMapper() {
		return this.objectMapper;
	}

	/**
	 * Set the {@code JsonEncoding} for this view.
	 * By default, {@linkplain JsonEncoding#UTF8 UTF-8} is used.
	 * <p>
	 *  为此视图设置{@code JsonEncoding}默认情况下,使用{@linkplain JsonEncoding#UTF8 UTF-8}
	 * 
	 */
	public void setEncoding(JsonEncoding encoding) {
		Assert.notNull(encoding, "'encoding' must not be null");
		this.encoding = encoding;
	}

	/**
	 * Return the {@code JsonEncoding} for this view.
	 * <p>
	 *  返回此视图的{@code JsonEncoding}
	 * 
	 */
	public final JsonEncoding getEncoding() {
		return this.encoding;
	}

	/**
	 * Whether to use the default pretty printer when writing the output.
	 * This is a shortcut for setting up an {@code ObjectMapper} as follows:
	 * <pre class="code">
	 * ObjectMapper mapper = new ObjectMapper();
	 * mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
	 * </pre>
	 * <p>The default value is {@code false}.
	 * <p>
	 *  编写输出时是否使用默认的漂亮打印机这是一个设置{@code ObjectMapper}的快捷方式,如下所示：
	 * <pre class="code">
	 * ObjectMapper mapper = new ObjectMapper(); mapperconfigure(SerializationFeatureINDENT_OUTPUT,true);
	 * </pre>
	 *  <p>默认值为{@code false}
	 * 
	 */
	public void setPrettyPrint(boolean prettyPrint) {
		this.prettyPrint = prettyPrint;
		configurePrettyPrint();
	}

	private void configurePrettyPrint() {
		if (this.prettyPrint != null) {
			this.objectMapper.configure(SerializationFeature.INDENT_OUTPUT, this.prettyPrint);
		}
	}

	/**
	 * Disables caching of the generated JSON.
	 * <p>Default is {@code true}, which will prevent the client from caching the generated JSON.
	 * <p>
	 *  禁用缓存生成的JSON <p>默认值为{@code true},这将阻止客户端缓存生成的JSON
	 * 
	 */
	public void setDisableCaching(boolean disableCaching) {
		this.disableCaching = disableCaching;
	}

	/**
	 * Whether to update the 'Content-Length' header of the response. When set to
	 * {@code true}, the response is buffered in order to determine the content
	 * length and set the 'Content-Length' header of the response.
	 * <p>The default setting is {@code false}.
	 * <p>
	 *  是否更新响应的"Content-Length"标题当设置为{@code true}时,缓冲响应以确定内容长度并设置响应的"Content-Length"头部<p>默认值设置为{@code false}
	 * 。
	 * 
	 */
	public void setUpdateContentLength(boolean updateContentLength) {
		this.updateContentLength = updateContentLength;
	}

	@Override
	protected void prepareResponse(HttpServletRequest request, HttpServletResponse response) {
		setResponseContentType(request, response);
		response.setCharacterEncoding(this.encoding.getJavaName());
		if (this.disableCaching) {
			response.addHeader("Cache-Control", "no-store");
		}
	}

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		OutputStream stream = (this.updateContentLength ? createTemporaryOutputStream() : response.getOutputStream());
		Object value = filterAndWrapModel(model, request);

		writeContent(stream, value);
		if (this.updateContentLength) {
			writeToResponse(response, (ByteArrayOutputStream) stream);
		}
	}

	/**
	 * Filter and optionally wrap the model in {@link MappingJacksonValue} container.
	 * <p>
	 *  过滤并可选地将模型包装在{@link MappingJacksonValue}容器中
	 * 
	 * 
	 * @param model the model, as passed on to {@link #renderMergedOutputModel}
	 * @param request current HTTP request
	 * @return the wrapped or unwrapped value to be rendered
	 */
	protected Object filterAndWrapModel(Map<String, Object> model, HttpServletRequest request) {
		Object value = filterModel(model);
		Class<?> serializationView = (Class<?>) model.get(JsonView.class.getName());
		FilterProvider filters = (FilterProvider) model.get(FilterProvider.class.getName());
		if (serializationView != null || filters != null) {
			MappingJacksonValue container = new MappingJacksonValue(value);
			container.setSerializationView(serializationView);
			container.setFilters(filters);
			value = container;
		}
		return value;
	}

	/**
	 * Write the actual JSON content to the stream.
	 * <p>
	 *  将实际的JSON内容写入流
	 * 
	 * 
	 * @param stream the output stream to use
	 * @param object the value to be rendered, as returned from {@link #filterModel}
	 * @throws IOException if writing failed
	 */
	protected void writeContent(OutputStream stream, Object object) throws IOException {
		JsonGenerator generator = this.objectMapper.getFactory().createGenerator(stream, this.encoding);

		writePrefix(generator, object);
		Class<?> serializationView = null;
		FilterProvider filters = null;
		Object value = object;

		if (value instanceof MappingJacksonValue) {
			MappingJacksonValue container = (MappingJacksonValue) value;
			value = container.getValue();
			serializationView = container.getSerializationView();
			filters = container.getFilters();
		}
		if (serializationView != null) {
			this.objectMapper.writerWithView(serializationView).writeValue(generator, value);
		}
		else if (filters != null) {
			this.objectMapper.writer(filters).writeValue(generator, value);
		}
		else {
			this.objectMapper.writeValue(generator, value);
		}
		writeSuffix(generator, object);
		generator.flush();
	}


	/**
	 * Set the attribute in the model that should be rendered by this view.
	 * When set, all other model attributes will be ignored.
	 * <p>
	 *  设置应该由该视图呈现的模型中的属性设置时,所有其他模型属性将被忽略
	 * 
	 */
	public abstract void setModelKey(String modelKey);

	/**
	 * Filter out undesired attributes from the given model.
	 * The return value can be either another {@link Map} or a single value object.
	 * <p>
	 * 从给定模型中过滤出不需要的属性返回值可以是另一个{@link Map}或单个值对象
	 * 
	 * 
	 * @param model the model, as passed on to {@link #renderMergedOutputModel}
	 * @return the value to be rendered
	 */
	protected abstract Object filterModel(Map<String, Object> model);

	/**
	 * Write a prefix before the main content.
	 * <p>
	 *  在主要内容之前编写一个前缀
	 * 
	 * 
	 * @param generator the generator to use for writing content.
	 * @param object the object to write to the output message.
	 */
	protected void writePrefix(JsonGenerator generator, Object object) throws IOException {
	}

	/**
	 * Write a suffix after the main content.
	 * <p>
	 *  主要内容后面写下一个后缀
	 * 
	 * @param generator the generator to use for writing content.
	 * @param object the object to write to the output message.
	 */
	protected void writeSuffix(JsonGenerator generator, Object object) throws IOException {
	}

}
