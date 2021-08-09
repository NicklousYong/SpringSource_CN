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

package org.springframework.http.converter.json;

import com.fasterxml.jackson.databind.ser.FilterProvider;

/**
 * A simple holder for the POJO to serialize via
 * {@link MappingJackson2HttpMessageConverter} along with further
 * serialization instructions to be passed in to the converter.
 *
 * <p>On the server side this wrapper is added with a
 * {@code ResponseBodyInterceptor} after content negotiation selects the
 * converter to use but before the write.
 *
 * <p>On the client side, simply wrap the POJO and pass it in to the
 * {@code RestTemplate}.
 *
 * <p>
 *  POJO通过{@link MappingJackson2HttpMessageConverter}序列化的简单持有者以及要传递给转换器的进一步的序列化指令
 * 
 * <p>在服务器端,这个包装器在内容协商后添加了一个{@code ResponseBodyInterceptor},选择要使用的转换器,但在写入之前
 * 
 *  <p>在客户端,只需将POJO包装并传递给{@code RestTemplate}
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 4.1
 */
public class MappingJacksonValue {

	private Object value;

	private Class<?> serializationView;

	private FilterProvider filters;

	private String jsonpFunction;


	/**
	 * Create a new instance wrapping the given POJO to be serialized.
	 * <p>
	 *  创建一个新的实例,将给定的POJO包装为序列化
	 * 
	 * 
	 * @param value the Object to be serialized
	 */
	public MappingJacksonValue(Object value) {
		this.value = value;
	}


	/**
	 * Modify the POJO to serialize.
	 * <p>
	 *  修改POJO序列化
	 * 
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * Return the POJO that needs to be serialized.
	 * <p>
	 *  返回需要序列化的POJO
	 * 
	 */
	public Object getValue() {
		return this.value;
	}

	/**
	 * Set the serialization view to serialize the POJO with.
	 * <p>
	 *  设置序列化视图以串行化POJO
	 * 
	 * 
	 * @see com.fasterxml.jackson.databind.ObjectMapper#writerWithView(Class)
	 * @see com.fasterxml.jackson.annotation.JsonView
	 */
	public void setSerializationView(Class<?> serializationView) {
		this.serializationView = serializationView;
	}

	/**
	 * Return the serialization view to use.
	 * <p>
	 *  返回要使用的序列化视图
	 * 
	 * 
	 * @see com.fasterxml.jackson.databind.ObjectMapper#writerWithView(Class)
	 * @see com.fasterxml.jackson.annotation.JsonView
	 */
	public Class<?> getSerializationView() {
		return this.serializationView;
	}

	/**
	 * Set the Jackson filter provider to serialize the POJO with.
	 * <p>
	 *  设置杰克逊过滤器提供程序以串行化POJO
	 * 
	 * 
	 * @since 4.2
	 * @see com.fasterxml.jackson.databind.ObjectMapper#writer(FilterProvider)
	 * @see com.fasterxml.jackson.annotation.JsonFilter
	 * @see Jackson2ObjectMapperBuilder#filters(FilterProvider)
	 */
	public void setFilters(FilterProvider filters) {
		this.filters = filters;
	}

	/**
	 * Return the Jackson filter provider to use.
	 * <p>
	 *  返回Jackson过滤器供应商使用
	 * 
	 * 
	 * @since 4.2
	 * @see com.fasterxml.jackson.databind.ObjectMapper#writer(FilterProvider)
	 * @see com.fasterxml.jackson.annotation.JsonFilter
	 */
	public FilterProvider getFilters() {
		return this.filters;
	}

	/**
	 * Set the name of the JSONP function name.
	 * <p>
	 *  设置JSONP函数名的名称
	 * 
	 */
	public void setJsonpFunction(String functionName) {
		this.jsonpFunction = functionName;
	}

	/**
	 * Return the configured JSONP function name.
	 * <p>
	 *  返回配置的JSONP函数名称
	 */
	public String getJsonpFunction() {
		return this.jsonpFunction;
	}

}
