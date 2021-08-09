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

package org.springframework.web.multipart.commons;

import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import org.springframework.util.Assert;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;
import org.springframework.web.util.WebUtils;

/**
 * Servlet-based {@link MultipartResolver} implementation for
 * <a href="http://commons.apache.org/proper/commons-fileupload">Apache Commons FileUpload</a>
 * 1.2 or above.
 *
 * <p>Provides "maxUploadSize", "maxInMemorySize" and "defaultEncoding" settings as
 * bean properties (inherited from {@link CommonsFileUploadSupport}). See corresponding
 * ServletFileUpload / DiskFileItemFactory properties ("sizeMax", "sizeThreshold",
 * "headerEncoding") for details in terms of defaults and accepted values.
 *
 * <p>Saves temporary files to the servlet container's temporary directory.
 * Needs to be initialized <i>either</i> by an application context <i>or</i>
 * via the constructor that takes a ServletContext (for standalone usage).
 *
 * <p>
 *  <a href=\"http://commonsapacheorg/proper/commons-fileupload\"> Apache Commons FileUpload </a> 12或更高版
 * 本的基于Servlet的{@link MultipartResolver}实施。
 * 
 * <p>提供"maxUploadSize","maxInMemorySize"和"defaultEncoding"设置作为bean属性(从{@link CommonsFileUploadSupport}继
 * 承))有关详细信息,请参阅相应的ServletFileUpload / DiskFileItemFactory属性("sizeMax","sizeThreshold","headerEncoding")
 * 默认条款和接受价值。
 * 
 *  将临时文件保存到servlet容器的临时目录需要由应用程序上下文<i>或</i>通过承载ServletContext(用于独立使用)的构造函数初始化<i>
 * 
 * 
 * @author Trevor D. Cook
 * @author Juergen Hoeller
 * @since 29.09.2003
 * @see #CommonsMultipartResolver(ServletContext)
 * @see #setResolveLazily
 * @see org.springframework.web.portlet.multipart.CommonsPortletMultipartResolver
 * @see org.apache.commons.fileupload.servlet.ServletFileUpload
 * @see org.apache.commons.fileupload.disk.DiskFileItemFactory
 */
