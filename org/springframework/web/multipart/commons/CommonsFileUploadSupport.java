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

package org.springframework.web.multipart.commons;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.WebUtils;

/**
 * Base class for multipart resolvers that use Apache Commons FileUpload
 * 1.2 or above.
 *
 * <p>Provides common configuration properties and parsing functionality
 * for multipart requests, using a Map of Spring CommonsMultipartFile instances
 * as representation of uploaded files and a String-based parameter Map as
 * representation of uploaded form fields.
 *
 * <p>Subclasses implement concrete resolution strategies for Servlet or Portlet
 * environments: see CommonsMultipartResolver and CommonsPortletMultipartResolver,
 * respectively. This base class is not tied to either of those APIs, factoring
 * out common functionality.
 *
 * <p>
 *  使用Apache Commons FileUpload 12或更高版本的多部分解析器的基类
 * 
 * <p>为多部分请求提供常见的配置属性和解析功能,使用Spring CommonsMultipartFile实例的映射表示上传的文件和基于String的参数Map作为上传的表单域的表示
 * 
 *  子类实现Servlet或Portlet环境的具体解决策略：分别参见CommonsMultipartResolver和CommonsPortletMultipartResolver这些基类与这些API中
 * 的任何一个都不相关,因而将常用功能。
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.0
 * @see CommonsMultipartFile
 * @see CommonsMultipartResolver
 * @see org.springframework.web.portlet.multipart.CommonsPortletMultipartResolver
 */
public abstract class CommonsFileUploadSupport {

	protected final Log logger = LogFactory.getLog(getClass());

	private final DiskFileItemFactory fileItemFactory;

	private final FileUpload fileUpload;

	private boolean uploadTempDirSpecified = false;


	/**
	 * Instantiate a new CommonsFileUploadSupport with its
	 * corresponding FileItemFactory and FileUpload instances.
	 * <p>
	 *  实例化一个新的CommonsFileUploadSupport及其相应的FileItemFactory和FileUpload实例
	 * 
	 * 
	 * @see #newFileItemFactory
	 * @see #newFileUpload
	 */
	public CommonsFileUploadSupport() {
		this.fileItemFactory = newFileItemFactory();
		this.fileUpload = newFileUpload(getFileItemFactory());
	}


	/**
	 * Return the underlying {@code org.apache.commons.fileupload.disk.DiskFileItemFactory}
	 * instance. There is hardly any need to access this.
	 * <p>
	 *  返回底层的{@code orgapachecommonsfileuploaddiskDiskFileItemFactory}实例几乎没有必要访问这个
	 * 
	 * 
	 * @return the underlying DiskFileItemFactory instance
	 */
	public DiskFileItemFactory getFileItemFactory() {
		return this.fileItemFactory;
	}

	/**
	 * Return the underlying {@code org.apache.commons.fileupload.FileUpload}
	 * instance. There is hardly any need to access this.
	 * <p>
	 * 返回底层的{@code orgapachecommonsfileuploadFileUpload}实例几乎不需要访问它
	 * 
	 * 
	 * @return the underlying FileUpload instance
	 */
	public FileUpload getFileUpload() {
		return this.fileUpload;
	}

	/**
	 * Set the maximum allowed size (in bytes) before an upload gets rejected.
	 * -1 indicates no limit (the default).
	 * <p>
	 *  在上传被拒绝之前设置允许的最大大小(以字节为单位)-1表示无限制(默认值)
	 * 
	 * 
	 * @param maxUploadSize the maximum upload size allowed
	 * @see org.apache.commons.fileupload.FileUploadBase#setSizeMax
	 */
	public void setMaxUploadSize(long maxUploadSize) {
		this.fileUpload.setSizeMax(maxUploadSize);
	}

	/**
	 * Set the maximum allowed size (in bytes) for each individual file before
	 * an upload gets rejected. -1 indicates no limit (the default).
	 * <p>
	 *  在上传拒绝之前设置每个单独文件的最大允许大小(以字节为单位)-1表示无限制(默认值)
	 * 
	 * 
	 * @param maxUploadSizePerFile the maximum upload size per file
	 * @since 4.2
	 * @see org.apache.commons.fileupload.FileUploadBase#setFileSizeMax
	 */
	public void setMaxUploadSizePerFile(long maxUploadSizePerFile) {
		this.fileUpload.setFileSizeMax(maxUploadSizePerFile);
	}

	/**
	 * Set the maximum allowed size (in bytes) before uploads are written to disk.
	 * Uploaded files will still be received past this amount, but they will not be
	 * stored in memory. Default is 10240, according to Commons FileUpload.
	 * <p>
	 *  设置允许的最大大小(以字节为单位),然后将上传文件写入磁盘上传的文件仍将被接收超过此数量,但不会存储在内存中默认为10240,根据Commons FileUpload
	 * 
	 * 
	 * @param maxInMemorySize the maximum in memory size allowed
	 * @see org.apache.commons.fileupload.disk.DiskFileItemFactory#setSizeThreshold
	 */
	public void setMaxInMemorySize(int maxInMemorySize) {
		this.fileItemFactory.setSizeThreshold(maxInMemorySize);
	}

