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

package org.springframework.http.converter.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.MediaType;

/**
 * Implementation of {@link org.springframework.http.converter.HttpMessageConverter} that can read and
 * write JSON using <a href="http://wiki.fasterxml.com/JacksonHome">Jackson 2.x's</a> {@link ObjectMapper}.
 *
 * <p>This converter can be used to bind to typed beans, or untyped {@code HashMap} instances.
 *
 * <p>By default, this converter supports {@code application/json} and {@code application/*+json}
 * with {@code UTF-8} character set. This can be overridden by setting the
 * {@link #setSupportedMediaTypes supportedMediaTypes} property.
 *
 * <p>The default constructor uses the default configuration provided by {@link Jackson2ObjectMapperBuilder}.
 *
 * <p>Compatible with Jackson 2.6 and higher, as of Spring 4.3.
 *
 * <p>
 *  实现可以使用<a href=\"http://wikifasterxmlcom/JacksonHome\"> Jackson 2x's </a> {@link ObjectMapper}读取和写入JS
 * ON的{@link orgspringframeworkhttpconverterHttpMessageConverter}。
 * 
 * <p>此转换器可用于绑定到类型的bean或非类型的{@code HashMap}实例
 * 
 *  <p>默认情况下,此转换器支持{@code application / json}和{@code application / * + json}与{@code UTF-8}字符集。
 * 这可以通过设置{@link #setSupportedMediaTypes supportedMediaTypes}来覆盖,属性。
 * 
 *  <p>默认构造函数使用{@link Jackson2ObjectMapperBuilder}提供的默认配置
 * 
 *  <p>兼容于Jackson 26及更高版本,截至春季43
 * 
 * 
 * @author Arjen Poutsma
 * @author Keith Donald
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @author Sebastien Deleuze
 * @since 3.1.2
 */
public class MappingJackson2HttpMessageConverter extends AbstractJackson2HttpMessageConverter {

	private String jsonPrefix;


	/**
	 * Construct a new {@link MappingJackson2HttpMessageConverter} using default configuration
	 * provided by {@link Jackson2ObjectMapperBuilder}.
	 * <p>
	 *  使用{@link Jackson2ObjectMapperBuilder}提供的默认配置构建新的{@link MappingJackson2HttpMessageConverter}
	 * 
	 */
	public MappingJackson2HttpMessageConverter() {
		this(Jackson2ObjectMapperBuilder.json().build());
	}

	/**
	 * Construct a new {@link MappingJackson2HttpMessageConverter} with a custom {@link ObjectMapper}.
	 * You can use {@link Jackson2ObjectMapperBuilder} to build it easily.
	 * <p>
	 *  使用自定义{@link ObjectMapper}构造新的{@link MappingJackson2HttpMessageConverter}您可以使用{@link Jackson2ObjectMapperBuilder}
	 * 轻松构建。
	 * 
	 * 
	 * @see Jackson2ObjectMapperBuilder#json()
	 */
	public MappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
		super(objectMapper, MediaType.APPLICATION_JSON, new MediaType("application", "*+json"));
	}

	/**
	 * Specify a custom prefix to use for this view's JSON output.
	 * Default is none.
	 * <p>
	 * 指定用于此视图的JSON输出的自定义前缀默认为无
	 * 
	 * 
	 * @see #setPrefixJson
	 */
	public void setJsonPrefix(String jsonPrefix) {
		this.jsonPrefix = jsonPrefix;
	}

	/**
	 * Indicate whether the JSON output by this view should be prefixed with ")]}', ". Default is false.
	 * <p>Prefixing the JSON string in this manner is used to help prevent JSON Hijacking.
	 * The prefix renders the string syntactically invalid as a script so that it cannot be hijacked.
	 * This prefix should be stripped before parsing the string as JSON.
	 * <p>
	 *  指示此视图的JSON输出是否应以")]}',"默认为false"为前缀以此方式预处理JSON字符串用于帮助防止JSON劫持该前缀使字符串在语法上无效作为脚本,它不能被劫持在将字符串解析为JSON之前,
	 * 该前缀应该被删除。
	 * 
	 * @see #setJsonPrefix
	 */
	public void setPrefixJson(boolean prefixJson) {
		this.jsonPrefix = (prefixJson ? ")]}', " : null);
	}


	@Override
	protected void writePrefix(JsonGenerator generator, Object object) throws IOException {
		if (this.jsonPrefix != null) {
			generator.writeRaw(this.jsonPrefix);
		}
		String jsonpFunction =
				(object instanceof MappingJacksonValue ? ((MappingJacksonValue) object).getJsonpFunction() : null);
		if (jsonpFunction != null) {
			generator.writeRaw("/**/");
			generator.writeRaw(jsonpFunction + "(");
		}
	}

	@Override
	protected void writeSuffix(JsonGenerator generator, Object object) throws IOException {
		String jsonpFunction =
				(object instanceof MappingJacksonValue ? ((MappingJacksonValue) object).getJsonpFunction() : null);
		if (jsonpFunction != null) {
			generator.writeRaw(");");
		}
	}

}
