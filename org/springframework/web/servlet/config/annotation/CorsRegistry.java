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

package org.springframework.web.servlet.config.annotation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.cors.CorsConfiguration;

/**
 * {@code CorsRegistry} assists with the registration of {@link CorsConfiguration}
 * mapped to a path pattern.
 *
 * <p>
 *  {@code CorsRegistry}协助映射到路径模式的{@link CorsConfiguration}注册
 * 
 * 
 * @author Sebastien Deleuze
 * @since 4.2
 * @see CorsRegistration
 */
public class CorsRegistry {

	private final List<CorsRegistration> registrations = new ArrayList<CorsRegistration>();


	/**
	 * Enable cross origin request handling for the specified path pattern.
	 *
	 * <p>Exact path mapping URIs (such as {@code "/admin"}) are supported as
	 * well as Ant-style path patterns (such as {@code "/admin/**"}).
	 *
	 * <p>By default, all origins, all headers, credentials and {@code GET},
	 * {@code HEAD}, and {@code POST} methods are allowed, and the max age
	 * is set to 30 minutes.
	 * <p>
	 *  为指定的路径模式启用跨源请求处理
	 * 
	 * <p>支持精确路径映射URI(如{@code"/ admin"})以及Ant样式路径模式(例如{@code"/ admin / **"})
	 */
	public CorsRegistration addMapping(String pathPattern) {
		CorsRegistration registration = new CorsRegistration(pathPattern);
		this.registrations.add(registration);
		return registration;
	}

	protected Map<String, CorsConfiguration> getCorsConfigurations() {
		Map<String, CorsConfiguration> configs = new LinkedHashMap<String, CorsConfiguration>(this.registrations.size());
		for (CorsRegistration registration : this.registrations) {
			configs.put(registration.getPathPattern(), registration.getCorsConfiguration());
		}
		return configs;
	}

}
