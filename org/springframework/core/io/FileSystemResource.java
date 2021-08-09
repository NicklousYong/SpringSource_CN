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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * {@link Resource} implementation for {@code java.io.File} handles.
 * Supports resolution as a {@code File} and also as a {@code URL}.
 * Implements the extended {@link WritableResource} interface.
 *
 * <p>
 *  {@code javaioFile}的{@link资源}实现处理支持将解析为{@code文件},也作为{@code URL}实现扩展{@link WritableResource}界面
 * 
 * 
 * @author Juergen Hoeller
 * @since 28.12.2003
 * @see java.io.File
 */
public class FileSystemResource extends AbstractResource implements WritableResource {

	private final File file;

	private final String path;


	/**
	 * Create a new {@code FileSystemResource} from a {@link File} handle.
	 * <p>Note: When building relative resources via {@link #createRelative},
	 * the relative path will apply <i>at the same directory level</i>:
	 * e.g. new File("C:/dir1"), relative path "dir2" -> "C:/dir2"!
	 * If you prefer to have relative paths built underneath the given root
	 * directory, use the {@link #FileSystemResource(String) constructor with a file path}
	 * to append a trailing slash to the root path: "C:/dir1/", which
	 * indicates this directory as root for all relative paths.
	 * <p>
	 * 从{@link File}句柄创建新的{@code FileSystemResource} <p>注意：通过{@link #createRelative}构建相对资源时,相对路径将在相同的目录级别<i>
	 * 应用<i> ：例如new File("C：/ dir1"),相对路径"dir2" - >"C：/ dir2"！如果您希望在给定的根目录下建立相对路径,请使用带有文件路径的{@link #FileSystemResource(String)构造函数将附加斜杠添加到根路径："C：/ dir1 /",表示该目录作为所有相对路径的根目录。
	 * 
	 * 
	 * @param file a File handle
	 */
	public FileSystemResource(File file) {
		Assert.notNull(file, "File must not be null");
		this.file = file;
		this.path = StringUtils.cleanPath(file.getPath());
	}

	/**
	 * Create a new {@code FileSystemResource} from a file path.
	 * <p>Note: When building relative resources via {@link #createRelative},
	 * it makes a difference whether the specified resource base path here
	 * ends with a slash or not. In the case of "C:/dir1/", relative paths
	 * will be built underneath that root: e.g. relative path "dir2" ->
	 * "C:/dir1/dir2". In the case of "C:/dir1", relative paths will apply
	 * at the same directory level: relative path "dir2" -> "C:/dir2".
	 * <p>
	 * 从文件路径创建一个新的{@code FileSystemResource} <p>注意：通过{@link #createRelative}构建相对资源时,它是否指定资源基本路径是否以斜杠结尾在某种情况下
	 * "C：/ dir1 /",相对路径。
	 * will be built underneath that root: e.g. relative path "dir2" ->
	 *  "C：/ dir1 / dir2"在"C：/ dir1"的情况下,相对路径将应用于相同的目录级别：相对路径"dir2" - >"C：/ dir2"
	 * 
	 * 
	 * @param path a file path
	 */
	public FileSystemResource(String path) {
		Assert.notNull(path, "Path must not be null");
		this.file = new File(path);
		this.path = StringUtils.cleanPath(path);
	}


	/**
	 * Return the file path for this resource.
	 * <p>
	 *  返回此资源的文件路径
	 * 
	 */
	public final String getPath() {
		return this.path;
	}

	/**
	 * This implementation returns whether the underlying file exists.
	 * <p>
	 *  此实现返回底层文件是否存在
	 * 
	 * 
	 * @see java.io.File#exists()
	 */
	@Override
	public boolean exists() {
		return this.file.exists();
	}

	/**
	 * This implementation checks whether the underlying file is marked as readable
	 * (and corresponds to an actual file with content, not to a directory).
	 * <p>
	 *  此实现检查底层文件是否被标记为可读(并且对应于具有内容的实际文件,而不是目录)
	 * 
	 * 
	 * @see java.io.File#canRead()
	 * @see java.io.File#isDirectory()
	 */
	@Override
	public boolean isReadable() {
		return (this.file.canRead() && !this.file.isDirectory());
	}

