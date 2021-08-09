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

package org.springframework.web.context.support;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.ServletContext;

import org.springframework.core.io.AbstractFileResolvingResource;
import org.springframework.core.io.ContextResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

/**
 * {@link org.springframework.core.io.Resource} implementation for
 * {@link javax.servlet.ServletContext} resources, interpreting
 * relative paths within the web application root directory.
 *
 * <p>Always supports stream access and URL access, but only allows
 * {@code java.io.File} access when the web application archive
 * is expanded.
 *
 * <p>
 *  针对{@link javaxservletServletContext}资源的{@link orgspringframeworkcoreioResource}实现,解释Web应用程序根目录中的相对路径
 * 。
 * 
 * 始终支持流访问和URL访问,但是当扩展Web应用程序存档时,只允许{@code javaioFile}访问
 * 
 * 
 * @author Juergen Hoeller
 * @since 28.12.2003
 * @see javax.servlet.ServletContext#getResourceAsStream
 * @see javax.servlet.ServletContext#getResource
 * @see javax.servlet.ServletContext#getRealPath
 */
public class ServletContextResource extends AbstractFileResolvingResource implements ContextResource {

	private final ServletContext servletContext;

	private final String path;


	/**
	 * Create a new ServletContextResource.
	 * <p>The Servlet spec requires that resource paths start with a slash,
	 * even if many containers accept paths without leading slash too.
	 * Consequently, the given path will be prepended with a slash if it
	 * doesn't already start with one.
	 * <p>
	 *  创建一个新的ServletContextResource Servlet规范要求资源路径以斜杠开始,即使许多容器也接受不带前导斜杠的路径,因此如果尚未开始,给定路径将以斜杠添加
	 * 
	 * 
	 * @param servletContext the ServletContext to load from
	 * @param path the path of the resource
	 */
	public ServletContextResource(ServletContext servletContext, String path) {
		// check ServletContext
		Assert.notNull(servletContext, "Cannot resolve ServletContextResource without ServletContext");
		this.servletContext = servletContext;

		// check path
		Assert.notNull(path, "Path is required");
		String pathToUse = StringUtils.cleanPath(path);
		if (!pathToUse.startsWith("/")) {
			pathToUse = "/" + pathToUse;
		}
		this.path = pathToUse;
	}


	/**
	 * Return the ServletContext for this resource.
	 * <p>
	 *  返回此资源的ServletContext
	 * 
	 */
	public final ServletContext getServletContext() {
		return this.servletContext;
	}

	/**
	 * Return the path for this resource.
	 * <p>
	 *  返回此资源的路径
	 * 
	 */
	public final String getPath() {
		return this.path;
	}

	/**
	 * This implementation checks {@code ServletContext.getResource}.
	 * <p>
	 *  这个实现检查{@code ServletContextgetResource}
	 * 
	 * 
	 * @see javax.servlet.ServletContext#getResource(String)
	 */
	@Override
	public boolean exists() {
		try {
			URL url = this.servletContext.getResource(this.path);
			return (url != null);
		}
		catch (MalformedURLException ex) {
			return false;
		}
	}

