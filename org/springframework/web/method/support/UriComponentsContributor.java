/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2013 the original author or authors.
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

import java.util.Map;

import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Strategy for contributing to the building of a {@link UriComponents} by
 * looking at a method parameter and an argument value and deciding what
 * part of the target URL should be updated.
 *
 * <p>
 *  通过查看方法参数和参数值来决定构建一个{@link UriComponents}的策略,并决定应该更新目标URL的哪一部分
 * 
 * 
 * @author Oliver Gierke
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public interface UriComponentsContributor {

	/**
	 * Whether this contributor supports the given method parameter.
	 * <p>
	 * 这个贡献者是否支持给定的方法参数
	 * 
	 */
	boolean supportsParameter(MethodParameter parameter);

	/**
	 * Process the given method argument and either update the
	 * {@link UriComponentsBuilder} or add to the map with URI variables to use to
	 * expand the URI after all arguments are processed.
	 * <p>
	 *  处理给定的方法参数,并更新{@link UriComponentsBuilder}或使用URI变量添加到地图,以便在处理所有参数后用于展开URI
	 * 
	 * @param parameter the controller method parameter, never {@literal null}.
	 * @param value the argument value, possibly {@literal null}.
	 * @param builder the builder to update, never {@literal null}.
	 * @param uriVariables a map to add URI variables to, never {@literal null}.
	 * @param conversionService a ConversionService to format values as Strings
	 */
	void contributeMethodArgument(MethodParameter parameter, Object value, UriComponentsBuilder builder,
			Map<String, Object> uriVariables, ConversionService conversionService);

}
