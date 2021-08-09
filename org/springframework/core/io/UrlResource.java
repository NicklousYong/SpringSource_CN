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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

/**
 * {@link Resource} implementation for {@code java.net.URL} locators.
 * Supports resolution as a {@code URL} and also as a {@code File} in
 * case of the {@code "file:"} protocol.
 *
 * <p>
 *  {@code javanetURL}定位器的{@link资源}实现支持{@code URL}的解析,并且{@code文件："}协议支持{@code File}
 * 
 * 
 * @author Juergen Hoeller
 * @since 28.12.2003
 * @see java.net.URL
 */
public class UrlResource extends AbstractFileResolvingResource {

	/**
	 * Original URI, if available; used for URI and File access.
	 * <p>
	 * 原始URI,如果可用;用于URI和文件访问
	 * 
	 */
	private final URI uri;

	/**
	 * Original URL, used for actual access.
	 * <p>
	 *  原始URL,用于实际访问
	 * 
	 */
	private final URL url;

	/**
	 * Cleaned URL (with normalized path), used for comparisons.
	 * <p>
	 *  已清理的URL(具有标准化路径),用于比较
	 * 
	 */
	private final URL cleanedUrl;


	/**
	 * Create a new {@code UrlResource} based on the given URI object.
	 * <p>
	 *  基于给定的URI对象创建一个新的{@code UrlResource}
	 * 
	 * 
	 * @param uri a URI
	 * @throws MalformedURLException if the given URL path is not valid
	 * @since 2.5
	 */
	public UrlResource(URI uri) throws MalformedURLException {
		Assert.notNull(uri, "URI must not be null");
		this.uri = uri;
		this.url = uri.toURL();
		this.cleanedUrl = getCleanedUrl(this.url, uri.toString());
	}

	/**
	 * Create a new {@code UrlResource} based on the given URL object.
	 * <p>
	 *  根据给定的URL对象创建一个新的{@code UrlResource}
	 * 
	 * 
	 * @param url a URL
	 */
	public UrlResource(URL url) {
		Assert.notNull(url, "URL must not be null");
		this.url = url;
		this.cleanedUrl = getCleanedUrl(this.url, url.toString());
		this.uri = null;
	}

	/**
	 * Create a new {@code UrlResource} based on a URL path.
	 * <p>Note: The given path needs to be pre-encoded if necessary.
	 * <p>
	 *  根据URL路径创建一个新的{@code UrlResource} <p>注意：如果需要,给定路径需要进行预编码
	 * 
	 * 
	 * @param path a URL path
	 * @throws MalformedURLException if the given URL path is not valid
	 * @see java.net.URL#URL(String)
	 */
	public UrlResource(String path) throws MalformedURLException {
		Assert.notNull(path, "Path must not be null");
		this.uri = null;
		this.url = new URL(path);
		this.cleanedUrl = getCleanedUrl(this.url, path);
	}

	/**
	 * Create a new {@code UrlResource} based on a URI specification.
	 * <p>The given parts will automatically get encoded if necessary.
	 * <p>
	 *  根据URI规范创建新的{@code UrlResource} <p>如有必要,给定的部分将自动编码
	 * 
	 * 
	 * @param protocol the URL protocol to use (e.g. "jar" or "file" - without colon);
	 * also known as "scheme"
	 * @param location the location (e.g. the file path within that protocol);
	 * also known as "scheme-specific part"
	 * @throws MalformedURLException if the given URL specification is not valid
	 * @see java.net.URI#URI(String, String, String)
	 */
	public UrlResource(String protocol, String location) throws MalformedURLException  {
		this(protocol, location, null);
	}

	/**
	 * Create a new {@code UrlResource} based on a URI specification.
	 * <p>The given parts will automatically get encoded if necessary.
	 * <p>
	 *  根据URI规范创建新的{@code UrlResource} <p>如有必要,给定的部分将自动编码
	 * 
	 * 
	 * @param protocol the URL protocol to use (e.g. "jar" or "file" - without colon);
	 * also known as "scheme"
	 * @param location the location (e.g. the file path within that protocol);
	 * also known as "scheme-specific part"
	 * @param fragment the fragment within that location (e.g. anchor on an HTML page,
	 * as following after a "#" separator)
	 * @throws MalformedURLException if the given URL specification is not valid
	 * @see java.net.URI#URI(String, String, String)
	 */
	public UrlResource(String protocol, String location, String fragment) throws MalformedURLException  {
		try {
			this.uri = new URI(protocol, location, fragment);
			this.url = this.uri.toURL();
			this.cleanedUrl = getCleanedUrl(this.url, this.uri.toString());
		}
		catch (URISyntaxException ex) {
			MalformedURLException exToThrow = new MalformedURLException(ex.getMessage());
			exToThrow.initCause(ex);
			throw exToThrow;
		}
	}


