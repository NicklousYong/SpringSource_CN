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
import java.net.URI;
import java.net.URL;

/**
 * Interface for a resource descriptor that abstracts from the actual
 * type of underlying resource, such as a file or class path resource.
 *
 * <p>An InputStream can be opened for every resource if it exists in
 * physical form, but a URL or File handle can just be returned for
 * certain resources. The actual behavior is implementation-specific.
 *
 * <p>
 *  用于从基础资源的实际类型(例如文件或类路径资源)抽象的资源描述符的接口
 * 
 * <p>如果物理形式存在,则可以为每个资源打开InputStream,但是可以为特定资源返回URL或文件句柄实际行为是实现特定的
 * 
 * 
 * @author Juergen Hoeller
 * @since 28.12.2003
 * @see #getInputStream()
 * @see #getURL()
 * @see #getURI()
 * @see #getFile()
 * @see WritableResource
 * @see ContextResource
 * @see UrlResource
 * @see ClassPathResource
 * @see FileSystemResource
 * @see PathResource
 * @see ByteArrayResource
 * @see InputStreamResource
 */
public interface Resource extends InputStreamSource {

	/**
	 * Determine whether this resource actually exists in physical form.
	 * <p>This method performs a definitive existence check, whereas the
	 * existence of a {@code Resource} handle only guarantees a valid
	 * descriptor handle.
	 * <p>
	 *  确定此资源是否实际存在于物理形式<p>此方法执行确定的存在检查,而{@code资源}句柄的存在仅保证有效的描述符句柄
	 * 
	 */
	boolean exists();

	/**
	 * Indicate whether the contents of this resource can be read via
	 * {@link #getInputStream()}.
	 * <p>Will be {@code true} for typical resource descriptors;
	 * note that actual content reading may still fail when attempted.
	 * However, a value of {@code false} is a definitive indication
	 * that the resource content cannot be read.
	 * <p>
	 *  指示此资源的内容是否可以通过{@link #getInputStream()}读取<p>典型资源描述符将为{@code true};请注意,尝试实际内容读取可能仍然失败但是,值{@code false}
	 * 是资源内容无法读取的明确指示。
	 * 
	 * 
	 * @see #getInputStream()
	 */
	boolean isReadable();

	/**
	 * Indicate whether this resource represents a handle with an open stream.
	 * If {@code true}, the InputStream cannot be read multiple times,
	 * and must be read and closed to avoid resource leaks.
	 * <p>Will be {@code false} for typical resource descriptors.
	 * <p>
	 * 指示此资源是否表示具有开放流的句柄如果{@code true},则InputStream不能被多次读取,并且必须读取和关闭以避免资源泄露<p>典型资源描述符将为{@code false}
	 * 
	 */
	boolean isOpen();

	/**
	 * Return a URL handle for this resource.
	 * <p>
	 *  返回此资源的URL句柄
	 * 
	 * 
	 * @throws IOException if the resource cannot be resolved as URL,
	 * i.e. if the resource is not available as descriptor
	 */
	URL getURL() throws IOException;

	/**
	 * Return a URI handle for this resource.
	 * <p>
	 *  返回此资源的URI句柄
	 * 
	 * 
	 * @throws IOException if the resource cannot be resolved as URI,
	 * i.e. if the resource is not available as descriptor
	 * @since 2.5
	 */
	URI getURI() throws IOException;

	/**
	 * Return a File handle for this resource.
	 * <p>
	 *  返回此资源的文件句柄
	 * 
	 * 
	 * @throws IOException if the resource cannot be resolved as absolute
	 * file path, i.e. if the resource is not available in a file system
	 */
	File getFile() throws IOException;

	/**
	 * Determine the content length for this resource.
	 * <p>
	 *  确定此资源的内容长度
	 * 
	 * 
	 * @throws IOException if the resource cannot be resolved
	 * (in the file system or as some other known physical resource type)
	 */
	long contentLength() throws IOException;

	/**
	 * Determine the last-modified timestamp for this resource.
	 * <p>
	 *  确定此资源的最后修改的时间戳
	 * 
	 * 
	 * @throws IOException if the resource cannot be resolved
	 * (in the file system or as some other known physical resource type)
	 */
	long lastModified() throws IOException;

	/**
	 * Create a resource relative to this resource.
	 * <p>
	 *  创建一个相对于此资源的资源
	 * 
	 * 
	 * @param relativePath the relative path (relative to this resource)
	 * @return the resource handle for the relative resource
	 * @throws IOException if the relative resource cannot be determined
	 */
	Resource createRelative(String relativePath) throws IOException;

	/**
	 * Determine a filename for this resource, i.e. typically the last
	 * part of the path: for example, "myfile.txt".
	 * <p>Returns {@code null} if this type of resource does not
	 * have a filename.
	 * <p>
	 *  确定此资源的文件名,即通常是路径的最后一部分：例如,"myfiletxt"<p>如果此类资源没有文件名,则返回{@code null}
	 * 
	 */
	String getFilename();

	/**
	 * Return a description for this resource,
	 * to be used for error output when working with the resource.
	 * <p>Implementations are also encouraged to return this value
	 * from their {@code toString} method.
	 * <p>
	 * 返回此资源的描述,用于处理资源时的错误输出<p>还鼓励实现从{@code toString}方法返回此值
	 * 
	 * @see Object#toString()
	 */
	String getDescription();

}
