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

package org.springframework.web.context.request;

import java.security.Principal;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.springframework.util.StringUtils;

/**
 * {@link WebRequest} adapter for a JSF {@link javax.faces.context.FacesContext}.
 *
 * <p>Requires JSF 2.0 or higher, as of Spring 4.0.
 *
 * <p>
 *  适用于JSF的{@link WebRequest}适配器{@link javaxfacescontextFacesContext}
 * 
 *  <p>自Spring 40起,需要JSF 20或更高版本
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.5.2
 */
public class FacesWebRequest extends FacesRequestAttributes implements NativeWebRequest {

	/**
	 * Create a new FacesWebRequest adapter for the given FacesContext.
	 * <p>
	 *  为给定的FacesContext创建一个新的FacesWebRequest适配器
	 * 
	 * 
	 * @param facesContext the current FacesContext
	 * @see javax.faces.context.FacesContext#getCurrentInstance()
	 */
	public FacesWebRequest(FacesContext facesContext) {
		super(facesContext);
	}


	@Override
	public Object getNativeRequest() {
		return getExternalContext().getRequest();
	}

	@Override
	public Object getNativeResponse() {
		return getExternalContext().getResponse();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getNativeRequest(Class<T> requiredType) {
		if (requiredType != null) {
			Object request = getExternalContext().getRequest();
			if (requiredType.isInstance(request)) {
				return (T) request;
			}
		}
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getNativeResponse(Class<T> requiredType) {
		if (requiredType != null) {
			Object response = getExternalContext().getResponse();
			if (requiredType.isInstance(response)) {
				return (T) response;
			}
		}
		return null;
	}


	@Override
	public String getHeader(String headerName) {
		return getExternalContext().getRequestHeaderMap().get(headerName);
	}

	@Override
	public String[] getHeaderValues(String headerName) {
		return getExternalContext().getRequestHeaderValuesMap().get(headerName);
	}

	@Override
	public Iterator<String> getHeaderNames() {
		return getExternalContext().getRequestHeaderMap().keySet().iterator();
	}

	@Override
	public String getParameter(String paramName) {
		return getExternalContext().getRequestParameterMap().get(paramName);
	}

	@Override
	public Iterator<String> getParameterNames() {
		return getExternalContext().getRequestParameterNames();
	}

	@Override
	public String[] getParameterValues(String paramName) {
		return getExternalContext().getRequestParameterValuesMap().get(paramName);
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return getExternalContext().getRequestParameterValuesMap();
	}

	@Override
	public Locale getLocale() {
		return getFacesContext().getExternalContext().getRequestLocale();
	}

	@Override
	public String getContextPath() {
		return getFacesContext().getExternalContext().getRequestContextPath();
	}

	@Override
	public String getRemoteUser() {
		return getFacesContext().getExternalContext().getRemoteUser();
	}

	@Override
	public Principal getUserPrincipal() {
		return getFacesContext().getExternalContext().getUserPrincipal();
	}

	@Override
	public boolean isUserInRole(String role) {
		return getFacesContext().getExternalContext().isUserInRole(role);
	}

	@Override
	public boolean isSecure() {
		return false;
	}

	@Override
	public boolean checkNotModified(long lastModifiedTimestamp) {
		return false;
	}

	@Override
	public boolean checkNotModified(String eTag) {
		return false;
	}

	/**
	 * Last-modified handling not supported for portlet requests:
	 * As a consequence, this method always returns {@code false}.
	 * <p>
	 * Portlet请求不支持最后修改的处理：因此,此方法始终返回{@code false}
	 * 
	 * @since 4.2
	 */
	@Override
	public boolean checkNotModified(String etag, long lastModifiedTimestamp) {
		return false;
	}

	@Override
	public String getDescription(boolean includeClientInfo) {
		ExternalContext externalContext = getExternalContext();
		StringBuilder sb = new StringBuilder();
		sb.append("context=").append(externalContext.getRequestContextPath());
		if (includeClientInfo) {
			Object session = externalContext.getSession(false);
			if (session != null) {
				sb.append(";session=").append(getSessionId());
			}
			String user = externalContext.getRemoteUser();
			if (StringUtils.hasLength(user)) {
				sb.append(";user=").append(user);
			}
		}
		return sb.toString();
	}


	@Override
	public String toString() {
		return "FacesWebRequest: " + getDescription(true);
	}

}