	/**
	 * Determine a cleaned URL for the given original URL.
	 * <p>
	 *  确定给定原始URL的已清除URL
	 * 
	 * 
	 * @param originalUrl the original URL
	 * @param originalPath the original URL path
	 * @return the cleaned URL
	 * @see org.springframework.util.StringUtils#cleanPath
	 */
	private URL getCleanedUrl(URL originalUrl, String originalPath) {
		try {
			return new URL(StringUtils.cleanPath(originalPath));
		}
		catch (MalformedURLException ex) {
			// Cleaned URL path cannot be converted to URL
			// -> take original URL.
			return originalUrl;
		}
	}

	/**
	 * This implementation opens an InputStream for the given URL.
	 * <p>It sets the {@code useCaches} flag to {@code false},
	 * mainly to avoid jar file locking on Windows.
	 * <p>
	 * 此实现为给定的URL打开一个InputStream <p>它将{@code useCaches}标志设置为{@code false},主要是为了避免Windows上的jar文件锁定
	 * 
	 * 
	 * @see java.net.URL#openConnection()
	 * @see java.net.URLConnection#setUseCaches(boolean)
	 * @see java.net.URLConnection#getInputStream()
	 */
	@Override
	public InputStream getInputStream() throws IOException {
		URLConnection con = this.url.openConnection();
		ResourceUtils.useCachesIfNecessary(con);
		try {
			return con.getInputStream();
		}
		catch (IOException ex) {
			// Close the HTTP connection (if applicable).
			if (con instanceof HttpURLConnection) {
				((HttpURLConnection) con).disconnect();
			}
			throw ex;
		}
	}

	/**
	 * This implementation returns the underlying URL reference.
	 * <p>
	 *  此实现返回底层URL引用
	 * 
	 */
	@Override
	public URL getURL() throws IOException {
		return this.url;
	}

	/**
	 * This implementation returns the underlying URI directly,
	 * if possible.
	 * <p>
	 *  如果可能,此实现将直接返回底层URI
	 * 
	 */
	@Override
	public URI getURI() throws IOException {
		if (this.uri != null) {
			return this.uri;
		}
		else {
			return super.getURI();
		}
	}

	/**
	 * This implementation returns a File reference for the underlying URL/URI,
	 * provided that it refers to a file in the file system.
	 * <p>
	 *  此实现返回底层URL / URI的文件引用,前提是它引用文件系统中的文件
	 * 
	 * 
	 * @see org.springframework.util.ResourceUtils#getFile(java.net.URL, String)
	 */
	@Override
	public File getFile() throws IOException {
		if (this.uri != null) {
			return super.getFile(this.uri);
		}
		else {
			return super.getFile();
		}
	}

	/**
	 * This implementation creates a {@code UrlResource}, applying the given path
	 * relative to the path of the underlying URL of this resource descriptor.
	 * <p>
	 *  此实现创建一个{@code UrlResource},应用相对于此资源描述符的底层URL的路径的给定路径
	 * 
	 * 
	 * @see java.net.URL#URL(java.net.URL, String)
	 */
	@Override
	public Resource createRelative(String relativePath) throws MalformedURLException {
		if (relativePath.startsWith("/")) {
			relativePath = relativePath.substring(1);
		}
		return new UrlResource(new URL(this.url, relativePath));
	}

	/**
	 * This implementation returns the name of the file that this URL refers to.
	 * <p>
	 *  此实现返回此URL引用的文件的名称
	 * 
	 * 
	 * @see java.net.URL#getFile()
	 * @see java.io.File#getName()
	 */
	@Override
	public String getFilename() {
		return new File(this.url.getFile()).getName();
	}

	/**
	 * This implementation returns a description that includes the URL.
	 * <p>
	 *  此实现返回包含该URL的描述
	 * 
	 */
	@Override
	public String getDescription() {
		return "URL [" + this.url + "]";
	}


	/**
	 * This implementation compares the underlying URL references.
	 * <p>
	 *  此实现将比较底层URL引用
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		return (obj == this ||
			(obj instanceof UrlResource && this.cleanedUrl.equals(((UrlResource) obj).cleanedUrl)));
	}

	/**
	 * This implementation returns the hash code of the underlying URL reference.
	 * <p>
	 * 此实现返回底层URL引用的哈希码
	 */
	@Override
	public int hashCode() {
		return this.cleanedUrl.hashCode();
	}

}
