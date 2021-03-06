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
import javax.servlet.ServletContext;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * Extends {@code PathExtensionContentNegotiationStrategy} that also uses
 * {@link ServletContext#getMimeType(String)} to resolve file extensions.
 *
 * <p>
 *  扩展{@code PathExtensionContentNegotiationStrategy},它也使用{@link ServletContext#getMimeType(String)}来解析文
 * 件扩展名。
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 3.2
 */
public class ServletPathExtensionContentNegotiationStrategy extends PathExtensionContentNegotiationStrategy {

	private final ServletContext servletContext;


	/**
	 * Create an instance with the given extension-to-MediaType lookup.
	 * <p>
	 *  使用给定的扩展到MediaType查找创建一个实例
	 * 
	 */
	public ServletPathExtensionContentNegotiationStrategy(
			ServletContext servletContext, Map<String, MediaType> mediaTypes) {

		super(mediaTypes);
		Assert.notNull(servletContext, "ServletContext is required");
		this.servletContext = servletContext;
	}

	/**
	 * Create an instance without any mappings to start with. Mappings may be
	 * added later when extensions are resolved through
	 * {@link ServletContext#getMimeType(String)} or via JAF.
	 * <p>
	 * 创建一个没有任何映射的实例,以MAP开头,稍后可以通过{@link ServletContext#getMimeType(String)}或通过JAF解析扩展名
	 * 
	 */
	public ServletPathExtensionContentNegotiationStrategy(ServletContext context) {
		this(context, null);
	}


	/**
	 * Resolve file extension via {@link ServletContext#getMimeType(String)}
	 * and also delegate to base class for a potential JAF lookup.
	 * <p>
	 *  通过{@link ServletContext#getMimeType(String)}解析文件扩展名,并将其委托给潜在JAF查询的基类
	 * 
	 */
	@Override
	protected MediaType handleNoMatch(NativeWebRequest webRequest, String extension)
			throws HttpMediaTypeNotAcceptableException {

		MediaType mediaType = null;
		if (this.servletContext != null) {
			String mimeType = this.servletContext.getMimeType("file." + extension);
			if (StringUtils.hasText(mimeType)) {
				mediaType = MediaType.parseMediaType(mimeType);
			}
		}
		if (mediaType == null || MediaType.APPLICATION_OCTET_STREAM.equals(mediaType)) {
			MediaType superMediaType = super.handleNoMatch(webRequest, extension);
			if (superMediaType != null) {
				mediaType = superMediaType;
			}
		}
		return mediaType;
	}

	/**
	 * Extends the base class
	 * {@link PathExtensionContentNegotiationStrategy#getMediaTypeForResource}
	 * with the ability to also look up through the ServletContext.
	 * <p>
	 *  扩展基础类{@link PathExtensionContentNegotiationStrategy#getMediaTypeForResource},还可以通过ServletContext查找
	 * 
	 * @param resource the resource to look up
	 * @return the MediaType for the extension or {@code null}.
	 * @since 4.3
	 */
	public MediaType getMediaTypeForResource(Resource resource) {
		MediaType mediaType = null;
		if (this.servletContext != null) {
			String mimeType = this.servletContext.getMimeType(resource.getFilename());
			if (StringUtils.hasText(mimeType)) {
				mediaType = MediaType.parseMediaType(mimeType);
			}
		}
		if (mediaType == null || MediaType.APPLICATION_OCTET_STREAM.equals(mediaType)) {
			MediaType superMediaType = super.getMediaTypeForResource(resource);
			if (superMediaType != null) {
				mediaType = superMediaType;
			}
		}
		return mediaType;
	}

}
