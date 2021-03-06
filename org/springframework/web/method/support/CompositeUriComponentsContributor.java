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

package org.springframework.web.method.support;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * A {@link UriComponentsContributor} containing a list of other contributors
 * to delegate and also encapsulating a specific {@link ConversionService} to
 * use for formatting method argument values to Strings.
 *
 * <p>
 *  一个{@link UriComponentsContributor},其中包含其他授权人员的列表,并且还封装了一个特定的{@link ConversionService},用于将字符串的格式化方法参数
 * 值。
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public class CompositeUriComponentsContributor implements UriComponentsContributor {

	private final List<Object> contributors = new LinkedList<Object>();

	private final ConversionService conversionService;


	/**
	 * Create an instance from a collection of {@link UriComponentsContributor}s or
	 * {@link HandlerMethodArgumentResolver}s. Since both of these tend to be implemented
	 * by the same class, the most convenient option is to obtain the configured
	 * {@code HandlerMethodArgumentResolvers} in {@code RequestMappingHandlerAdapter}
	 * and provide that to this constructor.
	 * <p>
	 * 从{@link UriComponentsContributor}或{@link HandlerMethodArgumentResolver}的集合中创建一个实例由于这两个都是同一个类实现的,所以最方便
	 * 的选项是在{@code中获取配置的{@code HandlerMethodArgumentResolvers}代码RequestMappingHandlerAdapter}并提供给此构造函数。
	 * 
	 * 
	 * @param contributors a collection of {@link UriComponentsContributor}
	 * or {@link HandlerMethodArgumentResolver}s.
	 */
	public CompositeUriComponentsContributor(UriComponentsContributor... contributors) {
		Collections.addAll(this.contributors, contributors);
		this.conversionService = new DefaultFormattingConversionService();
	}

	/**
	 * Create an instance from a collection of {@link UriComponentsContributor}s or
	 * {@link HandlerMethodArgumentResolver}s. Since both of these tend to be implemented
	 * by the same class, the most convenient option is to obtain the configured
	 * {@code HandlerMethodArgumentResolvers} in {@code RequestMappingHandlerAdapter}
	 * and provide that to this constructor.
	 * <p>
	 *  从{@link UriComponentsContributor}或{@link HandlerMethodArgumentResolver}的集合中创建一个实例由于这两个都是同一个类实现的,所以最方
	 * 便的选项是在{@code中获取配置的{@code HandlerMethodArgumentResolvers}代码RequestMappingHandlerAdapter}并提供给此构造函数。
	 * 
	 * 
	 * @param contributors a collection of {@link UriComponentsContributor}
	 * or {@link HandlerMethodArgumentResolver}s.
	 */
	public CompositeUriComponentsContributor(Collection<?> contributors) {
		this(contributors, null);
	}

	/**
	 * Create an instance from a collection of {@link UriComponentsContributor}s or
	 * {@link HandlerMethodArgumentResolver}s. Since both of these tend to be implemented
	 * by the same class, the most convenient option is to obtain the configured
	 * {@code HandlerMethodArgumentResolvers} in the {@code RequestMappingHandlerAdapter}
	 * and provide that to this constructor.
	 * <p>If the {@link ConversionService} argument is {@code null},
	 * {@link org.springframework.format.support.DefaultFormattingConversionService}
	 * will be used by default.
	 * <p>
	 * 从{@link UriComponentsContributor}或{@link HandlerMethodArgumentResolver}的集合中创建一个实例由于这两个方法都是由同一个类实现的,所以
	 * 最方便的选项是在{{@code HandlerMethodArgumentResolvers} @code RequestMappingHandlerAdapter}并提供给这个构造函数<p>如果{@link ConversionService}
	 * 参数是{@code null},默认情况下将使用{@link orgspringframeworkformatsupportDefaultFormattingConversionService}。
	 * 
	 * 
	 * @param contributors a collection of {@link UriComponentsContributor}
	 * or {@link HandlerMethodArgumentResolver}s.
	 * @param cs a ConversionService to use when method argument values
	 * need to be formatted as Strings before being added to the URI
	 */
	public CompositeUriComponentsContributor(Collection<?> contributors, ConversionService cs) {
		Assert.notNull(contributors, "'uriComponentsContributors' must not be null");
		this.contributors.addAll(contributors);
		this.conversionService = (cs != null ? cs : new DefaultFormattingConversionService());
	}


	public boolean hasContributors() {
		return this.contributors.isEmpty();
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		for (Object c : this.contributors) {
			if (c instanceof UriComponentsContributor) {
				UriComponentsContributor contributor = (UriComponentsContributor) c;
				if (contributor.supportsParameter(parameter)) {
					return true;
				}
			}
			else if (c instanceof HandlerMethodArgumentResolver) {
				if (((HandlerMethodArgumentResolver) c).supportsParameter(parameter)) {
					return false;
				}
			}
		}
		return false;
	}

	@Override
	public void contributeMethodArgument(MethodParameter parameter, Object value,
			UriComponentsBuilder builder, Map<String, Object> uriVariables, ConversionService conversionService) {

		for (Object contributor : this.contributors) {
			if (contributor instanceof UriComponentsContributor) {
				UriComponentsContributor ucc = (UriComponentsContributor) contributor;
				if (ucc.supportsParameter(parameter)) {
					ucc.contributeMethodArgument(parameter, value, builder, uriVariables, conversionService);
					break;
				}
			}
			else if (contributor instanceof HandlerMethodArgumentResolver) {
				if (((HandlerMethodArgumentResolver) contributor).supportsParameter(parameter)) {
					break;
				}
			}
		}
	}

	/**
	 * An overloaded method that uses the ConversionService created at construction.
	 * <p>
	 */
	public void contributeMethodArgument(MethodParameter parameter, Object value, UriComponentsBuilder builder,
			Map<String, Object> uriVariables) {

		this.contributeMethodArgument(parameter, value, builder, uriVariables, this.conversionService);
	}

}