	/**
	 * This implementation opens a FileInputStream for the underlying file.
	 * <p>
	 *  此实现为底层文件打开FileInputStream
	 * 
	 * 
	 * @see java.io.FileInputStream
	 */
	@Override
	public InputStream getInputStream() throws IOException {
		return new FileInputStream(this.file);
	}

	/**
	 * This implementation checks whether the underlying file is marked as writable
	 * (and corresponds to an actual file with content, not to a directory).
	 * <p>
	 * 该实现检查底层文件是否被标记为可写(并且对应于具有内容的实际文件,而不是目录)
	 * 
	 * 
	 * @see java.io.File#canWrite()
	 * @see java.io.File#isDirectory()
	 */
	@Override
	public boolean isWritable() {
		return (this.file.canWrite() && !this.file.isDirectory());
	}

	/**
	 * This implementation opens a FileOutputStream for the underlying file.
	 * <p>
	 *  此实现为底层文件打开FileOutputStream
	 * 
	 * 
	 * @see java.io.FileOutputStream
	 */
	@Override
	public OutputStream getOutputStream() throws IOException {
		return new FileOutputStream(this.file);
	}

	/**
	 * This implementation returns a URL for the underlying file.
	 * <p>
	 *  此实现返回底层文件的URL
	 * 
	 * 
	 * @see java.io.File#toURI()
	 */
	@Override
	public URL getURL() throws IOException {
		return this.file.toURI().toURL();
	}

	/**
	 * This implementation returns a URI for the underlying file.
	 * <p>
	 *  此实现返回底层文件的URI
	 * 
	 * 
	 * @see java.io.File#toURI()
	 */
	@Override
	public URI getURI() throws IOException {
		return this.file.toURI();
	}

	/**
	 * This implementation returns the underlying File reference.
	 * <p>
	 *  此实现返回底层文件引用
	 * 
	 */
	@Override
	public File getFile() {
		return this.file;
	}

	/**
	 * This implementation returns the underlying File's length.
	 * <p>
	 *  此实现返回底层文件的长度
	 * 
	 */
	@Override
	public long contentLength() throws IOException {
		return this.file.length();
	}

	/**
	 * This implementation creates a FileSystemResource, applying the given path
	 * relative to the path of the underlying file of this resource descriptor.
	 * <p>
	 *  此实现创建一个FileSystemResource,应用相对于此资源描述符的底层文件的路径的给定路径
	 * 
	 * 
	 * @see org.springframework.util.StringUtils#applyRelativePath(String, String)
	 */
	@Override
	public Resource createRelative(String relativePath) {
		String pathToUse = StringUtils.applyRelativePath(this.path, relativePath);
		return new FileSystemResource(pathToUse);
	}

	/**
	 * This implementation returns the name of the file.
	 * <p>
	 *  此实现返回文件的名称
	 * 
	 * 
	 * @see java.io.File#getName()
	 */
	@Override
	public String getFilename() {
		return this.file.getName();
	}

	/**
	 * This implementation returns a description that includes the absolute
	 * path of the file.
	 * <p>
	 *  此实现返回包含文件绝对路径的描述
	 * 
	 * 
	 * @see java.io.File#getAbsolutePath()
	 */
	@Override
	public String getDescription() {
		return "file [" + this.file.getAbsolutePath() + "]";
	}


	/**
	 * This implementation compares the underlying File references.
	 * <p>
	 * 此实现将比较底层文件引用
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		return (obj == this ||
			(obj instanceof FileSystemResource && this.path.equals(((FileSystemResource) obj).path)));
	}

	/**
	 * This implementation returns the hash code of the underlying File reference.
	 * <p>
	 *  此实现返回底层File引用的哈希码
	 */
	@Override
	public int hashCode() {
		return this.path.hashCode();
	}

}
