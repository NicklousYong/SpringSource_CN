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
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.lang.UsesJava7;
import org.springframework.util.Assert;

/**
 * {@link Resource} implementation for {@code java.nio.file.Path} handles.
 * Supports resolution as File, and also as URL.
 * Implements the extended {@link WritableResource} interface.
 *
 * <p>
 *  {@code javaniofilePath}的{@link资源}实现支持以文件形式支持解析,也可以以URL形式实现扩展{@link WritableResource}界面
 * 
 * 
 * @author Philippe Marschall
 * @author Juergen Hoeller
 * @since 4.0
 * @see java.nio.file.Path
 */
@UsesJava7
public class PathResource extends AbstractResource implements WritableResource {

	private final Path path;


	/**
	 * Create a new PathResource from a Path handle.
	 * <p>Note: Unlike {@link FileSystemResource}, when building relative resources
	 * via {@link #createRelative}, the relative path will be built <i>underneath</i>
	 * the given root:
	 * e.g. Paths.get("C:/dir1/"), relative path "dir2" -> "C:/dir1/dir2"!
	 * <p>
	 * 注意：与{@link FileSystemResource}不同,当通过{@link #createRelative}构建相对资源时,相对路径将在</i>下创建给定根：例如Pathsget("C：/ d
	 * ir1 /"),相对路径"dir2" - >"C：/ dir1 / dir2"！。
	 * 
	 * 
	 * @param path a Path handle
	 */
	public PathResource(Path path) {
		Assert.notNull(path, "Path must not be null");
		this.path = path.normalize();
	}

	/**
	 * Create a new PathResource from a Path handle.
	 * <p>Note: Unlike {@link FileSystemResource}, when building relative resources
	 * via {@link #createRelative}, the relative path will be built <i>underneath</i>
	 * the given root:
	 * e.g. Paths.get("C:/dir1/"), relative path "dir2" -> "C:/dir1/dir2"!
	 * <p>
	 *  注意：与{@link FileSystemResource}不同,当通过{@link #createRelative}构建相对资源时,相对路径将在</i>下创建给定根：比如Pathsget("C：/ 
	 * dir1 /"),相对路径"dir2" - >"C：/ dir1 / dir2"！。
	 * 
	 * 
	 * @param path a path
	 * @see java.nio.file.Paths#get(String, String...)
	 */
	public PathResource(String path) {
		Assert.notNull(path, "Path must not be null");
		this.path = Paths.get(path).normalize();
	}

	/**
	 * Create a new PathResource from a Path handle.
	 * <p>Note: Unlike {@link FileSystemResource}, when building relative resources
	 * via {@link #createRelative}, the relative path will be built <i>underneath</i>
	 * the given root:
	 * e.g. Paths.get("C:/dir1/"), relative path "dir2" -> "C:/dir1/dir2"!
	 * <p>
	 * 注意：与{@link FileSystemResource}不同,当通过{@link #createRelative}构建相对资源时,相对路径将在</i>下创建给定根：例如Pathsget("C：/ d
	 * ir1 /"),相对路径"dir2" - >"C：/ dir1 / dir2"！。
	 * 
	 * 
	 * @see java.nio.file.Paths#get(URI)
	 * @param uri a path URI
	 */
	public PathResource(URI uri) {
		Assert.notNull(uri, "URI must not be null");
		this.path = Paths.get(uri).normalize();
	}


	/**
	 * Return the file path for this resource.
	 * <p>
	 *  返回此资源的文件路径
	 * 
	 */
	public final String getPath() {
		return this.path.toString();
	}

	/**
	 * This implementation returns whether the underlying file exists.
	 * <p>
	 *  此实现返回底层文件是否存在
	 * 
	 * 
	 * @see org.springframework.core.io.PathResource#exists()
	 */
	@Override
	public boolean exists() {
		return Files.exists(this.path);
	}

	/**
	 * This implementation checks whether the underlying file is marked as readable
	 * (and corresponds to an actual file with content, not to a directory).
	 * <p>
	 *  此实现检查底层文件是否被标记为可读(并且对应于具有内容的实际文件,而不是目录)
	 * 
	 * 
	 * @see java.nio.file.Files#isReadable(Path)
	 * @see java.nio.file.Files#isDirectory(Path, java.nio.file.LinkOption...)
	 */
	@Override
	public boolean isReadable() {
		return (Files.isReadable(this.path) && !Files.isDirectory(this.path));
	}

