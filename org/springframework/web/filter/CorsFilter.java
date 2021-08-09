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

package org.springframework.web.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.Assert;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsProcessor;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.DefaultCorsProcessor;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * {@link javax.servlet.Filter} that handles CORS preflight requests and intercepts
 * CORS simple and actual requests thanks to a {@link CorsProcessor} implementation
 * ({@link DefaultCorsProcessor} by default) in order to add the relevant CORS
 * response headers (like {@code Access-Control-Allow-Origin}) using the provided
 * {@link CorsConfigurationSource} (for example an {@link UrlBasedCorsConfigurationSource}
 * instance.
 *
 * <p>This is an alternative to Spring MVC Java config and XML namespace CORS configuration,
 * useful for applications depending only on spring-web (not on spring-webmvc) or for
 * security constraints requiring CORS checks to be performed at {@link javax.servlet.Filter}
 * level.
 *
 * <p>This filter could be used in conjunction with {@link DelegatingFilterProxy} in order
 * to help with its initialization.
 *
 * <p>
 * {@link javaxservletFilter},由于{@link CorsProcessor}实现({@link DefaultCorsProcessor}默认))处理CORS预检要求并拦截COR
 * S简单实际的请求,以便添加相关的CORS响应头(如{@code Access -Control-Allow-Origin}),使用提供的{@link CorsConfigurationSource}(例
 * 如{@link UrlBasedCorsConfigurationSource}实例。
 * 
 *  <p>这是Spring MVC Java配置和XML命名空间CORS配置的替代方法,仅适用于仅适用于spring-web(不在spring-webmvc上)的应用程序,或对于需要在{@link javaxservletFilter}
 * 执行CORS检查的安全约束水平。
 * 
 * <p>此过滤器可以与{@link DelegatingFilterProxy}结合使用,以帮助其初始化
 * 
 * @author Sebastien Deleuze
 * @since 4.2
 * @see <a href="http://www.w3.org/TR/cors/">CORS W3C recommendation</a>
 */
public class CorsFilter extends OncePerRequestFilter {

	private final CorsConfigurationSource configSource;

	private CorsProcessor processor = new DefaultCorsProcessor();


	/**
	 * Constructor accepting a {@link CorsConfigurationSource} used by the filter
	 * to find the {@link CorsConfiguration} to use for each incoming request.
	 * <p>
	 * 
	 * 
	 * @see UrlBasedCorsConfigurationSource
	 */
	public CorsFilter(CorsConfigurationSource configSource) {
		Assert.notNull(configSource, "CorsConfigurationSource must not be null");
		this.configSource = configSource;
	}


	/**
	 * Configure a custom {@link CorsProcessor} to use to apply the matched
	 * {@link CorsConfiguration} for a request.
	 * <p>By default {@link DefaultCorsProcessor} is used.
	 * <p>
	 *  构造函数接受过滤器使用的{@link CorsConfigurationSource}来查找每个传入请求使用的{@link CorsConfiguration}
	 * 
	 */
	public void setCorsProcessor(CorsProcessor processor) {
		Assert.notNull(processor, "CorsProcessor must not be null");
		this.processor = processor;
	}


	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {

		if (CorsUtils.isCorsRequest(request)) {
			CorsConfiguration corsConfiguration = this.configSource.getCorsConfiguration(request);
			if (corsConfiguration != null) {
				boolean isValid = this.processor.processRequest(corsConfiguration, request, response);
				if (!isValid || CorsUtils.isPreFlightRequest(request)) {
					return;
				}
			}
		}

		filterChain.doFilter(request, response);
	}

}
