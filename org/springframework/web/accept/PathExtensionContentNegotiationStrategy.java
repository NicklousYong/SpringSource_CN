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

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.UrlPathHelper;

/**
 * A {@code ContentNegotiationStrategy} that resolves the file extension in the
 * request path to a key to be used to look up a media type.
 *
 * <p>If the file extension is not found in the explicit registrations provided
 * to the constructor, the Java Activation Framework (JAF) is used as a fallback
 * mechanism.
 *
 * <p>The presence of the JAF is detected and enabled automatically but the
 * {@link #setUseJaf(boolean)} property may be set to false.
 *
 * <p>
 *  {@code ContentNegotiationStrategy},将请求路径中的文件扩展名解析为用于查找媒体类型的密钥
 * 
 * <p>如果在提供给构造函数的显式注册中找不到文件扩展名,Java Activation Framework(JAF)将被用作回退机制
 * 
 *  <p>检测并启用JAF的存在,但{@link #setUseJaf(boolean)}属性可能设置为false
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 3.2
 */
public class PathExtensionContentNegotiationStrategy extends AbstractMappingContentNegotiationStrategy {

	private static final boolean JAF_PRESENT = ClassUtils.isPresent("javax.activation.FileTypeMap",
			PathExtensionContentNegotiationStrategy.class.getClassLoader());

	private static final Log logger = LogFactory.getLog(PathExtensionContentNegotiationStrategy.class);

	private UrlPathHelper urlPathHelper = new UrlPathHelper();

	private boolean useJaf = true;

	private boolean ignoreUnknownExtensions = true;


	/**
	 * Create an instance without any mappings to start with. Mappings may be added
	 * later on if any extensions are resolved through the Java Activation framework.
	 * <p>
	 *  创建一个没有任何映射的实例,以映射开始,如果任何扩展通过Java Activation框架解决,可以稍后添加
	 * 
	 */
	public PathExtensionContentNegotiationStrategy() {
		this(null);
	}

	/**
	 * Create an instance with the given map of file extensions and media types.
	 * <p>
	 *  使用给定的文件扩展名和媒体类型的映射创建一个实例
	 * 
	 */
	public PathExtensionContentNegotiationStrategy(Map<String, MediaType> mediaTypes) {
		super(mediaTypes);
		this.urlPathHelper.setUrlDecode(false);
	}


	/**
	 * Configure a {@code UrlPathHelper} to use in {@link #getMediaTypeKey}
	 * in order to derive the lookup path for a target request URL path.
	 * <p>
	 *  配置{@code UrlPathHelper}以在{@link #getMediaTypeKey}中使用,以便导出目标请求URL路径的查找路径
	 * 
	 * 
	 * @since 4.2.8
	 */
	public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
		this.urlPathHelper = urlPathHelper;
	}

	/**
	 * Whether to use the Java Activation Framework to look up file extensions.
	 * <p>By default this is set to "true" but depends on JAF being present.
	 * <p>
	 * 是否使用Java Activation Framework来查找文件扩展名<p>默认情况下,这被设置为"true",但是依赖于JAF存在
	 * 
	 */
	public void setUseJaf(boolean useJaf) {
		this.useJaf = useJaf;
	}

	/**
	 * Whether to ignore requests with unknown file extension. Setting this to
	 * {@code false} results in {@code HttpMediaTypeNotAcceptableException}.
	 * <p>By default this is set to {@code true}.
	 * <p>
	 *  是否忽略具有未知文件扩展名的请求将此设置为{@code false}会导致{@code HttpMediaTypeNotAcceptableException} <p>默认情况下设置为{@code true}
	 * 。
	 * 
	 */
	public void setIgnoreUnknownExtensions(boolean ignoreUnknownExtensions) {
		this.ignoreUnknownExtensions = ignoreUnknownExtensions;
	}


	@Override
	protected String getMediaTypeKey(NativeWebRequest webRequest) {
		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
		if (request == null) {
			logger.warn("An HttpServletRequest is required to determine the media type key");
			return null;
		}
		String path = this.urlPathHelper.getLookupPathForRequest(request);
		String extension = UriUtils.extractFileExtension(path);
		return (StringUtils.hasText(extension) ? extension.toLowerCase(Locale.ENGLISH) : null);
	}

	@Override
	protected MediaType handleNoMatch(NativeWebRequest webRequest, String extension)
			throws HttpMediaTypeNotAcceptableException {

		if (this.useJaf && JAF_PRESENT) {
			MediaType mediaType = ActivationMediaTypeFactory.getMediaType("file." + extension);
			if (mediaType != null && !MediaType.APPLICATION_OCTET_STREAM.equals(mediaType)) {
				return mediaType;
			}
		}
		if (this.ignoreUnknownExtensions) {
			return null;
		}
		throw new HttpMediaTypeNotAcceptableException(getAllMediaTypes());
	}

	/**
	 * A public method exposing the knowledge of the path extension strategy to
	 * resolve file extensions to a MediaType in this case for a given
	 * {@link Resource}. The method first looks up any explicitly registered
	 * file extensions first and then falls back on JAF if available.
	 * <p>
	 *  在这种情况下,给定的{@link Resource}方法会公开路径扩展策略的知识解决MediaType的文件扩展名。该方法首先查找任何明确注册的文件扩展名,然后退回到JAF(如果可用)
	 * 
	 * 
	 * @param resource the resource to look up
	 * @return the MediaType for the extension or {@code null}.
	 * @since 4.3
	 */
	public MediaType getMediaTypeForResource(Resource resource) {
		Assert.notNull(resource);
		MediaType mediaType = null;
		String filename = resource.getFilename();
		String extension = StringUtils.getFilenameExtension(filename);
		if (extension != null) {
			mediaType = lookupMediaType(extension);
		}
		if (mediaType == null && JAF_PRESENT) {
			mediaType = ActivationMediaTypeFactory.getMediaType(filename);
		}
		if (MediaType.APPLICATION_OCTET_STREAM.equals(mediaType)) {
			mediaType = null;
		}
		return mediaType;
	}


	/**
	 * Inner class to avoid hard-coded dependency on JAF.
	 * <p>
	 *  内部类避免硬编码对JAF的依赖
	 * 
	 */
	private static class ActivationMediaTypeFactory {

		private static final FileTypeMap fileTypeMap;

		static {
			fileTypeMap = initFileTypeMap();
		}

		/**
		 * Find extended mime.types from the spring-context-support module.
		 * <p>
		 *  从弹簧上下文支持模块中查找扩展模式
		 */
		private static FileTypeMap initFileTypeMap() {
			Resource resource = new ClassPathResource("org/springframework/mail/javamail/mime.types");
			if (resource.exists()) {
				if (logger.isTraceEnabled()) {
					logger.trace("Loading JAF FileTypeMap from " + resource);
				}
				InputStream inputStream = null;
				try {
					inputStream = resource.getInputStream();
					return new MimetypesFileTypeMap(inputStream);
				}
				catch (IOException ex) {
					// ignore
				}
				finally {
					if (inputStream != null) {
						try {
							inputStream.close();
						}
						catch (IOException ex) {
							// ignore
						}
					}
				}
			}
			if (logger.isTraceEnabled()) {
				logger.trace("Loading default Java Activation Framework FileTypeMap");
			}
			return FileTypeMap.getDefaultFileTypeMap();
		}

		public static MediaType getMediaType(String filename) {
			String mediaType = fileTypeMap.getContentType(filename);
			return (StringUtils.hasText(mediaType) ? MediaType.parseMediaType(mediaType) : null);
		}
	}

}