	/**
	 * Set the default character encoding to use for parsing requests,
	 * to be applied to headers of individual parts and to form fields.
	 * Default is ISO-8859-1, according to the Servlet spec.
	 * <p>If the request specifies a character encoding itself, the request
	 * encoding will override this setting. This also allows for generically
	 * overriding the character encoding in a filter that invokes the
	 * {@code ServletRequest.setCharacterEncoding} method.
	 * <p>
	 * 设置用于解析请求的默认字符编码,适用于单个部分的头部和表单字段默认值为ISO-8859-1,根据Servlet规范<p>如果请求指定了字符编码本身,请求编码将覆盖此设置这也允许一般地覆盖调用{@code ServletRequestsetCharacterEncoding}
	 * 方法的过滤器中的字符编码。
	 * 
	 * 
	 * @param defaultEncoding the character encoding to use
	 * @see javax.servlet.ServletRequest#getCharacterEncoding
	 * @see javax.servlet.ServletRequest#setCharacterEncoding
	 * @see WebUtils#DEFAULT_CHARACTER_ENCODING
	 * @see org.apache.commons.fileupload.FileUploadBase#setHeaderEncoding
	 */
	public void setDefaultEncoding(String defaultEncoding) {
		this.fileUpload.setHeaderEncoding(defaultEncoding);
	}

	protected String getDefaultEncoding() {
		String encoding = getFileUpload().getHeaderEncoding();
		if (encoding == null) {
			encoding = WebUtils.DEFAULT_CHARACTER_ENCODING;
		}
		return encoding;
	}

	/**
	 * Set the temporary directory where uploaded files get stored.
	 * Default is the servlet container's temporary directory for the web application.
	 * <p>
	 *  设置上传文件获取存储的临时目录Default是servlet容器的Web应用程序的临时目录
	 * 
	 * 
	 * @see org.springframework.web.util.WebUtils#TEMP_DIR_CONTEXT_ATTRIBUTE
	 */
	public void setUploadTempDir(Resource uploadTempDir) throws IOException {
		if (!uploadTempDir.exists() && !uploadTempDir.getFile().mkdirs()) {
			throw new IllegalArgumentException("Given uploadTempDir [" + uploadTempDir + "] could not be created");
		}
		this.fileItemFactory.setRepository(uploadTempDir.getFile());
		this.uploadTempDirSpecified = true;
	}

	protected boolean isUploadTempDirSpecified() {
		return this.uploadTempDirSpecified;
	}


	/**
	 * Factory method for a Commons DiskFileItemFactory instance.
	 * <p>Default implementation returns a standard DiskFileItemFactory.
	 * Can be overridden to use a custom subclass, e.g. for testing purposes.
	 * <p>
	 *  Commons DiskFileItemFactory实例的工厂方法<p>默认实现返回标准的DiskFileItemFactory可以覆盖使用自定义子类,例如用于测试目的
	 * 
	 * 
	 * @return the new DiskFileItemFactory instance
	 */
	protected DiskFileItemFactory newFileItemFactory() {
		return new DiskFileItemFactory();
	}

	/**
	 * Factory method for a Commons FileUpload instance.
	 * <p><b>To be implemented by subclasses.</b>
	 * <p>
	 * 一个Commons FileUpload实例的工厂方法<p> <b>要由子类</b>实现
	 * 
	 * 
	 * @param fileItemFactory the Commons FileItemFactory to build upon
	 * @return the Commons FileUpload instance
	 */
	protected abstract FileUpload newFileUpload(FileItemFactory fileItemFactory);


	/**
	 * Determine an appropriate FileUpload instance for the given encoding.
	 * <p>Default implementation returns the shared FileUpload instance
	 * if the encoding matches, else creates a new FileUpload instance
	 * with the same configuration other than the desired encoding.
	 * <p>
	 *  为给定的编码确定适当的FileUpload实例<p>如果编码匹配,则默认实现返回共享的FileUpload实例,否则创建一个新的FileUpload实例,具有与所需编码不同的配置
	 * 
	 * 
	 * @param encoding the character encoding to use
	 * @return an appropriate FileUpload instance.
	 */
	protected FileUpload prepareFileUpload(String encoding) {
		FileUpload fileUpload = getFileUpload();
		FileUpload actualFileUpload = fileUpload;

		// Use new temporary FileUpload instance if the request specifies
		// its own encoding that does not match the default encoding.
		if (encoding != null && !encoding.equals(fileUpload.getHeaderEncoding())) {
			actualFileUpload = newFileUpload(getFileItemFactory());
			actualFileUpload.setSizeMax(fileUpload.getSizeMax());
			actualFileUpload.setFileSizeMax(fileUpload.getFileSizeMax());
			actualFileUpload.setHeaderEncoding(encoding);
		}

		return actualFileUpload;
	}

