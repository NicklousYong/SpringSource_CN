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

package org.springframework.web.accept;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * A {@code ContentNegotiationStrategy} that resolves a query parameter to a key
 * to be used to look up a media type. The default parameter name is {@code format}.
 *
 * <p>
 *  将{@code ContentNegotiationStrategy}解析为用于查找媒体类型的键的查询参数默认参数名称为{@code format}
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 3.2
 */
public class ParameterContentNegotiationStrategy extends AbstractMappingContentNegotiationStrategy {

	private static final Log logger = LogFactory.getLog(ParameterContentNegotiationStrategy.class);

	private String parameterName = "format";


	/**
	 * Create an instance with the given map of file extensions and media types.
	 * <p>
	 * 使用给定的文件扩展名和媒体类型的映射创建一个实例
	 * 
	 */
	public ParameterContentNegotiationStrategy(Map<String, MediaType> mediaTypes) {
		super(mediaTypes);
	}


	/**
	 * Set the name of the parameter to use to determine requested media types.
	 * <p>By default this is set to {@code "format"}.
	 * <p>
	 *  设置用于确定所请求的媒体类型的参数的名称<p>默认情况下,这被设置为{@code"格式"}
	 */
	public void setParameterName(String parameterName) {
		Assert.notNull(parameterName, "'parameterName' is required");
		this.parameterName = parameterName;
	}

	public String getParameterName() {
		return this.parameterName;
	}


	@Override
	protected String getMediaTypeKey(NativeWebRequest request) {
		return request.getParameter(getParameterName());
	}

	@Override
	protected void handleMatch(String mediaTypeKey, MediaType mediaType) {
		if (logger.isDebugEnabled()) {
			logger.debug("Requested media type: '" + mediaType + "' based on '" +
					getParameterName() + "'='" + mediaTypeKey + "'");
		}
	}

	@Override
	protected MediaType handleNoMatch(NativeWebRequest request, String key)
			throws HttpMediaTypeNotAcceptableException {

		throw new HttpMediaTypeNotAcceptableException(getAllMediaTypes());
	}

}
