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

package org.springframework.core.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import org.springframework.util.ResourceUtils;

/**
 * Abstract base class for resources which resolve URLs into File references,
 * such as {@link UrlResource} or {@link ClassPathResource}.
 *
 * <p>Detects the "file" protocol as well as the JBoss "vfs" protocol in URLs,
 * resolving file system references accordingly.
 *
 * <p>
 *  将URL解析为文件引用的资源的抽象基类,例如{@link UrlResource}或{@link ClassPathResource}
 * 
 * <p>在URL中检测"文件"协议以及JBoss"vfs"协议,相应地解析文件系统引用
 * 
 * 
 * @author Juergen Hoeller
 * @since 3.0
 */
public abstract class AbstractFileResolvingResource extends AbstractResource {

	/**
	 * This implementation returns a File reference for the underlying class path
	 * resource, provided that it refers to a file in the file system.
	 * <p>
	 *  此实现返回基础类路径资源的文件引用,前提是它引用文件系统中的文件
	 * 
	 * 
	 * @see org.springframework.util.ResourceUtils#getFile(java.net.URL, String)
	 */
	@Override
	public File getFile() throws IOException {
		URL url = getURL();
		if (url.getProtocol().startsWith(ResourceUtils.URL_PROTOCOL_VFS)) {
			return VfsResourceDelegate.getResource(url).getFile();
		}
		return ResourceUtils.getFile(url, getDescription());
	}

	/**
	 * This implementation determines the underlying File
	 * (or jar file, in case of a resource in a jar/zip).
	 * <p>
	 *  此实现确定底层File(或jar文件,如果是jar / zip中的资源)
	 * 
	 */
	@Override
	protected File getFileForLastModifiedCheck() throws IOException {
		URL url = getURL();
		if (ResourceUtils.isJarURL(url)) {
			URL actualUrl = ResourceUtils.extractArchiveURL(url);
			if (actualUrl.getProtocol().startsWith(ResourceUtils.URL_PROTOCOL_VFS)) {
				return VfsResourceDelegate.getResource(actualUrl).getFile();
			}
			return ResourceUtils.getFile(actualUrl, "Jar URL");
		}
		else {
			return getFile();
		}
	}

	/**
	 * This implementation returns a File reference for the given URI-identified
	 * resource, provided that it refers to a file in the file system.
	 * <p>
	 *  该实现返回给定URI标识资源的文件引用,前提是它引用文件系统中的文件
	 * 
	 * 
	 * @see org.springframework.util.ResourceUtils#getFile(java.net.URI, String)
	 */
	protected File getFile(URI uri) throws IOException {
		if (uri.getScheme().startsWith(ResourceUtils.URL_PROTOCOL_VFS)) {
			return VfsResourceDelegate.getResource(uri).getFile();
		}
		return ResourceUtils.getFile(uri, getDescription());
	}


	@Override
	public boolean exists() {
		try {
			URL url = getURL();
			if (ResourceUtils.isFileURL(url)) {
				// Proceed with file system resolution...
				return getFile().exists();
			}
			else {
				// Try a URL connection content-length header...
				URLConnection con = url.openConnection();
				customizeConnection(con);
				HttpURLConnection httpCon =
						(con instanceof HttpURLConnection ? (HttpURLConnection) con : null);
				if (httpCon != null) {
					int code = httpCon.getResponseCode();
					if (code == HttpURLConnection.HTTP_OK) {
						return true;
					}
					else if (code == HttpURLConnection.HTTP_NOT_FOUND) {
						return false;
					}
				}
				if (con.getContentLength() >= 0) {
					return true;
				}
				if (httpCon != null) {
					// no HTTP OK status, and no content-length header: give up
					httpCon.disconnect();
					return false;
				}
				else {
					// Fall back to stream existence: can we open the stream?
					InputStream is = getInputStream();
					is.close();
					return true;
				}
			}
		}
		catch (IOException ex) {
			return false;
		}
	}

	@Override
	public boolean isReadable() {
		try {
			URL url = getURL();
			if (ResourceUtils.isFileURL(url)) {
				// Proceed with file system resolution...
				File file = getFile();
				return (file.canRead() && !file.isDirectory());
			}
			else {
				return true;
			}
		}
		catch (IOException ex) {
			return false;
		}
	}

	@Override
	public long contentLength() throws IOException {
		URL url = getURL();
		if (ResourceUtils.isFileURL(url)) {
			// Proceed with file system resolution...
			return getFile().length();
		}
		else {
			// Try a URL connection content-length header...
			URLConnection con = url.openConnection();
			customizeConnection(con);
			return con.getContentLength();
		}
	}

	@Override
	public long lastModified() throws IOException {
		URL url = getURL();
		if (ResourceUtils.isFileURL(url) || ResourceUtils.isJarURL(url)) {
			// Proceed with file system resolution...
			return super.lastModified();
		}
		else {
			// Try a URL connection last-modified header...
			URLConnection con = url.openConnection();
			customizeConnection(con);
			return con.getLastModified();
		}
	}


	/**
	 * Customize the given {@link URLConnection}, obtained in the course of an
	 * {@link #exists()}, {@link #contentLength()} or {@link #lastModified()} call.
	 * <p>Calls {@link ResourceUtils#useCachesIfNecessary(URLConnection)} and
	 * delegates to {@link #customizeConnection(HttpURLConnection)} if possible.
	 * Can be overridden in subclasses.
	 * <p>
	 * 自定义在{@link #exists()},{@link #contentLength()}或{@link #lastModified())调用过程中获取的给定{@link URLConnection}
	 *  <p> Calls {@link ResourceUtils #useCachesIfNecessary(URLConnection)}并委托给{@link #customizeConnection(HttpURLConnection)}
	 * }如果可能,可以在子类中被覆盖。
	 * 
	 * 
	 * @param con the URLConnection to customize
	 * @throws IOException if thrown from URLConnection methods
	 */
	protected void customizeConnection(URLConnection con) throws IOException {
		ResourceUtils.useCachesIfNecessary(con);
		if (con instanceof HttpURLConnection) {
			customizeConnection((HttpURLConnection) con);
		}
	}

	/**
	 * Customize the given {@link HttpURLConnection}, obtained in the course of an
	 * {@link #exists()}, {@link #contentLength()} or {@link #lastModified()} call.
	 * <p>Sets request method "HEAD" by default. Can be overridden in subclasses.
	 * <p>
	 *  自定义在{@link #exists()},{@link #contentLength()}或{@link #lastModified()}调用过程中获取的给定{@link HttpURLConnection}
	 *  <p>设置请求方法"HEAD "默认可以在子类中被覆盖。
	 * 
	 * 
	 * @param con the HttpURLConnection to customize
	 * @throws IOException if thrown from HttpURLConnection methods
	 */
	protected void customizeConnection(HttpURLConnection con) throws IOException {
		con.setRequestMethod("HEAD");
	}


	/**
	 * Inner delegate class, avoiding a hard JBoss VFS API dependency at runtime.
	 * <p>
	 */
	private static class VfsResourceDelegate {

		public static Resource getResource(URL url) throws IOException {
			return new VfsResource(VfsUtils.getRoot(url));
		}

		public static Resource getResource(URI uri) throws IOException {
			return new VfsResource(VfsUtils.getRoot(uri));
		}
	}

}