public class CommonsMultipartResolver extends CommonsFileUploadSupport
		implements MultipartResolver, ServletContextAware {

	private boolean resolveLazily = false;


	/**
	 * Constructor for use as bean. Determines the servlet container's
	 * temporary directory via the ServletContext passed in as through the
	 * ServletContextAware interface (typically by a WebApplicationContext).
	 * <p>
	 *  用作bean的构造方法通过ServletContextAware接口(通常由WebApplicationContext)传递的ServletContext确定servlet容器的临时目录,
	 * 
	 * 
	 * @see #setServletContext
	 * @see org.springframework.web.context.ServletContextAware
	 * @see org.springframework.web.context.WebApplicationContext
	 */
	public CommonsMultipartResolver() {
		super();
	}

	/**
	 * Constructor for standalone usage. Determines the servlet container's
	 * temporary directory via the given ServletContext.
	 * <p>
	 * 独立使用的构造方法通过给定的ServletContext确定servlet容器的临时目录
	 * 
	 * 
	 * @param servletContext the ServletContext to use
	 */
	public CommonsMultipartResolver(ServletContext servletContext) {
		this();
		setServletContext(servletContext);
	}


	/**
	 * Set whether to resolve the multipart request lazily at the time of
	 * file or parameter access.
	 * <p>Default is "false", resolving the multipart elements immediately, throwing
	 * corresponding exceptions at the time of the {@link #resolveMultipart} call.
	 * Switch this to "true" for lazy multipart parsing, throwing parse exceptions
	 * once the application attempts to obtain multipart files or parameters.
	 * <p>
	 *  设置是否在文件或参数访问时懒惰地解析多部分请求<p>默认为"false",立即解析多部分元素,在{@link #resolveMultipart}调用时抛出相应的异常将其切换为"对于惰性多部分解析,应
	 * 用程序尝试获取多部分文件或参数后抛出解析异常。
	 * 
	 */
	public void setResolveLazily(boolean resolveLazily) {
		this.resolveLazily = resolveLazily;
	}

	/**
	 * Initialize the underlying {@code org.apache.commons.fileupload.servlet.ServletFileUpload}
	 * instance. Can be overridden to use a custom subclass, e.g. for testing purposes.
	 * <p>
	 *  初始化底层的{@code orgapachecommonsfileuploadservletServletFileUpload}实例可以覆盖使用自定义子类,例如用于测试目的
	 * 
	 * 
	 * @param fileItemFactory the Commons FileItemFactory to use
	 * @return the new ServletFileUpload instance
	 */
	@Override
	protected FileUpload newFileUpload(FileItemFactory fileItemFactory) {
		return new ServletFileUpload(fileItemFactory);
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		if (!isUploadTempDirSpecified()) {
			getFileItemFactory().setRepository(WebUtils.getTempDir(servletContext));
		}
	}


	@Override
	public boolean isMultipart(HttpServletRequest request) {
		return (request != null && ServletFileUpload.isMultipartContent(request));
	}

	@Override
	public MultipartHttpServletRequest resolveMultipart(final HttpServletRequest request) throws MultipartException {
		Assert.notNull(request, "Request must not be null");
		if (this.resolveLazily) {
			return new DefaultMultipartHttpServletRequest(request) {
				@Override
				protected void initializeMultipart() {
					MultipartParsingResult parsingResult = parseRequest(request);
					setMultipartFiles(parsingResult.getMultipartFiles());
					setMultipartParameters(parsingResult.getMultipartParameters());
					setMultipartParameterContentTypes(parsingResult.getMultipartParameterContentTypes());
				}
			};
		}
		else {
			MultipartParsingResult parsingResult = parseRequest(request);
			return new DefaultMultipartHttpServletRequest(request, parsingResult.getMultipartFiles(),
					parsingResult.getMultipartParameters(), parsingResult.getMultipartParameterContentTypes());
		}
	}

	/**
	 * Parse the given servlet request, resolving its multipart elements.
	 * <p>
	 *  解析给定的servlet请求,解析其多部分元素
	 * 
	 * 
	 * @param request the request to parse
	 * @return the parsing result
	 * @throws MultipartException if multipart resolution failed.
	 */
	protected MultipartParsingResult parseRequest(HttpServletRequest request) throws MultipartException {
		String encoding = determineEncoding(request);
		FileUpload fileUpload = prepareFileUpload(encoding);
		try {
			List<FileItem> fileItems = ((ServletFileUpload) fileUpload).parseRequest(request);
			return parseFileItems(fileItems, encoding);
		}
		catch (FileUploadBase.SizeLimitExceededException ex) {
			throw new MaxUploadSizeExceededException(fileUpload.getSizeMax(), ex);
		}
		catch (FileUploadBase.FileSizeLimitExceededException ex) {
			throw new MaxUploadSizeExceededException(fileUpload.getFileSizeMax(), ex);
		}
		catch (FileUploadException ex) {
			throw new MultipartException("Failed to parse multipart servlet request", ex);
		}
	}

	/**
	 * Determine the encoding for the given request.
	 * Can be overridden in subclasses.
	 * <p>The default implementation checks the request encoding,
	 * falling back to the default encoding specified for this resolver.
	 * <p>
	 * 确定给定请求的编码可以在子类中覆盖<p>默认实现检查请求编码,返回到为此解析器指定的默认编码
	 * 
	 * @param request current HTTP request
	 * @return the encoding for the request (never {@code null})
	 * @see javax.servlet.ServletRequest#getCharacterEncoding
	 * @see #setDefaultEncoding
	 */
	protected String determineEncoding(HttpServletRequest request) {
		String encoding = request.getCharacterEncoding();
		if (encoding == null) {
			encoding = getDefaultEncoding();
		}
		return encoding;
	}

	@Override
	public void cleanupMultipart(MultipartHttpServletRequest request) {
		if (request != null) {
			try {
				cleanupFileItems(request.getMultiFileMap());
			}
			catch (Throwable ex) {
				logger.warn("Failed to perform multipart cleanup for servlet request", ex);
			}
		}
	}

}
