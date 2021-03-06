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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * Base class for {@code ContentNegotiationStrategy} implementations with the
 * steps to resolve a request to media types.
 *
 * <p>First a key (e.g. "json", "pdf") must be extracted from the request (e.g.
 * file extension, query param). The key must then be resolved to media type(s)
 * through the base class {@link MappingMediaTypeFileExtensionResolver} which
 * stores such mappings.
 *
 * <p>The method {@link #handleNoMatch} allow sub-classes to plug in additional
 * ways of looking up media types (e.g. through the Java Activation framework,
 * or {@link javax.servlet.ServletContext#getMimeType}. Media types resolved
 * via base classes are then added to the base class
 * {@link MappingMediaTypeFileExtensionResolver}, i.e. cached for new lookups.
 *
 * <p>
 *  使用{@code ContentNegotiationStrategy}实现的基类,并具有解决对媒体类型的请求的步骤
 * 
 * <p>首先必须从请求中提取一个关键字(例如"json","pdf")(例如,文件扩展名,查询参数)。
 * 然后,密钥必须通过基类{@link MappingMediaTypeFileExtensionResolver }存储这样的映射。
 * 
 *  <p>方法{@link #handleNoMatch}允许子类插入查找媒体类型的其他方式(例如通过Java Activation框架或{@link javaxservletServletContext#getMimeType}
 * 然后添加通过基类解析的媒体类型到基类{@link MappingMediaTypeFileExtensionResolver},即缓存新的查找。
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 3.2
 */
public abstract class AbstractMappingContentNegotiationStrategy extends MappingMediaTypeFileExtensionResolver
		implements ContentNegotiationStrategy {

	/**
	 * Create an instance with the given map of file extensions and media types.
	 * <p>
	 *  使用给定的文件扩展名和媒体类型的映射创建一个实例
	 * 
	 */
	public AbstractMappingContentNegotiationStrategy(Map<String, MediaType> mediaTypes) {
		super(mediaTypes);
	}


	@Override
	public List<MediaType> resolveMediaTypes(NativeWebRequest webRequest)
			throws HttpMediaTypeNotAcceptableException {

		return resolveMediaTypeKey(webRequest, getMediaTypeKey(webRequest));
	}

	/**
	 * An alternative to {@link #resolveMediaTypes(NativeWebRequest)} that accepts
	 * an already extracted key.
	 * <p>
	 *  接受已经提取的密钥的{@link #resolveMediaTypes(NativeWebRequest)}的替代方法
	 * 
	 * 
	 * @since 3.2.16
	 */
	public List<MediaType> resolveMediaTypeKey(NativeWebRequest webRequest, String key)
			throws HttpMediaTypeNotAcceptableException {

		if (StringUtils.hasText(key)) {
			MediaType mediaType = lookupMediaType(key);
			if (mediaType != null) {
				handleMatch(key, mediaType);
				return Collections.singletonList(mediaType);
			}
			mediaType = handleNoMatch(webRequest, key);
			if (mediaType != null) {
				addMapping(key, mediaType);
				return Collections.singletonList(mediaType);
			}
		}
		return Collections.emptyList();
	}


	/**
	 * Extract a key from the request to use to look up media types.
	 * <p>
	 * 从请求中提取密钥以用于查找介质类型
	 * 
	 * 
	 * @return the lookup key or {@code null}.
	 */
	protected abstract String getMediaTypeKey(NativeWebRequest request);

	/**
	 * Override to provide handling when a key is successfully resolved via
	 * {@link #lookupMediaType}.
	 * <p>
	 *  覆盖通过{@link #lookupMediaType}成功解析密钥时提供处理
	 * 
	 */
	protected void handleMatch(String key, MediaType mediaType) {
	}

	/**
	 * Override to provide handling when a key is not resolved via.
	 * {@link #lookupMediaType}. Sub-classes can take further steps to
	 * determine the media type(s). If a MediaType is returned from
	 * this method it will be added to the cache in the base class.
	 * <p>
	 *  覆盖当通过{@link #lookupMediaType}未解析密钥时提供处理子类可以采取进一步的步骤来确定媒体类型如果从此方法返回MediaType,它将被添加到基础中的缓存中类
	 */
	protected MediaType handleNoMatch(NativeWebRequest request, String key)
			throws HttpMediaTypeNotAcceptableException {

		return null;
	}

}