	/**
	 * Parse the given List of Commons FileItems into a Spring MultipartParsingResult,
	 * containing Spring MultipartFile instances and a Map of multipart parameter.
	 * <p>
	 *  将给定的Commons FileItem列表解析成Spring MultipartParsingResult,其中包含Spring MultipartFile实例和multipart参数Map
	 * 
	 * 
	 * @param fileItems the Commons FileIterms to parse
	 * @param encoding the encoding to use for form fields
	 * @return the Spring MultipartParsingResult
	 * @see CommonsMultipartFile#CommonsMultipartFile(org.apache.commons.fileupload.FileItem)
	 */
	protected MultipartParsingResult parseFileItems(List<FileItem> fileItems, String encoding) {
		MultiValueMap<String, MultipartFile> multipartFiles = new LinkedMultiValueMap<String, MultipartFile>();
		Map<String, String[]> multipartParameters = new HashMap<String, String[]>();
		Map<String, String> multipartParameterContentTypes = new HashMap<String, String>();

		// Extract multipart files and multipart parameters.
		for (FileItem fileItem : fileItems) {
			if (fileItem.isFormField()) {
				String value;
				String partEncoding = determineEncoding(fileItem.getContentType(), encoding);
				if (partEncoding != null) {
					try {
						value = fileItem.getString(partEncoding);
					}
					catch (UnsupportedEncodingException ex) {
						if (logger.isWarnEnabled()) {
							logger.warn("Could not decode multipart item '" + fileItem.getFieldName() +
									"' with encoding '" + partEncoding + "': using platform default");
						}
						value = fileItem.getString();
					}
				}
				else {
					value = fileItem.getString();
				}
				String[] curParam = multipartParameters.get(fileItem.getFieldName());
				if (curParam == null) {
					// simple form field
					multipartParameters.put(fileItem.getFieldName(), new String[] {value});
				}
				else {
					// array of simple form fields
					String[] newParam = StringUtils.addStringToArray(curParam, value);
					multipartParameters.put(fileItem.getFieldName(), newParam);
				}
				multipartParameterContentTypes.put(fileItem.getFieldName(), fileItem.getContentType());
			}
			else {
				// multipart file field
				CommonsMultipartFile file = new CommonsMultipartFile(fileItem);
				multipartFiles.add(file.getName(), file);
				if (logger.isDebugEnabled()) {
					logger.debug("Found multipart file [" + file.getName() + "] of size " + file.getSize() +
							" bytes with original filename [" + file.getOriginalFilename() + "], stored " +
							file.getStorageDescription());
				}
			}
		}
		return new MultipartParsingResult(multipartFiles, multipartParameters, multipartParameterContentTypes);
	}

	/**
	 * Cleanup the Spring MultipartFiles created during multipart parsing,
	 * potentially holding temporary data on disk.
	 * <p>Deletes the underlying Commons FileItem instances.
	 * <p>
	 *  清理在多部分解析过程中创建的Spring MultipartFiles,可能在磁盘上保留临时数据<p>删除底层Commons FileItem实例
	 * 
	 * 
	 * @param multipartFiles Collection of MultipartFile instances
	 * @see org.apache.commons.fileupload.FileItem#delete()
	 */
	protected void cleanupFileItems(MultiValueMap<String, MultipartFile> multipartFiles) {
		for (List<MultipartFile> files : multipartFiles.values()) {
			for (MultipartFile file : files) {
				if (file instanceof CommonsMultipartFile) {
					CommonsMultipartFile cmf = (CommonsMultipartFile) file;
					cmf.getFileItem().delete();
					if (logger.isDebugEnabled()) {
						logger.debug("Cleaning up multipart file [" + cmf.getName() + "] with original filename [" +
								cmf.getOriginalFilename() + "], stored " + cmf.getStorageDescription());
					}
				}
			}
		}
	}

	private String determineEncoding(String contentTypeHeader, String defaultEncoding) {
		if (!StringUtils.hasText(contentTypeHeader)) {
			return defaultEncoding;
		}
		MediaType contentType = MediaType.parseMediaType(contentTypeHeader);
		Charset charset = contentType.getCharset();
		return (charset != null ? charset.name() : defaultEncoding);
	}


	/**
	 * Holder for a Map of Spring MultipartFiles and a Map of
	 * multipart parameters.
	 * <p>
	 *  Spring MultipartFiles地图的持有人和多部分参数的地图
	 */
	protected static class MultipartParsingResult {

		private final MultiValueMap<String, MultipartFile> multipartFiles;

		private final Map<String, String[]> multipartParameters;

		private final Map<String, String> multipartParameterContentTypes;

		public MultipartParsingResult(MultiValueMap<String, MultipartFile> mpFiles,
				Map<String, String[]> mpParams, Map<String, String> mpParamContentTypes) {
			this.multipartFiles = mpFiles;
			this.multipartParameters = mpParams;
			this.multipartParameterContentTypes = mpParamContentTypes;
		}

		public MultiValueMap<String, MultipartFile> getMultipartFiles() {
			return this.multipartFiles;
		}

		public Map<String, String[]> getMultipartParameters() {
			return this.multipartParameters;
		}

		public Map<String, String> getMultipartParameterContentTypes() {
			return this.multipartParameterContentTypes;
		}
	}

}
