/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2012 the original author or authors.
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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.util.MultiValueMap;

/**
 * This interface defines the multipart request access operations
 * that are exposed for actual multipart requests. It is extended
 * by {@link MultipartHttpServletRequest} and the Portlet
 * {@link org.springframework.web.portlet.multipart.MultipartActionRequest}.
 *
 * <p>
 * 该界面定义了针对实际多部分请求的多部分请求访问操作。
 * 它由{@link MultipartHttpServletRequest}和Portlet {@link orgspringframeworkwebportletmultipartMultipartActionRequest}
 * 扩展,。
 * 该界面定义了针对实际多部分请求的多部分请求访问操作。
 * 
 * 
 * @author Juergen Hoeller
 * @author Arjen Poutsma
 * @since 2.5.2
 */
public interface MultipartRequest {

	/**
	 * Return an {@link java.util.Iterator} of String objects containing the
	 * parameter names of the multipart files contained in this request. These
	 * are the field names of the form (like with normal parameters), not the
	 * original file names.
	 * <p>
	 *  返回包含此请求中包含的多部分文件的参数名称的String对象的{@link javautilIterator}这些是表单的字段名称(如正常参数),而不是原始文件名
	 * 
	 * 
	 * @return the names of the files
	 */
	Iterator<String> getFileNames();

	/**
	 * Return the contents plus description of an uploaded file in this request,
	 * or {@code null} if it does not exist.
	 * <p>
	 *  在此请求中返回内容加上上传文件的描述,否则返回{@code null}(如果不存在)
	 * 
	 * 
	 * @param name a String specifying the parameter name of the multipart file
	 * @return the uploaded content in the form of a {@link MultipartFile} object
	 */
	MultipartFile getFile(String name);

	/**
	 * Return the contents plus description of uploaded files in this request,
	 * or an empty list if it does not exist.
	 * <p>
	 *  在此请求中返回内容加上上传文件的描述,如果不存在则返回空列表
	 * 
	 * 
	 * @param name a String specifying the parameter name of the multipart file
	 * @return the uploaded content in the form of a {@link MultipartFile} list
	 * @since 3.0
	 */
	List<MultipartFile> getFiles(String name);

	/**
	 * Return a {@link java.util.Map} of the multipart files contained in this request.
	 * <p>
	 *  返回此请求中包含的多部分文件的{@link javautilMap}
	 * 
	 * 
	 * @return a map containing the parameter names as keys, and the
	 * {@link MultipartFile} objects as values
	 */
	Map<String, MultipartFile> getFileMap();

	/**
	 * Return a {@link MultiValueMap} of the multipart files contained in this request.
	 * <p>
	 * 返回此请求中包含的多部分文件的{@link MultiValueMap}
	 * 
	 * 
	 * @return a map containing the parameter names as keys, and a list of
	 * {@link MultipartFile} objects as values
	 * @since 3.0
	 */
	MultiValueMap<String, MultipartFile> getMultiFileMap();

	/**
	 * Determine the content type of the specified request part.
	 * <p>
	 *  确定指定请求部分的内容类型
	 * 
	 * @param paramOrFileName the name of the part
	 * @return the associated content type, or {@code null} if not defined
	 * @since 3.1
	 */
	String getMultipartContentType(String paramOrFileName);

}
