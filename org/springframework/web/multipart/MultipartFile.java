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

package org.springframework.web.multipart;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.InputStreamSource;

/**
 * A representation of an uploaded file received in a multipart request.
 *
 * <p>The file contents are either stored in memory or temporarily on disk.
 * In either case, the user is responsible for copying file contents to a
 * session-level or persistent store as and if desired. The temporary storages
 * will be cleared at the end of request processing.
 *
 * <p>
 *  在多部分请求中接收到的上传文件的表示
 * 
 * <p>文件内容存储在内存中或临时存储在磁盘上任何一种情况下,用户负责将文件内容复制到会话级别或持久性存储,如果需要,临时存储将在请求结束时被清除处理
 * 
 * 
 * @author Juergen Hoeller
 * @author Trevor D. Cook
 * @since 29.09.2003
 * @see org.springframework.web.multipart.MultipartHttpServletRequest
 * @see org.springframework.web.multipart.MultipartResolver
 */
public interface MultipartFile extends InputStreamSource {

	/**
	 * Return the name of the parameter in the multipart form.
	 * <p>
	 *  返回多部分表单中参数的名称
	 * 
	 * 
	 * @return the name of the parameter (never {@code null} or empty)
	 */
	String getName();

	/**
	 * Return the original filename in the client's filesystem.
	 * <p>This may contain path information depending on the browser used,
	 * but it typically will not with any other than Opera.
	 * <p>
	 *  返回客户端文件系统中的原始文件名<p>这可能包含根据浏览器使用的路径信息,但通常不会与Opera
	 * 
	 * 
	 * @return the original filename, or the empty String if no file
	 * has been chosen in the multipart form, or {@code null}
	 * if not defined or not available
	 */
	String getOriginalFilename();

	/**
	 * Return the content type of the file.
	 * <p>
	 *  返回文件的内容类型
	 * 
	 * 
	 * @return the content type, or {@code null} if not defined
	 * (or no file has been chosen in the multipart form)
	 */
	String getContentType();

	/**
	 * Return whether the uploaded file is empty, that is, either no file has
	 * been chosen in the multipart form or the chosen file has no content.
	 * <p>
	 *  返回上传的文件是否为空,也就是说,在多部分表单中没有选择任何文件,或者所选文件没有内容
	 * 
	 */
	boolean isEmpty();

	/**
	 * Return the size of the file in bytes.
	 * <p>
	 *  以字节为单位返回文件的大小
	 * 
	 * 
	 * @return the size of the file, or 0 if empty
	 */
	long getSize();

	/**
	 * Return the contents of the file as an array of bytes.
	 * <p>
	 *  将文件的内容作为字节数组返回
	 * 
	 * 
	 * @return the contents of the file as bytes, or an empty byte array if empty
	 * @throws IOException in case of access errors (if the temporary store fails)
	 */
	byte[] getBytes() throws IOException;

	/**
	 * Return an InputStream to read the contents of the file from.
	 * The user is responsible for closing the stream.
	 * <p>
	 * 返回一个InputStream来读取文件的内容。用户负责关闭流
	 * 
	 * 
	 * @return the contents of the file as stream, or an empty stream if empty
	 * @throws IOException in case of access errors (if the temporary store fails)
	 */
	@Override
	InputStream getInputStream() throws IOException;

	/**
	 * Transfer the received file to the given destination file.
	 * <p>This may either move the file in the filesystem, copy the file in the
	 * filesystem, or save memory-held contents to the destination file.
	 * If the destination file already exists, it will be deleted first.
	 * <p>If the file has been moved in the filesystem, this operation cannot
	 * be invoked again. Therefore, call this method just once to be able to
	 * work with any storage mechanism.
	 * <p><strong>Note:</strong> when using Servlet 3.0 multipart support you
	 * need to configure the location relative to which files will be copied
	 * as explained in {@link javax.servlet.http.Part#write}.
	 * <p>
	 *  将接收到的文件传输到给定的目标文件<p>这可能会移动文件系统中的文件,复制文件系统中的文件或将内存保存的内容保存到目标文件如果目标文件已存在,将被删除第一个<p>如果文件已被移动到文件系统中,则无法再
	 * 次调用此操作。
	 * 因此,只需调用此方法即可使用任何存储机制<p> <strong>注意：</strong>使用Servlet 30多部分支持,您需要配置相对于哪些文件将被复制的位置,如{@link javaxservlethttpPart#write}
	 * 
	 * @param dest the destination file
	 * @throws IOException in case of reading or writing errors
	 * @throws IllegalStateException if the file has already been moved
	 * in the filesystem and is not available anymore for another transfer
	 */
	void transferTo(File dest) throws IOException, IllegalStateException;

}
