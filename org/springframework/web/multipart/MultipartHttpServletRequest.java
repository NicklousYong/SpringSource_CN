/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2011 the original author or authors.
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

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

/**
 * Provides additional methods for dealing with multipart content within a
 * servlet request, allowing to access uploaded files.
 * Implementations also need to override the standard
 * {@link javax.servlet.ServletRequest} methods for parameter access, making
 * multipart parameters available.
 *
 * <p>A concrete implementation is
 * {@link org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest}.
 * As an intermediate step,
 * {@link org.springframework.web.multipart.support.AbstractMultipartHttpServletRequest}
 * can be subclassed.
 *
 * <p>
 * 提供用于处理servlet请求中的多部分内容的其他方法,允许访问上传的文件实现还需要覆盖参数访问的标准{@link javaxservletServletRequest}方法,从而使多部分参数可用
 * 
 *  <p>具体实现是{@link orgspringframeworkwebmultipartsupportDefaultMultipartHttpServletRequest}作为中间步骤,可以将{@link orgspringframeworkwebmultipartsupportAbstractMultipartHttpServletRequest}
 * 进行子类化。
 * 
 * 
 * @author Juergen Hoeller
 * @author Trevor D. Cook
 * @since 29.09.2003
 * @see MultipartResolver
 * @see MultipartFile
 * @see javax.servlet.http.HttpServletRequest#getParameter
 * @see javax.servlet.http.HttpServletRequest#getParameterNames
 * @see javax.servlet.http.HttpServletRequest#getParameterMap
 * @see org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest
 * @see org.springframework.web.multipart.support.AbstractMultipartHttpServletRequest
 */
public interface MultipartHttpServletRequest extends HttpServletRequest, MultipartRequest {

	/**
	 * Return this request's method as a convenient HttpMethod instance.
	 * <p>
	 *  将此请求的方法作为方便的HttpMethod实例返回
	 * 
	 */
	HttpMethod getRequestMethod();

	/**
	 * Return this request's headers as a convenient HttpHeaders instance.
	 * <p>
	 *  将此请求的标题作为方便的HttpHeaders实例返回
	 * 
	 */
	HttpHeaders getRequestHeaders();

	/**
	 * Return the headers associated with the specified part of the multipart request.
	 * <p>If the underlying implementation supports access to headers, then all headers are returned.
	 * Otherwise, the returned headers will include a 'Content-Type' header at the very least.
	 * <p>
	 * 返回与多部分请求的指定部分相关联的头部<p>如果底层实现支持对头部的访问,则返回所有头部否则返回的头部至少包含"Content-Type"头部
	 */
	HttpHeaders getMultipartHeaders(String paramOrFileName);

}
