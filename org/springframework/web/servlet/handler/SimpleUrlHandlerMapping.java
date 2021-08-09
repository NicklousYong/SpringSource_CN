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

package org.springframework.web.servlet.handler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.util.CollectionUtils;

/**
 * Implementation of the {@link org.springframework.web.servlet.HandlerMapping}
 * interface to map from URLs to request handler beans. Supports both mapping to bean
 * instances and mapping to bean names; the latter is required for non-singleton handlers.
 *
 * <p>The "urlMap" property is suitable for populating the handler map with
 * bean references, e.g. via the map element in XML bean definitions.
 *
 * <p>Mappings to bean names can be set via the "mappings" property, in a form
 * accepted by the {@code java.util.Properties} class, like as follows:<br>
 * {@code
 * /welcome.html=ticketController
 * /show.html=ticketController
 * }<br>
 * The syntax is {@code PATH=HANDLER_BEAN_NAME}.
 * If the path doesn't begin with a slash, one is prepended.
 *
 * <p>Supports direct matches (given "/test" -> registered "/test") and "*"
 * pattern matches (given "/test" -> registered "/t*"). Note that the default
 * is to map within the current servlet mapping if applicable; see the
 * {@link #setAlwaysUseFullPath "alwaysUseFullPath"} property. For details on the
 * pattern options, see the {@link org.springframework.util.AntPathMatcher} javadoc.

 * <p>
 * 实现从URL映射到请求处理程序bean的{@link orgspringframeworkwebservletHandlerMapping}接口支持映射到bean实例并映射到bean名称;后者是非单例处
 * 理程序所必需的。
 * 
 *  <p>"urlMap"属性适用于使用bean引用填充处理程序映射,例如,通过XML bean定义中的map元素
 * 
 *  <p>可以通过"mappings"属性,以{@code javautilProperties}类接受的形式设置bean名称,如下所示：<br> {@code / welcomehtml = ticketController / showhtml = ticketController}
 *  < br>语法为{@code PATH = HANDLER_BEAN_NAME}如果路径不以斜杠开头,则前面加一个。
 * 
 * <p>支持直接匹配(给定"/ test" - >注册"/ test")和"*"模式匹配(给定"/ test" - >注册"/ t *")注意默认是在当前的servlet映射(如果适用)请参阅{@link #setAlwaysUseFullPath"alwaysUseFullPath"}
 * 属性有关模式选项的详细信息,请参阅{@link orgspringframeworkutilAntPathMatcher} javadoc。
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see #setMappings
 * @see #setUrlMap
 * @see BeanNameUrlHandlerMapping
 */
public class SimpleUrlHandlerMapping extends AbstractUrlHandlerMapping {

	private final Map<String, Object> urlMap = new LinkedHashMap<String, Object>();


	/**
	 * Map URL paths to handler bean names.
	 * This is the typical way of configuring this HandlerMapping.
	 * <p>Supports direct URL matches and Ant-style pattern matches. For syntax
	 * details, see the {@link org.springframework.util.AntPathMatcher} javadoc.
	 * <p>
	 *  将URL路径映射到处理程序bean名称这是配置此HandlerMapping的典型方式<p>支持直接URL匹配和Ant样式模式匹配有关语法详细信息,请参阅{@link orgspringframeworkutilAntPathMatcher}
	 *  javadoc。
	 * 
	 * 
	 * @param mappings properties with URLs as keys and bean names as values
	 * @see #setUrlMap
	 */
	public void setMappings(Properties mappings) {
		CollectionUtils.mergePropertiesIntoMap(mappings, this.urlMap);
	}

	/**
	 * Set a Map with URL paths as keys and handler beans (or handler bean names)
	 * as values. Convenient for population with bean references.
	 * <p>Supports direct URL matches and Ant-style pattern matches. For syntax
	 * details, see the {@link org.springframework.util.AntPathMatcher} javadoc.
	 * <p>
	 * 将具有URL路径的地图设置为键和处理程序bean(或处理程序bean名称)作为值适用于具有bean引用的群体的便利<p>支持直接URL匹配和Ant样式模式匹配有关语法详细信息,请参阅{@link orgspringframeworkutilAntPathMatcher}
	 *  javadoc。
	 * 
	 * 
	 * @param urlMap map with URLs as keys and beans as values
	 * @see #setMappings
	 */
	public void setUrlMap(Map<String, ?> urlMap) {
		this.urlMap.putAll(urlMap);
	}

	/**
	 * Allow Map access to the URL path mappings, with the option to add or
	 * override specific entries.
	 * <p>Useful for specifying entries directly, for example via "urlMap[myKey]".
	 * This is particularly useful for adding or overriding entries in child
	 * bean definitions.
	 * <p>
	 *  允许地图访问URL路径映射,并添加或覆盖特定条目的选项<p>可用于直接指定条目,例如通过"urlMap [myKey]"这对于添加或覆盖子bean定义中的条目非常有用
	 * 
	 */
	public Map<String, ?> getUrlMap() {
		return this.urlMap;
	}


	/**
	 * Calls the {@link #registerHandlers} method in addition to the
	 * superclass's initialization.
	 * <p>
	 *  调用{@link #registerHandlers}方法除了超类的初始化
	 * 
	 */
	@Override
	public void initApplicationContext() throws BeansException {
		super.initApplicationContext();
		registerHandlers(this.urlMap);
	}

	/**
	 * Register all handlers specified in the URL map for the corresponding paths.
	 * <p>
	 *  注册URL地图中指定的相应路径的所有处理程序
	 * 
	 * @param urlMap Map with URL paths as keys and handler beans or bean names as values
	 * @throws BeansException if a handler couldn't be registered
	 * @throws IllegalStateException if there is a conflicting handler registered
	 */
	protected void registerHandlers(Map<String, Object> urlMap) throws BeansException {
		if (urlMap.isEmpty()) {
			logger.warn("Neither 'urlMap' nor 'mappings' set on SimpleUrlHandlerMapping");
		}
		else {
			for (Map.Entry<String, Object> entry : urlMap.entrySet()) {
				String url = entry.getKey();
				Object handler = entry.getValue();
				// Prepend with slash if not already present.
				if (!url.startsWith("/")) {
					url = "/" + url;
				}
				// Remove whitespace from handler bean name.
				if (handler instanceof String) {
					handler = ((String) handler).trim();
				}
				registerHandler(url, handler);
			}
		}
	}

}
