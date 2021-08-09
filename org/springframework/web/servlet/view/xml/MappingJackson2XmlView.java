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

package org.springframework.web.servlet.view.xml;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.json.AbstractJackson2View;

/**
 * Spring MVC {@link View} that renders XML content by serializing the model for the current request
 * using <a href="http://wiki.fasterxml.com/JacksonHome">Jackson 2's</a> {@link XmlMapper}.
 *
 * <p>The Object to be serialized is supplied as a parameter in the model. The first serializable
 * entry is used. Users can either specify a specific entry in the model via the
 * {@link #setModelKey(String) sourceKey} property.
 *
 * <p>The default constructor uses the default configuration provided by {@link Jackson2ObjectMapperBuilder}.
 *
 * <p>Compatible with Jackson 2.6 and higher, as of Spring 4.3.
 *
 * <p>
 *  Spring MVC {@link View}通过使用<a href=\"http://wikifasterxmlcom/JacksonHome\"> Jackson 2的</a> {@link XmlMapper}
 * 序列化当前请求的模型来呈现XML内容。
 * 
 * 要串行化的对象作为模型中的参数提供使用第一个可序列化条目用户可以通过{@link #setModelKey(String)sourceKey}属性指定模型中的特定条目
 * 
 *  <p>默认构造函数使用{@link Jackson2ObjectMapperBuilder}提供的默认配置
 * 
 *  <p>兼容于Jackson 26及更高版本,截至春季43
 * 
 * 
 * @author Sebastien Deleuze
 * @since 4.1
 */
public class MappingJackson2XmlView extends AbstractJackson2View {

	public static final String DEFAULT_CONTENT_TYPE = "application/xml";


	private String modelKey;


	/**
	 * Construct a new {@code MappingJackson2XmlView} using default configuration
	 * provided by {@link Jackson2ObjectMapperBuilder} and setting the content type
	 * to {@code application/xml}.
	 * <p>
	 *  使用{@link Jackson2ObjectMapperBuilder}提供的默认配置构建新的{@code MappingJackson2XmlView},并将内容类型设置为{@code application / xml}
	 * 。
	 * 
	 */
	public MappingJackson2XmlView() {
		super(Jackson2ObjectMapperBuilder.xml().build(), DEFAULT_CONTENT_TYPE);
	}

	/**
	 * Construct a new {@code MappingJackson2XmlView} using the provided {@link XmlMapper}
	 * and setting the content type to {@code application/xml}.
	 * <p>
	 *  使用提供的{@link XmlMapper}构建新的{@code MappingJackson2XmlView},并将内容类型设置为{@code application / xml}
	 * 
	 * 
	 * @since 4.2.1
	 */
	public MappingJackson2XmlView(XmlMapper xmlMapper) {
		super(xmlMapper, DEFAULT_CONTENT_TYPE);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 *  {} @inheritDoc
	 * 
	 */
	@Override
	public void setModelKey(String modelKey) {
		this.modelKey = modelKey;
	}

	/**
	 * Filter out undesired attributes from the given model.
	 * The return value can be either another {@link Map} or a single value object.
	 * <p>
	 * 从给定模型中过滤出不需要的属性返回值可以是另一个{@link Map}或单个值对象
	 * 
	 * @param model the model, as passed on to {@link #renderMergedOutputModel}
	 * @return the value to be rendered
	 */
	@Override
	protected Object filterModel(Map<String, Object> model) {
		Object value = null;
		if (this.modelKey != null) {
			value = model.get(this.modelKey);
			if (value == null) {
				throw new IllegalStateException(
						"Model contains no object with key [" + this.modelKey + "]");
			}
		}
		else {
			for (Map.Entry<String, Object> entry : model.entrySet()) {
				if (!(entry.getValue() instanceof BindingResult) && !entry.getKey().equals(JsonView.class.getName())) {
					if (value != null) {
						throw new IllegalStateException("Model contains more than one object to render, only one is supported");
					}
					value = entry.getValue();
				}
			}
		}
		return value;
	}

}
