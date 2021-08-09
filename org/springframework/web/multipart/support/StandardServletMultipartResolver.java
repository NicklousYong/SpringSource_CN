/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2014 the original author or authors.
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

package org.springframework.web.multipart.support;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.apache.commons.logging.LogFactory;

import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;

/**
 * Standard implementation of the {@link MultipartResolver} interface,
 * based on the Servlet 3.0 {@link javax.servlet.http.Part} API.
 * To be added as "multipartResolver" bean to a Spring DispatcherServlet context,
 * without any extra configuration at the bean level (see below).
 *
 * <p><b>Note:</b> In order to use Servlet 3.0 based multipart parsing,
 * you need to mark the affected servlet with a "multipart-config" section in
 * {@code web.xml}, or with a {@link javax.servlet.MultipartConfigElement}
 * in programmatic servlet registration, or (in case of a custom servlet class)
 * possibly with a {@link javax.servlet.annotation.MultipartConfig} annotation
 * on your servlet class. Configuration settings such as maximum sizes or
 * storage locations need to be applied at that servlet registration level;
 * Servlet 3.0 does not allow for them to be set at the MultipartResolver level.
 *
 * <p>
 * 基于Servlet 30 {@link javaxservlethttpPart} API的{@link MultipartResolver}接口的标准实现要作为"multipartResolver"b
 * ean添加到Spring DispatcherServlet上下文中,在bean级别没有任何额外的配置(见下文)。
 * 
 * <p> <b>注意：</b>为了使用基于Servlet 30的多部分解析,您需要在{@code webxml}中使用"multipart-config"部分标记受影响的servlet,或者使用{@link需要在servlet注册级别应用程序化的servlet注册中的javaxservletMultipartConfigElement}
 * ,或者在servlet类上可能使用{@link javaxservletannotationMultipartConfig}注释的配置设置(如最大大小或存储位置); Servlet 30不允许将它们设置
 * 在MultipartResolver级别。
 * 
 * @author Juergen Hoeller
 * @since 3.1
 */
public class StandardServletMultipartResolver implements MultipartResolver {

	private boolean resolveLazily = false;


	/**
	 * Set whether to resolve the multipart request lazily at the time of
	 * file or parameter access.
	 * <p>Default is "false", resolving the multipart elements immediately, throwing
	 * corresponding exceptions at the time of the {@link #resolveMultipart} call.
	 * Switch this to "true" for lazy multipart parsing, throwing parse exceptions
	 * once the application attempts to obtain multipart files or parameters.
	 * <p>
	 * 
	 */
	public void setResolveLazily(boolean resolveLazily) {
		this.resolveLazily = resolveLazily;
	}


	@Override
	public boolean isMultipart(HttpServletRequest request) {
		// Same check as in Commons FileUpload...
		if (!"post".equals(request.getMethod().toLowerCase())) {
			return false;
		}
		String contentType = request.getContentType();
		return (contentType != null && contentType.toLowerCase().startsWith("multipart/"));
	}

	@Override
	public MultipartHttpServletRequest resolveMultipart(HttpServletRequest request) throws MultipartException {
		return new StandardMultipartHttpServletRequest(request, this.resolveLazily);
	}

	@Override
	public void cleanupMultipart(MultipartHttpServletRequest request) {
		// To be on the safe side: explicitly delete the parts,
		// but only actual file parts (for Resin compatibility)
		try {
			for (Part part : request.getParts()) {
				if (request.getFile(part.getName()) != null) {
					part.delete();
				}
			}
		}
		catch (Exception ex) {
			LogFactory.getLog(getClass()).warn("Failed to perform cleanup of multipart items", ex);
		}
	}

}