	/**
	 * This implementation opens a InputStream for the underlying file.
	 * <p>
	 *  此实现为底层文件打开一个InputStream
	 * 
	 * 
	 * @see java.nio.file.spi.FileSystemProvider#newInputStream(Path, OpenOption...)
	 */
	@Override
	public InputStream getInputStream() throws IOException {
		if (!exists()) {
			throw new FileNotFoundException(getPath() + " (no such file or directory)");
		}
		if (Files.isDirectory(this.path)) {
			throw new FileNotFoundException(getPath() + " (is a directory)");
		}
		return Files.newInputStream(this.path);
	}

	/**
	 * This implementation checks whether the underlying file is marked as writable
	 * (and corresponds to an actual file with content, not to a directory).
	 * <p>
	 *  该实现检查底层文件是否被标记为可写(并且对应于具有内容的实际文件,而不是目录)
	 * 
	 * 
	 * @see java.nio.file.Files#isWritable(Path)
	 * @see java.nio.file.Files#isDirectory(Path, java.nio.file.LinkOption...)
	 */
	@Override
	public boolean isWritable() {
		return (Files.isWritable(this.path) && !Files.isDirectory(this.path));
	}

	/**
	 * This implementation opens a OutputStream for the underlying file.
	 * <p>
	 * 此实现为底层文件打开一个OutputStream
	 * 
	 * 
	 * @see java.nio.file.spi.FileSystemProvider#newOutputStream(Path, OpenOption...)
	 */
	@Override
	public OutputStream getOutputStream() throws IOException {
		if (Files.isDirectory(this.path)) {
			throw new FileNotFoundException(getPath() + " (is a directory)");
		}
		return Files.newOutputStream(this.path);
	}

	/**
	 * This implementation returns a URL for the underlying file.
	 * <p>
	 *  此实现返回底层文件的URL
	 * 
	 * 
	 * @see java.nio.file.Path#toUri()
	 * @see java.net.URI#toURL()
	 */
	@Override
	public URL getURL() throws IOException {
		return this.path.toUri().toURL();
	}

	/**
	 * This implementation returns a URI for the underlying file.
	 * <p>
	 *  此实现返回底层文件的URI
	 * 
	 * 
	 * @see java.nio.file.Path#toUri()
	 */
	@Override
	public URI getURI() throws IOException {
		return this.path.toUri();
	}

	/**
	 * This implementation returns the underlying File reference.
	 * <p>
	 *  此实现返回底层文件引用
	 * 
	 */
	@Override
	public File getFile() throws IOException {
		try {
			return this.path.toFile();
		}
		catch (UnsupportedOperationException ex) {
			// Only paths on the default file system can be converted to a File:
			// Do exception translation for cases where conversion is not possible.
			throw new FileNotFoundException(this.path + " cannot be resolved to " + "absolute file path");
		}
	}

	/**
	 * This implementation returns the underlying File's length.
	 * <p>
	 *  此实现返回底层文件的长度
	 * 
	 */
	@Override
	public long contentLength() throws IOException {
		return Files.size(this.path);
	}

	/**
	 * This implementation returns the underlying File's timestamp.
	 * <p>
	 *  此实现返回底层文件的时间戳
	 * 
	 * 
	 * @see java.nio.file.Files#getLastModifiedTime(Path, java.nio.file.LinkOption...)
	 */
	@Override
	public long lastModified() throws IOException {
		// We can not use the superclass method since it uses conversion to a File and
		// only a Path on the default file system can be converted to a File...
		return Files.getLastModifiedTime(path).toMillis();
	}

	/**
	 * This implementation creates a FileResource, applying the given path
	 * relative to the path of the underlying file of this resource descriptor.
	 * <p>
	 *  此实现创建一个FileResource,应用相对于此资源描述符的底层文件的路径的给定路径
	 * 
	 * 
	 * @see java.nio.file.Path#resolve(String)
	 */
	@Override
	public Resource createRelative(String relativePath) throws IOException {
		return new PathResource(this.path.resolve(relativePath));
	}

	/**
	 * This implementation returns the name of the file.
	 * <p>
	 *  此实现返回文件的名称
	 * 
	 * 
	 * @see java.nio.file.Path#getFileName()
	 */
	@Override
	public String getFilename() {
		return this.path.getFileName().toString();
	}

	@Override
	public String getDescription() {
		return "path [" + this.path.toAbsolutePath() + "]";
	}


	/**
	 * This implementation compares the underlying Path references.
	 * <p>
	 *  此实现将比较底层Path引用
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		return (this == obj ||
			(obj instanceof PathResource && this.path.equals(((PathResource) obj).path)));
	}

	/**
	 * This implementation returns the hash code of the underlying Path reference.
	 * <p>
	 *  此实现返回底层Path引用的哈希码
	 */
	@Override
	public int hashCode() {
		return this.path.hashCode();
	}

}
