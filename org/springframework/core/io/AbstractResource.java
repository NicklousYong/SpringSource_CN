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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.springframework.core.NestedIOException;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;

/**
 * Convenience base class for {@link Resource} implementations,
 * pre-implementing typical behavior.
 *
 * <p>The "exists" method will check whether a File or InputStream can
 * be opened; "isOpen" will always return false; "getURL" and "getFile"
 * throw an exception; and "toString" will return the description.
 *
 * <p>
 *  {@link Resource}实现的便利基础类,预先实现典型行为
 * 
 * <p>"存在"方法将检查是否可以打开File或InputStream; "isOpen"将永远返回false; "getURL"和"getFile"抛出异常; "toString"将返回描述
 * 
 * 
 * @author Juergen Hoeller
 * @since 28.12.2003
 */
public abstract class AbstractResource implements Resource {

	/**
	 * This implementation checks whether a File can be opened,
	 * falling back to whether an InputStream can be opened.
	 * This will cover both directories and content resources.
	 * <p>
	 *  此实现检查是否可以打开文件,回溯到是否可以打开InputStream。这将覆盖目录和内容资源
	 * 
	 */
	@Override
	public boolean exists() {
		// Try file existence: can we find the file in the file system?
		try {
			return getFile().exists();
		}
		catch (IOException ex) {
			// Fall back to stream existence: can we open the stream?
			try {
				InputStream is = getInputStream();
				is.close();
				return true;
			}
			catch (Throwable isEx) {
				return false;
			}
		}
	}

	/**
	 * This implementation always returns {@code true}.
	 * <p>
	 *  这个实现总是返回{@code true}
	 * 
	 */
	@Override
	public boolean isReadable() {
		return true;
	}

	/**
	 * This implementation always returns {@code false}.
	 * <p>
	 *  这个实现总是返回{@code false}
	 * 
	 */
	@Override
	public boolean isOpen() {
		return false;
	}

	/**
	 * This implementation throws a FileNotFoundException, assuming
	 * that the resource cannot be resolved to a URL.
	 * <p>
	 *  该实现会抛出FileNotFoundException,假设资源无法解析为URL
	 * 
	 */
	@Override
	public URL getURL() throws IOException {
		throw new FileNotFoundException(getDescription() + " cannot be resolved to URL");
	}

	/**
	 * This implementation builds a URI based on the URL returned
	 * by {@link #getURL()}.
	 * <p>
	 *  此实现根据{@link #getURL()}返回的URL构建一个URI
	 * 
	 */
	@Override
	public URI getURI() throws IOException {
		URL url = getURL();
		try {
			return ResourceUtils.toURI(url);
		}
		catch (URISyntaxException ex) {
			throw new NestedIOException("Invalid URI [" + url + "]", ex);
		}
	}

	/**
	 * This implementation throws a FileNotFoundException, assuming
	 * that the resource cannot be resolved to an absolute file path.
	 * <p>
	 *  该实现会抛出FileNotFoundException,假设资源无法解析为绝对文件路径
	 * 
	 */
	@Override
	public File getFile() throws IOException {
		throw new FileNotFoundException(getDescription() + " cannot be resolved to absolute file path");
	}

	/**
	 * This implementation reads the entire InputStream to calculate the
	 * content length. Subclasses will almost always be able to provide
	 * a more optimal version of this, e.g. checking a File length.
	 * <p>
	 * 该实现读取整个InputStream来计算内容长度子类将几乎总是能够提供更优化的版本,例如检查文件长度
	 * 
	 * 
	 * @see #getInputStream()
	 * @throws IllegalStateException if {@link #getInputStream()} returns null.
	 */
	@Override
	public long contentLength() throws IOException {
		InputStream is = getInputStream();
		Assert.state(is != null, "Resource InputStream must not be null");
		try {
			long size = 0;
			byte[] buf = new byte[255];
			int read;
			while ((read = is.read(buf)) != -1) {
				size += read;
			}
			return size;
		}
		finally {
			try {
				is.close();
			}
			catch (IOException ex) {
			}
		}
	}

	/**
	 * This implementation checks the timestamp of the underlying File,
	 * if available.
	 * <p>
	 *  此实现检查底层文件的时间戳(如果可用)
	 * 
	 * 
	 * @see #getFileForLastModifiedCheck()
	 */
	@Override
	public long lastModified() throws IOException {
		long lastModified = getFileForLastModifiedCheck().lastModified();
		if (lastModified == 0L) {
			throw new FileNotFoundException(getDescription() +
					" cannot be resolved in the file system for resolving its last-modified timestamp");
		}
		return lastModified;
	}

	/**
	 * Determine the File to use for timestamp checking.
	 * <p>The default implementation delegates to {@link #getFile()}.
	 * <p>
	 *  确定要用于时间戳检查的文件<p>默认实现委托{@link #getFile()}
	 * 
	 * 
	 * @return the File to use for timestamp checking (never {@code null})
	 * @throws IOException if the resource cannot be resolved as absolute
	 * file path, i.e. if the resource is not available in a file system
	 */
	protected File getFileForLastModifiedCheck() throws IOException {
		return getFile();
	}

	/**
	 * This implementation throws a FileNotFoundException, assuming
	 * that relative resources cannot be created for this resource.
	 * <p>
	 *  此实现会抛出FileNotFoundException,假设无法为此资源创建相对资源
	 * 
	 */
	@Override
	public Resource createRelative(String relativePath) throws IOException {
		throw new FileNotFoundException("Cannot create a relative resource for " + getDescription());
	}

	/**
	 * This implementation always returns {@code null},
	 * assuming that this resource type does not have a filename.
	 * <p>
	 *  这个实现总是返回{@code null},假设这个资源类型没有文件名
	 * 
	 */
	@Override
	public String getFilename() {
		return null;
	}


	/**
	 * This implementation returns the description of this resource.
	 * <p>
	 *  此实现返回此资源的描述
	 * 
	 * 
	 * @see #getDescription()
	 */
	@Override
	public String toString() {
		return getDescription();
	}

	/**
	 * This implementation compares description strings.
	 * <p>
	 *  此实现将比较描述字符串
	 * 
	 * 
	 * @see #getDescription()
	 */
	@Override
	public boolean equals(Object obj) {
		return (obj == this ||
			(obj instanceof Resource && ((Resource) obj).getDescription().equals(getDescription())));
	}

	/**
	 * This implementation returns the description's hash code.
	 * <p>
	 *  此实现返回描述的哈希码
	 * 
	 * @see #getDescription()
	 */
	@Override
	public int hashCode() {
		return getDescription().hashCode();
	}

}
