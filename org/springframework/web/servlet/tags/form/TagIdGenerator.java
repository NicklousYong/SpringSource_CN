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

package org.springframework.web.servlet.tags.form;

import javax.servlet.jsp.PageContext;

/**
 * Utility class for generating '{@code id}' attributes values for JSP tags. Given the
 * name of a tag (the data bound path in most cases) returns a unique ID for that name within
 * the current {@link PageContext}. Each request for an ID for a given name will append an
 * ever increasing counter to the name itself. For instance, given the name '{@code person.name}',
 * the first request will give '{@code person.name1}' and the second will give
 * '{@code person.name2}'. This supports the common use case where a set of radio or check buttons
 * are generated for the same data field, with each button being a distinct tag instance.
 *
 * <p>
 * 为JSP标签生成"{@code id}"属性值的实用程序类给定标记的名称(大多数情况下的数据绑定路径)在当前的{@link PageContext}每个请求中返回该名称的唯一ID给定名称的ID将附加名称
 * 本身的越来越多的计数器例如,给定名称"{@code personname}",第一个请求将给出"{@code personname1}",第二个将给出"{@code personname2}'这支持常见
 * 的使用情况,即为相同的数据字段生成一组无线电或检查按钮,每个按钮都是一个不同的标签实例。
 * 
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 */
abstract class TagIdGenerator {

	/**
	 * The prefix for all {@link PageContext} attributes created by this tag.
	 * <p>
	 *  由此标记创建的所有{@link PageContext}属性的前缀
	 * 
	 */
	private static final String PAGE_CONTEXT_ATTRIBUTE_PREFIX = TagIdGenerator.class.getName() + ".";

	/**
	 * Get the next unique ID (within the given {@link PageContext}) for the supplied name.
	 * <p>
	 *  获取提供的名称的下一个唯一ID(在给定的{@link PageContext}内)
	 */
	public static String nextId(String name, PageContext pageContext) {
		String attributeName = PAGE_CONTEXT_ATTRIBUTE_PREFIX + name;
		Integer currentCount = (Integer) pageContext.getAttribute(attributeName);
		currentCount = (currentCount != null ? currentCount + 1 : 1);
		pageContext.setAttribute(attributeName, currentCount);
		return (name + currentCount);
	}

}