	/**
	 * This implementation delegates to {@code ServletContext.getResourceAsStream},
	 * which returns {@code null} in case of a non-readable resource (e.g. a directory).
	 * <p>
	 *  该实现委托{@code ServletContextgetResourceAsStream},在不可读资源(例如目录)的情况下返回{@code null}
	 * 
	 * 
	 * @see javax.servlet.ServletContext#getResourceAsStream(String)
	 */
	@Override
	public boolean isReadable() {
		InputStream is = this.servletContext.getResourceAsStream(this.path);
		if (is != null) {
			try {
				is.close();
			}
			catch (IOException ex) {
				// ignore
			}
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * This implementation delegates to {@code ServletContext.getResourceAsStream},
	 * but throws a FileNotFoundException if no resource found.
	 * <p>
	 * 此实现委托给{@code ServletContextgetResourceAsStream},但如果找不到资源,则抛出FileNotFoundException
	 * 
	 * 
	 * @see javax.servlet.ServletContext#getResourceAsStream(String)
	 */
	@Override
	public InputStream getInputStream() throws IOException {
		InputStream is = this.servletContext.getResourceAsStream(this.path);
		if (is == null) {
			throw new FileNotFoundException("Could not open " + getDescription());
		}
		return is;
	}

	/**
	 * This implementation delegates to {@code ServletContext.getResource},
	 * but throws a FileNotFoundException if no resource found.
	 * <p>
	 *  这个实现委托给{@code ServletContextgetResource},但如果没有找到资源,则抛出FileNotFoundException
	 * 
	 * 
	 * @see javax.servlet.ServletContext#getResource(String)
	 */
	@Override
	public URL getURL() throws IOException {
		URL url = this.servletContext.getResource(this.path);
		if (url == null) {
			throw new FileNotFoundException(
					getDescription() + " cannot be resolved to URL because it does not exist");
		}
		return url;
	}

	/**
	 * This implementation resolves "file:" URLs or alternatively delegates to
	 * {@code ServletContext.getRealPath}, throwing a FileNotFoundException
	 * if not found or not resolvable.
	 * <p>
	 *  此实现解决了"file："URL或者代理到{@code ServletContextgetRealPath},抛出FileNotFoundException,如果没有找到或不可解析
	 * 
	 * 
	 * @see javax.servlet.ServletContext#getResource(String)
	 * @see javax.servlet.ServletContext#getRealPath(String)
	 */
	@Override
	public File getFile() throws IOException {
		URL url = this.servletContext.getResource(this.path);
		if (url != null && ResourceUtils.isFileURL(url)) {
			// Proceed with file system resolution...
			return super.getFile();
		}
		else {
			String realPath = WebUtils.getRealPath(this.servletContext, this.path);
			return new File(realPath);
		}
	}

	/**
	 * This implementation creates a ServletContextResource, applying the given path
	 * relative to the path of the underlying file of this resource descriptor.
	 * <p>
	 *  此实现创建一个ServletContextResource,应用相对于此资源描述符的底层文件的路径的给定路径
	 * 
	 * 
	 * @see org.springframework.util.StringUtils#applyRelativePath(String, String)
	 */
	@Override
	public Resource createRelative(String relativePath) {
		String pathToUse = StringUtils.applyRelativePath(this.path, relativePath);
		return new ServletContextResource(this.servletContext, pathToUse);
	}

	/**
	 * This implementation returns the name of the file that this ServletContext
	 * resource refers to.
	 * <p>
	 *  此实现返回此ServletContext资源引用的文件的名称
	 * 
	 * 
	 * @see org.springframework.util.StringUtils#getFilename(String)
	 */
	@Override
	public String getFilename() {
		return StringUtils.getFilename(this.path);
	}

	/**
	 * This implementation returns a description that includes the ServletContext
	 * resource location.
	 * <p>
	 *  此实现返回包含ServletContext资源位置的描述
	 * 
	 */
	@Override
	public String getDescription() {
		return "ServletContext resource [" + this.path + "]";
	}

	@Override
	public String getPathWithinContext() {
		return this.path;
	}


	/**
	 * This implementation compares the underlying ServletContext resource locations.
	 * <p>
	 * 此实现将比较基础的ServletContext资源位置
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof ServletContextResource) {
			ServletContextResource otherRes = (ServletContextResource) obj;
			return (this.servletContext.equals(otherRes.servletContext) && this.path.equals(otherRes.path));
		}
		return false;
	}

	/**
	 * This implementation returns the hash code of the underlying
	 * ServletContext resource location.
	 * <p>
	 *  此实现返回底层ServletContext资源位置的哈希码
	 */
	@Override
	public int hashCode() {
		return this.path.hashCode();
	}

}
