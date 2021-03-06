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

package org.springframework.web.servlet.config.annotation;

import java.util.List;

import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * An implementation of {@link WebMvcConfigurer} with empty methods allowing
 * subclasses to override only the methods they're interested in.
 *
 * <p>
 *  使用空方法实现{@link WebMvcConfigurer},允许子类仅覆盖他们感兴趣的方法
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public abstract class WebMvcConfigurerAdapter implements WebMvcConfigurer {

	/**
	 * {@inheritDoc}
	 * <p>This implementation is empty.
	 * <p>
	 *  {@inheritDoc} <p>此实现为空
	 * 
	 */
	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
	}

	/**
	 * {@inheritDoc}
	 * <p>This implementation is empty.
	 * <p>
	 * {@inheritDoc} <p>此实现为空
	 * 
	 */
	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
	}

	/**
	 * {@inheritDoc}
	 * <p>This implementation is empty.
	 * <p>
	 *  {@inheritDoc} <p>此实现为空
	 * 
	 */
	@Override
	public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
	}

	/**
	 * {@inheritDoc}
	 * <p>This implementation is empty.
	 * <p>
	 *  {@inheritDoc} <p>此实现为空
	 * 
	 */
	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
	}

	/**
	 * {@inheritDoc}
	 * <p>This implementation is empty.
	 * <p>
	 *  {@inheritDoc} <p>此实现为空
	 * 
	 */
	@Override
	public void addFormatters(FormatterRegistry registry) {
	}

	/**
	 * {@inheritDoc}
	 * <p>This implementation is empty.
	 * <p>
	 *  {@inheritDoc} <p>此实现为空
	 * 
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
	}

	/**
	 * {@inheritDoc}
	 * <p>This implementation is empty.
	 * <p>
	 *  {@inheritDoc} <p>此实现为空
	 * 
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
	}

	/**
	 * {@inheritDoc}
	 * <p>This implementation is empty.
	 * <p>
	 *  {@inheritDoc} <p>此实现为空
	 * 
	 */
	@Override
	public void addCorsMappings(CorsRegistry registry) {
	}

	/**
	 * {@inheritDoc}
	 * <p>This implementation is empty.
	 * <p>
	 *  {@inheritDoc} <p>此实现为空
	 * 
	 */
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
	}

	/**
	 * {@inheritDoc}
	 * <p>This implementation is empty.
	 * <p>
	 *  {@inheritDoc} <p>此实现为空
	 * 
	 */
	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
	}

	/**
	 * {@inheritDoc}
	 * <p>This implementation is empty.
	 * <p>
	 *  {@inheritDoc} <p>此实现为空
	 * 
	 */
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
	}

	/**
	 * {@inheritDoc}
	 * <p>This implementation is empty.
	 * <p>
	 *  {@inheritDoc} <p>此实现为空
	 * 
	 */
	@Override
	public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
	}

	/**
	 * {@inheritDoc}
	 * <p>This implementation is empty.
	 * <p>
	 *  {@inheritDoc} <p>此实现为空
	 * 
	 */
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
	}

	/**
	 * {@inheritDoc}
	 * <p>This implementation is empty.
	 * <p>
	 *  {@inheritDoc} <p>此实现为空
	 * 
	 */
	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
	}

	/**
	 * {@inheritDoc}
	 * <p>This implementation is empty.
	 * <p>
	 *  {@inheritDoc} <p>此实现为空
	 * 
	 */
	@Override
	public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
	}

	/**
	 * {@inheritDoc}
	 * <p>This implementation is empty.
	 * <p>
	 *  {@inheritDoc} <p>此实现为空
	 * 
	 */
	@Override
	public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
	}

	/**
	 * {@inheritDoc}
	 * <p>This implementation returns {@code null}.
	 * <p>
	 *  {@inheritDoc} <p>此实现返回{@code null}
	 * 
	 */
	@Override
	public Validator getValidator() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * <p>This implementation returns {@code null}.
	 * <p>
	 * {@inheritDoc} <p>此实现返回{@code null}
	 */
	@Override
	public MessageCodesResolver getMessageCodesResolver() {
		return null;
	}

}
