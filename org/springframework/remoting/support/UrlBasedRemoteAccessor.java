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

package org.springframework.remoting.support;

import org.springframework.beans.factory.InitializingBean;

/**
 * Abstract base class for classes that access remote services via URLs.
 * Provides a "serviceUrl" bean property, which is considered as required.
 *
 * <p>
 *  通过URL访问远程服务的类的抽象基类提供"serviceUrl"bean属性,这被视为必需的
 * 
 * 
 * @author Juergen Hoeller
 * @since 15.12.2003
 */
public abstract class UrlBasedRemoteAccessor extends RemoteAccessor implements InitializingBean {

	private String serviceUrl;


	/**
	 * Set the URL of this remote accessor's target service.
	 * The URL must be compatible with the rules of the particular remoting provider.
	 * <p>
	 * 设置此远程访问者的目标服务的URL URL必须与特定远程提供程序的规则兼容
	 * 
	 */
	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	/**
	 * Return the URL of this remote accessor's target service.
	 * <p>
	 *  返回此远程访问者的目标服务的URL
	 */
	public String getServiceUrl() {
		return this.serviceUrl;
	}


	@Override
	public void afterPropertiesSet() {
		if (getServiceUrl() == null) {
			throw new IllegalArgumentException("Property 'serviceUrl' is required");
		}
	}

}
